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
 * Copyright 2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.business.fedora.resources;

import java.io.IOException;

import javax.sql.DataSource;

import org.springframework.jmx.export.annotation.ManagedResource;

import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * @spring.bean id="contentModel.DbContentModelCache"
 * @author Andr&eacute; Schenk
 */
@ManagedResource(objectName = "eSciDocCore:name=DbContentModelCache", description = "content model cache", log = true, logFile = "jmx.log", currencyTimeLimit = 15)
public class DbContentModelCache extends DbResourceCache {
    /**
     * SQL statements.
     */
    private static final String DELETE_CONTENT_MODEL =
        "DELETE FROM list.content_model WHERE id = ?";

    private static final String INSERT_CONTENT_MODEL =
        "INSERT INTO list.content_model (id, rest_content, soap_content) VALUES "
            + "(?, ?, ?)";

    /**
     * Create a new Content Model cache object.
     * 
     * @throws IOException
     *             Thrown if reading the configuration failed.
     * @throws WebserverSystemException
     *             If an error occurs accessing the eSciDoc configuration.
     */
    public DbContentModelCache() throws IOException, WebserverSystemException {
        resourceType = ResourceType.CONTENT_MODEL;
    }

    /**
     * Delete a Content Model from the database cache.
     * 
     * @param id
     *            Content Model id
     */
    protected void deleteResource(final String id) {
        getJdbcTemplate().update(DELETE_CONTENT_MODEL, new Object[] { id });
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
     * Store the Content Model in the database cache.
     * 
     * @param id
     *            Content Model id
     * @param restXml
     *            complete Content Model as REST XML
     * @param soapXml
     *            complete Content Model as SOAP XML
     * 
     * @throws SystemException
     *             A date string cannot be parsed.
     */
    protected void storeResource(
        final String id, final String restXml, final String soapXml)
        throws SystemException {
        getJdbcTemplate().update(INSERT_CONTENT_MODEL,
            new Object[] { id, restXml, soapXml });
    }
}
