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

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.Values;

/**
 * This object contains all user access rights used in the resource cache. These
 * access rights are SQL WHERE clauses which represent the read policies for a
 * specific user role.
 * 
 * @author SCHE
 */
public class AccessRights {
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
    private static final class Rules {
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
        private Rules(final String scopeRules, final String policyRules) {
            this.scopeRules = scopeRules;
            this.policyRules = policyRules;
        }
    }

    /**
     * Mapping from role id to SQL statements.
     */
    public static class RightsMap implements Map<String, Rules> {
        private final HashMap<String, Rules> hashMap =
            new HashMap<String, Rules>();

        public boolean equals(final Object o) {
            return hashMap.equals(o);
        }

        public int hashCode() {
            return hashMap.hashCode();
        }

        @Override
        public int size() {
            return hashMap.size();
        }

        @Override
        public boolean isEmpty() {
            return hashMap.isEmpty();
        }

        @Override
        public Rules get(final Object key) {
            return hashMap.get(key);
        }

        @Override
        public Rules put(final String key, final Rules value) {
            return this.hashMap.put(key, value);
        }

        @Override
        public boolean containsKey(final Object key) {
            return hashMap.containsKey(key);
        }

        @Override
        public Rules remove(final Object key) {
            return hashMap.remove(key);
        }

        @Override
        public void putAll(final Map<? extends String, ? extends Rules> m) {
            this.hashMap.putAll(m);
        }

        @Override
        public void clear() {
            hashMap.clear();
        }

        @Override
        public boolean containsValue(final Object value) {
            return hashMap.containsValue(value);
        }

        @Override
        public Set<String> keySet() {
            return hashMap.keySet();
        }

        @Override
        public Collection<Rules> values() {
            return hashMap.values();
        }

        @Override
        public Set<Entry<String, Rules>> entrySet() {
            return hashMap.entrySet();
        }
    }

    /**
     * Array containing all mappings between role id and SQL WHERE clause. The
     * array index corresponds to the resource type.
     */
    private final RightsMap[] rightsMap =
        new RightsMap[ResourceType.values().length];

    private Values values;

    /**
     * Delete a specific access right.
     * 
     * @param roleId
     *            role id
     */
    public void deleteAccessRight(final String roleId) {
        synchronized (this.rightsMap) {
            for (final RightsMap aRightsMap : this.rightsMap) {
                aRightsMap.remove(roleId);
            }
        }
    }

    /**
     * Delete all access rights.
     */
    public void deleteAccessRights() {
        synchronized (this.rightsMap) {
            for (int index = 0; index < rightsMap.length; index++) {
                this.rightsMap[index] = null;
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

        if (result == null || result.length() == 0) {
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
    public String getAccessRights(
        final ResourceType type, final String roleId, final String userId,
        final Set<String> groupIds,
        final Map<String, Map<String, List<RoleGrant>>> userGrants,
        final Map<String, Map<String, List<RoleGrant>>> userGroupGrants,
        final Set<String> hierarchicalContainers,
        final Set<String> hierarchicalOUs) {
        final StringBuilder accessRights = new StringBuilder();
        final String containerGrants =
            ensureNotEmpty(getSetAsString(hierarchicalContainers));
        final String ouGrants = ensureNotEmpty(getSetAsString(hierarchicalOUs));

        synchronized (this.rightsMap) {
            if (roleId != null && roleId.length() > 0) {
                if (!groupIds.isEmpty() && userGroupGrants != null
                    && userGroupGrants.containsKey(roleId)
                    || userGrants.containsKey(roleId)) {
                    final Rules rights = this.rightsMap[type.ordinal()].get(roleId);

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
                            MessageFormat
                                .format(
                                    rights.policyRules.replace("'", "''"),
                                    values.escape(userId),
                                    values.escape(roleId),
                                    groupSQL,
                                    quotedGroupSQL,
                                    ensureNotEmpty(getGrantsAsString(getOptimizedScopeIds(
                                        type, userGrants, userGroupGrants))),
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
                for (final Entry<String, Rules> role : this.rightsMap[type.ordinal()]
                    .entrySet()) {
                    if (!groupIds.isEmpty()
                        && userGroupGrants.containsKey(roleId)
                        || userGrants.containsKey(role.getKey())) {
                        final String groupSQL = getGroupSql(groupIds);
                        final String quotedGroupSQL =
                            groupSQL.replace("'", "''");

                        if (accessRights.length() > 0) {
                            accessRights.append(" OR ");
                        }
                        accessRights.append('(');

                        final String scopeSql =
                            MessageFormat.format(
                                role.getValue().scopeRules.replace("'", "''"),
                                values.escape(userId),
                                values.escape(role.getKey()),
                                groupSQL,
                                quotedGroupSQL,
                                getGrantsAsString(getScopeIds(userGrants,
                                    userGroupGrants)), containerGrants,
                                ouGrants);
                        final String policySql =
                            MessageFormat.format(
                                role.getValue().policyRules.replace("'", "''"),
                                values.escape(userId),
                                values.escape(role.getKey()),
                                groupSQL,
                                quotedGroupSQL,
                                getGrantsAsString(getOptimizedScopeIds(type,
                                    userGrants, userGroupGrants)),
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
        String result = null;
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
    private static String getGroupSql(final Collection<String> groupIds) {
        final StringBuilder result = new StringBuilder();

        result.append('(');
        if (groupIds != null && !groupIds.isEmpty()) {
            try {
                for (final String groupId : groupIds) {
                    if (result.length() > 1) {
                        result.append(" OR ");
                    }
                    result.append("group_id='");
                    result.append(groupId);
                    result.append('\'');
                }
            } catch (final Exception ignored) {
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
    public Iterable<String> getRoleIds(final ResourceType type) {
        return this.rightsMap[type.ordinal()].keySet();
    }

    /**
     * Put the given grant lists into a space separated string.
     * 
     * @param scopeIds
     *            list of all scopeIds of all grants of the user
     * 
     * @return string containing all given grants separated with space
     */
    private String getGrantsAsString(final Set<String> scopeIds) {
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
    private String getSetAsString(final Iterable<String> set) {
        final StringBuilder result = new StringBuilder();

        if (set != null) {
            for (final String element : set) {
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
     * @param placeHolder
     *            place holder to be searched for in the rule set
     * 
     * @return true if the rule set contains place holders for hierarchical
     *         resources
     */
    public boolean needsHierarchicalPermissions(
        final ResourceType type, final CharSequence roleId,
        final CharSequence placeHolder) {
        boolean result = false;

        synchronized (this.rightsMap) {
            if (type != null && roleId != null && roleId.length() > 0) {
                final Rules rules = this.rightsMap[type.ordinal()].get(roleId);

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
    public void putAccessRight(
        final ResourceType type, String roleId, final String scopeRules,
        final String policyRules) {
        final int resourceType = type.ordinal();
        synchronized (this.rightsMap) {
            if (this.rightsMap[resourceType] == null) {
                this.rightsMap[resourceType] = new RightsMap();
            }
            if (roleId == null) {
                roleId = DEFAULT_ROLE;
            }
            this.rightsMap[resourceType].put(roleId, new Rules(scopeRules,
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
    public Set<String> getScopeIds(
        final Map<String, Map<String, List<RoleGrant>>> userGrants,
        final Map<String, Map<String, List<RoleGrant>>> groupGrants) {
        final Set<String> result = new HashSet<String>();
        if (userGrants != null) {
            for (final Entry<String, Map<String, List<RoleGrant>>> entry : userGrants
                .entrySet()) {
                for (final String scopeId : entry.getValue().keySet()) {
                    if (scopeId.length() != 0) {
                        result.add(scopeId);
                    }
                }
            }
        }
        if (groupGrants != null) {
            for (final Entry<String, Map<String, List<RoleGrant>>> entry : groupGrants
                .entrySet()) {
                for (final String scopeId : entry.getValue().keySet()) {
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
    public Set<String> getOptimizedScopeIds(
        final ResourceType resourceType,
        final Map<String, Map<String, List<RoleGrant>>> userGrants,
        final Map<String, Map<String, List<RoleGrant>>> groupGrants) {
        final Set<String> result = new HashSet<String>();
        if (userGrants != null) {
            for (final Entry<String, Map<String, List<RoleGrant>>> entry : userGrants
                .entrySet()) {
                for (final Entry<String, List<RoleGrant>> subentry : entry
                    .getValue().entrySet()) {
                    if (subentry.getKey().length() != 0) {
                        final List<RoleGrant> grants = subentry.getValue();
                        if (grants != null) {
                            for (final RoleGrant grant : grants) {
                                final String objectHref = grant.getObjectHref();
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
            for (final Entry<String, Map<String, List<RoleGrant>>> entry : groupGrants
                .entrySet()) {
                for (final Entry<String, List<RoleGrant>> subentry : entry
                    .getValue().entrySet()) {
                    if (subentry.getKey().length() != 0) {
                        final List<RoleGrant> grants = subentry.getValue();
                        for (final RoleGrant grant : grants) {
                            final String objectHref = grant.getObjectHref();
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
    public ResourceType getResourceTypeFromHref(final String href) {
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
    public String toString() {
        final StringBuilder result = new StringBuilder();

        if (this.rightsMap != null) {
            for (final ResourceType type : ResourceType.values()) {
                result.append(type);
                result.append(":\n");
                for (final Entry<String, Rules> role : this.rightsMap[type.ordinal()]
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
