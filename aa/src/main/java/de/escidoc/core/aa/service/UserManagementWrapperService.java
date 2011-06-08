package de.escidoc.core.aa.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.springframework.security.core.context.SecurityContext;

import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Service endpoint interface for UserManagementWrapper.
 */
public interface UserManagementWrapperService extends Remote {

    void logout(SecurityContext securityContext) throws AuthenticationException, SystemException, RemoteException;

    void logout(String authHandle, Boolean restAccess) throws AuthenticationException, SystemException, RemoteException;

    void initHandleExpiryTimestamp(String handle, SecurityContext securityContext) throws AuthenticationException,
        SystemException, RemoteException;

    void initHandleExpiryTimestamp(String handle, String authHandle, Boolean restAccess)
        throws AuthenticationException, SystemException, RemoteException;

}
