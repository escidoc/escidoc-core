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
package de.escidoc.core.oum.business.fedora.organizationalunit;

import java.io.IOException;

import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.oum.business.fedora.resources.DbOumResourceCache;

import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @spring.bean id="organizationalunit.DbOrganizationalUnitCache"
 * @author Andr&eacute; Schenk
 */
@ManagedResource(objectName = "eSciDocCore:name=DbOrganizationalUnitCache", description = "organizational unit cache", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class DbOrganizationalUnitCache extends DbOumResourceCache {
    /**
     * SQL statements.
     */
    private static final String DELETE_OU = "DELETE FROM list.ou WHERE id = ?";

    private static final String INSERT_OU =
        "INSERT INTO list.ou (id, rest_content, soap_content) VALUES (?, ?, ?)";

    /**
     * Create a new organizational unit cache object.
     * 
     * @throws IOException
     *             Thrown if reading the configuration failed.
     * @throws WebserverSystemException
     *             If an error occurs accessing the escidoc configuration.
     */
    public DbOrganizationalUnitCache() throws IOException,
        WebserverSystemException {
        resourceType = ResourceType.OU;
    }

    /**
     * Delete an organizational unit from the database cache.
     * 
     * @param id
     *            organizational unit id
     */
    protected void deleteResource(final String id) {
        getJdbcTemplate().update(DELETE_OU, new Object[] { id });
    }

    /**
     * Store the organizational unit in the database cache.
     * 
     * @param id
     *            organizational unit id
     * @param restXml
     *            complete organizational unit as REST XML
     * @param soapXml
     *            complete organizational unit as SOAP XML
     * 
     * @throws SystemException
     *             A date string cannot be parsed.
     */
    protected void storeResource(
        final String id, final String restXml, final String soapXml)
        throws SystemException {
        getJdbcTemplate().update(INSERT_OU,
            new Object[] { id, restXml, soapXml });
    }
}
