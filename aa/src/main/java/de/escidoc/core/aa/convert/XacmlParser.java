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
 * Copyright 2008 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package de.escidoc.core.aa.convert;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.xacml.cond.FunctionFactory;
import com.sun.xacml.cond.FunctionFactoryProxy;
import com.sun.xacml.cond.StandardFunctionFactory;

import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface;
import de.escidoc.core.aa.business.persistence.ScopeDef;
import de.escidoc.core.aa.business.stax.handler.RolePropertiesStaxHandler;
import de.escidoc.core.aa.business.stax.handler.ScopeStaxHandler;
import de.escidoc.core.aa.business.stax.handler.XacmlStaxHandler;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionContains;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionIsIn;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionRoleInList;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionOneAttributeInBothLists;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionRoleIsGranted;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.list.ListSorting;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.StaxParser;

/**
 * This is a helper class to convert an XACML document into an SQL fragment.
 * 
 * @spring.bean id="convert.XacmlParser"
 * @author SCHE
 * @aa
 */
public class XacmlParser {
    // The following place holders may be used:
    // {0} : userId
    // {1} : roleId
    // {2} : groupSQL
    // {3} : quotedGroupSQL (used in stored procedures)
    private static final String USER_GRANT_SQL =
        "SELECT object_id FROM aa.role_grant WHERE user_id='{0}' AND "
            + "role_id='{1}' AND (revocation_date IS NULL OR revocation_date>"
            + "CURRENT_TIMESTAMP)";

    private static final String USER_GROUP_GRANT_SQL =
        "SELECT object_id FROM aa.role_grant WHERE {2} AND role_id='{1}' AND "
            + "(revocation_date IS NULL OR revocation_date>CURRENT_TIMESTAMP)";

    private static final String QUOTED_USER_GROUP_GRANT_SQL =
        "SELECT object_id FROM aa.role_grant WHERE {3} AND role_id='{1}' AND "
            + "(revocation_date IS NULL OR revocation_date>CURRENT_TIMESTAMP)";

    private static final String ID_SQL =
        "(r.id IN (" + USER_GRANT_SQL + ") OR r.id IN (" + USER_GROUP_GRANT_SQL
            + "))";

    /**
     * This map contains scopes which are ignored by this parser.
     */
    private static final Map<String, String> IGNORED_SCOPE_MAP =
        new HashMap<String, String>();

    /**
     * This map contains all scopes which can be mapped.
     */
    private static final Map<String, String> SCOPE_MAP =
        new HashMap<String, String>();

    static {
        // There are no components in the DB cache.
        IGNORED_SCOPE_MAP.put(
            "info:escidoc/names:aa:1.0:resource:component-id", "");
        IGNORED_SCOPE_MAP.put(
            "info:escidoc/names:aa:1.0:resource:component:item", "");
        IGNORED_SCOPE_MAP.put(
            "info:escidoc/names:aa:1.0:resource:component:item:container", "");
        IGNORED_SCOPE_MAP.put(
            "info:escidoc/names:aa:1.0:resource:component:item:hierarchical-"
                + "containers", "");
        IGNORED_SCOPE_MAP.put(
            "info:escidoc/names:aa:1.0:resource:component:item:context", "");
        IGNORED_SCOPE_MAP
            .put(
                "info:escidoc/names:aa:1.0:resource:organizational-unit:parent",
                "");

        // This is a rule from the "author" role which seems to be obsolete.
        IGNORED_SCOPE_MAP.put(
            "info:escidoc/names:aa:1.0:resource:container.collection:id", "");

        // resource container
        SCOPE_MAP
            .put(
                "info:escidoc/names:aa:1.0:resource:container:container.collection",
                "r.id IN (SELECT value FROM list.property WHERE local_path="
                    + "'/struct-map/item/id' AND resource_id IN (SELECT "
                    + "resource_id FROM list.property WHERE local_path="
                    + "'/properties/content-model/title' "
                    + "AND value='collection' AND (resource_id IN ("
                    + USER_GRANT_SQL + ") OR resource_id IN ("
                    + USER_GROUP_GRANT_SQL + "))))");

        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:container:context",
            "r.id IN (SELECT resource_id FROM list.property WHERE local_path="
                + "'/properties/context/id' AND (value IN (" + USER_GRANT_SQL
                + ") OR value IN (" + USER_GROUP_GRANT_SQL + ")))");

        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:container:container",
            "r.id IN (SELECT value FROM list.property WHERE local_path="
                + "'/struct-map/container/id' AND (resource_id IN ("
                + USER_GRANT_SQL + ") OR resource_id IN ("
                + USER_GROUP_GRANT_SQL + ")))");

        SCOPE_MAP
            .put(
                "info:escidoc/names:aa:1.0:resource:container:hierarchical-containers",
                "r.id IN (SELECT resource_id FROM getAllChildContainers('"
                    + "SELECT DISTINCT resource_id FROM list.property WHERE "
                    + "resource_id IN (" + USER_GRANT_SQL.replace("'", "''")
                    + ") OR resource_id IN ("
                    + QUOTED_USER_GROUP_GRANT_SQL.replace("'", "''") + ")'))");

        SCOPE_MAP
            .put("info:escidoc/names:aa:1.0:resource:container-id", ID_SQL);

        // resource content relation
        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:content-relation-id",
            ID_SQL);

        // resource context
        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:context-id", ID_SQL);

        // resource item
        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:item:component",
            "r.id IN (SELECT resource_id FROM list.property WHERE local_path="
                + "'/components/component/id' AND (value IN (" + USER_GRANT_SQL
                + ") OR value IN (" + USER_GROUP_GRANT_SQL + ")))");

        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:item:container",
            "r.id IN (SELECT value FROM list.property WHERE local_path="
                + "'/struct-map/item/id' AND (resource_id IN ("
                + USER_GRANT_SQL + ") OR resource_id IN ("
                + USER_GROUP_GRANT_SQL + ")))");

        SCOPE_MAP.put(
                "info:escidoc/names:aa:1.0:resource:item:container.collection",
                "r.id IN (SELECT value FROM list.property WHERE local_path="
                    + "'/struct-map/item/id' AND resource_id IN ("
                    + "SELECT resource_id FROM list.property WHERE "
                    + "local_path='/properties/content-model/title' "
                    + "AND value='collection' AND (resource_id IN ("
                    + USER_GRANT_SQL + ") OR resource_id IN ("
                    + USER_GROUP_GRANT_SQL + "))))");

        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:item:context",
            "r.id IN (SELECT resource_id FROM list.property WHERE local_path="
                + "'/properties/context/id' AND (value IN (" + USER_GRANT_SQL
                + ") OR value IN (" + USER_GROUP_GRANT_SQL + ")))");

        SCOPE_MAP.put(
            "info:escidoc/names:aa:1.0:resource:item:hierarchical-containers",
            "r.id IN (SELECT resource_id FROM getAllChildItems"
                + "('SELECT DISTINCT resource_id FROM list.property WHERE "
                + "resource_id IN (" + USER_GRANT_SQL.replace("'", "''")
                + ") OR resource_id IN ("
                + QUOTED_USER_GROUP_GRANT_SQL.replace("'", "''") + ")'))");

        SCOPE_MAP.put("info:escidoc/names:aa:1.0:resource:item-id", ID_SQL);

        // resource organizational unit
        SCOPE_MAP
            .put(
                "info:escidoc/names:aa:1.0:resource:organizational-unit:hierarchical-parents",
                "r.id IN (SELECT resource_id FROM getAllChildOUs('SELECT resource_id FROM "
                    + "list.property WHERE local_path=''/parents/parent/id'' AND "
                    + "(value IN (" + USER_GRANT_SQL.replace("'", "''")
                    + ") OR value IN" + "("
                    + USER_GROUP_GRANT_SQL.replace("'", "''") + "))'))");
    }

    /**
     * The logger.
     */
    private static final AppLogger LOG =
        new AppLogger(XacmlParser.class.getName());

    private XacmlFunctionRoleIsGranted xacmlFunctionRoleIsGranted;

    private PolicyParser pol = null;

    private EscidocRole role = null;

    private EscidocRoleDaoInterface roleDao = new EscidocRoleDaoInterface() {
        public boolean roleExists(final String identifier)
            throws SqlDatabaseSystemException {
            return false;
        }

        public void deleteRole(final EscidocRole r)
            throws SqlDatabaseSystemException {
        }

        public void flush() throws SqlDatabaseSystemException {
        }

        public EscidocRole retrieveRole(final String identifier)
            throws SqlDatabaseSystemException {
            return null;
        }

        public List<EscidocRole> retrieveRoles(
            final Map<String, Object> criteria, final int offset,
            final int maxResults, final String orderBy,
            final ListSorting sorting) throws SqlDatabaseSystemException {
            return null;
        }

        public List<EscidocRole> retrieveRoles(
            final String criteria, final int offset, final int maxResults)
            throws InvalidSearchQueryException, SqlDatabaseSystemException {
            return null;
        }

        public void saveOrUpdate(final EscidocRole r)
            throws SqlDatabaseSystemException {
        }

        public void deleteScopeDef(final ScopeDef scopeDef)
            throws SqlDatabaseSystemException {
        }
    };

    /**
     * Get an SQL fragment from the XACML translation process for the given
     * resource type.
     * 
     * @param resourceType
     *            resource type
     * 
     * @return SQL fragment representing the XACML policies for that resource
     *         type
     */
    public String getRules(final ResourceType resourceType) {
        StringBuffer result = new StringBuffer();
        String scopeRules = getScopeRules(resourceType);

        if ((scopeRules != null) && (scopeRules.length() > 0)) {
            result.append('(');
            result.append(scopeRules);
            result.append(')');
        }

        List<String> ruleList = pol.getMatchingRules(resourceType);

        for (String rule : ruleList) {
            if ((rule != null) && (rule.length() > 0)) {
                if (result.length() > 0) {
                    result.append(" AND ");
                }
                result.append(rule);
            }
        }
        return result.toString();
    }

    /**
     * Get an SQL fragment from the XACML translation process of the role scope
     * for the given resource type.
     * 
     * @param resourceType
     *            resource type
     * 
     * @return SQL fragment representing the role scope of the XACML document
     *         for that resource type
     */
    private String getScopeRules(final ResourceType resourceType) {
        StringBuffer result = new StringBuffer();
        String label = resourceType.getLabel();

        for (Object scope : role.getScopeDefs()) {
            if (label.equals(((ScopeDef) scope).getObjectType())) {
                String rule =
                    SCOPE_MAP.get(((ScopeDef) scope).getAttributeId());

                if (rule == null) {
                    if (IGNORED_SCOPE_MAP.containsKey(((ScopeDef) scope)
                        .getAttributeId())) {
                        LOG.info("ignore scope definition "
                            + ((ScopeDef) scope).getAttributeId());
                    }
                    else {
                        throw new IllegalArgumentException(
                            "no translation found for "
                                + ((ScopeDef) scope).getAttributeId());
                    }
                }
                else {
                    if (result.length() > 0) {
                        result.append(" OR ");
                    }
                    result.append('(');
                    result.append(rule);
                    result.append(')');
                }
            }
        }
        return result.toString();
    }

    /**
     * Initialize the XACML function factory.
     */
    private void initFactory() {
        FunctionFactoryProxy proxy =
            StandardFunctionFactory.getNewFactoryProxy();
        FunctionFactory factory = proxy.getTargetFactory();

        factory.addFunction(new XacmlFunctionContains());
        factory.addFunction(new XacmlFunctionIsIn());
        factory.addFunction(new XacmlFunctionRoleInList());
        factory.addFunction(new XacmlFunctionOneAttributeInBothLists());
        factory.addFunction(xacmlFunctionRoleIsGranted);
        FunctionFactory.setDefaultFactory(proxy);
    }

    /**
     * Parse the given role and convert the embedded rules which are interesting
     * for the resource cache into SQL fragments.
     * 
     * @param aRole
     *            role to be parsed
     * 
     * @throws WebserverSystemException
     *             Thrown in case of an internal error.
     */
    public void parse(final EscidocRole aRole) throws WebserverSystemException {
        this.role = aRole;
        initFactory();
        pol = new PolicyParser(role.getXacmlPolicySet());
    }

    /**
     * Parse the given XACML document and convert the embedded rules which are
     * interesting for the resource cache into SQL fragments.
     * 
     * @param file
     *            file to be parsed
     * 
     * @throws Exception
     *             Thrown in case of an internal error.
     */
    public void parse(final File file) throws Exception {
        role = new EscidocRole();
        initFactory();

        InputStream in = null;

        try {
            in = new BufferedInputStream(new FileInputStream(file));

            StaxParser sp = new StaxParser(XmlUtility.NAME_ROLE);
            RolePropertiesStaxHandler propertiesHandler =
                new RolePropertiesStaxHandler(role, roleDao);

            sp.addHandler(propertiesHandler);

            ScopeStaxHandler scopeHandler = new ScopeStaxHandler(role, roleDao);

            sp.addHandler(scopeHandler);

            XacmlStaxHandler xacmlHandler = new XacmlStaxHandler(role);

            sp.addHandler(xacmlHandler);
            sp.parse(in);
            pol = new PolicyParser(role.getXacmlPolicySet());
        }
        finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * Injects the {@link XacmlFunctionRoleIsGranted}.
     * 
     * @param xacmlFunctionRoleIsGranted
     *            the {@link XacmlFunctionRoleIsGranted} to inject.
     * 
     * @spring.property ref="eSciDoc.core.aa.XacmlFunctionRoleIsGranted"
     */
    public void setXacmlFunctionRoleIsGranted(
        final XacmlFunctionRoleIsGranted xacmlFunctionRoleIsGranted) {

        this.xacmlFunctionRoleIsGranted = xacmlFunctionRoleIsGranted;
    }

    /**
     * See Interface for functional description.
     * 
     * @return a string representation of the object
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();

        result.append("rules for container: ");
        result.append(getRules(ResourceType.CONTAINER));
        result.append('\n');

        result.append("rules for context: ");
        result.append(getRules(ResourceType.CONTEXT));
        result.append('\n');

        result.append("rules for item: ");
        result.append(getRules(ResourceType.ITEM));
        result.append('\n');

        result.append("rules for organizational-unit: ");
        result.append(getRules(ResourceType.OU));
        result.append('\n');

        return result.toString();
    }
}
