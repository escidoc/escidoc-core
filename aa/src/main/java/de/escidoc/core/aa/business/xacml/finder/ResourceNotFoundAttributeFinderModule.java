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
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attribute finder module implementation that checks if a resource object addressed by the resource-id exists and is of
 * the object type addressed by the id of the attribute that currently shall be resolved.<br> This finder module returns
 * either an empty {@code EvaluationResult} if the resource exists and is of the expected type, or it returns an
 * {@code EvaluationResult} reporting the appropriate {@code ResourceNotFoundException.} This finder module
 * must be the first eSciDoc specific finder module in the chain, but must be placed after the 'standard' finder
 * modules.
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.ResourceNotFoundAttributeFinderModule")
public class ResourceNotFoundAttributeFinderModule extends AbstractAttributeFinderModule {

    /**
     * Pattern matching object-type attribute ids or resource identifiers (...-id) attribute ids.
     */
    private static final Pattern PATTERN_OBJECT_TYPE_OR_RESOURCE_IDENTIFIER =
        Pattern.compile(AttributeIds.URN_OBJECT_TYPE + ".*|" + AttributeIds.RESOURCE_ATTR_PREFIX + "[^:]+-id.*");

    private static final String ERROR_MSG_RESOURCE_NOT_FOUND_DUE_TO_WRONG_TYPE =
        "The resource with the specified id is not of the expected type.";

    /**
     * Private constructor to prevent initialization.
     */
    protected ResourceNotFoundAttributeFinderModule() {
    }

    /**
     * See Interface for functional description.
     */
    @Override
    protected boolean assertAttribute(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber, final int designatorType) throws EscidocException {

        if (!super.assertAttribute(attributeIdValue, ctx, resourceId, resourceObjid, resourceVersionNumber,
            designatorType)) {
            return false;
        }

        return !(FinderModuleHelper.isNewResourceId(resourceId) || PATTERN_OBJECT_TYPE_OR_RESOURCE_IDENTIFIER.matcher(
            attributeIdValue).find());

    }

    /**
     * See Interface for functional description.
     */
    @Override
    protected Object[] resolveLocalPart(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber) throws ResourceNotFoundException, WebserverSystemException {

        // check if the object identified by resource-id is of the expected
        // object type addressed in the attribute id
        final String resourceObjectType = fetchObjectType(ctx);

        final Matcher matcher = PATTERN_PARSE_ATTRIBUTE_ID.matcher(attributeIdValue);
        if (matcher.find()) {
            final String expectedObjectType = matcher.group(2);
            if (!resourceObjectType.equals(expectedObjectType)) {
                final String emsg =
                    StringUtility.format(ERROR_MSG_RESOURCE_NOT_FOUND_DUE_TO_WRONG_TYPE, resourceId,
                        "expectedObjectType:" + expectedObjectType + ", attributeIdValue:" + attributeIdValue
                            + ", resourceObjectType:" + resourceObjectType);
                throw new ResourceNotFoundException(emsg);
            }
        }

        // always return null (empty result) if no error occurred.
        return null;
    }

}
