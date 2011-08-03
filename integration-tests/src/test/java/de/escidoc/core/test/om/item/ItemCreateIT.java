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

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Test creating Item objects.
 * 
 * @author Steffen Wagner
 */
public class ItemCreateIT extends ItemTestBase {

    /**
     * Test exception if XML string is empty.
     * 
     * @throws Exception
     *             Thrown if creation of example Item failed.
     */
    @Test(expected = MissingMethodParameterException.class)
    public void testEmptyCreate01() throws Exception {

        create("");
    }

    /**
     * Test if version public-status is ignored if an Item is created. (See issue INFR-775)
     * 
     * @throws Exception
     *             Thrown if creation of example Item failed.
     */
    @Test
    public void testIgnoreStatus01() throws Exception {

        // create an item an replace the value of the public-status element
        String itemXml = getExampleTemplate("item-minimal-for-create-01.xml");
        String xml = create(itemXml);

        Document item = EscidocAbstractTest.getDocument(xml);
        Node itemChanged = substitute(item, "/item/properties/public-status", "released");
        itemChanged = substitute(itemChanged, "/item/properties/version/status", "released");
        String xmlTmp = toString(itemChanged, false);

        String xml2 = create(xmlTmp);

        assertXmlExists("Wrong pulic-status for created Item.", xml2,
            "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status for created Item.", xml2,
            "/item/properties/version/status[text() = 'pending']");
    }

    /**
     * Test if version public-status is ignored if an Item is created. The public-status is set to in-revision (see
     * issue INFR-775, i guess in-rework means in-revision).
     * 
     * @throws Exception
     *             Thrown if creation of example Item failed.
     */
    @Test
    public void testIgnoreStatus02() throws Exception {

        // create an item an replace the value of the public-status element
        String itemXml = getExampleTemplate("item-minimal-for-create-01.xml");
        String xml = create(itemXml);

        Document item = EscidocAbstractTest.getDocument(xml);
        Node itemChanged = substitute(item, "/item/properties/public-status", "in-revision");
        itemChanged = substitute(itemChanged, "/item/properties/version/status", "in-revision");
        String xmlTmp = toString(itemChanged, false);

        String xml2 = create(xmlTmp);

        assertXmlExists("Wrong pulic-status for created Item.", xml2,
            "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status for created Item.", xml2,
            "/item/properties/version/status[text() = 'pending']");
    }

    /**
     * Test if version and public-status is ignored if an Item is created. The public-status is set to withdrawn.
     * 
     * @throws Exception
     *             Thrown if creation of example Item failed.
     */
    @Test
    public void testIgnoreStatus03() throws Exception {

        // create an item an replace the value of the public-status element
        String itemXml = getExampleTemplate("item-minimal-for-create-01.xml");
        String xml = create(itemXml);

        Document item = EscidocAbstractTest.getDocument(xml);
        Node itemChanged = substitute(item, "/item/properties/public-status", "withdrawn");
        itemChanged = substitute(itemChanged, "/item/properties/version/status", "withdrawn");
        String xmlTmp = toString(itemChanged, false);

        String xml2 = create(xmlTmp);

        assertXmlExists("Wrong pulic-status for created Item.", xml2,
            "/item/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status for created Item.", xml2,
            "/item/properties/version/status[text() = 'pending']");
    }

    /**
     * Test if special characters are handled correctly.
     * 
     * @throws Exception
     *             Thrown if creation of example Item failed.
     */
    @Test
    public void testSpecialCharacters() throws Exception {
        String inputXml = getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "/item_fuer_umlaut_create_mpdl.xml");
        Document inputDoc = EscidocAbstractTest.getDocument(inputXml);

        String outputXml = create(inputXml);
        Document outputDoc = EscidocAbstractTest.getDocument(outputXml);

        String mdTitlePath = "/item/md-records/md-record/publication/title";
        Node inputMdTitle = selectSingleNode(inputDoc, mdTitlePath);
        Node outputMdTitle = selectSingleNode(outputDoc, mdTitlePath);

        assertXmlEquals("", inputMdTitle, outputMdTitle);
    }

    /**
     * Test unexpected parser exception instead of InvalidXmlException during create (see issue INFR-911).
     * 
     * @throws Exception
     *             Thrown if behavior is not as expected.
     */
    @Test(expected = InvalidXmlException.class)
    public void testInvalidXml() throws Exception {

        /*
         * The infrastructure has thrown an unexpected parser exception during creation if a non XML data structure is
         * send (e.g. String).
         */
        create("laber-rababer");
    }

    /**
     * Test invalid XML in createComponent.
     * 
     * @throws Exception
     *             Thrown if behavior is not as expected.
     */
    @Test(expected = XmlCorruptedException.class)
    public void invalidXmlOncreateComponent() throws Exception {

        createComponent("escidoc:123", "laber-rababer");
    }

    /**
     * Test creation of a component by calling createComponent method.
     * 
     * @throws Exception
     *             Thrown if creation of component fail.
     */
    @Test
    public void createComponent01() throws Exception {

        // create an item an replace the value of the public-status element
        String itemXml = getExampleTemplate("item-minimal-for-create-01.xml");
        String xml = create(itemXml);
        Document itemDoc = EscidocAbstractTest.getDocument(xml);
        String itemId = getObjidValue(itemDoc);
        String lmd = getLastModificationDateValue(itemDoc);

        // prepare a component
        Document componentDoc =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "component_for_create.xml");

        // add last-modification-date
        NamedNodeMap atts = componentDoc.getDocumentElement().getAttributes();
        Attr newAtt = componentDoc.createAttribute("last-modification-date");
        newAtt.setNodeValue(lmd);
        atts.setNamedItem(newAtt);

        String xml2 = createComponent(itemId, toString(componentDoc, false));

        // TODO intensivate checks
        assertXmlValidComponent(xml2);
        assertXmlExists("Missing created Component", xml2, "/component/properties/valid-status[text() = 'valid']");
    }
}
