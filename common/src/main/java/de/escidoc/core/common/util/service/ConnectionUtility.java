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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.DefaultedHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;

/**
 * An utility class for HTTP requests.<br />
 * This class uses pooled HTTP connections.
 * 
 * @author Steffen Wagner
 */
@Service("escidoc.core.common.util.service.ConnectionUtility")
public class ConnectionUtility {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionUtility.class);

    private static final Pattern SPLIT_PATTERN = Pattern.compile(":");

    /**
     * TODO: The connection timeout is limited to 20000ms at maximum within the HttpClient.
     */
    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 60; // 60 seconds

    private static final int DEFAULT_SO_TIMEOUT = 1000 * 60; // 60 seconds

    private static final int HTTP_MAX_CONNECTIONS_PER_HOST = 30;

    private static final int HTTP_MAX_TOTAL_CONNECTIONS_FACTOR = 3;

    private static final int HTTP_RESPONSE_CLASS = 100;

    /**
     * The default constructor creates a {@link SchemeRegistry} with the default {@link Scheme}s HTTP and HTTPS.
     */
    private static final ThreadSafeClientConnManager CONN_MANAGER = new ThreadSafeClientConnManager();

    private static HttpHost PROXY_HOST;

    private static Pattern NON_PROXY_HOSTS_PATTERN;

    private static final HttpParams DEFAULT_HTTP_PARAMS = new BasicHttpParams();

    static {
        init();
    }

    /**
     * Allow instantiation for spring only.
     */
    protected ConnectionUtility() {
    }

    private static void init() {
        /*
         * Configuration independant settings and default values.
         */
        // ConnectionManager
        CONN_MANAGER.setMaxTotal(HTTP_MAX_TOTAL_CONNECTIONS_FACTOR);
        CONN_MANAGER.setDefaultMaxPerRoute(HTTP_MAX_CONNECTIONS_PER_HOST);
        // Default HttpParams
        HttpConnectionParams.setConnectionTimeout(DEFAULT_HTTP_PARAMS, DEFAULT_CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(DEFAULT_HTTP_PARAMS, DEFAULT_SO_TIMEOUT);

        /*
         * Configuration dependant settins.
         */
        final EscidocConfiguration config = EscidocConfiguration.getInstance();
        if (config == null) {
            return;
        }

        // Proxy configuration: non proxy hosts (exclusions)
        String nonProxyHosts = config.get(EscidocConfiguration.ESCIDOC_CORE_NON_PROXY_HOSTS);
        if (nonProxyHosts != null && nonProxyHosts.trim().length() != 0) {
            nonProxyHosts = nonProxyHosts.replaceAll("\\.", "\\\\.");
            nonProxyHosts = nonProxyHosts.replaceAll("\\*", "");
            nonProxyHosts = nonProxyHosts.replaceAll("\\?", "\\\\?");
            NON_PROXY_HOSTS_PATTERN = Pattern.compile(nonProxyHosts);
        }

        // Proxy configuration: The proxy host to use
        final String proxyHostName = config.get(EscidocConfiguration.ESCIDOC_CORE_PROXY_HOST);
        final String proxyPort = config.get(EscidocConfiguration.ESCIDOC_CORE_PROXY_PORT);
        if (proxyHostName != null && !proxyHostName.isEmpty()) {
            PROXY_HOST =
                proxyPort != null && !proxyPort.isEmpty() ? new HttpHost(proxyHostName, Integer.parseInt(proxyPort)) : new HttpHost(
                    proxyHostName);
        }

        // HttpClient configuration
        String property = null;
        if ((property = config.get(EscidocConfiguration.HTTP_CONNECTION_TIMEOUT)) != null) {
            try {
                HttpConnectionParams.setConnectionTimeout(DEFAULT_HTTP_PARAMS, Integer.parseInt(property));
            }
            catch (final NumberFormatException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Unable to use the " + EscidocConfiguration.HTTP_CONNECTION_TIMEOUT + " property.", e);
                }
            }
        }
        if ((property = config.get(EscidocConfiguration.HTTP_SOCKET_TIMEOUT)) != null) {
            try {
                HttpConnectionParams.setSoTimeout(DEFAULT_HTTP_PARAMS, Integer.parseInt(property));
            }
            catch (final NumberFormatException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Unable to use the " + EscidocConfiguration.HTTP_SOCKET_TIMEOUT + " property.", e);
                }
            }
        }
    }

    /**
     * Get a response-string for the URL. If the URL contains an Authentication part then is this used and stored for
     * this connection. Be aware to reset the authentication if the user name and password should not be reused for
     * later connection.
     * 
     * @param url
     *            The resource URL.
     * @return String response as String.
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public String getRequestURLAsString(final URL url) throws WebserverSystemException {
        return getRequestURLAsString(null, url);
    }

    /**
     * 
     * @param client
     * @param url
     * @return
     * @throws WebserverSystemException
     */
    public String getRequestURLAsString(final DefaultHttpClient client, final URL url) throws WebserverSystemException {
        final HttpResponse httpResponse = getRequestURL(client, url);
        return readResponse(httpResponse);
    }

    /**
     * Get a response-string for the URL. The username and password is stored for this connection. Later connection to
     * same URL doesn't require to set the authentication again. Be aware that this could lead to an security issue! To
     * avoid reuse reset the authentication for the URL.
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
    public String getRequestURLAsString(final URL url, final String username, final String password)
        throws WebserverSystemException {
        return getRequestURLAsString(null, url, username, password);
    }

    /**
     * 
     * @param client
     * @param url
     * @param username
     * @param password
     * @return
     * @throws WebserverSystemException
     */
    public String getRequestURLAsString(
        final DefaultHttpClient client, final URL url, final String username, final String password)
        throws WebserverSystemException {
        final HttpResponse httpResponse = getRequestURL(client, url, username, password);
        return readResponse(httpResponse);
    }

    /**
     * Get a response-string for the URL. If the URL contains an Authentication part then is this used and stored for
     * this connection. Be aware to reset the authentication if the user name and password should not be reused for
     * later connection. T
     * 
     * @param url
     *            The resource URL.
     * @param cookie
     *            the Cookie.
     * @return String response as String.
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public String getRequestURLAsString(final URL url, final Cookie cookie) throws WebserverSystemException {
        return getRequestURLAsString(null, url, cookie);
    }

    /**
     * 
     * @param client
     * @param url
     * @param cookie
     * @return
     * @throws WebserverSystemException
     */
    public String getRequestURLAsString(final DefaultHttpClient client, final URL url, final Cookie cookie)
        throws WebserverSystemException {
        final HttpResponse httpResponse = getRequestURL(client, url, cookie);
        return readResponse(httpResponse);
    }

    /**
     * Get a HttpGet for the URL. If the URL contains an Authentication part then is this used and stored for this
     * connection. Be aware to reset the authentication if the user name and password should not be reused for later
     * connection.
     * 
     * @param url
     *            The resource URL.
     * @return HttpGet.
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public HttpResponse getRequestURL(final URL url) throws WebserverSystemException {
        return getRequestURL(null, url);
    }

    /**
     * 
     * @param client
     * @param url
     * @return
     * @throws WebserverSystemException
     */
    public HttpResponse getRequestURL(final DefaultHttpClient client, final URL url) throws WebserverSystemException {
        final String username;
        final String password;
        final String userinfo = url.getUserInfo();
        if (userinfo != null) {
            final String[] loginValues = SPLIT_PATTERN.split(userinfo);
            username = loginValues[0];
            password = loginValues[1];
        }
        else {
            username = EscidocConfiguration.FEDORA_USER;
            password = EscidocConfiguration.FEDORA_PASSWORD;
        }
        return getRequestURL(client, url, username, password);
    }

    /**
     * Get the HttpGet with authentication. The username and password is stored for this connection. Later connection to
     * same URL doesn't require to set the authentication again. Be aware that this could lead to an security issue! To
     * avoid reuse reset the authentication for the URL.
     * 
     * @param url
     *            URL of resource.
     * @param username
     *            User name for authentication.
     * @param password
     *            Password for authentication.
     * @return HttpGet
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public HttpResponse getRequestURL(final URL url, final String username, final String password)
        throws WebserverSystemException {
        return getRequestURL(null, url, username, password);
    }

    /**
     * 
     * @param client
     * @param url
     * @param username
     * @param password
     * @return
     * @throws WebserverSystemException
     */
    public HttpResponse getRequestURL(
        final DefaultHttpClient client, final URL url, final String username, final String password)
        throws WebserverSystemException {
        return get(client, url);
    }

    /**
     * Get the HttpGet with a cookie.
     * 
     * @param url
     *            URL of resource.
     * @param cookie
     *            the Cookie.
     * @return HttpGet
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public HttpResponse getRequestURL(final URL url, final Cookie cookie) throws WebserverSystemException {
        return getRequestURL(null, url, cookie);
    }

    /**
     * 
     * @param client
     * @param url
     * @param cookie
     * @return
     * @throws WebserverSystemException
     */
    public HttpResponse getRequestURL(final DefaultHttpClient client, final URL url, final Cookie cookie)
        throws WebserverSystemException {
        return get(client, url, cookie, null, null);
    }

    /**
     * Get the HttpPost with authentication. Username and password is stored for connection. Later connections to same
     * URL doesn't require to set authentication again. Be aware that this could lead to an security issue! To avoid
     * reuse reset the authentication for the URL.
     * 
     * @param url
     *            URL of resource.
     * @param body
     *            The post body of HTTP request.
     * @param username
     *            User name for authentication.
     * @param password
     *            Password for authentication.
     * @return HttpPost
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public HttpResponse postRequestURL(final URL url, final String body, final String username, final String password)
        throws WebserverSystemException {
        return postRequestURL(null, url, body, username, password);
    }

    /**
     * 
     * @param client
     * @param url
     * @param body
     * @param username
     * @param password
     * @return
     * @throws WebserverSystemException
     */
    public HttpResponse postRequestURL(
        final DefaultHttpClient client, final URL url, final String body, final String username, final String password)
        throws WebserverSystemException {
        return post(client, url, body, null, username, password);
    }

    /**
     * 
     * @param url
     *            URL of resource.
     * @param body
     *            The post body of HTTP request.
     * @param cookie
     *            The cookie to use of the request.
     * @param username
     *            User name for authentication.
     * @param password
     *            Password for authentication.
     * @return HttpPost
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    public HttpResponse postRequestURL(final URL url, final String body, final Cookie cookie)
        throws WebserverSystemException {
        return postRequestURL(null, url, body, cookie);
    }

    /**
     * 
     * @param client
     * @param url
     * @param body
     * @param cookie
     * @return
     * @throws WebserverSystemException
     */
    public HttpResponse postRequestURL(
        final DefaultHttpClient client, final URL url, final String body, final Cookie cookie)
        throws WebserverSystemException {
        return post(client, url, body, cookie, null, null);
    }

    /**
     * @param url
     *            URL of resource.
     * @param body
     *            The post body of HTTP request.
     */
    public HttpResponse postRequestURL(final URL url, final String body) throws WebserverSystemException {
        return postRequestURL(null, url, body);
    }

    /**
     * 
     * @param client
     * @param url
     * @param body
     * @return
     * @throws WebserverSystemException
     */
    public HttpResponse postRequestURL(final DefaultHttpClient client, final URL url, final String body)
        throws WebserverSystemException {
        return post(client, url, body, null, null, null);
    }

    /**
     * Set Authentication to a given {@link DefaultHttpClient} instance.
     * 
     * @param url
     *            URL of resource.
     * @param username
     *            User name for authentication
     * @param password
     *            Password for authentication.
     * @throws WebserverSystemException
     *             e
     */
    public void setAuthentication(
        final DefaultHttpClient client, final URL url, final String username, final String password) {
        final CredentialsProvider credsProvider = new BasicCredentialsProvider();
        final AuthScope authScope = new AuthScope(url.getHost(), AuthScope.ANY_PORT, AuthScope.ANY_REALM);
        final Credentials creds = new UsernamePasswordCredentials(username, password);
        credsProvider.setCredentials(authScope, creds);
        client.setCredentialsProvider(credsProvider);
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
        client.addRequestInterceptor(preemptiveAuth, 0);
    }

    /**
     * @param url
     */
    private boolean isProxyRequired(final URL url) {
        if (NON_PROXY_HOSTS_PATTERN != null) {
            return !NON_PROXY_HOSTS_PATTERN.matcher(url.toString()).find();
        }
        return true;
    }

    /**
     * Get a new {@link HttpClient} instance. Each instance will use the same {@link ThreadSafeClientConnManager}. New
     * instances are being created because of possible configurations done on the {@link HttpClient}. See
     * {@link ConnectionUtility#setAuthentication(DefaultHttpClient, URL, String, String)} for example. If multiple
     * threads are using the same {@link HttpClient} instance, they could overwrite the credentials and everything else
     * of the {@link HttpClient} causing the {@link HttpClient} instance to become unusable for other threads. Therefore
     * new instances will be returned.<br/>
     * The {@link HttpClient} instance will be initialized with {@link DefaultedHttpParams}, which delegates resolution
     * of a parameter to the given default {@link HttpParams} instance, which is read-only, if the parameter is not
     * present in the local one.<br/>
     * <br/>
     * <b>Note:</b> A user of the returned {@link HttpClient} instance shall not modify the configurations on the
     * {@link ClientConnectionManager} of this instance, because this will affect all other users of the
     * {@link HttpClient} instances returned by this ConnectionUtility. If you need to change the behavior of the
     * {@link ClientConnectionManager}, then use the configuration of the {@link HttpClient} because {@link HttpParams}
     * will be handled in a hierarchy. <br/>
     * <br/>
     * <b>TODO:</b> return {@link HttpClient} instead of {@link DefaultHttpClient}.
     * 
     * @param url
     *            The url to call with the httpClient used to decide if proxy has to get used.
     * @return DefaultHttpClient
     * @throws IllegalArgumentException
     *             if and only if the specified <tt>url</tt> is <tt>null</tt>
     */
    public DefaultHttpClient getHttpClient(final URL url) {

        if (url == null) {
            throw new IllegalArgumentException("The specified URL must not be null.");
        }

        if (PROXY_HOST != null && isProxyRequired(url)) {
            return new DefaultHttpClient(CONN_MANAGER, new DefaultedHttpParams(new BasicHttpParams(),
                DEFAULT_HTTP_PARAMS).setParameter(ConnRoutePNames.DEFAULT_PROXY, PROXY_HOST));
        }
        else {
            return new DefaultHttpClient(CONN_MANAGER, new DefaultedHttpParams(new BasicHttpParams(),
                DEFAULT_HTTP_PARAMS));
        }
    }

    /**
     * Call the HttpGet.
     * 
     * @param url
     *            The URL for the HTTP GET method.
     * @return HttpGet
     * @throws WebserverSystemException
     *             If connection failed.
     */
    private HttpResponse get(final DefaultHttpClient client, final URL url) throws WebserverSystemException {
        return get(client, url, null, null, null);
    }

    /**
     * Call the HttpGet.
     * 
     * @param url
     *            The URL for the HTTP GET method.
     * @param cookie
     *            The Cookie.
     * @return HttpGet
     * @throws WebserverSystemException
     *             If connection failed.
     */
    private HttpResponse get(
        final DefaultHttpClient client, final URL url, final Cookie cookie, final String username, final String password)
        throws WebserverSystemException {
        return executeRequest(client, new HttpGet(), url, cookie, username, password);
    }

    /**
     * Call the HttpPost.
     * 
     * @param url
     *            The URL for the HTTP POST request
     * @param body
     *            The body for the POST request.
     * @param cookie
     *            The Cookie.
     * @return HttpResponse
     * @throws WebserverSystemException
     *             If connection failed.
     */
    private HttpResponse post(
        final DefaultHttpClient client, final URL url, final String body, final Cookie cookie, final String username,
        final String password) throws WebserverSystemException {
        try {
            final HttpPost httpPost = new HttpPost();
            httpPost.setEntity(new StringEntity(body, "UTF-8"));
            return executeRequest(client, httpPost, url, cookie, username, password);
        }
        catch (final UnsupportedEncodingException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * 
     * @param request
     * @param cookie
     * @return
     * @throws WebserverSystemException
     */
    private HttpResponse executeRequest(
        final DefaultHttpClient client, final HttpRequestBase request, final URL url, final Cookie cookie,
        final String username, final String password) throws WebserverSystemException {
        try {
            request.setURI(url.toURI());

            if (cookie != null) {
                HttpClientParams.setCookiePolicy(request.getParams(), CookiePolicy.BEST_MATCH);
                request.setHeader("Cookie", cookie.getName() + '=' + cookie.getValue());
            }

            DefaultHttpClient clientToUse = null;
            if (client == null) {
                clientToUse = getHttpClient(request.getURI().toURL());
            }
            else {
                clientToUse = client;
            }

            if (username != null && password != null) {
                setAuthentication(clientToUse, url, username, password);
            }

            final HttpResponse httpResponse = clientToUse.execute(request);

            final int responseCode = httpResponse.getStatusLine().getStatusCode();
            if (responseCode / HTTP_RESPONSE_CLASS != HttpServletResponse.SC_OK / HTTP_RESPONSE_CLASS) {
                final String errorPage = readResponse(httpResponse);
                throw new WebserverSystemException("HTTP connection to \"" + request.getURI().toString()
                    + "\" failed: " + errorPage);
            }

            return httpResponse;
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
        catch (final URISyntaxException e) {
            throw new WebserverSystemException("Illegal URL '" + url + "'.", e);
        }
    }

    /**
     * Reads the response as String from the HttpResponse class.
     * 
     * @param httpResponse
     *            The HttpResponse.
     * @return String.
     * @throws WebserverSystemException
     *             Thrown if connection failed.
     */
    private static String readResponse(final HttpResponse httpResponse) throws WebserverSystemException {
        try {
            return EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e.getMessage(), e);
        }
    }
}