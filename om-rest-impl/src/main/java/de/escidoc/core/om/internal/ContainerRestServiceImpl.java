/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
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
package de.escidoc.core.om.internal;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidItemStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingLicenceException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.XmlSchemaNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyDeletedException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyPublishedException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyWithdrawnException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.NotPublishedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHasChildrenException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHierarchyViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.om.ContainerRestService;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.RequestType;
import org.escidoc.core.domain.sru.ResponseType;
import org.escidoc.core.domain.sru.parameters.SruRequestTypeFactory;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.escidoc.core.utils.io.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import org.escidoc.core.domain.ResultTO;
import org.escidoc.core.domain.container.ContainerTO;
import org.escidoc.core.domain.container.ContainerPropertiesTO;
import org.escidoc.core.domain.item.ItemTO;
import org.escidoc.core.domain.item.ItemPropertiesTO;
import org.escidoc.core.domain.components.ComponentTO;
import org.escidoc.core.domain.components.ComponentsTO;
import org.escidoc.core.domain.components.ComponentPropertiesTO;
import org.escidoc.core.domain.metadatarecords.MdRecordTO;
import org.escidoc.core.domain.metadatarecords.MdRecordsTO;
import org.escidoc.core.domain.version.VersionHistoryTO;
import org.escidoc.core.domain.ou.ParentsTO;
import org.escidoc.core.domain.relations.RelationsTO;
import org.escidoc.core.domain.taskparam.StatusTaskParamTO;
import org.escidoc.core.domain.taskparam.OptimisticLockingTaskParamTO;
import org.escidoc.core.domain.taskparam.AssignPidTaskParamTO;
import org.escidoc.core.domain.taskparam.RelationTaskParamTO;
import org.escidoc.core.domain.taskparam.MembersTaskParamTO;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;

/**
 * REST Service Implementation for Container.
 * 
 * @author SWA
 * 
 */
@Service
public class ContainerRestServiceImpl implements ContainerRestService {

    private final static Logger LOG = LoggerFactory.getLogger(ContainerRestServiceImpl.class);

    @Autowired
    @Qualifier("service.ContainerHandler")
    private ContainerHandlerInterface containerHandler;

    private JAXBContext jaxbContext;

    protected ContainerRestServiceImpl() {
        try {
            this.jaxbContext = JAXBContext.newInstance(ContainerTO.class);
        }
        catch (JAXBException e) {
            LOG.error("Error on initialising JAXB context.", e);
        }
    }

    @Override
    public ContainerTO create(final ContainerTO containerTO) throws ContextNotFoundException,
        ContentModelNotFoundException, InvalidContentException, MissingMethodParameterException,
        MissingAttributeValueException, MissingElementValueException, SystemException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AuthenticationException,
        AuthorizationException, InvalidStatusException, MissingMdRecordException, XmlCorruptedException,
        XmlSchemaValidationException {

        return ServiceUtility.fromXML(ContainerTO.class,
            this.containerHandler.create(ServiceUtility.toXML(containerTO)));
    }

    @Override
    public void delete(String id) throws ContainerNotFoundException, LockingException, InvalidStatusException,
        SystemException, MissingMethodParameterException, AuthenticationException, AuthorizationException {

        this.containerHandler.delete(id);
    }

    @Override
    public ContainerTO retrieve(String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ContainerNotFoundException, SystemException {

        return ServiceUtility.fromXML(ContainerTO.class, this.containerHandler.retrieve(id));
    }

    @Override
    public ContainerTO update(String id, ContainerTO containerTO) throws ContainerNotFoundException, LockingException,
        InvalidContentException, MissingMethodParameterException, InvalidXmlException, OptimisticLockingException,
        InvalidStatusException, ReadonlyVersionException, SystemException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AuthenticationException, AuthorizationException,
        MissingAttributeValueException, MissingMdRecordException {

        return ServiceUtility.fromXML(ContainerTO.class,
            this.containerHandler.update(id, ServiceUtility.toXML(containerTO)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.context.ContainerRestService#retrieveMembers(SruSearchRequestParametersBean, java.util.String, java.util.String, java.util.String)
     */
    @Override
    public JAXBElement<? extends ResponseType> retrieveMembers(final String containerId,
        final SruSearchRequestParametersBean parameters, final String roleId, final String userId,
        final String omitHighlighting) throws InvalidSearchQueryException,
        MissingMethodParameterException, ContainerNotFoundException, SystemException {

        final List<String> additionalParams = new LinkedList<String>();
        additionalParams.add(roleId);
        additionalParams.add(userId);
        additionalParams.add(omitHighlighting);

        final JAXBElement<? extends RequestType> requestTO =
            SruRequestTypeFactory.createRequestTO(parameters, additionalParams);

		return ((JAXBElement<? extends ResponseType>) ServiceUtility.fromXML(
				Constants.SRU_CONTEXT_PATH , this.containerHandler
						.retrieveMembers(containerId, ServiceUtility.toMap(requestTO))));
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.context.ContainerRestService#retrieveMembers(SruSearchRequestParametersBean, java.util.String, java.util.String, java.util.String)
     */
    @Override
    public JAXBElement<? extends ResponseType> retrieveTocs(final String containerId,
        final SruSearchRequestParametersBean parameters, final String roleId, final String userId,
        final String omitHighlighting) throws InvalidSearchQueryException,
        MissingMethodParameterException, ContainerNotFoundException, InvalidXmlException, SystemException {

        final List<String> additionalParams = new LinkedList<String>();
        additionalParams.add(roleId);
        additionalParams.add(userId);
        additionalParams.add(omitHighlighting);

        final JAXBElement<? extends RequestType> requestTO =
            SruRequestTypeFactory.createRequestTO(parameters, additionalParams);

		return ((JAXBElement<? extends ResponseType>) ServiceUtility.fromXML(
				Constants.SRU_CONTEXT_PATH , this.containerHandler
						.retrieveTocs(containerId, ServiceUtility.toMap(requestTO))));
    }


    // FIXME
    // public TocsTO retrieveTocs( String id, Map<String, String[]> filter) throws InvalidSearchQueryException,
    // MissingMethodParameterException, ContainerNotFoundException, InvalidXmlException, SystemException;

    @Override
    public ResultTO addMembers(String id, MembersTaskParamTO membersTaskParamTO) throws ContainerNotFoundException,
        LockingException, InvalidContentException, MissingMethodParameterException, SystemException,
        InvalidContextException, AuthenticationException, AuthorizationException, OptimisticLockingException,
        MissingAttributeValueException {

        return ServiceUtility.fromXML(ResultTO.class,
            this.containerHandler.addMembers(id, ServiceUtility.toXML(membersTaskParamTO)));
    }

    // FIXME
    // public ResultTO addTocs( String id, TaskParamTO taskParamTO)
    // throws ContainerNotFoundException, LockingException, InvalidContentException, MissingMethodParameterException,
    // SystemException, InvalidContextException, AuthenticationException, AuthorizationException,
    // OptimisticLockingException, MissingAttributeValueException;

    @Override
    public ResultTO removeMembers(String id, MembersTaskParamTO membersTaskParamTO) throws ContextNotFoundException,
        LockingException, XmlSchemaValidationException, ItemNotFoundException, InvalidContextStatusException,
        InvalidItemStatusException, AuthenticationException, AuthorizationException, SystemException,
        ContainerNotFoundException, InvalidContentException {

        return ServiceUtility.fromXML(ResultTO.class,
            this.containerHandler.removeMembers(id, ServiceUtility.toXML(membersTaskParamTO)));
    }

    // TODO not supported till version 1.4
    // public String createMdRecord( String id, String xmlData) throws ContainerNotFoundException,
    // InvalidXmlException, LockingException, MissingMethodParameterException, AuthenticationException,
    // AuthorizationException, SystemException;

    @Override
    public MdRecordTO retrieveMdRecord(String id, String mdRecordId) throws ContainerNotFoundException,
        MissingMethodParameterException, MdRecordNotFoundException, AuthenticationException, AuthorizationException,
        SystemException {

        return ServiceUtility.fromXML(MdRecordTO.class, this.containerHandler.retrieveMdRecord(id, mdRecordId));
    }

    // FIXME
    // public String retrieveMdRecordContent( String id, String mdRecordId) throws ContainerNotFoundException,
    // MdRecordNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
    // SystemException;

    // FIXME
    // public String retrieveDcRecordContent( String id) throws ContainerNotFoundException,
    // AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException;

    @Override
    public MdRecordTO updateMetadataRecord(String id, String mdRecordId, MdRecordTO mdRecordTO)
        throws ContainerNotFoundException, LockingException, XmlSchemaNotFoundException, MdRecordNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        InvalidXmlException, InvalidStatusException, ReadonlyVersionException {

        return ServiceUtility.fromXML(MdRecordTO.class,
            this.containerHandler.updateMetadataRecord(id, mdRecordId, ServiceUtility.toXML(mdRecordTO)));
    }

    @Override
    public MdRecordsTO retrieveMdRecords(String id) throws ContainerNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return ServiceUtility.fromXML(MdRecordsTO.class, this.containerHandler.retrieveMdRecords(id));
    }

    @Override
    public ContainerPropertiesTO retrieveProperties(String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return ServiceUtility.fromXML(ContainerPropertiesTO.class, this.containerHandler.retrieveProperties(id));
    }

    // FIXME
    // public String retrieveResources( String id) throws ContainerNotFoundException,
    // MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    // FIXME
    // public EscidocBinaryContent retrieveResource(final String id, String resourceName, Map<String, String[]>
    // parameters)
    // throws SystemException, ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
    // AuthorizationException, OperationNotFoundException;

    @Override
    public RelationsTO retrieveRelations(String id) throws ContainerNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return ServiceUtility.fromXML(RelationsTO.class, this.containerHandler.retrieveRelations(id));
    }

    // FIXME
    // public String retrieveStructMap( String id) throws ContainerNotFoundException,
    // MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @Override
    public VersionHistoryTO retrieveVersionHistory(String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return ServiceUtility.fromXML(VersionHistoryTO.class, this.containerHandler.retrieveVersionHistory(id));
    }

    @Override
    public ParentsTO retrieveParents(String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ContainerNotFoundException, SystemException {

        return ServiceUtility.fromXML(ParentsTO.class, this.containerHandler.retrieveParents(id));
    }

    @Override
    public ResultTO release(String id, StatusTaskParamTO statusTaskParamTO) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        InvalidStatusException, SystemException, OptimisticLockingException, ReadonlyVersionException,
        InvalidXmlException {

        return ServiceUtility.fromXML(ResultTO.class,
            this.containerHandler.release(id, ServiceUtility.toXML(statusTaskParamTO)));
    }

    @Override
    public ResultTO submit(String id, StatusTaskParamTO statusTaskParamTO) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        InvalidStatusException, SystemException, OptimisticLockingException, ReadonlyVersionException,
        InvalidXmlException {

        return ServiceUtility.fromXML(ResultTO.class,
            this.containerHandler.submit(id, ServiceUtility.toXML(statusTaskParamTO)));
    }

    @Override
    public ResultTO revise(String id, StatusTaskParamTO statusTaskParamTO) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, InvalidStatusException, SystemException,
        OptimisticLockingException, ReadonlyVersionException, XmlCorruptedException {

        return ServiceUtility.fromXML(ResultTO.class,
            this.containerHandler.revise(id, ServiceUtility.toXML(statusTaskParamTO)));
    }

    @Override
    public ResultTO withdraw(String id, StatusTaskParamTO statusTaskParamTO) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        InvalidStatusException, SystemException, OptimisticLockingException, AlreadyWithdrawnException,
        ReadonlyVersionException, InvalidXmlException {

        return ServiceUtility.fromXML(ResultTO.class,
            this.containerHandler.withdraw(id, ServiceUtility.toXML(statusTaskParamTO)));
    }

    @Override
    public ResultTO lock(String id, OptimisticLockingTaskParamTO optimisticLockingTaskParamTO)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException {

        return ServiceUtility.fromXML(ResultTO.class,
            this.containerHandler.lock(id, ServiceUtility.toXML(optimisticLockingTaskParamTO)));
    }

    @Override
    public ResultTO unlock(String id, OptimisticLockingTaskParamTO optimisticLockingTaskParamTO)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException {

        return ServiceUtility.fromXML(ResultTO.class,
            this.containerHandler.unlock(id, ServiceUtility.toXML(optimisticLockingTaskParamTO)));
    }

    @Override
    public ResultTO addContentRelations(String id, RelationTaskParamTO relationTaskParamTO) throws SystemException,
        ContainerNotFoundException, OptimisticLockingException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AlreadyExistsException, InvalidStatusException, InvalidXmlException,
        MissingElementValueException, LockingException, ReadonlyVersionException, InvalidContentException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException {

        return ServiceUtility.fromXML(ResultTO.class,
            this.containerHandler.addContentRelations(id, ServiceUtility.toXML(relationTaskParamTO)));
    }

    @Override
    public ResultTO removeContentRelations(String id, RelationTaskParamTO relationTaskParamTO) throws SystemException,
        ContainerNotFoundException, OptimisticLockingException, InvalidStatusException, MissingElementValueException,
        InvalidXmlException, ContentRelationNotFoundException, LockingException, ReadonlyVersionException,
        AuthenticationException, AuthorizationException {

        return ServiceUtility.fromXML(ResultTO.class,
            this.containerHandler.removeContentRelations(id, ServiceUtility.toXML(relationTaskParamTO)));
    }

    @Override
    public ResultTO assignObjectPid(String id, AssignPidTaskParamTO assignPidTaskParamTO)
        throws InvalidStatusException, ContainerNotFoundException, LockingException, MissingMethodParameterException,
        OptimisticLockingException, SystemException, InvalidXmlException {

        return ServiceUtility.fromXML(ResultTO.class,
            this.containerHandler.assignObjectPid(id, ServiceUtility.toXML(assignPidTaskParamTO)));
    }

    @Override
    public ResultTO assignVersionPid(String id, AssignPidTaskParamTO assignPidTaskParamTO)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException, XmlCorruptedException, ReadonlyVersionException {

        return ServiceUtility.fromXML(ResultTO.class,
            this.containerHandler.assignVersionPid(id, ServiceUtility.toXML(assignPidTaskParamTO)));
    }

}
