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

import java.util.Date;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test suite for testing access-rights to statistic-reports.
 *
 * @author Michael Hoppe
 */
public class StatisticReaderAbstractTest extends GrantTestBase {

    private static final String HANDLE = PWCallback.TEST_HANDLE;

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

    /**
     * The constructor.
     *
     * @param transport     The transport identifier.
     * @param handlerCode   handlerCode of either UserAccountHandler or UserGroupHandler.
     * @param userOrGroupId userOrGroupId for grantCreation.
     * @throws Exception If anything fails.
     */
    public StatisticReaderAbstractTest(final int transport, final int handlerCode, final String userOrGroupId)
        throws Exception {
        super(transport, handlerCode);
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
        String now = new DateTime(new Date().getTime()).toString("yyyy-MM-dd");
        triggerPreprocessing(aggregationDefinitionId, now);
        triggerPreprocessing(newAggregationDefinitionId, now);

    }

    /**
     * triggers preprocessing via framework-interface.
     *
     * @param aggrDefinitionId aggrDefinitionId
     * @param date             date
     * @throws Exception If anything fails.
     */
    private void triggerPreprocessing(final String aggrDefinitionId, final String date) throws Exception {
        String preprocessingInformationXml =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_PREPROCESSING_INFO_PATH,
                "escidoc_preprocessing_information1.xml");
        Document doc = EscidocRestSoapTestBase.getDocument(preprocessingInformationXml);
        substitute(doc, "/preprocessing-information/start-date", date);
        substitute(doc, "/preprocessing-information/end-date", date);
        preprocessingClient.preprocess(aggrDefinitionId, toString(doc, false));
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
