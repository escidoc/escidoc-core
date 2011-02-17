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
package de.escidoc.core.sm.business.stax.handler;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.events.Attribute;
import de.escidoc.core.common.util.xml.stax.events.EndElement;
import de.escidoc.core.common.util.xml.stax.events.StartElement;
import de.escidoc.core.common.util.xml.stax.handler.DefaultHandler;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationDefinition;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationStatisticDataSelector;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationTable;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationTableField;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationTableIndexField;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationTableIndexe;
import de.escidoc.core.sm.business.persistence.hibernate.Scope;

import java.util.HashSet;
import java.util.Set;

/**
 * Fills xml-data into hibernate object.
 * 
 * @author MIH
 */
public class AggregationDefinitionStaxHandler extends DefaultHandler {

    private AggregationDefinition aggregationDefinition =
            new AggregationDefinition();

    private final Set<AggregationStatisticDataSelector>
                aggregationStatisticDataSelectors =
            new HashSet<AggregationStatisticDataSelector>();

    private final Set<AggregationTable> aggregationTables =
            new HashSet<AggregationTable>();
    
    private final String rootPath = "/aggregation-definition";
    
    private final String tablePath = "/aggregation-definition/aggregation-table";
    
    private final String tableFieldPath = 
            "/aggregation-definition/aggregation-table/field";
    
    private final String tableIndexPath = 
        "/aggregation-definition/aggregation-table/index";
    
    private final String statisticDataSelectorPath = 
                "/aggregation-definition/statistic-data";
    
    private int tableIndex = 0;
    
    private int tableFieldIndex = 0;
    
    private int tableIndexIndex = 0;
    
    private int tableIndexFieldIndex = 0;
    
    private int statisticDataSelectorIndex = 0;

    private boolean inTable = false;
    
    private boolean inTableField = false;
    
    private boolean inTableIndex = false;
    
    private boolean inStatisticDataSelector = false;
    
    private AggregationTableField aggregationTableField = null;

    private AggregationTableIndexe aggregationTableIndex = null;

    private AggregationTable aggregationTable = null;

    private AggregationStatisticDataSelector 
                aggregationStatisticDataSelector = null;

    private final StaxParser parser;

    /**
     * Constructor with StaxParser.
     * 
     * @param parser
     *            StaxParser
     * 
     */
    public AggregationDefinitionStaxHandler(final StaxParser parser) {
        this.parser = parser;
    }

    /**
     * Handle the character section of an element.
     * 
     * @param s
     *            The contents of the character section.
     * @param element
     *            The element.
     * @return The character section.
     * @throws Exception
     *             e
     * @see de.escidoc.core.common.util.xml.stax.handler.DefaultHandler#characters
     *      (java.lang.String,
     *      de.escidoc.core.common.util.xml.stax.events.StartElement)
     */
    public String characters(final String s, final StartElement element)
        throws Exception {
        if (inTable) {
            if (inTableField) {
                if ("name".equals(element.getLocalName())) {
                    if (aggregationTableField.getName() != null) {
                        aggregationTableField.setName(
                            aggregationTableField.getName() + s);
                    } else {
                        aggregationTableField.setName(s);
                    }
                }
                else if ("type".equals(element.getLocalName())) {
                    if (s != null) {
                        aggregationTableField.setDataType(s.trim());
                    }
                }
                else if ("xpath".equals(element.getLocalName())) {
                    if (s != null) {
                        if (aggregationTableField.getXpath() != null) {
                            aggregationTableField.setXpath(
                                aggregationTableField.getXpath() + s);
                        } else {
                            aggregationTableField.setXpath(s);
                        }
                    }
                }
                else if ("reduce-to".equals(element.getLocalName())) {
                    aggregationTableField.setReduceTo(s);
                }
            }
            else if (inTableIndex) {
                if ("name".equals(element.getLocalName())) {
                    if (aggregationTableIndex.getName() != null) {
                        aggregationTableIndex.setName(
                            aggregationTableIndex.getName() + s);
                    } else {
                        aggregationTableIndex.setName(s);
                    }
                }
                else if ("field".equals(element.getLocalName())) {
                    tableIndexFieldIndex++;
                    AggregationTableIndexField indexField =
                        new AggregationTableIndexField();
                    indexField.setField(s);
                    indexField.setListIndex(tableIndexFieldIndex);
                    indexField.setAggregationTableIndexe(aggregationTableIndex);
                    aggregationTableIndex.getAggregationTableIndexFields().add(
                        indexField);
                }
            }
            else {
                if ("name".equals(element.getLocalName())) {
                    if (aggregationTable.getName() != null) {
                        aggregationTable.setName(
                            aggregationTable.getName() + s);
                    } else {
                        aggregationTable.setName(s);
                    }
                }
            }
        }
        else if (inStatisticDataSelector) {
            if (("xpath".equals(element.getLocalName()))
                && (s != null)) {
                if (aggregationStatisticDataSelector.getXpath() != null) {
                    aggregationStatisticDataSelector.setXpath(
                        aggregationStatisticDataSelector.getXpath() + s);
                } else {
                    aggregationStatisticDataSelector.setXpath(s);
                }
            }
        }
        else {
            if ("name".equals(element.getLocalName())) {
                if (aggregationDefinition.getName() != null) {
                    aggregationDefinition.setName(
                        aggregationDefinition.getName() + s);
                } else {
                    aggregationDefinition.setName(s);
                }
            }
        }
        return s;
    }

    /**
     * Handle startElement event.
     * 
     * @param element startElement
     * @return StartElement startElement
     * @throws Exception e
     * 
     */
    public StartElement startElement(final StartElement element) throws Exception {
        String currentPath = parser.getCurPath();
        boolean fieldRootElement = false;
        if (tablePath.equals(currentPath)) {
            inTable = true;
            tableIndex++;
            tableFieldIndex = 0;
            tableIndexIndex = 0;
            aggregationTable = new AggregationTable();
            aggregationTable.setListIndex(tableIndex);
        }
        else if (tableFieldPath.equals(currentPath)) {
            inTableField = true;
            tableFieldIndex++;
            aggregationTableField = new AggregationTableField();
            aggregationTableField.setListIndex(tableFieldIndex);
        }
        else if (tableIndexPath.equals(currentPath)) {
            inTableIndex = true;
            tableIndexIndex++;
            tableIndexFieldIndex = 0;
            aggregationTableIndex = new AggregationTableIndexe();
            aggregationTableIndex.setListIndex(tableIndexIndex);
        }
        else if (statisticDataSelectorPath.equals(currentPath)) {
            inStatisticDataSelector = true;
            statisticDataSelectorIndex++;
            aggregationStatisticDataSelector = 
                        new AggregationStatisticDataSelector();
            aggregationStatisticDataSelector.setListIndex(
                                    statisticDataSelectorIndex);
        }
        else if ("scope".equals(element.getLocalName())) {
            String objId = XmlUtility.getIdFromStartElement(element);
            if (objId != null) {
                Scope scope = new Scope();
                scope.setId(objId);
                aggregationDefinition.setScope(scope);
            }
        }
        else if ("info-field".equals(element.getLocalName())) {
            aggregationTableField.setFieldTypeId(1);
            fieldRootElement = true;
        } 
        else if ("time-reduction-field".equals(element.getLocalName())) {
            aggregationTableField.setFieldTypeId(2);
            fieldRootElement = true;
        }
        else if ("count-cumulation-field".equals(element.getLocalName())) {
            aggregationTableField.setFieldTypeId(3);
            fieldRootElement = true;
        }
        else if ("difference-cumulation-field"
                    .equals(element.getLocalName())) {
            aggregationTableField.setFieldTypeId(4);
            fieldRootElement = true;
        }
        else if ("statistic-table".equals(element.getLocalName())) {
            aggregationStatisticDataSelector
                    .setSelectorType("statistic-table");
        } 
        if (fieldRootElement) {
            int indexOfAttribute = element.indexOfAttribute("", "feed");
            if (indexOfAttribute != (-1)) {
                Attribute att = element.getAttribute(indexOfAttribute);
                aggregationTableField.setFeed(att.getValue());
            }
        }
        return element;
    }

    /**
     * Handle endElement event.
     * 
     * @param element endElement
     * @return EndElement endElement
     * @throws Exception e
     * 
     */
    public EndElement endElement(final EndElement element) throws Exception {
        String currentPath = parser.getCurPath();
        if (tablePath.equals(currentPath)) {
            inTable = false;
            aggregationTables.add(aggregationTable);
        }
        else if (tableFieldPath.equals(currentPath)) {
            inTableField = false;
            aggregationTableField.setAggregationTable(aggregationTable);
            aggregationTable.getAggregationTableFields()
                                .add(aggregationTableField);
        }
        else if (tableIndexPath.equals(currentPath)) {
            inTableIndex = false;
            aggregationTableIndex.setAggregationTable(aggregationTable);
            aggregationTable.getAggregationTableIndexes()
            .add(aggregationTableIndex);
        }
        else if (statisticDataSelectorPath.equals(currentPath)) {
            inStatisticDataSelector = false;
            aggregationStatisticDataSelectors
                                    .add(aggregationStatisticDataSelector);
        }
        else if ((rootPath.equals(currentPath))
            && (aggregationDefinition.getName() == null
                    || aggregationStatisticDataSelectors == null
                    || aggregationStatisticDataSelectors.isEmpty()
                    || aggregationTables == null
                    || aggregationTables.isEmpty())) {
            //check objects
            throw new SystemException("DataIntegrity violated");
        }
        return element;
    }

    /**
     * @param aggregationDefinition the aggregationDefinition
     * @throws SystemException e
     */
    public void setAggregationDefinition(
            final AggregationDefinition aggregationDefinition) 
                                        throws SystemException {
        this.aggregationDefinition = aggregationDefinition;
        for (AggregationStatisticDataSelector aggregationStatisticDataSel 
                                        : aggregationStatisticDataSelectors) {
            aggregationStatisticDataSel
                    .setAggregationDefinition(aggregationDefinition);
        }
        for (AggregationTable aggregationTab : aggregationTables) {
            aggregationTab.setName(getReplacedTableOrIndexName(
                    aggregationDefinition, aggregationTab.getName()));
            if (aggregationTab.getAggregationTableIndexes() != null) {
                for (AggregationTableIndexe index 
                        : aggregationTab.getAggregationTableIndexes()) {
                    index.setName(getReplacedTableOrIndexName(
                            aggregationDefinition, index.getName()));
                }
            }
            aggregationTab
                    .setAggregationDefinition(aggregationDefinition);
        }
    }
    
    /**
     * Prefix table or index name with aggregationDefinitionId.
     * 
     * @param aggregationDef the aggregationDefinition
     * @param orgName the orgiginal Name
     * @return String prefixed name
     * @throws SystemException e
     */
    private String getReplacedTableOrIndexName(
            final AggregationDefinition aggregationDef, 
            final String orgName) throws SystemException {
        if (aggregationDef == null 
                || aggregationDef.getId() == null) {
            throw new SystemException(
                    "aggregationDefinition PrimKey may not be null");
        }
        StringBuilder replaced = new StringBuilder();
        replaced.append('_')
            .append(aggregationDef.getId().replaceAll("\\:", ""))
            .append('_');
        
        replaced.append(orgName);
        return replaced.toString();
    }
    
    /**
     * @return the aggregationDefinition
     */
    public AggregationDefinition getAggregationDefinition() {
        return aggregationDefinition;
    }

    /**
     * @return the aggregationStatisticDataSelectors
     */
    public Set<AggregationStatisticDataSelector> 
                getAggregationStatisticDataSelectors() {
        return aggregationStatisticDataSelectors;
    }

    /**
     * @return the aggregationTables
     */
    public Set<AggregationTable> getAggregationTables() {
        return aggregationTables;
    }

}
