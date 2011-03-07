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
package de.escidoc.core.common.business.fedora.resources.create;

import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FileSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.factory.ItemFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Component for create method.
 * 
 * Attention! This is only a helper class for the transition to integrate this
 * functionality into the Component class.
 * 
 * 
 * @author SWA
 * 
 */
public class ComponentCreate extends GenericResourceCreate
    implements Callable<String> {

    private static final AppLogger LOG =
        new AppLogger(ComponentCreate.class.getName());

    private List<MdRecordCreate> mdRecords = null;

    private BinaryContent content = null;

    private EscidocIdProvider idProvider = null;

    private String dcXml = null;

    private ComponentProperties properties = null;

    /**
     * Set ItemProperties.
     * 
     * @param properties
     *            The properties of Item.
     * @throws WebserverSystemException
     */
    public void setProperties(final ComponentProperties properties) {

        this.properties = properties;
    }

    /**
     * Add a metadata record to the Component.
     * 
     * @param mdRecord
     *            New metadata record.
     */
    public void addMdRecord(final MdRecordCreate mdRecord) {

        if (this.mdRecords == null) {
            this.mdRecords = new ArrayList<MdRecordCreate>();
        }

        this.mdRecords.add(mdRecord);
    }

    /**
     * Get MetadataRecord by name.
     * 
     * @param name
     *            Name of MetadataRecord.
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
     * Add content to Component.
     * 
     * @param content
     *            New content of Component
     */
    public void setContent(final BinaryContent content) {

        this.content = content;
    }

    /**
     * Get content of Component.
     * 
     * @return Content of Component
     */
    public BinaryContent getContent() {

        return this.content;
    }

    /**
     * Injects the {@link EscidocIdProvider}.
     * 
     * @param idProvider
     *            The {@link EscidocIdProvider} to set.
     * 
     * @spring.property ref="escidoc.core.business.EscidocIdProvider"
     */
    public void setIdProvider(final EscidocIdProvider idProvider) {

        this.idProvider = idProvider;
    }

    /**
     * Persist Component to Repository.
     * 
     * @return FoXML
     * @throws SystemException
     *             Thrown if getting new objid from ID-Provider failed.
     * @throws IOException
     *             Thrown if preparing of properties, meta data record failed.
     */
    public String getFOXML() throws SystemException, IOException {

        // objid
        if (getObjid() == null) {
            setObjid(this.idProvider.getNextPid());
        }

        final Map<String, Object> valueMap = new HashMap<String, Object>();

        valueMap.put(XmlTemplateProvider.DC, getDC());
        valueMap.putAll(preparePropertiesValueMap());
        valueMap.putAll(getRelsExtNamespaceValues());
        valueMap.put(XmlTemplateProvider.MD_RECORDS,
            getMetadataRecordsMap(mdRecords));

        valueMap.putAll(getContentValues());
        // control template
        valueMap.put(XmlTemplateProvider.IN_CREATE, XmlTemplateProvider.TRUE);
        valueMap.put(XmlTemplateProvider.DS_VERSIONABLE,
            XmlTemplateProvider.TRUE);

        return ItemFoXmlProvider.getInstance().getComponentFoXml(valueMap);
    }

    /**
     * Get DC (mapped from default metadata). Value is cached.
     * 
     * Precondition: The objid has to be set before getDC() is called.
     * 
     * @return DC or null if default metadata is missing).
     * 
     * @throws WebserverSystemException
     *             Thrown if an error occurs during DC creation.
     * @throws EncodingSystemException
     *             Thrown if the conversion to default encoding failed.
     */
    public String getDC() throws WebserverSystemException,
        EncodingSystemException {

        if (this.dcXml == null) {
            final MdRecordCreate mdRecord =
                getMetadataRecord(
                    XmlTemplateProvider.DEFAULT_METADATA_FOR_DC_MAPPING);

            if (mdRecord != null) {
                try {
                    // no content model id for component dc-mapping, default
                    // mapping
                    // should be applied
                    this.dcXml = getDC(mdRecord, null);
                }
                catch (Exception e) {
                    LOG.info("DC mapping of to create resource failed. " + e);
                }
            }
        }

        return this.dcXml;
    }

    /**
     * Persist Component to Repository.
     * 
     * @param forceSync
     *            Set true to force a TripleStore sync.
     * @return Fedora objid.
     * 
     * @throws InvalidContentException
     *             Thrown if validation of Component structure failed.
     * @throws SystemException
     *             Thrown if getting new objid from ID-Provider or Fedora
     *             synchronization failed.
     * @throws IOException
     *             Thrown if preparing of properties, meta data record failed.
     */
    public String persist(final boolean forceSync) throws SystemException,
        InvalidContentException, IOException {

        validate();
        final String foxml = getFOXML();
        return FedoraUtility
            .getInstance().storeObjectInFedora(foxml, forceSync);
    }

    /**
     * Thread persist. TODO Change construct later!
     * 
     * @return objid of Component
     * @throws Exception
     *             Thrown if a Thread failed.
     */
    @Override
    public String call() throws Exception {

        persist(false);
        return getObjid();
    }

    /*
     * -------------------------------------------------------------------------
     * 
     * private methods
     * 
     * -------------------------------------------------------------------------
     */

    /**
     * Prepare values for FOXML Template Renderer (Velocity).
     * 
     * @return HashMap with template values.
     * @throws WebserverSystemException
     *             In case of an internal error.
     */
    private Map<String, String> preparePropertiesValueMap()
        throws WebserverSystemException {

        final Map<String, String> valueMap = new HashMap<String, String>();

        valueMap.put(XmlTemplateProvider.OBJID, getObjid());

        // add RELS-EXT values -------------------------------------------------
        valueMap.put(XmlTemplateProvider.FRAMEWORK_BUILD_NUMBER,
            getBuildNumber());

        // add RELS-EXT object properties
        valueMap.put(XmlTemplateProvider.CREATED_BY_ID, this.properties
            .getCreatedById());
        valueMap.put(XmlTemplateProvider.CREATED_BY_TITLE, this.properties
            .getCreatedByName());

        valueMap.put(XmlTemplateProvider.VALID_STATUS, this.properties
            .getValidStatus());

        valueMap.put(XmlTemplateProvider.MIME_TYPE, this.properties
            .getMimeType());

        valueMap.put(XmlTemplateProvider.VISIBILITY, this.properties
            .getVisibility());

        valueMap.put(XmlTemplateProvider.CONTENT_CATEGORY, this.properties
            .getContentCatagory());

        if (this.content.getDataLocation() == null) {
            if (this.content.getDataLocation() != null) {
                valueMap.put(XmlTemplateProvider.REF, this.content
                    .getDataLocation().toString());
                valueMap.put(XmlTemplateProvider.REF_TYPE, "URL");
            }
            else {
                this.content.setDataLocation(uploadBase64EncodedContent(
                    getContent().getContent(), "content " + getObjid(),
                    this.properties.getMimeType()));
                valueMap.put(XmlTemplateProvider.REF, this.content
                    .getDataLocation().toString());
                valueMap.put(XmlTemplateProvider.REF_TYPE, "URL");
            }

        }
        valueMap.put(XmlTemplateProvider.CONTROL_GROUP, this.content
            .getStorageType().getAbbreviation());

        return valueMap;

    }

    /**
     * @return HashMap where name space values are to add.
     */
    private Map<String, String> getRelsExtNamespaceValues() {

        final Map<String, String> values = new HashMap<String, String>();

        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS_PREFIX,
            de.escidoc.core.common.business.Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS,
            de.escidoc.core.common.business.Constants.PROPERTIES_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_VERSION_NS_PREFIX,
            de.escidoc.core.common.business.Constants.VERSION_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_VERSION_NS,
            de.escidoc.core.common.business.Constants.VERSION_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_RELEASE_NS_PREFIX,
            de.escidoc.core.common.business.Constants.RELEASE_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_RELEASE_NS,
            de.escidoc.core.common.business.Constants.RELEASE_NS_URI);

        values
            .put(
                XmlTemplateProvider.ESCIDOC_RESOURCE_NS_PREFIX,
                de.escidoc.core.common.business.Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_RESOURCE_NS,
            de.escidoc.core.common.business.Constants.RESOURCES_NS_URI);

        values
            .put(
                XmlTemplateProvider.ESCIDOC_RELATION_NS,
                de.escidoc.core.common.business.Constants.STRUCTURAL_RELATIONS_NS_URI);

        values
            .put(
                XmlTemplateProvider.ESCIDOC_RELATION_NS_PREFIX,
                de.escidoc.core.common.business.Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT);

        return values;
    }

    /**
     * Get values for Content.
     * 
     * @return HashMap of content values
     */
    private Map<String, String> getContentValues() {

        final Map<String, String> values = new HashMap<String, String>();

        try {
            values
                .put(
                    XmlTemplateProvider.CONTENT_CHECKSUM_ALGORITHM,
                    EscidocConfiguration
                        .getInstance()
                        .get(
                            EscidocConfiguration.ESCIDOC_CORE_OM_CONTENT_CHECKSUM_ALGORITHM,
                            "DISABLED"));
        }
        catch (IOException e) {
            // FIXME IOException will occur earlier if no configuration can be
            // found, if the retrieved value is not available the default value
            // will be set
            e.printStackTrace();
        }
        values.put(XmlTemplateProvider.REF, this.content
            .getDataLocation().toString());
        values.put(XmlTemplateProvider.REF_TYPE, "URL");

        return values;
    }

    /**
     * Upload the content (a base64 encoded byte stream) to the staging area.
     * 
     * @param contentAsString
     *            Base64 encoded byte stream.
     * @param fileName
     *            The file name.
     * @param mimeType
     *            The mime type of the content.
     * @return The url to the staging area where the resulting file is
     *         accessible.
     * @throws WebserverSystemException
     *             In case of an internal error during decoding or storing the
     *             content.
     */
    private String uploadBase64EncodedContent(
        final String contentAsString, final String fileName,
        final String mimeType) throws WebserverSystemException {
        final String uploadUrl;
        try {
            final byte[] streamContent = Base64.decodeBase64(contentAsString.getBytes());
            uploadUrl =
                Utility.getInstance().upload(streamContent, fileName, mimeType);
        }
        catch (final FileSystemException e) {
            throw new WebserverSystemException(
                "Error while uploading of content to the staging area. ", e);
        }

        return uploadUrl;
    }

    /**
     * Validates Component of logical structure.
     * 
     * @throws InvalidContentException
     *             Thrown if content not fits to storage type.
     */
    private void validate() throws InvalidContentException {

        // FIXME this is schema work !!!
        // check if storage type is selected
        if (this.content.getStorageType() == null) {
            throw new InvalidContentException("Attribute 'storage' is missing.");
        }

        // check if storage attributes fits to content
        if (((this.content.getStorageType() == StorageType.EXTERNAL_URL)
            || (this.content.getStorageType() == StorageType.EXTERNAL_MANAGED))
            && ((this.content.getDataLocation() == null)
                && (this.content.getContent() != null))) {
            throw new InvalidContentException(
                "Attribute 'storage' fits not to inline content.");
        }
    }

}
