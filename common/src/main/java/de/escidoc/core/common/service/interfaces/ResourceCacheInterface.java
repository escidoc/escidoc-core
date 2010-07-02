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
package de.escidoc.core.common.service.interfaces;

import java.util.List;

import de.escidoc.core.common.business.fedora.resources.interfaces.FilterInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Interface of a resource cache.
 * 
 * @author sche
 */
public interface ResourceCacheInterface {
    /**
     * Store a resource in the database cache.
     *
     * @param id resource id
     * @param restXml complete resource as REST XML
     * @param soapXml complete resource as SOAP XML
     *
     * @throws SystemException The resource could not be stored.
     */
    void add(final String id, final String restXml, final String soapXml)
        throws SystemException;

    /**
     * Delete all resources of the current type and their properties from the
     * database cache.
     *
     * @throws SystemException The resources could not be deleted.
     */
    void clear() throws SystemException;

    /**
     * Check if the resource exists in the database cache.
     *
     * @param id resource id
     *
     * @return true if the resource exists
     */
    boolean exists(final String id);

    /**
     * Get a list of resource id's depending on the given parameters "user" and
     * "filter".
     *
     * @param userId user id
     * @param filter object containing all filter values
     *
     * @return list of resource id's
     * @throws InvalidSearchQueryException thrown if the given search query could
     *                                     not be translated into a SQL query
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     */
    List<String> getIds(final String userId, final FilterInterface filter)
        throws InvalidSearchQueryException, SystemException;

    /**
     * Ask whether or not the resource cache is enabled.
     * 
     * @return true if the resource cache is currently enabled
     */
    boolean isEnabled();

    /**
     * Remove a resource from the database cache.
     *
     * @param id resource id
     *.
     * @throws SystemException The resource could not be removed
     */
    void remove(final String id) throws SystemException;
}
