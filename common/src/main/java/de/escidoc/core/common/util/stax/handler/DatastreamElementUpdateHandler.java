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
package de.escidoc.core.common.util.stax.handler;

import java.util.Map;
import java.util.TreeMap;

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithText;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class DatastreamElementUpdateHandler extends DefaultHandler {

    private StaxParser parser;

    private TreeMap props;

    private String path;

    private boolean inside = false;

    private int insideLevel;

    private boolean parsed = false;

    public DatastreamElementUpdateHandler(StaxParser parser, String path) {
        this.parser = parser;
        this.props = new TreeMap();
        this.path = path;
    }

    public DatastreamElementUpdateHandler(Map props, StaxParser parser,
        String path) {
        this.parser = parser;
        this.props = new TreeMap(props);
        this.path = path;
    }

    public TreeMap getProperties() {
        return this.props;
    }

    public DatastreamElementUpdateHandler(String elementName,
        StartElementWithChildElements element, StaxParser parser, String path) {
        this.parser = parser;
        this.props = new TreeMap();
        this.props.put(elementName, element);
        this.path = path;
    }

    public StartElement startElement(StartElement element) {

        if (!parsed) {
            String theName = element.getLocalName();
            String currenrPath = parser.getCurPath();

            if (path.equals(currenrPath)) {
                inside = true;
                insideLevel++;
                // String namespace = element.getNamespace();
                // properties.put("namespaceUri", namespace);

            }
            else if (inside) {
                insideLevel++;
            }
        }
        return element;
    }

    public EndElement endElement(EndElement element) {
        if (!parsed) {
            String theName = element.getLocalName();
            if (inside) {

                insideLevel--;

                if (insideLevel == 0) {
                    inside = false;
                    parsed = true;
                    // return null;

                }
            }
        }
        return element;
    }

    public String characters(String data, StartElement element)
        throws Exception {
        if (!parsed) {
            if (props.size() > 0) {
                String theKey = element.getLocalName();

                if (inside && props.containsKey(theKey)) {
                    // update propertie and remove the used value
                    StartElementWithText replacementElement =
                        (StartElementWithText) props.get(theKey);

                    if (element.getNamespace().equals(
                        replacementElement.getNamespace())) {
                        if (theKey.equals("latest-revision.number")) {
                            data = String.valueOf(Integer.parseInt(data) + 1);
                        }
                        data = replacementElement.getElementText();
                        props.remove(theKey);
                    }

                }
            }
        }
        return data;
    }

}
