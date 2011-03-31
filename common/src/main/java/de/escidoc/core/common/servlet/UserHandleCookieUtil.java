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

package de.escidoc.core.common.servlet;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;

/**
 * Utility-Class that creates the escidocCookie containing the userHandle.
 *
 * @author Michael Hoppe
 */
public final class UserHandleCookieUtil {

    /**
     * The time span a cookie holding an eSciDoc user handle is valid (in seconds). -1 means valid until browser ends.
     */
    private static int escidocCookieLifetime = Integer.MIN_VALUE;

    /**
     * private constructor for util-class with static access only.
     */
    private UserHandleCookieUtil() {
    }

    /**
     * Creates an authentication cookie holding the provided eSciDoc user handle.<br> The cookie is valid for all
     * locations of the eSciDoc domain. Its max age is set to the value of the configuration parameter
     * escidoc.userHandle.cookie.lifetime.
     *
     * @param handle The eSciDocUserHandle.
     * @return Returns the created {@link Cookie}.
     * @throws WebserverSystemException Thrown in case of an internal error (configuration parameter not retrievable).
     */
    public static Cookie createAuthCookie(final String handle) throws WebserverSystemException {

        final Cookie authCookie = new Cookie(EscidocServlet.COOKIE_LOGIN, handle);

        authCookie.setMaxAge(getEscidocCookieLifetime());

        // Set the cookie for all locations of the escidoc domain
        authCookie.setPath("/");
        return authCookie;
    }

    /**
     * @return the escidocCookieLifetime.
     * @throws WebserverSystemException Thrown if access to configuration properties fails.
     */
    public static int getEscidocCookieLifetime() throws WebserverSystemException {

        if (escidocCookieLifetime == Integer.MIN_VALUE) {
            try {
                escidocCookieLifetime =
                    Integer.parseInt(EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.ESCIDOC_CORE_USERHANDLE_COOKIE_LIFETIME));
            }
            catch (final Exception e) {
                throw new WebserverSystemException(StringUtility.format("Can't get configuration parameter",
                    EscidocConfiguration.ESCIDOC_CORE_USERHANDLE_COOKIE_LIFETIME, e.getMessage()), e);
            }
        }
        return escidocCookieLifetime;
    }

    /**
     * Create Base46-Encoded userHandle.
     *
     * @param userHandle userHandle
     * @return the encoded userHandle.
     * @throws WebserverSystemException e
     */
    public static String createEncodedUserHandle(final String userHandle) throws WebserverSystemException {
        try {
            return new String(Base64.encodeBase64(userHandle.getBytes(XmlUtility.CHARACTER_ENCODING)),
                XmlUtility.CHARACTER_ENCODING);
        }
        catch (final UnsupportedEncodingException e) {
            throw new WebserverSystemException(StringUtility.format("Can't encode UserHandle Base64", e.getMessage()),
                e);
        }
    }

    /**
     * Create Base46-Encoded userHandle.
     *
     * @param userHandle userHandle
     * @return the decoded userHandle.
     * @throws WebserverSystemException e
     */
    public static String createDecodedUserHandle(final String userHandle) throws WebserverSystemException {
        try {
            return new String(Base64.decodeBase64(userHandle.getBytes(XmlUtility.CHARACTER_ENCODING)),
                XmlUtility.CHARACTER_ENCODING);
        }
        catch (final UnsupportedEncodingException e) {
            throw new WebserverSystemException(StringUtility.format("Can't decode UserHandle Base64", e.getMessage()),
                e);
        }
    }

}
