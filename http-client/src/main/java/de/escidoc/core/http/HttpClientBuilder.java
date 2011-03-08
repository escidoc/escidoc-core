package de.escidoc.core.http;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;

public abstract class HttpClientBuilder {

    public static HttpClientBuilder createHttpClient() {
        return new HttpClientBuilderImpl();
    }

    public abstract HttpClientBuilder withMaxTotalConnections(int maxTotalConnections);

    public abstract HttpClientBuilder withMaxConnectionsPerHost(int maxConnectionsPerHost);

    public abstract HttpClientBuilder withCredentialsProvider(CredentialsProvider credentialsProvider);

    public abstract HttpClientBuilder withUsernamePasswordCredentials(String urlString, String username, String password);

    public abstract HttpClient build();

}
