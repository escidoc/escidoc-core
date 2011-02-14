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
import de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.persistence.hibernate.AbstractHibernateDao;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.sm.business.filter.ReportDefinitionFilter;
import de.escidoc.core.sm.business.persistence.SmReportDefinitionsDaoInterface;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.DataAccessException;

import java.util.Collection;

/**
 * Database-Backend for the Report-Definitions database-table.
 * 
 * @author MIH
 * @spring.bean id="persistence.SmReportDefinitionsDao"
 * @sm
 */
public class SmReportDefinitionsHibernateDao
    extends AbstractHibernateDao
    implements SmReportDefinitionsDaoInterface {

    private static AppLogger log =
        new AppLogger(SmReportDefinitionsHibernateDao.class.getName());

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business
     *      .persistence.SmReportDefinitionsDaoInterface
     *      #save(de.escidoc.core.sm.business.persistence.hibernate.ReportDefinition)
     * 
     * @param reportDefinition
     *            The ReportDefinition Hibernate Object.
     * @throws SqlDatabaseSystemException
     *             e
     * 
     * @sm
     */
    public void save(final ReportDefinition reportDefinition)
        throws SqlDatabaseSystemException {
        super.save(reportDefinition);
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business
     *      .persistence.SmReportDefinitionsDaoInterface
     *      #update( de.escidoc.core.sm.business.persistence.hibernate.ReportDefinition)
     * 
     * @param reportDefinition
     *            The ReportDefinition Hibernate Object.
     * @throws SqlDatabaseSystemException
     *             e
     * 
     * @sm
     */
    public void update(final ReportDefinition reportDefinition)
        throws SqlDatabaseSystemException {
        super.update(reportDefinition);
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business
     *      .persistence.SmReportDefinitionsDaoInterface
     *      #delete( java.lang.Integer)
     * 
     * @param reportDefinition
     *            The ReportDefinition Hibernate Object.
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * 
     * @sm
     */
    public void delete(final ReportDefinition reportDefinition)
        throws SqlDatabaseSystemException {
        super.delete(reportDefinition);
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business
     *      .persistence.SmReportDefinitionsDaoInterface
     *      #retrieve( java.lang.Integer)
     * 
     * @param id
     *            The id of the ReportDefinition.
     * @return ReportDefinition as Hibernate Object
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * @throws ReportDefinitionNotFoundException
     *             Thrown if report-definition with given id was not found.
     * 
     * @sm
     */
    public ReportDefinition retrieve(final String id)
        throws SqlDatabaseSystemException, ReportDefinitionNotFoundException {
        ReportDefinition result = null;
        if (id != null) {
            try {
                result =
                    (ReportDefinition) 
                    getHibernateTemplate().get(ReportDefinition.class,
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
            throw new ReportDefinitionNotFoundException(
                    "ReportDefinition with id " + id + " was not found");
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business
     *      .persistence.SmReportDefinitionsDaoInterface
     *      #retrieveReportDefinitions()
     * 
     * @return Collection of ReportDefinitions as Hibernate Objects
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * 
     * @sm
     */
    public Collection<ReportDefinition> retrieveReportDefinitions()
        throws SqlDatabaseSystemException {
        final DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(ReportDefinition.class, "r");

            Collection<ReportDefinition> reportDefinitions =
                    getHibernateTemplate().findByCriteria(detachedCriteria);
            return reportDefinitions;
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business
     *      .persistence.SmReportDefinitionsDaoInterface
     *      #retrieveReportDefinitions( java.util.Collection)
     * 
     * @param scopeIds
     *            Collection of scopeIds
     * @param criteria
     *            The {@link String} containing the filter criteria as CQL query.
     * @param offset
     *            The index of the first result to be returned.
     * @param maxResults
     *            The maximal number of results to be returned.
     *
     * @return Collection of ReportDefinitions as Hibernate Objects
     * @throws InvalidSearchQueryException thrown if the given search query could
     *                                     not be translated into a SQL query
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     */
    public Collection<ReportDefinition> retrieveReportDefinitions(
        final Collection<String> scopeIds, final String criteria, final int offset,
        final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException {

        if ((scopeIds != null) && (!scopeIds.isEmpty())) {
            final DetachedCriteria detachedCriteria;

            if ((criteria != null) && (criteria.length() > 0)) {
                detachedCriteria = new ReportDefinitionFilter(criteria).toSql();
            }
            else {
                detachedCriteria =
                    DetachedCriteria.forClass(ReportDefinition.class, "r");
            }
            detachedCriteria.add(Restrictions.in("scope.id", scopeIds));

            Collection<ReportDefinition> reportDefinitions =
                getHibernateTemplate().findByCriteria(detachedCriteria, offset,
                    maxResults);
            return reportDefinitions;

        }
        return null;
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
