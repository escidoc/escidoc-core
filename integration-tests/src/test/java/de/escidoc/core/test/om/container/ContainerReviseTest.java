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

import de.escidoc.core.test.EscidocRestSoapTestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;

/**
 * Test suite for ContainerHandler.revise service.
 * 
 * @author TTE
 * 
 */
@RunWith(value = Parameterized.class)
public class ContainerReviseTest extends ContainerTestBase {

    private String theContainerXml;

    private String theContainerId;

    /**
     * @param transport
     *            The transport identifier.
     */
    public ContainerReviseTest(final int transport) {
        super(transport);
    }

    /**
     * Set up test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    @Before
    public void setUp() throws Exception {
        String xmlData =
            getContainerTemplate("create_container_WithoutMembers_v1.1.xml");

        this.theContainerXml = create(xmlData);
        this.theContainerId = getObjidValue(this.theContainerXml);
        super.setUp();
    }

    /**
     * Gets the task param including the last modification date of the resource
     * object identified by the field <code>containerId</code>.
     * 
     * @param includeWithdrawComment
     *            Flag indicating if the withdrawal comment shall be
     *            additionally included.
     * @return Returns the created task param
     * @throws Exception
     *             Thrown if anything fails.
     */
    protected String getTheLastModificationParam(
        final boolean includeWithdrawComment) throws Exception {

        return super.getTheLastModificationParam(includeWithdrawComment,
            this.theContainerId);
    }

    /**
     * Test successful revising a container.
     * 
     * @test.name Revise Container - Submitted
     * @test.id OM_RVC-1
     * @test.input
     *          <ul>
     *          <li>existing container id of a container in state submitted</li>
     *          <li>timestamp of the last modification of the container</li>
     *          </ul>
     * @test.expected: No result, no exception, Container has been revised.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvc1() throws Exception {

        String paramXml = getTheLastModificationParam(false);
        String resultXml = submit(theContainerId, paramXml);
        assertXmlValidResult(resultXml);

        paramXml = getTheLastModificationParam(false);
        final Document paramDocument =
            EscidocRestSoapTestBase.getDocument(paramXml);
        final String submittedLastModificationDate =
            getLastModificationDateValue(paramDocument);

        try {
            resultXml = revise(theContainerId, paramXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(
                "Revising the submitted container failed", e);
        }
        assertXmlValidResult(resultXml);

        final String revisedXml = retrieve(theContainerId);
        final Document revisedDocument =
            EscidocRestSoapTestBase.getDocument(revisedXml);
        assertDateBeforeAfter(submittedLastModificationDate,
            getLastModificationDateValue(revisedDocument));
//        assertXmlEquals("Unexpected status. ", revisedDocument,
//            XPATH_CONTAINER_STATUS, STATE_IN_REVISION);
        assertXmlEquals("Unexpected current version status", revisedDocument,
            XPATH_CONTAINER_CURRENT_VERSION_STATUS, STATE_IN_REVISION);
    }

    /**
     * Test declining revising a container in state pending.
     * 
     * @test.name Revise Container - Pending
     * @test.id OM_RVC-2
     * @test.input
     *          <ul>
     *          <li>existing container id of a container in state pending</li>
     *          <li>timestamp of the last modification of the container</li>
     *          </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvc2() throws Exception {

        String param = getTheLastModificationParam(false);

        try {
            revise(theContainerId, param);
            EscidocRestSoapTestBase
                .failMissingException(InvalidStatusException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising a container in state released.
     * 
     * @test.name Revise Container - Released
     * @test.id OM_RVC-3
     * @test.input
     *          <ul>
     *          <li>existing container id of a container in state released</li>
     *          <li>timestamp of the last modification of the container</li>
     *          </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvc3() throws Exception {

        String param = getTheLastModificationParam(false);
        String resultXml = submit(theContainerId, param);
        assertXmlValidResult(resultXml);

        String pidParam;
        if (getContainerClient().getPidConfig(
            "cmm.Container.objectPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig(
                "cmm.Container.objectPid.releaseWithoutPid", "false")) {
            if (selectSingleNode(EscidocRestSoapTestBase
                .getDocument(theContainerXml), "/container/properties/pid") == null) {
                pidParam =
                    getPidParam(this.theContainerId, "http://somewhere"
                        + this.theContainerId);
                assignObjectPid(this.theContainerId, pidParam);
            }
        }
        if (getContainerClient().getPidConfig(
            "cmm.Container.versionPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig(
                "cmm.Container.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theContainerXml);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false);
        resultXml = release(theContainerId, param);
        assertXmlValidResult(resultXml);

        param = getTheLastModificationParam(false);
        try {
            revise(theContainerId, param);
            EscidocRestSoapTestBase
                .failMissingException(InvalidStatusException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising a container in state withdrawn.
     * 
     * @test.name Revise Container - Withdrawn
     * @test.id OM_RVC-4
     * @test.input
     *          <ul>
     *          <li>existing container id of a container in state withdrawn</li>
     *          <li>timestamp of the last modification of the container</li>
     *          </ul>
     * @test.expected: InvalidStatusException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvc4() throws Exception {

        String param = getTheLastModificationParam(false);
        String resultXml = submit(theContainerId, param);
        assertXmlValidResult(resultXml);

        String pidParam;
        if (getContainerClient().getPidConfig(
            "cmm.Container.objectPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig(
                "cmm.Container.objectPid.releaseWithoutPid", "false")) {
            if (selectSingleNode(EscidocRestSoapTestBase
                .getDocument(theContainerXml), "/container/properties/pid") == null) {
                pidParam =
                    getPidParam(this.theContainerId, "http://somewhere"
                        + this.theContainerId);
                assignObjectPid(this.theContainerId, pidParam);
            }
        }
        if (getContainerClient().getPidConfig(
            "cmm.Container.versionPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig(
                "cmm.Container.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theContainerXml);
            pidParam =
                getPidParam(latestVersion, "http://somewhere" + latestVersion);
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
            EscidocRestSoapTestBase
                .failMissingException(InvalidStatusException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising failed with unexpected exception. ",
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining revising an unknown container.
     * 
     * @test.name Revise Container - Unknown container
     * @test.id OM_RVC-5
     * @test.input
     *          <ul>
     *          <li>id for that no container exists</li>
     *          <li>timestamp of the last modification of the container</li>
     *          </ul>
     * @test.expected: ContainerNotFoundException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvc5() throws Exception {

        String param = getTheLastModificationParam(false);

        try {
            revise(UNKNOWN_ID, param);
            EscidocRestSoapTestBase
                .failMissingException(ContainerNotFoundException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase
                .assertExceptionType(
                    "Revising unknown container failed with unexpected exception. ",
                    ContainerNotFoundException.class, e);
        }
    }

    /**
     * Test declining revising a container without providing a container id.
     * 
     * @test.name Revise Container - Missing container id
     * @test.id OM_RVC-6
     * @test.input
     *          <ul>
     *          <li>no container id is provided</li>
     *          <li>timestamp of the last modification of the container</li>
     *          </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvc6() throws Exception {

        String param = getTheLastModificationParam(false);

        try {
            revise(null, param);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising without id failed with unexpected exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising a container without providing task param.
     * 
     * @test.name Revise Container - Missing task param
     * @test.id OM_RVC-7
     * @test.input
     *          <ul>
     *          <li>existing container id of a container in state submitted</li>
     *          <li>No task param is provided</li>
     *          </ul>
     * @test.expected: MissingMethodParameterException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvc7() throws Exception {

        String param = getTheLastModificationParam(false);
        String resultXml = submit(theContainerId, param);
        assertXmlValidResult(resultXml);

        param = getTheLastModificationParam(false);

        try {
            revise(theContainerId, null);
            EscidocRestSoapTestBase
                .failMissingException(MissingMethodParameterException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising without id failed with unexpected exception. ",
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining revising a container without providing last modification
     * date in task param.
     * 
     * @test.name Revise Container - Missing last modification date
     * @test.id OM_RVC-8
     * @test.input
     *          <ul>
     *          <li>existing container id of a container in state submitted</li>
     *          <li>No last modification date is provided in task param</li>
     *          </ul>
     * @test.expected: MissingAttributeValueException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
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
            EscidocRestSoapTestBase
                .failMissingException(XmlCorruptedException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising without last modification date failed with"
                    + " unexpected exception. ", XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining revising a container with corrupted task param.
     * 
     * @test.name Revise Container - Corrupted task param
     * @test.id OM_RVC-9
     * @test.input
     *          <ul>
     *          <li>existing container id of a container in state submitted</li>
     *          <li>Corrupted task param is provided</li>
     *          </ul>
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvc9() throws Exception {

        String param = getTheLastModificationParam(false);
        submit(theContainerId, param);
        param = "<param";

        try {
            revise(theContainerId, param);
            // TODO should be XmlCorruptedException ???
            EscidocRestSoapTestBase
                .failMissingException(XmlCorruptedException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising without last modification date failed with"
                    + " unexpected exception. ", XmlCorruptedException.class, e);
        }
    }

    /**
     * Test declining revising a container with providing outdated last
     * modification date in task param.
     * 
     * @test.name Revise Container - Corrupted task param
     * @test.id OM_RVC-10
     * @test.input
     *          <ul>
     *          <li>existing container id of a container in state submitted</li>
     *          <li>task param is provided that contains an outdated last
     *          modificaton date</li>
     *          </ul>
     * @test.expected: XmlCorruptedException
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMRvc10() throws Exception {

        String param = getTheLastModificationParam(false, theContainerId);
        submit(theContainerId, param);

        try {
            revise(theContainerId, param);
            EscidocRestSoapTestBase
                .failMissingException(OptimisticLockingException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Revising with outdated last modification date failed "
                    + "with unexpected exception. ",
                OptimisticLockingException.class, e);
        }
    }
}
