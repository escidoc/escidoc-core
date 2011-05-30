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
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

/**
 * Test the implementation of the search resource.
 *
 * @author Michael Hoppe
 */
@Ignore("Test the implementation of the search resource")
public class TextExtractionSearchIT extends SearchTestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextExtractionSearchIT.class);

    private static String itemId = null;

    private static int methodCounter = 0;

    private static final String INDEX_NAME = "escidoc_all";

    public TextExtractionSearchIT() {
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
            methodCounter = 0;
        }
    }

    /**
     * insert item(s) into system for the tests.
     *
     * @throws Exception If anything fails.
     */
    private void prepare() throws Exception {
        // create empty indices/////////////////////////////////////////////////
        String urlParameters =
            "?operation=updateIndex" + "&action=createEmpty" + "&repositoryName=escidocrepository" + "&INDEX_NAME=";
        String httpUrl =
            getFrameworkUrl() + de.escidoc.core.test.common.client.servlet.Constants.FEDORAGSEARCH_BASE_URI
                + urlParameters;
        HttpHelper.executeHttpRequest(de.escidoc.core.test.common.client.servlet.Constants.HTTP_METHOD_GET, httpUrl,
            null, null, null);
        // /////////////////////////////////////////////////////////////////////

        try {
            // Create Item submit and release it //////////////////////////
            String xmlData =
                EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH,
                    "escidoc_text_extractor_error_item_rest.xml");
            String xml = item.create(xmlData);
            String lastModDate = getLastModificationDate(xml);
            itemId = getId(xml);

            // submit item
            item.submit(itemId, "<param last-modification-date=\"" + lastModDate + "\" />");

            // assignPids
            Document itemDoc = EscidocAbstractTest.getDocument(xml);
            String componentId = getComponentObjidValue(itemDoc, 1);
            String pidParam = getItemPidParam(itemId);
            item.assignContentPid(itemId, componentId, pidParam);
            pidParam = getItemPidParam(itemId);
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
            item.release(itemId, "<param last-modification-date=\"" + lastModDate + "\" />");

            // ////////////////////////////////////////////////////////////
        }
        catch (final Exception e) {
            LOGGER.error("", e);
        }
        waitForIndexerToAppear(itemId, INDEX_NAME);
    }

    /**
     * Test searching for a all objects.
     *
     * @throws Exception If anything fails.
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
     * @throws Exception If anything fails.
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
