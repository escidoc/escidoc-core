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

import java.io.IOException;
import java.util.Map;

import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.logger.AppLogger;

/**
 * This class is a value object for all parameters used in an SRU request with a
 * relational database as back end.
 * 
 * @author SCHE
 */
public class DbRequestParameters extends SRURequestParameters {
    public static final int DEFAULT_MAXIMUM_RECORDS = 20;

    // SQL starts counting from 0
    public static final int DEFAULT_START_RECORD = 0;

    /**
     * Logging goes there.
     */
    private static final AppLogger LOG = new AppLogger(
        DbRequestParameters.class.getName());

    private int defaultMaximumRecords = DEFAULT_MAXIMUM_RECORDS;

    /**
     * Create a new parameters object from the given map.
     * 
     * @param parameters
     *            map map containing the CQL request parameters
     */
    public DbRequestParameters(final Map<String, String[]> parameters) {
        super(parameters);
        try {
            defaultMaximumRecords =
                (int) EscidocConfiguration
                    .getInstance()
                    .getAsLong(
                        EscidocConfiguration.ESCIDOC_CORE_FILTER_DEFAULT_MAXIMUM_RECORDS);
        }
        catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
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
