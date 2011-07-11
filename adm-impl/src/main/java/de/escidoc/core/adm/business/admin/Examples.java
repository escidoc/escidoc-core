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
import de.escidoc.core.common.util.service.ConnectionUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContextHandlerInterface;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;
import de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Date;

/**
 * Load some example objects as resource XMLs into the eSciDoc repository.
 *
 * @author André Schenk
 */
@Service("admin.Examples")
public class Examples {

    private static final String EXAMPLE_CONTAINER = "container.xml";

    private static final String EXAMPLE_CONTENT_MODEL = "content-model.xml";

    private static final String EXAMPLE_CONTEXT = "context.xml";

    private static final String EXAMPLE_ITEM = "item.xml";

    private static final String EXAMPLE_OU = "organizational-unit.xml";

    @Autowired
    @Qualifier("escidoc.core.common.util.service.ConnectionUtility")
    private ConnectionUtility connectionUtility;

    @Autowired
    @Qualifier("service.ContentModelHandler")
    private ContentModelHandlerInterface contentModelHandler;

    @Autowired
    @Qualifier("service.ContextHandler")
    private ContextHandlerInterface contextHandler;

    @Autowired
    @Qualifier("service.ContainerHandler")
    private ContainerHandlerInterface containerHandler;

    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandlerInterface itemHandler;

    @Autowired
    @Qualifier("service.OrganizationalUnitHandler")
    private OrganizationalUnitHandlerInterface organizationalUnitHandler;

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
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    private static String getLastModificationDate(final String xml) throws XPathExpressionException, IOException,
        ParserConfigurationException, SAXException {
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
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    private static String getLastModificationDate(final String xml, final ResourceType type)
        throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
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
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    private static String getObjectId(final String xml, final ResourceType type) throws XPathExpressionException,
        IOException, ParserConfigurationException, SAXException {
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
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     * @throws de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.ContextNameNotUniqueException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContextException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingContentException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthenticationException
     * @throws de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws org.xml.sax.SAXException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthorizationException
     * @throws java.io.IOException
     * @throws de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     */
    public String load(final String directory) throws RelationPredicateNotFoundException, OptimisticLockingException,
        AuthorizationException, IOException, SAXException, InvalidContentException, StreamNotFoundException,
        AuthenticationException, MissingContentException, ReadonlyViolationException, InvalidXmlException,
        ContextNotFoundException, ContainerNotFoundException, MissingAttributeValueException, SystemException,
        InvalidStatusException, InvalidContextException, XPathExpressionException, MissingElementValueException,
        LockingException, ParserConfigurationException, ContentModelNotFoundException, ComponentNotFoundException,
        ContextNameNotUniqueException, OrganizationalUnitNotFoundException, FileNotFoundException,
        ReadonlyVersionException, ItemNotFoundException, MissingMdRecordException, ReferencedResourceNotFoundException,
        MissingMethodParameterException {
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
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthenticationException
     * @throws de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws org.xml.sax.SAXException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthorizationException
     * @throws java.io.IOException
     * @throws de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException
     */
    private String loadContainer(final String xml, final String contextId, final String contentModelId)
        throws XmlSchemaValidationException, MissingAttributeValueException, SystemException,
        RelationPredicateNotFoundException, IOException, AuthorizationException, InvalidStatusException, SAXException,
        XPathExpressionException, MissingElementValueException, ParserConfigurationException, InvalidContentException,
        ContentModelNotFoundException, XmlCorruptedException, AuthenticationException, MissingMdRecordException,
        ReferencedResourceNotFoundException, ContextNotFoundException, MissingMethodParameterException {
        final String createXml = this.containerHandler.create(MessageFormat.format(xml, contextId, contentModelId));
        return getObjectId(createXml, ResourceType.CONTAINER);
    }

    /**
     * Load the example content model.
     *
     * @param xml content model XML
     * @return object id of the newly created content model
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws org.xml.sax.SAXException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthenticationException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthorizationException
     * @throws java.io.IOException
     * @throws de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     * @throws de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     */
    private String loadContentModel(final String xml) throws MissingAttributeValueException, SystemException,
        XmlSchemaValidationException, AuthorizationException, IOException, SAXException, XPathExpressionException,
        ParserConfigurationException, InvalidContentException, XmlCorruptedException, AuthenticationException,
        MissingMethodParameterException {
        final String createXml = this.contentModelHandler.create(xml);
        return getObjectId(createXml, ResourceType.CONTENT_MODEL);
    }

    /**
     * Load the example context.
     *
     * @param xml  context XML
     * @param ouId organizational unit id
     * @return object id of the newly created context
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthenticationException
     * @throws de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.ContextNameNotUniqueException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws org.xml.sax.SAXException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws java.io.IOException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthorizationException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     */
    private String loadContext(final String xml, final String ouId) throws OptimisticLockingException,
        AuthorizationException, IOException, SAXException, InvalidContentException, StreamNotFoundException,
        AuthenticationException, InvalidXmlException, ContextNotFoundException, SystemException,
        MissingAttributeValueException, InvalidStatusException, MissingElementValueException, XPathExpressionException,
        ReadonlyElementViolationException, LockingException, ParserConfigurationException,
        ContentModelNotFoundException, OrganizationalUnitNotFoundException, ContextNameNotUniqueException,
        ReadonlyAttributeViolationException, MissingMethodParameterException {
        final String createXml = this.contextHandler.create(MessageFormat.format(xml, new Date().getTime(), ouId));
        final String result = getObjectId(createXml, ResourceType.CONTEXT);
        this.contextHandler.open(result, createTaskParam(getLastModificationDate(createXml, ResourceType.CONTEXT)));
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
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws de.escidoc.core.common.exceptions.application.violated.LockingException
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContextException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingContentException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthenticationException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws org.xml.sax.SAXException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthorizationException
     * @throws java.io.IOException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     * @throws de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException
     */
    private String loadItem(
        final String xml, final String contextId, final String contentModelId, final String containerId)
        throws OptimisticLockingException, RelationPredicateNotFoundException, AuthorizationException, IOException,
        SAXException, InvalidContentException, AuthenticationException, ReadonlyViolationException,
        MissingContentException, InvalidXmlException, ContextNotFoundException, ContainerNotFoundException,
        MissingAttributeValueException, SystemException, InvalidStatusException, InvalidContextException,
        MissingElementValueException, XPathExpressionException, LockingException, ParserConfigurationException,
        ContentModelNotFoundException, ComponentNotFoundException, ReadonlyVersionException, FileNotFoundException,
        ItemNotFoundException, MissingMdRecordException, ReferencedResourceNotFoundException,
        MissingMethodParameterException {
        final String createXml =
            this.containerHandler.createItem(containerId, MessageFormat.format(xml, contextId, contentModelId));
        final String result = getObjectId(createXml, ResourceType.ITEM);
        final String submitXml =
            this.itemHandler.submit(result, createTaskParam(getLastModificationDate(createXml, ResourceType.ITEM)));
        final String objectPidXml =
            this.itemHandler.assignObjectPid(result, createTaskParam(getLastModificationDate(submitXml)));
        final String versionPidXml =
            this.itemHandler.assignVersionPid(result, createTaskParam(getLastModificationDate(objectPidXml)));
        this.itemHandler.release(result, createTaskParam(getLastModificationDate(versionPidXml)));
        return result;
    }

    /**
     * Load the example organizational unit.
     *
     * @param xml organizational unit XML
     * @return object id of the newly created organizational unit
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthenticationException
     * @throws de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws javax.xml.xpath.XPathExpressionException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingElementValueException
     * @throws org.xml.sax.SAXException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.application.security.AuthorizationException
     * @throws java.io.IOException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException
     */
    private String loadOrganizationalUnit(final String xml) throws OptimisticLockingException,
        MissingAttributeValueException, SystemException, IOException, AuthorizationException, InvalidStatusException,
        SAXException, MissingElementValueException, XPathExpressionException, ParserConfigurationException,
        OrganizationalUnitNotFoundException, AuthenticationException, MissingMdRecordException, InvalidXmlException,
        MissingMethodParameterException {
        final String createXml = this.organizationalUnitHandler.create(xml);
        final String result = getObjectId(createXml, ResourceType.OU);
        this.organizationalUnitHandler.open(result,
            createTaskParam(getLastModificationDate(createXml, ResourceType.OU)));
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
