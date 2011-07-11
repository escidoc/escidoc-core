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
 * Copyright 2006-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test.common.client.servlet.sm;

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;

import java.util.Map;

/**
 * Offers access methods to the escidoc interface of the Statistic ReportDefinition resource.
 *
 * @author Michael Hoppe
 */
public class ReportDefinitionClient extends ClientBase {

    /**
     * Create an ReportDefinition in the escidoc framework.
     *
     * @param reportDefinitionXml The xml representation of the ReportDefinition.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object create(final Object reportDefinitionXml) throws Exception {

        return callEsciDoc("ReportDefinition.create", METHOD_CREATE, Constants.HTTP_METHOD_PUT,
            Constants.STATISTIC_REPORT_DEFINITION_BASE_URI, new String[] {}, changeToString(reportDefinitionXml));
    }

    /**
     * Delete an ReportDefinition from the escidoc framework.
     *
     * @param id The id of the ReportDefinition.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object delete(final String id) throws Exception {

        return callEsciDoc("ReportDefinition.delete", METHOD_DELETE, Constants.HTTP_METHOD_DELETE,
            Constants.STATISTIC_REPORT_DEFINITION_BASE_URI, new String[] { id });
    }

    /**
     * Retrieve the xml representation of an ReportDefinition.
     *
     * @param id The id of the ReportDefinition.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieve(final String id) throws Exception {

        return callEsciDoc("ReportDefinition.retrieve", METHOD_RETRIEVE, Constants.HTTP_METHOD_GET,
            Constants.STATISTIC_REPORT_DEFINITION_BASE_URI, new String[] { id });
    }

    /**
     * Retrieve the xml representation of the list of all report-definitions.
     *
     * @param filter filterXml
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveReportDefinitions(final Map<String, String[]> filter) throws Exception {

        return callEsciDoc("ReportDefinition.retrieveReportDefinitions", METHOD_RETRIEVE_REPORT_DEFINITIONS,
            Constants.HTTP_METHOD_GET, Constants.STATISTIC_REPORT_DEFINITIONS_BASE_URI, new String[] {}, filter);
    }

    /**
     * Update an ReportDefinition in the escidoc framework.
     *
     * @param id      The id of the ReportDefinition.
     * @param itemXml The xml representation of the ReportDefinition.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object update(final String id, final Object itemXml) throws Exception {

        return callEsciDoc("ReportDefinition.update", METHOD_UPDATE, Constants.HTTP_METHOD_PUT,
            Constants.STATISTIC_REPORT_DEFINITION_BASE_URI, new String[] { id }, changeToString(itemXml));
    }

}
