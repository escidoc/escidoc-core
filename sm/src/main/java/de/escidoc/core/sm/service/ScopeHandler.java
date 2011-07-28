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
package de.escidoc.core.sm.service;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.sm.service.interfaces.ScopeHandlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * An statistic Scope resource handler.
 *
 * @author Michael Hoppe
 */
@Service("service.ScopeHandler")
public class ScopeHandler implements ScopeHandlerInterface {

    @Autowired
    @Qualifier("business.ScopeHandler")
    private de.escidoc.core.sm.business.interfaces.ScopeHandlerInterface handler;

    /**
     * Private constructor to prevent initialization.
     */
    protected ScopeHandler() {
    }

    /**
     * See Interface for functional description.
     *
     * @param xmlData Scope as xml in Scope schema.
     * @return Returns the XML representation of the resource.
     * @throws AuthenticationException      Thrown in case of failed authentication.
     * @throws AuthorizationException       Thrown in case of failed authorization.
     * @throws XmlSchemaValidationException ex
     * @throws XmlCorruptedException        ex
     * @throws MissingMethodParameterException
     *                                      ex
     * @throws SystemException              ex
     * @see de.escidoc.core.sm.service.interfaces .ScopeHandlerInterface #create(java.lang.String)
     */
    @Override
    public String create(final String xmlData) throws AuthenticationException, AuthorizationException,
        XmlSchemaValidationException, XmlCorruptedException, MissingMethodParameterException, SystemException {
        return handler.create(xmlData);
    }

    /**
     * See Interface for functional description.
     *
     * @param id resource id.
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws ScopeNotFoundException  e.
     * @throws MissingMethodParameterException
     *                                 e.
     * @throws SystemException         e.
     * @see de.escidoc.core.sm.service.interfaces .ScopeHandlerInterface #delete(java.lang.String)
     */
    @Override
    public void delete(final String id) throws AuthenticationException, AuthorizationException, ScopeNotFoundException,
        MissingMethodParameterException, SystemException {
        handler.delete(id);
    }

    /**
     * See Interface for functional description.
     *
     * @param id resource id.
     * @return Returns the XML representation of the resource.
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws ScopeNotFoundException  e.
     * @throws MissingMethodParameterException
     *                                 e.
     * @throws SystemException         e.
     * @see de.escidoc.core.sm.service.interfaces .ScopeHandlerInterface #retrieve(java.lang.String)
     */
    @Override
    public String retrieve(final String id) throws AuthenticationException, AuthorizationException,
        ScopeNotFoundException, MissingMethodParameterException, SystemException {
        return handler.retrieve(id);
    }

    /**
     * See Interface for functional description.
     *
     * @param parameters filter as CQL query
     * @return Returns the XML representation of the resource-list.
     * @throws MissingMethodParameterException
     *                                     If the parameter filter is not given.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws AuthenticationException     Thrown in case of failed authentication.
     * @throws AuthorizationException      Thrown in case of failed authorization.
     * @throws SystemException             e.
     * @see de.escidoc.core.sm.service.interfaces .ScopeHandlerInterface #retrieveScopes(java.util.Map)
     */
    @Override
    public String retrieveScopes(final Map<String, String[]> parameters) throws InvalidSearchQueryException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return handler.retrieveScopes(parameters);
    }

    /**
     * See Interface for functional description.
     *
     * @param xmlData Scope data as xml in Scope schema.
     * @param id      resource id.
     * @return Returns the XML representation of the resource.
     * @throws AuthenticationException      Thrown in case of failed authentication.
     * @throws AuthorizationException       Thrown in case of failed authorization.
     * @throws ScopeNotFoundException       e.
     * @throws MissingMethodParameterException
     *                                      e.
     * @throws XmlSchemaValidationException e.
     * @throws XmlCorruptedException        e.
     * @throws SystemException              e.
     * @see de.escidoc.core.sm.service.interfaces .ScopeHandlerInterface #update(java.lang.String,java.lang.String)
     */
    @Override
    public String update(final String id, final String xmlData) throws AuthenticationException, AuthorizationException,
        ScopeNotFoundException, MissingMethodParameterException, XmlSchemaValidationException, XmlCorruptedException,
        SystemException {
        return handler.update(id, xmlData);
    }

}
