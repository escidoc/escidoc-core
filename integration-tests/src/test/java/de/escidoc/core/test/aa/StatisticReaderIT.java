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

import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import de.escidoc.core.test.security.client.PWCallback;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Test suite for testing access-rights to statistic-reports.
 *
 * @author Michael Hoppe
 */
@RunWith(Parameterized.class)
public class StatisticReaderIT extends GrantTestBase {

    /**
     * Initializes test-class with data.
     *
     * @return Collection with data.
     */
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { USER_ACCOUNT_HANDLER_CODE, PWCallback.ID_PREFIX + PWCallback.TEST_HANDLE },
            { USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_GROUP_LIST_ID },
            { USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_USER_LIST_ID },
            { USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_OU_LIST_ID },
            { USER_GROUP_HANDLER_CODE, USER_GROUP_WITH_EXTERNAL_SELECTOR } });
    }

    private static final String HANDLE = PWCallback.TEST_HANDLE;

    private static final String LOGINNAME = HANDLE;

    private static final String PASSWORD = PWCallback.PASSWORD;

    protected static String grantCreationUserOrGroupId = null;

    private static final int NUM_REPORT_DEFINITIONS = 3;

    private static final int NUM_STATISTIC_DATA = 4;

    private static int methodCounter = 0;

    private static final String SCOPE_ID = STATISTIC_SCOPE_ID;

    private static String newScopeId = null;

    private static String newAggregationDefinitionId = null;

    private static String aggregationDefinitionId = null;

    private static String[] newReportDefinitionIds = new String[NUM_REPORT_DEFINITIONS];

    private static String[] reportDefinitionIds = new String[NUM_REPORT_DEFINITIONS];

    private static final String SCOPE_TEMPLATE_NAME = "escidoc_scope3.xml";

    private static final String AGGREGATION_DEFINITION_TEMPLATE_NAME = "escidoc_aggregation_definition1.xml";

    private static final String REPORT_DEFINITION_TEMPLATE_NAME =
        "escidoc_report_definition_for_report_test${COUNTER}.xml";

    private static final String STATISTIC_DATA_TEMPLATE_NAME = "escidoc_statistic_data1.xml";

    private static final String REPORT_PARAMETERS_TEMPLATE_NAME = "escidoc_report_parameters0.xml";

    private static final String PREPROCESSING_URL =
        "jmx-console/HtmlAdaptor?action=invokeOp" + "&name=eSciDocCore%3Aname%3DStatisticPreprocessorService"
            + "&methodIndex=${methodIndex}&arg0=";

    private static final String STATISTIC_PREPROCESSR_METHOD_INDEX = "0";

    private static final Pattern METHOD_INDEX_PATTERN = Pattern.compile("\\$\\{methodIndex\\}");

    /**
     * The constructor.
     *
     * @param handlerCode   handlerCode of either UserAccountHandler or UserGroupHandler.
     * @param userOrGroupId userOrGroupId for grantCreation.
     * @throws Exception If anything fails.
     */
    public StatisticReaderIT(final int handlerCode, final String userOrGroupId) throws Exception {
        super(handlerCode);
        grantCreationUserOrGroupId = userOrGroupId;
    }

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        //revoke all Grants
        revokeAllGrants(grantCreationUserOrGroupId);
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
        if (methodCounter == getTestAnnotationsCount()) {
            revokeAllGrants(grantCreationUserOrGroupId);
            methodCounter = 0;
        }
    }

    /**
     * insert data into system for the tests.
     *
     * @throws Exception If anything fails.
     */
    private void prepare() throws Exception {
        // create new scope.
        newScopeId = doTestCreateScope(PWCallback.DEFAULT_HANDLE, SCOPE_TEMPLATE_NAME, null);

        // create aggregationDefinition in new scope
        newAggregationDefinitionId =
            doTestCreateAggregationDefinition(PWCallback.DEFAULT_HANDLE, AGGREGATION_DEFINITION_TEMPLATE_NAME,
                newScopeId, null);

        // create reportDefinitions in new scope
        for (int i = 0; i < NUM_REPORT_DEFINITIONS; i++) {
            newReportDefinitionIds[i] =
                doTestCreateReportDefinition(PWCallback.DEFAULT_HANDLE, REPORT_DEFINITION_TEMPLATE_NAME.replaceAll(
                    "\\$\\{COUNTER\\}", "" + (i + 1)), newScopeId, newAggregationDefinitionId, null);
        }

        // create statistic_data-records as system-admin in new scope
        for (int i = 0; i < NUM_STATISTIC_DATA; i++) {
            doTestCreateStatisticData(PWCallback.DEFAULT_HANDLE, STATISTIC_DATA_TEMPLATE_NAME, newScopeId, null);
        }

        // create aggregationDefinition in scope 3
        aggregationDefinitionId =
            doTestCreateAggregationDefinition(PWCallback.DEFAULT_HANDLE, AGGREGATION_DEFINITION_TEMPLATE_NAME,
                SCOPE_ID, null);

        // create reportDefinitions in scope 3
        for (int i = 0; i < NUM_REPORT_DEFINITIONS; i++) {
            reportDefinitionIds[i] =
                doTestCreateReportDefinition(PWCallback.DEFAULT_HANDLE, REPORT_DEFINITION_TEMPLATE_NAME.replaceAll(
                    "\\$\\{COUNTER\\}", "" + (i + 1)), SCOPE_ID, aggregationDefinitionId, null);
        }

        // create statistic_data-records as in scope 3
        for (int i = 0; i < NUM_STATISTIC_DATA; i++) {
            doTestCreateStatisticData(PWCallback.DEFAULT_HANDLE, STATISTIC_DATA_TEMPLATE_NAME, SCOPE_ID, null);
        }

        // trigger preprocessing of statistic_data
        triggerPreprocessing(STATISTIC_PREPROCESSR_METHOD_INDEX);

    }

    /**
     * triggers preprocessing.
     *
     * @param methodIndex methodIndex
     * @throws Exception If anything fails.
     */
    private void triggerPreprocessing(final String methodIndex) throws Exception {
        String urlParameters = PREPROCESSING_URL + System.currentTimeMillis();

        Matcher methodIndexMatcher = METHOD_INDEX_PATTERN.matcher(urlParameters);
        urlParameters = methodIndexMatcher.replaceAll(methodIndex);

        String httpUrl = getFrameworkUrl() + Constants.ESCIDOC_BASE_URI + urlParameters;
        long time = System.currentTimeMillis();
        HttpResponse httpRes =
            HttpHelper.executeHttpRequest(null, Constants.HTTP_METHOD_GET, httpUrl, null, null, null);
        String response = EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);
        httpRes.getEntity().consumeContent();
        response = " preprocessing needed " + (System.currentTimeMillis() - time) + response;
        try {
            assertMatches("String does not match es expected. " + response,
                "Operation completed successfully without a return value", response);

        }
        catch (final AssertionError e) {
            if (methodIndex.equals(STATISTIC_PREPROCESSR_METHOD_INDEX)) {
                triggerPreprocessing("1");
            }
            else {
                throw e;
            }
        }
    }

    /**
     * Tests successfully retrieving a report by an statistics_editor. Report-definition may get executed by
     * statistics-editor and depositor
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveReportAllowed1() throws Exception {

        //create grant statistics editor for user grantCreationUserOrGroupId 
        //with scope on scope:3
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.STATISTIC_SCOPE_BASE_URI + "/"
            + STATISTIC_SCOPE_ID, ROLE_HREF_STATISTICS_EDITOR, null);
        doTestRetrieveReport(HANDLE, REPORT_PARAMETERS_TEMPLATE_NAME, newReportDefinitionIds[1], null);
    }

    /**
     * Tests successfully retrieving a report by an default-user. Report-definition may get executed by default-user and
     * depositor
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveReportAllowed2() throws Exception {

        doTestRetrieveReport(HANDLE, REPORT_PARAMETERS_TEMPLATE_NAME, newReportDefinitionIds[0], null);
    }

    /**
     * Tests successfully retrieving a report by an system-administrator. Report-definition may get executed by no
     * roles.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveReportAllowed3() throws Exception {

        doTestRetrieveReport(PWCallback.DEFAULT_HANDLE, REPORT_PARAMETERS_TEMPLATE_NAME, newReportDefinitionIds[2],
            null);
    }

    /**
     * Tests successfully retrieving a report by an statistic-reader. Report-definition may get executed by no roles.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveReportAllowed4() throws Exception {

        //create grant statistics reader for user grantCreationUserOrGroupId 
        //with scope on scope:3
        doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.STATISTIC_SCOPE_BASE_URI + "/"
            + STATISTIC_SCOPE_ID, ROLE_HREF_STATISTICS_READER, null);
        doTestRetrieveReport(HANDLE, REPORT_PARAMETERS_TEMPLATE_NAME, reportDefinitionIds[2], null);
    }

}
