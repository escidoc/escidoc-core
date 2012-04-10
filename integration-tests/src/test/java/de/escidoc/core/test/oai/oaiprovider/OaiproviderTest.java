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
package de.escidoc.core.test.oai.oaiprovider;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Test the OaiProvider.
 * 
 * OaiProvider stores data dependent on configuration.
 * For tests it is configured to store data that have an md-record with name oaipmhtest
 * and metadata in schema http://www.openarchives.org/OAI/2.0/oai_dc.xsd
 * 
 * Set-definitions are defined by a search-query to index escidocoaipmh_all.
 * Only metadata of md-record with name escidoc is indexed!
 * 
 * For tests several items and containers are created. 
 * -Items with md-record with name=oaipmhtest and different elements md-record[@name=escidoc]/metadata/type
 * -Items with no md-record with name=oaipmhtest
 * -Containers with md-record with name=oaipmhtest and different elements md-record[@name=escidoc]/metadata/type
 * -Containers with no md-record with name=oaipmhtest
 * 
 * For tests, set-definitions are defined that refer to the different md-record[@name=escidoc]/metadata/type elements.
 * 
 *
 * @author Michael Hoppe
 */
@RunWith(value = Parameterized.class)
public class OaiproviderTest extends OaiproviderTestBase {

    private static int methodCounter = 0;

    private final int numItemsMusicType = 2;

    private final int numItemsVideoType = 2;

    private final int numItemsNoOai = 2;

    private final int numContainersMusicType = 2;

    private final int numContainersVideoType = 2;

    private final int numContainersNoOai = 2;

    private final int waitForPollMillies = 120000;

    private static String[] itemIdsMusic = null;

    private static String[] itemIdsVideo = null;

    private static String[] itemIdsNoOai = null;

    private static String[] containerIdsMusic = null;

    private static String[] containerIdsVideo = null;

    private static String[] containerIdsNoOai = null;

    private static String from = null;

    /**
     * @param transport The transport identifier.
     */
    public OaiproviderTest(final int transport) {
        super(transport);
    }

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        if (methodCounter == 0) {
            DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            from = new DateTime(System.currentTimeMillis(), DateTimeZone.UTC).toString(dateFormatter);
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
            deprepare();
        }
    }

    /**
     * prepare system for the tests.
     * Create 2 items that go into setdefinition with type=music
     * Create 2 items that go into setdefinition with type=video
     * Create 2 items that dont go into oaiprovider
     * Create 2 containers that go into setdefinition with type=music
     * Create 2 containers that go into setdefinition with type=video
     * Create 2 containers that dont go into oaiprovider
     *
     * @throws Exception If anything fails.
     */
    private void prepare() throws Exception {
        itemIdsMusic = new String[numItemsMusicType];
        itemIdsVideo = new String[numItemsVideoType];
        itemIdsNoOai = new String[numItemsNoOai];
        containerIdsMusic = new String[numContainersMusicType];
        containerIdsVideo = new String[numContainersVideoType];
        containerIdsNoOai = new String[numContainersNoOai];

        //create set-definitions
        createSuccessfully("escidoc_setdefinition_for_test.xml");
        createSuccessfully("escidoc_setdefinition_for_test1.xml");

        //create items in status released
        for (int i = 0; i < numItemsMusicType; i++) {
            itemIdsMusic[i] =
                getObjid(create(TEMPLATE_OAIPROVIDERTEST_ITEM_PATH + "/" + getTransport(false),
                    "escidoc_oai_item0.xml", "music", null, "music"));
        }
        for (int i = 0; i < numItemsVideoType; i++) {
            itemIdsVideo[i] =
                getObjid(create(TEMPLATE_OAIPROVIDERTEST_ITEM_PATH + "/" + getTransport(false),
                    "escidoc_oai_item0.xml", "video", null, "video"));
        }
        for (int i = 0; i < numItemsNoOai; i++) {
            itemIdsNoOai[i] =
                getObjid(create(TEMPLATE_OAIPROVIDERTEST_ITEM_PATH + "/" + getTransport(false),
                    "escidoc_oai_item0.xml", "nooai", "nooai", "nooai"));
        }

        //create containers in status released
        for (int i = 0; i < numContainersMusicType; i++) {
            containerIdsMusic[i] =
                getObjid(create(TEMPLATE_OAIPROVIDERTEST_CONTAINER_PATH + "/" + getTransport(false),
                    "escidoc_oai_container0.xml", "music", null, "music"));
        }
        for (int i = 0; i < numContainersVideoType; i++) {
            containerIdsVideo[i] =
                getObjid(create(TEMPLATE_OAIPROVIDERTEST_CONTAINER_PATH + "/" + getTransport(false),
                    "escidoc_oai_container0.xml", "video", null, "video"));
        }
        for (int i = 0; i < numContainersNoOai; i++) {
            containerIdsNoOai[i] =
                getObjid(create(TEMPLATE_OAIPROVIDERTEST_CONTAINER_PATH + "/" + getTransport(false),
                    "escidoc_oai_container0.xml", "nooai", "nooai", "nooai"));
        }
        Thread.sleep(waitForPollMillies);
    }

    /**
     * deprepare system for the tests.
     *
     * @throws Exception If anything fails.
     */
    private void deprepare() throws Exception {
        //withdraw items
        for (int i = 0; i < itemIdsMusic.length; i++) {
            withdrawItem(itemIdsMusic[i]);
        }
        for (int i = 0; i < itemIdsVideo.length; i++) {
            withdrawItem(itemIdsVideo[i]);
        }
        for (int i = 0; i < itemIdsNoOai.length; i++) {
            withdrawItem(itemIdsNoOai[i]);
        }

        //withdraw containers
        for (int i = 0; i < containerIdsMusic.length; i++) {
            withdrawContainer(containerIdsMusic[i]);
        }
        for (int i = 0; i < containerIdsVideo.length; i++) {
            withdrawContainer(containerIdsVideo[i]);
        }
        for (int i = 0; i < containerIdsNoOai.length; i++) {
            withdrawContainer(containerIdsNoOai[i]);
        }
        Thread.sleep(waitForPollMillies);
        String xml = oaiProviderClient.retrieve("verb=ListRecords&metadataPrefix=oai_dc&from=" + from);
        assertNodeCount(xml, "/OAI-PMH/ListRecords/record", 8);
        assertNodeCount(xml, "/OAI-PMH/ListRecords/record/header[@status='deleted']", 8);
        assertNodeCount(xml, "/OAI-PMH/ListRecords/record/metadata/dc[title='item1' and type='music']", 0);
        assertNodeCount(xml, "/OAI-PMH/ListRecords/record/metadata/dc[title='item1' and type='video']", 0);
        assertNodeCount(xml, "/OAI-PMH/ListRecords/record/metadata/dc[title='container1' and type='music']", 0);
        assertNodeCount(xml, "/OAI-PMH/ListRecords/record/metadata/dc[title='container1' and type='video']", 0);
    }

    /**
     * Test retrieving oaiDc Data.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveOaiDc() throws Exception {
        String xml = oaiProviderClient.retrieve("verb=ListRecords&metadataPrefix=oai_dc&from=" + from);
        assertNodeCount(xml, "/OAI-PMH/ListRecords/record", 8);
        assertNodeCount(xml, "/OAI-PMH/ListRecords/record/metadata/dc[title='item1' and type='music']", 2);
        assertNodeCount(xml, "/OAI-PMH/ListRecords/record/metadata/dc[title='item1' and type='video']", 2);
        assertNodeCount(xml, "/OAI-PMH/ListRecords/record/metadata/dc[title='container1' and type='music']", 2);
        assertNodeCount(xml, "/OAI-PMH/ListRecords/record/metadata/dc[title='container1' and type='video']", 2);
    }

}
