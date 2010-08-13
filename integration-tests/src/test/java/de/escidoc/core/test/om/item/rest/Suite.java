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

        TestSuite testSuite = new TestSuite(Suite.class.getName());
        testSuite.addTestSuite(ItemContentRelationsRestTest.class);
        testSuite.addTestSuite(ItemDeleteRestTest.class);
        testSuite.addTestSuite(ItemCreateRestTest.class);
        testSuite.addTestSuite(ItemFilterRestTest.class);
        testSuite.addTestSuite(ItemLifecycleRestTest.class);
        testSuite.addTestSuite(ItemLockRestTest.class);
        testSuite.addTestSuite(ItemPIDAssignmentRestTest.class);
        testSuite.addTestSuite(ItemRestTest.class);
        testSuite.addTestSuite(ItemRetrieveComponentPropertiesRestTest.class);
        testSuite.addTestSuite(ItemRetrievePropertiesRestTest.class);
        testSuite.addTestSuite(ItemRetrieveRestTest.class);
        testSuite.addTestSuite(ItemUpdateRestTest.class);
        testSuite.addTestSuite(ItemVersioningRestTest.class);
        testSuite.addTestSuite(ItemComponentExternalContentRestTest.class);
        // testSuite.addTestSuite(ItemContentTransformationRestTest.class);
        // testSuite.addTestSuite(ItemRetrieveContentRestTest.class);
        testSuite.addTestSuite(ComponentChecksumRestTest.class);
        testSuite.addTestSuite(ContentStreamsRestTest.class);
        testSuite.addTestSuite(ItemExamplesRestTest.class);
        testSuite.addTestSuite(ItemMetadataRestTest.class);
        testSuite.addTestSuite(SurrogateItemRestTest.class);
        testSuite.addTestSuite(ItemPerformanceRestTest.class);
        testSuite.addTestSuite(ItemParentsRestTest.class);

        return testSuite;
    }

}
