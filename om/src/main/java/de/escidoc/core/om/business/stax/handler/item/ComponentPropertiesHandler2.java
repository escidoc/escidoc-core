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

import de.escidoc.core.common.business.fedora.resources.create.ComponentProperties;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * Handles the Properties of one single Component.
 *
 * @author Steffen Wagner
 */
public class ComponentPropertiesHandler2 extends DefaultHandler {

    private static final String XPATH_COMPONENT_PROPERTIES = "/item/components/component/properties";

    private final ComponentProperties properties;

    private boolean inside;

    private final StaxParser parser;

    /**
     * StAX Handler for Component Properties. Extracts all required values from from Component Properties XML Section by
     * handle Events from StAX-Parser. The values are stored within a internal HashMap.
     * @param parser
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    public ComponentPropertiesHandler2(final StaxParser parser) throws WebserverSystemException {
        this.parser = parser;
        this.properties = new ComponentProperties();
    }

    /**
     * Get the Properties of the single Component as Map.
     *
     * @return Component Properties Map
     */
    public ComponentProperties getProperties() {
        return this.properties;
    }

    /**
     *
     */
    @Override
    public StartElement startElement(final StartElement element) {

        if (!this.inside) {
            final String currentPath = parser.getCurPath();
            if (currentPath.startsWith(XPATH_COMPONENT_PROPERTIES)) {
                this.inside = true;
            }
        }
        return element;
    }

    /**
     *
     */
    @Override
    public EndElement endElement(final EndElement element) {

        if (this.inside) {
            final String currentPath = parser.getCurPath();
            if (currentPath.startsWith(XPATH_COMPONENT_PROPERTIES)) {
                this.inside = false;
            }
        }
        return element;
    }

    /**
     *
     */
    @Override
    public String characters(final String s, final StartElement element) throws MissingElementValueException,
        InvalidContentException, WebserverSystemException {

        if (this.inside) {
            final String currentPath = element.getLocalName();

            if (currentPath.equals(Elements.ELEMENT_MIME_TYPE)) {
                if (s != null && s.trim().length() > 0) {
                    this.properties.setMimeType(s);
                }
            }
            else if (currentPath.equals(Elements.ELEMENT_VALID_STATUS)) {
                if (s != null && s.length() > 0) {
                    this.properties.setValidStatus(s);
                }
            }
            else if (currentPath.equals(Elements.ELEMENT_VISIBILITY)) {
                handleVisibility(s, currentPath);
            }
            else if (currentPath.equals(Elements.ELEMENT_COMPONENT_CONTENT_CATEGORY)) {

                handleContentCatagory(s, currentPath);
            }
        }

        return s;
    }

    /**
     *
     * @param s
     * @param currentPath
     * @throws MissingElementValueException
     */
    private void handleVisibility(final String s, final String currentPath) throws MissingElementValueException {

        if (s != null && s.length() > 0) {
            this.properties.setVisibility(s);
        }
        else {
            throw new MissingElementValueException("The value of element " + currentPath + " is missing");
        }
    }

    /**
     *
     * @param s
     * @param currentPath
     * @throws MissingElementValueException
     */
    private void handleContentCatagory(final String s, final String currentPath) throws MissingElementValueException {

        if (s != null && s.length() > 0) {
            this.properties.setContentCatagory(s);
        }
        else {
            throw new MissingElementValueException("The value of element " + currentPath + " is missing");
        }

    }
}
