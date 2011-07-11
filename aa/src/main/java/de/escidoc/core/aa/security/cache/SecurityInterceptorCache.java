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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.security.persistence.MethodMappingList;
import de.escidoc.core.common.util.security.persistence.RequestMappingDaoInterface;
import de.escidoc.core.common.util.string.StringUtility;

/**
 * Cache used in the {@link SecurityInterceptor} to avoid accesses to the database.<br>
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.common.SecurityInterceptorCache")
public class SecurityInterceptorCache {

    /**
     * The data access object to access request mappings.
     */
    @Autowired
    @Qualifier("persistence.HibernateRequestMappingDao")
    private RequestMappingDaoInterface requestMappingDao;

    /**
     * Gets the method mapping for the provided class name and method name. <br> If the mappings are not stored within
     * the cache they are fetched from the AA component.
     *
     * @param className  The name of the class to get the method mappings for.
     * @param methodName The name of the method to get the mappings for.
     * @return Returns the method mappings.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    @Cacheable(cacheName = "mappingsCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public MethodMappingList getMethodMappings(final String className, final String methodName)
        throws WebserverSystemException {
        final MethodMappingList methodMappings;
        try {
            methodMappings = retrieveMethodMappings(className, methodName);
        }
        catch (final Exception e) {
            throw new WebserverSystemException("Exception during method mappings retrieval. ", e);
        }

        if (methodMappings == null || methodMappings.sizeBefore() == 0 && methodMappings.sizeAfter() == 0) {
            final String errorMsg =
                StringUtility.format("No mapping found for class ", className, " and method ", methodName);
            throw new WebserverSystemException(errorMsg);
        }
        return methodMappings;
    }

    /**
     * Retrieves the method mapping for the provided class name and method name.
     *
     * @param className  The class name to retrieve the mapping for.
     * @param methodName The method name to retrieve the mapping for.
     * @return List of method mappings, containing both kinds of mappings, for before-mapping and for after-mapping
     * @throws MissingMethodParameterException
     *                         Thrown in case of a missing method parameter
     * @throws SystemException Thrown in case of an internal error.
     */
    public MethodMappingList retrieveMethodMappings(final String className, final String methodName) {
        return new MethodMappingList(this.requestMappingDao.retrieveMethodMappings(className, methodName));
    }

    /**
     * Injects the request mapping data access object.
     *
     * @param requestMappingDao The {@link RequestMappingDaoInterface} implementation to inject.
     */
    public void setRequestMappingDao(final RequestMappingDaoInterface requestMappingDao) {
        this.requestMappingDao = requestMappingDao;
    }
}
