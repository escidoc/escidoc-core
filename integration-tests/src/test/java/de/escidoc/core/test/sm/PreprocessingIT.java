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
 * Test the implementation of the Preprocessing resource.
 *
 * @author Michael Hoppe
 */
public class PreprocessingIT extends PreprocessingTestBase {

    private static int methodCounter = 0;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
        methodCounter++;
    }

    /**
     * test preprocessing.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testSMPRE1() throws Exception {
        String xml =
            EscidocAbstractTest.getTemplateAsString(TEMPLATE_PREPROCESSING_INFO_PATH,
                "escidoc_preprocessing_information1.xml");
        try {
            preprocess("escidoc:aggdef2", xml);
        }
        catch (final Exception e) {
            fail("Exception occured " + e.toString());
        }
    }

}
