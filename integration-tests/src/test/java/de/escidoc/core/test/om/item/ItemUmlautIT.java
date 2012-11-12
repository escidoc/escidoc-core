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

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.TaskParamFactory;

/**
 * Test creating Item objects.
 * 
 * @author Steffen Wagner
 */
public class ItemUmlautIT extends ItemTestBase {

    /**
     * Test updating item belonging to context with umlauts in name.
     * 
     * @throws Exception
     *             Thrown if creation of example Item failed.
     */
    @Test
    public void testContextWithUmlaut() throws Exception {
        Document context =
            getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + "/rest",
                "context_create.xml");
        String title = "Test Context äöü " + System.currentTimeMillis();
        substitute(context, "/context/properties/name", title);
        String contextXml =
            handleXmlResult(getContextClient().create(
                toString(context, false)));
        String contextId = getObjidValue(contextXml);
        String lastModified = getLastModificationDateValue(getDocument(contextXml));
        getContextClient().open(contextId, TaskParamFactory.getStatusTaskParam(new DateTime(lastModified, DateTimeZone.UTC), null));
        String itemXml =
            getTemplateAsString(TEMPLATE_ITEM_PATH + "/rest",
                "escidoc_item_1_component.xml");
        Document item = getDocument(itemXml);
        item = (Document) substituteId(item, "/item/properties/context", contextId);
        itemXml = create(toString(item, false));
        String itemId = getObjidValue(itemXml);
        item = (Document)deleteElement(getDocument(itemXml), "/item/components/component");
        itemXml = update(itemId, toString(item, false));
        assertXmlEquals("Umlauts not correctly handled", itemXml, "/item/properties/context/@title", title);
    }

}
