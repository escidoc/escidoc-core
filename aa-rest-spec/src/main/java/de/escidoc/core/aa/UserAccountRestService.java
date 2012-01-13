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

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.escidoc.core.domain.aa.CurrentGrantsTO;
import org.escidoc.core.domain.aa.GrantTO;
import org.escidoc.core.domain.aa.PermissionFilterTO;
import org.escidoc.core.domain.aa.UserAccountAttributeListTO;
import org.escidoc.core.domain.aa.UserAccountAttributeTO;
import org.escidoc.core.domain.aa.UserAccountListTO;
import org.escidoc.core.domain.aa.UserAccountPreferenceListTO;
import org.escidoc.core.domain.aa.UserAccountPreferenceTO;
import org.escidoc.core.domain.aa.UserAccountResourcesTO;
import org.escidoc.core.domain.aa.UserAccountTO;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.escidoc.core.domain.taskparam.OptimisticLockingTaskParamTO;
import org.escidoc.core.domain.taskparam.RevokeGrantTaskParamTO;
import org.escidoc.core.domain.taskparam.RevokeGrantsTaskParamTO;
import org.escidoc.core.domain.taskparam.UpdatePasswordTaskParamTO;
import org.escidoc.core.utils.io.MimeTypes;

import de.escidoc.core.common.exceptions.application.invalid.InvalidScopeException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.GrantNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.PreferenceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAttributeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyActiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyDeactiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyRevokedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * @author Michael Hoppe
 * 
 */

@Path("/")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface UserAccountRestService {

    @PUT
    UserAccountTO create(UserAccountTO userAccountTO) throws UniqueConstraintViolationException, InvalidStatusException,
    XmlCorruptedException, XmlSchemaValidationException, OrganizationalUnitNotFoundException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}")
    UserAccountTO retrieve(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    @PUT
    @Path("/{id}")
    UserAccountTO update(@PathParam("id") String id, UserAccountTO userAccountTO) throws UserAccountNotFoundException,
    UniqueConstraintViolationException, InvalidStatusException, XmlCorruptedException,
    XmlSchemaValidationException, MissingMethodParameterException, MissingAttributeValueException,
    OptimisticLockingException, AuthenticationException, AuthorizationException,
    OrganizationalUnitNotFoundException, SystemException;

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/current")
    UserAccountTO retrieveCurrentUser() throws UserAccountNotFoundException, AuthenticationException, AuthorizationException,
    SystemException;

    @POST
    @Path("/{id}/update-password")
    void updatePassword(@PathParam("id") String id, UpdatePasswordTaskParamTO taskParam) throws UserAccountNotFoundException, InvalidStatusException,
    XmlCorruptedException, MissingMethodParameterException, OptimisticLockingException, AuthenticationException,
    AuthorizationException, SystemException;

    @PUT
    @Path("/{id}/resources/preferences")
    UserAccountPreferenceListTO updatePreferences(@PathParam("id") String id, UserAccountPreferenceListTO userAccountPrefrencesTO) throws UserAccountNotFoundException,
    XmlCorruptedException, XmlSchemaValidationException, OptimisticLockingException, SystemException,
    AuthenticationException, AuthorizationException, MissingMethodParameterException,
    MissingAttributeValueException;

    @POST
    @Path("/{id}/activate")
    void activate(@PathParam("id") String id, OptimisticLockingTaskParamTO taskParam) throws AlreadyActiveException, UserAccountNotFoundException,
    XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
    OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException;

    @POST
    @Path("/{id}/deactivate")
    void deactivate(@PathParam("id") String id, OptimisticLockingTaskParamTO taskParam) throws AlreadyDeactiveException, UserAccountNotFoundException,
    XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
    OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources")
    UserAccountResourcesTO retrieveResources(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources/current-grants")
    CurrentGrantsTO retrieveCurrentGrants(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    @PUT
    @Path("/{id}/resources/grants/grant")
    GrantTO createGrant(@PathParam("id") String id, GrantTO grantTo) throws AlreadyExistsException, UserAccountNotFoundException,
    InvalidScopeException, RoleNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources/grants/grant/{grant-id}")
    GrantTO retrieveGrant(@PathParam("id") String id, @PathParam("grant-id") String grantId) throws UserAccountNotFoundException, GrantNotFoundException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @POST
    @Path("/{id}/resources/grants/grant/{grant-id}/revoke-grant")
    void revokeGrant(@PathParam("id") String id, @PathParam("grant-id") String grantId, RevokeGrantTaskParamTO taskParam) 
    throws UserAccountNotFoundException,
    GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @POST
    @Path("/{id}/resources/grants/revoke-grants")
    void revokeGrants(@PathParam("id") String id, RevokeGrantsTaskParamTO taskParam) throws UserAccountNotFoundException, GrantNotFoundException,
    AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources/preferences/preference/{name}")
    UserAccountPreferenceTO retrievePreference(@PathParam("id") String id, @PathParam("name") String name) throws UserAccountNotFoundException,
    PreferenceNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
    SystemException;

    @GET
    @Path("/{id}/resources/preferences")
    UserAccountPreferenceListTO retrievePreferences(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    @PUT
    @Path("/{id}/resources/preferences/preference")
    UserAccountPreferenceTO createPreference(@PathParam("id") String id, UserAccountPreferenceTO userAccountPreferenceTO)
    throws AlreadyExistsException,
    UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
    PreferenceNotFoundException;

    @PUT
    @Path("/{id}/resources/preferences/preference/{name}")
    UserAccountPreferenceTO updatePreference(
        @PathParam("id") String id, 
        @PathParam("name") String preferenceName, 
        UserAccountPreferenceTO userAccountPreferenceTO)
    throws AlreadyExistsException,
    UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
    PreferenceNotFoundException, OptimisticLockingException, MissingAttributeValueException;

    @DELETE
    @Path("/{id}/resources/preferences/preference/{name}")
    void deletePreference(@PathParam("id") String id, @PathParam("name") String preferenceName) throws UserAccountNotFoundException,
    PreferenceNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
    SystemException;

    @PUT
    @Path("/{id}/resources/attributes/attribute")
    UserAccountAttributeTO createAttribute(@PathParam("id") String id, UserAccountAttributeTO userAccountAttributeTO)
    throws AlreadyExistsException,
    UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources/attributes")
    UserAccountAttributeListTO retrieveAttributes(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources/attributes/{name}")
    UserAccountAttributeListTO retrieveNamedAttributes(@PathParam("id") String id, @PathParam("name") String name) 
    throws UserAccountNotFoundException,
    UserAttributeNotFoundException, MissingMethodParameterException, AuthenticationException,
    AuthorizationException, SystemException;

    @GET
    @Path("/{id}/resources/attributes/attribute/{att-id}")
    UserAccountAttributeTO retrieveAttribute(@PathParam("id") String id, @PathParam("att-id") String attId) 
    throws UserAccountNotFoundException,
    UserAttributeNotFoundException, MissingMethodParameterException, AuthenticationException,
    AuthorizationException, SystemException;

    @PUT
    @Path("/{id}/resources/attributes/attribute/{att-id}")
    UserAccountAttributeTO updateAttribute(
        @PathParam("id") String id, 
        @PathParam("att-id") String attId, 
        UserAccountAttributeTO userAccountAttributeTO)
    throws UserAccountNotFoundException,
    OptimisticLockingException, UserAttributeNotFoundException, ReadonlyElementViolationException,
    XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
    AuthorizationException, SystemException;

    @DELETE
    @Path("/{id}/resources/attributes/attribute/{att-id}")
    void deleteAttribute(@PathParam("id") String id, @PathParam("att-id") String attId) throws UserAccountNotFoundException,
    UserAttributeNotFoundException, ReadonlyElementViolationException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/retrievePermissionFilterQuery")
    PermissionFilterTO retrievePermissionFilterQuery(
                        @QueryParam("index") Set<String> index, 
                        @QueryParam("user") Set<String> user, 
                        @QueryParam("role") Set<String> role) 
    throws SystemException,
    InvalidSearchQueryException, AuthenticationException, AuthorizationException;

}
