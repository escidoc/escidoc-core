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

import de.escidoc.core.common.business.fedora.resources.create.ComponentCreate;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Handle Item XML to obtain Components.
 * <p/>
 * This handler obtains all (required) values of each Component of an Item and handle is within Component objects.
 *
 * @author Steffen Wagner
 */
public class ComponentsHandler extends DefaultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentsHandler.class);

    private static final String XPATH_COMPONENTS = "/item/components";

    private static final String XPATH_COMPONENT = XPATH_COMPONENTS + "/component";

    private final StaxParser parser;

    private final List<ComponentCreate> components = new ArrayList<ComponentCreate>();

    private boolean parsingComponent;

    private ComponentHandler componentHandler;

    /**
     * ComponentsHandler.
     *
     * @param parser StAX parser
     */
    public ComponentsHandler(final StaxParser parser) {
        this.parser = parser;
    }

    /**
     * Parser hits an XML start element.
     *
     * @param element StAX StartElement.
     * @return StAX StartElement
     */
    @Override
    public StartElement startElement(final StartElement element) throws InvalidContentException,
        MissingAttributeValueException, XMLStreamException, WebserverSystemException, IOException {

        if (this.parsingComponent) {
            this.componentHandler.startElement(element);
        }
        else {
            final String currentPath = parser.getCurPath();

            if (XPATH_COMPONENT.equals(currentPath)) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Parsing " + XPATH_COMPONENT);
                }
                // creating a new Component shows that the parser is within a
                // component element.
                this.parsingComponent = true;
                this.componentHandler = new ComponentHandler(this.parser);
                this.componentHandler.startElement(element);
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
    public EndElement endElement(final EndElement element) throws MissingContentException, WebserverSystemException {

        final String currentPath = parser.getCurPath();

        if (XPATH_COMPONENT.equals(currentPath)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Reached end of " + XPATH_COMPONENT);
            }
            // parser leaves the XML component element
            this.parsingComponent = false;
            this.componentHandler.endElement(element);
            this.components.add(this.componentHandler.getComponent());
        }
        else if (this.parsingComponent) {
            this.componentHandler.endElement(element);
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

        if (this.parsingComponent) {
            this.componentHandler.characters(s, element);
        }

        return s;
    }

    /**
     * Get all Components of the ComponentHandler.
     *
     * @return Vector with all Components.
     */
    public List<ComponentCreate> getComponents() {

        return this.components;
    }

}
