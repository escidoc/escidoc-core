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

import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test the implementation of the search resource.
 * 
 * @author MIH
 * 
 */
@RunWith(value = Parameterized.class)
public class SearchTest extends SearchTestBase {

    private static final String INDEX_NAME = "escidoc_all";

    private static String[] itemIds = null;

    private static String[] containerIds = null;

    private static HashMap<String, HashMap<String, String>> versionCheckMap =
        new HashMap<String, HashMap<String, String>>();

    private static int methodCounter = 0;

    private static String startTime = "";

    /**
     * @param transport
     *            The transport identifier.
     */
    public SearchTest(final int transport) {
        super(transport);
        item = new ItemHelper(transport);
        container = new ContainerHelper(transport);
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
            methodCounter = 0;
            deprepare();
            versionCheckMap =
                new HashMap<String, HashMap<String, String>>();
        }
    }

    /**
     * insert item(s) into system for the tests.
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
        log.info("starting SearchTest at " 
                + new DateTime(System.currentTimeMillis() 
                + (60 * 60 * 1000), DateTimeZone.UTC).toString());
        // create empty indices/////////////////////////////////////////////////
        String urlParameters =
            "?operation=updateIndex" + "&action=createEmpty"
                + "&repositoryName=escidocrepository" + "&indexName=";
        String httpUrl =
            HttpHelper
                .createUrl(
                    de.escidoc.core.test.common.client.servlet.Constants.PROTOCOL,
                    de.escidoc.core.test.common.client.servlet.Constants.HOST_PORT,
                    de.escidoc.core.test.common.client.servlet.Constants.FEDORAGSEARCH_BASE_URI
                        + urlParameters);
        HttpHelper
            .executeHttpRequest(
                de.escidoc.core.test.common.client.servlet.Constants.HTTP_METHOD_GET,
                httpUrl, null, null, null);
        // /////////////////////////////////////////////////////////////////////

        startTime =
            new DateTime(System.currentTimeMillis(), DateTimeZone.UTC)
                .toString();
        // Create Container/////////////////////////////////////////////////////
        try {
            containerIds = new String[Constants.NUM_CONTAINERS];
            for (int i = 0; i < Constants.NUM_CONTAINERS; i++) {
                String xmlData =
                    EscidocRestSoapTestBase.getTemplateAsString(
                        TEMPLATE_CONTAINER_SEARCH_PATH, 
                        "escidoc_search_container" + i
                            + "_" + getTransport(false) + ".xml");
                String xml = container.create(xmlData);
                String lastModDate = getLastModificationDate(xml);
                containerIds[i] = getId(xml);

                // submit container
                container.submit(containerIds[i],
                    "<param last-modification-date=\"" + lastModDate + "\" />");

                // assign pids
                String pidParam = getContainerPidParam(containerIds[i]);
                container.assignObjectPid(containerIds[i], pidParam);
                pidParam = getContainerPidParam(containerIds[i]);
                container.assignVersionPid(containerIds[i] + ":1", pidParam);

                // release container
                xml = container.retrieve(containerIds[i]);
                lastModDate = getLastModificationDate(xml);
                container.release(containerIds[i],
                    "<param last-modification-date=\"" + lastModDate + "\" />");
                xml = container.retrieve(containerIds[i]);
                lastModDate = getLastModificationDate(xml);
                xml = xml.replaceAll("Hoppe", "Hoppe1");
                container.update(containerIds[i], xml);
                versionCheckMap.put(
                    de.escidoc.core.test.common.client.servlet
                    .Constants.CONTAINER_BASE_URI + "/" + containerIds[i],
                    new HashMap<String, String>() {
                        private static final long serialVersionUID =
                            -615466009125477112L;

                        {
                            put("objectType", "container");
                            put("expectedPublicStatus", "released");
                            put("expectedVersionNumber", "12");
                            put("expectedLatestVersionNumber", "12");
                        }
                    });
            }
        }
        catch (Exception e) {
            log.error(e);
        }
        // /////////////////////////////////////////////////////////////////////

        try {
            itemIds = new String[Constants.NUM_ITEMS];
            for (int i = 0; i < Constants.NUM_ITEMS; i++) {
                // Create Item submit and release it //////////////////////////
                String xmlData =
                    EscidocRestSoapTestBase.getTemplateAsString(
                        TEMPLATE_ITEM_SEARCH_PATH, "escidoc_search_item" + i + "_"
                            + getTransport(false) + ".xml");
                String xml = container.createItem(containerIds[0], xmlData);
                String lastModDate = getLastModificationDate(xml);
                itemIds[i] = getId(xml);

                // submit item
                item.submit(itemIds[i], "<param last-modification-date=\""
                    + lastModDate + "\" />");

                // assignPids
                Document itemDoc = EscidocRestSoapTestBase.getDocument(xml);
                String componentId = getComponentObjidValue(itemDoc, 1);
                String pidParam = getItemPidParam(itemIds[i]);
                item.assignContentPid(itemIds[i], componentId, pidParam);
                pidParam = getItemPidParam(itemIds[i]);
                item.assignObjectPid(itemIds[i], pidParam);
                // version pid to item[0] is assigned in a later test
                // Sorry, but it depends on configuration if a release of an
                // Item/container is possible without versionPid. Therefore has
                // the 'later' test to operate on it own item.
                // if (i > 0) {
                String versionId = itemIds[i] + ":1";
                pidParam = getItemPidParam(versionId);
                item.assignVersionPid(versionId, pidParam);
                // }

                // release item
                xml = item.retrieve(itemIds[i]);
                lastModDate = getLastModificationDate(xml);
                item.release(itemIds[i], "<param last-modification-date=\""
                    + lastModDate + "\" />");
                if (i % 2 == 0) {
                    xml = item.retrieve(itemIds[i]);
                    lastModDate = getLastModificationDate(xml);
                    xml = xml.replaceAll("Huffman", "Huffman1");
                    item.update(itemIds[i], xml);
                    versionCheckMap.put(
                        de.escidoc.core.test.common.client.servlet
                        .Constants.ITEM_BASE_URI + "/" + itemIds[i] + ":1" ,
                        new HashMap<String, String>() {
                            private static final long serialVersionUID =
                                -5739781891807617223L;

                            {
                                put("objectType", "item");
                                put("expectedPublicStatus", "released");
                                put("expectedVersionNumber", "1");
                                put("expectedLatestVersionNumber", "2");
                            }
                        });
                }
                else {
                    versionCheckMap.put(
                        de.escidoc.core.test.common.client.servlet
                        .Constants.ITEM_BASE_URI + "/" + itemIds[i],
                        new HashMap<String, String>() {
                            private static final long serialVersionUID =
                                -562673198784019069L;

                            {
                                put("objectType", "item");
                                put("expectedPublicStatus", "released");
                                put("expectedVersionNumber", "1");
                                put("expectedLatestVersionNumber", "1");
                            }
                        });
                }

                // ////////////////////////////////////////////////////////////
            }
        }
        catch (Exception e) {
            log.error(e);
        }
        try {
            // release container with items as new members
            // triggers indexing
            String xml = container.retrieve(containerIds[0]);
            String lastModDate = getLastModificationDate(xml);
            // submit container
            container.submit(containerIds[0],
                "<param last-modification-date=\"" + lastModDate + "\" />");
            String version =
                selectSingleNode(EscidocRestSoapTestBase.getDocument(xml),
                    "/container/properties/version/number").getTextContent();

            // assign pids
            String pidParam = getContainerPidParam(containerIds[0]);
            container.assignVersionPid(containerIds[0] + ":" + version,
                pidParam);

            // release container
            xml = container.retrieve(containerIds[0]);
            lastModDate = getLastModificationDate(xml);
            container.release(containerIds[0],
                "<param last-modification-date=\"" + lastModDate + "\" />");
        }
        catch (Exception e) {
            log.error(e);
        }
        waitForIndexerToAppear(itemIds[Constants.NUM_ITEMS - 1], INDEX_NAME);
        Thread.sleep(60000);
    }

    /**
     * insert item(s) into system for the tests.
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
    private void prepareZip() throws Exception {
        // create empty indices/////////////////////////////////////////////////
        String urlParameters =
            "?operation=updateIndex" + "&action=createEmpty"
                + "&repositoryName=escidocrepository" + "&indexName=";
        String httpUrl =
            HttpHelper
                .createUrl(
                    de.escidoc.core.test.common.client.servlet.Constants.PROTOCOL,
                    de.escidoc.core.test.common.client.servlet.Constants.HOST_PORT,
                    de.escidoc.core.test.common.client.servlet.Constants.FEDORAGSEARCH_BASE_URI
                        + urlParameters);
        HttpHelper
            .executeHttpRequest(
                de.escidoc.core.test.common.client.servlet.Constants.HTTP_METHOD_GET,
                httpUrl, null, null, null);
        // /////////////////////////////////////////////////////////////////////

        // Create Container/////////////////////////////////////////////////////
        try {
            containerIds = new String[Constants.NUM_CONTAINERS];
            for (int i = 0; i < Constants.NUM_CONTAINERS; i++) {
                String xmlData =
                    EscidocRestSoapTestBase.getTemplateAsString(
                        TEMPLATE_CONTAINER_SEARCH_PATH, 
                        "escidoc_search_container" + i
                            + "_" + getTransport(false) + ".xml");
                String xml = container.create(xmlData);
                String lastModDate = getLastModificationDate(xml);
                containerIds[i] = getId(xml);

                // submit container
                container.submit(containerIds[i],
                    "<param last-modification-date=\"" + lastModDate + "\" />");

                // assign pids
                String pidParam = getContainerPidParam(containerIds[i]);
                container.assignObjectPid(containerIds[i], pidParam);
                pidParam = getContainerPidParam(containerIds[i]);
                container.assignVersionPid(containerIds[i] + ":1", pidParam);

                // release container
                xml = container.retrieve(containerIds[i]);
                lastModDate = getLastModificationDate(xml);
                container.release(containerIds[i],
                    "<param last-modification-date=\"" + lastModDate + "\" />");

            }
        }
        catch (Exception e) {
            log.error(e);
        }
        // /////////////////////////////////////////////////////////////////////

        try {
            ZipInputStream zipinputstream = null;
            ZipEntry zipentry;
            zipinputstream =
                new ZipInputStream(new FileInputStream(
                    "C:/eprojects/eSciDocCoreTest/src/java"
                        + TEMPLATE_ITEM_PATH + "/all_items.zip"));
            itemIds = new String[100000];
            int i = 0;
            while ((zipentry = zipinputstream.getNextEntry()) != null) {
                int n;
                byte[] buf = new byte[8192];
                StringBuffer itemXml = new StringBuffer("");
                String entryName = zipentry.getName();
                if (entryName.matches(".*" + getTransport(false) + "\\.xml")) {
                    while ((n = zipinputstream.read(buf)) > -1) {
                        byte[] real = new byte[n];
                        for (int j = 0; j < n; j++) {
                            real[j] = buf[j];
                        }
                        itemXml.append(new String(real, "UTF-8"));
                    }
                    String xmlData = itemXml.toString();
                    String xml = container.createItem(containerIds[0], xmlData);
                    String lastModDate = getLastModificationDate(xml);
                    itemIds[i] = getId(xml);

                    // submit item
                    item.submit(itemIds[i], "<param last-modification-date=\""
                        + lastModDate + "\" />");

                    // assignPids
                    Document itemDoc =
                        EscidocRestSoapTestBase.getDocument(xml);
                    String componentId = getComponentObjidValue(itemDoc, 1);
                    String pidParam = getItemPidParam(itemIds[i]);
                    item.assignContentPid(itemIds[i], componentId, pidParam);
                    pidParam = getItemPidParam(itemIds[i]);
                    item.assignObjectPid(itemIds[i], pidParam);
                    // version pid to item[0] is assigned in a later test
                    // Sorry, but it depends on configuration if a release of an
                    // Item/container is possible without versionPid. Therefore
                    // has
                    // the 'later' test to operate on it own item.
                    // if (i > 0) {
                    String versionId = itemIds[i] + ":1";
                    pidParam = getItemPidParam(versionId);
                    item.assignVersionPid(versionId, pidParam);
                    // }

                    // release item
                    xml = item.retrieve(itemIds[i]);
                    lastModDate = getLastModificationDate(xml);
                    item.release(itemIds[i], "<param last-modification-date=\""
                        + lastModDate + "\" />");

                    // ////////////////////////////////////////////////////////////
                    i++;
                }
                zipinputstream.closeEntry();
            }
        }
        catch (Exception e) {
            log.error(e);
        }
        try {
            // release container with items as new members
            // triggers indexing
            String xml = container.retrieve(containerIds[0]);
            String lastModDate = getLastModificationDate(xml);
            // submit container
            container.submit(containerIds[0],
                "<param last-modification-date=\"" + lastModDate + "\" />");

            // assign pids
            String pidParam = getContainerPidParam(containerIds[0]);
            container.assignVersionPid(containerIds[0] + ":"
                + (Constants.NUM_ITEMS + 1), pidParam);

            // release container
            xml = container.retrieve(containerIds[0]);
            lastModDate = getLastModificationDate(xml);
            container.release(containerIds[0],
                "<param last-modification-date=\"" + lastModDate + "\" />");
        }
        catch (Exception e) {
            log.error(e);
        }
        waitForIndexerToAppear(containerIds[0], INDEX_NAME);
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
    @Test
    public void testSBEX1() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        String response = explain(parameters, INDEX_NAME);
        assertXmlValidExplainPlan(response);
        assertEquals("srw/search/" + INDEX_NAME, getDatabase(response));
        assertEquals(Constants.INDEX_FIELD_COUNT, getIndexFieldCount(response));
        assertEquals(Constants.SORT_FIELD_COUNT, getSortFieldCount(response));
    }

    /**
     * explain operation where operation=explain for existing database xyz is
     * explicitly given.
     * 
     * @test.name explain (2)
     * @test.id SB_EX-2
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
    public void testSBEX2() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_OPERATION, FILTER_PARAMETER_EXPLAIN);
        String response = explain(parameters, INDEX_NAME);
        assertXmlValidExplainPlan(response);
        assertEquals("srw/search/" + INDEX_NAME, getDatabase(response));
        assertEquals(Constants.INDEX_FIELD_COUNT, getIndexFieldCount(response));
        assertEquals(Constants.SORT_FIELD_COUNT, getSortFieldCount(response));
    }

    /**
     * explain operation with operation=explain for a not existing database zzz.
     * 
     * @test.name explain (2)
     * @test.id SB_EX-2
     * @test.input existing database
     * @test.inputDescription database has to exist
     * @test.expected error message describing the reason for failure
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBEX3() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_OPERATION, FILTER_PARAMETER_EXPLAIN);
        try {
            explain(parameters, "zzz");
            fail("No exception occurred on explain in non-existing database.");

        }
        catch (Exception e) {
            // FIXME: Assert exception
        }
    }

    /**
     * Test searching for a single term.
     * 
     * @test.name Single Term Search
     * @test.id SB_SR-1
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
    public void testSBSR1() throws Exception {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=motor");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(true, checkHighlighting(response));
        assertEquals("1", getNumberOfHits(response));
        assertEquals("1", getFirstRecord(response));
    }

    /**
     * Test searching for a single term without Highlighting.
     * 
     * @test.name Single Term Search without Highlighting
     * @test.id SB_SR-1
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
    public void testSBSR1_1() throws Exception {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=motor");
        parameters.put(FILTER_PARAMETER_OMIT_HIGHLIGHTING, "true");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(false, checkHighlighting(response));
        assertEquals("1", getNumberOfHits(response));
        assertEquals("1", getFirstRecord(response));
    }

    /**
     * Database not existing.
     * 
     * @test.name Database not existing
     * @test.id SB_SR-2
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
    public void testSBSR2() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=motor");
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
     * @test.id SB_SR-6
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
    public void testSBSR6() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.language=de");
        parameters.put(FILTER_PARAMETER_STARTRECORD, "3");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("3", getFirstRecord(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter startrecord \u2013 (2).
     * 
     * @test.name Request parameter startrecord \u2013 (2)
     * @test.id SB_SR-7
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
    public void testSBSR7() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.language=de");
        parameters.put(FILTER_PARAMETER_STARTRECORD, "0");
        String response = null;
        try {
            response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals(FILTER_PARAMETER_STARTRECORD, getDiagnostics(response));
        }
        catch (Exception e) {
        }
    }

    /**
     * Request parameter maximumRecords \u2013 (1).
     * 
     * @test.name Request parameter maximumRecords \u2013 (1)
     * @test.id SB_SR-8
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
    public void testSBSR8() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.language=de");
        parameters.put(FILTER_PARAMETER_MAXIMUMRECORDS, "1");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("2", getNextRecordPosition(response));
        assertEquals("10", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter maximumRecords \u2013 (2).
     * 
     * @test.name Request parameter maximumRecords \u2013 (2)
     * @test.id SB_SR-9
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
    public void testSBSR9() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.language=de");
        parameters.put(FILTER_PARAMETER_MAXIMUMRECORDS, "0");
        String response = null;
        try {
            response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals(
                    "1/java.lang.IllegalArgumentException: nDocs must be &gt; 0", 
                    getDiagnostics(response));
        }
        catch (Exception e) {
        }
    }

    /**
     * Request parameter maximumRecords \u2013 (3).
     * 
     * @test.name Request parameter maximumRecords \u2013 (3)
     * @test.id SB_SR-10
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
    public void testSBSR10() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.language=de");
        parameters.put(FILTER_PARAMETER_MAXIMUMRECORDS, "-1");
        try {
            String response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals(FILTER_PARAMETER_MAXIMUMRECORDS, getDiagnostics(response));
        }
        catch (Exception e) {
        }
    }

    /**
     * Request parameter recordPacking \u2013 (1).
     * 
     * @test.name Request parameter recordPacking \u2013 (1)
     * @test.id SB_SR-11
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
    public void testSBSR11() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=motor");
        parameters.put(FILTER_PARAMETER_RECORDPACKING, "xml");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(null, getDiagnostics(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter recordPacking \u2013 (2).
     * 
     * @test.name Request parameter recordPacking \u2013 (2)
     * @test.id SB_SR-12
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
    public void testSBSR12() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=motor");
        parameters.put(FILTER_PARAMETER_RECORDPACKING, "string");
        String response = search(parameters, INDEX_NAME);
        assertEquals(null, getDiagnostics(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter recordPacking \u2013 (3).
     * 
     * @test.name Request parameter recordPacking \u2013 (3)
     * @test.id SB_SR-13
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
    public void testSBSR13() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=motor");
        parameters.put(FILTER_PARAMETER_RECORDPACKING, "something");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("something", getDiagnostics(response));
    }

    /**
     * Request parameter recordSchema \u2013 (1).
     * 
     * @test.name Request parameter recordSchema \u2013 (1)
     * @test.id SB_SR-14
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
    public void testSBSR14() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=motor");
        parameters.put(FILTER_PARAMETER_RECORDSCHEMA, "default");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter recordSchema \u2013 (2).
     * 
     * @test.name Request parameter recordSchema \u2013 (2)
     * @test.id SB_SR-15
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
    public void testSBSR15() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=motor");
        parameters.put(FILTER_PARAMETER_RECORDSCHEMA, "none");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("none", getDiagnostics(response));
    }

    /**
     * Request parameter sortKeys \u2013 (1).
     * 
     * @test.name Request parameter sortKeys \u2013 (1)
     * @test.id SB_SR-16
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
    public void testSBSR16() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.language=de");
        parameters.put(FILTER_PARAMETER_SORTKEYS, "sort.escidoc.most-recent-date");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        String[] dates = response.split("created[\\s]");
        String[] datesToCheck =
            { "1980-01-25", "1980-01-26", "1980-01-27", "1980-01-28" };
        int j = 0;
        for (int i = 1; i < dates.length; i++) {
            String datepart = dates[i];
            if (!datepart.matches("(?s)[^0-9]*" + datesToCheck[j] + ".*")) {
                j++;
                if (!datepart.matches("(?s)[^0-9]*" + datesToCheck[j] + ".*")) {
                    assertTrue("wrong sortorder, should: " + datesToCheck[j]
                        + ", is: " + datepart, false);
                }
            }
        }
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter sortKeys \u2013 (2).
     * 
     * @test.name Request parameter sortKeys \u2013 (2)
     * @test.id SB_SR-17
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
    public void testSBSR17() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.language=de");
        parameters
            .put(FILTER_PARAMETER_SORTKEYS, "sort.escidoc.created,,0 sort.escidoc.title");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        String[] records = response.split("<record>");
        String[] valuesToCheck =
            { "", "1980-01-28|Antriebsvorrichtung aus einem",
                "1980-01-27|Verfahren und Vorrichtung",
                "1980-01-27|Verfahren zum Vermessen",
                "1980-01-27|Verfahren zur Steuerung",
                "1980-01-26|Methode zum Auffinden",
                "1980-01-26|Methode zur Herstellung",
                "1980-01-26|Verfahren zur direkten",
                "1980-01-25|Ber&#xFC;hrungs- bzw. Einklemmschutz",
                "1980-01-25|Elektrochemischer Gassensor",
                "1980-01-25|Verfahren zur thermischen" };
        assertEquals(records.length, valuesToCheck.length);
        for (int i = 1; i < records.length; i++) {
            String[] parts = valuesToCheck[i].split("\\|");
            if (!records[i].matches("(?s).*<dcterms:created.*?>" + parts[0]
                + ".*")
                || !records[i].toLowerCase().matches(
                    "(?s).*<dc:title.*?>" + parts[1].toLowerCase() + ".*")) {
                assertTrue("wrong sortorder, should: " + parts[1].toLowerCase()
                    + ", is: " + records[i].toLowerCase(), false);

            }

        }
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter sortKeys \u2013 (2).
     * 
     * @test.name Request parameter sortKeys \u2013 (2)
     * @test.id SB_SR-171
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - valid sortkey
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
    public void testSBSR171() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.language=de");
        parameters.put(FILTER_PARAMETER_SORTKEYS, "sort.escidoc.alternative");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        String[] records = response.split("<record>");
        String[] valuesToCheck =
            { "", "&#xC4;Driving device consisting of a motor and a gear",
                "&#xE4;Process for controlling a long-stroke positioning",
                "aMethod of retreiving documents",
                "Anti-nipping device for power operated parts",
                "Electrochemical gas sensor",
                "&#xD6;Method and device for calibrating the penetration",
                "&#xF6;METHOD FOR PRODUCING A BIOACTIVE",
                "Process of thermal oxidation of an implanted semiconductor",
                "&#xFC;METHOD FOR DIRECT METHANE PYROLYSIS",
                "&#xDC;Method of measuring a borehole" };
        assertEquals(records.length, valuesToCheck.length);
        for (int i = 1; i < records.length; i++) {
            if (!records[i].toLowerCase().matches(
                "(?s).*<dcterms:alternative.*?>"
                    + valuesToCheck[i].toLowerCase() + ".*")) {
                assertTrue("wrong sortorder, should: "
                    + valuesToCheck[i].toLowerCase() + ", is: "
                    + records[i].toLowerCase(), false);

            }

        }
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter sortKeys \u2013 (2).
     * 
     * @test.name Request parameter sortKeys \u2013 (2)
     * @test.id SB_SR-172
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - _relevance_ sortkey
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results search results are sorted by
     *                _relevance_ sortkeys
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR172() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.language=de");
        parameters.put(FILTER_PARAMETER_SORTKEYS, "_relevance_");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        Pattern scorePattern =
            Pattern.compile("(?s).*<search-result:score.*?>(.*?)<.*");
        Matcher scoreMatcher = scorePattern.matcher("");
        String[] records = response.split("<record>");
        assertEquals(records.length, 11);
        String score = null;
        for (int i = 1; i < records.length; i++) {
            scoreMatcher.reset(records[i].toLowerCase());
            if (scoreMatcher.find()) {
                if (score != null && scoreMatcher.group(1).compareTo(score) > 0) {
                    assertTrue("wrong sortorder", false);
                }
                score = scoreMatcher.group(1);
            }
            else {
                assertTrue("score not found", false);
            }
        }
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter sortKeys \u2013 (2).
     * 
     * @test.name Request parameter sortKeys \u2013 (2)
     * @test.id SB_SR-173
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - _relevance_ sortkey and title sortkey
     * @test.inputDescription
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results search results are sorted by
     *                _relevance_ and title sortkeys
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR173() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.language=de");
        parameters.put(FILTER_PARAMETER_SORTKEYS, "_relevance_ sort.escidoc.title");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("10", getNumberOfHits(response));

        Pattern scorePattern =
            Pattern.compile("(?s).*<search-result:score.*?>(.*?)<.*");
        Matcher scoreMatcher = scorePattern.matcher("");

        Pattern titlePattern =
            Pattern.compile("(?s).*?md-record[^>]*?name=\"escidoc\".*?"
                + "<[^>]*?title.*?>\\s*?(.*?)\\s*?<.*");
        Matcher titleMatcher = titlePattern.matcher("");

        String[] records = response.split("<record>");
        String score = null;
        String savedTitle = "0";
        for (int i = 1; i < records.length; i++) {
            scoreMatcher.reset(records[i].toLowerCase());
            if (scoreMatcher.find()) {
                int compare = 0;
                if (score != null) {
                    compare = scoreMatcher.group(1).compareTo(score);
                    if (compare > 0) {
                        assertTrue("wrong sortorder", false);
                    }
                    else if (compare < 0) {
                        savedTitle = "0";
                    }
                }
                score = scoreMatcher.group(1);
            }
            else {
                assertTrue("score not found", false);
            }

            String title = null;
            titleMatcher.reset(records[i]);
            if (titleMatcher.find()) {
                title = titleMatcher.group(1);
            }
            else {
                assertTrue("title not found", false);
            }
            if (title.compareToIgnoreCase(savedTitle) < 0) {
                assertTrue("wrong sortorder", false);
            }
            savedTitle = title;
        }
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter sortKeys \u2013 (3).
     * 
     * @test.name Request parameter sortKeys \u2013 (3)
     * @test.id SB_SR-18
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
    public void testSBSR18() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.language=de");
        parameters.put(FILTER_PARAMETER_SORTKEYS, "sort.escidoc.sonstwas,,0");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("10", getNumberOfHits(response));
    }

    /**
     * Request parameter sortKeys \u2013 (4).
     * 
     * @test.name Request parameter sortKeys \u2013 (4)
     * @test.id SB_SR-19
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - sortkey that sorts for
     *             escidoc.member-count This is a index-field that only occurs
     *             with containers --> This should sort the container at the
     *             bottom
     * @test.inputDescription
     * @test.expected error message describing the reason for failure
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR19() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=test*");
        parameters.put(FILTER_PARAMETER_SORTKEYS, "sort.escidoc.member-count");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        String[] records = response.split("<record>");
        String[] valuesToCheck =
            { "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:container[^>]*?>|<container[^>]*?>)" };
        for (int i = 1; i < records.length; i++) {
            String record = records[i];
            if (!record.matches("(?s).*?" + valuesToCheck[i - 1] + ".*")) {
                assertTrue("wrong sortorder, should: " + valuesToCheck[i - 1]
                    + ", is: " + record, false);
            }
        }
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter sortKeys \u2013 (4).
     * 
     * @test.name Request parameter sortKeys \u2013 (4)
     * @test.id SB_SR-20
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database --- additional
     *             request parameter: - sortkey that sorts for
     *             escidoc.member-count This is a index-field that only occurs
     *             with containers --> This should sort the container at the top
     * @test.inputDescription
     * @test.expected error message describing the reason for failure
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR20() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=test*");
        parameters.put(FILTER_PARAMETER_SORTKEYS, "sort.escidoc.member-count,,0");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        String[] records = response.split("<record>");
        String[] valuesToCheck =
            { "(<[^>]*?:container[^>]*?>|<container[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)",
                "(<[^>]*?:item[^>]*?>|<item[^>]*?>)" };
        for (int i = 1; i < records.length; i++) {
            String record = records[i];
            if (!record.matches("(?s).*?" + valuesToCheck[i - 1] + ".*")) {
                assertTrue("wrong sortorder, should: " + valuesToCheck[i - 1]
                    + ", is: " + record, false);
            }
        }
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * : Request parameter stylesheet \u2013 (1).
     * 
     * @test.name : Request parameter stylesheet \u2013 (1)
     * @test.id SB_SR-21
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
    public void testSBSR21() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.language=de");
        parameters.put(FILTER_PARAMETER_STYLESHEET,
            "http://escidev5:8080/srw/searchRetrieveResponse.xsl");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("10", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Request parameter stylesheet \u2013 (2).
     * 
     * @test.name Request parameter stylesheet \u2013 (2)
     * @test.id SB_SR-22
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
    public void testSBSR22() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.language=de");
        parameters.put(FILTER_PARAMETER_STYLESHEET, "http://escidev5:8080/srw/xyz.xsl");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("10", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Boolean Operator AND.
     * 
     * @test.name Boolean Operator AND
     * @test.id SB_SR-23
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
    public void testSBSR23() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata all \"motor getriebe\"");
        String response = search(parameters, INDEX_NAME);
        assertEquals("1", getNumberOfHits(response));
        parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY,
            "escidoc.metadata=motor and escidoc.title=getriebe");
        response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Boolean Operator OR .
     * 
     * @test.name Boolean Operator OR
     * @test.id SB_SR-24
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
    public void testSBSR24() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata any \"motor automatic\"");
        String response = search(parameters, INDEX_NAME);
        assertEquals("2", getNumberOfHits(response));
        parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY,
            "escidoc.metadata=motor or escidoc.metadata=automatic");
        response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("2", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Use of * as Wildcard (1).
     * 
     * @test.name Use of * as Wildcard (1)
     * @test.id SB_SR-25
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
    public void testSBSR25() throws Exception {
        // Left truncation not supported
    }

    /**
     * Use of * as Wildcard (2).
     * 
     * @test.name Use of * as Wildcard (2)
     * @test.id SB_SR-26
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
    public void testSBSR26() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=be*g*");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("3", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Use of * as Wildcard (3).
     * 
     * @test.name Use of * as Wildcard (3)
     * @test.id SB_SR-27
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
    public void testSBSR27() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=be*");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("4", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Use of ? as Wildcard (1).
     * 
     * @test.name Use of ? as Wildcard (1)
     * @test.id SB_SR-28
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
    public void testSBSR28() throws Exception {
        // Left truncation not supported
    }

    /**
     * Use of ? as Wildcard (2).
     * 
     * @test.name Use of ? as Wildcard (2)
     * @test.id SB_SR-29
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
    public void testSBSR29() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=b??g");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("2", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Use of ? as Wildcard (3) .
     * 
     * @test.name Use of ? as Wildcard (3)
     * @test.id SB_SR-30
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
    public void testSBSR30() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=ber?");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("2", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * Fuzzy Search.
     * 
     * @test.name Fuzzy Search
     * @test.id SB_SR-31
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
    public void testSBSR31() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=/fuzzy dokument");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * : Umlaut as request-parameter .
     * 
     * @test.name : Umlaut as request-parameter \u2013 (1)
     * @test.id SB_SR-32
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
    public void testSBSR32() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, new String(
            "escidoc.metadata=patentanw\u00e4lte"
                .getBytes(ClientBase.DEFAULT_CHARSET),
            ClientBase.DEFAULT_CHARSET));
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * : UTF-8 characters in fulltext as request-parameter .
     * 
     * @test.name : UTF-8 characters in fulltext as request-parameter \u2013 (1)
     * @test.id SB_SR-33
     * @test.input mandatory request parameters: - any single term query which
     *             contains Special sign and results in some hits - existing
     *             database
     * @test.inputDescription
     * @test.expected
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR33() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, new String(
            "escidoc.fulltext=\u7b80\u4f53\u4e2d\u6587\u7f51\u9875"
                .getBytes(ClientBase.DEFAULT_CHARSET),
            ClientBase.DEFAULT_CHARSET));
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("10", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * : UTF-8 characters as request-parameter .
     * 
     * @test.name : UTF-8 characters as request-parameter \u2013 (1)
     * @test.id SB_SR-34
     * @test.input mandatory request parameters: - any single term query which
     *             contains Special sign and results in some hits - existing
     *             database
     * @test.inputDescription
     * @test.expected
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR34() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, new String(
            ("escidoc.any-source=\u5161\u4e5f\u5305\u56e0\u6c98"
                + "\u6c13\u4fb7\u67f5\u82d7\u5b6b\u5b6b\u8ca1")
                .getBytes(ClientBase.DEFAULT_CHARSET),
            ClientBase.DEFAULT_CHARSET));
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("10", getNumberOfHits(response));
        assertEquals(true, checkHighlighting(response));
    }

    /**
     * : Availability of userdefined indexes for items.
     * 
     * @test.name : search in userdefined indexes, restrict to items
     * @test.id SB_SR-35
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
    public void testSBSR35() throws Exception {
        Pattern itemIdPattern = Pattern.compile("(.*)\\$\\{ITEM_ID\\}(.*)");
        Matcher itemIdMatcher = itemIdPattern.matcher("");
        HashMap<String, String> parameters = new HashMap<String, String>();
        for (String indexName : Constants.ITEM_INDEX_USERDEFINED_SEARCHES
            .keySet()) {
            HashMap<String, String> info =
                Constants.ITEM_INDEX_USERDEFINED_SEARCHES.get(indexName);
            String searchString =
                itemIdMatcher.reset(info.get("searchString")).replaceAll(
                    "$1" + itemIds[3] + "$2");
            String expectedHits = info.get("expectedHits");
            parameters.put(FILTER_PARAMETER_QUERY, searchString
                + " and escidoc.objecttype=item");
            String response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("expected " + expectedHits + " for " + indexName
                + " but was " + getNumberOfHits(response), expectedHits,
                getNumberOfHits(response));
        }
    }

    /**
     * : Availability of userdefined indexes for container.
     * 
     * @test.name : search in userdefined indexes, restrict to container
     * @test.id SB_SR-36
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
    public void testSBSR36() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        for (String indexName : Constants.CONTAINER_INDEX_USERDEFINED_SEARCHES
            .keySet()) {
            HashMap<String, String> info =
                Constants.CONTAINER_INDEX_USERDEFINED_SEARCHES.get(indexName);
            String searchString = info.get("searchString");
            String expectedHits = info.get("expectedHits");
            parameters.put(FILTER_PARAMETER_QUERY, searchString
                + " and escidoc.objecttype=container");
            String response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("expected " + expectedHits + " for " + indexName
                + " but was " + getNumberOfHits(response), expectedHits,
                getNumberOfHits(response));
        }
    }

    /**
     * : Availability of properties indexes for item.
     * 
     * @test.name : search in property indexes, restrict to item
     * @test.id SB_SR-37
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
    public void testSBSR37() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        for (String indexName : Constants.ITEM_INDEX_PROPERTIES_SEARCHES
            .keySet()) {
            HashMap<String, String> info =
                Constants.ITEM_INDEX_PROPERTIES_SEARCHES.get(indexName);
            String searchString = info.get("searchString");
            String expectedHits = info.get("expectedHits");
            parameters.put(FILTER_PARAMETER_QUERY, searchString
                + " and escidoc.objecttype=item");
            String response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("expected " + expectedHits + " for " + indexName
                + " but was " + getNumberOfHits(response), expectedHits,
                getNumberOfHits(response));
        }
    }

    /**
     * : Availability of properties indexes for container.
     * 
     * @test.name : search in property indexes, restrict to container
     * @test.id SB_SR-38
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
    public void testSBSR38() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        for (String indexName : Constants.CONTAINER_INDEX_PROPERTIES_SEARCHES
            .keySet()) {
            HashMap<String, String> info =
                Constants.CONTAINER_INDEX_PROPERTIES_SEARCHES.get(indexName);
            String searchString = info.get("searchString");
            String expectedHits = info.get("expectedHits");
            parameters.put(FILTER_PARAMETER_QUERY, searchString
                + " and escidoc.objecttype=container");
            String response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("expected " + expectedHits + " for " + indexName
                + " but was " + getNumberOfHits(response), expectedHits,
                getNumberOfHits(response));
        }
    }

    /**
     * : Availability of component indexes for item.
     * 
     * @test.name : search in component indexes, restrict to item
     * @test.id SB_SR-39
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
    public void testSBSR39() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        for (String indexName : Constants.ITEM_INDEX_COMPONENTS_SEARCHES
            .keySet()) {
            HashMap<String, String> info =
                Constants.ITEM_INDEX_COMPONENTS_SEARCHES.get(indexName);
            String searchString = info.get("searchString");
            String expectedHits = info.get("expectedHits");
            parameters.put(FILTER_PARAMETER_QUERY, searchString
                + " and escidoc.objecttype=item");
            String response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("expected " + expectedHits + " for " + indexName
                + " but was " + getNumberOfHits(response), expectedHits,
                getNumberOfHits(response));
        }
    }

    /**
     * Test searching for a single term and check availability of version pids,
     * see issue 370.
     * 
     * @test.name Version Pid in Item - Issue 370
     * @test.id SB_SR-40
     * @test.input mandatory request parameters: - any single term query -
     *             existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found. Found object has set version
     *                pid.
     * @test.status Implemented
     * @test.issues 
     *              http://www.escidoc-project.de/issueManagement/show_bug.cgi?id
     *              =370
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR40() throws Exception {
        if (item.getItemClient().getPidConfig(
            "cmm.Item.versionPid.setPidAfterRelease", "true")
            && item.getItemClient().getPidConfig(
                "cmm.Item.versionPid.releaseWithoutPid", "false")) {

            // first search for version-pid
            // should find no records
            String searchString =
                "escidoc.any-identifier=\"hdl:somehandle/test/" + itemIds[0]
                    + ":1\"";
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put(FILTER_PARAMETER_QUERY, searchString);
            String response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("0", getNumberOfHits(response));

            // add version pid to released item
            String latestVersionId = itemIds[0] + ":1";
            item.assignVersionPid(latestVersionId);
            waitForIndexerToAppear(itemIds[0], INDEX_NAME);

            // search again for version-pid
            // should find one record
            response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("1", getNumberOfHits(response));
        }
    }

    /**
     * Test searching for all containers.
     * 
     * @test.name Search for Containers
     * @test.id SB_SR-41
     * @test.input mandatory request parameters: - query with
     *             escidoc.objecttype=container - existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found. Found object is container.
     * @test.status Implemented
     * @test.issues http://www.escidoc-project.de/issueManagement/
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR41() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.objecttype=container");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));
    }

    /**
     * Test searching for all items.
     * 
     * @test.name Search for Items
     * @test.id SB_SR-42
     * @test.input mandatory request parameters: - query with
     *             escidoc.objecttype=item - existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found. Found object are items.
     * @test.status Implemented
     * @test.issues http://www.escidoc-project.de/issueManagement/
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR42() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.objecttype=item");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("10", getNumberOfHits(response));
    }

    /**
     * Test searching for ISSN in escidoc.metadata 1.
     * 
     * @test.name Search for ISSN
     * @test.id SB_SR-43
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
    public void testSBSR43() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=ISSN");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("11", getNumberOfHits(response));
    }

    /**
     * Test searching for ISSN in escidoc.metadata 2.
     * 
     * @test.name Search for ISSN
     * @test.id SB_SR-44
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
    public void testSBSR44() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata=ISSN*");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("11", getNumberOfHits(response));
    }

    /**
     * Test searching for ISSN in escidoc.metadata 3.
     * 
     * @test.name Search for ISSN
     * @test.id SB_SR-45
     * @test.input mandatory request parameters: - query with escidoc.metadata
     *             all ISSN 12* - existing database
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
    public void testSBSR45() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.metadata all \"ISSN 12*\"");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("6", getNumberOfHits(response));
    }

    /**
     * Test searching for ISSN in escidoc.any-identifier 1.
     * 
     * @test.name Search for ISSN
     * @test.id SB_SR-46
     * @test.input mandatory request parameters: - query with
     *             escidoc.any-identifier=ISSN:12* - existing database
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
    public void testSBSR46() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.any-identifier=ISSN:12*");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("6", getNumberOfHits(response));
    }

    /**
     * Test searching for ISSN in escidoc.any-identifier 2.
     * 
     * @test.name Search for ISSN
     * @test.id SB_SR-47
     * @test.input mandatory request parameters: - query with
     *             escidoc.any-identifier=ISSN:1271137 - existing database
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
    public void testSBSR47() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.any-identifier=ISSN:1271137");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));
    }

    /**
     * Test searching for ISSN in escidoc.any-identifier 3.
     * 
     * @test.name Search for ISSN
     * @test.id SB_SR-48
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
    public void testSBSR48() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.any-identifier=ISSN*");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("11", getNumberOfHits(response));
    }

    /**
     * Test searching for component-properties.
     * 
     * @test.name Search for component-pid
     * @test.id SB_SR-49
     * @test.input mandatory request parameters: - query with
     *             escidoc.component.pid=hdl:somehandle* - existing database
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
    public void testSBSR49() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.component.pid=hdl:somehandle*");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("10", getNumberOfHits(response));
    }

    /**
     * Test searching for component-properties.
     * 
     * @test.name Search for mime-type
     * @test.id SB_SR-50
     * @test.input mandatory request parameters: - query with
     *             escidoc.component.mime-type=application/pdf - existing
     *             database
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
    public void testSBSR50() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY,
            "escidoc.component.mime-type=\"application/pdf\"");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("10", getNumberOfHits(response));
    }

    /**
     * Test searching for component-metadata.
     * 
     * @test.name Search for complete-name in component
     * @test.id SB_SR-51
     * @test.input mandatory request parameters: - query with
     *             escidoc.component.complete-name=comp* - existing database
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
    public void testSBSR51() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.component.complete-name=comp*");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("10", getNumberOfHits(response));
    }

    /**
     * Test searching for component-metadata.
     * 
     * @test.name Check if pdf-fulltext is indexed.
     * @test.id SB_SR-52
     * @test.input mandatory request parameters: - query with
     *             escidoc.fulltext=pdffulltext - existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected All indexed items are found. If not found, pdfs where not
     *                indexed. pdfs visibility is private, so if they are not
     *                indexed, the access-rights are not correctly set while
     *                indexing.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR52() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.fulltext=pdffulltext");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("10", getNumberOfHits(response));
    }

    /**
     * Test searching for escidoc.most-recent-date.
     * 
     * @test.name Check if escidoc.most-recent-date of item is 1980*.
     * @test.id SB_SR-53
     * @test.input mandatory request parameters: - query with
     *             escidoc.most-recent-date=1980* - existing database
     * @test.inputDescription execute single term query on given database
     * @test.expected 10 indexed items are found.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR53() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.most-recent-date=\"1980*\"");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("11", getNumberOfHits(response));
    }

    /**
     * : Test searching for special signs.
     * 
     * @test.name : search for special signs
     * @test.id SB_SR-54
     * @test.input mandatory request parameters: - any single term query which
     *             results in some hits - existing database
     * @test.inputDescription
     * @test.expected
     * @test.status Implemented for Bug
     *              http://www.escidoc-project.de/issueManagement
     *              /show_bug.cgi?id=662
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR54() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        for (String indexName : Constants.ITEM_INDEX_SPECIAL_CHAR_SEARCHES
            .keySet()) {
            HashMap<String, String> info =
                Constants.ITEM_INDEX_SPECIAL_CHAR_SEARCHES.get(indexName);
            String searchString = info.get("searchString");
            String expectedHits = info.get("expectedHits");
            parameters.put(FILTER_PARAMETER_QUERY, searchString
                + " and escidoc.objecttype=item");
            String response = search(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals("expected " + expectedHits + " for " + indexName
                + " but was " + getNumberOfHits(response), expectedHits,
                getNumberOfHits(response));
        }
    }

    /**
     * : Test searching for special signs.
     * 
     * @test.name : check search result for correct versions
     * @test.id SB_SR-55
     * @test.input mandatory request parameters: - any single term query which
     *             results in all hits - existing database
     * @test.inputDescription
     * @test.expected
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR55() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.most-recent-date=\"1980*\"");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("11", getNumberOfHits(response));
        checkVersions(response, versionCheckMap);
    }

    /**
     * : Test searching for dates.
     * 
     * @test.name : check search result for correct dates
     * @test.id SB_SR-56
     * @test.input mandatory request parameters: - any single date term query
     *             which results in all hits - existing database
     * @test.inputDescription
     * @test.expected
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testSBSR56() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(
                FILTER_PARAMETER_QUERY, "escidoc.latest-release.date>\"" + startTime + "\"");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("11", getNumberOfHits(response));
        checkVersions(response, versionCheckMap);
    }

    /**
     * Operation without optional parameters.
     * 
     * @test.name Operation without optional parameters
     * @test.id SB_SC-1
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
    public void testSBSC1() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_OPERATION, FILTER_PARAMETER_SCAN);
        parameters.put(FILTER_PARAMETER_SCAN_CLAUSE, "escidoc.metadata=berg");
        String response = scan(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(null, getDiagnostics(response));
    }

    /**
     * Operation with parameterresponse position (1).
     * 
     * @test.name Operation with parameterresponse position (1)
     * @test.id SB_SC-2
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
    public void testSBSC2() throws Exception {
        // parameter responsePosition is ignored by SRW-Server
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_OPERATION, FILTER_PARAMETER_SCAN);
        parameters.put(FILTER_PARAMETER_SCAN_CLAUSE, "escidoc.metadata=berg");
        parameters.put(FILTER_PARAMETER_RESPONSEPOSITION, "0");
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
     * @test.id SB_SC-3
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
    public void testSBSC3() throws Exception {
        // parameter responsePosition is ignored by SRW-Server
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_OPERATION, FILTER_PARAMETER_SCAN);
        parameters.put(FILTER_PARAMETER_SCAN_CLAUSE, "escidoc.metadata=berg");
        parameters.put(FILTER_PARAMETER_RESPONSEPOSITION, "1");
        String response = scan(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(null, getDiagnostics(response));
    }

    /**
     * Operation with parameterresponse position (3).
     * 
     * @test.name Operation with parameterresponse position (3)
     * @test.id SB_SC-4
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
    public void testSBSC4() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_OPERATION, FILTER_PARAMETER_SCAN);
        parameters.put(FILTER_PARAMETER_SCAN_CLAUSE, "escidoc.metadata=berg");
        parameters.put(FILTER_PARAMETER_RESPONSEPOSITION, "-2");
        try {
            String response = scan(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals(FILTER_PARAMETER_RESPONSEPOSITION, getDiagnostics(response));
        }
        catch (Exception e) {
        }
    }

    /**
     * Operation with parametermaximum terms (1).
     * 
     * @test.name Operation with parametermaximum terms (1)
     * @test.id SB_SC-5
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
    public void testSBSC5() throws Exception {
        // parameter maximumTerms is ignored by SRW-Server
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_OPERATION, FILTER_PARAMETER_SCAN);
        parameters.put(FILTER_PARAMETER_SCAN_CLAUSE, "escidoc.metadata=berg");
        parameters.put(FILTER_PARAMETER_MAXIMUMTERMS, "4");
        String response = scan(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(null, getDiagnostics(response));
    }

    /**
     * Operation with parametermaximum terms (2).
     * 
     * @test.name Operation with parametermaximum terms (2)
     * @test.id SB_SC-6
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
    public void testSBSC6() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_OPERATION, FILTER_PARAMETER_SCAN);
        parameters.put(FILTER_PARAMETER_SCAN_CLAUSE, "escidoc.metadata=berg");
        parameters.put(FILTER_PARAMETER_MAXIMUMTERMS, "-2");
        try {
            String response = scan(parameters, INDEX_NAME);
            assertXmlValidSearchResult(response);
            assertEquals(FILTER_PARAMETER_MAXIMUMTERMS, getDiagnostics(response));
        }
        catch (Exception e) {
        }
    }

    /**
     * Operation with parameter stylesheet (1).
     * 
     * @test.name Operation with parameter stylesheet (1)
     * @test.id SB_SC-7
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
    public void testSBSC7() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_OPERATION, FILTER_PARAMETER_SCAN);
        parameters.put(FILTER_PARAMETER_SCAN_CLAUSE, "escidoc.metadata=berg");
        parameters.put(FILTER_PARAMETER_STYLESHEET,
            "http://escidev5:8080/srw/scanResponse.xsl");
        String response = scan(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(null, getDiagnostics(response));
    }

    /**
     * Operation with parameter stylesheet (2).
     * 
     * @test.name Operation with parameter stylesheet (2)
     * @test.id SB_SC-8
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
    public void testSBSC8() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_OPERATION, FILTER_PARAMETER_SCAN);
        parameters.put(FILTER_PARAMETER_SCAN_CLAUSE, "escidoc.metadata=berg");
        parameters.put(FILTER_PARAMETER_STYLESHEET, "http://escidev5:8080/srw/yxr.xsl");
        String response = scan(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals(null, getDiagnostics(response));
    }

    /**
     * withdraw items to delete from index.
     * 
     * @throws Exception
     *             If anything fails.
     */
    private void deprepare() throws Exception {
        // Withdraw
        // Items//////////////////////////////////////////////////////////
        if (itemIds != null) {
            for (int i = 0; i < itemIds.length; i++) {
                if (itemIds[i] != null && !itemIds[i].equals("")) {
                    String xml = item.retrieve(itemIds[i]);
                    String lastModDate = getLastModificationDate(xml);
                    item.withdraw(itemIds[i],
                        "<param last-modification-date=\"" + lastModDate
                            + "\">" + "<withdraw-comment>"
                            + "This is a withdraw comment."
                            + "</withdraw-comment>" + "</param>");
                    // ////////////////////////////////////////////////////////

                }
            }
            waitForIndexerToDisappear(itemIds[itemIds.length - 1], INDEX_NAME);
            for (int i = 0; i < itemIds.length; i++) {
                if (itemIds[i] != null && !itemIds[i].equals("")) {
                    // Do search. Must be 0
                    // results///////////////////////////////////////////
                    HashMap<String, String> parameters =
                        new HashMap<String, String>();
                    parameters.put(FILTER_PARAMETER_QUERY, "escidoc.objid=" + itemIds[i]);
                    String response = search(parameters, INDEX_NAME);
                    assertEquals("Withdrawn Item found:" + itemIds[i], 
                                        "0", getNumberOfHits(response));
                    // ////////////////////////////////////////////////////////
                }
            }
        }
        // Withdraw
        // Container//////////////////////////////////////////////////////////
        if (containerIds != null) {
            for (int i = 0; i < containerIds.length; i++) {
                if (containerIds[i] != null && !containerIds[i].equals("")) {
                    String xml = container.retrieve(containerIds[i]);
                    String lastModDate = getLastModificationDate(xml);
                    container.withdraw(containerIds[i],
                        "<param last-modification-date=\"" + lastModDate
                            + "\">" + "<withdraw-comment>"
                            + "This is a withdraw comment."
                            + "</withdraw-comment>" + "</param>");
                    // ////////////////////////////////////////////////////////

                }
            }
            waitForIndexerToDisappear(containerIds[containerIds.length - 1],
                INDEX_NAME);
            for (int i = 0; i < containerIds.length; i++) {
                if (containerIds[i] != null && !containerIds[i].equals("")) {
                    // Do search. Must be 0
                    // results///////////////////////////////////////////
                    HashMap<String, String> parameters =
                        new HashMap<String, String>();
                    parameters.put(FILTER_PARAMETER_QUERY, "escidoc.objid=" + containerIds[i]);
                    String response = search(parameters, INDEX_NAME);
                    assertEquals("Withdrawn Container found:" + containerIds[i],
                                                   "0", getNumberOfHits(response));
                    // ////////////////////////////////////////////////////////
                }
            }
        }

    }

}
