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
 * Copyright 2009 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.business.filter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLTermNode;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.filter.CqlFilter;
import de.escidoc.core.aa.business.persistence.RoleGrant;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.util.xml.XmlUtility;

/**
 * This class parses a CQL filter to filter for eSciDoc role grants and
 * translates it into a Hibernate query.
 * 
 * @author SCHE
 */
public class RoleGrantFilter extends CqlFilter {
    private static final String PROP_EMAIL =
        Constants.PROPERTIES_NS_URI + XmlUtility.NAME_EMAIL;

    private static final String PROP_LOGINNAME =
        Constants.PROPERTIES_NS_URI + XmlUtility.NAME_LOGIN_NAME;

    private Set<String> userIds = new HashSet<String>();

    private Set<String> groupIds = new HashSet<String>();

    /**
     * Parse the given CQL query and create a corresponding Hibernate query to
     * filter for eSciDoc role grants from it.
     * 
     * @param query
     *            CQL query
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     */
    public RoleGrantFilter(final String query)
        throws InvalidSearchQueryException {
        criteriaMap.put(Constants.FILTER_ROLE, new Object[] { COMPARE_EQ,
            "roleId" });
        criteriaMap.put(Constants.FILTER_ASSIGNED_ON, new Object[] {
            COMPARE_EQ, "objectId" });
        criteriaMap.put(Constants.FILTER_CREATED_BY, new Object[] { COMPARE_EQ,
            "creatorId" });
        criteriaMap.put(Constants.FILTER_REVOKED_BY, new Object[] { COMPARE_EQ,
            "revokerId" });

        propertyNamesMap.put(PROP_EMAIL, "email");
        propertyNamesMap.put(PROP_LOGINNAME, "loginname");
        propertyNamesMap.put(TripleStoreUtility.PROP_NAME, "name");
        propertyNamesMap.put(TripleStoreUtility.PROP_CREATED_BY_ID,
            "userAccountByCreatorId.id");
        propertyNamesMap.put(TripleStoreUtility.PROP_MODIFIED_BY_ID,
            "userAccountByModifiedById.id");
        propertyNamesMap.put(Constants.DC_IDENTIFIER_URI, "id");
        propertyNamesMap.put(Constants.FILTER_USER, "userId");
        propertyNamesMap.put(Constants.FILTER_GROUP, "groupId");
        propertyNamesMap.put(Constants.FILTER_ROLE, "roleId");
        propertyNamesMap.put(Constants.FILTER_ASSIGNED_ON, "objectId");
        propertyNamesMap.put(Constants.FILTER_CREATED_BY, "creatorId");
        propertyNamesMap.put(Constants.FILTER_REVOKED_BY, "revokerId");
        propertyNamesMap
            .put(Constants.FILTER_REVOCATION_DATE, "revocationDate");
        propertyNamesMap.put(Constants.FILTER_CREATION_DATE, "creationDate");
        propertyNamesMap.put(Constants.FILTER_GRANTED_FROM, "grantedFrom");
        propertyNamesMap.put(Constants.FILTER_GRANTED_TO, "grantedTo");

        if (query != null) {
            try {
                CQLParser parser = new CQLParser();

                detachedCriteria =
                    DetachedCriteria.forClass(RoleGrant.class, "roleGrant");

                Criterion criterion = evaluate(parser.parse(query));

                if (criterion != null) {
                    detachedCriteria.add(criterion);
                }
            }
            catch (Exception e) {
                throw new InvalidSearchQueryException(e);
            }
        }
    }

    /**
     * Evaluate a CQL term node.
     * 
     * @param node
     *            CQL node
     * 
     * @return Hibernate query reflecting the given CQL query
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     */
    protected Criterion evaluate(final CQLTermNode node)
        throws InvalidSearchQueryException {
        Criterion result = null;
        Object[] parts = criteriaMap.get(node.getIndex());
        String value = node.getTerm();

        if (parts != null) {
            result =
                evaluate(node.getRelation(), (String) parts[1], value,
                    (Integer) (parts[0]) == COMPARE_LIKE);
        }
        else {
            String columnName = node.getIndex();

            if (columnName != null) {
                if (columnName.equals(Constants.FILTER_USER)) {
                    userIds.add(value);
                }
                else if (columnName.equals(Constants.FILTER_GROUP)) {
                    groupIds.add(value);
                }
                else if (columnName.equals(Constants.FILTER_REVOCATION_DATE)) {
                    result =
                        evaluate(
                            node.getRelation(),
                            "revocationDate",
                            ((value != null) && (value.length() > 0)) ? new Date(
                                new DateTime(value).getMillis())
                                : null, false);
                }
                else if (columnName.equals(Constants.FILTER_CREATION_DATE)) {
                    result =
                        evaluate(
                            node.getRelation(),
                            "creationDate",
                            ((value != null) && (value.length() > 0)) ? new Date(
                                new DateTime(value).getMillis())
                                : null, false);
                }
                else if (columnName.equals(Constants.FILTER_GRANTED_FROM)) {
                    result =
                        evaluate(
                            node.getRelation(),
                            "grantedFrom",
                            ((value != null) && (value.length() > 0)) ? new Date(
                                new DateTime(value).getMillis())
                                : null, false);
                }
                else if (columnName.equals(Constants.FILTER_GRANTED_TO)) {
                    result =
                        evaluate(
                            node.getRelation(),
                            "grantedTo",
                            ((value != null) && (value.length() > 0)) ? new Date(
                                new DateTime(value).getMillis())
                                : null, false);
                }
                else {
                    throw new InvalidSearchQueryException(
                        "unknown filter criteria: " + columnName);
                }
            }
        }
        return result;
    }

    /**
     * Get the list of the collected user group ids.
     * 
     * @return list of the collected user group ids
     */
    public Set<String> getGroupIds() {
        return groupIds;
    }

    /**
     * Get all property names that are allowed as filter criteria for that
     * filter.
     * 
     * @return all property names for that filter
     */
    public Set<String> getPropertyNames() {
        Set<String> result = new TreeSet<String>();

        result.addAll(super.getPropertyNames());
        result.add(Constants.FILTER_USER);
        result.add(Constants.FILTER_GROUP);
        result.add(Constants.FILTER_REVOCATION_DATE);
        result.add(Constants.FILTER_CREATION_DATE);
        result.add(Constants.FILTER_GRANTED_FROM);
        result.add(Constants.FILTER_GRANTED_TO);
        return result;
    }

    /**
     * Get the list of the collected user account ids.
     * 
     * @return list of the collected user account ids
     */
    public Set<String> getUserIds() {
        return userIds;
    }

    /**
     * Set the list of user groups.
     * 
     * @param groupIds
     *            list of user groups.
     */
    public void setGroupIds(final Set<String> groupIds) {
        this.groupIds = groupIds;
    }

    /**
     * Set the list of user accounts.
     * 
     * @param userIds
     *            list of user accounts.
     */
    public void setUserIds(final Set<String> userIds) {
        this.userIds = userIds;
    }

    /**
     * Convert the CQL filter into a Hibernate query.
     * 
     * @return Hibernate query representing this filter
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     */
    public DetachedCriteria toSql() throws InvalidSearchQueryException {
        DetachedCriteria result = super.toSql();

        // users
        Criterion userCriterion = null;

        if ((userIds != null) && (!userIds.isEmpty())) {
            userCriterion = getInRestrictions(userIds, "userId");
        }

        // groups
        Criterion groupCriterion = null;

        if ((groupIds != null) && (!groupIds.isEmpty())) {
            groupCriterion = getInRestrictions(groupIds, "groupId");
        }

        // concatenate users and groups with OR
        if ((userCriterion != null) || (groupCriterion != null)) {
            if (userCriterion == null) {
                result.add(groupCriterion);
            }
            else if (groupCriterion == null) {
                result.add(userCriterion);
            }
            else {
                result.add(Restrictions.or(userCriterion, groupCriterion));
            }
        }
        return result;
    }
}
