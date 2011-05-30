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
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class MimeTypeIT extends ItemTestBase {

    private String theItemId = null;

    /**
     * Successfully create components with MS Windows video and audio mime-types. Issue 622.
     */
    @Test
    public void testWithMSWinMimeTypes() throws Exception {
        Document xmlItem =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        xmlItem =
            (Document) substitute(xmlItem, "/item/components/component[1]/properties/mime-type", "video/x-ms-wmv");
        xmlItem =
            (Document) substitute(xmlItem, "/item/components/component[2]/properties/mime-type", "audio/x-ms-wma");
        String item = toString(xmlItem, true);

        item = create(item);
        assertXmlValidItem(item);
        xmlItem = getDocument(item);
        selectSingleNodeAsserted(xmlItem, "/item/components/component/properties[mime-type = 'video/x-ms-wmv']");
        selectSingleNodeAsserted(xmlItem, "/item/components/component/properties[mime-type = 'audio/x-ms-wma']");
    }

    /**
     * Create a component with content of mime-type text/plain. Related to issue 603.
     */
    @Test
    public void testPlainTextComponent() throws Exception {
        Document xmlItem =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "item-plain-text.xml");
        String item = toString(xmlItem, true);

        item = create(item);
        assertXmlValidItem(item);

        String id = getIdFromRootElement(item);
        if (id != null) {
            delete(id);
        }
    }

    /**
     *
     *
     * @throws Exception
     */
    @Test
    public void testDeclineUnknownMimeTypeCreate() throws Exception {
        Document xmlItem =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        xmlItem = (Document) substitute(xmlItem, "/item/components/component[1]/properties/mime-type", "unkn/own");
        String item = toString(xmlItem, true);

        item = create(item);
    }

    /**
     *
     *
     * @throws Exception
     */
    @Test
    public void testDeclineUnknownMimeTypeUpdate() throws Exception {
        Document xmlItem =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String item = toString(xmlItem, true);

        item = create(item);

        item =
            toString((Document) substitute(getDocument(item), "/item/components/component[1]/properties/mime-type",
                "unkn/own"), true);
        String id = getObjidValue(item);

        item = update(id, item);
    }

    /**
     * Test NOT declining update of an item with a component where property mime-type is set to a value not from the
     * infrastructures list of allowed mime-types. List of allowed mime-types does not longer exist.
     */
    @Test
    public void testUpdateComponentPropertyMimeType() throws Exception {
        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String theItemXml = create(xmlData);
        theItemId = getObjidValue(EscidocAbstractTest.getDocument(theItemXml));
        Document newItem = EscidocAbstractTest.getDocument(theItemXml);

        String testStringValue = "testing";
        String basePath = "/item/components/component/properties/";
        newItem = (Document) substitute(newItem, basePath + "mime-type", testStringValue);

        String newItemXml = toString(newItem, true);

        update(theItemId, newItemXml);

    }

}
