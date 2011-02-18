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

import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.xml.XMLConstants;

public class OntologyHandler extends DefaultHandler {

    private final StaxParser parser;

    private String predicateWithoutBase;

    private String base = "";

    private String predicate;

    private String predicateBase;

    private boolean exist = false;

    public OntologyHandler(final StaxParser parser, final String predicate) {

        this.parser = parser;
        String p = predicate;
        if (p.startsWith("<")) {
            p = p.substring(1);
        }
        if (p.endsWith(">")) {
            p = p.substring(0, p.length() - 1);
        }
        
        //TODO: If a predicate contains character #, this character wil be 
        //thrown away
        String[] predicateArray = p.split("#");
        if (predicateArray.length != 2) {
            exist = false;
            return;
        }
        this.predicateBase = predicateArray[0];
        this.predicateWithoutBase = predicateArray[1];
        this.predicate = p;
    }

    public StartElement startElement(StartElement element) {

        String elementPath = "/RDF/Property";
        String basePath = "/RDF";
        String currentPath = parser.getCurPath();
        if (basePath.equals(currentPath)) {
            int indexOfBase =
                element.indexOfAttribute(XMLConstants.XML_NS_URI, "base");
            if (indexOfBase != -1) {
                this.base = element.getAttribute(indexOfBase).getValue();
            }
        }
        if (elementPath.equals(currentPath)) {

            int indexOfId =
                element.indexOfAttribute(
                    de.escidoc.core.common.business.Constants.RDF_NAMESPACE_URI,
                    "ID");

            if (indexOfId != -1) {
                String id = element.getAttribute(indexOfId).getValue();
                if ((id.equals(this.predicateWithoutBase) && base
                    .equals(predicateBase))
                    || id.equals(this.predicate)) {
                    exist = true;                    
                }
            }
        }
        return element;
    }

    public final boolean isExist() {
        return exist;
    }

}
