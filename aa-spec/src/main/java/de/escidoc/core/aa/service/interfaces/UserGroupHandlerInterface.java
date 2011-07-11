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

import java.util.Map;

/**
 * The interface for access to a User Group resource.
 *
 * @author André Schenk
 */
public interface UserGroupHandlerInterface {

    /**
     * Create a User Group object.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * See chapter 6 for detailed information about input and output data elements.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Group is accessed using the provided reference.</li> <li>The XML data is
     * validated against the XML schema of a User Group ("user-group.xsd").</li> <li>A User Group is created from the
     * provided data including a generated internal id.</li> <li>The new User Group is set to active.</li>
     * <li>Created-by, creation-date, modified-by and last-modification-date are added to the new User Group.</li>
     * <li>The new User Group is stored.</li> <li>The XML representation for the stored User Group is created.</li>
     * <li>The XML data is returned.</li> </ul>
     *
     * @param xmlData The XML representation of the User Group to be created corresponding to XML schema
     *                "user-group.xsd".
     * @return The XML representation of the created User Group corresponding to XML schema "user-group.xsd", including
     *         the generated User Group ID, the created-by, creation-date, modified-by, and last-modification-date. In
     *         case of REST, the XML representation contains the list of virtual subresources, too.
     * @throws UniqueConstraintViolationException
     *                                      Thrown if the provided label of the User Group is not unique.
     * @throws XmlCorruptedException        Thrown if the provided XML data is invalid
     * @throws XmlSchemaValidationException Thrown if the provided XML data is not schema conform
     * @throws MissingMethodParameterException
     *                                      Thrown if no XML data is provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                      handle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @Validate(param = 0, resolver = "getUserGroupSchemaLocation")
    String create(String xmlData) throws UniqueConstraintViolationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException;

    /**
     * Delete the specified User Group.
     *
     * @param groupId The User Group ID.
     * @throws UserGroupNotFoundException Thrown if no User Group with the provided id exists.
     * @throws MissingMethodParameterException
     *                                    Thrown if no User Group ID is provided.
     * @throws AuthenticationException    Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                    handle.
     * @throws AuthorizationException     Thrown if the authorization fails.
     * @throws SystemException            Thrown in case of an internal system error.
     */
    void delete(String groupId) throws UserGroupNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieve the XML representation of a User Group object representing an eSciDoc User Group.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Group must exist.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Group is accessed using the provided reference.</li> <li>The XML data for
     * that User Group is created.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param groupId The User Group ID.
     * @return The XML representation of the User Group corresponding to XML schema "user-group.xsd".
     * @throws UserGroupNotFoundException Thrown if no User Group with the provided ID exists.
     * @throws MissingMethodParameterException
     *                                    Thrown if no User Group ID is provided.
     * @throws AuthenticationException    Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                    handle.
     * @throws AuthorizationException     Thrown if the authorization fails.
     * @throws SystemException            Thrown in case of an internal system error.
     */
    String retrieve(String groupId) throws UserGroupNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    /**
     * Update the data of a User Group.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The User Group must exist.<br/>
     * <p/>
     * See chapter 6 for detailed information about input and output data elements.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Group is accessed using the provided reference.</li> <li>Optimistic Locking
     * criteria is checked.</li> <li>The XML data is validated against the XML schema of a User Group.</li> <li>The
     * provided last-modification-date must match the last-modification-date currently saved in the system.</li> <li>It
     * is checked if the group label is unique.</li> <li>The User Group is updated from the provided data including
     * updated modified-by and last-modification-date.</li> <li>The updated User Group is stored.</li> <li>The XML data
     * for the updated User Group is created.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param groupId The User Group ID.
     * @param xmlData The XML representation of the User Group to be updated corresponding to XML schema
     *                "user-group.xsd".
     * @return The XML representation of the updated User Group corresponding to XML schema "user-group.xsd".
     * @throws UserGroupNotFoundException     Thrown if no User Group with the provided ID exists.
     * @throws UniqueConstraintViolationException
     *                                        Thrown if the provided label of the User Group is not unique.
     * @throws XmlCorruptedException          Thrown in case of invalid XML.
     * @throws XmlSchemaValidationException   Thrown if the provided XML data is not schema conform
     * @throws MissingMethodParameterException
     *                                        Thrown if either no User Group ID or no XML data is provided, i.e. it is
     *                                        {@code null.}
     * @throws MissingAttributeValueException Thrown if a mandatory attribute is not provided within the XML data.
     * @throws OptimisticLockingException     Thrown in case of an optimistic locking error.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                        handle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    @Validate(param = 1, resolver = "getUserGroupSchemaLocation")
    String update(String groupId, String xmlData) throws UserGroupNotFoundException,
        UniqueConstraintViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException;

    /**
     * Activate a User Group.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Group must exist.<br/>
     * <p/>
     * The User Group is deactived.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Group is accessed using the provided reference.</li> <li>Optimistic Locking
     * criteria is checked.</li> <li>The User Group is set to active.</li> <li>No data is returned.</li> </ul>
     * <b>Parameter for request:</b> (example)<br/>
     * <p/>
     * <pre>
     * &lt;param last-modification-date=&quot;1967-08-13T12:00:00.000+01:00&quot; /&gt;
     * </pre>
     *
     * @param groupId   The User Group ID to be activated.
     * @param taskParam The XML representation of task parameters including the last modification date. (see above)
     * @throws AlreadyActiveException         Thrown if the addressed User Group is active.
     * @throws UserGroupNotFoundException     Thrown if no User Group with the provided id exists.
     * @throws XmlCorruptedException          Thrown if the provided XML representation of task parameters are invalid.
     * @throws MissingMethodParameterException
     *                                        Thrown if no User Group ID or no task parameters are provided.
     * @throws MissingAttributeValueException Thrown if the provided task parameter does not contain the last
     *                                        modification date.
     * @throws OptimisticLockingException     Thrown in case of an optimistic locking error.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                        handle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    void activate(String groupId, String taskParam) throws AlreadyActiveException, UserGroupNotFoundException,
        XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
        OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Deactivate a User Group.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Group must exist.<br/>
     * <p/>
     * The User Group is active.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Group is accessed using the provided reference.</li> <li>Optimistic Locking
     * criteria is checked.</li> <li>The User Group is set to deactive.</li> <li>No data is returned.</li> </ul>
     * <b>Parameter for request:</b> (example)<br/>
     * <p/>
     * <pre>
     * &lt;param last-modification-date=&quot;1967-08-13T12:00:00.000+01:00&quot; /&gt;
     * </pre>
     *
     * @param groupId   The User Group ID to be deacivated.
     * @param taskParam The XML representation of task parameters including the last modification date. (see above)
     * @throws AlreadyDeactiveException       Thrown if the addressed User Group is deactive.
     * @throws UserGroupNotFoundException     Thrown if no User Group with the provided id exists.
     * @throws XmlCorruptedException          Thrown in case of invalid XML data (corrupt data, schema validation failed
     *                                        etc.)
     * @throws MissingMethodParameterException
     *                                        Thrown if no User Group ID or no task parameters are provided.
     * @throws MissingAttributeValueException Thrown if the provided task parameter does not contain the last
     *                                        modification date.
     * @throws OptimisticLockingException     Thrown in case of an optimistic locking error.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                        handle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    void deactivate(String groupId, String taskParam) throws AlreadyDeactiveException, UserGroupNotFoundException,
        XmlCorruptedException, MissingMethodParameterException, MissingAttributeValueException,
        OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException;

    //
    // Subresource - current grants
    //

    /**
     * Retrieve the specified Grant of the User Group.<br/> This Grant contains information about a Role that has been
     * granted to the User Group and specifies for which object this Role has been granted, if the Role is
     * limited.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Group must exist<br/>
     * <p/>
     * The Grant must exists and be contained in the list of Grants of the User Group.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Group is accessed using the provided reference.</li> <li>The Grant is
     * identified using the provided Grant id.</li> <li>The Grant is identified using the provided Grant id.</li>
     * <li>The XML data for the Grant is created from the stored data.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param groupId The User Group ID.
     * @param grantId The Grant ID that shall be retrieved.
     * @return The XML representation of the Grant corresponding to XML-schema "grant.xsd".
     * @throws UserGroupNotFoundException Thrown if no User Group with the provided id exists.
     * @throws GrantNotFoundException     Thrown if the specified Grant of the User Group cannot be found.
     * @throws MissingMethodParameterException
     *                                    Thrown if no User Group ID or no Grant ID is provided.
     * @throws AuthenticationException    Thrown if the authentication fails due to an invalid provided
     *                                    eSciDocUserHandle.
     * @throws AuthorizationException     Thrown if the authorization fails.
     * @throws SystemException            Thrown in case of an internal system error.
     */
    String retrieveGrant(String groupId, String grantId) throws UserGroupNotFoundException, GrantNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieve the Grants of an User Group that are currently valid, i.e. that currently have not been revoked.<br/>
     * <p/>
     * Retrieve the information about the Roles that currently have been granted to the User Group including the
     * information for which objects the limited roles are granted.
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The User Group must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Group is accessed using the provided reference.</li> <li>The grants
     * corresponding to the UserGroup are accessed.</li> <li>The currently valid grants are filtered.</li> <li>The XML
     * representation of a list of Grants containing these Grant objects is created.</li> <li>The XML data is
     * returned.</li> </ul>
     *
     * @param groupId The User Group ID.
     * @return The XML representation of the currently valid Grants of that User Group corresponding to XML-schema
     *         "grants.xsd".
     * @throws UserGroupNotFoundException Thrown if no User Group with the provided ID exists.
     * @throws MissingMethodParameterException
     *                                    Thrown if no User Group ID is provided.
     * @throws AuthenticationException    Thrown if the authentication fails due to an invalid provided
     *                                    eSciDocUserHandle.
     * @throws AuthorizationException     Thrown if the authorization fails.
     * @throws SystemException            Thrown in case of an internal system error.
     */
    String retrieveCurrentGrants(String groupId) throws UserGroupNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    //
    // Subresource - grant
    //

    /**
     * Create a Grant for the User Group.<br>
     * <p/>
     * This Grant contains the information about a Role that is granted to the User Group and specifies for which object
     * this Role is granted, if the Role is limited.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The User Group must exist.<br/>
     * <p/>
     * See chapter 6 for detailed information about input and output data elements.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Group is accessed using the provided reference.</li> <li>The XML data is
     * validated against the XML schema of a Grant ("grant.xsd").</li> <li>It is checked, that the referenced Role
     * exists.</li> <li>It is checked, that the referenced object (if any) exists and is of one of the supported
     * resource types.</li> <li>It is checked, that no grant for the Role specified in the XML data (and for the
     * specified object, in case of a limited role) exists for the User Group.</li> <li>The title of the referenced
     * object is fetched. If this fails, as a fallback, a default title is constructed using the resource type and the
     * id, e.g. "Context escidoc:12345".</li> <li>A Grant is created and associated with the User Group. Grant ID,
     * created-by, creation-date, and the last-modification-date are added to the new grant.</li> <li>The new Grant is
     * stored.</li> <li>The XML representation of the new Grant is created.</li> <li>The XML data is returned.</li>
     * </ul>
     *
     * @param groupId  The User Group ID.
     * @param grantXML The XML representation of the Grant to be created corresponding to XML schema "grant.xsd".
     * @return The XML representation of the created Grant corresponding to XML schema "grant.xsd".
     * @throws AlreadyExistsException       Thrown if the defined grant already exists for the User Group.
     * @throws UserGroupNotFoundException   Thrown if no User Group with the provided id exists.
     * @throws InvalidScopeException        Thrown if given Scope is not of object-type of allowed Scopes for the Role.
     * @throws RoleNotFoundException        Thrown if the Role referenced in the Grant does not exist.
     * @throws XmlCorruptedException        Thrown if the provided XML data is invalid.
     * @throws XmlSchemaValidationException Thrown if the provided XML data is not schema conform
     * @throws MissingMethodParameterException
     *                                      Thrown if no User Group ID or no Grant XML representation is provided.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided eSciDoc User
     *                                      Handle.
     * @throws AuthorizationException       Thrown if the authorization fails.
     * @throws SystemException              Thrown in case of an internal system error.
     */
    @Validate(param = 1, resolver = "getGrantsSchemaLocation")
    String createGrant(String groupId, String grantXML) throws AlreadyExistsException, UserGroupNotFoundException,
        InvalidScopeException, RoleNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Revoke a Grant<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Group must exist.<br/>
     * <p/>
     * The Grant must exist and be contained in the list of Grants of the User Group.<br/>
     * <p/>
     * The Grant must not be revoked.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Group is accessed using the provided reference.</li> <li>Optimistic Locking
     * criteria is checked.</li> <li>The Grant is identified using the provided grant id.</li> <li>The Grant is set to
     * "revoked". Revoked-by, revocation-date. and revocation-remark are added to the grant. The last-modification-date
     * is updated.</li> <li>The updated Grant is stored.</li> <li>No data is returned.</li> </ul> <b>Parameter for
     * request:</b> (example)<br/>
     * <p/>
     * <pre>
     * &lt;param last-modification-date=&quot;1967-08-13T12:00:00.000+01:00&quot;&gt;
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
     * @param groupId   The User Group ID for that a Grant shall be revoked.
     * @param grantId   The Grant ID that shall be revoked.
     * @param taskParam The remark for the revocation of the Grant in an XML structure: (see above)
     * @throws UserGroupNotFoundException     Thrown if no User Group with the provided ID exists.
     * @throws GrantNotFoundException         Thrown if the specified Grant of the User Group cannot be found.
     * @throws AlreadyRevokedException        Thrown if the addressed Grant is revoked.
     * @throws XmlCorruptedException          Thrown if the provided XML representation of the task parameters is
     *                                        invalid.
     * @throws MissingAttributeValueException Thrown if the last modification date is not provided in the task
     *                                        parameters.
     * @throws MissingMethodParameterException
     *                                        Thrown if at least on of the parameters is not provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided eSciDoc User
     *                                        Handle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    void revokeGrant(String groupId, String grantId, String taskParam) throws UserGroupNotFoundException,
        GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Revoke grants.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Group must exist.<br/>
     * <p/>
     * The Grants must exist and be contained in the list of Grants of the User Group.<br/>
     * <p/>
     * The Grants must not be revoked.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Group is accessed using the provided reference.</li> <li>The Grants are
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
     * @param groupId   The User Group ID for that a grant shall be revoked.
     * @param taskParam The filter for Grants to revoke and the remark for the revocation of the Grant in an XML
     *                  structure: (see above)
     * @throws UserGroupNotFoundException     Thrown if no User Group with the provided ID exists.
     * @throws GrantNotFoundException         Thrown if the specified Grant of the User Group cannot be found.
     * @throws AlreadyRevokedException        Thrown if the addressed Grant is revoked.
     * @throws XmlCorruptedException          Thrown if the provided XML representation of the task parameters is
     *                                        invalid.
     * @throws MissingAttributeValueException Thrown if the last modification date is not provided in the task
     *                                        parameters.
     * @throws MissingMethodParameterException
     *                                        Thrown if at least on of the parameters is not provided.
     * @throws AuthenticationException        Thrown if the authentication fails due to an invalid provided eSciDoc User
     *                                        Handle.
     * @throws AuthorizationException         Thrown if the authorization fails.
     * @throws SystemException                Thrown in case of an internal system error.
     */
    void revokeGrants(String groupId, String taskParam) throws UserGroupNotFoundException, GrantNotFoundException,
        AlreadyRevokedException, XmlCorruptedException, MissingAttributeValueException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Retrieve the resources section of a User Group.
     *
     * @param groupId The User Group ID
     * @return the resources of the User Group as XML structure
     * @throws SystemException            Thrown in case of an internal error.
     * @throws UserGroupNotFoundException Thrown if a User Group with the provided ID does not exist in the framework.
     */
    String retrieveResources(String groupId) throws UserGroupNotFoundException, SystemException;

    //
    // User Group list
    //

    /**
     * Retrieve a filtered list of User Groups applying filters.<br/>
     * <p/>
     * Default (and for now, the only) format is a list of full User Group XML representations.<br/> Access rights are
     * checked.<br/> <br/> NOTE: URI-Like Filters are deprecated and will be removed in the next version of the
     * core-framework. Please use the new PATH-like filters (eg /id instead of http://purl.org/dc/elements/1.1/identifier).
     * For further information about the filter-names, please see the explain-plan.<br/>
     * <p/>
     * <ul> <li>Check weather all filter names are valid.</li> <li>The User Groups are accessed using the provided
     * filters.</li> <li>The XML representation of the list of User Groups corresponding to XML schema is returned as
     * output.</li> </ul> <br/> See chapter "Filters" for detailed information about filter definitions.<br/>
     * <b>Additional filters valid for this method:</b><br/> <ul> <li>active<br/> retrieves all User Groups that are
     * activated (value=true) or deactivated (value=false).</li> <li>http://escidoc.de/core/01/properties/user with
     * value "id"<br/> retrieves all User Groups the User belongs to (hierarchically).</li> </ul> <br/>
     *
     * @param filter The XML representation of the filter criteria.
     * @return Returns the XML representation of a list of User Groups.
     * @throws MissingMethodParameterException
     *                                     Thrown if no XML representation of filter parameters is provided.
     * @throws AuthenticationException     Thrown if the authentication fails due to an invalid provided eSciDoc User
     *                                     Handle.
     * @throws AuthorizationException      Thrown if the authorization fails.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SystemException             Thrown in case of an internal system error.
     */
    String retrieveUserGroups(Map<String, String[]> filter) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, InvalidSearchQueryException, SystemException;

    /**
     * Add one or more Selectors to a User Group.<br/>
     * <p/>
     * This Selectors will be added to the selector list of that User Group.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The User Group must exist.<br/>
     * <p/>
     * All referenced objects must exist.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Group is accessed using the provided reference.</li> <li>The provided
     * last-modification-date must match the last-modification-date currently saved in the system.</li> <li>Add the
     * Selectors to the Selector List.</li> <li>The new last-modification-date is returned.</li> </ul> The Selectors to
     * be added to the User Group are listed in the "param" section of the input data using their IDs.
     * <p/>
     * <b>Parameter for request:</b> (example)<br/>
     * <p/>
     * <pre>
     * &lt;param last-modification-date=&quot;1967-08-13T12:00:00.000+01:00&quot;&gt;
     * </pre>
     * <p/>
     * <pre>
     *  &lt;selector name=&quot;user-account&quot; type=&quot;internal&quot;&gt;escidoc:23232&lt;/selector&gt;
     * </pre>
     * <p/>
     * <pre>
     *  &lt;selector name=&quot;user-group&quot; type=&quot;internal&quot;&gt;escidoc:12121&lt;/selector&gt;
     * </pre>
     * <p/>
     * <pre>
     * &lt;/param&gt;
     * </pre>
     *
     * @param groupId   The User Group ID.
     * @param taskParam The list of selectors to add to the User Group. (See example above.)
     * @return last-modification-date within XML (result.xsd)
     * @throws OrganizationalUnitNotFoundException
     *                                      Thrown if an Organizational Unit with the provided ID does not exist in the
     *                                      framework.
     * @throws UserAccountNotFoundException Thrown if a User Account with the provided ID does not exist in the
     *                                      framework.
     * @throws UserGroupNotFoundException   Thrown if a User Group with the provided ID does not exist in the
     *                                      framework.
     * @throws InvalidContentException      Thrown if for any ids there is no resource in the framework.
     * @throws MissingMethodParameterException
     *                                      Thrown if one of expected input parameters is missing.
     * @throws SystemException              Thrown if a framework internal error occurs.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                      handle.
     * @throws AuthorizationException       Thrown if authorization fails.
     * @throws OptimisticLockingException   If the provided latest-modification-date does not match.
     * @throws XmlCorruptedException        Thrown in case of provided invalid XML data (corrupted data, schema
     *                                      validation failed, missing mandatory element or attribute values).
     * @throws XmlSchemaValidationException Thrown if the provided XML data is not schema conform
     * @throws UserGroupHierarchyViolationException
     *                                      Thrown if the hierarchy of User Groups is violated.
     */
    @Validate(param = 1, resolver = "getAddSelectorsSchemaLocation")
    String addSelectors(String groupId, String taskParam) throws OrganizationalUnitNotFoundException,
        UserAccountNotFoundException, UserGroupNotFoundException, InvalidContentException,
        MissingMethodParameterException, SystemException, AuthenticationException, AuthorizationException,
        OptimisticLockingException, XmlCorruptedException, XmlSchemaValidationException,
        UserGroupHierarchyViolationException;

    /**
     * Remove one or more Selectors from a User Group.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The User Group must exist.<br/>
     * <p/>
     * All referenced objects must exist.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The User Group is accessed using the provided reference.</li> <li>Optimistic Locking
     * criteria is checked.</li> <li>Remove the Selectors from the Selector List.</li> <li>The new
     * last-modification-date is returned.</li> </ul> The Selectors to be removed from the User Group are listed in the
     * "param" section of the input data using their IDs.
     * <p/>
     * <b>Parameter for request:</b> (example)<br/>
     * <p/>
     * <pre>
     * &lt;param last-modification-date=&quot;1967-08-13T12:00:00.000+01:00&quot;&gt;
     * </pre>
     * <p/>
     * <pre>
     *  &lt;id&gt;selector-id&lt;/id&gt;
     * </pre>
     * <p/>
     * <pre>
     *  &lt;id&gt;selector-id&lt;/id&gt;
     * </pre>
     * <p/>
     * <pre>
     * &lt;/param&gt;
     * </pre>
     *
     * @param groupId   The User Group ID.
     * @param taskParam The list of Selectors to remove from the User Group. (See example above.)
     * @return last-modification-date within XML (result.xsd)
     * @throws UserGroupNotFoundException   Thrown if a User Group with the provided id does not exist in the
     *                                      framework.
     * @throws MissingMethodParameterException
     *                                      Thrown if one of expected input parameters is missing.
     * @throws SystemException              Thrown if a framework internal error occurs.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided eSciDoc User
     *                                      Handle.
     * @throws AuthorizationException       Thrown if authorization fails.
     * @throws OptimisticLockingException   If the provided latest-modification-date does not match.
     * @throws OrganizationalUnitNotFoundException
     *                                      Thrown if an Organizational Unit with the provided ID does not exist in the
     *                                      framework.
     * @throws UserAccountNotFoundException Thrown if a User Account with the provided ID does not exist in the
     *                                      framework.
     * @throws XmlCorruptedException        Thrown in case of provided invalid XML data (corrupted data, schema
     *                                      validation failed).
     * @throws XmlSchemaValidationException Thrown if the provided XML data is not schema conform
     */
    @Validate(param = 1, resolver = "getRemoveSelectorsSchemaLocation")
    String removeSelectors(String groupId, String taskParam) throws XmlCorruptedException,
        XmlSchemaValidationException, AuthenticationException, AuthorizationException, SystemException,
        UserGroupNotFoundException, OptimisticLockingException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, UserAccountNotFoundException;
}
