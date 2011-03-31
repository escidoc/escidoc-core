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
package de.escidoc.core.sm.business.filter;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.filter.CqlFilter;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.sm.business.persistence.hibernate.AggregationDefinition;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLTermNode;

/**
 * This class parses a CQL filter to filter for aggregation definitions and translates it into a Hibernate query.
 *
 * @author André Schenk
 */
public class AggregationDefinitionFilter extends CqlFilter {

    /**
     * Parse the given CQL query and create a corresponding Hibernate query to filter for eSciDoc aggregation
     * definitions from it.
     *
     * @param query CQL query
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     */
    public AggregationDefinitionFilter(final String query) throws InvalidSearchQueryException {
        //Adding or Removal of values has also to be done in Method evaluate
        //and in the Hibernate-Class-Method retrieveAggregationDefinitions
        // URI-style filters/////////////////////////////////////////////////////
        //Filter-Names
        criteriaMap.put(Constants.DC_IDENTIFIER_URI, new Object[] { COMPARE_EQ, "id" });

        //Sortby-Names
        propertyNamesMap.put(Constants.DC_IDENTIFIER_URI, "id");
        // //////////////////////////////////////////////////////////////////////

        // Path-style filters////////////////////////////////////////////////////
        //Filter-Names
        criteriaMap.put(Constants.FILTER_PATH_ID, new Object[] { COMPARE_EQ, "id" });

        //Sortby-Names
        propertyNamesMap.put(Constants.FILTER_PATH_ID, "id");
        // //////////////////////////////////////////////////////////////////////

        if (query != null) {
            try {
                final CQLParser parser = new CQLParser();

                this.detachedCriteria = DetachedCriteria.forClass(AggregationDefinition.class, "a");

                final Criterion criterion = evaluate(parser.parse(query));

                if (criterion != null) {
                    detachedCriteria.add(criterion);
                }
            }
            catch (final Exception e) {
                throw new InvalidSearchQueryException(e);
            }
        }
    }

    /**
     * Evaluate a CQL term node.
     *
     * @param node CQL node
     * @return Hibernate query reflecting the given CQL query
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     */
    @Override
    protected Criterion evaluate(final CQLTermNode node) throws InvalidSearchQueryException {
        Criterion result = null;
        final Object[] parts = criteriaMap.get(node.getIndex());
        final String value = node.getTerm();

        if (parts != null) {
            result = evaluate(node.getRelation(), (String) parts[1], value, (Integer) parts[0] == COMPARE_LIKE);
        }
        else {
            final String columnName = node.getIndex();

            if (columnName != null) {
                throw new InvalidSearchQueryException("unknown filter criteria: " + columnName);
            }
        }
        return result;
    }
}
