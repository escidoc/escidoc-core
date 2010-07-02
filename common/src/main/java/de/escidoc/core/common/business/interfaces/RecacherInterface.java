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
 * Copyright 2006-2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.business.interfaces;

import java.io.IOException;
import java.util.Collection;

import javax.jms.JMSException;

import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Interface for the recacher object.
 *
 * @author sche
 */
public interface RecacherInterface {

    /**
     * Delete a resource from the resource cache.
     *
     * @param id resource id
     *
     * @throws SystemException Thrown if eSciDoc failed to delete a resource.
     */
    void deleteResource(final String id) throws SystemException;

    /**
     * Get the current status of the running/finished recaching process.
     * 
     * @return current status (how many objects are still in the queue)
     * @throws SystemException thrown in case of an internal error
     */
    String getStatus() throws SystemException;

    /**
     * Put the given list of resource ids into the message queue.
     *
     * @param type resource type
     * @param ids list of resource ids
     *
     * @return the list of resource ids
     * @throws IOException Thrown if some properties could not be read from
     *                     configuration.
     * @throws JMSException Thrown if the connection to the message queue could
     *                      not be established.
     */
    Collection<String> queueIds(
        final ResourceType type, final Collection<String> ids)
        throws IOException, JMSException;

    /**
     * Start recaching or return the current status of a running recaching process.
     *
     * @param clearRepository clear the repository before adding objects to it
     *
     * @return current status of a running/finished recaching process
     *
     * @throws SystemException Thrown if eSciDoc failed to queue a resource.
     */
    String recache(final boolean clearRepository) throws SystemException;
}