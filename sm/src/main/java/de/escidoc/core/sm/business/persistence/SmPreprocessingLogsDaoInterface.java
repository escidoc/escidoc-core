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
package de.escidoc.core.sm.business.persistence;

import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.sm.business.persistence.hibernate.PreprocessingLog;

import java.util.Collection;
import java.util.Date;

/**
 * Database-Backend Interface for the PreprocessingLogs database-table.
 *
 * @author Michael Hoppe
 */
public interface SmPreprocessingLogsDaoInterface {

    /**
     * saves given PreprocessingLog-Data to the database.
     *
     * @param preprocessingLog preprocessingLog-Hibernate Object.
     * @return Integer primary key of created Object
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    String savePreprocessingLog(final PreprocessingLog preprocessingLog) throws SqlDatabaseSystemException;

    /**
     * retrieves all PreprocessingLogs from the database with given aggregationDefinitionId.
     *
     * @param aggregationDefinitionId aggregationDefinitionId
     * @return Collection of PreprocessingLogs as Hibernate-Objects
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    Collection<PreprocessingLog> retrievePreprocessingLogs(final String aggregationDefinitionId)
        throws SqlDatabaseSystemException;

    /**
     * retrieves all PreprocessingLogs from the database with given processingDate.
     *
     * @param processingDate processingDate
     * @return Collection of PreprocessingLogs as Hibernate-Objects
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    Collection<PreprocessingLog> retrievePreprocessingLogs(final Date processingDate) throws SqlDatabaseSystemException;

    /**
     * retrieves all PreprocessingLogs from the database with given aggregationDefinitionId and processingDate.
     *
     * @param aggregationDefinitionId aggregationDefinitionId
     * @param processingDate          processingDate
     * @return Collection of PreprocessingLogs as Hibernate-Objects
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    Collection<PreprocessingLog> retrievePreprocessingLogs(
        final String aggregationDefinitionId, final Date processingDate) throws SqlDatabaseSystemException;

    /**
     * retrieves all PreprocessingLogs from the database with given aggregationDefinitionId and error or not.
     *
     * @param aggregationDefinitionId aggregationDefinitionId
     * @param hasError                hasError
     * @return Collection of PreprocessingLogs as Hibernate-Objects
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    Collection<PreprocessingLog> retrievePreprocessingLogs(final String aggregationDefinitionId, final boolean hasError)
        throws SqlDatabaseSystemException;

    /**
     * retrieves all PreprocessingLogs from the database with given processingDate and error or not.
     *
     * @param processingDate processingDate
     * @param hasError       hasError
     * @return Collection of PreprocessingLogs as Hibernate-Objects
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    Collection<PreprocessingLog> retrievePreprocessingLogs(final Date processingDate, final boolean hasError)
        throws SqlDatabaseSystemException;

    /**
     * retrieves all PreprocessingLogs from the database with given processingDate and aggregationDefinitionId and error
     * or not.
     *
     * @param aggregationDefinitionId aggregationDefinitionId
     * @param processingDate          processingDate
     * @param hasError                hasError
     * @return Collection of PreprocessingLogs as Hibernate-Objects
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    Collection<PreprocessingLog> retrievePreprocessingLogs(
        final String aggregationDefinitionId, final Date processingDate, final boolean hasError)
        throws SqlDatabaseSystemException;

}
