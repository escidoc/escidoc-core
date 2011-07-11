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
package de.escidoc.core.aa.business.renderer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.escidoc.core.common.util.xml.factory.XmlTemplateProviderConstants;
import org.springframework.stereotype.Service;

import de.escidoc.core.aa.business.authorisation.CustomPolicyBuilder;
import de.escidoc.core.aa.business.persistence.EscidocPolicy;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.ScopeDef;
import de.escidoc.core.aa.business.persistence.UserAccount;
import de.escidoc.core.aa.business.renderer.interfaces.RoleRendererInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.filter.RecordPacking;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.factory.RoleXmlProvider;

/**
 * Role renderer implementation using the velocity template engine.
 *
 * @author Torsten Tetteroo
 */
@Service("eSciDoc.core.aa.business.renderer.VelocityXmlRoleRenderer")
public class VelocityXmlRoleRenderer extends AbstractRenderer implements RoleRendererInterface {

    /**
     * See Interface for functional description.
     *
     * @see RoleRendererInterface #render(de.escidoc.core.aa.business.persistence.EscidocRole)
     */
    @Override
    public String render(final EscidocRole role) throws WebserverSystemException {
        final Map<String, Object> values = new HashMap<String, Object>();
        addCommonValues(values);
        addRoleValues(role, values);
        addXacmlNamespaceValues(values);
        return getRoleXmlProvider().getRoleXml(values);
    }

    /**
     * See Interface for functional description.
     */
    @Override
    public String renderResources(final EscidocRole role) throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        values.put("isRootResources", XmlTemplateProviderConstants.TRUE);

        addCommonValues(values);
        values.put(XmlTemplateProviderConstants.VAR_LAST_MODIFICATION_DATE, XmlUtility.normalizeDate(role
            .getLastModificationDate()));
        values.put(XmlTemplateProviderConstants.VAR_ESCIDOC_BASE_URL, XmlUtility.getEscidocBaseUrl());
        addResourcesValues(role, values);

        return getRoleXmlProvider().getResourcesXml(values);
    }

    /**
     * See Interface for functional description.
     *
     * @param recordPacking A string to determine how the record should be escaped in the response. Defined values are
     *                      'string' and 'xml'. The default is 'xml'.
     */
    @Override
    public String renderRoles(final List<EscidocRole> roles, final RecordPacking recordPacking)
        throws WebserverSystemException {

        final Map<String, Object> values = new HashMap<String, Object>();
        addCommonValues(values);
        addRoleListValues(values);
        values.put("recordPacking", recordPacking);

        final Collection<Map<String, Object>> rolesValues = new ArrayList<Map<String, Object>>(roles.size());
        for (final EscidocRole escidocRole : roles) {
            final Map<String, Object> roleValues = new HashMap<String, Object>();
            addRoleValues(escidocRole, roleValues);
            rolesValues.add(roleValues);
        }
        values.put("roles", rolesValues);
        return getRoleXmlProvider().getRolesSrwXml(values);
    }

    /**
     * Adds the common values to the provided map.
     *
     * @param values The map to add values to.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private void addCommonValues(final Map<String, Object> values) {

        values.put("roleNamespacePrefix", Constants.ROLE_NS_PREFIX);
        values.put("roleNamespace", Constants.ROLE_NS_URI);
        addPropertiesNamespaceValues(values);
        addStructuralRelationNamespaceValues(values);
        addXlinkNamespaceValues(values);
        addEscidocBaseUrlValue(values);
    }

    /**
     * Adds the values of the role that shall be rendered to the provided {@link Map}.
     * @param role
     * @param values
     */
    private static void addRoleValues(final EscidocRole role, final Map<String, Object> values) {

        values.put(XmlTemplateProviderConstants.VAR_LAST_MODIFICATION_DATE, XmlUtility.normalizeDate(role
            .getLastModificationDate()));
        values.put(XmlTemplateProviderConstants.VAR_CREATION_DATE, XmlUtility.normalizeDate(role.getCreationDate()));
        values.put(XmlTemplateProviderConstants.VAR_DESCRIPTION, role.getDescription());

        values.put("roleId", role.getId());
        final UserAccount createdBy = role.getUserAccountByCreatorId();
        values.put("roleCreatedById", createdBy.getId());
        values.put("roleCreatedByHref", createdBy.getHref());
        values.put("roleCreatedByTitle", createdBy.getName());

        final UserAccount modifiedBy = role.getUserAccountByModifiedById();
        values.put("roleModifiedById", modifiedBy.getId());
        values.put("roleModifiedByHref", modifiedBy.getHref());
        values.put("roleModifiedByTitle", role.getUserAccountByCreatorId().getName());

        values.put("roleHref", XmlUtility.getRoleHref(role.getId()));
        values.put("roleIsLimited", role.isLimited());
        values.put("roleName", role.getRoleName());

        if (role.isLimited()) {
            // sort output
            Collection<ScopeDef> collection = role.getScopeDefs();
            if (collection != null && !collection.isEmpty()) {
                final List<ScopeDef> list = new ArrayList<ScopeDef>(collection);
                Collections.sort(list);
                collection = list;
            }
            values.put("roleScopeDefs", collection);
        }
        else {
            values.put("roleScopeDefs", null);
        }

        addPolicyValues(role, values);
        addResourcesValues(role, values);
    }

    /**
     * Adds the xacml policy values to the provided map.
     *
     * @param role   The role for that data shall be created.
     * @param values The map to add values to.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private static void addPolicyValues(final EscidocRole role, final Map<String, Object> values) {

        // There seems to be a problem in XacmlPolicy.encode that does not
        // escape special characters. Therefore, we should try to directly get
        // the xacml policy/policy set from the role. if the role's xml does not
        // contain a policy/policy set (in case of predefined roles), we have to
        // build and encode it.
        final Collection<EscidocPolicy> policies = role.getEscidocPolicies();
        final Iterator<EscidocPolicy> iter = policies.iterator();
        if (iter.hasNext()) {
            final EscidocPolicy policy = iter.next();
            values.put("policy", CustomPolicyBuilder.insertXacmlPrefix(policy.getXml()));
        }
    }

    /**
     * Adds the resources specific values to the provided map.
     *
     * @param role   The role for that data shall be created.
     * @param values The map to add values to.
     */
    private static void addResourcesValues(final EscidocRole role, final Map<String, Object> values) {

        values.put(XmlTemplateProviderConstants.VAR_RESOURCES_HREF, ROLE_URL_BASE + role.getId() + RESOURCES_URL_PART);
    }

    /**
     * Adds the role list values to the provided map.
     *
     * @param values The map to add values to.
     */
    private static void addRoleListValues(final Map<String, Object> values) {

        addRolesNamespaceValues(values);
        values.put("searchResultNamespace", Constants.SEARCH_RESULT_NS_URI);
        values.put("roleListTitle", "Role List");
        addXacmlNamespaceValues(values);
    }

    /**
     * Adds the values related to the names space of roles to the provided {@link Map}.
     *
     * @param values The {@link Map} to add the values to .
     */
    private static void addRolesNamespaceValues(final Map<String, Object> values) {
        values.put("roleListNamespacePrefix", Constants.ROLE_LIST_NS_PREFIX);
        values.put("roleListNamespace", Constants.ROLE_LIST_NS_URI);
    }

    /**
     * Adds the values for the name space declaration to the provided {@link Map}.
     *
     * @param values The {@link Map} to add the values to.
     */
    private static void addXacmlNamespaceValues(final Map<String, Object> values) {
        values.put(XmlTemplateProviderConstants.VAR_XACML_POLICY_NAMESPACE_PREFIX, Constants.XACML_POLICY_NS_PREFIX);
        values.put(XmlTemplateProviderConstants.VAR_XACML_POLICY_NAMESPACE, Constants.XACML_POLICY_NS_URI);
    }

    /**
     * Gets the {@link RoleXmlProvider} object.
     *
     * @return Returns the {@link RoleXmlProvider} object.
     * @throws WebserverSystemException Thrown in case of an internal error.
     */
    private static RoleXmlProvider getRoleXmlProvider() {

        return RoleXmlProvider.getInstance();
    }

}
