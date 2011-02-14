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
package de.escidoc.core.test.common.client.servlet.tme;

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.tme.JhoveHandler;
import de.escidoc.core.tme.JhoveHandlerServiceLocator;

import javax.xml.rpc.ServiceException;

/**
 * Offers access methods to the escidoc REST interface of the container
 * resource.
 * 
 * @author MSC
 * 
 */
public class JhoveClient extends ClientBase {

    private JhoveHandler soapClient = null;

    /**
     * @param transport
     *            The transport identifier.
     */
    public JhoveClient(final int transport) {
        super(transport);
    }

    /**
     * Retrieve the Containers of a Container.
     * 
     * @param param
     *            TODO
     * @return The HttpMethod after the service call (REST) or the result object
     *         (SOAP).
     * @throws Exception
     *             If the service call fails.
     */
    public Object identify(final String param) throws Exception {

        return callEsciDoc("Jhove.extract", METHOD_EXTRACT,
            Constants.HTTP_METHOD_POST, Constants.JHOVE_BASE_URI,
            new String[] {}, param);
    }

    /**
     * @return Returns the soapClient.
     * @throws ServiceException
     *             If service instantiation fails.
     */
    @Override
    public JhoveHandler getSoapClient() throws ServiceException {

        if (soapClient == null) {
            JhoveHandlerServiceLocator serviceLocator =
                new JhoveHandlerServiceLocator(getEngineConfig());
            serviceLocator
                .setJhoveHandlerServiceEndpointAddress(checkSoapAddress(serviceLocator
                    .getJhoveHandlerServiceAddress()));
            soapClient = serviceLocator.getJhoveHandlerService();
        }
        return soapClient;
    }

}
