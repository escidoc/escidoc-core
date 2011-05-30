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

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.fedora.TripleStoreTestBase;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class ItemDeleteIT extends ItemTestBase {

    private String theItemId;

    /**
     * Get the param with last-modification-date.
     *
     * @param includeWithdrawComment Set true if a withdraw comment is to include.
     * @return param with last-modification-date
     * @throws Exception Thrown if converting of XML to Document format fails.
     */
    private String getTheLastModificationParam(final boolean includeWithdrawComment) throws Exception {
        Document item = EscidocAbstractTest.getDocument(retrieve(theItemId));

        // get last-modification-date
        NamedNodeMap atts = item.getDocumentElement().getAttributes();
        Node lastModificationDateNode = atts.getNamedItem("last-modification-date");
        String lastModificationDate = lastModificationDateNode.getNodeValue();

        String param = "<param last-modification-date=\"" + lastModificationDate + "\" ";
        if (includeWithdrawComment) {
            param += "withdraw-comment=\"this is a withdraw comment\"";
        }
        param += "/>";

        return param;
    }

    /**
     * Test successfully deleting item in status "pending".
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMDi1a() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String itemXml = create(xml);
        this.theItemId = getObjidValue(itemXml);
        delete(this.theItemId);
        try {

            retrieve(this.theItemId);
            fail("No exception on retrieve item after delete.");
        }
        catch (final Exception e) {
            Class<?> ec = ItemNotFoundException.class;
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }

    /**
     * Test successfully delete item with components, which are not all in the last item version.
     */
    @Test
    public void testDeleteItemWithAllComponentsFromAllItemVersions() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String itemXml = create(xml);
        this.theItemId = getObjidValue(itemXml);
        Document curItem = EscidocAbstractTest.getDocument(itemXml);
        NodeList componentIdsAfterCreate = null;
        componentIdsAfterCreate = selectNodeList(curItem, "/item/components/component/@href");
        Vector<String> componentIds = new Vector<String>();
        for (int i = 0; i < componentIdsAfterCreate.getLength(); i++) {
            String id = componentIdsAfterCreate.item(i).getNodeValue();
            id = getIdFromHrefValue(id);
            componentIds.add(id);
        }
        final String itemUpdatedXml =
            update(theItemId, toString(deleteElement(curItem, "/item/components/component[1]"), false));
        Document itemAfterUpdate = EscidocAbstractTest.getDocument(itemUpdatedXml);
        NodeList componentIdsAfterUpdate = selectNodeList(itemAfterUpdate, "/item/components/component");
        assertEquals("number of components is wrong ", componentIdsAfterCreate.getLength() - 1, componentIdsAfterUpdate
            .getLength());
        assertNotNull(itemUpdatedXml);
        TripleStoreTestBase tripleStore = new TripleStoreTestBase();
        delete(theItemId);
        for (int i = 0; i < componentIds.size(); i++) {
            String result =
                tripleStore.requestMPT("<info:fedora/" + componentIds.get(i) + "> "
                    + "<http://purl.org/dc/elements/1.1/identifier>" + " *", "RDF/XML");
            NodeList components = selectNodeList(EscidocAbstractTest.getDocument(result), "/RDF/Description/*");
            assertEquals("result is not empty ", 0, components.getLength());

        }
    }

    /**
     * Test declining deleting item in status "released".
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMDi2a() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String itemXml = create(xml);
        this.theItemId = getObjidValue(itemXml);
        String param = getTheLastModificationParam(false);

        submit(this.theItemId, param);

        String pidParam;
        if (getItemClient().getPidConfig("cmm.Item.objectPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {
            pidParam = getPidParam(this.theItemId, "http://somewhere" + this.theItemId);
            assignObjectPid(this.theItemId, pidParam);
        }
        if (getItemClient().getPidConfig("cmm.Item.versionPid.setPidBeforeRelease", "true")
            && !getItemClient().getPidConfig("cmm.Item.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(itemXml);
            pidParam = getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(true);
        release(this.theItemId, param);
        Class<?> ec = InvalidStatusException.class;
        try {
            delete(this.theItemId);
            EscidocAbstractTest.failMissingException("Deleting an item in status 'released' was not declined. ", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Deleting an item in status 'released' raised wrong exception. ",
                ec, e);
        }

    }

    /**
     * Test declining deleting item in status "submitted".
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMDi2b() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String itemXml = create(xml);
        this.theItemId = getObjidValue(itemXml);
        String param = getTheLastModificationParam(false);
        submit(this.theItemId, param);
        Class<?> ec = InvalidStatusException.class;
        try {
            delete(this.theItemId);
            EscidocAbstractTest.failMissingException("Deleting an item in status 'released' was not declined. ", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Deleting an item in status 'released' raised "
                + "wrong exception. ", ec, e);
        }
    }

    /**
     * Test declining deleting item with not existing id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMDi3() throws Exception {

        try {
            retrieve("test");
        }
        catch (final ItemNotFoundException e) {
            return;
        }
        fail("Not expected exception");

    }

    /**
     * Test declining deleting item (input parameter item id is missing).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMDi4() throws Exception {

        try {
            delete(null);
        }
        catch (final MissingMethodParameterException e) {
            return;
        }
        fail("Not expected exception");
    }

    /**
     * Test declining deleting item with wrong id (id refers to another object type).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMDi5() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");

        String itemXml = create(xml);
        this.theItemId = getObjidValue(itemXml);
        Node component = selectSingleNode(EscidocAbstractTest.getDocument(itemXml), "/item/components/component");
        String componentId = getObjidValue(toString(component, true));

        try {
            delete(componentId);
        }
        catch (final ItemNotFoundException e) {
            return;
        }
        fail("Not expected exception");

    }
}
