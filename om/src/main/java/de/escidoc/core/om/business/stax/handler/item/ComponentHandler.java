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
import de.escidoc.core.common.business.fedora.resources.create.BinaryContent;
import de.escidoc.core.common.business.fedora.resources.create.ComponentCreate;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * Handle Component XML to obtain all required Component values.
 *
 * @author Steffen Wagner
 */
public class ComponentHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentHandler.class);

    private static final String XPATH_COMPONENT = "/item/components/component";

    private static final String XPATH_COMPONENT_PROPERTIES = XPATH_COMPONENT + "/properties";

    private static final String XPATH_COMPONENT_METADATA = XPATH_COMPONENT + "/md-records/md-record";

    private static final String XPATH_COMPONENT_CONTENT = XPATH_COMPONENT + "/content";

    private final StaxParser parser;

    private boolean parsingProperties;

    private boolean parsingMetaData;

    private boolean parsingContent;

    private ComponentPropertiesHandler2 propertiesHandler;

    private MetadataHandler2 metadataHandler;

    private final ComponentCreate component;

    private BinaryContent content;

    /**
     * ComponentHandler.
     *
     * @param parser StAX parser.
     */
    public ComponentHandler(final StaxParser parser) {
        this.parser = parser;
        this.component = new ComponentCreate();
    }

    /**
     * Parser hits an XML start element.
     *
     * @param element StAX StartElement
     * @return StAX StartElement
     */
    @Override
    public StartElement startElement(final StartElement element) throws InvalidContentException,
        MissingAttributeValueException, XMLStreamException, WebserverSystemException, IOException {

        if (this.parsingProperties) {
            this.propertiesHandler.startElement(element);
        }
        else if (this.parsingMetaData) {
            this.metadataHandler.startElement(element);
        }
        else if (this.parsingContent) {
            return element;
        }
        else {
            final String currentPath = parser.getCurPath();
            if (XPATH_COMPONENT_PROPERTIES.equals(currentPath)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Parser reached " + XPATH_COMPONENT_PROPERTIES);
                }
                // creating a new Component shows that the parser is within a
                // component element.
                this.parsingProperties = true;
                this.propertiesHandler = new ComponentPropertiesHandler2(this.parser);
                this.propertiesHandler.startElement(element);
            }
            else if (XPATH_COMPONENT_METADATA.equals(currentPath)) {
                this.parsingMetaData = true;
                this.metadataHandler =
                    new MetadataHandler2(this.parser, "/item/components/component/md-records/md-record");
                this.metadataHandler.startElement(element);
            }
            else if (XPATH_COMPONENT_CONTENT.equals(currentPath)) {
                this.parsingContent = true;
                this.content = new BinaryContent();
                this.content.setStorageType(getAttributeValue(element, null, "storage"));
                this.content.setDataLocation(getAttributeValue(element, Constants.XLINK_NS_URI, "href"));
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
    public EndElement endElement(final EndElement element) throws WebserverSystemException {

        final String currentPath = parser.getCurPath();

        if (XPATH_COMPONENT_PROPERTIES.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached end of " + XPATH_COMPONENT_PROPERTIES);
            }
            // parser leaves the XML component element
            this.parsingProperties = false;
            this.propertiesHandler.endElement(element);
            this.component.setProperties(this.propertiesHandler.getProperties());
            this.propertiesHandler = null;
        }
        else if (XPATH_COMPONENT_METADATA.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Parser reached end of " + XPATH_COMPONENT_METADATA);
            }
            // parser leaves the XML md-records element
            this.parsingMetaData = false;

            this.metadataHandler.endElement(element);
            this.component.addMdRecord(this.metadataHandler.getMetadataRecord());
            this.metadataHandler = null;
        }
        else if (XPATH_COMPONENT_CONTENT.equals(currentPath)) {
            this.parsingContent = false;
            this.component.setContent(this.content);
        }
        else {
            if (this.parsingMetaData) {
                this.metadataHandler.endElement(element);
            }
        }
        return element;
    }

    /**
     * @param s       Character of StAX character element
     * @param element StAX StartElement
     * @return Character of StAX character element
     */
    @Override
    public String characters(final String s, final StartElement element) throws InvalidContentException,
        MissingElementValueException, WebserverSystemException, XMLStreamException {

        if (this.parsingProperties) {
            this.propertiesHandler.characters(s, element);
        }
        else if (this.parsingMetaData) {
            this.metadataHandler.characters(s, element);
        }
        else if (this.parsingContent) {
            this.content.setContent(s);
        }
        return s;
    }

    /**
     * Get the Component.
     * <p/>
     * Attention! ComponentCreate is only a transition object. Later implementation has to return the Component class.
     *
     * @return Component
     */
    public ComponentCreate getComponent() {

        return this.component;
    }
}
