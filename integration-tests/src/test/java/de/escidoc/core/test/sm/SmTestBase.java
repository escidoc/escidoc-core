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

import de.escidoc.core.test.EscidocAbstractTest;
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
 */
public class SmTestBase extends EscidocAbstractTest {

    private StatisticDataClient statisticDataClient = null;

    private AggregationDefinitionClient aggregationDefinitionClient = null;

    private ReportDefinitionClient reportDefinitionClient = null;

    private ReportClient reportClient = null;

    private ScopeClient scopeClient = null;

    private PreprocessingClient preprocessingClient = null;

    private Pattern yearPattern =
        Pattern.compile("(?s)\\{yearReplacement\\}", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    private Matcher yearMatcher = yearPattern.matcher("");

    public SmTestBase() {
        this.statisticDataClient = new StatisticDataClient();
        this.aggregationDefinitionClient = new AggregationDefinitionClient();
        this.reportDefinitionClient = new ReportDefinitionClient();
        this.reportClient = new ReportClient();
        this.scopeClient = new ScopeClient();
        this.preprocessingClient = new PreprocessingClient();
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
     * @param xml String xml
     * @return Returns the primaryKey.
     * @throws Exception e
     */
    public String getPrimKey(final String xml) throws Exception {
        String id = null;
        Pattern hrefPattern = Pattern.compile("xlink:href=\"([^\"]*)\"");
        Matcher m = hrefPattern.matcher(xml);
        if (m.find()) {
            id = m.group(1);
            id = id.replaceFirst(".*\\/", "");
        }
        return id;
    }

    /**
     * replaces the objid of the element with given name from given xml with given primKey.
     *
     * @param xml         String xml
     * @param elementName String name of the element to replace objid
     * @param objid       String objid
     * @return Returns the replacedXml.
     */
    public String replaceElementPrimKey(final String xml, final String elementName, final String objid) {
        String replacedXml = null;
        replacedXml =
            xml.replaceFirst("(?s)(.*?<[^<]*?" + elementName + ".*?href=\"[^\"]*/).*?(\".*?>)", "$1" + objid + "$2");
        return replacedXml;
    }

    /**
     * replaces the id of the root-element from given xml with given primKey.
     *
     * @param xml  String xml
     * @param year String replacement year
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
     * @param xml                     String xml
     * @param aggregationDefinitionId aggregationDefinitionId
     * @return Returns the replacedXml.
     */
    public String replaceTableNames(final String xml, final String aggregationDefinitionId) {
        String idWithoutSpecialSigns = aggregationDefinitionId.replaceAll("\\:", "");
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
            fromClause = sql.replaceFirst("(?i).*?from(.*?)(where|order by|group by).*", "$1");
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
            replacedFromClause.append(idWithoutSpecialSigns).append("_").append(tables[i].trim());
        }
        replacedFromClause.append(" ");
        if (condition) {
            sql =
                sql.replaceFirst("(?i)(.*?from).*?((where|order by|group by).*)", "$1" + replacedFromClause.toString()
                    + "$2");
        }
        else {
            sql = sql.replaceFirst("(?i)(.*?from).*", "$1" + replacedFromClause.toString());
        }

        return pre + sql + post;
    }

    /**
     * Retrieve a Template as a String .<br> The used parser is NOT
     * namespace aware!
     *
     * @param path         The Path of the Template.
     * @param templateName The name of the template.
     * @return The String representation of the Template.
     * @throws Exception If anything fails.
     */
    public String getTemplateAsFixedScopeString(final String path, final String templateName) throws Exception {

        final Document document = getTemplateAsFixedScopeDocument(path, templateName);
        return toString(document, false);
    }

    /**
     * Retrieve a Template as a Document .<br> The used parser is NOT
     * namespace aware!
     *
     * @param path         The Path of the Template.
     * @param templateName The name of the template.
     * @return The String representation of the Template.
     * @throws Exception If anything fails.
     */
    public Document getTemplateAsFixedScopeDocument(final String path, final String templateName) throws Exception {

        final Document document = EscidocAbstractTest.getTemplateAsDocument(path, templateName);
        return document;
    }

    /**
     * Retrieve a Template as a String .<br> The used parser is NOT
     * namespace aware!
     *
     * @param path         The Path of the Template.
     * @param templateName The name of the template.
     * @return The String representation of the Template.
     * @throws Exception If anything fails.
     */
    public String getTemplateAsFixedAggregationDefinitionString(final String path, final String templateName)
        throws Exception {

        final Document document = getTemplateAsFixedAggregationDefinitionDocument(path, templateName);
        return toString(document, false);
    }

    /**
     * Retrieve a Template as a Document .<br> The used parser is NOT
     * namespace aware!
     *
     * @param path         The Path of the Template.
     * @param templateName The name of the template.
     * @return The String representation of the Template.
     * @throws Exception If anything fails.
     */
    public Document getTemplateAsFixedAggregationDefinitionDocument(final String path, final String templateName)
        throws Exception {

        final Document document = EscidocAbstractTest.getTemplateAsDocument(path, templateName);
        return document;
    }

    /**
     * Retrieve a Template as a String .<br> The used parser is NOT
     * namespace aware!
     *
     * @param path         The Path of the Template.
     * @param templateName The name of the template.
     * @return The String representation of the Template.
     * @throws Exception If anything fails.
     */
    public String getTemplateAsFixedReportDefinitionString(final String path, final String templateName)
        throws Exception {

        final Document document = getTemplateAsFixedReportDefinitionDocument(path, templateName);
        return toString(document, false);
    }

    /**
     * Retrieve a Template as a Document .<br> The used parser is NOT
     * namespace aware!
     *
     * @param path         The Path of the Template.
     * @param templateName The name of the template.
     * @return The String representation of the Template.
     * @throws Exception If anything fails.
     */
    public Document getTemplateAsFixedReportDefinitionDocument(final String path, final String templateName)
        throws Exception {

        final Document document = EscidocAbstractTest.getTemplateAsDocument(path, templateName);
        return document;
    }

    /**
     * Retrieve a Template as a String .<br> The used parser is NOT
     * namespace aware!
     *
     * @param path         The Path of the Template.
     * @param templateName The name of the template.
     * @return The String representation of the Template.
     * @throws Exception If anything fails.
     */
    public String getTemplateAsFixedReportParametersString(final String path, final String templateName)
        throws Exception {
        final Document document = getTemplateAsFixedReportParametersDocument(path, templateName);
        return toString(document, false);
    }

    /**
     * Retrieve a Template as a Document .<br> The used parser is NOT
     * namespace aware!
     *
     * @param path         The Path of the Template.
     * @param templateName The name of the template.
     * @return The String representation of the Template.
     * @throws Exception If anything fails.
     */
    public Document getTemplateAsFixedReportParametersDocument(final String path, final String templateName)
        throws Exception {
        return EscidocAbstractTest.getTemplateAsDocument(path, templateName);
    }

    /**
     * Retrieve a Template as a String .<br> The used parser is NOT
     * namespace aware!
     *
     * @param path         The Path of the Template.
     * @param templateName The name of the template.
     * @return The String representation of the Template.
     * @throws Exception If anything fails.
     */
    public String getTemplateAsFixedReportString(final String path, final String templateName) throws Exception {
        final Document document = getTemplateAsFixedReportDocument(path, templateName);
        return toString(document, false);
    }

    /**
     * Retrieve a Template as a Document .<br> The used parser is NOT
     * namespace aware!
     *
     * @param path         The Path of the Template.
     * @param templateName The name of the template.
     * @return The String representation of the Template.
     * @throws Exception If anything fails.
     */
    public Document getTemplateAsFixedReportDocument(final String path, final String templateName) throws Exception {
        return EscidocAbstractTest.getTemplateAsDocument(path, templateName);
    }

}
