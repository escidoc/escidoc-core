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
/**
 * 
 */
package de.escidoc.core.om.business.stax.handler.context;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

import javax.naming.directory.NoSuchAttributeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The PropertiesHandler. Verifies the elements <code>organizational-unit</code>
 * of a properties snippet of a parsed context XML. These elements must contain
 * <code>xlink:href</code> attribute in the REST case and <code>objid</code>
 * attribute in the SOAP case.
 * 
 *
 */
public class ContextPropertiesHandler extends DefaultHandler {

    private final Map<String, Object> propertiesMap = new HashMap<String, Object>();

    private final StaxParser parser;

    private String propertiesPath = "/context/properties";

    private final List<String> orgunits = new ArrayList<String>();

    /**
     * 
     * @param propertiesPath
     *            XPath to properties.
     * @param parser
     *            StaxParser
     */
    public ContextPropertiesHandler(final String propertiesPath,
        final StaxParser parser) {
        this.propertiesPath = propertiesPath;
        this.parser = parser;
    }

    /**
     * 
     * @param parser
     *            StaxParser
     */
    public ContextPropertiesHandler(final StaxParser parser) {
        this.parser = parser;
    }

    @Override
    public StartElement startElement(final StartElement element) {
        return element;
    }

    @Override
    public EndElement endElement(final EndElement element) {
        // String theName = element.getLocalName();
        // TODO
        return element;
    }

    @Override
    public String characters(final String data, final StartElement element)
        throws InvalidStatusException, OrganizationalUnitNotFoundException,
        SystemException, ReadonlyAttributeViolationException,
        MissingElementValueException {
        final String curPath = parser.getCurPath();

        if (curPath.startsWith(this.propertiesPath + '/')) {
            final String theName = element.getLocalName();

            // organizational-unit
            if (curPath.equals(this.propertiesPath
                + "/organizational-units/organizational-unit")) {

                try {
                    final String id;
                    if (UserContext.isRestAccess()) {
                        final String xlinkHref =
                            element
                                .getAttribute(
                                    Constants.XLINK_URI,
                                    "href").getValue();
                        id = XmlUtility.getIdFromURI(xlinkHref);

                        if (!xlinkHref.equals("/oum/organizational-unit/" + id)) {
                            throw new OrganizationalUnitNotFoundException(
                                "The 'organizational-unit' element has a wrong "
                                    + "url. the url have to look like: "
                                    + "/oum/organizational-unit/id");
                        }
                    }
                    else {
                        // SOAP access
                        id = element.getAttributeValue(null, "objid");
                    }

                    Utility.getInstance().checkIsOrganizationalUnit(id);

                    final String orgUnitStatus =
                        TripleStoreUtility.getInstance().getPropertiesElements(
                            id, TripleStoreUtility.PROP_PUBLIC_STATUS);

                    if (!orgUnitStatus
                        .equals(Constants.STATUS_OU_OPENED)) {
                        throw new InvalidStatusException("organizational-unit with id "
                                + id
                                + " should be in status "
                                + Constants.STATUS_OU_OPENED
                                + " but is in status " + orgUnitStatus);
                    }
                    this.orgunits.add(id);
                    this.propertiesMap.put(
                        Elements.ELEMENT_ORGANIZATIONAL_UNITS, this.orgunits);
                }
                catch (final NoSuchAttributeException e) {
                    throw new ReadonlyAttributeViolationException(e);
                }
            }
            else if (theName.equals(Elements.ELEMENT_NAME)) {
                if (data.length() == 0) {
                    throw new MissingElementValueException(
                        "The value of the element " + theName + " is missing");
                }
                else {
                    // propertiesMap.put(theName, data);
                    propertiesMap.put(Elements.ELEMENT_NAME, data);
                }
            }
            else if (theName.equals(Elements.ELEMENT_TYPE)) {
                if (data.length() == 0) {
                    throw new MissingElementValueException(
                        "The value of the element " + theName + " is missing");
                }
                else {
                    propertiesMap.put(Elements.ELEMENT_TYPE, data);
                }
            }
            else if (theName.equals(Elements.ELEMENT_DESCRIPTION)) {
                if (data.length() == 0) {
                    // int index =
                    // element.indexOfAttribute(
                    // de.escidoc.core.common.business.Constants.XLINK_URI,
                    // "href");
                    // String hrefVal = element.getAttribute(index).getValue();
                    // propertiesMap.put(element.getLocalName(), hrefVal);
                    // propertiesMap.put(theName, "");
                    propertiesMap.put(Elements.ELEMENT_DESCRIPTION, "");
                }
                else {
                    // propertiesMap.put(theName, data);
                    propertiesMap.put(Elements.ELEMENT_DESCRIPTION, data);
                }
            }
        }

        return data;
    }

    /**
     * Return property elements as HashMap without Organizational Units.
     * 
     * @return map of properties without organizational units.
     */
    public Map<String, Object> getPropertiesMap() {
        return this.propertiesMap;
    }

    /**
     * Get the organizational units of the Context.
     * 
     * @return organizational units
     */
    public Collection<String> getOrganizationalUnits() {
        return this.orgunits;
    }
}
