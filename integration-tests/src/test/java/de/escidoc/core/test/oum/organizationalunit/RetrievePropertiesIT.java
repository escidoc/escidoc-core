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
package de.escidoc.core.test.oum.organizationalunit;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.OrganizationalUnitNotFoundException;
import org.junit.Test;
import org.w3c.dom.Document;

public class RetrievePropertiesIT extends OrganizationalUnitTestBase {

    /**
     * Test retrieving the properties of an organizational unit.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumRP1() throws Exception {

        Document xml = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        String id = getObjidValue(xml);
        assertPropertiesElement("", xml, XPATH_ORGANIZATIONAL_UNIT_PROPERTIES, startTimestamp);
        String properties = retrieveProperties(id);
        assertXmlValidOrganizationalUnit(properties);
        assertPropertiesElement("", getDocument(properties), "/" + NAME_PROPERTIES, startTimestamp);
    }

    /**
     * Test declining retrieving properties of organizational unit with providing unknown id.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = OrganizationalUnitNotFoundException.class)
    public void testOumRP2() throws Exception {

        retrieveProperties(UNKNOWN_ID);
    }

    /**
     * Test declining retrieving properties of organizational unit with providing id of existing resource of another
     * resource type.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = OrganizationalUnitNotFoundException.class)
    public void testOumRP2_2() throws Exception {

        retrieveProperties(CONTEXT_ID);
    }

    /**
     * Test declining retrieving properties of organizational unit without providing id.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = MissingMethodParameterException.class)
    public void testOumRP3_1() throws Exception {

        retrieveProperties(null);
    }

    /**
     * Test declining retrieving properties of organizational unit without providing id.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = MissingMethodParameterException.class)
    public void testOumRP3_2() throws Exception {

        retrieveProperties("");
    }

}
