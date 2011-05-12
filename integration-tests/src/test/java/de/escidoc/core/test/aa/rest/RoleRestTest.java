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
package de.escidoc.core.test.aa.rest;

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.RoleNotFoundException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.aa.RoleAbstractTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test suite for the resource role using the REST interface.
 *
 * @author Torsten Tetteroo
 */
@RunWith(JUnit4.class)
public class RoleRestTest extends RoleAbstractTest {

    /**
     * The constructor.
     *
     * @throws Exception If anything fails.
     */
    public RoleRestTest() throws Exception {
        super();
    }

    /**
     * Test declining creation of Role with providing objid.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro9_rest() throws Exception {

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_for_create.xml");
        addAttribute(toBeCreatedDocument, XPATH_ROLE, createAttributeNode(toBeCreatedDocument, XLINK_NS_URI, null,
            EscidocTestBase.NAME_OBJID, "escidoc:42"));
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
     * Test successful creation of Role with set read-only values.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACro19_rest() throws Exception {

        EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ROLE_PATH, "role_for_create_rest_read_only.xml");
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
            EscidocRestSoapTestBase.failException("Retrieval of role failed.", e);
        }
        assertNotNull("No virtual resources of role retrieved.", resourcesXml);
        Document retrievedDocument = EscidocRestSoapTestBase.getDocument(resourcesXml);
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
            EscidocRestSoapTestBase.failMissingException(RoleNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(RoleNotFoundException.class, e);
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
            EscidocRestSoapTestBase.failMissingException(RoleNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(RoleNotFoundException.class, e);
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
            EscidocRestSoapTestBase.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(MissingMethodParameterException.class, e);
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
            EscidocRestSoapTestBase.failException("retrieving resources for role default-user fails with exception. ",
                e);
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
            EscidocRestSoapTestBase.failException("Updating without set read only element fails with exception. ", e);
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
            EscidocRestSoapTestBase.failException("Updating without set read only element fails with exception. ", e);
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
            EscidocRestSoapTestBase.failException("Updating without set read only element fails with exception. ", e);
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
            EscidocRestSoapTestBase.failException("Updating without set read only element fails with exception. ", e);
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
            EscidocRestSoapTestBase.failException("Updating without set read only element fails with exception. ", e);
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
            EscidocRestSoapTestBase.failException("Updating without set read only element fails with exception. ", e);
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
            EscidocRestSoapTestBase.failException(e);
        }
        assertXmlValidRetrievedRole(updatedXml, createdXml, startTimestamp, timestampBeforeLastModification, true);

        String retrievedXml = null;
        try {
            retrievedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException("Retrieve of updated role failed. ", e);
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
            EscidocRestSoapTestBase.failException(e);
        }
        assertXmlValidRetrievedRole(updatedXml, toBeUpdatedXml, startTimestamp, timestampBeforeLastModification, true);

        String retrievedXml = null;
        try {
            retrievedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException("Retrieve of updated role failed. ", e);
        }

        assertEquals("Retrieved differs from updated. ", updatedXml, retrievedXml);
    }

}
