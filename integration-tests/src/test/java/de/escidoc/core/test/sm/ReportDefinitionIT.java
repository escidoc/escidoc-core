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

import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test the implementation of the ReportDefinition resource.
 *
 * @author Michael Hoppe
 */
public class ReportDefinitionIT extends ReportDefinitionTestBase {

    public static final String NAME_REPORT_DEFINITION = "report-definition";

    public static final String XPATH_SRW_REPOR_DEFINITION_LIST_REPOR_DEFINITION =
        XPATH_SRW_RESPONSE_OBJECT + NAME_REPORT_DEFINITION;

    private ScopeAbstractIT scope = null;

    private static String scopeId = null;

    private static String adminScopeId = null;

    private static Collection<String> primKeys = new ArrayList<String>();

    private static int methodCounter = 0;

    public static final String XPATH_SRW_REP_DEF_LIST_REP_DEF = XPATH_SRW_RESPONSE_OBJECT + NAME_REP_DEF;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        scope = new ScopeAbstractIT() {
        };
        if (methodCounter == 0) {
            primKeys = new ArrayList<String>();
            createScopes();
            createReportDefinition();
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
            methodCounter = 0;
            deleteReportDefinition();
            deleteScopes();
        }
    }

    /**
     * Creates scope for tests.
     *
     * @throws Exception If anything fails.
     */
    private void createScopes() throws Exception {
        String xml = getTemplateAsFixedScopeString(TEMPLATE_SCOPE_PATH, "escidoc_scope1.xml");
        String result = scope.create(xml);
        scopeId = getPrimKey(result);
        xml = getTemplateAsFixedScopeString(TEMPLATE_SCOPE_PATH, "escidoc_scope_admin.xml");
        result = scope.create(xml);
        adminScopeId = getPrimKey(result);
    }

    /**
     * delete scope to clean database.
     *
     * @throws Exception If anything fails.
     */
    public void deleteScopes() throws Exception {
        scope.delete(scopeId.toString());
        scope.delete(adminScopeId.toString());
    }

    /**
     * create report-definition.
     *
     * @throws Exception If anything fails.
     */
    public void createReportDefinition() throws Exception {
        String xml = getTemplateAsFixedReportDefinitionString(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition1.xml");
        try {
            String result = create(xml);
            assertXmlValidReportDefinition(result);
            primKeys.add(getPrimKey(result));
        }
        catch (final Exception e) {
            fail("Exception occured " + e.toString());
        }
    }

    /**
     * delete report-definition.
     *
     * @throws Exception If anything fails.
     */
    public void deleteReportDefinition() throws Exception {
        for (String primKey : primKeys) {
            delete(primKey);
        }
    }

    /**
     * retrieve report-definition.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD1() throws Exception {
        String result = retrieve(primKeys.iterator().next());
        assertXmlValidReportDefinition(result);
    }

    /**
     * retrieve report-definition with invalid id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD2() throws Exception {
        try {
            retrieve("99999");
            fail("No exception occured on retrieve with invalid id.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals(exceptionType, "ReportDefinitionNotFoundException");
        }
    }

    /**
     * update report-definition.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD3() throws Exception {
        String xml = getTemplateAsFixedReportDefinitionString(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition2.xml");
        xml = replacePrimKey(xml, primKeys.iterator().next());
        String response = update(primKeys.iterator().next(), xml);
        assertXmlValidReportDefinition(response);
    }

    /**
     * create report-definition with invalid xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD4() throws Exception {
        String xml =
            getTemplateAsFixedReportDefinitionString(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition_invalid.xml");
        try {
            create(xml);
            fail("No exception occured on create with invalid xml.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals(exceptionType, "XmlSchemaValidationException");
        }
    }

    /**
     * create report-definition with sql that writes data.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD4_1() throws Exception {
        String xml = getTemplateAsFixedReportDefinitionString(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition3.xml");
        try {
            create(xml);
            fail("No exception occured on create with invalid xml.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals(exceptionType, "InvalidSqlException");
        }
    }

    /**
     * update with wrong primkey.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD5() throws Exception {
        String xml = getTemplateAsFixedReportDefinitionString(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition1.xml");
        try {
            update("99999", xml);
            fail("No exception occured on update with wrong primkey.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals(exceptionType, "ReportDefinitionNotFoundException");
        }
    }

    /**
     * create with wrong namespace-prefix.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD6() throws Exception {
        String xml = getTemplateAsFixedReportDefinitionString(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition7.xml");
        try {
            create(xml);
            fail("No exception occured on create with wrong namespace-prefix.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals(exceptionType, "XmlCorruptedException");
        }
    }

    /**
     * create correct namespace-prefix.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD7() throws Exception {
        String xml = getTemplateAsFixedReportDefinitionString(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition8.xml");
        String result = create(xml);
        primKeys.add(getPrimKey(result));
        assertXmlValidReportDefinition(result);
    }

    /**
     * create with admin-scope and aggregation-tables from different scopes.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD8() throws Exception {
        String xml = getTemplateAsFixedReportDefinitionString(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition10.xml");
        xml = replaceElementPrimKey(xml, "scope", adminScopeId.toString());
        String result = create(xml);
        primKeys.add(getPrimKey(result));
        assertXmlValidReportDefinition(result);
    }

    /**
     * create with normal-scope and aggregation-tables from different scopes.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD9() throws Exception {
        String xml = getTemplateAsFixedReportDefinitionString(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition10.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            create(xml);
            fail("No exception occured on create with scope-context violating sql.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals(exceptionType, "ScopeContextViolationException");
        }
    }

    /**
     * create with id-attribute.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD10() throws Exception {
        String xml = getTemplateAsFixedReportDefinitionString(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition9.xml");
        String result = create(xml);
        String primKey = getPrimKey(result);
        primKeys.add(primKey);
        assertXmlValidReportDefinition(result);
        assertNotEquals("primkey is 99999", "99999", primKey.toString());
    }

    /**
     * create with wrong scope.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD11() throws Exception {
        String xml = getTemplateAsFixedReportDefinitionString(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition11.xml");
        try {
            create(xml);
            fail("No exception occured on create with wrong scope.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals(exceptionType, "ScopeNotFoundException");
        }
    }

    /**
     * create with wrong sql.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD12() throws Exception {
        String xml = getTemplateAsFixedReportDefinitionString(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition12.xml");
        try {
            create(xml);
            fail("No exception occured on create with wrong scope.");

        }
        catch (final Exception e) {
            String exceptionType = e.getClass().getSimpleName();
            assertEquals(exceptionType, "InvalidSqlException");
        }
    }

    /**
     * create with correct sql with placeholders.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD13() throws Exception {
        String xml = getTemplateAsFixedReportDefinitionString(TEMPLATE_REP_DEF_PATH, "escidoc_report_definition13.xml");
        String result = create(xml);
        String primKey = getPrimKey(result);
        primKeys.add(primKey);
        assertXmlValidReportDefinition(result);
    }

    /**
     * retrieve list of all report-definitions.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD14() throws Exception {
        String result = retrieveReportDefinitions(new HashMap<String, String[]>());
        assertXmlValidSrwResponse(result);
    }

    /**
     * retrieve a report definition identified by id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD15() throws Exception {
        final String ID = "escidoc:repdef1";
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(EscidocAbstractTest.FILTER_PARAMETER_QUERY, new String[] { "\""
            + EscidocAbstractTest.FILTER_IDENTIFIER + "\"=" + ID });

        String result = retrieveReportDefinitions(filterParams);

        assertXmlValidSrwResponse(result);

        Document retrievedDocument = EscidocAbstractTest.getDocument(result);
        NodeList reportDefinitionNodes =
            selectNodeList(retrievedDocument, XPATH_SRW_REPOR_DEFINITION_LIST_REPOR_DEFINITION);
        assertEquals("Unexpected number of report definitions.", 1, reportDefinitionNodes.getLength());
        assertXmlExists("Missing report definition with id " + ID, retrievedDocument,
            XPATH_SRW_REPOR_DEFINITION_LIST_REPOR_DEFINITION + "[@objid='" + ID
                + "' or @href='/statistic/report-definition/" + ID + "']");
    }

    /**
     * retrieve a report definition identified by name.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMRD16() throws Exception {
        final String NAME = "Item retrievals, all users";
        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(EscidocAbstractTest.FILTER_PARAMETER_QUERY, new String[] { "\""
            + EscidocAbstractTest.FILTER_NAME + "\"=\"" + NAME + "\"" });

        String result = retrieveReportDefinitions(filterParams);

        assertXmlValidSrwResponse(result);

        Document retrievedDocument = EscidocAbstractTest.getDocument(result);
        NodeList reportDefinitionNodes =
            selectNodeList(retrievedDocument, XPATH_SRW_REPOR_DEFINITION_LIST_REPOR_DEFINITION);
        assertEquals("Unexpected number of report definitions.", 1, reportDefinitionNodes.getLength());
        assertXmlExists("Missing report definition with name " + NAME, retrievedDocument,
            XPATH_SRW_REPOR_DEFINITION_LIST_REPOR_DEFINITION + "[name='" + NAME + "']");
    }

    /**
     * Test successful retrieving a list of existing ReportDefinitions resources. Test if maximumRecords=0 delivers 0
     * Roles
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void emptyFilterZeroMaximumRecords() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();
        filterParams.put(FILTER_PARAMETER_MAXIMUMRECORDS, new String[] { "0" });

        String result = null;

        try {
            result = retrieveReportDefinitions(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving of list of ReportDefinitions failed. ", e);
        }

        assertXmlValidSrwResponse(result);
        Document retrievedDocument = EscidocAbstractTest.getDocument(result);
        NodeList resultNodes = selectNodeList(retrievedDocument, XPATH_SRW_REP_DEF_LIST_REP_DEF);
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

        filterParams.put(EscidocAbstractTest.FILTER_PARAMETER_EXPLAIN, new String[] { "" });

        String result = null;

        try {
            result = retrieveReportDefinitions(filterParams);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }
}
