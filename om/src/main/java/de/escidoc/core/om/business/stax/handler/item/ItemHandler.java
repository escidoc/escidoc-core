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

import de.escidoc.core.common.business.fedora.resources.create.ItemCreate;
import de.escidoc.core.common.business.stax.handler.common.ContentStreamsHandler;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Handle Item XML to obtain all required values (Properties, Metadata, Components, Content-Model-Specific, .. ).
 *
 * @author Steffen Wagner
 */
public class ItemHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemHandler.class);

    private boolean surrogate;

    private static final String XPATH_ITEM = "/item";

    private static final String XPATH_ITEM_PROPERTIES = XPATH_ITEM + "/properties";

    private static final String XPATH_ITEM_METADATA = XPATH_ITEM + "/md-records/md-record";

    private static final String XPATH_ITEM_COMPONENTS = XPATH_ITEM + "/components";

    private static final String XPATH_ITEM_CONTENT_STREAMS = XPATH_ITEM + "/content-streams";

    private static final String XPATH_ITEM_RELATION = XPATH_ITEM + "/relations/relation";

    private final StaxParser parser;

    private boolean parsingProperties;

    private boolean parsingMetaData;

    private boolean parsingContentStreams;

    private boolean parsingComponents;

    private boolean parsingRelations;

    private ItemPropertiesHandler propertiesHandler;

    private ContentStreamsHandler contentStreamsHandler;

    private ComponentsHandler componentsHandler;

    private MetadataHandler2 metadataHandler;

    private RelationHandler2 relationHandler;

    private final ItemCreate item;

    /**
     * ItemHandler.
     *
     * @param parser StAX Parser.
     * @throws WebserverSystemException If internal error occurs.
     */
    public ItemHandler(final StaxParser parser) {

        this.parser = parser;
        this.item = new ItemCreate();
    }

    /**
     * Parser hits an XML start element.
     *
     * @param element StAX Parser StartElement
     * @return StartElement The StartElement.
     */
    @Override
    public StartElement startElement(final StartElement element) throws InvalidContentException,
        MissingAttributeValueException, XMLStreamException, ReadonlyAttributeViolationException,
        ReadonlyElementViolationException, ContentModelNotFoundException, ContextNotFoundException,
        WebserverSystemException, IOException {

        if (this.parsingProperties) {
            this.propertiesHandler.startElement(element);
        }
        else if (this.parsingMetaData) {
            this.metadataHandler.startElement(element);
        }
        else if (this.parsingContentStreams) {
            this.contentStreamsHandler.startElement(element);
        }
        else if (this.parsingComponents) {
            this.componentsHandler.startElement(element);
        }
        else if (this.parsingRelations) {
            this.relationHandler.startElement(element);
        }
        else {
            final String currentPath = parser.getCurPath();

            if (XPATH_ITEM_PROPERTIES.equals(currentPath)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Parser reached " + XPATH_ITEM_PROPERTIES);
                }
                this.parsingProperties = true;
                this.propertiesHandler = new ItemPropertiesHandler(this.parser);
                this.propertiesHandler.startElement(element);
            }
            else if (XPATH_ITEM_METADATA.equals(currentPath)) {
                this.parsingMetaData = true;
                this.metadataHandler = new MetadataHandler2(this.parser, XPATH_ITEM_METADATA);
                this.metadataHandler.startElement(element);
            }
            else if (!this.surrogate && XPATH_ITEM_COMPONENTS.equals(currentPath)) {
                this.parsingComponents = true;
                this.componentsHandler = new ComponentsHandler(this.parser);
                this.componentsHandler.startElement(element);
            }
            else if (XPATH_ITEM_RELATION.equals(currentPath)) {
                this.parsingRelations = true;
                this.relationHandler = new RelationHandler2(this.parser, XPATH_ITEM_RELATION);
                this.relationHandler.startElement(element);
            }
            else if (!this.surrogate && XPATH_ITEM_CONTENT_STREAMS.equals(currentPath)) {
                this.parsingContentStreams = true;
                this.contentStreamsHandler = new ContentStreamsHandler(this.parser);
                this.contentStreamsHandler.startElement(element);
            }
        }

        return element;
    }

    /**
     * Parser hits an XML end element.
     *
     * @param element StAX EndElement
     * @return StAX EndElement
     */
    @Override
    public EndElement endElement(final EndElement element) throws MissingContentException, XMLStreamException,
        MissingAttributeValueException, ContextNotFoundException, ContentModelNotFoundException,
        UnsupportedEncodingException, InvalidContentException, WebserverSystemException, IntegritySystemException,
        TripleStoreSystemException, XmlCorruptedException {

        final String currentPath = parser.getCurPath();

        if (XPATH_ITEM_PROPERTIES.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached end of " + XPATH_ITEM_PROPERTIES);
            }
            // parser leaves the XML component element
            this.parsingProperties = false;
            this.propertiesHandler.endElement(element);
            this.item.setProperties(this.propertiesHandler.getProperties());
            if (this.propertiesHandler.getProperties().getObjectProperties().getOrigin() != null) {
                this.surrogate = true;
            }
            this.propertiesHandler = null;
        }
        else if (XPATH_ITEM_METADATA.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached end of " + XPATH_ITEM_METADATA);
            }
            // parser leaves the XML md-records element
            this.parsingMetaData = false;
            this.metadataHandler.endElement(element);
            this.item.addMdRecord(this.metadataHandler.getMetadataRecord());
            this.metadataHandler = null;
        }
        else if (!this.surrogate && XPATH_ITEM_COMPONENTS.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached end of " + XPATH_ITEM_COMPONENTS);
            }
            this.parsingComponents = false;
            this.componentsHandler.endElement(element);
            this.item.setComponents(this.componentsHandler.getComponents());
            this.componentsHandler = null;
        }
        else if (XPATH_ITEM_RELATION.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached end of " + XPATH_ITEM_RELATION);
            }
            this.parsingRelations = false;
            this.relationHandler.endElement(element);
            // if (this.relationHandler.getRelation() != null) {
            this.item.getRelations().add(this.relationHandler.getRelation());
            // }
            this.relationHandler = null;
        }
        else if (!this.surrogate && XPATH_ITEM_CONTENT_STREAMS.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached end of " + XPATH_ITEM_CONTENT_STREAMS);
            }
            this.parsingContentStreams = false;
            this.contentStreamsHandler.endElement(element);
            this.item.setContentStreams(this.contentStreamsHandler.getContentStreams());
            this.contentStreamsHandler = null;
        }
        else {
            if (this.parsingComponents) {
                this.componentsHandler.endElement(element);
            }
            else if (this.parsingProperties) {
                this.propertiesHandler.endElement(element);
            }
            else if (this.parsingMetaData) {
                this.metadataHandler.endElement(element);
            }
            else if (this.parsingRelations) {
                this.relationHandler.endElement(element);
            }
            else if (this.parsingContentStreams) {
                this.contentStreamsHandler.endElement(element);
            }
        }

        return element;
    }

    /**
     * Parser hits an XML character element.
     *
     * @param s       XML character element.
     * @param element StAX StartElement
     * @return XML character element.
     */
    @Override
    public String characters(final String s, final StartElement element) throws InvalidContentException,
        MissingElementValueException, WebserverSystemException, XMLStreamException, InvalidStatusException {

        if (this.parsingProperties) {
            this.propertiesHandler.characters(s, element);
        }
        else if (this.parsingComponents) {
            this.componentsHandler.characters(s, element);
        }
        else if (this.parsingMetaData) {
            this.metadataHandler.characters(s, element);
        }
        else if (this.parsingContentStreams) {
            this.contentStreamsHandler.characters(s, element);
        }

        return s;
    }

    /**
     * Get the Item.
     * <p/>
     * Attention! ItemCreate is only a transition object. Later implementation has to return the Item class.
     *
     * @return ItemCreate
     */
    public ItemCreate getItem() {

        return this.item;
    }

}
