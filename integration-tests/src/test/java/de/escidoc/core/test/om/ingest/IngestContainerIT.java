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
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
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
 * Test ingesting Container via ingest interface.<br>
 * By default, the tests are executed using a depositor user.
 * 
 * @author Steffen Wagner, KST
 */
public class IngestContainerIT extends IngestTestBase {

    private static final Pattern OBJECT_PATTERN =
        Pattern.compile("<objid resourceType=\"([^\"][^\"]*)\">(escidoc:\\d+)</objid>", Pattern.MULTILINE);

    /**
     * Test if a valid container gets ingested. The return value must be a xml fragment containing the object id and
     * conforming the the result.xsd schema.
     */
    @Test
    public void ingestContainer() throws Exception {
        Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTAINER_PATH, "rest/create_container.xml");
        String toBeCreatedXml = toString(toBeCreatedDocument, false);
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
            assert (resourceType.equals("CONTAINER"));

            // We can't assume anything about the object's id except not being
            // null, can we ?
            assert (objectId != null);
        }
        else {
            fail("no match for container found, return value of ingest " + "could not be matched successfully.");
        }
    }

    /**
     * Test if a valid Container in public-status 'released' gets ingested. The return value must be a xml fragment
     * containing the object id. The return value gets first parsed to check if it is well formed xml. Then the xml gets
     * matched against a pattern which looks for an object id.
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
    public void ingestReleasedContainer01() throws Exception {

        Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTAINER_PATH, "rest/create_container.xml");

        Element publicStatus =
            createElementNode(toBeCreatedDocument, "http://escidoc.de/core/01/properties/", "prop", "public-status",
                "released");
        Node parent = selectSingleNode(toBeCreatedDocument, "/container/properties");
        Node refNode = selectSingleNode(toBeCreatedDocument, "/container/properties/name");
        parent.insertBefore(publicStatus, refNode);

        // delete object pid node
        parent.removeChild(selectSingleNode(toBeCreatedDocument, "/container/properties/pid"));

        String toBeCreatedXml = toString(toBeCreatedDocument, false);

        ingest(toBeCreatedXml);
    }

    /**
     * Test if a valid Container in public-status 'released' gets ingested. The return value must be a xml fragment
     * containing the object id. The return value gets first parsed to check if it is well formed xml. Then the xml gets
     * matched against a pattern which looks for an object id.
     * 
     * @throws Exception
     *             the Exception gets thrown in the following cases:
     *             <ul>
     *             <li>The ingest fails due to internal reasons (Fedora, eSciDoc)</li>
     *             <li>The return value is not well formed</li>
     *             <li>The return value does not contain a valid object id.</li>
     *             <li>The public-status 'released' is not copied</li>
     *             <li>The object PID is not copied.</li>
     *             </ul>
     */
    @Test
    public void ingestReleasedContainer02() throws Exception {

        Document toBeCreatedDocument =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTAINER_PATH, "rest/create_container.xml");

        Element publicStatus =
            createElementNode(toBeCreatedDocument, "http://escidoc.de/core/01/properties/", "prop", "public-status",
                "released");
        Node parent = selectSingleNode(toBeCreatedDocument, "/container/properties");
        Node refNode = selectSingleNode(toBeCreatedDocument, "/container/properties/name");
        parent.insertBefore(publicStatus, refNode);

        // if pid is required for release than add pid
        if (!getItemClient().getPidConfig("cmm.Item.objectPid.releaseWithoutPid", "false")) {

            // add object PID if it not exists
            if (selectSingleNode(toBeCreatedDocument, "/container/properties/pid") == null) {

                Element objectPid =
                    createElementNode(toBeCreatedDocument, "http://escidoc.de/core/01/properties/", "prop", "pid",
                        "hdl:escidoc-dummy-pid");
                // this reference based on the template
                Node refNodePid = selectSingleNode(toBeCreatedDocument, "/container/properties/content-model-specific");
                parent.insertBefore(objectPid, refNodePid);
            }
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

            // Have we just ingested a container?
            assert resourceType.equals("CONTAINER") : "wrong resource type: " + resourceType;

            // We can't assume anything about the object's id except not being
            // null, can we ?
            assert (objectId != null);
        }
        else {
            fail("no match for item found, return value of ingest could " + "not be matched successfully.");
        }

        // assert at least public-status and objectPID
        String createdContainerXml = handleXmlResult(getContainerClient().retrieve(objectId));

        assertXmlExists("Wrong public-status", createdContainerXml,
            "/container/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", createdContainerXml,
            "/container/properties/version/status[text() = 'released']");
        assertXmlNotNull("pid", getDocument(createdContainerXml), "/container/properties/pid");
    }
}
