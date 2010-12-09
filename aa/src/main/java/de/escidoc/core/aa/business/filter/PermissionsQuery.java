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
package de.escidoc.core.aa.business.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.escidoc.core.aa.business.cache.PoliciesCacheProxy;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.interfaces.FilterInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;

/**
 * Encapsulate the work which has to be done to get the permission filter
 * queries for Lucene filtering.
 * 
 * @spring.bean id="filter.PermissionsQuery"
 * @author Andr&eacute; Schenk
 */
public class PermissionsQuery {
    private static final String HIERARCHICAL_CONTAINERS_PLACEHOLDER = "{5}";

    private static final String HIERARCHICAL_OUS_PLACEHOLDER = "{6}";

    /**
     * Logging goes there.
     */
    private static final AppLogger LOG = new AppLogger(
        PermissionsQuery.class.getName());

    private AccessRights accessRights = null;

    private PoliciesCacheProxy policiesCacheProxy = null;

    private TripleStoreUtility tripleStoreUtility = null;

    /**
     * Create a new resource cache object.
     * 
     * @throws IOException
     *             Thrown if reading the configuration failed.
     */
    public PermissionsQuery() throws IOException {
    }

    /**
     * Add the AA filters to the given SQL statement.
     * 
     * @param resourceType
     *            resource type
     * @param statement
     *            SQL statement
     * @param userId
     *            user id
     * @param groupIds
     *            list of all group id's the user belongs to
     * 
     * @throws WebserverSystemException
     *             Thrown if a framework internal error occurs.
     */
    private void addAccessRights(
        final ResourceType resourceType, final StringBuffer statement,
        final String userId, final Set<String> groupIds)
        throws WebserverSystemException {
        List<String> statements = new LinkedList<String>();
        Set<String> userGrants = getUserGrants(resourceType, userId, false);
        Set<String> userGroupGrants =
            getUserGroupGrants(resourceType, userId, false);
        Set<String> optimizedUserGrants =
            getUserGrants(resourceType, userId, true);
        Set<String> optimizedUserGroupGrants =
            getUserGroupGrants(resourceType, userId, true);
        Set<String> hierarchicalContainers = null;
        Set<String> hierarchicalOUs = null;

        for (String roleId : accessRights.getRoleIds(resourceType)) {
            if ((hierarchicalContainers == null)
                && accessRights.needsHierarchicalPermissions(resourceType,
                    roleId, userId, groupIds,
                    HIERARCHICAL_CONTAINERS_PLACEHOLDER)) {
                hierarchicalContainers =
                    getHierarchicalContainers(userGrants, userGroupGrants);
            }
            if ((hierarchicalOUs == null)
                && accessRights.needsHierarchicalPermissions(resourceType,
                    roleId, userId, groupIds, HIERARCHICAL_OUS_PLACEHOLDER)) {
                hierarchicalOUs =
                    getHierarchicalOUs(userGrants, userGroupGrants);
            }

            final String rights =
                accessRights.getAccessRights(resourceType, roleId, userId,
                    groupIds, userGrants, userGroupGrants, optimizedUserGrants,
                    optimizedUserGroupGrants, hierarchicalContainers,
                    hierarchicalOUs);

            if ((rights != null) && (rights.length() > 0)) {
                LOG.info("OR access rights for (" + userId + "," + roleId
                    + "): " + rights);
                statements.add(rights);
            }
        }

        // all matching access rights for the login user are ORed
        statement.append('(');
        for (int index = 0; index < statements.size(); index++) {
            if (index > 0) {
                statement.append(" OR ");
            }
            statement.append('(');
            statement.append(statements.get(index));
            statement.append(')');
        }
        statement.append(')');
    }

    /**
     * Get the part of the query which represents the access restrictions.
     * 
     * @param resourceTypes
     *            list of resource types which are allowed for this request
     * @param userId
     *            user id
     * @param filter
     *            object containing all the necessary parameters
     * 
     * @return sub query representing the access restrictions
     * @throws InvalidSearchQueryException
     *             Thrown if the given search query could not be translated into
     *             a SQL query.
     * @throws WebserverSystemException
     *             Thrown if a framework internal error occurs.
     */
    public String getFilterQuery(
        final Set<ResourceType> resourceTypes, final String userId,
        final FilterInterface filter) throws InvalidSearchQueryException,
        WebserverSystemException {
        StringBuffer result = new StringBuffer();

        for (ResourceType resourceType : resourceTypes) {
            if (result.length() > 0) {
                result.append(" OR ");
            }
            result.append('(');
            // add AA filters
            addAccessRights(resourceType, result, userId,
                retrieveGroupsForUser(userId));
            LOG.info("AA filters: " + result);

            // all restricting access rights from another user are ANDed
            if (filter.getUserId() != null) {
                Set<String> groupIds =
                    retrieveGroupsForUser(filter.getUserId());
                Set<String> userGrants =
                    getUserGrants(resourceType, filter.getUserId(), false);
                Set<String> userGroupGrants =
                    getUserGroupGrants(resourceType, filter.getUserId(), false);
                Set<String> optimizedUserGrants =
                    getUserGrants(resourceType, filter.getUserId(), true);
                Set<String> optimizedUserGroupGrants =
                    getUserGroupGrants(resourceType, filter.getUserId(), true);
                Set<String> hierarchicalContainers = null;
                Set<String> hierarchicalOUs = null;

                if (accessRights.needsHierarchicalPermissions(resourceType,
                    filter.getRoleId(), filter.getUserId(), groupIds,
                    HIERARCHICAL_CONTAINERS_PLACEHOLDER)) {
                    hierarchicalContainers =
                        getHierarchicalContainers(userGrants, userGroupGrants);
                }
                if (accessRights.needsHierarchicalPermissions(resourceType,
                    filter.getRoleId(), filter.getUserId(), groupIds,
                    HIERARCHICAL_OUS_PLACEHOLDER)) {
                    hierarchicalOUs =
                        getHierarchicalOUs(userGrants, userGroupGrants);
                }

                String rights =
                    accessRights.getAccessRights(resourceType,
                        filter.getRoleId(), filter.getUserId(), groupIds,
                        userGrants, userGroupGrants, optimizedUserGrants,
                        optimizedUserGroupGrants, hierarchicalContainers,
                        hierarchicalOUs);

                if ((rights != null) && (rights.length() > 0)) {
                    LOG.info("AND restricting access rights from "
                        + "another user (1): " + rights);
                    result.append(" AND ");
                    result.append(rights);
                }
            }
            result.append(')');
        }
        return result.toString();
    }

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
    private Set<String> getHierarchicalContainers(
        final Set<String> userGrants, final Set<String> userGroupGrants) {
        Set<String> result = new HashSet<String>();

        try {
            for (String grant : userGrants) {
                List<String> childContainers =
                    tripleStoreUtility.getAllChildContainers(grant);

                result.add(grant);
                if (childContainers != null) {
                    result.addAll(childContainers);
                }
            }
            for (String grant : userGroupGrants) {
                List<String> childContainers =
                    tripleStoreUtility.getAllChildContainers(grant);

                result.add(grant);
                if (childContainers != null) {
                    result.addAll(childContainers);
                }
            }
        }
        catch (TripleStoreSystemException e) {
            LOG.error("getting child containers from database failed", e);
        }
        return result;
    }

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
    private Set<String> getHierarchicalOUs(
        final Set<String> userGrants, final Set<String> userGroupGrants) {
        Set<String> result = new HashSet<String>();

        try {
            for (String grant : userGrants) {
                List<String> childOUs =
                    tripleStoreUtility.getAllChildOUs(grant);

                result.add(grant);
                if (childOUs != null) {
                    result.addAll(childOUs);
                }
            }
            for (String grant : userGroupGrants) {
                List<String> childOUs =
                    tripleStoreUtility.getAllChildOUs(grant);

                result.add(grant);
                if (childOUs != null) {
                    result.addAll(childOUs);
                }
            }
        }
        catch (TripleStoreSystemException e) {
            LOG.error("getting child OUs from database failed", e);
        }
        return result;
    }

    /**
     * Get the resource type from the given HREF.
     * 
     * @param href
     *            HREF to an eSciDoc resource
     * 
     * @return resource type for that HREF
     */
    private ResourceType getResourceTypeFromHref(final String href) {
        ResourceType result = null;

        if (href != null) {
            if (href.startsWith(Constants.CONTAINER_URL_BASE)) {
                result = ResourceType.CONTAINER;
            }
            else if (href.startsWith(Constants.CONTEXT_URL_BASE)) {
                result = ResourceType.CONTEXT;
            }
            else if (href.startsWith(Constants.ITEM_URL_BASE)) {
                result = ResourceType.ITEM;
            }
            else if (href.startsWith(Constants.ORGANIZATIONAL_UNIT_URL_BASE)) {
                result = ResourceType.OU;
            }
        }
        return result;
    }

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
    private Set<String> getUserGrants(
        final ResourceType resourceType, final String userId,
        final boolean optimize) {
        Set<String> result = new HashSet<String>();

        if ((userId != null) && (userId.length() > 0)) {
            try {
                final Map<String, Map<String, List<RoleGrant>>> currentRoleGrantMap =
                    policiesCacheProxy.getUserGrants(userId);

                if (currentRoleGrantMap != null) {
                    for (String role : currentRoleGrantMap.keySet()) {
                        final Map<String, List<RoleGrant>> currentGrantMap =
                            currentRoleGrantMap.get(role);

                        for (String objectId : currentGrantMap.keySet()) {
                            final List<RoleGrant> currentGrants =
                                currentGrantMap.get(objectId);

                            for (RoleGrant grant : currentGrants) {
                                final String objectHref = grant.getObjectHref();

                                if (!optimize || (objectHref == null)) {
                                    result.add(objectId);
                                    break;
                                }
                                else {
                                    final ResourceType grantType =
                                        getResourceTypeFromHref(objectHref);

                                    if (grantType == resourceType) {
                                        result.add(objectId);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                LOG.error("getting the user grants from AA failed", e);
            }
        }
        return result;
    }

    /**
     * Get all group grants assigned to the given user.
     * 
     * @param resourceType
     *            resource type
     * @param userId
     *            user id
     * @param optimize
     *            ignore all grants which are not granted to the same resource
     *            type as the given resource type
     * 
     * @return all group grants for the user
     */
    private Set<String> getUserGroupGrants(
        final ResourceType resourceType, final String userId,
        final boolean optimize) {
        Set<String> result = new HashSet<String>();

        if ((userId != null) && (userId.length() > 0)) {
            try {
                Set<String> groupIds = policiesCacheProxy.getUserGroups(userId);

                if (groupIds != null) {
                    for (String groupId : groupIds) {
                        final Map<String, Map<String, List<RoleGrant>>> currentRoleGrantMap =
                            policiesCacheProxy.getGroupGrants(groupId);

                        if (currentRoleGrantMap != null) {
                            for (String role : currentRoleGrantMap.keySet()) {
                                final Map<String, List<RoleGrant>> currentGrantMap =
                                    currentRoleGrantMap.get(role);

                                for (String objectId : currentGrantMap.keySet()) {
                                    final List<RoleGrant> currentGrants =
                                        currentGrantMap.get(objectId);

                                    for (RoleGrant grant : currentGrants) {
                                        final String objectHref =
                                            grant.getObjectHref();

                                        if (!optimize || (objectHref == null)) {
                                            result.add(objectId);
                                            break;
                                        }
                                        else {
                                            final ResourceType grantType =
                                                getResourceTypeFromHref(objectHref);

                                            if (grantType == resourceType) {
                                                result.add(objectId);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                LOG.error("getting the user group grants from AA failed", e);
            }
        }
        return result;
    }

    /**
     * wrapper for method from UserGroupHandler which returns an empty set in
     * case of an error.
     * 
     * @param userId
     *            user id
     * @return set of user groups or empty set
     */
    protected Set<String> retrieveGroupsForUser(final String userId) {
        Set<String> result = new HashSet<String>();

        if ((userId != null) && (userId.length() > 0)) {
            try {
                result = policiesCacheProxy.getUserGroups(userId);
            }
            catch (Exception e) {
                LOG.error("", e);
            }
        }
        return result;
    }

    /**
     * Injects the AccessRights object.
     * 
     * @spring.property ref="resource.DbAccessRights"
     * @param accessRights
     *            AccessRights from Spring
     */
    public void setAccessRights(final AccessRights accessRights) {
        this.accessRights = accessRights;
    }

    /**
     * Injects the policies cache proxy.
     * 
     * @spring.property ref="resource.PoliciesCacheProxy"
     * @param policiesCacheProxy
     *            the {@link PoliciesCacheProxy} to inject.
     */
    public void setPoliciesCacheProxy(
        final PoliciesCacheProxy policiesCacheProxy) {
        this.policiesCacheProxy = policiesCacheProxy;
    }

    /**
     * Injects the TripleStore utility.
     * 
     * @spring.property ref="business.TripleStoreUtility"
     * @param tripleStoreUtility
     *            TripleStoreUtility from Spring
     */
    public void setTripleStoreUtility(
        final TripleStoreUtility tripleStoreUtility) {
        this.tripleStoreUtility = tripleStoreUtility;
    }
}
