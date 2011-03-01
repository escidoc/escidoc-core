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

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.HttpHelper;

/**
 * Test if IndexSearcher gets reloaded when 
 * an object is added or deleted in the index.
 * 
 * @author MIH
 * 
 */
@RunWith(value = Parameterized.class)
public class ExpiredIndexSearcherTest extends SearchTestBase {

    private static final String INDEX_NAME = "item_container_admin";

    /**
     * @param transport
     *            The transport identifier.
     */
    public ExpiredIndexSearcherTest(final int transport) {
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
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
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
    public void testExpiredIndexSearcher() throws Exception {

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
        String xmlData =
            EscidocRestSoapTestBase.getTemplateAsString(
                TEMPLATE_ITEM_SEARCH_PATH, "escidoc_search_item0" + "_"
                    + getTransport(false) + ".xml");
        item.create(xmlData);
        HashMap<String, String> parameters = new HashMap<String, String>();
        HashMap<String, String> scanParameters = new HashMap<String, String>();
        parameters.put("query", "PID=escidoc*");
        scanParameters.put("operation", "scan");
        scanParameters.put("scanClause", "PID=\"\"");
        
        String response = search(parameters, INDEX_NAME);
        String scanResponse = scan(parameters, INDEX_NAME);
        assertEquals("1", getNumberOfHits(response));
        assertEquals("1", getNumberOfHits(scanResponse));

        String itemXml = item.create(xmlData);
        String itemId = getObjidValue(itemXml);
        scanResponse = scan(parameters, INDEX_NAME);
        response = search(parameters, INDEX_NAME);
        assertEquals("1", getNumberOfHits(response));
        assertEquals("2", getNumberOfHits(scanResponse));

        item.delete(itemId);
        response = search(parameters, INDEX_NAME);
        scanResponse = scan(parameters, INDEX_NAME);
        assertEquals("1", getNumberOfHits(response));
        assertEquals("1", getNumberOfHits(scanResponse));
    }

}
