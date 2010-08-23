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
package de.escidoc.core.test.om.container.soap;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The container test suite (SOAP).
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
        testSuite.addTest(new JUnit4TestAdapter(ContainerRetrieveSoapTest.class));

        testSuite.addTest(new JUnit4TestAdapter(ContainerContentRelationsSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerDeleteSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerLockSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerPidAssignmentSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerReleaseSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerCreateSoapTest.class));

        testSuite.addTest(new JUnit4TestAdapter(ContainerReviseSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerSubmitSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerUpdateSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerVersioningSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerWithdrawSoapTest.class));

        testSuite.addTest(new JUnit4TestAdapter(ContainerParentsSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerReferenceSoapTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerExamplesSoapTest.class));

        return testSuite;
    }

}
