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

import java.util.HashMap;

import de.escidoc.core.sm.business.persistence.hibernate.AggregationDefinition;

/**
 * Holds the data for the preprocessing of one AggrgeationDefinition.
 * 
 * @author MIH
 * @sm
 */
public class AggregationPreprocessorVo {

    private AggregationDefinition aggregationDefinition = null;

    /**
     * DataHash-Structure:.
     * 
     * -key: tablename, value:HashMap within this HashMap: -key: all
     * Info+timeReductionFields as String, value:HashMap within this HashMap:
     * -key:fieldname, value:fieldValue
     * 
     */
    private HashMap dataHash = new HashMap();

    /**
     * differencesHash-Structure:.
     * 
     * -key: tablename, value:HashMap within this HashMap: -key: all
     * Info+timeReductionFields as String, value:HashMap within this HashMap:
     * -key:fieldname, value:fieldValue
     * 
     */
    private HashMap differencesHash = new HashMap();

    /**
     * fieldTypeHash-Structure:. -key: tablename, value:HashMap within this
     * HashMap: -key: "fieldtype", value: HashMap within this HashMap: -key:
     * fieldname, value: filedtype (info, time-reduction, count-cumulation,
     * difference-cummulation -key: "dbtype", value: HashMap within this
     * HashMap: -key: fieldname, value:fieldtype (text,numeric, date)
     */
    private HashMap fieldTypeHash = new HashMap();

    private HashMap fieldHashForOneRecord = new HashMap();

    private HashMap differenceHashForOneRecord = new HashMap();

    private StringBuffer uniqueKeyForOneRecord = new StringBuffer("");

    /**
     * @return the aggregationDefinition
     */
    public AggregationDefinition getAggregationDefinition() {
        return aggregationDefinition;
    }

    /**
     * @param aggregationDefinition the aggregationDefinition to set
     */
    public void setAggregationDefinition(
            AggregationDefinition aggregationDefinition) {
        this.aggregationDefinition = aggregationDefinition;
    }

    /**
     * @return the dataHash
     */
    public HashMap getDataHash() {
        return dataHash;
    }

    /**
     * @param dataHash the dataHash to set
     */
    public void setDataHash(final HashMap dataHash) {
        this.dataHash = dataHash;
    }

    /**
     * @return the differencesHash
     */
    public HashMap getDifferencesHash() {
        return differencesHash;
    }

    /**
     * @param differencesHash the differencesHash to set
     */
    public void setDifferencesHash(final HashMap differencesHash) {
        this.differencesHash = differencesHash;
    }

    /**
     * @return the fieldTypeHash
     */
    public HashMap getFieldTypeHash() {
        return fieldTypeHash;
    }

    /**
     * @param fieldTypeHash the fieldTypeHash to set
     */
    public void setFieldTypeHash(final HashMap fieldTypeHash) {
        this.fieldTypeHash = fieldTypeHash;
    }

    /**
     * @return the fieldHashForOneRecord
     */
    public HashMap getFieldHashForOneRecord() {
        return fieldHashForOneRecord;
    }

    /**
     * @param fieldHashForOneRecord the fieldHashForOneRecord to set
     */
    public void setFieldHashForOneRecord(final HashMap fieldHashForOneRecord) {
        this.fieldHashForOneRecord = fieldHashForOneRecord;
    }

    /**
     * @return the differenceHashForOneRecord
     */
    public HashMap getDifferenceHashForOneRecord() {
        return differenceHashForOneRecord;
    }

    /**
     * @param differenceHashForOneRecord the differenceHashForOneRecord to set
     */
    public void setDifferenceHashForOneRecord(
            final HashMap differenceHashForOneRecord) {
        this.differenceHashForOneRecord = differenceHashForOneRecord;
    }

    /**
     * @return the uniqueKeyForOneRecord
     */
    public StringBuffer getUniqueKeyForOneRecord() {
        return uniqueKeyForOneRecord;
    }

    /**
     * @param uniqueKeyForOneRecord the uniqueKeyForOneRecord to set
     */
    public void setUniqueKeyForOneRecord(
            final StringBuffer uniqueKeyForOneRecord) {
        this.uniqueKeyForOneRecord = uniqueKeyForOneRecord;
    }


}
