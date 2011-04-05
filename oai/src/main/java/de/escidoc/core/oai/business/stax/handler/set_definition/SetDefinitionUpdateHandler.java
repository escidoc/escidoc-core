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
package de.escidoc.core.oai.business.stax.handler.set_definition;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class SetDefinitionUpdateHandler extends DefaultHandler {

    private static final String PROPERTIES_PATH = "/set-definition/properties";

    private boolean inProperties;

    private final StaxParser parser;

    private final Map<String, String> setDefinitionProperties = new HashMap<String, String>();

    public SetDefinitionUpdateHandler(final StaxParser parser) {
        this.parser = parser;

    }

    @Override
    public StartElement startElement(final StartElement element) {
        final String currenrPath = parser.getCurPath();
        if (PROPERTIES_PATH.equals(currenrPath)) {
            this.inProperties = true;
        }
        return element;
    }

    @Override
    public EndElement endElement(final EndElement element) {
        final String currenrPath = parser.getCurPath();
        if (PROPERTIES_PATH.equals(currenrPath)) {
            this.inProperties = false;
        }
        return element;
    }

    @Override
    public String characters(final String s, final StartElement element) throws XmlCorruptedException {

        final String theName = element.getLocalName();
        if (this.inProperties) {
            if (theName.equals(Elements.ELEMENT_NAME)) {
                if (s != null && s.length() > 0) {
                    setDefinitionProperties.put(Elements.ELEMENT_NAME, s);
                }
                else {
                    throw new XmlCorruptedException("the value of element " + theName + " is missing");
                }
            }
            else if (theName.equals(Elements.ELEMENT_DESCRIPTION) && s != null) {
                setDefinitionProperties.put(Elements.ELEMENT_DESCRIPTION, s);
            }
        }
        return s;
    }

    public Map<String, String> getSetProperties() {
        return this.setDefinitionProperties;
    }

}
