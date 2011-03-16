/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

import de.escidoc.core.common.business.fedora.Constants;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Dump test.
 * 
 * These test package has to include funtional test only. No ear is ready, no
 * database is created, no JBoss is running! That's why integration tests are to
 * settle at escidoc.ear integrration tests!
 * 
 * @author SWA
 * 
 */
public class DumpTest {

    @Test
    public void testHelloWorld() throws Exception {
        assertEquals("Just a test to see if everything works", "deleted",
            Constants.MIME_TYPE_DELETED);
    }
}