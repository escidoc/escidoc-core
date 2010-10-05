package de.escicore.http;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
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


/**
 * @author <a href="mailto:mail@eduard-hildebrandt.de">Eduard Hildebrandt</a>
 */
public class HttpClientBuilderImpl extends HttpClientBuilder {

    private static final int MAX_TOTAL_CONNECTIONS = 90;
    private static final int MAX_CONNECTIONS_PER_HOST = 30;

    private DefaultHttpClient httpClient;
    private int maxTotalConnections = MAX_TOTAL_CONNECTIONS;
    private int maxConnectionsPerHost = MAX_CONNECTIONS_PER_HOST;

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
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid url '" + urlString + "'.", e);
        }
        final AuthScope authScope = new AuthScope(url.getHost(), AuthScope.ANY_PORT, AuthScope.ANY_REALM);
        final UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(username, password);
        this.httpClient.getCredentialsProvider().setCredentials(authScope, usernamePasswordCredentials);
        return this;
    }

    @Override
    public HttpClient build() {
        final HttpParams httpParams = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(httpParams, this.maxTotalConnections);
        final ConnPerRouteBean connPerRoute = new ConnPerRouteBean(this.maxConnectionsPerHost);
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, connPerRoute);
        final Scheme httpSchema = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
        final SchemeRegistry schemaRegistry = new SchemeRegistry();
        schemaRegistry.register(httpSchema);
        final ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(httpParams, schemaRegistry);
        this.httpClient = new DefaultHttpClient(clientConnectionManager, httpParams);
        // don't wait for auth request
        this.httpClient.addRequestInterceptor(new PreemtiveAuthHttpRequestInterceptor(), 0);
        return this.httpClient;
    }
}
