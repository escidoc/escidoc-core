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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.UserGroupNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.AlreadyActiveException;
import de.escidoc.core.common.exceptions.remote.application.violated.AlreadyDeactiveException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.remote.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.remote.application.violated.UserGroupHierarchyViolationException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.oum.organizationalunit.OrganizationalUnitTestBase;

/**
 * Test suite for the userGroup resource.
 * 
 * @author MIH
 * 
 */
public abstract class UserGroupTest extends UserGroupTestBase {

    public static final String XPATH_RDF_ABOUT =
        XPATH_USER_GROUP_LIST_USER_GROUP + "/@about";

    public static final String RDF_RESOURCE_USER_GROUP =
        "http://www.escidoc.de/core/01/resources/UserGroup";

    public static final String RDF_USER_GROUP_BASE_URI =
        "http://localhost:8080" + Constants.USER_GROUP_BASE_URI;

    private static UserAttributeTestBase userAttributeTestBase = null;

    private static UserAccountTestBase userAccountTestBase = null;

    private static OrganizationalUnitTestBase organizationalUnitTestBase = null;
    
    private static String userAccountAttributeUser;

    private static String userAccountOuUser;

    private static String parentGroup;
    
    private static String childGroup;
    
    private static int additonalUserFilterSearchGroupsCount = 5;

    private static String[] additonalUserFilterSearchGroups = 
                    new String[additonalUserFilterSearchGroupsCount];

    private static String uniqueIdentifier;

    private static int methodCounter = 0;

    /**
     * @param transport
     *            The transport identifier.
     * @throws Exception
     *             e
     */
    public UserGroupTest(final int transport) throws Exception {

        super(transport);
        userAttributeTestBase = new UserAttributeTestBase(transport);
        userAccountTestBase = new UserAccountTestBase(transport);
        organizationalUnitTestBase = new OrganizationalUnitTestBase(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        if (methodCounter == 0) {
            prepareUserGroupUserFilterData();
        }
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
        methodCounter++;
    }

    /**
     * Test successful creating an UserGroup resource.
     * 
     * @test.name Create UserGroup
     * @test.id AA_CUG-1
     * @test.input UserGroup XML representation
     * @test.inputDescription: Valid XML representation of the UserGroup.
     * @test.expected: XML representation of the created UserGroup
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACug1() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");

        assertEquals(
            "Creation date and last modification date are different. ",
            assertCreationDateExists("", createdDocument),
            getLastModificationDateValue(createdDocument));
    }

    /**
     * Test declining creation of UserGroup with corrupted XML data.
     * 
     * @test.name Create UserGroup - Corrupt Xml
     * @test.id AA_CUG-2
     * @test.input UserGroup XML representation
     * @test.inputDescription: Corrupted UserGroup XML representation for
     *                         creating a new UserGroup is provided.
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACug2() throws Exception {

        try {
            create("<Corrupt XML data");
            EscidocRestSoapTestBase
                .failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining creation of UserGroup without providing XML data.
     * 
     * @test.name Create UserGroup - xml not provided
     * @test.id AA_CUG-3
     * @test.input UserGroup XML representation
     * @test.inputDescription: No UserGroup XML representation for creating a
     *                         new UserGroup is provided.
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACug3() throws Exception {

        try {
            create(null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining creation of UserGroup with missing mandatory element in
     * XML data.
     * 
     * @test.name Missing mandatory element
     * @test.id AA_CUG-5
     * @test.input UserGroup XML representation
     * @test.inputDescription: UserGroup XML representation of the UserGroup
     *                         provided, where one of the mandatory elements is
     *                         missing (label)
     * @test.expected: XmlSchemaValidationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACug5() throws Exception {

        final Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getDocument(EscidocRestSoapTestBase
                .getTemplateAsString(TEMPLATE_USER_GROUP_PATH,
                    "escidoc_usergroup_for_create.xml"));
        deleteElement(toBeCreatedDocument, XPATH_USER_GROUP_LABEL);

        try {
            create(toString(toBeCreatedDocument, false));
            EscidocRestSoapTestBase
                .failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining creation of UserGroup with non-unique label in XML data.
     * 
     * @test.name Chosen label not unique
     * @test.id AA_CUG-6
     * @test.input UserGroup XML representation
     * @test.inputDescription: UserGroup XML representation of the UserGroup
     *                         provided, where label is n o t unique
     * @test.expected: UniqueConstraintViolationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACug6() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String toBeCreatedDocument = toString(createdDocument, false);

        try {
            create(toBeCreatedDocument);
            EscidocRestSoapTestBase
                .failMissingException(UniqueConstraintViolationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                UniqueConstraintViolationException.class, e);
        }
    }

    /**
     * Test successful retrieving an existing UserGroup resource by its id.
     * 
     * @test.name Retrieve UserGroup (by id).
     * @test.id AA_RUG-1
     * @test.input UserGroup id
     * @test.inputDescription: Valid id of an existing UserGroup.
     * @test.expected: XML representation of the UserGroup
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARug1() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String createdUserGroupXML = toString(createdDocument, false);

        String retrievedUserGroupXML = null;
        try {
            retrievedUserGroupXML = retrieve(id);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertUserGroup(retrievedUserGroupXML, createdUserGroupXML,
            startTimestamp, startTimestamp, true);
    }

    /**
     * Test successful retrieving an existing UserGroup resource by its label.
     * 
     * @test.name Retrieve UserGroup (by label).
     * @test.id AA_RUG-1-2
     * @test.input UserGroup label
     * @test.inputDescription: Valid label of an existing UserGroup.
     * @test.expected: XML representation of the UserGroup
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    // TODO: deactiveted, a new method retrieveByLabel() should be introduced
    public void UtestAARug1_2() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String label =
            selectSingleNode(createdDocument, XPATH_USER_GROUP_LABEL)
                .getTextContent();

        String retrievedXml = null;
        try {
            retrievedXml = retrieve(label);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertUserGroup(retrievedXml, toString(createdDocument, false),
            startTimestamp, startTimestamp, true);
    }

    /**
     * Test declining the retrieval of an UserGroup with unknown id.
     * 
     * @test.name: UserGroup key not existent
     * @test.id AA_RUG-2
     * @test.input: UserGroup id.
     * @test.inputDescription: UserGroup id that is unknown to the system.
     * @test.expected: UserGroupNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARug2() throws Exception {

        try {
            retrieve(UNKNOWN_ID);
            EscidocRestSoapTestBase
                .failMissingException(UserGroupNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Wrong exception. ",
                UserGroupNotFoundException.class, e);
        }
    }

    /**
     * Test declining the retrieval of an UserGroup with invalid id of an
     * existing resource of another resource type.
     * 
     * @test.name: Retrieve User Group - Wrong Id
     * @test.id AA_RUG-2-2
     * @test.input: Context id instead of an UserGroup id.
     * @test.expected: UserGroupNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARug2_2() throws Exception {

        try {
            retrieve(CONTEXT_ID);
            EscidocRestSoapTestBase
                .failMissingException(UserGroupNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Wrong exception. ",
                UserGroupNotFoundException.class, e);
        }
    }

    /**
     * Test declining the retrieval of an UserGroup with missing parameter id.
     * 
     * @test.name: UserGroupkey not provided
     * @test.id: UM_RUG-3
     * @test.input: UserGroup id.
     * @test.inputDescription: No UserGroup id is sent as parameter.
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARug3() throws Exception {
        try {
            retrieve(null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Wrong exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test successful activating an existing, deactive UserGroup.
     * 
     * @test.name Activate an UserGroup
     * @test.id AA_AUG-1
     * @test.input UserGroup id
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-group-id</li>
     *             <li>timestamp of the last modification of the UserGroup</li>
     *             </ul>
     * @test.expected: No result, no exception, UserGroup has been activated.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAAug1() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String createdXml = toString(createdDocument, false);
        final String id = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" />";
        final String beforeDeactivationTimestamp = getNowAsTimestamp();
        try {
            deactivate(id, taskParamXML);
        }
        catch (final Exception e1) {
            EscidocRestSoapTestBase.failException(e1);
        }

        String retrievedUserGroupXML = null;
        try {
            retrievedUserGroupXML = retrieve(id);
        }
        catch (final Exception e1) {
            EscidocRestSoapTestBase.failException(e1);
        }
        final Document retrievedDeactivatedDocument =
            assertDeactiveUserGroup(retrievedUserGroupXML, createdXml,
                startTimestamp, beforeDeactivationTimestamp, true);
        lastModificationDate =
            getLastModificationDateValue(retrievedDeactivatedDocument);
        taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" />";

        final String beforeActivationTimestamp = getNowAsTimestamp();

        String xmlData = null;
        try {
            xmlData = activate(id, taskParamXML);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNull("Did not expect result data. ", xmlData);

        String retrievedActivatedXml = null;
        try {
            retrievedActivatedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertActiveUserGroup(retrievedActivatedXml, createdXml,
            startTimestamp, beforeActivationTimestamp, true);
        final Document retrievedActivatedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedActivatedXml);
        assertXmlLastModificationDateUpdate("", retrievedDeactivatedDocument,
            retrievedActivatedDocument);
    }

    /**
     * Test declining the activating of an UserGroup with invalid (unknown)
     * parameter id.
     * 
     * @test.name: Non-existent UserGroup
     * @test.id AA_AUG-2
     * @test.input: UserGroup id.
     * @test.inputDescription:
     *              <ul>
     *              <li>not-existing user-group-id</li>
     *              <li>timestamp of the last modification of the UserGroup</li>
     *              </ul>
     * @test.expected: UserGroupNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAAug2() throws Exception {

        final String taskParamXML =
            EscidocRestSoapTestBase.getTemplateAsString(
                TEMPLATE_USER_GROUP_PATH, "escidoc_task_param.xml");
        try {
            activate(UNKNOWN_ID, taskParamXML);
            EscidocRestSoapTestBase
                .failMissingException(UserGroupNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Wrong exception. ",
                UserGroupNotFoundException.class, e);
        }
    }

    /**
     * Test declining the activating of an UserGroup with id of a resource of
     * another resource type.
     * 
     * @test.name: Activate User Group - Wrong Id
     * @test.id AA_AUG-2-2
     * @test.input: UserGroup id.
     * @test.inputDescription:
     *              <ul>
     *              <li>if of existing resource of another resource type</li>
     *              <li>timestamp of the last modification of the UserGroup</li>
     *              </ul>
     * @test.expected: UserGroupNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAAug2_2() throws Exception {

        final String taskParamXML =
            EscidocRestSoapTestBase.getTemplateAsString(
                TEMPLATE_USER_GROUP_PATH, "escidoc_task_param.xml");
        try {
            activate(CONTEXT_ID, taskParamXML);
            EscidocRestSoapTestBase
                .failMissingException(UserGroupNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Wrong exception. ",
                UserGroupNotFoundException.class, e);
        }
    }

    /**
     * Test declining the activating of an UserGroup with missing parameter id.
     * 
     * @test.name: UserID not provided
     * @test.id AA_AUG-3
     * @test.input: UserGroup id.
     * @test.inputDescription:
     *              <ul>
     *              <li>user-group-id is not provided</li>
     *              <li>timestamp of the last modification of the UserGroup</li>
     *              </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAAug3() throws Exception {

        final String taskParamXML =
            EscidocRestSoapTestBase.getTemplateAsString(
                TEMPLATE_USER_GROUP_PATH, "escidoc_task_param.xml");
        try {
            activate(null, taskParamXML);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Wrong exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining the activating of an UserGroup that is already active.
     * 
     * @test.name: UserGroup already active
     * @test.id AA_AUG-4
     * @test.input: UserGroup id.
     * @test.inputDescription:
     *              <ul>
     *              <li>user-group-id, where status is already "active"</li>
     *              <li>timestamp of the last modification of the UserGroup</li>
     *              </ul>
     * @test.expected: AlreadyActiveException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAAug4() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        final String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" />";

        try {
            activate(id, taskParamXML);
            EscidocRestSoapTestBase
                .failMissingException(AlreadyActiveException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                AlreadyActiveException.class, e);
        }
    }

    /**
     * Test declining the activating of an UserGroup with missing task
     * parameters.
     * 
     * @test.name: Missing Task Parameters in Activate
     * @test.id AA_AUG-5
     * @test.input: UserGroup id.
     * @test.inputDescription: No Task Parameters are sent as parameter.
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAAug5() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String id = getObjidValue(createdDocument);

        try {
            deactivate(id, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Wrong exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test successful deactivating an existing, active UserGroup.
     * 
     * @test.name Successful Deactivating a UserGroup.
     * @test.id AA_DUG-1
     * @test.input UserGroup id
     * @test.inputDescription:
     *             <ul>
     *             <li>user-id</li>
     *             <li>timestamp of the last modification of the UserGroup</li>
     *             </ul>
     * @test.expected: No result, no exception, UserGroup has been deactivated.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAADug1() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String createdXml = toString(createdDocument, false);
        final String id = getObjidValue(createdDocument);
        final String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        final String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" />";

        final String beforeDeactivationTimestamp = getNowAsTimestamp();

        String xmlData = null;
        try {
            xmlData = deactivate(id, taskParamXML);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNull("Did not expect result data. ", xmlData);

        String retrievedDeactivatedXml = null;
        try {
            retrievedDeactivatedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertDeactiveUserGroup(retrievedDeactivatedXml, createdXml,
            startTimestamp, beforeDeactivationTimestamp, true);
        final Document retrievedDeactivatedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedDeactivatedXml);
        assertXmlLastModificationDateUpdate("", createdDocument,
            retrievedDeactivatedDocument);

    }

    /**
     * Test declining the deactivating of an UserGroup with invalid (unknown)
     * parameter id.
     * 
     * @test.name: Non-existent user-group-id
     * @test.id AA_DUG-2
     * @test.input: UserGroup id.
     * @test.inputDescription:
     *              <ul>
     *              <li>not-existing user-group-id</li>
     *              <li>timestamp of the last modification of the UserGroup</li>
     *              </ul>
     * @test.expected: UserGroupNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAADug2() throws Exception {

        final String taskParamXML =
            EscidocRestSoapTestBase.getTemplateAsString(
                TEMPLATE_USER_GROUP_PATH, "escidoc_task_param.xml");
        try {
            deactivate(UNKNOWN_ID, taskParamXML);
            EscidocRestSoapTestBase
                .failMissingException(UserGroupNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Wrong exception. ",
                UserGroupNotFoundException.class, e);
        }
    }

    /**
     * Test declining the deactivating of an UserGroup with id od a resource of
     * another resource type.
     * 
     * @test.name: Deacivate User Group - Wrong Id
     * @test.id AA_DUG-2-2
     * @test.input: UserGroup id.
     * @test.inputDescription:
     *              <ul>
     *              <li>id of existing resource of another resource type</li>
     *              <li>timestamp of the last modification of the UserGroup</li>
     *              </ul>
     * @test.expected: UserGroupNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAADug2_2() throws Exception {

        final String taskParamXML =
            EscidocRestSoapTestBase.getTemplateAsString(
                TEMPLATE_USER_GROUP_PATH, "escidoc_task_param.xml");
        try {
            deactivate(CONTEXT_ID, taskParamXML);
            EscidocRestSoapTestBase
                .failMissingException(UserGroupNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Wrong exception. ",
                UserGroupNotFoundException.class, e);
        }
    }

    /**
     * Test declining the deactivating of an UserGroup with missing parameter
     * id.
     * 
     * @test.name: User-group-id not provided
     * @test.id AA_DUG-3
     * @test.input: UserGroup id.
     * @test.inputDescription:
     *              <ul>
     *              <li>user-group-id is not provided</li>
     *              <li>timestamp of the last modification of the UserGroup</li>
     *              </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAADug3() throws Exception {

        final String taskParamXML =
            EscidocRestSoapTestBase.getTemplateAsString(
                TEMPLATE_USER_GROUP_PATH, "escidoc_task_param.xml");
        try {
            deactivate(null, taskParamXML);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Wrong exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining the deactivating of an UserGroup that is already deactive.
     * 
     * @test.name: UserGroupID already deactive
     * @test.id AA_DUG-4
     * @test.input: UserGroup id.
     * @test.inputDescription:
     *              <ul>
     *              <li>user-group-id, where status is already "deactive"</li>
     *              <li>timestamp of the last modification of the UserGroup</li>
     *              </ul>
     * @test.expected: AlreadyDeactiveException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAADug4() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String id = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" />";

        String xmlData = null;
        try {
            deactivate(id, taskParamXML);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }

        try {
            xmlData = retrieve(id);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertNotNull("No result from retrieve", xmlData);
        final Document document = EscidocRestSoapTestBase.getDocument(xmlData);
        assertXmlExists("Last modification date does not exist. ",
            createdDocument, XPATH_USER_GROUP_LAST_MOD_DATE);
        lastModificationDate =
            selectSingleNode(document, XPATH_USER_GROUP_LAST_MOD_DATE)
                .getTextContent();
        taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" />";

        try {
            deactivate(id, taskParamXML);
            EscidocRestSoapTestBase
                .failMissingException(AlreadyDeactiveException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                AlreadyDeactiveException.class, e);
        }
    }

    /**
     * Test declining the deactivating of an UserGroup with missing task
     * parameters.
     * 
     * @test.name: Missing Task Parameters in Deactivate
     * @test.id AA_DUG-5
     * @test.input: UserGroup id.
     * @test.inputDescription: No Task Parameters are sent as parameter.
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAADug5() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String id = getObjidValue(createdDocument);

        try {
            deactivate(id, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Wrong exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test successfully updating an UserGroup.
     * 
     * @test.name Update of UserGroup.
     * @test.id AA_UUG-1
     * @test.input UserGroup XML representation
     * @test.inputDescription: UserGroup XML representation of an existing
     *                         UserGroup for updating an UserGroup is provided.
     * @test.expected: The updated XML representation.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUug1() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final Document toBeUpdatedDocument = createdDocument;
        assertXmlExists("UserGroup name does not exist. ", toBeUpdatedDocument,
            XPATH_USER_GROUP_NAME);
        substitute(toBeUpdatedDocument, XPATH_USER_GROUP_NAME, "New Name");

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeModificationTimestamp = getNowAsTimestamp();

        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertActiveUserGroup(updatedXml, toBeUpdatedXml, startTimestamp,
            beforeModificationTimestamp, true);
    }

    /**
     * Test declining update of UserGroup without providing XML data.
     * 
     * @test.name Update of UserGroup without Data.
     * @test.id AA_UUG-2
     * @test.input UserGroup XML representation
     * @test.inputDescription: No UserGroup XML representation for updating an
     *                         UserGroup is provided.
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUug2() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String id = getObjidValue(createdDocument);

        try {
            update(id, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining update of UserGroup with corrupted XML data.
     * 
     * @test.name Update of UserGroup with corrupted Data.
     * @test.id AA_UUG-3
     * @test.input UserGroup XML representation
     * @test.inputDescription: Corrupted UserGroup XML representation for
     *                         updating an UserGroup is provided.
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUug3() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String id = getObjidValue(createdDocument);

        try {
            update(id, "<Corrupt XML data");
            EscidocRestSoapTestBase
                .failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining update of UserGroup without providing an id.
     * 
     * @test.name Update of UserGroup without Id.
     * @test.id AA_UUG-4
     * @test.input UserGroup XML representation
     * @test.inputDescription: No UserGroup XML representation for updating an
     *                         UserGroup is provided.
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUug4() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");

        try {
            update(null, toString(createdDocument, false));
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining updating of UserGroup in case of an optimistic locking
     * error.
     * 
     * @test.name Optimistic locking error
     * @test.id AA_UUG-5
     * @test.input UserGroup XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-group-id</li>
     *             <li>XML representation of the UserGroup, where the last
     *             modification timestamp does not matchh the current one</li>
     *             </ul>
     * @test.expected: OptimisticLockingException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUug5() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String objid = getObjidValue(createdDocument);
        substitute(createdDocument, XPATH_USER_GROUP_NAME, "New Name");
        update(objid, toString(createdDocument, false));

        try {
            update(objid, toString(createdDocument, false));
            EscidocRestSoapTestBase
                .failMissingException(OptimisticLockingException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                OptimisticLockingException.class, e);
        }
    }

    /**
     * Test declining updating of UserGroup with corrupted XML data.
     * 
     * @test.name Invalid XML representation
     * @test.id AA_UUG-6
     * @test.input UserGroup XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-group-id</li>
     *             <li>invalid XML representation of the UserGroup to be
     *             updated</li>
     *             </ul>
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUug6() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String objid = getObjidValue(createdDocument);

        try {
            update(objid, "<Corrupt XML data");
            EscidocRestSoapTestBase
                .failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining update of UserGroup where a mandatory element is missing
     * in XML data.
     * 
     * @test.name Mandatory parameter is missing
     * @test.id AA_UUG-8
     * @test.input UserGroup XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-group-id</li>
     *             <li>UserGroup XML representation of the UserGroup provided,
     *             where one of the mandatory elements is missing (label)</li>
     *             </ul>
     * @test.expected: XmlSchemaValidationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUug8() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String objid = getObjidValue(createdDocument);
        deleteElement(createdDocument, XPATH_USER_GROUP_LABEL);

        try {
            update(objid, toString(createdDocument, false));
            EscidocRestSoapTestBase
                .failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining update of UserGroup with non-unique label name in XML
     * data.
     * 
     * @test.name Chosen label not unique
     * @test.id AA_UUG-9
     * @test.input UserGroup XML representation
     * @test.inputDescription:
     *             <ul>
     *             <li>existing user-group-id</li>
     *             <li>XML representation of the UserGroup provided, where
     *             label is n o t unique </li>
     *             </ul>
     * @test.expected: UniqueConstraintViolationException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUug9() throws Exception {

        final Document createdDocument1 =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String label1 =
            selectSingleNode(createdDocument1, XPATH_USER_GROUP_LABEL)
                .getTextContent();

        final Document createdDocument2 =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String objid2 = getObjidValue(createdDocument2);
        substitute(createdDocument2, XPATH_USER_GROUP_LABEL, label1);

        try {
            update(objid2, toString(createdDocument2, false));
            EscidocRestSoapTestBase
                .failMissingException(UniqueConstraintViolationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                UniqueConstraintViolationException.class, e);
        }
    }

    /**
     * Test declining updating an UserGroup with unknown id.
     * 
     * @test.name Update UserGroup - Unknown Objid
     * @test.id AA_UUG-12
     * @test.input:
     *          <ul>
     *          <li>id that is unknown to the system</li>
     *          <li>Valid UserGroup XML representation for update</li>
     *          </ul>
     * @test.expected: UserGroupNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUug12() throws Exception {

        final Document toBeUpdatedDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");

        try {
            update(UNKNOWN_ID, toString(toBeUpdatedDocument, false));
            EscidocRestSoapTestBase
                .failMissingException(UserGroupNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                UserGroupNotFoundException.class, e);
        }

    }

    /**
     * Test declining updating an UserGroup with unknown id.
     * 
     * @test.name Update UserGroup - Wrong Id
     * @test.id AA_UUG-12-2
     * @test.input:
     *          <ul>
     *          <li>id of an existing context instead of an user group</li>
     *          <li>Valid UserGroup XML representation for update</li>
     *          </ul>
     * @test.expected: UserGroupNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUug12_2() throws Exception {

        final Document toBeUpdatedDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");

        try {
            update(CONTEXT_ID, toString(toBeUpdatedDocument, false));
            EscidocRestSoapTestBase
                .failMissingException(UserGroupNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                UserGroupNotFoundException.class, e);
        }

    }

    /**
     * Test successfully updating an UserGroup.
     * 
     * @test.name Update of UserGroup.
     * @test.id AA_UUG-13
     * @test.input UserGroup XML representation
     * @test.inputDescription: UserGroup XML representation of an existing
     *                         UserGroup for updating an UserGroup is provided.
     * @test.expected: The updated XML representation.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUug13() throws Exception {

        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final Document toBeUpdatedDocument = createdDocument;
        assertXmlExists("UserGroup name does not exist. ", toBeUpdatedDocument,
            XPATH_USER_GROUP_NAME);
        substitute(toBeUpdatedDocument, XPATH_USER_GROUP_NAME, "New Name");

        final String toBeUpdatedXml =
            modifyNamespacePrefixes(toString(toBeUpdatedDocument, false));
        assertXmlValidUserGroup(toBeUpdatedXml);
        final String beforeModificationTimestamp = getNowAsTimestamp();

        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertActiveUserGroup(updatedXml, toBeUpdatedXml, startTimestamp,
            beforeModificationTimestamp, true);
    }

    /**
     * Test successful retrieving a list of existing UserGroup resources.
     * 
     * @test.name Retrieve UserGroups - Success.
     * @test.id AA_RUGS-1
     * @test.input Valid filter criteria.
     * @test.expected: XML representation of the list of user groups containing
     *                 at least the predefined user-groups.
     * @test.status ToBeImplemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs1CQL() throws Exception {

        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_NAME + "\"=%"});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving of list of user groups failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserGroupsXml);
        // FIXME further assertions needed
    }

    /**
     * Test declining retrieving a list of user groups with providing corrupted
     * filter parameter.
     * 
     * @test.name Retrieve User Groups - Corrupted
     * @test.id AA_RUGS-3
     * @test.input:
     *          <ul>
     *          <li>corrupted filter parameter xml representation is provided</li>
     *          </ul>
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs3() throws Exception {

        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + NAME_CREATED_BY + "\"=\"Some value\""});
        try {
            retrieveUserGroups(filterParams);
            EscidocRestSoapTestBase.failMissingException(
                "Retrieving user groups with providing corrupted filter params"
                    + " not declined. ", InvalidSearchQueryException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Retrieving user groups with providing corrupted filter params"
                    + "not declined, properly. ", InvalidSearchQueryException.class,
                e);
        }
    }

    /**
     * Test declining retrieving a list of user groups with providing invalid
     * filter.
     * 
     * @test.name Retrieve User Groups - Invalid Filter
     * @test.id AA_RUGS-4
     * @test.input:
     *          <ul>
     *          <li>filter parameter is provided containing an invalid filter</li>
     *          </ul>
     * @test.expected: InvalidXmlException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs4CQL() throws Exception {

        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + NAME_CREATED_BY + "\"=\"Some value\""});
        try {
            retrieveUserGroups(filterParams);
            EscidocRestSoapTestBase.failMissingException(
                "Retrieving user groups with providing invalid filter params"
                    + " not declined. ", InvalidSearchQueryException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Retrieving user groups with providing invalid filter params"
                    + "not declined, properly. ",
                    InvalidSearchQueryException.class, e);
        }
    }

    /**
     * Test successful retrieving a list of existing UserGroup resources using
     * filter user-groups.
     * 
     * @test.name Retrieve UserGroups - Filter user-groups.
     * @test.id AA_RUGS-5
     * @test.input Valid filter criteria containing filter
     *             user-groups addressing the Depositor User and the System
     *             Administrator User.
     * @test.expected: XML representation of the list of user group only
     *                 containing the two addresses user groups.
     * @test.status ToBeImplemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs5CQL() throws Exception {
        final Document createdDocument1 =
            createSuccessfully("escidoc_usergroup_for_create.xml");

        final String id1 = getObjidValue(createdDocument1);

        final Document createdDocument2 =
            createSuccessfully("escidoc_usergroup_for_create.xml");

        final String id2 = getObjidValue(createdDocument2);
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_IDENTIFIER + "\"=" + id1 + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + id2});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving of list of user groups failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserGroupsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
        final NodeList userGroupNodes = selectNodeList(retrievedDocument,
                XPATH_SRW_USER_GROUP_LIST_USER_GROUP);
        assertEquals("Unexpected number of user groups.", 2, userGroupNodes
            .getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);
        // FIXME further assertions needed
    }

    /**
     * Test successful retrieving a list of existing UserGroup resources using
     * filter active.
     * 
     * @test.name Retrieve UserGroups - Filter active.
     * @test.id AA_RUGS-6
     * @test.input Valid filter criteria containing filter
     *             active that is set to true.
     * @test.expected: XML representation of the list of user groups at least
     *                 containing the predefined, activated user groups, but not
     *                 containing a deactivated user group.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs6CQL() throws Exception {

        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_ACTIVE + "\"=true"});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving of list of user groups failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserGroupsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);
        // FIXME further assertions needed
    }

    /**
     * Test successful retrieving a list of existing UserGroup resources using
     * filter deactive.
     * 
     * @test.name Retrieve UserGroups - Filter deactive.
     * @test.id AA_RUGS-7
     * @test.input Valid filter criteria containing filter deactive.
     * @test.expected: XML representation of the list of user groups at least
     *                 containing a deactivated user group but not containing an
     *                 active user group.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs7CQL() throws Exception {

        // prepare test by creating a user group and deactivate it
        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        final String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" />";

        try {
            deactivate(id, taskParamXML);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }

        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_ACTIVE + "\"=false"});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving of list of user groups failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserGroupsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);
        // FIXME further assertions needed
    }

    /**
     * Test successful retrieving a list of existing UserGroup resources using
     * filter label.
     * 
     * @test.name Retrieve UserGroups - Filter label.
     * @test.id AA_RUGS-9
     * @test.input Valid filter criteria containing filter
     *             label addressing the System Administrator Group.
     * @test.expected: XML representation of the list of user groups only
     *                 containing the system administrator user group.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs9CQL() throws Exception {
        final Document toBeCreatedDocument =
            getTemplateAsFixedUserGroupDocument(TEMPLATE_USER_GROUP_PATH,
                "escidoc_usergroup_for_create.xml");
        final Node labelNode =
            selectSingleNode(toBeCreatedDocument,
                "/user-group/properties/label");
        String label = "TestNewNewLabel" + System.currentTimeMillis();

        labelNode.setTextContent(label);
        final String toBeCreatedXml = toString(toBeCreatedDocument, false);
        String createdUserGroupXml = create(toBeCreatedXml);

        final String id = getObjidValue(createdUserGroupXml);
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_LABEL + "\"=%TestNewNewLabel%"});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving of list of user groups failed. ", e);
        }

        try {
            assertXmlValidSrwResponse(retrievedUserGroupsXml);
            final Document retrievedDocument =
                EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
            final NodeList userGroupNodes = selectNodeList(retrievedDocument,
                XPATH_SRW_USER_GROUP_LIST_USER_GROUP);
            assertEquals("Unexpected number of user groups.", 1, userGroupNodes
                .getLength());
            assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);

            // FIXME further assertions needed
        } finally {
            delete(id);
        }
    }

    /**
     * Test successful retrieving a list of existing UserGroup resources using
     * filter name.
     * 
     * @test.name Retrieve UserGroups - Filter name.
     * @test.id AA_RUGS-10
     * @test.input Valid filter criteria containing filter
     *             name addressing the System Administrator User.
     * @test.expected: XML representation of the list of user groups only
     *                 containing the system administrator user group.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs10CQL() throws Exception {
        final Document toBeCreatedDocument =
            getTemplateAsFixedUserGroupDocument(TEMPLATE_USER_GROUP_PATH,
                "escidoc_usergroup_for_create.xml");
        insertUniqueLabel(toBeCreatedDocument);
        final Node nameNode =
            selectSingleNode(toBeCreatedDocument, "/user-group/properties/name");
        String name = "TestNewName" + System.currentTimeMillis();

        nameNode.setTextContent(name);
        final String toBeCreatedXml = toString(toBeCreatedDocument, false);
        String createdUserGroupXml = create(toBeCreatedXml);

        final String id = getObjidValue(createdUserGroupXml);
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_NAME + "\"=%TestNewName%"});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving of list of user group failed. ", e);
        }

        try {
            assertXmlValidSrwResponse(retrievedUserGroupsXml);
            final Document retrievedDocument =
                EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
            final NodeList userGroupNodes = selectNodeList(retrievedDocument,
                XPATH_SRW_USER_GROUP_LIST_USER_GROUP);
            assertEquals("Unexpected number of user groups.", 1, userGroupNodes
                .getLength());
            assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);
            // FIXME further assertions needed
        } finally {
            delete(id);
        }
    }

    /**
     * Test successful retrieving a list of existing UserGroup resources using
     * filter user.
     * 
     * @test.name Retrieve UserGroups - Filter user.
     * @test.id AA_RUGS-101-CQL
     * @test.input Valid xml representation of filter criteria containing filter
     *             user.
     * @test.expected: XML representation of the list of user groups only
     *                 containing the users groups.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs101CQL() throws Exception {

        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_USER_GROUP_USER + "\"=" + userAccountAttributeUser});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving of list of user group failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserGroupsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
        final NodeList userGroupNodes = selectNodeList(retrievedDocument,
            XPATH_SRW_USER_GROUP_LIST_USER_GROUP);
        assertEquals("Unexpected number of user groups.", 2, userGroupNodes
            .getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);

        String href = "";
        String attributeName = "objid";
        if (getTransport() == Constants.TRANSPORT_REST) {
            href = "/aa/user-group/";
            attributeName = "href";
        }
        assertXmlExists("Missing group " + childGroup,
                retrievedDocument, XPATH_SRW_USER_GROUP_LIST_USER_GROUP
                + "[@" + attributeName + "='"
                + href + childGroup + "']");
        assertXmlExists("Missing group " + parentGroup,
                retrievedDocument, XPATH_SRW_USER_GROUP_LIST_USER_GROUP
                + "[@" + attributeName + "='"
                + href + parentGroup + "']");
    }

    /**
     * Test successful retrieving a list of existing UserGroup resources using
     * filter user.
     * 
     * @test.name Retrieve UserGroups - Filter user.
     * @test.id AA_RUGS-102-CQL
     * @test.input Valid xml representation of filter criteria containing filter
     *             user.
     * @test.expected: XML representation of the list of user groups only
     *                 containing the users groups.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs102CQL() throws Exception {

        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_USER_GROUP_USER + "\"=" + userAccountOuUser});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving of list of user group failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserGroupsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
        final NodeList userGroupNodes = selectNodeList(retrievedDocument,
            XPATH_SRW_USER_GROUP_LIST_USER_GROUP);
        assertEquals("Unexpected number of user groups.", 1, userGroupNodes
            .getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);

        String href = "";
        String attributeName = "objid";
        if (getTransport() == Constants.TRANSPORT_REST) {
            href = "/aa/user-group/";
            attributeName = "href";
        }
        assertXmlExists("Missing group " + parentGroup,
                retrievedDocument, XPATH_SRW_USER_GROUP_LIST_USER_GROUP
                + "[@" + attributeName + "='"
                + href + parentGroup + "']");
    }

    /**
     * Test successfully retrieving a list of user-groups using multiple
     * filters including user-filter.
     * 
     * @test.name Retrieve User Groups - Multiple filter including user-filter
     * @test.id AA_RUGS-103-CQL
     * @test.input:
     *          <ul>
     *          <li>valid task parameter containing filters user-groups,
     *          email, login-name, name, user and active.</li>
     *          </ul>
     * @test.expected: Valid XML representation of the list of user groups
     *                 only containing the expected groups.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs103CQL() throws Exception {

        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "(\"" + FILTER_IDENTIFIER + "\"=" 
            + additonalUserFilterSearchGroups[0] + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" 
            + additonalUserFilterSearchGroups[1] + " or "
            + "\"" + FILTER_USER_GROUP_USER + "\"=\"" 
            + userAccountAttributeUser + "\") and "
            + "\"" + FILTER_EMAIL + "\"=\"%@%\" and "
            + "\"" + FILTER_NAME + "\"=%test% and "
            + "\"" + FILTER_LABEL + "\"=%test% and "
            + "\"" + FILTER_ACTIVE + "\"=true"});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving list of user groups failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserGroupsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);

        final NodeList userGroupNodes =
            selectNodeList(retrievedDocument,
                XPATH_SRW_USER_GROUP_LIST_USER_GROUP);
        assertEquals("Unexpected number of user groups.", 3, userGroupNodes
                .getLength());

        String href = "";
        String attributeName = "objid";
        if (getTransport() == Constants.TRANSPORT_REST) {
            href = "/aa/user-group/";
            attributeName = "href";
        }
        assertXmlExists("Missing group " + additonalUserFilterSearchGroups[0],
                retrievedDocument, XPATH_SRW_USER_GROUP_LIST_USER_GROUP
                + "[@" + attributeName + "='"
                + href + additonalUserFilterSearchGroups[0] + "']");
        assertXmlExists("Missing group " + additonalUserFilterSearchGroups[1],
                retrievedDocument, XPATH_SRW_USER_GROUP_LIST_USER_GROUP
                + "[@" + attributeName + "='"
                + href + additonalUserFilterSearchGroups[1] + "']");
        assertXmlExists("Missing group " + childGroup,
                retrievedDocument, XPATH_SRW_USER_GROUP_LIST_USER_GROUP
                + "[@" + attributeName + "='"
                + href + childGroup + "']");
    }

    /**
     * Test successfully retrieving a list of user-groups using multiple
     * filters including user-filter with nonexisting user.
     * 
     * @test.name Retrieve User Groups - 
     * Multiple filter including user-filter with nonexisting user
     * @test.id AA_RUGS-104-CQL
     * @test.input:
     *          <ul>
     *          <li>valid task parameter containing filters user-groups,
     *          email, login-name, name, user and active.</li>
     *          </ul>
     * @test.expected: Valid XML representation of the list of user groups
     *                 only containing the expected groups.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs104CQL() throws Exception {
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();
            filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
                    "(\"" + FILTER_IDENTIFIER + "\"=" 
                    + additonalUserFilterSearchGroups[0] + " or "
                    + "\"" + FILTER_IDENTIFIER + "\"=" 
                    + additonalUserFilterSearchGroups[1] + " or "
                    + "\"" + FILTER_USER_GROUP_USER + "\"=\"neueruser\") and " 
                    + "\"" + FILTER_EMAIL + "\"=\"%@%\" and "
                    + "\"" + FILTER_NAME + "\"=%test% and "
                    + "\"" + FILTER_LABEL + "\"=%test% and "
                    + "\"" + FILTER_ACTIVE + "\"=true"});

            String retrievedUserGroupsXml = null;

            try {
                retrievedUserGroupsXml = retrieveUserGroups(filterParams);
            }
            catch (final Exception e) {
                EscidocRestSoapTestBase.failException(
                    "Retrieving list of user groups failed. ", e);
            }

            assertXmlValidSrwResponse(retrievedUserGroupsXml);
            final Document retrievedDocument =
                EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
            assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);

            final NodeList userGroupNodes =
                selectNodeList(retrievedDocument,
                    XPATH_SRW_USER_GROUP_LIST_USER_GROUP);
            assertEquals("Unexpected number of user groups.", 2, userGroupNodes
                    .getLength());

            String href = "";
            String attributeName = "objid";
            if (getTransport() == Constants.TRANSPORT_REST) {
                href = "/aa/user-group/";
                attributeName = "href";
            }
            assertXmlExists("Missing group " + additonalUserFilterSearchGroups[0],
                    retrievedDocument, XPATH_SRW_USER_GROUP_LIST_USER_GROUP
                    + "[@" + attributeName + "='"
                    + href + additonalUserFilterSearchGroups[0] + "']");
            assertXmlExists("Missing group " + additonalUserFilterSearchGroups[1],
                    retrievedDocument, XPATH_SRW_USER_GROUP_LIST_USER_GROUP
                    + "[@" + attributeName + "='"
                    + href + additonalUserFilterSearchGroups[1] + "']");
    }

    /**
     * Test invalid filters including user-filter.
     * 
     * @test.name Retrieve User Groups - Invalid filter including user-filter
     * @test.id AA_RUGS-106-CQL
     * @test.input:
     *          <ul>
     *          <li>user-filter containing wildcard.</li>
     *          </ul>
     * @test.expected: Exception.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs106CQL() throws Exception {
        final Class<InvalidSearchQueryException> ec = 
                        InvalidSearchQueryException.class;

        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();
            filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
                    "(\"" + FILTER_IDENTIFIER + "\"=" 
                    + additonalUserFilterSearchGroups[0] + " or "
                    + "\"" + FILTER_IDENTIFIER + "\"=" 
                    + additonalUserFilterSearchGroups[1] + " or "
                    + "\"" + FILTER_USER_GROUP_USER + "\"=\"%neueruser\") and " 
                    + "\"" + FILTER_EMAIL + "\"=\"%@%\" and "
                    + "\"" + FILTER_NAME + "\"=%test% and "
                    + "\"" + FILTER_LABEL + "\"=%test% and "
                    + "\"" + FILTER_ACTIVE + "\"=true"});

        try {
            retrieveUserGroups(filterParams);
            failMissingException(
                    "Retrieving with wildcard user filter "
                    + "criteria not declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(
               "Retrieving with wildcard user filter "
                    + "criteria not declined properly.",
                    ec, e);
        }

    }

    /**
     * Test invalid filters including user-filter.
     * 
     * @test.name Retrieve User Groups - Invalid filter including user-filter
     * @test.id AA_RUGS-107-CQL
     * @test.input:
     *          <ul>
     *          <li>user-filter containing non-allowed relation.</li>
     *          </ul>
     * @test.expected: Exception.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs107CQL() throws Exception {
        final Class<InvalidSearchQueryException> ec = 
                        InvalidSearchQueryException.class;

        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();
            filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
                    "(\"" + FILTER_IDENTIFIER + "\"=" 
                    + additonalUserFilterSearchGroups[0] + " or "
                    + "\"" + FILTER_IDENTIFIER + "\"=" 
                    + additonalUserFilterSearchGroups[1] + " or "
                    + "\"" + FILTER_USER_GROUP_USER + "\" any \"neueruser\") and " 
                    + "\"" + FILTER_EMAIL + "\"=\"%@%\" and "
                    + "\"" + FILTER_NAME + "\"=%test% and "
                    + "\"" + FILTER_LABEL + "\"=%test% and "
                    + "\"" + FILTER_ACTIVE + "\"=true"});

        try {
            retrieveUserGroups(filterParams);
            failMissingException(
                    "Retrieving with non-allowed relation user filter "
                    + "criteria not declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(
               "Retrieving with non-allowed relation user filter "
                    + "criteria not declined properly.",
                    ec, e);
        }

    }

    /**
     * Test invalid filters including user-filter.
     * 
     * @test.name Retrieve User Groups - Invalid filter including user-filter
     * @test.id AA_RUGS-108-CQL
     * @test.input:
     *          <ul>
     *          <li>user-filter containing non-allowed relation.</li>
     *          </ul>
     * @test.expected: Exception.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs108CQL() throws Exception {
        final Class<InvalidSearchQueryException> ec = 
                        InvalidSearchQueryException.class;

        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();
            filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
                    "(\"" + FILTER_IDENTIFIER + "\"=" 
                    + additonalUserFilterSearchGroups[0] + " or "
                    + "\"" + FILTER_IDENTIFIER + "\"=" 
                    + additonalUserFilterSearchGroups[1] + " or "
                    + "\"" + FILTER_USER_GROUP_USER + "\">\"neueruser\") and " 
                    + "\"" + FILTER_EMAIL + "\"=\"%@%\" and "
                    + "\"" + FILTER_NAME + "\"=%test% and "
                    + "\"" + FILTER_LABEL + "\"=%test% and "
                    + "\"" + FILTER_ACTIVE + "\"=true"});

        try {
            retrieveUserGroups(filterParams);
            failMissingException(
                    "Retrieving with non-allowed relation user filter "
                    + "criteria not declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(
               "Retrieving with non-allowed relation user filter "
                    + "criteria not declined properly.",
                    ec, e);
        }

    }

    /**
     * Test invalid filters including user-filter.
     * 
     * @test.name Retrieve User Groups - Invalid filter including user-filter
     * @test.id AA_RUGS-109-CQL
     * @test.input:
     *          <ul>
     *          <li>user-filter containing non-allowed relation.</li>
     *          </ul>
     * @test.expected: Exception.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs109CQL() throws Exception {
        final Class<InvalidSearchQueryException> ec = 
                        InvalidSearchQueryException.class;

        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();
            filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
                    "(\"" + FILTER_IDENTIFIER + "\"=" 
                    + additonalUserFilterSearchGroups[0] + " or "
                    + "\"" + FILTER_IDENTIFIER + "\"=" 
                    + additonalUserFilterSearchGroups[1] + " or "
                    + "\"" + FILTER_USER_GROUP_USER + "\"<=\"neueruser\") and " 
                    + "\"" + FILTER_EMAIL + "\"=\"%@%\" and "
                    + "\"" + FILTER_NAME + "\"=%test% and "
                    + "\"" + FILTER_LABEL + "\"=%test% and "
                    + "\"" + FILTER_ACTIVE + "\"=true"});

        try {
            retrieveUserGroups(filterParams);
            failMissingException(
                    "Retrieving with non-allowed relation group filter "
                    + "criteria not declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType(
               "Retrieving with non-allowed relation group filter "
                    + "criteria not declined properly.",
                    ec, e);
        }

    }

    /**
     * Test successfully retrieving an empty list of user-groups.
     * 
     * @test.name Retrieve User Groups - Empty list
     * @test.id AA_RUGS-13
     * @test.input:
     *          <ul>
     *          <li>valid task parameter containing filters active and
     *          identifier . The latter points to a resource that is not a
     *          user-group </li>
     *          </ul>
     * @test.expected: Valid XML representation of the list of user groups that
     *                 is empty.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs13CQL() throws Exception {

        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_ACTIVE + "\"=true and "
            + "\"" + FILTER_IDENTIFIER + "\"=escidoc:persistent3"});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving list of user groups failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserGroupsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
        assertXmlNotExists("List is not empty but should be.",
            retrievedDocument, XPATH_SRW_USER_GROUP_LIST_USER_GROUP);
    }

    /**
     * Test successfully retrieving a list of user-groups using multiple
     * filters.
     * 
     * @test.name Retrieve User Groups - Multiple filter
     * @test.id AA_RUGS-14
     * @test.input:
     *          <ul>
     *          <li>valid task parameter containing filters user-groups and
     *          active.</li>
     *          </ul>
     * @test.expected: Valid XML representation of the list of user groups only
     *                 containing the System Administrator user group and the
     *                 System Inspector user group.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs14CQL() throws Exception {
        final Document createdDocument1 =
            createSuccessfully("escidoc_usergroup_for_create.xml");

        final String id1 = getObjidValue(createdDocument1);

        final Document createdDocument2 =
            createSuccessfully("escidoc_usergroup_for_create.xml");

        final String id2 = getObjidValue(createdDocument2);
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "(\"" + FILTER_IDENTIFIER + "\"=" + id1 + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + id2 + ") and "
            + "\"" + FILTER_ACTIVE + "\"=true"});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving list of user groups failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserGroupsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
        final NodeList userGroupNodes = selectNodeList(retrievedDocument,
            XPATH_SRW_USER_GROUP_LIST_USER_GROUP);
        assertEquals("Unexpected number of user groups.", 2, userGroupNodes
            .getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);
    }

    /**
     * Test successfully retrieving an empty list of user-groups using
     * unsupported filter.
     * 
     * @test.name Retrieve User Groups - Unsupported filter
     * @test.id AA_RUGS-16
     * @test.input:
     *          <ul>
     *          <li>valid task parameter containing filters user-groups and
     *          active. Additionally, a filter criteria that is unsupported by
     *          retrieve user groups is used (context).</li>
     *          </ul>
     * @test.expected: Valid XML representation of an empty list of user groups.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs16CQL() throws Exception {

        final Class<InvalidSearchQueryException> ec =
            InvalidSearchQueryException.class;
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "(\"" + FILTER_IDENTIFIER + "\"=escidoc:group1 or "
            + "\"" + FILTER_IDENTIFIER + "\"=escidoc:group4) and "
            + "\"" + FILTER_ACTIVE + "\"=true and "
            + "\"" + FILTER_CONTEXT + "\"=escidoc:persistent3"});
        try {
            retrieveUserGroups(filterParams);
            failMissingException(
                "Retrieving with unknown filter criteria not declined.", ec);
        }
        catch (Exception e) {
            assertExceptionType(
                "Retrieving with unknown filter criteria not declined,"
                    + " properly.", ec, e);
        }
    }

    /**
     * Test successfully retrieving a list of user-groups using id filter and
     * ascending ordering by name.
     * 
     * @test.name Retrieve User Groups - Order-By Name Ascending
     * @test.id AA_RUGS-17
     * @test.input:
     *          <ul>
     *          <li>valid task parameter containing
     *          <ul>
     *          <li>filter ids addressing user-groups
     *          <ul>
     *          <li>System Administrator User</li>
     *          <li>System Inspector User (Read Only Super User)</li>
     *          <li>Depositor User</li>
     *          </ul>
     *          </li>
     *          <li>order-by id ascending definition</li>
     *          </ul>
     *          </ul>
     * @test.expected: Valid XML representation of the list of user groups
     *                 containing user groups
     *                 <ul>
     *                 <li>Depositor User</li>
     *                 <li>System Administrator User</li>
     *                 <li>System Inspector User (Read Only Super User)</li>
     *                 </ul>
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs17CQL() throws Exception {
        final Document createdDocument1 =
            createSuccessfully("escidoc_usergroup_for_create.xml", "1");

        final String id1 = getObjidValue(createdDocument1);

        final Document createdDocument2 =
            createSuccessfully("escidoc_usergroup_for_create.xml", "2");

        final String id2 = getObjidValue(createdDocument2);

        final Document createdDocument3 =
            createSuccessfully("escidoc_usergroup_for_create.xml", "3");

        final String id3 = getObjidValue(createdDocument3);
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_IDENTIFIER + "\"=" + id1 + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + id2 + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + id3 + " sortby "
            + "\"" + FILTER_NAME + "\"/sort.ascending"});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving list of user groups failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserGroupsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
        final NodeList userGroupNodes = selectNodeList(retrievedDocument,
            XPATH_SRW_USER_GROUP_LIST_USER_GROUP);
        assertEquals("Unexpected number of user groups.", 3, userGroupNodes
            .getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);

        assertXmlExists("Missing user group 'Newname3'", retrievedDocument,
            XPATH_SRW_RESPONSE_RECORD + "[3]" 
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_GROUP
            + "/properties[name='Newname3']");
        assertXmlExists("Missing user group 'Newname2'.", retrievedDocument,
            XPATH_SRW_RESPONSE_RECORD + "[2]" 
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_GROUP
            + "/properties[name='Newname2']");
        assertXmlExists("Missing user group 'Newname1'.", retrievedDocument,
            XPATH_SRW_RESPONSE_RECORD + "[1]" 
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_GROUP
            + "/properties[name='Newname1']");
    }

    /**
     * Test successfully retrieving a list of user-groups using id filter and
     * descending ordering by name.
     * 
     * @test.name Retrieve User Groups - Order-By Name Descending
     * @test.id AA_RUGS-18
     * @test.input:
     *          <ul>
     *          <li>valid task parameter containing
     *          <ul>
     *          <li>filter ids addressing user-groups
     *          <ul>
     *          <li>System Administrator User</li>
     *          <li>System Inspector User (Read Only Super User)</li>
     *          <li>Depositor User</li>
     *          </ul>
     *          </li>
     *          <li>order-by id descending definition</li>
     *          </ul>
     *          </ul>
     * @test.expected: Valid XML representation of the list of user groups
     *                 containing user groups
     *                 <ul>
     *                 <li>System Inspector User (Read Only Super User)</li>
     *                 <li>System Administrator User</li>
     *                 <li>Depositor User</li>
     *                 </ul>
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs18CQL() throws Exception {

        final Document createdDocument1 =
            createSuccessfully("escidoc_usergroup_for_create.xml", "1");

        final String id1 = getObjidValue(createdDocument1);

        final Document createdDocument2 =
            createSuccessfully("escidoc_usergroup_for_create.xml", "2");

        final String id2 = getObjidValue(createdDocument2);

        final Document createdDocument3 =
            createSuccessfully("escidoc_usergroup_for_create.xml", "3");

        final String id3 = getObjidValue(createdDocument3);
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_IDENTIFIER + "\"=" + id1 + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + id2 + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + id3 + " sortby "
            + "\"" + FILTER_NAME + "\"/sort.descending"});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving list of user groups failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserGroupsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
        final NodeList userGroupNodes = selectNodeList(retrievedDocument,
            XPATH_SRW_USER_GROUP_LIST_USER_GROUP);
        assertEquals("Unexpected number of user groups.", 3, userGroupNodes
            .getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);

        assertXmlExists("Missing user group 'Newname1'.", retrievedDocument,
            XPATH_SRW_RESPONSE_RECORD + "[3]" 
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_GROUP
            + "/properties[name='Newname1']");
        assertXmlExists("Missing user group 'Newname2'.", retrievedDocument,
            XPATH_SRW_RESPONSE_RECORD + "[2]" 
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_GROUP
            + "/properties[name='Newname2']");
        assertXmlExists("Missing user group 'Newname3'.", retrievedDocument,
            XPATH_SRW_RESPONSE_RECORD + "[1]" 
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_GROUP
            + "/properties[name='Newname3']");
    }

    /**
     * Test successfully retrieving a list of user-groups using id filter,
     * descending ordering by name and offset.
     * 
     * @test.name Retrieve User Groups - Offset
     * @test.id AA_RUGS-19
     * @test.input:
     *          <ul>
     *          <li>valid task parameter containing
     *          <ul>
     *          <li>filter ids addressing user-groups
     *          <ul>
     *          <li>System Administrator User</li>
     *          <li>System Inspector User (Read Only Super User)</li>
     *          <li>Depositor User</li>
     *          </ul>
     *          </li>
     *          <li>order-by id descending definition</li>
     *          <li>offset = 1</li>
     *          </ul>
     *          </ul>
     * @test.expected: Valid XML representation of the list of user groups
     *                 containing user groups
     *                 <ul>
     *                 <li>System Administrator User</li>
     *                 <li>Depositor User</li>
     *                 </ul>
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs19CQL() throws Exception {

        final Document createdDocument1 =
            createSuccessfully("escidoc_usergroup_for_create.xml", "1");

        final String id1 = getObjidValue(createdDocument1);

        final Document createdDocument2 =
            createSuccessfully("escidoc_usergroup_for_create.xml", "2");

        final String id2 = getObjidValue(createdDocument2);

        final Document createdDocument3 =
            createSuccessfully("escidoc_usergroup_for_create.xml", "3");

        final String id3 = getObjidValue(createdDocument3);
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_IDENTIFIER + "\"=" + id1 + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + id2 + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + id3 + " sortby "
            + "\"" + FILTER_NAME + "\"/sort.descending"});
        filterParams.put(FILTER_PARAMETER_STARTRECORD, new String[] {"2"});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving list of user groups failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserGroupsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
        final NodeList userGroupNodes = selectNodeList(retrievedDocument,
            XPATH_SRW_USER_GROUP_LIST_USER_GROUP);
        assertEquals("Unexpected number of user groups.", 2, userGroupNodes
            .getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);

        assertXmlExists("Missing user group 'Newname1'.", retrievedDocument,
            XPATH_SRW_RESPONSE_RECORD + "[2]" 
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_GROUP
            + "/properties[name='Newname1']");
        assertXmlExists("Missing user group 'Newname2'.", retrievedDocument,
            XPATH_SRW_RESPONSE_RECORD + "[1]" 
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_GROUP
            + "/properties[name='Newname2']");
    }

    /**
     * Test successfully retrieving a list of user-groups using id filter,
     * descending ordering by name, offset, and limit.
     * 
     * @test.name Retrieve User Groups - Limit
     * @test.id AA_RUGS-20
     * @test.input:
     *          <ul>
     *          <li>valid task parameter containing
     *          <ul>
     *          <li>filter ids addressing user-groups
     *          <ul>
     *          <li>System Administrator User</li>
     *          <li>System Inspector User (Read Only Super User)</li>
     *          <li>Depositor User</li>
     *          </ul>
     *          </li>
     *          <li>order-by id descending definition</li>
     *          <li>offset = 1</li>
     *          <li>limit = 1</li>
     *          </ul>
     *          </ul>
     * @test.expected: Valid XML representation of the list of user groups
     *                 containing user groups
     *                 <ul>
     *                 <li>System Administrator User</li>
     *                 </ul>
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARugs20CQL() throws Exception {
        final Document createdDocument1 =
            createSuccessfully("escidoc_usergroup_for_create.xml", "1");

        final String id1 = getObjidValue(createdDocument1);

        final Document createdDocument2 =
            createSuccessfully("escidoc_usergroup_for_create.xml", "2");

        final String id2 = getObjidValue(createdDocument2);

        final Document createdDocument3 =
            createSuccessfully("escidoc_usergroup_for_create.xml", "3");

        final String id3 = getObjidValue(createdDocument3);
        final Map <String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] {
            "\"" + FILTER_IDENTIFIER + "\"=" + id1 + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + id2 + " or "
            + "\"" + FILTER_IDENTIFIER + "\"=" + id3 + " sortby "
            + "\"" + FILTER_NAME + "\"/sort.descending"});
        filterParams.put(FILTER_PARAMETER_STARTRECORD, new String[] {"2"});
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] {"1"});

        String retrievedUserGroupsXml = null;

        try {
            retrievedUserGroupsXml = retrieveUserGroups(filterParams);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.failException(
                "Retrieving list of user groups failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserGroupsXml);
        final Document retrievedDocument =
            EscidocRestSoapTestBase.getDocument(retrievedUserGroupsXml);
        final NodeList userGroupNodes = selectNodeList(retrievedDocument,
            XPATH_SRW_USER_GROUP_LIST_USER_GROUP);
        assertEquals("Unexpected number of user groups.", 1, userGroupNodes
            .getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_GROUP);

        assertXmlExists("Missing user group 'Newname2'.", retrievedDocument,
            XPATH_SRW_RESPONSE_RECORD + "[1]" 
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_GROUP
            + "/properties[name='Newname2']");
    }

    @Test
    public void testDeleteUserGroupWithoutMembers() throws Exception {
        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");

        final String id = getObjidValue(createdDocument);
        delete(id);
        try {

            retrieve(id);
            fail("No exception on retrieve user group after delete.");
        }
        catch (Exception e) {
            Class< ? > ec = UserGroupNotFoundException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }

    }

    @Test
    public void testDeleteUserGroupWithMembers() throws Exception {
        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final String id = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        String[] selector1 = new String[3];
        selector1[0] = "o";
        selector1[1] = "user-attribute";
        selector1[2] = "escidoc:persistent1";
        String[] selector2 = new String[3];
        selector2[0] = "user-account";
        selector2[1] = "internal";
        selector2[2] = "escidoc:user42";
        ArrayList<String[]> selectors = new ArrayList<String[]>();
        selectors.add(selector1);
        selectors.add(selector2);
        String taskParam =
            getAddSelectorsTaskParam(selectors, lastModificationDate);
        addSelectors(id, taskParam);
        // System.out.println("group with selectors " + groupXml);
        delete(id);
        try {

            retrieve(id);
            fail("No exception on retrieve user group after delete.");
        }
        catch (Exception e) {
            Class< ? > ec = UserGroupNotFoundException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    @Test
    public void testAddSelectors() throws Exception {
        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        NodeList selectorNodes = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            selectorNodes = selectNodeList(createdDocument,
                "/user-group/selectors/selector/@href");
        }
        else {
            selectorNodes = selectNodeList(createdDocument,
                "/user-group/selectors/selector/@objid");
        }
        assertEquals("User-group members can not exist on user-group create",
            0, selectorNodes.getLength());
        final String id = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        String[] selector1 = new String[3];
        selector1[0] = "o";
        selector1[1] = "user-attribute";
        selector1[2] = "escidoc:persistent1";
        String[] selector2 = new String[3];
        selector2[0] = "user-account";
        selector2[1] = "internal";
        selector2[2] = "escidoc:user42";
        ArrayList<String[]> selectors = new ArrayList<String[]>();
        selectors.add(selector1);
        selectors.add(selector2);
        String taskParam =
            getAddSelectorsTaskParam(selectors, lastModificationDate);
        String groupXml = addSelectors(id, taskParam);
        Document groupAfterAddMembers = getDocument(groupXml);
        NodeList selectorNodesAfterAdd = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            selectorNodesAfterAdd = selectNodeList(groupAfterAddMembers,
                "/user-group/selectors/selector/@href");
        }
        else {
            selectorNodesAfterAdd = selectNodeList(groupAfterAddMembers,
                "/user-group/selectors/selector/@objid");
        }
        assertEquals(
            "User-group members can not be 0 after calling of addSelectors", 2,
            selectorNodesAfterAdd.getLength());

    }

    @Test
    public void testRemoveSelectors() throws Exception {
        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");

        final String id = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        String[] selector1 = new String[3];
        selector1[0] = "o";
        selector1[1] = "user-attribute";
        selector1[2] = "escidoc:persistent1";
        String[] selector2 = new String[3];
        selector2[0] = "user-account";
        selector2[1] = "internal";
        selector2[2] = "escidoc:user42";
        ArrayList<String[]> selectors = new ArrayList<String[]>();
        selectors.add(selector1);
        selectors.add(selector2);
        String taskParam =
            getAddSelectorsTaskParam(selectors, lastModificationDate);
        String groupXml = addSelectors(id, taskParam);
        Document userGroupDoc = getDocument(groupXml);
        String objid = getObjidValue(userGroupDoc);
        // NodeList selectorNodes = selectNodeList(userGroupDoc,
        // /user-group/selector);
        NodeList selectorNodes = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            selectorNodes = selectNodeList(userGroupDoc,
                "/user-group/selectors/selector/@href");
        }
        else {
            selectorNodes = selectNodeList(userGroupDoc,
                "/user-group/selectors/selector/@objid");
        }

        Vector<String> selectorsToRemove = new Vector<String>();
        for (int i = 0; i < selectorNodes.getLength(); i++) {
            String selectorId = selectorNodes.item(i).getNodeValue();
            if (Constants.TRANSPORT_REST == getTransport()) {
            selectorId = getIdFromHrefValue(selectorId);
            }
            selectorsToRemove.add(selectorId);
        }
        String lastModDate = getLastModificationDateValue(userGroupDoc);

        taskParam = getRemoveSelectorsTaskParam(selectorsToRemove, lastModDate);
        removeSelectors(objid, taskParam);
        String groupXmlWithoutMembers = retrieve(objid);
        Document groupWithoutMembers = getDocument(groupXmlWithoutMembers);
        NodeList selectorNodesAfterRemove = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            selectorNodesAfterRemove = selectNodeList(groupWithoutMembers,
                "/user-group/selectors/selector/@href");
        }
        else {
            selectorNodesAfterRemove = selectNodeList(groupWithoutMembers,
                "/user-group/selectors/selector/@objid");
        }
        assertEquals("User-group members should be removed ", 0,
            selectorNodesAfterRemove.getLength());

    }

    @Test
    public void testCyclenDetection() throws Exception {
        final Document createdSuperGroupDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final Document createdMemberLevel1Document =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        final Document createdMemberLevel2Document =
            createSuccessfully("escidoc_usergroup_for_create.xml");

        final String superGroupId = getObjidValue(createdSuperGroupDocument);
        final String memberLevel1Id =
            getObjidValue(createdMemberLevel1Document);
        final String memberLevel2Id =
            getObjidValue(createdMemberLevel2Document);

        String lastModificationDateSuperGroup =
            getLastModificationDateValue(createdSuperGroupDocument);
        String lastModificationDateMemberLevel1 =
            getLastModificationDateValue(createdMemberLevel1Document);
        String lastModificationDateMemberLevel2 =
            getLastModificationDateValue(createdMemberLevel2Document);

        String[] selector1 = new String[3];
        selector1[0] = "user-group";
        selector1[1] = "internal";
        selector1[2] = memberLevel1Id;
        ArrayList<String[]> selectors = new ArrayList<String[]>();
        selectors.add(selector1);

        String taskParam =
            getAddSelectorsTaskParam(selectors, lastModificationDateSuperGroup);

        addSelectors(superGroupId, taskParam);

        selector1[0] = "user-group";
        selector1[1] = "internal";
        selector1[2] = memberLevel2Id;
        selectors = new ArrayList<String[]>();
        selectors.add(selector1);

        taskParam =
            getAddSelectorsTaskParam(selectors,
                lastModificationDateMemberLevel1);

        addSelectors(memberLevel1Id, taskParam);

        selector1[0] = "user-group";
        selector1[1] = "internal";
        selector1[2] = superGroupId;
        selectors = new ArrayList<String[]>();
        selectors.add(selector1);

        taskParam =
            getAddSelectorsTaskParam(selectors,
                lastModificationDateMemberLevel2);
        try {

            addSelectors(memberLevel2Id, taskParam);
            fail("No exception on addSelectors with cyclen");
        }
        catch (Exception e) {
            Class< ? > ec = UserGroupHierarchyViolationException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }

    }

    /**
     * Try to add an internal selector with a name other than
     * "user-account", "user-group".
     *
     * @throws Exception Thrown if an unexpected error occurred.
     */
    @Test
    public void testIssue669() throws Exception {
        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        NodeList selectorNodes =
                selectNodeList(createdDocument, "/user-group/selectors/selector");

        assertEquals("User-group members can not exist on user-group create",
            0, selectorNodes.getLength());

        final String id = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        ArrayList<String[]> selectors = new ArrayList<String[]>();

        selectors.add(
                new String [] 
                     {"organizational-unit", "internal", "escidoc:nobody"});

        String taskParam =
            getAddSelectorsTaskParam(selectors, lastModificationDate);

        try {
            addSelectors(id, taskParam);
            fail("No exception on addSelectors with cyclen");
        }
        catch (Exception e) {
            Class< ? > ec = XmlCorruptedException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Try to add an selector with a type other than
     * "internal", "user-attribute".
     *
     * @throws Exception Thrown if an unexpected error occurred.
     */
    @Test
    public void testWrongSelectorType() throws Exception {
        final Document createdDocument =
            createSuccessfully("escidoc_usergroup_for_create.xml");
        NodeList selectorNodes =
                selectNodeList(createdDocument, "/user-group/selectors/selector");

        assertEquals("User-group members can not exist on user-group create",
            0, selectorNodes.getLength());

        final String id = getObjidValue(createdDocument);
        String lastModificationDate =
            getLastModificationDateValue(createdDocument);
        ArrayList<String[]> selectors = new ArrayList<String[]>();

        selectors.add(
            new String [] {"organizational-unit", "external", "escidoc:nobody"});

        String taskParam =
            getAddSelectorsTaskParam(selectors, lastModificationDate);

        try {
            addSelectors(id, taskParam);
            fail("No exception on addSelectors with cyclen");
        }
        catch (Exception e) {
            Class< ? > ec = XmlSchemaValidationException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test successfully retrieving an explain response.
     * 
     * @test.name explainTest
     * @test.id explainTest
     * @test.input
     * @test.expected: valid explain response.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void explainTest() throws Exception {
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_EXPLAIN, new String[] {""});

        String result = null;

        try {
            result = retrieveUserGroups(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertXmlValidSrwResponse(result);        
    }

    /**
     * Prepare data to test userGroupFilter with user-account.
     * @throws Exception e
     * 
     */
    private void prepareUserGroupUserFilterData() throws Exception {
        //create ou and open it
        String ouXml =
            organizationalUnitTestBase
                    .createSuccessfully("escidoc_ou_create.xml");
        Document createdDocument = getDocument(ouXml);
        String ouId = getObjidValue(getTransport(), createdDocument);
        String ouTitle = getTitleValue(createdDocument);
        String lastModDate = getLastModificationDateValue(createdDocument);
        organizationalUnitTestBase.open(ouId,
            "<param last-modification-date=\"" + lastModDate + "\" />");
        
        //create ou with parent=otherOu
        String[] parentValues = new String[2];
        parentValues[0] = ouId;
        parentValues[1] = ouTitle;
        
        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH,
                "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);
        organizationalUnitTestBase.insertParentsElement(toBeCreatedDocument,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, parentValues, false);

        String toBeCreatedXml = toString(toBeCreatedDocument, false);
        ouXml = organizationalUnitTestBase.create(toBeCreatedXml);
        createdDocument = getDocument(ouXml);
        String ouId1 = getObjidValue(getTransport(), createdDocument);
        lastModDate = getLastModificationDateValue(createdDocument);
        organizationalUnitTestBase.open(ouId1,
            "<param last-modification-date=\"" + lastModDate + "\" />");

        //create user with child ou
        Document createdUser =
            userAccountTestBase.createSuccessfully(
                "escidoc_useraccount_for_create1.xml");
        userAccountOuUser = getObjidValue(getTransport(), createdUser);
        userAttributeTestBase.createAttribute(
                userAccountOuUser, "<attribute xmlns="
                + "\"http://www.escidoc.de/schemas/attributes/0.1\""
                + " name=\"o\">"
                + ouId1 + "</attribute>");
        
        //create user with attribute
        String attributeName = "uafiltertestkey" + System.currentTimeMillis();
        String attributeValue = "uafiltertestvalue" + System.currentTimeMillis();
        createdUser = userAccountTestBase.createSuccessfully(
                "escidoc_useraccount_for_groupfilter_test.xml");
        userAccountAttributeUser = getObjidValue(getTransport(), createdUser);
        userAttributeTestBase.createAttribute(
                userAccountAttributeUser, "<attribute xmlns="
                + "\"http://www.escidoc.de/schemas/attributes/0.1\""
                + " name=\"" + attributeName + "\">"
                + attributeValue + "</attribute>");
        
        //create searchable groups
        for (int i = 0; i < additonalUserFilterSearchGroupsCount; i++) {
            toBeCreatedDocument =
                getTemplateAsDocument(TEMPLATE_USER_GROUP_PATH,
                    "escidoc_usergroup_for_userfilter_test.xml");
            uniqueIdentifier = Long.toString(System.currentTimeMillis());
            insertUserGroupValues(toBeCreatedDocument, 
                    "filtertestemail_" + i + "_" + uniqueIdentifier + "@test.de", 
                    "filtertestname_" + i + "_" + uniqueIdentifier, 
                    "filtertestlabel_" + i + "_" + uniqueIdentifier,
                    "filtertestdescription_" + i + "_" + uniqueIdentifier);
            String groupXml = handleXmlResult(
                    getClient().create(toString(toBeCreatedDocument, false)));
            additonalUserFilterSearchGroups[i] = 
                getObjidValue(getTransport(), getDocument(groupXml));
        }
        
        //create group with attribute-user-selector
        toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_USER_GROUP_PATH,
                "escidoc_usergroup_for_userfilter_test.xml");
        insertUserGroupValues(toBeCreatedDocument, 
                "test@test.de", 
                "test", 
                "test" + System.currentTimeMillis(),
                "test");
        String groupXml = handleXmlResult(
                getClient().create(toString(toBeCreatedDocument, false)));
        Document createdGroup = getDocument(groupXml);
        childGroup = getObjidValue(getTransport(), createdGroup);
        String lastModificationDate =
            getLastModificationDateValue(createdGroup);
        String[] selector1 = new String[3];
        selector1[0] = attributeName;
        selector1[1] = "user-attribute";
        selector1[2] = attributeValue;
        ArrayList<String[]> selectors = new ArrayList<String[]>();
        selectors.add(selector1);
        String taskParam =
            getAddSelectorsTaskParam(
                            selectors, lastModificationDate);
        addSelectors(childGroup, taskParam);
        
        
        //create group with user, ou and group selectors
        toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_USER_GROUP_PATH,
                "escidoc_usergroup_for_userfilter_test.xml");
        insertUserGroupValues(toBeCreatedDocument, 
                "test@test.de", 
                "sonstwas", 
                "sonstwas" + System.currentTimeMillis(),
                "sonstwas");
        groupXml = handleXmlResult(
                getClient().create(toString(toBeCreatedDocument, false)));
        createdGroup = getDocument(groupXml);
        parentGroup = getObjidValue(getTransport(), createdGroup);
        lastModificationDate = getLastModificationDateValue(createdGroup);
        selector1[0] = "o";
        selector1[1] = "user-attribute";
        selector1[2] = ouId;
        String[] selector2 = new String[3];
        selector2[0] = "user-account";
        selector2[1] = "internal";
        selector2[2] = TEST_USER_ACCOUNT_ID;
        String[] selector3 = new String[3];
        selector3[0] = "user-group";
        selector3[1] = "internal";
        selector3[2] = childGroup;
        selectors = new ArrayList<String[]>();
        selectors.add(selector1);
        selectors.add(selector2);
        selectors.add(selector3);
        taskParam =
            getAddSelectorsTaskParam(
                    selectors, lastModificationDate);
        addSelectors(parentGroup, taskParam);

    }
}
