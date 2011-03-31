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

/**
 *
 */
package de.escidoc.core.common.exceptions.application.notfound;

import de.escidoc.core.common.exceptions.EscidocException;

/**
 * @author Frank Schwichtenberg
 */
public class StreamNotFoundException extends EscidocException {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = -99921296230375036L;

    /**
     *
     */
    public StreamNotFoundException() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message message
     */
    public StreamNotFoundException(final String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message message
     * @param cause   cause
     */
    public StreamNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param cause cause
     */
    public StreamNotFoundException(final Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }

}
