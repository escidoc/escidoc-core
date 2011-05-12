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

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.notfound.RoleNotFoundException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.aa.GrantAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.w3c.dom.Document;

/**
 * Testsuite for the UserAccount's or UserGroups Grants with REST transport.
 *
 * @author Torsten Tetteroo
 */
@RunWith(JUnit4.class)
public class GrantRestAbstractTest extends GrantAbstractTest {

    /**
     * Constructor.
     *
     * @param handlerCode handlerCode.
     * @throws Exception If anything fails.
     */
    public GrantRestAbstractTest(final int handlerCode) throws Exception {
        super(handlerCode);
    }

    /**
     * Test declining creation of Grant with providing XML data without specifying role href.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACg9_rest() throws Exception {

        Document grantDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");
        deleteAttribute(grantDocument, XPATH_GRANT_ROLE, NAME_HREF);

        try {
            createGrant(defaultUserAccountOrGroupId, toString(grantDocument, false));
            EscidocRestSoapTestBase.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining creation of Grant with providing XML data without specifying object href.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACg11_rest() throws Exception {

        Document grantDocument =
            getTemplateAsFixedGrantDocument(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");
        deleteAttribute(grantDocument, XPATH_GRANT_OBJECT, NAME_HREF);

        try {
            createGrant(defaultUserAccountOrGroupId, toString(grantDocument, false));
            EscidocRestSoapTestBase.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Successfully create grant with set read only values (REST).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACg12_rest() throws Exception {

        final Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create_rest_read_only.xml");

        final String toBeCreatedXml = toString(toBeCreatedDocument, false);

        String createdXml = null;
        try {
            createdXml = createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        final Document createdDocument =
            assertGrant(createdXml, null, defaultUserAccountOrGroupId, startTimestamp, startTimestamp, false);

        // grant-remark
        assertXmlEquals("Grant remark mismatch, ", toBeCreatedDocument, createdDocument, XPATH_GRANT_GRANT_REMARK);

        // revocation-remark
        assertXmlNotExists("Unexpected revocation remark, ", createdDocument, XPATH_GRANT_REVOCATION_REMARK);

        // role reference
        assertXmlEquals("Role reference title unexpected, ", createdDocument, XPATH_GRANT_ROLE_XLINK_TITLE,
            "Administrator");

        // object reference
        assertXmlEquals("Object reference mismatch, href mismatch, ", toBeCreatedDocument, createdDocument,
            XPATH_GRANT_OBJECT_XLINK_HREF);
        assertXmlEquals("Object reference mismatch, title mismatch, ", createdDocument, XPATH_GRANT_OBJECT_XLINK_TITLE,
            "Test Collection");
    }

    /**
     * Test declining creation of Grant with providing reference to role with invalid href (base of href is not the base
     * of role hrefs).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACg13_3_rest() throws Exception {

        final Class<?> ec = RoleNotFoundException.class;

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");

        String roleHref = selectSingleNodeAsserted(toBeCreatedDocument, XPATH_GRANT_ROLE_XLINK_HREF).getTextContent();
        roleHref = roleHref.replaceFirst(Constants.ROLE_BASE_URI, Constants.ORGANIZATIONAL_UNIT_BASE_URI);
        substitute(toBeCreatedDocument, XPATH_GRANT_ROLE_XLINK_HREF, roleHref);

        final String toBeCreatedXml = toString(toBeCreatedDocument, false);

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestBase.failMissingException("Creating grant with invalid object href not declined. ", ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Creating grant with invalid object href not declined,"
                + " properly. ", ec, e);
        }
    }

    /**
     * Test declining creation of Grant with providing reference to object with invalid href (object id and object type
     * mismatch in href).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACg16_2_rest() throws Exception {

        final Class<?> ec = XmlCorruptedException.class;

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");
        substitute(toBeCreatedDocument, XPATH_GRANT_OBJECT_XLINK_HREF, Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
            + CONTEXT_ID);

        final String toBeCreatedXml = toString(toBeCreatedDocument, false);

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestBase.failMissingException("Creating grant with invalid object href not declined. ", ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Creating grant with invalid object href not declined,"
                + " properly. ", ec, e);
        }
    }
}
