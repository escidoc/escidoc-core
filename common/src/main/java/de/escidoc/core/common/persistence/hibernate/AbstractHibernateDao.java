/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.persistence.hibernate;

import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.util.string.StringUtility;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueResultException;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

/**
 * Abstract (base) class for data access objects using hibernate.
 *
 * @author Torsten Tetteroo
 */
public abstract class AbstractHibernateDao extends HibernateDaoSupport {

    protected static final int COMPARE_LIKE = 0;

    protected static final int COMPARE_EQ = 1;

    private static final String FEDORA_EXCEPTION_MESSAGE =
        new StringBuffer("Fedora might not be running:\n")
            .append("Problem retrieving Primary Key from Fedora:\n").toString();

    /**
     * Deletes the provided persistent object.
     *
     * @param object The object to delete.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    public void delete(final Object object) throws SqlDatabaseSystemException {

        if (object != null) {
            try {
                getHibernateTemplate().delete(object);
            }
            catch (final DataAccessException e) {
                handleBatchUpdateException((HibernateException) e.getCause());
                throw new SqlDatabaseSystemException(e.getMostSpecificCause()); // Ignore FindBugs
            }
        }
    }

    /**
     * Flush all pending saves, updates and deletes to the database.
     * <p/>
     * Only invoke this for selective eager flushing, for example when JDBC code needs to see certain changes within the
     * same transaction. Else, it's preferable to rely on auto-flushing at transaction completion.
     *
     * @throws SqlDatabaseSystemException Thrown in case of Hibernate errors
     */
    public void flush() {
        getHibernateTemplate().flush();
    }

    /**
     * Saves the provided persistent object.
     *
     * @param object The object to save.
     * @return The saved object.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    protected Object save(final Object object) throws SqlDatabaseSystemException {

        Object result = null;
        if (object != null) {
            try {
                result = getHibernateTemplate().save(object);
            }
            catch (final DataAccessException e) {
                handleBatchUpdateException((HibernateException) e.getCause());
                handleFedoraSystemException(e);
                throw new SqlDatabaseSystemException(e.getMostSpecificCause()); // Ignore FindBugs
            }
        }
        return result;
    }

    /**
     * Saves the provided object (if it has not been saved before) or updates the provided object.
     *
     * @param object The object to save or update.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     * @return
     */
    protected String saveOrUpdate(final Object object) throws SqlDatabaseSystemException {

        if (object != null) {
            try {
                getHibernateTemplate().saveOrUpdate(object);
            }
            catch (final DataAccessException e) {
                handleBatchUpdateException((HibernateException) e.getCause());
                handleFedoraSystemException(e);
                throw new SqlDatabaseSystemException(e.getMostSpecificCause()); // Ignore FindBugs
            }
        }
        return null;
    }

    /**
     * Saves the provided persistent object.
     *
     * @param object The object to update.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    protected void update(final Object object) throws SqlDatabaseSystemException {

        if (object != null) {
            try {
                getHibernateTemplate().update(object);
            }
            catch (final DataAccessException e) {
                handleBatchUpdateException((HibernateException) e.getCause());
                throw new SqlDatabaseSystemException(e.getMostSpecificCause()); // Ignore FindBugs
            }
        }
    }

    /**
     * Checks if the provided {@code HibernateException} contains a {@code BatchUpdateException} and throws an
     * special {@code SqlDatabaseSystemException} in this case.
     *
     * @param e The exception to check.
     * @throws SqlDatabaseSystemException Thrown if the provided exception contains an {@code BatchUpdateException}.
     */
    private static void handleBatchUpdateException(final HibernateException e) throws SqlDatabaseSystemException {
        if (e.getCause() instanceof BatchUpdateException) {
            final Exception e1 = ((SQLException) e.getCause()).getNextException();
            throw new SqlDatabaseSystemException(StringUtility.format(e.getMessage(), e.getCause().getMessage(), e1
                .getMessage()), e1);
        }
    }

    /**
     * Checks if the provided {@code HibernateException} contains a {@code FedoraSystemException} and throws
     * an special {@code SqlDatabaseSystemException} in this case.
     *
     * @param e The exception to check.
     * @throws SqlDatabaseSystemException Thrown if the provided exception contains an {@code FedoraSystemException}.
     */
    private static void handleFedoraSystemException(final Throwable e) throws SqlDatabaseSystemException {
        if (e.getCause() != null && e.getCause().getCause() != null) {
            final Throwable e1 = e.getCause().getCause();
            if (e1 instanceof FedoraSystemException) {
                final StringBuilder message = new StringBuilder(FEDORA_EXCEPTION_MESSAGE);
                final Throwable e2 = e1.getCause();
                if (e2 != null) {
                    message.append(e2.getMessage());
                }
                throw new SqlDatabaseSystemException(message.toString());
            }
        }
    }

    /**
     * Asserts the provided {@link List} contains one Object and returns it. If the list is empty, {@code null} is
     * returned. If the list contains more than one object, a {@link NonUniqueResultException} is thrown.
     *
     * @param results The {@link List} to be asserted.
     * @return Returns the single Object contained in the list or {@code null}.
     */
    protected Object getUniqueResult(final List<Object> results) {

        final int resultSize = results.size();
        if (resultSize == 1) {
            return results.get(0);
        }
        else if (resultSize > 1) {
            throw new NonUniqueResultException(resultSize);
        }
        else {
            return null;
        }
    }

    /**
     * merges 2 sets into 1
     *
     * @param s1
     * @param s2
     * @return Set merged set
     */
    protected Set<String> mergeSets(final Set<String> s1, final Set<String> s2) {
        if (s1 == null && s2 == null) {
            return null;
        }
        else if (s1 == null) {
            return s2;
        }
        else if (s2 == null) {
            return s1;
        }
        else {
            s1.addAll(s2);
            return s1;
        }

    }

}
