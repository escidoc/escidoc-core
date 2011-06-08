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

package de.escidoc.core.common.business.fedora;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.PoolUtils;
import org.apache.commons.pool.impl.StackObjectPool;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
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
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.fcrepo.client.FedoraClient;
import org.fcrepo.client.HttpInputStream;
import org.fcrepo.server.access.FedoraAPIA;
import org.fcrepo.server.management.FedoraAPIM;
import org.fcrepo.server.types.gen.Datastream;
import org.fcrepo.server.types.gen.MIMETypedStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedResource;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.security.PreemptiveAuthInterceptor;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * An utility class for Fedora requests.<br />
 * This class uses pools for the fedora interfaces.
 * 
 * @author Rozita Friedman
 */
@ManagedResource(objectName = "eSciDocCore:name=FedoraUtility", description = "The utility class to access the fedora repository.", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class FedoraUtility {

    public static final int SYNC_RETRIES = 10;

    private static final Logger LOGGER = LoggerFactory.getLogger(FedoraUtility.class);

    private static final int MAX_IDLE = 5;

    private static final int INIT_IDLE_CAPACITY = 2;

    private static final String FOXML_FORMAT = "info:fedora/fedora-system:FOXML-1.1";

    private static final int HTTP_OK = 200;

    private StackObjectPool fedoraClientPool;

    // Nutzerzugriffe
    private StackObjectPool apiaPool;

    // Handler für managed REsourcen
    private StackObjectPool apimPool;

    /**
     * Query string to trigger a sync via the fedora REST interface.
     */
    private String syncRestQuery;

    // TODO
    // in configurationsdatei auslagern--> escidoc config*

    // escidoc-core.properties // default config
    // escidoc-core.custom.properties --> f�hrend
    private static final int HTTP_MAX_CONNECTIONS_PER_HOST = 30;

    private static final int HTTP_MAX_TOTAL_CONNECTIONS = 90;

    private String fedoraUser;

    private String fedoraPassword;

    private String fedoraUrl;

    private String identifierPrefix;

    private DefaultHttpClient httpClient;

    private TripleStoreUtility tripleStoreUtility;

    @Autowired
    @Qualifier("business.Utility")
    private Utility utility;

    /**
     * Get the names of data streams selected by alternateId. Only the first
     * value of the altIds is compared.
     * 
     * @param pid
     *            The id of the Fedora object.
     * @param altId
     *            The alternate ID filter.
     * @return Vector of data stream names where the first altId is equal.
     * @throws FedoraSystemException
     *             Thrown if instantiation of Fedora connection fail.
     */
    public Collection<String> getDatastreamNamesByAltId(final String pid, final String altId)
        throws FedoraSystemException {
        final Collection<String> names = new ArrayList<String>();

        final Datastream[] ds = getDatastreamsInformation(pid);

        for (final Datastream d : ds) {
            final String[] altIDs = d.getAltIDs();
            if (altIDs.length > 0 && altIDs[0].equals(altId)) {
                names.add(d.getID());
            }
        }
        return names;
    }

    /**
     * Store an object.
     * 
     * @param foxml
     *            The foxml representation of the object.
     * @param syncTripleStore
     *            whether the triples should be flushed
     * @return The fedora pid of the stored object.
     * @throws WebserverSystemException
     *             Thrown if synchronization of Fedora fails.
     * @throws FedoraSystemException
     *             Thrown in case of a Fedora error.
     */
    public String storeObjectInFedora(final String foxml, final boolean syncTripleStore) throws FedoraSystemException,
        WebserverSystemException {

        String pid;

        FedoraAPIM apim = borrowApim();
        try {
            pid = apim.ingest(foxml.getBytes(XmlUtility.CHARACTER_ENCODING), FOXML_FORMAT, "eSciDoc object created");
        }
        catch (final Exception e) {

            // Workaround - try a secound connection
            if (e.getMessage().contains("Address already in use: connect")) {
                LOGGER.warn("APIM storeObjectInFedora(..) " + e);
                invalidateApimObject(apim);
                apim = borrowApim();

                try {
                    pid =
                        apim.ingest(foxml.getBytes(XmlUtility.CHARACTER_ENCODING), FOXML_FORMAT,
                            "eSciDoc object created");
                }
                catch (final Exception e1) {
                    preventWrongLogging(e1);
                    throw new FedoraSystemException("Ingest to Fedora failed. ", e);
                }
            }

            preventWrongLogging(e);
            throw new FedoraSystemException("Ingest to Fedora failed. ", e);
        }
        finally {
            returnApim(apim);
            if (syncTripleStore) {
                sync();
            }
        }
        return pid;
    }

    /**
     * The method retrieves metadata for all datastreams of the fedora object
     * with provided id as Array.
     * 
     * @param pid
     *            provided id
     * @return Fedora information set about all datastreams of the requested
     *         object.
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     */
    Datastream[] getDatastreamsInformation(final String pid) throws FedoraSystemException {
        return getDatastreamsInformation(pid, null);
    }

    /**
     * The method retrieves metadata for all datastreams of the fedora object
     * with provided id as Array.
     * 
     * @param pid
     *            provided id
     * @param timestamp
     *            Timestamp related to datastream version to retrieve. May be
     *            null.
     * @return metadata of datastreams.
     * @throws FedoraSystemException
     *             Thrown if Fedora request failed.
     */
    public Datastream[] getDatastreamsInformation(final String pid, final String timestamp)
        throws FedoraSystemException {
        Datastream[] datastreams = null;
        FedoraAPIM apim = borrowApim();
        try {
            // work around to prevent null returns
            datastreams = apim.getDatastreams(pid, timestamp, null);
            if (datastreams == null) {
                LOGGER.warn("APIM getDatastreams(" + pid + ", ..) returns null.");
                returnApim(apim);
                apim = borrowApim();
                datastreams = apim.getDatastreams(pid, timestamp, null);
            }
        }
        catch (final RemoteException e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on APIM getDatastreams(..)");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on APIM getDatastreams(..)", e);
            }
            invalidateApimObject(apim);
            apimPool.clear();
            apim = borrowApim();
            try {
                datastreams = apim.getDatastreams(pid, timestamp, null);
            }
            catch (final RemoteException e1) {
                final String message =
                    "Error on retrieve datastream (pid='" + pid + "', timestamp='" + timestamp + "') ";
                LOGGER.warn(message);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(message, e1);
                }
                throw new FedoraSystemException(message, e);
            }
        }
        finally {
            returnApim(apim);
        }
        return datastreams;
    }

    public byte[] getDissemination(final String pid, final String contentModelPid, final String name)
        throws FedoraSystemException {

        // TODO check if APIA.listMethods is sufficient for retrieving dynamic
        // resource names.
        final FedoraAPIA apia = borrowApia();
        try {
            return apia
                .getDissemination(pid,
                    "sdef:" + contentModelPid.replace(":", Constants.COLON_REPLACEMENT_PID) + '-' + name, name, null,
                    null).getStream();
        }
        catch (final Exception e) {
            throw new FedoraSystemException("Failed to get result of dynamic resource.", e);
        }
        finally {
            returnApia(apia);
        }
    }

    /**
     * Send a risearch request to fedora repository with flag flush set to true.
     * Call reinialize() in order to reset a Table Manager for the Triple Store.
     * 
     * @throws FedoraSystemException
     *             Thrown if TripleStore synchronization failed.
     * @throws WebserverSystemException
     *             Thrown if TripleStore initialization failed.
     */
    public void sync() throws FedoraSystemException, WebserverSystemException {

        FedoraClient fc = null;
        try {
            fc = borrowFedoraClient();
            final HttpInputStream httpInStr = fc.get(this.syncRestQuery, true);
            if (httpInStr.getStatusCode() != HTTP_OK) {
                throw new FedoraSystemException("Triplestore sync failed.");
            }
            tripleStoreUtility.reinitialize();
        }
        catch (final TripleStoreSystemException tse) {
            throw new WebserverSystemException(tse);
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
        finally {
            returnFedoraClient(fc);
        }
    }

    /**
     * Send a GET request to Fedora.
     * 
     * @param queryString
     *            query string
     * @return response from Fedora as input stream
     * @throws FedoraSystemException
     *             Thrown in case of an internal error.
     * @throws IOException
     *             Thrown if the GET request to Fedora failed.
     */
    public HttpInputStream query(final String queryString) throws FedoraSystemException, IOException {
        HttpInputStream result = null;
        FedoraClient fc = null;

        try {
            fc = borrowFedoraClient();
            result = fc.get(queryString, true);
            if (result.getStatusCode() != HTTP_OK) {
                throw new FedoraSystemException("GET request to Fedora failed with error " + result.getStatusCode());
            }
        }
        finally {
            returnFedoraClient(fc);
        }
        return result;
    }

    /**
     * Borrows a {@link FedoraClient} from the pool.
     * 
     * @return Returns a {@link FedoraClient}.
     * @throws FedoraSystemException
     *             Thrown in case of an internal error.
     */
    private FedoraClient borrowFedoraClient() throws FedoraSystemException {
        try {
            return (FedoraClient) fedoraClientPool.borrowObject();
        }
        catch (final Exception e) {
            throw convertPoolException(e);
        }
    }

    /**
     * Returns a {@link FedoraClient} to the pool.
     * 
     * @param fedoraClient
     *            The {@link FedoraClient} to be returned to the pool.
     * @throws FedoraSystemException
     *             Thrown in case of an internal error.
     */
    private void returnFedoraClient(final FedoraClient fedoraClient) throws FedoraSystemException {
        try {
            fedoraClientPool.returnObject(fedoraClient);
        }
        catch (final Exception e) {
            throw convertPoolException(e);
        }
    }

    /**
     * Borrows a {@link FedoraAPIA} from the pool.
     * 
     * @return Returns a {@link FedoraAPIA}.
     * @throws FedoraSystemException
     *             Thrown in case of an internal error.
     */
    private FedoraAPIA borrowApia() throws FedoraSystemException {
        try {
            return (FedoraAPIA) apiaPool.borrowObject();
        }
        catch (final Exception e) {
            throw convertPoolException(e);
        }
    }

    /**
     * Returns a {@link FedoraAPIA} to the pool.
     * 
     * @param fedoraApia
     *            The {@link FedoraAPIA} to be returned to the pool.
     * @throws FedoraSystemException
     *             Thrown in case of an internal error.
     */
    private void returnApia(final FedoraAPIA fedoraApia) throws FedoraSystemException {
        try {
            apiaPool.returnObject(fedoraApia);
        }
        catch (final Exception e) {
            throw convertPoolException(e);
        }
    }

    /**
     * Borrows a {@link FedoraAPIM} from the pool.
     * 
     * @return Returns a {@link FedoraAPIM}.
     * @throws FedoraSystemException
     *             Thrown in case of an internal error.
     */
    private FedoraAPIM borrowApim() throws FedoraSystemException {
        try {
            return (FedoraAPIM) apimPool.borrowObject();
        }
        catch (final Exception e) {
            throw convertPoolException(e);
        }
    }

    /**
     * Returns a {@link FedoraAPIM} to the pool.
     * 
     * @param fedoraApim
     *            The {@link FedoraAPIM} to be returned to the pool.
     * @throws FedoraSystemException
     *             Thrown in case of an internal error.
     */
    private void returnApim(final FedoraAPIM fedoraApim) throws FedoraSystemException {
        try {
            apimPool.returnObject(fedoraApim);
        }
        catch (final Exception e) {
            throw convertPoolException(e);
        }
    }

    /**
     * Invalidate a {@link FedoraAPIA} to the pool.
     * 
     * @param fedoraApia
     *            The {@link FedoraAPIA} to be returned to the pool.
     * @throws FedoraSystemException
     *             Thrown in case of an internal error.
     */
    private void invalidateApiaObject(final FedoraAPIA fedoraApia) throws FedoraSystemException {

        try {
            apiaPool.invalidateObject(fedoraApia);
        }
        catch (final Exception e) {
            throw convertPoolException(e);
        }
    }

    /**
     * Invalidate a {@link FedoraAPIM} to the pool.
     * 
     * @param fedoraApim
     *            The {@link FedoraAPIM} to be returned to the pool.
     * @throws FedoraSystemException
     *             Thrown in case of an internal error.
     */
    private void invalidateApimObject(final FedoraAPIM fedoraApim) throws FedoraSystemException {
        try {
            apimPool.invalidateObject(fedoraApim);
        }
        catch (final Exception e) {
            throw convertPoolException(e);
        }
    }

    /**
     * Converts an exception thrown during a pool operation to a
     * {@link FedoraSystemException}.
     * 
     * @param e
     *            The {@link Exception} to be converted.
     * @return Returns the {@link FedoraSystemException}
     */
    private static FedoraSystemException convertPoolException(final Exception e) {
        return e instanceof FedoraSystemException ? (FedoraSystemException) e : new FedoraSystemException(e
            .getMessage(), e);
    }

    @PostConstruct
    private void init() throws IOException, MalformedURLException, ServiceException {
        this.fedoraClientPool =
            new StackObjectPool(PoolUtils.synchronizedPoolableFactory(new BasePoolableObjectFactory() {
                /**
                 * See Interface for functional description.
                 * 
                 * @return
                 * @see BasePoolableObjectFactory #makeObject()
                 */
                @Override
                public Object makeObject() throws MalformedURLException {
                    return new FedoraClient(FedoraUtility.this.fedoraUrl, FedoraUtility.this.fedoraUser,
                        FedoraUtility.this.fedoraPassword);
                }
            }), MAX_IDLE, INIT_IDLE_CAPACITY);

        this.apiaPool = new StackObjectPool(PoolUtils.synchronizedPoolableFactory(new BasePoolableObjectFactory() {
            /**
             * See Interface for functional description.
             * 
             * @return
             * @see BasePoolableObjectFactory #makeObject()
             */
            @Override
            public Object makeObject() throws IOException, MalformedURLException, ServiceException {
                return new FedoraClient(FedoraUtility.this.fedoraUrl, FedoraUtility.this.fedoraUser,
                    FedoraUtility.this.fedoraPassword).getAPIA();
            }
        }), MAX_IDLE, INIT_IDLE_CAPACITY);

        this.apimPool = new StackObjectPool(new BasePoolableObjectFactory() {
            /**
             * See Interface for functional description.
             * 
             * @return
             * @see BasePoolableObjectFactory #makeObject()
             */
            @Override
            public Object makeObject() throws IOException, MalformedURLException, ServiceException {
                return new FedoraClient(FedoraUtility.this.fedoraUrl, FedoraUtility.this.fedoraUser,
                    FedoraUtility.this.fedoraPassword).getAPIM();
            }
        }, MAX_IDLE, INIT_IDLE_CAPACITY);

        this.syncRestQuery = this.fedoraUrl + "/risearch?flush=true";
    }

    /**
     * Returns a HttpClient object configured with credentials to access Fedora
     * URLs.
     * 
     * @return A HttpClient object configured with credentials to access Fedora
     *         URLs.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    DefaultHttpClient getHttpClient() throws WebserverSystemException {
        try {
            if (this.httpClient == null) {
                final HttpParams params = new BasicHttpParams();
                ConnManagerParams.setMaxTotalConnections(params, HTTP_MAX_TOTAL_CONNECTIONS);

                final ConnPerRoute connPerRoute = new ConnPerRouteBean(HTTP_MAX_CONNECTIONS_PER_HOST);
                ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

                final Scheme http = new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
                final SchemeRegistry sr = new SchemeRegistry();
                sr.register(http);
                final ClientConnectionManager cm = new ThreadSafeClientConnManager(params, sr);

                this.httpClient = new DefaultHttpClient(cm, params);
                final URL url = new URL(this.fedoraUrl);
                final CredentialsProvider credsProvider = new BasicCredentialsProvider();

                final AuthScope authScope = new AuthScope(url.getHost(), AuthScope.ANY_PORT, AuthScope.ANY_REALM);
                final Credentials creds = new UsernamePasswordCredentials(this.fedoraUser, this.fedoraPassword);
                credsProvider.setCredentials(authScope, creds);

                httpClient.setCredentialsProvider(credsProvider);
            }

            // don't wait for auth request
            final HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {

                @Override
                public void process(final HttpRequest request, final HttpContext context) throws HttpException,
                    IOException {

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

            httpClient.addRequestInterceptor(preemptiveAuth, 0);

            // try only BASIC auth; skip to test NTLM and DIGEST

            return this.httpClient;
        }
        catch (final MalformedURLException e) {
            throw new WebserverSystemException("Fedora URL from configuration malformed.", e);
        }
    }

    /**
     * Makes a HTTP GET request to Fedora URL expanded by given local URL.
     * 
     * @param localUrl
     *            The Fedora local URL. Should start with the '/' after the
     *            webcontext path (usually "fedora"). E.g. if
     *            http://localhost:8080/fedora/get/... then localUrl is /get/...
     * @return the content of the URL Request.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public InputStream requestFedoraURL(final String localUrl) throws WebserverSystemException {
        final InputStream fedoraResponseStream;
        try {
            final DefaultHttpClient httpClient = getHttpClient();
            final HttpContext localcontext = new BasicHttpContext();
            final BasicScheme basicAuth = new BasicScheme();
            localcontext.setAttribute("preemptive-auth", basicAuth);
            httpClient.addRequestInterceptor(new PreemptiveAuthInterceptor(), 0);
            final HttpGet httpGet = new HttpGet(this.fedoraUrl + localUrl);
            final HttpResponse httpResponse = httpClient.execute(httpGet);
            final int responseCode = httpResponse.getStatusLine().getStatusCode();
            if (responseCode != HttpServletResponse.SC_OK) {
                EntityUtils.consume(httpResponse.getEntity());
                throw new WebserverSystemException("Bad response code '" + responseCode + "' requesting '"
                    + this.fedoraUrl + localUrl + "'.", new FedoraSystemException(httpResponse
                    .getStatusLine().getReasonPhrase()));
            }
            fedoraResponseStream = httpResponse.getEntity().getContent();

        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }

        return fedoraResponseStream;
    }

    /**
     * @param fedoraUrl
     *            the fedoraUrl to inject
     */
    public void setFedoraUrl(final String fedoraUrl) {

        if (this.fedoraUrl != null) {
            throw new NotWritablePropertyException(this.getClass(), "fedoraUrl", "Property must not be set twice.");
        }
        this.fedoraUrl = fedoraUrl;
    }

    /**
     * @param fedoraUser
     *            the fedoraUser to inject
     */
    public void setFedoraUser(final String fedoraUser) {

        if (this.fedoraUser != null) {
            throw new NotWritablePropertyException(this.getClass(), "fedoraUser", "Property must not be set twice.");
        }
        this.fedoraUser = fedoraUser;
    }

    /**
     * @param fedoraPassword
     *            the fedoraPassword to inject
     */
    public void setFedoraPassword(final String fedoraPassword) {

        if (this.fedoraPassword != null) {
            throw new NotWritablePropertyException(this.getClass(), "fedoraPassword", "Property must not be set twice.");
        }
        this.fedoraPassword = fedoraPassword;
    }

    /**
     * @param identifierPrefix
     *            the identifierPrefix to inject
     */
    public void setIdentifierPrefix(final String identifierPrefix) {

        this.identifierPrefix = identifierPrefix;
    }

    /**
     * Injects the TripleStore utility.
     * 
     * @param tripleStoreUtility
     *            TripleStoreUtility from Spring
     */
    public void setTripleStoreUtility(final TripleStoreUtility tripleStoreUtility) {
        this.tripleStoreUtility = tripleStoreUtility;
    }

    /**
     * FIXME (SWA) crud, crud, crud!
     * <p/>
     * This should only prevent to write the FoXML into log if the resource URL
     * is wrong or resource in in accessible. If the exception is required as
     * Fedora Exception or not, like in the case of Components, is decided in
     * the upper methods.
     * 
     * @param e
     *            Exception
     */
    private void preventWrongLogging(final Exception e) {

        final Pattern patternErrorGetting =
            Pattern.compile("fedora.server.errors.GeneralException: Error getting", Pattern.CASE_INSENSITIVE);
        final Pattern patternMalformedUrl =
            Pattern.compile("fedora.server.errors.ObjectIntegrityException: "
                + "FOXML IO stream was bad : Malformed URL");

        final Matcher matcherErrorGetting = patternErrorGetting.matcher(e.getMessage());
        final Matcher matcherMalformedUrl = patternMalformedUrl.matcher(e.getMessage());

        if (matcherErrorGetting.find() || matcherMalformedUrl.find()) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Failed to load content. ");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Failed to load content. ", e);
            }
        }
        else {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Failed to modify Fedora datastream.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Failed to modify Fedora datastream.", e);
            }
        }

    }

}
