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

package de.escidoc.core.common.exceptions.application.security;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * The EsidocSecurityException is used to indicate that the action is not allowed because a security check fails. Status
 * code (302) indicating that a redirect to eSciDoc login could be needed.
 *
 * @author Torsten Tetteroo
 */
public class SecurityException extends EscidocException {

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = -7290777799078430569L;

    public static final int HTTP_STATUS_CODE = ESCIDOC_HTTP_SC_SECURITY;

    public static final String HTTP_STATUS_MESSAGE = "Security check failed. Redirect to login";

    private String redirectLocation;

    /**
     * Default constructor.
     *
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public SecurityException() throws WebserverSystemException {

        super(HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
        final String baseLocation = XmlUtility.getEscidocBaseUrl();
        this.redirectLocation = baseLocation + XmlUtility.BASE_LOGIN.substring(0, XmlUtility.BASE_LOGIN.length() - 1);
    }

    /**
     * Constructor used to create a new Exception with the specified detail message and a mapping to an initial
     * exception.
     *
     * @param message - the detail message.
     * @param cause   Throwable
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public SecurityException(final String message, final Throwable cause) throws WebserverSystemException {
        super(message, cause, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
        final String baseLocation = XmlUtility.getEscidocBaseUrl();
        this.redirectLocation = baseLocation + XmlUtility.BASE_LOGIN.substring(0, XmlUtility.BASE_LOGIN.length() - 1);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message - the detail message.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public SecurityException(final String message) throws WebserverSystemException {
        super(message, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
        final String baseLocation = XmlUtility.getEscidocBaseUrl();
        this.redirectLocation = baseLocation + XmlUtility.BASE_LOGIN.substring(0, XmlUtility.BASE_LOGIN.length() - 1);
    }

    /**
     * Constructor used to map an initial exception.
     *
     * @param cause Throwable
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public SecurityException(final Throwable cause) throws WebserverSystemException {
        super(cause, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
        final String baseLocation = XmlUtility.getEscidocBaseUrl();
        this.redirectLocation = baseLocation + XmlUtility.BASE_LOGIN.substring(0, XmlUtility.BASE_LOGIN.length() - 1);
    }

    /**
     * Default constructor.
     *
     * @param httpStatusCode the http status code
     * @param httpStatusMsg  the http status message
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public SecurityException(final int httpStatusCode, final String httpStatusMsg) throws WebserverSystemException {
        super(httpStatusCode, httpStatusMsg);
        final String baseLocation = XmlUtility.getEscidocBaseUrl();
        this.redirectLocation = baseLocation + XmlUtility.BASE_LOGIN.substring(0, XmlUtility.BASE_LOGIN.length() - 1);
    }

    /**
     * Constructor used to create a new Exception with the specified detail message and a mapping to an initial
     * exception.
     *
     * @param message        the detail message.
     * @param cause          Throwable
     * @param httpStatusCode the http status code
     * @param httpStatusMsg  the http status message
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public SecurityException(final String message, final Throwable cause, final int httpStatusCode,
        final String httpStatusMsg) throws WebserverSystemException {
        super(message, cause, httpStatusCode, httpStatusMsg);
        final String baseLocation = XmlUtility.getEscidocBaseUrl();
        this.redirectLocation = baseLocation + XmlUtility.BASE_LOGIN.substring(0, XmlUtility.BASE_LOGIN.length() - 1);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message        the detail message.
     * @param httpStatusCode the http status code
     * @param httpStatusMsg  the http status message
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public SecurityException(final String message, final int httpStatusCode, final String httpStatusMsg)
        throws WebserverSystemException {
        super(message, httpStatusCode, httpStatusMsg);
        final String baseLocation = XmlUtility.getEscidocBaseUrl();
        this.redirectLocation = baseLocation + XmlUtility.BASE_LOGIN.substring(0, XmlUtility.BASE_LOGIN.length() - 1);
    }

    /**
     * Constructor used to map an initial exception.
     *
     * @param cause          Throwable
     * @param httpStatusCode the http status code
     * @param httpStatusMsg  the http status message
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    public SecurityException(final Throwable cause, final int httpStatusCode, final String httpStatusMsg)
        throws WebserverSystemException {
        super(cause, httpStatusCode, httpStatusMsg);
        final String baseLocation = XmlUtility.getEscidocBaseUrl();
        this.redirectLocation = baseLocation + XmlUtility.BASE_LOGIN.substring(0, XmlUtility.BASE_LOGIN.length() - 1);
    }

    /**
     * Gets the redirect location (to the UserManagementWrapper).
     *
     * @return Returns the redirect location in a <code>String</code>.
     */
    public String getRedirectLocation() {
        return this.redirectLocation;
    }

    /**
     * Sets the redirect location (to the UserManagementWrapper).
     *
     */
    public void setRedirectLocation(final String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

}
