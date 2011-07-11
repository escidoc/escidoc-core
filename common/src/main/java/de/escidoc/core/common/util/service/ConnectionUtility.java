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

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
@Service("escidoc.core.common.util.service.ConnectionUtility")
public class ConnectionUtility {

    private static final Pattern SPLIT_PATTERN = Pattern.compile(":");

    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 60; // 60 seconds

    private static final int DEFAULT_SO_TIMEOUT = 1000 * 60; // 60 seconds

    private static final int HTTP_MAX_CONNECTIONS_PER_HOST = 30;

    private static final int HTTP_MAX_TOTAL_CONNECTIONS_FACTOR = 3;

    private static final int HTTP_RESPONSE_CLASS = 100;

    private int timeout = -1;

    private DefaultHttpClient httpClient;

    private HttpHost proxyHost;

    private boolean proxyConfigured;

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
        final HttpResponse httpResponse = getRequestURL(url);
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
        final HttpResponse httpResponse = getRequestURL(url, username, password);
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
        final HttpResponse httpResponse = getRequestURL(url, cookie);
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
     * Set Authentication stuff.
     *
     * @param url      URL of resource.
     * @param username User name for authentication
     * @param password Password for authentication.
     * @throws WebserverSystemException e
     */
    public void setAuthentication(final URL url, final String username, final String password) {
        final CredentialsProvider credsProvider = new BasicCredentialsProvider();
        final AuthScope authScope = new AuthScope(url.getHost(), AuthScope.ANY_PORT, AuthScope.ANY_REALM);
        final Credentials creds = new UsernamePasswordCredentials(username, password);
        credsProvider.setCredentials(authScope, creds);
        this.getHttpClient(null).setCredentialsProvider(credsProvider);
        // don't wait for auth request
        final HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {
            @Override
            public void process(final HttpRequest request, final HttpContext context) {
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
    private HttpHost getProxyHost() {
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

    /**
     * check if proxy has to get used for given url. If yes, set ProxyHost in httpClient
     *
     * @param url url
     * @throws WebserverSystemException e
     */
    private void setProxy(final CharSequence url) {
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

    /**
     * Get the HTTP Client (multi threaded).
     *
     * @param url the url to call with the httpClient used to decide if proxy has to get used.
     * @return HttpClient
     * @throws WebserverSystemException e
     */
    public DefaultHttpClient getHttpClient(final String url) {
        if (this.httpClient == null) {

            final HttpParams params = new BasicHttpParams();
            if (this.timeout == -1) {
                HttpConnectionParams.setConnectionTimeout(params, DEFAULT_CONNECTION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, DEFAULT_SO_TIMEOUT);
            }
            else {
                // TODO: Maybe separate Connection and SO timeout...
                HttpConnectionParams.setConnectionTimeout(params, this.timeout);
                HttpConnectionParams.setSoTimeout(params, this.timeout);
            }
            ConnManagerParams.setMaxTotalConnections(params, HTTP_MAX_TOTAL_CONNECTIONS_FACTOR);
            final ConnPerRoute connPerRoute = new ConnPerRouteBean(HTTP_MAX_CONNECTIONS_PER_HOST);
            ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
            final Scheme http = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
            final SchemeRegistry sr = new SchemeRegistry();
            sr.register(http);
            final ClientConnectionManager cm = new ThreadSafeClientConnManager(params, sr);
            this.httpClient = new DefaultHttpClient(cm, params);
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
        final HttpResponse httpResponse;
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
            // TODO
            // entitys für Body Posts
            final HttpPost httpPost = new HttpPost(url);
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
    private static String readResponse(final HttpResponse httpResponse) throws WebserverSystemException {
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
