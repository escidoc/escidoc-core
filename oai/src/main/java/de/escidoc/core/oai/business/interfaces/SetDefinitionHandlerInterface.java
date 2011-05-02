package de.escidoc.core.oai.business.interfaces;

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

public interface SetDefinitionHandlerInterface {

    /**
     * Create a set definition resource.
     *
     * @param setDefinition The data of the resource.
     * @return The XML representation of the created set-definition corresponding to XML-schema "set-definition.xsd".
     * @throws UniqueConstraintViolationException
     *                             If the specification of the created set-definition is not unique within the system.
     * @throws InvalidXmlException If the provided data is not valid XML.
     * @throws MissingMethodParameterException
     *                             If a set-definition data is missing.
     * @throws SystemException     If an error occurs
     */
    String create(final String setDefinition) throws UniqueConstraintViolationException, InvalidXmlException,
        MissingMethodParameterException, SystemException;

    /**
     * Retrieve a set definition.
     *
     * @param id The id of the set-definition to be retrieved.
     * @return The XML representation of the retrieved set-definition corresponding to XML-schema "set-definition.xsd".
     * @throws ResourceNotFoundException Thrown if a set-definition with the specified id cannot be found.
     * @throws MissingMethodParameterException
     *                                   If a set-definition id is missing.
     * @throws SystemException           If an error occurs.
     */
    String retrieve(final String id) throws ResourceNotFoundException, MissingMethodParameterException, SystemException;

    /**
     * Update an set-definition.
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
     */
    String update(final String id, final String xmlData) throws ResourceNotFoundException, OptimisticLockingException,
        MissingMethodParameterException, SystemException;

    /**
     * Delete a set-definition.
     *
     * @param id The id of the set-definition to be deleted
     * @throws ResourceNotFoundException Thrown if a set-definition with the specified id cannot be found.
     * @throws MissingMethodParameterException
     *                                   If a set-definition id is missing.
     * @throws SystemException           If an error occurs.
     */
    void delete(final String id) throws ResourceNotFoundException, MissingMethodParameterException, SystemException;

    /**
     * Retrieves a list of completes set-definitions applying filters.
     *
     * @param filter Simple XML containing the filter definition. See functional specification.
     * @return Returns XML representation of found set-definitions.
     * @throws AuthenticationException     Thrown if the authentication fails due to an invalid provided
     *                                     eSciDocUserHandle.
     * @throws AuthorizationException      Thrown if the authorization fails.
     * @throws InvalidSearchQueryException thrown if the given search query could not be translated into a SQL query
     * @throws SystemException             If an error occurs.
     */
    String retrieveSetDefinitions(final Map<String, String[]> filter) throws AuthenticationException,
        AuthorizationException, InvalidSearchQueryException, SystemException;
}
