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
package de.escidoc.core.aa.business.cache;

import com.sun.xacml.cond.EvaluationResult;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.xacml.XacmlPolicySet;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionRoleIsGranted;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import org.apache.commons.collections.map.LRUMap;
import org.springframework.security.userdetails.UserDetails;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to cache policies retrieved from the database for the XACML engine.<br>
 * This class caches different policy types:
 * <ul>
 * <li>The policies of a role, provided as <code>PolicySet</code>, are
 * cached using the role id as the key.
 * <li>The built policies of a user (depending on his/her roles and stored user
 * specific policies), provided as a <code>List</code> of
 * <code>PolicySet</code> or <code>Policy</code> objects, are cached using
 * the user id and the action id as keys.
 * <li>The policies of a user, provided as a <code>List</code> of
 * <code>Policy</code> objects, are temporarily cached using the user id and
 * the action id as keys. These are cleared when the built properties for the
 * same user and action are stored.
 * <li>The roles are cached using the role name as the key.</li>
 * 
 * </ul>
 * 
 * @author Roland Werner (Accenture)
 * @aa
 * 
 */
public final class PoliciesCache {

    /**
     * Fall back value if reading property
     * {@link <code>EscidocConfiguration.AA_CACHE_ROLES_SIZE</code>} fails.
     * 
     * @aa
     */
    private static final int ROLES_CACHE_SIZE_FALL_BACK = 20;

    /**
     * Fall back value if reading property
     * {@link <code>EscidocConfiguration.AA_CACHE_USERS_SIZE</code>} fails.
     * 
     * @aa
     */
    private static final int USERS_CACHE_SIZE_FALL_BACK = 50;

    /**
     * Fall back value if reading property
     * {@link <code>EscidocConfiguration.AA_CACHE_GROUPS_SIZE</code>} fails.
     * 
     * @aa
     */
    private static final int GROUPS_CACHE_SIZE_FALL_BACK = 200;

    /**
     * Fall back value if reading property
     * {@link <code>EscidocConfiguration.AA_CACHE_RESOURCES_IN_ROLE_IS_GRANTED_SIZE</code>}
     * fails.
     * 
     * @aa
     */
    private static final int AA_CACHE_RESOURCES_IN_ROLE_IS_GRANTED_SIZE_FALL_BACK =
        20;

    /**
     * The user policies cache is implemented as an <code>LRUMap</code>
     * (least-recently-used map), so it can only grow to a certain size.
     * 
     * @aa
     */
    private static Map<String, XacmlPolicySet> userPoliciesCache;

    /**
     * The user grants cache is implemented as an <code>LRUMap</code>
     * (least-recently-used map), so it can only grow to a certain size.
     * 
     * @aa
     */
    private static Map<String, Map<Object, Object>> userGrantsCache;

    /**
     * The user details cache is implemented as an <code>LRUMap</code>
     * (least-recently-used map), so it can only grow to a certain size.
     * 
     * @aa
     */
    private static Map<String, UserDetails> userDetailsCache;

    /**
     * The group grants cache is implemented as an <code>LRUMap</code>
     * (least-recently-used map), so it can only grow to a certain size.
     * 
     * @aa
     */
    private static Map<String, XacmlPolicySet> groupPoliciesCache;

    /**
     * The group grants cache is implemented as an <code>LRUMap</code>
     * (least-recently-used map), so it can only grow to a certain size.
     * 
     * @aa
     */
    private static Map<String, Map<Object, Object>> groupGrantsCache;

    /**
     * The group grants cache is implemented as an <code>LRUMap</code>
     * (least-recently-used map), so it can only grow to a certain size.
     * 
     * @aa
     */
    private static Map<String, Set<String>> userGroupsCache;

    /**
     * The role policies cache is implemented as a <code>LRUMap</code>
     * (least-recently-used map), so it can only grow to a certain size.
     * 
     * @aa
     */
    private static Map<URI, XacmlPolicySet> rolePoliciesCache;

    /**
     * The roles cache is implemented as a <code>LRUMap</code>
     * (least-recently-used map), so it can only grow to a certain size.
     * 
     * @aa
     */
    private static Map<String, EscidocRole> rolesCache;

    /**
     * The roleIsGranted result cache is implemented as a <code>LRUMap</code>
     * (least-recently-used map) (addressed by user-id) of a <code>LRUMap</code>
     * (addressed by role-name) of a <code>LRUMap</code> (addressed by
     * resource-id or <code>null</code>) , so it can only grow to a certain
     * size.
     * 
     * @aa
     */
    private static Map<String, Map<String, Map<String, EvaluationResult>>> roleIsGrantedCache;

    private static int rolesCacheSize;

    private static int usersCacheSize;

    private static int groupsCacheSize;

    private static int resourcesInXacmlFunctionRoleIsGrantedCacheSize;

    static {
        initCaches();
    }

    /**
     * Private constructor to prevent class from being instantiated.
     * 
     * @aa
     */
    private PoliciesCache() {
    }

    /**
     * Initializes the caches.<br/>The cache sizes are fetched from the eSciDoc
     * Configuration. If this fails, the default values are used as fall back.
     */
    private static void initCaches() {

        try {
            rolesCacheSize =
                Integer.parseInt(EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_AA_CACHE_ROLES_SIZE));
        }
        catch (Exception e) {
            rolesCacheSize = ROLES_CACHE_SIZE_FALL_BACK;
        }
        try {
            usersCacheSize =
                Integer.parseInt(EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_AA_CACHE_USERS_SIZE));
        }
        catch (Exception e) {
            usersCacheSize = USERS_CACHE_SIZE_FALL_BACK;
        }
        try {
            groupsCacheSize =
                Integer.parseInt(EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_AA_CACHE_GROUPS_SIZE));
        }
        catch (Exception e) {
            groupsCacheSize = GROUPS_CACHE_SIZE_FALL_BACK;
        }
        try {
            resourcesInXacmlFunctionRoleIsGrantedCacheSize =
                Integer
                    .parseInt(EscidocConfiguration
                        .getInstance()
                        .get(
                            EscidocConfiguration.ESCIDOC_CORE_AA_CACHE_RESOURCES_IN_ROLE_IS_GRANTED_SIZE));
        }
        catch (Exception e) {
            resourcesInXacmlFunctionRoleIsGrantedCacheSize =
                AA_CACHE_RESOURCES_IN_ROLE_IS_GRANTED_SIZE_FALL_BACK;
        }

        rolesCache = new LRUMap(rolesCacheSize);
        rolePoliciesCache = new LRUMap(rolesCacheSize);
        userGrantsCache = new LRUMap(usersCacheSize);
        userPoliciesCache = new LRUMap(usersCacheSize);
        groupGrantsCache = new LRUMap(groupsCacheSize);
        groupPoliciesCache = new LRUMap(groupsCacheSize);
        userDetailsCache = new LRUMap(usersCacheSize);
        userGroupsCache = new LRUMap(usersCacheSize);

        roleIsGrantedCache = new LRUMap(usersCacheSize);

    }

    /**
     * Stores the provided {@link EvaluationResult} for
     * {@link XacmlFunctionRoleIsGranted} result using the user ID, the role ID
     * and (optional) the resource ID as key. <br>
     * Realized as an outer {@link LRUMap} that uses user ID as key and which
     * has an inner {@link LRUMap} as value, that uses the role ID as key and
     * has an inner {@link LRUMap} as value that uses the resource id or
     * <code>null</code> as key and has an {@link Evaluation result} object as
     * value.
     * 
     * @param userOrGroupId
     *            The user or group ID to use as key for {@link LRUMap}. This must not
     *            be <code>null</code>. If <code>null</code> is provided,
     *            nothing is done.
     * @param roleId
     *            The role ID to use as key for {@link LRUMap}. This must not
     *            be <code>null</code>. If <code>null</code> is provided,
     *            nothing is done.
     * @param resourceId
     *            The resource ID to use as key for {@link LRUMap}. This may be
     *            <code>null</code>.
     * @param roleIsGranted
     *            The {@link EvaluationResult} holding the result of the
     *            {@link XacmlFunctionRoleIsGranted} for the provided values.
     * 
     * @aa
     */
    public static synchronized void putRoleIsGrantedEvaluationResult(
        final String userOrGroupId, final String roleId, final String resourceId,
        final EvaluationResult roleIsGranted) {

        if (userOrGroupId == null || roleId == null) {
            return;
        }

        Map<String, Map<String, EvaluationResult>> roleMap =
            roleIsGrantedCache.get(userOrGroupId);
        if (roleMap == null) {
            roleMap = new LRUMap(rolesCacheSize);
            roleIsGrantedCache.put(userOrGroupId, roleMap);
        }
        Map<String, EvaluationResult> resourceMap = roleMap.get(roleId);
        if (resourceMap == null) {
            resourceMap =
                new LRUMap(resourcesInXacmlFunctionRoleIsGrantedCacheSize);
            roleMap.put(roleId, resourceMap);
        }
        resourceMap.put(resourceId, roleIsGranted);
    }

    /**
     * Stores the provided user's policy set using the provided user ID as key.
     * <br>
     * The user's policy set has to be provided within a
     * <code>XacmlPolicySet</code>. <br>
     * Realized as an outer HashMap that uses user ID as key and which has an
     * inner HashMap as value. The inner HashMap has action as key and the list
     * of policies as value.
     * 
     * @param userId
     *            The user ID to use as key for HashMap. This must not be
     *            <code>null</code>. If <code>null</code> is provided,
     *            nothing is done.
     * @param userPolicies
     *            The user policy set contained in a
     *            <code>XacmlPolicySet</code>.
     * @aa
     */
    public static synchronized void putUserPolicies(
        final String userId, final XacmlPolicySet userPolicies) {

        if (userId == null) {
            return;
        }
        getUserPoliciesCache().put(userId, userPolicies);
    }

    /**
     * Stores the provided group's policy set using the provided group ID as key.
     * <br>
     * The group's policy set has to be provided within a
     * <code>XacmlPolicySet</code>. <br>
     * Realized as an outer HashMap that uses group ID as key and which has an
     * inner HashMap as value. The inner HashMap has action as key and the list
     * of policies as value.
     * 
     * @param groupId
     *            The group ID to use as key for HashMap. This must not be
     *            <code>null</code>. If <code>null</code> is provided,
     *            nothing is done.
     * @param groupPolicies
     *            The group policy set contained in a
     *            <code>XacmlPolicySet</code>.
     * @aa
     */
    public static synchronized void putGroupPolicies(
        final String groupId, final XacmlPolicySet groupPolicies) {

        if (groupId == null) {
            return;
        }
        getGroupPoliciesCache().put(groupId, groupPolicies);
    }

    /**
     * Stores the provided user's grants using the provided user ID as key. <br>
     * 
     * @param userId
     *            The user ID to use as key for HashMap. This must not be
     *            <code>null</code>. If <code>null</code> is provided,
     *            nothing is done.
     * @param userGrants
     *            The grants of the user contained in a <code>Map</code>.
     * @aa
     */
    public static synchronized void putUserGrants(
        final String userId, final Map userGrants) {

        if (userId == null) {
            return;
        }
        getUserGrantsCache().put(userId, userGrants);
    }

    /**
     * Stores the provided group's grants using the provided group ID as key. <br>
     * 
     * @param groupId
     *            The group ID to use as key for HashMap. This must not be
     *            <code>null</code>. If <code>null</code> is provided,
     *            nothing is done.
     * @param groupGrants
     *            The grants of the group contained in a <code>Map</code>.
     * @aa
     */
    public static synchronized void putGroupGrants(
        final String groupId, final Map groupGrants) {

        if (groupId == null) {
            return;
        }
        getGroupGrantsCache().put(groupId, groupGrants);
    }

    /**
     * Stores the provided user's details using the provided user ID as key. <br>
     * 
     * @param handle
     *            The handle to use as key for HashMap. This must not be
     *            <code>null</code>. If <code>null</code> is provided,
     *            nothing is done.
     * @param userDetails
     *            The details of the user contained in a <code>Map</code>.
     * @aa
     */
    public static synchronized void putUserDetails(
        final String handle, final UserDetails userDetails) {

        if (handle == null) {
            return;
        }
        getUserDetailsCache().put(handle, userDetails);
    }

    /**
     * Stores the groups the user with given userId belongs to. <br>
     * 
     * @param userId
     *            The userId to use as key for HashMap. This must not be
     *            <code>null</code>. If <code>null</code> is provided,
     *            nothing is done.
     * @param userGroups
     *            The groups of the user contained in a <code>Set</code>.
     * @aa
     */
    public static synchronized void putUserGroups(
        final String userId, final Set<String> userGroups) {

        if (userId == null) {
            return;
        }
        if (userGroups == null) {
            getUserGroupsCache().put(userId, new HashSet<String>());
        } else {
            getUserGroupsCache().put(userId, userGroups);
        }
    }

    /**
     * Stores the provided list of policies using the provided role as key.
     * 
     * @param idReference
     *            The reference of the role's policies set.
     * @param rolePolicies
     *            The policy set of the role referenced by the provided id in a
     *            <code>PolicyFinderResult</code>
     * @aa
     */
    public static synchronized void putRolePolicySet(
        final URI idReference, final XacmlPolicySet rolePolicies) {

        getRolePoliciesCache().put(idReference, rolePolicies);
    }

    /**
     * Stores the provided role using the role id as key.
     * 
     * @param roleId
     *            Identifier of the role.
     * @param role
     *            The role to cache.
     * @aa
     */
    public static synchronized void putRole(
        final String roleId, final EscidocRole role) {

        getRolesCache().put(roleId, role);
    }

    /**
     * Gets the {@link EvaluationResult} for {@link XacmlFunctionRoleIsGranted}
     * result using the user ID, the role ID and (optional) the resource ID as
     * key. <br>
     * Realized as an outer {@link LRUMap} that uses user ID as key and which
     * has an inner {@link LRUMap} as value, that uses the role ID as key and
     * has an inner {@link LRUMap} as value that uses the resource id or
     * <code>null</code> as key and has an {@link Evaluation result} object as
     * value.
     * 
     * @param userOrGroupId
     *            The user ID to use as key for {@link LRUMap}. This must not
     *            be <code>null</code>. If <code>null</code> is provided,
     *            nothing is done.
     * @param roleId
     *            The role ID to use as key for {@link LRUMap}. This must not
     *            be <code>null</code>. If <code>null</code> is provided,
     *            nothing is done.
     * @param resourceId
     *            The resource ID to use as key for {@link LRUMap}. This may be
     *            <code>null</code>.
     * @returns roleIsGranted The {@link EvaluationResult} holding the result of
     *          the {@link XacmlFunctionRoleIsGranted} for the provided values.
     * 
     * @aa
     */
    public static synchronized EvaluationResult getRoleIsGrantedEvaluationResult(
        final String userOrGroupId, final String roleId, final String resourceId) {

        Map<String, Map<String, EvaluationResult>> roleMap =
            roleIsGrantedCache.get(userOrGroupId);
        if (roleMap == null) {
            return null;
        }
        Map<String, EvaluationResult> resourceMap = roleMap.get(roleId);
        if (resourceMap == null) {
            return null;
        }
        return resourceMap.get(resourceId);
    }

    /**
     * Gets the the user policy set for the provided user ID.<br>
     * 
     * Realisation see method put.
     * 
     * @param userId
     *            The user ID to use as key for HashMap.
     * @return The <code>XacmlPolicySet</code> containing the policy set
     *         that consists of the user's polices, or <code>null</code>.
     * @aa
     */
    public static synchronized XacmlPolicySet getUserPolicies(
        final String userId) {

        return getUserPoliciesCache().get(userId);
    }

    /**
     * Gets the the group policy set for the provided group ID.<br>
     * 
     * Realisation see method put.
     * 
     * @param groupId
     *            The group ID to use as key for HashMap.
     * @return The <code>XacmlPolicySet</code> containing the policy set
     *         that consists of the group's polices, or <code>null</code>.
     * @aa
     */
    public static synchronized XacmlPolicySet getGroupPolicies(
        final String groupId) {

        return getGroupPoliciesCache().get(groupId);
    }

    /**
     * Gets the the user grants for the provided user ID.<br>
     * 
     * @param userId
     *            The user ID to use as key for HashMap.
     * @return The grants of the user in a <code>Map</code>, or
     *         <code>null</code>.
     * @aa
     */
    public static synchronized Map getUserGrants(final String userId) {

        return getUserGrantsCache().get(userId);
    }

    /**
     * Gets the the group grants for the provided group ID.<br>
     * 
     * @param groupId
     *            The group ID to use as key for HashMap.
     * @return The grants of the group in a <code>Map</code>, or
     *         <code>null</code>.
     * @aa
     */
    public static synchronized Map getGroupGrants(final String groupId) {

        return getGroupGrantsCache().get(groupId);
    }

    /**
     * Gets the the user details for the provided handle.<br>
     * 
     * @param handle
     *            The handle to use as key for HashMap.
     * @return The details of the user as <code>UserDetails</code>, or
     *         <code>null</code>.
     * @aa
     */
    public static synchronized UserDetails getUserDetails(final String handle) {

        return getUserDetailsCache().get(handle);
    }

    /**
     * Gets the the user groups for the provided userId.<br>
     * 
     * @param userId
     *            The userId to use as key for HashMap.
     * @return The groups of the user as <code>Set</code>, or
     *         <code>null</code>.
     * @aa
     */
    public static synchronized Set<String> getUserGroups(final String userId) {

        return getUserGroupsCache().get(userId);
    }

    /**
     * Gets the policies for the provided role.
     * 
     * @param idReference
     *            The reference of the role's policies set.
     * @return Returns the <code>PolicyFinderResult</code> containing the
     *         policy set of the addressed role.
     * @aa
     */
    public static synchronized XacmlPolicySet getRolePolicySet(
        final URI idReference) {

        return getRolePoliciesCache().get(idReference);
    }

    /**
     * Gets the role for the provided role id.
     * 
     * @param roleId
     *            The role identifier.
     * @return Returns the <code>EscidocRole</code> for the provided key.
     * @aa
     */
    public static synchronized EscidocRole getRole(final String roleId) {

        return getRolesCache().get(roleId);
    }

    /**
     * Removes all stored policies for the provided user ID from the cache.<br>
     * Has to be called whenever new policies come into effect for a specific
     * user (e.g. when a new role has been assigned to this user).
     * 
     * @param userId
     *            The user ID to remove policies from the cache for
     * @aa
     */
    public static synchronized void clearUserPolicies(final String userId) {

        getUserPoliciesCache().remove(userId);
        getRoleIsGrantedCache().remove(userId);
        getUserGrantsCache().remove(userId);
    }

    /**
     * Removes all stored policies for the provided group ID from the cache.<br>
     * Has to be called whenever new policies come into effect for a specific
     * group (e.g. when a new role has been assigned to this group).
     * 
     * @param groupId
     *            The group ID to remove policies from the cache for
     * @aa
     */
    public static synchronized void clearGroupPolicies(final String groupId) {
        getGroupPoliciesCache().remove(groupId);
        getRoleIsGrantedCache().remove(groupId);
        getGroupGrantsCache().remove(groupId);
    }

    /**
     * Removes all stored details for the provided handle from the cache.<br>
     * Has to be called whenever data changes for a specific
     * user (e.g. when a new handle is assigned).
     * 
     * @param handle
     *            The handle to remove from the cache for
     * @aa
     */
    public static synchronized void clearUserDetails(final String handle) {

        getUserDetailsCache().remove(handle);
    }

    /**
     * Removes everything from the userGroupsCache.
     * 
     * @aa
     */
    public static synchronized void clearUserGroups() {
        getUserGroupsCache().clear();
    }

    /**
     * Removes groups of specified user from the userGroupsCache.
     * @param userId id of the user
     * 
     * @aa
     */
    public static synchronized void clearUserGroups(final String userId) {

        getUserGroupsCache().remove(userId);
    }

    /**
     * Removes all data stored in the cache for the role identified by the
     * provided role id from the cache.<br>
     * Has to be called whenever the role changes or has been deleted.
     * 
     * @param roleId
     *            The id of the role to remove from the cache.
     * @throws SystemException e
     */
    public static synchronized void clearRole(final String roleId) 
                                                throws SystemException {

        try {
            getRolePoliciesCache().remove(new URI(roleId));
        } catch (URISyntaxException e) {
            throw new SystemException(e);
        }

        // FIXME: roles may be cached by name, not id. As a quick fix, the
        // cache is completely cleared. This should be optimized
        getRolesCache().clear();

        // The user policies cache still holds policies for the removed role.
        // To avoid usage of invalidated roles, the caches are cleared.
        getUserPoliciesCache().clear();

        // iterate over all maps stored in roleIsGrantedCache to remove the ones
        // relevant for the provided role id.
        for (Map<String, Map<String, EvaluationResult>> userCache : getRoleIsGrantedCache().values()) {
            userCache.remove(roleId);
        }
    }

    /**
     * Removes all stored policies from the cache.
     * 
     * @aa
     */
    public static void clear() {
        getRolePoliciesCache().clear();
        getRolesCache().clear();
        getUserPoliciesCache().clear();
        getRoleIsGrantedCache().clear();
        getUserGrantsCache().clear();
        getUserDetailsCache().clear();
        getGroupPoliciesCache().clear();
        getGroupGrantsCache().clear();
        getUserGroupsCache().clear();
    }

    /**
     * @return the rolePoliciesCache
     */
    private static Map<URI, XacmlPolicySet> getRolePoliciesCache() {
        return rolePoliciesCache;
    }

    /**
     * @return the rolesCache
     */
    private static Map<String, EscidocRole> getRolesCache() {
        return rolesCache;
    }

    /**
     * @return the userGrantsCache
     */
    private static Map<String, Map<Object, Object>> getUserGrantsCache() {
        return userGrantsCache;
    }

    /**
     * @return the userPoliciesCache
     */
    private static Map<String, XacmlPolicySet> getUserPoliciesCache() {
        return userPoliciesCache;
    }

    /**
     * @return the userDetailsCache
     */
    private static Map<String, UserDetails> getUserDetailsCache() {
        return userDetailsCache;
    }

    /**
     * @return the groupGrantsCache
     */
    private static Map<String, Map<Object, Object>> getGroupGrantsCache() {
        return groupGrantsCache;
    }

    /**
     * @return the groupPoliciesCache
     */
    private static Map<String, XacmlPolicySet> getGroupPoliciesCache() {
        return groupPoliciesCache;
    }

    /**
     * @return the userGroupsCache
     */
    private static Map<String, Set<String>> getUserGroupsCache() {
        return userGroupsCache;
    }

    /**
     * @return the roleIsGrantedCache
     */
    private static Map<String, Map<String, Map<String, EvaluationResult>>> getRoleIsGrantedCache() {
        return roleIsGrantedCache;
    }

}
