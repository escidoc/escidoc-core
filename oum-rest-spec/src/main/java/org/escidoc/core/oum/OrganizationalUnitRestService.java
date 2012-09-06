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
package org.escidoc.core.oum;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

import net.sf.oval.constraint.NotNull;
import org.escidoc.core.domain.metadatarecords.MdRecordTypeTO;
import org.escidoc.core.domain.metadatarecords.MdRecordsTypeTO;
import org.escidoc.core.domain.ou.OrganizationalUnitPropertiesTypeTO;
import org.escidoc.core.domain.ou.OrganizationalUnitResourcesTypeTO;
import org.escidoc.core.domain.ou.OrganizationalUnitTypeTO;
import org.escidoc.core.domain.ou.path.list.OrganizationalUnitPathListTypeTO;
import org.escidoc.core.domain.ou.successors.SuccessorsTypeTO;
import org.escidoc.core.domain.parents.ParentsTypeTO;
import org.escidoc.core.domain.result.ResultTypeTO;
import org.escidoc.core.domain.sru.ResponseTypeTO;
import org.escidoc.core.domain.taskparam.status.StatusTaskParamTO;
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

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Path("/oum/organizational-unit")
public interface OrganizationalUnitRestService {

    @PUT
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    JAXBElement<OrganizationalUnitTypeTO> create(@NotNull OrganizationalUnitTypeTO organizationalUnitTO)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        MissingAttributeValueException, MissingElementValueException, OrganizationalUnitNotFoundException,
        InvalidStatusException, XmlCorruptedException, XmlSchemaValidationException, MissingMdRecordException;

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<OrganizationalUnitTypeTO> retrieve(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException;

    @PUT
    @Path("/{id}")
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    JAXBElement<OrganizationalUnitTypeTO> update(@NotNull @PathParam("id") String id,
        @NotNull OrganizationalUnitTypeTO organizationalUnitTO)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, InvalidXmlException, MissingElementValueException,
        InvalidStatusException;

    @DELETE
    @Path("/{id}")
    void delete(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, InvalidStatusException, OrganizationalUnitHasChildrenException,
        SystemException;

    @PUT
    @Path("/{id}/md-records")
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    JAXBElement<MdRecordsTypeTO> updateMdRecords(@NotNull @PathParam("id") String id,
        @NotNull MdRecordsTypeTO mdRecordTO)
        throws AuthenticationException, AuthorizationException, InvalidXmlException, InvalidStatusException,
        MissingMethodParameterException, OptimisticLockingException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException;

    @PUT
    @Path("/{id}/parents")
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    JAXBElement<ParentsTypeTO> updateParents(@NotNull @PathParam("id") String id, @NotNull ParentsTypeTO parentsTO)
        throws AuthenticationException, AuthorizationException, InvalidXmlException, MissingMethodParameterException,
        OptimisticLockingException, OrganizationalUnitHierarchyViolationException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException, InvalidStatusException;

    @GET
    @Path("/{id}/properties")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<OrganizationalUnitPropertiesTypeTO> retrieveProperties(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/resources/{name}")
    @Produces(MediaType.TEXT_XML)
    Stream retrieveResource(@NotNull @PathParam("id") String id, @NotNull @PathParam("name") String resourceName)
        throws OrganizationalUnitNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OperationNotFoundException, SystemException;

    @GET
    @Path("/{id}/resources")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<OrganizationalUnitResourcesTypeTO> retrieveResources(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/md-records")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<MdRecordsTypeTO> retrieveMdRecords(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/md-records/md-record/{name}")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<MdRecordTypeTO> retrieveMdRecord(@NotNull @PathParam("id") String id,
        @NotNull @PathParam("name") String name)
        throws AuthenticationException, AuthorizationException, MdRecordNotFoundException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/parents")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<ParentsTypeTO> retrieveParents(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/resources/parent-objects")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<? extends ResponseTypeTO> retrieveParentObjects(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/resources/successors")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<SuccessorsTypeTO> retrieveSuccessors(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/resources/child-objects")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<? extends ResponseTypeTO> retrieveChildObjects(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException;

    @GET
    @Path("/{id}/resources/path-list")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<OrganizationalUnitPathListTypeTO> retrievePathList(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, OrganizationalUnitNotFoundException, SystemException,
        MissingMethodParameterException;

    @POST
    @Path("/{id}/close")
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    JAXBElement<ResultTypeTO> close(@NotNull @PathParam("id") String id, @NotNull StatusTaskParamTO closeParam)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, InvalidStatusException, SystemException, OptimisticLockingException,
        InvalidXmlException;

    @POST
    @Path("/{id}/open")
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    JAXBElement<ResultTypeTO> open(@NotNull @PathParam("id") String id, @NotNull StatusTaskParamTO openParams)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, InvalidStatusException, SystemException, OptimisticLockingException,
        InvalidXmlException;
}