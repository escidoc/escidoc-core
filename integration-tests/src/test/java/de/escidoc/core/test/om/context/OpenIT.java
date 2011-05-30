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
package de.escidoc.core.test.om.context;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class OpenIT extends ContextTestBase {

    private String path = TEMPLATE_CONTEXT_PATH;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        this.path += "/rest";
    }

    /**
     * Successfully test opening a context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmOc1() throws Exception {

        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);
        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);
        String lastModified = getLastModificationDateValue(createdDoc);
        open(id, getTaskParam(lastModified));
        String opened = retrieve(id);
        Document openedDoc = EscidocAbstractTest.getDocument(opened);

        assertXmlValidContext(created);
        assertXmlEquals("Context opening error: Wrong status!", opened, "/context/properties/public-status",
            CONTEXT_STATUS_OPENED);
        assertNotEquals("Comment not changed", selectSingleNode(createdDoc,
            "/context/properties/public-status-comment/text()").getNodeValue(), selectSingleNode(openedDoc,
            "/context/properties/public-status-comment/text()").getNodeValue());
        assertTimestampIsEqualOrAfter("Context opening error: last-modification-date has wrong value!",
            getLastModificationDateValue(EscidocAbstractTest.getDocument(opened)), lastModified);
        assertCreatedContext(opened, created, startTimestamp);
        assertCreatedBy("created-by not as expected!", createdDoc, openedDoc);
        assertModifiedBy("modified-by not as expected!", createdDoc, openedDoc);
    }

    /**
     * Test opening a context with non existing context id.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = ContextNotFoundException.class)
    public void testOmOc2() throws Exception {
        open("escidoc:UnknownContext", getTaskParam(null));
    }

    /**
     * Test opening a context with an existing context id but a wrong last-modification-date timestamp.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = OptimisticLockingException.class)
    public void testOmOc3a() throws Exception {
        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);
        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);
        // String lastModified = getLastModificationDateValue(createdDoc);
        open(id, getTaskParam(null));
    }

    /**
     * Test opening a context with an existing context id but an incorrect last-modification-date timestamp.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlCorruptedException.class)
    public void testOmOc3b() throws Exception {
        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);
        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);
        // String lastModified = getLastModificationDateValue(createdDoc);
        open(id, getTaskParam("incorrect timestamp"));
    }

    /**
     * Test opening a context with an existing context id and an missing last-modification-date timestamp attribute.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlCorruptedException.class)
    public void testOmOc3c() throws Exception {
        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);
        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);
        // String lastModified = getLastModificationDateValue(createdDoc);
        String param =
            toString(deleteAttribute(EscidocAbstractTest.getDocument(getTaskParam("incorrect timestamp")), "/param",
                "last-modification-date"), false);
        open(id, param);
    }

    /**
     * Test opening a context with missing context id but a correct last-modification-date timestamp.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = MissingMethodParameterException.class)
    public void testOmOc4a() throws Exception {
        open(null, getTaskParam(null));
    }

    /**
     * Test opening a context with existing context id but missing last-modification-date timestamp.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = MissingMethodParameterException.class)
    public void testOmOc4b() throws Exception {
        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);
        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);
        // String lastModified = getLastModificationDateValue(createdDoc);
        open(id, null);
    }

    /**
     * Test opening a context with existing context id and missing last-modification-date timestamp.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = MissingMethodParameterException.class)
    public void testOmOc4c() throws Exception {
        open(null, null);
    }

    /**
     * Try to open an already opened context.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = InvalidStatusException.class)
    public void testOmOc5() throws Exception {

        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);
        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);
        String lastModified = getLastModificationDateValue(createdDoc);
        open(id, getTaskParam(lastModified));

        open(id, getTaskParam(getLastModificationDateValue(EscidocAbstractTest.getDocument(retrieve(id)))));
    }

    /**
     * Test the last modification date timestamp of the open/close method.
     *
     * @throws Exception Thrown if anything failed.
     */
    @Test
    public void testReturnValue01() throws Exception {

        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);

        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String contextId = getObjidValue(createdDoc);

        String lmd = getLastModificationDateValue(createdDoc);
        String resultXml = open(contextId, getTaskParam(lmd));
        assertXmlValidResult(resultXml);
        String lmdOpen = getLastModificationDateValue(getDocument(resultXml));

        resultXml = close(contextId, getTaskParam(lmdOpen));
        assertXmlValidResult(resultXml);
        String lmdClose = getLastModificationDateValue(getDocument(resultXml));

        assertTimestampIsEqualOrAfter("Wrong timestamp", lmdClose, lmdOpen);
    }

}
