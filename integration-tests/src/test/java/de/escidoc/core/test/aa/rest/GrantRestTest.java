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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.notfound.RoleNotFoundException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.aa.GrantTest;
import de.escidoc.core.test.common.client.servlet.Constants;

/**
 * Testsuite for the UserAccount's or UserGroups Grants with REST transport.
 * 
 * @author TTE
 * 
 */
@RunWith(JUnit4.class)
public class GrantRestTest extends GrantTest {

    /**
     * Constructor.
     * 
     * @param handlerCode handlerCode.
     * @throws Exception
     *             If anything fails.
     */
    public GrantRestTest(final int handlerCode) 
                                                throws Exception {
        super(Constants.TRANSPORT_REST, handlerCode);
    }

    /**
     * Test declining creation of Grant with providing XML data without
     * specifying role href.
     * 
     * @test.name Create Grant - Missing Role Href - REST
     * @test.id AA_CG_9-rest
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created, role href
     *             is not provided</li>
     *             </ul>
     * @test.expected: XmlSchemaValidationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg9_rest() throws Exception {

        Document grantDocument =
            EscidocRestSoapTestsBase.getTemplateAsDocument(
                TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");
        deleteAttribute(grantDocument, XPATH_GRANT_ROLE, NAME_HREF);

        try {
            createGrant(defaultUserAccountOrGroupId, toString(grantDocument,
                false));
            EscidocRestSoapTestsBase.failMissingException(
                XmlSchemaValidationException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining creation of Grant with providing XML data without
     * specifying object href.
     * 
     * @test.name Create Grant - Missing Object Href - REST
     * @test.id AA_CG_11-rest
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created, object
     *             objid is not provided</li>
     *             </ul>
     * @test.expected: XmlSchemaValidationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg11_rest() throws Exception {

        Document grantDocument =
            getTemplateAsFixedGrantDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");
        deleteAttribute(grantDocument, XPATH_GRANT_OBJECT, NAME_HREF);

        try {
            createGrant(defaultUserAccountOrGroupId, toString(grantDocument,
                false));
            EscidocRestSoapTestsBase.failMissingException(
                XmlSchemaValidationException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Successfully create grant with set read only values (REST).
     * 
     * @test.name Create Grant - Read Only Values - REST
     * @test.id AA_Cg_12-rest
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created, all
     *             read-only values (REST) are set.</li>
     *             </ul>
     * @test.expected: The XML representation of the newly created grant as
     *                 described in the xml-Schema "http://www.escidoc.de/
     *                 schemas/user-account/0.1/grant.xsd"
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg12_rest() throws Exception {

        final Document toBeCreatedDocument =
            EscidocRestSoapTestsBase.getTemplateAsDocument(
                TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create_rest_read_only.xml");

        final String toBeCreatedXml = toString(toBeCreatedDocument, false);

        String createdXml = null;
        try {
            createdXml = createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        final Document createdDocument =
            assertGrant(createdXml, null, defaultUserAccountOrGroupId,
                startTimestamp, startTimestamp, false);

        // grant-remark
        assertXmlEquals("Grant remark mismatch, ", toBeCreatedDocument,
            createdDocument, XPATH_GRANT_GRANT_REMARK);

        // revocation-remark
        assertXmlNotExists("Unexpected revocation remark, ", createdDocument,
            XPATH_GRANT_REVOCATION_REMARK);

        // role reference
        assertXmlEquals("Role reference title unexpected, ", createdDocument,
            XPATH_GRANT_ROLE_XLINK_TITLE, "Administrator");

        // object reference
        assertXmlEquals("Object reference mismatch, href mismatch, ",
            toBeCreatedDocument, createdDocument, XPATH_GRANT_OBJECT_XLINK_HREF);
        assertXmlEquals("Object reference mismatch, title mismatch, ",
            createdDocument, XPATH_GRANT_OBJECT_XLINK_TITLE,
            "Test Collection");
    }
    
    /**
     * Test declining creation of Grant with providing reference to role with
     * invalid href (base of href is not the base of role hrefs).
     * 
     * @test.name Create Grant - Role referenced with invalid href.
     * @test.id AA_Cg_13_3_rest
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created, role
     *             referenced with an href that specifies another resource type
     *             (ou) instead of specifying role.</li>
     *             </ul>
     * @test.expected: RoleNotFoundException
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg13_3_rest() throws Exception {

        final Class< ? > ec = RoleNotFoundException.class;

        Document toBeCreatedDocument =
            EscidocRestSoapTestsBase.getTemplateAsDocument(
                TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");
        
        String roleHref =
            selectSingleNodeAsserted(toBeCreatedDocument,
                XPATH_GRANT_ROLE_XLINK_HREF).getTextContent();
        roleHref =
            roleHref.replaceFirst(Constants.ROLE_BASE_URI,
                Constants.ORGANIZATIONAL_UNIT_BASE_URI);
        substitute(toBeCreatedDocument, XPATH_GRANT_ROLE_XLINK_HREF,
            roleHref);

        final String toBeCreatedXml =
            toString(fixGrantDocument(toBeCreatedDocument), false);

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestsBase.failMissingException(
                "Creating grant with invalid object href not declined. ", ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "Creating grant with invalid object href not declined,"
                    + " properly. ", ec, e);
        }
    }
    
    /**
     * Test declining creation of Grant with providing reference to object with
     * invalid href (object id and object type mismatch in href).
     * 
     * @test.name Create Grant - Object referenced with invalid href.
     * @test.id AA_Cg_16_2_rest
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created, object
     *             referenced with an href that specifies a resource type and an
     *             id that do not match, type = ou, id = a context id.</li>
     *             </ul>
     * @test.expected: InvalidXmlException
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg16_2_rest() throws Exception {

        final Class< ? > ec = XmlCorruptedException.class;

        Document toBeCreatedDocument =
            EscidocRestSoapTestsBase.getTemplateAsDocument(
                TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");
        substitute(toBeCreatedDocument, XPATH_GRANT_OBJECT_XLINK_HREF,
            Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + CONTEXT_ID);

        final String toBeCreatedXml =
            toString(fixGrantDocument(toBeCreatedDocument), false);

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestsBase.failMissingException(
                "Creating grant with invalid object href not declined. ", ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "Creating grant with invalid object href not declined,"
                    + " properly. ", ec, e);
        }
    }
}
