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

import com.yourmediashelf.fedora.client.FedoraCredentials;
import com.yourmediashelf.fedora.client.response.FedoraResponse;
import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import de.escidoc.core.test.security.client.PWCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
     * @return The FOXML as String.
     * @throws Exception
     *             If the service call fails.
     */
    public Object export(final String id) throws Exception {
        String contentString = null;
        if (getTransport() == Constants.TRANSPORT_REST) {
            InputStream inStr = null;
            ByteArrayOutputStream out = null;
            try {
                com.yourmediashelf.fedora.client.FedoraClient restClient = 
                    getFedoraRestClient();
                FedoraResponse response = 
                    com.yourmediashelf.fedora.client.FedoraClient
                    .export(id).format(Constants.FOXML_FORMAT)
                                .context("public").execute(restClient);
                inStr = response.getEntityInputStream();
                out = new ByteArrayOutputStream();
                if (inStr != null) {
                    byte[] bytes = new byte[0xFFFF];
                    int i = -1;
                    while ((i = inStr.read(bytes)) > -1) {
                        out.write(bytes, 0, i);
                    }
                    out.flush();
                    contentString = new String(
                        out.toByteArray(), HttpHelper.HTTP_DEFAULT_CHARSET);
                }
                return contentString;
            } finally {
                if (inStr != null) {
                    try {
                        inStr.close();
                    } catch (IOException e) {}
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {}
                }
            }
        } else {
            return null;
        }
    }

    /**
     * get a binary datastream.
     * 
     * @param id
     *            the id of the resource.
     * @param componentId
     *            the id of the component.
     * @return The Datastream as String.
     * @throws Exception
     *             If the service call fails.
     */
    public Object getDatastreamDissimination(
            final String id, final String componentId) throws Exception {
        if (getTransport() == Constants.TRANSPORT_REST) {
            InputStream inStr = null;
            ByteArrayOutputStream out = null;
            String foxmlRecord = null;
            try {
                com.yourmediashelf.fedora.client.FedoraClient restClient = 
                    getFedoraRestClient();
                FedoraResponse response = 
                    com.yourmediashelf.fedora.client.FedoraClient
                    .getDatastreamDissemination(id, componentId).execute(restClient);
                inStr = response.getEntityInputStream();
                out = new ByteArrayOutputStream();
                if (inStr != null) {
                    byte[] bytes = new byte[0xFFFF];
                    int i = -1;
                    while ((i = inStr.read(bytes)) > -1) {
                        out.write(bytes, 0, i);
                    }
                    out.flush();
                    foxmlRecord = new String(
                        out.toByteArray(), HttpHelper.HTTP_DEFAULT_CHARSET);
                }
                String mimetype = response.getMimeType();
                return foxmlRecord;

            } finally {
                if (inStr != null) {
                    try {
                        inStr.close();
                    } catch (IOException e) {}
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {}
                }
            }
        } else {
            return null;

        }
    }

    /**
     * get the fedora describe xml.
     * 
     * @return The HttpMethod after the service call (REST) or the result object
     *         (SOAP).
     * @throws Exception
     *             If the service call fails.
     */
    public String describeFedora() throws Exception {
        if (getTransport() == Constants.TRANSPORT_REST) {
            InputStream inStr = null;
            ByteArrayOutputStream out = null;
            String contentString = null;
            try {
                com.yourmediashelf.fedora.client.FedoraClient restClient = 
                    getFedoraRestClient();
                FedoraResponse response = 
                    com.yourmediashelf.fedora.client.FedoraClient
                    .describeRepository().xml(true).execute(restClient);
                inStr = response.getEntityInputStream();
                out = new ByteArrayOutputStream();
                if (inStr != null) {
                    byte[] bytes = new byte[0xFFFF];
                    int i = -1;
                    while ((i = inStr.read(bytes)) > -1) {
                        out.write(bytes, 0, i);
                    }
                    out.flush();
                    contentString = new String(
                        out.toByteArray(), HttpHelper.HTTP_DEFAULT_CHARSET);
                }
                return contentString;

            } finally {
                if (inStr != null) {
                    try {
                        inStr.close();
                    } catch (IOException e) {}
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {}
                }
            }
        } else {
            return null;
        }
    }

    /**
     * get the fedora API A.
     * 
     * @return FedoraAPIA
     * @throws Exception
     *             If the service call fails.
     */
    private static com.yourmediashelf.fedora.client.FedoraClient 
                getFedoraRestClient()
                        throws Exception {
        com.yourmediashelf.fedora.client.FedoraClient restClient = 
            new com.yourmediashelf.fedora.client.FedoraClient(
                new FedoraCredentials(
                    new URL(Constants.PROTOCOL + "://"
                        + Constants.HOST_PORT + "/fedoradeviation"), 
                    "", PWCallback.DEFAULT_HANDLE));
        return restClient;
    }
    
}
