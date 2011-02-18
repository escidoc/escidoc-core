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
package de.escidoc.core.common.business.fedora.resources.create;

import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.RepositoryIndicator;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ContentRelationFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Content Relation.<br/>
 * 
 * @see http://colab.mpdl.mpg.de/mediawiki/ESciDoc_Content_Relations_Concept.
 * 
 * @author SWA
 * 
 */
public class ContentRelationCreate extends GenericResourceCreate
    implements Cloneable, Serializable {

    private static final long serialVersionUID = -2959419814324564197L;

    private static final AppLogger LOG =
        new AppLogger(ContentRelationCreate.class.getName());

    private final RepositoryIndicator ri = new RepositoryIndicator();

    private ContentRelationProperties properties = null;

    private List<MdRecordCreate> mdRecords = null;

    private transient EscidocIdProvider idProvider = null;

    private String dcXml = null;

    private URI type = null;

    private String subject = null;

    private String object = null;

    private String subjectVersion = null;

    private String objectVersion = null;

    /**
     * 
     * @throws WebserverSystemException
     *             Thrown if obtaining UserContext failed.
     */
    public ContentRelationCreate() throws WebserverSystemException {

        this.properties = new ContentRelationProperties();
    }

    /**
     * Set ContentRelationProperties.
     * 
     * @param properties
     *            The properties of Content Relation.
     */
    public final void setProperties(final ContentRelationProperties properties) {

        this.properties = properties;
    }

    /**
     * Add a metadata record to the Component.
     * 
     * @param mdRecord
     *            The new MetadataRecord.
     * @throws InvalidContentException
     *             Thrown if md-records with same name
     */
    public final void addMdRecord(final MdRecordCreate mdRecord)
        throws InvalidContentException {

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
     * @param name
     *            Name of MdRecord which is to delete.
     */
    public void deleteMdRecord(final String name) {

        Iterator<MdRecordCreate> it = this.mdRecords.iterator();
        while (it.hasNext()) {
            String recordName = it.next().getName();
            if (recordName.equals(name)) {
                it.remove();
                break;
            }
        }
    }

    /**
     * Injects the {@link EscidocIdProvider}.
     * 
     * Attention: the spring constructor is to override in the inheritage
     * classes
     * 
     * @param idProvider
     *            The {@link EscidocIdProvider} to set.
     * 
     * @spring.property ref="escidoc.core.business.EscidocIdProvider"
     */
    public final void setIdProvider(final EscidocIdProvider idProvider) {

        this.idProvider = idProvider;
    }

    /**
     * Persist whole Content Relation to Repository and force TripleStore sync.
     * 
     * @throws SystemException
     *             Thrown if internal error occur.
     */
    public final void persist() throws SystemException {

        persist(true);
    }

    /**
     * Persist whole Content Relation to Repository.
     * 
     * @param forceSync
     *            Set true to force synchronous sync of TripleStore.
     * @throws SystemException
     *             Thrown if internal error occur.
     */
    public final void persist(final boolean forceSync) throws SystemException {

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
                FedoraUtility.getInstance().sync();
            }

        }
        catch (Exception e) {
            throw new SystemException(e);
        }
    }

    /**
     * Get DC (mapped from default metadata). Value is cached.
     * 
     * Precondition: objid has to be set before getDC is called.
     * 
     * @return DC or null if default metadata is missing).
     * @throws WebserverSystemException
     *             Thrown if an error occurs during DC creation.
     * @throws EncodingSystemException
     *             Thrown if the conversion to default encoding failed.
     */
    public String getDC() throws WebserverSystemException,
        EncodingSystemException {

        if (this.dcXml == null) {

            MdRecordCreate mdRecord =
                getMetadataRecord(XmlTemplateProvider.DEFAULT_METADATA_FOR_DC_MAPPING);
            if (mdRecord != null) {
                try {
                    this.dcXml = getDC(mdRecord, "");
                }
                catch (Exception e) {
                    LOG.info("DC mapping of to create resource failed. " + e);
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
    public final List<MdRecordCreate> getMetadataRecords() {
        return this.mdRecords;
    }

    /**
     * Set all MdRecords. The set of MdRecords will be overridden.
     * 
     * @param mdrecords
     *            Vector with all new MdRecords of the ContentRelation
     */
    public void setMetadataRecords(final List<MdRecordCreate> mdrecords) {
        this.mdRecords = mdrecords;
    }

    /**
     * Get Metadata record by name.
     * 
     * @param name
     *            Name of MetadataRecord.
     * @return MetadataRecord with required name or null.
     */
    public final MdRecordCreate getMetadataRecord(final String name) {
        if (this.mdRecords != null) {
            for (MdRecordCreate mdRecord : this.mdRecords) {
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
    public final ContentRelationProperties getProperties() {
        return this.properties;
    }

    /**
     * Set relation type.
     * 
     * @param type
     *            URI with predicate
     */
    public final void setType(final URI type) {
        this.type = type;
    }

    /**
     * Get content relation type (URI with predicate).
     * 
     * @return URI with predicate
     */
    public final URI getType() {
        return type;
    }

    /**
     * Set subjects.
     * 
     * @param subject
     *            Set list of subjects.
     */
    public final void setSubject(final String subject) {
        this.subject = subject;
    }

    /**
     * Get subjects.
     * 
     * @return get list of subjects.
     */
    public final String getSubject() {
        return subject;
    }

    /**
     * Set objects.
     * 
     * @param object
     *            Set list of objects.
     */
    public final void setObject(final String object) {
        this.object = object;
    }

    /**
     * Get version number of subject.
     * 
     * @return version number of subject
     */
    public final String getSubjectVersion() {
        return subjectVersion;
    }

    /**
     * Set version number of subject.
     * 
     * @param subjectVersion
     *            version number of subject
     */
    public final void setSubjectVersion(final String subjectVersion) {
        this.subjectVersion = subjectVersion;
    }

    /**
     * Get version number of object.
     * 
     * @return version number of object
     */
    public final String getObjectVersion() {
        return objectVersion;
    }

    /**
     * Set version number of object.
     * 
     * @param objectVersion
     *            version number of object
     */
    public final void setObjectVersion(final String objectVersion) {
        this.objectVersion = objectVersion;
    }

    /**
     * Get objects.
     * 
     * @return get list of objects.
     */
    public final String getObject() {
        return object;
    }

    /**
     * Merge information from nCr to sCR and count the number of changed values.
     * 
     * @param nCr
     *            The resource with the update values
     * @return number of changed values
     * @throws InvalidContentException
     *             Thrown if name of meta record is not unique
     */
    public final int merge(final ContentRelationCreate nCr)
        throws InvalidContentException {

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
     * @param nCr
     *            Resource which value are to merge
     * @return number of merges
     * @throws InvalidContentException
     *             Thrown if name of MdRecord is not unique or other content is
     *             invalid
     */
    private int mergeMdRecords(final ContentRelationCreate nCr)
        throws InvalidContentException {

        int changes = 0;

        if (getMetadataRecords() == null && nCr.getMetadataRecords() != null) {

            // add all md-records
            for (MdRecordCreate mdRecordCreate : nCr.getMetadataRecords()) {
                MdRecordCreate mdRecord = mdRecordCreate;
                mdRecord.getRepositoryIndicator().setResourceIsNew(true);
                addMdRecord(mdRecord);
                changes++;
            }
        }
        else if (getMetadataRecords() != null
            && nCr.getMetadataRecords() == null) {

            // mark all md-records as deleted
            for (MdRecordCreate mdRecordCreate : getMetadataRecords()) {
                MdRecordCreate mdRecord = mdRecordCreate;
                mdRecord.getRepositoryIndicator().setResourceToDelete(true);
                changes++;
            }
        }
        else if ((getMetadataRecords() != null)
            && (nCr.getMetadataRecords() != null)) {

            // drop removed MdRecords
            Iterator<MdRecordCreate> it = getMetadataRecords().iterator();
            while (it.hasNext()) {
                MdRecordCreate mdRecord = it.next();
                String name = mdRecord.getName();

                MdRecordCreate newMdRecord = nCr.getMetadataRecord(name);
                if (newMdRecord == null) {
                    mdRecord.getRepositoryIndicator().setResourceToDelete(true);
                }

            }

            // compare existing, add new
            it = nCr.getMetadataRecords().iterator();
            while (it.hasNext()) {
                MdRecordCreate mdRecord = it.next();
                String name = mdRecord.getName();

                MdRecordCreate oldMdRecord = getMetadataRecord(name);
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
     * @param nCr
     *            Resource which value are to merge
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
                getProperties().setDescription(
                    nCr.getProperties().getDescription());
                changes++;
            }
        }
        else if (!getProperties().getDescription().equals(
            nCr.getProperties().getDescription())) {
            getProperties()
                .setDescription(nCr.getProperties().getDescription());
            changes++;
        }
        return changes;
    }

    /**
     * Check if the name is unique within the list of md-records.
     * 
     * @param records
     *            Vector with md-records.
     * @param name
     *            The name which is o check.
     * @throws InvalidContentException
     *             Thrown if the md-record name is not unique.
     */
    private static void checkUniqueName(
            final List<MdRecordCreate> records, final String name)
        throws InvalidContentException {

        for (MdRecordCreate record : records) {
            String recordName = record.getName();
            if (recordName.equals(name)) {

                String message =
                        "A md-record with the name '" + name
                                + "' occurs multiple times "
                                + "in the representation of a content relation.";
                LOG.error(message);
                throw new InvalidContentException(message);
            }
        }

    }

    /**
     * Create resource in Fedora.
     * 
     * @throws SystemException
     *             Thrown if internal error occurs.
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
        String foxml =
            ContentRelationFoXmlProvider.getInstance().getFoXml(this);
        FedoraUtility.getInstance().storeObjectInFedora(foxml, false);

        // creation /last-modification date
        String lastModificationDate =
            FedoraUtility.getInstance().getLastModificationDate(getObjid());
        getProperties().setCreationDate(lastModificationDate);
        getProperties().setLastModificationDate(lastModificationDate);
    }

    /**
     * Update resource in Fedora.
     * 
     * @throws SystemException
     *             Thrown if internal error occurs.
     */
    private void updateFedoraResource() throws SystemException {

        // update md-records
        if (getMetadataRecords() != null) {

            // drop removed MdRecords
            Iterator<MdRecordCreate> it = getMetadataRecords().iterator();
            while (it.hasNext()) {
                MdRecordCreate mdRecord = it.next();

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
     * @param sync
     *            Set true if TripleStore sync is to call. False otherwise.
     * @throws SystemException
     *             Thrown if updating Fedora repository or syncing TripleStore
     *             failed.
     */
    public final void persistProperties(final boolean sync) throws SystemException {

        String relsExt =
            ContentRelationFoXmlProvider.getInstance().getRelsExt(this);
        try {
            String lmd =
                FedoraUtility.getInstance().modifyDatastream(getObjid(),
                    Datastream.RELS_EXT_DATASTREAM,
                    Datastream.RELS_EXT_DATASTREAM_LABEL,
                    relsExt.getBytes(XmlUtility.CHARACTER_ENCODING), false);
            getProperties().setLastModificationDate(lmd);
        }
        catch (UnsupportedEncodingException e) {
            throw new SystemException(e);
     
        }
        if (sync) {
            FedoraUtility.getInstance().sync();
        }
    }

    /**
     * Mark datastream in repository as deleted.
     * 
     * @param mdRecord
     *            Md Record which is to delete
     * @throws FedoraSystemException
     *             Thrown if Fedora request failed.
     */
    private void deleteDatastream(final MdRecordCreate mdRecord)
        throws FedoraSystemException {

        // FedoraUtility.getInstance().setDatastreamState(getObjid(),
        // mdRecord.getName(), FedoraUtility.DATASTREAM_STATUS_DELETED);

        FedoraUtility.getInstance().purgeDatastream(getObjid(),
            mdRecord.getName(), null, null);

    }

    /**
     * Update MdRecord in repository (Fedora).
     * 
     * @param mdRecord
     *            The MdRecord which is to update.
     * @throws FedoraSystemException
     *             Thrown if Fedora request failed.
     * @throws WebserverSystemException
     *             Thrown if internal error occurs.
     */
    private void updateDataStream(final MdRecordCreate mdRecord)
        throws FedoraSystemException, WebserverSystemException {

        String[] altIds =
            { Datastream.METADATA_ALTERNATE_ID, mdRecord.getType(),
                mdRecord.getSchema() };

        byte[] content;
        try {
            content =
                mdRecord.getContent().getBytes(XmlUtility.CHARACTER_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new WebserverSystemException(e);
        }

        FedoraUtility.getInstance().modifyDatastream(getObjid(),
            mdRecord.getName(), mdRecord.getLabel(), mdRecord.getMimeType(),
            altIds, content, false);
        mdRecord.getRepositoryIndicator().setResourceChanged(false);
    }

    /**
     * Store a new create a datastream in the repository (Fedora).
     * 
     * @param mdRecord
     *            MdRecord
     * @throws FedoraSystemException
     *             Thrown if Fedora request failed.
     * @throws WebserverSystemException
     *             Thrown if internal error occurs.
     */
    private void createDatastream(final MdRecordCreate mdRecord)
        throws FedoraSystemException, WebserverSystemException {

        String[] altIds =
            { Datastream.METADATA_ALTERNATE_ID, mdRecord.getType(),
                mdRecord.getSchema() };

        byte[] content;
        try {
            content =
                mdRecord.getContent().getBytes(XmlUtility.CHARACTER_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new WebserverSystemException(e);
        }

        FedoraUtility.getInstance().addDatastream(getObjid(),
            mdRecord.getName(), altIds, mdRecord.getLabel(), false, content,
            false);
        mdRecord.getRepositoryIndicator().setResourceChanged(false);
        mdRecord.getRepositoryIndicator().setResourceIsNew(false);
    }

    /**
     * Creates and returns a copy of this object.
     * 
     * @return a clone of this instance.
     * @throws CloneNotSupportedException
     *             if the object's class does not support the Cloneable
     *             interface. Subclasses that override the clone method can also
     *             throw this exception to indicate that an instance cannot be
     *             cloned.
     */
    public final Object clone() throws CloneNotSupportedException {
        Object result = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(this);
            oos.close();
            InputStream fis = new ByteArrayInputStream(os.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(fis);
            result = ois.readObject();
            ois.close();
        }
        catch (Exception e) {
            CloneNotSupportedException cnse = new CloneNotSupportedException(e.toString()); // Ignore FindBugs
            cnse.initCause(e);
            throw cnse;
        }
        return result;
    }
}
