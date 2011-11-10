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

import org.junit.Ignore;
import org.junit.Test;
import java.util.ArrayList;
import org.joda.time.DateTime;
import org.w3c.dom.Document;

/**
 * Test all references of a Container.
 * 
 * @author Steffen Wagner
 */
public class ContainerMemberStressIT extends ContainerTestBase {

    final private static int NO_MEMBERS = 5000;

    /**
     * Create a huge number of Item and add them as Member to the Container.
     * 
     * @throws Exception
     */
    @Ignore("Ignore because of very long process time")
    @Test
    public void hughNumberofMembers() throws Exception {

        String xmlData = getContainerTemplate("create_container.xml");
        Document containerDoc = getDocument(create(xmlData));
        String containerId = getObjidValue(containerDoc);

        ArrayList<String> ids = new ArrayList<String>();
        for (int i = 0; i < NO_MEMBERS; i++) {
            final String memberId = createItem();
            ids.add(memberId);
        }

        addMembers(containerId, getMembersTaskParam(getLastModificationDateValue2(containerDoc), ids));
    }

    /**
     * Create Item via Item interface. 
     * 
     * @return id of the created Item.
     * @throws Exception
     *             Thrown if creation failed.
     */
    private String createItem() throws Exception {

        String xmlData = getItemTemplate("escidoc_item_198_for_create.xml");

        return getObjidValue(handleXmlResult(getItemClient().create(xmlData)));
    }
}
