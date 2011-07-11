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
package de.escidoc.core.test.oum.organizationalunit;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidStatusException;
import de.escidoc.core.test.EscidocXmlElements;
import de.escidoc.core.test.common.client.servlet.Constants;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.fail;

/**
 * Testing predecessor/successor relation of OU.
 *
 * @author Steffen Wagner
 */
public class PredecessorIT extends OrganizationalUnitTestBase {

    private static final String REPLACEMENT = "replacement";

    private static final String SPIN_OFF = "spin-off";

    private static final String FUSION = "fusion";

    private static final String AFFILIATION = "affiliation";

    private static final String INVALID = "invalid";

    /**
     * Test creating predecessor relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumPredecessorCreate01() throws Exception {

        // create two predecessors
        String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
        final String ou1Id = getObjidValue(getDocument(ou1Xml));

        open(ou1Id, getTheLastModificationParam(true, ou1Id, "Opened organizational unit."));

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);

        // add predecessor to OU 2
        Element predecessorsElement =
            toBeCreatedDocument.createElementNS(ORGANIZATIONAL_UNIT_NS_URI, "prefix-organizational-unit:predecessors");

        Element predecessorElement = toBeCreatedDocument.createElementNS(SREL_NS_URI, "prefix-srel:predecessor");
        predecessorElement.setAttributeNS(XLINK_NS_URI, "prefix-xlink:href", Constants.ORGANIZATIONAL_UNIT_BASE_URI
            + "/" + ou1Id);
        predecessorElement.setAttribute(EscidocXmlElements.OU_PREDECESSORS_ATTR_FORM, REPLACEMENT);

        predecessorsElement.appendChild(predecessorElement);

        toBeCreatedDocument.getDocumentElement().appendChild(predecessorsElement);

        String tmp = toString(toBeCreatedDocument, true);
        String ou2Xml = create(tmp);

        // check if predecessor is set
        assertXmlExists("Predecessor missing", ou2Xml, "/organizational-unit/predecessors/predecessor[@href='"
            + "/oum/organizational-unit/" + ou1Id + "']");

        final String ou2Id = getObjidValue(getDocument(ou2Xml));

        ou1Xml = retrieveSuccessors(ou1Id);
        // check if successor is set

        ou2Xml = retrieve(ou2Id);
        // check if predecessor is set
        assertXmlExists("Predecessor missing", ou2Xml, "/organizational-unit/predecessors/predecessor[@href='"
            + "/oum/organizational-unit/" + ou1Id + "']");
    }

    /**
     * Test creating successfully predecessor with form 'fusion'.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumPredecessorCreate02() throws Exception {

        // create two predecessors
        String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
        final String ou1Id = getObjidValue(getDocument(ou1Xml));

        open(ou1Id, getTheLastModificationParam(true, ou1Id, "Opened organizational unit."));

        String ou2Xml = createSuccessfully("escidoc_ou_create.xml");
        final String ou2Id = getObjidValue(getDocument(ou2Xml));

        open(ou2Id, getTheLastModificationParam(true, ou2Id, "Opened organizational unit."));

        // creating successor

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);

        // add predecessor to OU 3
        Element predecessorsElement =
            toBeCreatedDocument.createElementNS(ORGANIZATIONAL_UNIT_NS_URI, "prefix-organizational-unit:predecessors");

        // add predecessor 1
        Element predecessorElement = toBeCreatedDocument.createElementNS(SREL_NS_URI, "prefix-srel:predecessor");
        predecessorElement.setAttributeNS(XLINK_NS_URI, "prefix-xlink:href", Constants.ORGANIZATIONAL_UNIT_BASE_URI
            + "/" + ou1Id);
        predecessorElement.setAttribute(EscidocXmlElements.OU_PREDECESSORS_ATTR_FORM, FUSION);

        predecessorsElement.appendChild(predecessorElement);

        // add predecessor 2
        Element predecessorElement2 = toBeCreatedDocument.createElementNS(SREL_NS_URI, "prefix-srel:predecessor");
        predecessorElement2.setAttributeNS(XLINK_NS_URI, "prefix-xlink:href", Constants.ORGANIZATIONAL_UNIT_BASE_URI
            + "/" + ou2Id);
        predecessorElement2.setAttribute(EscidocXmlElements.OU_PREDECESSORS_ATTR_FORM, FUSION);

        predecessorsElement.appendChild(predecessorElement2);

        // add predecessors element
        toBeCreatedDocument.getDocumentElement().appendChild(predecessorsElement);

        String tmp = toString(toBeCreatedDocument, true);
        String ou3Xml = create(tmp);

        // check if predecessor is set
        assertXmlExists("Predecessor missing", ou3Xml, "/organizational-unit/predecessors/predecessor[@href='"
            + "/oum/organizational-unit/" + ou1Id + "']");
        assertXmlExists("Predecessor missing", ou3Xml, "/organizational-unit/predecessors/predecessor[@href='"
            + "/oum/organizational-unit/" + ou2Id + "']");
        final String ou3Id = getObjidValue(getDocument(ou3Xml));

        ou3Xml = retrieve(ou3Id);

        // check if predecessor is set
        assertXmlExists("Predecessor missing", ou3Xml, "/organizational-unit/predecessors/predecessor[@href='"
            + "/oum/organizational-unit/" + ou1Id + "']");
        assertXmlExists("Predecessor missing", ou3Xml, "/organizational-unit/predecessors/predecessor[@href='"
            + "/oum/organizational-unit/" + ou2Id + "']");

        // check if successors are set
        ou1Xml = retrieveSuccessors(ou1Id);
        // check if successor is set
        assertXmlExists("Predecessor missing", ou1Xml, "/successors/successor" + "[@href='/oum/organizational-unit/"
            + ou3Id + "']");
        ou2Xml = retrieveSuccessors(ou2Id);
        assertXmlExists("Predecessor missing", ou1Xml, "/successors/successor" + "[@href='/oum/organizational-unit/"
            + ou3Id + "']");
    }

    /**
     * Test if an exception is thrown if multiple predecessors are defined with form 'replacement'.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumPredecessorCreate03() throws Exception {

        // create two predecessors
        String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
        final String ou1Id = getObjidValue(getDocument(ou1Xml));

        open(ou1Id, getTheLastModificationParam(true, ou1Id, "Opened organizational unit."));

        String ou2Xml = createSuccessfully("escidoc_ou_create.xml");
        final String ou2Id = getObjidValue(getDocument(ou2Xml));

        open(ou2Id, getTheLastModificationParam(true, ou2Id, "Opened organizational unit."));

        // creating successor

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);

        // add predecessor to OU 3
        Element predecessorsElement =
            toBeCreatedDocument.createElementNS(ORGANIZATIONAL_UNIT_NS_URI, "prefix-organizational-unit:predecessors");

        // add predecessor 1
        Element predecessorElement = toBeCreatedDocument.createElementNS(SREL_NS_URI, "prefix-srel:predecessor");
        predecessorElement.setAttributeNS(XLINK_NS_URI, "prefix-xlink:href", Constants.ORGANIZATIONAL_UNIT_BASE_URI
            + "/" + ou1Id);
        predecessorElement.setAttribute(EscidocXmlElements.OU_PREDECESSORS_ATTR_FORM, REPLACEMENT);

        predecessorsElement.appendChild(predecessorElement);

        // add predecessor 2
        Element predecessorElement2 = toBeCreatedDocument.createElementNS(SREL_NS_URI, "prefix-srel:predecessor");
        predecessorElement2.setAttributeNS(XLINK_NS_URI, "prefix-xlink:href", Constants.ORGANIZATIONAL_UNIT_BASE_URI
            + "/" + ou2Id);
        predecessorElement2.setAttribute(EscidocXmlElements.OU_PREDECESSORS_ATTR_FORM, REPLACEMENT);

        predecessorsElement.appendChild(predecessorElement2);

        // add predecessors element
        toBeCreatedDocument.getDocumentElement().appendChild(predecessorsElement);

        String tmp = toString(toBeCreatedDocument, true);
        try {
            create(tmp);
            fail("It is possible to define a 'replacement' with multiple OUs" + " as predecessors.");
        }
        catch (final Exception e) {

        }
    }

    /**
     * Test updating predecessor relation.
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumPredecessorUpdate01() throws Exception {

        String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
        final String ou1Id = getObjidValue(getDocument(ou1Xml));

        open(ou1Id, getTheLastModificationParam(true, ou1Id, "Opened organizational unit."));

        // create second OU and set OU1 as predecessor
        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);

        Element predecessorsElement =
            toBeCreatedDocument.createElementNS(ORGANIZATIONAL_UNIT_NS_URI, "prefix-organizational-unit:predecessors");

        Element predecessorElement = toBeCreatedDocument.createElementNS(SREL_NS_URI, "prefix-srel:predecessor");
        predecessorElement.setAttributeNS(XLINK_NS_URI, "prefix-xlink:href", Constants.ORGANIZATIONAL_UNIT_BASE_URI
            + "/" + ou1Id);
        predecessorElement.setAttribute(EscidocXmlElements.OU_PREDECESSORS_ATTR_FORM, REPLACEMENT);

        predecessorsElement.appendChild(predecessorElement);

        toBeCreatedDocument.getDocumentElement().appendChild(predecessorsElement);

        String tmp = toString(toBeCreatedDocument, true);
        String ou2Xml = create(tmp);
        final String ou2Id = getObjidValue(getDocument(ou2Xml));

        // alter successor of OU2 from OU1 to OU3

        // create third OU
        String ou3Xml = createSuccessfully("escidoc_ou_create.xml");
        final String ou3Id = getObjidValue(getDocument(ou3Xml));

        open(ou3Id, getTheLastModificationParam(true, ou3Id, "Opened organizational unit."));

        // update OU2
        Document ou2doc = getDocument(retrieve(ou2Id));

        predecessorElement = toBeCreatedDocument.createElementNS(SREL_NS_URI, "srel:predecessor");

        predecessorElement.setAttributeNS(XLINK_NS_URI, "xlink:href", Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
            + ou3Id);
        predecessorElement.setAttribute(EscidocXmlElements.OU_PREDECESSORS_ATTR_FORM, REPLACEMENT);

        ou2doc.adoptNode(predecessorElement);
        selectSingleNode(ou2doc, "/organizational-unit/predecessors").replaceChild(predecessorElement,
            selectSingleNode(ou2doc, "/organizational-unit/predecessors/predecessor"));

        tmp = toString(ou2doc, true);
        ou2Xml = update(ou2Id, tmp);

        // check if predecessor is set
        assertXmlExists("Predecessor missing", ou2Xml, "/organizational-unit/predecessors/predecessor[@href='"
            + "/oum/organizational-unit/" + ou3Id + "']");
    }

    /**
     * Test creating predecessor relation from OU1 to OU1 (to itself loop).
     *
     * @throws Exception If anything fails.
     */
    @Test
    public void testOumPredecessorUpdate02() throws Exception {

        // create two parents
        String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
        final String ou1Id = getObjidValue(getDocument(ou1Xml));

        open(ou1Id, getTheLastModificationParam(true, ou1Id, "Opened organizational unit."));

        Document ou1doc = getDocument(retrieve(ou1Id));

        // append OU1 as predecessor to OU1
        Element predecessorsElement =
            ou1doc.createElementNS(ORGANIZATIONAL_UNIT_NS_URI, "organizational-unit:predecessors");

        Element predecessorElement = ou1doc.createElementNS(SREL_NS_URI, "srel:predecessor");
        predecessorElement.setAttributeNS(XLINK_NS_URI, "xlink:href", Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/"
            + ou1Id);
        predecessorElement.setAttribute(EscidocXmlElements.OU_PREDECESSORS_ATTR_FORM, REPLACEMENT);

        predecessorsElement.appendChild(predecessorElement);

        ou1doc.getDocumentElement().appendChild(predecessorsElement);

        String tmp = toString(ou1doc, true);
        try {
            update(ou1Id, tmp);
            fail("Missing exception. Predecessor can point to OU itself (loop).");
        }
        catch (final Exception e) {
            Class<? extends Exception> ec = InvalidStatusException.class;
            assertExceptionType(ec, e);
        }

    }

    /**
     * Test updating predecessor relation.
     *
     * @throws Exception If anything fails.
     */
    @Ignore("Test updating predecessor relation")
    @Test
    public void notestOumPredecessorUpdate03() throws Exception {

        // create OU1
        String ou1Xml = createSuccessfully("escidoc_ou_create.xml");
        final String ou1Id = getObjidValue(getDocument(ou1Xml));

        open(ou1Id, getTheLastModificationParam(true, ou1Id, "Opened organizational unit."));

        Document toBeCreatedDocument =
            getTemplateAsDocument(TEMPLATE_ORGANIZATIONAL_UNIT_PATH, "escidoc_ou_create.xml");
        setUniqueValue(toBeCreatedDocument, XPATH_ORGANIZATIONAL_UNIT_TITLE);

        // add OU1 as predecessor to OU 2
        Element predecessorsElement =
            toBeCreatedDocument.createElementNS(ORGANIZATIONAL_UNIT_NS_URI, "prefix-organizational-unit:predecessors");

        Element predecessorElement = toBeCreatedDocument.createElementNS(SREL_NS_URI, "prefix-srel:predecessor");
        predecessorElement.setAttributeNS(XLINK_NS_URI, "prefix-xlink:href", Constants.ORGANIZATIONAL_UNIT_BASE_URI
            + "/" + ou1Id);
        predecessorElement.setAttribute(EscidocXmlElements.OU_PREDECESSORS_ATTR_FORM, REPLACEMENT);

        predecessorsElement.appendChild(predecessorElement);

        toBeCreatedDocument.getDocumentElement().appendChild(predecessorsElement);

        String tmp = toString(toBeCreatedDocument, true);
        String ou2Xml = create(tmp);

        Document ou2doc = getDocument(ou2Xml);

        final String ou2Id = getObjidValue(ou2doc);

        // create third OU
        String ou3Xml = createSuccessfully("escidoc_ou_create.xml");
        final String ou3Id = getObjidValue(getDocument(ou3Xml));

        open(ou3Id, getTheLastModificationParam(true, ou3Id, "Opened organizational unit."));

        // append OU3 as predecessor to OU

        Element predOU3 = ou2doc.createElementNS(SREL_NS_URI, "srel:predecessor");
        predOU3.setAttributeNS(XLINK_NS_URI, "xlink:href", Constants.ORGANIZATIONAL_UNIT_BASE_URI + "/" + ou3Id);
        predOU3.setAttribute(EscidocXmlElements.OU_PREDECESSORS_ATTR_FORM, FUSION);

        selectSingleNode(ou2doc, "/organizational-unit/predecessors").appendChild(predOU3);

        tmp = toString(ou2doc, true);
        ou2Xml = update(ou2Id, tmp);

        // check if predecessor is set
        assertXmlExists("Predecessor missing", ou2Xml, "/organizational-unit/predecessors/predecessor[@href='"
            + "/oum/organizational-unit/" + ou1Id + "']");
        assertXmlExists("Predecessor missing", ou2Xml, "/organizational-unit/predecessors/predecessor[@href='"
            + "/oum/organizational-unit/" + ou3Id + "']");
    }

}
