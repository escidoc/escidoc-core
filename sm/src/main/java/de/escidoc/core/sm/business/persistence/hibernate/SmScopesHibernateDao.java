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
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.sm.business.filter.ScopeFilter;
import de.escidoc.core.sm.business.persistence.SmScopesDaoInterface;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Database-Backend for the Scopes database-table.
 * 
 * @author MIH
 * @spring.bean id="persistence.SmScopesDao"
 * @sm
 */
public class SmScopesHibernateDao     
    extends AbstractHibernateDao
    implements SmScopesDaoInterface {

    private static AppLogger log =
        new AppLogger(SmScopesHibernateDao.class.getName());

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmScopesDaoInterface
     *      #save(de.escidoc.core.sm.business.persistence.hibernate.Scope)
     * 
     * @param scope
     *            The scope hibernate object.
     * @throws SqlDatabaseSystemException
     *             e
     * 
     * @sm
     */
    public void save(final Scope scope)
        throws SqlDatabaseSystemException {
        super.save(scope);
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmScopesDaoInterface
     *      #update(de.escidoc.core.sm.business.persistence.hibernate.Scope)
     * 
     * @param scope
     *            The scope hibernate object.
     * @throws SqlDatabaseSystemException
     *             e
     * 
     * @sm
     */
    public void update(final Scope scope)
        throws SqlDatabaseSystemException {
        super.update(scope);
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmScopesDaoInterface
     *      #delete(de.escidoc.core.sm.business.persistence.hibernate.Scope)
     * 
     * @param scope
     *            The scope hibernate object.
     * 
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * @sm
     */
    public void delete(final Scope scope)
        throws SqlDatabaseSystemException {
        if (scope != null && ((scope.getAggregationDefinitions() != null
            && !scope.getAggregationDefinitions().isEmpty())
            || (scope.getReportDefinitions() != null 
            && !scope.getReportDefinitions().isEmpty()) 
            || (scope.getStatisticDatas() != null
            && !scope.getStatisticDatas().isEmpty()))) {
            throw new SqlDatabaseSystemException(
                "Scope still references dependent objects");
        }
        super.delete(scope);
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmScopesDaoInterface
     *      #retrieve(java.lang.Integer)
     * 
     * @param id
     *            The id of the Scope.
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * @throws ScopeNotFoundException
     *             Thrown if scope with given id was not found.
     * @return Scope scope as hibernate Object
     * 
     * @sm
     */
    public Scope retrieve(final String id)
        throws SqlDatabaseSystemException, ScopeNotFoundException {
        Scope result = null;
        if (id != null) {
            try {
                result =
                    (Scope) getHibernateTemplate().get(Scope.class,
                        id);
            }
            catch (DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (IllegalStateException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (HibernateException e) {
                throw new SqlDatabaseSystemException(
                    convertHibernateAccessException(e));
            }
        }
        if (result == null) {
            throw new ScopeNotFoundException(
                    "Scope with id " + id + " was not found");
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmScopesDaoInterface
     *      #retrieveScopes()
     * 
     * @return Collection of Scopes as hibernate Objects
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * 
     * @sm
     */
    public Collection<Scope> retrieveScopes() throws SqlDatabaseSystemException {
        final DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(Scope.class, "s");

        Collection<Scope> scopes =
                getHibernateTemplate().findByCriteria(detachedCriteria);
        return scopes;
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmScopesDaoInterface
     *      #retrieveScopes(java.util.Collection)
     * 
     * @param scopeIds
     *            Collection of scopeIds
     * @return Collection of Scopes as xml
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * 
     * @sm
     */
    public Collection<Scope> retrieveScopes(final Collection<String> scopeIds)
        throws SqlDatabaseSystemException {
        if (scopeIds == null || scopeIds.isEmpty()) {
            return null;
        }

        final DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(Scope.class, "s");
        detachedCriteria.add(Restrictions.in("id", scopeIds));

        Collection<Scope> scopes =
                getHibernateTemplate().findByCriteria(detachedCriteria);

        return scopes;
    }

    /**
     * retrieves Scopes from the database with scopeId in given list that match
     * the given filter.
     * 
     * @param scopeIds
     *            Collection of scopeIds
     * @param criteria
     *            The {@link String} containing the filter criteria as CQL
     *            query.
     * @param offset
     *            The index of the first result to be returned.
     * @param maxResults
     *            The maximal number of results to be returned.
     * 
     * @return Collection of Scopes as XML
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     */
    public Collection<Scope> retrieveScopes(
        final Collection<String> scopeIds, final String criteria,
        final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException {

        if ((scopeIds != null) && (!scopeIds.isEmpty())) {
            final DetachedCriteria detachedCriteria;

            if ((criteria != null) && (criteria.length() > 0)) {
                detachedCriteria = new ScopeFilter(criteria).toSql();
            }
            else {
                detachedCriteria = DetachedCriteria.forClass(Scope.class, "s");
            }
            detachedCriteria.add(Restrictions.in("id", scopeIds));

            Collection<Scope> scopes =
                getHibernateTemplate().findByCriteria(detachedCriteria, offset,
                    maxResults);

            if (scopes != null) {
                return scopes;
            }
        }
        return new ArrayList<Scope>();
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.persistence.SmScopesDaoInterface
     *      #retrieveScopeIds()
     * 
     * @return Collection of Scope-ids
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * 
     * @sm
     */
    public Collection<String> retrieveScopeIds() 
                throws SqlDatabaseSystemException {
        Collection<String> scopeIds = new ArrayList<String>();

        final DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(Scope.class, "s");
        Collection<Scope> scopes =
            getHibernateTemplate().findByCriteria(detachedCriteria);

        if (scopes != null) {
            for (Scope scope : scopes) {
                if (scope.getId() != null) {
                    scopeIds.add(scope.getId().toString());
                }
            }
        }
        return scopeIds;
    }

    /**
     * Wrapper of setSessionFactory to enable bean stuff generation for this
     * bean.
     * 
     * @param mySessionFactory
     *            The sessionFactory to set.
     * @spring.property ref="sm.SessionFactory"
     * @sm
     */
    public final void setMySessionFactory(final SessionFactory mySessionFactory) {

        super.setSessionFactory(mySessionFactory);
    }
    
}
