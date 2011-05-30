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
package de.escidoc.core.test.om.item;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.resources.PropertiesProvider;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test the implementation of checksums in components.
 *
 * @author Frank Schwichtenberg
 */
public class ComponentChecksumIT extends ItemTestBase {

    private String theItemXml;

    private String theItemId;

    private static final String ELEMENT_CHECKSUM = "checksum";

    private static final String ELEMENT_CHECKSUM_ALGORITHM = "checksum-algorithm";

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        // create an item and save the id
        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        theItemXml = create(xmlData);
        theItemId = getObjidValue(theItemXml);
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Override
    @After
    public void tearDown() throws Exception {

        super.tearDown();
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
    }

    /**
     * Test sucessfully retrieving an Item with checksum for every (at least two) components.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void testRetrieveItemWithChecksums() throws Exception {
        String itemXml = retrieve(this.theItemId);
        assertValidChecksums(itemXml, 2);
    }

    /**
     * Test sucessfully updating content with same external URL and checksum is unchanged.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void testChecksumUnchanged() throws Exception {

        Document itemDoc = getDocument(this.theItemXml);
        String imageChecksum =
            selectSingleNode(itemDoc,
                "//components/component/properties" + "[mime-type = 'image/jpeg']/checksum/text()").getNodeValue();

        String fedoraUrl = PropertiesProvider.getInstance().getProperty("fedora.url", "http://localhost:8082/fedora");

        String imageUrl = fedoraUrl + "/images/newlogo2.jpg";

        // change url to the one from creation of item
        Element contentNode =
            (Element) selectSingleNode(itemDoc, "//components/component"
                + "[properties/mime-type = 'image/jpeg']/content");
        Attr attr = itemDoc.createAttributeNS(de.escidoc.core.test.Constants.XLINK_NS_URI, "xlink:href");
        attr.setValue(imageUrl);
        contentNode.setAttributeNode(attr);

        String tmp = toString(itemDoc, false);
        String itemXml = update(this.theItemId, tmp);

        // content must be renewed
        selectSingleNodeAsserted(getDocument(itemXml), "//properties/version[number = '2']");

        // check xml returned by update
        assertValidChecksums(itemXml, 2);
        assertEquals(imageChecksum, selectSingleNode(getDocument(itemXml),
            "//components/component/properties" + "[mime-type = 'image/jpeg']/checksum/text()").getNodeValue());

        // check xml returned by retrieve
        itemXml = retrieve(this.theItemId);
        assertValidChecksums(itemXml, 2);
        assertEquals(imageChecksum, selectSingleNode(getDocument(itemXml),
            "//components/component/properties" + "[mime-type = 'image/jpeg']/checksum/text()").getNodeValue());
    }

    /**
     * Test successfully updating content checksum is changed.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void testChecksumChanged() throws Exception {

        Document itemDoc = getDocument(this.theItemXml);
        String imageChecksum =
            selectSingleNode(itemDoc,
                "//components/component/properties" + "[mime-type = 'image/jpeg']/checksum/text()").getNodeValue();

        String imageUrl =
            "http://" + PropertiesProvider.getInstance().getProperty(PropertiesProvider.ESCIDOC_SERVER_NAME) + ":"
                + PropertiesProvider.getInstance().getProperty(PropertiesProvider.ESCIDOC_SERVER_PORT)
                + "/images/escidoc-logo.jpg";

        // change url to the one of a different image
        Element contentNode =
            (Element) selectSingleNode(itemDoc, "//components/component"
                + "[properties/mime-type = 'image/jpeg']/content");
        Attr attr = itemDoc.createAttributeNS(de.escidoc.core.test.Constants.XLINK_NS_URI, "xlink:href");
        attr.setValue(imageUrl);
        contentNode.setAttributeNode(attr);

        String tmp = toString(itemDoc, false);
        String itemXml = update(this.theItemId, tmp);

        // content must be renewed
        selectSingleNodeAsserted(getDocument(itemXml), "//properties/version[number = '2']");

        // check xml returned by update
        assertValidChecksums(itemXml, 2);
        assertNotEquals("Checksum still the same.", imageChecksum, selectSingleNode(getDocument(itemXml),
            "//components/component/properties" + "[mime-type = 'image/jpeg']/checksum/text()").getNodeValue());

        // check xml returned by retrieve
        itemXml = retrieve(this.theItemId);
        assertValidChecksums(itemXml, 2);
        assertNotEquals("Checksum still the same.", imageChecksum, selectSingleNode(getDocument(itemXml),
            "//components/component/properties" + "[mime-type = 'image/jpeg']/checksum/text()").getNodeValue());
    }

    /**
     * Checks for valid checksums in every component.
     *
     * @param xml           A XML document with components.
     * @param minComponents Minimum number of expected components.
     * @throws Exception In case of an error.
     */
    public void assertValidChecksums(final String xml, final int minComponents) throws Exception {

        Document itemDoc = getDocument(xml);

        // two components expected
        selectSingleNodeAsserted(itemDoc, "//components/component[" + minComponents + "]");

        NodeList list;

        // no component without checksum
        list =
            selectNodeList(itemDoc, "//components/component[not(properties/" + ELEMENT_CHECKSUM
                + ") or not(properties/" + ELEMENT_CHECKSUM_ALGORITHM + ")]");
        if (list.getLength() > 0) {
            fail("Found at least one component without element '" + ELEMENT_CHECKSUM + "' or without element '"
                + ELEMENT_CHECKSUM_ALGORITHM + "'.");
        }

        // checksum not empty
        list = selectNodeList(itemDoc, "//checksum[string-length() < 1]");
        if (list.getLength() > 0) {
            fail("Found at least one checksum element without content.");
        }

        // checksum-algorithm from enumeration specified in xml schema
        assertXmlValidItem(xml);

    }

}