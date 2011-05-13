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

/**
 * Constants.
 *
 * @author Michael Hoppe
 */
public final class Constants {

    /**
     * Private Constructor.
     */
    private Constants() {
    }

    /**
     * Database-Constants.
     */
    public static final String SM_SCHEMA_NAME = "sm";

    public static final String AGGREGATION_DEFINITIONS_TABLE_NAME = "aggregation_definitions";

    public static final String STATISTIC_DATA_TABLE_NAME = "statistic_data";

    public static final String STATISTIC_DATA_TIMESTAMP_FIELD_NAME = "timemarker";

    public static final String STATISTIC_DATA_XML_FIELD_NAME = "xml_data";

    public static final String FRAMEWORK_SCOPE_ID = "1";

    public static final String DATABASE_FIELD_TYPE_NUMERIC = "numeric";

    public static final String DATABASE_FIELD_TYPE_TEXT = "text";

    public static final String DATABASE_FIELD_TYPE_DATE = "date";

    public static final String DATABASE_FIELD_TYPE_DAYDATE = "daydate";

    public static final String DATABASE_FIELD_TYPE_XPATH_BOOLEAN = "xpath-boolean";

    public static final String DATABASE_FIELD_TYPE_XPATH_STRING = "xpath-string";

    public static final String DATABASE_FIELD_TYPE_XPATH_NUMERIC = "xpath-numeric";

    public static final String DATABASE_FIELD_TYPE_FREE_SQL = "free-sql";

    public static final String DATABASE_OPERATOR_EQUALS = "=";

    public static final String DATABASE_OPERATOR_LESS = "<";

    public static final String DATABASE_OPERATOR_GREATER = ">";

    public static final String DATABASE_ALLIANCE_AND = "AND";

    public static final String DATABASE_ALLIANCE_OR = "OR";

    public static final String DATABASE_SELECT_TYPE_SELECT = "SELECT";

    public static final String DATABASE_SELECT_TYPE_DELETE = "DELETE";

    public static final String DATABASE_SELECT_TYPE_UPDATE = "UPDATE";

    /**
     * Aggregation-Definition selector types.
     */
    public static final String AGGREGATION_DEFINITION_TABLE_SELECTOR_TYPE = "statistic-table";

    public static final String AGGREGATION_DEFINITION_EXTERNAL_SELECTOR_TYPE = "external";

    /**
     * Aggregation-Definition Element names + types.
     */
    public static final String AGGREGATION_DEFINITION_ROOT_ELEMENT_NAME = "aggregation-definition";

    public static final String COUNT_CUMULATION_FIELD = "count-cumulation";

    public static final String DIFFERENCE_CUMULATION_FIELD = "difference-cumulation";

    public static final String INFO_FIELD = "info";

    public static final String TIME_REDUCTION_FIELD = "time-reduction";

    public static final int INFO_FIELD_ID = 1;

    public static final int TIME_REDUCTION_FIELD_ID = 2;

    public static final int COUNT_CUMULATION_FIELD_ID = 3;

    public static final int DIFFERENCE_CUMULATION_FIELD_ID = 4;

    public static final String TIME_REDUCTION_TYPE_YEAR = "year";

    public static final String TIME_REDUCTION_TYPE_MONTH = "month";

    public static final String TIME_REDUCTION_TYPE_DAY = "day";

    public static final String TIME_REDUCTION_TYPE_WEEKDAY = "weekday";

    public static final String AGGREGATION_DEFINITION_INDEX_NAME_ELEMENT_PATH =
        "/aggregation-definition/aggregation-table/index/name";

    public static final String AGGREGATION_DEFINITION_TABLE_NAME_ELEMENT_PATH =
        "/aggregation-definition/aggregation-table/name";

    /**
     * Report-Definition Element names.
     */
    public static final String REPORT_DEFINITION_ROOT_ELEMENT_NAME = "report-definition";

    /**
     * Scope Element names.
     */
    public static final String SCOPE_ROOT_ELEMENT_NAME = "scope";

    /**
     * Scope types.
     */
    public static final String SCOPE_TYPE_ADMIN = "admin";

    /**
     * Object Types for filtering (AA).
     */
    public static final String SCOPE_OBJECT_TYPE = "scope";

    /**
     * JAXB-Binding Constants.
     */
    public static final String AGGREGATION_DEFINITION_CONTEXT_PATH =
        "de.escidoc.core.sm.bindings.aggregationdefinition";

    public static final String REPORT_PARAMETERS_CONTEXT_PATH = "de.escidoc.core.sm.bindings.reportparameters";

    public static final String REPORT_DEFINITION_CONTEXT_PATH = "de.escidoc.core.sm.bindings.reportdefinition";

    public static final String SCOPE_CONTEXT_PATH = "de.escidoc.core.sm.bindings.scope";

    /**
     * Hibernate Constants.
     */
    public static final String AGGREGATION_DEFINITION_HIBERNATE_TABLE_NAME = "AggregationDefinition";

    public static final String REPORT_DEFINITION_HIBERNATE_TABLE_NAME = "ReportDefinition";

    public static final String SCOPE_HIBERNATE_TABLE_NAME = "Scope";

}
