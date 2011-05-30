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

import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Test suite for the role Statistics_editor.
 *
 * @author Michael Hoppe
 */
@RunWith(Parameterized.class)
public class StatisticEditorIT extends GrantTestBase {

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

    private static int methodCounter = 0;

    private static final String ALLOWED_SCOPE = STATISTIC_SCOPE_ID;

    private static String disallowedScope = null;

    private static String allowedScopeAggregationDefinitionId = null;

    private static String allowedScopeReportDefinitionId = null;

    private static String disallowedScopeAggregationDefinitionId = null;

    private static String disallowedScopeReportDefinitionId = null;

    private static final String SCOPE_TEMPLATE_NAME = "escidoc_scope3.xml";

    private static final String SCOPE_UPDATE_TEMPLATE_NAME = "escidoc_scope2.xml";

    private static final String AGGREGATION_DEFINITION_TEMPLATE_NAME = "escidoc_aggregation_definition1.xml";

    private static final String REPORT_DEFINITION_TEMPLATE_NAME = "escidoc_report_definition1.xml";

    private static final String REPORT_DEFINITION_UPDATE_TEMPLATE_NAME = "escidoc_report_definition2.xml";

    private static final String STATISTIC_DATA_TEMPLATE_NAME = "escidoc_statistic_data1.xml";

    /**
     * The constructor.
     *
     * @param handlerCode   handlerCode of either UserAccountHandler or UserGroupHandler.
     * @param userOrGroupId userOrGroupId for grantCreation.
     * @throws Exception If anything fails.
     */
    public StatisticEditorIT(final int handlerCode, final String userOrGroupId) throws Exception {
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
        if (methodCounter == 0) {
            //revoke all Grants
            revokeAllGrants(grantCreationUserOrGroupId);
            //create grant statistics editor for user grantCreationUserOrGroupId 
            //with scope on scope:3
            doTestCreateGrant(null, grantCreationUserOrGroupId, Constants.STATISTIC_SCOPE_BASE_URI + "/"
                + ALLOWED_SCOPE, ROLE_HREF_STATISTICS_EDITOR, null);
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
        //create scope where statistics_editor is not allowed to access.
        disallowedScope = doTestCreateScope(PWCallback.DEFAULT_HANDLE, SCOPE_TEMPLATE_NAME, null);

        //create aggregationDefinition in disallowedScope as system-admin
        disallowedScopeAggregationDefinitionId =
            doTestCreateAggregationDefinition(PWCallback.DEFAULT_HANDLE, AGGREGATION_DEFINITION_TEMPLATE_NAME,
                disallowedScope, null);

        //create reportDefinition in disallowedScope as system-admin
        disallowedScopeReportDefinitionId =
            doTestCreateReportDefinition(PWCallback.DEFAULT_HANDLE, REPORT_DEFINITION_TEMPLATE_NAME, disallowedScope,
                disallowedScopeAggregationDefinitionId, null);

    }

    /**
     * Tests successfully retrieving a scope by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveScope() throws Exception {

        doTestRetrieveScope(HANDLE, ALLOWED_SCOPE, null);
    }

    /**
     * Tests successfully updating a scope by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateScope() throws Exception {

        doTestUpdateScope(HANDLE, SCOPE_UPDATE_TEMPLATE_NAME, ALLOWED_SCOPE, null);
    }

    /**
     * Tests successfully retrieving a scope-list by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveScopeList() throws Exception {

        doTestRetrieveScopeList(HANDLE, ALLOWED_SCOPE);
    }

    /**
     * Tests successfully writing statistic-data by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateStatisticData() throws Exception {

        doTestCreateStatisticData(HANDLE, STATISTIC_DATA_TEMPLATE_NAME, ALLOWED_SCOPE, null);
    }

    /**
     * Tests successfully creating a aggregation-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateAggregationDefinition() throws Exception {

        allowedScopeAggregationDefinitionId =
            doTestCreateAggregationDefinition(HANDLE, AGGREGATION_DEFINITION_TEMPLATE_NAME, ALLOWED_SCOPE, null);
    }

    /**
     * Tests successfully retrieving a aggregation-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveAggregationDefinition() throws Exception {

        doTestRetrieveAggregationDefinition(HANDLE, allowedScopeAggregationDefinitionId, null);
    }

    /**
     * Tests successfully retrieving a list of aggregation-definitions by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveAggregationDefinitionList() throws Exception {

        doTestRetrieveAggregationDefinitionList(HANDLE, ALLOWED_SCOPE);
    }

    /**
     * Tests successfully creating a report-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateReportDefinition() throws Exception {

        allowedScopeReportDefinitionId =
            doTestCreateReportDefinition(HANDLE, REPORT_DEFINITION_TEMPLATE_NAME, ALLOWED_SCOPE,
                allowedScopeAggregationDefinitionId, null);
    }

    /**
     * Tests successfully retrieving a report-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveReportDefinition() throws Exception {

        doTestRetrieveReportDefinition(HANDLE, allowedScopeReportDefinitionId, null);
    }

    /**
     * Tests successfully retrieving a list of report-definitions by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveReportDefinitionList() throws Exception {

        doTestRetrieveReportDefinitionList(HANDLE, ALLOWED_SCOPE);
    }

    /**
     * Tests successfully updating a report-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateReportDefinition() throws Exception {

        doTestUpdateReportDefinition(HANDLE, REPORT_DEFINITION_UPDATE_TEMPLATE_NAME, allowedScopeReportDefinitionId,
            allowedScopeAggregationDefinitionId, ALLOWED_SCOPE, null);
    }

    /**
     * Tests successfully deleting a report-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteReportDefinition() throws Exception {

        doTestDeleteReportDefinition(HANDLE, allowedScopeReportDefinitionId, null);
    }

    /**
     * Tests successfully deleting a aggregation-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteAggregationDefinition() throws Exception {

        doTestDeleteAggregationDefinition(HANDLE, allowedScopeAggregationDefinitionId, null);
    }

    /**
     * Tests unsuccessfully retrieving a scope by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveDisallowedScope() throws Exception {

        doTestRetrieveScope(HANDLE, disallowedScope, AuthorizationException.class);
    }

    /**
     * Tests unsuccessfully updating a scope by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateDisallowedScope() throws Exception {

        doTestUpdateScope(HANDLE, SCOPE_UPDATE_TEMPLATE_NAME, disallowedScope, AuthorizationException.class);
    }

    /**
     * Tests unsuccessfully writing statistic-data by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateDisallowedStatisticData() throws Exception {

        doTestCreateStatisticData(HANDLE, STATISTIC_DATA_TEMPLATE_NAME, disallowedScope, AuthorizationException.class);
    }

    /**
     * Tests unsuccessfully creating a aggregation-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateDisallowedAggregationDefinition() throws Exception {

        doTestCreateAggregationDefinition(HANDLE, AGGREGATION_DEFINITION_TEMPLATE_NAME, disallowedScope,
            AuthorizationException.class);
    }

    /**
     * Tests unsuccessfully retrieving a aggregation-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveDisallowedAggregationDefinition() throws Exception {

        doTestRetrieveAggregationDefinition(HANDLE, disallowedScopeAggregationDefinitionId,
            AuthorizationException.class);
    }

    /**
     * Tests unsuccessfully creating a report-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCreateDisallowedReportDefinition() throws Exception {

        doTestCreateReportDefinition(HANDLE, REPORT_DEFINITION_TEMPLATE_NAME, disallowedScope,
            disallowedScopeAggregationDefinitionId, AuthorizationException.class);
    }

    /**
     * Tests unsuccessfully retrieving a report-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveDisallowedReportDefinition() throws Exception {

        doTestRetrieveReportDefinition(HANDLE, disallowedScopeReportDefinitionId, AuthorizationException.class);
    }

    /**
     * Tests unsuccessfully updating a report-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testUpdateDisallowedReportDefinition() throws Exception {

        doTestUpdateReportDefinition(HANDLE, REPORT_DEFINITION_UPDATE_TEMPLATE_NAME, disallowedScopeReportDefinitionId,
            disallowedScopeAggregationDefinitionId, disallowedScope, AuthorizationException.class);
    }

    /**
     * Tests unsuccessfully deleting a report-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteDisallowedReportDefinition() throws Exception {

        doTestDeleteReportDefinition(HANDLE, disallowedScopeReportDefinitionId, AuthorizationException.class);
    }

    /**
     * Tests unsuccessfully deleting a aggregation-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDeleteDisallowedAggregationDefinition() throws Exception {

        doTestDeleteAggregationDefinition(HANDLE, disallowedScopeAggregationDefinitionId, AuthorizationException.class);
    }

    /**
     * Tests successfully preprocessing an aggregation-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testPreprocessAggregationDefinition() throws Exception {
        String aggregationDefinitionId =
            doTestCreateAggregationDefinition(PWCallback.DEFAULT_HANDLE, AGGREGATION_DEFINITION_TEMPLATE_NAME,
                ALLOWED_SCOPE, null);
        List<String> aggregationDefinitionIds = new ArrayList<String>();
        aggregationDefinitionIds.add(aggregationDefinitionId);

        doTestPreprocessStatisticData(HANDLE, "escidoc_preprocessing_information1.xml", aggregationDefinitionIds,
            "2000-01-01", "2000-01-01", null);
    }

    /**
     * Tests unsuccessfully preprocessing an aggregation-definition by an statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testPreprocessDisallowedAggregationDefinition() throws Exception {
        String aggregationDefinitionId =
            doTestCreateAggregationDefinition(PWCallback.DEFAULT_HANDLE, AGGREGATION_DEFINITION_TEMPLATE_NAME,
                disallowedScope, null);

        List<String> aggregationDefinitionIds = new ArrayList<String>();
        aggregationDefinitionIds.add(aggregationDefinitionId);
        doTestPreprocessStatisticData(HANDLE, "escidoc_preprocessing_information1.xml", aggregationDefinitionIds,
            "2000-01-01", "2000-01-01", AuthorizationException.class);
    }

    /**
     * Test logging out a statistics_editor.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testAaStatisticsEditorLogout() throws Exception {

        doTestLogout(LOGINNAME, PASSWORD);
    }
}
