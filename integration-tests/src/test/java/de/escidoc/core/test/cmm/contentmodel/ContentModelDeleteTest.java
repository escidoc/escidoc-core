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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.  
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.test.cmm.contentmodel;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.violated.ResourceInUseException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.om.OmTestBase;
import org.junit.Test;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class ContentModelDeleteTest extends ContentModelTestBase {

    /**
     * Test deleting a ContentModel with minimal content.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCmDelete() throws Exception {

        String cmXml;
        String createdXML;
        String contentModelId;

        // minimal Content Model
        cmXml = getExampleTemplate("content-model-minimal-for-create.xml");
        createdXML = create(cmXml);
        contentModelId = getObjidValue(createdXML);
        retrieve(contentModelId);

        delete(contentModelId);

        Class<?> ec = ContentModelNotFoundException.class;
        try {
            retrieve(contentModelId);
            EscidocRestSoapTestBase.failMissingException("No exception retrieving deleted content model.", ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test deleting a ContentModel with minimal content which is referenced by a content object.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCmDeleteWhileInUse() throws Exception {

        String cmXml;
        String createdXML;
        String contentModelId;

        // minimal Content Model
        cmXml = getExampleTemplate("content-model-minimal-for-create.xml");
        createdXML = create(cmXml);
        contentModelId = getObjidValue(createdXML);
        retrieve(contentModelId);

        // create item with this content model
        String itemXml =
            EscidocRestSoapTestBase.getTemplateAsString(TEMPLATE_CONTENT_MODEL_PATH + "/rest",
                "item-minimal-for-content-model.xml");
        itemXml = itemXml.replace("##CONTENT_MODEL_ID##", contentModelId);
        OmTestBase omBase = new OmTestBase();
        itemXml = handleXmlResult(omBase.getItemClient().create(itemXml));

        Class<?> ec = ResourceInUseException.class;
        try {
            delete(contentModelId);
            EscidocRestSoapTestBase.failMissingException(ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test deleting a not existing ContentModel.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCtmDCt2() throws Exception {
        Class<?> ec = ContentModelNotFoundException.class;
        try {
            delete(UNKNOWN_ID);
            EscidocRestSoapTestBase.failMissingException(ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test deleting a ContentModel with providing an id of an existing resource of another type.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCtmDCt2_2() throws Exception {

        Class<?> ec = ContentModelNotFoundException.class;
        try {
            delete(CONTEXT_ID);
            EscidocRestSoapTestBase.failMissingException(ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }

    /**
     * Test deleting an ContentModel without id.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testCtmDCt3() throws Exception {

        Class<?> ec = MissingMethodParameterException.class;
        try {
            delete(null);
            EscidocRestSoapTestBase.failMissingException(ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType(ec, e);
        }
    }
}
