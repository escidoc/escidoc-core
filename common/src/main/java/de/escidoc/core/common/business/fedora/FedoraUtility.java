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

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.FileSystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.security.PreemptiveAuthInterceptor;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.apache.axis.types.NonNegativeInteger;
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
import org.fcrepo.server.types.gen.ObjectProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.servlet.http.HttpServletResponse;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An utility class for Fedora requests.<br />
 * This class uses pools for the fedora interfaces.
 * 
 * @author Rozita Friedman
 */
@ManagedResource(objectName = "eSciDocCore:name=FedoraUtility", description = "The utility class to access the fedora repository.", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class FedoraUtility implements InitializingBean {

    public static final String DATASTREAM_STATUS_DELETED = "D";

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

    // The methods exposed via jmx

    /**
     * Gets the FoXML version.
     * 
     * @return The version of FOXML
     */
    @ManagedAttribute(description = "The FoXML version.")
    public String getFoxmlVersion() {

        return FOXML_FORMAT;
    }

    /**
     * Gets the URL to the Fedora repository.
     * 
     * @return Returns the URL to the Fedora repository.
     */
    @ManagedAttribute(description = "The URL to the fedora repository.")
    public String getFedoraUrl() {

        return this.fedoraUrl;
    }

    /**
     * Gets the number of active apia connections.
     * 
     * @return Returns the number of active apia connections.
     */
    @ManagedAttribute(description = "The number of active apia connections.")
    public int getApiaPoolNumActive() {

        return apiaPool.getNumActive();
    }

    /**
     * Gets the number of idle apia connections.
     * 
     * @return Returns the number of idle apia connections.
     */
    @ManagedAttribute(description = "The number of idle apia connections.")
    public int getApiaPoolNumIdle() {

        return apiaPool.getNumIdle();
    }

    /**
     * Clears the pool of apia connections.
     */
    @ManagedOperation(description = "Clear the pool of apia connections.")
    public void clearApiaPool() {

        apiaPool.clear();
    }

    /**
     * Gets the number of active apim connections.
     * 
     * @return Returns the number of active apim connections.
     */
    @ManagedAttribute(description = "The number of active apim connections.")
    public int getApimPoolNumActive() {

        return apimPool.getNumActive();
    }

    /**
     * Gets the number of idle apim connections.
     * 
     * @return Returns the number of idle apim connections.
     */
    @ManagedAttribute(description = "The number of idle apim connections.")
    public int getApimPoolNumIdle() {

        return apimPool.getNumIdle();
    }

    /**
     * Clears the pool of apim connections.
     */
    @ManagedOperation(description = "Clear the pool of apim connections.")
    void clearApimPool() {

        apimPool.clear();
    }

    /**
     * Gets the number of active fedora clients.
     * 
     * @return Returns the number of active fedora clients.
     */
    @ManagedAttribute(description = "The number of active fedora clients.")
    public int getFedoraClientPoolNumActive() {

        return fedoraClientPool.getNumActive();
    }

    /**
     * Gets the number of idle fedora clients.
     * 
     * @return Returns the number of idle fedora clients.
     */
    @ManagedAttribute(description = "The number of idle fedora clients.")
    public int getFedoraClientPoolNumIdle() {

        return fedoraClientPool.getNumIdle();
    }

    /**
     * Clears the pool of fedora clients.
     */
    @ManagedOperation(description = "Clear the pool of fedora clients.")
    public void clearFedoraClientPool() {

        fedoraClientPool.clear();
    }

    /**
     * The method returns the foxml of the Fedora object with provided id via Fedora APIM-Webservice export().
     * 
     * @param pid
     *            Fedora object pid.
     * @return content of the fedora object foxml as byte []
     * @throws FedoraSystemException
     *             Thrown if retrieving FOXML of object failed.
     */
    public byte[] getObjectFoxml(final String pid) throws FedoraSystemException {

        final FedoraAPIM apim = borrowApim();
        try {
            return apim.export(pid, FOXML_FORMAT, "public");
        }
        catch (final RemoteException e) {
            throw new FedoraSystemException("APIM export failure: " + e.getMessage(), e);
        }
        finally {
            returnApim(apim);
        }
    }

    /**
     * Get next object IDs from Fedora (Fedora use the name PID for there identifier).
     * 
     * @param noOfPids
     *            Number of IDs which are to request.
     * @return Array with object IDs.
     * @throws FedoraSystemException
     *             Thrown if collection values from Fedora failed.
     */
    public String[] getNextPID(final int noOfPids) throws FedoraSystemException {

        String[] pids = null;
        final NonNegativeInteger number = new NonNegativeInteger(String.valueOf(noOfPids));

        final FedoraAPIM apim = borrowApim();
        try {
            pids = apim.getNextPID(number, this.identifierPrefix);
        }
        catch (final RemoteException e) {
            throw new FedoraSystemException("Unable to get Obids from Fedora: " + e.getMessage(), e);
        }
        finally {
            returnApim(apim);
        }
        return pids;
    }

    /**
     * The method sets the data stream status to a provided value.
     * 
     * @param pid
     *            The Fedora object id.
     * @param dsName
     *            The name of the datastream
     * @param dsState
     *            The status of the datastream.
     * @return Timestamp of the datastream.
     * @throws FedoraSystemException
     *             Thrown if set datastream at Fedora failed.
     */
    public String setDatastreamState(final String pid, final String dsName, final String dsState)
        throws FedoraSystemException {

        String timestamp = null;
        final FedoraAPIM apim = borrowApim();
        try {
            timestamp = apim.setDatastreamState(pid, dsName, dsState, "ds state is changed.");
        }
        catch (final RemoteException e) {
            throw new FedoraSystemException("APIM setDatastreamState failure: " + e.getMessage(), e);
        }
        finally {
            returnApim(apim);
        }
        return timestamp;
    }

    /**
     * The method fetches the {@link MIMETypedStream} of the datastream with provided data stream id from Fedora-object
     * with provided pid via Fedora APIA-Webservice getDatastreamDissemination().
     * 
     * @param dataStreamId
     *            The id of the datastream.
     * @param pid
     *            The Fedora object id.
     * @param timestamp
     *            Timestamp related to datastream version to retrieve. May be null.
     * @return Returns the {@link MIMETypedStream} representing the addressed datastream.
     * @throws FedoraSystemException
     *             Thrown in case of Fedora exceptions.
     */
    public MIMETypedStream getDatastreamWithMimeType(final String dataStreamId, final String pid, final String timestamp)
        throws FedoraSystemException {

        FedoraAPIA apia = borrowApia();
        try {
            return apia.getDatastreamDissemination(pid, dataStreamId, timestamp);
        }
        catch (final RemoteException e) {
            // Workaround
            LOGGER.warn("APIA getDatastreamWithMimeType(..) " + e);
            invalidateApiaObject(apia);
            clearApimPool();
            apia = borrowApia();
            try {
                return apia.getDatastreamDissemination(pid, dataStreamId, timestamp);
            }
            catch (final RemoteException e1) {
                final String message =
                    "Error on retrieve datastream (pid='" + pid + "', dataStreamId='" + dataStreamId + "', timestamp='"
                        + timestamp + "') ";
                LOGGER.warn(message);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(message, e1);
                }
                throw new FedoraSystemException(message, e);
            }
        }
        finally {
            returnApia(apia);
        }
    }

    /**
     * The method modifies the named xml datastream of the Object with the given Pid. New Datastream-content is the
     * given byte[] datastream
     * 
     * @param pid
     *            id of the Object
     * @param datastreamName
     *            datastreamName
     * @param datastreamLabel
     *            datastreamLabel
     * @param datastream
     *            datastream
     * @param syncTripleStore
     *            whether the triples should be flushed
     * @return The timestamp of the modified datastream.
     * @throws FedoraSystemException
     *             Thrown if Fedora access failed.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public String modifyDatastream(
        final String pid, final String datastreamName, final String datastreamLabel, final byte[] datastream,
        final boolean syncTripleStore) throws FedoraSystemException, WebserverSystemException {

        String timestamp = null;
        final FedoraAPIM apim = borrowApim();
        try {
            timestamp =
                apim.modifyDatastreamByValue(pid, datastreamName, new String[0], datastreamLabel,
                    de.escidoc.core.common.business.fedora.datastream.Datastream.MIME_TYPE_TEXT_XML, null, datastream,
                    null, null, null, true);
        }
        catch (final Exception e) {
            preventWrongLogging(e);
            throw new FedoraSystemException(e.toString(), e);
        }
        finally {
            returnApim(apim);
            if (syncTripleStore) {
                sync();
            }
        }
        return timestamp;
    }

    /**
     * The method modifies the named datastream of the Object with the given Pid. New Datastream-content is the given
     * byte[] datastream
     * 
     * @param pid
     *            id of the Object
     * @param datastreamName
     *            datastreamName
     * @param datastreamLabel
     *            datastreamLabel
     * @param mimeType
     *            mimeType
     * @param altIDs
     *            Vector with alternate Ids.
     * @param url
     *            url
     * @param syncTripleStore
     *            whether the triples should be flushed
     * @return The timestamp of the modified datastream.
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public String modifyDatastream(
        final String pid, final String datastreamName, final String datastreamLabel, final String mimeType,
        final String[] altIDs, final String url, final boolean syncTripleStore) throws FedoraSystemException,
        WebserverSystemException {

        String timestamp = null;
        final FedoraAPIM apim = borrowApim();
        try {
            timestamp =
                apim.modifyDatastreamByReference(pid, datastreamName, altIDs, datastreamLabel, mimeType, null, url,
                    null, null, "Modified by reference.", true);
        }
        catch (final Exception e) {
            throw new FedoraSystemException("Failed to modify Fedora datastream by reference: " + url, e);
        }
        finally {
            returnApim(apim);
            if (syncTripleStore) {
                sync();
            }
        }
        return timestamp;
    }

    /**
     * The method modifies the named xml datastream of the Object with the given Pid. New Datastream-content is the
     * given byte[] datastream
     * 
     * @param pid
     *            id of the Object
     * @param datastreamName
     *            datastreamName
     * @param datastreamLabel
     *            datastreamLabel
     * @param mimeType
     *            The MIME Type of the datastream.
     * @param alternateIDs
     *            String array of alternateIDs of the datastream
     * @param datastream
     *            datastream
     * @param syncTripleStore
     *            whether the triples should be flushed
     * @return The new timestamp of the modified datastream.
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public String modifyDatastream(
        final String pid, final String datastreamName, final String datastreamLabel, final String mimeType,
        final String[] alternateIDs, final byte[] datastream, final boolean syncTripleStore)
        throws FedoraSystemException, WebserverSystemException {

        String timestamp = null;
        final FedoraAPIM apim = borrowApim();
        try {
            timestamp =
                apim.modifyDatastreamByValue(pid, datastreamName, alternateIDs, datastreamLabel, mimeType, null,
                    datastream, null, null, null, true);
        }
        catch (final Exception e) {
            throw new FedoraSystemException("Failed to modify Fedora datastream.", e);
        }
        finally {
            returnApim(apim);
            if (syncTripleStore) {
                sync();
            }
        }
        return timestamp;
    }

    /**
     * Purge all versions of datastreams between timestamps.
     * 
     * @param pid
     *            Fedora pid
     * @param datastreamName
     *            Fedora datastream name
     * @param startDT
     *            Start timestamp. Use null for newest.
     * @param endDT
     *            End timestamp. Use null for oldest.
     * @throws FedoraSystemException
     *             Thrown if purging failed by Fedora
     */
    public void purgeDatastream(final String pid, final String datastreamName, final String startDT, final String endDT)
        throws FedoraSystemException {

        final FedoraAPIM apim = borrowApim();
        try {
            apim.purgeDatastream(pid, datastreamName, startDT, endDT, "datastream purged", false);
        }
        catch (final Exception e) {
            throw new FedoraSystemException("Failed to purge Fedora datastream.", e);
        }
        finally {
            returnApim(apim);
        }
    }

    /**
     * The method modifies the named xml datastream of the Object with the given Pid. New Datastream-content is the
     * given byte[] datastream
     * 
     * @param pid
     *            id of the Object
     * @param datastreamName
     *            datastreamName
     * @param datastreamLabel
     *            datastreamLabel
     * @param alternateIDs
     *            String array of alternateIDs of the datastream
     * @param datastream
     *            datastream
     * @param syncTripleStore
     *            whether the triples should be flushed
     * @return The timestamp of the modified datastream.
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public String modifyDatastream(
        final String pid, final String datastreamName, final String datastreamLabel, final String[] alternateIDs,
        final byte[] datastream, final boolean syncTripleStore) throws FedoraSystemException, WebserverSystemException {

        return modifyDatastream(pid, datastreamName, datastreamLabel,
            de.escidoc.core.common.business.fedora.datastream.Datastream.MIME_TYPE_TEXT_XML, alternateIDs, datastream,
            syncTripleStore);
    }

    /**
     * Get the names of data streams selected by alternateId. Only the first value of the altIds is compared.
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
     * The method retrieves metadata for all datastreams of the fedora object with provided id as Array.
     * 
     * @param pid
     *            provided id
     * @return Fedora information set about all datastreams of the requested object.
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     */
    Datastream[] getDatastreamsInformation(final String pid) throws FedoraSystemException {
        return getDatastreamsInformation(pid, null);
    }

    /**
     * The method retrieves metadata for all datastreams of the fedora object with provided id as Array.
     * 
     * @param pid
     *            provided id
     * @param timestamp
     *            Timestamp related to datastream version to retrieve. May be null.
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
            clearApimPool();
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

    /**
     * The method retrieves metadata for the datastream with a provided name of the fedora object with provided id as
     * Array.
     * 
     * @param pid
     *            provided object id
     * @param name
     *            provided data stream name
     * @param timestamp
     *            Timestamp related to datastream version to retrieve. May be null.
     * @return datastream information
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     */
    public Datastream getDatastreamInformation(final String pid, final String name, final String timestamp)
        throws FedoraSystemException {

        final FedoraAPIM apim = borrowApim();
        try {
            return apim.getDatastream(pid, name, timestamp);
        }
        catch (final Exception e) {
            throw new FedoraSystemException(e.toString(), e);
        }
        finally {
            returnApim(apim);
        }
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
     * The method retrieves history of a datastream.
     * 
     * @param pid
     *            Fedora object id
     * @param dsID
     *            ID of the datastream
     * @return history of datastream.
     * @throws FedoraSystemException
     *             Thrown if Fedora request failed.
     */
    public Datastream[] getDatastreamHistory(final String pid, final String dsID) throws FedoraSystemException {
        Datastream[] datastreams = null;
        final FedoraAPIM apim = borrowApim();
        try {
            datastreams = apim.getDatastreamHistory(pid, dsID);
        }
        catch (final RemoteException e) {
            throw new FedoraSystemException(e.toString(), e);
        }
        finally {
            returnApim(apim);
        }
        return datastreams;
    }

    public String addDatastream(
        final String pid, final String name, final String[] altIDs, final String label, final boolean versionable,
        final byte[] stream, final String controlGroup, final boolean syncTripleStore) throws FedoraSystemException,
        WebserverSystemException {
        final String tempURI;
        try {
            tempURI =
                Utility.getInstance().upload(stream, pid + name,
                    de.escidoc.core.common.business.fedora.datastream.Datastream.MIME_TYPE_TEXT_XML);
        }
        catch (final FileSystemException e) {
            throw new WebserverSystemException("Error while uploading of content of datastream '" + name
                + "' of the fedora object with id '" + pid + "' to the staging area. ", e);
        }
        final String datastreamID =
            addDatastream(pid, name, altIDs, label, versionable,
                de.escidoc.core.common.business.fedora.datastream.Datastream.MIME_TYPE_TEXT_XML, null, tempURI,
                controlGroup, "A", "created");

        if (syncTripleStore) {
            sync();
        }
        return datastreamID;
    }

    /**
     * Add datastream to Fedora object.
     * 
     * @param pid
     *            Fedora object id.
     * @param name
     *            Datastream ID
     * @param altIDs
     *            Alt IDs
     * @param label
     *            Label
     * @param versionable
     *            Set true if Fedora has to keep old version. Set false if update overrides existing versions.
     * @param stream
     *            byte[] information dataset
     * @param syncTripleStore
     *            whether the triples should be flushed
     * @return Fedora Identifier of added Datastream.
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public String addDatastream(
        final String pid, final String name, final String[] altIDs, final String label, final boolean versionable,
        final byte[] stream, final boolean syncTripleStore) throws FedoraSystemException, WebserverSystemException {

        final String tempURI;
        try {
            tempURI =
                Utility.getInstance().upload(stream, pid + name,
                    de.escidoc.core.common.business.fedora.datastream.Datastream.MIME_TYPE_TEXT_XML);
        }
        catch (final FileSystemException e) {
            throw new WebserverSystemException("Error while uploading of content of datastream '" + name
                + "' of the fedora object with id '" + pid + "' to the staging area. ", e);
        }
        final String datastreamID =
            addDatastream(pid, name, altIDs, label, versionable,
                de.escidoc.core.common.business.fedora.datastream.Datastream.MIME_TYPE_TEXT_XML, null, tempURI, "X",
                "A", "created");

        if (syncTripleStore) {
            sync();
        }
        return datastreamID;
    }

    /**
     * Add datastream to Fedora object. Datastream is versionated by default.
     * 
     * @param pid
     *            Fedora object id.
     * @param name
     *            Datastream ID
     * @param altIDs
     *            Alt IDs
     * @param label
     *            Label
     * @param stream
     *            byte[] information dataset
     * @param syncTripleStore
     * @return Fedora Identifier of added Datastream.
     * @throws FedoraSystemException
     *             Thrown if add of datastream failed during Fedora communication.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    @Deprecated
    // use addDatastream method where versionable is set explicitly
    public String addDatastream(
        final String pid, final String name, final String[] altIDs, final String label, final byte[] stream,
        final boolean syncTripleStore) throws FedoraSystemException, WebserverSystemException {

        return addDatastream(pid, name, altIDs, label, true, stream, syncTripleStore);
    }

    /**
     * Add datastream to Fedora object.
     * 
     * @param pid
     *            Fedora object id.
     * @param name
     *            Datastream ID
     * @param altIDs
     *            Alt IDs
     * @param label
     *            Label
     * @param url
     *            URL of binary data.
     * @param mimeType
     *            MIME Type
     * @param controlGroup
     *            Defines the datastream storage type. (See Fedora ControlGroups)
     * @param syncTripleStore
     *            whether the triples should be flushed
     * @return Fedora Identifier of added Datastream.
     * @throws FedoraSystemException
     *             Thrown if adding datastream to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown if syncing TripleStore failed.
     */
    public String addDatastream(
        final String pid, final String name, final String[] altIDs, final String label, final String url,
        final String mimeType, final String controlGroup, final boolean syncTripleStore) throws FedoraSystemException,
        WebserverSystemException {

        final String datastreamID =
            addDatastream(pid, name, altIDs, label, true, mimeType, null, url, controlGroup, "A", "created");

        if (syncTripleStore) {
            sync();
        }
        return datastreamID;
    }

    /**
     * Add datastream to the object.
     * 
     * @param pid
     *            The Fedora object Id or PID.
     * @param dsID
     *            The id of the datastream.
     * @param altIDs
     *            The alternate IDs.
     * @param dsLabel
     *            The label of the datastream.
     * @param versionable
     *            Set true if datastream is verionable. False if not.
     * @param mimeType
     *            The MIME type of the datastream.
     * @param formatURI
     *            TODO
     * @param dsLocation
     *            TODO
     * @param controlGroup
     *            The Fedora Control Group Type.
     * @param dsState
     *            TODO
     * @param logMessage
     *            The Log Message.
     * @return Id of datastream.
     * @throws FedoraSystemException
     *             Thrown in case of Fedora exceptions.
     */
    private String addDatastream(
        final String pid, final String dsID, final String[] altIDs, final String dsLabel, final boolean versionable,
        final String mimeType, final String formatURI, final String dsLocation, final String controlGroup,
        final String dsState, final String logMessage) throws FedoraSystemException {

        final FedoraAPIM apim = borrowApim();
        try {
            return apim.addDatastream(pid, dsID, altIDs, dsLabel, versionable, mimeType, formatURI, dsLocation,
                controlGroup, dsState, null, null, logMessage);
        }
        catch (final Exception e) {
            throw new FedoraSystemException(e.toString(), e);
        }
        finally {
            returnApim(apim);
        }
    }

    /**
     * Touch the object in Fedora. The object is only modified to new version with 'touched' comment. No further update
     * is done. As last process is the TripleStore synced.
     * 
     * @param pid
     *            Fedora object id.
     * @param syncTripleStore
     *            Set true if the TripleStore is to sync after object modifiing. Set false otherwise.
     * @return modified timestamp of the Fedora Objects
     * @throws FedoraSystemException
     *             Thrown if modifiyObject in Fedora fails.
     * @throws WebserverSystemException
     *             Thrown if sync TripleStore failed.
     */
    public String touchObject(final String pid, final boolean syncTripleStore) throws FedoraSystemException,
        WebserverSystemException {

        String timestamp = null;
        final FedoraAPIM apim = borrowApim();
        try {
            timestamp = apim.modifyObject(pid, null, null, null, "touched");
        }
        catch (final RemoteException e) {
            throw new FedoraSystemException(e.toString(), e);
        }
        finally {
            returnApim(apim);
            if (syncTripleStore) {
                sync();
            }
        }
        return timestamp;
    }

    /**
     * Delete the Object with the given pid.
     * 
     * @param pid
     *            id of the Object
     * @param syncTripleStore
     *            whether the triples should be flushed
     * @throws FedoraSystemException
     *             Thrown if Fedora request failed.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public void deleteObject(final String pid, final boolean syncTripleStore) throws FedoraSystemException,
        WebserverSystemException {
        final String msg = "Deleted object " + pid + '.';

        final FedoraAPIM apim = borrowApim();
        try {
            apim.purgeObject(pid, msg, false);
        }
        catch (final RemoteException e) {
            throw new FedoraSystemException("While deleting: ", e);
        }
        finally {
            returnApim(apim);
            if (syncTripleStore) {
                sync();
            }
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
        /*
         * TODO The call to Fedora sync is handled multiple time to get a successful result. A single request should
         * help, but the return value of the sync method is not always HTTP.200.
         */
        int i = 0;
        while (i < SYNC_RETRIES) {
            try {
                callSync();
                break;
            }
            catch (final IOException e) {
                logExcetionAndWait(e, i);
            }
            catch (final TripleStoreSystemException e) {
                logExcetionAndWait(e, i);
            }
            i++;
            if (i >= SYNC_RETRIES) {
                throw new FedoraSystemException("TripleStore sync failed.");
            }
        }
    }

    private static void logExcetionAndWait(final Exception e, final int i) throws FedoraSystemException {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("Error syncing with TripleStore.");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Error syncing with TripleStore.", e);
        }
        try {
            Thread.sleep((long) (i + 1000));
        }
        catch (final InterruptedException e1) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on waiting for Fedora.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on waiting for Fedora.", e);
            }
        }
    }

    /**
     * Send a risearch request to fedora repository with flag flush set to true. Call reinialize() in order to reset a
     * Table Manager for the Triple Store.
     * 
     * @throws TripleStoreSystemException
     *             Thrown resetting of a Table Manager failed.
     * @throws FedoraSystemException
     *             Thrown if TripleStore synchronization failed.
     * @throws WebserverSystemException
     *             Thrown if TripleStore initialization failed.
     * @throws IOException
     *             Thrown from FedoraClient
     */
    private void callSync() throws FedoraSystemException, IOException, TripleStoreSystemException,
        WebserverSystemException {

        FedoraClient fc = null;
        try {
            fc = borrowFedoraClient();
            final HttpInputStream httpInStr = fc.get(this.syncRestQuery, true);
            if (httpInStr.getStatusCode() != HTTP_OK) {
                throw new FedoraSystemException("Triplestore sync failed.");
            }
            tripleStoreUtility.reinitialize();
        }
        finally {
            returnFedoraClient(fc);
        }
    }

    /**
     * Get the last-modification-date for the Fedora object.
     * 
     * @param pid
     *            Fedora objectId.
     * @return last-modification-date
     * @throws FedoraSystemException
     *             Thrown if connection to and retrieving data from Fedora fails.
     */
    public String getLastModificationDate(final String pid) throws FedoraSystemException {

        ObjectProfile op;
        FedoraAPIA apia = borrowApia();

        try {
            op = apia.getObjectProfile(pid, null);
        }
        catch (final RemoteException e) {
            // Workaround
            invalidateApiaObject(apia);
            apia = borrowApia();

            try {
                op = apia.getObjectProfile(pid, null);
            }
            catch (final RemoteException e1) {
                final String message = "Error on retrieve object profile (pid='" + pid + "') ";
                LOGGER.warn(message);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(message, e1);
                }
                throw new FedoraSystemException(message, e);
            }

            throw new FedoraSystemException(e);
        }
        finally {
            returnApia(apia);
        }

        return op.getObjLastModDate();
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
     * Converts an exception thrown during a pool operation to a {@link FedoraSystemException}.
     * 
     * @param e
     *            The {@link Exception} to be converted.
     * @return Returns the {@link FedoraSystemException}
     */
    private static FedoraSystemException convertPoolException(final Exception e) {
        return e instanceof FedoraSystemException ? (FedoraSystemException) e : new FedoraSystemException(e
            .getMessage(), e);
    }

    /**
     * See Interface for functional description.
     * 
     * @see InitializingBean #afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws IOException, MalformedURLException, ServiceException {

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
     * Returns a HttpClient object configured with credentials to access Fedora URLs.
     * 
     * @return A HttpClient object configured with credentials to access Fedora URLs.
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
     *            The Fedora local URL. Should start with the '/' after the webcontext path (usually "fedora"). E.g. if
     *            http://localhost:8080/fedora/get/... then localUrl is /get/...
     * @return MimeInputStream for content of the URL Request.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public MimeInputStream requestMimeFedoraURL(final String localUrl) throws WebserverSystemException {
        final MimeInputStream fedoraResponseStream = new MimeInputStream();
        try {
            final DefaultHttpClient httpClient = getHttpClient();
            final HttpContext localcontext = new BasicHttpContext();
            final BasicScheme basicAuth = new BasicScheme();
            localcontext.setAttribute("preemptive-auth", basicAuth);
            httpClient.addRequestInterceptor(new PreemptiveAuthInterceptor(), 0);
            final HttpGet httpGet = new HttpGet(this.fedoraUrl + localUrl);
            final HttpResponse httpResponse = httpClient.execute(httpGet);
            final int responseCode = httpResponse.getStatusLine().getStatusCode();

            if (httpResponse.getFirstHeader("Content-Type") != null) {
                fedoraResponseStream.setMimeType(httpResponse.getFirstHeader("Content-Type").getValue());
            }

            if (responseCode != HttpServletResponse.SC_OK) {
                EntityUtils.consume(httpResponse.getEntity());
                throw new WebserverSystemException("Bad response code '" + responseCode + "' requesting '"
                    + this.fedoraUrl + localUrl + "'.", new FedoraSystemException(httpResponse
                    .getStatusLine().getReasonPhrase()));
            }
            fedoraResponseStream.setInputStream(httpResponse.getEntity().getContent());

        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }

        return fedoraResponseStream;
    }

    /**
     * Makes a HTTP GET request to Fedora URL expanded by given local URL.
     * 
     * @param localUrl
     *            The Fedora local URL. Should start with the '/' after the webcontext path (usually "fedora"). E.g. if
     *            http://localhost:8080/fedora/get/... then localUrl is /get/...
     * @return the content of the URL Request.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public InputStream requestFedoraURL(final String localUrl) throws WebserverSystemException {
        try {
            return requestMimeFedoraURL(localUrl).getInputStream();
        }
        catch (Exception e) {
            throw new WebserverSystemException(e);
        }
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
     * Get FedoraUtility instance.
     * 
     * @return Instance of FedoraUtilitiy.
     * @throws FedoraSystemException
     *             Thrown if instantiation failed.
     */
    public static FedoraUtility getInstance() throws FedoraSystemException {

        try {
            return (FedoraUtility) BeanLocator.getBean(BeanLocator.COMMON_FACTORY_ID,
                "escidoc.core.business.FedoraUtility");
        }
        catch (final WebserverSystemException e) {
            throw new FedoraSystemException("FedoraUtility creation failed", e);
        }
    }

    /**
     * FIXME (SWA) crud, crud, crud!
     * <p/>
     * This should only prevent to write the FoXML into log if the resource URL is wrong or resource in in accessible.
     * If the exception is required as Fedora Exception or not, like in the case of Components, is decided in the upper
     * methods.
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
