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
 * Copyright 2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.business.filter;

import java.util.Map;

import de.escidoc.core.common.business.Constants;

/**
 * This class is a value object for all parameters used in an SRU request.
 * 
 * @author SCHE
 */
public class SRURequest {
    private static final int DEFAULT_LIMIT = 1000;

    // CQL starts counting from 1
    private static final int DEFAULT_OFFSET = 1;

    public final String query;

    public final int limit;

    public final int offset;

    public final boolean explain;

    /**
     * Create a new CQLQuery object from the given map.
     * 
     * @param parameters
     *            map map containing the CQL request parameters
     */
    public SRURequest(final Map<String, String[]> parameters) {
        query =
            getStringParameter(parameters.get(Constants.SRU_PARAMETER_QUERY));
        limit =
            getIntParameter(parameters
                .get(Constants.SRU_PARAMETER_MAXIMUM_RECORDS), DEFAULT_LIMIT);
        offset =
            getIntParameter(parameters
                .get(Constants.SRU_PARAMETER_START_RECORD), DEFAULT_OFFSET) - 1;

        final String operation =
            getStringParameter(parameters
                .get(Constants.SRU_PARAMETER_OPERATION));

        explain =
            (getStringParameter(parameters.get(
                Constants.SRU_PARAMETER_EXPLAIN)) != null)
                || (Constants.SRU_PARAMETER_EXPLAIN.equalsIgnoreCase(operation));
    }

    /**
     * Get the first parameter from the given array and convert it into an
     * integer value. If the array is empty the default value is returned
     * instead.
     * 
     * @param parameter
     *            array containing the parameter to be extracted
     * @param defaultValue
     *            default value
     * 
     * @return first value from the given array as integer or the default value
     */
    private int getIntParameter(final Object[] parameter, final int defaultValue) {
        int result = defaultValue;

        if ((parameter != null) && (parameter.length > 0)) {
            result = Integer.parseInt(parameter[0].toString());
        }
        return result;
    }

    /**
     * Get the first parameter from the given array.
     * 
     * @param parameter
     *            array containing the parameter to be extracted
     * 
     * @return first value from the given array or null
     */
    private String getStringParameter(final Object[] parameter) {
        String result = null;

        if ((parameter != null) && (parameter.length > 0)) {
            result = parameter[0].toString();
        }
        return result;
    }
}
