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
package de.escidoc.core.aa.business.persistence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;

import de.escidoc.core.aa.business.interfaces.UserGroupHandlerInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.list.ListSorting;

/**
 * Interface of an data access object to access user account data.
 *
 * @author Torsten Tetteroo
 */
public interface UserAccountDaoInterface {

    /**
     * Checks if a user account with the provided id exists.<br> The user is identified by either the handle, the id, or
     * the login name.
     *
     * @param identityInfo The handle, id, or login name of the user.
     * @return Returns {@code true} if a user account with the provided identifier exists, else
     *         {@code false}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    boolean userAccountExists(final String identityInfo) throws SqlDatabaseSystemException;

    /**
     * Checks if a grant with the provided id exists.<br>
     *
     * @param grantId the id of the grant
     * @return Returns {@code true} if a grant with the provided identifier exists, else {@code false}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    boolean grantExists(final String grantId) throws SqlDatabaseSystemException;

    /**
     * Gets a data object for a user.<br> The user is identified by either the handle, the id, or the login name.
     *
     * @param identityInfo The handle, id, or login name of the user.
     * @return Returns the user data.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    UserAccount retrieveUserAccount(final String identityInfo) throws SqlDatabaseSystemException;

    /**
     * Get a Data Object for a user account.
     *
     * @param id The ID of the user account.
     * @return The user data.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    UserAccount retrieveUserAccountById(final String id) throws SqlDatabaseSystemException;

    /**
     * Get a Data Object for a user account.
     *
     * @param loginName The login name of the user account.
     * @return The user data.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    UserAccount retrieveUserAccountByLoginName(final String loginName) throws SqlDatabaseSystemException;

    /**
     * Get a Data Object for a user account.
     *
     * @param handle A handle identifying the user account.
     * @return The user data.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    UserAccount retrieveUserAccountByHandle(final String handle) throws SqlDatabaseSystemException;

    /**
     * Retrieves {@link UserAccount} objects using the provided values for filtering.
     *
     * @param criteria   The {@link Map} containing the filter criteria. This object is kept as provided by this
     *                   method.
     * @param offset     The index of the first result to be returned.
     * @param maxResults The maximal number of results to be returned.
     * @param orderBy    The predicate that shall be used for ordering.
     * @param sorting    The kind of ordering, i.e. ascending or descending.
     * @return Returns {@code List} of {@link UserAccount} objects selected by the provided parameters. If no
     *         parameter is provided, all user account objects are returned.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    List<UserAccount> retrieveUserAccounts(
        Map<String, Object> criteria, int offset, int maxResults, String orderBy, ListSorting sorting)
        throws SqlDatabaseSystemException;

    /**
     * Retrieves {@link UserAccount} objects using the provided values for filtering.
     *
     * @param criteria   The {@link String} containing the filter criteria as CQL query.
     * @param offset     The index of the first result to be returned.
     * @param maxResults The maximal number of results to be returned.
     * @return Returns {@code List} of {@link UserAccount} objects selected by the provided parameters. If no
     *         parameter is provided, all user account objects are returned.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SqlDatabaseSystemException  Thrown in case of an internal database access error.
     */
    List<UserAccount> retrieveUserAccounts(String criteria, int offset, int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException;

    /**
     * Save the provided user data.
     *
     * @param userAccount The user data object to save.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void save(final UserAccount userAccount) throws SqlDatabaseSystemException;

    /**
     * Update the provided user data.
     *
     * @param userAccount The user data object to save.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void update(final UserAccount userAccount) throws SqlDatabaseSystemException;

    /**
     * Delete the user account.
     *
     * @param userAccount The user data object to delete.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void delete(final UserAccount userAccount) throws SqlDatabaseSystemException;

    /**
     * Retrieves the grants referencing a specified role.<br> The grants will be sorted by related object and related
     * user account.
     *
     * @param role The id of the role for that the referencing grants shall be retrieved.
     * @return Returns sorted List of {@code Grant} objects.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    List<RoleGrant> retrieveGrantsByRole(final EscidocRole role) throws SqlDatabaseSystemException;

    /**
     * Retrieves the grants of an user.<br> The grants will be sorted by related role and related object.
     *
     * @param userId The id of the user whose grants shall be retrieved.
     * @return Returns sorted List of {@code Grant} objects.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    List<RoleGrant> retrieveGrantsByUserId(final String userId) throws SqlDatabaseSystemException;

    /**
     * Retrieves currently valid grant of the provided user for the specified role and (optional) id.
     *
     * @param userAccount The user account owning the grants that shall be retrieved.
     * @param role        The role that is granted to the user.
     * @param objId       The id of the object for that the role is granted to the user.
     * @return Returns the found {@code Grant} object or {@code null}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    RoleGrant retrieveCurrentGrant(final UserAccount userAccount, final EscidocRole role, final String objId)
        throws SqlDatabaseSystemException;

    /**
     * Retrieves a grant.
     *
     * @param userId  The id of the user owning the grant that shall be retrieved.
     * @param grantId The id of the grant that shall be retrieved.
     * @return Returns the found {@code Grant} object or {@code null}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    RoleGrant retrieveGrant(final String userId, final String grantId) throws SqlDatabaseSystemException;

    /**
     * Retrieves {@link RoleGrant} objects using the provided values for filtering.
     *
     * @param criteria The {@link Map} containing the filter criteria. This object is kept as provided by this method.
     * @param orderBy  The predicate that shall be used for ordering.
     * @param sorting  The kind of ordering, i.e. ascending or descending.
     * @return Returns {@code List} of {@link RoleGrant} objects selected by the provided parameters. If no
     *         parameter is provided, all roleGrant objects are returned.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    List<RoleGrant> retrieveGrants(Map<String, HashSet<String>> criteria, String orderBy, ListSorting sorting)
        throws SqlDatabaseSystemException;

    /**
     * Retrieves {@link RoleGrant} objects using the provided values for filtering.
     *
     * @param criteria         The {@link String} containing the filter criteria as CQL query.
     * @param offset           The index of the first result to be returned.
     * @param maxResults       The maximal number of results to be returned.
     * @param userGroupHandler business object to access methods for user groups
     * @return Returns {@code List} of {@link RoleGrant} objects selected by the provided parameters. If no
     *         parameter is provided, all roleGrant objects are returned.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SystemException             Thrown in case of an internal system error.
     */
    List<RoleGrant> retrieveGrants(
        String criteria, int offset, int maxResults, UserGroupHandlerInterface userGroupHandler)
        throws InvalidSearchQueryException, SystemException;

    /**
     * Save the provided grant.
     *
     * @param grant The {@code Grant} object to save.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void save(final RoleGrant grant) throws SqlDatabaseSystemException;

    /**
     * Update the provided grant.
     *
     * @param grant The {@code Grant} object to save.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void update(final RoleGrant grant) throws SqlDatabaseSystemException;

    /**
     * Retrieves a user-attribute.
     *
     * @param userId      The id of the user owning the attribute that shall be retrieved.
     * @param attributeId The id of the attribute that shall be retrieved.
     * @return Returns the found {@code UserAttribute} object or {@code null}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    UserAttribute retrieveAttribute(final String userId, final String attributeId) throws SqlDatabaseSystemException;

    /**
     * Retrieves all user-attributes.
     *
     * @param userAccount The user owning the attributes that shall be retrieved.
     * @return Returns the found list of {@code UserAttribute} objects or an empty list.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    List<UserAttribute> retrieveAttributes(final UserAccount userAccount) throws SqlDatabaseSystemException;

    /**
     * Retrieves all user-attributes.
     *
     * @param userAccount   The user owning the attributes that shall be retrieved.
     * @param attributeName The name of the attribute.
     * @return Returns the found list of {@code UserAttribute} objects or an empty list.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    List<UserAttribute> retrieveAttributes(final UserAccount userAccount, final String attributeName)
        throws SqlDatabaseSystemException;

    /**
     * Retrieve all attributes with given keys and values.
     *
     * @param attributes set of key/value pairs
     * @return List with userAttribute-Object
     * @throws SqlDatabaseSystemException e
     */
    List<UserAttribute> retrieveAttributes(final Set<HashMap<String, String>> attributes)
        throws SqlDatabaseSystemException;

    /**
     * Save the provided grant.
     *
     * @param attribute The {@code UserAttribute} object to save.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void save(final UserAttribute attribute) throws SqlDatabaseSystemException;

    /**
     * Update the provided grant.
     *
     * @param attribute The {@code UserAttribute} object to save.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void update(final UserAttribute attribute) throws SqlDatabaseSystemException;

    /**
     * Delete the provided grant.
     *
     * @param attribute The {@code UserAttribute} object to save.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void delete(final UserAttribute attribute) throws SqlDatabaseSystemException;

    /**
     * Get the user login data.
     *
     * @param handle The login name of the user account.
     * @return The user data.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    UserLoginData retrieveUserLoginDataByHandle(final String handle) throws SqlDatabaseSystemException;

    /**
     * Get the user login data.
     *
     * @param id The users id.
     * @return The login datas of the user in a {@code List}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    List<UserLoginData> retrieveUserLoginDataByUserId(final String id) throws SqlDatabaseSystemException;

    /**
     * Save the login data.
     *
     * @param data The user login data.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void saveOrUpdate(UserLoginData data) throws SqlDatabaseSystemException;

    /**
     * Delete the user login data.
     *
     * @param data The user login data.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void delete(UserLoginData data) throws SqlDatabaseSystemException;

    /**
     * Delete the user login data.
     *
     * @param handle The handle identifying user login data.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void deleteUserLoginData(String handle) throws SqlDatabaseSystemException;

    /**
     * Retrieve the user details of a user account that are used for authentication.
     *
     * @param handle The handle identifying the user account.
     * @return Returns the {@link UserDetails} of the addressed {@link UserAccount}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    UserDetails retrieveUserDetails(String handle) throws SqlDatabaseSystemException;

    /**
     * Retrieve the user login data whose expiry timestamp is less then the specified value.
     *
     * @param timestamp The timestamp (in milli seconds) to compare the login data timestamp with.
     * @return Returns all login data that have an expiry timestamp that is less than the specified value. This list may
     *         be empty, if no such login data exists.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    List<UserLoginData> retrieveExpiredUserLoginData(final long timestamp) throws SqlDatabaseSystemException;

    /**
     * Retrieves the preferences of an user.<br>
     *
     * @param userId The id of the user whose preferences shall be retrieved.
     * @return Returns List of {@code UserPreference} objects.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    List<UserPreference> retrievePreferencesByUserId(final String userId) throws SqlDatabaseSystemException;

    /**
     * Save the provided preference.
     *
     * @param preference The {@code UserPreference} object to save.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void save(final UserPreference preference) throws SqlDatabaseSystemException;

    /**
     * Delete the user preference.
     *
     * @param data The user login data.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void delete(UserPreference data) throws SqlDatabaseSystemException;
}
