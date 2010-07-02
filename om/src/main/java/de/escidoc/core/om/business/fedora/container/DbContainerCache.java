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
package de.escidoc.core.om.business.fedora.container;

import java.io.IOException;

import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.om.business.fedora.resources.DbOmResourceCache;

import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @spring.bean id="container.DbContainerCache"
 * @author Andr&eacute; Schenk
 * @om
 */
@ManagedResource(objectName = "eSciDocCore:name=DbContainerCache", description = "container cache", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class DbContainerCache extends DbOmResourceCache {
    /**
     * SQL statements.
     */
    private static final String DELETE_CONTAINER =
        "DELETE FROM list.container WHERE id = ?";

    private static final String INSERT_CONTAINER =
        "INSERT INTO list.container (id, rest_content, soap_content) VALUES "
        + "(?, ?, ?)";

    /**
     * Create a new container cache object.
     *
     * @throws IOException Thrown if reading the configuration failed.
     * @throws WebserverSystemException
     *             If an error occurs accessing the escidoc configuration.
     */
    public DbContainerCache() throws IOException, WebserverSystemException {
        resourceType = ResourceType.CONTAINER;
    }

    /**
     * Delete a container from the database cache.
     * 
     * @param id
     *            container id
     */
    protected void deleteResource(final String id) {
        getJdbcTemplate().update(DELETE_CONTAINER, new Object[] { id });
    }

   /**
     * Store the container in the database cache.
     * 
     * @param id
     *            container id
     * @param restXml
     *            complete container as REST XML
     * @param soapXml
     *            complete container as SOAP XML
     * 
     * @throws SystemException
     *             A date string cannot be parsed.
     */
    protected void storeResource(
        final String id, final String restXml, final String soapXml)
        throws SystemException {
        getJdbcTemplate().update(
            INSERT_CONTAINER, new Object[] {id, restXml, soapXml});
    }
}
