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

import de.escidoc.core.common.exceptions.remote.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Steffen Wagner
 */
@Ignore
// DigiLib Test sollen laut Matthias für das 1.3 Realease deaktiviert werden.
public class ItemContentTransformationIT extends ItemTestBase {

    private String itemId = null;

    private String itemXml = null;

    private static Document createdItem = null;

    private String componentId = null;

    private int componentNo = 2;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        if (itemId == null) {
            itemXml =
                EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                    "escidoc_item_for_image_transformation.xml");

            createdItem = EscidocAbstractTest.getDocument(create(itemXml));
            itemId = getObjidValue(createdItem);

            submit(itemId, getTheLastModificationParam(false, itemId));
            String versionId = itemId + ":1";
            assignVersionPid(versionId, getPidParam(versionId, getFrameworkUrl() + "/ir/item/" + versionId));

            release(itemId, getTheLastModificationParam(false, itemId));

            componentNo = 1;
            componentId = getObjidValue(createdItem, "/item/components/component[1]");
            Node node = selectSingleNode(createdItem, "/item/components/component[1]/properties/description");
            if (node == null) {
                componentNo = 2;
            }
        }
    }

    /**
     * Test successfully retrieving the component of an item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testItComRetr() throws Exception {

        componentId = getObjidValue(createdItem, "/item/components/component[properties]");

        retrieveComponentProperties(itemId, componentId);
        String templateProperties =
            toString(selectSingleNode(EscidocAbstractTest.getDocument(itemXml),
                "/item/components/component[properties/description]/properties"), true);

        componentId = getObjidValue(createdItem, "/item/components/component[not(properties/description)]");

        retrieveComponentProperties(itemId, componentId);
    }

    /**
     * Test retrieving the content with transformation of an unknown item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRCP2a() throws Exception {
        Class<?> ec = ItemNotFoundException.class;
        try {
            retrieveBinaryContent("unknown", componentId, "digilib",
                "?ws=1.0&wy=0.8&wh=1.8&ww=0.3&wx=0.1&dw=600&dh=300");
            fail(ec + " expected but no error occurred!");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

    }

    /**
     * Test retrieving the content of an item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRCP2b() throws Exception {
        Class<?> ec = ComponentNotFoundException.class;
        try {
            retrieveBinaryContent(itemId, "unknown", "digilib", "?ws=1.0&wy=0.8&wh=1.8&ww=0.3&wx=0.1&dw=600&dh=300");
            fail(ec + " expected but no error occurred!");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

    }

    /**
     * Test retrieving the content of an item (multiple times).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRCt1() throws Exception {
        String xpath = "/item/components/component[2]/";
        String compId = null;
        xpath += "@href";
        String debug = toString(selectSingleNode(createdItem, "/item/components"), false);
        Node component = selectSingleNode(createdItem, xpath);
        assertNotNull("Missing Component in Item", component);
        String href = component.getNodeValue();
        compId = getObjidFromHref(href);
        // FIXME replace this with a method to retrieve binary content and
        // compare the delivered afterwards.
        retrieveBinaryContent(itemId, compId, "digilib", "?ws=1.0&wy=0.8&wh=1.8&ww=0.3&wx=0.1&dw=600&dh=300");
    }

    /**
     * Test retrieving the content of an item with NON-ASCII parameter.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRCt2() throws Exception {

        String content =
            retrieveContent("escidoc:ex5", "escidoc:ex6", "digilib",
                "?ws=1.0&wy=0.8&wh=1.8&ww=0.3&wx=0.1&dw=600&dh=300&xyz=\t\n");
    }

    /**
     * Test retrieving the content of item multiple times (stress)
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRCt3() throws Exception {

        for (int i = 0; i < 10; i++) {
            retrieveContent("escidoc:ex5", "escidoc:ex6", "digilib",
                "?ws=1.0&wy=0.8&wh=1.8&ww=0.3&wx=0.1&dw=600&dh=300");
        }
    }

}
