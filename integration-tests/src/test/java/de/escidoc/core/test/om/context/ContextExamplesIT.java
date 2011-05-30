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

import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 * Test creating the example Context objects.
 *
 * @author Steffen Wagner
 */
public class ContextExamplesIT extends ContextTestBase {

    /**
     * Delete all Contexts with same name before test runs to avoid unique name conflicts. Finding contexts with the
     * same name based on filters. If filters fail, then could it be that a Context with same name still exist in
     * repository.
     */
    @Before
    public void preventUniqueNameConflict() throws Exception {

        final Map<String, String[]> filterParams = new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\"" + FILTER_NAME
            + "\"=\"Context%20Example%2001%20(REST)\"" });

        String result = retrieveContexts(filterParams);

        NodeList contexts =
            selectNodeList(EscidocAbstractTest.getDocument(result),
                "/searchRetrieveResponse/records/record/recordData/context/@objid");

    }

    /**
     * Test if the example context for create is still compatible with framework.
     *
     * @throws Exception Thrown if creation of example Context failed.
     */
    @Test
    public void creatingExample01() throws Exception {

        String contextXml = getExampleTemplate("context-minimal-for-create-01.xml");
        String xml = create(contextXml);
        assertXmlValidContext(xml);
        delete(getObjidValue(xml));
    }

}
