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
/**
 * 
 */
package de.escidoc.core.cmm.business.fedora;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.escidoc.core.cmm.business.fedora.contentModel.ContentModelHandlerRetrieve;
import de.escidoc.core.cmm.business.interfaces.ContentModelHandlerInterface;
import de.escidoc.core.cmm.business.stax.handler.contentModel.ContentModelCreateHandler;
import de.escidoc.core.cmm.business.stax.handler.contentModel.ContentModelPropertiesHandler;
import de.escidoc.core.cmm.business.stax.handler.contentModel.MdRecordDefinitionHandler;
import de.escidoc.core.cmm.business.stax.handler.contentModel.ResourceDefinitionHandler;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.CqlFilter;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.business.fedora.resources.create.ContentModelCreate;
import de.escidoc.core.common.business.fedora.resources.create.ContentStreamCreate;
import de.escidoc.core.common.business.fedora.resources.create.MdRecordCreate;
import de.escidoc.core.common.business.fedora.resources.create.MdRecordDefinitionCreate;
import de.escidoc.core.common.business.fedora.resources.create.ResourceDefinitionCreate;
import de.escidoc.core.common.business.fedora.resources.interfaces.ResourceCacheInterface;
import de.escidoc.core.common.business.fedora.resources.listener.ResourceListener;
import de.escidoc.core.common.business.filter.ExplainRequest;
import de.escidoc.core.common.business.filter.SRURequest;
import de.escidoc.core.common.business.indexing.IndexingHandler;
import de.escidoc.core.common.business.stax.handler.common.ContentStreamsHandler;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ResourceInUseException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.OptimisticLockingHandler;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ContentModelFoXmlProvider;
import de.escidoc.core.common.util.xml.factory.ExplainXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;

/**
 * @author FRS
 * @spring.bean id="business.FedoraContentModelHandler" scope="prototype"
 */
public class FedoraContentModelHandler extends ContentModelHandlerRetrieve
    implements ContentModelHandlerInterface {

    private static AppLogger log = new AppLogger(
        FedoraContentModelHandler.class.getName());

    private ResourceCacheInterface contentModelCache = null;

    private final List<ResourceListener> contentModelListeners =
        new Vector<ResourceListener>();

    /** SRW explain request. */
    private ExplainRequest explainRequest = null;

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @throws ContentModelNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.common.business.fedora.AbstractResourceHandler#retrieve(java.lang.String)
     */
    public String retrieve(final String id)
        throws ContentModelNotFoundException, SystemException {

        setContentModel(id);
        return render();
    }

    public String retrieveProperties(final String id)
        throws ContentModelNotFoundException, SystemException {
        setContentModel(id);
        return renderProperties();
    }

    public String retrieveContentStreams(final String id)
        throws ContentModelNotFoundException, SystemException {
        setContentModel(id);
        return renderContentStreams(true);
    }

    public String retrieveContentStream(final String id, final String name)
        throws ContentModelNotFoundException, SystemException {
        setContentModel(id);
        return renderContentStream(name, true);
    }

    public EscidocBinaryContent retrieveContentStreamContent(
        final String id, final String name)
        throws ContentModelNotFoundException, SystemException,
        ContentStreamNotFoundException, InvalidStatusException {

        setContentModel(id);
        if (getContentModel().isWithdrawn()) {
            final String msg =
                "The object is in state '"
                    + de.escidoc.core.common.business.Constants.STATUS_WITHDRAWN
                    + "'. Content is not accessible.";
            log.debug(msg);
            throw new InvalidStatusException(msg);
        }

        return getContentStream(name);
    }

    private EscidocBinaryContent getContentStream(final String name)
        throws ContentStreamNotFoundException, FedoraSystemException,
        WebserverSystemException {

        Datastream cs = getContentModel().getContentStream(name);

        EscidocBinaryContent bin = new EscidocBinaryContent();
        String fileName = cs.getLabel();
        bin.setFileName(fileName);

        String mimeType = cs.getMimeType();
        bin.setMimeType(mimeType);

        if (cs.getControlGroup().equals("R")) {
            bin.setRedirectUrl(cs.getLocation());
        }
        else {
            String fedoraLocalUrl =
                "/get/" + getContentModel().getId() + "/" + name;
            if (getContentModel().getVersionDate() != null) {
                fedoraLocalUrl += "/" + getContentModel().getVersionDate();
            }
            bin.setContent(getFedoraUtility().requestFedoraURL(fedoraLocalUrl));
        }

        return bin;
    }

    private EscidocBinaryContent retrieveOtherContent(final String dsName)
        throws WebserverSystemException {
        final String name = dsName;
        Datastream ds = getContentModel().getOtherStream(name);
        return getContent(ds);
    }

    private EscidocBinaryContent getContent(final Datastream ds)
        throws WebserverSystemException {

        EscidocBinaryContent bin = new EscidocBinaryContent();
        String name = ds.getName();
        String fileName = ds.getLabel();
        bin.setFileName(fileName);

        String mimeType = ds.getMimeType();
        bin.setMimeType(mimeType);

        if (ds.getControlGroup().equals("R")) {
            bin.setRedirectUrl(ds.getLocation());
        }
        else {
            String fedoraLocalUrl = "/get/" + ds.getParentId() + "/" + name;
            if (getContentModel().getVersionDate() != null) {
                fedoraLocalUrl += "/" + getContentModel().getVersionDate();
            }
            bin.setContent(getFedoraUtility().requestFedoraURL(fedoraLocalUrl));
        }

        return bin;

    }

    public String retrieveResources(final String id)
        throws ContentModelNotFoundException, SystemException {
        setContentModel(id);
        return renderResources();
    }

    public String retrieveResourceDefinitions(final String id)
        throws ContentModelNotFoundException, SystemException {
        setContentModel(id);
        return renderResourceDefinitions();
    }

    public String retrieveResourceDefinition(final String id, final String name)
        throws ContentModelNotFoundException, SystemException {
        setContentModel(id);
        return renderResourceDefinition(name);
    }

    public EscidocBinaryContent retrieveMdRecordDefinitionSchemaContent(
        final String id, final String name)
        throws ContentModelNotFoundException, SystemException {

        setContentModel(id);
        return retrieveOtherContent(name + "_xsd");
    }

    public EscidocBinaryContent retrieveResourceDefinitionXsltContent(
        final String id, final String name)
        throws ContentModelNotFoundException, ResourceNotFoundException,
        SystemException {

        setContentModel(id);

        // xslt is stored in sdef

        // sDef ID is build from CM ID and operation name
        String sDefId =
            "sdef:"
                + getContentModel().getId().replaceAll(":",
                    Constants.COLON_REPLACEMENT_PID) + "-" + name;

        // get the 'xslt' datastream from sDef
        Datastream ds = null;
        try {
            ds = new Datastream("xslt", sDefId, null);
        }
        catch (StreamNotFoundException e) {
            throw new ResourceNotFoundException("No XSLT for operation '"
                + name + "' in content model " + id + ".");
        }

        return getContent(ds);
    }

    public String retrieveVersionHistory(final String id)
        throws ContentModelNotFoundException, SystemException {

        setContentModel(id);
        String versionsXml = null;

        try {
            versionsXml =
                getContentModel()
                    .getWov()
                    .toStringUTF8()
                    .replaceFirst(
                        "<"
                            + de.escidoc.core.common.business.Constants.WOV_NAMESPACE_PREFIX
                            + ":" + Elements.ELEMENT_WOV_VERSION_HISTORY,
                        "<"
                            + de.escidoc.core.common.business.Constants.WOV_NAMESPACE_PREFIX
                            + ":" + Elements.ELEMENT_WOV_VERSION_HISTORY
                            + " xml:base=\"" + XmlUtility.getEscidocBaseUrl()
                            + "\" " + Elements.ATTRIBUTE_LAST_MODIFICATION_DATE
                            + "=\""
                            + getContentModel().getLastModificationDate()
                            + "\" ");
        }
        catch (StreamNotFoundException e) {
            throw new IntegritySystemException("Version history not found.", e);
        }

        return versionsXml;
    }

    /**
     * Retrieves a filtered list of Content Models.
     * 
     * @param parameterMap
     *            map of key - value pairs describing the filter
     * 
     * @return Returns XML representation of the list of Content Model objects.
     * @throws InvalidSearchQueryException
     *             Thrown if the given search query could not be translated into
     *             a SQL query.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    public String retrieveContentModels(final Map<String, String[]> parameterMap)
        throws InvalidSearchQueryException, SystemException {
        String result = null;
        CqlFilter filter = null;
        SRURequest parameters =
            new SRURequest((Map<String, String[]>) parameterMap);

        if (parameters.query != null) {
            filter = new CqlFilter(parameters.query);
        }
        else {
            filter = new CqlFilter();
        }
        filter.setLimit(parameters.limit);
        filter.setObjectType(ResourceType.CONTENT_MODEL);
        filter.setOffset(parameters.offset);
        if (parameters.explain) {
            StringWriter output = new StringWriter();

            explainRequest.explain(output, ResourceType.CONTENT_MODEL);
            result = output.toString();
        }
        else {
            StringWriter output = new StringWriter();
            long numberOfRecords =
                contentModelCache.getNumberOfRecords(getUtility()
                    .getCurrentUserId(), filter);

            output.write("<?xml version=\"1.0\" encoding=\""
                + XmlUtility.CHARACTER_ENCODING + "\"?>"
                + "<zs:searchRetrieveResponse "
                + "xmlns:zs=\"http://www.loc.gov/zing/srw/\">"
                + "<zs:version>1.1</zs:version>" + "<zs:numberOfRecords>"
                + numberOfRecords + "</zs:numberOfRecords>");
            if (numberOfRecords > 0) {
                output.write("<zs:records>");
            }
            contentModelCache.getResourceList(output, getUtility()
                .getCurrentUserId(), filter, "srw");
            if (numberOfRecords > 0) {
                output.write("</zs:records>");
            }
            output.write("</zs:searchRetrieveResponse>");
            result = output.toString();
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param xmlData
     * @return
     * @throws MalformedURLException
     * @throws MissingAttributeValueException
     * @throws InvalidContentException
     * @throws InvalidXmlException
     * @throws SystemException
     * @throws SystemException
     * @see de.escidoc.core.cmm.business.interfaces.ContentModelHandlerInterface#create(java.lang.String)
     */
    public String create(final String xmlData) throws InvalidContentException,
        MissingAttributeValueException, SystemException, XmlCorruptedException {

        ContentModelCreate contentModel = parseContentModel(xmlData);

        // check that the objid was not obtained from the representation
        contentModel.setObjid(null);

        contentModel.setIdProvider(getIdProvider());
        validate(contentModel);
        contentModel.persist(true);
        String objid = contentModel.getObjid();
        String resultContentModel = null;
        try {
            resultContentModel = retrieve(objid);
        }
        catch (ResourceNotFoundException e) {
            String msg =
                "The Content Model with id '" + objid
                    + "', which was just created, "
                    + "could not be found for retrieve.";
            log.warn(msg);
            throw new IntegritySystemException(msg, e);
        }
        fireContentModelCreated(objid, resultContentModel);
        return resultContentModel;
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @throws ContentModelNotFoundException
     * @throws SystemException
     * @throws LockingException
     * @throws InvalidStatusException
     * @throws ResourceInUseException
     * @see de.escidoc.core.common.business.fedora.AbstractResourceHandler#delete(java.lang.String)
     */
    public void delete(final String id) throws ContentModelNotFoundException,
        SystemException, LockingException, InvalidStatusException,
        ResourceInUseException {

        setContentModel(id);
        checkLocked();
        if (!(getContentModel().isPending() || getContentModel().isInRevision())) {
            throw new InvalidStatusException(
                "Content Model must be is public status pending or "
                    + "submitted in order to delete it.");
        }

        // check if objects refer this content model
        if (TripleStoreUtility.getInstance().hasReferringResource(id)) {
            throw new ResourceInUseException(
                "The content model is referred by "
                    + "an resource and can not be deleted.");
        }

        // delete every behavior (sdef, sdep) even those from old versions

        getFedoraUtility().deleteObject(getContentModel().getId(), true);
        fireContentModelDeleted(getContentModel().getId());
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.common.business.fedora.AbstractResourceHandler#update(java.lang.String,
     *      java.lang.String)
     * 
     * @param id
     * @param xmlData
     * @return
     * @throws ContentModelNotFoundException
     * @throws OptimisticLockingException
     * @throws SystemException
     * @throws ReadonlyVersionException
     * @throws MissingAttributeValueException
     */
    public String update(final String id, final String xmlData)
        throws ContentModelNotFoundException, OptimisticLockingException,
        SystemException, ReadonlyVersionException,
        MissingAttributeValueException, InvalidXmlException,
        InvalidContentException {

        setContentModel(id);
        String startTimestamp =
            getContentModel().getLastFedoraModificationDate();

        checkLatestVersion();

        /*
         * 
         */

        // parse incomming XML
        StaxParser sp = new StaxParser();
        // check optimistic locking criteria! and ID in root element?
        sp.addHandler(new OptimisticLockingHandler(getContentModel().getId(),
            Constants.CONTENT_MODEL_OBJECT_TYPE, getContentModel()
                .getLastModificationDate(), sp));
        // get name and description
        ContentModelPropertiesHandler cmph =
            new ContentModelPropertiesHandler(sp);
        sp.addHandler(cmph);
        // get md-record definitions
        MdRecordDefinitionHandler mrdh =
            new MdRecordDefinitionHandler(sp,
                "/content-model/md-record-definitions");
        sp.addHandler(mrdh);
        // get resource definitions
        ResourceDefinitionHandler rdh =
            new ResourceDefinitionHandler(sp,
                "/content-model/resource-definitions");
        sp.addHandler(rdh);
        // get content-streams
        ContentStreamsHandler csh =
            new ContentStreamsHandler(sp, "/content-model/content-streams");
        sp.addHandler(csh);

        try {
            sp.parse(xmlData);
        }
        catch (WebserverSystemException e) {
            throw e;
        }
        catch (MissingAttributeValueException e) {
            throw e;
        }
        catch (InvalidXmlException e) {
            throw e;
        }
        catch (InvalidContentException e) {
            throw e;
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }

        getContentModel().setTitle(
            cmph.getProperties().getObjectProperties().getTitle());
        getContentModel().setDescription(
            cmph.getProperties().getObjectProperties().getDescription());
        // update DC
        // TODO update title
        // TODO update description

        String sdexIdMidfix =
            getContentModel().getId().replaceAll(":",
                Constants.COLON_REPLACEMENT_PID)
                + "-";
        String sdefIdPrefix = "sdef:" + sdexIdMidfix;
        // String sdepIdPrefix = "sdep:" + sdexIdMidfix;
        List<ResourceDefinitionCreate> resourceDefinitions =
            rdh.getResourceDefinitions();

        // update RELS-EXT
        // FIXME store operation names in ContentModel and remove and add
        // services in one pass
        Map<String, Vector<StartElementWithChildElements>> deleteFromRelsExt =
            new HashMap<String, Vector<StartElementWithChildElements>>();
        Vector<StartElementWithChildElements> deleteElementList =
            new Vector<StartElementWithChildElements>();
        deleteElementList.add(new StartElementWithChildElements("hasService",
            Constants.FEDORA_MODEL_NS_URI, null, null, null, null));
        deleteFromRelsExt.put("/RDF/Description/hasService", deleteElementList);
        getContentModel().setRelsExt(
            Utility.updateRelsExt(null, deleteFromRelsExt, null,
                getContentModel(), null));

        List<StartElementWithChildElements> addToRelsExt =
            new Vector<StartElementWithChildElements>();
        // add services to RELS-EXT

        Iterator<ResourceDefinitionCreate> rdit =
            resourceDefinitions.iterator();
        while (rdit.hasNext()) {
            ResourceDefinitionCreate resourceDefinition = rdit.next();
            final StartElementWithChildElements hasServiceElement =
                new StartElementWithChildElements();
            hasServiceElement.setLocalName("hasService");
            hasServiceElement.setPrefix(Constants.FEDORA_MODEL_NS_PREFIX);
            hasServiceElement.setNamespace(Constants.FEDORA_MODEL_NS_URI);
            final Attribute resource =
                new Attribute("resource", Constants.RDF_NAMESPACE_URI,
                    Constants.RDF_NAMESPACE_PREFIX, "info:fedora/"
                        + sdefIdPrefix + resourceDefinition.getName());
            hasServiceElement.addAttribute(resource);
            addToRelsExt.add(hasServiceElement);
        }

        // TODO remove services from RELS-EXT
        // (<hasService
        // rdf:resource="info:fedora/sdef:escidoc_9003-trans"
        // xmlns="info:fedora/fedora-system:def/model#"/>)
        getContentModel().setRelsExt(
            Utility.updateRelsExt(addToRelsExt, null, null, getContentModel(),
                null));

        // Metadata Record Definitions
        List<MdRecordDefinitionCreate> mdRecordDefinitions =
            mrdh.getMdRecordDefinitions();
        // update DS-COMPOSITE
        Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put("MD_RECORDS", mdRecordDefinitions);
        String dsCompositeModelContent =
            ContentModelFoXmlProvider.getInstance().getContentModelDsComposite(
                valueMap);
        getContentModel().setDsCompositeModel(dsCompositeModelContent);
        // TODO create, delete, update *_XSD datastreams
        Iterator<MdRecordDefinitionCreate> it = mdRecordDefinitions.iterator();
        while (it.hasNext()) {
            MdRecordDefinitionCreate mdRecordDefinition = it.next();
            String name = mdRecordDefinition.getName();
            String xsdUrl = mdRecordDefinition.getSchemaHref();
            getContentModel()
                .setOtherStream(
                    name + "_xsd",
                    new Datastream(
                        name + "_xsd",
                        getContentModel().getId(),
                        xsdUrl,
                        de.escidoc.core.common.business.fedora.Constants.STORAGE_EXTERNAL_MANAGED,
                        "text/xml"));
        }

        // Resource Definitions
        // services are already added to RELS-EXT
        // TODO delete sdef+sdep
        // create service definitions and deployments or update xslt
        FedoraUtility fu = FedoraUtility.getInstance();
        TripleStoreUtility tu = TripleStoreUtility.getInstance();
        if (resourceDefinitions != null) {
            rdit = resourceDefinitions.iterator();
            while (rdit.hasNext()) {
                ResourceDefinitionCreate resourceDefinition = rdit.next();
                String sdefId = sdefIdPrefix + resourceDefinition.getName();
                // String sdepId = sdepIdPrefix + resourceDefinition.getName();

                if (tu.exists(sdefId)) {
                    // check if href for xslt is changed
                    // /cmm/content-model/escidoc:40013/resource-\
                    // definitions/resource-definition/trans/xslt
                    if (!(resourceDefinition.getXsltHref()
                        .equalsIgnoreCase("/cmm/content-model/"
                            + getContentModel().getId()
                            + "/resource-definitions/resource-definition/"
                            + resourceDefinition.getName() + "/xslt/content"))) {
                        // update xslt
                        fu.modifyDatastream(sdefId, "xslt",
                            "Transformation instructions for operation '"
                                + resourceDefinition.getName() + "'.",
                            "text/xml", new String[0],
                            resourceDefinition.getXsltHref(), false);
                    }
                    else {
                        log.debug("Do not update xslt.");
                    }
                }
                else {
                    // create
                    String sdefFoxml = getSDefFoXML(resourceDefinition);
                    fu.storeObjectInFedora(sdefFoxml, false);
                    String sdepFoxml = getSDepFoXML(resourceDefinition);
                    fu.storeObjectInFedora(sdepFoxml, false);
                }
            }
        }

        // Content Streams
        List<ContentStreamCreate> contentStreams = csh.getContentStreams();
        setContentStreams(contentStreams);

        /*
         * 
         */

        // check if modified
        String updatedXmlData = null;
        String endTimestamp = getContentModel().getLastFedoraModificationDate();
        if (!startTimestamp.equals(endTimestamp)) {
            // object is modified
            getUtility().makeVersion("ContentModelHandler.update()", null,
                getContentModel(), getFedoraUtility());
            getContentModel().persist();

            updatedXmlData = retrieve(getContentModel().getId());
            fireContentModelModified(getContentModel().getId(), updatedXmlData);
        }
        else {
            updatedXmlData = render();
        }

        return updatedXmlData;
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

        HashMap<String, Object> valueMap = new HashMap<String, Object>();

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

        HashMap<String, Object> valueMap = new HashMap<String, Object>();

        valueMap.putAll(getBehaviorValues(resourceDefinition));

        String foxml =
            ContentModelFoXmlProvider.getInstance().getServiceDeploymentFoXml(
                valueMap);
        return foxml;
    }

    private Map<String, Object> getBehaviorValues(
        final ResourceDefinitionCreate resourceDefinition) {
        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(XmlTemplateProvider.BEHAVIOR_CONTENT_MODEL_ID,
            getContentModel().getId());
        valueMap.put(
            XmlTemplateProvider.BEHAVIOR_CONTENT_MODEL_ID_UNDERSCORE,
            getContentModel().getId().replaceAll(":",
                Constants.COLON_REPLACEMENT_PID));

        valueMap.put(XmlTemplateProvider.BEHAVIOR_OPERATION_NAME,
            resourceDefinition.getName());
        valueMap.put(XmlTemplateProvider.BEHAVIOR_TRANSFORM_MD,
            resourceDefinition.getMdRecordName());
        valueMap.put(XmlTemplateProvider.BEHAVIOR_XSLT_HREF,
            resourceDefinition.getXsltHref());
        return valueMap;
    }

    /**
     * Creates Datastream objects from the values in
     * <code>contentStreamMap</code> and calls Item.setContentStreams with a
     * HashMap which contains the metadata datastreams as Datastream objects.
     * 
     * @param contentStreamMap
     *            A HashMap which contains the metadata datastreams as
     *            ByteArrayOutputStream.
     * @throws WebserverSystemException
     * @throws IntegritySystemException
     * @throws FedoraSystemException
     */
    private void setContentStreams(
        final List<ContentStreamCreate> contentStreams)
        throws WebserverSystemException, IntegritySystemException,
        FedoraSystemException {
        HashMap<String, Datastream> contentStreamDatastreams =
            new HashMap<String, Datastream>();

        Iterator<ContentStreamCreate> csIt = contentStreams.iterator();
        while (csIt.hasNext()) {
            ContentStreamCreate contentStream = csIt.next();
            String name = contentStream.getName();

            Datastream ds = null;
            if (contentStream.getContent() != null
                && contentStream.getContent().getContent() != null) {
                try {
                    ds =
                        new Datastream(name, getContentModel().getId(),
                            contentStream
                                .getContent().getContent()
                                .getBytes(XmlUtility.CHARACTER_ENCODING),
                            contentStream.getMimeType());
                }
                catch (UnsupportedEncodingException e) {
                    throw new WebserverSystemException(e);
                }
            }
            else if (contentStream.getContent().getDataLocation() != null) {
                ds =
                    new Datastream(
                        name,
                        getContentModel().getId(),
                        contentStream.getContent().getDataLocation().toString(),
                        contentStream
                            .getContent().getStorageType().getESciDocName(),
                        contentStream.getMimeType());
            }
            else {
                throw new IntegritySystemException(
                    "Content streams has neither href nor content.");
            }
            String title = contentStream.getTitle();
            if (title == null) {
                title = "";
            }
            ds.setLabel(title.trim());
            contentStreamDatastreams.put(name, ds);
        }

        getContentModel().setContentStreams(contentStreamDatastreams);
    }

    /**
     * Check if the requested item version is the latest version.
     * 
     * @throws ReadonlyVersionException
     *             If the requested item version is not the latest one.
     * 
     */
    protected void checkLatestVersion() throws ReadonlyVersionException {
        final String thisVersion = getContentModel().getVersionNumber();
        if (thisVersion != null
            && !thisVersion.equals(getContentModel().getLatestVersionNumber())) {
            throw new ReadonlyVersionException(
                "Only latest version can be modified.");
        }
    }

    /**
     * Validate the Content Model structure in general (independent if create or
     * ingest was selected).
     * 
     * Checks if all required values are set and consistent.
     * 
     * @param item
     *            The item which is to validate.
     * @throws InvalidStatusException
     *             Thrown if Item has invalid status.
     */
    private void validate(final ContentModelCreate item)
        throws InvalidContentException {

        // check public status of Content Model
        StatusType publicStatus =
            item.getProperties().getObjectProperties().getStatus();

        if (publicStatus != StatusType.PENDING) {

            log.debug("New Content Model has to be in public-status '"
                + StatusType.PENDING + "'.");
            item.getProperties().getObjectProperties()
                .setStatus(StatusType.PENDING);
        }

        // validate Metadata Records
        checkMetadataRecords(item);
    }

    /**
     * Check if the name attribute of Metadata Records is unique and at least
     * one Metadata Record has the name "escidoc".
     * 
     * @param item
     *            Item which is to validate.
     * @throws InvalidContentException
     *             Thrown if content is invalid.
     */
    private void checkMetadataRecords(final ContentModelCreate item)
        throws InvalidContentException {

        Vector<MdRecordCreate> mdRecords = item.getMetadataRecords();

        if ((mdRecords == null) || mdRecords.size() < 1) {
            // String message =
            // "The Item representation doesn't contain a "
            // + "mandatory md-record. A regular Item must contain a "
            // + "mandatory md-record. ";
            // log.error(message);
            // throw new MissingMdRecordException(message);
        }
        else {

            Vector<String> mdRecordNames = new Vector<String>();
            String name = null;
            for (int i = 0; i < mdRecords.size(); i++) {

                name = mdRecords.get(i).getName();

                // check uniqueness of names
                if (mdRecordNames.contains(name)) {
                    throw new InvalidContentException(
                        "Metadata 'md-record' with name='" + name
                            + "' exists multiple times.");
                }

                mdRecordNames.add(name);
            }
            // if (!mdRecordNames.contains(Elements.MANDATORY_MD_RECORD_NAME)
            // && item.getProperties().getObjectProperties().getOrigin() ==
            // null) {
            // String message =
            // "The item representation doesn't contain a "
            // + "mandatory md-record. A regular item must contain a "
            // + "mandatory md-record. ";
            // log.error(message);
            // throw new MissingMdRecordException(message);
            // }
        }
    }

    /**
     * @param xml
     * @return
     * @throws WebserverSystemException
     *             If an error occurs.
     * @throws InvalidContentException
     *             If invalid content is found.
     * @throws MissingAttributeValueException
     *             If a required attribute can not be found.
     * @throws XmlParserSystemException
     *             If an unexpected error occurs while parsing.
     * @throws XmlCorruptedException
     *             Thrown if the schema validation of the provided data failed.
     */
    private ContentModelCreate parseContentModel(final String xml)
        throws WebserverSystemException, InvalidContentException,
        MissingAttributeValueException, XmlParserSystemException,
        XmlCorruptedException {

        StaxParser sp = new StaxParser();

        ContentModelCreateHandler contentModelHandler =
            new ContentModelCreateHandler(sp);
        sp.addHandler(contentModelHandler);

        try {
            sp.parse(xml);
        }
        catch (WebserverSystemException e) {
            throw e;
        }
        catch (MissingAttributeValueException e) {
            throw e;
        }
        catch (XmlCorruptedException e) {
            throw e;
        }
        catch (InvalidContentException e) {
            throw e;
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }

        return contentModelHandler.getContentModel();
    }

    /**
     * Check if the content model is locked.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @throws LockingException
     *             If the item is locked and the current user is not the one who
     *             locked it.
     */
    protected void checkLocked() throws LockingException,
        WebserverSystemException {
        if (getContentModel().isLocked()
            && !getContentModel().getLockOwner().equals(
                Utility.getInstance().getCurrentUser()[0])) {
            throw new LockingException("Content Model + "
                + getContentModel().getId() + " is locked by "
                + getContentModel().getLockOwner() + ".");
        }
    }

    /**
     * Set the Content Model cache.
     * 
     * @param contentModelCache
     *            Content Model cache
     * @spring.property ref="contentModel.DbContentModelCache"
     */
    public void setContentModelCache(
        final ResourceCacheInterface contentModelCache) {
        this.contentModelCache = contentModelCache;
        contentModelListeners.add(contentModelCache);
    }

    /**
     * Injects the indexing handler.
     * 
     * @spring.property ref="common.business.indexing.IndexingHandler"
     * @param indexingHandler
     *            The indexing handler.
     */
    public void setIndexingHandler(final IndexingHandler indexingHandler) {
        contentModelListeners.add(indexingHandler);
    }

    /**
     * Set the ExplainRequest object.
     * 
     * @param explainRequest
     *            ExplainRequest
     * 
     * @spring.property 
     *                  ref="de.escidoc.core.common.business.filter.ExplainRequest"
     */
    public void setExplainRequest(final ExplainRequest explainRequest) {
        this.explainRequest = explainRequest;
    }

    /**
     * See Interface for functional description.
     * 
     * @param fedoraUtility
     * @see de.escidoc.core.common.business.fedora.HandlerBase
     *      #setFedoraUtility(de.escidoc.core.common.business.fedora.FedoraUtility)
     * 
     * @spring.property ref="escidoc.core.business.FedoraUtility"
     */
    @Override
    public void setFedoraUtility(final FedoraUtility fedoraUtility) {

        super.setFedoraUtility(fedoraUtility);
    }

    /**
     * See Interface for functional description.
     * 
     * @param idProvider
     * @see de.escidoc.core.common.business.fedora.HandlerBase
     *      #setIdProvider(de.escidoc.core.common.persistence.EscidocIdProvider)
     * 
     * @spring.property ref="escidoc.core.business.EscidocIdProvider"
     */
    @Override
    public void setIdProvider(final EscidocIdProvider idProvider) {

        super.setIdProvider(idProvider);
    }

    public String ingest(final String xmlData) throws EscidocException {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    /**
     * Notify the listeners that a Content Model was modified.
     * 
     * @param id
     *            Content Model id
     * @param xmlData
     *            complete Content Model XML
     * 
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    private void fireContentModelModified(final String id, final String xmlData)
        throws SystemException {
        String restXml = null;
        String soapXml = null;

        if (UserContext.isRestAccess()) {
            restXml = xmlData;
            soapXml = getAlternateForm();
        }
        else {
            restXml = getAlternateForm();
            soapXml = xmlData;
        }
        for (int index = 0; index < contentModelListeners.size(); index++) {
            (contentModelListeners.get(index)).resourceModified(id, restXml,
                soapXml);
        }
    }

    /**
     * Notify the listeners that an Content Model was created.
     * 
     * @param id
     *            Content Model id
     * @param xmlData
     *            complete Content Model XML
     * 
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    private void fireContentModelCreated(final String id, final String xmlData)
        throws SystemException {
        String restXml = null;
        String soapXml = null;

        if (UserContext.isRestAccess()) {
            restXml = xmlData;
            soapXml = getAlternateForm();
        }
        else {
            restXml = getAlternateForm();
            soapXml = xmlData;
        }
        for (int index = 0; index < contentModelListeners.size(); index++) {
            (contentModelListeners.get(index)).resourceCreated(id, restXml,
                soapXml);
        }
    }

    /**
     * Notify the listeners that an Content Model was deleted.
     * 
     * @param id
     *            Content Model id
     * 
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    private void fireContentModelDeleted(final String id)
        throws SystemException {
        for (int index = 0; index < contentModelListeners.size(); index++) {
            (contentModelListeners.get(index)).resourceDeleted(id);
        }
    }

    /**
     * Get the alternate form of a Content Model representation. If the current
     * request came in via REST, then the SOAP form will be returned here and
     * vice versa.
     * 
     * @return alternate form of the Content Model
     * 
     * @throws SystemException
     *             Thrown if an internal error occurred.
     */
    private String getAlternateForm() throws SystemException {
        String result = null;
        boolean isRestAccess = UserContext.isRestAccess();

        try {
            UserContext.setRestAccess(!isRestAccess);
            result = render();
        }
        catch (Exception e) {
            throw new SystemException(e);
        }
        finally {
            UserContext.setRestAccess(isRestAccess);
        }
        return result;
    }
}
