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

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.EscidocAbstractTest;
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
     * Gets the task param including the last modification date of the resource object identified by the field
     * <code>containerId</code>.
     *
     * @param includeWithdrawComment Flag indicating if the withdrawal comment shall be additionally included.
     * @return Returns the created task param
     * @throws Exception Thrown if anything fails.
     */
    protected String getTheLastModificationParam(final boolean includeWithdrawComment) throws Exception {

        return super.getTheLastModificationParam(includeWithdrawComment, this.theContainerId);
    }

    /**
     * Test successful revising a container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvc1() throws Exception {

        String paramXml = getTheLastModificationParam(false);
        String resultXml = submit(theContainerId, paramXml);
        assertXmlValidResult(resultXml);

        paramXml = getTheLastModificationParam(false);
        final Document paramDocument = EscidocAbstractTest.getDocument(paramXml);
        final String submittedLastModificationDate = getLastModificationDateValue(paramDocument);

        try {
            resultXml = revise(theContainerId, paramXml);
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

        String param = getTheLastModificationParam(false);

        try {
            revise(theContainerId, param);
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

        String param = getTheLastModificationParam(false);
        String resultXml = submit(theContainerId, param);
        assertXmlValidResult(resultXml);

        String pidParam;
        if (getContainerClient().getPidConfig("cmm.Container.objectPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.objectPid.releaseWithoutPid", "false")) {
            if (selectSingleNode(EscidocAbstractTest.getDocument(theContainerXml), "/container/properties/pid") == null) {
                pidParam = getPidParam(this.theContainerId, "http://somewhere" + this.theContainerId);
                assignObjectPid(this.theContainerId, pidParam);
            }
        }
        if (getContainerClient().getPidConfig("cmm.Container.versionPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theContainerXml);
            pidParam = getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false);
        resultXml = release(theContainerId, param);
        assertXmlValidResult(resultXml);

        param = getTheLastModificationParam(false);
        try {
            revise(theContainerId, param);
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

        String param = getTheLastModificationParam(false);
        String resultXml = submit(theContainerId, param);
        assertXmlValidResult(resultXml);

        String pidParam;
        if (getContainerClient().getPidConfig("cmm.Container.objectPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.objectPid.releaseWithoutPid", "false")) {
            if (selectSingleNode(EscidocAbstractTest.getDocument(theContainerXml), "/container/properties/pid") == null) {
                pidParam = getPidParam(this.theContainerId, "http://somewhere" + this.theContainerId);
                assignObjectPid(this.theContainerId, pidParam);
            }
        }
        if (getContainerClient().getPidConfig("cmm.Container.versionPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theContainerXml);
            pidParam = getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false);
        resultXml = release(theContainerId, param);
        assertXmlValidResult(resultXml);

        param = getTheLastModificationParam(true);
        resultXml = withdraw(theContainerId, param);
        assertXmlValidResult(resultXml);

        param = getTheLastModificationParam(false);

        try {
            revise(theContainerId, param);
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

        String param = getTheLastModificationParam(false);

        try {
            revise(UNKNOWN_ID, param);
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

        String param = getTheLastModificationParam(false);

        try {
            revise(null, param);
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

        String param = getTheLastModificationParam(false);
        String resultXml = submit(theContainerId, param);
        assertXmlValidResult(resultXml);

        param = getTheLastModificationParam(false);

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
    @Test
    public void testOMRvc8() throws Exception {

        String param = getTheLastModificationParam(false);
        String resultXml = submit(theContainerId, param);
        assertXmlValidResult(resultXml);

        param = "<param />";

        try {
            revise(theContainerId, param);
            // TODO should be XmlCorruptedException ???
            EscidocAbstractTest.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising without last modification date failed with"
                + " unexpected exception. ", XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining revising a container with corrupted task param.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvc9() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(theContainerId, param);
        param = "<param";

        try {
            revise(theContainerId, param);
            // TODO should be XmlCorruptedException ???
            EscidocAbstractTest.failMissingException(XmlCorruptedException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising without last modification date failed with"
                + " unexpected exception. ", XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining revising a container with providing outdated last modification date in task param.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMRvc10() throws Exception {

        String param = getTheLastModificationParam(false, theContainerId);
        submit(theContainerId, param);

        try {
            revise(theContainerId, param);
            EscidocAbstractTest.failMissingException(OptimisticLockingException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType("Revising with outdated last modification date failed "
                + "with unexpected exception. ", OptimisticLockingException.class, e);
        }
    }
}
