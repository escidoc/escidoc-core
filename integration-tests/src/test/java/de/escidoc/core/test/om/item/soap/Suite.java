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
package de.escidoc.core.test.om.item.soap;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The item test suite (Soap).
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
        testSuite.addTestSuite(ItemContentRelationsSoapTest.class);
        testSuite.addTestSuite(ItemCreateSoapTest.class);
        testSuite.addTestSuite(ItemDeleteSoapTest.class);
        testSuite.addTestSuite(ItemFilterSoapTest.class);
        testSuite.addTestSuite(ItemLifecycleSoapTest.class);
        testSuite.addTestSuite(ItemLockSoapTest.class);
        testSuite.addTestSuite(ItemPIDAssignmentSoapTest.class);
        testSuite.addTestSuite(ItemSoapTest.class);
        testSuite.addTestSuite(ItemRetrieveComponentPropertiesSoapTest.class);
        testSuite.addTestSuite(ItemRetrievePropertiesSoapTest.class);
        testSuite.addTestSuite(ItemRetrieveSoapTest.class);
        testSuite.addTestSuite(ItemUpdateSoapTest.class);
        testSuite.addTestSuite(ItemVersioningSoapTest.class);
        testSuite.addTestSuite(ItemComponentExternalContentSoapTest.class);
        testSuite.addTestSuite(ComponentChecksumSoapTest.class);
        testSuite.addTestSuite(ContentStreamsSoapTest.class);
        testSuite.addTestSuite(ItemExamplesSoapTest.class);
        testSuite.addTestSuite(ItemMetadataSoapTest.class);
        testSuite.addTestSuite(SurrogateItemSoapTest.class);
        testSuite.addTestSuite(ItemPerformanceSoapTest.class);
        testSuite.addTestSuite(ItemParentsSoapTest.class);

        return testSuite;
    }

}
