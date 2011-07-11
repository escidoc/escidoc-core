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
package de.escidoc.core.test.common.client.servlet.oai;

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.interfaces.ResourceHandlerClientInterface;

import java.util.Map;

/**
 * Offers access methods to the escidoc REST interface of the setDefintion resource.
 *
 * @author Rozita Friedman
 */
public class SetDefinitionClient extends ClientBase implements ResourceHandlerClientInterface {

    /**
     * Retrieve the Containers of a Container.
     *
     * @param id The id of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieve(final String id) throws Exception {

        return callEsciDoc("SetDefinition.retrieve", METHOD_RETRIEVE, Constants.HTTP_METHOD_GET,
            Constants.SET_DEFINITION_BASE_URI, new String[] { id });
    }

    /**
     * Create an defintion set in the escidoc framework.
     *
     * @param setDefinitionXml The xml representation of the defintion set.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object create(final Object setDefinitionXml) throws Exception {

        return callEsciDoc("SetDefinition.create", METHOD_CREATE, Constants.HTTP_METHOD_PUT,
            Constants.SET_DEFINITION_BASE_URI, new String[] {}, changeToString(setDefinitionXml));
    }

    /**
     * Delete an item from the escidoc framework.
     *
     * @param id The id of the item.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object delete(final String id) throws Exception {

        return callEsciDoc("SetDefinition.delete", METHOD_DELETE, Constants.HTTP_METHOD_DELETE,
            Constants.SET_DEFINITION_BASE_URI, new String[] { id });
    }

    /**
     * Update an item in the escidoc framework.
     *
     * @param id               The id of the setDefinition.
     * @param setDefinitionXml The xml representation of the setDefintion.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object update(final String id, final Object setDefinitionXml) throws Exception {

        return callEsciDoc("SetDefinition.update", METHOD_UPDATE, Constants.HTTP_METHOD_PUT,
            Constants.SET_DEFINITION_BASE_URI, new String[] { id }, changeToString(setDefinitionXml));
    }

    /**
     * Retrieve set definitions.
     *
     * @param filter The filter query.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveSetDefinitions(final Map<String, String[]> filter) throws Exception {
        return callEsciDoc("SetDefinition.retrieveSetDefinitions", METHOD_RETRIEVE_SET_DEFINITIONS,
            Constants.HTTP_METHOD_GET, Constants.SET_DEFINITIONS_BASE_URI, new String[] {}, filter);
    }

    /**
     *
     */
    public Object retrieveResources(final String id) throws Exception {
        return null;
    }

}
