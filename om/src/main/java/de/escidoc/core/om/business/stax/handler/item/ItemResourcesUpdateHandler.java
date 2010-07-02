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

import javax.naming.directory.NoSuchAttributeException;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class ItemResourcesUpdateHandler extends DefaultHandler {

    private StaxParser parser = null;

    private String resourcesPath = null;

    private String itemId = null;

    public ItemResourcesUpdateHandler(String itemId, String resourcesPath,
        StaxParser parser) {
        this.itemId = itemId;
        this.resourcesPath = resourcesPath;
        this.parser = parser;
    }

    public StartElement startElement(StartElement element)
        throws InvalidContentException {
        String curPath = parser.getCurPath();
        if (curPath.startsWith(resourcesPath)) {

            if (curPath.equals(resourcesPath)) {

                // check href
                String hrefStr = "/ir/item/" + itemId + "/resources";
                try {
                    Attribute href =
                        element.getAttribute(
                            de.escidoc.core.common.business.Constants.XLINK_URI,
                            "href");
                    // LAX adjust href
                    if (!href.getValue().equals(hrefStr)) {
                        href.setValue(hrefStr);
                    }
                }
                catch (NoSuchAttributeException e) {
                    // LAX adjust href
                    element.addAttribute(new Attribute("href",
                        de.escidoc.core.common.business.Constants.XLINK_URI,
                        de.escidoc.core.common.business.Constants.XLINK_PREFIX,
                        hrefStr));
                }

                // check title
                String titleStr = "Resources";
                try {
                    Attribute title =
                        element.getAttribute(
                            de.escidoc.core.common.business.Constants.XLINK_URI,
                            "title");
                    // LAX adjust title
                    if (title.getValue().length() == 0) {
                        title.setValue(titleStr);
                    }
                }
                catch (NoSuchAttributeException e) {
                    // LAX adjust title
                    element.addAttribute(new Attribute("title",
                        de.escidoc.core.common.business.Constants.XLINK_URI,
                        de.escidoc.core.common.business.Constants.XLINK_PREFIX,
                        titleStr));
                }

                // check type
                String typeStr = "simple";
                try {
                    Attribute type =
                        element.getAttribute(
                            de.escidoc.core.common.business.Constants.XLINK_URI,
                            "type");
                    // LAX adjust type
                    if (!type.getValue().equals(typeStr)) {
                        type.setValue(typeStr);
                    }
                }
                catch (NoSuchAttributeException e) {
                    // LAX adjust type
                    element.addAttribute(new Attribute("type",
                        de.escidoc.core.common.business.Constants.XLINK_URI,
                        de.escidoc.core.common.business.Constants.XLINK_PREFIX,
                        typeStr));
                }
            }
        }
        return element;
    }
}
