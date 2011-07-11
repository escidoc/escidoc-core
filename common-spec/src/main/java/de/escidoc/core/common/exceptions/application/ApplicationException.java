/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.exceptions.application;

import de.escidoc.core.common.exceptions.EscidocException;

/**
 * Main-Class for Exceptions that are caused by the business logic ApplicationException contains a HttpStatusCode and a
 * HttpStatusMessage which should be used in the REST interface. returned httpStatusCode is 400. Status code (400)
 * indicating the request sent by the client was syntactically incorrect.
 * <p/>
 * Subclasses are: ValidationException, throws statusCode 412 Status code (412) indicating that the precondition given
 * in one or more of the request-header fields evaluated to false when it was tested on the server.
 * <p/>
 * MissingParameterException, throws statusCode 417 Status code (417) indicating that the server could not meet the
 * expectation given in the Expect request header.
 * <p/>
 * ResourceNotFoundException, throws statusCode 404 Status code (404) indicating that the requested resource is not
 * available.
 * <p/>
 * RuleViolationException, throws statusCode 409 Status code (409) indicating that the request could not be completed
 * due to a conflict with the current state of the resource.
 *
 * @author Michael Hoppe (FIZ Karlsruhe)
 */
public class ApplicationException extends EscidocException {

    private static final long serialVersionUID = 6185967378077511872L;

    private static final int HTTP_STATUS_CODE = ESCIDOC_HTTP_SC_BAD_REQUEST;

    private static final String HTTP_STATUS_MESSAGE = "eSciDoc Application Error";

    /**
     * Default constructor.
     */
    protected ApplicationException() {
        super(HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Constructor used to create a new Exception with the specified detail message and a mapping to an initial
     * exception.
     *
     * @param message - the detail message.
     * @param cause   Throwable
     */
    protected ApplicationException(final String message, final Throwable cause) {
        super(message, cause, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message - the detail message.
     */
    protected ApplicationException(final String message) {
        super(message, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Constructor used to map an initial exception.
     *
     * @param cause Throwable
     */
    protected ApplicationException(final Throwable cause) {
        super(cause, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Default constructor.
     *
     * @param httpStatusCode the http status code
     * @param httpStatusMsg  the http status message
     */
    protected ApplicationException(final int httpStatusCode, final String httpStatusMsg) {
        super(httpStatusCode, httpStatusMsg);
    }

    /**
     * Constructor used to create a new Exception with the specified detail message and a mapping to an initial
     * exception.
     *
     * @param message        the detail message.
     * @param cause          Throwable
     * @param httpStatusCode the http status code
     * @param httpStatusMsg  the http status message
     */
    protected ApplicationException(final String message, final Throwable cause, final int httpStatusCode,
        final String httpStatusMsg) {
        super(message, cause, httpStatusCode, httpStatusMsg);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message        the detail message.
     * @param httpStatusCode the http status code
     * @param httpStatusMsg  the http status message
     */
    protected ApplicationException(final String message, final int httpStatusCode, final String httpStatusMsg) {
        super(message, httpStatusCode, httpStatusMsg);
    }

    /**
     * Constructor used to map an initial exception.
     *
     * @param cause          Throwable
     * @param httpStatusCode the http status code
     * @param httpStatusMsg  the http status message
     */
    protected ApplicationException(final Throwable cause, final int httpStatusCode, final String httpStatusMsg) {
        super(cause, httpStatusCode, httpStatusMsg);
    }

}
