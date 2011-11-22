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
package de.escidoc.core.sm.business.persistence;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.sm.business.vo.database.record.DatabaseRecordVo;
import de.escidoc.core.sm.business.vo.database.select.DatabaseSelectVo;
import de.escidoc.core.sm.business.vo.database.table.DatabaseTableVo;

import java.util.List;

/**
 * Interface for direct JDBC Database access.
 *
 * @author Michael Hoppe
 */
public interface DirectDatabaseAccessorInterface {

    /**
     * Converts xmldate into database-specific format. Eg for where-clauses
     *
     * @param xmldate date in xml-format
     * @return String date in database-specific format
     * @throws SqlDatabaseSystemException e
     */
    String convertDateForSelect(final String xmldate) throws SqlDatabaseSystemException;

        /**
     * Create a new Table according to the Informations in the databaseTableVo.
     *
     * @param databaseTableVo databaseTableVo with information about tablename, fieldnames and indexnames.
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     */
    void createTable(final DatabaseTableVo databaseTableVo) throws SqlDatabaseSystemException;

    /**
     * Drop a table according to the Informations in the databaseTableVo..
     *
     * @param databaseTableVo databaseTableVo with information about tablename.
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     */
    void dropTable(final DatabaseTableVo databaseTableVo) throws SqlDatabaseSystemException;

    /**
     * Insert new Record into Table.
     *
     * @param databaseRecordVo DatabaseRecordVo with information about tablename, fieldnames and indexnames.
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     */
    void createRecord(final DatabaseRecordVo databaseRecordVo) throws SqlDatabaseSystemException;

    /**
     * Delete Record.
     *
     * @param databaseSelectVo databaseSelectVo with information about record.
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     */
    void deleteRecord(final DatabaseSelectVo databaseSelectVo) throws SqlDatabaseSystemException;

    /**
     * Update Record.
     *
     * @param databaseSelectVo databaseSelectVo with information about record.
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     */
    void updateRecord(final DatabaseSelectVo databaseSelectVo) throws SqlDatabaseSystemException;

    /**
     * Execute SQL.
     *
     * @param databaseSelectVo databaseSelectVo with information about sql.
     * @return List of Maps with data (one Map per db-record)
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     */
    List executeSql(final DatabaseSelectVo databaseSelectVo) throws SqlDatabaseSystemException;

    /**
     * Execute SQL.
     *
     * @param sql sql-String
     * @return List of Maps with data (one Map per db-record)
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     */
    List executeReadOnlySql(final String sql) throws SqlDatabaseSystemException;

    /**
     * Get database-dependant sql-query-part for an xpath-boolean request.
     *
     * @param xpath xpath-expression.
     * @param field db-field expression shall run on.
     * @return String database-dependant query for an xpath-boolean request.
     */
    String getXpathBoolean(final String xpath, final String field);

    /**
     * Get database-dependant sql-query-part for an xpath-string request.
     *
     * @param xpath xpath-expression.
     * @param field db-field expression shall run on.
     * @return String database-dependant query for an xpath-string request.
     */
    String getXpathString(final String xpath, final String field);

    /**
     * checks if given fieldname is a reserved expression in the database.
     *
     * @param fieldname name of field
     * @throws SqlDatabaseSystemException e
     */
    void checkReservedExpressions(final String fieldname) throws SqlDatabaseSystemException;
}
