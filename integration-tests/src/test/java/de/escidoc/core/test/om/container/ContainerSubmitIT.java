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
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import static org.junit.Assert.assertTrue;
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

        DateTime t1 = getLastModificationDateValue2(getDocument(this.theContainerXml));
        String xml = submit(theContainerId, getStatusTaskParam(t1, null));
        DateTime t2 = getLastModificationDateValue2(getDocument(xml));

        assertTrue("Timestamp of submitted not after pending", t1.compareTo(t2) < 0);

        final Document submittedDocument = EscidocAbstractTest.getDocument(retrieve(theContainerId));
        DateTime t2a = getLastModificationDateValue2(submittedDocument);

        assertTrue("Timestamp of submitted not after pending", t1.compareTo(t2a) < 0);

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

        String xml =
            submit(theContainerId, getStatusTaskParam(getLastModificationDateValue2(getDocument(this.theContainerXml)),
                null));
        DateTime t1 = getLastModificationDateValue2(getDocument(xml));
        xml = revise(theContainerId, getStatusTaskParam(t1, null));

        DateTime t2 = getLastModificationDateValue2(getDocument(xml));

        assertTrue("Timestamp of submitted not after pending", t1.compareTo(t2) < 0);

        xml = submit(theContainerId, getStatusTaskParam(t2, null));
        DateTime t3 = getLastModificationDateValue2(getDocument(xml));

        assertTrue("Timestamp of submitted not after in-revision", t2.compareTo(t3) < 0);

        final Document submittedDocument = EscidocAbstractTest.getDocument(retrieve(theContainerId));
        DateTime t3a = getLastModificationDateValue2(submittedDocument);

        assertTrue("Timestamp of submitted not after in-revision", t2.compareTo(t3a) < 0);
        assertTrue("Timestamp not equal", t3.compareTo(t3a) == 0);

        assertXmlEquals("Unexpected status. ", submittedDocument, XPATH_CONTAINER_STATUS, STATE_SUBMITTED);
        assertXmlEquals("Unexpected current version status", submittedDocument, XPATH_CONTAINER_CURRENT_VERSION_STATUS,
            STATE_SUBMITTED);
    }

    /**
     * Test declining submit of container with non existing id.
     */
    @Test
    public void testOM_SC_2_1() throws Exception {

        try {
            submit("bla",
                getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theContainerId))), null));
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

        try {
            submit(theContainerId, getStatusTaskParam(new DateTime(), null));
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

        try {
            submit(null, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theContainerId))), null));
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
}
