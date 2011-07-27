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
 * Copyright 2007-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.business.xacml.finder;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import de.escidoc.core.aa.business.authorisation.Constants;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.string.StringUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Attribute finder module implementation that checks if provided attributes references existing resource objects. If a
 * referencing attribute does not point to an existing object or points to an object of another resource type, the
 * appropriate {@link ResourceNotFoundException} is thrown.<br> The ids of the attributes that have to be checked (i.e.
 * the ids of the attributes that have been provided in the authorization request) have to be provided in an environment
 * attribute with id "info:escidoc/names:aa:1.0:internal:environment:provided-attributes".<br> This check is performed
 * one time during the evaluation of a request, only.<br> The reason for performing these checks within the chain of
 * attribute finder modules is to provide the attribute values used to perform theses checks for further attribute
 * resolving.<br> This finder module must be the first eSciDoc specific finder module in the chain, but must be placed
 * after the 'standard' finder modules.
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.CheckProvidedAttributeFinderModule")
public class CheckProvidedAttributeFinderModule extends AbstractAttributeFinderModule {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckProvidedAttributeFinderModule.class);

    private static final String PROVIDED_ATTRIBUTES_ID =
        AttributeIds.INTERNAL_ENVIRONMENT_PREFIX + "provided-attributes";

    /**
     * Pattern used to check if an attribute id is a id of a new resource attribute or an identifier attribute.
     */
    private static final Pattern PATTERN_ID_ATTRIBUTE_OR_NEW_ATTRIBUTE = Pattern.compile(".*:([^-]*)(-id|-new){0,1}");

    @SuppressWarnings( { "CanBeFinal" })
    private static URI providedAttributesIdUri; // Ignore FindBugs

    static {
        try {
            providedAttributesIdUri = new URI(PROVIDED_ATTRIBUTES_ID);
        }
        catch (final URISyntaxException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on initialising provided attributes ID.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on initialising provided attributes ID.", e);
            }
        }
    }

    /**
     * Private constructor to prevent initialization.
     */
    protected CheckProvidedAttributeFinderModule() {
    }

    /**
     * See Interface for functional description.
     */
    @Override
    protected boolean assertAttribute(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber, final int designatorType) throws EscidocException {

        // The check should be performed one time per evaluation, only.
        // Additionally, it has to be assured, that the check is not again
        // executed during the check itself to avoid endless loops.
        // The first step will be to store a value in the request attribute
        // cache to flag that a check is currently executed or has been
        // executed. Therefore, if this value exists in the cache, this finder
        // must not be executed, again.
        return getFromCache("", "", "", PROVIDED_ATTRIBUTES_ID, ctx) == null;
    }

    /**
     * See Interface for functional description.<br> This implementation does not resolve a value but performs the check
     * if provided references to other resource objects are correct. It always returns {@code null} in case of
     * success or throws an exception.
     */
    @Override
    protected Object[] resolveLocalPart(
        final String attributeIdValue, final EvaluationCtx ctx, final String resourceId, final String resourceObjid,
        final String resourceVersionNumber) throws SystemException, ResourceNotFoundException {

        final AttributeValue providedAttributesIds =
            ctx.getEnvironmentAttribute(Constants.URI_XMLSCHEMA_STRING, getAttributeId(), null).getAttributeValue();
        putInCache("", "", "", PROVIDED_ATTRIBUTES_ID, ctx, new EvaluationResult(providedAttributesIds));
        final Iterator<StringAttribute> iter = ((BagAttribute) providedAttributesIds).iterator();
        while (iter.hasNext()) {
            final String attributeId = iter.next().getValue();
            final Matcher m = PATTERN_ID_ATTRIBUTE_OR_NEW_ATTRIBUTE.matcher(attributeId);
            if (m.find()) {
                final String expectedObjectType = m.group(1);
                if (PATTERN_ID_VALIDATABLE_OBJECT_TYPE.matcher(expectedObjectType).find()) {
                    final String id = fetchSingleResourceAttribute(ctx, attributeId);
                    String objectType;
                    try {
                        objectType = fetchObjectType(ctx, id);
                    }
                    catch (final ResourceNotFoundException e) {
                        objectType = "";
                    }
                    if (!expectedObjectType.equals(objectType)) {

                        final String resourceName =
                            StringUtility.convertToUpperCaseLetterFormat(expectedObjectType).toString();
                        final String exceptionName =
                            RESOURCE_NOT_FOUND_EXCEPTION_PACKAGE_PREFIX + resourceName + "NotFoundException";
                        final String errorMsg = StringUtility.format(resourceName + " not found", id);

                        try {
                            final Class<ResourceNotFoundException> exceptionClass =
                                (Class<ResourceNotFoundException>) Class.forName(exceptionName);
                            final Constructor<ResourceNotFoundException> constructor =
                                exceptionClass.getConstructor(new Class[] { String.class, Throwable.class });
                            throw constructor.newInstance(errorMsg, null);
                        }
                        catch (final ResourceNotFoundException e) {
                            throw e;
                        }
                        catch (final Exception e) {
                            throw new ResourceNotFoundException(errorMsg, e);
                        }
                    }
                }
            }
        }

        return null;

    }

    /**
     * Gets the id of the environment attribute used to forward the ids of the attributes provided within an
     * authorization request to this finder module.
     *
     * @return Returns an {@link URI} representing the attribute id.
     * @throws SystemException Thrown in case of an internal system error.
     */
    public static URI getAttributeId() {
        return providedAttributesIdUri;
    }

}
