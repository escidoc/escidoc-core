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
package de.escidoc.core.common.business.fedora.datastream;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import net.sf.oval.guard.Guarded;

import org.apache.cxf.jaxrs.client.ServerWebApplicationException;
import org.escidoc.core.services.fedora.AddDatastreamPathParam;
import org.escidoc.core.services.fedora.AddDatastreamQueryParam;
import org.escidoc.core.services.fedora.ControlGroup;
import org.escidoc.core.services.fedora.DatastreamState;
import org.escidoc.core.services.fedora.FedoraServiceClient;
import org.escidoc.core.services.fedora.GetDatastreamProfilePathParam;
import org.escidoc.core.services.fedora.GetDatastreamProfileQueryParam;
import org.escidoc.core.services.fedora.ModifiyDatastreamPathParam;
import org.escidoc.core.services.fedora.ModifyDatastreamQueryParam;
import org.escidoc.core.services.fedora.management.DatastreamProfileTO;
import org.esidoc.core.utils.io.MimeTypes;
import org.esidoc.core.utils.io.Stream;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.common.business.fedora.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.FileSystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * Representation of a datastream managed in Fedora Digital Repository System.
 *
 * @author Frank Schwichtenberg
 */
@Configurable(preConstruction = true)
@Guarded(applyFieldConstraintsToConstructors = true, applyFieldConstraintsToSetters = true, assertParametersNotNull = false, checkInvariants = true, inspectInterfaces = true)
public class Datastream {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Datastream.class);

    public static final String DC_DATASTREAM = "DC";

    public static final String RELS_EXT_DATASTREAM = "RELS-EXT";

    public static final String RELS_EXT_DATASTREAM_LABEL = "RELS_EXT DATASTREAM";

    private static final String CONTROL_GROUP_EXTERNAL_REFERENCE = "E";

    private static final String CONTROL_GROUP_REDIRECT = "R";

    private static final String CONTROL_GROUP_INTERNAL_XML = "X";

    private static final String CONTROL_GROUP_MANAGED = "M";

    public static final String METADATA_ALTERNATE_ID = "metadata";

    private final String name;

    private final String parentId;

    private DateTime timestamp;

    private List<String> alternateIDs = new ArrayList<String>();

    private String label;

    private String mimeType;

    private byte[] stream;

    private String controlGroupValue = CONTROL_GROUP_INTERNAL_XML;

    private Map<String, String> properties;

    private String location;

    private String checksumMethod;

    private String checksum;

    private DatastreamState state = DatastreamState.A;

    @Autowired
    private FedoraServiceClient fedoraServiceClient;

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tripleStoreUtility;

    @Autowired
    @Qualifier("business.Utility")
    private Utility utility;

    /**
     * Indicating the URL should not be sent when storing this in Fedora.
     */
    private boolean contentUnchanged;

    /**
     * Constructs the Stream identified by name and parentId. The version of the stream identified by timestamp is
     * retrieved from Fedora. If timestamp is {@code null} the latest version is retrieved.
     *
     * @param name      The name of the datastream.
     * @param parentId  The unique id the fedora object to which the datastream belongs.
     * @param timestamp A timestamp specify the version of the stream.
     * @throws StreamNotFoundException If there is no datastream identified by name and parentId in Fedora.
     * @throws FedoraSystemException   Thrown in case of an internal system error caused by failed fedora access.
     */
    public Datastream(@NotNull
    final String name, @NotNull
    final String parentId, final DateTime timestamp) throws FedoraSystemException, StreamNotFoundException {
        this.name = name;
        this.parentId = parentId;
        this.timestamp = timestamp;
        loadDataFromFedora();
    }

    /**
     * @param datastreamProfileTO
     * @param parentId
     */
    public Datastream(@NotNull
    final DatastreamProfileTO datastreamProfileTO, final String parentId) {
        this.name = datastreamProfileTO.getDsID();
        this.parentId = parentId;
        updateDatastream(datastreamProfileTO);
    }

    /**
     * @param datastreamProfileTO
     * @param parentId
     * @param overwriteTimestamp
     */
    public Datastream(@NotNull
    final DatastreamProfileTO datastreamProfileTO, final String parentId, final DateTime overwriteTimestamp) {
        this.name = datastreamProfileTO.getDsID();
        this.parentId = parentId;
        updateDatastream(datastreamProfileTO);
        this.timestamp = overwriteTimestamp;
    }

    /**
     * Constructs the Stream identified by name and parentId. The version of the stream identified by timestamp is
     * retrieved from Fedora. If timestamp is {@code null} the latest version is retrieved.
     *
     * @param name              The name of the datastream.
     * @param parentId          The unique id the fedora object to which the datastream belongs.
     * @param timestamp         A timestamp specify the version of the stream.
     * @param mimeType          MIME Type of the data stream.
     * @param location          TODO
     * @param controlGroupValue The Fedora Control Group type.
     */
    public Datastream(@NotNull
    final String name, @NotNull
    final String parentId, final DateTime timestamp, final String mimeType, final String location,
        final String controlGroupValue) {
        this.name = name;
        this.parentId = parentId;
        this.timestamp = timestamp;
        this.mimeType = mimeType;
        this.location = location;
        this.controlGroupValue = controlGroupValue;
    }

    /**
     * Constructs the Stream identified by name and parentId. The version of the stream identified by timestamp is
     * retrieved from Fedora. If timestamp is {@code null} the latest version is retrieved.
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
    public Datastream(@NotNull
    final String name, @NotNull
    final String parentId, final DateTime timestamp, final String mimeType, final String location,
        final String controlGroupValue, final String checksumMethod, final String checksum) {
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
     * Constructs a Stream of the given parameters. The stream may be concurrent with the datastream saved in Fedora or
     * not. Maybe there is no such datastream in Fedora.
     *
     * @param name     The name of this datastream
     * @param parentId The ID of the parent of this datastream.
     * @param stream   The string representing the content of this datastream.
     * @param mimeType TODO
     */
    public Datastream(@NotNull
    final String name, @NotNull
    final String parentId, @NotNull
    final byte[] stream, final String mimeType) {
        this.name = name;
        this.parentId = parentId;
        this.mimeType = mimeType;
        this.setStream(stream);
    }

    /**
     * Constructs a Stream of the given parameters. The stream may be concurrent with the datastream saved in Fedora or
     * not. Maybe there is no such datastream in Fedora.
     *
     * @param name     The name of this datastream
     * @param parentId The ID of the parent of this datastream.
     * @param url      The URL of the content.
     * @param storage  TODO
     * @param mimeType TODO
     */
    public Datastream(@NotNull
    final String name, @NotNull
    final String parentId, @NotNull
    final String url, final String storage, final String mimeType) {
        this.name = name;
        this.parentId = parentId;
        this.mimeType = mimeType;
        this.location = url;
        if (Constants.STORAGE_EXTERNAL_MANAGED.equalsIgnoreCase(storage)) {
            this.controlGroupValue = CONTROL_GROUP_EXTERNAL_REFERENCE;
        }
        else if (Constants.STORAGE_EXTERNAL_URL.equalsIgnoreCase(storage)) {
            this.controlGroupValue = CONTROL_GROUP_REDIRECT;
        }
        else if (Constants.STORAGE_INTERNAL_MANAGED.equalsIgnoreCase(storage)) {
            this.controlGroupValue = CONTROL_GROUP_MANAGED;
        }
    }

    /**
     * Constructs a Stream of the given parameters. The stream may be concurrent with the datastream saved in Fedora or
     * not. Maybe there is no such datastream in Fedora.
     *
     * @param name       The name of this datastream
     * @param parentId   The ID of the parent of this datastream.
     * @param stream     The string representing the content of this datastream.
     * @param mimeType   The MIME type of this datastream.
     * @param properties Map with properties of this datastream
     */
    public Datastream(@NotNull
    final String name, @NotNull
    final String parentId, @NotNull
    final byte[] stream, final String mimeType, final Map<String, String> properties) {
        this.name = name;
        this.parentId = parentId;
        this.mimeType = mimeType;
        this.properties = properties;
        this.setStream(stream);
    }

    private void loadDataFromFedora() throws StreamNotFoundException, FedoraSystemException {
        final GetDatastreamProfilePathParam path = new GetDatastreamProfilePathParam(this.parentId, this.name);
        final GetDatastreamProfileQueryParam query = new GetDatastreamProfileQueryParam();
        if (this.timestamp != null) {
            query.setAsOfDateTime(this.timestamp.withZone(DateTimeZone.UTC).toString());
        }
        try {
            final DatastreamProfileTO datastreamProfileTO = this.fedoraServiceClient.getDatastreamProfile(path, query);
            updateDatastream(datastreamProfileTO);
        }
        catch (final ServerWebApplicationException e) {
            if (e.getStatus() == 404) {
                throw new StreamNotFoundException(e);
            }
            else {
                throw new FedoraSystemException(e);
            }
        }
    }

    private void updateDatastream(final DatastreamProfileTO datastreamProfileTO) {
        this.mimeType = datastreamProfileTO.getDsMIME();
        this.location = datastreamProfileTO.getDsLocation();
        this.label = datastreamProfileTO.getDsLabel();
        this.state = DatastreamState.valueOf(datastreamProfileTO.getDsState());
        this.controlGroupValue = datastreamProfileTO.getDsControlGroup();
        this.alternateIDs = new ArrayList<String>(datastreamProfileTO.getDsAltID());
        this.checksumMethod = datastreamProfileTO.getDsChecksumType();
        this.checksum = datastreamProfileTO.getDsChecksum();
        if (datastreamProfileTO.getDateTime() != null) {
            this.timestamp = datastreamProfileTO.getDateTime();
        }
        else {
            this.timestamp = datastreamProfileTO.getDsCreateDate();
        }
    }

    /**
     * Merges the datastream identified by the name and the objectId to Fedora. The datastream must already exists in
     * Fedora. Otherwise call {@code persist()}.
     *
     * @return timestamp of datastream (last-modification-date)
     * @throws FedoraSystemException    Thrown if writing to Fedora failed.
     * @throws WebserverSystemException Thrown in case of internal failure (get configuration)
     */
    public DateTime merge() throws WebserverSystemException {
        final ModifiyDatastreamPathParam path = new ModifiyDatastreamPathParam(this.parentId, this.name);
        final ModifyDatastreamQueryParam query = new ModifyDatastreamQueryParam();
        query.setDsLabel(this.label);
        query.setMimeType(this.mimeType);
        query.setAltIDs(this.alternateIDs);
        String location = this.location;
        if (this.getStream() == null && this.location != null) {
            // FIXME this location/href is logic of Item!
            if (this.contentUnchanged
                || location.startsWith("/ir/item/" + getParentId())
                || location.startsWith(EscidocConfiguration
                    .getInstance().get(EscidocConfiguration.ESCIDOC_CORE_BASEURL)
                    + "/ir/item/" + getParentId())) {
                // TODO assuming unchanged href
                location = null;
            }
            else if (location.startsWith("/")) {
                // assuming relative URL
                location = EscidocConfiguration.getInstance().get(EscidocConfiguration.ESCIDOC_CORE_BASEURL) + location;
            }
        }
        else if (this.getStream() != null) {
            if (CONTROL_GROUP_MANAGED.equals(this.getControlGroup())) {
                try {
                    location = this.utility.upload(this.getStream(), this.parentId + this.name, MimeTypes.TEXT_XML);
                }
                catch (final FileSystemException e) {
                    throw new WebserverSystemException("Error while uploading of content of datastream '" + this.name
                        + "' of the fedora object with id '" + this.parentId + "' to the staging area. ", e);
                }
            }
        }
        query.setDsLocation(location);
        try {
            final DatastreamProfileTO datastreamProfile =
                this.fedoraServiceClient.modifyDatastream(path, query, convertStream());
            updateDatastream(datastreamProfile);
        }
        catch (final Exception e) {
            LOGGER.debug("Error on modifing datastream.", e);
            if (this.getStream() != null) {
                addDatastream();
            }
            else {
                addDatastream(location);
            }
        }
        return this.timestamp;
    }

    private Stream convertStream() throws WebserverSystemException {
        if (this.getStream() == null) {
            return null;
        }
        try {
            final Stream stream = new Stream();
            stream.write(this.getStream());
            stream.lock();
            return stream;
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
    }

    private void addDatastream(final String location) {
        final AddDatastreamPathParam addPath = new AddDatastreamPathParam(this.parentId, this.name);
        final AddDatastreamQueryParam addQuery = new AddDatastreamQueryParam();
        addQuery.setDsLocation(location);
        addQuery.setDsLabel(this.label);
        addQuery.setMimeType(this.mimeType);
        addQuery.setAltIDs(this.alternateIDs);
        final DatastreamProfileTO datastreamProfile = this.fedoraServiceClient.addDatastream(addPath, addQuery, null);
        updateDatastream(datastreamProfile);
    }

    private void addDatastream() throws WebserverSystemException {
        final AddDatastreamPathParam addPath = new AddDatastreamPathParam(this.parentId, this.name);
        final AddDatastreamQueryParam addQuery = new AddDatastreamQueryParam();
        addQuery.setDsLabel(this.label);
        addQuery.setMimeType(this.mimeType);
        addQuery.setAltIDs(this.alternateIDs);
        final DatastreamProfileTO datastreamProfile =
            this.fedoraServiceClient.addDatastream(addPath, addQuery, convertStream());
        updateDatastream(datastreamProfile);
    }

    /**
     * Persist datastream to Fedora.
     *
     * @param sync Set true if TripleStore sync is to force.
     * @return Fedora timestamp of datastream.
     * @throws FedoraSystemException    Thrown if writing of datastream into Fedora fails.
     * @throws WebserverSystemException Thrown if getting Fedora instance fails.
     */
    public String persist(final boolean sync) throws FedoraSystemException {
        final AddDatastreamPathParam path = new AddDatastreamPathParam(this.parentId, this.name);
        final AddDatastreamQueryParam query = new AddDatastreamQueryParam();
        query.setAltIDs(this.alternateIDs);
        query.setDsLabel(this.label);
        query.setMimeType(this.mimeType);
        query.setControlGroup(ControlGroup.fromValue(this.controlGroupValue));
        if (this.getStream() == null && this.location != null) {
            query.setDsLocation(this.location);
            this.fedoraServiceClient.addDatastream(path, query, null);
        }
        else if (this.getStream() != null) {
            query.setVersionable(Boolean.TRUE);
            final org.esidoc.core.utils.io.Stream stream = new org.esidoc.core.utils.io.Stream();
            try {
                stream.write(this.getStream());
                stream.lock();
            }
            catch (final IOException e) {
                throw new FedoraSystemException("Error on persisting datastream.", e);
            }
            final DatastreamProfileTO datastreamProfile = this.fedoraServiceClient.addDatastream(path, query, stream);
            this.updateDatastream(datastreamProfile);
        }
        if (sync) {
            this.fedoraServiceClient.sync();
            try {
                this.tripleStoreUtility.reinitialize();
            }
            catch (final TripleStoreSystemException e) {
                throw new FedoraSystemException("Error on reinitializing triple store.", e);
            }
        }
        return this.timestamp.withZone(DateTimeZone.UTC).toString(
            de.escidoc.core.common.business.Constants.TIMESTAMP_FORMAT);
    }

    /**
     * Mark datastream as deleted. The datastream is not purged from repository. A new version of datastream is created
     * with {@code <deleted/>} as content.
     *
     * @throws FedoraSystemException    If an error ocurres in Fedora.
     * @throws WebserverSystemException If an error ocurres.
     */
    public void delete() {
        final DatastreamProfileTO datastreamProfileTO =
            this.fedoraServiceClient.setDatastreamState(this.parentId, this.name, DatastreamState.D);
        this.updateDatastream(datastreamProfileTO);
    }

    /**
     * Checks if a data stream is logically deleted.
     *
     * @return true/false
     */
    public boolean isDeleted() {
        return DatastreamState.D.equals(this.state);
    }

    /**
     * Returns the name of the datastream which is unique in parents scope in Fedora.
     *
     * @return The name of this datastream.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns a {@link java.util.Set Set} of the alternate IDs of this datastream. Metadata datastreams have the
     * alternate ID "metadata".
     *
     * @return The alternate IDs of this datastream.
     */
    public List<String> getAlternateIDs() {
        return this.alternateIDs;
    }

    /**
     * Adds an alternate ID to the {@link java.util.List List} of the alternate IDs of this datastream. A subsequent
     * call with the same string have no effect. A value off {@code null} may be forbidden.
     *
     * @param alternateId An alternate ID to add to this Stream.
     */
    public void addAlternateId(final String alternateId) {
        this.alternateIDs.add(alternateId);
    }

    /**
     * Replaces an alternate ID in the {@link java.util.List List} of the alternate IDs of this datastream. A subsequent
     * call with the same string have no effect. A value off {@code null} may be forbidden.
     *
     * @param alternateId An alternate ID to add to this Stream.
     * @param index       position to insert ID
     */
    public void replaceAlternateId(final String alternateId, final int index) {
        alternateIDs.remove(index);
        this.alternateIDs.add(index, alternateId);
    }

    /**
     * Sets the alternate IDs for this datastream. Overrides all existing alternate IDs.
     *
     * @param alternateIDs A {@link java.util.Set Set} of strings with alternate IDs.
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
    public byte[] getStream() {
        // Workaround for the issue INFR666, now the content of a data stream
        // with a managed content should be pulled
        if (this.stream == null && ("X".equals(this.controlGroupValue) || "M".equals(this.controlGroupValue))) {
            loadStreamFromFedora();
        }
        return this.stream;
    }

    private void loadStreamFromFedora() {
        final Stream stream = this.fedoraServiceClient.getDatastream(this.parentId, this.name, this.timestamp);
        if (stream == null) {
            throw new RuntimeException("Stream is 'null' after retrieving "
                + "datastream from Fedora without exception.");
        }
        try {
            this.setStream(stream.getBytes());
        }
        catch (final IOException e) {
            throw new RuntimeException("Error loading datastream.", e);
        }
    }

    private void setStream(final byte[] stream) {
        this.stream = stream;
    }

    /**
     * Updates the string representing the datastream. The datastream may not be concurrent with Fedora unless
     * {@code save()} is called.
     *
     * @param stream The string representing the content of this datastream.
     * @return
     */
    public boolean updateStream(final byte[] stream) {
        if (!XmlUtility.isIdentical(stream, this.stream)) {
            this.setStream(stream);
            return true;
        }
        return false;
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
     * Return a String representation of the Stream using UTF-8 encoding.
     *
     * @return The String representation of the Stream.
     * @throws EncodingSystemException  If the charset UTF-8 is not supported.
     * @throws WebserverSystemException If an error ocurres.
     */
    public String toStringUTF8() throws EncodingSystemException {

        return toString(XmlUtility.CHARACTER_ENCODING).trim();
    }

    /**
     * See Interface for functional description.<br> This implementation calls {@code toStringUTF8}.
     *
     * @return The String representation of the Stream.
     */
    @Override
    public String toString() {
        try {
            return toStringUTF8();
        }
        catch (final EncodingSystemException e) {
            LOGGER.debug("Can not convert Stream to string.", e);
            return super.toString();
        }
    }

    /**
     * Return a String representation of the Stream.
     *
     * @param charset The character encoding.
     * @return The String representation of the Stream.
     * @throws EncodingSystemException  If the charset is not supported.
     * @throws WebserverSystemException If an error ocurres.
     */
    public String toString(final String charset) throws EncodingSystemException {
        try {
            return new String(getStream(), charset);
        }
        catch (final UnsupportedEncodingException e) {
            throw new EncodingSystemException("Charset " + charset + "is not supported!", e);
        }
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

    public String getMimeType() {
        return this.mimeType;
    }

    public String getLocation() {
        return this.location;
    }

    public String getChecksumMethod() {
        return this.checksumMethod;
    }

    public String getChecksum() {
        return this.checksum;
    }

    public void setContentUnchanged(final boolean b) {
        this.contentUnchanged = b;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Datastream that = (Datastream) o;
        if (!name.equals(that.name)) {
            return false;
        }
        if (!parentId.equals(that.parentId)) {
            return false;
        }
        return XmlUtility.isIdentical(that.getStream(), this.getStream());
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (getStream() != null ? Arrays.hashCode(getStream()) : 0);
        return result;
    }

    /**
     * A convenience method to support all old implementations using the  object instead of the new
     * {@link DatastreamProfileTO} object.<br/> <br/> If you want to convert an instance of {@link DatastreamProfileTO}
     * to an instance of  use the constructor {@link Datastream#Datastream(DatastreamProfileTO,
     * String)}
     *
     * @param datastreamProfileTOs
     * @param parentId
     * @return a map containing  objects representing the {@link DatastreamProfileTO} objects and the
     *         datastream ID as the keys.
     */
    public static Map<String, Datastream> convertDatastreamProfileTOs(@NotNull
    final List<DatastreamProfileTO> datastreamProfileTOs, final String parentId) {

        final Map<String, Datastream> result = new HashMap<String, Datastream>(datastreamProfileTOs.size() + 1);

        for (final DatastreamProfileTO datastreamProfileTO : datastreamProfileTOs) {
            result.put(datastreamProfileTO.getDsID(), new Datastream(datastreamProfileTO, parentId, null));
        }
        return result;
    }

    /**
     * A convenience method to support all old implementations using the  object instead of the new
     * {@link DatastreamProfileTO} object.<br/> <br/> If you want to convert an instance of {@link DatastreamProfileTO}
     * to an instance of  use the constructor {@link Datastream#Datastream(DatastreamProfileTO,
     * String)}
     *
     * @param datastreamProfileTOs
     * @param parentId
     * @param intoMap
     * @return a map containing  objects representing the {@link DatastreamProfileTO} objects and the
     *         datastream ID as the keys.
     */
    public static void convertDatastreamProfileTOs(@NotNull
    final List<DatastreamProfileTO> datastreamProfileTOs, final String parentId, final Map<String, Datastream> intoMap) {

        for (final DatastreamProfileTO datastreamProfileTO : datastreamProfileTOs) {
            intoMap.put(datastreamProfileTO.getDsID(), new Datastream(datastreamProfileTO, parentId, null));
        }
    }
}
