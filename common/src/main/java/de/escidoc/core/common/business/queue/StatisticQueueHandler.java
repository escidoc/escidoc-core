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
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;

import de.escidoc.core.common.binding.BindingHandlerInterface;
import de.escidoc.core.common.business.queue.vo.StatisticDataVo;
import de.escidoc.core.common.exceptions.system.ApplicationServerSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;

/*******************************************************************************
 * @author MIH
 * 
 * Handles Remote Messages to the statistic-queue.
 * 
 * @spring.bean id = "common.StatisticQueueHandler" lazy-init="true"
 *              destroy-method="dispose"
 * @common
 */
public class StatisticQueueHandler {

    private Queue statisticMessageQueue = null;

    private QueueConnectionFactory queueConnectionFactory = null;

    private BindingHandlerInterface bindingHandler;

    private static AppLogger log =
        new AppLogger(StatisticQueueHandler.class.getName());

    private Connection connection;

    private Session session;

    private MessageProducer statisticMessageProducer;

    private final int numRetries = 3;

    /**
     * Puts a message into the statistic-queue.
     * 
     * @param statisticDataVo
     *            statisticDataVo
     * 
     * @throws ApplicationServerSystemException
     *             e
     * 
     * @common
     */
    public void putMessage(final StatisticDataVo statisticDataVo)
        throws ApplicationServerSystemException {
        if (statisticDataVo == null) {
            throw new ApplicationServerSystemException(
                "statisticDataVo may not be null");
        }
        if (statisticDataVo.getStatisticRecord() == null) {
            throw new ApplicationServerSystemException(
                "statisticRecord may not be null");
        }

        String xml = null;
        try {
            xml = bindingHandler.marshal(statisticDataVo.getStatisticRecord());
        }
        catch (SystemException e) {
            log.error(e);
            throw new ApplicationServerSystemException(e);
        }

        putMessage(xml);
    }

    /**
     * Puts a message into the statistic-queue.
     * 
     * @param xmlData
     *            String xmlData to write to db
     * 
     * @throws ApplicationServerSystemException
     *             e
     * 
     * @sm
     */
    public synchronized void putMessage(final String xmlData)
        throws ApplicationServerSystemException {

        if (session == null) {
            throw new ApplicationServerSystemException(
                "Queue session not initialized.");
        }

        try {
            // Send message to Queue////////////////////////////////////////////
            final Message message = session.createTextMessage(xmlData);
            statisticMessageProducer.send(message);
            if (log.isDebugEnabled()) {
                log.debug("put message " + xmlData
                    + " into StatisticMessageQueue");
            }
            // /////////////////////////////////////////////////////////////////
        }
        catch (Exception e) {
            log.error(e);
            throw new ApplicationServerSystemException(e);
        }
    }

    /**
     * Closes (queue) session and (queue) connection.
     * 
     * @sm
     */
    public void dispose() {

        if (statisticMessageProducer != null) {
            try {
                statisticMessageProducer.close();
            }
            catch (Exception e1) {
                log.error(e1);
            }
            statisticMessageProducer = null;
        }
        if (session != null) {
            try {
                session.close();
            }
            catch (Exception e1) {
                log.error(e1);
            }
            session = null;
        }
        if (connection != null) {
            try {
                connection.close();
            }
            catch (Exception e1) {
                log.error(e1);
            }
            connection = null;
        }
    }

    /**
     * Setting the bindingHandler.
     * 
     * @param bindingHandler
     *            The bindingHandler to set.
     * @spring.property ref="common.binding.JaxbBindingHandler"
     */
    public final void setBindingHandler(
        final BindingHandlerInterface bindingHandler) {
        this.bindingHandler = bindingHandler;
    }

    /**
     * @spring.property ref="sm.remote.statisticMessageQueue"
     * @param statisticMessageQueue
     *            Queue
     * @common
     */
    public void setStatisticMessageQueue(final Queue statisticMessageQueue) {
        this.statisticMessageQueue = statisticMessageQueue;
    }

    /**
     * Injects the queue connection factory spring bean.<br>
     * This method should never be directly called in business logic.
     * 
     * @spring.property ref="sm.remote.QueueConnectionFactory"
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
                queueConnectionUser = 
                    EscidocConfiguration.getInstance().get(
                            EscidocConfiguration.ESCIDOC_CORE_QUEUE_USER);
                queueConnectionPassword = 
                    EscidocConfiguration.getInstance().get(
                            EscidocConfiguration.ESCIDOC_CORE_QUEUE_PASSWORD);
            } catch (IOException e) {
                log.error(e);
            }
            connection = queueConnectionFactory.createConnection(
                    queueConnectionUser, queueConnectionPassword);
            connection.setExceptionListener(
                    new EscidocExceptionListener());
            session =
                connection.createSession(false,
                    javax.jms.Session.AUTO_ACKNOWLEDGE);
            statisticMessageProducer =
                session.createProducer(statisticMessageQueue);
            return true;
        }
        catch (JMSException e) {
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
