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
package de.escidoc.core.aa.business;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.PolicyReference;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.combine.OrderedPermitOverridesPolicyAlg;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.finder.PolicyFinder;

import de.escidoc.core.aa.business.authorisation.Constants;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.aa.business.cache.PoliciesCache;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.ScopeDef;
import de.escidoc.core.aa.business.xacml.XacmlPolicyReference;
import de.escidoc.core.aa.business.xacml.XacmlPolicySet;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.UserContext;

/**
 * Class Proxies PoliciesCache. Contains methods that get User- or Group-Policies, grantedRoles, Roles, userDetails,
 * userGroups, rolePolicies. Contains calls to PoliciesCache to get cached Objects or chche Objects.
 *
 * @author Michael Hoppe
 */
@Service("security.SecurityHelper")
public class SecurityHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityHelper.class);

    @Autowired
    @Qualifier("security.PoliciesCache")
    private PoliciesCache policiesCache;

    private AbstractPolicy defaultPolicies = null;

    /**
     * The pattern to find the position to insert the "-new" marker in attribute ids for new resources.
     */
    private static final Pattern PATTERN_FIND_PLACE_FOR_MARKER =
        Pattern.compile('(' + AttributeIds.RESOURCE_ATTR_PREFIX + "[^:]*:[^:]*)(:{0,1}.*)");

    /**
     * The replacement pattern to insert the "-new" marker at the found position.
     */
    private static final String PATTERN_INSERT_MARKER = "$1" + AttributeIds.MARKER + "$2";

    /**
     * Private constructor to prevent initialization.
     */
    protected SecurityHelper() {
    }

    /**
     * Gets the {@link EvaluationResult} for {@link XacmlFunctionRoleIsGranted} result using the user ID, the role ID
     * and (optional) the resource ID as key. <br> Answers the question if user or group has assigned the given role and
     * if resourceId is in scope of that role (if role has scope-definitions).
     *
     * @param userOrGroupId The user ID or Group ID.
     * @param roleId        The role ID.
     * @param role
     * @param ctx
     * @param resourceId    The resource ID. This may be {@code null}.
     * @return EvaluationResult EvaluationResult for given user- or groupId, roleId and resourceId.
     * @throws Exception
     */
    public EvaluationResult getRoleIsGrantedEvaluationResult(
        final String userOrGroupId, final String roleId, final String resourceId, final EscidocRole role,
        final EvaluationCtx ctx) throws Exception {
        Map<String, Map<String, EvaluationResult>> result =
            policiesCache.getRoleIsGrantedEvaluationResultCached(userOrGroupId);
        if (result == null) {
            result = new HashMap<String, Map<String, EvaluationResult>>();
        }

        if (!result.isEmpty() && result.get(roleId) != null && !result.get(roleId).isEmpty()) {
            if (result.get(roleId).get(null) != null) {
                return result.get(roleId).get(null);
            }
            else if (result.get(roleId).get(resourceId) != null) {
                return result.get(roleId).get(resourceId);
            }
        }

        Map<String, Map<String, List<RoleGrant>>> roleGrants;
        try {
            roleGrants = getUserGrants(userOrGroupId);
        }
        catch (Exception e) {
            roleGrants = getGroupGrants(userOrGroupId);
        }
        // check if role is granted to the user or one of his groups
        final Map<String, List<RoleGrant>> grantsOfRole = roleGrants.get(role.getId());
        if (grantsOfRole == null) {
            // No grant of the role is found, i.e. the role has not been
            // granted to the user. Therefore, false is returned.
            if (result.get(roleId) == null) {
                result.put(roleId, new HashMap<String, EvaluationResult>());
            }
            result.get(roleId).put(null, EvaluationResult.getInstance(false));
            policiesCache.clearRoleIsGranted(userOrGroupId);
            policiesCache.putRoleIsGrantedEvaluationResult(userOrGroupId, result);
            return EvaluationResult.getInstance(false);
        }
        // At least one grant of the role is owned by the user.
        else if (!role.isLimited()) {
            // The role has been granted to the user. As this is an
            // unlimited role, this grant is valid for all objects,
            // therefore true is returned.
            if (result.get(roleId) == null) {
                result.put(roleId, new HashMap<String, EvaluationResult>());
            }
            result.get(roleId).put(null, EvaluationResult.getInstance(true));
            policiesCache.clearRoleIsGranted(userOrGroupId);
            policiesCache.putRoleIsGrantedEvaluationResult(userOrGroupId, result);
            return EvaluationResult.getInstance(true);
        }
        else {
            // The role has been granted to the user. As this is a limited
            // role, further checks have to be performed.

            // Get the object type from the context
            final String objectType =
                FinderModuleHelper.retrieveSingleResourceAttribute(ctx, Constants.URI_OBJECT_TYPE, true);

            if (role.getObjectTypes().contains(objectType)) {
                // role is defined for the object type. Find the related
                // scope definition
                for (final ScopeDef scopeDef : role.getScopeDefs()) {
                    if (scopeDef.getObjectType().equals(objectType)) {
                        // scope definition for the current object type has
                        // been found
                        String scopeDefAttributeId = scopeDef.getAttributeId();
                        if (scopeDefAttributeId == null) {
                            // The role is a limited one, but it is valid
                            // for all objects of the object type.
                            // Therefore, true is returned here, as the role
                            // has been granted to the user.
                            if (FinderModuleHelper.isNewResourceId(resourceId)) {
                                return EvaluationResult.getInstance(true);
                            }
                            if (result.get(roleId) == null) {
                                result.put(roleId, new HashMap<String, EvaluationResult>());
                            }
                            result.get(roleId).put(resourceId, EvaluationResult.getInstance(true));
                            policiesCache.clearRoleIsGranted(userOrGroupId);
                            policiesCache.putRoleIsGrantedEvaluationResult(userOrGroupId, result);
                            return EvaluationResult.getInstance(true);
                        }
                        else {
                            // The role is a limited one and is limited to
                            // objects related to the object identified by
                            // the scope definition's attribute id (for that
                            // the role is granted)

                            // Get the current resource id

                            // Resolve the scope definition's attribute
                            final Set<String> resolvedAttributeValues;
                            if (FinderModuleHelper.isNewResourceId(resourceId)) {
                                final Matcher matcher = PATTERN_FIND_PLACE_FOR_MARKER.matcher(scopeDefAttributeId);
                                if (matcher.find()) {
                                    scopeDefAttributeId = matcher.replaceAll(PATTERN_INSERT_MARKER);
                                }
                                resolvedAttributeValues =
                                    FinderModuleHelper.retrieveMultiResourceAttribute(ctx,
                                        new URI(scopeDefAttributeId), false);

                            }
                            else {
                                // for existing resources, the existing
                                // attribute is resolved
                                resolvedAttributeValues =
                                    FinderModuleHelper.retrieveMultiResourceAttribute(ctx,
                                        new URI(scopeDefAttributeId), false);
                            }

                            // the resolved attribute may be empty, e.g.
                            // in case of a context-id and creation of a new
                            // context. As for a non-existing resource no
                            // role can be granted, false has to be returned
                            // in this case (see issue 529). Otherwise, it
                            // has to be checked if the user has a grant
                            // for the addressed object.
                            if (resolvedAttributeValues != null && !resolvedAttributeValues.isEmpty()) {
                                for (final String resolvedAttributeValue : resolvedAttributeValues) {
                                    final Collection grantsOfRoleAndObject = grantsOfRole.get(resolvedAttributeValue);
                                    if (grantsOfRoleAndObject != null && !grantsOfRoleAndObject.isEmpty()) {
                                        if (FinderModuleHelper.isNewResourceId(resourceId)) {
                                            return EvaluationResult.getInstance(true);
                                        }
                                        if (result.get(roleId) == null) {
                                            result.put(roleId, new HashMap<String, EvaluationResult>());
                                        }
                                        result.get(roleId).put(resourceId, EvaluationResult.getInstance(true));
                                        policiesCache.clearRoleIsGranted(userOrGroupId);
                                        policiesCache.putRoleIsGrantedEvaluationResult(userOrGroupId, result);
                                        return EvaluationResult.getInstance(true);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        // scope definitions for other object types than the
                        // object type of the current resource are skipped.
                        continue;
                    }
                }
            }
            if (FinderModuleHelper.isNewResourceId(resourceId)) {
                return EvaluationResult.getInstance(false);
            }
            if (result.get(roleId) == null) {
                result.put(roleId, new HashMap<String, EvaluationResult>());
            }
            result.get(roleId).put(resourceId, EvaluationResult.getInstance(false));
            policiesCache.clearRoleIsGranted(userOrGroupId);
            policiesCache.putRoleIsGrantedEvaluationResult(userOrGroupId, result);
            return EvaluationResult.getInstance(false);
        }
    }

    /**
     * Gets the the user policy set for the provided user ID.<br>
     * <p/>
     *
     * @param userId The user ID .
     * @param policyFinder
     * @return The {@code XacmlPolicySet} containing the policy set that consists of the user's polices, or
     *         {@code null}.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws java.net.URISyntaxException
     * @throws com.sun.xacml.UnknownIdentifierException
     */
    public XacmlPolicySet getUserPolicies(final String userId, final PolicyFinder policyFinder)
        throws UnknownIdentifierException, URISyntaxException, WebserverSystemException {
        XacmlPolicySet result = policiesCache.getUserPolicies(userId);
        if (result != null) {
            return result;
        }

        final List<AbstractPolicy> policies = new ArrayList<AbstractPolicy>();

        // retrieve user's roles policies
        final XacmlPolicySet rolesPolicySet = retrieveUserRolesPolicies(userId, policyFinder);
        if (rolesPolicySet != null) {
            policies.add(rolesPolicySet);
        }

        // add the default policies
        final AbstractPolicy defPolicies = retrieveDefaultPolicies(policyFinder);
        if (defPolicies != null) {
            policies.add(defPolicies);
        }

        result =
            new XacmlPolicySet("Policies-" + userId,
                XacmlPolicySet.URN_POLICY_COMBINING_ALGORITHM_ORDERED_PERMIT_OVERRIDES, null, policies);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(result.toString());
        }
        policiesCache.clearUserPolicies(userId);
        return policiesCache.putUserPolicies(userId, result);
    }

    /**
     * Gets the the group policy set for the provided group ID.<br>
     * <p/>
     *
     * @param groupId The group ID to use as key for HashMap.
     * @param policyFinder
     * @return The {@code XacmlPolicySet} containing the policy set that consists of the group's polices, or
     *         {@code null}.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws java.net.URISyntaxException
     * @throws com.sun.xacml.UnknownIdentifierException
     */
    public XacmlPolicySet getGroupPolicies(final String groupId, final PolicyFinder policyFinder)
        throws UnknownIdentifierException, URISyntaxException, WebserverSystemException {
        XacmlPolicySet result = policiesCache.getGroupPolicies(groupId);
        if (result != null) {
            return result;
        }
        result = retrieveGroupRolesPolicies(groupId, policyFinder);
        if (result == null) {
            result =
                new XacmlPolicySet("roles-" + groupId,
                    XacmlPolicySet.URN_POLICY_COMBINING_ALGORITHM_ORDERED_PERMIT_OVERRIDES, null,
                    new ArrayList<AbstractPolicy>());
        }
        policiesCache.clearGroupPolicies(groupId);
        return policiesCache.putGroupPolicies(groupId, result);
    }

    /**
     * Gets the the user grants for the provided user ID.<br>
     *
     * @param userId The user ID to use as key for HashMap.
     * @return The grants of the user in a {@code Map}, or {@code null}.
     * @throws de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    public Map<String, Map<String, List<RoleGrant>>> getUserGrants(final String userId)
        throws UserAccountNotFoundException, SystemException {
        return policiesCache.getUserGrants(userId);
    }

    /**
     * Gets the the group grants for the provided group ID.<br>
     *
     * @param groupId The group ID to use as key for HashMap.
     * @return The grants of the group in a {@code Map}, or {@code null}.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    public Map<String, Map<String, List<RoleGrant>>> getGroupGrants(final String groupId)
        throws ResourceNotFoundException, SystemException {
        return policiesCache.getGroupGrants(groupId);
    }

    /**
     * Gets the the user details for the provided handle.<br>
     *
     * @param handle The handle to use as key for HashMap.
     * @return The details of the user as {@code UserDetails}, or {@code null}.
     * @throws de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException
     */
    public UserDetails getUserDetails(final String handle) throws SqlDatabaseSystemException {
        return policiesCache.getUserDetails(handle);
    }

    /**
     * Gets the the user groups for the provided userId.<br>
     *
     * @param userId The userId.
     * @return The groups of the user as {@code Set}, or {@code null}.
     * @throws de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    public Set<String> getUserGroups(final String userId) throws UserAccountNotFoundException, SystemException {
        return policiesCache.getUserGroups(userId);
    }

    /**
     * Gets the policies for the provided role.
     *
     * @param idReference The reference of the role's policies set.
     * @return Returns the {@code PolicyFinderResult} containing the policy set of the addressed role.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    public XacmlPolicySet getRolePolicySet(final URI idReference) throws WebserverSystemException {
        return policiesCache.getRolePolicySet(idReference, policiesCache.getRole(idReference.toString()));
    }

    /**
     * Gets the role for the provided role id.
     *
     * @param roleId The role identifier.
     * @return Returns the {@code EscidocRole} for the provided key.
     */
    public EscidocRole getRole(final String roleId) {
        return policiesCache.getRole(roleId);
    }

    /**
     * Removes all EvaluationResult-Objects (roles granted to a user or group) for the provided user ID from the cache.
     * <br> Has to be called whenever grants of a user change.
     *
     * @param userOrGroupId The user- or group ID to remove policies from the cache for
     */
    public void clearRoleIsGranted(final String userOrGroupId) {
        policiesCache.clearRoleIsGranted(userOrGroupId);
    }

    /**
     * Removes all EvaluationResult-Objects (roles granted to a user or group) from the cache.
     */
    public void clearRoleIsGranted() {
        policiesCache.clearRoleIsGranted();
    }

    /**
     * Removes all stored policies for the provided user ID from the cache.<br> Has to be called whenever new policies
     * come into effect for a specific user (e.g. when a new role has been assigned to this user).
     *
     * @param userId The user ID to remove policies from the cache for
     */
    public void clearUserPolicies(final String userId) {
        policiesCache.clearUserPolicies(userId);
    }

    /**
     * Removes all stored policies from the cache.<br> Has to be called whenever new policies come into effect for a
     * specific user (e.g. when a new role has been assigned to this user).
     */
    public void clearUserPolicies() {
        policiesCache.clearUserPolicies();
    }

    /**
     * Removes all stored policies for the provided group ID from the cache.<br> Has to be called whenever new policies
     * come into effect for a specific group (e.g. when a new role has been assigned to this group).
     *
     * @param groupId The group ID to remove policies from the cache for
     */
    public void clearGroupPolicies(final String groupId) {
        policiesCache.clearGroupPolicies(groupId);
    }

    /**
     * Removes all stored policies from the cache.<br> Has to be called whenever new policies come into effect for a
     * specific group (e.g. when a new role has been assigned to this group).
     */
    public void clearGroupPolicies() {
        policiesCache.clearGroupPolicies();
    }

    /**
     * Removes all stored grants for the provided user ID from the cache.
     *
     * @param userId The user ID to remove grants from the cache for
     */
    public void clearUserGrants(final String userId) {
        policiesCache.clearUserGrants(userId);
    }

    /**
     * Removes all stored grants of all users.
     */
    public void clearUserGrants() {
        policiesCache.clearUserGrants();
    }

    /**
     * Removes all stored grants for the provided group ID from the cache.
     *
     * @param groupId The group ID to remove policies from the cache for
     */
    public void clearGroupGrants(final String groupId) {
        policiesCache.clearGroupGrants(groupId);
    }

    /**
     * Removes all stored grants of all groups.
     */
    public void clearGroupGrants() {
        policiesCache.clearGroupGrants();
    }

    /**
     * Removes all stored user-details from the cache.<br> Has to be called whenever user-data changes.
     *
     * @param handle The handle of the user to remove details from the cache for
     */
    public void clearUserDetails(final String handle) {
        policiesCache.clearUserDetails(handle);
    }

    /**
     * Removes all stored user-details from the cache.
     */
    public void clearUserDetails() {
        policiesCache.clearUserDetails();
    }

    /**
     * Removes all stored groups of the given user from the cache.
     *
     * @param userId The user ID to remove policies from the cache for
     */
    public void clearUserGroups(final String userId) {
        policiesCache.clearUserGroups(userId);
    }

    /**
     * Removes all stored groups from the cache.
     */
    public void clearUserGroups() {
        policiesCache.clearUserGroups();
    }

    /**
     * Removes all stored policies for the provided role from the cache.<br> Has to be called whenever new policies come
     * into effect for a specific role.
     *
     * @param idReference The role to remove policies from the cache for
     */
    public void clearRolePolicies(final URI idReference) {
        policiesCache.clearRolePolicies(idReference);
    }

    /**
     * Removes all stored policies from the cache.
     */
    public void clearRolePolicies() {
        policiesCache.clearRolePolicies();
    }

    /**
     * Removes stored role with the provided role ID from the cache.
     *
     * @param roleId The role ID to remove.
     */
    public void clearRoles(final String roleId) {
        policiesCache.clearRoles(roleId);
    }

    /**
     * Removes all stored roles from the cache.
     */
    public void clearRoles() {
        policiesCache.clearRoles();
    }

    /**
     * Removes all stored policies for the provided user ID from the cache.<br> Has to be called whenever new policies
     * come into effect for a specific user (e.g. when a new role has been assigned to this user).
     *
     * @param userId The user ID to remove policies from the cache for
     */
    public void clearUserPoliciesCaches(final String userId) {
        policiesCache.clearUserPolicies(userId);
        policiesCache.clearRoleIsGranted(userId);
        policiesCache.clearUserGrants(userId);
    }

    /**
     * Removes all stored policies for the provided group ID from the cache.<br> Has to be called whenever new policies
     * come into effect for a specific group (e.g. when a new role has been assigned to this group).
     *
     * @param groupId The group ID to remove policies from the cache for
     */
    public void clearGroupPoliciesCaches(final String groupId) {
        policiesCache.clearGroupPolicies(groupId);
        policiesCache.clearRoleIsGranted(groupId);
        policiesCache.clearGroupGrants(groupId);
    }

    /**
     * Removes all data stored in the cache for the role identified by the provided role id from the cache.<br> Has to
     * be called whenever the role changes or has been deleted.
     *
     * @param roleId The id of the role to remove from the cache.
     * @throws SystemException e
     */
    public void clearRole(final String roleId) throws SystemException {
        try {
            policiesCache.clearRolePolicies(new URI(roleId));
        }
        catch (final URISyntaxException e) {
            throw new SystemException(e);
        }

        // FIXME: roles may be cached by name, not id. As a quick fix, the
        // cache is completely cleared. This should be optimized
        policiesCache.clearRoles();

        // The user policies cache still holds policies for the removed role.
        // To avoid usage of invalidated roles, the caches are cleared.
        policiesCache.clearUserPolicies();

        policiesCache.clearRoleIsGranted();

    }

    /**
     * Removes all stored policies from the cache.
     */
    public void clear() {
        policiesCache.clearRolePolicies();
        policiesCache.clearRoles();
        policiesCache.clearUserPolicies();
        policiesCache.clearRoleIsGranted();
        policiesCache.clearUserGrants();
        policiesCache.clearUserDetails();
        policiesCache.clearGroupPolicies();
        policiesCache.clearGroupGrants();
        policiesCache.clearUserGroups();
    }

    /**
     * Retrieve all policies given to the user by his/her (restricted) roles <br> The policies are returned in a
     * {@code XacmlPolicySet} with the policy combining algorithm set to ordered-permit-overrides.
     *
     * @param userId       The internal id of the user, used to identify the user account.
     * @param policyFinder the policyFinder to use.
     * @return Returns a {@code PolicySet} with the policy combining algorithm set to ordered-permit-overrides or
     *         {@code null}. The policy set is built up by policy references to the role policy sets. If the
     *         provided user id matches the anonymous user, {@code null} is returned.
     * @throws WebserverSystemException In case of an internal error.
     */
    private XacmlPolicySet retrieveUserRolesPolicies(final String userId, final PolicyFinder policyFinder)
        throws WebserverSystemException {

        if (UserContext.isIdOfAnonymousUser(userId)) {
            return null;
        }

        try {
            final Map<String, Map<String, List<RoleGrant>>> roleGrants = getUserGrants(userId);

            if (roleGrants == null || roleGrants.isEmpty()) {
                return null;
            }

            return retrieveRolesPolicies(roleGrants, userId, policyFinder, true);
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * Retrieve all policies given to the user/group by his/her (restricted) roles <br> The policies are returned in a
     * {@code XacmlPolicySet} with the policy combining algorithm set to ordered-permit-overrides.
     *
     * @param roleGrants    map with current grants of the user/group.
     * @param userOrGroupId The internal id of the user/group, used to identify the user account/user group.
     * @param policyFinder  the policyFinder to use.
     * @param isUser        boolean if user-roles are requested
     * @return Returns a {@code PolicySet} with the policy combining algorithm set to ordered-permit-overrides or
     *         {@code null}. The policy set is built up by policy references to the role policy sets. If the
     *         provided user id matches the anonymous user, {@code null} is returned.
     * @throws WebserverSystemException In case of an internal error.
     */
    private XacmlPolicySet retrieveRolesPolicies(
        final Map<String, Map<String, List<RoleGrant>>> roleGrants, final String userOrGroupId,
        final PolicyFinder policyFinder, final boolean isUser) throws WebserverSystemException {

        String userOrGroupIdentifier = "user";
        if (!isUser) {
            userOrGroupIdentifier = "group";
        }
        final List<AbstractPolicy> rolesPolicies = new ArrayList<AbstractPolicy>();
        try {
            for (final Object o : roleGrants.keySet()) {
                final String roleId = (String) o;
                final EscidocRole role = getRole(roleId);
                // The policyId is concatenated String
                // containing <roleName>/<user or group>/<userOrGroupId>
                final URI policySetId =
                    new URI(role.getPolicySetId().toString() + '/' + userOrGroupIdentifier + '/' + userOrGroupId);
                rolesPolicies.add(new XacmlPolicyReference(policySetId, PolicyReference.POLICYSET_REFERENCE,
                    policyFinder));
            }

            if (!rolesPolicies.isEmpty()) {
                return new XacmlPolicySet("roles-" + userOrGroupId, OrderedPermitOverridesPolicyAlg.algId, null,
                    rolesPolicies);
            }

        }
        catch (final Exception e) {
            throw new WebserverSystemException(e);
        }
        return null;
    }

    /**
     * Retrieves the default policies that are granted to every user.<br> The policies are fetched for the dummy role
     * "Default". They are set in the field {@code defaultPolicies}.
     *
     * @param policyFinder the policyFinder to use.
     * @return Returns an {@code XacmlPolicyReference} referencing the set of default policies.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private AbstractPolicy retrieveDefaultPolicies(final PolicyFinder policyFinder) throws WebserverSystemException {

        if (this.defaultPolicies == null) {
            try {
                this.defaultPolicies =
                    new XacmlPolicyReference(new URI(EscidocRole.DEFAULT_USER_ROLE_ID),
                        PolicyReference.POLICYSET_REFERENCE, policyFinder);
            }
            catch (final Exception e) {
                throw new WebserverSystemException(e);
            }
        }
        return this.defaultPolicies;
    }

    /**
     * Retrieve all policies given to the group by their (restricted) roles <br> The policies are returned in a
     * {@code XacmlPolicySet} with the policy combining algorithm set to ordered-permit-overrides.
     *
     * @param groupId      The internal id of the group.
     * @param policyFinder the policyFinder to use.
     * @return Returns a {@code PolicySet} with the policy combining algorithm set to ordered-permit-overrides or
     *         {@code null}. The policy set is built up by policy references to the role policy sets. If the
     *         provided user id matches the anonymous user, {@code null} is returned.
     * @throws WebserverSystemException In case of an internal error.
     */
    private XacmlPolicySet retrieveGroupRolesPolicies(final String groupId, final PolicyFinder policyFinder)
        throws WebserverSystemException {

        try {
            final Map<String, Map<String, List<RoleGrant>>> roleGrants = getGroupGrants(groupId);
            // cache grants for later retrieval during policy evaluation
            XacmlPolicySet policies = null;
            if (roleGrants != null && !roleGrants.isEmpty()) {
                policies = retrieveRolesPolicies(roleGrants, groupId, policyFinder, false);
            }
            return policies;
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e);
        }
    }

}
