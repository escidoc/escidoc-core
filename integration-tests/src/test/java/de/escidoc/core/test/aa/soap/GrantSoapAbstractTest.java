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

import de.escidoc.core.test.aa.GrantAbstractTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;

/**
 * Testsuite for the UserAccount's or UserGroups Grants with SOAP transport.
 * 
 * @author TTE
 * 
 */
@RunWith(JUnit4.class)
public class GrantSoapAbstractTest extends GrantAbstractTest {

    /**
     * Constructor.
     * 
     * @param handlerCode handlerCode.
     * @throws Exception
     *             If anything fails.
     */
    public GrantSoapAbstractTest(final int handlerCode) throws Exception {
        super(Constants.TRANSPORT_SOAP, handlerCode);
    }

    /**
     * Test declining creation of Grant with providing XML data without
     * specifying role objid.
     * 
     * @test.name Create Grant - Missing Role Objid - SOAP
     * @test.id AA_CG_9-soap
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created, role
     *             objid is not provided</li>
     *             </ul>
     * @test.expected: XmlSchemaValidationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg9_soap() throws Exception {

        Document grantDocument =
            getTemplateAsFixedGrantDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");
        deleteAttribute(grantDocument, XPATH_GRANT_ROLE, NAME_OBJID);

        try {
            createGrant(defaultUserAccountOrGroupId, toString(grantDocument, false));
            EscidocRestSoapTestBase.failMissingException(XmlSchemaValidationException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining creation of Grant with providing XML data without
     * specifying object objid.
     * 
     * @test.name Create Grant - Missing Object Objid - SOAP
     * @test.id AA_CG_11-soap
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
    public void testAACg11_soap() throws Exception {

        Document grantDocument =
            getTemplateAsFixedGrantDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");
        deleteAttribute(grantDocument, XPATH_GRANT_OBJECT, NAME_OBJID);

        try {
            createGrant(defaultUserAccountOrGroupId, toString(grantDocument, false));
            EscidocRestSoapTestBase.failMissingException(XmlSchemaValidationException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Successfully create grant with set read only values (SOAP).
     * 
     * @test.name Create Grant - Read Only Values - SOAP
     * @test.id AA_CG_12-soap
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created, all
     *             read-only values (SOAP) are set.</li>
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
    public void testAACg12_soap() throws Exception {

        final Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create_soap_read_only.xml");

        final String toBeCreatedXml = toString(toBeCreatedDocument, false);

        String createdXml = null;
        try {
            createdXml = createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        final Document createdDocument =
            assertGrant(createdXml, null, defaultUserAccountOrGroupId, startTimestamp,
                startTimestamp, false);

        // grant-remark
        assertXmlEquals("Grant remark mismatch, ", toBeCreatedDocument,
            createdDocument, XPATH_GRANT_GRANT_REMARK);

        // revocation-remark
        assertXmlNotExists("Unexpected revocation remark, ", createdDocument,
            XPATH_GRANT_REVOCATION_REMARK);

        // object reference
        assertXmlEquals("Object reference mismatch, ", toBeCreatedDocument,
            createdDocument, XPATH_GRANT_OBJECT);
    }
}
