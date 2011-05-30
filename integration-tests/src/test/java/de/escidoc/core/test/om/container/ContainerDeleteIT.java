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
import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.fedora.TripleStoreTestBase;
import de.escidoc.core.test.security.client.PWCallback;
import org.junit.Test;
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
