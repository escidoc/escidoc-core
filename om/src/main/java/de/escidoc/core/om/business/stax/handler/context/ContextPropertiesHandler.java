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
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.naming.directory.NoSuchAttributeException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The PropertiesHandler. Verifies the elements {@code organizational-unit} of a properties snippet of a parsed
 * context XML. These elements must contain {@code xlink:href} attribute.
 */
@Configurable
public class ContextPropertiesHandler extends DefaultHandler {

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    @Autowired
    @Qualifier("business.Utility")
    private Utility utility;

    private final Map<String, Object> propertiesMap = new HashMap<String, Object>();

    private final StaxParser parser;

    private final List<String> orgunits = new ArrayList<String>();

    /**
     * @param parser StaxParser
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
    public String characters(final String data, final StartElement element) throws InvalidStatusException,
        OrganizationalUnitNotFoundException, ReadonlyAttributeViolationException, MissingElementValueException,
        IntegritySystemException, TripleStoreSystemException, WebserverSystemException {
        final String curPath = parser.getCurPath();

        final String propertiesPath = "/context/properties";
        if (curPath.startsWith(propertiesPath + '/')) {
            final String theName = element.getLocalName();

            // organizational-unit
            if (curPath.equals(propertiesPath + "/organizational-units/organizational-unit")) {

                try {
                    final String xlinkHref = element.getAttribute(Constants.XLINK_URI, "href").getValue();
                    final String id = XmlUtility.getIdFromURI(xlinkHref);

                    if (!xlinkHref.equals("/oum/organizational-unit/" + id)) {
                        throw new OrganizationalUnitNotFoundException("The 'organizational-unit' element has a wrong "
                            + "url. the url have to look like: " + "/oum/organizational-unit/id");
                    }
                    this.utility.checkIsOrganizationalUnit(id);

                    final String orgUnitStatus =
                        this.tripleStoreUtility.getPropertiesElements(id, TripleStoreUtility.PROP_PUBLIC_STATUS);

                    if (!orgUnitStatus.equals(Constants.STATUS_OU_OPENED)) {
                        throw new InvalidStatusException("organizational-unit with id " + id + " should be in status "
                            + Constants.STATUS_OU_OPENED + " but is in status " + orgUnitStatus);
                    }
                    this.orgunits.add(id);
                    this.propertiesMap.put(Elements.ELEMENT_ORGANIZATIONAL_UNITS, this.orgunits);
                }
                catch (final NoSuchAttributeException e) {
                    throw new ReadonlyAttributeViolationException(e);
                }
            }
            else if (theName.equals(Elements.ELEMENT_NAME)) {
                if (data.length() == 0) {
                    throw new MissingElementValueException("The value of the element " + theName + " is missing");
                }
                else {
                    // propertiesMap.put(theName, data);
                    propertiesMap.put(Elements.ELEMENT_NAME, data);
                }
            }
            else if (theName.equals(Elements.ELEMENT_TYPE)) {
                if (data.length() == 0) {
                    throw new MissingElementValueException("The value of the element " + theName + " is missing");
                }
                else {
                    propertiesMap.put(Elements.ELEMENT_TYPE, data);
                }
            }
            else if (theName.equals(Elements.ELEMENT_DESCRIPTION)) {
                if (data.length() == 0) {
                    propertiesMap.put(Elements.ELEMENT_DESCRIPTION, "");
                }
                else {
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
