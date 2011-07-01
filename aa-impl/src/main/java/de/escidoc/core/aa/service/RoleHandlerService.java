package de.escidoc.core.aa.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import org.springframework.security.core.context.SecurityContext;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.RoleInUseViolationException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Service endpoint interface for RoleHandler.
 */
public interface RoleHandlerService extends Remote {

    String create(String xmlData, SecurityContext securityContext) throws UniqueConstraintViolationException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    String create(String xmlData, String authHandle, Boolean restAccess) throws UniqueConstraintViolationException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    void delete(String id, SecurityContext securityContext) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, RoleNotFoundException, RoleInUseViolationException, SystemException,
        RemoteException;

    void delete(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, RoleNotFoundException, RoleInUseViolationException,
        SystemException, RemoteException;

    String retrieve(String id, SecurityContext securityContext) throws RoleNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieve(String id, String authHandle, Boolean restAccess) throws RoleNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String update(String id, String xmlData, SecurityContext securityContext) throws RoleNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingAttributeValueException,
        UniqueConstraintViolationException, OptimisticLockingException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException, RemoteException;

    String update(String id, String xmlData, String authHandle, Boolean restAccess) throws RoleNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingAttributeValueException,
        UniqueConstraintViolationException, OptimisticLockingException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException, RemoteException;

    String retrieveRoles(Map filter, SecurityContext securityContext) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException, InvalidSearchQueryException, RemoteException;

    String retrieveRoles(Map filter, String authHandle, Boolean restAccess) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException, InvalidSearchQueryException, RemoteException;

}
