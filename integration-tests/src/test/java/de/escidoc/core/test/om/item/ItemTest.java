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
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.resources.ResourceProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the item resource.
 * 
 * @author MSC
 * 
 */
@RunWith(value = Parameterized.class)
public class ItemTest extends ItemTestBase {

    private String theItemId = null;

    /**
     * @param transport
     *            The transport identifier.
     */
    public ItemTest(final int transport) {
        super(transport);
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testIssue575() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_issue575.xml");
        String item = toString(xmlItem, true);

        item = create(item);
        assertXmlValidItem(item);
        Node node =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(item),
                "/item/components");
        assertNotNull("components container not available.", node);

        NodeList nodes =
            selectNodeList(EscidocRestSoapTestBase.getDocument(item),
                "/item/components/component");
        assertEquals("Found some components, but none expected.", 0, nodes
            .getLength());
        assertNotNull(node);
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testNatasaItemWithWhitespaces() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "ItemWithWhitespaces.xml");
        String item = toString(xmlItem, true);

        item = create(item);
        assertXmlValidItem(item);
        Node node =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(item),
                "/item/components");
        assertNotNull("components container not available.", node);

    }

    /**
     * Successfully create item and component with md-record "escidoc"
     * containing MODS. Issue 632.
     * 
     * @throws Exception
     */
    @Test
    public void testModsMd() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_MODS-MD.xml");
        String item = toString(xmlItem, true);

        item = create(item);
        assertXmlValidItem(item);
    }

    /**
     * Test content-model-specific is not need but can be added.
     * 
     * @throws Exception
     */
    @Test
    public void testContentModelSpecific() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_issue575.xml");
        String item =
            toString(deleteNodes(xmlItem,
                "/item/properties/content-model-specific"), true);

        // create item without c-m-s
        String itemId = null;
        try {
            item = create(item);
            itemId = getObjidValue(item);
        }
        catch (Exception e) {
            failException("No exception expected!", e);
        }
        assertXmlValidItem(item);
        Node node =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(item),
                "/item/properties/content-model-specific");
        assertNull("No element content-model-specific expected.", node);

        // add c-m-s
        item =
            item.replaceFirst("</escidocItem:properties",
                "<prop:content-model-specific/></escidocItem:properties");
        item = update(itemId, item);
        selectSingleNodeAsserted(getDocument(item),
            "/item/properties/content-model-specific");
        // insert into c-m-s
        item =
            item.replaceFirst("<prop:content-model-specific[^>]*>",
                "<prop:content-model-specific><nix></nix>");
        item = update(itemId, item);
        selectSingleNodeAsserted(getDocument(item),
            "/item/properties/content-model-specific/nix");
        // remove content from c-m-s
        item =
            toString(deleteNodes(getDocument(item),
                "/item/properties/content-model-specific/nix"), true);
        item = update(itemId, item);
        node =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(item),
                "/item/properties/content-model-specific/nix");
        assertNull("No element content-model-specific expected.", node);
        // remove c-m-s should not delete
        item =
            toString(deleteNodes(getDocument(item),
                "/item/properties/content-model-specific"), true);
        item = update(itemId, item);
        selectSingleNodeAsserted(getDocument(item),
            "/item/properties/content-model-specific");
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testZimPBItemCreate() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "item-001.xml");
        String item = toString(xmlItem, true);

        item = create(item);
        assertXmlValidItem(item);
        Node node =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(item),
                "/item/properties/pid");
        assertNotNull(node);

        Pattern PATTERN_OBJID_ATTRIBUTE = Pattern.compile("objid=\"([^\"]*)\"");
        Matcher m = PATTERN_OBJID_ATTRIBUTE.matcher(item);
        if (m.find()) {
            delete(m.group(1));
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testZimJMItemCreate() throws Exception {

        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "item-002.xml");
        String item = toString(xmlItem, true);
        // assertXmlValidItem(item);

        item = create(item);
        assertXmlValidItem(item);

        Pattern PATTERN_OBJID_ATTRIBUTE = Pattern.compile("objid=\"([^\"]*)\"");
        Matcher m = PATTERN_OBJID_ATTRIBUTE.matcher(item);
        if (m.find()) {
            delete(m.group(1));
        }
    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCreateUpdateZim2Item() throws Exception {

        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "zim2-create.xml");
        String item = toString(xmlItem, true);
        // assertXmlValidItem(item);

        item = create(item);
        assertXmlValidItem(item);

        String createdItemId = getIdFromRootElement(item);

        Document item4Update =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "zim2-updateTest.xml");
        String item4UpdateXml = toString(item4Update, true);

        item4UpdateXml = item4UpdateXml.replaceAll("##ITEMID##", createdItemId);
        item4UpdateXml =
            item4UpdateXml.replaceAll("##LASTMODIFICATIONDATE##",
                selectSingleNode(EscidocRestSoapTestBase.getDocument(item),
                    "/item/@last-modification-date").getNodeValue());
        item4UpdateXml =
            item4UpdateXml.replaceAll("##CREATIONDATE##", selectSingleNode(
                EscidocRestSoapTestBase.getDocument(item),
                "/item/properties/creation-date/text()").getNodeValue());
        // assertXmlValidItem(item4UpdateXml);
        String updatedItem = update(createdItemId, item4UpdateXml);
        assertXmlValidItem(updatedItem);

    }

    /**
     * 
     * @throws Exception
     */
    @Test
    public void testCreateUpdateIssue615() throws Exception {

        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "zim2-create.xml");
        String item = toString(xmlItem, true);
        // assertXmlValidItem(item);

        item = create(item);
        assertXmlValidItem(item);

        String createdItemId = getIdFromRootElement(item);
        String createTitle;
        if (getTransport() == Constants.TRANSPORT_REST) {
            createTitle =
                selectSingleNode(getDocument(item), "/item/@title")
                    .getNodeValue();
        }
        else {
            createTitle =
                selectSingleNode(getDocument(item),
                    "/item/md-records/md-record[@name='escidoc']//title/text()")
                    .getNodeValue();
        }

        Document item4Update =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "zim2-updateTest.xml");
        // remove all changes except title in metadata
        item4Update =
            (Document) deleteElement(item4Update,
                "/item/properties/content-model-specific/*");
        String item4UpdateXml = toString(item4Update, true);

        item4UpdateXml = item4UpdateXml.replaceAll("##ITEMID##", createdItemId);
        item4UpdateXml =
            item4UpdateXml.replaceAll("##LASTMODIFICATIONDATE##",
                selectSingleNode(EscidocRestSoapTestBase.getDocument(item),
                    "/item/@last-modification-date").getNodeValue());
        item4UpdateXml =
            item4UpdateXml.replaceAll("##CREATIONDATE##", selectSingleNode(
                EscidocRestSoapTestBase.getDocument(item),
                "/item/properties/creation-date/text()").getNodeValue());
        // assertXmlValidItem(item4UpdateXml);

        String updatedItem = update(createdItemId, item4UpdateXml);
        assertXmlValidItem(updatedItem);
        updatedItem = retrieve(createdItemId);
        assertXmlValidItem(updatedItem);
        String updateTitle;
        if (getTransport() == Constants.TRANSPORT_REST) {
            updateTitle =
                selectSingleNode(getDocument(updatedItem), "/item/@title")
                    .getNodeValue();
        }
        else {
            updateTitle =
                selectSingleNode(getDocument(updatedItem),
                    "/item/md-records/md-record[@name='escidoc']//title/text()")
                    .getNodeValue();
        }

        assertNotEquals("Title should be changed", createTitle, updateTitle);

    }

    /**
     * Test successfully creating content item.
     * 
     * @test.name CI with correct obligatory data
     * @test.id OM_CI_1-1
     * @test.input Item XML
     * @test.inputDescription Item XML with correct obligatory data (no Content
     *                        Components) obligatory data: <br/>
     *                        - Item title (item) <br/>
     *                        - Context (properties) <br/>
     *                        - Content Type (properties) <br/>
     *                        - Escidoc Internal Metadata Set <br/>
     *                        - title of Escidoc Internal Metadata Set <br/>
     *                        - name of Escidoc Internal Metadata Set <br/>
     *                        - ctx section (properties)
     * 
     * @test.expected Item XML now containing the parameters which are set by
     *                the system:
     * 
     * <br/>
     *                Content Item ID (item) <br/>
     *                item href (item) <br/>
     *                Timestamp of the latest modification in the system item) <br/>
     *                Timestamp of the creation in the system (item) <br/>
     *                Current Version status = pending (properties) <br/>
     *                Creator href (properties) <br/>
     *                Creator title (properties) <br/>
     *                Lock Status = unlocked (properties) <br/>
     *                Current Version Number = 1 (properties) <br/>
     *                Current Version Date = time stamp of creation of version 1
     *                (properties) <br/>
     *                Status of the Item = pending (properties) <br/>
     *                Latest version number = 1 (properties) <br/>
     *                Latest version date = Timestamp of the latest modification
     *                in the system (properties) <br/>
     *                EscidocResources "Snippet"
     * 
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi1a() throws Exception {

        // String fileName =
        // "src/java/de/escidoc/core/test/om/template/item/escidoc_item_198_for_create.xml";
        // String fileName =
        // "C:/workarea/projects/EscidocTest/src/java/de/escidoc/core/test/om/template/item/escidoc_item_198_for_create.xml";
        // File xmlNeu = new File(fileName);
        // File file = new File(
        // fileName);
        // InputStream is = new FileInputStream(file);
        // int b;
        // StringBuffer sb = new StringBuffer();
        // String xmlData = null;
        // while ((b = is.read()) != -1) {
        // sb = sb.append((char) b);
        // }
        // xmlData = sb.toString();
        // String test = getTemplateAsString(TEMPLATE_ITEM_PATH,
        // "escidoc_item_198_for_create.xml");

        // String xmlData = getTemplateAsString(TEMPLATE_ITEM_PATH,
        // "escidoc_item_198_for_create.xml");

        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");

        Node xmlItemWithoutComponents =
            deleteElement(xmlItem, "/item/components");
        String itemWithoutComponents = toString(xmlItemWithoutComponents, true);

        final String createdXml = create(itemWithoutComponents);

        assertXmlValidItem(createdXml);
        final Document createdDocument =
            EscidocRestSoapTestBase.getDocument(createdXml);

        this.theItemId = getIdFromRootElement(createdXml);

        String creatorId = null;
        if (getTransport() == Constants.TRANSPORT_SOAP) {
            Node itemObjiId =
                selectSingleNode(EscidocRestSoapTestBase
                    .getDocument(createdXml), "/item/@objid");
            this.theItemId = itemObjiId.getTextContent();
            Node creatorIdNode =
                selectSingleNode(EscidocRestSoapTestBase
                    .getDocument(createdXml),
                    "/item/properties/created-by/@objid");
            creatorId = creatorIdNode.getTextContent();
        }
        else {
            Pattern PATTERN_OBJID_ATTRIBUTE = null;
            PATTERN_OBJID_ATTRIBUTE = Pattern.compile(".*\\/([^\"\\/]*)");

            Node itemObjiId =
                selectSingleNode(EscidocRestSoapTestBase
                    .getDocument(createdXml), "/item/@href");

            Matcher m1 =
                PATTERN_OBJID_ATTRIBUTE.matcher(itemObjiId.getTextContent());
            if (m1.find()) {
                this.theItemId = m1.group(1);
            }

            Node creatorIdNode =
                selectSingleNode(EscidocRestSoapTestBase
                    .getDocument(createdXml),
                    "/item/properties/created-by/@href");
            Matcher m3 =
                PATTERN_OBJID_ATTRIBUTE.matcher(creatorIdNode.getTextContent());
            if (m3.find()) {
                creatorId = m3.group(1);
            }
        }
        if (getTransport() == Constants.TRANSPORT_REST) {
            assertXmlEquals("href value is wrong", createdDocument,
                "/item/@href", "/ir/item/" + theItemId);
            assertXmlEquals("href value is wrong", createdDocument,
                "/item/md-records/@href ", "/ir/item/" + theItemId
                    + "/md-records");
            assertXmlEquals("href value is wrong", createdDocument,
                "/item/md-records/md-record[1]/@href ", "/ir/item/" + theItemId
                    + "/md-records/md-record/escidoc");
            assertXmlEquals("href value is wrong", createdDocument,
                "/item/properties/@href ", "/ir/item/" + theItemId
                    + "/properties");
            assertXmlEquals("href value is wrong", createdDocument,
                "/item/md-records/md-record[1]/@href ", "/ir/item/" + theItemId
                    + "/md-records/md-record/escidoc");
            assertXmlEquals("href value is wrong", createdDocument,
                "/item/properties/@href ", "/ir/item/" + theItemId
                    + "/properties");
            Node itemTitle = selectSingleNode(createdDocument, "/item/@title");
            assertNotNull(itemTitle);
            assertFalse("item title is not set", itemTitle
                .getTextContent().equals(""));
            Node creatorTitle =
                selectSingleNode(createdDocument,
                    "/item/properties/created-by/@title");
            assertNotNull(creatorTitle.getTextContent());
            assertNotNull(creatorId);
        }

        Node modifiedDate =
            selectSingleNode(createdDocument, "/item/@last-modification-date");
        assertNotNull(modifiedDate);
        assertFalse("modified date is not set", modifiedDate
            .getTextContent().equals(""));
        assertXmlEquals("status value is wrong", createdDocument,
            "/item/properties/public-status", "pending");
        Node createdDate =
            selectSingleNode(createdDocument, "/item/properties/creation-date");
        assertNotNull(createdDate);
        assertFalse("created date is not set", createdDate
            .getTextContent().equals(""));
        assertXmlEquals("lock-status value is wrong", createdDocument,
            "/item/properties/lock-status", "unlocked");
        assertXmlEquals("current version number is wrong", createdDocument,
            "/item/properties/version/number", "1");
        assertXmlEquals("current version status is wrong", createdDocument,
            "/item/properties/version/status", "pending");
        // assertXmlEquals("current version valid-status is wrong",
        // createdDocument, "/item/properties/version/valid-status", "valid");
        assertXmlEquals("current version date is wrong", createdDocument,
            "/item/properties/version/date", modifiedDate.getTextContent());
        assertXmlEquals("latest version number is wrong", createdDocument,
            "/item/properties/latest-version/number", "1");
        assertXmlEquals("latest version date is wrong", createdDocument,
            "/item/properties/latest-version/date", modifiedDate
                .getTextContent());

        // assert metadata /publication/creator/person containing backslash
        // (see issue #286)
        assertXmlEquals("Metadata /publication/creator/person different",
            xmlItem, createdDocument, XPATH_ITEM_MD_RECORD
                + "/publication/creator/person/complete-name");

    }

    /**
     * Test successfully creating duplicate content item.
     * 
     * @test.name CI with correct obligatory data (Duplicate)
     * @test.id OM_CI_1-2
     * @test.input Item XML (no content components)
     * @test.inputDescription same Item XML input as in OM_CCI_1-1 (Test if
     *                        duplicate is saved with new Content Item ID)
     * @test.expected Item XML now containing the parameters which are set by
     *                the system: as described in OM_CI_1-1
     * 
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi1b() throws Exception {

        Document xmlItem1 =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        Node xmlItemWithoutComponents1 =
            deleteElement(xmlItem1, "/item/components");
        String itemWithoutComponents1 =
            toString(xmlItemWithoutComponents1, false);
        // create the first item
        String xml1 = create(itemWithoutComponents1);

        assertXmlValidItem(xml1);
        // Node itemObjiId1 = selectSingleNode(getDocument(xml1),
        // "/item/@objid");
        // String itemId1 = itemObjiId1.getTextContent();
        String itemId1 = getIdFromRootElement(xml1);

        // create the second item
        String xml2 = create(itemWithoutComponents1);
        assertXmlValidItem(xml2);

        String itemId2 = getIdFromRootElement(xml2);
        assertNotEquals("item id is not changed", itemId2, itemId1);
        delete(itemId1);
        delete(itemId2);
    }

    /**
     * Test declining creating an item without specifying the content model id,
     * using data provided for issue 365.
     * 
     * @test.name Create Item - Missing Content Model Id - Issue 365.
     * @test.id AA_CI-issue-365
     * @test.input Item XML representation with empty content model element
     *             (without href/objid).
     * @test.expected: MissingAttributeValueException
     * @test.status Implemented
     * @test.issue 
     *             http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=
     *             365
     * 
     * @throws Exception
     *             Thrown if anything fails.
     */
    @Test
    public void testOMCi_issue365() throws Exception {

        final Class<?> ec = XmlCorruptedException.class;

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        deleteElement(toBeCreatedDocument, XPATH_ITEM_CONTENT_MODEL);
        addAfter(toBeCreatedDocument, XPATH_ITEM_CONTEXT, createElementNode(
            toBeCreatedDocument, STRUCTURAL_RELATIONS_NS_URI, "srel",
            NAME_CONTENT_MODEL, null));

        String toBeCreatedXml = toString(toBeCreatedDocument, true);

        try {
            create(toBeCreatedXml);
            EscidocRestSoapTestBase.failMissingException(
                "Creating item with empty content-model element not declined.",
                ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Creating item with empty content-model element not declined"
                    + ", properly", ec, e);
        }

    }

    /**
     * Test declining creating content item with missing Context ID.
     * 
     * @test.name CI with missing obd (Context ID)
     * @test.id OM_CI_2-1
     * @test.input Item XML (no content components)
     * @test.inputDescription Item XML correct obligatory data (no List of
     *                        Content Components) as described in OM_CI_1-1
     *                        except Context ID
     * @test.expected Error message
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi2a() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        Node xmlItemWithoutComponents =
            deleteElement(xmlItem, "/item/components");

        Node xmlItemWithoutContextId;
        if (getTransport() == Constants.TRANSPORT_REST) {
            xmlItemWithoutContextId =
                substitute(xmlItemWithoutComponents,
                    "/item/properties/context/@href", "");
        }
        else {
            xmlItemWithoutContextId =
                substitute(xmlItemWithoutComponents,
                    "/item/properties/context/@objid", "");
        }

        String itemWithoutContextId = toString(xmlItemWithoutContextId, true);

        try {
            String xml = create(itemWithoutContextId);
        }
        catch (MissingAttributeValueException e) {
            return;
        }
        fail("Not expected exception");
    }

    /**
     * Test declining creating content item with missing Content Type.
     * 
     * @test.name CI with missing obd (Content Type)
     * @test.id OM_CI_2-2
     * @test.input Item XML (no content components)
     * @test.inputDescription Item XML correct obligatory data (no List of
     *                        Content Components) as described in OM_CI_1-1
     *                        except Content Type
     * @test.expected Error message
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi2b() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        Node xmlItemWithoutComponents =
            deleteElement(xmlItem, "/item/components");
        Node xmlItemWithoutContentType =
            deleteElement(xmlItemWithoutComponents,
                "/item/properties/content-model");

        String itemWithoutContentType =
            toString(xmlItemWithoutContentType, true);

        Class< ? > ec = XmlCorruptedException.class;
        try {
            String xml = create(itemWithoutContentType);
            EscidocRestSoapTestBase.failMissingException(ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining creating content item without any md-record.
     * 
     * @test.name CI with missing obd (Escidoc Internal Metadata Set)
     * @test.id OM_CI_2-3
     * @test.input Item XML (no content components)
     * @test.inputDescription Item XML correct obligatory data (no List of
     *                        Content Components) as described in OM_CI_1-1
     *                        except Escidoc Internal Metadata Set
     * @test.expected Error message
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi2c() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        Node xmlItemWithoutComponents =
            deleteElement(xmlItem, "/item/components");
        Node xmlItemWithoutEscidocMetadata =
            deleteElement(xmlItemWithoutComponents,
                "/item/md-records/md-record/publication");

        String itemWithoutEscidocMetadata =
            toString(xmlItemWithoutEscidocMetadata, true);

        Class<?> ec = XmlSchemaValidationException.class;
        try {
            String xml = create(itemWithoutEscidocMetadata);
            EscidocRestSoapTestBase.failMissingException(ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining creating content item with missing Escidoc Internal
     * Metadata Set.
     * 
     * @test.name CI with missing obd (Escidoc Internal Metadata Set)
     * @test.id OM_CI_2-3
     * @test.input Item XML (no content components)
     * @test.inputDescription Item XML correct obligatory data (no List of
     *                        Content Components) as described in OM_CI_1-1
     *                        except Escidoc Internal Metadata Set
     * @test.expected Error message
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi2e() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        Node xmlItemWithoutComponents =
            deleteElement(xmlItem, "/item/components");
        // Node attribute =
        // selectSingleNode(xmlItemWithoutComponents,
        // "/item/md-records/md-record/[@name = 'escidoc']/@name");
        // "/item/md-records/md-record/publication");
        Node xmlItemWithoutInternalMetadata =
            substitute(xmlItemWithoutComponents,
                "/item/md-records/md-record[@name = 'escidoc']/@name", "bla");
        String itemWithoutInternalMetadataXml =
            toString(xmlItemWithoutInternalMetadata, true);

        Class<?> ec = MissingMdRecordException.class;
        try {
            String xml = create(itemWithoutInternalMetadataXml);
            EscidocRestSoapTestBase.failMissingException(ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining creating content item with not existing Context.
     * 
     * @test.name CI with incorrect obd - Context does not exist
     * @test.id OM_CI_3-1
     * @test.input Item XML (no content components)
     * @test.inputDescription XML Item with invalid obligatory data (no List of
     *                        Content Components) obligatory data as described
     *                        in OM_CI_1-1 but Context does not exist
     * @test.expected Error message
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi3a() throws Exception {

        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        Node xmlItemWithoutComponents =
            deleteElement(xmlItem, "/item/components");
        Node xmlItemWithNotExistingContext = null;
        if (getTransport() == Constants.TRANSPORT_REST) {
            xmlItemWithNotExistingContext =
                substitute(xmlItemWithoutComponents,
                    "/item/properties/context/@href", "bla");
        }
        else {
            xmlItemWithNotExistingContext =
                substitute(xmlItemWithoutComponents,
                    "/item/properties/context/@objid", "bla");
        }

        String itemWithNotExistingContext =
            toString(xmlItemWithNotExistingContext, true);

        Class<?> ec = ContextNotFoundException.class;
        try {
            create(itemWithNotExistingContext);
            EscidocRestSoapTestBase.failMissingException(ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test successfully creating content item with one Content Component.
     * 
     * @test.name Correct CI with one Content Component (correct obd) (inline)
     *            [no staging link exist]
     * @test.id OM_CI_4
     * @test.input Item XML with Component stream
     * @test.inputDescription a. Item XML with correct obligatory data as
     *                        described in OM_CI_1-1<br/>
     *                        b. component stream with correct obligatory data: <br/>
     *                        - component title(component) <br/>
     *                        - description (properties) <br/>
     *                        - status(properties) <br/>
     *                        - visibility (properties) <br/>
     *                        - file-name (properties) <br/>
     *                        - mime-type (properties) <br/>
     *                        - file-size (properties) <br/>
     *                        - content title (content) <br/>
     *                        - base64 encoded binary content (content)
     * 
     * 
     * @test.expected Item XML now containing the parameters which are set by
     *                the system:<br/>
     *                for details see OM_CI_1-1<br/>
     *                and <br/>
     *                ID of the Content Component (component) <br/>
     *                Content Component href (component) <br/>
     *                creator href (properties) <br/>
     *                creator title (properties) <br/>
     *                - creation-date (properties) <br/>
     *                - last-modification-date (properties) <br/>
     *                - href to binary content (provided href to binary content
     *                will be changed or in case of base64 encoded binary
     *                content href will be set)
     * 
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi4() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        Node xmlItemWithoutSecondComponent =
            deleteElement(xmlItem, "/item/components/component[2]");
        String itemWithoutSecondComponent =
            toString(xmlItemWithoutSecondComponent, true);

        String xml = create(itemWithoutSecondComponent);
        assertXmlValidItem(xml);

        String componentId = null;
        String creatorId = null;
        if (getTransport() == Constants.TRANSPORT_SOAP) {
            Node itemObjiId =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/@objid");
            this.theItemId = itemObjiId.getTextContent();
            Node componentObjiId =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/@objid");
            componentId = componentObjiId.getTextContent();
            Node creatorIdNode =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/properties/created-by/@objid");
            creatorId = creatorIdNode.getTextContent();
        }
        else {
            Pattern PATTERN_OBJID_ATTRIBUTE = null;
            PATTERN_OBJID_ATTRIBUTE = Pattern.compile(".*\\/([^\"\\/]*)");

            Node itemObjiId =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/@href");

            Matcher m1 =
                PATTERN_OBJID_ATTRIBUTE.matcher(itemObjiId.getTextContent());
            if (m1.find()) {
                this.theItemId = m1.group(1);
            }

            Node componentObjiId =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/@href");

            Matcher m2 =
                PATTERN_OBJID_ATTRIBUTE.matcher(componentObjiId
                    .getTextContent());
            if (m2.find()) {
                componentId = m2.group(1);
            }

            Node creatorIdNode =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/properties/created-by/@href");
            Matcher m3 =
                PATTERN_OBJID_ATTRIBUTE.matcher(creatorIdNode.getTextContent());
            if (m3.find()) {
                creatorId = m3.group(1);
            }
        }

        if (getTransport() == Constants.TRANSPORT_REST) {
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/@href", "/ir/item/" + this.theItemId);
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/md-records/@href ", "/ir/item/"
                + this.theItemId + "/md-records");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/md-records/md-record[1]/@href ",
                "/ir/item/" + this.theItemId + "/md-records/md-record/escidoc");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/properties/@href ", "/ir/item/"
                + this.theItemId + "/properties");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/components/@href", "/ir/item/"
                + this.theItemId + "/components");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/components/component/@href",
                "/ir/item/" + this.theItemId + "/components/component/"
                    + componentId);
            assertXmlEquals("creator value is wrong", EscidocRestSoapTestBase
                .getDocument(xml),
                "/item/components/component/properties/created-by/@href",
                "/aa/user-account/" + creatorId);
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml),
                "/item/components/component[1]/content/@href", "/ir/item/"
                    + this.theItemId + "/components/component/" + componentId
                    + "/content");
            Node itemTitle =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/@title");
            assertNotNull(itemTitle);
            assertFalse("item title is not set", itemTitle
                .getTextContent().equals(""));
            Node creatorTitle =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/properties/created-by/@title");
            assertNotNull(creatorTitle.getTextContent());
            Node componentTitleNode =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/@title");
            String componentTitle = componentTitleNode.getTextContent();
            assertFalse("component title is not set", componentTitle.equals(""));
            assertXmlEquals("creator title value is wrong",
                EscidocRestSoapTestBase.getDocument(xml),
                "/item/components/component/properties/created-by/@title",
                creatorTitle.getTextContent());
            Node contentTitleNode =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/content/@title");
            assertNotNull(contentTitleNode);

            assertFalse("component content title is not set", contentTitleNode
                .getTextContent().equals(""));
        }

        Node modifiedDate =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/@last-modification-date");
        assertNotNull(modifiedDate);
        assertFalse("modified date is not set", modifiedDate
            .getTextContent().equals(""));
        assertXmlEquals("status value is wrong", EscidocRestSoapTestBase
            .getDocument(xml), "/item/properties/public-status", "pending");
        Node createdDate =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/properties/creation-date");
        assertNotNull(createdDate);
        assertFalse("created date is not set", createdDate
            .getTextContent().equals(""));
        assertXmlEquals("lock-status value is wrong", EscidocRestSoapTestBase
            .getDocument(xml), "/item/properties/lock-status", "unlocked");
        assertXmlEquals("current version number is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties//number", "1");
        assertXmlEquals("current version status is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/version/status", "pending");
        // assertXmlEquals("current version valid-status is wrong",
        // getDocument(xml), "/item/properties/version/valid-status", "valid");
        assertXmlEquals("current version date is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/version/date", modifiedDate.getTextContent());
        assertXmlEquals("latest version number is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/latest-version/number", "1");
        assertXmlEquals("latest version date is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/latest-version/date", modifiedDate
                .getTextContent());

        assertNotNull(creatorId);

        Node componentCreationDate1 =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/components/component/properties/creation-date");
        assertNotNull(componentCreationDate1);
        assertFalse("component creation date is not set",
            componentCreationDate1.getTextContent().equals(""));

        delete(this.theItemId);
    }

    /**
     * Test successfully creating content item with one Content Component
     * containing two md-records.
     */
    @Test
    public void testCreatingItemWithOneComponentWithTwoMdRecords()
        throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false),
                "escidoc_item_198_for_create_2_Component_Md-Records.xml");
        NodeList mdRecordsBeforeCreate =
            selectNodeList(xmlItem,
                "/item/components/component[1]/md-records/md-record");
        String xml = create(toString(xmlItem, false));
        // System.out.println("xml " + xml);
        assertXmlValidItem(xml);
        Document createdItem = EscidocRestSoapTestBase.getDocument(xml);
        NodeList mdRecordsAfterCreate =
            selectNodeList(createdItem,
                "/item/components/component[1]/md-records/md-record");
        assertEquals("Number of md-records is wrong ", mdRecordsAfterCreate
            .getLength(), mdRecordsBeforeCreate.getLength());

    }

    /**
     * Test successfully creating content item with two Content Components.
     * 
     * @test.name Correct CI with 2 correct Content Components (correct obd)
     * @test.id OM_CI_5
     * @test.input Item XML with two Component streams
     * 
     * @test.inputDescription a. Item XML with correct obligatory data as
     *                        described in OM_CI_1-1<br/>
     *                        b. Two Component Streams with correct obligatory
     *                        data: as described in Scenario OM_CI_4
     * 
     * @test.expected Item XML now containing the parameters which are set by
     *                the system:<br/>
     *                for details see OM_CI_1-1<br/>
     *                and <br/>
     *                IDs of two Content Components (component) <br/>
     *                Content Components hrefs of two components (component) <br/>
     *                creator hrefs of two components (properties) <br/>
     *                - creation-dates of two components (properties) <br/>
     *                - last-modification-dates of two components (properties)
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi5() throws Exception {

        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        Node xmiItemWithSEcondInlineContent =
            substitute(xmlItem, "/item/components/component[2]/content",
                "blablabla");
        Node xmiItemWithoutContentUrl =
            deleteAttribute(xmiItemWithSEcondInlineContent,
                "/item/components/component[2]/content", XLINK_HREF_TEMPLATES);
        String itemWithSEcondInlineContent =
            toString(xmiItemWithoutContentUrl, true);

        String xml = create(itemWithSEcondInlineContent);

        assertXmlValidItem(xml);
        this.theItemId = getIdFromRootElement(xml);

        String creatorId = null;
        Vector<String> componentIds = new Vector<String>();
        if (getTransport() == Constants.TRANSPORT_SOAP) {
            NodeList components =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/@objid");
            for (int i = 0; i < components.getLength(); i++) {
                Node componentObjId = components.item(i);
                String componentId = componentObjId.getTextContent();
                componentIds.add(componentId);
            }

            Node creatorIdNode =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/properties/created-by/@objid");
            creatorId = creatorIdNode.getTextContent();
        }
        else {
            Pattern PATTERN_OBJID_ATTRIBUTE = null;
            PATTERN_OBJID_ATTRIBUTE = Pattern.compile(".*\\/([^\"\\/]*)");

            NodeList components =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/@href");

            for (int i = 0; i < components.getLength(); i++) {
                Node componentHrefNode = components.item(i);
                String componentHref = componentHrefNode.getTextContent();

                Matcher m2 = PATTERN_OBJID_ATTRIBUTE.matcher(componentHref);
                if (m2.find()) {
                    componentIds.add(m2.group(1));
                }

            }

            Node creatorIdNode =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/properties/created-by/@href");
            Matcher m3 =
                PATTERN_OBJID_ATTRIBUTE.matcher(creatorIdNode.getTextContent());
            if (m3.find()) {
                creatorId = m3.group(1);
            }
        }

        if (getTransport() == Constants.TRANSPORT_REST) {
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml),
                "/item/components/component[1]/content/@href", "/ir/item/"
                    + theItemId + "/components/component/"
                    + componentIds.get(0) + "/content");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/@href", "/ir/item/" + theItemId);
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/md-records/@href ", "/ir/item/"
                + theItemId + "/md-records");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/md-records/md-record[1]/@href ",
                "/ir/item/" + theItemId + "/md-records/md-record/escidoc");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/properties/@href ", "/ir/item/"
                + theItemId + "/properties");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/components/@href", "/ir/item/"
                + theItemId + "/components");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/components/component[1]/@href",
                "/ir/item/" + theItemId + "/components/component/"
                    + componentIds.get(0));
            assertXmlEquals("creator value is wrong", EscidocRestSoapTestBase
                .getDocument(xml),
                "/item/components/component[1]/properties/created-by/@href",
                "/aa/user-account/" + creatorId);
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/components/component[2]/@href",
                "/ir/item/" + theItemId + "/components/component/"
                    + componentIds.get(1));
            assertXmlEquals("creator value is wrong", EscidocRestSoapTestBase
                .getDocument(xml),
                "/item/components/component[2]/properties/created-by/@href",
                "/aa/user-account/" + creatorId);
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml),
                "/item/components/component[2]/content/@href", "/ir/item/"
                    + theItemId + "/components/component/"
                    + componentIds.get(1) + "/content");
            Node itemTitle =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/@title");
            assertNotNull(itemTitle);
            assertFalse("item title is not set", itemTitle
                .getTextContent().equals(""));
            Node creatorTitle =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/properties/created-by/@title");
            assertNotNull(creatorTitle.getTextContent());
            NodeList componentTitles =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/@title");
            Vector<String> componentTitlesValues = new Vector<String>();
            for (int i = 0; i < componentTitles.getLength(); i++) {
                Node componentTitle = componentTitles.item(i);
                String title = componentTitle.getTextContent();
                componentTitlesValues.add(title);
            }
            assertFalse("component title is not set", componentTitlesValues
                .get(0).equals(""));
            assertXmlEquals("creator title value is wrong",
                EscidocRestSoapTestBase.getDocument(xml),
                "/item/components/component[1]/properties/created-by/@title",
                creatorTitle.getTextContent());
            NodeList componentContentTitles =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/content/@title");
            Vector<String> componentContentTitlesValues = new Vector<String>();
            for (int i = 0; i < componentContentTitles.getLength(); i++) {
                Node componentContentTitle = componentContentTitles.item(i);
                String contentTitle = componentContentTitle.getTextContent();
                componentContentTitlesValues.add(contentTitle);

            }
            assertFalse("component content title is not set",
                componentContentTitlesValues.get(1).equals(""));

            assertFalse("component title is not set", componentTitlesValues
                .get(1).equals(""));
            assertXmlEquals("creator title value is wrong",
                EscidocRestSoapTestBase.getDocument(xml),
                "/item/components/component[2]/properties/created-by/@title",
                creatorTitle.getTextContent());
            assertFalse("component content title is not set",
                componentContentTitlesValues.get(1).equals(""));
        }

        Node modifiedDate =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/@last-modification-date");
        assertNotNull(modifiedDate);
        assertFalse("modified date is not set", modifiedDate
            .getTextContent().equals(""));
        assertXmlEquals("status value is wrong", EscidocRestSoapTestBase
            .getDocument(xml), "/item/properties/public-status", "pending");
        Node createdDate =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/properties/creation-date");
        assertNotNull(createdDate);
        assertFalse("created date is not set", createdDate
            .getTextContent().equals(""));
        assertXmlEquals("lock-status value is wrong", EscidocRestSoapTestBase
            .getDocument(xml), "/item/properties/lock-status", "unlocked");
        assertXmlEquals("current version number is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/version/number", "1");
        assertXmlEquals("current version status is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/version/status", "pending");
        // assertXmlEquals("current version valid-status is wrong",
        // getDocument(xml), "/item/properties/version/valid-status", "valid");
        assertXmlEquals("current version date is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/version/date", modifiedDate.getTextContent());
        assertXmlEquals("latest version number is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/latest-version/number", "1");
        assertXmlEquals("latest version date is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/latest-version/date", modifiedDate
                .getTextContent());

        assertNotNull(creatorId);

        Node componentCreationDate1 =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/components/component[1]/properties/creation-date");
        assertNotNull(componentCreationDate1);
        assertFalse("component creation date is not set",
            componentCreationDate1.getTextContent().equals(""));

        Node componentCreationDate2 =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/components/component[2]/properties/creation-date");
        assertNotNull(componentCreationDate2);
        assertFalse("component creation date is not set",
            componentCreationDate2.getTextContent().equals(""));

        delete(theItemId);
    }

    /**
     * 
     * Test successfully creating content item with one Content Component.
     * 
     * @test.name Correct CI with one Content Component (correct obd) (via href)
     *            [link exists]
     * 
     * @test.id OM_CI_8
     * @test.input Item XML with Component stream
     * @test.inputDescription a. Item XML with correct obligatory data as
     *                        described in OM_CI_1-1<br/>
     *                        b. component stream with correct obligatory data: <br/>
     *                        - component title(component) <br/>
     *                        - description (properties) <br/>
     *                        - status(properties) <br/>
     *                        - visibility (properties) <br/>
     *                        - file-name (properties) <br/>
     *                        - mime-type (properties) <br/>
     *                        - file-size (properties) <br/>
     *                        - content title (content) <br/>
     *                        - href (content)
     * 
     * @test.expected
     * @test.expected Item XML now containing the parameters which are set by
     *                the system: <br/>
     *                ID of the Content Component (component) <br/>
     *                Content Component href (component) <br/>
     *                creator href (properties) <br/>
     *                creator title (properties) <br/>
     *                - creation-date (properties) <br/>
     *                - last-modification-date (properties) <br/>
     *                - href to binary content (provided href to binary content
     *                will be changed)
     * 
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi8() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        Node xmlItemWithoutFirstComponent =
            deleteElement(xmlItem, "/item/components/component[1]");
        String itemWithoutFirstComponent =
            toString(xmlItemWithoutFirstComponent, true);
        String xml = create(itemWithoutFirstComponent);
        assertXmlValidItem(xml);
        this.theItemId = getIdFromRootElement(xml);

        String componentId = null;
        String creatorId = null;
        if (getTransport() == Constants.TRANSPORT_SOAP) {
            Node itemObjiId =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/@objid");
            this.theItemId = itemObjiId.getTextContent();
            Node componentObjiId =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/@objid");
            componentId = componentObjiId.getTextContent();
            Node creatorIdNode =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/properties/created-by/@objid");
            creatorId = creatorIdNode.getTextContent();
        }
        else {
            Pattern PATTERN_OBJID_ATTRIBUTE = null;
            PATTERN_OBJID_ATTRIBUTE = Pattern.compile(".*\\/([^\"\\/]*)");

            Node itemObjiId =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/@href");

            Matcher m1 =
                PATTERN_OBJID_ATTRIBUTE.matcher(itemObjiId.getTextContent());
            if (m1.find()) {
                this.theItemId = m1.group(1);
            }

            Node componentObjiId =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/@href");

            assertNotNull("Component href missing", componentObjiId);
            assertNotNull("Component href value missing", componentObjiId
                .getTextContent());

            Matcher m2 =
                PATTERN_OBJID_ATTRIBUTE.matcher(componentObjiId
                    .getTextContent());
            if (m2.find()) {
                componentId = m2.group(1);
            }

            Node creatorIdNode =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/properties/created-by/@href");
            Matcher m3 =
                PATTERN_OBJID_ATTRIBUTE.matcher(creatorIdNode.getTextContent());
            if (m3.find()) {
                creatorId = m3.group(1);
            }
        }

        if (getTransport() == Constants.TRANSPORT_REST) {
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/@href", "/ir/item/" + theItemId);
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/md-records/@href ", "/ir/item/"
                + theItemId + "/md-records");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/md-records/md-record[1]/@href ",
                "/ir/item/" + theItemId + "/md-records/md-record/escidoc");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/properties/@href ", "/ir/item/"
                + theItemId + "/properties");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/components/@href", "/ir/item/"
                + theItemId + "/components");

            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/components/component/@href",
                "/ir/item/" + theItemId + "/components/component/"
                    + componentId);
            assertXmlEquals("creator value is wrong", EscidocRestSoapTestBase
                .getDocument(xml),
                "/item/components/component/properties/created-by/@href",
                "/aa/user-account/" + creatorId);
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml),
                "/item/components/component[1]/content/@href", "/ir/item/"
                    + theItemId + "/components/component/" + componentId
                    + "/content");
            Node itemTitle =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/@title");
            assertNotNull(itemTitle);
            assertFalse("item title is not set", itemTitle
                .getTextContent().equals(""));
            Node creatorTitle =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/properties/created-by/@title");
            assertNotNull(creatorTitle.getTextContent());

            Node componentTitleNode =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/@title");
            String componentTitle = componentTitleNode.getTextContent();

            assertFalse("component title is not set", componentTitle.equals(""));
            assertXmlEquals("creator title value is wrong",
                EscidocRestSoapTestBase.getDocument(xml),
                "/item/components/component/properties/created-by/@title",
                creatorTitle.getTextContent());
            Node contentTitleNode =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/content/@title");
            assertNotNull(contentTitleNode);

            assertFalse("component content title is not set", contentTitleNode
                .getTextContent().equals(""));

        }

        Node modifiedDate =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/@last-modification-date");
        assertNotNull(modifiedDate);
        assertFalse("modified date is not set", modifiedDate
            .getTextContent().equals(""));
        assertXmlEquals("status value is wrong", EscidocRestSoapTestBase
            .getDocument(xml), "/item/properties/public-status", "pending");
        Node createdDate =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/properties/creation-date");
        assertNotNull(createdDate);
        assertFalse("created date is not set", createdDate
            .getTextContent().equals(""));
        assertXmlEquals("lock-status value is wrong", EscidocRestSoapTestBase
            .getDocument(xml), "/item/properties/lock-status", "unlocked");
        assertXmlEquals("current version number is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/version/number", "1");
        assertXmlEquals("current version status is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/version/status", "pending");
        // assertXmlEquals("current version valid-status is wrong",
        // getDocument(xml), "/item/properties/version/valid-status", "valid");
        assertXmlEquals("current version date is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/version/date", modifiedDate.getTextContent());
        assertXmlEquals("latest version number is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/latest-version/number", "1");
        assertXmlEquals("latest version date is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/latest-version/date", modifiedDate
                .getTextContent());

        assertNotNull(creatorId);
        Node componentCreationDate1 =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/components/component/properties/creation-date");
        assertNotNull(componentCreationDate1);
        assertFalse("component creation date is not set",
            componentCreationDate1.getTextContent().equals(""));

        delete(theItemId);
    }

    /**
     * Test successfully creating content item with two Content Components.
     * 
     * @test.name Correct CI with 2 correct Content Components (via href)
     *            (correct obd) [links exists]
     * @test.id OM_CI_9
     * @test.input Item XML with two Component streams
     * 
     * @test.inputDescription a. Item XML with correct obligatory data as
     *                        described in OM_CI_1-1<br/>
     *                        b. Two Component Streams with correct obligatory
     *                        data: as described in Scenario OM_CI_8
     * 
     * @test.expected Item XML now containing the parameters which are set by
     *                the system as described in OM_CI_1-5
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi9() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_2_components.xml");

        String xml = create(toString(xmlItem, false));
        assertXmlValidItem(xml);
        this.theItemId = getIdFromRootElement(xml);

        String creatorId = null;
        Vector<String> componentIds = new Vector<String>();
        if (getTransport() == Constants.TRANSPORT_SOAP) {
            NodeList components =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/@objid");
            for (int i = 0; i < components.getLength(); i++) {
                Node componentObjId = components.item(i);
                String componentId = componentObjId.getTextContent();
                componentIds.add(componentId);
            }

            Node creatorIdNode =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/properties/created-by/@objid");
            creatorId = creatorIdNode.getTextContent();
        }
        else {
            Pattern PATTERN_OBJID_ATTRIBUTE = null;
            PATTERN_OBJID_ATTRIBUTE = Pattern.compile(".*\\/([^\"\\/]*)");

            NodeList components =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/@href");

            for (int i = 0; i < components.getLength(); i++) {
                Node componentHrefNode = components.item(i);
                String componentHref = componentHrefNode.getTextContent();

                Matcher m2 = PATTERN_OBJID_ATTRIBUTE.matcher(componentHref);
                if (m2.find()) {
                    componentIds.add(m2.group(1));
                }

            }

            Node creatorIdNode =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/properties/created-by/@href");
            Matcher m3 =
                PATTERN_OBJID_ATTRIBUTE.matcher(creatorIdNode.getTextContent());
            if (m3.find()) {
                creatorId = m3.group(1);
            }
        }

        if (getTransport() == Constants.TRANSPORT_REST) {
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/@href", "/ir/item/" + theItemId);
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/md-records/@href ", "/ir/item/"
                + theItemId + "/md-records");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/md-records/md-record[1]/@href ",
                "/ir/item/" + theItemId + "/md-records/md-record/escidoc");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/properties/@href ", "/ir/item/"
                + theItemId + "/properties");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/components/@href", "/ir/item/"
                + theItemId + "/components");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/components/component[1]/@href",
                "/ir/item/" + theItemId + "/components/component/"
                    + componentIds.get(0));
            assertXmlEquals("creator value is wrong", EscidocRestSoapTestBase
                .getDocument(xml),
                "/item/components/component[1]/properties/created-by/@href",
                "/aa/user-account/" + creatorId);
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml),
                "/item/components/component[1]/content/@href", "/ir/item/"
                    + theItemId + "/components/component/"
                    + componentIds.get(0) + "/content");
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml), "/item/components/component[2]/@href",
                "/ir/item/" + theItemId + "/components/component/"
                    + componentIds.get(1));
            assertXmlEquals("creator value is wrong", EscidocRestSoapTestBase
                .getDocument(xml),
                "/item/components/component[2]/properties/created-by/@href",
                "/aa/user-account/" + creatorId);
            assertXmlEquals("href value is wrong", EscidocRestSoapTestBase
                .getDocument(xml),
                "/item/components/component[2]/content/@href", "/ir/item/"
                    + theItemId + "/components/component/"
                    + componentIds.get(1) + "/content");
            Node itemTitle =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/@title");
            assertNotNull(itemTitle);
            assertFalse("item title is not set", itemTitle
                .getTextContent().equals(""));
            Node creatorTitle =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/properties/created-by/@title");
            assertNotNull(creatorTitle.getTextContent());

            NodeList componentTitles =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/@title");
            Vector<String> componentTitlesValues = new Vector<String>();
            for (int i = 0; i < componentTitles.getLength(); i++) {
                Node componentTitle = componentTitles.item(i);
                String title = componentTitle.getTextContent();
                componentTitlesValues.add(title);
            }

            assertFalse("component title is not set", componentTitlesValues
                .get(0).equals(""));
            assertXmlEquals("creator title value is wrong",
                EscidocRestSoapTestBase.getDocument(xml),
                "/item/components/component[1]/properties/created-by/@title",
                creatorTitle.getTextContent());
            NodeList componentContentTitles =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    "/item/components/component/content/@title");
            Vector<String> componentContentTitlesValues = new Vector<String>();
            for (int i = 0; i < componentContentTitles.getLength(); i++) {
                Node componentContentTitle = componentContentTitles.item(i);
                String contentTitle = componentContentTitle.getTextContent();
                componentContentTitlesValues.add(contentTitle);

            }
            assertFalse("component content title is not set",
                componentContentTitlesValues.get(1).equals(""));

            assertFalse("component title is not set", componentTitlesValues
                .get(1).equals(""));
            assertXmlEquals("creator title value is wrong",
                EscidocRestSoapTestBase.getDocument(xml),
                "/item/components/component[2]/properties/created-by/@title",
                creatorTitle.getTextContent());

            assertFalse("component content title is not set",
                componentContentTitlesValues.get(1).equals(""));
        }

        Node modifiedDate =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/@last-modification-date");
        assertNotNull(modifiedDate);
        assertFalse("modified date is not set", modifiedDate
            .getTextContent().equals(""));
        assertXmlEquals("status value is wrong", EscidocRestSoapTestBase
            .getDocument(xml), "/item/properties/public-status", "pending");
        Node createdDate =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/properties/creation-date");
        assertNotNull(createdDate);
        assertFalse("created date is not set", createdDate
            .getTextContent().equals(""));
        assertXmlEquals("lock-status value is wrong", EscidocRestSoapTestBase
            .getDocument(xml), "/item/properties/lock-status", "unlocked");
        assertXmlEquals("current version number is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/version/number", "1");
        assertXmlEquals("current version status is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/version/status", "pending");
        // assertXmlEquals("current version valid-status is wrong",
        // getDocument(xml), "/item/properties/version/valid-status", "valid");
        assertXmlEquals("current version date is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/version/date", modifiedDate.getTextContent());
        assertXmlEquals("latest version number is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/latest-version/number", "1");
        assertXmlEquals("latest version date is wrong",
            EscidocRestSoapTestBase.getDocument(xml),
            "/item/properties/latest-version/date", modifiedDate
                .getTextContent());

        assertNotNull(creatorId);

        Node componentCreationDate1 =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/components/component[1]/properties/creation-date");
        assertNotNull(componentCreationDate1);
        assertFalse("component creation date is not set",
            componentCreationDate1.getTextContent().equals(""));

        Node componentCreationDate2 =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/components/component[2]/properties/creation-date");
        assertNotNull(componentCreationDate2);
        assertFalse("component creation date is not set",
            componentCreationDate2.getTextContent().equals(""));

        delete(theItemId);
    }

    /**
     * Test successfully creating content item with two Content Components, The
     * first component does not contain md-records.
     * 
     * @test.name Correct CI with 2 correct Content Components (via href)
     *            (correct obd) [links exists]
     * @test.id OM_CI_9
     * @test.input Item XML with two Component streams
     * 
     * @test.inputDescription a. Item XML with correct obligatory data as
     *                        described in OM_CI_1-1<br/>
     *                        b. Two Component Streams with correct obligatory
     *                        data: as described in Scenario OM_CI_8
     * 
     * @test.expected Item XML now containing the parameters which are set by
     *                the system as described in OM_CI_1-5
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testCreateItemWithFirstComponendWithoutMdRecords()
        throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_2_components.xml");
        Node itemWith1ComponentWithoutMdRecords =
            deleteElement(xmlItem, "item/components/component[1]/md-records");
        Node itemWithContentin1ComponentWithExternalUrl =
            substitute(itemWith1ComponentWithoutMdRecords,
                "item/components/component[1]/content/@storage", "external-url");

        String xml =
            create(toString(itemWith1ComponentWithoutMdRecords, false));
        assertXmlValidItem(xml);
        this.theItemId = getIdFromRootElement(xml);
        Document createdItem = getDocument(xml);
        Node firstComponentContent =
            selectSingleNode(createdItem,
                "item/components/component[1]/content/@storage");
        if (firstComponentContent.getNodeValue().equals("external-url")) {
            Node mdRecords =
                selectSingleNode(createdItem,
                    "item/components/component[1]/md-records");
            assertNull("the component may not have md-records", mdRecords);
        }
        else {
            Node mdRecords =
                selectSingleNode(createdItem,
                    "item/components/component[2]/md-records");
            assertNull("the component may not have md-records", mdRecords);
        }

        // delete(itemId);
    }

    /**
     * Test declining creating content item and Content Component with wrong
     * href.
     * 
     * @test.name Incorrect Content Component � incorrect href
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi12a() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        Node xmlItemWithoutFirstComponent =
            deleteElement(xmlItem, "/item/components/component[1]");
        Node xmiItemWithWrongtHrefContent =
            substitute(xmlItemWithoutFirstComponent,
                "/item/components/component/content/@href", "http://localhost/bla");

        String itemWithWrongtHrefContent =
            toString(xmiItemWithWrongtHrefContent, true);
        try {
            create(itemWithWrongtHrefContent);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("",
                FileNotFoundException.class, e);

            return;
        }
        fail("Not expected exception");
    }

    /**
     * Test declining creating content item without content components - item
     * xml with set read only element.
     * 
     * @test.name Incorrect item xml <br />
     *            item xml with set read only element
     * @test.id OM_CI_12-5
     * @test.name CI without component stream
     * @test.inputDescription a. Item XML with correct obligatory data as
     *                        described in OM_CI_1-1, but with set read only
     *                        element
     * @test.expected Error message
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi12e() throws Exception {
        String newDate = "1970-01-01T01:00:00.000Z";
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        Element creator =
            xmlItem.createElementNS(PROPERTIES_NS_URI_04, "prop:creation-date");
        creator.setTextContent(newDate);
        NodeList propertiesList =
            xmlItem.getElementsByTagName("escidocItem:properties");
        Node properties = null;
        for (int i = 0; i < propertiesList.getLength(); i++) {
            properties = propertiesList.item(i);

        }
        NodeList ctsList = xmlItem.getElementsByTagName("srel:context");
        Node cts = null;
        for (int i = 0; i < ctsList.getLength(); i++) {
            cts = ctsList.item(i);

        }
        properties.insertBefore(creator, cts);

        String itemWithCreator = toString(xmlItem, true);

        Class<?> ec = ReadonlyElementViolationException.class;
        String xml = null;
        try {
            xml = create(itemWithCreator);
            // read only elements are ignored now
            // failMissingException(ec);
        }
        catch (Exception e) {
            // read only elements are ignored now
            fail("Exception on update with read-only element set.");
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
        // read only elements are ignored now
        assertNotEquals("Read-only element should not be set ",
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                "/item/properties/creation-date").getTextContent(), newDate);
    }

    /**
     * Test successfully creating item with two relations.
     * 
     * @throws Exception
     */
    @Test
    public void testRelations() throws Exception {
        String itemXml1 =
            create(EscidocRestSoapTestBase.getTemplateAsString(
                TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                "escidoc_item_198_for_create.xml"));
        String itemXml2 =
            create(EscidocRestSoapTestBase.getTemplateAsString(
                TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                "escidoc_item_198_for_create.xml"));

        String createdItemId1 = getIdFromRootElement(itemXml1);
        String createdItemId2 = getIdFromRootElement(itemXml2);

        String href1 = "/ir/item/" + createdItemId1;
        String href2 = "/ir/item/" + createdItemId2;
        String itemForCreateWithRelationsXml =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false),
                "escidoc_item_198_for_createWithRelations.xml");

        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_ID1##",
                createdItemId1);
        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_ID2##",
                createdItemId2);
        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_HREF1##", href1);
        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_HREF2##", href2);
        Document itemForCreateWithRelations =
            EscidocRestSoapTestBase.getDocument(itemForCreateWithRelationsXml);
        Node xmlItemWithoutComponents =
            deleteElement(itemForCreateWithRelations, "/item/components");
        String itemWithoutComponents = toString(xmlItemWithoutComponents, true);
        NodeList relations =
            selectNodeList(EscidocRestSoapTestBase
                .getDocument(itemWithoutComponents), "/item/relations/relation");

        String xml = create(itemWithoutComponents);
        NodeList relationsAfterCreate =
            selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                "/item/relations/relation");

        assertXmlValidItem(xml);
        assertEquals("Number of relations is wrong ", relations.getLength(),
            relationsAfterCreate.getLength());

        String createdItemId = getIdFromRootElement(xml);
        this.theItemId = createdItemId;
        xml = retrieve(createdItemId);
        relationsAfterCreate =
            selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                "/item/relations/relation");

        assertXmlValidItem(xml);
        assertEquals("Number of relations is wrong ", relations.getLength(),
            relationsAfterCreate.getLength());

    }

    /**
     * Test successfully creating item with two relations. The Item is created
     * from template. Each Item for content relation is created during test.
     * 
     * @throws Exception
     */
    @Test
    public void testRelations02() throws Exception {

        InputStream fis =
            ResourceProvider.getFileInputStreamFromFile(TEMPLATE_EXAMPLE_PATH
                + "/" + getTransport(false), "item-minimal-for-create-01.xml");
        String itemTemplate = ResourceProvider.getContentsFromInputStream(fis);

        String itemXml1 = create(itemTemplate);
        String itemXml2 = create(itemTemplate);

        String itemId1 = getIdFromRootElement(itemXml1);
        String itemId2 = getIdFromRootElement(itemXml2);

        if (getTransport() == Constants.TRANSPORT_REST) {
            // make hrefs from objid
            itemId1 = "/ir/item/" + itemId1;
            itemId2 = "/ir/item/" + itemId2;
        }

        String itemRelationsXml =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_relation_01.xml");

        itemRelationsXml =
            itemRelationsXml.replaceAll("###ITEM_ID1###", itemId1);
        itemRelationsXml =
            itemRelationsXml.replaceAll("###ITEM_ID2###", itemId2);

        String xmlCreated = create(itemRelationsXml);

        assertXmlValidItem(xmlCreated);

        NodeList relationsAfterCreate =
            selectNodeList(EscidocRestSoapTestBase.getDocument(xmlCreated),
                "/item/relations/relation");

        assertEquals("Number of relations is wrong ", 2, relationsAfterCreate
            .getLength());

        String createdItemId = getIdFromRootElement(xmlCreated);

        String xmlRetrieved = retrieve(createdItemId);
        assertXmlValidItem(xmlRetrieved);

        NodeList relationsAfterRetrieve =
            selectNodeList(EscidocRestSoapTestBase.getDocument(xmlRetrieved),
                "/item/relations/relation");

        assertEquals("Number of relations differs between create and retieve ",
            relationsAfterRetrieve.getLength(), relationsAfterCreate
                .getLength());

    }

    /**
     * Test declining creating item with relations, whose targets references non
     * existing resources.
     * 
     * @throws Exception
     */
    @Test
    public void testRelationsWithWrongTarget() throws Exception {

        String createdItemId1 = "bla1";
        String createdItemId2 = "bla2";

        String href1 = "/ir/item/" + createdItemId1;
        String href2 = "/ir/item/" + createdItemId2;
        String itemForCreateWithRelationsXml =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false),
                "escidoc_item_198_for_createWithRelations.xml");

        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_ID1##",
                createdItemId1);
        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_ID2##",
                createdItemId2);
        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_HREF1##", href1);
        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_HREF2##", href2);
        Document itemForCreateWithRelations =
            EscidocRestSoapTestBase.getDocument(itemForCreateWithRelationsXml);
        Node xmlItemWithoutComponents =
            deleteElement(itemForCreateWithRelations, "/item/components");
        String itemWithoutComponents = toString(xmlItemWithoutComponents, true);

        try {
            create(itemWithoutComponents);
            fail("No exception occured on item created with relations, which "
                + " references non existing targets.");
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "ReferencedResourceNotFound expected.",
                ReferencedResourceNotFoundException.class, e);
        }
    }

    /**
     * Tess declining creating item with relations, whose target ids containing
     * a version number.
     * 
     * @throws Exception
     */
    @Test
    public void testRelationsWithTargetContainingVersionNumber()
        throws Exception {

        String createdItemId1 = "escidoc:123:2";
        String createdItemId2 = "escidoc:123:3";

        String href1 = "/ir/item/" + createdItemId1;
        String href2 = "/ir/item/" + createdItemId2;
        String itemForCreateWithRelationsXml =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false),
                "escidoc_item_198_for_createWithRelations.xml");

        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_ID1##",
                createdItemId1);
        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_ID2##",
                createdItemId2);
        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_HREF1##", href1);
        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_HREF2##", href2);
        Document itemForCreateWithRelations =
            EscidocRestSoapTestBase.getDocument(itemForCreateWithRelationsXml);
        Node xmlItemWithoutComponents =
            deleteElement(itemForCreateWithRelations, "/item/components");
        String itemWithoutComponents = toString(xmlItemWithoutComponents, true);

        try {
            create(itemWithoutComponents);
            fail("No exception occured on item crate with relations, which "
                + " target ids containing a version number.");
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "InvalidContentException expected.",
                InvalidContentException.class, e);
        }
    }

    /**
     * Test declining creating item with relations with non existing predicate.
     * 
     * @throws Exception
     */
    @Test
    public void testRelationsWithWrongPredicate() throws Exception {

        String itemXml1 =
            create(EscidocRestSoapTestBase.getTemplateAsString(
                TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                "escidoc_item_198_for_create.xml"));
        String itemXml2 =
            create(EscidocRestSoapTestBase.getTemplateAsString(
                TEMPLATE_ITEM_PATH + "/" + getTransport(false),
                "escidoc_item_198_for_create.xml"));

        String createdItemId1 = null;
        String createdItemId2 = null;

        Pattern PATTERN_OBJID_ATTRIBUTE = null;
        if (getTransport() == Constants.TRANSPORT_SOAP) {
            PATTERN_OBJID_ATTRIBUTE = Pattern.compile("objid=\"([^\"]*)\"");
        }
        else {
            PATTERN_OBJID_ATTRIBUTE =
                Pattern.compile("href=\"/ir/item/([^\"]*)\"");
        }
        Matcher m1 = PATTERN_OBJID_ATTRIBUTE.matcher(itemXml1);
        if (m1.find()) {
            createdItemId1 = m1.group(1);
        }
        Matcher m2 = PATTERN_OBJID_ATTRIBUTE.matcher(itemXml2);
        if (m2.find()) {
            createdItemId2 = m2.group(1);
        }

        String href1 = "/ir/item/" + createdItemId1;
        String href2 = "/ir/item/" + createdItemId2;
        String itemForCreateWithRelationsXml =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false),
                "escidoc_item_198_for_createWithRelations.xml");

        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_ID1##",
                createdItemId1);
        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_ID2##",
                createdItemId2);
        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_HREF1##", href1);
        itemForCreateWithRelationsXml =
            itemForCreateWithRelationsXml.replaceAll("##ITEM_HREF2##", href2);
        Document itemForCreateWithRelations =
            EscidocRestSoapTestBase.getDocument(itemForCreateWithRelationsXml);
        Node xmlItemWithoutComponents =
            deleteElement(itemForCreateWithRelations, "/item/components");

        Node relationPredicate =
            selectSingleNode(xmlItemWithoutComponents,
                "/item/relations/relation[1]/@predicate");
        relationPredicate.setNodeValue("http://www.bla.de#bla");

        String itemWithoutComponents = toString(xmlItemWithoutComponents, true);
        try {
            create(itemWithoutComponents);
            fail("No exception occured on item create with relations, which "
                + " references non existing predicate.");
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "RelationPredicateNotFoundException expected.",
                RelationPredicateNotFoundException.class, e);
        }

    }

    @Test
    public void testComponentsInFirstVersion() throws Exception {
        // create an item with components
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        String itemXml = toString(xmlItem, true);
        final String createdItem = create(itemXml);
        String itemId = getIdFromRootElement(createdItem);

        // make second version
        xmlItem = EscidocRestSoapTestBase.getDocument(createdItem);
        selectSingleNode(xmlItem, "/item/properties/content-model-specific")
            .appendChild(xmlItem.createElement("nischt"));
        itemXml = toString(xmlItem, true);
        final String updatedItem = update(itemId, itemXml);

        // retrieve first version
        final String firstItem = retrieve(itemId + ":1");
        xmlItem = EscidocRestSoapTestBase.getDocument(firstItem);
        selectSingleNodeAsserted(xmlItem, "/item/components");
        selectSingleNodeAsserted(xmlItem, "/item/components/component");
        selectSingleNodeAsserted(xmlItem,
            "/item/components/component/properties");
        selectSingleNodeAsserted(xmlItem, "/item/components/component/content");
        assertXmlValidItem(firstItem);

        delete(itemId);
    }

    @Test
    public void testCreateFromRetrieve() throws Exception {

        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        deleteElement(xmlItem, "/item/components/component[1]");

        String itemId = getObjidValue(create(toString(xmlItem, true)));
        String createdItem = retrieve(itemId);
        Document createdItemDoc =
            EscidocRestSoapTestBase.getDocument(createdItem);
        createdItemDoc =
            (Document) substitute(createdItemDoc,
                "/item/components/component/content/@href",
                "http://localhost:8080");
        createdItem = toString(createdItemDoc, true);
        final String createdXml = create(createdItem);

        assertXmlValidItem(createdXml);
    }

    /**
     * Test successfully creatig of an item with 2 md-records.
     * 
     * @throws Exception
     *             Thrown if anything failed.
     */
    @Test
    public void testCreateItemWith2Mdrecords() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false),
                "escidoc_item_2_md_records_for_create.xml");
        NodeList mdrecords =
            selectNodeList(xmlItem, "/item/md-records/md-record");
        Node xmlItemWithoutComponents =
            deleteElement(xmlItem, "/item/components");

        String itemWithoutComponents = toString(xmlItemWithoutComponents, true);
        final String createdXml = create(itemWithoutComponents);
        assertXmlValidItem(createdXml);
        final Document createdDocument =
            EscidocRestSoapTestBase.getDocument(createdXml);

        NodeList mdrecordsAfterCreate =
            selectNodeList(createdDocument, "/item/md-records/md-record");
        assertEquals(mdrecords.getLength(), mdrecordsAfterCreate.getLength());

    }

    /**
     * Test decleaning creatig of an item with 2 escidoc md-records.
     * 
     * @throws Exception
     *             Thrown if anything failed.
     */
    @Test
    public void testCreateItemWith2EscidocMdrecords() throws Exception {
        String xmlItem =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false),
                "escidoc_item_2_escidoc_md_records_for_create.xml");
        try {
            create(xmlItem);
            fail("InvalidContentException expected if cretaing item without"
                + " Md-records.");
        }
        catch (Exception e) {
            Class<?> ec = InvalidContentException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }

    }

    /**
     * Test decleaning create an item without md-records.
     * 
     * @throws Exception
     *             Thrown if anything failed.
     */
    @Test
    public void testCreateItemWithoutMdrecords() throws Exception {
        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false),
                "escidoc_item_2_md_records_for_create.xml");

        Node xmlItemWithoutMdRecords =
            deleteElement(xmlItem, "/item/md-records");

        String itemWithoutMdRecords = toString(xmlItemWithoutMdRecords, true);

        try {
            create(itemWithoutMdRecords);
            fail("MissingMdRecordException expected if cretaing item without"
                + " Md-records.");
        }
        catch (Exception e) {
            Class<?> ec = MissingMdRecordException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }

    }

    /**
     * Test deleting component via deleteComponent method.
     * 
     * @throws Exception
     *             Thrown if anything failed.
     */
    @Test
    public void testDeleteComponent01() throws Exception {

        String itemXml =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        String createdItem = create(itemXml);
        Document itemDoc = getDocument(createdItem);

        String itemId = getObjidValue(itemDoc);

        String componentId = null;
        String componentHref = null;

        // get component id
        if (getTransport() == Constants.TRANSPORT_SOAP) {
            Node componentObjiId =
                selectSingleNode(itemDoc, "/item/components/component/@objid");
            componentId = componentObjiId.getTextContent();
        }
        else {
            Node componentObjiId =
                selectSingleNode(itemDoc, "/item/components/component/@href");

            componentHref = componentObjiId.getTextContent();
            componentId = getObjidFromHref(componentHref);
        }

        deleteComponent(itemId, componentId);

        // check if the component is realy deleted
        itemXml = retrieve(itemId);
        itemDoc = getDocument(itemXml);

        if (getTransport() == Constants.TRANSPORT_SOAP) {
            assertXmlNotExists("Component not deleted", itemDoc,
                "/item/components/component[@objid='" + componentId + "']");
        }
        else {
            assertXmlNotExists("Component not deleted", itemDoc,
                "/item/components/component[@href='" + componentHref + "']");
        }
    }

    /**
     * Test delete of not existing component via deleteComponent method.
     * 
     * @throws Exception
     *             Thrown if anything failed.
     */
    @Test
    public void testDeleteComponent02() throws Exception {

        String itemXml =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        String createdItem = create(itemXml);
        Document itemDoc = getDocument(createdItem);

        String itemId = getObjidValue(itemDoc);

        try {
            deleteComponent(itemId, itemId);
            fail("ComponentNotFoundException expected if deleting not"
                + " existing Component.");
        }
        catch (Exception e) {
            Class<?> ec = ComponentNotFoundException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    @Test
    public void testExternalUrlWithParam() throws Exception {

        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");
        Node xmlItemDoc =
            deleteElement(xmlItem,
                "/item/components/component[not(@href) or position() = 1]");
        // set URL
        String urlWithParam = "http://localhost:8080?p1=v1&p2=v2";
        xmlItemDoc =
            substitute(xmlItemDoc, "/item/components/component/content/@href",
                urlWithParam);
        // set storage
        String storage = "external-url";
        xmlItemDoc =
            substitute(xmlItemDoc,
                "/item/components/component/content/@storage", storage);

        String xml = toString(xmlItemDoc, true);

        final String createdXml = create(xml);
        assertXmlValidItem(createdXml);

        final Document createdDocument =
            EscidocRestSoapTestBase.getDocument(createdXml);

        // check URL
        assertEquals(urlWithParam, selectSingleNode(createdDocument,
            "/item/components/component/content/@href").getNodeValue());

        this.theItemId = getIdFromRootElement(createdXml);
        delete(this.theItemId);

    }

    /**
     * Test if the right Exception is thrown if multiple Metadata Records with
     * same name attribute are created.
     * 
     * @throws Exception
     */
    @Test
    public void testMDRecordUniqueNames01() throws Exception {

        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");

        // add new md-record
        String xmlMdRecord =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "md-record.xml");
        Node mdRecord =
            selectSingleNode(EscidocRestSoapTestBase.getDocument(xmlMdRecord),
                "/md-record");

        xmlItem.importNode(mdRecord, true);

        Node mdRecords = selectSingleNode(xmlItem, "/item/md-records");
        mdRecords.appendChild(xmlItem.adoptNode(mdRecord));
        xmlItem.normalize();

        NodeList mdRe =
            selectNodeList(xmlItem,
                "/item/md-records/md-record[@name='escidoc']");
        assertTrue("Not enough md-record with name 'escidoc' for test. ", mdRe
            .getLength() > 1);

        try {
            create(toString(xmlItem, true));
            fail("Missing Exception if Item contains more than one md-record"
                + " with name 'escidoc'.");
        }
        catch (Exception e) {
            // FIXME correct exception? its a value to much! JavaDoc: a
            // mandatory attribute value is not set
            Class<?> ec = InvalidContentException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);

        }
    }

    @Test
    public void testCreateWithNullInput() throws Exception {
        try {

            create(null);
        }
        catch (MissingMethodParameterException e) {
            return;
        }
        fail("Not expected exception");
    }

}
