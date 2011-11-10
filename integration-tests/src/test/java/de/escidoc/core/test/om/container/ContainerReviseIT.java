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

import java.net.URL;

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.AssignParam;

import org.joda.time.DateTime;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test suite for ContainerHandler.revise service.
 *
 * @author Torsten Tetteroo
 */
public class ContainerReviseIT extends ContainerTestBase {

    private String theContainerXml;

    private String theContainerId;

    /**
     * Set up test.
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
     * Test successful revising a container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvc1() throws Exception {

        String resultXml =
            submit(theContainerId,
                getStatusTaskParam(getLastModificationDateValue2(getDocument(theContainerXml)), null));
        assertXmlValidResult(resultXml);

        Document doc = getDocument(resultXml);
        final String submittedLastModificationDate = getLastModificationDateValue(doc);

        try {
            resultXml = revise(theContainerId, getStatusTaskParam(getLastModificationDateValue2(doc), "comment"));
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException("Revising the submitted container failed", e);
        }
        assertXmlValidResult(resultXml);

        final String revisedXml = retrieve(theContainerId);
        final Document revisedDocument = EscidocAbstractTest.getDocument(revisedXml);
        assertDateBeforeAfter(submittedLastModificationDate, getLastModificationDateValue(revisedDocument));
        //        assertXmlEquals("Unexpected status. ", revisedDocument,
        //            XPATH_CONTAINER_STATUS, STATE_IN_REVISION);
        assertXmlEquals("Unexpected current version status", revisedDocument, XPATH_CONTAINER_CURRENT_VERSION_STATUS,
            STATE_IN_REVISION);
    }

    /**
     * Test declining revising a container in state pending.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvc2() throws Exception {

        try {
            revise(theContainerId, getStatusTaskParam(
                getLastModificationDateValue2(getDocument(retrieve(theContainerId))), null));
            EscidocAbstractTest.failMissingException(InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising a container in state released.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvc3() throws Exception {

        String resultXml =
            submit(theContainerId, getStatusTaskParam(
                getLastModificationDateValue2(getDocument(retrieve(theContainerId))), null));
        assertXmlValidResult(resultXml);

        String pidParam;
        AssignParam assignPidParam = new AssignParam();

        if (getContainerClient().getPidConfig("cmm.Container.objectPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.objectPid.releaseWithoutPid", "false")) {
            if (selectSingleNode(EscidocAbstractTest.getDocument(theContainerXml), "/container/properties/pid") == null) {

                assignPidParam.setUrl(new URL("http://somewhere/" + this.theContainerId));
                pidParam =
                    getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(this.theContainerId))),
                        assignPidParam);

                assignObjectPid(this.theContainerId, pidParam);
            }
        }
        if (getContainerClient().getPidConfig("cmm.Container.versionPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.versionPid.releaseWithoutPid", "false")) {

            String latestVersion = getLatestVersionObjidValue(theContainerXml);

            assignPidParam.setUrl(new URL("http://somewhere/" + latestVersion));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(latestVersion))),
                    assignPidParam);

            assignVersionPid(latestVersion, pidParam);
        }

        resultXml =
            release(theContainerId, getStatusTaskParam(
                getLastModificationDateValue2(getDocument(retrieve(theContainerId))), null));
        assertXmlValidResult(resultXml);

        try {
            revise(theContainerId, getStatusTaskParam(
                getLastModificationDateValue2(getDocument(retrieve(theContainerId))), null));
            EscidocAbstractTest.failMissingException(InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising a container in state withdrawn.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvc4() throws Exception {

        String resultXml =
            submit(theContainerId, getStatusTaskParam(
                getLastModificationDateValue2(getDocument(retrieve(theContainerId))), null));
        assertXmlValidResult(resultXml);

        String pidParam;
        AssignParam assignPidParam = new AssignParam();

        if (getContainerClient().getPidConfig("cmm.Container.objectPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.objectPid.releaseWithoutPid", "false")) {
            if (selectSingleNode(EscidocAbstractTest.getDocument(theContainerXml), "/container/properties/pid") == null) {

                assignPidParam.setUrl(new URL("http://somewhere/" + this.theContainerId));
                pidParam =
                    getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(this.theContainerId))),
                        assignPidParam);

                assignObjectPid(this.theContainerId, pidParam);
            }
        }
        if (getContainerClient().getPidConfig("cmm.Container.versionPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.versionPid.releaseWithoutPid", "false")) {

            String latestVersion = getLatestVersionObjidValue(theContainerXml);

            assignPidParam.setUrl(new URL("http://somewhere/" + latestVersion));
            pidParam =
                getAssignPidTaskParam(getLastModificationDateValue2(getDocument(retrieve(latestVersion))),
                    assignPidParam);

            assignVersionPid(latestVersion, pidParam);
        }

        resultXml =
            release(theContainerId, getStatusTaskParam(
                getLastModificationDateValue2(getDocument(retrieve(theContainerId))), null));
        assertXmlValidResult(resultXml);

        resultXml =
            withdraw(theContainerId, getStatusTaskParam(
                getLastModificationDateValue2(getDocument(retrieve(theContainerId))), null));
        assertXmlValidResult(resultXml);

        try {
            revise(theContainerId, getStatusTaskParam(
                getLastModificationDateValue2(getDocument(retrieve(theContainerId))), null));
            EscidocAbstractTest.failMissingException(InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising an unknown container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvc5() throws Exception {

        try {
            revise(UNKNOWN_ID, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(UNKNOWN_ID))),
                null));
            EscidocAbstractTest.failMissingException(ContainerNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising unknown container failed with unexpected exception. ",
                ContainerNotFoundException.class, e);
        }
    }

    /**
     * Test declining revising a container without providing a container id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvc6() throws Exception {

        try {
            revise(null, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theContainerId))), null));
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising without id failed with unexpected exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising a container without providing task param.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvc7() throws Exception {

        String resultXml =
            submit(theContainerId,
                getStatusTaskParam(getLastModificationDateValue2(getDocument(theContainerXml)), null));
        assertXmlValidResult(resultXml);

        try {
            revise(theContainerId, null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising without id failed with unexpected exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising a container without providing last modification date in task param.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testOMRvc8() throws Exception {

        String resultXml =
            submit(theContainerId, getStatusTaskParam(
                getLastModificationDateValue2(getDocument(retrieve(theContainerId))), null));
        assertXmlValidResult(resultXml);

        String param = getStatusTaskParam(null, null);

        revise(theContainerId, param);
    }

    /**
     * Test declining revising a container with corrupted task param.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = XmlCorruptedException.class)
    public void testOMRvc9() throws Exception {

        submit(theContainerId, getStatusTaskParam(getLastModificationDateValue2(getDocument(retrieve(theContainerId))),
            null));
        String param = "<param";

        revise(theContainerId, param);
    }

    /**
     * Test declining revising a container with providing outdated last modification date in task param.
     *
     * @throws Exception If anything fails.
     */
    @Test(expected = OptimisticLockingException.class)
    public void testOMRvc10() throws Exception {

        DateTime lmd = getLastModificationDateValue2(getDocument(retrieve(theContainerId)));
        submit(theContainerId, getStatusTaskParam(lmd, null));

        revise(theContainerId, getStatusTaskParam(lmd, null));
    }
}
