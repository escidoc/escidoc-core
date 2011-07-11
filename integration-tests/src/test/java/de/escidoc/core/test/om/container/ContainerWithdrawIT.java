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
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.AlreadyWithdrawnException;
import de.escidoc.core.common.exceptions.remote.application.violated.OptimisticLockingException;
import de.escidoc.core.test.EscidocAbstractTest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the Container resource.
 *
 * @author Michael Schneider
 */
public class ContainerWithdrawIT extends ContainerTestBase {

    private static final String WITHDRAW_COMMENT = "This is a withdraw comment.";

    private String theContainerXml;

    private String theContainerId;

    private String theItemId;

    private String theSubcontainerId;

    /**
     * Test declining withdraw of container with non existing container id.
     */
    @Test
    public void testOM_WAC_2_1() throws Exception {

        String param = getTheLastModificationParam(true, theContainerId);

        try {
            withdraw("bla", param);
            fail("No exception occurred on withdraw with non" + "existing container id.");
        }
        catch (final Exception e) {
            Class<?> ec = ContainerNotFoundException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test declining withdraw of container with wrong time stamp.
     */
    @Test
    public void test_OM_WAC_2_2() throws Exception {

        String param = getTheLastModificationParam(false, theContainerId);
        submit(theContainerId, param);

        String pidParam;
        if (getContainerClient().getPidConfig("cmm.Container.objectPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.objectPid.releaseWithoutPid", "false")) {
            pidParam = getPidParam(this.theContainerId, "http://somewhere" + this.theContainerId);
            assignObjectPid(this.theContainerId, pidParam);
        }
        if (getContainerClient().getPidConfig("cmm.Container.versionPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theContainerXml);
            pidParam = getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false, theContainerId);
        release(theContainerId, param);

        param = getTheLastModificationParam(true, theContainerId);
        param =
            param.replaceFirst("<param last-modification-date=\"([0-9TZ:\\.-])+\"",
                "<param last-modification-date=\"2005-01-30T11:36:42.015Z\"");

        try {
            withdraw(theContainerId, param);
            fail("No exception occurred on withdraw with " + "wrong time stamp.");
        }
        catch (final Exception e) {
            Class<?> ec = OptimisticLockingException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test declining withdraw of container with missing container id.
     */
    @Test
    public void testOM_WAC_3_1() throws Exception {

        String param = getTheLastModificationParam(true, theContainerId);

        try {
            withdraw(null, param);
            fail("No exception occurred on withdraw with missing " + "container id.");
        }
        catch (final Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test declining withdraw of container with missing time stamp.
     */
    @Test
    public void testOM_WAC_3_2() throws Exception {

        try {
            withdraw(theContainerId, null);
            fail("No exception occurred on withdraw with missing" + "time stamp.");
        }
        catch (final Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Test declining withdraw of container before release.
     */
    @Test
    public void testOMWAC3_3() throws Exception {

        String param = getTheLastModificationParam(false, theContainerId);
        submit(theContainerId, param);
        param = getTheLastModificationParam(true, theContainerId);

        try {
            withdraw(theContainerId, param);
            fail("No exception occurred on withdraw bevore submit.");
        }
        catch (final Exception e) {
            Class<?> ec = InvalidStatusException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }
    }

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        this.theItemId = createItemFromTemplate("escidoc_item_198_for_create.xml");

        String xmlData = getContainerTemplate("create_container_WithoutMembers_v1.1.xml");
        theContainerXml = create(xmlData);

        this.theSubcontainerId = getObjidValue(theContainerXml);

        String xmlData1 = getContainerTemplate("create_container_v1.1-forItemAndforContainer.xml");
        String xmlWithItem = xmlData1.replaceAll("##ITEMID##", theItemId);
        String xmlWithItemAndContainer = xmlWithItem.replaceAll("##CONTAINERID##", theSubcontainerId);
        theContainerXml = create(xmlWithItemAndContainer);
        // String xmlData1 = getTemplateAsString(TEMPLATE_CONTAINER_PATH,
        // "create_container_v1.1-forContainer.xml");
        //        
        // String xmlWithContainer =
        // xmlData1.replaceAll("##CONTAINERID##}", theSubcontainerId);
        // theContainerXml = create(xmlWithContainer);

        this.theContainerId = getObjidValue(theContainerXml);

    }

    @Test
    public void testOM_WAC_1() throws Exception {
        String param = null;

        submitItemHelp();
        param = getTheLastModificationParam(false, theSubcontainerId);
        submit(theSubcontainerId, param);

        param = getTheLastModificationParam(false, theContainerId);
        submit(theContainerId, param);

        String pidParam;
        if (getContainerClient().getPidConfig("cmm.Container.objectPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.objectPid.releaseWithoutPid", "false")) {
            pidParam = getPidParam(this.theContainerId, "http://somewhere" + this.theContainerId);
            assignObjectPid(this.theContainerId, pidParam);
        }
        if (getContainerClient().getPidConfig("cmm.Container.versionPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theContainerXml);
            pidParam = getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false, theContainerId);
        release(theContainerId, param);

        param = getTheLastModificationParam(true, theContainerId);
        withdraw(theContainerId, param);
        try {
            String withdrawnContainer = retrieve(theContainerId);
            assertXmlEquals("Item is not in state withdrawn!", withdrawnContainer,
                "/container/properties/public-status", STATUS_WITHDRAWN);
        }
        catch (final Exception e) {
            fail("Unexpected exception occurred on retrieve after withdraw.");
        }

        // FIXME We have no content-types and can not decide if members should
        // be withdrawn too.

        // try {
        // String withdrawnContainer = retrieve(theSubcontainerId);
        // assertXMLEquals("Item is not in state withdrawn!",
        // withdrawnContainer, "/container/properties/status",
        // STATUS_WITHDRAWN);
        // }
        // catch (final Exception e) {
        // fail("Unexpected exception occurred on retrieve after withdraw.");
        // }
        //
        // try {
        // String xmlResult = handleXmlResult(getItemClient().retrieve(
        // theItemId));
        // assertXMLEquals("Item is not in state withdrawn!", xmlResult,
        // "/item/properties/status", STATUS_WITHDRAWN);
        // }
        // catch (final Exception e) {
        // fail("Unexpected exception occurred! " + e);
        // }

        // ---------------------------------------------------------------
        // test update withdrawn Item
        // see also (issue INFR-710)
        try {
            String newItemXml = addCtsElement(theContainerXml);
            update(theContainerId, newItemXml);
            fail("Update after withdrawn is possible.");
        }
        catch (final InvalidStatusException e) {
            // that's ok
        }

        try {
            submit(theContainerId, getTheLastModificationParam(false, theContainerId));
            fail("Submit after withdrawn is possible.");
        }
        catch (final InvalidStatusException e) {
            // that's ok
        }

        try {
            revise(theContainerId, getTheLastModificationParam(false, theContainerId));
            fail("Revise after withdrawn is possible.");
        }
        catch (final InvalidStatusException e) {
            // that's ok
        }

        try {
            release(theContainerId, getTheLastModificationParam(false, theContainerId));
            fail("Release after withdrawn is possible.");
        }
        catch (final InvalidStatusException e) {
            // that's ok
        }

        try {
            delete(theContainerId);
            fail("Delete after withdrawn is possible.");
        }
        catch (final InvalidStatusException e) {
            // that's ok
        }

        try {
            lock(theContainerId, getTheLastModificationParam(false, theContainerId));
            fail("Lock after withdrawn is possible.");
        }
        catch (final InvalidStatusException e) {
            // that's ok
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

        theSubcontainerId = null;
        // TODO purge object from Fedora
    }

    private void submitItemHelp() throws Exception {
        String param = getTheLastModificationParam(false, theItemId);
        Object result1 = getItemClient().submit(theItemId, param);
        if (result1 instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result1;
            assertHttpStatusOfMethod("", httpRes);
        }
    }

    /**
     * Test declining second withdraw of container.
     */
    @Test
    public void testOM_WAC_4() throws Exception {

        String param = null;

        submitItemHelp();
        param = getTheLastModificationParam(false, theSubcontainerId);
        submit(theSubcontainerId, param);

        param = getTheLastModificationParam(false, theContainerId);
        submit(theContainerId, param);

        String pidParam;
        if (getContainerClient().getPidConfig("cmm.Container.objectPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.objectPid.releaseWithoutPid", "false")) {
            pidParam = getPidParam(this.theContainerId, "http://somewhere" + this.theContainerId);
            assignObjectPid(this.theContainerId, pidParam);
        }
        if (getContainerClient().getPidConfig("cmm.Container.versionPid.setPidBeforeRelease", "true")
            && !getContainerClient().getPidConfig("cmm.Container.versionPid.releaseWithoutPid", "false")) {
            String latestVersion = getLatestVersionObjidValue(theContainerXml);
            pidParam = getPidParam(latestVersion, "http://somewhere" + latestVersion);
            assignVersionPid(latestVersion, pidParam);
        }

        param = getTheLastModificationParam(false, theContainerId);
        release(theContainerId, param);

        param = getTheLastModificationParam(true, theContainerId);
        withdraw(theContainerId, param);
        try {
            withdraw(theContainerId, param);
            fail("No exception occurred on second withdraw.");
        }
        catch (final Exception e) {
            Class<?> ec = AlreadyWithdrawnException.class;
            EscidocAbstractTest.assertExceptionType(ec.getName() + " expected.", ec, e);
        }

    }

    // TODO FRS: I reinserted this because it is not clear for me, how to
    // retrieve last-mod-date for item and/or container from EscidocTestBase
    @Override
    public String getTheLastModificationParam(boolean includeWithdrawComment, String id) throws Exception {
        String lastModificationDate = null;
        try {
            Document container = EscidocAbstractTest.getDocument(retrieve(id));

            // get last-modification-date
            NamedNodeMap atts = container.getDocumentElement().getAttributes();
            Node lastModificationDateNode = atts.getNamedItem("last-modification-date");
            lastModificationDate = lastModificationDateNode.getNodeValue();
        }
        catch (final ContainerNotFoundException e) {
            // nothing to do
        }
        if (lastModificationDate == null) {
            Object result = getItemClient().retrieve(id);
            String xmlResult = null;
            if (result instanceof HttpResponse) {
                HttpResponse httpRes = (HttpResponse) result;
                assertHttpStatusOfMethod("", httpRes);
                xmlResult = EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);

            }
            else if (result instanceof String) {
                xmlResult = (String) result;
            }
            Document item = EscidocAbstractTest.getDocument(xmlResult);
            // get last-modification-date
            NamedNodeMap atts = item.getDocumentElement().getAttributes();
            Node lastModificationDateNode = atts.getNamedItem("last-modification-date");
            lastModificationDate = lastModificationDateNode.getNodeValue();
        }
        String param = "<param last-modification-date=\"" + lastModificationDate + "\" ";
        param += ">";
        if (includeWithdrawComment) {
            param += "<withdraw-comment>" + WITHDRAW_COMMENT + "</withdraw-comment>";
            // param += "withdraw-comment=\"" + WITHDRAW_COMMENT + "\"";
        }
        param += "</param>";

        return param;
    }
}
