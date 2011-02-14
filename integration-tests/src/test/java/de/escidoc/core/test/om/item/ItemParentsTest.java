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

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ItemNotFoundException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.om.container.ContainerTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;

/**
 * Test retrieving parents of the Container resource.
 * 
 * @author MIH
 * 
 */
@RunWith(value = Parameterized.class)
public class ItemParentsTest extends ItemTestBase {

    private static ContainerTestBase containerTestBase;
    
    private static String[] containerIds = new String[3];
    
    private static String[] itemIds = new String[4];
    
    private static int methodCounter = 0;

    /**
     * @param transport
     *            The transport identifier.
     */
    public ItemParentsTest(final int transport) {
        super(transport);
        containerTestBase = new ContainerTestBase(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
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
     * @throws Exception
     *             If anything fails.
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
     * @throws Exception
     *             If anything fails.
     */
    private void prepare() throws Exception {
        String containerData =
            containerTestBase.getContainerTemplate("create_container.xml");
        String itemData =
            getItemTemplate("create_item_minimal.xml");

        String taskParam =
            "<param last-modification-date=\"${lastModificationDate}\">"
            + "${idParams}</param>";
        String idParam = "<id>${id}</id>";
        
        String containerXml = null;
        String itemXml = null;

        for (int i = 0; i < 3; i++) {
            containerXml = containerTestBase.create(containerData);
            containerIds[i] = getObjidValue(containerXml);
        }
        for (int i = 0; i < 4; i++) {
            itemXml = create(itemData);
            itemIds[i] = getObjidValue(itemXml);
        }
        
        //Add members to containerIds[0]
        StringBuffer idParams = new StringBuffer("");
        for (int i = 0; i < 2; i++) {
            idParams.append(idParam.replaceFirst("\\$\\{id\\}", 
                                    itemIds[i]));
        }
        String replacedTaskParam = taskParam.replaceFirst(
                "\\$\\{lastModificationDate\\}", 
                containerTestBase.getTheLastModificationDate(containerIds[0]));
        replacedTaskParam = replacedTaskParam.replaceFirst(
                "\\$\\{idParams\\}", idParams.toString());
        containerTestBase.addMembers(containerIds[0], replacedTaskParam);

        //Add members to containerIds[1]
        idParams = new StringBuffer("");
        for (int i = 0; i < 3; i++) {
            idParams.append(idParam.replaceFirst("\\$\\{id\\}", 
                                    itemIds[i]));
        }
        replacedTaskParam = taskParam.replaceFirst(
                "\\$\\{lastModificationDate\\}", 
                containerTestBase.getTheLastModificationDate(containerIds[1]));
        replacedTaskParam = replacedTaskParam.replaceFirst(
                "\\$\\{idParams\\}", idParams.toString());
        containerTestBase.addMembers(containerIds[1], replacedTaskParam);

        //Add members to containerIds[2]
        idParams = new StringBuffer("");
        idParams.append(idParam.replaceFirst("\\$\\{id\\}", 
                itemIds[0]));
        replacedTaskParam = taskParam.replaceFirst(
                "\\$\\{lastModificationDate\\}", 
                containerTestBase.getTheLastModificationDate(containerIds[2]));
        replacedTaskParam = replacedTaskParam.replaceFirst(
                "\\$\\{idParams\\}", idParams.toString());
        containerTestBase.addMembers(containerIds[2], replacedTaskParam);

    }

    /**
     * Test successful retrieving parents of an item.
     * 
     * @test.name Retrieve Parents of Item
     * @test.id testRetrieveParents
     * @test.input
     *          <ul>
     *          <li>id of an existing item</li>
     *          </ul>
     * @test.expected: Parents, no exception.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParents() throws Exception {
        String parentsXml = retrieveParents(itemIds[0]);
        assertXmlValidParents(parentsXml);
        Document parentsDoc = getDocument(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 3);
        assertXmlExists("expected container not found", 
                parentsDoc, 
                "/parents/parent[@objid='" 
                + containerIds[0] 
                + "']|/parents/parent[@href='" 
                + Constants.CONTAINER_BASE_URI 
                + "/" + containerIds[0] + "']");
        assertXmlExists("expected container not found", 
                parentsDoc, 
                "/parents/parent[@objid='" 
                + containerIds[1] 
                + "']|/parents/parent[@href='" 
                + Constants.CONTAINER_BASE_URI 
                + "/" + containerIds[1] + "']");
        assertXmlExists("expected container not found", 
                parentsDoc, 
                "/parents/parent[@objid='" 
                + containerIds[2] 
                + "']|/parents/parent[@href='" 
                + Constants.CONTAINER_BASE_URI 
                + "/" + containerIds[2] + "']");
    }

    /**
     * Test successful retrieving parents of an item.
     * 
     * @test.name Retrieve Parents of Item1
     * @test.id testRetrieveParents1
     * @test.input
     *          <ul>
     *          <li>id of an existing item</li>
     *          </ul>
     * @test.expected: Parents, no exception.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParents1() throws Exception {
        String parentsXml = retrieveParents(itemIds[1]);
        assertXmlValidParents(parentsXml);
        Document parentsDoc = getDocument(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 2);
        assertXmlExists("expected container not found", 
                parentsDoc, 
                "/parents/parent[@objid='" 
                + containerIds[0] 
                + "']|/parents/parent[@href='" 
                + Constants.CONTAINER_BASE_URI 
                + "/" + containerIds[0] + "']");
        assertXmlExists("expected container not found", 
                parentsDoc, 
                "/parents/parent[@objid='" 
                + containerIds[1] 
                + "']|/parents/parent[@href='" 
                + Constants.CONTAINER_BASE_URI 
                + "/" + containerIds[1] + "']");
        assertXmlNotExists("non-expected container found", 
                parentsDoc, 
                "/parents/parent[@objid='" 
                + containerIds[2] 
                + "']|/parents/parent[@href='" 
                + Constants.CONTAINER_BASE_URI 
                + "/" + containerIds[2] + "']");
    }

    /**
     * Test successful retrieving parents of an item.
     * 
     * @test.name Retrieve Parents of Item2
     * @test.id testRetrieveParents2
     * @test.input
     *          <ul>
     *          <li>id of an existing item</li>
     *          </ul>
     * @test.expected: Parents, no exception.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParents2() throws Exception {
        String parentsXml = retrieveParents(itemIds[2]);
        assertXmlValidParents(parentsXml);
        Document parentsDoc = getDocument(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 1);
        assertXmlNotExists("non-expected container found", 
                parentsDoc, 
                "/parents/parent[@objid='" 
                + containerIds[0] 
                + "']|/parents/parent[@href='" 
                + Constants.CONTAINER_BASE_URI 
                + "/" + containerIds[0] + "']");
        assertXmlExists("expected container not found", 
                parentsDoc, 
                "/parents/parent[@objid='" 
                + containerIds[1] 
                + "']|/parents/parent[@href='" 
                + Constants.CONTAINER_BASE_URI 
                + "/" + containerIds[1] + "']");
        assertXmlNotExists("non-expected container found", 
                parentsDoc, 
                "/parents/parent[@objid='" 
                + containerIds[2] 
                + "']|/parents/parent[@href='" 
                + Constants.CONTAINER_BASE_URI 
                + "/" + containerIds[2] + "']");
    }

    /**
     * Test successful retrieving parents of an item.
     * 
     * @test.name Retrieve Parents of Item3
     * @test.id testRetrieveParents3
     * @test.input
     *          <ul>
     *          <li>id of an existing item</li>
     *          </ul>
     * @test.expected: Parents, no exception.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testRetrieveParents3() throws Exception {
        String parentsXml = retrieveParents(itemIds[3]);
        assertXmlValidParents(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 0);
    }

    /**
     * Test declining retrieving parents of an item with wrong itemId.
     * 
     * @test.name Retrieve Parents of Item
     * @test.id testDecliningRetrieveParentsWithWrongId
     * @test.input
     *          <ul>
     *          <li>id of an non-existing item</li>
     *          </ul>
     * @test.expected: ItemNotFoundException.
     * @test.status Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testDecliningRetrieveParentsWithWrongId() throws Exception {
        try {
            retrieveParents("wrongId");
            EscidocRestSoapTestBase
            .failMissingException(ItemNotFoundException.class);
        } catch (Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(
                    ItemNotFoundException.class, e);
        }
    }

    /**
    * Test declining retrieving parents of an item with itemId null.
    * 
    * @test.name Retrieve Parents of Item
    * @test.id testDecliningRetrieveParentsWithNoId
    * @test.input
    *          <ul>
    *          <li>id null</li>
    *          </ul>
    * @test.expected: MissingMethodParameterException.
    * @test.status Implemented
    * 
    * @throws Exception
    *             If anything fails.
    */
   @Test
   public void testDecliningRetrieveParentsWithNoId() throws Exception {
       try {
           retrieveParents(null);
           EscidocRestSoapTestBase
           .failMissingException(MissingMethodParameterException.class);
       } catch (Exception e) {
           EscidocRestSoapTestBase.assertExceptionType(
                   MissingMethodParameterException.class, e);
       }
   }

}
