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

import de.escicore.index.IndexRequest;
import de.escicore.index.IndexRequestBuilder;
import de.escicore.index.IndexService;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.XmlFilter;
import de.escidoc.core.common.business.fedora.resources.interfaces.FilterInterface;
import de.escidoc.core.common.business.indexing.IndexingHandler;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.ApplicationServerSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.BeanLocator;

import javax.jms.MessageProducer;
import java.util.Collection;
import java.util.Vector;

/**
 * Provides Methods used for Re-indexing.
 * 
 * @spring.bean id="admin.Reindexer"
 * 
 * @author sche
 * @adm
 */
public class Reindexer {

    private static AppLogger log = new AppLogger(Reindexer.class.getName());

    private IndexService indexService;

    private IndexingHandler indexingHandler;

    private MessageProducer messageProducer;

    private Utility utility = null;

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
        StringBuffer result = new StringBuffer();
        ReindexStatus reindexStatus = ReindexStatus.getInstance();

        if (reindexStatus.startMethod()) {
            boolean idListEmpty = true;

            try {
                // Get all released Items
                Collection<String> itemHrefs =
                    getFilteredItems(indexName, clearIndex);
                idListEmpty &= itemHrefs.size() == 0;
                // Get all released Containers
                Collection<String> containerHrefs =
                    getFilteredContainers(indexName, clearIndex);
                idListEmpty &= containerHrefs.size() == 0;
                // Get all public viewable organizational-units
                Collection<String> orgUnitHrefs =
                    getFilteredOrganizationalUnits(indexName, clearIndex);
                idListEmpty &= orgUnitHrefs.size() == 0;

                if (clearIndex) {
                    // Delete indexes
                    sendDeleteIndexMessage(Constants.ITEM_OBJECT_TYPE,
                        indexName);
                    sendDeleteIndexMessage(Constants.CONTAINER_OBJECT_TYPE,
                        indexName);
                    sendDeleteIndexMessage(
                        Constants.ORGANIZATIONAL_UNIT_OBJECT_TYPE,
                        indexName);
                }

                result.append("<message>\n");
                result.append("scheduling " + itemHrefs.size()
                    + " item(s) for reindexing\n");
                result.append("</message>\n");

                result.append("<message>\n");
                result.append("scheduling " + containerHrefs.size()
                    + " container(s) for reindexing\n");
                result.append("</message>\n");

                result.append("<message>\n");
                result.append("scheduling " + orgUnitHrefs.size()
                    + " organizational-unit(s) for reindexing\n");
                result.append("</message>\n");

                // re-index released items
                for (String itemHref : itemHrefs) {
                    reindexStatus.inc(ResourceType.ITEM);
                    sendUpdateIndexMessage(itemHref,
                        Constants.ITEM_OBJECT_TYPE, indexName);
                }

                // re-index released containers
                for (String containerHref : containerHrefs) {
                    reindexStatus.inc(ResourceType.CONTAINER);
                    sendUpdateIndexMessage(containerHref,
                        Constants.CONTAINER_OBJECT_TYPE, indexName);
                }

                // re-index public viewable organizational-units
                for (String orgUnitHref : orgUnitHrefs) {
                    reindexStatus.inc(ResourceType.OU);
                    sendUpdateIndexMessage(orgUnitHref,
                        Constants.ORGANIZATIONAL_UNIT_OBJECT_TYPE,
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
     * Remove all ids from the given list which already exist in the given
     * index.
     * 
     * @param ids
     *            list of resource ids
     * @param objectType
     *            String name of the resource (eg Item, Container...).
     * @param indexName
     *            name of the index
     * @param clearIndex
     *            clear the index before adding objects to it
     * 
     * @return filtered list of resource ids
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     */
    private Collection<String> removeExistingIds(
        final Collection<String> ids, final String objectType,
        final String indexName, final boolean clearIndex)
        throws SystemException {
        Collection<String> result = null;

        if (clearIndex) {
            result = ids;
        }
        else {
            result = new Vector<String>();
            for (String id : ids) {
                if (!indexingHandler.exists(id, objectType, indexName)) {
                    result.add(id);
                }
            }
        }
        return result;
    }

    /**
     * Get a list of all released items.
     * 
     * @param indexName
     *            name of the index
     * @param clearIndex
     *            clear the index before adding objects to it
     * 
     * @return list of all released items
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     */
    public Collection<String> getFilteredItems(
        final String indexName, final boolean clearIndex)
        throws InvalidSearchQueryException, SystemException {
        FilterInterface filter = new XmlFilter();

        filter
            .addRestriction(TripleStoreUtility.PROP_PUBLIC_STATUS, "released");
        filter.setLimit(0);
        return removeExistingIds(BeanLocator.locateItemCache().getIds(
            getUtility().getCurrentUserId(), filter),
            Constants.ITEM_OBJECT_TYPE, indexName, clearIndex);
    }

    /**
     * Get a list of all released containers.
     * 
     * @param indexName
     *            name of the index
     * @param clearIndex
     *            clear the index before adding objects to it
     * 
     * @return list of all released containers
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     */
    public Collection<String> getFilteredContainers(
        final String indexName, final boolean clearIndex)
        throws InvalidSearchQueryException, SystemException {
        FilterInterface filter = new XmlFilter();

        filter
            .addRestriction(TripleStoreUtility.PROP_PUBLIC_STATUS, "released");
        filter.setLimit(0);
        return removeExistingIds(BeanLocator.locateContainerCache().getIds(
            getUtility().getCurrentUserId(), filter),
            Constants.CONTAINER_OBJECT_TYPE, indexName, clearIndex);
    }

    /**
     * Get a list of all opened or closed organizational units.
     * 
     * @param indexName
     *            name of the index
     * @param clearIndex
     *            clear the index before adding objects to it
     * 
     * @return list of all opened or closed organizational units
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws SystemException
     *             Thrown if a framework internal error occurs.
     */
    public Collection<String> getFilteredOrganizationalUnits(
        final String indexName, final boolean clearIndex)
        throws InvalidSearchQueryException, SystemException {
        FilterInterface filter = new XmlFilter();

        filter.addRestriction(TripleStoreUtility.PROP_PUBLIC_STATUS, "opened");
        filter.addRestriction(TripleStoreUtility.PROP_PUBLIC_STATUS, "closed");
        filter.setLimit(0);
        return removeExistingIds(BeanLocator
            .locateOrganizationalUnitCache().getIds(
                getUtility().getCurrentUserId(), filter),
            Constants.ORGANIZATIONAL_UNIT_OBJECT_TYPE, indexName,
            clearIndex);
    }

    /**
     * @return Returns the utility.
     */
    private Utility getUtility() {
        if (utility == null) {
            utility = Utility.getInstance();
        }
        return utility;
    }

    /**
     * @param objectType
     *            type of the resource.
     * @param indexName
     *            name of the index (may be null for "all indexes")
     * 
     * @throws ApplicationServerSystemException
     *             e
     * @admin
     */
    private void sendDeleteIndexMessage(
        final String objectType, final String indexName)
        throws ApplicationServerSystemException {
        try {
            IndexRequest indexRequest = IndexRequestBuilder.createIndexRequest()
                        .withAction(Constants.INDEXER_QUEUE_ACTION_PARAMETER_CREATE_EMPTY_VALUE)
                        .withIndexName(indexName)
                        .withObjectType(objectType)
                        .build();
        }
        catch (Exception e) {
            log.error(e);
            throw new ApplicationServerSystemException(e);
        }
    }

    /**
     * @param resource
     *            String resource.
     * 
     * @throws ApplicationServerSystemException
     *             e
     * @admin
     */
    public void sendDeleteObjectMessage(final String resource)
        throws ApplicationServerSystemException {
        try {
            IndexRequest indexRequest = IndexRequestBuilder.createIndexRequest()
                        .withAction(Constants.INDEXER_QUEUE_ACTION_PARAMETER_DELETE_VALUE)
                        .withResource(resource)
                        .build();
            this.indexService.index(indexRequest);
        }
        catch (Exception e) {
            log.error(e);
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
     * @admin
     */
    public void sendUpdateIndexMessage(
        final String resource, final String objectType,
        final String indexName) throws ApplicationServerSystemException {
        try {
            IndexRequest indexRequest = IndexRequestBuilder.createIndexRequest()
                        .withAction(Constants.INDEXER_QUEUE_ACTION_PARAMETER_UPDATE_VALUE)
                        .withIndexName(indexName)
                        .withResource(resource)
                        .withObjectType(objectType)
                        .build();
                this.indexService.index(indexRequest);
        }
        catch (Exception e) {
            log.error(e);
            throw new ApplicationServerSystemException(e);
        }
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
     */
    public void setIndexingHandler(final IndexingHandler indexingHandler) {
        this.indexingHandler = indexingHandler;
    }


   /**
     *
     * @param indexService
     * @spring.property ref="de.escicore.index.IndexService"
     */
    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }
}
