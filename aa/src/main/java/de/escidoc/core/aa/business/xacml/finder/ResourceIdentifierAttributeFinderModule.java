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
import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finder module implementation that handles the resource identifier modules like ...:resource:item-id
 * (...:resource:item).<br> This finder modules tries to resolve these attributes if they have not been provided. The
 * resource-id attribute is returned if the object-type related to the resource-id equals the "object-type" in the
 * attribute id.
 * <p/>
 * Supported Attributes:<br> -info:escidoc/names:aa:1.0:resource:component-id<br> the id of the component, single value
 * attribute -info:escidoc/names:aa:1.0:resource:container-id<br> the id of the container, single value attribute
 * -info:escidoc/names:aa:1.0:resource:content-type-id<br> the id of the content-type, single value attribute
 * -info:escidoc/names:aa:1.0:resource:context-id<br> the id of the context, single value attribute
 * -info:escidoc/names:aa:1.0:resource:grant-id<br> the id of the grant, single value attribute
 * -info:escidoc/names:aa:1.0:resource:item-id<br> the id of the item, single value attribute
 * -info:escidoc/names:aa:1.0:resource:organizational-unit-id<br> the id of the organizational-unit, single value
 * attribute -info:escidoc/names:aa:1.0:resource:role-id<br> the id of the role, single value attribute
 * -info:escidoc/names:aa:1.0:resource:user-account-id<br> the id of the user-account, single value attribute
 * -info:escidoc/names:aa:1.0:resource:user-group-id<br> the id of the user-group, single value attribute
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.ResourceIdentifierAttributeFinderModule")
public class ResourceIdentifierAttributeFinderModule extends AbstractAttributeFinderModule {

    /**
     * Pattern used to parse the attribute id and check if this module is responsible for it. This extracts the expected
     * object-type, too.
     */
    private static final Pattern PATTERN_PARSE_RESOURCE_IDENTIFIED_ATTRIBUTE_ID =
        Pattern.compile(AttributeIds.RESOURCE_ATTR_PREFIX + "(component|container|content-type|context|grant|item"
            + "|organizational-unit|role|user-account|user-group)(-id){0,1}$");

    /**
     * Private constructor to prevent initialization.
     */
    protected ResourceIdentifierAttributeFinderModule() {
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

        // make sure they're asking for a resource identifier attribute
        return PATTERN_PARSE_RESOURCE_IDENTIFIED_ATTRIBUTE_ID.matcher(attributeIdValue).find();

    }

    /**
     * See Interface for functional description.
     */
    @Override
    protected Object[] resolveLocalPart(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber) throws ResourceNotFoundException, WebserverSystemException {

        // check if resource id is empty, i.e. new resource. In this case, an
        // empty result (= null) has to be returned, as no resource identifiers
        // can exist for new resources
        if (FinderModuleHelper.isNewResourceId(resourceId)) {
            return null;
        }

        // all we have to do is to check if the object type of the object
        // identified by the resource-id matches the expected that is extracted
        // from the attribute id
        final String objectType = fetchObjectType(ctx);

        final Matcher matcher = PATTERN_PARSE_RESOURCE_IDENTIFIED_ATTRIBUTE_ID.matcher(attributeIdValue);
        if (matcher.find()) {
            final String objectTypeFromAttributeId = matcher.group(1);
            if (objectType.equals(objectTypeFromAttributeId)) {
                return new Object[] { CustomEvaluationResultBuilder.createSingleStringValueResult(resourceId),
                    attributeIdValue };
            }
        }
        return null;
    }

}
