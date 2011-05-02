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
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class OneComponentTitleHandler extends DefaultHandler {

    private final StaxParser parser;

    public OneComponentTitleHandler(final StaxParser parser) {
        this.parser = parser;
    }

    @Override
    public StartElement startElement(final StartElement element) throws ReadonlyAttributeViolationException,
        InvalidContentException {

        final String componentPath = "/component";
        final String theName = element.getLocalName();
        final String currenrPath = parser.getCurPath();
        if (componentPath.equals(currenrPath)) {
            final int indexOfobjId = element.indexOfAttribute(null, "objid");
            if (indexOfobjId != -1 && element.getAttribute(indexOfobjId).getValue().length() > 0) {
                throw new ReadonlyAttributeViolationException("Read only attribute \"objid\" of the " + "element "
                    + theName + " may not exist while create");
            }
            final int indexOfhref = element.indexOfAttribute(Constants.XLINK_URI, "href");
            if (indexOfhref != -1 && element.getAttribute(indexOfhref).getValue().length() > 0) {
                throw new ReadonlyAttributeViolationException("Read only attribute \"href\" of the " + "element "
                    + theName + " may not exist while create");
            }
        }
        return element;
    }

}
