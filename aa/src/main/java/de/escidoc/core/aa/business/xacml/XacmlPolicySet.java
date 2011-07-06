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

import com.sun.xacml.PolicySet;
import com.sun.xacml.Target;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.combine.CombiningAlgFactory;
import com.sun.xacml.combine.PolicyCombiningAlgorithm;
import de.escidoc.core.aa.business.authorisation.CustomPolicyBuilder;
import de.escidoc.core.aa.business.persistence.Action;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class holding the data of an XACML Policy Set.
 *
 * @author Torsten Tetteroo
 */
public class XacmlPolicySet extends PolicySet implements Serializable {

    private static final CombiningAlgFactory ALG_FACTORY = CombiningAlgFactory.getInstance();

    public static final String DEFAULT_POLICY_SET_ID = "Default-Policies";

    public static final String DEFAULT_ROLE = "Default";

    private static final String URN_POLICY_COMBINING_ALGORITHM =
        "urn:oasis:names:tc:xacml:1.0:policy-combining-algorithm:";

    public static final String URN_POLICY_COMBINING_ALGORITHM_ORDERED_PERMIT_OVERRIDES =
        URN_POLICY_COMBINING_ALGORITHM + "ordered-permit-overrides";

    public static final String URN_POLICY_COMBINING_ALGORITHM_ORDERED_DENY_OVERRIDES =
        URN_POLICY_COMBINING_ALGORITHM + "ordered-deny-overrides";

    private static final long serialVersionUID = -6321273571116516551L;

    /**
     * Creates an {@code XacmlPolicySet} object.
     *
     * @param policySetId          The id of the policy set.
     * @param combiningAlgorithmId The {@code PolicyCombiningAlgorithm}.
     * @param description          The description of the policy set.
     * @param policies             The contained Policy or PolicySet objects.
     * @throws URISyntaxException         Thrown if a provided URI is invalid.
     * @throws UnknownIdentifierException Thrown if the provided combinig algorithm id is inknown.
     */
    public XacmlPolicySet(final String policySetId, final String combiningAlgorithmId, final String description,
        final List policies) throws URISyntaxException, UnknownIdentifierException {

        this(policySetId, combiningAlgorithmId, description, null, null, policies);
    }

    /**
     * Creates an {@code XacmlPolicySet} object.
     *
     * @param policySetId          The id of the policy set.
     * @param combiningAlgorithmId The {@code PolicyCombiningAlgorithm}.
     * @param description          The description of the policy set.
     * @param targetSubjects       The subjects part of the target of the policy set.
     * @param targetResources      The resources part of the target of the policy set.
     * @param policies             The contained Policy or PolicySet objects.
     * @throws URISyntaxException         Thrown if a provided URI is invalid.
     * @throws UnknownIdentifierException Thrown if the provided combinig algorithm id is inknown.
     */
    public XacmlPolicySet(final String policySetId, final String combiningAlgorithmId, final String description,
        final List targetSubjects, final List targetResources, final List policies) throws URISyntaxException,
        UnknownIdentifierException {

        super(new URI(policySetId), getPolicyCombiningAlgorithm(combiningAlgorithmId), description, new XacmlTarget(
            targetSubjects, targetResources, new ArrayList<Action>()), policies);
    }

    /**
     * Creates an {@code XacmlPolicySet} object.
     *
     * @param policySetId          The id of the policy set.
     * @param combiningAlgorithmId The {@code PolicyCombiningAlgorithm}.
     * @param description          The description of the policy set.
     * @param target               The target of the policy set.
     * @param policies             The contained Policy or PolicySet objects.
     * @throws URISyntaxException         Thrown if a provided URI is invalid.
     * @throws UnknownIdentifierException Thrown if the provided combinig algorithm id is inknown.
     */
    public XacmlPolicySet(final String policySetId, final String combiningAlgorithmId, final String description,
        final Target target, final List policies) throws URISyntaxException, UnknownIdentifierException {

        super(new URI(policySetId), getPolicyCombiningAlgorithm(combiningAlgorithmId), description, target, policies);
    }

    /**
     * Gets the {@code PolicyCombiningAlgorithm} object for the provided combining algorithm id.
     *
     * @param combiningAlgorithmId The id of the combining algorithm.
     * @return Returns the {@code PolicyCombiningAlgorithm} object.
     * @throws UnknownIdentifierException Thrown if the provided identifier is unknown.
     * @throws URISyntaxException         Thrown if no URI can be generated for the provided id.
     */
    private static PolicyCombiningAlgorithm getPolicyCombiningAlgorithm(final String combiningAlgorithmId)
        throws UnknownIdentifierException, URISyntaxException {
        return (PolicyCombiningAlgorithm) ALG_FACTORY.createAlgorithm(new URI(combiningAlgorithmId));
    }

    /**
     * See Interface for functional description.
     *
     * @see Object#toString()
     */
    @Override
    public String toString() {

        try {
            return CustomPolicyBuilder.encode(this);
        }
        catch (final WebserverSystemException e) {
            return super.toString();
        }
    }

}
