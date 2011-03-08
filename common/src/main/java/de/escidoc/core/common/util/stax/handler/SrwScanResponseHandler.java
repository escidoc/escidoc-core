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

import java.util.HashSet;
import java.util.Set;

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * Parses a SRW Scan Response and extracts all Terms.
 * 
 * @author MIH
 * 
 */
public class SrwScanResponseHandler extends DefaultHandler {

    private final StaxParser parser;

    private final Set<String> terms = new HashSet<String>();
    
    private String lastTerm = "";
    
    private static final String TERM_VALUE_PATH = 
                        "/scanResponse/terms/term/value";
    
    private int noOfDocumentTerms;
    
    /**
     * Constructor
     */
    public SrwScanResponseHandler(final StaxParser parser) {
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
    @Override
    public String characters(
            final String s,
            final StartElement element)
            throws Exception {
        final String currentPath = parser.getCurPath();
        if (TERM_VALUE_PATH.equals(parser.getCurPath())) {
            terms.add(s.replaceAll("(.*?:.*?):.*", "$1"));
            noOfDocumentTerms++;
            lastTerm = s;
        } 
        return s;
    }

    /**
     * Get terms-Set.
     * 
     * @return terms set.
     */
    public Set<String> getTerms() {
        return terms;
    }

    /**
     * Reset number of Terms in last document.
     * 
     */
    public void resetNoOfDocumentTerms() {
        noOfDocumentTerms = 0;
    }

    /**
     * Get number of Terms in last document.
     * 
     * @return number of Terms in last document.
     */
    public int getNoOfDocumentTerms() {
        return noOfDocumentTerms;
    }

    /**
     * Get lastTerm.
     * 
     * @return last term parsed.
     */
    public String getLastTerm() {
        return lastTerm;
    }

}
