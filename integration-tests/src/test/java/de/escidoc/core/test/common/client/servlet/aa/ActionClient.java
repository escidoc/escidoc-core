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
package de.escidoc.core.test.common.client.servlet.aa;

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.interfaces.ResourceHandlerClientInterface;

/**
 * Offers access methods to the escidoc interface of the action resource.
 *
 * @author Torsten Tetteroo
 */
public class ActionClient extends ClientBase implements ResourceHandlerClientInterface {

    /**
     * Retrieve the xml representation of the resources of a action.
     *
     * @param id The action id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveResources(final String id) throws Exception {
        return callEsciDoc("Action.retrieveResources", METHOD_RETRIEVE_RESOURCES, Constants.HTTP_METHOD_GET,
            Constants.UNSECURED_ACTIONS_BASE_URI, new String[] { id, "resources" });
    }

    /**
     * Creates/Updates the unsecured actions list related to a context.
     *
     * @param contextId The id of the context
     * @param actions   The xml representation of the list of unsecured actions.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object createUnsecuredActions(final String contextId, final String actions) throws Exception {

        return callEsciDoc("Action.createUnsecuredActions", METHOD_CREATE_UNSECURED_ACTIONS, Constants.HTTP_METHOD_PUT,
            Constants.UNSECURED_ACTIONS_BASE_URI, new String[] { contextId }, changeToString(actions));
    }

    /**
     * Deletes the unsecured actions list related to a context.
     *
     * @param contextId The id of the context
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object deleteUnsecuredActions(final String contextId) throws Exception {

        return callEsciDoc("Action.deleteUnsecuredActions", METHOD_DELETE_UNSECURED_ACTIONS,
            Constants.HTTP_METHOD_DELETE, Constants.UNSECURED_ACTIONS_BASE_URI, new String[] { contextId });
    }

    /**
     * Retrieves the unsecured actions list related to a context.
     *
     * @param contextId The id of the context
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveUnsecuredActions(final String contextId) throws Exception {

        return callEsciDoc("Action.retrieveUnsecuredActions", METHOD_RETRIEVE_UNSECURED_ACTIONS,
            Constants.HTTP_METHOD_GET, Constants.UNSECURED_ACTIONS_BASE_URI, new String[] { contextId });
    }
}
