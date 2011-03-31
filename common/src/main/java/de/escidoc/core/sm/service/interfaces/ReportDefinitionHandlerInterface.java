/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. See the License for
 * the specific language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 * Copyright 2006-2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH
 * and Max-Planck-Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license
 * terms.
 */

package de.escidoc.core.sm.service.interfaces;

import de.escidoc.core.common.annotation.Validate;
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

import java.util.Map;

/**
 * Interface of an Statistic Report Definition Handler.
 *
 * @author Michael Hoppe
 */
public interface ReportDefinitionHandlerInterface {

    /**
     * Create new Report Definition with given xmlData.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Report Definition is created. </li> <li>The XML data is returned.</li> </ul>
     *
     * @param xmlData The XML representation of the Report Definition to be created corresponding to XML-schema
     *                "report-definition.xsd".
     * @return The XML representation of the created Report Definition corresponding to XML-schema
     *         "report-definition.xsd".
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
     */
    @Validate(param = 0, resolver = "getReportDefinitionSchemaLocation")
    String create(String xmlData) throws AuthenticationException, AuthorizationException, XmlSchemaValidationException,
        XmlCorruptedException, MissingMethodParameterException, InvalidSqlException, ScopeNotFoundException,
        ScopeContextViolationException, SystemException;

    /**
     * Delete the specified Report Definition.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Report Definition must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Report Definition is accessed using the provided reference.</li> <li>The Report
     * Definition is deleted.</li> <li>No data is returned.</li> </ul>
     *
     * @param reportDefinitionId The Report Definition ID to be deleted.
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws ReportDefinitionNotFoundException
     *                                 e.
     * @throws MissingMethodParameterException
     *                                 e.
     * @throws SystemException         e.
     */
    void delete(String reportDefinitionId) throws AuthenticationException, AuthorizationException,
        ReportDefinitionNotFoundException, MissingMethodParameterException, SystemException;

    /**
     * Retrieve the Report Definition with the given id.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Report Definition must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Report Definition is accessed using the provided reference.</li> <li>The XML data
     * is returned.</li> </ul>
     *
     * @param reportDefinitionId The Report Definition ID to be retrieved.
     * @return The XML representation of the retrieved Report Definition corresponding to XML-schema
     *         "report-definition.xsd".
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws ReportDefinitionNotFoundException
     *                                 e.
     * @throws MissingMethodParameterException
     *                                 e.
     * @throws SystemException         e.
     */
    String retrieve(String reportDefinitionId) throws AuthenticationException, AuthorizationException,
        ReportDefinitionNotFoundException, MissingMethodParameterException, SystemException;

    /**
     * Retrieves all resources the User is allowed to see.<br/> <br/> NOTE: URI-Like Filters are deprecated and will be
     * removed in the next version of the core-framework. Please use the new PATH-like filters (eg /id instead of
     * http://purl.org/dc/elements/1.1/identifier). For further information about the filter-names, please see the
     * explain-plan.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Report Definitions are accessed using the provided filter.</li> <li>Supported
     * criteria for filtering and sorting: <ul> <li>"http://purl.org/dc/elements/1.1/identifier" and "/id": Report
     * Definition ID</li> <li>"http://escidoc.de/core/01/properties/name" and "/properties/name": Report Definition
     * Name</li> </ul> </li> <li>The XML data is returned.</li> </ul>
     *
     * @param filter filter as CQL query
     * @return The XML representation of the Report Definitions corresponding to XML-schema "srw-types.xsd". List only
     *         contains these Report Definitions the user is allowed to see.
     * @throws MissingMethodParameterException
     *                                     If the parameter filter is not given.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws AuthenticationException     Thrown in case of failed authentication.
     * @throws AuthorizationException      Thrown in case of failed authorization.
     * @throws SystemException             e.
     */
    String retrieveReportDefinitions(Map<String, String[]> filter) throws InvalidSearchQueryException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    /**
     * Update the Report Definition.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The provided XML data in the body is only accepted if the size is less than ESCIDOC_MAX_XML_SIZE.<br/>
     * <p/>
     * The Report Definition must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>the Report Definition is updated. </li> <li>The XML data is returned.</li> </ul>
     *
     * @param reportDefinitionId The Report Definition ID.
     * @param xmlData            The XML representation of the Report Definition to be created corresponding to
     *                           XML-schema "report-definition.xsd".
     * @return The XML representation of the updated Report Definition corresponding to XML-schema
     *         "report-definition.xsd".
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
     */
    @Validate(param = 1, resolver = "getReportDefinitionSchemaLocation")
    String update(String reportDefinitionId, String xmlData) throws AuthenticationException, AuthorizationException,
        ReportDefinitionNotFoundException, MissingMethodParameterException, ScopeNotFoundException,
        InvalidSqlException, ScopeContextViolationException, XmlSchemaValidationException, XmlCorruptedException,
        SystemException;
}
