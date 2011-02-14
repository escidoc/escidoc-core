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
package de.escidoc.core.om.business.stax.handler;

import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.Vector;

public class ContentRelationsRemoveHandler extends DefaultHandler {

    private StaxParser parser = null;

    Vector<String> relationIds = new Vector<String>();

    private static AppLogger log =
        new AppLogger(ContentRelationsRemoveHandler.class.getName());

    public ContentRelationsRemoveHandler(StaxParser parser) {
        this.parser = parser;

    }

    public String characters(String data, StartElement element)
        throws MissingElementValueException {
        if (element.getLocalName().equals("id")) {
            if ((data == null) || (data.length() == 0)) {
                String message =
                    "The value of the element " + element.getLocalName()
                        + " is missing.";
                log.error(message);
                throw new MissingElementValueException(message);
            }
            this.relationIds.add(data);
        }
        return data;
    }

    /**
     * Returns a Vector with relations ids.
     * 
     * @return Vector
     */
    public Vector<String> getRelationIds() {
        return relationIds;
    }

}
