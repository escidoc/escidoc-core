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
package de.escidoc.core.test.load;

import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.logger.AppLogger;

/**
 * Test the mock implementation of the item resource.
 * 
 * @author MSC
 * 
 */
public class ProfileTest extends LoadTestBase {

    protected static AppLogger log = new AppLogger(ProfileTest.class.getName());

    private final boolean clearLogFile = true;

    /**
     * @param transport
     *            The transport identifier.
     */
    public ProfileTest(final int transport) {

        super(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    protected void setUp() throws Exception {
        initLogFile(clearLogFile);
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
     * Successful creation of a Context.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void notestRetrieveItem() throws Exception {
        String id = "escidoc:526";
        // String id = "escidoc:8";
        String item = retrieve(id);
        // id = "escidoc:4";
        // item = retrieve(id);
    }

    /**
     * Successful creation of a Context.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testCreateItem() throws Exception {

        String item =
            create(EscidocRestSoapTestsBase.getTemplateAsString(
                TEMPLATE_ITEM_PATH, "escidoc_item_198_for_create.xml"));
        String id = getObjidValue(EscidocRestSoapTestsBase.getDocument(item));
        if (log.isDebugEnabled()) {
            log.debug("Created Item with Id: " + id + ".");
        }

        // submit(id,
        // getTaskParam(getLastModificationDateValue(getDocument(item))));
        // String submitted = retrieve(id);
        // Thread.sleep(1);
        // release(id,
        // getTaskParam(getLastModificationDateValue(getDocument(submitted))));
        // String released = retrieve(id);
    }
}
