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
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GroupSelectorsAddHandler extends DefaultHandler {

    private static final String SELECTOR_PATH = "/param/selector";

    private boolean inSelector = false;

    private final StaxParser parser;

    private String[] selector = null;

    private final List<String[]> groupSelectors = new ArrayList<String[]>();

    public GroupSelectorsAddHandler(final StaxParser parser) {
        this.parser = parser;
    }

    @Override
    public StartElement startElement(final StartElement element) throws XmlCorruptedException {

        String currenrPath = parser.getCurPath();
        if (SELECTOR_PATH.equals(currenrPath)) {
            inSelector = true;
            this.selector = new String[3];
            int indexName = element.indexOfAttribute(null, "name");
            if (indexName >= 0) {
                String selectorName =
                    element.getAttribute(indexName).getValue();
                if (selectorName.equals("")) {
                    String message = "The value of the attribute 'selector/@name is missing."; 
                    throw new XmlCorruptedException(message);
                }
                this.selector[0] = selectorName;
            } 
            int indexType = element.indexOfAttribute(null, "type");
            if (indexType >= 0) {
                String selectorType =
                    element.getAttribute(indexType).getValue();
                if (selectorType.equals("")) {
                    String message = "The value of the attribute 'selector/@type is missing."; 
                    throw new XmlCorruptedException(message);
                }
                this.selector[1] = selectorType;
            } 
        }
        return element;
    }

    @Override
    public EndElement endElement(final EndElement element) {
        String currenrPath = parser.getCurPath();

       if (SELECTOR_PATH.equals(currenrPath)) {
            inSelector = false;
            groupSelectors.add(this.selector);
            this.selector = null;
        }
        return element;
    }

    @Override
    public String characters(final String s, final StartElement element) throws XmlCorruptedException {

        String theName = element.getLocalName();
        if ((inSelector) && (theName.equals("selector")) && ((s != null))) {
            if (s.equals("")) {
                String message = "the value of element 'selector' is missing";
                throw new XmlCorruptedException(message);   
            }
            this.selector[2] = s; 
        }
        return s;
    }
    
    
    
    public List<String[]> getGroupSelectors() {
        return this.groupSelectors;
    }
}
