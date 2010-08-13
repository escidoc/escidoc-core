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

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import de.escidoc.core.test.security.client.PWCallback;
import fedora.client.FedoraClient;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.MIMETypedStream;

/**
 * Offers access methods to the escidoc interfaces.
 * 
 * @author MIH
 * 
 */
public class DeviationClient extends ClientBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public DeviationClient(final int transport) {
        super(transport);
    }

    /**
     * get a resource xml.
     * 
     * @param id
     *            the id of the resource.
     * @return The HttpMethod after the service call (REST) or the result object
     *         (SOAP).
     * @throws Exception
     *             If the service call fails.
     */
    public Object export(final String id) throws Exception {
        if (getTransport() == Constants.TRANSPORT_REST) {
            return null;
        } else {
            FedoraAPIM apim = getAPIM();
            byte[] contentStream = apim.export(
                    id, "foxml1.0", "public");

            String contentString =
                new String(contentStream, HttpHelper.HTTP_DEFAULT_CHARSET);
            return contentString;
        }
    }

    /**
     * get a binary datastream.
     * 
     * @param id
     *            the id of the resource.
     * @param componentId
     *            the id of the component.
     * @return The HttpMethod after the service call (REST) or the result object
     *         (SOAP).
     * @throws Exception
     *             If the service call fails.
     */
    public Object getDatastreamDissimination(
            final String id, final String componentId) throws Exception {
        if (getTransport() == Constants.TRANSPORT_REST) {
            return null;
        } else {
            FedoraAPIA apia = getAPIA();
            MIMETypedStream stream = apia.getDatastreamDissemination(id, 
                    componentId, null);
            return stream;

        }
    }

    /**
     * get the fedoraClient.
     * 
     * @return FedoraClient
     * @throws Exception
     *             If the service call fails.
     */
    private static FedoraClient getFedoraClient()
            throws Exception {
        try {
            FedoraClient client = 
                new FedoraClient(
                        "http://localhost:8080/axis",
                    "", PWCallback.DEFAULT_HANDLE);
            return client;
        } catch (Exception e) {
            throw new Exception("Error getting FedoraClient", e);
        }
    }

    /**
     * get the fedora API A.
     * 
     * @return FedoraAPIA
     * @throws Exception
     *             If the service call fails.
     */
    private static FedoraAPIA getAPIA()
    throws Exception {
        FedoraClient client = getFedoraClient();
        try {
            return client.getAPIA();
        } catch (Exception e) {
            throw new Exception("Error getting API-A stub", e);
        }
    }
    
    /**
     * get the fedora API M.
     * 
     * @return FedoraAPIM
     * @throws Exception
     *             If the service call fails.
     */
    private static FedoraAPIM getAPIM()
    throws Exception {
        FedoraClient client = getFedoraClient();
        try {
            return client.getAPIM();
        } catch (Exception e) {
            throw new Exception("Error getting API-M stub", e);
        }
    }
    
}
