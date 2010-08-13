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
package de.escidoc.core.test.om.deviation;

import de.escidoc.core.test.common.client.servlet.interfaces.ResourceHandlerClientInterface;
import de.escidoc.core.test.om.OmTestBase;

/**
 * Test the implementation of the ingest interface.
 * 
 * @author SWA
 * 
 */
public class DeviationTestBase extends OmTestBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public DeviationTestBase(final int transport) {
        super(transport);
    }

    /**
     * @return Returns the itemClient
     */
    @Override
    public ResourceHandlerClientInterface getClient() {

        return getItemClient();
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    protected void setUp() throws Exception {

        super.setUp();
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    protected void tearDown() throws Exception {

        super.tearDown();
    }

    /**
     * Ingest a resource.
     * 
     * @param id
     *            the String containing the resource-id
     * @return XML representation of result
     * @throws Exception
     *             Thrown if ingest fails.
     */
    public String export(final String id) throws Exception {
        return handleXmlResult(getDeviationClient().export(id));

    }

    /**
     * Delete Context.
     * 
     * @param id
     *            Objid of Item.
     * @param componentId
     *            the id of the component.
     * @throws Exception
     *             Thrown if delete fails.
     */
    public void getDatastreamDissimination(
            final String id, final String componentId) throws Exception {
        getDeviationClient().getDatastreamDissimination(id, componentId);
    }

}
