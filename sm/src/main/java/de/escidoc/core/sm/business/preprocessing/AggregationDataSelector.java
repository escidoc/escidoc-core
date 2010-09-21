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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.StatisticPreprocessingSystemException;
import de.escidoc.core.common.exceptions.system.XmlParserSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.sm.business.Constants;
import de.escidoc.core.sm.business.SmXmlUtility;
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

/**
 * Extracts data for Aggregation Definition out of raw-statistic-data-table.
 * 
 * @spring.bean id="business.AggregationDataSelector" scope="prototype"
 * @tx
 * @author MIH
 * @sm
 */
public class AggregationDataSelector {

    private static AppLogger log =
        new AppLogger(AggregationDataSelector.class.getName());
    
    private DirectDatabaseAccessorInterface dbAccessor;

    private SmScopesDaoInterface scopesDao;

    private SmXmlUtility xmlUtility;
    
    public List getDataForAggregation(
            AggregationDefinition aggregationDefinition, Date date)
            throws ScopeNotFoundException, SqlDatabaseSystemException,
            StatisticPreprocessingSystemException, XmlParserSystemException {
        if (aggregationDefinition
                .getAggregationStatisticDataSelectors() != null) {
            AggregationStatisticDataSelector selector = null;
            for (AggregationStatisticDataSelector aggregationStatisticDataSelector 
                    : (Set<AggregationStatisticDataSelector>) 
                    aggregationDefinition
                    .getAggregationStatisticDataSelectors()) {
                if (aggregationStatisticDataSelector
                        .getSelectorType().equals("statistic-table")) {
                    selector = aggregationStatisticDataSelector;
                }
            }
            if (selector != null) {
                return dbAccessor
                .executeSql(generateStatisticTableSelectVo(
                        selector,
                aggregationDefinition.getScope().getId(),
                date));
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    /**
     * Generates DatabaseSelectVo for DirectDatabaseAccessor. Selects
     * statistic-records from statistic-table, matching scopeId, date and xpath.
     * 
     * @param scopeId
     *            scopeId
     * @param xpath
     *            xpath
     * @param date
     *            date
     * @return DatabaseSelectVo Returns DatabaseSelectVo with recordInfo.
     * @throws SqlDatabaseSystemException
     *             e
     * @throws XmlParserSystemException
     *             e
     * @throws StatisticPreprocessingSystemException
     *             e
     * @throws ScopeNotFoundException
     *             e
     * 
     * 
     * @sm
     */
    private DatabaseSelectVo generateStatisticTableSelectVo(
        final AggregationStatisticDataSelector aggregationStatisticDataSelector, 
        final String scopeId, final Date date)
                throws StatisticPreprocessingSystemException, XmlParserSystemException, 
                ScopeNotFoundException, SqlDatabaseSystemException {
        String xpath = null;
        if (aggregationStatisticDataSelector.getSelectorType() != null
                && aggregationStatisticDataSelector.getSelectorType()
                .equals("statistic-table")
            && aggregationStatisticDataSelector.getXpath() != null) {
            xpath = 
                aggregationStatisticDataSelector.getXpath();
        }
        DatabaseSelectVo databaseSelectVo = new DatabaseSelectVo();
        databaseSelectVo.setSelectType(Constants.DATABASE_SELECT_TYPE_SELECT);
        Collection<String> tablenames = new ArrayList<String>();
        tablenames.add(Constants.STATISTIC_DATA_TABLE_NAME);
        databaseSelectVo.setTableNames(tablenames);

        Collection<SelectFieldVo> selectFieldVos = new ArrayList<SelectFieldVo>();
        SelectFieldVo selectFieldVo = new SelectFieldVo();
        selectFieldVo.setFieldName(Constants.STATISTIC_DATA_XML_FIELD_NAME);
        selectFieldVos.add(selectFieldVo);
        SelectFieldVo selectFieldVo1 = new SelectFieldVo();
        selectFieldVo1.setFieldName(
        		Constants.STATISTIC_DATA_TIMESTAMP_FIELD_NAME);
        selectFieldVos.add(selectFieldVo1);
        databaseSelectVo.setSelectFieldVos(selectFieldVos);

        RootWhereGroupVo rootWhereGroupVo = new RootWhereGroupVo();
        RootWhereFieldVo rootWhereFieldVo = new RootWhereFieldVo();
        Collection<AdditionalWhereFieldVo> additionalWhereFieldVos =
            new ArrayList<AdditionalWhereFieldVo>();

        rootWhereFieldVo.setFieldName(
        		Constants.STATISTIC_DATA_TIMESTAMP_FIELD_NAME);
        rootWhereFieldVo
            .setFieldType(Constants.DATABASE_FIELD_TYPE_DAYDATE);
        rootWhereFieldVo.setOperator(Constants.DATABASE_OPERATOR_EQUALS);
        rootWhereFieldVo.setFieldValue(new SimpleDateFormat("yyyy-MM-dd").format(date));
        rootWhereGroupVo.setRootWhereFieldVo(rootWhereFieldVo);


        //get Scope to decide if scope-type is admin
        Scope scope = scopesDao.retrieve(scopeId);

        //only restrict to scope_id if Scope is no admin-scope
        if (!scope.getScopeType().equals(Constants.SCOPE_TYPE_ADMIN)) {
            AdditionalWhereFieldVo additionalWhereFieldVo =
                new AdditionalWhereFieldVo();
            additionalWhereFieldVo.setAlliance(Constants.DATABASE_ALLIANCE_AND);
            additionalWhereFieldVo.setFieldName("scope_id");
            additionalWhereFieldVo.setFieldType(Constants.DATABASE_FIELD_TYPE_TEXT);
            additionalWhereFieldVo.setOperator(Constants.DATABASE_OPERATOR_EQUALS);
            additionalWhereFieldVo.setFieldValue(scopeId);
            additionalWhereFieldVos.add(additionalWhereFieldVo);
        }

        if (xpath != null && !xpath.equals("")) {
            AdditionalWhereFieldVo xpathWhereFieldVo =
                new AdditionalWhereFieldVo();
            xpathWhereFieldVo.setAlliance(Constants.DATABASE_ALLIANCE_AND);
            xpathWhereFieldVo
                .setFieldType(Constants.DATABASE_FIELD_TYPE_FREE_SQL);
            xpathWhereFieldVo.setFieldValue(
                handleXpathQuery(xpath.replaceAll("\\s+", " "), 
                Constants.STATISTIC_DATA_XML_FIELD_NAME));
            additionalWhereFieldVos.add(xpathWhereFieldVo);
        }

        rootWhereGroupVo.setAdditionalWhereFieldVos(additionalWhereFieldVos);

        databaseSelectVo.setRootWhereGroupVo(rootWhereGroupVo);
        return databaseSelectVo;
    }

    /**
     * Generates String (for db-query) out of xpath-element for extraction of
     * raw data.
     * 
     * @param inputXpathQuery
     *            The xpathQuery.
     * @param field
     *            The field.
     * @return String with xpathQuery
     * @throws StatisticPreprocessingSystemException
     *             e
     * 
     * @sm
     */
    private String handleXpathQuery(
        final String inputXpathQuery, final String field)
        throws StatisticPreprocessingSystemException {
        try {
            StringBuffer dbXpathQuery = new StringBuffer(" (");
            String xpathQuery = inputXpathQuery.replaceAll("\\s+", " ");
            // Split at and/ors and save and/ors in array
            String operatorHelper =
                xpathQuery.replaceAll(
                    ".*?(( and )|( AND )|( And )|( or )|( OR )|( Or )).*?",
                    "$1\\|");
            String[] operators = operatorHelper.split("\\|");
            String[] xpathQueryParts =
                xpathQuery
                    .split("( and )|( AND )|( And )|( or )|( OR )|( Or )");

            // iterate over xpath-query-parts
            // (eg //parameter[@name>\"type\"]/* > \u2018page-request\u2019)
            for (int i = 0; i < xpathQueryParts.length; i++) {
                if (i > 0) {
                    dbXpathQuery.append(" ").append(operators[i - 1]).append(
                        " ");
                }

                // save opening and closing brackets
                String xpathQueryPart = xpathQueryParts[i].trim();
                StringBuffer openingBracketSaver = new StringBuffer("");
                StringBuffer closingBracketSaver = new StringBuffer("");
                while (xpathQueryPart.indexOf("(") == 0) {
                    xpathQueryPart = xpathQueryPart.substring(1);
                    openingBracketSaver.append("(");
                }
                while (xpathQueryPart.lastIndexOf(")") == xpathQueryPart
                    .length() - 1) {
                    xpathQueryPart =
                        xpathQueryPart
                            .substring(0, xpathQueryPart.length() - 2);
                    closingBracketSaver.append(")");
                }

                // split xpath-query part at operator (<,> or =)
                String xpathExpression =
                    xpathQueryPart.replaceAll("(\\[.*?\\].*?)(=|>|<)",
                        "$1\\|$2\\|");
                String[] xpathExpressionParts =
                    xpathExpression.split("\\|.*?\\|");
                dbXpathQuery.append(openingBracketSaver);
                if (xpathExpressionParts.length > 1) {
                    String operator =
                        xpathExpression.replaceAll(".*?\\|(.*?)\\|.*", "$1");
                    String left =
                        dbAccessor.getXpathString(xpathExpressionParts[0],
                            field);
                    String right =
                        xpathExpressionParts[1].replaceAll("['\"]", "");
                    dbXpathQuery
                        .append(left).append(" ").append(operator).append(" '")
                        .append(right).append("'");
                }
                else {
                    String left =
                        dbAccessor.getXpathBoolean(xpathExpressionParts[0],
                            field);
                    dbXpathQuery.append(left);
                }
                dbXpathQuery.append(closingBracketSaver);
            }
            dbXpathQuery.append(") ");
            return dbXpathQuery.toString();
        }
        catch (Exception e) {
            log.error("Cannot handle xpath-query for statistic-table");
            throw new StatisticPreprocessingSystemException(
                "Cannot handle xpath-query for statistic-table");
        }
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
     * Setting the xmlUtility.
     * 
     * @param xmlUtility
     *            The xmlUtility to set.
     * @spring.property ref="business.sm.XmlUtility"
     */
    public final void setXmlUtility(final SmXmlUtility xmlUtility) {
        this.xmlUtility = xmlUtility;
    }

}
