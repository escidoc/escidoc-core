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
package de.escidoc.core.test.common.fedora;

import org.fcrepo.client.FedoraClient;
import org.fcrepo.server.access.FedoraAPIA;
import org.fcrepo.server.management.FedoraAPIM;
import org.fcrepo.server.types.gen.Datastream;
import org.fcrepo.server.types.gen.MIMETypedStream;

import de.escidoc.core.common.exceptions.remote.system.FedoraSystemException;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import de.escidoc.core.test.common.resources.PropertiesProvider;

/**
 * An utility class for Fedora requests.
 * 
 * @author Rozita Friedman
 */
public class Client {

    private FedoraAPIM apim;

    private FedoraAPIA apia;

    private FedoraClient fc;

    /**
     * Fedora Client (with configuration from escidoc.properties).
     * 
     * @throws Exception
     *             Thrown if getting instance of FedoraClient failed.
     */
    public Client() throws Exception {

        try {
            fc =
                new FedoraClient(PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_URL),
                    PropertiesProvider.getInstance().getProperty(PropertiesProvider.FEDORA_USER), PropertiesProvider
                        .getInstance().getProperty(PropertiesProvider.FEDORA_PASSWORD));
            apia = fc.getAPIA();
            apim = fc.getAPIM();

        }
        catch (final Exception e) {
            throw new FedoraSystemException();
        }
    }

    /**
     * The method fetches content of the datastream with provided id from Fedora-object with provided id via Fedora
     * APIA-Webservice getDatastreamDissemination()and converts content of the datastream to string.
     * 
     * @param datastreamId
     *            id of the datastream to fetch
     * @param pid
     *            id of the Fedora-object
     * @return content of the datastream as string
     * @throws FedoraSystemException
     *             Thrown if getting Content from Fedora failed.
     */
    public String getDatastreamContent(final String datastreamId, final String pid) throws FedoraSystemException {

        MIMETypedStream datastream = null;
        String content = null;
        // get content of data stream with provided ID from
        // Fedora object with provided id
        try {
            datastream = apia.getDatastreamDissemination(pid, datastreamId, null);

            byte[] streamContent = datastream.getStream();
            // convert to String
            content = new String(streamContent, HttpHelper.HTTP_DEFAULT_CHARSET);

        }
        catch (final Exception e) {

            throw new FedoraSystemException();
        }
        return content;
    }

    /**
     * The method retrieves metadata for a datastream of the fedora object with provided id.
     * 
     * @param pid
     *            Id of Fedora object
     * @param dsId
     *            Id of datastream
     * @return Datastream
     * @throws FedoraSystemException
     *             Thrown if access to Fedora failed.
     */
    public Datastream getDatastreamInformation(final String pid, final String dsId) throws FedoraSystemException {
        Datastream datastreamInfos = null;
        try {
            datastreamInfos = apim.getDatastream(pid, dsId, null);
        }
        catch (final Exception e) {
            throw new FedoraSystemException();
        }
        return datastreamInfos;
    }
}
