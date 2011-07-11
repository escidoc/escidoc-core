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
package de.escidoc.core.aa.business.renderer;

import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.system.TripleStoreSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * DAO implementation used by the renderer to get needed data.
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.business.renderer.RendererDao")
public class RendererDao {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RendererDao.class);

    @Autowired
    @Qualifier("business.TripleStoreUtility")
    private TripleStoreUtility tsu;

    /**
     * Gets the title of the specified organizational unit.<br> First, the triple store is asked for the ou's title. If
     * this fails, the organizationalUnitHandler is asked for the xml representation of the organizational unit and the
     * title is set to the name extracted from the xml data. The latter has been implemented as a fallback to handle old
     * ous for that no title may be specified in the triple store.
     *
     * @param ouId The id of the organizational unit.
     * @return Returns the title of the organizational unit.
     * @throws de.escidoc.core.common.exceptions.system.TripleStoreSystemException
     */
    public String getOrganizationalUnitTitle(final String ouId) throws TripleStoreSystemException {

        String title = tsu.getTitle(ouId);

        if (title == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("OU title not found in triple store, returning 'Non existing Organizational Unit'");
            }
            // no title indicates the OU does not exist. Report unknown title or
            // throw an exception. In case of exception retrieving a user
            // account with non existing OU will fail.
            // Do not return 'null' because velocity will not replace the title
            // placeholder (?)
            title = "Non existing Organizational Unit";
        }
        return title;
    }
}
