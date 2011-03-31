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
package de.escidoc.core.oai.business.filter;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.TripleStoreUtility;
import de.escidoc.core.common.business.filter.CqlFilter;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.oai.business.persistence.SetDefinition;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLTermNode;

import java.util.Set;
import java.util.TreeSet;

/**
 * This class parses a CQL filter to filter for OAI set definitions and translates it into a Hibernate query.
 *
 * @author André Schenk
 */
public class SetDefinitionFilter extends CqlFilter {

    /**
     * Parse the given CQL query and create a corresponding Hibernate query to filter for OAI set definitions from it.
     *
     * @param query CQL query
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     */
    public SetDefinitionFilter(final String query) throws InvalidSearchQueryException {
        //Adding or Removal of values has also to be done in Method evaluate
        //and in the Hibernate-Class-Method retrieveSetDefinitions
        // URI-style filters/////////////////////////////////////////////////////
        //Filter-Names
        criteriaMap.put(Constants.DC_IDENTIFIER_URI, new Object[] { COMPARE_EQ, "id" });
        criteriaMap.put(TripleStoreUtility.PROP_NAME, new Object[] { COMPARE_LIKE, "name" });
        criteriaMap.put(TripleStoreUtility.PROP_CREATED_BY_ID, new Object[] { COMPARE_EQ, "creator_id" });
        criteriaMap.put(TripleStoreUtility.PROP_MODIFIED_BY_ID, new Object[] { COMPARE_EQ, "modified_by_id" });
        criteriaMap.put(Constants.SET_DEFINITION_NS_URI + '/' + XmlUtility.NAME_SPECIFICATION, new Object[] {
            COMPARE_LIKE, "specification" });

        //Sortby-Names
        propertyNamesMap.put("specification", "specification");
        propertyNamesMap.put(TripleStoreUtility.PROP_NAME, "name");
        propertyNamesMap.put(TripleStoreUtility.PROP_CREATED_BY_ID, "creator_id");
        propertyNamesMap.put(TripleStoreUtility.PROP_MODIFIED_BY_ID, "modified_by_id");
        propertyNamesMap.put(Constants.DC_IDENTIFIER_URI, "id");
        // //////////////////////////////////////////////////////////////////////

        // Path-style filters////////////////////////////////////////////////////
        //Filter-Names
        criteriaMap.put(Constants.FILTER_PATH_ID, new Object[] { COMPARE_EQ, "id" });
        criteriaMap.put(Constants.FILTER_PATH_NAME, new Object[] { COMPARE_LIKE, "name" });
        criteriaMap.put(Constants.FILTER_PATH_CREATED_BY_ID, new Object[] { COMPARE_EQ, "creator_id" });
        criteriaMap.put(Constants.FILTER_PATH_MODIFIED_BY_ID, new Object[] { COMPARE_EQ, "modified_by_id" });
        criteriaMap.put(Constants.FILTER_PATH_SPECIFICATION, new Object[] { COMPARE_LIKE, "specification" });

        // Sortby-Names
        propertyNamesMap.put("specification", "specification");
        propertyNamesMap.put(Constants.FILTER_PATH_SPECIFICATION, "specification");
        propertyNamesMap.put(Constants.FILTER_PATH_NAME, "name");
        propertyNamesMap.put(Constants.FILTER_PATH_CREATED_BY_ID, "creator_id");
        propertyNamesMap.put(Constants.FILTER_PATH_MODIFIED_BY_ID, "modified_by_id");
        propertyNamesMap.put(Constants.FILTER_PATH_ID, "id");
        // //////////////////////////////////////////////////////////////////////

        if (query != null) {
            try {
                final CQLParser parser = new CQLParser();

                this.detachedCriteria = DetachedCriteria.forClass(SetDefinition.class, "sd");

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

    /**
     * Get all property names that are allowed as filter criteria for that filter.
     *
     * @return all property names for that filter
     */
    @Override
    public Set<String> getPropertyNames() {
        final Set<String> result = new TreeSet<String>();

        result.addAll(super.getPropertyNames());
        return result;
    }
}
