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
package de.escidoc.core.sm.business;

import de.escidoc.core.common.business.fedora.Utility;
import de.escidoc.core.common.business.filter.DbRequestParameters;
import de.escidoc.core.common.business.filter.SRURequestParameters;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.system.SqlDatabaseSystemException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.factory.ExplainXmlProvider;
import de.escidoc.core.sm.business.filter.ScopeFilter;
import de.escidoc.core.sm.business.interfaces.ScopeHandlerInterface;
import de.escidoc.core.sm.business.persistence.SmScopesDaoInterface;
import de.escidoc.core.sm.business.persistence.hibernate.Scope;
import de.escidoc.core.sm.business.renderer.interfaces.ScopeRendererInterface;
import de.escidoc.core.sm.business.stax.handler.ScopeStaxHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An statistic Scope resource handler.
 *
 * @author Michael Hoppe
 */
@Service("business.ScopeHandler")
@org.springframework.context.annotation.Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ScopeHandler implements ScopeHandlerInterface {

    @Autowired
    @Qualifier("persistence.SmScopesDao")
    private SmScopesDaoInterface dao;

    @Autowired
    @Qualifier("business.sm.FilterUtility")
    private SmFilterUtility filterUtility;

    @Autowired
    @Qualifier("eSciDoc.core.aa.business.renderer.VelocityXmlScopeRenderer")
    private ScopeRendererInterface renderer;

    /**
     * See Interface for functional description.
     *
     * @param xmlData Scope as xml in Scope schema.
     * @return Returns the XML representation of the resource.
     * @throws MissingMethodParameterException
     *                         ex
     * @throws SystemException ex
     * @see de.escidoc.core.sm.business.interfaces .ScopeHandlerInterface #create(java.lang.String)
     */
    @Override
    public String create(final String xmlData) throws MissingMethodParameterException, SystemException {
        if (xmlData == null || xmlData.length() == 0) {
            throw new MissingMethodParameterException("xml may not be null");
        }
        // parse
        final StaxParser sp = new StaxParser();
        final ScopeStaxHandler handler = new ScopeStaxHandler();
        sp.addHandler(handler);
        try {
            sp.parse(xmlData);
        }
        catch (final Exception e) {
            throw new SystemException(e);
        }

        final Scope scope = handler.getScope();
        final Utility utility = new Utility();
        scope.setCreatorId(utility.getCurrentUserId());
        scope.setModifiedById(scope.getCreatorId());
        scope.setLastModificationDate(new Timestamp(System.currentTimeMillis()));
        scope.setCreationDate(scope.getLastModificationDate());

        dao.save(scope);

        return renderer.render(scope);
    }

    /**
     * See Interface for functional description.
     *
     * @param id resource id.
     * @throws ScopeNotFoundException e.
     * @throws MissingMethodParameterException
     *                                e.
     */
    @Override
    public void delete(final String id) throws ScopeNotFoundException, MissingMethodParameterException,
        SqlDatabaseSystemException {
        if (id == null) {
            throw new MissingMethodParameterException("id may not be null");
        }
        final Scope scope = dao.retrieve(id);
        dao.delete(scope);
    }

    /**
     * See Interface for functional description.
     *
     * @param id resource id.
     * @return Returns the XML representation of the resource.
     * @throws ScopeNotFoundException e.
     * @throws MissingMethodParameterException
     *                                e.
     * @throws SystemException        e.
     * @see de.escidoc.core.sm.business.interfaces .ScopeHandlerInterface #retrieve(java.lang.String)
     */
    @Override
    public String retrieve(final String id) throws ScopeNotFoundException, MissingMethodParameterException,
        SystemException {
        if (id == null) {
            throw new MissingMethodParameterException("id may not be null");
        }
        return renderer.render(dao.retrieve(id));
    }

    /**
     * See Interface for functional description.
     *
     * @param parameters filter as CQL query
     * @return Returns the XML representation of the resource-list.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SystemException             e.
     * @see de.escidoc.core.sm.business.interfaces .ScopeHandlerInterface#retrieveScopes(java.util.Map)
     */
    @Override
    public String retrieveScopes(final Map<String, String[]> parameters) throws InvalidSearchQueryException,
        SystemException {
        final String result;
        final SRURequestParameters params = new DbRequestParameters(parameters);
        final String query = params.getQuery();
        final int limit = params.getMaximumRecords();
        final int offset = params.getStartRecord();

        if (params.isExplain()) {
            final Map<String, Object> values = new HashMap<String, Object>();

            values.put("PROPERTY_NAMES", new ScopeFilter(null).getPropertyNames());
            result = ExplainXmlProvider.getInstance().getExplainScopeXml(values);
        }
        else if (limit == 0) {
            result = renderer.renderScopes(new ArrayList<Scope>(0), params.getRecordPacking());
        }
        else {
            // get all scope-ids from database
            final List<String> scopeIds = dao.retrieveScopeIds();

            Collection<String> filteredScopeIds = null;
            Collection<Scope> scopes = new ArrayList<Scope>();

            if (scopeIds != null && !scopeIds.isEmpty()) {
                // get scope-ids filtered by user-privileges
                filteredScopeIds = filterUtility.filterRetrievePrivilege(Constants.SCOPE_OBJECT_TYPE, scopeIds);
            }
            if (filteredScopeIds != null && !filteredScopeIds.isEmpty()) {
                // get scopes as XML
                scopes = dao.retrieveScopes(filteredScopeIds, query, offset, limit);
            }

            result = renderer.renderScopes(scopes, params.getRecordPacking());
        }
        return result;
    }

    /**
     * See Interface for functional description.
     *
     * @param xmlData Scope data as xml in Scope schema.
     * @param id      resource id.
     * @return Returns the XML representation of the resource.
     * @throws ScopeNotFoundException e.
     * @throws MissingMethodParameterException
     *                                e.
     * @throws SystemException        e.
     * @see de.escidoc.core.sm.business.interfaces .ScopeHandlerInterface #update(java.lang.String,java.lang.String)
     */
    @Override
    public String update(final String id, final String xmlData) throws ScopeNotFoundException,
        MissingMethodParameterException, SystemException {
        if (id == null) {
            throw new MissingMethodParameterException("id may not be null");
        }
        if (xmlData == null) {
            throw new MissingMethodParameterException("xmlData may not be null");
        }

        // parse
        final StaxParser sp = new StaxParser();
        final ScopeStaxHandler handler = new ScopeStaxHandler();
        handler.setScope(dao.retrieve(id));
        sp.addHandler(handler);
        try {
            sp.parse(xmlData);
        }
        catch (final Exception e) {
            throw new SystemException(e);
        }

        final Scope scope = handler.getScope();
        final Utility utility = new Utility();
        scope.setModifiedById(utility.getCurrentUserId());
        scope.setLastModificationDate(new Timestamp(System.currentTimeMillis()));

        dao.update(scope);

        return renderer.render(scope);
    }

}
