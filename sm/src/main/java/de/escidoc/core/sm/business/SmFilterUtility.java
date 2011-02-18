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
package de.escidoc.core.sm.business;

import de.escidoc.core.aa.service.interfaces.PolicyDecisionPointInterface;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.service.BeanLocator;

import java.util.Collection;
import java.util.List;

/**
 * An utility class for filtering objectids by AA-Component.
 * 
 * @spring.bean id="business.sm.FilterUtility"
 * @author MIH
 * @sm
 * 
 */
public class SmFilterUtility {

    private static PolicyDecisionPointInterface pdp;

    /**
     * Filters the provided list of object ids by evaluating the retrieve
     * privilege for the current user.
     * 
     * @param objectType
     *            The object type of the objects to filter.
     * @param objectIds
     *            The list of object ids that shall be filtered.
     * @return Returns the ids of the provided objects that the current user is
     *         allowed to retrieve.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error that prevents the
     *             filtering.
     * @sm
     */
    public static final Collection<String> filterRetrievePrivilege(
            final String objectType, final Collection<String> objectIds)
        throws WebserverSystemException {

        List<String> resultIds = null;
        if (pdp == null) {
            locateAa();
        }
        try {
            resultIds = pdp.evaluateRetrieve(
                    objectType, (List<String>) objectIds);
        }
        catch (Exception e) {
            throw new WebserverSystemException(e);
        }
        return resultIds;
    }

    /**
     * Locates the policy decision point. This should be called to initialize
     * the static field holding the pdp EJB.
     * 
     * @return PolicyDecisionPointInterface policyDecisionPointInterface
     * @throws WebserverSystemException
     *             Thrown if the policy decision point bean cannot be retrieved
     *             due to an internal error.
     * @sm
     */
    private static PolicyDecisionPointInterface locateAa()
        throws WebserverSystemException {

        pdp = BeanLocator.locatePolicyDecisionPoint();
        return pdp;
    }

}
