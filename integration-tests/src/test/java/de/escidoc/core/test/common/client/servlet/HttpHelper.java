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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.RequestAddCookies;
import org.apache.http.client.protocol.ResponseProcessCookies;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.esidoc.core.utils.io.MimeTypes;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.common.resources.PropertiesProvider;
import de.escidoc.core.test.common.resources.ResourceProvider;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Helper class providing executing of http requests.
 * 
 * @author Torsten Tetteroo
 */
public final class HttpHelper {

    private static final String ESCIDOC_COOKIE = "escidocCookie";

    public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";

    public static final String HTTP_DEFAULT_CHARSET = "UTF-8";

    /**
     * Private Constructor to prevent instantiation.
     */
    private HttpHelper() {
    }

    /**
     * Execute a http method.<br>
     * If neccessary, this method performs the login using valid login data of an existing account.
     * 
     * @param method
     *            The http method.
     * @param url
     *            The url.
     * @param body
     *            The request body.
     * @param mimeType
     *            The MIME type.
     * @param parameters
     *            The request parameters.
     * @return The resulting http method.
     * @throws Exception
     *             If anything fails.
     */
    public static HttpResponse executeHttpRequest(
        final String method, final String url, final Object body, final String mimeType,
        final Map<String, String[]> parameters) throws Exception {
        return executeHttpRequest(new DefaultHttpClient(), method, url, body, mimeType, parameters);
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
     * @param parameters
     *            The request parameters.
     * @return The http Response.
     * @throws Exception
     *             If anything fails.
     */
    public static HttpResponse executeHttpRequest(
        DefaultHttpClient client, final String method, final String url, final Object body, final String mimeType,
        final Map<String, String[]> parameters) throws Exception {
        HttpResponse result = null;

        if (client == null) {
            client = new DefaultHttpClient();
        }

        if (method != null) {

            /**
             * HTTP authentication added to the client since the introduction of HTTP authentication for the JBoss JMX-
             * and Web-Console.
             */
            final String jBossUser = PropertiesProvider.getInstance().getProperty(PropertiesProvider.JBOSS_AUTH_BASIC_USER);
            final String jBossPass = PropertiesProvider.getInstance().getProperty(PropertiesProvider.JBOSS_AUTH_BASIC_PASS);
            if (jBossUser != null && jBossPass != null) {
                final CredentialsProvider credsProvider = new BasicCredentialsProvider();
                final Credentials creds = new UsernamePasswordCredentials(jBossUser, jBossPass);

                credsProvider.setCredentials(new AuthScope(EscidocTestBase.getFrameworkHost(), new Integer(
                    EscidocTestBase.getFrameworkPort()).intValue()), creds);
                client.setCredentialsProvider(credsProvider);
            }

            if (method.toUpperCase(Locale.ENGLISH).equals(Constants.HTTP_METHOD_DELETE)) {
                result = doDelete(client, url);
            }
            else if (method.toUpperCase(Locale.ENGLISH).equals(Constants.HTTP_METHOD_GET)) {
                result = doGet(client, url, parameters);
            }
            else if (method.toUpperCase(Locale.ENGLISH).equals(Constants.HTTP_METHOD_POST)) {
                result = doPost(client, url, body, mimeType);
            }
            else if (method.toUpperCase(Locale.ENGLISH).equals(Constants.HTTP_METHOD_PUT)) {
                result = doPut(client, url, body, mimeType);
            }
        }
        return result;
    }

    /**
     * Execute a http delete request on the given url.<br>
     * If neccessary, this method performs the login using the provided login data. If login name is <code>null</code>
     * or the password is <code>null</code>, the login step is skipped.
     * 
     * @param client
     *            The http client.
     * @param url
     *            The url.
     * @return The resulting http method.
     * @throws Exception
     *             If anything fails.
     */
    public static HttpResponse doDelete(final HttpClient client, final String url) throws Exception {

        final HttpDelete method = new HttpDelete(url);
        PWCallback.addEscidocUserHandleCokie(method);
        final HttpResponse httpRes = client.execute(method);
        return httpRes;
    }

    /**
     * Execute a http get request on the given url.<br>
     * If neccessary, this method performs the login using the provided login data. If login name is <code>null</code>
     * or the password is <code>null</code>, the login step is skipped.
     * 
     * @param client
     *            The http client.
     * @param url
     *            The url.
     * @param parameters
     *            The request parameters.
     * @return The resulting http method.
     * @throws Exception
     *             If anything fails.
     */
    public static HttpResponse doGet(final HttpClient client, final String url, final Map<String, String[]> parameters)
        throws Exception {

        final HttpGet httpGet;

        if (parameters != null) {

            final List<NameValuePair> queryParameters = new ArrayList<NameValuePair>();

            for (final String parameter : parameters.keySet()) {
                for (final String value : parameters.get(parameter)) {
                    queryParameters.add(new BasicNameValuePair(parameter, value));
                }
            }

            final String formatted = URLEncodedUtils.format(queryParameters, "UTF-8");

            httpGet = new HttpGet(url + "?" + formatted);
        }
        else {
            httpGet = new HttpGet(url);
        }

        PWCallback.addEscidocUserHandleCokie(httpGet);
        final HttpResponse httpRes = client.execute(httpGet);
        return httpRes;
    }

    /**
     * Execute a http post request on the given url.<br>
     * If neccessary, this method performs the login using the provided login data. If login name is <code>null</code>
     * or the password is <code>null</code>, the login step is skipped.
     * 
     * @param httpClient
     *            The http client.
     * @param url
     *            The url.
     * @param body
     *            The request body.
     * @param mimeType
     *            The mime type of the data, in case of binary content. The name of the file, in case of binary content.
     * @return The resulting http method.
     * @throws Exception
     *             If anything fails.
     */
    public static HttpResponse doPost(
        final DefaultHttpClient httpClient, final String url, final Object body, final String mimeType)
        throws Exception {
        final HttpPost httpPost = new HttpPost(url);
        HttpEntity requestEntity = null;
        if (body instanceof String) {
            requestEntity = new StringEntity((String) body, MimeTypes.TEXT_XML, HTTP_DEFAULT_CHARSET);
        }
        else if (body instanceof InputStream) {
            requestEntity = new InputStreamEntity((InputStream) body, -1);
            // old client: method.setRequestBody((InputStream) body);
            httpPost.setHeader("Content-Type", mimeType);
            // FIXME: handle filename?
        }
        httpPost.setEntity(requestEntity);
        PWCallback.addEscidocUserHandleCokie(httpPost);
        // no Cookies
        httpClient.removeRequestInterceptorByClass(RequestAddCookies.class);
        httpClient.removeResponseInterceptorByClass(ResponseProcessCookies.class);

        final HttpResponse httpRes = httpClient.execute(httpPost);
        return httpRes;
    }

    /**
     * Execute a http put request on the given url.<br>
     * If neccessary, this method performs the login using the provided login data. If login name is <code>null</code>
     * or the password is <code>null</code>, the login step is skipped.
     * 
     * @param client
     *            The http client.
     * @param url
     *            The url.
     * @param body
     *            The request body.
     * @param mimeType
     *            The mime type of the data, in case of binary content.
     * @return The resulting http method.
     * @throws Exception
     *             If anything fails.
     */
    public static HttpResponse doPut(
        final DefaultHttpClient client, final String url, final Object body, final String mimeType) throws Exception {
        final HttpPut httpPut = new HttpPut(url);
        HttpEntity requestEntity = null;
        if (body instanceof String) {
            requestEntity = new StringEntity((String) body, MimeTypes.TEXT_XML, HTTP_DEFAULT_CHARSET);

        }
        else if (body instanceof InputStream) {
            requestEntity = new InputStreamEntity(((InputStream) body), -1);
            httpPut.setHeader(HTTP_HEADER_CONTENT_TYPE, mimeType);
        }
        httpPut.setEntity(requestEntity);
        PWCallback.addEscidocUserHandleCokie(httpPut);
        final HttpResponse httpRes = client.execute(httpPut);
        return httpRes;
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
        final List<Cookie> cookies = ((DefaultHttpClient) client).getCookieStore().getCookies();
        final ListIterator<Cookie> iter = cookies.listIterator();
        while (iter.hasNext()) {
            final Cookie cookie = iter.next();
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
    public static String createUrl(final String protocol, final String host, final String baseUri) {

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
     *            The elements describing the path to the (sub) resource: id, subresourceName1, subresourceId2, ...
     * @return The resulting url.
     */
    public static String createUrl(
        final String protocol, final String host, final String baseUri, final String[] pathElements) {

        String result = createUrl(protocol, host, baseUri);
        for (final String string : pathElements) {
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
     * @param parameter
     *            The parameter.
     * @param encodeParam
     *            set true if parameter are to encode
     * @return The resulting url.
     */
    public static String createUrl(
        final String protocol, final String host, final String baseUri, final String[] pathElements,
        final String parameter, final boolean encodeParam) {

        String result = createUrl(protocol, host, baseUri);
        for (final String string : pathElements) {
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
    public static String createUrl(final String protocol, final String host, final String baseUri, final String id) {

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
        final String protocol, final String host, final String baseUri, final String id, final String subResourceName) {

        return createUrl(protocol, host, baseUri, new String[] { id, subResourceName });
    }

    public static String createUrl(
        final String protocol, final String host, final String baseUri, final String id, final String subResourceName,
        final String parameter, final boolean encodeParam) {

        return createUrl(protocol, host, baseUri, new String[] { id, subResourceName }, parameter, encodeParam);
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
        final String protocol, final String host, final String baseUri, final String id, final String subResourceName,
        final String subResourceId) {

        return createUrl(protocol, host, baseUri, new String[] { id, subResourceName, subResourceId });
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
    public static String addParam(final String url, final String param, final String value) {
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
     *            Flag indicating that the provided values should cause a failed authentication, i.e. login page will be
     *            presented to the user as the result.
     * @param accountIsDeactivated
     *            Flag indicating that the authenticated user account should be deactivated.
     * @param targetUrl
     *            The target url to that the user shall be redirected.
     * @param encodeTargetUrlSlashes
     *            Flag indicating that the slashes contained in the targetUrl shall be encoded (<code>true</code>) or
     *            shall not be encoded ( <code>false</code>).
     * @return Returns the http method holding the result of the login, either the redirect to the target, the redirect
     *         to repeated login or the 'Deactivated User Account' page.
     * @throws Exception
     *             If anything fails.
     */
    public static HttpResponse performLogin(
        final DefaultHttpClient client, final String login, final String password,
        final boolean expectedAuthenticationFailure, final boolean accountIsDeactivated, final String targetUrl,
        final boolean encodeTargetUrlSlashes) throws Exception {

        if (login == null || password == null) {
            throw new IllegalArgumentException("login name and password must be provided.");
        }
        HttpResponse httpRes = null;

        // clear cookies in order to perform complete login
        client.getCookieStore().clear();

        final String loginServletUrl =
            EscidocTestBase.getFrameworkUrl() + "/aa/login?target="
                + encodeUrlParameter(targetUrl, encodeTargetUrlSlashes);
        final HttpGet loginMethod = new HttpGet(loginServletUrl);
        httpRes = client.execute(loginMethod);
        int status = httpRes.getStatusLine().getStatusCode();
        assertEquals("...", HttpServletResponse.SC_MOVED_TEMPORARILY, status);
        final Header location = httpRes.getFirstHeader("Location");
        assertNotNull("No location header received. ", location);
        final HttpGet gMethod = new HttpGet(location.getValue());
        httpRes = client.execute(gMethod);
        status = httpRes.getStatusLine().getStatusCode();
        // spring security filter will redirect to login form as no login
        // parameters are sent
        // TODO mare ResourceProvider.getContentsFromInputStream noch nötig?
        final String responseBody = ResourceProvider.getContentsFromInputStream(httpRes.getEntity().getContent());
        assertEquals("Unexpected status of LoginServlet response, ", HttpServletResponse.SC_OK, status);
        assertNotNull("No response body received, ", responseBody);
        assertTrue("Response does not contain the expected login" + " page. ", responseBody
            .indexOf("<input type=\"password\"") != -1);

        // Second step: Send filled login form
        final HttpPost postMethod = new HttpPost((EscidocTestBase.getFrameworkUrl() + "/aa/j_spring_security_check"));

        final List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair(Constants.PARAM_UM_LOGIN_NAME, login));
        formparams.add(new BasicNameValuePair(Constants.PARAM_UM_LOGIN_PASSWORD, password));
        final UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, HTTP.UTF_8);
        postMethod.setEntity(entity);

        httpRes = client.execute(postMethod);
        status = httpRes.getStatusLine().getStatusCode();
        // spring security filter will either redirect to login servlet or
        // to repeated login form
        final Header locationHeader = httpRes.getFirstHeader("Location");
        assertEquals("No redirect received", HttpStatus.SC_MOVED_TEMPORARILY, status);
        assertNotNull("No location header received. ", locationHeader);

        final String retrievedRedirectUrl = locationHeader.getValue();

        // assert redirect
        if (expectedAuthenticationFailure) {
            // redirect to repeated login page
            assertEquals("Unexpected redirect from spring security after expected" + " failed authentication",
                EscidocTestBase.getFrameworkUrl() + "/aa/login/login-repeated.html", retrievedRedirectUrl);
            return httpRes;
        }
        else {
            // correct values have been sent, redirected to login servlet.
            // Follow redirect
            assertEquals("Wrong redirect, expected redirect to login servlet", loginServletUrl, retrievedRedirectUrl);
            final HttpPost redirectMethod = new HttpPost(retrievedRedirectUrl);
            httpRes = client.execute(redirectMethod);

            if (accountIsDeactivated) {
                // correct values have been sent, user account is deactived.
                // page with info about deactivated account is presented
                EscidocAbstractTest.assertHttpStatus("Wrong status for expected 'Deactivated User Account' page.",
                    HttpServletResponse.SC_OK, httpRes);
                assertNull(httpRes.getFirstHeader("Location"));

                // FIXME: add assertion for page content
                final String deactivatedUserAccountPageBody =
                    ResourceProvider.getContentsFromInputStream(httpRes.getEntity().getContent());
                assertNotNull("No response body received, ", deactivatedUserAccountPageBody);
                assertTrue("Response does not contain the expected information" + " about deactivated account page. ",
                    deactivatedUserAccountPageBody.indexOf("Your account has been deactivated") != -1);

                return httpRes;
            }
            else {
                // user account is active, login servlet creates user handle
                // and redirects to target
                if (targetUrl != null && targetUrl.length() > 0) {
                    EscidocAbstractTest.assertHttpStatus("", HttpServletResponse.SC_SEE_OTHER, httpRes);
                    assertNotNull(httpRes.getFirstHeader("Location"));
                }
                else {
                    EscidocAbstractTest.assertHttpStatus("", HttpServletResponse.SC_OK, httpRes);
                }
                assertNotNull(httpRes.getFirstHeader("Set-Cookie"));

                return httpRes;
            }
        }

    }

    /**
     * Performs the logout.<br>
     * The provided values must not be <code>null</code>.
     * 
     * @param client
     *            The http client to use.
     * @param targetUrl
     *            The url to that the user shall be redirected after logout. This may be <code>null</code> indicating no
     *            redirect shall occur.
     * @param userHandle
     *            The eSciDOc user handle that shall be sent in the cookie of the logout request. If this is
     *            <code>null</code>, no eSciDOc cookie holding a handle is sent in the logout request.
     * @param encodeTargetUrlSlashes
     *            Flag indicating that the slashes contained in the targetUrl shall be encoded (<code>true</code>) or
     *            shall not be encoded ( <code>false</code>).
     * @return Returns the http method holding the result of the logout, either the redirect to the target or just the
     *         'Logged Out' page.
     * @throws Exception
     *             If anything fails.
     */
    public static HttpResponse performLogout(
        final DefaultHttpClient client, final String targetUrl, final String userHandle,
        final boolean encodeTargetUrlSlashes) throws Exception {

        // cookies aware
        final String savedCookiePolicy = (String) client.getParams().getParameter(ClientPNames.COOKIE_POLICY);
        client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

        // do not follow redirects
        try {

            // first step: Call the login servlet
            final String logoutServletUrl;
            if (targetUrl == null) {
                logoutServletUrl = EscidocTestBase.getFrameworkUrl() + "/aa/logout";
            }
            else {
                logoutServletUrl =
                    EscidocTestBase.getFrameworkUrl() + "/aa/logout?target="
                        + encodeUrlParameter(targetUrl, encodeTargetUrlSlashes);
            }
            final HttpGet logoutMethod = new HttpGet(logoutServletUrl);
            // handled automatically in new Httpclient
            // logoutMethod.setFollowRedirects(false);
            final BasicClientCookie cookie = new BasicClientCookie(ESCIDOC_COOKIE, null);

            if (userHandle == null) {

                cookie.setDomain(EscidocTestBase.getFrameworkHost());
                cookie.setPath("/");

                cookie.setSecure(false);
                client.getCookieStore().addCookie(cookie);

            }
            // String domain, String name, String value, String path, int maxAge, boolean secure
            else {
                cookie.setValue(userHandle);
                cookie.setDomain(EscidocTestBase.getFrameworkHost());
                cookie.setPath("/");

                cookie.setSecure(false);
                client.getCookieStore().addCookie(cookie);
            }

            return client.execute(logoutMethod);

        }
        finally {
            client.getParams().setParameter(ClientPNames.COOKIE_POLICY, savedCookiePolicy);
        }
    }

    /**
     * Encode the provided url parameter value.
     * 
     * @param parameterValue
     *            The Url parameter value.
     * @param encodeSlashes
     *            Flag indicating that the slashes contained in the parameter shall be encoded (<code>true</code>) or
     *            shall not be encoded ( <code>false</code>).
     */
    private static String encodeUrlParameter(final String parameterValue, final boolean encodeSlashes)
        throws UnsupportedEncodingException {

        final String encoded;
        if (encodeSlashes) {
            encoded = URLEncoder.encode(parameterValue, EscidocTestBase.DEFAULT_CHARSET);

        }
        else {
            encoded = URLEncoder.encode(parameterValue, EscidocTestBase.DEFAULT_CHARSET).replaceAll("%2F", "/");
        }
        return encoded;
    }
}
