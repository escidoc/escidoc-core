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

import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

public class SemanticQueryHandler extends DefaultHandler {

    private String query;

    private String subject;

    private String predicate;

    private String object;

    private String format;

    @Override
    public String characters(final String data, final StartElement element) throws MissingElementValueException {

        if ("query".equals(element.getLocalName())) {
            if (data == null || data.length() == 0) {
                throw new MissingElementValueException("The value of the element " + element.getLocalName()
                    + " is missing.");
            }
            this.query = XmlUtility.unescapeForbiddenXmlCharacters(data.trim());
            // extract predicate
            final String[] queryParts = query.split("\\ +");
            this.subject = queryParts[0].trim();
            this.predicate = queryParts[1].trim();
            this.object = queryParts[2].trim();
        }
        else if ("format".equals(element.getLocalName())) {
            if (data == null || data.length() == 0) {
                throw new MissingElementValueException("The value of the element " + element.getLocalName()
                    + " is missing.");
            }
            this.format = data;

        }

        return data;
    }

    /**
     * @return the format of triple store request
     */
    public String getFormat() {
        return this.format;
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return this.query;
    }

    /**
     * @return the query object part
     */
    public String getObject() {
        return this.object;
    }

    /**
     * @return the query predicate part
     */
    public String getPredicate() {
        return this.predicate;
    }

    /**
     * @return the query subject part
     */
    public String getSubject() {
        return this.subject;
    }
}
