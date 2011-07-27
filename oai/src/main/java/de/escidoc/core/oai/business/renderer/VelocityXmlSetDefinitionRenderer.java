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
package de.escidoc.core.oai.business.renderer;

import de.escidoc.core.aa.business.renderer.AbstractRenderer;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.filter.RecordPacking;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.SetDefinitionXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import de.escidoc.core.oai.business.persistence.SetDefinition;
import de.escidoc.core.oai.business.renderer.interfaces.SetDefinitionRendererInterface;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Set definition renderer implementation using the velocity template engine.
 *
 * @author Rozita Friedman
 */
@Service("eSciDoc.core.om.business.renderer.VelocityXmlSetDefinitionRenderer")
public final class VelocityXmlSetDefinitionRenderer extends AbstractRenderer implements SetDefinitionRendererInterface {

    /**
     * Private constructor to prevent initialization.
     */
    protected VelocityXmlSetDefinitionRenderer() {
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String render(final SetDefinition setDefinition) throws SystemException {
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootSetDefinition", XmlTemplateProviderConstants.TRUE);
        addCommonValues(values);
        addSetDefinitionValues(setDefinition, values);
        return getSetDefinitionXmlProvider().getSetDefinitionXml(values);
    }

    /**
     * Adds the values of the {@link SetDefinition} to the provided {@link Map}.
     *
     * @param setDefinition The {@link SetDefinition}.
     * @param values        The {@link Map} to add the values to.
     * @throws SystemException Thrown in case of an internal error.
     */
    private static void addSetDefinitionValues(final SetDefinition setDefinition, final Map<String, Object> values) {
        final DateTime lmd = new DateTime(setDefinition.getLastModificationDate(), DateTimeZone.UTC);
        values.put("setDefinitionLastModificationDate", lmd.toString());
        values.put("setDefinitionHref", setDefinition.getHref());
        final DateTime creationDate = new DateTime(setDefinition.getCreationDate(), DateTimeZone.UTC);
        values.put("setDefinitionCreationDate", creationDate.toString());
        values.put("setDefinitionName", setDefinition.getName());
        values.put("setDefinitionSpecification", setDefinition.getSpecification());
        values.put("setDefinitionQuery", setDefinition.getQuery());
        values.put("setDefinitionDescription", setDefinition.getDescription());
        values.put("setDefinitionId", setDefinition.getId());
        values.put("setDefinitionCreatedByTitle", setDefinition.getCreatorTitle());
        final String createdById = setDefinition.getCreatorId();
        final String cratedByHref = XmlUtility.getUserAccountHref(createdById);
        values.put("setDefinitionCreatedByHref", cratedByHref);
        values.put("setDefinitionCreatedById", createdById);
        values.put("setDefinitionModifiedByTitle", setDefinition.getModifiedByTitle());
        values.put("setDefinitionModifiedByHref", XmlUtility.getUserAccountHref(setDefinition.getModifiedById()));
        values.put("setDefinitionModifiedById", setDefinition.getModifiedById());

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.oai.business.renderer.interfaces.
     * SetDefinitionRendererInterface#renderSetDefinitions(java.util.List)
     */
    @Override
    public String renderSetDefinitions(final List<SetDefinition> setDefinitions, final RecordPacking recordPacking)
        throws SystemException {
        final Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootSetDefinition", "false");
        values.put("recordPacking", recordPacking);
        addCommonValues(values);
        addSetDefinitionListValues(values);

        final Collection<Map<String, Object>> setDefinitionsValues =
            new ArrayList<Map<String, Object>>(setDefinitions.size());
        for (final SetDefinition setDefinition : setDefinitions) {
            final Map<String, Object> setDefinitionValues = new HashMap<String, Object>();
            addSetDefinitionValues(setDefinition, setDefinitionValues);
            setDefinitionsValues.add(setDefinitionValues);
        }
        values.put("setDefinitions", setDefinitionsValues);
        return getSetDefinitionXmlProvider().getSetDefinitionsSrwXml(values);
    }

    /**
     * Adds the common values to the provided map.
     *
     * @param values The map to add values to.
     */
    private void addCommonValues(final Map<String, Object> values) {

        addSetDefinitionNamespaceValues(values);
        addPropertiesNamespaceValues(values);
        addStructuralRelationNamespaceValues(values);
        addEscidocBaseUrl(values);
    }

    /**
     * Adds the structural relations name space values to the provided map.
     *
     * @param values The map to add values to.
     */
    @Override
    protected void addStructuralRelationNamespaceValues(final Map<String, Object> values) {

        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);
    }

    /**
     * Adds the set definition name space values.
     *
     * @param values The {@link Map} to that the values shall be added.
     */
    private static void addSetDefinitionNamespaceValues(final Map<String, Object> values) {
        values.put("setDefinitionNamespacePrefix", Constants.SET_DEFINITION_NS_PREFIX);
        values.put("setDefinitionNamespace", Constants.SET_DEFINITION_NS_URI);
    }

    /**
     * Adds the set definition list values to the provided map.
     *
     * @param values The map to add values to.
     */
    private static void addSetDefinitionListValues(final Map<String, Object> values) {

        addSetDefinitionsNamespaceValues(values);
        values.put("searchResultNamespace", Constants.SEARCH_RESULT_NS_URI);
        values.put("setDefinitionListTitle", "Set Definition List");
    }

    /**
     * Adds the values related to the set definitions name space to the provided {@link Map}.
     *
     * @param values The MAP to add the values to.
     */
    private static void addSetDefinitionsNamespaceValues(final Map<String, Object> values) {

        values.put("setDefinitionListNamespacePrefix", Constants.SET_DEFINITION_LIST_NS_PREFIX);
        values.put("setDefinitionListNamespace", Constants.SET_DEFINITION_LIST_NS_URI);
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
     * Gets the {@code SetDefinitionXmlProvider} object.
     *
     * @return Returns the {@code SetDefinitionXmlProvider} object.
     */
    private static SetDefinitionXmlProvider getSetDefinitionXmlProvider() {

        return SetDefinitionXmlProvider.getInstance();
    }
}
