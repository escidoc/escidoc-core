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

package de.escidoc.core.oum;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.ResultTO;
import org.escidoc.core.domain.metadatarecords.MdRecordTO;
import org.escidoc.core.domain.metadatarecords.MdRecordsTO;
import org.escidoc.core.domain.ou.OrganizationalUnitPropertiesTO;
import org.escidoc.core.domain.ou.OrganizationalUnitResourcesTO;
import org.escidoc.core.domain.ou.OrganizationalUnitTO;
import org.escidoc.core.domain.ou.ParentsListTO;
import org.escidoc.core.domain.ou.ParentsTO;
import org.escidoc.core.domain.ou.PathListTO;
import org.escidoc.core.domain.ou.SuccessorListTO;
import org.escidoc.core.domain.sru.ResponseType;
import org.escidoc.core.domain.taskparam.StatusTaskParamTO;
import org.escidoc.core.utils.io.MimeTypes;
import org.escidoc.core.utils.io.Stream;

import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHasChildrenException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHierarchyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface OrganizationalUnitRestService {

    @PUT
    OrganizationalUnitTO create(OrganizationalUnitTO organizationalUnitTO) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, MissingAttributeValueException,
        MissingElementValueException, OrganizationalUnitNotFoundException, InvalidStatusException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMdRecordException;

    @GET
    @Path("/{id}")
    OrganizationalUnitTO retrieve(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    @POST
    @Path("/{id}")
    OrganizationalUnitTO update(@PathParam("id") String id, OrganizationalUnitTO organizationalUnitTO)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, InvalidXmlException, MissingElementValueException,
        InvalidStatusException;

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, InvalidStatusException,
        OrganizationalUnitHasChildrenException, SystemException;

    @POST
    @Path("/{id}/md-records")
    MdRecordsTO updateMdRecords(@PathParam("id") String id, MdRecordsTO mdRecordTO) throws AuthenticationException,
        AuthorizationException, InvalidXmlException, InvalidStatusException, MissingMethodParameterException,
        OptimisticLockingException, OrganizationalUnitNotFoundException, MissingElementValueException, SystemException;

    @POST
    @Path("/{id}/parents")
    ParentsTO updateParents(@PathParam("id") String id, ParentsTO parentsTO) throws AuthenticationException,
        AuthorizationException, InvalidXmlException, MissingMethodParameterException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException, InvalidStatusException;

    @GET
    @Path("/{id}/properties")
    OrganizationalUnitPropertiesTO retrieveProperties(@PathParam("id") String id) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/resources/{name}")
    Stream retrieveResource(@PathParam("id") String id, @PathParam("name") String resourceName)
        throws OrganizationalUnitNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OperationNotFoundException, SystemException;

    @GET
    @Path("/{id}/resources")
    OrganizationalUnitResourcesTO retrieveResources(@PathParam("id") String id) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/md-records")
    MdRecordsTO retrieveMdRecords(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/md-records/md-record/{name}")
    MdRecordTO retrieveMdRecord(@PathParam("id") String id, @PathParam("name") String name)
        throws AuthenticationException, AuthorizationException, MdRecordNotFoundException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/parents")
    ParentsListTO retrieveParents(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

     @GET
     @Path("/{id}/resources/parent-objects")
     JAXBElement<? extends ResponseType> retrieveParentObjects(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
     MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/resources/successors")
    SuccessorListTO retrieveSuccessors(@PathParam("id") String id) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

     @GET
     @Path("/{id}/resources/child-objects")
     JAXBElement<? extends ResponseType> retrieveChildObjects(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
     MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/resources/path-list")
    PathListTO retrievePathList(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        OrganizationalUnitNotFoundException, SystemException, MissingMethodParameterException;

    @POST
    @Path("/{id}/close")
    ResultTO close(@PathParam("id") String id, StatusTaskParamTO closeParam) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException, InvalidXmlException;

    @POST
    @Path("/{id}/open")
    ResultTO open(@PathParam("id") String id, StatusTaskParamTO openParams) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException, InvalidXmlException;
}
