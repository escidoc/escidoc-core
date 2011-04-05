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

import de.escidoc.core.cmm.service.interfaces.ContentModelHandlerInterface;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.ContextNameNotUniqueException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.IOUtils;
import de.escidoc.core.common.util.service.BeanLocator;
import de.escidoc.core.common.util.service.ConnectionUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContextHandlerInterface;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;
import de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;

/**
 * Load some example objects as resource XMLs into the eSciDoc repository.
 *
 * @author André Schenk
 */
public class Examples {

    private static final String EXAMPLE_CONTAINER = "container.xml";

    private static final String EXAMPLE_CONTENT_MODEL = "content-model.xml";

    private static final String EXAMPLE_CONTEXT = "context.xml";

    private static final String EXAMPLE_ITEM = "item.xml";

    private static final String EXAMPLE_OU = "organizational-unit.xml";

    private ConnectionUtility connectionUtility;

    /**
     * Create an XML snippet for a message that can be displayed on the Web page.
     *
     * @param message message text
     * @return XML snippet
     */
    private static String createMessage(final String message) {
        return "<message>" + message + "</message>";
    }

    /**
     * Create a snippet for a task parameter XML including the last modification date.
     *
     * @param lastModificationDate the last modification date
     * @return XML snippet
     */
    private static String createTaskParam(final String lastModificationDate) {
        return "<param last-modification-date=\"" + lastModificationDate + "\"/>";
    }

    /**
     * Extract the last modification date from the given result XML.
     *
     * @param xml result XML
     * @return last modification date
     * @throws Exception thrown if the XPath evaluation failed
     */
    private static String getLastModificationDate(final String xml) throws XPathExpressionException, IOException,
        ParserConfigurationException, SAXException, UnsupportedEncodingException {
        String result = null;

        if (xml != null) {
            ByteArrayInputStream input = null;
            try {
                input = new ByteArrayInputStream(xml.getBytes(XmlUtility.CHARACTER_ENCODING));
                final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                final Document xmlDom = db.parse(input);
                final XPath xpath = XPathFactory.newInstance().newXPath();
                result = xpath.evaluate("/result/@last-modification-date", xmlDom);
            }
            finally {
                IOUtils.closeStream(input);
            }
        }
        return result;
    }

    /**
     * Extract the last modification date from the given resource XML.
     *
     * @param xml  resource XML (item XML, container XML, ...)
     * @param type resource type
     * @return last modification date
     * @throws Exception thrown if the XPath evaluation failed
     */
    private static String getLastModificationDate(final String xml, final ResourceType type)
        throws XPathExpressionException, IOException, ParserConfigurationException, SAXException,
        UnsupportedEncodingException {
        String result = null;

        if (xml != null) {
            ByteArrayInputStream input = null;
            try {
                input = new ByteArrayInputStream(xml.getBytes(XmlUtility.CHARACTER_ENCODING));
                final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                final Document xmlDom = db.parse(input);
                final XPath xpath = XPathFactory.newInstance().newXPath();
                result = xpath.evaluate('/' + type.getLabel() + "/@last-modification-date", xmlDom);
            }
            finally {
                IOUtils.closeStream(input);
            }
        }
        return result;
    }

    /**
     * Extract the object id from the given resource XML.
     *
     * @param xml  resource XML (item XML, container XML, ...)
     * @param type resource type
     * @return object id
     * @throws Exception thrown if the XPath evaluation failed
     */
    private static String getObjectId(final String xml, final ResourceType type) throws XPathExpressionException,
        IOException, ParserConfigurationException, SAXException, UnsupportedEncodingException {
        String result = null;

        if (xml != null) {
            ByteArrayInputStream input = null;
            try {
                input = new ByteArrayInputStream(xml.getBytes(XmlUtility.CHARACTER_ENCODING));
                final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                final Document xmlDom = db.parse(input);
                final XPath xpath = XPathFactory.newInstance().newXPath();
                final String href =
                    xpath.evaluate('/' + type.getLabel() + "/@href|/" + type.getLabel() + "/@objid", xmlDom);
                result = href.substring(href.lastIndexOf('/') + 1);
            }
            finally {
                IOUtils.closeStream(input);
            }
        }
        return result;
    }

    /**
     * Load all example objects.
     *
     * @param directory URL to the directory which contains the eSciDoc XML files (including the trailing slash).
     * @return some useful information to the user which objects were loaded
     * @throws Exception thrown in case of an internal error
     */
    public String load(final String directory) throws MalformedURLException, WebserverSystemException,
        RelationPredicateNotFoundException, OptimisticLockingException, XmlSchemaValidationException,
        AuthorizationException, IOException, SAXException, InvalidContentException, XmlCorruptedException,
        StreamNotFoundException, AuthenticationException, MissingContentException, ReadonlyViolationException,
        InvalidXmlException, ContextNotFoundException, ContainerNotFoundException, MissingAttributeValueException,
        SystemException, InvalidStatusException, InvalidContextException, XPathExpressionException,
        MissingElementValueException, ReadonlyElementViolationException, LockingException,
        ParserConfigurationException, ContentModelNotFoundException, ComponentNotFoundException,
        ContextNameNotUniqueException, OrganizationalUnitNotFoundException, FileNotFoundException,
        ReadonlyVersionException, ItemNotFoundException, ReadonlyAttributeViolationException, MissingMdRecordException,
        ReferencedResourceNotFoundException, MissingMethodParameterException, UnsupportedEncodingException {
        final StringBuilder result = new StringBuilder();
        final String ouId = loadOrganizationalUnit(loadFile(directory + EXAMPLE_OU));

        result.append(createMessage("created " + ResourceType.OU.getLabel() + ": " + ouId));

        final String contextId = loadContext(loadFile(directory + EXAMPLE_CONTEXT), ouId);

        result.append(createMessage("created " + ResourceType.CONTEXT.getLabel() + ": " + contextId));

        final String contentModelId = loadContentModel(loadFile(directory + EXAMPLE_CONTENT_MODEL));

        result.append(createMessage("created " + ResourceType.CONTENT_MODEL.getLabel() + ": " + contentModelId));

        final String containerId = loadContainer(loadFile(directory + EXAMPLE_CONTAINER), contextId, contentModelId);

        result.append(createMessage("created " + ResourceType.CONTAINER.getLabel() + ": " + containerId));

        final String itemId = loadItem(loadFile(directory + EXAMPLE_ITEM), contextId, contentModelId, containerId);

        result.append(createMessage("created " + ResourceType.ITEM.getLabel() + ": " + itemId));

        return result.toString();
    }

    /**
     * Load the example container.
     *
     * @param xml            container XML
     * @param contextId      context id
     * @param contentModelId content model id
     * @return object id of the newly created container
     * @throws Exception thrown in case of an internal error
     */
    private static String loadContainer(final String xml, final String contextId, final String contentModelId)
        throws XmlSchemaValidationException, MissingAttributeValueException, SystemException,
        RelationPredicateNotFoundException, IOException, AuthorizationException, InvalidStatusException, SAXException,
        XPathExpressionException, MissingElementValueException, ParserConfigurationException, WebserverSystemException,
        InvalidContentException, ContentModelNotFoundException, XmlCorruptedException, AuthenticationException,
        MissingMdRecordException, ReferencedResourceNotFoundException, ContextNotFoundException,
        MissingMethodParameterException, UnsupportedEncodingException {
        String result = null;
        final ContainerHandlerInterface handler = BeanLocator.locateContainerHandler();

        if (handler != null) {
            final String createXml = handler.create(MessageFormat.format(xml, contextId, contentModelId));

            result = getObjectId(createXml, ResourceType.CONTAINER);
        }
        return result;
    }

    /**
     * Load the example content model.
     *
     * @param xml content model XML
     * @return object id of the newly created content model
     * @throws Exception thrown in case of an internal error
     */
    private static String loadContentModel(final String xml) throws MissingAttributeValueException, SystemException,
        XmlSchemaValidationException, AuthorizationException, IOException, SAXException, XPathExpressionException,
        WebserverSystemException, ParserConfigurationException, InvalidContentException, XmlCorruptedException,
        AuthenticationException, MissingMethodParameterException, UnsupportedEncodingException {
        String result = null;
        final ContentModelHandlerInterface handler = BeanLocator.locateContentModelHandler();

        if (handler != null) {
            final String createXml = handler.create(xml);

            result = getObjectId(createXml, ResourceType.CONTENT_MODEL);
        }
        return result;
    }

    /**
     * Load the example context.
     *
     * @param xml  context XML
     * @param ouId organizational unit id
     * @return object id of the newly created context
     * @throws Exception thrown in case of an internal error
     */
    private static String loadContext(final String xml, final String ouId) throws XmlSchemaValidationException,
        OptimisticLockingException, AuthorizationException, IOException, SAXException, WebserverSystemException,
        InvalidContentException, XmlCorruptedException, StreamNotFoundException, AuthenticationException,
        InvalidXmlException, ContextNotFoundException, SystemException, MissingAttributeValueException,
        InvalidStatusException, MissingElementValueException, XPathExpressionException,
        ReadonlyElementViolationException, LockingException, ParserConfigurationException,
        ContentModelNotFoundException, OrganizationalUnitNotFoundException, ContextNameNotUniqueException,
        ReadonlyAttributeViolationException, MissingMethodParameterException, UnsupportedEncodingException {
        String result = null;
        final ContextHandlerInterface handler = BeanLocator.locateContextHandler();

        if (handler != null) {
            final String createXml = handler.create(MessageFormat.format(xml, new Date().getTime(), ouId));

            result = getObjectId(createXml, ResourceType.CONTEXT);
            handler.open(result, createTaskParam(getLastModificationDate(createXml, ResourceType.CONTEXT)));
        }
        return result;
    }

    /**
     * Load the file from the given URL into a string.
     *
     * @param url file URL
     * @return string which contains the file content
     * @throws MalformedURLException    malformed URL
     * @throws WebserverSystemException thrown if the file couldn't be loaded
     */
    private String loadFile(final String url) throws WebserverSystemException, MalformedURLException {
        return connectionUtility.getRequestURLAsString(new URL(url));
    }

    /**
     * Load the example item.
     *
     * @param xml            item XML
     * @param contextId      context id
     * @param contentModelId content model id
     * @param containerId    container id
     * @return object id of the newly created item
     * @throws Exception thrown in case of an internal error
     */
    private static String loadItem(
        final String xml, final String contextId, final String contentModelId, final String containerId)
        throws OptimisticLockingException, RelationPredicateNotFoundException, AuthorizationException, IOException,
        SAXException, WebserverSystemException, InvalidContentException, XmlCorruptedException,
        AuthenticationException, ReadonlyViolationException, MissingContentException, InvalidXmlException,
        ContextNotFoundException, ContainerNotFoundException, MissingAttributeValueException, SystemException,
        InvalidStatusException, InvalidContextException, MissingElementValueException, XPathExpressionException,
        LockingException, ReadonlyElementViolationException, ParserConfigurationException,
        ContentModelNotFoundException, ComponentNotFoundException, ReadonlyVersionException, FileNotFoundException,
        ItemNotFoundException, ReadonlyAttributeViolationException, MissingMdRecordException,
        ReferencedResourceNotFoundException, MissingMethodParameterException, UnsupportedEncodingException {
        String result = null;
        final ContainerHandlerInterface containerHandler = BeanLocator.locateContainerHandler();
        final ItemHandlerInterface itemHandler = BeanLocator.locateItemHandler();

        if (containerHandler != null && itemHandler != null) {
            final String createXml =
                containerHandler.createItem(containerId, MessageFormat.format(xml, contextId, contentModelId));

            result = getObjectId(createXml, ResourceType.ITEM);

            final String submitXml =
                itemHandler.submit(result, createTaskParam(getLastModificationDate(createXml, ResourceType.ITEM)));
            final String objectPidXml =
                itemHandler.assignObjectPid(result, createTaskParam(getLastModificationDate(submitXml)));
            final String versionPidXml =
                itemHandler.assignVersionPid(result, createTaskParam(getLastModificationDate(objectPidXml)));

            itemHandler.release(result, createTaskParam(getLastModificationDate(versionPidXml)));
        }
        return result;
    }

    /**
     * Load the example organizational unit.
     *
     * @param xml organizational unit XML
     * @return object id of the newly created organizational unit
     * @throws Exception thrown in case of an internal error
     */
    private static String loadOrganizationalUnit(final String xml) throws OptimisticLockingException,
        MissingAttributeValueException, SystemException, XmlSchemaValidationException, IOException,
        AuthorizationException, InvalidStatusException, SAXException, MissingElementValueException,
        XPathExpressionException, ParserConfigurationException, WebserverSystemException, XmlCorruptedException,
        OrganizationalUnitNotFoundException, AuthenticationException, MissingMdRecordException, InvalidXmlException,
        MissingMethodParameterException, UnsupportedEncodingException {
        String result = null;
        final OrganizationalUnitHandlerInterface handler = BeanLocator.locateOrganizationalUnitHandler();

        if (handler != null) {
            final String createXml = handler.create(xml);

            result = getObjectId(createXml, ResourceType.OU);
            handler.open(result, createTaskParam(getLastModificationDate(createXml, ResourceType.OU)));
        }
        return result;
    }

    /**
     * Ingest the ConnectionUtility object.
     *
     * @param connectionUtility ConnectionUtility object to be ingested
     */
    public void setConnectionUtility(final ConnectionUtility connectionUtility) {
        this.connectionUtility = connectionUtility;
    }
}
