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

import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class PredicateValueMapExtractor extends DefaultHandler {

    private List<String> predicates = null;

    private Map<String, String> predicateValues = null;

    /**
     * Creates a Handler that collects all values of elements with specified
     * local name and namespace in a set.
     */
    public PredicateValueMapExtractor() {
        this.predicates = new Vector<String>();
        this.predicateValues = new HashMap<String, String>();
    }

    public void addElements(List<String> elementList) {
        predicates.addAll(elementList);
    }

    public Map<String, String> getElementValues() {
        return this.predicateValues;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#characters(java.lang.String,
     *      de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    public String characters(final String data, final StartElement element)
        throws IntegritySystemException {
        String curLocalName = element.getLocalName();
        String curNamespace = element.getNamespace();
        String curFQName = curNamespace + curLocalName;

        if (predicates.contains(curLocalName)) {
            if (predicateValues.containsKey(curLocalName)) {
                throw new IntegritySystemException("Element " + curLocalName
                    + " already bound.");
            }
            this.predicateValues.put(curLocalName, XmlUtility
                .escapeForbiddenXmlCharacters(data.trim()));
        }
        else if (predicates.contains(curFQName)) {
            if (predicateValues.containsKey(curFQName)) {
                throw new IntegritySystemException("Element " + curFQName
                    + " already bound.");
            }
            this.predicateValues.put(curFQName, XmlUtility
                .escapeForbiddenXmlCharacters(data.trim()));
        }

        return data;
    }
}
