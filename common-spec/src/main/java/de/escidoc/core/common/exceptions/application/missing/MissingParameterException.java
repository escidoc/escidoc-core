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

package de.escidoc.core.common.exceptions.application.missing;

import de.escidoc.core.common.exceptions.application.ApplicationException;

/**
 * Main-Class for Exceptions that are thrown because a mandatory parameter was not passed to the Interface. returned
 * httpStatusCode is 417. Status code (417) indicating that the server could not meet the expectation given in the
 * Expect request header.
 *
 * @author Michael Hoppe (FIZ Karlsruhe)
 */
public class MissingParameterException extends ApplicationException {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 11834342425140655L;

    private static final int HTTP_STATUS_CODE = ESCIDOC_HTTP_SC_MISSING;

    private static final String HTTP_STATUS_MESSAGE = "Parameter is missing.";

    /**
     * Default constructor.
     */
    public MissingParameterException() {
        super(HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Constructor used to create a new Exception with the specified detail message and a mapping to an initial
     * exception.
     *
     * @param message - the detail message.
     * @param cause   Throwable
     */
    public MissingParameterException(final String message, final Throwable cause) {
        super(message, cause, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message - the detail message.
     */
    public MissingParameterException(final String message) {
        super(message, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Constructor used to map an initial exception.
     *
     * @param cause Throwable
     */
    public MissingParameterException(final Throwable cause) {
        super(cause, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Default constructor.
     *
     * @param httpStatusCode the http status code
     * @param httpStatusMsg  the http status message
     */
    public MissingParameterException(final int httpStatusCode, final String httpStatusMsg) {
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
    public MissingParameterException(final String message, final Throwable cause, final int httpStatusCode,
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
    public MissingParameterException(final String message, final int httpStatusCode, final String httpStatusMsg) {
        super(message, httpStatusCode, httpStatusMsg);
    }

    /**
     * Constructor used to map an initial exception.
     *
     * @param cause          Throwable
     * @param httpStatusCode the http status code
     * @param httpStatusMsg  the http status message
     */
    public MissingParameterException(final Throwable cause, final int httpStatusCode, final String httpStatusMsg) {
        super(cause, httpStatusCode, httpStatusMsg);
    }

}
