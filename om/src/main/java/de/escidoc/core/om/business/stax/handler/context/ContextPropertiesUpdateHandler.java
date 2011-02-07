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

import java.util.HashMap;
import java.util.Vector;

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
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;

/**
 * 
 * @author SWA
 * 
 */
public class ContextPropertiesUpdateHandler extends DefaultHandler {

    private static AppLogger log =
        new AppLogger(ContextPropertiesUpdateHandler.class.getName());

    private StaxParser parser = null;

    private String propertiesPath = "/context/properties";

    private String contextId = null;

    // private List expected = null;

    private HashMap<String, String> changedValuesInRelsExt = null;

    private HashMap<String, String> changedValuesInDc = null;

    private Vector<String> deletableValues = null;

    private HashMap<String, String> valuesToAdd = null;

    private final Vector<String> orgunits = new Vector<String>();

    private final String organizationalUnitPath =
        "/context/properties/organizational-units/organizational-unit";

    /**
     * Handler to update Context properties.
     * 
     * @param contextId
     *            Id of the Context object.
     * @param parser
     *            StaxParser
     */
    public ContextPropertiesUpdateHandler(final String contextId,
        final StaxParser parser) {
        this.contextId = contextId;
        this.propertiesPath = "/context/properties";
        this.parser = parser;

        this.changedValuesInDc = new HashMap<String, String>();
        this.changedValuesInRelsExt = new HashMap<String, String>();
        deletableValues = new Vector<String>();
        valuesToAdd = new HashMap<String, String>();
        deletableValues.add(Elements.ELEMENT_DESCRIPTION);
    }

    /**
     * Handle the start of an element.
     * 
     * @param element
     *            The element.
     * @return The element.
     * @throws MissingAttributeValueException
     * @throws OrganizationalUnitNotFoundException
     * @throws InvalidStatusException
     * @throws SystemException
     * @om
     */
    @Override
    public StartElement startElement(StartElement element)
        throws InvalidContentException, ReadonlyElementViolationException,
        ReadonlyAttributeViolationException, MissingAttributeValueException,
        OrganizationalUnitNotFoundException, InvalidStatusException,
        SystemException {

        String currentPath = parser.getCurPath();
        // String theName = element.getLocalName();

        if (organizationalUnitPath.equals(currentPath)) {
            String id = XmlUtility.getIdFromStartElement(element);

            Utility.getInstance().checkIsOrganizationalUnit(id);

            final String orgUnitStatus =
                TripleStoreUtility.getInstance().getPropertiesElements(id,
                    TripleStoreUtility.PROP_PUBLIC_STATUS);

            if (!orgUnitStatus
                .equals(de.escidoc.core.common.business.Constants.STATUS_OU_OPENED)) {
                final String message =
                    "organizational-unit with id "
                        + id
                        + " should be in status "
                        + de.escidoc.core.common.business.Constants.STATUS_OU_OPENED
                        + " but is in status " + orgUnitStatus;

                log.error(message);
                throw new InvalidStatusException(message);
            }
            this.orgunits.add(id);
            // this.propertiesMap.put(
            // Elements.ELEMENT_ORGANIZATIONAL_UNITS, this.orgunits);

        }

        // if (curPath.startsWith(propertiesPath)) {
        // if (curPath.equals(propertiesPath
        // + "/organizational-units/organizational-unit")) {
        //
        // if (UserContext.isRestAccess()) {
        // try {
        // String xlinkType =
        // element
        // .getAttribute(
        // de.escidoc.core.common.business.Constants.XLINK_URI,
        // "type").getValue();
        // if (xlinkType == null || !xlinkType.equals("simple")) {
        // throw new ReadonlyAttributeViolationException(
        // "xlink:type is not simple.");
        // }
        //
        // // String xlinkTitle = element.getAttribute(
        // // de.escidoc.core.common.business.Constants.XLINK_URI,
        // // "title").getValue();
        //
        // // checkAttributeValue("creator-title", xlinkTitle);
        // // properties.put("creator-title", xlinkTitle);
        //
        // // FIXME update of organizational unit must be
        // // implemented !!!
        // String xlinkHref =
        // element
        // .getAttribute(
        // de.escidoc.core.common.business.Constants.XLINK_URI,
        // "href").getValue();
        // checkAttributeValue("organizational-unit", XmlUtility
        // .getIdFromURI(xlinkHref));
        // }
        // catch (NoSuchAttributeException e) {
        // throw new ReadonlyAttributeViolationException(e);
        // }
        // }
        // }
        // }

        return element;
    }

    public EndElement endElement(EndElement element) throws Exception {
        return element;
    }

    // private void checkAttributeValue(String key, String val)
    // throws ReadonlyElementViolationException,
    // ReadonlyAttributeViolationException, SystemException {
    // checkValue(key, val, true);
    //
    // }

    public String characters(String data, StartElement element)
        throws Exception {
        String curPath = parser.getCurPath();

        if (curPath.startsWith(propertiesPath)) {
            // name
            if (curPath.equals(propertiesPath + "/" + Elements.ELEMENT_NAME)) {
                if (data.equals("")) {
                    throw new MissingElementValueException(
                        "element 'name' is empty.");
                }
                if (checkValueChanged(Elements.ELEMENT_NAME, data)) {
                    this.changedValuesInDc.put(Elements.ELEMENT_NAME, data);
                }
            }
            // // status
            // else if (curPath.equals(propertiesPath + "/"
            // + TripleStoreUtility.PROP_PUBLIC_STATUS)) {
            // checkElementValue(TripleStoreUtility.PROP_PUBLIC_STATUS, data);
            // }
            // type
            else if (curPath.equals(propertiesPath + "/"
                + Elements.ELEMENT_TYPE)) {
                if (data.equals("")) {
                    throw new MissingElementValueException(
                        "element 'type' is empty.");
                }
                if (checkValueChanged(Elements.ELEMENT_TYPE, data)) {
                    this.changedValuesInRelsExt
                        .put(Elements.ELEMENT_TYPE, data);
                }

            }
            // description
            else if (curPath.equals(propertiesPath + "/"
                + Elements.ELEMENT_DESCRIPTION)) {
                deletableValues.remove(Elements.ELEMENT_DESCRIPTION);
                if (TripleStoreUtility.getInstance().getPropertiesElements(
                    contextId,
                    de.escidoc.core.common.business.Constants.DC_NS_URI
                        + Elements.ELEMENT_DESCRIPTION) == null) {
                    valuesToAdd.put(Elements.ELEMENT_DESCRIPTION, data);
                }
                else {
                    if (checkValueChanged(Elements.ELEMENT_DESCRIPTION, data)) {
                        this.changedValuesInDc.put(
                            Elements.ELEMENT_DESCRIPTION, data);
                    }
                }

            }
            // creation-date
            // else if (curPath.equals(propertiesPath + "/"
            // + TripleStoreUtility.PROP_CONTEXT_CREATION_DATE)) {
            // String curVal =
            // TripleStoreUtility.getInstance().getCreationDate(contextId);
            // if (!data.equals(curVal)) {
            // throw new ReadonlyElementViolationException(
            // "Context properties has invalid creation-date.");
            // }
            // }
            // // last-modification-data
            // else if (curPath.equals(propertiesPath +
            // "/last-modification-date")) {
            // String curVal =
            // TripleStoreUtility.getInstance().getLastModificationDate(
            // contextId);
            // if (!data.equals(curVal)) {
            // throw new ReadonlyElementViolationException(
            // "Context properties has invalid last-modification-date.");
            // }
            // }
        }

        return data;
    }

    /**
     * Return HashMap of values which are not equal to repository (TripleStore).
     * 
     * @return changed values
     */
    public HashMap<String, String> getChangedValuesInRelsExt() {
        return (this.changedValuesInRelsExt);
    }

    /**
     * Return HashMap of values which are not equal to repository (TripleStore).
     * 
     * @return changed values
     */
    public HashMap<String, String> getChangedValuesInDc() {
        return (this.changedValuesInDc);
    }

    // private void checkValue(String key, String val, boolean isAttribute)
    // throws ReadonlyElementViolationException,
    // ReadonlyAttributeViolationException, SystemException {
    // String curVal =
    // TripleStoreUtility
    // .getInstance()
    // .getPropertiesElements(
    // contextId,
    // key,
    // de.escidoc.core.common.business.Constants.CONTEXT_PROPERTIES_NAMESPACE_URI);
    // if (!curVal.equals(val)) {
    // String msg =
    // "Property " + key + " can not be updated to " + val + ".";
    // if (isAttribute) {
    // throw new ReadonlyAttributeViolationException(msg);
    // }
    // else {
    // throw new ReadonlyElementViolationException(msg);
    // }
    // }
    //
    // }

    // /**
    // * Check if element value was altered.
    // *
    // * @param key
    // * @param val
    // * @throws ReadonlyElementViolationException
    // * @throws ReadonlyAttributeViolationException
    // * @throws SystemException
    // */
    // private void checkElementValue(String key, String val)
    // throws ReadonlyElementViolationException,
    // ReadonlyAttributeViolationException, SystemException {
    // checkValue(key, val, false);
    // }

    // FIXME ? This check requires triplestore access. Just set new datastream
    // and leave it to the resource to check if it is changed!? (FRS)
    /**
     * Check if value equals repository entry (only TripleStrore entries).
     * 
     * 
     * @param key
     *            search key
     * @param value
     *            value
     * @return true If value does not compares to the reopsitory value. false If
     *         value compares to the respository value.
     * @throws SystemException
     *             In case of TripeStore access error.
     */
    private boolean checkValueChanged(final String key, final String value)
        throws SystemException {
        boolean changed = false;
        String repositoryValue = null;

        if (key.equals(Elements.ELEMENT_DESCRIPTION)) {
            repositoryValue =
                TripleStoreUtility.getInstance().getPropertiesElements(
                    contextId,
                    de.escidoc.core.common.business.Constants.DC_NS_URI + key);

        }
        else if (key.equals(Elements.ELEMENT_NAME)) {
            repositoryValue =
                TripleStoreUtility.getInstance().getTitle(contextId);
        }
        else {

            repositoryValue =
                TripleStoreUtility.getInstance().getPropertiesElements(
                    contextId,
                    de.escidoc.core.common.business.Constants.PROPERTIES_NS_URI
                        + key);
        }

        if (!XmlUtility.escapeForbiddenXmlCharacters(value).equals(
            repositoryValue)) {
            changed = true;
        }

        return (changed);
    }

    /**
     * 
     * @return
     */
    public Vector<String> getPropertiesToRemove() {
        return deletableValues;
    }

    /**
     * 
     * @return
     */
    public HashMap<String, String> getPropertiesToAdd() {
        return valuesToAdd;
    }

    /**
     * Get the organizational units of the Context.
     * 
     * @return organizational units
     */
    public Vector<String> getOrganizationalUnits() {
        return this.orgunits;
    }

}
