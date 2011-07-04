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
package de.escidoc.core.aa.business.xacml.function;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BooleanAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.cond.FunctionBase;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of a XACML (target) function that checks if the first attribute contains the second one.<br> The first
 * parameter has to be a StringAttribute that contains a space separated list of strings. The second parameter has to be
 * a StringAttribute whose value shall be check for existing in the list. <br> The function returns true, if the value
 * of the second parameter is found in the value of the first parameter.
 *
 * @author Torsten Tetteroo
 */
public class XacmlFunctionContains extends FunctionBase {

    /**
     * The name of this function.
     */
    public static final String NAME = AttributeIds.FUNCTION_PREFIX + "string-contains";

    /**
     * The parameter types.
     */
    private static final String[] PARAMS = { StringAttribute.identifier, StringAttribute.identifier };

    /**
     * The definitions of bag or non-bag parameters.
     */
    private static final boolean[] BAG_PARAMS = { false, false };

    /**
     * The constructor.
     */
    public XacmlFunctionContains() {

        super(NAME, 0, PARAMS, BAG_PARAMS, BooleanAttribute.identifier, false);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public EvaluationResult evaluate(final List inputs, final EvaluationCtx context) {

        final AttributeValue[] argValues = new AttributeValue[inputs.size()];
        final EvaluationResult result = evalArgs(inputs, context, argValues);
        if (result != null) {
            return result;
        }

        final StringAttribute encodedList = (StringAttribute) argValues[0];
        final StringAttribute value = (StringAttribute) argValues[1];
        final Pattern p =
            Pattern.compile(".*(\\A|\\s)" + value.getValue() + "(\\s|\\z).*", Pattern.MULTILINE | Pattern.DOTALL);
        final Matcher m = p.matcher(encodedList.getValue());

        return EvaluationResult.getInstance(m.matches());
    }
}
