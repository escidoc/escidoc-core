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
package de.escidoc.core.common.business.fedora.resources.interfaces;

import java.io.Writer;
import java.util.List;
import java.util.Set;

import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.listener.ResourceListener;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Interface of a resource cache.
 * 
 * @author sche
 */
public interface ResourceCacheInterface extends ResourceListener {
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
    void add(final String id, final String restXml, final String soapXml)
        throws SystemException;

    /**
     * Delete all resources of the current type and their properties from the
     * database cache.
     * 
     * @throws SystemException
     *             The resources could not be deleted.
     */
    void clear() throws SystemException;

    /**
     * Check if the resource exists in the database cache.
     * 
     * @param id
     *            resource id
     * 
     * @return true if the resource exists
     */
    boolean exists(final String id);

    /**
     * Get the list of all child containers for all containers the user is
     * granted to.
     * 
     * @param userGrants
     *            user grants of the user
     * @param userGroupGrants
     *            user group grants of the user
     * 
     * @return list of all child containers
     */
    Set<String> getHierarchicalContainers(
        final Set<String> userGrants, final Set<String> userGroupGrants);

    /**
     * Get the list of all child OUs for all OUs the user is granted to.
     * 
     * @param userGrants
     *            user grants of the user
     * @param userGroupGrants
     *            user group grants of the user
     * 
     * @return list of all child OUs
     */
    Set<String> getHierarchicalOUs(
        final Set<String> userGrants, final Set<String> userGroupGrants);

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
    List<String> getIds(final String userId, final FilterInterface filter)
        throws InvalidSearchQueryException, SystemException;

    /**
     * Get the number of records for that query.
     * 
     * @param userId
     *            user id
     * @param filter
     *            object containing all the necessary parameters
     * 
     * @return number of resources for that query
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     */
    long getNumberOfRecords(final String userId, final FilterInterface filter)
        throws InvalidSearchQueryException, SystemException;

    /**
     * Get all property names that are currently stored in the database for the
     * current resource type.
     * 
     * @return all property names for the current resource type
     */
    Set<String> getPropertyNames();

    /**
     * Get a list of resource ids and write it to the given writer.
     * 
     * @param output
     *            writer to which the resource id list will be written
     * @param userId
     *            user id
     * @param filter
     *            object containing all the necessary parameters
     * 
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     */
    void getResourceIds(
        final Writer output, final String userId, final FilterInterface filter)
        throws InvalidSearchQueryException, SystemException;

    /**
     * Get a list of resources and write it to the given writer.
     * 
     * @param output
     *            writer to which the resource list will be written
     * @param userId
     *            user id
     * @param filter
     *            object containing all the necessary parameters
     * @param format
     *            output format (may by null for the old behavior)
     * 
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     */
    void getResourceList(
        final Writer output, final String userId, final FilterInterface filter,
        final String format) throws InvalidSearchQueryException,
        SystemException;

    /**
     * Get all grants directly assigned to the given user for the given resource
     * type.
     * 
     * @param resourceType
     *            resource type
     * @param userId
     *            user id
     * @param optimize
     *            ignore all grants which are not granted to the same resource
     *            type as the given resource type
     * 
     * @return all direct grants for the user
     */
    Set<String> getUserGrants(
        final ResourceType resourceType, final String userId,
        final boolean optimize);

    /**
     * Get all group grants assigned to the given user.
     * 
     * @param userId
     *            user id
     * @param optimize
     *            ignore all grants which are not granted to the same resource
     *            type as the given resource type
     * 
     * @return all group grants for the user
     */
    Set<String> getUserGroupGrants(final String userId, final boolean optimize);

    /**
     * Ask whether or not the resource cache is enabled.
     * 
     * @return true if the resource cache is currently enabled
     */
    boolean isEnabled();

    /**
     * Remove a resource from the database cache.
     * 
     * @param id
     *            resource id .
     * @throws SystemException
     *             The resource could not be removed
     */
    void remove(final String id) throws SystemException;
}
