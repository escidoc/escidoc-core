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
package de.escidoc.core.test.tests;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.om.OmTestBase;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * This tests test test methods. OK, it's clear that tests can't fully check systems, but it seems reasonable to check
 * some test methods.
 *
 * @author Steffen Wagner
 */
public class AssertTests extends OmTestBase {

    /**
     * Check if the assertXmlEquals method throws an exception if the name of the root elements differs.
     */
    @Test
    public void testAssertXmlEquals() throws Exception {

        try {
            String xmlData =
                EscidocAbstractTest.getTemplateAsString(TEMPLATE_CONTAINER_PATH + "/rest", "create_container.xml");
            String containerXml = handleXmlResult(getContainerClient().create(xmlData));

            String tempItemXml =
                EscidocAbstractTest
                    .getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest", "escidoc_item_198_for_create.xml");

            String containerId = getObjidValue(containerXml);
            String itemXml = handleXmlResult(getContainerClient().createItem(containerId, tempItemXml));

            // both documents should differ at least in the root elements
            assertXmlEquals("createItem() response should equals retrieve", containerXml, itemXml);

            fail("AssertXmlEquals does not recognize root element");
        }
        catch (final Exception e) {
            Class<?> ec = Exception.class;
            EscidocAbstractTest.assertExceptionType(ec, e);
        }

    }

}
