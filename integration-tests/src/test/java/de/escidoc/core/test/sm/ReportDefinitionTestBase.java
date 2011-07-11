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
package de.escidoc.core.test.sm;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.Map;

/**
 * Base class for reportDefinition tests.
 *
 * @author Michael Hoppe
 */
public class ReportDefinitionTestBase extends SmTestBase {

    /**
     * Test creating a reportDefinition.
     *
     * @param dataXml The xml representation of the reportDefinition.
     * @return The created ReportDefinition.
     * @throws Exception If anything fails.
     */
    @Override
    public String create(final String dataXml) throws Exception {

        Object result = getReportDefinitionClient().create(dataXml);
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
     * Test deleting an reportDefinition from the mock framework.
     *
     * @param id The id of the reportDefinition.
     * @throws Exception If anything fails.
     */
    @Override
    public void delete(final String id) throws Exception {

        Object result = getReportDefinitionClient().delete(id);
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            assertHttpStatusOfMethod("", httpRes);
        }
    }

    /**
     * Test updating an reportDefinition of the mock framework.
     *
     * @param id  The id of the reportDefinition.
     * @param xml The xml representation of the reportDefinition.
     * @return The updated report description.
     * @throws Exception If anything fails.
     */
    @Override
    public String update(final String id, final String xml) throws Exception {

        Object result = getReportDefinitionClient().update(id, xml);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            xmlResult = EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);
            if (httpRes.getStatusLine().getStatusCode() >= 300 || httpRes.getStatusLine().getStatusCode() < 200) {
                throw new Exception(xmlResult);
            }

        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Test retrieving an reportDefinition from the mock framework.
     *
     * @param id The id of the reportDefinition.
     * @return The retrieved reportDefinition.
     * @throws Exception If anything fails.
     */
    @Override
    public String retrieve(final String id) throws Exception {

        Object result = getReportDefinitionClient().retrieve(id);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            assertHttpStatusOfMethod("", httpRes);
            xmlResult = EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

    /**
     * Test retrieving the list of all reportDefinitions from the mock framework.
     *
     * @param filter filter as CQL query
     * @return The retrieved reportDefinitions as xml.
     * @throws Exception If anything fails.
     */
    public String retrieveReportDefinitions(final Map<String, String[]> filter) throws Exception {

        Object result = getReportDefinitionClient().retrieveReportDefinitions(filter);
        String xmlResult = null;
        if (result instanceof HttpResponse) {
            HttpResponse httpRes = (HttpResponse) result;
            assertHttpStatusOfMethod("", httpRes);
            xmlResult = EntityUtils.toString(httpRes.getEntity(), HTTP.UTF_8);
        }
        else if (result instanceof String) {
            xmlResult = (String) result;
        }
        return xmlResult;
    }

}
