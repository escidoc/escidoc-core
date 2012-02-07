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

import de.escidoc.core.common.exceptions.remote.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidResourceException;
import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.sb.SearchTestBase;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.fail;

/**
 * Test ingesting resource via ingest interface.<br>
 * By default, the tests are executed using a depositor user.
 * 
 * @author Steffen Wagner, KST
 */
public class IngestContentModelIT extends IngestTestBase {

    private static final Pattern OBJECT_PATTERN =
        Pattern.compile("<objid resourceType=\"([^\"][^\"]*)\">(escidoc:\\d+)</objid>", Pattern.MULTILINE);

    /**
     * Test if a valid Content Model gets ingested. The return value must be a XML fragment containing the object id and
     * conforming the the result.xsd schema.
     * 
     * @throws Exception
     *             Throws Exception if test fail.
     */
    @Test
    public void ingestContentModel() throws Exception {
        String cmmTempl = getExampleTemplate("content-model-minimal-for-create.xml");

        String createdXml = ingest(cmmTempl);

        // assert document is well formed and valid
        assertXmlValidResult(createdXml);

        Matcher matcher = OBJECT_PATTERN.matcher(createdXml);

        if (matcher.find()) {
            String resourceType = matcher.group(1);
            String objectId = matcher.group(2);

            //check if object is indexed
            assertIndexed(SearchTestBase.CONTENT_MODEL_ADMIN_INDEX_NAME, objectId);

            // Have we just ingested a content model ?
            assert resourceType.equals("CONTENT_MODEL") : "expected resource type \"CONTENT_MODEL\" but got \""
                + resourceType + "\"";

            // We can't assume anything about the object's id except not being
            // null, can we ?
            assert (objectId != null);
        }
        else {
            fail("no match for content model found, return value " + "of ingest could not be matched successfully.");
        }
    }

    /**
     * Test unexpected parser exception instead of XmlCorruptedException during create (see issue INFR-911).
     * 
     * @throws Exception
     *             Thrown if behavior is not as expected.
     */
    @Test(expected = XmlCorruptedException.class)
    public void testInvalidXml() throws Exception {
        ingest("laber-rababer");
    }

}
