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
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.ReportXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import de.escidoc.core.sm.business.persistence.hibernate.ReportDefinition;
import de.escidoc.core.sm.business.renderer.interfaces.ReportRendererInterface;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Report renderer implementation using the velocity template engine.
 * <p/>
 * Value-Map: ESCIDOC_BASE_URL reportNamespacePrefix reportNamespace structuralRelationsNamespacePrefix
 * structuralRelationsNamespace parameterNamespacePrefix parameterNamespace reportDefinitionId reportDefinitionName
 * reportDefinitionHref records (List with Lists) List containing all fields of one record fieldname decimalvalue
 * stringvalue datevalue
 *
 * @author Michael Hoppe
 */
@Service("eSciDoc.core.aa.business.renderer.VelocityXmlReportRenderer")
public final class VelocityXmlReportRenderer implements ReportRendererInterface {

    /**
     * Private constructor to prevent initialization.
     */
    private VelocityXmlReportRenderer() {
    }

    /**
     * See Interface for functional description.
     *
     * @param dbResult         result from dbCall.
     * @param reportDefinition the reportDefinition Hibernate Object.
     */
    @Override
    public String render(final List dbResult, final ReportDefinition reportDefinition) throws SystemException {
        final Map<String, Object> values = new HashMap<String, Object>();
        addReportNamespaceValues(values);
        addReportValues(reportDefinition, values);
        addDataValues(dbResult, values);
        return getReportXmlProvider().getReportXml(values);
    }

    /**
     * Adds the values of the database-query to the provided {@link Map}.
     *
     * @param dbResult The dbResult.
     * @param values   The {@link Map} to add the values to.
     * @throws SystemException Thrown in case of an internal error.
     */
    private static void addDataValues(final Collection dbResult, final Map<String, Object> values) {
        final Collection<List<HashMap<String, Object>>> recordsList = new ArrayList<List<HashMap<String, Object>>>();
        if (dbResult != null && !dbResult.isEmpty()) {
            // Iterate records from database
            for (final Object aDbResult : dbResult) {
                final List<HashMap<String, Object>> recordFieldList = new ArrayList<HashMap<String, Object>>();
                final Map map = (Map) aDbResult;
                // iterate all fields of one record
                for (final Object o : map.keySet()) {
                    final String fieldname = (String) o;
                    // depending on the fieldtype,
                    // write stringvalue, datevalue or decimalvalue-element
                    if (map.get(fieldname) != null) {
                        final HashMap<String, Object> recordFieldMap = new HashMap<String, Object>();
                        recordFieldMap.put("fieldname", fieldname);
                        final String classname = map.get(fieldname).getClass().getSimpleName();
                        if ("BigDecimal".equals(classname)) {
                            recordFieldMap.put("decimalvalue", map.get(fieldname).toString());
                        }
                        else if ("Timestamp".equals(classname)) {
                            DateTime dateTime = new DateTime(map.get(fieldname));
                            dateTime = dateTime.withZone(DateTimeZone.UTC);
                            final String dateString = dateTime.toString(Constants.TIMESTAMP_FORMAT);
                            recordFieldMap.put("datevalue", dateString);
                        }
                        else {
                            recordFieldMap.put("stringvalue", map.get(fieldname));
                        }

                        // Add field to record
                        recordFieldList.add(recordFieldMap);
                    }
                }

                // add record to recordsVm
                if (!recordFieldList.isEmpty()) {
                    recordsList.add(recordFieldList);
                }
            }
        }
        values.put("records", recordsList);
    }

    /**
     * Adds the values to the provided {@link Map}.
     *
     * @param reportDefinition The reportDefinitionId Hibernate Object.
     * @param values           The {@link Map} to add the values to.
     * @throws SystemException Thrown in case of an internal error.
     */
    private static void addReportValues(final ReportDefinition reportDefinition, final Map<String, Object> values) {
        values.put("reportDefinitionId", reportDefinition.getId());
        values.put("reportDefinitionName", reportDefinition.getName());
        values.put("reportDefinitionHref", XmlUtility.getReportDefinitionHref(reportDefinition.getId()));
    }

    /**
     * Adds the scope name space values.
     *
     * @param values The {@link Map} to that the values shall be added.
     */
    private static void addReportNamespaceValues(final Map<String, Object> values) {
        addEscidocBaseUrl(values);
        values.put("reportNamespacePrefix", Constants.REPORT_NS_PREFIX);
        values.put("reportNamespace", Constants.REPORT_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PARAMETER_NS_PREFIX, Constants.PARAMETER_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PARAMETER_NS, Constants.PARAMETER_NS_URI);
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
     * Gets the {@code ReportXmlProvider} object.
     *
     * @return Returns the {@code ReportXmlProvider} object.
     */
    private static ReportXmlProvider getReportXmlProvider() {
        return ReportXmlProvider.getInstance();
    }

}
