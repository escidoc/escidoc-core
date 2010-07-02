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
package de.escidoc.core.common.util.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * An utility class for HTTP requests.<br />
 * This class uses pooled HTTP connections.
 * 
 * @author SWA
 * 
 * @spring.bean id="escidoc.core.common.util.service.ConnectionUtility"
 *              scope="singleton"
 */
public class ConnectionUtility {

    private static final AppLogger LOG =
        new AppLogger(ConnectionUtility.class.getName());

    private static final int HTTP_MAX_CONNECTIONS_PER_HOST = 30;

    private static final int HTTP_MAX_TOTAL_CONNECTIONS_FACTOR = 3;

    private static final int HTTP_RESPONSE_CLASS = 100;

    private int timeout = -1;

    private HttpClient httpClient = null;
    
    private ProxyHost proxyHost = null;
    
    private boolean proxyConfigured = false;

    private MultiThreadedHttpConnectionManager cm =
        new MultiThreadedHttpConnectionManager();

    /**
     * Get a response-string for the URL. If the URL contains an Authentication part
     * then is this used and stored for this connection. Be aware to reset the
     * authentication if the user name and password should not be reused for
     * later connection.
     * 
     * @param url
     *            The resource URL.
     * @return String response as String.
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public String getRequestURLAsString(final URL url)
        throws WebserverSystemException {

        GetMethod method = getRequestURL(url);
        return readResponse(method);
    }

    /**
     * Get a response-string for the URL. The username and password is
     * stored for this connection. Later connection to same URL doesn't require
     * to set the authentication again. Be aware that this could lead to an
     * security issue! To avoid reuse reset the authentication for the URL.
     * 
     * @param url
     *            The resource URL.
     * @param username
     *            User name for authentication.
     * @param password
     *            Password for authentication.
     * @return String response as String.
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public String getRequestURLAsString(
                            final URL url, 
                            final String username, 
                            final String password)
        throws WebserverSystemException {

        GetMethod method = getRequestURL(url, username, password);
        return readResponse(method);
    }

    /**
     * Get a response-string for the URL. If the URL contains an Authentication part
     * then is this used and stored for this connection. Be aware to reset the
     * authentication if the user name and password should not be reused for
     * later connection.
     * 
     * @param url
     *            The resource URL.
     * @param cookie
     *            the Cookie.
     * @return String response as String.
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public String getRequestURLAsString(final URL url, final Cookie cookie)
        throws WebserverSystemException {

        GetMethod method = getRequestURL(url, cookie);
        return readResponse(method);
    }

    /**
     * Get a GetMethod for the URL. If the URL contains an Authentication part
     * then is this used and stored for this connection. Be aware to reset the
     * authentication if the user name and password should not be reused for
     * later connection.
     * 
     * @param url
     *            The resource URL.
     * @return GetMethod.
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public GetMethod getRequestURL(final URL url)
        throws WebserverSystemException {

        String username = null;
        String password = null;

        String userinfo = url.getUserInfo();
        if (userinfo != null) {
            String[] loginValues = userinfo.split(":");
            username = loginValues[0];
            password = loginValues[1];
        }
        return getRequestURL(url, username, password);
    }

    /**
     * Get the GetMethod with authentication. The username and password is
     * stored for this connection. Later connection to same URL doesn't require
     * to set the authentication again. Be aware that this could lead to an
     * security issue! To avoid reuse reset the authentication for the URL.
     * 
     * @param url
     *            URL of resource.
     * @param username
     *            User name for authentication.
     * @param password
     *            Password for authentication.
     * @return GetMethod
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public GetMethod getRequestURL(
        final URL url, final String username, final String password)
        throws WebserverSystemException {

        setAuthentication(url, username, password);
        return get(url.toString());
    }

    /**
     * Get the GetMethod with a cookie.
     * 
     * @param url
     *            URL of resource.
     * @param cookie
     *            the Cookie.
     * @return GetMethod
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public GetMethod getRequestURL(
        final URL url, final Cookie cookie)
        throws WebserverSystemException {

        return get(url.toString(), cookie);
    }

    /**
     * Get a response-string for the URL. The username and password is
     * stored for this connection. Later connection to same URL doesn't require
     * to set the authentication again. Be aware that this could lead to an
     * security issue! To avoid reuse reset the authentication for the URL.
     * 
     * @param url
     *            The resource URL.
     * @param body
     *            The body of HTTP request.
     * @param username
     *            User name for authentication.
     * @param password
     *            Password for authentication.
     * @return String response as String.
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public String putRequestURLAsString(
                            final URL url,
                            final String body,
                            final String username, 
                            final String password)
        throws WebserverSystemException {

        PutMethod method = putRequestURL(url, body, username, password);
        return readResponse(method);
    }

    /**
     * Get a response-string for the URL. If the URL contains an Authentication part
     * then is this used and stored for this connection. Be aware to reset the
     * authentication if the user name and password should not be reused for
     * later connection.
     * 
     * @param url
     *            The resource URL.
     * @param body
     *            The body of HTTP request.
     * @param cookie
     *            the Cookie.
     * @return String response as String.
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public String putRequestURLAsString(
                            final URL url, 
                            final String body, 
                            final Cookie cookie)
        throws WebserverSystemException {

        PutMethod method = putRequestURL(url, body, cookie);
        return readResponse(method);
    }

    /**
     * Get the PutMethod with authentication. Username and password is stored
     * for connection. Later connections to same URL doesn't require to set
     * authentication again. Be aware that this could lead to an security issue!
     * To avoid reuse reset the authentication for the URL.
     * 
     * @param url
     *            URL of resource.
     * @param body
     *            The body of HTTP request.
     * @param username
     *            User name for authentication.
     * @param password
     *            Password for authentication.
     * @return PutMethod
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public PutMethod putRequestURL(
        final URL url, final String body, final String username,
        final String password) throws WebserverSystemException {

        setAuthentication(url, username, password);
        return put(url.toString(), body);
    }

    /**
     * Get the PutMethod with a cookie.
     * 
     * @param url
     *            URL of resource.
     * @param body
     *            The body of HTTP request.
     * @param cookie
     *            the Cookie.
     * @return PutMethod
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public PutMethod putRequestURL(
        final URL url, final String body, final Cookie cookie) 
                               throws WebserverSystemException {

        return put(url.toString(), body, cookie);
    }

    /**
     * Get a response-string for the URL. The username and password is
     * stored for this connection. Later connection to same URL doesn't require
     * to set the authentication again. Be aware that this could lead to an
     * security issue! To avoid reuse reset the authentication for the URL.
     * 
     * @param url
     *            The resource URL.
     * @param body
     *            The body of HTTP request.
     * @param username
     *            User name for authentication.
     * @param password
     *            Password for authentication.
     * @return String response as String.
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public String postRequestURLAsString(
                            final URL url,
                            final String body,
                            final String username, 
                            final String password)
        throws WebserverSystemException {

        PostMethod method = postRequestURL(url, body, username, password);
        return readResponse(method);
    }

    /**
     * Get a response-string for the URL. Cookie is set.
     * 
     * @param url
     *            The resource URL.
     * @param body
     *            The body of HTTP request.
     * @param cookie
     *            the Cookie.
     * @return String response as String.
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public String postRequestURLAsString(
                            final URL url, 
                            final String body, 
                            final Cookie cookie)
        throws WebserverSystemException {

        PostMethod method = postRequestURL(url, body, cookie);
        return readResponse(method);
    }

    /**
     * Get the PostMethod with authentication. Username and password is stored
     * for connection. Later connections to same URL doesn't require to set
     * authentication again. Be aware that this could lead to an security issue!
     * To avoid reuse reset the authentication for the URL.
     * 
     * @param url
     *            URL of resource.
     * @param body
     *            The post body of HTTP request.
     * @param username
     *            User name for authentication.
     * @param password
     *            Password for authentication.
     * @return PostMethod
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public PostMethod postRequestURL(
        final URL url, final String body, final String username,
        final String password) throws WebserverSystemException {

        setAuthentication(url, username, password);
        return post(url.toString(), body);
    }

    /**
     * Get the PostMethod with a Cookie.
     * 
     * @param url
     *            URL of resource.
     * @param body
     *            The post body of HTTP request.
     * @param cookie
     *            The Cookie.
     * @return PostMethod
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public PostMethod postRequestURL(
        final URL url, final String body, final Cookie cookie) 
                                throws WebserverSystemException {

        return post(url.toString(), body, cookie);
    }

    /**
     * Get the DeleteMethod with authentication. Username and password is stored
     * for connection. Later connections to same URL doesn't require to set
     * authentication again. Be aware that this could lead to an security issue!
     * To avoid reuse reset the authentication for the URL.
     * 
     * @param url
     *            URL of resource.
     * @param username
     *            User name for authentication.
     * @param password
     *            Password for authentication.
     * @return DeleteMethod
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public DeleteMethod deleteRequestURL(
        final URL url, final String username, final String password)
        throws WebserverSystemException {

        setAuthentication(url, username, password);
        return delete(url.toString());
    }

    /**
     * Get the DeleteMethod with a Cookie.
     * 
     * @param url
     *            URL of resource.
     * @param cookie
     *            The cookie.
     * @return DeleteMethod
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public DeleteMethod deleteRequestURL(
        final URL url, final Cookie cookie)
        throws WebserverSystemException {

        return delete(url.toString(), cookie);
    }

    /**
     * Set Authentication stuff.
     * 
     * @param url
     *            URL of resource.
     * @param username
     *            User name for authentication
     * @param password
     *            Password for authentication.
     * @throws WebserverSystemException e
     */
    public void setAuthentication(
        final URL url, final String username, final String password) 
                                        throws WebserverSystemException {

        if (username != null && password != null) {
            AuthScope authScope =
                new AuthScope(url.getHost(), AuthScope.ANY_PORT,
                    AuthScope.ANY_REALM);
            UsernamePasswordCredentials creds =
                new UsernamePasswordCredentials(username, password);
            getHttpClient(null).getState().setCredentials(authScope, creds);

            // don't wait for auth request
            getHttpClient(null).getParams().setAuthenticationPreemptive(true);
            // try only BASIC auth; skip to test NTLM and DIGEST
            List<String> authPrefs = new ArrayList<String>(1);
            authPrefs.add(AuthPolicy.BASIC);
            this.httpClient.getParams().setParameter(
                AuthPolicy.AUTH_SCHEME_PRIORITY, authPrefs);
        }
    }

    /**
     * Delete a specific authentication entry from HTTPClient.
     * 
     * @param url
     *            The URL to the resource.
     */
    public void resetAuthentication(final URL url) {

        AuthScope authScope =
            new AuthScope(url.getHost(), AuthScope.ANY_PORT,
                AuthScope.ANY_REALM);
        UsernamePasswordCredentials creds =
            new UsernamePasswordCredentials("", "");
        this.httpClient.getState().setCredentials(authScope, creds);
    }

    /**
     * set ProxyHost according to escidoc-core.properties.
     * 
     * @return ProxyHost
     * 
     * @throws WebserverSystemException e
     */
    private ProxyHost getProxyHost()
            throws WebserverSystemException {
        try {
            if (!proxyConfigured) {
                String proxyHostName = EscidocConfiguration.getInstance()
                    .get(EscidocConfiguration.ESCIDOC_CORE_PROXY_HOST);
                String proxyPort = EscidocConfiguration.getInstance()
                    .get(EscidocConfiguration.ESCIDOC_CORE_PROXY_PORT);
                if (proxyHostName != null && !proxyHostName.trim().equals("")) {
                    if (proxyPort != null && !proxyPort.trim().equals("")) {
                        this.proxyHost =
                                new ProxyHost(proxyHostName
                                , Integer.parseInt(proxyPort));
                    } else {
                        this.proxyHost = new ProxyHost(proxyHostName);
                    }
                }
                proxyConfigured = true;
            }
            return this.proxyHost;

        } catch (IOException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * check if proxy has to get used for given url.
     * If yes, set ProxyHost in httpClient
     * 
     * @param url url
     * 
     * @throws WebserverSystemException e
     */
    private void setProxy(final String url)
            throws WebserverSystemException {
        try {
            if (this.proxyHost != null) {
                String nonProxyHosts = EscidocConfiguration.getInstance()
                    .get(EscidocConfiguration.ESCIDOC_CORE_NON_PROXY_HOSTS);
                if (nonProxyHosts != null && !nonProxyHosts.trim().equals("")) {
                    nonProxyHosts = nonProxyHosts
                            .replaceAll("\\.", "\\\\.");
                    nonProxyHosts = nonProxyHosts.replaceAll("\\*", "");
                    nonProxyHosts = nonProxyHosts.replaceAll("\\?", "\\\\?");
                    Pattern nonProxyPattern = Pattern.compile(nonProxyHosts);
                    Matcher nonProxyMatcher = nonProxyPattern.matcher(url);
                    if (nonProxyMatcher.find()) {
                        this.httpClient.getHostConfiguration().setProxyHost(
                                null);
                    } else {
                        this.httpClient.getHostConfiguration()
                                .setProxyHost(proxyHost);
                    }
                } else {
                    this.httpClient.getHostConfiguration()
                        .setProxyHost(proxyHost);
                }
            }
        } catch (IOException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * Get the HTTP Client (multi threaded).
     * 
     * @param url the url to call with the httpClient
     *  used to decide if proxy has to get used.
     * @return HttpClient 
     * @throws WebserverSystemException e
     */
    public HttpClient getHttpClient(final String url) 
                        throws WebserverSystemException {
        if (this.httpClient == null) {
            this.cm.getParams().setMaxConnectionsPerHost(
                HostConfiguration.ANY_HOST_CONFIGURATION,
                HTTP_MAX_CONNECTIONS_PER_HOST);
            this.cm.getParams().setMaxTotalConnections(
                HTTP_MAX_CONNECTIONS_PER_HOST
                    * HTTP_MAX_TOTAL_CONNECTIONS_FACTOR);
            this.httpClient = new HttpClient(this.cm);
            if (timeout != -1) {
                HttpClientParams clientParams = new HttpClientParams();
                clientParams.setSoTimeout(timeout);
                this.httpClient.setParams(clientParams);
            }
        }
        if (getProxyHost() != null && url != null) {
            setProxy(url);
        }
        return this.httpClient;
    }

    /**
     * Call the GetMethod.
     * 
     * @param url
     *            The URL for the HTTP GET method.
     * @return GetMethod
     * @throws WebserverSystemException
     *             If connection failed.
     */
    private GetMethod get(final String url) throws WebserverSystemException {

        return get(url, null);
    }

    /**
     * Call the GetMethod.
     * 
     * @param url
     *            The URL for the HTTP GET method.
     * @param cookie
     *            The Cookie.
     * @return GetMethod
     * @throws WebserverSystemException
     *             If connection failed.
     */
    private GetMethod get(final String url, final Cookie cookie) 
                                    throws WebserverSystemException {

        GetMethod get = null;
        try {
            try {
                get = new GetMethod(url);
            } catch (IllegalArgumentException e) {
                get = new GetMethod(new URI(url, false).getEscapedURI());
            }
            if (cookie != null) {
                get.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
                get.setRequestHeader(
                    "Cookie", cookie.getName() + "=" + cookie.getValue());
            }
            int responseCode = getHttpClient(url).executeMethod(get);
            if ((responseCode / HTTP_RESPONSE_CLASS) 
                        != (HttpServletResponse.SC_OK / HTTP_RESPONSE_CLASS)) {
                get.releaseConnection();
                LOG.debug("Connection to '" + url
                    + "' failed with response code " + responseCode);
                throw new WebserverSystemException("HTTP connection to \""
                    + url + "\" failed.");
            }
        }
        catch (HttpException e) {
            throw new WebserverSystemException(e);
        }
        catch (IOException e) {
            throw new WebserverSystemException(e);
        }

        return get;
    }

    /**
     * Call the DeleteMethod.
     * 
     * @param url
     *            The URL for the HTTP DELETE method.
     * @return DeleteMethod
     * @throws WebserverSystemException
     *             If connection failed.
     */
    private DeleteMethod delete(final String url)
        throws WebserverSystemException {

        return delete(url, null);
    }

    /**
     * Call the DeleteMethod.
     * 
     * @param url
     *            The URL for the HTTP DELETE method.
     * @param cookie
     *            The Cookie.
     * @return DeleteMethod
     * @throws WebserverSystemException
     *             If connection failed.
     */
    private DeleteMethod delete(final String url, final Cookie cookie)
        throws WebserverSystemException {

        DeleteMethod delete = null;
        try {
            try {
                delete = new DeleteMethod(url);
            } catch (IllegalArgumentException e) {
                delete = new DeleteMethod(new URI(url, false).getEscapedURI());
            }
            if (cookie != null) {
                delete.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
                delete.setRequestHeader(
                    "Cookie", cookie.getName() + "=" + cookie.getValue());
            }
            int responseCode = getHttpClient(url).executeMethod(delete);
            if ((responseCode / HTTP_RESPONSE_CLASS) 
                != (HttpServletResponse.SC_OK / HTTP_RESPONSE_CLASS)) {
                delete.releaseConnection();
                LOG.debug("Connection to '" + url
                    + "' failed with response code " + responseCode);
                throw new WebserverSystemException("HTTP connection failed.");
            }
        }
        catch (HttpException e) {
            throw new WebserverSystemException(e);
        }
        catch (IOException e) {
            throw new WebserverSystemException(e);
        }

        return delete;
    }

    /**
     * Call the PutMethod.
     * 
     * @param url
     *            The URL for the HTTP PUT request
     * @param body
     *            The body for the PUT request.
     * @return PutMethod
     * @throws WebserverSystemException
     *             If connection failed.
     */
    private PutMethod put(final String url, final String body)
        throws WebserverSystemException {

        return put(url, body, null);
    }

    /**
     * Call the PutMethod.
     * 
     * @param url
     *            The URL for the HTTP PUT request
     * @param body
     *            The body for the PUT request.
     * @param cookie
     *            The Cookie.
     * @return PutMethod
     * @throws WebserverSystemException
     *             If connection failed.
     */
    private PutMethod put(final String url, final String body, final Cookie cookie)
        throws WebserverSystemException {

        PutMethod put = null;
        RequestEntity entity;
        try {
            entity =
                new StringRequestEntity(body, Constants.DEFAULT_MIME_TYPE,
                    XmlUtility.CHARACTER_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new WebserverSystemException(e);
        }

        try {
            try {
                put = new PutMethod(url);
            } catch (IllegalArgumentException e) {
                put = new PutMethod(new URI(url, false).getEscapedURI());
            }
            put.setRequestEntity(entity);
            if (cookie != null) {
                put.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
                put.setRequestHeader(
                    "Cookie", cookie.getName() + "=" + cookie.getValue());
            }

            int responseCode = getHttpClient(url).executeMethod(put);
            if ((responseCode / HTTP_RESPONSE_CLASS) 
                != (HttpServletResponse.SC_OK / HTTP_RESPONSE_CLASS)) {
                put.releaseConnection();
                LOG.debug("Connection to '" + url
                    + "' failed with response code " + responseCode);
                throw new WebserverSystemException("HTTP connection failed.");
            }
        }
        catch (HttpException e) {
            throw new WebserverSystemException(e);
        }
        catch (IOException e) {
            throw new WebserverSystemException(e);
        }

        return put;
    }

    /**
     * Call the PostMethod.
     * 
     * @param url
     *            The URL for the HTTP POST request
     * @param body
     *            The body for the POST request.
     * @return PostMethod
     * @throws WebserverSystemException
     *             If connection failed.
     */
    private PostMethod post(final String url, final String body)
        throws WebserverSystemException {

        return post(url, body, null);
    }

    /**
     * Call the PostMethod.
     * 
     * @param url
     *            The URL for the HTTP POST request
     * @param body
     *            The body for the POST request.
     * @param cookie
     *            The Cookie.
     * @return PostMethod
     * @throws WebserverSystemException
     *             If connection failed.
     */
    private PostMethod post(
                        final String url, 
                        final String body, 
                        final Cookie cookie)
        throws WebserverSystemException {

        PostMethod post = null;
        RequestEntity entity;
        try {
            entity =
                new StringRequestEntity(body, Constants.DEFAULT_MIME_TYPE,
                    XmlUtility.CHARACTER_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new WebserverSystemException(e);
        }

        try {
            try {
                post = new PostMethod(url);
            } catch (IllegalArgumentException e) {
                post = new PostMethod(new URI(url, false).getEscapedURI());
            }
            post.setRequestEntity(entity);
            if (cookie != null) {
                post.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
                post.setRequestHeader(
                    "Cookie", cookie.getName() + "=" + cookie.getValue());
            }

            getHttpClient(url).executeMethod(post);
        }
        catch (HttpException e) {
            throw new WebserverSystemException(e);
        }
        catch (IOException e) {
            throw new WebserverSystemException(e);
        }

        return post;
    }

    /**
     * Reads the response as String from the HttpMethodBase class.
     * 
     * @param method
     *            The HttpMethodBase.
     * @return String.
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public String readResponse(final HttpMethodBase method)
            throws WebserverSystemException {
        InputStream inputStream = null;
        BufferedReader in = null;
        StringBuffer buf = new StringBuffer("");
        try {
            inputStream = method.getResponseBodyAsStream();
            in = new BufferedReader(
                    new InputStreamReader(
                            inputStream, XmlUtility.CHARACTER_ENCODING));
            String str = new String("");
            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
        } catch (Exception e) {
            throw new WebserverSystemException(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {}
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {}
            }
        }
        return buf.toString();
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(final int timeout) {
        this.timeout = timeout;
        if (this.httpClient != null) {
            HttpClientParams clientParams = new HttpClientParams();
            clientParams.setSoTimeout(timeout);
            this.httpClient.setParams(clientParams);
        }
    }

}
