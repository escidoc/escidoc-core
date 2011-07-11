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
import de.escidoc.core.common.exceptions.application.notfound.AggregationDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationDefinition;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationStatisticDataSelector;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationTable;

import java.util.Collection;

/**
 * Database-Backend Interface for the Aggregation-Definitions database-table.
 *
 * @author Michael Hoppe
 */
public interface SmAggregationDefinitionsDaoInterface {

    /**
     * saves given AggregationDefinition to database.
     *
     * @param aggregationDefinition The Hibernate Object.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void save(final AggregationDefinition aggregationDefinition) throws SqlDatabaseSystemException;

    /**
     * saves given AggregationTable to database.
     *
     * @param aggregationTable The Hibernate Object.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void save(final AggregationTable aggregationTable) throws SqlDatabaseSystemException;

    /**
     * saves given AggregationStatisticDataSelector to database.
     *
     * @param aggregationStatisticDataSelector
     *         The Hibernate Object.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void save(final AggregationStatisticDataSelector aggregationStatisticDataSelector)
        throws SqlDatabaseSystemException;

    /**
     * deletes AggregationDefinition with given id in the database. returns aggregation-definition as xml.
     *
     * @param aggregationDefinition The AggregationDefinition Hibernate Object.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void delete(final AggregationDefinition aggregationDefinition) throws SqlDatabaseSystemException;

    /**
     * retrieves AggregationDefinition with given id in the database. returns aggregation-definition as xml.
     *
     * @param id The id of the AggregationDefinition.
     * @return Aggregation definition as xml
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     * @throws AggregationDefinitionNotFoundException
     *                                    Thrown if aggregation-definition with given id was not found.
     */
    AggregationDefinition retrieve(final String id) throws SqlDatabaseSystemException,
        AggregationDefinitionNotFoundException;

    /**
     * retrieves all AggregationDefinitions from the database. returns Collection of aggregation-definition-xmls.
     *
     * @return Collection of AggregationDefinitions as Hibernate Objects
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    Collection<AggregationDefinition> retrieveAggregationDefinitions() throws SqlDatabaseSystemException;

    /**
     * retrieves AggregationDefinitions from the database. returns Collection of aggregation-definition XMLs.
     *
     * @param scopeIds   Collection of scopeIds
     * @param criteria   The {@link String} containing the filter criteria as CQL query.
     * @param offset     The index of the first result to be returned.
     * @param maxResults The maximal number of results to be returned.
     * @return Collection of AggregationDefinitions as Hibernate Objects
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SqlDatabaseSystemException  Thrown in case of an internal database access error.
     */
    Collection<AggregationDefinition> retrieveAggregationDefinitions(
        Collection<String> scopeIds, final String criteria, final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException;

    /**
     * retrieves all AggregationDefinitions from the database with scopeId in given list. returns Collection of
     * aggregation-definition-xmls.
     *
     * @param scopeIds Collection of scopeIds
     * @return Collection of AggregationDefinitions as Hibernate Objects
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    Collection<AggregationDefinition> retrieveAggregationDefinitions(Collection<String> scopeIds)
        throws SqlDatabaseSystemException;

    /**
     * gets next Primary Key for table AGGREGATION_DEFINITIONS.
     *
     * @return String primary key.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    String getNextPrimkey() throws SqlDatabaseSystemException;

}
