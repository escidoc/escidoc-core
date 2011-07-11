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

import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Test suite for the DeleteObjects method of the admin tool.
 *
 * @author André Schenk
 */
public class DeleteObjectsIT extends AdminToolTestBase {

    /**
     * Delete a list of objects from Fedora and search index.
     *
     * @throws Exception If anything fails.
     */
    @Test(timeout = 30000)
    public void testDeleteObjects() throws Exception {
        // create item
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String itemId = getObjidValue(createItem(xml));

        // delete item
        xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<param><id>" + itemId + "</id></param>";
        deleteObjects(xml);

        // wait until process has finished
        final int waitTime = 5000;

        while (true) {
            String status = getPurgeStatus();
            if (status.indexOf("finished") > 0) {
                break;
            }
            Thread.sleep(waitTime);
        }

        // check if item still exists
        try {
            retrieveItem(itemId);
            fail("item with id " + itemId + " still exists");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ItemNotFoundException.class, e);
        }
    }
}
