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
package de.escidoc.core.om.business.fedora.contentRelation;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.LockHandler;
import de.escidoc.core.common.business.fedora.FedoraUtility;
import de.escidoc.core.common.business.fedora.HandlerBase;
import de.escidoc.core.common.business.fedora.Triple;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.fedora.datastream.Datastream;
import de.escidoc.core.common.business.fedora.resources.LockStatus;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.StatusType;
import de.escidoc.core.common.business.fedora.resources.create.ContentRelationCreate;
import de.escidoc.core.common.business.fedora.resources.create.MdRecordCreate;
import de.escidoc.core.common.business.fedora.resources.listener.ResourceListener;
import de.escidoc.core.common.business.filter.SRURequest;
import de.escidoc.core.common.business.filter.SRURequestParameters;
import de.escidoc.core.common.business.indexing.IndexingHandler;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.PidAlreadyAssignedException;
import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.IntegritySystemException;
import de.escidoc.core.common.exceptions.system.PidSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.persistence.PIDSystem;
import de.escidoc.core.common.persistence.PIDSystemFactory;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.stax.handler.RelsExtReadHandler;
import de.escidoc.core.common.util.stax.handler.TaskParamHandler;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ContentRelationXmlProvider;
import de.escidoc.core.om.business.fedora.ContentRelationsUtility;
import de.escidoc.core.om.business.interfaces.ContentRelationHandlerInterface;
import de.escidoc.core.om.business.stax.handler.item.ContentRelationHandler;
import org.joda.time.DateTime;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * ContentRelation handler.
 * 
 * @spring.bean id="business.FedoraContentRelationHandler" scope="prototype"
 * 
 * @author SWA
 * 
 */
public class FedoraContentRelationHandler extends HandlerBase
    implements ContentRelationHandlerInterface {

    private static AppLogger log = new AppLogger(
        FedoraContentRelationHandler.class.getName());

    private final List<ResourceListener> contentRelationListeners =
        new ArrayList<ResourceListener>();

    private PIDSystemFactory pidGenFactory = null;

    private PIDSystem pidGen = null;

    /** SRU request. */
    private SRURequest sruRequest = null;

    /**
     * Create Content Relation.
     * 
     * @param xmlData
     *            XML representation of Content Relation
     * @return XML representation of created Content Relation
     * 
     * @throws MissingMethodParameterException
     *             Thrown if method parameter is missing
     * @throws ReferencedResourceNotFoundException
     *             Thrown if referenced resource does not exist.
     * @throws RelationPredicateNotFoundException
     *             Thrown if the predicate is not registered.
     * @throws InvalidContentException
     *             Thrown if content is invalid
     * @throws InvalidXmlException
     *             Thrown if XML is invalid
     * @throws MissingAttributeValueException
     *             Thrown if attribute value is missing
     * @throws SystemException
     *             Thrown if internal error occur
     */
    public String create(final String xmlData)
        throws MissingAttributeValueException, MissingMethodParameterException,
        InvalidXmlException, InvalidContentException,
        ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, SystemException {

        ContentRelationCreate cr = null;
        cr = parseContentRelation(xmlData);

        // make sure that some values are fixed/ignored
        cr.setObjid(null);
        cr.getProperties().setStatus(StatusType.PENDING);
        cr.getProperties().setPid(null);

        validate(cr);

        cr.setIdProvider(getIdProvider());
        cr.persist(true);

        String resultCR =
            ContentRelationXmlProvider.getInstance().getContentRelationXml(cr);

        fireContentRelationCreated(cr, resultCR);
        return resultCR;
    }

    /**
     * Get escidoc XML representation of ContentRelation.
     * 
     * @param id
     *            objid of ContentRelation resource
     * @return escidoc XML representation of ContentRelation
     * @throws ContentRelationNotFoundException
     *             Thrown if under provided id no ContentRelation could be found
     * @throws SystemException
     *             Thrown if internal error occurs.
     */
    public String retrieve(final String id)
        throws ContentRelationNotFoundException, SystemException {

        ContentRelationCreate cr = setContentRelation(id);
        enrichWithMetadataContent(cr);
        return ContentRelationXmlProvider.getInstance().getContentRelationXml(
            cr);
    }

    /**
     * Retrieves a filtered list of content relations.
     * 
     * @param parameters
     *            parameters from the SRU request
     * 
     * @return Returns XML representation of the list of content relation
     *         objects.
     * @throws SystemException
     *             If case of internal error.
     */
    public String retrieveContentRelations(final SRURequestParameters parameters)
        throws SystemException {
        StringWriter result = new StringWriter();

        if (parameters.isExplain()) {
            sruRequest.explain(result, ResourceType.CONTENT_RELATION);
        }
        else {
            sruRequest.searchRetrieve(result,
                new ResourceType[] { ResourceType.CONTENT_RELATION },
                parameters.getQuery(), parameters.getLimit(), parameters.getOffset(),
                parameters.getUser(), parameters.getRole());
        }
        return result.toString();
    }

    /**
     * Get escidoc XML representation of ContentRelations properties.
     * 
     * @param id
     *            objid of ContentRelation resource
     * @return escidoc XML representation of ContentRelation
     * @throws ContentRelationNotFoundException
     *             Thrown if under provided id no ContentRelation could be found
     * @throws SystemException
     *             Thrown if internal error occurs.
     */
    public String retrieveProperties(final String id)
        throws ContentRelationNotFoundException, SystemException {

        ContentRelationCreate cr = setContentRelation(id);
        return ContentRelationXmlProvider
            .getInstance().getContentRelationPropertiesXml(cr);
    }

    /**
     * Get escidoc XML representation of ContentRelations md-records.
     * 
     * @param id
     *            objid of ContentRelation resource
     * @return escidoc XML representation of ContentRelation
     * @throws ContentRelationNotFoundException
     *             Thrown if under provided id no ContentRelation could be found
     * @throws SystemException
     *             Thrown if internal error occurs.
     */
    public String retrieveMdRecords(final String id)
        throws ContentRelationNotFoundException, SystemException {

        ContentRelationCreate cr = setContentRelation(id);
        enrichWithMetadataContent(cr);
        return ContentRelationXmlProvider
            .getInstance().getContentRelationMdRecords(cr);
    }

    /**
     * Get escidoc XML representation of ContentRelations md-records.
     * 
     * @param id
     *            objid of ContentRelation resource
     * @param name
     *            name of a md-record
     * @return escidoc XML representation of ContentRelation
     * @throws ContentRelationNotFoundException
     *             Thrown if under provided id no ContentRelation could be found
     * @throws MdRecordNotFoundException
     *             e
     * @throws SystemException
     *             Thrown if internal error occurs.
     */
    public String retrieveMdRecord(final String id, final String name)
        throws ContentRelationNotFoundException, MdRecordNotFoundException,
        SystemException {

        ContentRelationCreate cr = setContentRelation(id);
        enrichWithMetadataContent(cr);
        List<MdRecordCreate> mdRecords = cr.getMetadataRecords();
        if (mdRecords != null) {
            for (MdRecordCreate mr : mdRecords) {
                if (mr.getName().equals((name))) {
                    return ContentRelationXmlProvider
                        .getInstance().getContentRelationMdRecord(cr, mr);
                }
            }
        }
        throw new MdRecordNotFoundException("The md-record with name=" + name
            + " does not exist.");
    }

    /**
     * Update Content Relation.
     * 
     * @param id
     *            objid of Content Relation
     * @param xmlData
     *            XML representation of Content Relation
     * @return XML representation of updated Content Relation
     * 
     * @throws ContentRelationNotFoundException
     *             Thrown if no Content Relation could be found under provided
     *             objid
     * @throws OptimisticLockingException
     *             Thrown if resource is updated in the meantime and last
     *             modification date differs
     * @throws InvalidStatusException
     *             Thrown if resource has invalid status to update
     * @throws MissingAttributeValueException
     *             Thrown if attribute value is missing
     * @throws LockingException
     *             Thrown if resource is locked through other user
     * @throws MissingMethodParameterException
     *             Thrown if method parameter is missing
     * @throws ReferencedResourceNotFoundException
     *             Thrown if referenced resource does not exist.
     * @throws RelationPredicateNotFoundException
     *             Thrown if the predicate is not registered.
     * @throws InvalidContentException
     *             Thrown if content is invalid
     * @throws InvalidXmlException
     *             Thrown if XML is invalid
     * @throws MissingAttributeValueException
     *             Thrown if attribute value is missing
     * @throws SystemException
     *             Thrown if internal error occur
     */
    public String update(final String id, final String xmlData)
        throws ContentRelationNotFoundException, OptimisticLockingException,
        InvalidContentException, InvalidStatusException, LockingException,
        MissingAttributeValueException, MissingMethodParameterException,
        SystemException, InvalidXmlException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException {

        boolean resourceChanged = false;
        String result = null;

        // instance of stored Content Relation
        ContentRelationCreate cr = setContentRelation(id);
        enrichWithMetadataContent(cr);

        checkLocked(cr);
        checkReleased(cr);

        // instance of the update representation
        ContentRelationCreate updatedCR = parseContentRelation(xmlData);
        validate(updatedCR);

        // now compare this.contentRelation with updatedCR and transfer data
        if (cr.merge(updatedCR) > 0) {
            cr.persist();
            resourceChanged = true;
        }
        result =
            ContentRelationXmlProvider.getInstance().getContentRelationXml(cr);

        if (resourceChanged) {
            fireContentRelationModified(cr, result);
        }

        return result;
    }

    /**
     * Delete Content Relation.
     * 
     * Even if Content Relations have life cycle with release etc. it should be
     * possible to remove. This rule is different to the release status of Item
     * and Container where release means that the resource will be public
     * available anytime and could only be withdrawn. It's assumed that the
     * anytime feature isn't required for Content Relations because they not
     * contain primary data.
     * 
     * @param id
     *            The objid of the Content Relation
     * @throws ContentRelationNotFoundException
     *             Thrown if a content relation with the provided id cannot be
     *             found.
     * @throws LockingException
     *             Thrown if Content Relation is locked by other user
     * @throws SystemException
     *             Thrown if internal error occurs.
     */
    public void delete(final String id)
        throws ContentRelationNotFoundException, SystemException,
        LockingException {

        ContentRelationCreate cr = setContentRelation(id);
        checkLocked(cr);

        getFedoraUtility().deleteObject(cr.getObjid(), true);
        fireContentRelationDeleted(cr);
    }

    /**
     * @param xmlData
     *            content relation as XML
     * 
     * @return ingested content relation as XML
     * @throws EscidocException
     *             e
     */
    public String ingest(final String xmlData) throws EscidocException {

        throw new WebserverSystemException("Missing implementation");
    }

    /**
     * Injects the indexing handler.
     * 
     * @spring.property ref="common.business.indexing.IndexingHandler"
     * @param indexingHandler
     *            The indexing handler.
     */
    public void setIndexingHandler(final IndexingHandler indexingHandler) {
        addContentRelationListener(indexingHandler);
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
     * @param fedoraUtility
     *            Fedora utility
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
     *            id provider
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
     * Submit the Content Relation.
     * 
     * @param id
     *            objid of ContentRelation
     * @param param
     *            Task parameter
     * @return XML result
     * @throws ContentRelationNotFoundException
     *             e
     * @throws LockingException
     *             e
     * @throws InvalidStatusException
     *             e
     * @throws MissingMethodParameterException
     *             e
     * @throws SystemException
     *             e
     * @throws OptimisticLockingException
     *             e
     * @throws InvalidXmlException
     *             e
     * @throws InvalidContentException
     *             e
     */
    public String submit(final String id, final String param)
        throws ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidContentException {

        ContentRelationCreate cr = setContentRelation(id);
        TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);
        checkLocked(cr);

        validateToSubmitStatus(cr);

        // check optimistic locking criteria
        getUtility().checkOptimisticLockingCriteria(
            cr.getProperties().getLastModificationDate(),
            new DateTime(taskParameter.getLastModificationDate()),
            "Content relation " + id);

        cr.getProperties().setStatus(StatusType.SUBMITTED);
        // set status comment
        if (taskParameter.getComment() != null) {
            cr.getProperties().setStatusComment(taskParameter.getComment());
        }
        else {
            cr.getProperties()
                .setStatusComment("Status changed to 'submitted'");
        }

        cr.persistProperties(true);

        // load metadata content to resource
        enrichWithMetadataContent(cr);
        fireContentRelationModified(cr, ContentRelationXmlProvider
            .getInstance().getContentRelationXml(cr));

        return getUtility().prepareReturnXml(
            cr.getProperties().getLastModificationDate(), null);
    }

    /**
     * Register an content relation listener.
     * 
     * @param listener
     *            listener which will be added to the list
     */
    public void addContentRelationListener(final ResourceListener listener) {
        contentRelationListeners.add(listener);
    }

    /**
     * 
     * @param id
     *            objid of ContentRelation
     * @param param
     *            Task parameter
     * @return XML result
     * @throws ContentRelationNotFoundException
     *             e
     * @throws LockingException
     *             e
     * @throws InvalidStatusException
     *             e
     * @throws MissingMethodParameterException
     *             e
     * @throws SystemException
     *             e
     * @throws OptimisticLockingException
     *             e
     * @throws InvalidXmlException
     *             e
     * @throws InvalidContentException
     *             e
     */
    public String release(final String id, final String param)
        throws ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidContentException {

        ContentRelationCreate cr = setContentRelation(id);
        TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);
        checkLocked(cr);
        checkReleased(cr);
        if (!StatusType.SUBMITTED.equals(cr.getProperties().getStatus())) {
            String message =
                "The object is not in state '" + Constants.STATUS_SUBMITTED
                    + "' and can not be " + Constants.STATUS_RELEASED + ".";
            log.debug(message);
            throw new InvalidStatusException(message);
        }
        getUtility().checkOptimisticLockingCriteria(
            cr.getProperties().getLastModificationDate(),
            new DateTime(taskParameter.getLastModificationDate()),
            "Content relation " + id);

        cr.getProperties().setStatus(StatusType.RELEASED);

        // set status comment
        if (taskParameter.getComment() != null) {
            cr.getProperties().setStatusComment(taskParameter.getComment());
        }
        else {
            cr.getProperties().setStatusComment("Status changed to 'released'");
        }

        cr.persistProperties(true);

        // load metadata content to resource
        enrichWithMetadataContent(cr);
        fireContentRelationModified(cr, ContentRelationXmlProvider
            .getInstance().getContentRelationXml(cr));

        return getUtility().prepareReturnXml(
            cr.getProperties().getLastModificationDate(), null);
    }

    /**
     * Revise the Content Relation.
     * 
     * @param id
     *            Objid of Content Relation
     * @param taskParam
     *            TaskParameter
     * @return XML result
     * @throws ContentRelationNotFoundException
     *             e
     * @throws LockingException
     *             e
     * @throws InvalidStatusException
     *             e
     * @throws MissingMethodParameterException
     *             e
     * @throws SystemException
     *             e
     * @throws OptimisticLockingException
     *             e
     * @throws XmlCorruptedException
     *             e
     * @throws InvalidContentException
     *             e
     */
    public String revise(final String id, final String taskParam)
        throws ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, XmlCorruptedException,
        InvalidContentException {

        ContentRelationCreate cr = setContentRelation(id);
        TaskParamHandler taskParameter = XmlUtility.parseTaskParam(taskParam);
        checkLocked(cr);
        if (!StatusType.SUBMITTED.equals(cr.getProperties().getStatus())) {
            String message =
                "The object is not in state '" + Constants.STATUS_SUBMITTED
                    + "' and can not be revised.";
            log.debug(message);
            throw new InvalidStatusException(message);
        }
        getUtility().checkOptimisticLockingCriteria(
            cr.getProperties().getLastModificationDate(),
            new DateTime(taskParameter.getLastModificationDate()),
            "Content relation " + id);

        cr.getProperties().setStatus(StatusType.INREVISION);
        // set status comment
        if (taskParameter.getComment() != null) {
            cr.getProperties().setStatusComment(taskParameter.getComment());
        }
        else {
            cr.getProperties().setStatusComment(
                "Status changed to 'in-revision'");
        }

        cr.persistProperties(true);

        // load metadata content to resource
        enrichWithMetadataContent(cr);
        fireContentRelationModified(cr, ContentRelationXmlProvider
            .getInstance().getContentRelationXml(cr));

        return getUtility().prepareReturnXml(
            cr.getProperties().getLastModificationDate(), null);
    }

    /**
     * Lock a Content Relation for other user access.
     * 
     * @param id
     *            Objid of Content Relation
     * @param param
     *            XML TaskParam
     * @return Result XML data structure
     * @throws ContentRelationNotFoundException
     *             e
     * @throws LockingException
     *             e
     * @throws InvalidContentException
     *             e
     * @throws MissingMethodParameterException
     *             e
     * @throws SystemException
     *             e
     * @throws OptimisticLockingException
     *             e
     * @throws InvalidXmlException
     *             e
     * @throws InvalidStatusException
     *             e
     */
    public String lock(final String id, final String param)
        throws ContentRelationNotFoundException, LockingException,
        InvalidContentException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidStatusException {

        ContentRelationCreate cr = setContentRelation(id);
        checkReleased(cr);
        checkLocked(cr);

        TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);
        getUtility().checkOptimisticLockingCriteria(
            cr.getProperties().getLastModificationDate(),
            new DateTime(taskParameter.getLastModificationDate()),
            "Content relation " + id);

        // lock resource
        LockHandler lockHandler = LockHandler.getInstance();
        if (!cr.getProperties().isLocked()) {
            lockHandler.lock(cr.getObjid(), getUtility().getCurrentUser());

            cr.getProperties().setLockStatus(LockStatus.LOCKED);
            cr.getProperties().setLockOwnerId(getUtility().getCurrentUserId());
            cr.getProperties().setLockOwnerName(
                getUtility().getCurrentUserRealName());
            cr.getProperties().setLockDate(
                cr.getProperties().getLastModificationDate());
        }

        fireContentRelationModified(cr, ContentRelationXmlProvider
            .getInstance().getContentRelationXml(cr));

        // timestamp
        return getUtility().prepareReturnXml(
            cr.getProperties().getLastModificationDate(), null);
    }

    /**
     * Unlock a Content Relation for other user access.
     * 
     * @param id
     *            Objid of Content Relation
     * @param param
     *            XML TaskParam
     * @return Result XML data structure
     * @throws ContentRelationNotFoundException
     *             e
     * @throws LockingException
     *             e
     * @throws InvalidContentException
     *             e
     * @throws MissingMethodParameterException
     *             e
     * @throws SystemException
     *             e
     * @throws OptimisticLockingException
     *             e
     * @throws InvalidXmlException
     *             e
     * @throws InvalidStatusException
     *             Thrown if resource is not locked.
     */
    public String unlock(final String id, final String param)
        throws ContentRelationNotFoundException, LockingException,
        MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidXmlException,
        InvalidContentException, InvalidStatusException {

        ContentRelationCreate cr = setContentRelation(id);
        TaskParamHandler taskParameter = XmlUtility.parseTaskParam(param);

        getUtility().checkOptimisticLockingCriteria(
            cr.getProperties().getLastModificationDate(),
            new DateTime(taskParameter.getLastModificationDate()),
            "Content relation " + id);

        // lock resource
        LockHandler lockHandler = LockHandler.getInstance();
        if (cr.getProperties().isLocked()) {
            lockHandler.unlock(cr.getObjid());

            cr.getProperties().setLockStatus(LockStatus.UNLOCKED);
            cr.getProperties().setLockOwnerId(null);
            cr.getProperties().setLockOwnerId(null);
            cr.getProperties().setLockDate((DateTime) null);
        }

        fireContentRelationModified(cr, ContentRelationXmlProvider
            .getInstance().getContentRelationXml(cr));

        return getUtility().prepareReturnXml(
            cr.getProperties().getLastModificationDate(), null);
    }

    /**
     * Assign persistent identifier to a Content-relation object.
     * 
     * @param id
     *            The Id of the Content-relation witch is to assign with an
     *            ObjectPid.
     * @param taskParam
     *            XML snippet with parameter for the persistent identifier
     *            system.
     * @return The assigned persistent identifier for the Content-relation.
     * 
     * @throws ContentRelationNotFoundException
     *             Thrown if the object with id is does not exist or is no Item.
     * @throws LockingException
     *             Thrown if the Item is locked
     * @throws MissingMethodParameterException
     *             Thrown if a parameter is missing within
     *             <code>taskParam</code>.
     * @throws OptimisticLockingException
     *             Thrown if Item was altered in the mean time.
     * @throws PidAlreadyAssignedException
     *             Thrown if a Content-relation is already assigned a PID.
     * @throws InvalidXmlException
     *             Thrown if taskParam has invalid XML.
     * @throws SystemException
     *             Thrown in case of internal error.
     * @see de.escidoc.core.om.business.interfaces.ContentRelationsHandlerInterface
     *      #assignObjectPid(java.lang.String,java.lang.String)
     */
    public String assignObjectPid(final String id, final String taskParam)
        throws ContentRelationNotFoundException, LockingException,
        MissingMethodParameterException, OptimisticLockingException,
        InvalidXmlException, SystemException, PidAlreadyAssignedException {

        ContentRelationCreate cr = setContentRelation(id);
        if (cr.getProperties().getPid() != null) {
            String message =
                "A content relation with id " + id
                    + " is already assigned a PID";
            log.debug(message);
            throw new PidAlreadyAssignedException(message);
        }
        final TaskParamHandler taskParameter =
            XmlUtility.parseTaskParam(taskParam);
        checkLocked(cr);
        getUtility().checkOptimisticLockingCriteria(
            cr.getProperties().getLastModificationDate(),
            new DateTime(taskParameter.getLastModificationDate()),
            "Content-relation " + cr.getObjid());

        String pid = taskParameter.getPid();
        if (pid == null) {
            // get PID from external PID System
            pid = getPid(id, taskParam);
        }
        // else if (!cr.validPidStructure(pid)) {
        // throw new InvalidXmlException("Empty pid element of taskParam.");
        // }
        cr.getProperties().setPid(pid);
        // updateRelsExt();
        cr.persist();
        return (prepareResponse(cr, pid));
    }

    /**
     * Retrieves a list of registered predicates which can be used to create
     * content relations.
     * 
     * @return String containing a list with registered predicates.
     * @throws InvalidContentException
     *             Thrown if a xml file with an ontology has invalid content
     * @throws InvalidXmlException
     *             Thrown if a xml file with an ontology is invalid rdf/xml
     * @throws SystemException
     *             e
     */
    public String retrieveRegisteredPredicates()
        throws InvalidContentException, InvalidXmlException, SystemException {
        List<String> predicates = ContentRelationsUtility.getPredicates();
        Iterator<String> it = predicates.iterator();
        StringBuffer sb = new StringBuffer();
        sb.append("<predicates>");
        while (it.hasNext()) {
            sb.append(it.next());
            sb.append("\n");

        }
        sb.append("</predicates>");
        return sb.toString();
    }

    /**
     * Get Persistent Identifier from configured PID (Manager) service.
     * 
     * @param id
     *            Item ID
     * @param param
     *            XML snippet with PID Manager parameter.
     * @return Persistent Identifier
     * 
     * @throws PidSystemException
     *             Thrown if the communication with PID (Management) System
     *             fails.
     * @throws MissingMethodParameterException
     *             Thrown if necessary parameters are not part of the param XML
     *             structure.
     * @throws WebserverSystemException
     *             Thrown by assignPid().
     */
    public String getPid(final String id, final String param)
        throws PidSystemException, MissingMethodParameterException,
        WebserverSystemException {

        if (this.pidGenFactory == null) {
            pidGenFactory = PIDSystemFactory.getInstance();
        }
        if (this.pidGen == null) {
            pidGen = pidGenFactory.getPIDGenerator();
        }

        return (pidGen.assignPID(id, param));
    }

    /**
     * Retrieve virtual resources.
     * 
     * @param id
     *            objid of Content Relation
     * @return XML representation of resources
     * @throws ContentRelationNotFoundException
     * @throws SystemException
     */
    public String retrieveResources(final String id)
        throws ContentRelationNotFoundException, SystemException {

        ContentRelationCreate cr = setContentRelation(id);
        return ContentRelationXmlProvider
            .getInstance().getContentRelationResourcesXml(cr);
    }

    /**
     * Bounds a Content Relation object to this handler.
     * 
     * @param id
     *            The ID of the Content Relation which should be bound to this
     *            Handler.
     * @return value object of Content Relation with provided objid
     * @throws ContentRelationNotFoundException
     *             If there is no item with <code>id</code> in the repository.
     * @throws SystemException
     *             Thrown in case of an internal system error.
     */
    protected ContentRelationCreate setContentRelation(final String id)
        throws ContentRelationNotFoundException, SystemException {

        ContentRelationCreate cr = new ContentRelationCreate();
        cr.setObjid(id);

        cr.getProperties().setCreationDate(getCreationDate(cr.getObjid()));

        // parse RELS-EXT and instantiate ContentRelationCreate
        setRelsExtValues(cr);
        setMetadata(cr);

        // set further values (obtained from other sources)
        LockHandler lockHandler = LockHandler.getInstance();
        if (lockHandler.isLocked(id)) {
            try {
                cr.getProperties().setLockStatus(LockStatus.LOCKED);
            }
            catch (InvalidStatusException e) {
                // shouldn't happen
                throw new SystemException(e);
            }
            cr.getProperties().setLockDate(lockHandler.getLockDate(id));
            cr.getProperties().setLockOwnerId(lockHandler.getLockOwner(id));
            cr.getProperties().setLockOwnerName(
                lockHandler.getLockOwnerTitle(id));
        }

        // be aware some of them are triple store requests, which are expensive
        cr.getProperties().setLastModificationDate(
            getFedoraUtility().getLastModificationDate(id));

        return cr;
    }

    /**
     * Check if the Content Relation is locked.
     * 
     * @param cr
     *            Content Relation
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @throws LockingException
     *             If the Content is locked and the current user is not the one
     *             who locked it.
     */

    private void checkLocked(final ContentRelationCreate cr)
        throws LockingException, WebserverSystemException {

        if (cr.getProperties().isLocked()
            && !cr
                .getProperties().getLockOwnerId()
                .equals(getUtility().getCurrentUserId())) {

            String message =
                "Content Relation + " + cr.getObjid() + " is locked by "
                    + cr.getProperties().getLockOwnerId() + " ("
                    + cr.getProperties().getLockOwnerName() + ") .";
            log.debug(message);
            throw new LockingException(message);
        }
    }

    /**
     * Check release status of object.
     * 
     * @param cr
     *            ContentRelation
     * @throws InvalidStatusException
     *             Thrown if object is not in status released.
     */
    private void checkReleased(final ContentRelationCreate cr)
        throws InvalidStatusException {

        StatusType status = cr.getProperties().getStatus();
        if (status.equals(StatusType.RELEASED)) {
            final String msg =
                "The object is in state '" + Constants.STATUS_RELEASED
                    + "' and can not be" + " changed.";
            log.debug(msg);
            throw new InvalidStatusException(msg);
        }
    }

    /**
     * Retrieve the properties of the last version (RELS-EXT) and inject values
     * into ContentRelationCreate object.
     * 
     * @param cr
     *            ContentRelation object
     * @throws SystemException
     *             Thrown in case of internal failure.
     * @throws ContentRelationNotFoundException
     *             Thrown if resource with provided id could not be found in
     *             Fedora repository.
     */
    private void setRelsExtValues(final ContentRelationCreate cr)
        throws SystemException, ContentRelationNotFoundException {

        // retrieve resource with id from Fedora
        Datastream relsExt;
        try {
            relsExt =
                new Datastream(Datastream.RELS_EXT_DATASTREAM, cr.getObjid(),
                    null);
        }
        catch (StreamNotFoundException e) {
            throw new ContentRelationNotFoundException(
                "Content Relation with id '" + cr.getObjid()
                    + "' could not be found.", e);
        }

        final StaxParser sp = new StaxParser();

        final RelsExtReadHandler eve = new RelsExtReadHandler(sp);
        eve.cleanIdentifier(true);
        sp.addHandler(eve);
        try {
            sp.parse(relsExt.getStream());
        }
        catch (final Exception e) {
            throw new WebserverSystemException(e);
        }

        List<Triple> triples = eve.getElementValues().getTriples();

        // write triple values into ContentRelation object
        Iterator<Triple> it = triples.iterator();

        while (it.hasNext()) {
            Triple t = it.next();
            if (t
                .getPredicate().equals(TripleStoreUtility.PROP_FRAMEWORK_BUILD)) {
                cr.setBuildNumber(t.getObject());
            }
            // creator --------------
            else if (t.getPredicate().equals(
                TripleStoreUtility.PROP_CREATED_BY_ID)) {
                cr.getProperties().setCreatedById(t.getObject());
            }
            else if (t.getPredicate().equals(
                TripleStoreUtility.PROP_CREATED_BY_TITLE)) {
                cr.getProperties().setCreatedByName(t.getObject());
            }
            // modifier --------------
            else if (t.getPredicate().equals(
                TripleStoreUtility.PROP_MODIFIED_BY_ID)) {
                cr.getProperties().setModifiedById(t.getObject());
            }
            else if (t.getPredicate().equals(
                TripleStoreUtility.PROP_MODIFIED_BY_TITLE)) {
                cr.getProperties().setModifiedByName(t.getObject());
            }
            // public-status --------------
            else if (t.getPredicate().equals(
                TripleStoreUtility.PROP_PUBLIC_STATUS)) {

                StatusType st;
                try {
                    st = StatusType.getStatusType(t.getObject());
                }
                catch (InvalidStatusException e) {
                    // shouldn't happen
                    log.info("Invalid status: " + e);
                    throw new SystemException(e);
                }
                cr.getProperties().setStatus(st);
            }
            else if (t.getPredicate().equals(
                TripleStoreUtility.PROP_PUBLIC_STATUS_COMMENT)) {
                cr.getProperties().setStatusComment(t.getObject());
            }
            else if (t.getPredicate().equals(
                TripleStoreUtility.PROP_OBJECT_TYPE)) {
                // this is not the ContentRelation type, this is the type of
                // resource
                if (!(Constants.CONTENT_RELATION2_OBJECT_TYPE.equals(t
                    .getObject()) || (Constants.RDF_NAMESPACE_URI + "Statement")
                    .equals(t.getObject()))) {
                    throw new WebserverSystemException(
                        "Resource is not from type ContentRelation.");
                }
            }
            else if (t.getPredicate().equals(
                TripleStoreUtility.PROP_CONTENT_RELATION_SUBJECT)) {
                cr.setSubject(t.getObject());
            }
            else if (t.getPredicate().equals(
                TripleStoreUtility.PROP_CONTENT_RELATION_OBJECT)) {
                cr.setObject(t.getObject());
            }
            else if (t.getPredicate().equals(
                TripleStoreUtility.PROP_CONTENT_RELATION_DESCRIPTION)) {
                cr.getProperties().setDescription(t.getObject());
            }
            else if (t.getPredicate().equals(
                TripleStoreUtility.PROP_CONTENT_RELATION_TYPE)) {
                try {
                    cr.setType(new URI(t.getObject()));
                }
                catch (URISyntaxException e) {
                    // shouldn't happen
                    log.warn("Stored value for URI in invalid: " + e);
                    throw new SystemException(e);
                }
            }
            else if (t.getPredicate().equals(
                TripleStoreUtility.PROP_CONTENT_RELATION_OBJECT_VERSION)) {
                cr.setObjectVersion(t.getObject());
            }
            else if (t.getPredicate().equals(
                TripleStoreUtility.PROP_CONTENT_RELATION_SUBJECT)) {
                cr.setSubjectVersion(t.getObject());
            }
            else {
                // add values for mapping
                log.warn("Predicate not mapped " + t.getPredicate() + " = "
                    + t.getObject());
            }
        }
    }

    /**
     * Get creation date of Content Relation.
     * 
     * @param objid
     *            objid of Content Relation
     * @return creation date
     * @throws ContentRelationNotFoundException
     *             Thrown if creation date could not be found (indicates, that
     *             resource not exists)
     * @throws WebserverSystemException
     *             Thrown if internal failure occurs.
     * @throws TripleStoreSystemException
     *             Thrown if triple store request failed.
     */
    private String getCreationDate(final String objid)
        throws ContentRelationNotFoundException, WebserverSystemException,
        TripleStoreSystemException {

        String date = null;
        try {
            date = getTripleStoreUtility().getCreationDate(objid);
        }
        catch (TripleStoreSystemException e) {

            if (e.getMessage().contains("Creation date not found")) {
                throw new ContentRelationNotFoundException(
                    "Content Relation with objid '" + objid
                        + "' does not exist.", e);
            }
            else {
                throw e;
            }
        }
        return date;
    }

    /**
     * 
     * @param cr
     *            ContentRelation
     * @throws FedoraSystemException
     *             Thrown if access to Fedora failed.
     * @throws IntegritySystemException
     *             Thrown if data integrity is violated.
     */
    private void setMetadata(final ContentRelationCreate cr)
        throws FedoraSystemException, IntegritySystemException {

        org.fcrepo.server.types.gen.Datastream[] datastreamInfos =
            getFedoraUtility().getDatastreamsInformation(cr.getObjid(), null);

        for (int i = 0; i < datastreamInfos.length; i++) {

            // add meta data
            if (contains(datastreamInfos[i].getAltIDs(),
                Datastream.METADATA_ALTERNATE_ID) > -1) {

                // check if status of stream is not deleted
                if (!datastreamInfos[i].getState().equals(
                    FedoraUtility.DATASTREAM_STATUS_DELETED)) {

                    MdRecordCreate mdRecord = new MdRecordCreate();

                    try {
                        mdRecord.setName(datastreamInfos[i].getID());
                        cr.addMdRecord(mdRecord);
                    }
                    catch (InvalidContentException e) {
                        throw new IntegritySystemException(e);
                    }

                    mdRecord.setLabel(datastreamInfos[i].getLabel());
                    mdRecord.setChecksum(datastreamInfos[i].getChecksum());
                    // TODO checksum enabled missing
                    mdRecord.setMimeType(datastreamInfos[i].getMIMEType());
                    mdRecord.setControlGroup(datastreamInfos[i]
                        .getControlGroup().getValue());
                    mdRecord.setDatastreamLocation(datastreamInfos[i]
                        .getLocation());
                    mdRecord.getRepositoryIndicator().setResourceIsNew(false);

                    // alternate ids
                    mdRecord.setType(datastreamInfos[i].getAltIDs()[1]);
                    mdRecord.setSchema(datastreamInfos[i].getAltIDs()[2]);
                }
            }
        }
    }

    /**
     * Check if a value is in an array of Strings.
     * 
     * @param array
     *            The array of Strings.
     * @param value
     *            The value which is to check.
     * @return the int value of the position in the array and -1 if the values
     *         is not in the array
     */
    private int contains(final String[] array, final String value) {

        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Check if relation type is registered predicate.
     * 
     * @param predicate
     *            the predicate as URI
     * @throws InvalidContentException
     *             Thrown if predicate is invalid
     * @throws WebserverSystemException
     *             Thrown if internal error occur
     * @throws RelationPredicateNotFoundException
     *             Thrown if the predicate is not registered.
     */
    private void checkRelationType(final URI predicate)
        throws InvalidContentException, WebserverSystemException,
        RelationPredicateNotFoundException {
        if (!ContentRelationsUtility.validPredicate(predicate)) {
            String message =
                "Predicate " + predicate
                    + " is not on the registered predicate list. ";
            log.debug(message);
            throw new RelationPredicateNotFoundException(message);
        }

    }

    /**
     * Obtain values from XML an create value object ContentRelation.
     * 
     * @param xml
     *            The content relation XML (validated by schema).
     * @return ContentRelation
     * 
     * @throws InvalidContentException
     *             Thrown if content is invalid
     * @throws InvalidXmlException
     *             Thrown if XML is invalid
     * @throws MissingAttributeValueException
     *             Thrown if attribute value is missing
     * @throws SystemException
     *             Thrown if internal error occur
     */
    private ContentRelationCreate parseContentRelation(final String xml)
        throws MissingAttributeValueException, InvalidXmlException,
        InvalidContentException, SystemException {

        StaxParser sp = new StaxParser();

        ContentRelationHandler contentRelationHandler =
            new ContentRelationHandler(sp);
        sp.addHandler(contentRelationHandler);

        try {
            sp.parse(xml);
        }
        catch (InvalidContentException e) {
            throw new InvalidContentException(e.getMessage(), e);
        }
        catch (RelationPredicateNotFoundException e) {
            // shouldn't happen
            throw new SystemException(e);
        }
        catch (XmlCorruptedException e) {
            throw new XmlCorruptedException(e.getMessage(), e);
        }
        catch (MissingAttributeValueException e) {
            throw new MissingAttributeValueException(e.getMessage(), e);
        }
        catch (InvalidStatusException e) {
            // shouldn't happen
            throw new SystemException(e);
        }
        catch (SystemException e) {
            throw new SystemException(null, e);
        }
        catch (Exception e) {
            XmlUtility.handleUnexpectedStaxParserException(null, e);
        }
        return contentRelationHandler.getContentRelation();
    }

    /**
     * Enrich all meta data records with the (blob) content from repository.
     * 
     * @param cr
     *            ContentRelation
     * @throws WebserverSystemException
     *             Thrown if access to repository (Feodra) failed.
     */
    private void enrichWithMetadataContent(final ContentRelationCreate cr)
        throws WebserverSystemException {

        List<MdRecordCreate> mdRecords = cr.getMetadataRecords();
        if (mdRecords != null) {
            Iterator<MdRecordCreate> it = mdRecords.iterator();
            while (it.hasNext()) {
                MdRecordCreate mdRecord = it.next();

                if (mdRecord.getContent() == null) {

                    Datastream ds =
                        new Datastream(mdRecord.getName(), cr.getObjid(),
                            mdRecord.getMimeType(),
                            mdRecord.getDatastreamLocation(),
                            mdRecord.getControlGroup(), cr
                                .getProperties().getVersionDate());
                    mdRecord.setContent(new String(ds.getStream()));
                }
            }

        }
    }

    /**
     * Validate Content Relation.
     * 
     * Checks if the required values are given and references valid.
     * 
     * @param cr
     *            ContentRelation
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore access failed.
     * @throws WebserverSystemException
     *             If internal error occur
     * @throws ReferencedResourceNotFoundException
     *             Thrown if referenced resource does not exist.
     * @throws RelationPredicateNotFoundException
     *             Thrown if the predicate is not registered.
     * @throws InvalidContentException
     *             Thrown if predicate is invalid
     */
    private void validate(final ContentRelationCreate cr)
        throws TripleStoreSystemException, WebserverSystemException,
        ReferencedResourceNotFoundException, InvalidContentException,
        RelationPredicateNotFoundException {

        validateReference(cr.getSubject());
        validateReference(cr.getObject());
        checkRelationType(cr.getType());
    }

    /**
     * Check if referenced resources exists.
     * 
     * @param reference
     *            reference to be checked
     * @throws TripleStoreSystemException
     *             Thrown if TripleStore access failed.
     * @throws WebserverSystemException
     *             If internal error occur
     * @throws ReferencedResourceNotFoundException
     *             Thrown if referenced resource does not exist.
     */
    private void validateReference(final String reference)
        throws TripleStoreSystemException, WebserverSystemException,
        ReferencedResourceNotFoundException {

        if (!getTripleStoreUtility().exists(reference)) {
            throw new ReferencedResourceNotFoundException(
                "The referenced resource with objid=" + reference
                    + " does not exist.");
        }

    }

    /**
     * Checks if the ContentRelation has right status to change status to
     * submitted.
     * 
     * @param cr
     *            ContentRelation which is to check.
     * @throws InvalidStatusException
     *             Thrown if the current status of the ContentRelation is
     *             invalid to change to submitted.
     */
    private void validateToSubmitStatus(final ContentRelationCreate cr)
        throws InvalidStatusException {

        /*
         * Resource has to have status pending or in-revision when submit is
         * possible.
         */
        if (!(StatusType.PENDING.equals(cr.getProperties().getStatus()) || StatusType.INREVISION
            .equals(cr.getProperties().getStatus()))) {
            String message =
                "The object is not in state '" + Constants.STATUS_PENDING
                    + "' or '" + Constants.STATUS_IN_REVISION
                    + "' and can not be" + " submitted.";
            log.debug(message);
            throw new InvalidStatusException(message);
        }

    }

    /**
     * Notify the listeners that a content relation was modified.
     * 
     * @param cr
     *            Content Relation
     * @param xmlData
     *            complete content relation XML
     * 
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    private void fireContentRelationModified(
        final ContentRelationCreate cr, final String xmlData)
        throws SystemException {
        String restXml = null;
        String soapXml = null;

        if (UserContext.isRestAccess()) {
            restXml = xmlData;
            soapXml = getAlternateForm(cr);
        }
        else {
            restXml = getAlternateForm(cr);
            soapXml = xmlData;
        }
        for (int index = 0; index < contentRelationListeners.size(); index++) {
            (contentRelationListeners.get(index)).resourceModified(
                cr.getObjid(), restXml, soapXml);
        }
    }

    /**
     * Notify the listeners that an content relation was created.
     * 
     * @param cr
     *            content relation
     * @param xmlData
     *            complete content relation XML
     * 
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    private void fireContentRelationCreated(
        final ContentRelationCreate cr, final String xmlData)
        throws SystemException {
        String restXml = null;
        String soapXml = null;

        if (UserContext.isRestAccess()) {
            restXml = xmlData;
            soapXml = getAlternateForm(cr);
        }
        else {
            restXml = getAlternateForm(cr);
            soapXml = xmlData;
        }
        for (int index = 0; index < contentRelationListeners.size(); index++) {
            (contentRelationListeners.get(index)).resourceCreated(
                cr.getObjid(), restXml, soapXml);
        }
    }

    /**
     * Notify the listeners that an content relation was deleted.
     * 
     * @param cr
     *            content relation
     * @throws SystemException
     *             One of the listeners threw an exception.
     */
    private void fireContentRelationDeleted(final ContentRelationCreate cr)
        throws SystemException {
        for (int index = 0; index < contentRelationListeners.size(); index++) {
            (contentRelationListeners.get(index))
                .resourceDeleted(cr.getObjid());
        }
    }

    /**
     * Prepare the assignment response message.
     * 
     * Preconditions: The TripleStore must be in sync with the repository.
     * 
     * @param cr
     *            content relation
     * @param pid
     *            The new assigned PID.
     * @return response message
     * 
     * @throws WebserverSystemException
     *             Thrown in case of internal error.
     * @throws TripleStoreSystemException
     *             Thrown in case of TripleStore error.
     * 
     */
    private String prepareResponse(
        final ContentRelationCreate cr, final String pid)
        throws TripleStoreSystemException, WebserverSystemException {

        String result = null;
        try {
            result =
                getUtility().prepareReturnXml(
                    cr.getProperties().getLastModificationDate(),
                    "<pid>" + pid + "</pid>\n");
        }
        catch (SystemException e) {
            log.error(e);
            throw new WebserverSystemException(e);
        }
        return result;
    }

    /**
     * Get the alternate form of a content relation representation. If the
     * current request came in via REST, then the SOAP form will be returned
     * here and vice versa.
     * 
     * @param cr
     *            content relation
     * @return alternate form of the content relation
     * 
     * @throws SystemException
     *             An internal error occurred.
     */
    private String getAlternateForm(final ContentRelationCreate cr)
        throws SystemException {
        String result = null;
        boolean isRestAccess = UserContext.isRestAccess();

        try {
            if (isRestAccess) {
                UserContext.setRestAccess(false);
                result =
                    ContentRelationXmlProvider
                        .getInstance().getContentRelationXml(cr);
            }
            else {
                UserContext.setRestAccess(true);
                result =
                    ContentRelationXmlProvider
                        .getInstance().getContentRelationXml(cr);
            }
        }
        catch (WebserverSystemException e) {
            throw new SystemException(e);
        }
        catch (Exception e) {
            // should not happen here
            throw new SystemException(e);
        }
        finally {
            UserContext.setRestAccess(isRestAccess);
        }
        return result;
    }

}
