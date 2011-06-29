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

import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * This class is a value object for all parameters used in an SRU request with a relational database as back end.
 *
 * @author André Schenk
 */
public class DbRequestParameters extends SRURequestParameters {

    private static final int DEFAULT_MAXIMUM_RECORDS = 20;

    // SQL starts counting from 0
    private static final int DEFAULT_START_RECORD = 0;

    /**
     * Logging goes there.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DbRequestParameters.class);

    private static int defaultMaximumRecords = DEFAULT_MAXIMUM_RECORDS;

    static {
        try {
            defaultMaximumRecords =
                EscidocConfiguration.getInstance().getAsInt(
                    EscidocConfiguration.ESCIDOC_CORE_FILTER_DEFAULT_MAXIMUM_RECORDS);
        }
        catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Create a new parameters object from the given map.
     *
     * @param parameters map map containing the CQL request parameters
     */
    public DbRequestParameters(final Map<String, String[]> parameters) {
        super(parameters);
    }

    /**
     * Get the default maximum records value for search.
     *
     * @return default maximum records value for search
     */
    @Override
    protected int getDefaultMaximumRecords() {
        return defaultMaximumRecords;
    }

    /**
     * Get the default start record for search.
     *
     * @return default start record for search
     */
    @Override
    protected int getDefaultStartRecord() {
        return DEFAULT_START_RECORD;
    }
}
