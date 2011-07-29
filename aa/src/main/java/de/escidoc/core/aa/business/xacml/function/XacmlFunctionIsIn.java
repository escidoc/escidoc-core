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

import org.springframework.stereotype.Service;

/**
 * Implementation of an XACML (target) function that checks if a provided value can be found in a provided bag.<br> The
 * first parameter has to be a StringAttribute, the second one has to be a bag containing StringAttributes.<br> The
 * function returns true if the value of the first parameter is found in the value of the second parameter.
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.XacmlFunctionIsIn")
public class XacmlFunctionIsIn extends FunctionBase {

    /**
     * The name of this function.
     */
    private static final String NAME = AttributeIds.FUNCTION_PREFIX + "string-is-in";

    /**
     * The parameter types.
     */
    private static final String[] PARAMS = { StringAttribute.identifier, StringAttribute.identifier };

    /**
     * The definitions of bag or non-bag parameters.
     */
    private static final boolean[] BAG_PARAMS = { false, true };

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected XacmlFunctionIsIn() {

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

        final StringAttribute value1 = (StringAttribute) argValues[0];
        final StringAttribute value2 = (StringAttribute) argValues[1];
        return EvaluationResult.getInstance(value1.equals(value2));
    }

}
