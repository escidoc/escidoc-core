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
package de.escidoc.core.sm.business.preprocessing;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.StatisticPreprocessingSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.sm.business.Constants;
import de.escidoc.core.sm.business.persistence.DirectDatabaseAccessorInterface;
import de.escidoc.core.sm.business.persistence.SmPreprocessingLogsDaoInterface;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationDefinition;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationTable;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationTableField;
import de.escidoc.core.sm.business.persistence.hibernate.PreprocessingLog;
import de.escidoc.core.sm.business.vo.database.record.DatabaseRecordFieldVo;
import de.escidoc.core.sm.business.vo.database.record.DatabaseRecordVo;
import de.escidoc.core.sm.business.vo.database.select.AdditionalWhereFieldVo;
import de.escidoc.core.sm.business.vo.database.select.DatabaseSelectVo;
import de.escidoc.core.sm.business.vo.database.select.RootWhereFieldVo;
import de.escidoc.core.sm.business.vo.database.select.RootWhereGroupVo;
import de.escidoc.core.sm.business.vo.database.select.SelectFieldVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.InputSource;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Preprocesses Raw Statistic Data according one Aggregation-Definition and writes it into the aggregation-tables
 * defined in the aggregation-definition.
 *
 * @author Michael Hoppe
 */
@Service("business.AggregationPreprocessor")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AggregationPreprocessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregationPreprocessor.class);

    @Autowired
    @Qualifier("sm.persistence.DirectDatabaseAccessor")
    private DirectDatabaseAccessorInterface dbAccessor;

    @Autowired
    @Qualifier("persistence.SmPreprocessingLogsDao")
    private SmPreprocessingLogsDaoInterface preprocessingLogsDao;

    private XPathFactory xpathFactory;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected AggregationPreprocessor() {
    }

    /**
     * initialize global Hashes (dataHash, fieldTypeHash, differencesHash).
     *
     * @param aggregationDefinitionIn AggregationDefinition Binding Object.
     * @return AggregationPreprocessorVo initialized Data-Structure
     * @throws StatisticPreprocessingSystemException
     *          e
     */
    private AggregationPreprocessorVo initVo(final AggregationDefinition aggregationDefinitionIn)
        throws StatisticPreprocessingSystemException {
        if (aggregationDefinitionIn == null) {
            throw new StatisticPreprocessingSystemException("AggregationDefinition may not be null");
        }
        if (aggregationDefinitionIn.getAggregationTables() == null
            || aggregationDefinitionIn.getAggregationTables().isEmpty()) {
            throw new StatisticPreprocessingSystemException("Aggregation-Tables is null");
        }
        final AggregationPreprocessorVo aggregationPreprocessorVo = new AggregationPreprocessorVo();
        aggregationPreprocessorVo.setAggregationDefinition(aggregationDefinitionIn);
        for (final AggregationTable aggregationTable : aggregationDefinitionIn.getAggregationTables()) {
            if (aggregationTable.getName() == null || aggregationTable.getName().length() == 0) {
                throw new StatisticPreprocessingSystemException("Aggregation-Table-Name is null");
            }
            aggregationPreprocessorVo.getDataHash().put(aggregationTable.getName(), new HashMap());
            aggregationPreprocessorVo.getFieldTypeHash().put(aggregationTable.getName(),
                initFieldTypeHash(aggregationTable.getAggregationTableFields()));

        }
        return aggregationPreprocessorVo;
    }

    /**
     * initialize FieldHash.
     * <p/>
     * fieldTypeHash-Structure: -key: tablename, value:HashMap within this HashMap: -key: "fieldtype", value: HashMap
     * within this HashMap: -key: fieldname, value: filedtype (info, time-reduction, count-cumulation,
     * difference-cummulation -key: "dbtype", value: HashMap within this HashMap: -key: fieldname, value:fieldtype
     * (text,numeric, date)
     *
     * @param fieldList List of Table-Fields out of Aggregation-Definition.
     * @return HashMap fieldInfos
     * @throws StatisticPreprocessingSystemException
     *          e
     */
    private static Map initFieldTypeHash(final Collection<AggregationTableField> fieldList)
        throws StatisticPreprocessingSystemException {
        final Map fields = new HashMap();
        final HashMap fieldtypes = new HashMap();
        final HashMap dbtypes = new HashMap();
        if (fieldList == null || fieldList.isEmpty()) {
            throw new StatisticPreprocessingSystemException("Field-List is null");
        }
        for (final AggregationTableField field : fieldList) {
            if (field.getFieldTypeId() == Constants.COUNT_CUMULATION_FIELD_ID) {
                fieldtypes.put(field.getName(), Constants.COUNT_CUMULATION_FIELD);
                dbtypes.put(field.getName(), Constants.DATABASE_FIELD_TYPE_NUMERIC);
            }
            else if (field.getFieldTypeId() == Constants.DIFFERENCE_CUMULATION_FIELD_ID) {
                fieldtypes.put(field.getName(), Constants.DIFFERENCE_CUMULATION_FIELD);
                dbtypes.put(field.getName(), Constants.DATABASE_FIELD_TYPE_NUMERIC);
            }
            else if (field.getFieldTypeId() == Constants.INFO_FIELD_ID) {
                fieldtypes.put(field.getName(), Constants.INFO_FIELD);
                dbtypes.put(field.getName(), field.getDataType());
            }
            else if (field.getFieldTypeId() == Constants.TIME_REDUCTION_FIELD_ID) {
                fieldtypes.put(field.getName(), Constants.TIME_REDUCTION_FIELD);
                dbtypes.put(field.getName(), Constants.DATABASE_FIELD_TYPE_TEXT);
            }
            else {
                throw new StatisticPreprocessingSystemException("Field is empty");
            }
        }
        fields.put("fieldtype", fieldtypes);
        fields.put("dbtype", dbtypes);
        return fields;
    }

    /**
     * process Aggregation. -Iterate Statistic-records and cumulate data into dataHash. -Persist dataHash into Database.
     * (check if insert or update has to be done).
     *
     * @param aggregationDefinitionIn binding-object
     * @param statisticDatas          List of statisticData-Records as xml
     * @return AggregationPreprocessorVo Vo with preprocessed data
     * @throws StatisticPreprocessingSystemException
     *                                    e
     * @throws SqlDatabaseSystemException e
     */
    public AggregationPreprocessorVo processAggregation(
        final AggregationDefinition aggregationDefinitionIn, final Iterable statisticDatas)
        throws StatisticPreprocessingSystemException {
        this.xpathFactory = XPathFactory.newInstance();
        if (statisticDatas != null) {
            // initialize DataHash depending on AggregationDefinition
            final AggregationPreprocessorVo aggregationPreprocessorVo = initVo(aggregationDefinitionIn);
            // Iterate over statistic-records
            for (final Object statisticData : statisticDatas) {
                final Map map = (Map) statisticData;
                final String xml = (String) map.get(Constants.STATISTIC_DATA_XML_FIELD_NAME);
                final Timestamp timestamp = (Timestamp) map.get(Constants.STATISTIC_DATA_TIMESTAMP_FIELD_NAME);
                try {
                    // put data of statistic-record into dataHash
                    if (xml != null && timestamp != null) {
                        handleRecord(xml, timestamp, aggregationPreprocessorVo);
                    }
                    else {
                        LOGGER.error("xml or timestamp is null");
                    }
                }
                catch (final Exception e) {
                    throw new StatisticPreprocessingSystemException(e);
                }
            }

            return aggregationPreprocessorVo;
        }
        else {
            return null;
        }
    }

    /**
     * process one Statistic Record.
     *
     * @param xml                       statistic-record-xml
     * @param aggregationPreprocessorVo Object that holds all values
     * @param timestamp                 time the record was written
     * @throws StatisticPreprocessingSystemException
     *          e
     */
    private void handleRecord(
        final String xml, final Timestamp timestamp, final AggregationPreprocessorVo aggregationPreprocessorVo)
        throws StatisticPreprocessingSystemException {
        if (aggregationPreprocessorVo.getAggregationDefinition().getAggregationTables() == null
            || aggregationPreprocessorVo.getAggregationDefinition().getAggregationTables().isEmpty()) {
            throw new StatisticPreprocessingSystemException("Aggregation-Tables is null");
        }
        // Iterate over all Tables of this Aggregation
        for (final AggregationTable aggregationTable : aggregationPreprocessorVo
            .getAggregationDefinition().getAggregationTables()) {
            handleTable(xml, timestamp, aggregationTable, aggregationPreprocessorVo);

        }
    }

    /**
     * preprocess one statistic-record for one aggregation-table. -Put values in global -forOneRecord-Hashes. -merge
     * data of -forOneRecord-Hashes into global Hashes for this Aggregation.
     *
     * @param xml                       statistic-record-xml
     * @param timestamp                 time the record was written
     * @param aggregationTable          aggregation-table-binding-object
     * @param aggregationPreprocessorVo Object that holds all values
     * @throws StatisticPreprocessingSystemException
     *          e
     */
    private void handleTable(
        final String xml, final Timestamp timestamp, final AggregationTable aggregationTable,
        final AggregationPreprocessorVo aggregationPreprocessorVo) throws StatisticPreprocessingSystemException {
        // write all fields + values into fieldHashForOneRecord
        // later merge this with dataHash
        aggregationPreprocessorVo.setFieldHashForOneRecord(new HashMap());

        // Write differenceCumulationFields for this statistic-record with
        // key: fieldname and
        // value: fieldname + value from statistic-record
        // as defined in xpathFactory of this field
        aggregationPreprocessorVo.setDifferenceHashForOneRecord(new HashMap());

        // While iterating over the fields, build uniqueKey
        // to see if record with this information already exists in dataHash
        // only put info-field-values and time-reduction-field-values
        aggregationPreprocessorVo.setUniqueKeyForOneRecord(new StringBuffer(""));

        // Iterate over all fields of this Aggregation
        // to get the required data out of the statistic-record
        // as defined for each field as xpathFactory-expression
        for (final AggregationTableField field : aggregationTable.getAggregationTableFields()) {
            if (field.getFieldTypeId() == Constants.COUNT_CUMULATION_FIELD_ID) {
                handleCountCumulationField(field, aggregationPreprocessorVo);
            }
            else if (field.getFieldTypeId() == Constants.DIFFERENCE_CUMULATION_FIELD_ID) {
                handleDifferenceCumulationField(field, xml, aggregationPreprocessorVo);
            }
            else if (field.getFieldTypeId() == Constants.INFO_FIELD_ID) {
                handleInfoField(field, xml, aggregationPreprocessorVo);
            }
            else if (field.getFieldTypeId() == Constants.TIME_REDUCTION_FIELD_ID) {
                handleTimeReductionField(field, xml, timestamp, aggregationPreprocessorVo);
            }
        }

        // merge -forOneRecord-hashes into global Hashes
        // of this Aggregation-Definition
        mergeRecord(aggregationTable.getName(), aggregationPreprocessorVo);

    }

    /**
     * Handle one statistic-record for one info-field. Put values in global -forOneRecord-Hashes.
     *
     * @param field                     info-field
     * @param xml                       statistic-record-xml
     * @param aggregationPreprocessorVo Object that holds all values
     * @throws StatisticPreprocessingSystemException
     *          e
     */
    private void handleInfoField(
        final AggregationTableField field, final String xml, final AggregationPreprocessorVo aggregationPreprocessorVo)
        throws StatisticPreprocessingSystemException {
        // process info-field
        // -get fieldValue by querying statistic-data-xml
        // with XPath-expression of Field
        // -write fieldname + fieldvalue into fieldHash
        // -append fieldValue to uniqueKey
        try {
            String fieldValue =
                (String) xpathFactory.newXPath().evaluate(field.getXpath().replaceAll("\\s+", " "),
                    new InputSource(new ByteArrayInputStream(xml.getBytes(XmlUtility.CHARACTER_ENCODING))),
                    XPathConstants.STRING);

            fieldValue = fieldValue.trim().replaceAll("\\s+", " ");
            aggregationPreprocessorVo.getFieldHashForOneRecord().put(field.getName(), fieldValue);
            aggregationPreprocessorVo.getUniqueKeyForOneRecord().append(fieldValue);
        }
        catch (final Exception e) {
            throw new StatisticPreprocessingSystemException(e);
        }

    }

    /**
     * Handle one statistic-record for one time-reduction-field. Put values in global -forOneRecord-Hashes.
     *
     * @param field                     time-reduction-field
     * @param xml                       statistic-record-xml
     * @param timestamp                 time the record was written
     * @param aggregationPreprocessorVo Object that holds all values
     * @throws StatisticPreprocessingSystemException
     *          e
     */
    private void handleTimeReductionField(
        final AggregationTableField field, final String xml, final Timestamp timestamp,
        final AggregationPreprocessorVo aggregationPreprocessorVo) throws StatisticPreprocessingSystemException {
        // process time-reduction-field
        // -get fieldValue by querying statistic-data-xml
        // with XPath-expression of Field
        // If no xpathFactory-expression is given,
        // use timemarker-field from database
        // -reduce time to specified precision
        // (year,month,day,weekday)
        // -write fieldname + fieldvalue into fieldHashForOneRecord
        // -append fieldValue to uniqueKey
        try {
            final Calendar cal;
            if (field.getXpath() != null && field.getXpath().length() != 0) {
                String fieldValue =
                    (String) xpathFactory.newXPath().evaluate(field.getXpath().replaceAll("\\s+", " "),
                        new InputSource(new ByteArrayInputStream(xml.getBytes(XmlUtility.CHARACTER_ENCODING))),
                        XPathConstants.STRING);
                fieldValue = fieldValue.trim().replaceAll("\\s+", " ");
                final XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(fieldValue);
                cal = xmlCal.toGregorianCalendar();
            }
            else {
                cal = Calendar.getInstance();
                cal.setTimeInMillis(timestamp.getTime());
            }
            final String hashValue = reduceTime(cal, field.getReduceTo());
            aggregationPreprocessorVo.getFieldHashForOneRecord().put(field.getName(), hashValue);
            aggregationPreprocessorVo.getUniqueKeyForOneRecord().append(hashValue);
        }
        catch (final Exception e) {
            throw new StatisticPreprocessingSystemException(e);
        }
    }

    /**
     * Handle one statistic-record for one count-cumulation-field. Put values in global -forOneRecord-Hashes.
     *
     * @param field                     count-cumulation-field
     * @param aggregationPreprocessorVo Object that holds all values
     */
    private static void handleCountCumulationField(
        final AggregationTableField field, final AggregationPreprocessorVo aggregationPreprocessorVo) {
        // process count-cumulation-field:
        // just put 1 in fieldsHash
        aggregationPreprocessorVo.getFieldHashForOneRecord().put(field.getName(), "1");
    }

    /**
     * Handle one statistic-record for one difference-cumulation-field. Put values in global -forOneRecord-Hashes.
     *
     * @param field                     difference-cumulation-field
     * @param xml                       statistic-record-xml
     * @param aggregationPreprocessorVo Object that holds all values
     * @throws StatisticPreprocessingSystemException
     *          e
     */
    private void handleDifferenceCumulationField(
        final AggregationTableField field, final String xml, final AggregationPreprocessorVo aggregationPreprocessorVo)
        throws StatisticPreprocessingSystemException {
        // process difference-cumulation-field
        // -write fieldname + fieldvalue from this statistic-record
        // as defined in xpathFactory-expression in differenceCumulationFields
        // -just put 1 in fieldsHash
        try {
            String fieldValue =
                (String) xpathFactory.newXPath().evaluate(field.getXpath().replaceAll("\\s+", " "),
                    new InputSource(new ByteArrayInputStream(xml.getBytes(XmlUtility.CHARACTER_ENCODING))),
                    XPathConstants.STRING);
            fieldValue = fieldValue.trim().replaceAll("\\s+", " ");
            aggregationPreprocessorVo
                .getDifferenceHashForOneRecord().put(field.getName(), field.getName() + fieldValue);
        }
        catch (final Exception e) {
            throw new StatisticPreprocessingSystemException(e);
        }
        aggregationPreprocessorVo.getFieldHashForOneRecord().put(field.getName(), "1");

    }

    /**
     * merge values of global -ForOneRecord-Hashes into global Hashes for this aggregation.
     *
     * @param tablename                 tablename
     * @param aggregationPreprocessorVo Object that holds all values
     * @throws StatisticPreprocessingSystemException
     *          e
     */
    private static void mergeRecord(final String tablename, final AggregationPreprocessorVo aggregationPreprocessorVo) {
        // Iterate over differenceCumulationFields,
        // build unique key with tablename,
        // name of difference-cumulation-field,
        // uniqueKey that was generated with this statistic-record
        // and fieldname+fieldvalue.
        // Look if this key already exists
        // in global variable differencesHash
        // if it exists: set value of field in fieldHash to 0
        // if it doesnt exist: write it into global variable differencesHash
        if (aggregationPreprocessorVo.getDifferenceHashForOneRecord() != null) {
            for (final Object o : aggregationPreprocessorVo.getDifferenceHashForOneRecord().keySet()) {
                final String fieldname = (String) o;
                final StringBuilder key = new StringBuilder(tablename);
                key.append('|').append(fieldname).append('|').append(
                    aggregationPreprocessorVo.getUniqueKeyForOneRecord().toString()).append('|').append(
                    aggregationPreprocessorVo.getDifferenceHashForOneRecord().get(fieldname));
                if (aggregationPreprocessorVo.getDifferencesHash().get(key.toString()) == null) {
                    aggregationPreprocessorVo.getDifferencesHash().put(key.toString(), "");
                }
                else {
                    aggregationPreprocessorVo.getFieldHashForOneRecord().put(fieldname, "0");
                }
            }
        }

        // Look if uniqueKey already exists in dataHash
        // if yes: just add the secified values
        // of the count-cumulation-fields
        // and difference-cumulation-fields to the record in the dataHash
        // if no: add new record with uniqueKey to dataHash
        if (((Map) aggregationPreprocessorVo.getDataHash().get(tablename)).get(aggregationPreprocessorVo
            .getUniqueKeyForOneRecord().toString()) != null) {
            final Map record =
                (Map) ((Map) aggregationPreprocessorVo.getDataHash().get(tablename)).get(aggregationPreprocessorVo
                    .getUniqueKeyForOneRecord().toString());
            final Map tablefields = (Map) aggregationPreprocessorVo.getFieldTypeHash().get(tablename);
            HashMap fieldtypes = new HashMap();
            if (tablefields != null) {
                fieldtypes = (HashMap) tablefields.get("fieldtype");
            }
            final Set<Entry> fieldtypesEntrySet = fieldtypes.entrySet();
            for (final Entry entry : fieldtypesEntrySet) {
                final String fieldname = (String) entry.getKey();
                if (entry.getValue().equals(Constants.COUNT_CUMULATION_FIELD)
                    || entry.getValue().equals(Constants.DIFFERENCE_CUMULATION_FIELD)) {
                    BigInteger dataHashInteger = new BigInteger((String) record.get(fieldname));
                    final BigInteger oneRecordHashInteger =
                        new BigInteger((String) aggregationPreprocessorVo.getFieldHashForOneRecord().get(fieldname));
                    dataHashInteger = dataHashInteger.add(oneRecordHashInteger);
                    record.put(fieldname, dataHashInteger.toString());
                }
            }
        }
        else {
            ((Map) aggregationPreprocessorVo.getDataHash().get(tablename)).put(aggregationPreprocessorVo
                .getUniqueKeyForOneRecord().toString(), aggregationPreprocessorVo.getFieldHashForOneRecord());
        }
    }

    /**
     * persist Aggregation-data into database.
     *
     * @param aggregationPreprocessorVo Object that holds all values
     * @param aggregationDefinitionId   aggregationDefinitionId
     * @param date                      processing-date
     * @throws SqlDatabaseSystemException e
     */
    @Transactional(rollbackFor = { SystemException.class, RuntimeException.class })
    public void persistAggregation(
        final AggregationPreprocessorVo aggregationPreprocessorVo, final String aggregationDefinitionId, final Date date)
        throws SqlDatabaseSystemException {
        // dont process statistic-data for this date and
        // aggregation-definition
        // if statistic-data was processed successfully before.
        final Collection<PreprocessingLog> preprocessingLogs =
            preprocessingLogsDao.retrievePreprocessingLogs(aggregationDefinitionId, date, false);
        if (preprocessingLogs != null && !preprocessingLogs.isEmpty()) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            LOGGER.error("aggregation-definition " + aggregationDefinitionId
                + " already preprocessed successfully for date " + dateFormat.format(date));
            return;
        }
        final DatabaseSelectVo databaseSelectVo = new DatabaseSelectVo();
        if (aggregationPreprocessorVo != null && aggregationPreprocessorVo.getDataHash() != null
            && !aggregationPreprocessorVo.getDataHash().isEmpty()) {
            for (final Object o : aggregationPreprocessorVo.getDataHash().keySet()) {
                // Iterate dataHash, for each Aggregation-table
                final String tablename = (String) o;

                final Map tableRecords = (Map) aggregationPreprocessorVo.getDataHash().get(tablename);

                if (tableRecords != null && !tableRecords.isEmpty()) {
                    for (final Object o1 : tableRecords.values()) {
                        // for each record in dataHash->table,
                        // query database, if record already exists
                        final Map fields = (Map) o1;

                        // Build databaseSelectVo
                        final Collection<String> tablenames = new ArrayList<String>();
                        tablenames.add(tablename);
                        databaseSelectVo.setTableNames(tablenames);

                        databaseSelectVo.setSelectType(Constants.DATABASE_SELECT_TYPE_SELECT);

                        final SelectFieldVo selectFieldVo = new SelectFieldVo();
                        selectFieldVo.setFieldName("*");
                        final Collection<SelectFieldVo> selectFieldVos = new ArrayList<SelectFieldVo>();
                        selectFieldVos.add(selectFieldVo);
                        databaseSelectVo.setSelectFieldVos(selectFieldVos);

                        final RootWhereGroupVo rootWhereGroupVo = new RootWhereGroupVo();
                        if (fields == null || fields.isEmpty()) {
                            continue;
                        }

                        // Iterate fields in dataHash and fill WhereFieldVos
                        int i = 0;
                        for (final Object o2 : fields.keySet()) {
                            final String fieldname = (String) o2;
                            final Map fieldHash =
                                (Map) ((Map) aggregationPreprocessorVo.getFieldTypeHash().get(tablename))
                                    .get("fieldtype");
                            final Map dbHash =
                                (Map) ((Map) aggregationPreprocessorVo.getFieldTypeHash().get(tablename)).get("dbtype");
                            String fieldtype = null;
                            if (fieldHash != null) {
                                fieldtype = (String) fieldHash.get(fieldname);
                            }
                            if (fieldtype != null
                                && (fieldtype.equals(Constants.INFO_FIELD) || fieldtype
                                    .equals(Constants.TIME_REDUCTION_FIELD))) {
                                if (i == 0) {
                                    final RootWhereFieldVo rootWhereFieldVo = new RootWhereFieldVo();
                                    rootWhereFieldVo.setFieldName(fieldname);
                                    rootWhereFieldVo.setFieldType((String) dbHash.get(fieldname));
                                    rootWhereFieldVo.setFieldValue((String) fields.get(fieldname));
                                    rootWhereFieldVo.setOperator(Constants.DATABASE_OPERATOR_EQUALS);
                                    rootWhereGroupVo.setRootWhereFieldVo(rootWhereFieldVo);
                                }
                                else {
                                    final AdditionalWhereFieldVo additionalWhereFieldVo = new AdditionalWhereFieldVo();
                                    additionalWhereFieldVo.setAlliance(Constants.DATABASE_ALLIANCE_AND);
                                    additionalWhereFieldVo.setFieldName(fieldname);
                                    additionalWhereFieldVo.setFieldType((String) dbHash.get(fieldname));
                                    additionalWhereFieldVo.setFieldValue((String) fields.get(fieldname));
                                    additionalWhereFieldVo.setOperator(Constants.DATABASE_OPERATOR_EQUALS);
                                    if (rootWhereGroupVo.getAdditionalWhereFieldVos() == null) {
                                        rootWhereGroupVo
                                            .setAdditionalWhereFieldVos(new ArrayList<AdditionalWhereFieldVo>());
                                    }
                                    rootWhereGroupVo.getAdditionalWhereFieldVos().add(additionalWhereFieldVo);
                                }
                                i++;
                            }
                        }

                        // set where-clauses
                        databaseSelectVo.setRootWhereGroupVo(rootWhereGroupVo);

                        // Query Database
                        final List results = dbAccessor.executeSql(databaseSelectVo);

                        // If record doesnt exist yet, insert new Record
                        if (results == null || results.isEmpty()) {
                            // insert Record
                            insertRecord(tablename, fields, aggregationPreprocessorVo);

                            // if record already exists, take record and add
                            // values
                            // to count-cumulation-fields
                            // and difference-cumulation-fields
                        }
                        else {
                            // update Record
                            updateRecord(tablename, fields, results, aggregationPreprocessorVo);
                        }
                    }
                }
            }
        }
        final PreprocessingLog preprocessingLog = new PreprocessingLog();
        final AggregationDefinition aggregationDefinition = new AggregationDefinition();
        aggregationDefinition.setId(aggregationDefinitionId);
        preprocessingLog.setAggregationDefinition(aggregationDefinition);
        preprocessingLog.setHasError(false);
        preprocessingLog.setLogEntry("OK");
        preprocessingLog.setProcessingDate(new java.sql.Date(date.getTime()));
        preprocessingLogsDao.savePreprocessingLog(preprocessingLog);

    }

    /**
     * insert record into aggregation-table.
     *
     * @param tablename                 name of database table
     * @param fields                    database fields
     * @param aggregationPreprocessorVo Object that holds all values
     * @throws SqlDatabaseSystemException e
     */
    private void insertRecord(
        final String tablename, final Map fields, final AggregationPreprocessorVo aggregationPreprocessorVo)
        throws SqlDatabaseSystemException {
        final DatabaseRecordVo databaseRecordVo = new DatabaseRecordVo();
        databaseRecordVo.setTableName(tablename);
        final Collection<DatabaseRecordFieldVo> databaseRecordFieldVos = new ArrayList<DatabaseRecordFieldVo>();
        final Set<Entry> fieldsEntrySet = fields.entrySet();
        for (final Entry entry : fieldsEntrySet) {
            final String fieldname = (String) entry.getKey();
            final DatabaseRecordFieldVo databaseRecordFieldVo = new DatabaseRecordFieldVo();
            databaseRecordFieldVo.setFieldName(fieldname);
            databaseRecordFieldVo.setFieldValue((String) entry.getValue());
            databaseRecordFieldVo.setFieldType(getDbFieldType(tablename, fieldname, aggregationPreprocessorVo));
            databaseRecordFieldVos.add(databaseRecordFieldVo);
        }
        databaseRecordVo.setDatabaseRecordFieldVos(databaseRecordFieldVos);
        dbAccessor.createRecord(databaseRecordVo);
    }

    /**
     * updates record in aggregation-table. -Take list of results (should be one result only) and add values of this
     * processing to the count-cumulation-fields and difference-cumulation-fields -build databaseSelectVo -update record
     * in database
     *
     * @param tablename                 name of database table
     * @param fields                    database fields
     * @param results                   results from old db-record
     * @param aggregationPreprocessorVo Object that holds all values
     * @throws SqlDatabaseSystemException e
     */
    private void updateRecord(
        final String tablename, final Map fields, final Iterable results,
        final AggregationPreprocessorVo aggregationPreprocessorVo) throws SqlDatabaseSystemException {
        for (final Object result : results) {
            final Map fieldsMap = (Map) result;
            final DatabaseSelectVo databaseSelectVo = new DatabaseSelectVo();
            final Collection<String> tablenames = new ArrayList<String>();
            tablenames.add(tablename);
            databaseSelectVo.setTableNames(tablenames);

            databaseSelectVo.setSelectType(Constants.DATABASE_SELECT_TYPE_UPDATE);
            final RootWhereGroupVo rootWhereGroupVo = new RootWhereGroupVo();
            final Collection<SelectFieldVo> selectFieldVos = new ArrayList<SelectFieldVo>();

            final Set<Entry> fieldsEntrySet = fields.entrySet();
            try {
                int i = 0;
                for (final Entry entry : fieldsEntrySet) {
                    final String fieldname = (String) entry.getKey();
                    final Map fieldHash =
                        (Map) ((Map) aggregationPreprocessorVo.getFieldTypeHash().get(tablename)).get("fieldtype");
                    final Map dbHash =
                        (Map) ((Map) aggregationPreprocessorVo.getFieldTypeHash().get(tablename)).get("dbtype");
                    String fieldtype = null;
                    if (fieldHash != null) {
                        fieldtype = (String) fieldHash.get(fieldname);
                    }
                    if (fieldtype != null
                        && (fieldtype.equals(Constants.COUNT_CUMULATION_FIELD) || fieldtype
                            .equals(Constants.DIFFERENCE_CUMULATION_FIELD))) {
                        final SelectFieldVo selectFieldVo = new SelectFieldVo();
                        selectFieldVo.setFieldName(fieldname);
                        selectFieldVo.setFieldType(getDbFieldType(tablename, fieldname, aggregationPreprocessorVo));
                        BigInteger toAdd = new BigInteger((String) entry.getValue());
                        final BigInteger initial = new BigInteger(fieldsMap.get(fieldname).toString());
                        toAdd = toAdd.add(initial);
                        selectFieldVo.setFieldValue(toAdd.toString());
                        selectFieldVos.add(selectFieldVo);
                    }
                    else {
                        if (i == 0) {
                            final RootWhereFieldVo rootWhereFieldVo = new RootWhereFieldVo();
                            rootWhereFieldVo.setFieldName(fieldname);
                            rootWhereFieldVo.setFieldType((String) dbHash.get(fieldname));
                            rootWhereFieldVo.setFieldValue((String) entry.getValue());
                            rootWhereFieldVo.setOperator(Constants.DATABASE_OPERATOR_EQUALS);
                            rootWhereGroupVo.setRootWhereFieldVo(rootWhereFieldVo);
                        }
                        else {
                            final AdditionalWhereFieldVo additionalWhereFieldVo = new AdditionalWhereFieldVo();
                            additionalWhereFieldVo.setAlliance(Constants.DATABASE_ALLIANCE_AND);
                            additionalWhereFieldVo.setFieldName(fieldname);
                            additionalWhereFieldVo.setFieldType((String) dbHash.get(fieldname));
                            additionalWhereFieldVo.setFieldValue((String) entry.getValue());
                            additionalWhereFieldVo.setOperator(Constants.DATABASE_OPERATOR_EQUALS);
                            if (rootWhereGroupVo.getAdditionalWhereFieldVos() == null) {
                                rootWhereGroupVo.setAdditionalWhereFieldVos(new ArrayList<AdditionalWhereFieldVo>());
                            }
                            rootWhereGroupVo.getAdditionalWhereFieldVos().add(additionalWhereFieldVo);
                        }
                        i++;
                    }
                }
            }
            catch (final Exception e) {
                throw new SqlDatabaseSystemException(e);
            }
            databaseSelectVo.setRootWhereGroupVo(rootWhereGroupVo);
            databaseSelectVo.setSelectFieldVos(selectFieldVos);
            dbAccessor.updateRecord(databaseSelectVo);
        }
    }

    /**
     * get field type from aggregation-definition.
     *
     * @param tablename                 name of database table
     * @param fieldname                 name of database field
     * @param aggregationPreprocessorVo Object that holds all values
     * @return String fieldType (text or numeric)
     */
    private static String getDbFieldType(
        final String tablename, final String fieldname, final AggregationPreprocessorVo aggregationPreprocessorVo) {
        final Map tableFieldTypes = (Map) aggregationPreprocessorVo.getFieldTypeHash().get(tablename);
        if (tableFieldTypes != null) {
            final Map dbFieldTypes = (Map) tableFieldTypes.get("dbtype");
            if (dbFieldTypes != null) {
                return (String) dbFieldTypes.get(fieldname);
            }
            else {
                LOGGER.error(fieldname + " not found in fieldTypeHash");
            }
        }
        else {
            LOGGER.error(tablename + " not found in fieldTypeHash");
        }
        return null;
    }

    /**
     * reduce time to specified precision.
     *
     * @param cal      XMLGregorianCalendar-record-xml
     * @param reduceTo reduceTo
     * @return String reduced time
     */
    private static String reduceTime(final Calendar cal, final String reduceTo) {
        if (reduceTo.equals(Constants.TIME_REDUCTION_TYPE_YEAR)) {
            return Integer.valueOf(cal.get(Calendar.YEAR)).toString();
        }
        else if (reduceTo.equals(Constants.TIME_REDUCTION_TYPE_MONTH)) {
            return Integer.valueOf(cal.get(Calendar.MONTH) + 1).toString();
        }
        else if (reduceTo.equals(Constants.TIME_REDUCTION_TYPE_DAY)) {
            return Integer.valueOf(cal.get(Calendar.DAY_OF_MONTH)).toString();
        }
        else if (reduceTo.equals(Constants.TIME_REDUCTION_TYPE_WEEKDAY)) {
            return Integer.valueOf(cal.get(Calendar.DAY_OF_WEEK)).toString();
        }
        return "";
    }

}
