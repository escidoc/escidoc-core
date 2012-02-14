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

import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

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
@Guarded
public class SruSearchRequestParametersBean {

    public static final String DEFAULT_OPERATION = "searchRetrieve";

    public static final String DEFAULT_START_RECORD = "1";

    public static final String DEFAULT_RECORD_PACKING = "xml";

    public static final String DEFAULT_RESPONSE_POSITION = "1";

    @NotNull
    private String operation = DEFAULT_OPERATION;

    private String version;

    private String query;

    @NotNull
    private String startRecord = DEFAULT_START_RECORD;

    // @XmlSchemaType(name = "nonNegativeInteger")
    private String maximumRecords;

    @NotNull
    private String recordPacking = DEFAULT_RECORD_PACKING;

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
    private String responsePosition = DEFAULT_RESPONSE_POSITION;

    // @XmlSchemaType(name = "positiveInteger")
    private String maximumTerms;

    /**
     *
     */
    public SruSearchRequestParametersBean() {
    }

    /**
     * @return the operation
     * @see SruSearchRequestParametersBean#DEFAULT_OPERATION
     */
    @NotNull
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
     * @see SruSearchRequestParametersBean#DEFAULT_START_RECORD
     */
    @NotNull
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
     * @see SruSearchRequestParametersBean#DEFAULT_RECORD_PACKING
     */
    @NotNull
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
     * @see SruSearchRequestParametersBean#DEFAULT_RESPONSE_POSITION
     */
    @NotNull
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
        this.operation = (operation == null) ? DEFAULT_OPERATION : operation;
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
        this.startRecord = (startRecord == null) ? DEFAULT_START_RECORD : startRecord;
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
        this.recordPacking = (recordPacking == null) ? DEFAULT_RECORD_PACKING : recordPacking;
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
        this.responsePosition = (responsePosition == null) ? DEFAULT_RESPONSE_POSITION : responsePosition;
    }

    /**
     * @param maximumTerms
     *            the maximumTerms to set
     */
    public void setMaximumTerms(final String maximumTerms) {
        this.maximumTerms = maximumTerms;
    }

}