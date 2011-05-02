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

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.util.HashSet;
import java.util.Set;

/**
 * Parses a SRW Scan Response and extracts all Terms.
 *
 * @author Michael Hoppe
 */
public class SrwScanResponseHandler extends DefaultHandler {

    private final StaxParser parser;

    private final Set<String> terms = new HashSet<String>();

    private String lastTerm = "";

    private static final String TERM_VALUE_PATH = "/scanResponse/terms/term/value";

    private int noOfDocumentTerms;

    /**
     * Constructor
     * @param parser
     */
    public SrwScanResponseHandler(final StaxParser parser) {
        this.parser = parser;

    }

    /**
     * Handle the character section of an element.
     *
     * @param s       The contents of the character section.
     * @param element The element.
     * @return The character section.
     * @throws Exception e
     */
    @Override
    public String characters(final String s, final StartElement element) throws Exception {
        if (TERM_VALUE_PATH.equals(parser.getCurPath())) {
            terms.add(s.replaceAll("(.*?:.*?):.*", "$1"));
            this.noOfDocumentTerms++;
            this.lastTerm = s;
        }
        return s;
    }

    /**
     * Get terms-Set.
     *
     * @return terms set.
     */
    public Set<String> getTerms() {
        return this.terms;
    }

    /**
     * Reset number of Terms in last document.
     */
    public void resetNoOfDocumentTerms() {
        this.noOfDocumentTerms = 0;
    }

    /**
     * Get number of Terms in last document.
     *
     * @return number of Terms in last document.
     */
    public int getNoOfDocumentTerms() {
        return this.noOfDocumentTerms;
    }

    /**
     * Get lastTerm.
     *
     * @return last term parsed.
     */
    public String getLastTerm() {
        return this.lastTerm;
    }

}
