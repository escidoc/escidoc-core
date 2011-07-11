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
import de.escidoc.core.common.exceptions.application.notfound.AggregationDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.persistence.EscidocIdProvider;
import de.escidoc.core.common.persistence.hibernate.AbstractHibernateDao;
import de.escidoc.core.sm.business.filter.AggregationDefinitionFilter;
import de.escidoc.core.sm.business.persistence.SmAggregationDefinitionsDaoInterface;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Hibernate Database-Backend for the Aggregation-Definitions database-table.
 *
 * @author Michael Hoppe
 */
public class SmAggregationDefinitionsHibernateDao extends AbstractHibernateDao
    implements SmAggregationDefinitionsDaoInterface {

    private EscidocIdProvider idProvider;

    /**
     * See Interface for functional description.
     *
     * @param aggregationDefinition The aggregationDefinition Hibernate Object.
     * @throws SqlDatabaseSystemException e
     * @see de.escidoc.core.sm.business.persistence .SmAggregationdefinitionsDaoInterface
     *      #save(de.escidoc.core.sm.business.persistence.hibernate.AggregationDefinition)
     */
    @Override
    public void save(final AggregationDefinition aggregationDefinition) throws SqlDatabaseSystemException {
        super.save(aggregationDefinition);
    }

    /**
     * See Interface for functional description.
     *
     * @param aggregationTable The aggregationTable Hibernate Object.
     * @throws SqlDatabaseSystemException e
     * @see de.escidoc.core.sm.business.persistence .SmAggregationdefinitionsDaoInterface
     *      #save(de.escidoc.core.sm.business.persistence.hibernate.AggregationTable)
     */
    @Override
    public void save(final AggregationTable aggregationTable) throws SqlDatabaseSystemException {
        super.save(aggregationTable);
    }

    /**
     * See Interface for functional description.
     *
     * @param aggregationStatisticDataSelector
     *         The aggregationStatisticDataSelector Hibernate Object.
     * @throws SqlDatabaseSystemException e
     * @see de.escidoc.core.sm.business.persistence .SmAggregationdefinitionsDaoInterface
     *      #save(de.escidoc.core.sm.business.persistence.hibernate.AggregationStatisticDataSelector)
     */
    @Override
    public void save(final AggregationStatisticDataSelector aggregationStatisticDataSelector)
        throws SqlDatabaseSystemException {
        super.save(aggregationStatisticDataSelector);
    }

    /**
     * See Interface for functional description.
     *
     * @param aggregationDefinition The aggregationDefinition Hibernate Object.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     * @see de.escidoc.core.sm.business .persistence.SmAggregationDefinitionsDaoInterface
     *      #delete(de.escidoc.core.sm.business.persistence.hibernate.AggregationDefinition)
     */
    @Override
    public void delete(final AggregationDefinition aggregationDefinition) throws SqlDatabaseSystemException {
        super.delete(aggregationDefinition);
    }

    /**
     * See Interface for functional description.
     *
     * @param id The id of the AggregationDefinition.
     * @return AggregationDefinition as Hibernate Object
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     * @throws AggregationDefinitionNotFoundException
     *                                    Thrown if aggregation-definition with given id was not found.
     * @see de.escidoc.core.sm.business .persistence.SmAggregationDefinitionsDaoInterface #retrieve(java.lang.String)
     */
    @Override
    public AggregationDefinition retrieve(final String id) throws SqlDatabaseSystemException,
        AggregationDefinitionNotFoundException {
        AggregationDefinition result = null;
        if (id != null) {
            try {
                result = getHibernateTemplate().get(AggregationDefinition.class, id);
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
            throw new AggregationDefinitionNotFoundException("AggregationDefinition with id " + id + " was not found");
        }
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @return Collection of AggregationDefinitions as Hibernate Objects
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     * @see de.escidoc.core.sm.business .persistence.SmAggregationDefinitionsDaoInterface
     *      #retrieveAggregationDefinitions()
     */
    @Override
    public Collection<AggregationDefinition> retrieveAggregationDefinitions() throws SqlDatabaseSystemException {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(AggregationDefinition.class, "a");
        return getHibernateTemplate().findByCriteria(detachedCriteria);
    }

    /**
     * See Interface for functional description.
     *
     * @param scopeIds   Collection of scopeIds
     * @param criteria   The {@link String} containing the filter criteria as CQL query.
     * @param offset     The index of the first result to be returned.
     * @param maxResults The maximal number of results to be returned.
     * @return Collection of AggregationDefinitions as XML
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SqlDatabaseSystemException  Thrown in case of an internal database access error.
     * @see de.escidoc.core.sm.business .persistence.SmAggregationDefinitionsDaoInterface
     *      #retrieveAggregationDefinitions(java.lang.String, int offset, int maxResults)
     */
    @Override
    public Collection<AggregationDefinition> retrieveAggregationDefinitions(
        final Collection<String> scopeIds, final String criteria, final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException {
        final Collection<AggregationDefinition> result = new ArrayList<AggregationDefinition>();

        if (scopeIds != null && !scopeIds.isEmpty()) {

            final DetachedCriteria detachedCriteria =
                criteria != null && criteria.length() > 0 ? new AggregationDefinitionFilter(criteria).toSql() : DetachedCriteria
                    .forClass(AggregationDefinition.class, "a");
            detachedCriteria.add(Restrictions.in("scope.id", scopeIds));

            final Collection<AggregationDefinition> aggregationDefinitions =
                getHibernateTemplate().findByCriteria(detachedCriteria, offset, maxResults);

            if (aggregationDefinitions != null) {
                return aggregationDefinitions;
            }
        }
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @param scopeIds Collection of scopeIds
     * @return Collection of AggregationDefinitions as xml
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     * @see de.escidoc.core.sm.business .persistence.SmAggregationDefinitionsDaoInterface
     *      #retrieveAggregationDefinitions(java.util.Collection)
     */
    @Override
    public Collection<AggregationDefinition> retrieveAggregationDefinitions(final Collection<String> scopeIds)
        throws SqlDatabaseSystemException {

        if (scopeIds == null || scopeIds.isEmpty()) {
            return new ArrayList<AggregationDefinition>();
        }

        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(AggregationDefinition.class, "a");
        detachedCriteria.add(Restrictions.in("scope.id", scopeIds));

        final Collection<AggregationDefinition> aggregationDefinitions =
            getHibernateTemplate().findByCriteria(detachedCriteria);
        if (aggregationDefinitions != null) {
            return aggregationDefinitions;
        }
        return new ArrayList<AggregationDefinition>();
    }

    /**
     * gets next Primary Key for table AGGREGATION_DEFINITIONS.
     *
     * @return String primary key.
     * @throws SqlDatabaseSystemException Thrown in case of an internal database access error.
     */
    @Override
    public String getNextPrimkey() throws SqlDatabaseSystemException {
        try {
            return idProvider.getNextPid();
        }
        catch (final SystemException e) {
            throw new SqlDatabaseSystemException(e);
        }
    }

    /**
     * Setting the genericDao.
     *
     * @param idProvider The idProvider to set.
     */
    public final void setIdProvider(final EscidocIdProvider idProvider) {
        this.idProvider = idProvider;
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
