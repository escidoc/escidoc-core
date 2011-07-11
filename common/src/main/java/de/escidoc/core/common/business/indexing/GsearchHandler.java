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

package de.escidoc.core.common.business.indexing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.system.ApplicationServerSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.service.ConnectionUtility;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.GsearchIndexConfigurationHandler;
import de.escidoc.core.common.util.stax.handler.GsearchRepositoryInfoHandler;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * Execute http-request to fedoragsearch. Update with requestIndexing, delete with requestDeletion.
 */
@Service("common.business.indexing.GsearchHandler")
public class GsearchHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GsearchHandler.class);

    private static final Pattern SPLIT_PATTERN = Pattern.compile("\\s");

    private Map<String, Map<String, String>> indexConfigurations;

    private Map<String, String> repositoryInfo;

    private Set<String> supportedMimeTypes;

    private static final int MAX_ERROR_RETRIES = 15;

    @Autowired
    @Qualifier("escidoc.core.common.util.service.ConnectionUtility")
    private ConnectionUtility connectionUtility;

    /**
     * requests indexing by calling fedoragsearch-servlet.
     * <p/>
     * <pre>
     *        execute get-request with hardcoded index + repositoryname
     *        to fedoragsearch.
     * </pre>
     *
     * @param resource  String resource.
     * @param index     String name of the index.
     * @param pidSuffix PidSuffix for latestVersion, latestRelease if both is in index.
     * @param indexFulltextVisibilities
     * @return String response
     * @throws ApplicationServerSystemException
     *          e
     */
    public String requestIndexing(
        final String resource, final String index, final String pidSuffix, final String indexFulltextVisibilities)
        throws ApplicationServerSystemException {
        final Set<String> indexNames = new HashSet<String>();
        final StringBuilder responses = new StringBuilder();
        final StringBuilder exceptions = new StringBuilder();
        if (index == null || index.isEmpty()) {
            indexNames.addAll(getIndexConfigurations().keySet());
        }
        else {
            indexNames.add(index);
        }
        for (final String indexName : indexNames) {
            String updateIndexParams =
                Constants.INDEX_NAME_MATCHER.reset(Constants.GSEARCH_UPDATE_INDEX_PARAMS).replaceFirst(indexName);
            updateIndexParams = Constants.VALUE_MATCHER.reset(updateIndexParams).replaceFirst(resource);
            try {
                final String gsearchUrl = EscidocConfiguration.getInstance().get(EscidocConfiguration.GSEARCH_URL);

                String stylesheetParameters =
                    Constants.SUPPORTED_MIMETYPES_MATCHER
                        .reset(Constants.GSEARCH_STYLESHEET_PARAMS).replaceFirst(
                            URLEncoder.encode(getRepositoryInfo().get("SupportedMimeTypes"),
                                XmlUtility.CHARACTER_ENCODING));
                stylesheetParameters =
                    pidSuffix == null || pidSuffix.length() == 0 ? Constants.PID_VERSION_IDENTIFIER_TOTAL_MATCHER
                        .reset(stylesheetParameters).replaceFirst("") : Constants.PID_VERSION_IDENTIFIER_MATCHER.reset(
                        stylesheetParameters).replaceFirst(pidSuffix);
                stylesheetParameters =
                    indexFulltextVisibilities == null || indexFulltextVisibilities.length() == 0 ? Constants.INDEX_FULLTEXT_VISIBILITIES_TOTAL_MATCHER
                        .reset(stylesheetParameters).replaceFirst("") : Constants.INDEX_FULLTEXT_VISIBILITIES_MATCHER
                        .reset(stylesheetParameters).replaceFirst(
                            URLEncoder.encode(indexFulltextVisibilities, XmlUtility.CHARACTER_ENCODING));
                updateIndexParams += stylesheetParameters;

                connectionUtility.setTimeout(Constants.REQUEST_TIMEOUT);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("requesting " + updateIndexParams + " from " + gsearchUrl);
                }

                final String response =
                    connectionUtility.getRequestURLAsString(new URL(gsearchUrl + updateIndexParams));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("response: " + response);
                }

                // Catch Exceptions
                handleGsearchException(indexName, updateIndexParams, response, 0);

                responses.append(response).append('\n');
            }
            catch (final IOException e) {
                exceptions.append(e.getMessage()).append('\n');
            }
            catch (final WebserverSystemException e) {
                exceptions.append(e.getMessage()).append('\n');
            }
        }
        if (exceptions.length() > 0) {
            throw new ApplicationServerSystemException("Error while indexing resource." + exceptions);
        }
        return responses.toString();
    }

    /**
     * requests deletion of one index-entry by calling fedoragsearch-servlet.
     * <p/>
     * <pre>
     *        execute get-request with hardcoded index + repositoryname
     *        to fedoragsearch.
     * </pre>
     *
     * @param resource  String resource.
     * @param index     String name of the index.
     * @param pidSuffix PidSuffix for latestVersion, latestRelease if both is in index.
     * @return String response
     * @throws ApplicationServerSystemException
     *          e
     */
    public String requestDeletion(String resource, final String index, final String pidSuffix)
        throws ApplicationServerSystemException {
        final Set<String> indexNames = new HashSet<String>();
        final StringBuilder responses = new StringBuilder();
        final StringBuilder exceptions = new StringBuilder();
        if (pidSuffix != null) {
            resource = resource + ':' + pidSuffix;
        }
        if (index == null || index.isEmpty()) {
            indexNames.addAll(getIndexConfigurations().keySet());
        }
        else {
            indexNames.add(index);
        }
        for (final String indexName : indexNames) {
            String deleteIndexParams =
                Constants.INDEX_NAME_MATCHER.reset(Constants.GSEARCH_DELETE_INDEX_PARAMS).replaceFirst(indexName);
            deleteIndexParams =
                Constants.VALUE_MATCHER.reset(deleteIndexParams).replaceFirst(
                    XmlUtility.getObjidWithoutVersion(XmlUtility.getIdFromURI(resource)));

            try {
                final String gsearchUrl = EscidocConfiguration.getInstance().get(EscidocConfiguration.GSEARCH_URL);
                connectionUtility.setTimeout(Constants.REQUEST_TIMEOUT);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("requesting " + deleteIndexParams + " from " + gsearchUrl);
                }
                final String response =
                    connectionUtility.getRequestURLAsString(new URL(gsearchUrl + deleteIndexParams));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("response: " + response);
                }

                // Catch Exceptions
                handleGsearchException(indexName, deleteIndexParams, response, 0);

                responses.append(response).append('\n');
            }
            catch (final Exception e) {
                exceptions.append(e.getMessage()).append('\n');
            }
        }
        if (exceptions.length() > 0) {
            throw new ApplicationServerSystemException(exceptions.toString());
        }
        return responses.toString();
    }

    /**
     * requests new empty creation of index by calling fedoragsearch-servlet.
     * <p/>
     * <pre>
     *        execute get-request with hardcoded index + repositoryname
     *        to fedoragsearch.
     * </pre>
     *
     * @param index String name of the index.
     * @return String response
     * @throws ApplicationServerSystemException
     *          e
     */
    public String requestCreateEmpty(String index) throws ApplicationServerSystemException {
        if (index == null) {
            index = "";
        }
        final String createEmptyParams =
            Constants.INDEX_NAME_MATCHER.reset(Constants.GSEARCH_CREATE_EMPTY_INDEX_PARAMS).replaceFirst(index);
        try {
            final String gsearchUrl = EscidocConfiguration.getInstance().get(EscidocConfiguration.GSEARCH_URL);
            connectionUtility.setTimeout(Constants.REQUEST_TIMEOUT);
            String response = connectionUtility.getRequestURLAsString(new URL(gsearchUrl + createEmptyParams));
            // Catch Exceptions
            if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                if (Constants.LOCK_OBTAIN_TIMEOUT_MATCHER.reset(response).matches()) {
                    deleteLock(response);
                    response = connectionUtility.getRequestURLAsString(new URL(gsearchUrl + createEmptyParams));
                    if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                        deleteIndexDirs();
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("requesting " + createEmptyParams + " from " + gsearchUrl);
                    }
                    response = connectionUtility.getRequestURLAsString(new URL(gsearchUrl + createEmptyParams));
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("response: " + response);
                    }
                    if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                        throw new Exception(response);
                    }
                }
                else {
                    throw new Exception(response);
                }
            }
            return response;
        }
        catch (final Exception e) {
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
    }

    /**
     * requests optimization of the given index by calling fedoragsearch-servlet.
     * <p/>
     * <pre>
     *        execute get-request with hardcoded index
     *        to fedoragsearch.
     * </pre>
     *
     * @param index String name of the index.
     * @return String response
     * @throws ApplicationServerSystemException
     *          e
     */
    public String requestOptimize(final String index) throws ApplicationServerSystemException {
        final Set<String> indexNames = new HashSet<String>();
        final StringBuilder responses = new StringBuilder();
        final StringBuilder exceptions = new StringBuilder();
        if (index == null || index.isEmpty()) {
            indexNames.addAll(getIndexConfigurations().keySet());
        }
        else {
            indexNames.add(index);
        }
        for (final String indexName : indexNames) {
            final String optimizeIndexParams =
                Constants.INDEX_NAME_MATCHER.reset(Constants.GSEARCH_OPTIMIZE_INDEX_PARAMS).replaceFirst(indexName);
            try {
                final String gsearchUrl = EscidocConfiguration.getInstance().get(EscidocConfiguration.GSEARCH_URL);
                connectionUtility.setTimeout(Constants.REQUEST_TIMEOUT);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("requesting " + optimizeIndexParams + " from " + gsearchUrl);
                }
                final String response =
                    connectionUtility.getRequestURLAsString(new URL(gsearchUrl + optimizeIndexParams));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("response: " + response);
                }
                // Catch Exceptions
                handleGsearchException(indexName, optimizeIndexParams, response, 0);

                responses.append(response).append('\n');
            }
            catch (final Exception e) {
                exceptions.append(e.getMessage()).append('\n');
            }
        }
        if (exceptions.length() > 0) {
            throw new ApplicationServerSystemException(exceptions.toString());
        }
        return responses.toString();
    }

    /**
     * requests available index-configurations from fedoragsearch.
     * <p/>
     * <pre>
     *        execute get-request to fedoragsearch.
     * </pre>
     *
     * @return HashMap<String, HashMap<String, String>> index-configurations
     * @throws ApplicationServerSystemException
     *          e
     */
    private Map<String, Map<String, String>> requestIndexConfiguration() throws ApplicationServerSystemException {
        try {
            final String gsearchUrl = EscidocConfiguration.getInstance().get(EscidocConfiguration.GSEARCH_URL);
            connectionUtility.setTimeout(Constants.REQUEST_TIMEOUT);
            final String response =
                connectionUtility.getRequestURLAsString(new URL(gsearchUrl
                    + Constants.GSEARCH_GET_INDEX_CONFIGURATION_PARAMS));
            // Catch Exceptions
            if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                throw new Exception(response);
            }
            final StaxParser sp = new StaxParser();
            final GsearchIndexConfigurationHandler handler = new GsearchIndexConfigurationHandler();
            sp.addHandler(handler);

            sp.parse(new ByteArrayInputStream(response.getBytes(XmlUtility.CHARACTER_ENCODING)));
            return handler.getGsearchIndexConfiguration();
        }
        catch (final Exception e) {
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
    }

    /**
     * requests information about repository.
     * <p/>
     * <pre>
     *        execute get-request to fedoragsearch.
     * </pre>
     *
     * @return HashMap<String, String> repository-info
     * @throws ApplicationServerSystemException
     *          e
     */
    private Map<String, String> requestRepositoryInfo() throws ApplicationServerSystemException {
        try {
            final String gsearchUrl = EscidocConfiguration.getInstance().get(EscidocConfiguration.GSEARCH_URL);
            connectionUtility.setTimeout(Constants.REQUEST_TIMEOUT);
            final String response =
                connectionUtility.getRequestURLAsString(new URL(gsearchUrl
                    + Constants.GSEARCH_GET_REPOSITORY_INFO_PARAMS));
            // Catch Exceptions
            if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                throw new Exception(response);
            }
            final StaxParser sp = new StaxParser();
            final GsearchRepositoryInfoHandler handler = new GsearchRepositoryInfoHandler();
            sp.addHandler(handler);

            sp.parse(new ByteArrayInputStream(response.getBytes(XmlUtility.CHARACTER_ENCODING)));
            return handler.getGsearchRepositoryInfo();
        }
        catch (final Exception e) {
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
    }

    /**
     * @return the indexConfigurations
     * @throws ApplicationServerSystemException
     *          e
     */
    public Map<String, Map<String, String>> getIndexConfigurations() throws ApplicationServerSystemException {
        if (this.indexConfigurations == null) {
            this.indexConfigurations = requestIndexConfiguration();
        }
        return this.indexConfigurations;
    }

    /**
     * @return the repositoryInfo
     * @throws ApplicationServerSystemException
     *          e
     */
    public Map<String, String> getRepositoryInfo() throws ApplicationServerSystemException {
        if (this.repositoryInfo == null) {
            this.repositoryInfo = requestRepositoryInfo();
        }
        return this.repositoryInfo;
    }

    /**
     * @return the supportedMimeTypes
     * @throws ApplicationServerSystemException
     *          e
     */
    public Set<String> getSupportedMimeTypes() throws ApplicationServerSystemException {
        if (this.supportedMimeTypes == null) {
            this.supportedMimeTypes = new HashSet<String>();
            getRepositoryInfo();
            if (repositoryInfo.get("SupportedMimeTypes") != null) {
                final String[] supportedMimeTypesArr = SPLIT_PATTERN.split(repositoryInfo.get("SupportedMimeTypes"));
                supportedMimeTypes.addAll(Arrays.asList(supportedMimeTypesArr));
            }
        }
        return this.supportedMimeTypes;
    }

    /**
     * Check if gsearch returned an Exception. If it is an Exception and saying that an index-directory does not exist
     * or does not have segment-files, create this index-directory with segment-files. Otherwise throw Exception.
     *
     * @param index    name of the index
     * @param request  request that was send to gsearch
     * @param response response thatr was returned by gsearch
     * @param retries  numer of retries already executed
     * @throws de.escidoc.core.common.exceptions.system.ApplicationServerSystemException
     */
    private void handleGsearchException(final String index, final String request, String response, int retries)
        throws ApplicationServerSystemException {
        try {
            if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                String myIndex = index;
                // If index-directory does not exist yet
                // (first time indexer runs)
                // create empty index directory and then recall
                // gsearch
                if (Constants.NO_INDEX_DIR_MATCHER.reset(response).matches()) {
                    final String gsearchUrl = EscidocConfiguration.getInstance().get(EscidocConfiguration.GSEARCH_URL);
                    if (StringUtils.isEmpty(myIndex)) {
                        if (!Constants.NO_INDEX_DIR_INDEX_NAME_MATCHER.reset(response).matches()) {
                            throw new ApplicationServerSystemException(response);
                        }
                        myIndex = Constants.NO_INDEX_DIR_INDEX_NAME_MATCHER.group(1);
                        if (StringUtils.isEmpty(myIndex)) {
                            throw new ApplicationServerSystemException(response);
                        }
                    }
                    final String createEmptyParams =
                        Constants.INDEX_NAME_MATCHER.reset(Constants.GSEARCH_CREATE_EMPTY_INDEX_PARAMS).replaceFirst(
                            myIndex);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("creating empty index");
                    }
                    response = connectionUtility.getRequestURLAsString(new URL(gsearchUrl + createEmptyParams));
                    if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                        throw new ApplicationServerSystemException(response);
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("retrying request " + request);
                    }
                    response = connectionUtility.getRequestURLAsString(new URL(gsearchUrl + request));
                    if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                        if (retries < MAX_ERROR_RETRIES) {
                            retries++;
                            handleGsearchException(index, request, response, retries);
                        }
                        else {
                            throw new ApplicationServerSystemException(response);
                        }
                    }
                }
                else {
                    throw new ApplicationServerSystemException(response);
                }
            }
        }
        catch (final IOException e) {
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
        catch (final WebserverSystemException e) {
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
    }

    /**
     * Delete Index-Lock.
     *
     * @param response Errormessage that contains the path to the lockfile
     */
    private static void deleteLock(final String response) {
        try {
            String lockfilePath = response.replaceFirst("(?s).*Lock@", "");
            lockfilePath = lockfilePath.replaceFirst("(?s)<.*", "");
            final File file = new File(lockfilePath);
            file.delete();
        }
        catch (final Exception e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on deleting lock.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on deleting lock.", e);
            }
        }
    }

    /**
     * Delete Directories with Indexes.
     */
    private void deleteIndexDirs() {
        try {
            final String jbossDataDirPath = System.getProperty("jboss.server.data.dir");
            final String indexRootDirPath = jbossDataDirPath + "/index/lucene";
            final File indexRootDir = new File(indexRootDirPath);
            final String[] indexes = indexRootDir.list();
            for (final String indexe : indexes) {
                final File indexDir = new File(indexRootDir, indexe);
                deleteDir(indexDir);
            }
        }
        catch (final Exception e) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on deleting index directories.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on deleting index directories.", e);
            }
        }
    }

    /**
     * Delete Directory with Indexes.
     *
     * @param path path to Directory
     * @return boolean if delete was successful
     */
    public static boolean deleteDir(final File path) {
        if (path.exists()) {
            final File[] files = path.listFiles();
            for (final File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                }
                else {
                    file.delete();
                }
            }
        }
        return path.delete();
    }

    /**
     * See Interface for functional description.
     *
     * @param connectionUtility The HTTP connection utility.
     */
    public void setConnectionUtility(final ConnectionUtility connectionUtility) {

        this.connectionUtility = connectionUtility;
    }

}
