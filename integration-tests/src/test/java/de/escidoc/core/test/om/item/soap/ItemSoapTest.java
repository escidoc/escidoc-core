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
package de.escidoc.core.test.om.item.soap;

import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.om.item.ItemTestBase;
import org.junit.Test;
import org.w3c.dom.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Item tests with SOAP transport.
 * 
 * @author MSC
 * 
 */
public class ItemSoapTest extends ItemTestBase {

    /**
     * Constructor.
     * 
     */
    public ItemSoapTest() {
        super(Constants.TRANSPORT_SOAP);
    }

    /**
     * Test successfully creating item with special character (U201E), see issue
     * 288.
     * 
     * @test.name Create Item - Success - Issue 288
     * @test.id OUM_COU-issue-288
     * @test.input Valid Organizational Unit XML representation.
     * @test.expected: The expected result is the XML representation of the
     *                 created OrganizationalUnit, corresponding to XML-schema
     *                 "organizational-unit.xsd" including generated id, creator
     *                 and creation date
     * @test.status Implemented
     * @test.issue http://www.escidoc-project.de/issueManagement/show_bug.cgi?id=288
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testOMCi_issue_288() throws Exception {

        Document toBeCreatedDocument =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH,
                "item_issue_288.xml");
        deleteElement(toBeCreatedDocument, "/item/components");
        String toBeCreatedXml = toString(toBeCreatedDocument, true);

        String createdXml = null;
        try {
            createdXml = create(toBeCreatedXml);
        }
        catch (Exception e) {
            EscidocRestSoapTestBase
                .failException(
                    "Creating item with unicode character U201e failed (see issue 288)",
                    e);
        }

        assertXmlValidItem(createdXml);
        final Document createdDocument =
            EscidocRestSoapTestBase.getDocument(createdXml);
        assertXmlEquals("Node with special character is different",
            toBeCreatedDocument, createdDocument, "//alternative-name[2]");

        delete(getObjidValue(createdXml));

    }
    
    @Test
    public void testCreateUpdateIssue423() throws Exception {

        Document xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH,
                "item_fuer_umlaut_create_mpdl.xml");
        String item = toString(xmlItem, true);

        item = create(item);
        assertXmlValidItem(item);
        String lmd =
            getLastModificationDateValue(EscidocRestSoapTestBase
                .getDocument(item));

        Pattern PATTERN_OBJID_ATTRIBUTE = Pattern.compile("objid=\"([^\"]*)\"");
        Matcher m = PATTERN_OBJID_ATTRIBUTE.matcher(item);
        String itemId = null;
        if (m.find()) {
            itemId = m.group(1);
        }

        xmlItem =
            EscidocRestSoapTestBase.getTemplateAsDocument(TEMPLATE_ITEM_PATH,
                "item_fuer_umlaut_update_mpdl.xml");
        xmlItem =
            (Document) substitute(xmlItem, "/item/@last-modification-date", lmd);
        xmlItem = (Document) substitute(xmlItem, "/item/@objid", itemId);
        item = toString(xmlItem, true);

        item = update(itemId, item);
        assertXmlValidItem(item);

    }

}
