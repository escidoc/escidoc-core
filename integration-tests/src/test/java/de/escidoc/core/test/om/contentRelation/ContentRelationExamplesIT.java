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
package de.escidoc.core.test.om.contentRelation;

import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Test content relation create implementation.
 *
 * @author Steffen Wagner
 */
public class ContentRelationExamplesIT extends ContentRelationTestBase {

    /**
     * Test creating example content relation.
     *
     * @throws Exception Thrown if creating failed or not all values are like expected.
     */
    @Test
    public void testCreateExample01() throws Exception {

        String contentRelationXml = getExampleTemplate("content-relation-01.xml");
        String xml = create(contentRelationXml);
        String relationId = getObjidValue(xml);

        assertXmlValidContentRelation(xml);
        Document crDoc = getDocument(xml);
        selectSingleNodeAsserted(crDoc, "/content-relation/md-records/md-record[@name='escidoc']/bla");

        String xml1 = retrieve(relationId);
        assertXmlValidContentRelation(xml1);
        crDoc = getDocument(xml1);
        selectSingleNodeAsserted(crDoc, "/content-relation/md-records/md-record[@name='escidoc']/bla");
    }
}