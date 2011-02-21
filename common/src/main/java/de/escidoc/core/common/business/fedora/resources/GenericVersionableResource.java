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
import de.escidoc.core.common.business.PropertyMapKeys;
import de.escidoc.core.common.business.fedora.Triple;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.AddNewSubTreesToDatastream;
import de.escidoc.core.common.util.stax.handler.ItemRelsExtUpdateHandler;
import de.escidoc.core.common.util.stax.handler.RelsExtReadHandler;
import de.escidoc.core.common.util.stax.handler.WovReadHandler;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.CommonFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;
import org.fcrepo.server.types.gen.DatastreamControlGroup;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Generic Versionable Resource.
 * 
 * @author SWA
 * 
 */
public class GenericVersionableResource extends GenericResourcePid {

    /**
     * Separator of the identifier version suffix (escidoc:123:XY).
     */
    public static final String VERSION_NUMBER_SEPARATOR = ":";

    /**
     * Name of the version history (WOV) data stream.
     */
    public static final String DATASTREAM_WOV = "version-history";

    private static final AppLogger LOG =
        new AppLogger(GenericVersionableResource.class.getName());

    private String description = null;

    protected Datastream wov = null;

    private Map<String, String> currentVersionData = null;

    /**
     * Number of the version. Is null if it is the latest version.
     */
    private String versionNumber = null;

    // has the version number independent if it is the latest version or not
    private String versionId = null;

    private Map<String, String> lastVersionData;

    private String latestReleaseVersionNumber = null;

    private boolean initLastModifiedDate = true;

    // ---------------
    private boolean publicStatusChange = false;

    private boolean versionStatusChange = false;

    /**
     * 
     */
    public void setPublicStatusChange() {
        this.publicStatusChange = !this.publicStatusChange;
    }

    /**
     * Indicate if public status has changed.
     * 
     * @return true if public status has changed, false otherwise.
     */
    public boolean hasPublicStatusChanged() {
        return this.publicStatusChange;
    }

    /**
     * Indicate that version status has changed.
     */
    public void setVersionStatusChange() {
        this.versionStatusChange = !this.versionStatusChange;
    }

    /**
     * Indicate if version status has changed.
     * 
     * @return true if version status has changed, false otherwise.
     */
    public boolean hasVersionStatusChanged() {
        return this.versionStatusChange;
    }

    // --------------
    /**
     * Generic Versionable Object.
     * 
     * @param id
     *            The id of the object in the repository.
     * @throws ResourceNotFoundException
     *             Thrown if the resource with the provided objid was not found.
     * @throws TripleStoreSystemException
     *             Thrown in case of TripleStore error.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    public GenericVersionableResource(final String id)
        throws TripleStoreSystemException, WebserverSystemException,
        ResourceNotFoundException {

        super();
        setPropertiesNames(expandPropertiesNames(getPropertiesNames()),
            expandPropertiesNamesMapping(getPropertiesNamesMapping()));
        setId(id);
        try {
            setLastVersionData();
        }
        catch (WebserverSystemException e) {
            if (!TripleStoreUtility.getInstance().exists(id)) {
                String msg =
                    "Resource with the provided objid '" + id
                        + "' does not exist.";
                LOG.debug(msg);
                throw new ResourceNotFoundException(msg, e);
            }
            else {
                String msg = "Unexpected exception during RELS-EXT parsing.";
                LOG.warn(msg + e);
                throw new WebserverSystemException(e);
            }

        }
    }

    /**
     * Set the Id of the versionable Resource.
     * 
     * @param id
     *            The id of the object in the repository.
     * @throws ResourceNotFoundException
     *             Thrown if the resource with the provided objid was not found.
     * @throws TripleStoreSystemException
     *             Thrown in case of TripleStore error.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    @Override
    public void setId(final String id) throws TripleStoreSystemException,
        WebserverSystemException, ResourceNotFoundException {

        super.setId(id);
        this.versionId = XmlUtility.getVersionNumberFromObjid(id);

        // determine version Number (suffix). Depending on latest-version and
        // status is the versionNumber the suffix or null.
        this.versionNumber = id.replaceFirst(getId(), "");

        if ((this.versionNumber != null) && (this.versionNumber.length() > 0)) {

            this.versionNumber = this.versionNumber.substring(1);
            String latestVersionNumber =
                TripleStoreUtility.getInstance().getPropertiesElements(getId(),
                    TripleStoreUtility.PROP_LATEST_VERSION_NUMBER);
            if ((latestVersionNumber == null)
                || (Integer.valueOf(this.versionId) > Integer
                    .valueOf(latestVersionNumber))) {
                String message =
                    "The version " + versionNumber
                        + " of the requested resource " + "does not exist.";
                throw new ResourceNotFoundException(message);
            }
            if (this.versionNumber.equals(latestVersionNumber)) {
                this.versionNumber = null;
            }
        }
        else {
            if (UserContext.isRetrieveRestrictedToReleased()) {
                this.versionNumber =
                    TripleStoreUtility.getInstance().getPropertiesElements(
                        getId(), TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER);
                if (this.versionNumber == null) {
                    throw new ResourceNotFoundException(
                        "Latest release not found.");
                }
            }
            else {
                this.versionNumber = null;
            }
        }

    }

    /**
     * Get the objectId inclusive version number.
     * 
     * @return Id inclusive version number
     */
    public String getFullId() {
        if (this.versionId == null || this.versionId.length() <= 0) {
            return (getId() + VERSION_NUMBER_SEPARATOR + getLatestVersionNumber());
        }
        return (getId() + VERSION_NUMBER_SEPARATOR + this.versionId);
    }

    /**
     * Get the Number of the version. If the version Suffix is not set then is
     * the version Number null!
     * 
     * @return number of version
     */
    public String getVersionNumber() {
        if (versionNumber == null || versionNumber.length() <= 0) {
            return null;
        }
        return versionNumber;
    }

    /**
     * Get the status of the current version.
     * 
     * @return version status.
     * 
     * @throws IntegritySystemException
     *             Thrown if determining failed.
     */
    public String getVersionStatus() throws IntegritySystemException {
        return getVersionElementData(PropertyMapKeys.CURRENT_VERSION_STATUS);
    }

    /**
     * Set the status of the current version (version-status).
     * 
     * @param versionStatus
     *            new status of version
     * @throws IntegritySystemException
     *             Thrown if determining failed.
     */
    public void setVersionStatus(final String versionStatus)
        throws IntegritySystemException {
        getVersionData().put(PropertyMapKeys.CURRENT_VERSION_STATUS,
            versionStatus);
    }

    /**
     * Get the Number of the version. This values is ever unequal null! If the
     * version Suffix is not set then is the version Number the number of the
     * current retrieved version.
     * 
     * @return number of version
     */
    public String getVersionId() {
        if (this.versionId == null) {
            String curVersionId = getVersionNumber();
            if (curVersionId != null) {
                return curVersionId;
            }

            return getLatestVersionNumber();
        }
        return this.versionId;
    }

    /**
     * Get creation date of a versionated resource.
     * 
     * Attention: The creation date of a resource differs from the creation date
     * in the Fedora resource.
     * 
     * @return creation date
     * 
     * @throws TripleStoreSystemException
     *             Thrown if request to TripleStore failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    @Override
    public String getCreationDate() throws TripleStoreSystemException,
        WebserverSystemException {

        if (this.creationDate == null) {
            /*
             * The creation version date is the date of the first version. This
             * is not the creation date of the Fedora object! With Fedora
             * 3.2/3.3 is the date indirectly obtained from the date of the
             * first RELS-EXT version or from the version/date entry of the
             * second version of RELS-EXT or the 'created date' of the RELS-EXT
             * datastream.
             * 
             * Another way would be to obtain the creation date from the WOV.
             * 
             * The current implementation derives the creation date from the
             * 'created Date' of the RELS-EXT datastream.
             */

            try {
                org.fcrepo.server.types.gen.Datastream[] datastreams =
                    getFedoraUtility().getDatastreamHistory(getId(),
                        Datastream.RELS_EXT_DATASTREAM);
                this.creationDate =
                    datastreams[datastreams.length - 1].getCreateDate();
            }
            catch (FedoraSystemException e) {
                throw new WebserverSystemException(e);
            }

        }
        return this.creationDate;
    }

    /**
     * Get date of version.
     * 
     * @return date of version
     * 
     * @throws WebserverSystemException
     *             Thrown if value reading failed.
     */
    public String getVersionDate() throws WebserverSystemException {

        String versionDate;
        try {
            versionDate =
                getVersionElementData(PropertyMapKeys.CURRENT_VERSION_VERSION_DATE);
        }
        catch (IntegritySystemException e) {
            throw new WebserverSystemException(e);
        }

        return versionDate;
    }

    /**
     * Set the versionDate.
     * 
     * @param timestamp
     *            VersionDate timestamp
     * @throws WebserverSystemException
     *             Thrown if setting of value failed.
     */
    public void setVersionDate(final String timestamp)
        throws WebserverSystemException {
        try {
            setVersionElementData(TripleStoreUtility.PROP_LATEST_VERSION_DATE,
                timestamp);
        }
        catch (IntegritySystemException e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * Get the latest version number of the object.
     * 
     * @return latest version number
     */
    public String getLatestVersionNumber() {

        return lastVersionData
            .get(TripleStoreUtility.PROP_LATEST_VERSION_NUMBER);
    }

    /**
     * Get the full object identifier with version number.
     * 
     * @return object identifier with version number
     */
    public String getLatestVersionId() {
        return (getId() + VERSION_NUMBER_SEPARATOR + getLatestVersionNumber());
    }

    /**
     * Get the resource description.
     * 
     * @return description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Set the resource description.
     * 
     * @param description
     *            New description of resource.
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    // --------------------------------------------------------------------------

    /**
     * Returns the href of the Container where the version suffix depends on the
     * object initialization. If the Container was instanced with version suffix
     * then contains the href the version suffix otherwise not.
     * 
     * @return Return the link to the Container (with diversifing version
     *         suffix).
     */
    @Override
    public String getHref() {

        if (getVersionNumber() == null) {
            return super.getHref();
        }
        return super.getHref() + VERSION_NUMBER_SEPARATOR + getVersionNumber();
    }

    /**
     * Return the href of the Container where ever the version suffix is
     * included.
     * 
     * @return href with version suffix
     */
    public String getVersionHref() {

        return super.getHref() + VERSION_NUMBER_SEPARATOR + getVersionId();
    }

    /**
     * Return the href of the Container without version suffix.
     * 
     * @return href without version suffix
     */
    public String getHrefWithoutVersionNumber() {

        return super.getHref();
    }

    /**
     * Return the href to the latest version of the Container (where ever the
     * version suffix is included).
     * 
     * @return href to the latest Container version (with version suffix)
     */
    public String getLatestVersionHref() {

        return super.getHref() + VERSION_NUMBER_SEPARATOR
            + getLatestVersionNumber();
    }

    /**
     * Get number of latest released version.
     * 
     * @return latest-release version number
     * @throws WebserverSystemException
     *             Thrwon if TripleStore request failed.
     */
    public String getLatestReleaseVersionNumber()
        throws WebserverSystemException {

        if (this.latestReleaseVersionNumber == null) {
            try {
                this.latestReleaseVersionNumber =
                    TripleStoreUtility.getInstance().getPropertiesElements(
                        getId(), TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER);
            }
            catch (TripleStoreSystemException tse) {
                throw new WebserverSystemException(tse);
            }
        }

        return this.latestReleaseVersionNumber;
    }

    // --------------------------------------------------------------------------

    /**
     * Get HashMap of with latest version data.
     * 
     * @return latest version data
     */
    public Map<String, String> getLastVersionData() {

        return this.lastVersionData;
    }

    /**
     * Get last modification date of the resource. This modification date
     * differs from the Fedora object (last) modification date!
     * 
     * @return last-modification-date
     * 
     * @throws FedoraSystemException
     *             Thrown if access to Fedora fails.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    @Override
    public String getLastModificationDate() throws WebserverSystemException,
        FedoraSystemException {

        if (this.initLastModifiedDate) {
            if (isLatestVersion()) {
                // we can take this information from TripleStore (should be
                // faster)
                try {
                    setLastModificationDate(TripleStoreUtility
                        .getInstance().getPropertiesElements(getId(),
                            TripleStoreUtility.PROP_LATEST_VERSION_DATE));
                }
                catch (TripleStoreSystemException e) {
                    throw new WebserverSystemException(e);
                }
            }
            else {
                // use a parser to get the information for older versions
                try {
                    setLastModificationDate(getVersionElementData(Elements.ELEMENT_WOV_VERSION_TIMESTAMP));
                }
                catch (IntegritySystemException e) {
                    throw new WebserverSystemException(e);
                }
            }
            this.initLastModifiedDate = false;
        }

        return super.getLastModificationDate();
    }

    /**
     * Is resource latest version?
     * 
     * @return true if the resource is latest version. False otherwise.
     */
    public boolean isLatestVersion() {
        if (this.versionNumber == null) {
            return true;
        }
        else if (this.versionNumber.equals(getLatestVersionNumber())) {
            return true;
        }
        return false;
    }

    /**
     * Is resource latest version?
     * 
     * @return true if the resource is latest version. False otherwise.
     * @throws IntegritySystemException
     *             Thrown if required values are not available.
     */
    public boolean isLatestRelease() throws IntegritySystemException {

        return isLatestVersion() && getVersionStatus().equals("released");
    }

    /**
     * Retrieve the properties of the last version and prepare them as HashMap.
     * 
     * @return properties of latest version as HashMap
     * 
     * @throws TripleStoreSystemException
     *             Thrown if request of TripleStore failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     */
    public Map<String, String> setLastVersionData()
        throws TripleStoreSystemException, WebserverSystemException {

        final StaxParser sp = new StaxParser();

        final RelsExtReadHandler eve = new RelsExtReadHandler(sp);
        eve.cleanIdentifier(true);
        sp.addHandler(eve);
        try {
            sp.parse(getRelsExt(null).getStream());
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e);
        }

        this.lastVersionData = new HashMap<String, String>();

        List<Triple> triples = eve.getElementValues().getTriples();

        for (Triple triple : triples) {
            Triple t = triple;
            this.lastVersionData.put(t.getPredicate(), t.getObject());
        }

        return this.lastVersionData;
    }

    /**
     * Get object version data for the current version of resource. This values
     * are only read once from the WOV data stream (cached).
     * 
     * @return value of element or null
     * @throws IntegritySystemException
     *             Thrown if the integrity of WOV data is violated.
     */
    public Map<String, String> getVersionData() throws IntegritySystemException {

        if (this.currentVersionData == null) {
            try {
                this.currentVersionData = getVersionData(getVersionNumber());
            }
            catch (Exception e) {
                throw new IntegritySystemException(
                    "No version data for resource " + getId() + '.' + e);
            }
        }

        return this.currentVersionData;
    }

    /**
     * Get object version data for the specified version of resource. These
     * values are prepared with every request and not cached.
     * 
     * @param versionNo
     *            Number of version of resource
     * @return Map of version element values
     * 
     * @throws IntegritySystemException
     *             Thrown if the integrity of WOV data is violated.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    public Map<String, String> getVersionData(final String versionNo)
        throws IntegritySystemException, WebserverSystemException {

        Map<String, String> versionData;

        try {
            versionData = super.getResourceProperties();
        }
        catch (TripleStoreSystemException e) {
            throw new WebserverSystemException(e);
        }
        if (versionNo != null) {
            try {
                StaxParser sp = new StaxParser();
                WovReadHandler wrh = new WovReadHandler(sp, versionNo);
                sp.addHandler(wrh);
                sp.parse(getWov().getStream());
                Map<String, String> prop = wrh.getVersionData();
                versionData.putAll(prop);
            }
            catch (Exception e) {
                throw new IntegritySystemException(
                    "No version data for resource " + getId() + '.' + e);
            }
        }

        return versionData;
    }

    /**
     * Get resource version data for a selected element.
     * 
     * @param elementName
     *            Name of the Element.
     * @return value of element or null
     * 
     * @throws IntegritySystemException
     *             Thrown if data integrity is violated.
     */
    public String getVersionElementData(final String elementName)
        throws IntegritySystemException {

        return (getVersionData().get(elementName));
    }

    /**
     * Set values for the resource version.
     * 
     * @param elementName
     *            Name of the value.
     * @param value
     *            new value.
     * @throws WebserverSystemException
     *             Thrown in case of internal failure.
     * @throws IntegritySystemException
     *             Thrown if data integrity is violated.
     */
    private void setVersionElementData(
        final String elementName, final String value)
        throws WebserverSystemException, IntegritySystemException {

        getVersionData().put(elementName, value);
    }

    // -------------------------------------------------------------------------

    /**
     * Get Whole Object Version datastream (WOV).
     * 
     * @return WOV
     * @throws StreamNotFoundException
     *             Thrown if wov datastream was not found.
     * @throws FedoraSystemException
     *             Thrown in case of Fedora error.
     */
    public Datastream getWov() throws StreamNotFoundException,
        FedoraSystemException {

        if (this.wov == null) {
            this.wov = new Datastream(DATASTREAM_WOV, getId(), null);
        }
        return this.wov;
    }

    /**
     * Write the WOV data stream to Fedora repository.
     * 
     * @param ds
     *            The WOV data stream.
     * @throws StreamNotFoundException
     *             Thrown if the WOV data stream was not found.
     * @throws FedoraSystemException
     *             Thrown in case of Fedora error.
     */
    public void setWov(final Datastream ds) throws FedoraSystemException,
        StreamNotFoundException {

        if (!getWov().equals(ds)) {
            this.wov = ds;
            this.setNeedSync(true);
        }
    }

    /**
     * Write the Wov data stream to Fedora repository.
     * 
     * @param ds
     *            A byte array of the WOV data stream.
     * @throws StreamNotFoundException
     *             Thrown if the WOV data stream was not found.
     * @throws FedoraSystemException
     *             Thrown if update of WOV data stream in Fedora failed.
     * @throws TripleStoreSystemException
     *             Thrown if request of TripleStore failed.
     * @throws IntegritySystemException
     *             Thrown if data integrity is violated.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    public void setWov(final byte[] ds) throws StreamNotFoundException,
        FedoraSystemException, TripleStoreSystemException,
        IntegritySystemException, WebserverSystemException {

        setWov(new Datastream(Elements.ELEMENT_WOV_VERSION_HISTORY, getId(),
            ds, "text/xml"));

    }

    /**
     * Update the timestamp of the version within RELS-EXT.
     * 
     * @param newVersionTimestamp
     *            The timestamp of the version.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    public void updateRelsExtVersionTimestamp(final String newVersionTimestamp)
        throws WebserverSystemException {

        // TODO this method should be better called
        // setLastModificationDate(timestamp)
        // (and override the inherited method)

        // last operation is to update the timestamp in RELS-EXT
        Map<String, StartElementWithChildElements> updateElementsRelsExt =
            new TreeMap<String, StartElementWithChildElements>();

        updateElementsRelsExt.put(Constants.VERSION_NS_URI
            + Elements.ELEMENT_DATE, new StartElementWithChildElements(
            Constants.VERSION_NS_URI + Elements.ELEMENT_DATE,
            Constants.VERSION_NS_URI, Constants.VERSION_NS_PREFIX, null,
            newVersionTimestamp, null));

        // if status has changed to release than update latest-release/date
        try {
            if (hasVersionStatusChanged()
                && getVersionStatus().equals(Constants.STATUS_RELEASED)) {
                updateElementsRelsExt.put(Constants.RELEASE_NS_URI
                    + Elements.ELEMENT_DATE, new StartElementWithChildElements(
                    Constants.RELEASE_NS_URI + Elements.ELEMENT_DATE,
                    Constants.RELEASE_NS_URI, Constants.RELEASE_NS_PREFIX,
                    null, newVersionTimestamp, null));
            }
        }
        catch (IntegritySystemException e) {
            throw new WebserverSystemException(e);
        }

        updateRelsExt(updateElementsRelsExt, null);
        setLastModificationDate(newVersionTimestamp);
    }

    /**
     * Update the timestamp of the version within RELS-EXT. The timestamp is
     * fetched from the Fedora object.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     * @throws TripleStoreSystemException
     *             Thrown if request of TripleStore failed.
     * @throws FedoraSystemException
     *             Thrown if update of RELS-EXT data stream failed.
     */
    public void updateRelsExtVersionTimestamp()
        throws WebserverSystemException, TripleStoreSystemException,
        FedoraSystemException {

        // TODO this method should be better called setLastModificationDate()
        // (and override the inherited method)
        updateRelsExtVersionTimestamp(getLastFedoraModificationDate());
    }

    // --------------------------------------------------------------------------

    /**
     * It shouldn't be necessary to retrieve values from Fedora again, which the
     * last process has written in.
     * 
     * @throws TripleStoreSystemException
     *             Thrown if request of TripleStore failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    @Deprecated
    protected void getSomeValuesFromFedora() throws TripleStoreSystemException,
        WebserverSystemException {

        setLastVersionData();
        if (getVersionNumber() == null) {
            this.description =
                TripleStoreUtility.getInstance().getPropertiesElements(getId(),
                    Constants.DC_NS_URI + Elements.ELEMENT_DESCRIPTION);
        }
    }

    // --------------------------------------------------------------------------

    /**
     * Get the RELS-EXT for the corresponding version of the Resource.
     * 
     * @return RELS-EXT corresponding to the Resource version.
     * @throws StreamNotFoundException
     *             Thrown if the RELS-EXT data stream (with specified version)
     *             was not found.
     * @throws FedoraSystemException
     *             Thrown in case of internal error.
     */
    @Override
    public Datastream getRelsExt() throws StreamNotFoundException,
        FedoraSystemException {

        if (this.relsExt == null) {
            try {
                // Workaround until framework uses only one RELS-EXT per update
                if (isLatestVersion()) {
                    setRelsExt(new Datastream(Datastream.RELS_EXT_DATASTREAM,
                        getId(), null));
                }
                else {

                    setRelsExt(new Datastream(Datastream.RELS_EXT_DATASTREAM,
                        getId(), getVersionDate()));
                }
            }
            catch (WebserverSystemException e) {
                throw new FedoraSystemException(e);
            }
        }

        return super.getRelsExt();
    }

    /**
     * Get the RELS-EXT version defined by timestamp. This version is not
     * cached.
     * 
     * @param timestamp
     *            The timestamp of the RELS-EXT version which is to load.
     * @return RELS-EXT corresponding to the timestamp.
     * 
     * @throws StreamNotFoundException
     *             Thrown if the RELS-EXT data stream (with specified timestamp)
     *             was not found.
     * @throws FedoraSystemException
     *             Thrown in case of internal error.
     */
    public Datastream getRelsExt(final String timestamp)
        throws StreamNotFoundException, FedoraSystemException {

        Datastream relsExt =
            new Datastream(Datastream.RELS_EXT_DATASTREAM, getId(), timestamp);

        return relsExt;
    }

    // --------------------------------------------------------------------------

    /**
     * Parse data from WOV data stream. The values are retrievable via the
     * getVersionData() method.
     * 
     * @throws ResourceNotFoundException
     *             Thrown if the resource was not found.
     * @throws XmlParserSystemException
     *             Thrown in case of parser errors.
     * @throws WebserverSystemException
     *             Thrown in case of internal errors.
     */
    protected void setVersionData() throws ResourceNotFoundException,
        XmlParserSystemException, WebserverSystemException {

        // parse version-history
        StaxParser sp = new StaxParser();
        WovReadHandler wrh = new WovReadHandler(sp, this.versionNumber);
        sp.addHandler(wrh);
        try {
            sp.parse(this.getWov().getStream());
        }
        catch (IntegritySystemException e) {
            throw new XmlParserSystemException(e);
        }
        catch (Exception e) {
            throw new XmlParserSystemException("Unexpected exception.", e);
        }
        currentVersionData = wrh.getVersionData();
        if (currentVersionData == null || currentVersionData.size() <= 1) {
            throw new ResourceNotFoundException("Can not retrieve version '"
                + this.versionNumber + "' for Resource '" + getId() + "'.");
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

        return persist(true);
    }

    /**
     * Persists the whole object to Fedora.
     * 
     * @param sync
     *            Set <code>true</code> if TripleStore sync is to force.
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
    public String persist(final boolean sync) throws FedoraSystemException,
        WebserverSystemException {
        /*
         * Persist persists the data streams of the object and updates all
         * version depending values. These values are RELS-EXT (version/date)
         * and WOV timestamp.
         * 
         * It is assumed that all data (except timestamp information) are
         * up-to-date in the datastreams! Afterwards should no operations be
         * necessary.
         * 
         * Procedure to persist an resource with versions:
         * 
         * 1. write RELS-EXT
         * 
         * 2. get last-modifcation-date from fedora object (We need the
         * timestamp from RELS-EXT precisely. But if no other method writes
         * (hopefully) to this object so we can use the object timestamp.)
         * 
         * 3. write the timestamp to the WOV
         * 
         * 4. Update RELS-EXT (/version/date) with the timestamp (which is
         * written to WOV)
         * 
         * Note: These are to many data stream updates to write one single
         * information (timestamp)
         */
        String timestamp = null;
        if (this.isNeedSync()) {
            // ----------------------------------------------
            // writing RELS-EXT once (problem: /version/date is to old)
            // timestamp = getLastFedoraModificationDate();
            //
            // updateRelsExtVersionTimestamp(timestamp);
            // persistRelsExt();
            // updateWovTimestamp(getVersionNumber(), timestamp);
            // persistWov();

            // ----------------------------------------------
            // writing RELS-EXT twice.
            timestamp = persistRelsExt();
            if (timestamp == null) {
               timestamp = getLastFedoraModificationDate();
            }
            updateWovTimestamp(getVersionNumber(), timestamp);
            persistWov();
            updateRelsExtVersionTimestamp(timestamp);
            persistRelsExt();
            setResourceProperties(PropertyMapKeys.LAST_MODIFICATION_DATE,
                timestamp);
        }

        if (sync) {
            getFedoraUtility().sync();
        }

        return timestamp;
    }

    /**
     * Write WOV (Whole Object Versioning Datastream) to Fedora.
     * 
     * @return The new timestamp of the WOV data stream or null if not written
     *         to Fedora.
     * 
     * @throws FedoraSystemException
     *             Thrown if connection to Fedora failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     */
    protected String persistWov() throws FedoraSystemException,
        WebserverSystemException {

        String timestamp = null;
        if (this.wov != null) {
            timestamp = this.wov.merge();
        }
        return timestamp;
    }

    /**
     * Update Version History (WOV) with (last-modification) timestamp.
     * 
     * @param versionNo
     *            Number of version which is updated (mostly the latest, but not
     *            ever!) If null the latest version is updated.
     * @param timestamp
     *            The timestamp which is to write to WOV
     * @throws FedoraSystemException
     *             If Fedora reports an error.
     * @throws WebserverSystemException
     *             In case of an internal error.
     */
    protected void updateWovTimestamp(
        final String versionNo, final String timestamp)
        throws FedoraSystemException, WebserverSystemException {

        try {
            byte[] b = getWov().getStream();
            String tmpWov = new String(b, XmlUtility.CHARACTER_ENCODING);
            tmpWov =
                tmpWov.replaceAll(XmlTemplateProvider.TIMESTAMP_PLACEHOLDER,
                    timestamp);
            setWov(new Datastream(Elements.ELEMENT_WOV_VERSION_HISTORY,
                getId(), tmpWov.getBytes(XmlUtility.CHARACTER_ENCODING),
                "text/xml"));
        }
        catch (Exception e1) {
            throw new WebserverSystemException(e1);
        }
    }

    /**
     * Create a new event entry for WOV. (this version is an altered from
     * Utility class and should replace it).
     * 
     * @param latestModificationTimestamp
     *            The timestamp of the event.
     * @param newStatus
     *            The version status of the resource.
     * @param comment
     *            The event comment.
     * @return The new event entry
     * 
     * @throws WebserverSystemException
     * @throws IntegritySystemException
     */
    protected String createEventXml(
        final String latestModificationTimestamp, final String newStatus,
        final String comment) throws WebserverSystemException,
        IntegritySystemException {

        HashMap<String, String> eventValues = new HashMap<String, String>();

        eventValues.put(XmlTemplateProvider.VAR_EVENT_TYPE, newStatus);
        eventValues.put(XmlTemplateProvider.VAR_EVENT_XMLID, 'v'
            + getVersionElementData(PropertyMapKeys.LATEST_VERSION_NUMBER)
            + 'e' + System.currentTimeMillis());
        eventValues.put(XmlTemplateProvider.VAR_EVENT_ID_TYPE,
            Constants.PREMIS_ID_TYPE_URL_RELATIVE);
        eventValues.put(XmlTemplateProvider.VAR_EVENT_ID_VALUE,
            getHrefWithoutVersionNumber() + "/resources/"
                + Elements.ELEMENT_WOV_VERSION_HISTORY + '#'
                + eventValues.get(XmlTemplateProvider.VAR_EVENT_XMLID));
        eventValues.put(XmlTemplateProvider.TIMESTAMP,
            latestModificationTimestamp);
        eventValues.put(XmlTemplateProvider.VAR_COMMENT, XmlUtility
            .escapeForbiddenXmlCharacters(comment));
        eventValues.put(XmlTemplateProvider.VAR_AGENT_BASE_URI,
            Constants.USER_ACCOUNT_URL_BASE);
        eventValues.put(XmlTemplateProvider.VAR_AGENT_TITLE, UserContext
            .getRealName());
        eventValues.put(XmlTemplateProvider.VAR_AGENT_ID_TYPE,
            Constants.PREMIS_ID_TYPE_ESCIDOC);
        eventValues.put(XmlTemplateProvider.VAR_AGENT_ID_VALUE, UserContext
            .getId());
        eventValues.put(XmlTemplateProvider.VAR_OBJECT_ID_TYPE,
            Constants.PREMIS_ID_TYPE_ESCIDOC);
        eventValues.put(XmlTemplateProvider.VAR_OBJECT_ID_VALUE, getId());

        return CommonFoXmlProvider.getInstance().getPremisEventXml(eventValues);
    }

    /**
     * Update Version History (WOV) with new event entry.
     * 
     * @param versionNo
     *            Number of version which is updated (mostly the latest, but not
     *            ever!) If null the latest version is updated.
     * @param timestamp
     *            The timestamp which is to write to WOV
     * @param newEventEntry
     *            The event entry XML representation.
     * @throws FedoraSystemException
     *             If Fedora reports an error.
     * @throws WebserverSystemException
     *             In case of an internal error.
     */
    protected void writeEventToWov(
        final String versionNo, final String timestamp,
        final String newEventEntry) throws FedoraSystemException,
        WebserverSystemException {

        /*
         * The event entry is written with the version timestamp. But this value
         * used to be replaced within the later persist() method when all
         * datastreams are written.
         * 
         * FIXME make possible that also to older versions of the resource
         * events could be added
         */

        final StaxParser sp = new StaxParser();

        Map<String, StartElementWithChildElements> updateElementsWOV =
            new HashMap<String, StartElementWithChildElements>();
        // FIXME change first occurence of timestamp in version-history
        updateElementsWOV.put(TripleStoreUtility.PROP_VERSION_TIMESTAMP,
            new StartElementWithChildElements(
                TripleStoreUtility.PROP_VERSION_TIMESTAMP,
                Constants.WOV_NAMESPACE_URI, Constants.WOV_NAMESPACE_PREFIX,
                null, timestamp, null));

        ItemRelsExtUpdateHandler ireuh =
            new ItemRelsExtUpdateHandler(updateElementsWOV, sp);
        ireuh.setPath("/version-history/version/");
        sp.addHandler(ireuh);

        AddNewSubTreesToDatastream addNewSubtreesHandler =
            new AddNewSubTreesToDatastream("/version-history", sp);
        StartElement pointer =
            new StartElement("version", Constants.WOV_NAMESPACE_URI,
                "escidocVersions", null);
        addNewSubtreesHandler.setPointerElement(pointer);
        List<StartElementWithChildElements> elementsToAdd =
            new ArrayList<StartElementWithChildElements>();
        addNewSubtreesHandler.setSubtreeToInsert(elementsToAdd);
        sp.addHandler(addNewSubtreesHandler);

        try {
            sp.parse(getWov().getStream());
            ByteArrayOutputStream newWovStream =
                addNewSubtreesHandler.getOutputStreams();

            String newWovString =
                newWovStream
                    .toString(XmlUtility.CHARACTER_ENCODING).replaceFirst(
                        "(<" + Constants.WOV_NAMESPACE_PREFIX
                            + ":events[^>]*>)", "$1" + newEventEntry);

            setWov(new Datastream(Elements.ELEMENT_WOV_VERSION_HISTORY,
                getId(), newWovString.getBytes(XmlUtility.CHARACTER_ENCODING),
                "text/xml"));
        }
        catch (Exception e) {
            throw new WebserverSystemException(e);
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
    private static Collection<String> expandPropertiesNames(
            final Collection<String> propertiesNames) {

        Collection<String> newPropertiesNames;
        if (propertiesNames != null) {
            newPropertiesNames = propertiesNames;
        }
        else {
            newPropertiesNames = new ArrayList<String>();
        }

        // latest version ------------------------------------------------------
        newPropertiesNames.add(TripleStoreUtility.PROP_LATEST_VERSION_DATE);
        newPropertiesNames.add(TripleStoreUtility.PROP_LATEST_VERSION_NUMBER);
        newPropertiesNames.add(TripleStoreUtility.PROP_LATEST_VERSION_STATUS);
        newPropertiesNames.add(TripleStoreUtility.PROP_LATEST_VERSION_COMMENT);
        newPropertiesNames
            .add(TripleStoreUtility.PROP_LATEST_VERSION_VALID_STATUS);

        // latest release ------------------------------------------------------
        newPropertiesNames.add(TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER);
        newPropertiesNames.add(TripleStoreUtility.PROP_LATEST_RELEASE_DATE);

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
    private static Map<String, String> expandPropertiesNamesMapping(
            final Map<String, String> propertiesNamesMap) {

        Map<String, String> newPropertiesNamesMap;
        if (propertiesNamesMap != null) {
            newPropertiesNamesMap = propertiesNamesMap;
        }
        else {
            newPropertiesNamesMap = new HashMap<String, String>();
        }

        newPropertiesNamesMap.put(TripleStoreUtility.PROP_LATEST_VERSION_DATE,
            PropertyMapKeys.LATEST_VERSION_DATE);
        newPropertiesNamesMap.put(
            TripleStoreUtility.PROP_LATEST_VERSION_NUMBER,
            PropertyMapKeys.LATEST_VERSION_NUMBER);
        newPropertiesNamesMap.put(
            TripleStoreUtility.PROP_LATEST_VERSION_STATUS,
            PropertyMapKeys.LATEST_VERSION_VERSION_STATUS);
        newPropertiesNamesMap.put(
            TripleStoreUtility.PROP_LATEST_VERSION_COMMENT,
            PropertyMapKeys.LATEST_VERSION_VERSION_COMMENT);
        newPropertiesNamesMap.put(
            TripleStoreUtility.PROP_LATEST_VERSION_VALID_STATUS,
            PropertyMapKeys.LATEST_VERSION_VALID_STATUS);

        // TODO this seem a wrong mapping
        newPropertiesNamesMap.put(
            TripleStoreUtility.PROP_LATEST_VERSION_USER_ID,
            PropertyMapKeys.LATEST_VERSION_MODIFIED_BY_ID);
        newPropertiesNamesMap.put(
            TripleStoreUtility.PROP_LATEST_VERSION_USER_TITLE,
            PropertyMapKeys.LATEST_VERSION_MODIFIED_BY_TITLE);

        // map modifier
        newPropertiesNamesMap.put(TripleStoreUtility.PROP_MODIFIED_BY_ID,
            PropertyMapKeys.CURRENT_VERSION_MODIFIED_BY_ID);
        newPropertiesNamesMap.put(TripleStoreUtility.PROP_MODIFIED_BY_TITLE,
            PropertyMapKeys.CURRENT_VERSION_MODIFIED_BY_TITLE);

        // latest release -------------------
        newPropertiesNamesMap.put(
            TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER,
            PropertyMapKeys.LATEST_RELEASE_VERSION_NUMBER);
        newPropertiesNamesMap.put(TripleStoreUtility.PROP_LATEST_RELEASE_DATE,
            PropertyMapKeys.LATEST_RELEASE_VERSION_DATE);

        return newPropertiesNamesMap;
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
    @Override
    public Map<String, String> mapTripleStoreKeys(
        final Map<String, String> tripleStoreMap) {

        Map<String, String> properties = new HashMap<String, String>();

        for (String s : tripleStoreMap.keySet()) {
            String sourceKey = s;
            String value = tripleStoreMap.get(sourceKey);

            if (value != null) {
                String targetKey = getPropertiesNamesMapping().get(sourceKey);

                if (targetKey != null) {
                    properties.put(targetKey, value);

                    // dublicate values for current version if current version
                    // == latest version
                    if (getVersionNumber() == null) {
                        // current version == latest version
                        if (targetKey.startsWith("LATEST_")) {
                            // FIXME:schauen, ob alle andere properties fuer
                            // current und latest haben
                            // consistente namen
                            String currentVersionKey;
                            if (targetKey
                                    .equals(PropertyMapKeys.LATEST_VERSION_VERSION_STATUS)) {
                                currentVersionKey =
                                        PropertyMapKeys.CURRENT_VERSION_STATUS;
                            } else if (targetKey
                                    .equals(PropertyMapKeys.LATEST_VERSION_DATE)) {
                                currentVersionKey =
                                        PropertyMapKeys.CURRENT_VERSION_VERSION_DATE;
                            } else {
                                currentVersionKey =
                                        targetKey.replace("LATEST_", "CURRENT_");
                            }
                            properties.put(currentVersionKey, value);
                        }
                    } else {
                        if (targetKey
                                .equals(PropertyMapKeys.LATEST_VERSION_CONTEXT_ID)
                                || targetKey
                                .equals(PropertyMapKeys.LATEST_VERSION_CONTEXT_TITLE)
                                || targetKey
                                .equals(PropertyMapKeys.LATEST_VERSION_CONTENT_MODEL_ID)
                                || targetKey
                                .equals(PropertyMapKeys.LATEST_VERSION_CONTENT_MODEL_TITLE)) {
                            String currentVersionKey =
                                    targetKey.replace("LATEST_", "CURRENT_");
                            properties.put(currentVersionKey, value);
                        }
                    }
                } else {
                    properties.put(sourceKey, value);

                }
            }
        }

        return properties;
    }

    protected org.fcrepo.server.types.gen.Datastream[] getDatastreamInfos()
        throws WebserverSystemException, FedoraSystemException {

        String versionDate = null;
        if (!isLatestVersion()) {
            versionDate = getVersionDate();
        }
        // initialize datastreams with Fedora datastream information
        org.fcrepo.server.types.gen.Datastream[] datastreamInfos =
            getFedoraUtility().getDatastreamsInformation(getId(), versionDate);
        return datastreamInfos;
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
                if (isLatestVersion()) {
                    ds =
                            new Datastream(name, getId(), null, mimeType, location,
                                    controlGroupValue);
                } else {
                    ds =
                            new Datastream(name, getId(), getVersionDate(),
                                    mimeType, location, controlGroupValue);
                }

                ds.setAlternateIDs(new ArrayList<String>(altIDs));
                ds.setLabel(label);
                this.relsExt = ds;
            }
            // DC
            else if ((name.equals("DC")) && (this.dc == null)) {
                ds =
                        new Datastream("DC", getId(), getVersionDate(),
                                mimeType, location, controlGroupValue);
                ds.setAlternateIDs(new ArrayList<String>(altIDs));
                ds.setLabel(label);
                this.dc = ds;
            }
            // version-history
            if (name.equals(DATASTREAM_WOV)) {
                ds = new Datastream(name, getId(), null, mimeType, location, controlGroupValue);
                ds.setAlternateIDs(new ArrayList<String>(altIDs));
                ds.setLabel(label);
                setWov(ds);
            } else {
                LOG
                        .debug("Datastream "
                                + getId()
                                + '/'
                                + name
                                + " not instanziated in GenericVersionableResource.<init>.");
            }
        }
    }
}
