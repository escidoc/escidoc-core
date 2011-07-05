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

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.FileSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.xml.factory.ItemFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import org.apache.commons.codec.binary.Base64;
import org.escidoc.core.services.fedora.FedoraServiceClient;
import org.escidoc.core.services.fedora.IngestPathParam;
import org.escidoc.core.services.fedora.IngestQueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Component for create method.
 * <p/>
 * Attention! This is only a helper class for the transition to integrate this functionality into the Component class.
 *
 * @author Steffen Wagner
 */
@Configurable
public class ComponentCreate extends GenericResourceCreate implements Callable<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentCreate.class);

    private List<MdRecordCreate> mdRecords;

    private BinaryContent content;

    private EscidocIdProvider idProvider;

    private String dcXml;

    private ComponentProperties properties;

    @Autowired
    @Qualifier("business.Utility")
    private Utility utility;

    @Autowired
    private FedoraServiceClient fedoraServiceClient;

    /**
     * Set ItemProperties.
     *
     * @param properties The properties of Item.
     */
    public void setProperties(final ComponentProperties properties) {

        this.properties = properties;
    }

    /**
     * Add a metadata record to the Component.
     *
     * @param mdRecord New metadata record.
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
     * Add content to Component.
     *
     * @param content New content of Component
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
     * @param idProvider The {@link EscidocIdProvider} to set.
     */
    public void setIdProvider(final EscidocIdProvider idProvider) {
        this.idProvider = idProvider;
    }

    /**
     * Persist Component to Repository.
     *
     * @return FoXML
     * @throws SystemException Thrown if getting new objid from ID-Provider failed.
     */
    public String getFOXML() throws SystemException {

        // objid
        if (getObjid() == null) {
            setObjid(this.idProvider.getNextPid());
        }

        final Map<String, Object> valueMap = new HashMap<String, Object>();

        valueMap.put(XmlTemplateProviderConstants.DC, getDC());
        valueMap.putAll(preparePropertiesValueMap());
        valueMap.putAll(getRelsExtNamespaceValues());
        valueMap.put(XmlTemplateProviderConstants.MD_RECORDS, getMetadataRecordsMap(this.mdRecords));

        valueMap.putAll(getContentValues());
        // control template
        valueMap.put(XmlTemplateProviderConstants.IN_CREATE, XmlTemplateProviderConstants.TRUE);
        valueMap.put(XmlTemplateProviderConstants.DS_VERSIONABLE, XmlTemplateProviderConstants.TRUE);

        return ItemFoXmlProvider.getInstance().getComponentFoXml(valueMap);
    }

    /**
     * Get DC (mapped from default metadata). Value is cached.
     * <p/>
     * Precondition: The objid has to be set before getDC() is called.
     *
     * @return DC or null if default metadata is missing).
     */
    public String getDC() {

        if (this.dcXml == null) {
            final MdRecordCreate mdRecord =
                getMetadataRecord(XmlTemplateProviderConstants.DEFAULT_METADATA_FOR_DC_MAPPING);

            if (mdRecord != null) {
                try {
                    // no content model id for component dc-mapping, default
                    // mapping
                    // should be applied
                    this.dcXml = getDC(mdRecord, null);
                }
                catch (final Exception e) {
                    LOGGER.info("DC mapping of to create resource failed. " + e);
                }
            }
        }

        return this.dcXml;
    }

    /**
     * Persist Component to Repository.
     *
     * @param forceSync Set true to force a TripleStore sync.
     * @return Fedora objid.
     * @throws InvalidContentException Thrown if validation of Component structure failed.
     * @throws SystemException         Thrown if getting new objid from ID-Provider or Fedora synchronization failed.
     */
    public String persist(final boolean forceSync) throws SystemException, InvalidContentException {

        validate();
        final String foxml = getFOXML();
        final IngestPathParam path = new IngestPathParam();
        final IngestQueryParam query = new IngestQueryParam();
        final String returnValue;
        try {
            returnValue = this.fedoraServiceClient.ingest(path, query, foxml);
        }
        catch (Exception e) {
            throw new FedoraSystemException("Ingest to Fedora failed.", e);
        }
        if (forceSync) {
            this.fedoraServiceClient.sync();
        }
        return returnValue;
    }

    /**
     * Thread persist. TODO Change construct later!
     *
     * @return objid of Component
     */
    @Override
    public String call() throws IOException, InvalidContentException, SystemException {

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
     * @throws WebserverSystemException In case of an internal error.
     */
    private Map<String, String> preparePropertiesValueMap() throws WebserverSystemException {

        final Map<String, String> valueMap = new HashMap<String, String>();

        valueMap.put(XmlTemplateProviderConstants.OBJID, getObjid());

        // add RELS-EXT values -------------------------------------------------
        valueMap.put(XmlTemplateProviderConstants.FRAMEWORK_BUILD_NUMBER, getBuildNumber());

        // add RELS-EXT object properties
        valueMap.put(XmlTemplateProviderConstants.CREATED_BY_ID, this.properties.getCreatedById());
        valueMap.put(XmlTemplateProviderConstants.CREATED_BY_TITLE, this.properties.getCreatedByName());

        valueMap.put(XmlTemplateProviderConstants.VALID_STATUS, this.properties.getValidStatus());

        valueMap.put(XmlTemplateProviderConstants.MIME_TYPE, this.properties.getMimeType());

        valueMap.put(XmlTemplateProviderConstants.VISIBILITY, this.properties.getVisibility());

        valueMap.put(XmlTemplateProviderConstants.CONTENT_CATEGORY, this.properties.getContentCatagory());

        if (this.content.getDataLocation() == null) {
            if (this.content.getDataLocation() != null) {
                valueMap.put(XmlTemplateProviderConstants.REF, this.content.getDataLocation().toString());
                valueMap.put(XmlTemplateProviderConstants.REF_TYPE, "URL");
            }
            else {
                this.content.setDataLocation(uploadBase64EncodedContent(getContent().getContent(), "content "
                    + getObjid(), this.properties.getMimeType()));
                valueMap.put(XmlTemplateProviderConstants.REF, this.content.getDataLocation().toString());
                valueMap.put(XmlTemplateProviderConstants.REF_TYPE, "URL");
            }

        }
        valueMap.put(XmlTemplateProviderConstants.CONTROL_GROUP, this.content.getStorageType().getAbbreviation());

        return valueMap;

    }

    /**
     * @return HashMap where name space values are to add.
     */
    private static Map<String, String> getRelsExtNamespaceValues() {

        final Map<String, String> values = new HashMap<String, String>();

        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);

        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_VERSION_NS_PREFIX, Constants.VERSION_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_VERSION_NS, Constants.VERSION_NS_URI);

        values.put(XmlTemplateProviderConstants.ESCIDOC_RELEASE_NS_PREFIX, Constants.RELEASE_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_RELEASE_NS, Constants.RELEASE_NS_URI);

        values.put(XmlTemplateProviderConstants.ESCIDOC_RESOURCE_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_RESOURCE_NS, Constants.RESOURCES_NS_URI);

        values.put(XmlTemplateProviderConstants.ESCIDOC_RELATION_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);

        values.put(XmlTemplateProviderConstants.ESCIDOC_RELATION_NS_PREFIX,
            Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT);

        return values;
    }

    /**
     * Get values for Content.
     *
     * @return HashMap of content values
     */
    private Map<String, String> getContentValues() {
        final Map<String, String> values = new HashMap<String, String>();
        values.put(XmlTemplateProviderConstants.CONTENT_CHECKSUM_ALGORITHM, EscidocConfiguration.getInstance().get(
            EscidocConfiguration.ESCIDOC_CORE_OM_CONTENT_CHECKSUM_ALGORITHM, "DISABLED"));
        values.put(XmlTemplateProviderConstants.REF, this.content.getDataLocation().toString());
        values.put(XmlTemplateProviderConstants.REF_TYPE, "URL");
        return values;
    }

    /**
     * Upload the content (a base64 encoded byte stream) to the staging area.
     *
     * @param contentAsString Base64 encoded byte stream.
     * @param fileName        The file name.
     * @param mimeType        The mime type of the content.
     * @return The url to the staging area where the resulting file is accessible.
     * @throws WebserverSystemException In case of an internal error during decoding or storing the content.
     */
    private String uploadBase64EncodedContent(final String contentAsString, final String fileName, final String mimeType)
        throws WebserverSystemException {
        final String uploadUrl;
        try {
            final byte[] streamContent = Base64.decodeBase64(contentAsString.getBytes());
            uploadUrl = this.utility.upload(streamContent, fileName, mimeType);
        }
        catch (final FileSystemException e) {
            throw new WebserverSystemException("Error while uploading of content to the staging area. ", e);
        }

        return uploadUrl;
    }

    /**
     * Validates Component of logical structure.
     *
     * @throws InvalidContentException Thrown if content not fits to storage type.
     */
    private void validate() throws InvalidContentException {

        // FIXME this is schema work !!!
        // check if storage type is selected
        if (this.content.getStorageType() == null) {
            throw new InvalidContentException("Attribute 'storage' is missing.");
        }

        // check if storage attributes fits to content
        if ((this.content.getStorageType() == StorageType.EXTERNAL_URL || this.content.getStorageType() == StorageType.EXTERNAL_MANAGED)
            && this.content.getDataLocation() == null && this.content.getContent() != null) {
            throw new InvalidContentException("Attribute 'storage' fits not to inline content.");
        }
    }

}
