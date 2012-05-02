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

package de.escidoc.core.om;

import java.rmi.RemoteException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.escidoc.core.domain.components.ComponentPropertiesTO;
import org.escidoc.core.domain.components.ComponentTO;
import org.escidoc.core.domain.components.ComponentsTO;
import org.escidoc.core.domain.content.stream.ContentStreamTO;
import org.escidoc.core.domain.content.stream.ContentStreamsTO;
import org.escidoc.core.domain.item.ItemPropertiesTO;
import org.escidoc.core.domain.item.ItemResourcesTO;
import org.escidoc.core.domain.item.ItemTO;
import org.escidoc.core.domain.metadatarecords.MdRecordTO;
import org.escidoc.core.domain.metadatarecords.MdRecordsTO;
import org.escidoc.core.domain.ou.ParentsTO;
import org.escidoc.core.domain.relations.RelationsTO;
import org.escidoc.core.domain.result.ResultTO;
import org.escidoc.core.domain.taskparam.assignpid.AssignPidTaskParamTO;
import org.escidoc.core.domain.taskparam.optimisticlocking.OptimisticLockingTaskParamTO;
import org.escidoc.core.domain.taskparam.relation.RelationTaskParamTO;
import org.escidoc.core.domain.taskparam.status.StatusTaskParamTO;
import org.escidoc.core.domain.version.history.VersionHistoryTO;
import org.escidoc.core.utils.io.Stream;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
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
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
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
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * 
 * @author SWA
 *
 */
@Path("/ir/item")
public interface ItemRestService {

    @PUT
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    ItemTO create(ItemTO itemTO) throws MissingContentException, ContextNotFoundException,
        ContentModelNotFoundException, ReadonlyElementViolationException, MissingAttributeValueException,
        MissingElementValueException, ReadonlyAttributeViolationException, AuthenticationException,
        AuthorizationException, XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException,
        FileNotFoundException, SystemException, InvalidContentException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, MissingMdRecordException, InvalidStatusException, RemoteException;

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id) throws ItemNotFoundException, AlreadyPublishedException, LockingException,
        AuthenticationException, AuthorizationException, InvalidStatusException, MissingMethodParameterException,
        SystemException, RemoteException;

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_XML)
    ItemTO retrieve(@PathParam("id") String id) throws ItemNotFoundException, ComponentNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    @PUT
    @Path("/{id}")
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    ItemTO update(@PathParam("id") String id, ItemTO itemTO) throws ItemNotFoundException, FileNotFoundException,
        InvalidContextException, InvalidStatusException, LockingException, NotPublishedException,
        MissingLicenceException, ComponentNotFoundException, MissingContentException, AuthenticationException,
        AuthorizationException, InvalidXmlException, MissingMethodParameterException, InvalidContentException,
        SystemException, OptimisticLockingException, AlreadyExistsException, ReadonlyViolationException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, ReadonlyVersionException,
        MissingAttributeValueException, MissingMdRecordException, RemoteException;

    @PUT
    @Path("/{id}/components/component")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    ComponentTO createComponent(@PathParam("id") String id, ComponentTO componentTO) throws MissingContentException,
        ItemNotFoundException, ComponentNotFoundException, LockingException, MissingElementValueException,
        AuthenticationException, AuthorizationException, InvalidStatusException, MissingMethodParameterException,
        FileNotFoundException, InvalidXmlException, InvalidContentException, SystemException,
        ReadonlyViolationException, OptimisticLockingException, MissingAttributeValueException, RemoteException;

    @GET
    @Path("{id}/components/component/{componentId}")
    @Produces(MediaType.TEXT_XML)
    ComponentTO retrieveComponent(@PathParam("id") String id, @PathParam("componentId") String componentId)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, RemoteException;

    @GET
    @Path("{id}/components/component/{componentId}/md-records")
    @Produces(MediaType.TEXT_XML)
    MdRecordsTO retrieveComponentMdRecords(@PathParam("id") String id, @PathParam("componentId") String componentId)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, RemoteException;

    @GET
    @Path("{id}/components/component/{componentId}/md-records/md-record/{mdRecordId}")
    @Produces(MediaType.TEXT_XML)
    MdRecordTO retrieveComponentMdRecord(
        @PathParam("id") String id, @PathParam("componentId") String componentId,
        @PathParam("mdRecordId") String mdRecordId) throws ItemNotFoundException, AuthenticationException,
        AuthorizationException, ComponentNotFoundException, MdRecordNotFoundException, MissingMethodParameterException,
        SystemException, RemoteException;

    @PUT
    @Path("{id}/components/component")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    ComponentTO updateComponent(
        @PathParam("id") String id, @PathParam("componentId") String componentId, ComponentTO componentTO)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, FileNotFoundException,
        MissingAttributeValueException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        ReadonlyViolationException, MissingContentException, InvalidContentException, ReadonlyVersionException,
        RemoteException;

    @GET
    @Path("{id}/components")
    @Produces(MediaType.TEXT_XML)
    ComponentsTO retrieveComponents(@PathParam("id") String id) throws ItemNotFoundException, AuthenticationException,
        AuthorizationException, ComponentNotFoundException, MissingMethodParameterException, SystemException,
        RemoteException;

    @GET
    @Path("{id}/components/component/{componentId}/properties")
    @Produces(MediaType.TEXT_XML)
    ComponentPropertiesTO retrieveComponentProperties(
        @PathParam("id") String id, @PathParam("componentId") String componentId) throws ItemNotFoundException,
        ComponentNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException, RemoteException;

    // TODO not supported till version 1.4
    // @PUT
    // @Path("{id}/")
    // MdRecordTO createMdRecord(@PathParam("id") String id, MdRecordTO mdRecordTO) throws ItemNotFoundException,
    // SystemException, InvalidXmlException, LockingException, MissingAttributeValueException, InvalidStatusException,
    // ComponentNotFoundException, MissingMethodParameterException, AuthorizationException, AuthenticationException,
    // RemoteException;

    @GET
    @Path("{id}/md-records/md-record/{mdRecordId}")
    @Produces(MediaType.TEXT_XML)
    MdRecordTO retrieveMdRecord(@PathParam("id") String id, @PathParam("mdRecordId") String mdRecordId)
        throws ItemNotFoundException, MdRecordNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, RemoteException;

    @GET
    @Path("/{id}/md-records/md-record/{mdRecordId}/content")
    @Produces(MediaType.TEXT_XML)
    Stream retrieveMdRecordContent(@PathParam("id") String id, @PathParam("mdRecordId") String mdRecordId) throws ItemNotFoundException,
        MdRecordNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException;
    
    @GET
    @Path("/{id}/resources/dc/content")
    @Produces(MediaType.TEXT_XML)
    Stream retrieveDcRecordContent(@PathParam("id") String id) throws ItemNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, MdRecordNotFoundException,
        SystemException;

    @PUT
    @Path("{id}/md-records/md-record/{mdRecordId}")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    MdRecordTO updateMdRecord(
        @PathParam("id") String id, @PathParam("mdRecordId") String mdRecordId, MdRecordTO mdRecordTO)
        throws ItemNotFoundException, XmlSchemaNotFoundException, LockingException, InvalidContentException,
        MdRecordNotFoundException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        ReadonlyViolationException, ReadonlyVersionException, RemoteException;

    @GET
    @Path("{id}/md-records")
    @Produces(MediaType.TEXT_XML)
    MdRecordsTO retrieveMdRecords(@PathParam("id") String id) throws ItemNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, RemoteException;

    @GET
    @Path("{id}/content-streams")
    @Produces(MediaType.TEXT_XML)
    ContentStreamsTO retrieveContentStreams(@PathParam("id") String id) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    @GET
    @Path("{id}/content-streams/content-stream/{name}")
    @Produces(MediaType.TEXT_XML)
    ContentStreamTO retrieveContentStream(@PathParam("id") String id, @PathParam("name") String name)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException, ContentStreamNotFoundException, RemoteException;

    @GET
    @Path("{id}/properties")
    @Produces(MediaType.TEXT_XML)
    ItemPropertiesTO retrieveProperties(@PathParam("id") String id) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    @GET
    @Path("/{id}/resources")
    @Produces(MediaType.TEXT_XML)
    ItemResourcesTO retrieveResources(@PathParam("id") String id) throws ItemNotFoundException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources/{name}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    Stream retrieveResource(@PathParam("id") String id, @PathParam("name") String resourceName)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException, OperationNotFoundException;

    @GET
    @Path("{id}/resources/version-history")
    @Produces(MediaType.TEXT_XML)
    VersionHistoryTO retrieveVersionHistory(@PathParam("id") String id) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    @GET
    @Path("{id}/resources/parents")
    @Produces(MediaType.TEXT_XML)
    ParentsTO retrieveParents(@PathParam("id") String id) throws ItemNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    @GET
    @Path("{id}/relations")
    @Produces(MediaType.TEXT_XML)
    RelationsTO retrieveRelations(@PathParam("id") String id) throws ItemNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, RemoteException;

    @POST
    @Path("{id}/release")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    ResultTO release(@PathParam("id") String id, StatusTaskParamTO statusTaskParamTO) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, InvalidStatusException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        ReadonlyViolationException, ReadonlyVersionException, InvalidXmlException, RemoteException;

    @POST
    @Path("{id}/submit")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    ResultTO submit(@PathParam("id") String id, StatusTaskParamTO statusTaskParamTO) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, InvalidStatusException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        ReadonlyViolationException, ReadonlyVersionException, InvalidXmlException, RemoteException;

    @POST
    @Path("{id}/revise")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    ResultTO revise(@PathParam("id") String id, StatusTaskParamTO statusTaskParamTO) throws AuthenticationException,
        AuthorizationException, ItemNotFoundException, ComponentNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        ReadonlyViolationException, ReadonlyVersionException, InvalidContentException, XmlCorruptedException,
        RemoteException;

    @POST
    @Path("{id}/withdraw")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    ResultTO withdraw(@PathParam("id") String id, StatusTaskParamTO statusTaskParamTO) throws ItemNotFoundException,
        ComponentNotFoundException, NotPublishedException, LockingException, AlreadyWithdrawnException,
        AuthenticationException, AuthorizationException, InvalidStatusException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, ReadonlyViolationException, ReadonlyVersionException,
        InvalidXmlException, RemoteException;

    @POST
    @Path("{id}/lock")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    ResultTO lock(@PathParam("id") String id, OptimisticLockingTaskParamTO optimisticLockingTaskParamTO)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, InvalidContentException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidXmlException, InvalidStatusException, RemoteException;

    @POST
    @Path("{id}/unlock")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    ResultTO unlock(@PathParam("id") String id, OptimisticLockingTaskParamTO optimisticLockingTaskParamTO)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, RemoteException;

    @DELETE
    @Path("{id}/components/component/{componentId}")
    void deleteComponent(@PathParam("id") String id, @PathParam("componentId") String componentId)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, InvalidStatusException,
        RemoteException;

    @POST
    @Path("{id}/assign-version-pid")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    ResultTO assignVersionPid(@PathParam("id") String id, AssignPidTaskParamTO assignPidTaskParamTO)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidStatusException, XmlCorruptedException, ReadonlyVersionException, RemoteException;

    @POST
    @Path("{id}/assign-object-pid")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    ResultTO assignObjectPid(@PathParam("id") String id, AssignPidTaskParamTO assignPidTaskParamTO)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidStatusException, XmlCorruptedException, RemoteException;

    @POST
    @Path("{id}/components/component/{componentId}/assign-content-pid")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    ResultTO assignContentPid(
        @PathParam("id") String id, @PathParam("componentId") String componentId,
        AssignPidTaskParamTO assignPidTaskParamTO) throws ItemNotFoundException, LockingException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException, ComponentNotFoundException, XmlCorruptedException,
        ReadonlyVersionException, RemoteException;

    @POST
    @Path("{id}/content-relations/add")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    ResultTO addContentRelations(@PathParam("id") String id, RelationTaskParamTO relationTaskParamTO)
        throws SystemException, ItemNotFoundException, ComponentNotFoundException, OptimisticLockingException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AlreadyExistsException,
        InvalidStatusException, InvalidXmlException, MissingElementValueException, LockingException,
        ReadonlyViolationException, InvalidContentException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ReadonlyVersionException, RemoteException;

    @POST
    @Path("{id}/content-relations/remove")
    @Consumes(MediaType.TEXT_XML)
    @Produces(MediaType.TEXT_XML)
    ResultTO removeContentRelations(@PathParam("id") String id, RelationTaskParamTO relationTaskParamTO)
        throws SystemException, ItemNotFoundException, ComponentNotFoundException, OptimisticLockingException,
        InvalidStatusException, MissingElementValueException, InvalidContentException, InvalidXmlException,
        ContentRelationNotFoundException, AlreadyDeletedException, LockingException, ReadonlyViolationException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, ReadonlyVersionException,
        RemoteException;

    @GET
    @Path("{id}/components/component/{componentId}/content")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    Response retrieveContent(@PathParam("id")  String id, @PathParam("componentId") String componentId)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        InvalidStatusException, ResourceNotFoundException;

}
