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

import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.naming.directory.NoSuchAttributeException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class WovContentRelationsRetrieveHandler extends DefaultHandler {

    private boolean inside = false;

    private int insideLevel = 0;

    private final StaxParser parser;

    private GregorianCalendar sourceVersionTimeStamp = null;

    private boolean isParsed = false;

    private GregorianCalendar latestStatusTimestamp;

    private String status = "inactive";

    public WovContentRelationsRetrieveHandler(StaxParser parser, String sourceVersionTimeStamp)
            throws WebserverSystemException {
        this.parser = parser;
        try {
            this.sourceVersionTimeStamp =
                DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    sourceVersionTimeStamp).toGregorianCalendar();
            this.latestStatusTimestamp =
                DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    "1970-01-01T00:00:00.000Z").toGregorianCalendar();
        }
        catch (DatatypeConfigurationException e) {
            throw new WebserverSystemException(e);
        }
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public StartElement startElement(StartElement element)
        throws IntegritySystemException {
        if (!isParsed) {
            String elementPath = "/version-history/version";
//            String theName = element.getLocalName();
            String currentPath = parser.getCurPath();

            if (elementPath.equals(currentPath)) {
                inside = true;
                insideLevel++;
                // String namespace = element.getNamespace();
                // properties.put("namespaceUri", namespace);

            }
            else if (inside) {
                insideLevel++;
            }
        }
        return null;
    }

    @Override
    public EndElement endElement(EndElement element) {

//        String theName = element.getLocalName();
        if (inside) {
            insideLevel--;
            if (insideLevel == 0) {
                inside = false;
            }
        }

        return null;
    }

    @Override
    public String characters(String s, StartElement element)
        throws IntegritySystemException, XmlParserSystemException {
        if (inside) {
            String theName = element.getLocalName();
            try {
                if (theName.equals(Elements.ELEMENT_WOV_VERSION_STATUS)) {
                    String elementTimestampString =
                        element.getAttribute(null, "timestamp").getValue();
                    GregorianCalendar elementTimestamp =
                        DatatypeFactory.newInstance().newXMLGregorianCalendar(
                            elementTimestampString).toGregorianCalendar();
                    if ((sourceVersionTimeStamp.after(elementTimestamp))
                        && (elementTimestamp.after(latestStatusTimestamp))) {
                        latestStatusTimestamp = elementTimestamp;
                        this.status = s;
                    }
                }
            }
            catch (NoSuchAttributeException e) {
                throw new IntegritySystemException(e);
            }
            catch (DatatypeConfigurationException e) {
                throw new XmlParserSystemException(e);
            }

        }
        return null;
    }

}
