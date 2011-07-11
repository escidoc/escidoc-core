package de.escidoc.core.oai.service;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.core.context.SecurityContext;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Service endpoint interface for SetDefinitionHandler.
 */
public interface SetDefinitionHandlerService extends Remote {

    String create(String setDefinition, SecurityContext securityContext) throws UniqueConstraintViolationException,
        InvalidXmlException, MissingMethodParameterException, SystemException, AuthenticationException,
        AuthorizationException, RemoteException;

    String create(String setDefinition, String authHandle, Boolean restAccess)
        throws UniqueConstraintViolationException, InvalidXmlException, MissingMethodParameterException,
        SystemException, AuthenticationException, AuthorizationException, RemoteException;

    String retrieve(String setDefinitionId, SecurityContext securityContext) throws ResourceNotFoundException,
        MissingMethodParameterException, SystemException, AuthenticationException, AuthorizationException,
        RemoteException;

    String retrieve(String setDefinitionId, String authHandle, Boolean restAccess) throws ResourceNotFoundException,
        MissingMethodParameterException, SystemException, AuthenticationException, AuthorizationException,
        RemoteException;

    String update(String setDefinitionId, String xmlData, SecurityContext securityContext)
        throws ResourceNotFoundException, OptimisticLockingException, MissingMethodParameterException, SystemException,
        AuthenticationException, AuthorizationException, RemoteException;

    String update(String setDefinitionId, String xmlData, String authHandle, Boolean restAccess)
        throws ResourceNotFoundException, OptimisticLockingException, MissingMethodParameterException, SystemException,
        AuthenticationException, AuthorizationException, RemoteException;

    void delete(String setDefinitionId, SecurityContext securityContext) throws ResourceNotFoundException,
        MissingMethodParameterException, SystemException, AuthenticationException, AuthorizationException,
        RemoteException;

    void delete(String setDefinitionId, String authHandle, Boolean restAccess) throws ResourceNotFoundException,
        MissingMethodParameterException, SystemException, AuthenticationException, AuthorizationException,
        RemoteException;

    String retrieveSetDefinitions(Map filter, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, InvalidSearchQueryException, SystemException,
        RemoteException;

    String retrieveSetDefinitions(Map filter, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, InvalidSearchQueryException, SystemException,
        RemoteException;

}
