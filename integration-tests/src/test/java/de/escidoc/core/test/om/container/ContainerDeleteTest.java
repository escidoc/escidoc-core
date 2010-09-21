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

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.NodeList;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.security.AuthorizationException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.fedora.TripleStoreTestsBase;
import de.escidoc.core.test.security.client.PWCallback;

/**
 * Test the mock implementation of the container resource.
 * 
 * @author MSC
 * 
 */
@RunWith(value = Parameterized.class)
public class ContainerDeleteTest extends ContainerTestBase {

    /**
     * @param transport
     *            The transport identifier.
     */
    public ContainerDeleteTest(final int transport) {
        super(transport);
    }

    /**
     * Test successfully delete of container in status 'pending'.
     * 
     * @throws Exception
     *             If an error occurs.
     */
    @Test
    public void testOM_DC_1() throws Exception {

        String containerId =
            createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        delete(containerId);

        try {
            retrieve(containerId);
            EscidocRestSoapTestsBase
                .failMissingException(ContainerNotFoundException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                ContainerNotFoundException.class, e);
        }
    }

    /**
     * Test successfully delete of container in status 'revised'.
     * 
     * @throws Exception
     *             If an error occurs.
     */
    @Test
    public void testOM_DC_7() throws Exception {

        String containerId =
            createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        String paramXml = getTheLastModificationParam(false, containerId);
        submit(containerId, paramXml);
        paramXml = getTheLastModificationParam(false, containerId);
        revise(containerId, paramXml);
        delete(containerId);

        try {
            retrieve(containerId);
            EscidocRestSoapTestsBase
                .failMissingException(ContainerNotFoundException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                ContainerNotFoundException.class, e);
        }
    }

    /**
     * Test declining delete of container which contains members.
     * 
     * @throws Exception
     *             If an error occurs.
     */
    @Test
    public void testOM_DC_5() throws Exception {

        String theItemId =
            createItemFromTemplate("escidoc_item_198_for_create.xml");
        String xmlData =
            getContainerTemplate("create_container_v1.1-forItem.xml");

        String replaced = xmlData.replaceAll("##ITEMID##", theItemId);
        assertXmlValidContainer(getTransport(), replaced);
        String theContainerXml = create(replaced);

        String theContainerId = getObjidValue(theContainerXml);
        try {
            delete(theContainerId);
            EscidocRestSoapTestsBase.failMissingException(
                "No exception with deleting container with members.",
                InvalidStatusException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                InvalidStatusException.class, e);
        }
    }

    /**
     * Test declining delete of container with non existing id.
     * 
     * @throws Exception
     *             If an error occurs.
     */
    @Test
    public void testOM_DC_2() throws Exception {

        try {
            delete("bla");
            EscidocRestSoapTestsBase.failMissingException(
                "No exception occurred on delete with non existing id.",
                ContainerNotFoundException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                ContainerNotFoundException.class, e);
        }
    }

    /**
     * Test declining delete of container with missing id.
     * 
     * @throws Exception
     *             If an error occurs.
     */
    @Test
    public void testOM_DC_3() throws Exception {

        try {
            delete(null);
            EscidocRestSoapTestsBase.failMissingException(
                "No exception occurred on delete with missing id.",
                MissingMethodParameterException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                MissingMethodParameterException.class, e);
        }
    }

    /**
     * Test declining delete of container in status submitted.
     * 
     * @throws Exception
     *             If an error occurs.
     */
    @Test
    public void testOM_DC_6() throws Exception {

        String containerId =
            createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        String param = getTheLastModificationParam(false, containerId);
        submit(containerId, param);
        try {
            delete(containerId);
            EscidocRestSoapTestsBase.failMissingException(
                "No exception occurred on delete submitted container.",
                InvalidStatusException.class);
        }
        catch (Exception e) {
            EscidocRestSoapTestsBase.assertExceptionType(
                InvalidStatusException.class, e);
        }
    }

    // Test methods are obsolete, because they test the obsolete
    // interface methods
    // /**
    // * Test successfully deleting containers, which are referenced as
    // relations
    // * targets in the other container. After deletion of referenced
    // containers,
    // * the container has no relations more.
    // *
    // *
    // * @test.status Implemented
    // *
    // * @throws Exception
    // * If anything fails.
    // */
    // public void testDeleteWithRelations() throws Exception {
    // String containerXml1 =
    // create(getTemplateAsString(TEMPLATE_CONTAINER_PATH,
    // "create_container_WithoutMembers_v1.1.xml"));
    // String containerXml2 =
    // create(getTemplateAsString(TEMPLATE_CONTAINER_PATH,
    // "create_container_WithoutMembers_v1.1.xml"));
    //
    // String createdContainerId1 = null;
    // String createdContainerId2 = null;
    // Pattern PATTERN_OBJID_ATTRIBUTE = Pattern.compile("objid=\"([^\"]*)\"");
    // Matcher m1 = PATTERN_OBJID_ATTRIBUTE.matcher(containerXml1);
    // if (m1.find()) {
    // createdContainerId1 = m1.group(1);
    // }
    // Matcher m2 = PATTERN_OBJID_ATTRIBUTE.matcher(containerXml2);
    // if (m2.find()) {
    // createdContainerId2 = m2.group(1);
    // }
    //
    // String href1 = "/ir/container/" + createdContainerId1;
    // String href2 = "/ir/container/" + createdContainerId2;
    // String containerForCreateWithRelationsXml = getTemplateAsString(
    // TEMPLATE_CONTAINER_PATH,
    // "create_container_WithoutMembers_v1.1_WithRelations.xml");
    //
    // containerForCreateWithRelationsXml = containerForCreateWithRelationsXml
    // .replaceAll("##CONTAINER_ID1##", createdContainerId1);
    // containerForCreateWithRelationsXml = containerForCreateWithRelationsXml
    // .replaceAll("##CONTAINER_ID2##", createdContainerId2);
    // containerForCreateWithRelationsXml = containerForCreateWithRelationsXml
    // .replaceAll("##CONTAINER_HREF1##", href1);
    // containerForCreateWithRelationsXml = containerForCreateWithRelationsXml
    // .replaceAll("##CONTAINER_HREF2##", href2);
    // Document containerForCreateWithRelations =
    // getDocument(containerForCreateWithRelationsXml);
    //        
    // String xml = create(containerForCreateWithRelationsXml);
    // NodeList relations = selectNodeList(containerForCreateWithRelations,
    // "/container/relations/relation");
    //
    //        
    // assertEquals("container relations number is wrong",
    // relations.getLength(), 2);
    //
    // Node containerObjiId = selectSingleNode(getDocument(xml),
    // "/container/@objid");
    //
    // String containerId = containerObjiId.getTextContent();
    //
    // delete(createdContainerId1);
    // delete(createdContainerId2);
    // String containerAfterDeleteOfrelations = retrieve(containerId);
    //        
    // NodeList relationsOfContainerAfterDeleteOfRelations = selectNodeList(
    // getDocument(containerAfterDeleteOfrelations),
    // "/container/relations/relation");
    // assertEquals("container relations number is wrong",
    // relationsOfContainerAfterDeleteOfRelations.getLength(), 0);
    //
    // }

    /**
     * Test successfully delete container which is member.
     * 
     * @throws Exception
     *             If an error occurs.
     */
    @Test
    public void testDeleteMemberContainer() throws Exception {

        String theItemId =
            createItemFromTemplate("escidoc_item_198_for_create.xml");
        String theContainerId =
            createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        String xmlData =
            getContainerTemplate("create_container_v1.1-forItemAndforContainer.xml");

        String replaced = xmlData.replaceAll("##ITEMID##", theItemId);
        replaced = replaced.replaceAll("##CONTAINERID##", theContainerId);
        assertXmlValidContainer(getTransport(), replaced);
        String theParentId = getObjidValue(create(replaced));

        delete(theContainerId);
        String parentAfterDeletion = retrieve(theParentId);
        // first check if removed from container struct-map, but that's already
        // the case if member does not longer exist
        NodeList containerMembers =
            selectNodeList(getDocument(parentAfterDeletion),
                "/container/members//container");
        assertEquals("Member entry should be deleted while deleting member.",
            0, containerMembers.getLength());
        // check if RELS-EXT does not longer contain the member
        String triplestoreEntry =
            new TripleStoreTestsBase()
                .requestMPT(
                    "<info:fedora/"
                        + theParentId
                        + " <http://escidoc.de/core/01/structural-relations/member> <info:fedora/"
                        + theContainerId + ">", TripleStoreTestsBase.FORMAT_MPT);
        assertEquals("Member entry should be deleted while deleting member.",
            0, triplestoreEntry.length());
    }

    /**
     * Test declining delete of container which is member because of
     * insufficient rights to modify parent container.
     * 
     * @throws Exception
     *             If an error occurs.
     */
    @Test
    public void testDeclineDeleteMemberContainer01() throws Exception {

        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        String theItemId =
            createItemFromTemplate("escidoc_item_198_for_create.xml");
        String theContainerId =
            createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        String xmlData =
            getContainerTemplate("create_container_v1.1-forItemAndforContainer.xml");

        String replaced = xmlData.replaceAll("##ITEMID##", theItemId);
        replaced = replaced.replaceAll("##CONTAINERID##", theContainerId);
        assertXmlValidContainer(getTransport(), replaced);
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        create(replaced);

        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        Class ec = AuthorizationException.class;
        try {
            delete(theContainerId);
            fail("Delete of member must fail without update rights to container.");
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
    }

    /**
     * Test successfully delete item which is member.
     * 
     * @throws Exception
     *             If an error occurs.
     */
    @Test
    public void testDeleteMemberItem() throws Exception {

        String theItemId =
            createItemFromTemplate("escidoc_item_198_for_create.xml");
        String theContainerId =
            createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        String xmlData =
            getContainerTemplate("create_container_v1.1-forItemAndforContainer.xml");

        String replaced = xmlData.replaceAll("##ITEMID##", theItemId);
        replaced = replaced.replaceAll("##CONTAINERID##", theContainerId);
        assertXmlValidContainer(getTransport(), replaced);
        String theParentId = getObjidValue(create(replaced));

        getItemClient().delete(theItemId);
        String parentAfterDeletion = retrieve(theParentId);
        // first check if removed from container struct-map, but that's already
        // the case if member does not longer exist
        NodeList containerMembers =
            selectNodeList(getDocument(parentAfterDeletion),
                "/container/struct-map//item");
        assertEquals("Member entry should be deleted while deleting member.",
            0, containerMembers.getLength());
        // check if RELS-EXT does not longer contain the member
        String triplestoreEntry =
            new TripleStoreTestsBase()
                .requestMPT(
                    "<info:fedora/"
                        + theParentId
                        + " <http://escidoc.de/core/01/structural-relations/member> <info:fedora/"
                        + theItemId + ">", TripleStoreTestsBase.FORMAT_MPT);
        assertEquals("Member entry should be deleted while deleting member.",
            0, triplestoreEntry.length());

    }

    /**
     * Test declining delete of item which is member because of insufficient
     * rights to modify parent container.
     * 
     * @throws Exception
     *             If an error occurs.
     */
    @Test
    public void testDeclineDeleteMemberItem01() throws Exception {

        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        String theItemId =
            createItemFromTemplate("escidoc_item_198_for_create.xml");
        String theContainerId =
            createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        String xmlData =
            getContainerTemplate("create_container_v1.1-forItemAndforContainer.xml");

        String replaced = xmlData.replaceAll("##ITEMID##", theItemId);
        replaced = replaced.replaceAll("##CONTAINERID##", theContainerId);
        assertXmlValidContainer(getTransport(), replaced);
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
        create(replaced);

        PWCallback.setHandle(PWCallback.DEPOSITOR_HANDLE);
        Class ec = AuthorizationException.class;
        try {
            getItemClient().delete(theItemId);
            fail("Delete of member must fail without update rights to container.");
        }
        catch (Exception e) {
            assertExceptionType(ec, e);
        }
        PWCallback.setHandle(PWCallback.DEFAULT_HANDLE);
    }
}
