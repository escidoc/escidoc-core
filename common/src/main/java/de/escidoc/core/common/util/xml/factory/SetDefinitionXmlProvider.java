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
package de.escidoc.core.common.util.xml.factory;

import java.util.Map;

import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * XmlTemplateProvider implementation using the Velocity template engine.<br/>
 * This implementation uses the velocity singleton pattern.
 * 
 * @author sche
 * @common
 */
public final class SetDefinitionXmlProvider extends InfrastructureXmlProvider {

    private static final String SET_DEFINITION_RESOURCE_NAME = "set-definition";

    private static final String SET_DEFINITIONS_RESOURCE_NAME =
        "set-definition-list";

    private static final String SET_DEFINITIONS_SRW_RESOURCE_NAME =
        "set-definition-srw-list";

    private static final String SET_DEFINITION_PATH = "/set-definition";

    private static final String RESOURCES_PATH = SET_DEFINITION_PATH;

    private static SetDefinitionXmlProvider PROVIDER = new SetDefinitionXmlProvider();

    /**
     * Private constructor to prevent initialization.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    private SetDefinitionXmlProvider() {
    }

    /**
     * Gets the set definition xml PROVIDER.
     * 
     * @return Returns the <code>SetDefinitionXmlProvider</code> object.
     * @throws WebserverSystemException
     *             Thrown if the instance cannot be created due to an internal
     *             error.
     */
    public static SetDefinitionXmlProvider getInstance() {
        return PROVIDER;
    }

    /**
     * Get the XML representation for a set definition.
     * 
     * @param values
     *            variables to be put into the Velocity template
     * 
     * @return XML representation for the set definition
     * @throws WebserverSystemException
     *             Thrown if the instance cannot be created due to an internal
     *             error.
     */
    public String getSetDefinitionXml(final Map<String, Object> values)
        throws WebserverSystemException {

        return getXml(SET_DEFINITION_RESOURCE_NAME, SET_DEFINITION_PATH, values);
    }

    /**
     * Get the XML representation for a list of set definition.
     * 
     * @param values
     *            variables to be put into the Velocity template
     * 
     * @return XML representation for the list of set definition
     * @throws WebserverSystemException
     *             Thrown if the instance cannot be created due to an internal
     *             error.
     */
    public String getSetDefinitionsXml(final Map<String, Object> values)
        throws WebserverSystemException {

        return getXml(SET_DEFINITIONS_RESOURCE_NAME, RESOURCES_PATH, values);
    }

    /**
     * Get the XML representation for a list of set definition.
     * 
     * @param values
     *            variables to be put into the Velocity template
     * 
     * @return XML representation for the list of set definition (SRW format)
     * @throws WebserverSystemException
     *             Thrown if the instance cannot be created due to an internal
     *             error.
     */
    public String getSetDefinitionsSrwXml(final Map<String, Object> values)
        throws WebserverSystemException {

        return getXml(SET_DEFINITIONS_SRW_RESOURCE_NAME, RESOURCES_PATH, values);
    }
}
