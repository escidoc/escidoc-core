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

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.Policy;
import com.sun.xacml.Rule;
import com.sun.xacml.Target;
import com.sun.xacml.TargetMatch;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.combine.OrderedPermitOverridesPolicyAlg;
import com.sun.xacml.combine.OrderedPermitOverridesRuleAlg;
import com.sun.xacml.cond.Apply;
import com.sun.xacml.cond.EqualFunction;
import com.sun.xacml.cond.Evaluatable;
import com.sun.xacml.ctx.Result;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionContains;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.Values;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This is a helper class to convert an XACML policy into an SQL / Lucene
 * fragment.
 * 
 * @spring.bean id="convert.PolicyParser"
 * @author SCHE
 */
public class PolicyParser {
    private static final String ACTION_ID =
        "urn:oasis:names:tc:xacml:1.0:action:action-id";

    private static final String MATCH_PREFIX =
        "info:escidoc/names:aa:1.0:action:retrieve-";

    private static final Set<String> MATCHES = new HashSet<String>();

    static {
        MATCHES.add(MATCH_PREFIX + ResourceType.CONTAINER.getLabel());
        MATCHES.add(MATCH_PREFIX + ResourceType.CONTENT_MODEL.getLabel());
        MATCHES.add(MATCH_PREFIX + ResourceType.CONTENT_RELATION.getLabel());
        MATCHES.add(MATCH_PREFIX + ResourceType.CONTEXT.getLabel());
        MATCHES.add(MATCH_PREFIX + ResourceType.ITEM.getLabel());
        MATCHES.add(MATCH_PREFIX + ResourceType.OU.getLabel());
    }

    private ConditionParser con = null;

    private Values values = null;

    /**
     * This map only contains these actions which match the actions collected in
     * "MATCHES".
     */
    private final Map<Object, AttributeValue> actions =
        new HashMap<Object, AttributeValue>();

    /**
     * Return a list of all rules for the given resource type which match the
     * actions listed in "MATCHES".
     * 
     * @param resourceType
     *            resource type
     * 
     * @return list of matching rules
     */
    public List<String> getMatchingRules(final ResourceType resourceType) {
        List<String> result = new LinkedList<String>();

        for (Object action : actions.keySet()) {
            if (matches(actions.get(action),
                MATCH_PREFIX + resourceType.getLabel())) {
                if (action instanceof Policy) {
                    result.add(values.getNeutralAndElement(resourceType));
                }
                else if (action instanceof Rule) {
                    result.add(con.parse(((Rule) action).getCondition()));
                }
                else {
                    throw new IllegalArgumentException(action
                        + ": unknown action type");
                }
            }
        }
        return result;
    }

    /**
     * Determine whether or not the given expression is an action id.
     * 
     * @param evaluatable
     *            expression to be analyzed
     * 
     * @return true if the given expression is an action id
     */
    private boolean isActionId(final Evaluatable evaluatable) {
        return ((evaluatable != null)
            && (evaluatable instanceof AttributeDesignator)
            && (((AttributeDesignator) evaluatable)
            .getId().toString().equals(ACTION_ID)));
    }

    /**
     * Check if the given value list contains a value which has a match in
     * "MATCHES".
     * 
     * @param valueList
     *            value list
     * 
     * @return true if the given value list contains a value which has a match
     *         in "MATCHES"
     */
    private boolean matches(final Object valueList) {
        boolean result = false;

        if (!(valueList instanceof StringAttribute)) {
            throw new IllegalArgumentException(
                "only XMLSchema#string is supported");
        }
        for (String match : MATCHES) {
            if (matches(valueList, match)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Check if the given value list contains a value which matches the given
     * action.
     * 
     * @param valueList
     *            value list
     * @param action
     *            action which must be matched
     * 
     * @return true if the given value list contains a value which matches the
     *         given action
     */
    private boolean matches(final Object valueList, final String action) {
        boolean result;

        if (!(valueList instanceof StringAttribute)) {
            throw new IllegalArgumentException(
                "only XMLSchema#string is supported");
        }
        Pattern p =
            Pattern.compile(".*(\\A|\\s)" + action + "(\\s|\\z).*",
                Pattern.MULTILINE | Pattern.DOTALL);

        result =
            p.matcher(((StringAttribute) valueList).getValue()).matches();
        return result;
    }

    /**
     * Parse the given policy and collect all interesting rules (which match the
     * actions listed in "MATCHES") internally.
     * 
     * @param policy
     *            policy to be parsed
     */
    public void parse(final AbstractPolicy policy) {
        actions.clear();
        parsePolicy(policy);
        for (Object action : actions.keySet()) {
            if (action instanceof Rule) {
                parseRule((Rule) action);
            }
            else if (!(action instanceof Policy)){
                throw new IllegalArgumentException(action
                    + ": unknown action type");
            }
        }
    }

    /**
     * Parse an object.
     * 
     * @param targetObject
     *            target object
     * @param match
     *            match
     */
    private void parseAction(final Object targetObject, final Object match) {
        if (match != null) {
            if (match instanceof TargetMatch) {
                if (!((((TargetMatch) match).getMatchFunction()
                    instanceof EqualFunction) || (((TargetMatch) match)
                    .getMatchFunction() instanceof XacmlFunctionContains))) {
                    throw new IllegalArgumentException(((TargetMatch) match)
                        .getMatchFunction().getClass().getName()
                        + ": unknown action");
                }
                if (matches(((TargetMatch) match).getMatchValue())) {
                    if (isActionId(((TargetMatch) match).getMatchEvaluatable())) {
                        parseEvaluatable(((TargetMatch) match)
                            .getMatchEvaluatable());
                        actions.put(targetObject,
                            ((TargetMatch) match).getMatchValue());
                    }
                }
            }
            else if (match instanceof Iterable< ? >) {
                for (Object m : (Iterable< ? >) match) {
                    parseAction(targetObject, m);
                }
            }
            else {
                throw new IllegalArgumentException(match
                    + ": unknown action type");
            }
        }
    }

    /**
     * Parse a list of objects.
     * 
     * @param children
     *            list of objects to be parsed
     */
    private void parseChildren(final List< ? > children) {
        if (children != null) {
            for (Object child : children) {
                if (child instanceof AbstractPolicy) {
                    parsePolicy((AbstractPolicy) child);
                }
                else if (child instanceof Apply) {
                    parseCondition((Apply) child);
                }
                else if (child instanceof Evaluatable) {
                    parseEvaluatable((Evaluatable) child);
                }
                else if (child instanceof Rule) {
                    parseRule((Rule) child);
                }
                else {
                    throw new IllegalArgumentException(child
                        + ": unknown child type");
                }
            }
        }
    }

    /**
     * Parse an object.
     * 
     * @param apply
     *            object to be parsed
     */
    private void parseCondition(final Apply apply) {
        if (apply != null) {
            parseChildren(apply.getChildren());
        }
    }

    /**
     * Parse an object.
     * 
     * @param evaluatable
     *            object to be parsed
     */
    private void parseEvaluatable(final Evaluatable evaluatable) {
        if (evaluatable != null) {
            parseChildren(evaluatable.getChildren());
        }
    }

    /**
     * Parse an object.
     * 
     * @param policy
     *            object to be parsed
     */
    private void parsePolicy(final AbstractPolicy policy) {
        if (policy != null) {
            parseChildren(policy.getChildren());
            if (!((policy.getCombiningAlg()
                instanceof OrderedPermitOverridesPolicyAlg) || (policy
                .getCombiningAlg() instanceof OrderedPermitOverridesRuleAlg))) {
                throw new IllegalArgumentException(
                    "only ordered-permit-overrides is supported");
            }
            parseTarget(policy, policy.getTarget());
        }
    }

    /**
     * Parse an object.
     * 
     * @param rule
     *            object to be parsed
     */
    private void parseRule(final Rule rule) {
        if (rule != null) {
            if ((rule.getChildren() != null) && (rule.getChildren().size() > 0)) {
                throw new IllegalArgumentException(
                    "rule with children not supported");
            }
            if (rule.getEffect() != Result.DECISION_PERMIT) {
                throw new IllegalArgumentException("only Permit is supported");
            }
            parseChildren(rule.getChildren());
            parseCondition(rule.getCondition());
            parseTarget(rule, rule.getTarget());
        }
    }

    /**
     * Parse an object.
     * 
     * @param targetObject
     *            target object
     * @param target
     *            target
     */
    private void parseTarget(final Object targetObject, final Target target) {
        if (target != null) {
            if (target.getActions() != null) {
                for (Object match : target.getActions()) {
                    parseAction(targetObject, match);
                }
            }
        }
    }

    /**
     * Injects the condition parser object.
     * 
     * @spring.property ref="convert.ConditionParser"
     * @param con
     *            condition parser from Spring
     */
    public void setConditionParser(final ConditionParser con) {
        this.con = con;
    }

    /**
     * Injects the filter values object.
     * 
     * @spring.property ref="filter.Values"
     * @param values
     *            filter values object from Spring
     */
    public void setValues(final Values values) {
        this.values = values;
    }
}
