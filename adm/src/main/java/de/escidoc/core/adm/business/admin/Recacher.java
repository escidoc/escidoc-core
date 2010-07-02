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

import static de.escidoc.core.common.business.Constants.CONTAINER_OBJECT_TYPE;
import static de.escidoc.core.common.business.Constants.CONTENT_MODEL_OBJECT_TYPE;
import static de.escidoc.core.common.business.Constants.CONTENT_RELATION2_OBJECT_TYPE;
import static de.escidoc.core.common.business.Constants.CONTEXT_OBJECT_TYPE;
import static de.escidoc.core.common.business.Constants.ITEM_OBJECT_TYPE;
import static de.escidoc.core.common.business.Constants.ORGANIZATIONAL_UNIT_OBJECT_TYPE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;

import de.escidoc.core.adm.business.Constants;
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.interfaces.RecacherInterface;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.service.interfaces.ResourceCacheInterface;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.BeanLocator;

/**
 * Provides methods used for re-caching.
 * 
 * @spring.bean id="admin.Recacher"
 * 
 * @author sche
 */
public class Recacher implements RecacherInterface {
    /**
     * Triple store query to get a list of all containers.
     */
    private static final String CONTAINER_LIST_QUERY =
        "/risearch?type=triples&lang=spo&format=N-Triples&query=*%20%3chttp://"
            + "www.w3.org/1999/02/22-rdf-syntax-ns%23type%3e%20%3c"
            + CONTAINER_OBJECT_TYPE + "%3e";

    /**
     * Triple store query to get a list of all Content Models.
     */
    private static final String CONTENT_MODEL_LIST_QUERY =
        "/risearch?type=triples&lang=spo&format=N-Triples&query=*%20%3chttp://"
            + "www.w3.org/1999/02/22-rdf-syntax-ns%23type%3e%20%3c"
            + CONTENT_MODEL_OBJECT_TYPE + "%3e";

    /**
     * Triple store query to get a list of all content relations.
     */
    private static final String CONTENT_RELATION_LIST_QUERY =
        "/risearch?type=triples&lang=spo&format=N-Triples&query=*%20%3chttp://"
            + "www.w3.org/1999/02/22-rdf-syntax-ns%23type%3e%20%3c"
            + CONTENT_RELATION2_OBJECT_TYPE + "%3e";

    /**
     * Triple store query to get a list of all contexts.
     */
    private static final String CONTEXT_LIST_QUERY =
        "/risearch?type=triples&lang=spo&format=N-Triples&query=*%20%3chttp://"
            + "www.w3.org/1999/02/22-rdf-syntax-ns%23type%3e%20%3c"
            + CONTEXT_OBJECT_TYPE + "%3e";

    /**
     * Triple store query to get a list of all items.
     */
    private static final String ITEM_LIST_QUERY =
        "/risearch?type=triples&lang=spo&format=N-Triples&query=*%20%3chttp://"
            + "www.w3.org/1999/02/22-rdf-syntax-ns%23type%3e%20%3c"
            + ITEM_OBJECT_TYPE + "%3e";

    /**
     * Triple store query to get a list of all organizational units.
     */
    private static final String OU_LIST_QUERY =
        "/risearch?type=triples&lang=spo&format=N-Triples&query=*%20%3chttp://"
            + "www.w3.org/1999/02/22-rdf-syntax-ns%23type%3e%20%3c"
            + ORGANIZATIONAL_UNIT_OBJECT_TYPE + "%3e";

    /**
     * Logging goes there.
     */
    private static AppLogger logger = new AppLogger(Recacher.class.getName());

    private Map<ResourceType, ResourceCacheInterface> cacheMap =
        new HashMap<ResourceType, ResourceCacheInterface>();

    private FedoraUtility fedoraUtility = null;

    private MessageProducer messageProducer = null;

    private Queue messageQueue = null;

    private QueueConnection queueConnection = null;

    private QueueConnectionFactory queueConnectionFactory = null;

    private QueueSession queueSession = null;

    /**
     * Create a new Recacher object.
     */
    public Recacher() {
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
        catch (Exception e) {
            logger.error("could not localize bean", e);
        }
    }

    /**
     * Clear all resources from cache.
     * 
     * @throws SystemException
     *             Thrown if eSciDoc failed to clear the resource cache.
     */
    private void clearCache() throws SystemException {
        for (ResourceCacheInterface cache : cacheMap.values()) {
            cache.clear();
        }
    }

    /**
     * Create a connection to the message queue.
     * 
     * @throws IOException
     *             Thrown if some properties could not be read from
     *             configuration.
     * @throws JMSException
     *             Thrown if the connection to the message queue could not be
     *             established.
     */
    private void createQueueConnection() throws IOException, JMSException {
        if (queueConnection == null) {
            queueConnection =
                queueConnectionFactory.createQueueConnection(
                    EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.ESCIDOC_CORE_QUEUE_USER),
                    EscidocConfiguration.getInstance().get(
                        EscidocConfiguration.ESCIDOC_CORE_QUEUE_PASSWORD));
            queueSession =
                queueConnection.createQueueSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            messageProducer = queueSession.createProducer(messageQueue);
        }
    }

    /**
     * Delete a resource from the resource cache.
     * 
     * @param id
     *            resource id
     * 
     * @throws SystemException
     *             Thrown if eSciDoc failed to delete a resource.
     */
    public void deleteResource(final String id) throws SystemException {
        for (ResourceCacheInterface cache : cacheMap.values()) {
            cache.remove(id);
        }
    }

    /**
     * Get a list of all available resources of the given type from Fedora.
     * 
     * @param type
     *            resource type
     * @param listQuery
     *            Fedora query to get a list of all resources of the given type
     * @param clearCache
     *            clear the repository before adding objects to it
     * 
     * @return list of resource ids
     * @throws SystemException
     *             Thrown if eSciDoc failed to receive a resource.
     */
    private Collection<String> getIds(
        final ResourceType type, final String listQuery,
        final boolean clearCache) throws SystemException {
        Collection<String> result = new LinkedList<String>();
        BufferedReader input = null;

        try {
            input =
                new BufferedReader(new InputStreamReader(fedoraUtility
                    .query(listQuery)));

            RecacheStatus recacheStatus = RecacheStatus.getInstance();
            ResourceCacheInterface cache = cacheMap.get(type);
            String line;

            while ((line = input.readLine()) != null) {
                final String subject = getSubject(line);

                if (subject != null) {
                    final String id =
                        subject.substring(subject.indexOf('/') + 1);
                    if (cache.isEnabled() && (clearCache || !cache.exists(id))) {
                        recacheStatus.inc(type);
                        result.add(id);
                    }
                }
            }
        }
        catch (IOException e) {
            throw new SystemException(e);
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (IOException e) {
                    throw new SystemException(e);
                }
            }
        }
        return result;
    }

    /**
     * Get the current status of the running/finished recaching process.
     * 
     * @return current status (how many objects are still in the queue)
     * @throws SystemException
     *             thrown in case of an internal error
     */
    public String getStatus() throws SystemException {
        return RecacheStatus.getInstance().toString();
    }

    /**
     * Extract the subject from the given triple.
     * 
     * @param triple
     *            the triple from which the subject has to be extracted
     * 
     * @return the subject of the given triple
     */
    private String getSubject(final String triple) {
        String result = null;

        if (triple != null) {
            int index = triple.indexOf(' ');

            if (index > 0) {
                result = triple.substring(triple.indexOf('/') + 1, index - 1);
            }
        }
        return result;
    }

    /**
     * Put the given list of resource ids into the message queue.
     * 
     * @param type
     *            resource type
     * @param ids
     *            list of resource ids
     * 
     * @return the list of resource ids
     * @throws IOException
     *             Thrown if some properties could not be read from
     *             configuration.
     * @throws JMSException
     *             Thrown if the connection to the message queue could not be
     *             established.
     */
    public Collection<String> queueIds(
        final ResourceType type, final Collection<String> ids)
        throws IOException, JMSException {
        createQueueConnection();
        for (String id : ids) {
            ObjectMessage message = queueSession.createObjectMessage();

            message.setStringProperty(Constants.QUEUE_ID, id);
            message.setStringProperty(Constants.QUEUE_TYPE, type.name());
            messageProducer.send(message);
        }
        return ids;
    }

    /**
     * Start recaching or return the current status of a running recaching
     * process.
     * 
     * @param clearCache
     *            clear the cache before adding objects to it
     * 
     * @return current status of a running/finished recaching process
     * 
     * @throws SystemException
     *             Thrown if eSciDoc failed to queue a resource.
     */
    public String recache(final boolean clearCache) throws SystemException {
        StringBuffer result = new StringBuffer();
        RecacheStatus recacheStatus = RecacheStatus.getInstance();

        if (recacheStatus.startMethod()) {
            boolean idListEmpty = true;

            try {
                if (clearCache) {
                    clearCache();
                }

                Collection<String> ids;

                ids =
                    queueIds(ResourceType.CONTAINER, getIds(
                        ResourceType.CONTAINER, CONTAINER_LIST_QUERY,
                        clearCache));
                idListEmpty &= ids.size() == 0;
                result.append("<message>\n");
                result.append("scheduling " + ids.size()
                    + " container(s) for recaching\n");
                result.append("</message>\n");
                ids =
                    queueIds(ResourceType.CONTENT_MODEL, getIds(
                        ResourceType.CONTENT_MODEL,
                        CONTENT_MODEL_LIST_QUERY, clearCache));
                idListEmpty &= ids.size() == 0;
                result.append("<message>\n");
                result.append("scheduling " + ids.size()
                    + " content model(s) for recaching\n");
                result.append("</message>\n");
                ids =
                    queueIds(ResourceType.CONTENT_RELATION, getIds(
                        ResourceType.CONTENT_RELATION,
                        CONTENT_RELATION_LIST_QUERY, clearCache));
                idListEmpty &= ids.size() == 0;
                result.append("<message>\n");
                result.append("scheduling " + ids.size()
                    + " content relation(s) for recaching\n");
                result.append("</message>\n");
                ids =
                    queueIds(ResourceType.CONTEXT, getIds(ResourceType.CONTEXT,
                        CONTEXT_LIST_QUERY, clearCache));
                idListEmpty &= ids.size() == 0;
                result.append("<message>\n");
                result.append("scheduling " + ids.size()
                    + " context(s) for recaching\n");
                result.append("</message>\n");
                ids =
                    queueIds(ResourceType.OU, getIds(ResourceType.OU,
                        OU_LIST_QUERY, clearCache));
                idListEmpty &= ids.size() == 0;
                result.append("<message>\n");
                result.append("scheduling " + ids.size()
                    + " organizational unit(s) for recaching\n");
                result.append("</message>\n");
                ids =
                    queueIds(ResourceType.ITEM, getIds(ResourceType.ITEM,
                        ITEM_LIST_QUERY, clearCache));
                idListEmpty &= ids.size() == 0;
                result.append("<message>\n");
                result.append("scheduling " + ids.size()
                    + " item(s) for recaching\n");
                result.append("</message>\n");
            }
            catch (IOException e) {
                throw new SystemException(e);
            }
            catch (JMSException e) {
                throw new SystemException(e);
            }
            finally {
                if (idListEmpty) {
                    recacheStatus.finishMethod();
                }
                recacheStatus.setFillingComplete();
            }
        }
        else {
            result.append(getStatus());
        }
        return result.toString();
    }

    /**
     * Injects the {@link FedoraUtility}.
     * 
     * @spring.property ref="escidoc.core.business.FedoraUtility"
     * 
     * @param fedoraUtility
     *            the {@link FedoraUtility} to inject.
     */
    public void setFedoraUtility(final FedoraUtility fedoraUtility) {
        this.fedoraUtility = fedoraUtility;
    }

    /**
     * Injects the {@link Queue}.
     * 
     * @spring.property ref="adm.remote.recacherMessageQueue"
     * 
     * @param messageQueue
     *            message queue
     */
    public void setMessageQueue(final Queue messageQueue) {
        this.messageQueue = messageQueue;
    }

    /**
     * Injects the {@link QueueQueueConnectionFactory}.
     * 
     * @spring.property ref="common.local.QueueConnectionFactory"
     * 
     * @param queueConnectionFactory
     *            QueueConnectionFactory
     */
    public void setQueueConnectionFactory(
        final QueueConnectionFactory queueConnectionFactory) {
        this.queueConnectionFactory = queueConnectionFactory;
    }
}
