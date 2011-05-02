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
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler to obtain the properties for one Component.
 *
 * @author ??
 */
public class OneComponentPropertiesHandler extends DefaultHandler {

    private boolean inside;

    private int insideLevel;

    private final StaxParser parser;

    private final Map<String, String> properties = new HashMap<String, String>();

    /**
     * OneComponentPropertiesHandler.
     *
     * @param parser StAX parser.
     */
    public OneComponentPropertiesHandler(final StaxParser parser) {
        this.parser = parser;

    }

    /**
     * Get Properties of Component.
     *
     * @return Map with properties of Component.
     */
    public Map<String, String> getProperties() {
        return this.properties;
    }

    @Override
    public StartElement startElement(final StartElement element) throws ReadonlyElementViolationException,
        ReadonlyAttributeViolationException, InvalidContentException {

        if (this.inside) {

            this.insideLevel++;

        }
        else {
            final String currenrPath = parser.getCurPath();
            final String elementPath = "/component/properties";
            if (elementPath.equals(currenrPath)) {
                this.inside = true;
                this.insideLevel++;

            }
        }

        return element;
    }

    @Override
    public EndElement endElement(final EndElement element) {
        if (this.inside) {
            this.insideLevel--;

            if (this.insideLevel == 0) {
                this.inside = false;
            }
        }
        return element;
    }

    @Override
    public String characters(final String s, final StartElement element) throws MissingElementValueException,
        InvalidContentException, WebserverSystemException {

        final String theName = element.getLocalName();

        if (this.inside) {
            if (theName.equals(Elements.ELEMENT_MIME_TYPE)) {
                if (s != null && s.length() > 0) {
                    properties.put(TripleStoreUtility.PROP_MIME_TYPE, s);
                }
            }
            else if (theName.equals(Elements.ELEMENT_VALID_STATUS)) {
                if (s != null && s.length() > 0) {
                    properties.put(TripleStoreUtility.PROP_VALID_STATUS, s);
                }
            }
            else if (theName.equals(Elements.ELEMENT_VISIBILITY)) {
                if (s != null && s.length() > 0) {
                    properties.put(TripleStoreUtility.PROP_VISIBILITY, s);
                }
                else {
                    throw new MissingElementValueException("The value of element " + theName + " is missing");
                }
            }
            else if (theName.equals(Elements.ELEMENT_COMPONENT_CONTENT_CATEGORY)) {
                if (s != null && s.length() > 0) {
                    properties.put(TripleStoreUtility.PROP_COMPONENT_CONTENT_CATEGORY, s);
                }
                else {
                    throw new MissingElementValueException("The value of element " + theName + " is missing");
                }
            }
        }

        return s;
    }

}
