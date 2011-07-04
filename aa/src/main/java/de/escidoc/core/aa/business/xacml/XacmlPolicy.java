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
package de.escidoc.core.aa.business.xacml;

import com.sun.xacml.Policy;
import com.sun.xacml.combine.RuleCombiningAlgorithm;
import de.escidoc.core.aa.business.authorisation.CustomPolicyBuilder;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.net.URI;
import java.util.List;

/**
 * Class holding data of an XACML policy.
 *
 * @author Torsten Tetteroo
 */
public class XacmlPolicy extends Policy {

    private final String roleId;

    private static final String URN_RULE_COMBINING_ALGORITHM = "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:";

    public static final String URN_RULE_COMBINING_ALGORITHM_ORDERED_DENY_OVERRIDES =
        URN_RULE_COMBINING_ALGORITHM + "ordered-deny-overrides";

    public static final String URN_RULE_COMBINING_ALGORITHM_ORDERED_PERMIT_OVERRIDES =
        URN_RULE_COMBINING_ALGORITHM + "ordered-permit-overrides";

    /**
     * Constructor.
     *
     * @param policyId               The policy id.
     * @param ruleCombiningAlgorithm The rule combining algorithm.
     * @param description            The policy description.
     * @param targetSubjects         The subjects part of the target of the policy.
     * @param targetResources        The resources part of the target of the policy.
     * @param rules                  The policy rules.
     * @param roleId                 The role id.
     * @param actions                The actions for which this policy is defined.
     */
    public XacmlPolicy(final URI policyId, final RuleCombiningAlgorithm ruleCombiningAlgorithm,
        final String description, final List targetSubjects, final List targetResources, final List rules,
        final String roleId, final List actions) {

        super(policyId, ruleCombiningAlgorithm, description, new XacmlTarget(targetSubjects, targetResources, actions),
            rules);
        this.roleId = roleId;
    }

    /**
     * Gets the role id.
     *
     * @return Returns the role id.
     */
    public String getRoleId() {
        return this.roleId;
    }

    /**
     * See Interface for functional description.
     *
     * @see Object#toString()
     */
    public String toString() {

        try {
            return CustomPolicyBuilder.encode(this);
        }
        catch (final WebserverSystemException e) {
            return super.toString();
        }
    }

}
