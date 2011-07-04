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
package de.escidoc.core.oai.business.persistence;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.util.list.ListSorting;

import java.util.List;
import java.util.Map;

/**
 * Interface of an data access object to access set definition data.
 *
 * @author André Schenk
 */
public interface SetDefinitionDaoInterface {

    /**
     * Delete the set definition.
     *
     * @param setDefinition set definition object to delete
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void delete(final SetDefinition setDefinition) throws SqlDatabaseSystemException;

    /**
     * Retrieves a set definition.
     *
     * @param id The id of the set definition that shall be retrieved.
     * @return Returns the found {@code SetDefinition} object or {@code null}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    SetDefinition retrieveSetDefinition(final String id) throws SqlDatabaseSystemException;

    /**
     * Retrieves {@link SetDefinition} objects using the provided values for filtering.
     *
     * @param criteria   The {@link Map} containing the filter criteria. This object is kept as provided by this
     *                   method.
     * @param offset     The index of the first result to be returned.
     * @param maxResults The maximal number of results to be returned.
     * @param orderBy    The predicate that shall be used for ordering.
     * @param sorting    The kind of ordering, i.e. ascending or descending.
     * @return Returns {@code List} of {@link SetDefinition} objects selected by the provided parameters. If no
     *         parameter is provided, all user account objects are returned.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    List<SetDefinition> retrieveSetDefinitions(
        final Map<String, Object> criteria, final int offset, final int maxResults, final String orderBy,
        final ListSorting sorting) throws SqlDatabaseSystemException;

    /**
     * Retrieves {@link SetDefinition} objects using the provided values for filtering.
     *
     * @param criteria   The {@link String} containing the filter criteria as CQL query.
     * @param offset     The index of the first result to be returned.
     * @param maxResults The maximal number of results to be returned.
     * @return Returns {@code List} of {@link SetDefinition} objects selected by the provided parameters. If no
     *         parameter is provided, all user account objects are returned.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SqlDatabaseSystemException  Thrown in case of an internal database error.
     */
    List<SetDefinition> retrieveSetDefinitions(final String criteria, final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException;

    /**
     * Save the provided set definition data.
     *
     * @param setDefinition set definition data object to save
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void save(final SetDefinition setDefinition) throws SqlDatabaseSystemException;

    /**
     * Update the provided set definition data.
     *
     * @param setDefinition set definition data object to save
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    void update(final SetDefinition setDefinition) throws SqlDatabaseSystemException;

    SetDefinition findSetDefinitionBySpecification(final String specification) throws SqlDatabaseSystemException;

    // /**
    // * Checks if a set definition with the provided id exists.<br>
    // * The set definition is identified by either the id or the specification.
    // *
    // * @param identityInfo
    // * The id or specification of the set definition.
    // * @return Returns <code>true</code> if a set definition with the provided
    // * identifier exists, else <code>false</code>.
    // * @throws SqlDatabaseSystemException
    // * Thrown in case of an internal database access error.
    // */
    // boolean setDefinitionExists(final String identityInfo)
    // throws SqlDatabaseSystemException;
}
