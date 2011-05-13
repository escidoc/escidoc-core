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
package de.escidoc.core.test.adm;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.adm.AdminClient;
import de.escidoc.core.test.common.client.servlet.om.ItemClient;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test suite for the DeleteObjects method of the admin tool.
 *
 * @author André Schenk
 */
public class AdminToolTestBase extends EscidocAbstractTest {

    private AdminClient adminClient = null;

    private ItemClient itemClient = null;

    public AdminToolTestBase() {
        adminClient = new AdminClient();
        itemClient = new ItemClient();
        PWCallback.resetHandle();
    }

    /**
     * Create an item.
     *
     * @param xml item XML
     * @return created item
     * @throws Exception If anything fails.
     */
    protected String createItem(final String xml) throws Exception {
        return handleXmlResult(itemClient.create(xml));
    }

    /**
     * Delete objects from Fedora, resource cache and search index.
     *
     * @param xml XML with the object ids
     * @return current status as string (some statistics)
     * @throws Exception If anything fails.
     */
    protected String deleteObjects(final String xml) throws Exception {
        return handleXmlResult(adminClient.deleteObjects(xml));
    }

    /**
     * Get the current status of the purge process.
     *
     * @return current status as string (some statistics)
     * @throws Exception If anything fails.
     */
    protected String getPurgeStatus() throws Exception {
        return handleXmlResult(adminClient.getPurgeStatus());
    }

    /**
     * Get some information about the repository.
     *
     * @return repository information
     * @throws Exception If anything fails.
     */
    protected String getRepositoryInfo() throws Exception {
        return handleXmlResult(adminClient.getRepositoryInfo());
    }

    /**
     * Load the example objects into the repository.
     *
     * @param type examples type (only "common" allowed at the moment)
     * @return some information about the created objects
     * @throws Exception If anything fails.
     */
    protected String loadExamples(final String type) throws Exception {
        return handleXmlResult(adminClient.loadExamples(type));
    }

    /**
     * Get some information about the repository.
     *
     * @return repository information
     * @throws Exception If anything fails.
     */
    protected String getIndexConfiguration() throws Exception {
        return handleXmlResult(adminClient.getIndexConfiguration());
    }

    /**
     * Retrieve an item.
     *
     * @param id item id
     * @return item XML
     * @throws Exception If anything fails.
     */
    protected String retrieveItem(final String id) throws Exception {
        return handleXmlResult(itemClient.retrieve(id));
    }

    /**
     * Retrieve an item.
     *
     * @return item XML
     * @throws Exception If anything fails.
     */
    protected String reindex(final boolean clearIndex, final String indexName) throws Exception {
        return handleXmlResult(adminClient.reindex(clearIndex, indexName));
    }
}
