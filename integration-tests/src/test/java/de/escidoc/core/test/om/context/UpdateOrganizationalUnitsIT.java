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
package de.escidoc.core.test.om.context;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.compare.TripleStoreValue;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test the mock implementation of the item resource.
 *
 * @author Michael Schneider
 */
public class UpdateOrganizationalUnitsIT extends ContextTestBase {

    private String path = "";

    /**
     * Set up servlet test.
     *
     * @throws Exception If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        this.path += "/rest";
    }

    /**
     * Successfully test. Removing an Organizational Units to a Context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmContextDelOU() throws Exception {
        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));
        String template = toString(context, false);

        String created = create(template);
        assertXmlValidContext(created);

        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);

        // remove second OU from Context
        Node node = selectSingleNode(createdDoc, "/context/properties/organizational-units/organizational-unit");
        node.getParentNode().removeChild(node);

        // get id from removed OU
        String attrId = null;
        // String debug = toString(node, false);
        Node hrefNode = selectSingleNode(node, "@href");
        attrId = hrefNode.getNodeValue();

        created = toString(createdDoc, false);
        String newContext = update(id, created);

        // assert second OU does was removed from Context
        Document newContextDoc = EscidocAbstractTest.getDocument(newContext);

        String xpath = "/context/properties/organizational-units/organizational-unit";
        xpath += "[@href = '" + attrId + "']";
        node = selectSingleNode(newContextDoc, xpath);

        assertNull("OU not removed from Context", node);

        // assert data structure in FoXML (indirect via triple store)
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.contextTripleStoreValues(newContextDoc);
    }

    /**
     * Successfully test. Adding an Organizational Units to a Context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmContextAddOU() throws Exception {
        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));

        // remove second OU from Context
        Node node = selectSingleNode(context, "/context/properties/organizational-units/organizational-unit");

        String attrId = null;
        Node hrefNode = selectSingleNode(node, "@href");
        attrId = hrefNode.getNodeValue();

        node.getParentNode().removeChild(node);

        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);

        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);

        // add new OU to properties/org-units
        Node ou = selectSingleNode(createdDoc, "/context/properties/organizational-units/organizational-unit");
        Node newOu = ou.cloneNode(true);

        String xpath = "";
        xpath = "@href";

        substitute(newOu, xpath, attrId);

        Node ous = selectSingleNode(createdDoc, "/context/properties/organizational-units");

        ous.appendChild(newOu);

        template = toString(createdDoc, false);
        String newContext = update(id, template);

        // assert second OU does was removed from Context
        Document newContextDoc = EscidocAbstractTest.getDocument(newContext);

        // assert number of OUs == 2
        NodeList updatedOus =
            selectNodeList(newContextDoc, "/context/properties/organizational-units/organizational-unit");
        assertEquals("Missing new OU after update", 2, updatedOus.getLength());

        xpath = "/context/properties/organizational-units/organizational-unit";
        xpath += "[@href = '" + attrId + "']";
        node = selectSingleNode(newContextDoc, xpath);

        assertNotNull("OU not added to Context", node);

        // assert data structure in FoXML (indirect via triple store)
        TripleStoreValue tsv = new TripleStoreValue();
        tsv.contextTripleStoreValues(newContextDoc);
    }

    /**
     * Successfully test. Replacing an Organizational Units in a Context.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOmContextReplaceOU() throws Exception {
        Document context =
            EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_CONTEXT_PATH + this.path, "context_create.xml");
        substitute(context, "/context/properties/name", getUniqueName("PubMan Context "));

        // remove second OU from Context
        Node node = selectSingleNode(context, "/context/properties/organizational-units/organizational-unit");

        String attrId = null;
        // String debug = toString(node, false);
        Node hrefNode = selectSingleNode(node, "@href");
        attrId = hrefNode.getNodeValue();

        node.getParentNode().removeChild(node);

        String template = toString(context, false);
        String created = create(template);
        assertXmlValidContext(created);

        Document createdDoc = EscidocAbstractTest.getDocument(created);
        String id = getObjidValue(createdDoc);

        NodeList createdOus =
            selectNodeList(createdDoc, "/context/properties/organizational-units/organizational-unit");
        assertEquals("More than one OUs created", createdOus.getLength(), 1);

        // replace id
        String xpath = "/context/properties/organizational-units/organizational-unit/";
        xpath += "@href";
        // node = selectSingleNode(newContextDoc, xpath);
        substitute(createdDoc, xpath, attrId);

        // assert that update Context has only one OU
        NodeList updateOu = selectNodeList(createdDoc, "/context/properties/organizational-units/organizational-unit");
        assertEquals("More than one OUs for update", 1, updateOu.getLength());

        created = toString(createdDoc, false);
        String newContext = update(id, created);

        // assert that the OU was replaced in Context
        Document newContextDoc = EscidocAbstractTest.getDocument(newContext);

        // assert that only one OU is part of Context after OU
        NodeList updatedOus =
            selectNodeList(newContextDoc, "/context/properties/organizational-units/organizational-unit");
        assertEquals("Only one OU after update", updatedOus.getLength(), 1);

        // assert that the single OU has the right id
        xpath = "/context/properties/organizational-units/organizational-unit";
        xpath += "[@href = '" + attrId + "']";
        updateOu = selectNodeList(createdDoc, xpath);
        assertEquals("More than one OUs for update", 1, updateOu.getLength());
    }
}
