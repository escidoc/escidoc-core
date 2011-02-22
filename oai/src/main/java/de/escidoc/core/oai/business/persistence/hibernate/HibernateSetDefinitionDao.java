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
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author rof
 * @spring.bean id="persistence.SetDefinitionDao"
 */
public class HibernateSetDefinitionDao extends AbstractHibernateDao
    implements SetDefinitionDaoInterface {

    private final Map<String, Object[]> criteriaMap;

    private final Map<String, String> propertiesNamesMap;

    private SetDefinitionFilter setDefinitionFilter;

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * Constructor to initialize filter-names with RoleFilter-Class.
     */
    public HibernateSetDefinitionDao() {
        try {
            setDefinitionFilter = new SetDefinitionFilter(null);
        }
        catch (InvalidSearchQueryException e) {
            // Dont do anything because null-query is given
        }
        criteriaMap = setDefinitionFilter.getCriteriaMap();
        propertiesNamesMap = setDefinitionFilter.getPropertyMap();
    }

    /**
     * See Interface for functional description.
     * 
     * @param userGroup
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #delete(de.escidoc.core.aa.business.persistence.UserGroup)
     * @aa
     */
    @Override
    public void delete(final SetDefinition setDefinition)
        throws SqlDatabaseSystemException {
        super.delete(setDefinition);
    }

    @Override
    public SetDefinition findSetDefinitionBySpecification(
        final String specification) throws SqlDatabaseSystemException {
        SetDefinition result;
        try {
            result =
                (SetDefinition) getUniqueResult(getHibernateTemplate()
                    .findByCriteria(
                        DetachedCriteria.forClass(SetDefinition.class).add(
                            Restrictions.eq("specification", specification))));
        }
        catch (DataAccessException e) {
            throw new SqlDatabaseSystemException(e);
        }
        catch (HibernateException e) {
            //noinspection ThrowableResultOfMethodCallIgnored,ThrowableResultOfMethodCallIgnored
            throw new SqlDatabaseSystemException(
                convertHibernateAccessException(e));
        }
        catch (IllegalStateException e) {
            throw new SqlDatabaseSystemException(e);
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param groupId
     * @return
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #retrieveUserGroup(java.lang.String)
     * @aa
     */
    @Override
    public SetDefinition retrieveSetDefinition(final String id)
        throws SqlDatabaseSystemException {
        SetDefinition result = null;

        if (id != null) {
            try {
                result =
                        getHibernateTemplate().get(
                            SetDefinition.class, id);
            }
            catch (DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (IllegalStateException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (HibernateException e) {
                //noinspection ThrowableResultOfMethodCallIgnored,ThrowableResultOfMethodCallIgnored
                throw new SqlDatabaseSystemException(
                    convertHibernateAccessException(e));
            }
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param criteria
     * @param offset
     * @param maxResults
     * @param orderBy
     * @param sorting
     * 
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #retrieveSetDefinitions(java.util.Map, int, int, String, ListSorting)
     * @aa
     */
    @Override
    public List<SetDefinition> retrieveSetDefinitions(
        final Map<String, Object> criteria, final int offset,
        final int maxResults, final String orderBy, final ListSorting sorting)
        throws SqlDatabaseSystemException {
        List<SetDefinition> result;
        final DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(SetDefinition.class);
        final Map<String, Object> clonedCriterias =
            new HashMap<String, Object>(criteria);

        // ids
        final Set<String> setIds = mergeSets(
            (Set<String>) clonedCriterias.remove(Constants.DC_IDENTIFIER_URI),
            (Set<String>) clonedCriterias.remove(Constants.FILTER_PATH_ID));

        if (setIds != null) {
            detachedCriteria.add(Restrictions.in("id", setIds.toArray()));
        }

        for (String s : criteriaMap.keySet()) {
            final String key = s;
            final Object criteriaValue = clonedCriterias.remove(key);

            if (criteriaValue != null) {
                final Object[] parts = criteriaMap.get(key);
                if (parts[0].equals(COMPARE_EQ)) {
                    detachedCriteria.add(Restrictions.eq((String) parts[1],
                            criteriaValue));
                } else {
                    detachedCriteria.add(Restrictions.like((String) parts[1],
                            criteriaValue));
                }
            }
        }
        if (orderBy != null) {
            if (sorting == ListSorting.ASCENDING) {
                detachedCriteria.addOrder(Order.asc(propertiesNamesMap
                    .get(orderBy)));
            }
            else if (sorting == ListSorting.DESCENDING) {
                detachedCriteria.addOrder(Order.desc(propertiesNamesMap
                    .get(orderBy)));
            }
        }
        if (clonedCriterias.isEmpty()) {
            try {
                result =
                    getHibernateTemplate().findByCriteria(detachedCriteria,
                        offset, maxResults);
            }
            catch (DataAccessException e) {
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
     * @param criteria
     * @param offset
     * @param maxResults
     * @param orderBy
     * @param sorting
     * 
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #retrieveSetDefinitions(java.util.Map, int, int, String, ListSorting)
     */
    @Override
    public List<SetDefinition> retrieveSetDefinitions(
        final String criterias, final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException {

        List<SetDefinition> result;

        if ((criterias != null) && (criterias.length() > 0)) {
            result =
                getHibernateTemplate().findByCriteria(
                    new SetDefinitionFilter(criterias).toSql(), offset,
                    maxResults);
        }
        else {
            try {
                final DetachedCriteria detachedCriteria =
                    DetachedCriteria.forClass(SetDefinition.class);

                result =
                    getHibernateTemplate().findByCriteria(detachedCriteria,
                        offset, maxResults);
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
     * @param userGroup
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #save(de.escidoc.core.aa.business.persistence.UserGroup)
     * @aa
     */
    @Override
    public void save(final SetDefinition setDefinition)
        throws SqlDatabaseSystemException {
        super.save(setDefinition);
    }

    /**
     * See Interface for functional description.
     * 
     * @param userGroup
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #update(de.escidoc.core.aa.business.persistence.UserGroup)
     * @aa
     */
    @Override
    public void update(final SetDefinition setDefinition)
        throws SqlDatabaseSystemException {
        super.update(setDefinition);
    }

    // CHECKSTYLE:JAVADOC-ON



    /**
     * Wrapper of setSessionFactory to enable bean stuff generation for this
     * bean.
     * 
     * @param mySessionFactory
     *            The mySessionFactory to set.
     * @spring.property ref="eSciDoc.core.om.SessionFactory"
     */
    public final void setMySessionFactory(final SessionFactory mySessionFactory) {
        super.setSessionFactory(mySessionFactory);
    }
}
