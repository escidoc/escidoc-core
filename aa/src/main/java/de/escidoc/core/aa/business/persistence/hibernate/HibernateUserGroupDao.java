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
import org.springframework.dao.DataAccessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sche
 * @spring.bean id="persistence.UserGroupDao"
 * @aa
 */
public class HibernateUserGroupDao extends AbstractHibernateDao
    implements UserGroupDaoInterface {
    private static final String QUERY_RETRIEVE_GRANTS_BY_GROUP_ID =
        "from "
            + RoleGrant.class.getName()
            + " g where g.userGroupByGroupId.id = ? order by role_id, object_id";

    private final Map<String, Object[]> criteriaMap;

    private final Map<String, String> propertiesNamesMap;

    private UserGroupFilter userGroupFilter;

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * Constructor to initialize filter-names with RoleFilter-Class.
     */
    public HibernateUserGroupDao() {
        try {
            userGroupFilter = new UserGroupFilter(null);
        }
        catch (InvalidSearchQueryException e) {
            // Dont do anything because null-query is given
        }
        criteriaMap = userGroupFilter.getCriteriaMap();
        propertiesNamesMap = userGroupFilter.getPropertyMap();
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
    public void delete(final UserGroup userGroup)
        throws SqlDatabaseSystemException {
        super.delete(userGroup);
    }

    @Override
    public UserGroup findUsergroupByLabel(final String label)
        throws SqlDatabaseSystemException {
        UserGroup result;
        try {
            result =
                (UserGroup) getUniqueResult(getHibernateTemplate()
                    .findByCriteria(
                        DetachedCriteria.forClass(UserGroup.class).add(
                            Restrictions.eq("label", label))));
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
     * @param userGroupMember
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #delete(de.escidoc.core.aa.business.persistence.UserGroupMember)
     * @aa
     */
    @Override
    public void delete(final UserGroupMember userGroupMember)
        throws SqlDatabaseSystemException {
        super.delete(userGroupMember);
    }

    /**
     * See Interface for functional description.
     * 
     * @param userGroup
     * @param role
     * @param objId
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #retrieveCurrentGrant(java.lang.String, EscidocRole,
     *      java.lang.String)
     * @aa
     */
    @Override
    public RoleGrant retrieveCurrentGrant(
        final UserGroup userGroup, final EscidocRole role, final String objId)
        throws SqlDatabaseSystemException {
        RoleGrant result = null;

        if ((userGroup != null) && (role != null)) {
            try {
                DetachedCriteria criteria =
                    DetachedCriteria.forClass(RoleGrant.class).add(
                        Restrictions.eq("userGroupByGroupId", userGroup)).add(
                        Restrictions.eq("escidocRole", role)).add(
                        Restrictions.isNull("revocationDate"));
                if (objId != null) {
                    criteria = criteria.add(Restrictions.eq("objectId", objId));
                }
                result =
                    (RoleGrant) getUniqueResult(getHibernateTemplate()
                        .findByCriteria(criteria));
            }
            catch (DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (IllegalStateException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (HibernateException e) {
                //noinspection ThrowableResultOfMethodCallIgnored
                throw new SqlDatabaseSystemException(
                    convertHibernateAccessException(e));
            }
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param grantId
     * @return
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #retrieveGrant(java.lang.String)
     * @aa
     */
    @Override
    public RoleGrant retrieveGrant(final String grantId)
        throws SqlDatabaseSystemException {
        RoleGrant result = null;

        if (grantId != null) {
            try {
                result =
                        getHibernateTemplate().get(RoleGrant.class,
                            grantId);
            }
            catch (DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (IllegalStateException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (HibernateException e) {
                //noinspection ThrowableResultOfMethodCallIgnored
                throw new SqlDatabaseSystemException(
                    convertHibernateAccessException(e));
            }
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param groupId
     * @return
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #retrieveGrants(java.lang.String)
     * @aa
     */
    @Override
    public List<RoleGrant> retrieveGrants(final String groupId)
        throws SqlDatabaseSystemException {

        List<RoleGrant> result = null;

        if (groupId != null) {
            try {
                result =
                    getHibernateTemplate().find(
                        QUERY_RETRIEVE_GRANTS_BY_GROUP_ID, groupId);
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
     * @param groupIds
     * @return
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #retrieveGrants(List)
     * @aa
     */
    @Override
    public Map<String, List<RoleGrant>> retrieveCurrentGrants(
        final List<String> groupIds) throws SqlDatabaseSystemException {

        List<RoleGrant> roleGrants;
        Map<String, List<RoleGrant>> orderedResult =
            new HashMap<String, List<RoleGrant>>();

        final DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(RoleGrant.class);
        detachedCriteria.add(Restrictions.in("groupId", groupIds.toArray()));
        detachedCriteria.add(Restrictions.isNull("revocationDate"));
        detachedCriteria.addOrder(Order.desc("objectId"));

        try {
            roleGrants =
                getHibernateTemplate().findByCriteria(detachedCriteria);
        }
        catch (DataAccessException e) {
            throw new SqlDatabaseSystemException(e);
        }
        if (roleGrants !=  null) {
            for (RoleGrant roleGrant : roleGrants) {
                if (orderedResult.get(roleGrant.getGroupId()) == null) {
                    orderedResult.put(roleGrant.getGroupId(),
                        new ArrayList<RoleGrant>());
                }
                orderedResult.get(roleGrant.getGroupId()).add(roleGrant);
            }
        }

        return orderedResult;
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
    public UserGroup retrieveUserGroup(final String groupId)
        throws SqlDatabaseSystemException {
        UserGroup result = null;

        if (groupId != null) {
            try {
                result =
                        getHibernateTemplate().get(UserGroup.class,
                            groupId);
            }
            catch (DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (IllegalStateException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (HibernateException e) {
                //noinspection ThrowableResultOfMethodCallIgnored
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
     *      #retrieveUserGroups(java.util.Map, int, int, String, ListSorting)
     * @aa
     */
    @Override
    public List<UserGroup> retrieveUserGroups(
        final Map<String, Object> criteria, final int offset,
        final int maxResults, final String orderBy, final ListSorting sorting)
        throws SqlDatabaseSystemException {
        List<UserGroup> result;
        final DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(UserGroup.class);
        final Map<String, Object> clonedCriterias =
            new HashMap<String, Object>(criteria);

        // ids
        final Set<String> userGroupIds = mergeSets(
            (Set<String>) clonedCriterias.remove(Constants.DC_IDENTIFIER_URI),
            (Set<String>) clonedCriterias.remove(Constants.FILTER_PATH_ID));

        if (userGroupIds != null) {
            detachedCriteria.add(Restrictions.in("id", userGroupIds.toArray()));
        }

        // active flag
        final String active = (String) clonedCriterias.remove(
                                        Constants.FILTER_ACTIVE);
        final String active1 = (String) clonedCriterias.remove(
                                    Constants.FILTER_PATH_ACTIVE);

        if (active != null) {
            detachedCriteria.add(Restrictions.eq("active", Boolean
                .valueOf(active)));
        }
        else if (active1 != null) {
            detachedCriteria.add(Restrictions.eq("active",
                Boolean.valueOf(active1)));
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
            result = new ArrayList<UserGroup>(0);
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param criterias
     * @param offset
     * @param maxResults
     * 
     * @return
     * @throws InvalidSearchQueryException
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #retrieveUserGroups(java.lang.String, int, int)
     */
    @Override
    public List<UserGroup> retrieveUserGroups(
        final String criterias, final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException {

        List<UserGroup> result;

        if ((criterias != null) && (criterias.length() > 0)) {
            result =
                getHibernateTemplate().findByCriteria(
                    new UserGroupFilter(criterias).toSql(), offset, maxResults);
        }
        else {
            try {
                final DetachedCriteria detachedCriteria =
                    DetachedCriteria.forClass(UserGroup.class);

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
     * @param criteria
     * 
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #retrieveUserGroupMembers(java.util.Map)
     * @aa
     */
    @Override
    public List<UserGroupMember> retrieveUserGroupMembers(
        final Map<String, Object> criteria) throws SqlDatabaseSystemException {
        List<UserGroupMember> result;
        final DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(UserGroupMember.class);
        final Map<String, Object> clonedCriterias =
            new HashMap<String, Object>(criteria);

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
        final String name = (String) clonedCriterias.remove(
            TripleStoreUtility.PROP_NAME);
        final String name1 = (String) clonedCriterias.remove(
            Constants.FILTER_PATH_NAME);

        if (name != null) {
            detachedCriteria.add(Restrictions.eq(XmlUtility.NAME_NAME, name));
        }
        else if (name1 != null) {
            detachedCriteria.add(Restrictions.eq(XmlUtility.NAME_NAME, name1));
        }

        // value
        final Set<String> values = mergeSets(
            (Set<String>) clonedCriterias.remove(Constants.FILTER_VALUE),
            (Set<String>) clonedCriterias.remove(Constants.FILTER_PATH_VALUE));

        if (values != null && !values.isEmpty()) {
            if (values.size() > 1) {
                detachedCriteria.add(Restrictions.in(XmlUtility.NAME_VALUE,
                    values.toArray()));
            }
            else {
                detachedCriteria.add(Restrictions.eq(XmlUtility.NAME_VALUE,
                    values.iterator().next()));
            }
        }

        if (clonedCriterias.isEmpty()) {
            try {
                result =
                    getHibernateTemplate().findByCriteria(detachedCriteria);
            }
            catch (DataAccessException e) {
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
     * @param userId
     * 
     * @return List
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #retrieveGrantsByUserId(String)
     * @aa
     */
    public List<RoleGrant> retrieveGrantsByUserId(final String userId)
        throws SqlDatabaseSystemException {

        return null;
    }

    /**
     * See Interface for functional description.
     * 
     * @param grant
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #save(de.escidoc.core.aa.business.persistence.RoleGrant)
     * @aa
     */
    @Override
    public void save(final RoleGrant grant) throws SqlDatabaseSystemException {
        super.save(grant);
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
    public void save(final UserGroup userGroup)
        throws SqlDatabaseSystemException {
        super.save(userGroup);
    }

    /**
     * See Interface for functional description.
     * 
     * @param userGroupMember
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #save(de.escidoc.core.aa.business.persistence.UserGroupMember)
     * @aa
     */
    @Override
    public void save(final UserGroupMember userGroupMember)
        throws SqlDatabaseSystemException {
        super.save(userGroupMember);
    }

    /**
     * See Interface for functional description.
     * 
     * @param grant
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #update(de.escidoc.core.aa.business.persistence.RoleGrant)
     * @aa
     */
    @Override
    public void update(final RoleGrant grant) throws SqlDatabaseSystemException {
        super.update(grant);
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
    public void update(final UserGroup userGroup)
        throws SqlDatabaseSystemException {
        super.update(userGroup);
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * See Interface for functional description.
     * 
     * @param identityInfo
     *            identityInfo
     * @return boolean
     * @throws SqlDatabaseSystemException
     *             e
     * @see de.escidoc.core.aa.business.persistence.UserGroupDaoInterface
     *      #userGroupExists(java.lang.String)
     * @aa
     */
    @Override
    public boolean userGroupExists(final String identityInfo)
        throws SqlDatabaseSystemException {

        boolean result = false;
        if (identityInfo != null) {
            try {
                // try identification by id or label
                DetachedCriteria criteria =
                    DetachedCriteria.forClass(UserGroup.class).add(
                        Restrictions.or(Restrictions.eq("id", identityInfo),
                            Restrictions.eq("label", identityInfo)));
                result =
                    !getHibernateTemplate().findByCriteria(criteria).isEmpty();
            }
            catch (DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (IllegalStateException e) {
                throw new SqlDatabaseSystemException(e);
            }
            catch (HibernateException e) {
                //noinspection ThrowableResultOfMethodCallIgnored
                throw new SqlDatabaseSystemException(
                    convertHibernateAccessException(e));
            }
        }
        return result;
    }

    /**
     * Wrapper of setSessionFactory to enable bean stuff generation for this
     * bean.
     * 
     * @param mySessionFactory
     *            The mySessionFactory to set.
     * @spring.property ref="eSciDoc.core.aa.SessionFactory"
     */
    public final void setMySessionFactory(final SessionFactory mySessionFactory) {
        super.setSessionFactory(mySessionFactory);
    }
}
