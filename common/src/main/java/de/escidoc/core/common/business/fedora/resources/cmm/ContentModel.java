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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.business.fedora.resources.cmm;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.PropertyMapKeys;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.GenericVersionableResourcePid;
import de.escidoc.core.common.business.fedora.resources.create.ResourceDefinitionCreate;
import de.escidoc.core.common.business.fedora.resources.interfaces.VersionableResource;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.DcReadHandler;
import de.escidoc.core.common.util.stax.handler.cmm.DsCompositeModelHandler;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.fcrepo.server.types.gen.DatastreamControlGroup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Implementation of an eSciDoc Content Model Object which consist of
 * datastreams managed in Fedora Digital Repository System.
 * 
 * @author FRS
 * 
 */
public class ContentModel extends GenericVersionableResourcePid
    implements VersionableResource {

    public static final String DATASTREAM_DS_COMPOSITE_MODEL =
        "DS-COMPOSITE-MODEL";

    private Map<String, Datastream> contentStreams = null;

    private Map<String, Datastream> otherStreams = null;

    private Datastream dsCompositeModel;

    private boolean resourceInit;

    private Map<String, ResourceDefinitionCreate> resourceDefinitions = null;

    private static final AppLogger log = new AppLogger(ContentModel.class.getName());

    /**
     * Constructs the Content Model with the specified id. The datastreams are
     * instantiated and retrieved if the related getter is called.
     * 
     * @param id
     *            The ID of the Content Model.
     * 
     * @throws WebserverSystemException
     *             If an error occurs.
     * @throws FedoraSystemException
     *             If an error occurs accessing Fedora.
     * @throws TripleStoreSystemException
     *             If an error occurs accessing the triplestore.
     * @throws IntegritySystemException
     *             Thrown if there is an integrity error with the addressed
     *             object.
     * @throws StreamNotFoundException
     *             If a specific datastream can not be found.
     * @throws ResourceNotFoundException
     *             If an object with the specified ID can not be found. If there
     *             is such an object but this object is no Content Model a
     *             ContentModelNotFoundException is thrown.
     */
    public ContentModel(final String id) throws TripleStoreSystemException,
        WebserverSystemException, IntegritySystemException,
        FedoraSystemException, StreamNotFoundException,
        ResourceNotFoundException {
        super(id);
        setPropertiesNames(expandPropertiesNames(getPropertiesNames()),
            expandPropertiesNamesMapping(getPropertiesNamesMapping()));

        Utility.getInstance().checkIsContentModel(id);

        setHref(Constants.CONTENT_MODEL_URL_BASE + getId());
        setTitle(getProperty(TripleStoreUtility.PROP_DC_TITLE));
        setDescription(getProperty(TripleStoreUtility.PROP_DC_DESCRIPTION));

        contentStreams = new HashMap<String, Datastream>();
        otherStreams = new HashMap<String, Datastream>();
        initDatastreams(getDatastreamInfos());
    }

    /**
     * See Interface for functional description.
     * 
     * @return resource properties.
     * 
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore request failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     * @see de.escidoc.core.common.business.fedora.resources.GenericResource#getResourceProperties()
     */
    @Override
    public Map<String, String> getResourceProperties()
        throws TripleStoreSystemException, WebserverSystemException {

        if (!this.resourceInit) {
            super.getResourceProperties();

            // override dc properties
            try {
                addResourceProperties(getDublinCorePropertiesMap());
            }
            catch (XmlParserSystemException e) {
                throw new WebserverSystemException(e);
            }
            this.resourceInit = true;
        }
        // FIXME add caching of mapping
        return super.getResourceProperties();
    }

    private Map<String, String> getDublinCorePropertiesMap()
        throws XmlParserSystemException {

        // parse version-history
        final StaxParser sp = new StaxParser();
        final DcReadHandler dch = new DcReadHandler(sp);
        sp.addHandler(dch);
        try {
            sp.parse(getDc().getStream());
        }
        catch (final Exception e) {
            throw new XmlParserSystemException("Unexpected exception.", e);
        }
        return dch.getPropertiesMap();
    }

    public Datastream getDc() throws FedoraSystemException,
        WebserverSystemException {

        if (this.dc == null) {
            final Datastream ds;
            try {
                ds = new Datastream("DC", getId(), getVersionDate());
            }
            catch (StreamNotFoundException e) {
                throw new WebserverSystemException(e);
            }

            this.dc = ds;
        }

        return this.dc;
    }

    public void setDc(final Datastream ds) throws FedoraSystemException,
        WebserverSystemException {
        final Datastream curDs = getDc();
        if (!ds.equals(curDs)) {
            this.dc = ds;
            ds.merge();
        }
    }

    /**
     * Returns a Map containing all content streams of this content model. The
     * names of the content streams are the keys in the map. The map is
     * initialized creating this object.
     * 
     * @return The content streams of this content model.
     */
    public Map<String, Datastream> getContentStreams() {
        return this.contentStreams;
    }

    /**
     * Returns the specified content stream of this content model.
     * 
     * @param name
     *            The name of the content stream.
     * 
     * @return The specified content stream of this content model.
     */
    public Datastream getContentStream(final String name) {
        return this.contentStreams.get(name);
    }

    /**
     * Returns the specified other stream of this content model.
     * 
     * @param name
     *            The name of the content stream.
     * 
     * @return The specified content stream of this content model.
     */
    public Datastream getOtherStream(final String name) {
        return this.otherStreams.get(name);
    }

    /**
     * Init all content model datastreams. Some are initilized by super classes.
     * (This is faster than init of each single data stream).
     * 
     * @param datastreamInfos
     *            The Fedora datastream information.
     * 
     * @throws WebserverSystemException
     *             If an error occurs.
     * @throws FedoraSystemException
     *             If an error occurs accessing Fedora.
     * @throws TripleStoreSystemException
     *             If an error occurs accessing the triplestore.
     * @throws IntegritySystemException
     *             Thrown if there is an integrity error with the addressed
     *             object.
     * @throws StreamNotFoundException
     *             If a specific datastream can not be found.
     */
    @Override
    protected final void initDatastreams(
            final org.fcrepo.server.types.gen.Datastream[] datastreamInfos)
        throws WebserverSystemException, FedoraSystemException,
        TripleStoreSystemException, IntegritySystemException,
        StreamNotFoundException {

        super.initDatastreams(datastreamInfos);

        for (final org.fcrepo.server.types.gen.Datastream datastreamInfo : datastreamInfos) {
            final List<String> altIDs = Arrays.asList(datastreamInfo.getAltIDs());
            final String name = datastreamInfo.getID();
            final String label = datastreamInfo.getLabel();
            final DatastreamControlGroup controlGroup =
                    datastreamInfo.getControlGroup();
            final String controlGroupValue = controlGroup.getValue();
            final String mimeType = datastreamInfo.getMIMEType();
            final String location = datastreamInfo.getLocation();

            final Datastream ds;
            if (altIDs.contains("content-stream")) {
                // found content-stream
                ds =
                        new Datastream(name, getId(), getVersionDate(), mimeType,
                                location, controlGroupValue);
                ds.setAlternateIDs(new ArrayList<String>(altIDs));
                ds.setLabel(label);
                this.contentStreams.put(name, ds);
            } else if (name.equals(DATASTREAM_DS_COMPOSITE_MODEL)) {
                ds =
                        new Datastream(name, getId(), getVersionDate(), mimeType,
                                location, controlGroupValue);
                ds.setAlternateIDs(new ArrayList<String>(altIDs));
                ds.setLabel(label);
                this.dsCompositeModel = ds;
            } else if (!(name.equals(Datastream.RELS_EXT_DATASTREAM)
                    || "DC".equals(name) || name.equals(DATASTREAM_WOV))) {
                ds =
                        new Datastream(name, getId(), getVersionDate(), mimeType,
                                location, controlGroupValue);
                ds.setAlternateIDs(new ArrayList<String>(altIDs));
                ds.setLabel(label);
                this.otherStreams.put(name, ds);
            } else {
                log.debug("Datastream " + getId() + '/' + name
                        + " not instanziated in ContentModel.<init>.");
            }
        }
    }

    /**
     * Expand a list with names of properties values with the propertiesNames
     * for a versionized resource. These list could be used to request the
     * TripleStore.
     * 
     * @param propertiesNames
     *            Collection of propertiesNames. The collection contains only
     *            the version resource specific propertiesNames.
     * @return Parameter name collection
     */
    private Collection<String> expandPropertiesNames(
        final Collection<String> propertiesNames) {

        final Collection<String> newPropertiesNames;
        newPropertiesNames = propertiesNames != null ? propertiesNames : new ArrayList<String>();

        newPropertiesNames.add(TripleStoreUtility.PROP_CONTENT_CATEGORY);
        newPropertiesNames.add(TripleStoreUtility.PROP_DESCRIPTION);

        return newPropertiesNames;
    }

    /**
     * Expanding the properties naming map.
     * 
     * @param propertiesMapping
     *            The properties name mapping from external as key and the
     *            internal name as value. E.g. with the key "version-status" and
     *            "LATEST_VERSION_STATUS" as value is the value of
     *            "versin-status" after the mapping accessible with the internal
     *            key "LATEST_VERSION_STATUS".
     * @return The key mapping.
     */
    private Map<String, String> expandPropertiesNamesMapping(
        final Map<String, String> propertiesMapping) {

        final Map<String, String> newPropertiesNames;
        newPropertiesNames = propertiesMapping != null ? propertiesMapping : new HashMap<String, String>();
        newPropertiesNames.put(TripleStoreUtility.PROP_CONTENT_CATEGORY,
            PropertyMapKeys.LATEST_VERSION_CONTENT_CATEGORY);
        newPropertiesNames.put(TripleStoreUtility.PROP_DESCRIPTION,
            PropertyMapKeys.LATEST_VERSION_DESCRIPTION);

        return newPropertiesNames;
    }

    /**
     * Determines if the resource is in Public Status "withdrawn".
     * 
     * @return True if the resource is in status withdrawn. False otherwise.
     * 
     * @throws TripleStoreSystemException
     *             If an error occurs accessing the triplestore.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public boolean isWithdrawn() throws TripleStoreSystemException,
        WebserverSystemException {

        final String status = this.getProperty(PropertyMapKeys.PUBLIC_STATUS);
        return status.equals(Constants.STATUS_WITHDRAWN);
    }

    /**
     * Determines if the resource is in Public Status "released". That means it
     * is once released but not necessarily the latest version is released.
     * 
     * @return True if the resource is in status released. False otherwise.
     * 
     * @throws TripleStoreSystemException
     *             If an error occurs accessing the triplestore.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public boolean isReleased() throws TripleStoreSystemException,
        WebserverSystemException {

        final String status = this.getProperty(PropertyMapKeys.PUBLIC_STATUS);
        return status.equals(Constants.STATUS_RELEASED);
    }

    /**
     * Determines if the resource is in Public Status "pending".
     * 
     * @return True if the resource is in status pending. False otherwise.
     * 
     * @throws TripleStoreSystemException
     *             If an error occurs accessing the triplestore.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public boolean isPending() throws TripleStoreSystemException,
        WebserverSystemException {

        final String status = this.getProperty(PropertyMapKeys.PUBLIC_STATUS);
        return status.equals(Constants.STATUS_PENDING);
    }

    /**
     * Determines if the resource is in Public Status "in-revision".
     * 
     * @return True if the resource is in status in-revision. False otherwise.
     * 
     * @throws TripleStoreSystemException
     *             If an error occurs accessing the triplestore.
     * @throws WebserverSystemException
     *             If an error occurs.
     */
    public boolean isInRevision() throws TripleStoreSystemException,
        WebserverSystemException {

        final String status = this.getProperty(PropertyMapKeys.PUBLIC_STATUS);
        return status.equals(Constants.STATUS_IN_REVISION);
    }

    public List<DsTypeModel> getMdRecordDefinitionIDs()
        throws IntegritySystemException, WebserverSystemException {

        final StaxParser sp = new StaxParser();
        final DsCompositeModelHandler dcmh = new DsCompositeModelHandler(sp);
        sp.addHandler(dcmh);

        try {
            final Datastream dcm = getDsCompositeModel();
            if (dcm == null) {
                // in case of no DS_COMPOSITE_MODEL datastream, behave as if the
                // parser does not found any md-record-definition.
                // Usually a Content Model should have - at least an empty -
                // DS_COMPOSITE_MODEL datastream. But some old ones may have
                // not.
                return new ArrayList<DsTypeModel>();
            }
            final String x = dcm.toStringUTF8();
            sp.parse(x);
        }
        catch (IntegritySystemException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw new WebserverSystemException(
                "Unexpected exception parsing datastream DS-COMPOSITE-MODEL.",
                e);
        } catch (Exception e) {
            throw new WebserverSystemException(
                "Unexpected exception parsing datastream DS-COMPOSITE-MODEL.",
                e);
        }

        return dcmh.getDsTypeModels();
    }

    public Map<String, ResourceDefinitionCreate> getResourceDefinitions()
        throws WebserverSystemException, IntegritySystemException {

        if (this.resourceDefinitions == null) {
            this.resourceDefinitions =
                new HashMap<String, ResourceDefinitionCreate>();

            // get list of service references
            final Map<String, String> services;
            try {
                final Collection<String> pl = new ArrayList<String>();
                pl.add("info:fedora/fedora-system:def/model#hasService");
                services = this.getResourceProperties(pl);
            }
            catch (TripleStoreSystemException e) {
                throw new WebserverSystemException(
                    "Can not access triplestore.", e);
            }

            for (final Entry<String, String> entry : services.entrySet()) {
                final String serviceName =
                        entry.getValue().substring(
                                entry.getValue().lastIndexOf('-') + 1);
                final ResourceDefinitionCreate resourceDef =
                        new ResourceDefinitionCreate();
                try {
                    resourceDef.setName(serviceName);
                    // FIXME retrieve md-record
                    resourceDef.setMdRecordName("escdioc");
                    // FIXME create correct href?
                    // resourceDef.setXsltHref("");
                } catch (MissingAttributeValueException e) {
                    throw new IntegritySystemException(
                            "Service ID but no name.", e);
                }
                this.resourceDefinitions.put(serviceName, resourceDef);
            }
        }

        return this.resourceDefinitions;
    }

    private Datastream getDsCompositeModel() {
        return this.dsCompositeModel;
    }

    public void setDsCompositeModel(final String xml)
        throws WebserverSystemException {

        try {
            final Datastream ds =
                new Datastream("DS-COMPOSITE-MODEL", getId(),
                    xml.getBytes(XmlUtility.CHARACTER_ENCODING), "text/xml");
            setDsCompositeModel(ds);
        }
        catch (UnsupportedEncodingException e) {
            throw new WebserverSystemException(e);
        }
    }

    public void setDsCompositeModel(final Datastream ds) {

        if (!getDsCompositeModel().equals(ds)) {
            this.dsCompositeModel = ds;
            this.setNeedSync(true);
        }
    }

    public void setContentStreams(
        final Map<String, Datastream> contentStreamDatastreams)
        throws FedoraSystemException, WebserverSystemException {

        final Set<String> namesInFedora = getContentStreams().keySet();

        final Iterator<Entry<String, Datastream>> nameIt =
            contentStreamDatastreams.entrySet().iterator();
        // create/activate data streams which are in contentStreamDatastreams
        // but not in fedora
        // TODO: Check wether contentStreamDatastreams is used outside of this
        // method. If not, replace the following while and for with one
        // entry-set iterator
        while (nameIt.hasNext()) {
            final Entry<String, Datastream> mapEntry = nameIt.next();
            final String name = mapEntry.getKey();
            final Datastream current = mapEntry.getValue();
            // add DS ...
            setContentStream(name, current);
            if (!namesInFedora.contains(name)) {
                // and remove it from given list
                nameIt.remove();
            }
        }

        // delete data streams which are in fedora but not in given list
        for (final String nameInFedora : namesInFedora) {
            if (!contentStreamDatastreams.containsKey(nameInFedora)) {
                final Datastream fedoraDs = getContentStream(nameInFedora);
                fedoraDs.delete();
                this.contentStreams.remove(nameInFedora);
            }
        }
    }

    public void setContentStream(final String name, final Datastream ds)
        throws WebserverSystemException, FedoraSystemException {
        // don't trust the handler
        final List<String> alternateIDs = new ArrayList<String>();
        alternateIDs.add("content-stream");
        ds.setAlternateIDs(alternateIDs);

        final Datastream curDs = getContentStream(name);

        setStream(name, ds, curDs);
        this.contentStreams.put(name, ds);
    }

    public void setOtherStream(final String name, final Datastream ds)
        throws WebserverSystemException, FedoraSystemException {
        final Datastream curDs = getOtherStream(name);
        setStream(name, ds, curDs);
        this.otherStreams.put(name, ds);
    }

    private void setStream(
        final String name, final Datastream ds, final Datastream curDs)
        throws WebserverSystemException, FedoraSystemException {
        try {

            boolean contentChanged = false;
            boolean isNew = false;

            if (curDs == null) {
                isNew = true;
            }
            else {
                // FIXME change storage and mime-type ? (FRS)
                try {
                    if (ds.getLocation() != null
                        && !ds.getLocation().equals(curDs.getLocation())
                        && !(ds.getLocation().startsWith(
                            "/cmm/content-model/" + getId()) || ds
                            .getLocation().startsWith(
                                EscidocConfiguration.getInstance().get(
                                    EscidocConfiguration.ESCIDOC_CORE_BASEURL)
                                    + "/cmm/content-model/" + getId()))) {
                        contentChanged = true;
                    }
                    else {
                        ds.setContentUnchanged(true);
                    }
                    if (!curDs.getControlGroup().equals(ds.getControlGroup()) || !curDs.getMimeType().equals(ds.getMimeType()) || ds.getLabel() != null && curDs.getLabel() != null
                            && !curDs.getLabel().equals(ds.getLabel()) || !ds.equals(curDs)) {
                        contentChanged = true;
                    }
                }
                catch (IOException e) {
                    // FIXME catch IOE in EscidocConfiguration.getInstance()
                    throw new WebserverSystemException(e);
                }
            }

            // isNew does not indicate that the datastream does not exist
            // in fedora, it may be deleted
            if (contentChanged || isNew) {
                ds.merge();
            }
        }
        catch (final FedoraSystemException e) {
            log.debug("Error on setting stream.", e);
            // this is not an update; its a create
            ds.persist(false);
        }

    }

    /**
     * Persists the whole object to Fedora and force the TripleStore sync.
     * 
     * @return lastModificationDate of the resource (Attention this timestamp
     *         differs from the last-modification timestamp of the repository.
     *         See Versioning Concept.)
     * 
     * @throws FedoraSystemException
     *             Thrown if connection to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    @Override
    public String persist() throws FedoraSystemException,
        WebserverSystemException {

        if (this.isNeedSync()) {
            persistDsCompositeModel();
        }

        return persist(true);
    }

    private String persistDsCompositeModel() throws FedoraSystemException,
        WebserverSystemException {

        String timestamp = null;
        if (this.dsCompositeModel != null) {
            timestamp = this.dsCompositeModel.merge();
        }
        return timestamp;

    }
}
