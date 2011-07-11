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
package de.escidoc.core.test.common.client.servlet.sm;

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;

import java.util.Map;

/**
 * Offers access methods to the escidoc interface of the Statistic Scope resource.
 *
 * @author Michael Hoppe
 */
public class ScopeClient extends ClientBase {

    /**
     * Create an Scope in the escidoc framework.
     *
     * @param scopeXml The xml representation of the Scope.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object create(final Object scopeXml) throws Exception {

        return callEsciDoc("Scope.create", METHOD_CREATE, Constants.HTTP_METHOD_PUT,
            Constants.STATISTIC_SCOPE_BASE_URI, new String[] {}, changeToString(scopeXml));
    }

    /**
     * Delete an Scope from the escidoc framework.
     *
     * @param id The id of the Scope.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object delete(final String id) throws Exception {

        return callEsciDoc("Scope.delete", METHOD_DELETE, Constants.HTTP_METHOD_DELETE,
            Constants.STATISTIC_SCOPE_BASE_URI, new String[] { id });
    }

    /**
     * Retrieve the xml representation of an Scope.
     *
     * @param id The id of the Scope.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieve(final String id) throws Exception {

        return callEsciDoc("Scope.retrieve", METHOD_RETRIEVE, Constants.HTTP_METHOD_GET,
            Constants.STATISTIC_SCOPE_BASE_URI, new String[] { id });
    }

    /**
     * Retrieve the XML representation of the list of scopes.
     *
     * @param filter filter as CQL query
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveScopes(final Map<String, String[]> filter) throws Exception {

        return callEsciDoc("Scope.retrieveScopes", METHOD_RETRIEVE_SCOPES, Constants.HTTP_METHOD_GET,
            Constants.STATISTIC_SCOPES_BASE_URI, new String[] {}, filter);
    }

    /**
     * Update an Scope in the escidoc framework.
     *
     * @param id       The id of the Scope.
     * @param scopeXml The xml representation of the Scope.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object update(final String id, final Object scopeXml) throws Exception {

        return callEsciDoc("Scope.update", METHOD_UPDATE, Constants.HTTP_METHOD_PUT,
            Constants.STATISTIC_SCOPE_BASE_URI, new String[] { id }, changeToString(scopeXml));
    }

}
