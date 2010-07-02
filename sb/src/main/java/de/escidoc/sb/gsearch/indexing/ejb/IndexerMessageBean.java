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
package de.escidoc.sb.gsearch.indexing.ejb;

import java.util.HashMap;

import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;

import de.escidoc.core.adm.service.interfaces.AdminHandlerInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.indexing.IndexingHandler;
import de.escidoc.core.common.business.queue.errorprocessing.ErrorMessageHandler;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.UserContext;

/**
 * Handles requests to fedoragsearch. Creates, updates or deletes index-entries
 * in all indexes. Decides on the languages to index and puts messages in
 * Language-dependant message queues.
 * 
 * @ejb.bean name="IndexerMessageBean" display-name="IndexerMessageBean"
 *           description="Handles Indexer Messages"
 *           destination-type="javax.jms.Queue"
 *           acknowledge-mode="Auto-acknowledge"
 * @ejb.security-identity run-as="Administrator"
 * @ejb.transaction type="NotSupported"
 * @jboss.destination-jndi-name name="queue/IndexerMessageQueue"
 * @jboss.container-configuration name="Indexing Message Driven Bean"
 * 
 * @sb
 */
public class IndexerMessageBean implements MessageDrivenBean, MessageListener {

    private static final long serialVersionUID = -7726517198787388795L;

    private ErrorMessageHandler errorMessageHandler = null;

    private IndexingHandler indexingHandler = null;

    private AdminHandlerInterface adminHandler = null;

    private static AppLogger log =
        new AppLogger(IndexerMessageBean.class.getName());

    /**
     * Initializes ejb. Initializes springBeans.
     * 
     * @sb
     */
    public void ejbCreate() {
        try {
            indexingHandler =
                (IndexingHandler) BeanLocator
                    .getBean(BeanLocator.COMMON_FACTORY_ID,
                        "common.business.indexing.IndexingHandler");

            errorMessageHandler =
                (ErrorMessageHandler) BeanLocator.getBean(
                        BeanLocator.COMMON_FACTORY_ID, 
                            "common.ErrorMessageHandler");
            
            adminHandler = 
                (AdminHandlerInterface) BeanLocator.getBean(
                        BeanLocator.COMMON_FACTORY_ID, 
                            "service.AdminHandlerBean");

        }
        catch (Exception e) {
            log.error("ejbCreate(): Exception IndexerMessageBean: ", e);
        }
        if (log.isDebugEnabled()) {
            log.debug("IndexerMessageBean created");
        }
    }

    /**
     * Default-constructor.
     * 
     * @sb
     */
    public IndexerMessageBean() {
        super();

    }

    /**
     * setMessageDrivenContext.
     * 
     * @param messageDrivenContext
     *            MessageDrivenContext messageDrivenContext
     * @sb
     */
    public void setMessageDrivenContext(
        final MessageDrivenContext messageDrivenContext) {

    }

    /**
     * ejbRemove.
     * 
     * @sb
     */
    public void ejbRemove() {

    }

    /**
     * Gets resource from message-queue, decides on the languages to index and
     * put messages in Language-dependant message queues.
     * 
     * <pre>
     *        Get resource-url
     *        Get languages of resource
     *        put messages in appropriate message-queues.
     * </pre>
     * 
     * @param msg
     *            Message msg.
     * @sb
     */
    public void onMessage(final Message msg) {
        String resource = null;
        String objectType = null;
        String action = null;
        String indexName = null;
        try {
            //Get message-properties
            resource =
                msg
                    .getStringProperty(
                            de.escidoc.core.common.business
                            .Constants.INDEXER_QUEUE_RESOURCE_PARAMETER);
            objectType =
                msg
                    .getStringProperty(
                            de.escidoc.core.common.business
                            .Constants.INDEXER_QUEUE_OBJECT_TYPE_PARAMETER);
            action =
                msg
                    .getStringProperty(
                            de.escidoc.core.common.business
                            .Constants.INDEXER_QUEUE_ACTION_PARAMETER);
            indexName =
                msg
                    .getStringProperty(
                            de.escidoc.core.common.business
                            .Constants.INDEXER_QUEUE_PARAMETER_INDEX_NAME);
            boolean isReindexerCaller = msg.getBooleanProperty(
                    de.escidoc.core.common.business
                    .Constants.INDEXER_QUEUE_REINDEXER_CALLER);

            if (log.isDebugEnabled()) {
                log.debug("got message. resource: " 
                            + resource 
                            + ", objectType: " + objectType 
                            + ", action: " + action 
                            + ", indexName: " + indexName 
                            + ",isReindexerCaller: " 
                            + isReindexerCaller);
            }
            //If message was put in queue from outside, no UserContext is set
            try {
                boolean isInternalUser = UserContext.isInternalUser();

                if (!isInternalUser) {
                    UserContext.setUserContext("");
                    UserContext.runAsInternalUser();
                }
            }
            catch (Exception e) {
                UserContext.setUserContext("");
                UserContext.runAsInternalUser();
            }

            if (isReindexerCaller) {
                adminHandler.decreaseReindexStatus(objectType);
            }

            boolean allIndexes = ((indexName == null)
                || (indexName.trim().length() == 0)
                || (indexName.equalsIgnoreCase("all")));

            if (allIndexes) {
                indexingHandler.doIndexing(resource, objectType, action, true,
                    null);
            }
            else {
                indexingHandler.doIndexing(resource, objectType,
                    indexName, action, true, null);
            }

            //If reindexer wrote in queue, also index synchronous indexes
            if (isReindexerCaller) {
                if (allIndexes) {
                    indexingHandler.doIndexing(resource, objectType, action,
                        false, null);
                }
                else {
                    indexingHandler.doIndexing(resource, objectType,
                        indexName, action, false, null);
                }
            }
        }
        catch (Exception e) {
            final String message = 
                action + " of resource " 
                + resource + " failed";
            errorMessageHandler.putErrorMessage(
                    new HashMap<String, String>() {

                        private static final long serialVersionUID =
                            -741272987757132467L;

                    {
                        put("message", message); } }
                    , e,
             Constants.INDEXING_ERROR_LOGFILE);
            log.error(message);
            throw new RuntimeException();
        }
    }

}
