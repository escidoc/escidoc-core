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

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Component Properties.
 * 
 * 
 */
public class ComponentPropertiesHandler extends DefaultHandler {

    private static final String ELEMENT_PATH =
        "/item/components/component/properties";

    private boolean inside = false;

    private int insideLevel = 0;

    private StaxParser parser;

    private List<String> componentIds;

    private int componentNumber = 0;

    private final Map<String, Map<String, String>> componentsProperties =
        new HashMap<String, Map<String, String>>();

    private String componentId;

    Map<String, Map<String, String>> componentsBinary =
        new HashMap<String, Map<String, String>>();

    private static AppLogger log =
        new AppLogger(ComponentPropertiesHandler.class.getName());

    public ComponentPropertiesHandler(final List<String> componentIds,
        final StaxParser parser) {

        this.componentIds = componentIds;

        this.parser = parser;
    }

    /*
     * 
     */
    public ComponentPropertiesHandler(final StaxParser parser) {
        this.parser = parser;

    }

    public Map<String, Map<String, String>> getComponentsBinary() {
        return this.componentsBinary;
    }

    public Map<String, Map<String, String>> getProperties() {
        return this.componentsProperties;
    }

    @Override
    public StartElement startElement(final StartElement element) {

        if (inside) {
            Map<String, String> properties = null;
            if (!componentsProperties.containsKey(componentId)) {
                properties = new HashMap<String, String>();
                componentsProperties.put(componentId, properties);
            }
            else {
                properties = componentsProperties.get(componentId);
            }
            insideLevel++;

        }
        else {

            String currenrPath = parser.getCurPath();

            if (ELEMENT_PATH.equals(currenrPath)) {

                inside = true;
                insideLevel++;
                componentId = (String) componentIds.get(componentNumber);

            }

        }
        return element;
    }

    @Override
    public EndElement endElement(final EndElement element) {
        if (inside) {
            insideLevel--;

            if (insideLevel == 0) {
                inside = false;
                componentNumber++;

            }
        }
        return element;
    }

    @Override
    public String characters(final String s, final StartElement element)
        throws MissingElementValueException, InvalidContentException,
        WebserverSystemException {

        String theName = element.getLocalName();

        if (inside) {
            Map<String, String> binaryData = null;
            Map<String, String> properties = null;
            if (!componentsBinary.containsKey(componentId)) {
                binaryData = new HashMap<String, String>();
                componentsBinary.put(componentId, binaryData);
            }
            else {
                binaryData = componentsBinary.get(componentId);
            }
            if (!componentsProperties.containsKey(componentId)) {
                properties = new HashMap<String, String>();
                componentsProperties.put(componentId, properties);
            }
            else {
                properties = componentsProperties.get(componentId);
            }
            if (theName.equals(Elements.ELEMENT_MIME_TYPE)) {
                if ((s != null) && s.length() > 0) {
                    binaryData.put(TripleStoreUtility.PROP_MIME_TYPE, s);
                    properties.put(TripleStoreUtility.PROP_MIME_TYPE, s);

                }
            }
            else if (theName.equals(Elements.ELEMENT_VALID_STATUS)) {
                if ((s != null) && (s.length() > 0)) {
                    properties.put(TripleStoreUtility.PROP_VALID_STATUS, s);
                }
            }
            else if (theName.equals(Elements.ELEMENT_VISIBILITY)) {
                if ((s != null) && (s.length() > 0)) {
                    properties.put(TripleStoreUtility.PROP_VISIBILITY, s);
                }
                else {
                    log
                        .debug("the value of element " + theName
                            + " is missing");
                    throw new MissingElementValueException(
                        "the value of element " + theName + " is missing");
                }
            }
            else if (theName
                .equals(Elements.ELEMENT_COMPONENT_CONTENT_CATEGORY)) {
                if ((s != null) && (s.length() > 0)) {
                    properties.put(
                        TripleStoreUtility.PROP_COMPONENT_CONTENT_CATEGORY, s);
                }
                else {
                    log
                        .debug("the value of element " + theName
                            + " is missing");
                    throw new MissingElementValueException(
                        "the value of element " + theName + " is missing");
                }
            }
        }
        return s;
    }

}
