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
import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.AlreadyActiveException;
import de.escidoc.core.common.exceptions.remote.application.violated.AlreadyDeactiveException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.remote.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.remote.system.SqlDatabaseSystemException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.oum.organizationalunit.OrganizationalUnitTestBase;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Test suite for the userAccount resource.
 * 
 * @author Torsten Tetteroo
 *         <p/>
 *         changed for schema version 0.5:
 *         <ul>
 *         <li>Removed testAACua8 (primary attribute has been removed)</li>
 *         <li>Removed testAACua9 (primary attribute has been removed)</li>
 *         <li>Removed testAARuas11 (primary attribute has been removed)</li>
 *         <li>Modified testAARuas13 (primary attribute has been removed)</li>
 *         </ul>
 *         <p/>
 *         changes for schema version 0.3:
 *         <ul>
 *         <li>Replaced UM_CUA-4, UM_CUA-4-2, UM_CUA-10 by UM_CUA-10-rest/UM_CUA-10-soap</li>
 *         <li>Replaced UM_CUA-4-3 by UM_CUA-4-3-rest</li>
 *         <li>Replaced UM_UUA-7 by UM_UUA-7-rest/UM_UUA-7-soap</li>
 *         <li>Replaced UM_UUA-10 by UM_UUA-10-rest/UM_UUA-10-soap</li>
 *         <li>Replaced UM_UUA-11, UM_UUA-11-2, UM_UUA-11-3 by UM_UUA-11-rest, UM_UUA-11-2-rest, UM_UUA-11-3-rest</li>
 *         <li>Replaced UM_UUA-11-4 by UM_UUA-11-4-soap</li>
 *         <li>Replaced UM_RVR-1, UM_RVR-2, UM_RVR-2-2, and UM_RVR-3 by UM_RVR-1-rest, UM_RVR-2-rest, UM_RVR-2-2-rest,
 *         and UM_RVR-3-rest</li>
 *         </ul>
 */
public class UserAccountIT extends UserAccountTestBase {

    public static final String XPATH_RDF_ABOUT = XPATH_USER_ACCOUNT_LIST_USER_ACCOUNT + "/@about";

    public static final String RDF_RESOURCE_USER_ACCOUNT = "http://www.escidoc.de/core/01/resources/UserAccount";

    public static final String XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT = XPATH_SRW_RESPONSE_OBJECT + NAME_USER_ACCOUNT;

    private static final String SYSTEM_ADMINISTRATOR_USER_ID = "escidoc:testsystemadministrator";

    private static final String SYSTEM_ADMINISTRATOR_NAME = "Test System Administrator User";

    private static final String SYSTEM_ADMINISTRATOR_LOGIN_NAME = "testsystemadministrator";

    private static final String SYSTEM_INSPECTOR_USER_ID = "escidoc:testsysteminspector";

    private static final String SYSTEM_INSPECTOR_NAME = "Test System-Inspector User";

    private static final String SYSTEM_INSPECTOR_LOGIN_NAME = "testsysteminspector";

    private static final String DEPOSITOR_USER_ID = "escidoc:testdepositor";

    private static final String DEPOSITOR_NAME = "Test Depositor User";

    private static final String DEPOSITOR_LOGIN_NAME = "testdepositor";

    private static UserAttributeTestBase userAttributeTestBase = null;

    private static UserPreferenceTestBase userPreferenceTestBase = null;

    private static GrantTestBase grantTestBase = null;

    private static UserGroupTestBase userGroupTestBase = null;

    private static OrganizationalUnitTestBase organizationalUnitTestBase = null;

    private static String userAccountFilterGroup;

    private static String userAccountFilterUser;

    private static String userAccountFilterUser1;

    private static int additonalGroupFilterSearchUsersCount = 5;

    private static String[] additonalGroupFilterSearchUsers = new String[additonalGroupFilterSearchUsersCount];

    private static String uniqueIdentifier;

    private static int methodCounter = 0;

    public UserAccountIT() throws Exception {
        userAttributeTestBase = new UserAttributeTestBase() {
        };
        userPreferenceTestBase = new UserPreferenceTestBase() {
        };
        grantTestBase = new GrantTestBase(
            USER_ACCOUNT_HANDLER_CODE) {
        };
        organizationalUnitTestBase = new OrganizationalUnitTestBase();
        userGroupTestBase = new UserGroupTestBase() {
        };
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
            prepareUserAccountGroupFilterData();
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
     * Test successful creating an UserAccount resource.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACua1() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");

        assertEquals("Creation date and last modification date are different. ", assertCreationDateExists("",
            createdDocument), getLastModificationDateValue(createdDocument));
    }

    /**
     * Test declining creation of UserAccount with corrupted XML data.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACua2() throws Exception {

        try {
            create("<Corrupt XML data");
            EscidocAbstractTest.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining creation of UserAccount without providing XML data.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACua3() throws Exception {

        try {
            create(null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining creation of UserAccount with missing mandatory element in XML data.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACua5() throws Exception {

        final Document toBeCreatedDocument =
            EscidocAbstractTest.getDocument(EscidocAbstractTest.getTemplateAsString(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_useraccount_for_create.xml"));
        deleteElement(toBeCreatedDocument, XPATH_USER_ACCOUNT_LOGINNAME);

        try {
            create(toString(toBeCreatedDocument, false));
            EscidocAbstractTest.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining creation of UserAccount with reserved String as login Name.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACua61() throws Exception {

        final Document toBeCreatedDocument =
            EscidocAbstractTest.getDocument(EscidocAbstractTest.getTemplateAsString(TEMPLATE_USER_ACCOUNT_PATH,
                "escidoc_useraccount_for_create.xml"));
        substitute(toBeCreatedDocument, XPATH_USER_ACCOUNT_LOGINNAME, "current");

        try {
            create(toString(toBeCreatedDocument, false));
            EscidocAbstractTest.failMissingException(UniqueConstraintViolationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(UniqueConstraintViolationException.class, e);
        }
    }

    /**
     * Test declining creation of UserAccount with non-unique login name in XML data.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAACua6() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String toBeCreatedDocument = toString(createdDocument, false);

        try {
            create(toBeCreatedDocument);
            EscidocAbstractTest.failMissingException(UniqueConstraintViolationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(UniqueConstraintViolationException.class, e);
        }
    }

    /**
     * Test successful retrieving an existing UserAccount resource by its id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARua1() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String createdUserAccountXML = toString(createdDocument, false);

        String retrievedUserAccountXML = null;
        try {
            retrievedUserAccountXML = retrieve(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertUserAccount(retrievedUserAccountXML, createdUserAccountXML, startTimestamp, startTimestamp, true);
    }

    /**
     * Test successful retrieving an existing UserAccount resource by its loginname.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARua1_2() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String loginname = selectSingleNode(createdDocument, XPATH_USER_ACCOUNT_LOGINNAME).getTextContent();

        String retrievedXml = null;
        try {
            retrievedXml = retrieve(loginname);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertUserAccount(retrievedXml, toString(createdDocument, false), startTimestamp, startTimestamp, true);
    }

    /**
     * Test successful retrieving an existing UserAccount resource by its loginname.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void testAARuaWithLoginNameContainingSpace() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_WithSpaceInLoginName_create.xml");
        final String loginname = selectSingleNode(createdDocument, XPATH_USER_ACCOUNT_LOGINNAME).getTextContent();
        String encodedLoginName = URLEncoder.encode(loginname, DEFAULT_CHARSET);
        String retrievedXml = null;
        try {
            retrievedXml = retrieve(encodedLoginName);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertUserAccount(retrievedXml, toString(createdDocument, false), startTimestamp, startTimestamp, true);
    }

    /**
     * Test retrieving the current user.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveCurrentUser() throws Exception {

        try {
            retrieveCurrentUser(null);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
    }

    /**
     * Test retrieving the current user.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testDecliningRetrieveCurrentUser() throws Exception {

        try {
            retrieveCurrentUser(PWCallback.ANONYMOUS_HANDLE);
            EscidocAbstractTest.failMissingException(UserAccountNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(UserAccountNotFoundException.class, e);
        }
    }

    /**
     * Test declining the retrieval of an UserAccount with unknown id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARua2() throws Exception {

        try {
            retrieve(UNKNOWN_ID);
            EscidocAbstractTest.failMissingException(UserAccountNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", UserAccountNotFoundException.class, e);
        }
    }

    /**
     * Test declining the retrieval of an UserAccount with invalid id of an existing resource of another resource type.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARua2_2() throws Exception {

        try {
            retrieve(CONTEXT_ID);
            EscidocAbstractTest.failMissingException(UserAccountNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", UserAccountNotFoundException.class, e);
        }
    }

    /**
     * Test declining the retrieval of an UserAccount with missing parameter id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARua3() throws Exception {
        try {
            retrieve(null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test successful activating an existing, deactive UserAccount.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAAua1() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String createdXml = toString(createdDocument, false);
        final String id = getObjidValue(createdDocument);
        String lastModificationDate = getLastModificationDateValue(createdDocument);
        String taskParamXML = "<param last-modification-date=\"" + lastModificationDate + "\" />";
        final String beforeDeactivationTimestamp = getNowAsTimestamp();
        try {
            deactivate(id, taskParamXML);
        }
        catch (final Exception e1) {
            EscidocAbstractTest.failException(e1);
        }

        String retrievedUserAccountXML = null;
        try {
            retrievedUserAccountXML = retrieve(id);
        }
        catch (final Exception e1) {
            EscidocAbstractTest.failException(e1);
        }
        final Document retrievedDeactivatedDocument =
            assertDeactiveUserAccount(retrievedUserAccountXML, createdXml, startTimestamp, beforeDeactivationTimestamp,
                true);
        lastModificationDate = getLastModificationDateValue(retrievedDeactivatedDocument);
        taskParamXML = "<param last-modification-date=\"" + lastModificationDate + "\" />";

        final String beforeActivationTimestamp = getNowAsTimestamp();

        String xmlData = null;
        try {
            xmlData = activate(id, taskParamXML);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNull("Did not expect result data. ", xmlData);

        String retrievedActivatedXml = null;
        try {
            retrievedActivatedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertActiveUserAccount(retrievedActivatedXml, createdXml, startTimestamp, beforeActivationTimestamp, true);
        final Document retrievedActivatedDocument = EscidocAbstractTest.getDocument(retrievedActivatedXml);
        assertXmlLastModificationDateUpdate("", retrievedDeactivatedDocument, retrievedActivatedDocument);
    }

    /**
     * Test declining the activating of an UserAccount with invalid (unknown) parameter id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAAua2() throws Exception {

        final String taskParamXML =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_task_param.xml");
        try {
            activate(UNKNOWN_ID, taskParamXML);
            EscidocAbstractTest.failMissingException(UserAccountNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", UserAccountNotFoundException.class, e);
        }
    }

    /**
     * Test declining the activating of an UserAccount with id of a resource of another resource type.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAAua2_2() throws Exception {

        final String taskParamXML =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_task_param.xml");
        try {
            activate(CONTEXT_ID, taskParamXML);
            EscidocAbstractTest.failMissingException(UserAccountNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", UserAccountNotFoundException.class, e);
        }
    }

    /**
     * Test declining the activating of an UserAccount with missing parameter id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAAua3() throws Exception {

        final String taskParamXML =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_task_param.xml");
        try {
            activate(null, taskParamXML);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining the activating of an UserAccount that is already active.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAAua4() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String lastModificationDate = getLastModificationDateValue(createdDocument);
        final String taskParamXML = "<param last-modification-date=\"" + lastModificationDate + "\" />";

        try {
            activate(id, taskParamXML);
            EscidocAbstractTest.failMissingException(AlreadyActiveException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(AlreadyActiveException.class, e);
        }
    }

    /**
     * Test declining the activating of an UserAccount with missing task parameters.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAAua5() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);

        try {
            deactivate(id, null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test successful deactivating an existing, active UserAccount.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAADua1() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String loginName =
            selectSingleNodeAsserted(createdDocument, XPATH_USER_ACCOUNT_LOGINNAME).getTextContent();
        final String createdXml = toString(createdDocument, false);
        final String id = getObjidValue(createdDocument);
        final String lastModificationDate = getLastModificationDateValue(createdDocument);
        final String taskParamXML = "<param last-modification-date=\"" + lastModificationDate + "\" />";

        final String beforeDeactivationTimestamp = getNowAsTimestamp();

        String xmlData = null;
        try {
            xmlData = deactivate(id, taskParamXML);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNull("Did not expect result data. ", xmlData);

        String retrievedDeactivatedXml = null;
        try {
            retrievedDeactivatedXml = retrieve(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertDeactiveUserAccount(retrievedDeactivatedXml, createdXml, startTimestamp, beforeDeactivationTimestamp,
            true);
        final Document retrievedDeactivatedDocument = EscidocAbstractTest.getDocument(retrievedDeactivatedXml);
        assertXmlLastModificationDateUpdate("", createdDocument, retrievedDeactivatedDocument);

        // Currently, new created user accounts have predefined password
        // PubManR2
        getUserManagementWrapperClient().login(loginName, "PubManR2", false, true,
            getFrameworkUrl() + "/ir/item/escidoc:1", true);
    }

    /**
     * Test declining the deactivating of an UserAccount with invalid (unknown) parameter id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAADua2() throws Exception {

        final String taskParamXML =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_task_param.xml");
        try {
            deactivate(UNKNOWN_ID, taskParamXML);
            EscidocAbstractTest.failMissingException(UserAccountNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", UserAccountNotFoundException.class, e);
        }
    }

    /**
     * Test declining the deactivating of an UserAccount with id od a resource of another resource type.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAADua2_2() throws Exception {

        final String taskParamXML =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_task_param.xml");
        try {
            deactivate(CONTEXT_ID, taskParamXML);
            EscidocAbstractTest.failMissingException(UserAccountNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", UserAccountNotFoundException.class, e);
        }
    }

    /**
     * Test declining the deactivating of an UserAccount with missing parameter id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAADua3() throws Exception {

        final String taskParamXML =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_task_param.xml");
        try {
            deactivate(null, taskParamXML);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining the deactivating of an UserAccount that is already deactive.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAADua4() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        String lastModificationDate = getLastModificationDateValue(createdDocument);
        String taskParamXML = "<param last-modification-date=\"" + lastModificationDate + "\" />";

        String xmlData = null;
        try {
            deactivate(id, taskParamXML);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }

        try {
            xmlData = retrieve(id);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull("No result from retrieve", xmlData);
        final Document document = EscidocAbstractTest.getDocument(xmlData);
        assertXmlExists("Last modification date does not exist. ", createdDocument, XPATH_USER_ACCOUNT_LAST_MOD_DATE);
        lastModificationDate = selectSingleNode(document, XPATH_USER_ACCOUNT_LAST_MOD_DATE).getTextContent();
        taskParamXML = "<param last-modification-date=\"" + lastModificationDate + "\" />";

        try {
            deactivate(id, taskParamXML);
            EscidocAbstractTest.failMissingException(AlreadyDeactiveException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(AlreadyDeactiveException.class, e);
        }
    }

    /**
     * Test declining the deactivating of an UserAccount with missing task parameters.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAADua5() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);

        try {
            deactivate(id, null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test successfully updating an UserAccount.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua1() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final Document toBeUpdatedDocument = createdDocument;
        assertXmlExists("UserAccount name does not exist. ", toBeUpdatedDocument, XPATH_USER_ACCOUNT_NAME);
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_NAME, "New Name");

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeModificationTimestamp = getNowAsTimestamp();

        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertActiveUserAccount(updatedXml, toBeUpdatedXml, startTimestamp, beforeModificationTimestamp, true);
    }

    /**
     * Test declining update of UserAccount without providing XML data.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua2() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);

        try {
            update(id, null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining update of UserAccount with corrupted XML data.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua3() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);

        try {
            update(id, "<Corrupt XML data");
            EscidocAbstractTest.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining update of UserAccount without providing an id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua4() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");

        try {
            update(null, toString(createdDocument, false));
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining updating of UserAccount in case of an optimistic locking error.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua5() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String objid = getObjidValue(createdDocument);
        update(objid, toString(createdDocument, false));

        try {
            update(objid, toString(createdDocument, false));
            EscidocAbstractTest.failMissingException(OptimisticLockingException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(OptimisticLockingException.class, e);
        }
    }

    /**
     * Test declining updating of UserAccount with corrupted XML data.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua6() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String objid = getObjidValue(createdDocument);

        try {
            update(objid, "<Corrupt XML data");
            EscidocAbstractTest.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining update of UserAccount where a mandatory element is missing in XML data.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua8() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String objid = getObjidValue(createdDocument);
        deleteElement(createdDocument, XPATH_USER_ACCOUNT_LOGINNAME);

        try {
            update(objid, toString(createdDocument, false));
            EscidocAbstractTest.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining update of UserAccount with non-unique login name in XML data.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua9() throws Exception {

        final Document createdDocument1 = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String loginName1 = selectSingleNode(createdDocument1, XPATH_USER_ACCOUNT_LOGINNAME).getTextContent();

        final Document createdDocument2 = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String objid2 = getObjidValue(createdDocument2);
        substitute(createdDocument2, XPATH_USER_ACCOUNT_LOGINNAME, loginName1);

        try {
            update(objid2, toString(createdDocument2, false));
            EscidocAbstractTest.failMissingException(UniqueConstraintViolationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(UniqueConstraintViolationException.class, e);
        }
    }

    /**
     * Test declining updating an UserAccount with unknown id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua12() throws Exception {

        final Document toBeUpdatedDocument = createSuccessfully("escidoc_useraccount_for_create.xml");

        try {
            update(UNKNOWN_ID, toString(toBeUpdatedDocument, false));
            EscidocAbstractTest.failMissingException(UserAccountNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(UserAccountNotFoundException.class, e);
        }

    }

    /**
     * Test declining updating an UserAccount with unknown id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua12_2() throws Exception {

        final Document toBeUpdatedDocument = createSuccessfully("escidoc_useraccount_for_create.xml");

        try {
            update(CONTEXT_ID, toString(toBeUpdatedDocument, false));
            EscidocAbstractTest.failMissingException(UserAccountNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(UserAccountNotFoundException.class, e);
        }

    }

    /**
     * Test successfully updating an UserAccount.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUua13() throws Exception {

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final Document toBeUpdatedDocument = createdDocument;
        assertXmlExists("UserAccount name does not exist. ", toBeUpdatedDocument, XPATH_USER_ACCOUNT_NAME);
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_NAME, "New Name");

        final String toBeUpdatedXml = modifyNamespacePrefixes(toString(toBeUpdatedDocument, false));
        assertXmlValidUserAccount(toBeUpdatedXml);
        final String beforeModificationTimestamp = getNowAsTimestamp();

        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertActiveUserAccount(updatedXml, toBeUpdatedXml, startTimestamp, beforeModificationTimestamp, true);
    }

    /**
     * Test successfully updating the password of an UserAccount.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUup1() throws Exception {

        final String password = "new-pass";
        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String lastModificationDate = getLastModificationDateValue(createdDocument);
        final String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" ><password>" + password
                + "</password> </param>";

        updatePassword(id, taskParamXML);

        final String loginName = selectSingleNode(createdDocument, XPATH_USER_ACCOUNT_LOGINNAME).getTextContent();

        final String handle = login(loginName, password, true);
        assertNotNull("Login with new password was not successful!", handle);
    }

    /**
     * Test declining updating the password of an UserAccount without providing an id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUup2() throws Exception {

        final Class<MissingMethodParameterException> ec = MissingMethodParameterException.class;
        final String password = "new-pass";

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String lastModificationDate = getLastModificationDateValue(createdDocument);
        final String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" ><password>" + password
                + "</password> </param>";

        try {
            updatePassword(null, taskParamXML);
            failMissingException("Updating the password of an UserAccount has not been declined!", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Updating the password of an UserAccount without id has not been "
                + "declined correctly.", ec, e);
        }
    }

    /**
     * Test declining updating the password of an UserAccount without providing a task parameter.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUup3() throws Exception {

        final Class<MissingMethodParameterException> ec = MissingMethodParameterException.class;

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);

        try {
            updatePassword(id, null);
            failMissingException("Updating the password of an UserAccount has not been declined!", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Updating the password of an UserAccount without param has not "
                + "been declined correctly.", ec, e);
        }
    }

    /**
     * Test declining updating the password of an UserAccount without providing a password.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUup4() throws Exception {

        final Class<MissingMethodParameterException> ec = MissingMethodParameterException.class;
        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String lastModificationDate = getLastModificationDateValue(createdDocument);
        final String taskParamXML = "<param last-modification-date=\"" + lastModificationDate + "\" ></param>";

        try {
            updatePassword(id, taskParamXML);
            failMissingException("Updating the password of an UserAccount has not been declined!", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Updating the password of an UserAccount without password has not"
                + " been declined correctly.", ec, e);
        }
    }

    /**
     * Test declining updating the password of an UserAccount wiht an empty last-modification timestamp.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUup5() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;
        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String lastModificationDate = "";
        final String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" ><password>password </password> </param>";
        try {
            updatePassword(id, taskParamXML);
            failMissingException("Updating the password of an UserAccount has not been declined!", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Updating the password of an UserAccount with an empty "
                + "last-modification timestamp has not been declined correctly.", ec, e);
        }
    }

    /**
     * Test declining updating the password of an UserAccount without providing a last-modification-timestamp.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUup6() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;
        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String taskParamXML = "<param><password>password </password> </param>";
        try {
            updatePassword(id, taskParamXML);
            failMissingException("Updating the password of an UserAccount has not been declined!", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Updating the password of an UserAccount withoutwithout "
                + "providing a last-modification-timestamp has not been " + "declined correctly.", ec, e);
        }
    }

    /**
     * Test declining updating the password of an UserAccount with an unknown id.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUup7() throws Exception {

        final Class<UserAccountNotFoundException> ec = UserAccountNotFoundException.class;
        final String password = "new-pass";

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String lastModificationDate = getLastModificationDateValue(createdDocument);
        final String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" ><password>" + password
                + "</password> </param>";
        try {
            updatePassword(UNKNOWN_ID, taskParamXML);
            failMissingException("Updating the password of an UserAccount has not been declined!", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Updating the password of an UserAccount with an unknown id has "
                + "not been declined correctly.", ec, e);
        }
    }

    /**
     * Test declining updating the password of an UserAccount of a deactivated UserAccount.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUup8() throws Exception {

        final Class<InvalidStatusException> ec = InvalidStatusException.class;
        final String password = "new-pass";

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        String lastModificationDate = getLastModificationDateValue(createdDocument);
        String taskParamXML = "<param last-modification-date=\"" + lastModificationDate + "\" ></param>";
        deactivate(id, taskParamXML);

        lastModificationDate = getLastModificationDateValue(getDocument(retrieve(id)));

        taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" ><password>" + password
                + "</password> </param>";
        try {
            updatePassword(id, taskParamXML);
            failMissingException("Updating the password of an UserAccount has not been declined!", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Updating the password of an deactivated UserAccount has not been"
                + " declined correctly.", ec, e);
        }
    }

    /**
     * Test declining updating the password of an UserAccount with a wrong last-modification timestamp.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUup9() throws Exception {

        final Class<OptimisticLockingException> ec = OptimisticLockingException.class;
        final String password = "new-pass";

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String lastModificationDate = getNowAsTimestamp();
        final String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" ><password>" + password
                + "</password> </param>";

        try {
            updatePassword(id, taskParamXML);
            failMissingException("Updating the password of an UserAccount has not been declined!", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Updating the password of an UserAccount wrong last-modification "
                + "timestamp has not been declined correctly.", ec, e);
        }
    }

    /**
     * Test declining updating the password of an UserAccount with a wrong last-modification timestamp.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAAUup10() throws Exception {

        final String lastModificationDate = getNowAsTimestamp();
        final Class<OptimisticLockingException> ec = OptimisticLockingException.class;
        final String password = "new-pass";

        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String taskParamXML =
            "<param last-modification-date=\"" + lastModificationDate + "\" ><password>" + password
                + "</password> </param>";

        try {
            updatePassword(id, taskParamXML);
            failMissingException("Updating the password of an UserAccount has not been declined!", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Updating the password of an UserAccount wrong last-modification "
                + "timestamp has not been declined correctly.", ec, e);
        }
    }

    /**
     * Test deleting a user-account that has no referencing data-records.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testDeleteUserWithoutReferences() throws Exception {
        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");

        final String id = getObjidValue(createdDocument);
        userAttributeTestBase.createAttribute(id, "<attribute xmlns="
            + "\"http://www.escidoc.de/schemas/attributes/0.1\"" + " name=\"key\">value</attribute>");
        userPreferenceTestBase.createPreference(id,
            "<preference xmlns=\"http://www.escidoc.de/schemas/preferences/0.1\"" + " name=\"key\">value</preference>");
        delete(id);
        try {
            retrieve(id);
            fail("No exception on retrieve user after delete.");
        }
        catch (final Exception e) {
            Class<?> ec = UserAccountNotFoundException.class;
            EscidocAbstractTest.assertExceptionType(ec, e);
        }

    }

    /**
     * Test declining deleting a user-account that has referencing data-records.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testDecliningDeleteUserWithReferences() throws Exception {
        String grantId = null;
        String grantXml =
            grantTestBase.doTestCreateGrant(PWCallback.SYSTEMADMINISTRATOR_HANDLE, TEST_SYSTEMADMINISTRATOR_ID1,
                Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID, ROLE_HREF_DEPOSITOR, null);
        grantId = getObjidValue(grantXml);
        try {
            delete(TEST_SYSTEMADMINISTRATOR_ID1);
            fail("No exception on delete user with references.");
        }
        catch (final Exception e) {
            Class<?> ec = SqlDatabaseSystemException.class;
            EscidocAbstractTest.assertExceptionType(ec, e);
        }
        finally {
            if (grantId != null) {
                grantTestBase.doTestRevokeGrant(null, TEST_SYSTEMADMINISTRATOR_ID1, grantId, null);
            }
        }

    }

    /**
     * Test successful retrieving a list of existing UserAccount resources.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas1CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_NAME + "\"=%" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);
        assertXmlExists("Missing user System Administrator User.", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[properties/login-name='" + SYSTEM_ADMINISTRATOR_LOGIN_NAME
                + "']");
        assertXmlExists("Missing user System Inspector (Read Only Super User).", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[properties/login-name='" + SYSTEM_INSPECTOR_LOGIN_NAME + "']");
        assertXmlExists("Missing user Depositor User.", retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT
            + "[properties/login-name='" + DEPOSITOR_LOGIN_NAME + "']");
        // FIXME further assertions needed
    }

    /**
     * Test declining retrieving a list of user accounts with providing corrupted filter parameter.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas3() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_NAME + "\"=or or or or and" });
        try {
            retrieveUserAccounts(filterParams);
            EscidocAbstractTest.failMissingException("Retrieving user accounts with providing corrupted filter params"
                + " not declined. ", InvalidSearchQueryException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Retrieving user accounts with providing corrupted filter params"
                + "not declined, properly. ", InvalidSearchQueryException.class, e);
        }
    }

    /**
     * Test declining retrieving a list of user accounts with providing invalid filter.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas4CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + NAME_CREATED_BY + "\"=\"Some value\"" });
        try {
            retrieveUserAccounts(filterParams);
            EscidocAbstractTest.failMissingException("Retrieving user accounts with providing invalid filter params"
                + " not declined. ", InvalidSearchQueryException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Retrieving user accounts with providing invalid filter params"
                + "not declined, properly. ", InvalidSearchQueryException.class, e);
        }
    }

    /**
     * Test successful retrieving a list of existing UserAccount resources using filter user-accounts.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas5CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"="
            + SYSTEM_ADMINISTRATOR_USER_ID + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + DEPOSITOR_USER_ID });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        assertEquals("Unexpected number of user accounts.", 2, userAccountNodes.getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);
        assertXmlExists("Missing user System Administrator User.", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[properties/login-name='" + SYSTEM_ADMINISTRATOR_LOGIN_NAME
                + "']");
        assertXmlExists("Missing user Depositor User.", retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT
            + "[properties/login-name='" + DEPOSITOR_LOGIN_NAME + "']");
        // FIXME further assertions needed
    }

    /**
     * Test successful retrieving a list of existing UserAccount resources using filter active.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas6CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_ACTIVE + "\"=true" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);
        assertXmlExists("Missing user System Administrator User.", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[properties/login-name='" + SYSTEM_ADMINISTRATOR_LOGIN_NAME
                + "']");
        assertXmlExists("Missing user System Inspector (Read Only Super User).", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[properties/login-name='" + SYSTEM_INSPECTOR_LOGIN_NAME + "']");
        assertXmlExists("Missing user Depositor User.", retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT
            + "[properties/login-name='" + DEPOSITOR_LOGIN_NAME + "']");
        assertXmlNotExists("Unexpected deactivated user account", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[active='false']");
        // FIXME further assertions needed
    }

    /**
     * Test successful retrieving a list of existing UserAccount resources using filter deactive.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas7CQL() throws Exception {

        // prepare test by creating a user account and deactivate it
        final Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String loginName =
            selectSingleNodeAsserted(createdDocument, XPATH_USER_ACCOUNT_LOGINNAME).getTextContent();
        final String lastModificationDate = getLastModificationDateValue(createdDocument);
        final String taskParamXML = "<param last-modification-date=\"" + lastModificationDate + "\" />";

        try {
            deactivate(id, taskParamXML);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_ACTIVE + "\"=false" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);
        assertXmlExists("Missing deactivated user [" + id + "]", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[properties/login-name='" + loginName + "']");
        assertXmlNotExists("Unexpected activated user account", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[properties/active='true']");
        // FIXME further assertions needed
    }

    /**
     * Test declining retrieving a list of existing UserAccount resources using unsupported filter email.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas8CQL() throws Exception {

        final Class<InvalidSearchQueryException> ec = InvalidSearchQueryException.class;

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_EMAIL
            + "\"=\"system.administrator@superuser\"" });

        try {
            retrieveUserAccounts(filterParams);
            failMissingException("Retrieving with unknown filter criteria not declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving with unknown filter criteria not declined," + " properly.", ec, e);
        }

    }

    /**
     * Test successful retrieving a list of existing UserAccount resources using filter login-name.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas9CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_LOGIN_NAME + "\"=%testsys%" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        assertEquals("Unexpected number of user accounts.", 3, userAccountNodes.getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);
        assertXmlExists("Missing user System Administrator User.", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[properties/login-name='" + SYSTEM_ADMINISTRATOR_LOGIN_NAME
                + "']");
        // FIXME further assertions needed
    }

    /**
     * Test successful retrieving a list of existing UserAccount resources using filter name.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas10CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_NAME + "\"=\"%System Administrator%\"" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        assertEquals("Unexpected number of user accounts.", 3, userAccountNodes.getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);
        assertXmlExists("Missing user System Administrator User.", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[properties/login-name='" + SYSTEM_ADMINISTRATOR_LOGIN_NAME
                + "']");
        // FIXME further assertions needed
    }

    /**
     * Test successful retrieving a list of existing UserAccount resources using filter ou.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas12CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_ORGANIZATIONAL_UNIT + "\"=escidoc:ex3" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);
        assertXmlExists("Missing user System Administrator User.", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[properties/login-name='sysadmin']");
        assertXmlExists("Missing user System Inspector (Read Only Super User).", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[properties/login-name='inspector']");
        assertXmlExists("Missing user Depositor User.", retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT
            + "[properties/login-name='depositor']");

        // FIXME: assert only containing user accounts of addressed ou.

        // FIXME further assertions needed
    }

    /**
     * Test successful retrieving a list of existing UserAccount resources using filter group.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas121CQL() throws Exception {
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_URI_GROUP + "\"="
            + userAccountFilterGroup });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);

        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        assertEquals("Unexpected number of user accounts.", 3, userAccountNodes.getLength());

        String href = "";
        String attributeName = "objid";
        href = "/aa/user-account/";
        attributeName = "href";
        assertXmlExists("Missing user " + TEST_USER_ACCOUNT_ID, retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[@" + attributeName + "='" + href + TEST_USER_ACCOUNT_ID + "']");
        assertXmlExists("Missing user " + userAccountFilterUser, retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[@" + attributeName + "='" + href + userAccountFilterUser
                + "']");
        assertXmlExists("Missing user " + userAccountFilterUser1, retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[@" + attributeName + "='" + href + userAccountFilterUser1
                + "']");
    }

    /**
     * Test successfully retrieving an empty list of user-accounts.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas13CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_ACTIVE + "\"=true and " + "\""
            + FILTER_IDENTIFIER + "\"=escidoc:persistent3" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        assertXmlNotExists("List is not empty but should be.", retrievedDocument, XPATH_USER_ACCOUNT_LIST_USER_ACCOUNT);
    }

    /**
     * Test successfully retrieving a list of user-accounts using multiple filters.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas14CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_IDENTIFIER + "\"="
            + SYSTEM_ADMINISTRATOR_USER_ID + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + SYSTEM_INSPECTOR_USER_ID
            + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + DEPOSITOR_USER_ID + ") and " + "\"" + FILTER_LOGIN_NAME
            + "\"=testsys% and " + "\"" + FILTER_NAME + "\"=%Sys% and " + "\"" + FILTER_ACTIVE + "\"=true" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        assertEquals("Unexpected number of user accounts.", 2, userAccountNodes.getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);
        assertXmlExists("Missing user System Administrator User.", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[properties/login-name='" + SYSTEM_ADMINISTRATOR_LOGIN_NAME
                + "']");
        assertXmlExists("Missing user System Inspector User.", retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[properties/login-name='" + SYSTEM_INSPECTOR_LOGIN_NAME + "']");
    }

    /**
     * Test successfully retrieving a list of user-accounts using multiple filters including group-filter.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas141CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_IDENTIFIER + "\"="
            + additonalGroupFilterSearchUsers[0] + " or " + "\"" + FILTER_IDENTIFIER + "\"="
            + additonalGroupFilterSearchUsers[1] + " or " + "\"" + FILTER_USER_ACCOUNT_GROUP + "\"="
            + userAccountFilterGroup + ") and " + "\"" + FILTER_LOGIN_NAME + "\"=%test% and " + "\"" + FILTER_ACTIVE
            + "\"=true" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);

        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        assertEquals("Unexpected number of user accounts.", 4, userAccountNodes.getLength());

        String href = "";
        String attributeName = "objid";
        href = "/aa/user-account/";
        attributeName = "href";
        assertXmlExists("Missing user " + additonalGroupFilterSearchUsers[0], retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[@" + attributeName + "='" + href
                + additonalGroupFilterSearchUsers[0] + "']");
        assertXmlExists("Missing user " + additonalGroupFilterSearchUsers[1], retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[@" + attributeName + "='" + href
                + additonalGroupFilterSearchUsers[1] + "']");
        assertXmlExists("Missing user " + userAccountFilterUser1, retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[@" + attributeName + "='" + href + userAccountFilterUser1
                + "']");
        assertXmlExists("Missing user " + TEST_USER_ACCOUNT_ID, retrievedDocument,
            XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT + "[@" + attributeName + "='" + href + TEST_USER_ACCOUNT_ID + "']");
    }

    /**
     * Test filters including group-filter.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas143CQL() throws Exception {
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_IDENTIFIER + "\"="
            + additonalGroupFilterSearchUsers[0] + " or " + "\"" + FILTER_IDENTIFIER + "\"="
            + additonalGroupFilterSearchUsers[1] + " or " + "\"" + FILTER_USER_ACCOUNT_GROUP
            + "\"=\"neuegruppe\") and " + "\"" + FILTER_LOGIN_NAME + "\"=%test% and " + "\"" + FILTER_ACTIVE
            + "\"=true" });

        String retrievedUserAccountsXml = null;
        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of user accounts failed. ", e);
        }
        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);

        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        assertEquals("Unexpected number of user accounts.", 2, userAccountNodes.getLength());

    }

    /**
     * Test invalid filters including group-filter.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas144CQL() throws Exception {
        final Class<InvalidSearchQueryException> ec = InvalidSearchQueryException.class;

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_IDENTIFIER + "\"="
            + additonalGroupFilterSearchUsers[0] + " or " + "\"" + FILTER_IDENTIFIER + "\"="
            + additonalGroupFilterSearchUsers[1] + " or " + "\"" + FILTER_USER_ACCOUNT_GROUP + "\"=neuegru%) and "
            + "\"" + FILTER_LOGIN_NAME + "\"=%test% and " + "\"" + FILTER_ACTIVE + "\"=true" });

        try {
            retrieveUserAccounts(filterParams);
            failMissingException("Retrieving with wildcard group filter " + "criteria not declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving with wildcard group filter " + "criteria not declined properly.", ec, e);
        }

    }

    /**
     * Test invalid filters including group-filter.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas145CQL() throws Exception {
        final Class<InvalidSearchQueryException> ec = InvalidSearchQueryException.class;

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_IDENTIFIER + "\"="
            + additonalGroupFilterSearchUsers[0] + " or " + "\"" + FILTER_IDENTIFIER + "\"="
            + additonalGroupFilterSearchUsers[1] + " or " + "\"" + FILTER_USER_ACCOUNT_GROUP
            + "\" any neuegruppe) and " + "\"" + FILTER_LOGIN_NAME + "\"=%test% and " + "\"" + FILTER_ACTIVE
            + "\"=true" });

        try {
            retrieveUserAccounts(filterParams);
            failMissingException("Retrieving with non-allowed relation group filter " + "criteria not declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving with non-allowed relation group filter "
                + "criteria not declined properly.", ec, e);
        }

    }

    /**
     * Test invalid filters including group-filter.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas146CQL() throws Exception {
        final Class<InvalidSearchQueryException> ec = InvalidSearchQueryException.class;

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_IDENTIFIER + "\"="
            + additonalGroupFilterSearchUsers[0] + " or " + "\"" + FILTER_IDENTIFIER + "\"="
            + additonalGroupFilterSearchUsers[1] + " or " + "\"" + FILTER_USER_ACCOUNT_GROUP + "\" > neuegruppe) and "
            + "\"" + FILTER_LOGIN_NAME + "\"=%test% and " + "\"" + FILTER_ACTIVE + "\"=true" });

        try {
            retrieveUserAccounts(filterParams);
            failMissingException("Retrieving with non-allowed relation group filter " + "criteria not declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving with non-allowed relation group filter "
                + "criteria not declined properly.", ec, e);
        }

    }

    /**
     * Test invalid filters including group-filter.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas147CQL() throws Exception {
        final Class<InvalidSearchQueryException> ec = InvalidSearchQueryException.class;

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_IDENTIFIER + "\"="
            + additonalGroupFilterSearchUsers[0] + " or " + "\"" + FILTER_IDENTIFIER + "\"="
            + additonalGroupFilterSearchUsers[1] + " or " + "\"" + FILTER_USER_ACCOUNT_GROUP + "\" >= neuegruppe) and "
            + "\"" + FILTER_LOGIN_NAME + "\"=%test% and " + "\"" + FILTER_ACTIVE + "\"=true" });

        try {
            retrieveUserAccounts(filterParams);
            failMissingException("Retrieving with non-allowed relation group filter " + "criteria not declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving with non-allowed relation group filter "
                + "criteria not declined properly.", ec, e);
        }

    }

    /**
     * Test decline retrieving a list of user-accounts using unsupported filter.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas16CQL() throws Exception {

        final Class<InvalidSearchQueryException> ec = InvalidSearchQueryException.class;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_IDENTIFIER + "\"="
            + SYSTEM_ADMINISTRATOR_USER_ID + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + SYSTEM_INSPECTOR_USER_ID
            + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + DEPOSITOR_USER_ID + ") and " + "\"" + FILTER_LOGIN_NAME
            + "\"=testsys% and " + "\"" + FILTER_NAME + "\"=%Sys% and " + "\"" + FILTER_ACTIVE + "\"=true and " + "\""
            + FILTER_CONTEXT + "\"=escidoc:persistent3" });
        try {
            retrieveUserAccounts(filterParams);
            failMissingException("Retrieving with unknown filter criteria not declined.", ec);
        }
        catch (final Exception e) {
            assertExceptionType("Retrieving with unknown filter criteria not declined," + " properly.", ec, e);
        }
    }

    /**
     * Test successfully retrieving a list of user-accounts using id filter and ascending ordering by name.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas17CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"="
            + SYSTEM_ADMINISTRATOR_USER_ID + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + SYSTEM_INSPECTOR_USER_ID
            + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + DEPOSITOR_USER_ID + " sortby " + "\"" + FILTER_NAME
            + "\"/sort.ascending" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        assertEquals("Unexpected number of user accounts.", 3, userAccountNodes.getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);

        assertXmlExists("Missing user System Inspector User.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[3]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_ACCOUNT + "/properties[name='" + SYSTEM_INSPECTOR_NAME
            + "']");
        assertXmlExists("Missing user System Administrator User.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[2]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_ACCOUNT + "/properties[name='" + SYSTEM_ADMINISTRATOR_NAME
            + "']");
        assertXmlExists("Missing user Depositor User.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[1]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_ACCOUNT + "/properties[name='" + DEPOSITOR_NAME + "']");
    }

    /**
     * Test successfully retrieving a list of user-accounts using id filter and descending ordering by name.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas18CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"="
            + SYSTEM_ADMINISTRATOR_USER_ID + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + SYSTEM_INSPECTOR_USER_ID
            + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + DEPOSITOR_USER_ID + " sortby " + "\"" + FILTER_NAME
            + "\"/sort.descending" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        assertEquals("Unexpected number of user accounts.", 3, userAccountNodes.getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);

        assertXmlExists("Missing user System Inspector User.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[1]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_ACCOUNT + "/properties[name='" + SYSTEM_INSPECTOR_NAME
            + "']");
        assertXmlExists("Missing user System Administrator User.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[2]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_ACCOUNT + "/properties[name='" + SYSTEM_ADMINISTRATOR_NAME
            + "']");
        assertXmlExists("Missing user Depositor User.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[3]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_ACCOUNT + "/properties[name='" + DEPOSITOR_NAME + "']");
    }

    /**
     * Test successfully retrieving a list of user-accounts using id filter, descending ordering by name and offset.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas19CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"="
            + SYSTEM_ADMINISTRATOR_USER_ID + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + SYSTEM_INSPECTOR_USER_ID
            + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + DEPOSITOR_USER_ID + " sortby " + "\"" + FILTER_NAME
            + "\"/sort.descending" });
        filterParams.put(FILTER_PARAMETER_STARTRECORD, new String[] { "2" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        assertEquals("Unexpected number of user accounts.", 2, userAccountNodes.getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);

        assertXmlExists("Missing user System Administrator User.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[1]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_ACCOUNT + "/properties[name='" + SYSTEM_ADMINISTRATOR_NAME
            + "']");
        assertXmlExists("Missing user Depositor User.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[2]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_ACCOUNT + "/properties[name='" + DEPOSITOR_NAME + "']");
    }

    /**
     * Test successfully retrieving a list of user-accounts using id filter, descending ordering by name, offset, and
     * limit.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas20CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_IDENTIFIER + "\"="
            + SYSTEM_ADMINISTRATOR_USER_ID + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + SYSTEM_INSPECTOR_USER_ID
            + " or " + "\"" + FILTER_IDENTIFIER + "\"=" + DEPOSITOR_USER_ID + " sortby " + "\"" + FILTER_NAME
            + "\"/sort.descending" });
        filterParams.put(FILTER_PARAMETER_STARTRECORD, new String[] { "2" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        assertEquals("Unexpected number of user accounts.", 1, userAccountNodes.getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);

        assertXmlExists("Missing user System Administrator User.", retrievedDocument, XPATH_SRW_RESPONSE_RECORD + "[1]"
            + XPATH_SRW_RESPONSE_OBJECT_SUBPATH + NAME_USER_ACCOUNT + "/properties[name='" + SYSTEM_ADMINISTRATOR_NAME
            + "']");
    }

    /**
     * Test successfully retrieving a list of user-accounts by an Author user.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas21CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_NAME + "\"=%" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String retrievedUserAccountsXml = null;

        try {
            PWCallback.setHandle(PWCallback.AUTHOR_HANDLE);
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of user accounts failed. ", e);
        }
        finally {
            PWCallback.resetHandle();
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        assertEquals("Unexpected number of user accounts.", 1, userAccountNodes.getLength());
        assertRdfDescriptions(retrievedDocument, RDF_RESOURCE_USER_ACCOUNT);

        assertXmlExists("Missing user Test Author.", retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT
            + "[1][properties/name='Test Author User']");
    }

    /**
     * Test successfully retrieving an empty list of user-accounts by an Author user that uses offset = 1.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testAARuas22CQL() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_NAME + "\"=%" });
        filterParams.put(FILTER_PARAMETER_STARTRECORD, new String[] { "2" });

        String retrievedUserAccountsXml = null;

        try {
            PWCallback.setHandle(PWCallback.AUTHOR_HANDLE);
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving list of user accounts failed. ", e);
        }
        finally {
            PWCallback.resetHandle();
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        assertEquals("Unexpected number of user accounts.", 0, userAccountNodes.getLength());
    }

    /**
     * Test successful retrieving a list of existing UserAccount resources. Test if maximumRecords=0 delivers 0
     * UserAccounts
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void emptyFilterZeroMaximumRecords() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "0" });

        String retrievedUserAccountsXml = null;

        try {
            retrievedUserAccountsXml = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of user accounts failed. ", e);
        }

        assertXmlValidSrwResponse(retrievedUserAccountsXml);
        final Document retrievedDocument = EscidocAbstractTest.getDocument(retrievedUserAccountsXml);
        final NodeList userAccountNodes = selectNodeList(retrievedDocument, XPATH_SRW_USER_ACCOUNT_LIST_USER_ACCOUNT);
        final int totalRecordsWithZeroMaximum = userAccountNodes.getLength();

        assertEquals("Unexpected number of user accounts.", totalRecordsWithZeroMaximum, 0);

    }

    /**
     * Test successfully retrieving an explain response.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void explainTest() throws Exception {
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_EXPLAIN, new String[] { "" });

        String result = null;

        try {
            result = retrieveUserAccounts(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }

    /**
     * Prepare data to test userAccountFilter with group.
     * 
     * @throws Exception
     *             e
     */
    private void prepareUserAccountGroupFilterData() throws Exception {
        // create ou and open it
        String ouXml = organizationalUnitTestBase.createSuccessfully("escidoc_ou_create.xml");
        Document createdDocument = getDocument(ouXml);
        String ouId = getObjidValue(createdDocument);
        String ouTitle = getTitleValue(createdDocument);
        String lastModDate = getLastModificationDateValue(createdDocument);
        organizationalUnitTestBase.open(ouId, "<param last-modification-date=\"" + lastModDate + "\" />");

        // create ou with parent=otherOu
        String[] parentValues = new String[2];
        parentValues[0] = ouId;
        parentValues[1] = ouTitle;

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);
        organizationalUnitTestBase.insertParentsElement(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS,
            parentValues, false);

        String toBeCreatedXml = toString(toBeCreatedDocument, false);
        ouXml = organizationalUnitTestBase.create(toBeCreatedXml);
        createdDocument = getDocument(ouXml);
        String ouId1 = getObjidValue(createdDocument);
        lastModDate = getLastModificationDateValue(createdDocument);
        organizationalUnitTestBase.open(ouId1, "<param last-modification-date=\"" + lastModDate + "\" />");

        // create user with child ou
        Document createdUser = createSuccessfully("escidoc_useraccount_for_create1.xml");
        userAccountFilterUser = getObjidValue(createdUser);
        userAttributeTestBase.createAttribute(userAccountFilterUser, "<attribute xmlns="
            + "\"http://www.escidoc.de/schemas/attributes/0.1\"" + " name=\"o\">" + ouId1 + "</attribute>");

        // create user with attribute
        String attributeName = "uafiltertestkey" + System.currentTimeMillis();
        String attributeValue = "uafiltertestvalue" + System.currentTimeMillis();
        createdUser = createSuccessfully("escidoc_useraccount_for_create.xml");
        userAccountFilterUser1 = getObjidValue(createdUser);
        userAttributeTestBase.createAttribute(userAccountFilterUser1, "<attribute xmlns="
            + "\"http://www.escidoc.de/schemas/attributes/0.1\"" + " name=\"" + attributeName + "\">" + attributeValue
            + "</attribute>");

        // create searchable users with no group
        for (int i = 0; i < additonalGroupFilterSearchUsersCount; i++) {
            toBeCreatedDocument =
                getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_useraccount_for_groupfilter_test.xml");
            uniqueIdentifier = Long.toString(System.currentTimeMillis());
            insertUserAccountValues(toBeCreatedDocument, "filtertestname_" + i + "_" + uniqueIdentifier,
                "filtertestloginname_" + i + "_" + uniqueIdentifier);
            String userXml = handleXmlResult(getClient().create(toString(toBeCreatedDocument, false)));
            additonalGroupFilterSearchUsers[i] = getObjidValue(getDocument(userXml));
        }

        // create group with attribute-user-selector
        Document createdGroup = userGroupTestBase.createSuccessfully("escidoc_usergroup_for_create.xml");
        String groupId = getObjidValue(createdGroup);
        String lastModificationDate = getLastModificationDateValue(createdGroup);
        String[] selector1 = new String[3];
        selector1[0] = attributeName;
        selector1[1] = "user-attribute";
        selector1[2] = attributeValue;
        ArrayList<String[]> selectors = new ArrayList<String[]>();
        selectors.add(selector1);
        String taskParam = userGroupTestBase.getAddSelectorsTaskParam(selectors, lastModificationDate);
        userGroupTestBase.addSelectors(groupId, taskParam);

        // create group with user, ou and group selectors
        createdGroup = userGroupTestBase.createSuccessfully("escidoc_usergroup_for_create.xml");
        userAccountFilterGroup = getObjidValue(createdGroup);
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
        selector3[2] = groupId;
        selectors = new ArrayList<String[]>();
        selectors.add(selector1);
        selectors.add(selector2);
        selectors.add(selector3);
        taskParam = userGroupTestBase.getAddSelectorsTaskParam(selectors, lastModificationDate);
        userGroupTestBase.addSelectors(userAccountFilterGroup, taskParam);

    }

    /**
     * Test declining creation of UserAccount with set attribute objid in XML data.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACua4_3_rest() throws Exception {

        Document newUserAccountDoc =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_USER_ACCOUNT_PATH, "escidoc_useraccount_for_create.xml");
        Attr newNode = createAttributeNode(newUserAccountDoc, USER_ACCOUNT_NS_URI, null, "objid", "escidoc:1");
        addAttribute(newUserAccountDoc, XPATH_USER_ACCOUNT, newNode);
        insertUniqueLoginName(newUserAccountDoc);
        final String newUserAccountXML = toString(newUserAccountDoc, false);

        try {
            create(newUserAccountXML);
            EscidocAbstractTest.failMissingException(XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test successful creation of user account with set read-only values.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAACua10_rest() throws Exception {

        Document createdDocument = createSuccessfully("escidoc_useraccount_for_create_rest_read_only.xml");

        assertEquals("Creation date and last modification date are different. ", assertCreationDateExists("",
            createdDocument), getLastModificationDateValue(createdDocument));

        final String objid = getObjidValue(createdDocument);
        Document retrievedDocument = retrieveSuccessfully(objid);
        assertXmlEquals("Retrieved document not the same like the created one", createdDocument, retrievedDocument);
    }

    /**
     * Test successful update of user account with changed read-only values.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUua7_rest() throws Exception {

        Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String createdXml = toString(createdDocument, false);

        // root attributes
        final Document toBeUpdatedDocument =
            (Document) substitute(createdDocument, XPATH_USER_ACCOUNT_XLINK_HREF, "Some Href");
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_XLINK_TITLE, "Some Title");
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_XLINK_TYPE, "simple");
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_XML_BASE, "http://some.base.uri");

        // resources
        substitute(createdDocument, XPATH_USER_ACCOUNT_RESOURCES_XLINK_HREF, "Some Href");
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_RESOURCES_XLINK_TITLE, "Some Title");
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_RESOURCES_XLINK_TYPE, "simple");

        // resources.current-grants
        substitute(createdDocument, XPATH_USER_ACCOUNT_CURRENT_GRANTS_XLINK_HREF, "Some Href");
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_CURRENT_GRANTS_XLINK_TITLE, "Some Title");
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_CURRENT_GRANTS_XLINK_TYPE, "simple");

        // creation-date
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_CREATION_DATE, getNowAsTimestamp());

        // created-by
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_CREATED_BY_XLINK_HREF, "Some Href");
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_CREATED_BY_XLINK_TITLE, "Some Title");
        // type is fixed to simple, cannot be changed

        // modified-by
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_MODIFIED_BY_XLINK_HREF, "Some Href");
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_MODIFIED_BY_XLINK_TITLE, "Some Title");

        // active
        substitute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_ACTIVE, "false");

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();

        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Updating with changed read only values failed. ", e);
        }
        final Document updatedDocument =
            assertUserAccount(updatedXml, createdXml, startTimestamp, beforeUpdateTimestamp, true);

        final Document retrievedDocument = retrieveSuccessfully(id);
        assertXmlEquals("Retrieved document not the same like the created one", updatedDocument, retrievedDocument);
    }

    /**
     * Test successful update of user account without read-only values.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUua10_rest() throws Exception {

        Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String id = getObjidValue(createdDocument);
        final String createdXml = toString(createdDocument, false);

        // root attributes
        final Document toBeUpdatedDocument = (Document) deleteAttribute(createdDocument, XPATH_USER_ACCOUNT_XLINK_HREF);
        deleteAttribute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_XLINK_TITLE);
        deleteAttribute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_XLINK_TYPE);
        deleteAttribute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_XML_BASE);

        // resources
        deleteNodes(createdDocument, XPATH_USER_ACCOUNT_RESOURCES);

        // creation-date
        deleteNodes(toBeUpdatedDocument, XPATH_USER_ACCOUNT_CREATION_DATE);

        // created-by
        deleteNodes(toBeUpdatedDocument, XPATH_USER_ACCOUNT_CREATED_BY);

        // modified-by
        deleteNodes(toBeUpdatedDocument, XPATH_USER_ACCOUNT_MODIFIED_BY);

        // active
        deleteNodes(toBeUpdatedDocument, XPATH_USER_ACCOUNT_ACTIVE);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();

        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Updating with changed read only values failed. ", e);
        }
        final Document updatedDocument =
            assertUserAccount(updatedXml, createdXml, startTimestamp, beforeUpdateTimestamp, true);

        final Document retrievedDocument = retrieveSuccessfully(id);
        assertXmlEquals("Retrieved document not the same like the created one", updatedDocument, retrievedDocument);
    }

    /**
     * Test successfully updating an UserAccount without providing modified-by xlink title.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUua11_rest() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String createdXml = toString(toBeUpdatedDocument, false);
        deleteAttribute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_MODIFIED_BY, XLINK_PREFIX_ESCIDOC + ":" + NAME_TITLE);
        final String id = getObjidValue(toBeUpdatedDocument);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();

        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Updating with changed read only values failed. ", e);
        }
        final Document updatedDocument =
            assertUserAccount(updatedXml, createdXml, startTimestamp, beforeUpdateTimestamp, true);

        Document retrievedDocument = retrieveSuccessfully(id);
        assertXmlEquals("Retrieved document not the same like the created one", updatedDocument, retrievedDocument);

    }

    /**
     * Test declining updating an UserAccount without providing modified-by xlink type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUua11_2_rest() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String createdXml = toString(toBeUpdatedDocument, false);
        deleteAttribute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_MODIFIED_BY, XLINK_PREFIX_ESCIDOC + ":" + NAME_TYPE);
        final String id = getObjidValue(toBeUpdatedDocument);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();

        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Updating with changed read only values failed. ", e);
        }
        final Document updatedDocument =
            assertUserAccount(updatedXml, createdXml, startTimestamp, beforeUpdateTimestamp, true);

        Document retrievedDocument = retrieveSuccessfully(id);
        assertXmlEquals("Retrieved document not the same like the created one", updatedDocument, retrievedDocument);

    }

    /**
     * Test declining updating an UserAccount without providing modified-by xlink href.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAAUua11_3_rest() throws Exception {

        Document toBeUpdatedDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        final String createdXml = toString(toBeUpdatedDocument, false);
        deleteAttribute(toBeUpdatedDocument, XPATH_USER_ACCOUNT_MODIFIED_BY, XLINK_PREFIX_ESCIDOC + ":" + NAME_HREF);
        final String id = getObjidValue(toBeUpdatedDocument);

        final String toBeUpdatedXml = toString(toBeUpdatedDocument, false);
        final String beforeUpdateTimestamp = getNowAsTimestamp();

        String updatedXml = null;
        try {
            updatedXml = update(id, toBeUpdatedXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Updating with changed read only values failed. ", e);
        }
        final Document updatedDocument =
            assertUserAccount(updatedXml, createdXml, startTimestamp, beforeUpdateTimestamp, true);

        Document retrievedDocument = retrieveSuccessfully(id);
        assertXmlEquals("Retrieved document not the same like the created one", updatedDocument, retrievedDocument);

    }

    /**
     * Test successful retrieval of the resources of an UserAccount.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARvr1_rest() throws Exception {

        Document createdDocument = createSuccessfully("escidoc_useraccount_for_create.xml");
        String objid = getObjidValue(createdDocument);

        String resourcesXml = null;
        try {
            resourcesXml = retrieveResources(objid);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull("No data from retrieveResources. ", resourcesXml);
        assertXmlValidUserAccount(resourcesXml);
        Document resourcesDocument = EscidocAbstractTest.getDocument(resourcesXml);
        assertXmlExists("No resources element", resourcesDocument, XPATH_RESOURCES);
        assertXmlExists("no resources xlink:type", resourcesDocument, XPATH_RESOURCES_XLINK_TYPE);
        assertXmlExists("no resources xlink:title", resourcesDocument, XPATH_RESOURCES_XLINK_TITLE);
        assertXmlExists("no resources xlink:href", resourcesDocument, XPATH_RESOURCES_XLINK_HREF);
        assertXmlExists("no resources xml:base", resourcesDocument, XPATH_RESOURCES_BASE);

        assertXmlExists("No current grants resource element", resourcesDocument, XPATH_RESOURCES_CURRENT_GRANTS);
        assertXmlEquals("", resourcesDocument, XPATH_RESOURCES_CURRENT_GRANTS_XLINK_TITLE, "Current Grants");
        assertXmlExists("no current grants xlink:type", resourcesDocument, XPATH_RESOURCES_CURRENT_GRANTS_XLINK_TYPE);
        assertXmlExists("no current grants xlink:href", resourcesDocument, XPATH_RESOURCES_CURRENT_GRANTS_XLINK_HREF);

        Node resourcesNode = selectSingleNode(resourcesDocument, XPATH_RESOURCES);
        final NodeList childNodes = resourcesNode.getChildNodes();
        // expected nodes = 3: text: current-grants, preferences, attributes
        assertEquals("Unexpected number of children of resources, ", 4, childNodes.getLength());
    }

    /**
     * Test declining the retrieval of the resources of an UserAccount with unknown id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARvr2_rest() throws Exception {

        try {
            retrieveResources(UNKNOWN_ID);
            EscidocAbstractTest.failMissingException(UserAccountNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", UserAccountNotFoundException.class, e);
        }
    }

    /**
     * Test declining the retrieval of the resources of an UserAccount with id of an existing object of another resource
     * type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARvr2_2_rest() throws Exception {

        try {
            retrieveResources(CONTEXT_ID);
            EscidocAbstractTest.failMissingException(UserAccountNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", UserAccountNotFoundException.class, e);
        }
    }

    /**
     * Test declining the retrieval of the resources of an UserAccount with missing parameter id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAARvr3_rest() throws Exception {
        try {
            retrieveResources(null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Wrong exception. ", MissingMethodParameterException.class, e);
        }
    }
}
