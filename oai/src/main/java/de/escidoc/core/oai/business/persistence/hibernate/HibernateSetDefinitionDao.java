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
package de.escidoc.core.oai.business.persistence.hibernate;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.persistence.hibernate.AbstractHibernateDao;
import de.escidoc.core.common.util.list.ListSorting;
import de.escidoc.core.oai.business.filter.SetDefinitionFilter;
import de.escidoc.core.oai.business.persistence.SetDefinition;
import de.escidoc.core.oai.business.persistence.SetDefinitionDaoInterface;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
 * @author Rozita Friedman
 */
public class HibernateSetDefinitionDao extends AbstractHibernateDao implements SetDefinitionDaoInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateSetDefinitionDao.class);

    private Map<String, Object[]> criteriaMap = new HashMap<String, Object[]>();

    private Map<String, String> propertiesNamesMap = new HashMap<String, String>();

    /**
     * Constructor to initialize filter-names with RoleFilter-Class.
     */
    public HibernateSetDefinitionDao() {
        try {
            final SetDefinitionFilter setDefinitionFilter = new SetDefinitionFilter(null);
            this.criteriaMap = setDefinitionFilter.getCriteriaMap();
            this.propertiesNamesMap = setDefinitionFilter.getPropertyMap();
        }
        catch (final InvalidSearchQueryException e) {
            // Dont do anything because null-query is given
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
     * @see UserGroupDaoInterface #delete(de.escidoc.core.aa.business.persistence.UserGroup)
     */
    @Override
    public void delete(final SetDefinition setDefinition) throws SqlDatabaseSystemException {
        super.delete(setDefinition);
    }

    @Override
    public SetDefinition findSetDefinitionBySpecification(final String specification) throws SqlDatabaseSystemException {
        final SetDefinition result;
        try {
            result =
                (SetDefinition) getUniqueResult(getHibernateTemplate()
                    .findByCriteria(
                        DetachedCriteria.forClass(SetDefinition.class).add(
                            Restrictions.eq("specification", specification))));
        }
        catch (final DataAccessException e) {
            throw new SqlDatabaseSystemException(e);
        }
        catch (final HibernateException e) {
            // noinspection ThrowableResultOfMethodCallIgnored
            throw new SqlDatabaseSystemException(convertHibernateAccessException(e)); // Ignore FindBugs
        }
        catch (final IllegalStateException e) {
            throw new SqlDatabaseSystemException(e);
        }
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #retrieveUserGroup(java.lang.String)
     */
    @Override
    public SetDefinition retrieveSetDefinition(final String id) throws SqlDatabaseSystemException {
        SetDefinition result = null;

        if (id != null) {
            try {
                result = getHibernateTemplate().get(SetDefinition.class, id);
            }
            catch (final DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (final IllegalStateException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (final HibernateException e) {
                // noinspection ThrowableResultOfMethodCallIgnored
                throw new SqlDatabaseSystemException(convertHibernateAccessException(e)); // Ignore FindBugs
            }
        }
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #retrieveSetDefinitions(java.util.Map, int, int, String, ListSorting)
     */
    @Override
    public List<SetDefinition> retrieveSetDefinitions(
        final Map<String, Object> criteria, final int offset, final int maxResults, final String orderBy,
        final ListSorting sorting) throws SqlDatabaseSystemException {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(SetDefinition.class);
        final Map<String, Object> clonedCriterias = new HashMap<String, Object>(criteria);

        // ids
        final Set<String> setIds =
            mergeSets((Set<String>) clonedCriterias.remove(Constants.DC_IDENTIFIER_URI), (Set<String>) clonedCriterias
                .remove(Constants.FILTER_PATH_ID));

        if (setIds != null) {
            detachedCriteria.add(Restrictions.in("id", setIds.toArray()));
        }

        for (final Entry<String, Object[]> stringEntry : criteriaMap.entrySet()) {
            final Object criteriaValue = clonedCriterias.remove(stringEntry.getKey());
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
        if (orderBy != null) {
            if (sorting == ListSorting.ASCENDING) {
                detachedCriteria.addOrder(Order.asc(propertiesNamesMap.get(orderBy)));
            }
            else if (sorting == ListSorting.DESCENDING) {
                detachedCriteria.addOrder(Order.desc(propertiesNamesMap.get(orderBy)));
            }
        }
        final List<SetDefinition> result;
        if (clonedCriterias.isEmpty()) {
            try {
                result = getHibernateTemplate().findByCriteria(detachedCriteria, offset, maxResults);
            }
            catch (final DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
        }
        else {
            // unsupported filter criteria has been found, therefore the result
            // list must be empty.
            result = new ArrayList<SetDefinition>(0);
        }
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #retrieveSetDefinitions(java.util.Map, int, int, String, ListSorting)
     */
    @Override
    public List<SetDefinition> retrieveSetDefinitions(final String criterias, final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException {

        final List<SetDefinition> result;

        if (criterias != null && criterias.length() > 0) {
            result =
                getHibernateTemplate().findByCriteria(new SetDefinitionFilter(criterias).toSql(), offset, maxResults);
        }
        else {
            try {
                final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(SetDefinition.class);

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
     * @see UserGroupDaoInterface #save(de.escidoc.core.aa.business.persistence.UserGroup)
     */
    @Override
    public void save(final SetDefinition setDefinition) throws SqlDatabaseSystemException {
        super.save(setDefinition);
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #update(de.escidoc.core.aa.business.persistence.UserGroup)
     */
    @Override
    public void update(final SetDefinition setDefinition) throws SqlDatabaseSystemException {
        super.update(setDefinition);
    }

    /**
     * Wrapper of setSessionFactory to enable bean stuff generation for this bean.
     *
     * @param mySessionFactory The mySessionFactory to set.
     */
    public final void setMySessionFactory(final SessionFactory mySessionFactory) {
        setSessionFactory(mySessionFactory);
    }
}
