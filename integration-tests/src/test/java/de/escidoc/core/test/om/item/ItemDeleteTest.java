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
import de.escidoc.core.test.EscidocRestSoapTestBase;
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
public class ItemDeleteTest extends ItemTestBase {

    private String theItemId;

    /**
     * Get the param with last-modification-date.
     *
     * @param includeWithdrawComment Set true if a withdraw comment is to include.
     * @return param with last-modification-date
     * @throws Exception Thrown if converting of XML to Document format fails.
     */
    private String getTheLastModificationParam(final boolean includeWithdrawComment) throws Exception {
        Document item = EscidocRestSoapTestBase.getDocument(retrieve(theItemId));

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
            EscidocRestSoapTestBase
                .getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String itemXml = create(xml);
        this.theItemId = getObjidValue(itemXml);

        // Node itemObjiId = selectSingleNode(getDocument(itemXml),
        // "/item/@objid");
        //
        // String itemId = itemObjiId.getTextContent();
        // this.theItemId = itemId;

        delete(this.theItemId);
        try {

            retrieve(this.theItemId);
            fail("No exception on retrieve item after delete.");
        }
        catch (final Exception e) {
            Class<?> ec = ItemNotFoundException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test successfully delete item with components, which are not all in the last item version.
     */
    @Test
    public void testDeleteItemWithAllComponentsFromAllItemVersions() throws Exception {
        String xml =
            EscidocRestSoapTestBase
                .getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String itemXml = create(xml);
        this.theItemId = getObjidValue(itemXml);
        Document curItem = EscidocRestSoapTestBase.getDocument(itemXml);
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
        Document itemAfterUpdate = EscidocRestSoapTestBase.getDocument(itemUpdatedXml);
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
            NodeList components = selectNodeList(EscidocRestSoapTestBase.getDocument(result), "/RDF/Description/*");
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
            EscidocRestSoapTestBase
                .getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
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
            EscidocRestSoapTestBase
                .failMissingException("Deleting an item in status 'released' was not declined. ", ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Deleting an item in status 'released' raised wrong exception. ", ec, e);
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
            EscidocRestSoapTestBase
                .getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String itemXml = create(xml);
        this.theItemId = getObjidValue(itemXml);

        // Node itemObjiId = selectSingleNode(getDocument(itemXml),
        // "/item/@objid");
        // String itemId = itemObjiId.getTextContent();
        // this.theItemId = itemId;
        String param = getTheLastModificationParam(false);

        submit(this.theItemId, param);
        Class<?> ec = InvalidStatusException.class;
        try {
            delete(this.theItemId);
            EscidocRestSoapTestBase
                .failMissingException("Deleting an item in status 'released' was not declined. ", ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Deleting an item in status 'released' raised "
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
            EscidocRestSoapTestBase
                .getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");

        String itemXml = create(xml);
        this.theItemId = getObjidValue(itemXml);

        // Node itemObjiId = selectSingleNode(getDocument(itemXml),
        // "/item/@objid");
        // String itemId = itemObjiId.getTextContent();
        // FIXME
        // Node componentObjiId = selectSingleNode(getDocument(itemXml),
        // "/item/components/component/@objid");
        // String componentId = componentObjiId.getTextContent();

        Node component = selectSingleNode(EscidocRestSoapTestBase.getDocument(itemXml), "/item/components/component");
        String componentId = getObjidValue(toString(component, true));

        try {
            delete(componentId);
        }
        catch (final ItemNotFoundException e) {
            return;
        }
        fail("Not expected exception");

    }
    // Test methods are obsolete, because they test the obsolete
    // interface methods
    // /**
    // * Test successfully deleting items, which are referenced as relations
    // * targets in the other item. After deletion of referenced items, the item
    // * has no relations more.
    // *
    // *
    // * @test.status Implemented
    // *
    // * @throws Exception
    // * If anything fails.
    // */
    // @Test
    // public void testDeleteWithRelations() throws Exception {
    // String itemXml1 = create(getTemplateAsString(TEMPLATE_ITEM_PATH,
    // "escidoc_item_198_for_create" + getTransport(true) + ".xml"));
    // String itemXml2 = create(getTemplateAsString(TEMPLATE_ITEM_PATH,
    // "escidoc_item_198_for_create" + getTransport(true) + ".xml"));
    //
    // String createdItemId1 = null;
    // String createdItemId2 = null;
    // Pattern PATTERN_OBJID_ATTRIBUTE = Pattern.compile("objid=\"([^\"]*)\"");
    // Matcher m1 = PATTERN_OBJID_ATTRIBUTE.matcher(itemXml1);
    // if (m1.find()) {
    // createdItemId1 = m1.group(1);
    // }
    // Matcher m2 = PATTERN_OBJID_ATTRIBUTE.matcher(itemXml2);
    // if (m2.find()) {
    // createdItemId2 = m2.group(1);
    // }
    //
    // String href1 = "/ir/item/" + createdItemId1;
    // String href2 = "/ir/item/" + createdItemId2;
    // String itemForCreateWithRelationsXml = getTemplateAsString(
    // TEMPLATE_ITEM_PATH, "escidoc_item_198_for_createWithRelations" +
    // getTransport(true) + ".xml");
    //
    // itemForCreateWithRelationsXml = itemForCreateWithRelationsXml
    // .replaceAll("##ITEM_ID1##", createdItemId1);
    // itemForCreateWithRelationsXml = itemForCreateWithRelationsXml
    // .replaceAll("##ITEM_ID2##", createdItemId2);
    // itemForCreateWithRelationsXml = itemForCreateWithRelationsXml
    // .replaceAll("##ITEM_HREF1##", href1);
    // itemForCreateWithRelationsXml = itemForCreateWithRelationsXml
    // .replaceAll("##ITEM_HREF2##", href2);
    // Document itemForCreateWithRelations =
    // getDocument(itemForCreateWithRelationsXml);
    // Node xmlItemWithoutComponents = deleteElement(
    // itemForCreateWithRelations, "/item/components");
    // String itemWithoutComponents = toString(xmlItemWithoutComponents, true);
    //       
    // String xml = create(itemWithoutComponents);
    // NodeList relations = selectNodeList(getDocument(itemWithoutComponents),
    // "/item/relations/relation");
    //
    //        
    // assertEquals("item relations number is wrong", relations.getLength(), 2);
    //
    // Node itemObjiId = selectSingleNode(getDocument(xml), "/item/@objid");
    //
    // String itemId = itemObjiId.getTextContent();
    //
    // delete(createdItemId1);
    // delete(createdItemId2);
    // String itemAfterDeleteOfrelations = retrieve(itemId);
    //        
    // NodeList relationsOfitemAfterDeleteOfRelations = selectNodeList(
    // getDocument(itemAfterDeleteOfrelations), "/item/relations/relation");
    // assertEquals("item relations number is wrong",
    // relationsOfitemAfterDeleteOfRelations.getLength(), 0);
    //
    // }
}
