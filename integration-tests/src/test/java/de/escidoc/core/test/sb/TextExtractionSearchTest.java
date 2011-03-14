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
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Test the implementation of the search resource.
 * 
 * @author MIH
 * 
 */
@Ignore("Test the implementation of the search resource")
@RunWith(value = Parameterized.class)
public class TextExtractionSearchTest extends SearchTestBase {

    private static String itemId = null;

    private static int methodCounter = 0;
    
    private static final String INDEX_NAME = "escidoc_all";

    /**
     * @param transport
     *            The transport identifier.
     */
    public TextExtractionSearchTest(final int transport) {
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
        // create empty indices/////////////////////////////////////////////////
        String urlParameters =
            "?operation=updateIndex" + "&action=createEmpty"
                + "&repositoryName=escidocrepository" + "&INDEX_NAME=";
        String httpUrl =
            HttpHelper.createUrl(
                    de.escidoc.core.test.common.client.servlet
                    .Constants.PROTOCOL, 
                    de.escidoc.core.test.common.client.servlet
                    .Constants.HOST_PORT,
                    de.escidoc.core.test.common.client.servlet
                    .Constants.FEDORAGSEARCH_BASE_URI
                    + urlParameters);
        HttpHelper.executeHttpRequest(
                de.escidoc.core.test.common.client.servlet
                .Constants.HTTP_METHOD_GET
                , httpUrl
            , null, null, null);
        // /////////////////////////////////////////////////////////////////////

        try {
            // Create Item submit and release it //////////////////////////
            String xmlData =
                EscidocRestSoapTestBase.getTemplateAsString(
                    TEMPLATE_ITEM_PATH, "escidoc_text_extractor_error_item_"
                        + getTransport(false) + ".xml");
            String xml = item.create(xmlData);
            String lastModDate = getLastModificationDate(xml);
            itemId = getId(xml);

            // submit item
            item.submit(itemId, "<param last-modification-date=\""
                + lastModDate + "\" />");

            // assignPids
            Document itemDoc = EscidocRestSoapTestBase.getDocument(xml);
            String componentId = getComponentObjidValue(itemDoc, 1);
            String pidParam =
                getItemPidParam(itemId);
            item.assignContentPid(itemId, componentId, pidParam);
            pidParam =
                getItemPidParam(itemId);
            item.assignObjectPid(itemId, pidParam);
            // version pid to item[0] is assigned in a later test
            // Sorry, but it depends on configuration if a release of an
            // Item/container is possible without versionPid. Therefore has
            // the 'later' test to operate on it own item.
            // if (i > 0) {
            String versionId = itemId + ":1";
            pidParam = getItemPidParam(versionId);
            item.assignVersionPid(versionId, pidParam);
            // }

            // release item
            xml = item.retrieve(itemId);
            lastModDate = getLastModificationDate(xml);
            item.release(itemId, "<param last-modification-date=\""
                + lastModDate + "\" />");
            
            // ////////////////////////////////////////////////////////////
        }
        catch (Exception e) {
            log.error(e);
        }
        waitForIndexerToAppear(itemId, INDEX_NAME);
    }

    /**
     * Test searching for a all objects.
     * 
     * @test.name All Objects Search
     * @test.id SB_TEERR_SR-1
     * @test.input mandatory request parameters: - e* -
     *             existing database
     * @test.inputDescription execute all objects query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Ignore("Test searching for a all objects")
    @Test
    public void testSBTEERRRSR1() throws Exception {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.objid=e*");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));
        assertEquals("1", getFirstRecord(response));
    }

    /**
     * Test searching for objects where pdf-text-extraction failed.
     * 
     * @test.name pdf-text-extraction failed Search
     * @test.id SB_TEERR_SR-2
     * @test.input mandatory request parameters: - textfrompdffilenotextractable -
     *             existing database
     * @test.inputDescription execute all objects query on given database
     * @test.expected List of search records according to eSciDoc Default Schema
     *                for search results first record to be returned ist at the
     *                1st position in the sequence of matched records. only
     *                released objects are found.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Ignore("Test searching for objects where pdf-text-extraction failed")
    @Test
    public void testSBTEERRRSR2() throws Exception {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "escidoc.fulltext=textfrompdffilenotextractable");
        String response = search(parameters, INDEX_NAME);
        assertXmlValidSearchResult(response);
        assertEquals("1", getNumberOfHits(response));
        assertEquals("1", getFirstRecord(response));
    }

}
