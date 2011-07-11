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
package de.escidoc.core.aa.business.persistence.hibernate;

import de.escidoc.core.aa.business.filter.RoleFilter;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.ScopeDef;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.persistence.hibernate.AbstractHibernateDao;
import de.escidoc.core.common.util.list.ListSorting;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Escidoc role data access object using hibernate.
 *
 * @author Torsten Tetteroo
 */
public class HibernateEscidocRoleDao extends AbstractHibernateDao implements EscidocRoleDaoInterface {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateEscidocRoleDao.class);

    private Map<String, Object[]> criteriaMap = new HashMap<String, Object[]>();

    private Map<String, String> propertiesNamesMap = new HashMap<String, String>();

    /**
     * Constructor to initialize filter-names with RoleFilter-Class.
     */
    public HibernateEscidocRoleDao() {
        try {
            final RoleFilter roleFilter = new RoleFilter(null);
            this.criteriaMap = roleFilter.getCriteriaMap();
            this.propertiesNamesMap = roleFilter.getPropertyMap();
        }
        catch (final InvalidSearchQueryException e) {
            // Dont do anything because null-query is given.
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Exception for null-query");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exception for null-query", e);
            }
        }
    }

    /**
     * See Interface for functional description.
     *
     * @see EscidocRoleDaoInterface #roleExists(java.lang.String)
     */
    @Override
    public boolean roleExists(final String identifier) throws SqlDatabaseSystemException {

        boolean result = false;
        if (identifier != null) {
            try {
                final DetachedCriteria criteria =
                    DetachedCriteria.forClass(EscidocRole.class).add(
                        Restrictions.or(Restrictions.eq("id", identifier), Restrictions.eq("roleName", identifier)));
                result = !getHibernateTemplate().findByCriteria(criteria).isEmpty();
            }
            catch (final DataAccessException e1) {
                throw new SqlDatabaseSystemException(e1);
            }
            catch (final IllegalStateException e1) {
                throw new SqlDatabaseSystemException(e1);
            }
            catch (final HibernateException e) {
                //noinspection ThrowableResultOfMethodCallIgnored
                throw new SqlDatabaseSystemException(convertHibernateAccessException(e)); // Ignore FindBugs
            }
        }
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @see EscidocRoleDaoInterface #deleteRole(de.escidoc.core.aa.business.persistence.EscidocRole)
     */
    @Override
    public void deleteRole(final EscidocRole role) throws SqlDatabaseSystemException {

        delete(role);
    }

    /**
     * See Interface for functional description.
     *
     * @see EscidocRoleDaoInterface #retrieveRole(java.lang.String)
     */
    @Override
    public EscidocRole retrieveRole(final String identifier) throws SqlDatabaseSystemException {

        EscidocRole result = null;
        if (identifier != null) {
            try {
                result = getHibernateTemplate().get(EscidocRole.class, identifier);
                if (result == null) {
                    result =
                        (EscidocRole) getUniqueResult(getHibernateTemplate().findByCriteria(
                            DetachedCriteria.forClass(EscidocRole.class).add(Restrictions.eq("roleName", identifier))));
                }
            }
            catch (final DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (final HibernateException e) {
                //noinspection ThrowableResultOfMethodCallIgnored
                throw new SqlDatabaseSystemException(convertHibernateAccessException(e)); // Ignore FindBugs
            }
            catch (final IllegalStateException e) {
                throw new SqlDatabaseSystemException(e);
            }
        }
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @see EscidocRoleDaoInterface #retrieveRoles(java.util.Map, int, int, java.lang.String,
     *      de.escidoc.core.common.util.list.ListSorting)
     */
    @Override
    public List<EscidocRole> retrieveRoles(
        final Map<String, Object> criterias, final int offset, final int maxResults, final String orderBy,
        final ListSorting sorting) throws SqlDatabaseSystemException {

        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(EscidocRole.class, "r");
        detachedCriteria.add(Restrictions.ne("id", EscidocRole.DEFAULT_USER_ROLE_ID));

        if (criterias != null && !criterias.isEmpty()) {
            // ids
            final Set<String> roleIds =
                mergeSets((Set<String>) criterias.remove(Constants.DC_IDENTIFIER_URI), (Set<String>) criterias
                    .remove(Constants.FILTER_PATH_ID));
            if (roleIds != null && !roleIds.isEmpty()) {
                detachedCriteria.add(Restrictions.in("id", roleIds.toArray()));
            }

            // limited
            final String limited = (String) criterias.remove("limited");
            if (limited != null) {
                if (Boolean.parseBoolean(limited)) {
                    detachedCriteria.add(Restrictions.isNotEmpty("scopeDefs"));
                }
                else {
                    detachedCriteria.add(Restrictions.isEmpty("scopeDefs"));
                }
            }

            // granted
            final String granted = (String) criterias.remove("granted");
            if (granted != null) {
                final DetachedCriteria subQuery = DetachedCriteria.forClass(RoleGrant.class, "rg");
                subQuery.setProjection(Projections.rowCount());
                subQuery.add(Restrictions.eqProperty("escidocRole.id", "r.id"));

                if (Boolean.parseBoolean(granted)) {
                    detachedCriteria.add(Subqueries.lt(0, subQuery));
                }
                else {
                    detachedCriteria.add(Subqueries.eq(0, subQuery));
                }
            }

            for (final Entry<String, Object[]> stringEntry : criteriaMap.entrySet()) {
                final Object criteriaValue = criterias.remove(stringEntry.getKey());
                if (criteriaValue != null) {
                    final Object[] parts = stringEntry.getValue();
                    if (parts[0].equals(COMPARE_EQ)) {
                        detachedCriteria.add(Restrictions.eq((String) parts[1], criteriaValue));
                    }
                    else {
                        detachedCriteria.add(Restrictions.like((String) parts[1], criteriaValue));
                    }
                }
            }
        }

        if (orderBy != null) {
            if (sorting == ListSorting.ASCENDING) {
                detachedCriteria.addOrder(Order.asc(propertiesNamesMap.get(orderBy)));
            }
            else if (sorting == ListSorting.DESCENDING) {
                detachedCriteria.addOrder(Order.desc(propertiesNamesMap.get(orderBy)));
            }
        }

        if (criterias != null && criterias.isEmpty()) {

            final List<EscidocRole> result;
            try {
                result = getHibernateTemplate().findByCriteria(detachedCriteria, offset, maxResults);
            }
            catch (final DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }

            return result;
        }
        else {
            // unsupported filter criteria has been found, therefore the result
            // list must be empty.
            return new ArrayList<EscidocRole>(0);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @see EscidocRoleDaoInterface #retrieveRoles(java.lang.String, int, int)
     */
    @Override
    public List<EscidocRole> retrieveRoles(final String criterias, final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException {

        final List<EscidocRole> result;

        if (criterias != null && criterias.length() > 0) {
            result = getHibernateTemplate().findByCriteria(new RoleFilter(criterias).toSql(), offset, maxResults);
        }
        else {
            try {
                final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(EscidocRole.class, "r");

                detachedCriteria.add(Restrictions.ne("id", EscidocRole.DEFAULT_USER_ROLE_ID));
                result = getHibernateTemplate().findByCriteria(detachedCriteria, offset, maxResults);
            }
            catch (final DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
        }
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @see EscidocRoleDaoInterface #saveOrUpdate(de.escidoc.core.aa.business.persistence.EscidocRole)
     */
    @Override
    public void saveOrUpdate(final EscidocRole role) throws SqlDatabaseSystemException {

        super.saveOrUpdate(role);
    }

    /**
     * See Interface for functional description.
     *
     * @see EscidocRoleDaoInterface #deleteScopeDef(de.escidoc.core.aa.business.persistence.ScopeDef)
     */
    @Override
    public void deleteScopeDef(final ScopeDef scopeDef) throws SqlDatabaseSystemException {

        delete(scopeDef);
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
