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
 * Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.business.cache;

import de.escidoc.core.aa.business.interfaces.UserAccountHandlerInterface;
import de.escidoc.core.aa.business.interfaces.UserGroupHandlerInterface;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.servlet.Login;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class encapsulates access to the policies cache and ensures that the
 * cache is filled when reading from it.
 * 
 * @spring.bean id="resource.PoliciesCacheProxy"
 * @author SCHE
 */
public class PoliciesCacheProxy {
    private UserAccountHandlerInterface userAccountHandler = null;

    private UserGroupHandlerInterface userGroupHandler = null;

    /**
     * Gets the the group grants for the provided group ID.<br>
     * 
     * @param groupId
     *            The group ID to use as key for HashMap.
     * @return The grants of the group in a <code>Map</code>, or
     *         <code>null</code>.
     */
    public Map<String, Map<String, List<RoleGrant>>> getGroupGrants(
        final String groupId) {
        Map<String, Map<String, List<RoleGrant>>> result =
            PoliciesCache.getGroupGrants(groupId);

        if (result == null) {
            try {
                result = userGroupHandler.retrieveCurrentGrantsAsMap(groupId);
                PoliciesCache.putGroupGrants(groupId, result);
            }
            catch (Exception e) {
                // The caller doesn't expect to get an exception from here if
                // the group doesn't exist.
            }
        }
        return result;
    }

    /**
     * Gets the the user groups for the provided userId.<br>
     * 
     * @param userId
     *            The userId to use as key for HashMap.
     * @return The groups of the user as <code>Set</code>, or <code>null</code>.
     */
    public Set<String> getUserGroups(final String userId) {
        Set<String> result = PoliciesCache.getUserGroups(userId);

        if (result == null) {
            try {
                result = userGroupHandler.retrieveGroupsForUser(userId, true);
                PoliciesCache.putUserGroups(userId, result);
            }
            catch (Exception e) {
                // The caller doesn't expect to get an exception from here if
                // the user doesn't exist.
            }
        }
        return result;
    }

    /**
     * Gets the the user grants for the provided user ID.<br>
     * 
     * @param userId
     *            The user ID to use as key for HashMap.
     * @return The grants of the user in a <code>Map</code>, or
     *         <code>null</code>.
     */
    public Map<String, Map<String, List<RoleGrant>>> getUserGrants(
        final String userId) {
        Map<String, Map<String, List<RoleGrant>>> result =
            PoliciesCache.getUserGrants(userId);

        if (result == null) {
            try {
                result = userAccountHandler.retrieveCurrentGrantsAsMap(userId);
                PoliciesCache.putUserGrants(userId, result);
            }
            catch (Exception e) {
                // The caller doesn't expect to get an exception from here if
                // the user doesn't exist.
            }
        }
        return result;
    }

    /**
     * Injects the user account handler.
     * 
     * @param userAccountHandler
     *            user account handler from Spring
     * 
     * @spring.property ref="business.UserAccountHandler"
     */
    public void setUserAccountHandler(
        final UserAccountHandlerInterface userAccountHandler) {
        this.userAccountHandler = userAccountHandler;
    }

    /**
     * Injects the user group handler.
     * 
     * @param userGroupHandler
     *            user group handler from Spring
     * 
     * @spring.property ref="business.UserGroupHandler"
     */
    public void setUserGroupHandler(
        final UserGroupHandlerInterface userGroupHandler) {
        this.userGroupHandler = userGroupHandler;
    }
}
