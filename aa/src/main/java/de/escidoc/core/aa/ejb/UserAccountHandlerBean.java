/*
 * Generate EJB from POJO with Spring framework Bean Factory
 * Bernhard Kraus (Accenture)
 */

package de.escidoc.core.aa.ejb;

import de.escidoc.core.aa.service.interfaces.UserAccountHandlerInterface;
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
import de.escidoc.core.common.util.service.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.userdetails.UserDetails;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import java.rmi.RemoteException;
import java.util.Map;

public class UserAccountHandlerBean implements SessionBean {

    private UserAccountHandlerInterface service;

    private SessionContext sessionCtx;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAccountHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                beanFactoryLocator.useBeanFactory("UserAccountHandler.spring.ejb.context").getFactory();
            this.service = (UserAccountHandlerInterface) factory.getBean("service.UserAccountHandler");
        }
        catch (Exception e) {
            LOGGER.error("ejbCreate(): Exception UserAccountHandlerComponent: " + e);
            throw new CreateException(e.getMessage()); // Ignore FindBugs
        }
    }

    @Override
    public void setSessionContext(final SessionContext arg0) throws RemoteException {
        this.sessionCtx = arg0;
    }

    @Override
    public void ejbRemove() throws RemoteException {
    }

    @Override
    public void ejbActivate() throws RemoteException {

    }

    @Override
    public void ejbPassivate() throws RemoteException {

    }

    public String create(final String user, final SecurityContext securityContext)
        throws UniqueConstraintViolationException, XmlCorruptedException, XmlSchemaValidationException,
        OrganizationalUnitNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, InvalidStatusException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(user);
    }

    public String create(final String user, final String authHandle, final Boolean restAccess)
        throws UniqueConstraintViolationException, XmlCorruptedException, XmlSchemaValidationException,
        OrganizationalUnitNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, InvalidStatusException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(user);
    }

    public void delete(final String userId, final SecurityContext securityContext) throws UserAccountNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(userId);
    }

    public void delete(final String userId, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(userId);
    }

    public String update(final String userId, final String user, final SecurityContext securityContext)
        throws UserAccountNotFoundException, UniqueConstraintViolationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, MissingAttributeValueException,
        OptimisticLockingException, AuthenticationException, AuthorizationException,
        OrganizationalUnitNotFoundException, SystemException, InvalidStatusException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(userId, user);
    }

    public String update(final String userId, final String user, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, UniqueConstraintViolationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, MissingAttributeValueException,
        OptimisticLockingException, AuthenticationException, AuthorizationException,
        OrganizationalUnitNotFoundException, SystemException, InvalidStatusException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(userId, user);
    }

    public void updatePassword(final String userId, final String taskParam, final SecurityContext securityContext)
        throws UserAccountNotFoundException, InvalidStatusException, XmlCorruptedException,
        MissingMethodParameterException, OptimisticLockingException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.updatePassword(userId, taskParam);
    }

    public void updatePassword(
        final String userId, final String taskParam, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, InvalidStatusException, XmlCorruptedException,
        MissingMethodParameterException, OptimisticLockingException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.updatePassword(userId, taskParam);
    }

    public String retrieve(final String userId, final SecurityContext securityContext)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(userId);
    }

    public String retrieve(final String userId, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(userId);
    }

    public String retrieveCurrentUser(final SecurityContext securityContext) throws UserAccountNotFoundException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveCurrentUser();
    }

    public String retrieveCurrentUser(final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveCurrentUser();
    }

    public String retrieveResources(final String userId, final SecurityContext securityContext)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(userId);
    }

    public String retrieveResources(final String userId, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(userId);
    }

    public String retrieveCurrentGrants(final String userId, final SecurityContext securityContext)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveCurrentGrants(userId);
    }

    public String retrieveCurrentGrants(final String userId, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveCurrentGrants(userId);
    }

    public String retrieveGrant(final String userId, final String grantId, final SecurityContext securityContext)
        throws UserAccountNotFoundException, GrantNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveGrant(userId, grantId);
    }

    public String retrieveGrant(
        final String userId, final String grantId, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, GrantNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveGrant(userId, grantId);
    }

    public String retrieveGrants(final Map filter, final SecurityContext securityContext)
        throws MissingMethodParameterException, InvalidSearchQueryException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveGrants(filter);
    }

    public String retrieveGrants(final Map filter, final String authHandle, final Boolean restAccess)
        throws MissingMethodParameterException, InvalidSearchQueryException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveGrants(filter);
    }

    public void activate(final String userId, final String taskParam, final SecurityContext securityContext)
        throws AlreadyActiveException, UserAccountNotFoundException, XmlCorruptedException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.activate(userId, taskParam);
    }

    public void activate(final String userId, final String taskParam, final String authHandle, final Boolean restAccess)
        throws AlreadyActiveException, UserAccountNotFoundException, XmlCorruptedException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.activate(userId, taskParam);
    }

    public void deactivate(final String userId, final String taskParam, final SecurityContext securityContext)
        throws AlreadyDeactiveException, UserAccountNotFoundException, XmlCorruptedException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.deactivate(userId, taskParam);
    }

    public void deactivate(
        final String userId, final String taskParam, final String authHandle, final Boolean restAccess)
        throws AlreadyDeactiveException, UserAccountNotFoundException, XmlCorruptedException,
        MissingMethodParameterException, MissingAttributeValueException, OptimisticLockingException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.deactivate(userId, taskParam);
    }

    public String createGrant(final String userId, final String grantXML, final SecurityContext securityContext)
        throws AlreadyExistsException, UserAccountNotFoundException, InvalidScopeException, RoleNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createGrant(userId, grantXML);
    }

    public String createGrant(
        final String userId, final String grantXML, final String authHandle, final Boolean restAccess)
        throws AlreadyExistsException, UserAccountNotFoundException, InvalidScopeException, RoleNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createGrant(userId, grantXML);
    }

    public void revokeGrant(
        final String userId, final String grantId, final String taskParam, final SecurityContext securityContext)
        throws UserAccountNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.revokeGrant(userId, grantId, taskParam);
    }

    public void revokeGrant(
        final String userId, final String grantId, final String taskParam, final String authHandle,
        final Boolean restAccess) throws UserAccountNotFoundException, GrantNotFoundException, AlreadyRevokedException,
        XmlCorruptedException, MissingAttributeValueException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.revokeGrant(userId, grantId, taskParam);
    }

    public void revokeGrants(final String userId, final String taskParam, final SecurityContext securityContext)
        throws UserAccountNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.revokeGrants(userId, taskParam);
    }

    public void revokeGrants(
        final String userId, final String taskParam, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, GrantNotFoundException, AlreadyRevokedException, XmlCorruptedException,
        MissingAttributeValueException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.revokeGrants(userId, taskParam);
    }

    public String retrieveUserAccounts(final Map filter, final SecurityContext securityContext)
        throws MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        InvalidSearchQueryException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveUserAccounts(filter);
    }

    public String retrieveUserAccounts(final Map filter, final String authHandle, final Boolean restAccess)
        throws MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        InvalidSearchQueryException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveUserAccounts(filter);
    }

    public UserDetails retrieveUserDetails(final String handle, final SecurityContext securityContext)
        throws MissingMethodParameterException, AuthenticationException, AuthorizationException,
        UserAccountNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveUserDetails(handle);
    }

    public UserDetails retrieveUserDetails(final String handle, final String authHandle, final Boolean restAccess)
        throws MissingMethodParameterException, AuthenticationException, AuthorizationException,
        UserAccountNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveUserDetails(handle);
    }

    public String retrievePreferences(final String userId, final SecurityContext securityContext)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrievePreferences(userId);
    }

    public String retrievePreferences(final String userId, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrievePreferences(userId);
    }

    public String createPreference(
        final String userId, final String preferenceXML, final SecurityContext securityContext)
        throws AlreadyExistsException, UserAccountNotFoundException, PreferenceNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createPreference(userId, preferenceXML);
    }

    public String createPreference(
        final String userId, final String preferenceXML, final String authHandle, final Boolean restAccess)
        throws AlreadyExistsException, UserAccountNotFoundException, PreferenceNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createPreference(userId, preferenceXML);
    }

    public String updatePreferences(
        final String userId, final String preferencesXML, final SecurityContext securityContext)
        throws UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        OptimisticLockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, MissingAttributeValueException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updatePreferences(userId, preferencesXML);
    }

    public String updatePreferences(
        final String userId, final String preferencesXML, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        OptimisticLockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, MissingAttributeValueException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updatePreferences(userId, preferencesXML);
    }

    public String updatePreference(
        final String userId, final String preferenceName, final String preferenceXML,
        final SecurityContext securityContext) throws AlreadyExistsException, UserAccountNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, PreferenceNotFoundException, OptimisticLockingException,
        MissingAttributeValueException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updatePreference(userId, preferenceName, preferenceXML);
    }

    public String updatePreference(
        final String userId, final String preferenceName, final String preferenceXML, final String authHandle,
        final Boolean restAccess) throws AlreadyExistsException, UserAccountNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, PreferenceNotFoundException, OptimisticLockingException, MissingAttributeValueException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updatePreference(userId, preferenceName, preferenceXML);
    }

    public String retrievePreference(
        final String userId, final String preferenceName, final SecurityContext securityContext)
        throws UserAccountNotFoundException, PreferenceNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrievePreference(userId, preferenceName);
    }

    public String retrievePreference(
        final String userId, final String preferenceName, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, PreferenceNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrievePreference(userId, preferenceName);
    }

    public void deletePreference(final String userId, final String preferenceName, final SecurityContext securityContext)
        throws UserAccountNotFoundException, PreferenceNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.deletePreference(userId, preferenceName);
    }

    public void deletePreference(
        final String userId, final String preferenceName, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, PreferenceNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.deletePreference(userId, preferenceName);
    }

    public String createAttribute(final String userId, final String attributeXml, final SecurityContext securityContext)
        throws AlreadyExistsException, UserAccountNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createAttribute(userId, attributeXml);
    }

    public String createAttribute(
        final String userId, final String attributeXml, final String authHandle, final Boolean restAccess)
        throws AlreadyExistsException, UserAccountNotFoundException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createAttribute(userId, attributeXml);
    }

    public String retrieveAttributes(final String userId, final SecurityContext securityContext)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveAttributes(userId);
    }

    public String retrieveAttributes(final String userId, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveAttributes(userId);
    }

    public String retrieveNamedAttributes(final String userId, final String name, final SecurityContext securityContext)
        throws UserAccountNotFoundException, UserAttributeNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveNamedAttributes(userId, name);
    }

    public String retrieveNamedAttributes(
        final String userId, final String name, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, UserAttributeNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveNamedAttributes(userId, name);
    }

    public String retrieveAttribute(final String userId, final String attributeId, final SecurityContext securityContext)
        throws UserAccountNotFoundException, UserAttributeNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveAttribute(userId, attributeId);
    }

    public String retrieveAttribute(
        final String userId, final String attributeId, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, UserAttributeNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveAttribute(userId, attributeId);
    }

    public String updateAttribute(
        final String userId, final String attributeId, final String attributeXml, final SecurityContext securityContext)
        throws UserAccountNotFoundException, OptimisticLockingException, UserAttributeNotFoundException,
        ReadonlyElementViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updateAttribute(userId, attributeId, attributeXml);
    }

    public String updateAttribute(
        final String userId, final String attributeId, final String attributeXml, final String authHandle,
        final Boolean restAccess) throws UserAccountNotFoundException, OptimisticLockingException,
        UserAttributeNotFoundException, ReadonlyElementViolationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updateAttribute(userId, attributeId, attributeXml);
    }

    public void deleteAttribute(final String userId, final String attributeId, final SecurityContext securityContext)
        throws UserAccountNotFoundException, UserAttributeNotFoundException, ReadonlyElementViolationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.deleteAttribute(userId, attributeId);
    }

    public void deleteAttribute(
        final String userId, final String attributeId, final String authHandle, final Boolean restAccess)
        throws UserAccountNotFoundException, UserAttributeNotFoundException, ReadonlyElementViolationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.deleteAttribute(userId, attributeId);
    }

    public String retrievePermissionFilterQuery(final Map parameters, final SecurityContext securityContext)
        throws SystemException, InvalidSearchQueryException, AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrievePermissionFilterQuery(parameters);
    }

    public String retrievePermissionFilterQuery(final Map parameters, final String authHandle, final Boolean restAccess)
        throws SystemException, InvalidSearchQueryException, AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrievePermissionFilterQuery(parameters);
    }
}