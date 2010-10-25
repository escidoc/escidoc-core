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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ReportDefinitionXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.sm.business.persistence.hibernate.ReportDefinition;
import de.escidoc.core.sm.business.persistence.hibernate.ReportDefinitionRole;
import de.escidoc.core.sm.business.renderer.interfaces.ReportDefinitionRendererInterface;
import de.escidoc.core.sm.business.util.comparator.ReportDefinitionRoleComparator;

/**
 * ReportDefinition renderer implementation using the velocity template engine.
 * 
 * @author MIH
 * @spring.bean 
 *              id="eSciDoc.core.aa.business.renderer.VelocityXmlReportDefinitionRenderer"
 * @aa
 */
public final class VelocityXmlReportDefinitionRenderer
    implements ReportDefinitionRendererInterface {

    /**
     * Private constructor to prevent initialization.
     */
    private VelocityXmlReportDefinitionRenderer() {
    }

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param reportDefinition
     * @return
     * @throws SystemException
     * @see de.escidoc.core.sm.business.renderer.interfaces.
     *      ReportDefinitionRendererInterface#render(Map)
     * @sm
     */
    public String render(
            final ReportDefinition reportDefinition) 
                                        throws SystemException {
        Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootReportDefinition", XmlTemplateProvider.TRUE);
        addReportDefinitionNamespaceValues(values);
        addReportDefinitionValues(reportDefinition, values);

        final String ret =
            getReportDefinitionXmlProvider()
                .getReportDefinitionXml(values);

        return ret;
    }

    /**
     * Adds the values of the {@link ReportDefinition} to the provided {@link Map}.
     * 
     * @param reportDefinition
     *            The {@link ReportDefinition}.
     * @param values
     *            The {@link Map} to add the values to.
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    private void addReportDefinitionValues(
        final ReportDefinition reportDefinition, 
        final Map<String, Object> values)
        throws SystemException {
        DateTime createDateTime =
            new DateTime(reportDefinition.getCreationDate());
        createDateTime = createDateTime.withZone(DateTimeZone.UTC);
        String create = createDateTime.toString(Constants.TIMESTAMP_FORMAT);
        DateTime lmdDateTime =
            new DateTime(reportDefinition.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);

        values.put("reportDefinitionCreationDate", create);
        values.put("reportDefinitionCreatedById", 
            reportDefinition.getCreatorId());
        values.put("reportDefinitionCreatedByTitle", 
            "user " + reportDefinition.getCreatorId());
        values.put("reportDefinitionCreatedByHref", 
            XmlUtility.getUserAccountHref(reportDefinition.getCreatorId()));
        values.put("reportDefinitionLastModificationDate", lmd);
        values.put("reportDefinitionModifiedById", 
            reportDefinition.getModifiedById());
        values.put("reportDefinitionModifiedByTitle", 
            "user " + reportDefinition.getModifiedById());
        values.put("reportDefinitionModifiedByHref", 
            XmlUtility.getUserAccountHref(reportDefinition.getModifiedById()));
        values.put("reportDefinitionId", reportDefinition.getId());
        values.put("reportDefinitionName", reportDefinition.getName());
        values.put("reportDefinitionHref", 
            XmlUtility.getReportDefinitionHref(
                reportDefinition.getId()));
        values.put("reportDefinitionScopeId", 
                reportDefinition.getScope().getId());
        values.put("reportDefinitionScopeTitle", 
            reportDefinition.getScope().getName());
        values.put("reportDefinitionScopeHref", 
            XmlUtility.getScopeHref(reportDefinition.getScope().getId()));
        values.put("reportDefinitionSql", 
                reportDefinition.getSql());
        addReportDefinitionRoleValues(
                reportDefinition.getReportDefinitionRoles(), values);
    }
    
    /**
     * Adds the values of the {@link ReportDefinitionRole} to the provided {@link Map}.
     * 
     * @param reportDefinitionRoles
     *            set of reportDefinitionRoles.
     * @param values
     *            The {@link Map} to add the values to.
     */
    private void addReportDefinitionRoleValues(
        final Set<ReportDefinitionRole> reportDefinitionRoles, 
        final Map<String, Object> values) {
        if (reportDefinitionRoles != null 
                && !reportDefinitionRoles.isEmpty()) {
            Vector<HashMap<String, String>> reportDefinitionRolesVm = 
                new Vector<HashMap<String, String>>();
            TreeSet<ReportDefinitionRole> 
            sortedReportDefinitionRoles = 
                new TreeSet<ReportDefinitionRole>(
                new ReportDefinitionRoleComparator());
            sortedReportDefinitionRoles.addAll(
                    reportDefinitionRoles);
            for (ReportDefinitionRole reportDefinitionRole 
                                        : sortedReportDefinitionRoles) {
                HashMap<String, String> roleMap = new HashMap<String, String>();
                roleMap.put("id", reportDefinitionRole.getRoleId());
                roleMap.put("title", "role " + reportDefinitionRole.getRoleId());
                roleMap.put("href", XmlUtility.getRoleHref(
                                reportDefinitionRole.getRoleId()));
                
                reportDefinitionRolesVm.add(roleMap);
            }
            values.put("reportDefinitionRoles", reportDefinitionRolesVm);
        }
    }

    /**
     * See Interface for functional description.
     * 
     * @param reportDefinitions
     * @param asSrw
     * 
     * @return
     * @throws WebserverSystemException
     * @see de.escidoc.core.aa.business.renderer.interfaces.
     *      ReportDefinitionRendererInterface
     *      #renderReportDefinitions(de.escidoc.core.sm.business.ReportDefinition)
     * @sm
     */
    public String renderReportDefinitions(
        final Collection<ReportDefinition> reportDefinitions, 
        final boolean asSrw)
        throws SystemException {

        Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootReportDefinition", XmlTemplateProvider.FALSE);
        values.put("reportDefinitionListTitle", 
        "Report Definition List");
        addReportDefinitionNamespaceValues(values);
        addReportDefinitionListNamespaceValues(values);

        final List<Map<String, Object>> reportDefinitionsValues;
        if (reportDefinitions != null) {
            reportDefinitionsValues = new ArrayList<Map<String, Object>>(reportDefinitions.size());
            for (ReportDefinition reportDefinition 
                                    : reportDefinitions) {
                Map<String, Object> reportDefinitionValues =
                        new HashMap<String, Object>();
                addReportDefinitionNamespaceValues(
                        reportDefinitionValues);
                addReportDefinitionValues(
                        reportDefinition, reportDefinitionValues);
                reportDefinitionsValues.add(reportDefinitionValues);
            }
        } else {
            reportDefinitionsValues = new ArrayList<Map<String, Object>>();
        }
        values.put("reportDefinitions", reportDefinitionsValues);
        if (asSrw) {
            return getReportDefinitionXmlProvider()
                .getReportDefinitionsSrwXml(values);
        }
        else {
            return getReportDefinitionXmlProvider()
                .getReportDefinitionsXml(values);
        }
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Adds the report definition name space values.
     * 
     * @param values
     *            The {@link Map} to that the values shall be added.
     * @throws SystemException e
     * @sm
     */
    private void addReportDefinitionNamespaceValues(
            final Map<String, Object> values) throws SystemException {
        addEscidocBaseUrl(values);
        values.put("reportDefinitionNamespacePrefix",
            Constants.REPORT_DEFINITION_NS_PREFIX);
        values.put("reportDefinitionNamespace", 
                Constants.REPORT_DEFINITION_NS_URI);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS_PREFIX,
                Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_PROPERTIES_NS,
                Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProvider.ESCIDOC_SREL_NS_PREFIX,
                Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_SREL_NS,
                Constants.STRUCTURAL_RELATIONS_NS_URI);
    }

    /**
     * Adds the report definition list name space values.
     * 
     * @param values
     *            The {@link Map} to that the values shall be added.
     * @throws SystemException e
     * @sm
     */
    private void addReportDefinitionListNamespaceValues(
            final Map<String, Object> values) throws SystemException {
        addEscidocBaseUrl(values);
        values.put("reportDefinitionListNamespacePrefix",
            Constants.REPORT_DEFINITION_LIST_NS_PREFIX);
        values.put("reportDefinitionListNamespace", 
                Constants.REPORT_DEFINITION_LIST_NS_URI);
    }

    /**
     * Adds the escidoc base URL to the provided map.
     * 
     * @param values
     *            The map to add values to.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @sm
     */
    private void addEscidocBaseUrl(final Map<String, Object> values)
        throws WebserverSystemException {

        values.put(XmlTemplateProvider.VAR_ESCIDOC_BASE_URL, XmlUtility
            .getEscidocBaseUrl());
    }

    /**
     * Gets the <code>ReportDefinitionXmlProvider</code> object.
     * 
     * @return Returns the <code>ReportDefinitionXmlProvider</code> object.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @sm
     */
    private ReportDefinitionXmlProvider getReportDefinitionXmlProvider()
        throws WebserverSystemException {

        return ReportDefinitionXmlProvider.getInstance();
    }

}
