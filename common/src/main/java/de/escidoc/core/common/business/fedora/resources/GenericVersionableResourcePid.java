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
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.AddNewSubTreesToDatastream;
import de.escidoc.core.common.util.stax.handler.ItemRelsExtUpdateHandler;
import de.escidoc.core.common.util.stax.handler.MultipleExtractor;
import de.escidoc.core.common.util.stax.handler.RelsExtContentRelationsReadHandler;
import de.escidoc.core.common.util.stax.handler.RelsExtContentRelationsReadHandlerForUpdate;
import de.escidoc.core.common.util.stax.handler.foxml.ComponentIdsInItemFoxmlHandler;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;
import org.esidoc.core.utils.io.MimeTypes;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Configurable;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Version PID handling for Generic Versionable Resources.
 *
 * @author Steffen Wagner
 */
@Configurable(preConstruction = true)
public class GenericVersionableResourcePid extends GenericVersionableResource {

    private static final Pattern SPLIT_PATTERN_PREDICATE = Pattern.compile("#");

    private static final Pattern SPLIT_PATTERN_PREDICATE_AND_TARGET = Pattern.compile("###");

    private static final Pattern LATEST_RELEASE_PID_ENTRY =
        Pattern.compile("<[^:]+:" + TripleStoreUtility.PROP_LATEST_RELEASE_PID + "[^>]>[^<]</[^:]+:"
            + TripleStoreUtility.PROP_LATEST_RELEASE_PID + "\\s*>\\s*", Pattern.MULTILINE);

    /**
     * VersionPid HashMap (version no., versionPid).
     */
    private final Map<String, String> versionPids = new HashMap<String, String>();

    /**
     * Generic Versionable Object.
     *
     * @param id The id of the object in the repository.
     * @throws ResourceNotFoundException  Thrown if the resource with the provided objid was not found.
     * @throws TripleStoreSystemException Thrown in case of TripleStore error.
     * @throws WebserverSystemException   Thrown in case of internal error.
     */
    public GenericVersionableResourcePid(final String id) throws TripleStoreSystemException, WebserverSystemException,
        ResourceNotFoundException {
        super(id);
        setPropertiesNames(expandPropertiesNames(getPropertiesNames()),
            expandPropertiesNamesMapping(getPropertiesNamesMapping()));
    }

    /**
     * Set the Persistent Identifier to the Resource (ObjectPID). Update RELS-EXT with object PID. The pid is written to
     * RELS-EXT and concurrently is the last-modification-date of the latest version updated. Even if the object PID
     * isn't related only to the latest version this behavior makes it consistent to all other update methods (where an
     * update influence only the latest version (except assignVersionPid of course)).
     * <p/>
     * XPath for objectPid in the item XML representation is /&lt;resource&gt;/properties/pid
     * <p/>
     * ObjectPid is part of the RELS-EXT (and therefore in the TripleStore). With RELS-EXT is the WOV updated with a new
     * timestamp for the latest version.
     *
     * @param pid The PID which is to assign as object PID.
     * @throws SystemException Thrown in case of internal error.
     */
    @Override
    public void setObjectPid(final String pid) throws SystemException {

        super.setObjectPid(pid);

        final DateTime timestamp = getLastFedoraModificationDate();
        final String newEventEntry = createEventXml(timestamp, "assignObjectPid", "objectPid assigned");

        writeEventToWov(null, timestamp, newEventEntry);
        // FIXME updateRelsExt(pid, isLatestRelease());
    }

    /**
     * Get VersionPid for the current set resource version.
     *
     * @return versionPid for current version.
     * @throws IntegritySystemException Thrown if the data integrity is violated.
     */
    public String getVersionPid() throws IntegritySystemException {
        return getVersionPid(getFullId());
    }

    /**
     * Get versionPid for a defined resource version.
     *
     * @param fullId The id with version suffix to determine the versionPid.
     * @return versionPid
     * @throws IntegritySystemException Thrown if the integrity of WOV data is violated.
     */
    public String getVersionPid(final String fullId) throws IntegritySystemException {

        // TODO throw exact exceptions

        String pid = this.versionPids.get(fullId);

        if (pid == null) {
            if (fullId.equals(getFullId())) {
                pid = getVersionData().get(PropertyMapKeys.CURRENT_VERSION_PID);
            }
            else {
                // FIXME
                final DateTime latestReleaseDate =
                    new DateTime(getVersionElementData(PropertyMapKeys.LATEST_RELEASE_VERSION_DATE), DateTimeZone.UTC);
                // get the timestamp of this version
                // get the RELSE-EXT of the version and parse it for the
                // versionPid
                final Datastream relsExt;
                try {
                    relsExt = getRelsExt(latestReleaseDate);
                }
                catch (final FedoraSystemException e) {
                    throw new IntegritySystemException(e);
                }
                catch (final StreamNotFoundException e) {
                    throw new IntegritySystemException(e);
                }

                // parse RELS-EXT version for versionPid
                final StaxParser sp = new StaxParser();
                final ComponentIdsInItemFoxmlHandler cih = new ComponentIdsInItemFoxmlHandler(sp);
                sp.addHandler(cih);
                try {
                    sp.parse(relsExt.getStream());
                }
                catch (final Exception e) {
                    throw new IntegritySystemException(e);
                }
                pid = cih.getVersionPid();
                if (!validPidStructure(pid)) {
                    pid = null;
                }
            }

            if (validPidStructure(pid)) {
                this.versionPids.put(fullId, pid);
            }
        }

        return pid;
    }

    /**
     * Set the versionPID.
     *
     * @param pid The to assign PID.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.XmlParserSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    public void setVersionPid(final String pid) throws TripleStoreSystemException, IntegritySystemException,
        FedoraSystemException, WebserverSystemException, EncodingSystemException, XmlParserSystemException {

        final DateTime timestamp = getLastFedoraModificationDate();

        setLastModificationDate(timestamp);

        final String newEventEntry = createEventXml(timestamp, "assignVersionPid", "versionPid assigned");

        writeEventToWov(null, timestamp, newEventEntry);

        updatePidToWov(pid, timestamp);
        setPidToRelsExt(pid);

        versionPids.put(getFullId(), pid);
    }

    /**
     * Set the latest release pid. It's insert to the RELS-EXT.
     * <p/>
     * Precondition: The method checks not if the version is released! This check is part of the method caller.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.XmlParserSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    public void setLatestReleasePid() throws TripleStoreSystemException, IntegritySystemException,
        WebserverSystemException, FedoraSystemException, XmlParserSystemException {

        // Currently we can not trust the internal map because the status is
        // changes out side of the resource.
        String latestReleasedVersion = getProperty(PropertyMapKeys.LATEST_RELEASE_VERSION_NUMBER);
        if (latestReleasedVersion == null) {
            // FIXME emergency solution because the version status is not known
            // resource internal
            latestReleasedVersion = getVersionId();
        }

        // if (latestReleasedVersion != null) {

        // compare the versions (only set latest-release pid if the
        // latest-released version is older thean the current.
        final int lrvn = Integer.valueOf(latestReleasedVersion);
        final int cvn = Integer.valueOf(getVersionId());
        String pid = null;

        if (lrvn == cvn) {
            pid = getVersionPid();
        }
        else if (lrvn <= cvn) {
            pid = getVersionPid(getId() + VERSION_NUMBER_SEPARATOR + lrvn);
        }
        setLatestReleasePid(pid);
        setLatestReleasePid(pid);
        // }
    }

    /**
     * Set the latestReleasePid.
     *
     * @param pid The to PID of the latest released version.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.XmlParserSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     */
    public void setLatestReleasePid(final String pid) throws TripleStoreSystemException, FedoraSystemException,
        XmlParserSystemException, WebserverSystemException {

        byte[] relsExtS = null;

        if (getLatestReleaseVersionNumber() == null) {
            relsExtS = createLatestReleasePid(pid);
        }
        else if (getLatestReleasePid() != null && pid.length() != 0) {
            // relsExtS = updateVersionPid(pid);
            relsExtS = updateLatestReleasePid(pid);
        }

        // we could store the new collected pid
        // versionPids.put(getFullId(), pid);
        if (relsExtS != null) {
            setRelsExt(relsExtS);
        }
        // }
        // else {
        // unsetRelsExt();
        // }
    }

    /**
     * Check if Item Version has Persistent Identifier (versionPID).
     * <p/>
     * VersionPid has in the XML representation of item the XPath /item/properties/version/pid
     *
     * @return true if Item Version has PID false otherwise.
     * @throws WebserverSystemException Thrown in case of internal operation error. This exception encapsulates the
     *                                  Parser exceptions.
     */
    public boolean hasVersionPid() throws WebserverSystemException {

        final String pid;
        try {
            pid = getVersionPid();
        }
        catch (final IntegritySystemException e) {
            throw new WebserverSystemException(e);
        }
        return validPidStructure(pid);
    }

    /**
     * Get versionPID of the latest released version.
     * <p/>
     * FIXME This method should not be part of the GenericVersionableResource because release is not a feature of this
     * class.
     *
     * @return version PID of the latest release.
     * @throws TripleStoreSystemException Thrown if TripleStore request failed.
     * @throws WebserverSystemException   Thrown if calling instance of TripleStore connection failed.
     */
    @Deprecated
    public String getLatestReleasePid() throws TripleStoreSystemException, WebserverSystemException {

        final String pid = getProperty(PropertyMapKeys.LATEST_RELEASE_PID);
        if (validPidStructure(pid)) {
            return pid;
        }
        return null;
    }

    /**
     * Update RELS-EXT with version PID.
     *
     * @param pid Persistent Identifier
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.XmlParserSystemException
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    private void setPidToRelsExt(final String pid) throws TripleStoreSystemException, EncodingSystemException,
        IntegritySystemException, FedoraSystemException, XmlParserSystemException, WebserverSystemException {

        /*
         * if (version == latest release) if (latest-release.pid == null) create
         * new latest-release.pid element else if (latest-release.pid != null)
         * update latest-release.pid element else if (pid == null) remove
         * latest-release.pid element fi
         */

        // perpare parser chain
        // String ltstRlsNo = getLatestReleaseVersionNumber();
        // if ((ltstRlsNo != null) && (getVersionId().equals(ltstRlsNo))) {
        byte[] relsExtS = null;

        if (getVersionPid() == null) {
            relsExtS = createVersionPid(pid);
        }
        else if (getVersionPid() != null && pid.length() != 0) {
            relsExtS = updateVersionPid(pid);
        }
        else if (pid == null || pid.length() == 0) {
            relsExtS = deleteLatestReleasePid();
        }

        // now parse RELS-EXT

        if (relsExtS != null) {
            setRelsExt(relsExtS);
        }
    }

    /**
     * Update Version History (WOV) with PID.
     *
     * @param pid       persistent identifier
     * @param timestamp The timestamp of the assignment.
     * @throws WebserverSystemException   In case of an internal error.
     * @throws FedoraSystemException      If Fedora reports an error.
     * @throws TripleStoreSystemException If the triple store request failed.
     */
    public void updatePidToWov(final String pid, final DateTime timestamp) throws WebserverSystemException {

        // TODO add comment "objectPid added" to the version
        final StartElementWithChildElements pidElement =
            new StartElementWithChildElements(Elements.ELEMENT_PID, Constants.WOV_NAMESPACE_URI,
                Constants.WOV_NAMESPACE_PREFIX, null, pid, null);

        pidElement.addAttribute(new Attribute("timestamp", null, null, timestamp.toString()));
        pidElement.addAttribute(new Attribute("user", null, null, UserContext.getId()));

        final List<StartElementWithChildElements> elementsToAdd = new ArrayList<StartElementWithChildElements>();
        elementsToAdd.add(pidElement);

        final StaxParser sp = new StaxParser();
        final AddNewSubTreesToDatastream addNewSubtreesHandler =
            new AddNewSubTreesToDatastream('/' + Elements.ELEMENT_WOV_VERSION_HISTORY, sp);

        final StartElement pointer =
            new StartElement("version", Constants.WOV_NAMESPACE_URI, Constants.WOV_NAMESPACE_PREFIX, null);

        pointer.addAttribute(new Attribute("objid", null, null, getFullId()));

        addNewSubtreesHandler.setPointerElement(pointer);
        addNewSubtreesHandler.setSubtreeToInsert(elementsToAdd);
        sp.addHandler(addNewSubtreesHandler);

        try {
            sp.parse(new ByteArrayInputStream(getWov().getStream()));
            final ByteArrayOutputStream wovExtNew = addNewSubtreesHandler.getOutputStreams();
            final byte[] wovNewBytes = wovExtNew.toByteArray();
            setWov(new Datastream(DATASTREAM_WOV, getId(), wovNewBytes, MimeTypes.TEXT_XML));
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e);
        }
    }

    /**
     * Create a new version PID entry in RELS-EXT.
     *
     * @param pid The pid.
     * @return The new RELS-EXT.
     * @throws XmlParserSystemException   Thrown if parsing of RELS_ET fails.
     * @throws WebserverSystemException   In case of an internal error.
     * @throws TripleStoreSystemException If the triple store request failed.
     */
    private byte[] createVersionPid(final String pid) throws XmlParserSystemException, TripleStoreSystemException,
        WebserverSystemException {

        final StaxParser sp = new StaxParser();

        // create new RELS-EXT element
        final AddNewSubTreesToDatastream addNewSubtreesHandler = new AddNewSubTreesToDatastream("/RDF", sp);
        final StartElement pointer =
            new StartElement("Description", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf", null);
        addNewSubtreesHandler.setPointerElement(pointer);

        final List<StartElementWithChildElements> elements = new ArrayList<StartElementWithChildElements>();
        final StartElementWithChildElements versionPidElement = new StartElementWithChildElements();
        versionPidElement.setLocalName(Elements.ELEMENT_PID);
        versionPidElement.setPrefix(Constants.VERSION_NS_PREFIX);
        versionPidElement.setNamespace(Constants.VERSION_NS_URI);
        versionPidElement.setElementText(pid);

        elements.add(versionPidElement);

        // TODO update of the latest-release-pid even if this is wrong within
        // the inheritage structure (because release is no feature of the
        // GenericVersionableResource. But all other need more code refactoring)
        final String latestReleasedVersion = getProperty(PropertyMapKeys.LATEST_RELEASE_VERSION_NUMBER);

        byte[] relsExt = null;
        if (latestReleasedVersion != null) {

            // compare the versions (only set latest-release pid if the
            // latest-released version is older thean the current.
            final int lrvn = Integer.valueOf(latestReleasedVersion);
            final int cvn = Integer.valueOf(getVersionId());

            if (lrvn <= cvn) {
                // get the pid of the latest-release
                final String latestReleasePid = getLatestReleasePid();
                if (latestReleasePid != null) {
                    // update release pid
                    relsExt = updateLatestReleasePid(latestReleasePid);
                }
                else {
                    // add release pid
                    final StartElementWithChildElements latestReleasePidElement = new StartElementWithChildElements();
                    latestReleasePidElement.setLocalName(Elements.ELEMENT_PID);
                    latestReleasePidElement.setPrefix(Constants.RELEASE_NS_PREFIX);
                    latestReleasePidElement.setNamespace(Constants.RELEASE_NS_URI);
                    elements.add(latestReleasePidElement);
                }
            }
        }
        // excure to latest release pid finished
        addNewSubtreesHandler.setSubtreeToInsert(elements);
        sp.addHandler(addNewSubtreesHandler);

        try {
            if (relsExt != null) {
                sp.parse(new ByteArrayInputStream(relsExt));
            }
            else {
                sp.parse(new ByteArrayInputStream(getRelsExt().getStream()));
            }
        }
        catch (final XMLStreamException e) {
            throw new XmlParserSystemException(e);
        }
        catch (final Exception e) {
            throw new XmlParserSystemException("Unexpected Exception " + e);
        }

        return addNewSubtreesHandler.getOutputStreams().toByteArray();
    }

    /**
     * Create a new LatestesRelease PID entry into RELS-EXT.
     *
     * @param pid The pid.
     * @return The new RELS-EXT.
     * @throws XmlParserSystemException   Thrown if parsing of RELS_ET fails.
     * @throws WebserverSystemException   In case of an internal error.
     * @throws TripleStoreSystemException If the triple store request failed.
     */
    private byte[] createLatestReleasePid(final String pid) throws XmlParserSystemException {

        final StaxParser sp = new StaxParser();

        // create new RELS-EXT element
        final AddNewSubTreesToDatastream addNewSubtreesHandler = new AddNewSubTreesToDatastream("/RDF", sp);
        final StartElement pointer =
            new StartElement("Description", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf", null);
        addNewSubtreesHandler.setPointerElement(pointer);

        final List<StartElementWithChildElements> elements = new ArrayList<StartElementWithChildElements>();
        final StartElementWithChildElements versionPidElement = new StartElementWithChildElements();
        versionPidElement.setLocalName(Elements.ELEMENT_PID);
        versionPidElement.setPrefix(Constants.RELEASE_NS_PREFIX);
        versionPidElement.setNamespace(Constants.RELEASE_NS_URI);
        versionPidElement.setElementText(pid);

        elements.add(versionPidElement);

        addNewSubtreesHandler.setSubtreeToInsert(elements);
        sp.addHandler(addNewSubtreesHandler);

        try {
            sp.parse(getRelsExt().getStream());
        }
        catch (final XMLStreamException e) {
            throw new XmlParserSystemException(e);
        }
        catch (final Exception e) {
            throw new XmlParserSystemException("Unexpected Exception " + e);
        }

        return addNewSubtreesHandler.getOutputStreams().toByteArray();
    }

    /**
     * Update versionPid entry within RELS-EXT.
     *
     * @param pid The PID.
     * @return The updated RELS-EXT
     * @throws XmlParserSystemException Thrown if parsing of RELS_ET fails.
     */
    private byte[] updateVersionPid(final String pid) throws XmlParserSystemException {

        final StaxParser sp = new StaxParser();

        final Map<String, StartElementWithChildElements> elementsToUpdate =
            new HashMap<String, StartElementWithChildElements>();

        // update //properties/version/pid
        elementsToUpdate.put(Elements.ELEMENT_PID, new StartElementWithChildElements(Elements.ELEMENT_PID,
            Constants.VERSION_NS_URI, Constants.VERSION_NS_PREFIX, null, pid, null));
        final ItemRelsExtUpdateHandler ireuh = new ItemRelsExtUpdateHandler(elementsToUpdate, sp);

        final HashMap<String, String> extractPathes = new HashMap<String, String>();
        extractPathes.put("/RDF", null);
        final MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
        sp.addHandler(ireuh);
        sp.addHandler(me);

        try {
            sp.parse(getRelsExt().getStream());
        }
        catch (final XMLStreamException e) {
            throw new XmlParserSystemException(e);
        }
        catch (final Exception e) {
            throw new XmlParserSystemException("Unexpected Exception " + e);
        }

        final Map<String, Object> streams = me.getOutputStreams();

        return ((ByteArrayOutputStream) streams.get("RDF")).toByteArray();
    }

    /**
     * Update release/pid entry within RELS-EXT (latest-release).
     *
     * @param pid The PID.
     * @return The updated RELS-EXT
     * @throws XmlParserSystemException Thrown if parsing of RELS_ET fails.
     */
    private byte[] updateLatestReleasePid(final String pid) throws XmlParserSystemException {

        final StaxParser sp = new StaxParser();

        final Map<String, StartElementWithChildElements> elementsToUpdate =
            new HashMap<String, StartElementWithChildElements>();

        // update //properties/version/pid
        elementsToUpdate.put(Elements.ELEMENT_PID, new StartElementWithChildElements(Elements.ELEMENT_PID,
            Constants.RELEASE_NS_URI, Constants.RELEASE_NS_PREFIX, null, pid, null));
        final ItemRelsExtUpdateHandler ireuh = new ItemRelsExtUpdateHandler(elementsToUpdate, sp);

        final HashMap<String, String> extractPathes = new HashMap<String, String>();
        extractPathes.put("/RDF", null);
        final MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
        sp.addHandler(ireuh);
        sp.addHandler(me);

        try {
            sp.parse(new ByteArrayInputStream(getRelsExt().getStream()));
        }
        catch (final XMLStreamException e) {
            throw new XmlParserSystemException(e);
        }
        catch (final Exception e) {
            throw new XmlParserSystemException("Unexpected Exception " + e);
        }

        final Map<String, Object> streams = me.getOutputStreams();

        return ((ByteArrayOutputStream) streams.get("RDF")).toByteArray();
    }

    /**
     * Delete latest-release.pid entry from RELS-EXT.
     * <p/>
     * TODO release is not a feature of this class. Therefore is the method marked as deprecated.
     *
     * @return The updated RELS-EXT
     * @throws IntegritySystemException Thrown if the data integrity is violated.
     * @throws EncodingSystemException  Thrown if data encoding failed.
     * @throws FedoraSystemException    Thrown if Fedora requests fail.
     * @throws WebserverSystemException Thrown in case of internal failure.
     */
    @Deprecated
    private byte[] deleteLatestReleasePid() throws IntegritySystemException, EncodingSystemException,
        FedoraSystemException {

        // FIXME use a real XML parser to delete entries !
        String relsExt;
        try {
            relsExt = getRelsExt().toString(XmlUtility.CHARACTER_ENCODING);
        }
        catch (final StreamNotFoundException e) {
            throw new IntegritySystemException(e);
        }

        final Matcher m = LATEST_RELEASE_PID_ENTRY.matcher(relsExt);
        byte[] relsExtS = null;
        if (m.find()) {
            relsExt = relsExt.replaceAll(m.group(), "");
            try {
                relsExtS = relsExt.getBytes(XmlUtility.CHARACTER_ENCODING);
            }
            catch (final UnsupportedEncodingException e) {
                throw new IntegritySystemException(e);
            }
        }

        return relsExtS;
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

        newPropertiesNames.add(TripleStoreUtility.PROP_LATEST_VERSION_PID);
        newPropertiesNames.add(TripleStoreUtility.PROP_LATEST_RELEASE_PID);

        return newPropertiesNames;
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

        newPropertiesNamesMap.put(TripleStoreUtility.PROP_LATEST_VERSION_PID, PropertyMapKeys.LATEST_VERSION_PID);
        // FIXME release is a methd of Item/Container so this is to move higher
        // within the hirarchie
        newPropertiesNamesMap.put(TripleStoreUtility.PROP_LATEST_RELEASE_PID, PropertyMapKeys.LATEST_RELEASE_PID);

        return newPropertiesNamesMap;
    }

    /**
     * @return Vector with HashMaps of relations.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.XmlParserSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    public List<Map<String, String>> getRelations() throws FedoraSystemException, IntegritySystemException,
        XmlParserSystemException, WebserverSystemException {

        final Datastream relsExt;
        try {
            relsExt = getRelsExt();
        }
        catch (final StreamNotFoundException e1) {
            throw new IntegritySystemException("Stream not found.", e1);
        }
        final byte[] relsExtContent = relsExt.getStream();

        final StaxParser sp = new StaxParser();
        final ByteArrayInputStream relsExtInputStream = new ByteArrayInputStream(relsExtContent);

        final RelsExtContentRelationsReadHandler reHandler = new RelsExtContentRelationsReadHandler(sp);
        sp.addHandler(reHandler);
        try {
            sp.parse(relsExtInputStream);
        }
        catch (final WebserverSystemException e) {
            throw e;
        }
        catch (final Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        return reHandler.getRelations();
    }

    /**
     * Set content relations in the provided resource.
     *
     * @param sp                A StaxParser instance. (TODO ?FRS)
     * @param relationsToUpdate A list of relations.
     * @throws IntegritySystemException If the integrity of the repository is violated.
     * @throws XmlParserSystemException If parsing of xml data fails.
     * @throws WebserverSystemException In case of an internal error.
     * @throws FedoraSystemException    If the Fedora reports an error
     */
    public void setContentRelations(final StaxParser sp, final Collection<String> relationsToUpdate)
        throws XmlParserSystemException, WebserverSystemException, IntegritySystemException, FedoraSystemException {

        final Datastream relsExt;
        try {
            relsExt = getRelsExt();
        }
        catch (final StreamNotFoundException e1) {
            throw new IntegritySystemException("Stream not found.", e1);
        }
        final ByteArrayInputStream relsExtInputStream = new ByteArrayInputStream(relsExt.getStream());

        final RelsExtContentRelationsReadHandlerForUpdate relsExtHandler =
            new RelsExtContentRelationsReadHandlerForUpdate(sp);

        sp.addHandler(relsExtHandler);

        try {
            sp.parse(relsExtInputStream);
        }
        catch (final XMLStreamException e) {
            throw new XmlParserSystemException(e.getMessage(), e);
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e);
        }
        sp.clearHandlerChain();
        final List<String> existRelations = relsExtHandler.getRelationsStrings();

        final Collection<String> existRelationsCopy = new ArrayList<String>();
        existRelationsCopy.addAll(existRelations);
        existRelations.removeAll(relationsToUpdate);
        relationsToUpdate.removeAll(existRelationsCopy);
        List<StartElementWithChildElements> elementsToAdd = null;

        // prepare update relations
        if (!relationsToUpdate.isEmpty()) {
            elementsToAdd = new ArrayList<StartElementWithChildElements>();
            for (final String relation : relationsToUpdate) {
                final String[] predicateAndTarget = SPLIT_PATTERN_PREDICATE_AND_TARGET.split(relation);
                final String[] predicate = SPLIT_PATTERN_PREDICATE.split(predicateAndTarget[0]);
                final StartElementWithChildElements newContentRelationElement = new StartElementWithChildElements();
                newContentRelationElement.setLocalName(predicate[1]);
                newContentRelationElement.setPrefix(Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT);
                newContentRelationElement.setNamespace(predicate[0]);
                final Attribute resource =
                    new Attribute("resource", Constants.RDF_NAMESPACE_URI, Constants.RDF_NAMESPACE_PREFIX,
                        Constants.IDENTIFIER_PREFIX + predicateAndTarget[1]);
                newContentRelationElement.addAttribute(resource);
                // newComponentIdElement.setElementText(componentId);
                newContentRelationElement.setChildrenElements(null);
                elementsToAdd.add(newContentRelationElement);
            }
        }
        // prepare add/remove
        Map<String, List<StartElementWithChildElements>> toRemove = null;
        if (!existRelations.isEmpty()) {
            final Iterator<String> iterator = existRelations.iterator();
            final HashMap<String, List<StartElementWithChildElements>> predicateValuesVectorAssignment =
                new HashMap<String, List<StartElementWithChildElements>>();

            while (iterator.hasNext()) {
                final String relation = iterator.next();
                final String[] predicateAndTarget = SPLIT_PATTERN_PREDICATE_AND_TARGET.split(relation);
                final String[] predicate = SPLIT_PATTERN_PREDICATE.split(predicateAndTarget[0]);

                final StartElementWithChildElements newContentRelationElement = new StartElementWithChildElements();
                newContentRelationElement.setLocalName(predicate[1]);
                newContentRelationElement.setPrefix(Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT);
                newContentRelationElement.setNamespace(predicate[0] + '/');
                final Attribute resource =
                    new Attribute("resource", Constants.RDF_NAMESPACE_URI, Constants.RDF_NAMESPACE_PREFIX,
                        Constants.IDENTIFIER_PREFIX + predicateAndTarget[1]);
                newContentRelationElement.addAttribute(resource);
                newContentRelationElement.setChildrenElements(null);
                if (predicateValuesVectorAssignment.containsKey(predicate[1])) {
                    final List<StartElementWithChildElements> vector =
                        predicateValuesVectorAssignment.get(predicate[1]);
                    vector.add(newContentRelationElement);
                }
                else {
                    final List<StartElementWithChildElements> vector = new ArrayList<StartElementWithChildElements>();
                    vector.add(newContentRelationElement);
                    predicateValuesVectorAssignment.put(predicate[1], vector);
                }
            }

            // remove
            toRemove = new TreeMap<String, List<StartElementWithChildElements>>();

            for (final Entry<String, List<StartElementWithChildElements>> e : predicateValuesVectorAssignment
                .entrySet()) {
                toRemove.put("/RDF/Description/" + e.getKey(), e.getValue());
            }
        }

        // Update RELS-EXT
        if (toRemove != null || elementsToAdd != null) {
            final byte[] newRelsExtBytes =
                Utility.updateRelsExt(elementsToAdd, toRemove, relsExt.getStream(), this, null);
            try {
                setRelsExt(new String(newRelsExtBytes, XmlUtility.CHARACTER_ENCODING));
            }
            catch (final EncodingSystemException e) {
                throw new IntegritySystemException(e);
            }
            catch (final UnsupportedEncodingException e) {
                throw new WebserverSystemException(e);
            }
        }

    }

}
