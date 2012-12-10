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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.Constants;

/**
 * Test the update of the component resource.
 * 
 * @author Michael Hoppe
 */
public class ItemComponentUpdateIT extends ItemTestBase {

    private final String contentHref =
        Constants.HTTP_PROTOCOL + "://" + EscidocTestBase.getBaseHost() + ":" + EscidocTestBase.getBasePort()
            + Constants.TEST_DATA_BASE_URI + "/downloadMe.txt";

    private final String contentHref1 =
        Constants.HTTP_PROTOCOL + "://" + EscidocTestBase.getBaseHost() + ":" + EscidocTestBase.getBasePort()
            + Constants.TEST_DATA_BASE_URI + "/UploadTest.zip";

    private final String itemContentXPath = "/item/components/component/content/@href";

    private final String componentContentXPath = "/component/content/@href";

    private final String itemContentMimeTypeXPath = "/item/components/component/properties/mime-type";

    private final String componentContentMimeTypeXPath = "/component/properties/mime-type";

    private final String itemContentStorageXPath = "/item/components/component/content/@storage";

    private final String componentMdTitleXPath = "/component/md-records/md-record[@name='escidoc']/publication/title";

    private final String itemMdTitleXPath =
        "/item/components/component/md-records/md-record[@name='escidoc']/publication/title";

    private final String storage = "external-url";

    private final String newTitle = "new title";

    /**
     * Test successfully updating the md-record of an existing item-component.
     */
    @Test
    public void testUpdateComponentMdRecord() throws Exception {

        Document xmlData =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_1_component_1.xml");
        xmlData = (Document) substitute(xmlData, itemContentXPath, contentHref);
        xmlData = (Document) substitute(xmlData, itemContentStorageXPath, storage);

        String itemXml = create(toString(xmlData, false));
        Document itemDoc = getDocument(itemXml);
        String itemId = getObjidValue(itemDoc);
        String componentId = getComponentObjidValue(itemDoc, 1);
        String componentXml = retrieveComponent(itemId, componentId);
        Document componentDoc = getDocument(componentXml);

        Document newComponent = (Document) substitute(componentDoc, componentMdTitleXPath, newTitle);
        componentXml = updateComponent(itemId, componentId, toString(newComponent, false));
        assertXmlEquals("title not as expected", componentXml, componentMdTitleXPath, newTitle);
    }

    /**
     * Test successfully updating the content of an existing item-component with updateComponent.
     */
    @Test
    public void testUpdateComponentContent() throws Exception {

        Document xmlData =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_1_component_1.xml");
        xmlData = (Document) substitute(xmlData, itemContentXPath, contentHref);
        xmlData = (Document) substitute(xmlData, itemContentStorageXPath, storage);

        String itemXml = create(toString(xmlData, false));
        Document itemDoc = getDocument(itemXml);
        String itemId = getObjidValue(itemDoc);
        String componentId = getComponentObjidValue(itemDoc, 1);
        String componentXml = retrieveComponent(itemId, componentId);

        Document componentDoc = getDocument(componentXml);

        Document newComponent = (Document) substitute(componentDoc, componentContentXPath, contentHref1);
        componentXml = updateComponent(itemId, componentId, toString(newComponent, false));
        assertXmlEquals("content-href not as expected", componentXml, componentContentXPath, contentHref1);
    }

    /**
     * Test successfully updating the content of an existing item-component with updateComponent without mime-type.
     */
    @Test
    public void testUpdateComponentContentWithoutMimeType() throws Exception {

        Document xmlData =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_create_content_with_mimetype.xml");

        String itemXml = create(toString(xmlData, false));
        Document itemDoc = getDocument(itemXml);
        String itemId = getObjidValue(itemDoc);
        String componentId = getComponentObjidValue(itemDoc, 1);
        String componentXml = retrieveComponent(itemId, componentId);

        Document componentDoc = getDocument(componentXml);

        Document newComponent = (Document) deleteElement(componentDoc, componentContentMimeTypeXPath);
        componentXml = updateComponent(itemId, componentId, toString(newComponent, false));
        assertXmlEquals("mime-type not as expected", componentXml, componentContentMimeTypeXPath,
            MIME_TYPE_OCTET_STREAM);
        assertEquals("mime-type not as expected", MIME_TYPE_OCTET_STREAM, retrieveContentHeader(itemId, componentId,
            RESPONSE_HEADER_MIME_TYPE));
    }

    /**
     * Test successfully updating the content of an existing item-component with updateComponent with mime-type.
     */
    @Test
    public void testUpdateComponentContentWithMimeType() throws Exception {

        Document xmlData =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_create_content_with_mimetype.xml");

        String itemXml = create(toString(xmlData, false));
        Document itemDoc = getDocument(itemXml);
        String itemId = getObjidValue(itemDoc);
        String componentId = getComponentObjidValue(itemDoc, 1);
        String componentXml = retrieveComponent(itemId, componentId);

        Document componentDoc = getDocument(componentXml);

        Document newComponent = (Document) substitute(componentDoc, componentContentMimeTypeXPath, MIME_TYPE_PDF);
        componentXml = updateComponent(itemId, componentId, toString(newComponent, false));
        assertXmlEquals("mime-type not as expected", componentXml, componentContentMimeTypeXPath, MIME_TYPE_PDF);
        assertEquals("mime-type not as expected", MIME_TYPE_PDF, retrieveContentHeader(itemId, componentId,
            RESPONSE_HEADER_MIME_TYPE));
    }

    /**
     * Test successfully updating the md-record of an existing item-component with updateItem.
     */
    @Test
    public void testUpdateItemComponentMdRecord() throws Exception {

        Document xmlData =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_1_component_1.xml");
        xmlData = (Document) substitute(xmlData, itemContentXPath, contentHref);
        xmlData = (Document) substitute(xmlData, itemContentStorageXPath, storage);
        String itemXml = create(toString(xmlData, false));
        Document itemDoc = getDocument(itemXml);
        String itemId = getObjidValue(itemDoc);

        Document newItem = (Document) substitute(itemDoc, itemMdTitleXPath, newTitle);
        itemXml = update(itemId, toString(newItem, false));
        assertXmlEquals("title not as expected", itemXml, itemMdTitleXPath, newTitle);
    }

    /**
     * Test successfully updating the content of an existing item-component with updateItem.
     */
    @Test
    public void testUpdateItemComponentContent() throws Exception {

        Document xmlData =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_1_component_1.xml");
        xmlData = (Document) substitute(xmlData, itemContentXPath, contentHref);
        xmlData = (Document) substitute(xmlData, itemContentStorageXPath, storage);
        String itemXml = create(toString(xmlData, false));
        Document itemDoc = getDocument(itemXml);
        String itemId = getObjidValue(itemDoc);

        Document newItem = (Document) substitute(itemDoc, itemContentXPath, contentHref1);
        itemXml = update(itemId, toString(newItem, false));
        assertXmlEquals("content-href not as expected", itemXml, itemContentXPath, contentHref1);
    }

    /**
     * Test successfully updating the content of an existing item-component with updateComponent without mime-type.
     */
    @Test
    public void testUpdateItemComponentContentWithoutMimeType() throws Exception {

        Document xmlData =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_create_content_with_mimetype.xml");

        String itemXml = create(toString(xmlData, false));
        Document itemDoc = getDocument(itemXml);
        String itemId = getObjidValue(itemDoc);
        String componentId = getComponentObjidValue(itemDoc, 1);

        Document newItem = (Document) deleteElement(itemDoc, itemContentMimeTypeXPath);
        itemXml = update(itemId, toString(newItem, false));
        assertXmlEquals("mime-type not as expected", itemXml, itemContentMimeTypeXPath, MIME_TYPE_OCTET_STREAM);
        assertEquals("mime-type not as expected", MIME_TYPE_OCTET_STREAM, retrieveContentHeader(itemId, componentId,
            RESPONSE_HEADER_MIME_TYPE));
    }

    /**
     * Test successfully updating the content of an existing item-component with updateComponent with mime-type.
     */
    @Test
    public void testUpdateItemComponentContentWithMimeType() throws Exception {

        Document xmlData =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_create_content_with_mimetype.xml");

        String itemXml = create(toString(xmlData, false));
        Document itemDoc = getDocument(itemXml);
        String itemId = getObjidValue(itemDoc);
        String componentId = getComponentObjidValue(itemDoc, 1);

        Document newItem = (Document) substitute(itemDoc, itemContentMimeTypeXPath, MIME_TYPE_PDF);
        itemXml = update(itemId, toString(newItem, false));
        assertXmlEquals("mime-type not as expected", itemXml, itemContentMimeTypeXPath, MIME_TYPE_PDF);
        assertEquals("mime-type not as expected", MIME_TYPE_PDF, retrieveContentHeader(itemId, componentId,
            RESPONSE_HEADER_MIME_TYPE));
    }

    /**
     * Test successfully adding a component to an item via method updateItem.
     */
    @Test
    public void testAddComponentUpdateItem() throws Exception {
        Document xmlData =
                EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                    "escidoc_item_1_component_1_1.xml");

            String itemXml = create(toString(xmlData, false));
            Document itemDoc = getDocument(itemXml);
            String itemId = getObjidValue(itemDoc);
            String componentId = getComponentObjidValue(itemDoc, 1);
            assertEquals("mime-type not as expected", MIME_TYPE_PDF, retrieveContentHeader(itemId, componentId,
                RESPONSE_HEADER_MIME_TYPE));

            Document component =
                    EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                    "component_for_create_1.xml");
            itemXml = addComponent(itemXml, component);
            String updatedItemXml = update(itemId, itemXml);
            itemDoc = getDocument(updatedItemXml);
            String newComponentId = getComponentObjidValue(itemDoc, 1);
            String newComponentId1 = getComponentObjidValue(itemDoc, 2);
            if (newComponentId.equals(componentId)) {
                assertEquals("mime-type not as expected", MIME_TYPE_JPEG, retrieveContentHeader(itemId, newComponentId1,
                        RESPONSE_HEADER_MIME_TYPE));
                assertEquals("mime-type not as expected", MIME_TYPE_PDF, retrieveContentHeader(itemId, newComponentId,
                        RESPONSE_HEADER_MIME_TYPE));
            }
            else {
                assertEquals("mime-type not as expected", MIME_TYPE_JPEG, retrieveContentHeader(itemId, newComponentId,
                        RESPONSE_HEADER_MIME_TYPE));
                assertEquals("mime-type not as expected", MIME_TYPE_PDF, retrieveContentHeader(itemId, newComponentId1,
                        RESPONSE_HEADER_MIME_TYPE));
            }
    }

    /**
     * Test successfully adding a component to an item via method createComponent.
     */
    @Test
    public void testAddComponentCreateComponent() throws Exception {
        Document xmlData =
                EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                    "escidoc_item_1_component_1_1.xml");

            String itemXml = create(toString(xmlData, false));
            Document itemDoc = getDocument(itemXml);
            String itemId = getObjidValue(itemDoc);
            String componentId = getComponentObjidValue(itemDoc, 1);
            String lmd = getLastModificationDateValue(itemDoc);
            assertEquals("mime-type not as expected", MIME_TYPE_PDF, retrieveContentHeader(itemId, componentId,
                RESPONSE_HEADER_MIME_TYPE));

            // prepare a component
            Document componentDoc =
                    EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                    "component_for_create_1.xml");

            // add last-modification-date
            NamedNodeMap atts = componentDoc.getDocumentElement().getAttributes();
            Attr newAtt = componentDoc.createAttribute("last-modification-date");
            newAtt.setNodeValue(lmd);
            atts.setNamedItem(newAtt);

            String componentXml = createComponent(itemId, toString(componentDoc, false));
            String newComponentId = getObjidValue(componentXml);
            assertEquals("mime-type not as expected", MIME_TYPE_JPEG, retrieveContentHeader(itemId, newComponentId,
                RESPONSE_HEADER_MIME_TYPE));
    }

}
