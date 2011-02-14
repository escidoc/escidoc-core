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

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.joda.time.DateTime;

/**
 * Fills xml-data into VO.
 * 
 * @author MIH
 * @sm
 */
public class ReportParametersStaxHandler extends DefaultHandler {

    private StaxParser parser;
    
    private ReportParametersVo reportParametersVo = new ReportParametersVo();
    
    private ParameterVo parameterVo = null;
    
    /**
     * Cosntructor with StaxParser.
     * 
     * @param parser
     *            StaxParser
     * 
     * @sm
     */
    public ReportParametersStaxHandler(final StaxParser parser) {
        this.parser = parser;
    }

    /**
     * Handle startElement event.
     * 
     * @param element startElement
     * @return StartElement startElement
     * @throws Exception e
     * 
     * @sm
     */
    public StartElement startElement(final StartElement element) throws Exception {
        if ("report-definition".equals(element.getLocalName())) {
            try {
                reportParametersVo.setReportDefinitionId(
                        XmlUtility.getIdFromStartElement(element));
            } catch (MissingAttributeValueException e) {}
        }
        else if ("parameter".equals(element.getLocalName())) {
            parameterVo = new ParameterVo();
        }
        return element;
    }

    /**
     * Handle endElement event.
     * 
     * @param element endElement
     * @return EndElement endElement
     * @throws Exception e
     * 
     * @sm
     */
    public EndElement endElement(final EndElement element) throws Exception {
        if ("parameter".equals(element.getLocalName())) {
            reportParametersVo.getParameterVos().add(parameterVo);
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
     * @throws Exception e
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#characters
     *      (java.lang.String,
     *      de.escidoc.core.common.util.xml.stax.events.StartElement)
     * @om
     */
    public String characters(
                final String s, 
                final StartElement element) 
                    throws Exception {
        if ("datevalue".equals(element.getLocalName())) {
            parameterVo.setDateValue(new DateTime(s));
        }
        else if ("stringvalue".equals(element.getLocalName())) {
            if (parameterVo.getStringValue() != null) {
                parameterVo.setStringValue(
                    parameterVo.getStringValue() + s);
            } else {
                parameterVo.setStringValue(s);
            }
        }
        else if ("decimalvalue".equals(element.getLocalName())) {
            parameterVo.setDecimalValue(new Double(s));
        }
        return s;
    }

    /**
     * @return the reportParametersVo
     */
    public ReportParametersVo getReportParametersVo() {
        return reportParametersVo;
    }

}
