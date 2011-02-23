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
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Content Model for create method.
 * 
 * Attention! This is only a helper class for the transition to integrate this
 * functionality into the ContentModel class.
 * 
 * @author FRS
 * 
 */
public class ContentModelCreate extends GenericResourceCreate {

    private ContentModelProperties properties = null;

    private List<MdRecordDefinitionCreate> mdRecordDefinitions = null;

    private List<ContentStreamCreate> contentStreams = null;

    private EscidocIdProvider idProvider = null;

    private Map<String, ResourceDefinitionCreate> resourceDefinitions;

    public Map<String, ResourceDefinitionCreate> getResourceDefinitions() {
        return resourceDefinitions;
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
     */
    public void setMdRecordDefinitions(
        final List<MdRecordDefinitionCreate> mdRecordDefinitions) {

        this.mdRecordDefinitions = mdRecordDefinitions;
    }

    /**
     * Set resource definitions.
     */
    public void setResourceDefinitions(
        final Map<String, ResourceDefinitionCreate> resourceDefinitions) {

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
     * 
     *            FIXME This Spring construct seams not to work.
     * @spring.property ref="escidoc.core.business.EscidocIdProvider"
     */
    public void setIdProvider(final EscidocIdProvider idProvider) {

        this.idProvider = idProvider;
    }

    // /**
    // * Persist whole ContentModel to Repository and force TripleStore sync.
    // *
    // * @throws SystemException
    // * @throws MissingMdRecordException
    // * @throws InvalidStatusException
    // * @throws FileNotFoundException
    // * @throws RelationPredicateNotFoundException
    // * @throws ReferencedResourceNotFoundException
    // * @throws InvalidContentException
    // * @throws MissingAttributeValueException
    // */
    // public void persist() throws SystemException, InvalidStatusException,
    // MissingMdRecordException, FileNotFoundException,
    // InvalidContentException, ReferencedResourceNotFoundException,
    // RelationPredicateNotFoundException, MissingAttributeValueException {
    //
    // persist(true);
    // }

    /**
     * Persist whole ContentModel to Repository.
     * 
     * @param forceSync
     *            Set true to force synchronous sync of TripleStore.
     * @throws WebserverSystemException
     * @throws FedoraSystemException
     */
    public void persist(final boolean forceSync)
        throws WebserverSystemException, FedoraSystemException {

        // FIXME persist behavior

        try {
            // Do not set fedora object id earlier. Otherwise consumes
            // an unsuccessful requests an objid (and time). This is redundant
            // if rollback is implemented and gives an unused objid back to
            // the objid pool.
            if (getObjid() == null) {
                try {
                    setObjid(this.idProvider.getNextPid());
                }
                catch (SystemException e) {
                    // FIXME should be catched earlier (FRS)
                    throw new WebserverSystemException(e);
                }
            }

            // create service definitions and deployments
            if (resourceDefinitions != null) {
                Iterator<ResourceDefinitionCreate> it;
                it = this.resourceDefinitions.values().iterator();
                while (it.hasNext()) {
                    ResourceDefinitionCreate rdc = it.next();
                    String sdefFoxml = getSDefFoXML(rdc);
                    FedoraUtility.getInstance().storeObjectInFedora(sdefFoxml,
                        false);
                    String sdepFoxml = getSDepFoXML(rdc);
                    FedoraUtility.getInstance().storeObjectInFedora(sdepFoxml,
                        false);
                }
            }

            // serialize object without RELS-EXT and WOV to FOXML
            String foxml = getMinimalFoXML();
            FedoraUtility.getInstance().storeObjectInFedora(foxml, false);

            // take timestamp and prepare RELS-EXT
            String lmd = getLastModificationDateByWorkaround(getObjid());

            this.properties.getCurrentVersion().setDate(lmd);
            this.properties.getLatestVersion().setDate(lmd);
            if (this.properties.getLatestReleasedVersion() != null) {
                this.properties.getLatestReleasedVersion().setDate(lmd);
            }

            FedoraUtility.getInstance().addDatastream(getObjid(),
                FoXmlProvider.DATASTREAM_VERSION_HISTORY, new String[] {},
                "whole object versioning datastream", false,
                getWov().getBytes(XmlUtility.CHARACTER_ENCODING), false);

            // update RELS-EXT with timestamp
            String relsExt = renderRelsExt();
            FedoraUtility.getInstance().modifyDatastream(getObjid(),
                Datastream.RELS_EXT_DATASTREAM, "RELS_EXT DATASTREAM",
                relsExt.getBytes(XmlUtility.CHARACTER_ENCODING), false);

            if (forceSync) {
                FedoraUtility.getInstance().sync();
            }

        }
        catch (UnsupportedEncodingException e) {
            // TODO rollback
            throw new WebserverSystemException(e);
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
        return contentStreams;
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
        HashMap<String, String> templateValues = new HashMap<String, String>();

        templateValues.put(XmlTemplateProvider.OBJID,
            getObjidWithVersionSuffix());
        templateValues
            .put(XmlTemplateProvider.HREF, getHrefWithVersionSuffix());

        templateValues.put(XmlTemplateProvider.TITLE, this.properties
            .getObjectProperties().getTitle());
        templateValues.put(XmlTemplateProvider.VERSION_DATE, this.properties
            .getCurrentVersion().getDate());
        templateValues.put(XmlTemplateProvider.VERSION_NUMBER, this.properties
            .getCurrentVersion().getNumber());
        templateValues.put(XmlTemplateProvider.VERSION_STATUS, this.properties
            .getCurrentVersion().getStatus().toString());
        templateValues.put(XmlTemplateProvider.VERSION_COMMENT, this.properties
            .getCurrentVersion().getComment());

        templateValues.put(XmlTemplateProvider.VAR_NAMESPACE_PREFIX,
            Constants.WOV_NAMESPACE_PREFIX);
        templateValues.put(XmlTemplateProvider.VAR_NAMESPACE,
            Constants.WOV_NAMESPACE_URI);

        templateValues.put(XmlTemplateProvider.VERSION_NUMBER, this.properties
            .getCurrentVersion().getNumber());
        templateValues.put(XmlTemplateProvider.TIMESTAMP, this.properties
            .getCurrentVersion().getDate());

        // -------------------------------------

        templateValues.put(XmlTemplateProvider.VAR_AGENT_ID_TYPE,
            Constants.PREMIS_ID_TYPE_ESCIDOC);
        templateValues.put(XmlTemplateProvider.VAR_AGENT_BASE_URI,
            Constants.USER_ACCOUNT_URL_BASE);
        templateValues.put(XmlTemplateProvider.VAR_AGENT_ID_VALUE,
            UserContext.getId());
        templateValues.put(XmlTemplateProvider.VAR_AGENT_TITLE,
            UserContext.getRealName());

        // EVENT_XMLID EVENT_ID_TYPE EVENT_ID_VALUE
        templateValues.put(XmlTemplateProvider.VAR_EVENT_XMLID,
            "v1e" + System.currentTimeMillis());
        templateValues.put(
            XmlTemplateProvider.VAR_EVENT_ID_VALUE,
            templateValues.get(XmlTemplateProvider.HREF) + '/'
                + Elements.ELEMENT_WOV_VERSION_HISTORY + '#'
                + templateValues.get(XmlTemplateProvider.VAR_EVENT_XMLID));
        templateValues.put(XmlTemplateProvider.VAR_EVENT_ID_TYPE,
            Constants.PREMIS_ID_TYPE_URL_RELATIVE);
        templateValues.put(XmlTemplateProvider.VAR_OBJECT_ID_TYPE,
            Constants.PREMIS_ID_TYPE_ESCIDOC);
        templateValues.put(XmlTemplateProvider.VAR_OBJECT_ID_VALUE, getObjid());

        return CommonFoXmlProvider.getInstance().getWov(templateValues);
    }

    /**
     * Render Object FoXML with Components, ContentStreams and DC but with
     * incomplete RELS-EXT and wihtout WOV.
     * 
     * WOV is excluded and RELS-EXT incomplete because of non existing timestamp
     * (which is to add in a later step to the object).
     * 
     * @return FoXML representation of ContentModel.
     * 
     * @throws SystemException
     *             Thrown if rendering of ContentModel or sub-elements failed.
     * @throws UnsupportedEncodingException
     *             Thrown if conversion to default character set failed.
     * @throws WebserverSystemException
     */
    private String getMinimalFoXML() throws WebserverSystemException {

        Map<String, Object> valueMap = new HashMap<String, Object>();

        valueMap.put(XmlTemplateProvider.OBJID, getObjid());
        valueMap.put(XmlTemplateProvider.OBJID_UNDERSCORE, getObjid()
            .replaceAll(":", Constants.COLON_REPLACEMENT_PID));

        valueMap.put(XmlTemplateProvider.TITLE, this.properties
            .getObjectProperties().getTitle());

        valueMap.put(XmlTemplateProvider.DESCRIPTION, this.properties
            .getObjectProperties().getDescription());

        // RELS-EXT
        valueMap.putAll(getRelsExtNamespaceValues());
        valueMap.putAll(preparePropertiesValueMap());

        // add service definitions
        valueMap.put("BEHAVIORS", getResourceDefinitions());

        // in order to create DS-COMPOSITE and *_XSD datastreams
        // add md-record definitions
        valueMap.put("MD_RECORDS", getMetadataRecordDefinitions());

        // Content-Streams
        valueMap.put(XmlTemplateProvider.CONTENT_STREAMS,
            getContentStreamsMap());

        valueMap.put(XmlTemplateProvider.IN_CREATE, XmlTemplateProvider.TRUE);

        return ContentModelFoXmlProvider.getInstance().getContentModelFoXml(
            valueMap);
    }

    /**
     * Render service definition FoXML.
     * 
     * @param resourceDefinition
     *            The resource definition create object.
     * 
     * @return FoXML representation of service definition.
     * 
     * @throws SystemException
     *             Thrown if rendering of ContentModel or sub-elements failed.
     * @throws UnsupportedEncodingException
     *             Thrown if conversion to default character set failed.
     * @throws WebserverSystemException
     */
    private String getSDefFoXML(
        final ResourceDefinitionCreate resourceDefinition)
        throws WebserverSystemException {

        Map<String, Object> valueMap = new HashMap<String, Object>();

        valueMap.putAll(getBehaviorValues(resourceDefinition));

        String foxml =
            ContentModelFoXmlProvider.getInstance().getServiceDefinitionFoXml(
                valueMap);
        return foxml;
    }

    /**
     * Render service deployment FoXML.
     * 
     * @param resourceDefinition
     *            The resource definition create object.
     * 
     * @return FoXML representation of service deployment.
     * 
     * @throws SystemException
     *             Thrown if rendering of ContentModel or sub-elements failed.
     * @throws UnsupportedEncodingException
     *             Thrown if conversion to default character set failed.
     * @throws WebserverSystemException
     */
    private String getSDepFoXML(
        final ResourceDefinitionCreate resourceDefinition)
        throws WebserverSystemException {

        Map<String, Object> valueMap = new HashMap<String, Object>();

        valueMap.putAll(getBehaviorValues(resourceDefinition));

        String foxml =
            ContentModelFoXmlProvider.getInstance().getServiceDeploymentFoXml(
                valueMap);
        return foxml;
    }

    private Map<String, Object> getBehaviorValues(
        final ResourceDefinitionCreate resourceDefinition) {
        Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(XmlTemplateProvider.BEHAVIOR_CONTENT_MODEL_ID, getObjid());
        valueMap.put(XmlTemplateProvider.BEHAVIOR_CONTENT_MODEL_ID_UNDERSCORE,
            getObjid().replaceAll(":", Constants.COLON_REPLACEMENT_PID));

        valueMap.put(XmlTemplateProvider.BEHAVIOR_OPERATION_NAME,
            resourceDefinition.getName());
        valueMap.put(XmlTemplateProvider.BEHAVIOR_TRANSFORM_MD,
            resourceDefinition.getMdRecordName());
        valueMap.put(XmlTemplateProvider.BEHAVIOR_XSLT_HREF,
            resourceDefinition.getXsltHref());
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

        Map<String, Object> valueMap = new HashMap<String, Object>();

        valueMap.put(XmlTemplateProvider.OBJID, getObjid());
        valueMap.put(XmlTemplateProvider.OBJID_UNDERSCORE, getObjid()
            .replaceAll(":", Constants.COLON_REPLACEMENT_PID));

        valueMap.putAll(getRelsExtNamespaceValues());
        valueMap.putAll(preparePropertiesValueMap());
        // add service definitions
        valueMap.put("BEHAVIORS", getResourceDefinitions());

        return ContentModelFoXmlProvider.getInstance().getContentModelRelsExt(
            valueMap);
    }

    /**
     * Prepare values for FOXML Template Renderer (Velocity).
     * 
     * @return HashMap with template values.
     * @throws WebserverSystemException
     * @throws SystemException
     *             Thrown if obtaining values from framework configuration or
     *             TripleStore failed.
     */
    private Map<String, String> preparePropertiesValueMap()
        throws WebserverSystemException {

        Map<String, String> valueMap = new HashMap<String, String>();

        // add RELS-EXT values -------------------------------------------------
        valueMap.put(XmlTemplateProvider.FRAMEWORK_BUILD_NUMBER,
            getBuildNumber());

        // add RELS-EXT object properties
        valueMap.put(XmlTemplateProvider.CREATED_BY_ID, this.properties
            .getCurrentVersion().getCreatedById());
        valueMap.put(XmlTemplateProvider.CREATED_BY_TITLE, this.properties
            .getCurrentVersion().getCreatedByName());

        valueMap.put(XmlTemplateProvider.MODIFIED_BY_ID, this.properties
            .getCurrentVersion().getModifiedById());
        valueMap.put(XmlTemplateProvider.MODIFIED_BY_TITLE, this.properties
            .getCurrentVersion().getCreatedByName());

        valueMap.put(XmlTemplateProvider.PUBLIC_STATUS, this.properties
            .getObjectProperties().getStatus().toString());
        valueMap.put(XmlTemplateProvider.PUBLIC_STATUS_COMMENT, this.properties
            .getObjectProperties().getStatusComment());

        valueMap.put(XmlTemplateProvider.OBJECT_PID, this.properties
            .getObjectProperties().getPid());

        valueMap.put(XmlTemplateProvider.CONTEXT_ID, this.properties
            .getObjectProperties().getContextId());
        valueMap.put(XmlTemplateProvider.CONTEXT_TITLE, this.properties
            .getObjectProperties().getContextTitle());

        valueMap.put(XmlTemplateProvider.CONTENT_MODEL_ID, this.properties
            .getObjectProperties().getContentModelId());
        valueMap.put(XmlTemplateProvider.CONTENT_MODEL_TITLE, this.properties
            .getObjectProperties().getContentModelTitle());

        // add RELS-EXT current version values
        // version pid currently not supported for create
        // valueMap.put(XmlTemplateProvider.VERSION_PID, this.properties
        // .getCurrentVersion().getPid());

        valueMap.put(XmlTemplateProvider.VERSION_NUMBER, this.properties
            .getCurrentVersion().getNumber());
        String date = this.properties.getCurrentVersion().getDate();
        if (date == null) {
            date = "---";
        }
        valueMap.put(XmlTemplateProvider.VERSION_DATE, date);
        valueMap.put(XmlTemplateProvider.VERSION_STATUS, this.properties
            .getCurrentVersion().getStatus().toString());
        valueMap.put(XmlTemplateProvider.VERSION_COMMENT, this.properties
            .getCurrentVersion().getComment());

        // add RELS-EXT latest version values
        valueMap.put(XmlTemplateProvider.LATEST_VERSION_PID, this.properties
            .getLatestVersion().getPid());

        valueMap.put(XmlTemplateProvider.LATEST_VERSION_NUMBER, this.properties
            .getLatestVersion().getNumber());
        valueMap.put(XmlTemplateProvider.LATEST_VERSION_DATE, this.properties
            .getLatestVersion().getDate());
        valueMap.put(XmlTemplateProvider.LATEST_VERSION_STATUS, this.properties
            .getLatestVersion().getStatus().toString());
        valueMap.put(XmlTemplateProvider.LATEST_VERSION_COMMENT,
            this.properties.getLatestVersion().getComment());

        // in the case of a surrogate
        String origin = getProperties().getObjectProperties().getOrigin();
        String originObjectId =
            getProperties().getObjectProperties().getOriginObjectId();
        String originVersionId =
            getProperties().getObjectProperties().getOriginVersionId();
        if (origin != null) {
            valueMap.put("originObjectId", originObjectId);
            if (originVersionId != null) {
                valueMap.put(XmlTemplateProvider.VAR_ORIGIN_VERSION_ID,
                    originVersionId);
            }
        }

        // add RELS-EXT latest-released-version properties
        if (this.properties.getLatestReleasedVersion() != null) {
            valueMap.put(XmlTemplateProvider.LATEST_RELEASE_NUMBER,
                this.properties.getLatestReleasedVersion().getNumber());

            // latest release date
            if (this.properties.getLatestReleasedVersion().getDate() != null) {
                valueMap.put(XmlTemplateProvider.LATEST_RELEASE_DATE,
                    this.properties.getLatestReleasedVersion().getDate());
            }
            else {
                valueMap.put(XmlTemplateProvider.LATEST_RELEASE_DATE, "---");
            }

            // latest release pid
            if (this.properties.getLatestReleasedVersion().getPid() != null) {
                valueMap.put(XmlTemplateProvider.LATEST_RELEASE_PID,
                    this.properties.getLatestReleasedVersion().getPid());
            }
        }

        return valueMap;
    }

    /**
     * Getting Namespaces for RelsExt as Map.
     * 
     * @return HashMap with namespace values for XML representation.
     */
    private Map<String, String> getRelsExtNamespaceValues() {

        Map<String, String> values = new HashMap<String, String>();

        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS_PREFIX,
            Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS,
            Constants.PROPERTIES_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_VERSION_NS_PREFIX,
            Constants.VERSION_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_VERSION_NS,
            Constants.VERSION_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_RELEASE_NS_PREFIX,
            Constants.RELEASE_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_RELEASE_NS,
            Constants.RELEASE_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_RESOURCE_NS_PREFIX,
            Constants.STRUCTURAL_RELATIONS_NS_PREFIX);

        values.put(XmlTemplateProvider.ESCIDOC_RESOURCE_NS,
            Constants.RESOURCES_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_RELATION_NS,
            Constants.STRUCTURAL_RELATIONS_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_RELATION_NS_PREFIX,
            Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT);

        values.put(XmlTemplateProvider.ESCIDOC_ORIGIN_NS,
            de.escidoc.core.common.business.Constants.ORIGIN_NS_URI);

        values.put(XmlTemplateProvider.ESCIDOC_ORIGIN_NS_PREFIX,
            de.escidoc.core.common.business.Constants.ORIGIN_NS_PREFIX);

        return values;
    }

    /**
     * TODO remove this method if Fedora has fixed the timestamp bug (Fedora 3.0
     * and 3.1 do not update the object timestamp during create. It happens that
     * timestamps of steams are newer than the object timestamp. This failure
     * not occurs during a later update.).
     * 
     * @param objid
     *            The id of the Fedora Object.
     * @return LastModificationDate of the Object (with workaround for Fedora
     *         bug).
     * @throws FedoraSystemException
     *             Thrown if request to Fedora failed.
     */
    private String getLastModificationDateByWorkaround(final String objid)
        throws FedoraSystemException {

        // Work around for Fedora30 bug APIM.getDatastreams()
        String lastModificationDate = null;
        org.fcrepo.server.types.gen.Datastream[] relsExtInfo =
            FedoraUtility.getInstance().getDatastreamsInformation(objid, null);
        for (org.fcrepo.server.types.gen.Datastream aRelsExtInfo : relsExtInfo) {
            String createdDate = aRelsExtInfo.getCreateDate();

            if (lastModificationDate == null) {
                lastModificationDate = createdDate;
            } else {

                ReadableInstant cDate = new DateTime(createdDate);
                ReadableInstant lDate = new DateTime(lastModificationDate);
                if (lDate.isBefore(cDate)) {
                    lastModificationDate = createdDate;
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

        return getObjid() + ':'
            + this.properties.getCurrentVersion().getNumber();
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

        return Constants.ITEM_URL_BASE + getObjid() + ':'
            + this.properties.getCurrentVersion().getNumber();
    }

    /**
     * Get ContentStreams Vector/HashMap Structure for Velocity.
     * 
     * @return Vector which contains a HashMap with all values for each
     *         ContentStream. HashMap keys are keys for Velocity template.
     */
    private List<HashMap<String, String>> getContentStreamsMap() {
        /*
         * (has to move in an own renderer class, I know. Please feel free to
         * create class infrastructure.).
         */

        if (this.contentStreams == null) {
            return null;
        }

        List<HashMap<String, String>> contStreams =
            new ArrayList<HashMap<String, String>>();

        for (ContentStreamCreate contentStream : this.contentStreams) {
            HashMap<String, String> valueMap = new HashMap<String, String>();

            ContentStreamCreate cont = contentStream;

            valueMap.put(XmlTemplateProvider.CONTROL_GROUP, cont
                    .getContent().getStorageType().getAbbreviation());
            valueMap.put(XmlTemplateProvider.VAR_ID, cont.getName());
            valueMap.put(XmlTemplateProvider.VAR_VERSIONABLE,
                    XmlTemplateProvider.TRUE);
            valueMap.put(XmlTemplateProvider.VAR_ALT_IDS, "content-stream");
            valueMap.put(XmlTemplateProvider.MIME_TYPE, cont.getMimeType());

            valueMap.put(XmlTemplateProvider.VAR_LABEL, cont.getTitle());

            if (cont.getContent().getDataLocation() != null) {
                valueMap.put(XmlTemplateProvider.VAR_URL, cont
                        .getContent().getDataLocation().toString());
            }
            valueMap.put(XmlTemplateProvider.VAR_CONTENT, cont
                    .getContent().getContent());

            contStreams.add(valueMap);
        }

        return contStreams;
    }

    public List<MdRecordCreate> getMetadataRecords() {
        // TODO Auto-generated method stub
        return null;
    }
}
