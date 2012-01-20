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

import org.escidoc.core.domain.aa.RoleResourcesTO;
import org.escidoc.core.domain.aa.RoleTO;
import org.escidoc.core.utils.io.MimeTypes;

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

    /**
     * Create a role.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * No role with the same name (title) as it is provided in the data exists.<br/>
     * <p/>
     * See chapter 4 for detailed information about input and output data elements.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The role is accessed using the provided reference.</li> <li>The XML data is validated
     * against the XML-Schema of a role.</li> <li>It is checked whether no other role with the same name exists and if
     * the role name is allowed, i.e. it is not "Default-User".</li> <li>The role is created using the provided
     * data.</li> <li>Created-by, creation-date, modified-by, and last-modification-date are set.</li> <li>The role is
     * stored.</li> <li>The policy/policy set of the role is stored as provided. No checks on policy/policy set are
     * done.</li> <li>The XML representation of the created role corresponding to XML-schema is returned as output.</li>
     * </ul>
     *
     * @param roleTo The XML representation of the role to be created corresponding to XML-schema "role.xsd" as TO.
     * @return The XML representation of the created role corresponding to XML-schema "role.xsd" as TO, including the
     *         generated role id, the created-by, creation-date, modified-by, and last-modification-date. In case of
     *         REST, the XML representation contains the list of virtual subresources, too.
     * @throws XmlCorruptedException        Thrown if the provided xml data is invalid.
     * @throws XmlSchemaValidationException Thrown if the provided xml data is not schema conform.
     * @throws UniqueConstraintViolationException
     *                                      Thrown if the role name is not unique.
     * @throws MissingMethodParameterException
     *                                      Thrown if no role data has been provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided
     *                                      eSciDocUserHandle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal error.
     */
    @PUT
    RoleTO create(RoleTO roleTo) throws UniqueConstraintViolationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException;

    /**
     * Delete the specified role.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The role must exist<br/>
     * <p/>
     * The role must not be referenced by a role grant.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The role is accessed using the provided reference.</li> <li>The role is deleted.</li>
     * <li>No data is returned.</li> </ul>
     *
     * @param id The Role ID to be deleted.
     * @throws RoleNotFoundException       Thrown if no role with the provided id exists.
     * @throws RoleInUseViolationException Thrown if the role is referenced by a role grant.
     * @throws MissingMethodParameterException
     *                                     Thrown if no role id has been provided.
     * @throws AuthenticationException     Thrown if the authentication fails due to an invalid provided
     *                                     eSciDocUserHandle.
     * @throws AuthorizationException      Thrown if the authorization fails.
     * @throws SystemException             Thrown in case of an internal error.
     */
    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") String id) throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        RoleNotFoundException, RoleInUseViolationException, SystemException;

    /**
     * Retrieve the specified role.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The role must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The role is accessed using the provided reference. This identifier may be either the
     * Role ID or the Role Name.</li> <li>The XML representation of the role corresponding to XML-schema is returned as
     * TO.</li> </ul>
     *
     * @param id An unique identifier of the role to be retrieved, either the Role ID or the Role Name.
     * @return The XML representation of the retrieved role corresponding to XML-schema "role.xsd" as TO.
     * @throws RoleNotFoundException   Thrown if no role with the provided identifier exists.
     * @throws MissingMethodParameterException
     *                                 Thrown if no role identifier has been provided.
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided eSciDocUserHandle.
     * @throws AuthorizationException  Thrown if the authorization fails.
     * @throws SystemException         Thrown in case of an internal error.
     */
    @GET
    @Path("/{id}")
    RoleTO retrieve(@PathParam("id") String id) throws RoleNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    /**
     * Updated the specified role.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The role must exist<br/>
     * <p/>
     * See chapter 4 for detailed information about input and output data elements<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The role is accessed using the provided reference.</li> <li>Optimistic Locking
     * criteria is checked.</li> <li>The XML data is validated against the XML-Schema of a role.</li> <li>The provided
     * last-modification-date must match the last-modification-date currently saved in the system.</li> <li>It is
     * checked whether no other role with the same name exists and if the role name is allowed, i.e. it is not
     * "Default-User".</li> <li>The role is updated using the provided data.</li> <li>Modified-by and
     * last-modification-date are updated.</li> <li>The updated role is stored.</li> <li>The policy/policy set of the
     * role is replaced by the provided policy/policy set. No checks on policy/policy set are done.</li> <li>The XML
     * representation of the role corresponding to XML-schema is returned as output.</li> </ul>
     *
     * @param id  The Role ID to be updated.
     * @param roleTo The XML representation of the role to be updated corresponding to XML-schema "role.xsd" as TO.
     * @return Returns the XML representation of the updated role as TO.
     * @throws RoleNotFoundException          Thrown if no role with the provided id exists.
     * @throws XmlCorruptedException          Thrown if the provided xml data is not valid.
     * @throws XmlSchemaValidationException   Thrown if the provided xml data is not schema conform.
     * @throws MissingAttributeValueException Thrown if a mandatory attribute has not been set in the provided xml
     *                                        data.
     * @throws UniqueConstraintViolationException
     *                                        Thrown if the updated role name is not unique.
     * @throws OptimisticLockingException     Thrown in case of an optimistic locking error.
     * @throws MissingMethodParameterException
     *                                        Thrown if no role id has been provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                Thrown in case of an internal error.
     */
    @PUT
    @Path("/{id}")
    RoleTO update(@PathParam("id") String id, RoleTO roleTo) throws RoleNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingAttributeValueException, UniqueConstraintViolationException,
        OptimisticLockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException;

    /**
     * Retrieve the list of virtual resources of a role<br/>
     * <p/>
     * This methods returns a list of additional resources which aren't stored in AA but created on request by the
     * eSciDoc-Framework.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The role must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The role is accessed using the provided reference.</li> <li>Determine which resources
     * are available.</li> <li>Create the list of resources.</li> <li>The XML representation of the list of resources
     * corresponding to XML-schema is returned as output.</li> </ul>
     *
     * @param id The Role ID.
     * @return The XML representation of the resources of that role corresponding to XML-schema "role.xsd" as TO.
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided eSciDocUserHandle.
     * @throws AuthorizationException  Thrown if the authorization fails.
     * @throws MissingMethodParameterException
     *                                 Thrown if no role id has been provided.
     * @throws RoleNotFoundException   Thrown if no role with the provided id exists.
     * @throws SystemException         Thrown in case of an internal error.
     */
    @GET
    @Path("/{id}/resources")
    RoleResourcesTO retrieveResources(@PathParam("id") String id) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, RoleNotFoundException, SystemException;

}
