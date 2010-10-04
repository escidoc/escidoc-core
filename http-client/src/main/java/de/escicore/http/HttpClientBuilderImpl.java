package de.escicore.http;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
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
