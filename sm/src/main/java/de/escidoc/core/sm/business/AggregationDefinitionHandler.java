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
package de.escidoc.core.sm.business;

import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.filter.DbRequestParameters;
import de.escidoc.core.common.business.filter.SRURequestParameters;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.AggregationDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.factory.ExplainXmlProvider;
import de.escidoc.core.sm.business.filter.AggregationDefinitionFilter;
import de.escidoc.core.sm.business.interfaces.AggregationDefinitionHandlerInterface;
import de.escidoc.core.sm.business.persistence.DirectDatabaseAccessorInterface;
import de.escidoc.core.sm.business.persistence.SmAggregationDefinitionsDaoInterface;
import de.escidoc.core.sm.business.persistence.SmScopesDaoInterface;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationDefinition;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationStatisticDataSelector;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationTable;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationTableField;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationTableIndexField;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationTableIndexe;
import de.escidoc.core.sm.business.persistence.hibernate.Scope;
import de.escidoc.core.sm.business.renderer.interfaces.AggregationDefinitionRendererInterface;
import de.escidoc.core.sm.business.stax.handler.AggregationDefinitionStaxHandler;
import de.escidoc.core.sm.business.util.comparator.AggregationTableFieldComparator;
import de.escidoc.core.sm.business.util.comparator.AggregationTableIndexFieldComparator;
import de.escidoc.core.sm.business.vo.database.select.DatabaseSelectVo;
import de.escidoc.core.sm.business.vo.database.select.RootWhereFieldVo;
import de.escidoc.core.sm.business.vo.database.select.RootWhereGroupVo;
import de.escidoc.core.sm.business.vo.database.table.DatabaseIndexVo;
import de.escidoc.core.sm.business.vo.database.table.DatabaseTableFieldVo;
import de.escidoc.core.sm.business.vo.database.table.DatabaseTableVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

/**
 * A statistic AggregationDefinition resource handler.
 *
 * @author Michael Hoppe
 */
@Service("business.AggregationDefinitionHandler")
@org.springframework.context.annotation.Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class AggregationDefinitionHandler implements AggregationDefinitionHandlerInterface {

    @Autowired
    @Qualifier("persistence.SmAggregationDefinitionsDao")
    private SmAggregationDefinitionsDaoInterface dao;

    @Autowired
    @Qualifier("persistence.SmScopesDao")
    private SmScopesDaoInterface scopesDao;

    @Autowired
    @Qualifier("sm.persistence.DirectDatabaseAccessor")
    private DirectDatabaseAccessorInterface dbAccessor;

    @Autowired
    @Qualifier("business.sm.FilterUtility")
    private SmFilterUtility filterUtility;

    @Autowired
    @Qualifier("eSciDoc.core.aa.business.renderer.VelocityXmlAggregationDefinitionRenderer")
    private AggregationDefinitionRendererInterface renderer;

    /**
     * See Interface for functional description.
     *
     * @param xmlData aggregationDefinition as xml in aggregationDefinition schema.
     * @return Returns the XML representation of the resource.
     * @throws MissingMethodParameterException
     *                                ex
     * @throws ScopeNotFoundException ex
     * @throws SystemException        ex
     * @see de.escidoc.core.sm.business.interfaces .AggregationDefinitionHandlerInterface#create(java.lang.String)
     */
    @Override
    @Transactional(rollbackFor = { SystemException.class, RuntimeException.class })
    public String create(final String xmlData) throws MissingMethodParameterException, ScopeNotFoundException,
        SystemException {
        if (xmlData == null || xmlData.length() == 0) {
            throw new MissingMethodParameterException("xml may not be null");
        }

        // parse
        final StaxParser sp = new StaxParser();
        final AggregationDefinitionStaxHandler handler = new AggregationDefinitionStaxHandler(sp);
        sp.addHandler(handler);
        try {
            sp.parse(xmlData);
        }
        catch (final Exception e) {
            throw new SystemException(e);
        }

        final String scopeId = handler.getAggregationDefinition().getScope().getId();
        final Scope scope = scopesDao.retrieve(scopeId);

        // get AggregationDefinitionObject to insert aggregation-definition
        // into database
        final Utility utility = new Utility();
        final AggregationDefinition aggregationDefinition = handler.getAggregationDefinition();
        aggregationDefinition.setCreatorId(utility.getCurrentUserId());
        aggregationDefinition.setCreationDate(new Timestamp(System.currentTimeMillis()));
        aggregationDefinition.setScope(scope);

        dao.save(aggregationDefinition);
        handler.setAggregationDefinition(aggregationDefinition);

        // AggregationStatisticDataSelectors
        for (final AggregationStatisticDataSelector selector : handler.getAggregationStatisticDataSelectors()) {
            dao.save(selector);
        }
        aggregationDefinition.setAggregationStatisticDataSelectors(handler.getAggregationStatisticDataSelectors());

        // AggregationTables
        for (final AggregationTable aggregationTable : handler.getAggregationTables()) {
            dao.save(aggregationTable);
        }
        aggregationDefinition.setAggregationTables(handler.getAggregationTables());

        // Get databaseTableVos for all Aggregation-Tables
        // defined in Aggregation Definition
        final Collection<DatabaseTableVo> databaseTableVos = generateAggregationDatabaseTableVos(aggregationDefinition);
        if (databaseTableVos != null) {
            for (final DatabaseTableVo databaseTableVo : databaseTableVos) {
                // create aggregation table in Database
                dbAccessor.createTable(databaseTableVo);
            }
        }

        return renderer.render(aggregationDefinition);
    }

    /**
     * See Interface for functional description.
     *
     * @param id resource id.
     * @throws AggregationDefinitionNotFoundException
     *          e.
     * @throws MissingMethodParameterException
     *          e.
     * @see de.escidoc.core.sm.business.interfaces .AggregationDefinitionHandlerInterface #delete(java.lang.String)
     */
    @Override
    @Transactional(rollbackFor = { SystemException.class, RuntimeException.class })
    public void delete(final String id) throws AggregationDefinitionNotFoundException, MissingMethodParameterException,
        SqlDatabaseSystemException {
        if (id == null) {
            throw new MissingMethodParameterException("id may not be null");
        }
        final AggregationDefinition aggregationDefinition;
        try {
            // get aggregation definition to get aggregation table infos
            aggregationDefinition = dao.retrieve(id);
        }
        catch (final Exception e) {
            throw new AggregationDefinitionNotFoundException("AggregationDefinition with id " + id + " not found", e);
        }

        // Get databaseTableVos for all Aggregation-Tables
        // defined in Aggregation Definition
        final Collection<DatabaseTableVo> databaseTableVos = generateAggregationDatabaseTableVos(aggregationDefinition);

        if (databaseTableVos != null) {
            for (final DatabaseTableVo databaseTableVo : databaseTableVos) {
                // drop aggregation table in Database
                dbAccessor.dropTable(databaseTableVo);
            }
        }
        final DatabaseSelectVo databaseSelectVo = generateAggregationDatabaseRecordVoForDeletion(id);
        dbAccessor.deleteRecord(databaseSelectVo);
        // /////////////////////////////////////////////////////////////////
    }

    /**
     * See Interface for functional description.
     *
     * @param id resource id.
     * @return Returns the XML representation of the resource.
     * @throws AggregationDefinitionNotFoundException
     *                         e.
     * @throws MissingMethodParameterException
     *                         e.
     * @throws SystemException e.
     * @see de.escidoc.core.sm.business.interfaces .AggregationDefinitionHandlerInterface#retrieve(java.lang.String)
     */
    @Override
    public String retrieve(final String id) throws AggregationDefinitionNotFoundException,
        MissingMethodParameterException, SystemException {
        if (id == null) {
            throw new MissingMethodParameterException("id may not be null");
        }
        try {
            final AggregationDefinition aggregationDefinition = dao.retrieve(id);
            return renderer.render(aggregationDefinition);
        }
        catch (final NumberFormatException e) {
            throw new AggregationDefinitionNotFoundException("AggregationDefinition with id " + id + " not found", e);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param parameters filter as CQL query
     * @return Returns the XML representation of the resource-list.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SystemException             e.
     * @see de.escidoc.core.sm.business.interfaces .AggregationDefinitionHandlerInterface
     *      #retrieveAggregationDefinitions(java.util.Map)
     */
    @Override
    public String retrieveAggregationDefinitions(final Map<String, String[]> parameters)
        throws InvalidSearchQueryException, SystemException {
        final String result;
        final SRURequestParameters params = new DbRequestParameters(parameters);
        final String query = params.getQuery();
        final int limit = params.getMaximumRecords();
        final int offset = params.getStartRecord();

        if (params.isExplain()) {
            final Map<String, Object> values = new HashMap<String, Object>();

            values.put("PROPERTY_NAMES", new AggregationDefinitionFilter(null).getPropertyNames());
            result = ExplainXmlProvider.getInstance().getExplainAggregationDefinitionXml(values);
        }
        else if (limit == 0) {
            result =
                renderer.renderAggregationDefinitions(new ArrayList<AggregationDefinition>(0), params
                    .getRecordPacking());
        }
        else {
            // get all scope ids from database
            final List<String> scopeIds = scopesDao.retrieveScopeIds();

            Collection<String> filteredScopeIds = null;

            if (scopeIds != null && !scopeIds.isEmpty()) {
                // get scope-ids filtered by user-privileges
                filteredScopeIds = filterUtility.filterRetrievePrivilege(Constants.SCOPE_OBJECT_TYPE, scopeIds);
            }
            Collection<AggregationDefinition> aggregationDefinitions = null;
            if (filteredScopeIds != null && !filteredScopeIds.isEmpty()) {
                // get aggregation-definitions as XML
                aggregationDefinitions = dao.retrieveAggregationDefinitions(filteredScopeIds, query, offset, limit);
            }

            result = renderer.renderAggregationDefinitions(aggregationDefinitions, params.getRecordPacking());
        }
        return result;
    }

    /**
     * Generates DatabaseTableVos for DirectDatabaseAccessor to create Aggregation-Tables as defined in
     * aggregation-definition.
     *
     * @param aggregationDefinition aggregationDefinition binding object.
     * @return Collection Returns Collection with DatabaseTableVo.
     * @throws SqlDatabaseSystemException e
     */
    private Collection<DatabaseTableVo> generateAggregationDatabaseTableVos(
        final AggregationDefinition aggregationDefinition) throws SqlDatabaseSystemException {
        final Collection<DatabaseTableVo> databaseTableVos = new ArrayList<DatabaseTableVo>();
        for (final AggregationTable aggregationTable : aggregationDefinition.getAggregationTables()) {
            final DatabaseTableVo databaseTableVo = new DatabaseTableVo();
            databaseTableVo.setTableName(aggregationTable.getName().toLowerCase(Locale.ENGLISH));

            // Generate Fields
            final Collection<DatabaseTableFieldVo> databaseFieldVos = new ArrayList<DatabaseTableFieldVo>();
            // sort AggregationTableFields
            final Collection<AggregationTableField> sortedAggregationTableFields =
                new TreeSet<AggregationTableField>(new AggregationTableFieldComparator());
            sortedAggregationTableFields.addAll(aggregationTable.getAggregationTableFields());

            for (final AggregationTableField field : sortedAggregationTableFields) {
                final DatabaseTableFieldVo databaseTableFieldVo = new DatabaseTableFieldVo();
                if (field.getFieldTypeId() == Constants.COUNT_CUMULATION_FIELD_ID
                    || field.getFieldTypeId() == Constants.DIFFERENCE_CUMULATION_FIELD_ID
                    || field.getFieldTypeId() == Constants.TIME_REDUCTION_FIELD_ID) {
                    dbAccessor.checkReservedExpressions(field.getName());
                    databaseTableFieldVo.setFieldName(field.getName().toLowerCase(Locale.ENGLISH));
                    databaseTableFieldVo.setFieldType(Constants.DATABASE_FIELD_TYPE_NUMERIC);
                }
                else if (field.getFieldTypeId() == Constants.INFO_FIELD_ID) {
                    dbAccessor.checkReservedExpressions(field.getName());
                    databaseTableFieldVo.setFieldName(field.getName().toLowerCase(Locale.ENGLISH));
                    databaseTableFieldVo.setFieldType(field.getDataType());
                }
                else {
                    throw new SqlDatabaseSystemException("Table-Fields may not be empty in aggregation definition");
                }
                databaseFieldVos.add(databaseTableFieldVo);
            }
            databaseTableVo.setDatabaseFieldVos(databaseFieldVos);

            // Generate Indexes
            if (aggregationTable.getAggregationTableIndexes() != null
                && !aggregationTable.getAggregationTableIndexes().isEmpty()) {
                final Collection<DatabaseIndexVo> databaseIndexVos = new ArrayList<DatabaseIndexVo>();
                for (final AggregationTableIndexe index : aggregationTable.getAggregationTableIndexes()) {
                    final DatabaseIndexVo databaseIndexVo = new DatabaseIndexVo();
                    databaseIndexVo.setIndexName(index.getName().toLowerCase(Locale.ENGLISH));
                    final Collection<String> indexFields = new ArrayList<String>();
                    if (index.getAggregationTableIndexFields() != null) {
                        // sort AggregationTableIndexFields
                        final Collection<AggregationTableIndexField> sortedAggregationTableIndexFields =
                            new TreeSet<AggregationTableIndexField>(new AggregationTableIndexFieldComparator());
                        sortedAggregationTableIndexFields.addAll(index.getAggregationTableIndexFields());
                        for (final AggregationTableIndexField indexField : sortedAggregationTableIndexFields) {
                            indexFields.add(indexField.getField());
                        }
                    }
                    databaseIndexVo.setFields(indexFields);
                    databaseIndexVos.add(databaseIndexVo);
                }
                databaseTableVo.setDatabaseIndexVos(databaseIndexVos);
            }
            databaseTableVos.add(databaseTableVo);
        }
        return databaseTableVos;
    }

    /**
     * Generates DatabaseSelectVo for DirectDatabaseAccessor to delete a record in table Aggregation_Definitions.
     *
     * @param primKey primKey for table aggregation_definitions.
     * @return DatabaseSelectVo Returns DatabaseSelectVo with recordInfo.
     * @throws SqlDatabaseSystemException e
     */
    private static DatabaseSelectVo generateAggregationDatabaseRecordVoForDeletion(final String primKey)
        throws SqlDatabaseSystemException {
        final DatabaseSelectVo databaseSelectVo = new DatabaseSelectVo();
        databaseSelectVo.setSelectType(Constants.DATABASE_SELECT_TYPE_DELETE);
        final Collection<String> tablenames = new ArrayList<String>();
        tablenames.add(Constants.SM_SCHEMA_NAME + '.' + Constants.AGGREGATION_DEFINITIONS_TABLE_NAME);
        databaseSelectVo.setTableNames(tablenames);
        final RootWhereGroupVo rootWhereGroupVo = new RootWhereGroupVo();
        final RootWhereFieldVo rootWhereFieldVo = new RootWhereFieldVo();
        rootWhereFieldVo.setFieldName("id");
        rootWhereFieldVo.setFieldType(Constants.DATABASE_FIELD_TYPE_TEXT);
        rootWhereFieldVo.setOperator(Constants.DATABASE_OPERATOR_EQUALS);
        rootWhereFieldVo.setFieldValue(primKey);
        rootWhereGroupVo.setRootWhereFieldVo(rootWhereFieldVo);
        databaseSelectVo.setRootWhereGroupVo(rootWhereGroupVo);
        return databaseSelectVo;
    }

}
