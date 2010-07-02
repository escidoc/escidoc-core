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

import java.io.IOException;
import java.util.Vector;

import javax.xml.stream.XMLStreamException;

import de.escidoc.core.common.business.fedora.resources.create.ComponentCreate;
import de.escidoc.core.common.business.fedora.resources.create.ItemCreate;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * Handle Item XML to obtain Components.
 * 
 * This handler obtains all (required) values of each Component of an Item and
 * handle is within Component objects.
 * 
 * @author SWA
 * 
 */
public class ComponentsHandler extends DefaultHandler {

    private static final AppLogger LOG =
        new AppLogger(ComponentsHandler.class.getName());

    private static final String XPATH_COMPONENTS = "/item/components";

    private static final String XPATH_COMPONENT =
        XPATH_COMPONENTS + "/component";

    private StaxParser parser;

    private Vector<ComponentCreate> components = new Vector<ComponentCreate>();

    private boolean parsingComponent = false;

    private ComponentHandler componentHandler = null;

    private ItemCreate item;

    /**
     * ComponentsHandler.
     * 
     * @param parser
     *            StAX parser
     */
    public ComponentsHandler(final StaxParser parser, final ItemCreate item) {
        this.parser = parser;
        this.item = item;
    }

    /**
     * Parser hits an XML start element.
     * 
     * @param element
     *            StAX StartElement.
     * @return StAX StartElement
     * @throws MissingAttributeValueException
     * @throws XMLStreamException
     * @throws WebserverSystemException
     * @throws IOException
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws InvalidContentException, MissingAttributeValueException,
        XMLStreamException, WebserverSystemException, IOException {

        if (this.parsingComponent) {
            this.componentHandler.startElement(element);
        }
        else {
            String currentPath = parser.getCurPath();

            if (XPATH_COMPONENT.equals(currentPath)) {

                LOG.debug("Parsing " + XPATH_COMPONENT);
                // creating a new Component shows that the parser is within a
                // component element.
                this.parsingComponent = true;
                this.componentHandler = new ComponentHandler(parser, this.item);
                this.componentHandler.startElement(element);
            }
        }

        return element;
    }

    /**
     * Parser hits an XML end element.
     * 
     * @param element
     *            StAX EndElement
     * @return StAX EndElement
     * @throws MissingContentException
     * @throws XMLStreamException
     */
    @Override
    public EndElement endElement(final EndElement element)
        throws MissingContentException, WebserverSystemException {

        String currentPath = parser.getCurPath();

        if (XPATH_COMPONENT.equals(currentPath)) {
            LOG.debug("Reached end of " + XPATH_COMPONENT);

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
     * @param s
     *            Character of StAX character element
     * @param element
     *            StAX StartElement
     * @return Character of StAX character element
     * @throws XMLStreamException
     * 
     */
    @Override
    public String characters(final String s, final StartElement element)
        throws InvalidContentException, MissingElementValueException,
        WebserverSystemException, XMLStreamException {

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
    public Vector<ComponentCreate> getComponents() {

        return this.components;
    }

}
