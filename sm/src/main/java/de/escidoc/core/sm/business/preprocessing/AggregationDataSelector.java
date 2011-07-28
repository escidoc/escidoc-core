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

import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.StatisticPreprocessingSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.sm.business.Constants;
import de.escidoc.core.sm.business.persistence.DirectDatabaseAccessorInterface;
import de.escidoc.core.sm.business.persistence.SmScopesDaoInterface;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationDefinition;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationStatisticDataSelector;
import de.escidoc.core.sm.business.persistence.hibernate.Scope;
import de.escidoc.core.sm.business.vo.database.select.AdditionalWhereFieldVo;
import de.escidoc.core.sm.business.vo.database.select.DatabaseSelectVo;
import de.escidoc.core.sm.business.vo.database.select.RootWhereFieldVo;
import de.escidoc.core.sm.business.vo.database.select.RootWhereGroupVo;
import de.escidoc.core.sm.business.vo.database.select.SelectFieldVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Extracts data for Aggregation Definition out of raw-statistic-data-table.
 *
 * @author Michael Hoppe
 */
@Service("business.AggregationDataSelector")
@org.springframework.context.annotation.Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Transactional(rollbackFor = { SystemException.class, RuntimeException.class })
public class AggregationDataSelector {

    @Autowired
    @Qualifier("persistence.SmScopesDao")
    private DirectDatabaseAccessorInterface dbAccessor;

    @Autowired
    @Qualifier("sm.persistence.DirectDatabaseAccessor")
    private SmScopesDaoInterface scopesDao;

    /**
     * Private constructor to prevent initialization.
     */
    protected AggregationDataSelector() {
    }

    public List getDataForAggregation(final AggregationDefinition aggregationDefinition, final Date date)
        throws ScopeNotFoundException, SqlDatabaseSystemException, StatisticPreprocessingSystemException {
        if (aggregationDefinition.getAggregationStatisticDataSelectors() != null) {
            AggregationStatisticDataSelector selector = null;
            for (final AggregationStatisticDataSelector aggregationStatisticDataSelector : aggregationDefinition
                .getAggregationStatisticDataSelectors()) {
                if ("statistic-table".equals(aggregationStatisticDataSelector.getSelectorType())) {
                    selector = aggregationStatisticDataSelector;
                }
            }
            return selector != null ? dbAccessor.executeSql(generateStatisticTableSelectVo(selector,
                aggregationDefinition.getScope().getId(), date)) : null;
        }
        else {
            return null;
        }

    }

    /**
     * Generates DatabaseSelectVo for DirectDatabaseAccessor. Selects statistic-records from statistic-table, matching
     * scopeId, date and xpath.
     *
     * @param aggregationStatisticDataSelector
     * @param scopeId scopeId
     * @param date    date
     * @return DatabaseSelectVo Returns DatabaseSelectVo with recordInfo.
     * @throws SqlDatabaseSystemException e
     * @throws StatisticPreprocessingSystemException
     *                                    e
     * @throws ScopeNotFoundException     e
     */
    private DatabaseSelectVo generateStatisticTableSelectVo(
        final AggregationStatisticDataSelector aggregationStatisticDataSelector, final String scopeId, final Date date)
        throws StatisticPreprocessingSystemException, ScopeNotFoundException, SqlDatabaseSystemException {
        String xpath = null;
        if (aggregationStatisticDataSelector.getSelectorType() != null
            && "statistic-table".equals(aggregationStatisticDataSelector.getSelectorType())
            && aggregationStatisticDataSelector.getXpath() != null) {
            xpath = aggregationStatisticDataSelector.getXpath();
        }
        final DatabaseSelectVo databaseSelectVo = new DatabaseSelectVo();
        databaseSelectVo.setSelectType(Constants.DATABASE_SELECT_TYPE_SELECT);
        final Collection<String> tablenames = new ArrayList<String>();
        tablenames.add(Constants.STATISTIC_DATA_TABLE_NAME);
        databaseSelectVo.setTableNames(tablenames);

        final Collection<SelectFieldVo> selectFieldVos = new ArrayList<SelectFieldVo>();
        final SelectFieldVo selectFieldVo = new SelectFieldVo();
        selectFieldVo.setFieldName(Constants.STATISTIC_DATA_XML_FIELD_NAME);
        selectFieldVos.add(selectFieldVo);
        final SelectFieldVo selectFieldVo1 = new SelectFieldVo();
        selectFieldVo1.setFieldName(Constants.STATISTIC_DATA_TIMESTAMP_FIELD_NAME);
        selectFieldVos.add(selectFieldVo1);
        databaseSelectVo.setSelectFieldVos(selectFieldVos);

        final RootWhereGroupVo rootWhereGroupVo = new RootWhereGroupVo();
        final RootWhereFieldVo rootWhereFieldVo = new RootWhereFieldVo();
        final Collection<AdditionalWhereFieldVo> additionalWhereFieldVos = new ArrayList<AdditionalWhereFieldVo>();

        rootWhereFieldVo.setFieldName(Constants.STATISTIC_DATA_TIMESTAMP_FIELD_NAME);
        rootWhereFieldVo.setFieldType(Constants.DATABASE_FIELD_TYPE_DAYDATE);
        rootWhereFieldVo.setOperator(Constants.DATABASE_OPERATOR_EQUALS);
        rootWhereFieldVo.setFieldValue(new SimpleDateFormat("yyyy-MM-dd").format(date));
        rootWhereGroupVo.setRootWhereFieldVo(rootWhereFieldVo);

        //get Scope to decide if scope-type is admin
        final Scope scope = scopesDao.retrieve(scopeId);

        //only restrict to scope_id if Scope is no admin-scope
        if (!scope.getScopeType().equals(Constants.SCOPE_TYPE_ADMIN)) {
            final AdditionalWhereFieldVo additionalWhereFieldVo = new AdditionalWhereFieldVo();
            additionalWhereFieldVo.setAlliance(Constants.DATABASE_ALLIANCE_AND);
            additionalWhereFieldVo.setFieldName("scope_id");
            additionalWhereFieldVo.setFieldType(Constants.DATABASE_FIELD_TYPE_TEXT);
            additionalWhereFieldVo.setOperator(Constants.DATABASE_OPERATOR_EQUALS);
            additionalWhereFieldVo.setFieldValue(scopeId);
            additionalWhereFieldVos.add(additionalWhereFieldVo);
        }

        if (xpath != null && xpath.length() != 0) {
            final AdditionalWhereFieldVo xpathWhereFieldVo = new AdditionalWhereFieldVo();
            xpathWhereFieldVo.setAlliance(Constants.DATABASE_ALLIANCE_AND);
            xpathWhereFieldVo.setFieldType(Constants.DATABASE_FIELD_TYPE_FREE_SQL);
            xpathWhereFieldVo.setFieldValue(handleXpathQuery(xpath.replaceAll("\\s+", " "),
                Constants.STATISTIC_DATA_XML_FIELD_NAME));
            additionalWhereFieldVos.add(xpathWhereFieldVo);
        }

        rootWhereGroupVo.setAdditionalWhereFieldVos(additionalWhereFieldVos);

        databaseSelectVo.setRootWhereGroupVo(rootWhereGroupVo);
        return databaseSelectVo;
    }

    /**
     * Generates String (for db-query) out of xpath-element for extraction of raw data.
     *
     * @param inputXpathQuery The xpathQuery.
     * @param field           The field.
     * @return String with xpathQuery
     * @throws StatisticPreprocessingSystemException
     *          e
     */
    private String handleXpathQuery(final String inputXpathQuery, final String field)
        throws StatisticPreprocessingSystemException {
        try {
            final StringBuilder dbXpathQuery = new StringBuilder(" (");
            final String xpathQuery = inputXpathQuery.replaceAll("\\s+", " ");
            // Split at and/ors and save and/ors in array
            final String operatorHelper =
                xpathQuery.replaceAll(".*?(( and )|( AND )|( And )|( or )|( OR )|( Or )).*?", "$1\\|");
            final String[] operators = operatorHelper.split("\\|");
            final String[] xpathQueryParts = xpathQuery.split("( and )|( AND )|( And )|( or )|( OR )|( Or )");

            // iterate over xpath-query-parts
            // (eg //parameter[@name>\"type\"]/* > \u2018page-request\u2019)
            for (int i = 0; i < xpathQueryParts.length; i++) {
                if (i > 0) {
                    dbXpathQuery.append(' ').append(operators[i - 1]).append(' ');
                }

                // save opening and closing brackets
                String xpathQueryPart = xpathQueryParts[i].trim();
                final StringBuilder openingBracketSaver = new StringBuilder("");
                final StringBuilder closingBracketSaver = new StringBuilder("");
                while (xpathQueryPart.indexOf('(') == 0) {
                    xpathQueryPart = xpathQueryPart.substring(1);
                    openingBracketSaver.append('(');
                }
                while (xpathQueryPart.lastIndexOf(')') == xpathQueryPart.length() - 1) {
                    xpathQueryPart = xpathQueryPart.substring(0, xpathQueryPart.length() - 2);
                    closingBracketSaver.append(')');
                }

                // split xpath-query part at operator (<,> or =)
                final String xpathExpression = xpathQueryPart.replaceAll("(\\[.*?\\].*?)(=|>|<)", "$1\\|$2\\|");
                final String[] xpathExpressionParts = xpathExpression.split("\\|.*?\\|");
                dbXpathQuery.append(openingBracketSaver);
                if (xpathExpressionParts.length > 1) {
                    final String operator = xpathExpression.replaceAll(".*?\\|(.*?)\\|.*", "$1");
                    final String left = dbAccessor.getXpathString(xpathExpressionParts[0], field);
                    final String right = xpathExpressionParts[1].replaceAll("['\"]", "").trim();
                    dbXpathQuery.append(left).append(' ').append(operator).append(" '").append(right).append('\'');
                }
                else {
                    final String left = dbAccessor.getXpathBoolean(xpathExpressionParts[0], field);
                    dbXpathQuery.append(left);
                }
                dbXpathQuery.append(closingBracketSaver);
            }
            dbXpathQuery.append(") ");
            return dbXpathQuery.toString();
        }
        catch (final Exception e) {
            throw new StatisticPreprocessingSystemException("Cannot handle xpath-query for statistic-table", e);
        }
    }

}
