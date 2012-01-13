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
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.escidoc.core.domain.aa.AddSelectorsTO;
import org.escidoc.core.domain.aa.CurrentGrantsTO;
import org.escidoc.core.domain.aa.GrantTO;
import org.escidoc.core.domain.aa.RemoveSelectorsTO;
import org.escidoc.core.domain.aa.UserGroupListTO;
import org.escidoc.core.domain.aa.UserGroupResourcesTO;
import org.escidoc.core.domain.aa.UserGroupSelectorsTO;
import org.escidoc.core.domain.aa.UserGroupTO;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.escidoc.core.domain.taskparam.OptimisticLockingTaskParamTO;
import org.escidoc.core.domain.taskparam.RevokeGrantTaskParamTO;
import org.escidoc.core.domain.taskparam.RevokeGrantsTaskParamTO;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidScopeException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.GrantNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyActiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyDeactiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyRevokedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.application.violated.UserGroupHierarchyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author Michael Hoppe
 * 
 */

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface UserGroupRestService {

    @PUT
    UserGroupTO create(UserGroupTO userGroupTO) throws UniqueConstraintViolationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException;

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id) throws UserGroupNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}")
    UserGroupTO retrieve(@PathParam("id") String id) throws UserGroupNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    @PUT
    @Path("/{id}")
    UserGroupTO update(@PathParam("id") String id, UserGroupTO userGroupTO) throws UserGroupNotFoundException,
        UniqueConstraintViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException;

    @POST
    @Path("/{id}/activate")
    void activate(@PathParam("id") String id, OptimisticLockingTaskParamTO taskParam) throws AlreadyActiveException, UserGroupNotFoundException,
        XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
        OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException;

    @POST
    @Path("/{id}/deactivate")
    void deactivate(@PathParam("id") String id, OptimisticLockingTaskParamTO taskParam) throws AlreadyDeactiveException, UserGroupNotFoundException,
        XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
        OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources/grants/grant/{grant-id}")
    GrantTO retrieveGrant(@PathParam("id") String id, @PathParam("grant-id") String grantId) throws UserGroupNotFoundException, GrantNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources/current-grants")
    CurrentGrantsTO retrieveCurrentGrants(@PathParam("id") String id) throws UserGroupNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    @PUT
    @Path("/{id}/resources/grants/grant")
    GrantTO createGrant(@PathParam("id") String id, GrantTO grantTo) throws AlreadyExistsException, UserGroupNotFoundException,
        InvalidScopeException, RoleNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @POST
    @Path("/{id}/resources/grants/grant/{grant-id}/revoke-grant")
    void revokeGrant(@PathParam("id") String id, @PathParam("grant-id") String grantId, RevokeGrantTaskParamTO taskParam) throws UserGroupNotFoundException,
        GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @POST
    @Path("/{id}/resources/grants/revoke-grants")
    void revokeGrants(@PathParam("id") String id, RevokeGrantsTaskParamTO taskParam) throws UserGroupNotFoundException, GrantNotFoundException,
        AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources")
    UserGroupResourcesTO retrieveResources(@PathParam("id") String id) throws UserGroupNotFoundException, SystemException;

    @POST
    @Path("/{id}/selectors-add")
    UserGroupSelectorsTO addSelectors(@PathParam("id") String id, AddSelectorsTO taskParam) throws OrganizationalUnitNotFoundException,
        UserAccountNotFoundException, UserGroupNotFoundException, InvalidContentException,
        MissingMethodParameterException, SystemException, AuthenticationException, AuthorizationException,
        OptimisticLockingException, XmlCorruptedException, XmlSchemaValidationException,
        UserGroupHierarchyViolationException;

    @POST
    @Path("/{id}/selectors-remove")
    UserGroupSelectorsTO removeSelectors(@PathParam("id") String id, RemoveSelectorsTO taskParam) throws XmlCorruptedException,
        XmlSchemaValidationException, AuthenticationException, AuthorizationException, SystemException,
        UserGroupNotFoundException, OptimisticLockingException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, UserAccountNotFoundException;
}
