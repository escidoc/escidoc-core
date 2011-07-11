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
package de.escidoc.core.sm.business.vo.database.table;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.sm.business.vo.database.DatabaseConventionChecker;

import java.util.Collection;

/**
 * Value Object that holds Information about a database-table when creating a database-table.
 *
 * @author Michael Hoppe
 */
public class DatabaseTableVo {

    private String tableName;

    private Collection<DatabaseTableFieldVo> databaseFieldVos;

    private Collection<DatabaseIndexVo> databaseIndexVos;

    /**
     * @return the databaseFieldVos
     */
    public Collection<DatabaseTableFieldVo> getDatabaseFieldVos() {
        return this.databaseFieldVos;
    }

    /**
     * @param databaseFieldVos the databaseFieldVos to set
     */
    public void setDatabaseFieldVos(final Collection<DatabaseTableFieldVo> databaseFieldVos) {
        this.databaseFieldVos = databaseFieldVos;
    }

    /**
     * @return the databaseIndexVos
     */
    public Collection<DatabaseIndexVo> getDatabaseIndexVos() {
        return this.databaseIndexVos;
    }

    /**
     * @param databaseIndexVos the databaseIndexVos to set
     */
    public void setDatabaseIndexVos(final Collection<DatabaseIndexVo> databaseIndexVos) {
        this.databaseIndexVos = databaseIndexVos;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * @param tableName the tableName to set
     * @throws SqlDatabaseSystemException databaseException
     */
    public void setTableName(final String tableName) throws SqlDatabaseSystemException {
        DatabaseConventionChecker.checkName(tableName);
        this.tableName = tableName;
    }
}
