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

import java.util.Date;

/**
 * Database-Backend Interface for the Statistic-Data database-table.
 *
 * @author Michael Hoppe
 */
public interface SmStatisticDataDaoInterface {

    /**
     * saves given statistic Data xml and timestamp to database.
     *
     * @param xmlData The statistic data xml.
     * @param scopeId The id of the scope.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void saveStatisticData(final String xmlData, final String scopeId) throws SqlDatabaseSystemException;

    /**
     * retrieves the statistic-data with the lowest timemarker for given scope.
     *
     * @param scopeId The id of the scope.
     * @return Date lowest date
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    Date retrieveMinTimestamp(final String scopeId) throws SqlDatabaseSystemException;
}
