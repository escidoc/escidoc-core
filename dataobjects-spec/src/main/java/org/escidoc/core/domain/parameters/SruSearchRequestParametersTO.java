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
package org.escidoc.core.domain.parameters;

/**
 * @author MIH
 *
 */
public class SruSearchRequestParametersTO {
    
    private String operation;
    private String version;
    private String query;
    private String startRecord;
    private String maximumRecords;
    private String recordPacking;
    private String recordSchema;
    private String stylesheet;
    private String xinfo5roleId;
    private String xinfo5userId;
    private String xinfo5omitHighlighting;

    /**
     * @return the xinfo5roleId
     */
    public String getXinfo5roleId() {
        return xinfo5roleId;
    }
    /**
     * @param xinfo5roleId the xinfo5roleId to set
     */
    public void setXinfo5roleId(String xinfo5roleId) {
        this.xinfo5roleId = xinfo5roleId;
    }
    /**
     * @return the xinfo5userId
     */
    public String getXinfo5userId() {
        return xinfo5userId;
    }
    /**
     * @param xinfo5userId the xinfo5userId to set
     */
    public void setXinfo5userId(String xinfo5userId) {
        this.xinfo5userId = xinfo5userId;
    }
    /**
     * @return the xinfo5omitHighlighting
     */
    public String getXinfo5omitHighlighting() {
        return xinfo5omitHighlighting;
    }
    /**
     * @param xinfo5omitHighlighting the xinfo5omitHighlighting to set
     */
    public void setXinfo5omitHighlighting(String xinfo5omitHighlighting) {
        this.xinfo5omitHighlighting = xinfo5omitHighlighting;
    }

    /**
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }
    /**
     * @param operation the operation to set
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }
    /**
     * @return the startRecord
     */
    public String getStartRecord() {
        return startRecord;
    }
    /**
     * @param startRecord the startRecord to set
     */
    public void setStartRecord(String startRecord) {
        this.startRecord = startRecord;
    }
    /**
     * @return the maximumRecords
     */
    public String getMaximumRecords() {
        return maximumRecords;
    }
    /**
     * @param maximumRecords the maximumRecords to set
     */
    public void setMaximumRecords(String maximumRecords) {
        this.maximumRecords = maximumRecords;
    }
    /**
     * @return the recordPacking
     */
    public String getRecordPacking() {
        return recordPacking;
    }
    /**
     * @param recordPacking the recordPacking to set
     */
    public void setRecordPacking(String recordPacking) {
        this.recordPacking = recordPacking;
    }
    /**
     * @return the recordSchema
     */
    public String getRecordSchema() {
        return recordSchema;
    }
    /**
     * @param recordSchema the recordSchema to set
     */
    public void setRecordSchema(String recordSchema) {
        this.recordSchema = recordSchema;
    }
    /**
     * @return the stylesheet
     */
    public String getStylesheet() {
        return stylesheet;
    }
    /**
     * @param stylesheet the stylesheet to set
     */
    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }
    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }
    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }
    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }
    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }
    

}
