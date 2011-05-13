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
package de.escidoc.core.test.common.client.servlet.aa;

import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.interfaces.ResourceHandlerClientInterface;

import java.util.Map;

/**
 * Offers access methods to the escidoc interface of the user group resource.
 *
 * @author Michael Hoppe
 */
public class UserGroupClient extends GrantClient implements ResourceHandlerClientInterface {

    /**
     * Create a user group.
     *
     * @param userGroupXml The xml representation of an userGroup
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object create(final Object userGroupXml) throws Exception {

        return callEsciDoc("UserGroup.create", METHOD_CREATE, Constants.HTTP_METHOD_PUT, Constants.USER_GROUP_BASE_URI,
            new String[] {}, changeToString(userGroupXml));
    }

    /**
     * Delete a user group.
     *
     * @param id The userGroup id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object delete(final String id) throws Exception {
        return callEsciDoc("UserGroup.delete", METHOD_DELETE, Constants.HTTP_METHOD_DELETE,
            Constants.USER_GROUP_BASE_URI, new String[] { id });
    }

    /**
     * Retrieve the xml representation of a userGroup.
     *
     * @param id The id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieve(final String id) throws Exception {
        return callEsciDoc("UserGroup.retrieve", METHOD_RETRIEVE, Constants.HTTP_METHOD_GET,
            Constants.USER_GROUP_BASE_URI, new String[] { id });
    }

    /**
     * Retrieve the xml representation of the resources of a user group.
     *
     * @param id The id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveResources(final String id) throws Exception {
        return callEsciDoc("UserGroup.retrieveResources", METHOD_RETRIEVE_RESOURCES, Constants.HTTP_METHOD_GET,
            Constants.USER_GROUP_BASE_URI, new String[] { id, Constants.SUB_RESOURCES });
    }

    /**
     * Retrieve the xml representation of the list of user groups.
     *
     * @param filter The filter parameters.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveUserGroups(final Map<String, String[]> filter) throws Exception {
        return callEsciDoc("UserGroup.retrieveUserGroups", METHOD_RETRIEVE_USER_GROUPS, Constants.HTTP_METHOD_GET,
            Constants.USER_GROUPS_BASE_URI, new String[] {}, filter);
    }

    /**
     * Update a user group.
     *
     * @param id           The userGroup id.
     * @param userGroupXml The xml representation of the user
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object update(final String id, final Object userGroupXml) throws Exception {

        return callEsciDoc("UserGroup.update", METHOD_UPDATE, Constants.HTTP_METHOD_PUT, Constants.USER_GROUP_BASE_URI,
            new String[] { id }, changeToString(userGroupXml));
    }

    /**
     * Activate a user group.
     *
     * @param id           The user group id.
     * @param taskParamXml The task parameter in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object activate(final String id, final String taskParamXml) throws Exception {

        return callEsciDoc("UserGroup.activate", METHOD_ACTIVATE, Constants.HTTP_METHOD_POST,
            Constants.USER_GROUP_BASE_URI, new String[] { id, "activate" }, changeToString(taskParamXml));
    }

    /**
     * Deactivate a user group.
     *
     * @param id           The user group id.
     * @param taskParamXml The task parameter in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object deactivate(final String id, final String taskParamXml) throws Exception {

        return callEsciDoc("UserGroup.deactivate", METHOD_DEACTIVATE, Constants.HTTP_METHOD_POST,
            Constants.USER_GROUP_BASE_URI, new String[] { id, "deactivate" }, changeToString(taskParamXml));
    }

    /**
     * Retrieve the current grants of the specified user group.
     *
     * @param id The user group id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieveCurrentGrants(final String id) throws Exception {

        return callEsciDoc("UserGroup.retrieveCurrentGrants", METHOD_RETRIEVE_CURRENT_GRANTS,
            Constants.HTTP_METHOD_GET, Constants.USER_GROUP_BASE_URI, new String[] { id, "resources/current-grants" });
    }

    /**
     * Create a grant for the specified user group.
     *
     * @param id       The user group id.
     * @param grantXml The XML representation of an grant.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object createGrant(final String id, final String grantXml) throws Exception {

        return callEsciDoc("UserGroup.createGrant", METHOD_CREATE_GRANT, Constants.HTTP_METHOD_PUT,
            Constants.USER_GROUP_BASE_URI, new String[] { id, "/resources/grants/grant" }, changeToString(grantXml));
    }

    /**
     * Retrieve a grant from the specified user group.
     *
     * @param id      The user group id.
     * @param grantId The id of the grant.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieveGrant(final String id, final String grantId) throws Exception {

        return callEsciDoc("UserGroup.retrieveGrant", METHOD_RETRIEVE_GRANT, Constants.HTTP_METHOD_GET,
            Constants.USER_GROUP_BASE_URI, new String[] { id, "resources/grants/grant", grantId });
    }

    /**
     * Add selecors to the specified user group.
     *
     * @param id        The user group id.
     * @param taskParam The task parameter in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */

    public Object addSelectors(final String id, final String taskParam) throws Exception {

        return callEsciDoc("UserGroup.addSelectors", METHOD_ADD_SELECTORS, Constants.HTTP_METHOD_POST,
            Constants.USER_GROUP_BASE_URI, new String[] { id, Constants.SUB_ADD_SELECTORS }, taskParam);
    }

    /**
     * Add selecors to the specified user group.
     *
     * @param id        The user group id.
     * @param taskParam The task parameter in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */

    public Object removeSelectors(final String id, final String taskParam) throws Exception {

        return callEsciDoc("UserGroup.removeSelectors", METHOD_REMOVE_SELECTORS, Constants.HTTP_METHOD_POST,
            Constants.USER_GROUP_BASE_URI, new String[] { id, Constants.SUB_REMOVE_SELECTORS }, taskParam);
    }

    /**
     * Revoke a grant from the specified user group.
     *
     * @param id        The user group id.
     * @param filterXml The filter-criteria in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object revokeGrants(final String id, final String filterXml) throws Exception {

        String[] pathElements = new String[] { id, "resources/grants/revoke-grants" };
        return callEsciDoc("UserGroup.revokeGrants", METHOD_REVOKE_GRANTS, Constants.HTTP_METHOD_POST,
            Constants.USER_GROUP_BASE_URI, pathElements, changeToString(filterXml));
    }

    /**
     * Revoke a grant from the specified user account.
     *
     * @param id           The user account id.
     * @param grantId      The id of the grant.
     * @param taskParamXml The task parameter in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object revokeGrant(final String id, final String grantId, final String taskParamXml) throws Exception {

        String[] pathElements = new String[] { id, "resources/grants/grant", grantId, "revoke-grant" };
        return callEsciDoc("UserGroup.revokeGrant", METHOD_REVOKE_GRANT, Constants.HTTP_METHOD_POST,
            Constants.USER_GROUP_BASE_URI, pathElements, changeToString(taskParamXml));
    }

}
