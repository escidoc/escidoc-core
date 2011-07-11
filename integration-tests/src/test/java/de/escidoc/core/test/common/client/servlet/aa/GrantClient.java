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

import java.util.Map;

/**
 * Offers access methods to the escidoc interface of the grant resource.
 *
 * @author Michael Hoppe
 */
public class GrantClient extends ClientBase {

    /**
     * Retrieve the current grants of the specified user group or user-account.
     *
     * @param id The user group id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveCurrentGrants(final String id) throws Exception {
        return null;
    }

    /**
     * Create a grant for the specified user group or user-account.
     *
     * @param id       The user group id.
     * @param grantXml The XML representation of an grant.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object createGrant(final String id, final String grantXml) throws Exception {
        return null;
    }

    /**
     * Retrieve a grant from the specified user group or user-account.
     *
     * @param id      The user group id.
     * @param grantId The id of the grant.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveGrant(final String id, final String grantId) throws Exception {
        return null;
    }

    /**
     * Revoke a grant from the specified user group or user-account.
     *
     * @param id           The user group id.
     * @param grantId      The id of the grant.
     * @param taskParamXml The task parameter in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object revokeGrant(final String id, final String grantId, final String taskParamXml) throws Exception {
        return null;
    }

    /**
     * Revoke a grant from the specified user group or user-account.
     *
     * @param id        The user group id.
     * @param filterXml The filter-criteria in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object revokeGrants(final String id, final String filterXml) throws Exception {
        return null;
    }

    /**
     * Retrieve a list of grants matching the provided filter-criteria.
     *
     * @param filter The filter-criteria in an XML structure.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveGrants(final Map<String, String[]> filter) throws Exception {
        return null;
    }
}
