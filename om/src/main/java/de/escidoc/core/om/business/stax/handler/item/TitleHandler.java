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

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class TitleHandler extends DefaultHandler {

    protected StaxParser parser;

    private String itemTitle = null;

    private static AppLogger log = new AppLogger(TitleHandler.class.getName());

    /**
     * TitleHandler.
     * 
     * @param parser
     *            StAX parser.
     */
    public TitleHandler(StaxParser parser) {
        this.parser = parser;

    }

    public String getItemTitle() {
        return itemTitle;
    }

    public StartElement startElement(StartElement element)
        throws MissingAttributeValueException,
        ReadonlyAttributeViolationException, ReadonlyElementViolationException {

        String itemPath = "/item";
        String componentPath = "/item/components/component";
        String componentsPath = "/item/components/components";
        String theName = element.getLocalName();

        String currentPath = parser.getCurPath();

        if (itemPath.equals(currentPath)) {
            int indexOfTitle =
                element.indexOfAttribute(Constants.XLINK_URI, "title");
            if (indexOfTitle == (-1)) {
                String message =
                    "The value of the attribute \"title\" of the " + "element "
                        + theName + " is missing";
                log.error(message);
            }
            else {
                // ignore read-only attributes
                // throw new MissingAttributeValueException(message);
                Attribute title = element.getAttribute(indexOfTitle);
                String titleValue = title.getValue();
                if (title != null) {
                    itemTitle = titleValue;
                }
            }

        }
        else if (componentPath.equals(currentPath)) {

        }
        else if (componentsPath.equals(currentPath)) {

        }

        return element;
    }
}
