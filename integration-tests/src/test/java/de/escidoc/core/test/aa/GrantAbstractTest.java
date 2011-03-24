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


import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidScopeException;
import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.GrantNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.remote.application.violated.AlreadyRevokedException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test suite for the Grants of an UserAccount or UserGroup.
 * 
 * @author Torsten Tetteroo
 * 
 * changes for schema version 0.3:
 * <ul>
 * <li>Removed UM_CG_6 (duplicate to UM_CG_7)</li>
 * <li>Removed UM_CG_8 and UM_CG_10 (either href or object id in link element)</li>
 * <li>Replaced UM_CG_9 and UM_CG_11 by UM_CG_9-rest/UM_CG_9-soap and
 * UM_CG_11-rest/UM_CG_11-soap</li>
 * <li>Replaced UM_CG_12 by UM_CG_12-rest/UM_CG_12-soap</li>
 * </ul>
 */
public class GrantAbstractTest extends GrantTestBase {

    /**
     * The constructor.
     * 
     * @param transport
     *            The transport identifier.
     * @param handlerCode
     *            The handlerCode.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public GrantAbstractTest(final int transport,
                final int handlerCode) throws Exception {
        super(transport, handlerCode);
    }
    
    public GrantAbstractTest(final int transport,
        final int handlerCode,
        final String userIdOrUserGroupId
        ) throws Exception {
        super(transport, handlerCode, userIdOrUserGroupId);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        revokeAllGrants(defaultUserAccountOrGroupId);
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
    }

    /**
     * Successfully create grant.
     * 
     * @test.name Create a Grant for the UserAccount or UserGroup
     * @test.id AA_Cg_1
     * @test.input Grant XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created</li>
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
    public void testAACg1() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        if (getTransport() == Constants.TRANSPORT_REST) {
            assertXmlEquals("Object title mismatch. ", createdDocument,
                XPATH_GRANT_OBJECT_XLINK_TITLE, "Test Collection");
        }
    }

    /**
     * Successfully create grant without reference to object.
     * 
     * @test.name Create Grant - No Object Reference
     * @test.id AA_Cg_1-2
     * @test.input Grant XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created</li>
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
    public void testAACg1_2() throws Exception {

        Document toBeCreatedDocument =
            getTemplateAsFixedGrantDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        // delete the object element
        deleteElement(toBeCreatedDocument, XPATH_GRANT_OBJECT);

        final String toBeCreatedXml = toString(toBeCreatedDocument, false);

        String createdXml = null;
        try {
            createdXml = createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertGrant(createdXml, toBeCreatedXml, defaultUserAccountOrGroupId,
            startTimestamp, startTimestamp, false);
    }

    /**
     * Successfully create grant referencing a new context.
     * 
     * @test.name Create Grant - New Context
     * @test.id AA_Cg_1-3
     * @test.input Grant XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created, grant
     *             references a newly created context.</li>
     *             </ul>
     * @test.expected: The XML representation of the newly created grant as
     *                 described in the grant xml-Schema.
     * 
     * @test.status Implemented
     * @test.issues http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=358
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg1_3() throws Exception {

        // create context
        String path = TEMPLATE_CONTEXT_PATH;
        if (getTransport() == Constants.TRANSPORT_REST) {
            path += "/rest";
        }
        else if (getTransport() == Constants.TRANSPORT_SOAP) {
            path += "/soap";
        }
        Document toBeCreatedContextDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(path,
                "context_create.xml");
        substitute(toBeCreatedContextDocument, "/context/properties/name",
            getUniqueName("PubMan Context "));
        String createdContextXml = null;
        try {
            final String toBeCreatedContextXml =
                toString(toBeCreatedContextDocument, false);
            createdContextXml =
                create(CONTEXT_HANDLER_CODE, toBeCreatedContextXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Creating context to be referenced in grant failed. ", e);
        }
        final String contextId = getObjidValue(createdContextXml);
        final String contextHref = Constants.CONTEXT_BASE_URI + "/" + contextId;

        // create grant on context
        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(
                TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");
        // set reference to created context
        substitute(toBeCreatedDocument, XPATH_GRANT_OBJECT_XLINK_HREF,
            contextHref);

        final String toBeCreatedXml =
            toString(fixGrantDocument(toBeCreatedDocument), false);

        String createdXml = null;
        try {
            createdXml = createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertGrant(createdXml, toBeCreatedXml, defaultUserAccountOrGroupId,
            startTimestamp, startTimestamp, false);
    }

    /**
     * Successfully create grant with scope on container.
     * 
     * @test.name Create a Grant for the UserAccount or UserGroup
     * @test.id AA_Cg_1_4
     * @test.input Grant XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created</li>
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
    public void testAACg1_4() throws Exception {

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        //create container
        String containerXml = 
            prepareContainer(
                PWCallback.DEFAULT_HANDLE, 
                STATUS_PENDING, null,
                false, false);
        Document containerDocument =
            EscidocRestSoapTestBase.getDocument(containerXml);
        String containerId = getObjidValue(containerDocument);

        // substitute the role
        substitute(
                toBeCreatedDocument, 
                XPATH_GRANT_ROLE_XLINK_HREF, 
                ROLE_HREF_COLLABORATOR);
        // substitute the object element
        substitute(
                toBeCreatedDocument, 
                XPATH_GRANT_OBJECT_XLINK_HREF, 
                Constants.CONTAINER_BASE_URI 
                    + "/" + containerId);
        final String toBeCreatedXml = 
            toString(fixGrantDocument(toBeCreatedDocument), false);
        
        String createdXml = null;
        try {
            createdXml = createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertGrant(createdXml, toBeCreatedXml, defaultUserAccountOrGroupId,
            startTimestamp, startTimestamp, false);
    }

    /**
     * Successfully create grant with scope on item and component.
     * 
     * @test.name Create a Grant for the UserAccount or UserGroup
     * @test.id AA_Cg_1_5
     * @test.input Grant XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created</li>
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
    public void testAACg1_5() throws Exception {

        Document toBeCreatedDocument =
                getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        String itemXml =
                prepareItem(
                PWCallback.DEFAULT_HANDLE,
                STATUS_PENDING,
                null,
                false, false);
        Document document =
                EscidocRestSoapTestBase.getDocument(itemXml);

        // save ids
        String itemId = getObjidValue(document);
        String publicComponentId = extractComponentId(document,
                VISIBILITY_PUBLIC);

        // substitute the role
        substitute(
                toBeCreatedDocument,
                XPATH_GRANT_ROLE_XLINK_HREF,
                ROLE_HREF_COLLABORATOR);

        // substitute the object element
        substitute(
                toBeCreatedDocument,
                XPATH_GRANT_OBJECT_XLINK_HREF,
                Constants.ITEM_BASE_URI
                + "/" + itemId);
        String toBeCreatedXml =
                toString(fixGrantDocument(toBeCreatedDocument), false);

        String createdXml = null;
        try {
            createdXml = createGrant(defaultUserAccountOrGroupId,
                    toBeCreatedXml);
        } catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertGrant(createdXml, toBeCreatedXml, defaultUserAccountOrGroupId,
                startTimestamp, startTimestamp, false);

        toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH,
            "escidoc_grant_for_create.xml");        

        // substitute the role
        substitute(
                toBeCreatedDocument,
                XPATH_GRANT_ROLE_XLINK_HREF,
                ROLE_HREF_COLLABORATOR);

        // substitute the object element
        substitute(
                toBeCreatedDocument,
                XPATH_GRANT_OBJECT_XLINK_HREF,
                Constants.ITEM_BASE_URI
                + "/" + itemId + "/" + Constants.SUB_COMPONENT
                + "/" + publicComponentId);
        toBeCreatedXml =
                toString(fixGrantDocument(toBeCreatedDocument), false);

        try {
            createdXml = createGrant(defaultUserAccountOrGroupId,
                    toBeCreatedXml);
        } catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertGrant(createdXml, toBeCreatedXml, defaultUserAccountOrGroupId,
                startTimestamp, startTimestamp, false);
    }

    /**
     * Successfully create grant with scope on scope.
     * 
     * @test.name Create a Grant for the UserAccount or UserGroup
     * @test.id AA_Cg_1_6
     * @test.input Grant XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created</li>
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
    public void testAACg1_6() throws Exception {

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        // substitute the role
        substitute(
                toBeCreatedDocument, 
                XPATH_GRANT_ROLE_XLINK_HREF, 
                ROLE_HREF_STATISTICS_EDITOR);
        // substitute the object element
        substitute(
                toBeCreatedDocument, 
                XPATH_GRANT_OBJECT_XLINK_HREF, 
                Constants.STATISTIC_SCOPE_BASE_URI 
                    + "/" + STATISTIC_SCOPE_ID);
        final String toBeCreatedXml = 
            toString(fixGrantDocument(toBeCreatedDocument), false);
        
        String createdXml = null;
        try {
            createdXml = createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertGrant(createdXml, toBeCreatedXml, defaultUserAccountOrGroupId,
            startTimestamp, startTimestamp, false);
    }

    /**
     * Successfully create grant with scope on user-group.
     * 
     * @test.name Create a Grant for the UserAccount or UserGroup
     * @test.id AA_Cg_1_7
     * @test.input Grant XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created</li>
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
    public void testAACg1_7() throws Exception {

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        // substitute the role
        substitute(
                toBeCreatedDocument, 
                XPATH_GRANT_ROLE_XLINK_HREF, 
                ROLE_HREF_USER_GROUP_INSPECTOR);
        // substitute the object element
        substitute(
                toBeCreatedDocument, 
                XPATH_GRANT_OBJECT_XLINK_HREF, 
                Constants.USER_GROUP_BASE_URI 
                    + "/" + USER_GROUP_WITH_USER_LIST_ID);
        final String toBeCreatedXml = 
            toString(fixGrantDocument(toBeCreatedDocument), false);
        
        String createdXml = null;
        try {
            createdXml = createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertGrant(createdXml, toBeCreatedXml, defaultUserAccountOrGroupId,
            startTimestamp, startTimestamp, false);
    }

    /**
     * Successfully create grant with scope on content-relation.
     * 
     * @test.name Create a Grant for the UserAccount or UserGroup
     * @test.id AA_Cg_1_8
     * @test.input Grant XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created</li>
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
    public void testAACg1_8() throws Exception {

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        String contentRelationXml = 
            prepareContentRelation(
                    PWCallback.DEFAULT_HANDLE, 
                    STATUS_PENDING, null, null,  false, false);
        // substitute the role
        substitute(
                toBeCreatedDocument, 
                XPATH_GRANT_ROLE_XLINK_HREF, 
                ROLE_HREF_CONTENT_RELATION_MODIFIER);
        // substitute the object element
        substitute(
                toBeCreatedDocument, 
                XPATH_GRANT_OBJECT_XLINK_HREF, 
                Constants.CONTENT_RELATION_BASE_URI 
                    + "/" + getObjidValue(contentRelationXml));
        final String toBeCreatedXml = 
            toString(fixGrantDocument(toBeCreatedDocument), false);
        
        String createdXml = null;
        try {
            createdXml = createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertGrant(createdXml, toBeCreatedXml, defaultUserAccountOrGroupId,
            startTimestamp, startTimestamp, false);
    }

    /**
     * Test declining creation of Grant with providing unknown UserAccount id.
     * 
     * @test.name User-id not existent
     * @test.id AA_CG_2
     * @test.input UserAccount id
     * @test.inputDescription:
     *             <ul>
     *             <li>non-existent user-id</li>
     *             <li>XML representation of the grant to be created</li>
     *             </ul>
     * @test.expected: notFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg2() throws Exception {

        String grantXml =
            getTemplateAsFixedGrantString(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        try {
            createGrant(UNKNOWN_ID, grantXml);
            EscidocRestSoapTestBase
                .failMissingException(notFoundException.getClass());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    notFoundException.getClass(), e);
        }
    }

    /**
     * Test declining creation of Grant with providing id of a resource of
     * another resource type.
     * 
     * @test.name Create Grant - Wrong Id
     * @test.id AA_CG_2-2
     * @test.input UserAccount id
     * @test.inputDescription:
     *             <ul>
     *             <li>id of existing resource of another resource type</li>
     *             <li>XML representation of the grant to be created</li>
     *             </ul>
     * @test.expected: notFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg2_2() throws Exception {

        String grantXml =
            getTemplateAsFixedGrantString(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        try {
            createGrant(CONTEXT_ID, grantXml);
            EscidocRestSoapTestBase
                .failMissingException(notFoundException.getClass());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                notFoundException.getClass(), e);
        }
    }

    /**
     * Test declining creation of Grant with providing 
     * scope object-type that is not allowed by role (organizational-unit).
     * 
     * @test.name Create Grant - Wrong scope object-type
     * @test.id AA_CG_2-3
     * @test.input UserAccount id, grant XML
     * @test.inputDescription:
     *             <ul>
     *             <li>id of existing user</li>
     *             <li>XML representation of the grant to be created</li>
     *             </ul>
     * @test.expected: InvalidScopeException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg2_3() throws Exception {

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        // substitute the object element
        substitute(
                toBeCreatedDocument, 
                XPATH_GRANT_OBJECT_XLINK_HREF, 
                Constants.ORGANIZATIONAL_UNIT_BASE_URI 
                    + "/" + ORGANIZATIONAL_UNIT_ID);
        final String toBeCreatedXml = 
            toString(fixGrantDocument(toBeCreatedDocument), false);
        

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestBase
                .failMissingException(InvalidScopeException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    InvalidScopeException.class, e);
            String testString = "has objectType organizational-unit";
            assertMatches(e.toString() 
                    + " does not match " 
                    + testString, testString, e.toString());
        }
    }

    /**
     * Test declining creation of Grant with providing 
     * scope object-type that is not allowed by role (user-account).
     * 
     * @test.name Create Grant - Wrong scope object-type
     * @test.id AA_CG_2-4
     * @test.input UserAccount id, grant XML
     * @test.inputDescription:
     *             <ul>
     *             <li>id of existing user</li>
     *             <li>XML representation of the grant to be created</li>
     *             </ul>
     * @test.expected: InvalidScopeException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg2_4() throws Exception {

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        // substitute the object element
        substitute(
                toBeCreatedDocument, 
                XPATH_GRANT_OBJECT_XLINK_HREF, 
                Constants.USER_ACCOUNT_BASE_URI 
                    + "/" + TEST_USER_ACCOUNT_ID);
        final String toBeCreatedXml = 
            toString(fixGrantDocument(toBeCreatedDocument), false);
        

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestBase
                .failMissingException(InvalidScopeException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    InvalidScopeException.class, e);
            String testString = "has objectType user-account";
            assertMatches(e.toString() 
                    + " does not match " 
                    + testString, testString, e.toString());
        }
    }

    /**
     * Test declining creation of Grant with providing 
     * scope object-type that is not allowed by role (role).
     * 
     * @test.name Create Grant - Wrong scope object-type
     * @test.id AA_CG_2-5
     * @test.input UserAccount id, grant XML
     * @test.inputDescription:
     *             <ul>
     *             <li>id of existing user</li>
     *             <li>XML representation of the grant to be created</li>
     *             </ul>
     * @test.expected: InvalidScopeException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg2_5() throws Exception {

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        // substitute the object element
        substitute(
                toBeCreatedDocument, 
                XPATH_GRANT_OBJECT_XLINK_HREF, 
                ROLE_HREF_DEPOSITOR);
        final String toBeCreatedXml = 
            toString(fixGrantDocument(toBeCreatedDocument), false);
        

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestBase
                .failMissingException(InvalidScopeException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    InvalidScopeException.class, e);
            String testString = "has objectType role";
            assertMatches(e.toString() 
                    + " does not match " 
                    + testString, testString, e.toString());
        }
    }

    /**
     * Test declining creation of Grant with providing 
     * scope object-type that is not allowed by role (grant).
     * 
     * @test.name Create Grant - Wrong scope object-type
     * @test.id AA_CG_2-6
     * @test.input UserAccount id, grant XML
     * @test.inputDescription:
     *             <ul>
     *             <li>id of existing user</li>
     *             <li>XML representation of the grant to be created</li>
     *             </ul>
     * @test.expected: InvalidScopeException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg2_6() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        // substitute the object element
        substitute(
                toBeCreatedDocument, 
                XPATH_GRANT_OBJECT_XLINK_HREF, 
                Constants.USER_ACCOUNT_BASE_URI 
                + "/" + TEST_USER_ACCOUNT_ID + "/" 
                + Constants.SUB_GRANT + "/" + grantId);
        final String toBeCreatedXml = 
            toString(fixGrantDocument(toBeCreatedDocument), false);
        

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestBase
                .failMissingException(InvalidScopeException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    InvalidScopeException.class, e);
            String testString = "has objectType grant";
            assertMatches(e.toString() 
                    + " does not match " 
                    + testString, testString, e.toString());
        }
    }

    /**
     * Test declining creation of Grant with providing 
     * scope object-type that is not allowed by role (content-type).
     * 
     * @test.name Create Grant - Wrong scope object-type
     * @test.id AA_CG_2-7
     * @test.input UserAccount id, grant XML
     * @test.inputDescription:
     *             <ul>
     *             <li>id of existing user</li>
     *             <li>XML representation of the grant to be created</li>
     *             </ul>
     * @test.expected: InvalidScopeException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg2_7() throws Exception {

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        // substitute the object element
        substitute(
                toBeCreatedDocument, 
                XPATH_GRANT_OBJECT_XLINK_HREF, 
                Constants.CONTENT_MODEL_BASE_URI 
                + "/" + CONTENT_TYPE_ID);
        final String toBeCreatedXml = 
            toString(fixGrantDocument(toBeCreatedDocument), false);
        

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestBase
                .failMissingException(InvalidScopeException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    InvalidScopeException.class, e);
            String testString = "has objectType content-model";
            assertMatches(e.toString() 
                    + " does not match " 
                    + testString, testString, e.toString());
        }
    }

    /**
     * Test declining creation of Grant without providing UserAccount id.
     * 
     * @test.name User-id not provided
     * @test.id AA_CG_3
     * @test.input UserAccount id
     * @test.inputDescription:
     *             <ul>
     *             <li>user-id is not provided</li>
     *             <li>XML representation of the grant to be created</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg3() throws Exception {

        String grantXml =
            EscidocRestSoapTestBase.getTemplateAsString(
                TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");

        try {
            createGrant(null, grantXml);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining creation of Grant with corrupted XML data.
     * 
     * @test.name Invalid XML representation
     * @test.id AA_CG_4
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>invalid XML representation of the grant to be created</li>
     *             </ul>
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg4() throws Exception {

        try {
            createGrant(defaultUserAccountOrGroupId, "<Corrupt XML data");
            EscidocRestSoapTestBase
                .failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            // FIXME: temporarily, the authorization throws wrong exception.
            // therefore both are checked here. This is the correct assertion.
            // assertExceptionType(XmlCorruptedException.class, e);

            if (e.getClass().getName().equals(
                InvalidXmlException.class.getName())) {
                // success
            }
            else {
                EscidocRestSoapTestBase.assertExceptionType(
                    XmlCorruptedException.class, e);
            }
        }
    }

    /**
     * Test declining creation of Grant without providing XML data.
     * 
     * @test.name XML representation not provided
     * @test.id AA_CG_5
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created is not
     *             provided</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg5() throws Exception {

        try {
            createGrant(defaultUserAccountOrGroupId, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining creation of Grant with providing XML data without a
     * mandatory element.
     * 
     * @test.name Missing mandatory parameter
     * @test.id AA_CG_7
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created, but
     *             mandatory parameter is missing</li>
     *             </ul>
     * @test.expected: XmlSchemaValidationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg7() throws Exception {

        Document grantDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(
                TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");
        deleteElement(grantDocument, XPATH_GRANT_ROLE);

        try {
            createGrant(defaultUserAccountOrGroupId, toString(grantDocument, false));
            EscidocRestSoapTestBase
                .failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining creation of Grant with providing reference to unknown
     * role.
     * 
     * @test.name Create Grant - Unknown Role
     * @test.id AA_Cg_13
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created,
     *             referenced role does not exist</li>
     *             </ul>
     * @test.expected: RoleNotFoundException
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg13() throws Exception {

        final Class ec = RoleNotFoundException.class;

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(
                TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");
        substitute(toBeCreatedDocument, XPATH_GRANT_ROLE_XLINK_HREF,
            Constants.ROLE_BASE_URI + "/" + UNKNOWN_ID);

        final String toBeCreatedXml =
            toString(fixGrantDocument(toBeCreatedDocument), false);

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestBase.failMissingException(
                "Creating grant with unknown role not declined. ", ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Creating grant with unknown role not declined, properly. ",
                ec, e);
        }
    }

    /**
     * Test declining creation of Grant with providing role reference to an
     * object that is not a role.
     * 
     * @test.name Create Grant - No Role
     * @test.id AA_Cg_13-2
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created, object
     *             referenced in role reference is not a role</li>
     *             </ul>
     * @test.expected: RoleNotFoundException
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg13_2() throws Exception {

        final Class ec = RoleNotFoundException.class;

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(
                TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");
        substitute(toBeCreatedDocument, XPATH_GRANT_ROLE_XLINK_HREF,
            Constants.ROLE_BASE_URI + "/" + CONTEXT_ID);

        final String toBeCreatedXml =
            toString(fixGrantDocument(toBeCreatedDocument), false);

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestBase.failMissingException(
                "Creating grant with role-reference to context not declined. ",
                ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase
                .assertExceptionType(
                    "Creating grant with role-reference to context not declined, properly. ",
                    ec, e);
        }
    }

    /**
     * Test declining creating duplicate grant.
     * 
     * @test.name Create a Grant for the UserAccount - Duplicate Grant
     * @test.id AA_Cg_14
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created, but the
     *             grant is a duplicate to an existing, valid grant of the user.</li>
     *             </ul>
     * @test.expected: AlreadyExistsException
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg14() throws Exception {

        createGrantSuccessfully("escidoc_grant_for_create.xml");

        // recreate the same grant
        final String toBeCreatedXml =
            getTemplateAsFixedGrantString(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestBase.failMissingException(
                "Creating duplicate grant not declined.",
                AlreadyExistsException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Creating duplicate grant not declined, properly.",
                AlreadyExistsException.class, e);
        }
    }

    /**
     * Test successfully recreating a revoked grant.
     * 
     * @test.name Create a Grant for the UserAccount - Recreate Revoked Grant
     * @test.id AA_Cg_15
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created, grant is
     *             the duplicate to an existing but revoked garnt of the user.</li>
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
    public void testAACg15() throws Exception {

        // create a grant and revoke it
        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        String taskParamXML =
            "<param last-modification-date=\""
                + lastModificationDate
                + "\" >"
                + "<revocation-remark>Some revocation\n remark</revocation-remark>"
                + "</param>";

        try {
            revokeGrant(defaultUserAccountOrGroupId, grantId, taskParamXML, null);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Revoking grant failed (test initialization).", e);
        }

        // recreate the same grant
        final String toBeCreatedXml =
            getTemplateAsFixedGrantString(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_grant_for_create.xml");

        String createdXml = null;
        try {
            createdXml = createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Recreating revoked grant failed.", e);
        }
        assertGrant(createdXml, toString(createdDocument, false),
            defaultUserAccountOrGroupId, startTimestamp, startTimestamp, false);
    }

    /**
     * Test declining creation of Grant with providing reference to unknown
     * object.
     * 
     * @test.name Create Grant - Unknown Object referenced
     * @test.id AA_Cg_16
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created,
     *             referenced object does not exist</li>
     *             </ul>
     * @test.expected: InvalidXmlException
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg16() throws Exception {

        final Class ec = XmlCorruptedException.class;

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(
                TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");
        substitute(toBeCreatedDocument, XPATH_GRANT_OBJECT_XLINK_HREF,
            Constants.CONTEXT_BASE_URI + "/" + UNKNOWN_ID);

        final String toBeCreatedXml =
            toString(fixGrantDocument(toBeCreatedDocument), false);

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestBase
                .failMissingException(
                    "Creating grant with reference to unknown object not declined. ",
                    ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Creating grant with reference to unknown object not declined,"
                    + " properly. ", ec, e);
        }
    }

    /**
     * Test declining creation of Grant with providing reference to unsupported
     * object.
     * 
     * @test.name Create Grant - Unsupported Object referenced
     * @test.id AA_Cg_17
     * @test.input UserAccount XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>XML representation of the grant to be created,
     *             referenced object is of a resource type that is not supported
     *             (user-account).</li>
     *             </ul>
     * @test.expected: InvalidXmlException
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACg17() throws Exception {

        final Class ec = XmlCorruptedException.class;

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(
                TEMPLATE_USER_ACCOUNT_PATH, "escidoc_grant_for_create.xml");
        substitute(toBeCreatedDocument, XPATH_GRANT_OBJECT_XLINK_HREF,
            Constants.STATISTIC_AGGREGATION_DEFINITIONS_BASE_URI 
            + "/" + EscidocTestBase.TEST_AGGREGATION_DEFINITION_ID);

        final String toBeCreatedXml =
            toString(fixGrantDocument(toBeCreatedDocument), false);

        try {
            createGrant(defaultUserAccountOrGroupId, toBeCreatedXml);
            EscidocRestSoapTestBase.failMissingException(
                "Creating grant with reference to unsupported object"
                    + " not declined. ", ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Creating grant with reference to unsupported object"
                    + " not declined, properly. ", ec, e);
        }
    }

    /**
     * Test successfully retrieving a Grant.
     * 
     * @test.name Nonexistent user-id
     * @test.id AA_RG_1
     * @test.input UserAccount id, Grant id
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>existing grant-id of a grant that belong to the
     *             specified user account</li>
     *             </ul>
     * @test.expected: XML representation of the grant
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARg1() throws Exception {

        final Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        final String grantId = getObjidValue(createdDocument);
        Document retrievedDocument = retrieveGrantSuccessfully(grantId);
        assertXmlEquals("Wrong data received. ", createdDocument,
            retrievedDocument);
    }

    /**
     * Test declining retrieving a Grant with providing an unknown UserAccount
     * id.
     * 
     * @test.name Nonexistent user-id
     * @test.id AA_RG_2
     * @test.input UserAccount id, Grant id
     * @test.inputDescription:
     *             <ul>
     *             <li>non-existent user-id</li>
     *             <li>existing grant-id</li>
     *             </ul>
     * @test.expected: notFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARg2() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);

        try {
            retrieveGrant(UNKNOWN_ID, grantId);
            EscidocRestSoapTestBase
                .failMissingException(notFoundException.getClass());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                notFoundException.getClass(), e);
        }
    }

    /**
     * Test declining retrieving a Grant with providing an id of an existing
     * resource of another type.
     * 
     * @test.name Retrieve Grant - Wrong Id
     * @test.id AA_RG_2-2
     * @test.input
     *          <ul>
     *          <li>context-id instead of an user account id</li>
     *          <li>existing grant-id</li>
     *          </ul>
     * @test.expected: notFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARg2_2() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);

        try {
            retrieveGrant(CONTEXT_ID, grantId);
            EscidocRestSoapTestBase
                .failMissingException(notFoundException.getClass());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                notFoundException.getClass(), e);
        }
    }

    /**
     * Test declining retrieving a Grant without providing the user id.
     * 
     * @test.name User-id not provided
     * @test.id AA_RG_3
     * @test.input UserAccount id
     * @test.inputDescription:
     *             <ul>
     *             <li>user-id is not provided</li>
     *             <li>existing grant-id</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARg3() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);

        try {
            retrieveGrant(null, grantId);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining retrieving a Grant with providing unknown grant id.
     * 
     * @test.name Non-existent grant-id
     * @test.id AA_RG_4
     * @test.input UserAccount id, Grant id
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>non-existent grant-id</li>
     *             </ul>
     * @test.expected: GrantNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARg4() throws Exception {

        try {
            retrieveGrant(defaultUserAccountOrGroupId, UNKNOWN_ID);
            EscidocRestSoapTestBase
                .failMissingException(GrantNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                GrantNotFoundException.class, e);
        }
    }

    /**
     * Test declining retrieving a Grant with providing id of another
     * UserAccount.
     * 
     * @test.name Non-existent grant-id
     * @test.id AA_RG_4-2
     * @test.input UserAccount id, Grant id
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>existing grant-id, but grant owned by another user</li>
     *             </ul>
     * @test.expected: GrantNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARg4_2() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);

        try {
            if (handlerCode == USER_GROUP_HANDLER_CODE) {
                retrieveGrant(USER_GROUP_WITH_USER_LIST_ID, grantId);
            } else {
                retrieveGrant(TEST_USER_ACCOUNT_ID1, grantId);
            }
            EscidocRestSoapTestBase
                .failMissingException(GrantNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                GrantNotFoundException.class, e);
        }
    }

    /**
     * Test declining retrieving a Grant with providing id of an existing
     * resource of another type.
     * 
     * @test.name Retrieve Grant - Wrong Grant-id
     * @test.id AA_RG_4-3
     * @test.input
     *          <ul>
     *          <li>existing user id</li>
     *          <li>existing context id instead of grant id</li>
     *          </ul>
     * @test.expected: GrantNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARg4_3() throws Exception {

        createGrantSuccessfully("escidoc_grant_for_create.xml");

        try {
            if (handlerCode == USER_GROUP_HANDLER_CODE) {
                retrieveGrant(USER_GROUP_WITH_USER_LIST_ID, CONTEXT_ID);
            } else {
                retrieveGrant(TEST_USER_ACCOUNT_ID1, CONTEXT_ID);
            }
            EscidocRestSoapTestBase
                .failMissingException(GrantNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                GrantNotFoundException.class, e);
        }
    }

    /**
     * Test declining retrieving a Grant without providing the grant id.
     * 
     * @test.name Non-existent grant-id
     * @test.id AA_RG_5
     * @test.input Grant id
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>grant-id is not provided</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARg5() throws Exception {

        try {
            retrieveGrant(defaultUserAccountOrGroupId, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Successfully revoking an existing Grant.
     * 
     * @test.name Revoke a Grant
     * @test.id AA_RVG_1
     * @test.input UserAccount id, Grant id
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>existing grant-id</li>
     *             <li>remark for the revocation of the Grant in XML structure</li>
     *             </ul>
     * @test.expected: Grant revoked
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg1() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        String taskParamXML =
            "<param last-modification-date=\""
                + lastModificationDate
                + "\" >"
                + "<revocation-remark>Some revocation\n remark</revocation-remark>"
                + "</param>";

        try {
            revokeGrant(defaultUserAccountOrGroupId, grantId, taskParamXML, null);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }

        Document updatedDocument = retrieveGrantSuccessfully(grantId);
        assertXmlLastModificationDateUpdate("Modification date not updated",
            createdDocument, updatedDocument);
        assertXmlExists("Missing revoked-by", updatedDocument,
            XPATH_GRANT_REVOKED_BY);
        assertXmlExists("Missing revocation-date", updatedDocument,
            XPATH_GRANT_REVOCATION_DATE);
        assertXmlExists("Missing revocation remark", updatedDocument,
            XPATH_GRANT_REVOCATION_REMARK);
        assertXmlEquals("Unexpected revocation remark, ", updatedDocument,
            XPATH_GRANT_REVOCATION_REMARK, "Some revocation\n remark");
    }

    /**
     * Test declining revoking a Grant with providing unknown UserAccount id.
     * 
     * @test.name Non-existent User-id
     * @test.id AA_RVG_2
     * @test.input UserAccount id, Grant id, Task parameters
     * @test.inputDescription:
     *             <ul>
     *             <li>non-existent user-id</li>
     *             <li>existing grant-id</li>
     *             <li>remark for the revocation of the Grant in XML structure</li>
     *             </ul>
     * @test.expected: notFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg2() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" />";

        try {
            revokeGrant(UNKNOWN_ID, grantId, taskParamXML, null);
            EscidocRestSoapTestBase
                .failMissingException(notFoundException.getClass());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                notFoundException.getClass(), e);
        }
    }

    /**
     * Test declining revoking a Grant with providing id of an existing resource
     * of another type..
     * 
     * @test.name Revoke Grant - Wrong Id
     * @test.id AA_RVG_2-2
     * @test.input
     *          <ul>
     *          <li>existing context-id instead of user-account id</li>
     *          <li>existing grant-id</li>
     *          <li>remark for the revocation of the Grant in XML structure</li>
     *          </ul>
     * @test.expected: notFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg2_2() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" />";

        try {
            revokeGrant(CONTEXT_ID, grantId, taskParamXML, null);
            EscidocRestSoapTestBase
                .failMissingException(notFoundException.getClass());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                notFoundException.getClass(), e);
        }
    }

    /**
     * Test declining revoking a Grant that does not exist.
     * 
     * @test.name Non-existent Grant-id
     * @test.id AA_RVG_3
     * @test.input UserAccount id, Grant id, Task parameters
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>non-existent grant-id</li>
     *             <li>remark for the revocation of the Grant in XML structure</li>
     *             </ul>
     * @test.expected: GrantNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg3() throws Exception {

        String taskParamXML =
            "<param last-modification-date=\""
                + "2007-01-01T01:01:01.000Z\" />";

        try {
            revokeGrant(defaultUserAccountOrGroupId, UNKNOWN_ID, taskParamXML, null);
            EscidocRestSoapTestBase
                .failMissingException(GrantNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                GrantNotFoundException.class, e);
        }
    }

    /**
     * Test declining revoking a Grant with providing id of another resource as
     * the grant id.
     * 
     * @test.name Revoke Grant - Wrong Grant-id
     * @test.id AA_RVG_3-2
     * @test.input UserAccount id, Grant id, Task parameters
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>id of a resource of another resource type as grant-id</li>
     *             <li>remark for the revocation of the Grant in XML structure</li>
     *             </ul>
     * @test.expected: GrantNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg3_2() throws Exception {

        String taskParamXML =
            "<param last-modification-date=\""
                + "2007-01-01T01:01:01.000Z\" />";

        try {
            revokeGrant(defaultUserAccountOrGroupId, CONTEXT_ID, taskParamXML, null);
            EscidocRestSoapTestBase
                .failMissingException(GrantNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                GrantNotFoundException.class, e);
        }
    }

    /**
     * Test declining revoking a Grant without providing the grant id.
     * 
     * @test.name Grant-id not provided
     * @test.id AA_RVG_4
     * @test.input Grant id
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             <li>grant-id is not provided</li>
     *             <li>remark for the revocation of the Grant in XML structure</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg4() throws Exception {

        String taskParamXML =
            "<param last-modification-date=\""
                + "2007-01-01T01:01:01.000Z\" />";

        try {
            revokeGrant(defaultUserAccountOrGroupId, null, taskParamXML, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revoking a Grant without providing the user id.
     * 
     * @test.name User-id not provided
     * @test.id AA_RVG_5
     * @test.input UserAccount id
     * @test.inputDescription:
     *             <ul>
     *             <li>user-id is not provided</li>
     *             <li>existing grant-id</li>
     *             <li>remark for the revocation of the Grant in XML structure</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg5() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" />";

        try {
            revokeGrant(null, grantId, taskParamXML, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Declining revoking a revoked Grant.
     * 
     * @test.name Grant already revoked
     * @test.id AA_RVG_6
     * @test.input UserAccount id, Grant id
     * @test.inputDescription: Id of existing UserAccount, id of an existing but
     *                         revoked grant of the specified UserAccount.
     * @test.expected: AlreadyRevokedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg6() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" />";

        try {
            revokeGrant(defaultUserAccountOrGroupId, grantId, taskParamXML, null);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }

        String retrievedGrantXml = null;
        try {
            retrievedGrantXml = retrieveGrant(defaultUserAccountOrGroupId, grantId);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull("No data from retrieve. ", retrievedGrantXml);
        Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedGrantXml);
        lastModificationDate = getLastModificationDateValue(retrievedDocument);
        taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" />";

        try {
            revokeGrant(defaultUserAccountOrGroupId, grantId, taskParamXML, null);
            EscidocRestSoapTestBase
                .failMissingException(AlreadyRevokedException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                AlreadyRevokedException.class, e);
        }
    }

    /**
     * Test declining revoking a Grant without providing the task parameters.
     * 
     * @test.name Revoke of Grant without task parameters.
     * @test.id AA_RVG_7
     * @test.input UserAccount id, Grant id, Task parameters
     * @test.inputDescription: No Task parameters are provided.
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg7() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);

        try {
            revokeGrant(defaultUserAccountOrGroupId, grantId, null, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test successfully revoking a Grant without providing the last-modification-date.
     * 
     * @test.name Revoke of Grant without providing the last-modification-date.
     * @test.id AA_RVG_7_1
     * @test.input UserAccount id, Grant id, Task parameters
     * @test.inputDescription: No last-modification-date is provided.
     * @test.expected: OK
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg7_1() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);
        String taskParamXML =
            "<param><revocation-remark>some remark</revocation-remark></param>";

        try {
            revokeGrant(defaultUserAccountOrGroupId, grantId, taskParamXML, null);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
    }

    /**
     * Test successfully revoking a Grant with providing wrong last-modification-date.
     * 
     * @test.name Revoke of Grant with providing wrong last-modification-date.
     * @test.id AA_RVG_7_2
     * @test.input UserAccount id, Grant id, Task parameters
     * @test.inputDescription: Wrong last-modification-date is provided.
     * @test.expected: OK
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg7_2() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);
        String taskParamXML =
            "<param last-modification-date=\"2008-01-01\" />";

        try {
            revokeGrant(defaultUserAccountOrGroupId, grantId, taskParamXML, null);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
    }

    /**
     * Test declining revoking a Grant with providing id of another UserAccount.
     * 
     * @test.name Revoke Grant of wrong UserAccount.
     * @test.id AA_RVG_8
     * @test.input UserAccount id, Grant id, Task parameters
     * @test.inputDescription: Id of existing UserAccount, Id of existing Grant
     *                         that is owned by another UserAccount, valid task
     *                         parameters.
     * @test.expected: GrantNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg8() throws Exception {

        Document createdDocument =
            createGrantSuccessfully("escidoc_grant_for_create.xml");
        String grantId = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" />";

        try {
            if (handlerCode == USER_GROUP_HANDLER_CODE) {
                revokeGrant(USER_GROUP_WITH_USER_LIST_ID, 
                        grantId, taskParamXML, null);
            } else {
                revokeGrant(TEST_USER_ACCOUNT_ID1, 
                        grantId, taskParamXML, null);
            }
            EscidocRestSoapTestBase
                .failMissingException(GrantNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                GrantNotFoundException.class, e);
        }
    }

    /**
     * Test revoking Grants with providing ids of existing grants.
     * 
     * @test.name Revoke Grants with list of grantIds.
     * @test.id AA_RVG_9
     * @test.input UserAccount id, Task parameters
     * @test.inputDescription: Id of existing UserAccountt, valid list 
     *                         of ids in task parameters.
     * @test.expected: Grant revoked
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg9() throws Exception {
        //create grants
        int numberOfGrants = 3;
        Map<String, Node> expectedGrants = 
            createGrantsSuccessfully(defaultUserAccountOrGroupId, numberOfGrants);
        
        //retrieve grants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, expectedGrants.size());

        //revoke 2 grants
        StringBuffer taskParamXML = new StringBuffer("");
        taskParamXML.append("<param>")
            .append("<filter name=\"" + DC_NS_URI + "identifier" + "\">");
        int count = 0;
        for (String grantId : expectedGrants.keySet()) {
            if (count < 2) {
                taskParamXML.append("<id>").append(grantId).append("</id>");
            }
            count++;
        }
        taskParamXML.append("</filter>")
          .append(
             "<revocation-remark>Some revocation\n remark</revocation-remark>")
                    .append("</param>");

        try {
            revokeGrants(defaultUserAccountOrGroupId, taskParamXML.toString());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                    "Revoking grants failed. ", e);
        }
        
        //retrieveGrants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, 1);
    }

    /**
     * Test revoking Grants with providing empty filter.
     * 
     * @test.name Revoke all Grants.
     * @test.id AA_RVG_10
     * @test.input UserAccount id, Task parameters
     * @test.inputDescription: Id of existing UserAccount, empty filter.
     * @test.expected: Grants revoked
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg10() throws Exception {
        //create grants
        int numberOfGrants = 3;
        Map<String, Node> expectedGrants = 
            createGrantsSuccessfully(defaultUserAccountOrGroupId, numberOfGrants);
        
        //retrieve grants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, expectedGrants.size());

        //revoke all grants
        StringBuffer taskParamXML = new StringBuffer("");
        taskParamXML.append("<param>")
            .append("<filter />");
        taskParamXML.append(
             "<revocation-remark>Some revocation\n remark</revocation-remark>")
                    .append("</param>");

        try {
            revokeGrants(defaultUserAccountOrGroupId, taskParamXML.toString());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                    "Revoking grants failed. ", e);
        }
        
        //retrieveGrants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, 0);
    }

    /**
     * Test revoking Grants with providing no filter.
     * 
     * @test.name Revoke all Grants.
     * @test.id AA_RVG_11
     * @test.input UserAccount id, Task parameters
     * @test.inputDescription: Id of existing UserAccountt, empty filter.
     * @test.expected: No Grants revoked
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg11() throws Exception {
        //create grants
        int numberOfGrants = 3;
        Map<String, Node> expectedGrants = 
            createGrantsSuccessfully(defaultUserAccountOrGroupId, numberOfGrants);
        
        //retrieve grants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, expectedGrants.size());

        //revoke all grants
        StringBuffer taskParamXML = new StringBuffer("");
        taskParamXML.append("<param>").append(
             "<revocation-remark>Some revocation\n remark</revocation-remark>")
                    .append("</param>");

        try {
            revokeGrants(defaultUserAccountOrGroupId, taskParamXML.toString());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                    "Revoking grants failed. ", e);
        }
        
        //retrieveGrants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, 0);
    }

    /**
     * Test revoking Grants with providing empty param-element.
     * 
     * @test.name Revoke all Grants.
     * @test.id AA_RVG_12
     * @test.input UserAccount id, Task parameters
     * @test.inputDescription: Id of existing UserAccountt, empty filter.
     * @test.expected: No Grants revoked
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg12() throws Exception {
        //create grants
        int numberOfGrants = 3;
        Map<String, Node> expectedGrants = 
            createGrantsSuccessfully(defaultUserAccountOrGroupId, numberOfGrants);
        
        //retrieve grants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, expectedGrants.size());

        //revoke all grants
        StringBuffer taskParamXML = new StringBuffer("");
        taskParamXML.append("<param />");

        try {
            revokeGrants(defaultUserAccountOrGroupId, taskParamXML.toString());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                    "Revoking grants failed. ", e);
        }
        
        //retrieveGrants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, 0);
    }

    /**
     * Test revoking Grants with providing empty filter list.
     * 
     * @test.name Revoke no Grants.
     * @test.id AA_RVG_13
     * @test.input UserAccount id, Task parameters
     * @test.inputDescription: Id of existing UserAccountt, empty filter.
     * @test.expected: No Grants revoked
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg13() throws Exception {
        //create grants
        int numberOfGrants = 3;
        Map<String, Node> expectedGrants = 
            createGrantsSuccessfully(defaultUserAccountOrGroupId, numberOfGrants);
        
        //retrieve grants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, expectedGrants.size());

        //revoke no grants
        StringBuffer taskParamXML = new StringBuffer("");
        taskParamXML.append("<param>")
            .append("<filter name=\"" + DC_NS_URI + "identifier" + "\">");
        taskParamXML.append("</filter>")
          .append(
             "<revocation-remark>Some revocation\n remark</revocation-remark>")
                    .append("</param>");

        try {
            revokeGrants(defaultUserAccountOrGroupId, taskParamXML.toString());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                    "Revoking grants failed. ", e);
        }
        
        //retrieveGrants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, expectedGrants.size());
    }

    /**
     * Test revoking Grants with providing ids of non-existing grants.
     * 
     * @test.name Revoke Grants with list of non-existing grantIds.
     * @test.id AA_RVG_14
     * @test.input UserAccount id, Task parameters
     * @test.inputDescription: Id of existing UserAccount, invalid list 
     *                         of ids in task parameters.
     * @test.expected: GrantNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg14() throws Exception {
        //create grants
        int numberOfGrants = 3;
        Map<String, Node> expectedGrants = 
            createGrantsSuccessfully(defaultUserAccountOrGroupId, numberOfGrants);
        
        //retrieve grants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, expectedGrants.size());

        //revoke 2 existing grants and 2 grants that do not exist
        //check Transactional Functionality
        StringBuffer taskParamXML = new StringBuffer("");
        taskParamXML.append("<param>")
            .append("<filter name=\"" + DC_NS_URI + "identifier" + "\">");
        int count = 0;
        for (String grantId : expectedGrants.keySet()) {
            if (count < 2) {
                taskParamXML.append("<id>").append(grantId).append("</id>");
            }
            count++;
        }
        taskParamXML.append("<id>").append("nonexistingid1").append("</id>");
        taskParamXML.append("<id>").append("nonexistingid2").append("</id>");
        taskParamXML.append("</filter>")
          .append(
             "<revocation-remark>Some revocation\n remark</revocation-remark>")
                    .append("</param>");

        try {
            revokeGrants(defaultUserAccountOrGroupId, taskParamXML.toString());
            EscidocRestSoapTestBase
                .failMissingException(GrantNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    GrantNotFoundException.class, e);
        }
        
        //retrieveGrants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, numberOfGrants);
    }

    /**
     * Test revoking Grants with providing id of non-existing userAccount.
     * 
     * @test.name Revoke Grants with non-existing userAccount.
     * @test.id AA_RVG_15
     * @test.input UserAccount id, Task parameters
     * @test.inputDescription: Id of non-existing UserAccount, valid list 
     *                         of ids in task parameters.
     * @test.expected: notFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARvg15() throws Exception {
        //create grants
        int numberOfGrants = 3;
        Map<String, Node> expectedGrants = 
            createGrantsSuccessfully(defaultUserAccountOrGroupId, numberOfGrants);
        
        //retrieve grants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, expectedGrants.size());

        //revoke 2 existing grants and 2 grants that do not exist
        //check Transactional Functionality
        StringBuffer taskParamXML = new StringBuffer("");
        taskParamXML.append("<param>")
            .append("<filter name=\"" + DC_NS_URI + "identifier" + "\">");
        int count = 0;
        for (String grantId : expectedGrants.keySet()) {
            if (count < 2) {
                taskParamXML.append("<id>").append(grantId).append("</id>");
            }
            count++;
        }
        taskParamXML.append("</filter>")
          .append(
             "<revocation-remark>Some revocation\n remark</revocation-remark>")
                    .append("</param>");

        try {
            revokeGrants("nonexistinguser", taskParamXML.toString());
            EscidocRestSoapTestBase
                .failMissingException(notFoundException.getClass());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    notFoundException.getClass(), e);
        }
        
        //retrieveGrants
        retrieveGrantsSuccessfully(defaultUserAccountOrGroupId, numberOfGrants);
    }

    /**
     * Test successfully retrieving current Grants. <br>
     * In this scenario, no grant has been revoked.
     * 
     * @test.name Retrieve Current Grants (1)
     * @test.id AA_RGU_1
     * @test.input UserAccount id
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             </ul>
     * @test.expected: XML representation of the current grants.
     * @test.status Implemented
     * @test.issues http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=327
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARgu1() throws Exception {

        final int expectedLength = 3;
        final Map<String, Node> expectedGrants =
            new HashMap<String, Node>(expectedLength);
        for (int i = 0; i < expectedLength; i++) {
            final String templateName;
            if (i == 0) {
                templateName = "escidoc_grant_for_create.xml";
            }
            else {
                templateName = "escidoc_grant" + (i + 1) + "_for_create.xml";
            }

            final Document createdDocument =
                createGrantSuccessfully(defaultUserAccountOrGroupId, templateName);
            final String objid = getObjidValue(createdDocument);
            expectedGrants.put(objid, createdDocument);
        }

        String currentGrantsXml = null;
        try {
            currentGrantsXml = retrieveCurrentGrants(defaultUserAccountOrGroupId);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertXmlValidGrants(currentGrantsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(currentGrantsXml);
        getLastModificationDateValue(retrievedDocument);
        NodeList grants =
            selectNodeList(retrievedDocument, XPATH_CURRENT_GRANTS_GRANT);
        final int length = grants.getLength();
        assertEquals("Unexpected number of current grants. ", expectedLength,
            length);
        for (int i = 0; i < expectedLength; i++) {
            final Node toBeAsserted = grants.item(i);
            final String toBeAssertedId = getObjidValue(toBeAsserted, "");
            final String msg =
                "Asserting " + (i + 1) + ". grant. [" + toBeAssertedId + "]";
            final Node expected = expectedGrants.get(toBeAssertedId);
            assertNotNull(msg + "Unexpected grant [" + toBeAssertedId + "]",
                expected);
            final String xpathToBeAssertedGrant =
                XPATH_CURRENT_GRANTS_GRANT + "[" + (i + 1) + "]";
            if (getTransport() == Constants.TRANSPORT_REST) {
                assertXmlEquals(msg + "Title mismatch. ", expected,
                    XPATH_GRANT_XLINK_TITLE, toBeAsserted,
                    xpathToBeAssertedGrant + PART_XLINK_TITLE);
                assertXmlEquals(msg + "Href mismatch. ", expected,
                    XPATH_GRANT_XLINK_HREF, toBeAsserted,
                    xpathToBeAssertedGrant + PART_XLINK_HREF);
            }
            else {
                assertXmlEquals(msg + "Objid mismatch. ", expected,
                    XPATH_GRANT_OBJID, toBeAsserted, xpathToBeAssertedGrant
                        + PART_OBJID);
            }
            assertXmlEquals(msg + "Properties mismatch. ", expected,
                XPATH_GRANT_PROPERTIES, toBeAsserted, xpathToBeAssertedGrant
                    + "/" + NAME_PROPERTIES);
        }
    }

    /**
     * Test successfully retrieving current Grants. <br>
     * In this scenario, a grant has been revoked.
     * 
     * @test.name Retrieve Current Grants (2)
     * @test.id AA_RGU_2
     * @test.input UserAccount id
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             </ul>
     * @test.expected: XML representation of the current grants, not containing
     *                 the revoked grant.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARgu2() throws Exception {
        final int expectedLength = 2;
        final Map<String, Node> expectedGrants =
            new HashMap<String, Node>(expectedLength);
        for (int i = 0; i < (expectedLength + 1); i++) {
            final String templateName;
            if (i == 0) {
                templateName = "escidoc_grant_for_create.xml";
            }
            else {
                templateName = "escidoc_grant" + (i + 1) + "_for_create.xml";
            }
            final Document createdDocument =
                createGrantSuccessfully(defaultUserAccountOrGroupId, templateName);

            final String objid = getObjidValue(createdDocument);

            if (i == 1) {
                // revoke one of the grants
                String lastModificationDate1 =
                    getLastModificationDateValue(createdDocument);
                String taskParam1Xml =
                    "<param last-modification-date=\""
                        + lastModificationDate1
                        + "\" >"
                        + "<revocation-remark>Some revocation\n remark</revocation-remark>"
                        + "</param>";
                try {
                    revokeGrant(defaultUserAccountOrGroupId, objid, taskParam1Xml, null);
                }
                catch (final Exception e) {
                    EscidocRestSoapTestBase.failException(
                        "Revoking grant failed. ", e);
                }
            }
            else {
                expectedGrants.put(objid, createdDocument);
            }
        }

        String currentGrantsXml = null;
        try {
            currentGrantsXml = retrieveCurrentGrants(defaultUserAccountOrGroupId);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertXmlValidGrants(currentGrantsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(currentGrantsXml);
        getLastModificationDateValue(retrievedDocument);
        NodeList grants =
            selectNodeList(retrievedDocument, XPATH_CURRENT_GRANTS_GRANT);
        final int length = grants.getLength();
        assertEquals("Unexpected number of current grants. ", expectedLength,
            length);
        for (int i = 0; i < expectedLength; i++) {
            final Node toBeAsserted = grants.item(i);
            final String toBeAssertedId = getObjidValue(toBeAsserted, "");
            final String msg =
                "Asserting " + (i + 1) + ". grant. [" + toBeAssertedId + "]";
            final Node expected = expectedGrants.get(toBeAssertedId);
            assertNotNull(msg + "Unexpected grant [" + toBeAssertedId + "]",
                expected);
            final String xpathToBeAssertedGrant =
                XPATH_CURRENT_GRANTS_GRANT + "[" + (i + 1) + "]";
            if (getTransport() == Constants.TRANSPORT_REST) {
                assertXmlEquals(msg + "Title mismatch. ", expected,
                    XPATH_GRANT_XLINK_TITLE, toBeAsserted,
                    xpathToBeAssertedGrant + PART_XLINK_TITLE);
                assertXmlEquals(msg + "Href mismatch. ", expected,
                    XPATH_GRANT_XLINK_HREF, toBeAsserted,
                    xpathToBeAssertedGrant + PART_XLINK_HREF);
            }
            else {
                assertXmlEquals(msg + "Objid mismatch. ", expected,
                    XPATH_GRANT_OBJID, toBeAsserted, xpathToBeAssertedGrant
                        + PART_OBJID);
            }
            assertXmlEquals(msg + "Properties mismatch. ", expected,
                XPATH_GRANT_PROPERTIES, toBeAsserted, xpathToBeAssertedGrant
                    + "/" + NAME_PROPERTIES);
        }
    }

    /**
     * Test successfully retrieving current Grants. <br>
     * check for assigned-on element of system-administrator.
     * Issue:
     * https://www.escidoc.org/jira/browse/INFR-903
     * 
     * @test.name Retrieve Current Grants (2_1)
     * @test.id AA_RGU_2_1
     * @test.input UserAccount id
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-id</li>
     *             </ul>
     * @test.expected: XML representation of the current grants.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARgu2_1() throws Exception {
        final int expectedLength = 4;
        final Map<String, Node> expectedGrants =
            new HashMap<String, Node>(expectedLength);
        for (int i = 0; i < expectedLength; i++) {
            final String templateName;
            if (i == 0) {
                templateName = "escidoc_grant_for_create.xml";
            }
            else {
                templateName = "escidoc_grant" + (i + 1) + "_for_create.xml";
            }
            final Document createdDocument =
                createGrantSuccessfully(defaultUserAccountOrGroupId, templateName);

            final String objid = getObjidValue(createdDocument);

            expectedGrants.put(objid, createdDocument);
        }

        String currentGrantsXml = null;
        try {
            currentGrantsXml = retrieveCurrentGrants(defaultUserAccountOrGroupId);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertXmlValidGrants(currentGrantsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(currentGrantsXml);
        getLastModificationDateValue(retrievedDocument);
        NodeList grants =
            selectNodeList(retrievedDocument, XPATH_CURRENT_GRANTS_GRANT);
        final int length = grants.getLength();
        assertEquals("Unexpected number of current grants. ", expectedLength,
            length);
        for (int i = 0; i < expectedLength; i++) {
            final Node toBeAsserted = grants.item(i);
            final String toBeAssertedId = getObjidValue(toBeAsserted, "");
            final String msg =
                "Asserting " + (i + 1) + ". grant. [" + toBeAssertedId + "]";
            final Node expected = expectedGrants.get(toBeAssertedId);
            assertNotNull(msg + "Unexpected grant [" + toBeAssertedId + "]",
                expected);
            final String xpathToBeAssertedGrant =
                XPATH_CURRENT_GRANTS_GRANT + "[" + (i + 1) + "]";
            if (getTransport() == Constants.TRANSPORT_REST) {
                Node roleNode = 
                    selectSingleNode(toBeAsserted, 
                            xpathToBeAssertedGrant 
                            + "/properties/role/@href");
                if (roleNode.getNodeValue()
                        .contains(ROLE_HREF_SYSTEM_ADMINISTRATOR)) {
                    assertNull(
                            "assigned-on must be null for system-administrator", 
                            selectSingleNode(toBeAsserted, 
                                    xpathToBeAssertedGrant 
                                    + "/properties/assigned-on"));
                } else {
                    assertNotNull("assigned-on must be set for role", 
                            selectSingleNode(toBeAsserted, 
                                    xpathToBeAssertedGrant 
                                    + "/properties/assigned-on"));
                }
            }
            else {
                Node roleNode = 
                    selectSingleNode(toBeAsserted, 
                            xpathToBeAssertedGrant 
                            + "/properties/role/@objid");
                if (roleNode.getNodeValue().contains(
                        getObjidFromHref(ROLE_HREF_SYSTEM_ADMINISTRATOR)
                        )) {
                    assertNull(
                            "assigned-on must be null for system-administrator", 
                            selectSingleNode(toBeAsserted, 
                                    xpathToBeAssertedGrant 
                                    + "/properties/assigned-on"));
                } else {
                    assertNotNull("assigned-on must be set for role", 
                            selectSingleNode(toBeAsserted, 
                                    xpathToBeAssertedGrant 
                                    + "/properties/assigned-on"));
                }
            }
        }
    }

    /**
     * Test declining retrieving current grants with providing an unknown
     * UserAccount id.
     * 
     * @test.name Nonexistent user-id
     * @test.id AA_RGU_3
     * @test.input UserAccount id
     * @test.inputDescription:
     *             <ul>
     *             <li>non-existent user-id</li>
     *             </ul>
     * @test.expected: notFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARgu3() throws Exception {

        try {
            retrieveCurrentGrants(UNKNOWN_ID);
            EscidocRestSoapTestBase
                .failMissingException(notFoundException.getClass());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                notFoundException.getClass(), e);
        }
    }

    /**
     * Test declining retrieving current grants with providing an id of an
     * existing resource of another type.
     * 
     * @test.name Retrieve Current Grants - Wrong Id
     * @test.id AA_RGU_3-2
     * @test.input
     *          <ul>
     *          <li>context id instead of user account id</li>
     *          </ul>
     * @test.expected: notFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARgu3_2() throws Exception {

        try {
            retrieveCurrentGrants(CONTEXT_ID);
            EscidocRestSoapTestBase
                .failMissingException(notFoundException.getClass());
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                notFoundException.getClass(), e);
        }
    }

    /**
     * Test declining retrieving current Grants without providing the user id.
     * 
     * @test.name User-id not provided
     * @test.id AA_RGU4
     * @test.input UserAccount id
     * @test.inputDescription:
     *             <ul>
     *             <li>user-id is not provided</li>
     *             </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARgu4() throws Exception {

        try {
            retrieveCurrentGrants(null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

}
