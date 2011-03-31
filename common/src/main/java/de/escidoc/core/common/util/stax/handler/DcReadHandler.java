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

/**
 *
 */
package de.escidoc.core.common.util.stax.handler;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Frank Schwichtenberg
 */
public class DcReadHandler extends DefaultHandler {

    private final StaxParser parser;

    private static final String DC_PATH = "/dc";

    private final Map<String, String> properties = new HashMap<String, String>();

    /**
     *
     * @param parser
     */
    public DcReadHandler(final StaxParser parser) {
        this.parser = parser;
    }

    @Override
    public String characters(final String data, final StartElement element) throws IntegritySystemException {
        final String curPath = parser.getCurPath();
        final String theName = element.getLocalName();

        // organizational-unit
        if (curPath.equals(DC_PATH + '/' + Elements.ELEMENT_DC_TITLE)) {
            if (data.length() == 0) {
                properties.put(Elements.ELEMENT_DC_TITLE, "");
                properties.put(TripleStoreUtility.PROP_DC_TITLE, "");
            }
            else {
                // propertiesMap.put(theName, data);
                properties.put(Elements.ELEMENT_DC_TITLE, data);
                properties.put(TripleStoreUtility.PROP_DC_TITLE, data);
            }
        }
        else if (theName.equals(Elements.ELEMENT_DESCRIPTION)) {

            if (data.length() == 0) {

                properties.put(Elements.ELEMENT_DESCRIPTION, "");
                properties.put(TripleStoreUtility.PROP_DC_DESCRIPTION, "");
            }
            else {
                // propertiesMap.put(theName, data);
                properties.put(Elements.ELEMENT_DESCRIPTION, data);
                properties.put(TripleStoreUtility.PROP_DC_DESCRIPTION, data);
            }

        }
        return data;
    }

    /**
     * Return property elements as HashMap.
     *
     * @return map of properties without organizational units.
     */
    public Map<String, String> getPropertiesMap() {
        return this.properties;
    }

}
