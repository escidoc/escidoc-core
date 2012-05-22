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
package org.escidoc.core.om.internal;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.container.ContainerPropertiesTO;
import org.escidoc.core.domain.container.ContainerResourcesTO;
import org.escidoc.core.domain.container.ContainerTO;
import org.escidoc.core.domain.container.structmap.StructMapTO;
import org.escidoc.core.domain.metadatarecords.MdRecordTO;
import org.escidoc.core.domain.metadatarecords.MdRecordsTO;
import org.escidoc.core.domain.ou.ParentsTO;
import org.escidoc.core.domain.relations.RelationsTO;
import org.escidoc.core.domain.result.ResultTO;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.ResponseTypeTO;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.escidoc.core.domain.taskparam.assignpid.AssignPidTaskParamTO;
import org.escidoc.core.domain.taskparam.members.MembersTaskParamTO;
import org.escidoc.core.domain.taskparam.optimisticlocking.OptimisticLockingTaskParamTO;
import org.escidoc.core.domain.taskparam.relation.RelationTaskParamTO;
import org.escidoc.core.domain.taskparam.status.StatusTaskParamTO;
import org.escidoc.core.domain.version.history.VersionHistoryTO;
import org.escidoc.core.om.ContainerRestService;
import org.escidoc.core.utils.io.EscidocBinaryContent;
import org.escidoc.core.utils.io.IOUtils;
import org.escidoc.core.utils.io.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.XmlSchemaNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyWithdrawnException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;

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

    @Autowired
    private ServiceUtility serviceUtility;

    protected ContainerRestServiceImpl() {
    }

    @Override
    public ContainerTO create(final ContainerTO containerTO) throws ContextNotFoundException,
        ContentModelNotFoundException, InvalidContentException, MissingMethodParameterException,
        MissingAttributeValueException, MissingElementValueException, SystemException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AuthenticationException,
        AuthorizationException, InvalidStatusException, MissingMdRecordException, XmlCorruptedException,
        XmlSchemaValidationException {

        return serviceUtility.fromXML(ContainerTO.class,
            this.containerHandler.create(serviceUtility.toXML(containerTO)));
    }

    @Override
    public void delete(String id) throws ContainerNotFoundException, LockingException, InvalidStatusException,
        SystemException, MissingMethodParameterException, AuthenticationException, AuthorizationException {

        this.containerHandler.delete(id);
    }

    @Override
    public ContainerTO retrieve(String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ContainerNotFoundException, SystemException {

        return serviceUtility.fromXML(ContainerTO.class, this.containerHandler.retrieve(id));
    }

    @Override
    public ContainerTO update(String id, ContainerTO containerTO) throws ContainerNotFoundException, LockingException,
        InvalidContentException, MissingMethodParameterException, InvalidXmlException, OptimisticLockingException,
        InvalidStatusException, ReadonlyVersionException, SystemException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AuthenticationException, AuthorizationException,
        MissingAttributeValueException, MissingMdRecordException {

        return serviceUtility.fromXML(ContainerTO.class,
            this.containerHandler.update(id, serviceUtility.toXML(containerTO)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.context.ContainerRestService#retrieveMembers(org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean,
     * java.util.String, java.util.String, java.util.String)
     */
    @Override
    public JAXBElement<? extends ResponseTypeTO> retrieveMembers(
        final String containerId, 
        final SruSearchRequestParametersBean parameters, 
        final String roleId,
        final String userId, 
        final String omitHighlighting) throws InvalidSearchQueryException,
        MissingMethodParameterException, ContainerNotFoundException, SystemException {

        Map<String, String[]> map = serviceUtility.handleSruRequest(parameters, roleId, userId, omitHighlighting);

        return (JAXBElement<? extends ResponseTypeTO>) serviceUtility.fromXML(
            this.containerHandler.retrieveMembers(containerId, map));
    }

    @Override
    public ResultTO addMembers(final String id, final MembersTaskParamTO membersTaskParamTO) throws ContainerNotFoundException,
        LockingException, InvalidContentException, MissingMethodParameterException, SystemException,
        InvalidContextException, AuthenticationException, AuthorizationException, OptimisticLockingException,
        MissingAttributeValueException {

        return serviceUtility.fromXML(ResultTO.class,
            this.containerHandler.addMembers(id, serviceUtility.toXML(membersTaskParamTO)));
    }

    @Override
    public ResultTO removeMembers(String id, MembersTaskParamTO membersTaskParamTO) throws ContextNotFoundException,
        LockingException, XmlSchemaValidationException, ItemNotFoundException, InvalidContextStatusException,
        InvalidItemStatusException, AuthenticationException, AuthorizationException, SystemException,
        ContainerNotFoundException, InvalidContentException {

        return serviceUtility.fromXML(ResultTO.class,
            this.containerHandler.removeMembers(id, serviceUtility.toXML(membersTaskParamTO)));
    }

    // TODO not supported till version 1.4
    // public String createMdRecord( String id, String xmlData) throws ContainerNotFoundException,
    // InvalidXmlException, LockingException, MissingMethodParameterException, AuthenticationException,
    // AuthorizationException, SystemException;

    @Override
    public MdRecordTO retrieveMdRecord(String id, String mdRecordId) throws ContainerNotFoundException,
        MissingMethodParameterException, MdRecordNotFoundException, AuthenticationException, AuthorizationException,
        SystemException {

        return serviceUtility.fromXML(MdRecordTO.class, this.containerHandler.retrieveMdRecord(id, mdRecordId));
    }

    @Override
    public Stream retrieveMdRecordContent(String id, String mdRecordId) throws ContainerNotFoundException,
        MdRecordNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {

        Stream stream = new Stream();
        try {
            Reader reader = new StringReader(this.containerHandler.retrieveMdRecordContent(id, mdRecordId));
            IOUtils.copy(reader, stream);
        }
        catch (IOException e) {
            LOG.error("Failed to copy stream", e);
            throw new SystemException(e);
        }
        return stream;
    }

    @Override
    public Stream retrieveDcRecordContent(String id) throws ContainerNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException {

        Stream stream = new Stream();
        try {
            Reader reader = new StringReader(this.containerHandler.retrieveDcRecordContent(id));
            IOUtils.copy(reader, stream);
        }
        catch (IOException e) {
            LOG.error("Failed to copy stream", e);
            throw new SystemException(e);
        }
        return stream;
    }

    @Override
    public MdRecordTO updateMetadataRecord(String id, String mdRecordId, MdRecordTO mdRecordTO)
        throws ContainerNotFoundException, LockingException, XmlSchemaNotFoundException, MdRecordNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        InvalidXmlException, InvalidStatusException, ReadonlyVersionException {

        return serviceUtility.fromXML(MdRecordTO.class,
            this.containerHandler.updateMetadataRecord(id, mdRecordId, serviceUtility.toXML(mdRecordTO)));
    }

    @Override
    public MdRecordsTO retrieveMdRecords(String id) throws ContainerNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return serviceUtility.fromXML(MdRecordsTO.class, this.containerHandler.retrieveMdRecords(id));
    }

    @Override
    public ContainerPropertiesTO retrieveProperties(String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return serviceUtility.fromXML(ContainerPropertiesTO.class, this.containerHandler.retrieveProperties(id));
    }

    @Override
    public ContainerResourcesTO retrieveResources(String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return serviceUtility.fromXML(ContainerResourcesTO.class, this.containerHandler.retrieveResources(id));
    }

    @Override
    public Stream retrieveResource(
        final String id, 
        final String resourceName, 
        final SruSearchRequestParametersBean parameters, 
        final String roleId, 
        final String userId, 
        final String omitHighlighting) throws SystemException,
        ContainerNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        OperationNotFoundException {

        // prepare parameter list
        Map<String, String[]> map = serviceUtility.handleSruRequest(parameters, roleId, userId, omitHighlighting);
        EscidocBinaryContent content = this.containerHandler.retrieveResource(id, resourceName, map);

        Stream stream = new Stream();
        try {
            IOUtils.copy(content.getContent(), stream);
        }
        catch (IOException e) {
            String msg = "Failed to copy stream";
            LOG.error(msg, e);
            throw new SystemException(msg, e);
        }
        return stream;
    }

    @Override
    public RelationsTO retrieveRelations(String id) throws ContainerNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        return serviceUtility.fromXML(RelationsTO.class, this.containerHandler.retrieveRelations(id));
    }

    @Override
     public StructMapTO retrieveStructMap( String id) throws ContainerNotFoundException,
     MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        
        return serviceUtility.fromXML(StructMapTO.class, this.containerHandler.retrieveStructMap(id));
    }

    @Override
    public VersionHistoryTO retrieveVersionHistory(String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {

        return serviceUtility.fromXML(VersionHistoryTO.class, this.containerHandler.retrieveVersionHistory(id));
    }

    @Override
    public ParentsTO retrieveParents(String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ContainerNotFoundException, SystemException {

        return serviceUtility.fromXML(ParentsTO.class, this.containerHandler.retrieveParents(id));
    }

    @Override
    public ResultTO release(String id, StatusTaskParamTO statusTaskParamTO) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        InvalidStatusException, SystemException, OptimisticLockingException, ReadonlyVersionException,
        InvalidXmlException {

        return serviceUtility.fromXML(ResultTO.class,
            this.containerHandler.release(id, serviceUtility.toXML(statusTaskParamTO)));
    }

    @Override
    public ResultTO submit(String id, StatusTaskParamTO statusTaskParamTO) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        InvalidStatusException, SystemException, OptimisticLockingException, ReadonlyVersionException,
        InvalidXmlException {

        return serviceUtility.fromXML(ResultTO.class,
            this.containerHandler.submit(id, serviceUtility.toXML(statusTaskParamTO)));
    }

    @Override
    public ResultTO revise(String id, StatusTaskParamTO statusTaskParamTO) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, InvalidStatusException, SystemException,
        OptimisticLockingException, ReadonlyVersionException, XmlCorruptedException {

        return serviceUtility.fromXML(ResultTO.class,
            this.containerHandler.revise(id, serviceUtility.toXML(statusTaskParamTO)));
    }

    @Override
    public ResultTO withdraw(String id, StatusTaskParamTO statusTaskParamTO) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        InvalidStatusException, SystemException, OptimisticLockingException, AlreadyWithdrawnException,
        ReadonlyVersionException, InvalidXmlException {

        return serviceUtility.fromXML(ResultTO.class,
            this.containerHandler.withdraw(id, serviceUtility.toXML(statusTaskParamTO)));
    }

    @Override
    public ResultTO lock(String id, OptimisticLockingTaskParamTO optimisticLockingTaskParamTO)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException {

        return serviceUtility.fromXML(ResultTO.class,
            this.containerHandler.lock(id, serviceUtility.toXML(optimisticLockingTaskParamTO)));
    }

    @Override
    public ResultTO unlock(String id, OptimisticLockingTaskParamTO optimisticLockingTaskParamTO)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException {

        return serviceUtility.fromXML(ResultTO.class,
            this.containerHandler.unlock(id, serviceUtility.toXML(optimisticLockingTaskParamTO)));
    }

    @Override
    public ResultTO addContentRelations(String id, RelationTaskParamTO relationTaskParamTO) throws SystemException,
        ContainerNotFoundException, OptimisticLockingException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AlreadyExistsException, InvalidStatusException, InvalidXmlException,
        MissingElementValueException, LockingException, ReadonlyVersionException, InvalidContentException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException {

        return serviceUtility.fromXML(ResultTO.class,
            this.containerHandler.addContentRelations(id, serviceUtility.toXML(relationTaskParamTO)));
    }

    @Override
    public ResultTO removeContentRelations(String id, RelationTaskParamTO relationTaskParamTO) throws SystemException,
        ContainerNotFoundException, OptimisticLockingException, InvalidStatusException, MissingElementValueException,
        InvalidXmlException, ContentRelationNotFoundException, LockingException, ReadonlyVersionException,
        AuthenticationException, AuthorizationException {

        return serviceUtility.fromXML(ResultTO.class,
            this.containerHandler.removeContentRelations(id, serviceUtility.toXML(relationTaskParamTO)));
    }

    @Override
    public ResultTO assignObjectPid(String id, AssignPidTaskParamTO assignPidTaskParamTO)
        throws InvalidStatusException, ContainerNotFoundException, LockingException, MissingMethodParameterException,
        OptimisticLockingException, SystemException, InvalidXmlException {

        return serviceUtility.fromXML(ResultTO.class,
            this.containerHandler.assignObjectPid(id, serviceUtility.toXML(assignPidTaskParamTO)));
    }

    @Override
    public ResultTO assignVersionPid(String id, AssignPidTaskParamTO assignPidTaskParamTO)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException, XmlCorruptedException, ReadonlyVersionException {

        return serviceUtility.fromXML(ResultTO.class,
            this.containerHandler.assignVersionPid(id, serviceUtility.toXML(assignPidTaskParamTO)));
    }
}
