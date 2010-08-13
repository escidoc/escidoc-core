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
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test.common.client.servlet;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.axis.utils.StringUtils;
import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.EscidocTestsBase;
import de.escidoc.core.test.common.resources.ResourceProvider;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Helper class providing executing of http requests.
 * 
 * @author TTE
 * 
 */
public final class HttpHelper {

    private static final String ESCIDOC_COOKIE = "escidocCookie";

    public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    public static final String HTTP_DEFAULT_CONTENT_TYPE = "text/xml";

    public static final String HTTP_DEFAULT_CHARSET = "UTF-8";

    /**
     * Private Constructor to prevent instantiation.
     */
    private HttpHelper() {
    }

    /**
     * Execute a http method.<br>
     * If neccessary, this method performs the login using valid login data of
     * an existing account.
     * 
     * @param method
     *            The http method.
     * @param url
     *            The url.
     * @param body
     *            The request body.
     * @param mimeType
     *            The MIME type.
     * @param filename
     *            The file name.
     * @param parameters
     *            The request parameters.
     * 
     * @return The resulting http method.
     * @throws Exception
     *             If anything fails.
     */
    public static HttpMethod executeHttpMethod(
        final String method, final String url, final Object body,
        final String mimeType, final String filename,
        final Map<String, String[]> parameters) throws Exception {

        return executeHttpMethod(new HttpClient(), method, url, body, mimeType,
            filename, parameters);
    }

    /**
     * Execute an http method.
     * 
     * @param client
     *            The http client.
     * @param method
     *            The http method.
     * @param url
     *            The url.
     * @param body
     *            The request body.
     * @param mimeType
     *            The MIME type.
     * @param filename
     *            The file name.
     * @param parameters
     *            The request parameters.
     * 
     * @return The resulting http method.
     * @throws Exception
     *             If anything fails.
     */
    public static HttpMethod executeHttpMethod(
        final HttpClient client, final String method, final String url,
        final Object body, final String mimeType, final String filename,
        final Map<String, String[]> parameters) throws Exception {
        HttpMethod result = null;
        if (method != null) {
            if (method.toUpperCase().equals(Constants.HTTP_METHOD_DELETE)) {
                result = doDelete(client, url);
            }
            else if (method.toUpperCase().equals(Constants.HTTP_METHOD_GET)) {
                result = doGet(client, url, parameters);
            }
            else if (method.toUpperCase().equals(Constants.HTTP_METHOD_POST)) {
                result = doPost(client, url, body, mimeType, filename);
            }
            else if (method.toUpperCase().equals(Constants.HTTP_METHOD_PUT)) {
                result = doPut(client, url, body, mimeType, filename);
            }
        }
        return result;
    }

    /**
     * Execute a http delete request on the given url.<br>
     * If neccessary, this method performs the login using the provided login
     * data. If login name is <code>null</code> or the password is
     * <code>null</code>, the login step is skipped.
     * 
     * @param client
     *            The http client.
     * @param url
     *            The url.
     * @return The resulting http method.
     * @throws Exception
     *             If anything fails.
     */
    public static DeleteMethod doDelete(
        final HttpClient client, final String url) throws Exception {

        final DeleteMethod method = new DeleteMethod(url);
        PWCallback.addEscidocUserHandleCokie(method);
        client.executeMethod(method);
        return method;
    }

    /**
     * Execute a http get request on the given url.<br>
     * If neccessary, this method performs the login using the provided login
     * data. If login name is <code>null</code> or the password is
     * <code>null</code>, the login step is skipped.
     * 
     * @param client
     *            The http client.
     * @param url
     *            The url.
     * @param parameters
     *            The request parameters.
     * 
     * @return The resulting http method.
     * @throws Exception
     *             If anything fails.
     */
    public static GetMethod doGet(
        final HttpClient client, final String url,
        final Map<String, String[]> parameters) throws Exception {

        final GetMethod method = new GetMethod(url);
        // redirects are handled by this implementation.
        method.setFollowRedirects(false);
        PWCallback.addEscidocUserHandleCokie(method);
        if (parameters != null) {
            List<NameValuePair> queryParameters =
                new ArrayList<NameValuePair>();

            for (String parameter : parameters.keySet()) {
                for (String value : parameters.get(parameter)) {
                    queryParameters.add(new NameValuePair(parameter, value));
                }
            }
            method
                .setQueryString(queryParameters.toArray(new NameValuePair[0]));
        }
        client.executeMethod(method);
        return method;
    }

    /**
     * Execute a http post request on the given url.<br>
     * If neccessary, this method performs the login using the provided login
     * data. If login name is <code>null</code> or the password is
     * <code>null</code>, the login step is skipped.
     * 
     * @param client
     *            The http client.
     * @param url
     *            The url.
     * @param body
     *            The request body.
     * @param mimeType
     *            The mime type of the data, in case of binary content.
     * @param filename
     *            The name of the file, in case of binary content.
     * @return The resulting http method.
     * @throws Exception
     *             If anything fails.
     */
    public static PostMethod doPost(
        final HttpClient client, final String url, final Object body,
        final String mimeType, final String filename) throws Exception {
        final PostMethod method = new PostMethod(url);
        RequestEntity requestEntity = null;
        if (body instanceof String) {
            requestEntity =
                new StringRequestEntity((String) body,
                    HTTP_DEFAULT_CONTENT_TYPE, HTTP_DEFAULT_CHARSET);
            // old client: method.setRequestBody((String) body);
        }
        else if (body instanceof NameValuePair[]) {
            method.setRequestBody((NameValuePair[]) body);
        }
        else if (body instanceof InputStream) {
            requestEntity = new InputStreamRequestEntity((InputStream) body);
            // old client: method.setRequestBody((InputStream) body);
            method.setRequestHeader("Content-Type", mimeType);
            // FIXME: handle filename?
        }
        method.setRequestEntity(requestEntity);
        PWCallback.addEscidocUserHandleCokie(method);
        client.executeMethod(method);
        return method;
    }

    /**
     * Execute a http put request on the given url.<br>
     * If neccessary, this method performs the login using the provided login
     * data. If login name is <code>null</code> or the password is
     * <code>null</code>, the login step is skipped.
     * 
     * @param client
     *            The http client.
     * @param url
     *            The url.
     * @param body
     *            The request body.
     * @param mimeType
     *            The mime type of the data, in case of binary content.
     * @param filename
     *            The name of the file, in case of binary content.
     * @return The resulting http method.
     * @throws Exception
     *             If anything fails.
     */
    public static PutMethod doPut(
        final HttpClient client, final String url, final Object body,
        final String mimeType, final String filename) throws Exception {
        final PutMethod method = new PutMethod(url);
        RequestEntity requestEntity = null;
        if (body instanceof String) {
            requestEntity =
                new StringRequestEntity((String) body,
                    HTTP_DEFAULT_CONTENT_TYPE, HTTP_DEFAULT_CHARSET);
            // old client: method.setRequestBody((String) body);
        }
        else if (body instanceof InputStream) {
            requestEntity = new InputStreamRequestEntity((InputStream) body);
            // old client: method.setRequestBody((InputStream) body);
            method.setRequestHeader(HTTP_HEADER_CONTENT_TYPE, mimeType);
            // FIXME: handle filename?
        }
        method.setRequestEntity(requestEntity);
        PWCallback.addEscidocUserHandleCokie(method);
        client.executeMethod(method);
        return method;
    }

    /**
     * Get a cookie from the http client.
     * 
     * @param client
     *            The http client.
     * @return The cookie.
     */
    public static Cookie getCookie(final HttpClient client) {

        // FIXME: this needs rework as the cookie support is disabled for the
        // client.
        Cookie result = null;
        final Cookie[] cookies = client.getState().getCookies();
        for (int i = 0; i < cookies.length; ++i) {
            final Cookie cookie = cookies[0];
            if (cookie.getName().equals(ESCIDOC_COOKIE)) {
                result = cookie;
                break;
            }
        }
        return result;
    }

    /**
     * Create an url.
     * 
     * @param protocol
     *            The protocol.
     * @param host
     *            The host.
     * @return The resulting url.
     */
    private static String createUrl(final String protocol, final String host) {
        String result = protocol;
        if (result.indexOf("://") == -1) {
            result += "://";
        }
        result += host;
        if (result.endsWith("/")) {
            result += result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * Create an url.
     * 
     * @param protocol
     *            The protocol.
     * @param host
     *            The host.
     * @param baseUri
     *            The base uri.
     * @return The resulting url.
     */
    public static String createUrl(
        final String protocol, final String host, final String baseUri) {

        String result = createUrl(protocol, host);
        if (baseUri != null) {
            result = concatUrl(result, baseUri);
        }
        else {
            result += "/";
        }
        return result;
    }

    /**
     * Create an url.
     * 
     * @param protocol
     *            The protocol.
     * @param host
     *            The host.
     * @param baseUri
     *            The base uri.
     * @param pathElements
     *            The elements describing the path to the (sub) resource: id,
     *            subresourceName1, subresourceId2, ...
     * @return The resulting url.
     */
    public static String createUrl(
        final String protocol, final String host, final String baseUri,
        final String[] pathElements) {

        String result = createUrl(protocol, host, baseUri);
        for (int i = 0; i < pathElements.length; i++) {
            final String string = pathElements[i];
            if (string != null) {
                result = concatUrl(result, string);
            }
            else {
                result += "/";
            }
        }
        return result;
    }

    /**
     * Create an url.
     * 
     * @param protocol
     *            The protocol.
     * @param host
     *            The host.
     * @param baseUri
     *            The base uri.
     * @param pathElements
     *            Elements of path
     * @param paramter
     *            The parameter.
     * @param encodeParam
     *            set true if parameter are to encode
     * @return The resulting url.
     */
    public static String createUrl(
        final String protocol, final String host, final String baseUri,
        final String[] pathElements, final String parameter,
        final boolean encodeParam) {

        String result = createUrl(protocol, host, baseUri);
        for (int i = 0; i < pathElements.length; i++) {
            final String string = pathElements[i];
            if (string != null) {
                result = concatUrl(result, string);
            }
            else {
                result += "/";
            }
        }

        if (parameter != null) {
            if (result.endsWith("/")) {
                result = result.substring(0, result.length() - 1);
            }
            result += parameter;
        }
        return result;
    }

    /**
     * Create an url.
     * 
     * @param protocol
     *            The protocol.
     * @param host
     *            The host.
     * @param baseUri
     *            The base uri.
     * @param id
     *            The id to append.
     * @return The resulting url.
     */
    public static String createUrl(
        final String protocol, final String host, final String baseUri,
        final String id) {

        return createUrl(protocol, host, baseUri, new String[] { id });
    }

    /**
     * Create an url.
     * 
     * @param protocol
     *            The protocol.
     * @param host
     *            The host.
     * @param baseUri
     *            The base uri.
     * @param id
     *            The id to append.
     * @param subResourceName
     *            the sub resource name.
     * @return The resulting url.
     */
    public static String createUrl(
        final String protocol, final String host, final String baseUri,
        final String id, final String subResourceName) {

        return createUrl(protocol, host, baseUri, new String[] { id,
            subResourceName });
    }

    public static String createUrl(
        final String protocol, final String host, final String baseUri,
        final String id, final String subResourceName, final String parameter,
        final boolean encodeParam) {

        return createUrl(protocol, host, baseUri, new String[] { id,
            subResourceName }, parameter, encodeParam);
    }

    /**
     * Create an url.
     * 
     * @param protocol
     *            The protocol.
     * @param host
     *            The host.
     * @param baseUri
     *            The base uri.
     * @param id
     *            The id to append.
     * @param subResourceName
     *            the sub resource name.
     * @param subResourceId
     *            the sub resource id to append.
     * @return The resulting url.
     */
    public static String createUrl(
        final String protocol, final String host, final String baseUri,
        final String id, final String subResourceName,
        final String subResourceId) {

        return createUrl(protocol, host, baseUri, new String[] { id,
            subResourceName, subResourceId });
    }

    /**
     * Add a part to the given url.
     * 
     * @param url
     *            The url.
     * @param append
     *            The part to append to the url.
     * @return The resulting url.
     */
    public static String concatUrl(final String url, final String append) {

        String result = url;
        if (append.startsWith("/")) {
            result += append;
        }
        else {
            result += "/" + append;
        }
        // if (!result.endsWith("/")) {
        // if (!append.startsWith("/")) {
        // result += "/" + append;
        // }
        // else {
        // result += append;
        // }
        // }
        // else {
        // if (!append.startsWith("/")) {
        // result += append;
        // }
        // else {
        // result += append.substring(1);
        // }
        // }
        return result;

    }

    /**
     * Add a parameter to the given url.
     * 
     * @param url
     *            The url.
     * @param param
     *            The name of the parameter.
     * @param value
     *            The value of teh parameter.
     * @return The url containing the new parameter.
     */
    public static String addParam(
        final String url, final String param, final String value) {
        String result = url;
        if (result.indexOf("?") == -1) {
            result += "?";
        }
        else {
            result += "&";
        }
        result += param + "=" + value;
        return result;
    }

    /**
     * Performs the login.<br>
     * The provided values must not be <code>null</code>.
     * 
     * @param client
     *            The http client to use.
     * @param login
     *            The login name.
     * @param password
     *            The password.
     * @param expectedAuthenticationFailure
     *            Flag indicating that the provided values should cause a failed
     *            authentication, i.e. login page will be presented to the user
     *            as the result.
     * @param accountIsDeactivated
     *            Flag indicating that the authenticated user account should be
     *            deactivated.
     * @param targetUrl
     *            The target url to that the user shall be redirected.
     * @param encodeTargetUrlSlashes
     *            Flag indicating that the slashes contained in the targetUrl
     *            shall be encoded (<code>true</code>) or shall not be encoded (
     *            <code>false</code>).
     * @return Returns the http method holding the result of the login, either
     *         the redirect to the target, the redirect to repeated login or the
     *         'Deactivated User Account' page.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public static HttpMethod performLogin(
        final HttpClient client, final String login, final String password,
        final boolean expectedAuthenticationFailure,
        final boolean accountIsDeactivated, final String targetUrl,
        final boolean encodeTargetUrlSlashes) throws Exception {

        if (login == null || password == null) {
            throw new IllegalArgumentException(
                "login name and password must be provided.");
        }

        // cookies aware
        final String savedCookiePolicy = client.getParams().getCookiePolicy();
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

        // clear cookies in order to perform complete login
        client.getState().clearCookies();

        try {
            final String loginServletUrl =
                "http://" + Constants.HOST_PORT + "/aa/login?target="
                    + encodeUrlParameter(targetUrl, encodeTargetUrlSlashes);
            final HttpMethod loginMethod = new GetMethod((loginServletUrl));
            int status = client.executeMethod(loginMethod);

            // spring security filter will redirect to login form as no login
            // parameters are sent
            final String responseBody =
                ResourceProvider.getContentsFromInputStream(loginMethod
                    .getResponseBodyAsStream());
            TestCase.assertEquals(
                "Unexpected status of LoginServlet response, ",
                HttpServletResponse.SC_OK, status);
            TestCase.assertNotNull("No response body received, ", responseBody);
            TestCase.assertTrue("Response does not contain the expected login"
                + " page. ",
                responseBody.indexOf("<input type=\"password\"") != -1);

            // Second step: Send filled login form
            final PostMethod postMethod =
                new PostMethod(
                    ("http://" + Constants.HOST_PORT + "/aa/j_spring_security_check"));
            final NameValuePair[] loginParams =
                new NameValuePair[] {
                    new NameValuePair(Constants.PARAM_UM_LOGIN_NAME, login),
                    new NameValuePair(Constants.PARAM_UM_LOGIN_PASSWORD,
                        password), };
            postMethod.setRequestBody(loginParams);
            status = client.executeMethod(postMethod);

            // spring security filter will either redirect to login servlet or
            // to repeated login form
            final Header loctionHeader =
                postMethod.getResponseHeader("Location");
            TestCase.assertEquals("No redirect received",
                HttpStatus.SC_MOVED_TEMPORARILY, status);
            TestCase.assertNotNull("No location header received. ",
                loctionHeader);
            final String retrievedRedirectUrl = loctionHeader.getValue();

            // assert redirect
            if (expectedAuthenticationFailure) {
                // redirect to repeated login page
                TestCase
                    .assertEquals(
                        "Unexpected redirect from spring security after expected"
                            + " failed authentication", "http://"
                            + Constants.HOST_PORT
                            + "/aa/login/login-repeated.html",
                        retrievedRedirectUrl);
                return postMethod;
            }
            else {
                // correct values have been sent, redirected to login servlet.
                // Follow redirect
                TestCase.assertEquals(
                    "Wrong redirect, expected redirect to login servlet",
                    loginServletUrl, retrievedRedirectUrl);
                final HttpMethod redirectMethod =
                    new PostMethod(retrievedRedirectUrl);
                status = client.executeMethod(redirectMethod);

                if (accountIsDeactivated) {
                    // correct values have been sent, user account is deactived.
                    // page with info about deactivated account is presented
                    EscidocRestSoapTestsBase
                        .assertHttpStatus(
                            "Wrong status for expected 'Deactivated User Account' page.",
                            HttpServletResponse.SC_OK, redirectMethod);
                    TestCase.assertNull(redirectMethod
                        .getResponseHeader("Location"));
                    // FIXME: add assertion for page content
                    final String deactivatedUserAccountPageBody =
                        ResourceProvider
                            .getContentsFromInputStream(redirectMethod
                                .getResponseBodyAsStream());
                    TestCase.assertNotNull("No response body received, ",
                        deactivatedUserAccountPageBody);
                    TestCase
                        .assertTrue(
                            "Response does not contain the expected information"
                                + " about deactivated account page. ",
                            deactivatedUserAccountPageBody
                                .indexOf("Your account has been deactivated") != -1);

                    return redirectMethod;
                }
                else {
                    // user account is active, login servlet creates user handle
                    // and redirects to target
                    if (!StringUtils.isEmpty(targetUrl)) {
                        EscidocRestSoapTestsBase.assertHttpStatus("",
                                HttpServletResponse.SC_SEE_OTHER, redirectMethod);
                        TestCase.assertNotNull(redirectMethod
                                .getResponseHeader("Location"));
                    } else {
                        EscidocRestSoapTestsBase.assertHttpStatus("",
                                HttpServletResponse.SC_OK, redirectMethod);
                    }
                    TestCase.assertNotNull(redirectMethod
                        .getResponseHeader("Set-Cookie"));

                    return redirectMethod;
                }
            }
        }
        finally {
            client.getParams().setCookiePolicy(savedCookiePolicy);
        }
    }

    /**
     * Performs the logout.<br>
     * The provided values must not be <code>null</code>.
     * 
     * @param client
     *            The http client to use.
     * @param targetUrl
     *            The url to that the user shall be redirected after logout.
     *            This may be <code>null</code> indicating no redirect shall
     *            occur.
     * @param userHandle
     *            The eSciDOc user handle that shall be sent in the cookie of
     *            the logout request. If this is <code>null</code>, no eSciDOc
     *            cookie holding a handle is sent in the logout request.
     * @param encodeTargetUrlSlashes
     *            Flag indicating that the slashes contained in the targetUrl
     *            shall be encoded (<code>true</code>) or shall not be encoded (
     *            <code>false</code>).
     * @return Returns the http method holding the result of the logout, either
     *         the redirect to the target or just the 'Logged Out' page.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public static HttpMethod performLogout(
        final HttpClient client, final String targetUrl,
        final String userHandle, final boolean encodeTargetUrlSlashes)
        throws Exception {

        // cookies aware
        final String savedCookiePolicy = client.getParams().getCookiePolicy();
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);

        // do not follow redirects
        try {

            // first step: Call the login servlet
            // FIXME: removed fixed URL
            final String logoutServletUrl;
            if (targetUrl == null) {
                logoutServletUrl =
                    "http://" + Constants.HOST_PORT + "/aa/logout";
            }
            else {
                logoutServletUrl =
                    "http://" + Constants.HOST_PORT + "/aa/logout?target="
                        + encodeUrlParameter(targetUrl, encodeTargetUrlSlashes);
            }
            final HttpMethod logoutMethod = new GetMethod((logoutServletUrl));
            logoutMethod.setFollowRedirects(false);
            if (userHandle == null) {
                client.getState().addCookie(
                    new Cookie(Constants.HOST, ESCIDOC_COOKIE, null, "/", 0,
                        false));
            }
            else {
                client.getState().addCookie(
                    new Cookie(Constants.HOST, ESCIDOC_COOKIE, userHandle, "/",
                        -1, false));
            }

            client.executeMethod(logoutMethod);

            return logoutMethod;
        }
        finally {
            client.getParams().setCookiePolicy(savedCookiePolicy);
        }
    }

    /**
     * Encode the provided url parameter value.
     * 
     * @param parameterValue
     *            The Url parameter value.
     * @param encodeSlashes
     *            Flag indicating that the slashes contained in the parameter
     *            shall be encoded (<code>true</code>) or shall not be encoded (
     *            <code>false</code>).
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String encodeUrlParameter(
        final String parameterValue, final boolean encodeSlashes)
        throws UnsupportedEncodingException {

        final String encoded;
        if (encodeSlashes) {
            encoded =
                URLEncoder.encode(parameterValue,
                    EscidocTestsBase.DEFAULT_CHARSET);

        }
        else {
            encoded =
                URLEncoder.encode(parameterValue,
                    EscidocTestsBase.DEFAULT_CHARSET).replaceAll("%2F", "/");
        }
        return encoded;
    }
}
