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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.filter.DbRequestParameters;
import de.escidoc.core.common.business.filter.SRURequestParameters;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.AggregationDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
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

/**
 * A statistic AggregationDefinition resource handler.
 * 
 * @spring.bean id="business.AggregationDefinitionHandler" scope="prototype"
 * @author MIH
 * @sm
 */
public class AggregationDefinitionHandler
    implements AggregationDefinitionHandlerInterface {

    private static final AppLogger log = new AppLogger(
        AggregationDefinitionHandler.class.getName());

    private SmAggregationDefinitionsDaoInterface dao;

    private SmScopesDaoInterface scopesDao;

    private DirectDatabaseAccessorInterface dbAccessor;

    private SmFilterUtility filterUtility;

    private AggregationDefinitionRendererInterface renderer;

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.interfaces
     *      .AggregationDefinitionHandlerInterface#create(java.lang.String)
     * 
     * @param xmlData
     *            aggregationDefinition as xml in aggregationDefinition schema.
     * @return Returns the XML representation of the resource.
     * 
     * @throws MissingMethodParameterException
     *             ex
     * @throws ScopeNotFoundException
     *             ex
     * @throws SystemException
     *             ex
     * @tx
     * @sm
     */
    public String create(final String xmlData)
        throws MissingMethodParameterException, ScopeNotFoundException,
        SystemException {
        if (log.isDebugEnabled()) {
            log.debug("AggregationDefinitionHandler does create");
        }
        if (xmlData == null || xmlData.length() == 0) {
            log.error("xml may not be null");
            throw new MissingMethodParameterException("xml may not be null");
        }

        String scopeId = null;
        AggregationDefinition aggregationDefinition = null;

        // parse
        StaxParser sp = new StaxParser();
        AggregationDefinitionStaxHandler handler =
            new AggregationDefinitionStaxHandler(sp);
        sp.addHandler(handler);
        try {
            sp.parse(xmlData);
        }
        catch (Exception e) {
            log.error(e);
            throw new SystemException(e);
        }

        scopeId = handler.getAggregationDefinition().getScope().getId();
        Scope scope = scopesDao.retrieve(scopeId);

        // get AggregationDefinitionObject to insert aggregation-definition
        // into database
        Utility utility = new Utility();
        aggregationDefinition = handler.getAggregationDefinition();
        aggregationDefinition.setCreatorId(utility.getCurrentUserId());
        aggregationDefinition.setCreationDate(new Timestamp(System
            .currentTimeMillis()));
        aggregationDefinition.setScope(scope);

        dao.save(aggregationDefinition);
        handler.setAggregationDefinition(aggregationDefinition);

        // AggregationStatisticDataSelectors
        for (AggregationStatisticDataSelector selector : handler
            .getAggregationStatisticDataSelectors()) {
            dao.save(selector);
        }
        aggregationDefinition.setAggregationStatisticDataSelectors(handler
            .getAggregationStatisticDataSelectors());

        // AggregationTables
        for (AggregationTable aggregationTable : handler.getAggregationTables()) {
            dao.save(aggregationTable);
        }
        aggregationDefinition.setAggregationTables(handler
            .getAggregationTables());

        // Get databaseTableVos for all Aggregation-Tables
        // defined in Aggregation Definition
        Collection<DatabaseTableVo> databaseTableVos =
            generateAggregationDatabaseTableVos(aggregationDefinition);
        if (databaseTableVos != null) {
            for (DatabaseTableVo databaseTableVo : databaseTableVos) {
                // create aggregation table in Database
                dbAccessor.createTable(databaseTableVo);
            }
        }

        return renderer.render(aggregationDefinition);
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.interfaces
     *      .AggregationDefinitionHandlerInterface #delete(java.lang.String)
     * 
     * @param id
     *            resource id.
     * 
     * @throws AggregationDefinitionNotFoundException
     *             e.
     * @throws MissingMethodParameterException
     *             e.
     * @throws SystemException
     *             e.
     * @tx
     * @sm
     */
    public void delete(final String id)
        throws AggregationDefinitionNotFoundException,
        MissingMethodParameterException, SystemException {
        if (log.isDebugEnabled()) {
            log.debug("AggregationDefinitionHandler does delete");
        }
        if (id == null) {
            log.error("id may not be null");
            throw new MissingMethodParameterException("id may not be null");
        }
        AggregationDefinition aggregationDefinition = null;
        try {
            // get aggregation definition to get aggregation table infos
            aggregationDefinition = dao.retrieve(id);
        }
        catch (Exception e) {
            log.error("AggregationDefinition with id " + id + " not found");
            throw new AggregationDefinitionNotFoundException(
                "AggregationDefinition with id " + id + " not found", e);
        }

        // Get databaseTableVos for all Aggregation-Tables
        // defined in Aggregation Definition
        Collection<DatabaseTableVo> databaseTableVos =
            generateAggregationDatabaseTableVos(aggregationDefinition);

        if (databaseTableVos != null) {
            for (DatabaseTableVo databaseTableVo : databaseTableVos) {
                // drop aggregation table in Database
                dbAccessor.dropTable(databaseTableVo);
            }
        }
        DatabaseSelectVo databaseSelectVo =
            generateAggregationDatabaseRecordVoForDeletion(id);
        dbAccessor.deleteRecord(databaseSelectVo);
        // /////////////////////////////////////////////////////////////////
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.interfaces
     *      .AggregationDefinitionHandlerInterface#retrieve(java.lang.String)
     * 
     * @param id
     *            resource id.
     * @return Returns the XML representation of the resource.
     * 
     * @throws AggregationDefinitionNotFoundException
     *             e.
     * @throws MissingMethodParameterException
     *             e.
     * @throws SystemException
     *             e.
     * 
     * @sm
     */
    public String retrieve(final String id)
        throws AggregationDefinitionNotFoundException,
        MissingMethodParameterException, SystemException {
        if (log.isDebugEnabled()) {
            log.debug("AggregationDefinitionHandler does retrieve");
        }
        if (id == null) {
            log.error("id may not be null");
            throw new MissingMethodParameterException("id may not be null");
        }
        try {
            AggregationDefinition aggregationDefinition = dao.retrieve(id);
            return renderer.render(aggregationDefinition);
        }
        catch (NumberFormatException e) {
            log.error("AggregationDefinition with id " + id + " not found");
            throw new AggregationDefinitionNotFoundException(
                "AggregationDefinition with id " + id + " not found", e);
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.interfaces
     *      .AggregationDefinitionHandlerInterface
     *      #retrieveAggregationDefinitions(java.util.Map)
     * 
     * @param parameters
     *            filter as CQL query
     * 
     * @return Returns the XML representation of the resource-list.
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws SystemException
     *             e.
     */
    public String retrieveAggregationDefinitions(
        final Map<String, String[]> parameters)
        throws InvalidSearchQueryException, SystemException {
        String result = null;
        SRURequestParameters params =
            new DbRequestParameters(parameters);
        String query = params.getQuery();
        int limit = params.getLimit();
        int offset = params.getOffset();

        if (params.isExplain()) {
            Map<String, Object> values = new HashMap<String, Object>();

            values.put("PROPERTY_NAMES",
                new AggregationDefinitionFilter(null).getPropertyNames());
            result =
                ExplainXmlProvider
                    .getInstance().getExplainAggregationDefinitionXml(values);
        }
        else {
            // get all scope ids from database
            Collection<String> scopeIds = scopesDao.retrieveScopeIds();

            Collection<String> filteredScopeIds = null;
            Collection<AggregationDefinition> aggregationDefinitions = null;

            if (scopeIds != null && !scopeIds.isEmpty()) {
                // get scope-ids filtered by user-privileges
                filteredScopeIds =
                    filterUtility.filterRetrievePrivilege(
                        Constants.SCOPE_OBJECT_TYPE, scopeIds);
            }

            // int numberOfRecords = 0;

            if (filteredScopeIds != null && !filteredScopeIds.isEmpty()) {
                // get aggregation-definitions as XML
                aggregationDefinitions =
                    dao.retrieveAggregationDefinitions(filteredScopeIds, query,
                        offset, limit);
                if (aggregationDefinitions != null) {
                    // numberOfRecords = aggregationDefinitions.size();
                }
            }

            result =
                renderer.renderAggregationDefinitions(aggregationDefinitions,
                    params.getRecordPacking());
        }
        return result;
    }

    /**
     * Generates DatabaseTableVos for DirectDatabaseAccessor to create
     * Aggregation-Tables as defined in aggregation-definition.
     * 
     * @param aggregationDefinition
     *            aggregationDefinition binding object.
     * @return Collection Returns Collection with DatabaseTableVo.
     * @throws SqlDatabaseSystemException
     *             e
     * 
     * @sm
     */
    private Collection<DatabaseTableVo> generateAggregationDatabaseTableVos(
        final AggregationDefinition aggregationDefinition)
        throws SqlDatabaseSystemException {
        Collection<DatabaseTableVo> databaseTableVos =
            new ArrayList<DatabaseTableVo>();
        for (AggregationTable aggregationTable : aggregationDefinition
            .getAggregationTables()) {
            DatabaseTableVo databaseTableVo = new DatabaseTableVo();
            databaseTableVo.setTableName(aggregationTable
                .getName().toLowerCase());

            // Generate Fields
            Collection<DatabaseTableFieldVo> databaseFieldVos =
                new ArrayList<DatabaseTableFieldVo>();
            // sort AggregationTableFields
            TreeSet<AggregationTableField> sortedAggregationTableFields =
                new TreeSet<AggregationTableField>(
                    new AggregationTableFieldComparator());
            sortedAggregationTableFields
                .addAll(aggregationTable
                    .getAggregationTableFields());

            for (AggregationTableField field : sortedAggregationTableFields) {
                DatabaseTableFieldVo databaseTableFieldVo =
                    new DatabaseTableFieldVo();
                if (field.getFieldTypeId() == Constants.COUNT_CUMULATION_FIELD_ID) {
                    dbAccessor.checkReservedExpressions(field.getName());
                    databaseTableFieldVo.setFieldName(field
                        .getName().toLowerCase());
                    databaseTableFieldVo
                        .setFieldType(Constants.DATABASE_FIELD_TYPE_NUMERIC);
                }
                else if (field.getFieldTypeId() == Constants.DIFFERENCE_CUMULATION_FIELD_ID) {
                    dbAccessor.checkReservedExpressions(field.getName());
                    databaseTableFieldVo.setFieldName(field
                        .getName().toLowerCase());
                    databaseTableFieldVo
                        .setFieldType(Constants.DATABASE_FIELD_TYPE_NUMERIC);
                }
                else if (field.getFieldTypeId() == Constants.INFO_FIELD_ID) {
                    dbAccessor.checkReservedExpressions(field.getName());
                    databaseTableFieldVo.setFieldName(field
                        .getName().toLowerCase());
                    databaseTableFieldVo.setFieldType(field.getDataType());
                }
                else if (field.getFieldTypeId() == Constants.TIME_REDUCTION_FIELD_ID) {
                    dbAccessor.checkReservedExpressions(field.getName());
                    databaseTableFieldVo.setFieldName(field
                        .getName().toLowerCase());
                    databaseTableFieldVo
                        .setFieldType(Constants.DATABASE_FIELD_TYPE_NUMERIC);
                }
                else {
                    log
                        .error("Table-Fields may not be empty in aggregation definition");
                    throw new SqlDatabaseSystemException(
                        "Table-Fields may not be empty in aggregation definition");
                }
                databaseFieldVos.add(databaseTableFieldVo);
            }
            databaseTableVo.setDatabaseFieldVos(databaseFieldVos);

            // Generate Indexes
            if (aggregationTable.getAggregationTableIndexes() != null
                && !aggregationTable.getAggregationTableIndexes().isEmpty()) {
                Collection<DatabaseIndexVo> databaseIndexVos =
                    new ArrayList<DatabaseIndexVo>();
                for (AggregationTableIndexe index : aggregationTable
                    .getAggregationTableIndexes()) {
                    DatabaseIndexVo databaseIndexVo = new DatabaseIndexVo();
                    databaseIndexVo.setIndexName(index.getName().toLowerCase());
                    Collection<String> indexFields = new ArrayList<String>();
                    if (index.getAggregationTableIndexFields() != null) {
                        // sort AggregationTableIndexFields
                        TreeSet<AggregationTableIndexField> sortedAggregationTableIndexFields =
                            new TreeSet<AggregationTableIndexField>(
                                new AggregationTableIndexFieldComparator());
                        sortedAggregationTableIndexFields
                            .addAll(index
                                .getAggregationTableIndexFields());
                        for (AggregationTableIndexField indexField : sortedAggregationTableIndexFields) {
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
     * Generates DatabaseSelectVo for DirectDatabaseAccessor to delete a record
     * in table Aggregation_Definitions.
     * 
     * @param primKey
     *            primKey for table aggregation_definitions.
     * @return DatabaseSelectVo Returns DatabaseSelectVo with recordInfo.
     * @throws SqlDatabaseSystemException
     *             e
     * 
     * @sm
     */
    private static DatabaseSelectVo generateAggregationDatabaseRecordVoForDeletion(
            final String primKey) throws SqlDatabaseSystemException {
        DatabaseSelectVo databaseSelectVo = new DatabaseSelectVo();
        databaseSelectVo.setSelectType(Constants.DATABASE_SELECT_TYPE_DELETE);
        Collection<String> tablenames = new ArrayList<String>();
        tablenames.add(Constants.SM_SCHEMA_NAME + '.'
            + Constants.AGGREGATION_DEFINITIONS_TABLE_NAME);
        databaseSelectVo.setTableNames(tablenames);

        RootWhereGroupVo rootWhereGroupVo = new RootWhereGroupVo();
        RootWhereFieldVo rootWhereFieldVo = new RootWhereFieldVo();

        rootWhereFieldVo.setFieldName("id");
        rootWhereFieldVo.setFieldType(Constants.DATABASE_FIELD_TYPE_TEXT);
        rootWhereFieldVo.setOperator(Constants.DATABASE_OPERATOR_EQUALS);
        rootWhereFieldVo.setFieldValue(primKey);

        rootWhereGroupVo.setRootWhereFieldVo(rootWhereFieldVo);

        databaseSelectVo.setRootWhereGroupVo(rootWhereGroupVo);
        return databaseSelectVo;
    }

    /**
     * Setter for the dao.
     * 
     * @spring.property ref="persistence.SmAggregationDefinitionsDao"
     * @param dao
     *            The data access object.
     * 
     * @sm
     */
    public void setDao(final SmAggregationDefinitionsDaoInterface dao) {
        this.dao = dao;
    }

    /**
     * Setter for the scopesDao.
     * 
     * @spring.property ref="persistence.SmScopesDao"
     * @param scopesDao
     *            The data access object.
     * 
     * @sm
     */
    public void setScopesDao(final SmScopesDaoInterface scopesDao) {
        this.scopesDao = scopesDao;
    }

    /**
     * Setting the directDatabaseAccessor.
     * 
     * @param dbAccessorIn
     *            The directDatabaseAccessor to set.
     * @spring.property ref="sm.persistence.DirectDatabaseAccessor"
     */
    public final void setDirectDatabaseAccessor(
        final DirectDatabaseAccessorInterface dbAccessorIn) {
        this.dbAccessor = dbAccessorIn;
    }

    /**
     * Setting the filterUtility.
     * 
     * @param filterUtility
     *            The filterUtility to set.
     * @spring.property ref="business.sm.FilterUtility"
     */
    public final void setFilterUtility(final SmFilterUtility filterUtility) {
        this.filterUtility = filterUtility;
    }

    /**
     * Injects the renderer.
     * 
     * @param renderer
     *            The renderer to inject.
     * 
     * @spring.property ref=
     *                  "eSciDoc.core.aa.business.renderer.VelocityXmlAggregationDefinitionRenderer"
     * @aa
     */
    public void setRenderer(
        final AggregationDefinitionRendererInterface renderer) {
        this.renderer = renderer;
    }

}
