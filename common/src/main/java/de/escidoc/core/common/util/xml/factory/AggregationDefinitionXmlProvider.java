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
 * AggregationDefinition renderer implementation using the velocity template engine.
 *
 * @author Michael Hoppe
 */
public final class AggregationDefinitionXmlProvider extends InfrastructureXmlProvider {

    private static final String AGGREGATION_DEFINITION_RESOURCE_NAME = "aggregation-definition";

    private static final String AGGREGATION_DEFINITIONS_RESOURCE_NAME = "aggregation-definition-list";

    private static final String AGGREGATION_DEFINITIONS_SRW_RESOURCE_NAME = "aggregation-definition-srw-list";

    private static final String AGGREGATION_DEFINITION_PATH = "/aggregation-definition";

    private static final AggregationDefinitionXmlProvider PROVIDER = new AggregationDefinitionXmlProvider();

    /**
     * Private constructor to prevent initialization.
     */
    private AggregationDefinitionXmlProvider() {
    }

    /**
     * Gets the AggregationDefinition xml PROVIDER.
     *
     * @return Returns the {@code AggregationDefinitionXmlProvider} object.
     */
    public static AggregationDefinitionXmlProvider getInstance() {
        return PROVIDER;
    }

    /**
     * Gets the AggregationDefinition xml.
     *
     * @param values rendering values
     * @return String rendered xml
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getAggregationDefinitionXml(final Map values) throws WebserverSystemException {

        return getXml(AGGREGATION_DEFINITION_RESOURCE_NAME, AGGREGATION_DEFINITION_PATH, values);
    }

    /**
     * Gets the AggregationDefinitions xml.
     *
     * @param values rendering values
     * @return String rendered xml
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getAggregationDefinitionsXml(final Map values) throws WebserverSystemException {

        return getXml(AGGREGATION_DEFINITIONS_RESOURCE_NAME, AGGREGATION_DEFINITION_PATH, values);
    }

    /**
     * Gets the AggregationDefinitions xml in srw-schema.
     *
     * @param values rendering values
     * @return String rendered xml
     * @throws WebserverSystemException Thrown if the instance cannot be created due to an internal error.
     */
    public String getAggregationDefinitionsSrwXml(final Map values) throws WebserverSystemException {

        return getXml(AGGREGATION_DEFINITIONS_SRW_RESOURCE_NAME, AGGREGATION_DEFINITION_PATH, values);
    }

}
