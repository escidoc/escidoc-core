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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.escidoc.core.services.fedora.management.DatastreamProfileTO;
import org.escidoc.core.services.fedora.management.DatastreamProfilesTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.PropertyMapKeys;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.GenericResource;
import de.escidoc.core.common.business.fedora.resources.interfaces.ContextInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.notfound.AdminDescriptorNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;

/**
 * Context.
 * 
 * @author Steffen Wagner
 */
@Configurable(preConstruction = true)
public class Context extends GenericResource implements ContextInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(Context.class);

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    private Datastream dc;

    private Datastream properties;

    private Datastream resources;

    private final Map<String, Datastream> adminDescriptors = new HashMap<String, Datastream>();

    private boolean ouUpdated;

    public Context() {
    }

    /**
     * Instantiates the Context with the specified id. The datastreams are instantiated and retrieved if the related
     * getter is called.
     * 
     * @param id
     *            The id of an context managed in Fedora.
     * @throws ContextNotFoundException
     *             Thrown if Context with id could not be found.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    public Context(final String id) throws ContextNotFoundException, TripleStoreSystemException,
        IntegritySystemException {
        super(id);
        init();
    }

    private void init() throws ContextNotFoundException, TripleStoreSystemException, IntegritySystemException {
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
    public Datastream getResources() throws StreamNotFoundException, FedoraSystemException {
        // if properties is unset, instantiate the Stream
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
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     */
    public void setResources(final Datastream ds) throws StreamNotFoundException, FedoraSystemException,
        WebserverSystemException {
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
            throw new StreamNotFoundException("No properties for context " + getId() + '.', e);
        }
        // getSomeValuesFromFedora();

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.fedora.resources.interfaces.ContextInterface #getProperties()
     */
    @Override
    public Datastream getProperties() throws StreamNotFoundException, FedoraSystemException {
        // if properties is unset, instantiate the Stream
        if (this.properties == null) {
            this.properties = new Datastream("properties", getId(), null);
        }
        return this.properties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.fedora.resources.interfaces.ContextInterface #
     * setProperties(de.escidoc.core.common.business.fedora.datastream.Stream )
     */
    @Override
    public void setProperties(final Datastream ds) throws StreamNotFoundException, FedoraSystemException,
        WebserverSystemException {
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
            throw new StreamNotFoundException("No properties for context " + getId() + '.', e);
        }
        // getSomeValuesFromFedora();
    }

    public void setOrganizationalUnits(final List<String> ous) throws InvalidContentException,
        TripleStoreSystemException, WebserverSystemException {

        // check that at least one OU is given
        if (ous.isEmpty()) {
            throw new InvalidContentException("No 'organizational-unit' element is given.");
        }

        final List<String> currentOus = getOrganizationalUnitObjids();

        // merge new OUS with existing ------------------------------
        // remove
        final Map<String, List<StartElementWithChildElements>> elementsToRemove =
            new TreeMap<String, List<StartElementWithChildElements>>();

        Iterator<String> it = currentOus.iterator();
        while (it.hasNext()) {
            final String ou = it.next();
            if (!ous.contains(ou)) {
                this.ouUpdated = true;
                final StartElementWithChildElements ouToRemove = new StartElementWithChildElements();
                ouToRemove.setLocalName(Elements.ELEMENT_ORGANIZATIONAL_UNIT);
                ouToRemove.setPrefix(Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
                ouToRemove.setNamespace(Constants.STRUCTURAL_RELATIONS_NS_URI);

                final Attribute resource =
                    new Attribute("resource", Constants.RDF_NAMESPACE_URI, Constants.RDF_NAMESPACE_PREFIX,
                        "info:fedora/" + ou);

                ouToRemove.addAttribute(resource);
                ouToRemove.setChildrenElements(null);

                final List<StartElementWithChildElements> toRemove = new ArrayList<StartElementWithChildElements>();
                toRemove.add(ouToRemove);
                elementsToRemove.put("/RDF/Description/" + Elements.ELEMENT_ORGANIZATIONAL_UNIT, toRemove);

                it.remove();
            }
        }

        // add
        final List<StartElementWithChildElements> elementsToAdd = new ArrayList<StartElementWithChildElements>();

        it = ous.iterator();
        while (it.hasNext()) {
            final String ou = it.next();

            if (!currentOus.contains(ou)) {
                this.ouUpdated = true;
                currentOus.add(ou);

                // create elements for RELS-EXT update
                final StartElementWithChildElements ouElement = new StartElementWithChildElements();
                ouElement.setLocalName(Elements.ELEMENT_ORGANIZATIONAL_UNIT);
                ouElement.setPrefix(Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
                ouElement.setNamespace(Constants.STRUCTURAL_RELATIONS_NS_URI);

                final Attribute resource =
                    new Attribute("resource", Constants.RDF_NAMESPACE_URI, Constants.RDF_NAMESPACE_PREFIX,
                        "info:fedora/" + ou);

                ouElement.addAttribute(resource);
                ouElement.setChildrenElements(null);

                elementsToAdd.add(ouElement);
            }
        }

        try {
            final byte[] relsExtNewBytes = Utility.updateRelsExt(elementsToAdd, elementsToRemove, null, this, null);
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
    @Override
    public String getHref() {
        return Constants.CONTEXT_URL_BASE + getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.fedora.resources.interfaces.ContextInterface #getRelsExt()
     */
    public Datastream getDc() throws StreamNotFoundException, FedoraSystemException {
        if (this.dc == null) {
            this.dc = new Datastream("DC", getId(), null);
        }
        return this.dc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.fedora.resources.interfaces.ContextInterface
     * #setRelsExt(de.escidoc.core.common.business.fedora.datastream.Stream)
     */
    public void setDc(final Datastream ds) throws StreamNotFoundException, FedoraSystemException,
        WebserverSystemException {
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
            throw new StreamNotFoundException("No DC for context " + getId() + '.', e);
        }
        // getSomeValuesFromFedora();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.fedora.resources.interfaces.FedoraResource #getAdminDescriptors()
     */
    @Override
    public Map<String, DatastreamProfileTO> getAdminDescriptors() throws IntegritySystemException,
        FedoraSystemException {

        final DatastreamProfilesTO profiles =
            getFedoraServiceClient().getDatastreamProfilesByAltId(getId(),
                de.escidoc.core.common.business.fedora.Constants.ADMIN_DESCRIPTOR_ALT_ID, null);
        /*
         * make a map from the list to keep the old implementation "alive" but do not instantiate a DataStream Object to
         * avoid multiple loading of datastreams.
         */
        final Map<String, DatastreamProfileTO> map =
            new HashMap<String, DatastreamProfileTO>(profiles.getDatastreamProfile().size() + 1);

        for (final DatastreamProfileTO profile : profiles.getDatastreamProfile()) {
            map.put(profile.getDsID(), profile);
        }

        return map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.fedora.resources.interfaces.ContextInterface
     * #getAdminDescriptor(java.lang.String)
     */
    @Override
    public Datastream getAdminDescriptor(final String adminDescriptorName) throws FedoraSystemException,
        AdminDescriptorNotFoundException {

        final Map<String, Datastream> admDescs = getAdminDescriptorsMap();

        if (!admDescs.containsKey(adminDescriptorName)) {
            throw new AdminDescriptorNotFoundException("Admin descriptor with name '" + adminDescriptorName
                + "' does not exist.");
        }

        return admDescs.get(adminDescriptorName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.business.fedora.resources.interfaces.ContextInterface
     * #setAdminDescriptor(de.escidoc.core.common.business.fedora.datastream. Stream)
     */
    @Override
    public void setAdminDescriptor(final Datastream ds) throws FedoraSystemException, WebserverSystemException {

        // TODO never reached by create, therefore no persist is needed. Correct
        // behavior? (FRS)
        ds.merge();

    }

    /**
     * Get a map of all admin-decriptors of the Context. The map consists of datastream name and the admin-descriptor
     * datastream itself.
     * 
     * @return Map of admin-descriptors.
     * @throws FedoraSystemException
     *             Thrown if retrieve of datastreams fail.
     */
    public Map<String, Datastream> getAdminDescriptorsMap() throws FedoraSystemException {

        final DatastreamProfilesTO profiles =
            getFedoraServiceClient().getDatastreamProfilesByAltId(getId(),
                de.escidoc.core.common.business.fedora.Constants.ADMIN_DESCRIPTOR_ALT_ID, null);

        // add only new Datastreams to HashMap
        for (final DatastreamProfileTO profile : profiles.getDatastreamProfile()) {
            if (!this.adminDescriptors.containsKey(profile.getDsID())) {
                try {
                    final Datastream newDs = new Datastream(profile.getDsID(), getId(), null);
                    this.adminDescriptors.put(profile.getDsID(), newDs);
                }
                catch (final StreamNotFoundException e) {
                    LOGGER.error(
                        "AdminDescriptor \"" + profile.getDsID() + "\" not found for Context " + getId() + '.', e);
                }
            }
        }

        return this.adminDescriptors;
    }

    /**
     * Add an AdminDescriptor to Context.
     * 
     * @param adm
     *            Admin Descriptor Stream
     */
    public void addAdminDescriptor(final Datastream adm) {

        this.adminDescriptors.put(adm.getName(), adm);
        // TODO mark DS as not persist!
    }

    /**
     * Delete an AdminDescriptor from Context.
     * 
     * @param admDescName
     *            The name/id of the AdminDescriptor (must be unique within Context).
     * @throws FedoraSystemException
     *             Thrown if Fedora reports an error.
     * @throws WebserverSystemException
     *             Thrown if an internal error occurs.
     */
    public void deleteAdminDescriptor(final String admDescName) {

        this.adminDescriptors.get(admDescName).delete();
    }

    /**
     * Get id of modifier.
     * 
     * @return modified-by id
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    public String getModifiedBy() throws TripleStoreSystemException, WebserverSystemException {
        return getProperty(PropertyMapKeys.LATEST_VERSION_MODIFIED_BY_ID);
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
    public List<String> getOrganizationalUnitObjids() throws TripleStoreSystemException {
        return this.tripleStoreUtility
            .getPropertiesElementsVector(getId(), TripleStoreUtility.PROP_ORGANIZATIONAL_UNIT);
    }

    /**
     * Get hrefs of organizational units of context.
     * 
     * @return Vector with hrefs of organizational units.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    public Collection<String> getOrganizationalUnitHrefs() throws TripleStoreSystemException {
        final String path = "/oum/organizational-unit/";
        final List<String> propVals = getOrganizationalUnitObjids();
        final Collection<String> ouHrefs = new ArrayList<String>(propVals.size());

        for (final String s : propVals) {
            ouHrefs.add(path + s);
        }

        return ouHrefs;
    }

    /**
     * Check if resource with object id exists and has type of Context.
     * 
     * @throws ContextNotFoundException
     *             Thrown if no Context exists with this object id.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    protected void checkContextExist() throws ContextNotFoundException, TripleStoreSystemException,
        IntegritySystemException {

        try {
            this.getUtility().checkIsContext(getId());
        }
        catch (final ResourceNotFoundException e) {
            throw new ContextNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * Expand a list with names of properties values with the propertiesNames for a versionated resource. These list
     * could be used to request the TripleStore.
     * 
     * @param propertiesNames
     *            Collection of propertiesNames. The collection contains only the version resource specific
     *            propertiesNames.
     * @return Parameter name collection
     */
    private static Collection<String> expandPropertiesNames(final Collection<String> propertiesNames) {

        final Collection<String> newPropertiesNames =
            propertiesNames != null ? propertiesNames : new ArrayList<String>();

        newPropertiesNames.add(TripleStoreUtility.PROP_CONTEXT_TYPE);
        newPropertiesNames.add(Constants.DC_NS_URI + "description");

        return newPropertiesNames;
    }

    /**
     * Expanding the properties naming map.
     * 
     * @param propertiesMapping
     *            The properties name mapping from external as key and the internal name as value. E.g. with the key
     *            "version-status" and "LATEST_VERSION_STATUS" as value is the value of "version-status" after the
     *            mapping accessible with the internal key "LATEST_VERSION_STATUS".
     * @return The key mapping.
     */
    private static Map<String, String> expandPropertiesNamesMapping(final Map<String, String> propertiesMapping) {
        final Map<String, String> newPropertiesNames =
            propertiesMapping != null ? propertiesMapping : new HashMap<String, String>();
        newPropertiesNames.put(Constants.DC_NS_URI + "description", PropertyMapKeys.LATEST_VERSION_DESCRIPTION);
        newPropertiesNames.put(TripleStoreUtility.PROP_CONTEXT_TYPE, PropertyMapKeys.CONTEXT_TYPE);
        return newPropertiesNames;
    }

    /**
     * @return true if Organizational Unit was updated. False otherwise.
     */
    public boolean isOuUpdated() {
        return this.ouUpdated;
    }

}
