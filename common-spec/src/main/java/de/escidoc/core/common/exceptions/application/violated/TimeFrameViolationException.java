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

package de.escidoc.core.common.exceptions.application.violated;

/**
 * The TimeFrameViolationException is used to indicate that TimeFrame is not valid because TimeFrameStart is later than
 * TimeFrameEnd or a format for TimeFrameStart/TimeFrameEnd is wrong. returned httpStatusCode is 409. Status code (409)
 * indicating that the request could not be completed due to a conflict with the current state of the resource.
 *
 * @author Michael Hoppe (FIZ Karlsruhe)
 */
public class TimeFrameViolationException extends RuleViolationException {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = 4944149462274326147L;

    private static final int HTTP_STATUS_CODE = ESCIDOC_HTTP_SC_VIOLATED;

    private static final String HTTP_STATUS_MESSAGE = "Time frame is not valid.";

    /**
     * Default constructor.
     */
    public TimeFrameViolationException() {
        super(HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Constructor used to map an initial exception.
     *
     * @param error Throwable
     */
    public TimeFrameViolationException(final Throwable error) {
        super(error, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message - the detail message.
     */
    public TimeFrameViolationException(final String message) {
        super(message, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Constructor used to create a new Exception with the specified detail message and a mapping to an initial
     * exception.
     *
     * @param message - the detail message.
     * @param error   Throwable
     */
    public TimeFrameViolationException(final String message, final Throwable error) {
        super(message, error, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }
}
