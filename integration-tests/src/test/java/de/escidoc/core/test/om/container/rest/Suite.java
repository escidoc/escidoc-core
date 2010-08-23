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
package de.escidoc.core.test.om.container.rest;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The container test suite (REST).
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
        testSuite.addTest(new JUnit4TestAdapter(ContainerRetrieveRestTest.class));

        testSuite.addTest(new JUnit4TestAdapter(ContainerContentRelationsRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerDeleteRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerLockRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerPidAssignmentRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerReleaseRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerCreateRestTest.class));

        testSuite.addTest(new JUnit4TestAdapter(ContainerReviseRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerSubmitRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerUpdateRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerVersioningRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerWithdrawRestTest.class));

        testSuite.addTest(new JUnit4TestAdapter(ContainerParentsRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerReferenceRestTest.class));
        testSuite.addTest(new JUnit4TestAdapter(ContainerExamplesRestTest.class));

        return testSuite;
    }

}
