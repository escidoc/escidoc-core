package de.escidoc.core.sm.service;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.core.context.SecurityContext;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Service endpoint interface for ScopeHandler.
 */
public interface ScopeHandlerService extends Remote {

    String create(String xmlData, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, XmlSchemaValidationException, XmlCorruptedException, MissingMethodParameterException,
        SystemException, RemoteException;

    String create(String xmlData, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, XmlSchemaValidationException, XmlCorruptedException, MissingMethodParameterException,
        SystemException, RemoteException;

    void delete(String id, SecurityContext securityContext) throws AuthenticationException, AuthorizationException,
        ScopeNotFoundException, MissingMethodParameterException, SystemException, RemoteException;

    void delete(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, ScopeNotFoundException, MissingMethodParameterException, SystemException,
        RemoteException;

    String retrieve(String id, SecurityContext securityContext) throws AuthenticationException, AuthorizationException,
        ScopeNotFoundException, MissingMethodParameterException, SystemException, RemoteException;

    String retrieve(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, ScopeNotFoundException, MissingMethodParameterException, SystemException,
        RemoteException;

    String retrieveScopes(Map parameters, SecurityContext securityContext) throws InvalidSearchQueryException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveScopes(Map parameters, String authHandle, Boolean restAccess) throws InvalidSearchQueryException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String update(String id, String xmlData, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, ScopeNotFoundException, MissingMethodParameterException, XmlSchemaValidationException,
        XmlCorruptedException, SystemException, RemoteException;

    String update(String id, String xmlData, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, ScopeNotFoundException, MissingMethodParameterException, XmlSchemaValidationException,
        XmlCorruptedException, SystemException, RemoteException;

}
