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

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.PartialCacheKey;
import com.googlecode.ehcache.annotations.Property;
import com.googlecode.ehcache.annotations.TriggersRemove;
import com.sun.xacml.cond.EvaluationResult;

import de.escidoc.core.aa.business.interfaces.UserAccountHandlerInterface;
import de.escidoc.core.aa.business.interfaces.UserGroupHandlerInterface;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserAccount;
import de.escidoc.core.aa.business.persistence.UserAccountDaoInterface;
import de.escidoc.core.aa.business.xacml.XacmlPolicySet;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.EscidocUserDetails;

/**
 * Class to cache policies retrieved from the database for the XACML engine.<br> This class caches different policy
 * types: <ul> <li>Cache: roleIsGrantedCache, Key: user or group ID. The information if a specific role is granted to a
 * user or group for a specific resource (scope-definitions).</li> <li>Cache: userPoliciesCache, Key: user ID. The
 * policy-set of all policies of a specific user.</li> <li>Cache: groupPoliciesCache, Key: group ID. The policy-set of
 * all policies of a specific group.</li> <li>Cache: userGrantsCache, Key: user ID. The grants of a user as map with
 * key:roleId, value: map with key:scopeId, value:RoleGrant.</li> <li>Cache: groupGrantsCache, Key: group ID. The grants
 * of a group as map with key:roleId, value: map with key:scopeId, value:RoleGrant.</li> <li>Cache: userDetailsCache,
 * Key: user ID. The details of a specific user</li> <li>Cache: userGroupsCache, Key: user ID. The groups of a specific
 * user</li> <li>Cache: rolePoliciesCache, Key: URI idReference. The policies of a specific role</li> <li>Cache:
 * rolesCache, Key: role ID. The Role-Object of a specific role ID</li>
 * <p/>
 * </ul>
 *
 * @author Roland Werner (Accenture)
 */
@Service("security.PoliciesCache")
public class PoliciesCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(PoliciesCache.class);

    @Autowired
    @Qualifier("business.UserAccountHandler")
    private UserAccountHandlerInterface userAccountHandler;

    @Autowired
    @Qualifier("business.UserGroupHandler")
    private UserGroupHandlerInterface userGroupHandler;

    @Autowired
    @Qualifier("persistence.UserAccountDao")
    private UserAccountDaoInterface userAccountDao;

    @Autowired
    @Qualifier("persistence.EscidocRoleDao")
    private EscidocRoleDaoInterface roleDao;

    /**
     * Stores the provided roleIsGranted Map for {@link XacmlFunctionRoleIsGranted} result using the user- or group ID
     * as key.
     *
     * @param userOrGroupId The user or group ID..
     * @param roleIsGranted The Map with key: role ID, value: map with key: resource ID, value: EvaluationResult.
     * @return
     */
    @Cacheable(cacheName = "roleIsGrantedCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public Map<String, Map<String, EvaluationResult>> putRoleIsGrantedEvaluationResult(@PartialCacheKey
    final String userOrGroupId, final Map<String, Map<String, EvaluationResult>> roleIsGranted) {
        return roleIsGranted;
    }

    /**
     * Gets the provided roleIsGranted Map for {@link XacmlFunctionRoleIsGranted} result using the user- or group ID as
     * key.
     *
     * @param userOrGroupId The user or group ID..
     * @return Map.  The Map with key: role ID, value: map with key: resource ID, value: EvaluationResult.
     */
    @Cacheable(cacheName = "roleIsGrantedCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public Map<String, Map<String, EvaluationResult>> getRoleIsGrantedEvaluationResultCached(@PartialCacheKey
    final String userOrGroupId) {
        return new HashMap<String, Map<String, EvaluationResult>>();
    }

    /**
     * Gets the user policy set for the provided user ID.<br> If no policy-Set is stored for the given user, return
     * null.
     * <p/>
     *
     * @param userId The user ID to use as key for HashMap.
     * @return The {@code XacmlPolicySet} containing the policy set that consists of the user's polices, or
     *         {@code null}.
     */
    @Cacheable(cacheName = "userPoliciesCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public XacmlPolicySet getUserPolicies(final String userId) {
        return null;
    }

    /**
     * Puts the user policy set for the provided user ID.<br>
     * <p/>
     *
     * @param userId The user ID to use as key for HashMap.
     * @param policySet
     * @return The {@code XacmlPolicySet} containing the policy set that consists of the user's polices.
     */
    @Cacheable(cacheName = "userPoliciesCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public XacmlPolicySet putUserPolicies(@PartialCacheKey
    final String userId, final XacmlPolicySet policySet) {
        return policySet;
    }

    /**
     * Gets the the group policy set for the provided group ID.<br> If no policy-Set is stored for the given group,
     * return null.
     * <p/>
     *
     * @param groupId The group ID to use as key for HashMap.
     * @return The {@code XacmlPolicySet} containing the policy set that consists of the group's polices, or
     *         {@code null}.
     */
    @Cacheable(cacheName = "groupPoliciesCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public XacmlPolicySet getGroupPolicies(final String groupId) {
        return null;
    }

    /**
     * Puts the group policy set for the provided group ID.<br>
     * <p/>
     *
     * @param groupId The group ID to use as key for HashMap.
     * @param policySet
     * @return The {@code XacmlPolicySet} containing the policy set that consists of the user's polices.
     */
    @Cacheable(cacheName = "groupPoliciesCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public XacmlPolicySet putGroupPolicies(@PartialCacheKey
    final String groupId, final XacmlPolicySet policySet) {
        return policySet;
    }

    /**
     * Gets the the user grants for the provided user ID.<br>
     *
     * @param userId The user ID to use as key for HashMap.
     * @return The grants of the user in a {@code Map}, or {@code null}.
     * @throws de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    @Cacheable(cacheName = "userGrantsCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public Map<String, Map<String, List<RoleGrant>>> getUserGrants(final String userId)
        throws UserAccountNotFoundException, SystemException {
        return userAccountHandler.retrieveCurrentGrantsAsMap(userId);
    }

    /**
     * Gets the the group grants for the provided group ID.<br>
     *
     * @param groupId The group ID to use as key for HashMap.
     * @return The grants of the group in a {@code Map}, or {@code null}.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    @Cacheable(cacheName = "groupGrantsCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public Map<String, Map<String, List<RoleGrant>>> getGroupGrants(final String groupId)
        throws ResourceNotFoundException, SystemException {
        return userGroupHandler.retrieveCurrentGrantsAsMap(groupId);
    }

    /**
     * Gets the the user details for the provided handle.<br>
     *
     * @param handle The handle to use as key for HashMap.
     * @return The details of the user as {@code UserDetails}, or {@code null}.
     * @throws de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException
     */
    @Cacheable(cacheName = "userDetailsCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public UserDetails getUserDetails(final String handle) throws SqlDatabaseSystemException {
        EscidocUserDetails result = null;
        final UserAccount userAccount = userAccountDao.retrieveUserAccountByHandle(handle);
        if (userAccount != null) {
            result = new EscidocUserDetails();
            result.setId(userAccount.getId());
            result.setRealName(userAccount.getName());
        }
        return result;
    }

    /**
     * Gets the the user groups for the provided userId.<br>
     *
     * @param userId The userId.
     * @return The groups of the user as {@code Set}, or {@code null}.
     * @throws de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    @Cacheable(cacheName = "userGroupsCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public Set<String> getUserGroups(final String userId) throws UserAccountNotFoundException, SystemException {
        return userGroupHandler.retrieveGroupsForUser(userId, true);
    }

    /**
     * Gets the policies for the provided role.
     *
     * @param idReference The reference of the role's policies set.
     * @param role        The role-object.
     * @return Returns the {@code PolicyFinderResult} containing the policy set of the addressed role.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    @Cacheable(cacheName = "rolePoliciesCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public XacmlPolicySet getRolePolicySet(@PartialCacheKey
    final URI idReference, final EscidocRole role) throws WebserverSystemException {
        if (role == null) {
            return null;
        }
        return (XacmlPolicySet) role.getXacmlPolicySet();
    }

    /**
     * Gets the role for the provided role id.
     *
     * @param roleId The role identifier.
     * @return Returns the {@code EscidocRole} for the provided key.
     */
    @Cacheable(cacheName = "rolesCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public EscidocRole getRole(final String roleId) {
        EscidocRole role = null;
        try {
            role = roleDao.retrieveRole(roleId);
            role.isLimited();
        }
        catch (SqlDatabaseSystemException e) {
            final String message = "Error on retrieving role '" + roleId + '\'';
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn(message);
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(message, e);
            }
        }
        return role;
    }

    /**
     * Removes all EvaluationResult-Objects (roles granted to a user or group) for the provided user ID from the cache.
     * <br> Has to be called whenever grants of a user change.
     *
     * @param userOrGroupId The user- or group ID to remove policies from the cache for
     */
    @TriggersRemove(cacheName = "roleIsGrantedCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public void clearRoleIsGranted(@PartialCacheKey
    final String userOrGroupId) {
    }

    /**
     * Removes all EvaluationResult-Objects (roles granted to a user or group) from the cache.
     */
    @TriggersRemove(cacheName = "roleIsGrantedCache", removeAll = true)
    public void clearRoleIsGranted() {
    }

    /**
     * Removes all stored policies for the provided user ID from the cache.<br> Has to be called whenever new policies
     * come into effect for a specific user (e.g. when a new role has been assigned to this user).
     *
     * @param userId The user ID to remove policies from the cache for
     */
    @TriggersRemove(cacheName = "userPoliciesCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public void clearUserPolicies(final String userId) {
    }

    /**
     * Removes all stored policies from the cache.<br> Has to be called whenever new policies come into effect for a
     * specific user (e.g. when a new role has been assigned to this user).
     */
    @TriggersRemove(cacheName = "userPoliciesCache", removeAll = true)
    public void clearUserPolicies() {
    }

    /**
     * Removes all stored policies for the provided group ID from the cache.<br> Has to be called whenever new policies
     * come into effect for a specific group (e.g. when a new role has been assigned to this group).
     *
     * @param groupId The group ID to remove policies from the cache for
     */
    @TriggersRemove(cacheName = "groupPoliciesCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public void clearGroupPolicies(final String groupId) {
    }

    /**
     * Removes all stored policies from the cache.<br> Has to be called whenever new policies come into effect for a
     * specific group (e.g. when a new role has been assigned to this group).
     */
    @TriggersRemove(cacheName = "groupPoliciesCache", removeAll = true)
    public void clearGroupPolicies() {
    }

    /**
     * Removes all stored grants for the provided user ID from the cache.
     *
     * @param userId The user ID to remove grants from the cache for
     */
    @TriggersRemove(cacheName = "userGrantsCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public void clearUserGrants(final String userId) {
    }

    /**
     * Removes all stored grants of all users.
     */
    @TriggersRemove(cacheName = "userGrantsCache", removeAll = true)
    public void clearUserGrants() {
    }

    /**
     * Removes all stored grants for the provided group ID from the cache.
     *
     * @param groupId The group ID to remove policies from the cache for
     */
    @TriggersRemove(cacheName = "groupGrantsCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public void clearGroupGrants(final String groupId) {
    }

    /**
     * Removes all stored grants of all groups.
     */
    @TriggersRemove(cacheName = "groupGrantsCache", removeAll = true)
    public void clearGroupGrants() {
    }

    /**
     * Removes all stored user-details from the cache.<br> Has to be called whenever user-data changes.
     *
     * @param handle The handle of the user to remove details from the cache for
     */
    @TriggersRemove(cacheName = "userDetailsCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public void clearUserDetails(final String handle) {
    }

    /**
     * Removes all stored user-details from the cache.
     */
    @TriggersRemove(cacheName = "userDetailsCache", removeAll = true)
    public void clearUserDetails() {
    }

    /**
     * Removes all stored groups of the given user from the cache.
     *
     * @param userId The user ID to remove policies from the cache for
     */
    @TriggersRemove(cacheName = "userGroupsCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public void clearUserGroups(final String userId) {
    }

    /**
     * Removes all stored groups from the cache.
     */
    @TriggersRemove(cacheName = "userGroupsCache", removeAll = true)
    public void clearUserGroups() {
    }

    /**
     * Removes all stored policies for the provided role from the cache.<br> Has to be called whenever new policies come
     * into effect for a specific role.
     *
     * @param idReference The role to remove policies from the cache for
     */
    @TriggersRemove(cacheName = "rolePoliciesCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public void clearRolePolicies(final URI idReference) {
    }

    /**
     * Removes all stored policies from the cache.
     */
    @TriggersRemove(cacheName = "rolePoliciesCache", removeAll = true)
    public void clearRolePolicies() {
    }

    /**
     * Removes stored role with the provided role ID from the cache.
     *
     * @param roleId The role ID to remove.
     */
    @TriggersRemove(cacheName = "rolesCache", keyGenerator = @KeyGenerator(name = "HashCodeCacheKeyGenerator", properties = { @Property(name = "includeMethod", value = "false") }))
    public void clearRoles(final String roleId) {
    }

    /**
     * Removes all stored roles from the cache.
     */
    @TriggersRemove(cacheName = "rolesCache", removeAll = true)
    public void clearRoles() {
    }

}
