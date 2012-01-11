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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.core.domain.sru.parameters;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MIH
 * 
 */
public class SruSearchRequestParametersBean {

    public static final String SRU_PARAMETER_OPERATION = "operation";

    public static final String SRU_PARAMETER_QUERY = "query";

    public static final String SRU_PARAMETER_START_RECORD = "startRecord";

    public static final String SRU_PARAMETER_MAXIMUM_RECORDS = "maximumRecords";

    public static final String SRU_PARAMETER_EXPLAIN = "explain";

    public static final String SRU_PARAMETER_RECORD_PACKING = "recordPacking";

    public static final String SRU_PARAMETER_RECORD_SCHEMA = "recordSchema";

    public static final String SRU_PARAMETER_VERSION = "version";

    public static final String SRU_PARAMETER_RECORD_XPATH = "recordXPath";

    public static final String SRU_PARAMETER_RESULT_SET_TTL = "resultSetTTL";

    public static final String SRU_PARAMETER_SORT_KEYS = "sortKeys";

    public static final String SRU_PARAMETER_STYLESHEET = "stylesheet";

    public static final String SRU_PARAMETER_SCAN_CLAUSE = "scanClause";

    public static final String SRU_PARAMETER_RESPONSE_POSITION = "responsePosition";

    public static final String SRU_PARAMETER_MAXIMUM_TERMS = "maximumTerms";

    public static final String SRU_PARAMETER_ROLE = "x-info5-roleId";

    public static final String SRU_PARAMETER_USER = "x-info5-userId";

    public static final String SRU_PARAMETER_OMIT_HIGHLIGHTING = "x-info5-omitHighlighting";

    private String operation = "searchRetrieve"; // default behavior

    private String version;

    private String query;

    private String startRecord = "1"; // default behavior

    // @XmlSchemaType(name = "nonNegativeInteger")
    private String maximumRecords;

    private String recordPacking = "string"; // default behavior

    private String recordSchema;

    private String recordXPath;

    // @XmlSchemaType(name = "nonNegativeInteger")
    private String resultSetTTL;

    private String sortKeys;

    // @XmlSchemaType(name = "anyURI")
    private String stylesheet;

    // for scan requests only

    private String scanClause;

    // @XmlSchemaType(name = "nonNegativeInteger")
    private String responsePosition = "1"; // default behavior

    // @XmlSchemaType(name = "positiveInteger")
    private String maximumTerms;
    
    private String user;
    private String role;
    
    private String omitHighlighting;

    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @return the startRecord
     */
    public String getStartRecord() {
        return startRecord;
    }

    /**
     * @return the maximumRecords
     */
    public String getMaximumRecords() {
        return maximumRecords;
    }

    /**
     * @return the recordPacking
     */
    public String getRecordPacking() {
        return recordPacking;
    }

    /**
     * @return the recordSchema
     */
    public String getRecordSchema() {
        return recordSchema;
    }

    /**
     * @return the recordXPath
     */
    public String getRecordXPath() {
        return recordXPath;
    }

    /**
     * @return the resultSetTTL
     */
    public String getResultSetTTL() {
        return resultSetTTL;
    }

    /**
     * @return the sortKeys
     */
    public String getSortKeys() {
        return sortKeys;
    }

    /**
     * @return the stylesheet
     */
    public String getStylesheet() {
        return stylesheet;
    }

    /**
     * @return the scanClause
     */
    public String getScanClause() {
        return scanClause;
    }

    /**
     * @return the responsePosition
     */
    public String getResponsePosition() {
        return responsePosition;
    }

    /**
     * @return the maximumTerms
     */
    public String getMaximumTerms() {
        return maximumTerms;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @return the omitHighlighting
     */
    public String getOmitHighlighting() {
        return omitHighlighting;
    }

    /**
     * @param operation
     *            the operation to set
     */
    public void setOperation(final String operation) {
        this.operation = operation;
    }

    /**
     * @param version
     *            the version to set
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * @param query
     *            the query to set
     */
    public void setQuery(final String query) {
        this.query = query;
    }

    /**
     * @param startRecord
     *            the startRecord to set
     */
    public void setStartRecord(final String startRecord) {
        this.startRecord = startRecord;
    }

    /**
     * @param maximumRecords
     *            the maximumRecords to set
     */
    public void setMaximumRecords(final String maximumRecords) {
        this.maximumRecords = maximumRecords;
    }

    /**
     * @param recordPacking
     *            the recordPacking to set
     */
    public void setRecordPacking(final String recordPacking) {
        this.recordPacking = recordPacking;
    }

    /**
     * @param recordSchema
     *            the recordSchema to set
     */
    public void setRecordSchema(final String recordSchema) {
        this.recordSchema = recordSchema;
    }

    /**
     * @param recordXPath
     *            the recordXPath to set
     */
    public void setRecordXPath(final String recordXPath) {
        this.recordXPath = recordXPath;
    }

    /**
     * @param resultSetTTL
     *            the resultSetTTL to set
     */
    public void setResultSetTTL(final String resultSetTTL) {
        this.resultSetTTL = resultSetTTL;
    }

    /**
     * @param sortKeys
     *            the sortKeys to set
     */
    public void setSortKeys(final String sortKeys) {
        this.sortKeys = sortKeys;
    }

    /**
     * @param stylesheet
     *            the stylesheet to set
     */
    public void setStylesheet(final String stylesheet) {
        this.stylesheet = stylesheet;
    }

    /**
     * @param scanClause
     *            the scanClause to set
     */
    public void setScanClause(final String scanClause) {
        this.scanClause = scanClause;
    }

    /**
     * @param responsePosition
     *            the responsePosition to set
     */
    public void setResponsePosition(final String responsePosition) {
        this.responsePosition = responsePosition;
    }

    /**
     * @param maximumTerms
     *            the maximumTerms to set
     */
    public void setMaximumTerms(final String maximumTerms) {
        this.maximumTerms = maximumTerms;
    }
    
    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @param omitHighlighting the omitHighlighting to set
     */
    public void setOmitHighlighting(String omitHighlighting) {
        this.omitHighlighting = omitHighlighting;
    }

    /**
     * return Object as Map
     * @return Map parameterMap
     */
    public Map<String, String[]> toParameterMap() {
        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap.put(SRU_PARAMETER_OPERATION, new String[]{getOperation()});
        parameterMap.put(SRU_PARAMETER_START_RECORD, new String[]{getStartRecord()});
        parameterMap.put(SRU_PARAMETER_RECORD_PACKING, new String[]{getRecordPacking()});
        parameterMap.put(SRU_PARAMETER_RESPONSE_POSITION, new String[]{getResponsePosition()});
        if (getVersion() != null) {
            parameterMap.put(SRU_PARAMETER_VERSION, new String[]{getVersion()});
        }
        if (getQuery() != null) {
            parameterMap.put(SRU_PARAMETER_QUERY, new String[]{getQuery()});
        }
        if (getMaximumRecords() != null) {
            parameterMap.put(SRU_PARAMETER_MAXIMUM_RECORDS, new String[]{getMaximumRecords()});
        }
        if (getRecordSchema() != null) {
            parameterMap.put(SRU_PARAMETER_RECORD_SCHEMA, new String[]{getRecordSchema()});
        }
        if (getRecordXPath() != null) {
            parameterMap.put(SRU_PARAMETER_RECORD_XPATH, new String[]{getRecordXPath()});
        }
        if (getResultSetTTL() != null) {
            parameterMap.put(SRU_PARAMETER_RESULT_SET_TTL, new String[]{getResultSetTTL()});
        }
        if (getSortKeys() != null) {
            parameterMap.put(SRU_PARAMETER_SORT_KEYS, new String[]{getSortKeys()});
        }
        if (getStylesheet() != null) {
            parameterMap.put(SRU_PARAMETER_STYLESHEET, new String[]{getStylesheet()});
        }
        if (getScanClause() != null) {
            parameterMap.put(SRU_PARAMETER_SCAN_CLAUSE, new String[]{getScanClause()});
        }
        if (getMaximumTerms() != null) {
            parameterMap.put(SRU_PARAMETER_MAXIMUM_TERMS, new String[]{getMaximumTerms()});
        }
        if (getUser() != null) {
            parameterMap.put(SRU_PARAMETER_USER, new String[]{getUser()});
        }
        if (getRole() != null) {
            parameterMap.put(SRU_PARAMETER_ROLE, new String[]{getRole()});
        }
        if (getOmitHighlighting() != null) {
            parameterMap.put(SRU_PARAMETER_OMIT_HIGHLIGHTING, new String[]{getOmitHighlighting()});
        }
        return parameterMap;
    }
}