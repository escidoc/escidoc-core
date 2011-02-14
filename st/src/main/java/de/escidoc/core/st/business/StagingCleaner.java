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
package de.escidoc.core.st.business;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.st.business.persistence.StagingFileDao;

import java.io.IOException;
import java.util.List;

/**
 * Implementation of staging area clean up.
 * 
 * @author TTE
 * @spring.bean id="st.StagingCleaner"
 * @st
 */
public class StagingCleaner {

    private static final AppLogger LOG =
        new AppLogger(StagingCleaner.class.getName());

    /**
     * Offset added to staging files' expire time stamp before removing them to
     * avoid removing of currently transmitted files.
     */
    private static final long EXPIRY_OFFSET = 500000;

    /** The staging file data access object used in this cleaner. */
    private StagingFileDao stagingFileDao;

    /**
     * Setting the stagingFileDao.
     * 
     * @param stagingSessionFileDao
     *            The stagingFileDao to set.
     * @spring.property ref="persistence.StagingFileDao"
     * @st
     */
    public final void setStagingSessionFactory(
        final StagingFileDao stagingSessionFileDao) {

        this.stagingFileDao = stagingSessionFileDao;
    }

    /**
     * Cleans up the staging area, i.e. removes each file in the file system
     * associated to an expired staging file object and each expired staging
     * file whose associated file does not exist or could be removed.
     * 
     * @st
     */
    public void cleanUp() {

        LOG.debug("Cleaning up the staging file area");

        List<StagingFile> expiredStagingFiles;
        try {
            expiredStagingFiles = stagingFileDao.findExpiredStagingFiles();
        }
        catch (SqlDatabaseSystemException e) {
            LOG.error(e);
            return;
        }

        for (int i = 0; i < expiredStagingFiles.size(); i++) {
            StagingFile stagingFile = expiredStagingFiles.get(i);

            // To avoid removing of a file that is currently transmitted to
            // a client, a offset is added to the expire time stamp.
            // TODO: this should be removed by locking mechanism.
            if (stagingFile.getExpiryTs() + EXPIRY_OFFSET < System
                .currentTimeMillis()) {

                try {
                    if (stagingFile.hasFile()) {
                        stagingFile.clear();
                    }
                }
                catch (IOException e) {
                    LOG.error(StringUtility.format(
                            "Removing file failed", stagingFile.getReference(),
                            e.getClass().getName()).toString(), e);
                }
                try {
                    if (!stagingFile.hasFile()) {
                        stagingFileDao.delete(stagingFile);
                    }
                }
                catch (SqlDatabaseSystemException e) {
                    LOG.error(e);
                }
            }
        }
    }

}
