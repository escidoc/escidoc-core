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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import org.escidoc.core.services.fedora.AddDatastreamPathParam;
import org.escidoc.core.services.fedora.AddDatastreamQueryParam;
import org.escidoc.core.services.fedora.FedoraServiceClient;
import org.escidoc.core.services.fedora.ModifiyDatastreamPathParam;
import org.escidoc.core.services.fedora.ModifyDatastreamQueryParam;
import org.esidoc.core.utils.io.Stream;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.CommonFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.ContentModelFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.FoXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;

/**
 * Content Model for create method.
 * <p/>
 * Attention! This is only a helper class for the transition to integrate this functionality into the ContentModel
 * class.
 * 
 * @author Frank Schwichtenberg
 */
@Configurable
public class ContentModelCreate extends GenericResourceCreate {

    public final static Logger LOG = LoggerFactory.getLogger(ContentModelCreate.class);

    @Autowired
    private FedoraServiceClient fedoraServiceClient;

    @Autowired
    @Qualifier("escidoc.core.business.FedoraUtility")
    private FedoraUtility fedoraUtility;

    private ContentModelProperties properties;

    private List<MdRecordDefinitionCreate> mdRecordDefinitions;

    private List<ContentStreamCreate> contentStreams;

    private EscidocIdProvider idProvider;

    private Map<String, ResourceDefinitionCreate> resourceDefinitions;

    public Map<String, ResourceDefinitionCreate> getResourceDefinitions() {
        return this.resourceDefinitions;
    }

    /**
     * Set Content Model properties.
     * 
     * @param properties
     *            The properties of Content Model.
     */
    public void setProperties(final ContentModelProperties properties) {

        this.properties = properties;
    }

    /**
     * Get Properties of ContentModel.
     * 
     * @return ContentModelProperties
     */
    public ContentModelProperties getProperties() {
        return this.properties;
    }

    /**
     * Set metadata record definitions.
     * 
     * @param mdRecordDefinitions
     */
    public void setMdRecordDefinitions(final List<MdRecordDefinitionCreate> mdRecordDefinitions) {

        this.mdRecordDefinitions = mdRecordDefinitions;
    }

    /**
     * Set resource definitions.
     * 
     * @param resourceDefinitions
     */
    public void setResourceDefinitions(final Map<String, ResourceDefinitionCreate> resourceDefinitions) {

        this.resourceDefinitions = resourceDefinitions;
    }

    /**
     * Get vector of all MdRecords.
     * 
     * @return All MdRecords.
     */
    public List<MdRecordDefinitionCreate> getMetadataRecordDefinitions() {
        return this.mdRecordDefinitions;
    }

    /**
     * Injects the {@link EscidocIdProvider}.
     * 
     * @param idProvider
     *            The {@link EscidocIdProvider} to set.
     *            <p/>
     *            FIXME This Spring construct seams not to work.
     */
    public void setIdProvider(final EscidocIdProvider idProvider) {

        this.idProvider = idProvider;
    }

    /**
     * Persist whole ContentModel to Repository.
     * 
     * @param forceSync
     *            Set true to force synchronous sync of TripleStore.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     * @throws de.escidoc.core.common.exceptions.system.FedoraSystemException
     */
    public void persist(final boolean forceSync) throws WebserverSystemException, FedoraSystemException {

        // FIXME persist behavior

        // Do not set fedora object id earlier. Otherwise consumes
        // an unsuccessful requests an objid (and time). This is redundant
        // if rollback is implemented and gives an unused objid back to
        // the objid pool.
        if (getObjid() == null) {
            try {
                setObjid(this.idProvider.getNextPid());
            }
            catch (final SystemException e) {
                // FIXME should be catched earlier (FRS)
                throw new WebserverSystemException(e);
            }
        }

        // create service definitions and deployments
        if (this.resourceDefinitions != null) {
            for (final ResourceDefinitionCreate resourceDefinitionCreate : this.resourceDefinitions.values()) {
                final String sdefFoxml = getSDefFoXML(resourceDefinitionCreate);
                this.fedoraUtility.storeObjectInFedora(sdefFoxml, false);
                final String sdepFoxml = getSDepFoXML(resourceDefinitionCreate);
                this.fedoraUtility.storeObjectInFedora(sdepFoxml, false);
            }
        }

        // serialize object without RELS-EXT and WOV to FOXML
        final String foxml = getMinimalFoXML();
        this.fedoraUtility.storeObjectInFedora(foxml, false);

        // take timestamp and prepare RELS-EXT
        final DateTime lmd = getLastModificationDateByWorkaround(getObjid());

        this.properties.getCurrentVersion().setDate(lmd);
        this.properties.getLatestVersion().setDate(lmd);
        if (this.properties.getLatestReleasedVersion() != null) {
            this.properties.getLatestReleasedVersion().setDate(lmd);
        }

        final AddDatastreamPathParam addPath =
            new AddDatastreamPathParam(getObjid(), FoXmlProvider.DATASTREAM_VERSION_HISTORY);
        final AddDatastreamQueryParam addQuery = new AddDatastreamQueryParam();
        addQuery.setDsLabel("whole object versioning datastream");
        addQuery.setVersionable(false);
        final Stream addStream = new Stream();
        try {
            addStream.write(getWov().getBytes(XmlUtility.CHARACTER_ENCODING));
            addStream.lock();
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
        this.fedoraServiceClient.addDatastream(addPath, addQuery, addStream);

        // update RELS-EXT with timestamp
        final String relsExt = renderRelsExt();
        final ModifiyDatastreamPathParam path =
            new ModifiyDatastreamPathParam(getObjid(), Datastream.RELS_EXT_DATASTREAM);
        final ModifyDatastreamQueryParam query = new ModifyDatastreamQueryParam();
        query.setDsLabel(Datastream.RELS_EXT_DATASTREAM_LABEL);
        final Stream stream = new Stream();
        try {
            stream.write(relsExt.getBytes(XmlUtility.CHARACTER_ENCODING));
        }
        catch (final UnsupportedEncodingException e) {
            throw new WebserverSystemException(e);
        }
        catch (final IOException e) {
            throw new WebserverSystemException(e);
        }
        this.fedoraServiceClient.modifyDatastream(path, query, stream);

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

    /**
     * @param contentStreams
     *            the contentStreams to set
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
     * Render an initial WOV.
     * 
     * @return XML representation of Whole Object Versioning (WoV)
     * @throws WebserverSystemException
     *             Thrown if rendering failed.
     */
    private String getWov() throws WebserverSystemException {

        // control template
        final HashMap<String, String> templateValues = new HashMap<String, String>();

        templateValues.put(XmlTemplateProvider.OBJID, getObjidWithVersionSuffix());
        templateValues.put(XmlTemplateProvider.HREF, getHrefWithVersionSuffix());

        templateValues.put(XmlTemplateProvider.TITLE, this.properties.getObjectProperties().getTitle());
        //templateValues.put(XmlTemplateProvider.VERSION_DATE, this.properties.getCurrentVersion().getDate().toString());
        //templateValues.put(XmlTemplateProvider.TIMESTAMP, this.properties.getCurrentVersion().getDate().toString());
        DateTime date = this.properties.getCurrentVersion().getDate();
        if (date == null) {
            templateValues.put(XmlTemplateProvider.VERSION_DATE, null);
            templateValues.put(XmlTemplateProvider.TIMESTAMP, null);
        }
        else {
            templateValues.put(XmlTemplateProvider.VERSION_DATE, date.toString());
            templateValues.put(XmlTemplateProvider.TIMESTAMP, date.toString());
        }

        templateValues.put(XmlTemplateProvider.VERSION_NUMBER, this.properties.getCurrentVersion().getNumber());
        templateValues.put(XmlTemplateProvider.VERSION_STATUS, this.properties
            .getCurrentVersion().getStatus().toString());
        templateValues.put(XmlTemplateProvider.VERSION_COMMENT, this.properties.getCurrentVersion().getComment());

        templateValues.put(XmlTemplateProvider.VAR_NAMESPACE_PREFIX, Constants.WOV_NAMESPACE_PREFIX);
        templateValues.put(XmlTemplateProvider.VAR_NAMESPACE, Constants.WOV_NAMESPACE_URI);

        templateValues.put(XmlTemplateProvider.VERSION_NUMBER, this.properties.getCurrentVersion().getNumber());

        // -------------------------------------

        templateValues.put(XmlTemplateProvider.VAR_AGENT_ID_TYPE, Constants.PREMIS_ID_TYPE_ESCIDOC);
        templateValues.put(XmlTemplateProvider.VAR_AGENT_BASE_URI, Constants.USER_ACCOUNT_URL_BASE);
        templateValues.put(XmlTemplateProvider.VAR_AGENT_ID_VALUE, UserContext.getId());
        templateValues.put(XmlTemplateProvider.VAR_AGENT_TITLE, UserContext.getRealName());

        // EVENT_XMLID EVENT_ID_TYPE EVENT_ID_VALUE
        templateValues.put(XmlTemplateProvider.VAR_EVENT_XMLID, "v1e" + System.currentTimeMillis());
        templateValues.put(XmlTemplateProvider.VAR_EVENT_ID_VALUE, templateValues.get(XmlTemplateProvider.HREF) + '/'
            + Elements.ELEMENT_WOV_VERSION_HISTORY + '#' + templateValues.get(XmlTemplateProvider.VAR_EVENT_XMLID));
        templateValues.put(XmlTemplateProvider.VAR_EVENT_ID_TYPE, Constants.PREMIS_ID_TYPE_URL_RELATIVE);
        templateValues.put(XmlTemplateProvider.VAR_OBJECT_ID_TYPE, Constants.PREMIS_ID_TYPE_ESCIDOC);
        templateValues.put(XmlTemplateProvider.VAR_OBJECT_ID_VALUE, getObjid());

        return CommonFoXmlProvider.getInstance().getWov(templateValues);
    }

    /**
     * Render Object FoXML with Components, ContentStreams and DC but with incomplete RELS-EXT and wihtout WOV.
     * <p/>
     * WOV is excluded and RELS-EXT incomplete because of non existing timestamp (which is to add in a later step to the
     * object).
     * 
     * @return FoXML representation of ContentModel.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    private String getMinimalFoXML() throws WebserverSystemException {

        final Map<String, Object> valueMap = new HashMap<String, Object>();

        valueMap.put(XmlTemplateProvider.OBJID, getObjid());
        valueMap.put(XmlTemplateProvider.OBJID_UNDERSCORE, getObjid().replaceAll(":", Constants.COLON_REPLACEMENT_PID));

        valueMap.put(XmlTemplateProvider.TITLE, this.properties.getObjectProperties().getTitle());

        valueMap.put(XmlTemplateProvider.DESCRIPTION, this.properties.getObjectProperties().getDescription());

        // RELS-EXT
        valueMap.putAll(getRelsExtNamespaceValues());
        valueMap.putAll(preparePropertiesValueMap());

        // add service definitions
        valueMap.put("BEHAVIORS", getResourceDefinitions());

        // in order to create DS-COMPOSITE and *_XSD datastreams
        // add md-record definitions
        valueMap.put("MD_RECORDS", getMetadataRecordDefinitions());

        // Content-Streams
        valueMap.put(XmlTemplateProvider.CONTENT_STREAMS, getContentStreamsMap());

        valueMap.put(XmlTemplateProvider.IN_CREATE, XmlTemplateProvider.TRUE);

        return ContentModelFoXmlProvider.getInstance().getContentModelFoXml(valueMap);
    }

    /**
     * Render service definition FoXML.
     * 
     * @param resourceDefinition
     *            The resource definition create object.
     * @return FoXML representation of service definition.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    private String getSDefFoXML(final ResourceDefinitionCreate resourceDefinition) throws WebserverSystemException {
        final Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.putAll(getBehaviorValues(resourceDefinition));
        return ContentModelFoXmlProvider.getInstance().getServiceDefinitionFoXml(valueMap);
    }

    /**
     * Render service deployment FoXML.
     * 
     * @param resourceDefinition
     *            The resource definition create object.
     * @return FoXML representation of service deployment.
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    private String getSDepFoXML(final ResourceDefinitionCreate resourceDefinition) throws WebserverSystemException {
        final Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.putAll(getBehaviorValues(resourceDefinition));
        return ContentModelFoXmlProvider.getInstance().getServiceDeploymentFoXml(valueMap);
    }

    private Map<String, Object> getBehaviorValues(final ResourceDefinitionCreate resourceDefinition) {
        final Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(XmlTemplateProvider.BEHAVIOR_CONTENT_MODEL_ID, getObjid());
        valueMap.put(XmlTemplateProvider.BEHAVIOR_CONTENT_MODEL_ID_UNDERSCORE, getObjid().replaceAll(":",
            Constants.COLON_REPLACEMENT_PID));

        valueMap.put(XmlTemplateProvider.BEHAVIOR_OPERATION_NAME, resourceDefinition.getName());
        valueMap.put(XmlTemplateProvider.BEHAVIOR_TRANSFORM_MD, resourceDefinition.getMdRecordName());
        valueMap.put(XmlTemplateProvider.BEHAVIOR_XSLT_HREF, resourceDefinition.getXsltHref());
        return valueMap;
    }

    /**
     * Compile all values for RELS-EXT and render XML representation.
     * 
     * @return RELS-EXT XML snippet
     * @throws WebserverSystemException
     *             Thrown if renderer failed.
     */
    private String renderRelsExt() throws WebserverSystemException {

        final Map<String, Object> valueMap = new HashMap<String, Object>();

        valueMap.put(XmlTemplateProvider.OBJID, getObjid());
        valueMap.put(XmlTemplateProvider.OBJID_UNDERSCORE, getObjid().replaceAll(":", Constants.COLON_REPLACEMENT_PID));

        valueMap.putAll(getRelsExtNamespaceValues());
        valueMap.putAll(preparePropertiesValueMap());
        // add service definitions
        valueMap.put("BEHAVIORS", getResourceDefinitions());

        return ContentModelFoXmlProvider.getInstance().getContentModelRelsExt(valueMap);
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
        String date = this.properties.getCurrentVersion().getDate().toString();
        if (date == null) {
            date = "---";
        }
        valueMap.put(XmlTemplateProvider.VERSION_DATE, date);
        valueMap.put(XmlTemplateProvider.VERSION_STATUS, this.properties.getCurrentVersion().getStatus().toString());
        valueMap.put(XmlTemplateProvider.VERSION_COMMENT, this.properties.getCurrentVersion().getComment());

        // add RELS-EXT latest version values
        valueMap.put(XmlTemplateProvider.LATEST_VERSION_PID, this.properties.getLatestVersion().getPid());

        valueMap.put(XmlTemplateProvider.LATEST_VERSION_NUMBER, this.properties.getLatestVersion().getNumber());
        //        valueMap.put(XmlTemplateProvider.LATEST_VERSION_DATE, this.properties.getLatestVersion().getDate().toString());
        DateTime lateVersionDate = this.properties.getLatestVersion().getDate();
        if (lateVersionDate == null) {
            valueMap.put(XmlTemplateProvider.LATEST_VERSION_DATE, null);
        }
        else {
            valueMap.put(XmlTemplateProvider.LATEST_VERSION_DATE, lateVersionDate.toString());
        }

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
                    .getLatestReleasedVersion().getDate().toString());
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
     * TODO remove this method if Fedora has fixed the timestamp bug (Fedora 3.0 and 3.1 do not update the object
     * timestamp during create. It happens that timestamps of steams are newer than the object timestamp. This failure
     * not occurs during a later update.).
     * 
     * @param objid
     *            The id of the Fedora Object.
     * @return LastModificationDate of the Object (with workaround for Fedora bug).
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     */
    private DateTime getLastModificationDateByWorkaround(final String objid) throws FedoraSystemException {

        // Work around for Fedora30 bug APIM.getDatastreams()
        DateTime lastModificationDate = null;
        final org.fcrepo.server.types.gen.Datastream[] relsExtInfo =
            this.fedoraUtility.getDatastreamsInformation(objid, null);

        for (final org.fcrepo.server.types.gen.Datastream aRelsExtInfo : relsExtInfo) {
            final String createdDate = aRelsExtInfo.getCreateDate();

            if (lastModificationDate == null) {
                lastModificationDate = new DateTime(createdDate, DateTimeZone.UTC);
            }
            else {
                final DateTime cDate = new DateTime(createdDate, DateTimeZone.UTC);
                if (lastModificationDate.isBefore(cDate)) {
                    lastModificationDate = cDate;
                }
            }
        }
        // End of the work around
        return lastModificationDate;
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
     * Href shouldn't be part of the ContentModel
     */
    private String getHrefWithVersionSuffix() {

        return Constants.CONTENT_MODEL_URL_BASE + getObjid() + ':' + this.properties.getCurrentVersion().getNumber();
    }

    /**
     * Get ContentStreams Vector/HashMap Structure for Velocity.
     * 
     * @return Vector which contains a HashMap with all values for each ContentStream. HashMap keys are keys for
     *         Velocity template.
     */
    private List<HashMap<String, String>> getContentStreamsMap() {
        /*
         * (has to move in an own renderer class, I know. Please feel free to create class infrastructure.).
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
            valueMap.put(XmlTemplateProvider.VAR_LABEL, contentStream.getTitle());
            if (contentStream.getContent().getDataLocation() != null) {
                valueMap.put(XmlTemplateProvider.VAR_URL, contentStream.getContent().getDataLocation().toString());
            }
            valueMap.put(XmlTemplateProvider.VAR_CONTENT, contentStream.getContent().getContent());
            contStreams.add(valueMap);
        }

        return contStreams;
    }

    public List<MdRecordCreate> getMetadataRecords() {
        // TODO Auto-generated method stub
        return null;
    }
}
