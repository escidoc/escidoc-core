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

import de.escidoc.core.test.EscidocAbstractTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Test stability by increasing the number of versions of the Container resource.
 *
 * @author Rozita Friedman
 */
public class ContainerRetrieveLargContainerIT extends ContainerTestBase {

    private String theContainerXml;

    private String theContainerId;

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {

        String theItemId = createItemFromTemplate("escidoc_item_198_for_create.xml");
        String xmlData = getContainerTemplate("create_container_v1.1-forItem.xml");
        String replaced = xmlData.replaceAll("##ITEMID##", theItemId);

        theContainerXml = create(replaced);
        this.theContainerId = getObjidValue(this.theContainerXml);

    }

    /**
     * Tested successively adding 10000 members to a container and retrieve of this large container.
     */
    // ignore test now because it runs 6h 
    @Ignore("ignore test now because it runs 6h")
    @Test
    public void testAddAllMembers() throws Exception {

        for (int i = 0; i < 10000; i++) {
            String itemToAddID = createItemFromTemplate("escidoc_item_198_for_create.xml");

            String taskParam = "<param last-modification-date=\"" + getTheLastModificationDate() + "\" ";
            taskParam += ">";

            taskParam += "<id>" + itemToAddID + "</id>";

            taskParam += "</param>";
            try {
                addMembers(theContainerId, taskParam);
            }
            catch (final Exception e) {
                throw new Exception(e);
            }

        }

        String containerXml = retrieve(theContainerId);
        assertXmlValidContainer(containerXml);
    }

    private String getTheLastModificationDate() throws Exception {
        Document item = EscidocAbstractTest.getDocument(retrieve(theContainerId));

        // get last-modification-date
        NamedNodeMap atts = item.getDocumentElement().getAttributes();
        Node lastModificationDateNode = atts.getNamedItem("last-modification-date");
        String lastModificationDate = lastModificationDateNode.getNodeValue();

        return lastModificationDate;
    }

}