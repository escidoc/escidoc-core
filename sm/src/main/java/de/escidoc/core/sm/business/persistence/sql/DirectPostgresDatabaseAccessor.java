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
package de.escidoc.core.sm.business.persistence.sql;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.sm.business.Constants;
import de.escidoc.core.sm.business.persistence.DirectDatabaseAccessorInterface;
import de.escidoc.core.sm.business.vo.database.record.DatabaseRecordFieldVo;
import de.escidoc.core.sm.business.vo.database.record.DatabaseRecordVo;
import de.escidoc.core.sm.business.vo.database.select.AdditionalWhereFieldVo;
import de.escidoc.core.sm.business.vo.database.select.AdditionalWhereGroupVo;
import de.escidoc.core.sm.business.vo.database.select.DatabaseSelectVo;
import de.escidoc.core.sm.business.vo.database.select.RootWhereFieldVo;
import de.escidoc.core.sm.business.vo.database.select.RootWhereGroupVo;
import de.escidoc.core.sm.business.vo.database.select.SelectFieldVo;
import de.escidoc.core.sm.business.vo.database.table.DatabaseIndexVo;
import de.escidoc.core.sm.business.vo.database.table.DatabaseTableFieldVo;
import de.escidoc.core.sm.business.vo.database.table.DatabaseTableVo;

/**
 * Class for direct JDBC Database access via Hibernate.
 *
 * @author Michael Hoppe
 */
public class DirectPostgresDatabaseAccessor extends JdbcDaoSupport implements DirectDatabaseAccessorInterface {

    //Check CREATE INDEX and DROP INDEX Statement
    //Method getCreateStatements + get DropStatements

    //Check Where-Clause <tablename>.<fieldname> allowed?
    //Method handleFieldTypeWhere

    //Eventually convert xml:date-format to database-specific format
    //with Method convertDate()
    //for Method createRecord()

    //Check xPath-Methods(getXpathBoolean, getXpathString, getXpathNumeric)

    private static final Pattern SPLIT_PATTERN = Pattern.compile(",");

    private static final String TIMESTAMP_FIELD_TYPE = "timestamp";

    private static final String TEXT_FIELD_TYPE = "text";

    private static final String NUMERIC_FIELD_TYPE = "numeric";

    private static final String SYSDATE = "'now'";

    private static final String DATE_FUNCTION = "'${date_placeholder}'";

    private static final String DATE_TO_CHAR_DAY_OF_MONTH_FUNCTION = "date_trunc('day',${FIELD_NAME})";

    private static final String DATE_TO_CHAR_FUNCTION = "${FIELD_NAME}";

    private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("\\$\\{FIELD_NAME\\}");

    private static final Matcher FIELD_NAME_MATCHER = FIELD_NAME_PATTERN.matcher("");

    private static final String XPATH_BOOLEAN_FUNCTION =
        "(xpath('${XPATH}', XMLPARSE(DOCUMENT ${FIELD_NAME})))[1]::text IS NOT NULL";

    private static final String XPATH_STRING_FUNCTION =
        "(xpath('${XPATH}', XMLPARSE(DOCUMENT ${FIELD_NAME})))[1]::text";

    private static final String XPATH_NUMBER_FUNCTION =
        "(xpath('${XPATH}', XMLPARSE(DOCUMENT ${FIELD_NAME})))[1]::text";

    private static final Pattern XPATH_PATTERN = Pattern.compile("\\$\\{XPATH\\}(.*?)\\$\\{FIELD_NAME\\}");

    private static final Matcher XPATH_MATCHER = XPATH_PATTERN.matcher("");

    private static final Map<String, String> RESERVED_EXPRESSIONS = new HashMap<String, String>();

    static {
        RESERVED_EXPRESSIONS.put("user", "");
        RESERVED_EXPRESSIONS.put("timestamp", "");
    }

    /**
     * Converts xmldate into database-specific format. Eg for where-clauses
     *
     * @param xmldate date in xml-format
     * @return String date in database-specific format
     * @throws SqlDatabaseSystemException e
     */
    private static String convertDateForSelect(final String xmldate) throws SqlDatabaseSystemException {
        try {
            String dateFormatString = "yyyy-MM-dd";
            if (xmldate.contains(":")) {
                dateFormatString = "yyyy-MM-dd HH:mm:ss";
            }
            final XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(xmldate);
            final Calendar cal = xmlCal.toGregorianCalendar();
            final SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
            return DATE_FUNCTION.replaceFirst("\\$\\{date_placeholder\\}", dateFormat.format(cal.getTime()));
        }
        catch (final Exception e) {
            throw new SqlDatabaseSystemException(e);
        }
    }

    /**
     * Converts xmldate into database-specific format.
     *
     * @param xmldate date in xml-format
     * @return String date in database-specific format
     * @throws de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException
     */
    private static String convertDateForInsert(final String xmldate) throws SqlDatabaseSystemException {
        try {
            String dateFormatString = "yyyy-MM-dd";
            if (xmldate.contains(":")) {
                dateFormatString = "yyyy-MM-dd HH:mm:ss";
            }
            final XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(xmldate);
            final Calendar cal = xmlCal.toGregorianCalendar();
            final SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatString);
            return DATE_FUNCTION.replaceFirst("\\$\\{date_placeholder\\}", dateFormat.format(cal.getTime()));
        }
        catch (final Exception e) {
            throw new SqlDatabaseSystemException(e);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param databaseTableVo databaseTableVo with information about tablename, fieldnames and indexnames.
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     * @see DirectDatabaseAccessorInterface #createTable(DatabaseTableVo)
     */
    @Override
    public void createTable(final DatabaseTableVo databaseTableVo) throws SqlDatabaseSystemException {
        checkDatabaseTableVo(databaseTableVo);
        final Collection<String> sqls = getCreateStatements(databaseTableVo);
        try {
            for (final String sql : sqls) {
                getJdbcTemplate().execute(sql);
            }
        }
        catch (final Exception e) {
            throw new SqlDatabaseSystemException(e);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param databaseTableVo databaseTableVo.
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     * @see DirectDatabaseAccessorInterface #dropTable(DatabaseTableVo)
     */
    @Override
    public void dropTable(final DatabaseTableVo databaseTableVo) throws SqlDatabaseSystemException {
        checkDatabaseTableVo(databaseTableVo);
        final Collection<String> sqls = getDropStatements(databaseTableVo);
        try {
            for (final String sql : sqls) {
                getJdbcTemplate().execute(sql);
            }
        }
        catch (final Exception e) {
            throw new SqlDatabaseSystemException(e);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param databaseRecordVo databaseRecordVo with information about tablename and fieldnames + values.
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     * @see DirectDatabaseAccessorInterface #createRecord(DatabaseRecordVo)
     */
    @Override
    public void createRecord(final DatabaseRecordVo databaseRecordVo) throws SqlDatabaseSystemException {
        checkDatabaseRecordVo(databaseRecordVo);
        try {
            final String tablename = handleTableName(databaseRecordVo.getTableName());
            final StringBuilder sql = new StringBuilder("");
            final StringBuilder fieldsSql = new StringBuilder("");
            final StringBuilder valuesSql = new StringBuilder(" VALUES (");
            fieldsSql.append("INSERT INTO ").append(tablename).append(" (");
            final Collection<DatabaseRecordFieldVo> fields = databaseRecordVo.getDatabaseRecordFieldVos();
            int i = 0;
            for (final DatabaseRecordFieldVo field : fields) {
                if (i > 0) {
                    fieldsSql.append(',');
                    valuesSql.append(',');
                }
                fieldsSql.append(field.getFieldName());

                String value = field.getFieldValue();

                // Case type=date and value=sysdate => sysdate
                // (is 'now' in postgres)
                if (field.getFieldType().equalsIgnoreCase(Constants.DATABASE_FIELD_TYPE_DATE)) {
                    value = "sysdate".equalsIgnoreCase(value) ? SYSDATE : convertDateForInsert(value);
                }
                else if (field.getFieldType().equalsIgnoreCase(Constants.DATABASE_FIELD_TYPE_TEXT)) {
                    value = value.replaceAll("'", "''");
                    value = '\'' + value + '\'';
                }

                valuesSql.append(value);
                i++;
            }
            fieldsSql.append(") ");
            valuesSql.append(");");
            sql.append(fieldsSql).append(valuesSql);
            getJdbcTemplate().execute(sql.toString());
        }
        catch (final Exception e) {
            throw new SqlDatabaseSystemException(e);
        }

    }

    /**
     * See Interface for functional description.
     *
     * @param databaseSelectVo databaseSelectVo.
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     * @see DirectDatabaseAccessorInterface #deleteRecord(DatabaseRecordVo)
     */
    @Override
    public void deleteRecord(final DatabaseSelectVo databaseSelectVo) throws SqlDatabaseSystemException {
        checkDatabaseSelectVo(databaseSelectVo);
        try {
            final String tablename = handleTableName(databaseSelectVo.getTableNames().iterator().next());
            final StringBuilder sql = new StringBuilder("");
            sql.append("DELETE FROM ").append(tablename);
            if (databaseSelectVo.getRootWhereGroupVo() != null) {
                sql.append(" WHERE ");
                sql.append(handleWhereClause(databaseSelectVo));
            }
            sql.append(';');

            getJdbcTemplate().execute(sql.toString());
        }
        catch (final Exception e) {
            throw new SqlDatabaseSystemException(e);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param databaseSelectVo databaseSelectVo.
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     * @see DirectDatabaseAccessorInterface #updateRecord(DatabaseSelectVo)
     */
    @Override
    public void updateRecord(final DatabaseSelectVo databaseSelectVo) throws SqlDatabaseSystemException {
        checkDatabaseSelectVo(databaseSelectVo);
        try {
            final String tablename = handleTableName(databaseSelectVo.getTableNames().iterator().next());
            final StringBuilder sql = new StringBuilder("");
            sql.append("UPDATE ").append(tablename).append(" SET ");
            int i = 0;
            for (final SelectFieldVo selectFieldVo : databaseSelectVo.getSelectFieldVos()) {
                if (i > 0) {
                    sql.append(',');
                }
                sql.append(handleFieldTypeWhere(null, selectFieldVo.getFieldName(), selectFieldVo.getFieldType(),
                    selectFieldVo.getFieldValue(), "=", null));
                i++;
            }
            if (databaseSelectVo.getRootWhereGroupVo() != null) {
                sql.append(" WHERE ");
                sql.append(handleWhereClause(databaseSelectVo));
            }
            sql.append(';');

            getJdbcTemplate().execute(sql.toString());
        }
        catch (final Exception e) {
            throw new SqlDatabaseSystemException(e);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param databaseSelectVo databaseSelectVo with information about sql.
     * @return List of Maps with data (one Map per db-record)
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     * @see DirectDatabaseAccessorInterface #executeSql(DatabaseSelectVo)
     */
    @Override
    public List executeSql(final DatabaseSelectVo databaseSelectVo) throws SqlDatabaseSystemException {
        checkDatabaseSelectVo(databaseSelectVo);
        try {
            final StringBuilder sql = new StringBuilder("");
            sql.append(databaseSelectVo.getSelectType()).append(' ');
            if (databaseSelectVo.getSelectType().equalsIgnoreCase(Constants.DATABASE_SELECT_TYPE_UPDATE)) {
                final String tablename = handleTableName(databaseSelectVo.getTableNames().iterator().next());
                sql.append(tablename).append(" SET ");
            }
            else if (databaseSelectVo.getSelectType().equalsIgnoreCase(Constants.DATABASE_SELECT_TYPE_DELETE)) {
                final String tablename = handleTableName(databaseSelectVo.getTableNames().iterator().next());
                sql.append(" FROM ").append(tablename).append(' ');
            }
            else if (databaseSelectVo.getSelectType().equalsIgnoreCase(Constants.DATABASE_SELECT_TYPE_SELECT)) {
                sql.append(handleSelectFields(databaseSelectVo.getSelectFieldVos()));
                sql.append(" FROM ");
                int i = 0;
                for (final String tabname : databaseSelectVo.getTableNames()) {
                    final String tablename = handleTableName(tabname);
                    if (i > 0) {
                        sql.append(',');
                    }
                    sql.append(tablename).append(" AS ");
                    sql.append(tablename.replaceFirst(".*?\\.", "")).append(' ');
                    i++;
                }
                if (databaseSelectVo.getRootWhereGroupVo() != null) {
                    sql.append(" WHERE ");
                    sql.append(handleWhereClause(databaseSelectVo));
                }
            }

            sql.append(';');

            return getJdbcTemplate().queryForList(sql.toString());
        }
        catch (final Exception e) {
            throw new SqlDatabaseSystemException(e);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param sql sql-String
     * @return List of Maps with data
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     * @see DirectDatabaseAccessorInterface #executeSql(java.lang.String)
     */
    @Override
    public List executeReadOnlySql(final String sql) throws SqlDatabaseSystemException {
        String executionSql = sql;
        executionSql = executionSql.replaceAll("\\s+", " ");
        boolean condition = false;
        if (executionSql.matches("(?i).* (where|order by|group by) .*")) {
            condition = true;
        }
        final String fromClause =
            condition ? executionSql.replaceFirst("(?i).*?from(.*?)(where|order by|group by).*", "$1") : executionSql
                .replaceFirst("(?i).*?from(.*)", "$1");
        final String[] tables = SPLIT_PATTERN.split(fromClause);
        final StringBuilder replacedFromClause = new StringBuilder(" ");
        for (int i = 0; i < tables.length; i++) {
            if (i > 0) {
                replacedFromClause.append(',');
            }
            replacedFromClause.append(handleTableName(tables[i].trim()));
        }
        replacedFromClause.append(' ');
        executionSql =
            condition ? executionSql.replaceFirst("(?i)(.*?from).*?((where|order by|group by).*)", "$1"
                + Matcher.quoteReplacement(replacedFromClause.toString()) + "$2") : executionSql.replaceFirst(
                "(?i)(.*?from).*", "$1" + Matcher.quoteReplacement(replacedFromClause.toString()));

        try {
            return getJdbcTemplate().queryForList(executionSql);
        }
        catch (final Exception e) {
            throw new SqlDatabaseSystemException(e);
        }
    }

    /**
     * Create create sql-statement from databaseTableVo.
     *
     * @param databaseTableVo databaseTableVo with information about tablename, fieldnames and indexnames.
     * @return Collection with sql-statements
     */
    private Collection<String> getCreateStatements(final DatabaseTableVo databaseTableVo) {

        final Collection<String> sqlStatements = new ArrayList<String>();
        // Get Create-Statement for Table
        final String tablename = handleTableName(databaseTableVo.getTableName());
        final StringBuilder createSql = new StringBuilder("CREATE TABLE ");
        createSql.append(tablename).append(" (");
        int i = 0;
        for (final DatabaseTableFieldVo databaseTableFieldVo : databaseTableVo.getDatabaseFieldVos()) {
            if (i > 0) {
                createSql.append(',');
            }
            String dbDataType = "";
            if (databaseTableFieldVo.getFieldType().equals(Constants.DATABASE_FIELD_TYPE_DATE)) {
                dbDataType = TIMESTAMP_FIELD_TYPE;
            }
            else if (databaseTableFieldVo.getFieldType().equals(Constants.DATABASE_FIELD_TYPE_NUMERIC)) {
                dbDataType = NUMERIC_FIELD_TYPE;
            }
            else if (databaseTableFieldVo.getFieldType().equals(Constants.DATABASE_FIELD_TYPE_TEXT)) {
                dbDataType = TEXT_FIELD_TYPE;
            }
            createSql.append(databaseTableFieldVo.getFieldName()).append(' ').append(dbDataType).append("");
            i++;
        }
        createSql.append(");");
        sqlStatements.add(createSql.toString());

        // Get Create-Statements for Indexes
        final Collection<DatabaseIndexVo> databaseIndexVos = databaseTableVo.getDatabaseIndexVos();
        if (databaseIndexVos != null) {
            for (final DatabaseIndexVo databaseIndexVo : databaseIndexVos) {
                final StringBuilder indexSql = new StringBuilder("CREATE INDEX ");
                final String indexName = databaseIndexVo.getIndexName();
                indexSql.append(indexName).append(" ON ").append(tablename).append(" (");
                final Collection<String> indexFields = databaseIndexVo.getFields();
                int j = 0;
                for (final String indexField : indexFields) {
                    if (j > 0) {
                        indexSql.append(',');
                    }
                    indexSql.append(indexField);
                    j++;
                }
                indexSql.append(");");
                sqlStatements.add(indexSql.toString());
            }
        }
        return sqlStatements;

    }

    /**
     * Create drop sql-statements from databaseTableVo.
     *
     * @param databaseTableVo databaseTableVo with information about tablename and indexnames.
     * @return Collection with sql-statements
     */
    private Collection<String> getDropStatements(final DatabaseTableVo databaseTableVo) {
        final Collection<String> sqlStatements = new ArrayList<String>();
        final String tablename = handleTableName(databaseTableVo.getTableName());
        // Get Drop-Statements for Indexes
        final Collection<DatabaseIndexVo> databaseIndexVos = databaseTableVo.getDatabaseIndexVos();
        if (databaseIndexVos != null) {
            for (final DatabaseIndexVo databaseIndexVo : databaseIndexVos) {
                final StringBuilder indexSql = new StringBuilder("DROP INDEX ");
                String indexName = databaseIndexVo.getIndexName();
                if (!indexName.matches(".*\\..*")) {
                    indexName = Constants.SM_SCHEMA_NAME + '.' + indexName;
                }
                indexSql.append(indexName).append(';');
                sqlStatements.add(indexSql.toString());
            }
        }

        // Get Drop-Statement for Table
        final StringBuilder dropSql = new StringBuilder("DROP TABLE ");
        dropSql.append(tablename);
        sqlStatements.add(dropSql.toString());

        return sqlStatements;

    }

    /**
     * makes string for where-clause out of databaseSelectVo.
     *
     * @param databaseSelectVo databaseSelectVo.
     * @return String string for where clause
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     */
    private String handleWhereClause(final DatabaseSelectVo databaseSelectVo) throws SqlDatabaseSystemException {
        final StringBuilder whereClause = new StringBuilder(" ");
        boolean additionalWhereGroups = false;
        if (databaseSelectVo.getAdditionalWhereGroupVos() != null
            && !databaseSelectVo.getAdditionalWhereGroupVos().isEmpty()) {
            additionalWhereGroups = true;
        }
        if (additionalWhereGroups) {
            whereClause.append('(');
        }
        final RootWhereGroupVo rootWhereGroupVo = databaseSelectVo.getRootWhereGroupVo();
        RootWhereFieldVo rootWhereFieldVo = rootWhereGroupVo.getRootWhereFieldVo();
        if (databaseSelectVo.getSelectType().equals(Constants.DATABASE_SELECT_TYPE_UPDATE)
            || databaseSelectVo.getSelectType().equals(Constants.DATABASE_SELECT_TYPE_DELETE)) {
            rootWhereFieldVo.setTableName(null);
        }
        whereClause.append(handleFieldTypeWhere(rootWhereFieldVo.getTableName(), rootWhereFieldVo.getFieldName(),
            rootWhereFieldVo.getFieldType(), rootWhereFieldVo.getFieldValue(), rootWhereFieldVo.getOperator(),
            rootWhereFieldVo.getXpath()));
        if (rootWhereGroupVo.getAdditionalWhereFieldVos() != null
            && !rootWhereGroupVo.getAdditionalWhereFieldVos().isEmpty()) {
            for (final AdditionalWhereFieldVo additionalWhereFieldVo : rootWhereGroupVo.getAdditionalWhereFieldVos()) {
                whereClause.append(additionalWhereFieldVo.getAlliance());
                if (databaseSelectVo.getSelectType().equals(Constants.DATABASE_SELECT_TYPE_UPDATE)
                    || databaseSelectVo.getSelectType().equals(Constants.DATABASE_SELECT_TYPE_DELETE)) {
                    additionalWhereFieldVo.setTableName(null);
                }
                whereClause.append(handleFieldTypeWhere(additionalWhereFieldVo.getTableName(), additionalWhereFieldVo
                    .getFieldName(), additionalWhereFieldVo.getFieldType(), additionalWhereFieldVo.getFieldValue(),
                    additionalWhereFieldVo.getOperator(), additionalWhereFieldVo.getXpath()));
            }
        }
        if (additionalWhereGroups) {
            whereClause.append(") ");
            for (final AdditionalWhereGroupVo additionalWhereGroupVo : databaseSelectVo.getAdditionalWhereGroupVos()) {
                whereClause.append(additionalWhereGroupVo.getAlliance());
                whereClause.append(" (");
                rootWhereFieldVo = additionalWhereGroupVo.getRootWhereFieldVo();
                if (databaseSelectVo.getSelectType().equals(Constants.DATABASE_SELECT_TYPE_UPDATE)
                    || databaseSelectVo.getSelectType().equals(Constants.DATABASE_SELECT_TYPE_DELETE)) {
                    rootWhereFieldVo.setTableName(null);
                }
                whereClause.append(handleFieldTypeWhere(rootWhereFieldVo.getTableName(), rootWhereFieldVo
                    .getFieldName(), rootWhereFieldVo.getFieldType(), rootWhereFieldVo.getFieldValue(),
                    rootWhereFieldVo.getOperator(), rootWhereFieldVo.getXpath()));
                if (additionalWhereGroupVo.getAdditionalWhereFieldVos() != null
                    && !additionalWhereGroupVo.getAdditionalWhereFieldVos().isEmpty()) {
                    for (final AdditionalWhereFieldVo additionalWhereFieldVo : additionalWhereGroupVo
                        .getAdditionalWhereFieldVos()) {
                        whereClause.append(additionalWhereFieldVo.getAlliance());
                        if (databaseSelectVo.getSelectType().equals(Constants.DATABASE_SELECT_TYPE_UPDATE)
                            || databaseSelectVo.getSelectType().equals(Constants.DATABASE_SELECT_TYPE_DELETE)) {
                            additionalWhereFieldVo.setTableName(null);
                        }
                        whereClause.append(handleFieldTypeWhere(additionalWhereFieldVo.getTableName(),
                            additionalWhereFieldVo.getFieldName(), additionalWhereFieldVo.getFieldType(),
                            additionalWhereFieldVo.getFieldValue(), additionalWhereFieldVo.getOperator(),
                            additionalWhereFieldVo.getXpath()));
                    }
                }
                whereClause.append(") ");
            }
        }

        return whereClause.append(' ').toString();
    }

    /**
     * makes string for where-clause out of databaseSelectVo.
     *
     * @param tableName  tableName.
     * @param fieldName  fieldName.
     * @param fieldType  fieldType.
     * @param fieldValue fieldValue.
     * @param operator   operator.
     * @param xpath      xpath.
     * @return String string for where clause
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     */
    private String handleFieldTypeWhere(
        final String tableName, final String fieldName, final String fieldType, final String fieldValue,
        final String operator, final String xpath) throws SqlDatabaseSystemException {
        final StringBuilder whereClause = new StringBuilder(" ");
        final StringBuilder longFieldName = new StringBuilder("");
        if (tableName != null && tableName.length() != 0) {
            longFieldName.append(handleTableName(tableName).replaceFirst(".*?\\.", "")).append('.');
        }
        longFieldName.append(fieldName);
        if (fieldType.equalsIgnoreCase(Constants.DATABASE_FIELD_TYPE_TEXT)) {
            final String value = fieldValue.replaceAll("'", "''");
            whereClause.append(longFieldName).append(' ').append(operator).append(" '").append(value).append("' ");
        }
        else if (fieldType.endsWith(Constants.DATABASE_FIELD_TYPE_DATE)) {
            if (fieldType.equalsIgnoreCase(Constants.DATABASE_FIELD_TYPE_DAYDATE)) {
                final String dayOfMonthFunction =
                    FIELD_NAME_MATCHER.reset(DATE_TO_CHAR_DAY_OF_MONTH_FUNCTION).replaceAll(
                        Matcher.quoteReplacement(longFieldName.toString()));

                whereClause.append(dayOfMonthFunction).append(operator).append(' ');
            }
            else {
                final String dateToCharFunction =
                    FIELD_NAME_MATCHER.reset(DATE_TO_CHAR_FUNCTION).replaceAll(
                        Matcher.quoteReplacement(longFieldName.toString()));
                whereClause.append(dateToCharFunction).append(operator).append(' ');
            }
            final String value = "sysdate".equalsIgnoreCase(fieldValue) ? SYSDATE : convertDateForSelect(fieldValue);
            whereClause.append(value).append(' ');
        }
        else if (fieldType.equalsIgnoreCase(Constants.DATABASE_FIELD_TYPE_NUMERIC)) {
            whereClause.append(longFieldName).append(' ').append(operator).append(fieldValue).append(' ');
        }
        else if (fieldType.equalsIgnoreCase(Constants.DATABASE_FIELD_TYPE_XPATH_BOOLEAN)) {
            whereClause.append(getXpathBoolean(xpath, fieldName)).append(' ');
        }
        else if (fieldType.equalsIgnoreCase(Constants.DATABASE_FIELD_TYPE_XPATH_STRING)) {
            whereClause.append(getXpathString(xpath, fieldName)).append(' ').append(operator).append(" '").append(
                fieldValue.trim()).append("' ");
        }
        else if (fieldType.equalsIgnoreCase(Constants.DATABASE_FIELD_TYPE_XPATH_NUMERIC)) {
            whereClause.append(getXpathNumeric(xpath, fieldName)).append(' ').append(operator).append(" '").append(
                fieldValue.trim()).append("' ");
        }
        else if (fieldType.equalsIgnoreCase(Constants.DATABASE_FIELD_TYPE_FREE_SQL)) {
            whereClause.append(fieldValue);
        }
        else {
            throw new SqlDatabaseSystemException("wrong fieldType given");
        }
        return whereClause.append(' ').toString();
    }

    /**
     * Handles select-fields for sql-query.
     *
     * @param selectFieldVos Collection of SelectFieldVo.
     * @return String string with fields to select
     * @throws SqlDatabaseSystemException If an error occurs accessing the database.
     */
    private String handleSelectFields(final Iterable<SelectFieldVo> selectFieldVos) {
        final StringBuilder selectFields = new StringBuilder(" ");
        int i = 0;
        for (final SelectFieldVo selectFieldVo : selectFieldVos) {
            if (i > 0) {
                selectFields.append(',');
            }
            if ("*".equals(selectFieldVo.getFieldName())) {
                selectFields.append('*');
                break;
            }
            if (selectFieldVo.getTableName() != null && selectFieldVo.getTableName().length() != 0) {
                final String tablename = handleTableName(selectFieldVo.getTableName()).replaceFirst(".*?\\.", "");
                selectFields.append(tablename).append('.');
            }
            selectFields.append(selectFieldVo.getFieldName());
            i++;
        }

        return selectFields.append(' ').toString();
    }

    /**
     * Get database-dependant sql-query-part for an xpath-boolean request.
     *
     * @param xpath xpath-expression.
     * @param field db-field.
     * @return String database-dependant query for an xpath-boolean request.
     */
    @Override
    public String getXpathBoolean(final String xpath, final String field) {
        return XPATH_MATCHER.reset(XPATH_BOOLEAN_FUNCTION).replaceAll(
            Matcher.quoteReplacement(xpath) + "$1" + Matcher.quoteReplacement(field));
    }

    /**
     * Get database-dependant sql-query-part for an xpath-string request.
     *
     * @param xpath xpath-expression.
     * @param field db-field.
     * @return String database-dependant query for an xpath-string request.
     */
    @Override
    public String getXpathString(final String xpath, final String field) {
        final StringBuilder replacedXpath = new StringBuilder(xpath.trim());
        if (!replacedXpath.toString().endsWith("text()")) {
            if (!replacedXpath.toString().endsWith("/")) {
                replacedXpath.append('/');
            }
            replacedXpath.append("text()");
        }
        return XPATH_MATCHER.reset(XPATH_STRING_FUNCTION).replaceAll(
            Matcher.quoteReplacement(replacedXpath.toString()) + "$1" + Matcher.quoteReplacement(field));
    }

    /**
     * Get database-dependant sql-query-part for an xpath-numeric request.
     *
     * @param xpath xpath-expression.
     * @param field db-field.
     * @return String database-dependant query for an xpath-string request.
     */
    String getXpathNumeric(final String xpath, final String field) {
        final StringBuilder replacedXpath = new StringBuilder(xpath.trim());
        if (!replacedXpath.toString().endsWith("text()")) {
            if (!replacedXpath.toString().endsWith("/")) {
                replacedXpath.append('/');
            }
            replacedXpath.append("text()");
        }
        return XPATH_MATCHER.reset(XPATH_NUMBER_FUNCTION).replaceAll(
            Matcher.quoteReplacement(replacedXpath.toString()) + "$1" + Matcher.quoteReplacement(field));
    }

    /**
     * checks DatabaseTableVo for validity.
     *
     * @param databaseTableVo databaseTableVo.
     * @throws SqlDatabaseSystemException SqlDatabaseSystemException
     */
    public void checkDatabaseTableVo(final DatabaseTableVo databaseTableVo) throws SqlDatabaseSystemException {
        if (databaseTableVo.getTableName() == null || databaseTableVo.getTableName().length() == 0) {
            throw new SqlDatabaseSystemException("tablename may not be empty");
        }
        if (databaseTableVo.getDatabaseFieldVos() == null || databaseTableVo.getDatabaseFieldVos().isEmpty()) {
            throw new SqlDatabaseSystemException("database-fields may not be empty");
        }
        for (final DatabaseTableFieldVo databaseTableFieldVo : databaseTableVo.getDatabaseFieldVos()) {
            if (databaseTableFieldVo.getFieldName() == null || databaseTableFieldVo.getFieldName().length() == 0) {
                throw new SqlDatabaseSystemException("fieldname may not be empty");
            }
            if (databaseTableFieldVo.getFieldType() == null || databaseTableFieldVo.getFieldType().length() == 0) {
                throw new SqlDatabaseSystemException("fieldtype may not be empty");
            }

        }
        if (databaseTableVo.getDatabaseIndexVos() != null) {
            for (final DatabaseIndexVo databaseIndexVo : databaseTableVo.getDatabaseIndexVos()) {
                if (databaseIndexVo.getIndexName() == null || databaseIndexVo.getIndexName().length() == 0) {
                    throw new SqlDatabaseSystemException("indexname may not be empty");
                }
                if (databaseIndexVo.getFields() == null) {
                    throw new SqlDatabaseSystemException("indexfields may not be empty");
                }
                for (final String field : databaseIndexVo.getFields()) {
                    if (field == null || field.length() == 0) {
                        throw new SqlDatabaseSystemException("indexfield may not be null");
                    }
                }

            }
        }
    }

    /**
     * checks DatabaseRecordVo for validity.
     *
     * @param databaseRecordVo databaseRecordVo.
     * @throws SqlDatabaseSystemException SqlDatabaseSystemException
     */
    public void checkDatabaseRecordVo(final DatabaseRecordVo databaseRecordVo) throws SqlDatabaseSystemException {
        if (databaseRecordVo.getTableName() == null || databaseRecordVo.getTableName().length() == 0) {
            throw new SqlDatabaseSystemException("tablename may not be empty");
        }
        if (databaseRecordVo.getDatabaseRecordFieldVos() == null
            || databaseRecordVo.getDatabaseRecordFieldVos().isEmpty()) {
            throw new SqlDatabaseSystemException("database-fields may not be empty");
        }
        for (final DatabaseRecordFieldVo databaseRecordFieldVo : databaseRecordVo.getDatabaseRecordFieldVos()) {
            if (databaseRecordFieldVo.getFieldName() == null || databaseRecordFieldVo.getFieldName().length() == 0) {
                throw new SqlDatabaseSystemException("fieldname may not be empty");
            }
            if (databaseRecordFieldVo.getFieldType() == null || databaseRecordFieldVo.getFieldType().length() == 0) {
                throw new SqlDatabaseSystemException("fieldtype may not be empty");
            }
            if (databaseRecordFieldVo.getFieldValue() == null) {
                throw new SqlDatabaseSystemException("fieldvalue may not be empty");
            }
        }
    }

    /**
     * checks DatabaseSelectVo for validity.
     *
     * @param databaseSelectVo databaseSelectVo.
     * @throws SqlDatabaseSystemException SqlDatabaseSystemException
     */
    public void checkDatabaseSelectVo(final DatabaseSelectVo databaseSelectVo) throws SqlDatabaseSystemException {
        if (databaseSelectVo.getTableNames() == null || databaseSelectVo.getTableNames().isEmpty()) {
            throw new SqlDatabaseSystemException("tablenames may not be empty");
        }
        for (final String tablename : databaseSelectVo.getTableNames()) {
            if (tablename == null || tablename.length() == 0) {
                throw new SqlDatabaseSystemException("tablename may not be null");
            }
        }
        if (databaseSelectVo.getSelectType() == null || databaseSelectVo.getSelectType().length() == 0) {
            throw new SqlDatabaseSystemException("database-fields may not be empty");
        }
        if (databaseSelectVo.getSelectType().equals(Constants.DATABASE_SELECT_TYPE_SELECT)
            || databaseSelectVo.getSelectType().equals(Constants.DATABASE_SELECT_TYPE_UPDATE)) {
            if (databaseSelectVo.getSelectFieldVos() == null || databaseSelectVo.getSelectFieldVos().isEmpty()) {
                throw new SqlDatabaseSystemException("select-fields may not be empty");
            }
            for (final SelectFieldVo selectFieldVo : databaseSelectVo.getSelectFieldVos()) {
                if (selectFieldVo.getFieldName() == null || selectFieldVo.getFieldName().length() == 0) {
                    throw new SqlDatabaseSystemException("select-fieldname may not be empty");
                }
                if (databaseSelectVo.getTableNames().size() > 1
                    && (selectFieldVo.getTableName() == null || selectFieldVo.getTableName().length() == 0)) {
                    throw new SqlDatabaseSystemException("select-field-tablename may not be empty");
                }
                if (databaseSelectVo.getSelectType().equals(Constants.DATABASE_SELECT_TYPE_UPDATE)) {
                    if (selectFieldVo.getFieldValue() == null) {
                        throw new SqlDatabaseSystemException("select-field-value may not be null");
                    }
                    if (selectFieldVo.getFieldType() == null || selectFieldVo.getFieldType().length() == 0) {
                        throw new SqlDatabaseSystemException("select-field-type may not be empty");
                    }
                }
            }
        }
        if (databaseSelectVo.getAdditionalWhereGroupVos() != null
            && !databaseSelectVo.getAdditionalWhereGroupVos().isEmpty()
            && databaseSelectVo.getRootWhereGroupVo() == null) {
            throw new SqlDatabaseSystemException("root where group may not be empty");
        }
        if (databaseSelectVo.getRootWhereGroupVo() != null) {
            final RootWhereGroupVo rootWhereGroupVo = databaseSelectVo.getRootWhereGroupVo();
            if (rootWhereGroupVo.getRootWhereFieldVo() == null) {
                throw new SqlDatabaseSystemException("root where field may not be empty");
            }
            final RootWhereFieldVo rootWhereFieldVo = rootWhereGroupVo.getRootWhereFieldVo();
            checkWhereFieldVo("root", rootWhereFieldVo.getFieldName(), rootWhereFieldVo.getFieldType(),
                rootWhereFieldVo.getFieldValue(), rootWhereFieldVo.getOperator(), rootWhereFieldVo.getXpath());
            if (rootWhereGroupVo.getAdditionalWhereFieldVos() != null) {
                for (final AdditionalWhereFieldVo additionalWhereFieldVo : rootWhereGroupVo
                    .getAdditionalWhereFieldVos()) {
                    checkWhereFieldVo("additional", additionalWhereFieldVo.getFieldName(), additionalWhereFieldVo
                        .getFieldType(), additionalWhereFieldVo.getFieldValue(), additionalWhereFieldVo.getOperator(),
                        additionalWhereFieldVo.getXpath());
                }
            }

        }
        if (databaseSelectVo.getAdditionalWhereGroupVos() != null) {
            for (final AdditionalWhereGroupVo additionalWhereGroupVo : databaseSelectVo.getAdditionalWhereGroupVos()) {
                if (additionalWhereGroupVo.getRootWhereFieldVo() == null) {
                    throw new SqlDatabaseSystemException("root where field may not be empty");
                }
                final RootWhereFieldVo rootWhereFieldVo = additionalWhereGroupVo.getRootWhereFieldVo();
                checkWhereFieldVo("root", rootWhereFieldVo.getFieldName(), rootWhereFieldVo.getFieldType(),
                    rootWhereFieldVo.getFieldValue(), rootWhereFieldVo.getOperator(), rootWhereFieldVo.getXpath());
                if (additionalWhereGroupVo.getAdditionalWhereFieldVos() != null) {
                    for (final AdditionalWhereFieldVo additionalWhereFieldVo : additionalWhereGroupVo
                        .getAdditionalWhereFieldVos()) {
                        checkWhereFieldVo("additional", additionalWhereFieldVo.getFieldName(), additionalWhereFieldVo
                            .getFieldType(), additionalWhereFieldVo.getFieldValue(), additionalWhereFieldVo
                            .getOperator(), additionalWhereFieldVo.getXpath());
                    }

                }
            }
        }
    }

    /**
     * checks WhereFieldVo for validity.
     *
     * @param type       type (root or additional).
     * @param fieldName  fieldName.
     * @param fieldType  fieldType.
     * @param fieldValue fieldValue.
     * @param operator   operator.
     * @param xpath      xpath.
     * @throws SqlDatabaseSystemException SqlDatabaseSystemException
     */
    private static void checkWhereFieldVo(
        final CharSequence type, final CharSequence fieldName, final String fieldType, final String fieldValue,
        final CharSequence operator, final CharSequence xpath) throws SqlDatabaseSystemException {
        if (type == null || type.length() == 0 || !"root".equals(type) && !"additional".equals(type)) {
            throw new SqlDatabaseSystemException("wrong type given");
        }
        if ("additional".equals(type) && (operator == null || operator.length() == 0)) {
            throw new SqlDatabaseSystemException("operator may not be null");
        }
        if (fieldType == null || fieldType.length() == 0) {
            throw new SqlDatabaseSystemException("fieldtype may not be null");
        }
        if (fieldValue == null) {
            throw new SqlDatabaseSystemException("fieldvalue may not be null");
        }
        if (!fieldType.equals(Constants.DATABASE_FIELD_TYPE_FREE_SQL)) {
            if (fieldName == null || fieldName.length() == 0) {
                throw new SqlDatabaseSystemException("fieldname may not be null");
            }
            if (operator == null || operator.length() == 0) {
                throw new SqlDatabaseSystemException("operator may not be null");
            }
            if (fieldType.startsWith("xpath") && (xpath == null || xpath.length() == 0)) {
                throw new SqlDatabaseSystemException("xpath may not be null");
            }
        }
    }

    /**
     * extends tablename with schemaname and quotes.
     *
     * @param tablename name of table
     * @return String replaced tablename
     */
    public String handleTableName(final String tablename) {
        return tablename.matches(".*\\..*") ? tablename : Constants.SM_SCHEMA_NAME + '.' + tablename;

    }

    /**
     * checks if given fieldname is a reserved expression in the database.
     *
     * @param fieldname name of field
     * @throws SqlDatabaseSystemException e
     */
    @Override
    public void checkReservedExpressions(final String fieldname) throws SqlDatabaseSystemException {
        if (RESERVED_EXPRESSIONS.get(fieldname) != null) {
            throw new SqlDatabaseSystemException(fieldname + " must not be used as fieldname");
        }

    }

    /**
     * Wrapper of setDataSource to enable bean stuff generation for this handler.
     * @param myDataSource
     */
    public void setMyDataSource(final DataSource myDataSource) {
        setDataSource(myDataSource);
    }

}
