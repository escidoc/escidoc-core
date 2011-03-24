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

import de.escidoc.core.test.EscidocRestSoapTestBase;
import de.escidoc.core.test.common.client.servlet.Constants;
import de.escidoc.core.test.common.client.servlet.sm.AggregationDefinitionClient;
import de.escidoc.core.test.common.client.servlet.sm.PreprocessingClient;
import de.escidoc.core.test.common.client.servlet.sm.ReportClient;
import de.escidoc.core.test.common.client.servlet.sm.ReportDefinitionClient;
import de.escidoc.core.test.common.client.servlet.sm.ScopeClient;
import de.escidoc.core.test.common.client.servlet.sm.StatisticDataClient;
import org.w3c.dom.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for tests of the mock implementation of the SB resources.
 * 
 * @author Michael Hoppe
 * 
 */
public class SmTestBase extends EscidocRestSoapTestBase {

    private StatisticDataClient statisticDataClient = null;

    private AggregationDefinitionClient aggregationDefinitionClient = null;

    private ReportDefinitionClient reportDefinitionClient = null;

    private ReportClient reportClient = null;

    private ScopeClient scopeClient = null;
    
    private PreprocessingClient preprocessingClient = null;
    
    private Pattern yearPattern = Pattern.compile(
            "(?s)\\{yearReplacement\\}", 
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    private Matcher yearMatcher = yearPattern.matcher("");


    /**
     * @param transport
     *            The transport identifier.
     */
    public SmTestBase(final int transport) {
        super(transport);
        this.statisticDataClient = new StatisticDataClient(transport);
        this.aggregationDefinitionClient =
            new AggregationDefinitionClient(transport);
        this.reportDefinitionClient = new ReportDefinitionClient(transport);
        this.reportClient = new ReportClient(transport);
        this.scopeClient = new ScopeClient(transport);
        this.preprocessingClient = new PreprocessingClient(transport);
    }

    /**
     * @return Returns the statisticDataClient.
     */
    public StatisticDataClient getStatisticDataClient() {
        return statisticDataClient;
    }

    /**
     * @return Returns the aggregationDefinitionClient.
     */
    public AggregationDefinitionClient getAggregationDefinitionClient() {
        return aggregationDefinitionClient;
    }

    /**
     * @return Returns the reportDefinitionClient.
     */
    public ReportDefinitionClient getReportDefinitionClient() {
        return reportDefinitionClient;
    }

    /**
     * @return Returns the reportClient.
     */
    public ReportClient getReportClient() {
        return reportClient;
    }

    /**
     * @return Returns the scopeClient.
     */
    public ScopeClient getScopeClient() {
        return scopeClient;
    }

    /**
     * @return Returns the preprocessingClient.
     */
    public PreprocessingClient getPreprocessingClient() {
        return preprocessingClient;
    }

    /**
     * extracts the id of the root-element from given xml.
     * 
     * @param xml
     *            String xml
     * @return Returns the primaryKey.
     * @throws Exception e
     */
    public String getPrimKey(final String xml) throws Exception {
        String id = null;
        if (getTransport() == Constants.TRANSPORT_SOAP) {
            Pattern objidAttributePattern = Pattern.compile("objid=\"([^\"]*)\"");
            Matcher m = objidAttributePattern.matcher(xml);
            if (m.find()) {
                id = m.group(1);
            }
        }
        else {
            Pattern hrefPattern = Pattern.compile("xlink:href=\"([^\"]*)\"");
            Matcher m = hrefPattern.matcher(xml);
            if (m.find()) {
                id = m.group(1);
                id = id.replaceFirst(".*\\/", "");
            }
        }
        return id;
    }

    /**
     * replaces the objid of the element with given name 
     * from given xml with given primKey.
     * 
     * @param xml
     *            String xml
     * @param elementName
     *            String name of the element to replace objid
     * @param objid
     *            String objid
     * @return Returns the replacedXml.
     */
    public String replaceElementPrimKey(
            final String xml, 
            final String elementName, final String objid) {
        String replacedXml = null;
        if (getTransport() == Constants.TRANSPORT_SOAP) {
            replacedXml =
                xml.replaceFirst("(?s)(.*?<[^<]*?" 
                    + elementName 
                    + ".*?objid=\").*?(\".*?>)", "$1"
                    + objid + "$2");
        } else {
            replacedXml =
                xml.replaceFirst("(?s)(.*?<[^<]*?" 
                    + elementName 
                    + ".*?href=\"[^\"]*/).*?(\".*?>)", "$1"
                    + objid + "$2");
        }
        return replacedXml;
    }

    /**
     * replaces the id of the root-element from given xml with given primKey.
     * 
     * @param xml
     *            String xml
     * @param year
     *            String replacement year
     * @return Returns the replacedXml.
     */
    public String replaceYear(final String xml, final String year) {
        yearMatcher = yearMatcher.reset(xml);
        String replacedXml = yearMatcher.replaceAll(year);
        return replacedXml;
    }

    /**
     * replaces tablenames in reportdefinition.
     * 
     * @param xml
     *            String xml
     * @param aggregationDefinitionId
     *            aggregationDefinitionId
     * @return Returns the replacedXml.
     */
    public String replaceTableNames(final String xml, 
            final String aggregationDefinitionId) {
        String idWithoutSpecialSigns = 
            aggregationDefinitionId.replaceAll("\\:", "");
        boolean condition = false;
        String pre = xml.replaceFirst("(?s)(.*?<sql>).*", "$1");
        String post = xml.replaceFirst("(?s).*?(<\\/sql>.*)", "$1");
        String sql = xml.replaceFirst("(?s).*?<sql>(.*?)<\\/sql>.*", "$1");

        sql = sql.replaceAll("\\s+", " ");
        if (sql.matches("(?i).* (where|order by|group by) .*")) {
            condition = true;
        }
        String fromClause;
        if (condition) {
            fromClause =
                sql.replaceFirst(
                    "(?i).*?from(.*?)(where|order by|group by).*", "$1");
        }
        else {
            fromClause = sql.replaceFirst("(?i).*?from(.*)", "$1");
        }
        String[] tables = fromClause.split(",");
        StringBuffer replacedFromClause = new StringBuffer(" ");
        for (int i = 0; i < tables.length; i++) {
            if (i > 0) {
                replacedFromClause.append(",");
            }
            replacedFromClause
                .append(idWithoutSpecialSigns).append("_")
                .append(tables[i].trim());
        }
        replacedFromClause.append(" ");
        if (condition) {
            sql =
                sql.replaceFirst(
                    "(?i)(.*?from).*?((where|order by|group by).*)",
                    "$1" + replacedFromClause.toString() + "$2");
        }
        else {
            sql =
                sql.replaceFirst("(?i)(.*?from).*",
                    "$1" + replacedFromClause.toString());
        }

        return pre + sql + post;
    }

    /**
     * Retrieve a Template as a String and fixes the "link" attributes in case
     * of SOAP.<br>
     * The used parser is NOT namespace aware!
     * 
     * @param path
     *            The Path of the Template.
     * @param templateName
     *            The name of the template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public String getTemplateAsFixedScopeString(
        final String path, final String templateName) throws Exception {

        final Document document =
            getTemplateAsFixedScopeDocument(path, templateName);
        return toString(document, false);
    }

    /**
     * Retrieve a Template as a Document and fixes the "link" attributes in case
     * of SOAP.<br>
     * The used parser is NOT namespace aware!
     * 
     * @param path
     *            The Path of the Template.
     * @param templateName
     *            The name of the template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public Document getTemplateAsFixedScopeDocument(
        final String path, final String templateName) throws Exception {

        final Document document =
            EscidocRestSoapTestBase.getTemplateAsDocument(path, templateName);
        fixScopeDocument(document);
        return document;
    }

    /**
     * Fixes the "link" attributes in case of SOAP.
     * 
     * @param document
     *            The document to fix.
     * @return Returns the provided (fixed) document.
     * @throws Exception
     *             If anything fails.
     */
    public Document fixScopeDocument(final Document document) throws Exception {

        fixLinkAttributes(document, XPATH_SCOPE);
        return document;
    }

    /**
     * Retrieve a Template as a String and fixes the "link" attributes in case
     * of SOAP.<br>
     * The used parser is NOT namespace aware!
     * 
     * @param path
     *            The Path of the Template.
     * @param templateName
     *            The name of the template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public String getTemplateAsFixedAggregationDefinitionString(
        final String path, final String templateName) throws Exception {

        final Document document =
            getTemplateAsFixedAggregationDefinitionDocument(path, templateName);
        return toString(document, false);
    }

    /**
     * Retrieve a Template as a Document and fixes the "link" attributes in case
     * of SOAP.<br>
     * The used parser is NOT namespace aware!
     * 
     * @param path
     *            The Path of the Template.
     * @param templateName
     *            The name of the template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public Document getTemplateAsFixedAggregationDefinitionDocument(
        final String path, final String templateName) throws Exception {

        final Document document =
            EscidocRestSoapTestBase.getTemplateAsDocument(path, templateName);
        fixAggregationDefinitionDocument(document);
        return document;
    }

    /**
     * Fixes the "link" attributes in case of SOAP.
     * 
     * @param document
     *            The document to fix.
     * @return Returns the provided (fixed) document.
     * @throws Exception
     *             If anything fails.
     */
    public Document fixAggregationDefinitionDocument(
            final Document document) throws Exception {

        fixLinkAttributes(document, XPATH_AGGREGATION_DEFINITION);
        fixLinkAttributes(document, XPATH_AGGREGATION_DEFINITION_SCOPE);
        return document;
    }

    /**
     * Retrieve a Template as a String and fixes the "link" attributes in case
     * of SOAP.<br>
     * The used parser is NOT namespace aware!
     * 
     * @param path
     *            The Path of the Template.
     * @param templateName
     *            The name of the template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public String getTemplateAsFixedReportDefinitionString(
        final String path, final String templateName) throws Exception {

        final Document document =
            getTemplateAsFixedReportDefinitionDocument(path, templateName);
        return toString(document, false);
    }

    /**
     * Retrieve a Template as a Document and fixes the "link" attributes in case
     * of SOAP.<br>
     * The used parser is NOT namespace aware!
     * 
     * @param path
     *            The Path of the Template.
     * @param templateName
     *            The name of the template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public Document getTemplateAsFixedReportDefinitionDocument(
        final String path, final String templateName) throws Exception {

        final Document document =
            EscidocRestSoapTestBase.getTemplateAsDocument(path, templateName);
        fixReportDefinitionDocument(document);
        return document;
    }

    /**
     * Fixes the "link" attributes in case of SOAP.
     * 
     * @param document
     *            The document to fix.
     * @return Returns the provided (fixed) document.
     * @throws Exception
     *             If anything fails.
     */
    public Document fixReportDefinitionDocument(
            final Document document) throws Exception {

        fixLinkAttributes(document, XPATH_REPORT_DEFINITION);
        fixLinkAttributes(document, XPATH_REPORT_DEFINITION_SCOPE);
        return document;
    }

    /**
     * Retrieve a Template as a String and fixes the "link" attributes in case
     * of SOAP.<br>
     * The used parser is NOT namespace aware!
     * 
     * @param path
     *            The Path of the Template.
     * @param templateName
     *            The name of the template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public String getTemplateAsFixedReportParametersString(
        final String path, final String templateName) throws Exception {

        final Document document =
            getTemplateAsFixedReportParametersDocument(path, templateName);
        return toString(document, false);
    }

    /**
     * Retrieve a Template as a Document and fixes the "link" attributes in case
     * of SOAP.<br>
     * The used parser is NOT namespace aware!
     * 
     * @param path
     *            The Path of the Template.
     * @param templateName
     *            The name of the template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public Document getTemplateAsFixedReportParametersDocument(
        final String path, final String templateName) throws Exception {

        final Document document =
            EscidocRestSoapTestBase.getTemplateAsDocument(path, templateName);
        fixReportParametersDocument(document);
        return document;
    }

    /**
     * Fixes the "link" attributes in case of SOAP.
     * 
     * @param document
     *            The document to fix.
     * @return Returns the provided (fixed) document.
     * @throws Exception
     *             If anything fails.
     */
    public Document fixReportParametersDocument(
            final Document document) throws Exception {

        fixLinkAttributes(document, XPATH_REPORT_PARAMETERS_REPORT_DEFINITION);
        return document;
    }

    /**
     * Retrieve a Template as a String and fixes the "link" attributes in case
     * of SOAP.<br>
     * The used parser is NOT namespace aware!
     * 
     * @param path
     *            The Path of the Template.
     * @param templateName
     *            The name of the template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public String getTemplateAsFixedReportString(
        final String path, final String templateName) throws Exception {

        final Document document =
            getTemplateAsFixedReportDocument(path, templateName);
        return toString(document, false);
    }

    /**
     * Retrieve a Template as a Document and fixes the "link" attributes in case
     * of SOAP.<br>
     * The used parser is NOT namespace aware!
     * 
     * @param path
     *            The Path of the Template.
     * @param templateName
     *            The name of the template.
     * @return The String representation of the Template.
     * @throws Exception
     *             If anything fails.
     */
    public Document getTemplateAsFixedReportDocument(
        final String path, final String templateName) throws Exception {

        final Document document =
            EscidocRestSoapTestBase.getTemplateAsDocument(path, templateName);
        fixReportDocument(document);
        return document;
    }

    /**
     * Fixes the "link" attributes in case of SOAP.
     * 
     * @param document
     *            The document to fix.
     * @return Returns the provided (fixed) document.
     * @throws Exception
     *             If anything fails.
     */
    public Document fixReportDocument(
            final Document document) throws Exception {

        fixLinkAttributes(document, XPATH_REPORT_REPORT_DEFINITION);
        return document;
    }

}
