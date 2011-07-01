package de.escidoc.core.aa.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.springframework.security.core.context.SecurityContext;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Service endpoint interface for ActionHandler.
 */
public interface ActionHandlerService extends Remote {

    String createUnsecuredActions(String contextId, String actions, SecurityContext securityContext)
        throws ContextNotFoundException, XmlCorruptedException, XmlSchemaValidationException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    String createUnsecuredActions(String contextId, String actions, String authHandle, Boolean restAccess)
        throws ContextNotFoundException, XmlCorruptedException, XmlSchemaValidationException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    void deleteUnsecuredActions(String contextId, SecurityContext securityContext) throws ContextNotFoundException,
        AuthenticationException, AuthorizationException, SystemException, RemoteException;

    void deleteUnsecuredActions(String contextId, String authHandle, Boolean restAccess)
        throws ContextNotFoundException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveUnsecuredActions(String contextId, SecurityContext securityContext) throws ContextNotFoundException,
        AuthenticationException, AuthorizationException, SystemException, RemoteException;

    String retrieveUnsecuredActions(String contextId, String authHandle, Boolean restAccess)
        throws ContextNotFoundException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

}
