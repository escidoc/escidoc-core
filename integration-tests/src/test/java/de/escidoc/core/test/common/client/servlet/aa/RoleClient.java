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

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.interfaces.ResourceHandlerClientInterface;

import java.util.Map;

/**
 * Offers access methods to the escidoc interface of the role resource.
 *
 * @author Torsten Tetteroo
 */
public class RoleClient extends ClientBase implements ResourceHandlerClientInterface {

    /**
     * Create a role.
     *
     * @param roleXml The xml representation of a role
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object create(final Object roleXml) throws Exception {

        return callEsciDoc("Role.create", METHOD_CREATE, Constants.HTTP_METHOD_PUT, Constants.ROLE_BASE_URI,
            new String[] {}, changeToString(roleXml));
    }

    /**
     * Delete a role.
     *
     * @param id The role id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object delete(final String id) throws Exception {
        return callEsciDoc("Role.delete", METHOD_DELETE, Constants.HTTP_METHOD_DELETE, Constants.ROLE_BASE_URI,
            new String[] { id });
    }

    /**
     * Retrieve the xml representation of a role.
     *
     * @param id The role id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieve(final String id) throws Exception {
        return callEsciDoc("Role.retrieve", METHOD_RETRIEVE, Constants.HTTP_METHOD_GET, Constants.ROLE_BASE_URI,
            new String[] { id });
    }

    /**
     * Retrieve the xml representation of the resources of a role.
     *
     * @param id The role id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveResources(final String id) throws Exception {
        return callEsciDoc("Role.retrieveResources", METHOD_RETRIEVE_RESOURCES, Constants.HTTP_METHOD_GET,
            Constants.ROLE_BASE_URI, new String[] { id, "resources" });
    }

    /**
     * Update a role.
     *
     * @param id      The role id.
     * @param roleXml The xml representation of the role
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object update(final String id, final Object roleXml) throws Exception {

        return callEsciDoc("Role.update", METHOD_UPDATE, Constants.HTTP_METHOD_PUT, Constants.ROLE_BASE_URI,
            new String[] { id }, changeToString(roleXml));
    }

    /**
     * Retrieve role list.
     *
     * @param filter The filter param.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveRoles(final Map<String, String[]> filter) throws Exception {

        return callEsciDoc("Role.retrieveRoles", METHOD_RETRIEVE_ROLES, Constants.HTTP_METHOD_GET,
            Constants.ROLES_BASE_URI, new String[] {}, filter);
    }
}
