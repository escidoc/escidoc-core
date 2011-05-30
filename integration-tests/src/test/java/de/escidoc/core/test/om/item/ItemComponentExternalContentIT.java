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

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.remote.system.WebserverSystemException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.om.interfaces.ItemXpathsProvider;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class ItemComponentExternalContentIT extends ItemTestBase {

    private static final String STORAGE_EXTERNAL_MANAGED = "external-managed";

    private static final String STORAGE_EXTERNAL_URL = "external-url";

    private String theItemId;

    private String theItemXml;

    private String componentId;

    private String urlBeforeCreate = null;

    private String urlAfterCreate = null;

    /**
     * Test successfully creating an item with a component containing a binary content,referenced by an URL, and the
     * attribute 'storage' set to 'external-url'.
     */
    @Test
    public void testCreateItemWithExternalBinaryContentAndExternalExternalUrl() throws Exception {

        createItemWithExternalBinaryContent(STORAGE_EXTERNAL_URL);

        assertEquals("The attribute 'href' has a wrong value", urlBeforeCreate, urlAfterCreate);
    }

    /**
     * Test successfully creating an item with a component containing a binary content,referenced by an URL, and the
     * attribute 'storage' set to 'external-managed'.
     */
    @Test
    public void testCreateItemWithExternalBinaryContentAndExternalManaged() throws Exception {
        createItemWithExternalBinaryContent(STORAGE_EXTERNAL_MANAGED);
    }

    /**
     * Test declining creating an item with a component containing a binary content inline, and the attribute 'storage'
     * set to 'external-url'.
     */
    @Test
    public void testCreateItemWithExternalUrlStorageAndInlineBinary() throws Exception {
        createItemWithExternalStorageAndInlineBinary(STORAGE_EXTERNAL_URL);
    }

    /**
     * Test declining creating an item with a component containing a binary content inline, and the attribute 'storage'
     * set to 'external-managed'.
     */
    @Test
    public void testCreateItemWithExternalManagedStorageAndInlineBinary() throws Exception {
        createItemWithExternalStorageAndInlineBinary(STORAGE_EXTERNAL_MANAGED);
    }

    private void createItemWithExternalStorageAndInlineBinary(final String storage) throws Exception {
        Document item =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String storageBeforeCreate = storage;
        Document newItem =
            (Document) substitute(item, "/item/components/component[1]/content/@storage", storageBeforeCreate);
        Node itemWithoutSecondComponent = deleteElement(newItem, "/item/components/component[2]");
        String xmlData = toString(itemWithoutSecondComponent, false);
        try {
            theItemXml = create(xmlData);
            fail("No exception occurred on create item with the attribute" + " storage set to " + storage
                + " and inline binary content.");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("InvalidContentException", InvalidContentException.class, e);
        }
    }

    /**
     * Test declining creating an item with a component which has no attribute 'storage' in a content element.
     */
    @Test
    public void testCreateItemWithoutAttributeStorage() throws Exception {
        Document item =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");

        Document newItem = (Document) deleteAttribute(item, "/item/components/component[1]/content/@storage");
        Node itemWithoutSecondComponent = deleteElement(newItem, "/item/components/component[2]");
        String xmlData = toString(itemWithoutSecondComponent, false);
        try {
            theItemXml = create(xmlData);
            fail("No exception occurred on create item with component without "
                + "the attribute 'storage' in element 'content'");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("InvalidContentException", InvalidContentException.class, e);
        }
    }

    /**
     * Test successfully updating an item with a component containing a binary content inline, and the attribute
     * 'storage' set to 'external-url'. The attribute storage was set to 'internal-managed' while create.
     */
    @Test
    public void testUpdateItemWithExternalUrlStorageAndInlineBinary() throws Exception {
        updateItemWithExternalStorageAndInlineBinary(STORAGE_EXTERNAL_URL);
    }

    /**
     * Test successfully updating an item with a component containing a binary content inline, and the attribute
     * 'storage' set to 'external-managed'. The attribute storage was set to 'internal-managed' while create.
     */
    @Test
    public void testUpdateItemWithExternalManagedStorageAndInlineBinary() throws Exception {
        updateItemWithExternalStorageAndInlineBinary(STORAGE_EXTERNAL_MANAGED);
    }

    private void updateItemWithExternalStorageAndInlineBinary(final String storage) throws Exception {
        Document item =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");

        Node itemWithoutSecondComponent = deleteElement(item, "/item/components/component[2]");
        String xmlData = toString(itemWithoutSecondComponent, false);
        theItemXml = create(xmlData);
        theItemId = getObjidValue(EscidocAbstractTest.getDocument(theItemXml));
        assertXmlValidItem(xmlData);
        Document createdItem = getDocument(theItemXml);
        String componentHrefValue = selectSingleNode(createdItem, "/item/components/component/@href").getNodeValue();
        componentId = getObjidFromHref(componentHrefValue);
        Node withUpdatedStorage = substitute(createdItem, "/item/components/component/content/@storage", storage);
        String toUpdate = toString(withUpdatedStorage, true);
        update(theItemId, toUpdate);
    }

    /**
     * Test successfully updating an item with a component containing a binary content inline, and missing attribute
     * 'storage'. The attribute storage was set to 'internal-managed' while create.
     */
    @Test
    public void testUpdateItemWithoutStorageAttributeOnUpdateAndInlineBinary() throws Exception {
        Document item =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");

        Node itemWithoutSecondComponent = deleteElement(item, "/item/components/component[2]");
        String xmlData = toString(itemWithoutSecondComponent, false);
        theItemXml = create(xmlData);
        theItemId = getObjidValue(EscidocAbstractTest.getDocument(theItemXml));
        assertXmlValidItem(xmlData);
        Document createdItem = getDocument(theItemXml);
        String componentHrefValue = selectSingleNode(createdItem, "/item/components/component/@href").getNodeValue();
        componentId = getObjidFromHref(componentHrefValue);
        Node withoutStorage = deleteAttribute(createdItem, "/item/components/component/content/@storage");
        String toUpdate = toString(withoutStorage, true);
        update(theItemId, toUpdate);
    }

    /**
     * Test declining updating an item with a component containing a binary content inline, and the attribute 'storage'
     * set to 'internal-managed'. The attribute storage was set to 'external-url' while create.
     */
    @Test
    public void testUpdateItemWithStorageExternalUrlAndInlineBinary() throws Exception {
        updateItemWithInternalManagedAndInlineBinary(STORAGE_EXTERNAL_URL);
    }

    /**
     * Test declining updating an item with a component containing a binary content inline, and the attribute 'storage'
     * set to 'internal-managed'. The attribute storage was set to 'external-managed' while create.
     */
    @Test
    public void testUpdateItemWithStorageExternalManagedAndInlineBinary() throws Exception {
        updateItemWithInternalManagedAndInlineBinary(STORAGE_EXTERNAL_MANAGED);
    }

    private void updateItemWithInternalManagedAndInlineBinary(String storage) throws Exception {
        Document item =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");

        Node itemWithoutSecondComponent = deleteElement(item, "/item/components/component[1]");
        Node itemWithContentExternalUrl =
            substitute(itemWithoutSecondComponent, "/item/components/component/content/@storage", storage);
        String xmlData = toString(itemWithContentExternalUrl, false);
        theItemXml = create(xmlData);
        theItemId = getObjidValue(EscidocAbstractTest.getDocument(theItemXml));
        assertXmlValidItem(xmlData);
        Document createdItem = getDocument(theItemXml);

        Node withUpdatedStorage =
            substitute(createdItem, "/item/components/component/content/@storage", "internal-managed");
        Node withUpdatedStorageAndBinaryInline =
            substitute(withUpdatedStorage, "/item/components/component/content", "ksd asda�f ad�fa  da sa");

        String toUpdate = toString(withUpdatedStorageAndBinaryInline, true);
        try {
            update(theItemId, toUpdate);
            fail("No exception occured on update item with component which "
                + "content attribute 'storage' was set to 'external-url' on "
                + "create and inline binary content while update.");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("InvalidContentException", InvalidContentException.class, e);
        }

    }

    /**
     * Test declining Adding of a new Component with content inline and missing attribute 'storage'.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAddingComponentWithoutAttributeStorage() throws Exception {
        Document item =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");

        Node itemWithoutSecondComponent = deleteElement(item, "/item/components/component[2]");
        String xmlData = toString(itemWithoutSecondComponent, false);
        theItemXml = create(xmlData);
        theItemId = getObjidValue(EscidocAbstractTest.getDocument(theItemXml));
        assertXmlValidItem(xmlData);

        // get new component from template
        String templateComponentXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create_2_Component_Md-Records.xml");
        Node itemWithFirstComponentWithoutAttributeStorage =
            deleteAttribute(EscidocAbstractTest.getDocument(templateComponentXml),
                "/item/components/component[1]/content/@storage");
        Node newComponent =
            selectSingleNode(itemWithFirstComponentWithoutAttributeStorage, "/item/components/component[1]");

        // add new component to item
        // string op start
        String theItemXmlWith1AddComp = insertNamespacesInRootElement(theItemXml);
        theItemXmlWith1AddComp =
            theItemXmlWith1AddComp.replaceFirst("</escidocComponents:components>", toString(newComponent, true)
                + "</escidocComponents:components>");

        String newItemXml = theItemXmlWith1AddComp; // toString(curItem, false);
        try {
            update(theItemId, newItemXml);
            fail("No exception occurred on update item with a new component " + "without content attribute 'storage'");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("InvalidContentException", InvalidContentException.class, e);
        }
    }

    /**
     * Test declining retrieve a component content of a component containing the attribute 'storage' set to
     * 'external-managed' and a wrong URL.
     */
    @Test
    public void testRetrieveItemWithStorageExternalUrlAndWrongUrl() throws Exception {
        Document item =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String storageBeforeCreate = "external-managed";
        Document newItem =
            (Document) substitute(item, "/item/components/component[2]/content/@storage", storageBeforeCreate);
        Document newItem2 =
            (Document) substitute(newItem, "/item/components/component[2]/content/@href", "http://www.bla.invalid");
        Node itemWithoutSecondComponent = deleteElement(newItem2, "/item/components/component[1]");
        String xmlData = toString(itemWithoutSecondComponent, false);

        String theItemXml = create(xmlData);
        String theItemId = getObjidValue(EscidocAbstractTest.getDocument(theItemXml));

        assertXmlValidItem(xmlData);
        Document createdItem = getDocument(theItemXml);
        String componentId;
        String componentHrefValue = selectSingleNode(createdItem, "/item/components/component/@href").getNodeValue();

        componentId = getObjidFromHref(componentHrefValue);
        try {
            retrieveContent(theItemId, componentId);
            fail("No exception occurred on retrieve content of a component with "
                + "the attribute 'storage' set to 'external-managed and a wrong url " + "/ir/item/" + theItemId
                + "/components/component/" + componentId + "/content");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("WebserverSystemException", WebserverSystemException.class, e);
        }
    }
}
