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
package de.escidoc.core.test.om.container.soap;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.escidoc.core.common.exceptions.remote.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.remote.application.notfound.ContainerNotFoundException;
import de.escidoc.core.test.EscidocRestSoapTestsBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.om.container.ContainerUpdateTest;

/**
 * Item tests with REST transport.
 * 
 * @author MSC
 * 
 */
public class ContainerUpdateSoapTest extends ContainerUpdateTest {

    /**
     * Constructor.
     * 
     */
    public ContainerUpdateSoapTest() {
        super(Constants.TRANSPORT_SOAP);
    }

    /**
     * Create Toc (only SOAP).
     * 
     * @test.name Create Toc (only SOAP)
     * @test.id OM_CTC_1_1
     * @test.input Container ID, correct XML representation of a toc for the
     *             container.
     * 
     * @test.expected The xml representation of a toc for the container.
     * 
     * @test.status Not Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void UtestOM_CTC_1_1() throws Exception {
        dotestCreateToc();
    }

    /**
     * Create Toc: nonexisting container.
     * 
     * @test.name Create Toc: nonexisting container.
     * @test.id OM_CTC_2
     * @test.input Container ID, correct XML representation to create a toc for
     *             the container.
     * 
     * @test.expected Error message with reason for failure.
     * 
     * @test.status Not Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void UtestOM_CTC_2() throws Exception {
        try {
            createToc("escidoc:nonexsist", getCreateTocXml(startTimestamp));
            fail("No exception on create toc in nonexisting container.");
        }
        catch (Exception e) {
            Class ec = ContainerNotFoundException.class;
            EscidocRestSoapTestsBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * Create Toc: incorrect xml representation to create a toc for the
     * container.
     * 
     * @test.name Create Toc: incorrect xml representation to create a toc for
     *            the container.
     * @test.id OM_CTC_3
     * @test.input Container ID, incorrect XML representation to create a toc
     *             for the container.
     * 
     * @test.expected Error message with reason for failure.
     * 
     * @test.status Not Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void notestOM_CTC_3() throws Exception {
        fail("TODO");
    }

    /**
     * Create Toc: Container id not provided.
     * 
     * @test.name Create Toc: Container id not provided.
     * @test.id OM_CTC_4_1
     * @test.input No container ID, correct XML representation to create a toc
     *             for the container.
     * 
     * @test.expected Error message with reason for failure.
     * 
     * @test.status Not Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void UtestOM_CTC_4_1() throws Exception {
        try {
            createToc(null, getCreateTocXml(""));
            fail("No exception on create toc without container id.");
        }
        catch (Exception e) {
            Class ec = MissingMethodParameterException.class;
            EscidocRestSoapTestsBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    /**
     * Create Toc: xml representation of toc not provided.
     * 
     * @test.name Create Toc: xml representation of toc not provided.
     * @test.id OM_CTC_4_2
     * @test.input Container ID, no XML representation to create a toc for the
     *             container.
     * 
     * @test.expected Error message with reason for failure.
     * 
     * @test.status Not Implemented
     * 
     * @throws Exception
     *             If anything fails.
     */
    public void UtestOM_CTC_4_2() throws Exception {
        try {
            createToc(getTheContainerId(), null);
            fail("No exception on create toc without xml representation.");
        }
        catch (Exception e) {
            Class ec = MissingMethodParameterException.class;
            EscidocRestSoapTestsBase.assertExceptionType(ec.getName()
                + " expected.", ec, e);
        }
    }

    public void dotestCreateToc() throws Exception {
        deleteToc(getTheContainerId());
        createToc(
            getTheContainerId(),
            getCreateTocXml(getLastModificationDateValue(EscidocRestSoapTestsBase
                .getDocument(retrieve(getTheContainerId())))));
        Document containerWithNewToc =
            EscidocRestSoapTestsBase.getDocument(retrieve(getTheContainerId()));
        Node toc =
            selectSingleNodeAsserted(containerWithNewToc, "/container/toc");
        assertXmlValidContainer(toString(toc, true));

    }

    public String getCreateTocXml(final String lastModificationDate) {
        return "<toc:toc "
            // + XLINK_HREF_ESCIDOC + "=\"/ir/container/escidoc:5790/toc\" "
            + XLINK_TITLE_ESCIDOC
            + "=\"tocs\" "
            + XLINK_TYPE_ESCIDOC
            + "=\"simple\" "
            + "xmlns:toc=\"http://www.escidoc.de/schemas/toc/0.2\" "
            + XLINK_NS_DECL_ESCIDOC
            + " last-modification-date=\""
            + lastModificationDate
            + "\" >"
            + "<toc:description>description</toc:description>"
            + "<member-ref-list:member-ref-list "
            + "xmlns:member-ref-list=\"http://www.escidoc.de/schemas/memberreflist/0.2\"> "
            + "</member-ref-list:member-ref-list></toc:toc>";
    }
}
