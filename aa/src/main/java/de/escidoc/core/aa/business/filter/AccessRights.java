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

import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.Values;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import javax.sql.DataSource;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This object contains all user access rights used in the resource cache. These
 * access rights are SQL WHERE clauses which represent the read policies for a
 * specific user role.
 * 
 * @spring.bean id="business.AccessRights" scope="singleton"
 * 
 * @author SCHE
 */
public class AccessRights extends JdbcDaoSupport {
    /**
     * The id of the default role for anonymous access.
     */
    private static final String DEFAULT_ROLE = "escidoc:role-default-user";

    /**
     * Resource id which will never exist in the repository.
     */
    private static final String INVALID_ID = "escidoc:-1";

    /**
     * Container for the scope rules and the policy rules of a role.
     */
    private static class Rules {
        public final String scopeRules;

        public final String policyRules;

        /**
         * Constructor.
         * 
         * @param scopeRules
         *            scope rules
         * @param policyRules
         *            policy rules
         */
        public Rules(final String scopeRules, final String policyRules) {
            this.scopeRules = scopeRules;
            this.policyRules = policyRules;
        }
    }

    /**
     * Mapping from role id to SQL statements.
     */
    public class RightsMap extends HashMap<String, Rules> {
        private static final long serialVersionUID = 7311398691300996752L;
    }

    /**
     * Array containing all mappings between role id and SQL WHERE clause. The
     * array index corresponds to the resource type.
     */
    private final RightsMap[] rightsMap =
        new RightsMap[ResourceType.values().length];

    private Values values = null;

    /**
     * Delete a specific access right.
     * 
     * @param roleId
     *            role id
     */
    public final void deleteAccessRight(final String roleId) {
        synchronized (rightsMap) {
            for (RightsMap aRightsMap : rightsMap) {
                aRightsMap.remove(roleId);
            }
        }
    }

    /**
     * Delete all access rights.
     */
    public final void deleteAccessRights() {
        synchronized (rightsMap) {
            for (int index = 0; index < rightsMap.length; index++) {
                rightsMap[index] = null;
            }
        }
    }

    /**
     * Ensure the given string is not empty by adding a dummy eSciDoc ID to it.
     * 
     * @param s
     *            string to be checked
     * 
     * @return non empty string
     */
    private String ensureNotEmpty(final String s) {
        String result = s;

        if ((result == null) || (result.length() == 0)) {
            result = values.escape(INVALID_ID);
        }
        return result;
    }

    /**
     * Get the SQL WHERE clause which matches the given role id. The SQL string
     * is already filled with the given user id if there is a place holder
     * inside the SQL string.
     * 
     * @param type
     *            resource type
     * @param roleId
     *            role id
     * @param userId
     *            user id
     * @param groupIds
     *            list of all user groups the user belongs to
     * @param userGrants
     *            grants directly assigned to a user
     * @param userGroupGrants
     *            group grants assigned to a user
     * @param hierarchicalContainers
     *            list of all child containers for all containers the user is
     *            granted to
     * @param hierarchicalOUs
     *            list of all child OUs for all OUs the user is granted to
     * 
     * @return SQL WHERE clause that represents the read policies for the given
     *         user role and user.
     */
    public final String getAccessRights(
            final ResourceType type, final String roleId, final String userId,
            final Set<String> groupIds,
            Map<String, Map<String, List<RoleGrant>>> userGrants,
            Map<String, Map<String, List<RoleGrant>>> userGroupGrants,
            final Set<String> hierarchicalContainers,
            final Set<String> hierarchicalOUs) {
        String result = null;
        final StringBuilder accessRights = new StringBuilder();
        final String containerGrants =
            ensureNotEmpty(getSetAsString(hierarchicalContainers));
        final String ouGrants = ensureNotEmpty(getSetAsString(hierarchicalOUs));

        synchronized (rightsMap) {
            if ((roleId != null) && (roleId.length() > 0)) {
                if (((!groupIds.isEmpty()) && userGroupGrants != null
                    && userGroupGrants.containsKey(roleId))
                    || (userGrants.containsKey(roleId))) {
                    Rules rights =
                        rightsMap[type.ordinal()].get(roleId);

                    if (rights != null) {
                        final String groupSQL = getGroupSql(groupIds);
                        final String quotedGroupSQL =
                            groupSQL.replace("'", "''");
                        final String scopeSql =
                            MessageFormat.format(
                                rights.scopeRules.replace("'", "''"),
                                    values.escape(userId),
                                    values.escape(roleId),
                                    groupSQL,
                                    quotedGroupSQL,
                                    ensureNotEmpty(getGrantsAsString(getScopeIds(
                                        userGrants, userGroupGrants))),
                                    containerGrants, ouGrants);
                        final String policySql =
                            MessageFormat.format(
                                rights.policyRules.replace("'", "''"),
                                    values.escape(userId),
                                    values.escape(roleId),
                                    groupSQL,
                                    quotedGroupSQL,
                                    ensureNotEmpty(getGrantsAsString(getOptimizedScopeIds(
                                        type,
                                        userGrants,
                                        userGroupGrants))),
                                    containerGrants, ouGrants);

                        if (scopeSql.length() > 0) {
                            accessRights.append(values.getAndCondition(
                                scopeSql, policySql));
                        }
                        else if (policySql.length() > 0) {
                            accessRights.append(policySql);
                        }
                    }
                }
            }
            else {
                // concatenate all rules with "OR"
                for (Map.Entry<String, Rules> role : rightsMap[type
                    .ordinal()].entrySet()) {
                    if (((!groupIds.isEmpty()) && userGroupGrants.containsKey(
                        roleId))
                        || (userGrants.containsKey(role.getKey()))) {
                        final String groupSQL = getGroupSql(groupIds);
                        final String quotedGroupSQL =
                            groupSQL.replace("'", "''");

                        if (accessRights.length() > 0) {
                            accessRights.append(" OR ");
                        }
                        accessRights.append('(');

                        final String scopeSql =
                            MessageFormat.format(
                                role.getValue().scopeRules.replace("'",
                                    "''"),
                                    values.escape(userId),
                                    values.escape(role.getKey()),
                                    groupSQL,
                                    quotedGroupSQL,
                                    getGrantsAsString(getScopeIds(userGrants,
                                        userGroupGrants)), containerGrants,
                                    ouGrants);
                        final String policySql =
                            MessageFormat.format(
                                role.getValue().policyRules.replace("'",
                                    "''"),
                                    values.escape(userId),
                                    values.escape(role.getKey()),
                                    groupSQL,
                                    quotedGroupSQL,
                                    getGrantsAsString(getOptimizedScopeIds(
                                        type,
                                        userGrants,
                                        userGroupGrants)),
                                    containerGrants, ouGrants);

                        if (scopeSql.length() > 0) {
                            accessRights.append(values.getAndCondition(
                                scopeSql, policySql));
                        }
                        else if (policySql.length() > 0) {
                            accessRights.append(policySql);
                        }
                        accessRights.append(')');
                    }
                }
            }
        }
        if (accessRights.length() > 0) {
            result = accessRights.toString();
        }
        return result;
    }

    /**
     * Get the SQL snippet which lists all group ids a user is member of.
     * 
     * @param groupIds
     *            list of all user groups the user belongs to
     * 
     * @return SQL snippet with all group ids
     */
    private static String getGroupSql(final Set<String> groupIds) {
        StringBuilder result = new StringBuilder();

        result.append('(');
        if ((groupIds != null) && (!groupIds.isEmpty())) {
            try {
                for (String groupId : groupIds) {
                    if (result.length() > 1) {
                        result.append(" OR ");
                    }
                    result.append("group_id='");
                    result.append(groupId);
                    result.append('\'');
                }
            }
            catch (Exception e) {
                result.append("FALSE");
            }
        }
        else {
            result.append("FALSE");
        }
        result.append(')');
        return result.toString();
    }

    /**
     * Get a list of all role ids.
     * 
     * @param type
     *            resource type
     * 
     * @return list of all role ids
     */
    public final Collection<String> getRoleIds(final ResourceType type) {
        return rightsMap[type.ordinal()].keySet();
    }

    /**
     * Put the given grant lists into a space separated string.
     * 
     * @param scoepIds
     *            list of all scopeIds of all grants of the user
     * 
     * @return string containing all given grants separated with space
     */
    private String getGrantsAsString(
        final Set<String> scopeIds) {
        return getSetAsString(scopeIds);
    }

    /**
     * Put the given list into a space separated string.
     * 
     * @param set
     *            list of strings
     * 
     * @return string containing all given strings separated with space
     */
    private String getSetAsString(final Set<String> set) {
        StringBuilder result = new StringBuilder();

        if (set != null) {
            for (String element : set) {
                if (result.length() > 0) {
                    result.append(' ');
                }
                result.append(values.escape(element));
            }
        }
        return result.toString();
    }

    /**
     * Check if the rule set for the given combination of resource type and role
     * id contains place holders for hierarchical containers or organizational
     * units.
     * 
     * This method should be called before generating the hierarchical list of
     * resources because this will be an expensive operation.
     * 
     * @param type
     *            resource type
     * @param roleId
     *            role id
     * @param userId
     *            user id
     * @param groupIds
     *            list of all user groups the user belongs to
     * @param placeHolder
     *            place holder to be searched for in the rule set
     * 
     * @return true if the rule set contains place holders for hierarchical
     *         resources
     */
    public final boolean needsHierarchicalPermissions(
            final ResourceType type, final String roleId, final String placeHolder) {
        boolean result = false;

        synchronized (rightsMap) {
            if ((type != null)
                && (roleId != null)
                && (roleId.length() > 0)) {
                final Rules rules = rightsMap[type.ordinal()].get(roleId);

                if (rules != null) {
                    result =
                        rules.policyRules.contains(placeHolder)
                            || rules.scopeRules.contains(placeHolder);
                }
            }
        }
        return result;
    }

    /**
     * Store the given access right in the database table list.filter.
     * 
     * @param type
     *            resource type
     * @param roleId
     *            role id
     * @param scopeRules
     *            SQL statement representing the scope rules for the given
     *            combination of resource type and role
     * @param policyRules
     *            SQL statement representing the policy rules for the given
     *            combination of resource type and role
     */
    public final void putAccessRight(
            final ResourceType type, String roleId, final String scopeRules,
            final String policyRules) {
        final int resourceType = type.ordinal();
        synchronized (rightsMap) {
            if (rightsMap[resourceType] == null) {
                rightsMap[resourceType] = new RightsMap();
            }
            if (roleId == null) {
                roleId = DEFAULT_ROLE;
            }
            rightsMap[resourceType].put(roleId, new Rules(scopeRules,
                policyRules));
        }
    }

    /**
     * Get all scopeIds of all Grants.
     * 
     * @param userGrants
     *            user grants
     * @param groupGrants
     *            group grants
     * 
     * @return set of ids of all scopes
     */
    static final Set<String> getScopeIds(
            final Map<String, Map<String, List<RoleGrant>>> userGrants,
            final Map<String, Map<String, List<RoleGrant>>> groupGrants) {
        Set<String> result = new HashSet<String>();
        if (userGrants != null) {
            for (Entry<String, Map<String, List<RoleGrant>>> entry 
                                            : userGrants.entrySet()) {
                for (String scopeId : entry.getValue().keySet()) {
                    if (scopeId.length() != 0) {
                        result.add(scopeId);
                    }
                }
            }
        }
        if (groupGrants != null) {
            for (Entry<String, Map<String, List<RoleGrant>>> entry 
                                        : groupGrants.entrySet()) {
                for (String scopeId : entry.getValue().keySet()) {
                    if (scopeId.length() != 0) {
                        result.add(scopeId);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Get all scopeIds of all Grants.
     * 
     * @param resourceType
     *            type of resource
     * @param userGrants
     *            user grants
     * @param groupGrants
     *            group grants
     * 
     * @return set of ids of all scopes
     */
    public final Set<String> getOptimizedScopeIds(final ResourceType resourceType,
                                                  final Map<String, Map<String, List<RoleGrant>>> userGrants,
                                                  final Map<String, Map<String, List<RoleGrant>>> groupGrants) {
        Set<String> result = new HashSet<String>();
        if (userGrants != null) {
            for (Entry<String, Map<String, List<RoleGrant>>> entry 
                                            : userGrants.entrySet()) {
                for (Entry<String, List<RoleGrant>> subentry 
                                : entry.getValue().entrySet()) {
                    if (subentry.getKey().length() != 0) {
                        List<RoleGrant> grants = subentry.getValue();
                        if (grants != null) {
                            for (RoleGrant grant : grants) {
                                final String objectHref =
                                    grant.getObjectHref();
                                final ResourceType grantType =
                                    getResourceTypeFromHref(objectHref);

                                if (grantType == resourceType) {
                                    result.add(subentry.getKey());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (groupGrants != null) {
            for (Entry<String, Map<String, List<RoleGrant>>> entry 
                                            : groupGrants.entrySet()) {
                for (Entry<String, List<RoleGrant>> subentry 
                                : entry.getValue().entrySet()) {
                    if (subentry.getKey().length() != 0) {
                        List<RoleGrant> grants = subentry.getValue();
                        for (RoleGrant grant : grants) {
                            final String objectHref =
                                grant.getObjectHref();
                            final ResourceType grantType =
                                getResourceTypeFromHref(objectHref);

                            if (grantType == resourceType) {
                                result.add(subentry.getKey());
                                break;
                            }
                        }
                    }
                }
            }
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
    static final ResourceType getResourceTypeFromHref(final String href) {
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
     * Injects the data source.
     * 
     * @spring.property ref="escidoc-core.DataSource"
     * @param myDataSource
     *            data source from Spring
     */
    public void setMyDataSource(final DataSource myDataSource) {
        super.setDataSource(myDataSource);
    }

    /**
     * Injects the filter values object.
     * 
     * @spring.property ref="filter.Values"
     * @param values
     *            filter values object from Spring
     */
    public void setValues(final Values values) {
        this.values = values;
    }

    /**
     * Return a string representation of the rights map.
     * 
     * @return string representation of the rights map
     */
    public final String toString() {
        StringBuilder result = new StringBuilder();

        if (rightsMap != null) {
            for (ResourceType type : ResourceType.values()) {
                result.append(type);
                result.append(":\n");
                for (Map.Entry<String, Rules> role : rightsMap[type.ordinal()]
                    .entrySet()) {
                    result.append("  ");
                    result.append(role.getKey());
                    result.append('=');
                    result.append(role.getValue().scopeRules);
                    result.append(',');
                    result.append(role.getValue().policyRules);
                    result.append('\n');
                }
            }
        }
        return result.toString();
    }

    /**
     * Get id of default-role.
     * 
     * @return String id of default role
     */
    public static String getDefaultRole() {
        return DEFAULT_ROLE;
    }

}
