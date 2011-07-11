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
package de.escidoc.core.sm.business;

import java.util.ArrayList;
import java.util.Collection;

/**
 * An utility class for SM XML Handling.
 *
 * @author Michael Hoppe
 */
public final class SmXmlUtility {

    private SmXmlUtility() {
    }

    /**
     * Extracts aggregation-definition-prim-keys out of the table-names of the given sql.
     *
     * @param sql sql-statement of report-definition.
     * @return Collection with aggregation-definition prim keys.
     */
    public static Collection<String> extractAggregationPrimKeysFromSql(final String sql) {
        final Collection<String> primKeys = new ArrayList<String>();
        if (sql != null) {
            String workSql = sql.replaceAll("\\s+", " ");
            workSql = workSql.replaceAll("\\s+", " ");
            boolean condition = false;
            if (workSql.matches("(?i).* (where|order by|group by) .*")) {
                condition = true;
            }
            final String fromClause =
                condition ? workSql.replaceFirst("(?i).*?from(.*?)(where|order by|group by).*", "$1") : workSql
                    .replaceFirst("(?i).*?from(.*)", "$1");
            final String[] tables = fromClause.split(",");
            for (String table : tables) {
                if (table.matches(".*?_.*")) {
                    table = table.replaceFirst(".*?\\.", "").trim();
                    if (table.startsWith("_")) {
                        table = table.replaceFirst("_", "");
                    }
                    primKeys.add(table.replaceFirst("(.*?)_.*", "$1"));
                }
            }
        }
        return primKeys;
    }

    /**
     * returns id of scope.
     *
     * @param xmlData .
     * @return String scopeId
     */
    public static String getScopeId(final String xmlData) {
        return xmlData.replaceFirst("(?s).*?<[^>]*?scope.*?objid=\"(.*?)\".*", "$1");
    }

    /**
     * removes Special Signs from primKey to create a table with primKey in name.
     *
     * @param primKey primKey
     * @return String primKeyWithoutSpecialSigns
     */
    public static String convertPrimKeyToTableName(final String primKey) {
        if (primKey != null) {
            return primKey.replaceAll("\\:", "");
        }
        return null;
    }

}
