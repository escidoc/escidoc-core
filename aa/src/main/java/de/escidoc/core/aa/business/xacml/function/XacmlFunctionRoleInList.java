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

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.PolicyTreeElement;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BooleanAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.cond.FunctionBase;
import com.sun.xacml.finder.PolicyFinderResult;
import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.aa.business.SecurityHelper;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.UserContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * Implementation of an XACML (target) function that checks if a role has been granted to the current user (for an
 * object).<br> The first parameter holds the role name, the second one the object type of the object that shall be
 * checked. <br> This function returns {@code true}, <ul> <li>if the role is the dummy role holding the policies of
 * the default user. <li>if the role is a unlimited role and has been granted to the subject(user), or</li> <li>if the
 * role is a limited role and has been granted for the object identified by the resource-id of the context to the
 * current user (subject).</li> </ul>
 *
 * @author Michael Hoppe
 */
@Service("eSciDoc.core.aa.XacmlFunctionRoleInList")
public class XacmlFunctionRoleInList extends FunctionBase {

    /**
     * The name of this function.
     */
    private static final String NAME = AttributeIds.FUNCTION_PREFIX + "role-in-list";

    @Autowired
    @Qualifier("security.SecurityHelper")
    private SecurityHelper securityHelper;

    /**
     * Private constructor to prevent initialization.
     */
    protected XacmlFunctionRoleInList() {
        super(NAME, 0, StringAttribute.identifier, false, 1, BooleanAttribute.identifier, false);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public EvaluationResult evaluate(final List inputs, final EvaluationCtx ctx) {

        try {
            final AttributeValue[] argValues = new AttributeValue[inputs.size()];
            EvaluationResult result = evalArgs(inputs, ctx, argValues);
            if (result != null) {
                return result;
            }

            // Get the roles of the user
            final PolicyFinderResult policyFinderResult =
                new PolicyFinderResult(securityHelper.getUserPolicies(getUserId(), null));
            final Collection<String> roleNames = getRoleNames(policyFinderResult.getPolicy(), new ArrayList<String>());

            // Compare roles of user with roles in List
            if (argValues != null && argValues[0] != null) {
                final String compareString = argValues[0].encode().toLowerCase(Locale.ENGLISH);
                for (final String roleName : roleNames) {
                    if (compareString.contains(roleName)) {
                        result = EvaluationResult.getInstance(true);
                        return result;
                    }
                }
            }
            result = EvaluationResult.getInstance(false);
            return result;
        }
        catch (final Exception e) {
            return CustomEvaluationResultBuilder.createProcessingErrorResult(e);
        }
    }

    /**
     * Parse name of the Roles the user is in from the attached Policy-Set.
     *
     * @param policy    policySet
     * @param roleNames Collection with name of roles
     * @return Collection roleNames
     */
    private static Collection<String> getRoleNames(final PolicyTreeElement policy, final Collection<String> roleNames) {
        if (policy != null) {
            try {
                roleNames.add(policy.getId().getPath().toLowerCase(Locale.ENGLISH));
            }
            catch (final Exception e) {
                if (policy.getChildren() != null) {
                    AbstractPolicy abstractPolicy = null;
                    for (final Object o : policy.getChildren()) {
                        try {
                            abstractPolicy = (AbstractPolicy) o;
                            final PolicyTreeElement policyReference = abstractPolicy;
                            roleNames.add(policyReference.getId().getPath().toLowerCase(Locale.ENGLISH));
                        }
                        catch (final Exception e1) {
                            getRoleNames(abstractPolicy, roleNames);
                        }
                    }
                }
            }
        }
        return roleNames;
    }

    /**
     * gets UserId from UserContext.
     * <p/>
     * <pre>
     * </pre>
     *
     * @return String userId
     * @throws de.escidoc.core.common.exceptions.system.WebserverSystemException
     */
    private static String getUserId() throws WebserverSystemException {
        return UserContext.getId();
    }

}
