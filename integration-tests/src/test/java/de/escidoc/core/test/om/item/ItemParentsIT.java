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
package de.escidoc.core.test.om.item;

import java.util.ArrayList;
import java.util.List;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.om.container.ContainerTestBase;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test retrieving parents of the Container resource.
 *
 * @author Michael Hoppe
 */
public class ItemParentsIT extends ItemTestBase {

    private static ContainerTestBase containerTestBase;

    private static String[] containerIds = new String[3];

    private static String[] itemIds = new String[4];

    private static int methodCounter = 0;

    public ItemParentsIT() {
        containerTestBase = new ContainerTestBase();
    }

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void initialize() throws Exception {
        if (methodCounter == 0) {
            prepare();
        }
    }

    /**
     * Clean up after servlet test.
     *
     * @throws Exception If anything fails.
     */
    @After
    public void deinitialize() throws Exception {
        methodCounter++;
        if (methodCounter == getTestAnnotationsCount()) {
        }
    }

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    private void prepare() throws Exception {
        String containerData = containerTestBase.getContainerTemplate("create_container.xml");
        String itemData = getItemTemplate("create_item_minimal.xml");

        String containerXml = null;
        String itemXml = null;
        DateTime[] containerLmds = new DateTime[3];

        for (int i = 0; i < 3; i++) {
            containerXml = containerTestBase.create(containerData);
            Document doc = getDocument(containerXml);
            containerIds[i] = getObjidValue(doc);
            containerLmds[i] = getLastModificationDateValue2(doc);
        }
        for (int i = 0; i < 4; i++) {
            itemXml = create(itemData);
            itemIds[i] = getObjidValue(itemXml);
        }

        List<String> ids = new ArrayList<String>();
        //Add members to containerIds[0]
        for (int i = 0; i < 2; i++) {
            ids.add(itemIds[i]);
        }
        containerTestBase.addMembers(containerIds[0], getMembersTaskParam(containerLmds[0], ids));

        //Add members to containerIds[1]
        ids = new ArrayList<String>();
        for (int i = 0; i < 3; i++) {
            ids.add(itemIds[i]);
        }
        containerTestBase.addMembers(containerIds[1], getMembersTaskParam(containerLmds[1], ids));

        //Add members to containerIds[2]
        ids = new ArrayList<String>();
        ids.add(itemIds[0]);
        containerTestBase.addMembers(containerIds[2], getMembersTaskParam(containerLmds[2], ids));
    }

    /**
     * Test successful retrieving parents of an item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveParents() throws Exception {
        String parentsXml = retrieveParents(itemIds[0]);
        assertXmlValidParents(parentsXml);
        Document parentsDoc = getDocument(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 3);
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='" + containerIds[0]
            + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/" + containerIds[0] + "']");
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='" + containerIds[1]
            + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/" + containerIds[1] + "']");
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='" + containerIds[2]
            + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/" + containerIds[2] + "']");
    }

    /**
     * Test successful retrieving parents of an item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveParents1() throws Exception {
        String parentsXml = retrieveParents(itemIds[1]);
        assertXmlValidParents(parentsXml);
        Document parentsDoc = getDocument(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 2);
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='" + containerIds[0]
            + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/" + containerIds[0] + "']");
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='" + containerIds[1]
            + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/" + containerIds[1] + "']");
        assertXmlNotExists("non-expected container found", parentsDoc, "/parents/parent[@objid='" + containerIds[2]
            + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/" + containerIds[2] + "']");
    }

    /**
     * Test successful retrieving parents of an item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveParents2() throws Exception {
        String parentsXml = retrieveParents(itemIds[2]);
        assertXmlValidParents(parentsXml);
        Document parentsDoc = getDocument(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 1);
        assertXmlNotExists("non-expected container found", parentsDoc, "/parents/parent[@objid='" + containerIds[0]
            + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/" + containerIds[0] + "']");
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='" + containerIds[1]
            + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/" + containerIds[1] + "']");
        assertXmlNotExists("non-expected container found", parentsDoc, "/parents/parent[@objid='" + containerIds[2]
            + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/" + containerIds[2] + "']");
    }

    /**
     * Test successful retrieving parents of an item.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveParents3() throws Exception {
        String parentsXml = retrieveParents(itemIds[3]);
        assertXmlValidParents(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 0);
    }

    /**
     * Test successful retrieving parents of an item with a version number.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveParentsWithVersion() throws Exception {
        String parentsXml = retrieveParents(itemIds[1] + ":1");
        assertXmlValidParents(parentsXml);
        Document parentsDoc = getDocument(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 2);
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='" + containerIds[0]
            + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/" + containerIds[0] + "']");
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='" + containerIds[1]
            + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/" + containerIds[1] + "']");
        assertXmlNotExists("non-expected container found", parentsDoc, "/parents/parent[@objid='" + containerIds[2]
            + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/" + containerIds[2] + "']");
    }

    /**
     * Test declining retrieving parents of an item with wrong itemId.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDecliningRetrieveParentsWithWrongId() throws Exception {
        try {
            retrieveParents("wrongId");
            EscidocAbstractTest.failMissingException(ItemNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ItemNotFoundException.class, e);
        }
    }

    /**
     * Test declining retrieving parents of an item with itemId null.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDecliningRetrieveParentsWithNoId() throws Exception {
        try {
            retrieveParents(null);
            EscidocAbstractTest.failMissingException(MissingMethodParameterException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(MissingMethodParameterException.class, e);
        }
    }

}
