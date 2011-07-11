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
package de.escidoc.core.sm.business.vo.database.select;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.sm.business.Constants;
import de.escidoc.core.sm.business.vo.database.DatabaseConventionChecker;

import java.util.Collection;

/**
 * Holds all values needed to execute an sql-statement or to delete/update records in a database-table.
 *
 * @author Michael Hoppe
 */
public class DatabaseSelectVo {

    // selectType also can be 'UPDATE' or 'DELETE'
    private String selectType = Constants.DATABASE_SELECT_TYPE_SELECT;

    private Collection<String> tableNames;

    private Collection<SelectFieldVo> selectFieldVos;

    private RootWhereGroupVo rootWhereGroupVo;

    private Collection<AdditionalWhereGroupVo> additionalWhereGroupVos;

    /**
     * @return the additionalWhereGroupVos
     */
    public Collection<AdditionalWhereGroupVo> getAdditionalWhereGroupVos() {
        return this.additionalWhereGroupVos;
    }

    /**
     * @param additionalWhereGroupVos the additionalWhereGroupVos to set
     */
    public void setAdditionalWhereGroupVos(final Collection<AdditionalWhereGroupVo> additionalWhereGroupVos) {
        this.additionalWhereGroupVos = additionalWhereGroupVos;
    }

    /**
     * @return the rootWhereGroupVo
     */
    public RootWhereGroupVo getRootWhereGroupVo() {
        return this.rootWhereGroupVo;
    }

    /**
     * @param rootWhereGroupVo the rootWhereGroupVo to set
     */
    public void setRootWhereGroupVo(final RootWhereGroupVo rootWhereGroupVo) {
        this.rootWhereGroupVo = rootWhereGroupVo;
    }

    /**
     * @return the selectFieldVos
     */
    public Collection<SelectFieldVo> getSelectFieldVos() {
        return this.selectFieldVos;
    }

    /**
     * @param selectFieldVos the selectFieldVos to set
     */
    public void setSelectFieldVos(final Collection<SelectFieldVo> selectFieldVos) {
        this.selectFieldVos = selectFieldVos;
    }

    /**
     * can be select, update or delete.
     *
     * @return the selectType
     */
    public String getSelectType() {
        return this.selectType;
    }

    /**
     * can be select, update or delete.
     *
     * @param selectType the selectType to set
     * @throws SqlDatabaseSystemException e
     */
    public void setSelectType(final String selectType) throws SqlDatabaseSystemException {
        if (selectType == null || !selectType.equals(Constants.DATABASE_SELECT_TYPE_SELECT)
            && !selectType.equals(Constants.DATABASE_SELECT_TYPE_UPDATE)
            && !selectType.equals(Constants.DATABASE_SELECT_TYPE_DELETE)) {
            throw new SqlDatabaseSystemException("wrong selectType given");
        }
        this.selectType = selectType;
    }

    /**
     * @return the tableNames
     */
    public Collection<String> getTableNames() {
        return this.tableNames;
    }

    /**
     * @param tableNames the tableNames to set
     * @throws SqlDatabaseSystemException databaseException
     */
    public void setTableNames(final Collection<String> tableNames) throws SqlDatabaseSystemException {
        for (final String tableName : tableNames) {
            DatabaseConventionChecker.checkName(tableName);
        }
        this.tableNames = tableNames;
    }
}
