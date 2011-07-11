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

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import de.escidoc.core.st.business.persistence.StagingFileDao;

/**
 * Implementation of staging area clean up.
 *
 * @author Torsten Tetteroo
 */
@Service("st.StagingCleaner")
public class StagingCleaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(StagingCleaner.class);

    /**
     * Offset added to staging files' expire time stamp before removing them to avoid removing of currently transmitted
     * files.
     */
    private static final long EXPIRY_OFFSET = 500000L;

    /**
     * The staging file data access object used in this cleaner.
     */
    @Autowired
    @Qualifier("persistence.StagingFileDao")
    private StagingFileDao stagingFileDao;

    /**
     * Cleans up the staging area, i.e. removes each file in the file system associated to an expired staging file
     * object and each expired staging file whose associated file does not exist or could be removed.
     */
    @Scheduled(fixedRate = 3600000)
    // TODO: made configurable
    public void cleanUp() {
        final long lastExecutionTime = StagingCleanerTimer.getInstance().getLastExecutionTime();
        if (lastExecutionTime > 0L && System.currentTimeMillis() - lastExecutionTime < 10000L) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Cleaning up the staging file area.");
        }
        final List<StagingFile> expiredStagingFiles;
        try {
            expiredStagingFiles = stagingFileDao.findExpiredStagingFiles();
        }
        catch (final SqlDatabaseSystemException e) {
            LOGGER.error("Error on finding expired staging files.", e);
            return;
        }

        for (final StagingFile stagingFile : expiredStagingFiles) {
            // To avoid removing of a file that is currently transmitted to
            // a client, a offset is added to the expire time stamp.
            // TODO: this should be removed by locking mechanism.
            if (stagingFile.getExpiryTs() + EXPIRY_OFFSET < System.currentTimeMillis()) {

                try {
                    if (stagingFile.hasFile()) {
                        stagingFile.clear();
                    }
                }
                catch (final IOException e) {
                    if (LOGGER.isWarnEnabled()) {
                        LOGGER.warn(StringUtility.format("Removing file failed", stagingFile.getReference(), e
                            .getClass().getName()));
                    }
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(StringUtility.format("Removing file failed", stagingFile.getReference(), e
                            .getClass().getName()), e);
                    }
                }
                try {
                    if (!stagingFile.hasFile()) {
                        stagingFileDao.delete(stagingFile);
                    }
                }
                catch (final SqlDatabaseSystemException e) {
                    LOGGER.error("Error on deleting staging file " + stagingFile.getToken(), e);
                }
            }
        }
    }

}
