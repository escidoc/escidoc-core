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

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.sm.business.persistence.hibernate.ReportDefinition;

import java.util.Collection;

/**
 * Database-Backend Interface for the Report-Definitions database-table.
 *
 * @author Michael Hoppe
 */
public interface SmReportDefinitionsDaoInterface {

    /**
     * saves given ReportDefinition to the database.
     *
     * @param reportDefinition The ReportDefinition as Hibernate Object.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void save(final ReportDefinition reportDefinition) throws SqlDatabaseSystemException;

    /**
     * updates ReportDefinition in the database.
     *
     * @param reportDefinition The ReportDefinition as Hibernate Object.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void update(final ReportDefinition reportDefinition) throws SqlDatabaseSystemException;

    /**
     * deletes ReportDefinition in the database.
     *
     * @param reportDefinition The ReportDefinition as Hibernate Object.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void delete(final ReportDefinition reportDefinition) throws SqlDatabaseSystemException;

    /**
     * retrieves ReportDefinition with given id in the database.
     *
     * @param id The id of the ReportDefinition.
     * @return ReportDefinition as Hibernate Object
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     * @throws ReportDefinitionNotFoundException
     *                                    Thrown if report-definition with given id was not found.
     */
    ReportDefinition retrieve(final String id) throws SqlDatabaseSystemException, ReportDefinitionNotFoundException;

    /**
     * retrieves all ReportDefinitions from the database.
     *
     * @return Collection of ReportDefinitions as xml
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    Collection<ReportDefinition> retrieveReportDefinitions() throws SqlDatabaseSystemException;

    /**
     * retrieves all ReportDefinitions from the database with scopeId in given list.
     *
     * @param scopeIds   Collection of scopeIds
     * @param criteria   The {@link String} containing the filter criteria as CQL query.
     * @param offset     The index of the first result to be returned.
     * @param maxResults The maximal number of results to be returned.
     * @return Collection of ReportDefinitions as Hibernate Objects
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SqlDatabaseSystemException  Thrown in case of an internal database access error.
     */
    Collection<ReportDefinition> retrieveReportDefinitions(
        Collection<String> scopeIds, final String criteria, final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException;
}
