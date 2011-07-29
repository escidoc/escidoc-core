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
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.BooleanAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.cond.FunctionBase;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;

import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * Implementation of a XACML (target) function that checks if at least one Attribute is contained in both Bags.<br> The
 * first parameter has to be a StringAttribute that contains a space separated list of strings. The second parameter has
 * to be a StringAttribute whose value shall be check for existing in the list. <br> The function returns true, if the
 * value of the second parameter is found in the value of the first parameter.
 *
 * @author Michael Hoppe
 */
@Service("eSciDoc.core.aa.XacmlFunctionOneAttributeInBothLists")
public class XacmlFunctionOneAttributeInBothLists extends FunctionBase {

    /**
     * The name of this function.
     */
    private static final String NAME = AttributeIds.FUNCTION_PREFIX + "one-attribute-in-both-lists";

    /**
     * The parameter types.
     */
    private static final String[] PARAMS = { StringAttribute.identifier, StringAttribute.identifier };

    /**
     * The definitions of bag or non-bag parameters.
     */
    private static final boolean[] BAG_PARAMS = { true, true };

    /**
     * The constructor.
     */
    public XacmlFunctionOneAttributeInBothLists() {

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

        if (!(argValues[0] instanceof BagAttribute) || !(argValues[1] instanceof BagAttribute)) {
            return EvaluationResult.getInstance(false);
        }

        if (argValues[0] != null && !((BagAttribute) argValues[0]).isEmpty()) {
            for (Iterator<StringAttribute> iterator = ((BagAttribute) argValues[0]).iterator(); iterator.hasNext();) {
                if (((BagAttribute) argValues[1]).contains(iterator.next())) {
                    return EvaluationResult.getInstance(true);
                }
            }
        }
        return EvaluationResult.getInstance(false);
    }
}
