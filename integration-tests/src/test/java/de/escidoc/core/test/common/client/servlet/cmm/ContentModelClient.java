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
package de.escidoc.core.test.common.client.servlet.cmm;

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;

import java.util.Map;

/**
 * Offers access to the content type handler methods.
 *
 * @author Torsten Tetteroo
 */
public class ContentModelClient extends ClientBase {

    /**
     * Retrieve the xml representation of a content model.
     *
     * @param id The id of the container.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    @Override
    public Object retrieve(final String id) throws Exception {

        return callEsciDoc("ContentModel.retrieve", METHOD_RETRIEVE, Constants.HTTP_METHOD_GET,
            Constants.CONTENT_MODEL_BASE_URI, new String[] { id });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.test.common.client.servlet.ClientBase#create(java.lang
     * .Object)
     */
    @Override
    public Object create(final Object xml) throws Exception {

        return callEsciDoc("ContentModel.create", METHOD_CREATE, Constants.HTTP_METHOD_PUT,
            Constants.CONTENT_MODEL_BASE_URI, new String[] {}, xml);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.test.common.client.servlet.ClientBase#update(java.lang
     * .String, java.lang.Object)
     */
    @Override
    public Object update(final String id, final Object xml) throws Exception {

        return callEsciDoc("ContentModel.update", METHOD_UPDATE, Constants.HTTP_METHOD_PUT,
            Constants.CONTENT_MODEL_BASE_URI, new String[] { id }, xml);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.test.common.client.servlet.ClientBase#delete(java.lang
     * .String)
     */
    @Override
    public Object delete(final String id) throws Exception {

        return callEsciDoc("ContentModel.delete", METHOD_DELETE, Constants.HTTP_METHOD_DELETE,
            Constants.CONTENT_MODEL_BASE_URI, new String[] { id });
    }

    /**
     * Retrieve content models.
     *
     * @param filter The filter param.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveContentModels(final Map<String, String[]> filter) throws Exception {
        return callEsciDoc("ContentModel.retrieveContentModels", METHOD_RETRIEVE_CONTENT_MODELS,
            Constants.HTTP_METHOD_GET, Constants.CONTENT_MODELS_BASE_URI, new String[] {}, filter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.escidoc.core.test.common.client.servlet.ClientBase#retrieveResources
     * (java.lang.String)
     */
    public Object retrieveResources(final String id) throws Exception {

        return callEsciDoc("ContentModel.retrieveResources", METHOD_RETRIEVE_RESOURCES, Constants.HTTP_METHOD_GET,
            Constants.CONTENT_MODEL_BASE_URI, new String[] { id, "resources" });
    }

    public Object retrieveProperties(final String id) throws Exception {

        return callEsciDoc("ContentModel.retrieveProperties", METHOD_RETRIEVE_PROPERTIES, Constants.HTTP_METHOD_GET,
            Constants.CONTENT_MODEL_BASE_URI, new String[] { id, "properties" });
    }

    public Object retrieveContentStreams(final String id) throws Exception {

        return callEsciDoc("ContentModel.retrieveContentStreams", METHOD_RETRIEVE_CONTENT_STREAMS,
            Constants.HTTP_METHOD_GET, Constants.CONTENT_MODEL_BASE_URI, new String[] { id, "content-streams" });
    }

    public Object retrieveContentStream(final String id, final String name) throws Exception {

        return callEsciDoc("ContentModel.retrieveContentStream", METHOD_RETRIEVE_CONTENT_STREAM,
            Constants.HTTP_METHOD_GET, Constants.CONTENT_MODEL_BASE_URI, new String[] { id,
                "content-streams/content-stream", name });
    }

    /**
     * Retrieve the history of an Content Model.
     *
     * @param id The id of the Content Model.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    public Object retrieveVersionHistory(final String id) throws Exception {

        return callEsciDoc("ContentModel.retrieveVersionHistory", METHOD_RETRIEVE_VERSION_HISTORY,
            Constants.HTTP_METHOD_GET, Constants.CONTENT_MODEL_BASE_URI, new String[] { id,
                Constants.SUB_VERSION_HISTORY });
    }

}
