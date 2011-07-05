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

import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
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
 * @author Michael Hoppe
 */
public class AggregationDefinitionStaxHandler extends DefaultHandler {

    private AggregationDefinition aggregationDefinition = new AggregationDefinition();

    private final Set<AggregationStatisticDataSelector> aggregationStatisticDataSelectors =
        new HashSet<AggregationStatisticDataSelector>();

    private final Set<AggregationTable> aggregationTables = new HashSet<AggregationTable>();

    private static final String ROOT_PATH = "/aggregation-definition";

    private static final String TABLE_PATH = "/aggregation-definition/aggregation-table";

    private static final String TABLE_FIELD_PATH = "/aggregation-definition/aggregation-table/field";

    private static final String TABLE_INDEX_PATH = "/aggregation-definition/aggregation-table/index";

    private static final String STATISTIC_DATA_SELECTOR_PATH = "/aggregation-definition/statistic-data";

    private int tableIndex;

    private int tableFieldIndex;

    private int tableIndexIndex;

    private int tableIndexFieldIndex;

    private int statisticDataSelectorIndex;

    private boolean inTable;

    private boolean inTableField;

    private boolean inTableIndex;

    private boolean inStatisticDataSelector;

    private AggregationTableField aggregationTableField;

    private AggregationTableIndexe aggregationTableIndex;

    private AggregationTable aggregationTable;

    private AggregationStatisticDataSelector aggregationStatisticDataSelector;

    private final StaxParser parser;

    /**
     * Constructor with StaxParser.
     *
     * @param parser StaxParser
     */
    public AggregationDefinitionStaxHandler(final StaxParser parser) {
        this.parser = parser;
    }

    /**
     * Handle the character section of an element.
     *
     * @param s       The contents of the character section.
     * @param element The element.
     * @return The character section.
     * @throws Exception e
     */
    @Override
    public String characters(final String s, final StartElement element) throws Exception {
        if (this.inTable) {
            if (this.inTableField) {
                if ("name".equals(element.getLocalName())) {
                    if (aggregationTableField.getName() != null) {
                        aggregationTableField.setName(aggregationTableField.getName() + s);
                    }
                    else {
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
                            aggregationTableField.setXpath(aggregationTableField.getXpath() + s);
                        }
                        else {
                            aggregationTableField.setXpath(s);
                        }
                    }
                }
                else if ("reduce-to".equals(element.getLocalName())) {
                    aggregationTableField.setReduceTo(s);
                }
            }
            else if (this.inTableIndex) {
                if ("name".equals(element.getLocalName())) {
                    if (aggregationTableIndex.getName() != null) {
                        aggregationTableIndex.setName(aggregationTableIndex.getName() + s);
                    }
                    else {
                        aggregationTableIndex.setName(s);
                    }
                }
                else if ("field".equals(element.getLocalName())) {
                    this.tableIndexFieldIndex++;
                    final AggregationTableIndexField indexField = new AggregationTableIndexField();
                    indexField.setField(s);
                    indexField.setListIndex(this.tableIndexFieldIndex);
                    indexField.setAggregationTableIndexe(this.aggregationTableIndex);
                    aggregationTableIndex.getAggregationTableIndexFields().add(indexField);
                }
            }
            else {
                if ("name".equals(element.getLocalName())) {
                    if (aggregationTable.getName() != null) {
                        aggregationTable.setName(aggregationTable.getName() + s);
                    }
                    else {
                        aggregationTable.setName(s);
                    }
                }
            }
        }
        else if (this.inStatisticDataSelector) {
            if ("xpath".equals(element.getLocalName()) && s != null) {
                if (aggregationStatisticDataSelector.getXpath() != null) {
                    aggregationStatisticDataSelector.setXpath(aggregationStatisticDataSelector.getXpath() + s);
                }
                else {
                    aggregationStatisticDataSelector.setXpath(s);
                }
            }
        }
        else {
            if ("name".equals(element.getLocalName())) {
                if (aggregationDefinition.getName() != null) {
                    aggregationDefinition.setName(aggregationDefinition.getName() + s);
                }
                else {
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
     */
    @Override
    public StartElement startElement(final StartElement element) throws MissingAttributeValueException {
        final String currentPath = parser.getCurPath();
        boolean fieldRootElement = false;
        if (TABLE_PATH.equals(currentPath)) {
            this.inTable = true;
            this.tableIndex++;
            this.tableFieldIndex = 0;
            this.tableIndexIndex = 0;
            this.aggregationTable = new AggregationTable();
            aggregationTable.setListIndex(this.tableIndex);
        }
        else if (TABLE_FIELD_PATH.equals(currentPath)) {
            this.inTableField = true;
            this.tableFieldIndex++;
            this.aggregationTableField = new AggregationTableField();
            aggregationTableField.setListIndex(this.tableFieldIndex);
        }
        else if (TABLE_INDEX_PATH.equals(currentPath)) {
            this.inTableIndex = true;
            this.tableIndexIndex++;
            this.tableIndexFieldIndex = 0;
            this.aggregationTableIndex = new AggregationTableIndexe();
            aggregationTableIndex.setListIndex(this.tableIndexIndex);
        }
        else if (STATISTIC_DATA_SELECTOR_PATH.equals(currentPath)) {
            this.inStatisticDataSelector = true;
            this.statisticDataSelectorIndex++;
            this.aggregationStatisticDataSelector = new AggregationStatisticDataSelector();
            aggregationStatisticDataSelector.setListIndex(this.statisticDataSelectorIndex);
        }
        else if ("scope".equals(element.getLocalName())) {
            final String objId = XmlUtility.getIdFromStartElement(element);
            if (objId != null) {
                final Scope scope = new Scope();
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
        else if ("difference-cumulation-field".equals(element.getLocalName())) {
            aggregationTableField.setFieldTypeId(4);
            fieldRootElement = true;
        }
        else if ("statistic-table".equals(element.getLocalName())) {
            aggregationStatisticDataSelector.setSelectorType("statistic-table");
        }
        if (fieldRootElement) {
            final int indexOfAttribute = element.indexOfAttribute("", "feed");
            if (indexOfAttribute != -1) {
                final Attribute att = element.getAttribute(indexOfAttribute);
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
     */
    @Override
    public EndElement endElement(final EndElement element) throws SystemException {
        final String currentPath = parser.getCurPath();
        if (TABLE_PATH.equals(currentPath)) {
            this.inTable = false;
            aggregationTables.add(this.aggregationTable);
        }
        else if (TABLE_FIELD_PATH.equals(currentPath)) {
            this.inTableField = false;
            aggregationTableField.setAggregationTable(this.aggregationTable);
            aggregationTable.getAggregationTableFields().add(this.aggregationTableField);
        }
        else if (TABLE_INDEX_PATH.equals(currentPath)) {
            this.inTableIndex = false;
            aggregationTableIndex.setAggregationTable(this.aggregationTable);
            aggregationTable.getAggregationTableIndexes().add(this.aggregationTableIndex);
        }
        else if (STATISTIC_DATA_SELECTOR_PATH.equals(currentPath)) {
            this.inStatisticDataSelector = false;
            aggregationStatisticDataSelectors.add(this.aggregationStatisticDataSelector);
        }
        else if (ROOT_PATH.equals(currentPath)
            && (aggregationDefinition.getName() == null || this.aggregationStatisticDataSelectors == null
                || aggregationStatisticDataSelectors.isEmpty() || this.aggregationTables == null || aggregationTables
                .isEmpty())) {
            //check objects
            throw new SystemException("DataIntegrity violated");
        }
        return element;
    }

    /**
     * @param aggregationDefinition the aggregationDefinition
     * @throws SystemException e
     */
    public void setAggregationDefinition(final AggregationDefinition aggregationDefinition) throws SystemException {
        this.aggregationDefinition = aggregationDefinition;
        for (final AggregationStatisticDataSelector aggregationStatisticDataSel : this.aggregationStatisticDataSelectors) {
            aggregationStatisticDataSel.setAggregationDefinition(aggregationDefinition);
        }
        for (final AggregationTable aggregationTab : this.aggregationTables) {
            aggregationTab.setName(getReplacedTableOrIndexName(aggregationDefinition, aggregationTab.getName()));
            if (aggregationTab.getAggregationTableIndexes() != null) {
                for (final AggregationTableIndexe index : aggregationTab.getAggregationTableIndexes()) {
                    index.setName(getReplacedTableOrIndexName(aggregationDefinition, index.getName()));
                }
            }
            aggregationTab.setAggregationDefinition(aggregationDefinition);
        }
    }

    /**
     * Prefix table or index name with aggregationDefinitionId.
     *
     * @param aggregationDef the aggregationDefinition
     * @param orgName        the original Name
     * @return String prefixed name
     * @throws SystemException e
     */
    private static String getReplacedTableOrIndexName(final AggregationDefinition aggregationDef, final String orgName)
        throws SystemException {
        if (aggregationDef == null || aggregationDef.getId() == null) {
            throw new SystemException("aggregationDefinition PrimKey may not be null");
        }
        final StringBuilder replaced = new StringBuilder();
        replaced.append(aggregationDef.getId().replaceAll("\\:", "")).append('_').append(orgName);

        return replaced.toString();
    }

    /**
     * @return the aggregationDefinition
     */
    public AggregationDefinition getAggregationDefinition() {
        return this.aggregationDefinition;
    }

    /**
     * @return the aggregationStatisticDataSelectors
     */
    public Set<AggregationStatisticDataSelector> getAggregationStatisticDataSelectors() {
        return this.aggregationStatisticDataSelectors;
    }

    /**
     * @return the aggregationTables
     */
    public Set<AggregationTable> getAggregationTables() {
        return this.aggregationTables;
    }

}
