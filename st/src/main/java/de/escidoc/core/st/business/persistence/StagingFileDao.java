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
package de.escidoc.core.st.business.persistence;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.st.business.StagingFile;

import java.util.List;

/**
 * Interface of the data access layer.
 *
 * @author Torsten Tetteroo
 */
public interface StagingFileDao {

    /**
     * Finds the staging file identified by the given token.
     *
     * @param token The token identifying the staging file.
     * @return Returns the staging file object identified by the token or {@code null}.
     * @throws SqlDatabaseSystemException TODO
     */
    StagingFile findStagingFile(final String token) throws SqlDatabaseSystemException;

    /**
     * Find the staging files with expired token.
     *
     * @return {@code List} containing all staging files that have an expired token, i.e. for which the expiry
     *         timestamp is less than the current timestamp.
     * @throws SqlDatabaseSystemException TODO
     */
    List<StagingFile> findExpiredStagingFiles() throws SqlDatabaseSystemException;

    /**
     * Save the given staging file to database.<br> While saving the object, the unique token identifying the staging
     * file object is generated.
     *
     * @param stagingFile The staging file to save.
     * @throws SqlDatabaseSystemException TODO
     */
    void save(final StagingFile stagingFile) throws SqlDatabaseSystemException;

    /**
     * Update the given staging file to database.
     *
     * @param stagingFile The staging file to update.
     * @throws SqlDatabaseSystemException TODO
     */
    void update(final StagingFile stagingFile) throws SqlDatabaseSystemException;

    /**
     * Save or update the given staging file to database.<br> While saving the object, the unique token identifying the
     * staging file object is generated.
     *
     * @param stagingFile The staging file to save/update.
     * @throws SqlDatabaseSystemException TODO
     */
    void saveOrUpdate(final StagingFile stagingFile) throws SqlDatabaseSystemException;

    /**
     * Delete the given staging file from database.
     *
     * @param stagingFile The staging file to delete.
     * @throws SqlDatabaseSystemException TODO
     */
    void delete(final StagingFile stagingFile) throws SqlDatabaseSystemException;

    /**
     * Delete the given staging files from database.
     *
     * @param stagingFiles The staging files to delete.
     * @throws SqlDatabaseSystemException TODO
     */
    void delete(final StagingFile[] stagingFiles) throws SqlDatabaseSystemException;

}
