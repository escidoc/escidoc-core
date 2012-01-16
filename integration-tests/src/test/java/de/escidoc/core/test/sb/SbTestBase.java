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
package de.escidoc.core.test.sb;

import de.escidoc.core.test.EscidocAbstractTest;
import de.escidoc.core.test.common.client.servlet.ClientBase;
import de.escidoc.core.test.common.client.servlet.oum.OrganizationalUnitClient;
import de.escidoc.core.test.common.client.servlet.sb.SearchClient;
import de.escidoc.core.test.security.client.PWCallback;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import static org.junit.Assert.assertNotNull;

/**
 * Base class for tests of the mock implementation of the SB resources.
 *
 * @author Michael Hoppe
 */
public class SbTestBase extends EscidocAbstractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SbTestBase.class);

    private SearchClient sbClient = null;

    private final OrganizationalUnitClient organizationalUnitClient;

    private final OrganizationalUnitHelper orgUnitHelper;

    protected static final int ORGANIZATIONAL_UNIT_HANDLER_CODE = 5;

    public SbTestBase() {
        this.sbClient = new SearchClient();
        this.organizationalUnitClient = new OrganizationalUnitClient();
        this.orgUnitHelper = new OrganizationalUnitHelper();
    }

    /**
     * @param handlerCode the code of the handler to use.
     * @param xml         xml of object to create.
     * @return String xml-representation of the created object
     * @throws Exception e
     */
    public String create(final int handlerCode, final String xml) throws Exception {
        Object result = getClient(handlerCode).create(xml);
        return handleResult(result);
    }

    /**
     * @param handlerCode the code of the handler to use.
     * @param id          id to retrieve.
     * @return String xml-representation of the retrieved object
     * @throws Exception e
     */
    public String retrieve(final int handlerCode, final String id) throws Exception {
        Object result = getClient(handlerCode).retrieve(id);
        return handleResult(result);
    }

    /**
     * @param handlerCode the code of the handler to use.
     * @param id          id to retrieve.
     * @param xml         The ou-xml.
     * @return String xml-representation of the retrieved object
     * @throws Exception e
     */
    public String update(final int handlerCode, final String id, final String xml) throws Exception {
        Object result = getClient(handlerCode).update(id, xml);
        return handleResult(result);
    }

    /**
     * Prepares an organizational unit for a test.<br> The organizational unit is created and set into the specified
     * state.
     *
     * @param creatorUserHandle The eSciDoc user handle of the creator.
     * @param template          the xml of the org-unit.
     * @param status            The status to set for the item. If this is <code>null</code>, no item is created and
     *                          <code>null</code> is returned.
     * @param parentIds         parentIds of this orgUnit
     * @return Returns the XML representation of the created organizational unit.
     * @throws Exception If anything fails.
     */
    public String prepareOrgUnit(
        final String creatorUserHandle, final Document template, final String status, final String[] parentIds)
        throws Exception {

        if (status == null) {
            return null;
        }

        PWCallback.setHandle(creatorUserHandle);
        String createdXml = null;
        try {
            createdXml = create(ORGANIZATIONAL_UNIT_HANDLER_CODE, prepareOrgUnitData(template, parentIds));
        }
        catch (final Exception e) {
            EscidocAbstractTest.failException(e);
        }
        assertNotNull(createdXml);
        Document document = EscidocAbstractTest.getDocument(createdXml);
        final String objidValue = getObjidValue(document);

        if (!ORGANIZATIONAL_UNIT_STATUS_CREATED.equals(status) && !ORGANIZATIONAL_UNIT_STATUS_DELETED.equals(status)) {
            LOGGER.info("opening ou with id " + objidValue);
            createdXml = openOrgUnit(objidValue);
            if (!ORGANIZATIONAL_UNIT_STATUS_OPENED.equals(status)) {
                createdXml = closeOrgUnit(objidValue);
            }
        }
        else if (ORGANIZATIONAL_UNIT_STATUS_DELETED.equals(status)) {
            deleteOrgUnit(objidValue);
        }

        return createdXml;
    }

    /**
     * Prepares the data for an organizational unit.
     *
     * @param template  name of the template
     * @param parentIds parentIds of this orgUnit
     * @return Returns the xml representation of an organizational unit.
     * @throws Exception If anything fails.
     */
    protected String prepareOrgUnitData(final Document template, final String[] parentIds) throws Exception {

        setUniqueValue(template, XPATH_ORGANIZATIONAL_UNIT_TITLE);

        if (parentIds != null && parentIds.length > 0) {
            final int length = parentIds.length;
            final String[] parentValues = new String[length * 2];
            for (int i = 0; i < length; i++) {
                parentValues[i] = parentIds[i];
                parentValues[i + length] = null;
            }
            orgUnitHelper.insertParentsElement(template, XPATH_ORGANIZATIONAL_UNIT_MD_RECORDS, parentValues, false);
        }
        final String toBeCreatedXml = toString(template, true);

        return toBeCreatedXml;
    }

    /**
     * Handles the result of a base service access.
     *
     * @param result The result to handle.
     * @return Returns the xml response.
     * @throws Exception Thrown if anything fails.
     */
    private String handleResult(final Object result) throws Exception {

        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            xmlResult = EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);
            assertHttpStatusOfMethod("", httpRes);
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Opens on Organizational Unit.
     *
     * @param objidValue The object-id.
     * @return Returns the xml response.
     * @throws Exception Thrown if anything fails.
     */
    protected String openOrgUnit(final String objidValue) throws Exception {
        getOrganizationalUnitClient().open(objidValue, orgUnitHelper.getTheLastModificationParam(false, objidValue));
        String createdXml = retrieve(ORGANIZATIONAL_UNIT_HANDLER_CODE, objidValue);
        return createdXml;
    }

    /**
     * Closes on Organizational Unit.
     *
     * @param objidValue The object-id.
     * @return Returns the xml response.
     * @throws Exception Thrown if anything fails.
     */
    protected String closeOrgUnit(final String objidValue) throws Exception {
        getOrganizationalUnitClient().close(objidValue, orgUnitHelper.getTheLastModificationParam(false, objidValue));
        String createdXml = retrieve(ORGANIZATIONAL_UNIT_HANDLER_CODE, objidValue);
        return createdXml;
    }

    /**
     * Deletes on Organizational Unit.
     *
     * @param objidValue The object-id.
     * @throws Exception Thrown if anything fails.
     */
    protected void deleteOrgUnit(final String objidValue) throws Exception {
        getOrganizationalUnitClient().delete(objidValue);
    }

    /**
     * Closes on Organizational Unit.
     *
     * @param objidValue The object-id.
     * @param xml        The ou-xml.
     * @return Returns the xml response.
     * @throws Exception Thrown if anything fails.
     */
    protected String updateMdRecordsOfOrgUnit(final String objidValue, final String xml) throws Exception {
        getOrganizationalUnitClient().updateMdRecords(objidValue, xml);
        String createdXml = retrieve(ORGANIZATIONAL_UNIT_HANDLER_CODE, objidValue);
        return createdXml;
    }

    /**
     * Gets the corresponding client for the provided handler code.
     *
     * @param handlerCode The code identifying the handler.
     * @return Returns the client.
     * @throws Exception Thrown if anything fails.
     */
    protected ClientBase getClient(final int handlerCode) throws Exception {

        ClientBase client = null;
        switch (handlerCode) {

            case ORGANIZATIONAL_UNIT_HANDLER_CODE:
                client = organizationalUnitClient;
                break;
            default:
                throw new Exception("Unknown handler code [" + handlerCode + "]");
        }

        return client;
    }

    /**
     * @return Returns the organizationalUnitClient.
     */
    public OrganizationalUnitClient getOrganizationalUnitClient() {
        return organizationalUnitClient;
    }

    /**
     * @return Returns the searchClient.
     */
    public SearchClient getSearchClient() {
        return sbClient;
    }

}
