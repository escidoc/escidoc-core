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

package de.escidoc.core.http;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpClientBuilderImpl extends HttpClientBuilder {

    private static final int MAX_TOTAL_CONNECTIONS = 90;
    private static final int MAX_CONNECTIONS_PER_HOST = 30;

    private DefaultHttpClient httpClient;
    private int maxTotalConnections = MAX_TOTAL_CONNECTIONS;
    private int maxConnectionsPerHost = MAX_CONNECTIONS_PER_HOST;

    public HttpClientBuilderImpl() {
        this.initHttpClient();
    }

    @Override
    public HttpClientBuilder withMaxTotalConnections(final int maxTotalConnections) {
        this.maxTotalConnections = maxTotalConnections;
        return this;
    }

    @Override
    public HttpClientBuilder withMaxConnectionsPerHost(final int maxConnectionsPerHost) {
        this.maxConnectionsPerHost = maxConnectionsPerHost;
        return this;
    }

    @Override
    public HttpClientBuilder withCredentialsProvider(final CredentialsProvider credentialsProvider) {
        this.httpClient.setCredentialsProvider(credentialsProvider);
        return this;
    }

    @Override
    public HttpClientBuilder withUsernamePasswordCredentials(final String urlString, final String username, final String password) {
        final URL url;
        try {
            url = new URL(urlString);
        } catch (final MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url '" + urlString + "'.", e);
        }
        final AuthScope authScope = new AuthScope(url.getHost(), AuthScope.ANY_PORT, AuthScope.ANY_REALM);
        final Credentials usernamePasswordCredentials = new UsernamePasswordCredentials(username, password);
        this.httpClient.getCredentialsProvider().setCredentials(authScope, usernamePasswordCredentials);
        return this;
    }

    private void initHttpClient() {
        final HttpParams httpParams = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(httpParams, this.maxTotalConnections);
        final ConnPerRoute connPerRoute = new ConnPerRouteBean(this.maxConnectionsPerHost);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
        final Scheme httpSchema = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
        final SchemeRegistry schemaRegistry = new SchemeRegistry();
        schemaRegistry.register(httpSchema);
        final ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(httpParams, schemaRegistry);
        this.httpClient = new DefaultHttpClient(clientConnectionManager, httpParams);
        // don't wait for auth request
        this.httpClient.addRequestInterceptor(new PreemtiveAuthHttpRequestInterceptor(), 0);
    }

    @Override
    public HttpClient build() {
        final HttpClient returnValue = this.httpClient;
        this.initHttpClient();
        return returnValue;
    }
}
