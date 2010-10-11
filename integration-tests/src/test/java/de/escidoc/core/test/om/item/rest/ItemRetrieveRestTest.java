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
package de.escidoc.core.test.om.item.rest;

import org.junit.Test;

import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.om.item.ItemTestBase;

/**
 * Item tests with REST transport.
 * 
 * @author MSC
 * 
 */
public class ItemRetrieveRestTest extends ItemTestBase {

    /**
     * Constructor.
     * 
     */
    public ItemRetrieveRestTest() {
        super(Constants.TRANSPORT_REST);
    }

    /**
     * Test retrieve resources of Item.
     * 
     * @throws Exception
     *             Thrown if anything fails.
     */
    @Test
    public void testRetrieveResources() throws Exception {
        String xml =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        String itemXml = create(xml);
        String itemId = getObjidValue(itemXml);

        String resources = retrieveResources(itemId);
        assertXmlValidItem(resources);

    }

}
