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
import de.escidoc.core.common.exceptions.application.invalid.InvalidSqlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.ScopeContextViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.sm.service.interfaces.ReportDefinitionHandlerInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * A statistic ReportDefinition resource handler.
 *
 * @author Michael Hoppe
 */
@Service("service.ReportDefinitionHandler")
public class ReportDefinitionHandler implements ReportDefinitionHandlerInterface {

    @Autowired
    @Qualifier("business.ReportDefinitionHandler")
    private de.escidoc.core.sm.business.interfaces.ReportDefinitionHandlerInterface handler;

    /**
     * See Interface for functional description.
     *
     * @param xmlData ReportDefinition as xml in ReportDefinition schema.
     * @return Returns the XML representation of the resource.
     * @throws AuthenticationException        Thrown in case of failed authentication.
     * @throws AuthorizationException         Thrown in case of failed authorization.
     * @throws XmlSchemaValidationException   ex
     * @throws XmlCorruptedException          ex
     * @throws MissingMethodParameterException
     *                                        ex
     * @throws ScopeNotFoundException         ex
     * @throws ScopeContextViolationException ex
     * @throws InvalidSqlException            ex
     * @throws SystemException                ex
     * @see de.escidoc.core.sm.service.interfaces .ReportDefinitionHandlerInterface #create(java.lang.String)
     */
    @Override
    public String create(final String xmlData) throws AuthenticationException, AuthorizationException,
        XmlSchemaValidationException, XmlCorruptedException, InvalidSqlException, MissingMethodParameterException,
        ScopeNotFoundException, ScopeContextViolationException, SystemException {
        return handler.create(xmlData);
    }

    /**
     * See Interface for functional description.
     *
     * @param id resource id.
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws ReportDefinitionNotFoundException
     *                                 e.
     * @throws MissingMethodParameterException
     *                                 e.
     * @throws SystemException         e.
     * @see de.escidoc.core.sm.service.interfaces .ReportDefinitionHandlerInterface #delete(java.lang.String)
     */
    @Override
    public void delete(final String id) throws AuthenticationException, AuthorizationException,
        ReportDefinitionNotFoundException, MissingMethodParameterException, SystemException {
        handler.delete(id);
    }

    /**
     * See Interface for functional description.
     *
     * @param id resource id.
     * @return Returns the XML representation of the resource.
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws ReportDefinitionNotFoundException
     *                                 e.
     * @throws MissingMethodParameterException
     *                                 e.
     * @throws SystemException         e.
     * @see de.escidoc.core.sm.service.interfaces .ReportDefinitionHandlerInterface #retrieve(java.lang.String)
     */
    @Override
    public String retrieve(final String id) throws AuthenticationException, AuthorizationException,
        ReportDefinitionNotFoundException, MissingMethodParameterException, SystemException {
        return handler.retrieve(id);
    }

    /**
     * See Interface for functional description.
     *
     * @param filter filter as CQL query
     * @return Returns the XML representation of the resource-list.
     * @throws MissingMethodParameterException
     *                                     If the parameter filter is not given.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws AuthenticationException     Thrown in case of failed authentication.
     * @throws AuthorizationException      Thrown in case of failed authorization.
     * @throws SystemException             e.
     * @see de.escidoc.core.sm.service.interfaces .ReportDefinitionHandlerInterface #retrieveReportDefinitions()
     */
    @Override
    public String retrieveReportDefinitions(final Map<String, String[]> filter) throws InvalidSearchQueryException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        return handler.retrieveReportDefinitions(filter);
    }

    /**
     * See Interface for functional description.
     *
     * @param xmlData ReportDefinition data as xml in ReportDefinition schema.
     * @param id      resource id.
     * @return Returns the XML representation of the resource.
     * @throws AuthenticationException        Thrown in case of failed authentication.
     * @throws AuthorizationException         Thrown in case of failed authorization.
     * @throws ReportDefinitionNotFoundException
     *                                        e.
     * @throws MissingMethodParameterException
     *                                        e.
     * @throws ScopeNotFoundException         ex
     * @throws ScopeContextViolationException ex
     * @throws InvalidSqlException            ex
     * @throws XmlSchemaValidationException   e.
     * @throws XmlCorruptedException          e.
     * @throws SystemException                e.
     * @see de.escidoc.core.sm.service.interfaces .ReportDefinitionHandlerInterface #update(java.lang.String,java.lang.String)
     */
    @Override
    public String update(final String id, final String xmlData) throws AuthenticationException, AuthorizationException,
        ReportDefinitionNotFoundException, MissingMethodParameterException, ScopeNotFoundException,
        InvalidSqlException, ScopeContextViolationException, XmlSchemaValidationException, XmlCorruptedException,
        SystemException {
        return handler.update(id, xmlData);
    }

}
