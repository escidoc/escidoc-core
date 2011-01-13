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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.dao.DataAccessException;

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

/**
 * Escidoc role data access object using hibernate.
 * 
 * @spring.bean id="persistence.EscidocRoleDao"
 * @author TTE
 * 
 */
public class HibernateEscidocRoleDao extends AbstractHibernateDao
    implements EscidocRoleDaoInterface {

    private final Map<String, Object[]> CRITERIA_MAP;

    private final Map<String, String> PROPERTIES_NAMES_MAP;
    
    private RoleFilter roleFilter;

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * Constructor to initialize filter-names with RoleFilter-Class.
     */
    public HibernateEscidocRoleDao() {
        try {
            roleFilter = new RoleFilter(null);
        } catch (InvalidSearchQueryException e) {
            //Dont do anything because null-query is given
        }
        CRITERIA_MAP = roleFilter.getCriteriaMap();
        PROPERTIES_NAMES_MAP = roleFilter.getPropertyMap();
    }

    /**
     * See Interface for functional description.
     * 
     * @param identifier
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface
     *      #roleExists(java.lang.String)
     * @aa
     */
    public boolean roleExists(final String identifier)
        throws SqlDatabaseSystemException {

        boolean result = false;
        if (identifier != null) {
            try {
                DetachedCriteria criteria =
                    DetachedCriteria.forClass(EscidocRole.class).add(
                        Restrictions.or(Restrictions.eq("id", identifier),
                            Restrictions.eq("roleName", identifier)));
                result =
                    !getHibernateTemplate().findByCriteria(criteria).isEmpty();
            }
            catch (DataAccessException e1) {
                throw new SqlDatabaseSystemException(e1);
            }
            catch (IllegalStateException e1) {
                throw new SqlDatabaseSystemException(e1);
            }
            catch (HibernateException e) {
                throw new SqlDatabaseSystemException(
                    convertHibernateAccessException(e));
            }
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param role
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface
     *      #deleteRole(de.escidoc.core.aa.business.persistence.EscidocRole)
     * @aa
     */
    public void deleteRole(final EscidocRole role)
        throws SqlDatabaseSystemException {

        super.delete(role);
    }

    /**
     * See Interface for functional description.
     * 
     * @param identifier
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface
     *      #retrieveRole(java.lang.String)
     * @aa
     */
    public EscidocRole retrieveRole(final String identifier)
        throws SqlDatabaseSystemException {

        EscidocRole result = null;
        if (identifier != null) {
            try {
                result =
                    (EscidocRole) getHibernateTemplate().get(EscidocRole.class,
                        identifier);
                if (result == null) {
                    result =
                        (EscidocRole) getUniqueResult(getHibernateTemplate()
                            .findByCriteria(
                                DetachedCriteria
                                    .forClass(EscidocRole.class)
                                    .add(
                                        Restrictions.eq("roleName", identifier))));
                }
            }
            catch (DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (HibernateException e) {
                throw new SqlDatabaseSystemException(
                    convertHibernateAccessException(e));
            }
            catch (IllegalStateException e) {
                throw new SqlDatabaseSystemException(e);
            }
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param criterias
     * @param offset
     * @param maxResults
     * @param orderBy
     * @param sorting
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface
     *      #retrieveRoles(java.util.Map, int, int, java.lang.String,
     *      de.escidoc.core.common.util.list.ListSorting)
     * @aa
     */
    public List<EscidocRole> retrieveRoles(
        final Map<String, Object> criterias, final int offset,
        final int maxResults, final String orderBy, final ListSorting sorting)
        throws SqlDatabaseSystemException {

        List<EscidocRole> result = null;
        final DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(EscidocRole.class, "r");
        detachedCriteria.add(Restrictions.ne("id",
            EscidocRole.DEFAULT_USER_ROLE_ID));

        if (criterias != null && !criterias.isEmpty()) {
            // ids
            final Set<String> roleIds = mergeSets(
                (Set<String>) criterias.remove(Constants.DC_IDENTIFIER_URI), 
                (Set<String>) criterias.remove(Constants.FILTER_PATH_ID));
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
                DetachedCriteria subQuery =
                    DetachedCriteria.forClass(RoleGrant.class, "rg");
                subQuery.setProjection(Projections.rowCount());
                subQuery.add(Restrictions.eqProperty("escidocRole.id", "r.id"));

                if (Boolean.parseBoolean(granted)) {
                    detachedCriteria.add(Subqueries.lt(0, subQuery));
                }
                else {
                    detachedCriteria.add(Subqueries.eq(0, subQuery));
                }
            }

            Iterator<String> keys = CRITERIA_MAP.keySet().iterator();
            while (keys.hasNext()) {
                final String key = keys.next();
                final Object criteriaValue = criterias.remove(key);
                if (criteriaValue != null) {
                    final Object[] parts = CRITERIA_MAP.get(key);
                    if (parts[0].equals(COMPARE_EQ)) {
                        detachedCriteria.add(Restrictions.eq((String) parts[1],
                            criteriaValue));
                    }
                    else {
                        detachedCriteria.add(Restrictions.like(
                            (String) parts[1], criteriaValue));
                    }
                }
            }
        }

        if (orderBy != null) {
            if (sorting == ListSorting.ASCENDING) {
                detachedCriteria.addOrder(Order.asc(PROPERTIES_NAMES_MAP
                    .get(orderBy)));
            }
            else if (sorting == ListSorting.DESCENDING) {
                detachedCriteria.addOrder(Order.desc(PROPERTIES_NAMES_MAP
                    .get(orderBy)));
            }
        }

        if (criterias.isEmpty()) {

            try {
                result =
                    getHibernateTemplate().findByCriteria(detachedCriteria,
                        offset, maxResults);
            }
            catch (DataAccessException e) {
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
     * @param criterias
     * @param offset
     * @param maxResults
     * @return
     * @throws InvalidSearchQueryException
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface
     *      #retrieveRoles(java.lang.String, int, int)
     */
    public List<EscidocRole> retrieveRoles(
        final String criterias, final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException {

        List<EscidocRole> result = null;

        if ((criterias != null) && (criterias.length() > 0)) {
            result = getHibernateTemplate().findByCriteria(
                new RoleFilter(criterias).toSql(), offset, maxResults);
        }
        else {
            try {
                final DetachedCriteria detachedCriteria =
                    DetachedCriteria.forClass(EscidocRole.class, "r");

                detachedCriteria.add(Restrictions.ne("id",
                    EscidocRole.DEFAULT_USER_ROLE_ID));
                result = getHibernateTemplate().findByCriteria(
                    detachedCriteria, offset, maxResults);
            }
            catch (DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param role
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface
     *      #saveOrUpdate(de.escidoc.core.aa.business.persistence.EscidocRole)
     * @aa
     */
    public void saveOrUpdate(final EscidocRole role)
        throws SqlDatabaseSystemException {

        super.saveOrUpdate(role);
    }

    /**
     * See Interface for functional description.
     * 
     * @param scopeDef
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface
     *      #deleteScopeDef(de.escidoc.core.aa.business.persistence.ScopeDef)
     * @aa
     */
    public void deleteScopeDef(final ScopeDef scopeDef)
        throws SqlDatabaseSystemException {

        super.delete(scopeDef);
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Wrapper of setSessionFactory to enable bean stuff generation for this
     * bean.
     * 
     * @param mySessionFactory
     *            The sessionFactory to set.
     * @spring.property ref="eSciDoc.core.aa.SessionFactory"
     * @aa
     */
    public final void setMySessionFactory(final SessionFactory mySessionFactory) {

        super.setSessionFactory(mySessionFactory);
    }

}
