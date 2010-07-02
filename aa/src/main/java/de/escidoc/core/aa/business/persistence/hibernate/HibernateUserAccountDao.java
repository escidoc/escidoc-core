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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.springframework.dao.DataAccessException;
import org.springframework.security.userdetails.UserDetails;

import de.escidoc.core.aa.business.cache.PoliciesCache;
import de.escidoc.core.aa.business.filter.RoleGrantFilter;
import de.escidoc.core.aa.business.filter.UserAccountFilter;
import de.escidoc.core.aa.business.interfaces.UserGroupHandlerInterface;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.aa.business.persistence.UserAccount;
import de.escidoc.core.aa.business.persistence.UserAccountDaoInterface;
import de.escidoc.core.aa.business.persistence.UserAttribute;
import de.escidoc.core.aa.business.persistence.UserLoginData;
import de.escidoc.core.aa.business.persistence.UserPreference;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.persistence.hibernate.AbstractHibernateDao;
import de.escidoc.core.common.util.configuration.EscidocConfiguration;
import de.escidoc.core.common.util.list.ListSorting;
import de.escidoc.core.common.util.service.EscidocUserDetails;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * @author MSC
 * @spring.bean id="persistence.UserAccountDao"
 * @aa
 */
public class HibernateUserAccountDao extends AbstractHibernateDao
    implements UserAccountDaoInterface {

    private static final String FILTER_ACTIVE =
        Constants.PROPERTIES_NS_URI + XmlUtility.NAME_ACTIVE;

    public static final String PROP_ORGANIZATIONAL_UNIT =
        Constants.STRUCTURAL_RELATIONS_NS_URI
            + XmlUtility.NAME_ORGANIZATIONAL_UNIT;

    public static final String PROP_LOGINNAME =
        Constants.PROPERTIES_NS_URI + XmlUtility.NAME_LOGIN_NAME;

    private static final String QUERY_RETRIEVE_LOGINDATA_BY_USER_ID =
        "from UserLoginData u where u.userAccount.id = ?";

    private static final String QUERY_RETRIEVE_LOGINDATA_BY_HANDLE =
        "from UserLoginData u where u.handle = ?";

    private static final String QUERY_RETRIEVE_GRANTS_BY_ROLE =
        "from "
            + RoleGrant.class.getName()
            + " g where g.escidocRole = ? "
            + "order by g.objectId, g.userAccountByUserId";

    private static final String QUERY_RETRIEVE_GRANTS_BY_USER_ID =
        "from "
            + RoleGrant.class.getName()
            + " g where g.userAccountByUserId.id = ? order by role_id, object_id";

    private static final String QUERY_RETRIEVE_PREFERENCES_BY_USER_ID =
        "from "
            + UserPreference.class.getName()
            + " g where g.userAccountByUserId.id = ? ";

    private static final String QUERY_RETRIEVE_USER_ACCOUNT_BY_HANDLE =
        "select u from UserAccount u, UserLoginData l where l.userAccount = u"
            + " and l.handle = ? and ? <= l.expiryts";

    private static final String QUERY_RETRIEVE_USER_ACCOUNT_BY_LOGINNAME =
        "from UserAccount u where u.loginname = ?";

    private static final Map<String, Object[]> CRITERIA_MAP =
        new HashMap<String, Object[]>();

    private static final Map<String, String> PROPERTIES_NAMES_MAP =
        new HashMap<String, String>();

    static {
        CRITERIA_MAP.put(PROP_LOGINNAME, new Object[] { COMPARE_LIKE,
            "loginname" });
        CRITERIA_MAP.put(TripleStoreUtility.PROP_NAME, new Object[] {
            COMPARE_LIKE, "name" });
        CRITERIA_MAP.put(TripleStoreUtility.PROP_CREATED_BY_ID, new Object[] {
            COMPARE_EQ, "userAccountByCreatorId.id" });
        CRITERIA_MAP.put(TripleStoreUtility.PROP_MODIFIED_BY_ID, new Object[] {
            COMPARE_EQ, "userAccountByModifiedById.id" });
        CRITERIA_MAP.put(Constants.PROPERTIES_NS_URI
            + XmlUtility.NAME_CREATION_DATE,
            new String[] { "r.creationDate = " });

        PROPERTIES_NAMES_MAP.put(PROP_LOGINNAME, "loginname");
        PROPERTIES_NAMES_MAP.put(TripleStoreUtility.PROP_NAME, "name");
        PROPERTIES_NAMES_MAP.put(TripleStoreUtility.PROP_CREATED_BY_ID,
            "userAccountByCreatorId.id");
        PROPERTIES_NAMES_MAP.put(TripleStoreUtility.PROP_MODIFIED_BY_ID,
            "userAccountByModifiedById.id");
        PROPERTIES_NAMES_MAP.put(Constants.DC_IDENTIFIER_URI, "id");
        PROPERTIES_NAMES_MAP.put(Constants.FILTER_USER, "userId");
        PROPERTIES_NAMES_MAP.put(Constants.FILTER_GROUP, "groupId");
        PROPERTIES_NAMES_MAP.put(Constants.FILTER_ROLE, "roleId");
        PROPERTIES_NAMES_MAP.put(Constants.FILTER_ASSIGNED_ON, "objectId");
        PROPERTIES_NAMES_MAP.put(Constants.FILTER_CREATED_BY, "creatorId");
        PROPERTIES_NAMES_MAP.put(Constants.FILTER_REVOKED_BY, "revokerId");
        PROPERTIES_NAMES_MAP.put(
            Constants.FILTER_REVOCATION_DATE_FROM, "revocationDate");
        PROPERTIES_NAMES_MAP.put(
            Constants.FILTER_REVOCATION_DATE_TO, "revocationDate");
        PROPERTIES_NAMES_MAP.put(
            Constants.FILTER_CREATION_DATE_FROM, "creationDate");
        PROPERTIES_NAMES_MAP.put(
            Constants.FILTER_CREATION_DATE_TO, "creationDate");
    
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param identityInfo
     * @return
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #userAccountExists(java.lang.String)
     * @aa
     */
    public boolean userAccountExists(final String identityInfo)
        throws SqlDatabaseSystemException {

        boolean result = false;
        if (identityInfo != null) {
            try {
                // try identification by id or login name
                DetachedCriteria criteria =
                    DetachedCriteria.forClass(UserAccount.class).add(
                        Restrictions.or(Restrictions.eq("id", identityInfo),
                            Restrictions.eq("loginname", identityInfo)));
                result =
                    !getHibernateTemplate().findByCriteria(criteria).isEmpty();
                if (!result) {
                    // try identification by handle
                    result =
                        !getHibernateTemplate().find(
                            QUERY_RETRIEVE_USER_ACCOUNT_BY_HANDLE,
                            new Object[] { identityInfo,
                                System.currentTimeMillis() }).isEmpty();
                }
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
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param grantId
     * @return boolean true or false
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #grantExists(java.lang.String)
     * @aa
     */
    public boolean grantExists(final String grantId)
        throws SqlDatabaseSystemException {

        boolean result = false;
        if (grantId != null) {
            try {
                RoleGrant roleGrant =
                    (RoleGrant) getHibernateTemplate().get(RoleGrant.class,
                        grantId);
                if (roleGrant != null) {
                    result = true;
                }
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
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param identityInfo
     * @return
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveUserAccount(java.lang.String)
     * @aa
     */
    public UserAccount retrieveUserAccount(final String identityInfo)
        throws SqlDatabaseSystemException {

        UserAccount result = null;

        if (identityInfo != null) {
            try {
                result = retrieveUserAccountById(identityInfo);
                if (result == null) {
                    result = retrieveUserAccountByHandle(identityInfo);
                    if (result == null) {
                        result = retrieveUserAccountByLoginName(identityInfo);
                    }
                }
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
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @see UserAccountDaoInterface#retrieveUserAccountById(java.lang.String)
     * @aa
     */
    public UserAccount retrieveUserAccountById(final String id)
        throws SqlDatabaseSystemException {

        UserAccount result = null;
        if (id != null) {
            try {
                result =
                    (UserAccount) getHibernateTemplate().get(UserAccount.class,
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
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param loginName
     * @return
     * @see UserAccountDaoInterface#retrieveUserAccountByLoginName(
     *      java.lang.String)
     * @aa
     */
    public UserAccount retrieveUserAccountByLoginName(final String loginName)
        throws SqlDatabaseSystemException {

        try {
            return (UserAccount) getUniqueResult(getHibernateTemplate().find(
                QUERY_RETRIEVE_USER_ACCOUNT_BY_LOGINNAME, loginName));
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

    /**
     * See Interface for functional description.
     * 
     * @param handle
     * @return
     * @see UserAccountDaoInterface#retrieveUserAccountByHandle(
     *      java.lang.String)
     * @aa
     */
    public UserAccount retrieveUserAccountByHandle(final String handle)
        throws SqlDatabaseSystemException {

        try {
            return (UserAccount) getUniqueResult(getHibernateTemplate().find(
                QUERY_RETRIEVE_USER_ACCOUNT_BY_HANDLE,
                new Object[] { handle, System.currentTimeMillis() }));
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

    /**
     * See Interface for functional description.
     * 
     * @param offset
     * @param maxResults
     * @param criterias
     * 
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveUserAccounts(java.util.Map, int, int, String, ListSorting)
     * @aa
     */
    public List<UserAccount> retrieveUserAccounts(
        final Map<String, Object> criterias, final int offset,
        final int maxResults, final String orderBy, final ListSorting sorting)
        throws SqlDatabaseSystemException {

        final DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(UserAccount.class, "user");

        final Map<String, Object> clonedCriterias =
            new HashMap<String, Object>(criterias);

        // ids
        final Set<String> userAccountIds =
            (Set<String>) clonedCriterias.remove(Constants.DC_IDENTIFIER_URI);
        if (userAccountIds != null) {
            detachedCriteria.add(Restrictions
                .in("id", userAccountIds.toArray()));
        }

        // active flag
        final String active = (String) clonedCriterias.remove(FILTER_ACTIVE);
        if (active != null) {
            detachedCriteria
                .add(Restrictions.eq("active", new Boolean(active)));
        }

        Iterator<String> keys = CRITERIA_MAP.keySet().iterator();
        while (keys.hasNext()) {
            final String key = keys.next();
            final Object criteriaValue = clonedCriterias.remove(key);
            if (criteriaValue != null) {
                final Object[] parts = CRITERIA_MAP.get(key);
                if (parts[0].equals(COMPARE_EQ)) {
                    detachedCriteria.add(Restrictions.eq((String) parts[1],
                        criteriaValue));
                }
                else {
                    detachedCriteria.add(Restrictions.like((String) parts[1],
                        criteriaValue));
                }
            }
        }

        // organizational units
        final String organizationalUnit =
            (String) clonedCriterias.remove(PROP_ORGANIZATIONAL_UNIT);
        if (organizationalUnit != null) {

            String ouAttributeName = null;
            try {
                ouAttributeName = EscidocConfiguration.getInstance()
                    .get(EscidocConfiguration
                        .ESCIDOC_CORE_AA_OU_ATTRIBUTE_NAME);
            } catch (IOException e) {
                throw new SqlDatabaseSystemException(e);
            }
            if (ouAttributeName == null || ouAttributeName.equals("")) {
                throw new SqlDatabaseSystemException(
                        "ou-attribute-name not found in configuration");
            }
            detachedCriteria
                .add(Restrictions
                    .sqlRestriction(
                        "this_.id in ("
                        + "select ua.id from aa.user_account ua, "
                        + "aa.user_attribute atts "
                        + "where ua.id = atts.user_id "
                        + "and atts.name = '" + ouAttributeName
                        + "' and atts.value = ?)",
                        organizationalUnit, Hibernate.STRING));
            // detachedCriteria.add(Restrictions.like("ous", StringUtility
            // .concatenateToString("%", organizationalUnit, "|||%")));
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

        if (clonedCriterias.isEmpty()) {
            List<UserAccount> result = null;

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
            return new ArrayList<UserAccount>(0);
        }

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
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveUserAccounts(java.lang.String, int, int)
     */
    public List<UserAccount> retrieveUserAccounts(
        final String criterias, final int offset, final int maxResults)
        throws InvalidSearchQueryException, SqlDatabaseSystemException {

        List<UserAccount> result = null;

        if ((criterias != null) && (criterias.length() > 0)) {
            result = getHibernateTemplate().findByCriteria(
                new UserAccountFilter(criterias).toSql(), offset, maxResults);
        }
        else {
            try {
                final DetachedCriteria detachedCriteria =
                    DetachedCriteria.forClass(UserAccount.class, "user");

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
     * @param userAccount
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #save(de.escidoc.core.aa.business.persistence.UserAccount)
     * @aa
     */
    public void save(final UserAccount userAccount)
        throws SqlDatabaseSystemException {

        super.save(userAccount);
    }

    /**
     * See Interface for functional description.
     * 
     * @param userAccount
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #update(de.escidoc.core.aa.business.persistence.UserAccount)
     * @aa
     */
    public void update(final UserAccount userAccount)
        throws SqlDatabaseSystemException {
        //remove user from cache
        clearUserDetailsCache(userAccount.getId());
        super.update(userAccount);
    }

    /**
     * See Interface for functional description.
     * 
     * @param userAccount
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #delete(de.escidoc.core.aa.business.persistence.UserAccount)
     * @aa
     */
    public void delete(final UserAccount userAccount)
        throws SqlDatabaseSystemException {
        //remove User from Cache
        if ((userAccount.getEscidocRolesByCreatorId() != null 
                && !userAccount.getEscidocRolesByCreatorId().isEmpty())
                || (userAccount.getEscidocRolesByModifiedById() != null 
                && !userAccount.getEscidocRolesByModifiedById().isEmpty())
                || (userAccount.getRoleGrantsByCreatorId() != null 
                && !userAccount.getRoleGrantsByCreatorId().isEmpty())
                || (userAccount.getRoleGrantsByRevokerId() != null 
                && !userAccount.getRoleGrantsByRevokerId().isEmpty())
                || (userAccount.getUserAccountsByCreatorId() != null 
                && !userAccount.getUserAccountsByCreatorId().isEmpty())
                || (userAccount.getUserAccountsByModifiedById() != null 
                && !userAccount.getUserAccountsByModifiedById().isEmpty())
                || (userAccount.getUserGroupsByCreatorId() != null 
                && !userAccount.getUserGroupsByCreatorId().isEmpty())
                || (userAccount.getUserGroupsByModifiedById() != null 
                && !userAccount.getUserGroupsByModifiedById().isEmpty())) {
            throw new SqlDatabaseSystemException(
                    "UserAccount has references to other tables");
        }
        clearUserDetailsCache(userAccount.getId());
        super.delete(userAccount);
    }

    /**
     * See Interface for functional description.
     * 
     * @param role
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveGrantsByRole(EscidocRole)
     * @aa
     */
    public List<RoleGrant> retrieveGrantsByRole(final EscidocRole role)
        throws SqlDatabaseSystemException {

        List<RoleGrant> result = null;
        if (role != null) {
            try {
                result =
                    getHibernateTemplate().find(QUERY_RETRIEVE_GRANTS_BY_ROLE,
                        role);
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
     * @param userId
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveGrantsByUserId(java.lang.String)
     * @aa
     */
    public List<RoleGrant> retrieveGrantsByUserId(final String userId)
        throws SqlDatabaseSystemException {

        List<RoleGrant> result = null;
        if (userId != null) {
            try {
                result =
                    getHibernateTemplate().find(
                        QUERY_RETRIEVE_GRANTS_BY_USER_ID, userId);
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
     * @param userAccount
     * @param role
     * @param objId
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveCurrentGrant(java.lang.String, EscidocRole,
     *      java.lang.String)
     * @aa
     */
    public RoleGrant retrieveCurrentGrant(
        final UserAccount userAccount, final EscidocRole role,
        final String objId) throws SqlDatabaseSystemException {

        RoleGrant result = null;
        if (userAccount != null && role != null) {

            try {
                DetachedCriteria criteria =
                    DetachedCriteria
                        .forClass(RoleGrant.class)
                        .add(
                            Restrictions.eq("userAccountByUserId", userAccount))
                        .add(Restrictions.eq("escidocRole", role)).add(
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
                throw new SqlDatabaseSystemException(
                    convertHibernateAccessException(e));
            }
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param userId
     * @param grantId
     * @return
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveGrant(java.lang.String, java.lang.String)
     * @aa
     */
    public RoleGrant retrieveGrant(final String userId, final String grantId)
        throws SqlDatabaseSystemException {

        RoleGrant result = null;
        if (grantId != null) {
            try {
                result =
                    (RoleGrant) getHibernateTemplate().get(RoleGrant.class,
                        grantId);
                if (result == null
                    || !result.getUserAccountByUserId().getId().equals(userId)) {
                    result = null;
                }
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
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param offset
     * @param maxResults
     * @param criterias
     * 
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveGrants(java.util.Map, int, int, String, ListSorting)
     * @aa
     */
    public List<RoleGrant> retrieveGrants(
        final Map<String, HashSet<String>> criterias,
        final String orderBy, final ListSorting sorting)
        throws SqlDatabaseSystemException {

        final DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(RoleGrant.class, "roleGrant");

        final Map<String, Object> clonedCriterias =
            new HashMap<String, Object>(criterias);
        
        // users
        final Set<String> userIds =
            (Set<String>) clonedCriterias.remove(Constants.FILTER_USER);
        Criterion userCriterion = null;
        if (userIds != null && !userIds.isEmpty()) {
            userCriterion = getInRestrictions(userIds, "userId");
        }

        // groups
        final Set<String> groupIds =
            (Set<String>) clonedCriterias.remove(Constants.FILTER_GROUP);
        Criterion groupCriterion = null;
        if (groupIds != null && !groupIds.isEmpty()) {
            groupCriterion = getInRestrictions(groupIds, "groupId");
        }
        
        // concatenate users and groups with OR
        if (userCriterion != null || groupCriterion != null) {
            if (userCriterion == null) {
                detachedCriteria.add(groupCriterion);
            } else if (groupCriterion == null) {
                detachedCriteria.add(userCriterion);
            } else {
                detachedCriteria.add(
                        Restrictions.or(userCriterion, groupCriterion));
            }
        }

        // roles
        final Set<String> roleIds =
            (Set<String>) clonedCriterias.remove(Constants.FILTER_ROLE);
        if (roleIds != null && !roleIds.isEmpty()) {
            detachedCriteria.add(getInRestrictions(roleIds, "roleId"));
        }

        // assigned-on
        final Set<String> objectIds =
            (Set<String>) clonedCriterias.remove(Constants.FILTER_ASSIGNED_ON);
        if (objectIds != null && !objectIds.isEmpty()) {
            detachedCriteria.add(getInRestrictions(objectIds, "objectId"));
        }

        // revocation-date-from
        final Set<String> revocationDateFrom =
            (Set<String>) clonedCriterias.remove(
                    Constants.FILTER_REVOCATION_DATE_FROM);
        if (revocationDateFrom != null && !revocationDateFrom.isEmpty()) {
            detachedCriteria.add(getDateRestriction(
                    revocationDateFrom, "revocationDate", true));
        }

        // revocation-date-to
        final Set<String> revocationDateTo =
            (Set<String>) clonedCriterias.remove(
                    Constants.FILTER_REVOCATION_DATE_TO);
        if (revocationDateTo != null && !revocationDateTo.isEmpty()) {
            detachedCriteria.add(getDateRestriction(
                    revocationDateTo, "revocationDate", false));
        }

        // creation-date-from
        final Set<String> grantedDateFrom =
            (Set<String>) clonedCriterias.remove(
                    Constants.FILTER_CREATION_DATE_FROM);
        if (grantedDateFrom != null && !grantedDateFrom.isEmpty()) {
            detachedCriteria.add(getDateRestriction(
                    grantedDateFrom, "creationDate", true));
        }

        // creation-date-to
        final Set<String> grantedDateTo =
            (Set<String>) clonedCriterias.remove(
                    Constants.FILTER_CREATION_DATE_TO);
        if (grantedDateTo != null && !grantedDateTo.isEmpty()) {
            detachedCriteria.add(getDateRestriction(
                    grantedDateTo, "creationDate", false));
        }

        // created-by
        final Set<String> creatorIds =
            (Set<String>) clonedCriterias.remove(Constants.FILTER_CREATED_BY);
        if (creatorIds != null && !creatorIds.isEmpty()) {
            detachedCriteria.add(getInRestrictions(creatorIds, "creatorId"));
        }

        // revoked-by
        final Set<String> revokerIds =
            (Set<String>) clonedCriterias.remove(Constants.FILTER_REVOKED_BY);
        if (revokerIds != null && !revokerIds.isEmpty()) {
            detachedCriteria.add(getInRestrictions(revokerIds, "revokerId"));
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

        if (clonedCriterias.isEmpty()) {
            List<RoleGrant> result = null;

            try {
                result =
                    getHibernateTemplate().findByCriteria(detachedCriteria);
            }
            catch (DataAccessException e) {
                throw new SqlDatabaseSystemException(e);
            }
            return result;
        }
        else {
            // unsupported filter criteria has been found, therefore the result
            // list must be empty.
            return new ArrayList<RoleGrant>(0);
        }

    }

    /**
     * See Interface for functional description.
     * 
     * @param criterias
     * @param offset
     * @param maxResults
     * @param userGroupHandler
     * 
     * @return
     * @throws InvalidSearchQueryException
     * @throws SystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveGrants(java.lang.String, int, int, UserGroupHandlerInterface)
     */
    public List<RoleGrant> retrieveGrants(
        final String criterias, final int offset, final int maxResults,
        final UserGroupHandlerInterface userGroupHandler)
        throws InvalidSearchQueryException, SystemException {
        List<RoleGrant> result = null;

        if ((criterias != null) && (criterias.length() > 0)) {
            RoleGrantFilter filter = new RoleGrantFilter(criterias);
            Set<String> userIds = filter.getUserIds();
            Set<String> groupIds = filter.getGroupIds();

            //check if userId and groupId was provided
            if ((userIds != null) && (!userIds.isEmpty())
                && (groupIds != null) && (!groupIds.isEmpty())) {
                throw new InvalidSearchQueryException(
                      "you may not provide a userId and a groupId at the same "
                    + "time");
            }

            //if userIds or groupIds are provided, 
            //get all groups the given users/groups belong to
            if ((userIds != null) && (!userIds.isEmpty())) {
                for (String userId : userIds) {
                    try {
                        groupIds.addAll(
                            userGroupHandler.retrieveGroupsForUser(userId));
                    }
                    catch (UserAccountNotFoundException e) {
                    }
                }
                filter.setGroupIds(groupIds);

            }
            else if ((groupIds != null) && (!groupIds.isEmpty())) {
                for (String groupId : groupIds) {
                    groupIds.addAll(
                        userGroupHandler.retrieveGroupsForGroup(groupId));
                }
                filter.setGroupIds(groupIds);
            }
            result = getHibernateTemplate().findByCriteria(
                filter.toSql(), offset, maxResults);
        }
        else {
            try {
                final DetachedCriteria detachedCriteria =
                    DetachedCriteria.forClass(RoleGrant.class, "roleGrant");

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
     * @param grant
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #save(de.escidoc.core.aa.business.persistence.RoleGrant)
     * @aa
     */
    public void save(final RoleGrant grant) throws SqlDatabaseSystemException {

        super.save(grant);
    }

    /**
     * See Interface for functional description.
     * 
     * @param grant
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #update(de.escidoc.core.aa.business.persistence.RoleGrant)
     * @aa
     */
    public void update(final RoleGrant grant) throws SqlDatabaseSystemException {

        super.update(grant);
    }

    /**
     * See Interface for functional description.
     * 
     * @param userId
     * @param attributeId
     * @return
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveAttribute(java.lang.String, java.lang.String)
     * @aa
     */
    public UserAttribute retrieveAttribute(
            final String userId, final String attributeId)
        throws SqlDatabaseSystemException {

        UserAttribute result = null;
        if (attributeId != null) {
            try {
                result =
                    (UserAttribute) getHibernateTemplate()
                    .get(UserAttribute.class, attributeId);
                if (result == null
                    || !result.getUserAccountByUserId().getId().equals(userId)) {
                    result = null;
                }
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
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param userAccount
     *
     * @return
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveAttributes(
     *      de.escidoc.core.aa.business.persistence.UserAccount)
     */
    public List<UserAttribute> retrieveAttributes(final UserAccount userAccount)
        throws SqlDatabaseSystemException {

        List<UserAttribute> result = null;
        DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(UserAttribute.class, "userAttribute");

        detachedCriteria.add(Restrictions.eq("userAccountByUserId", userAccount));
        try {
            result = getHibernateTemplate().findByCriteria(detachedCriteria);
        }
        catch (DataAccessException e) {
            throw new SqlDatabaseSystemException(e);
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param userAccount
     * @param attributeName
     *
     * @return
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveAttributes(
     *          de.escidoc.core.aa.business.persistence.UserAccount, 
     *          java.lang.String)
     */
    public List<UserAttribute> retrieveAttributes(
                        final UserAccount userAccount, 
                        final String attributeName)
        throws SqlDatabaseSystemException {

        List<UserAttribute> result = null;
        DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(UserAttribute.class, "userAttribute");

        detachedCriteria.add(Restrictions.eq("userAccountByUserId", userAccount));
        if (attributeName != null) {
            detachedCriteria.add(Restrictions.eq("name", attributeName));
        }
        try {
            result = getHibernateTemplate().findByCriteria(detachedCriteria);
        }
        catch (DataAccessException e) {
            throw new SqlDatabaseSystemException(e);
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param attributes set of key/value pairs
     *
     * @return
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveAttributes(java.lang.String)
     */
    public List<UserAttribute> retrieveAttributes(
            final Set<HashMap<String, String>> attributes)
        throws SqlDatabaseSystemException {
        
        if (attributes == null) {
            throw new SqlDatabaseSystemException("attributes may not be null");
        }

        List<UserAttribute> result = null;
        DetachedCriteria detachedCriteria =
            DetachedCriteria.forClass(UserAttribute.class, "userAttribute");
        
        Criterion criterion = null;
        for (HashMap<String, String> attribute : attributes) {
            for (String key : attribute.keySet()) {
                if (criterion == null) {
                    criterion = Restrictions.and(
                            Restrictions.eq("name", key), 
                            Restrictions.eq("value", attribute.get(key)));
                } else {
                    Criterion criterion1 = Restrictions.and(
                            Restrictions.eq("name", key), 
                            Restrictions.eq("value", attribute.get(key)));
                    criterion = Restrictions.or(criterion, criterion1);
                }
            }
        }
        
        detachedCriteria.add(criterion);
        try {
            result = getHibernateTemplate().findByCriteria(detachedCriteria);
        }
        catch (DataAccessException e) {
            throw new SqlDatabaseSystemException(e);
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param attribute
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #save(de.escidoc.core.aa.business.persistence.UserAttribute)
     * @aa
     */
    public void save(final UserAttribute attribute) 
                        throws SqlDatabaseSystemException {

        super.save(attribute);
    }

    /**
     * See Interface for functional description.
     * 
     * @param attribute
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #update(de.escidoc.core.aa.business.persistence.UserAttribute)
     * @aa
     */
    public void update(final UserAttribute attribute) 
                        throws SqlDatabaseSystemException {

        super.update(attribute);
    }

    /**
     * See Interface for functional description.
     * 
     * @param attribute
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #delete(de.escidoc.core.aa.business.persistence.UserAttribute)
     * @aa
     */
    public void delete(final UserAttribute attribute) 
                        throws SqlDatabaseSystemException {

        super.delete(attribute);
    }

    /**
     * See Interface for functional description.
     * 
     * @param handle
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveUserLoginDataByHandle(java.lang.String)
     * @aa
     */
    public UserLoginData retrieveUserLoginDataByHandle(final String handle)
        throws SqlDatabaseSystemException {

        UserLoginData result = null;
        try {
            result =
                checkUserLoginData(
                        (UserLoginData) getUniqueResult(getHibernateTemplate()
                    .find(QUERY_RETRIEVE_LOGINDATA_BY_HANDLE, handle)));
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
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param id
     * @return
     * @see de.escidoc.core.aa.business.persistence.UmUserLoginDataDaoInterface
     *      #retrieveUserLoginDataByUserId(java.lang.Long)
     * @aa
     */
    public List<UserLoginData> retrieveUserLoginDataByUserId(final String id)
        throws SqlDatabaseSystemException {

        try {
            return checkUserLoginData(getHibernateTemplate().find(
                QUERY_RETRIEVE_LOGINDATA_BY_USER_ID, new Object[] { id }));
        }
        catch (DataAccessException e) {
            throw new SqlDatabaseSystemException(e);
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @param handle
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveUserDetails(java.lang.String)
     */
    public UserDetails retrieveUserDetails(final String handle)
        throws SqlDatabaseSystemException {

        EscidocUserDetails result = null;
        if (handle != null) {
            
            //Check if UserDetails is already cached
            result = (EscidocUserDetails) PoliciesCache.getUserDetails(handle);
            if (result != null) {
                return result;
            }
            try {
                UserAccount userAccount = retrieveUserAccountByHandle(handle);
                if (userAccount != null) {
                    result =
                        new EscidocUserDetails();
                    result.setId(userAccount.getId());
                    result.setRealName(userAccount.getName());
                    
                    //Put Object in UserDetails-Cache
                    PoliciesCache.putUserDetails(handle, result);
                }
//                // FIXME: remove
//                System.out.println(getSession().connection());
//                // end
//                final Object[] res =
//                    (Object[]) getUniqueResult(getHibernateTemplate().find(
//                        QUERY_RETRIEVE_USER_DETAILS_DATA,
//                        new Object[] { handle, System.currentTimeMillis() }));
//                if (res != null) {
//                    result =
//                        new EscidocUserDetails();
//                    result.setId((String) res[0]);
//                    result.setRealName((String) res[1]);
//                    
//                    //Get OUs of user
//                    
//                    
//                    //Put Object in UserDetails-Cache
//                    PoliciesCache.putUserDetails(handle, result);
//                }
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

        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @param data
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #saveOrUpdate(de.escidoc.core.aa.business.persistence.UserLoginData)
     * @aa
     */
    public void saveOrUpdate(final UserLoginData data)
        throws SqlDatabaseSystemException {
        //remove UserDetails from Cache
        PoliciesCache.clearUserDetails(data.getHandle());
        super.saveOrUpdate(data);
    }

    /**
     * See Interface for functional description.
     * 
     * @param data
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #delete(de.escidoc.core.aa.business.persistence.UserLoginData)
     * @aa
     */
    public void delete(final UserLoginData data)
        throws SqlDatabaseSystemException {
        //remove UserData from Cache
        PoliciesCache.clearUserDetails(data.getHandle());
        super.delete(data);
    }

    /**
     * See Interface for functional description.
     * 
     * @param handle
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface#deleteUserLoginData(java.lang.String)
     */
    public void deleteUserLoginData(final String handle)
        throws SqlDatabaseSystemException {
        //remove UserData from Cache
        PoliciesCache.clearUserDetails(handle);
        super.delete(retrieveUserLoginDataByHandle(handle));
    }

    /**
     * See Interface for functional description.
     * 
     * @param timestamp
     * @return
     * @throws SqlDatabaseSystemException
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrieveExpiredUserLoginData(long)
     * @aa
     */
    @SuppressWarnings("unchecked")
    public List<UserLoginData> retrieveExpiredUserLoginData(final long timestamp)
        throws SqlDatabaseSystemException {

        final DetachedCriteria criteria =
            DetachedCriteria.forClass(UserLoginData.class);
        criteria.add(Restrictions.lt("expiryts", Long.valueOf(timestamp)));
        try {
            return getHibernateTemplate().findByCriteria(criteria);
        }
        catch (DataAccessException e) {
            throw new SqlDatabaseSystemException(e);
        }
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Checks if the provided
     * {@link de.escidoc.core.aa.business.persistence.UserLoginData} objects are
     * expired. The expired objects are removed from the storage.
     * 
     * @param userLoginDatas
     *            The <code>List</code> of
     *            {@link de.escidoc.core.aa.business.persistence.UserLoginData}
     *            objects to check.
     * @return Returns <code>List</code> of all non-expired
     *         {@link de.escidoc.core.aa.business.persistence.UserLoginData}
     *         objects of the provided list.
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     */
    private List<UserLoginData> checkUserLoginData(
        final List<UserLoginData> userLoginDatas)
        throws SqlDatabaseSystemException {

        if (userLoginDatas == null || userLoginDatas.isEmpty()) {
            return userLoginDatas;
        }

        List<UserLoginData> ret = new ArrayList<UserLoginData>();
        Iterator<UserLoginData> iterator = userLoginDatas.iterator();
        while (iterator.hasNext()) {
            UserLoginData userLoginData = iterator.next();
            userLoginData = checkUserLoginData(userLoginData);
            if (userLoginData != null) {
                ret.add(userLoginData);
            }
        }
        return ret;
    }

    /**
     * Checks if the provided
     * {@link de.escidoc.core.aa.business.persistence.UserLoginData} object for
     * is expired. If this is the case, the object is deleted from the storage.
     * And object is removed from Cache.
     * 
     * @param data
     *            The
     *            {@link de.escidoc.core.aa.business.persistence.UserLoginData}
     *            object to check.
     * @return Returns the provided
     *         {@link de.escidoc.core.aa.business.persistence.UserLoginData}
     *         object or <code>null</code> if it is expired.
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     */
    private UserLoginData checkUserLoginData(final UserLoginData data)
        throws SqlDatabaseSystemException {

        UserLoginData result = data;
        if ((result != null) && (isExpired(result))) {
            delete(result);
            PoliciesCache.clearUserDetails(data.getHandle());
            result = null;
        }
        return result;
    }

    /**
     * Removes UserDetails of user with given id from UserDetails-Cache.
     * 
     * @param userId
     *            The user Id.
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     */
    private void clearUserDetailsCache(final String userId)
        throws SqlDatabaseSystemException {

        UserAccount userAccount = retrieveUserAccountById(userId);
        if (userAccount != null 
                && userAccount.getUserLoginDatas() != null 
                && !userAccount.getUserLoginDatas().isEmpty()) {
            for (Iterator<UserLoginData> iterator = 
                userAccount.getUserLoginDatas().iterator(); 
                                            iterator.hasNext();) {
                UserLoginData userLoginData = iterator.next();
                PoliciesCache.clearUserDetails(userLoginData.getHandle());
            }
        }
    }

    /**
     * Checks if the provided
     * {@link de.escidoc.core.aa.business.persistence.UserLoginData} object is
     * expired.
     * 
     * @param data
     *            The
     *            {@link de.escidoc.core.aa.business.persistence.UserLoginData}
     *            object to check.
     * @return Returns <code>true</code> if the provided object is expired.
     */
    private boolean isExpired(final UserLoginData data) {

        boolean result = false;
        if (data.getExpiryts() - System.currentTimeMillis() <= 0) {
            result = true;
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

    /**
     * See Interface for functional description.
     * 
     * @param userId userId
     * @return List of UserPreference objects
     * @throws SqlDatabaseSystemException e
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrievePreferenceByUserId(java.lang.String)
     * @aa
     */
    public List<UserPreference> retrievePreferencesByUserId(final String userId)
        throws SqlDatabaseSystemException {

        List<UserPreference> result = null;
        if (userId != null) {
            try {
                result =
                    getHibernateTemplate().find(
                        QUERY_RETRIEVE_PREFERENCES_BY_USER_ID, userId);
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
     * @param preference
     *            The <code>UserPreference</code> object to save.
     * @throws SqlDatabaseSystemException
     *             Thrown in case of an internal database access error.
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #retrievePreferenceByUserId(java.lang.String)
     * 
     * @aa
     */
    public void save(final UserPreference preference) 
                        throws SqlDatabaseSystemException {
        super.save(preference);
    }

    /**
     * See Interface for functional description.
     * 
     * @param data userPreference Object
     * @throws SqlDatabaseSystemException e
     * @see de.escidoc.core.aa.business.persistence.UserAccountDaoInterface
     *      #delete(de.escidoc.core.aa.business.persistence.UserPreference)
     * @aa
     */
    public void delete(final UserPreference data)
        throws SqlDatabaseSystemException {
        super.delete(data);
    }
    
    /**
     * get an in-restriction. Eventually concatenated with an isNull-restriction
     * if criteria-set contains a null-value.
     * 
     * @param criteria criteria to put in in-restriction
     * @param fieldName field-name for in-restriction
     * @return Criterion
     * 
     * @aa
     */
    private Criterion getInRestrictions(
            final Set<String> criteria, final String fieldName) {
        if (criteria.contains("")) {
            criteria.remove("");
            if (criteria.isEmpty()) {
                return Restrictions
                        .isNull(fieldName);
            } else {
                return Restrictions
                        .or(Restrictions.isNull(fieldName), Restrictions
                                .in(fieldName, criteria.toArray()));
            }
        } else {
            return Restrictions
                    .in(fieldName, criteria.toArray());
        }
    }
    
    /**
     * get an ge- or le-restriction for dates. 
     * Eventually concatenated with an isNull-restriction
     * if criteria-set contains a null-value.
     * 
     * @param criteria criteria to put in in-restriction
     * @param fieldName field-name for in-restriction
     * @param isFrom true if search for dates greater the given date
     * @return Criterion
     * @throws SqlDatabaseSystemException e
     * 
      * @aa
     */
    private Criterion getDateRestriction(
                   final Set<String> criteria, 
                   final String fieldName, final boolean isFrom) 
                    throws SqlDatabaseSystemException {
        boolean containsNull = false;
        if (criteria.contains("")) {
            criteria.remove("");
            containsNull = true;
        }
        if (criteria.size() > 1) {
            throw new SqlDatabaseSystemException(
                    "from- and to-restrictions may only occur once");
        }
        if (containsNull) {
            if (criteria.isEmpty()) {
                return Restrictions
                        .isNull(fieldName);
            } else {
                if (isFrom) {
                    return Restrictions
                    .or(Restrictions.isNull(fieldName), Restrictions
                       .ge(fieldName, 
                          new Date(
                            new DateTime(
                             criteria.iterator().next()).getMillis())));
                } else {
                    return Restrictions
                    .or(Restrictions.isNull(fieldName), Restrictions
                       .le(fieldName, 
                          new Date(
                            new DateTime(
                             criteria.iterator().next()).getMillis())));
                }
            }
        } else {
            if (isFrom) {
                return Restrictions
                .ge(fieldName, new Date(
                        new DateTime(
                                criteria.iterator().next()).getMillis()));
            } else {
                return Restrictions
                .le(fieldName, new Date(
                        new DateTime(
                                criteria.iterator().next()).getMillis()));
            }
        }
    }
    
}
