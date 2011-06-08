package de.escidoc.core.sm.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import org.springframework.security.core.context.SecurityContext;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.AggregationDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Service endpoint interface for AggregationDefinitionHandler.
 */
public interface AggregationDefinitionHandlerService extends Remote {

    String create(String xmlData, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, XmlSchemaValidationException, XmlCorruptedException, MissingMethodParameterException,
        ScopeNotFoundException, SystemException, RemoteException;

    String create(String xmlData, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, XmlSchemaValidationException, XmlCorruptedException, MissingMethodParameterException,
        ScopeNotFoundException, SystemException, RemoteException;

    void delete(String id, SecurityContext securityContext) throws AuthenticationException, AuthorizationException,
        AggregationDefinitionNotFoundException, MissingMethodParameterException, SystemException, RemoteException;

    void delete(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, AggregationDefinitionNotFoundException, MissingMethodParameterException,
        SystemException, RemoteException;

    String retrieve(String id, SecurityContext securityContext) throws AuthenticationException, AuthorizationException,
        AggregationDefinitionNotFoundException, MissingMethodParameterException, SystemException, RemoteException;

    String retrieve(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, AggregationDefinitionNotFoundException, MissingMethodParameterException,
        SystemException, RemoteException;

    String retrieveAggregationDefinitions(Map parameters, SecurityContext securityContext)
        throws InvalidSearchQueryException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    String retrieveAggregationDefinitions(Map parameters, String authHandle, Boolean restAccess)
        throws InvalidSearchQueryException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

}
