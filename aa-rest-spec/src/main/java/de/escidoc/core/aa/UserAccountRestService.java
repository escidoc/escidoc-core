/**
 * 
 */
package de.escidoc.core.aa;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.escidoc.core.domain.aa.CurrentGrantsTO;
import org.escidoc.core.domain.aa.GrantListTO;
import org.escidoc.core.domain.aa.GrantTO;
import org.escidoc.core.domain.aa.PermissionFilterTO;
import org.escidoc.core.domain.aa.UserAccountAttributeListTO;
import org.escidoc.core.domain.aa.UserAccountAttributeTO;
import org.escidoc.core.domain.aa.UserAccountListTO;
import org.escidoc.core.domain.aa.UserAccountPreferenceTO;
import org.escidoc.core.domain.aa.UserAccountPreferenceListTO;
import org.escidoc.core.domain.aa.UserAccountResourcesTO;
import org.escidoc.core.domain.aa.UserAccountTO;
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

@Path("")
@Produces(MimeTypes.TEXT_XML)
@Consumes(MimeTypes.TEXT_XML)
public interface UserAccountRestService {

    @PUT
    @Path("/user-account")
    UserAccountTO create(UserAccountTO userAccountTO) throws UniqueConstraintViolationException, InvalidStatusException,
    XmlCorruptedException, XmlSchemaValidationException, OrganizationalUnitNotFoundException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/user-account/{id}")
    UserAccountTO retrieve(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    @PUT
    @Path("/user-account/{id}")
    UserAccountTO update(@PathParam("id") String id, UserAccountTO userAccountTO) throws UserAccountNotFoundException,
    UniqueConstraintViolationException, InvalidStatusException, XmlCorruptedException,
    XmlSchemaValidationException, MissingMethodParameterException, MissingAttributeValueException,
    OptimisticLockingException, AuthenticationException, AuthorizationException,
    OrganizationalUnitNotFoundException, SystemException;

    @DELETE
    @Path("/user-account/{id}")
    void delete(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/user-account/current")
    UserAccountTO retrieveCurrentUser() throws UserAccountNotFoundException, AuthenticationException, AuthorizationException,
    SystemException;

    /**
     * FIXME taskParam
     * (use TO from task-param schema)
     */
    @POST
    @Path("/user-account/{id}/update-password")
    void updatePassword(@PathParam("id") String id, final String taskParam) throws UserAccountNotFoundException, InvalidStatusException,
    XmlCorruptedException, MissingMethodParameterException, OptimisticLockingException, AuthenticationException,
    AuthorizationException, SystemException;

    @PUT
    @Path("/user-account/{id}/resources/preferences")
    UserAccountPreferenceListTO updatePreferences(@PathParam("id") String id, UserAccountPreferenceListTO userAccountPrefrencesTO) throws UserAccountNotFoundException,
    XmlCorruptedException, XmlSchemaValidationException, OptimisticLockingException, SystemException,
    AuthenticationException, AuthorizationException, MissingMethodParameterException,
    MissingAttributeValueException;

    /**
     * FIXME taskParam
     * (use TO from task-param schema)
     */
    @POST
    @Path("/user-account/{id}/activate")
    void activate(@PathParam("id") String id, final String taskParam) throws AlreadyActiveException, UserAccountNotFoundException,
    XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
    OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * FIXME taskParam
     * (use TO from task-param schema)
     */
    @POST
    @Path("/user-account/{id}/deactivate")
    void deactivate(@PathParam("id") String id, final String taskParam) throws AlreadyDeactiveException, UserAccountNotFoundException,
    XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
    OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/user-account/{id}/resources")
    UserAccountResourcesTO retrieveResources(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/user-account/{id}/resources/current-grants")
    CurrentGrantsTO retrieveCurrentGrants(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    /**
     * FIXME Map
     */
    @GET
    @Path("/grants")
    GrantListTO retrieveGrants(final Map<String, String[]> filter) throws MissingMethodParameterException,
    InvalidSearchQueryException, AuthenticationException, AuthorizationException, SystemException;

    @PUT
    @Path("/user-account/{id}/resources/grants/grant")
    GrantTO createGrant(@PathParam("id") String id, final GrantTO grantTo) throws AlreadyExistsException, UserAccountNotFoundException,
    InvalidScopeException, RoleNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/user-account/{id}/resources/grants/grant/{grant-id}")
    GrantTO retrieveGrant(@PathParam("id") String id, @PathParam("grant-id") String grantId) throws UserAccountNotFoundException, GrantNotFoundException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * FIXME taskParam
     * (use TO from task-param schema)
     */
    @POST
    @Path("/user-account/{id}/resources/grants/grant/{grant-id}/revoke-grant")
    void revokeGrant(@PathParam("id") String id, @PathParam("grant-id") String grantId, final String taskParam) 
    throws UserAccountNotFoundException,
    GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * FIXME taskParam
     * (use TO from task-param schema)
     */
    @POST
    @Path("/user-account/{id}/resources/grants/revoke-grants")
    void revokeGrants(@PathParam("id") String id, final String taskParam) throws UserAccountNotFoundException, GrantNotFoundException,
    AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * FIXME Map
     */
    @GET
    @Path("/user-accounts")
    UserAccountListTO retrieveUerAccounts(final Map<String, String[]> filter) throws MissingMethodParameterException,
    AuthenticationException, AuthorizationException, InvalidSearchQueryException, SystemException;

    @GET
    @Path("/user-account/{id}/resources/preferences/preference/{name}")
    UserAccountPreferenceTO retrievePreference(@PathParam("id") String id, @PathParam("name") String name) throws UserAccountNotFoundException,
    PreferenceNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
    SystemException;

    @GET
    @Path("/user-account/{id}/resources/preferences")
    UserAccountPreferenceListTO retrievePreferences(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    @PUT
    @Path("/user-account/{id}/resources/preferences/preference")
    UserAccountPreferenceTO createPreference(@PathParam("id") String id, UserAccountPreferenceTO userAccountPreferenceTO)
    throws AlreadyExistsException,
    UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
    PreferenceNotFoundException;

    @PUT
    @Path("/user-account/{id}/resources/preferences/preference/{name}")
    UserAccountPreferenceTO updatePreference(
        @PathParam("id") String id, 
        @PathParam("name") String preferenceName, 
        UserAccountPreferenceTO userAccountPreferenceTO)
    throws AlreadyExistsException,
    UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
    PreferenceNotFoundException, OptimisticLockingException, MissingAttributeValueException;

    @DELETE
    @Path("/user-account/{id}/resources/preferences/preference/{name}")
    void deletePreference(@PathParam("id") String id, @PathParam("name") String preferenceName) throws UserAccountNotFoundException,
    PreferenceNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
    SystemException;

    @PUT
    @Path("/user-account/{id}/resources/attributes/attribute")
    UserAccountAttributeTO createAttribute(@PathParam("id") String id, UserAccountAttributeTO userAccountAttributeTO)
    throws AlreadyExistsException,
    UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/user-account/{id}/resources/attributes")
    UserAccountAttributeListTO retrieveAttributes(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    @GET
    @Path("/user-account/{id}/resources/attributes/{name}")
    UserAccountAttributeListTO retrieveNamedAttributes(@PathParam("id") String id, @PathParam("name") String name) 
    throws UserAccountNotFoundException,
    UserAttributeNotFoundException, MissingMethodParameterException, AuthenticationException,
    AuthorizationException, SystemException;

    @GET
    @Path("/user-account/{id}/resources/attributes/attribute/{att-id}")
    UserAccountAttributeTO retrieveAttribute(@PathParam("id") String id, @PathParam("att-id") String attId) 
    throws UserAccountNotFoundException,
    UserAttributeNotFoundException, MissingMethodParameterException, AuthenticationException,
    AuthorizationException, SystemException;

    @PUT
    @Path("/user-account/{id}/resources/attributes/attribute/{att-id}")
    UserAccountAttributeTO updateAttribute(
        @PathParam("id") String id, 
        @PathParam("att-id") String attId, 
        UserAccountAttributeTO userAccountAttributeTO)
    throws UserAccountNotFoundException,
    OptimisticLockingException, UserAttributeNotFoundException, ReadonlyElementViolationException,
    XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
    AuthorizationException, SystemException;

    @DELETE
    @Path("/user-account/{id}/resources/attributes/attribute/{att-id}")
    void deleteAttribute(@PathParam("id") String id, @PathParam("att-id") String attId) throws UserAccountNotFoundException,
    UserAttributeNotFoundException, ReadonlyElementViolationException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    /**
     * FIXME Map
     */
    @GET
    @Path("/user-account/retrievePermissionFilterQuery")
    PermissionFilterTO retrievePermissionFilterQuery(final Map<String, String[]> parameters) 
    throws SystemException,
    InvalidSearchQueryException, AuthenticationException, AuthorizationException;

}
