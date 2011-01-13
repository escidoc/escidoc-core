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
 * Copyright 2007-2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.persistence.hibernate;

import java.sql.BatchUpdateException;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.NonUniqueResultException;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import de.escidoc.core.common.exceptions.system.FedoraSystemException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.util.string.StringUtility;

/**
 * Abstract (base) class for data access objects using hibernate.
 * 
 * @author TTE
 * 
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
     * @param object
     *            The object to delete.
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * @aa
     */
    public void delete(final Object object) throws SqlDatabaseSystemException {

        if (object != null) {
            try {
                getHibernateTemplate().delete(object);
            }
            catch (DataAccessException e) {
                handleBatchUpdateException((HibernateException) e.getCause());
                throw new SqlDatabaseSystemException(e.getMostSpecificCause());
            }
        }
    }

    /**
     * Flush all pending saves, updates and deletes to the database.
     *
     * Only invoke this for selective eager flushing, for example when JDBC code
     * needs to see certain changes within the same transaction. Else, it's
     * preferable to rely on auto-flushing at transaction completion.
     * 
     * @throws SqlDatabaseSystemException Thrown in case of Hibernate errors
     */
    public void flush() throws SqlDatabaseSystemException {
        getHibernateTemplate().flush();
    }

    /**
     * Saves the provided persistent object.
     * 
     * @param object
     *            The object to save.
     * @return The saved object.
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * @aa
     */
    protected Object save(final Object object)
        throws SqlDatabaseSystemException {

        Object result = null;
        if (object != null) {
            try {
                result = getHibernateTemplate().save(object);
            }
            catch (DataAccessException e) {
                handleBatchUpdateException((HibernateException) e.getCause());
                handleFedoraSystemException(e);
                throw new SqlDatabaseSystemException(e.getMostSpecificCause());
            }
        }
        return result;
    }

    /**
     * Saves the provided object (if it has not been saved before) or updates
     * the provided object.
     * 
     * @param object
     *            The object to save or update.
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * @aa
     */
    protected String saveOrUpdate(final Object object)
        throws SqlDatabaseSystemException {

        if (object != null) {
            try {
                getHibernateTemplate().saveOrUpdate(object);
            }
            catch (DataAccessException e) {
                handleBatchUpdateException((HibernateException) e.getCause());
                handleFedoraSystemException(e);
                throw new SqlDatabaseSystemException(e.getMostSpecificCause());
            }
        }
        return null;
    }

    /**
     * Saves the provided persistent object.
     * 
     * @param object
     *            The object to update.
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     */
    protected void update(final Object object)
        throws SqlDatabaseSystemException {

        if (object != null) {
            try {
                getHibernateTemplate().update(object);
            }
            catch (DataAccessException e) {
                handleBatchUpdateException((HibernateException) e.getCause());
                throw new SqlDatabaseSystemException(e.getMostSpecificCause());
            }
        }
    }

    /**
     * Checks if the provided <code>HibernateException</code> contains a
     * <code>BatchUpdateException</code> and throws an special
     * <code>SqlDatabaseSystemException</code> in this case.
     * 
     * @param e
     *            The exception to check.
     * @throws SqlDatabaseSystemException
     *             Thrown if the provided exception contains an
     *             <code>BatchUpdateException</code>.
     */
    private void handleBatchUpdateException(final HibernateException e)
        throws SqlDatabaseSystemException {
        if (e.getCause() instanceof BatchUpdateException) {
            Exception e1 =
                ((BatchUpdateException) e.getCause()).getNextException();
            throw new SqlDatabaseSystemException(StringUtility
                .concatenateWithBracketsToString(e.getMessage(), e
                    .getCause().getMessage(), e1.getMessage()), e1);
        }
    }

    /**
     * Checks if the provided <code>HibernateException</code> contains a
     * <code>FedoraSystemException</code> and throws an special
     * <code>SqlDatabaseSystemException</code> in this case.
     * 
     * @param e
     *            The exception to check.
     * @throws SqlDatabaseSystemException
     *             Thrown if the provided exception contains an
     *             <code>FedoraSystemException</code>.
     */
    private void handleFedoraSystemException(final Throwable e)
        throws SqlDatabaseSystemException {
        if (e.getCause() != null 
                && e.getCause().getCause() != null) {
            Throwable e1 = e.getCause().getCause();
            if (e1 instanceof FedoraSystemException) {
                StringBuffer message = 
                    new StringBuffer(FEDORA_EXCEPTION_MESSAGE);
                Throwable e2 = e1.getCause();
                if (e2 != null) {
                    message.append(e2.getMessage());
                }
                throw new SqlDatabaseSystemException(message.toString());
            }
        }
    }

    /**
     * Asserts the provided {@link List} contains one Object and returns it. If
     * the list is empty, <code>null</code> is returned. If the list contains
     * more than one object, a {@link NonUniqueResultException} is thrown.
     * 
     * @param results
     *            The {@link List} to be asserted.
     * @return Returns the single Object contained in the list or
     *         <code>null</code>.
     * @aa
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
     * @param set1 set1
     * @param set2 set2
     *            
     * @return Set merged set
     */
    protected Set<String> mergeSets(
        final Set<String> set1, final Set<String> set2) {
        
        Set<String> s1 = set1;
        Set<String> s2 = set2;
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
