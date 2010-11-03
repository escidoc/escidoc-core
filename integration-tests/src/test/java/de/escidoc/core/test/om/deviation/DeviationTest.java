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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.w3c.dom.Document;

import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.om.OmTestBase;

/**
 * Test ingesting resource via ingest interface.<br>
 * By default, the tests are executed using a depositor user.
 * 
 * @author SWA, KST
 * 
 */
//@RunWith(value = Parameterized.class)
public class DeviationTest extends DeviationTestBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public DeviationTest(final int transport) {
        super(transport);
    }

    /**
     * Clean up after test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @After
    public void tearDown() throws Exception {

        super.tearDown();
    }

    /**
     * Test retrieving the fedora describe xml.
     * 
     * @throws Exception e
     */
    @Test
    public void testDescribe() throws Exception {
        String describe = getDescribe();
        assertTrue(describe.contains("repositoryName"));
    }

    /**
     * Test retrieving an item-xml.
     * 
     * @throws Exception e
     */
    @Test
    public void testExport() throws Exception {

        String toBeCreatedXml =
            EscidocRestSoapTestBase
                .getTemplateAsString(TEMPLATE_ITEM_PATH + "/" 
                		+ getTransport(false), 
                            "escidoc_test_item0.xml");

        String createdXml = create(toBeCreatedXml);
        String id = getObjidValue(createdXml);
        String itemXml = export(id);
        assertXmlValidItem(itemXml);

    }

    /**
     * Test retrieving an content.
     * 
     * @throws Exception e
     */
    @Test
    public void testDatastreamDissimination() throws Exception {

        String toBeCreatedXml =
            EscidocRestSoapTestBase
            .getTemplateAsString(TEMPLATE_ITEM_PATH + "/" 
                    + getTransport(false), 
                        "escidoc_test_item0.xml");

        String createdXml = create(toBeCreatedXml);
        Document document =
            EscidocRestSoapTestBase.getDocument(createdXml);
        String id = getObjidValue(createdXml);
        String componentId =
            getObjidValue(document, OmTestBase.XPATH_ITEM_COMPONENTS
                + "/" + NAME_COMPONENT);
        String content = (String)getDatastreamDissimination(
                id, Constants.ITEM_BASE_URI + "/"
                + id + "/" + Constants.SUB_COMPONENT + "/"
                + componentId 
                + "/" + Constants.SUB_CONTENT);
        assertTrue(content.contains("Antriebsvorrichtung"));

    }

}
