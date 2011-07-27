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
import com.sun.xacml.cond.EvaluationResult;
import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of an XACML attribute finder module that is responsible for the attributes related to an eSciDoc
 * role.<br> This finder module supports XACML resource attributes.<br> The attribute values are fetched from the xml
 * representation of the user account.
 * <p/>
 * Supported Attributes:<br> -info:escidoc/names:aa:1.0:resource:role:id<br> the id of the role, single value attribute
 * -info:escidoc/names:aa:1.0:resource:role:created-by<br> the id of the user who created the role, single value
 * attribute -info:escidoc/names:aa:1.0:resource:role:modified-by<br> the id of the user who last modified the role,
 * single value attribute -info:escidoc/names:aa:1.0:resource:role:name<br> the name of the role, single value
 * attribute
 * <p/>
 * created-by and modified-by can have tailing string with user-account-attributes.
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.RoleAttributeFinderModule")
public class RoleAttributeFinderModule extends AbstractAttributeFinderModule {

    /**
     * Pattern used to parse the attribute id and fetch the resolveable part, the last part of the resolveable part and
     * the tail.
     */
    private static final String ROLE_ATTRS = "(id|created-by|modified-by|name)";

    private static final Pattern PATTERN_PARSE_ROLE_ATTRIBUTE_ID =
        Pattern.compile('(' + AttributeIds.ROLE_ATTR_PREFIX + ROLE_ATTRS + ")(:.*){0,1}");

    /**
     * This attribute matches the id of the role.
     */
    private static final String ATTR_ROLE_ID = AttributeIds.ROLE_ATTR_PREFIX + "id";

    /**
     * This attribute matches the user who created the role.
     */
    private static final String ATTR_ROLE_CREATED_BY = AttributeIds.ROLE_ATTR_PREFIX + "created-by";

    /**
     * This attribute matches the user who modified the role.
     */
    private static final String ATTR_ROLE_MODIFIED_BY = AttributeIds.ROLE_ATTR_PREFIX + "modified-by";

    /**
     * This attribute matches the name of the role.
     */
    private static final String ATTR_ROLE_NAME = AttributeIds.ROLE_ATTR_PREFIX + "name";

    @Autowired
    @Qualifier("persistence.EscidocRoleDao")
    private EscidocRoleDaoInterface roleDao;

    /**
     * Private constructor to prevent initialization.
     */
    protected RoleAttributeFinderModule() {
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    protected boolean assertAttribute(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber, final int designatorType) throws EscidocException {

        // make sure attribute id is escidoc internal format and not empty
        // as this finder cannot resolve attributes of new resources.
        if (!super.assertAttribute(attributeIdValue, ctx, resourceId, resourceObjid, resourceVersionNumber,
            designatorType)
            || FinderModuleHelper.isNewResourceId(resourceId)) {

            return false;
        }

        // make sure attribute is in escidoc internal format for role attribute
        return PATTERN_PARSE_ROLE_ATTRIBUTE_ID.matcher(attributeIdValue).find();

    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    protected Object[] resolveLocalPart(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber) throws RoleNotFoundException, WebserverSystemException {

        EvaluationResult result = null;
        final String resolvedAttributeIdValue;
        final Matcher roleAttributeMatcher = PATTERN_PARSE_ROLE_ATTRIBUTE_ID.matcher(attributeIdValue);
        if (roleAttributeMatcher.find()) {
            resolvedAttributeIdValue = roleAttributeMatcher.group(1);
            final EscidocRole role = retrieveRole(ctx, resourceId);
            if (resolvedAttributeIdValue.equals(ATTR_ROLE_ID)) {
                result = CustomEvaluationResultBuilder.createSingleStringValueResult(role.getId());
            }
            else if (resolvedAttributeIdValue.equals(ATTR_ROLE_CREATED_BY)) {
                result =
                    CustomEvaluationResultBuilder.createSingleStringValueResult(role
                        .getUserAccountByCreatorId().getId());
            }
            else if (resolvedAttributeIdValue.equals(ATTR_ROLE_MODIFIED_BY)) {
                result =
                    CustomEvaluationResultBuilder.createSingleStringValueResult(role
                        .getUserAccountByModifiedById().getId());
            }
            else if (resolvedAttributeIdValue.equals(ATTR_ROLE_NAME)) {
                result = CustomEvaluationResultBuilder.createSingleStringValueResult(role.getRoleName());
            }
        }
        else {
            return null;
        }

        return new Object[] { result, resolvedAttributeIdValue };
    }

    /**
     * Retrieve role from the system.
     *
     * @param ctx    The evaluation context, which will be used as key for the cache.
     * @param roleId The role id.
     * @return Returns the {@code EscidocRole} identified by the provided id.
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws RoleNotFoundException    Thrown if no role with provided id exists.
     */
    private EscidocRole retrieveRole(final EvaluationCtx ctx, final String roleId) throws WebserverSystemException,
        RoleNotFoundException {

        EscidocRole role = (EscidocRole) getFromCache(XmlUtility.NAME_ID, null, null, roleId, ctx);
        if (role == null) {
            try {
                role = roleDao.retrieveRole(roleId);
                if (role == null) {
                    throw new RoleNotFoundException(StringUtility.format("Role not found", roleId));
                }
                putInCache(XmlUtility.NAME_ID, null, null, roleId, ctx, role);
            }
            catch (final SqlDatabaseSystemException e) {
                throw new WebserverSystemException(StringUtility.format("Exception during retrieval of the role", e
                    .getMessage()), e);
            }
        }

        return role;
    }
}
