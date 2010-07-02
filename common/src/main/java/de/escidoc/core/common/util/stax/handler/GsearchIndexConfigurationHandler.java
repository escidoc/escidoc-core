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

import java.util.HashMap;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * Parses the GsearchIndexConfiguration-XML into a Hash-Structure.
 * 
 * @author MIH
 * 
 */
public class GsearchIndexConfigurationHandler extends DefaultHandler {

    private HashMap<String, HashMap<String, String>> gsearchIndexConfiguration = 
        new HashMap<String, HashMap<String, String>>();
    
    private static final String INDEX_ELEMENT_NAME = "index";

    private static final String INDEX_NAME_ELEMENT_NAME = "name";

    private static final String PROPERTY_ELEMENT_NAME = "property";

    private static final String KEY_ELEMENT_NAME = "key";

    private static final String VALUE_ELEMENT_NAME = "value";
    
    private String indexName = null;
    
    private HashMap<String, String> properties = new HashMap<String, String>();
    
    private String key = null;

    private String value = null;

    /**
     * See Interface for functional description.
     * 
     * @param element endElement
     * @return EndElement element
     * @throws WebserverSystemException e
     * 
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler
     *  #endElement(de.escidoc.core.common.util.xml.stax.events.EndElement)
     */
    @Override
    public EndElement endElement(final EndElement element)
        throws WebserverSystemException {
        if (element.getLocalName().equals(PROPERTY_ELEMENT_NAME)) {
            properties.put(key, value);
            key = null;
            value = null;
        }
        else if (element.getLocalName().equals(INDEX_ELEMENT_NAME)) {
            if (indexName == null) {
                throw new WebserverSystemException("index name is null");
            }
            gsearchIndexConfiguration.put(indexName, properties);
            indexName = null;
            properties = new HashMap<String, String>();
        }
        return element;
    }


    /**
     * Handle the character section of an element.
     * 
     * @param s
     *            The contents of the character section.
     * @param element
     *            The element.
     * @return The character section.
     * @throws Exception
     *             e
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#characters
     *      (java.lang.String,
     *      de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    public String characters(
            final String s,
            final StartElement element)
            throws Exception {
        if (INDEX_NAME_ELEMENT_NAME.equals(element.getLocalName())) {
            indexName = s;
        } 
        else if (KEY_ELEMENT_NAME.equals(element.getLocalName())) {
            key = s;
        }
        else if (VALUE_ELEMENT_NAME.equals(element.getLocalName())) {
            value = s;
        }
        return s;
    }

    /**
     * @return the gsearchIndexConfiguration
     */
    public HashMap<String, HashMap<String, String>> 
                        getGsearchIndexConfiguration() {
        return gsearchIndexConfiguration;
    }

}
