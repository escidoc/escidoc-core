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
package de.escidoc.core.test.om.item.rest;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The item test suite (REST).
 * 
 * @author TTE
 * 
 */
public class Suite {

    /**
     * Creates the test suite.
     * 
     * @return Returns the created <code>TestSuite</code> object.
     */
    public static Test suite() {

        TestSuite testSuite = new TestSuite("Item Rest Test Suite");
        testSuite.addTest(new JUnit4TestAdapter(ItemContentRelationsRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemDeleteRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemCreateRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemFilterRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemLifecycleRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemLockRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemPIDAssignmentRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemRetrieveComponentPropertiesRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemRetrievePropertiesRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemRetrieveRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemUpdateRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemVersioningRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemComponentExternalContentRestTest.class));
        // testSuite.addTest(new JUnit4TestAdapter(ItemContentTransformationRestTest.class));
        // testSuite.addTest(new JUnit4TestAdapter(ItemRetrieveContentRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ComponentChecksumRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContentStreamsRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemExamplesRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemMetadataRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(SurrogateItemRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemPerformanceRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemParentsRestTest.class));

        return testSuite;
    }

}
