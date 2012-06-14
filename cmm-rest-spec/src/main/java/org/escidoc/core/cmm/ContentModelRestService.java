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
package org.escidoc.core.cmm;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ResourceInUseException;
import de.escidoc.core.common.exceptions.system.SystemException;
import net.sf.oval.constraint.NotNull;
import org.escidoc.core.domain.content.model.ContentModelPropertiesTypeTO;
import org.escidoc.core.domain.content.model.ContentModelResourcesTypeTO;
import org.escidoc.core.domain.content.model.ContentModelTypeTO;
import org.escidoc.core.domain.version.history.VersionHistoryTypeTO;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Path("/cmm/content-model")
public interface ContentModelRestService {

    @PUT
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    JAXBElement<ContentModelTypeTO> create(@NotNull ContentModelTypeTO contentModelTypeTO)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        MissingAttributeValueException, InvalidContentException, XmlCorruptedException, XmlSchemaValidationException;

    @DELETE
    @Path("/{id}")
    void delete(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, ContentModelNotFoundException,
        MissingMethodParameterException, SystemException, LockingException, InvalidStatusException,
        ResourceInUseException;

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<ContentModelTypeTO> retrieve(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, ContentModelNotFoundException,
        MissingMethodParameterException, SystemException;

    @GET
    @Path("/{id}/properties")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<ContentModelPropertiesTypeTO> retrieveProperties(@NotNull @PathParam("id") String id)
        throws ContentModelNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException;

    @GET
    @Path("/{id}/resources")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<ContentModelResourcesTypeTO> retrieveResources(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, ContentModelNotFoundException,
        MissingMethodParameterException, SystemException;

    @GET
    @Path("/{id}/resources/version-history")
    @Produces(MediaType.TEXT_XML)
    JAXBElement<VersionHistoryTypeTO> retrieveVersionHistory(@NotNull @PathParam("id") String id)
        throws AuthenticationException, AuthorizationException, ContentModelNotFoundException,
        MissingMethodParameterException, SystemException;

    @PUT
    @Path("/{id}")
    @Produces(MediaType.TEXT_XML)
    @Consumes(MediaType.TEXT_XML)
    JAXBElement<ContentModelTypeTO> update(@NotNull @PathParam("id") String id, @NotNull ContentModelTypeTO contentModelTypeTO)
        throws AuthenticationException, AuthorizationException, ContentModelNotFoundException, InvalidXmlException,
        MissingMethodParameterException, OptimisticLockingException, SystemException, ReadonlyVersionException,
        MissingAttributeValueException, InvalidContentException;

    @GET
    @Path("/{id}/md-record-definitions/md-record-definition/{name}/schema/content")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    Response retrieveMdRecordDefinitionSchemaContent(@PathParam("id") String id, @PathParam("name") String name)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        ContentModelNotFoundException, SystemException;

    @GET
    @Path("/{id}/resource-definitions/resource-definition/{name}/xslt/content")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    Response retrieveResourceDefinitionXsltContent(@PathParam("id") String id, @PathParam("name") String name)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        ResourceNotFoundException;

}
