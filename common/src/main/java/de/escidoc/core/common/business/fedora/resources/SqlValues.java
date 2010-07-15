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
package de.escidoc.core.common.business.fedora.resources;

import java.text.MessageFormat;

/**
 * Encapsulate the sub queries for SQL filtering.
 * 
 * @spring.bean id="filter.Values"
 * @author Andr&eacute; Schenk
 */
public final class SqlValues extends Values {
    // The following place holders may be used:
    // {0} : userId
    // {1} : roleId
    // {2} : groupSQL
    // {3} : quotedGroupSQL (used in stored procedures)
    private static final String USER_GRANT_SQL =
        "SELECT object_id FROM aa.role_grant WHERE user_id='{0}' AND "
            + "role_id='{1}' AND (revocation_date IS NULL OR revocation_date>"
            + "CURRENT_TIMESTAMP)";

    private static final String USER_GROUP_GRANT_SQL =
        "SELECT object_id FROM aa.role_grant WHERE {2} AND role_id='{1}' AND "
            + "(revocation_date IS NULL OR revocation_date>CURRENT_TIMESTAMP)";

    private static final String QUOTED_USER_GROUP_GRANT_SQL =
        "SELECT object_id FROM aa.role_grant WHERE {3} AND role_id='{1}' AND "
            + "(revocation_date IS NULL OR revocation_date>CURRENT_TIMESTAMP)";

    private static final String ID_SQL =
        "r.id IN (" + USER_GRANT_SQL + ") OR r.id IN (" + USER_GROUP_GRANT_SQL
            + ")";

    static {
        FUNCTION_MAP.put(FUNCTION_AND, "AND");
        FUNCTION_MAP.put(FUNCTION_OR, "OR");
        FUNCTION_MAP.put(FUNCTION_STRING_CONTAINS, "");
        FUNCTION_MAP.put(FUNCTION_STRING_EQUAL, "AND");
        FUNCTION_MAP.put(FUNCTION_STRING_ONE_AND_ONLY, "");

        OPERAND_MAP.put("component", "/components/component/id");
        OPERAND_MAP.put("content-model", "/properties/content-model/id");
        OPERAND_MAP.put("context", "/properties/context/id");
        OPERAND_MAP.put("created-by", "/properties/created-by/id");
        OPERAND_MAP.put("latest-release-number",
            "/properties/latest-release/number");
        OPERAND_MAP.put("latest-version-modified-by",
            "/properties/version/modified-by/id");
        OPERAND_MAP.put("latest-version-number", "/properties/version/number");
        OPERAND_MAP.put("latest-version-status", "/properties/version/status");
        OPERAND_MAP.put("lock-date", "/properties/lock-date");
        OPERAND_MAP.put("lock-owner", "/properties/lock-owner");
        OPERAND_MAP.put("lock-status", "/properties/lock-status");
        OPERAND_MAP.put("organizational-unit",
            "/properties/organizational-units/organizational-unit/id");
        OPERAND_MAP.put("public-status", "/properties/public-status");
        OPERAND_MAP.put("subject-id", USER_ID);
        OPERAND_MAP.put("version-modified-by",
            "/properties/version/modified-by/id");
        OPERAND_MAP.put("version-status", "/properties/version/status");

        // resource container
        SCOPE_MAP
            .put(
                "info:escidoc/names:aa:1.0:resource:container:container.collection",
                "r.id IN (SELECT value FROM list.property WHERE local_path="
                    + "'/struct-map/item/id' AND resource_id IN (SELECT "
                    + "resource_id FROM list.property WHERE local_path="
                    + "'/properties/content-model/title' "
                    + "AND value='collection' AND (resource_id IN ("
                    + USER_GRANT_SQL + ") OR resource_id IN ("
                    + USER_GROUP_GRANT_SQL + "))))");

        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:container:context",
            "r.id IN (SELECT resource_id FROM list.property WHERE local_path="
                + "'/properties/context/id' AND (value IN (" + USER_GRANT_SQL
                + ") OR value IN (" + USER_GROUP_GRANT_SQL + ")))");

        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:container:container",
            "r.id IN (SELECT value FROM list.property WHERE local_path="
                + "'/struct-map/container/id' AND (resource_id IN ("
                + USER_GRANT_SQL + ") OR resource_id IN ("
                + USER_GROUP_GRANT_SQL + ")))");

        SCOPE_MAP
            .put(
                "info:escidoc/names:aa:1.0:resource:container:hierarchical-containers",
                "r.id IN (SELECT resource_id FROM getAllChildContainers('"
                    + "SELECT DISTINCT resource_id FROM list.property WHERE "
                    + "resource_id IN (" + USER_GRANT_SQL.replace("'", "''")
                    + ") OR resource_id IN ("
                    + QUOTED_USER_GROUP_GRANT_SQL.replace("'", "''") + ")'))");

        SCOPE_MAP
            .put("info:escidoc/names:aa:1.0:resource:container-id", ID_SQL);

        // resource content relation
        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:content-relation-id",
            ID_SQL);

        // resource context
        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:context-id", ID_SQL);

        // resource item
        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:item:component",
            "r.id IN (SELECT resource_id FROM list.property WHERE local_path="
                + "'/components/component/id' AND (value IN (" + USER_GRANT_SQL
                + ") OR value IN (" + USER_GROUP_GRANT_SQL + ")))");

        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:item:container",
            "r.id IN (SELECT value FROM list.property WHERE local_path="
                + "'/struct-map/item/id' AND (resource_id IN ("
                + USER_GRANT_SQL + ") OR resource_id IN ("
                + USER_GROUP_GRANT_SQL + ")))");

        SCOPE_MAP.put(
            "info:escidoc/names:aa:1.0:resource:item:container.collection",
            "r.id IN (SELECT value FROM list.property WHERE local_path="
                + "'/struct-map/item/id' AND resource_id IN ("
                + "SELECT resource_id FROM list.property WHERE "
                + "local_path='/properties/content-model/title' "
                + "AND value='collection' AND (resource_id IN ("
                + USER_GRANT_SQL + ") OR resource_id IN ("
                + USER_GROUP_GRANT_SQL + "))))");

        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:item:context",
            "r.id IN (SELECT resource_id FROM list.property WHERE local_path="
                + "'/properties/context/id' AND (value IN (" + USER_GRANT_SQL
                + ") OR value IN (" + USER_GROUP_GRANT_SQL + ")))");

        SCOPE_MAP.put(
            "info:escidoc/names:aa:1.0:resource:item:hierarchical-containers",
            "r.id IN (SELECT resource_id FROM getAllChildItems"
                + "('SELECT DISTINCT resource_id FROM list.property WHERE "
                + "resource_id IN (" + USER_GRANT_SQL.replace("'", "''")
                + ") OR resource_id IN ("
                + QUOTED_USER_GROUP_GRANT_SQL.replace("'", "''") + ")'))");

        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:item-id", ID_SQL);

        // TODO
        IGNORED_SCOPES
            .add("info:escidoc/names:aa:1.0:resource:organizational-unit:hierarchical-parents");
    }

    /**
     * Escape a string.
     * 
     * @param s
     *            string
     * 
     * @return the escaped string
     */
    public String escape(final String s) {
        return s;
    }

    /**
     * Combine the given operands with AND.
     * 
     * @param operand1
     *            first operand
     * @param operand2
     *            second operand
     * 
     * @return AND conjunction of the given operands
     */
    public String getAndCondition(final String operand1, final String operand2) {
        return MessageFormat.format("({0}) AND ({1})", new Object[] { operand1,
            operand2 });
    }

    /**
     * Get a CONTAINS statement with the given operand.
     * 
     * @param operand
     *            operand
     * 
     * @return CONTAINS statement with the given operand
     */
    public String getContainsCondition(final String operand) {
        return MessageFormat.format(
            "r.id IN (SELECT resource_id FROM list.property WHERE {0})",
            new Object[] { operand });
    }

    /**
     * Combine the given operands with =.
     * 
     * @param operand1
     *            first operand
     * @param operand2
     *            second operand
     * 
     * @return EQUALS conjunction of the given operands
     */
    public String getEqualCondition(final String operand1, final String operand2) {
        return getContainsCondition(getKeyValueCondition(operand1, operand2));
    }

    /**
     * Get a condition of the form key=operand1 and value=operand2.
     * 
     * @param operand1
     *            first operand
     * @param operand2
     *            second operand
     * 
     * @return key/value statement of the given operands
     */
    public String getKeyValueCondition(
        final String operand1, final String operand2) {
        return MessageFormat.format("local_path=''{0}'' AND value=''{1}''",
            new Object[] { operand1, operand2 });
    }

    /**
     * Get a statement which does not affect another statement when combining it
     * with AND.
     * 
     * @param resourceType
     *            resource type
     * 
     * @return neutral element for AND
     */
    public String getNeutralAndElement(final ResourceType resourceType) {
        return "TRUE";
    }

    /**
     * Combine the given operands with OR.
     * 
     * @param operand1
     *            first operand
     * @param operand2
     *            second operand
     * 
     * @return OR conjunction of the given operands
     */
    public String getOrCondition(final String operand1, final String operand2) {
        return MessageFormat.format("({0}) OR ({1})", new Object[] { operand1,
            operand2 });
    }
}
