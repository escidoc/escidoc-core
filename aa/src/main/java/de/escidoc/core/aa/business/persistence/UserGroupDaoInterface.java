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
package de.escidoc.core.aa.business.persistence;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.util.list.ListSorting;

import java.util.List;
import java.util.Map;

/**
 * Interface of an data access object to access user group data.
 *
 * @author André Schenk
 */
public interface UserGroupDaoInterface {

    /**
     * Delete the user group member.
     *
     * @param userGroupMember user group object to delete
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void delete(final UserGroupMember userGroupMember) throws SqlDatabaseSystemException;

    /**
     * Delete the user group.
     *
     * @param userGroup user group object to delete
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void delete(final UserGroup userGroup) throws SqlDatabaseSystemException;

    /**
     * Retrieves currently valid grant of the provided user group for the specified role and (optional) id.
     *
     * @param userGroup user group owning the grants that shall be retrieved
     * @param role      role that is granted to the user group
     * @param objId     id of the object for that the role is granted to the user group
     * @return Returns the found {@code Grant} object or {@code null}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    RoleGrant retrieveCurrentGrant(final UserGroup userGroup, final EscidocRole role, final String objId)
        throws SqlDatabaseSystemException;

    /**
     * Retrieves current grants for a List of groupIds. Returns current Grants as HashMap with key: userGroupId valus:
     * List of RoleGrants
     *
     * @param groupIds ids of the userGroups
     * @return Returns the found Grants in a HashMap.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    Map<String, List<RoleGrant>> retrieveCurrentGrants(final List<String> groupIds) throws SqlDatabaseSystemException;

    /**
     * Retrieves a grant.
     *
     * @param grantId The id of the grant that shall be retrieved.
     * @return Returns the found {@code Grant} object or {@code null}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    RoleGrant retrieveGrant(final String grantId) throws SqlDatabaseSystemException;

    /**
     * Retrieves the grants of a user group.<br> The grants will be sorted by related role and related object.
     *
     * @param groupId The id of the user group whose grants shall be retrieved.
     * @return Returns sorted List of {@code Grant} objects.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    List<RoleGrant> retrieveGrants(final String groupId) throws SqlDatabaseSystemException;

    /**
     * Retrieves a user group.
     *
     * @param groupId The id of the user group that shall be retrieved.
     * @return Returns the found {@code UserGroup} object or {@code null}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    UserGroup retrieveUserGroup(final String groupId) throws SqlDatabaseSystemException;

    /**
     * Retrieves {@link UserGroup} objects using the provided values for filtering.
     *
     * @param criteria   The {@link Map} containing the filter criteria. This object is kept as provided by this
     *                   method.
     * @param offset     The index of the first result to be returned.
     * @param maxResults The maximal number of results to be returned.
     * @param orderBy    The predicate that shall be used for ordering.
     * @param sorting    The kind of ordering, i.e. ascending or descending.
     * @return Returns {@code List} of {@link UserGroup} objects selected by the provided parameters. If no
     *         parameter is provided, all user account objects are returned.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    List<UserGroup> retrieveUserGroups(
        final Map<String, Object> criteria, final int offset, final int maxResults, final String orderBy,
        final ListSorting sorting) throws SqlDatabaseSystemException;

    /**
     * Retrieves {@link UserGroup} objects using the provided values for filtering.
     *
     * @param criteria   The {@link String} containing the filter criteria as CQL query.
     * @param offset     The index of the first result to be returned.
     * @param maxResults The maximal number of results to be returned.
     * @return Returns {@code List} of {@link UserGroup} objects selected by the provided parameters. If no
     *         parameter is provided, all user account objects are returned.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SqlDatabaseSystemException  Thrown in case of an internal database access error.
     */
    List<UserGroup> retrieveUserGroups(final String criteria, final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException;

    /**
     * Retrieves {@link UserGroupMember} objects using the provided values for filtering.
     *
     * @param criteria The {@link Map} containing the filter criteria. This object is kept as provided by this method.
     * @return Returns {@code List} of {@link UserGroupMember} objects selected by the provided parameters. If no
     *         parameter is provided, all user group member objects are returned.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    List<UserGroupMember> retrieveUserGroupMembers(final Map<String, Object> criteria)
        throws SqlDatabaseSystemException;

    /**
     * Save the provided grant.
     *
     * @param grant {@code Grant} object to save
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void save(final RoleGrant grant) throws SqlDatabaseSystemException;

    /**
     * Save the provided user group data.
     *
     * @param userGroup user group data object to save
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void save(final UserGroup userGroup) throws SqlDatabaseSystemException;

    /**
     * Save the provided user group member data.
     *
     * @param userGroupMember user group member data object to save
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void save(final UserGroupMember userGroupMember) throws SqlDatabaseSystemException;

    /**
     * Update the provided grant.
     *
     * @param grant {@code Grant} object to save
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void update(final RoleGrant grant) throws SqlDatabaseSystemException;

    /**
     * Update the provided user group data.
     *
     * @param userGroup user group data object to save
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void update(final UserGroup userGroup) throws SqlDatabaseSystemException;

    UserGroup findUsergroupByLabel(final String label) throws SqlDatabaseSystemException;

    /**
     * Checks if a user group with the provided id exists.<br> The group is identified by either the id or the label.
     *
     * @param identityInfo The id or label of the group.
     * @return Returns {@code true} if a user group with the provided identifier exists, else {@code false}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    boolean userGroupExists(final String identityInfo) throws SqlDatabaseSystemException;

}
