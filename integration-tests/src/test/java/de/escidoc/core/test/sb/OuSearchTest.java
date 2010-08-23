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
package de.escidoc.core.test.sb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import de.escidoc.core.test.common.resources.PropertiesProvider;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test the implementation of the search resource.
 * 
 * @author MIH
 * 
 */
public class OuSearchTest extends SearchTestBase {

    private static String[] orgUnitIds = null;

    private static HashMap<Integer, String[]> parentIds =
        new HashMap<Integer, String[]>();

    private static int methodCounter = 0;

    private static final String INDEX_NAME = "escidocou_all";

    private static final int SLEEP_TIME = 5000;

    private static String startTime = "";

    /**
     * @param transport
     *            The transport identifier.
     */
    public OuSearchTest(final int transport) {
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
        if (methodCounter == getTestAnnotationsCount()) {

        }
    }

    /**
     * insert org-unit(s) into system for the tests.
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
        log.info("starting OUSearchTest at " 
            + new DateTime(System.currentTimeMillis() 
            + (60 * 60 * 1000), DateTimeZone.UTC).toString());
        // create empty index/////////////////////////////////////////////////
        String urlParameters =
            "?operation=updateIndex" + "&action=createEmpty"
                + "&repositoryName=escidocrepository" + "&indexName=";

        PropertiesProvider p = new PropertiesProvider();
        String escidocServer =
            p.getProperty(PropertiesProvider.ESCIDOC_SERVER_NAME);
        String escidocPort =
            p.getProperty(PropertiesProvider.ESCIDOC_SERVER_PORT);

        String httpUrl =
            HttpHelper
                .createUrl(
                    de.escidoc.core.test.common.client.servlet.Constants.PROTOCOL,
                    escidocServer + ":" + escidocPort,
                    de.escidoc.core.test.common.client.servlet.Constants.FEDORAGSEARCH_BASE_URI
                        + urlParameters);
        HttpHelper
            .executeHttpMethod(
                de.escidoc.core.test.common.client.servlet.Constants.HTTP_METHOD_GET,
                httpUrl + INDEX_NAME, null, null, null, null);
        // ////////////////////////////////////////////////////////////////////

        startTime =
            new DateTime(System.currentTimeMillis() 
                    - (60 * 60 * 1000), DateTimeZone.UTC)
                .toString();
        // Create Org-Units////////////////////////////////////////////////////
        orgUnitIds = new String[Constants.NUM_ORG_UNITS];
        for (int i = 0; i < Constants.NUM_ORG_UNITS; i++) {
            final String templateName = "escidoc_search_ou" + i + ".xml";
            final Document document =
                getTemplateAsDocument(TEMPLATE_SB_ORGANIZATIONAL_UNIT_PATH,
                    templateName);

            String xml =
                prepareOrgUnit(PWCallback.DEFAULT_HANDLE, document,
                    ORGANIZATIONAL_UNIT_STATUS_OPENED, parentIds.get(i));

            writeVariables(i, xml);
            Thread.sleep(SLEEP_TIME);
        }
        Thread.sleep(60000);
        // /////////////////////////////////////////////////////////////////////
    }

    /**
     * update org-unit(s) for the tests.
     * 
     * @test.name update
     * @test.id UPDATE
     * @test.input
     * @test.inputDescription
     * @test.expected
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    private void update() throws Exception {
        // UPDATE Org-Units////////////////////////////////////////////////////
        try {
            // close org-unit 6
            closeOrgUnit(orgUnitIds[6]);
            Thread.sleep(SLEEP_TIME);

            // update org-unit 0
            String xml =
                retrieve(ORGANIZATIONAL_UNIT_HANDLER_CODE, orgUnitIds[0]);
            xml =
                xml.replaceAll("Hierarchy Top Level",
                    "Hierarchy Top Level updated ");
            update(ORGANIZATIONAL_UNIT_HANDLER_CODE, orgUnitIds[0], xml);
            Thread.sleep(SLEEP_TIME);

            // update metadata of org-unit 1
            xml = retrieve(ORGANIZATIONAL_UNIT_HANDLER_CODE, orgUnitIds[1]);
            xml = xml.replaceAll("Munich", "London");
            update(ORGANIZATIONAL_UNIT_HANDLER_CODE, orgUnitIds[1], xml);
            Thread.sleep(SLEEP_TIME);

        }
        catch (Exception e) {
            log.error(e);
        }
        // /////////////////////////////////////////////////////////////////////
    }

    /**
     * prepare parentIds for tree-structure of test-org-units. write orgUnitid
     * as variable.
     * 
     * @param i
     *            number of org-unit.
     * @param xml
     *            org-unit-xml.
     */
    private void writeVariables(final int i, final String xml) {
        String id = getId(xml);
        orgUnitIds[i] = id;
        switch (i) {
            case 0:
                parentIds.put(1, new String[] { id });
                parentIds.put(2, new String[] { id });
                break;
            case 2:
                parentIds.put(3, new String[] { orgUnitIds[1] });
                parentIds.put(4, new String[] { orgUnitIds[1] });
                parentIds.put(5, new String[] { orgUnitIds[1], id });
                parentIds.put(6, new String[] { id });
                break;
            default:
                break;
        }
    }

    /**
     * explain operation without parameters for existing database xyz.
     * 
     * @test.name explain (1)
     * @test.id SB_OU_EX-1
     * @test.input
     * @test.inputDescription
     * @test.expected explain plan for the corresponding database according
     *                ZeeRex Schema
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUEX1() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        String response = explain(parameters, INDEX_NAME);
        assertXmlValidExplainPlan(response);
        assertEquals("srw/search/escidocou_all", getDatabase(response));
        assertEquals(Constants.OU_INDEX_FIELD_COUNT,
            getIndexFieldCount(response));
        assertEquals(Constants.OU_SORT_FIELD_COUNT, getSortFieldCount(response));
    }

    /**
     * explain operation where operation=explain for existing database xyz is
     * explicitly given.
     * 
     * @test.name explain (2)
     * @test.id SB_OU_EX-2
     * @test.input existing database
     * @test.inputDescription database has to exist
     * @test.expected explain plan for the corresponding database according
     *                ZeeRex Schema
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUEX2() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("operation", "explain");
        String response = explain(parameters, INDEX_NAME);
        assertXmlValidExplainPlan(response);
        assertEquals("srw/search/escidocou_all", getDatabase(response));
        assertEquals(Constants.OU_INDEX_FIELD_COUNT,
            getIndexFieldCount(response));
        assertEquals(Constants.OU_SORT_FIELD_COUNT, getSortFieldCount(response));
    }

    /**
     * Test searching for a single term.
     * 
     * @test.name Single Term Search
     * @test.id SB_OU_SR-1
     * @test.input mandatory request parameters: - any single term query -
     *             existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR1() throws Exception {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=mitte-rechts");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(true, checkHighlighting(response));
        assertEquals("1", getNumberOfHits(response));
        assertEquals("1", getFirstRecord(response));
    }

    /**
     * Database not existing.
     * 
     * @test.name Database not existing
     * @test.id SB_OU_SR-2
     * @test.input mandatory request parameters - any single term query -
     *             non-existing database --- no additional request parameters
     * @test.inputDescription input contains wrong database
     * @test.expected error message describing the reason for failure
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR2() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=mitte-rechts");
        try {
            search(parameters, "escidoc_fault");
            fail("No exception occured on search in non-existing database.");
        }
        catch (Exception e) {
            // FIXME: assert exception
        }
    }

    /**
     * Request parameter startRecord \u2013 (1).
     * 
     * @test.name Request parameter startRecord \u2013 (1)
     * @test.id SB_OU_SR-3
     * @test.input mandatory request parameters: - any single term query which
     *             results in more hits than are shown on one page by default -
     *             existing database --- additional request parameter: -
     *             startRecord = one digit greater than the number of hits that
     *             are shown on one page by default
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results the 1st record to be returned ist at
     *                the corresponding position in the sequence of matched
     *                records only released objects are found
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR3() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=orgunit");
        parameters.put("startRecord", "3");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("3", getFirstRecord(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter startrecord – (2).
     * 
     * @test.name Request parameter startrecord – (2)
     * @test.id SB_OU_SR-4
     * @test.input mandatory request parameters: - any single term query which
     *             results in more hits than are shown on one page by default -
     *             existing database --- additional request parameter: -
     *             startRecord = 0
     * @test.inputDescription
     * @test.expected error message describing the reason for failure
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR4() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=orgunit");
        parameters.put("startRecord", "0");
        String response = null;
        try {
            response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("startRecord", getDiagnostics(response));
        }
        catch (Exception e) {
        }
    }

    /**
     * Request parameter maximumRecords – (1).
     * 
     * @test.name Request parameter maximumRecords – (1)
     * @test.id SB_OU_SR-5
     * @test.input mandatory request parameters: - any single term query which
     *             results in many hits - existing database --- additional
     *             request parameter: - maximumRecords = any value greater than
     *             0
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records Number of
     *                records to be returned may be equal or less than the value
     *                of the parameter maximumRecords only released objects are
     *                found
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR5() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=orgunit");
        parameters.put("maximumRecords", "1");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("2", getNextRecordPosition(response));
        assertEquals(Integer.toString(Constants.NUM_ORG_UNITS),
            getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter maximumRecords – (2).
     * 
     * @test.name Request parameter maximumRecords – (2)
     * @test.id SB_OU_SR-6
     * @test.input --- mandatory request parameters: - any single term query
     *             which results in many hits - existing database --- additional
     *             request parameter: - maximumRecords = 0
     * @test.inputDescription
     * @test.expected
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR6() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=orgunit");
        parameters.put("maximumRecords", "1");
        String response = null;
        try {
            response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("2", getNextRecordPosition(response));
            assertEquals(Integer.toString(Constants.NUM_ORG_UNITS),
                getNumberOfHits(response));
        }
        catch (Exception e) {
        }
    }

    /**
     * Request parameter maximumRecords – (3).
     * 
     * @test.name Request parameter maximumRecords – (3)
     * @test.id SB_OU_SR-7
     * @test.input mandatory request parameters: - any single term query which
     *             results in many hits - existing database --- additional
     *             request parameter: - maximumRecords = any negative value
     * @test.inputDescription
     * @test.expected error message describing the reason for failure
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR7() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=orgunit");
        parameters.put("maximumRecords", "-1");
        try {
            String response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("maximumRecords", getDiagnostics(response));
        }
        catch (Exception e) {
        }
    }

    /**
     * Request parameter recordPacking – (1).
     * 
     * @test.name Request parameter recordPacking – (1)
     * @test.id SB_OU_SR-8
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - RecordPacking = xml
     * @test.inputDescription
     * @test.expected List of search records (xml) according to eSciDoc Default
     *                Schema for search results
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR8() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=orgunit");
        parameters.put("recordPacking", "xml");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(null, getDiagnostics(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter recordPacking – (2).
     * 
     * @test.name Request parameter recordPacking – (2)
     * @test.id SB_OU_SR-9
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - RecordPacking = string
     * @test.inputDescription
     * @test.expected List of search records (string)
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR9() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=orgunit");
        parameters.put("recordPacking", "string");
        String response = search(parameters, INDEX_NAME);
        assertEquals(null, getDiagnostics(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter recordPacking – (3).
     * 
     * @test.name Request parameter recordPacking – (3)
     * @test.id SB_OU_SR-10
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - RecordPacking = any invalid value, e.g.
     *             invalid
     * @test.inputDescription
     * @test.expected error message describing the reason for failure
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR10() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=orgunit");
        parameters.put("recordPacking", "something");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("something", getDiagnostics(response));
    }

    /**
     * Request parameter recordSchema – (1).
     * 
     * @test.name Request parameter recordSchema – (1)
     * @test.id SB_OU_SR-11
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - valid RecordSchema shall explicitly be
     *             named
     * @test.inputDescription
     * @test.expected List of search records according to the explicitly named
     *                eSciDoc Schema for search results
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR11() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=mitte-rechts");
        parameters.put("recordSchema", "default");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter recordSchema – (2).
     * 
     * @test.name Request parameter recordSchema – (2)
     * @test.id SB_OU_SR-12
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - invalid RecordSchema
     * @test.inputDescription
     * @test.expected error message describing the reason for failure
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR12() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=mitte-rechts");
        parameters.put("recordSchema", "none");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("none", getDiagnostics(response));
    }

    /**
     * Request parameter sortKeys – (1).
     * 
     * @test.name Request parameter sortKeys – (1)
     * @test.id SB_OU_SR-13
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - a single valid sortkey
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results search results are sorted by
     *                corresponding sortkey
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR13() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=orgunit");
        parameters.put("sortKeys", "sort.escidoc.identifier");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        String[] splitStr = response.split("<dc:identifier.*?>");
        String[] stringsToCheck =
            { "1-01-01", "2-01-01", "2-02-01", "3-01-01", "3-02-01", "3-03-02",
                "3-04-01" };
        int j = 0;
        for (int i = 1; i < splitStr.length; i++) {
            String splitPart = splitStr[i];
            if (!splitPart.matches("(?s)[^0-9]*" + stringsToCheck[j] + ".*")) {
                j++;
                if (!splitPart
                    .matches("(?s)[^0-9]*" + stringsToCheck[j] + ".*")) {
                    assertTrue("wrong sortorder", false);
                }
            }
        }
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter sortKeys – (2).
     * 
     * @test.name Request parameter sortKeys – (2)
     * @test.id SB_OU_SR-14
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - two valid sortkeys
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results search results are sorted by
     *                corresponding sortkeys
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR14() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=orgunit");
        parameters.put("sortKeys",
            "sort.escidoc.organization-type,,0 sort.escidoc.identifier");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        String[] records = response.split("<record>");
        String[] valuesToCheck =
            { "", "workgroup|3-01-01", "workgroup|3-02-01",
                "workgroup|3-03-02", "workgroup|3-04-01", "society|1-01-01",
                "institute|2-01-01", "institute|2-02-01" };
        assertEquals(records.length, valuesToCheck.length);
        for (int i = 1; i < records.length; i++) {
            String[] parts = valuesToCheck[i].split("\\|");
            if (!records[i].matches("(?s).*<.*?organization-type.*?>"
                + parts[0] + ".*")
                || !records[i].toLowerCase().matches(
                    "(?s).*<.*?identifier.*?>" + parts[1].toLowerCase() + ".*")) {
                assertTrue("wrong sortorder", false);

            }

        }
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter sortKeys – (3).
     * 
     * @test.name Request parameter sortKeys – (3)
     * @test.id SB_OU_SR-15
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - invalid sortkey
     * @test.inputDescription
     * @test.expected 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR15() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=orgunit");
        parameters.put("sortKeys", "sort.escidoc.sonstwas,,0");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("7", getNumberOfHits(response));
    }

    /**
     * : Request parameter stylesheet – (1).
     * 
     * @test.name : Request parameter stylesheet – (1)
     * @test.id SB_OU_SR-16
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - existent styleSheet
     * @test.inputDescription
     * @test.expected
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR16() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=orgunit");
        parameters.put("stylesheet",
            "http://escidev5:8080/srw/searchRetrieveResponse.xsl");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(Integer.toString(Constants.NUM_ORG_UNITS),
            getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter stylesheet – (2).
     * 
     * @test.name Request parameter stylesheet – (2)
     * @test.id SB_OU_SR-17
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - nonexistent styleSheet
     * @test.inputDescription
     * @test.expected error message describing the reason for failure
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR17() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=orgunit");
        parameters.put("stylesheet", "http://escidev5:8080/srw/xyz.xsl");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(Integer.toString(Constants.NUM_ORG_UNITS),
            getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Boolean Operator AND.
     * 
     * @test.name Boolean Operator AND
     * @test.id SB_OU_SR-18
     * @test.input --- mandatory request parameters: - query where two terms are
     *             connected by Boolean AND - existing database --- no
     *             additional request parameters
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results Both search terms have to match
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR18() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata all \"type institute\"");
        String response = search(parameters, INDEX_NAME);
        assertEquals("2", getNumberOfHits(response));
        parameters = new HashMap<String, String>();
        parameters.put("query",
            "escidoc.metadata=type and escidoc.metadata=institute");
        response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("2", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Boolean Operator OR .
     * 
     * @test.name Boolean Operator OR
     * @test.id SB_OU_SR-19
     * @test.input mandatory request parameters: - query where two terms are
     *             connected by Boolean OR - existing database --- no additional
     *             request parameters
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results Only one search term needs to match
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR19() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata any \"type institute\"");
        String response = search(parameters, INDEX_NAME);
        assertEquals(Integer.toString(Constants.NUM_ORG_UNITS),
            getNumberOfHits(response));
        parameters = new HashMap<String, String>();
        parameters.put("query",
            "escidoc.metadata=type or escidoc.metadata=institute");
        response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(Integer.toString(Constants.NUM_ORG_UNITS),
            getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Use of * as Wildcard (1).
     * 
     * @test.name Use of * as Wildcard (1)
     * @test.id SB_OU_SR-20
     * @test.input mandatory request parameters: - single term query where start
     *             of term is masked by * - existing database --- no additional
     *             request parameters
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR20() throws Exception {
        // Left truncation not supported
    }

    /**
     * Use of * as Wildcard (2).
     * 
     * @test.name Use of * as Wildcard (2)
     * @test.id SB_OU_SR-21
     * @test.input mandatory request parameters: - single term query where
     *             wildcard * is used in the middle of the term - existing
     *             database --- no additional request parameters
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR21() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=or*ni*");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(Integer.toString(Constants.NUM_ORG_UNITS),
            getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Use of * as Wildcard (3).
     * 
     * @test.name Use of * as Wildcard (3)
     * @test.id SB_OU_SR-22
     * @test.input mandatory request parameters: - single term query where end
     *             of term is masked by * - existing database --- no additional
     *             request parameters
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR22() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=org*");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(Integer.toString(Constants.NUM_ORG_UNITS),
            getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Use of ? as Wildcard (1).
     * 
     * @test.name Use of ? as Wildcard (1)
     * @test.id SB_OU_SR-23
     * @test.input mandatory request parameters: - single term query where start
     *             of term is masked by ? - existing database --- no additional
     *             request parameters
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR23() throws Exception {
        // Left truncation not supported
    }

    /**
     * Use of ? as Wildcard (2).
     * 
     * @test.name Use of ? as Wildcard (2)
     * @test.id SB_OU_SR-24
     * @test.input mandatory request parameters: - single term query where
     *             wildcard ? is used in the middle of the term - existing
     *             database --- no additional request parameters
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR24() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=wo??gro?p");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("4", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Use of ? as Wildcard (3) .
     * 
     * @test.name Use of ? as Wildcard (3)
     * @test.id SB_OU_SR-25
     * @test.input mandatory request parameters: - single term query where end
     *             of term is masked by ? - existing database --- no additional
     *             request parameters
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR25() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=workgrou?");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("4", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Fuzzy Search.
     * 
     * @test.name Fuzzy Search
     * @test.id SB_OU_SR-26
     * @test.input mandatory request parameters: - single term query where
     *             relation modifier fuzzy is provided - existing database ---
     *             no additional request parameters
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR26() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=/fuzzy werkgrup");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("4", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * : Umlaut as request-parameter .
     * 
     * @test.name : Umlaut as request-parameter – (1)
     * @test.id SB_OU_SR-27
     * @test.input mandatory request parameters: - any single term query which
     *             contains umlaut and results in some hits - existing database
     * @test.inputDescription
     * @test.expected
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR27() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", new String("escidoc.metadata=t\u00fcbingen"
            .getBytes(ClientBase.DEFAULT_CHARSET), ClientBase.DEFAULT_CHARSET));
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * : Availability of any.. indexes for org-units.
     * 
     * @test.name : search in any... indexes
     * @test.id SB_OU_SR-28
     * @test.input mandatory request parameters: - any single term query which
     *             searches an any.. index and results in some hits - existing
     *             database
     * @test.inputDescription
     * @test.expected
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR28() throws Exception {
        Pattern orgUnitIdPattern =
            Pattern.compile("(.*)\\$\\{ORGUNIT(.*?)\\}(.*)");
        Matcher orgUnitIdMatcher = orgUnitIdPattern.matcher("");
        HashMap<String, String> parameters = new HashMap<String, String>();
        for (String indexName : Constants.ORG_UNIT_INDEX_USERDEFINED_SEARCHES
            .keySet()) {
            HashMap<String, String> info =
                Constants.ORG_UNIT_INDEX_USERDEFINED_SEARCHES.get(indexName);
            orgUnitIdMatcher.reset(info.get("searchString"));
            String searchString = info.get("searchString");
            if (orgUnitIdMatcher.matches()) {
                int indexNum = new Integer(orgUnitIdMatcher.group(2));
                searchString =
                    orgUnitIdMatcher.replaceAll("$1" + orgUnitIds[indexNum]
                        + "$3");
            }
            String expectedHits = info.get("expectedHits");
            parameters.put("query", searchString);
            String response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("expected " + expectedHits + " for " + indexName
                + " but was " + getNumberOfHits(response), expectedHits,
                getNumberOfHits(response));
        }
    }

    /**
     * : Availability of properties indexes for org-units.
     * 
     * @test.name : search in property indexes, restrict to item
     * @test.id SB_OU_SR-29
     * @test.input mandatory request parameters: - any single term query which
     *             searches an properties index and results in some hits -
     *             existing database
     * @test.inputDescription
     * @test.expected
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR29() throws Exception {
        Pattern orgUnitIdPattern =
            Pattern.compile("(.*)\\$\\{ORGUNIT(.*?)\\}(.*)");
        Matcher orgUnitIdMatcher = orgUnitIdPattern.matcher("");
        HashMap<String, String> parameters = new HashMap<String, String>();
        for (String indexName : Constants.ORG_UNIT_INDEX_PROPERTIES_SEARCHES
            .keySet()) {
            HashMap<String, String> info =
                Constants.ORG_UNIT_INDEX_PROPERTIES_SEARCHES.get(indexName);
            orgUnitIdMatcher.reset(info.get("searchString"));
            String searchString = info.get("searchString");
            if (orgUnitIdMatcher.matches()) {
                int indexNum = new Integer(orgUnitIdMatcher.group(2));
                searchString =
                    orgUnitIdMatcher.replaceAll("$1" + orgUnitIds[indexNum]
                        + "$3");
            }
            String expectedHits = info.get("expectedHits");
            parameters.put("query", searchString);
            String response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("expected " + expectedHits + " for " + indexName
                + " but was " + getNumberOfHits(response), expectedHits,
                getNumberOfHits(response));
        }
    }

    /**
     * Test searching for ISSN in escidoc.metadata 1.
     * 
     * @test.name Search for ISSN
     * @test.id SB_OU_SR-30
     * @test.input mandatory request parameters: - query with
     *             escidoc.metadata=ISSN - existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found. Found object are items.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR30() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=ISSN");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(Integer.toString(Constants.NUM_ORG_UNITS),
            getNumberOfHits(response));
    }

    /**
     * Test searching for ISSN in escidoc.metadata 2.
     * 
     * @test.name Search for ISSN
     * @test.id SB_OU_SR-31
     * @test.input mandatory request parameters: - query with
     *             escidoc.metadata=ISSN* - existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found. Found object are items.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR31() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=ISSN*");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(Integer.toString(Constants.NUM_ORG_UNITS),
            getNumberOfHits(response));
    }

    /**
     * Test searching for ISSN in escidoc.metadata 3.
     * 
     * @test.name Search for ISSN
     * @test.id SB_OU_SR-32
     * @test.input mandatory request parameters: - query with escidoc.metadata
     *             all ISSN 2-* - existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found. Found object are items.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR32() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata all \"ISSN 2-*\"");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("2", getNumberOfHits(response));
    }

    /**
     * Test searching for ISSN in escidoc.any-identifier 1.
     * 
     * @test.name Search for ISSN
     * @test.id SB_OU_SR-33
     * @test.input mandatory request parameters: - query with
     *             escidoc.any-identifier=ISSN:2-* - existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found. Found object are items.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR33() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.any-identifier=ISSN:2-*");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("2", getNumberOfHits(response));
    }

    /**
     * Test searching for ISSN in escidoc.any-identifier 2.
     * 
     * @test.name Search for ISSN
     * @test.id SB_OU_SR-34
     * @test.input mandatory request parameters: - query with
     *             escidoc.any-identifier=ISSN:2-01-01 - existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found. Found object are items.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR34() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.any-identifier=ISSN:2-01-01");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));
    }

    /**
     * Test searching for ISSN in escidoc.any-identifier 3.
     * 
     * @test.name Search for ISSN
     * @test.id SB_OU_SR-35
     * @test.input mandatory request parameters: - query with
     *             escidoc.any-identifier=ISSN* - existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found. Found object are items.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR35() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.any-identifier=ISSN*");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(Integer.toString(Constants.NUM_ORG_UNITS),
            getNumberOfHits(response));
    }

    /**
     * Test searching for updated OrgUnits.
     * 
     * @test.name Search for updated OrgUnits
     * @test.id SB_OU_SR-36
     * @test.input mandatory request parameters: - query with
     *             escidoc.public-status=closed - existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found. Found object are items.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR36() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.public-status="
            + ORGANIZATIONAL_UNIT_STATUS_CLOSED);
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("0", getNumberOfHits(response));

        parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=updated");
        response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("0", getNumberOfHits(response));

        update();
    }

    /**
     * Test searching for updated OrgUnits.
     * 
     * @test.name Search for updated OrgUnits
     * @test.id SB_OU_SR-37
     * @test.input mandatory request parameters: - query with
     *             escidoc.public-status=closed - existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found. Found object are items.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR37() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.public-status="
            + ORGANIZATIONAL_UNIT_STATUS_CLOSED);
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));

        parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=updated");
        response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));

        parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.metadata=london");
        response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));

    }

    /**
     * : Test searching for dates.
     * 
     * @test.name : check search result for correct dates
     * @test.id SBOUSR38
     * @test.input mandatory request parameters: - any single date term query
     *             which results in all hits - existing database
     * @test.inputDescription
     * @test.expected
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSR38() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("query", "escidoc.creation-date>\"" + startTime + "\"");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(Integer.toString(Constants.NUM_ORG_UNITS),
            getNumberOfHits(response));
    }

    /**
     * Operation without optional parameters.
     * 
     * @test.name Operation without optional parameters
     * @test.id SB_OU_SC-1
     * @test.input - mandatory request parameters (scanClause and Operation) -
     *             no optional request parameters
     * @test.inputDescription
     * @test.expected XML document in scanresponse Type-schema that contains the
     *                terms and information about the scan request requested
     *                term is 1st term in the response (default)
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSC1() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("operation", "scan");
        parameters.put("scanClause", "escidoc.metadata=orgunit");
        String response = scan(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(null, getDiagnostics(response));
    }

    /**
     * Operation with parameterresponse position (1).
     * 
     * @test.name Operation with parameterresponse position (1)
     * @test.id SB_OU_SC-2
     * @test.input - mandatory request parameters (scanClause and Operation) -
     *             request parameter response position = 0
     * @test.inputDescription
     * @test.expected XML document in scanresponse Type-schema that contains the
     *                terms and information about the scan request requested
     *                term is immediately before the 1st term in the response
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSC2() throws Exception {
        // parameter responsePosition is ignored by SRW-Server
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("operation", "scan");
        parameters.put("scanClause", "escidoc.metadata=orgunit");
        parameters.put("responsePosition", "0");
        try {
            String response = scan(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals(null, getDiagnostics(response));
        }
        catch (Exception e) {
        }
    }

    /**
     * Operation with parameterresponse position (2).
     * 
     * @test.name Operation with parameterresponse position (2)
     * @test.id SB_OU_SC-3
     * @test.input - mandatory request parameters (scanClause and Operation) -
     *             request parameter response position = 1
     * @test.inputDescription
     * @test.expected XML document in scanresponse Type-schema that contains the
     *                terms and information about the scan request requested
     *                term is 1st in the response (default)
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSC3() throws Exception {
        // parameter responsePosition is ignored by SRW-Server
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("operation", "scan");
        parameters.put("scanClause", "escidoc.metadata=orgunit");
        parameters.put("responsePosition", "1");
        String response = scan(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(null, getDiagnostics(response));
    }

    /**
     * Operation with parameterresponse position (3).
     * 
     * @test.name Operation with parameterresponse position (3)
     * @test.id SB_OU_SC-4
     * @test.input - mandatory request parameters (scanClause and Operation) -
     *             request parameter response position = -n (negative value)
     * @test.inputDescription
     * @test.expected error message describing the reason for failure
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSC4() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("operation", "scan");
        parameters.put("scanClause", "escidoc.metadata=orgunit");
        parameters.put("responsePosition", "-2");
        try {
            String response = scan(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("responsePosition", getDiagnostics(response));
        }
        catch (Exception e) {
        }
    }

    /**
     * Operation with parametermaximum terms (1).
     * 
     * @test.name Operation with parametermaximum terms (1)
     * @test.id SB_OU_SC-5
     * @test.input - mandatory request parameters (scanClause and Operation) -
     *             request parameter maximum terms = any positive integer
     * @test.inputDescription
     * @test.expected XML document in scanresponse Type-schema that contains the
     *                terms and information about the scan request requested
     *                term is 1st term in the response (default)
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSC5() throws Exception {
        // parameter maximumTerms is ignored by SRW-Server
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("operation", "scan");
        parameters.put("scanClause", "escidoc.metadata=orgunit");
        parameters.put("maximumTerms", "4");
        String response = scan(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(null, getDiagnostics(response));
    }

    /**
     * Operation with parametermaximum terms (2).
     * 
     * @test.name Operation with parametermaximum terms (2)
     * @test.id SB_OU_SC-6
     * @test.input - mandatory request parameters (scanClause and Operation) -
     *             request parameter maximum terms = any negative integer
     * @test.inputDescription
     * @test.expected error message describing the reason for failure
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSC6() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("operation", "scan");
        parameters.put("scanClause", "escidoc.metadata=orgunit");
        parameters.put("maximumTerms", "-2");
        try {
            String response = scan(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("maximumTerms", getDiagnostics(response));
        }
        catch (Exception e) {
        }
    }

    /**
     * Operation with parameter stylesheet (1).
     * 
     * @test.name Operation with parameter stylesheet (1)
     * @test.id SB_OU_SC-7
     * @test.input - mandatory request parameters (scanClause and Operation) -
     *             request parameter stylesheet = existent stylesheet
     * 
     * @test.inputDescription
     * @test.expected
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSC7() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("operation", "scan");
        parameters.put("scanClause", "escidoc.metadata=orgunit");
        parameters.put("stylesheet",
            "http://escidev5:8080/srw/scanResponse.xsl");
        String response = scan(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(null, getDiagnostics(response));
    }

    /**
     * Operation with parameter stylesheet (2).
     * 
     * @test.name Operation with parameter stylesheet (2)
     * @test.id SB_OU_SC-8
     * @test.input mandatory request parameters (scanClause and Operation) -
     *             request parameter stylesheet = non-existent stylesheet
     * @test.inputDescription
     * @test.expected error message describing the reason for failure
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBOUSC8() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("operation", "scan");
        parameters.put("scanClause", "escidoc.metadata=orgunit");
        parameters.put("stylesheet", "http://escidev5:8080/srw/yxr.xsl");
        String response = scan(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(null, getDiagnostics(response));
    }

}
