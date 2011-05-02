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
package de.escidoc.core.common.util.db;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Enumeration to describe all types of relational databases.
 * 
 * @author André Schenk
 */
public enum DatabaseType {
    ORACLE("Oracle"), POSTGRES("PostgreSQL");

    private final String productName;

    /**
     * Create a new DatabaseType object.
     * 
     * @param productName
     *            database product name
     */
    DatabaseType(final String productName) {
        this.productName = productName;
    }

    /**
     * Get the corresponding DatabaseType object from the given database product name.
     * 
     * @param productName
     *            database product name
     * @return corresponding DatabaseType object
     */
    private static DatabaseType getDatabaseTypeFromProductName(final String productName) {
        DatabaseType result = null;

        for (final DatabaseType resourceType : DatabaseType.values()) {
            if (resourceType.productName.equals(productName)) {
                result = resourceType;
                break;
            }
        }
        return result;
    }

    /**
     * Get the current database product name.
     * 
     * @return database product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Create a DatabaseType object from the database product name which is fetched from the database meta data.
     * 
     * @param conn
     *            database connection
     * 
     * @return DatabaseType object
     * @throws SQLException
     *             Thrown if the JDBC request to the database failed.
     */
    public static DatabaseType valueOf(final Connection conn) throws SQLException {
        return getDatabaseTypeFromProductName(conn.getMetaData().getDatabaseProductName());
    }
}
