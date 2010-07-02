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

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class ItemPropertiesAfterCreationHandler extends DefaultHandler {

    protected boolean inside = false;

    protected int insideLevel = 0;

    // protected Map nsuris = null;
    protected StaxParser parser;

    protected String itemId;

    // String creator = null;

    protected boolean inLatestVersion = false;

    protected boolean inCurrentVersion = false;

    String itemCreationDate = null;

    String itemVersionCreationDate = null;

    // private static AppLogger log =
    // new AppLogger(ItemPropertiesAfterCreationHandler.class.getName());

    public ItemPropertiesAfterCreationHandler(final String itemId,
        final StaxParser parser) {
        this.itemId = itemId;
        this.parser = parser;
    }

    /*
     * 
     */public ItemPropertiesAfterCreationHandler(StaxParser parser) {
        this.parser = parser;

    }

    public String getItemCreationDate() {
        return itemCreationDate;
    }

    public void setItemCreationDate(String itemCreationDate) {
        this.itemCreationDate = itemCreationDate;
    }

    public String getItemVersionCreationDate() {
        return itemVersionCreationDate;
    }

    public void setItemVersionCreationDate(String itemVersionCreationDate) {
        this.itemVersionCreationDate = itemVersionCreationDate;
    }

    @Override
    public StartElement startElement(StartElement element) {

        String elementPath = "/properties";

        if (inside) {
            String theName = element.getLocalName();
            if (theName.equals("latest-version")) {
                inLatestVersion = true;
                int indexOfHref =
                    element.indexOfAttribute(
                        de.escidoc.core.common.business.Constants.XLINK_URI,
                        "href");
                Attribute href = element.getAttribute(indexOfHref);
                href.setValue("/ir/item/" + itemId
                    + "/properties/latest-version");
            }
            else if (theName.equals("current-version")) {
                inCurrentVersion = true;
                int indexOfHref =
                    element.indexOfAttribute(
                        de.escidoc.core.common.business.Constants.XLINK_URI,
                        "href");
                Attribute href = element.getAttribute(indexOfHref);
                href.setValue("/ir/item/" + itemId
                    + "/properties/current-version");
            }

            insideLevel++;

        }
        else {
            String currenrPath = parser.getCurPath();

            if (elementPath.equals(currenrPath)) {
                inside = true;
                insideLevel++;
                // String namespace = element.getNamespace();

                int indexOfHref =
                    element.indexOfAttribute(
                        de.escidoc.core.common.business.Constants.XLINK_URI,
                        "href");
                Attribute href = element.getAttribute(indexOfHref);
                href.setValue("/ir/item/" + itemId + "/properties");

            }
        }
        return element;
    }

    @Override
    public EndElement endElement(EndElement element) {
        if (inside) {
            String theName = element.getLocalName();
            if (theName.equals("latest-version")) {
                inLatestVersion = false;
            }
            else if (theName.equals("current-version")) {
                inCurrentVersion = false;
            }
            insideLevel--;

            if (insideLevel == 0) {
                inside = false;
            }
        }
        return element;
    }

    @Override
    public String characters(String s, StartElement element) {
        String resultText = s;
        String theName = element.getLocalName();

        if (inLatestVersion) {
            if (theName == "date") {
                if (this.itemVersionCreationDate != null) {
                    resultText = this.itemVersionCreationDate;
                }
            }
        }
        else if (inCurrentVersion) {
            if (theName == "date") {
                if (this.itemVersionCreationDate != null) {
                    resultText = this.itemVersionCreationDate;
                }
            }
        }
        else if (theName == "creation-date") {
            if (this.itemCreationDate != null) {
                resultText = this.itemCreationDate;
            }

        }

        return resultText;
    }
}
