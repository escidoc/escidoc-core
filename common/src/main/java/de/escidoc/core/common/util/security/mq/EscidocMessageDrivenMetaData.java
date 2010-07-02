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

import java.util.HashMap;

import org.jboss.metadata.ApplicationMetaData;
import org.jboss.metadata.MessageDrivenMetaData;

import de.escidoc.core.common.util.configuration.EscidocConfiguration;

/**
 * @author MIH
 * 
 * Custom MessageDrivenMetaData. 
 * retrieves user and password from escidoc-core.properties.
 * 
 * @common
 */
public class EscidocMessageDrivenMetaData extends MessageDrivenMetaData {

    private String user;

    private String passwd;

    private int acknowledgeMode;

    private byte subscriptionDurability;

    private byte methodTransactionType;

    private String messagingType;

    private String destinationType;

    private String destinationLink;

    private String messageSelector;

    private String destinationJndiName;

    private String clientId;

    private String subscriptionId;

    private HashMap activationConfigProperties;

    private String resourceAdapterName;

    /**
     * Constructor. 
     * set user and password from escidoc-core.properties.
     * 
     * @param app ApplicationMetaData
     * 
     * @common
     */
    public EscidocMessageDrivenMetaData(final ApplicationMetaData app) {
        super(app);
        try {
            setUser(EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_QUEUE_USER));
            setPasswd(EscidocConfiguration.getInstance().get(
                    EscidocConfiguration.ESCIDOC_CORE_QUEUE_PASSWORD));
        } catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * @return the user
     */
    @Override
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(final String user) {
        this.user = user;
    }

    /**
     * @return the passwd
     */
    @Override
    public String getPasswd() {
        return passwd;
    }

    /**
     * @param passwd the passwd to set
     */
    public void setPasswd(final String passwd) {
        this.passwd = passwd;
    }

    /**
     * @return the acknowledgeMode
     */
    @Override
    public int getAcknowledgeMode() {
        return acknowledgeMode;
    }

    /**
     * @param acknowledgeMode the acknowledgeMode to set
     */
    public void setAcknowledgeMode(final int acknowledgeMode) {
        this.acknowledgeMode = acknowledgeMode;
    }

    /**
     * @return the subscriptionDurability
     */
    @Override
    public byte getSubscriptionDurability() {
        return subscriptionDurability;
    }

    /**
     * @param subscriptionDurability the subscriptionDurability to set
     */
    public void setSubscriptionDurability(final byte subscriptionDurability) {
        this.subscriptionDurability = subscriptionDurability;
    }

    /**
     * @return the methodTransactionType
     */
    @Override
    public byte getMethodTransactionType() {
        return methodTransactionType;
    }

    /**
     * @param methodTransactionType the methodTransactionType to set
     */
    public void setMethodTransactionType(final byte methodTransactionType) {
        this.methodTransactionType = methodTransactionType;
    }

    /**
     * @return the messagingType
     */
    @Override
    public String getMessagingType() {
        return messagingType;
    }

    /**
     * @param messagingType the messagingType to set
     */
    public void setMessagingType(final String messagingType) {
        this.messagingType = messagingType;
    }

    /**
     * @return the destinationType
     */
    @Override
    public String getDestinationType() {
        return destinationType;
    }

    /**
     * @param destinationType the destinationType to set
     */
    public void setDestinationType(final String destinationType) {
        this.destinationType = destinationType;
    }

    /**
     * @return the destinationLink
     */
    @Override
    public String getDestinationLink() {
        return destinationLink;
    }

    /**
     * @param destinationLink the destinationLink to set
     */
    public void setDestinationLink(final String destinationLink) {
        this.destinationLink = destinationLink;
    }

    /**
     * @return the messageSelector
     */
    @Override
    public String getMessageSelector() {
        return messageSelector;
    }

    /**
     * @param messageSelector the messageSelector to set
     */
    public void setMessageSelector(final String messageSelector) {
        this.messageSelector = messageSelector;
    }

    /**
     * @return the destinationJndiName
     */
    @Override
    public String getDestinationJndiName() {
        return destinationJndiName;
    }

    /**
     * @param destinationJndiName the destinationJndiName to set
     */
    public void setDestinationJndiName(final String destinationJndiName) {
        this.destinationJndiName = destinationJndiName;
    }

    /**
     * @return the clientId
     */
    @Override
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId the clientId to set
     */
    public void setClientId(final String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return the subscriptionId
     */
    @Override
    public String getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * @param subscriptionId the subscriptionId to set
     */
    public void setSubscriptionId(final String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    /**
     * @return the activationConfigProperties
     */
    @Override
    public HashMap getActivationConfigProperties() {
        return activationConfigProperties;
    }

    /**
     * @param activationConfigProperties the activationConfigProperties to set
     */
    public void setActivationConfigProperties(
            final HashMap activationConfigProperties) {
        this.activationConfigProperties = activationConfigProperties;
    }

    /**
     * @return the resourceAdapterName
     */
    @Override
    public String getResourceAdapterName() {
        return resourceAdapterName;
    }

    /**
     * @param resourceAdapterName the resourceAdapterName to set
     */
    public void setResourceAdapterName(final String resourceAdapterName) {
        this.resourceAdapterName = resourceAdapterName;
    }
    
}
