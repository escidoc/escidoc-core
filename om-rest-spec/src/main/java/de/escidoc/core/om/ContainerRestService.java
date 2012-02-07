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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.ResultTO;
import org.escidoc.core.domain.container.ContainerPropertiesTO;
import org.escidoc.core.domain.container.ContainerResourcesTO;
import org.escidoc.core.domain.container.ContainerTO;
import org.escidoc.core.domain.container.StructMapTO;
import org.escidoc.core.domain.metadatarecords.MdRecordTO;
import org.escidoc.core.domain.metadatarecords.MdRecordsTO;
import org.escidoc.core.domain.ou.ParentsTO;
import org.escidoc.core.domain.relations.RelationsTO;
import org.escidoc.core.domain.sru.ResponseType;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.escidoc.core.domain.taskparam.AssignPidTaskParamTO;
import org.escidoc.core.domain.taskparam.MembersTaskParamTO;
import org.escidoc.core.domain.taskparam.OptimisticLockingTaskParamTO;
import org.escidoc.core.domain.taskparam.RelationTaskParamTO;
import org.escidoc.core.domain.taskparam.StatusTaskParamTO;
import org.escidoc.core.domain.version.VersionHistoryTO;
import org.escidoc.core.utils.io.MimeTypes;
import org.escidoc.core.utils.io.Stream;

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

/**
 * 
 * @author SWA
 * 
 */
@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface ContainerRestService {

    @PUT
    @Path("/{id}")
    ContainerTO create(final ContainerTO containerTO) throws ContextNotFoundException, ContentModelNotFoundException,
        InvalidContentException, MissingMethodParameterException, MissingAttributeValueException,
        MissingElementValueException, SystemException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMdRecordException, XmlCorruptedException, XmlSchemaValidationException;

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id) throws ContainerNotFoundException, LockingException,
        InvalidStatusException, SystemException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException;

    @GET
    @Path("/{id}")
    ContainerTO retrieve(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ContainerNotFoundException, SystemException;

    @POST
    @Path("/{id}")
    ContainerTO update(@PathParam("id") String id, ContainerTO containerTO) throws ContainerNotFoundException,
        LockingException, InvalidContentException, MissingMethodParameterException, InvalidXmlException,
        OptimisticLockingException, InvalidStatusException, ReadonlyVersionException, SystemException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AuthenticationException,
        AuthorizationException, MissingAttributeValueException, MissingMdRecordException;

	@GET
	@Path("/{id}/resources/members")
	JAXBElement<? extends ResponseType> retrieveMembers(@PathParam("id") String id,
        @QueryParam("") SruSearchRequestParametersBean parameters, 
        @QueryParam("x-info5-roleId") String roleId,
        @QueryParam("x-info5-userId") String userId, 
        @QueryParam("x-info5-omitHighlighting") String omitHighlighting) throws InvalidSearchQueryException,
            MissingMethodParameterException, ContainerNotFoundException, SystemException;

    @POST
    @Path("/{id}/members/add")
    ResultTO addMembers(@PathParam("id") String id, MembersTaskParamTO membersTaskParamTO)
        throws ContainerNotFoundException, LockingException, InvalidContentException, MissingMethodParameterException,
        SystemException, InvalidContextException, AuthenticationException, AuthorizationException,
        OptimisticLockingException, MissingAttributeValueException;

    @POST
    @Path("/{id}/members/remove")
    ResultTO removeMembers(@PathParam("id") String id, MembersTaskParamTO membersTaskParamTO)
        throws ContextNotFoundException, LockingException, XmlSchemaValidationException, ItemNotFoundException,
        InvalidContextStatusException, InvalidItemStatusException, AuthenticationException, AuthorizationException,
        SystemException, ContainerNotFoundException, InvalidContentException;

    // TODO not supported till version 1.4
    // @POST
    // @Path("/{id}/md-records/md-record")
    // String createMdRecord(@PathParam("id") String id, String xmlData) throws ContainerNotFoundException,
    // InvalidXmlException, LockingException, MissingMethodParameterException, AuthenticationException,
    // AuthorizationException, SystemException;

    @GET
    @Path("/{id}/md-records/md-record/{mdRecordId}")
    MdRecordTO retrieveMdRecord(@PathParam("id") String id, @PathParam("mdRecordId") String mdRecordId)
        throws ContainerNotFoundException, MissingMethodParameterException, MdRecordNotFoundException,
        AuthenticationException, AuthorizationException, SystemException;

     @GET
     @Path("/{id}/md-records/md-record/{mdRecordId}/content")
     Stream retrieveMdRecordContent(@PathParam("id") String id, @PathParam("mdRecordId") String mdRecordId) throws ContainerNotFoundException,
     MdRecordNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
     SystemException;

     @GET
     @Path("/{id}/resources/dc/content")
     Stream retrieveDcRecordContent(@PathParam("id") String id) throws ContainerNotFoundException,
     AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException;

    @POST
    @Path("/{id}/md-records/md-record/{mdRecordId}")
    MdRecordTO updateMetadataRecord(
        @PathParam("id") String id, @PathParam("mdRecordId") String mdRecordId, MdRecordTO mdRecordTO)
        throws ContainerNotFoundException, LockingException, XmlSchemaNotFoundException, MdRecordNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        InvalidXmlException, InvalidStatusException, ReadonlyVersionException;

    @GET
    @Path("/{id}/md-records")
    MdRecordsTO retrieveMdRecords(@PathParam("id") String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/properties")
    ContainerPropertiesTO retrieveProperties(@PathParam("id") String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

     @GET
     @Path("/{id}/resources")
     ContainerResourcesTO retrieveResources(@PathParam("id") String id) throws ContainerNotFoundException,
     MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources/{name}")
    Stream retrieveResource(@PathParam("id") String id, @PathParam("name") String resourceName, 
        @QueryParam("") SruSearchRequestParametersBean parameters, 
        @QueryParam("x-info5-roleId") String roleId,
        @QueryParam("x-info5-userId") String userId, 
        @QueryParam("x-info5-omitHighlighting") String omitHighlighting)
        throws SystemException, ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, OperationNotFoundException;

    @GET
    @Path("/{id}/relations")
    RelationsTO retrieveRelations(@PathParam("id") String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/struct-map")
    StructMapTO retrieveStructMap(@PathParam("id") String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources/version-history")
    VersionHistoryTO retrieveVersionHistory(@PathParam("id") String id) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources/parents")
    ParentsTO retrieveParents(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ContainerNotFoundException, SystemException;

    @POST
    @Path("/{id}/release")
    ResultTO release(@PathParam("id") String id, StatusTaskParamTO statusTaskParamTO)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, InvalidStatusException, SystemException, OptimisticLockingException,
        ReadonlyVersionException, InvalidXmlException;

    @POST
    @Path("/{id}/submit")
    ResultTO submit(@PathParam("id") String id, StatusTaskParamTO statusTaskParamTO) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        InvalidStatusException, SystemException, OptimisticLockingException, ReadonlyVersionException,
        InvalidXmlException;

    @POST
    @Path("/{id}/revise")
    ResultTO revise(@PathParam("id") String id, StatusTaskParamTO statusTaskParamTO) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, InvalidStatusException, SystemException,
        OptimisticLockingException, ReadonlyVersionException, XmlCorruptedException;

    @POST
    @Path("/{id}/withdraw")
    ResultTO withdraw(@PathParam("id") String id, StatusTaskParamTO statusTaskParamTO)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, InvalidStatusException, SystemException, OptimisticLockingException,
        AlreadyWithdrawnException, ReadonlyVersionException, InvalidXmlException;

    @POST
    @Path("/{id}/lock")
    ResultTO lock(@PathParam("id") String id, OptimisticLockingTaskParamTO optimisticLockingTaskParamTO)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException;

    @POST
    @Path("/{id}/unlock")
    ResultTO unlock(@PathParam("id") String id, OptimisticLockingTaskParamTO optimisticLockingTaskParamTO)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException;

    @POST
    @Path("/{id}/content-relations/add")
    ResultTO addContentRelations(@PathParam("id") String id, RelationTaskParamTO relationTaskParamTO)
        throws SystemException, ContainerNotFoundException, OptimisticLockingException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AlreadyExistsException,
        InvalidStatusException, InvalidXmlException, MissingElementValueException, LockingException,
        ReadonlyVersionException, InvalidContentException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException;

    @POST
    @Path("/{id}/content-relations/remove")
    ResultTO removeContentRelations(@PathParam("id") String id, RelationTaskParamTO relationTaskParamTO)
        throws SystemException, ContainerNotFoundException, OptimisticLockingException, InvalidStatusException,
        MissingElementValueException, InvalidXmlException, ContentRelationNotFoundException, LockingException,
        ReadonlyVersionException, AuthenticationException, AuthorizationException;

    @POST
    @Path("/{id}/assign-object-pid")
    ResultTO assignObjectPid(@PathParam("id") String id, AssignPidTaskParamTO assignPidTaskParamTO)
        throws InvalidStatusException, ContainerNotFoundException, LockingException, MissingMethodParameterException,
        OptimisticLockingException, SystemException, InvalidXmlException;

    @POST
    @Path("/{id}/assign-version-pid")
    ResultTO assignVersionPid(@PathParam("id") String id, AssignPidTaskParamTO assignPidTaskParamTO)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException, XmlCorruptedException, ReadonlyVersionException;

}
