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
package de.escidoc.core.sm.business.renderer;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.filter.RecordPacking;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ReportDefinitionXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import de.escidoc.core.sm.business.persistence.hibernate.ReportDefinition;
import de.escidoc.core.sm.business.persistence.hibernate.ReportDefinitionRole;
import de.escidoc.core.sm.business.renderer.interfaces.ReportDefinitionRendererInterface;
import de.escidoc.core.sm.business.util.comparator.ReportDefinitionRoleComparator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * ReportDefinition renderer implementation using the velocity template engine.
 *
 * @author Michael Hoppe
 */
@Service("eSciDoc.core.aa.business.renderer.VelocityXmlReportDefinitionRenderer")
public final class VelocityXmlReportDefinitionRenderer implements ReportDefinitionRendererInterface {

    /**
     * Private constructor to prevent initialization.
     */
    private VelocityXmlReportDefinitionRenderer() {
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String render(final ReportDefinition reportDefinition) throws SystemException {
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootReportDefinition", XmlTemplateProviderConstants.TRUE);
        addReportDefinitionNamespaceValues(values);
        addReportDefinitionValues(reportDefinition, values);
        return getReportDefinitionXmlProvider().getReportDefinitionXml(values);
    }

    /**
     * Adds the values of the {@link ReportDefinition} to the provided {@link Map}.
     *
     * @param reportDefinition The {@link ReportDefinition}.
     * @param values           The {@link Map} to add the values to.
     * @throws SystemException Thrown in case of an internal error.
     */
    private static void addReportDefinitionValues(
        final ReportDefinition reportDefinition, final Map<String, Object> values) {
        DateTime createDateTime = new DateTime(reportDefinition.getCreationDate());
        createDateTime = createDateTime.withZone(DateTimeZone.UTC);
        final String create = createDateTime.toString(Constants.TIMESTAMP_FORMAT);
        DateTime lmdDateTime = new DateTime(reportDefinition.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        final String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);

        values.put("reportDefinitionCreationDate", create);
        values.put("reportDefinitionCreatedById", reportDefinition.getCreatorId());
        values.put("reportDefinitionCreatedByTitle", "user " + reportDefinition.getCreatorId());
        values.put("reportDefinitionCreatedByHref", XmlUtility.getUserAccountHref(reportDefinition.getCreatorId()));
        values.put("reportDefinitionLastModificationDate", lmd);
        values.put("reportDefinitionModifiedById", reportDefinition.getModifiedById());
        values.put("reportDefinitionModifiedByTitle", "user " + reportDefinition.getModifiedById());
        values.put("reportDefinitionModifiedByHref", XmlUtility.getUserAccountHref(reportDefinition.getModifiedById()));
        values.put("reportDefinitionId", reportDefinition.getId());
        values.put("reportDefinitionName", reportDefinition.getName());
        values.put("reportDefinitionHref", XmlUtility.getReportDefinitionHref(reportDefinition.getId()));
        values.put("reportDefinitionScopeId", reportDefinition.getScope().getId());
        values.put("reportDefinitionScopeTitle", reportDefinition.getScope().getName());
        values.put("reportDefinitionScopeHref", XmlUtility.getScopeHref(reportDefinition.getScope().getId()));
        values.put("reportDefinitionSql", reportDefinition.getSql());
        addReportDefinitionRoleValues(reportDefinition.getReportDefinitionRoles(), values);
    }

    /**
     * Adds the values of the {@link ReportDefinitionRole} to the provided {@link Map}.
     *
     * @param reportDefinitionRoles set of reportDefinitionRoles.
     * @param values                The {@link Map} to add the values to.
     */
    private static void addReportDefinitionRoleValues(
        final Collection<ReportDefinitionRole> reportDefinitionRoles, final Map<String, Object> values) {
        if (reportDefinitionRoles != null && !reportDefinitionRoles.isEmpty()) {
            final Collection<HashMap<String, String>> reportDefinitionRolesVm =
                new ArrayList<HashMap<String, String>>();
            final Collection<ReportDefinitionRole> sortedReportDefinitionRoles =
                new TreeSet<ReportDefinitionRole>(new ReportDefinitionRoleComparator());
            sortedReportDefinitionRoles.addAll(reportDefinitionRoles);
            for (final ReportDefinitionRole reportDefinitionRole : sortedReportDefinitionRoles) {
                final HashMap<String, String> roleMap = new HashMap<String, String>();
                roleMap.put("id", reportDefinitionRole.getRoleId());
                roleMap.put("title", "role " + reportDefinitionRole.getRoleId());
                roleMap.put("href", XmlUtility.getRoleHref(reportDefinitionRole.getRoleId()));

                reportDefinitionRolesVm.add(roleMap);
            }
            values.put("reportDefinitionRoles", reportDefinitionRolesVm);
        }
    }

    /**
     * See Interface for functional description.
     *
     * @param recordPacking A string to determine how the record should be escaped in the response. Defined values are
     *                      'string' and 'xml'. The default is 'xml'.
     * @see de.escidoc.core.sm.business.renderer.interfaces.ReportDefinitionRendererInterface
     *      #renderReportDefinitions(de.escidoc.core.sm.business.ReportDefinition)
     */
    @Override
    public String renderReportDefinitions(
        final Collection<ReportDefinition> reportDefinitions, final RecordPacking recordPacking) throws SystemException {

        final Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootReportDefinition", XmlTemplateProviderConstants.FALSE);
        values.put("reportDefinitionListTitle", "Report Definition List");
        values.put("recordPacking", recordPacking);
        addReportDefinitionNamespaceValues(values);
        addReportDefinitionListNamespaceValues(values);

        final List<Map<String, Object>> reportDefinitionsValues;
        if (reportDefinitions != null) {
            reportDefinitionsValues = new ArrayList<Map<String, Object>>(reportDefinitions.size());
            for (final ReportDefinition reportDefinition : reportDefinitions) {
                final Map<String, Object> reportDefinitionValues = new HashMap<String, Object>();
                addReportDefinitionNamespaceValues(reportDefinitionValues);
                addReportDefinitionValues(reportDefinition, reportDefinitionValues);
                reportDefinitionsValues.add(reportDefinitionValues);
            }
        }
        else {
            reportDefinitionsValues = new ArrayList<Map<String, Object>>();
        }
        values.put("reportDefinitions", reportDefinitionsValues);
        return getReportDefinitionXmlProvider().getReportDefinitionsSrwXml(values);
    }

    /**
     * Adds the report definition name space values.
     *
     * @param values The {@link Map} to that the values shall be added.
     */
    private static void addReportDefinitionNamespaceValues(final Map<String, Object> values) {
        addEscidocBaseUrl(values);
        values.put("reportDefinitionNamespacePrefix", Constants.REPORT_DEFINITION_NS_PREFIX);
        values.put("reportDefinitionNamespace", Constants.REPORT_DEFINITION_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);
    }

    /**
     * Adds the report definition list name space values.
     *
     * @param values The {@link Map} to that the values shall be added.
     */
    private static void addReportDefinitionListNamespaceValues(final Map<String, Object> values) {
        addEscidocBaseUrl(values);
        values.put("searchResultNamespace", Constants.SEARCH_RESULT_NS_URI);
        values.put("reportDefinitionListNamespacePrefix", Constants.REPORT_DEFINITION_LIST_NS_PREFIX);
        values.put("reportDefinitionListNamespace", Constants.REPORT_DEFINITION_LIST_NS_URI);
    }

    /**
     * Adds the escidoc base URL to the provided map.
     *
     * @param values The map to add values to.
     */
    private static void addEscidocBaseUrl(final Map<String, Object> values) {

        values.put(XmlTemplateProviderConstants.VAR_ESCIDOC_BASE_URL, XmlUtility.getEscidocBaseUrl());
    }

    /**
     * Gets the {@code ReportDefinitionXmlProvider} object.
     *
     * @return Returns the {@code ReportDefinitionXmlProvider} object.
     */
    private static ReportDefinitionXmlProvider getReportDefinitionXmlProvider() {

        return ReportDefinitionXmlProvider.getInstance();
    }

}
