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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamException;

import de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.PropertyMapKeys;
import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.business.fedora.resources.create.ItemCreate;
import de.escidoc.core.common.business.fedora.resources.create.MdRecordCreate;
import de.escidoc.core.common.business.fedora.resources.create.RelationCreate;
import de.escidoc.core.common.business.fedora.resources.item.Component;
import de.escidoc.core.common.business.filter.LuceneRequestParameters;
import de.escidoc.core.common.business.filter.SRURequest;
import de.escidoc.core.common.business.filter.SRURequestParameters;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
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
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.MultipleExtractor;
import de.escidoc.core.common.util.stax.handler.OptimisticLockingHandler;
import de.escidoc.core.common.util.stax.handler.TaskParamHandler;
import de.escidoc.core.common.util.xml.Elements;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.StartElementWithChildElements;
import de.escidoc.core.om.business.fedora.ContentRelationsUtility;
import de.escidoc.core.om.business.fedora.contentRelation.FedoraContentRelationHandler;
import de.escidoc.core.om.business.interfaces.ItemHandlerInterface;
import de.escidoc.core.om.business.stax.handler.ContentRelationsAddHandler2Edition;
import de.escidoc.core.om.business.stax.handler.ContentRelationsRemoveHandler2Edition;
import de.escidoc.core.om.business.stax.handler.ContentRelationsUpdateHandler2Edition;
import de.escidoc.core.om.business.stax.handler.MdRecordsUpdateHandler;
import de.escidoc.core.om.business.stax.handler.component.NewComponentExtractor;
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
 */
public class FedoraItemHandler extends ItemHandlerPid
    implements ItemHandlerInterface {

    private static final AppLogger LOGGER = new AppLogger(
        FedoraItemHandler.class.getName());

    private FedoraContentRelationHandler contentRelationHandler = null;

    /** The policy decision point used to check access privileges. */
    private PolicyDecisionPointInterface pdp;

    /** SRU request. */
    private SRURequest sruRequest = null;

    /**
     * Gets the {@link PolicyDecisionPointInterface} implementation.
     * 
     * @return PolicyDecisionPointInterface
     */
    PolicyDecisionPointInterface getPdp() {

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
    @Override
    public String retrieve(final String id) throws ItemNotFoundException,
        MissingMethodParameterException, SystemException,
        ComponentNotFoundException, AuthorizationException {

        setItem(id);
        final String message =
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
    @Override
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
        final String startTimestamp = getItem().getLastFedoraModificationDate();

        checkLatestVersion();
        checkLocked();
        checkWithdrawn("No update allowed.");
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        final String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ':' + originVersionId;
        }
        final boolean origin =
            loadOrigin("You cannot update a full surrogate Item representation "
                + "because you have no access rights on the Item '"
                + originId
                + "'. Subressources which are part of the surrogate Item "
                + "(and not the origin Item) are still accessible. Try "
                + "using subresource methods.");

        try {
            final StaxParser sp = new StaxParser();

            final OptimisticLockingHandler olh =
                new OptimisticLockingHandler(getItem().getId(),
                    Constants.ITEM_OBJECT_TYPE, getItem()
                        .getLastModificationDate(), sp);
            sp.addHandler(olh);
            final ContentRelationsUpdateHandler2Edition cruh =
                new ContentRelationsUpdateHandler2Edition(sp);
            sp.addHandler(cruh);

            sp.addHandler(new ItemUpdateHandler(getItem().getId(), sp));

            final MdRecordsUpdateHandler mdHandler =
                new MdRecordsUpdateHandler("/item/md-records", sp, origin);
            sp.addHandler(mdHandler);

            ContentStreamHandler csh = null;
            NewComponentExtractor nce = null;
            ComponentMdRecordsUpdateHandler cmuh = null;

            if (!origin) {
                csh = new ContentStreamHandler(getItem());
                sp.addHandler(csh);
                final ComponentUpdateHandler cuh = new ComponentUpdateHandler(getItem().getId(),
                        "/item/components/component", sp);
                sp.addHandler(cuh);
                nce = new NewComponentExtractor(sp);
                cmuh =
                    new ComponentMdRecordsUpdateHandler(
                        "/item/components/component", sp);

                sp.addHandler(cmuh);
                sp.addHandler(nce);
            }
            final HashMap<String, String> extractPathes =
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
            final MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
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

            final Map<String, Object> streams = me.getOutputStreams();
            try {
                final Object cmsStream =
                    streams.get(Elements.ELEMENT_CONTENT_MODEL_SPECIFIC);
                if (cmsStream != null) {
                    setContentTypeSpecificProperties(((ByteArrayOutputStream) cmsStream)
                        .toString(XmlUtility.CHARACTER_ENCODING));
                }
            }
            catch (UnsupportedEncodingException e) {
                throw new EncodingSystemException(e.getMessage(), e);
            }
            final Map mdRecordsStreams = (HashMap) streams.get("md-records");
            if ((mdRecordsStreams != null)
                && !mdRecordsStreams.containsKey("escidoc") && !origin) {
                throw new MissingMdRecordException(
                    "No escidoc internal metadata found "
                        + "(md-record/@name='escidoc'");
            }
            final Map<String, Map<String, String>> mdRecordsAttributes =
                mdHandler.getMetadataAttributes();
            final String escidocMdNsUri = mdHandler.getEscidocMdRecordNameSpace();
            setMetadataRecords(mdRecordsStreams, mdRecordsAttributes,
                escidocMdNsUri);

            // set content streams
            if (!origin) {
                setContentStreams(csh.getContentStreams());
            }
            // set content relations
            final List<String> relationsToUpdate = cruh.getContentRelationsData();
            getItem().setContentRelations(sp, relationsToUpdate);

            // components
            // TODO: Aenderungen an ITEM.RELS-EXT fuer alle components in einem
            // schritt machen + Aenderungen an content relations (? FRS)
            boolean resourceUpdated = false;
            if (!origin) {
                Map<String, Object> components =
                    (Map<String, Object>) streams.get("components");

                if (components == null) {
                    components = new HashMap<String, Object>();
                }
                final Map<String, Map<String, Map<String, String>>> componentMdRecordsAttributes =
                    cmuh.getMetadataAttributes();

                final Map<String, String> nsUris = cmuh.getNamespacesMap();
                components.put("new", nce.getOutputStreams());
                setComponents(components, componentMdRecordsAttributes, nsUris);

                // this persist is necessary to control the Components
                resourceUpdated = getItem().persistComponents();
            }

            // check if modified
            final String updatedXmlData;
            final String endTimestamp = getItem().getLastFedoraModificationDate();
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
            throw new WebserverSystemException("unreachable", e);
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
    @Override
    public String create(final String xml) throws MissingContentException,
        ContextNotFoundException, ContentModelNotFoundException,
        ReadonlyElementViolationException, MissingAttributeValueException,
        MissingElementValueException, ReadonlyAttributeViolationException,
        XmlCorruptedException, MissingMethodParameterException,
        FileNotFoundException, SystemException,
        ReferencedResourceNotFoundException, InvalidContentException,
        RelationPredicateNotFoundException, MissingMdRecordException,
        InvalidStatusException, AuthorizationException {

        final ItemCreate item = parseItem(xml);

        // check that the objid was not obtained from the representation
        item.setObjid(null);

        item.setIdProvider(getIdProvider());
        validateCreate(item);
        item.persist(true);
        final String objid = item.getObjid();
        final String resultItem;
        try {
            resultItem = retrieve(objid);
        }
        catch (ResourceNotFoundException e) {
            final String msg =
                "The Item with id '" + objid + "', which was just created, "
                    + "could not be found for retrieve.";
            LOGGER.warn(msg);
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
    @Override
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

        final ItemCreate item = parseItem(xml);
        item.setIdProvider(getIdProvider());
        validateIngest(item);
        item.persist(true);
        final String objid = item.getObjid();
        try {
            if (EscidocConfiguration.getInstance().getAsBoolean(
                EscidocConfiguration.ESCIDOC_CORE_NOTIFY_INDEXER_ENABLED)) {
                fireItemCreated(objid, retrieve(objid));
            }
        }
        catch (IOException e) {
            throw new SystemException(
                "The eSciDoc configuration could not be read", e);
        }
        catch (ResourceNotFoundException e) {
            final String msg =
                "The Item with id '" + objid + "', which was just ingested, "
                    + "could not be found for retrieve.";
            LOGGER.warn(msg);
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
    @Override
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
    @Override
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
    @Override
    public String retrieveMdRecords(final String id)
        throws ItemNotFoundException, MissingMethodParameterException,
        SystemException, AuthorizationException {

        setItem(id);
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        final String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ':' + originVersionId;
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
    @Override
    public String retrieveMdRecord(final String id, final String mdRecordId)
        throws ItemNotFoundException, MdRecordNotFoundException,
        MissingMethodParameterException, SystemException,
        AuthorizationException {

        setItem(id);

        String mdRecord;
        try {
            mdRecord = renderMdRecord(mdRecordId, false, true);
            if (mdRecord.length() == 0) {
                throw new MdRecordNotFoundException();
            }
        }
        catch (MdRecordNotFoundException e) {
            final String originObjectId =
                getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
            if (originObjectId != null) {
                final String message =
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
            final String message =
                "Metadata record with name " + mdRecordId
                    + " not found in item " + id + '.';
            LOGGER.debug(message);
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
    @Override
    public String retrieveMdRecordContent(
        final String id, final String mdRecordId) throws ItemNotFoundException,
        MdRecordNotFoundException, MissingMethodParameterException,
        SystemException, AuthorizationException {
        setItem(id);
        String mdRecord;
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
            final boolean origin = loadOrigin(message);
            if (origin) {
                mdRecord = retrieveMdRecord(mdRecordId, true);
            }
            else {
                message =
                    "Metadata record with name " + mdRecordId
                        + " not found in item " + id + '.';
                LOGGER.debug(message);
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
    @Override
    public String retrieveDcRecordContent(final String id)
        throws ItemNotFoundException, MissingMethodParameterException,
        SystemException, MdRecordNotFoundException, AuthorizationException {
        setItem(id);
        String dc;
        try {
            final Datastream mdRecord = getItem().getMdRecord(
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
            final boolean origin = loadOrigin(message);
            if (origin) {
                dc = getOriginItem().getDc().toString();
            }
            else {
                message =
                    "Metadata record with name DC" + " not found in item " + id
                        + '.';
                LOGGER.debug(message);
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
    @Override
    public String updateMetadataRecord(
        final String id, final String mdRecordId, final String xmlData)
        throws ItemNotFoundException, XmlSchemaNotFoundException,
        LockingException, XmlCorruptedException, XmlSchemaValidationException,
        InvalidContentException, MdRecordNotFoundException,
        ReadonlyViolationException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidStatusException,
        ReadonlyVersionException, AuthorizationException {

        setItem(id);
        final String startTimestamp = getItem().getLastFedoraModificationDate();

        checkLatestVersion();
        checkLocked();
        checkReleased();
        checkWithdrawn("No update allowed.");

        final StaxParser sp = new StaxParser();
        final OptimisticLockingHandler olh =
            new OptimisticLockingHandler(getItem().getId(),
                Constants.ITEM_OBJECT_TYPE,
                getItem().getLastModificationDate(), sp);
        sp.addHandler(olh);
        final MdRecordsUpdateHandler mdHandler = new MdRecordsUpdateHandler("", sp);
        sp.addHandler(mdHandler);

        final MultipleExtractor me = new MultipleExtractor("/md-record", "name", sp);
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

        final Map mds = (HashMap) me.getOutputStreams().get("md-records");
        // there is only one md-record (root element is md-record)
        final ByteArrayOutputStream mdXml =
            (ByteArrayOutputStream) mds.get(mdRecordId);
        final Map<String, Map<String, String>> mdAttributes =
            mdHandler.getMetadataAttributes();
        final Map<String, String> mdRecordAttributes = mdAttributes.get(mdRecordId);
        try {
            setMetadataRecord(mdRecordId,
                mdXml.toString(XmlUtility.CHARACTER_ENCODING),
                mdRecordAttributes);
        }
        catch (UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }

        final String endTimestamp = getItem().getLastFedoraModificationDate();
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
        final String newMdRecord;
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
    @Override
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
    @Override
    public String createMdRecord(final String id, final String xmlData)
        throws ItemNotFoundException, SystemException,
        XmlSchemaValidationException, LockingException,
        MissingAttributeValueException, InvalidStatusException,
        ComponentNotFoundException, AuthorizationException {

        setItem(id);
        final String startTimestamp = getItem().getLastFedoraModificationDate();

        checkLocked();
        checkReleased();

        final HashMap<String, String> extractPathes = new HashMap<String, String>();
        extractPathes.put("/md-record", "name");

        final StaxParser sp = new StaxParser();
        final MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
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
        final Map map = me.getOutputStreams();
        final Map mdRecords = (Map) map.get("md-records");
        final Set keySet = mdRecords.keySet();
        final Iterator it = keySet.iterator();
        if (!it.hasNext()) {
            throw new XmlSchemaValidationException(
                "No name found for metadata datastream.");
        }
        final String name = (String) it.next();

        final byte[] xmlDataBytes;
        try {
            xmlDataBytes = xmlData.getBytes(XmlUtility.CHARACTER_ENCODING);
        }
        catch (UnsupportedEncodingException e) {
            throw new EncodingSystemException(e.getMessage(), e);
        }
        final Datastream newMDS =
            new Datastream(name, getItem().getId(), xmlDataBytes, "text/xml");
        newMDS.addAlternateId(Datastream.METADATA_ALTERNATE_ID); // this is the
        // reason for
        // setMdRecord etc.
        // FIXME persist DS by set it in the resource object
        newMDS.persist(false);

        final String endTimestamp = getItem().getLastFedoraModificationDate();
        if (!startTimestamp.equals(endTimestamp)) {
            makeVersion("Item.createMetadataRecord");
            getItem().persist();
        }
        final String newMdRecord;
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
    @Override
    public String retrieveComponents(final String id)
        throws ItemNotFoundException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException,
        AuthorizationException {

        setItem(id);
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        final String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ':' + originVersionId;
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

        setItem(id);
        filterParams.put("query", new String[] { "\"/subject/id\"="
            + getItem().getId() + " or " + "\"/subject/id\"="
            + getItem().getFullId() + " or " + "\"/object/id\"="
            + getItem().getId() + " or " + "\"/object/id\"="
            + getItem().getFullId() });

        final String searchResponse = contentRelationHandler.retrieveContentRelations(
                new LuceneRequestParameters(filterParams));
        return transformSearchResponse2relations(searchResponse);

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

        final String startTimestamp = getItem().getLastFedoraModificationDate();

        final StaxParser sp = new StaxParser();
        final OptimisticLockingHandler olh =
            new OptimisticLockingHandler(getItem().getId(),
                Constants.ITEM_OBJECT_TYPE,
                getItem().getLastModificationDate(), sp);
        sp.addHandler(olh);

        final ComponentMdRecordsUpdateHandler cmuh =
            new ComponentMdRecordsUpdateHandler(
                "/components/component/md-records", sp);
        sp.addHandler(cmuh);
        // extract datastreams from xmlData
        final HashMap<String, String> extractPathes = new HashMap<String, String>();
        extractPathes.put("/components/component", "objid");
        extractPathes.put("/components/component/properties", null);
        extractPathes.put("/components/component/licenses", null);
        extractPathes.put("/components/component/content", null);
        extractPathes.put("/components/component/md-records/md-record", "name");

        final MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
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

        final Map<String, Object> streams = me.getOutputStreams();
        setComponents(streams, cmuh.getMetadataAttributes(),
            cmuh.getNamespacesMap());

        // return the new item
        String updatedXmlData = null;
        try {
            updatedXmlData = retrieveComponents(id);
        }
        catch (AuthorizationException e) {
            // can not occur
        }
        final String endTimestamp = getItem().getLastFedoraModificationDate();
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
    @Override
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
    @Override
    public EscidocBinaryContent retrieveResource(
        final String id, final String resourceName,
        final Map<String, String[]> parameters) throws SystemException,
        ItemNotFoundException, OperationNotFoundException {

        final EscidocBinaryContent content = new EscidocBinaryContent();
        content.setMimeType("text/xml");

        if ("version-history".equals(resourceName)) {
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
        else if ("relations".equals(resourceName)) {
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
        final String contentModelId =
            getItem().getProperty(
                PropertyMapKeys.LATEST_VERSION_CONTENT_MODEL_ID);
        final byte[] bytes;
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
    @Override
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
    @Override
    public String retrieveComponent(final String id, final String componentId)
        throws ItemNotFoundException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException,
        AuthorizationException {

        setItem(id);
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        final String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ':' + originVersionId;
        }
        loadOrigin("You have no access rights on the item " + originId
            + " , which is reffered by a surrogate item " + id
            + ". Therefore you cannot access any subressources of this item.");

        return renderComponent(componentId, true);
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
    @Override
    public String retrieveComponentMdRecords(
        final String id, final String componentId)
        throws ItemNotFoundException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException,
        AuthorizationException {

        setItem(id);
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        final String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ':' + originVersionId;
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
    @Override
    public String retrieveComponentMdRecord(
        final String id, final String componentId, final String mdRecordId)
        throws ItemNotFoundException, ComponentNotFoundException,
        MdRecordNotFoundException, MissingMethodParameterException,
        SystemException, AuthorizationException {

        setItem(id);
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        final String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ':' + originVersionId;
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
    @Override
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
    @Override
    public String createComponent(final String id, final String xmlData)
        throws ItemNotFoundException, MissingContentException,
        ReadonlyViolationException, LockingException,
        MissingElementValueException, XmlCorruptedException,
        InvalidStatusException, MissingMethodParameterException,
        FileNotFoundException, InvalidContentException, SystemException,
        XmlSchemaValidationException, OptimisticLockingException,
        MissingAttributeValueException, ComponentNotFoundException {

        setItem(id);
        final StaxParser sp = new StaxParser();
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
        final String addedComponent = addComponent(xmlData);
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
    @Override
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
        final String startTimestamp = getItem().getLastFedoraModificationDate();

        checkLatestVersion();
        checkLocked();
        checkReleased();
        checkWithdrawn("No update allowed.");

        final HashMap<String, String> extractPathes = new HashMap<String, String>();
        extractPathes.put("/component/properties", null);
        extractPathes.put("/component/md-records/md-record", "name");
        extractPathes.put("/component/content", null);

        final StaxParser sp = new StaxParser();
        final OptimisticLockingHandler olh =
            new OptimisticLockingHandler(getItem().getId(),
                Constants.ITEM_OBJECT_TYPE,
                getItem().getLastModificationDate(), sp);
        sp.addHandler(olh);
        final ComponentMdRecordsUpdateHandler cmuh =
            new ComponentMdRecordsUpdateHandler("/component/md-records", sp);
        sp.addHandler(cmuh);
        final MultipleExtractor me = new MultipleExtractor(extractPathes, sp);
        sp.addHandler(me);

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

        final Map<String, Object> compsMap =
            (HashMap<String, Object>) me.getOutputStreams().get("components");
        final Map compMap = (Map) compsMap.get(componentId);

        setComponent(getItem().getComponent(componentId), compMap, cmuh
            .getMetadataAttributes().get(componentId), cmuh
            .getNamespacesMap().get(componentId));

        final String updatedXmlData = retrieveComponent(id, componentId);

        final String endTimestamp = getItem().getLastFedoraModificationDate();
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
    @Override
    public String release(final String id, final String param)
        throws ItemNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException,
        ReadonlyVersionException, InvalidXmlException,
        ComponentNotFoundException {

        setItem(id);
        final TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);

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
            final String curStatus =
                getItem().getProperty(
                    PropertyMapKeys.LATEST_VERSION_VERSION_STATUS);
            if (!Constants.STATUS_SUBMITTED.equals(curStatus)) {
                throw new InvalidStatusException("The object is not in state '"
                    + Constants.STATUS_SUBMITTED + "' and can not be "
                    + Constants.STATUS_RELEASED + '.');
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
            final List<String> surrogateItemIds =
                getTripleStoreUtility().getSurrogates(getItem().getId());
            final Collection<String> referencedSurrogateItemIds = new ArrayList<String>();
            for (final String surrogateId : surrogateItemIds) {
                final String versionId =
                    getTripleStoreUtility().getRelation(surrogateId,
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

        return getUtility().prepareReturnXmlFromLastModificationDate(
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
    @Override
    public String submit(final String id, final String param)
        throws ItemNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException,
        ReadonlyVersionException, InvalidXmlException,
        ComponentNotFoundException {

        setItem(id);
        final TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);

        checkWithdrawn("No modification allowed.");
        checkLatestVersion();
        checkLocked();
        checkReleased();

        if (getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(),
            "Item " + getItem().getId())) {

            // check version status
            final String curStatus = getItem().getVersionStatus();
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

        return getUtility().prepareReturnXmlFromLastModificationDate(
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
    @Override
    public String revise(final String id, final String param)
        throws ItemNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException,
        ReadonlyVersionException, XmlCorruptedException,
        ComponentNotFoundException {

        setItem(id);
        final TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);

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

        return getUtility().prepareReturnXmlFromLastModificationDate(
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
    @Override
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
            getTripleStoreUtility().getPropertiesElements(id,
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

        final TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);

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

        return getUtility().prepareReturnXmlFromLastModificationDate(
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
    @Override
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
        final TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);
        getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(), "Item " + id);

        final StaxParser sp = new StaxParser();

        final ContentRelationsAddHandler2Edition addHandler =
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
        final List<Map<String, String>> relationsData = addHandler.getRelations();

        if ((relationsData != null) && (!relationsData.isEmpty())) {
            final List<StartElementWithChildElements> elements =
                new ArrayList<StartElementWithChildElements>();
            for (final Map<String, String> relation : relationsData) {
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
                // newComponentIdElement.setElementText(componentId);
                newContentRelationElement.setChildrenElements(null);

                elements.add(newContentRelationElement);
            }
            final byte[] relsExtNewBytes =
                Utility.updateRelsExt(elements, null, null, getItem(), null);
            getItem().setRelsExt(relsExtNewBytes);

            makeVersion("Item.addContentRelations");
            getItem().persist();

            fireItemModified(getItem().getId());
        }

        return getUtility().prepareReturnXmlFromLastModificationDate(
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
    @Override
    public String removeContentRelations(final String id, final String param)
        throws SystemException, ItemNotFoundException,
        OptimisticLockingException, InvalidStatusException,
        MissingElementValueException, InvalidXmlException,
        ContentRelationNotFoundException, LockingException,
        ReadonlyViolationException, ReadonlyVersionException,
        ComponentNotFoundException {

        setItem(id);
        checkLatestVersion();
        final String startTimestamp = getItem().getLastFedoraModificationDate();
        checkLocked();
        checkWithdrawn("Removing of content relations is not allowed.");
        final TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);
        getUtility().checkOptimisticLockingCriteria(
            getItem().getLastModificationDate(),
            taskParameter.getLastModificationDate(), "Item " + id);

        final StaxParser sp = new StaxParser();

        final ContentRelationsRemoveHandler2Edition removeHandler =
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

        final List<Map<String, String>> relationsData = removeHandler.getRelations();
        if ((relationsData != null) && (!relationsData.isEmpty())) {
            final Map<String, List<StartElementWithChildElements>> toRemove =
                new TreeMap<String, List<StartElementWithChildElements>>();
            final Iterator<Map<String, String>> iterator =
                relationsData.iterator();
            final HashMap<String, List<StartElementWithChildElements>> predicateValuesVectorAssignment =
                new HashMap<String, List<StartElementWithChildElements>>();
            boolean resourceUpdated = false;
            while (iterator.hasNext()) {
                resourceUpdated = true;
                final Map<String, String> relation = iterator.next();

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
                    final List<StartElementWithChildElements> vector =
                        predicateValuesVectorAssignment.get(predicateValue);
                    vector.add(newContentRelationElement);
                }
                else {
                    final List<StartElementWithChildElements> vector =
                        new ArrayList<StartElementWithChildElements>();
                    vector.add(newContentRelationElement);
                    predicateValuesVectorAssignment.put(predicateValue, vector);
                }

            }
            final Set<String> keySet = predicateValuesVectorAssignment.keySet();
            for (final String predicateValue : keySet) {
                final List<StartElementWithChildElements> elements =
                    predicateValuesVectorAssignment.get(predicateValue);
                toRemove.put("/RDF/Description/" + predicateValue, elements);
            }
            final byte[] relsExtNewBytes =
                Utility.updateRelsExt(null, toRemove, null, getItem(), null);
            getItem().setRelsExt(relsExtNewBytes);
            // getItem().persist();

            final String endTimestamp = getItem().getLastFedoraModificationDate();
            if (resourceUpdated || !startTimestamp.equals(endTimestamp)) {
                makeVersion("Item.removeContentRelations");
                getItem().persist();
            }

            fireItemModified(getItem().getId());
        }

        return getUtility().prepareReturnXmlFromLastModificationDate(
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
    @Override
    public String lock(final String id, final String param)
        throws ItemNotFoundException, LockingException,
        InvalidContentException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidXmlException,
        ComponentNotFoundException, InvalidStatusException {

        setItem(id);
        checkWithdrawn("No modification allowed.");

        final TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);
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

        return getUtility().prepareReturnXmlFromLastModificationDate(
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
    @Override
    public String unlock(final String id, final String param)
        throws ItemNotFoundException, LockingException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidXmlException,
        ComponentNotFoundException {

        setItem(id);
        final TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);
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

        return getUtility().prepareReturnXmlFromLastModificationDate(
            getItem().getLastModificationDate());
    }

    /**
     * @param id
     * @return
     * @throws ItemNotFoundException
     * @throws SystemException
     * @see de.escidoc.core.om.business.interfaces.ItemHandlerInterface#retrieveVersionHistory(java.lang.String)
     */
    @Override
    public String retrieveVersionHistory(final String id)
        throws ItemNotFoundException, SystemException {

        setItem(id);
        final String versionsXml;

        try {
            // versionsXml =
            // getVersions().replaceFirst(
            // "xlink:type=\"simple\"",
            // "xml:base=\"" + XmlUtility.getEscidocBaseUrl()
            // + "\" xlink:type=\"simple\" ");
            versionsXml =
                getVersions().replaceFirst(
                    '<' + Constants.WOV_NAMESPACE_PREFIX + ':'
                        + Elements.ELEMENT_WOV_VERSION_HISTORY,
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><"
                        + Constants.WOV_NAMESPACE_PREFIX + ':'
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
    @Override
    public String retrieveParents(final String id)
        throws ItemNotFoundException, SystemException {
        Utility.getInstance().checkIsItem(id);
        return renderParents(id);
    }

    /**
     * @param parameters
     * @return
     * @throws SystemException
     */
    @Override
    public String retrieveItems(final SRURequestParameters parameters)
        throws SystemException {
        final StringWriter result = new StringWriter();

        if (parameters.isExplain()) {
            sruRequest.explain(result, ResourceType.ITEM);
        }
        else {
            sruRequest.searchRetrieve(result,
                new ResourceType[] { ResourceType.ITEM }, parameters);
        }
        return result.toString();
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
    @Override
    public String retrieveComponentProperties(
        final String id, final String componentId)
        throws ItemNotFoundException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException,
        AuthorizationException {

        setItem(id);
        String originId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        final String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);
        if (originVersionId != null) {
            originId = originId + ':' + originVersionId;
        }
        loadOrigin("You have no access rights on the item " + originId
            + " , which is reffered by a surrogate item " + id
            + ". Therefore you cannot access any subressources of this item.");

        return renderComponentProperties(componentId);
    }


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
    @Override
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

        final StaxParser sp = new StaxParser();

        final ItemHandler itemHandler = new ItemHandler(sp);
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
     * Set the SRURequest object.
     * 
     * @param sruRequest
     *            SRURequest
     * 
     * @spring.property ref="de.escidoc.core.common.business.filter.SRURequest"
     */
    public void setSruRequest(final SRURequest sruRequest) {
        this.sruRequest = sruRequest;
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
    @Override
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
        final Map mdMap, final Map mdAttributesMap,
        final String escidocMdRecordnsUri) throws SystemException {
        final Map<String, Datastream> dsMap = new HashMap<String, Datastream>();
        if (mdMap == null) {
            getItem().setMdRecords(dsMap);
        }
        else {
            for (final Object o : mdMap.keySet()) {
                final String name = (String) o;
                final ByteArrayOutputStream stream =
                    (ByteArrayOutputStream) mdMap.get(name);
                final byte[] xmlBytes = stream.toByteArray();
                HashMap<String, String> mdProperties = null;
                if ("escidoc".equals(name)) {
                    mdProperties = new HashMap<String, String>();
                    mdProperties.put("nsUri", escidocMdRecordnsUri);

                }
                final Datastream ds =
                    new Datastream(name, getItem().getId(), xmlBytes,
                        "text/xml", mdProperties);
                final Map mdRecordAttributes =
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
        final Map<String, Datastream> contentStreamDatastreams =
            new HashMap<String, Datastream>();

        for (final String name : contentStreamMap.keySet()) {
            final Map<String, Object> csValues = contentStreamMap.get(name);
            final Datastream ds;
            if (csValues.containsKey(Elements.ELEMENT_CONTENT)) {
                final ByteArrayOutputStream stream = (ByteArrayOutputStream) csValues.get(Elements.ELEMENT_CONTENT);
                final byte[] xmlBytes = stream.toByteArray();
                ds = new Datastream(name, getItem().getId(), xmlBytes,
                        (String) csValues.get(Elements.ATTRIBUTE_CONTENT_STREAM_MIME_TYPE));
            }
            else if (csValues.containsKey(Elements.ATTRIBUTE_XLINK_HREF)) {
                ds =
                    new Datastream(name, getItem().getId(),
                        (String) csValues.get(Elements.ATTRIBUTE_XLINK_HREF),
                        (String) csValues.get(Elements.ATTRIBUTE_STORAGE),
                        (String) csValues
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
        final StatusType publicStatus =
            item.getProperties().getObjectProperties().getStatus();

        if (publicStatus != StatusType.PENDING) {

            LOGGER.debug("New Items has to be in public-status '"
                + StatusType.PENDING + "'.");
            item.getProperties().getObjectProperties()
                .setStatus(StatusType.PENDING);
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
        final StatusType publicStatus =
            item.getProperties().getObjectProperties().getStatus();
        if (publicStatus == StatusType.RELEASED) {
            // check if we need a PID if the release an Item and if the PID is
            // given.
            if ((!Boolean.valueOf(System
                .getProperty("cmm.Item.objectPid.releaseWithoutPid")))

            && (item.getProperties().getObjectProperties().getPid() == null)) {
                final String msg =
                    "Item with public-status released requires an PID.";
                LOGGER.debug(msg);
                throw new InvalidStatusException(msg);
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
            item.getProperties().getCurrentVersion()
                .setStatus(StatusType.RELEASED);
            item.getProperties().setLatestReleasedVersion(
                item.getProperties().getCurrentVersion());
        }
        else if (publicStatus != StatusType.PENDING) {
            LOGGER.debug("New Items has to be in public-status '"
                + StatusType.PENDING + "' or '" + StatusType.RELEASED);
            item.getProperties().getObjectProperties()
                .setStatus(StatusType.PENDING);
            item.getProperties().getCurrentVersion()
                .setStatus(StatusType.PENDING);
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
        final Iterator<RelationCreate> it = item.getRelations().iterator();
        if (it != null) {
            while (it.hasNext()) {
                final RelationCreate relation = it.next();
                checkRefElement(relation.getTarget());
                if (!ContentRelationsUtility.validPredicate(relation
                    .getPredicateNs() + '#' + relation.getPredicate())) {
                    final String message =
                        "Predicate '" + relation.getPredicate()
                            + "' is invalid. ";
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(message);
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
            final String objid =
                item.getProperties().getObjectProperties().getOriginObjectId();
            final String versionNumber =
                item.getProperties().getObjectProperties().getOriginVersionId();
            // in a case of a floating reference to the origin item
            // ensure, that a latest release and not a latest version
            // of the origin item will be fetched.
            final String publicStatus =
                getTripleStoreUtility().getPropertiesElements(objid,
                    TripleStoreUtility.PROP_PUBLIC_STATUS);
            if (publicStatus == null) {
                final String message =
                    "A referenced Item '" + origin + "' does not exist.";
                LOGGER.error(message);
                throw new InvalidContentException();
            }
            else if (publicStatus.equals(Constants.STATUS_WITHDRAWN)) {
                final String message =
                    "The referenced Item '" + origin
                        + "' is in status 'withdrawn'. The surrogate Item can "
                        + "not be created.";
                LOGGER.error(message);
                throw new InvalidStatusException();
            }

            final String latestReleaseNumber =
                getTripleStoreUtility().getPropertiesElements(objid,
                    TripleStoreUtility.PROP_LATEST_RELEASE_NUMBER);
            if (latestReleaseNumber == null) {

                final String message =
                    "The referenced Item with id '" + origin
                        + "' is not released.";
                LOGGER.error(message);
                throw new InvalidStatusException(message);
            }
            if (versionNumber == null) {
                origin = objid + ':' + latestReleaseNumber;
            }

            if (!checkUserRights(origin)) {
                final String message =
                    "You can not create a surrogate Item based "
                        + "on the Item '" + origin
                        + "' because you have no access "
                        + "rights on this Item.";
                LOGGER.debug(message);
                throw new AuthorizationException(message);
            }
            try {
                setOriginItem(origin);

            }
            catch (ItemNotFoundException e) {
                final String message =
                    "The referenced Item '" + origin + "' does not exist.";
                LOGGER.debug(message);
                throw new InvalidContentException();
            }

            if (getOriginItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN) != null) {
                final String message =
                    "A referenced original Item should be "
                        + "a regular Item, not a surrogate Item.";
                LOGGER.debug(message);
                throw new InvalidContentException(message);
            }
            final String versionStatus =
                getOriginItem().getResourceProperties().get(
                    PropertyMapKeys.CURRENT_VERSION_STATUS);
            if (!versionStatus.equals(Constants.STATUS_RELEASED)) {
                final String message =
                    "The referenced Item version is not released. "
                        + "You can create a surrogate Item only based on a "
                        + "released Item version.";
                LOGGER.debug(message);
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
        final List<MdRecordCreate> mdRecords = item.getMetadataRecords();

        // check if md-record with name 'escidoc'

        if ((mdRecords == null) || mdRecords.size() < 1) {
            if (item.getProperties().getObjectProperties().getOrigin() == null) {
                final String message =
                    "The Item representation doesn't contain a "
                        + "mandatory md-record. A regular Item must contain a "
                        + "mandatory md-record. ";
                LOGGER.error(message);
                throw new MissingMdRecordException(message);
            }

        }
        else {

            final Collection<String> mdRecordNames = new ArrayList<String>();
            for (final MdRecordCreate mdRecord : mdRecords) {

                final String name = mdRecord.getName();

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
                final String message =
                    "The item representation doesn't contain a "
                        + "mandatory md-record. A regular item must contain a "
                        + "mandatory md-record. ";
                LOGGER.error(message);
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

        final String targetObjectType =
            getTripleStoreUtility().getObjectType(targetId);

        if (targetObjectType == null) {
            final String message =
                "Resource with id '" + targetId + "' does not exist.";
            LOGGER.debug(message);
            throw new ReferencedResourceNotFoundException(message);
        }

        if (!de.escidoc.core.common.business.Constants.ITEM_OBJECT_TYPE
            .equals(targetObjectType)
            && !de.escidoc.core.common.business.Constants.CONTAINER_OBJECT_TYPE
                .equals(targetObjectType)) {
            final String message =
                "A related resource '" + targetId
                    + "' is neither 'Item' nor 'Container' ";

            LOGGER.debug(message);
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

        final String originObjectId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        final String originId;

        final String originVersionId =
            getItem().getResourceProperties().get(
                PropertyMapKeys.ORIGIN_VERSION);

        if (originVersionId == null) {
            final String latestReleaseNumber =
                getTripleStoreUtility().getPropertiesElements(originObjectId,
                    Constants.RELEASE_NS_URI + Elements.ELEMENT_NUMBER);
            setOriginId(originObjectId);
            originId = originObjectId + ':' + latestReleaseNumber;
        }
        else {
            originId = originObjectId + ':' + originVersionId;
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

        final List<String> id = new ArrayList<String>();
        id.add(origin);

        final List<String> ids;
        try {
            ids = getPdp().evaluateRetrieve("item", id);
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e);
        }

        return !((ids == null) || ids.isEmpty());

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

        final String originObjectId =
            getItem().getResourceProperties().get(PropertyMapKeys.ORIGIN);
        boolean origin = false;

        if (originObjectId != null) {
            origin = true;
            prepareAndSetOriginItem();
            if (!checkUserRights(getOriginItem().getFullId())) {
                LOGGER.debug(errorMessage);
                throw new AuthorizationException(errorMessage);
            }
        }
        else {
            resetOriginItem();
        }

        return origin;
    }
}
