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
package de.escidoc.core.aa.business.xacml.finder;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.cond.EvaluationResult;
import de.escidoc.core.aa.business.authorisation.Constants;
import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserAccountDaoInterface;
import de.escidoc.core.aa.business.persistence.UserGroupDaoInterface;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.notfound.GrantNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of an XACML attribute finder module that is responsible for the attributes related to an grant.<br>
 * This finder module supports XACML resource attributes.<br> The attribute values are fetched from the xml
 * representation of the grant.<br>
 * <p/>
 * Supported Attributes:<br> -info:escidoc/names:aa:1.0:resource:user-account:grant:created-by<br> the id of the user
 * who created the grant, single value attribute -info:escidoc/names:aa:1.0:resource:user-account:grant:assigned-on<br>
 * the id of the object the grant is assigned on (scope of the grant), single value attribute
 * -info:escidoc/names:aa:1.0:resource:user-account:grant:assigned-on:created-by<br> the id of the user who created the
 * object the grant is assigned on, single value attribute -info:escidoc/names:aa:1.0:resource:user-account:grant:assigned-on:context<br>
 * the context-id of the object the grant is assigned on, single value attribute -info:escidoc/names:aa:1.0:resource:user-account:grant:role<br>
 * the role-id of the grant, single value attribute -info:escidoc/names:aa:1.0:resource:user-group:grant:created-by<br>
 * the id of the user who created the group-grant, single value attribute -info:escidoc/names:aa:1.0:resource:user-group:grant:assigned-on<br>
 * the id of the object the group-grant is assigned on, single value attribute -info:escidoc/names:aa:1.0:resource:user-group:grant:assigned-on:created-by<br>
 * the id of the user who created the object the group-grant is assigned on, single value attribute
 * -info:escidoc/names:aa:1.0:resource:user-group:grant:assigned-on:context<br> the context-id of the object the
 * group-grant is assigned on, single value attribute -info:escidoc/names:aa:1.0:resource:user-group:grant:role<br> the
 * role-id of the grant, single value attribute
 * <p/>
 * assigned-on:created-by only resolves if grant is assigned to a component, container, item or context.<br>
 * <p/>
 * assigned-on:context only resolves if grant is assigned to a component, container or item.
 *
 * @author Michael Hoppe
 */
@Service("eSciDoc.core.aa.GrantAttributeFinderModule")
public class GrantAttributeFinderModule extends AbstractAttributeFinderModule {

    private static final String ATTR_ASSIGNED_ON = "assigned-on";

    private static final String ATTR_CREATED_BY = "created-by";

    private static final String ATTR_ROLE = "role";

    private static final String RESOLVABLE_GRANT_ATTRS = ATTR_ASSIGNED_ON + '|' + ATTR_CREATED_BY + '|' + ATTR_ROLE;

    private static final Pattern PATTERN_GRANT_ATTRIBUTE_PREFIX =
        Pattern.compile(AttributeIds.USER_ACCOUNT_GRANT_ATTR_PREFIX + '|' + AttributeIds.USER_GROUP_GRANT_ATTR_PREFIX);

    private static final Pattern PATTERN_PARSE_GRANT_ATTRIBUTE_ID =
        Pattern.compile("((" + AttributeIds.USER_ACCOUNT_GRANT_ATTR_PREFIX + '|'
            + AttributeIds.USER_GROUP_GRANT_ATTR_PREFIX + ")(" + RESOLVABLE_GRANT_ATTRS + "))(-new){0,1}(:(.*)){0,1}");

    @Autowired
    @Qualifier("persistence.UserAccountDao")
    private UserAccountDaoInterface userAccountDao;

    @Autowired
    @Qualifier("persistence.UserGroupDao")
    private UserGroupDaoInterface userGroupDao;

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tsu;

    /**
     * Private constructor to prevent initialization.
     */
    protected GrantAttributeFinderModule() {
    }

    /**
     * See Interface for functional description.
     */
    @Override
    protected boolean assertAttribute(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber, final int designatorType) throws EscidocException {

        // make sure this is an Resource attribute
        if (designatorType != AttributeDesignator.RESOURCE_TARGET) {
            return false;
        }
        // make sure attribute is in escidoc-internal format for
        // grant attributes
        return PATTERN_GRANT_ATTRIBUTE_PREFIX.matcher(attributeIdValue).find();

    }

    /**
     * See Interface for functional description.
     */
    @Override
    protected Object[] resolveLocalPart(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber) throws SystemException, ResourceNotFoundException {

        final EvaluationResult result;
        final String resolvedAttributeIdValue;

        final Matcher grantAttributeMatcher = PATTERN_PARSE_GRANT_ATTRIBUTE_ID.matcher(attributeIdValue);
        if (grantAttributeMatcher.find()) {
            // -new attribute is not resolvable
            if (grantAttributeMatcher.group(4) != null) {
                return null;
            }
            final String resolvableAttribute = grantAttributeMatcher.group(1);
            final String attributeId = grantAttributeMatcher.group(3);
            final String tail = grantAttributeMatcher.group(6);

            final Object[] returnArr;
            if (ATTR_ASSIGNED_ON.equals(attributeId)) {
                returnArr = resolveAssignedOnAttribute(ctx, attributeIdValue, resolvableAttribute, tail);
            }
            else if (ATTR_CREATED_BY.equals(attributeId)) {
                returnArr = resolveCreatedByAttribute(ctx, resolvableAttribute);
            }
            else if (ATTR_ROLE.equals(attributeId)) {
                returnArr = resolveRoleAttribute(ctx, resolvableAttribute);
            }
            else {
                return null;
            }
            result = (EvaluationResult) returnArr[0];
            resolvedAttributeIdValue = (String) returnArr[1];

        }
        else {
            return null;
        }

        return new Object[] { result, resolvedAttributeIdValue };
    }

    /**
     * resolve attribute assigned-on. check if tail is present and resolvable (dependent on variable
     * SUPPORTED_ASSIGNED_ON_OBJECT_ATTRIBUTES). if tail is not resolvable, mark whole attribute as unresolvable.
     *
     * @param ctx                 EvaluationContext
     * @param attributeIdValue    whole attribute
     * @param resolvableAttribute resolvable part of attribute
     * @param tail                tail after resolvable part
     * @return Object[] result
     * @throws de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     */
    private Object[] resolveAssignedOnAttribute(
        final EvaluationCtx ctx, final String attributeIdValue, final String resolvableAttribute, final String tail)
        throws SystemException, ResourceNotFoundException {
        EvaluationResult result;
        final String userOrGroupId =
            FinderModuleHelper.retrieveSingleResourceAttribute(ctx, Constants.URI_RESOURCE_ID, true);
        final String grantId =
            FinderModuleHelper.retrieveSingleResourceAttribute(ctx, Constants.URI_SUBRESOURCE_ID, true);
        String assignedOnObjectId;
        if (grantId == null || grantId.length() == 0) {
            // if no grantId is present
            // fetch grant-attribute from invocation-mapping
            try {
                assignedOnObjectId = fetchSingleResourceAttribute(ctx, resolvableAttribute + "-new");
            }
            catch (final Exception e) {
                // not assigned to an object
                // so mark complete attribute as unresolvable
                result =
                    CustomEvaluationResultBuilder
                        .createSingleStringValueResult(de.escidoc.core.common.business.Constants.UNRESOLVED_ATTRIBUTE_VALUE);
                return new Object[] { result, attributeIdValue };
            }
        }
        else {
            final RoleGrant grant =
                resolvableAttribute.matches(".*" + XmlUtility.NAME_USER_ACCOUNT + ".*") ? getUserAccountGrant(ctx,
                    userOrGroupId, grantId) : getUserGroupGrant(ctx, grantId);
            assertGrant(grantId, grant);
            assignedOnObjectId = grant.getObjectId();
        }
        if (assignedOnObjectId == null) {
            // not assigned on an object
            // so mark complete attribute as unresolvable
            result =
                CustomEvaluationResultBuilder
                    .createSingleStringValueResult(de.escidoc.core.common.business.Constants.UNRESOLVED_ATTRIBUTE_VALUE);
            return new Object[] { result, attributeIdValue };
        }

        // check if tailing attribute is resolvable for assigned object-type
        if (tail != null) {
            final String objectType = fetchObjectType(ctx, assignedOnObjectId);
            if (objectType.equals(XmlUtility.NAME_COMPONENT) && tail.equals(XmlUtility.NAME_CONTEXT)) {
                // if we have to resolve the context of a component,
                // we first have to get the itemId and resolve context for
                // the itemId
                final List<String> itemIds =
                    FinderModuleHelper.retrieveFromTripleStore(true, tsu.getRetrieveWhereClause(true,
                        TripleStoreUtility.PROP_COMPONENT, assignedOnObjectId, null, null, null), assignedOnObjectId,
                        TripleStoreUtility.PROP_COMPONENT, this.tsu);
                if (itemIds == null || itemIds.isEmpty() || itemIds.size() != 1) {
                    result =
                        CustomEvaluationResultBuilder.createResourceNotFoundResult(new ItemNotFoundException(
                            "item for component " + assignedOnObjectId + " not found"));
                }
                else {
                    assignedOnObjectId = itemIds.get(0);
                }
            }
        }
        result = CustomEvaluationResultBuilder.createSingleStringValueResult(assignedOnObjectId);
        return new Object[] { result, resolvableAttribute };
    }

    /**
     * resolve attribute created-by.
     *
     * @param ctx                 EvaluationContext
     * @param resolvableAttribute resolvable part of attribute
     * @return Object[] result
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException
     */
    private Object[] resolveCreatedByAttribute(final EvaluationCtx ctx, final String resolvableAttribute)
        throws SqlDatabaseSystemException, ResourceNotFoundException, WebserverSystemException {
        final String userOrGroupId =
            FinderModuleHelper.retrieveSingleResourceAttribute(ctx, Constants.URI_RESOURCE_ID, true);
        final String grantId =
            FinderModuleHelper.retrieveSingleResourceAttribute(ctx, Constants.URI_SUBRESOURCE_ID, true);
        if (grantId == null || grantId.length() == 0) {
            throw new GrantNotFoundException("no grantId found");
        }
        final RoleGrant grant =
            resolvableAttribute.matches(".*" + XmlUtility.NAME_USER_ACCOUNT + ".*") ? userAccountDao.retrieveGrant(
                userOrGroupId, grantId) : userGroupDao.retrieveGrant(grantId);
        assertGrant(grantId, grant);
        final String createdBy = grant.getCreatorId();

        final EvaluationResult result = CustomEvaluationResultBuilder.createSingleStringValueResult(createdBy);
        return new Object[] { result, resolvableAttribute };
    }

    /**
     * resolve attribute role.
     *
     * @param ctx                 EvaluationContext
     * @param resolvableAttribute resolvable part of attribute
     * @return Object[] result
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException
     */
    private Object[] resolveRoleAttribute(final EvaluationCtx ctx, final String resolvableAttribute)
        throws WebserverSystemException, ResourceNotFoundException {
        final String userOrGroupId =
            FinderModuleHelper.retrieveSingleResourceAttribute(ctx, Constants.URI_RESOURCE_ID, true);
        final String grantId =
            FinderModuleHelper.retrieveSingleResourceAttribute(ctx, Constants.URI_SUBRESOURCE_ID, true);
        final String roleId;
        if (grantId == null || grantId.length() == 0) {
            // if no grantId is present
            // fetch grant-attribute from invocation-mapping
            roleId = fetchSingleResourceAttribute(ctx, resolvableAttribute + "-new");
        }
        else {
            final RoleGrant grant =
                resolvableAttribute.matches(".*" + XmlUtility.NAME_USER_ACCOUNT + ".*") ? getUserAccountGrant(ctx,
                    userOrGroupId, grantId) : getUserGroupGrant(ctx, grantId);
            assertGrant(grantId, grant);
            roleId = grant.getRoleId();
        }

        final EvaluationResult result = CustomEvaluationResultBuilder.createSingleStringValueResult(roleId);
        return new Object[] { result, resolvableAttribute };
    }

    /**
     * Retrieve user-group grant from the system.
     *
     * @param ctx     The evaluation context, which will be used as key for the cache.
     * @param grantId The grant id.
     * @return Returns the {@code RoleGrant} identified by the provided id.
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws GrantNotFoundException   Thrown if no grant with provided id exists.
     */
    private RoleGrant getUserGroupGrant(final EvaluationCtx ctx, final String grantId) throws WebserverSystemException,
        GrantNotFoundException {
        RoleGrant grant = (RoleGrant) getFromCache(XmlUtility.NAME_ID, null, null, grantId, ctx);
        if (grant == null) {
            try {
                grant = userGroupDao.retrieveGrant(grantId);
            }
            catch (final Exception e) {
                throw new WebserverSystemException(StringUtility.format("Exception during retrieval of the grant", e
                    .getMessage()), e);
            }
        }
        assertGrant(grantId, grant);

        putInCache(XmlUtility.NAME_ID, null, null, grantId, ctx, grant);
        return grant;
    }

    /**
     * Retrieve user-account grant from the system.
     *
     * @param ctx     The evaluation context, which will be used as key for the cache.
     * @param userId  The user id.
     * @param grantId The grant id.
     * @return Returns the {@code RoleGrant} identified by the provided id.
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws GrantNotFoundException   Thrown if no grant with provided id exists.
     */
    private RoleGrant getUserAccountGrant(final EvaluationCtx ctx, final String userId, final String grantId)
        throws WebserverSystemException, GrantNotFoundException {
        RoleGrant grant = (RoleGrant) getFromCache(XmlUtility.NAME_ID, null, null, grantId, ctx);
        if (grant == null) {
            try {
                grant = userAccountDao.retrieveGrant(userId, grantId);
            }
            catch (final Exception e) {
                throw new WebserverSystemException(StringUtility.format("Exception during retrieval of the grant", e
                    .getMessage()), e);
            }
        }
        assertGrant(grantId, grant);

        putInCache(XmlUtility.NAME_ID, null, null, grantId, ctx, grant);
        return grant;
    }

    /**
     * Asserts that the grant is provided, i.e. it is not {@code null}.
     *
     * @param grantId   The grant id for which the grant should be provided (should exist).
     * @param roleGrant The role grant to assert.
     * @throws GrantNotFoundException Thrown if assertion fails.
     */
    private static void assertGrant(final String grantId, final RoleGrant roleGrant) throws GrantNotFoundException {

        if (roleGrant == null) {
            throw new GrantNotFoundException(StringUtility.format("Grant with provided id does not exist", grantId));
        }
    }
}
