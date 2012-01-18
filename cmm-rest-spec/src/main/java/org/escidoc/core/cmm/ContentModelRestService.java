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

import org.escidoc.core.domain.content.model.ContentModelResourcesTO;
import org.escidoc.core.domain.content.model.ContentModelPropertiesTO;
import org.escidoc.core.domain.content.model.ContentModelTO;
import org.escidoc.core.domain.version.VersionHistoryTO;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ResourceInUseException;
import de.escidoc.core.common.exceptions.system.SystemException;

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface ContentModelRestService {

    @PUT
    ContentModelTO create(ContentModelTO contentModelTO) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, MissingAttributeValueException, InvalidContentException,
        XmlCorruptedException, XmlSchemaValidationException;

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        ContentModelNotFoundException, MissingMethodParameterException, SystemException, LockingException,
        InvalidStatusException, ResourceInUseException;

    @GET
    @Path("/{id}")
    ContentModelTO retrieve(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        ContentModelNotFoundException, MissingMethodParameterException, SystemException;

    @GET
    @Path("/{id}/properties")
    ContentModelPropertiesTO retrieveProperties(@PathParam("id") String id) throws ContentModelNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException;

     @GET
     @Path("/{id}/resources")
     ContentModelResourcesTO retrieveResources(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
     ContentModelNotFoundException, MissingMethodParameterException, SystemException;

    @GET
    @Path("/{id}/resources/version-history")
    VersionHistoryTO retrieveVersionHistory(@PathParam("id") String id) throws AuthenticationException,
        AuthorizationException, ContentModelNotFoundException, MissingMethodParameterException, SystemException;

    @PUT
    @Path("/{id}")
    ContentModelTO update(@PathParam("id") String id, ContentModelTO contentModelTO) throws AuthenticationException,
        AuthorizationException, ContentModelNotFoundException, InvalidXmlException, MissingMethodParameterException,
        OptimisticLockingException, SystemException, ReadonlyVersionException, MissingAttributeValueException,
        InvalidContentException;

    // FIXME
    // @GET
    // @Path("/{id}")
    // EscidocBinaryContent retrieveMdRecordDefinitionSchemaContent(@PathParam("id") String id, String name)
    // throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
    // ContentModelNotFoundException, SystemException;

    // FIXME
    // @GET
    // @Path("/{id}")
    // EscidocBinaryContent retrieveResourceDefinitionXsltContent(@PathParam("id") String id, String name) throws
    // AuthenticationException,
    // AuthorizationException, MissingMethodParameterException, SystemException, ResourceNotFoundException;

}
