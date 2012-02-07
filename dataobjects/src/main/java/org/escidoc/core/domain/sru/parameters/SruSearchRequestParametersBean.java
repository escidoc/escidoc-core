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

/**
 * Bean implementation for JAX-RS @QueryParam annotation usage on JAX-RS interfaces. <br/>
 * <br/>
 * Example:<br/>
 * <br/>
 * <code>public method interfaceMethod(@QueryParam("") SruSearchRequestParametersBean) {...}
 * </code><br/>
 * <br/>
 * This will map all query parameters to this bean, if and only if the query parameter name fits to one of the setter
 * method name.<br/>
 * <br/>
 * Example: The query parameter <i>operation</i> requires the existence of the setter method <i>setOperation</i>.
 * 
 * @author MIH
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
public class SruSearchRequestParametersBean {

    private String operation = "searchRetrieve"; // default behavior

    private String version;

    private String query;

    private String startRecord = "1"; // default behavior

    // @XmlSchemaType(name = "nonNegativeInteger")
    private String maximumRecords;

    private String recordPacking = "xml"; // default behavior

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

    public SruSearchRequestParametersBean() {
    }

    public SruSearchRequestParametersBean(final String operation, final String version, final String query,
        final String startRecord, final String maximumRecords, final String recordPacking, final String recordSchema,
        final String recordXPath, final String resultSetTTL, final String sortKeys, final String stylesheet,
        final String scanClause, final String responsePosition, final String maximumTerms) {
        if (operation != null) {
            this.operation = operation;
        }
        if (version != null) {
            this.version = version;
        }
        if (query != null) {
            this.query = query;
        }
        if (startRecord != null) {
            this.startRecord = startRecord;
        }
        if (maximumRecords != null) {
            this.maximumRecords = maximumRecords;
        }
        if (recordPacking != null) {
            this.recordPacking = recordPacking;
        }
        if (recordSchema != null) {
            this.recordSchema = recordSchema;
        }
        if (recordXPath != null) {
            this.recordXPath = recordXPath;
        }
        if (resultSetTTL != null) {
            this.resultSetTTL = resultSetTTL;
        }
        if (sortKeys != null) {
            this.sortKeys = sortKeys;
        }
        if (stylesheet != null) {
            this.stylesheet = stylesheet;
        }
        if (scanClause != null) {
            this.scanClause = scanClause;
        }
        if (responsePosition != null) {
            this.responsePosition = responsePosition;
        }
        if (maximumTerms != null) {
            this.maximumTerms = maximumTerms;
        }
    }

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

}