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
package de.escidoc.core.common.business.queue;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.ApplicationServerSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;

/*******************************************************************************
 * @author MIH
 * 
 * @spring.bean id = "common.IndexerQueueHandler" lazy-init="true"
 *              destroy-method="dispose"
 * @common
 */
public class IndexerQueueHandler {

    private Queue indexerMessageQueue = null;

    private QueueConnectionFactory queueConnectionFactory = null;

    private static AppLogger log = new AppLogger(IndexerQueueHandler.class
            .getName());

    private Session session;

    private Connection connection;

    private MessageProducer indexerMessageProducer;

    private final int numRetries = 3;

    /**
     * Closes (queue) session and (queue) connection.
     * 
     * @common
     */
    public void dispose() {

        if (indexerMessageProducer != null) {
            try {
                indexerMessageProducer.close();
            } catch (Exception e1) {
                log.error(e1);
            }
            indexerMessageProducer = null;
        }
        if (session != null) {
            try {
                session.close();
            } catch (Exception e1) {
                log.error(e1);
            }
            session = null;
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e1) {
                log.error(e1);
            }
            connection = null;
        }
    }

    /**
     * Puts a message into the indexer-queue. Property-name is resource, values
     * is given string.<br>
     * This method delegates to putMessage(String, String).
     * 
     * @param resource
     *            String message
     * @param resourceName
     *            String name of the resource (Item, Container...)
     * @param xml
     *            object-representation in xml.
     * @throws ApplicationServerSystemException
     *             e
     * @common
     */
    public void putUpdateMessage(final String resource,
            final String resourceName, final String xml)
            throws ApplicationServerSystemException {

        putMessage(resource,
                Constants.INDEXER_QUEUE_ACTION_PARAMETER_UPDATE_VALUE,
                resourceName, xml);
    }

    /**
     * Puts a message into the indexer-queue. Property-name is resource, values
     * is given string.
     * 
     * @param resource
     *            String message
     * @param resourceName
     *            String name of the resource (Item, Container...)
     * @param xml
     *            object-representation in xml.
     * @throws ApplicationServerSystemException
     *             e
     * @common
     */
    public void putDeleteMessage(final String resource,
            final String resourceName, final String xml)
            throws ApplicationServerSystemException {

        putMessage(resource,
                Constants.INDEXER_QUEUE_ACTION_PARAMETER_DELETE_VALUE,
                resourceName, xml);
    }

    /**
     * Puts a message into the indexer-queue.
     * 
     * @param resource
     *            String message. Sent in parameter
     *            INDEXER_QUEUE_RESOURCE_PARAMETER.
     * @param resourceName
     *            String name of the resource (Item, Container...)
     * @param indexerQueueActionParameter
     *            The action parameter to send to indexer. Sent in parameter
     *            INDEXER_QUEUE_ACTION_PARAMETER.
     * @param xml
     *            object-representation in xml.
     * @throws ApplicationServerSystemException
     *             e
     * @common
     */
    public synchronized void putMessage(final String resource,
            final String resourceName,
            final String indexerQueueActionParameter, final String xml)
            throws ApplicationServerSystemException {

        try {
            // Send message to Queue///////////////////////////////////////////
            final ObjectMessage message = session.createObjectMessage();
            message.setStringProperty(Constants.INDEXER_QUEUE_ACTION_PARAMETER,
                    indexerQueueActionParameter);
            message.setStringProperty(
                    Constants.INDEXER_QUEUE_RESOURCE_PARAMETER, resource);
            message
                    .setStringProperty(
                            Constants.INDEXER_QUEUE_OBJECT_TYPE_PARAMETER,
                            resourceName);
            indexerMessageProducer.send(message);
            // ////////////////////////////////////////////////////////////////
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new ApplicationServerSystemException(e.getMessage(), e);
        }
    }

    /**
     * For the Spring wiring.
     * 
     * @return IndexerMessageQueue the interface
     * @common
     */
    public Queue getIndexerMessageQueue() {
        return indexerMessageQueue;
    }

    /**
     * @spring.property ref="sb.remote.indexerMessageQueue"
     * @param indexerMessageQueue
     *            Queue
     * @common
     */
    public void setIndexerMessageQueue(final Queue indexerMessageQueue) {
        this.indexerMessageQueue = indexerMessageQueue;
    }

    /**
     * For the Spring wiring.
     * 
     * @return queueConnectionFactory the interface
     * @common
     */
    public QueueConnectionFactory getQueueConnectionFactory() {
        return queueConnectionFactory;
    }

    /**
     * Injects the queue connection factory spring bean.<br>
     * This method should never be directly called in business logic.
     * 
     * @spring.property ref="sb.remote.QueueConnectionFactory"
     * @param queueConnectionFactory
     *            QueueConnectionFactory
     * @common
     */
    public void setQueueConnectionFactory(
            final QueueConnectionFactory queueConnectionFactory) {
        this.queueConnectionFactory = queueConnectionFactory;
        initProducer();
    }

    /**
     * Creates connection, session and producer.
     * 
     * @return boolean success
     * 
     * @common
     */
    private boolean initProducer() {
        try {
            String queueConnectionUser = "";
            String queueConnectionPassword = "";
            try {
                queueConnectionUser = EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.ESCIDOC_CORE_QUEUE_USER);
                queueConnectionPassword = EscidocConfiguration.getInstance()
                        .get(EscidocConfiguration.ESCIDOC_CORE_QUEUE_PASSWORD);
            } catch (IOException e) {
                log.error(e);
            }
            connection = queueConnectionFactory.createConnection(
                    queueConnectionUser, queueConnectionPassword);
            connection.setExceptionListener(new EscidocExceptionListener());
            session = connection.createSession(false,
                    javax.jms.Session.AUTO_ACKNOWLEDGE);
            indexerMessageProducer = session
                    .createProducer(indexerMessageQueue);
            return true;
        } catch (JMSException e) {
            log.error("Error creating (queue) connection or session.", e);
            dispose();
        }
        return false;
    }

    /**
     * @author MIH
     * 
     * Class handles Exceptions of JMSConnection.
     * 
     */
    private class EscidocExceptionListener implements ExceptionListener {
        /**
         * Handles Exceptions of JMSConnection.
         * 
         * @param e JMSException
         * 
         */
        public void onException(final JMSException e) {
            for (int i = 0; i < numRetries; i++) {
                try {
                    connection.close(); // unregisters the ExceptionListener
                } catch (Exception e2) {
                }

                boolean setupOK = initProducer();

                if (setupOK) {
                    return;
                }
            }

            log.error("Cannot re-establish queue connection, giving up ...");
        }
    }
}
