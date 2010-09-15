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
package de.escidoc.core.om.business.fedora.item;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.PropertyMapKeys;
import de.escidoc.core.common.business.TripleStoreConnector;
import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.CqlFilter;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.business.fedora.resources.XmlFilter;
import de.escidoc.core.common.business.fedora.resources.create.ItemCreate;
import de.escidoc.core.common.business.fedora.resources.create.MdRecordCreate;
import de.escidoc.core.common.business.fedora.resources.create.RelationCreate;
import de.escidoc.core.common.business.fedora.resources.interfaces.FilterInterface;
import de.escidoc.core.common.business.fedora.resources.item.Component;
import de.escidoc.core.common.business.filter.SRURequest;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreOutputFormatException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingLicenceException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.XmlSchemaNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyPublishedException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyWithdrawnException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.NotPublishedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.PidAlreadyAssignedException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException;
import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.MultipleExtractor;
import de.escidoc.core.common.util.stax.handler.OptimisticLockingHandler;
import de.escidoc.core.common.util.stax.handler.TaskParamHandler;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ExplainXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;
import de.escidoc.core.om.business.fedora.ContentRelationsUtility;
import de.escidoc.core.om.business.fedora.contentRelation.FedoraContentRelationHandler;
import de.escidoc.core.om.business.interfaces.ItemHandlerInterface;
import de.escidoc.core.om.business.security.UserFilter;
import de.escidoc.core.om.business.stax.handler.ContentRelationsAddHandler2Edition;
import de.escidoc.core.om.business.stax.handler.ContentRelationsRemoveHandler2Edition;
import de.escidoc.core.om.business.stax.handler.ContentRelationsUpdateHandler2Edition;
import de.escidoc.core.om.business.stax.handler.MdRecordsUpdateHandler;
import de.escidoc.core.om.business.stax.handler.component.NewComponentExtractor;
import de.escidoc.core.om.business.stax.handler.filter.RDFRegisteredOntologyFilter;
import de.escidoc.core.om.business.stax.handler.item.ComponentMdRecordsUpdateHandler;
import de.escidoc.core.om.business.stax.handler.item.ComponentUpdateHandler;
import de.escidoc.core.om.business.stax.handler.item.ContentStreamHandler;
import de.escidoc.core.om.business.stax.handler.item.ItemHandler;
import de.escidoc.core.om.business.stax.handler.item.ItemUpdateHandler;

/**
 * The retrieve, update, create and delete methods implement the
 * {@link de.escidoc.core.om.business.interfaces.ItemHandlerInterface
 * ItemHandlerInterface}. These methods handle strings of xmlData and use the
 * private (get,) set and render methods to set xmlData in the system or get
 * xmlData from the system.
 * <p>
 * The private set methods take strings of xmlData as parameter and handling
 * objects of type
 * {@link de.escidoc.core.common.business.fedora.datastream.Datastream
 * Datastream} that hold the xmlData in an Item or Component object.
 * <p>
 * To split incoming xmlData into the datastreams it consists of, the
 * {@link de.escidoc.core.common.util.stax.StaxParser StaxParser} is used. In
 * order to modify datastreams or handle values provided in datastreams more
 * than one Handler (implementations of
 * {@link de.escidoc.core.common.util.xml.stax.handler.DefaultHandler
 * DefaultHandler}) can be added to the StaxParser. The
 * {@link de.escidoc.core.common.util.stax.handler.MultipleExtractor
 * MultipleExtractor} have to be the last Handler in the HandlerChain of a
 * StaxParser.
 * 
 * @spring.bean id="business.FedoraItemHandler" scope="prototype"
 * @author FRS
 * 
 * @om
 */
public class FedoraItemHandler extends ItemHandlerPid
    implements ItemHandlerInterface {

    private static AppLogger log =
        new AppLogger(FedoraItemHandler.class.getName());

    private FedoraContentRelationHandler contentRelationHandler = null;

    /** The policy decision point used to check access privileges. */
    private PolicyDecisionPointInterface pdp;

    /**
     * FedoraItemHandler.
     */
    public FedoraItemHandler() {
    }

    /**
     * Gets the {@link PolicyDecisionPointInterface} implementation.
     * 
     * @return PolicyDecisionPointInterface
     */
    protected PolicyDecisionPointInterface getPdp() {

        return pdp;
    }

    /**
     * Injects the {@link PolicyDecisionPointInterface} implementation.
     * 
     * @param pdp
     *            the {@link PolicyDecisionPointInterface} to be injected.
     * @spring.property ref="service.PolicyDecisionPointBean"
     */
    public void setPdp(final PolicyDecisionPointInterface pdp) {

        this.pdp = pdp;
    }

    // CHECKSTYLE:JAVADOC-OFF
    /**
     * @param id
     * @return
     * @throws ItemNotFoundException
     * @throws ComponentNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#retrieve(java.lang.String)
     */
    public String retrieve(final String id) throws ItemNotFoundException,
        MissingMethodParameterException, SystemException,
        ComponentNotFoundException, AuthorizationException {

        setItem(id);
        String message =
            "You cannot access a full surrogate item representation"
                + " because you have no access rights on the item "
                + getOriginId()
                + " . You can access subressourcess owned by a "
                + "surrogate item using retrieve methods on " + "subresources.";
        loadOrigin(message);

        return render();
    }

    /**
     * @param id
     * @param xmlData
     * @return new XML representation of updated Item.
     * @throws ItemNotFoundException
     * @throws FileNotFoundException
     * @throws InvalidContextException
     * @throws InvalidStatusException
     * @throws LockingException
     * @throws NotPublishedException
     * @throws MissingLicenceException
     * @throws ComponentNotFoundException
     * @throws MissingAttributeValueException
     * @throws AlreadyPublishedException
     * @throws InvalidXmlException
     * @throws MissingMethodParameterException
     * @throws InvalidContentException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws AlreadyExistsException
     * @throws ReadonlyViolationException
     * @throws RelationPredicateNotFoundException
     * @throws ReferencedResourceNotFoundException
     * @throws ReadonlyVersionException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#update(java.lang.String,
     *      java.lang.String)
     */
    public String update(final String id, final String xmlData)
        throws ItemNotFoundException, FileNotFoundException,
        InvalidContextException, InvalidStatusException, LockingException,
        NotPublishedException, MissingLicenceException,
        ComponentNotFoundException, MissingAttributeValueException,
        InvalidXmlException, MissingMethodParameterException,
        InvalidContentException, SystemException, OptimisticLockingException,
        ReadonlyViolationException, RelationPredicateNotFoundException,
        ReferencedResourceNotFoundException, ReadonlyVersionException,
        MissingMdRecordException, AuthorizationException {

        setItem(id);
        String startTimestamp = getItem().getLastFedoraModificationDate();

        checkLatestVersion();
        checkLocked();
        checkWithdrawn("No update allowed.");
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ":" + originVersionId;
        }
        boolean origin =
            loadOrigin("You cannot update a full surrogate Item representation "
                + "because you have no access rights on the Item '"
                + originId
                + "'. Subressources which are part of the surrogate Item "
                + "(and not the origin Item) are still accessible. Try "
                + "using subresource methods.");

        try {
            StaxParser sp = new StaxParser();

            OptimisticLockingHandler olh =
                new OptimisticLockingHandler(getItem().getId(),
                    Constants.ITEM_OBJECT_TYPE, getItem()
                        .getLastModificationDate(), sp);
            sp.addHandler(olh);
            ContentRelationsUpdateHandler2Edition cruh =
                new ContentRelationsUpdateHandler2Edition(sp);
            sp.addHandler(cruh);

            sp.addHandler(new ItemUpdateHandler(getItem().getId(), sp));
            // ItemPropertiesUpdateHandler ipuh =
            // new ItemPropertiesUpdateHandler(getItem(), "/item/properties",
            // sp);
            // sp.addHandler(ipuh);
            MdRecordsUpdateHandler mdHandler =
                new MdRecordsUpdateHandler("/item/md-records", sp, origin);
            sp.addHandler(mdHandler);

            ContentStreamHandler csh = null;
            ComponentUpdateHandler cuh = null;
            NewComponentExtractor nce = null;
            ComponentMdRecordsUpdateHandler cmuh = null;

            if (!origin) {
                csh = new ContentStreamHandler(getItem());
                sp.addHandler(csh);
                cuh =
                    new ComponentUpdateHandler(getItem().getId(),
                        "/item/components/component", sp);
                sp.addHandler(cuh);
                nce = new NewComponentExtractor(sp);
                cmuh =
                    new ComponentMdRecordsUpdateHandler(
                        "/item/components/component", sp);

                sp.addHandler(cmuh);
                sp.addHandler(nce);
            }
            HashMap<String, String> extractPathes =
                new HashMap<String, String>();
            extractPathes.put("/item/properties/"
                + Elements.ELEMENT_CONTENT_MODEL_SPECIFIC + "", null);
            extractPathes.put("/item/relations", null);
            extractPathes.put("/item/resources", null);
            extractPathes.put("/item/md-records/md-record", "name");
            // extractPathes.put("/item/components/component", "objid");
            if (!origin) {
                extractPathes
                    .put("/item/components/component/properties", null);
                extractPathes.put("/item/components/component/content", null);
                extractPathes.put(
                    "/item/components/component/md-records/md-record", "name");
            }
            MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
            sp.addHandler(me);

            try {
                sp.parse(xmlData);
            }
            catch (OptimisticLockingException e) {
                throw e;
            }
            catch (MissingAttributeValueException e) {
                throw e;
            }
            catch (MissingMdRecordException e) {
                throw e;
            }
            catch (WebserverSystemException e) {
                throw e;
            }
            catch (InvalidContentException e) {
                throw e;
            }
            catch (InvalidXmlException e) {
                throw e;
            }
            catch (ReferencedResourceNotFoundException e) {
                throw e;
            }
            catch (RelationPredicateNotFoundException e) {
                throw e;
            }
            catch (TripleStoreSystemException e) {
                throw e;
            }
            catch (EncodingSystemException e) {
                throw e;
            }
            catch (XmlParserSystemException e) {
                throw e;
            }
            catch (XMLStreamException e) {
                throw new XmlParserSystemException(e);
            }
            catch (Exception e) {
                XmlUtility.handleUnexpectedStaxParserException("", e);
            }
            sp.clearHandlerChain();

            HashMap<String, Object> streams = me.getOutputStreams();
            try {
                Object cmsStream =
                    streams.get(Elements.ELEMENT_CONTENT_MODEL_SPECIFIC);
                if (cmsStream != null) {
                    setContentTypeSpecificProperties(((ByteArrayOutputStream) cmsStream)
                        .toString(XmlUtility.CHARACTER_ENCODING));
                }
            }
            catch (UnsupportedEncodingException e) {
                throw new EncodingSystemException(e.getMessage(), e);
            }
            HashMap mdRecordsStreams = (HashMap) streams.get("md-records");
            if ((mdRecordsStreams != null)
                && !mdRecordsStreams.containsKey("escidoc") && !origin) {
                throw new MissingMdRecordException(
                    "No escidoc internal metadata found "
                        + "(md-record/@name='escidoc'");
            }
            Map<String, Map<String, String>> mdRecordsAttributes =
                mdHandler.getMetadataAttributes();
            String escidocMdNsUri = mdHandler.getEscidocMdRecordNameSpace();
            setMetadataRecords(mdRecordsStreams, mdRecordsAttributes,
                escidocMdNsUri);

            // set content streams
            if (!origin) {
                setContentStreams(csh.getContentStreams());
            }
            // set content relations
            Vector<String> relationsToUpdate = cruh.getContentRelationsData();
            getItem().setContentRelations(sp, relationsToUpdate);

            // components
            // TODO: Aenderungen an ITEM.RELS-EXT fuer alle components in einem
            // schritt machen + Aenderungen an content relations (? FRS)
            boolean resourceUpdated = false;
            if (!origin) {
                HashMap<String, Object> components =
                    (HashMap<String, Object>) streams.get("components");

                if (components == null) {
                    components = new HashMap<String, Object>();
                }
                HashMap<String, HashMap<String, HashMap<String, String>>> componentMdRecordsAttributes =
                    cmuh.getMetadataAttributes();

                Map<String, String> nsUris = cmuh.getNamespacesMap();
                components.put("new", nce.getOutputStreams());
                setComponents(components, componentMdRecordsAttributes, nsUris);

                // this persist is necessary to control the Components
                resourceUpdated = getItem().persistComponents();
            }

            // check if modified
            String updatedXmlData = null;
            String endTimestamp = getItem().getLastFedoraModificationDate();
            if (resourceUpdated || !startTimestamp.equals(endTimestamp)
                || getItem().isNewVersion()) {
                // object is modified
                makeVersion("ItemHandler.update()");
                getItem().persist();

                updatedXmlData = retrieve(getItem().getId());
                fireItemModified(getItem().getId(), updatedXmlData);
            }
            else {
                updatedXmlData = render();
            }

            return updatedXmlData;
        }
        catch (MissingContentException e) {
            throw new WebserverSystemException(e);
        }
        catch (MissingElementValueException e) {
            throw new WebserverSystemException("unreachable");
        }
    }

    /**
     * Create an Item.
     * 
     * @param xml
     * @return
     * @throws MissingContentException
     * @throws ContextNotFoundException
     * @throws ContentModelNotFoundException
     * @throws ReadonlyElementViolationException
     * @throws MissingAttributeValueException
     * @throws MissingElementValueException
     * @throws ReadonlyAttributeViolationException
     * @throws XmlCorruptedException
     * @throws MissingMethodParameterException
     * @throws FileNotFoundException
     * @throws SystemException
     * @throws ReferencedResourceNotFoundException
     * @throws InvalidContentException
     * @throws RelationPredicateNotFoundException
     * @throws InvalidStatusException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#create(java.lang.String)
     */
    public String create(final String xml) throws MissingContentException,
        ContextNotFoundException, ContentModelNotFoundException,
        ReadonlyElementViolationException, MissingAttributeValueException,
        MissingElementValueException, ReadonlyAttributeViolationException,
        XmlCorruptedException, MissingMethodParameterException,
        FileNotFoundException, SystemException,
        ReferencedResourceNotFoundException, InvalidContentException,
        RelationPredicateNotFoundException, MissingMdRecordException,
        InvalidStatusException, AuthorizationException {

        ItemCreate item = parseItem(xml);

        // check that the objid was not obtained from the representation
        item.setObjid(null);

        item.setIdProvider(getIdProvider());
        validateCreate(item);
        item.persist(true);
        String objid = item.getObjid();
        String resultItem = null;
        try {
            resultItem = retrieve(objid);
        }
        catch (ResourceNotFoundException e) {
            String msg =
                "The Item with id '" + objid + "', which was just created, "
                    + "could not be found for retrieve.";
            log.warn(msg);
            throw new IntegritySystemException(msg, e);
        }
        fireItemCreated(objid, resultItem);
        return resultItem;
    }

    /**
     * Ingest an item.
     * 
     * @param xml
     *            The item to be ingested.
     * 
     * @throws SystemException
     * @throws InvalidStatusException
     * @throws MissingMdRecordException
     * @throws RelationPredicateNotFoundException
     * @throws InvalidContentException
     * @throws ReferencedResourceNotFoundException
     * @throws FileNotFoundException
     * @throws MissingMethodParameterException
     * @throws InvalidXmlException
     * @throws MissingElementValueException
     * @throws MissingAttributeValueException
     * @throws ContentModelNotFoundException
     * @throws ContextNotFoundException
     * @throws MissingContentException
     * @throws ReadonlyAttributeViolationException
     * @throws ReadonlyElementViolationException
     * 
     * @return the pid of the ingested item.
     * 
     */
    public String ingest(final String xml)
        throws ReadonlyElementViolationException,
        ReadonlyAttributeViolationException, MissingContentException,
        ContextNotFoundException, ContentModelNotFoundException,
        MissingAttributeValueException, MissingElementValueException,
        InvalidXmlException, MissingMethodParameterException,
        FileNotFoundException, ReferencedResourceNotFoundException,
        InvalidContentException, RelationPredicateNotFoundException,
        MissingMdRecordException, InvalidStatusException, SystemException,
        AuthorizationException {

        ItemCreate item = parseItem(xml);
        item.setIdProvider(getIdProvider());
        validateIngest(item);
        item.persist(true);
        String objid = item.getObjid();
        try {
            if (getDbResourceCache().isEnabled()) {
                fireItemCreated(objid, retrieve(objid));
            }
        }
        catch (ResourceNotFoundException e) {
            String msg =
                "The Item with id '" + objid + "', which was just ingested, "
                    + "could not be found for retrieve.";
            log.warn(msg);
            throw new IntegritySystemException(msg, e);
        }
        return objid;

    }

    /**
     * @param id
     * @throws ItemNotFoundException
     * @throws AlreadyPublishedException
     * @throws LockingException
     * @throws InvalidStatusException
     * @throws ComponentNotFoundException
     * @throws SystemException
     * @throws AuthorizationException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#delete(java.lang.String)
     */
    public void delete(final String id) throws ItemNotFoundException,
        AlreadyPublishedException, LockingException, InvalidStatusException,
        SystemException, AuthorizationException {

        remove(id);
        fireItemDeleted(id);
    }

    /**
     * @param id
     * @return
     * @throws ItemNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#retrieveProperties(java.lang.String)
     */
    public String retrieveProperties(final String id)
        throws ItemNotFoundException, MissingMethodParameterException,
        SystemException {

        setItem(id);
        return renderProperties();
    }

    /**
     * @param id
     * @return
     * @throws ItemNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#retrieveMdRecords(java.lang.String)
     */
    public String retrieveMdRecords(final String id)
        throws ItemNotFoundException, MissingMethodParameterException,
        SystemException, AuthorizationException {

        setItem(id);
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ":" + originVersionId;
        }
        loadOrigin("You cannot retrieve md-records of the surrogate Item "
            + "because you have no access rights on the Item '" + originId
            + "'. Subressources which are part of the surrogate Item "
            + "(and not the origin Item) are still accessible. Try "
            + "using subresource methods.");

        return renderMdRecords(true);
    }

    /**
     * @param id
     * @param mdRecordId
     * @return
     * @throws ItemNotFoundException
     * @throws MdRecordNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#retrieveMdRecord(java.lang.String,java.lang.String)
     */
    public String retrieveMdRecord(final String id, final String mdRecordId)
        throws ItemNotFoundException, MdRecordNotFoundException,
        MissingMethodParameterException, SystemException,
        AuthorizationException {

        setItem(id);

        String mdRecord = null;
        try {
            mdRecord = renderMdRecord(mdRecordId, false, true);
            if (mdRecord.length() == 0) {
                throw new MdRecordNotFoundException();
            }
        }
        catch (MdRecordNotFoundException e) {
            String originObjectId =
                getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
            if (originObjectId != null) {
                String message =
                    "Md-record with name " + mdRecordId
                        + " is owned by the item " + getOriginId()
                        + ", which is reffered by a surrogate item " + id
                        + ". You have no access rights on the item "
                        + getOriginId()
                        + ". Therefore you cannot access any md-records "
                        + "of this item.";
                loadOrigin(message);
                mdRecord = renderMdRecord(mdRecordId, true, true);
            }
            else {
                throw e;
            }
        }

        if (mdRecord.length() == 0) {
            String message =
                "Metadata record with name " + mdRecordId
                    + " not found in item " + id + ".";
            log.debug(message);
            throw new MdRecordNotFoundException(message);
        }
        return mdRecord;
    }

    /**
     * This Method is used by OAI Provider to retrieve metadata without the
     * surrounding md-record element.
     * 
     * @param id
     * @param mdRecordId
     * @throws ItemNotFoundException
     * @throws MdRecordNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws AuthorizationException
     * @return
     * 
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#retrieveMdRecordContent(java.lang.String,java.lang.String)
     */
    public String retrieveMdRecordContent(
        final String id, final String mdRecordId) throws ItemNotFoundException,
        MdRecordNotFoundException, MissingMethodParameterException,
        SystemException, AuthorizationException {
        setItem(id);
        String mdRecord = null;
        try {
            mdRecord = retrieveMdRecord(mdRecordId, false);
        }
        catch (MdRecordNotFoundException e) {
            String message =
                "Md-record with name " + mdRecordId + " is owned by the item "
                    + getOriginId()
                    + ", which is reffered by a surrogate item " + id
                    + ". You have no access rights on the item "
                    + getOriginId()
                    + ". Therefore you cannot access any md-records "
                    + "of this item.";
            boolean origin = loadOrigin(message);
            if (origin) {
                mdRecord = retrieveMdRecord(mdRecordId, true);
            }
            else {
                message =
                    "Metadata record with name " + mdRecordId
                        + " not found in item " + id + ".";
                log.debug(message);
                throw new MdRecordNotFoundException(message);
            }
        }
        return mdRecord;
    }

    /**
     * @param id
     * @return
     * @throws ItemNotFoundException
     * @throws MdRecordNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws AuthorizationException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#retrieveMdRecordContent(java.lang.String,java.lang.String)
     */
    public String retrieveDcRecordContent(final String id)
        throws ItemNotFoundException, MissingMethodParameterException,
        SystemException, MdRecordNotFoundException, AuthorizationException {
        setItem(id);
        Datastream mdRecord = null;
        String dc = null;
        try {
            mdRecord =
                getItem().getMdRecord(
                    XmlTemplateProvider.DEFAULT_METADATA_FOR_DC_MAPPING);
            if (mdRecord.isDeleted()) {
                throw new MdRecordNotFoundException();
            }
            dc = getItem().getDc().toString();
        }
        catch (MdRecordNotFoundException e) {
            String message =
                "Md-record with name DC" + " is owned by the item "
                    + getOriginId()
                    + ", which is reffered by a surrogate item " + id
                    + ". You have no access rights on the item "
                    + getOriginId()
                    + ". Therefore you cannot access any md-records "
                    + "of this item.";
            boolean origin = loadOrigin(message);
            if (origin) {
                dc = getOriginItem().getDc().toString();
            }
            else {
                message =
                    "Metadata record with name DC" + " not found in item " + id
                        + ".";
                log.debug(message);
                throw new MdRecordNotFoundException(message);
            }

        }
        return dc;
    }

    /**
     * @param id
     * @param mdRecordId
     * @param xmlData
     * @return
     * @throws ItemNotFoundException
     * @throws XmlSchemaNotFoundException
     * @throws LockingException
     * @throws XmlCorruptedException
     * @throws XmlSchemaValidationException
     * @throws InvalidContentException
     * @throws MdRecordNotFoundException
     * @throws ReadonlyViolationException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws InvalidStatusException
     * @throws ReadonlyVersionException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#updateMdRecord(java.lang.String,java.lang.String,
     *      java.lang.String)
     */
    public String updateMetadataRecord(
        final String id, final String mdRecordId, final String xmlData)
        throws ItemNotFoundException, XmlSchemaNotFoundException,
        LockingException, XmlCorruptedException, XmlSchemaValidationException,
        InvalidContentException, MdRecordNotFoundException,
        ReadonlyViolationException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidStatusException,
        ReadonlyVersionException, AuthorizationException {

        setItem(id);
        String startTimestamp = getItem().getLastFedoraModificationDate();

        checkLatestVersion();
        checkLocked();
        checkReleased();
        checkWithdrawn("No update allowed.");

        String newMdRecord = null;

        StaxParser sp = new StaxParser();
        OptimisticLockingHandler olh =
            new OptimisticLockingHandler(getItem().getId(),
                Constants.ITEM_OBJECT_TYPE,
                getItem().getLastModificationDate(), sp);
        sp.addHandler(olh);
        MdRecordsUpdateHandler mdHandler = new MdRecordsUpdateHandler("", sp);
        sp.addHandler(mdHandler);

        MultipleExtractor me = new MultipleExtractor("/md-record", "name", sp);
        sp.addHandler(me);

        try {
            sp.parse(xmlData);
        }
        catch (XMLStreamException e) {
            // the only exception thrown by MultipleExtractor
            throw new XmlParserSystemException(e);
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException(e.getMessage(), e);
        }

        HashMap mds = (HashMap) me.getOutputStreams().get("md-records");
        // there is only one md-record (root element is md-record)
        ByteArrayOutputStream mdXml =
            (ByteArrayOutputStream) mds.get(mdRecordId);
        Map<String, Map<String, String>> mdAttributes =
            mdHandler.getMetadataAttributes();
        Map<String, String> mdRecordAttributes = mdAttributes.get(mdRecordId);
        try {
            setMetadataRecord(mdRecordId, mdXml
                .toString(XmlUtility.CHARACTER_ENCODING), mdRecordAttributes);
        }
        catch (UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }

        String endTimestamp = getItem().getLastFedoraModificationDate();
        if (!startTimestamp.equals(endTimestamp)) {
            makeVersion("Item.updateMedataRecord");
            getItem().persist();
            try {
                fireItemModified(getItem().getId());
            }
            catch (ComponentNotFoundException e) {
                throw new SystemException(e);
            }
        }
        try {
            newMdRecord = retrieveMdRecord(getItem().getId(), mdRecordId);
        }
        catch (MdRecordNotFoundException e) {
            throw new IntegritySystemException(
                "After succesfully update metadata.", e);
        }

        return newMdRecord;
    }

    /**
     * @param id
     * @param xmlData
     * @return
     * @throws ItemNotFoundException
     * @throws XmlSchemaNotFoundException
     * @throws SystemException
     * @throws XmlSchemaValidationException
     * @throws LockingException
     * @throws MissingAttributeValueException
     * @throws InvalidStatusException
     * @throws MissingMethodParameterException
     * @throws XmlCorruptedException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#createMetadataRecord(java.lang.String,java.lang.String)
     */
    @Deprecated
    public String createMetadataRecord(final String id, final String xmlData)
        throws ItemNotFoundException, XmlSchemaNotFoundException,
        SystemException, XmlSchemaValidationException, LockingException,
        MissingAttributeValueException, InvalidStatusException,
        MissingMethodParameterException, XmlCorruptedException,
        ComponentNotFoundException, AuthorizationException {

        return createMdRecord(id, xmlData);
    }

    /**
     * Create Metadata Record.
     * 
     * @param id
     *            objid of Item
     * @param xmlData
     *            XML of new Metadata Record
     * @return XML representation of created Metadata Record
     * 
     * @throws ItemNotFoundException
     * @throws XmlSchemaNotFoundException
     * @throws SystemException
     * @throws XmlSchemaValidationException
     * @throws LockingException
     * @throws MissingAttributeValueException
     * @throws InvalidStatusException
     * @throws MissingMethodParameterException
     * @throws XmlCorruptedException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#createMdRecord(java.lang.String,java.lang.String)
     */
    public String createMdRecord(final String id, final String xmlData)
        throws ItemNotFoundException, SystemException,
        XmlSchemaValidationException, LockingException,
        MissingAttributeValueException, InvalidStatusException,
        ComponentNotFoundException, AuthorizationException {

        setItem(id);
        String startTimestamp = getItem().getLastFedoraModificationDate();

        checkLocked();
        checkReleased();

        String newMdRecord = null;

        HashMap<String, String> extractPathes = new HashMap<String, String>();
        extractPathes.put("/md-record", "name");

        StaxParser sp = new StaxParser();
        MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
        sp.addHandler(me);

        // retrieve name from xml
        try {
            sp.parse(xmlData);
        }
        catch (Exception e) {
            throw new WebserverSystemException(
                "Unexpected exception while parsing xml data in "
                    + "FedoraItemHandler.createMetadataRecord.", e);
        }
        HashMap map = me.getOutputStreams();
        HashMap mdRecords = (HashMap) map.get("md-records");
        Set keySet = mdRecords.keySet();
        Iterator it = keySet.iterator();
        if (!it.hasNext()) {
            throw new XmlSchemaValidationException(
                "No name found for metadata datastream.");
        }
        String name = (String) it.next();

        byte[] xmlDataBytes = null;
        try {
            xmlDataBytes = xmlData.getBytes(XmlUtility.CHARACTER_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }
        Datastream newMDS =
            new Datastream(name, getItem().getId(), xmlDataBytes, "text/xml");
        newMDS.addAlternateId(Datastream.METADATA_ALTERNATE_ID); // this is the
        // reason for
        // setMdRecord etc.
        // FIXME persist DS by set it in the resource object
        newMDS.persist(false);

        String endTimestamp = getItem().getLastFedoraModificationDate();
        if (!startTimestamp.equals(endTimestamp)) {
            makeVersion("Item.createMetadataRecord");
            getItem().persist();
        }
        try {
            newMdRecord = retrieveMdRecord(getItem().getId(), name);
        }
        catch (ItemNotFoundException e) {
            throw new IntegritySystemException(
                "After succesfully create metadata.", e);
        }
        catch (MdRecordNotFoundException e) {
            throw new IntegritySystemException(
                "After succesfully create metadata.", e);
        }
        catch (MissingMethodParameterException e) {
            throw new IntegritySystemException(
                "After succesfully create metadata.", e);
        }
        fireItemModified(getItem().getId());

        return newMdRecord;
    }

    /**
     * @param id
     * @return
     * @throws ItemNotFoundException
     * @throws ComponentNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#retrieveComponents(java.lang.String)
     */
    public String retrieveComponents(final String id)
        throws ItemNotFoundException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException,
        AuthorizationException {

        setItem(id);
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ":" + originVersionId;
        }
        loadOrigin("You have no access rights on the item " + originId
            + " , which is reffered by a surrogate item " + id
            + ". Therefore you cannot access any subressources of this item.");

        return renderComponents(true);
    }

    /**
     * Retrieve all content relation in which the current item is subject or
     * object.
     * 
     * @param id
     *            item id
     * 
     * @return list of content relations
     * @throws ItemNotFoundException
     *             Thrown if an item with the specified id could not be found.
     * @throws SystemException
     *             If an error occurs.
     */
    private String retrieveContentRelations(final String id)
        throws ItemNotFoundException, SystemException {
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        String result = null;

        setItem(id);
        filterParams.put("query", new String[] { "\"/subject/id\"="
            + getItem().getId() + " or " + "\"/subject/id\"="
            + getItem().getFullId() + " or " + "\"/object/id\"="
            + getItem().getId() + " or " + "\"/object/id\"="
            + getItem().getFullId() });

        try {
            String searchResponse =
                contentRelationHandler.retrieveContentRelations(filterParams);
            result = transformSearchResponse2relations(searchResponse);
        }
        catch (InvalidSearchQueryException e) {
            throw new SystemException(e);
        }

        return result;

    }

    /**
     * @param id
     * @param xmlData
     * @return
     * @throws ItemNotFoundException
     * @throws ComponentNotFoundException
     * @throws FileNotFoundException
     * @throws XmlSchemaValidationException
     * @throws LockingException
     * @throws ReadonlyElementViolationException
     * @throws ReadonlyAttributeViolationException
     * @throws MissingAttributeValueException
     * @throws AlreadyPublishedException
     * @throws UserNotFoundException
     * @throws InvalidStatusException
     * @throws SystemException
     * @throws MissingMethodParameterException
     * @throws OptimisticLockingException
     * @throws MissingContentException
     * @throws MissingElementValueException
     * @throws InvalidContentException
     * @throws XmlCorruptedException
     * @throws ReadonlyVersionException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#updateComponents(java.lang.String,java.lang.String)
     */
    public String updateComponents(final String id, final String xmlData)
        throws ItemNotFoundException, ComponentNotFoundException,
        FileNotFoundException, XmlSchemaValidationException, LockingException,
        ReadonlyElementViolationException, ReadonlyAttributeViolationException,
        MissingAttributeValueException, AlreadyPublishedException,
        UserNotFoundException, InvalidStatusException, SystemException,
        MissingMethodParameterException, OptimisticLockingException,
        MissingContentException, MissingElementValueException,
        InvalidContentException, XmlCorruptedException,
        ReadonlyVersionException {

        setItem(id);
        checkLatestVersion();
        checkLocked();
        checkReleased();
        checkWithdrawn("No update allowed.");

        // TODO check if this is realy needed. the intension may be to set the
        // components map.

        renderComponents(false);

        String startTimestamp = getItem().getLastFedoraModificationDate();

        String updatedXmlData = null;

        StaxParser sp = new StaxParser();
        OptimisticLockingHandler olh =
            new OptimisticLockingHandler(getItem().getId(),
                Constants.ITEM_OBJECT_TYPE,
                getItem().getLastModificationDate(), sp);
        sp.addHandler(olh);

        ComponentMdRecordsUpdateHandler cmuh =
            new ComponentMdRecordsUpdateHandler(
                "/components/component/md-records", sp);
        sp.addHandler(cmuh);
        // extract datastreams from xmlData
        HashMap<String, String> extractPathes = new HashMap<String, String>();
        extractPathes.put("/components/component", "objid");
        extractPathes.put("/components/component/properties", null);
        extractPathes.put("/components/component/licenses", null);
        extractPathes.put("/components/component/content", null);
        extractPathes.put("/components/component/md-records/md-record", "name");

        MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
        sp.addHandler(me);

        try {
            sp.parse(xmlData);
        }
        catch (XMLStreamException e) {
            // the only exception thrown from MultipleExtractor
            throw new XmlParserSystemException(e);
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }

        HashMap<String, Object> streams = me.getOutputStreams();
        setComponents(streams, cmuh.getMetadataAttributes(), cmuh
            .getNamespacesMap());

        // return the new item
        try {
            updatedXmlData = retrieveComponents(id);
        }
        catch (AuthorizationException e) {
            // can not occur
        }
        String endTimestamp = getItem().getLastFedoraModificationDate();
        if (!startTimestamp.equals(endTimestamp)) {
            makeVersion("Item.updateComponents");
            getItem().persist();
            fireItemModified(getItem().getId());
        }

        return updatedXmlData;
    }

    /**
     * @param id
     * @return
     * @throws ItemNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#retrieveResources(java.lang.String)
     */
    public String retrieveResources(final String id)
        throws ItemNotFoundException, MissingMethodParameterException,
        SystemException {

        setItem(id);
        return renderResources();
    }

    /**
     * @param id
     * @param resourceName
     * @return
     * @throws SystemException
     * @throws ItemNotFoundException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#retrieveResources(java.lang.String)
     */
    public EscidocBinaryContent retrieveResource(
        final String id, final String resourceName,
        final Map<String, String[]> parameters) throws SystemException,
        ItemNotFoundException, OperationNotFoundException {

        EscidocBinaryContent content = new EscidocBinaryContent();
        content.setMimeType("text/xml");

        if (resourceName.equals("version-history")) {
            try {
                content.setContent(new ByteArrayInputStream(
                    retrieveVersionHistory(id).getBytes(
                        XmlUtility.CHARACTER_ENCODING)));
                return content;
            }
            catch (UnsupportedEncodingException e) {
                throw new WebserverSystemException(e);
            }
        }
        else if (resourceName.equals("relations")) {
            try {
                content.setContent(new ByteArrayInputStream(
                    retrieveContentRelations(id).getBytes(
                        XmlUtility.CHARACTER_ENCODING)));
                return content;
            }
            catch (UnsupportedEncodingException e) {
                throw new WebserverSystemException(e);
            }
        }

        setItem(id);
        String contentModelId =
            getItem().getProperty(
                PropertyMapKeys.LATEST_VERSION_CONTENT_MODEL_ID);
        byte[] bytes;
        try {
            bytes =
                FedoraUtility.getInstance().getDissemination(id,
                    contentModelId, resourceName);
        }
        catch (FedoraSystemException e) {
            throw new OperationNotFoundException(e);
        }
        content.setContent(new ByteArrayInputStream(bytes));

        return content;
    }

    /**
     * @param id
     * @return
     * @throws ItemNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#retrieveRelations(java.lang.String)
     */
    public String retrieveRelations(final String id)
        throws ItemNotFoundException, MissingMethodParameterException,
        SystemException {
        setItem(id);
        return renderRelations();
    }

    /**
     * @param id
     * @param componentId
     * @return
     * @throws ItemNotFoundException
     * @throws ComponentNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#retrieveComponent(java.lang.String,java.lang.String)
     */
    public String retrieveComponent(final String id, final String componentId)
        throws ItemNotFoundException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException,
        AuthorizationException {

        setItem(id);
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ":" + originVersionId;
        }
        loadOrigin("You have no access rights on the item " + originId
            + " , which is reffered by a surrogate item " + id
            + ". Therefore you cannot access any subressources of this item.");

        String component = renderComponent(componentId, true);

        return component;
    }

    /**
     * 
     * @param id
     * @param componentId
     * @return
     * @throws ItemNotFoundException
     * @throws ComponentNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface
     *      #retrieveComponentMdRecords(java.lang.String,java.lang.String)
     */
    public String retrieveComponentMdRecords(
        final String id, final String componentId)
        throws ItemNotFoundException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException,
        AuthorizationException {

        setItem(id);
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ":" + originVersionId;
        }
        loadOrigin("You have no access rights on the item " + originId
            + " , which is reffered by a surrogate item " + id
            + ". Therefore you cannot access any subressources of this item.");

        return renderComponentMdRecords(componentId, true);
    }

    /**
     * 
     * @param id
     * @param componentId
     * @param mdRecordId
     * @return
     * @throws ItemNotFoundException
     * @throws ComponentNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#retrieveComponentMdRecord(java.lang.String,java.lang.String,java.lang.String)
     */
    public String retrieveComponentMdRecord(
        final String id, final String componentId, final String mdRecordId)
        throws ItemNotFoundException, ComponentNotFoundException,
        MdRecordNotFoundException, MissingMethodParameterException,
        SystemException, AuthorizationException {

        setItem(id);
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ":" + originVersionId;
        }
        loadOrigin("You have no access rights on the item " + originId
            + " , which is reffered by a surrogate item " + id
            + ". Therefore you cannot access any subressources of this item.");

        return renderComponentMdRecord(componentId, mdRecordId, true);
    }

    /**
     * @param itemId
     * @param componentId
     * @return
     * @throws LockingException
     * @throws AlreadyPublishedException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws InvalidStatusException
     * @throws ItemNotFoundException
     * @throws ResourceNotFoundException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#deleteComponent(java.lang.String,java.lang.String)
     */
    public void deleteComponent(final String itemId, final String componentId)
        throws LockingException, MissingMethodParameterException,
        SystemException, InvalidStatusException, ComponentNotFoundException,
        ItemNotFoundException {

        setItem(itemId);

        // TODO ugly stuff, removing a Component of the item beside the Item!
        // this is easy to move into the Item class
        // removeComponent(componentId);
        getItem().deleteComponent(componentId);

        makeVersion("Item.deleteComponent");
        getItem().persist();

        fireItemModified(getItem().getId());
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     *            The id of the Item
     * @param xmlData
     *            The XML representation of the component.
     * @return
     * @throws ItemNotFoundException
     * @throws MissingContentException
     * @throws ReadonlyViolationException
     * @throws LockingException
     * @throws MissingElementValueException
     * @throws XmlCorruptedException
     * @throws InvalidStatusException
     * @throws MissingMethodParameterException
     * @throws FileNotFoundException
     * @throws InvalidContentException
     * @throws ReadonlyAttributeViolationException
     * @throws SystemException
     * @throws XmlSchemaValidationException
     * @throws OptimisticLockingException
     * @throws MissingAttributeValueException
     *             cf. Interface
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#createComponent(java.lang.String,
     *      java.lang.String)
     */
    public String createComponent(final String id, final String xmlData)
        throws ItemNotFoundException, MissingContentException,
        ReadonlyViolationException, LockingException,
        MissingElementValueException, XmlCorruptedException,
        InvalidStatusException, MissingMethodParameterException,
        FileNotFoundException, InvalidContentException, SystemException,
        XmlSchemaValidationException, OptimisticLockingException,
        MissingAttributeValueException, ComponentNotFoundException {

        setItem(id);
        StaxParser sp = new StaxParser();
        sp
            .addHandler(new OptimisticLockingHandler(getItem().getId(),
                Constants.ITEM_OBJECT_TYPE,
                getItem().getLastModificationDate(), sp));

        try {
            sp.parse(xmlData);
        }
        catch (OptimisticLockingException e) {
            throw e;
        }
        catch (MissingAttributeValueException e) {
            throw e;
        }
        catch (WebserverSystemException e) {
            throw e;
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException("", e);
        }
        sp.clearHandlerChain();
        String addedComponent = addComponent(xmlData);
        makeVersion("Component added.");
        getItem().persist();

        fireItemModified(getItem().getId());

        return addedComponent;
    }

    /**
     * @param id
     * @param componentId
     * @param xmlData
     * @return
     * @throws ItemNotFoundException
     * @throws ComponentNotFoundException
     * @throws LockingException
     * @throws XmlSchemaValidationException
     * @throws FileNotFoundException
     * @throws ReadonlyElementViolationException
     * @throws MissingAttributeValueException
     * @throws InvalidStatusException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws XmlCorruptedException
     * @throws ReadonlyAttributeViolationException
     * @throws MissingContentException
     * @throws InvalidContentException
     * @throws ReadonlyVersionException
     * @see de.escidoc.core.om.service.interfaces.ItemHandlerInterface#updateComponent(java.lang.String,java.lang.String,
     *      java.lang.String)
     */
    public String updateComponent(
        final String id, final String componentId, final String xmlData)
        throws ItemNotFoundException, ComponentNotFoundException,
        LockingException, XmlSchemaValidationException, FileNotFoundException,
        ReadonlyElementViolationException, MissingAttributeValueException,
        InvalidStatusException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, XmlCorruptedException,
        ReadonlyAttributeViolationException, MissingContentException,
        InvalidContentException, ReadonlyVersionException,
        AuthorizationException {

        setItem(id);
        if (!getItem().getComponentIds().contains(componentId)) {
            try {
                getItem().addComponent(
                    new Component(componentId, getItem().getId(), null));
            }
            catch (ResourceNotFoundException e) {
                throw new ComponentNotFoundException(e);
            }
        }
        String startTimestamp = getItem().getLastFedoraModificationDate();

        checkLatestVersion();
        checkLocked();
        checkReleased();
        checkWithdrawn("No update allowed.");

        HashMap<String, String> extractPathes = new HashMap<String, String>();
        extractPathes.put("/component/properties", null);
        extractPathes.put("/component/md-records/md-record", "name");
        extractPathes.put("/component/content", null);

        StaxParser sp = new StaxParser();
        OptimisticLockingHandler olh =
            new OptimisticLockingHandler(getItem().getId(),
                Constants.ITEM_OBJECT_TYPE,
                getItem().getLastModificationDate(), sp);
        sp.addHandler(olh);
        ComponentMdRecordsUpdateHandler cmuh =
            new ComponentMdRecordsUpdateHandler("/component/md-records", sp);
        sp.addHandler(cmuh);
        MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
        sp.addHandler(me);

        String updatedXmlData = null;

        try {
            sp.parse(xmlData);
        }
        catch (XMLStreamException e) {
            // the only exception MultipleExtractor throws
            throw new XmlParserSystemException(e);
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }

        HashMap<String, Object> compsMap =
            (HashMap<String, Object>) me.getOutputStreams().get("components");
        Map compMap = (HashMap) compsMap.get(componentId);

        setComponent(getItem().getComponent(componentId), compMap, cmuh
            .getMetadataAttributes().get(componentId), cmuh
            .getNamespacesMap().get(componentId));

        updatedXmlData = retrieveComponent(id, componentId);

        String endTimestamp = getItem().getLastFedoraModificationDate();
        if (!startTimestamp.equals(endTimestamp)) {
            makeVersion("Item.updateComponent");
            getItem().persist();

            fireItemModified(getItem().getId());
        }

        return updatedXmlData;
    }

    /**
     * @param id
     * @param param
     * @return
     * @throws ItemNotFoundException
     * @throws LockingException
     * @throws InvalidStatusException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws ReadonlyViolationException
     * @throws ReadonlyVersionException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#release(java.lang.String,java.lang.String)
     */
    public String release(final String id, final String param)
        throws ItemNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException,
        ReadonlyVersionException, InvalidXmlException,
        ComponentNotFoundException {

        setItem(id);
        TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);

        checkWithdrawn("No modification allowed.");
        checkLocked();
        checkLatestVersion();
        checkReleased();
        checkPid();

        if (getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(),
            "Item " + getItem().getId())) {

            // check version status
            String curStatus =
                getItem().getProperty(
                    PropertyMapKeys.LATEST_VERSION_VERSION_STATUS);
            if (!Constants.STATUS_SUBMITTED.equals(curStatus)) {
                throw new InvalidStatusException("The object is not in state '"
                    + Constants.STATUS_SUBMITTED + "' and can not be "
                    + Constants.STATUS_RELEASED + ".");
            }

            // set status "released"
            // only renew the timestamp and set status with version entry
            makeVersion(taskParameter.getComment(), Constants.STATUS_RELEASED);
            getItem().setLatestReleasePid();
            getItem().persist();

            // notify indexer
            // getUtility().notifyIndexerAddPublication(getItem().getHref());
            fireItemModified(getItem().getId());
            // find surrogate items which reference this item by a floating
            // reference, recache them and if necessary reindex them.
            List<String> surrogateItemIds =
                TripleStoreUtility.getInstance().getSurrogates(
                    getItem().getId());
            List<String> referencedSurrogateItemIds = new Vector<String>();
            Iterator<String> it = surrogateItemIds.iterator();
            while (it.hasNext()) {
                String surrogateId = it.next();
                String versionId =
                    TripleStoreUtility.getInstance().getRelation(surrogateId,
                        TripleStoreUtility.PROP_ORIGIN_VERSION);
                if (versionId == null) {
                    setOriginId(getItem().getId());
                    setOriginItem(getItem());
                    referencedSurrogateItemIds.add(surrogateId);
                }
            }
            // run item recaching/reindexing asynchronously
            queueItemsModified(referencedSurrogateItemIds);
        }

        return getUtility().prepareReturnXml(
            getItem().getLastModificationDate());
    }

    /**
     * @param id
     * @param param
     * @return
     * @throws ItemNotFoundException
     * @throws LockingException
     * @throws InvalidStatusException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws ReadonlyViolationException
     * @throws ReadonlyVersionException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#submit(java.lang.String,java.lang.String)
     */
    public String submit(final String id, final String param)
        throws ItemNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException,
        ReadonlyVersionException, InvalidXmlException,
        ComponentNotFoundException {

        setItem(id);
        TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);

        checkWithdrawn("No modification allowed.");
        checkLatestVersion();
        checkLocked();
        checkReleased();

        if (getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(),
            "Item " + getItem().getId())) {

            // check version status
            String curStatus = getItem().getVersionStatus();
            if (!(Constants.STATUS_PENDING.equals(curStatus) || Constants.STATUS_IN_REVISION
                .equals(curStatus))) {
                throw new InvalidStatusException("The object is not in state '"
                    + Constants.STATUS_PENDING + "' or '"
                    + Constants.STATUS_IN_REVISION + "' and can not be"
                    + " submitted.");
            }

            // set status "submited"
            // only renew the timestamp and set status with version entry
            makeVersion(taskParameter.getComment(), Constants.STATUS_SUBMITTED);
            getItem().persist();

            fireItemModified(getItem().getId());
        }

        return getUtility().prepareReturnXml(
            getItem().getLastModificationDate());
    }

    /**
     * @param id
     * @param param
     * @return
     * @throws ItemNotFoundException
     * @throws LockingException
     * @throws InvalidStatusException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws ReadonlyViolationException
     * @throws ReadonlyVersionException
     * @throws ComponentNotFoundException
     * @throws XmlCorruptedException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#revise(java.lang.String,java.lang.String)
     */
    public String revise(final String id, final String param)
        throws ItemNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException,
        ReadonlyVersionException, XmlCorruptedException,
        ComponentNotFoundException {

        setItem(id);
        TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);

        checkWithdrawn("No modification allowed.");
        checkLatestVersion();
        checkLocked();
        checkVersionStatus(Constants.STATUS_SUBMITTED);

        if (getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(),
            "Item " + getItem().getId())) {

            // set status "in-revision"
            // only renew the timestamp and set status with version entry
            makeVersion(taskParameter.getComment(),
                Constants.STATUS_IN_REVISION);
            getItem().persist();

            fireItemModified(getItem().getId());
        }

        return getUtility().prepareReturnXml(
            getItem().getLastModificationDate());
    }

    /**
     * @param id
     * @param param
     * @return
     * @throws ItemNotFoundException
     * @throws NotPublishedException
     * @throws LockingException
     * @throws AlreadyWithdrawnException
     * @throws InvalidStatusException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws ReadonlyViolationException
     * @throws ReadonlyVersionException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#withdraw(java.lang.String,java.lang.String)
     */
    public String withdraw(final String id, final String param)
        throws ItemNotFoundException, NotPublishedException, LockingException,
        AlreadyWithdrawnException, InvalidStatusException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException,
        ReadonlyVersionException, InvalidXmlException,
        ComponentNotFoundException {

        // we want special exceptions if already withdrawn, so check something
        // before setItem()
        Utility.getInstance().checkIsItem(id);

        final String curStatus =
            TripleStoreUtility.getInstance().getPropertiesElements(id,
                TripleStoreUtility.PROP_PUBLIC_STATUS);
        if (curStatus.equals(Constants.STATUS_WITHDRAWN)) {
            throw new AlreadyWithdrawnException(
                "The object is already withdrawn");
        }
        if (!curStatus.equals(Constants.STATUS_RELEASED)) {
            throw new NotPublishedException(
                "The object is not in state 'released' and can not be "
                    + "withdrawn.");
        }

        setItem(id);

        TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);

        String withdrawComment = taskParameter.getWithdrawComment();
        if (withdrawComment == null) {
            withdrawComment = taskParameter.getComment();
            if (withdrawComment == null) {
                throw new MissingMethodParameterException(
                    "No withdraw comment found.");
            }
        }

        checkLatestVersion();
        checkLocked();

        if (getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(),
            "Item " + getItem().getId())) {

            makeVersion(withdrawComment, Constants.STATUS_WITHDRAWN);
            getItem().persist();

            // getUtility().notifyIndexerDeletePublication(getItem().getHref());

            fireItemModified(getItem().getId());
        }

        return getUtility().prepareReturnXml(
            getItem().getLastModificationDate());
    }

    /**
     * @param id
     * @param param
     * @return
     * @throws SystemException
     * @throws ItemNotFoundException
     * @throws OptimisticLockingException
     * @throws ReferencedResourceNotFoundException
     * @throws RelationPredicateNotFoundException
     * @throws AlreadyExistsException
     * @throws InvalidStatusException
     * @throws XmlCorruptedException
     * @throws InvalidContentException
     * @throws ReadonlyAttributeViolationException
     * @throws MissingElementValueException
     * @throws LockingException
     * @throws ReadonlyElementViolationException
     * @throws XmlSchemaValidationException
     * @throws ReadonlyVersionException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#addContentRelations(java.lang.String,java.lang.String)
     */
    public String addContentRelations(final String id, final String param)
        throws SystemException, ItemNotFoundException,
        OptimisticLockingException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AlreadyExistsException,
        InvalidStatusException, InvalidContentException, InvalidXmlException,
        ReadonlyAttributeViolationException, MissingElementValueException,
        LockingException, ReadonlyElementViolationException,
        ReadonlyVersionException, ComponentNotFoundException {

        setItem(id);
        checkLatestVersion();
        checkLocked();
        checkWithdrawn("Adding of content relations is not allowed.");
        TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);
        getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(), "Item " + id);

        StaxParser sp = new StaxParser();

        ContentRelationsAddHandler2Edition addHandler =
            new ContentRelationsAddHandler2Edition(sp, getItem().getId());
        sp.addHandler(addHandler);
        try {
            sp.parse(param);
            sp.clearHandlerChain();
        }
        catch (MissingElementValueException e) {
            throw e;
        }
        catch (ReferencedResourceNotFoundException e) {
            throw e;
        }
        catch (RelationPredicateNotFoundException e) {
            throw e;
        }
        catch (InvalidContentException e) {
            throw e;
        }
        catch (InvalidXmlException e) {
            throw e;
        }
        catch (SystemException e) {
            throw e;
        }
        catch (AlreadyExistsException e) {
            throw e;
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        Vector<HashMap<String, String>> relationsData =
            addHandler.getRelations();

        if ((relationsData != null) && (relationsData.size() > 0)) {
            Vector<StartElementWithChildElements> elements =
                new Vector<StartElementWithChildElements>();
            Iterator<HashMap<String, String>> iterator =
                relationsData.iterator();
            while (iterator.hasNext()) {
                HashMap<String, String> relation = iterator.next();
                String predicateValue = relation.get("predicateValue");
                String predicateNs = relation.get("predicateNs");
                String target = relation.get("target");

                StartElementWithChildElements newContentRelationElement =
                    new StartElementWithChildElements();
                newContentRelationElement.setLocalName(predicateValue);
                newContentRelationElement
                    .setPrefix(Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT);
                newContentRelationElement.setNamespace(predicateNs);
                Attribute resource =
                    new Attribute("resource", Constants.RDF_NAMESPACE_URI,
                        Constants.RDF_NAMESPACE_PREFIX, "info:fedora/" + target);
                newContentRelationElement.addAttribute(resource);
                // newComponentIdElement.setElementText(componentId);
                newContentRelationElement.setChildrenElements(null);

                elements.add(newContentRelationElement);
            }
            byte[] relsExtNewBytes =
                Utility.updateRelsExt(elements, null, null, getItem(), null);
            getItem().setRelsExt(relsExtNewBytes);

            makeVersion("Item.addContentRelations");
            getItem().persist();

            fireItemModified(getItem().getId());
        }

        return getUtility().prepareReturnXml(
            getItem().getLastModificationDate());
    }

    /**
     * @param id
     * @param param
     * @return
     * @throws SystemException
     * @throws ItemNotFoundException
     * @throws OptimisticLockingException
     * @throws InvalidStatusException
     * @throws MissingElementValueException
     * @throws InvalidXmlException
     * @throws ContentRelationNotFoundException
     * @throws LockingException
     * @throws ReadonlyViolationException
     * @throws ReadonlyVersionException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#removeContentRelations(java.lang.String,java.lang.String)
     */
    public String removeContentRelations(final String id, final String param)
        throws SystemException, ItemNotFoundException,
        OptimisticLockingException, InvalidStatusException,
        MissingElementValueException, InvalidXmlException,
        ContentRelationNotFoundException, LockingException,
        ReadonlyViolationException, ReadonlyVersionException,
        ComponentNotFoundException {

        setItem(id);
        checkLatestVersion();
        String startTimestamp = getItem().getLastFedoraModificationDate();
        checkLocked();
        checkWithdrawn("Removing of content relations is not allowed.");
        TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);
        getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(), "Item " + id);

        boolean resourceUpdated = false;

        StaxParser sp = new StaxParser();

        ContentRelationsRemoveHandler2Edition removeHandler =
            new ContentRelationsRemoveHandler2Edition(sp, getItem().getId());
        sp.addHandler(removeHandler);
        try {
            sp.parse(param);
            sp.clearHandlerChain();
        }
        catch (MissingElementValueException e) {
            throw new MissingElementValueException(e);
        }
        catch (ContentRelationNotFoundException e) {
            throw new ContentRelationNotFoundException(e);
        }
        catch (TripleStoreSystemException e) {
            throw new TripleStoreSystemException(e);
        }
        catch (WebserverSystemException e) {
            throw new TripleStoreSystemException(e);
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }

        Vector<HashMap<String, String>> relationsData =
            removeHandler.getRelations();
        if ((relationsData != null) && (relationsData.size() > 0)) {
            final TreeMap<String, Vector<StartElementWithChildElements>> toRemove =
                new TreeMap<String, Vector<StartElementWithChildElements>>();
            final Iterator<HashMap<String, String>> iterator =
                relationsData.iterator();
            HashMap<String, Vector<StartElementWithChildElements>> predicateValuesVectorAssignment =
                new HashMap<String, Vector<StartElementWithChildElements>>();
            while (iterator.hasNext()) {
                resourceUpdated = true;
                final HashMap<String, String> relation = iterator.next();

                final String predicateValue = relation.get("predicateValue");
                final String predicateNs = relation.get("predicateNs");
                final String target = relation.get("target");

                final StartElementWithChildElements newContentRelationElement =
                    new StartElementWithChildElements();
                newContentRelationElement.setLocalName(predicateValue);
                newContentRelationElement
                    .setPrefix(Constants.CONTENT_RELATIONS_NS_PREFIX_IN_RELSEXT);
                newContentRelationElement.setNamespace(predicateNs);
                final Attribute resource =
                    new Attribute("resource", Constants.RDF_NAMESPACE_URI,
                        Constants.RDF_NAMESPACE_PREFIX, "info:fedora/" + target);
                newContentRelationElement.addAttribute(resource);
                newContentRelationElement.setChildrenElements(null);
                if (predicateValuesVectorAssignment.containsKey(predicateValue)) {
                    Vector<StartElementWithChildElements> vector =
                        predicateValuesVectorAssignment.get(predicateValue);
                    vector.add(newContentRelationElement);
                }
                else {
                    Vector<StartElementWithChildElements> vector =
                        new Vector<StartElementWithChildElements>();
                    vector.add(newContentRelationElement);
                    predicateValuesVectorAssignment.put(predicateValue, vector);
                }

            }
            Set<String> keySet = predicateValuesVectorAssignment.keySet();
            Iterator<String> iteratorKeys = keySet.iterator();
            while (iteratorKeys.hasNext()) {
                String predicateValue = iteratorKeys.next();
                Vector<StartElementWithChildElements> elements =
                    predicateValuesVectorAssignment.get(predicateValue);
                toRemove.put("/RDF/Description/" + predicateValue, elements);
            }
            final byte[] relsExtNewBytes =
                Utility.updateRelsExt(null, toRemove, null, getItem(), null);
            getItem().setRelsExt(relsExtNewBytes);
            // getItem().persist();

            String endTimestamp = getItem().getLastFedoraModificationDate();
            if (resourceUpdated || !startTimestamp.equals(endTimestamp)) {
                makeVersion("Item.removeContentRelations");
                getItem().persist();
            }

            fireItemModified(getItem().getId());
        }

        return getUtility().prepareReturnXml(
            getItem().getLastModificationDate());
    }

    /**
     * Lock an Item for other user access.
     * 
     * @param id
     * @param param
     * @return
     * @throws ItemNotFoundException
     * @throws LockingException
     * @throws InvalidContentException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws ComponentNotFoundException
     * @throws InvalidStatusException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#lock(java.lang.String,java.lang.String)
     */
    public String lock(final String id, final String param)
        throws ItemNotFoundException, LockingException,
        InvalidContentException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidXmlException,
        ComponentNotFoundException, InvalidStatusException {

        setItem(id);
        checkWithdrawn("No modification allowed.");

        TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);
        checkLocked();

        if (getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(),
            "Item " + getItem().getId())) {
            getItem().setLocked(true, getUtility().getCurrentUser());
            // to lock/unlock is no modification of the object, don't update
            // timestamp

            fireItemModified(getItem().getId());
        }

        return getUtility().prepareReturnXml(
            getItem().getLastModificationDate());
    }

    /**
     * @param id
     * @param param
     * @throws ItemNotFoundException
     * @throws LockingException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @throws OptimisticLockingException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#unlock(java.lang.String,java.lang.String)
     */
    public String unlock(final String id, final String param)
        throws ItemNotFoundException, LockingException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidXmlException,
        ComponentNotFoundException {

        setItem(id);
        TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);
        // checked by AA
        // checkLocked();

        if (getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(),
            "Item " + getItem().getId())) {

            getItem().setLocked(false, null);
            // to lock/unlock is no modification of the object, don't update
            // timestamp

            fireItemModified(getItem().getId());
        }

        return getUtility().prepareReturnXml(
            getItem().getLastModificationDate());
    }

    /**
     * @param id
     * @return
     * @throws ItemNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#retrieveVersionHistory(java.lang.String)
     */
    public String retrieveVersionHistory(final String id)
        throws ItemNotFoundException, SystemException {

        setItem(id);
        String versionsXml = null;

        try {
            // versionsXml =
            // getVersions().replaceFirst(
            // "xlink:type=\"simple\"",
            // "xml:base=\"" + XmlUtility.getEscidocBaseUrl()
            // + "\" xlink:type=\"simple\" ");
            versionsXml =
                getVersions().replaceFirst(
                    "<" + Constants.WOV_NAMESPACE_PREFIX + ":"
                        + Elements.ELEMENT_WOV_VERSION_HISTORY,
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><"
                        + Constants.WOV_NAMESPACE_PREFIX + ":"
                        + Elements.ELEMENT_WOV_VERSION_HISTORY + " xml:base=\""
                        + XmlUtility.getEscidocBaseUrl() + "\" "
                        + Elements.ATTRIBUTE_LAST_MODIFICATION_DATE + "=\""
                        + getItem().getLastModificationDate() + "\" ");
        }
        catch (StreamNotFoundException e) {
            throw new IntegritySystemException("Version history not found.", e);
        }

        return versionsXml;
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     *            id
     * @return
     * @throws ItemNotFoundException
     *             e
     * @throws SystemException
     *             cf. Interface
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#retrieveParents(java.lang.String)
     */
    public String retrieveParents(final String id)
        throws ItemNotFoundException, SystemException {
        Utility.getInstance().checkIsItem(id);
        return renderParents(id);
    }

    /**
     * @param filter
     * @return
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws InvalidXmlException
     * @throws SystemException
     * @throws MissingMethodParameterException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#retrieveItems(java.lang.String)
     */
    public String retrieveItems(final String filter)
        throws InvalidSearchQueryException, InvalidXmlException,
        MissingMethodParameterException, SystemException {
        return retrieveItems((Object) filter);
    }

    /**
     * @param filter
     * @return
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a CQL query
     * @throws SystemException
     * @throws MissingMethodParameterException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#retrieveItems(java.util.Map)
     */
    public String retrieveItems(final Map<String, String[]> filter)
        throws InvalidSearchQueryException, MissingMethodParameterException,
        SystemException {
        String result = null;

        try {
            result = retrieveItems((Object) filter);
        }
        catch (InvalidXmlException e) {
            // cannot happen here
        }
        return result;
    }

    /**
     * @param filterObject
     * @return
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws InvalidXmlException
     * @throws SystemException
     * @throws MissingMethodParameterException
     * @throws ComponentNotFoundException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#retrieveItems(java.lang.String)
     */
    private String retrieveItems(final Object filterObject)
        throws InvalidSearchQueryException, InvalidXmlException,
        MissingMethodParameterException, SystemException {

        String result = null;
        FilterInterface filter = null;
        String format = null;
        boolean explain = false;

        if (filterObject instanceof String) {
            TaskParamHandler taskParameter =
                XmlUtility.parseTaskParam((String) filterObject, false);

            filter = new XmlFilter((String) filterObject);
            format = taskParameter.getFormat();
        }
        else if (filterObject instanceof Map<?, ?>) {
            SRURequest parameters =
                new SRURequest((Map<String, String[]>) filterObject);

            if (parameters.query != null) {
                filter = new CqlFilter(parameters.query);
            }
            else {
                filter = new CqlFilter();
            }
            filter.setLimit(parameters.limit);
            filter.setOffset(parameters.offset);
            format = "srw";
            explain = parameters.explain;
        }
        filter.setObjectType(ResourceType.ITEM);

        if ((format == null) || (format.length() == 0)
            || (format.equalsIgnoreCase("full"))) {
            StringWriter output = new StringWriter();
            String restRootAttributes = "";

            if (UserContext.isRestAccess()) {
                restRootAttributes =
                    "xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
                        + "xlink:type=\"simple\" "
                        + "xlink:title=\"list of items\" xml:base=\""
                        + XmlUtility.getEscidocBaseUrl() + "\" ";
            }
            output.write("<?xml version=\"1.0\" encoding=\""
                + XmlUtility.CHARACTER_ENCODING
                + "\"?>"
                + "<il:item-list xmlns:il=\""
                + Constants.ITEM_LIST_NAMESPACE_URI
                + "\" "
                + restRootAttributes
                + "limit=\""
                + filter.getLimit()
                + "\" offset=\""
                + filter.getOffset()
                + "\" number-of-records=\""
                + getDbResourceCache().getNumberOfRecords(
                    getUtility().getCurrentUserId(), filter) + "\">");
            getDbResourceCache().getResourceList(output,
                getUtility().getCurrentUserId(), filter, null);
            output.write("</il:item-list>");
            result = output.toString();
        }
        else if ((format != null) && (format.equalsIgnoreCase("deleteParam"))) {
            BufferedReader reader = null;

            try {
                StringBuffer idList = new StringBuffer();
                StringWriter output = new StringWriter();

                getDbResourceCache().getResourceIds(output,
                    getUtility().getCurrentUserId(), filter);
                reader =
                    new BufferedReader(new StringReader(output.toString()));

                String id;

                while ((id = reader.readLine()) != null) {
                    idList.append("<id>");
                    idList.append(id);
                    idList.append("</id>\n");
                }
                result = "<param>" + idList.toString() + "</param>";
            }
            catch (IOException e) {
                throw new SystemException(e);
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException e) {
                    }
                }
            }
        }
        else if ((format != null) && (format.equalsIgnoreCase("rdf"))) {
            try {

                result =
                    getObjectList(Constants.ITEM_OBJECT_TYPE,
                        (String) filterObject);
            }
            catch (InvalidContentException e) {
                throw new XmlCorruptedException(e);
            }
        }
        else if ((format != null) && (format.equalsIgnoreCase("srw"))) {
            if (explain) {
                Map<String, Object> values = new HashMap<String, Object>();

                values.put("PROPERTY_NAMES", getDbResourceCache()
                    .getPropertyNames());
                result =
                    ExplainXmlProvider.getInstance().getExplainItemXml(values);
            }
            else {
                StringWriter output = new StringWriter();
                long numberOfRecords =
                    getDbResourceCache().getNumberOfRecords(
                        getUtility().getCurrentUserId(), filter);

                output.write("<?xml version=\"1.0\" encoding=\""
                    + XmlUtility.CHARACTER_ENCODING + "\"?>"
                    + "<zs:searchRetrieveResponse "
                    + "xmlns:zs=\"http://www.loc.gov/zing/srw/\">"
                    + "<zs:version>1.1</zs:version>" + "<zs:numberOfRecords>"
                    + numberOfRecords + "</zs:numberOfRecords>");
                if (numberOfRecords > 0) {
                    output.write("<zs:records>");
                }
                getDbResourceCache().getResourceList(output,
                    getUtility().getCurrentUserId(), filter, "srw");
                if (numberOfRecords > 0) {
                    output.write("</zs:records>");
                }
                output.write("</zs:searchRetrieveResponse>");
                result = output.toString();
            }
        }
        else if (filterObject instanceof String) {

            Map<String, Object> filterMap = XmlUtility.getFilterMap((String) filterObject);

            String userCriteria = null;
            String roleCriteria = null;
            String whereClause = null;
            if (filterMap != null) {
                // filter out user permissions
                userCriteria = (String) filterMap.get("user");
                roleCriteria = (String) filterMap.get("role");

                try {
                    whereClause =
                        getPdp().getRoleUserWhereClause("container",
                            userCriteria, roleCriteria).toString();
                }
                catch (final SystemException e) {
                    // FIXME: throw SystemException?
                    throw new SystemException(
                        "Failed to retrieve clause for user and role criteria",
                        e);
                }
                catch (MissingMethodParameterException e) {
                    throw new SystemException(
                        "Failed to retrieve clause for user and role criteria",
                        e);
                }
            }

            List<String> list =
                TripleStoreUtility.getInstance().evaluate(
                    Constants.ITEM_OBJECT_TYPE, filterMap, null, whereClause);

            List<String> itemIds;
            try {
                itemIds =
                    getPdp().evaluateRetrieve(Constants.ITEM_OBJECT_TYPE, list);
            }
            catch (final Exception e) {
                throw new WebserverSystemException(e);
            }

            // prototyping new item list
            String rdfItemList = "";
            Iterator<String> it = itemIds.iterator();
            TripleStoreConnector.init();
            while (it.hasNext()) {
                String id = it.next();
                try {
                    String triples =
                        TripleStoreConnector.requestMPT("<info:fedora/" + id
                            + "> * *", "RDF/XML");

                    XMLInputFactory inf = XMLInputFactory.newInstance();
                    RDFRegisteredOntologyFilter rdfFilter =
                        new RDFRegisteredOntologyFilter();
                    rdfFilter.setWorkaroundForItemList(true);
                    XMLEventReader reader =
                        inf.createFilteredReader(inf
                            .createXMLEventReader(new StringReader(triples)),
                            rdfFilter);

                    StringWriter sw = new StringWriter();
                    XMLEventWriter writer = XmlUtility.createXmlEventWriter(sw);

                    // writer.add(reader);
                    while (reader.hasNext()) {
                        XMLEvent event = reader.nextEvent();
                        writer.add(event);
                    }

                    String cur = sw.toString();
                    if (cur.startsWith("<?xml")) {
                        int index = cur.indexOf('>');
                        cur = cur.substring(index + 1);
                    }
                    rdfItemList += cur;

                }
                catch (InvalidTripleStoreOutputFormatException e) {
                    throw new WebserverSystemException(e);
                }
                catch (InvalidTripleStoreQueryException e) {
                    throw new WebserverSystemException(e);
                }
                catch (FactoryConfigurationError e) {
                    throw new WebserverSystemException(e);
                }
                catch (XMLStreamException e) {
                    throw new WebserverSystemException(e);
                }
            }
            rdfItemList =
                "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
                    + rdfItemList + "</rdf:RDF>";

            if (format.equalsIgnoreCase("rdf")
                || format.equalsIgnoreCase("short")) {
                result = rdfItemList;
            }
            else if (format.equalsIgnoreCase("atom")) {
                // transform item list
                try {
                    String xsltUrl =
                        EscidocConfiguration.getInstance().appendToSelfURL(
                            "/xsl/shortList2atom.xsl");
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    TransformerFactory tf = TransformerFactory.newInstance();
                    Transformer t =
                        tf.newTransformer(new StreamSource(new URL(xsltUrl)
                            .openStream()));
                    t.transform(
                        new StreamSource(new StringReader(rdfItemList)),
                        new StreamResult(out));

                    result = out.toString(XmlUtility.CHARACTER_ENCODING);
                }
                catch (Exception e) {
                    throw new WebserverSystemException(
                        "Transforming short item list to atom failed.", e);
                }
            }
            else {
                // FIXME exception type
                throw new WebserverSystemException("Invalid list format.");
            }
        }

        return result;

    }

    private String getObjectList(final String objectType, final String filterXml)
        throws InvalidContentException, TripleStoreSystemException,
        MissingMethodParameterException, InvalidXmlException,
        WebserverSystemException {

        Map<String, Object> filter = null;
        try {
            filter = XmlUtility.getFilterMap(filterXml);
        }
        catch (XmlParserSystemException e) {
            throw new XmlCorruptedException(e);
        }

        final String roleCriteria = (String) filter.get("role");
        final String userCriteria = (String) filter.get("user");
        String whereClause = null;

        if (userCriteria == null) {
            if (roleCriteria != null) {
                throw new MissingMethodParameterException(
                    "If role criteria is used, user id must be specified");
            }
        }
        else {
            try {
                UserFilter ufilter = new UserFilter();
                whereClause =
                    ufilter.getRoleUserWhereClause(objectType, userCriteria,
                        roleCriteria);
                if (whereClause == null) {
                    // FIXME
                    return null;
                }
            }
            catch (final SystemException e) {
                // FIXME: throw SystemException?
                throw new TripleStoreSystemException(
                    "Failed to retrieve clause for user and role criteria", e);
            }
        }

        return TripleStoreUtility.getInstance().getObjectList(
            Constants.ITEM_OBJECT_TYPE, filter, whereClause);

    }

    /**
     * @param id
     * @param componentId
     * @return
     * @throws ItemNotFoundException
     * @throws ComponentNotFoundException
     * @throws MissingMethodParameterException
     * @throws SystemException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#retrieveComponentProperties(java.lang.String,java.lang.String)
     */
    public String retrieveComponentProperties(
        final String id, final String componentId)
        throws ItemNotFoundException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException,
        AuthorizationException {

        setItem(id);
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ":" + originVersionId;
        }
        loadOrigin("You have no access rights on the item " + originId
            + " , which is reffered by a surrogate item " + id
            + ". Therefore you cannot access any subressources of this item.");

        return renderComponentProperties(componentId);
    }

    // CHECKSTYLE:JAVADOC-OFF
    /**
     * @param id
     *            The id of the item.
     * @return TODO
     * @throws ItemNotFoundException
     *             TODO
     */
    public String retrieveRevisions(final String id)
        throws ItemNotFoundException {
        // FIXME
        throw new UnsupportedOperationException();
    }

    /**
     * @param id
     *            The id of the item.
     * @param taskParam
     *            Taskparam XML including the latest-modification-date.
     * @return TODO
     * @throws ItemNotFoundException
     *             TODO
     */
    public String moveToContext(final String id, final String taskParam)
        throws ItemNotFoundException, ContextNotFoundException,
        InvalidContentException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException {
        // FIXME
        throw new UnsupportedOperationException();
    }

    /**
     * @param comment
     *            Optional comment to associate with the created version or
     *            event.
     * @throws SystemException
     *             If an error occures.
     */
    private void makeVersion(final String comment) throws SystemException {
        makeVersion(comment, null);
    }

    /**
     * Parses the item.
     * 
     * @param xml
     *            the String containing the item xml
     * @return ItemCreate
     * @throws WebserverSystemException
     * @throws XmlParserSystemException
     * @throws ReadonlyElementViolationException
     * @throws ReadonlyAttributeViolationException
     * @throws ContentModelNotFoundException
     * @throws ContextNotFoundException
     * @throws MissingContentException
     * @throws MissingAttributeValueException
     * @throws MissingElementValueException
     * @throws XmlCorruptedException
     * @throws InvalidContentException
     * @throws ReferencedResourceNotFoundException
     * @throws InvalidStatusException
     * @throws RelationPredicateNotFoundException
     * @throws MissingMdRecordException
     * @throws EncodingSystemException
     * @throws TripleStoreSystemException
     * @throws IntegritySystemException
     */
    private ItemCreate parseItem(final String xml)
        throws WebserverSystemException, XmlParserSystemException,
        ReadonlyElementViolationException, ReadonlyAttributeViolationException,
        ContentModelNotFoundException, ContextNotFoundException,
        MissingContentException, MissingAttributeValueException,
        MissingElementValueException, XmlCorruptedException,
        InvalidContentException, ReferencedResourceNotFoundException,
        InvalidStatusException, RelationPredicateNotFoundException,
        MissingMdRecordException, EncodingSystemException,
        TripleStoreSystemException, IntegritySystemException {

        StaxParser sp = new StaxParser();

        ItemHandler itemHandler = new ItemHandler(sp);
        sp.addHandler(itemHandler);

        try {
            sp.parse(xml);
        }
        catch (LockingException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (OptimisticLockingException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (AlreadyExistsException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (OrganizationalUnitNotFoundException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (ContentRelationNotFoundException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (PidAlreadyAssignedException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (TmeException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (XMLStreamException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }

        return itemHandler.getItem();
    }

    /**
     * @param comment
     *            Optional comment to associate with the created version or
     *            event.
     * @param newStatus
     *            The status of the new version.
     * @throws SystemException
     *             If an error occures.
     */
    private void makeVersion(final String comment, final String newStatus)
        throws SystemException {
        getUtility().makeVersion(comment, newStatus, getItem(),
            getFedoraUtility());
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * Injects the content relation handler.
     * 
     * @param contentRelationHandler
     *            The {@link FedoraContentRelationHandler}.
     * @spring.property ref="business.FedoraContentRelationHandler"
     * 
     */
    public void setContentRelationHandler(
        final FedoraContentRelationHandler contentRelationHandler) {
        this.contentRelationHandler = contentRelationHandler;
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
     * Injects the triple store utility bean.
     * 
     * @param tsu
     *            The {@link TripleStoreUtility}.
     * @spring.property ref="business.TripleStoreUtility"
     * 
     */
    public void setTripleStoreUtility(final TripleStoreUtility tsu) {
        super.setTripleStoreUtility(tsu);
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

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Sets the metadata datastream with name <code>name</code> for this item.
     * 
     * @param name
     *            The name of the metadata datastream.
     * @param xml
     *            The datastream.
     * @param mdAttributes
     *            A Map containing the metadata datastreams type and schema.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    private void setMetadataRecord(
        final String name, final String xml,
        final Map<String, String> mdAttributes) throws SystemException {

        // this method must be reimplemented to use set-method in item
        throw new SystemException("Not yet implemented.");
        // Datastream oldDs = null;
        // Datastream newDs = null;
        //
        // byte[] xmlBytes = null;
        // try {
        // xmlBytes = xml.getBytes(XmlUtility.CHARACTER_ENCODING);
        // }
        // catch (UnsupportedEncodingException e) {
        // throw new EncodingSystemException(e.getMessage(), e);
        // }
        //
        // newDs = new Datastream(name, getItem().getId(), xmlBytes,
        // "text/xml");
        // newDs.addAlternateId("metadata");
        // newDs.addAlternateId(mdAttributes.get("type"));
        // newDs.addAlternateId(mdAttributes.get("schema"));
        //
        // oldDs = getItem().getMdRecord(name);
        //
        // if (!oldDs.equals(newDs)) { // TODO check if update is allowed
        // getItem().setMdRecord(name, newDs);
        // }
    }

    /**
     * Creates Datastream objects from the ByteArrayOutputStreams in
     * <code>mdMap</code> and calls Item.setMdRecords with a HashMap which
     * contains the metadata datastreams as Datastream objects.
     * 
     * @param mdMap
     *            A HashMap which contains the metadata datastreams as
     *            ByteArrayOutputStream.
     * @param mdAttributesMap
     *            A HashMap which contains the metadata attributes.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    private void setMetadataRecords(
        final HashMap mdMap, final Map mdAttributesMap,
        final String escidocMdRecordnsUri) throws SystemException {
        HashMap<String, Datastream> dsMap = new HashMap<String, Datastream>();
        if (mdMap == null) {
            getItem().setMdRecords(dsMap);
        }
        else {
            Iterator mdIt = mdMap.keySet().iterator();
            while (mdIt.hasNext()) {
                String name = (String) mdIt.next();
                ByteArrayOutputStream stream =
                    (ByteArrayOutputStream) mdMap.get(name);
                byte[] xmlBytes = stream.toByteArray();
                HashMap<String, String> mdProperties = null;
                if (name.equals("escidoc")) {
                    mdProperties = new HashMap<String, String>();
                    mdProperties.put("nsUri", escidocMdRecordnsUri);

                }
                Datastream ds =
                    new Datastream(name, getItem().getId(), xmlBytes,
                        "text/xml", mdProperties);
                HashMap mdRecordAttributes =
                    (HashMap) mdAttributesMap.get(name);
                ds.addAlternateId(Datastream.METADATA_ALTERNATE_ID);
                ds.addAlternateId((String) mdRecordAttributes.get("type"));
                ds.addAlternateId((String) mdRecordAttributes.get("schema"));
                dsMap.put(name, ds);
            }
            getItem().setMdRecords(dsMap);
        }
    }

    /**
     * Creates Datastream objects from the values in
     * <code>contentStreamMap</code> and calls Item.setContentStreams with a
     * HashMap which contains the metadata datastreams as Datastream objects.
     * 
     * @param contentStreamMap
     *            A HashMap which contains the metadata datastreams as
     *            ByteArrayOutputStream.
     * @throws IntegritySystemException
     *             e
     * @throws WebserverSystemException
     *             e
     * @throws FedoraSystemException
     *             e
     */
    private void setContentStreams(
        final Map<String, Map<String, Object>> contentStreamMap)
        throws FedoraSystemException, WebserverSystemException,
        IntegritySystemException {
        HashMap<String, Datastream> contentStreamDatastreams =
            new HashMap<String, Datastream>();

        Iterator<String> csIt = contentStreamMap.keySet().iterator();
        while (csIt.hasNext()) {
            String name = csIt.next();
            Map<String, Object> csValues = contentStreamMap.get(name);

            Datastream ds = null;
            if (csValues.containsKey(Elements.ELEMENT_CONTENT)) {
                ByteArrayOutputStream stream =
                    (ByteArrayOutputStream) csValues
                        .get(Elements.ELEMENT_CONTENT);
                byte[] xmlBytes = stream.toByteArray();
                ds =
                    new Datastream(name, getItem().getId(), xmlBytes,
                        (String) csValues
                            .get(Elements.ATTRIBUTE_CONTENT_STREAM_MIME_TYPE));
            }
            else if (csValues.containsKey(Elements.ATTRIBUTE_XLINK_HREF)) {
                ds =
                    new Datastream(name, getItem().getId(), (String) csValues
                        .get(Elements.ATTRIBUTE_XLINK_HREF), (String) csValues
                        .get(Elements.ATTRIBUTE_STORAGE), (String) csValues
                        .get(Elements.ATTRIBUTE_CONTENT_STREAM_MIME_TYPE));
            }
            else {
                throw new IntegritySystemException(
                    "Content streams has neither href nor content.");
            }
            String title =
                (String) csValues.get(Elements.ATTRIBUTE_XLINK_TITLE);
            if (title == null) {
                title = "";
            }
            ds.setLabel(title.trim());
            contentStreamDatastreams.put(name, ds);
        }

        getItem().setContentStreams(contentStreamDatastreams);
    }

    /**
     * @param xml
     *            XML representation of Item to create.
     * @param isCreate
     *            If true than is the return value a XML representation of the
     *            whole Item. If false than is the return value the objid of the
     *            Item only.
     * @return XML representation of created Item
     * @throws MissingContentException
     *             e
     * @throws ContextNotFoundException
     *             e
     * @throws ContentModelNotFoundException
     *             e
     * @throws ReadonlyElementViolationException
     *             e
     * @throws MissingAttributeValueException
     *             e
     * @throws MissingElementValueException
     *             e
     * @throws ReadonlyAttributeViolationException
     *             e
     * @throws InvalidXmlException
     *             e
     * @throws MissingMethodParameterException
     *             e
     * @throws FileNotFoundException
     *             e
     * @throws SystemException
     *             e
     * @throws ReferencedResourceNotFoundException
     *             e
     * @throws InvalidContentException
     *             e
     * @throws RelationPredicateNotFoundException
     *             e
     * @throws InvalidStatusException
     *             e
     * @throws MissingMdRecordException
     *             e
     * @throws AuthorizationException
     *             e
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#create(java.lang.String)
     */
    private String doCreate(final String xml, final boolean isCreate)
        throws MissingContentException, ContextNotFoundException,
        ContentModelNotFoundException, ReadonlyElementViolationException,
        MissingAttributeValueException, MissingElementValueException,
        ReadonlyAttributeViolationException, InvalidXmlException,
        MissingMethodParameterException, FileNotFoundException,
        SystemException, ReferencedResourceNotFoundException,
        InvalidContentException, RelationPredicateNotFoundException,
        MissingMdRecordException, InvalidStatusException,
        AuthorizationException {

        StaxParser sp = new StaxParser();

        ItemHandler itemHandler = new ItemHandler(sp);
        sp.addHandler(itemHandler);

        try {
            sp.parse(xml);
        }
        catch (LockingException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (OptimisticLockingException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (AlreadyExistsException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (OrganizationalUnitNotFoundException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (ContentRelationNotFoundException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (PidAlreadyAssignedException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (TmeException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        catch (XMLStreamException e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }

        ItemCreate item = itemHandler.getItem();
        itemHandler = null; // not required anymore
        item.setIdProvider(getIdProvider());

        if (!isCreate) {
            validateIngest(item);

        }
        else {
            validateCreate(item);
        }

        item.persist(true);

        // render Item for retrieve -----------------------------
        String objid = item.getObjid();
        item = null; // not needed anymore
        String resultItem = null;

        try {
            resultItem = retrieve(objid);
        }
        catch (ResourceNotFoundException e) {
            String msg =
                "The Item with id '" + objid + "', which was just created, "
                    + "could not be found for retrieve.";
            log.warn(msg);
            throw new IntegritySystemException(msg, e);
        }

        fireItemCreated(objid, resultItem);

        if (!isCreate) {
            return objid;
        }

        return resultItem;
    }

    /**
     * Validate the Item.
     * 
     * Checks if all required values are set and consistent.
     * 
     * @param item
     *            The item which is to validate.
     * @throws InvalidStatusException
     *             Thrown if Item has invalid status.
     * @throws MissingMdRecordException
     *             Thrown if required md-record is missing.
     * @throws ReferencedResourceNotFoundException
     *             Thrown if reference to another resource could not be resolved
     *             as valid resoure.
     * @throws InvalidContentException
     *             Thrown if content is invalid.
     * @throws RelationPredicateNotFoundException
     *             Thrown if the predicate of the relation was not found within
     *             the ontologie.
     * @throws SystemException
     *             TODO
     * @throws AuthorizationException
     *             Thrown if permissions are restricted on origin Item.
     * @throws MissingAttributeValueException
     *             Thrown if required attribute is missing.
     * @throws XmlCorruptedException
     *             e
     */
    private void validateCreate(final ItemCreate item)
        throws InvalidStatusException, MissingMdRecordException,
        InvalidContentException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AuthorizationException,
        SystemException, MissingAttributeValueException, XmlCorruptedException {

        /*
         * Distinguish between create and ingest in the business level and not
         * in the parser leads to this circumstance where values (like
         * public-status), which where ignored before by parser, has now to be
         * checked in a higher level.
         * 
         * Maybe there exist different ways to avoid these additional (method
         * depending) check use either separate parser (but these would be
         * mostly identical). Or enhance the XML schema and use different XML
         * schema for ingest and create (seems for me just now, SWA, a elegant
         * solution).
         */
        validateOriginItem(item);
        validate(item);

        // check status public status of Item
        StatusType publicStatus =
            item.getProperties().getObjectProperties().getStatus();

        if (publicStatus != StatusType.PENDING) {

            log.debug("New Items has to be in public-status '"
                + StatusType.PENDING + "'.");
            item.getProperties().getObjectProperties().setStatus(
                StatusType.PENDING);
            // item.getProperties().getObjectProperties().setStatusComment(
            // "Object created");
        }
    }

    /**
     * Validate the Item.
     * 
     * Checks if all required values are set and consistent.
     * 
     * @param item
     *            The item which is to validate.
     * @throws InvalidStatusException
     *             Thrown if Item has invalid status.
     * @throws MissingMdRecordException
     *             Thrown if required md-record is missing.
     * @throws ReferencedResourceNotFoundException
     *             Thrown if reference to another resource could not be resolved
     *             as valid resoure.
     * @throws InvalidContentException
     *             Thrown if content is invalid.
     * @throws RelationPredicateNotFoundException
     *             Thrown if the predicate of the relation was not found within
     *             the ontologie.
     * @throws MissingAttributeValueException
     *             Thrown if name Attribute is not unique or no Metadata Record
     *             with name "escidoc" exists.
     * @throws XmlParserSystemException
     *             Thrown if XML parser throws exception.
     * @throws EncodingSystemException
     *             Thrown if encoding is invalid.
     * @throws TripleStoreSystemException
     *             Thrown if access to TripleStore failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     * @throws InvalidXmlException
     *             e
     */
    private void validateIngest(final ItemCreate item)
        throws InvalidStatusException, TripleStoreSystemException,
        WebserverSystemException, MissingMdRecordException,
        InvalidContentException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, EncodingSystemException,
        XmlParserSystemException, MissingAttributeValueException,
        InvalidXmlException {

        /*
         * see validateCreate
         */
        validate(item);

        // check status public status of Item
        StatusType publicStatus =
            item.getProperties().getObjectProperties().getStatus();
        if (publicStatus == StatusType.RELEASED) {
            // check if we need a PID if the release an Item and if the PID is
            // given.
            if (!Boolean.valueOf(System
                .getProperty("cmm.Item.objectPid.releaseWithoutPid"))) {

                if (item.getProperties().getObjectProperties().getPid() == null) {
                    String msg =
                        "Item with public-status released requires an PID.";
                    log.debug(msg);
                    throw new InvalidStatusException(msg);
                }
            }

            // make sure that version 1 is also in status released
            // if (!Boolean.valueOf(System
            // .getProperty("cmm.Item.versionPid.releaseWithoutPid"))) {
            //
            // if (item.getProperties().getCurrentVersion().getPid() == null) {
            // String msg =
            // "Item with version-status released requires an PID.";
            // log.debug(msg);
            // throw new InvalidStatusException(msg);
            // }
            // }
            item.getProperties().getCurrentVersion().setStatus(
                StatusType.RELEASED);
            item.getProperties().setLatestReleasedVersion(
                item.getProperties().getCurrentVersion());
        }
        else if (publicStatus != StatusType.PENDING) {
            log.debug("New Items has to be in public-status '"
                + StatusType.PENDING + "' or '" + StatusType.RELEASED);
            item.getProperties().getObjectProperties().setStatus(
                StatusType.PENDING);
            item.getProperties().getCurrentVersion().setStatus(
                StatusType.PENDING);
        }
    }

    /**
     * Validate the Item structure in general (independent if create or ingest
     * was selected).
     * 
     * Checks if all required values are set and consistent.
     * 
     * @param item
     *            The item which is to validate.
     * @throws InvalidStatusException
     *             Thrown if Item has invalid status.
     * @throws MissingMdRecordException
     *             Thrown if required md-record is missing.
     * @throws ReferencedResourceNotFoundException
     *             Thrown if reference to another resource could not be resolved
     *             as valid resoure.
     * @throws InvalidContentException
     *             Thrown if content is invalid.
     * @throws RelationPredicateNotFoundException
     *             Thrown if the predicate of the relation was not found within
     *             the ontologie.
     * @throws MissingAttributeValueException
     *             Thrown if name Attribute is not unique or no Metadata Record
     *             with name "escidoc" exists.
     * @throws XmlParserSystemException
     *             Thrown if XML parser throws exception.
     * @throws EncodingSystemException
     *             Thrown if encoding is invalid.
     * @throws TripleStoreSystemException
     *             Thrown if access to TripleStore failed.
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     * @throws XmlCorruptedException
     *             e
     */
    private void validate(final ItemCreate item) throws InvalidStatusException,
        TripleStoreSystemException, WebserverSystemException,
        MissingMdRecordException, InvalidContentException,
        XmlCorruptedException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, EncodingSystemException,
        XmlParserSystemException, MissingAttributeValueException {

        checkContextStatus(item
            .getProperties().getObjectProperties().getContextId(),
            Constants.STATUS_CONTEXT_OPENED);

        // validate Content Relations
        Iterator<RelationCreate> it = item.getRelations().iterator();
        if (it != null) {
            while (it.hasNext()) {
                RelationCreate relation = it.next();
                checkRefElement(relation.getTarget());
                ContentRelationsUtility utility = new ContentRelationsUtility();
                if (!utility.validPredicate(relation.getPredicateNs() + "#"
                    + relation.getPredicate())) {
                    String message =
                        "Predicate '" + relation.getPredicate()
                            + "' is invalid. ";
                    if (log.isDebugEnabled()) {
                        log.debug(message);
                    }
                    throw new RelationPredicateNotFoundException(message);
                }
            }
        }

        // validate Metadata Records
        checkMetadataRecords(item);
    }

    /**
     * Validate permissions and status of origin Item (surrogate Item concept).
     * 
     * @param item
     *            The surrogate Item.
     * @throws InvalidContentException
     *             Thrown if content is invalid.
     * @throws InvalidStatusException
     *             Thrown if status of origin Item in invalid.
     * @throws AuthorizationException
     *             Thrown if access to origin Item is denied.
     * @throws SystemException
     *             Thrown in case of internal error.
     */
    private void validateOriginItem(final ItemCreate item)
        throws InvalidContentException, InvalidStatusException,
        AuthorizationException, SystemException {

        String origin = item.getProperties().getObjectProperties().getOrigin();
        if (origin != null) {
            String objid =
                item.getProperties().getObjectProperties().getOriginObjectId();
            String versionNumber =
                item.getProperties().getObjectProperties().getOriginVersionId();
            // in a case of a floating reference to the origin item
            // ensure, that a latest release and not a latest version
            // of the origin item will be fetched.
            String publicStatus =
                TripleStoreUtility.getInstance().getPropertiesElements(objid,
                    TripleStoreUtility.PROP_PUBLIC_STATUS);
            if (publicStatus == null) {
                String message =
                    "A referenced Item '" + origin + "' does not exist.";
                log.error(message);
                throw new InvalidContentException();
            }
            else if (publicStatus.equals(Constants.STATUS_WITHDRAWN)) {
                String message =
                    "The referenced Item '" + origin
                        + "' is in status 'withdrawn'. The surrogate Item can "
                        + "not be created.";
                log.error(message);
                throw new InvalidStatusException();
            }

            String latestReleaseNumber =
                TripleStoreUtility.getInstance().getPropertiesElements(objid,
                    TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER);
            if (latestReleaseNumber == null) {

                String message =
                    "The referenced Item with id '" + origin
                        + "' is not released.";
                log.error(message);
                throw new InvalidStatusException(message);
            }
            if (versionNumber == null) {
                origin = objid + ":" + latestReleaseNumber;
            }

            if (!checkUserRights(origin)) {
                String message =
                    "You can not create a surrogate Item based "
                        + "on the Item '" + origin
                        + "' because you have no access "
                        + "rights on this Item.";
                log.debug(message);
                throw new AuthorizationException(message);
            }
            try {
                setOriginItem(origin);

            }
            catch (ItemNotFoundException e) {
                String message =
                    "The referenced Item '" + origin + "' does not exist.";
                log.debug(message);
                throw new InvalidContentException();
            }

            if (getOriginItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN) != null) {
                String message =
                    "A referenced original Item should be "
                        + "a regular Item, not a surrogate Item.";
                log.debug(message);
                throw new InvalidContentException(message);
            }
            String versionStatus =
                getOriginItem().getResourceProperties().get(
                    PropertyMapKeys.CURRENT_VERSION_STATUS);
            if (!versionStatus.equals(Constants.STATUS_RELEASED)) {
                String message =
                    "The referenced Item version is not released. "
                        + "You can create a surrogate Item only based on a "
                        + "released Item version.";
                log.debug(message);
                throw new InvalidStatusException(message);
            }

        }

    }

    /**
     * Check if the name attribute of Metadata Records is unique and at least
     * one Metadata Record has the name "escidoc".
     * 
     * @param item
     *            Item which is to validate.
     * 
     * @throws MissingMdRecordException
     *             Thrown if the mandatory MdRecortd with name "escidoc" is
     *             missing.
     * @throws InvalidContentException
     *             Thrown if content is invalid.
     */
    private void checkMetadataRecords(final ItemCreate item)
        throws MissingMdRecordException, InvalidContentException {

        /*
         * TODO move Item validation to seperate validation class.
         */
        Vector<MdRecordCreate> mdRecords = item.getMetadataRecords();

        // check if md-record with name 'escidoc'

        if ((mdRecords == null) || mdRecords.size() < 1) {
            if (item.getProperties().getObjectProperties().getOrigin() == null) {
                String message =
                    "The Item representation doesn't contain a "
                        + "mandatory md-record. A regular Item must contain a "
                        + "mandatory md-record. ";
                log.error(message);
                throw new MissingMdRecordException(message);
            }

        }
        else {

            Vector<String> mdRecordNames = new Vector<String>();
            String name = null;
            for (int i = 0; i < mdRecords.size(); i++) {

                name = mdRecords.get(i).getName();

                // check uniqueness of names
                if (mdRecordNames.contains(name)) {
                    throw new InvalidContentException(
                        "Metadata 'md-record' with name='"
                        // + Elements.MANDATORY_MD_RECORD_NAME
                            + name + "' exists multiple times.");
                }

                mdRecordNames.add(name);
            }
            if (!mdRecordNames.contains(Elements.MANDATORY_MD_RECORD_NAME)
                && item.getProperties().getObjectProperties().getOrigin() == null) {
                String message =
                    "The item representation doesn't contain a "
                        + "mandatory md-record. A regular item must contain a "
                        + "mandatory md-record. ";
                log.error(message);
                throw new MissingMdRecordException(message);
            }

        }
    }

    /**
     * 
     * @param targetId
     *            Objid of reference target.
     * @throws InvalidContentException
     *             Thrown if reference points to an not supported Resource (not
     *             to Item or Container)
     * @throws TripleStoreSystemException
     *             Thrown if requesting type of resource failed.
     * @throws ReferencedResourceNotFoundException
     *             Thrown if no resource under provided id exists.
     * @throws WebserverSystemException
     *             Thrown if creating instance of TripleStoreUtility failed.
     */
    private void checkRefElement(final String targetId)
        throws InvalidContentException, TripleStoreSystemException,
        ReferencedResourceNotFoundException, WebserverSystemException {

        String targetObjectType =
            TripleStoreUtility.getInstance().getObjectType(targetId);

        if (targetObjectType == null) {
            String message =
                "Resource with id '" + targetId + "' does not exist.";
            log.debug(message);
            throw new ReferencedResourceNotFoundException(message);
        }

        if (!de.escidoc.core.common.business.Constants.ITEM_OBJECT_TYPE
            .equals(targetObjectType)
            && !de.escidoc.core.common.business.Constants.CONTAINER_OBJECT_TYPE
                .equals(targetObjectType)) {
            String message =
                "A related resource '" + targetId
                    + "' is neither 'Item' nor 'Container' ";

            log.debug(message);
            throw new InvalidContentException(message);
        }
    }

    /**
     * Obtain right version of origin Item.
     * 
     * @throws ItemNotFoundException
     *             Thrown if no Item with this objid exits.
     * @throws SystemException
     *             Thrown in case of TripleStore request failures.
     */
    private void prepareAndSetOriginItem() throws ItemNotFoundException,
        SystemException {

        String originObjectId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        String originId = null;

        String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);

        if (originVersionId == null) {
            String latestReleaseNumber =
                TripleStoreUtility.getInstance().getPropertiesElements(
                    originObjectId,
                    Constants.RELEASE_NS_URI + Elements.ELEMENT_NUMBER);
            setOriginId(originObjectId);
            originId = originObjectId + ":" + latestReleaseNumber;
        }
        else {
            originId = originObjectId + ":" + originVersionId;
            setOriginId(originId);
        }
        setOriginItem(originId);
    }

    /**
     * Check if the user has priviliges to access the origin Item.
     * 
     * @param origin
     *            Objid of the origin Item
     * @return true if user has permission on origin Item, false if access with
     *         provided userid is forbidden.
     * @throws SystemException
     *             Thrown in case of internal failure.
     */
    private boolean checkUserRights(final String origin) throws SystemException {

        Vector<String> id = new Vector<String>();
        id.add(origin);

        List<String> ids;
        try {
            ids = getPdp().evaluateRetrieve("item", id);
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e);
        }

        if ((ids == null) || ids.size() == 0) {
            return false;
        }

        return true;
    }

    // /**
    // * Check if Item is surrogate Item and set objid of origin Item.
    // *
    // * @throws ItemNotFoundException
    // * Thrown if the resource for the obtained objid is no Item.
    // * @throws AuthorizationException
    // * Thrown if user has no permission to origin Item.
    // * @throws SystemException
    // * Thrown in case of internal error.
    // */
    // private void setOriginItem() throws ItemNotFoundException,
    // AuthorizationException, SystemException {
    //
    // String originObjectId =
    // getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
    //
    // if (originObjectId != null) {
    // prepareAndSetOriginItem();
    // if (!checkUserRights(getOriginItem().getFullId())) {
    // String message =
    // "You cannot access a full surrogate item representation"
    // + " because you have no access rights on the item "
    // + getOriginId()
    // + " . You can access subressourcess owned by a "
    // + "surrogate item using retrieve methods on "
    // + "subresources.";
    //
    // log.debug(message);
    // throw new AuthorizationException(message);
    // }
    // }
    // else {
    // setOriginItem(null);
    // }
    // }

    /**
     * Load origin Item. User permissions are checked.
     * 
     * @param errorMessage
     *            The error message if failure occurs because of permission
     *            restriction.
     * @return true if origin Item was loaded, false otherwise
     * 
     * @throws ItemNotFoundException
     *             Thrown if Item with provided objid not exits.
     * @throws SystemException
     *             Thrown in case of internal failure.
     * @throws AuthorizationException
     *             Thrown if user has no permission to use origin Item.
     */
    private boolean loadOrigin(final String errorMessage)
        throws ItemNotFoundException, SystemException, AuthorizationException {

        String originObjectId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        boolean origin = false;

        if (originObjectId != null) {
            origin = true;
            prepareAndSetOriginItem();
            if (!checkUserRights(getOriginItem().getFullId())) {
                log.debug(errorMessage);
                throw new AuthorizationException(errorMessage);
            }
        }
        else {
            resetOriginItem();
        }

        return origin;
    }
}
