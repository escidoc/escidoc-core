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

package de.escidoc.core.oai.service.interfaces;

import de.escidoc.core.common.annotation.Validate;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

import java.util.Map;

/**
 * Interface of an set definition handler.
 *
 * @author Rozita Friedman
 */
public interface SetDefinitionHandlerInterface {

    /**
     * Create a set definition resource.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/> See chapter 4 for detailed information about input and output data elements<br/>
     * <b>Tasks:</b><br/> <ul> <li>The XML data is validated against the XML-Schema of a set-definition. </li> <li>It's
     * checked weather the set specification is unique within the system.</li> <li>Some new data is added to the input
     * xml(see Chapter 4)</li> <li>The XML representation of the set-definition corresponding to XML-schema is returned
     * as output.</li> </ul>
     *
     * @param setDefinition The data of the resource.
     * @return The XML representation of the created set-definition corresponding to XML-schema "set-definition.xsd".
     * @throws UniqueConstraintViolationException
     *                                 If the specification of the created set-definition is not unique within the
     *                                 system.
     * @throws InvalidXmlException     If the provided data is not valid XML.
     * @throws MissingMethodParameterException
     *                                 If a set-definition data is missing.
     * @throws SystemException         If an error occurs
     * @throws AuthenticationException Thrown if the authentication fails due to an invalid provided eSciDocUserHandle.
     * @throws AuthorizationException  Thrown if the authorization fails.
     */
    @Validate(param = 0, resolver = "getSetDefinitionSchemaLocation")
    String create(final String setDefinition) throws UniqueConstraintViolationException, InvalidXmlException,
        MissingMethodParameterException, SystemException, AuthenticationException, AuthorizationException;

    /**
     * Retrieve a set definition.<br/>
     * <p/>
     * <b>Prerequisites:</b><br/> The set-definition must exist<br/>
     * <p/>
     * <b>Tasks:</b><br/> <ul> <li>The set-definition is accessed using the provided reference.</li> <li>The XML
     * representation of the set-definition corresponding to XML-schema is returned as output.</li> </ul>
     *
     * @param id The id of the set-definition to be retrieved.
     * @return The XML representation of the retrieved set-definition corresponding to XML-schema "set-definition.xsd".
     * @throws ResourceNotFoundException Thrown if a set-definition with the specified id cannot be found.
     * @throws MissingMethodParameterException
     *                                   If a set-definition id is missing.
     * @throws SystemException           If an error occurs.
     * @throws AuthenticationException   Thrown if the authentication fails due to an invalid provided
     *                                   eSciDocUserHandle.
     * @throws AuthorizationException    Thrown if the authorization fails.
     */
    String retrieve(final String id) throws ResourceNotFoundException, MissingMethodParameterException,
        SystemException, AuthenticationException, AuthorizationException;

    /**
     * Update an set-definition<br/> <b>Prerequisites:</b> <br/> The set-definition must exist.<br/> See chapter 4 for
     * detailed information about input and output data elements<br/> <b>Tasks:</b><br/> <ul> <li>The XML data is
     * validated against the XML-Schema of a set-definition. </li> <li>Optimistic Locking criteria is checked.</li>
     * <li>If changed, a description and a name are updated.</li> <li>The XML input data is updated.(see Chapter 4)</li>
     * <li>The XML representation of the set-definition corresponding to XML-schema is returned as output.</li> </ul>
     *
     * @param id      The id of the set-definition to be updated.
     * @param xmlData The XML representation of the set-definition to be updated corresponding to XML-schema
     *                "set-definition.xsd".
     * @return The XML representation of the updated set-definition corresponding to XML-schema "set-definition.xsd".
     * @throws ResourceNotFoundException  Thrown if an set-definition with the specified id cannot be found.
     * @throws OptimisticLockingException If the provided latest-modification-date does not match.
     * @throws MissingMethodParameterException
     *                                    if some of data is not provided.
     * @throws SystemException            If an error occurs.
     * @throws AuthenticationException    Thrown if the authentication fails due to an invalid provided
     *                                    eSciDocUserHandle.
     * @throws AuthorizationException     Thrown if the authorization fails.
     */
    @Validate(param = 1, resolver = "getSetDefinitionSchemaLocation")
    String update(final String id, final String xmlData) throws ResourceNotFoundException, OptimisticLockingException,
        MissingMethodParameterException, SystemException, AuthenticationException, AuthorizationException;

    /**
     * Delete a set-definition.<br/> <b>Prerequisites:</b><br/> The set-definition must exist<br/> <b>Tasks:</b><br/>
     * <ul> <li>The set-definition will be deleted from the system.</li> </ul>
     *
     * @param id The id of the set-definition to be deleted
     * @throws ResourceNotFoundException Thrown if a set-definition with the specified id cannot be found.
     * @throws MissingMethodParameterException
     *                                   If a set-definition id is missing.
     * @throws SystemException           If an error occurs.
     * @throws AuthenticationException   Thrown if the authentication fails due to an invalid provided
     *                                   eSciDocUserHandle.
     * @throws AuthorizationException    Thrown if the authorization fails.
     */
    void delete(final String id) throws ResourceNotFoundException, MissingMethodParameterException, SystemException,
        AuthenticationException, AuthorizationException;

    /**
     * Retrieves a list of completes set-definitions applying filters.<br/> <br/> NOTE: URI-Like Filters are deprecated
     * and will be removed in the next version of the core-framework. Please use the new PATH-like filters (eg /id
     * instead of http://purl.org/dc/elements/1.1/identifier). For further information about the filter-names, please
     * see the explain-plan.<br/> <b>Tasks:</b><br/> <ul> <li>Check weather all filter names are valid.</li> <li>The
     * set-definitions are accessed using the provided filters.</li> <li>The XML representation of the list of all
     * set-definitions corresponding to SRW schema is returned as output.</li> </ul> <br/> See chapter "Filters" for
     * detailed information about filter definitions.
     *
     * @param filter Simple XML containing the filter definition. See functional specification.
     * @return Returns the XML representation of found set-definitions.
     * @throws AuthenticationException     Thrown if the authentication fails due to an invalid provided
     *                                     eSciDocUserHandle.
     * @throws AuthorizationException      Thrown if the authorization fails.
     * @throws MissingMethodParameterException
     *                                     If the parameter filter is not given.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SystemException             If an error occurs.
     */
    String retrieveSetDefinitions(final Map<String, String[]> filter) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, InvalidSearchQueryException, SystemException;
}
