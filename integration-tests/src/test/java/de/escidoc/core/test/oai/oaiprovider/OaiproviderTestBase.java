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
package de.escidoc.core.test.oai.oaiprovider;

import static org.junit.Assert.fail;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.TaskParamFactory;
import de.escidoc.core.test.common.client.servlet.interfaces.ResourceHandlerClientInterface;
import de.escidoc.core.test.common.client.servlet.oai.OaiProviderClient;
import de.escidoc.core.test.common.client.servlet.om.ContainerClient;
import de.escidoc.core.test.common.client.servlet.om.IngestClient;
import de.escidoc.core.test.common.client.servlet.om.ItemClient;
import de.escidoc.core.test.oai.setdefinition.SetDefinitionTestBase;

/**
 * Base class for tests of the OaiProvider.
 *
 * @author Michael Hoppe
 */
public class OaiproviderTestBase extends SetDefinitionTestBase {

    protected final IngestClient ingestClient;

    protected final ContainerClient containerClient;

    protected final ItemClient itemClient;

    protected final OaiProviderClient oaiProviderClient;

    private final String TYPE_XPATH = "//md-record[@name='oaipmhtest']/dc/type";

    private final String MDRECORDNAME_XPATH = "//md-record[@name='oaipmhtest']/@name";

    //Set-Definitions are only definable on md-record with name=escidoc!!!
    private final String ESCIDOCMDRECORDTYPE_XPATH = "//md-record[@name='escidoc']/metadata/type";

    private static final Pattern OBJECT_PATTERN =
        Pattern.compile("<objid resourceType=\"([^\"][^\"]*)\">(escidoc:\\d+)</objid>", Pattern.MULTILINE);

    /**
     * @param transport The transport identifier.
     */
    public OaiproviderTestBase() {
        this.ingestClient = new IngestClient();
        this.containerClient = new ContainerClient();
        this.itemClient = new ItemClient();
        this.oaiProviderClient = new OaiProviderClient();
    }

    /**
     * Successfully ingests an object.
     *
     * @param templateName The name of the template.
     * @return Returns the Item document.
     * @throws Exception If anything fails
     */
    public String create(
        final String templatePath, final String templateName, final String type, final String mdrecordName,
        final String escidocMdrecordType) throws Exception {

        final Document toBeCreatedDocument = EscidocAbstractTest.getTemplateAsDocument(templatePath, templateName);

        if (type != null) {
            substitute(toBeCreatedDocument, TYPE_XPATH, type);
        }

        if (mdrecordName != null) {
            substitute(toBeCreatedDocument, MDRECORDNAME_XPATH, mdrecordName);
        }

        if (escidocMdrecordType != null) {
            substitute(toBeCreatedDocument, ESCIDOCMDRECORDTYPE_XPATH, escidocMdrecordType);
        }

        String createdItemXml = null;
        try {
            createdItemXml = handleXmlResult(ingestClient.ingest(toString(toBeCreatedDocument, false)));
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        return createdItemXml;
    }

    /**
     * Successfully withdraws an item.
     *
     * @param itemId The id of the item.
     * @return Returns the Item document.
     * @throws Exception If anything fails
     */
    public String withdrawItem(final String itemId) throws Exception {
        return handleXmlResult(this.itemClient.withdraw(itemId, TaskParamFactory.getStatusTaskParam(
            getLastModificationDateValue2(EscidocAbstractTest.getDocument(handleXmlResult(this.itemClient
                .retrieve(itemId)))), "Some withdraw comment")));
    }

    /**
     * Successfully withdraws an container.
     *
     * @param containerId The id of the container.
     * @return Returns the Container document.
     * @throws Exception If anything fails
     */
    public String withdrawContainer(final String containerId) throws Exception {
        return handleXmlResult(this.containerClient.withdraw(containerId, TaskParamFactory.getStatusTaskParam(
            getLastModificationDateValue2(EscidocAbstractTest.getDocument(handleXmlResult(this.containerClient
                .retrieve(containerId)))), "Some withdraw comment")));
    }

    /**
     * Gets objid from return-xml of ingested resource.
     *
     * @param ingestReturnXml The return-xml of ingested resource.
     * @return Returns objid.
     * @throws Exception If anything fails
     */
    public String getObjid(final String ingestReturnXml) throws Exception {
        Matcher matcher = OBJECT_PATTERN.matcher(ingestReturnXml);

        if (matcher.find()) {
            return matcher.group(2);
        }
        else {
            fail("no match for item found, return value of ingest could not " + "be matched successfully.");
        }
        return null;
    }

    /**
     * Inserts a unique label into the provided document by adding the current timestamp to the contained label.
     *
     * @param document The document.
     * @return The inserted login name.
     * @throws Exception If anything fails.
     */
    protected String insertUniqueSetSpecification(final Document document) throws Exception {

        assertXmlExists("No specification found in template data. ", document, "/set-definition/specification");
        final Node specNode = selectSingleNode(document, "/set-definition/specification");
        String specification = specNode.getTextContent().trim();
        specification += System.currentTimeMillis();

        specNode.setTextContent(specification);

        return specification;
    }

    /**
     * @return Returns the itemClient
     */
    @Override
    public ResourceHandlerClientInterface getClient() {

        return getSetDefinitionClient();
    }

    /**
     * Retrieve set definitions.
     *
     * @param filter The filter query.
     * @return a list of set definitions as SRW response.
     * @throws Exception Thrown if an internal error occurred.
     */
    public String retrieveSetDefinitions(final Map<String, String[]> filter) throws Exception {
        Object result = getSetDefinitionClient().retrieveSetDefinitions(filter);

        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            assertHttpStatusOfMethod("", httpRes);
        }
        return handleXmlResult(result);
    }
}
