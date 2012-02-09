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

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContainerNotFoundException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.Constants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test retrieving parents of the Container resource.
 *
 * @author Michael Hoppe
 */
public class ContainerParentsIT extends ContainerTestBase {

    private static String topLevelContainerId;

    private static String[] middleLevelContainerIds = new String[3];

    private static String[] lowLevelContainerIds = new String[3];

    private static int methodCounter = 0;

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
        String xmlData = getContainerTemplate("create_container.xml");

        String taskParam = "<param last-modification-date=\"${lastModificationDate}\">" + "${idParams}</param>";
        String idParam = "<id>${id}</id>";

        String containerXml = create(xmlData);
        topLevelContainerId = getObjidValue(containerXml);

        for (int i = 0; i < 3; i++) {
            containerXml = create(xmlData);
            middleLevelContainerIds[i] = getObjidValue(containerXml);
        }
        for (int i = 0; i < 2; i++) {
            containerXml = create(xmlData);
            lowLevelContainerIds[i] = getObjidValue(containerXml);
        }

        //Add members to TopLevelContainer
        StringBuffer idParams = new StringBuffer("");
        for (int i = 0; i < 3; i++) {
            idParams.append(idParam.replaceFirst("\\$\\{id\\}", middleLevelContainerIds[i]));
        }
        String replacedTaskParam =
            taskParam.replaceFirst("\\$\\{lastModificationDate\\}", getTheLastModificationDate(topLevelContainerId));
        replacedTaskParam = replacedTaskParam.replaceFirst("\\$\\{idParams\\}", idParams.toString());
        addMembers(topLevelContainerId, replacedTaskParam);

        //Add members to middleLevelContainer[0]
        idParams = new StringBuffer("");
        idParams.append(idParam.replaceFirst("\\$\\{id\\}", lowLevelContainerIds[0]));
        replacedTaskParam =
            taskParam.replaceFirst("\\$\\{lastModificationDate\\}",
                getTheLastModificationDate(middleLevelContainerIds[0]));
        replacedTaskParam = replacedTaskParam.replaceFirst("\\$\\{idParams\\}", idParams.toString());
        addMembers(middleLevelContainerIds[0], replacedTaskParam);

        //Add members to middleLevelContainer[1]
        idParams = new StringBuffer("");
        idParams.append(idParam.replaceFirst("\\$\\{id\\}", lowLevelContainerIds[0]));
        idParams.append(idParam.replaceFirst("\\$\\{id\\}", lowLevelContainerIds[1]));
        replacedTaskParam =
            taskParam.replaceFirst("\\$\\{lastModificationDate\\}",
                getTheLastModificationDate(middleLevelContainerIds[1]));
        replacedTaskParam = replacedTaskParam.replaceFirst("\\$\\{idParams\\}", idParams.toString());
        addMembers(middleLevelContainerIds[1], replacedTaskParam);

        //Add members to middleLevelContainer[2]
        idParams = new StringBuffer("");
        idParams.append(idParam.replaceFirst("\\$\\{id\\}", lowLevelContainerIds[0]));
        idParams.append(idParam.replaceFirst("\\$\\{id\\}", lowLevelContainerIds[1]));
        replacedTaskParam =
            taskParam.replaceFirst("\\$\\{lastModificationDate\\}",
                getTheLastModificationDate(middleLevelContainerIds[2]));
        replacedTaskParam = replacedTaskParam.replaceFirst("\\$\\{idParams\\}", idParams.toString());
        addMembers(middleLevelContainerIds[2], replacedTaskParam);

    }

    /**
     * Test successful retrieving parents of a container.
     *
     * @throws Exception If anything fails.
     */
    // the way to count number of tests let assume that the test design smells 
    @Test
    public void testRetrieveParents() throws Exception {
        String parentsXml = retrieveParents(middleLevelContainerIds[1]);
        assertXmlValidParents(parentsXml);
        Document parentsDoc = getDocument(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 1);
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='" + topLevelContainerId
            + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/" + topLevelContainerId + "']");
    }

    /**
     * Test successful retrieving parents of a container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveParents1() throws Exception {
        String parentsXml = retrieveParents(lowLevelContainerIds[0]);
        assertXmlValidParents(parentsXml);
        Document parentsDoc = getDocument(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 3);
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='"
            + middleLevelContainerIds[0] + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/"
            + middleLevelContainerIds[0] + "']");
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='"
            + middleLevelContainerIds[1] + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/"
            + middleLevelContainerIds[1] + "']");
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='"
            + middleLevelContainerIds[2] + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/"
            + middleLevelContainerIds[2] + "']");
    }

    /**
     * Test successful retrieving parents of a container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveParents2() throws Exception {
        String parentsXml = retrieveParents(lowLevelContainerIds[1]);
        assertXmlValidParents(parentsXml);
        Document parentsDoc = getDocument(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 2);
        assertXmlNotExists("non-expected container found", parentsDoc, "/parents/parent[@objid='"
            + middleLevelContainerIds[0] + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/"
            + middleLevelContainerIds[0] + "']");
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='"
            + middleLevelContainerIds[1] + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/"
            + middleLevelContainerIds[1] + "']");
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='"
            + middleLevelContainerIds[2] + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/"
            + middleLevelContainerIds[2] + "']");
    }

    /**
     * Test successful retrieving parents of a container.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveParents3() throws Exception {
        String parentsXml = retrieveParents(topLevelContainerId);
        assertXmlValidParents(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 0);
    }

    /**
     * Test successful retrieving parents of a container with a version number.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testRetrieveParentsWithVersion() throws Exception {
        String parentsXml = retrieveParents(lowLevelContainerIds[0] + ":1");
        assertXmlValidParents(parentsXml);
        Document parentsDoc = getDocument(parentsXml);
        assertNodeCount(parentsXml, "/parents/parent", 3);
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='"
            + middleLevelContainerIds[0] + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/"
            + middleLevelContainerIds[0] + "']");
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='"
            + middleLevelContainerIds[1] + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/"
            + middleLevelContainerIds[1] + "']");
        assertXmlExists("expected container not found", parentsDoc, "/parents/parent[@objid='"
            + middleLevelContainerIds[2] + "']|/parents/parent[@href='" + Constants.CONTAINER_BASE_URI + "/"
            + middleLevelContainerIds[2] + "']");
    }

    /**
     * Test declining retrieving parents of a container with wrong containerId.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testDecliningRetrieveParentsWithWrongId() throws Exception {
        try {
            retrieveParents("wrongId");
            EscidocAbstractTest.failMissingException(ContainerNotFoundException.class);
        }
        catch (final Exception e) {
            EscidocAbstractTest.assertExceptionType(ContainerNotFoundException.class, e);
        }
    }

    /**
     * Test declining retrieving parents of a container with containerId null.
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
