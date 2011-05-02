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

/**
 * Holds all values needed to represent a field in an additional where-clause.
 *
 * @author Michael Hoppe
 */
public class AdditionalWhereFieldVo {

    private String tableName;

    private String fieldName;

    private String fieldValue;

    private String xpath;

    // also can be '<' or '>'
    private String operator = Constants.DATABASE_OPERATOR_EQUALS;

    // also can be numeric, date, daydate, xpath-boolean,
    // xpath-string, xpath-numeric, free-sql
    private String fieldType = Constants.DATABASE_FIELD_TYPE_TEXT;

    // alliance also can be 'or'
    private String alliance = Constants.DATABASE_ALLIANCE_AND;

    /**
     * can be 'and' or 'or'.
     *
     * @return the alliance
     */
    public String getAlliance() {
        return this.alliance;
    }

    /**
     * can be 'and' or 'or'.
     *
     * @param alliance the alliance to set
     * @throws SqlDatabaseSystemException e
     */
    public void setAlliance(final String alliance) throws SqlDatabaseSystemException {
        if (alliance == null || !alliance.equalsIgnoreCase(Constants.DATABASE_ALLIANCE_AND)
            && !alliance.equalsIgnoreCase(Constants.DATABASE_ALLIANCE_OR)) {
            throw new SqlDatabaseSystemException("wrong alliance given");
        }
        this.alliance = alliance;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * @param fieldName the fieldName to set
     * @throws SqlDatabaseSystemException databaseException
     */
    public void setFieldName(final String fieldName) throws SqlDatabaseSystemException {
        DatabaseConventionChecker.checkName(fieldName);
        this.fieldName = fieldName;
    }

    /**
     * can be text, numeric, date, daydate, xpath-boolean, xpath-string, xpath-numeric, free-sql.
     *
     * @return the fieldType
     */
    public String getFieldType() {
        return this.fieldType;
    }

    /**
     * can be text, numeric, date, daydate, xpath-boolean, xpath-string, xpath-numeric, free-sql.
     *
     * @param fieldType the fieldType to set
     * @throws SqlDatabaseSystemException e
     */
    public void setFieldType(final String fieldType) throws SqlDatabaseSystemException {
        if (fieldType == null || !fieldType.equals(Constants.DATABASE_FIELD_TYPE_TEXT)
            && !fieldType.equals(Constants.DATABASE_FIELD_TYPE_NUMERIC)
            && !fieldType.equals(Constants.DATABASE_FIELD_TYPE_DATE)
            && !fieldType.equals(Constants.DATABASE_FIELD_TYPE_DAYDATE)
            && !fieldType.equals(Constants.DATABASE_FIELD_TYPE_XPATH_BOOLEAN)
            && !fieldType.equals(Constants.DATABASE_FIELD_TYPE_XPATH_STRING)
            && !fieldType.equals(Constants.DATABASE_FIELD_TYPE_FREE_SQL)) {
            throw new SqlDatabaseSystemException("wrong fieldType given");
        }
        this.fieldType = fieldType;
    }

    /**
     * @return the fieldValue
     */
    public String getFieldValue() {
        return this.fieldValue;
    }

    /**
     * @param fieldValue the fieldValue to set
     */
    public void setFieldValue(final String fieldValue) {
        this.fieldValue = fieldValue;
    }

    /**
     * @return the operator
     */
    public String getOperator() {
        return this.operator;
    }

    /**
     * can be =,<,>.
     *
     * @param operator the operator to set
     * @throws SqlDatabaseSystemException e
     */
    public void setOperator(final String operator) throws SqlDatabaseSystemException {
        if (operator == null || !operator.equals(Constants.DATABASE_OPERATOR_EQUALS)
            && !operator.equals(Constants.DATABASE_OPERATOR_LESS)
            && !operator.equals(Constants.DATABASE_OPERATOR_GREATER)) {
            throw new SqlDatabaseSystemException("wrong operator given");
        }
        this.operator = operator;
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

    /**
     * @return the xpath
     */
    public String getXpath() {
        return this.xpath;
    }

    /**
     * @param xpath the xpath to set
     */
    public void setXpath(final String xpath) {
        this.xpath = xpath;
    }

}
