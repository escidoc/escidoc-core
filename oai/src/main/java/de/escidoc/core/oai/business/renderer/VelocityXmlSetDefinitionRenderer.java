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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import de.escidoc.core.aa.business.renderer.AbstractRenderer;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.SetDefinitionXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProvider;
import de.escidoc.core.oai.business.persistence.SetDefinition;
import de.escidoc.core.oai.business.renderer.interfaces.SetDefinitionRendererInterface;

/**
 * Set definition renderer implementation using the velocity template engine.
 * 
 * @author rof
 * @spring.bean 
 *              id="eSciDoc.core.om.business.renderer.VelocityXmlSetDefinitionRenderer"
 */
public final class VelocityXmlSetDefinitionRenderer extends AbstractRenderer
    implements SetDefinitionRendererInterface {

    // CHECKSTYLE:JAVADOC-OFF

    /**
     * See Interface for functional description.
     * 
     * @param setDefinition
     * 
     * @return
     * @throws SystemException
     * @see de.escidoc.core.oai.business.renderer.interfaces.SetDefinitionRendererInterface#render(Map)
     * @aa
     */
    public String render(final SetDefinition setDefinition)
        throws SystemException {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootSetDefinition", XmlTemplateProvider.TRUE);
        addCommonValues(values);
        addSetDefinitionValues(setDefinition, values);
        final String ret =
            getSetDefinitionXmlProvider().getSetDefinitionXml(values);
        return ret;
    }

    /**
     * Adds the values of the {@link SetDefinition} to the provided {@link Map}.
     * 
     * @param setDefinition
     *            The {@link SetDefinition}.
     * @param values
     *            The {@link Map} to add the values to.
     * 
     * @throws SystemException
     *             Thrown in case of an internal error.
     */
    private void addSetDefinitionValues(
        final SetDefinition setDefinition, final Map<String, Object> values)
        throws SystemException {
        DateTime lmdDateTime =
            new DateTime(setDefinition.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("setDefinitionLastModificationDate", lmd);
        values.put("setDefinitionHref", setDefinition.getHref());
        DateTime creationDateTime =
            new DateTime(setDefinition.getCreationDate());
        creationDateTime = creationDateTime.withZone(DateTimeZone.UTC);
        String creationDate =
            creationDateTime.toString(Constants.TIMESTAMP_FORMAT);
        values.put("setDefinitionCreationDate", creationDate);
        values.put("setDefinitionName", setDefinition.getName());
        values.put("setDefinitionSpecification", setDefinition
            .getSpecification());
        values.put("setDefinitionQuery", setDefinition.getQuery());
        values.put("setDefinitionDescription", setDefinition.getDescription());
        values.put("setDefinitionId", setDefinition.getId());
        values.put("setDefinitionCreatedByTitle", setDefinition
            .getCreatorTitle());
        String createdById = setDefinition.getCreatorId();
        String cratedByHref = XmlUtility.getUserAccountHref(createdById);
        values.put("setDefinitionCreatedByHref", cratedByHref);
        values.put("setDefinitionCreatedById", createdById);
        values.put("setDefinitionModifiedByTitle", setDefinition
            .getModifiedByTitle());
        values.put("setDefinitionModifiedByHref", XmlUtility
            .getUserAccountHref(setDefinition.getModifiedById()));
        values.put("setDefinitionModifiedById", setDefinition
            .getModifiedById());

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.oai.business.renderer.interfaces.
     * SetDefinitionRendererInterface#renderSetDefinitions(java.util.List)
     */
    public String renderSetDefinitions(
        final List<SetDefinition> setDefinitions)
        throws SystemException {
        Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootSetDefinition", "false");
        addCommonValues(values);
        addSetDefinitionListValues(values);

        final List<Map<String, Object>> setDefinitionsValues =
            new ArrayList<Map<String, Object>>(setDefinitions.size());
        Iterator<SetDefinition> iter = setDefinitions.iterator();

        while (iter.hasNext()) {
            SetDefinition setDefinition = iter.next();
            Map<String, Object> setDefinitionValues =
                new HashMap<String, Object>();

            addSetDefinitionValues(setDefinition, setDefinitionValues);
            setDefinitionsValues.add(setDefinitionValues);
        }
        values.put("setDefinitions", setDefinitionsValues);
        return getSetDefinitionXmlProvider().getSetDefinitionsSrwXml(values);
    }

    // CHECKSTYLE:JAVADOC-ON

    /**
     * Adds the common values to the provided map.
     * 
     * @param values
     *            The map to add values to.
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    private void addCommonValues(final Map<String, Object> values)
        throws WebserverSystemException {

        addSetDefinitionNamespaceValues(values);
        addPropertiesNamespaceValues(values);
        addStructuralRelationNamespaceValues(values);
        addEscidocBaseUrl(values);
    }

    /**
     * Adds the structural relations name space values to the provided map.
     * 
     * @param values
     *            The map to add values to.
     * @aa
     */
    protected void addStructuralRelationNamespaceValues(
        final Map<String, Object> values) {

        values.put(XmlTemplateProvider.ESCIDOC_SREL_NS_PREFIX,
            Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProvider.ESCIDOC_SREL_NS,
            Constants.STRUCTURAL_RELATIONS_NS_URI);
    }

    /**
     * Adds the set definition name space values.
     * 
     * @param values
     *            The {@link Map} to that the values shall be added.
     * @aa
     */
    private void addSetDefinitionNamespaceValues(
        final Map<String, Object> values) {
        values.put("setDefinitionNamespacePrefix",
            Constants.SET_DEFINITION_NS_PREFIX);
        values.put("setDefinitionNamespace", Constants.SET_DEFINITION_NS_URI);
    }

    /**
     * Adds the set definition list values to the provided map.
     * 
     * @param values
     *            The map to add values to.
     * @aa
     */
    private void addSetDefinitionListValues(final Map<String, Object> values) {

        addSetDefinitionsNamespaceValues(values);
        values.put("searchResultNamespace",
            Constants.SEARCH_RESULT_NS_URI);
        values.put("setDefinitionListTitle", "Set Definition List");
    }

    /**
     * Adds the values related to the set definitions name space to the provided
     * {@link Map}.
     * 
     * @param values
     *            The MAP to add the values to.
     * @aa
     */
    private void addSetDefinitionsNamespaceValues(
        final Map<String, Object> values) {

        values.put("setDefinitionListNamespacePrefix",
            Constants.SET_DEFINITION_LIST_NS_PREFIX);
        values.put("setDefinitionListNamespace",
            Constants.SET_DEFINITION_LIST_NS_URI);
    }

    /**
     * Adds the escidoc base URL to the provided map.
     * 
     * @param values
     *            The map to add values to.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    private void addEscidocBaseUrl(final Map<String, Object> values)
        throws WebserverSystemException {

        values.put(XmlTemplateProvider.VAR_ESCIDOC_BASE_URL, XmlUtility
            .getEscidocBaseUrl());
    }

    /**
     * Gets the <code>SetDefinitionXmlProvider</code> object.
     * 
     * @return Returns the <code>SetDefinitionXmlProvider</code> object.
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     * @aa
     */
    private SetDefinitionXmlProvider getSetDefinitionXmlProvider()
        throws WebserverSystemException {

        return SetDefinitionXmlProvider.getInstance();
    }
}
