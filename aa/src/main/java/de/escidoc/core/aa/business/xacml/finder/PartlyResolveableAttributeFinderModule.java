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
import de.escidoc.core.aa.business.authorisation.Constants;
import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.missing.MissingParameterException;
import de.escidoc.core.common.util.string.StringUtility;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attribute finder module implementation that resolves partly resolveable attributes of new or updated resources.<br>
 * This finder module handles all attributes that contain the substring "-new" but does not end with this substring.
 * This substring identifies attributes whose values are changed during a create or an update operation. The attribute
 * id consists of two parts: The first part of until the "-new" substring, and the part behind "-new" which must not be
 * empty. The first part cannot be automatically resolved and must be provided within the request, i.e. it must be part
 * of the context. The value for this part is retrieved from the context. If this fails, an error is "thrown".
 * Otherwise, the retrieved value is used as the starting point to resolve the second part of the attribute id by
 * recursively calling {@code getResourceAttribute}.<br> This finder module should be the one of the first eSciDoc
 * specific finder module in the chain, but must be placed after the 'standard' finder modules.
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.PartlyResolveableAttributeFinderModule")
public class PartlyResolveableAttributeFinderModule extends AbstractAttributeFinderModule {

    /**
     * Pattern to check if an attribute id is a partly resolveable attribute id, i.e. it contains the substring MARKER
     * but does not end with it, and it contains the MARKER one time, only.
     */
    private static final Pattern PATTERN_PARSE_PARTLY_RESOLVEABLE_ATTRIBUTE_ID =
        Pattern.compile("(^.*?" + AttributeIds.MARKER + ")+:(.*?" + AttributeIds.MARKER + ")*.*?");

    /**
     * The length of the marker.
     */
    public static final int MARKER_LENGTH = AttributeIds.MARKER.length();

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected PartlyResolveableAttributeFinderModule() {
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

        // make sure it is a partly resolveable attribute id, i.e. it contains
        // the substring MARKER but does not end with it, and it contains the
        // MARKER one time, only.
        final Matcher matcher = PATTERN_PARSE_PARTLY_RESOLVEABLE_ATTRIBUTE_ID.matcher(attributeIdValue);
        return !(!matcher.find() || matcher.group(2) != null);

    }

    /**
     * See Interface for functional description.
     */
    @Override
    protected Object[] resolveLocalPart(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber) throws EscidocException {

        // First, the part until the MARKER has to be resolved. This is an
        // attribute for that the value has to be provided in the request
        // as it is a "new" value that can not be automatically resolved.
        // ctx.getResourceAttribute() is called to get this new value.
        // This fetched value is returned to enable recursively resolving the
        // complete attribute id, see AbstractAttributeFinderModule.
        final Matcher matcher = PATTERN_PARSE_PARTLY_RESOLVEABLE_ATTRIBUTE_ID.matcher(attributeIdValue);
        matcher.find();
        final String firstPartAttributeId = matcher.group(1);

        EvaluationResult result;
        try {
            result = ctx.getResourceAttribute(Constants.URI_XMLSCHEMA_STRING, new URI(firstPartAttributeId), null);
        }
        catch (final URISyntaxException e) {
            result = CustomEvaluationResultBuilder.createSyntaxErrorResult(e);
        }
        if (isEmptyResult(result)) {
            result =
                CustomEvaluationResultBuilder.createMissingAttributeErrorResult(new MissingParameterException(
                    StringUtility.format("Needed attribute value not provided", firstPartAttributeId)));
        }

        return new Object[] { result, firstPartAttributeId };
    }

}
