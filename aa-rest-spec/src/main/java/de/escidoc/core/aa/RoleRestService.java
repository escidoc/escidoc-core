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
 * Copyright 2006-2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
/**
 * 
 */
package de.escidoc.core.aa;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.escidoc.core.domain.aa.RoleListTO;
import org.escidoc.core.domain.aa.RoleResourcesTO;
import org.escidoc.core.domain.aa.RoleTO;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.RoleInUseViolationException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author Michael Hoppe
 * 
 */

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface RoleRestService {

    @PUT
    RoleTO create(RoleTO roleTo) throws UniqueConstraintViolationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException;

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id) throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        RoleNotFoundException, RoleInUseViolationException, SystemException;

    @GET
    @Path("/{id}")
    RoleTO retrieve(@PathParam("id") String id) throws RoleNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    @PUT
    @Path("/{id}")
    RoleTO update(@PathParam("id") String id, RoleTO roleTo) throws RoleNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingAttributeValueException, UniqueConstraintViolationException,
        OptimisticLockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException;

    @GET
    @Path("/{id}/resources")
    RoleResourcesTO retrieveResources(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, RoleNotFoundException, SystemException;

}
