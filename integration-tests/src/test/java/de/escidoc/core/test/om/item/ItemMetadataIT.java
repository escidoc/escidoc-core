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

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.fedora.Client;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.junit.Assert.assertNotSame;

/**
 * Test handling of meta data within Item/Components.
 *
 * @author Steffen Wagner
 */
public class ItemMetadataIT extends ItemTestBase {

    /**
     * Test if no internal mapping happens if md-record with name DC is delivered from user/solution.
     * <p/>
     * A DC meta set is usually created by XSLT mapping and stored within Fedora DC datastream. This test checks if the
     * collision between user/solution provided DC and potentioally automatically mapped DC is well solved. (The mapping
     * should not happen if DC is delivered).
     *
     * @throws Exception Thrown if self defined DC is retrievable.
     */
    @Test
    public void testCreateMd01() throws Exception {

        final String templateName = "item-minimal-for-create-01.xml";
        // check if the used meta data record is mapped to DC
        checkIfDCMappingHappens(templateName);

        String itemXml = getExampleTemplate(templateName);

        // add explicid datastream with name DC to Item
        Document curItem = EscidocAbstractTest.getDocument(itemXml);

        // select or create md-records node
        Node mdRecords = selectSingleNode(curItem, "/item/md-records");
        if (mdRecords == null) {
            // add first components element
            selectSingleNode(curItem, "/item").appendChild(curItem.createElement("escidocMdRecords:md-records"));
            mdRecords = selectSingleNode(curItem, "/item/md-records");
        }

        // add md-record with name dc
        Document mdRecordDC = EscidocAbstractTest.getTemplateAsDocument(TEMPLATE_ITEM_PATH + "/rest", "md-record.xml");
        Node newMdRecord = selectSingleNode(mdRecordDC, "/md-record");

        Node importedNde = curItem.importNode(newMdRecord, true);
        // mdRecords.appendChild(curItem.adoptNode(newMdRecord));

        substitute(curItem, "/item/md-records/md-record[@name='escidoc']", importedNde);

        curItem.normalize();

        String newXml = toString(curItem, false);

        String xml = create(newXml);

        // check if created XML contains both meta data elements
        Document newItem = EscidocAbstractTest.getDocument(xml);
        assertXmlExists("md-record with name 'escidoc' missing", newItem,
            "/item/md-records/md-record[@name = 'escidoc']");

        String objid = getObjidValue(newItem);

        Client fc = new Client();
        String dcXML = fc.getDatastreamContent("DC", objid);

        assertNotSame("DC mapping failed.", 0, dcXML.length());
        Document newDC = EscidocAbstractTest.getDocument(dcXML);

        assertXmlExists("DC mapping failed: missing or wrong dc:title", newDC,
            "/dc/title[text() = 'Quasiparticle calculations for " + "point defects at semiconductor surfaces']");

        assertXmlExists("DC mapping failed: missing or wrong dc:creator", newDC,
            "/dc/creator[text() = 'Arno Schindlmayr']");
        assertXmlExists("DC mapping failed: missing or wrong dc:subject", newDC,
            "/dc/subject[text() = 'Physics ; Astronomy']");
        assertXmlExists("DC mapping failed: missing or wrong dc:date", newDC, "/dc/date[text() = '2006-11-13']");
        assertXmlExists("DC mapping failed: missing or wrong dc:identifier", newDC, "/dc/identifier[text() = '" + objid
            + "']");
        assertXmlExists("DC mapping failed: missing or wrong dc:source", newDC,
            "/dc/source[text() = 'Theory of Defects in Semiconductors']");
        assertXmlExists("DC mapping failed: missing or wrong dc:language", newDC, "/dc/language[text() = 'en']");
    }

    /**
     * Checks if the framework is able to map the md-record with name 'escidoc' to 'DC'.
     *
     * @param templateName Name of template.
     * @throws Exception Thrown if DC record is missing.
     */
    private void checkIfDCMappingHappens(final String templateName) throws Exception {
        String itemXml = getExampleTemplate(templateName);

        // add explicid datastream with name DC to Item
        Document curItem = EscidocAbstractTest.getDocument(itemXml);
        String newXml = toString(curItem, false);

        String xml = create(newXml);

        // check if created XML contains both meta data elements
        Document newItem = EscidocAbstractTest.getDocument(xml);
        assertXmlExists("md-record with name 'escidoc' missing", newItem,
            "/item/md-records/md-record[@name = 'escidoc']");

        String objid = getObjidValue(newItem);

        Client fc = new Client();
        String dcXML = fc.getDatastreamContent("DC", objid);
        assertNotSame("DC mapping failed.", 0, dcXML.length());

    }
}