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
package de.escidoc.core.test.sm;

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.EscidocTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import junit.framework.AssertionFailedError;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test the implementation of the Report resource.
 * 
 * @author MIH
 * 
 */
@RunWith(value = Parameterized.class)
public class ReportTest extends ReportTestBase {

    private ReportDefinitionTest reportDefinition = null;

    private AggregationDefinitionTest aggregationDefinition = null;

    private static String[] reportDefinitionIds = null;

    private static String aggregationDefinitionId = null;

    private static int methodCounter = 0;

    private static final String PREPROCESSING_URL = 
                "jmx-console/HtmlAdaptor?action=invokeOp"
        + "&name=eSciDocCore%3Aname%3DStatisticPreprocessorService"
        + "&methodIndex=${methodIndex}&arg0=";
    
    private static final String STATISTIC_PREPROCESSR_METHOD_INDEX = "0";
    
    private static final Pattern METHOD_INDEX_PATTERN = 
                    Pattern.compile("\\$\\{methodIndex\\}");

    /**
     * @param transport
     *            The transport identifier.
     */
    public ReportTest(final int transport) {
        super(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        reportDefinition = new ReportDefinitionTest(getTransport());
        aggregationDefinition = new AggregationDefinitionTest(getTransport());
        if (methodCounter == 0) {
            createAggregationDefinition();
            createReportDefinitions();
            triggerPreprocessing(aggregationDefinitionId, "2000-01-01");
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
        if (methodCounter == getTestAnnotationsCount()) {
            // deleteReportDefinitions();
            // deleteAggregationDefinition();
            // deleteScope();
        }
    }

    /**
     * Creates report-definitions for tests.
     * 
     */
    private void createReportDefinitions() {
        try {
            reportDefinitionIds = new String[5];
            for (int i = 1; i < 6; i++) {
                String xml =
                    getTemplateAsFixedReportDefinitionString(
                        TEMPLATE_REP_DEF_PATH,
                        "escidoc_report_definition_for_report_test" + i
                            + ".xml");
                xml = replaceElementPrimKey(xml, "scope", 
                            EscidocTestBase.STATISTIC_SCOPE_ID1);
                xml =
                    replaceTableNames(xml, aggregationDefinitionId.toString());
                String result = reportDefinition.create(xml);
                reportDefinitionIds[i - 1] = getPrimKey(result);
            }
        }
        catch (Exception e) {
            fail("Exception occured " + e.toString());
        }
    }

    /**
     * explain operation without parameters for existing database xyz.
     * 
     * @test.name explain (1)
     * @test.id SB_EX-1
     * @test.input
     * @test.inputDescription
     * @test.expected explain plan for the corresponding database according
     *                ZeeRex Schema
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void createAggregationDefinition() throws Exception {
        String xml =
            getTemplateAsFixedAggregationDefinitionString(TEMPLATE_AGG_DEF_PATH,
                "escidoc_aggregation_definition3.xml");
        xml = replaceElementPrimKey(xml, "scope", 
                EscidocTestBase.STATISTIC_SCOPE_ID1);
        try {
            String result = aggregationDefinition.create(xml);
            aggregationDefinitionId = getPrimKey(result);
        }
        catch (Exception e) {
            fail("Exception occured " + e.toString());
        }
    }

    /**
     * triggers preprocessing via jmx-console.
     * 
     * @param methodIndex methodIndex
     * @throws Exception
     *             If anything fails.
     */
    private void triggerPreprocessing(
            final String methodIndex) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, 1);
        cal.set(Calendar.YEAR, 2009);
        
        String urlParameters =
            PREPROCESSING_URL + cal.getTimeInMillis();
        
        Matcher methodIndexMatcher = 
            METHOD_INDEX_PATTERN.matcher(urlParameters);
        urlParameters = methodIndexMatcher.replaceAll(methodIndex);

        String httpUrl =
            HttpHelper.createUrl(Constants.PROTOCOL, Constants.HOST_PORT,
                Constants.ESCIDOC_BASE_URI + urlParameters);
        long time = System.currentTimeMillis();
        HttpResponse result = 
            HttpHelper.executeHttpRequest(
                    Constants.HTTP_METHOD_GET, httpUrl, null,
            "", "", null);
        String response = EntityUtils.toString(result.getEntity(), HTTP.UTF_8);
        response = " preprocessing needed " 
                + (System.currentTimeMillis() - time) + response;
        try {
            assertMatches(
                    "String does not match es expected. " + response, 
                    "Operation completed successfully without a return value", 
                    response);
        } catch (AssertionFailedError e) {
            if (methodIndex.equals(STATISTIC_PREPROCESSR_METHOD_INDEX)) {
                triggerPreprocessing("1");
            } else {
                throw e;
            }
        }
    }

    /**
     * triggers preprocessing via framework-interface.
     * 
     * @param aggrDefinitionId aggrDefinitionId
     * @param date date
     * @throws Exception
     *             If anything fails.
     */
    private void triggerPreprocessing(
            final String aggrDefinitionId, 
                final String date) throws Exception {
        String preprocessingInformationXml =
            EscidocRestSoapTestBase.getTemplateAsString(
                    TEMPLATE_PREPROCESSING_INFO_PATH,
                "escidoc_preprocessing_information1.xml");
        Document doc = 
            EscidocRestSoapTestBase.
                getDocument(preprocessingInformationXml);
        substitute(doc, "/preprocessing-information/start-date", date);
        substitute(doc, "/preprocessing-information/end-date", date);
        getPreprocessingClient().preprocess(
                aggrDefinitionId, toString(doc, false));
    }

    /**
     * delete report-definitions.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void deleteReportDefinitions() throws Exception {
        for (int i = 0; i < reportDefinitionIds.length; i++) {
            reportDefinition.delete(reportDefinitionIds[i].toString());
        }
    }

    /**
     * delete aggregation-definition.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void deleteAggregationDefinition() throws Exception {
        aggregationDefinition.delete(aggregationDefinitionId.toString());
    }

    /**
     * retrieve reports with all report-definitionids and compare it to
     * escidoc_expected_reports.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSMRP1() throws Exception {
        StringBuffer results = new StringBuffer("");
        // Always use same report_parameters.xml
        String xml =
            getTemplateAsFixedReportParametersString(
                TEMPLATE_REP_PARAMETERS_PATH, "escidoc_report_parameters1.xml");

        results.append(checkReport(0, 1, xml));
        results.append(checkReport(1, 2, xml));
        results.append(checkReport(2, 3, xml));
        results.append(checkReport(3, 4, xml));

        // trigger Preprocessing once again/////////////////////////////////////
        triggerPreprocessing(STATISTIC_PREPROCESSR_METHOD_INDEX);
        // /////////////////////////////////////////////////////////////////////

        results.append(checkReport(0, 5, xml));
        results.append(checkReport(1, 6, xml));
        results.append(checkReport(2, 7, xml));
        results.append(checkReport(3, 8, xml));

        // trigger Preprocessing once again/////////////////////////////////////
        triggerPreprocessing(aggregationDefinitionId, "2000-01-02");
        // /////////////////////////////////////////////////////////////////////

        results.append(checkReport(0, 9, xml));
        results.append(checkReport(1, 10, xml));
        results.append(checkReport(2, 11, xml));
        results.append(checkReport(3, 12, xml));

        // check reportDefinition with wrong placeholder////////////////////////
        try {
            xml =
                replaceElementPrimKey(xml, "report-definition",
                    reportDefinitionIds[4].toString());
            retrieve(xml);
            results.append("NO EXCEPTION");

        }
        catch (Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            if (exceptionType.equals("MissingMethodParameterException")) {
                results.append("OK");
            } else {
                System.out.println(exceptionType);
                results.append("WRONG");
            }
            assertEquals(exceptionType, "MissingMethodParameterException");
        }
        assertEquals("results not as expected: " + results.toString(), 
                "OKOKOKOKOKOKOKOKOKOKOKOKOK", results.toString());
    }

    /**
     * Tests declining retrieving a report with providing corrupted xml.
     * 
     * @test.name Default Policies - Corrupted Xml.
     * @test.id SM_Rrp-2
     * @test.input
     *          <ul>
     *          <li>Corrupted xml.</li>
     *          </ul>
     * @test.expected: InvalidXmlException
     * @test.status Implemented
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSMRP2() throws Exception {

        final Class<XmlCorruptedException> ec = XmlCorruptedException.class;
        try {
            retrieve("Corrupted");
            EscidocRestSoapTestBase.failMissingException(
                "Retrieving report with providing corrupted xml not declined.",
                ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Retrieving report with providing corrupted xml not declined,"
                    + " properly.", ec, e);
        }
    }

    /**
     * Tests declining retrieving a report without providing xml.
     * 
     * @test.name Default Policies - No Xml.
     * @test.id SM_Rrp-3
     * @test.input
     *          <ul>
     *          <li>No parameter xml is provided.</li>
     *          </ul>
     * @test.expected: InvalidXmlException
     * @test.status Implemented
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSMRP3() throws Exception {

        final Class<MissingMethodParameterException> ec =
            MissingMethodParameterException.class;
        try {
            retrieve(null);
            EscidocRestSoapTestBase.failMissingException(
                "Retrieving report without providing xml not declined.", ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Retrieving report without providing corrupted xml not declined,"
                    + " properly.", ec, e);
        }
    }
    
    /**
     * retrieve report and check it.
     * 
     * @param repDefIndex index
     * @param expectedIndex index
     * @param xml report-parameters
     * @return String result
     * @throws Exception
     *             If anything fails.
     */
    private String checkReport(
        final int repDefIndex, 
        final int expectedIndex, 
        final String xml) throws Exception {
        String xml1 =
            replaceElementPrimKey(xml, "report-definition",
                reportDefinitionIds[repDefIndex].toString());
        String result = retrieve(xml1);

        String expected =
            getTemplateAsFixedReportString(TEMPLATE_REPORT_PATH,
                "escidoc_expected_report" + expectedIndex + ".xml");
        if (!result.matches("(?s).*" 
                + reportDefinitionIds[repDefIndex].toString() + ".*")) {
            return "WRONG";
        }
        expected = replaceYear(expected, "2009");

        result = result.replaceAll("\\s+", "");
        result = result.replaceFirst(".*?<report:report-record.*?>", "");
        expected = expected.replaceAll("\\s+", "");
        expected = expected.replaceFirst(".*?<report:report-record.*?>", "");
        if (expected.equals(result)) {
            return "OK";
        } else {
            System.out.println(repDefIndex + expectedIndex + result);
            return "WRONG";
        }
        
    }
}
