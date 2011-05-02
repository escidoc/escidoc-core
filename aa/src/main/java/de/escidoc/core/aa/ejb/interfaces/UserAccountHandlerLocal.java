package de.escidoc.core.aa.ejb.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.InvalidScopeException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.GrantNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.PreferenceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RoleNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAccountNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.UserAttributeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyActiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyDeactiveException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyRevokedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.userdetails.UserDetails;

import javax.ejb.EJBLocalObject;
import java.util.Map;

/**
 * Local interface for UserAccountHandler.
 */
public interface UserAccountHandlerLocal extends EJBLocalObject {

    String create(String user, SecurityContext securityContext) throws UniqueConstraintViolationException,
        XmlCorruptedException, XmlSchemaValidationException, OrganizationalUnitNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        InvalidStatusException;

    String create(String user, String authHandle, Boolean restAccess) throws UniqueConstraintViolationException,
        XmlCorruptedException, XmlSchemaValidationException, OrganizationalUnitNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        InvalidStatusException;

    void delete(String userId, SecurityContext securityContext) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    void delete(String userId, String authHandle, Boolean restAccess) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    String update(String userId, String user, SecurityContext securityContext) throws UserAccountNotFoundException,
        UniqueConstraintViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, OrganizationalUnitNotFoundException, SystemException,
        InvalidStatusException;

    String update(String userId, String user, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, UniqueConstraintViolationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, MissingAttributeValueException,
        OptimisticLockingException, AuthenticationException, AuthorizationException,
        OrganizationalUnitNotFoundException, SystemException, InvalidStatusException;

    void updatePassword(String userId, String taskParam, SecurityContext securityContext)
        throws UserAccountNotFoundException, InvalidStatusException, XmlCorruptedException,
        MissingMethodParameterException, OptimisticLockingException, AuthenticationException, AuthorizationException,
        SystemException;

    void updatePassword(String userId, String taskParam, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, InvalidStatusException, XmlCorruptedException,
        MissingMethodParameterException, OptimisticLockingException, AuthenticationException, AuthorizationException,
        SystemException;

    String retrieve(String userId, SecurityContext securityContext) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    String retrieve(String userId, String authHandle, Boolean restAccess) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    String retrieveCurrentUser(SecurityContext securityContext) throws UserAccountNotFoundException,
        AuthenticationException, AuthorizationException, SystemException;

    String retrieveCurrentUser(String authHandle, Boolean restAccess) throws UserAccountNotFoundException,
        AuthenticationException, AuthorizationException, SystemException;

    String retrieveResources(String userId, SecurityContext securityContext) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    String retrieveResources(String userId, String authHandle, Boolean restAccess) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    String retrieveCurrentGrants(String userId, SecurityContext securityContext) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    String retrieveCurrentGrants(String userId, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException;

    String retrieveGrant(String userId, String grantId, SecurityContext securityContext)
        throws UserAccountNotFoundException, GrantNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    String retrieveGrant(String userId, String grantId, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, GrantNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    String retrieveGrants(Map filter, SecurityContext securityContext) throws MissingMethodParameterException,
        InvalidSearchQueryException, AuthenticationException, AuthorizationException, SystemException;

    String retrieveGrants(Map filter, String authHandle, Boolean restAccess) throws MissingMethodParameterException,
        InvalidSearchQueryException, AuthenticationException, AuthorizationException, SystemException;

    void activate(String userId, String taskParam, SecurityContext securityContext) throws AlreadyActiveException,
        UserAccountNotFoundException, XmlCorruptedException, MissingMethodParameterException,
        MissingAttributeValueException, OptimisticLockingException, AuthenticationException, AuthorizationException,
        SystemException;

    void activate(String userId, String taskParam, String authHandle, Boolean restAccess)
        throws AlreadyActiveException, UserAccountNotFoundException, XmlCorruptedException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException;

    void deactivate(String userId, String taskParam, SecurityContext securityContext) throws AlreadyDeactiveException,
        UserAccountNotFoundException, XmlCorruptedException, MissingMethodParameterException,
        MissingAttributeValueException, OptimisticLockingException, AuthenticationException, AuthorizationException,
        SystemException;

    void deactivate(String userId, String taskParam, String authHandle, Boolean restAccess)
        throws AlreadyDeactiveException, UserAccountNotFoundException, XmlCorruptedException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException;

    String createGrant(String userId, String grantXML, SecurityContext securityContext) throws AlreadyExistsException,
        UserAccountNotFoundException, InvalidScopeException, RoleNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException;

    String createGrant(String userId, String grantXML, String authHandle, Boolean restAccess)
        throws AlreadyExistsException, UserAccountNotFoundException, InvalidScopeException, RoleNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException;

    void revokeGrant(String userId, String grantId, String taskParam, SecurityContext securityContext)
        throws UserAccountNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException;

    void revokeGrant(String userId, String grantId, String taskParam, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException;

    void revokeGrants(String userId, String taskParam, SecurityContext securityContext)
        throws UserAccountNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException;

    void revokeGrants(String userId, String taskParam, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException;

    String retrieveUserAccounts(Map filter, SecurityContext securityContext) throws MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException, InvalidSearchQueryException;

    String retrieveUserAccounts(Map filter, String authHandle, Boolean restAccess)
        throws MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        InvalidSearchQueryException;

    UserDetails retrieveUserDetails(String handle, SecurityContext securityContext)
        throws MissingMethodParameterException, AuthenticationException, AuthorizationException,
        UserAccountNotFoundException, SystemException;

    UserDetails retrieveUserDetails(String handle, String authHandle, Boolean restAccess)
        throws MissingMethodParameterException, AuthenticationException, AuthorizationException,
        UserAccountNotFoundException, SystemException;

    String retrievePreferences(String userId, SecurityContext securityContext) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    String retrievePreferences(String userId, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException;

    String createPreference(String userId, String preferenceXML, SecurityContext securityContext)
        throws AlreadyExistsException, UserAccountNotFoundException, PreferenceNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException;

    String createPreference(String userId, String preferenceXML, String authHandle, Boolean restAccess)
        throws AlreadyExistsException, UserAccountNotFoundException, PreferenceNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException;

    String updatePreferences(String userId, String preferencesXML, SecurityContext securityContext)
        throws UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        OptimisticLockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, MissingAttributeValueException;

    String updatePreferences(String userId, String preferencesXML, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        OptimisticLockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, MissingAttributeValueException;

    String updatePreference(String userId, String preferenceName, String preferenceXML, SecurityContext securityContext)
        throws AlreadyExistsException, UserAccountNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, PreferenceNotFoundException, OptimisticLockingException, MissingAttributeValueException;

    String updatePreference(
        String userId, String preferenceName, String preferenceXML, String authHandle, Boolean restAccess)
        throws AlreadyExistsException, UserAccountNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, PreferenceNotFoundException, OptimisticLockingException, MissingAttributeValueException;

    String retrievePreference(String userId, String preferenceName, SecurityContext securityContext)
        throws UserAccountNotFoundException, PreferenceNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    String retrievePreference(String userId, String preferenceName, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, PreferenceNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    void deletePreference(String userId, String preferenceName, SecurityContext securityContext)
        throws UserAccountNotFoundException, PreferenceNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    void deletePreference(String userId, String preferenceName, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, PreferenceNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    String createAttribute(String userId, String attributeXml, SecurityContext securityContext)
        throws AlreadyExistsException, UserAccountNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException;

    String createAttribute(String userId, String attributeXml, String authHandle, Boolean restAccess)
        throws AlreadyExistsException, UserAccountNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException;

    String retrieveAttributes(String userId, SecurityContext securityContext) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    String retrieveAttributes(String userId, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException;

    String retrieveNamedAttributes(String userId, String name, SecurityContext securityContext)
        throws UserAccountNotFoundException, UserAttributeNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    String retrieveNamedAttributes(String userId, String name, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, UserAttributeNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    String retrieveAttribute(String userId, String attributeId, SecurityContext securityContext)
        throws UserAccountNotFoundException, UserAttributeNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    String retrieveAttribute(String userId, String attributeId, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, UserAttributeNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException;

    String updateAttribute(String userId, String attributeId, String attributeXml, SecurityContext securityContext)
        throws UserAccountNotFoundException, OptimisticLockingException, UserAttributeNotFoundException,
        ReadonlyElementViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    String updateAttribute(String userId, String attributeId, String attributeXml, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, OptimisticLockingException, UserAttributeNotFoundException,
        ReadonlyElementViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    void deleteAttribute(String userId, String attributeId, SecurityContext securityContext)
        throws UserAccountNotFoundException, UserAttributeNotFoundException, ReadonlyElementViolationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    void deleteAttribute(String userId, String attributeId, String authHandle, Boolean restAccess)
        throws UserAccountNotFoundException, UserAttributeNotFoundException, ReadonlyElementViolationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException;

    String retrievePermissionFilterQuery(Map parameters, SecurityContext securityContext) throws SystemException,
        InvalidSearchQueryException, AuthenticationException, AuthorizationException;

    String retrievePermissionFilterQuery(Map parameters, String authHandle, Boolean restAccess) throws SystemException,
        InvalidSearchQueryException, AuthenticationException, AuthorizationException;

}
