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
package de.escidoc.core.oum.business.fedora.resources;

import java.io.IOException;

import javax.sql.DataSource;

import de.escidoc.core.common.business.LockHandler;
import de.escidoc.core.common.business.fedora.resources.AccessRights;
import de.escidoc.core.common.business.fedora.resources.DbResourceCache;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.BeanLocator;

/**
 * Abstract super class for the organizational unit resource cache.
 *
 * @author Andr&eacute; Schenk
 * @oum
 */
public abstract class DbOumResourceCache extends DbResourceCache {

    /**
     * Constructor.
     *
     * @throws IOException Thrown if reading the configuration failed.
     * @throws WebserverSystemException
     *             If an error occurs accessing the escidoc configuration.
     */
    public DbOumResourceCache() throws IOException, WebserverSystemException {
    }

    /**
     * Injects the access rights object.
     * 
     * @spring.property ref="resource.DbAccessRights"
     * @param accessRights
     *            access rights from Spring
     */
    public void setAccessRights(final AccessRights accessRights) {
        this.accessRights = accessRights;
    }

    /**
     * Injects the lock handler object.
     * 
     * @spring.property ref="business.LockHandler"
     * @param lockHandler
     *            lock handler from Spring
     */
    public void setLockHandler(final LockHandler lockHandler) {
        this.lockHandler = lockHandler;
    }

    /**
     * Injects the data source.
     * 
     * @spring.property ref="escidoc-core.DataSource"
     * @param myDataSource
     *            data source from Spring
     */
    public void setMyDataSource(final DataSource myDataSource) {
        super.setDataSource(myDataSource);
    }
}
