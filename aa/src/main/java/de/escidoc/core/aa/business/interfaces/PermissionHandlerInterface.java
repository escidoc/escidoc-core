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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.business.interfaces;

import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.common.business.interfaces.ResourceHandlerInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidScopeException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyActiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyDeactiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyRevokedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.xml.XmlUtility;

import java.util.List;
import java.util.Map;

/**
 * Common interface for all handlers dealing with permissions.
 *
 * @author André Schenk
 */
public interface PermissionHandlerInterface extends ResourceHandlerInterface {

    String XPATH_GRANT_ASSIGNED_ON =
        '/' + XmlUtility.NAME_GRANT + '/' + XmlUtility.NAME_PROPERTIES + '/' + XmlUtility.NAME_ASSIGNED_ON;

    String XPATH_GRANT_ROLE =
        '/' + XmlUtility.NAME_GRANT + '/' + XmlUtility.NAME_PROPERTIES + '/' + XmlUtility.NAME_ROLE;

    String MSG_WRONG_HREF = "Referenced object href is wrong, object has another type.";

    String MSG_GRANT_RESTRICTION_VIOLATED =
        "Grants can be created on containers, content models, contexts"
            + ", items, components, organizational units, user-accounts, "
            + "user-groups and scopes, only. No resource of one " + "of these types with the provided id exists.";

    String MSG_USER_NOT_FOUND_BY_ID = "User with provided id does not exist.";

    String MSG_USER_NOT_FOUND_BY_IDENTITY_INFO = "User with provided user identity does not exist.";

    String MSG_GROUP_NOT_FOUND_BY_ID = "User group with provided id does not exist.";

    String MSG_GRANT_NOT_FOUND_BY_ID = "Grant with provided id does not exist.";

    /**
     * Activate a resource.
     *
     * @param resourceId unique identifier of the resource
     * @param taskParam  XML representation of task parameters including the last modification date
     * @throws AlreadyActiveException         Thrown if the addressed resource is already activated.
     * @throws ResourceNotFoundException      Thrown if no resource with the provided id exists.
     * @throws XmlCorruptedException          Thrown if the provided XML data is invalid.
     * @throws MissingAttributeValueException Thrown if a mandatory attribute is not provided within the XML data.
     * @throws OptimisticLockingException     Thrown in case of an optimistic locking error.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    void activate(String resourceId, String taskParam) throws AlreadyActiveException, ResourceNotFoundException,
        XmlCorruptedException, MissingAttributeValueException, OptimisticLockingException, SystemException;

    /**
     * Deactivate a resource.
     *
     * @param resourceId unique identifier of the resource
     * @param taskParam  XML representation of task parameters including the last modification date
     * @throws AlreadyDeactiveException       Thrown if the addressed resource is already deactivated.
     * @throws ResourceNotFoundException      Thrown if no resource with the provided id exists.
     * @throws XmlCorruptedException          Thrown if the provided XML data is invalid.
     * @throws MissingAttributeValueException Thrown if a mandatory attribute is not provided within the XML data.
     * @throws OptimisticLockingException     Thrown in case of an optimistic locking error.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    void deactivate(String resourceId, String taskParam) throws AlreadyDeactiveException, ResourceNotFoundException,
        XmlCorruptedException, MissingAttributeValueException, OptimisticLockingException, SystemException;

    /**
     * Create a Grant for the resource (User Account or UserGroup).
     *
     * @param resourceId Resource ID (User Account or UserGroup)
     * @param grantXML   XML representation of the Grant to be created corresponding to XML-schema "grant.xsd"
     * @return XML representation of the created grant corresponding to XML-schema "grant.xsd"
     * @throws AlreadyExistsException    Thrown if the defined grant already exists for the resource.
     * @throws AuthenticationException   Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                   handle.
     * @throws AuthorizationException    Thrown if the authorization fails.
     * @throws ResourceNotFoundException Thrown if no resource with the provided id exists.
     * @throws InvalidScopeException     Thrown if given scope is not of object-type of allowed scopes for the role.
     * @throws XmlCorruptedException     Thrown if the provided XML data is invalid.
     * @throws SystemException           Thrown in case of an internal system error.
     */
    String createGrant(String resourceId, String grantXML) throws AlreadyExistsException, AuthenticationException,
        AuthorizationException, ResourceNotFoundException, XmlCorruptedException, InvalidScopeException,
        SystemException;

    /**
     * Retrieves the currently valid Grants for the resource (User Account or UserGroup) specified by resourceId in a
     * {@code Map}.
     *
     * @param resourceId Resource ID (User Account or UserGroup)
     * @return Returns the current grants of the resource in a {@code Map}.
     * @throws ResourceNotFoundException Thrown if no resource with the provided id exists.
     * @throws SystemException           Thrown in case of an internal system error.
     */
    Map<String, Map<String, List<RoleGrant>>> retrieveCurrentGrantsAsMap(String resourceId)
        throws ResourceNotFoundException, SystemException;

    /**
     * Retrieves the current grants for the provided ids in a List of Maps.
     *
     * @param resourceIds Resource IDs
     * @return Returns the current grants of the resources in a HashMap of Maps.
     * @throws SystemException Thrown in case of an internal system error.
     */
    Map<String, Map<String, Map<String, List<RoleGrant>>>> retrieveManyCurrentGrantsAsMap(List<String> resourceIds)
        throws SystemException;

    /**
     * Retrieve the Grants of the Resource (User Account or UserGroup) that are currently valid, i.e. that currently
     * have not been revoked.<br/>
     * <p/>
     * Retrieve the information about the Roles that currently have been granted to the resource including the
     * information for which objects the limited roles are granted.
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The resource must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The resource is accessed using the provided reference.</li> <li>The grants
     * corresponding to the Resource are accessed.</li> <li>The currently valid grants are filtered.</li> <li>The XML
     * representation of a list of Grants containing these Grant objects is created.</li> <li>The XML data is
     * returned.</li> </ul>
     *
     * @param resourceId The Resource ID (User Account or User Group).
     * @return The XML representation of the currently valid Grants of that Resource corresponding to XML-schema
     *         "grants.xsd".
     * @throws ResourceNotFoundException Thrown if no resource with the provided id exists.
     * @throws MissingMethodParameterException
     *                                   Thrown if no user id is provided.
     * @throws AuthenticationException   Thrown if the authentication fails due to an invalid provided
     *                                   eSciDocUserHandle.
     * @throws AuthorizationException    Thrown if the authorization fails.
     * @throws SystemException           Thrown in case of an internal system error.
     */
    String retrieveCurrentGrants(String resourceId) throws ResourceNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    /**
     * Revoke a Grant.
     *
     * @param groupId   User Group ID for that the Grant shall be revoked
     * @param grantId   Grant ID that shall be revoked
     * @param taskParam remark for the revocation of the Grant in an XML structure
     * @throws MissingMethodParameterException
     *                                        Thrown if parameter is missing.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided
     *                                        eSciDocUserHandle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws ResourceNotFoundException      Thrown if no resource with the provided id exists.
     * @throws AlreadyRevokedException        Thrown if the addressed grant is already revoked.
     * @throws XmlCorruptedException          Thrown if the provided XML data is invalid.
     * @throws MissingAttributeValueException Thrown if a mandatory attribute is not provided within the XML data.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    void revokeGrant(String groupId, String grantId, String taskParam) throws ResourceNotFoundException,
        AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieve the specified Grant of the User Group.<br/> This Grant contains information about a role that has been
     * granted to the User Group and specifies for which object this role has been granted, if the role is
     * limited.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Group must exist<br/>
     * <p/>
     * The Grant must exists and be contained in the list of grants of the User Group.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Group is accessed using the provided reference.</li> <li>The Grant is
     * identified using the provided Grant id.</li> <li>The Grant is identified using the provided Grant id.</li>
     * <li>The XML data for the Grant is created from the stored data.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param groupId User Group ID.
     * @param grantId Grant IDt that shall be retrieved.
     * @return The XML representation of the Grant corresponding to XML-schema "grant.xsd".
     * @throws ResourceNotFoundException Thrown if no resource with the provided id exists.
     * @throws MissingMethodParameterException
     *                                   Thrown if no user id or no grant id is provided.
     * @throws AuthenticationException   Thrown if the authentication fails due to an invalid provided
     *                                   eSciDocUserHandle.
     * @throws AuthorizationException    Thrown if the authorization fails.
     * @throws SystemException           Thrown in case of an internal system error.
     */
    String retrieveGrant(String groupId, String grantId) throws ResourceNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Revoke a list of Grants which match the given search criteria.
     *
     * @param resourceId id of the resource (User Account or User Group) for that Grants shall be revoked
     * @param filterXML  XML representation of the search filter to be used corresponding to XML-schema "filter.xsd"
     * @throws ResourceNotFoundException      Thrown if no resource with the provided id exists.
     * @throws AlreadyRevokedException        Thrown if the addressed grants are already revoked.
     * @throws XmlCorruptedException          Thrown if the provided XML data is invalid.
     * @throws MissingAttributeValueException Thrown if a mandatory attribute is not provided within the XML data.
     * @throws SystemException                Thrown in case of an internal system error.
     * @throws AuthorizationException         Thrown in case of an AuthorizationException.
     */
    void revokeGrants(String resourceId, String filterXML) throws ResourceNotFoundException, AlreadyRevokedException,
        XmlCorruptedException, MissingAttributeValueException, SystemException, AuthorizationException;
}
