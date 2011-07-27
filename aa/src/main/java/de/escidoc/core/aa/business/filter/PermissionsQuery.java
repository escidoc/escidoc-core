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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.aa.business.SecurityHelper;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.interfaces.FilterInterface;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;

/**
 * Encapsulate the work which has to be done to get the permission filter queries for Lucene filtering.
 *
 * @author Andr&eacute; Schenk
 */
@Service("filter.PermissionsQuery")
public class PermissionsQuery {

    private static final String HIERARCHICAL_CONTAINERS_PLACEHOLDER = "{5}";

    private static final String HIERARCHICAL_OUS_PLACEHOLDER = "{6}";

    /**
     * Logging goes there.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionsQuery.class);

    @Autowired
    @Qualifier("resource.AccessRights")
    private AccessRights accessRights;

    @Autowired
    @Qualifier("security.SecurityHelper")
    private SecurityHelper securityHelper;

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    /**
     * Private constructor to prevent initialization.
     */
    protected PermissionsQuery() {
    }

    /**
     * Add the AA filters to the given SQL statement.
     *
     * @param resourceType resource type
     * @param statement    SQL statement
     * @param userId       user id
     * @throws WebserverSystemException Thrown if a framework internal error occurs.
     */
    private void addAccessRights(final ResourceType resourceType, final StringBuffer statement, final String userId) {
        final List<String> statements = new LinkedList<String>();
        final Map<String, Map<String, List<RoleGrant>>> userGrants = getUserGrants(userId);
        final Map<String, Map<String, List<RoleGrant>>> userGroupGrants = getUserGroupGrants(userId);
        Set<String> hierarchicalContainers = null;
        Set<String> hierarchicalOUs = null;

        for (final String roleId : accessRights.getRoleIds(resourceType)) {
            if (userGrants.keySet().contains(roleId) || userGroupGrants.keySet().contains(roleId)
                || roleId.equals(AccessRights.getDefaultRole())) {
                if (hierarchicalContainers == null
                    && accessRights.needsHierarchicalPermissions(resourceType, roleId,
                        HIERARCHICAL_CONTAINERS_PLACEHOLDER)) {
                    hierarchicalContainers =
                        getHierarchicalContainers(accessRights.getOptimizedScopeIds(ResourceType.CONTAINER, userGrants,
                            userGroupGrants, roleId));
                }
                if (hierarchicalOUs == null
                    && accessRights.needsHierarchicalPermissions(resourceType, roleId, HIERARCHICAL_OUS_PLACEHOLDER)) {
                    hierarchicalOUs =
                        getHierarchicalOUs(accessRights.getOptimizedScopeIds(ResourceType.OU, userGrants,
                            userGroupGrants, roleId));
                }

                final String rights =
                    accessRights.getAccessRights(resourceType, roleId, userId, retrieveGroupsForUser(userId),
                        userGrants, userGroupGrants, hierarchicalContainers, hierarchicalOUs);

                if (rights != null && rights.length() > 0) {
                    LOGGER.debug("OR access rights for (" + userId + ',' + roleId + "): " + rights);
                    statements.add(rights);
                }
            }
        }
        statement.append(accessRights.appendAccessRights(statements));
    }

    /**
     * Get the part of the query which represents the access restrictions.
     *
     * @param resourceTypes list of resource types which are allowed for this request
     * @param userId        user id
     * @param filter        object containing all the necessary parameters
     * @return sub query representing the access restrictions
     * @throws InvalidSearchQueryException Thrown if the given search query could not be translated into a SQL query.
     * @throws WebserverSystemException    Thrown if a framework internal error occurs.
     */
    public String getFilterQuery(
        final Iterable<ResourceType> resourceTypes, final String userId, final FilterInterface filter) {
        final StringBuffer result = new StringBuffer();

        for (final ResourceType resourceType : resourceTypes) {
            if (result.length() > 0) {
                result.append(" OR ");
            }
            result.append('(');
            // add AA filters
            addAccessRights(resourceType, result, userId);
            LOGGER.debug("AA filters: " + result);

            // all restricting access rights from another user are ANDed
            if (filter.getUserId() != null) {
                final List<String> foreignUserRights = new LinkedList<String>();
                final Map<String, Map<String, List<RoleGrant>>> userGrants = getUserGrants(filter.getUserId());
                final Map<String, Map<String, List<RoleGrant>>> userGroupGrants =
                    getUserGroupGrants(filter.getUserId());
                Set<String> hierarchicalContainers = null;

                if (accessRights.needsHierarchicalPermissions(resourceType, filter.getRoleId(),
                    HIERARCHICAL_CONTAINERS_PLACEHOLDER)) {
                    hierarchicalContainers =
                        getHierarchicalContainers(accessRights.getOptimizedScopeIds(ResourceType.CONTAINER, userGrants,
                            userGroupGrants, filter.getRoleId()));
                }

                Set<String> hierarchicalOUs = null;

                if (accessRights.needsHierarchicalPermissions(resourceType, filter.getRoleId(),
                    HIERARCHICAL_OUS_PLACEHOLDER)) {
                    hierarchicalOUs =
                        getHierarchicalOUs(accessRights.getOptimizedScopeIds(ResourceType.OU, userGrants,
                            userGroupGrants, filter.getRoleId()));
                }

                final Set<String> groupIds = retrieveGroupsForUser(filter.getUserId());
                String rights =
                    accessRights.getAccessRights(resourceType, filter.getRoleId(), filter.getUserId(), groupIds,
                        userGrants, userGroupGrants, hierarchicalContainers, hierarchicalOUs);

                if (rights != null && rights.length() > 0) {
                    foreignUserRights.add(rights);
                }

                if (filter.getRoleId() != null) {
                    // add rules for default role
                    final String containerGrants =
                        accessRights.ensureNotEmpty(accessRights.getSetAsString(hierarchicalContainers));
                    final String ouGrants = accessRights.ensureNotEmpty(accessRights.getSetAsString(hierarchicalOUs));

                    rights =
                        accessRights.getRoleQuery(resourceType, AccessRights.DEFAULT_ROLE, filter.getUserId(),
                            userGrants, userGroupGrants, containerGrants, ouGrants);
                    if (rights != null && rights.length() > 0) {
                        foreignUserRights.add(rights);
                    }
                }

                if (!foreignUserRights.isEmpty()) {
                    rights = accessRights.appendAccessRights(foreignUserRights);
                    LOGGER.debug("AND restricting access rights from " + "another user (1): " + rights);
                    result.append(" AND (");
                    result.append(rights);
                    result.append(')');
                }
            }
            result.append(')');
        }
        return result.toString();
    }

    /**
     * Get a list of all child containers for the given containers.
     *
     * @param containerIds container list
     * @return list of all child containers
     */
    private Set<String> getHierarchicalContainers(final Iterable<String> containerIds) {
        final Set<String> result = new HashSet<String>();

        try {
            for (final String containerId : containerIds) {
                final List<String> childContainers = tripleStoreUtility.getAllChildContainers(containerId);

                result.add(containerId);
                if (childContainers != null) {
                    result.addAll(childContainers);
                }
            }
        }
        catch (final TripleStoreSystemException e) {
            LOGGER.error("getting child containers from database failed", e);
        }
        return result;
    }

    /**
     * Get a list of all child OUs for the given OUs.
     *
     * @param ouIds OU list
     * @return list of all child OUs
     */
    private Set<String> getHierarchicalOUs(final Iterable<String> ouIds) {
        final Set<String> result = new HashSet<String>();

        try {
            for (final String ouId : ouIds) {
                final List<String> childOUs = tripleStoreUtility.getAllChildOUs(ouId);

                result.add(ouId);
                if (childOUs != null) {
                    result.addAll(childOUs);
                }
            }
        }
        catch (final TripleStoreSystemException e) {
            LOGGER.error("getting child OUs from database failed", e);
        }
        return result;
    }

    /**
     * Get all grants directly assigned to the given user.
     *
     * @param userId user id
     * @return all direct grants for the user
     */
    private Map<String, Map<String, List<RoleGrant>>> getUserGrants(final String userId) {

        Map<String, Map<String, List<RoleGrant>>> result = null;
        try {
            result = securityHelper.getUserGrants(userId);
        }
        catch (Exception e) {
            // The caller doesn't expect to get an exception from here if
            // the group doesn't exist.
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on retrieving grants.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on retrieving grants.", e);
            }
        }
        if (result == null) {
            result = new HashMap<String, Map<String, List<RoleGrant>>>();
        }

        // Add Default-Role
        final Map<String, List<RoleGrant>> defaultScope = new HashMap<String, List<RoleGrant>>();
        defaultScope.put("", null);
        result.put(AccessRights.getDefaultRole(), defaultScope);
        return result;
    }

    /**
     * Get all group grants assigned to the given user.
     *
     * @param userId user id
     * @return all group grants for the user
     */
    private Map<String, Map<String, List<RoleGrant>>> getUserGroupGrants(final String userId) {
        final Map<String, Map<String, List<RoleGrant>>> result = new HashMap<String, Map<String, List<RoleGrant>>>();
        if (userId != null && userId.length() > 0) {
            try {
                final Set<String> groupIds = securityHelper.getUserGroups(userId);

                if (groupIds != null) {
                    for (final String groupId : groupIds) {
                        final Map<String, Map<String, List<RoleGrant>>> currentRoleGrantMap =
                            securityHelper.getGroupGrants(groupId);

                        if (currentRoleGrantMap != null) {
                            for (final Entry<String, Map<String, List<RoleGrant>>> entry : currentRoleGrantMap
                                .entrySet()) {
                                if (!result.containsKey(entry.getKey())) {
                                    result.put(entry.getKey(), new HashMap<String, List<RoleGrant>>());
                                }
                                final Map<String, List<RoleGrant>> currentGrantMap = entry.getValue();

                                for (final Entry<String, List<RoleGrant>> currEntry : currentGrantMap.entrySet()) {
                                    result.get(entry.getKey()).put(currEntry.getKey(), currEntry.getValue());
                                }
                            }
                        }
                    }
                }
            }
            catch (final Exception e) {
                LOGGER.error("getting the user group grants from AA failed", e);
            }
        }
        return result;
    }

    /**
     * wrapper for method from UserGroupHandler which returns an empty set in case of an error.
     *
     * @param userId user id
     * @return set of user groups or empty set
     */
    protected Set<String> retrieveGroupsForUser(final String userId) {
        Set<String> result = new HashSet<String>();

        if (userId != null && userId.length() > 0) {
            try {
                result = securityHelper.getUserGroups(userId);
            }
            catch (final Exception e) {
                LOGGER.error("", e);
            }
        }
        return result;
    }
}
