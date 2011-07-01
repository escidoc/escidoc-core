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

package de.escidoc.core.common.exceptions.application.notfound;

/**
 * The TargetBasketNotFoundException is used to indicate that the target basket requested in the context of the
 * service-call is null or does not exist in the System. returned httpStatusCode is 404. Status code (404) indicating
 * that the requested resource is not available.
 *
 * @author Michael Hoppe (FIZ Karlsruhe)
 */
public class TargetBasketNotFoundException extends ResourceNotFoundException {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = -9130430226462103139L;

    private static final int HTTP_STATUS_CODE = ESCIDOC_HTTP_SC_NOT_FOUND;

    private static final String HTTP_STATUS_MESSAGE = "Target Basket was not found.";

    /**
     * Default constructor.
     */
    public TargetBasketNotFoundException() {
        super(HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Constructor used to map an initial exception.
     *
     * @param error Throwable
     */
    public TargetBasketNotFoundException(final Throwable error) {
        super(error, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message - the detail message.
     */
    public TargetBasketNotFoundException(final String message) {
        super(message, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }

    /**
     * Constructor used to create a new Exception with the specified detail message and a mapping to an initial
     * exception.
     *
     * @param message - the detail message.
     * @param error   Throwable
     */
    public TargetBasketNotFoundException(final String message, final Throwable error) {
        super(message, error, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
    }
}
