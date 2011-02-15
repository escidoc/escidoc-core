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
package de.escidoc.core.common.business.indexing;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.resources.listener.ResourceListener;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.index.IndexRequest;
import de.escidoc.core.index.IndexRequestBuilder;
import de.escidoc.core.index.IndexService;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handler for synchronous indexing via gsearch.
 * 
 * @spring.bean id="common.business.indexing.IndexingHandler" lazy-init="true"
 * 
 * @author MIH
 * 
 */
public class IndexingHandler implements ResourceListener {

    private static AppLogger log = new AppLogger(
        IndexingHandler.class.getName());

    private GsearchHandler gsearchHandler = null;

    private IndexingCacheHandler indexingCacheHandler;

    private DocumentBuilder docBuilder;

    private IndexService indexService;

    private TripleStoreUtility tripleStoreUtility;

    private boolean notifyIndexerEnabled = true;

    private Collection<String> indexNames = null;

    private Map<String, Map<String, Map<String, Object>>> objectTypeParameters =
        null;

    /**
     * Constructor.
     * 
     * @throws SystemException
     *             e
     */
    public IndexingHandler() throws SystemException {
        DocumentBuilderFactory docBuilderFactory =
            DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new SystemException(e.getMessage());
        }
        try {
            notifyIndexerEnabled =
                EscidocConfiguration.getInstance().getAsBoolean(
                    EscidocConfiguration.ESCIDOC_CORE_NOTIFY_INDEXER_ENABLED);
        }
        catch (IOException e) {
            throw new SystemException(e.getMessage());
        }
    }

    // begin implementation of ResourceListener

    /**
     * Resource was created, so write indexes.
     * 
     * @param id
     *            resource id
     * @param restXml
     *            complete resource as REST XML
     * @param soapXml
     *            complete resource as SOAP XML
     * 
     * @throws SystemException
     *             The resource could not be stored.
     */
    public void resourceCreated(
        final String id, final String restXml, final String soapXml)
        throws SystemException {
        if (!notifyIndexerEnabled) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("gsearchindexing STARTING, xml is " + restXml);
        }
        long time = System.currentTimeMillis();
        if (restXml != null && restXml.length() > 0) {
            if (log.isDebugEnabled()) {
                log.debug("writing xml in cache");
            }
            indexingCacheHandler.writeObjectInCache(id, restXml);
            if (log.isDebugEnabled()) {
                log.debug("gsearchindexing caching xml via deviation handler "
                    + " needed " + (System.currentTimeMillis() - time) + " ms");
            }
        }
        String objectType = tripleStoreUtility.getObjectType(id);
        addResource(id, objectType, restXml);
        if (log.isDebugEnabled()) {
            log.debug("gsearchindexing whole indexing of resource " + id
                + " of type " + objectType + " needed "
                + (System.currentTimeMillis() - time) + " ms");
        }
    }

    /**
     * Delete a resource from the indexes.
     * 
     * @param id
     *            resource id
     * 
     * @throws SystemException
     *             The resource could not be deleted.
     */
    public void resourceDeleted(final String id) throws SystemException {
        if (!notifyIndexerEnabled) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("gsearchindexing STARTING deletion");
        }
        long time = System.currentTimeMillis();
        deleteResource(id);
        if (log.isDebugEnabled()) {
            log.debug("gsearchindexing whole deletion of resource " + id
                + " needed " + (System.currentTimeMillis() - time) + " ms");
        }
    }

    /**
     * Replace a resource in the indexes.
     * 
     * @param id
     *            resource id
     * @param restXml
     *            complete resource as REST XML
     * @param soapXml
     *            complete resource as SOAP XML
     * 
     * @throws SystemException
     *             The resource could not be deleted and newly created.
     */
    public void resourceModified(
        final String id, final String restXml, final String soapXml)
        throws SystemException {
        if (!notifyIndexerEnabled) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("gsearchindexing STARTING, xml is " + restXml);
        }
        long time = System.currentTimeMillis();
        if (restXml != null && restXml.length() > 0) {
            if (log.isDebugEnabled()) {
                log.debug("replacing xml in cache");
            }
            indexingCacheHandler.replaceObjectInCache(id, restXml);
            if (log.isDebugEnabled()) {
                log.debug("gsearchindexing caching xml via deviation handler "
                    + " needed " + (System.currentTimeMillis() - time) + " ms");
            }
        }
        String objectType = tripleStoreUtility.getObjectType(id);
        addResource(id, objectType, restXml);
        if (log.isDebugEnabled()) {
            log.debug("gsearchindexing whole indexing of resource " + id
                + " of type " + objectType + " needed "
                + (System.currentTimeMillis() - time) + " ms");
        }
    }

    // end implementation of ResourceListener

    /**
     * Add resource to index.
     * 
     * @param resource
     *            href of the resource to index.
     * @param objectType
     *            type of object to index.
     * @param xml
     *            xml of the resource to index.
     * @throws SystemException
     *             e
     */
    private void addResource(
        final String resource, final String objectType, final String xml)
        throws SystemException {
        indexResource(
            resource,
            objectType,
            de.escidoc.core.common.business.Constants.INDEXER_QUEUE_ACTION_PARAMETER_UPDATE_VALUE,
            xml);
    }

    /**
     * delete resource from index.
     * 
     * @param resource
     *            href of the resource to index.
     * @throws SystemException
     *             e
     */
    private void deleteResource(final String resource) throws SystemException {
        doIndexing(
            resource,
            null,
            de.escidoc.core.common.business.Constants.INDEXER_QUEUE_ACTION_PARAMETER_DELETE_VALUE,
            false, null);
    }

    /**
     * Check if indexing has to be done synchronously or asynchronously. If
     * synchronously, immediately index. If asynchronously, write in
     * message-queue. If both, do both (resource can be indexed in more than one
     * index).
     * 
     * @param resource
     *            href of the resource to index.
     * @param objectType
     *            type of object to index.
     * @param action
     *            indexing-action (update or delete).
     * @param xml
     *            object-representation in xml.
     * @throws SystemException
     *             e
     */
    private void indexResource(
        final String resource, final String objectType, final String action,
        final String xml) throws SystemException {

        long time = System.currentTimeMillis();

        if (log.isDebugEnabled()) {
            log.debug("Do indexing for resource " + resource + ", objectType: "
                + objectType);
        }

        // check if there exist indexing-parameters for given resource.
        // If not, do nothing.
        if (getObjectTypeParameters().get(objectType) != null) {
            boolean indexAsynch = false;
            boolean indexSynch = false;
            for (Map<String, Object> indexParameters : getObjectTypeParameters()
                .get(objectType).values()) {
                if (indexParameters.get("indexAsynchronous") != null
                    && Boolean.valueOf((String) indexParameters
                        .get("indexAsynchronous"))) {
                    indexAsynch = true;
                }
                else {
                    indexSynch = true;
                }
            }
            if (indexSynch) {
                if (log.isDebugEnabled()) {
                    log.debug("indexing synchronously");
                }
                doIndexing(resource, objectType, action, false, xml);
            }
            if (indexAsynch) {
                if (log.isDebugEnabled()) {
                    log.debug("indexing asynchronously");
                }
                IndexRequest indexRequest =
                    IndexRequestBuilder
                        .createIndexRequest().withResource(resource)
                        .withObjectType(objectType).withAction(action)
                        .withData(xml).withIsReindexerCaller(false).build();
                this.indexService.index(indexRequest);
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("gsearchindexing resource " + resource + " of type "
                + objectType + ", action=" + action + " needed "
                + (System.currentTimeMillis() - time) + " ms");
        }
    }

    /**
     * Check indexing-action (update, delete or create-empty). If update, check
     * indexing-configuration for resource and do update for specified indexes.
     * 
     * @param resource
     *            String resource.
     * @param objectType
     *            String name of the resource (eg Item, Container...).
     * @param action
     *            String indexing-action (update, delete, create-empty).
     * @param isAsynch
     *            boolean called asynch.
     * 
     * @param xml
     *            object-representation in xml.
     * @throws SystemException
     *             e
     */
    public void doIndexing(
        final String resource, final String objectType, final String action,
        final boolean isAsynch, final String xml) throws SystemException {
        if (log.isDebugEnabled()) {
            log.debug("calling do Indexing with resource: " + resource
                + ", objectType: " + objectType + ", action: " + action
                + ", isAsynch: " + isAsynch + ", xml: " + xml);
        }
        if (action == null
            || action
                .equalsIgnoreCase(de.escidoc.core.common.business.Constants.INDEXER_QUEUE_ACTION_PARAMETER_UPDATE_VALUE)) {

            // get Index-Parameters for resourceName
            Map<String, Map<String, Object>> resourceParameters =
                getObjectTypeParameters().get(objectType);
            if (resourceParameters == null) {
                log.info("No indexing information found for objectType "
                    + objectType);
                return;
            }
            for (String indexName : resourceParameters.keySet()) {
                if (log.isDebugEnabled()) {
                    log.debug("indexing for index " + indexName);
                }
                doIndexing(resource, objectType, indexName, action, isAsynch,
                    xml);
            }
        }
        else if (action
            .equalsIgnoreCase(de.escidoc.core.common.business.Constants.INDEXER_QUEUE_ACTION_PARAMETER_DELETE_VALUE)) {
            gsearchHandler.requestDeletion(resource, null, null);
            gsearchHandler.requestDeletion(resource, null,
                Constants.LATEST_VERSION_PID_SUFFIX);
            gsearchHandler.requestDeletion(resource, null,
                Constants.LATEST_RELEASE_PID_SUFFIX);
        }
        else if (action
            .equalsIgnoreCase(de.escidoc.core.common.business.Constants.INDEXER_QUEUE_ACTION_PARAMETER_CREATE_EMPTY_VALUE)) {
            gsearchHandler.requestCreateEmpty(null);
        }
    }

    /**
     * Check indexing-action (update, delete or create-empty). 
     * Do action for specified index.
     * 
     * 
     * @param resource
     *            String resource.
     * @param objectType
     *            String name of the resource (eg Item, Container...).
     * @param indexName
     *            name of the index
     * @param action
     *            String indexing-action (update, delete, create-empty).
     * @param isAsynch
     *            boolean called asynch.
     * 
     * @param xml
     *            object-representation in xml.
     * @throws SystemException
     *             e
     */
    public void doIndexing(
        final String resource, final String objectType, final String indexName,
        final String action, final boolean isAsynch, final String xml)
        throws SystemException {

        if (log.isDebugEnabled()) {
            log.debug("indexing " + resource + ", objectType: " + objectType
                + ", indexName: " + indexName + ", action: " + action
                + ", isAsynch: " + isAsynch + ", xml: " + xml);
        }

        // get Index-Parameters for resourceName
        Map<String, Map<String, Object>> resourceParameters =
            getObjectTypeParameters().get(objectType);
        if (resourceParameters == null) {
            log.info("No indexing information found for objectType "
                + objectType);
            return;
        }
        Map<String, Object> parameters = resourceParameters.get(indexName);

        if (parameters == null) {
            return;
        }

        String versionedResource = resource;
        String pidSuffix = null;
        String latestReleasedVersion = null;
        // Check if latest released version has to get indexed
        if (parameters.get("indexReleasedVersion") != null
            && (new Boolean((String) parameters.get("indexReleasedVersion")) || parameters
                .get("indexReleasedVersion").equals("both"))) {
            // get latest released version
            if (log.isDebugEnabled()) {
                log.debug("index released version, so do checks");
            }
            latestReleasedVersion =
                tripleStoreUtility.getPropertiesElements(
                    XmlUtility.getIdFromURI(resource),
                    TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER);
            String thisVersion =
                tripleStoreUtility.getPropertiesElements(
                    XmlUtility.getIdFromURI(resource),
                    TripleStoreUtility.PROP_LATEST_VERSION_NUMBER);

            if (log.isDebugEnabled()) {
                log.debug("latest released version: " + latestReleasedVersion
                    + ", thisVersion: " + thisVersion);
            }
            if (new Boolean((String) parameters.get("indexReleasedVersion"))) {
                if ((latestReleasedVersion == null) || (thisVersion == null)) {
                    if (log.isDebugEnabled()) {
                        log.debug("returning");
                    }
                    return;
                }
                if (!latestReleasedVersion.equals(thisVersion)) {
                    // adapt resource
                    versionedResource = resource + ":" + latestReleasedVersion;
                }
            }
            else {
                if (latestReleasedVersion == null) {
                    pidSuffix = Constants.LATEST_VERSION_PID_SUFFIX;
                }
                else {
                    if (!latestReleasedVersion.equals(thisVersion)) {
                        pidSuffix = Constants.LATEST_VERSION_PID_SUFFIX;
                    }
                    else {
                        pidSuffix = Constants.LATEST_RELEASE_PID_SUFFIX;
                    }
                }
            }
        }

        if (action == null
            || action
                .equalsIgnoreCase(de.escidoc.core.common.business.Constants.INDEXER_QUEUE_ACTION_PARAMETER_UPDATE_VALUE)) {
            try {
                // check the asynch-mode
                if (parameters.get("indexAsynchronous") == null
                    || !new Boolean(
                        (String) parameters.get("indexAsynchronous"))
                        .equals(isAsynch)) {
                    if (log.isDebugEnabled()) {
                        log.debug("is Asynch: " + isAsynch
                            + " and indexAsynchronous-param is "
                            + parameters.get("indexAsynchronous")
                            + ", so returning");
                    }
                    return;
                }

                // check if only objects with certain prerequisite
                // shall be in the index
                // (prerequisite is an xpath-Expression)
                int prerequisite =
                    checkPrerequisites(xml, parameters, resource, null);
                if (log.isDebugEnabled()) {
                    log.debug("prerequisites found " + prerequisite);
                }
                if (prerequisite == Constants.DO_NOTHING) {
                    if (log.isDebugEnabled()) {
                        log.debug("returning");
                    }
                    return;
                }

                if (log.isDebugEnabled()) {
                    log.debug("Updating index " + indexName + " with "
                        + versionedResource);
                }
                if (prerequisite == Constants.DO_DELETE) {
                    if (log.isDebugEnabled()) {
                        log.debug("request deletion " + indexName + " with "
                            + resource);
                    }
                    gsearchHandler.requestDeletion(resource, indexName,
                        pidSuffix);
                }
                else {
                    if (log.isDebugEnabled()) {
                        log.debug("request indexing " + indexName + " with "
                            + versionedResource);
                    }
                    if (pidSuffix != null
                        && pidSuffix
                            .equals(Constants.LATEST_RELEASE_PID_SUFFIX)) {
                        gsearchHandler.requestDeletion(versionedResource,
                            indexName, Constants.LATEST_VERSION_PID_SUFFIX);
                    }
                    if (pidSuffix != null
                        && pidSuffix
                            .equals(Constants.LATEST_VERSION_PID_SUFFIX)
                        && latestReleasedVersion != null) {
                        // reindex latest released version
                        gsearchHandler.requestIndexing(versionedResource + ":"
                            + latestReleasedVersion, indexName,
                            Constants.LATEST_RELEASE_PID_SUFFIX,
                            (String) parameters
                                .get("indexFulltextVisibilities"));
                    }
                    gsearchHandler.requestIndexing(versionedResource,
                        indexName, pidSuffix,
                        (String) parameters.get("indexFulltextVisibilities"));
                }
            }
            catch (Error e) {
                throw new SystemException(e.getMessage());
            }
        }
        else if (action
            .equalsIgnoreCase(de.escidoc.core.common.business.Constants.INDEXER_QUEUE_ACTION_PARAMETER_DELETE_VALUE)) {
            if (log.isDebugEnabled()) {
                log.debug("request deletion " + indexName + ", resource "
                    + resource);
            }
            gsearchHandler.requestDeletion(resource, indexName, pidSuffix);
        }
        else if (action
            .equalsIgnoreCase(de.escidoc.core.common.business.Constants.INDEXER_QUEUE_ACTION_PARAMETER_CREATE_EMPTY_VALUE)) {
            if (log.isDebugEnabled()) {
                log.debug("request createEmpty " + indexName);
            }
            gsearchHandler.requestCreateEmpty(indexName);
        }
    }

    /**
     * Check prerequisite-XPaths. -if parameter prerequisites exists: -if some
     * prerequisite-deletion-value matches, then delete object from index.
     * 
     * -if some prerequisite-indexing-value matches, then update object in
     * index.
     * 
     * -if parameter prerequisites doesnt exist: -update index
     * 
     * 
     * @param xml
     *            String resource-xml.
     * @param parameters
     *            parameters for resource + index.
     * @param resource
     *            String resource-identifier.
     * @param domObject
     *            Dom-Object that holds resource-xml.
     * 
     * @return int action to take (delete, update, nothing)
     * @throws Exception
     *             e
     */
    private int checkPrerequisites(
        String xml, final Map<String, Object> parameters,
        final String resource, Document domObject) throws SystemException {
        if (log.isDebugEnabled()) {
            log.debug("prerequisites is " + parameters.get("prerequisites"));
        }
        if (parameters.get("prerequisites") == null) {
            return Constants.DO_UPDATE;
        }
        else {
            try {
                HashMap<String, String> prerequisites =
                    (HashMap<String, String>) parameters.get("prerequisites");
                if (prerequisites.get("indexingPrerequisiteXpath") == null
                    && prerequisites.get("deletePrerequisiteXpath") == null) {
                    return Constants.DO_UPDATE;
                }
                else {
                    if (xml == null) {
                        if (log.isDebugEnabled()) {
                            log.debug("xml is null, requesting it from cache");
                        }
                        xml =
                            indexingCacheHandler
                                .retrieveObjectFromCache(resource);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("xml is: " + xml);
                    }
                    long time = System.currentTimeMillis();
                    if (domObject == null) {
                        domObject = getXmlAsDocument(xml);
                    }
                    if (prerequisites.get("indexingPrerequisiteXpath") != null) {
                        Node updateNode =
                            XPathAPI.selectSingleNode(domObject,
                                prerequisites.get("indexingPrerequisiteXpath"));
                        if (log.isDebugEnabled()) {
                            log
                                .debug("gsearchindexing xpath-exec on DOM-Object "
                                    + " needed "
                                    + (System.currentTimeMillis() - time)
                                    + " ms");
                        }
                        if (updateNode != null) {
                            return Constants.DO_UPDATE;
                        }
                    }
                    if (prerequisites.get("deletePrerequisiteXpath") != null) {
                        Node deleteNode =
                            XPathAPI.selectSingleNode(domObject,
                                prerequisites.get("deletePrerequisiteXpath"));
                        if (log.isDebugEnabled()) {
                            log
                                .debug("gsearchindexing xpath-exec on DOM-Object "
                                    + " needed "
                                    + (System.currentTimeMillis() - time)
                                    + " ms");
                        }
                        if (deleteNode != null) {
                            return Constants.DO_DELETE;
                        }
                    }
                }
            }
            catch (TransformerException e) {
                throw new SystemException(e.getMessage());
            }
        }
        return Constants.DO_NOTHING;
    }

    /**
     * Check if the given id already exists in the given index.
     * 
     * @param id
     *            resource id
     * @param objectType
     *            String name of the resource (eg Item, Container...).
     * @param indexName
     *            name of the index (null or "all" means to search in all
     *            indexes)
     * 
     * @return true if the resource already exists
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     */
    public boolean exists(
        final String id, final String objectType, final String indexName)
        throws SystemException {
        boolean result = false;
        Map<String, Map<String, Object>> resourceParameters =
            getObjectTypeParameters().get(objectType);

        if ((id != null) && (resourceParameters != null)) {
            if (indexName == null || indexName.trim().length() == 0
                || indexName.equalsIgnoreCase("all")) {
                for (String indexName2 : resourceParameters.keySet()) {
                    result = exists(id, indexName2);
                    if (result) {
                        break;
                    }
                }
            }
            else {
                result = exists(id, indexName);
            }
        }
        return result;
    }

    /**
     * Check if the given id already exists in the given index.
     * 
     * @param id
     *            resource id
     * @param indexName
     *            name of the index
     * 
     * @return true if the resource already exists
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     */
    private boolean exists(final String id, final String indexName)
        throws SystemException {
        boolean result = false;

        try {

            HttpParams params = new BasicHttpParams();
            Scheme http =
                new Scheme("http", PlainSocketFactory.getSocketFactory(), 80);
            SchemeRegistry sr = new SchemeRegistry();
            sr.register(http);
            ClientConnectionManager cm =
                new ThreadSafeClientConnManager(params, sr);

            DefaultHttpClient client = new DefaultHttpClient(cm, params);

            StringBuffer query = new StringBuffer("");
            for (int i = 0; i < Constants.INDEX_PRIM_KEY_FIELDS.length; i++) {
                if (query.length() > 0) {
                    query.append(" or ");
                }
                query.append(Constants.INDEX_PRIM_KEY_FIELDS[i])
                                            .append("=").append(id);
            }

            HttpGet httpGet =
                new HttpGet(EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.SRW_URL)
                    + "/search/"
                    + indexName
                    + "?query="
                    + URLEncoder.encode(query.toString(), "UTF-8"));

            HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
                Pattern numberOfRecordsPattern =
                    Pattern.compile("numberOfRecords>(.*?)<");

                Matcher m =
                    numberOfRecordsPattern.matcher(EntityUtils.toString(
                        response.getEntity(), HTTP.UTF_8));

                if (m.find()) {
                    result = Integer.parseInt(m.group(1)) > 0;
                }
            }
        }
        catch (IOException e) {
            throw new SystemException(e.getMessage());
        }
        return result;
    }

    /**
     * Get Names of available indexes from gsearch-config.
     * 
     * @return COllection with indexNames
     * @throws IOException
     *             e
     * @throws SystemException
     *             e
     * 
     */
    private Collection<String> getIndexNames() throws IOException,
        SystemException {
        if (indexNames == null) {
            // Get index names from gsearch-config
            Map<String, Map<String, String>> indexConfig =
                gsearchHandler.getIndexConfigurations();
            indexNames = new ArrayList<String>();
            indexNames.addAll(indexConfig.keySet());
            if (log.isDebugEnabled()) {
                log.debug("configured indexNames: " + indexConfig.keySet());
            }
        }
        return indexNames;
    }

    /**
     * Get configuration for available indexes.
     * 
     * @throws IOException
     *             e
     * @throws SystemException
     *             e
     * 
     */
    private void getIndexConfigs() throws IOException, SystemException {
        // Build IndexInfo HashMap
        objectTypeParameters =
            new HashMap<String, Map<String, Map<String, Object>>>();
        String searchPropertiesDirectory =
            EscidocConfiguration.getInstance().get(
                EscidocConfiguration.SEARCH_PROPERTIES_DIRECTORY);
        for (String indexName : getIndexNames()) {
            if (log.isDebugEnabled()) {
                log.debug("getting configuration for index " + indexName);
            }
            Properties indexProps = new Properties();
            InputStream propStream = null;
            try {
                propStream = IndexingHandler.class.getResourceAsStream("/"
                    + searchPropertiesDirectory + "/index/" + indexName
                    + "/index.object-types.properties");
                if (propStream == null) {
                    throw new SystemException(searchPropertiesDirectory + "/index/"
                        + indexName + "/index.object-types.properties "
                        + "not found in classpath");
                }
                indexProps.load(propStream);
            } finally {
                if(propStream != null) {
                    try {
                        propStream.close();
                    } catch(IOException e) {
                        // ignore this exception
                    }
                }
            }
            Pattern objectTypePattern = Pattern.compile(".*?\\.(.*?)\\..*");
            Matcher objectTypeMatcher = objectTypePattern.matcher("");
            HashSet<String> objectTypes = new HashSet<String>();
            Iterator<Object> iter = indexProps.keySet().iterator();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                if (key.startsWith("Resource")) {
                    String propVal = indexProps.getProperty(key);
                    if (log.isDebugEnabled()) {
                        log.debug("found property " + key + ":" + propVal);
                    }
                    objectTypeMatcher.reset(key);
                    if (!objectTypeMatcher.matches()) {
                        throw new IOException(key
                            + " is not a supported property");
                    }
                    String objectType =
                        de.escidoc.core.common.business.Constants.RESOURCES_NS_URI
                            + objectTypeMatcher.group(1);
                    if (objectTypeParameters.get(objectType) == null) {
                        if (log.isDebugEnabled()) {
                            log.debug("initializing HashMap for objectType "
                                + objectType);
                        }
                        objectTypeParameters.put(objectType,
                            new HashMap<String, Map<String, Object>>());
                    }
                    if (objectTypeParameters.get(objectType).get(indexName) == null) {
                        objectTypeParameters.get(objectType).put(indexName,
                            new HashMap<String, Object>());
                        if (log.isDebugEnabled()) {
                            log.debug("adding " + indexName + " to "
                                + objectType);
                        }
                        objectTypes.add(objectType);
                    }
                    if (key.contains("Prerequisite")) {
                        if (objectTypeParameters
                            .get(objectType).get(indexName)
                            .get("prerequisites") == null) {
                            objectTypeParameters
                                .get(objectType)
                                .get(indexName)
                                .put("prerequisites",
                                    new HashMap<String, String>());
                        }
                        ((HashMap<String, String>) objectTypeParameters
                            .get(objectType).get(indexName)
                            .get("prerequisites")).put(
                            key.replaceFirst(".*\\.", ""), propVal);
                        if (log.isDebugEnabled()) {
                            log.debug("adding prerequisite " + key + ":"
                                + propVal);
                        }
                    }
                    else {
                        objectTypeParameters
                            .get(objectType).get(indexName)
                            .put(key.replaceFirst(".*\\.", ""), propVal);
                        if (log.isDebugEnabled()) {
                            log
                                .debug("adding parameter " + key + ":"
                                    + propVal);
                        }
                    }
                }
            }
            for (String objectType : objectTypes) {
                if (objectTypeParameters
                    .get(objectType).get(indexName).get("indexAsynchronous") == null) {
                    objectTypeParameters
                        .get(objectType).get(indexName)
                        .put("indexAsynchronous", "false");
                }
                if (objectTypeParameters
                    .get(objectType).get(indexName).get("indexReleasedVersion") == null) {
                    objectTypeParameters
                        .get(objectType).get(indexName)
                        .put("indexReleasedVersion", "false");
                }
            }
        }
    }

    /**
     * Get XML-String as Dom-Document representation.
     * 
     * @param xml
     *            xml
     * @return Document xml as dom-Document
     * @throws Exception
     *             e
     */
    private Document getXmlAsDocument(final String xml) throws SystemException {
        try {
            InputStream in =
                new ByteArrayInputStream(
                    xml.getBytes(XmlUtility.CHARACTER_ENCODING));
            return docBuilder.parse(new InputSource(in));
        }
        catch (Exception e) {
            throw new SystemException(e.getMessage());
        }
    }

    /**
     * @return the objectTypeParameters
     * @throws WebserverSystemException
     *             e
     */
    public Map<String, Map<String, Map<String, Object>>> getObjectTypeParameters()
        throws WebserverSystemException {
        if (objectTypeParameters == null) {
            try {
                getIndexConfigs();
            }
            catch (Exception e) {
                throw new WebserverSystemException(e.getMessage());
            }
        }
        return objectTypeParameters;
    }

    /**
     * Setting the gsearchHandler.
     * 
     * @param gsearchHandler
     *            The gsearchHandler to set.
     * @spring.property ref="common.business.indexing.GsearchHandler"
     */
    public final void setGsearchHandler(final GsearchHandler gsearchHandler) {
        this.gsearchHandler = gsearchHandler;
    }

    /**
     * Setting the indexingCacheHandler.
     * 
     * @param indexingCacheHandler
     *            The indexingCacheHandler to set.
     * @spring.property ref="common.business.indexing.IndexingCacheHandler"
     */
    public final void setIndexingCacheHandler(
        final IndexingCacheHandler indexingCacheHandler) {
        this.indexingCacheHandler = indexingCacheHandler;
    }

    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

    /**
     * Injects the TripleStore utility.
     * 
     * @spring.property ref="business.TripleStoreUtility"
     * @param tripleStoreUtility
     *            TripleStoreUtility from Spring
     */
    public void setTripleStoreUtility(
        final TripleStoreUtility tripleStoreUtility) {
        this.tripleStoreUtility = tripleStoreUtility;
    }
}
