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

package de.escidoc.core.common.util.service;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An utility class for HTTP requests.<br /> This class uses pooled HTTP connections.
 *
 * @author Steffen Wagner
 */
public class ConnectionUtility {

    private static final Pattern SPLIT_PATTERN = Pattern.compile(":");

    private static final int HTTP_MAX_CONNECTIONS_PER_HOST = 30;

    private static final int HTTP_MAX_TOTAL_CONNECTIONS_FACTOR = 3;

    private static final int HTTP_RESPONSE_CLASS = 100;

    private int timeout = -1;

    private DefaultHttpClient httpClient;

    private HttpHost proxyHost;

    private boolean proxyConfigured;

    private final Logger logger = LoggerFactory.getLogger(ConnectionUtility.class);

    /**
     * Get a response-string for the URL. If the URL contains an Authentication part then is this used and stored for
     * this connection. Be aware to reset the authentication if the user name and password should not be reused for
     * later connection.
     *
     * @param url The resource URL.
     * @return String response as String.
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public String getRequestURLAsString(final URL url) throws WebserverSystemException {

        long start = System.currentTimeMillis();

        final HttpResponse httpResponse = getRequestURL(url);

        long end = System.currentTimeMillis();
        logger.info("getRequestURLAsString <" + url.toString() + "> needed " + (end - start) + " msec");

        return readResponse(httpResponse);
    }

    /**
     * Get a response-string for the URL. The username and password is stored for this connection. Later connection to
     * same URL doesn't require to set the authentication again. Be aware that this could lead to an security issue! To
     * avoid reuse reset the authentication for the URL.
     *
     * @param url      The resource URL.
     * @param username User name for authentication.
     * @param password Password for authentication.
     * @return String response as String.
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public String getRequestURLAsString(final URL url, final String username, final String password)
        throws WebserverSystemException {

        long start = System.currentTimeMillis();

        final HttpResponse httpResponse = getRequestURL(url, username, password);

        long end = System.currentTimeMillis();
        logger.debug("getRequestURLAsString <" + url.toString() + " " + username + "> needed " + (end - start) + " msec");
            
        return readResponse(httpResponse);
    }

    /**
     * Get a response-string for the URL. If the URL contains an Authentication part then is this used and stored for
     * this connection. Be aware to reset the authentication if the user name and password should not be reused for
     * later connection. T
     *
     * @param url    The resource URL.
     * @param cookie the Cookie.
     * @return String response as String.
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public String getRequestURLAsString(final URL url, final Cookie cookie) throws WebserverSystemException {

        long start = System.currentTimeMillis();

        final HttpResponse httpResponse = getRequestURL(url, cookie);

        long end = System.currentTimeMillis();
        logger.info("getRequestURLAsString <" + url.toString() + " " + cookie.toString() + "> needed " + (end - start)
            + " msec");

        return readResponse(httpResponse);
    }

    /**
     * Get a HttpGet for the URL. If the URL contains an Authentication part then is this used and stored for this
     * connection. Be aware to reset the authentication if the user name and password should not be reused for later
     * connection.
     *
     * @param url The resource URL.
     * @return HttpGet.
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public HttpResponse getRequestURL(final URL url) throws WebserverSystemException {

        final String username;
        final String password;

        final String userinfo = url.getUserInfo();
        if (userinfo != null) {
            final String[] loginValues = SPLIT_PATTERN.split(":");
            username = loginValues[0];
            password = loginValues[1];
        }
        else {
            username = EscidocConfiguration.FEDORA_USER;
            password = EscidocConfiguration.FEDORA_PASSWORD;
        }

        return getRequestURL(url, username, password);
    }

    /**
     * Get the HttpGet with authentication. The username and password is stored for this connection. Later connection to
     * same URL doesn't require to set the authentication again. Be aware that this could lead to an security issue! To
     * avoid reuse reset the authentication for the URL.
     *
     * @param url      URL of resource.
     * @param username User name for authentication.
     * @param password Password for authentication.
     * @return HttpGet
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public HttpResponse getRequestURL(final URL url, final String username, final String password)
        throws WebserverSystemException {

        setAuthentication(url, username, password);
        return get(url.toString());
    }

    /**
     * Get the HttpGet with a cookie.
     *
     * @param url    URL of resource.
     * @param cookie the Cookie.
     * @return HttpGet
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public HttpResponse getRequestURL(final URL url, final Cookie cookie) throws WebserverSystemException {

        return get(url.toString(), cookie);
    }

    /**
     * Get a response-string for the URL. The username and password is stored for this connection. Later connection to
     * same URL doesn't require to set the authentication again. Be aware that this could lead to an security issue! To
     * avoid reuse reset the authentication for the URL.
     *
     * @param url      The resource URL.
     * @param body     The body of HTTP request.
     * @param username User name for authentication.
     * @param password Password for authentication.
     * @return String response as String.
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public String putRequestURLAsString(final URL url, final String body, final String username, final String password)
        throws WebserverSystemException {

        final HttpResponse method = putRequestURL(url, body, username, password);
        return readResponse(method);
    }

    /**
     * Get a response-string for the URL. If the URL contains an Authentication part then is this used and stored for
     * this connection. Be aware to reset the authentication if the user name and password should not be reused for
     * later connection.
     *
     * @param url    The resource URL.
     * @param body   The body of HTTP request.
     * @param cookie the Cookie.
     * @return String response as String.
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public String putRequestURLAsString(final URL url, final String body, final Cookie cookie)
        throws WebserverSystemException {

        final HttpResponse method = putRequestURL(url, body, cookie);
        return readResponse(method);
    }

    /**
     * Get the HttpPut with authentication. Username and password is stored for connection. Later connections to same
     * URL doesn't require to set authentication again. Be aware that this could lead to an security issue! To avoid
     * reuse reset the authentication for the URL.
     *
     * @param url      URL of resource.
     * @param body     The body of HTTP request.
     * @param username User name for authentication.
     * @param password Password for authentication.
     * @return HttpPut
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public HttpResponse putRequestURL(final URL url, final String body, final String username, final String password)
        throws WebserverSystemException {

        setAuthentication(url, username, password);
        return put(url.toString(), body);
    }

    /**
     * Get the HttpPut with a cookie.
     *
     * @param url    URL of resource.
     * @param body   The body of HTTP request.
     * @param cookie the Cookie.
     * @return HttpPut
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public HttpResponse putRequestURL(final URL url, final String body, final Cookie cookie)
        throws WebserverSystemException {

        return put(url.toString(), body, cookie);
    }

    /**
     * Get a response-string for the URL. The username and password is stored for this connection. Later connection to
     * same URL doesn't require to set the authentication again. Be aware that this could lead to an security issue! To
     * avoid reuse reset the authentication for the URL.
     *
     * @param url      The resource URL.
     * @param body     The body of HTTP request.
     * @param username User name for authentication.
     * @param password Password for authentication.
     * @return String response as String.
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public String postRequestURLAsString(final URL url, final String body, final String username, final String password)
        throws WebserverSystemException {

        final HttpResponse method = postRequestURL(url, body, username, password);
        return readResponse(method);
    }

    /**
     * Get a response-string for the URL. Cookie is set.
     *
     * @param url    The resource URL.
     * @param body   The body of HTTP request.
     * @param cookie the Cookie.
     * @return String response as String.
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public String postRequestURLAsString(final URL url, final String body, final Cookie cookie)
        throws WebserverSystemException {

        final HttpResponse method = postRequestURL(url, body, cookie);
        return readResponse(method);
    }

    /**
     * Get the HttpPost with authentication. Username and password is stored for connection. Later connections to same
     * URL doesn't require to set authentication again. Be aware that this could lead to an security issue! To avoid
     * reuse reset the authentication for the URL.
     *
     * @param url      URL of resource.
     * @param body     The post body of HTTP request.
     * @param username User name for authentication.
     * @param password Password for authentication.
     * @return HttpPost
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public HttpResponse postRequestURL(final URL url, final String body, final String username, final String password)
        throws WebserverSystemException {

        setAuthentication(url, username, password);
        return post(url.toString(), body);
    }

    /**
     * Get the HttpPost with a Cookie.
     *
     * @param url    URL of resource.
     * @param body   The post body of HTTP request.
     * @param cookie The Cookie.
     * @return HttpPost
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public HttpResponse postRequestURL(final URL url, final String body, final Cookie cookie)
        throws WebserverSystemException {

        return post(url.toString(), body, cookie);
    }

    /**
     * Get the HttpDelete with authentication. Username and password is stored for connection. Later connections to same
     * URL doesn't require to set authentication again. Be aware that this could lead to an security issue! To avoid
     * reuse reset the authentication for the URL.
     *
     * @param url      URL of resource.
     * @param username User name for authentication.
     * @param password Password for authentication.
     * @return HttpDelete
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public HttpDelete deleteRequestURL(final URL url, final String username, final String password)
        throws WebserverSystemException {

        setAuthentication(url, username, password);
        return delete(url.toString());
    }

    /**
     * Get the HttpDelete with a Cookie.
     *
     * @param url    URL of resource.
     * @param cookie The cookie.
     * @return HttpDelete
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public HttpDelete deleteRequestURL(final URL url, final Cookie cookie) throws WebserverSystemException {

        return delete(url.toString(), cookie);
    }

    /**
     * Set Authentication stuff.
     *
     * @param url      URL of resource.
     * @param username User name for authentication
     * @param password Password for authentication.
     * @throws WebserverSystemException e
     */
    public void setAuthentication(final URL url, final String username, final String password)
        throws WebserverSystemException {

        final CredentialsProvider credsProvider = new BasicCredentialsProvider();

        final AuthScope authScope = new AuthScope(url.getHost(), AuthScope.ANY_PORT, AuthScope.ANY_REALM);
        final Credentials creds = new UsernamePasswordCredentials(username, password);
        credsProvider.setCredentials(authScope, creds);

        this.getHttpClient(null).setCredentialsProvider(credsProvider);

        // don't wait for auth request
        final HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {

            @Override
            public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {

                final AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
                final CredentialsProvider credsProvider =
                    (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
                final HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);

                // If not auth scheme has been initialized yet
                if (authState.getAuthScheme() == null) {
                    final AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
                    // Obtain credentials matching the target host
                    final Credentials creds = credsProvider.getCredentials(authScope);
                    // If found, generate BasicScheme preemptively
                    if (creds != null) {
                        authState.setAuthScheme(new BasicScheme());
                        authState.setCredentials(creds);
                    }
                }
            }

        };

        this.getHttpClient(null).addRequestInterceptor(preemptiveAuth, 0);

    }

    /**
     * Delete a specific authentication entry from HTTPClient.
     *
     * @param url The URL to the resource.
     */
    public void resetAuthentication(final URL url) {

        final AuthScope authScope = new AuthScope(url.getHost(), AuthScope.ANY_PORT, AuthScope.ANY_REALM);

        final Credentials creds = new UsernamePasswordCredentials("", "");

        httpClient.getCredentialsProvider().setCredentials(authScope, creds);
    }

    /**
     * set ProxyHost according to escidoc-core.properties.
     *
     * @return HttpHost
     * @throws WebserverSystemException e
     */
    private HttpHost getProxyHost() throws WebserverSystemException {
        try {
            if (!this.proxyConfigured) {
                final String proxyHostName =
                    EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_PROXY_HOST);
                final String proxyPort =
                    EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_PROXY_PORT);
                if (proxyHostName != null && proxyHostName.trim().length() != 0) {
                    this.proxyHost =
                        proxyPort != null && proxyPort.trim().length() != 0 ? new HttpHost(proxyHostName, Integer
                            .parseInt(proxyPort)) : new HttpHost(proxyHostName);
                }
                this.proxyConfigured = true;
            }
            return this.proxyHost;

        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * check if proxy has to get used for given url. If yes, set ProxyHost in httpClient
     *
     * @param url url
     * @throws WebserverSystemException e
     */
    private void setProxy(final CharSequence url) throws WebserverSystemException {
        try {
            if (this.proxyHost != null) {
                String nonProxyHosts =
                    EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_NON_PROXY_HOSTS);
                if (nonProxyHosts != null && nonProxyHosts.trim().length() != 0) {
                    nonProxyHosts = nonProxyHosts.replaceAll("\\.", "\\\\.");
                    nonProxyHosts = nonProxyHosts.replaceAll("\\*", "");
                    nonProxyHosts = nonProxyHosts.replaceAll("\\?", "\\\\?");
                    final Pattern nonProxyPattern = Pattern.compile(nonProxyHosts);
                    final Matcher nonProxyMatcher = nonProxyPattern.matcher(url);
                    if (nonProxyMatcher.find()) {
                        this.httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, null);
                    }
                    else {
                        this.httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, this.proxyHost);
                    }
                }
                else {
                    this.httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, this.proxyHost);

                }
            }
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * Get the HTTP Client (multi threaded).
     *
     * @param url the url to call with the httpClient used to decide if proxy has to get used.
     * @return HttpClient
     * @throws WebserverSystemException e
     */
    public DefaultHttpClient getHttpClient(final String url) throws WebserverSystemException {
        if (this.httpClient == null) {

            final HttpParams params = new BasicHttpParams();
            ConnManagerParams.setMaxTotalConnections(params, HTTP_MAX_TOTAL_CONNECTIONS_FACTOR);

            final ConnPerRoute connPerRoute = new ConnPerRouteBean(HTTP_MAX_CONNECTIONS_PER_HOST);
            ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

            final Scheme http = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);

            // Schema für SSL Verbindungen
            // SSLSocketFactory sf = new
            // SSLSocketFactory(SSLContext.getInstance("TLS"));
            // sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
            // Scheme https = new Scheme("https", sf, 443);

            final SchemeRegistry sr = new SchemeRegistry();
            sr.register(http);
            // sr.register(https);

            final ClientConnectionManager cm = new ThreadSafeClientConnManager(params, sr);

            this.httpClient = new DefaultHttpClient(cm, params);

            if (this.timeout != -1) {
                // TODO timeout testen
                ConnManagerParams.setTimeout(params, (long) this.timeout);
            }
        }
        if (getProxyHost() != null && url != null) {
            setProxy(url);
        }
        return this.httpClient;
    }

    /**
     * Call the HttpGet.
     *
     * @param url The URL for the HTTP GET method.
     * @return HttpGet
     * @throws WebserverSystemException If connection failed.
     */
    private HttpResponse get(final String url) throws WebserverSystemException {

        return get(url, null);
    }

    /**
     * Call the HttpGet.
     *
     * @param url    The URL for the HTTP GET method.
     * @param cookie The Cookie.
     * @return HttpGet
     * @throws WebserverSystemException If connection failed.
     */
    private HttpResponse get(final String url, final Cookie cookie) throws WebserverSystemException {

        HttpResponse httpResponse = null;
        try {
            final HttpGet httpGet = new HttpGet(new URI(url));
            if (cookie != null) {
                HttpClientParams.setCookiePolicy(httpGet.getParams(), CookiePolicy.BEST_MATCH);
                httpGet.setHeader("Cookie", cookie.getName() + '=' + cookie.getValue());
            }
            httpResponse = getHttpClient(url).execute(httpGet);

            final int responseCode = httpResponse.getStatusLine().getStatusCode();

            if (responseCode / HTTP_RESPONSE_CLASS != HttpServletResponse.SC_OK / HTTP_RESPONSE_CLASS) {
                final String errorPage = readResponse(httpResponse);

                // TODO logging, Url abgelöst?
                // URLEncodedUtils.LOGGER.debug("Connection to '" + url
                // + "' failed with response code " + responseCode);
                throw new WebserverSystemException("HTTP connection to \"" + url + "\" failed: " + errorPage);
            }
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
        catch (final URISyntaxException e) {
            throw new WebserverSystemException("Illegal URL '" + url + "'.", e);
        }
        return httpResponse;
    }

    /**
     * Call the HttpDelete.
     *
     * @param url The URL for the HTTP DELETE method.
     * @return HttpDelete
     * @throws WebserverSystemException If connection failed.
     */
    private HttpDelete delete(final String url) throws WebserverSystemException {

        return delete(url, null);
    }

    /**
     * Call the HttpDelete.
     *
     * @param url    The URL for the HTTP DELETE method.
     * @param cookie The Cookie.
     * @return HttpDelete
     * @throws WebserverSystemException If connection failed.
     */
    private HttpDelete delete(final String url, final Cookie cookie) throws WebserverSystemException {
        try {
            final HttpDelete delete = new HttpDelete(new URI(url));
            final HttpResponse httpResponse = getHttpClient(url).execute(delete);
            if (httpResponse.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK) {
                final String errorPage = readResponse(httpResponse);
                throw new WebserverSystemException("HTTP connection to \"" + url + "\" failed: " + errorPage);
            }
            return delete;
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
        catch (final URISyntaxException e) {
            throw new WebserverSystemException(e);
        }
        catch (final IllegalArgumentException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * Call the HttpPut.
     *
     * @param url  The URL for the HTTP PUT request
     * @param body The body for the PUT request.
     * @return HttpPut
     * @throws WebserverSystemException If connection failed.
     */
    private HttpResponse put(final String url, final String body) throws WebserverSystemException {
        return put(url, body, null);
    }

    /**
     * Call the HttpPut.
     *
     * @param url    The URL for the HTTP PUT request
     * @param body   The body for the PUT request.
     * @param cookie The Cookie.
     * @return HttpPut
     * @throws WebserverSystemException If connection failed.
     */
    private HttpResponse put(final String url, final String body, final Cookie cookie) throws WebserverSystemException {

        final HttpResponse httpResponse;
        try {
            final HttpEntity entity =
                new StringEntity(body, Constants.DEFAULT_MIME_TYPE, XmlUtility.CHARACTER_ENCODING);
            final HttpPut httpPut = new HttpPut(url);
            httpPut.setEntity(entity);
            httpResponse = getHttpClient(url).execute(httpPut);
            final int responseCode = httpResponse.getStatusLine().getStatusCode();
            if (responseCode != HttpServletResponse.SC_OK) {
                final String errorPage = readResponse(httpResponse);
                throw new WebserverSystemException("HTTP connection to \"" + url + "\" failed: " + errorPage);
            }
        }
        catch (final UnsupportedEncodingException e) {
            throw new WebserverSystemException(e);
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
        return httpResponse;
    }

    /**
     * Call the HttpPost.
     *
     * @param url  The URL for the HTTP POST request
     * @param body The body for the POST request.
     * @return HttpPost
     * @throws WebserverSystemException If connection failed.
     */
    private HttpResponse post(final String url, final String body) throws WebserverSystemException {

        return post(url, body, null);
    }

    /**
     * Call the HttpPost.
     *
     * @param url    The URL for the HTTP POST request
     * @param body   The body for the POST request.
     * @param cookie The Cookie.
     * @return HttpResponse
     * @throws WebserverSystemException If connection failed.
     */
    private HttpResponse post(final String url, final String body, final Cookie cookie) throws WebserverSystemException {

        final HttpResponse httpResponse;
        try {
            final HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(body, Constants.DEFAULT_MIME_TYPE, XmlUtility.CHARACTER_ENCODING));

            if (cookie != null) {
                HttpClientParams.setCookiePolicy(httpPost.getParams(), CookiePolicy.BEST_MATCH);
                httpPost.setHeader("Cookie", cookie.getName() + '=' + cookie.getValue());
            }

            httpResponse = getHttpClient(url).execute(httpPost);
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }

        return httpResponse;
    }

    /**
     * Reads the response as String from the HttpResponse class.
     *
     * @param httpResponse The HttpResponse.
     * @return String.
     * @throws WebserverSystemException Thrown if connection failed.
     */
    public String readResponse(final HttpResponse httpResponse) throws WebserverSystemException {
        try {
            return EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(final int timeout) {
        this.timeout = timeout;
        if (this.httpClient != null) {

            httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, timeout);
            // TODO:
            // http.protocol.expect-continue': activates Expect: 100-Continue
            // handshake for the
            // entity enclosing methods. The purpose of the Expect: 100-Continue
            // handshake is to allow
            // the client that is sending a request message with a request body
            // to determine if the origin server
            // is willing to accept the request (based on the request headers)
            // before the client sends the request
            // body. The use of the Expect: 100-continue handshake can result in
            // a noticeable performance improvement
            // for entity enclosing requests (such as POST and PUT) that require
            // the target server's authentication.
            // Expect: 100-continue handshake should be used with caution, as it
            // may cause problems with HTTP
            // servers and proxies that do not support HTTP/1.1 protocol. This
            // parameter expects a value of type
            // java.lang.Boolean. If this parameter is not set HttpClient will
            // attempt to use the handshake.
            // httpClient.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE,
            // Boolean.TRUE);

        }
    }
}
