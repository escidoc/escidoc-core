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
package de.escidoc.core.aa.business.stax.handler;

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Stax handler implementation that extracts ouIds out of a ouList.
 * 
 * @aa
 * @author MIH
 * 
 */
public class OuListOuIdHandler extends DefaultHandler {
    public static final String XLINK_PREFIX = "xlink";

    public static final String XLINK_URI = "http://www.w3.org/1999/xlink";

    private StaxParser parser;

    private List<String> ids = new ArrayList<String>();

    /**
     * The constructor.
     * 
     * @param parser
     *            The StaxParser.
     * @aa
     */
    public OuListOuIdHandler(final StaxParser parser) {
        this.parser = parser;

    }

    /**
     * See Interface for functional description.
     * 
     * @param element element
     * @return StartElement
     * @throws Exception e
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler
     *      #startElement(de.escidoc.core.common.util.xml.stax.events.StartElement)
     * @aa
     */
    @Override
    public StartElement startElement(final StartElement element) throws Exception {

        String ouRefPath = "/organizational-unit-path-list/"
                + "organizational-unit-path/organizational-unit-ref";
        String currentPath = parser.getCurPath();

        if (ouRefPath.equals(currentPath)) {
            ids.add(XmlUtility.getIdFromStartElement(element));
        }
        return element;
    }

    /**
     * @return the ids
     */
    public List<String> getIds() {
        return ids;
    }

}
