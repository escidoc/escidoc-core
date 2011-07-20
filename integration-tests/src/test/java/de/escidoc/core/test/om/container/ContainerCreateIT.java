/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License
 * for the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.test.om.container;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.security.client.PWCallback;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class ContainerCreateIT extends ContainerTestBase {

    public static final String XPATH_CONTAINER_XLINK_HREF = "/container/@href";

    public static final String XPATH_CONTAINER_XLINK_TITLE = "/container/@title";

    private String path = "";

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        this.path += "/rest";
    }

    /**
     * Test successfully creating a Container with an Item and a Container as members.
     */
    @Test
    public void testCreateContainerWithMembers() throws Exception {

        String theContainerId = createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");

        String theItemId = createItemFromTemplate("escidoc_item_198_for_create.xml");

        String containerTemplate = getContainerTemplate("create_container_v1.1-forItemAndforContainer.xml");

        String xmlWithItem = containerTemplate.replaceAll("##ITEMID##", theItemId);
        String xmlWithItemAndContainer = xmlWithItem.replaceAll("##CONTAINERID##", theContainerId);

        String theContainerXml = create(xmlWithItemAndContainer);
        Document theContainer = getDocument(theContainerXml);

        selectSingleNodeAsserted(theContainer, "/container/struct-map//*[@objid='" + theItemId
            + "' or @href= '/ir/item/" + theItemId + "']");
        selectSingleNodeAsserted(theContainer, "/container/struct-map//*[@objid='" + theContainerId
            + "' or @href= '/ir/container/" + theContainerId + "']");
    }

    /**
     * Successful creation of a Container with empty content of an md-redord. SchemaException expected.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testConCr1() throws Exception {

        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTAINER_PATH + this.path, "create_container.xml");
        substitute(context, "/container/properties/name", getUniqueName("Container Name "));
        substitute(context, "/container/md-records/md-record[1]", "");
        String template = toString(context, false);

        create(template);
    }

    /**
     * Test if namespaces in meta data records of Container are still part of the representation after create.
     * <p/>
     * Issue INFR-947
     *
     * @throws Exception If anything fails.
     */
    @Ignore
    @Test
    public void containerMetadataNamespaces() throws Exception {

        String container = getTemplateAsString(TEMPLATE_CONTAINER_PATH + this.path, "container_issue_infr_947.xml");

        String createdContainer = create(container);

        // assert that namespace declarations of metadata are still present
        // after create
        assertTrue("Missing eterms namespace", createdContainer
            .contains("xmlns:eterms=\"http://purl.org/escidoc/metadata/terms/0.1/\""));
        assertTrue("Missing PURL", createdContainer.contains("xmlns:dc=\"http://purl.org/dc/elements/1.1\""));
    }

    /**
     * Test unexpected parser exception instead of InvalidXmlException during create (see issue INFR-911).
     *
     * @throws Exception Thrown if behavior is not as expected.
     */
    @Test(expected = InvalidXmlException.class)
    public void testInvalidXml() throws Exception {

        /*
         * The infrastructure has thrown an unexpected parser exception during
         * creation if a non XML datastructur is send (e.g. String).
         */
        create("laber-rababer");
    }

    /**
     * Test schema validation check (see issue INFR-1196).
     * 
     * @throws Exception
     *             Thrown if behavior is not as expected.
     */
    @Test
    public void testSchemaValidation() {
        final String handle = PWCallback.getHandle();

        try {
            PWCallback.resetHandle();
            System.out.println("create: " + create("<container/>"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            PWCallback.setHandle(handle);
        }
    }

    /**
     * https://www.escidoc.org/jira/browse/INFR-1096
     * <p/>
     * Create a container without a content-model-specific element.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateContainerWithoutContentModel() throws Exception {
        Document container =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTAINER_PATH + this.path, "create_container.xml");

        deleteNodes(container, XPATH_CONTAINER_PROPERTIES_CMS);
        create(toString(container, false));
    }

    @Test
    public void testOM_CCO_1_1() throws Exception {

        String container = getContainerTemplate("create_container_WithoutMembers_v1.1.xml");

        assertXmlValidContainer(container);
        final String theContainerXml = create(container);
        assertXmlValidContainer(theContainerXml);

        Document document = EscidocAbstractTest.getDocument(theContainerXml);
        String containerId = getObjidValue(document);

        assertXmlEquals("href value is wrong", document, XPATH_CONTAINER_XLINK_HREF, "/ir/container/" + containerId);
        Node containerTitle = selectSingleNode(document, XPATH_CONTAINER_XLINK_TITLE);
        assertNotNull(containerTitle);
        // assertFalse("container title is not set", containerTitle
        // .getTextContent().equals(""));
        assertXmlEquals("href value is wrong", document, "/container/md-records/@href ", "/ir/container/" + containerId
            + "/md-records");
        assertXmlEquals("href value is wrong", document, "/container/md-records/md-record[1]/@href ", "/ir/container/"
            + containerId + "/md-records/md-record/escidoc");
        assertXmlEquals("href value is wrong", document, "/container/properties/@href ", "/ir/container/" + containerId
            + "/properties");
        assertXmlEquals("href value is wrong", document, "/container/properties/version/@href ", "/ir/container/"
            + containerId + ":1");
        assertXmlEquals("href value is wrong", document, "/container/properties/latest-version/@href ",
            "/ir/container/" + containerId + ":1");
        Node creatorId = selectSingleNode(document, "/container/properties/created-by/@href");
        assertNotNull(creatorId.getTextContent());

        Node latestRelease = selectSingleNode(document, "/container/properties/latest-release");
        assertNull(latestRelease);
        Node itemRef = selectSingleNode(document, "/container/struct-map/member-ref-list/item-ref");
        assertNull(itemRef);
        Node containerRef = selectSingleNode(document, "/container/struct-map/member-ref-list/container-ref");
        assertNull(containerRef);

        Node modifiedDate = selectSingleNode(document, "/container/@last-modification-date");
        assertNotNull(modifiedDate);
        assertFalse("modified date is not set", modifiedDate.getTextContent().equals(""));
        assertXmlEquals("status value is wrong", document, "/container/properties/public-status", "pending");
        assertXmlEquals("pid value is wrong", document, "/container/properties/pid", "hdl:123/container456");
        Node createdDate = selectSingleNode(document, "/container/properties/creation-date");
        assertNotNull(createdDate);
        assertFalse("created date is not set", createdDate.getTextContent().equals(""));
        assertXmlEquals("lock-status value is wrong", document, "/container/properties/lock-status", "unlocked");
        assertXmlEquals("current version number is wrong", document, "/container/properties/version/number", "1");
        assertXmlEquals("current version status is wrong", document, "/container/properties/version/status", "pending");
        // assertXmlEquals("current version valid-status is wrong", document,
        // "/container/properties/version/valid-status", "valid");
        assertXmlEquals("current version date is wrong", document, "/container/properties/version/date", modifiedDate
            .getTextContent());
        assertXmlEquals("latest version number is wrong", document, "/container/properties/latest-version/number", "1");
        assertXmlEquals("latest version date is wrong", document, "/container/properties/latest-version/date",
            modifiedDate.getTextContent());
        Node creator = selectSingleNode(document, "/container/properties/created-by");
        assertNotNull(creator);
        Node creatorTitle = selectSingleNode(document, "/container/properties/created-by/@title");
        assertNotNull(creatorTitle.getTextContent());
    }

    /**
     * Test successfully creating container.
     */
    @Test
    public void testOM_CCO_1_2() throws Exception {

        String xmlData1 = getContainerTemplate("create_container_WithoutMembers_v1.1.xml");

        assertXmlValidContainer(xmlData1);
        final String theContainerXml1 = create(xmlData1);
        assertXmlValidContainer(theContainerXml1);

        String subContainerId1 = getObjidValue(theContainerXml1);

        final String theContainerXml2 = create(xmlData1);
        assertXmlValidContainer(theContainerXml2);

        String subContainerId2 = getObjidValue(theContainerXml2);

        String xmlData = getContainerTemplate("create_container_v1.1-forContainer.xml");

        String xmlWithContainer1 = xmlData.replaceAll("##CONTAINERID1##", subContainerId1);
        String xmlWithContainer2 = xmlWithContainer1.replaceAll("##CONTAINERID2##", subContainerId2);
        Document document = EscidocAbstractTest.getDocument(xmlWithContainer2);
        NodeList members = selectNodeList(document, "/container/struct-map/member-ref-list/member/container-ref/@href");
        final String theContainerXml = create(xmlWithContainer2);
        assertXmlValidContainer(theContainerXml);
        final Document createdDocument = EscidocAbstractTest.getDocument(theContainerXml);
        NodeList membersAfterCreate =
            selectNodeList(createdDocument, "/container/struct-map/member-ref-list/member/container-ref/@href");

        List<String> membersList = nodeList2List(members);
        List<String> membersListAfterCreate = nodeList2List(membersAfterCreate);

        assertListContentEqual("Member list does not contain the same IDs as struct map.", membersList,
            membersListAfterCreate);

        Node itemRef = selectSingleNode(createdDocument, "/container/struct-map/item");
        assertNull(itemRef);
    }

    /**
     * Test declining creating container with missing Context href.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_CCO_2_3() throws Exception {

        Document container =
            EscidocAbstractTest.getDocument(getContainerTemplate("create_container_WithoutMembers_v1.1.xml"));
        Node containerWithMissingContextHref =
            deleteAttribute(container, "/container/properties/context", XLINK_HREF_TEMPLATES);
        String containerWithMissingContextHrefXml = toString(containerWithMissingContextHref, false);

        try {
            create(containerWithMissingContextHrefXml);
            fail("No exception occured on create with missing context href.");

        }
        catch (final Exception e) {
            Class<?> ec = XmlCorruptedException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
            return;
        }

    }

    /**
     * Test declining creating container with Context href with wrong syntax.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_CCO_2_2() throws Exception {
        Document container =
            EscidocAbstractTest.getDocument(getContainerTemplate("create_container_WithoutMembers_v1.1.xml"));
        Node containerWithWrongContextHref = substitute(container, "/container/properties/context/@href", "/ir/bla");
        String containerWithWrongContextHrefXml = toString(containerWithWrongContextHref, false);
        try {
            create(containerWithWrongContextHrefXml);
        }
        catch (final ContextNotFoundException e) {
            return;
        }
        fail("Not expected exception");

    }

    /**
     * Test declining creating container with non existing context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_CCO_3() throws Exception {
        Document container =
            EscidocAbstractTest.getDocument(getContainerTemplate("create_container_WithoutMembers_v1.1.xml"));
        Node containerWithWrongContextId =
            substitute(container, "/container/properties/context/@href", "/ir/context/bla");
        String containerWithWrongContextIdXml = toString(containerWithWrongContextId, false);
        try {
            create(containerWithWrongContextIdXml);
        }
        catch (final ContextNotFoundException e) {
            return;
        }
        fail("Not expected exception");

    }

    /**
     * Test declining creating container with id of the context, which responses to another object type than context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_CCO_4() throws Exception {
        Document container =
            EscidocAbstractTest.getDocument(getContainerTemplate("create_container_WithoutMembers_v1.1.xml"));
        Node containerWithWrongContextObjectType =
            substitute(container, "/container/properties/context/@href", "/ctm/context/escidoc:persistent4");
        String containerWithWrongContextObjectTypeXml = toString(containerWithWrongContextObjectType, false);
        try {
            create(containerWithWrongContextObjectTypeXml);
        }
        catch (final ContextNotFoundException e) {
            return;
        }
        fail("Not expected exception");

    }

    /**
     * Test declining creating container without any md-record.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_CCO_5() throws Exception {
        Document container =
            EscidocAbstractTest.getDocument(getContainerTemplate("create_container_WithoutMembers_v1.1.xml"));

        Node attributeMdRecordName = selectSingleNode(container, "/container/md-records/md-record[1]/@name");
        String nameValue = attributeMdRecordName.getTextContent();
        Node containerWithoutEscidocMetadata = null;
        if (nameValue.equals("escidoc")) {
            containerWithoutEscidocMetadata = deleteElement(container, "/container/md-records/md-record[1]");
        }
        String containerWithoutEscidocMetadataXml = toString(containerWithoutEscidocMetadata, true);

        try {
            create(containerWithoutEscidocMetadataXml);
        }
        catch (final InvalidXmlException e) {
            return;
        }
        fail("Not expected exception");
    }

    /**
     * Test declining creating container with missing Escidoc Internal Metadata Set.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMCi2e() throws Exception {
        Document xmlContainer =
            EscidocAbstractTest.getDocument(getContainerTemplate("create_container_WithoutMembers_v1.1.xml"));

        // Node attribute =
        // selectSingleNode(xmlItemWithoutComponents,
        // "/item/md-records/md-record/[@name = 'escidoc']/@name");
        // "/item/md-records/md-record/publication");
        Node xmlContainerWithoutInternalMetadata =
            substitute(xmlContainer, "/container/md-records/md-record[@name = 'escidoc']/@name", "bla");
        String xmlContainerWithoutInternalMetadataXml = toString(xmlContainerWithoutInternalMetadata, true);

        Class<?> ec = MissingMdRecordException.class;
        try {
            create(xmlContainerWithoutInternalMetadataXml);
            EscidocAbstractTest.failMissingException(ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
    }

    /**
     * Test successfully creating container with two relations.
     *
     * @throws Exception Thrown if anything fails.
     */
    @Test
    public void testRelations() throws Exception {
        String containerXml1 = create(getContainerTemplate("create_container_WithoutMembers_v1.1.xml"));
        assertXmlValidContainer(containerXml1);
        String containerXml2 = create(getContainerTemplate("create_container_WithoutMembers_v1.1.xml"));
        assertXmlValidContainer(containerXml2);
        Document document1 = EscidocAbstractTest.getDocument(containerXml1);
        String createdContainerId1 = getObjidValue(document1);
        Document document2 = EscidocAbstractTest.getDocument(containerXml2);
        String createdContainerId2 = getObjidValue(document2);

        String href1 = "/ir/container/" + createdContainerId1;
        String href2 = "/ir/container/" + createdContainerId2;
        String containerForCreateWithRelationsXml =
            getContainerTemplate("create_container_WithoutMembers_v1.1_WithRelations.xml");

        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_ID1##", createdContainerId1);
        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_ID2##", createdContainerId2);
        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_HREF1##", href1);
        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_HREF2##", href2);
        Document containerForCreateWithRelations = EscidocAbstractTest.getDocument(containerForCreateWithRelationsXml);

        NodeList relations = selectNodeList(containerForCreateWithRelations, "/container/relations/relation");

        String xml = create(containerForCreateWithRelationsXml);

        NodeList relationsAfterCreate =
            selectNodeList(EscidocAbstractTest.getDocument(xml), "/container/relations/relation");

        assertXmlValidContainer(xml);
        assertEquals("Number of relations is wrong ", relations.getLength(), relationsAfterCreate.getLength());

    }

    /**
     * Test declining creating container with relations, whose targets references non existing resources.
     */
    @Test
    public void testRelationsWithWrongTarget() throws Exception {

        String createdContainerId1 = "bla1";
        String createdContainerId2 = "bla2";

        String href1 = "/ir/container/" + createdContainerId1;
        String href2 = "/ir/container/" + createdContainerId2;
        String containerForCreateWithRelationsXml =
            getContainerTemplate("create_container_WithoutMembers_v1.1_WithRelations.xml");

        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_ID1##", createdContainerId1);
        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_ID2##", createdContainerId2);
        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_HREF1##", href1);
        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_HREF2##", href2);

        try {
            create(containerForCreateWithRelationsXml);
            fail("No exception occured on container create with relations, which "
                + " references non existing targets.");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("ReferencedResourceNotFoundException expected.",
                ReferencedResourceNotFoundException.class, e);
        }

    }

    /**
     * Test declining creating container with relations, whose target ids containing a version number.
     */
    @Test
    public void testRelationsWithTargetContainingVersionNumber() throws Exception {
        String createdContainerId1 = "escidoc:123:2";
        String createdContainerId2 = "escidoc:123:3";

        String href1 = "/ir/container/" + createdContainerId1;
        String href2 = "/ir/container/" + createdContainerId2;
        String containerForCreateWithRelationsXml =
            getContainerTemplate("create_container_WithoutMembers_v1.1_WithRelations.xml");

        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_ID1##", createdContainerId1);
        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_ID2##", createdContainerId2);
        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_HREF1##", href1);
        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_HREF2##", href2);
        try {
            create(containerForCreateWithRelationsXml);
            fail("No exception occured on container crate with relations, which "
                + " target ids containing a version number.");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("InvalidContentException expected.", InvalidContentException.class,
                e);
        }
    }

    /**
     * Test declining creating container with relations with non existing predicate.
     */
    @Test
    public void testRelationsWithWrongPredicate() throws Exception {

        String createdContainerId1 = createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        String createdContainerId2 = createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");

        String href1 = "/ir/container/" + createdContainerId1;
        String href2 = "/ir/container/" + createdContainerId2;

        String containerForCreateWithRelationsXml =
            getContainerTemplate("create_container_WithoutMembers_v1.1_WithRelations.xml");

        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_ID1##", createdContainerId1);
        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_ID2##", createdContainerId2);
        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_HREF1##", href1);
        containerForCreateWithRelationsXml =
            containerForCreateWithRelationsXml.replaceAll("##CONTAINER_HREF2##", href2);
        Document containerForCreateWithRelations = EscidocAbstractTest.getDocument(containerForCreateWithRelationsXml);
        Node relationPredicate =
            selectSingleNode(containerForCreateWithRelations, "/container/relations/relation[1]/@predicate");
        relationPredicate.setNodeValue("http://www.bla.de#bla");

        String containerXml = toString(containerForCreateWithRelations, true);

        try {
            create(containerXml);
            fail("No exception occured on container create with relations, which "
                + " references non existing predicate.");
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("RelationPredicateNotFoundException expected.",
                RelationPredicateNotFoundException.class, e);
        }

    }

    /**
     * Test declining creation of Container with providing reference to context with invalid href (substring context not
     * in href).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_CCO_13_1_rest() throws Exception {

        final Class<?> ec = ContextNotFoundException.class;

        Document toBeCreatedDocument =
            EscidocAbstractTest.getDocument(getContainerTemplate("create_container_WithoutMembers_v1.1.xml"));

        String href =
            selectSingleNodeAsserted(toBeCreatedDocument, XPATH_CONTAINER_CONTEXT_XLINK_HREF).getTextContent();
        href = href.replaceFirst(Constants.CONTEXT_BASE_URI, Constants.ORGANIZATIONAL_UNIT_BASE_URI);
        substitute(toBeCreatedDocument, XPATH_CONTAINER_CONTEXT_XLINK_HREF, href);

        String toBeCreatedXml = toString(toBeCreatedDocument, true);

        try {
            create(toBeCreatedXml);
            EscidocAbstractTest.failMissingException("Creating Container with invalid object href not declined. ", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Creating Container with invalid object href not declined,"
                + " properly. ", ec, e);
        }
    }

    /**
     * Test declining creation of Container with providing reference to content-model with invalid href (substring
     * content-model not in href).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_CCO_13_2_rest() throws Exception {

        final Class<?> ec = ContentModelNotFoundException.class;

        Document toBeCreatedDocument =
            EscidocAbstractTest.getDocument(getContainerTemplate("create_container_WithoutMembers_v1.1.xml"));

        String href =
            selectSingleNodeAsserted(toBeCreatedDocument, XPATH_CONTAINER_CONTENT_TYPE_XLINK_HREF).getTextContent();
        href = href.replaceFirst(Constants.CONTENT_MODEL_BASE_URI, Constants.ORGANIZATIONAL_UNIT_BASE_URI);
        substitute(toBeCreatedDocument, XPATH_CONTAINER_CONTENT_TYPE_XLINK_HREF, href);

        String toBeCreatedXml = toString(toBeCreatedDocument, true);

        try {
            create(toBeCreatedXml);
            EscidocAbstractTest.failMissingException("Creating Container with invalid object href not declined. ", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Creating Container with invalid object href not declined,"
                + " properly. ", ec, e);
        }
    }

    /**
     * Test declinig creating an container without specifying the content model id, using data provided for issue 365.
     *
     * @throws Exception Thrown if anythinf fails.
     */
    @Test
    public void testOM_CCO_issue365() throws Exception {

        final Class<?> ec = XmlCorruptedException.class;

        Document toBeCreatedDocument =
            EscidocAbstractTest.getDocument(getContainerTemplate("create_container_WithoutMembers_v1.1.xml"));
        deleteElement(toBeCreatedDocument, XPATH_CONTAINER_CONTENT_MODEL);
        addAfter(toBeCreatedDocument, XPATH_CONTAINER_CONTEXT, createElementNode(toBeCreatedDocument, SREL_NS_URI,
            "srel", NAME_CONTENT_MODEL, null));

        String toBeCreatedXml = toString(toBeCreatedDocument, true);

        try {
            create(toBeCreatedXml);
            EscidocAbstractTest.failMissingException(
                "Creating container with empty content-model element not declined.", ec);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Creating container with empty content-model element not declined"
                + ", properly", ec, e);
        }

    }

    /**
     * Test successfully creating of an container with 2 md-records.
     */
    @Test
    public void testCreateContainerWith2Mdrecords() throws Exception {
        Document xmlContainer =
            EscidocAbstractTest
                .getDocument(getContainerTemplate("create_container_2_Md_Records_WithoutMembers_v1.1.xml"));
        NodeList mdrecords = selectNodeList(xmlContainer, "/container/md-records/md-record");
        String containerWithoutAdminDescriptorXml = toString(xmlContainer, false);
        assertXmlValidContainer(containerWithoutAdminDescriptorXml);

        final String createdXml = create(containerWithoutAdminDescriptorXml);

        assertXmlValidContainer(createdXml);
        final Document createdDocument = EscidocAbstractTest.getDocument(createdXml);

        NodeList mdrecordsAfterCreate = selectNodeList(createdDocument, "/container/md-records/md-record");
        assertEquals(mdrecords.getLength(), mdrecordsAfterCreate.getLength());

    }
}
