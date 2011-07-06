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
package de.escidoc.core.aa.business.persistence.hibernate;

import de.escidoc.core.aa.business.filter.UserGroupFilter;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserGroup;
import de.escidoc.core.aa.business.persistence.UserGroupDaoInterface;
import de.escidoc.core.aa.business.persistence.UserGroupMember;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.persistence.hibernate.AbstractHibernateDao;
import de.escidoc.core.common.util.list.ListSorting;
import de.escidoc.core.common.util.xml.XmlUtility;
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
 * @author André Schenk
 */
public class HibernateUserGroupDao extends AbstractHibernateDao implements UserGroupDaoInterface {

    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateUserGroupDao.class);

    private static final String QUERY_RETRIEVE_GRANTS_BY_GROUP_ID =
        "from " + RoleGrant.class.getName() + " g where g.userGroupByGroupId.id = ? order by role_id, object_id";

    private Map<String, Object[]> criteriaMap = new HashMap<String, Object[]>();

    private Map<String, String> propertiesNamesMap = new HashMap<String, String>();

    /**
     * Constructor to initialize filter-names with RoleFilter-Class.
     */
    public HibernateUserGroupDao() {
        try {
            final UserGroupFilter userGroupFilter = new UserGroupFilter(null);
            this.criteriaMap = userGroupFilter.getCriteriaMap();
            this.propertiesNamesMap = userGroupFilter.getPropertyMap();
        }
        catch (final InvalidSearchQueryException e) {
            // Dont do anything because null-query is given.
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Expected exception for null-query");
            }
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Expected exception for null-query", e);
            }
        }
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #delete(de.escidoc.core.aa.business.persistence.UserGroup)
     */
    @Override
    public void delete(final UserGroup userGroup) throws SqlDatabaseSystemException {
        super.delete(userGroup);
    }

    @Override
    public UserGroup findUsergroupByLabel(final String label) throws SqlDatabaseSystemException {
        final UserGroup result;
        try {
            result =
                (UserGroup) getUniqueResult(getHibernateTemplate().findByCriteria(
                    DetachedCriteria.forClass(UserGroup.class).add(Restrictions.eq("label", label))));
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
     * @see UserGroupDaoInterface #delete(de.escidoc.core.aa.business.persistence.UserGroupMember)
     */
    @Override
    public void delete(final UserGroupMember userGroupMember) throws SqlDatabaseSystemException {
        super.delete(userGroupMember);
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #retrieveCurrentGrant(java.lang.String, EscidocRole, java.lang.String)
     */
    @Override
    public RoleGrant retrieveCurrentGrant(final UserGroup userGroup, final EscidocRole role, final String objId)
        throws SqlDatabaseSystemException {
        RoleGrant result = null;

        if (userGroup != null && role != null) {
            try {
                DetachedCriteria criteria =
                    DetachedCriteria
                        .forClass(RoleGrant.class).add(Restrictions.eq("userGroupByGroupId", userGroup)).add(
                            Restrictions.eq("escidocRole", role)).add(Restrictions.isNull("revocationDate"));
                if (objId != null) {
                    criteria = criteria.add(Restrictions.eq("objectId", objId));
                }
                result = (RoleGrant) getUniqueResult(getHibernateTemplate().findByCriteria(criteria));
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
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #retrieveGrant(java.lang.String)
     */
    @Override
    public RoleGrant retrieveGrant(final String grantId) throws SqlDatabaseSystemException {
        RoleGrant result = null;

        if (grantId != null) {
            try {
                result = getHibernateTemplate().get(RoleGrant.class, grantId);
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
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #retrieveGrants(java.lang.String)
     */
    @Override
    public List<RoleGrant> retrieveGrants(final String groupId) throws SqlDatabaseSystemException {

        List<RoleGrant> result = null;

        if (groupId != null) {
            try {
                result = getHibernateTemplate().find(QUERY_RETRIEVE_GRANTS_BY_GROUP_ID, groupId);
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
     * @see UserGroupDaoInterface #retrieveGrants(List)
     */
    @Override
    public Map<String, List<RoleGrant>> retrieveCurrentGrants(final List<String> groupIds)
        throws SqlDatabaseSystemException {

        final Map<String, List<RoleGrant>> orderedResult = new HashMap<String, List<RoleGrant>>();

        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(RoleGrant.class);
        detachedCriteria.add(Restrictions.in("groupId", groupIds.toArray()));
        detachedCriteria.add(Restrictions.isNull("revocationDate"));
        detachedCriteria.addOrder(Order.desc("objectId"));

        final List<RoleGrant> roleGrants;
        try {
            roleGrants = getHibernateTemplate().findByCriteria(detachedCriteria);
        }
        catch (final DataAccessException e) {
            throw new SqlDatabaseSystemException(e);
        }
        if (roleGrants != null) {
            for (final RoleGrant roleGrant : roleGrants) {
                if (orderedResult.get(roleGrant.getGroupId()) == null) {
                    orderedResult.put(roleGrant.getGroupId(), new ArrayList<RoleGrant>());
                }
                orderedResult.get(roleGrant.getGroupId()).add(roleGrant);
            }
        }

        return orderedResult;
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #retrieveUserGroup(java.lang.String)
     */
    @Override
    public UserGroup retrieveUserGroup(final String groupId) throws SqlDatabaseSystemException {
        UserGroup result = null;

        if (groupId != null) {
            try {
                result = getHibernateTemplate().get(UserGroup.class, groupId);
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
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #retrieveUserGroups(java.util.Map, int, int, String, ListSorting)
     */
    @Override
    public List<UserGroup> retrieveUserGroups(
        final Map<String, Object> criteria, final int offset, final int maxResults, final String orderBy,
        final ListSorting sorting) throws SqlDatabaseSystemException {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(UserGroup.class);
        final Map<String, Object> clonedCriterias = new HashMap<String, Object>(criteria);

        // ids
        final Set<String> userGroupIds =
            mergeSets((Set<String>) clonedCriterias.remove(Constants.DC_IDENTIFIER_URI), (Set<String>) clonedCriterias
                .remove(Constants.FILTER_PATH_ID));

        if (userGroupIds != null) {
            detachedCriteria.add(Restrictions.in("id", userGroupIds.toArray()));
        }

        // active flag
        final String active = (String) clonedCriterias.remove(Constants.FILTER_ACTIVE);
        final String active1 = (String) clonedCriterias.remove(Constants.FILTER_PATH_ACTIVE);

        if (active != null) {
            detachedCriteria.add(Restrictions.eq("active", Boolean.valueOf(active)));
        }
        else if (active1 != null) {
            detachedCriteria.add(Restrictions.eq("active", Boolean.valueOf(active1)));
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
        final List<UserGroup> result;
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
            result = new ArrayList<UserGroup>(0);
        }
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #retrieveUserGroups(java.lang.String, int, int)
     */
    @Override
    public List<UserGroup> retrieveUserGroups(final String criterias, final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException {

        final List<UserGroup> result;

        if (criterias != null && criterias.length() > 0) {
            result = getHibernateTemplate().findByCriteria(new UserGroupFilter(criterias).toSql(), offset, maxResults);
        }
        else {
            try {
                final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(UserGroup.class);

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
     * @see UserGroupDaoInterface #retrieveUserGroupMembers(java.util.Map)
     */
    @Override
    public List<UserGroupMember> retrieveUserGroupMembers(final Map<String, Object> criteria)
        throws SqlDatabaseSystemException {
        final DetachedCriteria detachedCriteria = DetachedCriteria.forClass(UserGroupMember.class);
        final Map<String, Object> clonedCriterias = new HashMap<String, Object>(criteria);

        // type
        final String type = (String) clonedCriterias.remove(Constants.FILTER_TYPE);
        final String type1 = (String) clonedCriterias.remove(Constants.FILTER_PATH_TYPE);

        if (type != null) {
            detachedCriteria.add(Restrictions.eq(XmlUtility.NAME_TYPE, type));
        }
        else if (type1 != null) {
            detachedCriteria.add(Restrictions.eq(XmlUtility.NAME_TYPE, type1));
        }

        // name
        final String name = (String) clonedCriterias.remove(TripleStoreUtility.PROP_NAME);
        final String name1 = (String) clonedCriterias.remove(Constants.FILTER_PATH_NAME);

        if (name != null) {
            detachedCriteria.add(Restrictions.eq(XmlUtility.NAME_NAME, name));
        }
        else if (name1 != null) {
            detachedCriteria.add(Restrictions.eq(XmlUtility.NAME_NAME, name1));
        }

        // value
        final Set<String> values =
            mergeSets((Set<String>) clonedCriterias.remove(Constants.FILTER_VALUE), (Set<String>) clonedCriterias
                .remove(Constants.FILTER_PATH_VALUE));

        if (values != null && !values.isEmpty()) {
            if (values.size() > 1) {
                detachedCriteria.add(Restrictions.in(XmlUtility.NAME_VALUE, values.toArray()));
            }
            else {
                detachedCriteria.add(Restrictions.eq(XmlUtility.NAME_VALUE, values.iterator().next()));
            }
        }

        final List<UserGroupMember> result;
        if (clonedCriterias.isEmpty()) {
            try {
                result = getHibernateTemplate().findByCriteria(detachedCriteria);
            }
            catch (final DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
        }
        else {
            // unsupported filter criteria has been found, therefore the result
            // list must be empty.
            result = new ArrayList<UserGroupMember>(0);
        }
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #save(de.escidoc.core.aa.business.persistence.RoleGrant)
     */
    @Override
    public void save(final RoleGrant grant) throws SqlDatabaseSystemException {
        super.save(grant);
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #save(de.escidoc.core.aa.business.persistence.UserGroup)
     */
    @Override
    public void save(final UserGroup userGroup) throws SqlDatabaseSystemException {
        super.save(userGroup);
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #save(de.escidoc.core.aa.business.persistence.UserGroupMember)
     */
    @Override
    public void save(final UserGroupMember userGroupMember) throws SqlDatabaseSystemException {
        super.save(userGroupMember);
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #update(de.escidoc.core.aa.business.persistence.RoleGrant)
     */
    @Override
    public void update(final RoleGrant grant) throws SqlDatabaseSystemException {
        super.update(grant);
    }

    /**
     * See Interface for functional description.
     *
     * @see UserGroupDaoInterface #update(de.escidoc.core.aa.business.persistence.UserGroup)
     */
    @Override
    public void update(final UserGroup userGroup) throws SqlDatabaseSystemException {
        super.update(userGroup);
    }

    /**
     * See Interface for functional description.
     *
     * @param identityInfo identityInfo
     * @return boolean
     * @throws SqlDatabaseSystemException e
     * @see UserGroupDaoInterface #userGroupExists(java.lang.String)
     */
    @Override
    public boolean userGroupExists(final String identityInfo) throws SqlDatabaseSystemException {

        boolean result = false;
        if (identityInfo != null) {
            try {
                // try identification by id or label
                final DetachedCriteria criteria =
                    DetachedCriteria.forClass(UserGroup.class).add(
                        Restrictions.or(Restrictions.eq("id", identityInfo), Restrictions.eq("label", identityInfo)));
                result = !getHibernateTemplate().findByCriteria(criteria).isEmpty();
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
        return result;
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
