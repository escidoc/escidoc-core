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
import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Test of a surrogate item resource.
 *
 * @author Rozita Friedman
 */
public class SurrogateItemIT extends ItemTestBase {

    /**
     * Creates a surrogate item owned a mandatory md-record, which references an original item, containing a mandatory
     * and an optional md-record and two components. Tests if the surrogate item representation contains its own
     * mandatory md-record, the optional md-record of the original item and two components of the original item.
     */
    @Test
    public void testCreateSurrogateItemWithOwnAndInheritedMdRecordsLatestRelease() throws Exception {
        Document item =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        Element mdRecord =
            item.createElementNS("http://www.escidoc.de/schemas/metadatarecords/0.5",
                "escidocMetadataRecords:md-record");
        mdRecord.setAttribute("name", "name1");
        mdRecord.setAttribute("schema", "bla");
        Element mdRecordContent = item.createElement("bla");
        mdRecord.appendChild(mdRecordContent);
        selectSingleNode(item, "/item/md-records").appendChild(mdRecord);
        String itemWith2MdRecordXml = toString(item, true);

        String createdItem = create(itemWith2MdRecordXml);
        Document createdItemDocument = getDocument(createdItem);
        String componentId1 = null;
        String componentId2 = null;

        String componentHref1 = null;
        String componentHref2 = null;
        componentHref1 = selectSingleNode(createdItemDocument, "/item/components/component[1]/@href").getNodeValue();
        componentHref2 = selectSingleNode(createdItemDocument, "/item/components/component[2]/@href").getNodeValue();

        String itemId = getObjidValue(createdItem);
        String itemHref = "/ir/item/" + itemId;
        String param = getTheLastModificationParam(false, itemId, null);
        submit(itemId, param);

        // add object and version pid to release
        String pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignVersionPid(itemId, pidParam);
        pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignObjectPid(itemId, pidParam);

        param = getTheLastModificationParam(false, itemId, null);
        release(itemId, param);
        String surrogateItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");

        String replaced = surrogateItemXml.replaceAll("##ITEMHREF##", itemHref);

        String createdSurrogateItem = create(replaced);

        Document surrogateDocument = getDocument(createdSurrogateItem);
        componentHref1 = selectSingleNode(surrogateDocument, "/item/components/component[1]/@href").getNodeValue();
        componentHref2 = selectSingleNode(surrogateDocument, "/item/components/component[2]/@href").getNodeValue();

        String originId = null;
        Node origin = selectSingleNode(surrogateDocument, "/item/properties/origin/@href");
        assertNotNull("attribute 'href' of the element " + "'origin' may not be null", origin);
        originId = origin.getNodeValue();
        assertEquals(originId, itemHref);

        Node inherited = selectSingleNode(surrogateDocument, "/item/md-records/md-record[@name='escidoc']/@inherited");
        assertNull("attribute 'inherited' of the element " + "'md-record' with name 'escidoc' must be null", inherited);
        Node optionalMdRecord = selectSingleNode(surrogateDocument, "/item/md-records/md-record[@name='name1']");
        assertNotNull("item must inherite a md-record with " + "a name 'name1'", optionalMdRecord);

        Node inherited2 = selectSingleNode(surrogateDocument, "/item/md-records/md-record[@name='name1']/@inherited");
        assertNotNull("item must inherite a md-record with " + "a name 'name1'", inherited2);

        // check if the both components are inherited
        Node origin1 =
            selectSingleNode(surrogateDocument, "/item/components/component[@href='" + componentHref1 + "']/@inherited");
        assertNotNull("attribute 'inherited' of the element " + "'component' may not be null", origin1);
        assertNotNull("attribute 'inherited' of the element " + "'component' may not be null", origin1);
        Node origin2 =
            selectSingleNode(surrogateDocument, "/item/components/component[@href='" + componentHref2 + "']/@inherited");
        assertNotNull("attribute 'inherited' of the element " + "'component' may not be null", origin2);
        NodeList componentList = selectNodeList(surrogateDocument, "/item/components/component");
        assertEquals(2, componentList.getLength());
        assertXmlValidItem(createdSurrogateItem);
    }

    /**
     * Creates a surrogate item, which has not any md-records and references an original item containing a mandatory
     * md-record. Tests if a surrogate item representation contains the mandatory md-record of the original item and
     * xlink:title of the original item in the REST-CASE.
     */
    @Test
    public void testCreateSurrogateItemWithInheritedMandatoryMdRecord() throws Exception {

        String itemXml =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));
        String createdItem = create(itemXml);
        String originalXlinkTitle = null;
        Document createdItemDocument = getDocument(itemXml);
        originalXlinkTitle = selectSingleNode(createdItemDocument, "/item/@title").getNodeValue();
        String itemId = getObjidValue(createdItem);
        String itemHref = "/ir/item/" + itemId;
        String param = getTheLastModificationParam(false, itemId, null);
        submit(itemId, param);

        // add object and version pid to release
        String pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignVersionPid(itemId, pidParam);
        pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignObjectPid(itemId, pidParam);

        param = getTheLastModificationParam(false, itemId, null);
        release(itemId, param);
        String surrogateItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");
        String replaced = surrogateItemXml.replaceAll("##ITEMHREF##", itemHref);

        Document surrogateDocument = getDocument(replaced);
        Node surrogateWithoutMdRecords = deleteElement(surrogateDocument, "/item/md-records");
        String surrogate = toString(surrogateWithoutMdRecords, true);
        String createdSurrogateItem = create(surrogate);
        Document createdSurrogateDocument = getDocument(createdSurrogateItem);
        String originId = null;
        Node origin = selectSingleNode(createdSurrogateDocument, "/item/properties/origin/@href");
        assertNotNull("attribute 'href' of the element " + "'origin' may not be null", origin);
        originId = origin.getNodeValue();
        assertEquals(originId, itemHref);

        Node inherited =
            selectSingleNode(createdSurrogateDocument, "/item/md-records/md-record[@name='escidoc']/@inherited");
        assertNotNull("attribute 'inherited' of the element " + "'md-record' with name 'escidoc' must be null",
            inherited);
        assertXmlValidItem(createdSurrogateItem);

        // test, if xlink:title of a surrogate comes from the original item
        String surrogateXlinkTitle = selectSingleNode(createdSurrogateDocument, "/item/@title").getNodeValue();
        assertEquals("xlink:title of the surogate is not equal xlink:title" + " of the original ", surrogateXlinkTitle,
            originalXlinkTitle);

    }

    /**
     * Relations are not inherited, the test is not correct any more.
     */
    @Ignore("Relations are not inherited from surrogate, the test is not correct any more.")
    @Test
    public void testCreateSurrogateItemWithOwnAndInheritedContentRelations() throws Exception {
        String itemXml1 =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));

        String itemXml2 =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));

        String createdItemId1 = getIdFromRootElement(itemXml1);
        String createdItemId2 = getIdFromRootElement(itemXml2);

        String href1 = "/ir/item/" + createdItemId1;
        String href2 = "/ir/item/" + createdItemId2;

        String itemForCreateWithRelationsXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_createWithRelations.xml");

        itemForCreateWithRelationsXml = itemForCreateWithRelationsXml.replaceAll("##ITEM_ID1##", createdItemId1);
        itemForCreateWithRelationsXml = itemForCreateWithRelationsXml.replaceAll("##ITEM_ID2##", createdItemId2);

        itemForCreateWithRelationsXml = itemForCreateWithRelationsXml.replaceAll("##ITEM_HREF1##", href1);
        itemForCreateWithRelationsXml = itemForCreateWithRelationsXml.replaceAll("##ITEM_HREF2##", href2);

        Document itemForCreateWithRelations = EscidocAbstractTest.getDocument(itemForCreateWithRelationsXml);
        Node xmlItemWithoutComponents = deleteElement(itemForCreateWithRelations, "/item/components");
        String itemWithoutComponents = toString(xmlItemWithoutComponents, true);
        NodeList relations =
            selectNodeList(EscidocAbstractTest.getDocument(itemWithoutComponents), "/item/relations/relation");

        // create item with relations, no components
        String xml = create(itemWithoutComponents);
        NodeList relationsAfterCreate =
            selectNodeList(EscidocAbstractTest.getDocument(xml), "/item/relations/relation");

        assertEquals("Number of relations is wrong ", relations.getLength(), relationsAfterCreate.getLength());

        String itemId = getObjidValue(xml);
        String itemHref = "/ir/item/" + itemId;

        String param = getTheLastModificationParam(false, itemId, null);
        submit(itemId, param);

        // add object and version pid to release
        String pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignVersionPid(itemId, pidParam);
        pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignObjectPid(itemId, pidParam);

        param = getTheLastModificationParam(false, itemId, null);
        release(itemId, param);

        String surrogateItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");
        String replaced = surrogateItemXml.replaceAll("##ITEMID##", itemHref);

        // create surrogate for release item with relations and no components
        String createdSurrogateItem = create(replaced);
        Document surrogateDocument = getDocument(createdSurrogateItem);

        // check surrogate
        assertXmlValidItem(createdSurrogateItem);
        NodeList relationsAfterCreateInSurrogate = selectNodeList(surrogateDocument, "/item/relations/relation");

        assertEquals("Number of relations is wrong ", relationsAfterCreateInSurrogate.getLength(), relationsAfterCreate
            .getLength());
        Node inherited1 = selectSingleNode(surrogateDocument, "/item/relations/relation[1]/@inherited");
        Node inherited2 = selectSingleNode(surrogateDocument, "/item/relations/relation[2]/@inherited");
        assertNotNull("attribute 'inherited' of the element " + "'relation' may not be null", inherited1);
        assertNotNull("attribute 'inherited' of the element " + "'relation' may not be null", inherited2);
    }

    /**
     * Creates a surrogate item, containing a mandatory md-record. The surrogate item references a released original
     * item by a floating reference, whose released version contains only a mandatory md-record, but a latest version in
     * a state pending contains additionally an optional md-record. Tests if a surrogate item representation contains
     * only its own mandatory md-record and does not contain any md-records of the original item.
     */
    @Test
    public void testCreateSurrogateItemWithOwnMdRecordLatestReleasePublicStatusPending() throws Exception {
        String itemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String createdItem = create(itemXml);
        String itemId = getObjidValue(createdItem);
        String itemHref = "/ir/item/" + itemId;
        String param = getTheLastModificationParam(false, itemId, null);
        submit(itemId, param);

        // add object and version pid to release
        String pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignVersionPid(itemId, pidParam);
        pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignObjectPid(itemId, pidParam);

        param = getTheLastModificationParam(false, itemId, null);
        release(itemId, param);
        String released = retrieve(itemId);
        Document releasedDocument = getDocument(released);

        // Append a new md-record to distinguish between a released version
        // and the last pending version.
        Element mdRecord =
            releasedDocument.createElementNS("http://www.escidoc.de/schemas/metadatarecords/0.5",
                "escidocMetadataRecords:md-record");
        mdRecord.setAttribute("name", "name1");
        mdRecord.setAttribute("schema", "bla");
        Element mdRecordContent = releasedDocument.createElement("bla");
        mdRecord.appendChild(mdRecordContent);
        selectSingleNode(releasedDocument, "/item/md-records").appendChild(mdRecord);
        String itemWith2MdRecordXml = toString(releasedDocument, true);
        String updated = update(itemId, itemWith2MdRecordXml);
        Document updatedDocument = getDocument(updated);
        String versionStatus = getVersionStatus(getDocument(updated));
        assertEquals("version status must be 'pending'", "pending", versionStatus);
        assertEquals(2, selectNodeList(updatedDocument, "/item/md-records/md-record").getLength());
        String surrogateItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");
        String replaced = surrogateItemXml.replaceAll("##ITEMHREF##", itemHref);

        String createdSurrogateItem = create(replaced);

        assertXmlValidItem(createdSurrogateItem);
        Document surrogateDocument = getDocument(createdSurrogateItem);
        String originId = null;
        Node origin = selectSingleNode(surrogateDocument, "/item/properties/origin/@href");
        assertNotNull("attribute 'href' of the element " + "'origin' may not be null", origin);
        originId = origin.getNodeValue();
        assertEquals(originId, itemHref);
        Node inherited = selectSingleNode(surrogateDocument, "/item/md-records/md-record[@name='escidoc']/@inherited");
        assertNull("attribute 'inherited' of the element " + "'md-record' must be null", inherited);
        Node secondMdRecord = selectSingleNode(surrogateDocument, "/item/md-records/md-record[@name='name1']");
        assertNull("item may not have a second md-record", secondMdRecord);
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testDecleaningCreateSurrogateItemWithFixedReferenceToUnreleasedVersion() throws Exception {
        String itemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String createdItem = create(itemXml);
        String itemId = getObjidValue(createdItem);
        String itemHref = "/ir/item/" + itemId;
        String param = getTheLastModificationParam(false, itemId, null);
        submit(itemId, param);

        // add object and version pid to release
        String pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignVersionPid(itemId, pidParam);
        pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignObjectPid(itemId, pidParam);

        param = getTheLastModificationParam(false, itemId, null);
        release(itemId, param);
        String released = retrieve(itemId);
        Document releasedDocument = getDocument(released);

        // Append a new md-record to distinguish between a released version
        // and the last pending version.
        Element mdRecord =
            releasedDocument.createElementNS("http://www.escidoc.de/schemas/metadatarecords/0.5",
                "escidocMetadataRecords:md-record");
        mdRecord.setAttribute("name", "name1");
        mdRecord.setAttribute("schema", "bla");
        Element mdRecordContent = releasedDocument.createElement("bla");
        mdRecord.appendChild(mdRecordContent);
        selectSingleNode(releasedDocument, "/item/md-records").appendChild(mdRecord);
        String itemWith2MdRecordXml = toString(releasedDocument, true);
        String updated = update(itemId, itemWith2MdRecordXml);
        Document updatedDocument = getDocument(updated);
        String versionStatus = getVersionStatus(getDocument(updated));
        assertEquals("version status must be 'pending'", "pending", versionStatus);
        assertEquals(2, selectNodeList(updatedDocument, "/item/md-records/md-record").getLength());
        String surrogateItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");
        String replaced = surrogateItemXml.replaceAll("##ITEMHREF##", itemHref + ":2");

        try {
            create(replaced);
            fail("Missing Exception on create an surrogate item with a " + "fixed reference to an unreleased version");
        }
        catch (final Exception e) {
            // FIXME correct exception? its a value to much! JavaDoc: a
            // mandatory attribute value is not set
            Class<?> ec = InvalidStatusException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);

        }

    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testDecleaningCreateSurrogateItemWithReferenceToUnreleasedItem() throws Exception {
        String itemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String createdItem = create(itemXml);
        String itemId = getObjidValue(createdItem);
        String itemHref = "/ir/item/" + itemId;
        String surrogateItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");
        String replaced = surrogateItemXml.replaceAll("##ITEMHREF##", itemHref + ":2");

        try {
            create(replaced);
            fail("Missing Exception on create an surrogate item with a reference" + "to an unreleased item");
        }
        catch (final Exception e) {
            // FIXME correct exception? its a value to much! JavaDoc: a
            // mandatory attribute value is not set
            Class<?> ec = InvalidStatusException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);

        }
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testDecleaningCreateSurrogateItemWithReferenceToWithdrawnItem() throws Exception {
        String itemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String createdItem = create(itemXml);
        String itemId = getObjidValue(createdItem);
        String itemHref = "/ir/item/" + itemId;
        String param = getTheLastModificationParam(false, itemId, null);
        submit(itemId, param);

        // add object and version pid to release
        String pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignVersionPid(itemId, pidParam);
        pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignObjectPid(itemId, pidParam);

        param = getTheLastModificationParam(false, itemId, null);
        release(itemId, param);
        param = getTheLastModificationParam(true, itemId, "withdraw");
        withdraw(itemId, param);
        String surrogateItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");
        String replaced = surrogateItemXml.replaceAll("##ITEMHREF##", itemHref + ":2");

        try {
            create(replaced);
            fail("Missing Exception on create an surrogate item with a reference" + "to a withdrawn item");
        }
        catch (final Exception e) {
            // FIXME correct exception? its a value to much! JavaDoc: a
            // mandatory attribute value is not set
            Class<?> ec = InvalidStatusException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testDecleaningCreateSurrogateItemWithNotExistingReference() throws Exception {

        String surrogateItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");
        String replaced = surrogateItemXml.replaceAll("##ITEMHREF##", "bla");
        try {
            create(replaced);
            fail("Missing Exception on create an surrogate item with a not " + "existing reference");
        }
        catch (final Exception e) {
            // FIXME correct exception? its a value to much! JavaDoc: a
            // mandatory attribute value is not set
            Class<?> ec = InvalidContentException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);

        }

    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void testDecleaningCreateSurrogateItemWithReferenceToSurrogateItem() throws Exception {
        String itemXml =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));
        String createdItem = create(itemXml);
        String itemId = getObjidValue(createdItem);
        String itemHref = "/ir/item/" + itemId;
        String param = getTheLastModificationParam(false, itemId, null);
        submit(itemId, param);

        // add object and version pid to release
        String pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignVersionPid(itemId, pidParam);
        pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignObjectPid(itemId, pidParam);

        param = getTheLastModificationParam(false, itemId, null);
        release(itemId, param);
        String surrogateItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");

        String replaced = surrogateItemXml.replaceAll("##ITEMHREF##", itemHref);

        String createdSurrogateItem = create(replaced);

        String surrogateId = getObjidValue(createdSurrogateItem);
        String surrogateHref = "/ir/item/" + surrogateId;
        param = getTheLastModificationParam(false, surrogateId, null);
        submit(surrogateId, param);

        // add object and version pid to release
        pidParam = getPidParam(surrogateId, "http://somewhere" + itemId);
        assignVersionPid(surrogateId, pidParam);
        pidParam = getPidParam(surrogateId, "http://somewhere" + itemId);
        assignObjectPid(surrogateId, pidParam);

        param = getTheLastModificationParam(false, surrogateId, null);
        release(surrogateId, param);
        String surrogate2ItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");
        String replaced2 = surrogate2ItemXml.replaceAll("##ITEMHREF##", surrogateHref);
        try {
            create(replaced2);
            fail("Missing Exception on create an surrogate item with a reference " + "to a surrogate item");
        }
        catch (final Exception e) {
            // FIXME correct exception? its a value to much! JavaDoc: a
            // mandatory attribute value is not set
            Class<?> ec = InvalidContentException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);

        }
    }

    /**
     * Creates a surrogate item without own md-records. While update a mandatory md-record is added to the surrogate
     * item. Tests if a surrogate item representation after update does not contain the attribute "inherited" in a
     * mandatory md-record any more.
     */
    @Test
    public void testUpdateSurrogateItemWithInheritedMandatoryMdRecord() throws Exception {
        String itemXml =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));
        String createdItem = create(itemXml);
        String itemId = getObjidValue(createdItem);
        String itemHref = "/ir/item/" + itemId;
        String param = getTheLastModificationParam(false, itemId, null);
        submit(itemId, param);

        // add object and version pid to release
        String pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignVersionPid(itemId, pidParam);
        pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignObjectPid(itemId, pidParam);

        param = getTheLastModificationParam(false, itemId, null);
        release(itemId, param);
        String surrogateItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");

        String replaced = surrogateItemXml.replaceAll("##ITEMHREF##", itemHref);
        Document surrogateDocument = getDocument(replaced);
        Node surrogateWithoutMdRecords = deleteElement(surrogateDocument, "/item/md-records");
        String surrogate = toString(surrogateWithoutMdRecords, true);
        String createdSurrogateItem = create(surrogate);
        assertXmlValidItem(createdSurrogateItem);

        String surrogateId = getObjidValue(createdSurrogateItem);
        Document createdSurrogateDocument = getDocument(createdSurrogateItem);
        String originId = null;
        Node origin = selectSingleNode(createdSurrogateDocument, "/item/properties/origin/@href");
        assertNotNull("attribute 'href' of the element " + "'origin' may not be null", origin);
        originId = origin.getNodeValue();
        assertEquals(originId, itemHref);
        Node inherited =
            selectSingleNode(createdSurrogateDocument, "/item/md-records/md-record[@name='escidoc']/@inherited");
        assertNotNull("attribute 'inherited' of the element " + "'md-record' with name 'escidoc' may not be null",
            inherited);
        Node withoutInheritedInMandatoryMdRecord =
            deleteAttribute(createdSurrogateDocument, "/item/md-records/md-record/@inherited");
        String surrogateToUpdate = toString(withoutInheritedInMandatoryMdRecord, false);
        String updatedInherited = update(surrogateId, surrogateToUpdate);
        Document updatedInheritedDocument = getDocument(updatedInherited);
        inherited =
            selectSingleNode(updatedInheritedDocument, "/item/md-records/md-record[@name='escidoc']/@inherited");
        assertNull("attribute 'inherited' of the element " + "'md-record' with name 'escidoc' must be null", inherited);
    }

    /**
     * Creates a surrogate item with own md-record. While update a mandatory md-record is deleted from the surrogate
     * item. Tests if a surrogate item representation after update contains the attribute "inherited" in a mandatory
     * md-record.
     */
    @Test
    public void testUpdateSurrogateItemWithOwnMandatoryMdRecord() throws Exception {
        String itemXml =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));
        String createdItem = create(itemXml);
        String itemId = getObjidValue(createdItem);
        String itemHref = "/ir/item/" + itemId;
        String param = getTheLastModificationParam(false, itemId, null);
        submit(itemId, param);

        // add object and version pid to release
        String pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignVersionPid(itemId, pidParam);
        pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignObjectPid(itemId, pidParam);

        param = getTheLastModificationParam(false, itemId, null);
        release(itemId, param);
        String surrogateItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");

        String replaced = surrogateItemXml.replaceAll("##ITEMHREF##", itemHref);

        String createdSurrogateItem = create(replaced);
        assertXmlValidItem(createdSurrogateItem);

        String surrogateId = getObjidValue(createdSurrogateItem);
        Document createdSurrogateDocument = getDocument(createdSurrogateItem);
        String originId = null;

        Node origin = selectSingleNode(createdSurrogateDocument, "/item/properties/origin/@href");
        assertNotNull("attribute 'href' of the element " + "'origin' may not be null", origin);
        originId = origin.getNodeValue();
        assertEquals(originId, itemHref);

        Node inherited =
            selectSingleNode(createdSurrogateDocument, "/item/md-records/md-record[@name='escidoc']/@inherited");
        assertNull("attribute 'inherited' of the element " + "'md-record' with name 'escidoc' must be null", inherited);

        Node surrogateWithoutMdRecords = deleteElement(createdSurrogateDocument, "/item/md-records");
        String surrogateToUpdate = toString(surrogateWithoutMdRecords, false);
        String updatedInherited = update(surrogateId, surrogateToUpdate);
        Document updatedInheritedDocument = getDocument(updatedInherited);
        inherited =
            selectSingleNode(updatedInheritedDocument, "/item/md-records/md-record[@name='escidoc']/@inherited");
        assertNotNull("attribute 'inherited' of the element " + "'md-record' with name 'escidoc' may not be null",
            inherited);
    }

    /**
     * Creates a surrogate item, which inherites two content streams from its original item. Tests if a surrogate item
     * representation contains the attribite "inherited" in the element "content-streams".
     */
    @Test
    public void testCreateSurrogateItemWithContentStreams() throws Exception {

        Document itemXml =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create_3content-streams.xml");
        Node withoutFirstContentStream = deleteElement(itemXml, "/item/content-streams/content-stream[1]");
        String item = toString(withoutFirstContentStream, false);
        String createdItem = create(item);
        String itemId = getObjidValue(createdItem);
        String itemHref = "/ir/item/" + itemId;
        String param = getTheLastModificationParam(false, itemId, null);
        submit(itemId, param);

        // add object and version pid to release
        String pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignVersionPid(itemId, pidParam);
        pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignObjectPid(itemId, pidParam);

        param = getTheLastModificationParam(false, itemId, null);
        release(itemId, param);
        String surrogateItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");

        String replaced = surrogateItemXml.replaceAll("##ITEMHREF##", itemHref);

        String createdSurrogateItem = create(replaced);
        assertXmlValidItem(createdSurrogateItem);

        Document createdSurrogateDocument = getDocument(createdSurrogateItem);
        Node contentStreamsContainerInherited =
            selectSingleNode(createdSurrogateDocument, "/item/content-streams/@inherited");
        assertNotNull(contentStreamsContainerInherited);
        NodeList contentStreams = selectNodeList(createdSurrogateDocument, "/item/content-streams/content-stream");

        assertEquals(contentStreams.getLength(), 2);
        for (int i = 1; i < 3; i++) {
            Node contentStreamInherited =
                selectSingleNode(createdSurrogateDocument, "/item/content-streams/content-stream[" + i + "]/@inherited");
            assertNotNull(contentStreamInherited);
        }
    }

    /**
     * Creates a surrogate item, containing a mandatory md-record. The surrogate item references a released original
     * item by a floating reference, whose released version contains only a mandatory md-record. Addes an optional
     * md-record to the original item and releases the original item after that. Tests if a surrogate item
     * representation contains the optional md-record of the original item.
     */
    @Test
    public void testChangeSurrogateItemRepresentationWhileOriginalRelease() throws Exception {
        String itemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        String createdItem = create(itemXml);
        String itemId = getObjidValue(createdItem);
        String itemHref = "/ir/item/" + itemId;
        String param = getTheLastModificationParam(false, itemId, null);
        submit(itemId, param);

        // add object and version pid to release
        String pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignVersionPid(itemId, pidParam);
        pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignObjectPid(itemId, pidParam);

        param = getTheLastModificationParam(false, itemId, null);
        release(itemId, param);
        String released = retrieve(itemId);
        Document releasedDocument = getDocument(released);

        // Append a new md-record to distinguish between a released version
        // and the last pending version.
        Element mdRecord =
            releasedDocument.createElementNS("http://www.escidoc.de/schemas/metadatarecords/0.5",
                "escidocMetadataRecords:md-record");
        mdRecord.setAttribute("name", "name1");
        mdRecord.setAttribute("schema", "bla");
        Element mdRecordContent = releasedDocument.createElement("bla");
        mdRecordContent.setTextContent("bla");
        mdRecord.appendChild(mdRecordContent);
        selectSingleNode(releasedDocument, "/item/md-records").appendChild(mdRecord);
        String itemWith2MdRecordXml = toString(releasedDocument, true);
        String updated = update(itemId, itemWith2MdRecordXml);
        Document updatedDocument = getDocument(updated);

        assertEquals(2, selectNodeList(updatedDocument, "/item/md-records/md-record").getLength());

        String surrogateItemXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "surrogate_escidoc_item_198_for_create.xml");
        String replaced = surrogateItemXml.replaceAll("##ITEMHREF##", itemHref);

        String createdSurrogateItem = create(replaced);
        String surrogateId = getObjidValue(createdSurrogateItem);
        assertXmlValidItem(createdSurrogateItem);
        Document surrogateDocument = getDocument(createdSurrogateItem);
        String originId = null;
        Node origin = selectSingleNode(surrogateDocument, "/item/properties/origin/@href");
        assertNotNull("attribute 'href' of the element " + "'origin' may not be null", origin);
        originId = origin.getNodeValue();
        assertEquals(originId, itemHref);
        Node inherited = selectSingleNode(surrogateDocument, "/item/md-records/md-record[@name='escidoc']/@inherited");
        assertNull("attribute 'inherited' of the element " + "'md-record' must be null", inherited);
        Node secondMdRecord = selectSingleNode(surrogateDocument, "/item/md-records/md-record[@name='name1']");
        assertNull("item may not have a second md-record", secondMdRecord);

        param = getTheLastModificationParam(false, itemId, null);
        submit(itemId, param);

        // add object and version pid to release
        pidParam = getPidParam(itemId, "http://somewhere" + itemId);
        assignVersionPid(itemId, pidParam);

        param = getTheLastModificationParam(false, itemId, null);
        release(itemId + ":2", param);
        String adaptedSurrogateItem = retrieve(surrogateId);
        Document adaptedSurrogateItemDocument = getDocument(adaptedSurrogateItem);
        secondMdRecord = selectSingleNode(adaptedSurrogateItemDocument, "/item/md-records/md-record[@name='name1']");
        assertNotNull("item must have a second md-record", secondMdRecord);
    }

}
