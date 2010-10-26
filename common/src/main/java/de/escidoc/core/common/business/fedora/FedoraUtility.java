    /*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or http://www.escidoc.de/license.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.business.fedora;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.FileSystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import fedora.client.FedoraClient;
import fedora.client.HttpInputStream;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.Datastream;
import fedora.server.types.gen.MIMETypedStream;
import fedora.server.types.gen.ObjectProfile;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.pool.BasePoolableObjectFactory;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An utility class for Fedora requests.<br />
 * This class uses pools for the fedora interfaces.
 * 
 * @author ROF
 * @spring.bean id="escidoc.core.business.FedoraUtility"
 * @om
 * 
 */
@ManagedResource(objectName = "eSciDocCore:name=FedoraUtility", description = "The utility class to access the fedora repository.", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class FedoraUtility implements InitializingBean {

    public static final String DATASTREAM_STATUS_DELETED = "D";

    private static final AppLogger LOG =
        new AppLogger(FedoraUtility.class.getName());

    private static final int MAX_IDLE = 5;

    private static final int INIT_IDLE_CAPACITY = 2;

    private static final String FOXML_FORMAT =
        "info:fedora/fedora-system:FOXML-1.1";

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

    //  TODO
    //in configurationsdatei auslagern--> escidoc config*
    
    // escidoc-core.properties // default config 
    // escidoc-core.custom.properties --> f�hrend
    private static final int HTTP_MAX_CONNECTIONS_PER_HOST = 30;

    private static final int HTTP_MAX_TOTAL_CONNECTIONS = 90;

    private String fedoraUser;

    private String fedoraPassword;

    private String fedoraUrl;

    private String identifierPrefix;

    private ClientConnectionManager cm = null;

    private DefaultHttpClient httpClient;
     // The methods exposed via jmx

    /**
     * Gets the FoXML version.
     * 
     * @return The version of FOXML
     * @common
     */
    @ManagedAttribute(description = "The FoXML version.")
    public String getFoxmlVersion() {

        return FOXML_FORMAT;
    }

    /**
     * Gets the URL to the Fedora repository.
     * 
     * @return Returns the URL to the Fedora repository.
     * @common
     */
    @ManagedAttribute(description = "The URL to the fedora repository.")
    public String getFedoraUrl() {

        return fedoraUrl;
    }

    /**
     * Gets the number of active apia connections.
     * 
     * @return Returns the number of active apia connections.
     * @common
     */
    @ManagedAttribute(description = "The number of active apia connections.")
    public int getApiaPoolNumActive() {

        return apiaPool.getNumActive();
    }

    /**
     * Gets the number of idle apia connections.
     * 
     * @return Returns the number of idle apia connections.
     * @common
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
     * @common
     */
    @ManagedAttribute(description = "The number of active apim connections.")
    public int getApimPoolNumActive() {

        return apimPool.getNumActive();
    }

    /**
     * Gets the number of idle apim connections.
     * 
     * @return Returns the number of idle apim connections.
     * @common
     */
    @ManagedAttribute(description = "The number of idle apim connections.")
    public int getApimPoolNumIdle() {

        return apimPool.getNumIdle();
    }

    /**
     * Clears the pool of apim connections.
     */
    @ManagedOperation(description = "Clear the pool of apim connections.")
    public void clearApimPool() {

        apimPool.clear();
    }

    /**
     * Gets the number of active fedora clients.
     * 
     * @return Returns the number of active fedora clients.
     * @common
     */
    @ManagedAttribute(description = "The number of active fedora clients.")
    public int getFedoraClientPoolNumActive() {

        return fedoraClientPool.getNumActive();
    }

    /**
     * Gets the number of idle fedora clients.
     * 
     * @return Returns the number of idle fedora clients.
     * @common
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
     * The method returns the foxml of the Fedora object with provided id via
     * Fedora APIM-Webservice export().
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
        catch (RemoteException e) {
            LOG.warn("APIM export failure: " + e);
            throw new FedoraSystemException(e.getMessage(), e);
        }
        finally {
            returnApim(apim);
        }
    }

    /**
     * Get next object IDs from Fedora (Fedora use the name PID for there
     * identifier).
     * 
     * @param noOfPids
     *            Number of IDs which are to request.
     * @return Array with object IDs.
     * @throws FedoraSystemException
     *             Thrown if collection values from Fedora failed.
     */
    public String[] getNextPID(final int noOfPids) throws FedoraSystemException {

        String[] pids = null;
        final NonNegativeInteger number =
            new NonNegativeInteger(String.valueOf(noOfPids));

        final FedoraAPIM apim = borrowApim();
        try {
            pids = apim.getNextPID(number, this.identifierPrefix);
        }
        catch (RemoteException e) {
            LOG.warn("Unable to get Obids from Fedora: " + e);
            throw new FedoraSystemException(e.getMessage(), e);
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
     * 
     * @throws FedoraSystemException
     *             Thrown if set datastream at Fedora failed.
     */
    public String setDatastreamState(
        final String pid, final String dsName, final String dsState)
        throws FedoraSystemException {

        String timestamp = null;
        final FedoraAPIM apim = borrowApim();
        try {
            timestamp =
                apim.setDatastreamState(pid, dsName, dsState,
                    "ds state is changed.");
        }
        catch (RemoteException e) {
            LOG.debug("APIM setDatastreamState failure: " + e);
            throw new FedoraSystemException(e.getMessage(), e);
        }
        finally {
            returnApim(apim);
        }
        return timestamp;
    }

    /**
     * The method fetches the {@link MIMETypedStream} of the datastream with
     * provided data stream id from Fedora-object with provided pid via Fedora
     * APIA-Webservice getDatastreamDissemination().
     * 
     * @param dataStreamId
     *            The id of the datastream.
     * @param pid
     *            The Fedora object id.
     * @param timestamp
     *            Timestamp related to datastream version to retrieve. May be
     *            null.
     * @return Returns the {@link MIMETypedStream} representing the addressed
     *         datastream.
     * @throws FedoraSystemException
     *             Thrown in case of Fedora exceptions.
     */
    public MIMETypedStream getDatastreamWithMimeType(
        final String dataStreamId, final String pid, final String timestamp)
        throws FedoraSystemException {

        FedoraAPIA apia = borrowApia();
        try {
            return apia
                .getDatastreamDissemination(pid, dataStreamId, timestamp);
        }
        catch (RemoteException e) {
            // Workaround
            LOG.warn("APIA getDatastreamWithMimeType(..) " + e);
            invalidateApiaObject(apia);
            clearApimPool();
            apia = borrowApia();
            try {
                return apia.getDatastreamDissemination(pid, dataStreamId,
                    timestamp);
            }
            catch (RemoteException e1) {
                LOG.warn("Retrieve datastream (pid='" + pid
                    + "', dataStreamId='" + dataStreamId + "', timestamp='"
                    + timestamp + "') " + e);
                throw new FedoraSystemException(e.toString(), e);
            }
        }
        finally {
            returnApia(apia);
        }
    }

    /**
     * The method modifies the named xml datastream of the Object with the given
     * Pid. New Datastream-content is the given byte[] datastream
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
     * @om
     */
    public String modifyDatastream(
        final String pid, final String datastreamName,
        final String datastreamLabel, final byte[] datastream,
        final boolean syncTripleStore) throws FedoraSystemException,
        WebserverSystemException {

        String timestamp = null;
        final FedoraAPIM apim = borrowApim();
        try {
            timestamp =
                apim.modifyDatastreamByValue(pid, datastreamName,
                    new String[0], datastreamLabel, "text/xml", null,
                    datastream, null, null, null, true);
        }
        catch (Exception e) {
            preventWrongLogging(e, datastream);
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
     * The method modifies the named datastream of the Object with the given
     * Pid. New Datastream-content is the given byte[] datastream
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
     * 
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @om
     */
    public String modifyDatastream(
        final String pid, final String datastreamName,
        final String datastreamLabel, final String mimeType,
        final String[] altIDs, final String url, final boolean syncTripleStore)
        throws FedoraSystemException, WebserverSystemException {

        String timestamp = null;
        final FedoraAPIM apim = borrowApim();
        try {
            timestamp =
                apim.modifyDatastreamByReference(pid, datastreamName, altIDs,
                    datastreamLabel, mimeType, null, url, null, null,
                    "Modified by reference.", true);
        }
        catch (Exception e) {
            LOG.warn("Failed to modify Fedora datastream by reference: " + url);
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
     * The method modifies the named xml datastream of the Object with the given
     * Pid. New Datastream-content is the given byte[] datastream
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
     * 
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @om
     */
    public String modifyDatastream(
        final String pid, final String datastreamName,
        final String datastreamLabel, final String mimeType,
        final String[] alternateIDs, final byte[] datastream,
        final boolean syncTripleStore) throws FedoraSystemException,
        WebserverSystemException {

        String timestamp = null;
        final FedoraAPIM apim = borrowApim();
        try {
            timestamp =
                apim.modifyDatastreamByValue(pid, datastreamName, alternateIDs,
                    datastreamLabel, mimeType, null, datastream, null, null,
                    null, true);
        }
        catch (Exception e) {
            LOG.warn("Failed to modify Fedora datastream:\n"
                + "======== begin data stream ================\n"
                + new String(datastream) + "\n"
                + "======== end data stream ==================\n" + e);
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
    public void purgeDatastream(
        final String pid, final String datastreamName, final String startDT,
        final String endDT) throws FedoraSystemException {

        final FedoraAPIM apim = borrowApim();
        try {
            apim.purgeDatastream(pid, datastreamName, startDT, endDT,
                "datastream purged", false);
        }
        catch (Exception e) {
            if(LOG.isWarnEnabled()) {
                LOG.warn("Failed to purge Fedora datastream:\n======== begin data stream ================\n"
                + datastreamName + "\n======== end data stream ==================\n" + e);
            }
            throw new FedoraSystemException(e.toString(), e);
        }
        finally {
            returnApim(apim);
        }
    }

    /**
     * The method modifies the named xml datastream of the Object with the given
     * Pid. New Datastream-content is the given byte[] datastream
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
     * 
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public String modifyDatastream(
        final String pid, final String datastreamName,
        final String datastreamLabel, final String[] alternateIDs,
        final byte[] datastream, final boolean syncTripleStore)
        throws FedoraSystemException, WebserverSystemException {

        return modifyDatastream(pid, datastreamName, datastreamLabel,
            "text/xml", alternateIDs, datastream, syncTripleStore);
    }

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
    public Vector<String> getDatastreamNamesByAltId(
        final String pid, final String altId) throws FedoraSystemException {
        final Vector<String> names = new Vector<String>();

        final fedora.server.types.gen.Datastream[] ds =
            getDatastreamsInformation(pid);

        for (int i = 0; i < ds.length; i++) {
            final String[] altIDs = ds[i].getAltIDs();
            if (altIDs.length > 0 && altIDs[0].equals(altId)) {
                names.add(ds[i].getID());
            }
        }
        return (names);
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
    public String storeObjectInFedora(
        final String foxml, final boolean syncTripleStore)
        throws FedoraSystemException, WebserverSystemException {

        String pid;

        FedoraAPIM apim = borrowApim();
        try {
            pid =
                apim.ingest(foxml.getBytes(XmlUtility.CHARACTER_ENCODING),
                    FOXML_FORMAT, "eSciDoc object created");
        }
        catch (Exception e) {

            // Workaround - try a secound connection
            if (e.getMessage().contains("Address already in use: connect")) {
                LOG.warn("APIM storeObjectInFedora(..) " + e);
                invalidateApimObject(apim);
                apim = borrowApim();

                try {
                    pid =
                        apim.ingest(foxml
                            .getBytes(XmlUtility.CHARACTER_ENCODING),
                            FOXML_FORMAT, "eSciDoc object created");
                }
                catch (Exception e1) {
                    preventWrongLogging(e, foxml);
                    throw new FedoraSystemException(
                        "Ingest to Fedora failed. ", e);
                }
            }

            preventWrongLogging(e, foxml);
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
    public Datastream[] getDatastreamsInformation(final String pid)
        throws FedoraSystemException {
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
    public Datastream[] getDatastreamsInformation(
        final String pid, final String timestamp) throws FedoraSystemException {

        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.concatenateWithBracketsToString(
                "getDatastreamsInformation ", pid, timestamp));
        }

        Datastream[] datastreams = null;
        FedoraAPIM apim = borrowApim();
        try {
            // work around to prevent null returns
            datastreams = apim.getDatastreams(pid, timestamp, null);
            if (datastreams == null) {
                LOG.warn("APIM getDatastreams(" + pid + ", ..) returns null.");
                returnApim(apim);
                apim = borrowApim();
                datastreams = apim.getDatastreams(pid, timestamp, null);
            }
        }
        catch (RemoteException e) {
            // Workaround
            LOG.info("APIM getDatastreams(..) " + e);
            invalidateApimObject(apim);
            clearApimPool();
            apim = borrowApim();
            try {
                datastreams = apim.getDatastreams(pid, timestamp, null);
            }
            catch (RemoteException e1) {
                throw new FedoraSystemException(e.toString(), e);
            }
        }
        finally {
            returnApim(apim);
        }
        return datastreams;
    }

    /**
     * The method retrieves metadata for the datastream with a provided name of
     * the fedora object with provided id as Array.
     * 
     * @param pid
     *            provided object id
     * @param name
     *            provided data stream name
     * @param timestamp
     *            Timestamp related to datastream version to retrieve. May be
     *            null.
     * @return datastream information
     * 
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     */
    public Datastream getDatastreamInformation(
        final String pid, final String name, final String timestamp)
        throws FedoraSystemException {

        final FedoraAPIM apim = borrowApim();
        try {
            return apim.getDatastream(pid, name, timestamp);
        }
        catch (Exception e) {
            throw new FedoraSystemException(e.toString(), e);
        }
        finally {
            returnApim(apim);
        }
    }

    public byte[] getDissemination(
        final String pid, final String contentModelPid, final String name)
        throws FedoraSystemException {

        // TODO check if APIA.listMethods is sufficient for retrieving dynamic
        // resource names.
        final FedoraAPIA apia = borrowApia();
        try {
            return apia.getDissemination(
                pid,
                "sdef:"
                    + contentModelPid.replace(":",
                        Constants.COLON_REPLACEMENT_PID) + "-" + name, name,
                null, null).getStream();
        }
        catch (Exception e) {
            throw new FedoraSystemException(
                "Failed to get result of dynamic resource.", e);
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
    public Datastream[] getDatastreamHistory(final String pid, final String dsID)
        throws FedoraSystemException {

        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtility.concatenateWithBracketsToString(
                "getDatastreamHistory ", pid, dsID));
        }

        Datastream[] datastreams = null;
        FedoraAPIM apim = borrowApim();
        try {
            datastreams = apim.getDatastreamHistory(pid, dsID);
        }
        catch (RemoteException e) {
            throw new FedoraSystemException(e.toString(), e);
        }
        finally {
            returnApim(apim);
        }
        return datastreams;
    }

    public String addDatastream(
        final String pid, final String name, final String[] altIDs,
        final String label, final boolean versionable, final byte[] stream,
        final String controlGroup, final boolean syncTripleStore)
        throws FedoraSystemException, WebserverSystemException {
        String tempURI = null;
        try {
            tempURI =
                Utility.getInstance().upload(stream, pid + name, "text/xml");
        }
        catch (FileSystemException e) {
            final String message =
                "Error while uploading of content of datastream '" + name
                    + "' of the fedora object with id '" + pid
                    + "' to the staging area. ";
            LOG.error(message + e.getMessage());
            throw new WebserverSystemException(message, e);
        }
        final String datastreamID =
            addDatastream(pid, name, altIDs, label, versionable, "text/xml",
                null, tempURI, controlGroup, "A", "created");

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
     *            Set true if Fedora has to keep old version. Set false if
     *            update overrides existing versions.
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
        final String pid, final String name, final String[] altIDs,
        final String label, final boolean versionable, final byte[] stream,
        final boolean syncTripleStore) throws FedoraSystemException,
        WebserverSystemException {

        String tempURI = null;
        try {
            tempURI =
                Utility.getInstance().upload(stream, pid + name, "text/xml");
        }
        catch (FileSystemException e) {
            final String message =
                "Error while uploading of content of datastream '" + name
                    + "' of the fedora object with id '" + pid
                    + "' to the staging area. ";
            LOG.error(message + e.getMessage());
            throw new WebserverSystemException(message, e);
        }
        final String datastreamID =
            addDatastream(pid, name, altIDs, label, versionable, "text/xml",
                null, tempURI, "X", "A", "created");

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
     *            whether the triples should be flushed
     * @return Fedora Identifier of added Datastream.
     * @throws FedoraSystemException
     *             Thrown if add of datastream failed during Fedora
     *             communication.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    @Deprecated
    // use addDatastream method where versionable is set explicitly
    public String addDatastream(
        final String pid, final String name, final String[] altIDs,
        final String label, final byte[] stream, final boolean syncTripleStore)
        throws FedoraSystemException, WebserverSystemException {

        return addDatastream(pid, name, altIDs, label, true, stream,
            syncTripleStore);
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
     *            Defines the datastream storage type. (See Fedora
     *            ControlGroups)
     * @param syncTripleStore
     *            whether the triples should be flushed
     * @return Fedora Identifier of added Datastream.
     * 
     * @throws FedoraSystemException
     *             Thrown if adding datastream to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown if syncing TripleStore failed.
     */
    public String addDatastream(
        final String pid, final String name, final String[] altIDs,
        final String label, final String url, final String mimeType,
        final String controlGroup, final boolean syncTripleStore)
        throws FedoraSystemException, WebserverSystemException {

        final String datastreamID =
            addDatastream(pid, name, altIDs, label, true, mimeType, null, url,
                controlGroup, "A", "created");

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
        final String pid, final String dsID, final String[] altIDs,
        final String dsLabel, final boolean versionable, final String mimeType,
        final String formatURI, final String dsLocation,
        final String controlGroup, final String dsState, final String logMessage)
        throws FedoraSystemException {

        final FedoraAPIM apim = borrowApim();
        try {
            return apim.addDatastream(pid, dsID, altIDs, dsLabel, versionable,
                mimeType, formatURI, dsLocation, controlGroup, dsState, null,
                null, logMessage);
        }
        catch (Exception e) {
            throw new FedoraSystemException(e.toString(), e);
        }
        finally {
            returnApim(apim);
        }
    }

    /**
     * Touch the object in Fedora. The object is only modified to new version
     * with 'touched' comment. No further update is done. As last process is the
     * TripleStore synced.
     * 
     * @param pid
     *            Fedora object id.
     * @param syncTripleStore
     *            Set true if the TripleStore is to sync after object modifiing.
     *            Set false otherwise.
     * @return modified timestamp of the Fedora Objects
     * @throws FedoraSystemException
     *             Thrown if modifiyObject in Fedora fails.
     * @throws WebserverSystemException
     *             Thrown if sync TripleStore failed.
     */
    public String touchObject(final String pid, final boolean syncTripleStore)
        throws FedoraSystemException, WebserverSystemException {

        String timestamp = null;
        final FedoraAPIM apim = borrowApim();
        try {
            timestamp = apim.modifyObject(pid, null, null, null, "touched");
        }
        catch (RemoteException e) {
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
     * @om
     */
    public void deleteObject(final String pid, final boolean syncTripleStore)
        throws FedoraSystemException, WebserverSystemException {
        final String msg = "Deleted object " + pid + ".";

        final FedoraAPIM apim = borrowApim();
        try {
            apim.purgeObject(pid, msg, false);
        }
        catch (RemoteException e) {
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
     * Call the method callSync. In case of failure make 4 attempts more.
     * 
     * @throws FedoraSystemException
     *             Thrown if TripleStore synchronization failed.
     * @throws WebserverSystemException
     *             Thrown if TripleStore initialization failed.
     */
    public void sync() throws FedoraSystemException, WebserverSystemException {
        /*
         * TODO The call to Fedora sync is handled multiple time to get a
         * successful result. A single request should help, but the return value
         * of the sync method is not always HTTP.200.
         */
        int i = 0;
        while (i < 5) {
            try {
                callSync();
                break;

            }
            catch (IOException e) {
                LOG.error(e);
                if (i == 4) {
                    throw new FedoraSystemException(e);
                }
            }
            catch (TripleStoreSystemException e) {
                LOG.error(e);
                if (i == 4) {
                    throw new FedoraSystemException("Triplestore sync failed.",
                        e);
                }
            }

        }
    }

    /**
     * Send a risearch request to fedora repository with flag flush set to true.
     * Call reinialize() in order to reset a Table Manager for the Triple Store.
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
    private void callSync() throws FedoraSystemException, IOException,
        TripleStoreSystemException, WebserverSystemException {
        FedoraClient fc = null;
        try {
            fc = borrowFedoraClient();
            fedora.client.HttpInputStream httpInStr =
                fc.get(syncRestQuery, true);
            if (httpInStr.getStatusCode() != HTTP_OK) {
                throw new FedoraSystemException("Triplestore sync failed.");
            }
            TripleStoreUtility.getInstance().reinitialize();
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
     *             Thrown if connection to and retrieving data from Fedora
     *             fails.
     */
    public String getLastModificationDate(final String pid)
        throws FedoraSystemException {

        ObjectProfile op;
        FedoraAPIA apia = borrowApia();

        try {
            op = apia.getObjectProfile(pid, null);
        }
        catch (RemoteException e) {
            // Workaround
            LOG.warn("APIA getLastModificationDate(..) " + e);
            invalidateApiaObject(apia);
            apia = borrowApia();

            try {
                op = apia.getObjectProfile(pid, null);
            }
            catch (RemoteException e1) {
                throw new FedoraSystemException(e.toString(), e);
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
     * 
     * @return response from Fedora as input stream
     * @throws FedoraSystemException
     *             Thrown in case of an internal error.
     * @throws IOException
     *             Thrown if the GET request to Fedora failed.
     */
    public HttpInputStream query(final String queryString)
        throws FedoraSystemException, IOException {
        HttpInputStream result = null;
        FedoraClient fc = null;

        try {
            fc = borrowFedoraClient();
            result = fc.get(queryString, true);
            if (result.getStatusCode() != HTTP_OK) {
                throw new FedoraSystemException(
                    "GET request to Fedora failed with error "
                        + result.getStatusCode());
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
    private synchronized FedoraClient borrowFedoraClient()
        throws FedoraSystemException {
        try {
            return (FedoraClient) fedoraClientPool.borrowObject();
        }
        catch (Exception e) {
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
    private synchronized void returnFedoraClient(final FedoraClient fedoraClient)
        throws FedoraSystemException {
        try {
            fedoraClientPool.returnObject(fedoraClient);
        }
        catch (Exception e) {
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
    private synchronized FedoraAPIA borrowApia() throws FedoraSystemException {

        try {
            return (FedoraAPIA) apiaPool.borrowObject();
        }
        catch (Exception e) {
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
    private synchronized void returnApia(final FedoraAPIA fedoraApia)
        throws FedoraSystemException {
        try {
            apiaPool.returnObject(fedoraApia);
        }
        catch (Exception e) {
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
    private synchronized FedoraAPIM borrowApim() throws FedoraSystemException {

        try {
            return (FedoraAPIM) apimPool.borrowObject();
        }
        catch (Exception e) {
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
    private synchronized void returnApim(final FedoraAPIM fedoraApim)
        throws FedoraSystemException {
        try {
            apimPool.returnObject(fedoraApim);
        }
        catch (Exception e) {
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
    private synchronized void invalidateApiaObject(final FedoraAPIA fedoraApia)
        throws FedoraSystemException {

        try {
            apiaPool.invalidateObject(fedoraApia);
        }
        catch (Exception e) {
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
    private synchronized void invalidateApimObject(final FedoraAPIM fedoraApim)
        throws FedoraSystemException {
        try {
            apimPool.invalidateObject(fedoraApim);
        }
        catch (Exception e) {
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
    private FedoraSystemException convertPoolException(final Exception e) {

        try {
            return ((FedoraSystemException) e);
        }
        catch (ClassCastException classCastException) {
            return new FedoraSystemException(e.getMessage(), e);
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @throws Exception
     * @see org.springframework.beans.factory.InitializingBean
     *      #afterPropertiesSet()
     * @common
     */
    public void afterPropertiesSet() throws Exception {

        fedoraClientPool = new StackObjectPool(new BasePoolableObjectFactory() {
            /**
             * See Interface for functional description.
             * 
             * @return
             * @throws Exception
             * @see org.apache.commons.pool.BasePoolableObjectFactory
             *      #makeObject()
             */
            @Override
            public synchronized Object makeObject() throws Exception {
                return new FedoraClient(fedoraUrl, fedoraUser, fedoraPassword);
            }
        }, MAX_IDLE, INIT_IDLE_CAPACITY);

        apiaPool = new StackObjectPool(new BasePoolableObjectFactory() {
            /**
             * See Interface for functional description.
             * 
             * @return
             * @throws Exception
             * @see org.apache.commons.pool.BasePoolableObjectFactory
             *      #makeObject()
             */
            @Override
            public synchronized Object makeObject() throws Exception {
                return new FedoraClient(fedoraUrl, fedoraUser, fedoraPassword)
                    .getAPIA();
            }
        }, MAX_IDLE, INIT_IDLE_CAPACITY);

        apimPool = new StackObjectPool(new BasePoolableObjectFactory() {
            /**
             * See Interface for functional description.
             * 
             * @return
             * @throws Exception
             * @see org.apache.commons.pool.BasePoolableObjectFactory
             *      #makeObject()
             */
            @Override
            public synchronized Object makeObject() throws Exception {
                return new FedoraClient(fedoraUrl, fedoraUser, fedoraPassword)
                    .getAPIM();
            }
        }, MAX_IDLE, INIT_IDLE_CAPACITY);

        syncRestQuery = fedoraUrl + "/risearch?flush=true";
    }

    /**
     * Returns a HttpClient object configured with credentials to access Fedora
     * URLs.
     * 
     * @return A HttpClient object configured with credentials to access Fedora
     *         URLs.
     * @throws WebserverSystemException
     */
    public HttpClient getHttpClient() throws WebserverSystemException {
        try {
            if(httpClient==null)
            {    
                HttpParams params = new BasicHttpParams();
                ConnManagerParams.setMaxTotalConnections(params,
                    HTTP_MAX_TOTAL_CONNECTIONS);
    
                ConnPerRouteBean connPerRoute =
                    new ConnPerRouteBean(HTTP_MAX_CONNECTIONS_PER_HOST);
                ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
    
                Scheme http =  new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
                SchemeRegistry sr = new SchemeRegistry();
                sr.register(http);
                 cm = new ThreadSafeClientConnManager(params, sr);
    
                this.httpClient = new DefaultHttpClient(this.cm, params);   
                URL url = new URL(fedoraUrl);
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                
                AuthScope authScope =
                    new AuthScope(url.getHost(), AuthScope.ANY_PORT,
                        AuthScope.ANY_REALM);
                UsernamePasswordCredentials creds =
                    new UsernamePasswordCredentials(fedoraUser, fedoraPassword);
                credsProvider.setCredentials(authScope, creds);

                httpClient.setCredentialsProvider(credsProvider);
            } 
                       
            // don't wait for auth request
            HttpRequestInterceptor preemptiveAuth = new HttpRequestInterceptor() {
                
                public void process(
                        final HttpRequest request, 
                        final HttpContext context) throws HttpException, IOException {
                    
                    AuthState authState = (AuthState) context.getAttribute(
                            ClientContext.TARGET_AUTH_STATE);
                    CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                            ClientContext.CREDS_PROVIDER);
                    HttpHost targetHost = (HttpHost) context.getAttribute(
                            ExecutionContext.HTTP_TARGET_HOST);
                    
                    // If not auth scheme has been initialized yet
                    if (authState.getAuthScheme() == null) {
                        AuthScope authScope = new AuthScope(
                                targetHost.getHostName(), 
                                targetHost.getPort());
                        // Obtain credentials matching the target host
                        Credentials creds = credsProvider.getCredentials(authScope);
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
          
            return httpClient;
        }
        catch (MalformedURLException e) {
            throw new WebserverSystemException(
                "Fedora URL from configuration malformed.", e);
        }
    }

    /**
     * Makes a HTTP GET request to Fedora URL expanded by given local URL.
     * 
     * @param localUrl
     *            The Fedora local URL. Should start with the '/' after the
     *            webcontext path (usually "fedora"). E.g. if
     *            http://localhost:8080/fedora/get/... then localUrl is /get/...
     * 
     * @return the content of the URL Request.
     * 
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public InputStream requestFedoraURL(final String localUrl)
        throws WebserverSystemException {
        HttpGet httpGet = null;
        HttpResponse httpResponse=null;
        InputStream fedoraResponseStream =null;
        try {
           httpGet = new HttpGet(fedoraUrl + localUrl);
           httpResponse = getHttpClient().execute(httpGet);
           int responseCode = httpResponse.getStatusLine().getStatusCode();
           if (responseCode != HttpServletResponse.SC_OK) {
         
                           throw new WebserverSystemException("Bad response code '"
                    + responseCode + "' requesting '" + fedoraUrl + localUrl
                    + "'.", new FedoraSystemException(httpResponse.getStatusLine().getReasonPhrase()));
            }
           fedoraResponseStream = httpResponse.getEntity().getContent();
           
        }
        catch (IOException e) {
            throw new WebserverSystemException(e);
        }

        return fedoraResponseStream;
    }

    /**
     * @param fedoraUrl
     *            the fedoraUrl to inject
     * @spring.property value="${fedora.url}"
     * @common
     */
    public void setFedoraUrl(final String fedoraUrl) {

        if (this.fedoraUrl != null) {
            throw new NotWritablePropertyException(this.getClass(),
                "fedoraUrl", "Property must not be set twice.");
        }
        this.fedoraUrl = fedoraUrl;
    }

    /**
     * @param fedoraUser
     *            the fedoraUser to inject
     * @spring.property value="${fedora.user}"
     * @common
     */
    public void setFedoraUser(final String fedoraUser) {

        if (this.fedoraUser != null) {
            throw new NotWritablePropertyException(this.getClass(),
                "fedoraUser", "Property must not be set twice.");
        }
        this.fedoraUser = fedoraUser;
    }

    /**
     * @param fedoraPassword
     *            the fedoraPassword to inject
     * @spring.property value="${fedora.password}"
     * @common
     */
    public void setFedoraPassword(final String fedoraPassword) {

        if (this.fedoraPassword != null) {
            throw new NotWritablePropertyException(this.getClass(),
                "fedoraPassword", "Property must not be set twice.");
        }
        this.fedoraPassword = fedoraPassword;
    }

    /**
     * @param identifierPrefix
     *            the identifierPrefix to inject
     * @spring.property value="${escidoc-core.identifier.prefix}"
     * @common
     */
    public void setIdentifierPrefix(final String identifierPrefix) {

        this.identifierPrefix = identifierPrefix;
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
            return (FedoraUtility) BeanLocator.getBean(
                BeanLocator.COMMON_FACTORY_ID,
                "escidoc.core.business.FedoraUtility");
        }
        catch (WebserverSystemException e) {
            throw new FedoraSystemException("FedoraUtility creation failed", e);
        }
    }

    /**
     * FIXME (SWA) crud, crud, crud!
     * 
     * This should only prevent to write the FoXML into log if the resource URL
     * is wrong or resource in in accessible. If the exception is required as
     * Fedora Exception or not, like in the case of Components, is decided in
     * the upper methods.
     * 
     * @param e
     *            Exception
     * @param datastream
     *            datastream (to write it to logfile)
     */
    private void preventWrongLogging(final Exception e, final byte[] datastream) {

        final Pattern PATTERN_ERROR_GETTING =
            Pattern.compile(
                "fedora.server.errors.GeneralException: Error getting",
                Pattern.CASE_INSENSITIVE);
        final Pattern PATTERN_MALFORMED_URL =
            Pattern.compile("fedora.server.errors.ObjectIntegrityException: "
                + "FOXML IO stream was bad : Malformed URL");

        Matcher matcherErrorGetting =
            PATTERN_ERROR_GETTING.matcher(e.getMessage());
        Matcher matcherMalformedUrl =
            PATTERN_MALFORMED_URL.matcher(e.getMessage());

        if (!(matcherErrorGetting.find() || matcherMalformedUrl.find())) {
            LOG.debug("Failed to modify Fedora datastream. " + e.toString());
            // LOG.warn("Failed to modify Fedora datastream:\n"
            // + "======== begin data stream ================\n"
            // + new String(datastream) + "\n"
            // + "======== end data stream ==================\n" + e);
        }
        else {
            LOG.debug("Failed to load content. " + e.toString());
        }

    }

    /**
     * FIXME crud stuff.
     * 
     * @param e
     *            Exception
     * @param datastream
     *            datastream (to write it to logfile)
     */
    private void preventWrongLogging(final Exception e, final String datastream) {

        try {
            preventWrongLogging(e, datastream
                .getBytes(XmlUtility.CHARACTER_ENCODING));
        }
        catch (UnsupportedEncodingException e1) {
            // nothing to do ? (FRS)
            // SWA: This is derived from crud code. The main failure is the
            // approach to write something to Fedora and if an Exception failed,
            // than try an update instead of create. If the main failure is
            // removed from code, than is this method redundant.
        }
    }
 

}
