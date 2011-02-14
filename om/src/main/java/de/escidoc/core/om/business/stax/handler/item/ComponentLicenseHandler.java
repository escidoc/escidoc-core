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

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.violated.TimeFrameViolationException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.Vector;

public class ComponentLicenseHandler extends DefaultHandler {

    private boolean inside = false;

    private int insideLevel = 0;

    private StaxParser parser;

    private String itemId;

    private Vector<String> componentIds;

    private boolean inLicense = false;

    private int licenceNumber = 0;

    private int componentNumber = 0;

    private String timeFrameStart = null;

    private String timeFrameEnd = null;

    private static AppLogger log =
        new AppLogger(ComponentLicenseHandler.class.getName());

    public ComponentLicenseHandler(final String itemId,
        final Vector<String> componentIds, final StaxParser parser) {
        this.itemId = itemId;
        this.componentIds = componentIds;
        this.parser = parser;
    }

    public ComponentLicenseHandler(StaxParser parser) {
        this.parser = parser;
    }

    public StartElement startElement(StartElement element)
        throws MissingAttributeValueException {
        String elementPath = "/item/components/component/licenses";
        String theName = element.getLocalName();
        if (inside) {
            insideLevel++;
            if (theName.equals("licence") || theName.equals("licence-type")) {
                int indexOfHref =
                    element.indexOfAttribute(
                        de.escidoc.core.common.business.Constants.XLINK_URI,
                        "href");
                Attribute href = element.getAttribute(indexOfHref);

                if (theName.equals("licence")) {

                    href.setValue("/ir/item/" + itemId + "/components/"
                        + componentIds.get(componentNumber) + "/licenses/lic"
                        + (licenceNumber + 1));
                    licenceNumber++;
                    timeFrameStart = null;
                    timeFrameEnd = null;
                }
                if (theName.equals("licence-type")) {
                    String hrefValue = href.getValue();
                    if (hrefValue == "") {
                        log.error("the value of"
                            + " href atribute of the element " + theName
                            + " is missing");
                        throw new MissingAttributeValueException(
                            "the value of the \"href\" atribute of the element "
                                + theName + " is missing");
                    }
                }

            }
        } else {
            String currenrPath = parser.getCurPath();
            if (elementPath.equals(currenrPath)) {
                inside = true;
                insideLevel++;
                int indexOfHref =
                    element.indexOfAttribute(de.escidoc.core.common.business.Constants.XLINK_URI, "href");
                Attribute href = element.getAttribute(indexOfHref);
                href.setValue("/ir/item/" + itemId + "/components/"
                    + componentIds.get(componentNumber) + "/licenses");

            }

        }
        return element;
    }

    public EndElement endElement(EndElement element) {
        if (inside) {
            insideLevel--;

            if (insideLevel == 0) {
                inside = false;
                componentNumber++;
                licenceNumber = 0;
            }
        }
        return element;
    }

    public String characters(String s, StartElement element)
        throws TimeFrameViolationException, MissingElementValueException {

        String theName = element.getLocalName();
        if (theName.equals("time-frame-start") || theName.equals("time-frame-end")) {
            if ((s == null) || (s.length() == 0)) {
                log.error("the mandatory text of" + " the element " + theName + " is missing");
                throw new MissingElementValueException("the mandatory text of"
                    + " the element " + theName + " is missing");
            }
            if (theName.equals("time-frame-start")) {
                timeFrameStart = s;
            } else if (theName.equals("time-frame-end")) {
                timeFrameEnd = s;
            }
        }
        return s;
    }
}
