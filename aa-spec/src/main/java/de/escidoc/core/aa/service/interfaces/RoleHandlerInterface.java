/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
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

package de.escidoc.core.aa.service.interfaces;

import de.escidoc.core.common.annotation.Validate;
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

import java.util.Map;

/**
 * Interface of a handler managing eSciDoc roles.
 *
 * @author Torsten Tetteroo
 */
public interface RoleHandlerInterface {

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
     * @param xmlData The XML representation of the role to be created corresponding to XML-schema "role.xsd".
     * @return The XML representation of the created role corresponding to XML-schema "role.xsd", including the
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
    @Validate(param = 0, resolver = "getRoleSchemaLocation")
    String create(String xmlData) throws UniqueConstraintViolationException, XmlCorruptedException,
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
     * @param roleId The Role ID to be deleted.
     * @throws RoleNotFoundException       Thrown if no role with the provided id exists.
     * @throws RoleInUseViolationException Thrown if the role is referenced by a role grant.
     * @throws MissingMethodParameterException
     *                                     Thrown if no role id has been provided.
     * @throws AuthenticationException     Thrown if the authentication fails due to an invalid provided
     *                                     eSciDocUserHandle.
     * @throws AuthorizationException      Thrown if the authorization fails.
     * @throws SystemException             Thrown in case of an internal error.
     */
    void delete(String roleId) throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
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
     * output.</li> </ul>
     *
     * @param identifier An unique identifier of the role to be retrieved, either the Role ID or the Role Name.
     * @return The XML representation of the retrieved role corresponding to XML-schema "role.xsd".
     * @throws RoleNotFoundException   Thrown if no role with the provided identifier exists.
     * @throws MissingMethodParameterException
     *                                 Thrown if no role identifier has been provided.
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided eSciDocUserHandle.
     * @throws AuthorizationException  Thrown if the authorization fails.
     * @throws SystemException         Thrown in case of an internal error.
     */
    String retrieve(String identifier) throws RoleNotFoundException, MissingMethodParameterException,
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
     * @param roleId  The Role ID to be updated.
     * @param xmlData The XML representation of the role to be updated corresponding to XML-schema "role.xsd".
     * @return Returns the XML representation of the updated role.
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
    @Validate(param = 1, resolver = "getRoleSchemaLocation")
    String update(String roleId, String xmlData) throws RoleNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingAttributeValueException, UniqueConstraintViolationException,
        OptimisticLockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException;

    //
    // Subresources
    //

    //
    // Subresource - resources
    //

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
     * @param roleId The Role ID.
     * @return The XML representation of the resources of that role corresponding to XML-schema "role.xsd".
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided eSciDocUserHandle.
     * @throws AuthorizationException  Thrown if the authorization fails.
     * @throws MissingMethodParameterException
     *                                 Thrown if no role id has been provided.
     * @throws RoleNotFoundException   Thrown if no role with the provided id exists.
     * @throws SystemException         Thrown in case of an internal error.
     */
    String retrieveResources(String roleId) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, RoleNotFoundException, SystemException;

    /**
     * Retrieves a filtered list of roles. <br/>
     * <p/>
     * Default (and for now, the only) format is a list of full role representations. <br/>Access rights are not checked
     * per role instance, but it is checked, whether the user is allowed to retrieve a list of roles.<br/> <br/> NOTE:
     * URI-Like Filters are deprecated and will be removed in the next version of the core-framework. Please use the new
     * PATH-like filters (eg /id instead of http://purl.org/dc/elements/1.1/identifier). For further information about
     * the filter-names, please see the explain-plan.<br/> <b>Additional filters valid for this method:</b><br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>Check whether the user is allowed to perform the action "retrieve-roles" <li>Check
     * whether all filter names are valid.</li> <li>The roles are accessed using the provided filters.</li> <li>The XML
     * representation of the list of roles corresponding to XML-schema is returned as output.</li> </ul> <br/> See
     * chapter "Filters" for detailed information about filter definitions.<br/> <b>Additional filters valid for this
     * method:</b><br/> <ul> <li>limited<br/> retrieves all roles that are limited roles (value = true) or unlimited
     * roles (value = false).</li> <li>granted<br/> retrieves all roles that have been granted to a user (value = true)
     * or all roles that have never been granted to a user (value = false).</li> </ul>
     *
     * @param filter map of key - value pairs containing the filter definition. See functional specification.
     * @return Returns an XML representation of a list of roles.
     * @throws MissingMethodParameterException
     *                                     Thrown if no task parameter has been provided.
     * @throws AuthenticationException     Thrown if the authentication fails due to an invalid provided
     *                                     eSciDocUserHandle.
     * @throws AuthorizationException      Thrown if the authorization fails.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SystemException             Thrown in case of an internal error.
     */
    String retrieveRoles(Map<String, String[]> filter) throws MissingMethodParameterException, AuthenticationException,
        AuthorizationException, InvalidSearchQueryException, SystemException;
}
