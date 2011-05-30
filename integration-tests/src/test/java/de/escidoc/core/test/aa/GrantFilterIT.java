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
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.aa.GrantClient;
import de.escidoc.core.test.security.client.PWCallback;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test suite for the GrantFilter .
 *
 * @author Michael Hoppe
 */
public class GrantFilterIT extends GrantTestBase {

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

    public static final String XPATH_SRW_GRANT_LIST_GRANT = XPATH_SRW_RESPONSE_OBJECT + NAME_GRANT;

    private static HashMap<String, String> partTimeParameters = new HashMap<String, String>();

    private static ArrayList<HashMap<String, String>> creatorSysadmins = new ArrayList<HashMap<String, String>>();

    private static ArrayList<HashMap<String, String>> revokerSysadmins = new ArrayList<HashMap<String, String>>();

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

    public GrantFilterIT() throws Exception {
        super(USER_ACCOUNT_HANDLER_CODE);
        userAttributeTestBase = new UserAttributeTestBase() {
        };
    }

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
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
     * @throws Exception If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
        methodCounter++;
    }

    /**
     * insert grants into system for the tests.
     *
     * @throws Exception If anything fails.
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
                final String createdXml = doTestCreateContainer(PWCallback.DEFAULT_HANDLE, null);
                objects.add(Constants.CONTAINER_BASE_URI + "/" + getObjidValue(createdXml));
            }

            // create items
            for (int i = 0; i < NUM_ITEMS; i++) {
                final String createdXml = doTestCreateItem(PWCallback.DEFAULT_HANDLE, null);
                objects.add(Constants.ITEM_BASE_URI + "/" + getObjidValue(createdXml));
            }

            // create users
            for (int i = 0; i < NUM_USERS; i++) {
                Document user = createSuccessfully("escidoc_useraccount_for_create1.xml");
                String userId = getObjidValue(user);
                userAttributeTestBase.createAttribute(userId, "<attribute xmlns="
                    + "\"http://www.escidoc.de/schemas/attributes/0.1\"" + " name=\"o\">"
                    + EscidocTestBase.ORGANIZATIONAL_UNIT_ID1 + "</attribute>");
                userIds.add(userId);
            }

            startTime = new DateTime(System.currentTimeMillis()).toString();

            // create different roles for each user and group
            int roleCounter = 0;
            for (String role : ROLES) {
                if (roleCounter == PART_TIME_ROLE_NUMBER) {
                    partTimeParameters.put("time", new DateTime(System.currentTimeMillis()).toString());
                }
                roleCounter++;
                for (String object : objects) {
                    String createHandle = creatorSysadmins.get(0).get("handle");
                    String revokeHandle = revokerSysadmins.get(0).get("handle");
                    int i = 0;
                    String grantXml;
                    setClient((GrantClient) getClient(USER_ACCOUNT_HANDLER_CODE));
                    for (String userId : userIds) {
                        grantXml = doTestCreateGrant(createHandle, userId, object, role, null);
                        if (creatorSysadmins.get(0).get("grantCount") == null) {
                            creatorSysadmins.get(0).put("grantCount", "0");
                        }
                        int grantCount = Integer.parseInt(creatorSysadmins.get(0).get("grantCount")) + 1;
                        creatorSysadmins.get(0).put("grantCount", Integer.toString(grantCount));
                        if (i % 2 == 0) {
                            Document createdDocument = EscidocAbstractTest.getDocument(grantXml);
                            String grantId = getObjidValue(createdDocument);
                            String lastModificationDate = getLastModificationDateValue(createdDocument);
                            String taskParamXML = "<param last-modification-date=\"" + lastModificationDate + "\" />";
                            revokeGrant(userId, grantId, taskParamXML, revokeHandle);
                            if (revokerSysadmins.get(0).get("grantCount") == null) {
                                revokerSysadmins.get(0).put("grantCount", "0");
                            }
                            grantCount = Integer.parseInt(revokerSysadmins.get(0).get("grantCount")) + 1;
                            revokerSysadmins.get(0).put("grantCount", Integer.toString(grantCount));
                        }
                        i++;
                    }
                    createHandle = creatorSysadmins.get(1).get("handle");
                    revokeHandle = revokerSysadmins.get(1).get("handle");
                    setClient((GrantClient) getClient(USER_GROUP_HANDLER_CODE));
                    for (String groupId : GROUP_IDS) {
                        grantXml = doTestCreateGrant(createHandle, groupId, object, role, null);
                        if (creatorSysadmins.get(1).get("grantCount") == null) {
                            creatorSysadmins.get(1).put("grantCount", "0");
                        }
                        int grantCount = Integer.parseInt(creatorSysadmins.get(1).get("grantCount")) + 1;
                        creatorSysadmins.get(1).put("grantCount", Integer.toString(grantCount));
                        if (i % 2 == 0) {
                            Document createdDocument = EscidocAbstractTest.getDocument(grantXml);
                            String grantId = getObjidValue(createdDocument);
                            String lastModificationDate = getLastModificationDateValue(createdDocument);
                            String taskParamXML = "<param last-modification-date=\"" + lastModificationDate + "\" />";
                            revokeGrant(groupId, grantId, taskParamXML, revokeHandle);
                            if (revokerSysadmins.get(1).get("grantCount") == null) {
                                revokerSysadmins.get(1).put("grantCount", "0");
                            }
                            grantCount = Integer.parseInt(revokerSysadmins.get(1).get("grantCount")) + 1;
                            revokerSysadmins.get(1).put("grantCount", Integer.toString(grantCount));
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
     * @throws Exception If anything fails.
     */
    @Test
    public void userIdFilterCQL() throws Exception {
        int expectedGrantCount = USER_GRANT_COUNT * 3;
        for (String userId : userIds) {
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(userId);
            allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);
            allowedValues.add(USER_GROUP_WITH_OU_LIST_ID);

            final Map<String, String[]> filterParams = new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_USER + "\"=" + userId + " and "
                + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
            filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

            String result = null;

            try {
                result = retrieveGrants(filterParams);
            }
            catch (final Exception e) {
                EscidocAbstractTest.failException(e);
            }
            assertXmlValidSrwResponse(result);
            assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
            assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        }
    }

    /**
     * Test successfully filtering grants for several userIds.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_USER + "\"=" + userId + " or " + "\""
            + FILTER_USER + "\"=" + userId1 + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for a groupId.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void groupIdFilterCQL() throws Exception {
        int expectedGrantCount = GROUP_GRANT_COUNT;
        for (String groupId : GROUP_IDS) {
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(groupId);
            allowedValues.add(USER_GROUP_WITH_GROUP_LIST_ID);

            final Map<String, String[]> filterParams = new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_GROUP + "\"=" + groupId + " and "
                + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
            filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

            String result = null;

            try {
                result = retrieveGrants(filterParams);
            }
            catch (final Exception e) {
                EscidocAbstractTest.failException(e);
            }
            assertXmlValidSrwResponse(result);
            if (groupId.equals(USER_GROUP_WITH_OU_LIST_ID)) {
                assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount * 2);
            }
            else {
                assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
            }
            assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        }
    }

    /**
     * Test successfully filtering grants for several groupIds.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_GROUP + "\"=" + groupId + " or " + "\""
            + FILTER_GROUP + "\"=" + groupId1 + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for a roleId.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void roleIdFilterCQL() throws Exception {
        int expectedGrantCount = ROLE_GRANT_COUNT;
        for (String roleId : ROLES) {
            roleId = getObjidFromHref(roleId);
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(roleId);

            final Map<String, String[]> filterParams = new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_ROLE + "\"=" + roleId + " and "
                + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
            filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

            String result = null;

            try {
                result = retrieveGrants(filterParams);
            }
            catch (final Exception e) {
                EscidocAbstractTest.failException(e);
            }
            assertXmlValidSrwResponse(result);
            assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
            assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/role", allowedValues, true);
        }
    }

    /**
     * Test successfully filtering grants for several roleIds.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_ROLE + "\"=" + roleId + " or " + "\""
            + FILTER_ROLE + "\"=" + roleId1 + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for a objectId.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void objectIdFilterCQL() throws Exception {
        int expectedGrantCount = OBJECT_GRANT_COUNT;
        for (String objectId : objects) {
            objectId = getObjidFromHref(objectId);
            List<String> allowedValues = new ArrayList<String>();
            allowedValues.add(objectId);

            final Map<String, String[]> filterParams = new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId
                + " and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
            filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

            String result = null;

            try {
                result = retrieveGrants(filterParams);
            }
            catch (final Exception e) {
                EscidocAbstractTest.failException(e);
            }
            assertXmlValidSrwResponse(result);
            assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
            assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues,
                true);
        }
    }

    /**
     * Test successfully filtering grants for several objectIds.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_ASSIGNED_ON + "\"=" + objectId + " or "
            + "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId1 + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\""
            + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revocationDateFrom.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void revocationDateFromFilterCQL() throws Exception {
        int expectedGrantCount = REVOKED_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_REVOCATION_DATE + "\">=\"" + startTime
            + "\" and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for revocationDateFrom.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void revocationDateFromFilter1CQL() throws Exception {
        int expectedGrantCount = 0;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_REVOCATION_DATE + "\">=\""
            + new DateTime(System.currentTimeMillis()).toString() + "\" and " + "\"" + FILTER_CREATION_DATE + "\">=\""
            + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for revocationDateTo.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void revocationDateToFilterCQL() throws Exception {
        int expectedGrantCount = 0;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_REVOCATION_DATE + "\"<=\"" + startTime
            + "\" and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for revocationDateTo.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void revocationDateToFilter1CQL() throws Exception {
        int expectedGrantCount = REVOKED_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_REVOCATION_DATE + "\"<=\""
            + new DateTime(System.currentTimeMillis()).toString() + "\" and " + "\"" + FILTER_CREATION_DATE + "\">=\""
            + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for grantedDateFrom.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void grantedDateFromFilterCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for grantedDateFrom.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void grantedDateFromFilter1CQL() throws Exception {
        int expectedGrantCount = 0;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\""
            + new DateTime(System.currentTimeMillis()).toString() + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for grantedDateTo.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void grantedDateToFilterCQL() throws Exception {
        int expectedGrantCount = 0;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\"<=\"" + startTime
            + "\" and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for grantedDateTo.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void grantedDateToFilter1CQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\"<=\""
            + new DateTime(System.currentTimeMillis()).toString() + "\" and " + "\"" + FILTER_CREATION_DATE + "\">=\""
            + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully filtering grants for creatorId.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void creatorIdFilterCQL() throws Exception {
        int expectedGrantCount = Integer.parseInt(creatorSysadmins.get(0).get("grantCount"));
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(creatorSysadmins.get(0).get("userId"));

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATED_BY + "\"="
            + creatorSysadmins.get(0).get("userId") + " and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/created-by", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for creatorIds.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void creatorIdsFilterCQL() throws Exception {
        int expectedGrantCount =
            Integer.parseInt(creatorSysadmins.get(0).get("grantCount"))
                + Integer.parseInt(creatorSysadmins.get(1).get("grantCount"));
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(creatorSysadmins.get(0).get("userId"));
        allowedValues.add(creatorSysadmins.get(1).get("userId"));

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_CREATED_BY + "\"="
            + creatorSysadmins.get(0).get("userId") + " or " + "\"" + FILTER_CREATED_BY + "\"="
            + creatorSysadmins.get(1).get("userId") + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/created-by", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revokerId.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void revokerIdFilterCQL() throws Exception {
        int expectedGrantCount = Integer.parseInt(revokerSysadmins.get(0).get("grantCount"));
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(revokerSysadmins.get(0).get("userId"));
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_REVOKED_BY + "\"="
            + revokerSysadmins.get(0).get("userId") + " and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/revoked-by", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revokerIds.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void revokerIdsFilterCQL() throws Exception {
        int expectedGrantCount =
            Integer.parseInt(revokerSysadmins.get(0).get("grantCount"))
                + Integer.parseInt(revokerSysadmins.get(1).get("grantCount"));
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(revokerSysadmins.get(0).get("userId"));
        allowedValues.add(revokerSysadmins.get(1).get("userId"));

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_REVOKED_BY + "\"="
            + revokerSysadmins.get(0).get("userId") + " or " + "\"" + FILTER_REVOKED_BY + "\"="
            + revokerSysadmins.get(1).get("userId") + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/revoked-by", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userId and objectId.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_USER + "\"=" + userId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userIds and objectIds.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_USER + "\"=" + userId + " or " + "\""
            + FILTER_USER + "\"=" + userId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"=" + objectId + " or " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId1 + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userId,objectId and roleId.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_USER + "\"=" + userId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\"" + FILTER_ROLE + "\"=" + roleId + " and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userIds,objectIds and roleIds.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_USER + "\"=" + userId + " or " + "\""
            + FILTER_USER + "\"=" + userId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"=" + objectId + " or " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId1 + ") and " + "(\"" + FILTER_ROLE + "\"=" + roleId + " or " + "\""
            + FILTER_ROLE + "\"=" + roleId1 + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for creatorId and objectId.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATED_BY + "\"=" + creatorId + " and "
            + "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\"" + FILTER_CREATION_DATE + "\">=\""
            + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/created-by", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for creatorIds and objectIds.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_CREATED_BY + "\"=" + creatorId + " or "
            + "\"" + FILTER_CREATED_BY + "\"=" + creatorId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"=" + objectId
            + " or " + "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId1 + ") and " + "\"" + FILTER_CREATION_DATE
            + "\">=\"" + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/created-by", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revokerId and objectId.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_REVOKED_BY + "\"=" + revokerId + " and "
            + "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\"" + FILTER_CREATION_DATE + "\">=\""
            + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/revoked-by", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for revokerIds and objectIds.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_REVOKED_BY + "\"=" + revokerId + " or "
            + "\"" + FILTER_REVOKED_BY + "\"=" + revokerId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"=" + objectId
            + " or " + "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId1 + ") and " + "\"" + FILTER_CREATION_DATE
            + "\">=\"" + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/revoked-by", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for groupId and objectId.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_GROUP + "\"=" + groupId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for groupIds and objectIds.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_GROUP + "\"=" + groupId + " or " + "\""
            + FILTER_GROUP + "\"=" + groupId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"=" + objectId + " or "
            + "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId1 + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\""
            + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for groupId,objectId and roleId.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_GROUP + "\"=" + groupId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\"" + FILTER_ROLE + "\"=" + roleId + " and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for groupIds,objectIds and roleIds.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_GROUP + "\"=" + groupId + " or " + "\""
            + FILTER_GROUP + "\"=" + groupId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"=" + objectId + " or "
            + "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId1 + ") and " + "(\"" + FILTER_ROLE + "\"=" + roleId + " or "
            + "\"" + FILTER_ROLE + "\"=" + roleId1 + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userId and objectId created in the first half of the prepare-method.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_USER + "\"=" + userId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" and " + "\"" + FILTER_CREATION_DATE + "\"<=\"" + partTimeParameters.get("time") + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userIds and objectIds created in the first half of the prepare-method.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_USER + "\"=" + userId + " or " + "\""
            + FILTER_USER + "\"=" + userId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"=" + objectId + " or " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId1 + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" and " + "\"" + FILTER_CREATION_DATE + "\"<=\"" + partTimeParameters.get("time") + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userId,objectId and roleId created in the first half of the
     * prepare-method.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_USER + "\"=" + userId + " and " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId + " and " + "\"" + FILTER_ROLE + "\"=" + roleId + " and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" and " + "\"" + FILTER_CREATION_DATE + "\"<=\""
            + partTimeParameters.get("time") + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for userIds,objectIds and roleIds created in the first half of the
     * prepare-method.
     *
     * @throws Exception If anything fails.
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

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_USER + "\"=" + userId + " or " + "\""
            + FILTER_USER + "\"=" + userId1 + ") and " + "(\"" + FILTER_ASSIGNED_ON + "\"=" + objectId + " or " + "\""
            + FILTER_ASSIGNED_ON + "\"=" + objectId1 + ") and " + "(\"" + FILTER_ROLE + "\"=" + roleId + " or " + "\""
            + FILTER_ROLE + "\"=" + roleId1 + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime + "\" and "
            + "\"" + FILTER_CREATION_DATE + "\"<=\"" + partTimeParameters.get("time") + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/role", allowedValues, true);
    }

    /**
     * Test successfully filtering grants for objectId revoked in the first half of the prepare-method.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void objectIdFirstpartRevocationFilterCQL() throws Exception {
        int expectedGrantCount = OBJECT_REVOCATION_FIRSTPART_GRANT_COUNT;
        Iterator<String> objectIdIter = objects.iterator();
        String objectId = getObjidFromHref(objectIdIter.next());
        List<String> allowedValues = new ArrayList<String>();
        allowedValues.add(objectId);

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_ASSIGNED_ON + "\"=" + objectId + " and "
            + "\"" + FILTER_REVOCATION_DATE + "\">=\"" + startTime + "\" and " + "\"" + FILTER_REVOCATION_DATE
            + "\"<=\"" + partTimeParameters.get("time") + "\"" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", allowedValues, true);
    }

    /**
     * Test successfully sorting grants for userId ascending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void userIdSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_USER + "\"/sort.ascending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to",
            "parent::node()[@resource='user-account']", true, true);
    }

    /**
     * Test successfully sorting grants for userId descending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void userIdSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_USER + "\"/sort.descending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to",
            "parent::node()[@resource='user-account']", false, true);
    }

    /**
     * Test successfully sorting grants for groupId ascending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void groupIdSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_GROUP + "\"/sort.ascending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to",
            "parent::node()[@resource='user-group']", true, true);
    }

    /**
     * Test successfully sorting grants for groupId descending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void groupIdSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_GROUP + "\"/sort.descending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to",
            "parent::node()[@resource='user-group']", false, true);
    }

    /**
     * Test successfully sorting grants for roleId ascending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void roleIdSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_ROLE + "\"/sort.ascending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/role", null, true, true);
    }

    /**
     * Test successfully sorting grants for roleId descending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void roleIdSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_ROLE + "\"/sort.descending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/role", null, false, true);
    }

    /**
     * Test successfully sorting grants for objectId ascending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void objectIdSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_ASSIGNED_ON + "\"/sort.ascending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", null, true, true);
    }

    /**
     * Test successfully sorting grants for objectId descending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void objectIdSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_ASSIGNED_ON + "\"/sort.descending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/assigned-on", null, false, true);
    }

    /**
     * Test successfully sorting grants for creatorId ascending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void creatorIdSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_CREATED_BY + "\"/sort.ascending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/created-by", null, true, true);
    }

    /**
     * Test successfully sorting grants for creatorId descending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void creatorIdSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_CREATED_BY + "\"/sort.descending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/created-by", null, false, true);
    }

    /**
     * Test successfully sorting grants for revokerId ascending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void revokerIdSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_REVOKED_BY + "\"/sort.ascending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/revoked-by", null, true, true);
    }

    /**
     * Test successfully sorting grants for revokerId descending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void revokerIdSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_REVOKED_BY + "\"/sort.descending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/revoked-by", null, false, true);
    }

    /**
     * Test successfully sorting grants for revocationDate ascending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void revocationDateSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_REVOCATION_DATE + "\"/sort.ascending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/revocation-date", null, true, true);
    }

    /**
     * Test successfully sorting grants for revocationDate descending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void revocationDateSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_REVOCATION_DATE + "\"/sort.descending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/revocation-date", null, false, true);
    }

    /**
     * Test successfully sorting grants for grantedDateFrom ascending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void grantedDateFromSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_GRANTED_FROM + "\"/sort.ascending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-from", null, true, true);
    }

    /**
     * Test successfully sorting grants for grantedDateFrom descending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void grantedDateFromSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_GRANTED_FROM + "\"/sort.descending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-from", null, false, true);
    }

    /**
     * Test successfully sorting grants for grantedDateTo ascending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void grantedDateToSortFilterAscCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_CREATION_DATE + "\"/sort.ascending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/creation-date", null, true, true);
    }

    /**
     * Test successfully sorting grants for grantedDateTo descending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void grantedDateToSortFilterDescCQL() throws Exception {
        int expectedGrantCount = TOTAL_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_GRANTED_TO + "\"/sort.descending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertSorted(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", null, false, true);
    }

    /**
     * Test successfully sorting grants for grantedDateTo descending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void offsetLimitFilterCQL() throws Exception {
        int expectedGrantCount = USER_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\" sortby " + "\"" + FILTER_USER + "\"/sort.ascending" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { String.valueOf(USER_GRANT_COUNT) });

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
            catch (final Exception e) {
                EscidocAbstractTest.failException(e);
            }
            assertXmlValidSrwResponse(result);
            assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
            assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
            i++;
        }
    }

    /**
     * Test successfully retrieving grants with userId = null.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void nullUserFilterCQL() throws Exception {
        int expectedGrantCount = GROUP_GRANT_COUNT * GROUP_IDS.size();
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_USER + "\"=\"\" and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", GROUP_IDS, true);
    }

    /**
     * Test successfully retrieving grants with userId = null or userId = existing userId.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void nullPlusUserFilterCQL() throws Exception {
        int expectedGrantCount = GROUP_GRANT_COUNT * GROUP_IDS.size() + USER_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_USER + "\"=\"\" or " + "\""
            + FILTER_USER + "\"=" + userIds.get(0) + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        List<String> allowedValues = new ArrayList<String>();
        allowedValues.addAll(GROUP_IDS);
        allowedValues.add(userIds.get(0));
        String result = null;
        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
    }

    /**
     * Test successfully retrieving grants with groupId = null.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void nullGroupFilterCQL() throws Exception {
        int expectedGrantCount = USER_GRANT_COUNT * userIds.size();
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_GROUP + "\"=\"\" and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", userIds, true);
    }

    /**
     * Test successfully retrieving grants with groupId = null or groupId = existing groupId.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void nullPlusGroupFilterCQL() throws Exception {
        int expectedGrantCount = USER_GRANT_COUNT * userIds.size() + GROUP_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_GROUP + "\"=\"\" or " + "\""
            + FILTER_GROUP + "\"=" + GROUP_IDS.get(0) + ") and " + "\"" + FILTER_CREATION_DATE + "\">=\"" + startTime
            + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        List<String> allowedValues = new ArrayList<String>();
        allowedValues.addAll(userIds);
        allowedValues.add(GROUP_IDS.get(0));
        String result = null;
        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
        assertAllowedXpathValues(result, XPATH_SRW_GRANT_LIST_GRANT + "/properties/granted-to", allowedValues, true);
    }

    /**
     * Test successfully retrieving grants with revokerId = null.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void nullRevokerFilterCQL() throws Exception {
        int expectedGrantCount = NON_REVOKED_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_REVOKED_BY + "\"=\"\" and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully retrieving grants with revokerId = null or revokerId = existing revokerId.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void nullPlusRevokerFilterCQL() throws Exception {
        int expectedGrantCount = NON_REVOKED_GRANT_COUNT + Integer.parseInt(revokerSysadmins.get(0).get("grantCount"));
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_REVOKED_BY + "\"=\"\" or " + "\""
            + FILTER_REVOKED_BY + "\"=" + revokerSysadmins.get(0).get("userId") + ") and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully retrieving grants with revocationDate = null.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void nullRevocationDateFilterCQL() throws Exception {
        int expectedGrantCount = NON_REVOKED_GRANT_COUNT;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_REVOCATION_DATE + "\"=\"\" and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test successfully retrieving grants with revocationDate = null or revocationDate > parttimeDate.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void nullPlusRevocationDateFilterCQL() throws Exception {
        int expectedGrantCount = NON_REVOKED_GRANT_COUNT + REVOKED_FROM_PARTTIME_GRANTS;
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "(\"" + FILTER_REVOCATION_DATE + "\"=\"\" or " + "\""
            + FILTER_REVOCATION_DATE + "\">=\"" + partTimeParameters.get("time") + "\") and " + "\""
            + FILTER_CREATION_DATE + "\">=\"" + startTime + "\"" });
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "1000" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
        assertNodeCount(result, XPATH_SRW_GRANT_LIST_GRANT, expectedGrantCount);
    }

    /**
     * Test declining retrieving grants with invalid filter-xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void invalidFilterDeclineCQL() throws Exception {
        try {
            final Map<String, String[]> filterParams = new HashMap<String, String[]>();

            filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + NAME_CREATED_BY + "\"=\"Some value\"" });
            retrieveGrants(filterParams);
            EscidocAbstractTest.failMissingException("Retrieving grants with providing corrupt filter"
                + " not declined. ", InvalidSearchQueryException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Retrieving grants with providing corrupt filter"
                + "not declined properly. ", InvalidSearchQueryException.class, e);
        }
    }

    /**
     * Test successful retrieving a list of existing Grant resources. Test if maximumRecords=0 delivers 0 Grant
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void emptyFilterZeroMaximumRecords() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "0" });

        String result = null;

        try {
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of Grants failed. ", e);
        }

        assertXmlValidSrwResponse(result);
        Document retrievedDocument = EscidocAbstractTest.getDocument(result);
        NodeList resultNodes = selectNodeList(retrievedDocument, XPATH_SRW_GRANT_LIST_GRANT);
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
            result = retrieveGrants(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }

    /**
     * Create systemadmin-users and return userId + handle in hashmap.
     *
     * @param count number of sysadmins to create
     * @return HashMap containing sysadmins with key=userId and value=handle
     * @throws Exception If anything fails.
     */
    private ArrayList<HashMap<String, String>> createSysadmins(final int count) throws Exception {
        ArrayList<HashMap<String, String>> returnList = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < count; i++) {
            // create user + grant
            Document user = createSuccessfully("escidoc_useraccount_for_create1.xml");
            final String userId = getObjidValue(user);
            doTestCreateGrant(null, userId, null, ROLE_HREF_SYSTEM_ADMINISTRATOR, null);

            // update password
            final String lastModificationDate = getLastModificationDateValue(user);
            final String taskParamXML =
                "<param last-modification-date=\"" + lastModificationDate + "\" ><password>" + SYSADMIN_PASSWORD
                    + "</password> </param>";
            updatePassword(userId, taskParamXML);

            // login to get handle
            final String loginName = selectSingleNode(user, XPATH_USER_ACCOUNT_LOGINNAME).getTextContent();
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
