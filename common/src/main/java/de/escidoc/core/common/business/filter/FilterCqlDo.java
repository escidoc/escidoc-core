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
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.common.business.filter;

import java.io.IOException;
import java.util.Vector;

import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLSortNode;
import org.z3950.zing.cql.Modifier;
import org.z3950.zing.cql.ModifierSet;

import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

/**
 * @author Michael Hoppe
 * 
 * Class parses CQL-Query and extends it with a resource-type-subquery.
 *
 */
public class FilterCqlDo {

    private String query;

    private String sortBy;

    private String resourceTypeSubQuery;

    /**
     * Constructor.
     * 
     * @param inQuery CQL-query
     * @param resourceTypes resourceTypes to be expected in the response
     * @throws WebserverSystemException
     *             thrown if the given cql query could not be parsed
     */
    public FilterCqlDo(final String inQuery, final ResourceType[] resourceTypes) throws WebserverSystemException {
        if (inQuery != null && inQuery.length() > 0) {
            try {
                CQLParser cqlParser = new CQLParser();
                CQLNode rootNode = cqlParser.parse(inQuery);
                if (rootNode instanceof CQLSortNode) {
                    setSortBy((CQLSortNode) rootNode);
                    setQuery(((CQLSortNode) rootNode).subtree.toCQL());
                }
                else {
                    setQuery(inQuery);
                }
            }
            catch (CQLParseException e) {
                throw new WebserverSystemException(e);
            }
            catch (IOException e) {
                throw new WebserverSystemException(e);
            }
        }
        setResourceTypeSubquery(resourceTypes);
    }

    /**
     * Set sortBy String by parsing CQLSortNode.
     * 
     * @param rootNode the cql root node
     */
    private void setSortBy(final CQLSortNode rootNode) {
        StringBuilder sortByBuilder = new StringBuilder();
        Vector<ModifierSet> sorts = ((CQLSortNode) rootNode).getSortIndexes();
        if (sorts != null && !sorts.isEmpty()) {
            sortByBuilder.append(" sortBy ");
            for (ModifierSet modifierSet : sorts) {
                sortByBuilder.append("\"").append(modifierSet.getBase()).append("\"");
                if (modifierSet.getModifiers() != null && !modifierSet.getModifiers().isEmpty()) {
                    for (Modifier modifier : modifierSet.getModifiers()) {
                        sortByBuilder.append("/").append(modifier.toCQL());
                    }
                }
                sortByBuilder.append(" ");
            }
            sortBy = sortByBuilder.toString();
        }
    }

    /**
     * Set resourceTypeSubQuery.
     * 
     * @param resourceTypes resourceTypes to be expected in the response.
     */
    private void setResourceTypeSubquery(final ResourceType[] resourceTypes) {
        final StringBuilder resourceTypeQuery = new StringBuilder();

        for (final ResourceType resourceType : resourceTypes) {
            if (resourceTypeQuery.length() > 0) {
                resourceTypeQuery.append(" OR ");
            }
            resourceTypeQuery.append("\"type\"=");
            resourceTypeQuery.append(resourceType.getLabel());
        }
        resourceTypeSubQuery = resourceTypeQuery.toString();
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * Get CQL-Query expanded with resource-types subquery.
     * 
     * @return String query.
     */
    public String getTypedFilterQuery() {
        final StringBuilder filterQuery = new StringBuilder();
        if (resourceTypeSubQuery != null && resourceTypeSubQuery.length() > 0) {
            filterQuery.append('(');
            filterQuery.append(resourceTypeSubQuery);
            filterQuery.append(") ");
        }
        if (query != null && query.length() > 0) {
            if (filterQuery.length() > 0) {
                filterQuery.append("AND ");
            }
            filterQuery.append('(').append(query).append(')');
        }
        if (sortBy != null) {
            filterQuery.append(sortBy);
        }
        return filterQuery.toString();
    }

}
