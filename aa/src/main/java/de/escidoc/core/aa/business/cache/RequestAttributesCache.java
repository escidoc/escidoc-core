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

import org.springframework.stereotype.Service;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.PartialCacheKey;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.sun.xacml.EvaluationCtx;

/**
 * Class to cache objects retrieved from the system for the XACML engine.<br>
 *
 * @author Roland Werner (Accenture)
 */
@Service("security.RequestAttributesCache")
public class RequestAttributesCache {

    /**
     * Puts object in cache.
     *
     * @param ctx      the EvaluationContext.
     * @param cacheKey the cacheKey for this object.
     * @param result   the object to cache.
     * @return Object cached Object.
     */
    @Cacheable(cacheName = "attributesCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public Object putAttribute(@PartialCacheKey
    final EvaluationCtx ctx, @PartialCacheKey
    final String cacheKey, final Object result) {
        return result;
    }

    /**
     * Get object from cache. If object is not in cache, return null.
     *
     * @param ctx      the EvaluationContext.
     * @param cacheKey the cacheKey for this object.
     * @return Object cached Object.
     */
    @Cacheable(cacheName = "attributesCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public Object getAttribute(final EvaluationCtx ctx, final String cacheKey) {
        return null;
    }

    /**
     * Remove object from cache.
     *
     * @param ctx      the EvaluationContext.
     * @param cacheKey the cacheKey for this object.
     */
    @TriggersRemove(cacheName = "attributesCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public void clearAttribute(final EvaluationCtx ctx, final String cacheKey) {
    }

}
