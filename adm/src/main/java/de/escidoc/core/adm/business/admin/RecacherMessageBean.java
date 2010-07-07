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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.Message;
import javax.jms.MessageListener;

import de.escidoc.core.adm.business.Constants;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.interfaces.ResourceCacheInterface;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.UserContext;

/**
 * Updates the resource cache.
 * 
 * @ejb.bean name="RecacherMessageBean" display-name="RecacherMessageBean"
 *           description="Handles Recacher Messages"
 *           destination-type="javax.jms.Queue"
 *           acknowledge-mode="Auto-acknowledge"
 * @ejb.security-identity run-as="Administrator"
 * @ejb.transaction type="NotSupported"
 * @jboss.destination-jndi-name name="queue/RecacherMessageQueue"
 * @jboss.container-configuration name="Recaching Message Driven Bean"
 * @spring.bean id="business.RecacherMessageBean"
 * 
 * @author sche
 */
public class RecacherMessageBean implements MessageDrivenBean, MessageListener {
    /**
     * Unique identifier for this class.
     */
    private static final long serialVersionUID = 2346172612245885881L;

    /**
     * Pattern to identify body of resource representation.
     */
    private static final Pattern PATTERN_BODY =
        Pattern.compile("(<[^?].*)", Pattern.MULTILINE | Pattern.DOTALL);

    /**
     * Method name to get a single resource from eSciDoc.
     */
    private static final String RETRIEVE_METHOD_NAME = "retrieve";

    /**
     * Logging goes there.
     */
    private static AppLogger logger =
        new AppLogger(RecacherMessageBean.class.getName());

    private Map<ResourceType, ResourceCacheInterface> cacheMap =
        new HashMap<ResourceType, ResourceCacheInterface>();

    /**
     * ejbCreate.
     */
    public void ejbCreate() {
        try {
            cacheMap.put(ResourceType.CONTAINER, BeanLocator
                .locateContainerCache());
            cacheMap.put(ResourceType.CONTENT_MODEL, BeanLocator
                .locateContentModelCache());
            cacheMap.put(ResourceType.CONTENT_RELATION, BeanLocator
                .locateContentRelationCache());
            cacheMap
                .put(ResourceType.CONTEXT, BeanLocator.locateContextCache());
            cacheMap.put(ResourceType.ITEM, BeanLocator.locateItemCache());
            cacheMap.put(ResourceType.OU, BeanLocator
                .locateOrganizationalUnitCache());
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
     * Return the corresponding resource handler for the given type.
     * 
     * @param type
     *            resource type
     * 
     * @return resource handler for the given type
     * @throws WebserverSystemException
     *             Thrown if a resource handler could not be localized.
     */
    private Object getHandler(final ResourceType type)
        throws WebserverSystemException {
        Object result = null;

        if (type == ResourceType.CONTAINER) {
            result = BeanLocator.locateContainerHandler();
        }
        else if (type == ResourceType.CONTENT_MODEL) {
            result = BeanLocator.locateContentModelHandler();
        }
        else if (type == ResourceType.CONTENT_RELATION) {
            result = BeanLocator.locateContentRelationHandler();
        }
        else if (type == ResourceType.CONTEXT) {
            result = BeanLocator.locateContextHandler();
        }
        else if (type == ResourceType.ITEM) {
            result = BeanLocator.locateItemHandler();
        }
        else if (type == ResourceType.OU) {
            result = BeanLocator.locateOrganizationalUnitHandler();
        }
        return result;
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
     */
    public void onMessage(final Message msg) {
        if (msg != null) {
            ResourceType type = null;

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

                type =
                    ResourceType.valueOf(msg
                        .getStringProperty(Constants.QUEUE_TYPE));

                String xmlDataRest = retrieveResource(id, type, true);
                String xmlDataSoap = retrieveResource(id, type, false);

                storeResource(type, id, xmlDataRest, xmlDataSoap);
            }
            catch (Exception e) {
                logger.error("could not dequeue message", e);
            }
            finally {
                if (type != null) {
                    RecacheStatus.getInstance().dec(type);
                }
            }
        }
    }

    /**
     * Retrieve a single resource.
     * 
     * @param id
     *            resource id
     * @param type
     *            resource type
     * @param isRestAccess
     *            true if the REST form should be requested
     * 
     * @return XML representation of this resource
     * @throws SystemException
     *             Thrown if eSciDoc failed to retrieve the resource.
     */
    private String retrieveResource(
        final String id, final ResourceType type, final boolean isRestAccess)
        throws SystemException {
        String result = null;
        boolean oldValue = UserContext.isRestAccess();

        UserContext.setRestAccess(isRestAccess);
        try {
            Object handler = getHandler(type);
            Method retrieveMethod =
                handler
                    .getClass().getMethod(RETRIEVE_METHOD_NAME, String.class);

            result = (String) retrieveMethod.invoke(handler, id);

            Matcher m = PATTERN_BODY.matcher(result);

            if (m.find()) {
                result = m.group(1);
            }
        }
        catch (Exception e) {
            logger.error("could not retrieve resource", e);
            throw new SystemException(e);
        }
        UserContext.setRestAccess(oldValue);
        return result;
    }

    /**
     * setMessageDrivenContext.
     * 
     * @param messageDrivenContext
     *            context
     */
    public void setMessageDrivenContext(
        final MessageDrivenContext messageDrivenContext) {
    }

    /**
     * Store a resource in the database cache.
     * 
     * @param type
     *            resource type
     * @param id
     *            resource id
     * @param xmlDataRest
     *            complete item as XML (REST form)
     * @param xmlDataSoap
     *            complete item as XML (SOAP form)
     * 
     * @throws SystemException
     *             Thrown if eSciDoc failed to receive a resource.
     */
    private void storeResource(
        final ResourceType type, final String id, final String xmlDataRest,
        final String xmlDataSoap) throws SystemException {

        logger.debug("store " + type.getLabel() + " \"" + id + "\"");
        cacheMap.get(type).add(id, xmlDataRest, xmlDataSoap);
    }
}
