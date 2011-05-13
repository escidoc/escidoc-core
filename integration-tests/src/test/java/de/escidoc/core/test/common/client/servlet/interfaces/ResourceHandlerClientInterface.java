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
package de.escidoc.core.test.common.client.servlet.interfaces;

/**
 * Interface of a resource handler.<br /> This interface defines methods common to all resource handlers, e.g. create.
 *
 * @author Torsten Tetteroo
 */
public interface ResourceHandlerClientInterface {

    /**
     * Create a resource.
     *
     * @param resourceXml The xml representation of the resource
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    Object create(final Object resourceXml) throws Exception;

    /**
     * Delete a resource.
     *
     * @param id The resource id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    Object delete(final String id) throws Exception;

    /**
     * Retrieve the xml representation of a resource.
     *
     * @param id The resource id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    Object retrieve(final String id) throws Exception;

    /**
     * Retrieve the xml representation of the (virtual) resources of a resource.
     *
     * @param id The resource id.
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    Object retrieveResources(final String id) throws Exception;

    /**
     * Update an user.
     *
     * @param id          The resource id.
     * @param resourceXml The xml representation of the resource
     * @return The HttpMethod after the service call .
     * @throws Exception If the service call fails.
     */
    Object update(final String id, final Object resourceXml) throws Exception;
}
