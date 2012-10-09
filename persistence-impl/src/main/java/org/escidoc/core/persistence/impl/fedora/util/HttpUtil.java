package org.escidoc.core.persistence.impl.fedora.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpUtil {

    private static enum RESPONSE_TYPE {
        BYTE_ARRAY, INPUT_STREAM, READER
    };

    private String cookie = null;

    private Credentials credentials = null;

    private Object get(final String url, RESPONSE_TYPE responseType)
        throws AuthenticationException, IOException {
        Object result = null;
        HttpGet method = new HttpGet(url);

        setAuthHeader(method);

        HttpResponse response = new DefaultHttpClient().execute(method);

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                if (responseType == RESPONSE_TYPE.BYTE_ARRAY) {
                    ByteArrayOutputStream output = new ByteArrayOutputStream();

                    entity.writeTo(output);
                    result = output.toByteArray();
                    output.close();
                }
                else if (responseType == RESPONSE_TYPE.INPUT_STREAM) {
                    result = entity.getContent();
                }
                else if (responseType == RESPONSE_TYPE.READER) {
                    Header contentEncodingHeader = entity.getContentEncoding();

                    if (contentEncodingHeader == null
                        || contentEncodingHeader.getValue().trim().length() == 0) {
                        throw new IOException(
                            "No content encoding found in response. Try getAsStream()");
                    }
                    result =
                        new InputStreamReader(entity.getContent(),
                            contentEncodingHeader.getValue());
                }
            }
        }
        else {
            throw new IOException(response.getStatusLine().getReasonPhrase()
                + ": " + url);
        }
        return result;
    }

    public byte[] getAsByteArray(final String url) throws IOException,
        AuthenticationException {
        return (byte[]) get(url, RESPONSE_TYPE.BYTE_ARRAY);
    }

    public InputStream getAsStream(final String url) throws IOException,
        AuthenticationException {
        return (InputStream) get(url, RESPONSE_TYPE.INPUT_STREAM);
    }

    public Reader getAsReader(final String url) throws IOException,
        AuthenticationException {
        return (Reader) get(url, RESPONSE_TYPE.READER);
    }

    public String getAsString(final String url) throws AuthenticationException,
        IOException {
        StringBuffer result = new StringBuffer();

        Reader reader = this.getAsReader(url);
        BufferedReader bReader = new BufferedReader(reader);

        String line = bReader.readLine();
        while (line != null) {
            result.append(line);
            line = bReader.readLine();
        }

        return result.toString();
    }

    public InputStream post(final String url) throws IOException,
        AuthenticationException {
        InputStream result = null;
        HttpPost method = null;

        try {
            method = new HttpPost(url);
            setAuthHeader(method);

            HttpResponse response = new DefaultHttpClient().execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    result = entity.getContent();
                }
            }
            else {
                throw new IOException(response
                    .getStatusLine().getReasonPhrase());
            }
        }
        finally {
            if ((result == null) && (method != null)) {
                method.abort();
            }
        }
        return result;
    }

    public void post(final String url, String content) throws IOException,
        AuthenticationException {
        HttpPost method = null;

        try {
            method = new HttpPost(url);
            setAuthHeader(method);
            // FIXME Writing xml here is an error point. StringEntity selects
            // charset.
            StringEntity reqEntity = new StringEntity(content);

            reqEntity.setContentType("text/xml");
            method.setEntity(reqEntity);

            HttpResponse response = new DefaultHttpClient().execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    entity.consumeContent();
                }
            }
            else {
                throw new IOException(response
                    .getStatusLine().getReasonPhrase());
            }
        }
        finally {
            if (method != null) {
                method.abort();
            }
        }
    }

    public void put(final String url, String content) throws IOException,
        AuthenticationException {
        HttpPut method = null;

        try {
            method = new HttpPut(url);
            setAuthHeader(method);
            StringEntity reqEntity = new StringEntity(content);

            reqEntity.setContentType("text/xml");
            method.setEntity(reqEntity);

            HttpResponse response = new DefaultHttpClient().execute(method);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    entity.consumeContent();
                }
            }
            else {
                throw new IOException(response
                    .getStatusLine().getReasonPhrase());
            }
        }
        finally {
            if (method != null) {
                method.abort();
            }
        }
    }

    private void setAuthHeader(HttpRequest method)
        throws AuthenticationException {
        if (credentials != null) {
            BasicScheme scheme = new BasicScheme();
            Header authorizationHeader =
                scheme.authenticate(credentials, method);

            method.addHeader(authorizationHeader);
        }
        if (cookie != null) {
            method.addHeader("Cookie", cookie);
        }
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
}
