/**
 * <h1>Concept:</h1>
 * Package de.escidoc.core.sm.business.vo.database<br>
 * contains Value-Objects for create, insert, update, delete<br>
 * and select-statements to a database.<br>
 * This is done to encapsulate the logic for creating sql-statements <br>
 * for different databases.<br>
 * The VOs are parameters for calls to the classes in package<br>
 * de.escidoc.core.sm.business.persistence.sql<br>
 * <br>
 * <h2>create table:</h2>
 * use classes in package de.escidoc.core.sm.business.vo.database.table<br>
 * DatabaseTableVo, DatabaseTableFieldVo, DatabaseIndexVo<br>
 * <br>
 * <h2>drop table:</h2>
 * use classes in package de.escidoc.core.sm.business.vo.database.table<br>
 * DatabaseTableVo, DatabaseIndexVo<br>
 * databaseTableFieldVo is not needed for drop.<br>
 * <br>
 * <h2>create new record in a database table:</h2>
 * use classes in package de.escidoc.core.sm.business.vo.database.record<br>
 * DatabaseRecordVo, DatabaseRecordFieldVo<br>
 * <br>
 * <h2>change values in one or more records in a database table (update)<br>
 * or<br>
 * delete one or more records in a database table<br>
 * or<br>
 * execute an sql statement:</h2>
 * use classes in package de.escidoc.core.sm.business.vo.database.select<br>
 * <br>
 * <h3>DatabaseSelectVo: </h3>
 * -Root-Object, is the parameter for the calls to the classes<br>
 * in package de.escidoc.core.sm.business.persistence.sql<br>
 * -define selectType: select, update or delete<br>
 * -define Collection with table-names <br>
 * (for update or delete only one tablename is allowed)<br>
 * -define Collection with SelectFieldVos<br>
 * -define RootWhereGroupVo<br>
 * -define Collection of AdditionalWhereGroupVos<br>
 * <br>
 * <h3>SelectFieldVo:</h3>
 * -define tableName and fieldName<br>
 * -if update or delete of records in the database, define fieldValue<br>
 * (used for ...SET FIELD1="VALUE1",....)<br>
 * <br>
 * <h3>RootWhereGroupVo + AdditionalWhereGroupVo:</h3>
 * WhereGroups are groups of where-statements that are enclosed by brackets<br>
 * (eg (FIELD1=VALUE1 AND FIELD2=VALUE2))<br>
 * If only one WhereGroup is used, there will be no brackets in the<br>
 * resulting sql-statement.<br>
 * Always use the RootWhereGroupVo as first WhereGroupVo.<br>
 * This generates the following sql-statement-part:<br>
 * WHERE FIELD1=VALUE1...<br>
 * For the following WhereGroups, use AdditionalWhereGroupVos.<br>
 * These generate where-statements with the given alliance (and/or)<br>
 * If more than one WhereGroup is used, each WhereGroup is enclosed by brackets<br>
 * and the WhereGroups can put together with alliances (and/or).<br>
 * -fill rootWhereFieldVo<br>
 * -optionally fill AdditionalWhereFieldVos<br>
 * <br>
 * <h3>RootWhereFieldVo + AdditionalWhereFieldVo:</h3>
 * WhereFields are groups of Where-Statements. One Where-Statement<br>
 * is written as the following sql-statement-part:<br>
 * FIELD1=/&gt;/&lt;FIELD2<br>
 * Optionally you can put additional where-fields that are concatenated with<br>
 * a given alliance (and/or)<br>
 * -define tablename and fieldname<br>
 * -define fieldtype <br>
 * (text,numeric, date, daydate, xpath-boolean,
 * xpath-string, xpath-numeric, free-sql)<br>
 * -define fieldvalue (for fieldtype text,numeric, date, daydate)<br>
 * or xpath-expression (for fieldtype xpath-boolean,
 * xpath-string, xpath-numeric) <br>
 * -define operator (=,&lt;,&gt;)<br>
 */
package de.escidoc.core.sm.business.vo.database;