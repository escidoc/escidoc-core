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
package de.escidoc.core.test.oum.organizationalunit.rest;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The organizational unit test suite (REST).
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
        testSuite.addTestSuite(CloseRestTest.class);
        testSuite.addTestSuite(CreateRestTest.class);
        testSuite.addTestSuite(DeleteRestTest.class);
        testSuite.addTestSuite(OpenRestTest.class);
        testSuite.addTestSuite(PersistentOUsRestTest.class);
        testSuite.addTestSuite(RetrieveChildObjectsRestTest.class);
        testSuite.addTestSuite(RetrieveMdRecordsRestTest.class);
        testSuite.addTestSuite(RetrieveParentsRestTest.class);
        testSuite.addTestSuite(RetrieveParentObjectsRestTest.class);
        testSuite.addTestSuite(RetrievePathListRestTest.class);
        testSuite.addTestSuite(RetrieveResourcesRestTest.class);
        testSuite.addTestSuite(RetrieveRestTest.class);
        testSuite.addTestSuite(RetrievePropertiesRestTest.class);
        testSuite.addTestSuite(UpdateRestTest.class);
        testSuite.addTestSuite(UpdateMdRecordsRestTest.class);
        testSuite.addTestSuite(UpdateParentsRestTest.class);
        testSuite.addTestSuite(ExamplesRestTest.class);
        testSuite.addTestSuite(PredecessorRestTest.class);

        return testSuite;
    }

}
