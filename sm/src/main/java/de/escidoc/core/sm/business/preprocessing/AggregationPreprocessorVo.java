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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */

package de.escidoc.core.sm.business.preprocessing;

import de.escidoc.core.sm.business.persistence.hibernate.AggregationDefinition;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds the data for the preprocessing of one AggrgeationDefinition.
 *
 * @author Michael Hoppe
 */
public class AggregationPreprocessorVo {

    private AggregationDefinition aggregationDefinition;

    /**
     * DataHash-Structure:.
     * <p/>
     * -key: tablename, value:HashMap within this HashMap: -key: all Info+timeReductionFields as String, value:HashMap
     * within this HashMap: -key:fieldname, value:fieldValue
     */
    private Map dataHash = new HashMap();

    /**
     * differencesHash-Structure:.
     * <p/>
     * -key: tablename, value:HashMap within this HashMap: -key: all Info+timeReductionFields as String, value:HashMap
     * within this HashMap: -key:fieldname, value:fieldValue
     */
    private Map differencesHash = new HashMap();

    /**
     * fieldTypeHash-Structure:. -key: tablename, value:HashMap within this HashMap: -key: "fieldtype", value: HashMap
     * within this HashMap: -key: fieldname, value: filedtype (info, time-reduction, count-cumulation,
     * difference-cummulation -key: "dbtype", value: HashMap within this HashMap: -key: fieldname, value:fieldtype
     * (text,numeric, date)
     */
    private Map fieldTypeHash = new HashMap();

    private Map fieldHashForOneRecord = new HashMap();

    private Map differenceHashForOneRecord = new HashMap();

    private StringBuffer uniqueKeyForOneRecord = new StringBuffer("");

    /**
     * @return the aggregationDefinition
     */
    public AggregationDefinition getAggregationDefinition() {
        return this.aggregationDefinition;
    }

    /**
     * @param aggregationDefinition the aggregationDefinition to set
     */
    public void setAggregationDefinition(final AggregationDefinition aggregationDefinition) {
        this.aggregationDefinition = aggregationDefinition;
    }

    /**
     * @return the dataHash
     */
    public Map getDataHash() {
        return this.dataHash;
    }

    /**
     * @param dataHash the dataHash to set
     */
    public void setDataHash(final Map dataHash) {
        this.dataHash = dataHash;
    }

    /**
     * @return the differencesHash
     */
    public Map getDifferencesHash() {
        return this.differencesHash;
    }

    /**
     * @param differencesHash the differencesHash to set
     */
    public void setDifferencesHash(final Map differencesHash) {
        this.differencesHash = differencesHash;
    }

    /**
     * @return the fieldTypeHash
     */
    public Map getFieldTypeHash() {
        return this.fieldTypeHash;
    }

    /**
     * @param fieldTypeHash the fieldTypeHash to set
     */
    public void setFieldTypeHash(final Map fieldTypeHash) {
        this.fieldTypeHash = fieldTypeHash;
    }

    /**
     * @return the fieldHashForOneRecord
     */
    public Map getFieldHashForOneRecord() {
        return this.fieldHashForOneRecord;
    }

    /**
     * @param fieldHashForOneRecord the fieldHashForOneRecord to set
     */
    public void setFieldHashForOneRecord(final Map fieldHashForOneRecord) {
        this.fieldHashForOneRecord = fieldHashForOneRecord;
    }

    /**
     * @return the differenceHashForOneRecord
     */
    public Map getDifferenceHashForOneRecord() {
        return this.differenceHashForOneRecord;
    }

    /**
     * @param differenceHashForOneRecord the differenceHashForOneRecord to set
     */
    public void setDifferenceHashForOneRecord(final Map differenceHashForOneRecord) {
        this.differenceHashForOneRecord = differenceHashForOneRecord;
    }

    /**
     * @return the uniqueKeyForOneRecord
     */
    public StringBuffer getUniqueKeyForOneRecord() {
        return this.uniqueKeyForOneRecord;
    }

    /**
     * @param uniqueKeyForOneRecord the uniqueKeyForOneRecord to set
     */
    public void setUniqueKeyForOneRecord(final StringBuffer uniqueKeyForOneRecord) {
        this.uniqueKeyForOneRecord = uniqueKeyForOneRecord;
    }

}
