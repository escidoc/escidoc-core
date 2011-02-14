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
package de.escidoc.core.test.adm.rest;

import de.escidoc.core.test.adm.AdminToolTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import org.junit.Test;

/**
 * Test for the Admin tool with REST transport.
 * 
 * @author SCHE
 * 
 */
public class LoadExamplesRestTest extends AdminToolTestBase {

    /**
     * Constructor.
     * 
     * @throws Exception
     *             If anything fails.
     */
    public LoadExamplesRestTest() throws Exception {
        super(Constants.TRANSPORT_REST);
    }

    /**
     * Load the examples objects into the repository.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(timeout = 30000)
    public void testLoadExamples() throws Exception {
        loadExamples("common");
    }
}
