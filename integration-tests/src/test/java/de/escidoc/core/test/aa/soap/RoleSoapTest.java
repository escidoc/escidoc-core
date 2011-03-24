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
package de.escidoc.core.test.aa.soap;

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.aa.RoleAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for the resource role using the SOAP interface.
 * 
 * @author Torsten Tetteroo
 * 
 */
@RunWith(JUnit4.class)
public class RoleSoapTest extends RoleAbstractTest {

    /**
     * Constructor.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public RoleSoapTest() throws Exception {

        super(Constants.TRANSPORT_SOAP);
    }

    /**
     * Test successful creation of Role with set read-only values.
     * 
     * @test.name Create Role - Read Only Values
     * @test.id AA_CRO-19-soap
     * @test.input: Valid role XML representation for creating a new role is
     *              provided. Read only values are specified within the data.
     * @test.expected: Role is returned with additional data like xlink
     *                 attributes and last-modification-date attributes,
     *                 creation-date, creator.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACro19_soap() throws Exception {

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ROLE_PATH,
                "role_for_create_soap_read_only.xml");
        Document createdDocument =
            createSuccessfully("role_for_create_soap_read_only.xml");

        assertEquals(
            "Creation date and last modification date are different. ",
            assertCreationDateExists("", createdDocument),
            getLastModificationDateValue(createdDocument));

        final String objid = getObjidValue(createdDocument);
        Document retrievedDocument = retrieveSuccessfully(objid);
        assertXmlEquals(
            "Retrieved document not the same like the created one.",
            createdDocument, retrievedDocument);
    }

    /**
     * Test declining creation of Role with providing xlink:title.
     * 
     * @test.name Create Role - Providing Xlink Title Attribute
     * @test.id AA_CRO-6-soap
     * @test.input: Role XML representation with specified xlink title.
     * @test.expected: XmlSchemaValidationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACro6_soap() throws Exception {

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_for_create.xml");

        addAttribute(toBeCreatedDocument, XPATH_ROLE, createAttributeNode(
            toBeCreatedDocument, XLINK_NS_URI, XLINK_PREFIX_TEMPLATES,
            EscidocTestBase.NAME_TITLE, "Some Value"));
        insertUniqueRoleName(toBeCreatedDocument);

        try {
            create(toString(toBeCreatedDocument, false));
            EscidocRestSoapTestBase.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining creation of Role with providing xlink:href.
     * 
     * @test.name Create Role - Providing Xlink href Attribute
     * @test.id AA_CRO-7-soap
     * @test.input: Role XML representation with specified xlink href.
     * @test.expected: XmlSchemaValidationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACro7_soap() throws Exception {

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_for_create.xml");
        addAttribute(toBeCreatedDocument, XPATH_ROLE, createAttributeNode(
            toBeCreatedDocument, XLINK_NS_URI, XLINK_PREFIX_TEMPLATES,
            EscidocTestBase.NAME_HREF, "http://www.escidoc.de"));
        insertUniqueRoleName(toBeCreatedDocument);

        try {
            create(toString(toBeCreatedDocument, false));
            EscidocRestSoapTestBase.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining creation of Role with providing xlink:type.
     * 
     * @test.name Create Role - Providing Xlink type Attribute
     * @test.id AA_CRO-8-soap
     * @test.input: Role XML representation with specified xlink type.
     * @test.expected: XmlSchemaValidationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACro8_soap() throws Exception {

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_for_create.xml");
        addAttribute(toBeCreatedDocument, XPATH_ROLE, createAttributeNode(
            toBeCreatedDocument, XLINK_NS_URI, XLINK_PREFIX_TEMPLATES,
            EscidocTestBase.NAME_TYPE, "simple"));
        insertUniqueRoleName(toBeCreatedDocument);

        try {
            create(toString(toBeCreatedDocument, false));
            EscidocRestSoapTestBase.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test successfully updating a Role without providing creator objid.
     * 
     * @test.name Update Role - Missing Objid Attribute of Creator
     * @test.id AA_URO-16-soap
     * @test.input:
     *          <ul>
     *          <li>id of an existing role</li>
     *          <li>Role XML representation without objid attribute in creator
     *          element.</li>
     *          </ul>
     * @test.expected: Successfully updated role.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUro16_soap() throws Exception {

        Document toBeUpdatedDocument =
            createSuccessfully("role_for_create.xml");

        final String createdXml = toString(toBeUpdatedDocument, false);

        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_CREATED_BY,
            EscidocTestBase.NAME_OBJID);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        final String timestampBeforeLastModification = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml =
                update(getObjidValue(toBeUpdatedDocument), toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Updating without set read only element fails with exception. ",
                e);
        }
        assertXmlValidRetrievedRole(updatedXml, createdXml, startTimestamp,
            timestampBeforeLastModification, true);
    }

    /**
     * Test successfully updating a Role without providing modified-by objid.
     * 
     * @test.name Update Role - Missing Objid Attribute of Modified-by
     * @test.id AA_URO-28-soap
     * @test.input:
     *          <ul>
     *          <li>id of an existing role</li>
     *          <li>Role XML representation without objid attribute in
     *          modified-by element.</li>
     *          </ul>
     * @test.expected: Successfully updated role.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUro28_soap() throws Exception {

        Document toBeUpdatedDocument =
            createSuccessfully("role_for_create.xml");

        final String createdXml = toString(toBeUpdatedDocument, false);

        deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_CREATED_BY,
            EscidocTestBase.NAME_OBJID);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        final String timestampBeforeLastModification = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml =
                update(getObjidValue(toBeUpdatedDocument), toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Updating without set read only element fails with exception. ",
                e);
        }
        assertXmlValidRetrievedRole(updatedXml, createdXml, startTimestamp,
            timestampBeforeLastModification, true);
    }

    /**
     * Test successful update of Role with changed read-only values.
     * 
     * @test.id AA_URO-31-soap
     * @test.input: Valid role XML representation for updating a role with
     *              changed read only values.
     * @test.expected: Role is returned with updated data like xlink attributes
     *                 and last-modification-date attributes.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUro31_soap() throws Exception {

        Document createdDocument = createSuccessfully("role_for_create.xml");
        final String id = getObjidValue(createdDocument);

        // change some values
        final Node resourceTypeAttr =
            selectSingleNode(createdDocument,
                XPATH_ROLE_SCOPE_DEF_RESOURCE_TYPE);
        resourceTypeAttr.setTextContent("user-account");

        final Node policySetIdAttr =
            selectSingleNode(createdDocument, XPATH_ROLE_POLICY_SET_ID);
        policySetIdAttr.setTextContent("changedId");

        final String createdXml = toString(createdDocument, false);

        // change read only values

        // root attribute
        final Document toBeUpdatedDocument =
            (Document) substitute(createdDocument, XPATH_ROLE_OBJID,
                "some:objid");

        // resources do not exist in role

        // properties do not have xlink attributes

        // creation-date
        substitute(toBeUpdatedDocument, XPATH_ROLE_CREATION_DATE,
            getNowAsTimestamp());

        // created-by
        substitute(toBeUpdatedDocument, XPATH_ROLE_CREATED_BY_OBJID,
            "some:objid");

        // modified-by
        substitute(toBeUpdatedDocument, XPATH_ROLE_MODIFIED_BY_OBJID,
            "some:objid");

        // unlimited flag
        substitute(toBeUpdatedDocument, XPATH_ROLE_SCOPE_UNLIMITED, "true");

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);

        final String timestampBeforeLastModification = getNowAsTimestamp();
        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        // do not compare creation date in assertion because creation date has
        // been changed in input data
        assertXmlValidRetrievedRole(updatedXml, toBeUpdatedXml, startTimestamp,
            timestampBeforeLastModification, false);

        String retrievedXml = null;
        try {
            retrievedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException("Retrieve of updated role failed. ", e);
        }

        assertEquals("Retrieved differs from updated. ", updatedXml,
            retrievedXml);
    }

    /**
     * Test successful update of Role without read-only values.
     * 
     * @test.id AA_URO-32-soap
     * @test.input: Valid role XML representation for updating a role without
     *              read only values.
     * @test.expected: Role is returned with updated data like xlink attributes
     *                 and last-modification-date attributes.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUro32_soap() throws Exception {

        Document toBeUpdatedDocument =
            createSuccessfully("role_for_create.xml");
        final String id = getObjidValue(toBeUpdatedDocument);

        final Node resourceTypeAttr =
            selectSingleNode(toBeUpdatedDocument,
                XPATH_ROLE_SCOPE_DEF_RESOURCE_TYPE);
        resourceTypeAttr.setTextContent("user-account");

        final Node policySetIdAttr =
            selectSingleNode(toBeUpdatedDocument, XPATH_ROLE_POLICY_SET_ID);
        policySetIdAttr.setTextContent("changedId");

        // remove read only values

        // root attributes
        toBeUpdatedDocument =
            (Document) deleteAttribute(toBeUpdatedDocument, XPATH_ROLE_OBJID);

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
            EscidocRestSoapTestBase.failException(e);
        }
        assertXmlValidRetrievedRole(updatedXml, toBeUpdatedXml, startTimestamp,
            timestampBeforeLastModification, true);

        String retrievedXml = null;
        try {
            retrievedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException("Retrieve of updated role failed. ", e);
        }

        assertEquals("Retrieved differs from updated. ", updatedXml,
            retrievedXml);
    }
}
