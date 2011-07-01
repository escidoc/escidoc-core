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
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.business.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.UserGroupHierarchyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.Map;
import java.util.Set;

/**
 * The interface for access to a user group resource.
 *
 * @author André Schenk
 */
public interface UserGroupHandlerInterface extends PermissionHandlerInterface {

    String MSG_GROUP_INVALID_SELECTOR_NAME = "The given name is not allowed for internal selectors.";

    /**
     * Add one or more selectors to a User Group.<br/>
     *
     * @param groupId   User Group ID
     * @param taskParam list of selectors to add to the User Group
     * @return last-modification-date within XML (result.xsd)
     * @throws OrganizationalUnitNotFoundException
     *                                      Thrown if an Organizational Unit with the provided id does not exist in the
     *                                      framework.
     * @throws UserAccountNotFoundException Thrown if a User Account with the provided id does not exist in the
     *                                      framework.
     * @throws UserGroupNotFoundException   Thrown if a User Group with the provided id does not exist in the
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
     * @throws UserGroupHierarchyViolationException
     *                                      Thrown if a selector's group id is already in the user group tree of the
     *                                      given user group.
     */
    String addSelectors(String groupId, String taskParam) throws OrganizationalUnitNotFoundException,
        UserAccountNotFoundException, UserGroupNotFoundException, InvalidContentException,
        MissingMethodParameterException, SystemException, AuthenticationException, AuthorizationException,
        OptimisticLockingException, XmlCorruptedException, UserGroupHierarchyViolationException;

    /**
     * Remove one or more selectors from a User Group.<br/>
     *
     * @param groupId   User Group ID
     * @param taskParam list of selectors to remove from the User Group
     * @return last-modification-date within XML (result.xsd)
     * @throws OrganizationalUnitNotFoundException
     *                                      Thrown if an Organizational Unit with the provided id does not exist in the
     *                                      framework.
     * @throws UserAccountNotFoundException Thrown if a User Account with the provided id does not exist in the
     *                                      framework.
     * @throws UserGroupNotFoundException   Thrown if a User Group with the provided id does not exist in the
     *                                      framework.
     * @throws MissingMethodParameterException
     *                                      Thrown if one of expected input parameters is missing.
     * @throws SystemException              Thrown if a framework internal error occurs.
     * @throws AuthenticationException      Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                      handle.
     * @throws AuthorizationException       Thrown if authorization fails.
     * @throws OptimisticLockingException   If the provided latest-modification-date does not match.
     * @throws XmlCorruptedException        Thrown in case of provided invalid XML data (corrupted data, schema
     *                                      validation failed).
     */
    String removeSelectors(String groupId, String taskParam) throws XmlCorruptedException, AuthenticationException,
        AuthorizationException, SystemException, UserGroupNotFoundException, OptimisticLockingException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, UserAccountNotFoundException;

    /**
     * Retrieve the resources section of a User Group.
     *
     * @param groupId id of the user group
     * @return the resources of the user group as XML structure
     * @throws SystemException            Thrown in case of an internal error.
     * @throws UserGroupNotFoundException Thrown if a user group with the provided id does not exist in the framework.
     */
    String retrieveResources(final String groupId) throws UserGroupNotFoundException, SystemException;

    /**
     * Retrieve a list of User Groups which match the given search criteria.
     *
     * @param filter The filter criteria as CQL query.
     * @return XML representation of all User Groups which match the given search criteria
     * @throws AuthenticationException     Thrown if the authentication fails due to an invalid provided eSciDoc user
     *                                     handle.
     * @throws AuthorizationException      Thrown if the authorization fails.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SystemException             Thrown in case of an internal system error.
     */
    String retrieveUserGroups(Map<String, String[]> filter) throws AuthenticationException, AuthorizationException,
        InvalidSearchQueryException, SystemException;

    /**
     * Retrieve all groups hierarchically the given user currently belongs to as Set of groupIds.
     *
     * @param userId the User Account ID
     * @return Set with groupIds
     * @throws UserAccountNotFoundException in case the User Account for the userId is not found
     * @throws SystemException              Thrown in case of an internal system error.
     */
    Set<String> retrieveGroupsForUser(String userId) throws UserAccountNotFoundException, SystemException;

    /**
     * Retrieve all User Groups hierarchically the given user currently belongs to as Set of groupIds. If activeOnly is
     * set to true, just find active groups
     *
     * @param userId     User Account ID
     * @param activeOnly true if only active User Groups should be found.
     * @return Set with groupIds
     * @throws UserAccountNotFoundException in case the userAccount for the userId is not found
     * @throws SystemException              Thrown in case of an internal system error.
     */
    Set<String> retrieveGroupsForUser(String userId, boolean activeOnly) throws UserAccountNotFoundException,
        SystemException;

    /**
     * Retrieve all User Groups hierarchically the given User Group currently belongs to as Set of groupIds.
     *
     * @param groupId the User Group ID
     * @return Set with groupIds
     * @throws SystemException Thrown in case of an internal system error.
     */
    Set<String> retrieveGroupsForGroup(String groupId) throws SystemException;

}
