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

public class RetrieveMdRecordsIT extends OrganizationalUnitTestBase {

    /**
     * Test retrieving the properties of an organizational unit. The organizational unit has no external-id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumROd1_1() throws Exception {

        Document xml = getDocument(createSuccessfully("escidoc_ou_create.xml"));
        String id = getObjidValue(xml);
        String organizationDetails = retrieveMdRecords(id);
        assertXmlValidOrganizationalUnit(organizationDetails);
        assertEscidocMdRecord(id, getDocument(organizationDetails), selectSingleNode(xml,
            XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS), startTimestamp);

    }

    //    /**
    //     * Test retrieving the properties of an organizational unit. The
    //     * organizational unit contains an external-id.
    //     *
    //     * @test.name Retrieve Organization Details of Organizational Unit - Success
    //     * @test.id OUM_RP-1-2
    //     * @test.input
    //     *          <ul>
    //     *          <li>Id of existing organizational unit.</li>
    //     *          </ul>
    //     * @test.expected: XML representation of the list of properties of the
    //     *                 organizational unit.
    //     * @test.status Revoked - no more requirements for external id handling at
    //     *              the moment
    //     *
    //     * @throws Exception
    //     *             If anything fails.
    //     */
    // public void testOumROd1_2() throws Exception {
    //
    // Document xml =
    // getDocument(createSuccessfully("escidoc_ou_create_with_external_id.xml"));
    // String id = getObjidValue(xml);
    // String organizationDetails = retrieveOrganizationDetails(id);
    // assertXmlValidOrganizationalUnit(organizationDetails);
    // assertEscidocMdRecord(id, getDocument(organizationDetails),
    // selectSingleNode(xml,
    // XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS), startTimestamp);
    //
    // }

    /**
     * Test declining retrieving organization-details of organizational unit with providing unknown id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumROd2() throws Exception {

        Class ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieveProperties(UNKNOWN_ID);
            failMissingException(ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining retrieving organization-details of organizational unit with providing id of existing resource of
     * another resource type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumROd2_2() throws Exception {

        Class ec = OrganizationalUnitNotFoundException.class;
        try {
            retrieveProperties(CONTEXT_ID);
            failMissingException(ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining retrieving organization-details of organizational unit without providing id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumROd3_1() throws Exception {

        Class ec = MissingMethodParameterException.class;
        try {
            retrieveProperties(null);
            failMissingException(ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

    /**
     * Test declining retrieving of organization-details of organizational unit without providing id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumROd3_2() throws Exception {

        Class ec = MissingMethodParameterException.class;
        try {
            retrieveProperties("");
            failMissingException(ec);
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
    }

}
