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
package de.escidoc.core.om.business.fedora.context;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.PropertyMapKeys;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.GenericResource;
import de.escidoc.core.common.business.fedora.resources.interfaces.ContextInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Context.
 * 
 * @author SWA
 * 
 */
public class Context extends GenericResource implements ContextInterface {

    private static final AppLogger LOGGER = new AppLogger(Context.class.getName());

    private Datastream dc = null;

    private Datastream properties = null;

    private Datastream resources = null;

    private final Map<String, Datastream> adminDescriptors =
        new HashMap<String, Datastream>();

    private final Datastream organizationalUnits = null;

    private boolean ouUpdated = false;

    /**
     * Instantiates the Context with the specified id. The datastreams are
     * instantiated and retrieved if the related getter is called.
     * 
     * @param id
     *            The id of an context managed in Fedora.
     * @throws ContextNotFoundException
     *             Thrown if Context with id could not be found.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    public Context(final String id) throws ContextNotFoundException,
        SystemException {

        super(id);
        setPropertiesNames(expandPropertiesNames(getPropertiesNames()),
            expandPropertiesNamesMapping(getPropertiesNamesMapping()));

        checkContextExist();
    }

    /**
     * Get Resources datastream.
     * 
     * @return Datastream of resources.
     * @throws StreamNotFoundException
     *             If datastream not exists.
     * @throws FedoraSystemException
     *             If datastream is not accessible.
     */
    public Datastream getResources() throws StreamNotFoundException,
        FedoraSystemException {
        // if properties is unset, instantiate the Datastream
        if (this.resources == null) {
            this.resources = new Datastream("resources", getId(), null);
        }
        return this.properties;
    }

    /**
     * Set Resource datastream.
     * 
     * @param ds
     *            new resource datastream.
     * @throws StreamNotFoundException
     *             If resource datastream could not be found.
     * 
     * @throws SystemException
     *             If anything else fails.
     */
    public void setResources(final Datastream ds)
        throws StreamNotFoundException, SystemException {
        // check if properties is set, is equal to ds and save to fedora
        try {
            final Datastream curDs = getProperties();
            if (!ds.equals(curDs)) {
                this.resources = ds;
                ds.merge();
            }
        }
        catch (final StreamNotFoundException e) {
            // A context have to have a properties datastream
            throw new StreamNotFoundException("No properties for context "
                + getId() + ".", e);
        }
        // getSomeValuesFromFedora();

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.fedora.resources.interfaces.ContextInterface
     * #getProperties()
     */
    public Datastream getProperties() throws StreamNotFoundException,
        FedoraSystemException {
        // if properties is unset, instantiate the Datastream
        if (this.properties == null) {
            this.properties = new Datastream("properties", getId(), null);
        }
        return this.properties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.fedora.resources.interfaces.ContextInterface
     * #
     * setProperties(de.escidoc.core.common.business.fedora.datastream.Datastream
     * )
     */
    public void setProperties(final Datastream ds)
        throws StreamNotFoundException, SystemException {
        // check if properties is set, is equal to ds and save to fedora
        try {
            final Datastream curDs = getProperties();
            if (!ds.equals(curDs)) {
                this.properties = ds;
                ds.merge();
            }
        }
        catch (final StreamNotFoundException e) {
            // A context have to have a properties datastream
            throw new StreamNotFoundException("No properties for context "
                + getId() + ".", e);
        }
        // getSomeValuesFromFedora();
    }

    /**
     * @return Return Organizational Units.
     */
    public Datastream getOrganizationalUnitsDS() {
        return this.organizationalUnits;
    }

    public void setOrganizationalUnits(final List<String> ous)
        throws InvalidContentException, TripleStoreSystemException,
        WebserverSystemException {

        // check that at least one OU is given
        if (ous.size() == 0) {
            final String message =
                "No 'organizational-unit' element is given. ";
            LOGGER.error(message);
            throw new InvalidContentException(message);
        }

        final List<String> currentOus = getOrganizationalUnitObjids();

        // merge new OUS with existing ------------------------------
        // remove
        final TreeMap<String, List<StartElementWithChildElements>> elementsToRemove =
            new TreeMap<String, List<StartElementWithChildElements>>();

        Iterator<String> it = currentOus.iterator();
        while (it.hasNext()) {
            final String ou = it.next();
            if (!ous.contains(ou)) {
                this.ouUpdated = true;
                final StartElementWithChildElements ouToRemove =
                    new StartElementWithChildElements();
                ouToRemove.setLocalName(Elements.ELEMENT_ORGANIZATIONAL_UNIT);
                ouToRemove.setPrefix(Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
                ouToRemove.setNamespace(Constants.STRUCTURAL_RELATIONS_NS_URI);

                final Attribute resource =
                    new Attribute("resource", Constants.RDF_NAMESPACE_URI,
                        Constants.RDF_NAMESPACE_PREFIX, "info:fedora/" + ou);

                ouToRemove.addAttribute(resource);
                ouToRemove.setChildrenElements(null);

                List<StartElementWithChildElements> toRemove =
                    new ArrayList<StartElementWithChildElements>();
                toRemove.add(ouToRemove);
                elementsToRemove.put("/RDF/Description/"
                    + Elements.ELEMENT_ORGANIZATIONAL_UNIT, toRemove);

                it.remove();
            }
        }

        // add
        final List<StartElementWithChildElements> elementsToAdd =
            new ArrayList<StartElementWithChildElements>();

        it = ous.iterator();
        while (it.hasNext()) {
            final String ou = it.next();

            if (!currentOus.contains(ou)) {
                this.ouUpdated = true;
                currentOus.add(ou);

                // create elements for RELS-EXT update
                final StartElementWithChildElements ouElement =
                    new StartElementWithChildElements();
                ouElement.setLocalName(Elements.ELEMENT_ORGANIZATIONAL_UNIT);
                ouElement.setPrefix(Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
                ouElement.setNamespace(Constants.STRUCTURAL_RELATIONS_NS_URI);
                ouElement.setElementText(ou);

                elementsToAdd.add(ouElement);
            }
        }

        try {
            final byte[] relsExtNewBytes =
                Utility.updateRelsExt(elementsToAdd, elementsToRemove, null,
                    this, null);
            setRelsExt(relsExtNewBytes);
        }
        catch (final IntegritySystemException e) {
            throw new WebserverSystemException(e);
        }
        catch (final FedoraSystemException e) {
            throw new WebserverSystemException(e);
        }
        catch (final XmlParserSystemException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * Get href of context.
     * 
     * @return href.
     */
    public String getHref() {
        return (Constants.CONTEXT_URL_BASE + getId());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.fedora.resources.interfaces.ContextInterface
     * #getRelsExt()
     */
    public Datastream getDc() throws StreamNotFoundException,
        FedoraSystemException {
        if (this.dc == null) {
            this.dc = new Datastream("DC", getId(), null);
        }
        return this.dc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.fedora.resources.interfaces.ContextInterface
     * #setRelsExt(de.escidoc.core.common.business.fedora.datastream.Datastream)
     */
    public void setDc(final Datastream ds) throws StreamNotFoundException,
        FedoraSystemException, WebserverSystemException,
        TripleStoreSystemException {
        // check if dc is set, is equal to ds and save to fedora
        try {
            final Datastream curDs = getDc();
            if (!ds.equals(curDs)) {
                this.dc = ds;
                ds.merge();
            }
        }
        catch (final StreamNotFoundException e) {
            // An item have to have a RELS-EXT datastream
            throw new StreamNotFoundException("No DC for context " + getId()
                + ".", e);
        }
        // getSomeValuesFromFedora();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.fedora.resources.interfaces.FedoraResource
     * #getAdminDescriptors()
     */
    public Map<String, Datastream> getAdminDescriptors()
        throws IntegritySystemException, FedoraSystemException {

        final Map<String, Datastream> result =
            new HashMap<String, Datastream>();
        final org.fcrepo.server.types.gen.Datastream[] datastreams =
            getFedoraUtility().getDatastreamsInformation(getId(), null);
        final List<String> names = new ArrayList<String>();

        for (org.fcrepo.server.types.gen.Datastream datastream : datastreams) {
            final String[] altIDs = datastream.getAltIDs();
            if ((altIDs.length > 0)
                    && (altIDs[0]
                    .equals(de.escidoc.core.common.business.fedora.Constants.ADMIN_DESCRIPTOR_ALT_ID))) {
                names.add(datastream.getID());
            }
        }

        for (String name : names) {
            final String dsNname = name;
            try {
                final Datastream newDs = new Datastream(dsNname, getId(), null);
                // new Datastream(name, getId(), this.versionDate);
                result.put(dsNname, newDs);
            } catch (final StreamNotFoundException e) {
                final String message =
                        "Admin-descriptor \"" + dsNname
                                + "\" not found for Context " + getId() + '.';
                LOGGER.error(message, e);
                throw new IntegritySystemException(message, e);
            }

        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.fedora.resources.interfaces.ContextInterface
     * #getAdminDescriptor(java.lang.String)
     */
    public Datastream getAdminDescriptor(final String adminDescriptorName)
        throws FedoraSystemException {

        return getAdminDescriptorsMap().get(adminDescriptorName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.om.business.fedora.resources.interfaces.ContextInterface
     * #setAdminDescriptor(de.escidoc.core.common.business.fedora.datastream.
     * Datastream)
     */
    public void setAdminDescriptor(final Datastream ds)
        throws FedoraSystemException, WebserverSystemException {

        // TODO never reached by create, therefore no persist is needed. Correct
        // behavior? (FRS)
        ds.merge();

    }

    /**
     * Get a map of all admin-decriptors of the Context. The map consists of
     * datastream name and the admin-descriptor datastream itself.
     * 
     * @return Map of admin-descriptors.
     * @throws FedoraSystemException
     *             Thrown if retrieve of datastreams fail.
     */
    public Map<String, Datastream> getAdminDescriptorsMap()
        throws FedoraSystemException {

        final List<String> dsNames =
            getFedoraUtility()
                .getDatastreamNamesByAltId(
                    getId(),
                    de.escidoc.core.common.business.fedora.Constants.ADMIN_DESCRIPTOR_ALT_ID);

        // add only new Datastreams to HashMap
        for (String dsName1 : dsNames) {
            final String dsName = dsName1;
            if (!this.adminDescriptors.containsKey(dsName)) {
                try {
                    final Datastream newDs =
                            new Datastream(dsName, getId(), null);
                    this.adminDescriptors.put(dsName, newDs);
                } catch (final StreamNotFoundException e) {
                    LOGGER.error("AdminDescriptor \"" + dsName
                            + "\" not found for Context " + getId() + '.', e);
                }
            }
        }

        return (this.adminDescriptors);
    }

    /**
     * Add an AdminDescriptor to Context.
     * 
     * @param adm
     *            Admin Descriptor Datastream
     */
    public void addAdminDescriptor(final Datastream adm) {

        this.adminDescriptors.put(adm.getName(), adm);
        // TODO mark DS as not persist!
    }

    /**
     * Delete an AdminDescriptor from Context.
     * 
     * @param admDescName
     *            The name/id of the AdminDescriptor (must be unique within
     *            Context).
     * @throws FedoraSystemException
     *             Thrown if Fedora reports an error.
     * @throws WebserverSystemException
     *             Thrown if an internal error occurs.
     */
    public void deleteAdminDescriptor(final String admDescName)
        throws FedoraSystemException, WebserverSystemException {

        this.adminDescriptors.get(admDescName).delete();
    }

    /**
     * Get id of modifier.
     * 
     * @return modified-by id
     * @throws SystemException
     *             If anything fails.
     */
    public String getModifiedBy() throws SystemException {
        return getResourceProperties().get(
            PropertyMapKeys.LATEST_VERSION_MODIFIED_BY_ID);
    }

    /**
     * Get object id of organizational units of context.
     * 
     * @return Vector with organizational units object ids
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore request fails.
     * @throws WebserverSystemException
     *             If anything fails.
     */
    public List<String> getOrganizationalUnitObjids()
        throws TripleStoreSystemException, WebserverSystemException {
        return (TripleStoreUtility.getInstance().getPropertiesElementsVector(
            getId(), TripleStoreUtility.PROP_ORGANIZATIONAL_UNIT));
    }

    /**
     * Get hrefs of organizational units of context.
     * 
     * @return Vector with hrefs of organizational units.
     * @throws SystemException
     *             If anythings fails.
     */
    public List<String> getOrganizationalUnitHrefs() throws SystemException {
        final String path = "/oum/organizational-unit/";
        final List<String> propVals = getOrganizationalUnitObjids();
        final List<String> ouHrefs = new ArrayList<String>(propVals.size());
        
        for (String s : propVals) {
            ouHrefs.add(path + s);
        }

        return (ouHrefs);
    }

    /**
     * Check if resource with object id exists and has type of Context.
     * 
     * @throws ContextNotFoundException
     *             Thrown if no Context exists with this object id.
     * @throws SystemException
     *             Thrown in case of internal error.
     */
    private void checkContextExist() throws ContextNotFoundException,
        SystemException {

        try {
            Utility.getInstance().checkIsContext(getId());
        }
        catch (final ResourceNotFoundException e) {
            throw new ContextNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * Expand a list with names of properties values with the propertiesNames
     * for a versionated resource. These list could be used to request the
     * TripleStore.
     * 
     * @param propertiesNames
     *            Collection of propertiesNames. The collection contains only
     *            the version resource specific propertiesNames.
     * @return Parameter name collection
     */
    private Collection<String> expandPropertiesNames(
        final Collection<String> propertiesNames) {

        Collection<String> newPropertiesNames;
        if (propertiesNames != null) {
            newPropertiesNames = propertiesNames;
        }
        else {
            newPropertiesNames = new ArrayList<String>();
        }

        newPropertiesNames.add(TripleStoreUtility.PROP_CONTEXT_TYPE);
        newPropertiesNames.add(Constants.DC_NS_URI + "description");

        return newPropertiesNames;
    }

    /**
     * Expanding the properties naming map.
     * 
     * @param propertiesMapping
     *            The properties name mapping from external as key and the
     *            internal name as value. E.g. with the key "version-status" and
     *            "LATEST_VERSION_STATUS" as value is the value of
     *            "version-status" after the mapping accessible with the
     *            internal key "LATEST_VERSION_STATUS".
     * @return The key mapping.
     */
    private Map<String, String> expandPropertiesNamesMapping(
        final Map<String, String> propertiesMapping) {

        Map<String, String> newPropertiesNames;
        if (propertiesMapping != null) {
            newPropertiesNames = propertiesMapping;
        }
        else {
            newPropertiesNames = new HashMap<String, String>();
        }

        newPropertiesNames.put(Constants.DC_NS_URI + "description",
            PropertyMapKeys.LATEST_VERSION_DESCRIPTION);
        newPropertiesNames.put(TripleStoreUtility.PROP_CONTEXT_TYPE,
            PropertyMapKeys.CONTEXT_TYPE);

        return newPropertiesNames;
    }

    /**
     * 
     * @return true if Organizational Unit was updated. False otherwise.
     */
    public boolean isOuUpdated() {
        return this.ouUpdated;
    }

}
