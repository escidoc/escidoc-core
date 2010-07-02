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
package de.escidoc.core.common.util.security.mq;

import org.jboss.ejb.plugins.jms.JMSContainerInvoker;
import org.jboss.metadata.MessageDrivenMetaData;

/**
 * @author MIH
 * 
 * Custom JMSContainerInvoker, overwrites getMetaData-Method 
 * to retrieve user and password from escidoc-core.properties.
 * 
 * @common
 */
public class EscidocJmsContainerInvoker extends JMSContainerInvoker {
    
    /**
     * Get config from container, put data into 
     * EscidocMessageDrivenMetaData-object.
     * EscidocMessageDrivenMetaData retrieves
     * user and password from escidoc-core.properties.
     * 
     * @return MessageDrivenMetaData metadata
     * 
     * @common
     */
    @Override
    public MessageDrivenMetaData getMetaData() {
        MessageDrivenMetaData config = (MessageDrivenMetaData) container
                .getBeanMetaData();
        EscidocMessageDrivenMetaData escidocConfig = 
            new EscidocMessageDrivenMetaData(
                    config.getApplicationMetaData());
        escidocConfig.setAcknowledgeMode(acknowledgeMode);
        escidocConfig.setActivationConfigProperties(
                config.getActivationConfigProperties());
        escidocConfig.setClientId(config.getClientId());
        escidocConfig.setDestinationJndiName(
                config.getDestinationJndiName());
        escidocConfig.setDestinationLink(config.getDestinationLink());
        escidocConfig.setDestinationType(config.getDestinationType());
        escidocConfig.setMessageSelector(config.getMessageSelector());
        escidocConfig.setMessagingType(config.getMessagingType());
        escidocConfig.setMethodTransactionType(
                config.getMethodTransactionType());
        escidocConfig.setResourceAdapterName(
                config.getResourceAdapterName());
        escidocConfig.setSubscriptionDurability(
                config.getSubscriptionDurability());
        escidocConfig.setSubscriptionId(config.getSubscriptionId());
        return escidocConfig;
    }
}
