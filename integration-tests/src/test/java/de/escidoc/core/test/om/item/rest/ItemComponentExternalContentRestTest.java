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

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.escidoc.core.common.exceptions.remote.system.WebserverSystemException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.om.interfaces.ItemXpathsProvider;
import de.escidoc.core.test.om.item.ItemComponentExternalContentTest;

/**
 * Item tests with REST transport.
 * 
 * @author MSC
 * 
 */
public class ItemComponentExternalContentRestTest
    extends ItemComponentExternalContentTest implements ItemXpathsProvider {

    /**
     * Constructor.
     * 
     */
    public ItemComponentExternalContentRestTest() {
        super(Constants.TRANSPORT_REST);
    }

    /**
     * Test successfully creating an item with a component containing a binary
     * content,referenced by an URL, and the attribute 'storage' set to
     * 'external-url'. The retrieve of the content is successfull.
     * 
     * @throws Exception
     */
    // FIXME test for redirect
    public void NOtestCreateItemWithExternalBinaryContentAndExternalExternalUrl()
        throws Exception {
        super.testCreateItemWithExternalBinaryContentAndExternalExternalUrl();

        retrieveContent(theItemId, componentId);

    }

    /**
     * Test successfully creating an item with a component containing a binary
     * content,referenced by an URL, and the attribute 'storage' set to
     * 'external-managed'. The retrieve of the content is successfull.
     * 
     * @throws Exception
     */
    public void testCreateItemWithExternalBinaryContentAndExternalManaged()
        throws Exception {
        super.testCreateItemWithExternalBinaryContentAndExternalManaged();
        retrieveContent(theItemId, componentId);
    }

    /**
     * Test declining retrieve a component content of a compoenent containing
     * the attribute 'storage' set to 'external-managed' and a wrong url.
     * 
     * @throws Exception
     */
    public void testRetrieveItemWithStorageExternalUrlAndWrongUrl()
        throws Exception {
        Document item =
            EscidocRestSoapTestsBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        String storageBeforeCreate = "external-managed";
        Document newItem =
            (Document) substitute(item,
                "/item/components/component[2]/content/@storage",
                storageBeforeCreate);
        Document newItem2 =
            (Document) substitute(newItem,
                "/item/components/component[2]/content/@href",
                "http://www.bla.de");
        Node itemWithoutSecondComponent =
            deleteElement(newItem2, "/item/components/component[1]");
        String xmlData = toString(itemWithoutSecondComponent, false);

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

        try {
            retrieveContent(theItemId, componentId);
            // FIXME redirect expected
            // fail("No exception occurred on retrieve content of a component with "
            // +
            // "the attribute 'storage' set to 'external-managed and a wrong url");
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "WebserverSystemException", WebserverSystemException.class, e);
        }

    }
}
