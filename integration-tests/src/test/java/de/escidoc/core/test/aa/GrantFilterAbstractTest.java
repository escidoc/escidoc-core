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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.system.SqlDatabaseSystemException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.EscidocTestsBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.aa.GrantClient;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test suite for the GrantFilter .
 * 
 * @author MIH
 * 
 */
public class GrantFilterAbstractTest extends GrantTestBase {

    private static UserAttributeTestBase userAttributeTestBase = null;

    private static final int REVOKED_FROM_PARTTIME_GRANTS = 40;

    private static final int REVOKED_TO_PARTTIME_GRANTS = 60;

    private static final int REPLACEMENT_INT = 123456789;

    private static final int OBJECT_REVOCATION_FIRSTPART_GRANT_COUNT = 12;

    private static final int USER_OBJECT_ROLE_FIRSTPART_GRANT_COUNT = 1;

    private static final int USER_OBJECT_FIRSTPART_GRANT_COUNT = 3;

    private static final int GROUP_OBJECT_ROLE_GRANT_COUNT = 1;

    private static final int GROUP_OBJECT_GRANT_COUNT = 5;

    private static final int REVOKER_OBJECT_GRANT_COUNT = 10;

    private static final int CREATORS_OBJECTS_GRANT_COUNT = 70;

    private static final int CREATOR_OBJECT_GRANT_COUNT = 20;

    private static final int USER_OBJECT_ROLE_GRANT_COUNT = 1;

    private static final int USER_OBJECT_GRANT_COUNT = 5;

    private static final int TOTAL_GRANT_COUNT = 175;

    private static final int REVOKED_GRANT_COUNT = 100;

    private static final int NON_REVOKED_GRANT_COUNT = 75;

    private static final int OBJECT_GRANT_COUNT = 35;

    private static final int ROLE_GRANT_COUNT = 35;

    private static final int GROUP_GRANT_COUNT = 25;

    private static final int USER_GRANT_COUNT = 25;

    private static int methodCounter = 0;

    private static final String SYSADMIN_PASSWORD = "passwd";

    private static final int NUM_SYSADMINS = 2;

    private static final int NUM_USERS = 4;

    private static final int NUM_CONTAINERS = 2;

    private static final int NUM_ITEMS = 2;

    private static String startTime = "";

    private static final int PART_TIME_ROLE_NUMBER = 3;

    public static final String XPATH_SRW_GRANT_LIST_GRANT =
        XPATH_SRW_RESPONSE_RECORD + "/recordData/" + NAME_GRANT;

    private static HashMap<String, String> partTimeParameters =
        new HashMap<String, String>();

    private static ArrayList<HashMap<String, String>> creatorSysadmins =
        new ArrayList<HashMap<String, String>>();

    private static ArrayList<HashMap<String, String>> revokerSysadmins =
        new ArrayList<HashMap<String, String>>();

    private static ArrayList<String> userIds = new ArrayList<String>();

    private static final ArrayList<String> GROUP_IDS = new ArrayList<String>() {

        private static final long serialVersionUID = 7384615734214431586L;

        {
            add(USER_GROUP_WITH_GROUP_LIST_ID);
            add(USER_GROUP_WITH_OU_LIST_ID);
            add(USER_GROUP_WITH_USER_LIST_ID);
        }
    };

    private static final ArrayList<String> ROLES = new ArrayList<String>() {

        private static final long serialVersionUID = -5711772890758441804L;

        {
            add(ROLE_HREF_GRANT_TEST_1);
            add(ROLE_HREF_GRANT_TEST_2);
            add(ROLE_HREF_GRANT_TEST_3);
            add(ROLE_HREF_GRANT_TEST_4);
            add(ROLE_HREF_GRANT_TEST_5);
        }
    };

    private static ArrayList<String> objects = new ArrayList<String>() {

        private static final long serialVersionUID = -4200740733641855204L;

        {
            add(Constants.CONTEXT_BASE_URI + "/" + CONTEXT_ID);
        }
    };

    /**
     * The constructor.
     * 
     * @param transport
     *            The transport identifier.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public GrantFilterAbstractTest(final int transport) throws Exception {
        super(transport, USER_ACCOUNT_HANDLER_CODE);
        userAttributeTestBase = new UserAttributeTestBase(transport);
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
            prepare();
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
     * insert grants into system for the tests.
     * 
     * @test.name prepare
     * @test.id PREPARE
     * @test.input
     * @test.inputDescription
     * @test.expected
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    private void prepare() throws Exception {
        try {
            setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
            for (String groupId : GROUP_IDS) {
                revokeAllGrants(groupId);
            }
            setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));

            // create users(handles) with systemadministrator-rights
            creatorSysadmins = createSysadmins(NUM_SYSADMINS);
            revokerSysadmins = createSysadmins(NUM_SYSADMINS);

            // create containers
            for (int i = 0; i < NUM_CONTAINERS; i++) {
                final String createdXml =
                    doTestCreateContainer(PWCallback.DEFAULT_HANDLE, null);
                objects.add(Constants.CONTAINER_BASE_URI + "/"
                    + getObjidValue(createdXml));
            }

            // create items
            for (int i = 0; i < NUM_ITEMS; i++) {
                final String createdXml =
                    doTestCreateItem(PWCallback.DEFAULT_HANDLE, null);
                objects.add(Constants.ITEM_BASE_URI + "/"
                    + getObjidValue(createdXml));
            }

            // create users
            for (int i = 0; i < NUM_USERS; i++) {
                Document user =
                    createSuccessfully("escidoc_useraccount_for_create1.xml");
                String userId = getObjidValue(getTransport(), user);
                userAttributeTestBase.createAttribute(
                        userId, "<attribute xmlns="
                        + "\"http://www.escidoc.de/schemas/attributes/0.1\""
                        + " name=\"o\">"
                        + EscidocTestsBase.ORGANIZATIONAL_UNIT_ID1 
                        + "</attribute>");
                userIds.add(userId);
            }

            startTime = new DateTime(System.currentTimeMillis()).toString();

            // create different roles for each user and group
            int roleCounter = 0;
            for (String role : ROLES) {
                if (roleCounter == PART_TIME_ROLE_NUMBER) {
                    partTimeParameters.put("time", new DateTime(System
                        .currentTimeMillis()).toString());
                    System.out.println(partTimeParameters.get("time"));
                }
                roleCounter++;
                for (String object : objects) {
                    String createHandle = creatorSysadmins.get(0).get("handle");
                    String revokeHandle = revokerSysadmins.get(0).get("handle");
                    int i = 0;
                    String grantXml;
                    setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
                    for (String userId : userIds) {
                        grantXml =
                            doTestCreateGrant(createHandle, userId, object,
                                role, null);
                        if (creatorSysadmins.get(0).get("grantCount") == null) {
                            creatorSysadmins.get(0).put("grantCount", "0");
                        }
                        int grantCount =
                            Integer.parseInt(creatorSysadmins.get(0).get(
                                "grantCount")) + 1;
                        creatorSysadmins.get(0).put("grantCount",
                            Integer.toString(grantCount));
                        if (i % 2 == 0) {
                            Document createdDocument =
                                EscidocRestSoapTestsBase.getDocument(grantXml);
                            String grantId = getObjidValue(createdDocument);
                            String lastModificationDate =
                                getLastModificationDateValue(createdDocument);
                            String taskParamXML =
                                "<param last-modification-date=\""
                                    + lastModificationDate + "\" />";
                            revokeGrant(userId, grantId, taskParamXML,
                                revokeHandle);
                            if (revokerSysadmins.get(0).get("grantCount") == null) {
                                revokerSysadmins.get(0).put("grantCount", "0");
                            }
                            grantCount =
                                Integer.parseInt(revokerSysadmins.get(0).get(
                                    "grantCount")) + 1;
                            revokerSysadmins.get(0).put("grantCount",
                                Integer.toString(grantCount));
                        }
                        i++;
                    }
                    createHandle = creatorSysadmins.get(1).get("handle");
                    revokeHandle = revokerSysadmins.get(1).get("handle");
                    setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
                    for (String groupId : GROUP_IDS) {
                        grantXml =
                            doTestCreateGrant(createHandle, groupId, object,
                                role, null);
                        if (creatorSysadmins.get(1).get("grantCount") == null) {
                            creatorSysadmins.get(1).put("grantCount", "0");
                        }
                        int grantCount =
                            Integer.parseInt(creatorSysadmins.get(1).get(
                                "grantCount")) + 1;
                        creatorSysadmins.get(1).put("grantCount",
                            Integer.toString(grantCount));
                        if (i % 2 == 0) {
                            Document createdDocument =
                                EscidocRestSoapTestsBase.getDocument(grantXml);
                            String grantId = getObjidValue(createdDocument);
                            String lastModificationDate =
                                getLastModificationDateValue(createdDocument);
                            String taskParamXML =
                                "<param last-modification-date=\""
                                    + lastModificationDate + "\" />";
                            revokeGrant(groupId, grantId, taskParamXML,
                                revokeHandle);
                            if (revokerSysadmins.get(1).get("grantCount") == null) {
                                revokerSysadmins.get(1).put("grantCount", "0");
                            }
                            grantCount =
                                Integer.parseInt(revokerSysadmins.get(1).get(
                                    "grantCount")) + 1;
                            revokerSysadmins.get(1).put("grantCount",
                                Integer.toString(grantCount));
                        }
                        i++;
                    }
                }
            }
        }
        finally {
            setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
        }

    }

    /**
     * Test successfully filtering grants for a userId.
     * 
     * @test.name userIdFilter
     * @test.id userIdFilter
     * @test.input UserAccount id
     * @test.inputDescription: <ul>
     *                         <li>existing user-id</li>
     *                         </ul>
     * @test.expected: result with all grants of the user.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdFilter() throws Exception {
        int expectedGrantCount = USER_GRANT_COUNT * 3;
        for (String userId : userIds) {
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(userId);
            allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
            allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
            String filterXml =
                "<param>" + getFilter(FILTER_USER, userId)
                    + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                    + "</param>";
            String result = null;
            try {
                result = retrieveGrants(filterXml);
            }
            catch (Exception e) {
                EscidocRestSoapTestsBase.failException(e);
            }
            assertXmlValidGrantList(result);
            assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
            assertAllowedXpathValues(result,
                "/grant-list/grant/properties/granted-to", allowedValues, true);
        }

    }

    /**
     * Test successfully filtering grants for a userId.
     * 
     * @test.name userIdFilter
     * @test.id userIdFilter
     * @test.input UserAccount id
     * @test.inputDescription: <ul>
     *                         <li>existing user-id</li>
     *                         </ul>
     * @test.expected: result with all grants of the user.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdFilterCQL() throws Exception {
        int expectedGrantCount = USER_GRANT_COUNT * 3;
        for (String userId : userIds) {
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(userId);
            allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
            allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);

            final Map<String, String[]> filterParams =
                new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
                + FILTER_USER + "\"=" + userId + " and " + "\""
                + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

            String result = null;

            try {
                result = retrieveGrants(filterParams);
            }
            catch (Exception e) {
                EscidocRestSoapTestsBase.failException(e);
            }
            assertXmlValidSrwResponse(result);
            assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT,
                expectedGrantCount);
            assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
                + "/properties/granted-to", allowedValues, true);
        }
    }

    /**
     * Test successfully filtering grants for several userIds.
     * 
     * @test.name userIdsFilter
     * @test.id userIdsFilter
     * @test.input UserAccount ids
     * @test.inputDescription: <ul>
     *                         <li>existing user-ids</li>
     *                         </ul>
     * @test.expected: result with all grants of the users.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdsFilter() throws Exception {
        int expectedGrantCount = USER_GRANT_COUNT * 3 + 25;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        String userId1 = userIdIter.next();
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(userId1);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        String filterXml =
            "<param>" + getFilter(FILTER_USER, userId)
                + getFilter(FILTER_USER, userId1)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);

    }

    /**
     * Test successfully filtering grants for several userIds.
     * 
     * @test.name userIdsFilter
     * @test.id userIdsFilter
     * @test.input UserAccount ids
     * @test.inputDescription: <ul>
     *                         <li>existing user-ids</li>
     *                         </ul>
     * @test.expected: result with all grants of the users.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdsFilterCQL() throws Exception {
        int expectedGrantCount = USER_GRANT_COUNT * 3 + 25;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        String userId1 = userIdIter.next();
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(userId1);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_USER + "\"=" + userId + " or " + "\"" + FILTER_USER
            + "\"=" + userId1 + ") and " + "\"" + FILTER_CREATION_DATE
            + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for a groupId.
     * 
     * @test.name groupIdFilter
     * @test.id groupIdFilter
     * @test.input UserGroup id
     * @test.inputDescription: <ul>
     *                         <li>existing group-id</li>
     *                         </ul>
     * @test.expected: result with all grants of the group.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdFilter() throws Exception {
        int expectedGrantCount = GROUP_GRANT_COUNT;
        for (String groupId : GROUP_IDS) {
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(groupId);
            allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
            String filterXml =
                "<param>" + getFilter(FILTER_GROUP, groupId)
                    + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                    + "</param>";
            String result = null;
            try {
                result = retrieveGrants(filterXml);
            }
            catch (Exception e) {
                EscidocRestSoapTestsBase.failException(e);
            }
            assertXmlValidGrantList(result);
            if (groupId.equals(USER_GROUP_WITH_OU_LIST_ID)) {
                assertNodeCount(result, "/grant-list/grant",
                    expectedGrantCount * 2);
            }
            else {
                assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
            }
            assertAllowedXpathValues(result,
                "/grant-list/grant/properties/granted-to", allowedValues, true);
        }

    }

    /**
     * Test successfully filtering grants for a groupId.
     * 
     * @test.name groupIdFilter
     * @test.id groupIdFilter
     * @test.input UserGroup id
     * @test.inputDescription: <ul>
     *                         <li>existing group-id</li>
     *                         </ul>
     * @test.expected: result with all grants of the group.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdFilterCQL() throws Exception {
        int expectedGrantCount = GROUP_GRANT_COUNT;
        for (String groupId : GROUP_IDS) {
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(groupId);
            allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);

            final Map<String, String[]> filterParams =
                new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
                + FILTER_GROUP + "\"=" + groupId + " and " + "\""
                + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

            String result = null;

            try {
                result = retrieveGrants(filterParams);
            }
            catch (Exception e) {
                EscidocRestSoapTestsBase.failException(e);
            }
            assertXmlValidSrwResponse(result);
            if (groupId.equals(USER_GROUP_WITH_OU_LIST_ID)) {
                assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT,
                    expectedGrantCount * 2);
            } else {
                assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT,
                    expectedGrantCount);
            }
            assertAllowedXpathValues(result,
                XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to",
                allowedValues, true);
        }
    }

    /**
     * Test successfully filtering grants for several groupIds.
     * 
     * @test.name groupIdsFilter
     * @test.id groupIdsFilter
     * @test.input UserGroup ids
     * @test.inputDescription: <ul>
     *                         <li>existing group-ids</li>
     *                         </ul>
     * @test.expected: result with all grants of the groups.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdsFilter() throws Exception {
        int expectedGrantCount = GROUP_GRANT_COUNT * 2;
        Iterator<String> groupIdIter = GROUP_IDS.iterator();
        String groupId = groupIdIter.next();
        String groupId1 = groupIdIter.next();
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(groupId);
        allowedValues.add(groupId1);
        String filterXml =
            "<param>" + getFilter(FILTER_GROUP, groupId)
                + getFilter(FILTER_GROUP, groupId1)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);

    }

    /**
     * Test successfully filtering grants for several groupIds.
     * 
     * @test.name groupIdsFilter
     * @test.id groupIdsFilter
     * @test.input UserGroup ids
     * @test.inputDescription: <ul>
     *                         <li>existing group-ids</li>
     *                         </ul>
     * @test.expected: result with all grants of the groups.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdsFilterCQL() throws Exception {
        int expectedGrantCount = GROUP_GRANT_COUNT * 2;
        Iterator<String> groupIdIter = GROUP_IDS.iterator();
        String groupId = groupIdIter.next();
        String groupId1 = groupIdIter.next();
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(groupId);
        allowedValues.add(groupId1);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_GROUP + "\"=" + groupId + " or " + "\"" + FILTER_GROUP
            + "\"=" + groupId1 + ") and " + "\"" + FILTER_CREATION_DATE
            + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for a roleId.
     * 
     * @test.name roleIdFilter
     * @test.id roleIdFilter
     * @test.input role id
     * @test.inputDescription: <ul>
     *                         <li>existing role-id</li>
     *                         </ul>
     * @test.expected: result with all grants of the role.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void roleIdFilter() throws Exception {
        int expectedGrantCount = ROLE_GRANT_COUNT;
        for (String roleId : ROLES) {
            roleId = getObjidFromHref(roleId);
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(roleId);
            String filterXml =
                "<param>" + getFilter(FILTER_ROLE, roleId)
                    + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                    + "</param>";
            String result = null;
            try {
                result = retrieveGrants(filterXml);
            }
            catch (Exception e) {
                EscidocRestSoapTestsBase.failException(e);
            }
            assertXmlValidGrantList(result);
            assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
            assertAllowedXpathValues(result,
                "/grant-list/grant/properties/role", allowedValues, true);
        }

    }

    /**
     * Test successfully filtering grants for a roleId.
     * 
     * @test.name roleIdFilter
     * @test.id roleIdFilter
     * @test.input role id
     * @test.inputDescription: <ul>
     *                         <li>existing role-id</li>
     *                         </ul>
     * @test.expected: result with all grants of the role.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void roleIdFilterCQL() throws Exception {
        int expectedGrantCount = ROLE_GRANT_COUNT;
        for (String roleId : ROLES) {
            roleId = getObjidFromHref(roleId);
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(roleId);

            final Map<String, String[]> filterParams =
                new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
                + FILTER_ROLE + "\"=" + roleId + " and " + "\""
                + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

            String result = null;

            try {
                result = retrieveGrants(filterParams);
            }
            catch (Exception e) {
                EscidocRestSoapTestsBase.failException(e);
            }
            assertXmlValidSrwResponse(result);
            assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT,
                expectedGrantCount);
            assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
                + "/properties/role", allowedValues, true);
        }
    }

    /**
     * Test successfully filtering grants for several roleIds.
     * 
     * @test.name roleIdsFilter
     * @test.id roleIdsFilter
     * @test.input role ids
     * @test.inputDescription: <ul>
     *                         <li>existing role-ids</li>
     *                         </ul>
     * @test.expected: result with all grants of the roles.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void roleIdsFilter() throws Exception {
        int expectedGrantCount = ROLE_GRANT_COUNT * 2;
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        String roleId1 = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(roleId);
        allowedValues.add(roleId1);
        String filterXml =
            "<param>" + getFilter(FILTER_ROLE, roleId)
                + getFilter(FILTER_ROLE, roleId1)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result, "/grant-list/grant/properties/role",
            allowedValues, true);

    }

    /**
     * Test successfully filtering grants for several roleIds.
     * 
     * @test.name roleIdsFilter
     * @test.id roleIdsFilter
     * @test.input role ids
     * @test.inputDescription: <ul>
     *                         <li>existing role-ids</li>
     *                         </ul>
     * @test.expected: result with all grants of the roles.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void roleIdsFilterCQL() throws Exception {
        int expectedGrantCount = ROLE_GRANT_COUNT * 2;
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        String roleId1 = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(roleId);
        allowedValues.add(roleId1);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_ROLE + "\"=" + roleId + " or " + "\"" + FILTER_ROLE
            + "\"=" + roleId1 + ") and " + "\"" + FILTER_CREATION_DATE
            + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for a objectId.
     * 
     * @test.name objectIdFilter
     * @test.id objectIdFilter
     * @test.input object id
     * @test.inputDescription: <ul>
     *                         <li>existing object-id</li>
     *                         </ul>
     * @test.expected: result with all grants of the object.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void objectIdFilter() throws Exception {
        int expectedGrantCount = OBJECT_GRANT_COUNT;
        for (String objectId : objects) {
            objectId = getObjidFromHref(objectId);
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(objectId);
            String filterXml =
                "<param>" + getFilter(FILTER_ASSIGNED_ON, objectId)
                    + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                    + "</param>";
            String result = null;
            try {
                result = retrieveGrants(filterXml);
            }
            catch (Exception e) {
                EscidocRestSoapTestsBase.failException(e);
            }
            assertXmlValidGrantList(result);
            assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
            assertAllowedXpathValues(result,
                "/grant-list/grant/properties/assigned-on", allowedValues, true);
        }

    }

    /**
     * Test successfully filtering grants for a objectId.
     * 
     * @test.name objectIdFilter
     * @test.id objectIdFilter
     * @test.input object id
     * @test.inputDescription: <ul>
     *                         <li>existing object-id</li>
     *                         </ul>
     * @test.expected: result with all grants of the object.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void objectIdFilterCQL() throws Exception {
        int expectedGrantCount = OBJECT_GRANT_COUNT;
        for (String objectId : objects) {
            objectId = getObjidFromHref(objectId);
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(objectId);

            final Map<String, String[]> filterParams =
                new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
                + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\""
                + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

            String result = null;

            try {
                result = retrieveGrants(filterParams);
            }
            catch (Exception e) {
                EscidocRestSoapTestsBase.failException(e);
            }
            assertXmlValidSrwResponse(result);
            assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT,
                expectedGrantCount);
            assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
                + "/properties/assigned-on", allowedValues, true);
        }
    }

    /**
     * Test successfully filtering grants for several objectIds.
     * 
     * @test.name objectIdsFilter
     * @test.id objectIdsFilter
     * @test.input object ids
     * @test.inputDescription: <ul>
     *                         <li>existing object-ids</li>
     *                         </ul>
     * @test.expected: result with all grants of the objects.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void objectIdsFilter() throws Exception {
        int expectedGrantCount = OBJECT_GRANT_COUNT * 2;
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(objectId);
        allowedValues.add(objectId1);
        String filterXml =
            "<param>" + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_ASSIGNED_ON, objectId1)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);

    }

    /**
     * Test successfully filtering grants for several objectIds.
     * 
     * @test.name objectIdsFilter
     * @test.id objectIdsFilter
     * @test.input object ids
     * @test.inputDescription: <ul>
     *                         <li>existing object-ids</li>
     *                         </ul>
     * @test.expected: result with all grants of the objects.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void objectIdsFilterCQL() throws Exception {
        int expectedGrantCount = OBJECT_GRANT_COUNT * 2;
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(objectId);
        allowedValues.add(objectId1);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " or " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId1 + ") and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revocationDateFrom.
     * 
     * @test.name revocationDateFromFilter
     * @test.id revocationDateFromFilter
     * @test.input revocationDateFrom
     * @test.inputDescription: <ul>
     *                         <li>revocationDateFrom</li>
     *                         </ul>
     * @test.expected: result with all grants having revocationDate >
     *                 revocationDateFrom.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateFromFilter() throws Exception {
        int expectedGrantCount = REVOKED_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_REVOCATION_DATE_FROM, startTime)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);

    }

    /**
     * Test successfully filtering grants for revocationDateFrom.
     * 
     * @test.name revocationDateFromFilter
     * @test.id revocationDateFromFilter
     * @test.input revocationDateFrom
     * @test.inputDescription: <ul>
     *                         <li>revocationDateFrom</li>
     *                         </ul>
     * @test.expected: result with all grants having revocationDate >
     *                 revocationDateFrom.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateFromFilterCQL() throws Exception {
        int expectedGrantCount = REVOKED_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_REVOCATION_DATE + "\">=\"" + startTime + "\" and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for revocationDateFrom.
     * 
     * @test.name revocationDateFromFilter1
     * @test.id revocationDateFromFilter1
     * @test.input revocationDateFrom
     * @test.inputDescription: <ul>
     *                         <li>revocationDateFrom</li>
     *                         </ul>
     * @test.expected: result with all grants having revocationDate >
     *                 revocationDateFrom.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateFromFilter1() throws Exception {
        int expectedGrantCount = 0;
        String filterXml =
            "<param>"
                + getFilter(FILTER_REVOCATION_DATE_FROM, new DateTime(System
                    .currentTimeMillis()).toString())
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);

    }

    /**
     * Test successfully filtering grants for revocationDateFrom.
     * 
     * @test.name revocationDateFromFilter1
     * @test.id revocationDateFromFilter1
     * @test.input revocationDateFrom
     * @test.inputDescription: <ul>
     *                         <li>revocationDateFrom</li>
     *                         </ul>
     * @test.expected: result with all grants having revocationDate >
     *                 revocationDateFrom.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateFromFilter1CQL() throws Exception {
        int expectedGrantCount = 0;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_REVOCATION_DATE + "\">=\""
            + new DateTime(System.currentTimeMillis()).toString() + "\" and "
            + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for revocationDateTo.
     * 
     * @test.name revocationDateToFilter
     * @test.id revocationDateToFilter
     * @test.input revocationDateTo
     * @test.inputDescription: <ul>
     *                         <li>revocationDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having revocationDate &lt;
     *                 revocationDateTo.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateToFilter() throws Exception {
        int expectedGrantCount = 0;
        String filterXml =
            "<param>" + getFilter(FILTER_REVOCATION_DATE_TO, startTime)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);

    }

    /**
     * Test successfully filtering grants for revocationDateTo.
     * 
     * @test.name revocationDateToFilter
     * @test.id revocationDateToFilter
     * @test.input revocationDateTo
     * @test.inputDescription: <ul>
     *                         <li>revocationDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having revocationDate &lt;
     *                 revocationDateTo.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateToFilterCQL() throws Exception {
        int expectedGrantCount = 0;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_REVOCATION_DATE + "\"<=\"" + startTime + "\" and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for revocationDateTo.
     * 
     * @test.name revocationDateToFilter
     * @test.id revocationDateToFilter
     * @test.input revocationDateTo
     * @test.inputDescription: <ul>
     *                         <li>revocationDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having revocationDate &lt;
     *                 revocationDateTo.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateToFilter1() throws Exception {
        int expectedGrantCount = REVOKED_GRANT_COUNT;
        String filterXml =
            "<param>"
                + getFilter(FILTER_REVOCATION_DATE_TO, new DateTime(System
                    .currentTimeMillis()).toString())
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);

    }

    /**
     * Test successfully filtering grants for revocationDateTo.
     * 
     * @test.name revocationDateToFilter
     * @test.id revocationDateToFilter
     * @test.input revocationDateTo
     * @test.inputDescription: <ul>
     *                         <li>revocationDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having revocationDate &lt;
     *                 revocationDateTo.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateToFilter1CQL() throws Exception {
        int expectedGrantCount = REVOKED_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_REVOCATION_DATE + "\"<=\""
            + new DateTime(System.currentTimeMillis()).toString() + "\" and "
            + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for grantedDateFrom.
     * 
     * @test.name grantedDateFromFilter
     * @test.id grantedDateFromFilter
     * @test.input grantedDateFrom
     * @test.inputDescription: <ul>
     *                         <li>grantedDateFrom</li>
     *                         </ul>
     * @test.expected: result with all grants having grantedDate >
     *                 grantedDateFrom.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateFromFilter() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);

    }

    /**
     * Test successfully filtering grants for grantedDateFrom.
     * 
     * @test.name grantedDateFromFilter
     * @test.id grantedDateFromFilter
     * @test.input grantedDateFrom
     * @test.inputDescription: <ul>
     *                         <li>grantedDateFrom</li>
     *                         </ul>
     * @test.expected: result with all grants having grantedDate >
     *                 grantedDateFrom.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateFromFilterCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for grantedDateFrom.
     * 
     * @test.name grantedDateFromFilter1
     * @test.id grantedDateFromFilter1
     * @test.input grantedDateFrom
     * @test.inputDescription: <ul>
     *                         <li>grantedDateFrom</li>
     *                         </ul>
     * @test.expected: result with all grants having grantedDate >
     *                 grantedDateFrom.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateFromFilter1() throws Exception {
        int expectedGrantCount = 0;
        String filterXml =
            "<param>"
                + getFilter(FILTER_CREATION_DATE_FROM, new DateTime(System
                    .currentTimeMillis()).toString()) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);

    }

    /**
     * Test successfully filtering grants for grantedDateFrom.
     * 
     * @test.name grantedDateFromFilter1
     * @test.id grantedDateFromFilter1
     * @test.input grantedDateFrom
     * @test.inputDescription: <ul>
     *                         <li>grantedDateFrom</li>
     *                         </ul>
     * @test.expected: result with all grants having grantedDate >
     *                 grantedDateFrom.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateFromFilter1CQL() throws Exception {
        int expectedGrantCount = 0;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\""
            + new DateTime(System.currentTimeMillis()).toString() + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for grantedDateTo.
     * 
     * @test.name grantedDateToFilter
     * @test.id grantedDateToFilter
     * @test.input grantedDateTo
     * @test.inputDescription: <ul>
     *                         <li>grantedDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having grantedDate &lt;
     *                 grantedDateTo.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateToFilter() throws Exception {
        int expectedGrantCount = 0;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_TO, startTime)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);

    }

    /**
     * Test successfully filtering grants for grantedDateTo.
     * 
     * @test.name grantedDateToFilter
     * @test.id grantedDateToFilter
     * @test.input grantedDateTo
     * @test.inputDescription: <ul>
     *                         <li>grantedDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having grantedDate &lt;
     *                 grantedDateTo.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateToFilterCQL() throws Exception {
        int expectedGrantCount = 0;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\"<=\"" + startTime + "\" and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for grantedDateTo.
     * 
     * @test.name grantedDateToFilter1
     * @test.id grantedDateToFilter1
     * @test.input grantedDateTo
     * @test.inputDescription: <ul>
     *                         <li>grantedDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having grantedDate &lt;
     *                 grantedDateTo.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateToFilter1() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>"
                + getFilter(FILTER_CREATION_DATE_TO, new DateTime(System
                    .currentTimeMillis()).toString())
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);

    }

    /**
     * Test successfully filtering grants for grantedDateTo.
     * 
     * @test.name grantedDateToFilter1
     * @test.id grantedDateToFilter1
     * @test.input grantedDateTo
     * @test.inputDescription: <ul>
     *                         <li>grantedDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having grantedDate &lt;
     *                 grantedDateTo.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateToFilter1CQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\"<=\""
            + new DateTime(System.currentTimeMillis()).toString() + "\" and "
            + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for creatorId.
     * 
     * @test.name creatorIdFilter
     * @test.id creatorIdFilter
     * @test.input creatorId
     * @test.inputDescription: <ul>
     *                         <li>creatorId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided creatorId.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void creatorIdFilter() throws Exception {
        int expectedGrantCount =
            Integer.parseInt(creatorSysadmins.get(0).get("grantCount"));
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(creatorSysadmins.get(0).get("userId"));
        String filterXml =
            "<param>"
                + getFilter(FILTER_CREATED_BY, creatorSysadmins.get(0).get(
                    "userId"))
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/created-by", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for creatorId.
     * 
     * @test.name creatorIdFilter
     * @test.id creatorIdFilter
     * @test.input creatorId
     * @test.inputDescription: <ul>
     *                         <li>creatorId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided creatorId.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void creatorIdFilterCQL() throws Exception {
        int expectedGrantCount =
            Integer.parseInt(creatorSysadmins.get(0).get("grantCount"));
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(creatorSysadmins.get(0).get("userId"));

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATED_BY + "\"=" + creatorSysadmins.get(0).get("userId")
            + " and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/created-by", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for creatorIds.
     * 
     * @test.name creatorIdsFilter
     * @test.id creatorIdsFilter
     * @test.input creatorIds
     * @test.inputDescription: <ul>
     *                         <li>creatorIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided creatorIds.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void creatorIdsFilter() throws Exception {
        int expectedGrantCount =
            Integer.parseInt(creatorSysadmins.get(0).get("grantCount"))
                + Integer.parseInt(creatorSysadmins.get(1).get("grantCount"));
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(creatorSysadmins.get(0).get("userId"));
        allowedValues.add(creatorSysadmins.get(1).get("userId"));
        String filterXml =
            "<param>"
                + getFilter(FILTER_CREATED_BY, creatorSysadmins.get(0).get(
                    "userId"))
                + getFilter(FILTER_CREATED_BY, creatorSysadmins.get(1).get(
                    "userId"))
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/created-by", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for creatorIds.
     * 
     * @test.name creatorIdsFilter
     * @test.id creatorIdsFilter
     * @test.input creatorIds
     * @test.inputDescription: <ul>
     *                         <li>creatorIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided creatorIds.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void creatorIdsFilterCQL() throws Exception {
        int expectedGrantCount =
            Integer.parseInt(creatorSysadmins.get(0).get("grantCount"))
                + Integer.parseInt(creatorSysadmins.get(1).get("grantCount"));
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(creatorSysadmins.get(0).get("userId"));
        allowedValues.add(creatorSysadmins.get(1).get("userId"));

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_CREATED_BY + "\"=" + creatorSysadmins.get(0).get("userId")
            + " or " + "\"" + FILTER_CREATED_BY + "\"="
            + creatorSysadmins.get(1).get("userId") + ") and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/created-by", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revokerId.
     * 
     * @test.name revokerIdFilter
     * @test.id revokerIdFilter
     * @test.input revokerId
     * @test.inputDescription: <ul>
     *                         <li>revokerId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided revokerId.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revokerIdFilter() throws Exception {
        int expectedGrantCount =
            Integer.parseInt(revokerSysadmins.get(0).get("grantCount"));
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(revokerSysadmins.get(0).get("userId"));
        String filterXml =
            "<param>"
                + getFilter(FILTER_REVOKED_BY, revokerSysadmins.get(0).get(
                    "userId"))
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/revoked-by", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revokerId.
     * 
     * @test.name revokerIdFilter
     * @test.id revokerIdFilter
     * @test.input revokerId
     * @test.inputDescription: <ul>
     *                         <li>revokerId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided revokerId.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revokerIdFilterCQL() throws Exception {
        int expectedGrantCount =
            Integer.parseInt(revokerSysadmins.get(0).get("grantCount"));
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(revokerSysadmins.get(0).get("userId"));
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_REVOKED_BY + "\"=" + revokerSysadmins.get(0).get("userId")
            + " and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/revoked-by", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revokerIds.
     * 
     * @test.name revokerIdsFilter
     * @test.id revokerIdsFilter
     * @test.input revokerIds
     * @test.inputDescription: <ul>
     *                         <li>revokerIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided revokerIds.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revokerIdsFilter() throws Exception {
        int expectedGrantCount =
            Integer.parseInt(revokerSysadmins.get(0).get("grantCount"))
                + Integer.parseInt(revokerSysadmins.get(1).get("grantCount"));
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(revokerSysadmins.get(0).get("userId"));
        allowedValues.add(revokerSysadmins.get(1).get("userId"));
        String filterXml =
            "<param>"
                + getFilter(FILTER_REVOKED_BY, revokerSysadmins.get(0).get(
                    "userId"))
                + getFilter(FILTER_REVOKED_BY, revokerSysadmins.get(1).get(
                    "userId"))
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/revoked-by", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revokerIds.
     * 
     * @test.name revokerIdsFilter
     * @test.id revokerIdsFilter
     * @test.input revokerIds
     * @test.inputDescription: <ul>
     *                         <li>revokerIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided revokerIds.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revokerIdsFilterCQL() throws Exception {
        int expectedGrantCount =
            Integer.parseInt(revokerSysadmins.get(0).get("grantCount"))
                + Integer.parseInt(revokerSysadmins.get(1).get("grantCount"));
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(revokerSysadmins.get(0).get("userId"));
        allowedValues.add(revokerSysadmins.get(1).get("userId"));

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_REVOKED_BY + "\"=" + revokerSysadmins.get(0).get("userId")
            + " or " + "\"" + FILTER_REVOKED_BY + "\"="
            + revokerSysadmins.get(1).get("userId") + ") and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/revoked-by", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userId and objectId.
     * 
     * @test.name userIdObjectIdFilter
     * @test.id userIdObjectIdFilter
     * @test.input userIdObjectIdFilter
     * @test.inputDescription: <ul>
     *                         <li>userId</li>
     *                         <li>objectId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userId and
     *                 objectId
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdObjectIdFilter() throws Exception {
        int expectedGrantCount = USER_OBJECT_GRANT_COUNT * 3;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);
        String filterXml =
            "<param>" + getFilter(FILTER_USER, userId)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userId and objectId.
     * 
     * @test.name userIdObjectIdFilter
     * @test.id userIdObjectIdFilter
     * @test.input userIdObjectIdFilter
     * @test.inputDescription: <ul>
     *                         <li>userId</li>
     *                         <li>objectId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userId and
     *                 objectId
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdObjectIdFilterCQL() throws Exception {
        int expectedGrantCount = USER_OBJECT_GRANT_COUNT * 3;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_USER + "\"=" + userId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userIds and objectIds.
     * 
     * @test.name userIdsObjectIdsFilter
     * @test.id userIdsObjectIdsFilter
     * @test.input userIdsObjectIdsFilter
     * @test.inputDescription: <ul>
     *                         <li>userIds</li>
     *                         <li>objectIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userIds and
     *                 objectIds
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdsObjectIdsFilter() throws Exception {
        int expectedGrantCount = USER_OBJECT_GRANT_COUNT * 8;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        String userId1 = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(userId1);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        String filterXml =
            "<param>" + getFilter(FILTER_USER, userId)
                + getFilter(FILTER_USER, userId1)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_ASSIGNED_ON, objectId1)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userIds and objectIds.
     * 
     * @test.name userIdsObjectIdsFilter
     * @test.id userIdsObjectIdsFilter
     * @test.input userIdsObjectIdsFilter
     * @test.inputDescription: <ul>
     *                         <li>userIds</li>
     *                         <li>objectIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userIds and
     *                 objectIds
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdsObjectIdsFilterCQL() throws Exception {
        int expectedGrantCount = USER_OBJECT_GRANT_COUNT * 8;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        String userId1 = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(userId1);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_USER + "\"=" + userId + " or " + "\"" + FILTER_USER
            + "\"=" + userId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"="
            + objectId + " or " + "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId1
            + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userId,objectId and roleId.
     * 
     * @test.name userIdObjectIdRoleIdFilter
     * @test.id userIdObjectIdRoleIdFilter
     * @test.input userIdObjectIdRoleIdFilter
     * @test.inputDescription: <ul>
     *                         <li>userId</li>
     *                         <li>objectId</li>
     *                         <li>roleId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userId, objectId
     *                 and roleId
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdObjectIdRoleIdFilter() throws Exception {
        int expectedGrantCount = USER_OBJECT_ROLE_GRANT_COUNT * 3;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);
        allowedValues.add(roleId);
        String filterXml =
            "<param>" + getFilter(FILTER_USER, userId)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_ROLE, roleId)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, "/grant-list/grant/properties/role",
            allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userId,objectId and roleId.
     * 
     * @test.name userIdObjectIdRoleIdFilter
     * @test.id userIdObjectIdRoleIdFilter
     * @test.input userIdObjectIdRoleIdFilter
     * @test.inputDescription: <ul>
     *                         <li>userId</li>
     *                         <li>objectId</li>
     *                         <li>roleId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userId, objectId
     *                 and roleId
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdObjectIdRoleIdFilterCQL() throws Exception {
        int expectedGrantCount = USER_OBJECT_ROLE_GRANT_COUNT * 3;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);
        allowedValues.add(roleId);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_USER + "\"=" + userId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\""
            + FILTER_ROLE + "\"=" + roleId + " and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userIds,objectIds and roleIds.
     * 
     * @test.name userIdsObjectIdsRoleIdsFilter
     * @test.id userIdsObjectIdsRoleIdsFilter
     * @test.input userIdsObjectIdsRoleIdsFilter
     * @test.inputDescription: <ul>
     *                         <li>userIds</li>
     *                         <li>objectIds</li>
     *                         <li>roleIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userIds, objectIds
     *                 and roleIds
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdsObjectIdsRoleIdsFilter() throws Exception {
        int expectedGrantCount = USER_OBJECT_ROLE_GRANT_COUNT * 16;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        String userId1 = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        String roleId1 = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(userId1);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);
        allowedValues.add(roleId);
        allowedValues.add(roleId1);
        String filterXml =
            "<param>" + getFilter(FILTER_USER, userId)
                + getFilter(FILTER_USER, userId1)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_ASSIGNED_ON, objectId1)
                + getFilter(FILTER_ROLE, roleId)
                + getFilter(FILTER_ROLE, roleId1)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, "/grant-list/grant/properties/role",
            allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userIds,objectIds and roleIds.
     * 
     * @test.name userIdsObjectIdsRoleIdsFilter
     * @test.id userIdsObjectIdsRoleIdsFilter
     * @test.input userIdsObjectIdsRoleIdsFilter
     * @test.inputDescription: <ul>
     *                         <li>userIds</li>
     *                         <li>objectIds</li>
     *                         <li>roleIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userIds, objectIds
     *                 and roleIds
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdsObjectIdsRoleIdsFilterCQL() throws Exception {
        int expectedGrantCount = USER_OBJECT_ROLE_GRANT_COUNT * 16;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        String userId1 = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        String roleId1 = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(userId1);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);
        allowedValues.add(roleId);
        allowedValues.add(roleId1);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_USER + "\"=" + userId + " or " + "\"" + FILTER_USER
            + "\"=" + userId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"="
            + objectId + " or " + "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId1
            + ") and " + "(\"" + FILTER_ROLE + "\"=" + roleId + " or " + "\""
            + FILTER_ROLE + "\"=" + roleId1 + ") and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for creatorId and objectId.
     * 
     * @test.name creatorIdObjectIdFilter
     * @test.id creatorIdObjectIdFilter
     * @test.input creatorIdObjectIdFilter
     * @test.inputDescription: <ul>
     *                         <li>creatorId</li>
     *                         <li>objectId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided creatorId and
     *                 objectId
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void creatorIdObjectIdFilter() throws Exception {
        int expectedGrantCount = CREATOR_OBJECT_GRANT_COUNT;
        String creatorId = creatorSysadmins.get(0).get("userId");
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(creatorId);
        allowedValues.add(objectId);
        String filterXml =
            "<param>" + getFilter(FILTER_CREATED_BY, creatorId)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/created-by", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for creatorId and objectId.
     * 
     * @test.name creatorIdObjectIdFilter
     * @test.id creatorIdObjectIdFilter
     * @test.input creatorIdObjectIdFilter
     * @test.inputDescription: <ul>
     *                         <li>creatorId</li>
     *                         <li>objectId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided creatorId and
     *                 objectId
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void creatorIdObjectIdFilterCQL() throws Exception {
        int expectedGrantCount = CREATOR_OBJECT_GRANT_COUNT;
        String creatorId = creatorSysadmins.get(0).get("userId");
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(creatorId);
        allowedValues.add(objectId);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATED_BY + "\"=" + creatorId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/created-by", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for creatorIds and objectIds.
     * 
     * @test.name creatorIdsObjectIdsFilter
     * @test.id creatorIdsObjectIdsFilter
     * @test.input creatorIdsObjectIdsFilter
     * @test.inputDescription: <ul>
     *                         <li>creatorIds</li>
     *                         <li>objectIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided creatorIds and
     *                 objectIds
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void creatorIdsObjectIdsFilter() throws Exception {
        int expectedGrantCount = CREATORS_OBJECTS_GRANT_COUNT;
        String creatorId = creatorSysadmins.get(0).get("userId");
        String creatorId1 = creatorSysadmins.get(1).get("userId");
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(creatorId);
        allowedValues.add(creatorId1);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);
        String filterXml =
            "<param>" + getFilter(FILTER_CREATED_BY, creatorId)
                + getFilter(FILTER_CREATED_BY, creatorId1)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_ASSIGNED_ON, objectId1)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/created-by", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for creatorIds and objectIds.
     * 
     * @test.name creatorIdsObjectIdsFilter
     * @test.id creatorIdsObjectIdsFilter
     * @test.input creatorIdsObjectIdsFilter
     * @test.inputDescription: <ul>
     *                         <li>creatorIds</li>
     *                         <li>objectIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided creatorIds and
     *                 objectIds
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void creatorIdsObjectIdsFilterCQL() throws Exception {
        int expectedGrantCount = CREATORS_OBJECTS_GRANT_COUNT;
        String creatorId = creatorSysadmins.get(0).get("userId");
        String creatorId1 = creatorSysadmins.get(1).get("userId");
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(creatorId);
        allowedValues.add(creatorId1);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_CREATED_BY + "\"=" + creatorId + " or " + "\""
            + FILTER_CREATED_BY + "\"=" + creatorId1 + ") and " + "(\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " or " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId1 + ") and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/created-by", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revokerId and objectId.
     * 
     * @test.name revokerIdObjectIdFilter
     * @test.id revokerIdObjectIdFilter
     * @test.input revokerIdObjectIdFilter
     * @test.inputDescription:
     *             <ul>
     *             <li>revokerId</li>
     *             <li>objectId</li>
     *             </ul>
     * @test.expected: result with all grants having provided revokerId and
     *                 objectId
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revokerIdObjectIdFilter() throws Exception {
        int expectedGrantCount = REVOKER_OBJECT_GRANT_COUNT;
        String revokerId = revokerSysadmins.get(0).get("userId");
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(revokerId);
        allowedValues.add(objectId);
        String filterXml =
            "<param>" + getFilter(FILTER_REVOKED_BY, revokerId)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/revoked-by", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revokerId and objectId.
     * 
     * @test.name revokerIdObjectIdFilter
     * @test.id revokerIdObjectIdFilter
     * @test.input revokerIdObjectIdFilter
     * @test.inputDescription: <ul>
     *                         <li>revokerId</li>
     *                         <li>objectId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided revokerId and
     *                 objectId
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revokerIdObjectIdFilterCQL() throws Exception {
        int expectedGrantCount = REVOKER_OBJECT_GRANT_COUNT;
        String revokerId = revokerSysadmins.get(0).get("userId");
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(revokerId);
        allowedValues.add(objectId);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_REVOKED_BY + "\"=" + revokerId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/revoked-by", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revokerIds and objectIds.
     * 
     * @test.name revokerIdsObjectIdsFilter
     * @test.id revokerIdsObjectIdsFilter
     * @test.input revokerIdsObjectIdsFilter
     * @test.inputDescription: <ul>
     *                         <li>revokerIds</li>
     *                         <li>objectIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided revokerIds and
     *                 objectIds
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revokerIdsObjectIdsFilter() throws Exception {
        int expectedGrantCount = REVOKER_OBJECT_GRANT_COUNT * 2 * 2;
        String revokerId = revokerSysadmins.get(0).get("userId");
        String revokerId1 = revokerSysadmins.get(1).get("userId");
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(revokerId);
        allowedValues.add(revokerId1);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);
        String filterXml =
            "<param>" + getFilter(FILTER_REVOKED_BY, revokerId)
                + getFilter(FILTER_REVOKED_BY, revokerId1)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_ASSIGNED_ON, objectId1)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/revoked-by", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revokerIds and objectIds.
     * 
     * @test.name revokerIdsObjectIdsFilter
     * @test.id revokerIdsObjectIdsFilter
     * @test.input revokerIdsObjectIdsFilter
     * @test.inputDescription: <ul>
     *                         <li>revokerIds</li>
     *                         <li>objectIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided revokerIds and
     *                 objectIds
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revokerIdsObjectIdsFilterCQL() throws Exception {
        int expectedGrantCount = REVOKER_OBJECT_GRANT_COUNT * 2 * 2;
        String revokerId = revokerSysadmins.get(0).get("userId");
        String revokerId1 = revokerSysadmins.get(1).get("userId");
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(revokerId);
        allowedValues.add(revokerId1);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_REVOKED_BY + "\"=" + revokerId + " or " + "\""
            + FILTER_REVOKED_BY + "\"=" + revokerId1 + ") and " + "(\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " or " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId1 + ") and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/revoked-by", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for groupId and objectId.
     * 
     * @test.name groupIdObjectIdFilter
     * @test.id groupIdObjectIdFilter
     * @test.input groupIdObjectIdFilter
     * @test.inputDescription: <ul>
     *                         <li>groupId</li>
     *                         <li>objectId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided groupId and
     *                 objectId
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdObjectIdFilter() throws Exception {
        int expectedGrantCount = GROUP_OBJECT_GRANT_COUNT;
        Iterator<String> groupIdIter = GROUP_IDS.iterator();
        String groupId = groupIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(groupId);
        allowedValues.add(objectId);
        String filterXml =
            "<param>" + getFilter(FILTER_GROUP, groupId)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for groupId and objectId.
     * 
     * @test.name groupIdObjectIdFilter
     * @test.id groupIdObjectIdFilter
     * @test.input groupIdObjectIdFilter
     * @test.inputDescription: <ul>
     *                         <li>groupId</li>
     *                         <li>objectId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided groupId and
     *                 objectId
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdObjectIdFilterCQL() throws Exception {
        int expectedGrantCount = GROUP_OBJECT_GRANT_COUNT;
        Iterator<String> groupIdIter = GROUP_IDS.iterator();
        String groupId = groupIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(groupId);
        allowedValues.add(objectId);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_GROUP + "\"=" + groupId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for groupIds and objectIds.
     * 
     * @test.name groupIdsObjectIdsFilter
     * @test.id groupIdsObjectIdsFilter
     * @test.input groupIdsObjectIdsFilter
     * @test.inputDescription: <ul>
     *                         <li>groupIds</li>
     *                         <li>objectIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userIds and
     *                 objectIds
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdsObjectIdsFilter() throws Exception {
        int expectedGrantCount = GROUP_OBJECT_GRANT_COUNT * 2 * 2;
        Iterator<String> groupIdIter = GROUP_IDS.iterator();
        String groupId = groupIdIter.next();
        String groupId1 = groupIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(groupId);
        allowedValues.add(groupId1);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);
        String filterXml =
            "<param>" + getFilter(FILTER_GROUP, groupId)
                + getFilter(FILTER_GROUP, groupId1)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_ASSIGNED_ON, objectId1)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for groupIds and objectIds.
     * 
     * @test.name groupIdsObjectIdsFilter
     * @test.id groupIdsObjectIdsFilter
     * @test.input groupIdsObjectIdsFilter
     * @test.inputDescription: <ul>
     *                         <li>groupIds</li>
     *                         <li>objectIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userIds and
     *                 objectIds
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdsObjectIdsFilterCQL() throws Exception {
        int expectedGrantCount = GROUP_OBJECT_GRANT_COUNT * 2 * 2;
        Iterator<String> groupIdIter = GROUP_IDS.iterator();
        String groupId = groupIdIter.next();
        String groupId1 = groupIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(groupId);
        allowedValues.add(groupId1);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_GROUP + "\"=" + groupId + " or " + "\"" + FILTER_GROUP
            + "\"=" + groupId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"="
            + objectId + " or " + "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId1
            + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for groupId,objectId and roleId.
     * 
     * @test.name groupIdObjectIdRoleIdFilter
     * @test.id groupIdObjectIdRoleIdFilter
     * @test.input groupIdObjectIdRoleIdFilter
     * @test.inputDescription: <ul>
     *                         <li>groupId</li>
     *                         <li>objectId</li>
     *                         <li>roleId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided groupId, objectId
     *                 and roleId
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdObjectIdRoleIdFilter() throws Exception {
        int expectedGrantCount = GROUP_OBJECT_ROLE_GRANT_COUNT;
        Iterator<String> groupIdIter = GROUP_IDS.iterator();
        String groupId = groupIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(groupId);
        allowedValues.add(objectId);
        allowedValues.add(roleId);
        String filterXml =
            "<param>" + getFilter(FILTER_GROUP, groupId)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_ROLE, roleId)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, "/grant-list/grant/properties/role",
            allowedValues, true);
    }

    /**
     * Test successfully filtering grants for groupId,objectId and roleId.
     * 
     * @test.name groupIdObjectIdRoleIdFilter
     * @test.id groupIdObjectIdRoleIdFilter
     * @test.input groupIdObjectIdRoleIdFilter
     * @test.inputDescription: <ul>
     *                         <li>groupId</li>
     *                         <li>objectId</li>
     *                         <li>roleId</li>
     *                         </ul>
     * @test.expected: result with all grants having provided groupId, objectId
     *                 and roleId
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdObjectIdRoleIdFilterCQL() throws Exception {
        int expectedGrantCount = GROUP_OBJECT_ROLE_GRANT_COUNT;
        Iterator<String> groupIdIter = GROUP_IDS.iterator();
        String groupId = groupIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(groupId);
        allowedValues.add(objectId);
        allowedValues.add(roleId);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_GROUP + "\"=" + groupId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\""
            + FILTER_ROLE + "\"=" + roleId + " and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for groupIds,objectIds and roleIds.
     * 
     * @test.name groupIdsObjectIdsRoleIdsFilter
     * @test.id groupIdsObjectIdsRoleIdsFilter
     * @test.input groupIdsObjectIdsRoleIdsFilter
     * @test.inputDescription: <ul>
     *                         <li>groupIds</li>
     *                         <li>objectIds</li>
     *                         <li>roleIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided groupIds,
     *                 objectIds and roleIds
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdsObjectIdsRoleIdsFilter() throws Exception {
        int expectedGrantCount = GROUP_OBJECT_ROLE_GRANT_COUNT * 2 * 2 * 2;
        Iterator<String> groupIdIter = GROUP_IDS.iterator();
        String groupId = groupIdIter.next();
        String groupId1 = groupIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        String roleId1 = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(groupId);
        allowedValues.add(groupId1);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);
        allowedValues.add(roleId);
        allowedValues.add(roleId1);
        String filterXml =
            "<param>" + getFilter(FILTER_GROUP, groupId)
                + getFilter(FILTER_GROUP, groupId1)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_ASSIGNED_ON, objectId1)
                + getFilter(FILTER_ROLE, roleId)
                + getFilter(FILTER_ROLE, roleId1)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, "/grant-list/grant/properties/role",
            allowedValues, true);
    }

    /**
     * Test successfully filtering grants for groupIds,objectIds and roleIds.
     * 
     * @test.name groupIdsObjectIdsRoleIdsFilter
     * @test.id groupIdsObjectIdsRoleIdsFilter
     * @test.input groupIdsObjectIdsRoleIdsFilter
     * @test.inputDescription: <ul>
     *                         <li>groupIds</li>
     *                         <li>objectIds</li>
     *                         <li>roleIds</li>
     *                         </ul>
     * @test.expected: result with all grants having provided groupIds,
     *                 objectIds and roleIds
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdsObjectIdsRoleIdsFilterCQL() throws Exception {
        int expectedGrantCount = GROUP_OBJECT_ROLE_GRANT_COUNT * 2 * 2 * 2;
        Iterator<String> groupIdIter = GROUP_IDS.iterator();
        String groupId = groupIdIter.next();
        String groupId1 = groupIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        String roleId1 = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(groupId);
        allowedValues.add(groupId1);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);
        allowedValues.add(roleId);
        allowedValues.add(roleId1);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_GROUP + "\"=" + groupId + " or " + "\"" + FILTER_GROUP
            + "\"=" + groupId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"="
            + objectId + " or " + "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId1
            + ") and " + "(\"" + FILTER_ROLE + "\"=" + roleId + " or " + "\""
            + FILTER_ROLE + "\"=" + roleId1 + ") and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userId and objectId created in the
     * first half of the prepare-method.
     * 
     * @test.name userIdObjectIdFirstpartFilter
     * @test.id userIdObjectIdFirstpartFilter
     * @test.input userIdObjectIdFirstpartFilter
     * @test.inputDescription: <ul>
     *                         <li>userId</li>
     *                         <li>objectId</li>
     *                         <li>grantedDateFrom</li>
     *                         <li>grantedDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userId and
     *                 objectId created in the first half of the prepare-method.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdObjectIdFirstpartFilter() throws Exception {
        int expectedGrantCount = USER_OBJECT_FIRSTPART_GRANT_COUNT * 3;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);
        String filterXml =
            "<param>"
                + getFilter(FILTER_USER, userId)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getFilter(FILTER_CREATION_DATE_TO, partTimeParameters
                    .get("time")) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userId and objectId created in the
     * first half of the prepare-method.
     * 
     * @test.name userIdObjectIdFirstpartFilter
     * @test.id userIdObjectIdFirstpartFilter
     * @test.input userIdObjectIdFirstpartFilter
     * @test.inputDescription: <ul>
     *                         <li>userId</li>
     *                         <li>objectId</li>
     *                         <li>grantedDateFrom</li>
     *                         <li>grantedDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userId and
     *                 objectId created in the first half of the prepare-method.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdObjectIdFirstpartFilterCQL() throws Exception {
        int expectedGrantCount = USER_OBJECT_FIRSTPART_GRANT_COUNT * 3;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_USER + "\"=" + userId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" and " + "\""
            + FILTER_CREATION_DATE + "\"<=\"" + partTimeParameters.get("time")
            + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userIds and objectIds created in
     * the first half of the prepare-method.
     * 
     * @test.name userIdsObjectIdsFirstpartFilter
     * @test.id userIdsObjectIdsFirstpartFilter
     * @test.input userIdsObjectIdsFirstpartFilter
     * @test.inputDescription: <ul>
     *                         <li>userIds</li>
     *                         <li>objectIds</li>
     *                         <li>grantedDateFrom</li>
     *                         <li>grantedDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userIds and
     *                 objectIds created in the first half of the
     *                 prepare-method.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdsObjectIdsFirstpartFilter() throws Exception {
        int expectedGrantCount = USER_OBJECT_FIRSTPART_GRANT_COUNT * 8;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        String userId1 = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(userId1);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);
        String filterXml =
            "<param>"
                + getFilter(FILTER_USER, userId)
                + getFilter(FILTER_USER, userId1)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_ASSIGNED_ON, objectId1)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getFilter(FILTER_CREATION_DATE_TO, partTimeParameters
                    .get("time")) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userIds and objectIds created in
     * the first half of the prepare-method.
     * 
     * @test.name userIdsObjectIdsFirstpartFilter
     * @test.id userIdsObjectIdsFirstpartFilter
     * @test.input userIdsObjectIdsFirstpartFilter
     * @test.inputDescription: <ul>
     *                         <li>userIds</li>
     *                         <li>objectIds</li>
     *                         <li>grantedDateFrom</li>
     *                         <li>grantedDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userIds and
     *                 objectIds created in the first half of the
     *                 prepare-method.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdsObjectIdsFirstpartFilterCQL() throws Exception {
        int expectedGrantCount = USER_OBJECT_FIRSTPART_GRANT_COUNT * 8;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        String userId1 = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(userId1);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_USER + "\"=" + userId + " or " + "\"" + FILTER_USER
            + "\"=" + userId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"="
            + objectId + " or " + "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId1
            + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" and " + "\"" + FILTER_CREATION_DATE + "\"<=\""
            + partTimeParameters.get("time") + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userId,objectId and roleId created
     * in the first half of the prepare-method.
     * 
     * @test.name userIdObjectIdRoleIdFirstpartFilter
     * @test.id userIdObjectIdRoleIdFirstpartFilter
     * @test.input userIdObjectIdRoleIdFirstpartFilter
     * @test.inputDescription: <ul>
     *                         <li>userId</li>
     *                         <li>objectId</li>
     *                         <li>roleId</li>
     *                         <li>grantedDateFrom</li>
     *                         <li>grantedDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userId, objectId
     *                 and roleId created in the first half of the
     *                 prepare-method.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdObjectIdRoleIdFirstpartFilter() throws Exception {
        int expectedGrantCount = USER_OBJECT_ROLE_FIRSTPART_GRANT_COUNT * 3;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);
        allowedValues.add(roleId);
        String filterXml =
            "<param>"
                + getFilter(FILTER_USER, userId)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_ROLE, roleId)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getFilter(FILTER_CREATION_DATE_TO, partTimeParameters
                    .get("time")) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, "/grant-list/grant/properties/role",
            allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userId,objectId and roleId created
     * in the first half of the prepare-method.
     * 
     * @test.name userIdObjectIdRoleIdFirstpartFilter
     * @test.id userIdObjectIdRoleIdFirstpartFilter
     * @test.input userIdObjectIdRoleIdFirstpartFilter
     * @test.inputDescription: <ul>
     *                         <li>userId</li>
     *                         <li>objectId</li>
     *                         <li>roleId</li>
     *                         <li>grantedDateFrom</li>
     *                         <li>grantedDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userId, objectId
     *                 and roleId created in the first half of the
     *                 prepare-method.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdObjectIdRoleIdFirstpartFilterCQL() throws Exception {
        int expectedGrantCount = USER_OBJECT_ROLE_FIRSTPART_GRANT_COUNT * 3;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);
        allowedValues.add(roleId);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_USER + "\"=" + userId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\""
            + FILTER_ROLE + "\"=" + roleId + " and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" and " + "\""
            + FILTER_CREATION_DATE + "\"<=\"" + partTimeParameters.get("time")
            + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userIds,objectIds and roleIds
     * created in the first half of the prepare-method.
     * 
     * @test.name userIdsObjectIdsRoleIdsFirstpartFilter
     * @test.id userIdsObjectIdsRoleIdsFirstpartFilter
     * @test.input userIdsObjectIdsRoleIdsFirstpartFilter
     * @test.inputDescription: <ul>
     *                         <li>userIds</li>
     *                         <li>objectIds</li>
     *                         <li>roleIds</li>
     *                         <li>grantedDateFrom</li>
     *                         <li>grantedDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userIds, objectIds
     *                 and roleIds created in the first half of the
     *                 prepare-method.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdsObjectIdsRoleIdsFirstpartFilter() throws Exception {
        int expectedGrantCount = USER_OBJECT_ROLE_FIRSTPART_GRANT_COUNT * 16;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        String userId1 = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        String roleId1 = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(userId1);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);
        allowedValues.add(roleId);
        allowedValues.add(roleId1);
        String filterXml =
            "<param>"
                + getFilter(FILTER_USER, userId)
                + getFilter(FILTER_USER, userId1)
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_ASSIGNED_ON, objectId1)
                + getFilter(FILTER_ROLE, roleId)
                + getFilter(FILTER_ROLE, roleId1)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getFilter(FILTER_CREATION_DATE_TO, partTimeParameters
                    .get("time")) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, "/grant-list/grant/properties/role",
            allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userIds,objectIds and roleIds
     * created in the first half of the prepare-method.
     * 
     * @test.name userIdsObjectIdsRoleIdsFirstpartFilter
     * @test.id userIdsObjectIdsRoleIdsFirstpartFilter
     * @test.input userIdsObjectIdsRoleIdsFirstpartFilter
     * @test.inputDescription: <ul>
     *                         <li>userIds</li>
     *                         <li>objectIds</li>
     *                         <li>roleIds</li>
     *                         <li>grantedDateFrom</li>
     *                         <li>grantedDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having provided userIds, objectIds
     *                 and roleIds created in the first half of the
     *                 prepare-method.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdsObjectIdsRoleIdsFirstpartFilterCQL() throws Exception {
        int expectedGrantCount = USER_OBJECT_ROLE_FIRSTPART_GRANT_COUNT * 16;
        Iterator<String> userIdIter = userIds.iterator();
        String userId = userIdIter.next();
        String userId1 = userIdIter.next();
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        String objectId1 = getObjidFromHref(objectIdIter.next());
        Iterator<String> roleIdIter = ROLES.iterator();
        String roleId = getObjidFromHref(roleIdIter.next());
        String roleId1 = getObjidFromHref(roleIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(userId);
        allowedValues.add(userId1);
        allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
        allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);
        allowedValues.add(objectId);
        allowedValues.add(objectId1);
        allowedValues.add(roleId);
        allowedValues.add(roleId1);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_USER + "\"=" + userId + " or " + "\"" + FILTER_USER
            + "\"=" + userId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"="
            + objectId + " or " + "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId1
            + ") and " + "(\"" + FILTER_ROLE + "\"=" + roleId + " or " + "\""
            + FILTER_ROLE + "\"=" + roleId1 + ") and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" and " + "\""
            + FILTER_CREATION_DATE + "\"<=\"" + partTimeParameters.get("time")
            + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for objectId revoked in the first half
     * of the prepare-method.
     * 
     * @test.name ObjectIdFirstpartRevocationFilter
     * @test.id ObjectIdFirstpartRevocationFilter
     * @test.input ObjectIdFirstpartRevocationFilter
     * @test.inputDescription: <ul>
     *                         <li>objectId</li>
     *                         <li>revocationDateFrom</li>
     *                         <li>revocationDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having provided objectId revoked
     *                 in the first half of the prepare-method.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void objectIdFirstpartRevocationFilter() throws Exception {
        int expectedGrantCount = OBJECT_REVOCATION_FIRSTPART_GRANT_COUNT;
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(objectId);
        String filterXml =
            "<param>"
                + getFilter(FILTER_ASSIGNED_ON, objectId)
                + getFilter(FILTER_REVOCATION_DATE_FROM, startTime)
                + getFilter(FILTER_REVOCATION_DATE_TO, partTimeParameters
                    .get("time")) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for objectId revoked in the first half
     * of the prepare-method.
     * 
     * @test.name ObjectIdFirstpartRevocationFilter
     * @test.id ObjectIdFirstpartRevocationFilter
     * @test.input ObjectIdFirstpartRevocationFilter
     * @test.inputDescription: <ul>
     *                         <li>objectId</li>
     *                         <li>revocationDateFrom</li>
     *                         <li>revocationDateTo</li>
     *                         </ul>
     * @test.expected: result with all grants having provided objectId revoked
     *                 in the first half of the prepare-method.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void objectIdFirstpartRevocationFilterCQL() throws Exception {
        int expectedGrantCount = OBJECT_REVOCATION_FIRSTPART_GRANT_COUNT;
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(objectId);

        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\""
            + FILTER_REVOCATION_DATE + "\">=\"" + startTime + "\" and " + "\""
            + FILTER_REVOCATION_DATE + "\"<=\""
            + partTimeParameters.get("time") + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully sorting grants for userId ascending.
     * 
     * @test.name userIdSortFilter
     * @test.id userIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by userId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdSortFilterAsc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_USER, true) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/granted-to",
            "parent::node()[@resource='user-account']", true, true);
    }

    /**
     * Test successfully sorting grants for userId ascending.
     * 
     * @test.name userIdSortFilter
     * @test.id userIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by userId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_USER + "\"/sort.ascending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to",
            "parent::node()[@resource='user-account']", true, true);
    }

    /**
     * Test successfully sorting grants for userId descending.
     * 
     * @test.name userIdSortFilter
     * @test.id userIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by userId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdSortFilterDesc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_USER, false) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/granted-to",
            "parent::node()[@resource='user-account']", false, true);
    }

    /**
     * Test successfully sorting grants for userId descending.
     * 
     * @test.name userIdSortFilter
     * @test.id userIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by userId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void userIdSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_USER + "\"/sort.descending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to",
            "parent::node()[@resource='user-account']", false, true);
    }

    /**
     * Test successfully sorting grants for groupId ascending.
     * 
     * @test.name groupIdSortFilter
     * @test.id groupIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by groupId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdSortFilterAsc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_GROUP, true) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/granted-to",
            "parent::node()[@resource='user-group']", true, true);
    }

    /**
     * Test successfully sorting grants for groupId ascending.
     * 
     * @test.name groupIdSortFilter
     * @test.id groupIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by groupId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_GROUP + "\"/sort.ascending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to",
            "parent::node()[@resource='user-group']", true, true);
    }

    /**
     * Test successfully sorting grants for groupId descending.
     * 
     * @test.name groupIdSortFilter
     * @test.id groupIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by groupId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdSortFilterDesc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_GROUP, false) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/granted-to",
            "parent::node()[@resource='user-group']", false, true);
    }

    /**
     * Test successfully sorting grants for groupId descending.
     * 
     * @test.name groupIdSortFilter
     * @test.id groupIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by groupId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void groupIdSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_GROUP + "\"/sort.descending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to",
            "parent::node()[@resource='user-group']", false, true);
    }

    /**
     * Test successfully sorting grants for roleId ascending.
     * 
     * @test.name roleIdSortFilter
     * @test.id roleIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by roleId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void roleIdSortFilterAsc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_ROLE, true) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/role", null, true,
            true);
    }

    /**
     * Test successfully sorting grants for roleId ascending.
     * 
     * @test.name roleIdSortFilter
     * @test.id roleIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by roleId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void roleIdSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_ROLE + "\"/sort.ascending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/role",
            null, true, true);
    }

    /**
     * Test successfully sorting grants for roleId descending.
     * 
     * @test.name roleIdSortFilter
     * @test.id roleIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by roleId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void roleIdSortFilterDesc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_ROLE, false) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/role", null, false,
            true);
    }

    /**
     * Test successfully sorting grants for roleId descending.
     * 
     * @test.name roleIdSortFilter
     * @test.id roleIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by roleId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void roleIdSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_ROLE + "\"/sort.descending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/role",
            null, false, true);
    }

    /**
     * Test successfully sorting grants for objectId ascending.
     * 
     * @test.name objectIdSortFilter
     * @test.id objectIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by objectId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void objectIdSortFilterAsc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_ASSIGNED_ON, true) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/assigned-on", null,
            true, true);
    }

    /**
     * Test successfully sorting grants for objectId ascending.
     * 
     * @test.name objectIdSortFilter
     * @test.id objectIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by objectId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void objectIdSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_ASSIGNED_ON + "\"/sort.ascending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", null, true, true);
    }

    /**
     * Test successfully sorting grants for objectId descending.
     * 
     * @test.name objectIdSortFilter
     * @test.id objectIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by objectId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void objectIdSortFilterDesc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_ASSIGNED_ON, false) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/assigned-on", null,
            false, true);
    }

    /**
     * Test successfully sorting grants for objectId descending.
     * 
     * @test.name objectIdSortFilter
     * @test.id objectIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by objectId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void objectIdSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_ASSIGNED_ON + "\"/sort.descending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/assigned-on", null, false, true);
    }

    /**
     * Test successfully sorting grants for creatorId ascending.
     * 
     * @test.name creatorIdSortFilter
     * @test.id creatorIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by creatorId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void creatorIdSortFilterAsc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_CREATED_BY, true) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/created-by", null,
            true, true);
    }

    /**
     * Test successfully sorting grants for creatorId ascending.
     * 
     * @test.name creatorIdSortFilter
     * @test.id creatorIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by creatorId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void creatorIdSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_CREATED_BY + "\"/sort.ascending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/created-by", null, true, true);
    }

    /**
     * Test successfully sorting grants for creatorId descending.
     * 
     * @test.name creatorIdSortFilter
     * @test.id creatorIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by creatorId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void creatorIdSortFilterDesc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_CREATED_BY, false) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/created-by", null,
            false, true);
    }

    /**
     * Test successfully sorting grants for creatorId descending.
     * 
     * @test.name creatorIdSortFilter
     * @test.id creatorIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by creatorId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void creatorIdSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_CREATED_BY + "\"/sort.descending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/created-by", null, false, true);
    }

    /**
     * Test successfully sorting grants for revokerId ascending.
     * 
     * @test.name revokerIdSortFilter
     * @test.id revokerIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by revokerId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revokerIdSortFilterAsc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_REVOKED_BY, true) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/revoked-by", null,
            true, true);
    }

    /**
     * Test successfully sorting grants for revokerId ascending.
     * 
     * @test.name revokerIdSortFilter
     * @test.id revokerIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by revokerId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revokerIdSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_REVOKED_BY + "\"/sort.ascending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/revoked-by", null, true, true);
    }

    /**
     * Test successfully sorting grants for revokerId descending.
     * 
     * @test.name revokerIdSortFilter
     * @test.id revokerIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by revokerId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revokerIdSortFilterDesc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_REVOKED_BY, false) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/revoked-by", null,
            false, true);
    }

    /**
     * Test successfully sorting grants for revokerId descending.
     * 
     * @test.name revokerIdSortFilter
     * @test.id revokerIdSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by revokerId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revokerIdSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_REVOKED_BY + "\"/sort.descending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/revoked-by", null, false, true);
    }

    /**
     * Test successfully sorting grants for revocationDateFrom ascending.
     * 
     * @test.name revocationDateFromSortFilter
     * @test.id revocationDateFromSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by
     *                 revocationDateFrom.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateFromSortFilterAsc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_REVOCATION_DATE_FROM, true) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/revocation-date",
            null, true, true);
    }

    /**
     * Test successfully sorting grants for revocationDate ascending.
     * 
     * @test.name revocationDateSortFilter
     * @test.id revocationDateSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by
     *                 revocationDate.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_REVOCATION_DATE + "\"/sort.ascending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/revocation-date", null, true, true);
    }

    /**
     * Test successfully sorting grants for revocationDateFrom descending.
     * 
     * @test.name revocationDateFromSortFilter
     * @test.id revocationDateFromSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by
     *                 revocationDateFrom.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateFromSortFilterDesc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_REVOCATION_DATE_FROM, false) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/revocation-date",
            null, false, true);
    }

    /**
     * Test successfully sorting grants for revocationDate descending.
     * 
     * @test.name revocationDateSortFilter
     * @test.id revocationDateSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by
     *                 revocationDate.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_REVOCATION_DATE + "\"/sort.descending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/revocation-date", null, false, true);
    }

    /**
     * Test successfully sorting grants for revocationDateTo ascending.
     * 
     * @test.name revocationDateToSortFilter
     * @test.id revocationDateToSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by
     *                 revocationDateTo.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateToSortFilterAsc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_REVOCATION_DATE_TO, true) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/revocation-date",
            null, true, true);
    }

    /**
     * Test successfully sorting grants for revocationDateTo descending.
     * 
     * @test.name revocationDateToSortFilter
     * @test.id revocationDateToSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by
     *                 revocationDateTo.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void revocationDateToSortFilterDesc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_REVOCATION_DATE_TO, false) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/revocation-date",
            null, false, true);
    }

    /**
     * Test successfully sorting grants for grantedDateFrom ascending.
     * 
     * @test.name grantedDateFromSortFilter
     * @test.id grantedDateFromSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by
     *                 grantedDateFrom.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateFromSortFilterAsc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_CREATION_DATE_FROM, true) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/creation-date",
            null, true, true);
    }

    /**
     * Test successfully sorting grants for grantedDateFrom ascending.
     * 
     * @test.name grantedDateFromSortFilter
     * @test.id grantedDateFromSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by
     *                 grantedDateFrom.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateFromSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_GRANTED_FROM + "\"/sort.ascending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result,
            XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-from",
            null, true, true);
    }

    /**
     * Test successfully sorting grants for grantedDateFrom descending.
     * 
     * @test.name grantedDateFromSortFilter
     * @test.id grantedDateFromSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by
     *                 grantedDateFrom.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateFromSortFilterDesc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_CREATION_DATE_FROM, false) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/creation-date",
            null, false, true);
    }

    /**
     * Test successfully sorting grants for grantedDateFrom descending.
     * 
     * @test.name grantedDateFromSortFilter
     * @test.id grantedDateFromSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by
     *                 grantedDateFrom.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateFromSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_GRANTED_FROM + "\"/sort.descending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result,
            XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-from",
            null, false, true);
    }

    /**
     * Test successfully sorting grants for grantedDateTo ascending.
     * 
     * @test.name grantedDateToSortFilter
     * @test.id grantedDateToSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by grantedDateTo.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateToSortFilterAsc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_CREATION_DATE_TO, true) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/creation-date",
            null, true, true);
    }

    /**
     * Test successfully sorting grants for grantedDateTo ascending.
     * 
     * @test.name grantedDateToSortFilter
     * @test.id grantedDateToSortFilter
     * @test.input
     * @test.expected: result with all grants sorted ascending by grantedDateTo.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateToSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_CREATION_DATE + "\"/sort.ascending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/creation-date", null, true, true);
    }

    /**
     * Test successfully sorting grants for grantedDateTo descending.
     * 
     * @test.name grantedDateToSortFilter
     * @test.id grantedDateToSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by
     *                 grantedDateTo.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateToSortFilterDesc() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_CREATION_DATE_TO, false) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertSorted(result, "/grant-list/grant/properties/creation-date",
            null, false, true);
    }

    /**
     * Test successfully sorting grants for grantedDateTo descending.
     * 
     * @test.name grantedDateToSortFilter
     * @test.id grantedDateToSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by
     *                 grantedDateTo.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void grantedDateToSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_GRANTED_TO + "\"/sort.descending" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result,
            XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to",
            null, false, true);
    }

    /**
     * Test successfully sorting grants for grantedDateTo descending.
     * 
     * @test.name grantedDateToSortFilter
     * @test.id grantedDateToSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by
     *                 grantedDateTo.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void offsetLimitFilter() throws Exception {
        int expectedGrantCount = USER_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, startTime)
                + getOrderBy(FILTER_USER, true) + getLimit(USER_GRANT_COUNT)
                + getOffset(REPLACEMENT_INT) + "</param>";
        String result = null;
        int i = 0;
        for (String userId : userIds) {
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(userId);
            try {
                result =
                    retrieveGrants(filterXml.replaceAll("123456789", Integer
                        .toString(i * USER_GRANT_COUNT)));
            }
            catch (Exception e) {
                EscidocRestSoapTestsBase.failException(e);
            }
            assertXmlValidGrantList(result);
            assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
            assertAllowedXpathValues(result,
                "/grant-list/grant/properties/granted-to", allowedValues, true);
            i++;
        }
    }

    /**
     * Test successfully sorting grants for grantedDateTo descending.
     * 
     * @test.name grantedDateToSortFilter
     * @test.id grantedDateToSortFilter
     * @test.input
     * @test.expected: result with all grants sorted descending by
     *                 grantedDateTo.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void offsetLimitFilterCQL() throws Exception {
        int expectedGrantCount = USER_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" sortby " + "\""
            + FILTER_USER + "\"/sort.ascending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { String
            .valueOf(USER_GRANT_COUNT) });

        String result = null;
        int i = 0;

        for (String userId : userIds) {
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(userId);
            try {
                filterParams.put(FILTER_PARAMETER_STARTRECORD,
                    new String[] { String.valueOf(i * USER_GRANT_COUNT + 1) });
                result = retrieveGrants(filterParams);
            }
            catch (Exception e) {
                EscidocRestSoapTestsBase.failException(e);
            }
            assertXmlValidSrwResponse(result);
            assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT,
                expectedGrantCount);
            assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
                + "/properties/granted-to", allowedValues, true);
            i++;
        }
    }

    /**
     * Test successfully retrieving grants with userId = null.
     * 
     * @test.name nullUserFilter
     * @test.id nullUserFilter
     * @test.input
     * @test.expected: result with all grants having userId = null.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullUserFilter() throws Exception {
        int expectedGrantCount = GROUP_GRANT_COUNT * GROUP_IDS.size();
        String filterXml =
            "<param>" + getFilter(FILTER_USER, null)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", GROUP_IDS, true);
    }

    /**
     * Test successfully retrieving grants with userId = null.
     * 
     * @test.name nullUserFilter
     * @test.id nullUserFilter
     * @test.input
     * @test.expected: result with all grants having userId = null.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullUserFilterCQL() throws Exception {
        int expectedGrantCount = GROUP_GRANT_COUNT * GROUP_IDS.size();
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_USER + "\"=\"\" and " + "\"" + FILTER_CREATION_DATE
            + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", GROUP_IDS, true);
    }

    /**
     * Test successfully retrieving grants with userId = null or userId =
     * existing userId.
     * 
     * @test.name nullPlusUserFilter
     * @test.id nullPlusUserFilter
     * @test.input
     * @test.expected: result with all grants having userId = null or userId =
     *                 existing userId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullPlusUserFilter() throws Exception {
        int expectedGrantCount =
            GROUP_GRANT_COUNT * GROUP_IDS.size() + USER_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_USER, null)
                + getFilter(FILTER_USER, userIds.get(0))
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.addAll(GROUP_IDS);
        allowedValues.add(userIds.get(0));
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
    }

    /**
     * Test successfully retrieving grants with userId = null or userId =
     * existing userId.
     * 
     * @test.name nullPlusUserFilter
     * @test.id nullPlusUserFilter
     * @test.input
     * @test.expected: result with all grants having userId = null or userId =
     *                 existing userId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullPlusUserFilterCQL() throws Exception {
        int expectedGrantCount =
            GROUP_GRANT_COUNT * GROUP_IDS.size() + USER_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_USER + "\"=\"\" or " + "\"" + FILTER_USER + "\"="
            + userIds.get(0) + ") and " + "\"" + FILTER_CREATION_DATE
            + "\">=\"" + startTime + "\"" });

        List<String> allowedValues = new ArrayList<String>();
        allowedValues.addAll(GROUP_IDS);
        allowedValues.add(userIds.get(0));
        String result = null;
        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
    }

    /**
     * Test successfully retrieving grants with groupId = null.
     * 
     * @test.name nullGroupFilter
     * @test.id nullGroupFilter
     * @test.input
     * @test.expected: result with all grants having groupId = null.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullGroupFilter() throws Exception {
        int expectedGrantCount = USER_GRANT_COUNT * userIds.size();
        String filterXml =
            "<param>" + getFilter(FILTER_GROUP, null)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", userIds, true);
    }

    /**
     * Test successfully retrieving grants with groupId = null.
     * 
     * @test.name nullGroupFilter
     * @test.id nullGroupFilter
     * @test.input
     * @test.expected: result with all grants having groupId = null.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullGroupFilterCQL() throws Exception {
        int expectedGrantCount = USER_GRANT_COUNT * userIds.size();
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_GROUP + "\"=\"\" and " + "\"" + FILTER_CREATION_DATE
            + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", userIds, true);
    }

    /**
     * Test successfully retrieving grants with groupId = null or groupId =
     * existing groupId.
     * 
     * @test.name nullPlusGroupFilter
     * @test.id nullPlusGroupFilter
     * @test.input
     * @test.expected: result with all grants having groupId = null or groupId =
     *                 existing groupId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullPlusGroupFilter() throws Exception {
        int expectedGrantCount =
            USER_GRANT_COUNT * userIds.size() + GROUP_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_GROUP, null)
                + getFilter(FILTER_GROUP, GROUP_IDS.get(0))
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.addAll(userIds);
        allowedValues.add(GROUP_IDS.get(0));
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
        assertAllowedXpathValues(result,
            "/grant-list/grant/properties/granted-to", allowedValues, true);
    }

    /**
     * Test successfully retrieving grants with groupId = null or groupId =
     * existing groupId.
     * 
     * @test.name nullPlusGroupFilter
     * @test.id nullPlusGroupFilter
     * @test.input
     * @test.expected: result with all grants having groupId = null or groupId =
     *                 existing groupId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullPlusGroupFilterCQL() throws Exception {
        int expectedGrantCount =
            USER_GRANT_COUNT * userIds.size() + GROUP_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_GROUP + "\"=\"\" or " + "\"" + FILTER_GROUP + "\"="
            + GROUP_IDS.get(0) + ") and " + "\"" + FILTER_CREATION_DATE
            + "\">=\"" + startTime + "\"" });

        List<String> allowedValues = new ArrayList<String>();
        allowedValues.addAll(userIds);
        allowedValues.add(GROUP_IDS.get(0));
        String result = null;
        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT
            + "/properties/granted-to", allowedValues, true);
    }

    /**
     * Test successfully retrieving grants with revokerId = null.
     * 
     * @test.name nullRevokerFilter
     * @test.id nullRevokerFilter
     * @test.input
     * @test.expected: result with all grants having revokerId = null.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullRevokerFilter() throws Exception {
        int expectedGrantCount = NON_REVOKED_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_REVOKED_BY, null)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
    }

    /**
     * Test successfully retrieving grants with revokerId = null.
     * 
     * @test.name nullRevokerFilter
     * @test.id nullRevokerFilter
     * @test.input
     * @test.expected: result with all grants having revokerId = null.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullRevokerFilterCQL() throws Exception {
        int expectedGrantCount = NON_REVOKED_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_REVOKED_BY + "\"=\"\" and " + "\"" + FILTER_CREATION_DATE
            + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully retrieving grants with revokerId = null or revokerId =
     * existing revokerId.
     * 
     * @test.name nullPlusRevokerFilter
     * @test.id nullPlusRevokerFilter
     * @test.input
     * @test.expected: result with all grants having revokerId = null or
     *                 revokerId = existing revokerId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullPlusRevokerFilter() throws Exception {
        int expectedGrantCount =
            NON_REVOKED_GRANT_COUNT
                + Integer.parseInt(revokerSysadmins.get(0).get("grantCount"));
        String filterXml =
            "<param>"
                + getFilter(FILTER_REVOKED_BY, null)
                + getFilter(FILTER_REVOKED_BY, revokerSysadmins.get(0).get(
                    "userId"))
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
    }

    /**
     * Test successfully retrieving grants with revokerId = null or revokerId =
     * existing revokerId.
     * 
     * @test.name nullPlusRevokerFilter
     * @test.id nullPlusRevokerFilter
     * @test.input
     * @test.expected: result with all grants having revokerId = null or
     *                 revokerId = existing revokerId.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullPlusRevokerFilterCQL() throws Exception {
        int expectedGrantCount =
            NON_REVOKED_GRANT_COUNT
                + Integer.parseInt(revokerSysadmins.get(0).get("grantCount"));
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_REVOKED_BY + "\"=\"\" or " + "\"" + FILTER_REVOKED_BY
            + "\"=" + revokerSysadmins.get(0).get("userId") + ") and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully retrieving grants with revocationDate = null.
     * 
     * @test.name nullRevocationDateFilter
     * @test.id nullRevocationDateFilter
     * @test.input
     * @test.expected: result with all grants having revocationDate = null.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullRevocationDateFilter() throws Exception {
        int expectedGrantCount = NON_REVOKED_GRANT_COUNT;
        String filterXml =
            "<param>" + getFilter(FILTER_REVOCATION_DATE_FROM, null)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);

        filterXml =
            "<param>" + getFilter(FILTER_REVOCATION_DATE_TO, null)
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
    }

    /**
     * Test successfully retrieving grants with revocationDate = null.
     * 
     * @test.name nullRevocationDateFilter
     * @test.id nullRevocationDateFilter
     * @test.input
     * @test.expected: result with all grants having revocationDate = null.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullRevocationDateFilterCQL() throws Exception {
        int expectedGrantCount = NON_REVOKED_GRANT_COUNT;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_REVOCATION_DATE + "\"=\"\" and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully retrieving grants with revocationDate = null or
     * revocationDate > parttimeDate.
     * 
     * @test.name nullPlusRevocationDateFilter
     * @test.id nullPlusRevocationDateFilter
     * @test.input
     * @test.expected: result with all grants having revocationDate = null or
     *                 revocationDate > parttimeDate.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullPlusRevocationDateFilter() throws Exception {
        int expectedGrantCount =
            NON_REVOKED_GRANT_COUNT + REVOKED_FROM_PARTTIME_GRANTS;
        String filterXml =
            "<param>"
                + getFilter(FILTER_REVOCATION_DATE_FROM, null)
                + getFilter(FILTER_REVOCATION_DATE_FROM, partTimeParameters
                    .get("time"))
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        String result = null;
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);

        expectedGrantCount =
            NON_REVOKED_GRANT_COUNT + REVOKED_TO_PARTTIME_GRANTS;
        filterXml =
            "<param>"
                + getFilter(FILTER_REVOCATION_DATE_TO, null)
                + getFilter(FILTER_REVOCATION_DATE_TO, partTimeParameters
                    .get("time"))
                + getFilter(FILTER_CREATION_DATE_FROM, startTime) + "</param>";
        try {
            result = retrieveGrants(filterXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidGrantList(result);
        assertNodeCount(result, "/grant-list/grant", expectedGrantCount);
    }

    /**
     * Test successfully retrieving grants with revocationDate = null or
     * revocationDate > parttimeDate.
     * 
     * @test.name nullPlusRevocationDateFilter
     * @test.id nullPlusRevocationDateFilter
     * @test.input
     * @test.expected: result with all grants having revocationDate = null or
     *                 revocationDate > parttimeDate.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullPlusRevocationDateFilterCQL() throws Exception {
        int expectedGrantCount =
            NON_REVOKED_GRANT_COUNT + REVOKED_FROM_PARTTIME_GRANTS;
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\""
            + FILTER_REVOCATION_DATE + "\"=\"\" or " + "\""
            + FILTER_REVOCATION_DATE + "\">=\""
            + partTimeParameters.get("time") + "\") and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test declining retrieving grants with filterXml = null.
     * 
     * @test.name nullFilterDecline
     * @test.id nullFilterDecline
     * @test.input null
     * @test.expected: MissingMethodParameterException
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void nullFilterDecline() throws Exception {
        try {
            retrieveGrants((String) null);
            EscidocRestSoapTestsBase.failMissingException(
                "Retrieving grants without providing filter params"
                    + " not declined. ", MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "Retrieving grants without providing filter params"
                    + "not declined properly. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining retrieving grants with corrupt filterXml.
     * 
     * @test.name corruptFilterDecline
     * @test.id corruptFilterDecline
     * @test.input corrupt xml
     * @test.expected: XmlCorruptedException
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void corruptFilterDecline() throws Exception {
        String filterXml = "<Corrupt XML data";
        try {
            retrieveGrants(filterXml);
            EscidocRestSoapTestsBase.failMissingException(
                "Retrieving grants with providing corrupt filter"
                    + " not declined. ", XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestsBase
                .assertExceptionType(
                    "Retrieving grants with providing corrupt filter"
                        + "not declined properly. ",
                    XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining retrieving grants with invalid filter-xml.
     * 
     * @test.name invalidFilterDecline
     * @test.id invalidFilterDecline
     * @test.input invalidXml with wrong filter
     * @test.expected: XmlSchemaValidationException
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void invalidFilterDecline() throws Exception {
        try {
            retrieveGrants("<param><created-by>some value</created-by></param>");
            EscidocRestSoapTestsBase.failMissingException(
                "Retrieving grants with providing corrupt filter"
                    + " not declined. ", XmlSchemaValidationException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "Retrieving grants with providing corrupt filter"
                    + "not declined properly. ",
                XmlSchemaValidationException.class, e);
        }
    }

    /**
     * Test declining retrieving grants with invalid filter-xml.
     * 
     * @test.name invalidFilterDecline
     * @test.id invalidFilterDecline
     * @test.input invalidXml with wrong filter
     * @test.expected: XmlSchemaValidationException
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void invalidFilterDeclineCQL() throws Exception {
        try {
            final Map<String, String[]> filterParams =
                new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
                + NAME_CREATED_BY + "\"=\"Some value\"" });
            retrieveGrants(filterParams);
            EscidocRestSoapTestsBase.failMissingException(
                "Retrieving grants with providing corrupt filter"
                    + " not declined. ", InvalidSearchQueryException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "Retrieving grants with providing corrupt filter"
                    + "not declined properly. ",
                InvalidSearchQueryException.class, e);
        }
    }

    /**
     * Test declining retrieving grants with same datefilter twice.
     * 
     * @test.name dateFilterTwiceDecline
     * @test.id dateFilterTwiceDecline
     * @test.input date Filter Twice
     * @test.expected: SqlDatabaseSystemException
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void dateFilterTwiceDecline() throws Exception {
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_FROM, "2009-04-22")
                + getFilter(FILTER_CREATION_DATE_FROM, "2009-04-23")
                + "</param>";
        try {
            retrieveGrants(filterXml);
            EscidocRestSoapTestsBase.failMissingException(
                "Retrieving grants with date filter twice" + " not declined. ",
                SqlDatabaseSystemException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "Retrieving grants with date filter twice"
                    + "not declined properly. ",
                SqlDatabaseSystemException.class, e);
        }
    }

    /**
     * Test declining retrieving grants with same datefilter twice.
     * 
     * @test.name dateFilterTwiceDecline1
     * @test.id dateFilterTwiceDecline1
     * @test.input date Filter Twice
     * @test.expected: SqlDatabaseSystemException
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void dateFilterTwiceDecline1() throws Exception {
        String filterXml =
            "<param>" + getFilter(FILTER_CREATION_DATE_TO, "2009-04-22")
                + getFilter(FILTER_CREATION_DATE_TO, "2009-04-23") + "</param>";
        try {
            retrieveGrants(filterXml);
            EscidocRestSoapTestsBase.failMissingException(
                "Retrieving grants with date filter twice" + " not declined. ",
                SqlDatabaseSystemException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "Retrieving grants with date filter twice"
                    + "not declined properly. ",
                SqlDatabaseSystemException.class, e);
        }
    }

    /**
     * Test declining retrieving grants with same datefilter twice.
     * 
     * @test.name dateFilterTwiceDecline2
     * @test.id dateFilterTwiceDecline2
     * @test.input date Filter Twice
     * @test.expected: SqlDatabaseSystemException
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void dateFilterTwiceDecline2() throws Exception {
        String filterXml =
            "<param>" + getFilter(FILTER_REVOCATION_DATE_FROM, "2009-04-22")
                + getFilter(FILTER_REVOCATION_DATE_FROM, "2009-04-23")
                + "</param>";
        try {
            retrieveGrants(filterXml);
            EscidocRestSoapTestsBase.failMissingException(
                "Retrieving grants with date filter twice" + " not declined. ",
                SqlDatabaseSystemException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "Retrieving grants with date filter twice"
                    + "not declined properly. ",
                SqlDatabaseSystemException.class, e);
        }
    }

    /**
     * Test declining retrieving grants with same datefilter twice.
     * 
     * @test.name dateFilterTwiceDecline3
     * @test.id dateFilterTwiceDecline3
     * @test.input date Filter Twice
     * @test.expected: SqlDatabaseSystemException
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void dateFilterTwiceDecline3() throws Exception {
        String filterXml =
            "<param>" + getFilter(FILTER_REVOCATION_DATE_TO, "2009-04-22")
                + getFilter(FILTER_REVOCATION_DATE_TO, "2009-04-23")
                + "</param>";
        try {
            retrieveGrants(filterXml);
            EscidocRestSoapTestsBase.failMissingException(
                "Retrieving grants with date filter twice" + " not declined. ",
                SqlDatabaseSystemException.class);
        }
        catch (final Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                "Retrieving grants with date filter twice"
                    + "not declined properly. ",
                SqlDatabaseSystemException.class, e);
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
            result = retrieveGrants(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.failException(e);
        }
        assertXmlValidSrwResponse(result);        
    }

    /**
     * Create systemadmin-users and return userId + handle in hashmap.
     * 
     * @param count
     *            number of sysadmins to create
     * @return HashMap containing sysadmins with key=userId and value=handle
     * @throws Exception
     *             If anything fails.
     */
    private ArrayList<HashMap<String, String>> createSysadmins(final int count)
        throws Exception {
        ArrayList<HashMap<String, String>> returnList =
            new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < count; i++) {
            // create user + grant
            Document user =
                createSuccessfully("escidoc_useraccount_for_create1.xml");
            final String userId = getObjidValue(user);
            doTestCreateGrant(null, userId, null,
                ROLE_HREF_SYSTEM_ADMINISTRATOR, null);

            // update password
            final String lastModificationDate =
                getLastModificationDateValue(user);
            final String taskParamXML =
                "<param last-modification-date=\"" + lastModificationDate
                    + "\" ><password>" + SYSADMIN_PASSWORD
                    + "</password> </param>";
            updatePassword(userId, taskParamXML);

            // login to get handle
            final String loginName =
                selectSingleNode(user, XPATH_USER_ACCOUNT_LOGINNAME)
                    .getTextContent();
            final String handle = login(loginName, SYSADMIN_PASSWORD, true);

            // save userId + handle
            HashMap<String, String> valueMap = new HashMap<String, String>();
            valueMap.put("userId", userId);
            valueMap.put("handle", handle);
            returnList.add(valueMap);
        }
        return returnList;
    }
}
