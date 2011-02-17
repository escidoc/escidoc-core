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
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.logger.AppLogger;
import de.escidoc.core.common.util.stax.StaxParser;
import de.escidoc.core.common.util.xml.factory.ExplainXmlProvider;
import de.escidoc.core.sm.business.filter.ScopeFilter;
import de.escidoc.core.sm.business.interfaces.ScopeHandlerInterface;
import de.escidoc.core.sm.business.persistence.SmScopesDaoInterface;
import de.escidoc.core.sm.business.persistence.hibernate.Scope;
import de.escidoc.core.sm.business.renderer.interfaces.ScopeRendererInterface;
import de.escidoc.core.sm.business.stax.handler.ScopeStaxHandler;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * An statistic Scope resource handler.
 * 
 * @spring.bean id="business.ScopeHandler" scope="prototype"
 * @author MIH
 */
public class ScopeHandler implements ScopeHandlerInterface {

    private static AppLogger log = new AppLogger(ScopeHandler.class.getName());

    private SmScopesDaoInterface dao;

    private SmFilterUtility filterUtility;

    private ScopeRendererInterface renderer;

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.interfaces .ScopeHandlerInterface
     *      #create(java.lang.String)
     * 
     * @param xmlData
     *            Scope as xml in Scope schema.
     * @return Returns the XML representation of the resource.
     * 
     * @throws MissingMethodParameterException
     *             ex
     * @throws SystemException
     *             ex
     * 
     */
    public String create(final String xmlData)
        throws MissingMethodParameterException, SystemException {
        if (log.isDebugEnabled()) {
            log.debug("ScopeHandler does create");
        }
        if (xmlData == null || xmlData.equals("")) {
            log.error("xml may not be null");
            throw new MissingMethodParameterException("xml may not be null");
        }

        // parse
        StaxParser sp = new StaxParser();
        ScopeStaxHandler handler = new ScopeStaxHandler();
        sp.addHandler(handler);
        try {
            sp.parse(xmlData);
        }
        catch (Exception e) {
            log.error(e);
            throw new SystemException(e);
        }

        Scope scope = handler.getScope();
        Utility utility = new Utility();
        scope.setCreatorId(utility.getCurrentUserId());
        scope.setModifiedById(scope.getCreatorId());
        scope
            .setLastModificationDate(new Timestamp(System.currentTimeMillis()));
        scope.setCreationDate(scope.getLastModificationDate());

        dao.save(scope);

        return renderer.render(scope);
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.interfaces .ScopeHandlerInterface
     *      #delete(java.lang.String)
     * 
     * @param id
     *            resource id.
     * 
     * @throws ScopeNotFoundException
     *             e.
     * @throws MissingMethodParameterException
     *             e.
     * @throws SystemException
     *             e.
     * 
     */
    public void delete(final String id) throws ScopeNotFoundException,
        MissingMethodParameterException, SystemException {
        if (log.isDebugEnabled()) {
            log.debug("ScopeHandler does delete");
        }
        if (id == null) {
            log.error("id may not be null");
            throw new MissingMethodParameterException("id may not be null");
        }
        Scope scope = null;
        scope = dao.retrieve(id);
        dao.delete(scope);
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.interfaces .ScopeHandlerInterface
     *      #retrieve(java.lang.String)
     * 
     * @param id
     *            resource id.
     * @return Returns the XML representation of the resource.
     * 
     * @throws ScopeNotFoundException
     *             e.
     * @throws MissingMethodParameterException
     *             e.
     * @throws SystemException
     *             e.
     * 
     */
    public String retrieve(final String id) throws ScopeNotFoundException,
        MissingMethodParameterException, SystemException {
        if (log.isDebugEnabled()) {
            log.debug("ScopeHandler does retrieve");
        }
        if (id == null) {
            log.error("id may not be null");
            throw new MissingMethodParameterException("id may not be null");
        }
        return renderer.render(dao.retrieve(id));
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.interfaces
     *      .ScopeHandlerInterface#retrieveScopes(java.util.Map)
     * 
     * @param parameters
     *            filter as CQL query
     * @return Returns the XML representation of the resource-list.
     * @throws InvalidSearchQueryException
     *             thrown if the given search query could not be translated into
     *             a SQL query
     * @throws SystemException
     *             e.
     */
    public String retrieveScopes(final Map<String, String[]> parameters)
        throws InvalidSearchQueryException, SystemException {
        String result = null;
        SRURequestParameters params =
            new DbRequestParameters((Map<String, String[]>) parameters);
        String query = params.getQuery();
        int limit = params.getLimit();
        int offset = params.getOffset();

        if (params.isExplain()) {
            Map<String, Object> values = new HashMap<String, Object>();

            values.put("PROPERTY_NAMES",
                new ScopeFilter(null).getPropertyNames());
            result =
                ExplainXmlProvider.getInstance().getExplainScopeXml(values);
        }
        else {
            // get all scope-ids from database
            Collection<String> scopeIds = dao.retrieveScopeIds();

            Collection<String> filteredScopeIds = null;
            Collection<Scope> scopes = new ArrayList<Scope>();

            if (scopeIds != null && !scopeIds.isEmpty()) {
                // get scope-ids filtered by user-privileges
                filteredScopeIds =
                    filterUtility.filterRetrievePrivilege(
                        Constants.SCOPE_OBJECT_TYPE, scopeIds);
            }

//            int numberOfRecords = 0;

            if (filteredScopeIds != null && !filteredScopeIds.isEmpty()) {
                // get scopes as XML
                scopes =
                    dao.retrieveScopes(filteredScopeIds, query, offset, limit);
                if (scopes != null) {
//                    numberOfRecords = scopes.size();
                }
            }

            result =
                renderer.renderScopes(scopes);
        }
        return result;
    }

    /**
     * See Interface for functional description.
     * 
     * @see de.escidoc.core.sm.business.interfaces .ScopeHandlerInterface
     *      #update(java.lang.String,java.lang.String)
     * 
     * @param xmlData
     *            Scope data as xml in Scope schema.
     * @param id
     *            resource id.
     * @return Returns the XML representation of the resource.
     * 
     * @throws ScopeNotFoundException
     *             e.
     * @throws MissingMethodParameterException
     *             e.
     * @throws SystemException
     *             e.
     * 
     */
    public String update(final String id, final String xmlData)
        throws ScopeNotFoundException, MissingMethodParameterException,
        SystemException {
        if (log.isDebugEnabled()) {
            log.debug("ScopeHandler does update");
        }
        if (id == null) {
            log.error("id may not be null");
            throw new MissingMethodParameterException("id may not be null");
        }
        if (xmlData == null) {
            log.error("xmlData may not be null");
            throw new MissingMethodParameterException("xmlData may not be null");
        }

        // parse
        StaxParser sp = new StaxParser();
        ScopeStaxHandler handler = new ScopeStaxHandler();
        handler.setScope(dao.retrieve(id));
        sp.addHandler(handler);
        try {
            sp.parse(xmlData);
        }
        catch (Exception e) {
            log.error(e);
            throw new SystemException(e);
        }

        Scope scope = handler.getScope();
        Utility utility = new Utility();
        scope.setModifiedById(utility.getCurrentUserId());
        scope
            .setLastModificationDate(new Timestamp(System.currentTimeMillis()));

        dao.update(scope);

        return renderer.render(scope);
    }

    /**
     * Setter for the dao.
     * 
     * @spring.property ref="persistence.SmScopesDao"
     * @param dao
     *            The data access object.
     * 
     */
    public void setDao(final SmScopesDaoInterface dao) {
        this.dao = dao;
    }

    /**
     * Setting the filterUtility.
     * 
     * @param filterUtility
     *            The filterUtility to set.
     * @spring.property ref="business.sm.FilterUtility"
     */
    public final void setFilterUtility(final SmFilterUtility filterUtility) {
        this.filterUtility = filterUtility;
    }

    /**
     * Injects the renderer.
     * 
     * @param renderer
     *            The renderer to inject.
     * 
     * @spring.property 
     *                  ref="eSciDoc.core.aa.business.renderer.VelocityXmlScopeRenderer"
     */
    public void setRenderer(final ScopeRendererInterface renderer) {
        this.renderer = renderer;
    }

}
