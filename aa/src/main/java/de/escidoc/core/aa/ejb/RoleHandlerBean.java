package de.escidoc.core.aa.ejb;

import de.escidoc.core.aa.service.interfaces.RoleHandlerInterface;
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
import de.escidoc.core.common.util.service.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.security.context.SecurityContext;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import java.rmi.RemoteException;
import java.util.Map;

public class RoleHandlerBean implements SessionBean {

    private RoleHandlerInterface service;

    private SessionContext sessionCtx;

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                beanFactoryLocator.useBeanFactory("RoleHandler.spring.ejb.context").getFactory();
            this.service = (RoleHandlerInterface) factory.getBean("service.RoleHandler");
        }
        catch (Exception e) {
            LOGGER.error("ejbCreate(): Exception RoleHandlerComponent: " + e);
            throw new CreateException(e.getMessage()); // Ignore FindBugs
        }
    }

    @Override
    public final void setSessionContext(final SessionContext arg0) throws RemoteException {
        this.sessionCtx = arg0;
    }

    @Override
    public final void ejbRemove() throws RemoteException {
    }

    @Override
    public final void ejbActivate() throws RemoteException {

    }

    @Override
    public final void ejbPassivate() throws RemoteException {

    }

    public final String create(final String xmlData, final SecurityContext securityContext)
        throws UniqueConstraintViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xmlData);
    }

    public final String create(final String xmlData, final String authHandle, final Boolean restAccess)
        throws UniqueConstraintViolationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xmlData);
    }

    public final void delete(final String id, final SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, RoleNotFoundException, RoleInUseViolationException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public final void delete(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException, RoleNotFoundException,
        RoleInUseViolationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public final String retrieve(final String id, final SecurityContext securityContext) throws RoleNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public final String retrieve(final String id, final String authHandle, final Boolean restAccess)
        throws RoleNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public final String retrieveResources(final String id, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException, RoleNotFoundException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(id);
    }

    public final String retrieveResources(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException, RoleNotFoundException,
        SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(id);
    }

    public final String update(final String id, final String xmlData, final SecurityContext securityContext)
        throws RoleNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        MissingAttributeValueException, UniqueConstraintViolationException, OptimisticLockingException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }

    public final String update(final String id, final String xmlData, final String authHandle, final Boolean restAccess)
        throws RoleNotFoundException, XmlCorruptedException, XmlSchemaValidationException,
        MissingAttributeValueException, UniqueConstraintViolationException, OptimisticLockingException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }

    public final String retrieveRoles(final Map filter, final SecurityContext securityContext)
        throws MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        InvalidSearchQueryException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveRoles(filter);
    }

    public final String retrieveRoles(final Map filter, final String authHandle, final Boolean restAccess)
        throws MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        InvalidSearchQueryException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveRoles(filter);
    }
}