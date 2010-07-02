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

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * Fills xml-data into HashMap Structure.
 * 
 * @author MIH
 */
public class IndexConfigurationStaxHandler extends DefaultHandler {

    private HashMap<String, HashMap
        <String, HashMap<String, Object>>> indexConfiguration = 
            new HashMap<String, HashMap
            <String, HashMap<String, Object>>>();
    
    private String resourceName = null;

    private String indexName = null;

    private StaxParser parser;
    
    private final String RESOURCE_NAME_ELEMENT_NAME = "resource-name";
    
    private final String INDEX_NAME_ELEMENT_NAME = "index-name";
    
    private final String PREREQUISITES_ELEMENT_NAME = "prerequisites";
    
    private HashMap<String, String> indexElements = 
                        new HashMap<String, String>() {{ 
                                put("index-asynchronous", 
                                        "indexAsynchronous");
                                put("index-released-version", 
                                        "indexReleasedVersion");
                        }};
    
    private HashMap<String, String> prerequisiteElements = 
        new HashMap<String, String>() {{ 
                put("indexing-xpath", 
                        "indexingPrerequisiteXpath");
                put("delete-xpath", 
                        "deletePrerequisiteXpath");
        }};
        
    
    /**
     * Constructor with StaxParser.
     * 
     * @param parser
     *            StaxParser
     * 
     */
    public IndexConfigurationStaxHandler(final StaxParser parser) {
        this.parser = parser;
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
        if (RESOURCE_NAME_ELEMENT_NAME.equals(element.getLocalName())) {
            indexConfiguration.put(
                    s, new HashMap<String, HashMap<String, Object>>());
            resourceName = s;
        } 
        else if (INDEX_NAME_ELEMENT_NAME.equals(element.getLocalName())) {
            indexConfiguration.get(resourceName).put(
                            s, new HashMap<String, Object>());
            indexName = s;
        }
        else if (prerequisiteElements.containsKey(element.getLocalName())) {
            if (indexConfiguration.get(resourceName)
                    .get(indexName).get(PREREQUISITES_ELEMENT_NAME) == null) {
                indexConfiguration.get(resourceName)
                .get(indexName).put(
                        PREREQUISITES_ELEMENT_NAME, new HashMap<String, String>());
            }
            ((HashMap)indexConfiguration.get(resourceName)
                    .get(indexName).get(PREREQUISITES_ELEMENT_NAME)).put(
                    prerequisiteElements.get(element.getLocalName()), s);
        }
        else if (indexElements.containsKey(element.getLocalName())) {
            indexConfiguration.get(resourceName)
                    .get(indexName).put(
                    indexElements.get(element.getLocalName()), s);
        }
        return s;
    }

    /**
     * @return the indexConfiguration
     */
    public HashMap<String, HashMap<String, HashMap<String, Object>>> 
                                            getIndexConfiguration() {
        return indexConfiguration;
    }

}
