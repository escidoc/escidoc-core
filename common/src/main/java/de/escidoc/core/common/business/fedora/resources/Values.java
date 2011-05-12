/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.common.business.fedora.resources;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Abstract super class to get the sub queries for filtering.
 *
 * @author Andr&eacute; Schenk
 */
public abstract class Values {

    public static final String FUNCTION_AND = "urn:oasis:names:tc:xacml:1.0:function:and";

    public static final String FUNCTION_OR = "urn:oasis:names:tc:xacml:1.0:function:or";

    public static final String FUNCTION_STRING_CONTAINS = "info:escidoc/names:aa:1.0:function:string-contains";

    public static final String FUNCTION_STRING_EQUAL = "urn:oasis:names:tc:xacml:1.0:function:string-equal";

    public static final String FUNCTION_STRING_ONE_AND_ONLY =
        "urn:oasis:names:tc:xacml:1.0:function:string-one-and-only";

    public static final String USER_ID = "{0}";

    /**
     * This map contains all functions which can be mapped.
     */
    protected static final Map<String, String> FUNCTION_MAP = new HashMap<String, String>();

    /**
     * This map contains all operands which can be mapped.
     */
    protected static final Map<String, String> OPERAND_MAP = new HashMap<String, String>();

    /**
     * This set contains scopes which are ignored by this parser.
     */
    protected static final Collection<String> IGNORED_SCOPES = new HashSet<String>();

    /**
     * This map contains all scopes which can be mapped.
     */
    protected static final Map<String, String> SCOPE_MAP = new HashMap<String, String>();

    static {
        // There are no components in the DB cache.
        IGNORED_SCOPES.add("info:escidoc/names:aa:1.0:resource:component-id");
        IGNORED_SCOPES.add("info:escidoc/names:aa:1.0:resource:component:item");
        IGNORED_SCOPES.add("info:escidoc/names:aa:1.0:resource:component:item:container");
        IGNORED_SCOPES.add("info:escidoc/names:aa:1.0:resource:component:item:context");
        IGNORED_SCOPES.add("info:escidoc/names:aa:1.0:resource:component:item:hierarchical-containers");

        // These are rules from the "author" role which seems to be obsolete.
        IGNORED_SCOPES.add("info:escidoc/names:aa:1.0:resource:container.collection:id");
        IGNORED_SCOPES.add("info:escidoc/names:aa:1.0:resource:container:container.collection");
        IGNORED_SCOPES.add("info:escidoc/names:aa:1.0:resource:item:container.collection");
    }

    /**
     * Get the filter operation for the given function name.
     *
     * @param name function name (AND, OR, ...)
     * @return corresponding filter operation name
     */
    public String getFunction(final String name) {
        return FUNCTION_MAP.get(name);
    }

    /**
     * Check if the given scope is in the list of scopes which can be ignored.
     *
     * @param name scope name
     * @return whether or not the given scope may be ignored for filters
     */
    public boolean ignoreScope(final String name) {
        return IGNORED_SCOPES.contains(name);
    }

    /**
     * Get the entry from the operand list with the given name.
     *
     * @param name operand name
     * @return operand usable in filter query
     */
    public String getOperand(final String name) {
        return OPERAND_MAP.get(name);
    }

    /**
     * Get the entry from the scope list with the given name.
     *
     * @param name scope name
     * @return sub query representing the given scope usable in filter query
     */
    public String getScope(final String name) {
        return SCOPE_MAP.get(name);
    }

    /**
     * Escape a string.
     *
     * @param s string
     * @return the escaped string
     */
    public abstract String escape(final String s);

    /**
     * Combine the given operands with AND.
     *
     * @param operand1 first operand
     * @param operand2 second operand
     * @return AND conjunction of the given operands
     */
    public abstract String getAndCondition(final String operand1, final String operand2);

    /**
     * Get a CONTAINS statement with the given operand.
     *
     * @param operand operand
     * @return CONTAINS statement with the given operand
     */
    public abstract String getContainsCondition(final String operand);

    /**
     * Combine the given operands with =.
     *
     * @param operand1 first operand
     * @param operand2 second operand
     * @return EQUALS conjunction of the given operands
     */
    public abstract String getEqualCondition(final String operand1, final String operand2);

    /**
     * Get a condition of the form key=operand1 and value=operand2.
     *
     * @param operand1 first operand
     * @param operand2 second operand
     * @return key/value statement of the given operands
     */
    public abstract String getKeyValueCondition(final String operand1, final String operand2);

    /**
     * Get a statement which does not affect another statement when combining it with AND (evaluates to TRUE).
     *
     * @param resourceType resource type
     * @return neutral element for AND
     */
    public abstract String getNeutralAndElement(final ResourceType resourceType);

    /**
     * Get a statement which does not affect another statement when combining it with OR (evaluates to FALSE).
     *
     * @param resourceType resource type
     * @return neutral element for OR
     */
    public abstract String getNeutralOrElement(final ResourceType resourceType);

    /**
     * Combine the given operands with OR.
     *
     * @param operand1 first operand
     * @param operand2 second operand
     * @return OR conjunction of the given operands
     */
    public abstract String getOrCondition(final String operand1, final String operand2);
}
