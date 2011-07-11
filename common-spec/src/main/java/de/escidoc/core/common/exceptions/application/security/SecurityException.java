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
import de.escidoc.core.common.util.configuration.EscidocConfiguration;

/**
 * The EsidocSecurityException is used to indicate that the action is not allowed because a security check fails. Status
 * code (302) indicating that a redirect to eSciDoc login could be needed.
 *
 * @author Torsten Tetteroo
 */
public class SecurityException extends EscidocException {

    private static final String BASE_AA = "/aa/";
    public static final String BASE_LOGIN = BASE_AA + "login" + '/';

    /**
     * The serial version uid.
     */
    private static final long serialVersionUID = -7290777799078430569L;

    private static final int HTTP_STATUS_CODE = ESCIDOC_HTTP_SC_SECURITY;

    private static final String HTTP_STATUS_MESSAGE = "Security check failed. Redirect to login";

    private String redirectLocation;

    /**
     * Default constructor.
     */
    public SecurityException() {

        super(HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
        this.redirectLocation = getEscidocBaseUrl() + BASE_LOGIN.substring(0, BASE_LOGIN.length() - 1);
    }

    /**
     * Constructor used to create a new Exception with the specified detail message and a mapping to an initial
     * exception.
     *
     * @param message - the detail message.
     * @param cause   Throwable
     */
    public SecurityException(final String message, final Throwable cause) {
        super(message, cause, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
        this.redirectLocation = getEscidocBaseUrl() + BASE_LOGIN.substring(0, BASE_LOGIN.length() - 1);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message - the detail message.
     */
    public SecurityException(final String message) {
        super(message, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
        final String baseLocation = getEscidocBaseUrl();
        this.redirectLocation = baseLocation + BASE_LOGIN.substring(0, BASE_LOGIN.length() - 1);
    }

    /**
     * Constructor used to map an initial exception.
     *
     * @param cause Throwable
     */
    public SecurityException(final Throwable cause) {
        super(cause, HTTP_STATUS_CODE, HTTP_STATUS_MESSAGE);
        this.redirectLocation = getEscidocBaseUrl() + BASE_LOGIN.substring(0, BASE_LOGIN.length() - 1);
    }

    /**
     * Default constructor.
     *
     * @param httpStatusCode the http status code
     * @param httpStatusMsg  the http status message
     */
    public SecurityException(final int httpStatusCode, final String httpStatusMsg) {
        super(httpStatusCode, httpStatusMsg);
        this.redirectLocation = getEscidocBaseUrl() + BASE_LOGIN.substring(0, BASE_LOGIN.length() - 1);
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
    public SecurityException(final String message, final Throwable cause, final int httpStatusCode,
        final String httpStatusMsg) {
        super(message, cause, httpStatusCode, httpStatusMsg);
        this.redirectLocation = getEscidocBaseUrl() + BASE_LOGIN.substring(0, BASE_LOGIN.length() - 1);
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message        the detail message.
     * @param httpStatusCode the http status code
     * @param httpStatusMsg  the http status message
     */
    public SecurityException(final String message, final int httpStatusCode, final String httpStatusMsg) {
        super(message, httpStatusCode, httpStatusMsg);
        this.redirectLocation = getEscidocBaseUrl() + BASE_LOGIN.substring(0, BASE_LOGIN.length() - 1);
    }

    /**
     * Constructor used to map an initial exception.
     *
     * @param cause          Throwable
     * @param httpStatusCode the http status code
     * @param httpStatusMsg  the http status message
     */
    public SecurityException(final Throwable cause, final int httpStatusCode, final String httpStatusMsg) {
        super(cause, httpStatusCode, httpStatusMsg);
        this.redirectLocation = getEscidocBaseUrl() + BASE_LOGIN.substring(0, BASE_LOGIN.length() - 1);
    }

    /**
     * Gets the redirect location (to the UserManagementWrapper).
     *
     * @return Returns the redirect location in a {@code String}.
     */
    public String getRedirectLocation() {
        return this.redirectLocation;
    }

    /**
     * Sets the redirect location (to the UserManagementWrapper).
     *
     * @param redirectLocation
     */
    public void setRedirectLocation(final String redirectLocation) {
        this.redirectLocation = redirectLocation;
    }

    private static String getEscidocBaseUrl() {
        return EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_BASEURL);
    }


}
