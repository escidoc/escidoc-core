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
package de.escidoc.core.om.business.stax.handler.context;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Steffen Wagner
 */
@Configurable
public class ContextPropertiesUpdateHandler extends DefaultHandler {

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    @Autowired
    @Qualifier("business.Utility")
    private Utility utility;

    private final StaxParser parser;

    private String propertiesPath = "/context/properties";

    private final String contextId;

    private final Map<String, String> changedValuesInRelsExt;

    private final Map<String, String> changedValuesInDc;

    private final List<String> deletableValues;

    private final Map<String, String> valuesToAdd;

    private final List<String> orgunits = new ArrayList<String>();

    private static final String ORGANIZATIONAL_UNIT_PATH =
        "/context/properties/organizational-units/organizational-unit";

    /**
     * Handler to update Context properties.
     *
     * @param contextId Id of the Context object.
     * @param parser    StaxParser
     */
    public ContextPropertiesUpdateHandler(final String contextId, final StaxParser parser) {
        this.contextId = contextId;
        this.propertiesPath = "/context/properties";
        this.parser = parser;

        this.changedValuesInDc = new HashMap<String, String>();
        this.changedValuesInRelsExt = new HashMap<String, String>();
        this.deletableValues = new ArrayList<String>();
        this.valuesToAdd = new HashMap<String, String>();
        deletableValues.add(Elements.ELEMENT_DESCRIPTION);
    }

    /**
     * Handle the start of an element.
     *
     * @param element The element.
     * @return The element.
     */
    @Override
    public StartElement startElement(final StartElement element) throws InvalidContentException,
        ReadonlyElementViolationException, ReadonlyAttributeViolationException, MissingAttributeValueException,
        OrganizationalUnitNotFoundException, InvalidStatusException, TripleStoreSystemException,
        IntegritySystemException, WebserverSystemException {

        final String currentPath = parser.getCurPath();
        // String theName = element.getLocalName();

        if (ORGANIZATIONAL_UNIT_PATH.equals(currentPath)) {
            final String id = XmlUtility.getIdFromStartElement(element);

            this.utility.checkIsOrganizationalUnit(id);

            final String orgUnitStatus =
                this.tripleStoreUtility.getPropertiesElements(id, TripleStoreUtility.PROP_PUBLIC_STATUS);

            if (!orgUnitStatus.equals(Constants.STATUS_OU_OPENED)) {
                throw new InvalidStatusException("Organizational unit with id " + id + " should be in status "
                    + Constants.STATUS_OU_OPENED + " but is in status " + orgUnitStatus);
            }
            this.orgunits.add(id);
        }

        return element;
    }

    @Override
    public EndElement endElement(final EndElement element) throws Exception {
        return element;
    }

    @Override
    public String characters(final String data, final StartElement element) throws SystemException,
        MissingElementValueException {
        final String curPath = parser.getCurPath();

        if (curPath.startsWith(this.propertiesPath)) {
            // name
            if (curPath.equals(this.propertiesPath + '/' + Elements.ELEMENT_NAME)) {
                if (data.length() == 0) {
                    throw new MissingElementValueException("element 'name' is empty.");
                }
                if (checkValueChanged(Elements.ELEMENT_NAME, data)) {
                    this.changedValuesInDc.put(Elements.ELEMENT_NAME, data);
                }
            }

            // type
            else if (curPath.equals(this.propertiesPath + '/' + Elements.ELEMENT_TYPE)) {
                if (data.length() == 0) {
                    throw new MissingElementValueException("element 'type' is empty.");
                }
                if (checkValueChanged(Elements.ELEMENT_TYPE, data)) {
                    this.changedValuesInRelsExt.put(Elements.ELEMENT_TYPE, data);
                }

            }
            // description
            else if (curPath.equals(this.propertiesPath + '/' + Elements.ELEMENT_DESCRIPTION)) {
                deletableValues.remove(Elements.ELEMENT_DESCRIPTION);
                if (this.tripleStoreUtility.getPropertiesElements(this.contextId, Constants.DC_NS_URI
                    + Elements.ELEMENT_DESCRIPTION) == null) {
                    valuesToAdd.put(Elements.ELEMENT_DESCRIPTION, data);
                }
                else {
                    if (checkValueChanged(Elements.ELEMENT_DESCRIPTION, data)) {
                        this.changedValuesInDc.put(Elements.ELEMENT_DESCRIPTION, data);
                    }
                }

            }

        }

        return data;
    }

    /**
     * Return HashMap of values which are not equal to repository (TripleStore).
     *
     * @return changed values
     */
    public Map<String, String> getChangedValuesInRelsExt() {
        return this.changedValuesInRelsExt;
    }

    /**
     * Return HashMap of values which are not equal to repository (TripleStore).
     *
     * @return changed values
     */
    public Map<String, String> getChangedValuesInDc() {
        return this.changedValuesInDc;
    }

    // FIXME ? This check requires triplestore access. Just set new datastream
    // and leave it to the resource to check if it is changed!? (FRS)

    /**
     * Check if value equals repository entry (only TripleStrore entries).
     *
     * @param key   search key
     * @param value value
     * @return true If value does not compares to the reopsitory value. false If value compares to the respository
     *         value.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    private boolean checkValueChanged(final String key, final String value) throws TripleStoreSystemException {

        final String repositoryValue =
            key.equals(Elements.ELEMENT_DESCRIPTION) ? this.tripleStoreUtility.getPropertiesElements(this.contextId,
                Constants.DC_NS_URI + key) : key.equals(Elements.ELEMENT_NAME) ? this.tripleStoreUtility
                .getTitle(this.contextId) : this.tripleStoreUtility.getPropertiesElements(this.contextId,
                Constants.PROPERTIES_NS_URI + key);

        boolean changed = false;
        if (!XmlUtility.escapeForbiddenXmlCharacters(value).equals(repositoryValue)) {
            changed = true;
        }

        return changed;
    }

    /**
     *
     * @return
     */
    public List<String> getPropertiesToRemove() {
        return this.deletableValues;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getPropertiesToAdd() {
        return this.valuesToAdd;
    }

    /**
     * Get the organizational units of the Context.
     *
     * @return organizational units
     */
    public List<String> getOrganizationalUnits() {
        return this.orgunits;
    }

}
