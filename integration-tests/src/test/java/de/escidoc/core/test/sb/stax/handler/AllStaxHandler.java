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
package de.escidoc.core.test.sb.stax.handler;

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import de.escidoc.core.test.EscidocTestBase;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Fills all elements + attributes and values in list.
 *
 * @author Michael Hoppe
 */
public class AllStaxHandler extends DefaultHandler {

    private final StaxParser parser;

    /**
     * Holds values of all elements and attributes as key-value pairs. Key is path to element or attribute eg
     * /properties/version/number If attribute is href, extract objectId out of href and write it as key id If element
     * is root-element, write root-element name as value with key = type.
     */
    private List<String> values = new ArrayList<String>();

    /**
     * Constructor with StaxParser.
     *
     * @param parser StaxParser
     */
    public AllStaxHandler(final StaxParser parser) {
        this.parser = parser;
    }

    /**
     * Parser hits an XML character element. Write all elements and all attributes with path to element and value (eg
     * /properties/version/status=released) in a list. If attribute is xlink:href, extract objectId out of href and
     * replace attribute-name in path with /id.
     *
     * @param s       XML character element.
     * @param element StAX StartElement
     * @return XML character element.
     */
    @Override
    public String characters(final String s, final StartElement element) throws UnsupportedEncodingException {

        String path = parser.getCurPath().replaceFirst("/.*?/", "/");
        if (path.equals(parser.getCurPath())) {
            path = "";
            values.add("\"type\"=\"" + parser.getCurPath().substring(1) + "\"");
        }
        else {
            if (s != null && !s.trim().equals("")) {
                values.add("\"" + path + "\"=\"" + s + "\"");
            }
        }
        List<Attribute> attributes = element.getAttributes();
        for (Attribute attribute : attributes) {
            if (attribute.getValue() != null && !attribute.getValue().trim().equals("")) {
                String value = attribute.getValue();
                String namespace = attribute.getNamespace();
                String localName = attribute.getLocalName();
                if (namespace != null && EscidocTestBase.XLINK_NS_URI.equals(namespace)) {
                    if (attribute.getLocalName().equals("href")) {
                        localName = "id";
                        value = value.replaceFirst(".*/", "");
                        if (path.equals("")) {
                            value = value.replaceFirst("(.*?:.*?):.*", "$1");
                        }
                        if (!value.contains(":")) {
                            value = "";
                        }
                    }
                    else {
                        value = "";
                    }
                }
                if (!value.equals("")) {
                    values.add("\"" + path + "/" + localName + "\"=\"" + value + "\"");
                }
            }
        }

        return s;
    }

    /**
     * Get the values.
     *
     * @return List with searches.
     */
    public List<String> getValues() {
        return values;
    }

}
