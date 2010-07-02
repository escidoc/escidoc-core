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
package de.escidoc.core.aa.convert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.Apply;

/**
 * This is a helper class to convert a XACML condition into an SQL fragment.
 *
 * @author SCHE
 * @aa
 */
public final class ConditionParser {
    private static final String FUNCTION_AND =
        "urn:oasis:names:tc:xacml:1.0:function:and";
    private static final String FUNCTION_OR =
        "urn:oasis:names:tc:xacml:1.0:function:or";
    private static final String FUNCTION_STRING_CONTAINS =
        "info:escidoc/names:aa:1.0:function:string-contains";
    private static final String FUNCTION_STRING_EQUAL =
        "urn:oasis:names:tc:xacml:1.0:function:string-equal";
    private static final String FUNCTION_STRING_ONE_AND_ONLY =
        "urn:oasis:names:tc:xacml:1.0:function:string-one-and-only";

    private static final String STRING_ONE_AND_ONLY = "string-one-and-only";
 
    private static final Map <String, String> FUNCTION_MAP =
        new HashMap <String, String>();
    private static final Map <String, String> OPERAND_MAP =
        new HashMap <String, String>();

    static {
        FUNCTION_MAP.put(FUNCTION_AND,                 "AND");
        FUNCTION_MAP.put(FUNCTION_OR,                  "OR");
        FUNCTION_MAP.put(FUNCTION_STRING_CONTAINS,     "");
        FUNCTION_MAP.put(FUNCTION_STRING_EQUAL,        "AND");
        FUNCTION_MAP.put(FUNCTION_STRING_ONE_AND_ONLY, STRING_ONE_AND_ONLY);

        // CHECKSTYLE:OFF
        OPERAND_MAP.put("component",                  "/components/component/id");
        OPERAND_MAP.put("content-model",              "/properties/content-model/id");
        OPERAND_MAP.put("context",                    "/properties/context/id");
        OPERAND_MAP.put("created-by",                 "/properties/created-by/id");
        OPERAND_MAP.put("latest-release-number",      "/properties/latest-release/number");
        OPERAND_MAP.put("latest-version-modified-by", "/properties/version/modified-by/id");
        OPERAND_MAP.put("latest-version-number",      "/properties/version/number");
        OPERAND_MAP.put("latest-version-status",      "/properties/version/status");
        OPERAND_MAP.put("lock-date",                  "/properties/lock-date");
        OPERAND_MAP.put("lock-owner",                 "/properties/lock-owner");
        OPERAND_MAP.put("lock-status",                "/properties/lock-status");
        OPERAND_MAP.put("organizational-unit",        "/properties/organizational-units/organizational-unit/id");
        OPERAND_MAP.put("public-status",              "/properties/public-status");
        OPERAND_MAP.put("subject-id",                 "'{0}'");
        OPERAND_MAP.put("version-modified-by",        "/properties/version/modified-by/id");
        OPERAND_MAP.put("version-status",             "/properties/version/status");
        // CHECKSTYLE:ON
    }

    /**
     * Default constructor.
     */
    private ConditionParser() {
    }

    /**
     * Extract the attribute name from a function URI.
     *
     * @param function function URI
     *
     * @return the last part of the URI path
     */
    private static String getAttribute(final String function) {
        String result = function;

        if (function != null) {
            int index = function.lastIndexOf(':');

            if ((index >= 0) && (index < function.length() - 1)) {
                result = function.substring(index + 1);
            }
        }
        return result;
    }

    /**
     * Parse the given condition and convert it into SQL.
     *
     * @param condition XACML condition
     *
     * @return SQL fragment representing the XACML condition
     */
    public static String parse(final Apply condition) {
        StringBuffer result = new StringBuffer();

        if (condition != null) {
            List< ? > children = condition.getChildren();
            String function = condition.getFunction().getIdentifier().toString();
            String sqlFunction = (String) FUNCTION_MAP.get(function);

            if (sqlFunction != null) {
                if (children != null) {
                    if (children.size() == 2) {
                        result.append('(');
                        if (function.equals(FUNCTION_STRING_CONTAINS)) {
                            if ((children.get(0) instanceof StringAttribute)
                                && (children.get(1) instanceof Apply)) {
                                result.append(parseContains(
                                    ((StringAttribute) children.get(0)).getValue(),
                                    parse((Apply) children.get(1))));
                            }
                            else {
                                throw new IllegalArgumentException(
                                    children.get(0).getClass().getName() + " or "
                                    + children.get(1).getClass().getName()
                                    + ": unexpected operand type");
                            }
                        }
                        else {
                            if (children.get(0) instanceof Apply) {
                                result.append(parse((Apply) children.get(0)));
                            }
                            else if (children.get(0) instanceof StringAttribute) {
                                result.append("r.id IN (SELECT resource_id");
                                result.append(" FROM list.property WHERE ");
                                result.append("local_path='");
                                result.append(((StringAttribute)
                                    children.get(0)).getValue());
                                result.append("'");
                            }
                            else {
                                throw new IllegalArgumentException(
                                    children.get(0).getClass().getName()
                                    + ": unexpected operand type");
                            }
                            result.append(" " + sqlFunction + " ");
                            if (children.get(1) instanceof Apply) {
                                result.append(parse((Apply) children.get(1)));
                            }
                            else if (children.get(1) instanceof StringAttribute) {
                                if (function.equals(FUNCTION_STRING_EQUAL)) {
                                    result.append("value=");
                                }
                                result.append("'");
                                result.append(((StringAttribute)
                                    children.get(1)).getValue());
                                result.append("')");
                            }
                            else {
                                throw new IllegalArgumentException(
                                    children.get(1).getClass().getName()
                                    + ": unexpected operand type");
                            }
                        }
                        result.append(')');
                    }
                    else if ((children.size() == 1)
                        && (sqlFunction == STRING_ONE_AND_ONLY)) {
                        if (children.get(0) instanceof AttributeDesignator) {
                            String sqlOperand = (String) OPERAND_MAP.get(
                                getAttribute(((AttributeDesignator)
                                    children.get(0)).getId().toString()));

                            if (sqlOperand != null) {
                                if (sqlOperand.startsWith("'")) {
                                    result.append("value=");
                                    result.append(sqlOperand);
                                    result.append(')');
                                }
                                else {
                                    result.append("r.id IN (SELECT resource_id");
                                    result.append(" FROM list.property");
                                    result.append(" WHERE local_path='");
                                    result.append(sqlOperand);
                                    result.append("'");
                                }
                            }
                            else {
                                throw new IllegalArgumentException(
                                    ((AttributeDesignator) children.get(0)).getId()
                                    + ": unknown operand");
                             }
                        }
                        else {
                            throw new IllegalArgumentException(
                                children.get(0).getClass().getName()
                                + ": unexpected operand type");
                        }
                    }
                    else {
                        throw new IllegalArgumentException(
                            "only binary operations allowed");
                    }
                }
                else {
                    throw new IllegalArgumentException("missing children");
                }
            }
            else {
                throw new IllegalArgumentException(
                    function + ": unknown function");
            }
        }
        return result.toString();
    }

    /**
     * Parse the self defined function "string-contains" and create an SQL snippet
     * from it.
     *
     * @param list list of possible values
     * @param value value which must match one of the values given in the above list
     *
     * @return SQL equivalent for that function
     */
    private static String parseContains(final String list, final String value) {
        StringBuffer result = new StringBuffer();
        String[] listValues = list.split(" ");

        for (String listvalue : listValues) {
            if (result.length() > 0) {
                result.append(" OR ");
            }
            result.append('(');
            result.append(value);
            result.append(" AND value='");
            result.append(listvalue);
            result.append("'))");
        }
        return result.toString();
    }
}
