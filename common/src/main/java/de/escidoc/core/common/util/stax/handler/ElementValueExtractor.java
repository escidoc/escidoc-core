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
package de.escidoc.core.common.util.stax.handler;

import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashSet;
import java.util.Set;

public class ElementValueExtractor extends DefaultHandler {

    String elementNamespace = null;

    String elementLocalName = null;

    Set<String> elementValues = null;

    /**
     * Creates a Handler that collects all values of elements with specified
     * local name and namespace in a set.
     * 
     * @param elementNamespace
     * @param elementLocalName
     */
    public ElementValueExtractor(final String elementNamespace,
        final String elementLocalName) {
        this.elementNamespace = elementNamespace;
        this.elementLocalName = elementLocalName;
        this.elementValues = new HashSet<String>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#characters(java.lang.String,
     *      de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    public String characters(final String data, final StartElement element) {
        String curLocalName = element.getLocalName();

        if (elementLocalName.equals(curLocalName)) {

            if (elementNamespace != null
                && !elementNamespace.equals(element.getNamespace())) {
                // namespace is set but does not match
                return data;
            }

            this.elementValues.add(data);

        }

        return data;
    }

    public Set<String> getElementValues() {
        return elementValues;
    }

}
