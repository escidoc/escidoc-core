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
package de.escidoc.core.test.common.client.servlet.aa;

import de.escidoc.core.aa.PolicyDecisionPoint;
import de.escidoc.core.aa.PolicyDecisionPointServiceLocator;
import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;

import javax.xml.rpc.ServiceException;

/**
 * Offers access methods to the escidoc REST and soap interface of the policy
 * decision point.
 * 
 * @author TTE
 * 
 */
public class PolicyDecisionPointClient extends ClientBase {

    private PolicyDecisionPoint soapClient = null;

    /**
     * 
     * @param transport
     *            The transport identifier.
     */
    public PolicyDecisionPointClient(final int transport) {
        super(transport);
    }

    /**
     * 
     * @return Returns the soapClient.
     * @throws ServiceException
     *             If the client creation fails.
     */
    @Override
    public PolicyDecisionPoint getSoapClient() throws ServiceException {

        if (soapClient == null) {
            PolicyDecisionPointServiceLocator serviceLocator =
                new PolicyDecisionPointServiceLocator(getEngineConfig());
            serviceLocator
                .setPolicyDecisionPointServiceEndpointAddress(checkSoapAddress(serviceLocator
                    .getPolicyDecisionPointServiceAddress()));
            soapClient = serviceLocator.getPolicyDecisionPointService();
        }
        return soapClient;
    }

    /**
     * Evaluates the provided authorization requests.
     * 
     * @param requestsXml
     *            The xml representation of a list of authorization requests.
     * @return The HttpMethod after the service call (REST) or the result object
     *         (SOAP).
     * @throws Exception
     *             If the service call fails.
     */
    public Object evaluate(final String requestsXml) throws Exception {

        return callEsciDoc("Pdp.evaluate", METHOD_EVALUATE,
            Constants.HTTP_METHOD_PUT, Constants.PDP_BASE_URI, new String[] {},
            changeToString(requestsXml));
    }
}
