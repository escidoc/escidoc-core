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
import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.aa.business.persistence.UserGroup;
import de.escidoc.core.aa.business.persistence.UserGroupDaoInterface;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of an XACML attribute finder module that is responsible for the attributes related to an
 * user-group.<br> This finder module supports XACML resource attributes.<br> The attribute values are fetched from the
 * xml representation of the user-group.
 * <p/>
 * Supported Attributes:<br> -info:escidoc/names:aa:1.0:resource:user-group:created-by<br> the id of the user who
 * created the user-group, single value attribute -info:escidoc/names:aa:1.0:resource:user-group:modified-by<br> the id
 * of the user who last modified the user-group, single value attribute -info:escidoc/names:aa:1.0:resource:user-group:id<br>
 * the id of the user-group, single value attribute -info:escidoc/names:aa:1.0:resource:user-group:name<br> the name of
 * the user-group, single value attribute
 *
 * @author Michael Hoppe
 */
@Service("eSciDoc.core.aa.UserGroupAttributeFinderModule")
public class UserGroupAttributeFinderModule extends AbstractAttributeFinderModule {

    private static final String ATTR_CREATED_BY = "created-by";

    private static final String ATTR_MODIFIED_BY = "modified-by";

    private static final String ATTR_ID = "id";

    private static final String ATTR_NAME = "name";

    private static final String RESOLVABLE_USER_GROUP_ATTRS =
        ATTR_CREATED_BY + '|' + ATTR_MODIFIED_BY + '|' + ATTR_ID + '|' + ATTR_NAME;

    private static final Pattern PATTERN_USER_GROUP_ATTRIBUTE_PREFIX =
        Pattern.compile(AttributeIds.USER_GROUP_ATTR_PREFIX);

    private static final Pattern PATTERN_PARSE_USER_GROUP_ATTRIBUTE_ID =
        Pattern.compile("((" + AttributeIds.USER_GROUP_ATTR_PREFIX + ")(" + RESOLVABLE_USER_GROUP_ATTRS
            + "))(-new){0,1}(:(.*)){0,1}");

    @Autowired
    @Qualifier("persistence.UserGroupDao")
    private UserGroupDaoInterface userGroupDao;

    /**
     * Private constructor to prevent initialization.
     */
    protected UserGroupAttributeFinderModule() {
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
        // user-group attributes
        return PATTERN_USER_GROUP_ATTRIBUTE_PREFIX.matcher(attributeIdValue).find();

    }

    /**
     * See Interface for functional description.
     */
    @Override
    protected Object[] resolveLocalPart(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber) throws UserGroupNotFoundException, WebserverSystemException {

        EvaluationResult result;
        final String resolvedAttributeIdValue;

        final Matcher userGroupAttributeMatcher = PATTERN_PARSE_USER_GROUP_ATTRIBUTE_ID.matcher(attributeIdValue);
        if (userGroupAttributeMatcher.find()) {
            // -new attribute is not resolvable
            if (userGroupAttributeMatcher.group(4) != null) {
                return null;
            }
            resolvedAttributeIdValue = userGroupAttributeMatcher.group(1);
            final String attributeId = userGroupAttributeMatcher.group(3);

            final String userGroupId = FinderModuleHelper.getResourceId(ctx);
            if (FinderModuleHelper.isNewResourceId(userGroupId)) {
                return null;
            }

            // ask cache for previously cached results
            result =
                (EvaluationResult) getFromCache(resourceId, resourceObjid, resourceVersionNumber, attributeIdValue, ctx);

            if (result == null) {
                final UserGroup userGroup = retrieveUserGroup(ctx, userGroupId);
                if (ATTR_CREATED_BY.equals(attributeId)) {
                    result =
                        CustomEvaluationResultBuilder.createSingleStringValueResult(userGroup.getCreatorId().getId());
                }
                else if (ATTR_MODIFIED_BY.equals(attributeId)) {
                    result =
                        CustomEvaluationResultBuilder
                            .createSingleStringValueResult(userGroup.getModifiedById().getId());
                }
                else if (ATTR_ID.equals(attributeId)) {
                    result = CustomEvaluationResultBuilder.createSingleStringValueResult(userGroup.getId());
                }
                else if (ATTR_NAME.equals(attributeId)) {
                    result = CustomEvaluationResultBuilder.createSingleStringValueResult(userGroup.getName());
                }
                else {
                    return null;
                }
            }

        }
        else {
            return null;
        }

        putInCache(resourceId, resourceObjid, resourceVersionNumber, resolvedAttributeIdValue, ctx, result);
        return new Object[] { result, resolvedAttributeIdValue };
    }

    /**
     * Asserts that the user group is provided, i.e. it is not {@code null} .
     *
     * @param userGroupId The userGroup id for which the account should be provided (should exist).
     * @param userGroup   The user group to assert.
     * @throws UserGroupNotFoundException Thrown if assertion fails.
     */
    private static void assertUserGroup(final String userGroupId, final UserGroup userGroup)
        throws UserGroupNotFoundException {

        if (userGroup == null) {
            throw new UserGroupNotFoundException(StringUtility.format("Group with provided id does not exist",
                userGroupId));
        }
    }

    /**
     * Retrieve User Group from the system.
     *
     * @param ctx         The evaluation context, which will be used as key for the cache.
     * @param userGroupId The user group id.
     * @return Returns the {@code UserGroup} identified by the provided id.
     * @throws WebserverSystemException   Thrown in case of an internal error.
     * @throws UserGroupNotFoundException Thrown if no user account with provided id exists.
     */
    private UserGroup retrieveUserGroup(final EvaluationCtx ctx, final String userGroupId)
        throws WebserverSystemException, UserGroupNotFoundException {

        UserGroup userGroup = (UserGroup) getFromCache(XmlUtility.NAME_ID, null, null, userGroupId, ctx);
        if (userGroup == null) {
            try {
                userGroup = userGroupDao.retrieveUserGroup(userGroupId);
            }
            catch (final Exception e) {
                throw new WebserverSystemException(StringUtility.format("Exception during retrieval of the user group",
                    e.getMessage()), e);
            }
        }

        assertUserGroup(userGroupId, userGroup);

        putInCache(XmlUtility.NAME_ID, null, null, userGroupId, ctx, userGroup);
        return userGroup;
    }

}
