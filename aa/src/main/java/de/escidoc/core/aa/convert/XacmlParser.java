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

import com.sun.xacml.cond.FunctionFactory;
import com.sun.xacml.cond.FunctionFactoryProxy;
import com.sun.xacml.cond.StandardFunctionFactory;
import de.escidoc.core.aa.business.persistence.EscidocRole;
import de.escidoc.core.aa.business.persistence.EscidocRoleDaoInterface;
import de.escidoc.core.aa.business.persistence.ScopeDef;
import de.escidoc.core.aa.business.persistence.ScopeDefBase;
import de.escidoc.core.aa.business.stax.handler.RolePropertiesStaxHandler;
import de.escidoc.core.aa.business.stax.handler.ScopeStaxHandler;
import de.escidoc.core.aa.business.stax.handler.XacmlStaxHandler;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionContains;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionIsIn;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionOneAttributeInBothLists;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionRoleInList;
import de.escidoc.core.aa.business.xacml.function.XacmlFunctionRoleIsGranted;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.Values;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.WebserverSystemException;
import de.escidoc.core.common.util.IOUtils;
import de.escidoc.core.common.util.list.ListSorting;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.common.util.xml.stax.StaxParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * This is a helper class to convert an XACML document into an SQL / Lucene
 * fragment.
 * 
 * @author SCHE
 */
public class XacmlParser {
    /**
     * The logger.
     */
    private static final Logger LOGGER =
        LoggerFactory.getLogger(XacmlParser.class);

    private XacmlFunctionRoleIsGranted xacmlFunctionRoleIsGranted;

    private PolicyParser pol;

    private EscidocRole role;

    private Values values;

    private final EscidocRoleDaoInterface roleDao = new EscidocRoleDaoInterface() {
        @Override
        public boolean roleExists(final String identifier)
            throws SqlDatabaseSystemException {
            return false;
        }

        @Override
        public void deleteRole(final EscidocRole r)
            throws SqlDatabaseSystemException {
        }

        @Override
        public void flush() throws SqlDatabaseSystemException {
        }

        @Override
        public EscidocRole retrieveRole(final String identifier)
            throws SqlDatabaseSystemException {
            return null;
        }

        @Override
        public List<EscidocRole> retrieveRoles(
            final Map<String, Object> criteria, final int offset,
            final int maxResults, final String orderBy,
            final ListSorting sorting) throws SqlDatabaseSystemException {
            return null;
        }

        @Override
        public List<EscidocRole> retrieveRoles(
            final String criteria, final int offset, final int maxResults)
            throws InvalidSearchQueryException, SqlDatabaseSystemException {
            return null;
        }

        @Override
        public void saveOrUpdate(final EscidocRole r)
            throws SqlDatabaseSystemException {
        }

        @Override
        public void deleteScopeDef(final ScopeDef scopeDef)
            throws SqlDatabaseSystemException {
        }
    };

    /**
     * Get an SQL fragment from the XACML translation process of the policy
     * rules for the given resource type.
     * 
     * @param resourceType
     *            resource type
     * 
     * @return SQL fragment representing the policy rules of the XACML document
     *         for that resource type
     */
    public String getPolicyRules(final ResourceType resourceType) {
        String result = "";
        final List<String> ruleList = pol.getMatchingRules(resourceType);

        for (final String rule : ruleList) {
            if (rule != null && rule.length() > 0) {
                result = result.length() > 0 ? values.getAndCondition(result, rule) : rule;
            }
        }
        if (result.length() == 0) {
            result = "FALSE";
        }
        return result;
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
    public String getScopeRules(final ResourceType resourceType) {
        String result = "";
        final String label = resourceType.getLabel();

        for (final Object scope : role.getScopeDefs()) {
            if (label.equals(((ScopeDefBase) scope).getObjectType())) {
                final String rule =
                    values.getScope(((ScopeDefBase) scope).getAttributeId());

                if (rule == null) {
                    if (values.ignoreScope(((ScopeDefBase) scope).getAttributeId())) {
                        LOGGER.info("ignore scope definition "
                            + ((ScopeDefBase) scope).getAttributeId());
                    }
                    else {
                        throw new IllegalArgumentException(
                            "no translation found for "
                                + ((ScopeDefBase) scope).getAttributeId());
                    }
                }
                else {
                    result = result.length() > 0 ? values.getOrCondition(result, rule) : rule;
                }
            }
        }
        return result;
    }

    /**
     * Initialize the XACML function factory.
     */
    private void initFactory() {
        final FunctionFactoryProxy proxy =
            StandardFunctionFactory.getNewFactoryProxy();
        final FunctionFactory factory = proxy.getTargetFactory();

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
        pol.parse(role.getXacmlPolicySet());
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

            final StaxParser sp = new StaxParser(XmlUtility.NAME_ROLE);
            final RolePropertiesStaxHandler propertiesHandler =
                new RolePropertiesStaxHandler(role, roleDao);

            sp.addHandler(propertiesHandler);

            final ScopeStaxHandler scopeHandler = new ScopeStaxHandler(role);

            sp.addHandler(scopeHandler);

            final XacmlStaxHandler xacmlHandler = new XacmlStaxHandler(role);

            sp.addHandler(xacmlHandler);
            sp.parse(in);
            pol.parse(role.getXacmlPolicySet());
        }
        finally {
            IOUtils.closeStream(in);
        }
    }

    /**
     * Injects the policy parser object.
     * 
     * @param pol
     *            policy parser from Spring
     */
    public void setPolicyParser(final PolicyParser pol) {
        this.pol = pol;
    }

    /**
     * Injects the {@link XacmlFunctionRoleIsGranted}.
     * 
     * @param xacmlFunctionRoleIsGranted
     *            the {@link XacmlFunctionRoleIsGranted} to inject.
     */
    public void setXacmlFunctionRoleIsGranted(
        final XacmlFunctionRoleIsGranted xacmlFunctionRoleIsGranted) {

        this.xacmlFunctionRoleIsGranted = xacmlFunctionRoleIsGranted;
    }

    /**
     * Injects the filter values object.
     *
     * @param values
     *            filter values object from Spring
     */
    public void setValues(final Values values) {
        this.values = values;
    }
}
