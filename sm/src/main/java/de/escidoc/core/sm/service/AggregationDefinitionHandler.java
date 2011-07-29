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
import de.escidoc.core.common.exceptions.application.notfound.AggregationDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.sm.service.interfaces.AggregationDefinitionHandlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * An statistic aggregationDefinition resource handler.
 *
 * @author Michael Hoppe
 */
@Service("service.AggregationDefinitionHandler")
public class AggregationDefinitionHandler implements AggregationDefinitionHandlerInterface {

    @Autowired
    @Qualifier("business.AggregationDefinitionHandler")
    private de.escidoc.core.sm.business.interfaces.AggregationDefinitionHandlerInterface handler;

    /**
     * Protected constructor to prevent instantiation outside of the Spring-context.
     */
    protected AggregationDefinitionHandler() {
    }

    /**
     * See Interface for functional description.
     *
     * @param xmlData aggregationDefinition as xml in aggregationDefinition schema.
     * @return Returns the XML representation of the resource.
     * @throws AuthenticationException      Thrown in case of failed authentication.
     * @throws AuthorizationException       Thrown in case of failed authorization.
     * @throws XmlSchemaValidationException ex
     * @throws XmlCorruptedException        ex
     * @throws MissingMethodParameterException
     *                                      ex
     * @throws ScopeNotFoundException       ex
     * @throws SystemException              ex
     * @see de.escidoc.core.sm.service.interfaces .AggregationDefinitionHandlerInterface #create(java.lang.String)
     */
    @Override
    public String create(final String xmlData) throws AuthenticationException, AuthorizationException,
        XmlSchemaValidationException, XmlCorruptedException, MissingMethodParameterException, ScopeNotFoundException,
        SystemException {
        return handler.create(xmlData);
    }

    /**
     * See Interface for functional description.
     *
     * @param id resource id.
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws AggregationDefinitionNotFoundException
     *                                 e.
     * @throws MissingMethodParameterException
     *                                 e.
     * @throws SystemException         e.
     * @see de.escidoc.core.sm.service.interfaces .AggregationDefinitionHandlerInterface #delete(java.lang.String)
     */
    @Override
    public void delete(final String id) throws AuthenticationException, AuthorizationException,
        AggregationDefinitionNotFoundException, MissingMethodParameterException, SystemException {
        handler.delete(id);
    }

    /**
     * See Interface for functional description.
     *
     * @param id resource id.
     * @return Returns the XML representation of the resource.
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws AggregationDefinitionNotFoundException
     *                                 e.
     * @throws MissingMethodParameterException
     *                                 e.
     * @throws SystemException         e.
     * @see de.escidoc.core.sm.service.interfaces .AggregationDefinitionHandlerInterface #retrieve(java.lang.String)
     */
    @Override
    public String retrieve(final String id) throws AuthenticationException, AuthorizationException,
        AggregationDefinitionNotFoundException, MissingMethodParameterException, SystemException {
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
     * @see de.escidoc.core.sm.service.interfaces .AggregationDefinitionHandlerInterface
     *      #retrieveAggregationDefinitions(java.util.Map)
     */
    @Override
    public String retrieveAggregationDefinitions(final Map<String, String[]> parameters)
        throws InvalidSearchQueryException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        return handler.retrieveAggregationDefinitions(parameters);
    }
}
