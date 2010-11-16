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
 * Copyright 2006-2010 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.business.filter;

import java.util.Map;

/**
 * This class is a value object for all parameters used in an SRU request with
 * Lucene as back end.
 * 
 * @author SCHE
 */
public class LuceneRequestParameters extends SRURequestParameters {
    // Do not set a default search limit on client side.
    public static final int DEFAULT_LIMIT = -1;

    // Lucene starts counting from 1
    public static final int DEFAULT_OFFSET = 1;

    /**
     * Create a new parameters object from the given map.
     * 
     * @param parameters
     *            map map containing the CQL request parameters
     */
    public LuceneRequestParameters(final Map<String, String[]> parameters) {
        super(parameters);
    }

    /**
     * Get the default search limit.
     * 
     * @return default search limit
     */
    protected int getDefaultLimit() {
        return DEFAULT_LIMIT;
    }

    /**
     * Get the default search offset.
     * 
     * @return default search offset
     */
    protected int getDefaultOffset() {
        return DEFAULT_OFFSET;
    }
}
