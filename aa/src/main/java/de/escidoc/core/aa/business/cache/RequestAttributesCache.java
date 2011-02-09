/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.business.cache;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;

import com.sun.xacml.EvaluationCtx;

import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.string.StringUtility;

/**
 * Class to cache objects retrieved from the system for the XACML engine.<br>
 * The objects are store in a {@link Map} that is synchronized by
 * {@link Collections}.synchronizedMap({@link Map}).
 * 
 * @author Roland Werner (Accenture)
 * @aa
 * 
 */
public final class RequestAttributesCache {

    /**
     * The logger.
     */
    private static final AppLogger LOG =
        new AppLogger(RequestAttributesCache.class.getName());

    /**
     * Fall back value if reading property
     * {@link <code>EscidocConfiguration.AA_CACHE_USERS_SIZE</code>} fails.
     * 
     * @aa
     */
    private static final int USERS_CACHE_SIZE_FALL_BACK = 50;

    /**
     * Fall back value if reading property
     * {@link <code>EscidocConfiguration.AA_CACHE_ATTRIBUTES_SIZE</code>} fails.
     * 
     * @aa
     */
    private static final int INTERNAL_CACHE_SIZE_FALL_BACK = 50;

    /**
     * The cache is implemented as a synchronized LRUMap (least-recently-used
     * map), so it can only grow to a certain size.
     * 
     * @aa
     */
    private static Map<EvaluationCtx, Map<Object, Object>> attributesCache;

    /**
     * This cache size should be set to the number of expected concurrent users.
     * It is fetched from the properties. If this fails, a fall back value is
     * used.
     */
    private static int usersCacheSize = USERS_CACHE_SIZE_FALL_BACK;

    /**
     * This cache size should be set to the number of system objects that should
     * be cached for a request at one point of time. It is fetched from the
     * properties. If this fails, a fall back value is used.
     */
    private static int internalCacheSize;

    static {
        initCaches();
    }

    /**
     * Private constructor to prevent class from being instantiated.
     * 
     * @aa
     */
    private RequestAttributesCache() {
    }

    /**
     * Initializes the caches.<br/>The cache sizes are fetched from the eSciDoc
     * Configuration. If this fails, the default values are used as fallback an
     * an error is logged.
     */
    private static void initCaches() {

        try {
            usersCacheSize =
                Integer.parseInt(EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_AA_CACHE_USERS_SIZE));
        }
        catch (Exception e) {
            usersCacheSize = USERS_CACHE_SIZE_FALL_BACK;
        }
        try {
            internalCacheSize =
                Integer
                    .parseInt(EscidocConfiguration
                        .getInstance()
                        .get(
                            EscidocConfiguration.ESCIDOC_CORE_AA_CACHE_ATTRIBUTES_SIZE));
        }
        catch (Exception e) {
            internalCacheSize = INTERNAL_CACHE_SIZE_FALL_BACK;
        }

        createAttributeCache();
    }

    /**
     * Creates the attribute cache.<br/>The cache is implemented as a
     * synchronized LRUMap (least-recently-used map), so it can only grow to a
     * certain size. The size is taken from the static field
     * <code>usersCacheSize</code> that has to be initialized before calling
     * this method (this is not checked).
     */
    @SuppressWarnings("unchecked")
    private static void createAttributeCache() {

        attributesCache =
            Collections.synchronizedMap(new LRUMap(usersCacheSize));
    }

    /**
     * Creates the internal map holding the system objects (attributes) that
     * shall be cached for a request.<br/>
     * 
     * @return Returns a synchronized LRU map. The map size is taken from the
     *         static field <code>internalCacheSize</code> that has to be
     *         initialized before calling this method (this is not checked).
     */
    @SuppressWarnings("unchecked")
    private static Map<Object, Object> createInternalMap() {

        return Collections.synchronizedMap(new LRUMap(internalCacheSize));
    }

    /**
     * Stores the provided object using the provided key in the internal cache
     * for this EvaluationCtx.
     * 
     * Realised as a outer LRUMap that uses context as key and which has an
     * inner LRUMap as value. The inner LRUMap has key as key and object as
     * value. Both LRUMaps are synchronized.
     * 
     * @param context
     *            The context to use as key for the outer HashMap.
     * @param key
     *            The key to use as key for the inner HashMap.
     * @param object
     *            The value for the inner HashMap.
     * @aa
     */
    public static void put(
        final EvaluationCtx context, final Object key, final Object object) {

        try {
            if (key == null || context == null) {
                return;
            }
            Map<Object, Object> internalMap = attributesCache.get(context);
            if (internalMap == null) {
                internalMap = createInternalMap();
                attributesCache.put(context, internalMap);
            }
            internalMap.put(key, object);
        }
        catch (RuntimeException e) {
            LOG.error(StringUtility.format(
                "Runtime exception during put.", context, key, object), e);
            createAttributeCache();
        }
    }

    /**
     * Gets the object for the provided key, given that we are still in the same
     * EvaluationCtx.
     * 
     * Realisation see method put.
     * 
     * @param context
     *            The context to use as key for the outer HashMap.
     * @param key
     *            The key to use as key for the inner HashMap.
     * @return The value of the inner HashMap.
     * @aa
     */
    public static Object get(final EvaluationCtx context, final Object key) {

        try {
            if (key == null || context == null) {
                return null;
            }
            Map<Object, Object> internalMap = attributesCache.get(context);
            if (internalMap == null) {
                return null;
            }
            return internalMap.get(key);
        }
        catch (RuntimeException e) {
            LOG.error(StringUtility.format(
                "Runtime exception during get.", context, key), e);
            createAttributeCache();
            return null;
        }
    }

}
