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
package de.escidoc.core.cmm.business.stax.handler.contentModel;

import de.escidoc.core.common.business.fedora.resources.create.ContentModelCreate;
import de.escidoc.core.common.business.stax.handler.common.ContentStreamsHandler;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Handle Item XML to obtain all required values (Properties, Metadata, Components, Content-Model-Specific, .. ).
 *
 * @author Steffen Wagner
 */
public class ContentModelCreateHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentModelCreateHandler.class);

    private static final String XPATH_CONTENT_MODEL = "/content-model";

    private static final String XPATH_CONTENT_MODEL_PROPERTIES = XPATH_CONTENT_MODEL + "/properties";

    private static final String XPATH_CONTENT_MODEL_METADATA = XPATH_CONTENT_MODEL + "/md-record-definitions";

    private static final String XPATH_CONTENT_MODEL_RESOURCE_DEFINITIONS =
        XPATH_CONTENT_MODEL + "/resource-definitions";

    private static final String XPATH_CONTENT_MODEL_CONTENT_STREAMS = XPATH_CONTENT_MODEL + "/content-streams";

    private final StaxParser parser;

    private boolean parsingProperties;

    private boolean parsingMetaData;

    private boolean parsingResourceDefinitions;

    private boolean parsingContentStreams;

    private ContentModelPropertiesHandler propertiesHandler;

    private ContentStreamsHandler contentStreamsHandler;

    private ResourceDefinitionHandler resourceDefinitionHandler;

    private MdRecordDefinitionHandler metadataHandler;

    private final ContentModelCreate contentModel;

    /**
     * ItemHandler.
     *
     * @param parser StAX Parser.
     * @throws WebserverSystemException If internal error occurs.
     */
    public ContentModelCreateHandler(final StaxParser parser) {

        this.parser = parser;
        this.contentModel = new ContentModelCreate();
    }

    /**
     * Parser hits an XML start element.
     *
     * @param element StAX Parser StartElement
     * @return StartElement The StartElement.
     */
    @Override
    public StartElement startElement(final StartElement element) throws InvalidContentException,
        MissingAttributeValueException, ReadonlyAttributeViolationException, ReadonlyElementViolationException,
        ContentModelNotFoundException, ContextNotFoundException, WebserverSystemException {

        if (this.parsingProperties) {
            this.propertiesHandler.startElement(element);
        }
        else if (this.parsingMetaData) {
            this.metadataHandler.startElement(element);
        }
        else if (this.parsingResourceDefinitions) {
            this.resourceDefinitionHandler.startElement(element);
        }
        else if (this.parsingContentStreams) {
            this.contentStreamsHandler.startElement(element);
        }
        else {
            final String currentPath = parser.getCurPath();

            if (XPATH_CONTENT_MODEL_PROPERTIES.equals(currentPath)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Parser reached " + XPATH_CONTENT_MODEL_PROPERTIES);
                }
                this.parsingProperties = true;
                this.propertiesHandler = new ContentModelPropertiesHandler(this.parser);
                this.propertiesHandler.startElement(element);
            }
            else if (XPATH_CONTENT_MODEL_METADATA.equals(currentPath)) {
                this.parsingMetaData = true;
                this.metadataHandler = new MdRecordDefinitionHandler(this.parser, XPATH_CONTENT_MODEL_METADATA);
                this.metadataHandler.startElement(element);
            }
            else if (XPATH_CONTENT_MODEL_RESOURCE_DEFINITIONS.equals(currentPath)) {
                this.parsingResourceDefinitions = true;
                this.resourceDefinitionHandler =
                    new ResourceDefinitionHandler(this.parser, XPATH_CONTENT_MODEL_RESOURCE_DEFINITIONS);
                this.resourceDefinitionHandler.startElement(element);
            }
            else if (XPATH_CONTENT_MODEL_CONTENT_STREAMS.equals(currentPath)) {
                this.parsingContentStreams = true;
                this.contentStreamsHandler =
                    new ContentStreamsHandler(this.parser, XPATH_CONTENT_MODEL_CONTENT_STREAMS);
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
    public EndElement endElement(final EndElement element) throws MissingContentException, InvalidXmlException,
        MissingAttributeValueException, ContextNotFoundException, ContentModelNotFoundException,
        UnsupportedEncodingException, InvalidContentException, WebserverSystemException {

        final String currentPath = parser.getCurPath();

        if (XPATH_CONTENT_MODEL_PROPERTIES.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached end of " + XPATH_CONTENT_MODEL_PROPERTIES);
            }
            // parser leaves the XML component element
            this.parsingProperties = false;
            this.propertiesHandler.endElement(element);
            this.contentModel.setProperties(this.propertiesHandler.getProperties());
            this.propertiesHandler = null;
        }
        else if (XPATH_CONTENT_MODEL_METADATA.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached end of " + XPATH_CONTENT_MODEL_METADATA);
            }
            // parser leaves the XML md-records element
            this.parsingMetaData = false;
            this.contentModel.setMdRecordDefinitions(this.metadataHandler.getMdRecordDefinitions());
            this.metadataHandler = null;
        }
        else if (XPATH_CONTENT_MODEL_CONTENT_STREAMS.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached end of " + XPATH_CONTENT_MODEL_CONTENT_STREAMS);
            }
            this.parsingContentStreams = false;
            this.contentModel.setContentStreams(this.contentStreamsHandler.getContentStreams());
            this.contentStreamsHandler = null;
        }
        else if (XPATH_CONTENT_MODEL_RESOURCE_DEFINITIONS.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached end of " + XPATH_CONTENT_MODEL_RESOURCE_DEFINITIONS);
            }
            this.parsingResourceDefinitions = false;
            this.contentModel.setResourceDefinitions(this.resourceDefinitionHandler.getResourceDefinitions());
            this.resourceDefinitionHandler = null;
        }
        else {
            if (this.parsingProperties) {
                this.propertiesHandler.endElement(element);
            }
            else if (this.parsingMetaData) {
                this.metadataHandler.endElement(element);
            }
            else if (this.parsingResourceDefinitions) {
                this.resourceDefinitionHandler.endElement(element);
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
        MissingElementValueException, WebserverSystemException, InvalidStatusException {

        if (this.parsingProperties) {
            this.propertiesHandler.characters(s, element);
        }
        else if (this.parsingResourceDefinitions) {
            this.resourceDefinitionHandler.characters(s, element);
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
     * @return Component
     */
    public ContentModelCreate getContentModel() {

        return this.contentModel;
    }

}
