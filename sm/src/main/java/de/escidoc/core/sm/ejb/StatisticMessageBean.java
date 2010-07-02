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
package de.escidoc.core.sm.ejb;

import java.util.HashMap;

import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.queue.errorprocessing.ErrorMessageHandler;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.sm.business.interfaces.StatisticDataHandlerInterface;

/**
 * Takes 'raw' statistic-data-records from the create-method of the
 * StatisticData-Interface and writes them into the database-table.
 * 
 * @ejb.bean name="StatisticMessageBean" display-name="StatisticMessageBean"
 *           description="Handles Statistic Messages"
 *           destination-type="javax.jms.Queue"
 *           acknowledge-mode="Auto-acknowledge"
 * @jboss.destination-jndi-name name="queue/StatisticMessageQueue"
 * @jboss.container-configuration name="StatisticManager Message Driven Bean"
 * 
 * @sm
 */
public class StatisticMessageBean implements MessageDrivenBean, MessageListener {

    private static AppLogger log =
        new AppLogger(StatisticMessageBean.class.getName());

    private ErrorMessageHandler errorMessageHandler = null;

    private StatisticDataHandlerInterface handler = null;

    /**
     * Initializes ejb + spring beans.
     */
    public void ejbCreate() {

    }

    /**
     * Default-constructor.
     */
    public StatisticMessageBean() {
        super();

    }

    /**
     * setMessageDrivenContext.
     * 
     * @param messageDrivenContext
     *            MessageDrivenContext messageDrivenContext
     */
    public void setMessageDrivenContext(
        final MessageDrivenContext messageDrivenContext) {

    }

    /**
     * ejbRemove.
     * 
     * @sm
     */
    public void ejbRemove() {

    }

    private StatisticDataHandlerInterface getStatisticDataHandler() {
        if (this.handler == null) {
            try {
                handler =
                    (StatisticDataHandlerInterface) BeanLocator.getBean(
                        BeanLocator.SM_FACTORY_ID,
                        "business.StatisticDataHandler");
            }
            catch (Exception e) {
                log.error("ejbCreate(): Exception StatisticMessageBean: " + e);
            }
        }
        return this.handler;
    }

    private ErrorMessageHandler getErrorMessageHandler() {

        if (this.errorMessageHandler == null) {
            try {
                errorMessageHandler =
                    (ErrorMessageHandler) BeanLocator.getBean(
                        BeanLocator.COMMON_FACTORY_ID,
                        "common.ErrorMessageHandler");
            }
            catch (Exception e) {
                log.error("ejbCreate(): Exception StatisticMessageBean: " + e);
            }
        }
        return this.errorMessageHandler;
    }

    /**
     * Gets resource from message-queue, takes xmlString and calls Method which
     * inserts xml into database.
     * 
     * <pre>
     *        Get xml
     *        call method which inserts xml into database.
     * </pre>
     * 
     * @param msg
     *            Message msg.
     * @sm
     */
    public void onMessage(final Message msg) {

        try {
            getStatisticDataHandler().insertStatisticData(((TextMessage) msg).getText());
        }
        catch (Exception e) {
            final String message = "inserting statistic-data failed";
            getErrorMessageHandler().putErrorMessage(new HashMap<String, String>() {
                {
                    put("message", message);
                }
            }, e, Constants.STATISTIC_ERROR_LOGFILE);
            log.error(e);
            throw new RuntimeException(e);
        }
    }

}
