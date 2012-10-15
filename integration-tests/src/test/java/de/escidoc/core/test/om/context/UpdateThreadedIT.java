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

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import de.escidoc.core.test.EscidocAbstractTest;

/**
 * Test the context resource threaded.
 *
 * @author Michael Hoppe
 */
public class UpdateThreadedIT extends ContextTestBase {

    private String path = TEMPLATE_CONTEXT_PATH;

    private String contextId = null;

    private String contextId1 = null;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        this.path += "/rest";
        if (contextId == null || contextId1 == null) {
            Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
            substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
            String template = toString(context, false);
            String contextXml = create(template);
            assertXmlValidContext(contextXml);
            assertCreatedContext(contextXml, template, startTimestamp);
            Document created = EscidocAbstractTest.getDocument(contextXml);
            contextId = getObjidValue(created);

            Document context1 = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
            substitute(context1, "/context/properties/name", getUniqueName("PubMan Context "));
            String template1 = toString(context1, false);
            String contextXml1 = create(template1);
            assertXmlValidContext(contextXml1);
            assertCreatedContext(contextXml1, template1, startTimestamp);
            Document created1 = EscidocAbstractTest.getDocument(contextXml1);
            contextId1 = getObjidValue(created1);
        }
    }

    /**
     * Successfully test retrieving a context threaded.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testContextUpdateThreaded() throws Exception {
        HashMap<String, String> parameters = new HashMap<String, String>();
        HashMap<String, String> parameters1 = new HashMap<String, String>();
        parameters.put("action", "retrieve");
        parameters.put("id", contextId);
        parameters.put("searchString", "Descriptor for Something");

        parameters1.put("action", "update");
        parameters1.put("id", contextId1);
        parameters1.put("searchString", "Descriptor for Something");

        Thread[] ts = new Thread[2];

        ContextThreadRunnable runnable = new ContextThreadRunnable(parameters);
        ts[0] = new Thread(runnable);
        ts[0].start();

        ContextThreadRunnable runnable1 = new ContextThreadRunnable(parameters1);
        ts[1] = new Thread(runnable1);
        ts[1].start();

        try {
            ts[0].join(100000);
            if (runnable.message != null) {
                throw new Exception(runnable.message);
            }
            if (runnable1.message != null) {
                throw new Exception(runnable1.message);
            }
        }
        finally {
            ts[0].interrupt();
            ts[1].interrupt();
        }
    }
}
