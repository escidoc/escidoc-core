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

import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;

import de.escidoc.core.adm.business.Constants;
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.UserContext;

/**
 * Deletes objects from Fedora.
 *
 * @ejb.bean name="PurgeMessageBean" display-name="PurgeMessageBean"
 *           description="Handles Purge Messages"
 *           destination-type="javax.jms.Queue"
 *           acknowledge-mode="Auto-acknowledge"
 * @ejb.security-identity run-as="Administrator"
 * @ejb.transaction type="NotSupported"
 * @jboss.destination-jndi-name name="queue/PurgeMessageQueue"
 * @jboss.container-configuration name="Purge Message Driven Bean"
 * @spring.bean id="business.PurgeMessageBean"
 *
 * @author sche
 */
public class PurgeMessageBean implements MessageDrivenBean, MessageListener {
    /**
     * Unique identifier for this class.
     */
    private static final long serialVersionUID = 1776938217132296521L;

    /**
     * Logging goes there.
     */
    private static AppLogger logger =
        new AppLogger(PurgeMessageBean.class.getName());

    private FedoraUtility fedoraUtility = null;

    /**
     * ejbCreate.
     */
    public void ejbCreate() {
        try {
            fedoraUtility = (FedoraUtility) BeanLocator.getBean(
                BeanLocator.COMMON_FACTORY_ID,
                "escidoc.core.business.FedoraUtility");
        }
        catch (WebserverSystemException e) {
            logger.error("could not localize bean", e);
        }
    }

    /**
     * ejbRemove.
     */
    public void ejbRemove() {
    }

    /**
     * Gets resource id from message-queue and deletes the resource from Fedora.
     *
     * @param msg Message msg.
     */
    public void onMessage(final Message msg) {
        if (msg != null) {
            try {
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

                String id = msg.getStringProperty(Constants.QUEUE_ID);

                for (String componentId : TripleStoreUtility.getInstance()
                    .getComponents(id)) {
                    fedoraUtility.deleteObject(componentId, false);
                }
                fedoraUtility.deleteObject(id, false);
                // synchronize triple store
                fedoraUtility.sync();

            }
            catch (Exception e) {
                logger.error("could not dequeue message", e);
            }
            finally {
                PurgeStatus.getInstance().dec();
            }
        }
    }

    /**
     * setMessageDrivenContext.
     *
     * @param messageDrivenContext context
     */
    public void setMessageDrivenContext(
        final MessageDrivenContext messageDrivenContext) {
    }
}
