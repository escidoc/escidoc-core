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
package de.escidoc.core.aa.filter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import de.escidoc.core.common.business.fedora.resources.ResourceType;

/**
 * This object contains all user access rights used in the resource cache. These
 * access rights are SQL WHERE clauses which represent the read policies for a
 * specific user role.
 * 
 * @author SCHE
 */
public abstract class AccessRights extends JdbcDaoSupport {
    /**
     * The id of the default role for anonymous access.
     */
    protected static final String DEFAULT_ROLE = "escidoc:role-default-user";

    /**
     * SQL query to check if a grant for a user and role exists in the database.
     */
    private static final String USER_GRANT_EXISTS =
        "SELECT id FROM aa.role_grant WHERE user_id = ? AND role_id = ? AND "
            + "(revocation_date IS NULL OR revocation_date>CURRENT_TIMESTAMP)";

    /**
     * SQL query to check if a grant for a role exists in the database.
     */
    private static final String USER_GROUP_GRANT_EXISTS =
        "SELECT group_id FROM aa.role_grant WHERE group_id IS NOT NULL "
            + "AND role_id = ? "
            + "AND (revocation_date IS NULL OR revocation_date>CURRENT_TIMESTAMP)";

    /**
     * SQL query to check if the role exists in the database.
     */
    private static final String ROLE_EXISTS =
        "SELECT id FROM aa.escidoc_role WHERE id = ?";

    /**
     * SQL query to check if the user exists in the database.
     */
    private static final String USER_EXISTS =
        "SELECT id FROM aa.user_account WHERE id = ?";

    /**
     * Array containing all mappings between role id and SQL WHERE clause. The
     * array index corresponds to the resource type.
     */
    protected final Map<String, String>[] rightsMap = new HashMap[ResourceType
        .values().length];

    protected Values values = null;

    /**
     * Delete a specific access right.
     * 
     * @param roleId
     *            role id
     */
    public abstract void deleteAccessRight(final String roleId);

    /**
     * Delete all access rights.
     */
    public abstract void deleteAccessRights();

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
     *            list of all user grants the user belongs to
     * @param userGroupGrants
     *            list of all user group grants the user belongs to
     * @param hierarchicalContainers
     *            list of all containers the user may access
     * 
     * @return SQL WHERE clause that represents the read policies for the given
     *         user role and user.
     */
    public String getAccessRights(
        final ResourceType type, final String roleId, final String userId,
        final Set<String> groupIds, final Set<String> userGrants,
        final Set<String> userGroupGrants,
        final Set<String> hierarchicalContainers) {
        String result = null;
        StringBuffer accessRights = new StringBuffer();

        readAccessRights();
        if ((userExists(userId)) || (userId == null) || (userId.length() == 0)) {
            synchronized (rightsMap) {
                if ((roleId != null) && (roleId.length() > 0)) {
                    if (roleExists(roleId)) {
                        if (((groupIds.size() > 0) && userGroupGrantExists(
                            roleId, groupIds))
                            || (userGrantExists(userId, roleId))) {
                            String right =
                                (String) rightsMap[type.ordinal()].get(roleId);

                            if (right != null) {
                                String groupSQL = getGroupSql(groupIds);
                                String quotedGroupSQL =
                                    groupSQL.replace("'", "''");

                                accessRights.append(new MessageFormat(right
                                    .replace("'", "''")).format(new Object[] {
                                    values.escape(userId),
                                    values.escape(roleId),
                                    groupSQL,
                                    quotedGroupSQL,
                                    getGrantsAsString(userGrants,
                                        userGroupGrants),
                                    getSetAsString(hierarchicalContainers) }));
                            }
                        }
                    }
                    else {
                        // unknown role id
                        accessRights.append("FALSE");
                    }
                }
                else {
                    // concatenate all rules with "OR"
                    for (Map.Entry<String, String> role : rightsMap[type
                        .ordinal()].entrySet()) {
                        if (((groupIds.size() > 0) && userGroupGrantExists(
                            roleId, groupIds))
                            || (userGrantExists(userId, role.getKey()))) {
                            String groupSQL = getGroupSql(groupIds);

                            if (accessRights.length() > 0) {
                                accessRights.append(" OR ");
                            }
                            accessRights.append('(');
                            accessRights.append(new MessageFormat(role
                                .getValue().replace("'", "''"))
                                .format(new Object[] {
                                    values.escape(userId),
                                    values.escape(role.getKey()),
                                    groupSQL,
                                    getGrantsAsString(userGrants,
                                        userGroupGrants),
                                    getSetAsString(hierarchicalContainers) }));
                            accessRights.append(')');
                        }
                    }
                }
            }
        }
        else {
            // unknown user id
            accessRights.append("FALSE");
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
    private String getGroupSql(final Set<String> groupIds) {
        StringBuffer result = new StringBuffer();

        result.append('(');
        if ((groupIds != null) && (groupIds.size() > 0)) {
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
    public Collection<String> getRoleIds(final ResourceType type) {
        readAccessRights();
        return rightsMap[type.ordinal()].keySet();
    }

    /**
     * Put the given grant lists into a space separated string.
     * 
     * @param userGrants
     *            list of all user grants the user belongs to
     * @param userGroupGrants
     *            list of all user group grants the user belongs to
     * 
     * @return string containing all given grants separated with space
     */
    private String getGrantsAsString(
        final Set<String> userGrants, final Set<String> userGroupGrants) {
        StringBuffer result = new StringBuffer(getSetAsString(userGrants));
        String userGroupGrantString = getSetAsString(userGroupGrants);

        if (userGroupGrantString.length() > 0) {
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(userGroupGrantString);
        }
        return result.toString();
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
        StringBuffer result = new StringBuffer();

        for (String element : set) {
            if (result.length() > 0) {
                result.append(' ');
            }
            result.append(values.escape(element));
        }
        return result.toString();
    }

    /**
     * Get an access right from outside (AA) and store it internally.
     * 
     * @param type
     *            resource type
     * @param roleId
     *            role id
     * @param sqlStatement
     *            SQL statement for the given combination of resource type and
     *            role
     */
    public abstract void putAccessRight(
        final ResourceType type, final String roleId, final String sqlStatement);

    /**
     * Read all access rights and store them in a map internally.
     */
    protected abstract void readAccessRights();

    /**
     * Check if the role with the given role id exists in AA.
     * 
     * @param roleId
     *            role id
     * 
     * @return true if the role exists
     */
    private boolean roleExists(final String roleId) {
        boolean result = false;

        if (roleId != null) {
            result =
                (Boolean) getJdbcTemplate().query(ROLE_EXISTS,
                    new Object[] { roleId }, new ResultSetExtractor() {
                        public Object extractData(final ResultSet rs)
                            throws SQLException {
                            return Boolean.valueOf(rs.next());
                        }
                    });
        }
        return result;
    }

    /**
     * Return a string representation of the rights map.
     * 
     * @return string representation of the rights map
     */
    public String toString() {
        StringBuffer result = new StringBuffer();

        if (rightsMap != null) {
            for (ResourceType type : ResourceType.values()) {
                result.append(type);
                result.append(":\n");
                for (Map.Entry<String, String> role : rightsMap[type.ordinal()]
                    .entrySet()) {
                    result.append("  ");
                    result.append(role.getKey());
                    result.append('=');
                    result.append(role.getValue());
                    result.append('\n');
                }
            }
        }
        return result.toString();
    }

    /**
     * Check if the user with the given user id exists in AA.
     * 
     * @param userId
     *            user id
     * 
     * @return true if the user exists
     */
    private boolean userExists(final String userId) {
        boolean result = false;

        if (userId != null) {
            result =
                (Boolean) getJdbcTemplate().query(USER_EXISTS,
                    new Object[] { userId }, new ResultSetExtractor() {
                        public Object extractData(final ResultSet rs)
                            throws SQLException {
                            return Boolean.valueOf(rs.next());
                        }
                    });
        }
        return result;
    }

    /**
     * Check if a grant for the given combination of userId, roleId exists.
     * 
     * @param userId
     *            user id
     * @param roleId
     *            role id
     * 
     * @return true, if a grant exists
     */
    private boolean userGrantExists(final String userId, final String roleId) {
        boolean result = false;

        if ((userId != null) && (roleId != null)) {
            if (roleId.equals(DEFAULT_ROLE)) {
                result = true;
            }
            else {
                result =
                    (Boolean) getJdbcTemplate().query(USER_GRANT_EXISTS,
                        new Object[] { userId, roleId },
                        new ResultSetExtractor() {
                            public Object extractData(final ResultSet rs)
                                throws SQLException {
                                return Boolean.valueOf(rs.next());
                            }
                        });
            }
        }
        return result;
    }

    /**
     * Check if a grant for the given roleId exists.
     * 
     * @param roleId
     *            role id
     * @param groupIds
     *            list of group ids which the current user is member of
     * 
     * @return true, if a grant exists
     */
    private boolean userGroupGrantExists(
        final String roleId, final Set<String> groupIds) {
        boolean result = false;

        if (roleId != null) {
            if (roleId.equals(DEFAULT_ROLE)) {
                result = true;
            }
            else {
                result =
                    (Boolean) getJdbcTemplate().query(USER_GROUP_GRANT_EXISTS,
                        new Object[] { roleId }, new ResultSetExtractor() {
                            public Object extractData(final ResultSet rs)
                                throws SQLException {
                                boolean result = false;

                                while (rs.next()) {
                                    if (groupIds.contains(rs.getString(1))) {
                                        result = true;
                                        break;
                                    }
                                }
                                return result;
                            }
                        });
            }
        }
        return result;
    }
}
