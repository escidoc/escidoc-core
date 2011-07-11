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
import de.escidoc.core.common.util.xml.factory.ScopeXmlProvider;
import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import de.escidoc.core.sm.business.persistence.hibernate.Scope;
import de.escidoc.core.sm.business.renderer.interfaces.ScopeRendererInterface;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scope renderer implementation using the velocity template engine.
 *
 * @author Michael Hoppe
 */
@Service("eSciDoc.core.aa.business.renderer.VelocityXmlScopeRenderer")
public final class VelocityXmlScopeRenderer implements ScopeRendererInterface {

    /**
     * Private constructor to prevent initialization.
     */
    private VelocityXmlScopeRenderer() {
    }

    /**
     * See Interface for functional description.
     *
     */
    @Override
    public String render(final Scope scope) throws SystemException {
        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootScope", XmlTemplateProviderConstants.TRUE);
        addScopeNamespaceValues(values);
        addScopeValues(scope, values);
        return getScopeXmlProvider().getScopeXml(values);
    }

    /**
     * Adds the values of the {@link Scope} to the provided {@link Map}.
     *
     * @param scope  The {@link Scope}.
     * @param values The {@link Map} to add the values to.
     * @throws SystemException Thrown in case of an internal error.
     */
    private static void addScopeValues(final Scope scope, final Map<String, Object> values) {
        DateTime createDateTime = new DateTime(scope.getCreationDate());
        createDateTime = createDateTime.withZone(DateTimeZone.UTC);
        final String create = createDateTime.toString(Constants.TIMESTAMP_FORMAT);
        DateTime lmdDateTime = new DateTime(scope.getLastModificationDate());
        lmdDateTime = lmdDateTime.withZone(DateTimeZone.UTC);
        final String lmd = lmdDateTime.toString(Constants.TIMESTAMP_FORMAT);

        values.put("scopeCreationDate", create);
        values.put("scopeCreatedById", scope.getCreatorId());
        values.put("scopeCreatedByTitle", "user " + scope.getCreatorId());
        values.put("scopeCreatedByHref", XmlUtility.getUserAccountHref(scope.getCreatorId()));
        values.put("scopeLastModificationDate", lmd);
        values.put("scopeModifiedById", scope.getModifiedById());
        values.put("scopeModifiedByTitle", "user " + scope.getModifiedById());
        values.put("scopeModifiedByHref", XmlUtility.getUserAccountHref(scope.getModifiedById()));
        values.put("scopeId", scope.getId());
        values.put("scopeName", scope.getName());
        values.put("scopeHref", XmlUtility.getScopeHref(scope.getId()));
        values.put("scopeType", scope.getScopeType());
    }

    /**
     * See Interface for functional description.
     *
     * @param recordPacking A string to determine how the record should be escaped in the response. Defined values are
     *                      'string' and 'xml'. The default is 'xml'.
     */
    @Override
    public String renderScopes(final Collection<Scope> scopes, final RecordPacking recordPacking)
        throws SystemException {

        final Map<String, Object> values = new HashMap<String, Object>();

        values.put("isRootScope", XmlTemplateProviderConstants.FALSE);
        values.put("scopeListTitle", "Scope List");
        values.put("recordPacking", recordPacking);
        addScopeNamespaceValues(values);
        addScopeListNamespaceValues(values);

        final List<Map<String, Object>> scopesValues;
        if (scopes != null) {
            scopesValues = new ArrayList<Map<String, Object>>(scopes.size());
            for (final Scope scope : scopes) {
                final Map<String, Object> scopeValues = new HashMap<String, Object>();
                addScopeNamespaceValues(scopeValues);
                addScopeValues(scope, scopeValues);
                scopesValues.add(scopeValues);
            }
        }
        else {
            scopesValues = new ArrayList<Map<String, Object>>();
        }
        values.put("scopes", scopesValues);
        return getScopeXmlProvider().getScopesSrwXml(values);
    }

    /**
     * Adds the scope name space values.
     *
     * @param values The {@link Map} to that the values shall be added.
     */
    private static void addScopeNamespaceValues(final Map<String, Object> values) {
        addEscidocBaseUrl(values);
        values.put("scopeNamespacePrefix", Constants.SCOPE_NS_PREFIX);
        values.put("scopeNamespace", Constants.SCOPE_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS_PREFIX, Constants.PROPERTIES_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_PROPERTIES_NS, Constants.PROPERTIES_NS_URI);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS_PREFIX, Constants.STRUCTURAL_RELATIONS_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.ESCIDOC_SREL_NS, Constants.STRUCTURAL_RELATIONS_NS_URI);
    }

    /**
     * Adds the scope list name space values.
     *
     * @param values The {@link Map} to that the values shall be added.
     */
    private static void addScopeListNamespaceValues(final Map<String, Object> values) {
        addEscidocBaseUrl(values);
        values.put("searchResultNamespace", Constants.SEARCH_RESULT_NS_URI);
        values.put("scopeListNamespacePrefix", Constants.SCOPE_LIST_NS_PREFIX);
        values.put("scopeListNamespace", Constants.SCOPE_LIST_NS_URI);
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
     * Gets the {@code ScopeXmlProvider} object.
     *
     * @return Returns the {@code ScopeXmlProvider} object.
     */
    private static ScopeXmlProvider getScopeXmlProvider() {

        return ScopeXmlProvider.getInstance();
    }

}
