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
package de.escidoc.core.common.business.fedora.resources;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.LockHandler;
import de.escidoc.core.common.business.PropertyMapKeys;
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.Triple;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.interfaces.FedoraResource;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.ItemRelsExtUpdateHandler;
import de.escidoc.core.common.util.stax.handler.MultipleExtractor;
import de.escidoc.core.common.util.stax.handler.RelsExtReadHandler;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;
import org.fcrepo.server.types.gen.DatastreamControlGroup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic Resource supports object id, title, last modified, datastream,
 * locking and sync mechanisms.
 * 
 * @author SWA
 * 
 */
public class GenericResource implements FedoraResource {

    private final AppLogger log = new AppLogger(GenericResource.class.getName());

    public static final String DATASTREAM_DEFAULT = "datastream";

    /**
     * TODO NeedSync in this form is ineffective. This should be at least a
     * Vector as register which data set is out of sync, an only this is called
     * during the persist() method.
     */
    private boolean needSync = false;

    // -----------------------

    protected Datastream relsExt = null;

    protected Datastream dc = null;

    private String id = null;

    private String title = null;

    /**
     * The resource Href.
     */
    private String href = null;

    private Datastream datastream;

    private LockHandler lockHandler = null;

    private FedoraUtility fu = null;

    private org.fcrepo.server.types.gen.Datastream[] datastreamsInformation;

    // for versionated resources (like Item/Container) is the creationDate not
    // the Fedora CreationDate!
    protected String creationDate = null;

    private String createdBy = null;

    private Collection<String> propertiesNames = null;

    /**
     * Mapping from the TripleStore or WOV keys to the internal keys.
     */
    private Map<String, String> propertiesNamesMapping = null;

    /**
     * Properties value map. Should contain all properties values of the
     * resource.
     */
    private Map<String, String> propertiesMap = null;

    // -----------------------

    /**
     * Constructor.
     * 
     */
    public GenericResource() {

        setPropertiesNames(expandPropertiesNames(getPropertiesNames()),
            expandPropertiesNamesMapping(getPropertiesNamesMapping()));
    }

    /**
     * Constructor.
     * 
     * @param objid
     *            The id of the object in the repository.
     */
    public GenericResource(final String objid) {

        this.id = objid;
        setPropertiesNames(expandPropertiesNames(getPropertiesNames()),
            expandPropertiesNamesMapping(getPropertiesNamesMapping()));
    }

    /**
     * Get object Id (without version suffix).
     * 
     * @return objid (without version suffix)
     */
    @Override
    public String getId() {

        return this.id;
    }

    /**
     * Href of the Resource. This method is to override through the inherited
     * classes.
     * 
     * @return the href
     */
    @Override
    public String getHref() {

        return this.href;
    }

    /**
     * Set the href of the Resource. The href path is to set by the inherit
     * classes depending on the resource path. (/ir/item/.. or /ir/container/..)
     * 
     * @param path
     *            The path to the resource.
     */
    public void setHref(final String path) {

        this.href = path;
    }

    /**
     * Set object id.
     * 
     * @param id
     *            The object Id.
     * @throws TripleStoreSystemException
     *             Thrown if communication with TripleStore fails.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     * @throws ResourceNotFoundException
     * @throws ResourceNotFoundException
     *             Thrown if the resource with the provided id does not exist.
     */
    public void setId(final String id) throws TripleStoreSystemException,
        WebserverSystemException, ResourceNotFoundException {

        this.id = XmlUtility.getObjidWithoutVersion(id);
    }

    /**
     * Get creation date of resource.
     * 
     * @return creation date
     * 
     * @throws TripleStoreSystemException
     *             Thrown if request to TripleStore failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    public String getCreationDate() throws TripleStoreSystemException,
        WebserverSystemException {

        if (this.creationDate == null) {
            this.creationDate =
                TripleStoreUtility.getInstance().getCreationDate(this.id);
        }
        return this.creationDate;
    }

    /**
     * Set the creation date.
     * 
     * @param creationDate
     *            The creationDate to set
     */
    public void setCreationDate(final String creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isNeedSync() {
        return needSync;
    }

    public void setNeedSync(boolean needSync) {
        this.needSync = needSync;
    }

    /**
     * Get last modification date of the resource. This modification date
     * differs from the Fedora object (last) modification date!
     * 
     * @return last-modification-date
     * 
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     * @throws FedoraSystemException
     *             Thrown if access to Fedora fails.
     */
    public String getLastModificationDate() throws WebserverSystemException,
        FedoraSystemException {

        String lastModificationDate;
        try {
            lastModificationDate =
                getResourceProperties().get(
                    PropertyMapKeys.LAST_MODIFICATION_DATE);
            if (lastModificationDate == null) {
                lastModificationDate =
                    getFedoraUtility().getLastModificationDate(this.id);
                setLastModificationDate(lastModificationDate);
            }
        }
        catch (TripleStoreSystemException e) {
            throw new WebserverSystemException(e);
        }

        return lastModificationDate;
    }

    /**
     * Set the last-modification-date.
     * 
     * @param timestamp
     *            The new timestamp for the lastModificationDate.
     * @throws WebserverSystemException
     *             Thrown if requesting TripleStore failed.
     */
    public void setLastModificationDate(final String timestamp)
        throws WebserverSystemException {
        // this.lastModifiedDate = timestamp;
        try {
            getResourceProperties().put(PropertyMapKeys.LAST_MODIFICATION_DATE,
                timestamp);
        }
        catch (TripleStoreSystemException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * Get last modification date of object from Fedora. This is the date of the
     * Fedora object and differs from the last-modification-date of the
     * resource!
     * 
     * @return last-modification-date
     * 
     * @throws FedoraSystemException
     *             Thrown if access to Fedora fails.
     */
    @Override
    public String getLastFedoraModificationDate() throws FedoraSystemException {

        return getFedoraUtility().getLastModificationDate(this.id);
    }

    /**
     * Get title of object.
     * 
     * @return object title
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    public String getTitle() throws WebserverSystemException {

        if (this.title == null) {
            try {
                this.title = TripleStoreUtility.getInstance().getTitle(this.id);
            }
            catch (final TripleStoreSystemException e) {
                throw new WebserverSystemException(e);
            }
        }

        return this.title;
    }

    /**
     * Set the resource title.
     * 
     * @param title
     *            The new title.
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Get object status.
     * 
     * @return status of resource.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    public String getStatus() throws WebserverSystemException {

        String status;
        try {
            status = getResourceProperties().get(PropertyMapKeys.PUBLIC_STATUS);
        }
        catch (final TripleStoreSystemException e) {
            throw new WebserverSystemException(e);
        }

        return status;
    }

    /**
     * Get createdBy.
     * 
     * @return createdBy
     * 
     * @throws TripleStoreSystemException
     *             Thrown if request to TripleStore failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    public String getCreatedBy() throws TripleStoreSystemException,
        WebserverSystemException {

        if (this.createdBy == null) {
            this.createdBy =
                TripleStoreUtility.getInstance().getPropertiesElements(this.id,
                    TripleStoreUtility.PROP_CREATED_BY_ID);
        }

        return this.createdBy;
    }

    /**
     * Set createdBy.
     * 
     * @param createdBy
     *            The id of the creator;
     */
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    // -------------------------------------------------------------------------

    /**
     * Check if Resource exist in Fedora.
     * 
     * @throws ResourceNotFoundException
     *             Thrown if no resource could be found under the object id.
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore request failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    public void checkResourceExist() throws ResourceNotFoundException,
        TripleStoreSystemException, WebserverSystemException {

        if (this.id == null
            || !TripleStoreUtility.getInstance().exists(this.id)) {
            final String msg =
                "Resource with id " + this.id + " does not exist.";
            log.debug(msg);
            throw new ResourceNotFoundException(msg);
        }
    }

    // -------------------------------------------------------------------------

    /**
     * Get RELS-EXT datastream.
     * 
     * @return RELS-EXT datastream.
     * @throws FedoraSystemException
     *             Thrown in case of exceptions with Fedora.
     * @throws StreamNotFoundException
     *             Thrown if RELS-EXT datastream could not be found.
     */
    @Override
    public Datastream getRelsExt() throws FedoraSystemException,
        StreamNotFoundException {

        if (this.relsExt == null) {
            this.relsExt =
                new Datastream(Datastream.RELS_EXT_DATASTREAM, this.id, null);
        }
        return this.relsExt;
    }

    /**
     * Get RELS-EXT datastream.
     * 
     * @return RELS-EXT datastream as String.
     * 
     * @throws EncodingSystemException
     *             Thrown if data stream encoding failed.
     * @throws StreamNotFoundException
     *             Thrown if the datastream was not found.
     * @throws FedoraSystemException
     *             Thrown if Fedora request failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     */
    public String getRelsExtAsString() throws EncodingSystemException,
        WebserverSystemException, FedoraSystemException,
        StreamNotFoundException {

        String xml;
        try {
            xml =
                new String(getRelsExt().getStream(),
                    XmlUtility.CHARACTER_ENCODING);
        }
        catch (final UnsupportedEncodingException e) {
            log.debug(e.getMessage());
            throw new EncodingSystemException(e);
        }
        return xml;
    }

    /**
     * See Interface for functional description.
     * 
     * @param ds
     *            The RELS-EXT datasream.
     * @throws StreamNotFoundException
     *             Thrown if the datastream was not found.
     * @throws FedoraSystemException
     *             Thrown if Fedora request failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     * @see de.escidoc.core.common.business.fedora.resources.interfaces.FedoraResource#setRelsExt(de.escidoc.core.common.business.fedora.datastream.Datastream)
     */
    @Override
    public void setRelsExt(final Datastream ds) throws FedoraSystemException,
        WebserverSystemException {

        if (this.relsExt == null || !this.relsExt.equals(ds)) {
            this.relsExt = ds;
            this.needSync = true;
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @param relsExt
     *            The RELS-EXT datasream.
     * @throws WebserverSystemException
     * @throws FedoraSystemException
     * @throws StreamNotFoundException
     *             Thrown if the datastream was not found.
     * @throws FedoraSystemException
     *             Thrown if Fedora request failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     * @see de.escidoc.core.common.business.fedora.resources.interfaces.FedoraResource#setRelsExt(byte[])
     */
    @Override
    public void setRelsExt(final byte[] relsExt) throws FedoraSystemException,
        WebserverSystemException {

        // TODO for resources that don't use versions could the RELS-EXT set to
        // override mode
        setRelsExt(new Datastream(Datastream.RELS_EXT_DATASTREAM, this.id,
            relsExt, "text/xml"));
    }

    /**
     * Set the RELS-EXT data stream.
     * 
     * @param relsExt
     *            The RELS-EXT datasream.
     * @throws EncodingSystemException
     *             Thrown if data stream encoding failed.
     * @throws StreamNotFoundException
     *             Thrown if the datastream was not found.
     * @throws FedoraSystemException
     *             Thrown if Fedora request failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     */
    public void setRelsExt(final java.io.ByteArrayOutputStream relsExt)
        throws FedoraSystemException, WebserverSystemException,
        StreamNotFoundException, EncodingSystemException {

        try {
            setRelsExt(relsExt.toString(XmlUtility.CHARACTER_ENCODING));
        }
        catch (final UnsupportedEncodingException e) {
            log.debug(e.getMessage());
            throw new EncodingSystemException(e);
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @param relsExt
     *            The RELS-EXT datasream.
     * @throws EncodingSystemException
     *             Thrown if data stream encoding failed.
     * @throws WebserverSystemException
     * @throws FedoraSystemException
     * @throws StreamNotFoundException
     *             Thrown if the datastream was not found.
     * @throws FedoraSystemException
     *             Thrown if Fedora request failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     * @see de.escidoc.core.common.business.fedora.resources.interfaces.FedoraResource#setRelsExt(java.lang.String)
     */
    @Override
    public void setRelsExt(final String relsExt)
        throws EncodingSystemException, FedoraSystemException,
        WebserverSystemException {

        try {
            setRelsExt(relsExt.getBytes(XmlUtility.CHARACTER_ENCODING));
        }
        catch (final UnsupportedEncodingException e) {
            log.debug(e.getMessage());
            throw new EncodingSystemException(e);
        }
    }

    /**
     * Unset RELS-EXT datastream to prevent merge with repository.
     */
    protected void unsetRelsExt() {
        this.relsExt = null;
    }

    /**
     * Update Elements in RELS-EXT.
     * 
     * @param elementsToUpdate
     *            Map of elements which are to update.
     * @param elementsToRemove
     *            Map of elements which are to remove.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    protected void updateRelsExt(
        final Map<String, StartElementWithChildElements> elementsToUpdate,
        final Map<String, List<StartElementWithChildElements>> elementsToRemove)
        throws WebserverSystemException {

        final StaxParser sp = new StaxParser();

        if (elementsToUpdate != null) {
            final ItemRelsExtUpdateHandler itemRelsExtUpdateHandler =
                new ItemRelsExtUpdateHandler(elementsToUpdate, sp);
            sp.addHandler(itemRelsExtUpdateHandler);
        }

        final HashMap<String, String> pathes = new HashMap<String, String>();
        pathes.put("/RDF", null);
        final MultipleExtractor me = new MultipleExtractor(pathes, sp);
        me.removeElements(elementsToRemove);
        sp.addHandler(me);

        try {
            // final ByteArrayInputStream relsExtBA =
            // new ByteArrayInputStream(getRelsExt().getStream());
            sp.parse(getRelsExt().getStream());
            setRelsExt((ByteArrayOutputStream) me.getOutputStreams().get("RDF"));
        }
        catch (final Exception e) {
            log.debug(e.getMessage());
            throw new WebserverSystemException("Unexpected Exception.", e);
        }
    }

    /* ------------------------------------------------------------------------- */

    /**
     * Get resource properties. The internal propertiesNames are use to request
     * the parameter. If this propertiesNames differ from the default list then
     * either set a new list or use the getResourceProperties(propertiesNames)
     * method. The values of this request are cached. The key names are mapped
     * to the internal key representation.
     * 
     * @return resource properties.
     * 
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore request failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     */
    public Map<String, String> getResourceProperties()
        throws TripleStoreSystemException, WebserverSystemException {

        if (this.propertiesMap == null) {
            Map<String, String> lastVersionData =
                mapTripleList2TupleList(parseTriplesFromRelsExt());
            this.propertiesMap = mapTripleStoreKeys(lastVersionData);
        }
        return this.propertiesMap;
    }

    /**
     * Get value of property. Moved from Item (FRS).
     * 
     * @param prop
     *            Name of property.
     * @return Value of property.
     * @throws TripleStoreSystemException
     * @throws WebserverSystemException
     */
    public String getProperty(final String prop)
        throws TripleStoreSystemException, WebserverSystemException {

        return getResourceProperties().get(prop);
    }

    /**
     * Get resource properties with a provided set of propertiesNames. The
     * values of this request are not cached.
     * 
     * @param propertiesNamesCol
     *            The collection of properties names that are to request.
     * @return resource properties.
     * 
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore request failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     */
    public Map<String, String> getResourceProperties(
        final Collection<String> propertiesNamesCol)
        throws TripleStoreSystemException, WebserverSystemException {

        // return getTripleStoreUtility().getProperties(getId(),
        // propertiesNamesCol);

        Map<String, String> tripleStoreValues =
            TripleStoreUtility.getInstance().getProperties(getId(),
                propertiesNamesCol);

        return mapTripleStoreKeys(tripleStoreValues);
    }

    /**
     * Get the value of a resource properties. The properties is referenced by
     * key (see for key names PropertyMapKeys).
     * 
     * @param key
     *            The property key.
     * @return The value of the property.
     * 
     * @throws TripleStoreSystemException
     *             Thrown if a TripleStore request failed.
     * @throws WebserverSystemException
     *             Thrown if parsing of WOV failed or an internal failure
     *             occurs.
     */
    public String getResourcePropertiesValue(final String key)
        throws TripleStoreSystemException, WebserverSystemException {

        return getResourceProperties().get(key);
    }

    /**
     * This is an interims method to create a way for updating properties if the
     * resource is changed out side of the resource classes.
     * 
     * @param key
     *            The properties key name.
     * @param value
     *            The properties value.
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     */
    @Deprecated
    public void setResourceProperties(final String key, final String value)
        throws WebserverSystemException {

        try {
            getResourceProperties().put(key, value);
        }
        catch (TripleStoreSystemException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * Maps the keys from the TripleStore to the internal used keys (from
     * PropertyMapKeys). This method maps all keys which are used in this class.
     * 
     * @param tripleStoreMap
     *            A map with TripleStore key, value pairs
     * @return A map with key, values pairs where the keys are object consist
     *         (see PropertyMapKeys class)
     * 
     */
    public Map<String, String> mapTripleStoreKeys(
        final Map<String, String> tripleStoreMap) {

        Map<String, String> properties = new HashMap<String, String>();

        for (String sourceKey : tripleStoreMap.keySet()) {
            String value = tripleStoreMap.get(sourceKey);
            if (value != null) {
                String targetKey = this.propertiesNamesMapping.get(sourceKey);
                if (targetKey != null) {
                    properties.put(targetKey, value);
                } else {
                    properties.put(sourceKey, value);
                }
            }
        }

        return properties;
    }

    /**
     * Add value to the resource properties map. Double key entries are
     * overridden.
     * 
     * @param valueMap
     *            map with new values
     */
    public void addResourceProperties(final Map<String, String> valueMap) {

        if (this.propertiesMap == null) {
            this.propertiesMap = valueMap;
        }
        else {
            this.propertiesMap.putAll(valueMap);
        }
    }

    /**
     * Get a list of all in this class used (RELS-EXT) properties names.
     * 
     * @return list of RELS-EXT properties names.
     */
    protected final Collection<String> getPropertiesNames() {

        return this.propertiesNames;
    }

    /**
     * Get a list of all in this class used (RELS-EXT) properties names.
     * 
     * @return list of RELS-EXT properties names.
     */
    protected final Map<String, String> getPropertiesNamesMapping() {

        return this.propertiesNamesMapping;
    }

    /**
     * Set the Collection of properties names. This names are defined as
     * resource parameter. The cached of properties values is deleted.
     * 
     * @param propertiesNames
     *            The Collection of parameter names for this resource.
     * @param propertiesNamesMapping
     *            The Map of key names how they to map to the internal key
     *            names.
     */
    protected final void setPropertiesNames(
            final Collection<String> propertiesNames,
            final Map<String, String> propertiesNamesMapping) {

        this.propertiesNames = propertiesNames;
        this.propertiesNamesMapping = propertiesNamesMapping;
        this.propertiesMap = null;
    }

    // -------------------------------------------------------------------------

    /**
     * Check if object is locked.
     * 
     * @return Whether the resource is locked or not.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public boolean isLocked() throws WebserverSystemException {

        return getLockHandler().isLocked(this.id);
    }

    /**
     * If the item is locked the lock owner is returned, null otherwise.
     * 
     * @return The lock owner or null.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public String getLockOwner() throws WebserverSystemException {

        return getLockHandler().getLockOwner(this.id);
    }

    /**
     * If the item is locked the lock owner title is returned, null otherwise.
     * 
     * @return The lock owner or null.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public String getLockOwnerTitle() throws WebserverSystemException {

        return getLockHandler().getLockOwnerTitle(this.id);
    }

    /**
     * If the container is locked the lock date is returned, null otherwise.
     * 
     * @return The lock date or null.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public String getLockDate() throws WebserverSystemException {

        return getLockHandler().getLockDate(this.id);
    }

    /**
     * Lock/Unlock object.
     * 
     * @param lock
     *            True == lock object. False == unlock object.
     * @param lockOwner
     *            Ids who is lock the object.
     * @throws LockingException
     *             Thrown if locking fails.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    public void setLocked(final boolean lock, final String[] lockOwner)
        throws LockingException, SystemException {

        // Should lock only be checked in handler? No, it is part of the
        // resource representation.
        if ((lock) && (lockOwner == null)) {
            String msg = "Need lockOwner.";
            log.debug(msg);
            throw new NullPointerException(msg);
        }

        if (lock && !isLocked()) {
            getLockHandler().lock(this.id, lockOwner);
        }
        else if (isLocked()) {
            getLockHandler().unlock(this.id);
        }
    }

    // -------------------------------------------------------------------------

    /**
     * Get a new, virgin Datastream.
     * 
     * @return datastream
     * @throws StreamNotFoundException
     *             Thrown if the datastream was not found.
     * @throws FedoraSystemException
     *             Thrown if Fedora request failed.
     */
    public Datastream getDatastream() throws StreamNotFoundException,
        FedoraSystemException {

        if (this.datastream == null) {
            this.datastream = new Datastream(DATASTREAM_DEFAULT, this.id, null);
        }
        return this.datastream;
    }

    /**
     * Set Datastream to the resource.
     * 
     * @param ds
     *            The datastream.
     * @throws StreamNotFoundException
     *             Thrown if the datastream was not found.
     * @throws LockingException
     *             Thrown if resource is locked.
     * @throws SystemException
     *             Thrown in case of internal failure.
     */
    public void setDatastream(final Datastream ds)
        throws StreamNotFoundException, LockingException, SystemException {

        if (isLocked()) {
            String msg = "Resource " + this.id + " is locked.";
            log.debug(msg);
            throw new LockingException(msg);
        }
        // check if datastream is set, is equal to ds and save to fedora
        try {
            // FIXME the getDatastream method delivers an virgin Datastream. Is
            // this compare really necessary (Ok, this prevents writing of
            // unused data streams.)?
            final Datastream curDs = getDatastream();

            if (!ds.equals(curDs)) {

                this.datastream = ds;
                String lmd = ds.merge();
                setLastModificationDate(lmd);
                this.needSync = true;
            }
        }
        catch (final StreamNotFoundException e) {
            // An item have to have a RELS-EXT datastream
            throw new StreamNotFoundException(
                "No " + Datastream.RELS_EXT_DATASTREAM + " for item " + this.id
                    + '.', e);
        }

        // // don't forget to update
        // // TODO check if the new introduced return value of the
        // Datastream.merge
        // // method makes this update redundant!
        // setLastModificationDate(getFedoraUtility().getLastModificationDate(
        // this.id));
        //
        // this.needSync = true;

    }

    // -------------------------------------------------------------------------

    /**
     * @return Is true if object out of sync with Repository.
     */
    public boolean isNewVersion() {
        return (this.needSync);
    }

    /**
     * Persists the whole object to Fedora.
     * 
     * @return lastModificationDate of the resource
     * 
     * @throws FedoraSystemException
     *             Thrown if connection to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    public String persist() throws FedoraSystemException,
        WebserverSystemException {

        return persist(true);
    }

    /**
     * Persists the whole object to Fedora.
     * 
     * @param sync
     *            Set <code>true</code> if TripleStore sync is to force.
     * @return lastModificationDate of the resource.
     * 
     * @throws FedoraSystemException
     *             Thrown if connection to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    public String persist(final boolean sync) throws FedoraSystemException,
        WebserverSystemException {

        /*
         * Well, this is not nice but the used comparing method, to detect
         * changes, is expensive. If RELS-EXT was not updated (through the
         * methods of this class), then should a persist be redundant.
         */
        if (this.needSync) {
            String lastModificationDate = persistRelsExt();
            setLastModificationDate(lastModificationDate);
        }

        if (sync) {
            getFedoraUtility().sync();
        }

        return getLastFedoraModificationDate();
    }

    /**
     * Write RELS-EXT to Fedora.
     * 
     * @return new timestamp of data stream (or null if not updated)
     * 
     * @throws FedoraSystemException
     *             Thrown if connection to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    protected String persistRelsExt() throws FedoraSystemException,
        WebserverSystemException {

        String timestamp = null; // Maybe would it be better, if we use the
        // old timestamp instead of null.
        if (this.relsExt != null) {
            timestamp = this.relsExt.merge();
        }

        return timestamp;
    }

    /**
     * Get LockHandler.
     * 
     * @return LockHandler
     */
    public LockHandler getLockHandler() {

        if (this.lockHandler == null) {
            this.lockHandler = LockHandler.getInstance();
        }

        return this.lockHandler;
    }

    /**
     * Get instance of FedoraUtility.
     * 
     * @return FedoraUtility instance
     * @throws FedoraSystemException
     *             Thrown if instantiating of FedoraUtility fails.
     */
    public FedoraUtility getFedoraUtility() throws FedoraSystemException {
        if (fu == null) {
            fu = FedoraUtility.getInstance();
        }
        return fu;
    }

    /**
     * See Interface for functional description.
     * 
     * @return Array with information for all data streams.
     * 
     * @throws FedoraSystemException
     * @see de.escidoc.core.common.business.fedora.resources.interfaces.FedoraResource#getDatastreamsInformation()
     */
    @Override
    public org.fcrepo.server.types.gen.Datastream[] getDatastreamsInformation()
        throws FedoraSystemException {

        if (this.datastreamsInformation == null) {
            this.datastreamsInformation =
                getFedoraUtility().getDatastreamsInformation(this.id, null);
        }

        return this.datastreamsInformation;
    }

    // -------------------------------------------------------------------------
    // private methods
    // -------------------------------------------------------------------------

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

        newPropertiesNames.add(TripleStoreUtility.PROP_DC_TITLE);
        // FIXME only container has description
        newPropertiesNames.add(TripleStoreUtility.PROP_DC_DESCRIPTION);
        newPropertiesNames.add(TripleStoreUtility.PROP_CREATED_BY_TITLE);
        newPropertiesNames.add(TripleStoreUtility.PROP_CREATED_BY_ID);
        newPropertiesNames.add(TripleStoreUtility.PROP_PUBLIC_STATUS);
        newPropertiesNames.add(TripleStoreUtility.PROP_PUBLIC_STATUS_COMMENT);

        newPropertiesNames.add(TripleStoreUtility.PROP_CONTENT_MODEL_TITLE);
        newPropertiesNames.add(TripleStoreUtility.PROP_CONTENT_MODEL_ID);

        newPropertiesNames.add(TripleStoreUtility.PROP_CONTEXT_TITLE);
        newPropertiesNames.add(TripleStoreUtility.PROP_CONTEXT_ID);

        newPropertiesNames.add(TripleStoreUtility.PROP_LATEST_VERSION_USER_ID);
        newPropertiesNames
            .add(TripleStoreUtility.PROP_LATEST_VERSION_USER_TITLE);

        newPropertiesNames.add(TripleStoreUtility.PROP_ORIGIN);
        newPropertiesNames.add(TripleStoreUtility.PROP_ORIGIN_VERSION);

        return newPropertiesNames;

    }

    /**
     * Expand the map for the to mapping key names. The properties key names
     * from the TripleStore differ to the internal representation. Therefore we
     * translate the key names to the internal.
     * 
     * @param propertiesNamesMap
     *            The key is the to replace value. E.g. the &lt;oldKeyName,
     *            newKeyName&gt;
     * @return propertiesNamesMappingMap
     */
    private Map<String, String> expandPropertiesNamesMapping(
        final Map<String, String> propertiesNamesMap) {

        Map<String, String> newPropertiesNamesMap;
        if (propertiesNamesMap != null) {
            newPropertiesNamesMap = propertiesNamesMap;
        }
        else {
            newPropertiesNamesMap = new HashMap<String, String>();
        }

        newPropertiesNamesMap.put(TripleStoreUtility.PROP_DC_TITLE,
            PropertyMapKeys.LATEST_VERSION_TITLE);
        newPropertiesNamesMap.put(TripleStoreUtility.PROP_DC_DESCRIPTION,
            PropertyMapKeys.LATEST_VERSION_DESCRIPTION);
        newPropertiesNamesMap.put(TripleStoreUtility.PROP_CREATED_BY_TITLE,
            PropertyMapKeys.CREATED_BY_TITLE);
        newPropertiesNamesMap.put(TripleStoreUtility.PROP_CREATED_BY_ID,
            PropertyMapKeys.CREATED_BY_ID);
        newPropertiesNamesMap.put(TripleStoreUtility.PROP_PUBLIC_STATUS,
            PropertyMapKeys.PUBLIC_STATUS);
        newPropertiesNamesMap.put(
            TripleStoreUtility.PROP_PUBLIC_STATUS_COMMENT,
            PropertyMapKeys.PUBLIC_STATUS_COMMENT);
        newPropertiesNamesMap.put(TripleStoreUtility.PROP_CONTENT_MODEL_TITLE,
            PropertyMapKeys.LATEST_VERSION_CONTENT_MODEL_TITLE);
        newPropertiesNamesMap.put(TripleStoreUtility.PROP_CONTENT_MODEL_ID,
            PropertyMapKeys.LATEST_VERSION_CONTENT_MODEL_ID);
        newPropertiesNamesMap.put(TripleStoreUtility.PROP_CONTEXT_TITLE,
            PropertyMapKeys.LATEST_VERSION_CONTEXT_TITLE);
        newPropertiesNamesMap.put(TripleStoreUtility.PROP_CONTEXT_ID,
            PropertyMapKeys.LATEST_VERSION_CONTEXT_ID);

        newPropertiesNamesMap.put(
            TripleStoreUtility.PROP_LATEST_VERSION_USER_ID,
            PropertyMapKeys.LATEST_VERSION_MODIFIED_BY_ID);
        newPropertiesNamesMap.put(
            TripleStoreUtility.PROP_LATEST_VERSION_USER_TITLE,
            PropertyMapKeys.LATEST_VERSION_MODIFIED_BY_TITLE);

        newPropertiesNamesMap.put(TripleStoreUtility.PROP_ORIGIN,
            PropertyMapKeys.ORIGIN);

        newPropertiesNamesMap.put(TripleStoreUtility.PROP_ORIGIN_VERSION,
            PropertyMapKeys.ORIGIN_VERSION);

        return newPropertiesNamesMap;
    }

    /**
     * Map the value of the sourceKey from the sourceMap to the targetKey in the
     * targetMap.
     * 
     * @param sourceMap
     *            The source map of values.
     * @param targetMap
     *            The target map for values.
     * @param sourceKey
     *            The key in the source Map.
     * @param targetKey
     *            The key in the target Map where the value is to store.
     */
    public void map(
        final Map<String, String> sourceMap,
        final Map<String, String> targetMap, final String sourceKey,
        final String targetKey) {

        String value = sourceMap.get(sourceKey);
        if (value != null) {
            targetMap.put(targetKey, value);
        }
    }

    /**
     * FIXME move to genericResource
     * 
     * Check if the Resource is fro the provided type.
     * 
     * @param resourceType
     *            Required resource type.
     * @return true if resource is from provided type.
     * @throws IntegritySystemException
     *             Thrown if object type could not retrieved
     * @throws WebserverSystemException
     * @throws TripleStoreSystemException
     */
    public boolean checkResourceType(final ResourceType resourceType)
        throws IntegritySystemException, TripleStoreSystemException,
        WebserverSystemException {

        String type =
            getResourceProperties().get(TripleStoreUtility.PROP_OBJECT_TYPE);

        if (resourceType == ResourceType.ITEM) {
            if (type.equals(Constants.ITEM_OBJECT_TYPE)) {
                return true;
            }
        }
        else if (resourceType == ResourceType.COMPONENT) {
            if (type.equals(Constants.COMPONENT_OBJECT_TYPE)) {
                return true;
            }
        }
        else if (resourceType == ResourceType.CONTAINER) {
            if (type.equals(Constants.CONTAINER_OBJECT_TYPE)) {
                return true;
            }
        }
        else if (resourceType == ResourceType.CONTEXT) {
            if (type.equals(Constants.CONTEXT_OBJECT_TYPE)) {
                return true;
            }
        }
        else if ((resourceType == ResourceType.CONTENT_RELATION)
            && (type.equals(Constants.CONTENT_RELATION2_OBJECT_TYPE))) {
            return true;
        }

        return false;
    }

    /**
     * Map a List fo Triples to a tuple list (where predicate is key and object
     * is value). The subject is dropped.
     * 
     * @param triples
     *            The Triples which are to map.
     * @return Tupel list.
     */
    private Map<String, String> mapTripleList2TupleList(
        final Iterable<Triple> triples) {
        final Map<String, String> lastVersionData = new HashMap<String, String>();
        for (Triple triple : triples) {
            lastVersionData.put(triple.getPredicate(), triple.getObject());
            if (triple.getPredicate().equals(TripleStoreUtility.PROP_DC_TITLE)) {
                this.title = triple.getObject();
            }
        }
        return lastVersionData;

    }

    /**
     * Parse RelsExt and obtain Triples.
     * 
     * @return List with Triples of RelsExt.
     * 
     * @throws WebserverSystemException
     *             Thrown if retrieve of RelsExt or parsing failed.
     */
    private List<Triple> parseTriplesFromRelsExt()
        throws WebserverSystemException {
        Datastream tmpRelsExt;
        try {
            tmpRelsExt =
                new Datastream(Datastream.RELS_EXT_DATASTREAM, getId(), null);
        }
        catch (FedoraSystemException e1) {
            throw new WebserverSystemException(e1);
        }
        catch (StreamNotFoundException e1) {
            throw new WebserverSystemException(e1);
        }

        final StaxParser sp = new StaxParser();

        final RelsExtReadHandler eve = new RelsExtReadHandler(sp);
        eve.cleanIdentifier(true);
        sp.addHandler(eve);
        try {
            sp.parse(new ByteArrayInputStream(tmpRelsExt.getStream()));
        }
        catch (final Exception e) {
            String msg = "Unexpected exception during RELS-EXT parsing.";
            log.warn(msg + e);
            throw new WebserverSystemException(msg, e);
        }

        return eve.getElementValues().getTriples();
    }

    protected org.fcrepo.server.types.gen.Datastream[] getDatastreamInfos()
        throws WebserverSystemException, FedoraSystemException {
        return FedoraUtility.getInstance().getDatastreamsInformation(getId(), null);
    }

    protected void initDatastreams(
        org.fcrepo.server.types.gen.Datastream[] datastreamInfos)
        throws WebserverSystemException, FedoraSystemException,
        TripleStoreSystemException, IntegritySystemException,
        StreamNotFoundException {

        for (org.fcrepo.server.types.gen.Datastream datastreamInfo : datastreamInfos) {
            List<String> altIDs = Arrays.asList(datastreamInfo.getAltIDs());
            String name = datastreamInfo.getID();
            String label = datastreamInfo.getLabel();
            DatastreamControlGroup controlGroup =
                    datastreamInfo.getControlGroup();
            String controlGroupValue = controlGroup.getValue();
            String mimeType = datastreamInfo.getMIMEType();
            String location = datastreamInfo.getLocation();

            Datastream ds;
            // RELS-EXT
            if (name.equals(Datastream.RELS_EXT_DATASTREAM)) {
                // The RELS-EXT in the Fedora repository is newer than the
                // version specified by versionDate. The difference between both
                // versions are timestamps (version/date, release/date).
                ds =
                        new Datastream(name, getId(), null, mimeType, location,
                                controlGroupValue);

                ds.setAlternateIDs(new ArrayList<String>(altIDs));
                ds.setLabel(label);
                // setRelsExt(ds);
                this.relsExt = ds;
            }
            // DC
            else if ("DC".equals(name)) {
                if (this.dc == null) {
                    ds =
                            new Datastream("DC", getId(), null, mimeType, location,
                                    controlGroupValue);
                    ds.setAlternateIDs(new ArrayList<String>(altIDs));
                    ds.setLabel(label);
                    this.dc = ds;
                }
            } else {
                log.debug("Datastream " + getId() + '/' + name
                        + " not instanziated in GenericResource.<init>.");
            }
        }

    }
}
