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

package de.escidoc.core.test.sb;

/**
 * Is called from sylesheet that transforms foxml to indexable document. Performs different string-operations. to call
 * this class from stylesheet: declaration in sylesheet-element: xmlns:component-accessor=
 * "xalan://de.escidoc.sb.gsearch.xslt.ComponentAccessor" xmlns:string-helper="xalan://de.escidoc.sb.gsearch.xslt.StringHelper"
 * extension-element-prefixes="component-accessor string-helper" use: <xsl:value-of
 * select="string-helper:getSubstringAfterLast($PID,'/')"/>
 *
 * @author Michael Hoppe
 */
public class StringHelper {

    /**
     * Returns the substring after the last occurence of character.
     *
     * @param term      term
     * @param character character
     * @return String substring of term after last occurence of character.
     */
    public String getSubstringAfterLast(final String term, final String character) {
        if (term == null || term.lastIndexOf(character) == -1) {
            return term;
        }
        return term.substring(term.lastIndexOf(character) + 1);
    }

    /**
     * @sb
     */
    public String getSplitPart(final String term, final String character, final int partNo) {
        if (term == null || term.lastIndexOf(character) == -1) {
            return term;
        }
        String[] parts = term.split(character);
        if (parts != null && parts.length >= partNo) {
            return parts[partNo].trim();
        }
        return term;
    }

}
