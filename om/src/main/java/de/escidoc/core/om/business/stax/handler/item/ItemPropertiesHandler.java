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
package de.escidoc.core.om.business.stax.handler.item;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.business.fedora.resources.create.ItemProperties;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.MultipleExtractor;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Handle and obtain values from Item Properties section.
 */
@Configurable
public class ItemPropertiesHandler extends DefaultHandler {

    @Autowired
    @Qualifier("business.Utility")
    private Utility utility;

    private final StaxParser parser;

    private final ItemProperties properties;

    private static final String XPATH_ITEM = '/' + Elements.ELEMENT_ITEM;

    private static final String XPATH_ITEM_PROPERTIES = XPATH_ITEM + '/' + Elements.ELEMENT_PROPERTIES;

    private static final String XPATH_ITEM_CONTENT_MODEL_SPECIFIC =
        XPATH_ITEM_PROPERTIES + '/' + Elements.ELEMENT_CONTENT_MODEL_SPECIFIC;

    private final Collection<String> expectedElements = new ArrayList<String>();

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemPropertiesHandler.class);

    private boolean parsingContentModelSpecific;

    private MultipleExtractor contentModelHandler;

    /**
     * @param parser StAX Parser
     * @throws WebserverSystemException Thrown by VersionProperties if obtaining user context failed.
     */
    public ItemPropertiesHandler(final StaxParser parser) throws WebserverSystemException {

        this.parser = parser;
        this.properties = new ItemProperties();
    }

    /**
     * Get ItemProperties.
     *
     * @return ItemProperties.
     */
    public ItemProperties getProperties() {

        return this.properties;
    }

    /**
     * @param element StartElement
     * @return StartElement
     */
    @Override
    public StartElement startElement(final StartElement element) throws ContentModelNotFoundException,
        ContextNotFoundException, MissingAttributeValueException, ReadonlyAttributeViolationException,
        ReadonlyElementViolationException, WebserverSystemException, XMLStreamException, InvalidContentException {

        if (this.parsingContentModelSpecific) {
            this.contentModelHandler.startElement(element);
        }
        else {
            final String curPath = parser.getCurPath();

            if (curPath.startsWith(XPATH_ITEM_PROPERTIES)) {
                final String theName = element.getLocalName();
                if (theName.equals(Elements.ELEMENT_PROPERTIES)) {
                    expectedElements.add(Elements.ELEMENT_CONTEXT);
                    expectedElements.add(Elements.ELEMENT_CONTENT_MODEL);
                }
                else if (theName.equals(Elements.ELEMENT_CONTEXT)) {
                    handleContextElement(element);
                }
                else if (theName.equals(Elements.ELEMENT_CONTENT_MODEL)) {
                    handleContentModel(element);
                }
                else if (theName.equals(Elements.ELEMENT_ORIGIN)) {
                    handleOrigin(element);
                }
                else if (theName.equals(Elements.ELEMENT_CONTENT_MODEL_SPECIFIC)) {

                    this.parsingContentModelSpecific = true;
                    this.contentModelHandler = new MultipleExtractor(XPATH_ITEM_CONTENT_MODEL_SPECIFIC, this.parser);

                    this.contentModelHandler.startElement(element);
                }
            }
        }
        return element;
    }

    /**
     * @param element EndElement
     * @return EndElement
     */
    @Override
    public EndElement endElement(final EndElement element) throws MissingAttributeValueException,
        ContextNotFoundException, ContentModelNotFoundException, XMLStreamException, UnsupportedEncodingException,
        IntegritySystemException, TripleStoreSystemException, WebserverSystemException, XmlCorruptedException {

        final String currentPath = parser.getCurPath();
        if (currentPath.equals(XPATH_ITEM_PROPERTIES)) {
            if (!expectedElements.isEmpty()) {
                throw new XmlCorruptedException("One of " + expectedElements.toString() + " missing.");
            }

            // String id = properties.get(TripleStoreUtility.PROP_CONTEXT_ID);
            String id = this.properties.getObjectProperties().getContextId();
            utility.checkIsContext(id);
            id = this.properties.getObjectProperties().getContentModelId();
            utility.checkIsContentModel(id);
        }
        else if (currentPath.equals(XPATH_ITEM_CONTENT_MODEL_SPECIFIC)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached end of " + XPATH_ITEM_CONTENT_MODEL_SPECIFIC);
            }
            this.parsingContentModelSpecific = false;
            this.contentModelHandler.endElement(element);

            final ByteArrayOutputStream cms =
                (ByteArrayOutputStream) this.contentModelHandler.getOutputStreams().get(
                    Elements.ELEMENT_CONTENT_MODEL_SPECIFIC);

            this.properties.setContentModelSpecific(cms.toString(XmlUtility.CHARACTER_ENCODING).trim());
            this.contentModelHandler = null;
        }
        else {
            if (this.parsingContentModelSpecific) {
                this.contentModelHandler.endElement(element);
            }
        }

        return element;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#characters
     * (java.lang.String,
     * de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    @Override
    public String characters(final String data, final StartElement element) throws WebserverSystemException,
        InvalidStatusException {

        final String curPath = parser.getCurPath();
        if (curPath.equals(XPATH_ITEM_PROPERTIES + '/' + Elements.ELEMENT_PID)) {
            // properties.put(TripleStoreUtility.PROP_OBJECT_PID, data);
            this.properties.getObjectProperties().setPid(data);
        }
        else if (curPath.equals(XPATH_ITEM_PROPERTIES + '/' + Elements.ELEMENT_PUBLIC_STATUS)) {
            this.properties.getObjectProperties().setStatus(getStatusType(data));
        }
        else if (this.parsingContentModelSpecific) {
            this.contentModelHandler.characters(data, element);
        }

        return data;
    }

    /**
     * @param element StAX StartElement
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     */
    private void handleContextElement(final StartElement element) throws MissingAttributeValueException,
        ContextNotFoundException {

        this.expectedElements.remove(Elements.ELEMENT_CONTEXT);
        String contextId;
        try {
            contextId = element.getAttributeValue(null, Elements.ATTRIBUTE_XLINK_OBJID);
            if (contextId == null || contextId.length() < 1) {
                throw new MissingAttributeValueException("No context id found.");
            }
        }
        catch (final NoSuchAttributeException e) {
            final String href;
            try {
                href = element.getAttributeValue(Constants.XLINK_NS_URI, Elements.ATTRIBUTE_XLINK_HREF);
            }
            catch (final NoSuchAttributeException e1) {
                throw new MissingAttributeValueException("The attribute " + Elements.ATTRIBUTE_XLINK_HREF + " of "
                    + Elements.ELEMENT_CONTEXT + " is missing in item for create.", e);
            }
            final int indexOfLastSlash = href.lastIndexOf('/');
            contextId = href.substring(indexOfLastSlash + 1);
            if (contextId == null || contextId.length() < 1) {
                throw new MissingAttributeValueException("No context id found.", e);
            }
            if (!href.substring(0, indexOfLastSlash + 1).equalsIgnoreCase(Constants.CONTEXT_URL_BASE)) {
                throw new ContextNotFoundException("The " + Elements.ELEMENT_CONTEXT + " element has a wrong url."
                    + "the url have to look like: " + Constants.CONTEXT_URL_BASE + "[id] ", e);
            }
        }
        this.properties.getObjectProperties().setContextId(contextId);
    }

    /**
     * @param element StAX StartElement
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException
     */
    private void handleContentModel(final StartElement element) throws MissingAttributeValueException,
        ContentModelNotFoundException {
        expectedElements.remove(Elements.ELEMENT_CONTENT_MODEL);
        // FIXME check this method: it seams that here is a mixture
        // between variable names (contentModelId and contextId)
        String contentModelId;
        try {
            contentModelId = element.getAttributeValue(null, Elements.ATTRIBUTE_XLINK_OBJID);
            if (contentModelId == null || contentModelId.length() < 1) {
                throw new MissingAttributeValueException("No content-model id found.");
            }
        }
        catch (final NoSuchAttributeException e) {
            final String href;
            try {
                href = element.getAttributeValue(Constants.XLINK_NS_URI, Elements.ATTRIBUTE_XLINK_HREF);
            }
            catch (final NoSuchAttributeException e1) {
                final String refType = Elements.ELEMENT_CONTENT_MODEL;
                final String objType = "item";
                throw new MissingAttributeValueException("The attribute " + Elements.ATTRIBUTE_XLINK_HREF + " of "
                    + refType + " is missing in " + objType + " for create.", e);
            }
            final int indexOfLastSlash = href.lastIndexOf('/');
            contentModelId = href.substring(indexOfLastSlash + 1);
            if (contentModelId == null || contentModelId.length() < 1) {
                throw new MissingAttributeValueException("No content model id found.", e);
            }
            if (!href.substring(0, indexOfLastSlash + 1).equalsIgnoreCase(Constants.CONTENT_MODEL_URL_BASE)) {
                throw new ContentModelNotFoundException("The " + Elements.ELEMENT_CONTENT_MODEL
                    + " element has a wrong url." + "the url have to look like: " + Constants.CONTENT_MODEL_URL_BASE
                    + "[id] ", e);
            }
        }
        this.properties.getObjectProperties().setContentModelId(contentModelId);

    }

    /**
     * @param element StAX StartElement
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     */
    private void handleOrigin(final StartElement element) throws MissingAttributeValueException,
        InvalidContentException {

        String originId;
        try {
            originId = element.getAttributeValue(null, Elements.ATTRIBUTE_XLINK_OBJID);
            if (originId == null || originId.length() < 1) {
                throw new MissingAttributeValueException("No origin id found.");
            }
        }
        catch (final NoSuchAttributeException e) {
            final String href;
            try {
                href = element.getAttributeValue(Constants.XLINK_NS_URI, Elements.ATTRIBUTE_XLINK_HREF);
            }
            catch (final NoSuchAttributeException e1) {
                final String refType = Elements.ELEMENT_ORIGIN;
                final String objType = "item";
                throw new MissingAttributeValueException("The attribute " + Elements.ATTRIBUTE_XLINK_HREF + " of "
                    + refType + " is missing in " + objType + " for create.", e);
            }
            final int indexOfLastSlash = href.lastIndexOf('/');
            originId = href.substring(indexOfLastSlash + 1);
            if (originId == null || originId.length() < 1) {
                throw new MissingAttributeValueException("No origin id found.", e);
            }
            if (!href.substring(0, indexOfLastSlash + 1).equalsIgnoreCase(Constants.ITEM_URL_BASE)) {
                throw new InvalidContentException("The " + Elements.ELEMENT_ORIGIN + " element has a wrong url."
                    + "the url have to look like: " + Constants.ITEM_URL_BASE + "[id] ", e);
            }
        }
        this.properties.getObjectProperties().setOrigin(originId);

    }

    /**
     * Convert status from String to Enum type.
     *
     * @param type object/version status type as String.
     * @return StatusType
     * @throws InvalidStatusException Thrown if unknown or invalid status type was set.
     */
    private static StatusType getStatusType(final String type) throws InvalidStatusException {

        if (type != null) {
            if (type.equals(StatusType.PENDING.toString())) {
                return StatusType.PENDING;
            }
            else if (type.equals(StatusType.RELEASED.toString())) {
                return StatusType.RELEASED;
            }
            else if (type.equals(StatusType.SUBMITTED.toString())) {
                return StatusType.SUBMITTED;
            }
            else if (type.equals(StatusType.WITHDRAWN.toString())) {
                return StatusType.WITHDRAWN;
            }
            else if (type.equals(StatusType.INREVISION.toString())) {
                return StatusType.INREVISION;
            }
        }
        throw new InvalidStatusException("Invalid status '" + type + '\'');
    }
}
