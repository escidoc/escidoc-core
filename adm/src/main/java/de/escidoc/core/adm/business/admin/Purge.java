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
 * Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.adm.business.admin;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;

import de.escidoc.core.adm.business.Constants;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;

/**
 * Provides methods used for purging objects from Fedora.
 *
 * @spring.bean id="admin.Purge"
 * @adm
 */
public class Purge {
    private MessageProducer messageProducer = null;
    private Queue messageQueue = null;
    private QueueConnection queueConnection = null;
    private QueueConnectionFactory queueConnectionFactory = null;
    private QueueSession queueSession = null;

    /**
     * Create a connection to the message queue.
     *
     * @throws IOException Thrown if some properties could not be read from
     *                     configuration.
     * @throws JMSException Thrown if the connection to the message queue could
     *                      not be established.
     */
    private void createQueueConnection() throws IOException, JMSException {
        if (queueConnection == null) {
            queueConnection = queueConnectionFactory.createQueueConnection(
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_QUEUE_USER),
                EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_QUEUE_PASSWORD));
            queueSession = queueConnection.createQueueSession(false,
                Session.AUTO_ACKNOWLEDGE);
            messageProducer = queueSession.createProducer(messageQueue);
        }
    }

    /**
     * Get the current status of the running/finished purging process.
     * 
     * @return current status (how many objects are still in the queue)
     * @throws SystemException thrown in case of an internal error
     */
    public String getStatus() throws SystemException {
        return PurgeStatus.getInstance().toString();
    }

    /**
     * Delete a resource asynchronously from Fedora.
     *
     * @param id resource id
     * @throws IOException Thrown if some properties could not be read from
     *                     configuration.
     * @throws JMSException Thrown if the connection to the message queue could
     *                      not be established.
     */
    public void sendDeleteObjectMessage(final String id)
        throws IOException, JMSException {
        createQueueConnection();

        PurgeStatus purgeStatus = PurgeStatus.getInstance();
        ObjectMessage message = queueSession.createObjectMessage();

        message.setStringProperty(Constants.QUEUE_ID, id);
        messageProducer.send(message);
        purgeStatus.inc();
    }

    /**
     * Injects the {@link Queue}.
     *
     * @spring.property ref="adm.remote.purgeMessageQueue"
     *
     * @param messageQueue message queue
     */
    public void setMessageQueue(final Queue messageQueue) {
        this.messageQueue = messageQueue;
    }

    /**
     * Injects the {@link QueueQueueConnectionFactory}.
     *
     * @spring.property ref="common.local.QueueConnectionFactory"
     *
     * @param queueConnectionFactory QueueConnectionFactory
     */
    public void setQueueConnectionFactory(
        final QueueConnectionFactory queueConnectionFactory) {
        this.queueConnectionFactory = queueConnectionFactory;
    }
}
