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
package de.escidoc.core.test.om.ingest;

import static org.junit.Assert.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.sb.SearchTestBase;

/**
 * Test ingesting Item via ingest interface.<br>
 * By default, the tests are executed using a depositor user.
 * 
 * @author Steffen Wagner, KST
 */
public class IngestItemIT extends IngestTestBase {

    private static final Pattern OBJECT_PATTERN =
        Pattern.compile("<objid resourceType=\"([^\"][^\"]*)\">(escidoc:\\d+)</objid>", Pattern.MULTILINE);

    /**
     * Test if a valid item gets ingested. The return value must be a xml fragment containing the object id. The return
     * value gets first parsed to check if it is well formed xml. Then the xml gets matched against a pattern which
     * looks for an object id.
     * 
     * @throws Exception
     *             the Exception gets thrown in the following cases: <ul <li>The ingest fails due to internal reasons
     *             (Fedora, eSciDoc) <li>The return value is not well formed <li>The return value does not contain a
     *             vaild object id. </ul>
     */
    @Test
    public void testIngestItemValid() throws Exception {

        String toBeCreatedXml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_ITEM_PATH,
                "rest/escidoc_item_198_for_create_2_Component_Md-Records.xml");

        String createdXml = ingest(toBeCreatedXml);

        // Document is well formed and valid
        assertXmlValidResult(createdXml);

        Matcher matcher = OBJECT_PATTERN.matcher(createdXml);

        if (matcher.find()) {
            String resourceType = matcher.group(1);
            String objectId = matcher.group(2);

            //check if object is indexed
            assertIndexed(SearchTestBase.ITEM_CONTAINER_ADMIN_INDEX_NAME, objectId);

            // Have we just ingested an item ?
            assert (resourceType.equals("ITEM"));

            // We can't assume anything about the object's id except not being
            // null, can we ?
            assert (objectId != null);
        }
        else {
            fail("no match for item found, return value of ingest could not " + "be matched successfully.");
        }
    }

    /**
     * Test if a valid Item in public-status 'released' gets ingested. The return value must be a xml fragment
     * containing the object id. The return value gets first parsed to check if it is well formed xml. Then the xml gets
     * matched against a pattern which looks for an object id.
     * 
     * FIXME it depends on the pid behaviour configuration if the tests fails or not. One can ingest an item in status released, but sometimes a pid is required for this and sometimes not.
     *  
     * @throws Exception
     *             the Exception gets thrown in the following cases:
     *             <ul>
     *             <li>The ingest fails due to internal reasons (Fedora, eSciDoc)</li>
     *             <li>The return value is not well formed</li>
     *             <li>The return value does not contain a vaild object id.</li>
     *             <li>No exception is thrown because object PID is missing</li>
     *             </ul>
     */
    @Test(expected = InvalidStatusException.class)
    public void testIngestReleasedItem01() throws Exception {

        Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH, "rest/item_without_component.xml");

        Element publicStatus =
            createElementNode(toBeCreatedDocument, "http://escidoc.de/core/01/properties/", "prop", "public-status",
                "released");
        Node parent = selectSingleNode(toBeCreatedDocument, "/item/properties");
        Node refNode = selectSingleNode(toBeCreatedDocument, "/item/properties/context");
        parent.insertBefore(publicStatus, refNode);

        String toBeCreatedXml = toString(toBeCreatedDocument, false);

        ingest(toBeCreatedXml);
    }

    /**
     * Test if a valid Item in public-status 'released' gets ingested. The return value must be a xml fragment
     * containing the object id. The return value gets first parsed to check if it is well formed xml. Then the xml gets
     * matched against a pattern which looks for an object id.
     * 
     * @throws Exception
     *             the Exception gets thrown in the following cases:
     *             <ul>
     *             <li>The ingest fails due to internal reasons (Fedora, eSciDoc)</li>
     *             <li>The return value is not well formed</li>
     *             <li>The return value does not contain a vaild object id.</li>
     *             <li>The public-status 'released' is not copied</li>
     *             <li>The object pid is not copied.</li>
     *             </ul>
     */
    @Test
    public void testIngestReleasedItem02() throws Exception {

        Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH, "rest/item_without_component.xml");

        Element publicStatus =
            createElementNode(toBeCreatedDocument, "http://escidoc.de/core/01/properties/", "prop", "public-status",
                "released");
        Node parent = selectSingleNode(toBeCreatedDocument, "/item/properties");
        Node refNode = selectSingleNode(toBeCreatedDocument, "/item/properties/context");
        parent.insertBefore(publicStatus, refNode);

        // if pid is required for release than add pid
        if (!getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {

            Element objectPid =
                createElementNode(toBeCreatedDocument, "http://escidoc.de/core/01/properties/", "prop", "pid",
                    "hdl:escidoc-dummy-pid");
            // this reference based on the template
            Node refNodePid = selectSingleNode(toBeCreatedDocument, "/item/properties/content-model-specific");
            parent.insertBefore(objectPid, refNodePid);

        }

        String toBeCreatedXml = toString(toBeCreatedDocument, false);
        String createdXml = ingest(toBeCreatedXml);

        // Document is well formed and valid
        assertXmlValidResult(createdXml);

        Matcher matcher = OBJECT_PATTERN.matcher(createdXml);
        String objectId = null;

        if (matcher.find()) {
            String resourceType = matcher.group(1);
            objectId = matcher.group(2);

            //check if object is indexed
            assertIndexed(SearchTestBase.ITEM_CONTAINER_ADMIN_INDEX_NAME, objectId);

            // Have we just ingested an item ?
            assert (resourceType.equals("ITEM"));

            // We can't assume anything about the object's id except not being
            // null, can we ?
            assert (objectId != null);
        }
        else {
            fail("no match for item found, return value of ingest " + "could not be matched successfully.");
        }

        // assert at least public-status and objectPID
        String createdItemXml = handleXmlResult(getItemClient().retrieve(objectId));

        assertXmlExists("Wrong public-status", createdItemXml, "/item/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", createdItemXml, "/item/properties/version/status[text() = 'released']");
        assertXmlNotNull("pid", getDocument(createdItemXml), "/item/properties/pid");
    }
}
