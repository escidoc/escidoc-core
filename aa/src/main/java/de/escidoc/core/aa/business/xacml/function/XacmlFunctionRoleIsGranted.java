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
package de.escidoc.core.aa.business.xacml.function;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BooleanAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.cond.FunctionBase;
import de.escidoc.core.aa.business.authorisation.Constants;
import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.aa.business.cache.PoliciesCache;
import de.escidoc.core.aa.business.cache.PoliciesCacheProxy;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface;
import de.escidoc.core.aa.business.persistence.ScopeDef;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of an XACML (target) function that checks if a role has been
 * granted to the current user (for an object).<br>
 * The first parameter holds the role name, the second one the object type of
 * the object that shall be checked. <br>
 * This function returns <code>true</code>,
 * <ul>
 * <li>if the role is the dummy role holding the policies of the default user.
 * <li>if the role is a unlimited role and has been granted to the
 * subject(user), or</li>
 * <li>if the role is a limited role and has been granted for the object
 * identified by the resource-id of the context to the current user (subject).</li>
 * </ul>
 * 
 * @spring.bean id="eSciDoc.core.aa.XacmlFunctionRoleIsGranted"
 * 
 * @author TTE
 * 
 */
public class XacmlFunctionRoleIsGranted extends FunctionBase {

    /**
     * The pattern to find the position to insert the "-new" marker in attribute
     * ids for new resources.
     * 
     * @see PATTERN_INSERT_MARKER
     */
    private static final Pattern PATTERN_FIND_PLACE_FOR_MARKER = Pattern
        .compile('(' + AttributeIds.RESOURCE_ATTR_PREFIX
            + "[^:]*:[^:]*)(:{0,1}.*)");

    /**
     * The replacement pattern to insert the "-new" marker at the found
     * position.
     * 
     * @see PATTERN_FIND_PLACE_FOR_MARKER
     */
    private static final String PATTERN_INSERT_MARKER = "$1"
        + AttributeIds.MARKER + "$2";

    /**
     * The pattern used to check if a role name is the name of the dummy roles
     * holding the default policies.
     */
    private static final Pattern PATTERN_DEFAULT_USER_ROLE_ID = Pattern
        .compile(EscidocRole.DEFAULT_USER_ROLE_ID);

    /** The name of this function. */
    public static final String NAME = AttributeIds.FUNCTION_PREFIX
        + "role-is-granted";

    private PoliciesCacheProxy policiesCacheProxy = null;

    private EscidocRoleDaoInterface roleDao;

    /**
     * The constructor.
     */
    public XacmlFunctionRoleIsGranted() {

        super(NAME, 0, StringAttribute.identifier, false, 2,
            BooleanAttribute.identifier, false);
    }



    /**
     * See Interface for functional description.
     * 
     * @param inputs
     * @param ctx
     * @return
     * @see com.sun.xacml.cond.Function#evaluate(java.util.List,
     *      com.sun.xacml.EvaluationCtx)
     */
    @Override
    public EvaluationResult evaluate(final List inputs, final EvaluationCtx ctx) {

        try {
            final AttributeValue[] argValues =
                new AttributeValue[inputs.size()];
            final EvaluationResult result = evalArgs(inputs, ctx, argValues);
            if (result != null) {
                return result;
            }
            // Get the role name from the input and check if it is the dummy
            // role for default policies
            // The policyId is concatenated String
            // containing <roleName>/<user or group>/<userorGroupId>
            final String policyId = ((StringAttribute) (argValues[0])).getValue();
            final String[] parts = policyId.split("/");
            final StringBuilder roleIdentifier = new StringBuilder("");
            if (parts.length > 2) {
                for (int i = 0; i < parts.length - 2; i++) {
                    roleIdentifier.append(parts[i]);
                }
            }
            else {
                roleIdentifier.append(policyId);
            }
            final String roleId = roleIdentifier.toString();
            if (PATTERN_DEFAULT_USER_ROLE_ID.matcher(roleId).find()) {
                return EvaluationResult.getInstance(true);
            }
            // Get the userOrGroupid from the policyId
            final String userOrGroupId;
            userOrGroupId = parts.length > 2 ? parts[parts.length - 1] : FinderModuleHelper.retrieveSingleSubjectAttribute(ctx,
                    Constants.URI_SUBJECT_ID, true);

            // Get the resource id from the context
            final String resourceId = FinderModuleHelper.getResourceId(ctx);

            // Fetch the role identified by the role name
            // FIXME: maybe it is better to use the role ids in the policies
            // instead of the names?
            EscidocRole role = PoliciesCache.getRole(roleId);
            if (role == null) {
                role = getRoleDao().retrieveRole(roleId);
                role.isLimited();
                PoliciesCache.putRole(role.getId(), role);
            }

            return getUserGroupEvaluationResult(userOrGroupId, resourceId,
                role, ctx);

        }
        catch (final ResourceNotFoundException e) {
            return CustomEvaluationResultBuilder
                .createResourceNotFoundResult(e);
        }
        catch (final Exception e) {
            return CustomEvaluationResultBuilder.createProcessingErrorResult(e);
        }
    }

    /**
     * Tries to fetch an {@link EvaluationResult} for the provided values of a
     * user.
     * 
     * @param userOrGroupId
     *            The id of the user account for that the result shall be
     *            determined. This value must not be <code>null</code>.
     * @param resourceId
     *            The id of the resource for that the result shall be
     *            determined. This may be <code>null</code>.
     * @param role
     *            The role for that the result shall be determined. This value
     *            must not be <code>null</code>.
     * @param ctx
     *            The EvaluationCtx.
     * @return Returns the {@link EvaluationResult} found in the cache or
     *         <code>null</code>.
     */
    private EvaluationResult getUserGroupEvaluationResult(
        final String userOrGroupId, final String resourceId,
        final EscidocRole role, final EvaluationCtx ctx) {
        try {
            // try to find result in cache
            final EvaluationResult result =
                fetchFromCache(userOrGroupId, role.getId(), resourceId);
            if (result != null) {
                // TODO: one problem exists. The cached result can be invalid,
                // because the addressed resource does not exists anymore, or
                // the cached result belongs to a resource of another object
                // type.
                // But both situations should be detected by the business logic
                // or by further attribute resolving and the
                // ResourceNotFoundException will be thrown, then. Therefore, it
                // is not checked, here.
                return result;
            }

            // No result found in cache, needs to be evaluated
            // try getting role grants of the user
            Map roleGrants = policiesCacheProxy.getUserGrants(userOrGroupId);
            if (roleGrants == null) {
                // try getting roleGrants for group
                roleGrants = policiesCacheProxy.getGroupGrants(userOrGroupId);
            }

            // check if role is granted to the user or one of his groups
            final Map grantsOfRole = (Map) roleGrants.get(role.getId());
            if (grantsOfRole == null) {
                // No grant of the role is found, i.e. the role has not been
                // granted to the user. Therefore, false is returned.
                return createCachedResult(userOrGroupId, role.getId(), null,
                    false);
            }
            // At least one grant of the role is owned by the user.
            else if (!role.isLimited()) {
                // The role has been granted to the user. As this is an
                // unlimited role, this grant is valid for all objects,
                // therefore true is returned.
                return createCachedResult(userOrGroupId, role.getId(), null,
                    true);
            }
            else {
                // The role has been granted to the user. As this is a limited
                // role, further checks have to be performed.

                // Get the object type from the context
                final String objectType =
                    FinderModuleHelper.retrieveSingleResourceAttribute(ctx,
                        Constants.URI_OBJECT_TYPE, true);

                if (role.getObjectTypes().contains(objectType)) {
                    // role is defined for the object type. Find the related
                    // scope definition
                    for (final ScopeDef scopeDef : role.getScopeDefs()) {
                        if (scopeDef.getObjectType().equals(objectType)) {
                            // scope definition for the current object type has
                            // been found
                            String scopeDefAttributeId =
                                    scopeDef.getAttributeId();
                            if (scopeDefAttributeId == null) {
                                // The role is a limited one, but it is valid
                                // for all objects of the object type.
                                // Therefore, true is returned here, as the role
                                // has been granted to the user.
                                return createCachedResult(userOrGroupId,
                                        role.getId(), resourceId, true);
                            } else {
                                // The role is a limited one and is limited to
                                // objects related to the object identified by
                                // the scope definition's attribute id (for that
                                // the role is granted)

                                // Get the current resource id

                                // Resolve the scope definition's attribute
                                final Set<String> resolvedAttributeValues;
                                if (FinderModuleHelper
                                        .isNewResourceId(resourceId)) {
                                    final Matcher matcher =
                                            PATTERN_FIND_PLACE_FOR_MARKER
                                                    .matcher(scopeDefAttributeId);
                                    if (matcher.find()) {
                                        scopeDefAttributeId =
                                                matcher
                                                        .replaceAll(PATTERN_INSERT_MARKER);
                                    }
                                    resolvedAttributeValues =
                                            FinderModuleHelper
                                                    .retrieveMultiResourceAttribute(
                                                            ctx, new URI(
                                                                    scopeDefAttributeId), false);

                                } else {
                                    // for existing resources, the existing
                                    // attribute is resolved
                                    resolvedAttributeValues =
                                            FinderModuleHelper
                                                    .retrieveMultiResourceAttribute(
                                                            ctx, new URI(
                                                                    scopeDefAttributeId), false);
                                }

                                // the resolved attribute may be empty, e.g.
                                // in case of a context-id and creation of a new
                                // context. As for a non-existing resource no
                                // role can be granted, false has to be returned
                                // in this case (see issue 529). Otherwise, it
                                // has to be checked if the user has a grant
                                // for the addressed object.
                                if (resolvedAttributeValues != null
                                        && !resolvedAttributeValues.isEmpty()) {
                                    for (final String resolvedAttributeValue : resolvedAttributeValues) {
                                        final Collection grantsOfRoleAndObject =
                                                (List) grantsOfRole
                                                        .get(resolvedAttributeValue);
                                        if (grantsOfRoleAndObject != null
                                                && !grantsOfRoleAndObject.isEmpty()) {
                                            return createCachedResult(
                                                    userOrGroupId, role.getId(),
                                                    resourceId, true);
                                        }
                                    }
                                }
                            }
                        } else {
                            // scope definitions for other object types than the
                            // object type of the current resource are skipped.
                            continue;
                        }
                    }
                }
                return createCachedResult(userOrGroupId, role.getId(),
                    resourceId, false);
            }
        }
        catch (final ResourceNotFoundException e) {
            return CustomEvaluationResultBuilder
                .createResourceNotFoundResult(e);
        }
        catch (final Exception e) {
            return CustomEvaluationResultBuilder.createProcessingErrorResult(e);
        }

    }

    /**
     * Tries to fetch an {@link EvaluationResult} for the provided values from
     * the {@link PoliciesCache}.
     * 
     * @param userId
     *            The id of the user account for that the result shall be
     *            determined. This value must not be <code>null</code>.
     * @param roleId
     *            The id of the role for that the result shall be determined.
     *            This value must not be <code>null</code>.
     * @param resourceId
     *            The id of the resource for that the result shall be
     *            determined. This may be <code>null</code>.
     * @return Returns the {@link EvaluationResult} found in the cache or
     *         <code>null</code>.
     */
    private EvaluationResult fetchFromCache(
        final String userId, final String roleId, final String resourceId) {

        // try to get a result for unlimited role or role not granted to the
        // user (resource-id = null)
        EvaluationResult result =
            PoliciesCache
                .getRoleIsGrantedEvaluationResult(userId, roleId, null);
        if (result == null) {
            // try to get a result for limited role
            result =
                PoliciesCache.getRoleIsGrantedEvaluationResult(userId, roleId,
                    resourceId);
        }
        return result;
    }

    /**
     * Creates and caches an {@link EvaluationResult} for the provided values.<br>
     * The result is only cached if the resource id is not the id of a new
     * resource that shall be created, as this would lead to fetching wrong
     * results from the cache for the next new resource.
     * 
     * @param userId
     *            The id of the user account for that the result has been
     *            determined. This value must not be <code>null</code>.
     * @param roleId
     *            The id of the role for that the result has been determined.
     *            This value must not be <code>null</code>.
     * @param resourceId
     *            The id of the resource for that the result has been
     *            determined. This may be <code>null</code>.
     * @param roleIsGranted
     *            Flag indicating if the role has been granted to the user
     *            (optional: for the provided resource).
     */
    private EvaluationResult createCachedResult(
        final String userId, final String roleId, final String resourceId,
        final boolean roleIsGranted) {

        final EvaluationResult result =
            EvaluationResult.getInstance(roleIsGranted);
        if (!FinderModuleHelper.isNewResourceId(resourceId)) {
            PoliciesCache.putRoleIsGrantedEvaluationResult(userId, roleId,
                resourceId, result);
        }
        return result;
    }

    /**
     * Gets the data access object bean used to access role data from the
     * database.<br>
     */
    private EscidocRoleDaoInterface getRoleDao() {

        return roleDao;
    }

    /**
     * Injects the role dao.
     * 
     * @param roleDao
     *            the {@link EscidocRoleDaoInterface} implementation to inject.
     * 
     * @spring.property ref="persistence.EscidocRoleDao"
     */
    public void setRoleDao(final EscidocRoleDaoInterface roleDao) {
        this.roleDao = roleDao;
    }

    /**
     * Injects the policies cache proxy.
     * 
     * @param policiesCacheProxy
     *            the {@link PoliciesCacheProxy} to inject.
     * 
     * @spring.property ref="resource.PoliciesCacheProxy"
     */
    public void setPoliciesCacheProxy(
        final PoliciesCacheProxy policiesCacheProxy) {
        this.policiesCacheProxy = policiesCacheProxy;
    }
}
