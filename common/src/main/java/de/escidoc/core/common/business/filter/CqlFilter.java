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
package de.escidoc.core.common.business.filter;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.z3950.zing.cql.CQLAndNode;
import org.z3950.zing.cql.CQLBooleanNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLOrNode;
import org.z3950.zing.cql.CQLRelation;
import org.z3950.zing.cql.CQLSortNode;
import org.z3950.zing.cql.CQLTermNode;
import org.z3950.zing.cql.Modifier;
import org.z3950.zing.cql.ModifierSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * This class parses a CQL filter and translates it into a Hibernate query.
 * 
 * @author SCHE
 */
public abstract class CqlFilter {

    protected static final int COMPARE_LIKE = 0;
    protected static final int COMPARE_EQ = 1;

    /**
     * Mapping from URI to filter attribute.
     */
    protected final Map<String, Object[]> criteriaMap = new HashMap<String, Object[]>();

    /**
     * Mapping from URI to order by attribute.
     */
    protected final Map<String, String> propertyNamesMap = new HashMap<String, String>();

    /**
     * Holds criteria with special handling.
     */
    protected final Set<String> specialCriteriaNames = new HashSet<String>();

    protected DetachedCriteria detachedCriteria = null;

    public void addCriteria(String key, Object[] value) {
        this.criteriaMap.put(key, value);
    }

    /**
     * Evaluate a CQL boolean node.
     *
     * @param node CQL node
     *
     * @return Hibernate query reflecting the given CQL query
     * @throws InvalidSearchQueryException thrown if the given search query could
     *                                     not be translated into a SQL query
     */
    protected Criterion evaluate(final CQLBooleanNode node)
        throws InvalidSearchQueryException {
        Criterion result = null;
        Criterion left = evaluate(node.left);
        Criterion right = evaluate(node.right);

        if (node instanceof CQLAndNode) {
            if ((left != null) || (right != null)) {
                result = Restrictions.and(
                    getAndRestriction(left), getAndRestriction(right));
            }
        }
        else if (node instanceof CQLOrNode) {
            if ((left != null) || (right != null)) {
                result = Restrictions.or(
                    getOrRestriction(left), getOrRestriction(right));
            }
        }
        else {
            throw new InvalidSearchQueryException(
                node + ": node type not implemented");
        }
        return result;
    }

    /**
     * Evaluate a CQL node.
     *
     * @param node CQL node
     *
     * @return Hibernate query reflecting the given CQL query
     * @throws InvalidSearchQueryException thrown if the given search query could
     *                                     not be translated into a SQL query
     */
    protected Criterion evaluate(final CQLNode node)
        throws InvalidSearchQueryException {
        Criterion result;

        if (node instanceof CQLBooleanNode) {
            result = evaluate((CQLBooleanNode) node);
        }
        else if (node instanceof CQLSortNode) {
            result = evaluate((CQLSortNode) node);
        }
        else if (node instanceof CQLTermNode) {
            result = evaluate((CQLTermNode) node);
        }
        else {
            throw new InvalidSearchQueryException(
                node + ": node type not implemented");
        }
        return result;
    }

    /**
     * Evaluate a CQL relation.
     *
     * @param relation CQL relation
     * @param propertyName left side of the statement
     * @param value right side of the statement
     * @param useLike use LIKE instead of = in case of an equality relation
     *
     * @return Hibernate query reflecting the given CQL query
     * @throws InvalidSearchQueryException thrown if the given search query could
     *                                     not be translated into a SQL query
     */
    protected Criterion evaluate(final CQLRelation relation,
        final String propertyName, final Object value, final boolean useLike)
        throws InvalidSearchQueryException {
        Criterion result;
        final String rel = relation.getBase();

        if ((value == null) || (value.toString().length() == 0)) {
            result = Restrictions.isNull(propertyName);
        }
        else {
            if (rel.equals("<")) {
                result = Restrictions.lt(propertyName, value);
            }
            else if (rel.equals("<=")) {
                result = Restrictions.le(propertyName, value);
            }
            else if (rel.equals("=")) {
                if (useLike) {
                    result = Restrictions.like(propertyName, value);
                }
                else {
                    result = Restrictions.eq(propertyName, value);
                }
            }
            else if (rel.equals(">=")) {
                result = Restrictions.ge(propertyName, value);
            }
            else if (rel.equals(">")) {
                result = Restrictions.gt(propertyName, value);
            }
            else if (rel.equals("<>")) {
                result = Restrictions.ne(propertyName, value);
            }
            else {
                throw new InvalidSearchQueryException(
                    rel + ": relation not implemented");
            }
        }
        return result;
    }

    /**
     * Evaluate a CQL sort node.
     *
     * @param node CQL node
     *
     * @return Hibernate query reflecting the given CQL query
     * @throws InvalidSearchQueryException thrown if the given search query could
     *                                     not be translated into a SQL query
     */
   protected Criterion evaluate(final CQLSortNode node)
        throws InvalidSearchQueryException {
        setOrderBy(node.getSortIndexes());
        return evaluate(node.subtree);
    }

   /**
    * Evaluate a CQL term node.
    *
    * @param node CQL node
    *
    * @return Hibernate query reflecting the given CQL query
    * @throws InvalidSearchQueryException thrown if the given search query could
    *                                     not be translated into a SQL query
    */
    protected Criterion evaluate(final CQLTermNode node)
        throws InvalidSearchQueryException {
        return evaluate(
            node.getRelation(), node.getIndex(), node.getTerm(), false);
    }

    /**
     * Return the given criterion if it is not NULL. Otherwise return "TRUE".
     * 
     * @param criterion Hibernate query or NULL
     *
     * @return the given Hibernate query or "TRUE"
     */
    private Criterion getAndRestriction(final Criterion criterion) {
        Criterion result;

        if (criterion != null) {
            result = criterion;
        }
        else {
            result = Restrictions.sqlRestriction("TRUE");
        }
        return result;
    }

    /**
     * get an in-restriction. Eventually concatenated with an isNull-restriction
     * if criteria-set contains a null-value.
     *
     * @param criteria criteria to put in in-restriction
     * @param fieldName field-name for in-restriction
     *
     * @return Criterion
     */
    protected Criterion getInRestrictions(
        final Collection<String> criteria, final String fieldName) {
        if (criteria.contains("")) {
            criteria.remove("");
            if (criteria.isEmpty()) {
                return Restrictions.isNull(fieldName);
            }
            else {
                return Restrictions.or(Restrictions.isNull(fieldName),
                    Restrictions.in(fieldName, criteria.toArray()));
            }
        }
        else {
            return Restrictions.in(fieldName, criteria.toArray());
        }
    }

    /**
     * Return the given criterion if it is not NULL. Otherwise return "FALSE".
     * 
     * @param criterion Hibernate query or NULL
     *
     * @return the given Hibernate query or "FALSE"
     */
    private Criterion getOrRestriction(final Criterion criterion) {
        Criterion result;

        if (criterion != null) {
            result = criterion;
        }
        else {
            result = Restrictions.sqlRestriction("FALSE");
        }
        return result;
    }

    /**
     * Get all property names that are allowed as filter criteria for that filter.
     *
     * @return all property names for that filter
     */
    public Set<String> getPropertyNames() {
        Set<String> result = new TreeSet<String>();

        for (String criteria : criteriaMap.keySet()) {
            result.add(criteria);
        }
        return result;
    }

    /**
     * Get propertyNamesMap.
     *
     * @return propertyNamesMap
     */
    public Map<String, String> getPropertyMap() {
        return propertyNamesMap;
    }

    /**
     * Get criteriaMap.
     *
     * @return criteriaMap
     */
    public Map<String, Object[]> getCriteriaMap() {
        return criteriaMap;
    }

    /**
     * Get specialCriteriaNames.
     *
     * @return specialCriteriaNames
     */
    public Set<String> getSpecialCriteria() {
        return specialCriteriaNames;
    }

    /**
     * Set all sorting attributes.
     * 
     * @param orderBy order by attributes
     *
     * @throws InvalidSearchQueryException thrown if the given search query could
     *                                     not be translated into a SQL query
     */
    protected void setOrderBy(final Iterable<ModifierSet> orderBy)
        throws InvalidSearchQueryException {
        for (ModifierSet modifier : orderBy) {
            if (modifier.getModifiers().isEmpty()) {
                detachedCriteria.addOrder(Order.asc(modifier.getBase()));
            } else {
                for (Modifier mod : modifier.getModifiers()) {
                    String columnName =
                            propertyNamesMap.get(modifier.getBase());

                    if (columnName == null) {
                        throw new InvalidSearchQueryException(
                                "attribute \"" + modifier.getBase()
                                        + "\" not allowed for sorting");
                    }
                    if (mod.getType().equals("sort.ascending")) {
                        detachedCriteria.addOrder(Order.asc(columnName));
                    } else if (mod.getType().equals("sort.descending")) {
                        detachedCriteria.addOrder(Order.desc(columnName));
                    } else {
                        throw new InvalidSearchQueryException(
                                mod.getType()
                                        + ": index modifier type not implemented");
                    }
                }
            }
        }
    }

    /**
     * Convert the CQL filter into a Hibernate query.
     *
     * @return Hibernate query representing this filter
     * @throws InvalidSearchQueryException thrown if the given search query could
     *                                     not be translated into a SQL query
     */
    public DetachedCriteria toSql() throws InvalidSearchQueryException {
        return detachedCriteria;
    }
}
