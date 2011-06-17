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
import org.escidoc.core.services.fedora.FedoraServiceClient;
import org.fcrepo.client.FedoraClient;
import org.fcrepo.server.access.FedoraAPIA;
import org.fcrepo.server.management.FedoraAPIM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedResource;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
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

    private StackObjectPool fedoraClientPool;

    // Nutzerzugriffe
    private StackObjectPool apiaPool;

    // Handler für managed REsourcen
    private StackObjectPool apimPool;

    private static final int HTTP_MAX_CONNECTIONS_PER_HOST = 30;

    private static final int HTTP_MAX_TOTAL_CONNECTIONS = 90;

    private String fedoraUser;

    private String fedoraPassword;

    private String fedoraUrl;

    private String identifierPrefix;

    private DefaultHttpClient httpClient;

    @Autowired
    private FedoraServiceClient fedoraServiceClient;

    @Autowired
    @Qualifier("business.Utility")
    private Utility utility;

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
                fedoraServiceClient.sync();
            }
        }
        return pid;
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
