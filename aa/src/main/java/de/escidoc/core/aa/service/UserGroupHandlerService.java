package de.escidoc.core.aa.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import org.springframework.security.core.context.SecurityContext;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidScopeException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.GrantNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserGroupNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyActiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyDeactiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyRevokedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.application.violated.UserGroupHierarchyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Service endpoint interface for UserGroupHandler.
 */
public interface UserGroupHandlerService extends Remote {

    String create(String xmlData, SecurityContext securityContext) throws UniqueConstraintViolationException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    String create(String xmlData, String authHandle, Boolean restAccess) throws UniqueConstraintViolationException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    void delete(String groupId, SecurityContext securityContext) throws UserGroupNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    void delete(String groupId, String authHandle, Boolean restAccess) throws UserGroupNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieve(String groupId, SecurityContext securityContext) throws UserGroupNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieve(String groupId, String authHandle, Boolean restAccess) throws UserGroupNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String update(String groupId, String xmlData, SecurityContext securityContext) throws UserGroupNotFoundException,
        UniqueConstraintViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException, RemoteException;

    String update(String groupId, String xmlData, String authHandle, Boolean restAccess)
        throws UserGroupNotFoundException, UniqueConstraintViolationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, MissingAttributeValueException,
        OptimisticLockingException, AuthenticationException, AuthorizationException, SystemException, RemoteException;

    void activate(String groupId, String taskParam, SecurityContext securityContext) throws AlreadyActiveException,
        UserGroupNotFoundException, XmlCorruptedException, MissingMethodParameterException,
        MissingAttributeValueException, OptimisticLockingException, AuthenticationException, AuthorizationException,
        SystemException, RemoteException;

    void activate(String groupId, String taskParam, String authHandle, Boolean restAccess)
        throws AlreadyActiveException, UserGroupNotFoundException, XmlCorruptedException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException, RemoteException;

    void deactivate(String groupId, String taskParam, SecurityContext securityContext) throws AlreadyDeactiveException,
        UserGroupNotFoundException, XmlCorruptedException, MissingMethodParameterException,
        MissingAttributeValueException, OptimisticLockingException, AuthenticationException, AuthorizationException,
        SystemException, RemoteException;

    void deactivate(String groupId, String taskParam, String authHandle, Boolean restAccess)
        throws AlreadyDeactiveException, UserGroupNotFoundException, XmlCorruptedException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException, RemoteException;

    String retrieveCurrentGrants(String userGroupId, SecurityContext securityContext)
        throws UserGroupNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    String retrieveCurrentGrants(String userGroupId, String authHandle, Boolean restAccess)
        throws UserGroupNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    String createGrant(String groupId, String grantXML, SecurityContext securityContext) throws AlreadyExistsException,
        UserGroupNotFoundException, InvalidScopeException, RoleNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, RemoteException;

    String createGrant(String groupId, String grantXML, String authHandle, Boolean restAccess)
        throws AlreadyExistsException, UserGroupNotFoundException, InvalidScopeException, RoleNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    void revokeGrant(String groupId, String grantId, String taskParam, SecurityContext securityContext)
        throws UserGroupNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    void revokeGrant(String groupId, String grantId, String taskParam, String authHandle, Boolean restAccess)
        throws UserGroupNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    String retrieveGrant(String groupId, String grantId, SecurityContext securityContext)
        throws UserGroupNotFoundException, GrantNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException, RemoteException;

    String retrieveGrant(String groupId, String grantId, String authHandle, Boolean restAccess)
        throws UserGroupNotFoundException, GrantNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException, RemoteException;

    void revokeGrants(String groupId, String taskParam, SecurityContext securityContext)
        throws UserGroupNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    void revokeGrants(String groupId, String taskParam, String authHandle, Boolean restAccess)
        throws UserGroupNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    String retrieveResources(String groupId, SecurityContext securityContext) throws UserGroupNotFoundException,
        SystemException, RemoteException;

    String retrieveResources(String groupId, String authHandle, Boolean restAccess) throws UserGroupNotFoundException,
        SystemException, RemoteException;

    String retrieveUserGroups(Map filter, SecurityContext securityContext) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, InvalidSearchQueryException, SystemException, RemoteException;

    String retrieveUserGroups(Map filter, String authHandle, Boolean restAccess)
        throws MissingMethodParameterException, AuthenticationException, AuthorizationException,
        InvalidSearchQueryException, SystemException, RemoteException;

    String addSelectors(String groupId, String taskParam, SecurityContext securityContext)
        throws OrganizationalUnitNotFoundException, UserAccountNotFoundException, UserGroupNotFoundException,
        InvalidContentException, MissingMethodParameterException, SystemException, AuthenticationException,
        AuthorizationException, OptimisticLockingException, XmlCorruptedException, XmlSchemaValidationException,
        UserGroupHierarchyViolationException, RemoteException;

    String addSelectors(String groupId, String taskParam, String authHandle, Boolean restAccess)
        throws OrganizationalUnitNotFoundException, UserAccountNotFoundException, UserGroupNotFoundException,
        InvalidContentException, MissingMethodParameterException, SystemException, AuthenticationException,
        AuthorizationException, OptimisticLockingException, XmlCorruptedException, XmlSchemaValidationException,
        UserGroupHierarchyViolationException, RemoteException;

    String removeSelectors(String groupId, String taskParam, SecurityContext securityContext)
        throws XmlCorruptedException, XmlSchemaValidationException, AuthenticationException, AuthorizationException,
        SystemException, UserGroupNotFoundException, OptimisticLockingException, MissingMethodParameterException,
        UserAccountNotFoundException, OrganizationalUnitNotFoundException, RemoteException;

    String removeSelectors(String groupId, String taskParam, String authHandle, Boolean restAccess)
        throws XmlCorruptedException, XmlSchemaValidationException, AuthenticationException, AuthorizationException,
        SystemException, UserGroupNotFoundException, OptimisticLockingException, MissingMethodParameterException,
        UserAccountNotFoundException, OrganizationalUnitNotFoundException, RemoteException;

}
