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
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.naming.directory.NoSuchAttributeException;

/**
 * Stax handler implementation that handles the item update.
 * 
 * @author FRS
 */
public class ItemUpdateHandler extends DefaultHandler {

    private StaxParser parser = null;

    private static final String ITEM_PATH = "/item";

    private String itemId = null;

    private boolean done = false;

    /**
     * The constructor.
     * 
     * @param itemId
     *            The id of the item that shall be updated.
     * @param parser
     *            The <code>StaxParser</code>.
     */
    public ItemUpdateHandler(final String itemId, final StaxParser parser) {

        this.itemId = itemId;
        this.parser = parser;
    }



    /**
     * See Interface for functional description.
     * 
     * @param element
     * @return
     * @throws InvalidContentException
     * @see de.escidoc.core.om.business.stax.handler.DefaultHandler#startElement
     *      (de.escidoc.core.om.business.stax.events.StartElement)
     */
    @Override
    public StartElement startElement(final StartElement element)
        throws InvalidContentException {

        final String curPath = parser.getCurPath();
        if (!done && curPath.equals(ITEM_PATH)) {
            // handle xlink:href attribute
            try {
                final String href = element
                        .getAttribute(Constants.XLINK_URI, "href").getValue();
                final String expectedHref =
                    Constants.ITEM_URL_BASE + this.itemId;
                // check href
                if (!href.equals(expectedHref)) {
                    throw new InvalidContentException(StringUtility
                        .format(
                                "Attribute xlink:href has invalid value.", href,
                                expectedHref));
                }
            }
            catch (NoSuchAttributeException e) {
                // LAX
            }

            // handle objid attribute
            // in case of non lax handling, an exception must be thrown if no
            // objid has been provided.
            try {
                final String objid = element.getAttribute(null, "objid").getValue();
                if (!objid.equals(itemId)) {
                    throw new InvalidContentException(
                        StringUtility
                            .format(
                                    "Attribute objid has invalid value.", objid,
                                    itemId));
                }

            }
            catch (NoSuchAttributeException e) {
                // LAX
                // throw new InvalidContentException(
                // "Required attribute missed in item element.", e);
            }

            done = true;
        }
        return element;
    }



}
