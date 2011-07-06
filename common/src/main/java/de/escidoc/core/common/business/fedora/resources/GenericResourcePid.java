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

package de.escidoc.core.common.business.fedora.resources;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.PropertyMapKeys;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.AddNewSubTreesToDatastream;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;
import org.springframework.beans.factory.annotation.Configurable;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Extends the Generic Resource with Object PID features. Object PIDs are stored with RELS-EXT. The timestamp is not
 * influenced by assignment methods.
 *
 * @author Steffen Wagner
 */
@Configurable(preConstruction = true)
public class GenericResourcePid extends GenericResource {

    private String objectPid;

    /**
     * Constructor.
     */
    public GenericResourcePid() {
        setPropertiesNames(expandPropertiesNames(getPropertiesNames()),
            expandPropertiesNamesMapping(getPropertiesNamesMapping()));
    }

    /**
     * Constructor.
     *
     * @param objid The id of the object in the repository.
     * @throws ResourceNotFoundException  Thrown if the resource with the provided objid was not found.
     * @throws TripleStoreSystemException Thrown in case of TripleStore error.
     * @throws WebserverSystemException   Thrown in case of internal error.
     */
    public GenericResourcePid(final String objid) throws TripleStoreSystemException, WebserverSystemException,
        ResourceNotFoundException {

        setPropertiesNames(expandPropertiesNames(getPropertiesNames()),
            expandPropertiesNamesMapping(getPropertiesNamesMapping()));
        setId(objid);
    }

    /**
     * Remove the Persistent Identifier from the Resource (ObjectPID).
     * <p/>
     * XPath for objectPid in the item XML representation is /&lt;resource&gt;/properties/pid
     * <p/>
     * ObjectPid is part of the RELS-EXT (and therefore in the TripleStore)
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    public void removeObjectPid() throws WebserverSystemException {
        final Map<String, List<StartElementWithChildElements>> deleteFromRelsExt =
            new TreeMap<String, List<StartElementWithChildElements>>();
        final List<StartElementWithChildElements> elementsToRemove = new ArrayList<StartElementWithChildElements>();

        elementsToRemove.add(new StartElementWithChildElements(Elements.ELEMENT_PID, Constants.PROPERTIES_NS_URI, null,
            null, null, null));
        deleteFromRelsExt.put("/RDF/Description/pid", elementsToRemove);
        updateRelsExt(null, deleteFromRelsExt);
        this.objectPid = null;
    }

    /**
     * Set the Persistent Identifier from the Resource (ObjectPID).
     * <p/>
     * XPath for objectPid in the item XML representation is /&lt;resource&gt;/properties/pid
     * <p/>
     * ObjectPid is part of the RELS-EXT (and therefore in the TripleStore)
     *
     * @param pid The PID which is to assign as object PID.
     * @throws SystemException Thrown in case of internal error.
     */
    public void setObjectPid(final String pid) throws SystemException {

        if (!validPidStructure(pid)) {
            throw new SystemException("Invalid structure for Persistent Identifier");
        }
        updateRelsExtWithObjectPid(pid);
        this.objectPid = pid;
    }

    /**
     * Get the object PID.
     *
     * @return The objPid or null if no object PID is assigned.
     * @throws TripleStoreSystemException Thrown if TripleStore request fails.
     * @throws WebserverSystemException   Thrown if TripleStore instance failed.
     */
    public String getObjectPid() throws TripleStoreSystemException, WebserverSystemException {

        // TODO use objectPid from the propertiesMap and avoid a second
        // parameter
        if (this.objectPid == null) {
            this.objectPid = getProperty(PropertyMapKeys.OBJECT_PID);
            // FIXME
            // sche: It seems that the key used for object PID differs between
            // Item and Component.
            if (this.objectPid == null) {
                this.objectPid = getProperty(TripleStoreUtility.PROP_OBJECT_PID);
            }
            // getTripleStoreUtility().getPropertiesElements(getId(),
            // TripleStoreUtility.PROP_OBJECT_PID);
            if (!validPidStructure(this.objectPid)) {
                this.objectPid = null;
            }
        }
        return this.objectPid;

    }

    /**
     * Check if Container (Object) has Persistent Identifier (ObjectPID).
     * <p/>
     * XPath for objectPid in the item XML representation is /&lt;resource&gt;/properties/pid
     * <p/>
     * ObjectPid is part of the RELS-EXT (and therefore in the TripleStore)
     *
     * @return true if Item has objectPid, false otherwise
     * @throws TripleStoreSystemException Thrown if TripleStore request fails.
     * @throws WebserverSystemException   Thrown in case of internal error.
     */
    public boolean hasObjectPid() throws TripleStoreSystemException, WebserverSystemException {

        return getObjectPid() != null;
    }

    /**
     * Check if the structure of the PID has valid structure in relation to the defined requirements.
     *
     * @param pid The to validate PID.
     * @return true if the structure is valid, false otherwise.
     */
    public static boolean validPidStructure(final CharSequence pid) {
        return pid != null && pid.length() > 0;
    }

    /**
     * Expand a list with names of properties values with the propertiesNames for a versionated resource. These list
     * could be used to request the TripleStore.
     *
     * @param propertiesNames Collection of propertiesNames. The collection contains only the version resource specific
     *                        propertiesNames.
     * @return Parameter name collection
     */
    private static Collection<String> expandPropertiesNames(final Collection<String> propertiesNames) {

        final Collection<String> newPropertiesNames =
            propertiesNames != null ? propertiesNames : new ArrayList<String>();

        newPropertiesNames.add(TripleStoreUtility.PROP_OBJECT_PID);

        return newPropertiesNames;
    }

    /**
     * Update RELS-EXT with object PID. The pid is written to RELS-EXT ( {@code properties/pid}).
     *
     * @param pid persistent identifier
     * @throws FedoraSystemException      If Fedora reports an error.
     * @throws EncodingSystemException    If an encoding failure occurs.
     * @throws XmlParserSystemException   If parsing of XMl data fails.
     * @throws WebserverSystemException   In case of an internal error.
     * @throws TripleStoreSystemException Thrown if TripleStore requests fail.
     */
    private void updateRelsExtWithObjectPid(final String pid) throws XmlParserSystemException, EncodingSystemException,
        FedoraSystemException, WebserverSystemException {

        // TODO we haven't defined a non-versioned resource where we use
        // objectPid. So this method is more or less untested.
        final StaxParser sp = new StaxParser();

        final StartElementWithChildElements pidElement = new StartElementWithChildElements();
        pidElement.setLocalName(Elements.ELEMENT_PID);
        pidElement.setPrefix(Constants.PROPERTIES_NS_PREFIX);
        pidElement.setNamespace(Constants.PROPERTIES_NS_URI);
        pidElement.setElementText(pid);

        final AddNewSubTreesToDatastream addNewSubtreesHandler = new AddNewSubTreesToDatastream("/RDF", sp);
        final StartElement pointer =
            new StartElement("Description", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf", null);
        addNewSubtreesHandler.setPointerElement(pointer);
        final List<StartElementWithChildElements> elements = new ArrayList<StartElementWithChildElements>();
        elements.add(pidElement);
        addNewSubtreesHandler.setSubtreeToInsert(elements);
        sp.addHandler(addNewSubtreesHandler);

        try {
            sp.parse(getRelsExt().toStringUTF8());
        }
        catch (final XMLStreamException e) {
            throw new XmlParserSystemException(e);
        }
        catch (final Exception e) {
            throw new XmlParserSystemException("Unexpected Exception " + e);
        }
        setRelsExt(addNewSubtreesHandler.getOutputStreams());
    }

    /**
     * Expand the map for the to mapping key names. The properties key names from the TripleStore differ to the internal
     * representation. Therefore we translate the key names to the internal.
     *
     * @param propertiesNamesMap The key is the to replace value. E.g. the &lt;oldKeyName, newKeyName&gt;
     * @return propertiesNamesMappingMap
     */
    private static Map<String, String> expandPropertiesNamesMapping(final Map<String, String> propertiesNamesMap) {

        final Map<String, String> newPropertiesNamesMap =
            propertiesNamesMap != null ? propertiesNamesMap : new HashMap<String, String>();

        newPropertiesNamesMap.put(TripleStoreUtility.PROP_OBJECT_PID, PropertyMapKeys.OBJECT_PID);

        return newPropertiesNamesMap;
    }

}
