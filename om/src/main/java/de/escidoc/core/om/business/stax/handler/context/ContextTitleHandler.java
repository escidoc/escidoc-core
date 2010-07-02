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
package de.escidoc.core.om.business.stax.handler.context;

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * The TitleHandler.
 * 
 * @author JKR
 * 
 * @om
 */
public class ContextTitleHandler extends DefaultHandler {

    private StaxParser staxParser;

    private String title = null;

    private static AppLogger logger =
        new AppLogger(ContextTitleHandler.class.getName());

    /**
     * Instantiate a TitleHandler.
     * 
     * @param parser
     *            The parser.
     * 
     * @om
     */
    public ContextTitleHandler(final StaxParser parser) {
        this.staxParser = parser;

    }

    /**
     * Handle the start of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @throws MissingAttributeValueException
     *             If a required attribute is missing.
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#startElement
     *      (de.escidoc.core.common.util.xml.stax.events.StartElement)
     * @om
     */
    public StartElement startElement(final StartElement element)
        throws MissingAttributeValueException {

        String elementPath = "/context";

        if (elementPath.equals(staxParser.getCurPath())) {
            int indexOfTitle =
                element
                    .indexOfAttribute(
                        de.escidoc.core.common.business.Constants.XLINK_URI,
                        "title");
            if (indexOfTitle != -1) {
                this.title = element.getAttribute(indexOfTitle).getValue();
            }
        }

        return element;
    }

    /**
     * Handle the end of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#endElement
     *      (de.escidoc.core.common.util.xml.stax.events.EndElement)
     * @om
     */
    public EndElement endElement(final EndElement element) {

        return element;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle() {
        return title;
    }
}
