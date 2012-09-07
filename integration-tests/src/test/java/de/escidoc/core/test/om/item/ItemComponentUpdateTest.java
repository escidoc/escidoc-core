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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.resources.PropertiesProvider;

/**
 * Test the update of the component resource.
 * 
 * @author Michael Hoppe
 */
@RunWith(value = Parameterized.class)
public class ItemComponentUpdateTest extends ItemTestBase {

    private final String contentHref =
        PropertiesProvider.getInstance().getProperty(PropertiesProvider.TESTDATA_URL) + "/testDocuments/downloadMe.txt";

    private final String contentHref1 =
        PropertiesProvider.getInstance().getProperty(PropertiesProvider.TESTDATA_URL) + "/testDocuments/UploadTest.zip";

    private final String itemContentXPath = "/item/components/component/content/@href";

    private final String componentContentXPath = "/component/content/@href";

    private final String itemContentStorageXPath = "/item/components/component/content/@storage";

    private final String componentMdTitleXPath = "/component/md-records/md-record[@name='escidoc']/publication/title";

    private final String itemMdTitleXPath =
        "/item/components/component/md-records/md-record[@name='escidoc']/publication/title";

    private final String storage = "external-url";

    private final String newTitle = "new title";

    /**
     * @param transport
     *            The transport identifier.
     */
    public ItemComponentUpdateTest(final int transport) {
        super(transport);
    }

    /**
     * Test successfully updating the md-record of an existing item-component.
     */
    @Test
    public void testUpdateComponentMdRecord() throws Exception {

        Document xmlData =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                "escidoc_item_1_component_1.xml");
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
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                "escidoc_item_1_component_1.xml");
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
     * Test successfully updating the md-record of an existing item-component with updateItem.
     */
    @Test
    public void testUpdateItemComponentMdRecord() throws Exception {

        Document xmlData =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                "escidoc_item_1_component_1.xml");
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
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                "escidoc_item_1_component_1.xml");
        xmlData = (Document) substitute(xmlData, itemContentXPath, contentHref);
        xmlData = (Document) substitute(xmlData, itemContentStorageXPath, storage);
        String itemXml = create(toString(xmlData, false));
        Document itemDoc = getDocument(itemXml);
        String itemId = getObjidValue(itemDoc);

        Document newItem = (Document) substitute(itemDoc, itemContentXPath, contentHref1);
        itemXml = update(itemId, toString(newItem, false));
        assertXmlEquals("content-href not as expected", itemXml, itemContentXPath, contentHref1);
    }

}
