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
package de.escidoc.core.test.om.ingest;

import org.junit.Test;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidResourceException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.test.Constants;

/**
 * Test ingesting resource via ingest interface.<br>
 * By default, the tests are executed using a depositor user.
 * 
 * @author Steffen Wagner, KST
 */
public class IngestIT extends IngestTestBase {

    /**
     * Test what happens if an invalid but well formed XML fragment gets ingested. An InvalidResourceException has to be
     * thrown as a result.
     * 
     * @throws Exception
     *             the Exception, in this case InvalidResourceException
     */
    @Test(expected = InvalidResourceException.class)
    public void testIngestXmlNotValid() throws Exception {
        String toBeCreatedXml = Constants.XML_HEADER + "<root><a/></root>";
        ingest(toBeCreatedXml);
    }

    /**
     * Tests what happens if a not well formed xml fragment gets ingested. First the exception type gets checked, then
     * the content of the exception message gets checked. If either fail the test fails.
     */
    @Test(expected = InvalidResourceException.class)
    public void testIngestXmlNotWellFormed() throws Exception {

        String toBeCreatedXml = Constants.XML_HEADER + "<roo><a/></root>";

        ingest(toBeCreatedXml);
    }

    /**
     * Test unexpected parser exception instead of XmlCorruptedException during create (see issue INFR-911).
     * 
     * @throws Exception
     *             Thrown if behavior is not as expected.
     */
    @Test(expected = XmlCorruptedException.class)
    public void testInvalidXml() throws Exception {
        ingest("laber-rababer");
    }

}
