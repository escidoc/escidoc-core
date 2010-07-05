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
package de.escidoc.core.aa.filter;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.jmx.export.annotation.ManagedResource;

import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * @spring.bean id="context.DbContextCache"
 * @author Andr&eacute; Schenk
 */
@ManagedResource(objectName = "eSciDocCore:name=DbContextCache", description = "context cache", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class DbContextCache extends DbResourceCache {
    /**
     * SQL statements.
     */
    private static final String DELETE_CONTEXT =
        "DELETE FROM list.context WHERE id = ?";

    private static final String INSERT_CONTEXT =
        "INSERT INTO list.context (id, rest_content, soap_content) VALUES "
        + "(?, ?, ?)";

    /**
     * Create a new context cache object.
     *
     * @throws IOException Thrown if reading the configuration failed.
     * @throws WebserverSystemException
     *             If an error occurs accessing the escidoc configuration.
     */
    public DbContextCache() throws IOException, WebserverSystemException {
        resourceType = ResourceType.CONTEXT;
    }

    /**
     * Delete a context from the database cache.
     * 
     * @param id
     *            context id
     */
    protected void deleteResource(final String id) {
        getJdbcTemplate().update(DELETE_CONTEXT, new Object[] { id });
    }

    /**
     * Injects the data source.
     *
     * @spring.property ref="escidoc-core.DataSource"
     * @param myDataSource data source from Spring
     */
    public void setMyDataSource(final DataSource myDataSource) {
        super.setDataSource(myDataSource);
    }

   /**
     * Store the context in the database cache.
     * 
     * @param id
     *            context id
     * @param restXml
     *            complete context as REST XML
     * @param soapXml
     *            complete context as SOAP XML
     * 
     * @throws SystemException
     *             A date string cannot be parsed.
     */
    protected void storeResource(
        final String id, final String restXml, final String soapXml)
        throws SystemException {
        getJdbcTemplate().update(
            INSERT_CONTEXT, new Object[] {id, restXml, soapXml});
    }
}
