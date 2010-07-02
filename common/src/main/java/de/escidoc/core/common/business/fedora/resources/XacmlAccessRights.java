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
package de.escidoc.core.common.business.fedora.resources;

import java.util.HashMap;

import javax.sql.DataSource;

/**
 * Get the user access rights from AA when instantiating the XACML policies.
 * These access rights are SQL WHERE clauses which represent the read policies
 * for a specific user role.
 *
 * @spring.bean id="resource.XacmlAccessRights"
 * @author SCHE
 */
public class XacmlAccessRights extends AccessRights {

    /**
     * Delete a specific access right.
     *
     * @param roleId role id
     */
    public void deleteAccessRight(final String roleId) {
    }

    /**
     * Delete all access rights.
     */
    public void deleteAccessRights() {
        synchronized (rightsMap) {
            for (int index = 0; index < rightsMap.length; index++) {
                rightsMap [index] = null;
            }
        }
    }

    /**
     * Get a access right from outside (AA) and store it in a map internally.
     *
     * @param type resource type
     * @param roleId role id
     * @param sqlStatement SQL statement for the given combination of resource
     *                      type and role
     */
    public void putAccessRight(
        final ResourceType type, final String roleId, final String sqlStatement) {

        synchronized (rightsMap) {
            if (rightsMap[type.ordinal()] == null) {
                rightsMap[type.ordinal()] = new HashMap <String, String>();
            }
            rightsMap[type.ordinal()].put(roleId, sqlStatement);
        }
    }

    /**
     * This method is only a dummy in this kind of access rights module. The
     * access rights are get from AA instead.
     */
    protected void readAccessRights() {
    }

    /**
     * Injects the data source.
     *
     * @spring.property ref="escidoc-core.DataSource"
     * @param myDataSource data source from Spring
     */
    public void setMyDataSource(final DataSource myDataSource) {
        super.setDataSource(myDataSource);
    }
}
