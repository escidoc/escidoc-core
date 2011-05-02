/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.business.filter;

import de.escidoc.core.common.business.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a value object for all parameters used in an SRU request.
 *
 * @author André Schenk
 */
public abstract class SRURequestParameters {

    private final String query;

    private final int maximumRecords;

    private final int startRecord;

    private final boolean explain;

    private Map<String, String> extraData;

    private RecordPacking recordPacking;

    /**
     * Create a new parameters object from the given map.
     *
     * @param parameters map map containing the CQL request parameters
     */
    protected SRURequestParameters(final Map<String, String[]> parameters) {
        this.query = getStringParameter(parameters.get(Constants.SRU_PARAMETER_QUERY));
        this.maximumRecords =
            getIntParameter(parameters.get(Constants.SRU_PARAMETER_MAXIMUM_RECORDS), getDefaultMaximumRecords());
        final int givenStartRecord = getIntParameter(parameters.get(Constants.SRU_PARAMETER_START_RECORD), -1);
        this.startRecord =
            givenStartRecord > -1 ? givenStartRecord + getDefaultStartRecord() - 1 : getDefaultStartRecord();
        putExtraDataParameter(Constants.SRU_PARAMETER_USER, parameters.get(Constants.SRU_PARAMETER_USER));
        putExtraDataParameter(Constants.SRU_PARAMETER_ROLE, parameters.get(Constants.SRU_PARAMETER_ROLE));
        putExtraDataParameter(Constants.SRU_PARAMETER_OMIT_HIGHLIGHTING, parameters
            .get(Constants.SRU_PARAMETER_OMIT_HIGHLIGHTING));

        final String operation = getStringParameter(parameters.get(Constants.SRU_PARAMETER_OPERATION));

        this.explain =
            getStringParameter(parameters.get(Constants.SRU_PARAMETER_EXPLAIN)) != null
                || Constants.SRU_PARAMETER_EXPLAIN.equalsIgnoreCase(operation);
        this.recordPacking =
            RecordPacking.fromType(getStringParameter(parameters.get(Constants.SRU_PARAMETER_RECORD_PACKING), "xml"));
        if (this.recordPacking == null) {
            this.recordPacking = RecordPacking.XML;
        }
    }

    public String getQuery() {
        return this.query;
    }

    public int getMaximumRecords() {
        return this.maximumRecords;
    }

    public int getStartRecord() {
        return this.startRecord;
    }

    public boolean isExplain() {
        return this.explain;
    }

    public final RecordPacking getRecordPacking() {
        return this.recordPacking;
    }

    public Map<String, String> getExtraData() {
        return this.extraData;
    }

    /**
     * Get the default maximum records value for search.
     *
     * @return default maximum records value for search
     */
    protected abstract int getDefaultMaximumRecords();

    /**
     * Get the default start record for search.
     *
     * @return default start record for search
     */
    protected abstract int getDefaultStartRecord();

    /**
     * Get the first parameter from the given array and convert it into an integer value. If the array is empty the
     * default value is returned instead.
     *
     * @param parameter    array containing the parameter to be extracted
     * @param defaultValue default value
     * @return first value from the given array as integer or the default value
     */
    private static int getIntParameter(final Object[] parameter, final int defaultValue) {
        int result = defaultValue;

        if (parameter != null && parameter.length > 0) {
            result = Integer.parseInt(parameter[0].toString());
        }
        return result;
    }

    /**
     * Get the first parameter from the given array.
     *
     * @param parameter    array containing the parameter to be extracted
     * @param defaultValue default value
     * @return first value from the given array or the default value
     */
    private static String getStringParameter(final Object[] parameter, final String defaultValue) {
        String result = defaultValue;

        if (parameter != null && parameter.length > 0) {
            result = parameter[0].toString();
        }
        return result;
    }

    /**
     * Get the first parameter from the given array.
     *
     * @param parameter array containing the parameter to be extracted
     * @return first value from the given array or null
     */
    private static String getStringParameter(final Object[] parameter) {
        return getStringParameter(parameter, null);
    }

    /**
     * Get the key and the first parameter from the given array and put it in extraData HashMap.
     *
     * @param key       the key
     * @param parameter array containing the parameter to be extracted
     */
    private void putExtraDataParameter(final String key, final Object[] parameter) {
        if (key != null && parameter != null && parameter.length > 0) {
            if (this.extraData == null) {
                this.extraData = new HashMap<String, String>();
            }
            extraData.put(key, getStringParameter(parameter));
        }
    }

}
