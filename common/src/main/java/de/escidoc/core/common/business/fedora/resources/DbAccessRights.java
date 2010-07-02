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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * Read the user access rights stored in the database table "list.filter".
 * These access rights are SQL WHERE clauses which represent the read policies
 * for a specific user role.
 *
 * @spring.bean id="resource.DbAccessRights"
 * @author SCHE
 */
public class DbAccessRights extends AccessRights {
    /**
     * SQL statements.
     */
    private static final String ALL_FILTERS = "select * from list.filter";

    private static final String DELETE_ALL_FILTERS =
        "DELETE FROM list.filter";

    private static final String DELETE_FILTER_FOR_ROLE =
        "DELETE FROM list.filter WHERE role_id = ?";

    private static final String DELETE_FILTER =
        "DELETE FROM list.filter WHERE role_id = ? AND type = ?";

    private static final String INSERT_FILTER =
        "INSERT INTO list.filter (role_id, type, rule) VALUES (?, ?, ?)";

    /**
     * Create a new object and initialize the rights map.
     */
    public DbAccessRights() {
        for (int index = 0; index < rightsMap.length; index++) {
            rightsMap [index] = new HashMap <String, String>();
        }
    }

    /**
     * Delete a specific access right.
     *
     * @param roleId role id
     */
    public void deleteAccessRight(final String roleId) {
        getJdbcTemplate().update(DELETE_FILTER_FOR_ROLE, new Object[] {roleId});
    }

    /**
     * Delete all access rights.
     */
    public void deleteAccessRights() {
        getJdbcTemplate().update(DELETE_ALL_FILTERS);
        synchronized (rightsMap) {
            for (int index = 0; index < rightsMap.length; index++) {
                rightsMap [index] = null;
            }
        }
    }

    /**
     * Store the given access right in the database table list.filter.
     *
     * @param type resource type
     * @param roleId role id
     * @param sqlStatement SQL statement for the given combination of resource
     *                      type and role
     */
    public void putAccessRight(
        final ResourceType type, final String roleId, final String sqlStatement) {
        String sqlType = type.name().toLowerCase();

        getJdbcTemplate().update(
            DELETE_FILTER, new Object[] {roleId, sqlType});
        getJdbcTemplate().update(
            INSERT_FILTER, new Object[] {roleId, sqlType, sqlStatement});
    }

    /**
     * Read all access rights from the database and store them in a map
     * internally.
     */
    protected void readAccessRights() {
        synchronized (rightsMap) {
            getJdbcTemplate().query(
                ALL_FILTERS,
                new ResultSetExtractor() {
                    public Object extractData(final ResultSet rs)
                        throws SQLException {
                        while (rs.next()) {
                            String roleId = rs.getString(1);
                            final int resourceType = ResourceType.valueOf(
                                rs.getString(2).toUpperCase()).ordinal();

                            if (roleId == null) {
                                roleId = DEFAULT_ROLE;
                            }
                            if (rightsMap[resourceType] == null) {
                                rightsMap[resourceType] =
                                    new HashMap <String, String>();
                            }
                            rightsMap[resourceType].put(roleId, rs.getString(3));
                        }
                        return null;
                    }
                }
                );
        }
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
