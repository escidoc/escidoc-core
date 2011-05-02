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

/**
 *
 */
package de.escidoc.core.common.business.fedora.datastream;

import de.escidoc.core.common.business.fedora.Constants;
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.FileSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.common.util.xml.XmlUtility;
import org.fcrepo.server.types.gen.DatastreamControlGroup;
import org.fcrepo.server.types.gen.MIMETypedStream;
import org.joda.time.ReadableDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Representation of a datastream managed in Fedora Digital Repository System.
 * <p/>
 * Note: The Set <code>alternateIDs</code> is a not synchronized {@link HashSet HashSet}.
 *
 * @author Frank Schwichtenberg
 */
@Configurable
public class Datastream {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Datastream.class);

    public static final String DC_DATASTREAM = "DC";

    public static final String RELS_EXT_DATASTREAM = "RELS-EXT";

    public static final String RELS_EXT_DATASTREAM_LABEL = "RELS_EXT DATASTREAM";

    public static final String MIME_TYPE_TEXT_XML = "text/xml";

    public static final String CONTROL_GROUP_EXTERNAL_REFERENCE = "E";

    public static final String CONTROL_GROUP_REDIRECT = "R";

    public static final String CONTROL_GROUP_INTERNAL_XML = "X";

    public static final String CONTROL_GROUP_MANAGED = "M";

    public static final String METADATA_ALTERNATE_ID = "metadata";

    private final String name;

    private final String parentId;

    private String timestamp;

    private List<String> alternateIDs = new ArrayList<String>();

    private String label;

    private String mimeType;

    private byte[] theStream;

    private String md5Hash;

    private String controlGroupValue = "X";

    private Map<String, String> properties;

    private String location;

    private String checksumMethod;

    private String checksum;

    private static final boolean VERSIONABLE = true;

    @Autowired
    @Qualifier("escidoc.core.business.FedoraUtility")
    private FedoraUtility fedoraUtility;

    @Autowired
    @Qualifier("business.Utility")
    private Utility utility;

    /**
     * Indicating the URL should not be sent when storing this in Fedora.
     */
    private boolean contentUnchanged;

    /**
     * Constructs the Datastream identified by name and parentId. The version of the stream identified by timestamp is
     * retrieved from Fedora. If timestamp is <code>null</code> the latest version is retrieved.
     *
     * @param name      The name of the datastream.
     * @param parentId  The unique id the fedora object to which the datastream belongs.
     * @param timestamp A timestamp specify the version of the stream.
     * @throws StreamNotFoundException If there is no datastream identified by name and parentId in Fedora.
     * @throws FedoraSystemException   Thrown in case of an internal system error caused by failed fedora access.
     */
    public Datastream(final String name, final String parentId, final String timestamp) throws FedoraSystemException,
        StreamNotFoundException {
        this.name = name;
        this.parentId = parentId;
        this.timestamp = timestamp;
    }

    /**
     * Constructs the Datastream identified by name and parentId. The version of the stream identified by timestamp is
     * retrieved from Fedora. If timestamp is <code>null</code> the latest version is retrieved.
     *
     * @param name              The name of the datastream.
     * @param parentId          The unique id the fedora object to which the datastream belongs.
     * @param timestamp         A timestamp specify the version of the stream.
     * @param mimeType          MIME Type of the data stream.
     * @param location          TODO
     * @param controlGroupValue The Fedora Control Group type.
     */
    public Datastream(final String name, final String parentId, final String mimeType, final String location,
        final String controlGroupValue, final ReadableDateTime timestamp) {
        if (timestamp != null) {
            final String tsFormat = de.escidoc.core.common.business.Constants.TIMESTAMP_FORMAT;
            timestamp.toString(tsFormat);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(StringUtility.format("Datastream()", name, parentId, mimeType, location, controlGroupValue));
        }
        this.name = name;
        this.parentId = parentId;
        this.controlGroupValue = controlGroupValue;
        this.mimeType = mimeType;
        this.location = location;

    }

    /**
     * Constructs the Datastream identified by name and parentId. The version of the stream identified by timestamp is
     * retrieved from Fedora. If timestamp is <code>null</code> the latest version is retrieved.
     *
     * @param name              The name of the datastream.
     * @param parentId          The unique id the fedora object to which the datastream belongs.
     * @param timestamp         A timestamp specify the version of the stream.
     * @param mimeType          MIME Type of the data stream.
     * @param location          TODO
     * @param controlGroupValue The Fedora Control Group type.
     */
    public Datastream(final String name, final String parentId, final String timestamp, final String mimeType,
        final String location, final String controlGroupValue) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(StringUtility.format("Datastream()", name, parentId, timestamp, mimeType, location,
                controlGroupValue));
        }

        this.name = name;
        this.parentId = parentId;
        this.timestamp = timestamp;
        this.controlGroupValue = controlGroupValue;
        this.mimeType = mimeType;
        this.location = location;

    }

    /**
     * Constructs the Datastream identified by name and parentId. The version of the stream identified by timestamp is
     * retrieved from Fedora. If timestamp is <code>null</code> the latest version is retrieved.
     *
     * @param name              The name of the datastream.
     * @param parentId          The unique id the fedora object to which the datastream belongs.
     * @param timestamp         A timestamp specify the version of the stream.
     * @param mimeType          MIME Type of the data stream.
     * @param location          TODO
     * @param controlGroupValue The Fedora Control Group type.
     * @param checksumMethod    The method to compute the streams checksum.
     * @param checksum          The streams checksum.
     */
    public Datastream(final String name, final String parentId, final String timestamp, final String mimeType,
        final String location, final String controlGroupValue, final String checksumMethod, final String checksum) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(StringUtility.format("Datastream()", name, parentId, timestamp, mimeType, location,
                controlGroupValue));
        }

        this.name = name;
        this.parentId = parentId;
        this.timestamp = timestamp;
        this.controlGroupValue = controlGroupValue;
        this.mimeType = mimeType;
        this.location = location;
        if (checksumMethod != null && !"disabled".equalsIgnoreCase(checksumMethod)) {
            this.checksumMethod = checksumMethod;
            this.checksum = checksum;
        }

    }

    /**
     * Constructs a Datastream of the given parameters. The stream may be concurrent with the datastream saved in Fedora
     * or not. Maybe there is no such datastream in Fedora.
     *
     * @param name      The name of this datastream
     * @param parentId  The ID of the parent of this datastream.
     * @param theStream The string representing the content of this datastream.
     * @param mimeType  TODO
     */
    public Datastream(final String name, final String parentId, final byte[] theStream, final String mimeType) {
        this.name = name;
        this.parentId = parentId;
        // FIXME theStream must not be null
        this.theStream = theStream;
        if (theStream == null) {
            LOGGER.warn("Empty datastream initialized. "
                + StringUtility.format("Datastream()", name, parentId, mimeType));
            this.theStream = new byte[0];
        }
        this.mimeType = mimeType;

    }

    /**
     * Constructs a Datastream of the given parameters. The stream may be concurrent with the datastream saved in Fedora
     * or not. Maybe there is no such datastream in Fedora.
     *
     * @param name     The name of this datastream
     * @param parentId The ID of the parent of this datastream.
     * @param url      The URL of the content.
     * @param storage  TODO
     * @param mimeType TODO
     */
    public Datastream(final String name, final String parentId, final String url, final String storage,
        final String mimeType) {
        this.name = name;
        this.parentId = parentId;
        // FIXME location must not be null
        this.location = url;
        if (url == null) {
            LOGGER.warn("Empty datastream initialized. url = null "
                + StringUtility.format("Datastream()", name, parentId, storage));
        }
        this.theStream = null;
        if (Constants.STORAGE_EXTERNAL_MANAGED.equalsIgnoreCase(storage)) {
            this.controlGroupValue = CONTROL_GROUP_EXTERNAL_REFERENCE;
        }
        else if (Constants.STORAGE_EXTERNAL_URL.equalsIgnoreCase(storage)) {
            this.controlGroupValue = CONTROL_GROUP_REDIRECT;
        }
        else if (Constants.STORAGE_INTERNAL_MANAGED.equalsIgnoreCase(storage)) {
            this.controlGroupValue = CONTROL_GROUP_MANAGED;
        }
        this.mimeType = mimeType;
    }

    /**
     * Constructs a Datastream of the given parameters. The stream may be concurrent with the datastream saved in Fedora
     * or not. Maybe there is no such datastream in Fedora.
     *
     * @param name       The name of this datastream
     * @param parentId   The ID of the parent of this datastream.
     * @param theStream  The string representing the content of this datastream.
     * @param mimeType   The MIME type of this datastream.
     * @param properties Map with properties of this datastream
     */
    public Datastream(final String name, final String parentId, final byte[] theStream, final String mimeType,
        final Map<String, String> properties) {
        this.name = name;
        this.parentId = parentId;
        // FIXME theStream must not be null
        this.theStream = theStream;
        this.mimeType = mimeType;
        this.properties = properties;
    }

    /**
     * Retrieves a datastream identified by the name and the parentId from Fedora.
     *
     * @throws StreamNotFoundException If the datastream could not be retrieved from Fedora.
     * @throws FedoraSystemException   Thrown in case of an internal system error caused by failed fedora access.
     */
    @PostConstruct
    protected void init() throws StreamNotFoundException, FedoraSystemException {

        final org.fcrepo.server.types.gen.Datastream fedoraDatastream;
        try {
            fedoraDatastream = this.fedoraUtility.getDatastreamInformation(this.parentId, this.name, this.timestamp);
        }
        catch (final FedoraSystemException e1) {
            throw new StreamNotFoundException("Fedora datastream '" + this.name + "' not found.", e1);
        }

        if (fedoraDatastream == null) {
            throw new StreamNotFoundException("Datastream informations are 'null' after retrieving "
                + "datastream from Fedora without exception.");
        }

        this.label = fedoraDatastream.getLabel();
        final DatastreamControlGroup controlGroup = fedoraDatastream.getControlGroup();
        this.controlGroupValue = controlGroup.getValue();
        final String[] altIDs = fedoraDatastream.getAltIDs();
        this.alternateIDs.addAll(Arrays.asList(altIDs));

        this.mimeType = fedoraDatastream.getMIMEType();

        this.location = fedoraDatastream.getLocation();

        final String checksumMethodTmp = fedoraDatastream.getChecksumType();
        if (!"disabled".equalsIgnoreCase(checksumMethodTmp)) {
            this.checksumMethod = checksumMethodTmp;

            this.checksum = fedoraDatastream.getChecksum();
        }

        this.md5Hash = null;
    }

    /**
     * Merges the datastream identified by the name and the objectId to Fedora. The datastream must already exists in
     * Fedora. Otherwise call <code>persist()</code>.
     *
     * @return timestamp of datastream (last-modification-date)
     * @throws FedoraSystemException    Thrown if writing to Fedora failed.
     * @throws WebserverSystemException Thrown in case of internal failure (get configuration)
     */
    public String merge() throws FedoraSystemException, WebserverSystemException {

        if (this.getStream() == null && this.location != null) {
            String loc = this.location;
            try {
                // FIXME this location/href is logic of Item!
                if (this.contentUnchanged
                    || loc.startsWith("/ir/item/" + getParentId())
                    || loc.startsWith(EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_BASEURL)
                        + "/ir/item/" + getParentId())) {
                    // TODO assuming unchanged href
                    loc = null;
                }
                else if (loc.startsWith("/")) {
                    // assuming relative URL
                    loc = EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_BASEURL) + loc;
                }
            }
            catch (final IOException e) {
                throw new WebserverSystemException(e);
            }
            try {
                this.fedoraUtility.modifyDatastream(this.parentId, this.name, this.label, this.mimeType,
                    this.alternateIDs.toArray(new String[alternateIDs.size()]), loc, false);
            }
            catch (final FedoraSystemException e) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Error on modifing datastream.");
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Error on modifing datastream.", e);
                }
                this.fedoraUtility.setDatastreamState(this.parentId, this.name, "A");
                this.timestamp =
                    this.fedoraUtility.modifyDatastream(this.parentId, this.name, this.label, this.mimeType,
                        this.alternateIDs.toArray(new String[alternateIDs.size()]), loc, false);
            }
        }
        else if (this.getStream() != null) {
            if ("X".equals(this.getControlGroup())) {
                try {
                    this.timestamp =
                        this.fedoraUtility.modifyDatastream(this.parentId, this.name, this.label, this.mimeType,
                            this.alternateIDs.toArray(new String[alternateIDs.size()]), this.getStream(), false);
                }
                catch (final FedoraSystemException e) {
                    LOGGER.debug("Error on modifing datastream.", e);
                    this.fedoraUtility.setDatastreamState(this.parentId, this.name, "A");
                    this.timestamp =
                        this.fedoraUtility.modifyDatastream(this.parentId, this.name, this.label, this.mimeType,
                            this.alternateIDs.toArray(new String[alternateIDs.size()]), this.getStream(), false);
                }
            }
            else if (this.getControlGroup().equals(CONTROL_GROUP_MANAGED)) {
                String tempURI = null;
                try {
                    try {
                        tempURI = this.utility.upload(this.getStream(), this.parentId + this.name, MIME_TYPE_TEXT_XML);
                    }
                    catch (final FileSystemException e) {
                        throw new WebserverSystemException("Error while uploading of content of datastream '"
                            + this.name + "' of the fedora object with id '" + this.parentId
                            + "' to the staging area. ", e);
                    }
                    this.timestamp =
                        this.fedoraUtility.modifyDatastream(this.parentId, this.name, this.label, this.mimeType,
                            this.alternateIDs.toArray(new String[alternateIDs.size()]), tempURI, false);
                }
                catch (final FedoraSystemException e) {
                    LOGGER.debug("Error on modifing datastream.", e);
                    this.fedoraUtility.setDatastreamState(this.parentId, this.name, "A");
                    this.timestamp =
                        this.fedoraUtility.modifyDatastream(this.parentId, this.name, this.label, this.mimeType,
                            this.alternateIDs.toArray(new String[alternateIDs.size()]), tempURI, false);
                }

            }
        }
        return this.timestamp;
    }

    /**
     * Persist datastream to Fedora.
     *
     * @param sync Set true if TripleStore sync is to force.
     * @return Fedora timestamp of datastream.
     * @throws FedoraSystemException    Thrown if writing of datastream into Fedora fails.
     * @throws WebserverSystemException Thrown if getting Fedora instance fails.
     */
    public String persist(final boolean sync) throws FedoraSystemException, WebserverSystemException {

        if (this.getStream() == null && this.location != null) {
            this.timestamp =
                this.fedoraUtility.addDatastream(this.parentId, this.name, this.alternateIDs
                    .toArray(new String[alternateIDs.size()]), this.label, this.location, this.mimeType,
                    this.controlGroupValue, sync);
        }
        else if (this.getStream() != null) {
            this.timestamp =
                this.fedoraUtility.addDatastream(this.parentId, this.name, this.alternateIDs
                    .toArray(new String[alternateIDs.size()]), this.label, VERSIONABLE, this.getStream(), sync);
        }

        return this.timestamp;
    }

    /**
     * Mark datastream as deleted. The datastream is not purged from repository. A new version of datastream is created
     * with <code><deleted/></code> as content.
     *
     * @throws FedoraSystemException    If an error ocurres in Fedora.
     * @throws WebserverSystemException If an error ocurres.
     */
    public void delete() throws FedoraSystemException, WebserverSystemException {
        try {
            // TODO: check of the 'concurrent' flag have to be done too
            if (MIME_TYPE_TEXT_XML.equals(this.mimeType)) {

                this.fedoraUtility.modifyDatastream(this.parentId, this.name, this.label, Constants.MIME_TYPE_DELETED,
                    this.alternateIDs.toArray(new String[alternateIDs.size()]), this.getStream(), false);
                this.fedoraUtility
                    .setDatastreamState(this.parentId, this.name, FedoraUtility.DATASTREAM_STATUS_DELETED);

                init();
            }
        }
        catch (final StreamNotFoundException e) {
            throw new FedoraSystemException("Datastream.delete: ", e);
        }
    }

    /**
     * Checks if a data stream is logically deleted.
     *
     * @return true/false
     */
    public boolean isDeleted() {

        return this.mimeType.equals(Constants.MIME_TYPE_DELETED);
    }

    /**
     * Returns the name of the datastream which is unique in parents scope in Fedora.
     *
     * @return The name of this datastream.
     */
    public String getName() {
        return this.name;
    }

    /*
     * Sets the name of this datastream which is unique in parents scope in
     * Fedora. A second call to this method has no effect.
     * 
     * @param name public void setName(final String name) { if (this.name ==
     * null) { this.name = name; } }
     */

    /**
     * Returns a {@link Set Set} of the alternate IDs of this datastream. Metadata datastreams have the alternate ID
     * "metadata".
     *
     * @return The alternate IDs of this datastream.
     */
    public List<String> getAlternateIDs() {
        return this.alternateIDs;
    }

    /**
     * Adds an alternate ID to the {@link Vector Vector} of the alternate IDs of this datastream. A subsequent call with
     * the same string have no effect. A value off <code>null</code> may be forbidden.
     *
     * @param alternateId An alternate ID to add to this Datastream.
     */
    public void addAlternateId(final String alternateId) {
        this.alternateIDs.add(alternateId);
    }

    /**
     * Replaces an alternate ID in the {@link Vector Vector} of the alternate IDs of this datastream. A subsequent call
     * with the same string have no effect. A value off <code>null</code> may be forbidden.
     *
     * @param alternateId An alternate ID to add to this Datastream.
     * @param index       position to insert ID
     */
    public void replaceAlternateId(final String alternateId, final int index) {
        alternateIDs.remove(index);
        this.alternateIDs.add(index, alternateId);
    }

    /**
     * Sets the alternate IDs for this datastream. Overrides all existing alternate IDs.
     *
     * @param alternateIDs A {@link Set Set} of strings with alternate IDs.
     */
    public void setAlternateIDs(final List<String> alternateIDs) {
        this.alternateIDs = alternateIDs;
    }

    /**
     * Gets the unique id of this datastreams parent.
     *
     * @return The ID of the parent of this datastream.
     */
    public String getParentId() {
        return this.parentId;
    }

    /**
     * Gets the string representing the datastream.
     *
     * @return The string which is the datastream in Fedora.
     * @throws WebserverSystemException If an error ocurres.
     */
    public byte[] getStream() throws WebserverSystemException {
        // Workaround for the issue INFR666, now the content of a data stream
        // with a managed content should be pulled
        if (this.theStream == null && ("X".equals(this.controlGroupValue) || "M".equals(this.controlGroupValue))) {
            final MIMETypedStream datastream;
            try {
                datastream = this.fedoraUtility.getDatastreamWithMimeType(this.name, this.parentId, this.timestamp);
            }
            catch (final FedoraSystemException e) {
                throw new WebserverSystemException(StringUtility.format("Content of datastream could not be retrieved "
                    + "from Fedora after succesfully get " + "datastream information", this.name, this.parentId,
                    this.timestamp), e);
            }

            if (datastream == null) {
                throw new WebserverSystemException("Datastream is 'null' after retrieving "
                    + "datastream from Fedora without exception.");
            }

            this.theStream = datastream.getStream();

        }

        return this.theStream;
    }

    /**
     * Gets the Map with datastream properties.
     *
     * @return Map with datastream properties.
     */
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * Return a String representation of the Datastream using UTF-8 encoding.
     *
     * @return The String representation of the Datastream.
     * @throws EncodingSystemException  If the charset UTF-8 is not supported.
     * @throws WebserverSystemException If an error ocurres.
     */
    public String toStringUTF8() throws EncodingSystemException, WebserverSystemException {

        return toString(XmlUtility.CHARACTER_ENCODING).trim();
    }

    /**
     * See Interface for functional description.<br> This implementation calls <code>toStringUTF8</code>.
     *
     * @return The String representation of the Datastream.
     */
    @Override
    public String toString() {
        try {
            return toStringUTF8();
        }
        catch (final EncodingSystemException e) {
            LOGGER.debug("Can not convert Datastream to string.", e);
            return super.toString();
        }
        catch (final WebserverSystemException e) {
            LOGGER.debug("Can not convert Datastream to string.", e);
            return super.toString();
        }
    }

    /**
     * Return a String representation of the Datastream.
     *
     * @param charset The character encoding.
     * @return The String representation of the Datastream.
     * @throws EncodingSystemException  If the charset is not supported.
     * @throws WebserverSystemException If an error ocurres.
     */
    public String toString(final String charset) throws EncodingSystemException, WebserverSystemException {
        try {
            return new String(getStream(), XmlUtility.CHARACTER_ENCODING);
        }
        catch (final UnsupportedEncodingException e) {
            throw new EncodingSystemException("Charset " + charset + "is not supported!", e);
        }
    }

    /**
     * Sets the string representing the datastream. The datastream may not be concurrent with Fedora unless
     * <code>save()</code> is called.
     *
     * @param theStream The string representing the content of this datastream.
     * @throws FedoraSystemException    If an error ocurres in Fedora.
     * @throws WebserverSystemException Thrown in case of an internal error.
     * @throws StreamNotFoundException  If the stream can not be retrieved.
     */
    public void setStream(final byte[] theStream) throws FedoraSystemException, WebserverSystemException,
        StreamNotFoundException {

        try {
            final String newMd5 = XmlUtility.getMd5Hash(theStream);
            if (!getMd5Hash().equals(newMd5)) {
                this.theStream = theStream;
                this.md5Hash = newMd5;
                merge();
            }
        }
        catch (final ParserConfigurationException e) {
            throw new WebserverSystemException("Creating checksum of datastream fails.", e);
        }
        catch (final SAXException e) {
            throw new WebserverSystemException("Creating checksum of datastream fails.", e);
        }

    }

    /**
     * Compares the name, parentId and md5Hash of the given datastream with this datastream.
     * <p/>
     * Note: Fedora modifies the datastream xml. (changes the sequence of attributes, ...)
     *
     * @param obj The Datastream object which is to compare.
     * @return true if the datastreams are equal.
     */
    @Override
    public boolean equals(final Object obj) {

        if (!(obj instanceof Datastream)) {
            return false;
        }

        final Datastream ds = (Datastream) obj;
        if (MIME_TYPE_TEXT_XML.equals(this.mimeType)) {
            if (!this.name.equals(ds.name)) {
                return false;
            }

            if (!this.parentId.equals(ds.parentId)) {
                return false;
            }

            // FIXME compare alternate IDs, location, label, Control Group etc.
            // ???
            try {
                // FIXME compare xml with control group M
                if (this.getStream() != null && !this.getMd5Hash().equals(ds.getMd5Hash())) {
                    return false;
                }
            }
            catch (final ParserConfigurationException e) {
                LOGGER.debug("Can not compare datastreams.", e);
                return false;
            }
            catch (final SAXException e) {
                LOGGER.debug("Can not compare datastreams.", e);
                return false;
            }
            catch (final WebserverSystemException e) {
                LOGGER.debug("Can not compare datastreams.", e);
                return false;
            }
            return true;
        }
        // return false;
        // I see no reason why they are NOT equal.
        // FIXME a lot of tests are done outside of this method
        return true;
    }

    /**
     * Returns the label of this datastream.
     *
     * @return The label of this datastream.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Sets the label of this datastream.
     *
     * @param label The label of this datastream.
     */
    public void setLabel(final String label) {
        this.label = label;
    }

    /**
     * Returns the md5 checksum of the content of this datastream. The {@link de.escidoc.core.common.util.stax.XMLHashHandler
     * XMLHashHandler} is used to generate a comparable string from the xml data and calculate the checksum.
     *
     * @return The md5 hash of the content of this datastream.
     * @throws ParserConfigurationException by (SAXParserFactory).newSAXParser()
     * @throws SAXException                 by (SAXParserFactory).newSAXParser() and (SAXParser).parser()
     * @throws WebserverSystemException     If an error ocurres.
     */
    public String getMd5Hash() throws ParserConfigurationException, SAXException, WebserverSystemException {
        if (MIME_TYPE_TEXT_XML.equals(this.mimeType)) {
            if (this.md5Hash == null && getStream() != null) {
                this.md5Hash = XmlUtility.getMd5Hash(this.getStream());
            }
            else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("MD5 Hash of datastream " + getParentId() + '/' + getName() + " reused.");
                }
            }
            return this.md5Hash;
        }
        else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        // not needed for HashTables in eSciDoc Business Layer because
        // Datastreams are values not keys

        int hash = 0;
        try {
            hash = (this.name.hashCode() + this.parentId.hashCode() + getMd5Hash().hashCode()) / 3;
        }
        catch (final Exception e) {
            LOGGER.debug("Error on generating hash code.", e);
        }

        return hash;
    }

    /**
     * Get the used ControlGroup type.
     *
     * @return controlGroup
     */
    public String getControlGroup() {
        return this.controlGroupValue;
    }

    /**
     * Set the used ControlGroup type.
     *
     * @param controlGroup Fedora controlGroup type
     */
    public void setControlGroup(final String controlGroup) {
        this.controlGroupValue = controlGroup;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return this.mimeType;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * @return The method by which the checksum was calculated.
     */
    public String getChecksumMethod() {
        return this.checksumMethod;
    }

    /**
     * @return The checksum of the stream.
     */
    public String getChecksum() {
        return this.checksum;
    }

    public void setContentUnchanged(final boolean b) {
        this.contentUnchanged = b;
    }
}
