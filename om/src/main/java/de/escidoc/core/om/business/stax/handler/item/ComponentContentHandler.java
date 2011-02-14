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

import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.Vector;

public class ComponentContentHandler extends DefaultHandler {

    private StaxParser parser;

    private String itemId;

    private Vector<String> componentIds;

    private HashMap<String, HashMap<String, String>> binaryData = new HashMap<String, HashMap<String, String>>();

    private String uploadUrl = null;

    private boolean inContent = false;

    private int componentNumber = 0;

    private static AppLogger log =
        new AppLogger(ComponentContentHandler.class.getName());

    private final String elementPath = "/item/components/component/content";

    public ComponentContentHandler(final String itemId,
        final Vector<String> componentIds, final StaxParser parser) {
        this.itemId = itemId;
        this.componentIds = componentIds;
        this.parser = parser;
    }

    /*
     * 
     */public ComponentContentHandler(StaxParser parser) {
        this.parser = parser;

    }

    public HashMap<String, HashMap<String, String>> getBinaryData() {
        return this.binaryData;
    }

    public StartElement startElement(StartElement element) {

        // String theName = element.getLocalName();

        String currenrPath = parser.getCurPath();
        if (this.elementPath.equals(currenrPath)) {
            HashMap<String, String> componentBinary =
                new HashMap<String, String>();
            String componentId = (String) (componentIds.get(componentNumber));
            binaryData.put(componentId, componentBinary);
            inContent = true;
            int indexOfHref =
                element
                    .indexOfAttribute(
                        de.escidoc.core.common.business.Constants.XLINK_URI,
                        "href");
            Attribute href = element.getAttribute(indexOfHref);
            uploadUrl = href.getValue();
            componentBinary.put("uploadUrl", uploadUrl);
            href.setValue("/ir/item/" + itemId + "/components/" + componentId
                + "/content");
        }
        return element;
    }

    public EndElement endElement(EndElement element) {
        if (inContent) {
            inContent = false;
            componentNumber++;
        }
        return element;
    }

    public String characters(String s, StartElement element)
        throws MissingContentException {

        String resultText = s;
        if (inContent) {
            String componentId = (String) (componentIds.get(componentNumber));
            if ((s != null) && (s.length() > 0)) {
                HashMap<String, String> componentBinary =
                    binaryData.get(componentId);
                componentBinary.put("content", s);
            }
            else if (uploadUrl == "") {
                log.error("the content of component with id " + componentNumber
                    + " is missing");
                throw new MissingContentException(
                    "the content of component with id " + componentNumber
                        + " is missing");
            }
            resultText = "";
        }

        return resultText;
    }
}
