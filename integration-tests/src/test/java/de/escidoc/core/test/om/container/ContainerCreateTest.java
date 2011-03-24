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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.escidoc.core.common.exceptions.remote.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.remote.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.test.EscidocRestSoapTestBase;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.Document;


/**
 * Test the mock implementation of the item resource.
 * 
 * @author Michael Schwantner
 * 
 */
@RunWith(value = Parameterized.class)
public class ContainerCreateTest extends ContainerTestBase {

    public static final String XPATH_CONTAINER_XLINK_HREF = "/container/@href";

    public static final String XPATH_CONTAINER_XLINK_TITLE =
        "/container/@title";

    private String path = "";

    /**
     * @param transport
     *            The transport identifier.
     */
    public ContainerCreateTest(final int transport) {
        super(transport);
    }

    /**
     * Set up servlet test.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Before
    public void setUp() throws Exception {
        this.path += "/" + getTransport(false);
    }

    /**
     * Test successfully creating a Container with an Item and a Container as
     * members.
     */
    @Test
    public void testCreateContainerWithMembers() throws Exception {

        String theContainerId =
            createContainerFromTemplate("create_container_WithoutMembers_v1.1.xml");

        String theItemId =
            createItemFromTemplate("escidoc_item_198_for_create.xml");

        String containerTemplate =
            getContainerTemplate("create_container_v1.1-forItemAndforContainer.xml");

        String xmlWithItem =
            containerTemplate.replaceAll("##ITEMID##", theItemId);
        String xmlWithItemAndContainer =
            xmlWithItem.replaceAll("##CONTAINERID##", theContainerId);

        String theContainerXml = create(xmlWithItemAndContainer);
        Document theContainer = getDocument(theContainerXml);

        selectSingleNodeAsserted(theContainer,
            "/container/struct-map//*[@objid='" + theItemId
                + "' or @href= '/ir/item/" + theItemId + "']");
        selectSingleNodeAsserted(theContainer,
            "/container/struct-map//*[@objid='" + theContainerId
                + "' or @href= '/ir/container/" + theContainerId + "']");
    }

    /**
     * Successful creation of a Container with empty content of an md-redord.
     * SchemaException expected.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test(expected = XmlSchemaValidationException.class)
    public void testConCr1() throws Exception {

        Document context =
            EscidocRestSoapTestBase.getTemplateAsDocument(
                TEMPLATE_CONTAINER_PATH + this.path, "create_container.xml");
        substitute(context, "/container/properties/name",
            getUniqueName("Container Name "));
        substitute(context, "/container/md-records/md-record[1]", "");
        String template = toString(context, false);

        create(template);
    }

    /**
     * Test if namespaces in meta data records of Container are still part of
     * the representation after create.
     * 
     * Issue INFR-947
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Ignore
    @Test
    public void containerMetadataNamespaces() throws Exception {

        String container =
            getTemplateAsString(TEMPLATE_CONTAINER_PATH + this.path,
                "container_issue_infr_947.xml");

        String createdContainer = create(container);

        // assert that namespace declarations of metadata are still present
        // after create
        assertTrue(
            "Missing eterms namespace",
            createdContainer
                .contains("xmlns:eterms=\"http://purl.org/escidoc/metadata/terms/0.1/\""));
        assertTrue("Missing PURL",
            createdContainer
                .contains("xmlns:dc=\"http://purl.org/dc/elements/1.1\""));
    }

    /**
     * Test unexpected parser exception instead of InvalidXmlException during
     * create (see issue INFR-911).
     * 
     * @throws Exception
     *             Thrown if behavior is not as expected.
     */
    @Test(expected = InvalidXmlException.class)
    public void testInvalidXml() throws Exception {

        /*
         * The infrastructure has thrown an unexpected parser exception during
         * creation if a non XML datastructur is send (e.g. String).
         */
        create("laber-rababer");
    }

    /**
     * https://www.escidoc.org/jira/browse/INFR-1096
     * 
     * Create a container without a content-model-specific element.
     * 
     * @throws Exception
     *             If anything fails.
     */
    @Test
    public void testCreateContainerWithoutContentModel() throws Exception {
        Document container =
            EscidocRestSoapTestBase.getTemplateAsDocument(
                TEMPLATE_CONTAINER_PATH + this.path, "create_container.xml");

        deleteNodes(container, XPATH_CONTAINER_PROPERTIES_CMS);
        create(toString(container, false));
    }
}
