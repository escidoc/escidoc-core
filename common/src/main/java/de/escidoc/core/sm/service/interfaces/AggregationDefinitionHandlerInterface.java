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
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.AggregationDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.Map;

/**
 * Interface of an Statistic AggregationDefinition Handler.
 *
 * @author Michael Hoppe
 */
public interface AggregationDefinitionHandlerInterface {

    /**
     * Creates new Aggregation Definition with given xmlData.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>Validation of the delivered XML-data</li> <li>Create the Aggregation Definition</li>
     * <li>Create associated Aggregation Tables in database.</li> <li>The XML data is returned.</li> </ul>
     *
     * @param xmlData The XML representation of the Aggregation Definition to be created corresponding to XML-schema
     *                "aggregation-definition.xsd".
     * @return The XML representation of the created Aggregation Definition corresponding to XML-schema
     *         "aggregation-definition.xsd".
     * @throws AuthenticationException      Thrown in case of failed authentication.
     * @throws AuthorizationException       Thrown in case of failed authorization.
     * @throws XmlSchemaValidationException ex
     * @throws XmlCorruptedException        ex
     * @throws MissingMethodParameterException
     *                                      ex
     * @throws ScopeNotFoundException       ex
     * @throws SystemException              ex
     */
    @Validate(param = 0, resolver = "getAggregationDefinitionSchemaLocation")
    String create(String xmlData) throws AuthenticationException, AuthorizationException, XmlSchemaValidationException,
        XmlCorruptedException, MissingMethodParameterException, ScopeNotFoundException, SystemException;

    /**
     * Delete Aggregation Definition.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Aggregation Definition must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Aggregation Definition is accessed using the provided reference.</li> <li>The
     * Aggregation Definition is deleted.</li> <li>Associated Aggregation Tables are deleted.</li> <li>No data is
     * returned.</li> </ul>
     *
     * @param aggregationDefinitionId The Aggregation Definition ID.
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws AggregationDefinitionNotFoundException
     *                                 e.
     * @throws MissingMethodParameterException
     *                                 e.
     * @throws SystemException         e.
     */
    void delete(String aggregationDefinitionId) throws AuthenticationException, AuthorizationException,
        AggregationDefinitionNotFoundException, MissingMethodParameterException, SystemException;

    /**
     * Retrieve a specified Aggregation Definition.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * The Aggregation Definition must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The Aggregation Definition is accessed using the provided reference.</li> <li>The XML
     * data is returned.</li> </ul>
     *
     * @param aggregationDefinitionId The Aggregation Definition ID.
     * @return The XML representation of the Aggregation Definition corresponding to XML-schema
     *         "aggregation-definition.xsd".
     * @throws AuthenticationException Thrown in case of failed authentication.
     * @throws AuthorizationException  Thrown in case of failed authorization.
     * @throws AggregationDefinitionNotFoundException
     *                                 e.
     * @throws MissingMethodParameterException
     *                                 e.
     * @throws SystemException         e.
     */
    String retrieve(String aggregationDefinitionId) throws AuthenticationException, AuthorizationException,
        AggregationDefinitionNotFoundException, MissingMethodParameterException, SystemException;

    /**
     * Retrieves all resources the User is allowed to see.<br/> <br/> NOTE: URI-Like Filters are deprecated and will be
     * removed in the next version of the core-framework. Please use the new PATH-like filters (eg /id instead of
     * http://purl.org/dc/elements/1.1/identifier). For further information about the filter-names, please see the
     * explain-plan.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>All Aggregation Definitions the user may see are accessed.</li> <li>The XML data is
     * returned.</li> </ul>
     *
     * @param parameters filter as CQL query
     * @return The XML representation of the list of Aggregation Definitions corresponding to XML-schema
     *         "aggregation-definition-list.xsd".
     * @throws MissingMethodParameterException
     *                                     If the parameter filter is not given.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws AuthenticationException     Thrown in case of failed authentication.
     * @throws AuthorizationException      Thrown in case of failed authorization.
     * @throws SystemException             e.
     */
    String retrieveAggregationDefinitions(Map<String, String[]> parameters) throws InvalidSearchQueryException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;
}
