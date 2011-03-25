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
package de.escidoc.core.test.common.client.servlet.om;

import de.escidoc.core.om.SemanticStoreHandler;
import de.escidoc.core.om.SemanticStoreHandlerServiceLocator;
import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;

import javax.xml.rpc.ServiceException;

/**
 * Offers access methods to the escidoc REST interface of the container
 * resource.
 * 
 * @author Michael Schneider
 * 
 */
public class SemanticStoreClient extends ClientBase {

    private SemanticStoreHandler soapClient = null;

    /**
     * @param transport
     *            The transport identifier.
     */
    public SemanticStoreClient(final int transport) {
        super(transport);
    }

    /**
     * Retrieve the Containers of a Container.
     * 
     * @param queryParam
     *            The id of the container.
     * @return The HttpMethod after the service call (REST) or the result object
     *         (SOAP).
     * @throws Exception
     *             If the service call fails.
     */
    public Object spo(final String queryParam) throws Exception {

        return callEsciDoc("SemanticStore.spo", METHOD_SPO,
            Constants.HTTP_METHOD_POST, Constants.SEMANTIC_STORE_BASE_URI
                + Constants.SPO, new String[] {}, queryParam);
    }

    /**
     * @return Returns the soapClient.
     * @throws ServiceException
     *             If service instantiation fails.
     */
    public SemanticStoreHandler getSoapClient() throws ServiceException {

        if (soapClient == null) {
            SemanticStoreHandlerServiceLocator serviceLocator =
                new SemanticStoreHandlerServiceLocator(getEngineConfig());
            serviceLocator
                .setSemanticStoreHandlerServiceEndpointAddress(checkSoapAddress(serviceLocator
                    .getSemanticStoreHandlerServiceAddress()));
            soapClient = serviceLocator.getSemanticStoreHandlerService();
        }
        return soapClient;
    }

}
