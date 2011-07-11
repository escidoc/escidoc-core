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
package de.escidoc.core.sm.business.stax.handler;

import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Extracts dates out of processing-information.
 *
 * @author Michael Hoppe
 */
public class PreprocessingInformationStaxHandler extends DefaultHandler {

    private Date startDate;

    private Date endDate;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final StaxParser parser;

    private static final String START_DATE_PATH = "/preprocessing-information/start-date";

    private static final String END_DATE_PATH = "/preprocessing-information/end-date";

    /**
     * Cosntructor with StaxParser.
     *
     * @param parser StaxParser
     */
    public PreprocessingInformationStaxHandler(final StaxParser parser) {
        this.parser = parser;
    }

    /**
     * Handle the character section of an element.
     *
     * @param s       The contents of the character section.
     * @param element The element.
     * @return The character section.
     */
    @Override
    public String characters(final String s, final StartElement element) throws ParseException {
        final String currentPath = parser.getCurPath();

        if (START_DATE_PATH.equals(currentPath)) {
            this.startDate = DATE_FORMAT.parse(s);
        }
        else if (END_DATE_PATH.equals(currentPath)) {
            this.endDate = DATE_FORMAT.parse(s);
        }
        return s;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return this.startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return this.endDate;
    }

}
