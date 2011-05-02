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
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.CommonFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.FoXmlProvider;
import de.escidoc.core.common.util.xml.factory.ItemFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Item for create method.
 * <p/>
 * Attention! This is only a helper class for the transition to integrate this functionality into the Item class.
 *
 * @author Steffen Wagner
 */
@Configurable
public class ItemCreate extends GenericResourceCreate {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemCreate.class);

    private ItemProperties properties;

    private List<MdRecordCreate> mdRecords;

    private List<ComponentCreate> components;

    private List<ContentStreamCreate> contentStreams;

    private RelationsCreate relations = new RelationsCreate();

    private EscidocIdProvider idProvider;

    private String dcXml;

    // define pattern
    // taken from method handleFedoraUploadError
    // in order to make them static final
    private static final String ERROR_MSG_NO_HTTP_PROTOCOL =
        "The url has a wrong protocol." + " The protocol must be a http protocol.";

    private static final Pattern PATTERN_ERROR_GETTING =
        Pattern.compile("fedora.server.errors.GeneralException: Error getting", Pattern.CASE_INSENSITIVE);

    private static final Pattern PATTERN_MALFORMED_URL =
        Pattern.compile("fedora.server.errors.ObjectIntegrityException: " + "FOXML IO stream was bad : Malformed URL");

    /**
     * Set ItemProperties.
     *
     * @param properties The properties of Item.
     */
    public void setProperties(final ItemProperties properties) {

        this.properties = properties;
    }

    /**
     * Add a metadata record to the Component.
     *
     * @param mdRecord The new MetadataRecord.
     */
    public void addMdRecord(final MdRecordCreate mdRecord) {

        if (this.mdRecords == null) {
            this.mdRecords = new ArrayList<MdRecordCreate>();
        }

        this.mdRecords.add(mdRecord);
    }

    /**
     * Set Components.
     *
     * @param components Vector with new set of Components. Existing Components are removed.
     */
    public void setComponents(final List<ComponentCreate> components) {

        this.components = components;
    }

    /**
     * Add a Component to the list of Components.
     *
     * @param component New Component.
     */
    public void addComponent(final ComponentCreate component) {

        this.components.add(component);
    }

    /**
     * Delete Component.
     *
     * @param component Component to delete.
     */
    public void delComponent(final ComponentCreate component) {

        this.components.remove(component);
    }

    /**
     * Get Components of Item.
     *
     * @return Vector with all Components of Item.
     */
    public List<ComponentCreate> getComponents() {

        return this.components;
    }

    /**
     * Injects the {@link EscidocIdProvider}.
     *
     * @param idProvider The {@link EscidocIdProvider} to set.
     *                   <p/>
     *                   FIXME This Spring construct seams not to work.
     */
    public void setIdProvider(final EscidocIdProvider idProvider) {

        this.idProvider = idProvider;
    }

    /**
     * Persist whole Item to Repository and force TripleStore sync.
     * @throws de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException
     * @throws de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     * @throws de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    public void persist() throws SystemException, InvalidStatusException, MissingMdRecordException,
        FileNotFoundException, InvalidContentException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, MissingAttributeValueException, IntegritySystemException,
        FedoraSystemException {

        persist(true);
    }

    /**
     * Persist whole Item to Repository.
     *
     * @param forceSync Set true to force synchronous sync of TripleStore.
     * @throws SystemException Thrown if an unexpected error occurs
     * @throws de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    public void persist(final boolean forceSync) throws SystemException, FileNotFoundException,
        InvalidContentException, IntegritySystemException, FedoraSystemException {

        if (getProperties().getObjectProperties().getOrigin() == null) {
            persistComponents();
        }

        try {
            // Do not set fedora object id earlier. Otherwise consumes
            // an unsuccessful requests an objid (and time). This is redundant
            // if rollback is implemented and gives an unused objid back to
            // the objid pool.
            if (getObjid() == null) {
                setObjid(this.idProvider.getNextPid());
            }

            if (this.properties.getObjectProperties().getTitle() == null) {
                // if title is no set through DC,
                // update title (this is required because the title shall
                // contain the version number

                this.properties.getObjectProperties().setTitle("Item " + getObjid());
            }

            getProperties().getObjectProperties().setContextTitle(
                getTripleStoreUtility().getTitle(this.properties.getObjectProperties().getContextId()));
            getProperties().getObjectProperties().setContentModelTitle(
                getTripleStoreUtility().getTitle(this.properties.getObjectProperties().getContentModelId()));

            // serialize object without RELS-EXT and WOV to FOXML
            final String foxml = getMinimalFoXML();
            getFedoraUtility().storeObjectInFedora(foxml, false);

            // take timestamp and prepare RELS-EXT
            // String lmd =
            // FedoraUtility.getInstance().getLastModificationDate(getObjid());
            final String lmd = getLastModificationDateByWorkaround(getObjid());

            this.properties.getCurrentVersion().setDate(lmd);
            this.properties.getLatestVersion().setDate(lmd);
            if (this.properties.getLatestReleasedVersion() != null) {
                this.properties.getLatestReleasedVersion().setDate(lmd);
            }
            getFedoraUtility().addDatastream(getObjid(), FoXmlProvider.DATASTREAM_VERSION_HISTORY, new String[] {},
                "whole object versioning datastream", false, getWov().getBytes(XmlUtility.CHARACTER_ENCODING), false);

            // update RELS-EXT with timestamp
            final String relsExt = renderRelsExt();
            getFedoraUtility().modifyDatastream(getObjid(), Datastream.RELS_EXT_DATASTREAM,
                Datastream.RELS_EXT_DATASTREAM_LABEL, relsExt.getBytes(XmlUtility.CHARACTER_ENCODING), false);
            if (forceSync) {
                getFedoraUtility().sync();
            }

        }
        catch (final Exception e) {

            rollbackComponents();
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
     * @throws EncodingSystemException  Thrown if the conversion to default encoding failed.
     */
    public String getDC() throws WebserverSystemException, EncodingSystemException {

        if (this.dcXml == null) {

            final MdRecordCreate mdRecord = getMetadataRecord(XmlTemplateProvider.DEFAULT_METADATA_FOR_DC_MAPPING);
            if (mdRecord != null) {
                try {
                    this.dcXml = getDC(mdRecord, this.properties.getObjectProperties().getContentModelId());
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
     * Get Metadatarecord by name.
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
     * @param relations the relations to set
     */
    public void setRelations(final RelationsCreate relations) {
        this.relations = relations;
    }

    /**
     * @return the relations
     */
    public RelationsCreate getRelations() {
        return this.relations;
    }

    /**
     * @param contentStream the contentStreams to set
     */
    public void addContentStream(final ContentStreamCreate contentStream) {
        this.contentStreams.add(contentStream);
    }

    /**
     * @param contentStream the contentStreams to delete
     */
    public void delContentStreams(final ContentStreamCreate contentStream) {
        this.contentStreams.remove(contentStream);
    }

    /**
     * @param contentStreams the contentStreams to set
     */
    public void setContentStreams(final List<ContentStreamCreate> contentStreams) {
        this.contentStreams = contentStreams;
    }

    /**
     * @return the contentStreams
     */
    public List<ContentStreamCreate> getContentStreams() {
        return this.contentStreams;
    }

    /**
     * Get Properties of Item.
     *
     * @return ItemProperties
     */
    public ItemProperties getProperties() {
        return this.properties;
    }

    /*
     * -------------------------------------------------------------------------
     * 
     * private methods
     * 
     * -------------------------------------------------------------------------
     */

    /**
     * Purge just created Components if the whole Item could not be created.
     */
    private void rollbackComponents() {

        // starting rollback
        final List<ComponentCreate> comp = getComponents();
        if (comp != null) {
            for (int i = 0; i < comp.size(); i++) {
                try {
                    getFedoraUtility().deleteObject(getComponents().get(i).getObjid(), true);
                }
                catch (final Exception e2) {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn("Error on deleting object.");
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Error on deleting object.", e2);
                    }
                }
            }
        }
        // now the object it self (maybe it doesn't exists)
        try {
            getFedoraUtility().deleteObject(getObjid(), true);
        }
        catch (final Exception e2) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error on deleting object.");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Error on deleting object.", e2);
            }
        }
    }

    /**
     * Render an initial WOV.
     *
     * @return XML representation of Whole Object Versioning (WoV)
     * @throws WebserverSystemException Thrown if rendering failed.
     */
    private String getWov() throws WebserverSystemException {

        // control template
        final HashMap<String, String> templateValues = new HashMap<String, String>();

        templateValues.put(XmlTemplateProvider.OBJID, getObjidWithVersionSuffix());
        templateValues.put(XmlTemplateProvider.HREF, getHrefWithVersionSuffix());

        templateValues.put(XmlTemplateProvider.TITLE, this.properties.getObjectProperties().getTitle());
        templateValues.put(XmlTemplateProvider.VERSION_DATE, this.properties.getCurrentVersion().getDate());
        templateValues.put(XmlTemplateProvider.VERSION_NUMBER, this.properties.getCurrentVersion().getNumber());
        templateValues.put(XmlTemplateProvider.VERSION_STATUS, this.properties
            .getCurrentVersion().getStatus().toString());
        templateValues.put(XmlTemplateProvider.VERSION_COMMENT, this.properties.getCurrentVersion().getComment());

        templateValues.put(XmlTemplateProvider.VAR_NAMESPACE_PREFIX, Constants.WOV_NAMESPACE_PREFIX);
        templateValues.put(XmlTemplateProvider.VAR_NAMESPACE, Constants.WOV_NAMESPACE_URI);

        templateValues.put(XmlTemplateProvider.VERSION_NUMBER, this.properties.getCurrentVersion().getNumber());
        templateValues.put(XmlTemplateProvider.TIMESTAMP, this.properties.getCurrentVersion().getDate());

        // -------------------------------------

        templateValues.put(XmlTemplateProvider.VAR_AGENT_ID_TYPE, Constants.PREMIS_ID_TYPE_ESCIDOC);
        templateValues.put(XmlTemplateProvider.VAR_AGENT_BASE_URI, Constants.USER_ACCOUNT_URL_BASE);
        templateValues.put(XmlTemplateProvider.VAR_AGENT_ID_VALUE, UserContext.getId());
        templateValues.put(XmlTemplateProvider.VAR_AGENT_TITLE, UserContext.getRealName());

        // EVENT_XMLID EVENT_ID_TYPE EVENT_ID_VALUE
        templateValues.put(XmlTemplateProvider.VAR_EVENT_XMLID, "v1e" + System.currentTimeMillis());
        templateValues.put(XmlTemplateProvider.VAR_EVENT_ID_VALUE, Constants.ITEM_URL_BASE + getObjid() + "/resources/"
            + Elements.ELEMENT_WOV_VERSION_HISTORY + '#' + templateValues.get(XmlTemplateProvider.VAR_EVENT_XMLID));
        templateValues.put(XmlTemplateProvider.VAR_EVENT_ID_TYPE, Constants.PREMIS_ID_TYPE_URL_RELATIVE);
        templateValues.put(XmlTemplateProvider.VAR_OBJECT_ID_TYPE, Constants.PREMIS_ID_TYPE_ESCIDOC);
        templateValues.put(XmlTemplateProvider.VAR_OBJECT_ID_VALUE, getObjid());

        return CommonFoXmlProvider.getInstance().getWov(templateValues);
    }

    /**
     * Render Object FoXML with Components, ContentStreams and DC but with incomplete RELS-EXT and without WOV.
     * <p/>
     * WOV is excluded and RELS-EXT incomplete because of non existing timestamp (which is to add in a later step to the
     * object).
     * <p/>
     * It is important that the RELS-EXT datastream at least of the datastreams, because the create timestamp of the
     * RELS-EXT is used as creation timestamp of the resource. But the timestamps of the datastreams (even if they are
     * created with one request) could be differ. And it may happen, that the RELS-EXT is not created at least. If the
     * timestamp of RELS-EXT is older than other datastreams than are these other datastreams not part of the specified
     * version (because only these datastreams are part of the resource which are equal or older than the timestamp of
     * the version).
     * <p/>
     * Creating the RELS-EXT datastream afterward with a separate could be a performance issue.
     *
     * @return FoXML representation of Item.
     * @throws SystemException              Thrown if rendering of Item or sub-elements failed.
     * @throws UnsupportedEncodingException Thrown if conversion to default character set failed.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.EncodingSystemException
     */
    private String getMinimalFoXML() throws SystemException, UnsupportedEncodingException, EncodingSystemException,
        WebserverSystemException {

        final Map<String, Object> valueMap = new HashMap<String, Object>();

        valueMap.put(XmlTemplateProvider.OBJID, getObjid());

        valueMap.put(XmlTemplateProvider.TITLE, this.properties.getObjectProperties().getTitle());

        // RELS-EXT
        valueMap.putAll(getRelsExtNamespaceValues());
        valueMap.putAll(preparePropertiesValueMap());
        valueMap.put(XmlTemplateProvider.CONTENT_RELATIONS, prepareContentRelationsValueMap());

        // add Metadata as Map
        valueMap.put(XmlTemplateProvider.MD_RECORDS, getMetadataRecordsMap(this.mdRecords));

        // DC (inclusive mapping)----------------------------------------------
        final String dcXml = getDC();

        if (dcXml != null && dcXml.length() > 0) {
            valueMap.put(XmlTemplateProvider.DC, dcXml);
        }
        if (!valueMap.containsKey(XmlTemplateProvider.VAR_ORIGIN_OBJECT_ID)) {
            // Content-Streams
            valueMap.put(XmlTemplateProvider.CONTENT_STREAMS, getContentStreamsMap());
            // Components
            valueMap.put(XmlTemplateProvider.COMPONENTS, getComponentIds());
        }

        // add Content-model-specific
        valueMap.put(XmlTemplateProvider.CONTENT_MODEL_SPECIFIC, this.properties.getContentModelSpecific());

        valueMap.put(XmlTemplateProvider.IN_CREATE, XmlTemplateProvider.TRUE);

        return ItemFoXmlProvider.getInstance().getItemFoXml(valueMap);
    }

    /**
     * Compile all values for RELS-EXT and render XML representation.
     *
     * @return RELS-EXT XML snippet
     * @throws SystemException Thrown if renderer failed.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    private String renderRelsExt() throws SystemException, WebserverSystemException {

        final Map<String, Object> valueMap = new HashMap<String, Object>();

        valueMap.put(XmlTemplateProvider.OBJID, getObjid());

        valueMap.putAll(getRelsExtNamespaceValues());
        valueMap.putAll(preparePropertiesValueMap());

        valueMap.put(XmlTemplateProvider.CONTENT_RELATIONS, prepareContentRelationsValueMap());

        if (!valueMap.containsKey(XmlTemplateProvider.VAR_ORIGIN_OBJECT_ID)) {
            valueMap.put(XmlTemplateProvider.COMPONENTS, getComponentIds());
        }

        return ItemFoXmlProvider.getInstance().getItemRelsExt(valueMap);
    }

    /**
     * Persist all Components of the Item.
     * @throws de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException
     * @throws de.escidoc.core.common.exceptions.system.SystemException
     * @throws de.escidoc.core.common.exceptions.application.invalid.InvalidContentException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     * @throws de.escidoc.core.common.exceptions.system.IntegritySystemException
     */
    private void persistComponents() throws SystemException, FileNotFoundException, InvalidContentException,
        IntegritySystemException, FedoraSystemException {

        int i = 0;
        List<String> componentIds = null;

        try {
            if (this.components != null) {
                /*
                 * FIXME upload method to staging service is not thread safe!
                 */
                componentIds = new ArrayList<String>();
                for (i = 0; i < this.components.size(); i++) {
                    // old unthreaded - works
                    final ComponentCreate component = this.components.get(i);
                    component.setIdProvider(this.idProvider);
                    final String id = component.persist(false);
                    componentIds.add(id);
                }
            }
        }
        catch (final InvalidContentException fne) {
            rollbackCreate(componentIds);
            throw fne;
        }
        catch (final FedoraSystemException e) {

            final Pattern patternInvalidFoXml = Pattern.compile("fedora.server.errors.ObjectValidityException");

            final Matcher invalidFoxml = patternInvalidFoXml.matcher(e.getCause().getMessage());

            if (invalidFoxml.find()) {
                throw new IntegritySystemException(e);
            }

            if (this.components.get(i).getContent().getDataLocation() != null) {
                handleFedoraUploadError(this.components.get(i).getContent().getDataLocation().toString(), e);
            }
        }
        catch (final Exception e) {
            rollbackCreate(componentIds);
            throw new SystemException("Failure during create Fedora object.", e);
        }
    }

    /**
     * Prepare values for FOXML Template Renderer (Velocity).
     *
     * @return HashMap with template values.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    private Map<String, String> preparePropertiesValueMap() throws WebserverSystemException {

        final Map<String, String> valueMap = new HashMap<String, String>();

        // add RELS-EXT values -------------------------------------------------
        valueMap.put(XmlTemplateProvider.FRAMEWORK_BUILD_NUMBER, getBuildNumber());

        // add RELS-EXT object properties
        valueMap.put(XmlTemplateProvider.CREATED_BY_ID, this.properties.getCurrentVersion().getCreatedById());
        valueMap.put(XmlTemplateProvider.CREATED_BY_TITLE, this.properties.getCurrentVersion().getCreatedByName());

        valueMap.put(XmlTemplateProvider.MODIFIED_BY_ID, this.properties.getCurrentVersion().getModifiedById());
        valueMap.put(XmlTemplateProvider.MODIFIED_BY_TITLE, this.properties.getCurrentVersion().getCreatedByName());

        valueMap.put(XmlTemplateProvider.PUBLIC_STATUS, this.properties.getObjectProperties().getStatus().toString());
        valueMap.put(XmlTemplateProvider.PUBLIC_STATUS_COMMENT, this.properties
            .getObjectProperties().getStatusComment());

        valueMap.put(XmlTemplateProvider.OBJECT_PID, this.properties.getObjectProperties().getPid());

        valueMap.put(XmlTemplateProvider.CONTEXT_ID, this.properties.getObjectProperties().getContextId());
        valueMap.put(XmlTemplateProvider.CONTEXT_TITLE, this.properties.getObjectProperties().getContextTitle());

        valueMap.put(XmlTemplateProvider.CONTENT_MODEL_ID, this.properties.getObjectProperties().getContentModelId());
        valueMap.put(XmlTemplateProvider.CONTENT_MODEL_TITLE, this.properties
            .getObjectProperties().getContentModelTitle());

        // add RELS-EXT current version values
        // version pid currently not supported for create
        // valueMap.put(XmlTemplateProvider.VERSION_PID, this.properties
        // .getCurrentVersion().getPid());

        valueMap.put(XmlTemplateProvider.VERSION_NUMBER, this.properties.getCurrentVersion().getNumber());
        String date = this.properties.getCurrentVersion().getDate();
        if (date == null) {
            date = "---";
        }
        valueMap.put(XmlTemplateProvider.VERSION_DATE, date);
        valueMap.put(XmlTemplateProvider.VERSION_STATUS, this.properties.getCurrentVersion().getStatus().toString());
        valueMap.put(XmlTemplateProvider.VERSION_COMMENT, this.properties.getCurrentVersion().getComment());

        // add RELS-EXT latest version values
        valueMap.put(XmlTemplateProvider.LATEST_VERSION_PID, this.properties.getLatestVersion().getPid());

        valueMap.put(XmlTemplateProvider.LATEST_VERSION_NUMBER, this.properties.getLatestVersion().getNumber());
        valueMap.put(XmlTemplateProvider.LATEST_VERSION_DATE, this.properties.getLatestVersion().getDate());
        valueMap.put(XmlTemplateProvider.LATEST_VERSION_STATUS, this.properties
            .getLatestVersion().getStatus().toString());
        valueMap.put(XmlTemplateProvider.LATEST_VERSION_COMMENT, this.properties.getLatestVersion().getComment());

        // in the case of a surrogate
        final String origin = getProperties().getObjectProperties().getOrigin();
        final String originObjectId = getProperties().getObjectProperties().getOriginObjectId();
        final String originVersionId = getProperties().getObjectProperties().getOriginVersionId();
        if (origin != null) {
            valueMap.put("originObjectId", originObjectId);
            if (originVersionId != null) {
                valueMap.put(XmlTemplateProvider.VAR_ORIGIN_VERSION_ID, originVersionId);
            }
        }

        // add RELS-EXT latest-released-version properties
        if (this.properties.getLatestReleasedVersion() != null) {
            valueMap.put(XmlTemplateProvider.LATEST_RELEASE_NUMBER, this.properties
                .getLatestReleasedVersion().getNumber());

            // latest release date
            if (this.properties.getLatestReleasedVersion().getDate() != null) {
                valueMap.put(XmlTemplateProvider.LATEST_RELEASE_DATE, this.properties
                    .getLatestReleasedVersion().getDate());
            }
            else {
                valueMap.put(XmlTemplateProvider.LATEST_RELEASE_DATE, "---");
            }

            // latest release pid
            if (this.properties.getLatestReleasedVersion().getPid() != null) {
                valueMap.put(XmlTemplateProvider.LATEST_RELEASE_PID, this.properties
                    .getLatestReleasedVersion().getPid());
            }
        }

        return valueMap;
    }

    /**
     * Getting Namespaces for RelsExt as Map.
     *
     * @return HashMap with namespace values for XML representation.
     */
    private static Map<String, String> getRelsExtNamespaceValues() {

        final Map<String, String> values = new HashMap<String, String>();

        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_VERSION_NS_PREFIX, Constants.VERSION_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_VERSION_NS, Constants.VERSION_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_RELEASE_NS_PREFIX, Constants.RELEASE_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_RELEASE_NS, Constants.RELEASE_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_RESOURCE_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);

        values.put(XmlTemplateProvider.ESCIDOC_RESOURCE_NS, Constants.RESOURCES_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_RELATION_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_RELATION_NS_PREFIX, Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT);

        values.put(XmlTemplateProvider.ESCIDOC_ORIGIN_NS, Constants.ORIGIN_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_ORIGIN_NS_PREFIX, Constants.ORIGIN_NS_PREFIX);

        return values;
    }

    /**
     * Try a rollback by removing created Resources.
     *
     * @param componentIds Fedora objid of resources which are to purge.
     */
    private void rollbackCreate(final Iterable<String> componentIds) {
        for (final String componentId : componentIds) {
            LOGGER.debug("Rollback Component create (" + componentId + ").");
            try {
                getFedoraUtility().deleteObject(componentId, false);
            }
            catch (final Exception e2) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Purging of Fedora Object (" + componentId + ") failed.");
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Purging of Fedora Object (" + componentId + ") failed.", e2);
                }
            }
        }
    }

    /**
     * TODO remove this method if Fedora has fixed the timestamp bug (Fedora 3.0 and 3.1 do not update the object
     * timestamp during create. It happens that timestamps of streams are newer than the object timestamp. This failure
     * not occurs during a later update.).
     *
     * @param objid The id of the Fedora Object.
     * @return LastModificationDate of the Object (with workaround for Fedora bug).
     * @throws FedoraSystemException Thrown if request to Fedora failed.
     */
    private String getLastModificationDateByWorkaround(final String objid) throws FedoraSystemException {
        // Work around for Fedora30 bug APIM.getDatastreams()
        final org.fcrepo.server.types.gen.Datastream relsExtInfo =
            getFedoraUtility().getDatastreamInformation(objid, Datastream.RELS_EXT_DATASTREAM, null);
        return relsExtInfo.getCreateDate();
    }

    /**
     * Get objid with version suffix 123:1.
     *
     * @return objid with version suffix.
     */
    private String getObjidWithVersionSuffix() {

        return getObjid() + ':' + this.properties.getCurrentVersion().getNumber();
    }

    /**
     * Get href with version suffix.
     *
     * @return Put on Version suffix
     */
    @Deprecated
    /*
     * Href shouldn't be part of the Item
     */
    private String getHrefWithVersionSuffix() {

        return Constants.ITEM_URL_BASE + getObjid() + ':' + this.properties.getCurrentVersion().getNumber();
    }

    /**
     * Get a vector with all ids of the Components.
     *
     * @return Component objid
     */
    private List<String> getComponentIds() {

        if (this.components != null) {
            final Iterator<ComponentCreate> it = this.components.iterator();
            final List<String> componentIDs = new ArrayList<String>();
            while (it.hasNext()) {
                componentIDs.add(it.next().getObjid());
            }
            return componentIDs;
        }
        return null;
    }

    /**
     * Handle a Fedora Exception thrown while uploading content.
     *
     * @param url The URL.
     * @param e   The Fedora Exception.
     * @throws FileNotFoundException Thrown if the resource ref of Fedora content is not accessible.
     * @throws FedoraSystemException Thrown if the reason for the Fedora Exception was not an unaccible content resource
     *                               (file).
     */
    private static void handleFedoraUploadError(final String url, final FedoraSystemException e)
        throws FileNotFoundException, FedoraSystemException {

        final Matcher matcherErrorGetting = PATTERN_ERROR_GETTING.matcher(e.getMessage());
        final Matcher matcherMalformedUrl = PATTERN_MALFORMED_URL.matcher(e.getMessage());

        if (matcherErrorGetting.find() || matcherMalformedUrl.find()) {
            throw new FileNotFoundException("Error getting content from " + url, e);
        }
        if (!(url.startsWith("http://") || url.startsWith("https://"))) {
            throw new FileNotFoundException(ERROR_MSG_NO_HTTP_PROTOCOL);
        }
        // TODO: Reuse HttpClient
        final HttpClient client = new DefaultHttpClient();
        try {
            /*
             * FIXME (SWA) This whole Exception handling is a little bit crud.
             * Nevertheless, if this construct survives the HTTP connection test
             * should be handles via ConnectionUtility!
             */
            final HttpUriRequest httpMessage = new HttpGet(url);
            final HttpResponse response = client.execute(httpMessage);
            final int resultCode = response.getStatusLine().getStatusCode();

            if (resultCode != HttpServletResponse.SC_OK) {
                throw new FileNotFoundException("Bad request. [" + response.getStatusLine() + ", " + url + ']');
            }
        }
        catch (final Exception e1) {
            throw new FileNotFoundException("Error getting content from " + url, e1);
        }
        finally {
            client.getConnectionManager().shutdown();
        }
        throw e;
    }

    /**
     * Create Vector/HashMap structure to transfer date to velocity.
     *
     * @return Vector with HashMaps of ContentRelation values.
     */
    private List<HashMap<String, String>> prepareContentRelationsValueMap() {

        List<HashMap<String, String>> crel = null;

        final Iterator<RelationCreate> it = this.relations.iterator();
        if (it != null) {
            if (it.hasNext()) {
                crel = new ArrayList<HashMap<String, String>>();
            }

            while (it.hasNext()) {
                final RelationCreate rel = it.next();

                final HashMap<String, String> relation = new HashMap<String, String>();
                relation.put(XmlTemplateProvider.PREDICATE, rel.getPredicate());
                relation.put(XmlTemplateProvider.PREDICATE_NS, rel.getPredicateNs());
                relation.put(XmlTemplateProvider.OBJID, rel.getTarget());

                crel.add(relation);
            }
        }

        return crel;
    }

    /**
     * Get ContentStreams Vector/HashMap Structure for Velocity.
     *
     * @return Vector which contains a HashMap with all values for each ContentStream. HashMap keys are keys for
     *         Velocity template.
     */
    private List<HashMap<String, String>> getContentStreamsMap() {
        /*
         * (has to move in an own renderer class, I know. Please feel free to
         * create class infrastructure.).
         */

        if (this.contentStreams == null) {
            return null;
        }

        final List<HashMap<String, String>> contStreams = new ArrayList<HashMap<String, String>>();

        for (final ContentStreamCreate contentStream : this.contentStreams) {
            final HashMap<String, String> valueMap = new HashMap<String, String>();
            valueMap.put(XmlTemplateProvider.CONTROL_GROUP, contentStream
                .getContent().getStorageType().getAbbreviation());
            valueMap.put(XmlTemplateProvider.VAR_ID, contentStream.getName());
            valueMap.put(XmlTemplateProvider.VAR_VERSIONABLE, XmlTemplateProvider.TRUE);
            valueMap.put(XmlTemplateProvider.VAR_ALT_IDS, "content-stream");
            valueMap.put(XmlTemplateProvider.MIME_TYPE, contentStream.getMimeType());

            // FIXME using title as label seams inconsistent
            valueMap.put(XmlTemplateProvider.VAR_LABEL, contentStream.getTitle());

            if (contentStream.getContent().getDataLocation() != null) {
                valueMap.put(XmlTemplateProvider.VAR_URL, contentStream.getContent().getDataLocation().toString());
            }
            valueMap.put(XmlTemplateProvider.VAR_CONTENT, contentStream.getContent().getContent());

            contStreams.add(valueMap);
        }

        return contStreams;
    }
}
