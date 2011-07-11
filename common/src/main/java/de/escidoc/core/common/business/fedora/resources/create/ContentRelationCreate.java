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

package de.escidoc.core.common.business.fedora.resources.create;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import org.escidoc.core.services.fedora.AddDatastreamPathParam;
import org.escidoc.core.services.fedora.AddDatastreamQueryParam;
import org.escidoc.core.services.fedora.DeleteDatastreamPathParam;
import org.escidoc.core.services.fedora.DeleteDatastreamQueryParam;
import org.escidoc.core.services.fedora.FedoraServiceClient;
import org.escidoc.core.services.fedora.IngestPathParam;
import org.escidoc.core.services.fedora.IngestQueryParam;
import org.escidoc.core.services.fedora.ModifiyDatastreamPathParam;
import org.escidoc.core.services.fedora.ModifyDatastreamQueryParam;
import org.escidoc.core.services.fedora.access.ObjectProfileTO;
import org.escidoc.core.services.fedora.management.DatastreamProfileTO;
import org.esidoc.core.utils.io.IOUtils;
import org.esidoc.core.utils.io.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.RepositoryIndicator;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ContentRelationFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;

/**
 * Content Relation.<br/> See http://colab.mpdl.mpg.de/mediawiki/ESciDoc_Content_Relations_Concept.
 *
 * @author Steffen Wagner
 */
@Configurable
public class ContentRelationCreate extends GenericResourceCreate implements Cloneable, Serializable {

    private static final long serialVersionUID = -2959419814324564197L;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentRelationCreate.class);

    @Autowired
    private transient FedoraServiceClient fedoraServiceClient;

    private final RepositoryIndicator ri = new RepositoryIndicator();

    private ContentRelationProperties properties;

    private List<MdRecordCreate> mdRecords;

    private transient EscidocIdProvider idProvider;

    private String dcXml;

    private URI type;

    private String subject;

    private String object;

    private String subjectVersion;

    private String objectVersion;

    /**
     * @throws WebserverSystemException Thrown if obtaining UserContext failed.
     */
    public ContentRelationCreate() throws WebserverSystemException {

        this.properties = new ContentRelationProperties();
    }

    /**
     * Set ContentRelationProperties.
     *
     * @param properties The properties of Content Relation.
     */
    public void setProperties(final ContentRelationProperties properties) {

        this.properties = properties;
    }

    /**
     * Add a metadata record to the Component.
     *
     * @param mdRecord The new MetadataRecord.
     * @throws InvalidContentException Thrown if md-records with same name
     */
    public void addMdRecord(final MdRecordCreate mdRecord) throws InvalidContentException {

        if (this.mdRecords == null) {
            this.mdRecords = new ArrayList<MdRecordCreate>();
        }
        else {
            checkUniqueName(this.mdRecords, mdRecord.getName());
        }
        mdRecord.getRepositoryIndicator().setResourceIsNew(true);
        this.mdRecords.add(mdRecord);
    }

    /**
     * Delete MdRecord with provided name.
     *
     * @param name Name of MdRecord which is to delete.
     */
    public void deleteMdRecord(final String name) {

        final Iterator<MdRecordCreate> it = this.mdRecords.iterator();
        while (it.hasNext()) {
            final String recordName = it.next().getName();
            if (recordName.equals(name)) {
                it.remove();
                break;
            }
        }
    }

    /**
     * Injects the {@link EscidocIdProvider}.
     * <p/>
     * Attention: the spring constructor is to override in the inheritage classes
     *
     * @param idProvider The {@link EscidocIdProvider} to set.
     */
    public void setIdProvider(final EscidocIdProvider idProvider) {
        this.idProvider = idProvider;
    }

    /**
     * Persist whole Content Relation to Repository and force TripleStore sync.
     *
     * @throws SystemException Thrown if internal error occur.
     */
    public void persist() throws SystemException {
        persist(true);
    }

    /**
     * Persist whole Content Relation to Repository.
     *
     * @param forceSync Set true to force synchronous sync of TripleStore.
     * @throws SystemException Thrown if internal error occur.
     */
    public void persist(final boolean forceSync) throws SystemException {
        try {
            if (getObjid() == null) {
                // Do not set fedora object id earlier. Otherwise consumes
                // an unsuccessful requests an objid (and time). This is
                // redundant
                // if rollback is implemented and gives an unused objid back to
                // the objid pool.
                createFedoraResource();
            }
            else {
                if (this.ri.isResourceChanged()) {
                    updateFedoraResource();
                }
            }
            if (forceSync) {
                this.fedoraServiceClient.sync();
                try {
                    this.getTripleStoreUtility().reinitialize();
                }
                catch (TripleStoreSystemException e) {
                    throw new FedoraSystemException("Error on reinitializing triple store.", e);
                }
            }
        }
        catch (final Exception e) {
            throw new SystemException(e);
        }
    }

    /**
     * Get DC (mapped from default metadata). Value is cached.
     * <p/>
     * Precondition: objid has to be set before getDC is called.
     *
     * @return DC or null if default metadata is missing).
     * @throws WebserverSystemException Thrown if an error occurs during DC creation.
     */
    public String getDC() {

        if (this.dcXml == null) {

            final MdRecordCreate mdRecord =
                getMetadataRecord(XmlTemplateProviderConstants.DEFAULT_METADATA_FOR_DC_MAPPING);
            if (mdRecord != null) {
                try {
                    this.dcXml = getDC(mdRecord, "");
                }
                catch (final Exception e) {
                    LOGGER.info("DC mapping of to create resource failed. " + e);
                }
            }
        }
        return this.dcXml;
    }

    /**
     * Get vector of all MdRecords.
     *
     * @return All MdRecords.
     */
    public List<MdRecordCreate> getMetadataRecords() {
        return this.mdRecords;
    }

    /**
     * Set all MdRecords. The set of MdRecords will be overridden.
     *
     * @param mdrecords Vector with all new MdRecords of the ContentRelation
     */
    public void setMetadataRecords(final List<MdRecordCreate> mdrecords) {
        this.mdRecords = mdrecords;
    }

    /**
     * Get Metadata record by name.
     *
     * @param name Name of MetadataRecord.
     * @return MetadataRecord with required name or null.
     */
    public MdRecordCreate getMetadataRecord(final String name) {
        if (this.mdRecords != null) {
            for (final MdRecordCreate mdRecord : this.mdRecords) {
                if (mdRecord.getName().equals(name)) {
                    return mdRecord;
                }
            }
        }
        return null;
    }

    /**
     * Get Properties of Content Relation.
     *
     * @return ObjectProperties
     */
    public ContentRelationProperties getProperties() {
        return this.properties;
    }

    /**
     * Set relation type.
     *
     * @param type URI with predicate
     */
    public void setType(final URI type) {
        this.type = type;
    }

    /**
     * Get content relation type (URI with predicate).
     *
     * @return URI with predicate
     */
    public URI getType() {
        return this.type;
    }

    /**
     * Set subjects.
     *
     * @param subject Set list of subjects.
     */
    public void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * Get subjects.
     *
     * @return get list of subjects.
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * Set objects.
     *
     * @param object Set list of objects.
     */
    public void setObject(final String object) {
        this.object = object;
    }

    /**
     * Get version number of subject.
     *
     * @return version number of subject
     */
    public String getSubjectVersion() {
        return this.subjectVersion;
    }

    /**
     * Set version number of subject.
     *
     * @param subjectVersion version number of subject
     */
    public void setSubjectVersion(final String subjectVersion) {
        this.subjectVersion = subjectVersion;
    }

    /**
     * Get version number of object.
     *
     * @return version number of object
     */
    public String getObjectVersion() {
        return this.objectVersion;
    }

    /**
     * Set version number of object.
     *
     * @param objectVersion version number of object
     */
    public void setObjectVersion(final String objectVersion) {
        this.objectVersion = objectVersion;
    }

    /**
     * Get objects.
     *
     * @return get list of objects.
     */
    public String getObject() {
        return this.object;
    }

    /**
     * Merge information from nCr to sCR and count the number of changed values.
     *
     * @param nCr The resource with the update values
     * @return number of changed values
     * @throws InvalidContentException Thrown if name of meta record is not unique
     */
    public int merge(final ContentRelationCreate nCr) throws InvalidContentException {

        int changes = 0;

        // merge relation
        changes += mergeRelation(nCr);

        // merge md-records
        changes += mergeMdRecords(nCr);

        if (changes > 0) {
            this.ri.setResourceChanged(true);
        }
        return changes;
    }

    /**
     * Get the repository indicator.
     *
     * @return RepositoryIndicator
     */
    public RepositoryIndicator getRepositoryIndicator() {
        return this.ri;
    }

    /**
     * Merge meta data values from nCr into the object.
     *
     * @param nCr Resource which value are to merge
     * @return number of merges
     * @throws InvalidContentException Thrown if name of MdRecord is not unique or other content is invalid
     */
    private int mergeMdRecords(final ContentRelationCreate nCr) throws InvalidContentException {

        int changes = 0;

        if (getMetadataRecords() == null && nCr.getMetadataRecords() != null) {

            // add all md-records
            for (final MdRecordCreate mdRecord : nCr.getMetadataRecords()) {
                mdRecord.getRepositoryIndicator().setResourceIsNew(true);
                addMdRecord(mdRecord);
                changes++;
            }
        }
        else if (getMetadataRecords() != null && nCr.getMetadataRecords() == null) {

            // mark all md-records as deleted
            for (final MdRecordCreate mdRecord : getMetadataRecords()) {
                mdRecord.getRepositoryIndicator().setResourceToDelete(true);
                changes++;
            }
        }
        else if (getMetadataRecords() != null && nCr.getMetadataRecords() != null) {

            // drop removed MdRecords
            Iterator<MdRecordCreate> it = getMetadataRecords().iterator();
            while (it.hasNext()) {
                final MdRecordCreate mdRecord = it.next();
                final String name = mdRecord.getName();

                final MdRecordCreate newMdRecord = nCr.getMetadataRecord(name);
                if (newMdRecord == null) {
                    mdRecord.getRepositoryIndicator().setResourceToDelete(true);
                }

            }

            // compare existing, add new
            it = nCr.getMetadataRecords().iterator();
            while (it.hasNext()) {
                final MdRecordCreate mdRecord = it.next();
                final String name = mdRecord.getName();

                final MdRecordCreate oldMdRecord = getMetadataRecord(name);
                if (oldMdRecord == null) {
                    mdRecord.getRepositoryIndicator().setResourceIsNew(true);
                    addMdRecord(mdRecord);
                    changes++;
                }
                else if (oldMdRecord.merge(mdRecord) > 0) {
                    mdRecord.getRepositoryIndicator().setResourceChanged(true);
                    changes++;
                }
            }
        }
        return changes;
    }

    /**
     * Merge relation values from nCr into the object.
     *
     * @param nCr Resource which value are to merge
     * @return number of merges
     */
    private int mergeRelation(final ContentRelationCreate nCr) {

        int changes = 0;
        /*
         * We not allow to update subject, predicate or object for
         * ContentRelation
         */
        // // ContentRelation object changed
        // if (getObject() == null) {
        // if (nCr.getObject() != null) {
        // setObject(nCr.getObject());
        // changes++;
        // }
        // }
        // else if (!getObject().equals(nCr.getObject())) {
        // setObject(nCr.getObject());
        // changes++;
        // }
        //
        // // ContentRelation object version changed
        // if (getObjectVersion() == null) {
        // if (nCr.getObjectVersion() != null) {
        // setObjectVersion(nCr.getObjectVersion());
        // changes++;
        // }
        // }
        // else if (!getObjectVersion().equals(nCr.getObjectVersion())) {
        // setObjectVersion(nCr.getObjectVersion());
        // changes++;
        // }
        //
        // // ContentRelation subject changed
        // if (getSubject() == null) {
        // if (nCr.getSubject() != null) {
        // setSubject(nCr.getSubject());
        // changes++;
        // }
        // }
        // else if (!getSubject().equals(nCr.getSubject())) {
        // setSubject(nCr.getSubject());
        // changes++;
        // }
        // // ContentRelation subject version changed
        // if (getSubjectVersion() == null) {
        // if (nCr.getSubjectVersion() != null) {
        // setSubjectVersion(nCr.getSubjectVersion());
        // changes++;
        // }
        // }
        // else if (!getSubjectVersion().equals(nCr.getSubjectVersion())) {
        // setSubjectVersion(nCr.getSubjectVersion());
        // changes++;
        // }
        // ContentRelation description changed
        if (getProperties().getDescription() == null) {
            if (nCr.getProperties().getDescription() != null) {
                getProperties().setDescription(nCr.getProperties().getDescription());
                changes++;
            }
        }
        else if (!getProperties().getDescription().equals(nCr.getProperties().getDescription())) {
            getProperties().setDescription(nCr.getProperties().getDescription());
            changes++;
        }
        return changes;
    }

    /**
     * Check if the name is unique within the list of md-records.
     *
     * @param records Vector with md-records.
     * @param name    The name which is o check.
     * @throws InvalidContentException Thrown if the md-record name is not unique.
     */
    private static void checkUniqueName(final Iterable<MdRecordCreate> records, final String name)
        throws InvalidContentException {

        for (final MdRecordCreate record : records) {
            final String recordName = record.getName();
            if (recordName.equals(name)) {
                throw new InvalidContentException("A md-record with the name '" + name + "' occurs multiple times "
                    + "in the representation of a content relation.");
            }
        }

    }

    /**
     * Create resource in Fedora.
     *
     * @throws SystemException Thrown if internal error occurs.
     */
    private void createFedoraResource() throws SystemException {
        setObjid(this.idProvider.getNextPid());

        if (this.properties.getTitle() == null) {
            // if title is no set through DC,
            // update title (this is required because the title shall
            // contain the version number)

            this.properties.setTitle("Content Relation " + getObjid());
        }

        // serialize object without RELS-EXT and WOV to FOXML
        final String foxml = ContentRelationFoXmlProvider.getInstance().getFoXml(this);
        final IngestPathParam path = new IngestPathParam();
        final IngestQueryParam query = new IngestQueryParam();
        this.fedoraServiceClient.ingest(path, query, foxml);

        // creation /last-modification date
        final ObjectProfileTO objectProfile = this.fedoraServiceClient.getObjectProfile(getObjid());
        getProperties().setCreationDate(objectProfile.getObjLastModDate());
        getProperties().setLastModificationDate(objectProfile.getObjLastModDate());
    }

    /**
     * Update resource in Fedora.
     *
     * @throws SystemException Thrown if internal error occurs.
     */
    private void updateFedoraResource() throws SystemException {

        // update md-records
        if (getMetadataRecords() != null) {

            // drop removed MdRecords
            final Iterator<MdRecordCreate> it = getMetadataRecords().iterator();
            while (it.hasNext()) {
                final MdRecordCreate mdRecord = it.next();

                if (mdRecord.getRepositoryIndicator().isResourceToDelete()) {
                    // if MdRecord is marked as deleted
                    deleteDatastream(mdRecord);
                    it.remove();
                }
                else if (mdRecord.getRepositoryIndicator().isResourceChanged()) {
                    updateDataStream(mdRecord);
                }
                else if (mdRecord.getRepositoryIndicator().isResourceIsNew()) {
                    createDatastream(mdRecord);
                }

            }
        }

        // update rels-ext
        persistProperties(false);
    }

    /**
     * Persist properties.
     *
     * @param sync Set true if TripleStore sync is to call. False otherwise.
     * @throws SystemException Thrown if updating Fedora repository or syncing TripleStore failed.
     */
    public void persistProperties(final boolean sync) throws SystemException {
        final String relsExt = ContentRelationFoXmlProvider.getInstance().getRelsExt(this);
        final ModifiyDatastreamPathParam path =
            new ModifiyDatastreamPathParam(getObjid(), Datastream.RELS_EXT_DATASTREAM);
        final ModifyDatastreamQueryParam query = new ModifyDatastreamQueryParam();
        query.setDsLabel(Datastream.RELS_EXT_DATASTREAM_LABEL);
        final Stream stream = new Stream();
        try {
            stream.write(relsExt.getBytes(XmlUtility.CHARACTER_ENCODING));
            stream.lock();
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
        final DatastreamProfileTO datastreamProfile = this.fedoraServiceClient.modifyDatastream(path, query, stream);

        if (datastreamProfile.getDateTime() != null) {
            getProperties().setLastModificationDate(datastreamProfile.getDateTime());
        }
        else {
            getProperties().setLastModificationDate(datastreamProfile.getDsCreateDate());
        }

        if (sync) {
            this.fedoraServiceClient.sync();
            try {
                this.getTripleStoreUtility().reinitialize();
            }
            catch (TripleStoreSystemException e) {
                throw new FedoraSystemException("Error on reinitializing triple store.", e);
            }
        }
    }

    /**
     * Mark datastream in repository as deleted. A
     *
     * @param mdRecord Md Record which is to delete
     * @throws FedoraSystemException Thrown if Fedora request failed.
     */
    private void deleteDatastream(final MdRecordCreate mdRecord) {
        final DeleteDatastreamPathParam path = new DeleteDatastreamPathParam(getObjid(), mdRecord.getName());
        final DeleteDatastreamQueryParam query = new DeleteDatastreamQueryParam();
        this.fedoraServiceClient.deleteDatastream(path, query);
    }

    /**
     * Update MdRecord in repository (Fedora).
     *
     * @param mdRecord The MdRecord which is to update.
     * @throws FedoraSystemException    Thrown if Fedora request failed.
     * @throws WebserverSystemException Thrown if internal error occurs.
     */
    private void updateDataStream(final MdRecordCreate mdRecord) throws WebserverSystemException {

        final String[] altIds = { Datastream.METADATA_ALTERNATE_ID, mdRecord.getType(), mdRecord.getSchema() };

        final byte[] content;
        try {
            content = mdRecord.getContent().getBytes(XmlUtility.CHARACTER_ENCODING);
        }
        catch (final UnsupportedEncodingException e) {
            throw new WebserverSystemException(e);
        }
        final ModifiyDatastreamPathParam path = new ModifiyDatastreamPathParam(getObjid(), mdRecord.getName());
        final ModifyDatastreamQueryParam query = new ModifyDatastreamQueryParam();
        query.setDsLabel(mdRecord.getLabel());
        query.setMimeType(mdRecord.getMimeType());
        query.setAltIDs(Arrays.asList(altIds));
        final Stream stream = new Stream();
        try {
            stream.write(content);
            stream.lock();
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
        this.fedoraServiceClient.modifyDatastream(path, query, stream);
        mdRecord.getRepositoryIndicator().setResourceChanged(false);
    }

    /**
     * Store a new create a datastream in the repository (Fedora).
     *
     * @param mdRecord MdRecord
     * @throws FedoraSystemException    Thrown if Fedora request failed.
     * @throws WebserverSystemException Thrown if internal error occurs.
     */
    private void createDatastream(final MdRecordCreate mdRecord) throws WebserverSystemException {

        final String[] altIds = { Datastream.METADATA_ALTERNATE_ID, mdRecord.getType(), mdRecord.getSchema() };

        final byte[] content;
        try {
            content = mdRecord.getContent().getBytes(XmlUtility.CHARACTER_ENCODING);
        }
        catch (final UnsupportedEncodingException e) {
            throw new WebserverSystemException(e);
        }

        final AddDatastreamPathParam path = new AddDatastreamPathParam(getObjid(), mdRecord.getName());
        final AddDatastreamQueryParam query = new AddDatastreamQueryParam();
        query.setAltIDs(Arrays.asList(altIds));
        query.setDsLabel(mdRecord.getLabel());
        query.setVersionable(Boolean.FALSE);
        final Stream stream = new Stream();
        try {
            stream.write(content);
            stream.lock();
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
        this.fedoraServiceClient.addDatastream(path, query, stream);

        mdRecord.getRepositoryIndicator().setResourceChanged(false);
        mdRecord.getRepositoryIndicator().setResourceIsNew(false);
    }

    /**
     * Creates and returns a copy of this object.
     *
     * @return a clone of this instance.
     * @throws CloneNotSupportedException if the object's class does not support the Cloneable interface. Subclasses
     *                                    that override the clone method can also throw this exception to indicate that
     *                                    an instance cannot be cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        final Object result;
        try {
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(os);
            try {
                oos.writeObject(this);
            }
            finally {
                IOUtils.closeStream(oos);
            }
            final InputStream fis = new ByteArrayInputStream(os.toByteArray());
            final ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                result = ois.readObject();
            }
            finally {
                IOUtils.closeStream(ois);
            }
        }
        catch (final Exception e) {
            final CloneNotSupportedException cnse = new CloneNotSupportedException(e.toString()); // Ignore
            // FindBugs
            cnse.initCause(e);
            throw cnse;
        }
        return result;
    }
}
