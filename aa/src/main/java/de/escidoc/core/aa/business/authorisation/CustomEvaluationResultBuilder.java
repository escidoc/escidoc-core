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
package de.escidoc.core.aa.business.authorisation;

import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.ctx.Status;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to create XACML evaluation results.
 *
 * @author Torsten Tetteroo
 */
public final class CustomEvaluationResultBuilder {

    /**
     * Private constructor to prevent instantiation.
     */
    private CustomEvaluationResultBuilder() {
    }

    /**
     * Creates an {@code EvaluationResult} object holding an error {@code Status}.<br> This can be used to
     * tunnel an exception through the XACML engine.<br> If the provided exception is not an
     * {@code EscidocException}, it is wrapped by a {@code WebserverSystemException .}
     *
     * @param status The status code, one of the codes defined in the class {@code com.sun.xacml.ctx.Status}.
     * @param e      The {@code Exception} that caused this error result.
     * @return Returns the created {@code EvaluationResult} object.
     */
    private static EvaluationResult createErrorResult(final String status, final Exception e) {

        return new EvaluationResult(CustomStatusBuilder.createErrorStatus(status, e));

    }

    /**
     * Creates an {@code EvaluationResult} object holding a {@code Status} indicating a syntax error.
     *
     * @param e The {@code Exception} that caused this error result.
     * @return Returns the created {@code EvaluationResult} object.
     */
    public static EvaluationResult createSyntaxErrorResult(final Exception e) {

        return createErrorResult(Status.STATUS_PROCESSING_ERROR, e);
    }

    /**
     * Creates an empty evaluation result for the attribute type http://www.w3.org/2001/XMLSchema#string.
     *
     * @return Returns an empty evaluation result.
     */
    public static EvaluationResult createEmptyEvaluationResult() {

        return createEmptyEvaluationResult(Constants.URI_XMLSCHEMA_STRING);
    }

    /**
     * Creates an empty evaluation result for the provided attribute type.
     *
     * @param attributeType The URI specifying the attribute type.
     * @return Returns an empty evaluation result.
     */
    public static EvaluationResult createEmptyEvaluationResult(final URI attributeType) {

        return new EvaluationResult(BagAttribute.createEmptyBag(attributeType));
    }

    /**
     * Creates an {@code EvaluationResult} object holding a {@code Status} indicating a missing attribute
     * error.
     *
     * @param e The {@code Exception} that caused this error result.
     * @return Returns the created {@code EvaluationResult} object.
     */
    public static EvaluationResult createMissingAttributeErrorResult(final Exception e) {

        return createErrorResult(Status.STATUS_MISSING_ATTRIBUTE, e);
    }

    /**
     * Creates an {@code EvaluationResult} object holding a {@code Status} indicating a processing error.
     *
     * @param e The {@code Exception} that caused this error result.
     * @return Returns the created {@code EvaluationResult} object.
     */
    public static EvaluationResult createProcessingErrorResult(final Exception e) {

        return createErrorResult(Status.STATUS_PROCESSING_ERROR, e);
    }

    /**
     * Creates a evaluation result holding a single value.
     *
     * @param value The single value of the evaluation result.
     * @return Returns the created {@link EvaluationResult} object.
     */
    public static EvaluationResult createSingleStringValueResult(final String value) {

        final List<StringAttribute> stringAttributes = new ArrayList<StringAttribute>();
        stringAttributes.add(new StringAttribute(value));
        return new EvaluationResult(new BagAttribute(Constants.URI_XMLSCHEMA_STRING, stringAttributes));
    }

    /**
     * Creates an error result for a {@code ResourceNotFoundException}.
     *
     * @param e The {@code ResourceNotFoundException}.
     * @return Returns the created {@code EvaluationResult}.
     */
    public static EvaluationResult createResourceNotFoundResult(final ResourceNotFoundException e) {

        return createErrorResult(CustomStatusBuilder.getResourceNotFoundStatusCode(e), e);
    }

}
