/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.util.stax.handler;

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class UserAccountHandler extends DefaultHandler {

    private final StaxParser parser;

    private String accountId;

    private String accountName;

    public String getAccountId() {
        return this.accountId;
    }

    public String getAccountName() {
        return this.accountName;
    }

    /*
     * 
     */
    public UserAccountHandler(final StaxParser parser) {
        this.parser = parser;

    }

    @Override
    public StartElement startElement(final StartElement element) throws MissingAttributeValueException {

        final String elementPath = "/user-account";
        final String currenrPath = parser.getCurPath();

        if (currenrPath.equals(elementPath)) {
            final int indexOfObjid = element.indexOfAttribute(null, "objid");
            final Attribute objid = element.getAttribute(indexOfObjid);
            this.accountId = objid.getValue();
        }
        return element;
    }

    @Override
    public String characters(final String data, final StartElement element) {
        if ("name".equals(element.getLocalName())) {
            this.accountName = data;
        }

        return data;
    }

}
