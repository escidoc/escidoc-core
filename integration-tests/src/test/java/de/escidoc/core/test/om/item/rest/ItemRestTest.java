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
package de.escidoc.core.test.om.item.rest;

import de.escidoc.core.common.exceptions.remote.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContextNotFoundException;
import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.om.interfaces.ItemXpathsProvider;
import de.escidoc.core.test.om.item.ItemTestBase;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Item tests.
 *
 * @author Michael Schneider
 */
public class ItemRestTest extends ItemTestBase implements ItemXpathsProvider {

    /**
     * Test declining creation of Item with providing reference to context with invalid href (substring context not in
     * href).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMCi13_1_rest() throws Exception {

        final Class<?> ec = ContextNotFoundException.class;

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml");

        String href = selectSingleNodeAsserted(toBeCreatedDocument, XPATH_ITEM_CONTEXT_XLINK_HREF).getTextContent();
        href = href.replaceFirst(Constants.CONTEXT_BASE_URI, Constants.ORGANIZATIONAL_UNIT_BASE_URI);
        substitute(toBeCreatedDocument, XPATH_ITEM_CONTEXT_XLINK_HREF, href);

        String toBeCreatedXml = toString(toBeCreatedDocument, true);

        try {
            create(toBeCreatedXml);
            EscidocRestSoapTestBase.failMissingException("Creating item with invalid object href not declined. ", ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Creating item with invalid object href not declined,"
                + " properly. ", ec, e);
        }
    }

    /**
     * Test declining creation of Item with providing reference to content-model with invalid href (substring
     * content-model not in href).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOMCi13_2_rest() throws Exception {

        final Class ec = ContentModelNotFoundException.class;

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_198_for_create.xml");

        String href =
            selectSingleNodeAsserted(toBeCreatedDocument, XPATH_ITEM_CONTENT_TYPE_XLINK_HREF).getTextContent();
        href = href.replaceFirst(Constants.CONTENT_MODEL_BASE_URI, Constants.ORGANIZATIONAL_UNIT_BASE_URI);
        substitute(toBeCreatedDocument, XPATH_ITEM_CONTENT_TYPE_XLINK_HREF, href);

        String toBeCreatedXml = toString(toBeCreatedDocument, true);

        try {
            create(toBeCreatedXml);
            EscidocRestSoapTestBase.failMissingException("Creating item with invalid object href not declined. ", ec);
        }
        catch (final Exception e) {
            EscidocRestSoapTestBase.assertExceptionType("Creating item with invalid object href not declined,"
                + " properly. ", ec, e);
        }
    }
}
