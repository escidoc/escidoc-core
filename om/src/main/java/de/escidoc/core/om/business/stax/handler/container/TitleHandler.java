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
package de.escidoc.core.om.business.stax.handler.container;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * The TitleHandler.
 * 
 * @author MSC
 * 
 * @om
 */
public class TitleHandler extends DefaultHandler {

    private StaxParser staxParser;

    private String title = null;

    private static AppLogger logger =
        new AppLogger(TitleHandler.class.getName());

    /**
     * Instantiate a TitleHandler.
     * 
     * @param parser
     *            The parser.
     * 
     * @om
     */
    public TitleHandler(final StaxParser parser) {
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
        throws MissingAttributeValueException,
        ReadonlyAttributeViolationException, ReadonlyElementViolationException {
        String theName = element.getLocalName();
        String elementPath = "/container";
        String resourcesPath = "/container/resources";

        // if (staxParser.getCurPath().equals(resourcesPath)) {
        // String message = "The element " + theName
        // + " may not exist while create.";
        // logger.error(message);
        // throw new ReadonlyElementViolationException(message);
        // }
        if (elementPath.equals(staxParser.getCurPath())) {

            int indexOfTitle =
                element.indexOfAttribute(Constants.XLINK_URI, "title");
            if (indexOfTitle == -1) {
                this.title = "";
                // String message = "The value of the attribute \"title\" of the
                // "
                // + "element " + theName + " is missing";
                // logger.error(message);
                // throw new MissingAttributeValueException(message);
            }
            Attribute title = element.getAttribute(indexOfTitle);
            this.title = title.getValue();

            // int indexOfType = element.indexOfAttribute(Constants.XLINK_URI,
            // "type");
            // if (indexOfType == (-1)) {
            // Attribute type = new Attribute("type", Constants.XLINK_URI,
            // Constants.XLINK_PREFIX, "simple");
            // element.addAttribute(type);
            // } else {
            // Attribute type = element.getAttribute(indexOfType);
            // String typeValue = type.getValue();
            // if(!typeValue.equals(Constants.XLINK_TYPE_SIMPLE)) {
            // type.setValue("simple");
            // }
            // }
            // int indexOfhref= element.indexOfAttribute(Constants.XLINK_URI,
            // "href");
            // if(indexOfhref!=(-1)) {
            // String message = "Read only attribute \"href\" of the "
            // + "element " + theName + " may not exist while create";
            // logger.error(message);
            // throw new ReadonlyAttributeViolationException(message);
            // }
            // int indexOfobjId = element.indexOfAttribute(null, "objid");
            // if(indexOfobjId!=(-1)) {
            // String message = "Read only attribute \"objid\" of the "
            // + "element " + theName + " may not exist while create";
            // logger.error(message);
            // throw new ReadonlyAttributeViolationException(message);
            // }
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
