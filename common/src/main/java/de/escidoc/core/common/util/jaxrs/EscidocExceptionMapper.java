/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.util.jaxrs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.aopalliance.aop.AspectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.security.SecurityException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.servlet.UserHandleCookieUtil;
import de.escidoc.core.common.util.jaxb.ExceptionTOFactory;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * @author Michael Hoppe
 *
 */
@Provider
public class EscidocExceptionMapper implements ExceptionMapper<Throwable> {

    @Context
    private HttpServletRequest httpRequest;

    /**
     * The LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EscidocExceptionMapper.class);

    /**
     * The target URL to which the user shall be redirected after the authentication process.
     */
    public static final String PARAM_TARGET = "target";

    public static final String ENCODING = XmlUtility.CHARACTER_ENCODING;

    private static final String XML_RESPONSE_CONTENT_TYPE = "text/xml; charset=" + XmlUtility.CHARACTER_ENCODING;

    /**
     * HTTP header Cache-Control (since HTTP 1.1).
     */
    private static final String HTTP_HEADER_CACHE_CONTROL = "Cache-Control";

    /**
     * The No-Cache directive for Cache-Control Header and Pragma to prevent caching of the http response.
     */
    private static final String HTTP_HEADER_VALUE_NO_CACHE = "no-cache";

    /**
     * HTTP Pragma.
     */
    private static final String HTTP_HEADER_PRAGMA = "Pragma";

    /**
     * HTTP Content-Type.
     */
    private static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * HTTP Location.
     */
    private static final String HTTP_HEADER_LOCATION = "Location";

    private static final String HEADER_ESCIDOC_EXCEPTION = "eSciDocException";

    public static final String COOKIE_LOGIN = "escidocCookie";

    public Response toResponse(Throwable e) {
        ResponseBuilder responseBuilder = handleException(e);
        return responseBuilder.build();
    }

    /**
     * Handles an {@code Exception}.<br>
     * Depending on the exception that caused the invocation target exception, different responses are created and sent
     * to the client.
     *
     * @param e
     *            The exception to handle.
     * @return Returns Exception as ResponseBuilder.
     */
    private ResponseBuilder handleException(final Throwable e) {

        try {
            if (e == null) {
                return null;
            }
            else if (e instanceof InvocationTargetException) {
                return handleException(((InvocationTargetException) e).getTargetException());
            }
            else if (e instanceof AspectException) {
                return handleException(e.getCause());
            }
            else if (e instanceof AuthenticationException) {
                return doRedirect((SecurityException) e);
            }
            else if (e instanceof AuthorizationException) {
                final String[] authValues = getAuthValues();
                if (authValues == null || authValues[1].length() == 0) {
                    return doRedirect((SecurityException) e);
                }
                else {
                    ((SecurityException) e).setRedirectLocation(null);
                    return doDeclineHttpRequest((EscidocException) e);
                }
            }
            else if (e instanceof EscidocException) {
                return doDeclineHttpRequest((EscidocException) e);
            }
            else if (e instanceof UndeclaredThrowableException) {
                final Throwable undeclaredThrowable = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
                if (undeclaredThrowable.getClass().getName().equals(AuthenticationException.class.getName())) {
                    return doRedirect((SecurityException) undeclaredThrowable);
                }
                else {
                    return doDeclineHttpRequest(new WebserverSystemException(StringUtility.format(
                        "Undeclared throwable during method execution", undeclaredThrowable.getClass().getName()),
                        undeclaredThrowable));
                }
            }

            LOGGER.error(StringUtility.format("Caught exception cannot be handled, returning "
                + WebserverSystemException.class.getName() + " to client.", e.getClass().getName(), e.getMessage()), e);
            if (e.getMessage() != null) {
                return doDeclineHttpRequest(new WebserverSystemException(e.getMessage(), e));
            }
            else {
                return doDeclineHttpRequest(new WebserverSystemException(e.getClass().getName(), e));
            }
        }
        catch (IOException ioe) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        }
        catch (SystemException sye) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Redirect the user to the URL provided within the exception that causes this redirect.<br/>
     * This method extracts the values from the provided exception..
     *
     * @param exception
     *            The exception that causes the redirect.
     * @return Returns Exception as ResponseBuilder.
     * @throws IOException e
     * @throws SystemException e
     */
    private ResponseBuilder doRedirect(final SecurityException exception) throws IOException, SystemException {

        final String redirectLocation =
            exception.getRedirectLocation() + '?' + PARAM_TARGET + '='
                + URLEncoder.encode(httpRequest.getRequestURL().toString(), ENCODING);

        final int httpStatusCode = exception.getHttpStatusCode();

        ResponseBuilder responseBuilder = Response.status(httpStatusCode);
        initResponse(responseBuilder);
        responseBuilder.header(HTTP_HEADER_LOCATION, redirectLocation);
        if (exception.getClass().getName() != null) {
            responseBuilder.header(HEADER_ESCIDOC_EXCEPTION, exception.getClass().getName());
        }
        responseBuilder.entity(ExceptionTOFactory.generateExceptionTO(exception));
        return responseBuilder;
    }

    /**
     * Decline an incoming http request with given error code and message taken from the exception.
     * 
     * @param exception
     *            The exception.
     * @return Returns Exception as ResponseBuilder.
     * @throws IOException e
     * @throws SystemException e
     */
    private ResponseBuilder doDeclineHttpRequest(final EscidocException exception) throws IOException, SystemException {
        ResponseBuilder responseBuilder = Response.status(exception.getHttpStatusCode());
        initResponse(responseBuilder);
        responseBuilder.header(HEADER_ESCIDOC_EXCEPTION, exception.getClass().getName());
        if (exception instanceof SecurityException) {
            if (((SecurityException) exception).getRedirectLocation() != null) {
                responseBuilder.header(HTTP_HEADER_LOCATION, ((SecurityException) exception).getRedirectLocation());
            }
        }
        responseBuilder.entity(ExceptionTOFactory.generateExceptionTO(exception));
        return responseBuilder;
    }

    /**
     * Initializes the provided {@code ResponseBuilder} object to prevent caching of the response and to
     * specify the content-type.<br/>
     * The content-type is initialized to the value of XML_RESPONSE_CONTENT_TYPE.
     *
     * @param responseBuilder
     *            The {@code ResponseBuilder} object to that the no-cache headers shall be added.
     */
    private void initResponse(final ResponseBuilder responseBuilder) {
        responseBuilder.header(HTTP_HEADER_CACHE_CONTROL, HTTP_HEADER_VALUE_NO_CACHE);
        responseBuilder.header(HTTP_HEADER_PRAGMA, HTTP_HEADER_VALUE_NO_CACHE);
        responseBuilder.header(HTTP_HEADER_CONTENT_TYPE, XML_RESPONSE_CONTENT_TYPE);
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
     * @return Returns an {@code String} array with the user name value at the first position and the password at
     *         the second position.<br>
     *         If no Authorization header has been set, {@code null} is returned.
     * @throws IOException
     *             In case of an I/O error.
     */
    private String[] getAuthValues() throws IOException {

        // Authentication via browser cookie
        final Cookie cookie = getCookie(COOKIE_LOGIN);
        if (cookie != null) {
            final String handle = cookie.getValue();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Received handle in cookie: " + handle);
            }
            return new String[] { "ShibbolethUser", handle };
        }
        // Authentication via Auth-Header
        else if (httpRequest.getHeader("Authorization") != null && httpRequest.getHeader("Authorization").length() != 0) {
            String authHeader = httpRequest.getHeader("Authorization");
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
     * Get a cookie.
     * 
     * @param name
     *            The name of the cookie.
     * @return The cookie.
     */
    private Cookie getCookie(final String name) {
        Cookie result = null;
        final Cookie[] cookies = httpRequest.getCookies();
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
