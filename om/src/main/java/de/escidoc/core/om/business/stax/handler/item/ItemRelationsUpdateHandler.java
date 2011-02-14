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
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.naming.directory.NoSuchAttributeException;

public class ItemRelationsUpdateHandler extends DefaultHandler {

    private StaxParser parser = null;

    private String relationsPath = null;

    private String itemId = null;

    public ItemRelationsUpdateHandler(String itemId, String relationsPath,
        StaxParser parser) {
        this.itemId = itemId;
        this.relationsPath = relationsPath;
        this.parser = parser;
    }

    public StartElement startElement(StartElement element) throws Exception {
        String curPath = parser.getCurPath();
        if (curPath.startsWith(relationsPath)) {

            try {
                String href =
                    element
                        .getAttribute(Constants.XLINK_URI, "href").getValue();
                if (!href.equals(Constants.ITEM_URL_BASE + itemId
                    + "/relations")) {
                    throw new ReadonlyAttributeViolationException(
                        "Item properties has invalid xlink:href.");
                }
            }
            catch (NoSuchAttributeException e) {
                // LAX
            }

            // try {
            // String title =
            // element
            // .getAttribute(Constants.XLINK_URI, "title").getValue();
            // // TODO check title?
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
            //
            // try {
            // String linkType =
            // element
            // .getAttribute(Constants.XLINK_URI, "type").getValue();
            // // LAX
            // // if (!"simple".equals(linkType)) {
            // // throw new ReadonlyAttributeViolationException(
            // // "Item properties has invalid xlink:type.");
            // // }
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // }
        }
        return element;
    }

}
