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
 * Test the implementation of the deviation interface.
 *
 * @author Michael Hoppe
 */
public class DeviationTestBase extends OmTestBase {

    /**
     * @return Returns the itemClient
     */
    @Override
    public ResourceHandlerClientInterface getClient() {

        return getItemClient();
    }

    /**
     * Test fedora.describe via deviation-handler.
     *
     * @return describe-XML
     * @throws Exception Thrown if delete fails.
     */
    public String getDescribe() throws Exception {
        return getDeviationClient().describeFedora();
    }

    /**
     * Test fedora.export via deviation-handler.
     *
     * @param id the String containing the resource-id
     * @return XML representation of result
     * @throws Exception Thrown if ingest fails.
     */
    public String export(final String id) throws Exception {
        return getDeviationClient().export(id);

    }

    /**
     * Test fedora.getDatastreamDissimination via deviation-handler.
     *
     * @param id          Objid of Item.
     * @param componentId the id of the component.
     * @return binary object as String
     * @throws Exception Thrown if delete fails.
     */
    public String getDatastreamDissimination(final String id, final String componentId) throws Exception {
        return getDeviationClient().getDatastreamDissimination(id, componentId);
    }

}
