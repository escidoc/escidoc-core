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

import junit.framework.JUnit4TestAdapter;
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
        testSuite.addTest(new JUnit4TestAdapter(ItemContentRelationsSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemCreateSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemDeleteSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemFilterSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemLifecycleSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemLockSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemPIDAssignmentSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemRetrieveComponentPropertiesSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemRetrievePropertiesSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemRetrieveSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemUpdateSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemVersioningSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemComponentExternalContentSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ComponentChecksumSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContentStreamsSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemExamplesSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemMetadataSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(SurrogateItemSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemPerformanceSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ItemParentsSoapTest.class));

        return testSuite;
    }

}
