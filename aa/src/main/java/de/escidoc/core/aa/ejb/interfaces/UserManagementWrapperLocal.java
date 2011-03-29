package de.escidoc.core.aa.ejb.interfaces;

import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBLocalObject;

/**
 * Local interface for UserManagementWrapper.
 */
public interface UserManagementWrapperLocal extends EJBLocalObject {

    void logout(SecurityContext securityContext)
            throws AuthenticationException,
            SystemException;

    void logout(String authHandle, Boolean restAccess)
            throws AuthenticationException,
            SystemException;

    void initHandleExpiryTimestamp(String handle, SecurityContext securityContext)
            throws AuthenticationException,
            SystemException;

    void initHandleExpiryTimestamp(String handle, String authHandle, Boolean restAccess)
            throws AuthenticationException,
            SystemException;

}
