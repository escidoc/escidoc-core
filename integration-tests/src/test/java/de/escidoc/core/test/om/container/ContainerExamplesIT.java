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

import de.escidoc.core.test.common.fedora.Client;
import org.fcrepo.server.types.gen.Datastream;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * Test processing Container examples from lecture tree.
 *
 * @author Steffen Wagner
 */
public class ContainerExamplesIT extends ContainerTestBase {

    /**
     * Test if the example container for create is still compatible with framework.
     *
     * @throws Exception Thrown if creation of example Container failed.
     */
    @Test
    public void testCreatingExample01() throws Exception {

        String containerXml = getExampleTemplate("container-minimal-for-create-01.xml");
        String xml = create(containerXml);
        assertXmlValidContainer(xml);
    }

    /**
     * Test if the example container for create is still compatible with framework.
     *
     * @throws Exception Thrown if creation of example Container failed.
     */
    @Test
    public void testCreatingExample02() throws Exception {

        String containerXml = getExampleTemplate("container-minimal-for-create-02.xml");
        String xml = create(containerXml);
        assertXmlValidContainer(xml);
    }

    /**
     * Test if versionable for version-history datastream is set to false.
     *
     * @throws Exception Thrown if creation of example Item failed.
     */
    @Test
    public void testCreatingExample03() throws Exception {

        String xml = getExampleTemplate("container-minimal-for-create-01.xml");
        String itemXml = create(xml);

        String objid = getObjidValue(itemXml);
        Client fc = new Client();

        Datastream ds = fc.getDatastreamInformation(objid, "version-history");
        assertFalse("Version-History should not be versioned", ds.isVersionable());
    }

}
