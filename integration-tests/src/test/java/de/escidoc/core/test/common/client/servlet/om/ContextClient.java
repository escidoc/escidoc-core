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
package de.escidoc.core.test.common.client.servlet.om;

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.interfaces.ContextHandlerClientInterface;

import java.util.Map;

/**
 * Offers access methods to the escidoc interfaces of the context resource.
 *
 * @author Michael Schneider
 */
public class ContextClient extends ClientBase implements ContextHandlerClientInterface {

    /**
     * Retrieve the xml representation of all contexts matching the filter criteria.
     *
     * @param filter The filters to select the contexts.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveContexts(final Map<String, String[]> filter) throws Exception {

        return callEsciDoc("Context.retrieveContexts", METHOD_RETRIEVE_CONTEXTS, Constants.HTTP_METHOD_GET,
            Constants.CONTEXTS_BASE_URI, new String[] {}, filter);
    }

    /**
     * Retrieve the xml representation of all members of the context matching the filter criteria.
     *
     * @param id     The id of the context.
     * @param filter The filters to select the members.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveMembers(final String id, final Map<String, String[]> filter) throws Exception {

        return callEsciDoc("Context.retrieveMembers", METHOD_RETRIEVE_MEMBERS, Constants.HTTP_METHOD_GET,
            Constants.CONTEXT_BASE_URI, new String[] { id,
                Constants.SUB_RESOURCES + "/" + Constants.SUB_CONTAINER_MEMBERS }, filter);
    }

    /**
     * Retrieve the xml representation of all virtual resources of the context matching the filter criteria.
     *
     * @param id The id of the context.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveResources(final String id) throws Exception {

        return callEsciDoc("Context.retrieveResources", METHOD_RETRIEVE_RESOURCES, Constants.HTTP_METHOD_GET,
            Constants.CONTEXT_BASE_URI, new String[] { id, Constants.SUB_RESOURCES });
    }

    /**
     * Retrieve the xml representation of all admin-descriptors of the context.
     *
     * @param id The id of the context.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveAdminDescriptors(final String id) throws Exception {

        return callEsciDoc("Context.retrieveAdminDescriptors", METHOD_RETRIEVE_ADMIN_DESCRIPTORS,
            Constants.HTTP_METHOD_GET, Constants.CONTEXT_BASE_URI, new String[] { id, Constants.SUB_ADMINDESCRIPTORS });
    }

    /**
     * Retrieve the xml representation of admin descriptor of a context.
     *
     * @param id              The id of the context.
     * @param admDescriptorId The name of the admin-descriptor.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveAdminDescriptor(final String id, final String admDescriptorId) throws Exception {

        return callEsciDoc("Context.retrieveAdminDescriptor", METHOD_RETRIEVE_ADMINDESCRIPTOR,
            Constants.HTTP_METHOD_GET, Constants.CONTEXT_BASE_URI, new String[] { id, Constants.SUB_ADMINDESCRIPTOR,
                admDescriptorId });
    }

    /**
     * Open a Context.
     *
     * @param id        The id of the context.
     * @param taskParam The task parameters including the last-modification-date.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object open(final String id, final String taskParam) throws Exception {

        return callEsciDoc("Context.retrieveMembers", METHOD_OPEN, Constants.HTTP_METHOD_POST,
            Constants.CONTEXT_BASE_URI, new String[] { id, Constants.SUB_CONTEXT_OPEN }, taskParam);
    }

    /**
     * Close a Context.
     *
     * @param id        The id of the context.
     * @param taskParam The task parameters including the last-modification-date.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object close(final String id, final String taskParam) throws Exception {

        return callEsciDoc("Context.retrieveMembers", METHOD_CLOSE, Constants.HTTP_METHOD_POST,
            Constants.CONTEXT_BASE_URI, new String[] { id, Constants.SUB_CLOSE }, taskParam);
    }

    /**
     * Retrieve the xml representation of a context.
     *
     * @param id The id of the context.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieve(final String id) throws Exception {

        return callEsciDoc("Context.retrieve", METHOD_RETRIEVE, Constants.HTTP_METHOD_GET, Constants.CONTEXT_BASE_URI,
            new String[] { id });
    }

    /**
     * Retrieve the xml representation of the properties of a resource.
     *
     * @param id The resource id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveProperties(final String id) throws Exception {

        return callEsciDoc("Context.retrieveProperties", METHOD_RETRIEVE_PROPERTIES, Constants.HTTP_METHOD_GET,
            Constants.CONTEXT_BASE_URI, new String[] { id, Constants.SUB_PROPERTIES });
    }

    /**
     * Create a context in the escidoc framework.
     *
     * @param contextXml The xml representation of the context.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object create(final Object contextXml) throws Exception {

        return callEsciDoc("Context.create", METHOD_CREATE, Constants.HTTP_METHOD_PUT, Constants.CONTEXT_BASE_URI,
            new String[] {}, changeToString(contextXml));
    }

    /**
     * Update a context in the escidoc framework.
     *
     * @param id         The id of the context.
     * @param contextXml The xml representation of the context.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object update(final String id, final Object contextXml) throws Exception {

        return callEsciDoc("Context.update", METHOD_UPDATE, Constants.HTTP_METHOD_PUT, Constants.CONTEXT_BASE_URI,
            new String[] { id }, changeToString(contextXml));
    }

    /**
     * Delete a context from the escidoc framework.
     *
     * @param id The id of the context.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object delete(final String id) throws Exception {

        return callEsciDoc("Context.delete", METHOD_DELETE, Constants.HTTP_METHOD_DELETE, Constants.CONTEXT_BASE_URI,
            new String[] { id });
    }

}
