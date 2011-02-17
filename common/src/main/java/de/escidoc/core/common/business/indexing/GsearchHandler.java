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

import de.escidoc.core.common.exceptions.system.ApplicationServerSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.ConnectionUtility;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.GsearchIndexConfigurationHandler;
import de.escidoc.core.common.util.stax.handler.GsearchRepositoryInfoHandler;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Execute http-request to fedoragsearch. Update with requestIndexing, delete
 * with requestDeletion.
 * 
 * @spring.bean id = "common.business.indexing.GsearchHandler"
 */
public class GsearchHandler {

    private static AppLogger log =
        new AppLogger(GsearchHandler.class.getName());

    private Map<String, Map<String, String>> 
                                indexConfigurations = null;

    private Map<String, String>    repositoryInfo = null;

    private Set<String>    supportedMimeTypes = null;
    
    private static final int MAX_ERROR_RETRIES = 15;

    private ConnectionUtility connectionUtility = null;

    /**
     * requests indexing by calling fedoragsearch-servlet.
     * 
     * <pre>
     *        execute get-request with hardcoded index + repositoryname
     *        to fedoragsearch.
     * </pre>
     * 
     * @param resource
     *            String resource.
     * @param index
     *            String name of the index.
     * @param pidSuffix
     *            PidSuffix for latestVersion, latestRelease
     *            if both is in index.
     * @return String response
     * 
     * @throws ApplicationServerSystemException
     *             e
     */
    public String requestIndexing(
        final String resource, 
        String index, 
        final String pidSuffix,
        final String indexFulltextVisibilities)
        throws ApplicationServerSystemException {
        long time = System.currentTimeMillis();
        if (index == null) {
            index = "";
        }
        String updateIndexParams = 
            Constants.INDEX_NAME_MATCHER.reset(
                    Constants.GSEARCH_UPDATE_INDEX_PARAMS)
                                                .replaceFirst(index);
        updateIndexParams = 
            Constants.VALUE_MATCHER.reset(updateIndexParams)
                                                .replaceFirst(resource);
        try {
            String gsearchUrl =
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.GSEARCH_URL);

            String stylesheetParameters =
                Constants.SUPPORTED_MIMETYPES_MATCHER.reset(
                        Constants.GSEARCH_STYLESHEET_PARAMS)
                            .replaceFirst(URLEncoder.encode(
                                    getRepositoryInfo()
                                        .get("SupportedMimeTypes")
                                    , XmlUtility.CHARACTER_ENCODING));
            if (pidSuffix == null || pidSuffix.equals("")) {
                stylesheetParameters =
                    Constants.PID_VERSION_IDENTIFIER_TOTAL_MATCHER.reset(
                                            stylesheetParameters)
                                                .replaceFirst("");
            } else {
                stylesheetParameters =
                    Constants.PID_VERSION_IDENTIFIER_MATCHER.reset(
                                            stylesheetParameters)
                                                .replaceFirst(pidSuffix);
            }
            if (indexFulltextVisibilities == null 
                || indexFulltextVisibilities.equals("")) {
                stylesheetParameters =
                    Constants.INDEX_FULLTEXT_VISIBILITIES_TOTAL_MATCHER.reset(
                                            stylesheetParameters)
                                                .replaceFirst("");
            } else {
                stylesheetParameters =
                    Constants.INDEX_FULLTEXT_VISIBILITIES_MATCHER.reset(
                                            stylesheetParameters)
                                                .replaceFirst(
                                                        URLEncoder.encode(
                                                                indexFulltextVisibilities, 
                                                                XmlUtility.CHARACTER_ENCODING));
            }
            updateIndexParams = updateIndexParams + stylesheetParameters;

            connectionUtility.setTimeout(Constants.REQUEST_TIMEOUT);
            if (log.isDebugEnabled()) {
                log.debug("requesting " + updateIndexParams 
                                + " from " + gsearchUrl);
                time = System.currentTimeMillis();
            }
            
            String response = connectionUtility.getRequestURLAsString(
                                    new URL(gsearchUrl + updateIndexParams));
            if (log.isDebugEnabled()) {
                log.debug("request needed " 
                        + (System.currentTimeMillis() - time) 
                        + " ms, response: " + response);
            }

            // Catch Exceptions
            handleGsearchException(
                    index, updateIndexParams, response, 0);
            
            //Optimize Index?
            //MIH: dont check optimize, do it with cronjob
            //checkOptimize(response, index);

            return response;
        }
        catch (IOException e) {
            log
                .error("error while indexing resource " + resource
                    + ", waited " + (System.currentTimeMillis() - time)
                    + " ms " + e.getMessage());
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
        catch (WebserverSystemException e) {
            log
                .error("error while indexing resource " + resource
                    + ", waited " + (System.currentTimeMillis() - time)
                    + " ms " + e.getMessage(), e);
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
    }

    /**
     * requests deletion of one index-entry by calling fedoragsearch-servlet.
     * 
     * <pre>
     *        execute get-request with hardcoded index + repositoryname
     *        to fedoragsearch.
     * </pre>
     * 
     * @param resource
     *            String resource.
     * @param index
     *            String name of the index.
     * @param pidSuffix
     *            PidSuffix for latestVersion, latestRelease
     *            if both is in index.
     * 
     * @return String response
     * 
     * @throws ApplicationServerSystemException
     *             e
     */
    public String requestDeletion(
        String resource, String index, final String pidSuffix)
        throws ApplicationServerSystemException {
        if (index == null) {
            index = "";
        }
        if (pidSuffix != null) {
            resource = resource + ":" + pidSuffix;
        }
        String deleteIndexParams = 
            Constants.INDEX_NAME_MATCHER.reset(
                    Constants.GSEARCH_DELETE_INDEX_PARAMS)
                                                .replaceFirst(index);
        deleteIndexParams = 
            Constants.VALUE_MATCHER.reset(deleteIndexParams)
             .replaceFirst(XmlUtility.getObjidWithoutVersion(
                                 XmlUtility.getIdFromURI(resource)));
        
        try {
            String gsearchUrl =
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.GSEARCH_URL);
            connectionUtility.setTimeout(Constants.REQUEST_TIMEOUT);
            if (log.isDebugEnabled()) {
                log.debug("requesting " + deleteIndexParams 
                                + " from " + gsearchUrl);
            }
            long time = System.currentTimeMillis();
            String response = connectionUtility.getRequestURLAsString(
                                    new URL(gsearchUrl + deleteIndexParams));
            if (log.isDebugEnabled()) {
                log.debug("request needed " 
                        + (System.currentTimeMillis() - time) 
                        + " ms, response: " + response);
            }

            // Catch Exceptions
            handleGsearchException(
                    index, deleteIndexParams, response, 0);

            //Optimize Index?
            //MIH: dont check optimize, do it with cronjob
            //checkOptimize(response, index);
            return response;
        }
        catch (Exception e) {
            log.error(e);
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
    }

    /**
     * requests new empty creation of index by calling fedoragsearch-servlet.
     * 
     * <pre>
     *        execute get-request with hardcoded index + repositoryname
     *        to fedoragsearch.
     * </pre>
     * 
     * @param index
     *            String name of the index.
     * 
     * @return String response
     * 
     * @throws ApplicationServerSystemException
     *             e
     */
    public String requestCreateEmpty(String index)
        throws ApplicationServerSystemException {
        if (index == null) {
            index = "";
        }
        String createEmptyParams = 
            Constants.INDEX_NAME_MATCHER.reset(
                    Constants.GSEARCH_CREATE_EMPTY_INDEX_PARAMS)
                                                .replaceFirst(index);
        try {
            String gsearchUrl =
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.GSEARCH_URL);
            connectionUtility.setTimeout(Constants.REQUEST_TIMEOUT);
            String response = connectionUtility.getRequestURLAsString(
                                    new URL(gsearchUrl + createEmptyParams));
            // Catch Exceptions
            if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                if (Constants.LOCK_OBTAIN_TIMEOUT_MATCHER
                        .reset(response).matches()) {
                    deleteLock(response);
                    response = connectionUtility.getRequestURLAsString(
                            new URL(gsearchUrl + createEmptyParams));
                    if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                        deleteIndexDirs();
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("requesting " + createEmptyParams 
                                        + " from " + gsearchUrl);
                    }
                    long time = System.currentTimeMillis();
                    response = connectionUtility.getRequestURLAsString(
                            new URL(gsearchUrl + createEmptyParams));
                    if (log.isDebugEnabled()) {
                        log.debug("request needed " 
                                + (System.currentTimeMillis() - time) 
                                + " ms, response: " + response);
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
        catch (Exception e) {
            log.error(e);
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
    }

    /**
     * requests optimization of 
     * the given index by calling fedoragsearch-servlet.
     * 
     * <pre>
     *        execute get-request with hardcoded index
     *        to fedoragsearch.
     * </pre>
     * 
     * @param index
     *            String name of the index.
     * 
     * @return String response
     * 
     * @throws ApplicationServerSystemException
     *             e
     */
    public String requestOptimize(String index)
        throws ApplicationServerSystemException {
        if (index == null) {
            index = "";
        }
        String optimizeIndexParams = 
            Constants.INDEX_NAME_MATCHER.reset(
                    Constants.GSEARCH_OPTIMIZE_INDEX_PARAMS)
                                                .replaceFirst(index);
        try {
            String gsearchUrl =
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.GSEARCH_URL);
            connectionUtility.setTimeout(Constants.REQUEST_TIMEOUT);
            if (log.isDebugEnabled()) {
                log.debug("requesting " + optimizeIndexParams 
                                + " from " + gsearchUrl);
            }
            long time = System.currentTimeMillis();
            String response = connectionUtility.getRequestURLAsString(
                            new URL(gsearchUrl + optimizeIndexParams));
            if (log.isDebugEnabled()) {
                log.debug("request needed " 
                        + (System.currentTimeMillis() - time) 
                        + " ms, response: " + response);
            }
            // Catch Exceptions
            if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                throw new Exception(response);
            }
            return response;
        }
        catch (Exception e) {
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
    }

    /**
     * requests available index-configurations from fedoragsearch.
     * 
     * <pre>
     *        execute get-request to fedoragsearch.
     * </pre>
     * 
     * @return HashMap<String, HashMap<String, String>> index-configurations
     * 
     * @throws ApplicationServerSystemException
     *             e
     */
    private Map<String, Map<String, String>> requestIndexConfiguration()
        throws ApplicationServerSystemException {
        try {
            String gsearchUrl =
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.GSEARCH_URL);
            connectionUtility.setTimeout(Constants.REQUEST_TIMEOUT);
            String response = connectionUtility.getRequestURLAsString(
                    new URL(gsearchUrl 
                            + Constants.GSEARCH_GET_INDEX_CONFIGURATION_PARAMS));
            // Catch Exceptions
            if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                throw new Exception(response);
            }
            StaxParser sp = new StaxParser();
            GsearchIndexConfigurationHandler handler =
                        new GsearchIndexConfigurationHandler();
            sp.addHandler(handler);

            sp.parse(new ByteArrayInputStream(response
                .getBytes(XmlUtility.CHARACTER_ENCODING)));
            return handler.getGsearchIndexConfiguration();
        }
        catch (Exception e) {
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
    }

    /**
     * requests information about repository.
     * 
     * <pre>
     *        execute get-request to fedoragsearch.
     * </pre>
     * 
     * @return HashMap<String, String> repository-info
     * 
     * @throws ApplicationServerSystemException
     *             e
     */
    private Map<String, String> requestRepositoryInfo()
        throws ApplicationServerSystemException {
        try {
            String gsearchUrl =
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.GSEARCH_URL);
            connectionUtility.setTimeout(Constants.REQUEST_TIMEOUT);
            String response = connectionUtility.getRequestURLAsString(
                    new URL(gsearchUrl
                    + Constants.GSEARCH_GET_REPOSITORY_INFO_PARAMS));
            // Catch Exceptions
            if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                throw new Exception(response);
            }
            StaxParser sp = new StaxParser();
            GsearchRepositoryInfoHandler handler =
                        new GsearchRepositoryInfoHandler();
            sp.addHandler(handler);

            sp.parse(new ByteArrayInputStream(response
                .getBytes(XmlUtility.CHARACTER_ENCODING)));
            return handler.getGsearchRepositoryInfo();
        }
        catch (Exception e) {
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
    }

    /**
     * checks if index has to get optimized
     * (if mod(docCount/Constants.OPTIMIZE_DOCUMENT_COUNT) == 0,
     * index has to get optimized.
     * exctracts the docCount out of the given xml-String
     * docCount is the number of docs in the index.
     * 
     * @param response xml returned by request to fedoragsearch.
     * @param index name of the index to check/optimize.
     */
    public void checkOptimize(final String response, final String index) {
        int docCount = 1;
        String docCountStr = "";
        if (Constants.DOC_COUNT_MATCHER.reset(response).matches()) {
            docCountStr = Constants.DOC_COUNT_MATCHER.group(1);
        }
        try {
            docCount = Integer.parseInt(docCountStr);
        } catch (Exception e){}
        if (docCount 
                % Constants.OPTIMIZE_DOCUMENT_COUNT == 0) {
            try {
                requestOptimize(index);
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    /**
     * @return the indexConfigurations
     * @throws ApplicationServerSystemException e
     */
    public Map<String, Map<String, String>> getIndexConfigurations() 
                                    throws ApplicationServerSystemException {
        if (indexConfigurations == null) {
            indexConfigurations = requestIndexConfiguration();
        }
        return indexConfigurations;
    }

    /**
     * @return the repositoryInfo
     * @throws ApplicationServerSystemException e
     */
    public Map<String, String> getRepositoryInfo() 
                                    throws ApplicationServerSystemException {
        if (repositoryInfo == null) {
            repositoryInfo = requestRepositoryInfo();
        }
        return repositoryInfo;
    }

    /**
     * @return the supportedMimeTypes
     * 
     * @throws ApplicationServerSystemException e
     */
    public Set<String> getSupportedMimeTypes() 
                throws ApplicationServerSystemException {
        if (supportedMimeTypes == null) {
            supportedMimeTypes = new HashSet<String>();
            getRepositoryInfo();
            if (repositoryInfo.get("SupportedMimeTypes") != null) {
                String[] supportedMimeTypesArr = 
                    repositoryInfo.get("SupportedMimeTypes").split("\\s");
                supportedMimeTypes.addAll(Arrays.asList(supportedMimeTypesArr));
            }
        }
        return supportedMimeTypes;
    }
    
    /**
     * Check if gsearch returned an Exception.
     * If it is an Exception and saying that 
     * an index-directory does not exist 
     * or does not have segment-files,
     * create this index-directory with segment-files.
     * Otherwise throw Exception.
     * 
     * @param index name of the index
     * @param request request that was send to gsearch
     * @param response response thatr was returned by gsearch
     * @param retries numer of retries already executed
     * 
     * @throws Exception e
     * 
     * @sb
     */
    private void handleGsearchException(
            final String index, 
            final String request, 
            String response, 
            int retries) throws ApplicationServerSystemException {
        try {
            if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                String myIndex = index;
                // If index-directory does not exist yet
                // (first time indexer runs)
                // create empty index directory and then recall
                // gsearch
                if (Constants.NO_INDEX_DIR_MATCHER.reset(response).matches()) {
                    String gsearchUrl =
                        EscidocConfiguration.getInstance().get(
                            EscidocConfiguration.GSEARCH_URL);
                    if (StringUtils.isEmpty(myIndex)) {
                        if (!Constants.NO_INDEX_DIR_INDEX_NAME_MATCHER
                            .reset(response).matches()) {
                            if (log.isDebugEnabled()) {
                                log.debug(
                                    "handleGsearchException is throwing Exception1");
                            }
                            throw new ApplicationServerSystemException(response);
                        }
                        myIndex = 
                            Constants.NO_INDEX_DIR_INDEX_NAME_MATCHER.group(1);
                        if (StringUtils.isEmpty(myIndex)) {
                            if (log.isDebugEnabled()) {
                                log.debug(
                                    "handleGsearchException is throwing Exception2");
                            }
                            throw new ApplicationServerSystemException(response);
                        }
                    }
                    String createEmptyParams = 
                        Constants.INDEX_NAME_MATCHER.reset(
                                Constants.GSEARCH_CREATE_EMPTY_INDEX_PARAMS)
                                                        .replaceFirst(myIndex);
                    if (log.isDebugEnabled()) {
                        log.debug("creating empty index");
                    }
                    response = connectionUtility.getRequestURLAsString(
                            new URL(gsearchUrl + createEmptyParams));
                    if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                        if (log.isDebugEnabled()) {
                            log.debug(
                                "handleGsearchException is throwing Exception3");
                        }
                        throw new ApplicationServerSystemException(response);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("retrying request " + request);
                    }
                    response = connectionUtility.getRequestURLAsString(
                            new URL(gsearchUrl + request));
                    if (Constants.EXCEPTION_MATCHER.reset(response).matches()) {
                        if (retries < MAX_ERROR_RETRIES) {
                            retries++;
                            handleGsearchException(
                                index, request, response, retries);
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug(
                                    "handleGsearchException is throwing Exception4");
                            }
                            throw new ApplicationServerSystemException(response);
                        }
                    }
                }
                else {
                    if (log.isDebugEnabled()) {
                        log.debug(
                            "handleGsearchException is throwing Exception5");
                    }
                    throw new ApplicationServerSystemException(response);
                }
            }
        } catch (IOException e) {
            throw new ApplicationServerSystemException(e.getMessage(), e);
        } catch (WebserverSystemException e) {
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
    }
    
    /**
     * Delete Index-Lock.
     * 
     * @param response Errormessage that 
     * contains the path to the lockfile
     * 
     * @sb
     */
    private void deleteLock(final String response) {
        try {
            String lockfilePath =
                response.replaceFirst("(?s).*Lock@", "");
            lockfilePath = lockfilePath.replaceFirst("(?s)<.*", "");
            File file = new File(lockfilePath);
            file.delete();
        }
        catch (Exception e) {
            log.error(e);
        }
    }
    
    /**
     * Delete Directories with Indexes.
     * 
     * @sb
     */
    private void deleteIndexDirs() {
        try {
            String jbossDataDirPath = 
                System.getProperty("jboss.server.data.dir");
            String indexRootDirPath = jbossDataDirPath + "/index/lucene";
            File indexRootDir = new File(indexRootDirPath);
            String[] indexes = indexRootDir.list();
            for (String indexe : indexes) {
                File indexDir = new File(indexRootDir, indexe);
                deleteDir(indexDir);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * Delete Directory with Indexes.
     * 
     * @param path path to Directory
     * @return boolean if delete was successful
     * 
     * @sb
     */
    public boolean deleteDir(final File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else {
                    file.delete();
                }
            }
          }
          return (path.delete());
    }
    
    /**
     * See Interface for functional description.
     * 
     * @param connectionUtility
     *            The HTTP connection utility.
     * 
     * @spring.property ref="escidoc.core.common.util.service.ConnectionUtility"
     */
    public void setConnectionUtility(final ConnectionUtility connectionUtility) {

        this.connectionUtility = connectionUtility;
    }

}
