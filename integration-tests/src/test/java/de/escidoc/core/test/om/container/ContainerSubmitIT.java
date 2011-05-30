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
package de.escidoc.core.test.om.container;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the Container resource.
 *
 * @author Michael Schneider
 */
public class ContainerSubmitIT extends ContainerTestBase {

    private String theContainerXml;

    private String theContainerId;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        String xmlData = getContainerTemplate("create_container_WithoutMembers_v1.1.xml");

        this.theContainerXml = create(xmlData);
        this.theContainerId = getObjidValue(this.theContainerXml);
    }

    /**
     * Test successful submitting a container in state "pending".
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_SC_1() throws Exception {

        String paramXml = getTheLastModificationParam(false, theContainerId);
        final Document paramDocument = EscidocAbstractTest.getDocument(paramXml);
        final String pendingLastModificationDate = getLastModificationDateValue(paramDocument);

        try {
            submit(theContainerId, paramXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Submitting the pending item failed. ", e);
        }

        String submittedXml = null;
        try {
            submittedXml = retrieve(theContainerId);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving the revised, submitted item failed. ", e);
        }
        final Document submittedDocument = EscidocAbstractTest.getDocument(submittedXml);
        assertDateBeforeAfter(pendingLastModificationDate, getLastModificationDateValue(submittedDocument));
        assertXmlEquals("Unexpected status. ", submittedDocument, XPATH_CONTAINER_STATUS, STATE_SUBMITTED);
        assertXmlEquals("Unexpected current version status", submittedDocument, XPATH_CONTAINER_CURRENT_VERSION_STATUS,
            STATE_SUBMITTED);
    }

    /**
     * Test successful submitting a container in state "in-revision".
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOM_SC_1_2() throws Exception {

        String paramXml = getTheLastModificationParam(false, theContainerId);
        submit(theContainerId, paramXml);
        paramXml = getTheLastModificationParam(false, theContainerId);
        revise(theContainerId, paramXml);
        paramXml = getTheLastModificationParam(false, theContainerId);
        final Document paramDocument = EscidocAbstractTest.getDocument(paramXml);
        final String revisedLastModificationDate = getLastModificationDateValue(paramDocument);

        try {
            submit(theContainerId, paramXml);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Submitting the revised item failed. ", e);
        }

        String submittedXml = null;
        try {
            submittedXml = retrieve(theContainerId);
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Retrieving the revised, submitted item failed. ", e);
        }
        final Document submittedDocument = EscidocAbstractTest.getDocument(submittedXml);
        assertDateBeforeAfter(revisedLastModificationDate, getLastModificationDateValue(submittedDocument));
        assertXmlEquals("Unexpected status. ", submittedDocument, XPATH_CONTAINER_STATUS, STATE_SUBMITTED);
        assertXmlEquals("Unexpected current version status", submittedDocument, XPATH_CONTAINER_CURRENT_VERSION_STATUS,
            STATE_SUBMITTED);
    }

    /**
     * Test declining submit of container with non existing id.
     */
    @Test
    public void testOM_SC_2_1() throws Exception {

        String param = getTheLastModificationParam(false, theContainerId);

        try {
            submit("bla", param);
            fail("No exception occured on submit with non existing id.");
        }
        catch (final Exception e) {
            Class<?> ec = ContainerNotFoundException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test declining submitting of container with wrong time stamp.
     */
    @Test
    public void test_OM_SC_2_2() throws Exception {

        String param = getTheLastModificationParam(false, theContainerId);
        param =
            param.replaceFirst("<param last-modification-date=\"([0-9TZ:\\.-])+\"",
                "<param last-modification-date=\"2005-01-30T11:36:42.015Z\"");

        try {
            submit(theContainerId, param);
            fail("No exception occured on submit with wrong time stamp.");
        }
        catch (final Exception e) {
            Class<?> ec = OptimisticLockingException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test declining submitting of container with missing id
     */
    @Test
    public void testOM_SC_3_1() throws Exception {

        String param = getTheLastModificationParam(false, theContainerId);

        try {
            submit(null, param);
            fail("No exception occured on submit with missing id.");
        }
        catch (final Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test declining submitting of container with missing time stamp
     */
    @Test
    public void testOM_SC_3_2() throws Exception {

        try {
            submit(theContainerId, null);
            fail("No exception occured on submit with missing time stamp.");
        }
        catch (final Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Clean up after test.
     *
     * @throws Exception If anything fails.
     */
    @Override
    @After
    public void tearDown() throws Exception {

        super.tearDown();
        theContainerXml = null;

        theContainerId = null;

        // TODO purge object from Fedora
    }

}
