/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.business.stax.handler.context;

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithText;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.Map;
import java.util.TreeMap;

public class DcUpdateHandler extends DefaultHandler {

    private final StaxParser parser;

    private final Map<String, StartElementWithText> props;

    private static final String PATH = "/dc/";

    public DcUpdateHandler(final StaxParser parser) {
        this.parser = parser;
        this.props = new TreeMap<String, StartElementWithText>();
    }

    public DcUpdateHandler(final Map<String, StartElementWithText> props, final StaxParser parser) {
        this.parser = parser;
        this.props = new TreeMap<String, StartElementWithText>(props);
    }

    public Map<String, StartElementWithText> getProperties() {
        return this.props;
    }

    public DcUpdateHandler(final String elementName, final StartElementWithChildElements element,
        final StaxParser parser) {
        this.parser = parser;
        this.props = new TreeMap<String, StartElementWithText>();
        this.props.put(elementName, element);
    }

    @Override
    public String characters(final String data, final StartElement element) {
        String newData = data;
        if (!props.isEmpty()) {
            final String curPath = parser.getCurPath();
            final String theKey = element.getLocalName();
            if (curPath.endsWith(PATH + theKey) && props.containsKey(theKey)) {
                // update propertie and remove the used value
                final StartElementWithText replacementElement = props.get(theKey);
                String curElementNamespace = element.getNamespace();
                String replacementElementNamespace = replacementElement.getNamespace();

                // namespaces must not be null for testing
                if (curElementNamespace == null) {
                    curElementNamespace = "";
                }
                if (replacementElementNamespace == null) {
                    replacementElementNamespace = "";
                }
                if (curElementNamespace.endsWith("/") && !replacementElementNamespace.endsWith("/")) {
                    replacementElementNamespace += "/";
                }
                if (curElementNamespace.equals(replacementElementNamespace)) {
                    // replacement is equal to current element

                    newData = replacementElement.getElementText();
                    props.remove(theKey);
                }

            }
        }
        return newData;
    }

}
