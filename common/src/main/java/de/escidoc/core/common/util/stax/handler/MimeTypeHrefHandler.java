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

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class MimeTypeHrefHandler extends DefaultHandler {

    private StaxParser parser;

    private Map binaryData = new HashMap();

    private boolean inComponent = false;

    private int componentNumber = 0;

    private static AppLogger log =
        new AppLogger(MimeTypeHrefHandler.class.getName());

    public MimeTypeHrefHandler(StaxParser parser) {
        this.parser = parser;
    }

    public Map getBinaryData() {
        return this.binaryData;
    }

    public StartElement startElement(StartElement element)
        throws ReadonlyAttributeViolationException, InvalidContentException {

        String elementPath = "/item/components/component/content";

        String componentPath = "/item/components/component";
        String currentPath = parser.getCurPath();

        if (currentPath.equals(componentPath)) {
            inComponent = true;
        }
        else {

            if (inComponent && elementPath.equals(currentPath)) {
                HashMap componentBinary = null;
                if (binaryData.containsKey(Integer.valueOf(componentNumber))) {
                    componentBinary =
                        (HashMap) binaryData.get(Integer.valueOf(componentNumber));
                }
                else {
                    componentBinary = new HashMap();
                    binaryData.put(Integer.valueOf(componentNumber),
                        componentBinary);
                }

                int indexOfHref =
                    element.indexOfAttribute(Constants.XLINK_URI, "href");
                if (indexOfHref != -1) {
                    Attribute href = element.getAttribute(indexOfHref);

                    componentBinary.put("href", href.getValue());
                }
            }

        }
        return null;
    }

    public EndElement endElement(EndElement element)
        throws MissingContentException {
        if (parser.getCurPath().equals("/item/components/component")) {

            componentNumber++;
            inComponent = false;
        }
        return null;
    }

    public String characters(String s, StartElement element)
        throws ReadonlyAttributeViolationException {
        HashMap componentBinary = null;
        if (inComponent) {
            if ((element.getLocalName()).equals("mime-type")) {
                if (binaryData.containsKey(Integer.valueOf(componentNumber))) {
                    componentBinary =
                        (HashMap) binaryData.get(Integer.valueOf(componentNumber));
                }
                else {
                    componentBinary = new HashMap();
                    binaryData.put(Integer.valueOf(componentNumber),
                        componentBinary);
                }

                componentBinary.put("mime-type", s);
            }
        }
        return null;
    }
}
