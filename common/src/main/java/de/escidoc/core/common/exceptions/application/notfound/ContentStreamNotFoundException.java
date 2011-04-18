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

@Deprecated
public class ContentStreamNotFoundException extends ResourceNotFoundException {

    /**
     *
     */
    private static final long serialVersionUID = -2584653003617447929L;

    public static final String HTTP_STATUS_MESSAGE = "Content stream was not found";

    public ContentStreamNotFoundException() {
        // TODO Auto-generated constructor stub
    }

    public ContentStreamNotFoundException(final int httpStatusCode, final String httpStatusMsg) {
        super(httpStatusCode, httpStatusMsg);
        // TODO Auto-generated constructor stub
    }

    public ContentStreamNotFoundException(final String message, final int httpStatusCode, final String httpStatusMsg) {
        super(message, httpStatusCode, httpStatusMsg);
        // TODO Auto-generated constructor stub
    }

    public ContentStreamNotFoundException(final String message, final Throwable cause, final int httpStatusCode,
        final String httpStatusMsg) {
        super(message, cause, httpStatusCode, httpStatusMsg);
        // TODO Auto-generated constructor stub
    }

    public ContentStreamNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }

    public ContentStreamNotFoundException(final String message) {
        super(message);
        // TODO Auto-generated constructor stub
    }

    public ContentStreamNotFoundException(final Throwable cause, final int httpStatusCode, final String httpStatusMsg) {
        super(cause, httpStatusCode, httpStatusMsg);
        // TODO Auto-generated constructor stub
    }

    public ContentStreamNotFoundException(final Throwable cause) {
        super(cause);
        // TODO Auto-generated constructor stub
    }
}
