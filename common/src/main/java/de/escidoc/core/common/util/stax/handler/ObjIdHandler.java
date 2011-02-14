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

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.List;
import java.util.Vector;

public class ObjIdHandler extends DefaultHandler {
    public static final String XLINK_PREFIX = "xlink";

    public static final String XLINK_URI = "http://www.w3.org/1999/xlink";

    private StaxParser parser;

    private String itemId;

    private List componentIds;

    private int componentNumber = 0;

    private static AppLogger log = new AppLogger(ObjIdHandler.class.getName());

    public ObjIdHandler(final String itemId, final List componentIds,
        final StaxParser parser) {
        this.itemId = itemId;
        this.componentIds = componentIds;
        this.parser = parser;
    }

    /*
     * 
     */public ObjIdHandler(StaxParser parser) {
        this.parser = parser;

    }

    @Override
    public StartElement startElement(StartElement element)
        throws MissingAttributeValueException {

        String itemPath = "/item";
        String componentsPath = "/item/components";
        String componentPath = "/item/components/component";
        String theName = element.getLocalName();

        String currenrPath = parser.getCurPath();

        if (itemPath.equals(currenrPath)) {

            int indexOfHref = element.indexOfAttribute(XLINK_URI, "href");
            Attribute href = element.getAttribute(indexOfHref);
            href.setValue("/ir/item/" + itemId);
            int indexOfObjid = element.indexOfAttribute(null, "objid");
            Attribute objid = element.getAttribute(indexOfObjid);
            objid.setValue(itemId);
        }
        else if (componentsPath.equals(currenrPath)) {
            int indexOfHref = element.indexOfAttribute(XLINK_URI, "href");
            Attribute href = element.getAttribute(indexOfHref);
            href.setValue("/ir/item/" + itemId + "/components");
        }
        else if (componentPath.equals(currenrPath)) {
            int indexOfHref = element.indexOfAttribute(XLINK_URI, "href");
            Attribute href = element.getAttribute(indexOfHref);
            href.setValue("/ir/item/" + itemId + "/components/"
                + componentIds.get(componentNumber));
            int indexOfObjid = element.indexOfAttribute(null, "objid");
            Attribute objid = element.getAttribute(indexOfObjid);
            objid.setValue((String) componentIds.get(componentNumber));
            componentNumber++;
        }

        return element;
    }

    @Override
    public EndElement endElement(EndElement element) {

        return element;
    }

}
