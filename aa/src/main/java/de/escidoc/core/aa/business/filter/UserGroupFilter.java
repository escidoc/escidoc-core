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
import de.escidoc.core.aa.business.persistence.UserGroup;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;

/**
 * This class parses a CQL filter to filter for eSciDoc user groups and
 * translates it into a Hibernate query.
 * 
 * @author SCHE
 */
public class UserGroupFilter extends CqlFilter {
    /**
     * Parse the given CQL query and create a corresponding Hibernate query to
     * filter for eSciDoc user groups from it.
     * 
     * @param query
     *            CQL query
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     */
    public UserGroupFilter(final String query)
        throws InvalidSearchQueryException {
        //Adding or Removal of values has also to be done in Method evaluate
        //and in the Hibernate-Class-Method retrieveUserAccounts
        //And adapt Pattern GROUP_FILTER_PATTERN in UserAccountHandler
        //And adapt method ExtendedFilterHandler.transformFilterName
        // URI-style filters/////////////////////////////////////////////////////
        //Filter-Names
        criteriaMap.put(Constants.DC_IDENTIFIER_URI, new Object[] { COMPARE_EQ,
            "id" });
        criteriaMap.put(TripleStoreUtility.PROP_NAME, new Object[] {
            COMPARE_LIKE, "name" });
        criteriaMap.put(Constants.FILTER_LABEL, new Object[] { COMPARE_LIKE,
            "label" });
        criteriaMap.put(Constants.FILTER_EMAIL, new Object[] { COMPARE_LIKE,
            "email" });
        criteriaMap.put(TripleStoreUtility.PROP_CREATED_BY_ID, new Object[] {
            COMPARE_EQ, "userGroupByCreatorId.id" });
        criteriaMap.put(TripleStoreUtility.PROP_MODIFIED_BY_ID, new Object[] {
            COMPARE_EQ, "userGroupByModifiedById.id" });

        //Sortby-Names
        propertyNamesMap.put(TripleStoreUtility.PROP_NAME, "name");
        propertyNamesMap.put(Constants.FILTER_LABEL, "label");
        propertyNamesMap.put(Constants.FILTER_EMAIL, "email");
        propertyNamesMap.put(TripleStoreUtility.PROP_CREATED_BY_ID,
            "userGroupByCreatorId.id");
        propertyNamesMap.put(TripleStoreUtility.PROP_MODIFIED_BY_ID,
            "userGroupByModifiedById.id");
        propertyNamesMap.put(Constants.DC_IDENTIFIER_URI, "id");
        // //////////////////////////////////////////////////////////////////////

        // Path-style filters////////////////////////////////////////////////////
        //Filter-Names

        if (query != null) {
            try {
                CQLParser parser = new CQLParser();

                detachedCriteria = DetachedCriteria.forClass(UserGroup.class);

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
                if (columnName.equals(PROP_URI_ACTIVE)) {
                    result =
                        Restrictions.eq("active", Boolean.parseBoolean(value));
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
                else {
                    throw new InvalidSearchQueryException(
                        "unknown filter criteria: " + columnName);
                }
            }
        }
        return result;
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
        result.add(PROP_URI_ACTIVE);
        result.add(Constants.FILTER_CREATION_DATE);
        return result;
    }
}
