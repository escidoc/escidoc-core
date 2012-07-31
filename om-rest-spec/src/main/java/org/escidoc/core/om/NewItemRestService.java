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
package org.escidoc.core.om;

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
import javax.xml.bind.JAXBElement;

import net.sf.oval.constraint.NotNull;
import org.escidoc.core.domain.components.ComponentPropertiesTypeTO;
import org.escidoc.core.domain.components.ComponentTypeTO;
import org.escidoc.core.domain.components.ComponentsTypeTO;
import org.escidoc.core.domain.content.stream.ContentStreamTypeTO;
import org.escidoc.core.domain.content.stream.ContentStreamsTypeTO;
import org.escidoc.core.domain.item.ItemPropertiesTypeTO;
import org.escidoc.core.domain.item.ItemResourcesTypeTO;
import org.escidoc.core.domain.item.ItemTypeTO;
import org.escidoc.core.domain.metadatarecords.MdRecordTypeTO;
import org.escidoc.core.domain.metadatarecords.MdRecordsTypeTO;
import org.escidoc.core.domain.parents.ParentsTypeTO;
import org.escidoc.core.domain.relations.RelationsTypeTO;
import org.escidoc.core.domain.result.ResultTypeTO;
import org.escidoc.core.domain.taskparam.assignpid.AssignPidTaskParamTO;
import org.escidoc.core.domain.taskparam.optimisticlocking.OptimisticLockingTaskParamTO;
import org.escidoc.core.domain.taskparam.relation.RelationTaskParamTO;
import org.escidoc.core.domain.taskparam.status.StatusTaskParamTO;
import org.escidoc.core.domain.version.history.VersionHistoryTypeTO;
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
 * @author SWA
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Path("/ir/newitem")
public interface NewItemRestService {

    @PUT
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    JAXBElement<ItemTypeTO> create(@NotNull ItemTypeTO itemTO)
        throws MissingContentException, ContextNotFoundException, ContentModelNotFoundException,
        ReadonlyElementViolationException, MissingAttributeValueException, MissingElementValueException,
        ReadonlyAttributeViolationException, AuthenticationException, AuthorizationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, FileNotFoundException, SystemException,
        InvalidContentException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        MissingMdRecordException, InvalidStatusException, RemoteException;

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<ItemTypeTO> retrieve(@NotNull @PathParam("id") String id)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, RemoteException;

    @PUT
    @Path("/{id}")
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    JAXBElement<ItemTypeTO> update(@NotNull @PathParam("id") String id, @NotNull ItemTypeTO itemTO)
        throws ItemNotFoundException, FileNotFoundException, InvalidContextException, InvalidStatusException,
        LockingException, NotPublishedException, MissingLicenceException, ComponentNotFoundException,
        MissingContentException, AuthenticationException, AuthorizationException, InvalidXmlException,
        MissingMethodParameterException, InvalidContentException, SystemException, OptimisticLockingException,
        AlreadyExistsException, ReadonlyViolationException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, ReadonlyVersionException, MissingAttributeValueException,
        MissingMdRecordException, RemoteException;

}