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
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.common.fedora.TripleStoreTestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.util.Iterator;
import java.util.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the item resource.
 * 
 * @author Michael Schneider
 */
public class ItemUpdateIT extends ItemTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemUpdateIT.class);

    private String theItemId;

    private String theItemXml;

    public static final String XPATH_TRIPLE_STORE_DC_CREATOR = "/RDF/Description/creator";

    public static final String XPATH_TRIPLE_STORE_DC_IDENTIFIER = "/RDF/Description/identifier";

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        // create an item and save the id and xml data
        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        theItemXml = create(xmlData);
        theItemId = getObjidValue(EscidocAbstractTest.getDocument(theItemXml));
    }

    /**
     * Test successfully adding of component "escidoc" md-record while update.
     */
    @Test
    public void testAddingComponentEscidocMdRecordWhileUpdate() throws Exception {
        Document xmlItem =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create_2_Component_Md-Records.xml");
        Node xmlItemWithoutComponentEscidocMdRecord =
            deleteElement(xmlItem, "/item/components/component[1]/md-records/md-record[@name = 'escidoc']");
        String itemToCreate = toString(xmlItemWithoutComponentEscidocMdRecord, false);
        String createdXml = create(itemToCreate);

        assertXmlValidItem(createdXml);
        Document createdDocument = getDocument(createdXml);

        String createdItemId = getObjidValue(createdDocument);

        // String mdRecordsPath = "/item/components/component/md-records";
        String mdRecordPath = "/item/components/component/md-records/md-record[@name = 'escidoc']";

        Node notExistedComponentMdRecord = selectSingleNode(createdDocument, mdRecordPath);
        assertNull("Escidoc md-record must be deleted", notExistedComponentMdRecord);

        // Node componentMdRecords =
        // selectSingleNode(createdDocument, mdRecordsPath);
        Element mdRecord =
            createdDocument.createElementNS("http://www.escidoc.de/schemas/metadatarecords/0.4",
                "escidocMetadataRecords:md-record");
        mdRecord.setAttribute("name", "escidoc");
        mdRecord.setAttribute("schema", "bla");
        Element mdRecordContent = createdDocument.createElement("oai_dc");
        // add dc:title
        Element mdRecordContentTitle = createdDocument.createElementNS("http://purl.org/dc/elements/1.1/", "dc:title");
        String mdRecordTitleContent = "Title Title Title";
        mdRecordContentTitle.setTextContent(mdRecordTitleContent);
        mdRecordContent.appendChild(mdRecordContentTitle);
        // add dc:description
        Element mdRecordContentDescription =
            createdDocument.createElementNS("http://purl.org/dc/elements/1.1/", "dc:description");
        String mdRecordContentDescriptionContent = "Description";
        mdRecordContentDescription.setTextContent(mdRecordContentDescriptionContent);
        mdRecordContent.appendChild(mdRecordContentDescription);
        mdRecord.appendChild(mdRecordContent);
        selectSingleNode(createdDocument, "/item/components/component/md-records").appendChild(mdRecord);
        String itemWith2ComponentMdRecordXml = toString(createdDocument, true);
        String replaced =
            itemWith2ComponentMdRecordXml.replaceFirst("<escidocItem:item",
                "<escidocItem:item xmlns:dc=\"http://purl.org/dc/elements/1.1/\" ");
        String updatedXml = update(createdItemId, replaced);
        Document updatedDocument = getDocument(updatedXml);
        Node escidocComponentMdRecord = selectSingleNode(updatedDocument, mdRecordPath);
        assertNotNull("Escidoc md-record must be created", escidocComponentMdRecord);
        // check for title from dc-mapping
        selectSingleNodeAsserted(updatedDocument, "/item/components/component/@title[. = '" + mdRecordTitleContent
            + "']");
        // check for description from dc-mapping
        selectSingleNodeAsserted(updatedDocument, "/item/components/component/properties/description[. = '"
            + mdRecordContentDescriptionContent + "']");

    }

    /**
     * Tests successfully updating an Item adding a component with 'escidoc' metadata record.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAddComponentWithEscidocMdRecord() throws Exception {

        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_1_component.xml");
        Document itemDoc = getDocument(xmlData);
        Node componentNode = selectSingleNode(itemDoc, "/item/components/component");
        String component = toString(componentNode, true);
        String componentTitle =
            selectSingleNode(componentNode, "//component/md-records/md-record[@name = 'escidoc']//title/text()")
                .getNodeValue();
        String componentDescription =
            selectSingleNode(componentNode, "//component/md-records/md-record[@name = 'escidoc']//description/text()")
                .getNodeValue();
        Node itemWithoutCompontent = deleteElement(itemDoc, "/item/components");
        String itemXml = create(toString(itemWithoutCompontent, false));

        Document item = getDocument(itemXml, true);
        Node notExpectedComponent = selectSingleNode(item, "/item/components/component");
        assertNull("Item must not have component.", notExpectedComponent);

        String itemId = getObjidValue(itemXml);

        // update with component
        itemXml = itemXml.replace("</escidocComponents:components>", component + "</escidocComponents:components>");
        itemXml = itemXml.replaceAll("prefix-xlink", "xlink");
        Document updateItem = getDocument(itemXml);
        selectSingleNodeAsserted(updateItem, "/item/components/component/md-records/md-record");
        String temp = toString(updateItem, false);
        final String updatedXml = update(itemId, temp);

        // check item with component
        assertXmlValidItem(updatedXml);

        Document updateDocument = EscidocAbstractTest.getDocument(updatedXml);
        // check for component with metadata
        selectSingleNodeAsserted(updateDocument, "/item/components/component/md-records/md-record");
        // check for title from dc-mapping
        selectSingleNodeAsserted(updateDocument, "/item/components/component/@title[. = '" + componentTitle + "']");
        // check for description from dc-mapping
        selectSingleNodeAsserted(updateDocument, "/item/components/component/properties/description[. = '"
            + componentDescription + "']");
    }

    /**
     * Test successfully adding an Component with md-record to an Item without Components.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAddComponentWithMdRecord02() throws Exception {

        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "item_without_component.xml");
        String itemXml = create(xmlData);
        String itemId = getObjidValue(itemXml);

        // add component to created Item
        String itemWithComponent = addComponent(itemXml);

        // update Item with new Component
        final String updatedXml = update(itemId, itemWithComponent);

        // check item with component
        assertXmlValidItem(updatedXml);

        Document updateDocument = EscidocAbstractTest.getDocument(updatedXml);
        // check for component with metadata
        selectSingleNodeAsserted(updateDocument, "/item/components/component/md-records/md-record");
    }

    /**
     * Test update Item without id (id= null).
     * 
     * @throws Exception
     *             If framework
     */
    @Test
    public void testUpdateWithNullId() throws Exception {
        try {

            update(null, this.theItemXml);
            fail("Not expected exception");
        }
        catch (final MissingMethodParameterException e) {
            // Well done
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testIssue393() throws Exception {
        Document item = getDocument(theItemXml);
        String dcTitleXPath = "/item/md-records/md-record[@name=\"escidoc\"]/publication/title";
        String newTitle = getUniqueName(selectSingleNode(item, dcTitleXPath).getTextContent());
        Node toBeUpdated = substitute(item, dcTitleXPath, newTitle);
        try {
            String updated = update(theItemId, toString(toBeUpdated, false));
            assertXmlValidItem(updated);
            Document updatedDocument = getDocument(updated);
            assertXmlEquals("DC-Title in Md Record not updated!", updatedDocument, dcTitleXPath, newTitle);
            assertXmlEquals("Title in Item root element not updated!", updatedDocument, "/item/@title", newTitle);
        }
        catch (final Exception e) {
            failException(e);
        }
    }

    @Test
    public void testUpdateCts() throws Exception {

        Document newItem = EscidocAbstractTest.getDocument(theItemXml);
        selectSingleNode(newItem, "/item/properties/content-model-specific").appendChild(
            newItem.createElement("nischt"));
        substitute(newItem, "/item/@title", "");
        String newItemXml = toString(newItem, false);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("item " + toString(newItem, false));
        }
        String xml = update(theItemId, newItemXml);

        assertNotNull(selectSingleNode(EscidocAbstractTest.getDocument(xml),
            "/item/properties/content-model-specific/nischt"));
        assertXmlValidItem(xml);
    }

    /**
     * Tests successfully updating metadata record of an item.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_UCI_1() throws Exception {

        // changed from retrieve/update metadata to retrieve/update with
        // metadata (2007-01-25, FRS)

        // Changed from update @title to update (dc:)title because @title is not
        // longer stored. And enabled for SOAP too. (2007-11-12, FRS)

        String mdRecordName = "escidoc";
        String newTitle = "new title";
        String titleXPath = "/item/md-records/md-record[@name='" + mdRecordName + "']//title";
        String lastModificationDateXPath = "/item/@last-modification-date";

        // String mdRecordXml = retrieveMetadataRecord(theItemId,
        // mdRecordName);
        String mdRecordXml = retrieve(theItemId);
        Document newMdRecord =
            (Document) substitute(EscidocAbstractTest.getDocument(mdRecordXml), titleXPath, newTitle);
        Node oldModDateNode = selectSingleNode(newMdRecord, lastModificationDateXPath);

        // String xml = updateMetadataRecord(theItemId, mdRecordName,
        // toString(
        // newMdRecord, false));
        String tmp = toString(newMdRecord, false);
        final String updatedXml = update(theItemId, tmp);
        Document updateDocument = EscidocAbstractTest.getDocument(updatedXml);
        Node newModDateNode = selectSingleNode(updateDocument, lastModificationDateXPath);

        assertDateBeforeAfter(oldModDateNode.getNodeValue(), newModDateNode.getNodeValue());

        Node updatedTitleNode = selectSingleNode(updateDocument, titleXPath);
        String updatedTitleString = updatedTitleNode.getTextContent();

        assertEquals(newTitle, updatedTitleString);

        assertXmlValidItem(updatedXml);
    }

    /**
     * Tests successfully updating 'escidoc' metadata record of an item.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_UCI_1_3() throws Exception {

        // changed from retrieve/update metadata to retrieve/update with
        // metadata (2007-01-25, FRS)

        String mdRecordName = "escidoc";
        String newName = "new name";
        String mdXPath =
            "/item/md-records/md-record[@name='" + mdRecordName + "']/publication/creator/person/family-name";

        String lastModificationDateXPath = "/item/@last-modification-date";

        // String mdRecordXml = retrieveMetadataRecord(theItemId,
        // mdRecordName);
        String mdRecordXml = retrieve(theItemId);
        String oldCreatorName =
            selectSingleNode(EscidocAbstractTest.getDocument(mdRecordXml), mdXPath).getTextContent();
        TripleStoreTestBase tripleStore = new TripleStoreTestBase();
        String result =
            tripleStore.requestMPT("<info:fedora/" + theItemId + "> " + "<http://purl.org/dc/elements/1.1/creator>"
                + " *", "RDF/XML");
        Node dcCreator = selectSingleNode(EscidocAbstractTest.getDocument(result), XPATH_TRIPLE_STORE_DC_CREATOR);
        assertNotNull("DC Creator not in TripleStore", dcCreator);
        String oldCreatorDcName = dcCreator.getTextContent();
        assertTrue(oldCreatorName + " is not contained in DC 'creator' ", oldCreatorDcName.endsWith(oldCreatorName));
        Document newMdRecord = (Document) substitute(EscidocAbstractTest.getDocument(mdRecordXml), mdXPath, newName);
        Node oldModDateNode = selectSingleNode(newMdRecord, lastModificationDateXPath);

        // String xml = updateMetadataRecord(theItemId, mdRecordName,
        // toString(
        // newMdRecord, false));
        final String updatedXml = update(theItemId, toString(newMdRecord, false));
        Document updateDocument = EscidocAbstractTest.getDocument(updatedXml);
        Node newModDateNode = selectSingleNode(updateDocument, lastModificationDateXPath);

        assertDateBeforeAfter(oldModDateNode.getNodeValue(), newModDateNode.getNodeValue());

        assertXmlValidItem(updatedXml);
        result =
            tripleStore.requestMPT("<info:fedora/" + theItemId + "> " + "<http://purl.org/dc/elements/1.1/creator>"
                + " *", "RDF/XML");
        String newCreatorDcName =
            selectSingleNode(EscidocAbstractTest.getDocument(result), XPATH_TRIPLE_STORE_DC_CREATOR).getTextContent();
        assertTrue(newName + " is not contained in DC 'creator' ", newCreatorDcName.endsWith(newName));

    }

    /**
     * Check the DC Mapping. A Metadata Record 'escidoc' is created. The md-record is converted to DC data stream by
     * framework. DC elements are put into TripleStore by Fedora. These test checks the Mapping indirectly by requesting
     * the TripleStore.
     * 
     * @throws Exception
     *             Thrown if DC mapping fails.
     */
    @Test
    public void testDcMapping() throws Exception {

        Document itemDoc =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "item_without_component.xml");

        // replace MD-Record ---------------
        // remove md-record
        Node oldMdRecord = selectSingleNode(itemDoc, "/item/md-records/md-record");
        oldMdRecord.getParentNode().removeChild(oldMdRecord);

        // add new md-record
        String xmlMdRecord = EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "md-record.xml");
        Node mdRecord = selectSingleNode(EscidocAbstractTest.getDocument(xmlMdRecord), "/md-record");

        itemDoc.importNode(mdRecord, true);

        Node mdRecords = selectSingleNode(itemDoc, "/item/md-records");
        mdRecords.appendChild(itemDoc.adoptNode(mdRecord));

        itemDoc.normalize();
        String newXml = toString(itemDoc, false);

        // create Item
        String newItemXml = create(newXml);
        String itemId = getObjidValue(newItemXml);

        // assert values
        String oldCreatorName =
            selectSingleNode(itemDoc,
                "/item/md-records/md-record[@name='escidoc']/" + "publication/creator/person/family-name")
                .getTextContent();

        // check TripleStore ---------------------------------------------------
        TripleStoreTestBase tripleStore = new TripleStoreTestBase();
        // for creator
        String result =
            tripleStore.requestMPT(
                "<info:fedora/" + itemId + "> " + "<http://purl.org/dc/elements/1.1/creator>" + " *", "RDF/XML");
        Node dcCreator = selectSingleNode(EscidocAbstractTest.getDocument(result), XPATH_TRIPLE_STORE_DC_CREATOR);
        assertNotNull("DC Creator not in TripleStore", dcCreator);

        String oldCreatorDcName = dcCreator.getTextContent();
        assertTrue(oldCreatorName + " is not contained in DC 'creator' ", oldCreatorDcName.endsWith(oldCreatorName));

        // DC identifier (the objid is set by mapper)
        result =
            tripleStore.requestMPT("<info:fedora/" + itemId + "> " + "<http://purl.org/dc/elements/1.1/identifier>"
                + " \"" + itemId + "\"", "RDF/XML");
        Node dcIdentifier = selectSingleNode(EscidocAbstractTest.getDocument(result), XPATH_TRIPLE_STORE_DC_IDENTIFIER);
        assertNotNull("DC Identifier (with objid) is not in TripleStore", dcIdentifier);

        // TODO
        // title
        // further DC values
    }

    /**
     * Tests updating item with revision/public-status=withdrawn.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_UCI_3() throws Exception {

        Document newItem =
            (Document) substitute(EscidocAbstractTest.getDocument(theItemXml), "/item/properties/public-status",
                "withdrawn");
        String newItemXml = toString(newItem, false);

        try {
            update(theItemId, newItemXml);
        }
        catch (final Exception e) {
            fail("No exception expected on update with public-status set to 'withdrawn'. " + e);
        }
    }

    /**
     * Change existing Component with new one.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_UCI_2() throws Exception {

        Document curItem = EscidocAbstractTest.getDocument(theItemXml);
        // save component
        // Node curFirstComponent = selectSingleNode(curItem,
        // "/item/components/component[1]");
        // String curFirstComponentId = null; // selectSingleNode(curItem,
        // // "/item/components/component[1]/@objid/text()").getNodeValue();
        // Pattern PATTERN_OBJID_ATTRIBUTE =
        // Pattern.compile("objid=\"([^\"]*)\"");
        // Matcher m =
        // PATTERN_OBJID_ATTRIBUTE.matcher(toString(curFirstComponent,
        // true));
        // if (m.find()) {
        // curFirstComponentId = m.group(1);
        // }

        // get new component from template
        String templateComponentXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        Node newComponent =
            selectSingleNode(EscidocAbstractTest.getDocument(templateComponentXml), "/item/components/component[2]");

        // add new component to item
        // string op start
        String theItemXmlWith2AddComp = insertNamespacesInRootElement(theItemXml);
        theItemXmlWith2AddComp =
            theItemXmlWith2AddComp.replaceFirst("</escidocComponents:components>", toString(newComponent, true)
                + toString(newComponent, true) + "</escidocComponents:components>");
        // string op end

        // Node nC = curItem.adoptNode(newComponent);
        // Node n = deleteElement(curItem, "/item/components/component[1]");
        // Node components = selectSingleNode(curItem, "/item/components");
        // // components.appendChild(nC);
        //
        String newItemXml = theItemXmlWith2AddComp;
        // String theItemXmlX = theItemXml
        // .replaceFirst("</escidocComponents:component>",
        // "</escidocComponents:component>X");
        // String newItemXml = theItemXmlX.replaceFirst(
        // "<escidocComponents:component\\ .+</escidocComponents:component>X",
        // "");
        //
        update(theItemId, newItemXml);
        //
        // try {
        // retrieveComponent(theItemId, null);
        // }
        // catch (final Exception e) {
        // Class ec = ComponentNotFoundException.class;
        // assertExceptionType(ec.getName() + " expected.", ec, e);
        // }
        // assertXMLValidItem(updatedItemXml);

    }

    /**
     * Delete one of two existing Components with update item.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_UCI_2_1() throws Exception {

        submit(theItemId, getTheLastModificationParam(false, theItemId));
        releaseWithPid(theItemId);
        theItemXml = retrieve(theItemId);

        Document curItem = EscidocAbstractTest.getDocument(theItemXml);
        // save component
        String curFirstComponentId = null;
        String curSecondComponentId = null;
        curFirstComponentId =
            getObjidFromHref(selectSingleNode(curItem, "/item/components/component[1]/@href").getNodeValue());
        curSecondComponentId =
            getObjidFromHref(selectSingleNode(curItem, "/item/components/component[2]/@href").getNodeValue());
        final String itemUpdatedXml =
            update(theItemId, toString(deleteElement(curItem, "/item/components/component[1]"), false));
        assertNotNull(itemUpdatedXml);
        Document document = EscidocAbstractTest.getDocument(itemUpdatedXml);
        assertNull("Deleted component returned from update",
        // /ir/item/escidoc:18372/components/component/escidoc:18374
            selectSingleNode(document, "/item/components/component[@href=\"" + "/ir/item/" + theItemId
                + "/components/component/" + curFirstComponentId + "\"]"));
        assertNotNull("Deleted component returned from update", selectSingleNode(document,
            "/item/components/component[@href=\"" + "/ir/item/" + theItemId + "/components/component/"
                + curSecondComponentId + "\"]"));
        try {
            retrieveComponent(theItemId, curFirstComponentId);
            EscidocAbstractTest.failMissingException("No exception on retrieve deleted component.",
                ComponentNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ComponentNotFoundException.class, e);
        }

        Node oldModDate = selectSingleNode(curItem, "/item/@last-modification-date");
        String updatedItemXml = retrieve(theItemId);
        Node newModDate =
            selectSingleNode(EscidocAbstractTest.getDocument(updatedItemXml), "/item/@last-modification-date");

        assertDateBeforeAfter(oldModDate.getNodeValue(), newModDate.getNodeValue());
        assertXmlValidItem(updatedItemXml);
    }

    /**
     * Add two Components with content href.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_UCI_2_2() throws Exception {

        Document curItem = EscidocAbstractTest.getDocument(theItemXml);
        Node oldModDate = selectSingleNode(curItem, "/item/@last-modification-date");

        // get new component from template
        String templateComponentXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        Node newComponent =
            selectSingleNode(EscidocAbstractTest.getDocument(templateComponentXml), "/item/components/component[2]");

        // add new component to item
        // string op start
        String theItemXmlWith2AddComp = insertNamespacesInRootElement(theItemXml);
        theItemXmlWith2AddComp =
            theItemXmlWith2AddComp.replaceFirst("</escidocComponents:components>", toString(newComponent, true)
                + toString(newComponent, true) + "</escidocComponents:components>");
        // string op end

        // DOM op
        // curItem = insertNamespacesInRootElement(curItem);
        // Node components = selectSingleNode(curItem, "/item/components");
        // components.appendChild(curItem.adoptNode(newComponent));

        String newItemXml = theItemXmlWith2AddComp;// toString(curItem, false);
        update(theItemId, newItemXml);

        String xml = retrieve(theItemId);
        Document xmlDoc = EscidocAbstractTest.getDocument(xml);

        Node c = selectSingleNode(xmlDoc, "/item/components/component[2]");
        assertNotNull(c);

        Node newModDate = selectSingleNode(xmlDoc, "/item/@last-modification-date");

        assertDateBeforeAfter(oldModDate.getNodeValue(), newModDate.getNodeValue());

    }

    /**
     * Add one Component with content inline and two md-records.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_UCI_2_4() throws Exception {

        Document curItem = EscidocAbstractTest.getDocument(theItemXml);

        /*
         * If you test Components, please be aware that the order of Component is not defined. This means that the
         * latest added Component has not to be the latest in the representation. The only way to differ Components are
         * objids (or href). I know this is sometimes inconvenient, espacially if the objid is added by framework.
         */

        // obtain objids of existing Components
        Vector<String> compIds = obtainComponentIds(curItem);

        // get new component from template
        String templateComponentXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create_2_Component_Md-Records.xml");

        Node newComponent =
            selectSingleNode(EscidocAbstractTest.getDocument(templateComponentXml), "/item/components/component[1]");

        // add new component to item
        // TODO replace string ops against DOM ops
        // string op start
        String theItemXmlWith1AddComp = insertNamespacesInRootElement(theItemXml);
        theItemXmlWith1AddComp =
            theItemXmlWith1AddComp.replaceFirst("</escidocComponents:components>", toString(newComponent, true)
                + "</escidocComponents:components>");
        // string op end

        String newItemXml = theItemXmlWith1AddComp;
        String xml = update(theItemId, newItemXml);
        Document updatedItemDoc = EscidocAbstractTest.getDocument(xml);

        Vector<String> compIdsUpdate = obtainComponentIds(updatedItemDoc);
        assertEquals("Component was not added", compIds.size() + 1, compIdsUpdate.size());

        // what is objid of new Component
        String newComponentId = null;
        for (int i = 0; i < compIdsUpdate.size(); i++) {
            if (!compIds.contains(compIdsUpdate.get(i))) {
                newComponentId = compIdsUpdate.get(i);
                break;
            }
        }

        // Compare number of MdRecords of Components
        Iterator<String> it = compIdsUpdate.iterator();
        while (it.hasNext()) {
            String componentId = it.next();
            String xPathCompId = null;
            xPathCompId =
                "/item/components/component" + "[@href='/ir/item/" + this.theItemId + "/components/component/"
                    + componentId + "']/md-records/md-record";

            NodeList mdRecordsCreate = null;
            if (componentId.equals(newComponentId)) {
                // select MdReocrds of Item for update (the send Item)
                mdRecordsCreate =
                    selectNodeList(EscidocAbstractTest.getDocument(newItemXml),
                        "/item/components/component[not(@*)]/md-records/md-record");

            }
            else {
                // select MdReocrds of created Item
                mdRecordsCreate = selectNodeList(curItem, xPathCompId);
            }

            NodeList mdRecordsUpdate = selectNodeList(updatedItemDoc, xPathCompId);
            assertEquals("Number of md-records in Component '" + componentId + "'", mdRecordsCreate.getLength(),
                mdRecordsUpdate.getLength());
        }

    }

    /**
     * Add Component with content inline.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_UCI_2_3() throws Exception {

        Document curItem = EscidocAbstractTest.getDocument(theItemXml);
        Node oldModDate = selectSingleNode(curItem, "/item/@last-modification-date");

        // get new component from template
        String templateComponentXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        Node newComponent =
            selectSingleNode(EscidocAbstractTest.getDocument(templateComponentXml), "/item/components/component[1]");
        String newComponentXml = toString(newComponent, true);
        newComponentXml =
            newComponentXml.replaceFirst("<escidocComponents:component",
                "<escidocComponents:component xmlns:prefix-xlink=\"" + "http://www.w3.org/1999/xlink\" ");
        // add new component to item
        // string op start
        String theItemXmlX =
            theItemXml.replaceFirst("</escidocComponents:components>", "X</escidocComponents:components>");
        String newItemXml =
            theItemXmlX.replaceFirst("X</escidocComponents:components>", newComponentXml
                + "</escidocComponents:components>");

        update(theItemId, newItemXml);
        String xml = retrieve(theItemId);
        Document xmlDoc = EscidocAbstractTest.getDocument(xml);

        Node c = selectSingleNode(xmlDoc, "/item/components/component[3]");
        assertNotNull(c);

        Node newModDate = selectSingleNode(xmlDoc, "/item/@last-modification-date");

        assertDateBeforeAfter(oldModDate.getNodeValue(), newModDate.getNodeValue());
        // add new component to item
    }

    /**
     * Item with missing ContextID.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_UCI_4_1() throws Exception {

        Document item = EscidocAbstractTest.getDocument(theItemXml);
        Node node = selectSingleNode(item, "/item/properties/context");

        // remove context.href
        NamedNodeMap atts = node.getAttributes();
        if (atts != null) {
            atts.removeNamedItem(EscidocTestBase.XLINK_HREF_ESCIDOC);
        }

        String newItemXml = toString(item, false);
        String xml = update(theItemId, newItemXml);

        // remove context
        item = EscidocAbstractTest.getDocument(xml);
        node = selectSingleNode(item, "/item/properties/context");

        node.getParentNode().removeChild(node);

        newItemXml = toString(item, false);
        xml = update(theItemId, newItemXml);
        assertXmlValidItem(xml);
    }

    /**
     * Item with missing escidoc internal metadata.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(expected = MissingMdRecordException.class)
    public void testOM_UCI_4_2() throws Exception {
        Document item = EscidocAbstractTest.getDocument(theItemXml);
        // Node newItem = deleteElement(item,
        // "/item/md-records/md-record[@name='escidoc']");
        Node newItem = substitute(item, "/item/md-records/md-record/@name", "codicse");

        String newItemXml = toString(newItem, true);
        update(theItemId, newItemXml);
    }

    /**
     * Delete all components by update item without components element.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testItemWithoutComponents() throws Exception {

        Document item = EscidocAbstractTest.getDocument(theItemXml);
        deleteElement(item, "/item/components");
        String newItemXml = toString(item, false);

        String updatedItem = update(theItemId, newItemXml);
        assertNull(selectSingleNode(EscidocAbstractTest.getDocument(updatedItem), "/item/components/component"));
        assertXmlValidItem(updatedItem);

        updatedItem = retrieve(theItemId);
        assertNull(selectSingleNode(EscidocAbstractTest.getDocument(updatedItem), "/item/components/component"));
        assertXmlValidItem(updatedItem);

        // update with retrieved item, still no components
        updatedItem = update(theItemId, updatedItem);
        assertNull(selectSingleNode(EscidocAbstractTest.getDocument(updatedItem), "/item/components/component"));
        assertXmlValidItem(updatedItem);
    }

    /**
     * Delete all components by update item without any component element.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testItemWithoutComponent() throws Exception {

        Document item = EscidocAbstractTest.getDocument(theItemXml);

        while (selectNodeList(item, "/item/components/component").getLength() != 0) {
            item = (Document) deleteElement(item, "/item/components/component");
        }

        String newItemXml = toString(item, false);

        String updatedItem = update(theItemId, newItemXml);
        assertNull("Components must be deleted.", selectSingleNode(getDocument(updatedItem),
            "/item/components/component"));
        assertXmlValidItem(updatedItem);

        updatedItem = retrieve(theItemId);
        assertNull("Components must be deleted.", selectSingleNode(getDocument(updatedItem),
            "/item/components/component"));
        assertXmlValidItem(updatedItem);
    }

    /**
     * Item does not validate against Schema.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Ignore("Item does not validate against Schema - test not yet implemented")
    @Test
    public void testOM_UCI_5() throws Exception {

    }

    /**
     * Correct Item with one Component (binary content inline)
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Ignore("Correct Item with one Component (binary content inline) - test not yet implemented")
    @Test
    public void testOM_UCI_6_1() throws Exception {

    }

    /**
     * Correct Item with one Component (binary content link)
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Ignore("Correct Item with one Component (binary content link) - test not yet implemented")
    @Test
    public void notestOM_UCI_6_2() throws Exception {

    }

    /**
     * Optimistic locking test: LastModification timestamp of the XML item is older than the LastModification timestamp
     * in the system - update not allowed.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(expected = OptimisticLockingException.class)
    public void testOM_UCI_7_1() throws Exception {

        Document item = EscidocAbstractTest.getDocument(theItemXml);

        // change last-modification-date
        NamedNodeMap atts = item.getDocumentElement().getAttributes();
        // Node lastModificationDateNode =
        // atts.getNamedItem("last-modification-date");
        // String lastModificationDate =
        // lastModificationDateNode.getNodeValue();

        Attr newAtt = item.createAttribute("last-modification-date");
        // newAtt.setNodeValue(lastModificationDate.replaceFirst("\\.d{3}.*$",
        // ""));
        newAtt.setNodeValue("1970-01-01T00:00:00.000Z");
        atts.setNamedItem(newAtt);

        update(theItemId, toString(item, false));
    }

    /**
     * Nonexisting ID for Item.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(expected = ItemNotFoundException.class)
    public void testOM_UCI_8_1() throws Exception {

        update("test", theItemXml);
    }

    /**
     * Component with given id does not exist. Item with nonexisting ID for Component [creates a new component]
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(expected = InvalidContentException.class)
    public void testOM_UCI_8_2() throws Exception {

        Document curItem = EscidocAbstractTest.getDocument(theItemXml);

        Node component = selectSingleNode(curItem, "/item/components/component[1]");
        NamedNodeMap atts = component.getAttributes();

        Node newItem = null;
        Node hrefNode = atts.getNamedItem(EscidocTestBase.XLINK_HREF_ESCIDOC);
        String hrefVal = hrefNode.getNodeValue();
        String hrefNewVal = hrefVal.replaceFirst("/component/escidoc:[^\"]+", "/component/escidoc:x");
        hrefNode.setNodeValue(hrefNewVal);
        newItem = substitute(curItem, "/item/components/component[1]/@href", hrefNewVal);
        String newItemXml = toString(newItem, true);

        update(theItemId, newItemXml);
    }

    /**
     * Incorrect Component: Inline File(s) too big
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Ignore("Incorrect Component: Inline File(s) too big - test not yet implemented")
    @Test
    public void testOM_UCI_10_1() throws Exception {

    }

    /**
     * Incorrect Component: binary content link is not valid (HTTP 404).
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(expected = FileNotFoundException.class)
    public void testOM_UCI_10_2() throws Exception {
        Document newItem = EscidocAbstractTest.getDocument(theItemXml);
        Node itemWithWrongContentHref =
            substitute(newItem, "/item/components/component/content[1]/@href", "http://localhost/bla");
        String xmlItemWithWrongContentHref = toString(itemWithWrongContentHref, true);

        update(theItemId, xmlItemWithWrongContentHref);
    }

    /**
     * Update binary content by link
     * 
     * @test.name Update Component content by href
     * @test.id OM_UCI_10-3
     * @test.input XML item with Component with new binary content link
     * @test.expected Error message
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    @Ignore
    public void testOM_UCI_10_3() throws Exception {
        Document newItem = getDocument(theItemXml);
        Node itemWithNewContentHref =
            substitute(newItem, "/item/components/component/content[1]/@href",
                "http://localhost:8080/images/escidoc-logo.jpg");
        String xmlItemWithNewContentHref = toString(itemWithNewContentHref, true);

        String xml = update(theItemId, xmlItemWithNewContentHref);

        // TODO check binary content
    }

    /**
     * Item with set readonly element.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_UCI_12() throws Exception {
        Document newItem = EscidocAbstractTest.getDocument(theItemXml);
        Node itemWithWrongCreationdate =
            substitute(newItem, "/item/properties/creation-date", "1970-01-01T00:00:00.000Z");
        String xmlItemWithWrongCreationdate = toString(itemWithWrongCreationdate, true);

        try {
            String xml = update(theItemId, xmlItemWithWrongCreationdate);
        }
        catch (final Exception e) {
            fail("Not expected exception " + e);
        }

    }

    @Test
    public void testUpdateComponentProperties() throws Exception {

        Document newItem = EscidocAbstractTest.getDocument(theItemXml);

        int cnum = 1;
        Node validStatus =
            selectSingleNode(newItem, "/item/components/component[" + cnum + "]/properties/valid-status");

        if (validStatus == null) {
            // use next component
            cnum++;
        }

        // determine a xpath that reliably selects the component
        String basePath = null;
        String componentHref = null;
        componentHref = selectSingleNode(newItem, "/item/components/component[" + cnum + "]/@href").getNodeValue();
        basePath = "/item/components/component[@href='" + componentHref + "']/properties/";

        // test update all updatable properties
        String testStringValue = "testing";

        // valid-status visibility content-category mime-type
        // description and file-name are set throu metadata "escidoc"
        // (file-size and locator-url do not longer exist in component)
        // TODO check if values are new
        newItem = (Document) substitute(newItem, basePath + "valid-status", testStringValue);
        newItem = (Document) substitute(newItem, basePath + "visibility", "institutional");
        newItem = (Document) substitute(newItem, basePath + "content-category", testStringValue);
        newItem =
            (Document) substitute(newItem, basePath + "mime-type", "application/vnd.mitsubishi.misty-guard.trustweb");

        String newItemXml = toString(newItem, true);
        newItemXml = update(theItemId, newItemXml);
        Document updatedItem = EscidocAbstractTest.getDocument(newItemXml);

        assertEquals(testStringValue, selectSingleNode(updatedItem, basePath + "valid-status/text()").getNodeValue());
        assertEquals("institutional", selectSingleNode(updatedItem, basePath + "visibility/text()").getNodeValue());
        assertEquals(testStringValue, selectSingleNode(updatedItem, basePath + "content-category/text()")
            .getNodeValue());
        assertEquals("application/vnd.mitsubishi.misty-guard.trustweb", selectSingleNode(updatedItem,
            basePath + "mime-type/text()").getNodeValue());

        newItemXml = retrieve(theItemId);
        updatedItem = EscidocAbstractTest.getDocument(newItemXml);

        assertEquals(testStringValue, selectSingleNode(updatedItem, basePath + "valid-status/text()").getNodeValue());
        assertEquals("institutional", selectSingleNode(updatedItem, basePath + "visibility/text()").getNodeValue());
        assertEquals(testStringValue, selectSingleNode(updatedItem, basePath + "content-category/text()")
            .getNodeValue());
        assertEquals("application/vnd.mitsubishi.misty-guard.trustweb", selectSingleNode(updatedItem,
            basePath + "mime-type/text()").getNodeValue());
    }

    /**
     * Successfully create a new component via update item.
     */
    @Test
    public void testUpdateItemWithNewComponent() throws Exception {

        Document newItem = EscidocAbstractTest.getDocument(theItemXml);
        Document componentDoc = null;
        String componentXml = null;

        int cnum = 1;
        String componentId = getObjidValue(newItem, "/item/components/component[" + cnum + "]");

        // with wrong objid
        componentXml = retrieveComponent(theItemId, componentId);
        componentDoc = getDocument(componentXml);
        componentXml = toString(componentDoc, true).replaceAll(componentId, "escidoc:ex6");

        String updateItemXml = theItemXml.replaceFirst("(:components[^>]*>)", "$1" + componentXml);
        try {
            update(theItemId, updateItemXml);
            fail("Exception expected.");
        }
        catch (final Exception e) {
            Class ec = InvalidContentException.class;
            assertExceptionType(ec, e);
        }

        // with empty objid
        componentXml = retrieveComponent(theItemId, componentId);
        componentDoc = getDocument(componentXml);
        componentDoc = (Document) substitute(componentDoc, "/component/@href", "");
        componentXml = toString(componentDoc, true);

        updateItemXml = theItemXml.replaceFirst("(:components[^>]*>)", "$1" + componentXml);
        update(theItemId, updateItemXml);

        // now there are 3 components
        selectSingleNodeAsserted(getDocument(retrieve(theItemId)), "/item/components/component[3]");

        theItemXml = retrieve(theItemId);
        // without objid and type and title
        componentXml = retrieveComponent(theItemId, componentId);
        componentDoc = getDocument(componentXml);
        selectSingleNode(componentDoc, "/component").getAttributes().removeNamedItem("xlink:href");
        selectSingleNode(componentDoc, "/component").getAttributes().removeNamedItem("xlink:type");
        selectSingleNode(componentDoc, "/component").getAttributes().removeNamedItem("xlink:title");
        componentXml = toString(componentDoc, true);

        updateItemXml = theItemXml.replaceFirst("(:components[^>]*>)", "$1" + componentXml);
        update(theItemId, updateItemXml);

        // now there are 4 components
        selectSingleNodeAsserted(getDocument(retrieve(theItemId)), "/item/components/component[4]");
    }

    /**
     * Decline update item with two components with same id.
     */
    @Test
    public void testUpdateItemWithDublicateComponent() throws Exception {
        Document newItem = EscidocAbstractTest.getDocument(theItemXml);
        Document updateItem = null;

        // with wrong objid
        this.theItemXml = retrieve(theItemId);
        newItem = getDocument(this.theItemXml);

        int cnum = 1;
        String componentId = getObjidValue(newItem, "/item/components/component[" + cnum + "]");
        cnum++;
        String componentHrefToReplace =
            selectSingleNode(newItem, "/item/components/component[" + cnum + "]/@href").getNodeValue();

        newItem =
            (Document) substitute(newItem, "/item/components/component[" + cnum + "]/@href", "/ir/item/"
                + this.theItemId + "/components/component/" + componentId);

        String updateItemXml = toString(newItem, false);

        try {
            this.theItemXml = update(theItemId, updateItemXml);
            fail("Exception expected.");
        }
        catch (final Exception e) {
            Class ec = InvalidContentException.class;
            assertExceptionType(ec, e);
        }

        // ensure still two components
        selectSingleNodeAsserted(getDocument(this.theItemXml), "/item/components/component[2]");
    }

    @Test
    public void testUpdateComponentWithWrongHrefContent() throws Exception {
        // description
        // visibility content-category file-name mime-type file-size
        Document newItem = EscidocAbstractTest.getDocument(theItemXml);

        String componentId = null;
        String basePath = null;
        componentId = getObjidFromHref(selectSingleNode(newItem, "/item/components/component/@href").getNodeValue());
        basePath =
            "/item/components/component[@href=\"" + "/ir/item/" + theItemId + "/components/component/" + componentId
                + "\"]/properties/";
        String testStringValue = "testing";
        String testMimetypeValue = "image/tiff";
        // TODO check if values are new
        newItem = (Document) substitute(newItem, basePath + "visibility", "institutional");
        // TODO content-category read-only !?
        // newItem = (Document) substitute(newItem, basePath +
        // "content-category",
        // "abstract");
        newItem = (Document) substitute(newItem, basePath + "file-name", testStringValue);
        newItem = (Document) substitute(newItem, basePath + "mime-type", testMimetypeValue);
        // newItem =
        // (Document) substitute(newItem, basePath + "file-size",
        // testStringValue);

        String newItemXml = toString(newItem, true);
        update(theItemId, newItemXml);

        Document updatedItem = EscidocAbstractTest.getDocument(retrieve(theItemId));

        // assertEquals(testStringValue, selectSingleNode(updatedItem,
        // basePath + "description/text()").getNodeValue());
        assertEquals("institutional", selectSingleNode(updatedItem, basePath + "visibility/text()").getNodeValue());
        // assertEquals("abstract", selectSingleNode(updatedItem,
        // basePath + "content-category/text()").getNodeValue());
        // assertEquals(testStringValue, selectSingleNode(updatedItem,
        // basePath + "file-name/text()").getNodeValue());
        assertEquals(testMimetypeValue, selectSingleNode(updatedItem, basePath + "mime-type/text()").getNodeValue());
        // assertEquals(testStringValue, selectSingleNode(updatedItem,
        // basePath + "file-size/text()").getNodeValue());
    }

    @Test
    public void testUpdateComponentReadonlyProperties() throws Exception {
        // creation-date
        // pid
        // created-by/@xlink:href created-by/@xlink:title created-by/@xlink:type

        Document document = EscidocAbstractTest.getDocument(theItemXml);

        // String componentId = selectSingleNode(document,
        // "/item/components/component/@objid").getNodeValue();
        // String basePath = "/item/components/component[@objid = '" +
        // componentId
        // + "']/properties/";
        String componentId = null;
        String basePath = null;
        componentId = getObjidFromHref(selectSingleNode(document, "/item/components/component/@href").getNodeValue());
        basePath =
            "/item/components/component[@href=\"" + "/ir/item/" + theItemId + "/components/component/" + componentId
                + "\"]/properties/";
        String testStringValue = "testing";
        String testDateValue = "1970-01-01T01:00:00.000Z";
        // TODO check if values are new
        // TODO attribute selection/substitution
        Node testItem;
        String xml = null;
        testItem = substitute(document, basePath + "creation-date", testDateValue);
        try {
            xml = update(theItemId, toString(testItem, true));
        }
        catch (final Exception e) {
            fail("No exception after update creation-date expected.");
        }
        assertNull(selectSingleNode(EscidocAbstractTest.getDocument(xml), basePath + "creation-date[text() = '"
            + testDateValue + "']"));

        // document = getDocument(xml);
        // testItem = substitute(document, basePath + "valid-status",
        // "invalid");
        // try {
        // xml = update(theItemId, toString(testItem, true));
        // }
        // catch (final Exception e) {
        // fail("No exception after update valid-status expected.");
        // // assertExceptionType(ee.getName() + " expected.", ee, e);
        // }

        // pid
        document = EscidocAbstractTest.getDocument(xml);
        if (selectSingleNode(document, basePath + "pid") != null) {
            testItem = substitute(document, basePath + "pid", testStringValue);
            try {
                xml = update(theItemId, toString(testItem, true));
            }
            catch (final Exception e) {
                fail("No exception after update pid expected.");
            }
        }
        assertNull(selectSingleNode(EscidocAbstractTest.getDocument(xml), basePath + "pid[text() = '" + testStringValue
            + "']"));

        // content-category
        // not fix
        document = EscidocAbstractTest.getDocument(xml);
        testItem = substitute(document, basePath + "content-category", "invalid");
        try {
            xml = update(theItemId, toString(testItem, true));
        }
        catch (final Exception e) {
            fail("No exception after update content-category expected.");
            // assertExceptionType(ee.getName() + " expected.", ee, e);
        }
        // file-name
        document = EscidocAbstractTest.getDocument(xml);
        testItem = substitute(document, basePath + "file-name", "invalid");
        try {
            xml = update(theItemId, toString(testItem, true));
        }
        catch (final Exception e) {
            fail("No exception after update file-name expected.");
            // assertExceptionType(ee.getName() + " expected.", ee, e);
        }

        // mime-type
        document = EscidocAbstractTest.getDocument(xml);
        testItem = substitute(document, basePath + "mime-type", "application/pdf");
        try {
            xml = update(theItemId, toString(testItem, true));
        }
        catch (final Exception e) {
            fail("No exception after update mime-type expected.");
            // assertExceptionType(ee.getName() + " expected.", ee, e);
        }

        // // file-size
        // document = EscidocAbstractTest.getDocument(xml);
        // // testItem = substitute(document, basePath + "file-size",
        // "invalid");
        // try {
        // xml = update(theItemId, toString(testItem, true));
        // }
        // catch (final Exception e) {
        // fail("No exception after update file-size expected.");
        // // assertExceptionType(ee.getName() + " expected.", ee, e);
        // }
    }

    @Test
    public void testUpdateReadonlyProperties() throws Exception {
        // xlink:href xlink:title xlink:type
        // creation-date context xlink:href xlink:type xlink:title content-type
        // xlink:type xlink:href xlink:title
        // creator xlink:href xlink:title xlink:type
        // lock-status lock-owner status withdrawal-comment
        // current-version (number date version-status valid-status xlink:href
        // xlink:title xlink:type)
        // latest-version (number date xlink:href xlink:title xlink:type)
        // latest-revision (number date pid xlink:href xlink:title xlink:type)
        // content-model-specific

        String basePath = "/item/properties/";
        String testDateValue = "1970-01-01T01:00:00.000Z";
        String testIntegerValue = "-1";
        // TODO check if values are new
        // TODO attribute selection/substitution
        Node testItem;
        // testItem = substitute(getDocument(theItemXml), basePath +
        // "@xlink:href",
        // testStringValue);
        // try{
        // update(theItemId, toString(testItem, true));
        // }
        // catch(Exception e){
        // assertExceptionType(ae.getName() + " expected.", ae, e);
        // }

        // testItem = substitute(getDocument(theItemXml), basePath +
        // "@xlink:title",
        // testStringValue);
        // try{
        // update(theItemId, toString(testItem, true));
        // }
        // catch(Exception e){
        // assertExceptionType(ae.getName() + " expected.", ae, e);
        // }

        // testItem = substitute(getDocument(theItemXml), basePath +
        // "@xlink:type",
        // "extended");
        // try{
        // update(theItemId, toString(testItem, true));
        // }
        // catch(Exception e){
        // assertExceptionType(ae.getName() + " expected.", ae, e);
        // }

        Document document = EscidocAbstractTest.getDocument(theItemXml);
        String xml = null;
        testItem = substitute(document, basePath + "creation-date", "1970-01-01T01:00:00.000Z");
        try {
            xml = update(theItemId, toString(testItem, true));
        }
        catch (final Exception e) {
            fail("No exception after update creation-date expected.");
        }

        // lock-status
        document = EscidocAbstractTest.getDocument(xml);
        testItem = substitute(document, basePath + "lock-status", "locked");
        try {
            xml = update(theItemId, toString(testItem, true));
        }
        catch (final Exception e) {
            fail("No exception after update lock-status expected.");
        }

        // lock-owner // TODO test with locked item

        // status
        document = EscidocAbstractTest.getDocument(xml);
        testItem = substitute(document, basePath + "public-status", "withdrawn");
        try {
            xml = update(theItemId, toString(testItem, true));
        }
        catch (final Exception e) {
            fail("No exception after update public-status expected.");
        }

        // withdrawal-comment; can not be updated in (non-)withdrawn item

        // current-version (xlink:href
        // xlink:title xlink:type)
        String basePathCV = basePath + "version/";
        // number
        document = EscidocAbstractTest.getDocument(xml);
        testItem = substitute(document, basePathCV + "number", testIntegerValue);
        try {
            xml = update(theItemId, toString(testItem, true));
        }
        catch (final Exception e) {
            fail("No exception after update version number expected.");
        }

        // date
        document = EscidocAbstractTest.getDocument(xml);
        testItem = substitute(document, basePathCV + "date", testDateValue);
        try {
            xml = update(theItemId, toString(testItem, true));
        }
        catch (final Exception e) {
            fail("No exception after update date expected.");
        }

        // version-status
        document = EscidocAbstractTest.getDocument(xml);
        testItem = substitute(document, basePathCV + "status", "withdrawn");
        try {
            xml = update(theItemId, toString(testItem, true));
        }
        catch (final Exception e) {
            fail("No exception after update version status expected.");
        }

        // valid-status REMOVED
        // document = EscidocAbstractTest.getDocument(xml);
        // //testItem = substitute(document, basePathCV + "valid-status",
        // "invalid");
        // try {
        // xml = update(theItemId, toString(testItem, true));
        // }
        // catch (final Exception e) {
        // fail("No exception after update valid-status expected.");
        // }

        // latest-version (number date xlink:href xlink:title xlink:type)
        // number
        document = EscidocAbstractTest.getDocument(xml);
        String basePathLV = basePath + "/latest-version/";
        testItem = substitute(document, basePathLV + "number", testIntegerValue);
        try {
            xml = update(theItemId, toString(testItem, true));
        }
        catch (final Exception e) {
            fail("No exception after update number expected.");
        }

        // date
        document = EscidocAbstractTest.getDocument(xml);
        testItem = substitute(document, basePathLV + "date", testDateValue);
        try {
            xml = update(theItemId, toString(testItem, true));
        }
        catch (final Exception e) {
            fail("No exception after update date expected.");
        }

        // latest-revision (number date pid xlink:href xlink:title xlink:type)

    }

    /**
     * Test successfully updating item with two existing relations. One new relation will be added while update, one
     * existing relation will be deleted while update.
     */
    @Test
    public void testRelationsUpdate() throws Exception {
        String itemXml1 =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));
        String itemXml2 =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));
        String itemNew =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));

        String createdItemId1 = getObjidValue(EscidocAbstractTest.getDocument(itemXml1));
        String createdItemId2 = getObjidValue(EscidocAbstractTest.getDocument(itemXml2));
        String createdItemIdToAdd = getObjidValue(EscidocAbstractTest.getDocument(itemNew));

        String hrefToAdd = "/ir/item/" + createdItemIdToAdd;
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
        NodeList relations = selectNodeList(xmlItemWithoutComponents, "/item/relations/relation");
        String itemWithoutComponents = toString(xmlItemWithoutComponents, true);
        String xml = create(itemWithoutComponents);
        String createdItemId3 = getObjidValue(EscidocAbstractTest.getDocument(xml));

        Document item = EscidocAbstractTest.getDocument(xml);

        // check for a second relation, getting the href/objid
        String targetRelationToLeave = selectSingleNode(item, "/item/relations/relation[2]/@href").getTextContent();

        // href/objid of the first relation and replacing it by href/objid of
        // third one
        Node targetHref = null;
        Node targetObjectId = null;
        String targetRelationToAdd = null;
        String targetRelationToRemove = null;
        targetHref = selectSingleNode(item, "/item/relations/relation[1]/@href");
        targetRelationToRemove = targetHref.getTextContent();
        targetHref.setNodeValue(hrefToAdd);
        targetRelationToAdd = hrefToAdd;

        // update with first relation changed to third one
        String updatedXml = update(createdItemId3, toString(item, true));
        Document updatedItem = EscidocAbstractTest.getDocument(updatedXml);

        // check response from update
        NodeList relationsAfterUpdate = selectNodeList(updatedItem, "/item/relations/relation");

        // href/objid of relations after update
        String targetNew1 = selectSingleNode(updatedItem, "/item/relations/relation[1]/@href").getTextContent();
        String targetNew2 = selectSingleNode(updatedItem, "/item/relations/relation[2]/@href").getTextContent();

        assertXmlValidItem(xml);
        // assert number of relations
        assertEquals("Number of relations is wrong ", relations.getLength(), relationsAfterUpdate.getLength());

        // assert added relation is there
        if (targetNew2.equals(targetRelationToLeave)) {
            assertEquals("Relation target Id is wrong ", targetNew1, targetRelationToAdd);
        }
        else {
            assertEquals("Relation target Id is wrong ", targetNew1, targetRelationToLeave);
        }

        // assert first relation is no longer there
        Node removedNode =
            selectSingleNode(updatedItem, "/item/relations/relation[@* = '" + targetRelationToRemove + "']");
        assertNull(removedNode);
        // assert third relation is there
        selectSingleNodeAsserted(updatedItem, "/item/relations/relation[@* = '" + targetRelationToAdd + "']");
        // assert second relation is still there
        selectSingleNodeAsserted(updatedItem, "/item/relations/relation[@* = '" + targetRelationToLeave + "']");

        // check response from retrieve
        xml = retrieve(createdItemId3);
        updatedItem = getDocument(xml);

        relationsAfterUpdate = selectNodeList(updatedItem, "/item/relations/relation");

        // href/objid of relations after update
        targetNew1 = selectSingleNode(updatedItem, "/item/relations/relation[1]/@href").getTextContent();
        ;
        targetNew2 = selectSingleNode(updatedItem, "/item/relations/relation[2]/@href").getTextContent();

        assertXmlValidItem(xml);
        // assert number of relations
        assertEquals("Number of relations is wrong ", relations.getLength(), relationsAfterUpdate.getLength());

        // assert ???
        if (targetNew2.equals(targetRelationToLeave)) {
            assertEquals("Relation target Id is wrong ", targetNew1, targetRelationToAdd);
        }
        else {
            assertEquals("Relation target Id is wrong ", targetNew1, targetRelationToLeave);
        }

        // assert first relation is no longer there
        removedNode = selectSingleNode(updatedItem, "/item/relations/relation[@* = '" + targetRelationToRemove + "']");
        assertNull(removedNode);
        // assert third relation is there
        selectSingleNodeAsserted(updatedItem, "/item/relations/relation[@* = '" + targetRelationToAdd + "']");
        // assert second relation is still there
        selectSingleNodeAsserted(updatedItem, "/item/relations/relation[@* = '" + targetRelationToLeave + "']");

    }

    /**
     * Test successfully updating item with two relations removed.
     */
    @Test
    public void testRelationsRemoveUpdate() throws Exception {
        String relationTarget1 =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));
        String relationTarget2 =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));

        String relationTargetId1 = getObjidValue(EscidocAbstractTest.getDocument(relationTarget1));
        String relationTargetId2 = getObjidValue(EscidocAbstractTest.getDocument(relationTarget2));
        String relationTargetHref1 = "/ir/item/" + relationTargetId1;
        String relationTargetHref2 = "/ir/item/" + relationTargetId2;

        String itemXmlTemplate =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_createWithRelations.xml");

        // replace either placeholders for objid or for href
        itemXmlTemplate = itemXmlTemplate.replaceAll("##ITEM_ID1##", relationTargetId1);
        itemXmlTemplate = itemXmlTemplate.replaceAll("##ITEM_ID2##", relationTargetId2);
        itemXmlTemplate = itemXmlTemplate.replaceAll("##ITEM_HREF1##", relationTargetHref1);
        itemXmlTemplate = itemXmlTemplate.replaceAll("##ITEM_HREF2##", relationTargetHref2);

        // remove components
        Document itemForCreateWithRelations = EscidocAbstractTest.getDocument(itemXmlTemplate);
        Node xmlItemWithoutComponents = deleteElement(itemForCreateWithRelations, "/item/components");

        // save relations
        NodeList relations = selectNodeList(xmlItemWithoutComponents, "/item/relations/relation");

        // create item with two relations
        String itemWithoutComponents = toString(xmlItemWithoutComponents, true);
        String xml = create(itemWithoutComponents);

        Document item = EscidocAbstractTest.getDocument(xml);
        String createdItemId = getObjidValue(item);

        // check for two relations
        selectSingleNodeAsserted(item, "/item/relations/relation[2]");
        selectSingleNodeAsserted(getDocument(retrieve(createdItemId)), "/item/relations/relation[2]");

        // remove relations and update
        Node itemWithoutRelations = deleteElement(item, "/item/relations");
        String itemWithoutRelationsXml = toString(itemWithoutRelations, false);
        xml = update(createdItemId, itemWithoutRelationsXml);
        Document updatedItem = getDocument(xml);

        // check for no relation
        Node relation = selectSingleNode(updatedItem, "/item/relations/relation[1]");
        assertNull("Found unexpected relation after update.", relation);
        relation = selectSingleNode(getDocument(retrieve(getObjidValue(updatedItem))), "/item/relations/relation[1]");
        assertNull("Found unexpected relation in retrieve after update.", relation);

    }

    /**
     * Test declining updating of an item with a new relation, which has a non existing predicate.
     */
    @Test
    public void testRelationsUpdateWithWrongPredicate() throws Exception {
        String itemXml1 =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));
        String itemXml2 =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));

        String createdItemId1 = getObjidValue(EscidocAbstractTest.getDocument(itemXml1));
        String createdItemId2 = getObjidValue(EscidocAbstractTest.getDocument(itemXml2));

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
        String xml = create(itemWithoutComponents);
        Document item = EscidocAbstractTest.getDocument(xml);
        String createdItemId3 = getObjidValue(item);

        Node predicate = selectSingleNode(item, "/item/relations/relation[1]/@predicate");
        predicate.setNodeValue("bla");

        try {
            update(createdItemId3, toString(item, true));
            fail("No exception occured on added an relation with non " + "existing predicate to the item");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("RelationPredicateNotFoundException.",
                RelationPredicateNotFoundException.class, e);
        }
    }

    /**
     * Test declining updating of an item with a new relation, which has a non existing target.
     */
    @Test
    public void testRelationsUpdateWithNonExistingTarget() throws Exception {
        String itemXml1 =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));
        String itemXml2 =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));

        String createdItemId1 = getObjidValue(EscidocAbstractTest.getDocument(itemXml1));
        String createdItemId2 = getObjidValue(EscidocAbstractTest.getDocument(itemXml2));

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
        String xml = create(itemWithoutComponents);

        Document item = EscidocAbstractTest.getDocument(xml);
        String createdItemId3 = getObjidValue(item);

        Node target = selectSingleNode(item, "/item/relations/relation[1]/@href");
        target.setNodeValue("bla");

        try {
            update(createdItemId3, toString(item, true));
            fail("No exception occured on added an relation with non existing target " + "to the item");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("ReferencedResourceNotFoundException.",
                ReferencedResourceNotFoundException.class, e);
        }
    }

    /**
     * Test declining updating of an item which relation id attributes are not equal
     */
    @Test
    public void testRelationsUpdateWithWrongReferenceAttributes() throws Exception {
        String itemXml1 =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));
        String itemXml2 =
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml"));

        String createdItemId1 = getObjidValue(EscidocAbstractTest.getDocument(itemXml1));
        String createdItemId2 = getObjidValue(EscidocAbstractTest.getDocument(itemXml2));

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
        String xml = create(itemWithoutComponents);

        Document item = EscidocAbstractTest.getDocument(xml);
        String createdItemId3 = getObjidValue(item);

        Node targetId = selectSingleNode(item, "/item/relations/relation[1]/@href");

        targetId.setNodeValue("bla");

        try {
            update(createdItemId3, toString(item, true));
            fail("No exception expected on added an relation with unequal ids " + "attributes to the item");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("ReferencedResourceNotFoundException.",
                ReferencedResourceNotFoundException.class, e);
        }
    }

    /**
     * Test successfully deleting of one optional md-record from an item while update.
     */
    @Test
    public void testDeleteMdrecordWhileUpdate() throws Exception {
        Document xmlItem =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_2_md_records_for_create.xml");
        NodeList mdrecords = selectNodeList(xmlItem, "/item/md-records/md-record");
        Node xmlItemWithoutComponents = deleteElement(xmlItem, "/item/components");

        String itemWithoutComponents = toString(xmlItemWithoutComponents, true);
        String createdXml = create(itemWithoutComponents);

        assertXmlValidItem(createdXml);
        Document createdDocument = EscidocAbstractTest.getDocument(createdXml);
        String createdItemId = getObjidValue(createdDocument);
        NodeList mdrecordsAfterCreate = selectNodeList(createdDocument, "/item/md-records/md-record");
        assertEquals(mdrecords.getLength(), mdrecordsAfterCreate.getLength());
        Node itemWithOneMdRecord = deleteElement(createdDocument, "/item/md-records/md-record[@name = 'name1']");
        String itemWithOneMdRecordXml = toString(itemWithOneMdRecord, true);

        String updatedItemWith1MdRecord = update(createdItemId, itemWithOneMdRecordXml);
        Document udatedDocument = EscidocAbstractTest.getDocument(updatedItemWith1MdRecord);
        NodeList mdrecordsAfterUpdate = selectNodeList(udatedDocument, "/item/md-records/md-record");
        assertEquals((mdrecordsAfterCreate.getLength() - 1), mdrecordsAfterUpdate.getLength());

    }

    /**
     * Test successfully updating an item. The first update deletes one optional md-record, the second update add the
     * md-record with the same value of the attribute 'name' again.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAddExistingMdrecordWhileUpdate() throws Exception {
        Document xmlItem =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_2_md_records_for_create.xml");
        NodeList mdrecords = selectNodeList(xmlItem, "/item/md-records/md-record");
        Node xmlItemWithoutComponents = deleteElement(xmlItem, "/item/components");

        String itemWithoutComponents = toString(xmlItemWithoutComponents, true);
        String createdXml = create(itemWithoutComponents);

        assertXmlValidItem(createdXml);
        Document createdDocument = EscidocAbstractTest.getDocument(createdXml);
        String createdItemId = getObjidValue(createdDocument);
        NodeList mdrecordsAfterCreate = selectNodeList(createdDocument, "/item/md-records/md-record");
        assertEquals(mdrecords.getLength(), mdrecordsAfterCreate.getLength());
        Node itemWithOneMdRecord = deleteElement(createdDocument, "/item/md-records/md-record[@name = 'name1']");
        String itemWithOneMdRecordXml = toString(itemWithOneMdRecord, true);

        String updatedItemWith1MdRecord = update(createdItemId, itemWithOneMdRecordXml);
        Document updatedDocument = EscidocAbstractTest.getDocument(updatedItemWith1MdRecord);
        NodeList mdrecordsAfterUpdate = selectNodeList(updatedDocument, "/item/md-records/md-record");
        assertEquals((mdrecordsAfterCreate.getLength() - 1), mdrecordsAfterUpdate.getLength());

        Element mdRecord =
            updatedDocument.createElementNS("http://www.escidoc.de/schemas/metadatarecords/0.3",
                "escidocMetadataRecords:md-record");
        mdRecord.setAttribute("name", "name1");
        mdRecord.setAttribute("schema", "bla");
        Element mdRecordContent = updatedDocument.createElement("bla");
        mdRecord.appendChild(mdRecordContent);
        selectSingleNode(updatedDocument, "/item/md-records").appendChild(mdRecord);
        String itemWith2MdRecordXml = toString(updatedDocument, true);
        String dobleUpdated = update(createdItemId, itemWith2MdRecordXml);
        Document doubleUpdatedDocument = EscidocAbstractTest.getDocument(dobleUpdated);
        NodeList mdrecordsAfterSecondUpdate = selectNodeList(doubleUpdatedDocument, "/item/md-records/md-record");
        assertEquals(mdrecordsAfterUpdate.getLength() + 1, mdrecordsAfterSecondUpdate.getLength());

    }

    /**
     * Test successfully adding of a new optional md-record to an item.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAddNewMdrecordWhileUpdate() throws Exception {
        Document xmlItem =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        NodeList mdrecords = selectNodeList(xmlItem, "/item/md-records/md-record");
        Node xmlItemWithoutComponents = deleteElement(xmlItem, "/item/components");

        String itemWithoutComponents = toString(xmlItemWithoutComponents, true);
        String createdXml = create(itemWithoutComponents);

        assertXmlValidItem(createdXml);
        Document createdDocument = EscidocAbstractTest.getDocument(createdXml);
        String createdItemId = getObjidValue(createdDocument);
        NodeList mdrecordsAfterCreate = selectNodeList(createdDocument, "/item/md-records/md-record");
        assertEquals(mdrecords.getLength(), mdrecordsAfterCreate.getLength());

        Element mdRecord =
            createdDocument.createElementNS("http://www.escidoc.de/schemas/metadatarecords/0.3",
                "escidocMetadataRecords:md-record");
        mdRecord.setAttribute("name", "name1");
        mdRecord.setAttribute("schema", "bla");
        Element mdRecordContent = createdDocument.createElement("bla");
        mdRecord.appendChild(mdRecordContent);
        selectSingleNode(createdDocument, "/item/md-records").appendChild(mdRecord);
        String itemWith2MdRecordXml = toString(createdDocument, true);
        String updated = update(createdItemId, itemWith2MdRecordXml);
        Document updatedDocument = EscidocAbstractTest.getDocument(updated);
        NodeList mdrecordsAfterUpdate = selectNodeList(updatedDocument, "/item/md-records/md-record");
        assertEquals(mdrecordsAfterUpdate.getLength() - 1, mdrecordsAfterCreate.getLength());

    }

    /**
     * Test successfully deleting of one optional component md-record while update.
     */
    @Test
    public void testDeleteComponentMdRecordWhileUpdate() throws Exception {
        Document xmlItem =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create_2_Component_Md-Records.xml");
        NodeList mdrecords = selectNodeList(xmlItem, "/item/components/component/md-records/md-record");

        String createdXml = create(toString(xmlItem, false));

        assertXmlValidItem(createdXml);
        Document createdDocument = EscidocAbstractTest.getDocument(createdXml);
        String createdItemId = getObjidValue(createdDocument);
        NodeList mdrecordsAfterCreate =
            selectNodeList(createdDocument, "/item/components/component/md-records/md-record");
        assertEquals(mdrecords.getLength(), mdrecordsAfterCreate.getLength());
        Node itemWithOneComponentMdRecord =
            deleteElement(createdDocument, "/item/components/component[1]/md-records/md-record[@name = 'md2']");
        String itemWithOneComponentMdRecordXml = toString(itemWithOneComponentMdRecord, true);

        String updatedItemWith1MdRecord = update(createdItemId, itemWithOneComponentMdRecordXml);
        Document udatedDocument = EscidocAbstractTest.getDocument(updatedItemWith1MdRecord);
        NodeList mdrecordsAfterUpdate =
            selectNodeList(udatedDocument, "/item/components/component/md-records/md-record");
        assertEquals((mdrecordsAfterCreate.getLength() - 1), mdrecordsAfterUpdate.getLength());

    }

    /**
     * Test successfully deleting of component "escidoc" md-record while update.
     */
    @Test
    public void testDeleteComponentEscidocMdRecordWhileUpdate() throws Exception {
        String xmlItem =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create_2_Component_Md-Records.xml");

        String createdXml = create(xmlItem);

        assertXmlValidItem(createdXml);
        Document createdDocument = getDocument(createdXml);
        String createdItemId = getObjidValue(createdDocument);
        String componentId = null;
        String mdRecordPath = null;
        String componentHrefValue =
            selectSingleNode(createdDocument, "/item/components/component[1]/@href").getNodeValue();
        componentId = getObjidFromHref(componentHrefValue);
        mdRecordPath =
            "/item/components/component[@href='" + componentHrefValue + "']/md-records/md-record[@name = 'escidoc']";
        Node itemWithoutComponentEscidocMdRecord = deleteElement(createdDocument, mdRecordPath);
        String itemWithoutComponentEscidocMdRecordXml = toString(itemWithoutComponentEscidocMdRecord, true);
        String updatedXml = update(createdItemId, itemWithoutComponentEscidocMdRecordXml);
        Document updatedDocument = getDocument(updatedXml);
        Node notExistedMdRecord = selectSingleNode(updatedDocument, mdRecordPath);
        assertNull("Escidoc md-record must be deleted", notExistedMdRecord);

    }

    /**
     * Test successfully updating an item. The first update deletes one optional component md-record, the second update
     * add the md-record with the same value of the attribute 'name' to this component again.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAddExistingComponentMdrecordWhileUpdate() throws Exception {
        Document xmlItem =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create_2_Component_Md-Records.xml");
        NodeList mdrecords = selectNodeList(xmlItem, "/item/components/component/md-records/md-record");

        String createdXml = create(toString(xmlItem, false));

        assertXmlValidItem(createdXml);
        Document createdDocument = EscidocAbstractTest.getDocument(createdXml);
        String createdItemId = getObjidValue(createdDocument);
        NodeList mdrecordsAfterCreate =
            selectNodeList(createdDocument, "/item/components/component/md-records/md-record");
        assertEquals(mdrecords.getLength(), mdrecordsAfterCreate.getLength());
        Node itemWithOneComponentMdRecord =
            deleteElement(createdDocument, "/item/components/component/md-records/md-record[@name = 'md2']");
        String itemWithOneComponentMdRecordXml = toString(itemWithOneComponentMdRecord, true);

        String updatedItemWith1MdRecord = update(createdItemId, itemWithOneComponentMdRecordXml);
        Document udatedDocument = EscidocAbstractTest.getDocument(updatedItemWith1MdRecord);
        NodeList mdrecordsAfterUpdate =
            selectNodeList(udatedDocument, "/item/components/component/md-records/md-record");
        assertEquals((mdrecordsAfterCreate.getLength() - 1), mdrecordsAfterUpdate.getLength());

        Element mdRecord =
            udatedDocument.createElementNS("http://www.escidoc.de/schemas/metadatarecords/0.3",
                "escidocMetadataRecords:md-record");
        mdRecord.setAttribute("name", "md1");
        mdRecord.setAttribute("schema", "bla");
        Element mdRecordContent = udatedDocument.createElement("bla");
        mdRecord.appendChild(mdRecordContent);
        selectSingleNode(udatedDocument, "/item/components/component/md-records").appendChild(mdRecord);
        String itemWith2ComponentMdRecordXml = toString(udatedDocument, true);
        String dobleUpdated = update(createdItemId, itemWith2ComponentMdRecordXml);
        Document doubleUpdatedDocument = EscidocAbstractTest.getDocument(dobleUpdated);
        NodeList mdrecordsAfterSecondUpdate =
            selectNodeList(doubleUpdatedDocument, "/item/components/component/md-records/md-record");
        assertEquals(mdrecordsAfterUpdate.getLength() + 1, mdrecordsAfterSecondUpdate.getLength());
    }

    /**
     * Test successfully adding of a new optional md-record to a component.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAddNewMdrecordToComponentWhileUpdate() throws Exception {
        Document xmlItem =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");
        NodeList mdrecords = selectNodeList(xmlItem, "/item/components/component/md-records/md-record");

        String createdXml = create(toString(xmlItem, false));

        assertXmlValidItem(createdXml);
        Document createdDocument = EscidocAbstractTest.getDocument(createdXml);
        String createdItemId = getObjidValue(createdDocument);

        NodeList mdrecordsAfterCreate =
            selectNodeList(createdDocument, "/item/components/component/md-records/md-record");
        assertEquals(mdrecords.getLength(), mdrecordsAfterCreate.getLength());
        Element mdRecord =
            createdDocument.createElementNS("http://www.escidoc.de/schemas/metadatarecords/0.4",
                "escidocMetadataRecords:md-record");
        mdRecord.setAttribute("name", "name1");
        mdRecord.setAttribute("schema", "bla");
        Element mdRecordContent = createdDocument.createElement("bla");
        mdRecord.appendChild(mdRecordContent);
        // Element mdRecords =
        // createdDocument.createElementNS(
        // "http://www.escidoc.de/schemas/metadatarecords/0.4",
        // "escidocMetadataRecords:md-records");
        // mdRecords.appendChild(mdRecord);
        selectSingleNode(createdDocument, "/item/components/component[1]/md-records").appendChild(mdRecord);
        // FIXME: DOM-API does not create a new element with a namespace
        // declaration, therefore
        // added a namespace declaration to 'md-records' element by a
        // String-replace
        String itemWithComponentMdRecordXml = toString(createdDocument, true);
        String updated = update(createdItemId, itemWithComponentMdRecordXml);
        Document updatedDocument = getDocument(updated);
        NodeList mdrecordsAfterUpdate =
            selectNodeList(updatedDocument, "/item/components/component/md-records/md-record");
        assertEquals(mdrecordsAfterUpdate.getLength() - 1, mdrecordsAfterCreate.getLength());

    }

    /**
     * Tests successfully updating 'escidoc' metadata record of an component. Update of escidoc md-record should cause
     * the update of a component property "file-name"
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testUpdateComponentEscidocMdRecord() throws Exception {

        // changed from retrieve/update metadata to retrieve/update with
        // metadata (2007-01-25, FRS)
        Document item = getDocument(theItemXml, true);
        String fileNamePath = null;
        String mdXPath = null;
        String mdRecordName = "escidoc";
        String href = selectSingleNode(item, "item/components/component[1]/@href").getNodeValue();
        String componentId = getObjidFromHref(href);
        mdXPath =
            "/item/components/component[@href='" + href + "']/md-records/md-record[@name='" + mdRecordName
                + "']/publication/title";
        fileNamePath = "/item/components/component[@href='" + href + "']/properties/file-name";
        String oldTitleValue = selectSingleNode(item, mdXPath).getTextContent();
        String newTitle = "new title";
        String oldFileName = selectSingleNode(item, fileNamePath).getTextContent();

        assertEquals("file name and md-record.title are not equal", oldTitleValue, oldFileName);
        // String lastModificationDateXPath = "/item/@last-modification-date";

        Document newItem = (Document) substitute(item, mdXPath, newTitle);

        final String updatedXml = update(theItemId, toString(newItem, false));
        assertXmlValidItem(updatedXml);
        Document updateDocument = EscidocAbstractTest.getDocument(updatedXml);

        String newTitleValue = selectSingleNode(updateDocument, mdXPath).getTextContent();

        String newFileName = selectSingleNode(updateDocument, fileNamePath).getTextContent();

        assertEquals("file name and md-record.title are not equal", newTitleValue, newFileName);

    }

    /**
     * Tests successfully updating 'escidoc' metadata record of an component. Update of escidoc md-record should cause
     * the update of a component property "file-name"
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testUpdateComponentEscidocMdRecord02() throws Exception {

        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_1_component.xml");
        String itemXml = create(xmlData);

        Document item = getDocument(itemXml, true);
        String itemId = getObjidValue(itemXml);
        String mdRecordName = "escidoc";

        String href = selectSingleNode(item, "item/components/component[1]/@href").getNodeValue();
        String componentId = getObjidFromHref(href);
        String mdXPath =
            "/item/components/component[@href='" + href + "']/md-records/md-record[@name='" + mdRecordName
                + "']/publication/title";
        String fileNamePath = "/item/components/component[@href='" + href + "']/properties/file-name";

        String oldTitleValue = selectSingleNode(item, mdXPath).getTextContent().trim();
        String oldFileName = selectSingleNode(item, fileNamePath).getTextContent().trim();

        assertEquals("file name and md-record.title are not equal", oldTitleValue, oldFileName);
        // String lastModificationDateXPath = "/item/@last-modification-date";

        // Test update title
        String newTitle = "new title";
        Document newItem = (Document) substitute(item, mdXPath, newTitle);

        String temp = toString(newItem, false);
        final String updatedXml = update(itemId, temp);
        assertXmlValidItem(updatedXml);
        Document updateDocument = EscidocAbstractTest.getDocument(updatedXml);

        String newTitleValue = selectSingleNode(updateDocument, mdXPath).getTextContent();

        String newFileName = selectSingleNode(updateDocument, fileNamePath).getTextContent();

        assertEquals("file name and md-record.title are not equal", newTitleValue.trim(), newFileName);
    }

    /**
     * Tests to add a md-record to a component which had no md-record before.
     * 
     * @see INFR-1151
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testUpdateComponentEscidocMdRecord03() throws Exception {

        Document itemDoc =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_1_component.xml");

        // remove md-records from component
        Node delMdRecord = selectSingleNode(itemDoc, "/item/components/component/md-records");
        delMdRecord.getParentNode().removeChild(delMdRecord);

        itemDoc.normalize();

        String tmpl = toString(itemDoc, false);
        String itemXml = create(tmpl);

        // add md-record to the component
        Document itemDoc2 = EscidocAbstractTest.getDocument(itemXml);
        String itemId = getObjidValue(itemDoc2);

        Element mdRecord =
            itemDoc2.createElementNS("http://www.escidoc.de/schemas/metadatarecords/0.5",
                "escidocMetadataRecords:md-record");
        mdRecord.setAttribute("name", "name1");
        mdRecord.setAttribute("schema", "bla");
        Element mdRecordContent = itemDoc2.createElement("bla");
        mdRecord.appendChild(mdRecordContent);

        Element mdRecords =
            itemDoc2.createElementNS("http://www.escidoc.de/schemas/metadatarecords/0.5",
                "escidocMetadataRecords:md-records");
        mdRecords.appendChild(mdRecord);
        selectSingleNode(itemDoc2, "/item/components/component[1]").appendChild(mdRecords);
        String newXml = toString(itemDoc2, true);

        String itemXmlV2 = update(itemId, newXml);
        assertXmlValidItem(itemXmlV2);

        // assert values
        Document itemDoc3 = getDocument(itemXmlV2, true);
        String mdXPath = "/item/components/component[1]/md-records/md-record[@name='name1']/bla";

        assertNotNull("missing node", selectSingleNode(itemDoc3, mdXPath));
    }

    /**
     * Test update without changes does not create version.
     */
    @Test
    public void testUpdateItemDoNotModified() throws Exception {

        Document itemDoc = getDocument(this.theItemXml);
        String versionNumber =
            selectSingleNodeAsserted(itemDoc, "/item/properties/version/number/text()").getNodeValue();
        String modDate = selectSingleNodeAsserted(itemDoc, "/item/@last-modification-date").getNodeValue();

        String updatedItem = update(this.theItemId, this.theItemXml);

        // version number should be unchanged
        assertEquals("version number", versionNumber, selectSingleNode(getDocument(updatedItem),
            "/item/properties/version/number/text()").getNodeValue());
        // last modification timestamp must be unchanged
        assertEquals("last modification date", modDate, selectSingleNode(getDocument(updatedItem),
            "/item/@last-modification-date").getNodeValue());

        delete(this.theItemId);
    }

    /**
     * Test succesfully update metadata by just changing the namespace but NOT the prefix bound to that namespace.
     */
    @Test
    public void testUpdateMdRecordNamespace() throws Exception {

        Document itemDoc = getDocument(this.theItemXml);
        String versionNumber =
            selectSingleNodeAsserted(itemDoc, "/item/properties/version/number/text()").getNodeValue();
        String modDate = selectSingleNodeAsserted(itemDoc, "/item/@last-modification-date").getNodeValue();

        String itemDocString = toString(itemDoc, false);
        itemDocString =
            itemDocString.replace("xmlns=\"http://escidoc.mpg.de/metadataprofile/schema/0.1/\"",
                "xmlns=\"http://just.for.test/namespace\"");

        String updatedItem = update(this.theItemId, itemDocString);

        // version number should be changed
        assertNotEquals("version number should be changed updating md-record namespace", versionNumber,
            selectSingleNode(getDocument(updatedItem), "/item/properties/version/number/text()").getNodeValue());
        // last modification timestamp must be changed
        assertNotEquals("last modification date should be changed updating md-record namespace", modDate,
            selectSingleNode(getDocument(updatedItem), "/item/@last-modification-date").getNodeValue());

        delete(this.theItemId);
    }

    /**
     * Test if an update of an Item with one of it older version is possible.
     * 
     * See issue INFR-951
     * 
     * @throws Exception
     *             If update is not handled like expected.
     */
    @Test
    public void testRestoreAndAccessOlderVersions() throws Exception {

        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_1_component.xml");

        final String item1Xml = create(xmlData);
        final String itemId = getObjidValue(item1Xml);

        // create new versions by adding further components
        final String item2Xml = update(itemId, addComponent(item1Xml));
        final String item3Xml = update(itemId, addComponent(item2Xml));

        // update
        String lmd = getLastModificationDateValue(getDocument(item3Xml));
        final String restoreItem1Xml =
            update(itemId, toString(substitute(getDocument(item1Xml), "/item/@last-modification-date", lmd), false));
        lmd = getLastModificationDateValue(getDocument(restoreItem1Xml));

        // update last-modification-date
        // remove objid/href from components
        Document item3Doc = getDocument(item3Xml);
        substitute(item3Doc, "/item/@last-modification-date", lmd);
        deleteNodes(item3Doc, "/item/components/component/@objid");
        deleteNodes(item3Doc, "/item/components/component/@href");

        final String restoreItem3Xml = update(itemId, toString(item3Doc, false));
    }

    /**
     * Obatins Component Ids from Item.
     * 
     * @return Vector with objids of Component.
     */
    private Vector<String> obtainComponentIds(Document curItem) throws TransformerException {
        String xPathCompId = "/item/components/component/@href";
        NodeList componentIds = selectNodeList(curItem, xPathCompId);

        Vector<String> compIds = new Vector<String>();
        for (int i = 0; i < componentIds.getLength(); i++) {
            Node objRef = componentIds.item(i);
            String ref = objRef.getTextContent();
            ref = getObjidFromHref(ref);
            compIds.add(ref);
        }

        return compIds;
    }
}
