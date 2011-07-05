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
package de.escidoc.core.sm.business.persistence.hibernate;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.persistence.hibernate.AbstractHibernateDao;
import de.escidoc.core.sm.business.filter.ScopeFilter;
import de.escidoc.core.sm.business.persistence.SmScopesDaoInterface;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Database-Backend for the Scopes database-table.
 *
 * @author Michael Hoppe
 */
public class SmScopesHibernateDao extends AbstractHibernateDao implements SmScopesDaoInterface {

    /**
     * See Interface for functional description.
     *
     * @param scope The scope hibernate object.
     * @throws SqlDatabaseSystemException e
     * @see SmScopesDaoInterface #save(de.escidoc.core.sm.business.persistence.hibernate.Scope)
     */
    @Override
    public void save(final Scope scope) throws SqlDatabaseSystemException {
        super.save(scope);
    }

    /**
     * See Interface for functional description.
     *
     * @param scope The scope hibernate object.
     * @throws SqlDatabaseSystemException e
     * @see SmScopesDaoInterface #update(de.escidoc.core.sm.business.persistence.hibernate.Scope)
     */
    @Override
    public void update(final Scope scope) throws SqlDatabaseSystemException {
        super.update(scope);
    }

    /**
     * See Interface for functional description.
     *
     * @param scope The scope hibernate object.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     * @see SmScopesDaoInterface #delete(de.escidoc.core.sm.business.persistence.hibernate.Scope)
     */
    @Override
    public void delete(final Scope scope) throws SqlDatabaseSystemException {
        if (scope != null
            && (scope.getAggregationDefinitions() != null && !scope.getAggregationDefinitions().isEmpty()
                || scope.getReportDefinitions() != null && !scope.getReportDefinitions().isEmpty() || scope
                .getStatisticDatas() != null
                && !scope.getStatisticDatas().isEmpty())) {
            throw new SqlDatabaseSystemException("Scope still references dependent objects");
        }
        super.delete(scope);
    }

    /**
     * See Interface for functional description.
     *
     * @param id The id of the Scope.
     * @return Scope scope as hibernate Object
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     * @throws ScopeNotFoundException     Thrown if scope with given id was not found.
     * @see SmScopesDaoInterface #retrieve(java.lang.Integer)
     */
    @Override
    public Scope retrieve(final String id) throws SqlDatabaseSystemException, ScopeNotFoundException {
        Scope result = null;
        if (id != null) {
            try {
                result = getHibernateTemplate().get(Scope.class, id);
            }
            catch (final DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (final IllegalStateException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (final HibernateException e) {
                //noinspection ThrowableResultOfMethodCallIgnored
                throw new SqlDatabaseSystemException(convertHibernateAccessException(e)); // Ignore FindBugs
            }
        }
        if (result == null) {
            throw new ScopeNotFoundException("Scope with id " + id + " was not found");
        }
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @return Collection of Scopes as hibernate Objects
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     * @see SmScopesDaoInterface #retrieveScopes()
     */
    @Override
    public Collection<Scope> retrieveScopes() throws SqlDatabaseSystemException {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Scope.class, "s");
        return getHibernateTemplate().findByCriteria(detachedCriteria);
    }

    /**
     * See Interface for functional description.
     *
     * @param scopeIds Collection of scopeIds
     * @return Collection of Scopes as xml
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     * @see SmScopesDaoInterface #retrieveScopes(java.util.Collection)
     */
    @Override
    public Collection<Scope> retrieveScopes(final Collection<String> scopeIds) throws SqlDatabaseSystemException {
        if (scopeIds == null || scopeIds.isEmpty()) {
            return null;
        }

        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Scope.class, "s");
        detachedCriteria.add(Restrictions.in("id", scopeIds));
        return getHibernateTemplate().findByCriteria(detachedCriteria);
    }

    /**
     * retrieves Scopes from the database with scopeId in given list that match the given filter.
     *
     * @param scopeIds   Collection of scopeIds
     * @param criteria   The {@link String} containing the filter criteria as CQL query.
     * @param offset     The index of the first result to be returned.
     * @param maxResults The maximal number of results to be returned.
     * @return Collection of Scopes as XML
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SqlDatabaseSystemException  Thrown in case of an internal database access error.
     */
    @Override
    public Collection<Scope> retrieveScopes(
        final Collection<String> scopeIds, final String criteria, final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException {

        if (scopeIds != null && !scopeIds.isEmpty()) {

            final DetachedCriteria detachedCriteria =
                criteria != null && criteria.length() > 0 ? new ScopeFilter(criteria).toSql() : DetachedCriteria
                    .forClass(Scope.class, "s");
            detachedCriteria.add(Restrictions.in("id", scopeIds));

            final Collection<Scope> scopes =
                getHibernateTemplate().findByCriteria(detachedCriteria, offset, maxResults);

            if (scopes != null) {
                return scopes;
            }
        }
        return new ArrayList<Scope>();
    }

    /**
     * See Interface for functional description.
     *
     * @return Collection of Scope-ids
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     * @see SmScopesDaoInterface #retrieveScopeIds()
     */
    @Override
    public List<String> retrieveScopeIds() throws SqlDatabaseSystemException {
        final List<String> scopeIds = new ArrayList<String>();

        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Scope.class, "s");
        final Collection<Scope> scopes = getHibernateTemplate().findByCriteria(detachedCriteria);

        if (scopes != null) {
            for (final Scope scope : scopes) {
                if (scope.getId() != null) {
                    scopeIds.add(scope.getId());
                }
            }
        }
        return scopeIds;
    }

    /**
     * Wrapper of setSessionFactory to enable bean stuff generation for this bean.
     *
     * @param mySessionFactory The sessionFactory to set.
     */
    public final void setMySessionFactory(final SessionFactory mySessionFactory) {

        setSessionFactory(mySessionFactory);
    }

}
