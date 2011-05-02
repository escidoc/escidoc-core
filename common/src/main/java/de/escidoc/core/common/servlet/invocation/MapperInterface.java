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

package de.escidoc.core.common.servlet.invocation;

import de.escidoc.core.common.exceptions.system.EncodingSystemException;
import de.escidoc.core.common.servlet.invocation.exceptions.MethodNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Interface for mapping a http request to a resource method.
 *
 * @author Michael Schneider
 */
public interface MapperInterface {

    /**
     * Retrieve the matching resource method from the HTTP method and the request uri.
     *
     * @param request The HTTP request.
     * @return The resource method.
     * @throws MethodNotFoundException If no matching method is found.
     * @throws EncodingSystemException e
     */
    BeanMethod getMethod(final HttpServletRequest request) throws MethodNotFoundException, EncodingSystemException;

    /**
     * Retrieve the matching resource method from the provided URI, HTTP method and the request body.
     *
     * @param uri        The request URI.
     * @param query      The request Query.
     * @param parameters The request parameters.
     * @param httpMethod The http method.
     * @param body       The body of the request, if any.
     * @return The resource method.
     * @throws MethodNotFoundException If no matching method is found.
     * @throws EncodingSystemException e
     */
    BeanMethod getMethod(
        final String uri, final String query, final Map<String, String[]> parameters, final String httpMethod,
        final Object body) throws MethodNotFoundException, EncodingSystemException;

}
