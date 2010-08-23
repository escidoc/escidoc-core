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
package de.escidoc.core.test.oum.organizationalunit.soap;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The organizatonal unit test suite (Soap).
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
        testSuite.addTest(new JUnit4TestAdapter(CloseSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(CreateSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(DeleteSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(OpenSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(PersistentOUsSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(RetrieveChildObjectsSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(RetrieveMdRecordsSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(RetrieveParentsSoapTest.class));
        // testSuite.addTest(new JUnit4TestAdapter(RetrieveOrganizationalUnitsRefsSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(RetrieveOrganizationalUnitsSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(RetrieveParentObjectsSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(RetrievePathListSoap.class));
        testSuite.addTest(new JUnit4TestAdapter(RetrieveSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(RetrievePropertiesSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(UpdateSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(UpdateMdRecordsSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(UpdateParentsSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ExamplesSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(PredecessorSoapTest.class));

        return testSuite;
    }

}
