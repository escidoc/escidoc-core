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
package de.escidoc.core.sm.business.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSqlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.violated.ScopeContextViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.Map;

/**
 * Interface of an Statistic Report Definition Handler of the business layer.
 *
 * @author Michael Hoppe
 */
public interface ReportDefinitionHandlerInterface {

    /**
     * Creates new Report Definition with given xmlData.
     *
     * @param xmlData Report Definition as xml in Report Definition schema.
     * @return Returns the XML representation of the Report Definition.
     * @throws MissingMethodParameterException
     *                                        ex
     * @throws ScopeNotFoundException         ex
     * @throws ScopeContextViolationException ex
     * @throws InvalidSqlException            ex
     * @throws SystemException                ex
     */
    String create(String xmlData) throws MissingMethodParameterException, InvalidSqlException, ScopeNotFoundException,
        ScopeContextViolationException, SystemException;

    /**
     * Deletes the specified Report Definition.
     *
     * @param reportDefinitionId The Report Definition ID.
     * @throws ReportDefinitionNotFoundException
     *                         e.
     * @throws MissingMethodParameterException
     *                         e.
     * @throws SystemException e.
     */
    void delete(String reportDefinitionId) throws ReportDefinitionNotFoundException, MissingMethodParameterException,
        SystemException;

    /**
     * Retrieves the specified Report Definition.
     *
     * @param reportDefinitionId The Report Definition ID.
     * @return Returns the XML representation of the Report Definition.
     * @throws ReportDefinitionNotFoundException
     *                         e.
     * @throws MissingMethodParameterException
     *                         e.
     * @throws SystemException e.
     */
    String retrieve(String reportDefinitionId) throws ReportDefinitionNotFoundException,
        MissingMethodParameterException, SystemException;

    /**
     * Retrieves all Report Definitions the user is allowed to see.
     *
     * @param filter filter as CQL query
     * @return Returns the XML representation of the Report Definition-list.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SystemException             e.
     */
    String retrieveReportDefinitions(Map<String, String[]> filter) throws InvalidSearchQueryException, SystemException;

    /**
     * Updates the specified Report Definition with the provided data.
     *
     * @param reportDefinitionId The Report Definition ID.
     * @param xmlData            The new data of the Report Definition.
     * @return Returns the XML representation of the updated Report Definition.
     * @throws ReportDefinitionNotFoundException
     *                                        e.
     * @throws MissingMethodParameterException
     *                                        e.
     * @throws ScopeNotFoundException         ex
     * @throws ScopeContextViolationException ex
     * @throws InvalidSqlException            ex
     * @throws SystemException                e.
     */
    String update(String reportDefinitionId, String xmlData) throws ReportDefinitionNotFoundException,
        MissingMethodParameterException, ScopeNotFoundException, InvalidSqlException, ScopeContextViolationException,
        SystemException;
}
