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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.adm.business.admin;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.indexing.IndexingHandler;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.ApplicationServerSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.IOUtils;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.index.IndexRequest;
import de.escidoc.core.index.IndexRequestBuilder;
import de.escidoc.core.index.IndexService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Provides Methods used for Re-indexing.
 * 
 * @spring.bean id="admin.Reindexer"
 * 
 * @author sche
 */
public class Reindexer {
    /**
     * Triple store query to get a list of all containers.
     */
    private static final String CONTAINER_LIST_QUERY =
        "/risearch?type=triples&lang=spo&format=N-Triples&query=*%20%3chttp://"
            + "www.w3.org/1999/02/22-rdf-syntax-ns%23type%3e%20%3c"
            + ResourceType.CONTAINER.getUri() + "%3e";

    /**
     * Triple store query to get a list of all Content Models.
     */
    private static final String CONTENT_MODEL_LIST_QUERY =
        "/risearch?type=triples&lang=spo&format=N-Triples&query=*%20%3chttp://"
            + "www.w3.org/1999/02/22-rdf-syntax-ns%23type%3e%20%3c"
            + ResourceType.CONTENT_MODEL.getUri() + "%3e";

    /**
     * Triple store query to get a list of all content relations.
     */
    private static final String CONTENT_RELATION_LIST_QUERY =
        "/risearch?type=triples&lang=spo&format=N-Triples&query=*%20%3chttp://"
            + "www.w3.org/1999/02/22-rdf-syntax-ns%23type%3e%20%3c"
            + ResourceType.CONTENT_RELATION.getUri() + "%3e";

    /**
     * Triple store query to get a list of all contexts.
     */
    private static final String CONTEXT_LIST_QUERY =
        "/risearch?type=triples&lang=spo&format=N-Triples&query=*%20%3chttp://"
            + "www.w3.org/1999/02/22-rdf-syntax-ns%23type%3e%20%3c"
            + ResourceType.CONTEXT.getUri() + "%3e";

    /**
     * Triple store query to get a list of all items.
     */
    private static final String ITEM_LIST_QUERY =
        "/risearch?type=triples&lang=spo&format=N-Triples&query=*%20%3chttp://"
            + "www.w3.org/1999/02/22-rdf-syntax-ns%23type%3e%20%3c"
            + ResourceType.ITEM.getUri() + "%3e";

    /**
     * Triple store query to get a list of all organizational units.
     */
    private static final String OU_LIST_QUERY =
        "/risearch?type=triples&lang=spo&format=N-Triples&query=*%20%3chttp://"
            + "www.w3.org/1999/02/22-rdf-syntax-ns%23type%3e%20%3c"
            + ResourceType.OU.getUri() + "%3e";

    private static final AppLogger LOG = new AppLogger(
        Reindexer.class.getName());

    private FedoraUtility fedoraUtility;

    private IndexService indexService;

    private IndexingHandler indexingHandler;

    // Indexer configuration
    private Map<String, Map<String, Map<String, Object>>> objectTypeParameters;

    /**
     * Check if the given index contains objects with the given resource type.
     * 
     * @param indexName
     *            name of the index (may be null for "all indexes")
     * @param type
     *            resource type
     * 
     * @return true if the index contains objects of the given type
     */
    private boolean contains(final String indexName, final ResourceType type) {
        final boolean result;

        if (indexName == null || indexName.trim().length() == 0
            || "all".equalsIgnoreCase(indexName)) {
            result = true;
        }
        else {
            final Map<String, Map<String, Object>> resourceParameters =
                objectTypeParameters.get(type.getUri());
            if (resourceParameters == null) {
                return false;
            }

            result = resourceParameters.containsKey(indexName);
        }
        return result;
    }

    /**
     * @param clearIndex
     *            clear the index before adding objects to it
     * @param indexName
     *            name of the index (may be null for "all indexes")
     * 
     * @return total number of objects found, ...
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     */
    public String reindex(final boolean clearIndex, final String indexName)
        throws SystemException, InvalidSearchQueryException {
        if ("errorTest".equals(indexName)) {
            return testReindexError();
        }
        final StringBuilder result = new StringBuilder();
        final ReindexStatus reindexStatus = ReindexStatus.getInstance();

        if (reindexStatus.startMethod()) {
            boolean idListEmpty = true;

            try {
                // Get all Containers
                final Collection<String> containerHrefs =
                    getIds(indexName, ResourceType.CONTAINER,
                        CONTAINER_LIST_QUERY, clearIndex);

                idListEmpty &= containerHrefs.isEmpty();

                // Get all Content Models
                final Collection<String> contentModelHrefs =
                    getIds(indexName, ResourceType.CONTENT_MODEL,
                        CONTENT_MODEL_LIST_QUERY, clearIndex);

                idListEmpty &= contentModelHrefs.isEmpty();

                // Get all Content Relations
                final Collection<String> contentRelationHrefs =
                    getIds(indexName, ResourceType.CONTENT_RELATION,
                        CONTENT_RELATION_LIST_QUERY, clearIndex);

                idListEmpty &= contentRelationHrefs.isEmpty();

                // Get all Contexts
                final Collection<String> contextHrefs =
                    getIds(indexName, ResourceType.CONTEXT, CONTEXT_LIST_QUERY,
                        clearIndex);

                idListEmpty &= contextHrefs.isEmpty();

                // Get all Items
                final Collection<String> itemHrefs =
                    getIds(indexName, ResourceType.ITEM, ITEM_LIST_QUERY,
                        clearIndex);

                idListEmpty &= itemHrefs.isEmpty();

                // Get all Organizational Units
                final Collection<String> orgUnitHrefs =
                    getIds(indexName, ResourceType.OU, OU_LIST_QUERY,
                        clearIndex);

                idListEmpty &= orgUnitHrefs.isEmpty();

                if (clearIndex) {
                    // Delete indexes
                    sendDeleteIndexMessage(ResourceType.CONTAINER, indexName);
                    sendDeleteIndexMessage(ResourceType.CONTENT_MODEL,
                        indexName);
                    sendDeleteIndexMessage(ResourceType.CONTENT_RELATION,
                        indexName);
                    sendDeleteIndexMessage(ResourceType.CONTEXT, indexName);
                    sendDeleteIndexMessage(ResourceType.ITEM, indexName);
                    sendDeleteIndexMessage(ResourceType.OU, indexName);
                }

                result.append("<message>\n");
                result.append("scheduling ").append(containerHrefs.size()).append(" container(s) for reindexing\n");
                result.append("</message>\n");

                result.append("<message>\n");
                result.append("scheduling ").append(contentModelHrefs.size()).append(" content models(s) for reindexing\n");
                result.append("</message>\n");

                result.append("<message>\n");
                result.append("scheduling ").append(contentRelationHrefs.size()).append(" content relation(s) for reindexing\n");
                result.append("</message>\n");

                result.append("<message>\n");
                result.append("scheduling ").append(contextHrefs.size()).append(" context(s) for reindexing\n");
                result.append("</message>\n");

                result.append("<message>\n");
                result.append("scheduling ").append(itemHrefs.size()).append(" item(s) for reindexing\n");
                result.append("</message>\n");

                result.append("<message>\n");
                result.append("scheduling ").append(orgUnitHrefs.size()).append(" organizational-unit(s) for reindexing\n");
                result.append("</message>\n");

                // re-index Containers
                for (final String containerHref : containerHrefs) {
                    sendUpdateIndexMessage(containerHref,
                        ResourceType.CONTAINER, indexName);
                }

                // re-index Content Models
                for (final String contentModelHref : contentModelHrefs) {
                    sendUpdateIndexMessage(contentModelHref,
                        ResourceType.CONTENT_MODEL, indexName);
                }

                // re-index Content Relations
                for (final String contentRelationHref : contentRelationHrefs) {
                    sendUpdateIndexMessage(contentRelationHref,
                        ResourceType.CONTENT_RELATION, indexName);
                }

                // re-index Contexts
                for (final String contextHref : contextHrefs) {
                    sendUpdateIndexMessage(contextHref, ResourceType.CONTEXT,
                        indexName);
                }

                // re-index Items
                for (final String itemHref : itemHrefs) {
                    sendUpdateIndexMessage(itemHref, ResourceType.ITEM,
                        indexName);
                }

                // re-index Organizational Units
                for (final String orgUnitHref : orgUnitHrefs) {
                    sendUpdateIndexMessage(orgUnitHref, ResourceType.OU,
                        indexName);
                }
            }
            finally {
                if (idListEmpty) {
                    reindexStatus.finishMethod();
                }
                reindexStatus.setFillingComplete();
            }
        }
        else {
            result.append(getStatus());
        }
        return result.toString();
    }

    /**
     * @param indexName
     *            name of the index (may be null for "all indexes")
     * 
     * @return total number of objects found, ...
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     */
    public String testReindexError() throws SystemException,
        InvalidSearchQueryException {
        sendUpdateIndexMessage("nonexistingPid", ResourceType.ITEM, null);
        return "OK";
    }

    /**
     * Extract the subject from the given triple.
     * 
     * @param triple
     *            the triple from which the subject has to be extracted
     * 
     * @return the subject of the given triple
     */
    private String getSubject(final String triple) {
        String result = null;

        if (triple != null) {
            final int index = triple.indexOf(' ');

            if (index > 0) {
                result = triple.substring(triple.indexOf('/') + 1, index - 1);
            }
        }
        return result;
    }

    /**
     * @param objectType
     *            type of the resource.
     * @param indexName
     *            name of the index (may be null for "all indexes")
     * 
     * @throws ApplicationServerSystemException
     *             e
     */
    private void sendDeleteIndexMessage(
        final ResourceType objectType, final String indexName)
        throws ApplicationServerSystemException {
        try {
            final IndexRequest indexRequest =
                IndexRequestBuilder
                    .createIndexRequest()
                    .withAction(
                        Constants.INDEXER_QUEUE_ACTION_PARAMETER_CREATE_EMPTY_VALUE)
                    .withIndexName(indexName)
                    .withObjectType(objectType.getUri()).build();
            this.indexService.index(indexRequest);
        }
        catch (Exception e) {
            throw new ApplicationServerSystemException(e);
        }
    }

    /**
     * @param resource
     *            String resource.
     * 
     * @throws ApplicationServerSystemException
     *             e
     */
    public void sendDeleteObjectMessage(final String resource)
        throws ApplicationServerSystemException {
        try {
            final IndexRequest indexRequest =
                IndexRequestBuilder
                    .createIndexRequest()
                    .withAction(
                        Constants.INDEXER_QUEUE_ACTION_PARAMETER_DELETE_VALUE)
                    .withResource(resource).build();
            this.indexService.index(indexRequest);
        }
        catch (Exception e) {
            throw new ApplicationServerSystemException(e);
        }
    }

    /**
     * @param resource
     *            String resource.
     * @param objectType
     *            type of the resource.
     * @param indexName
     *            name of the index (may be null for "all indexes")
     * 
     * @throws ApplicationServerSystemException
     *             e
     */
    private void sendUpdateIndexMessage(
        final String resource, final ResourceType objectType,
        final String indexName) throws ApplicationServerSystemException {
        try {
            final IndexRequest indexRequest =
                IndexRequestBuilder
                    .createIndexRequest()
                    .withAction(
                        Constants.INDEXER_QUEUE_ACTION_PARAMETER_UPDATE_VALUE)
                    .withIndexName(indexName).withResource(resource)
                    .withObjectType(objectType.getUri())
                    .withIsReindexerCaller(true).build();
            this.indexService.index(indexRequest);
        }
        catch (Exception e) {
            throw new ApplicationServerSystemException(e);
        }
    }

    /**
     * Get a list of all available resources of the given type from Fedora.
     * 
     * @param indexName
     *            name of the index
     * @param type
     *            resource type
     * @param listQuery
     *            Fedora query to get a list of all resources of the given type
     * @param clearIndex
     *            clear the index before adding objects to it
     * 
     * @return list of resource ids
     * @throws SystemException
     *             Thrown if eSciDoc failed to receive a resource.
     */
    private Collection<String> getIds(final String indexName,
                                      final ResourceType type,
                                      final String listQuery,
                                      final boolean clearIndex) throws SystemException {
        final Collection<String> result = new LinkedList<String>();
        if (contains(indexName, type)) {
            BufferedReader input = null;
            try {
                input = new BufferedReader(new InputStreamReader(fedoraUtility.query(listQuery)));
                final ReindexStatus reindexStatus = ReindexStatus.getInstance();
                final String objectType = type.getUri();
                String line;
                while ((line = input.readLine()) != null) {
                    final String subject = getSubject(line);
                    if (subject != null) {
                        final String id = subject.substring(subject.indexOf('/') + 1);
                        if (clearIndex || !indexingHandler.exists(id, objectType, indexName)) {
                            reindexStatus.inc(type);
                            result.add(id);
                        }
                    }
                }
            } catch (IOException e) {
                throw new SystemException(e);
            } finally {
                IOUtils.closeStream(input);
            }
        }
        return result;
    }

    /**
     * Get the current status of the running/finished reindexing process.
     * 
     * @return current status (how many objects are still in the queue)
     * @throws SystemException
     *             thrown in case of an internal error
     */
    public String getStatus() throws SystemException {
        return ReindexStatus.getInstance().toString();
    }

    /**
     * @spring.property ref="common.business.indexing.IndexingHandler"
     * @param indexingHandler
     *            indexing handler
     * 
     * @throws WebserverSystemException
     *             thrown if the index configuration could not be read
     */
    public void setIndexingHandler(final IndexingHandler indexingHandler)
        throws WebserverSystemException {
        this.indexingHandler = indexingHandler;
        this.objectTypeParameters = indexingHandler.getObjectTypeParameters();
    }

    /**
     * 
     * @param indexService
     *            index service
     * @spring.property ref="de.escidoc.core.index.IndexService"
     */
    public void setIndexService(final IndexService indexService) {
        this.indexService = indexService;
    }

    /**
     * Injects the {@link FedoraUtility}.
     * 
     * @spring.property ref="escidoc.core.business.FedoraUtility"
     * 
     * @param fedoraUtility
     *            the {@link FedoraUtility} to inject.
     */
    public void setFedoraUtility(final FedoraUtility fedoraUtility) {
        this.fedoraUtility = fedoraUtility;
    }
}
