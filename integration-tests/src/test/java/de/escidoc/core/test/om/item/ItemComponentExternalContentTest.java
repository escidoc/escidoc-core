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

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidContentException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.om.interfaces.ItemXpathsProvider;

/**
 * Test the mock implementation of the item resource.
 * 
 * @author MSC
 * 
 */
public class ItemComponentExternalContentTest extends ItemTestBase
    implements ItemXpathsProvider {

    protected String theItemId;

    protected String theItemXml;

    protected String componentId;

    private String urlBeforeCreate = null;

    private String urlAfterCreate = null;

    /**
     * @param transport
     *            The transport identifier.
     */
    public ItemComponentExternalContentTest(final int transport) {
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
     * Test successfully creating an item with a component containing a binary
     * content,referenced by an URL, and the attribute 'storage' set to
     * 'external-url'.
     * 
     * @throws Exception
     */
    public void testCreateItemWithExternalBinaryContentAndExternalExternalUrl()
        throws Exception {
        createItemWithExternalBinaryContent("external-url");

        assertEquals("The attribute 'href' has a wrong value", urlBeforeCreate,
            urlAfterCreate);
    }

    /**
     * Test successfully creating an item with a component containing a binary
     * content,referenced by an URL, and the attribute 'storage' set to
     * 'external-managed'.
     * 
     * @throws Exception
     */
    public void testCreateItemWithExternalBinaryContentAndExternalManaged()
        throws Exception {
        createItemWithExternalBinaryContent("external-managed");
    }

    public void createItemWithExternalBinaryContent(final String storage)
        throws Exception {
        Document item =
            EscidocRestSoapTestsBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        String storageBeforeCreate = storage;
        urlBeforeCreate =
            selectSingleNode(item,
                "/item/components/component[2]/content/@href").getNodeValue();
        Document newItem =
            (Document) substitute(item,
                "/item/components/component[2]/content/@storage",
                storageBeforeCreate);
        Node itemWithoutSecondComponent =
            deleteElement(newItem, "/item/components/component[1]");
        String xmlData = toString(itemWithoutSecondComponent, false);
        // System.out.println("item " + xmlData);
        theItemXml = create(xmlData);
        theItemId =
            getObjidValue(EscidocRestSoapTestsBase.getDocument(theItemXml));
        assertXmlValidItem(xmlData);
        Document createdItem = getDocument(theItemXml);
        if (getTransport(true).equals("REST")) {
            String componentHrefValue =
                selectSingleNode(createdItem,
                    "/item/components/component/@href").getNodeValue();
            componentId = getObjidFromHref(componentHrefValue);
        }
        else {
            componentId =
                selectSingleNode(createdItem,
                    "/item/components/component/@objid").getNodeValue();
        }
        urlAfterCreate =
            selectSingleNode(createdItem,
                "/item/components/component/content/@href").getNodeValue();
        String storageAfterCtreate =
            selectSingleNode(createdItem,
                "/item/components/component/content/@storage").getNodeValue();
        assertEquals("The attribute 'storage' has a wrong valuue",
            storageBeforeCreate, storageAfterCtreate);
        // String retrievedItem = retrieve(theItemId);
        // System.out.println("item " + retrievedItem);
    }

    /**
     * Test declining creating an item with a component containing a binary
     * content inline, and the attribute 'storage' set to 'external-url'.
     * 
     * @throws Exception
     */
    public void testCreateItemWithExternalUrlStorageAndInlineBinary()
        throws Exception {
        createItemWithExternalStorageAndInlineBinary("external-url");
    }

    /**
     * Test declining creating an item with a component containing a binary
     * content inline, and the attribute 'storage' set to 'external-managed'.
     * 
     * @throws Exception
     */
    public void testCreateItemWithExternalManagedStorageAndInlineBinary()
        throws Exception {
        createItemWithExternalStorageAndInlineBinary("external-managed");
    }

    public void createItemWithExternalStorageAndInlineBinary(
        final String storage) throws Exception {
        Document item =
            EscidocRestSoapTestsBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        String storageBeforeCreate = storage;
        Document newItem =
            (Document) substitute(item,
                "/item/components/component[1]/content/@storage",
                storageBeforeCreate);
        Node itemWithoutSecondComponent =
            deleteElement(newItem, "/item/components/component[2]");
        String xmlData = toString(itemWithoutSecondComponent, false);
        try {
            theItemXml = create(xmlData);
            fail("No exception occurred on create item with the attribute"
                + " storage set to " + storage + " and inline binary content.");
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "InvalidContentException", InvalidContentException.class, e);
        }
    }

    /**
     * Test declining creating an item with a component which has no attribute
     * 'storage' in a content element.
     * 
     * @throws Exception
     */
    public void testCreateItemWithoutAttributeStorage() throws Exception {
        Document item =
            EscidocRestSoapTestsBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");

        Document newItem =
            (Document) deleteAttribute(item,
                "/item/components/component[1]/content/@storage");
        Node itemWithoutSecondComponent =
            deleteElement(newItem, "/item/components/component[2]");
        String xmlData = toString(itemWithoutSecondComponent, false);
        try {
            theItemXml = create(xmlData);
            fail("No exception occurred on create item with component without "
                + "the attribute 'storage' in element 'content'");
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "InvalidContentException", InvalidContentException.class, e);
        }
    }

    /**
     * Test successfully updating an item with a component containing a binary
     * content inline, and the attribute 'storage' set to 'external-url'. The
     * attribute storage was set to 'internal-managed' while create.
     * 
     * @throws Exception
     */
    public void testUpdateItemWithExternalUrlStorageAndInlineBinary(
        String storage) throws Exception {
        updateItemWithExternalStorageAndInlineBinary("external-url");
    }

    /**
     * Test successfully updating an item with a component containing a binary
     * content inline, and the attribute 'storage' set to 'external-managed'.
     * The attribute storage was set to 'internal-managed' while create.
     * 
     * @throws Exception
     */
    public void testUpdateItemWithExternalManagedStorageAndInlineBinary(
        String storage) throws Exception {
        updateItemWithExternalStorageAndInlineBinary("external-managed");
    }

    public void updateItemWithExternalStorageAndInlineBinary(
        final String storage) throws Exception {
        Document item =
            EscidocRestSoapTestsBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");

        Node itemWithoutSecondComponent =
            deleteElement(item, "/item/components/component[2]");
        String xmlData = toString(itemWithoutSecondComponent, false);
        // System.out.println("item " + xmlData);
        theItemXml = create(xmlData);
        theItemId =
            getObjidValue(EscidocRestSoapTestsBase.getDocument(theItemXml));
        assertXmlValidItem(xmlData);
        Document createdItem = getDocument(theItemXml);
        if (getTransport(true).equals("REST")) {
            String componentHrefValue =
                selectSingleNode(createdItem,
                    "/item/components/component/@href").getNodeValue();
            componentId = getObjidFromHref(componentHrefValue);
        }
        else {
            componentId =
                selectSingleNode(createdItem,
                    "/item/components/component/@objid").getNodeValue();
        }
        Node withUpdatedStorage =
            substitute(createdItem,
                "/item/components/component/content/@storage", storage);
        String toUpdate = toString(withUpdatedStorage, true);
        update(theItemId, toUpdate);
    }

    /**
     * Test successfully updating an item with a component containing a binary
     * content inline, and missing attribute 'storage'. The attribute storage
     * was set to 'internal-managed' while create.
     * 
     * @throws Exception
     */
    public void testUpdateItemWithoutStorageAttributeOnUpdateAndInlineBinary()
        throws Exception {
        Document item =
            EscidocRestSoapTestsBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");

        Node itemWithoutSecondComponent =
            deleteElement(item, "/item/components/component[2]");
        String xmlData = toString(itemWithoutSecondComponent, false);
        // System.out.println("item " + xmlData);
        theItemXml = create(xmlData);
        theItemId =
            getObjidValue(EscidocRestSoapTestsBase.getDocument(theItemXml));
        assertXmlValidItem(xmlData);
        Document createdItem = getDocument(theItemXml);
        if (getTransport(true).equals("REST")) {
            String componentHrefValue =
                selectSingleNode(createdItem,
                    "/item/components/component/@href").getNodeValue();
            componentId = getObjidFromHref(componentHrefValue);
        }
        else {
            componentId =
                selectSingleNode(createdItem,
                    "/item/components/component/@objid").getNodeValue();
        }
        Node withoutStorage =
            deleteAttribute(createdItem,
                "/item/components/component/content/@storage");
        String toUpdate = toString(withoutStorage, true);
        update(theItemId, toUpdate);
    }

    /**
     * Test declining updating an item with a component containing a binary
     * content inline, and the attribute 'storage' set to 'internal-managed'.
     * The attribute storage was set to 'external-url' while create.
     * 
     * @throws Exception
     */
    public void testUpdateItemWithStorageExternalUrlAndInlineBinary(
        String storage) throws Exception {
        updateItemWithInternalManagedAndInlineBinary("external-url");
    }

    /**
     * Test declining updating an item with a component containing a binary
     * content inline, and the attribute 'storage' set to 'internal-managed'.
     * The attribute storage was set to 'external-managed' while create.
     * 
     * @throws Exception
     */
    public void testUpdateItemWithStorageExternalManagedAndInlineBinary(
        String storage) throws Exception {
        updateItemWithInternalManagedAndInlineBinary("external-managed");
    }

    public void updateItemWithInternalManagedAndInlineBinary(String storage)
        throws Exception {
        Document item =
            EscidocRestSoapTestsBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");

        Node itemWithoutSecondComponent =
            deleteElement(item, "/item/components/component[1]");
        Node itemWithContentExternalUrl =
            substitute(itemWithoutSecondComponent,
                "/item/components/component/content/@storage", storage);
        String xmlData = toString(itemWithContentExternalUrl, false);
        // System.out.println("item " + xmlData);
        theItemXml = create(xmlData);
        theItemId =
            getObjidValue(EscidocRestSoapTestsBase.getDocument(theItemXml));
        assertXmlValidItem(xmlData);
        Document createdItem = getDocument(theItemXml);

        Node withUpdatedStorage =
            substitute(createdItem,
                "/item/components/component/content/@storage",
                "internal-managed");
        Node withUpdatedStorageAndBinaryInline =
            substitute(withUpdatedStorage,
                "/item/components/component/content",
                "ksd asda�f ad�fa  da sa");

        String toUpdate = toString(withUpdatedStorageAndBinaryInline, true);
        // System.out.println("to update " + toUpdate);
        try {
            update(theItemId, toUpdate);
            fail("No exception occured on update item with component which "
                + "content attribute 'storage' was set to 'external-url' on "
                + "create and inline binary content while update.");
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "InvalidContentException", InvalidContentException.class, e);
        }

    }

    /**
     * Test declining Adding of a new Component with content inline and missing
     * attribute 'storage'.
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testAddingComponentWithoutAttributeStorage() throws Exception {
        Document item =
            EscidocRestSoapTestsBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");

        Node itemWithoutSecondComponent =
            deleteElement(item, "/item/components/component[2]");
        String xmlData = toString(itemWithoutSecondComponent, false);
        // System.out.println("item " + xmlData);
        theItemXml = create(xmlData);
        theItemId =
            getObjidValue(EscidocRestSoapTestsBase.getDocument(theItemXml));
        assertXmlValidItem(xmlData);
        // Document curItem = EscidocRestSoapTestsBase.getDocument(theItemXml);

        // get new component from template
        String templateComponentXml =
            EscidocRestSoapTestsBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false),
                "escidoc_item_198_for_create_2_Component_Md-Records.xml");
        Node itemWithFirstComponentWithoutAttributeStorage =
            deleteAttribute(EscidocRestSoapTestsBase
                .getDocument(templateComponentXml),
                "/item/components/component[1]/content/@storage");
        Node newComponent =
            selectSingleNode(itemWithFirstComponentWithoutAttributeStorage,
                "/item/components/component[1]");

        // add new component to item
        // string op start
        String theItemXmlWith1AddComp =
            insertNamespacesInRootElement(theItemXml);
        theItemXmlWith1AddComp =
            theItemXmlWith1AddComp.replaceFirst(
                "</escidocComponents:components>", toString(newComponent, true)
                    + "</escidocComponents:components>");

        String newItemXml = theItemXmlWith1AddComp; // toString(curItem, false);
        try {
            update(theItemId, newItemXml);
            fail("No exception occurred on update item with a new component "
                + "without content attribute 'storage'");
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "InvalidContentException", InvalidContentException.class, e);
        }

    }

}
