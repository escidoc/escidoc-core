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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test.cmm.contentmodel;

import de.escidoc.core.test.EscidocAbstractTest;
import org.apache.xpath.XPathAPI;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.junit.Assert.assertEquals;

/**
 * Test Content Model versioning behavior.
 *
 * @author Steffen Wagner
 */
public class ContentModelVersioningIT extends ContentModelTestBase {

    /**
     * Test timestamps of Content Model.
     * <p/>
     * Related to issue INFR-707
     *
     * @throws Exception If behavior or timestamps is not as expected.
     */
    @Test
    public void testContentModelTimestamps01() throws Exception {

        String contentModelXml = getExampleTemplate("content-model-minimal-for-create.xml");
        String cmV1E1 = create(contentModelXml);

        Document cmDocV1E1 = getDocument(cmV1E1);
        String objid = getObjidValue(cmV1E1);

        Document wovDocV1E1 = EscidocAbstractTest.getDocument(retrieveVersionHistory(objid));

        // check timestamps consistency ----------------------------------
        // /content-model/@last-modification-date ==
        // /content-model/properties/creation-date
        assertEquals("Timestamp in root attribute of Content Model [" + objid + "] differs from creation-date"
            + " (create was the one and only event)", XPathAPI.selectSingleNode(cmDocV1E1,
            "/content-model/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(cmDocV1E1,
            "/content-model/properties/creation-date").getTextContent());

        // /content-model/@last-modification-date ==
        // /content-model/properties/version/date
        assertEquals("Timestamp in root attribute differs from version-date"
            + " (this Content Model has only one version)", XPathAPI.selectSingleNode(cmDocV1E1,
            "/content-model/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(cmDocV1E1,
            "/content-model/properties/version/date").getTextContent());

        // /content-model/@last-modification-date ==
        // /content-model/latest-version/date
        assertEquals("Timestamp in root attribute differs from creation-date", XPathAPI.selectSingleNode(cmDocV1E1,
            "/content-model/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(cmDocV1E1,
            "/content-model/properties/latest-version/date").getTextContent());

        // /content-model/@last-modification-date ==
        // /version-history/@last-modification-date
        assertEquals("Timestamp in root attribute differs from creation-date", XPathAPI.selectSingleNode(cmDocV1E1,
            "/content-model/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(wovDocV1E1,
            "/version-history/@last-modification-date").getTextContent());

        // /content-model/@last-modification-date ==
        // /version-history/version[version-number='1']/timestamp
        assertEquals("Timestamp in root attribute differs from creation-date", XPathAPI.selectSingleNode(cmDocV1E1,
            "/content-model/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(wovDocV1E1,
            "/version-history/version[version-number='1']/timestamp").getTextContent());

        // /content-model/@last-modification-date ==
        // /version-history/version[<objid>:1]/events/event/eventDateTime
        assertEquals("Timestamp in root attribute differs from creation-date", XPathAPI.selectSingleNode(cmDocV1E1,
            "/content-model/@last-modification-date").getTextContent(), XPathAPI.selectSingleNode(wovDocV1E1,
            "/version-history/version[version-number='1']" + "/events/event[last()]/eventDateTime").getTextContent());

    }

    /**
     * Test timestamps of Content Model.
     * <p/>
     * Related to issue INFR-820
     * <p/>
     * Content Model changed in following way: <ul> <li>create</li> <li>update</li> <li>update</li> </ul> The
     * datastructure is check only after update to version 2 to keep this test clearly arranged. There is an other test
     * which checks the data structure for a lifecycle where release of version 1 is the latest event.
     *
     * @throws Exception If behavior or timestamps is not as expected.
     */
    @Test
    public void testContentModelTimestamps03() throws Exception {

        // version 1
        String contentModelXml = getExampleTemplate("content-model-minimal-for-create.xml");
        String cmV1E1 = create(contentModelXml);

        Document cmDocV1E1 = getDocument(cmV1E1);
        String objid = getObjidValue(cmV1E1);

        Node tmpl = substitute(cmDocV1E1, "/content-model/properties/name", "Update Test CM");

        // version 2
        String cmXmlV2E1 = update(objid, toString(tmpl, false));
        Document cmDocV2E1 = EscidocAbstractTest.getDocument(cmXmlV2E1);

        // FIXME the test is uncomplete because update() failed.
        /*
         * check data structure
         */
        Document wovDocV2E1 = EscidocAbstractTest.getDocument(retrieveVersionHistory(objid));

        // check timestamp of version 1
        assertEquals("timestamp of version 1 of Content Model [" + objid
            + "] differs from timestamp of latest event of version 1 " + "in version-history", XPathAPI
            .selectSingleNode(wovDocV2E1, "/version-history/version[version-number='1']/timestamp").getTextContent(),
            XPathAPI.selectSingleNode(wovDocV2E1,
                "/version-history/version[version-number='1']" + "/events/event[1]/eventDateTime").getTextContent());
    }

}
