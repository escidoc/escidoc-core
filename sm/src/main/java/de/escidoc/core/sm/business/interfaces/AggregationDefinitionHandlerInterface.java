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
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.AggregationDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.Map;

/**
 * Interface of an Statistic Aggregation Definition Handler of the business layer.
 *
 * @author Michael Hoppe
 */
public interface AggregationDefinitionHandlerInterface {

    /**
     * Creates new Aggregation Definition with given xmlData.
     *
     * @param xmlData Aggregation Definition as xml in Aggregation Definition schema.
     * @return Returns the XML representation of the resource.
     * @throws MissingMethodParameterException
     *                                ex
     * @throws ScopeNotFoundException ex
     * @throws SystemException        ex
     */
    String create(String xmlData) throws MissingMethodParameterException, ScopeNotFoundException, SystemException;

    /**
     * Deletes the specified resource.
     *
     * @param aggregationDefinitionId The Aggregation Definition ID of the resource.
     * @throws AggregationDefinitionNotFoundException
     *                         e.
     * @throws MissingMethodParameterException
     *                         e.
     * @throws SystemException e.
     */
    void delete(String aggregationDefinitionId) throws AggregationDefinitionNotFoundException,
        MissingMethodParameterException, SystemException;

    /**
     * Retrieves the specified resource.
     *
     * @param aggregationDefinitionId The Aggregation Definition ID of the resource.
     * @return Returns the XML representation of the Aggregation Definition.
     * @throws AggregationDefinitionNotFoundException
     *                         e.
     * @throws MissingMethodParameterException
     *                         e.
     * @throws SystemException e.
     */
    String retrieve(String aggregationDefinitionId) throws AggregationDefinitionNotFoundException,
        MissingMethodParameterException, SystemException;

    /**
     * Retrieves all Aggregation Definitions the user is allowed to see.
     *
     * @param filter filter as CQL query
     * @return Returns the XML representation of the Aggregation Definition-list.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SystemException             e.
     */
    String retrieveAggregationDefinitions(Map<String, String[]> filter) throws InvalidSearchQueryException,
        SystemException;
}
