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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.escidoc.core.test.EscidocRestSoapTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.test.common.client.servlet.Constants;

/**
 * Test the mock implementation of the container resource.
 * 
 * @author MSC
 * 
 */
@RunWith(value = Parameterized.class)
public class ContainerRetrieveTest extends ContainerTestBase {
    
    public static final String XPATH_SRW_CONTAINER_LIST_MEMBER =
        XPATH_SRW_RESPONSE_RECORD + "/recordData";

    public static final String XPATH_SRW_CONTAINER_LIST_CONTAINER =
        XPATH_SRW_CONTAINER_LIST_MEMBER + "/" + NAME_CONTAINER;

    private String theContainerXml;

    protected String theContainerId;

    private String theItemId;

    private String path = TEMPLATE_CONTAINER_PATH;

    /**
     * @param transport
     *            The transport identifier.
     */
    public ContainerRetrieveTest(final int transport) {
        super(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.path += "/" + getTransport(false);

        this.theItemId = createItem();
        String xmlData =
            EscidocRestSoapTestBase.getTemplateAsString(this.path,
                "create_container_v1.1-forItem.xml");

        theContainerXml = create(xmlData.replaceAll("##ITEMID##", theItemId));
        this.theContainerId = getObjidValue(this.theContainerXml);
    }

    /**
     * Clean up after servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();

        try {
            delete(this.theContainerId);
        }
        catch (Exception e) {
            // do nothing
        }
    }

    /**
     * Retrieve members: correct input (success case).
     * 
     * @test.name Retrieve members: correct input (success case).
     * @test.id OM_RFLMC_1_2
     * @test.input Container ID, filter criteria for all members.
     * 
     * @test.expected The XML representation of the filtered list of members of
     *                the container.
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_RFLMC_1_2() throws Exception {

        List<String> smMembersList =
            getStructMapMembers(retrieve(theContainerId));
        String memberListXml =
            retrieveMembers(theContainerId, "<param><filter /></param>");
        List<String> mlMembersList = getMemberListMembers(memberListXml, false);

        assertListContentEqual(
            "Member list does not contain the same IDs as struct map.",
            mlMembersList, smMembersList);

        assertXmlValidContainerMembersList(memberListXml);
    }

    /**
     * Retrieve members: correct input (success case).
     * 
     * @test.name Retrieve members: correct input (success case).
     * @test.id OM_RFLMC_1_2
     * @test.input Container ID, filter criteria for all members.
     * 
     * @test.expected The XML representation of the filtered list of members of
     *                the container.
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_RFLMC_1_2CQL() throws Exception {

        List<String> smMembersList =
            getStructMapMembers(retrieve(theContainerId));
        String memberListXml =
            retrieveMembers(theContainerId, new HashMap<String, String[]>());
        List<String> mlMembersList = getMemberListMembers(memberListXml, true);

        assertListContentEqual(
            "Member list does not contain the same IDs as struct map.",
            mlMembersList, smMembersList);
        assertXmlValidSrwResponse(memberListXml);
    }

    /**
     * Retrieve members: nonexisting container id.
     * 
     * @test.name Retrieve members: nonexisting container id.
     * @test.id OM_RFLMC_2
     * @test.input Nonexisting Container ID, filter criteria for all members.
     * 
     * @test.expected Error message with reason for failure.
     * 
     * @test.status Not Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_RFLMC_2() throws Exception {
        try {
            retrieveMembers("escidoc:nonexist1", "<param><filter /></param>");
            fail("No exception on retrieve members of nonexisting container.");
        }
        catch (Exception e) {
            Class<?> ec = ContainerNotFoundException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected on retrieve members.", ec, e);
        }
    }

    /**
     * Retrieve members: nonexisting container id.
     * 
     * @test.name Retrieve members: nonexisting container id.
     * @test.id OM_RFLMC_2
     * @test.input Nonexisting Container ID, filter criteria for all members.
     * 
     * @test.expected Error message with reason for failure.
     * 
     * @test.status Not Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_RFLMC_2CQL() throws Exception {
        try {
            retrieveMembers("escidoc:nonexist1",
                new HashMap<String, String[]>());
            fail("No exception on retrieve members of nonexisting container.");
        }
        catch (Exception e) {
            Class<?> ec = ContainerNotFoundException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected on retrieve members.", ec, e);
        }
    }

    /**
     * Retrieve members: incorrect filter criteria.
     * 
     * @test.name Retrieve members: incorrect filter criteria.
     * @test.id OM_RFLMC_3
     * @test.input Container ID, incorrect filter criteria for all members.
     * 
     * @test.expected Error message with reason for failure.
     * 
     * @test.status Not Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_RFLMC_3() throws Exception {
        try {
            retrieveMembers(theContainerId, "<param><failure /></param>");
            fail("No exception on retrieve members with wrong filter param.");
        }
        catch (Exception e) {
            Class< ? > ec = XmlSchemaValidationException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected on retrieve members.", ec, e);
        }
    }

    @Test
    public void testOM_RFLMC_3_1() throws Exception {
        final Class<?> ec = MissingMethodParameterException.class;
        try {
            retrieveMembers(theContainerId, "");
            EscidocRestSoapTestBase
                .failMissingException(
                    "No exception on retrieve members with wrong filter param.",
                    ec);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                "Wrong exception from retrieve mebers. ", ec, e);
        }
    }

    @Test
    public void testOM_RFLMC_3_2() throws Exception {
        try {
            retrieveMembers(theContainerId, "<param><failure");
            fail("No exception on retrieve members with wrong filter param.");
        }
        catch (Exception e) {
            Class<?> ec = XmlCorruptedException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected on retrieve members.", ec, e);
        }
    }

    /**
     * Retrieve members: container id not provided.
     * 
     * @test.name Retrieve members: container id not provided.
     * @test.id OM_RFLMC_4_1
     * @test.input No Container ID (null), filter criteria for all members.
     * 
     * @test.expected Error message with reason for failure.
     * 
     * @test.status Not Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_RFLMC_4_1() throws Exception {
        try {
            retrieveMembers(null, "<param><filter /></param>");
            fail("No exception on retrieve members with container id = null.");
        }
        catch (Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected on retrieve members.", ec, e);
        }
    }

    /**
     * Retrieve members: container id not provided.
     * 
     * @test.name Retrieve members: container id not provided.
     * @test.id OM_RFLMC_4_1
     * @test.input No Container ID (null), filter criteria for all members.
     * 
     * @test.expected Error message with reason for failure.
     * 
     * @test.status Not Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_RFLMC_4_1CQL() throws Exception {
        try {
            retrieveMembers(null, new HashMap<String, String[]>());
            fail("No exception on retrieve members with container id = null.");
        }
        catch (Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected on retrieve members.", ec, e);
        }
    }

    @Test
    public void testCompareRetrieveContainerByTwoMethods_IssueINFR_657()
        throws Exception {
        String container1Xml = retrieve(theContainerId);
        String container2Xml =
            retrieveContainers("<param>"
                + "<filter name=\"http://purl.org/dc/elements/1.1/identifier\">"
                + "<id>" + theContainerId + "</id>" + "</filter></param>");
        Document container1Document = getDocument(container1Xml);
        Document container2Document = getDocument(container2Xml);
        String lmdInContainer1 =
            selectSingleNode(container1Document,
                "/container/@last-modification-date").getNodeValue();
        String versionDateInContainer1 =
            selectSingleNode(container1Document,
                "/container/properties/version/date").getTextContent();
        assertEquals("last modification date and version date are not equal",
            versionDateInContainer1, lmdInContainer1);
        String lmdInContainer2 =
            selectSingleNode(container2Document,
                "/container-list/container/@last-modification-date")
                .getNodeValue();
        String versionDateInContainer2 =
            selectSingleNode(container2Document,
                "/container-list/container/properties/version/date")
                .getTextContent();
        assertEquals("last modification date and version date are not equal",
            versionDateInContainer2, lmdInContainer2);

    }

    /**
     * Retrieve members: filter criteria not provided.
     * 
     * @test.name Retrieve members: filter criteria not provided.
     * @test.id OM_RFLMC_4_2
     * @test.input Container ID, No filter criteria for all members.
     * 
     * @test.expected Error message with reason for failure.
     * 
     * @test.status Not Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_RFLMC_4_2() throws Exception {
        try {
            retrieveMembers(theContainerId, (String) null);
            fail("No exception on retrieve members with param = null.");
        }
        catch (Exception e) {
            Class<?> ec = MissingMethodParameterException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected on retrieve members.", ec, e);
        }
    }

    /**
     * Test sucessfully retriving of container.
     * 
     * @throws Exception
     */
    @Test
    public void testOM_RC_1_1() throws Exception {
        String containerXml = retrieve(theContainerId);

        assertXmlValidContainer(containerXml);
    }

    /**
     * Test declining retrieving of container with non existing id.
     * 
     * @throws Exception
     */
    @Test
    public void testOM_RC_2() throws Exception {

        try {
            retrieve("bla");
            fail("No exception occurred on retrieve with non existing id.");
        }
        catch (Exception e) {
            Class<?> ec = ContainerNotFoundException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * Test declining retrieving of container with missing id.
     * 
     * @throws Exception
     */
    @Test
    public void testOM_RC_3() throws Exception {

        try {
            retrieve(null);
            fail("No exception occurred on retrieve with missing id.");
        }
        catch (Exception e) {
            // Class<?> ec = ContainerNotFoundException.class;
            Class<?> ec = MissingMethodParameterException.class;
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * Test retrieving all Containers from the repository. The list of
     * Containers is afterwards checked with retrieving each Container
     * separately. Note: This test checks not if one of the Containers is
     * missing in the List!
     * 
     * @throws Exception
     *             If one of the Container in the list is not retrievable.
     */
    @Test
    public void testRetrieveContainers() throws Exception {

        String xml = retrieveContainers("<param><filter /></param>");
        assertXmlValidContainerList(xml);

        NodeList nodes = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            nodes =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    "/container-list/container/@href");
        }
        else {
            nodes =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    "/container-list/container/@objid");
        }
        assertContainers(nodes);
    }

    /**
     * Test retrieving all Containers from the repository. The list of
     * Containers is afterwards checked with retrieving each Container
     * separately. Note: This test checks not if one of the Containers is
     * missing in the List!
     * 
     * @throws Exception
     *             If one of the Container in the list is not retrievable.
     */
    @Test
    public void testRetrieveContainersCQL() throws Exception {

        String xml = retrieveContainers(new HashMap<String, String[]>());

        assertXmlValidSrwResponse(xml);

        NodeList nodes = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            nodes =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    XPATH_SRW_CONTAINER_LIST_CONTAINER + "/@href");
        }
        else {
            nodes =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    XPATH_SRW_CONTAINER_LIST_CONTAINER + "/@objid");
        }
        assertContainers(nodes);
    }

    @Test
    public void testRetrievePendingContainers() throws Exception {
        doTestFilterContainersStatus("pending", false, false);
        doTestFilterContainersStatus("pending", false, true);
    }

    @Test
    public void testRetrievePendingVersionContainers() throws Exception {
        doTestFilterContainersStatus("pending", true, false);
        doTestFilterContainersStatus("pending", true, true);
    }

    @Test
    public void testRetrievePendingContainerRefs() throws Exception {
        doTestFilterContainersStatus("pending", true, false);
        doTestFilterContainersStatus("pending", true, true);
    }

    @Test
    public void testRetrieveSubmittedContainers() throws Exception {
        doTestFilterContainersStatus("submitted", false, false);
        doTestFilterContainersStatus("submitted", false, true);
    }

    @Test
    public void testRetrieveSubmittedVersionContainers() throws Exception {
        doTestFilterContainersStatus("submitted", true, false);
        doTestFilterContainersStatus("submitted", true, true);
    }

    @Test
    public void testRetrieveSubmittedContainerRefs() throws Exception {
        doTestFilterContainersStatus("submitted", true, false);
        doTestFilterContainersStatus("submitted", true, true);
    }

    @Test
    public void testRetrieveMembers() throws Exception {
        // make list from containers struct map
        Document container =
            EscidocRestSoapTestBase.getDocument(retrieve(theContainerId));
        NodeList smMembers = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            smMembers =
                selectNodeList(container, "/container/struct-map/*/@href");
        }
        else {
            smMembers =
                selectNodeList(container, "/container/struct-map/*/@objid");
        }

        // make list from containers member list
        String xml =
            retrieveMembers(theContainerId, "<param><filter /></param>");
        NodeList mlMembers = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            mlMembers =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    "/member-list/*/@href");
        }
        else {
            mlMembers =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    "/member-list/*/@objid");
        }

        List<String> smMembersList = nodeList2List(smMembers);
        List<String> mlMembersList = nodeList2List(mlMembers);

        assertListContentEqual(
            "Member list does not contain the same IDs as struct map.",
            mlMembersList, smMembersList);

        assertXmlValidContainerMembersList(xml);
    }

    @Test
    public void testRetrieveMembersCQL() throws Exception {
        // make list from containers struct map
        Document container =
            EscidocRestSoapTestBase.getDocument(retrieve(theContainerId));
        NodeList smMembers = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            smMembers =
                selectNodeList(container, "/container/struct-map/*/@href");
        }
        else {
            smMembers =
                selectNodeList(container, "/container/struct-map/*/@objid");
        }

        // make list from containers member list
        String xml =
            retrieveMembers(theContainerId, new HashMap<String, String[]>());
        NodeList mlMembers = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            mlMembers =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    XPATH_SRW_CONTAINER_LIST_MEMBER + "/*/@href");
        }
        else {
            mlMembers =
                selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                    XPATH_SRW_CONTAINER_LIST_MEMBER + "/*/@objid");
        }

        List<String> smMembersList = nodeList2List(smMembers);
        List<String> mlMembersList = nodeList2List(mlMembers);

        assertListContentEqual(
            "Member list does not contain the same IDs as struct map.",
            mlMembersList, smMembersList);
        assertXmlValidSrwResponse(xml);
    }

    @Test
    public void testRetrievePendingMembers() throws Exception {
        String xml =
            retrieveMembers(theContainerId, "<param><filter name=\""
                + PROPERTIES_NS_URI_04
                + "public-status\">pending</filter></param>");
        assertXmlValidContainerMembersList(xml);

        Document xmlDoc = EscidocRestSoapTestBase.getDocument(xml);
        NodeList memberIds = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            memberIds = selectNodeList(xmlDoc, "/member-list/*/@href");
        }
        else {
            memberIds = selectNodeList(xmlDoc, "/member-list/*/@objid");
        }

        for (int i = memberIds.getLength() - 1; i >= 0; i--) {
            String id = memberIds.item(i).getNodeValue();
            if (Constants.TRANSPORT_REST == getTransport()) {

                selectSingleNodeAsserted(xmlDoc, "/member-list/*[@href = '"
                    + id + "']/properties/public-status[text() = 'pending']");
            }
            else {
                selectSingleNodeAsserted(xmlDoc, "/member-list/*[@objid = '"
                    + id + "']/properties/public-status[text() = 'pending']");
            }
        }
    }

    @Test
    public void testRetrievePendingMembersCQL() throws Exception {
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_PUBLIC_STATUS + "\"=pending" });

        String xml = retrieveMembers(theContainerId, filterParams);

        assertXmlValidSrwResponse(xml);

        Document xmlDoc = EscidocRestSoapTestBase.getDocument(xml);
        NodeList memberIds = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            memberIds =
                selectNodeList(xmlDoc, XPATH_SRW_CONTAINER_LIST_MEMBER
                    + "/*/@href");
        }
        else {
            memberIds =
                selectNodeList(xmlDoc, XPATH_SRW_CONTAINER_LIST_MEMBER
                    + "/*/@objid");
        }

        for (int i = memberIds.getLength() - 1; i >= 0; i--) {
            String id = memberIds.item(i).getNodeValue();
            if (Constants.TRANSPORT_REST == getTransport()) {

                selectSingleNodeAsserted(xmlDoc,
                    XPATH_SRW_CONTAINER_LIST_MEMBER + "/*[@href = '" + id
                        + "']/properties/public-status[text() = 'pending']");
            }
            else {
                selectSingleNodeAsserted(xmlDoc,
                    XPATH_SRW_CONTAINER_LIST_MEMBER + "/*[@objid = '" + id
                        + "']/properties/public-status[text() = 'pending']");
            }
        }
    }

    @Test
    public void testRetrieveSubmittedItemMembers() throws Exception {
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_PUBLIC_STATUS + "\"=submitted and " + "\""
            + RDF_TYPE_NS_URI + "\"=\"" + RESOURCES_NS_URI + "Item\"" });

        String xml = retrieveMembers(theContainerId, filterParams);

        assertXmlValidSrwResponse(xml);

        Document xmlDoc = EscidocRestSoapTestBase.getDocument(xml);
        NodeList memberIds = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            memberIds =
                selectNodeList(xmlDoc, XPATH_SRW_CONTAINER_LIST_MEMBER
                    + "/*/@href");
        }
        else {
            memberIds =
                selectNodeList(xmlDoc, XPATH_SRW_CONTAINER_LIST_MEMBER
                    + "/*/@objid");
        }

        for (int i = memberIds.getLength() - 1; i >= 0; i--) {
            String id = memberIds.item(i).getNodeValue();
            if (Constants.TRANSPORT_REST == getTransport()) {

                selectSingleNodeAsserted(xmlDoc, "/member-list/*[@href = '"
                    + id + "']/properties/public-status[text() = 'submitted']");
            }
            else {
                selectSingleNodeAsserted(xmlDoc, "/member-list/*[@objid = '"
                    + id + "']/properties/public-status[text() = 'submitted']");
            }
        }
    }

    @Test
    public void testRetrieveSubmittedItemMembersCQL() throws Exception {
        String xml =
            retrieveMembers(theContainerId, "<param><filter name=\""
                + PROPERTIES_NS_URI_04
                + "public-status\">submitted</filter><filter name=\""
                + RDF_TYPE_NS_URI + "\">" + RESOURCES_NS_URI
                + "Item</filter></param>");

        assertXmlValidContainerMembersList(xml);

        Document xmlDoc = EscidocRestSoapTestBase.getDocument(xml);
        NodeList memberIds = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            memberIds = selectNodeList(xmlDoc, "/member-list/*/@href");
        }
        else {
            memberIds = selectNodeList(xmlDoc, "/member-list/*/@objid");
        }

        for (int i = memberIds.getLength() - 1; i >= 0; i--) {
            String id = memberIds.item(i).getNodeValue();
            if (Constants.TRANSPORT_REST == getTransport()) {

                selectSingleNodeAsserted(xmlDoc, "/member-list/*[@href = '"
                    + id + "']/properties/public-status[text() = 'submitted']");
            }
            else {
                selectSingleNodeAsserted(xmlDoc, "/member-list/*[@objid = '"
                    + id + "']/properties/public-status[text() = 'submitted']");
            }
        }
    }

    @Test
    public void testRetrievePendingContainerMembers() throws Exception {
        String xml =
            retrieveMembers(theContainerId, "<param><filter name=\""
                + PROPERTIES_NS_URI_04
                + "public-status\">pending</filter><filter name=\""
                + RDF_TYPE_NS_URI + "\">" + RESOURCES_NS_URI
                + "Container</filter></param>");

        assertXmlValidContainerMembersList(xml);

        Document xmlDoc = EscidocRestSoapTestBase.getDocument(xml);
        NodeList memberIds = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            memberIds = selectNodeList(xmlDoc, "/member-list/*/@objid");
        }
        else {
            memberIds = selectNodeList(xmlDoc, "/member-list/*/@objid");
        }
        for (int i = memberIds.getLength() - 1; i >= 0; i--) {
            String id = memberIds.item(i).getNodeValue();
            if (Constants.TRANSPORT_REST == getTransport()) {

                selectSingleNodeAsserted(xmlDoc,
                    "/member-list/container[@href = '" + id
                        + "']/properties/public-status[text() = 'pending']");
            }
            else {
                selectSingleNodeAsserted(xmlDoc,
                    "/member-list/container[@objid = '" + id
                        + "']/properties/public-status[text() = 'pending']");
            }
        }
    }

    @Test
    public void testRetrievePendingContainerMembersCQL() throws Exception {
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_QUERY, new String[] { "\""
            + FILTER_PUBLIC_STATUS + "\"=pending and " + "\"" + RDF_TYPE_NS_URI
            + "\"=\"" + RESOURCES_NS_URI + "Container\"" });

        String xml = retrieveMembers(theContainerId, filterParams);

        assertXmlValidSrwResponse(xml);

        Document xmlDoc = EscidocRestSoapTestBase.getDocument(xml);
        NodeList memberIds = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            memberIds =
                selectNodeList(xmlDoc, XPATH_SRW_CONTAINER_LIST_MEMBER
                    + "/*/@objid");
        }
        else {
            memberIds =
                selectNodeList(xmlDoc, XPATH_SRW_CONTAINER_LIST_MEMBER
                    + "/*/@objid");
        }
        for (int i = memberIds.getLength() - 1; i >= 0; i--) {
            String id = memberIds.item(i).getNodeValue();
            if (Constants.TRANSPORT_REST == getTransport()) {

                selectSingleNodeAsserted(xmlDoc,
                    "/member-list/container[@href = '" + id
                        + "']/properties/public-status[text() = 'pending']");
            }
            else {
                selectSingleNodeAsserted(xmlDoc,
                    "/member-list/container[@objid = '" + id
                        + "']/properties/public-status[text() = 'pending']");
            }
        }
    }

    /**
     * Check if all Members of a Container are part of the retrieveMembers()
     * representation. See also Bug #638, where only the Items could be
     * retrieved and not Container.
     * 
     * @throws Exception
     *             e
     */
    @Test
    public void testRetrievingMembers() throws Exception {

        int maxContainer = 1;
        int maxItem = 1;

        // creating Container --------------------------------------------------
        String containerId =
            createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        // create multiple Resource (item/Container) ---------------------------
        Vector<String> ids = new Vector<String>();
        for (int i = 0; i < maxItem; i++) {
            String id =
                createItemFromTemplate("escidoc_item_198_for_create.xml");
            ids.add(id);
        }
        for (int i = 0; i < maxContainer; i++) {
            String id =
                createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
            ids.add(id);
        }

        Document containerDoc = getDocument(retrieve(containerId));
        String lmd = getLastModificationDateValue(containerDoc);

        String taskParam = createAddMemberTaskParam(ids, lmd);
        addMembers(containerId, taskParam);

        // check if retrieveMembers contains exactly the kind of objects -------
        String memberListXml =
            retrieveMembers(containerId, "<param><filter /></param>");
        List<String> members = getMemberListMembers(memberListXml, false);

        // converting hrefs to objids
        if (Constants.TRANSPORT_REST == getTransport()) {
            for (int i = 0; i < members.size(); i++) {
                String objid = getObjidFromHref(members.get(i));
                members.set(i, objid);
            }
        }

        // compare the members with ids
        for (int i = 0; i < ids.size(); i++) {
            assertTrue("Added Member '" + ids.get(i)
                + "' is not part of the member list of Container with objid '"
                + containerId + "'.", members.contains(ids.get(i)));
        }
    }

    /**
     * Check if all Members of a Container are part of the retrieveMembers()
     * representation. See also Bug #638, where only the Items could be
     * retrieved and not Container.
     * 
     * @throws Exception
     *             e
     */
    @Test
    public void testRetrievingMembersCQL() throws Exception {

        int maxContainer = 1;
        int maxItem = 1;

        // creating Container --------------------------------------------------
        String containerId =
            createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
        // create multiple Resource (item/Container) ---------------------------
        Vector<String> ids = new Vector<String>();
        for (int i = 0; i < maxItem; i++) {
            String id =
                createItemFromTemplate("escidoc_item_198_for_create.xml");
            ids.add(id);
        }
        for (int i = 0; i < maxContainer; i++) {
            String id =
                createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");
            ids.add(id);
        }

        Document containerDoc = getDocument(retrieve(containerId));
        String lmd = getLastModificationDateValue(containerDoc);

        String taskParam = createAddMemberTaskParam(ids, lmd);
        addMembers(containerId, taskParam);

        // check if retrieveMembers contains exactly the kind of objects -------
        String memberListXml =
            retrieveMembers(containerId, new HashMap<String, String[]>());
        List<String> members = getMemberListMembers(memberListXml, true);

        // converting hrefs to objids
        if (Constants.TRANSPORT_REST == getTransport()) {
            for (int i = 0; i < members.size(); i++) {
                String objid = getObjidFromHref(members.get(i));
                members.set(i, objid);
            }
        }

        // compare the members with ids
        for (int i = 0; i < ids.size(); i++) {
            if (!members.contains(ids.get(i))) {
                throw new Exception("Added Member '" + ids.get(i)
                    + "' not part of the member list.");
            }
        }
    }

    /**
     * Create a Container with whitespaces in md-records attribute name. This
     * has to be fail with a schema exception.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_MdRecords() throws Exception {

        Class<?> ec = XmlSchemaValidationException.class;

        String nameWS = "MD-Records Descriptor Name with whitespaces";

        Document context =
            EscidocRestSoapTestBase.getTemplateAsDocument(this.path,
                "create_container.xml");
        substitute(context, "/container/md-records/md-record[2]/@name", nameWS);
        String template = toString(context, false);

        try {
            create(template);
            fail(ec + " expected but no error occurred!");
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * Create a Container with more than the allowed number of characters in
     * md-records attribute name. The length is limited to 64 charachter. This
     * has to be fail with a schema exception.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOM_MdRecords2() throws Exception {

        Class<?> ec = XmlSchemaValidationException.class;

        String nameLong =
            "MD-Records_Attribute_Name_without_whitespaces_but_"
                + "extra_long_to_reach_the_64_character_limit_of_fedora_other"
                + "_things_are_not_tested";

        Document context =
            EscidocRestSoapTestBase.getTemplateAsDocument(this.path,
                "create_container.xml");
        substitute(context, "/container/md-records/md-record[2]/@name",
            nameLong);
        String template = toString(context, false);

        try {
            create(template);
            fail(ec + " expected but no error occurred!");
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * Test retrieving struct-map of a container.
     * 
     * Bugzilla #585
     * (http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=585)
     * 
     * @throws Exception
     *             Thrown if retrieving fails.
     */
    @Test
    public void testRetrievingStructMap() throws Exception {

        String containerXml = retrieve(theContainerId);

        assertXmlExists("struct-map", containerXml, "/container/struct-map");

        String structMap = retrieveStructMap(theContainerId);
        // assertXmlStructMap(structMap);
    }

    /***************************************************************************
     * private methods
     * **********************************************************************
     */

    /**
     * Prepare the TaskParam for addMember.
     * 
     * @param members
     *            Vector with id of member candidates.
     * @param lastModificationDate
     *            The last modification date of the resource (Container).
     * @return TaskParam
     */
    private String createAddMemberTaskParam(
        final Vector<String> members, final String lastModificationDate) {

        String taskParam =
            "<param last-modification-date=\"" + lastModificationDate + "\" ";
        taskParam += ">\n";

        for (int i = 0; i < members.size(); i++) {
            taskParam += "<id>" + members.get(i) + "</id>\n";
        }
        taskParam += "</param>";

        return taskParam;
    }

    /**
     * Get the ids of the StructMap members.
     * 
     * @param xml
     * @return
     * @throws Exception
     */
    private List<String> getStructMapMembers(final String xml) throws Exception {

        Document container = EscidocRestSoapTestBase.getDocument(xml);

        NodeList smMembers = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            smMembers =
                selectNodeList(container, "/container/struct-map/*/@href");
        }
        else {
            smMembers =
                selectNodeList(container, "/container/struct-map/*/@objid");
        }

        return nodeList2List(smMembers);
    }

    /**
     * Get the ids of the memberList members.
     * 
     * @param xml
     *            XML escidoc member-list.
     * @return Objids exctracted from the member list.
     * @throws Exception
     */
    private List<String> getMemberListMembers(
        final String xml, final boolean srw) throws Exception {

        // make list from containers member list
        NodeList mlMembers = null;
        if (Constants.TRANSPORT_REST == getTransport()) {
            if (srw) {
                mlMembers =
                    selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                        XPATH_SRW_CONTAINER_LIST_MEMBER + "/*/@href");
            }
            else {
                mlMembers =
                    selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                        "/member-list/*/@href");
            }
        }
        else {
            if (srw) {
                mlMembers =
                    selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                        XPATH_SRW_CONTAINER_LIST_MEMBER + "/*/@objid");
            }
            else {
                mlMembers =
                    selectNodeList(EscidocRestSoapTestBase.getDocument(xml),
                        "/member-list/*/@objid");
            }
        }

        return nodeList2List(mlMembers);
    }

    private void doTestFilterContainersStatus(
        final String reqStatus, final boolean versionStatus, final boolean srw)
        throws Exception {

        String filterName = FILTER_PUBLIC_STATUS;
        String filterResultXPath = "/container/properties/public-status/text()";
        if (versionStatus) {
            filterName = "" + FILTER_VERSION_STATUS;
            filterResultXPath = "/container/properties/version/status/text()";
        }

        String list = null;

        if (srw) {
            final Map<String, String[]> filterParams =
                new HashMap<String, String[]>();
            StringBuffer filter =
                new StringBuffer("\"" + filterName + "\"=" + reqStatus);

            if (versionStatus) {
                filter.append(" and " + "\"" + FILTER_PUBLIC_STATUS
                    + "\"=released");
            }
            filterParams.put(FILTER_PARAMETER_QUERY, new String[] { filter
                .toString() });
            list = retrieveContainers(filterParams);
            assertXmlValidSrwResponse(list);
        }
        else {
            String filterXml =
                "<param>" + "<filter name=\"" + filterName + "\">" + reqStatus
                    + "</filter>";
            if (versionStatus) {
                filterXml +=
                    "<filter name=\"" + FILTER_PUBLIC_STATUS
                        + "\">released</filter>";
            }
            filterXml += "</param>";
            list = retrieveContainers(filterXml);
            assertXmlValidContainerList(list);
        }

        NodeList nodes = null;

        if (getTransport() == Constants.TRANSPORT_REST) {
            if (srw) {
                nodes =
                    selectNodeList(EscidocRestSoapTestBase.getDocument(list),
                        XPATH_SRW_CONTAINER_LIST_CONTAINER + "/@href");
            }
            else {
                nodes =
                    selectNodeList(EscidocRestSoapTestBase.getDocument(list),
                        "/container-list/container/@href");
            }
        }
        else if (getTransport() == Constants.TRANSPORT_SOAP) {
            if (srw) {
                nodes =
                    selectNodeList(EscidocRestSoapTestBase.getDocument(list),
                        XPATH_SRW_CONTAINER_LIST_CONTAINER + "/@objid");
            }
            else {
                nodes =
                    selectNodeList(EscidocRestSoapTestBase.getDocument(list),
                        "/container-list/container/@objid");
            }
        }

        for (int count = nodes.getLength() - 1; count >= 0; count--) {
            Node node = nodes.item(count);
            String nodeValue = null;
            if (getTransport() == Constants.TRANSPORT_REST) {
                nodeValue = getIdFromHrefValue(node.getNodeValue());
            }
            else {
                nodeValue = node.getNodeValue();
            }

            try {
                String container = retrieve(nodeValue);
                String containerStatus =
                    selectSingleNode(
                        EscidocRestSoapTestBase.getDocument(container),
                        filterResultXPath).getNodeValue();
                assertEquals(reqStatus, containerStatus);
            }
            catch (ContainerNotFoundException e) {
                if (reqStatus.equals(STATUS_WITHDRAWN)) {
                    EscidocRestSoapTestBase.assertExceptionType(
                        ItemNotFoundException.class, e);
                }
                else {
                    fail("No container could be retrieved with id " + nodeValue
                        + " returned by retrieveContainerRefs.");
                }
            }

        }
    }

    private void assertContainers(NodeList nodes) throws Exception {
        if (nodes.getLength() == 0) {
            fail("No containers found.");
        }

        for (int count = nodes.getLength() - 1; count >= 0; count--) {
            Node node = nodes.item(count);
            String nodeValue = node.getNodeValue();
            try {

                if (Constants.TRANSPORT_REST == getTransport()) {
                    nodeValue = getIdFromURI(nodeValue);
                }
                String container = retrieve(nodeValue);
                assertXmlValidContainer(container);
            }
            catch (de.escidoc.core.common.exceptions.remote.system.FedoraSystemException e) {
                throw e;
            }
            catch (Exception e) {
                throw e;
            }
        }
    }

    /**
     * Create Item from template.
     * 
     * @return objid of Item.
     * @throws Exception
     *             Thrown if creation or id extraction failed.
     */
    private String createItem() throws Exception {

        // create an item and save the id
        String xmlData =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "escidoc_item_198_for_create.xml");

        String theItemXml = handleXmlResult(getItemClient().create(xmlData));
        return getObjidValue(theItemXml);
    }

    private void doTestFilterMembersUserRole(
        final String id, final String reqUser, final String reqRole)
        throws Exception {

        String filterXml = "<param>";
        if (reqUser != null) {
            filterXml += "<filter name=\"user\">" + reqUser + "</filter>";
        }
        if (reqRole != null) {
            filterXml += "<filter name=\"role\">" + reqRole + "</filter>";
        }
        filterXml += "</param>";

        NodeList items = null;
        NodeList containers = null;

        String list = retrieveMembers(id, filterXml);
        if (Constants.TRANSPORT_REST == getTransport()) {
            items =
                selectNodeList(EscidocRestSoapTestBase.getDocument(list),
                    "/member-list/member/item/@href");
            containers =
                selectNodeList(EscidocRestSoapTestBase.getDocument(list),
                    "/member-list/member/container/@href");
        }
        else {
            items =
                selectNodeList(EscidocRestSoapTestBase.getDocument(list),
                    "/member-list/member/item/@objid");
            containers =
                selectNodeList(EscidocRestSoapTestBase.getDocument(list),
                    "/member-list/member/container/@objid");
        }

        for (int count = containers.getLength() - 1; count >= 0; count--) {
            Node node = containers.item(count);
            String nodeValue = node.getNodeValue();
            if (Constants.TRANSPORT_REST == getTransport()) {
                nodeValue = getIdFromURI(nodeValue);
            }
            try {
                retrieve(nodeValue);
            }
            catch (ContainerNotFoundException e) {
                throw e;
            }

        }
        for (int count = items.getLength() - 1; count >= 0; count--) {
            Node node = items.item(count);
            String nodeValue = node.getNodeValue();
            if (Constants.TRANSPORT_REST == getTransport()) {
                nodeValue = getIdFromURI(nodeValue);
            }
            try {
                handleXmlResult(getItemClient().retrieve(nodeValue));

            }
            catch (ItemNotFoundException e) {
                throw e;
            }

        }
    }

    /**
     * 
     * @param memberIds
     * @return
     */
    private String getMemberRefList(List<String> memberIds) {

        String result =
            "<member-ref-list:member-ref-list><member-ref-list:member>"
                + "<member-ref-list:item-ref  " + XLINK_TYPE_ESCIDOC
                + "=\"simple\" " + XLINK_HREF_ESCIDOC + "=\"/ir/item/"
                + theItemId + "\" objid=\"" + theItemId + "\"/>";

        if (memberIds != null) {
            // FIXME this methods does nothing useful
            Iterator<String> it = memberIds.iterator();
            while (it.hasNext()) {
                String id = it.next();
            }
        }

        result +=
            "</member-ref-list:member>" + "</member-ref-list:member-ref-list>";

        return result;
    }

    /**
     * Test successfully retrieving md-record.
     * 
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveMdRecord() throws Exception {
        retrieveMdRecord(true, "escidoc");
    }

    /**
     * Test decline retrieving md-record without container ID.
     * 
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveMdRecordWithoutItemID() throws Exception {
        Class ec = MissingMethodParameterException.class;
        String msg = "Expected " + ec.getName();
        try {
            retrieveMdRecord(false, "escidoc");
        }
        catch (Exception e) {
            assertExceptionType(msg, ec, e);
        }
    }

    /**
     * Test decline retrieving md-record with no name.
     * 
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveMdRecordWithoutName() throws Exception {
        Class ec = MissingMethodParameterException.class;
        String msg = "Expected " + ec.getName();
        try {
            retrieveMdRecord(true, null);
        }
        catch (Exception e) {
            assertExceptionType(msg, ec, e);
        }
    }

    /**
     * Test decline retrieving md-record with empty name.
     * 
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveMdRecordWithEmptyName() throws Exception {
        Class ec = MissingMethodParameterException.class;
        String msg = "Expected " + ec.getName();
        try {
            retrieveMdRecord(true, "");
        }
        catch (Exception e) {
            assertExceptionType(msg, ec, e);
        }
    }

    /**
     * Test successfully retrieving md-record with non existing name.
     * 
     * 
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveMdRecordNonExistingName() throws Exception {
        Class ec = MdRecordNotFoundException.class;
        String msg = "Expected " + ec.getName();
        try {
            retrieveMdRecord(true, "blablub");
        }
        catch (Exception e) {
            assertExceptionType(msg, ec, e);
        }
    }

    /**
     * Test successfully retrieving an explain response.
     * 
     * @test.name testExplainRetrieveContainers
     * @test.id testExplainRetrieveContainers
     * @test.input
     * @test.expected: valid explain response.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testExplainRetrieveContainers() throws Exception {
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_EXPLAIN, new String[] { "" });

        String result = null;

        try {
            result = retrieveContainers(filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }

    /**
     * Test successfully retrieving an explain response.
     * 
     * @test.name testExplainRetrieveMembers
     * @test.id testExplainRetrieveMembers
     * @test.input
     * @test.expected: valid explain response.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testExplainRetrieveMembers() throws Exception {
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_EXPLAIN, new String[] { "" });

        String result = null;

        try {
            result = retrieveMembers(theContainerId, filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }

    /**
     * Test successfully retrieving an explain response.
     * 
     * @test.name testExplainRetrieveTocs
     * @test.id testExplainRetrieveTocs
     * @test.input
     * @test.expected: valid explain response.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testExplainRetrieveTocs() throws Exception {
        final Map<String, String[]> filterParams =
            new HashMap<String, String[]>();

        filterParams.put(FILTER_PARAMETER_EXPLAIN, new String[] { "" });

        String result = null;

        try {
            result = retrieveTocs(theContainerId, filterParams);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase.failException(e);
        }
        assertXmlValidSrwResponse(result);
    }

    /**
     * Test if the objid is handles right.
     * 
     * see issue INFR-773
     * 
     * The tests creates an Container with one Item as Member and uses then on
     * the Item handler the objid of the Item with and without version suffix.
     * The framework has to answer with ContainerNotFoundException in all cases.
     * 
     * @throws Exception
     *             If framework behavior is not as expected.
     */
    @Test
    public void testWrongObjid01() throws Exception {

        // create container
        String containerTemplXml = getContainerTemplate("create_container.xml");
        containerTemplXml =
            containerTemplXml.replaceAll("escidoc:persistent3", "escidoc:ex1");
        String containerXml = create(containerTemplXml);
        String containerId = getObjidValue(containerXml);

        String itemTemplXml =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_ITEM_PATH
                + "/" + getTransport(false), "item_without_component.xml");

        String itemXml = createItem(containerId, itemTemplXml);
        String itemId = getObjidValue(itemXml);

        try {
            retrieve(itemId);
        }
        catch (Exception e) {
            assertExceptionType("Wrong exception",
                ContainerNotFoundException.class, e);
        }

        try {
            retrieve(itemId + ":1");
        }
        catch (Exception e) {
            assertExceptionType("Wrong exception",
                ContainerNotFoundException.class, e);
        }

        try {
            retrieve(itemId + ":a");
        }
        catch (Exception e) {
            assertExceptionType("Wrong exception",
                ContainerNotFoundException.class, e);
        }
    }

    /**
     * Creates an Item and retrieves the md-record by given name.
     * 
     * @param resourceId
     *            If the retrieve should be done with resource ID.
     * @param name
     *            The name of the md-record to be retrieved.
     * @throws Exception
     *             If an error occures.
     */
    private void retrieveMdRecord(final boolean resourceId, final String name)
        throws Exception {
        if (!resourceId) {
            this.theContainerId = null;
        }

        String retrievedMdRecord =
            retrieveMetadataRecord(this.theContainerId, name);
        assertCreatedMdRecord(name, this.theContainerId, "container",
            retrievedMdRecord, this.theContainerXml, startTimestamp);

    }

}