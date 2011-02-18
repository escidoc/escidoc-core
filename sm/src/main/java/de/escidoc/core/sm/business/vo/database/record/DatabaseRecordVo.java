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
package de.escidoc.core.sm.business.vo.database.record;

import java.util.Collection;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.sm.business.vo.database.DatabaseConventionChecker;

/**
 * Holds all values needed to represent one database-record.
 * 
 * @author MIH
 */
public class DatabaseRecordVo {
    private String tableName;

    private Collection<DatabaseRecordFieldVo> databaseRecordFieldVos;

    /**
     * @return the databaseRecordFieldVos
     */
    public final Collection<DatabaseRecordFieldVo> getDatabaseRecordFieldVos() {
        return databaseRecordFieldVos;
    }

    /**
     * @param databaseRecordFieldVos
     *            the databaseRecordFieldVos to set
     */
    public final void setDatabaseRecordFieldVos(
            final Collection<DatabaseRecordFieldVo> databaseRecordFieldVos) {
        this.databaseRecordFieldVos = databaseRecordFieldVos;
    }

    /**
     * @return the tableName
     */
    public final String getTableName() {
        return tableName;
    }

    /**
     * @param tableName
     *            the tableName to set
     * @throws SqlDatabaseSystemException databaseException
     */
    public final void setTableName(final String tableName)
                        throws SqlDatabaseSystemException {
        DatabaseConventionChecker.checkName(tableName);
        this.tableName = tableName;
    }
}
