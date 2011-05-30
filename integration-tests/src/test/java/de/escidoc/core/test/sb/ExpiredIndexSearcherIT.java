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

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.HttpHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Test if IndexSearcher gets reloaded when an object is added or deleted in the index.
 *
 * @author Michael Hoppe
 */
public class ExpiredIndexSearcherIT extends SearchTestBase {

    private static final String INDEX_NAME = "item_container_admin";

    public ExpiredIndexSearcherIT() {
        item = new ItemHelper();
        container = new ContainerHelper();
    }

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
    }

    /**
     * Test searching for a single term.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testExpiredIndexSearcher() throws Exception {

        // create empty indices/////////////////////////////////////////////////
        String urlParameters =
            "?operation=updateIndex" + "&action=createEmpty" + "&repositoryName=escidocrepository" + "&indexName=";
        String httpUrl =
            getFrameworkUrl() + de.escidoc.core.test.common.client.servlet.Constants.FEDORAGSEARCH_BASE_URI
                + urlParameters;
        HttpHelper.executeHttpRequest(de.escidoc.core.test.common.client.servlet.Constants.HTTP_METHOD_GET, httpUrl,
            null, null, null);
        // /////////////////////////////////////////////////////////////////////
        String xmlData =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_SEARCH_PATH, "escidoc_search_item0_rest.xml");
        item.create(xmlData);
        HashMap<String, String> parameters = new HashMap<String, String>();
        HashMap<String, String> scanParameters = new HashMap<String, String>();
        parameters.put(FILTER_PARAMETER_QUERY, "PID=escidoc*");
        scanParameters.put(FILTER_PARAMETER_OPERATION, FILTER_PARAMETER_SCAN);
        scanParameters.put(FILTER_PARAMETER_SCAN_CLAUSE, "PID=\"\"");

        String response = search(parameters, INDEX_NAME);
        String scanResponse = scan(scanParameters, INDEX_NAME);
        assertEquals("1", getNumberOfHits(response));
        assertEquals("1", getNumberOfScanHits(scanResponse));

        String itemXml = item.create(xmlData);
        String itemId = getObjidValue(itemXml);
        scanResponse = scan(scanParameters, INDEX_NAME);
        response = search(parameters, INDEX_NAME);
        assertEquals("2", getNumberOfHits(response));
        assertEquals("2", getNumberOfScanHits(scanResponse));

        item.delete(itemId);
        response = search(parameters, INDEX_NAME);
        scanResponse = scan(scanParameters, INDEX_NAME);
        assertEquals("1", getNumberOfHits(response));
        assertEquals("1", getNumberOfScanHits(scanResponse));
    }

}
