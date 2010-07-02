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
 * Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import de.escidoc.core.common.business.fedora.resources.DbResourceCache;
import de.escidoc.core.common.business.fedora.resources.interfaces.FilterInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.service.interfaces.ResourceCacheInterface;

/**
 * A service interface to the resource cache.
 * 
 * @author sche
 */
public abstract class ResourceCache implements ResourceCacheInterface {

    /**
     * The underlying cache object.
     */
    protected DbResourceCache cache = null;

    /**
     * Store a resource in the database cache.
     * 
     * @param id
     *            resource id
     * @param restXml
     *            complete resource as REST XML
     * @param soapXml
     *            complete resource as SOAP XML
     * 
     * @throws SystemException
     *             The resource could not be stored.
     */
    public void add(final String id, final String restXml, final String soapXml)
        throws SystemException {
        cache.resourceCreated(id, restXml, soapXml);
    }

    /**
     * Delete all resources of the current type and their properties from the
     * database cache.
     * 
     * @throws SystemException
     *             The resources could not be deleted.
     */
    public void clear() throws SystemException {
        cache.clear();
    }

    /**
     * Check if the resource exists in the database cache.
     * 
     * @param id
     *            resource id
     * 
     * @return true if the resource exists
     */
    public boolean exists(final String id) {
        return cache.exists(id);
    }

    /**
     * Get a list of resource id's depending on the given parameters "user" and
     * "filter".
     * 
     * @param userId
     *            user id
     * @param filter
     *            object containing all filter values
     * 
     * @return list of resource id's
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     */
    public List<String> getIds(final String userId, final FilterInterface filter)
        throws InvalidSearchQueryException, SystemException {
        List<String> result = new LinkedList<String>();
        BufferedReader reader = null;

        try {
            StringWriter writer = new StringWriter();

            cache.getResourceIds(writer, userId, filter);
            reader = new BufferedReader(new StringReader(writer.toString()));

            String line = null;

            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        }
        catch (IOException e) {
            throw new SystemException(e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                }
            }
        }
        return result;
    }

    /**
     * Ask whether or not the resource cache is enabled.
     * 
     * @return true if the resource cache is currently enabled
     */
    public boolean isEnabled() {
        return cache.isEnabled();
    }

    /**
     * Remove a resource from the database cache.
     * 
     * @param id
     *            resource id .
     * @throws SystemException
     *             The resource could not be removed
     */
    public void remove(final String id) throws SystemException {
        cache.resourceDeleted(id);
    }
}
