/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
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

package de.escidoc.core.aa.servlet;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.servlet.UserHandleCookieUtil;
import de.escidoc.core.common.util.service.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationFilter.class);

    public static final String COOKIE_LOGIN = "escidocCookie";

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final HttpServletResponse httpResponse = (HttpServletResponse) response;
        final String[] authValues = getAuthValues(httpRequest, httpResponse);
        if (authValues[1] != null) {
            UserContext.setUserContext(authValues[1]);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

    /**
     * Gets the http basic authorization values from the provided http servlet request.<br>
     * The following steps are performed:
     * <ul>
     * <li>If the provided http request contains the http authorization header {@code Authorization}, username and
     * password are extracted from the header and returned.</li>
     * <li>If no authorization header has been found, a cookie containing the escidoc handle is searched. If such a
     * cookie is found, the username &quot;Shibboleth-user&quot; and the handle as password are returned.</li>
     * <li>If no authorization header and no valid cookie has been found, a redirect to the escidoc login servlet is
     * sent back and the method returns {@code null}.
     * </ul>
     *
     * @param request
     *            The http request.
     * @param response
     *            The http response.
     * @return Returns an {@code String} array with the user name value at the first position and the password at
     *         the second position.<br>
     *         If no Authorization header has been set, {@code null} is returned.
     * @throws IOException
     *             In case of an I/O error.
     */
    private static String[] getAuthValues(final HttpServletRequest request, final HttpServletResponse response)
        throws IOException {

        // Authentication via browser cookie
        final Cookie cookie = getCookie(COOKIE_LOGIN, request);
        if (cookie != null) {
            final String handle = cookie.getValue();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Received handle in cookie: " + handle);
            }
            return new String[] { "ShibbolethUser", handle };
        }
        // Authentication via Auth-Header
        else if (request.getHeader("Authorization") != null && request.getHeader("Authorization").length() != 0) {
            String authHeader = request.getHeader("Authorization");
            authHeader = authHeader.substring(authHeader.indexOf(' '));
            try {
                final String decoded = UserHandleCookieUtil.createDecodedUserHandle(authHeader);
                final int i = decoded.indexOf(':');
                return new String[] { "ShibbolethUser", decoded.substring(i + 1) };
            }
            catch (final WebserverSystemException e) {
                throw new IOException("cannot decode user handle", e);
            }
        }
        else {
            LOGGER.info("No handle in cookie received, assuming  anonymous access.");
            return new String[] { "", "" };
        }
    }

    /**
     * Get a cookie from the provided http request.
     *
     * @param name
     *            The name of the cookie.
     * @param request
     *            the request.
     * @return The cookie.
     */
    public static Cookie getCookie(final String name, final HttpServletRequest request) {
        Cookie result = null;
        final Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (final Cookie cooky : cookies) {
                if (name.equals(cooky.getName())) {
                    result = cooky;
                    break;
                }
            }
        }
        return result;
    }
}
