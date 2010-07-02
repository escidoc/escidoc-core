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
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class ContainerUpdateHandler extends DefaultHandler {

    private StaxParser parser = null;

    private String containerPath = "/container";

    private String containerId = null;

    private boolean done = false;

    private String containerTitle;

    private static AppLogger log =
        new AppLogger(ContainerUpdateHandler.class.getName());

    public ContainerUpdateHandler(String containerId, StaxParser parser) {
        this.containerId = containerId;
        this.parser = parser;
    }

    public StartElement startElement(StartElement element)
        throws ReadonlyAttributeViolationException, InvalidContentException {
        String curPath = parser.getCurPath();

        if (!done && curPath.equals(containerPath)) {

            int indexOfObid = element.indexOfAttribute(null, "objid");
            if (indexOfObid != -1) {
                String objid = element.getAttribute(indexOfObid).getValue();
                if (!objid.equals(containerId)) {
                    throw new InvalidContentException(
                        "Attribut objid has invalid value.");
                }
            }

            // String xlinkType =
            // element.getAttribute(de.escidoc.core.common.business.Constants.XLINK_URI,
            // "type").getValue();
            // if (xlinkType == null || !xlinkType.equals("simple")) {
            // throw new ReadonlyAttributeViolationException(
            // "xlink:type is not simple.");
            // }
            int indexOfHref =
                element.indexOfAttribute(Constants.XLINK_URI, "href");
            if (indexOfHref != -1) {
                String xlinkHref = element.getAttribute(indexOfHref).getValue();
                if (xlinkHref.equals("")
                    || !xlinkHref.equals("/ir/container/" + containerId)) {
                    throw new ReadonlyAttributeViolationException(
                        "Container href must be '/ir/container/" + containerId
                            + "'.");
                }
            }

            // try {
            // String base =
            // element.getAttribute(de.escidoc.core.common.business.Constants.XML_NSURI,
            // "base")
            // .getValue();
            // // check base
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // String msg = "Missing attribute in " + curPath + ".";
            // log.warn(msg, e);
            // }

            // try {
            // containerTitle = element
            // .getAttribute(
            // de.escidoc.core.common.business.Constants.XLINK_URI,
            // "title").getValue();
            // }
            // catch (NoSuchAttributeException e) {
            // // LAX
            // containerTitle = "";
            // String msg = "Missing attribute in " + curPath + ".";
            // log.warn(msg, e);
        }

        done = true;
        // }
        return element;
    }

    public String getContainerTitle() {
        return containerTitle;
    }
}
