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
package de.escidoc.core.test.adm;

import de.escidoc.core.test.common.client.servlet.Constants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Test suite for the reindex method of the admin tool.
 *
 * @author Michael Hoppe
 */
@RunWith(value = Parameterized.class)
public class ReindexingErrorTest extends AdminToolTestBase {

    /**
     * The constructor.
     *
     * @param transport The transport identifier.
     * @throws Exception If anything fails.
     */
    public ReindexingErrorTest(final int transport) throws Exception {
        super(transport);
    }

    /**
     * Test reindexing with error.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testReindexingError() throws Exception {
        if (getTransport() == Constants.TRANSPORT_REST) {
            reindex(false, "errorTest");
        }
    }

}
