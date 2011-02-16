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
package de.escidoc.core.aa.business.stax.handler;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class GroupCreateUpdateHandler extends DefaultHandler {

    private static final String PROPERTIES_PATH = "/user-group/properties";

    private boolean inProperties = false;

  
    private final StaxParser parser;

    private final Map<String, String> groupProperties =
        new HashMap<String, String>();

    private static final AppLogger log =
        new AppLogger(GroupCreateUpdateHandler.class.getName());

    /*
     * 
     */public GroupCreateUpdateHandler(final StaxParser parser) {
        this.parser = parser;

    }

    @Override
    public StartElement startElement(final StartElement element) {

        String currenrPath = parser.getCurPath();

        
        if (PROPERTIES_PATH.equals(currenrPath)) {
            inProperties = true;

        }
        
        return element;
    }

    @Override
    public EndElement endElement(final EndElement element) {
        String currenrPath = parser.getCurPath();

        if (PROPERTIES_PATH.equals(currenrPath)) {

            inProperties = false;

        }
        
        return element;
    }

    @Override
    public String characters(final String s, final StartElement element)
        throws XmlCorruptedException {

        String theName = element.getLocalName();
        if (inProperties) {
            if (theName.equals(Elements.ELEMENT_NAME)) {
                if ((s != null) && (s.length() > 0)) {
                    groupProperties.put(Elements.ELEMENT_NAME, s);
                }
                else {
                    log
                        .error("the value of element " + theName
                            + " is missing");
                    throw new XmlCorruptedException(
                        "the value of element " + theName + " is missing");
                }
            }
            else if (theName.equals(Elements.ELEMENT_DESCRIPTION)) {
                if ((s != null)) {
                    groupProperties.put(Elements.ELEMENT_DESCRIPTION, s);
                }
            }
            else if (theName.equals(Elements.ELEMENT_TYPE)) {
                if ((s != null)) {
                    groupProperties.put(Elements.ELEMENT_TYPE, s);
                }

            }
            else if (theName.equals("label")) {
                if ((s != null) && (s.length() > 0)) {
                    groupProperties.put("label", s);
                }
                else {
                    log
                        .error("the value of element " + theName
                            + " is missing");
                    throw new XmlCorruptedException(
                        "the value of element " + theName + " is missing");
                }
                
            }
            else if (theName.equals("email")) {
                if ((s != null)) {
                    groupProperties.put("email", s);
                }
            }
        }
        
        return s;
    }
    
    public Map<String, String> getGroupProperties() {
        return this.groupProperties;
    }
    
    
}
