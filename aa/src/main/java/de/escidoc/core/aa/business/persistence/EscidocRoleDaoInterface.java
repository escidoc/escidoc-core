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
package de.escidoc.core.aa.business.persistence;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.util.list.ListSorting;

import java.util.List;
import java.util.Map;

/**
 * Interface of a data access object used to access {@link EscidocRole} objects.
 *
 * @author Torsten Tetteroo
 */
public interface EscidocRoleDaoInterface {

    /**
     * Checks if a role with the provided id exists.
     *
     * @param identifier An unique identifier of the {@link EscidocRole} object, either the id or the name of the role.
     * @return Returns {@code true} if a role with the provided id exists, else {@code false}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    boolean roleExists(final String identifier) throws SqlDatabaseSystemException;

    /**
     * Deletes the provided {@link EscidocRole} object from the storage.
     *
     * @param role The {@link EscidocRole} object to delete.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database error.
     */
    void deleteRole(final EscidocRole role) throws SqlDatabaseSystemException;

    /**
     * Flush all pending saves, updates and deletes to the database.
     * <p/>
     * Only invoke this for selective eager flushing, for example when JDBC code needs to see certain changes within the
     * same transaction. Else, it's preferable to rely on auto-flushing at transaction completion.
     *
     * @throws SqlDatabaseSystemException Thrown in case of Hibernate errors
     */
    void flush() throws SqlDatabaseSystemException;

    /**
     * Retrieves the {@link EscidocRole} object identified by the provided parameter value.
     *
     * @param identifier An unique identifier of the {@link EscidocRole} object, either the id or the name of the role.
     * @return Returns the identified {@link EscidocRole} object or {@code null}.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database error.
     */
    EscidocRole retrieveRole(final String identifier) throws SqlDatabaseSystemException;

    /**
     * Retrieves list of roles identified by the provided query.
     *
     * @param criteria   The {@link Map} containing the filter criteria. This object is changed during method
     *                   execution.
     * @param offset     The index of the first result to be returned.
     * @param maxResults The maximal number of results to be returned.
     * @param orderBy    The predicate that shall be used for ordering.
     * @param sorting    The kind of ordering, i.e. ascending or descending.
     * @return Returns {@code List} of identified {@link EscidocRole} objects. If no query or an empty query is
     *         provided, all roles are selected.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database error.
     */
    List<EscidocRole> retrieveRoles(
        Map<String, Object> criteria, int offset, int maxResults, String orderBy, ListSorting sorting)
        throws SqlDatabaseSystemException;

    /**
     * Retrieves list of roles identified by the provided query.
     *
     * @param criteria   The {@link String} containing the filter criteria as CQL query.
     * @param offset     The index of the first result to be returned.
     * @param maxResults The maximal number of results to be returned.
     * @return Returns {@code List} of identified {@link EscidocRole} objects. If no query or an empty query is
     *         provided, all roles are selected.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SqlDatabaseSystemException  Thrown in case of an internal database error.
     */
    List<EscidocRole> retrieveRoles(String criteria, int offset, int maxResults) throws InvalidSearchQueryException,
        SqlDatabaseSystemException;

    /**
     * Saves the provided new {@link EscidocRole} object or updated the provided existing {@link EscidocRole} object to
     * the database.
     *
     * @param role The {@link EscidocRole} object to save or update.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database error.
     */
    void saveOrUpdate(final EscidocRole role) throws SqlDatabaseSystemException;

    /**
     * Deletes the provided {@link ScopeDef} object from the database.
     *
     * @param scopeDef The {@link ScopeDef} object to delete.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database error.
     */
    void deleteScopeDef(final ScopeDef scopeDef) throws SqlDatabaseSystemException;

}
