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

package de.escidoc.core.common.exceptions.application.invalid;

/**
 * The LastModificationDateMissingException is used to indicate that the last-modification-date sent in the service-call
 * is empty.
 * 
 * @author André Schenk
 */
public class LastModificationDateMissingException extends ValidationException {

    private static final long serialVersionUID = 24640466755582509L;

    /**
     * Default constructor.
     */
    public LastModificationDateMissingException() {
    }

    /**
     * Constructor used to map an initial exception.
     * 
     * @param error
     *            Throwable
     */
    public LastModificationDateMissingException(final Throwable error) {
        super(error);
    }

    /**
     * Constructs a new exception with the specified detail message.
     * 
     * @param message
     *            - the detail message.
     */
    public LastModificationDateMissingException(final String message) {
        super(message);
    }

    /**
     * Constructor used to create a new Exception with the specified detail message and a mapping to an initial
     * exception.
     * 
     * @param message
     *            - the detail message.
     * @param error
     *            Throwable
     */
    public LastModificationDateMissingException(final String message, final Throwable error) {
        super(message, error);
    }
}
