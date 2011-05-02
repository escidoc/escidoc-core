package de.escidoc.core.aa.ejb.interfaces;

import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;

/**
 * Remote interface for UserManagementWrapper.
 */
public interface UserManagementWrapperRemote extends EJBObject {

    void logout(SecurityContext securityContext) throws AuthenticationException, SystemException, RemoteException;

    void logout(String authHandle, Boolean restAccess) throws AuthenticationException, SystemException, RemoteException;

    void initHandleExpiryTimestamp(String handle, SecurityContext securityContext) throws AuthenticationException,
        SystemException, RemoteException;

    void initHandleExpiryTimestamp(String handle, String authHandle, Boolean restAccess)
        throws AuthenticationException, SystemException, RemoteException;

}
