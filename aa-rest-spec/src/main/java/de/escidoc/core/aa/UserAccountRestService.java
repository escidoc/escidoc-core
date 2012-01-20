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
import org.escidoc.core.domain.aa.UserAccountPreferenceListTO;
import org.escidoc.core.domain.aa.UserAccountPreferenceTO;
import org.escidoc.core.domain.aa.UserAccountResourcesTO;
import org.escidoc.core.domain.aa.UserAccountTO;
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

    /**
     * Create an User Account representing an eSciDoc user.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * An User Account with the login name provided within the XML data does not exist.<br/>
     * <p/>
     * See chapter 6 for detailed information about input and output data elements.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>The XML data is
     * validated against the XML-Schema of an User Account ("user-account.xsd").</li> <li>It is checked if the chosen
     * login name is unique.</li> <li>It is checked that one of the provided Organizational Units of the user is marked
     * as the primary one.</li> <li>An User Account is created from the provided data including a generated internal
     * id.</li> <li>The new User Account is set to active.</li> <li>Created-by, creation-date, modified-by and
     * last-modification-date are added to the new User Account.</li> <li>The new User Account is stored.</li> <li>The
     * XML representation for the stored User Account is created.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param userAccountTO The XML representation of the User Account to be created corresponding to XML-schema
     *                "user-account.xsd" as TO.
     * @return The XML representation of the created User Account corresponding to XML-schema "user-account.xsd" as TO,
     *         including the generated user id, the created-by, creation-date, modified-by, and last-modification-date.
     *         In case of REST, the XML representation contains the list of virtual subresources, too.
     * @throws UniqueConstraintViolationException
     *                                      Thrown if the provided login name of the user is not unique.
     * @throws InvalidStatusException       Thrown if a referenced Organizational Unit is not in public status
     *                                      &quot;opened&quot;
     * @throws XmlCorruptedException        Thrown if the provided xml data is invalid
     * @throws XmlSchemaValidationException Thrown if the provided xml data is not schema conform
     * @throws OrganizationalUnitNotFoundException
     *                                      Thrown if an organizational unit referenced within the provided xml data
     *                                      does not exist.
     * @throws MissingMethodParameterException
     *                                      Thrown if no xml data is provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @PUT
    UserAccountTO create(UserAccountTO userAccountTO) throws UniqueConstraintViolationException, InvalidStatusException,
    XmlCorruptedException, XmlSchemaValidationException, OrganizationalUnitNotFoundException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieve the XML representation of an User Account representing an eSciDoc User.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Account must exist<br/> The loginname of the UserAccount may not be "current" as this is a reserved
     * String that is used by method retrieveCurrentUser.
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>The XML data for
     * that User Account is created.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param id An unique identifier of the User Account. This can either be the internal id, the login name, the
     *               email, or the eSciDocUserHandle which is generated when the user logs in to Escidoc.
     * @return The XML representation of the User Account corresponding to XML-schema "user-account.xsd" as TO.
     * @throws UserAccountNotFoundException Thrown if no User Account with the provided id exists.
     * @throws MissingMethodParameterException
     *                                      Thrown if no user identifier (userId) is provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @GET
    @Path("/{id}")
    UserAccountTO retrieve(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    /**
     * Update the data of an User Account object.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The User Account must exist<br/>
     * <p/>
     * See chapter 6 for detailed information about input and output data elements.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>Optimistic
     * Locking criteria is checked.</li> <li>The XML data is validated against the XML-Schema of an UserAccount.</li>
     * <li>The provided last-modification-date must match the last-modification-date currently saved in the system.</li>
     * <li>It is checked if the chosen login name is unique.</li> <li>It is checked that one of the provided
     * organizational-units of the user is marked as the primary one.</li> <li>The UserAccount is updated from the
     * provided data including updated modified-by and last-modification-date.</li> <li>The updated UserAccount is
     * stored.</li> <li>The XML data for the updated UserAccount is created.</li> <li>The XML data is returned.</li>
     * </ul>
     *
     * @param id  The User Account ID.
     * @param userAccountTO The XML representation of the User Account to be updated corresponding to XML-schema
     *                "user-account.xsd" as TO.
     * @return The XML representation of the updated User Account corresponding to XML-schema "user-account.xsd" as TO.
     * @throws UserAccountNotFoundException   Thrown if no User Account with the provided is exists.
     * @throws UniqueConstraintViolationException
     *                                        Thrown if the provided login name of the User Account is not unique.
     * @throws InvalidStatusException         Thrown if a referenced Organizational Unit is not in public status
     *                                        &quot;opened&quot;
     * @throws XmlCorruptedException          Thrown in case of invalid Xml.
     * @throws XmlSchemaValidationException   Thrown if the provided xml data is not schema conform
     * @throws MissingMethodParameterException
     *                                        Thrown if either no userId or no xml data is provided, i.e. it is
     *                                        {@code null.}
     * @throws MissingAttributeValueException Thrown if a mandatory attribute is not provided within the xml data.
     * @throws OptimisticLockingException     Thrown in case of an optimistic locking error.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws OrganizationalUnitNotFoundException
     *                                        Thrown if an organizational unit referenced in the provided xml data does
     *                                        not exist.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    @PUT
    @Path("/{id}")
    UserAccountTO update(@PathParam("id") String id, UserAccountTO userAccountTO) throws UserAccountNotFoundException,
    UniqueConstraintViolationException, InvalidStatusException, XmlCorruptedException,
    XmlSchemaValidationException, MissingMethodParameterException, MissingAttributeValueException,
    OptimisticLockingException, AuthenticationException, AuthorizationException,
    OrganizationalUnitNotFoundException, SystemException;

    /**
     * Deletes the specified user account. If the user account has references to other tables, an
     * SQLDatabaseSystemException is thrown and the user account is not deleted. References to other tables exist if: -
     * the user created roles, role-grants, user-account or user-groups. - the user modified roles, user-accounts or
     * user-groups. - the user revoked grants.
     *
     * @param id The User Account ID.
     * @throws UserAccountNotFoundException Thrown if no user account with the provided id exists.
     * @throws MissingMethodParameterException
     *                                      Thrown if no user id is provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieve the XML representation of the User Account of the current user as TO.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>The XML data for
     * that User Account is created.</li> <li>The XML data is returned as TO.</li> </ul>
     *
     * @return The XML representation of the User Account corresponding to XML-schema "user-account.xsd" as TO.
     * @throws UserAccountNotFoundException Thrown if no User is logged in.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @GET
    @Path("/current")
    UserAccountTO retrieveCurrentUser() throws UserAccountNotFoundException, AuthenticationException, AuthorizationException,
    SystemException;

    /**
     * Change the password of an User Account<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Account must exist<br/>
     * <p/>
     * The User Account is active.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>Optimistic
     * Locking criteria is checked.</li> <li>The password of the User Account is updated.</li> <li>No data is
     * returned.</li> </ul> <b>Parameter for request:</b> (example)<br/>
     * <p/>
     * <pre>
     * &lt;param last-modification-date=&quot;1967-08-13T12:00:00.000+01:00&quot; /&gt;
     * </pre>
     * <p/>
     * <pre>
     *     &lt;password&gt;password&lt;/password&gt;
     * </pre>
     * <p/>
     * <pre>
     * &lt; /param&gt;
     * </pre>
     *
     * @param id    The User Account ID.
     * @param taskParam The XML representation according to update-password-task-param.xsd of task parameters including 
     *                  the last modification date attribute and the new password as TO. (see above)
     * @throws InvalidStatusException       Thrown if the addressed User Account is not active.
     * @throws UserAccountNotFoundException Thrown if no User Account with the provided id exists.
     * @throws XmlCorruptedException        Thrown in case of invalid xml data (corrupt data, schema validation failed
     *                                      etc.)
     * @throws MissingMethodParameterException
     *                                      Thrown if no user id or no task parameters are provided.
     * @throws OptimisticLockingException   Thrown in case of an optimistic locking error.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @POST
    @Path("/{id}/update-password")
    void updatePassword(@PathParam("id") String id, UpdatePasswordTaskParamTO taskParam) throws UserAccountNotFoundException, InvalidStatusException,
    XmlCorruptedException, MissingMethodParameterException, OptimisticLockingException, AuthenticationException,
    AuthorizationException, SystemException;

    /**
     * Update the set of preferences associated with a User Account. The name of a preference must be unique for the
     * user.
     *
     * @param id         The User Account ID.
     * @param userAccountPrefrencesTO The XML representation of the set of preferences as TO.
     * @return The XML representation of the updated set of preferences as TO.
     * @throws UserAccountNotFoundException   If an User Account with the specified userId does not exist.
     * @throws XmlCorruptedException          If the XML representation is invalid.
     * @throws XmlSchemaValidationException   Thrown if the provided xml data is not schema conform
     * @throws SystemException                If an error occurs.
     * @throws OptimisticLockingException     If the give last modification timestamp does not match the current one.
     * @throws MissingMethodParameterException
     *                                        Thrown if no user id or no task parameters are provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws MissingAttributeValueException If there is no last modificate date attribute.
     */
    @PUT
    @Path("/{id}/resources/preferences")
    UserAccountPreferenceListTO updatePreferences(@PathParam("id") String id, UserAccountPreferenceListTO userAccountPrefrencesTO) throws UserAccountNotFoundException,
    XmlCorruptedException, XmlSchemaValidationException, OptimisticLockingException, SystemException,
    AuthenticationException, AuthorizationException, MissingMethodParameterException,
    MissingAttributeValueException;

    /**
     * Activate an User Account<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Account must exist<br/>
     * <p/>
     * The User Account is deactivated.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>Optimistic
     * Locking criteria is checked.</li> <li>The User Account is set to active.</li> <li>No data is returned.</li> </ul>
     * <b>Parameter for request:</b> (example)<br/>
     * <p/>
     * <pre>
     * &lt;param last-modification-date=&quot;1967-08-13T12:00:00.000+01:00&quot; /&gt;
     * </pre>
     *
     * @param id    The User Account ID to be acivated.
     * @param taskParam The XML representation of task parameters conforming to optimistic-locking-task-param.xsd as TO. 
     * Including the timestamp of the last modification of the Account (attribute 'last-modification-date', required). 
     * The last-modification-date is necessary for optimistic locking purpose. (example above)
     * @throws AlreadyActiveException         Thrown if the addressed User Account is active.
     * @throws UserAccountNotFoundException   Thrown if no User Account with the provided id exists.
     * @throws XmlCorruptedException          Thrown if the provided XML representation of task parameters are invalid.
     * @throws MissingMethodParameterException
     *                                        Thrown if no user id or no task parameters are provided.
     * @throws MissingAttributeValueException Thrown if the provided task parameter does not contain the last
     *                                        modification date.
     * @throws OptimisticLockingException     Thrown in case of an optimistic locking error.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                 eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    @POST
    @Path("/{id}/activate")
    void activate(@PathParam("id") String id, OptimisticLockingTaskParamTO taskParam) throws AlreadyActiveException, UserAccountNotFoundException,
    XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
    OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Deactivate an User Account<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Account must exist<br/>
     * <p/>
     * The User Account is active.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>Optimistic
     * Locking criteria is checked.</li> <li>The User Account is set to deactive.</li> <li>No data is returned.</li>
     * </ul> <b>Parameter for request:</b> (example)<br/>
     * <p/>
     * <pre>
     * &lt;param last-modification-date=&quot;1967-08-13T12:00:00.000+01:00&quot; /&gt;
     * </pre>
     *
     * @param id    The User Account ID to be deacivated.
     * @param taskParam The XML representation of task parameters conforming to optimistic-locking-task-param.xsd as TO. 
     * Including the timestamp of the last modification of the Account (attribute 'last-modification-date', required). 
     * The last-modification-date is necessary for optimistical locking purpose. (example above)
     * @throws AlreadyDeactiveException       Thrown if the addressed User Account is deactive.
     * @throws UserAccountNotFoundException   Thrown if no User Account with the provided id exists.
     * @throws XmlCorruptedException          Thrown in case of invalid xml data (corrupt data, schema validation failed
     *                                        etc.)
     * @throws MissingMethodParameterException
     *                                        Thrown if no user id or no task parameters are provided.
     * @throws MissingAttributeValueException Thrown if the provided task parameter does not contain the last
     *                                        modification date.
     * @throws OptimisticLockingException     Thrown in case of an optimistic locking error.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    @POST
    @Path("/{id}/deactivate")
    void deactivate(@PathParam("id") String id, OptimisticLockingTaskParamTO taskParam) throws AlreadyDeactiveException, UserAccountNotFoundException,
    XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
    OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieve the list of virtual Resources of an User Account.<br/>
     * <p/>
     * This methods returns a list of additional resources which aren't directly stored in the User Account but
     * retrieved or created on request by the eSciDoc-Framework.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Account must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>Determine which
     * resources are available.</li> <li>The XML data for the list of resources is created.</li> <li>The XML data is
     * returned.</li> </ul>
     *
     * @param id The User Account ID.
     * @return The XML representation of the resources of that User Account corresponding to XML-schema
     *         "resources.xsd" as TO.
     * @throws UserAccountNotFoundException Thrown if no User Account with the provided id exists.
     * @throws MissingMethodParameterException
     *                                      Thrown if no user id is provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @GET
    @Path("/{id}/resources")
    UserAccountResourcesTO retrieveResources(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieve the Grants of an User Account that are currently valid, i.e. that currently have not been revoked.<br/>
     * <p/>
     * Retrieve the information about the roles that currently have been granted to the User Account including the
     * information for which objects the limited roles are granted.
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The User Account must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>The grants
     * corresponding to the User Account are accessed.</li> <li>The currently valid grants are filtered.</li> <li>The
     * XML representation of a list of Grants containing these Grant objects is created.</li> <li>The XML data is
     * returned.</li> </ul>
     *
     * @param id The User Account ID.
     * @return The XML representation of the currently valid grants of that User Account corresponding to XML-schema
     *         "grants.xsd" as TO.
     * @throws UserAccountNotFoundException Thrown if no User Account with the provided id exists.
     * @throws MissingMethodParameterException
     *                                      Thrown if no user id is provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @GET
    @Path("/{id}/resources/current-grants")
    CurrentGrantsTO retrieveCurrentGrants(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    /**
     * Create a Grant for the User Account.<br> This Grant contains the information about a Role that is granted to the
     * User Account and specifies for which object this Role is granted, if the Role is limited.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The User Account must exist<br/>
     * <p/>
     * See chapter 6 for detailed information about input and output data elements.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>The XML data is
     * validated against the XML-Schema of a Grant ("grant.xsd").</li> <li>It is checked, that the referenced Role
     * exists.</li> <li>It is checked, that the referenced object (if any) exists and is of one of the supported
     * resource types.</li> <li>It is checked, that no Grant for the Role specified in the XML data (and for the
     * specified object, in case of a limited Role) exists for the User Account.</li> <li>The title of the referenced
     * object is fetched. If this fails, as a fallback, a default title is constructed using the resource type and the
     * id, e.g. "Context escidoc:12345".</li> <li>A Grant is created and associated with the User Account. Grant id,
     * created-by, creation-date, and the last-modification-date are added to the new Grant.</li> <li>The new grant is
     * stored.</li> <li>The XML representation of the new Grant is created.</li> <li>The XML data is returned.</li>
     * </ul>
     *
     * @param id   The User Account ID.
     * @param grantTo The XML representation of the Grant to be created corresponding to XML-schema "grant.xsd" as TO.
     * @return The XML representation of the created Grant corresponding to XML-schema "grant.xsd" as TO.
     * @throws AlreadyExistsException       Thrown if the defined grant already exists for the User Account.
     * @throws UserAccountNotFoundException Thrown if no User Account with the provided id exists.
     * @throws InvalidScopeException        Thrown if given Scope is not of object-type of allowed Scopes for the Role.
     * @throws RoleNotFoundException        Thrown if the Role referenced in the Grant does not exist.
     * @throws XmlCorruptedException        Thrown if the provided xml data is invalid.
     * @throws XmlSchemaValidationException Thrown if the provided xml data is not schema conform
     * @throws MissingMethodParameterException
     *                                      Thrown if no userId or no Grant XML representation is provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @PUT
    @Path("/{id}/resources/grants/grant")
    GrantTO createGrant(@PathParam("id") String id, GrantTO grantTo) throws AlreadyExistsException, UserAccountNotFoundException,
    InvalidScopeException, RoleNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieve the specified Grant of the User Account.<br/> This Grant contains information about a Role that has been
     * granted to the User Account and specifies for which object this Role has been granted, if the Role is
     * limited.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Account must exist<br/>
     * <p/>
     * The Grant must exist and be contained in the list of Grants of the User Account.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>The Grant is
     * identified using the provided Grant ID.</li> <li>The Grant is identified using the provided Grant ID.</li>
     * <li>The XML data for the Grant is created from the stored data.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param id  The User Account ID.
     * @param grantId The Grant ID that shall be retrieved.
     * @return The XML representation of the Grant corresponding to XML-schema "grant.xsd" as TO.
     * @throws UserAccountNotFoundException Thrown if no User Account with the provided id exists.
     * @throws GrantNotFoundException       Thrown if the specified Grant of the User Account cannot be found.
     * @throws MissingMethodParameterException
     *                                      Thrown if no User ID or no Grant ID is provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @GET
    @Path("/{id}/resources/grants/grant/{grant-id}")
    GrantTO retrieveGrant(@PathParam("id") String id, @PathParam("grant-id") String grantId) throws UserAccountNotFoundException, GrantNotFoundException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Revoke a Grant<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Account must exist<br/>
     * <p/>
     * The Grant must exist and be contained in the list of Grants of the User Account.<br/>
     * <p/>
     * The Grant must not be revoked.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>Optimistic
     * Locking criteria is checked.</li> <li>The Grant is identified using the provided Grant ID.</li> <li>The Grant is
     * set to "revoked". Revoked-by, revocation-date. and revocation-remark are added to the Grant. The
     * last-modification-date is updated.</li> <li>The updated Grant is stored.</li> <li>No data is returned.</li> </ul>
     * <b>Parameter for request:</b> (example)<br/>
     * <p/>
     * <pre>
     * &lt;param&gt;
     * </pre>
     * <p/>
     * <pre>
     *  &lt;revocation-remark&gt;Some revocation remark
     * </pre>
     * <p/>
     * <pre>
     *  &lt;/revocation-remark&gt;
     * </pre>
     * <p/>
     * <pre>
     * &lt;/param&gt;
     * </pre>
     *
     * @param id    The User Account ID for that a grant shall be revoked.
     * @param grantId   The Grant ID that shall be revoked.
     * @param taskParam The XML representation of task parameters conforming to revoke-grant-task-param.xsd as TO. 
     * Including the timestamp of the last modification (attribute 'last-modification-date', required, necessary for optimistical locking purpose)
     * and a revocation-remark. (see example above)
     * @throws UserAccountNotFoundException   Thrown if no User Account with the provided id exists.
     * @throws GrantNotFoundException         Thrown if the specified Grant of the User Account cannot be found.
     * @throws AlreadyRevokedException        Thrown if the addressed Grant is revoked.
     * @throws XmlCorruptedException          Thrown if the provided XML representation of the task parameters is
     *                                        invalid.
     * @throws MissingAttributeValueException Thrown if the last modification date is not provided in the task
     *                                        parameters.
     * @throws MissingMethodParameterException
     *                                        Thrown if at least on of the parameters is not provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    @POST
    @Path("/{id}/resources/grants/grant/{grant-id}/revoke-grant")
    void revokeGrant(@PathParam("id") String id, @PathParam("grant-id") String grantId, RevokeGrantTaskParamTO taskParam) 
    throws UserAccountNotFoundException,
    GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Revoke Grants<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Account must exist<br/>
     * <p/>
     * The Grants must exist and be contained in the list of Grants of the User Account.<br/>
     * <p/>
     * The Grants must not be revoked.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>The Grants are
     * identified using the provided ids in the filter-parameter.</li> <li>Revoke all Grants by providing no
     * filter-parameter.</li> <li>The Grants are set to "revoked". Revoked-by, revocation-date. and revocation-remark
     * are added to the Grants. The last-modification-date is updated.</li> <li>The updated Grants are stored.</li>
     * <li>No data is returned.</li> </ul> <b>Parameter for request:</b> (example)<br/>
     * <p/>
     * <pre>
     * &lt;param&gt;
     * </pre>
     * <p/>
     * <pre>
     * &lt;filter name=&quot;http://purl.org/dc/elements/1.1/identifier&quot;&gt;
     * </pre>
     * <p/>
     * <pre>
     * &lt;id&gt;escidoc:grant1&lt;/id&gt;
     * </pre>
     * <p/>
     * <pre>
     * &lt;/filter&gt;
     * </pre>
     * <p/>
     * <pre>
     *  &lt;revocation-remark&gt;Some revocation remark
     * </pre>
     * <p/>
     * <pre>
     *  &lt;/revocation-remark&gt;
     * </pre>
     * <p/>
     * <pre>
     * &lt;/param&gt;
     * </pre>
     *
     * @param id    The User Account ID for that a grant shall be revoked.
     * @param taskParam The XML representation of task parameters conforming to revoke-grants-task-param.xsd as TO. 
     * Containing the filter for grants to revoke and a revocation-remark.
     * The filter consists of an id and name (url), see example above.
     * @throws UserAccountNotFoundException   Thrown if no User Account with the provided ID exists.
     * @throws GrantNotFoundException         Thrown if the specified Grant of the User Account cannot be found.
     * @throws AlreadyRevokedException        Thrown if the addressed Grant is revoked.
     * @throws XmlCorruptedException          Thrown if the provided XML representation of the task parameters is
     *                                        invalid.
     * @throws MissingAttributeValueException Thrown if the last modification date is not provided in the task
     *                                        parameters.
     * @throws MissingMethodParameterException
     *                                        Thrown if at least on of the parameters is not provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    @POST
    @Path("/{id}/resources/grants/revoke-grants")
    void revokeGrants(@PathParam("id") String id, RevokeGrantsTaskParamTO taskParam) throws UserAccountNotFoundException, GrantNotFoundException,
    AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieves the user preference identified by given name associated to the User Account identified by the
     * provided eSciDoc User Handle.
     *
     * @param id         The User Account ID.
     * @param name The User Preference Name.
     * @return Returns user preference TO-object.
     * @throws MissingMethodParameterException
     *                                      Thrown if no XML representation of filter parameters is provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws UserAccountNotFoundException Thrown if no User Account can be identified by the provided handle.
     * @throws PreferenceNotFoundException  Thrown if the preference does not exist.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @GET
    @Path("/{id}/resources/preferences/preference/{name}")
    UserAccountPreferenceTO retrievePreference(@PathParam("id") String id, @PathParam("name") String name) throws UserAccountNotFoundException,
    PreferenceNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
    SystemException;

    /**
     * Retrieves user preference object associated to the User Account identified by the provided eSciDoc user
     * handle.
     *
     * @param id The User Account ID.
     * @return Returns user preference TO object.
     * @throws MissingMethodParameterException
     *                                      Thrown if no XML representation of filter parameters is provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws UserAccountNotFoundException Thrown if no User Account can be identified by the provided handle.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @GET
    @Path("/{id}/resources/preferences")
    UserAccountPreferenceListTO retrievePreferences(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    /**
     * Create a User Preference for the User Account.<br> This Preference contains a name and a value.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The User Account must exist<br/> The User Preference must not exist.<br/>
     * <p/>
     * See chapter 6 for detailed information about input and output data elements.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>The XML data is
     * validated against the XML-Schema of a User Preference ("preferences.xsd").</li> <li>A User Preference is created
     * and associated with the User Account.</li> <li>The new User Preference is stored.</li> <li>The XML representation
     * of the new User Preference is created.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param id        The User Account ID.
     * @param userAccountPreferenceTO The XML representation of the User Preference to be created corresponding to XML-schema
     *                      "preferences.xsd" as TO.
     * @return The XML representation of the created User Preference corresponding to XML-schema "preferences.xsd" as TO.
     * @throws AlreadyExistsException       Thrown if the defined User Preference already exists for the User Account.
     * @throws UserAccountNotFoundException Thrown if no User Account with the provided id exists.
     * @throws PreferenceNotFoundException  Thrown if the User Preference does not exist.
     * @throws XmlCorruptedException        Thrown if the provided xml data is invalid.
     * @throws XmlSchemaValidationException Thrown if the provided xml data is not schema conform
     * @throws MissingMethodParameterException
     *                                      Thrown if no User Account ID or no User Preference XML representation is
     *                                      provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @PUT
    @Path("/{id}/resources/preferences/preference")
    UserAccountPreferenceTO createPreference(@PathParam("id") String id, UserAccountPreferenceTO userAccountPreferenceTO)
    throws AlreadyExistsException,
    UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
    PreferenceNotFoundException;

    /**
     * Update a User Preference for the User Account.<br> The User Preference contains a name and a value.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The User Account must exist<br/> The User Preference for the User Account must exist<br/>
     * <p/>
     * See chapter 6 for detailed information about input and output data elements.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>The XML data is
     * validated against the XML-Schema of a User Preference ("preferences.xsd").</li> <li>The User Preference is
     * accessed and updated.</li> <li>The User Preference is stored.</li> <li>The XML representation of the changed User
     * Preference is created.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param id         The User Account ID.
     * @param preferenceName The User Preference Name.
     * @param userAccountPreferenceTO  The XML representation of the User Preference to be created corresponding to XML-schema
     *                       "preferences.xsd" as TO.
     * @return The XML representation of the created User Preference corresponding to XML-schema "preferences.xsd" as TO.
     * @throws AlreadyExistsException         Thrown if the defined User Preference already exists for the User
     *                                        Account.
     * @throws UserAccountNotFoundException   Thrown if no User Account with the provided User Account ID exists.
     * @throws PreferenceNotFoundException    Thrown if the User Preference does not exist.
     * @throws XmlCorruptedException          Thrown if the provided xml data is invalid.
     * @throws XmlSchemaValidationException   Thrown if the provided xml data is not schema conform
     * @throws MissingMethodParameterException
     *                                        Thrown if no User Account ID or no User Preference XML representation is
     *                                        provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                Thrown in case of an internal system error.
     * @throws OptimisticLockingException     If the give last modification timestamp does not match the current one.
     * @throws MissingAttributeValueException If there is no last modificate date attribute.
     */
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

    /**
     * Remove a User Preference from the User Account.<br> The User Preference is identified by its name within the User
     * Account.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Account must exist<br/> The User Preference for the User Account must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>The User
     * Preference is accessed and removed.</li> </ul>
     *
     * @param id         The User Account ID.
     * @param preferenceName The User Preference Name.
     * @throws UserAccountNotFoundException Thrown if no User Account with the provided User Account ID exists.
     * @throws PreferenceNotFoundException  Thrown if the User Preference does not exist.
     * @throws MissingMethodParameterException
     *                                      Thrown if no User Account ID or no User Preference Name is provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @DELETE
    @Path("/{id}/resources/preferences/preference/{name}")
    void deletePreference(@PathParam("id") String id, @PathParam("name") String preferenceName) throws UserAccountNotFoundException,
    PreferenceNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
    SystemException;

    /**
     * Create an User Attribute for the User Account.<br> This User Attribute contains a name and a value.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The user account must exist.<br/>
     * <p/>
     * See chapter 6 for detailed information about input and output data elements.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>The XML data is
     * validated against the XML-Schema of a User Attribute ("user-attributes.xsd").</li> <li>An User Attribute is
     * created and associated with the User Account.</li> <li>The new User Attribute is stored.</li> <li>The XML
     * representation of the new User Attribute is created.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param id       The User Account ID.
     * @param userAccountAttributeTO The XML representation of the User Attribute to be created corresponding to XML-schema
     *                     "user-attributes.xsd" as TO.
     * @return The XML representation of the created User Attribute corresponding to XML-schema "user-attributes.xsd" as TO.
     * @throws AlreadyExistsException       Thrown if the defined User Attribute already exists for the User Account.
     * @throws UserAccountNotFoundException Thrown if no User Account with the provided User Account ID exists.
     * @throws XmlCorruptedException        Thrown if the provided xml data is invalid.
     * @throws XmlSchemaValidationException Thrown if the provided xml data is not schema conform
     * @throws MissingMethodParameterException
     *                                      Thrown if no User Account ID or no User Attribute XML representation is
     *                                      provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @PUT
    @Path("/{id}/resources/attributes/attribute")
    UserAccountAttributeTO createAttribute(@PathParam("id") String id, UserAccountAttributeTO userAccountAttributeTO)
    throws AlreadyExistsException,
    UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
    MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieves user attribute objects associated to the User Account identified by the provided User Account
     * ID.
     *
     * @param id The User Account ID.
     * @return Returns user attribute TO objects.
     * @throws MissingMethodParameterException
     *                                      Thrown if no User Account ID is provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws UserAccountNotFoundException Thrown if no User Account can be identified by the provided User Account
     *                                      ID.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @GET
    @Path("/{id}/resources/attributes")
    UserAccountAttributeListTO retrieveAttributes(@PathParam("id") String id) throws UserAccountNotFoundException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieves user attribute objects associated to the User Account identified by the provided User Account ID
     * and the provided User Attribute Name.
     *
     * @param id The User Account ID.
     * @param name   The User Attribute Name to be retrieved.
     * @return Returns user attribute TO objects.
     * @throws MissingMethodParameterException
     *                                        Thrown if no User Account ID or User Attribute Name is provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws UserAccountNotFoundException   Thrown if no User Account can be identified by the provided User Account
     *                                        ID.
     * @throws UserAttributeNotFoundException Thrown if the given User Attribute could not be found.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    @GET
    @Path("/{id}/resources/attributes/{name}")
    UserAccountAttributeListTO retrieveNamedAttributes(@PathParam("id") String id, @PathParam("name") String name) 
    throws UserAccountNotFoundException,
    UserAttributeNotFoundException, MissingMethodParameterException, AuthenticationException,
    AuthorizationException, SystemException;

    /**
     * Retrieves the user attribute TO object associated to the User Account identified by the provided User Account
     * ID and the provided User Attribute ID.
     *
     * @param id      The User Account ID.
     * @param attId The User Attribute ID to be retrieved.
     * @return Returns the user attribute TO object.
     * @throws MissingMethodParameterException
     *                                        Thrown if no User Account ID or User Attribute ID is provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws UserAccountNotFoundException   Thrown if no User Account can be identified by the provided User Account
     *                                        ID.
     * @throws UserAttributeNotFoundException Thrown if the given User Attribute could not be found.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    @GET
    @Path("/{id}/resources/attributes/attribute/{att-id}")
    UserAccountAttributeTO retrieveAttribute(@PathParam("id") String id, @PathParam("att-id") String attId) 
    throws UserAccountNotFoundException,
    UserAttributeNotFoundException, MissingMethodParameterException, AuthenticationException,
    AuthorizationException, SystemException;

    /**
     * Update an User Attribute for the User Account.<br> This User Attribute contains a name and a value.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The User Account must exist.<br/>
     * <p/>
     * See chapter 6 for detailed information about input and output data elements.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>The XML data is
     * validated against the XML-Schema of a User Attribute ("user-attributes.xsd").</li> <li>An User Attribute is
     * updated.</li> <li>The modified User Attribute is stored.</li> <li>The XML representation of the User Attribute is
     * created.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param id       The User Account ID.
     * @param attId  The User Attribute ID to be updated.
     * @param userAccountAttributeTO The XML representation of the User Attribute to be updated corresponding to XML-schema
     *                     "user-attributes.xsd" as TO.
     * @return The XML representation of the updated User Attribute corresponding to XML-schema "user-attributes.xsd" as TO.
     * @throws UserAccountNotFoundException   Thrown if no User Account with the provided User Account ID exists.
     * @throws XmlCorruptedException          Thrown if the provided xml data is invalid.
     * @throws XmlSchemaValidationException   Thrown if the provided xml data is not schema conform
     * @throws MissingMethodParameterException
     *                                        Thrown if no User Account ID or no User Attribute XML representation is
     *                                        provided.
     * @throws UserAttributeNotFoundException Thrown if the given User Attribute could not be found.
     * @throws ReadonlyElementViolationException
     *                                        Thrown if the User Attribute is set to read only.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                Thrown in case of an internal system error.
     * @throws OptimisticLockingException     If the give last modification timestamp does not match the current one.
     */
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

    /**
     * Remove an User Attribute of the User Account.<br> The User Attribute is identified by it's ID within the User
     * Account.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Account must exist.<br/> The User Attribute for the User Account must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Account is accessed using the provided reference.</li> <li>The User
     * Attribute is accessed and removed.</li> </ul>
     *
     * @param id      The User Account ID.
     * @param attId The User Attribute ID to be updated.
     * @throws UserAccountNotFoundException   Thrown if no User Account with the provided ID exists.
     * @throws UserAttributeNotFoundException Thrown if the User Attribute does not exist.
     * @throws ReadonlyElementViolationException
     *                                        Thrown if the User Attribute is set to read only.
     * @throws MissingMethodParameterException
     *                                        Thrown if no User Account ID or no User Attribute ID is provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    @DELETE
    @Path("/{id}/resources/attributes/attribute/{att-id}")
    void deleteAttribute(@PathParam("id") String id, @PathParam("att-id") String attId) throws UserAccountNotFoundException,
    UserAttributeNotFoundException, ReadonlyElementViolationException, MissingMethodParameterException,
    AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieves a filter statement which contains the permission rules which later can be added to a user given filter
     * statement to ensure only those resources are visible for which the user has the necessary access rights.
     *
     * @param index GET-Parameter index
     * @param user GET-Parameter index
     * @param role GET-Parameter index
     * @return filter sub query with permission rules as TO
     * @throws SystemException             Thrown in case of an internal system error.
     * @throws InvalidSearchQueryException Thrown if the given search query could not be translated into a SQL query.
     * @throws AuthenticationException     Thrown if the authentication fails due to an invalid provided
     *                                     eSciDocUserHandle.
     * @throws AuthorizationException      Thrown if the authorization fails.
     */
    @GET
    @Path("/retrievePermissionFilterQuery")
    PermissionFilterTO retrievePermissionFilterQuery(
                        @QueryParam("index") Set<String> index, 
                        @QueryParam("user") Set<String> user, 
                        @QueryParam("role") Set<String> role) 
    throws SystemException,
    InvalidSearchQueryException, AuthenticationException, AuthorizationException;

}
