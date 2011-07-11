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
package de.escidoc.core.test.aa;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.remote.application.violated.RoleInUseViolationException;
import de.escidoc.core.common.exceptions.remote.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.remote.system.WebserverSystemException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.aa.RoleClient;
import de.escidoc.core.test.common.client.servlet.interfaces.ResourceHandlerClientInterface;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test suite for the resource role.
 *
 * @author Torsten Tetteroo
 *         <p/>
 *         changes for schema version 0.3: <ul> <li>Replaced tests AA_CRO-6, AA_CRO-7, AA_CRO-8 by AA_CRO-6-soap,
 *         AA_CRO-7-soap, AA_CRO-8-soap</li> <li>Replaced tests AA_CRO-9 by AA_CRO-9-rest</li> <li>Replaced tests
 *         AA_CRO-10, AA_CRO-11, AA_CRO-12, AA_CRO-15, AA_CRO-16, AA_CRO-17, by AA_CRO-6-rest and AA_CRO-6-soap</li>
 *         <li>Replaced AA_RRR-* tests by AA_RRR-*-rest tests.</li> <li>Replaced AA_URO-6, AA_URO-7, AA_URO-8 by
 *         AA_URO-32-rest</li> <li>Replaced AA_URO-9 by AA_URO-32-soap</li> <li>Replaced AA_URO-11, AA_URO-12, AA_URO-24
 *         by AA_URO-32-rest and AA_URO-32-soap</li> <li>Replaced AA_URO-13, AA_URO-14, AA_URO-15, testAAUro25,
 *         testAAUro26, testAAUro27 by AA_URO-xy-rest</li> <li>Replaced AA_URO-16, AA_URO-28 by AA_URO-xy-soap</li>
 *         <li>Removed AA_URO-21 and AA_URO-22 (umlimited flag not evaluated, anymore)</li> </ul>
 */
public class RoleIT extends AaTestBase {

    public static final String RDF_RESOURCE_ROLE = "http://www.escidoc.de/core/01/resources/Role";

    public static final String XPATH_RESOURCES = "/resources";

    public static final String NAME_POLICY_SET_ID = "PolicySetId";

    public static final String NAME_POLICY_COMBINING_ALG_ID = "PolicyCombiningAlgId";

    public static final String NAME_RELATION_ATTRIBUTE_ID = "relation-attribute-id";

    public static final String NAME_SCOPE = "scope";

    public static final String NAME_SCOPE_DEF = "scope-def";

    public static final String NAME_RESOURCE_TYPE = "resource-type";

    public static final String XPATH_RESOURCES_XLINK_TITLE = XPATH_RESOURCES + "/@" + EscidocTestBase.NAME_TITLE;

    public static final String XPATH_RESOURCES_HREF = XPATH_RESOURCES + "/@" + EscidocTestBase.NAME_HREF;

    public static final String XPATH_RESOURCES_XLINK_TYPE = XPATH_RESOURCES + "/@" + EscidocTestBase.NAME_TYPE;

    public static final String XPATH_RESOURCES_OBJID = XPATH_RESOURCES + "/@" + EscidocTestBase.NAME_OBJID;

    public static final String NAME_UNLIMITED = "unlimited";

    public static final String XPATH_ROLE = "/role";

    public static final String XPATH_ROLE_RESOURCES = XPATH_ROLE + XPATH_RESOURCES;

    public static final String XPATH_ROLE_XLINK_TYPE = XPATH_ROLE + "/@type";

    public static final String XPATH_ROLE_XLINK_TITLE = XPATH_ROLE + "/@title";

    public static final String XPATH_ROLE_XLINK_HREF = XPATH_ROLE + "/@href";

    public static final String XPATH_ROLE_OBJID = XPATH_ROLE + "/@objid";

    public static final String XPATH_ROLE_LAST_MOD_DATE = XPATH_ROLE + "/@last-modification-date";

    public static final String XPATH_ROLE_PROPERTIES = XPATH_ROLE + "/properties";

    public static final String XPATH_ROLE_MODIFIED_BY = XPATH_ROLE_PROPERTIES + "/" + EscidocTestBase.NAME_MODIFIED_BY;

    public static final String XPATH_ROLE_MODIFIED_BY_OBJID =
        XPATH_ROLE_MODIFIED_BY + "/@" + EscidocTestBase.NAME_OBJID;

    public static final String XPATH_ROLE_MODIFIED_BY_XLINK_HREF =
        XPATH_ROLE_MODIFIED_BY + "/@" + EscidocTestBase.NAME_HREF;

    public static final String XPATH_ROLE_MODIFIED_BY_XLINK_TITLE =
        XPATH_ROLE_MODIFIED_BY + "/@" + EscidocTestBase.NAME_TITLE;

    public static final String XPATH_ROLE_MODIFIED_BY_XLINK_TYPE =
        XPATH_ROLE_MODIFIED_BY + "/@" + EscidocTestBase.NAME_TYPE;

    public static final String XPATH_ROLE_CREATION_DATE = XPATH_ROLE_PROPERTIES + "/creation-date";

    public static final String XPATH_ROLE_CREATED_BY = XPATH_ROLE_PROPERTIES + "/created-by";

    public static final String XPATH_ROLE_CREATED_BY_XLINK_TYPE = XPATH_ROLE_CREATED_BY + "/@type";

    public static final String XPATH_ROLE_CREATED_BY_XLINK_TITLE = XPATH_ROLE_CREATED_BY + "/@title";

    public static final String XPATH_ROLE_CREATED_BY_XLINK_HREF = XPATH_ROLE_CREATED_BY + "/@href";

    public static final String XPATH_ROLE_CREATED_BY_OBJID = XPATH_ROLE_CREATED_BY + "/@objid";

    public static final String XPATH_ROLE_DESCRIPTION = XPATH_ROLE_PROPERTIES + "/description";

    public static final String XPATH_ROLE_NAME = XPATH_ROLE_PROPERTIES + "/name";

    public static final String XPATH_ROLE_SCOPE = XPATH_ROLE + "/" + NAME_SCOPE;

    public static final String XPATH_ROLE_SCOPE_UNLIMITED = XPATH_ROLE_SCOPE + "/@" + NAME_UNLIMITED;

    public static final String XPATH_ROLE_SCOPE_DEF = XPATH_ROLE_SCOPE + "/" + NAME_SCOPE_DEF;

    public static final String XPATH_ROLE_SCOPE_DEF_RESOURCE_TYPE = XPATH_ROLE_SCOPE_DEF + "/@" + NAME_RESOURCE_TYPE;

    public static final String XPATH_ROLE_SCOPE_DEF_ATTRIBUTE_ID =
        XPATH_ROLE_SCOPE_DEF + "/@" + NAME_RELATION_ATTRIBUTE_ID;

    public static final String XPATH_ROLE_POLICY = XPATH_ROLE + "/" + "Policy";

    public static final String XPATH_ROLE_POLICY_SET = XPATH_ROLE + "/" + "PolicySet";

    public static final String XPATH_ROLE_POLICY_OR_POLICY_SET = XPATH_ROLE_POLICY + "|" + XPATH_ROLE_POLICY_SET;

    public static final String XPATH_ROLE_POLICY_SET_ID = XPATH_ROLE_POLICY_SET + "/@" + NAME_POLICY_SET_ID;

    public static final String XPATH_ROLE_POLICY_COMBINING_ALG_ID =
        XPATH_ROLE_POLICY_SET + "/@" + NAME_POLICY_COMBINING_ALG_ID;

    public static final String XPATH_ROLE_PROPERTIES_XLINK_TYPE = XPATH_ROLE_PROPERTIES + PART_XLINK_TYPE;

    public static final String XPATH_ROLE_PROPERTIES_XLINK_TITLE = XPATH_ROLE_PROPERTIES + PART_XLINK_TITLE;

    public static final String XPATH_ROLE_PROPERTIES_XLINK_HREF = XPATH_ROLE_PROPERTIES + PART_XLINK_HREF;

    public static final String XPATH_ROLE_RESOURCES_XLINK_TYPE = XPATH_ROLE_RESOURCES + PART_XLINK_TYPE;

    public static final String XPATH_ROLE_RESOURCES_XLINK_TITLE = XPATH_ROLE_RESOURCES + PART_XLINK_TITLE;

    public static final String XPATH_ROLE_RESOURCES_XLINK_HREF = XPATH_ROLE_RESOURCES + PART_XLINK_HREF;

    public static final String XPATH_ROLE_XML_BASE = XPATH_ROLE + PART_XML_BASE;

    public static final String XPATH_ROLE_LIST = "/" + "role-list";

    public static final String XPATH_ROLE_LIST_ROLE = XPATH_ROLE_LIST + "/" + NAME_ROLE;

    public static final String XPATH_SRW_ROLE_LIST_ROLE = XPATH_SRW_RESPONSE_OBJECT + NAME_ROLE;

    private final RoleClient roleClient;

    /**
     * The constructor.
     *
     * @throws Exception If anything fails.
     */
    public RoleIT() throws Exception {
        this.roleClient = new RoleClient();
    }

    /**
     * @return the roleClient
     */
    @Override
    public RoleClient getRoleClient() {

        return roleClient;
    }

    /**
     * @return the roleClient
     */
    @Override
    public ResourceHandlerClientInterface getClient() {

        return getRoleClient();
    }

    /**
     * Inserts a unique role name into the provided document by adding the current timestamp to the contained role
     * name.
     *
     * @param document The document.
     * @return The inserted role name.
     * @throws Exception If anything fails.
     */
    protected String insertUniqueRoleName(final Document document) throws Exception {

        assertXmlExists("No role name found in template data. ", document, XPATH_ROLE_NAME);
        final Node nameNode = selectSingleNode(document, XPATH_ROLE_NAME);
        String name = nameNode.getTextContent().trim();
        name += System.currentTimeMillis();

        nameNode.setTextContent(name);

        return name;
    }

    /**
     * Successfully creates a Role.
     *
     * @param templateName The name of the template.
     * @return Returns the role document.
     * @throws Exception If anything fails
     */
    protected Document createSuccessfully(final String templateName) throws Exception {

        String newRoleXML = EscidocAbstractTest.getTemplateAsString(TEMPLATE_ROLE_PATH, templateName);
        assertXmlValidRole(newRoleXML);
        Document toBeCreatedDocument = EscidocAbstractTest.getDocument(newRoleXML);
        insertUniqueRoleName(toBeCreatedDocument);

        final String toBeCreatedXml = toString(toBeCreatedDocument, false);

        String createdXml = null;
        try {
            createdXml = create(toBeCreatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        return assertXmlValidRetrievedRole(createdXml, toBeCreatedXml, startTimestamp, startTimestamp, false);
    }

    /**
     * Asserts that the provided document holds an valid xml representation of a role and all element and attributes
     * that are expected in a retrieved representation exist.
     *
     * @param toBeAssertedXml         The created/updated role.
     * @param originalXml             The template role used to create/update the organizational unit. If this parameter
     *                                is <code>null</code>, no check with the original data is performed.
     * @param timestampBeforeCreation A timestamp before the creation has been started. This is used to check the
     *                                creation date.
     * @param timestampBeforeLastModification
     *                                A timestamp before the last modification has been started. This is used to check
     *                                the last modification date.
     * @param assertCreationDate      Flag to indicate if the creation-date and created-by values shall be asserted
     *                                (<code>true</code>) or not ( <code>false</code>).
     * @return Returns the document representing the provided xml data.
     * @throws Exception If anything fails
     */
    public Document assertXmlValidRetrievedRole(
        final String toBeAssertedXml, final String originalXml, final String timestampBeforeCreation,
        final String timestampBeforeLastModification, final boolean assertCreationDate) throws Exception {

        final String msg = "Asserting retrieved role failed. ";

        // validate xml
        assertXmlValidRole(toBeAssertedXml);

        final Document toBeAssertedDocument = EscidocAbstractTest.getDocument(toBeAssertedXml);

        // assert root
        assertRootElement(msg + "Root element failed. ", toBeAssertedDocument, XPATH_ROLE, Constants.ROLE_BASE_URI,
            timestampBeforeLastModification);

        // assert properties
        assertPropertiesElementUnversioned("Properties failed. ", toBeAssertedDocument, XPATH_ROLE_PROPERTIES,
            timestampBeforeCreation);

        // name
        assertXmlExists(msg + "Missing name", toBeAssertedDocument, XPATH_ROLE_NAME);

        // assert resources. This must not exist, as it is not defined in SOAP
        // and it is empty and therefore must not exists in REST, too.
        assertXmlNotExists(msg + "Unexpected resources. ", toBeAssertedDocument, XPATH_ROLE_RESOURCES);

        // assert scope
        final boolean toBeAssertedUnlimitedValue =
            Boolean.parseBoolean(getAttributeValue(toBeAssertedDocument, XPATH_ROLE_SCOPE, NAME_UNLIMITED));
        if (!toBeAssertedUnlimitedValue) {
            assertXmlExists(msg + "Expected at least one scope-def element", toBeAssertedDocument, XPATH_ROLE_SCOPE_DEF);
        }
        else {
            assertXmlNotExists("Did not expect a scope-def element", toBeAssertedDocument, XPATH_ROLE_SCOPE_DEF);
        }

        // assert policies
        assertXmlExists(msg + "Missing policy (set).", toBeAssertedDocument, XPATH_ROLE_POLICY_OR_POLICY_SET);

        if (originalXml != null) {
            Document originalDocument = EscidocAbstractTest.getDocument(originalXml);

            if (assertCreationDate) {
                final String expectedCreationDate = getCreationDateValue(originalDocument);
                if (expectedCreationDate != null) {

                    // creation-date
                    assertXmlEquals(msg + "creation date mismatch, ", toBeAssertedDocument, XPATH_ROLE_CREATION_DATE,
                        expectedCreationDate);

                    // created-by
                    assertCreatedBy(msg + "created-by mismatch, ", originalDocument, toBeAssertedDocument);
                }
            }

            // name
            assertXmlEquals(msg + "name mismatch, ", originalDocument, toBeAssertedDocument, XPATH_ROLE_NAME);

            // description
            assertXmlEquals(msg + "description mismatch, ", originalDocument, toBeAssertedDocument,
                XPATH_ROLE_DESCRIPTION);

            // scope-defs
            assertXmlEquals(msg + "scope(-defs) mismatch, ", originalDocument, toBeAssertedDocument,
                XPATH_ROLE_SCOPE_DEF);

            // policy (set)
            assertXmlEquals(msg + "Policy(Set) mismatch, ", originalDocument, toBeAssertedDocument,
                XPATH_ROLE_POLICY_OR_POLICY_SET);
        }

        return toBeAssertedDocument;
    }

    /**
     * Retrieves the role identified by the provided id.
     *
     * @param id The role id.
     * @return Returns the retrieved document.
     * @throws Exception Thrown if anything fails.
     */
    protected Document retrieveSuccessfully(final String id) throws Exception {
        String retrievedXml = null;
        try {
            retrievedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieval of role failed.", e);
        }
        return assertXmlValidRetrievedRole(retrievedXml, null, null, null, false);
    }

    /**
     * Test successful creation of Role.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro1() throws Exception {

        Document createdDocument = createSuccessfully("role_for_create.xml");

        assertEquals("Creation date and last modification date are different. ", assertCreationDateExists("",
            createdDocument), getLastModificationDateValue(createdDocument));

        final String objid = getObjidValue(createdDocument);
        Document retrievedDocument = retrieveSuccessfully(objid);
        assertXmlEquals("Retrieved document not the same like the created one", createdDocument, retrievedDocument);
    }

    /**
     * Test successful creation of Role "FeePayer".
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro1_2() throws Exception {

        Document createdDocument = createSuccessfully("role-fee-payer.xml");

        assertEquals("Creation date and last modification date are different. ", assertCreationDateExists("",
            createdDocument), getLastModificationDateValue(createdDocument));

        final String objid = getObjidValue(createdDocument);
        Document retrievedDocument = retrieveSuccessfully(objid);
        assertXmlEquals("Retrieved document not the same like the created one", createdDocument, retrievedDocument);
    }

    /**
     * Test successful creation of Role.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro2() throws Exception {

        Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_unlimited_for_create.xml");
        Document createdDocument = createSuccessfully("role_unlimited_for_create.xml");

        assertEquals("Creation date and last modification date are different. ", assertCreationDateExists("",
            createdDocument), getLastModificationDateValue(createdDocument));
        assertXmlEquals("scope-defs are different. ", toBeCreatedDocument, createdDocument, XPATH_ROLE_SCOPE);
        assertXmlEquals("policies are different. ", toBeCreatedDocument, createdDocument, XPATH_ROLE_POLICY_SET);

        Document retrievedDocument = retrieveSuccessfully(getObjidValue(createdDocument));
        assertXmlEquals("Retrieved document not the same like the created one", createdDocument, retrievedDocument);
    }

    /**
     * Test declining creation of Role with corrupted XML data.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro3() throws Exception {

        try {
            create("<Corrupt XML data");
            EscidocAbstractTest.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining creation of Role without providing XML data.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro4() throws Exception {

        try {
            create(null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining creation of Role with existing role name.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro5() throws Exception {

        Document createdDocument = createSuccessfully("role_for_create.xml");
        final String roleName = selectSingleNode(createdDocument, XPATH_ROLE_NAME).getTextContent().trim();
        Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_for_create.xml");
        final Node nameNode = selectSingleNode(toBeCreatedDocument, XPATH_ROLE_NAME);
        nameNode.setTextContent(roleName);

        try {
            create(toString(toBeCreatedDocument, false));
            EscidocAbstractTest.failMissingException(UniqueConstraintViolationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(UniqueConstraintViolationException.class, e);
        }
    }

    /**
     * Test declining creation of Role with invalid XACML-Policy.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro6() throws Exception {

        Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_cone_invalid.xml");
        final Node nameNode = selectSingleNode(toBeCreatedDocument, XPATH_ROLE_NAME);
        nameNode.setTextContent(nameNode.getTextContent() + System.currentTimeMillis());

        try {
            create(toString(toBeCreatedDocument, false));
            EscidocAbstractTest.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining creation of Role without XACML-Policy.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro7() throws Exception {

        Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_without_policy.xml");
        final Node nameNode = selectSingleNode(toBeCreatedDocument, XPATH_ROLE_NAME);
        nameNode.setTextContent(nameNode.getTextContent() + System.currentTimeMillis());

        try {
            create(toString(toBeCreatedDocument, false));
            EscidocAbstractTest.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining creation of Role without providing scope element.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro13() throws Exception {

        Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_for_create.xml");
        deleteElement(toBeCreatedDocument, XPATH_ROLE_SCOPE);
        insertUniqueRoleName(toBeCreatedDocument);

        try {
            create(toString(toBeCreatedDocument, false));
            EscidocAbstractTest.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining creation of Role without providing resource type attribute in scope-def element.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro14() throws Exception {

        Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_for_create.xml");
        deleteAttribute(toBeCreatedDocument, XPATH_ROLE_SCOPE_DEF, NAME_RESOURCE_TYPE);
        insertUniqueRoleName(toBeCreatedDocument);

        try {
            create(toString(toBeCreatedDocument, false));
            EscidocAbstractTest.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining creation of Role with forbidden name.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro18() throws Exception {

        Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_for_create.xml");
        Node nameNode = selectSingleNode(toBeCreatedDocument, XPATH_ROLE_NAME);
        nameNode.setTextContent("Default-User");

        try {
            final String toBeCreatedXml = toString(toBeCreatedDocument, false);
            create(toBeCreatedXml);
            EscidocAbstractTest.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining creation of Role with attributeId but without attributeObjectType.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro19() throws Exception {

        try {
            create(EscidocAbstractTest
                .getTemplateAsString(TEMPLATE_ROLE_PATH, "role_without_attribute_object_type.xml"));
            EscidocAbstractTest.failMissingException(WebserverSystemException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(WebserverSystemException.class, e);
        }
    }

    /**
     * Test declining creation of Role with wrong attributeObjectType.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro20() throws Exception {

        try {
            create(EscidocAbstractTest.getTemplateAsString(TEMPLATE_ROLE_PATH,
                "role_with_wrong_attribute_object_type.xml"));
            EscidocAbstractTest.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test successful delete of Role.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAADro1() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        final String id = getObjidValue(toBeUpdatedDocument);

        try {
            delete(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }

        // try to retrieve the deleted role
        try {
            retrieve(id);
            EscidocAbstractTest.failMissingException(RoleNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(RoleNotFoundException.class, e);
        }
    }

    /**
     * Test deleting a Role with providing an unknown role id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAADro2() throws Exception {

        try {
            delete(UNKNOWN_ID);
            EscidocAbstractTest.failMissingException(RoleNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(RoleNotFoundException.class, e);
        }
    }

    /**
     * Test deleting a Role with providing an id of an existing resource of another type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAADro2_2() throws Exception {

        try {
            delete(CONTEXT_ID);
            EscidocAbstractTest.failMissingException(RoleNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(RoleNotFoundException.class, e);
        }
    }

    /**
     * Test declining deleting a Role without providing a role id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAADro3() throws Exception {

        try {
            delete(null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining delete of Role that is referenced by a role grant.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAADro4() throws Exception {

        final String id = "escidoc:role-depositor";

        try {
            delete(id);
            EscidocAbstractTest.failMissingException(RoleInUseViolationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(RoleInUseViolationException.class, e);
        }

        // try to retrieve the role
        try {
            retrieve(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
    }

    /**
     * Test declining delete of Role that is forbidden to be accessed.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAADro5() throws Exception {

        final String id = "escidoc:role-default-user";

        try {
            delete(id);
            EscidocAbstractTest.failMissingException(RoleNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(RoleNotFoundException.class, e);
        }
    }

    /**
     * Test successfully retrieving a Role by id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARro1() throws Exception {

        final Document toBeRetrievedDocument = createSuccessfully("role_for_create.xml");
        final String id = getObjidValue(toBeRetrievedDocument);

        Document retrievedDocument = null;
        try {
            retrievedDocument = retrieveSuccessfully(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieveing existing role by its id failed. ", e);
        }
        assertXmlEquals("Retrieved document not the same like the created one", toBeRetrievedDocument,
            retrievedDocument);
    }

    /**
     * Test successfully retrieving a Role by its name.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARro1_2() throws Exception {

        final Document toBeRetrievedDocument = createSuccessfully("role_for_create.xml");
        final String name = getNameValue(toBeRetrievedDocument);

        Document retrievedDocument = null;
        try {
            retrievedDocument = retrieveSuccessfully(name);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieveing existing role by its name failed. ", e);
        }
        assertXmlEquals("Retrieved document not the same like the created one", toBeRetrievedDocument,
            retrievedDocument);
    }

    /**
     * Test retrieving a Role with providing an unknown role id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARro2() throws Exception {

        try {
            retrieve(UNKNOWN_ID);
            EscidocAbstractTest.failMissingException(RoleNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(RoleNotFoundException.class, e);
        }
    }

    /**
     * Test retrieving a Role with providing an id of an existing resource of another type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARro2_2() throws Exception {

        try {
            retrieve(CONTEXT_ID);
            EscidocAbstractTest.failMissingException(RoleNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(RoleNotFoundException.class, e);
        }
    }

    /**
     * Test retrieving a Role without providing a role id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARro3() throws Exception {

        try {
            retrieve(null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test successfully retrieving the administrator role.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARro5_1() throws Exception {

        final String id = "escidoc:role-administrator";

        String retrievedXml = null;
        try {
            retrievedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieval of role failed.", e);
        }
        assertXmlValidRetrievedRole(retrievedXml, null, null, null, false);
        // TODO: add some assertions
    }

    /**
     * Test successfully retrieving the depositor role.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARro5_3() throws Exception {

        final String id = "escidoc:role-depositor";

        retrieveSuccessfully(id);
        // TODO: add some assertions
    }

    /**
     * Test successfully retrieving the metadata editor role.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARro5_4() throws Exception {

        final String id = "escidoc:role-md-editor";

        retrieveSuccessfully(id);
        // TODO: add some assertions
    }

    /**
     * Test successfully retrieving the moderator role.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARro5_5() throws Exception {

        final String id = "escidoc:role-moderator";

        retrieveSuccessfully(id);
        // TODO: add some assertions
    }

    /**
     * Test successfully retrieving the system administrator role.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARro5_6() throws Exception {

        final String id = "escidoc:role-system-administrator";

        retrieveSuccessfully(id);
        // TODO: add some assertions
    }

    /**
     * Test successfully retrieving the system inspector role.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARro5_7() throws Exception {

        final String id = "escidoc:role-system-inspector";

        retrieveSuccessfully(id);
        // TODO: add some assertions
    }

    /**
     * Test successful update of Role.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro1() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        final String id = getObjidValue(toBeUpdatedDocument);

        final Node resourceTypeAttr = selectSingleNode(toBeUpdatedDocument, XPATH_ROLE_SCOPE_DEF_RESOURCE_TYPE);
        resourceTypeAttr.setTextContent("user-account");

        final Node policySetIdAttr = selectSingleNode(toBeUpdatedDocument, XPATH_ROLE_POLICY_SET_ID);
        policySetIdAttr.setTextContent("changedId");

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        final String timestampBeforeLastModification = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidRetrievedRole(updatedXml, toBeUpdatedXml, startTimestamp, timestampBeforeLastModification, true);

        String retrievedXml = null;
        try {
            retrievedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieve of updated role failed. ", e);
        }

        assertEquals("Retrieved differs from updated. ", updatedXml, retrievedXml);
    }

    /**
     * Test declining updating a Role with corrupted XML data.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro2() throws Exception {

        try {
            create("<Corrupt XML data");
            EscidocAbstractTest.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlCorruptedException.class, e);
        }
    }

    /**
     * Test updating a Role with providing an unknown role id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro3() throws Exception {

        Document createdDocument = createSuccessfully("role_for_create.xml");
        Document toBeUpdatedDocument = createdDocument;

        try {
            update(UNKNOWN_ID, toString(toBeUpdatedDocument, false));
            EscidocAbstractTest.failMissingException(RoleNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(RoleNotFoundException.class, e);
        }
    }

    /**
     * Test updating a Role with providing an id of an existing resource of another type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro3_2() throws Exception {

        Document createdDocument = createSuccessfully("role_for_create.xml");
        Document toBeUpdatedDocument = createdDocument;

        try {
            update(CONTEXT_ID, toString(toBeUpdatedDocument, false));
            EscidocAbstractTest.failMissingException(RoleNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(RoleNotFoundException.class, e);
        }
    }

    /**
     * Test declining updating a Role without providing role id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro4() throws Exception {

        Document createdDocument = createSuccessfully("role_for_create.xml");
        Document toBeUpdatedDocument = createdDocument;

        try {
            update(null, toString(toBeUpdatedDocument, false));
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining updating a Role without providing XML data.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro5() throws Exception {

        Document createdDocument = createSuccessfully("role_for_create.xml");

        try {
            update(getObjidValue(createdDocument), null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining update of Role with invalid XACML-Policy.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro6() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        final String id = getObjidValue(toBeUpdatedDocument);

        final Node policyCombiningAlgIdAttr = selectSingleNode(toBeUpdatedDocument, XPATH_ROLE_POLICY_COMBINING_ALG_ID);
        policyCombiningAlgIdAttr.setTextContent("testing-algorithm");

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        try {
            update(id, toBeUpdatedXml);
            EscidocAbstractTest.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlCorruptedException.class, e);
        }
        String roleXml = retrieve(id);
        Document roleDocument = getDocument(roleXml);
        assertNotEquals("PolicyCombiningAlg has wrong value", "testing-algorithm", selectSingleNode(roleDocument,
            XPATH_ROLE_POLICY_COMBINING_ALG_ID).getTextContent());
    }

    /**
     * Test declining update of Role without XACML-Policy.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro7() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        final String id = getObjidValue(toBeUpdatedDocument);

        deleteElement(toBeUpdatedDocument, XPATH_ROLE_POLICY_SET);
        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        try {
            update(id, toBeUpdatedXml);
            EscidocAbstractTest.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining updating a Role without providing last modification date.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro10() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE, EscidocTestBase.NAME_LAST_MODIFICATION_DATE);

        try {
            update(getObjidValue(toBeUpdatedDocument), toString(toBeUpdatedDocument, false));
            EscidocAbstractTest.failMissingException(MissingAttributeValueException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingAttributeValueException.class, e);
        }
    }

    /**
     * Test declining updating a Role without providing scope-def resource-type attribute.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro17() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_SCOPE_DEF, NAME_RESOURCE_TYPE);

        try {
            update(getObjidValue(toBeUpdatedDocument), toString(toBeUpdatedDocument, false));
            EscidocAbstractTest.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining updating a Role without providing policy.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro18() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        deleteElement(toBeUpdatedDocument, XPATH_ROLE_POLICY_SET);

        try {
            update(getObjidValue(toBeUpdatedDocument), toString(toBeUpdatedDocument, false));
            EscidocAbstractTest.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining updating a Role without providing scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro19() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        deleteElement(toBeUpdatedDocument, XPATH_ROLE_SCOPE);

        try {
            update(getObjidValue(toBeUpdatedDocument), toString(toBeUpdatedDocument, false));
            EscidocAbstractTest.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining update of Role with role name that is not unique.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro20() throws Exception {

        final Document createdDocument1 = createSuccessfully("role_for_create.xml");
        final Document createdDocument2 = createSuccessfully("role_for_create.xml");
        final String roleName1 = selectSingleNode(createdDocument1, XPATH_ROLE_NAME).getTextContent().trim();
        Document toBeUpdateDocument = createdDocument2;
        final Node nameNode = selectSingleNode(toBeUpdateDocument, XPATH_ROLE_NAME);
        nameNode.setTextContent(roleName1);

        try {
            create(toString(toBeUpdateDocument, false));
            EscidocAbstractTest.failMissingException(UniqueConstraintViolationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(UniqueConstraintViolationException.class, e);
        }
    }

    /**
     * Test declining update of Role with outdated last modification timestamp.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro23() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        final String id = getObjidValue(toBeUpdatedDocument);
        final String lastModificationDate = getLastModificationDateValue(toBeUpdatedDocument);

        String updatedXml = null;
        try {
            updatedXml = update(id, toString(toBeUpdatedDocument, false));
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(updatedXml);
        Document toBeUpdatedDocument2 = EscidocAbstractTest.getDocument(updatedXml);
        deleteAttribute(toBeUpdatedDocument2, XPATH_ROLE, EscidocTestBase.NAME_LAST_MODIFICATION_DATE);
        addAttribute(toBeUpdatedDocument2, XPATH_ROLE, createAttributeNode(toBeUpdatedDocument2, XLINK_NS_URI, null,
            EscidocTestBase.NAME_LAST_MODIFICATION_DATE, lastModificationDate));

        try {
            update(getObjidValue(toBeUpdatedDocument2), toString(toBeUpdatedDocument2, false));
            EscidocAbstractTest.failMissingException(OptimisticLockingException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(OptimisticLockingException.class, e);
        }
    }

    /**
     * Test declining updating a Role with forbidden role name.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro29() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        Node nameNode = selectSingleNode(toBeUpdatedDocument, XPATH_ROLE_NAME);
        nameNode.setTextContent("Default-User");

        try {
            update(getObjidValue(toBeUpdatedDocument), toString(toBeUpdatedDocument, false));
            EscidocAbstractTest.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining updating a Role with forbidden access.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro30() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");

        try {
            update("escidoc:role-default-user", toString(toBeUpdatedDocument, false));
            EscidocAbstractTest.failMissingException(RoleNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(RoleNotFoundException.class, e);
        }
    }

    /**
     * Test successful update of Role with changed namespace prefixes.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro33() throws Exception {

        final Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        final String id = getObjidValue(toBeUpdatedDocument);

        final Node resourceTypeAttr = selectSingleNode(toBeUpdatedDocument, XPATH_ROLE_SCOPE_DEF_RESOURCE_TYPE);
        resourceTypeAttr.setTextContent("user-account");

        final Node policySetIdAttr = selectSingleNode(toBeUpdatedDocument, XPATH_ROLE_POLICY_SET_ID);
        policySetIdAttr.setTextContent("changedId");

        final String toBeUpdatedXml = modifyNamespacePrefixes(toString(toBeUpdatedDocument, false));
        assertXmlValidRole(toBeUpdatedXml);

        final String timestampBeforeLastModification = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidRetrievedRole(updatedXml, toBeUpdatedXml, startTimestamp, timestampBeforeLastModification, true);

        String retrievedXml = null;
        try {
            retrievedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieve of updated role failed. ", e);
        }

        assertEquals("Retrieved differs from updated. ", updatedXml, retrievedXml);
    }

    // FIXME: test with 2 scope defs with same resource type

    /**
     * Test retrieving list of roles from the framework.
     *
     * @param filter The filter criteria.
     * @return The retrieved resource.
     * @throws Exception If anything fails.
     */
    public String retrieveRoles(final Map<String, String[]> filter) throws Exception {
        return handleXmlResult(getRoleClient().retrieveRoles(filter));
    }

    /**
     * Test successfully retrieving a list of roles.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs1CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_NAME + "\"=%" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String retrievedXml = null;

        try {
            retrievedXml = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of roles failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedXml);

        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_ROLE);
        assertXmlExists("Missing role Administrator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Administrator']");
        assertXmlExists("Missing role Depositor.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Depositor']");
        assertXmlExists("Missing role MD-Editor.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='MD-Editor']");
        assertXmlExists("Missing role Moderator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Moderator']");
        assertXmlExists("Missing role Ingester.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Ingester']");
        assertXmlExists("Missing role System-Administrator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='System-Administrator']");
        assertXmlExists("Missing role System-Inspector.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='System-Inspector']");
        assertXmlExists("Missing role Statistics-Editor.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Statistics-Editor']");
        assertXmlExists("Missing role Statistics-Reader.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Statistics-Reader']");
        assertXmlExists("Missing role Privileged-Viewer.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Privileged-Viewer']");
        assertXmlExists("Missing role Collaborator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Collaborator']");
        assertXmlExists("Missing role Audience.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Audience']");
        assertXmlExists("Missing role Collaborator-Modifier.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Collaborator-Modifier']");
    }

    /**
     * Test declining retrieving a list of roles with providing corrupted filter parameter.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs3() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_NAME + "\"=or or or or and" });
        try {
            retrieveRoles(filterParams);
            EscidocAbstractTest.failMissingException("Retrieving roles with providing corrupted filter params"
                + " not declined. ", InvalidSearchQueryException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Retrieving roles with providing corrupted filter params"
                + "not declined, properly. ", InvalidSearchQueryException.class, e);
        }
    }

    /**
     * Test declining retrieving a list of roles with providing invalid filter.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs4CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + NAME_CREATED_BY + "\"=\"Some value\"" });
        try {
            retrieveRoles(filterParams);
            EscidocAbstractTest.failMissingException("Retrieving roles with providing corrupted filter params"
                + " not declined. ", InvalidSearchQueryException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Retrieving roles with providing corrupted filter params"
                + "not declined, properly. ", InvalidSearchQueryException.class, e);
        }
    }

    /**
     * Test successfully retrieving a list of roles using filter roles.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs5CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER
            + "\"=escidoc:role-administrator or \"" + FILTER_IDENTIFIER + "\"=escidoc:role-md-editor" });

        String retrievedXml = null;

        try {
            retrievedXml = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of roles failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedXml);
        final NodeList roleNodes = selectNodeList(retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE);
        assertEquals("Unexpected number of roles in list.", 2, roleNodes.getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_ROLE);
        assertXmlExists("Missing role Administrator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Administrator']");
        assertXmlExists("Missing role Author.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='MD-Editor']");
    }

    /**
     * Test successfully retrieving a list of roles using filter name.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs6CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_NAME + "\"=System-Admin%" });

        String retrievedXml = null;

        try {
            retrievedXml = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of roles failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedXml);
        final NodeList roleNodes = selectNodeList(retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE);
        assertEquals("Unexpected number of roles in list.", 1, roleNodes.getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_ROLE);
        assertXmlExists("Missing role System-Administrator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='System-Administrator']");
    }

    /**
     * Test successfully retrieving a list of roles using filter limited.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs7CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "limited=true" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String retrievedXml = null;

        try {
            retrievedXml = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of roles failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_ROLE);
        assertXmlExists("Missing role Administrator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Administrator']");
        assertXmlExists("Missing role Depositor.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Depositor']");
        assertXmlExists("Missing role MD-Editor.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='MD-Editor']");
        assertXmlExists("Missing role Moderator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Moderator']");

        assertXmlNotExists("Unexpected role System-Administrator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='System-Administrator']");
        assertXmlNotExists("Unexpected role System-Inspector.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='System-Inspector']");
    }

    /**
     * Test successfully retrieving a list of roles using filter unlimited.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs8CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "limited=false" });

        String retrievedXml = null;

        try {
            retrievedXml = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of roles failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_ROLE);
        assertXmlExists("Missing role System-Administrator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='System-Administrator']");
        assertXmlExists("Missing role System-Inspector.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='System-Inspector']");

        assertXmlNotExists("Unexpected role Administrator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Administrator']");
        assertXmlNotExists("Unexpected role Author.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Author']");
        assertXmlNotExists("Unexpected role Depositor.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Depositor']");
        assertXmlNotExists("Unexpected role MD-Editor.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='MD-Editor']");
        assertXmlNotExists("Unexpected role Moderator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Moderator']");
    }

    /**
     * Test successfully retrieving a list of roles using filter granted.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs9CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "granted=true" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String retrievedXml = null;

        try {
            retrievedXml = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of roles failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_ROLE);
        assertXmlExists("Missing role Administrator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Administrator']");
        assertXmlExists("Missing role Depositor.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Depositor']");
        assertXmlExists("Missing role MD-Editor.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='MD-Editor']");
        assertXmlExists("Missing role Moderator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Moderator']");

        assertXmlExists("Missing role System-Administrator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='System-Administrator']");
        assertXmlExists("Missing role System-Inspector.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='System-Inspector']");
        assertXmlNotExists("Unexpected role Author.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Author']");
    }

    /**
     * Test successfully retrieving a list of roles using filter never-granted.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs10CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "granted=false" });

        String retrievedXml = null;

        try {
            retrievedXml = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of roles failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_ROLE);

        assertXmlNotExists("Unexpected role Administrator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Administrator']");
        assertXmlNotExists("Unexpected role Depositor.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Depositor']");
        assertXmlNotExists("Unexpected role MD-Editor.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='MD-Editor']");
        assertXmlNotExists("Unexpected role Moderator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Moderator']");
        assertXmlNotExists("Unexpected role System-Administrator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='System-Administrator']");
        assertXmlNotExists("Unexpected role System-Inspector.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='System-Inspector']");
    }

    /**
     * Test successfully retrieving a list of roles using multiple filters.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs12CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_IDENTIFIER
            + "\"=escidoc:role-administrator or " + "\"" + FILTER_IDENTIFIER + "\"=escidoc:role-depositor or " + "\""
            + FILTER_IDENTIFIER + "\"=escidoc:role-md-editor or " + "\"" + FILTER_IDENTIFIER
            + "\"=escidoc:role-moderator or " + "\"" + FILTER_IDENTIFIER + "\"=escidoc:role-system-inspector or "
            + "\"" + FILTER_IDENTIFIER + "\"=escidoc:role-system-administrator) and " + "\"" + FILTER_NAME
            + "\"=A%or and limited=true and granted=true" });

        String retrievedXml = null;

        try {
            retrievedXml = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of roles failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedXml);
        final NodeList roleNodes = selectNodeList(retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE);
        assertEquals("Unexpected number of roles.", 1, roleNodes.getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_ROLE);
        assertXmlExists("Missing role Administrator.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE
            + "[properties/name='Administrator']");
    }

    /**
     * Test decline retrieving a list of roles using unsupported filter.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs14CQL() throws Exception {

        final Class<InvalidSearchQueryException> ec = InvalidSearchQueryException.class;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY,
            new String[] { "(\"" + FILTER_IDENTIFIER + "\"=escidoc:role-administrator or " + "\"" + FILTER_IDENTIFIER
                + "\"=escidoc:role-depositor or " + "\"" + FILTER_IDENTIFIER + "\"=escidoc:role-md-editor or " + "\""
                + FILTER_IDENTIFIER + "\"=escidoc:role-moderator or " + "\"" + FILTER_IDENTIFIER
                + "\"=escidoc:role-system-inspector or " + "\"" + FILTER_IDENTIFIER
                + "\"=escidoc:role-system-administrator and " + "\"" + FILTER_NAME + "\"=A%or and " + "\""
                + FILTER_CONTEXT + "\"=escidoc:persistent3 and limited=true " + "and granted=true" });
        try {
            retrieveRoles(filterParams);
            failMissingException("Retrieving with unknown filter criteria not declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving with unknown filter criteria not declined," + " properly.", ec, e);
        }
    }

    /**
     * Test successfully retrieving a list of roles using ids and order-by ascending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs15CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER
            + "\"=escidoc:role-administrator or " + "\"" + FILTER_IDENTIFIER + "\"=escidoc:role-depositor or " + "\""
            + FILTER_IDENTIFIER + "\"=escidoc:role-md-editor sortby " + "\"" + FILTER_NAME + "\"/sort.ascending" });

        String retrievedXml = null;

        try {
            retrievedXml = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of roles failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_ROLE);

        assertXmlExists("Missing role MD-Editor.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[3]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_ROLE + "/properties[name='MD-Editor']");
        assertXmlExists("Missing role Depositor.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[2]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_ROLE + "/properties[name='Depositor']");
        assertXmlExists("Missing role Administrator.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[1]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_ROLE + "/properties[name='Administrator']");
        assertXmlNotExists("Unexpected 4.th role.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE + "[4]");
    }

    /**
     * Test successfully retrieving a list of roles using ids and order-by descending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs16CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER
            + "\"=escidoc:role-administrator or " + "\"" + FILTER_IDENTIFIER + "\"=escidoc:role-depositor or " + "\""
            + FILTER_IDENTIFIER + "\"=escidoc:role-md-editor sortby " + "\"" + FILTER_NAME + "\"/sort.descending" });

        String retrievedXml = null;

        try {
            retrievedXml = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of roles failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_ROLE);

        assertXmlExists("Missing role MD-Editor.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[1]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_ROLE + "/properties[name='MD-Editor']");
        assertXmlExists("Missing role Depositor.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[2]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_ROLE + "/properties[name='Depositor']");
        assertXmlExists("Missing role Administrator.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[3]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_ROLE + "/properties[name='Administrator']");
        assertXmlNotExists("Unexpected 4.th role.", retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE + "[4]");
    }

    /**
     * Test successfully retrieving a list of roles using ids, order-by descending and offset.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs17CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER
            + "\"=escidoc:role-administrator or " + "\"" + FILTER_IDENTIFIER + "\"=escidoc:role-depositor or " + "\""
            + FILTER_IDENTIFIER + "\"=escidoc:role-md-editor sortby " + "\"" + FILTER_NAME + "\"/sort.descending" });
        filterParams.put(FILTER_PARAMETER_STARTRECORD, new String[] { "2" });

        String retrievedXml = null;

        try {
            retrievedXml = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of roles failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_ROLE);

        assertXmlExists("Missing role Depositor.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[1]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_ROLE + "/properties[name='Depositor']");
        assertXmlExists("Missing role Administrator.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[2]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_ROLE + "/properties[name='Administrator']");
        assertXmlNotExists("Unexpected 3.th role.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[3]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH.substring(0, XPATH_SRW_RESPONSE_OBJECT_SUBPATH.length() - 1) + "["
            + NAME_ROLE + "]");
    }

    /**
     * Test successfully retrieving a list of roles using ids, order-by descending and offset.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrs18CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER
            + "\"=escidoc:role-administrator or " + "\"" + FILTER_IDENTIFIER + "\"=escidoc:role-depositor or " + "\""
            + FILTER_IDENTIFIER + "\"=escidoc:role-md-editor sortby " + "\"" + FILTER_NAME + "\"/sort.descending" });
        filterParams.put(FILTER_PARAMETER_STARTRECORD, new String[] { "2" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1" });

        String retrievedXml = null;

        try {
            retrievedXml = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of roles failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_ROLE);

        assertXmlExists("Missing role Depositor.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[1]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_ROLE + "/properties[name='Depositor']");
        assertXmlNotExists("Unexpected 2.th role.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[2]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH.substring(0, XPATH_SRW_RESPONSE_OBJECT_SUBPATH.length() - 1) + "["
            + NAME_ROLE + "]");
    }

    /**
     * Test successful retrieving a list of existing Roles resources. Test if maximumRecords=0 delivers 0 Roles
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void emptyFilterZeroMaximumRecords() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "0" });

        String result = null;

        try {
            result = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of Roles failed. ", e);
        }

        assertXmlValidSrwResponse(result);
        Document retrievedDocument = EscidocAbstractTest.getDocument(result);
        NodeList resultNodes = selectNodeList(retrievedDocument, XPATH_SRW_ROLE_LIST_ROLE);
        final int totalRecordsWithZeroMaximum = resultNodes.getLength();

        assertEquals("Unexpected number of records.", totalRecordsWithZeroMaximum, 0);

    }

    /**
     * Test successfully retrieving an explain response.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void explainTest() throws Exception {
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_EXPLAIN, new String[] { "" });

        String result = null;

        try {
            result = retrieveRoles(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }

    /**
     * Test declining creation of Role with providing objid.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro9_rest() throws Exception {

        Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_for_create.xml");
        addAttribute(toBeCreatedDocument, XPATH_ROLE, createAttributeNode(toBeCreatedDocument, XLINK_NS_URI, null,
            EscidocTestBase.NAME_OBJID, "escidoc:42"));
        insertUniqueRoleName(toBeCreatedDocument);

        try {
            create(toString(toBeCreatedDocument, false));
            EscidocAbstractTest.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test successful creation of Role with set read-only values.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro19_rest() throws Exception {

        EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_for_create_rest_read_only.xml");
        Document createdDocument = createSuccessfully("role_for_create_rest_read_only.xml");

        assertEquals("Creation date and last modification date are different. ", assertCreationDateExists("",
            createdDocument), getLastModificationDateValue(createdDocument));

        final String objid = getObjidValue(createdDocument);
        Document retrievedDocument = retrieveSuccessfully(objid);
        assertXmlEquals("Retrieved document not the same like the created one", createdDocument, retrievedDocument);
    }

    /**
     * Test successfully retrieving resources of a Role.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrr1_rest() throws Exception {

        String resourcesXml = null;
        try {
            resourcesXml = retrieveResources("escidoc:role-depositor");
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieval of role failed.", e);
        }
        assertNotNull("No virtual resources of role retrieved.", resourcesXml);
        Document retrievedDocument = EscidocAbstractTest.getDocument(resourcesXml);
        assertXmlExists("No resources element", retrievedDocument, XPATH_RESOURCES);
        assertXmlExists("No xlink title", retrievedDocument, XPATH_RESOURCES_XLINK_TITLE);
        assertXmlExists("No xlink href", retrievedDocument, XPATH_RESOURCES_HREF);
        assertXmlExists("No xlink type", retrievedDocument, XPATH_RESOURCES_XLINK_TYPE);
        assertXmlNotExists("Found unexpected objid", retrievedDocument, XPATH_RESOURCES_OBJID);
        NodeList children = selectSingleNode(retrievedDocument, XPATH_RESOURCES).getChildNodes();
        assertEquals("Unexpected number of children of resources element", 0, children.getLength());
    }

    /**
     * Test retrieving the resources of a Role with providing an unknown role id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrr2_rest() throws Exception {

        try {
            retrieveResources(UNKNOWN_ID);
            EscidocAbstractTest.failMissingException(RoleNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(RoleNotFoundException.class, e);
        }
    }

    /**
     * Test retrieving the resources of a Role with providing an id of an existing resource of another resource type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrr2_2_rest() throws Exception {

        try {
            retrieveResources(CONTEXT_ID);
            EscidocAbstractTest.failMissingException(RoleNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(RoleNotFoundException.class, e);
        }
    }

    /**
     * Test retrieving the resources of a Role without providing a role id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrr3_rest() throws Exception {

        try {
            retrieveResources(null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining retrieving the resources of a Role that is forbidden to be accessed.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARrr4_rest() throws Exception {

        final String id = "escidoc:role-default-user";

        try {
            retrieveResources(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("retrieving resources for role default-user fails with exception. ", e);
        }
    }

    /**
     * Test successfully updating a Role without providing creator xlink title.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro13_rest() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_CREATED_BY, XLINK_PREFIX_ESCIDOC + ":"
            + EscidocTestBase.NAME_TITLE);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        try {
            update(getObjidValue(toBeUpdatedDocument), toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Updating without set read only element fails with exception. ", e);
        }
    }

    /**
     * Test successfully updating a Role without providing creator xlink href.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro14_rest() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_CREATED_BY, XLINK_PREFIX_ESCIDOC + ":"
            + EscidocTestBase.NAME_HREF);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        try {
            update(getObjidValue(toBeUpdatedDocument), toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Updating without set read only element fails with exception. ", e);
        }
    }

    /**
     * Test successfully updating a Role without providing creator xlink type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro15_rest() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_CREATED_BY, XLINK_PREFIX_ESCIDOC + ":"
            + EscidocTestBase.NAME_TYPE);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        try {
            update(getObjidValue(toBeUpdatedDocument), toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Updating without set read only element fails with exception. ", e);
        }
    }

    /**
     * Test successfully updating a Role without providing modified-by xlink title.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro25_rest() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_MODIFIED_BY, XLINK_PREFIX_ESCIDOC + ":"
            + EscidocTestBase.NAME_TITLE);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        try {
            update(getObjidValue(toBeUpdatedDocument), toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Updating without set read only element fails with exception. ", e);
        }
    }

    /**
     * Test successfully updating a Role without providing modified-by xlink href.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro26_rest() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_MODIFIED_BY, XLINK_PREFIX_ESCIDOC + ":"
            + EscidocTestBase.NAME_HREF);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        try {
            update(getObjidValue(toBeUpdatedDocument), toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Updating without set read only element fails with exception. ", e);
        }
    }

    /**
     * Test successfully updating a Role without providing modified-by xlink type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro27_rest() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_MODIFIED_BY, XLINK_PREFIX_ESCIDOC + ":"
            + EscidocTestBase.NAME_TYPE);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        try {
            update(getObjidValue(toBeUpdatedDocument), toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Updating without set read only element fails with exception. ", e);
        }
    }

    /**
     * Test successful update of Role with changed read-only values.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro31_rest() throws Exception {

        final Document createdDocument = createSuccessfully("role_for_create.xml");
        final String id = getObjidValue(createdDocument);

        // change some values
        final Node resourceTypeAttr = selectSingleNode(createdDocument, XPATH_ROLE_SCOPE_DEF_RESOURCE_TYPE);
        resourceTypeAttr.setTextContent("user-account");

        final Node policySetIdAttr = selectSingleNode(createdDocument, XPATH_ROLE_POLICY_SET_ID);
        policySetIdAttr.setTextContent("changedId");

        final String createdXml = toString(createdDocument, false);

        // change read only values

        // root attributes
        final Document toBeUpdatedDocument = (Document) substitute(createdDocument, XPATH_ROLE_XLINK_HREF, "Some Href");
        substitute(toBeUpdatedDocument, XPATH_ROLE_XLINK_TITLE, "Some Title");
        substitute(toBeUpdatedDocument, XPATH_ROLE_XLINK_TYPE, "simple");
        substitute(toBeUpdatedDocument, XPATH_ROLE_XML_BASE, "http://some.base.uri");

        // resources do not exist in role

        // properties do not have xlink attributes

        // creation-date
        substitute(toBeUpdatedDocument, XPATH_ROLE_CREATION_DATE, getNowAsTimestamp());

        // created-by
        substitute(toBeUpdatedDocument, XPATH_ROLE_CREATED_BY_XLINK_HREF, "Some Href");
        substitute(toBeUpdatedDocument, XPATH_ROLE_CREATED_BY_XLINK_TITLE, "Some Title");
        // type is fixed to simple, cannot be changed

        // modified-by
        substitute(toBeUpdatedDocument, XPATH_ROLE_MODIFIED_BY_XLINK_HREF, "Some Href");
        substitute(toBeUpdatedDocument, XPATH_ROLE_MODIFIED_BY_XLINK_TITLE, "Some Title");
        // type is fixed to simple, cannot be changed

        // unlimited flag
        substitute(toBeUpdatedDocument, XPATH_ROLE_SCOPE_UNLIMITED, "true");

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        final String timestampBeforeLastModification = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidRetrievedRole(updatedXml, createdXml, startTimestamp, timestampBeforeLastModification, true);

        String retrievedXml = null;
        try {
            retrievedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieve of updated role failed. ", e);
        }

        assertEquals("Retrieved differs from updated. ", updatedXml, retrievedXml);
    }

    /**
     * Test successful update of Role without read-only values.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUro32_rest() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("role_for_create.xml");
        final String id = getObjidValue(toBeUpdatedDocument);

        final Node resourceTypeAttr = selectSingleNode(toBeUpdatedDocument, XPATH_ROLE_SCOPE_DEF_RESOURCE_TYPE);
        resourceTypeAttr.setTextContent("user-account");

        final Node policySetIdAttr = selectSingleNode(toBeUpdatedDocument, XPATH_ROLE_POLICY_SET_ID);
        policySetIdAttr.setTextContent("changedId");

        // remove read only values

        // root attributes
        toBeUpdatedDocument = (Document) deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_XLINK_HREF);
        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_XLINK_TITLE);
        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_XLINK_TYPE);
        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_XML_BASE);

        // resources do not exist in role

        // properties do not have xlink attributes

        // creation-date
        deleteNodes(toBeUpdatedDocument, XPATH_ROLE_CREATION_DATE);

        // created-by
        deleteNodes(toBeUpdatedDocument, XPATH_ROLE_CREATED_BY);

        // modified-by
        deleteNodes(toBeUpdatedDocument, XPATH_ROLE_MODIFIED_BY);

        // unlimited flag
        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_SCOPE_UNLIMITED);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        final String timestampBeforeLastModification = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidRetrievedRole(updatedXml, toBeUpdatedXml, startTimestamp, timestampBeforeLastModification, true);

        String retrievedXml = null;
        try {
            retrievedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieve of updated role failed. ", e);
        }

        assertEquals("Retrieved differs from updated. ", updatedXml, retrievedXml);
    }
}
