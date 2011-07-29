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

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BooleanAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.cond.FunctionBase;

import de.escidoc.core.aa.business.authorisation.Constants;
import de.escidoc.core.aa.business.authorisation.CustomEvaluationResultBuilder;
import de.escidoc.core.aa.business.authorisation.FinderModuleHelper;
import de.escidoc.core.aa.business.SecurityHelper;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.common.business.aa.authorisation.AttributeIds;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;

/**
 * Implementation of an XACML (target) function that checks if a role has been granted to the current user (for an
 * object).<br> The first parameter holds the role name, the second one the object type of the object that shall be
 * checked. <br> This function returns {@code true}, <ul> <li>if the role is the dummy role holding the policies of
 * the default user. <li>if the role is a unlimited role and has been granted to the subject(user), or</li> <li>if the
 * role is a limited role and has been granted for the object identified by the resource-id of the context to the
 * current user (subject).</li> </ul>
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.XacmlFunctionRoleIsGranted")
public class XacmlFunctionRoleIsGranted extends FunctionBase {

    private static final Pattern SPLIT_PATTERN = Pattern.compile("/");

    /**
     * The pattern used to check if a role name is the name of the dummy roles holding the default policies.
     */
    private static final Pattern PATTERN_DEFAULT_USER_ROLE_ID = Pattern.compile(EscidocRole.DEFAULT_USER_ROLE_ID);

    /**
     * The name of this function.
     */
    public static final String NAME = AttributeIds.FUNCTION_PREFIX + "role-is-granted";

    @Autowired
    @Qualifier("security.SecurityHelper")
    private SecurityHelper securityHelper;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected XacmlFunctionRoleIsGranted() {
        super(NAME, 0, StringAttribute.identifier, false, 2, BooleanAttribute.identifier, false);
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public EvaluationResult evaluate(final List inputs, final EvaluationCtx ctx) {

        try {
            final AttributeValue[] argValues = new AttributeValue[inputs.size()];
            final EvaluationResult result = evalArgs(inputs, ctx, argValues);
            if (result != null) {
                return result;
            }
            // Get the role name from the input and check if it is the dummy
            // role for default policies
            // The policyId is concatenated String
            // containing <roleName>/<user or group>/<userorGroupId>
            final String policyId = ((StringAttribute) argValues[0]).getValue();
            final String[] parts = SPLIT_PATTERN.split(policyId);
            final StringBuilder roleIdentifier = new StringBuilder("");
            if (parts.length > 2) {
                for (int i = 0; i < parts.length - 2; i++) {
                    roleIdentifier.append(parts[i]);
                }
            }
            else {
                roleIdentifier.append(policyId);
            }
            final String roleId = roleIdentifier.toString();
            if (PATTERN_DEFAULT_USER_ROLE_ID.matcher(roleId).find()) {
                return EvaluationResult.getInstance(true);
            }
            // Get the userOrGroupid from the policyId
            final String userOrGroupId =
                parts.length > 2 ? parts[parts.length - 1] : FinderModuleHelper.retrieveSingleSubjectAttribute(ctx,
                    Constants.URI_SUBJECT_ID, true);

            // Get the resource id from the context
            final String resourceId = FinderModuleHelper.getResourceId(ctx);

            // Fetch the role identified by the role name
            final EscidocRole role = securityHelper.getRole(roleId);

            return securityHelper.getRoleIsGrantedEvaluationResult(userOrGroupId, role.getId(), resourceId, role, ctx);

        }
        catch (final ResourceNotFoundException e) {
            return CustomEvaluationResultBuilder.createResourceNotFoundResult(e);
        }
        catch (final Exception e) {
            return CustomEvaluationResultBuilder.createProcessingErrorResult(e);
        }
    }

}
