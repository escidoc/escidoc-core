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

package de.escidoc.core.common.util.xml.factory;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.util.Map;

/**
 * ReportDefinition renderer implementation using the velocity template engine.
 *
 * @author Michael Hoppe
 */
public final class ReportDefinitionXmlProvider extends InfrastructureXmlProvider {

    private static final String REPORT_DEFINITION_RESOURCE_NAME = "report-definition";

    private static final String REPORT_DEFINITIONS_RESOURCE_NAME = "report-definition-list";

    private static final String REPORT_DEFINITIONS_SRW_RESOURCE_NAME = "report-definition-srw-list";

    private static final String REPORT_DEFINITION_PATH = "/report-definition";

    private static final ReportDefinitionXmlProvider PROVIDER = new ReportDefinitionXmlProvider();

    /**
     * Private constructor to prevent initialization.
     */
    private ReportDefinitionXmlProvider() {
    }

    /**
     * Gets the ReportDefinition xml provider.
     *
     * @return Returns the {@code ReportDefinitionXmlProvider} object.
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public static ReportDefinitionXmlProvider getInstance() {
        return PROVIDER;
    }

    /**
     * Gets the ReportDefinition xml.
     *
     * @param values rendering values
     * @return String rendered xml
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getReportDefinitionXml(final Map values) throws WebserverSystemException {

        return getXml(REPORT_DEFINITION_RESOURCE_NAME, REPORT_DEFINITION_PATH, values);
    }

    /**
     * Gets the ReportDefinitions xml.
     *
     * @param values rendering values
     * @return String rendered xml
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getReportDefinitionsXml(final Map values) throws WebserverSystemException {

        return getXml(REPORT_DEFINITIONS_RESOURCE_NAME, REPORT_DEFINITION_PATH, values);
    }

    /**
     * Gets the ReportDefinitions xml in srw-schema.
     *
     * @param values rendering values
     * @return String rendered xml
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getReportDefinitionsSrwXml(final Map values) throws WebserverSystemException {

        return getXml(REPORT_DEFINITIONS_SRW_RESOURCE_NAME, REPORT_DEFINITION_PATH, values);
    }

}
