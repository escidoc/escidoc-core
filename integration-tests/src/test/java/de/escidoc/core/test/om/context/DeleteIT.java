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
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContextNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test Context delete methods.
 */
public class DeleteIT extends ContextTestBase {

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
     * Test successfully deleting context in status created.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = ContextNotFoundException.class)
    public void testOmDc1() throws Exception {
        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        final String objid = getObjidValue(getDocument(created));

        delete(objid);
        retrieve(objid);
    }

    /**
     * Test deleting a context with non existing context id.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = ContextNotFoundException.class)
    public void testOmDc2() throws Exception {

        delete("escidoc:UnknownContext");
    }

    /**
     * Test deleting a context with missing context id.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = MissingMethodParameterException.class)
    public void testOmDc3() throws Exception {

        delete(null);
    }

    /**
     * Test deleting a context with status opened.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = InvalidStatusException.class)
    public void testOmDc4() throws Exception {
        Document context = EscidocAbstractTest.getTemplateAsDocument(this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);
        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);
        String lastModification = getLastModificationDateValue(createdDoc);
        open(id, getTaskParam(lastModification));
        delete(id);
    }
}
