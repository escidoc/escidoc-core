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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.util.stax.handler;

import java.util.ArrayList;
import java.util.Collection;

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class GrantsHandler extends DefaultHandler {
    protected StaxParser parser;

    public static final String XLINK_URI = "http://www.w3.org/1999/xlink";

    protected Collection<String> roles = new ArrayList<String>();

    private static AppLogger log =
        new AppLogger(GrantsHandler.class.getName());

    /*
     * 
     */public GrantsHandler(StaxParser parser) {
        this.parser = parser;

    }

    @Override
    public StartElement startElement(StartElement element)
        throws MissingAttributeValueException {

        String grantPath = "/current-grants/grant/properties/role";
        String currentPath = parser.getCurPath();

        if (grantPath.equals(currentPath)) {
            int indexOfTitle = element.indexOfAttribute(XLINK_URI, "title");
            if (indexOfTitle != (-1)) {
                Attribute att = element.getAttribute(indexOfTitle);
                roles.add(att.getValue());
            }
        }
        return element;
    }

	/**
	 * @return the roles
	 */
	public Collection<String> getRoles() {
		return roles;
	}

}
