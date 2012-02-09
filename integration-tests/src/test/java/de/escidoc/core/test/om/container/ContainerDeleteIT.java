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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.fedora.TripleStoreTestBase;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Test the mock implementation of the container resource.
 *
 * @author Michael Schneider
 */
public class ContainerDeleteIT extends ContainerTestBase {

    /**
     * Test successfully delete of container in status 'pending'.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void testOM_DC_1() throws Exception {

        String containerId = createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        delete(containerId);

        try {
            retrieve(containerId);
            EscidocAbstractTest.failMissingException(ContainerNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ContainerNotFoundException.class, e);
        }
    }

    /**
     * Test successfully delete of container in status 'revised'.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void testOM_DC_7() throws Exception {

        String containerId = createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        String paramXml = getTheLastModificationParam(false, containerId);
        submit(containerId, paramXml);
        paramXml = getTheLastModificationParam(false, containerId);
        revise(containerId, paramXml);
        delete(containerId);

        try {
            retrieve(containerId);
            EscidocAbstractTest.failMissingException(ContainerNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ContainerNotFoundException.class, e);
        }
    }

    /**
     * Test declining delete of container which contains members.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void testOM_DC_5() throws Exception {

        String theItemId = createItemFromTemplate("escidoc_item_198_for_create.xml");
        String xmlData = getContainerTemplate("create_container_v1.1-forItem.xml");

        String replaced = xmlData.replaceAll("##ITEMID##", theItemId);
        assertXmlValidContainer(replaced);
        String theContainerXml = create(replaced);

        String theContainerId = getObjidValue(theContainerXml);
        try {
            delete(theContainerId);
            EscidocAbstractTest.failMissingException("No exception with deleting container with members.",
                InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining delete of container with non existing id.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void testOM_DC_2() throws Exception {

        try {
            delete("bla");
            EscidocAbstractTest.failMissingException("No exception occurred on delete with non existing id.",
                ContainerNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ContainerNotFoundException.class, e);
        }
    }

    /**
     * Test declining delete of container with missing id.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void testOM_DC_3() throws Exception {

        try {
            delete(null);
            EscidocAbstractTest.failMissingException("No exception occurred on delete with missing id.",
                MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining delete of container in status submitted.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void testOM_DC_6() throws Exception {

        String containerId = createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        String param = getTheLastModificationParam(false, containerId);
        submit(containerId, param);
        try {
            delete(containerId);
            EscidocAbstractTest.failMissingException("No exception occurred on delete submitted container.",
                InvalidStatusException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(InvalidStatusException.class, e);
        }
    }

    /**
     * Test successfully delete container which is member.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void testDeleteMemberContainer() throws Exception {

        String theItemId = createItemFromTemplate("escidoc_item_198_for_create.xml");
        String theContainerId = createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        String xmlData = getContainerTemplate("create_container_v1.1-forItemAndforContainer.xml");

        String replaced = xmlData.replaceAll("##ITEMID##", theItemId);
        replaced = replaced.replaceAll("##CONTAINERID##", theContainerId);
        assertXmlValidContainer(replaced);
        String theParentId = getObjidValue(create(replaced));

        delete(theContainerId);
        String parentAfterDeletion = retrieve(theParentId);
        // first check if removed from container struct-map, but that's already
        // the case if member does not longer exist
        NodeList containerMembers = selectNodeList(getDocument(parentAfterDeletion), "/container/members//container");
        assertEquals("Member entry should be deleted while deleting member.", 0, containerMembers.getLength());
        // check if RELS-EXT does not longer contain the member
        String triplestoreEntry =
            new TripleStoreTestBase().requestMPT("<info:fedora/" + theParentId
                + " <http://escidoc.de/core/01/structural-relations/member> <info:fedora/" + theContainerId + ">",
                TripleStoreTestBase.FORMAT_MPT);
        assertEquals("Member entry should be deleted while deleting member.", 0, triplestoreEntry.length());
    }

    /**
     * Test declining delete of container which is member because of insufficient rights to modify parent container.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void testDeclineDeleteMemberContainer01() throws Exception {

        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        String theItemId = createItemFromTemplate("escidoc_item_198_for_create.xml");
        String theContainerId = createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        String xmlData = getContainerTemplate("create_container_v1.1-forItemAndforContainer.xml");

        String replaced = xmlData.replaceAll("##ITEMID##", theItemId);
        replaced = replaced.replaceAll("##CONTAINERID##", theContainerId);
        assertXmlValidContainer(replaced);
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        create(replaced);

        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        Class ec = AuthorizationException.class;
        try {
            delete(theContainerId);
            fail("Delete of member must fail without update rights to container.");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
    }

    /**
     * Test successfully delete item which is member.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void testDeleteMemberItem() throws Exception {

        String theItemId = createItemFromTemplate("escidoc_item_198_for_create.xml");
        String theContainerId = createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        String xmlData = getContainerTemplate("create_container_v1.1-forItemAndforContainer.xml");

        String replaced = xmlData.replaceAll("##ITEMID##", theItemId);
        replaced = replaced.replaceAll("##CONTAINERID##", theContainerId);
        assertXmlValidContainer(replaced);
        String theParentId = getObjidValue(create(replaced));

        getItemClient().delete(theItemId);
        String parentAfterDeletion = retrieve(theParentId);
        // first check if removed from container struct-map, but that's already
        // the case if member does not longer exist
        NodeList containerMembers = selectNodeList(getDocument(parentAfterDeletion), "/container/struct-map//item");
        assertEquals("Member entry should be deleted while deleting member.", 0, containerMembers.getLength());
        // check if RELS-EXT does not longer contain the member
        String triplestoreEntry =
            new TripleStoreTestBase().requestMPT("<info:fedora/" + theParentId
                + " <http://escidoc.de/core/01/structural-relations/member> <info:fedora/" + theItemId + ">",
                TripleStoreTestBase.FORMAT_MPT);
        assertEquals("Member entry should be deleted while deleting member.", 0, triplestoreEntry.length());

    }

    /**
     * Test delete of members which are in status released.
     * 
     * Issue INFR-1199
     * 
     * 10 containers in status released are set as members to a further container. Then are three of these containers to
     * delete. 2 new Container are to add as member and the parent Container is to release at least.  
     * 
     * @throws Exception
     *             If an error occurs.
     */
    @Test
    public void deleteReleasedContainerMember() throws Exception {

        List<String> memberIDs = new ArrayList<String>();

        String xmlData = getContainerTemplate("create_container_WithoutMembers_v1.1.xml");

        // create released Container
        for (int i = 0; i < 10; i++) {
            // create Container
            Document x = getDocument(create(xmlData));
            String containerId = getObjidValue(x);
            String lmdContainer = getLastModificationDateValue(x);

            // submit Container
            lmdContainer = prepareContainerPid(containerId, lmdContainer);
            String param =
                getTheLastModificationParam(true, containerId, String.valueOf(System.nanoTime()), lmdContainer);
            String resultXml = submit(containerId, param);
            lmdContainer = getLastModificationDateValue(getDocument(resultXml));

            // release the Container
            param = getTheLastModificationParam(true, containerId, String.valueOf(System.nanoTime()), lmdContainer);
            release(containerId, param);

            // add to member list
            memberIDs.add(containerId);
        }

        // create Container
        Document xP = getDocument(create(xmlData));
        String pContainerId = getObjidValue(xP);
        String lmdpContainer = getLastModificationDateValue(xP);

        // add all other as member to this Container
        StringBuilder taskParam = new StringBuilder("<param last-modification-date=\"" + lmdpContainer + "\">");

        Iterator<String> it = memberIDs.iterator();
        while (it.hasNext()) {
            taskParam.append("<id>");
            taskParam.append(it.next());
            taskParam.append("</id>");
        }
        taskParam.append("</param>");

        String resultXml = addMembers(pContainerId, taskParam.toString());
        lmdpContainer = getLastModificationDateValue(getDocument(resultXml));

        // assert that the container is in version-status pending (because of addMembers)
        String rXml = retrieve(pContainerId);
        assertXmlExists("Wrong pulic-status", rXml, "/container/properties/public-status[text() = 'pending']");
        assertXmlExists("Wrong version status", rXml, "/container/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong number of members", rXml, "/container/struct-map[count(./container) = '10']");
        assertXmlExists("Wrong version number", rXml, "/container/properties/version/number[text() = '2']");

        // submit Container
        lmdpContainer = prepareContainerPid(pContainerId, lmdpContainer);
        String param =
            getTheLastModificationParam(true, pContainerId, String.valueOf(System.nanoTime()), lmdpContainer);
        resultXml = submit(pContainerId, param);
        lmdpContainer = getLastModificationDateValue(getDocument(resultXml));

        // release the Container
        param = getTheLastModificationParam(true, pContainerId, String.valueOf(System.nanoTime()), lmdpContainer);
        resultXml = release(pContainerId, param);
        lmdpContainer = getLastModificationDateValue(getDocument(resultXml));

        // purge three members (with admin purge method)
        List<String> deletedMemberIDs = new ArrayList<String>();
        taskParam = new StringBuilder("<param last-modification-date=\"" + lmdpContainer + "\">");

        taskParam.append("<id>").append(memberIDs.get(2)).append("</id>");
        deletedMemberIDs.add(memberIDs.remove(2));
        taskParam.append("<id>").append(memberIDs.get(3)).append("</id>");
        deletedMemberIDs.add(memberIDs.remove(3));
        taskParam.append("<id>").append(memberIDs.get(5)).append("</id>");
        deletedMemberIDs.add(memberIDs.remove(5));

        taskParam.append("</param>");

        getAdminClient().deleteObjects(taskParam.toString());

        // wait until process has finished
        final int waitTime = 5000;

        while (true) {
            String status = handleXmlResult(getAdminClient().getPurgeStatus());
            if (status.indexOf("finished") > 0) {
                break;
            }
            Thread.sleep(waitTime);
        }

        // TODO assert Members
        rXml = retrieve(pContainerId);
        // retrieve method displayes only existing members
        assertXmlExists("Wrong number of members", rXml, "/container/struct-map[count(./container) = '7']");

        // add two new container
        List<String> newMemberIDs = new ArrayList<String>();

        // create released Container
        for (int i = 0; i < 2; i++) {
            // create Container
            Document x = getDocument(create(xmlData));
            String containerId = getObjidValue(x);
            String lmdContainer = getLastModificationDateValue(x);

            // submit Container
            lmdContainer = prepareContainerPid(containerId, lmdContainer);
            param = getTheLastModificationParam(true, containerId, String.valueOf(System.nanoTime()), lmdContainer);
            resultXml = submit(containerId, param);
            lmdContainer = getLastModificationDateValue(getDocument(resultXml));

            // release the Container
            param = getTheLastModificationParam(true, containerId, String.valueOf(System.nanoTime()), lmdContainer);
            release(containerId, param);

            // add to member list
            newMemberIDs.add(containerId);
        }
        // add new Container to parent Container
        taskParam = new StringBuilder("<param last-modification-date=\"" + lmdpContainer + "\">");

        it = newMemberIDs.iterator();
        while (it.hasNext()) {
            taskParam.append("<id>");
            taskParam.append(it.next());
            taskParam.append("</id>");
            it.remove();
        }
        taskParam.append("</param>");

        resultXml = addMembers(pContainerId, taskParam.toString());
        lmdpContainer = getLastModificationDateValue(getDocument(resultXml));

        // check container version status
        rXml = retrieve(pContainerId);
        assertXmlExists("Wrong pulic-status", rXml, "/container/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", rXml, "/container/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong number of members", rXml, "/container/struct-map[count(./container) = '9']");
        assertXmlExists("Wrong version number", rXml, "/container/properties/version/number[text() = '3']");

        // release Container again
        // submit Container
        lmdpContainer = prepareContainerPid(pContainerId, lmdpContainer);
        param = getTheLastModificationParam(true, pContainerId, String.valueOf(System.nanoTime()), lmdpContainer);
        resultXml = submit(pContainerId, param);
        lmdpContainer = getLastModificationDateValue(getDocument(resultXml));

        // release the Container
        param = getTheLastModificationParam(true, pContainerId, String.valueOf(System.nanoTime()), lmdpContainer);
        try {
            release(pContainerId, param);
            fail("IntegritySystemException expected");
        }
        // FIXME actually an IntegrityException should be thrown (but it seems not be the case - INFR-1469)  
        catch (Exception e) {
            // that's ok
        }

        // now try to fix the container again by removing the purged members from container
        taskParam = new StringBuilder("<param last-modification-date=\"" + lmdpContainer + "\">");

        it = deletedMemberIDs.iterator();
        while (it.hasNext()) {
            taskParam.append("<id>");
            taskParam.append(it.next());
            taskParam.append("</id>");
            it.remove();
        }
        taskParam.append("</param>");

        resultXml = removeMembers(pContainerId, taskParam.toString());
        lmdpContainer = getLastModificationDateValue(getDocument(resultXml));

        // check container version status
        rXml = retrieve(pContainerId);
        assertXmlExists("Wrong pulic-status", rXml, "/container/properties/public-status[text() = 'released']");
        // FIXME comment in if issue INFR-1471 is fixed
        // assertXmlExists("Wrong version status", rXml, "/container/properties/version/status[text() = 'pending']");
        assertXmlExists("Wrong version number", rXml, "/container/properties/version/number[text() = '4']");

        // TODO assert Members
        assertXmlExists("Wrong number of members", rXml, "/container/struct-map[count(./container) = '9']");

        // submit Container
        lmdpContainer = prepareContainerPid(pContainerId, lmdpContainer);
        // FIXME comment in if issue INFR-1471 is fixed
        // param = getTheLastModificationParam(true, pContainerId, String.valueOf(System.nanoTime()), lmdpContainer);
        // resultXml = submit(pContainerId, param);
        // lmdpContainer = getLastModificationDateValue(getDocument(resultXml));

        // release the Container
        param = getTheLastModificationParam(true, pContainerId, String.valueOf(System.nanoTime()), lmdpContainer);
        release(pContainerId, param);

        // TODO assert Members
        rXml = retrieve(pContainerId);
        assertXmlExists("Wrong pulic-status", rXml, "/container/properties/public-status[text() = 'released']");
        assertXmlExists("Wrong version status", rXml, "/container/properties/version/status[text() = 'released']");
        assertXmlExists("Wrong version number", rXml, "/container/properties/version/number[text() = '4']");
        assertXmlExists("Wrong number of members", rXml, "/container/struct-map[count(./container) = '9']");
    }

    /**
     * Test declining delete of item which is member because of insufficient rights to modify parent container.
     *
     * @throws Exception If an error occurs.
     */
    @Test
    public void testDeclineDeleteMemberItem01() throws Exception {

        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        String theItemId = createItemFromTemplate("escidoc_item_198_for_create.xml");
        String theContainerId = createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        String xmlData = getContainerTemplate("create_container_v1.1-forItemAndforContainer.xml");

        String replaced = xmlData.replaceAll("##ITEMID##", theItemId);
        replaced = replaced.replaceAll("##CONTAINERID##", theContainerId);
        assertXmlValidContainer(replaced);
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        create(replaced);

        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        Class ec = AuthorizationException.class;
        try {
            getItemClient().delete(theItemId);
            fail("Delete of member must fail without update rights to container.");
        }
        catch (final Exception e) {
            assertExceptionType(ec, e);
        }
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
    }
}
