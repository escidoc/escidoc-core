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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.security.cache;

import de.escidoc.core.aa.security.aop.SecurityInterceptor;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.security.persistence.MethodMappingList;
import de.escidoc.core.common.util.security.persistence.RequestMappingDaoInterface;
import de.escidoc.core.common.util.string.StringUtility;

import java.util.Map;
import java.util.TreeMap;

/**
 * Cache used in the {@link SecurityInterceptor} to avoid accesses to the
 * database.<br>
 * 
 * @author TTE
 * @spring.bean id="eSciDoc.core.common.SecurityInterceptorCache"
 * @common
 */
public class SecurityInterceptorCache {

    /**
     * The logger.
     */
    private static final AppLogger log =
        new AppLogger(SecurityInterceptorCache.class.getName());

    /**
     * Cache for method mappings.
     * 
     * @common
     */
    private final Map<String, MethodMappingList> mappingsCache =
        new TreeMap<String, MethodMappingList>();

    /**
     * The data access object to access request mappings.
     */
    private RequestMappingDaoInterface requestMappingDao;

    /**
     * The default constructor.
     * 
     * @common
     */
    public SecurityInterceptorCache() {

        clear();
    }

    /**
     * Clears the cache.
     * 
     * @common
     */
    public final void clear() {

        mappingsCache.clear();
    }

    /**
     * Gets the method mapping for the provided class name and method name. <br>
     * If the mappings are not stored within the cache they are fetched from the
     * AA component.
     * 
     * @param className
     *            The name of the class to get the method mappings for.
     * @param methodName
     *            The name of the method to get the mappings for.
     * @return Returns the method mappings.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @common
     */
    public final synchronized MethodMappingList getMethodMappings(
            final String className, final String methodName)
        throws WebserverSystemException {

        final String key =
            StringUtility
                .concatenateWithColon(className, methodName).toString();
        MethodMappingList methodMappings = mappingsCache.get(key);

        if (methodMappings == null) {
            try {
                methodMappings = retrieveMethodMappings(className, methodName);
            }
            catch (Exception e) {
                throw new WebserverSystemException(
                    "Exception during method mappings retrieval. ", e);
            }

            if (methodMappings == null
                || (methodMappings.sizeBefore() == 0 && methodMappings
                    .sizeAfter() == 0)) {
                final String errorMsg =
                        StringUtility.format(
                                "No mapping found for key", key);
                log.error(errorMsg);
                throw new WebserverSystemException(errorMsg);
            }
            mappingsCache.put(key, methodMappings);
        }
        return methodMappings;
    }

    /**
     * Retrieves the method mapping for the provided class name and method name.
     * 
     * @param className
     *            The class name to retrieve the mapping for.
     * @param methodName
     *            The method name to retrieve the mapping for.
     * @return List of method mappings, containing both kinds of mappings, for
     *         before-mapping and for after-mapping
     * 
     * @throws MissingMethodParameterException
     *             Thrown in case of a missing method parameter
     * @throws SystemException
     *             Thrown in case of an internal error.
     * 
     * @common
     */
    public final MethodMappingList retrieveMethodMappings(
            final String className, final String methodName)
        throws MissingMethodParameterException, SystemException {

        return new MethodMappingList(this.requestMappingDao
            .retrieveMethodMappings(className, methodName));
    }

    /**
     * Injects the request mapping data access object.
     * 
     * @param requestMappingDao
     *            The {@link RequestMappingDaoInterface} implementation to
     *            inject.
     * @spring.property ref="persistence.HibernateRequestMappingDao"
     * @common
     */
    public void setRequestMappingDao(
        final RequestMappingDaoInterface requestMappingDao) {

        this.requestMappingDao = requestMappingDao;
    }
}
