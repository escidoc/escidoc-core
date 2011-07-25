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
package de.escidoc.core.test.sm;

import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * Test the implementation of the statistic data resource.
 *
 * @author Michael Hoppe
 */
public class StatisticDataIT extends StatisticDataTestBase {

    private ScopeIT scope = null;

    private static String scopeId = null;

    private static int methodCounter = 0;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        scope = new ScopeIT() {
        };
        if (methodCounter == 0) {
            createScope();
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
     * Creates scope for tests.
     */
    private void createScope() {
        try {
            String xml = getTemplateAsFixedScopeString(TEMPLATE_SCOPE_PATH, "escidoc_scope1.xml");
            String result = scope.create(xml);
            scopeId = getPrimKey(result);
        }
        catch (final Exception e) {
            fail("Exception occured " + e.toString());
        }
    }

    /**
     * create statistic data with invalid xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMSD2() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_STAT_DATA_PATH, "escidoc_statistic_data_invalid.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        create(xml);
    }

    /**
     * create statistic data with correct xml.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMSD3() throws Exception {
        String xml = EscidocAbstractTest.getTemplateAsString(TEMPLATE_STAT_DATA_PATH, "escidoc_statistic_data1.xml");
        xml = replaceElementPrimKey(xml, "scope", scopeId.toString());
        try {
            create(xml);
        }
        catch (final Exception e) {
            fail("Exception occured " + e.toString());
        }
    }

}
