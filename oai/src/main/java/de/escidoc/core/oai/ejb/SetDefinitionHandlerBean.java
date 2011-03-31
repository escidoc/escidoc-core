/*
 * Generate EJB from POJO with Spring framework Bean Factory
 * Bernhard Kraus (Accenture)
 */

package de.escidoc.core.oai.ejb;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.oai.service.interfaces.SetDefinitionHandlerInterface;
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

public class SetDefinitionHandlerBean implements SessionBean {

    SetDefinitionHandlerInterface service;

    SessionContext sessionCtx;

    private static final Logger LOGGER = LoggerFactory.getLogger(SetDefinitionHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                beanFactoryLocator.useBeanFactory("SetDefinitionHandler.spring.ejb.context").getFactory();
            this.service = (SetDefinitionHandlerInterface) factory.getBean("service.SetDefinitionHandler");
        }
        catch (Exception e) {
            LOGGER.error("ejbCreate(): Exception SetDefinitionHandlerComponent: " + e);
            throw new CreateException(e.getMessage());
        }
    }

    public void setSessionContext(final SessionContext arg0) throws RemoteException {
        this.sessionCtx = arg0;
    }

    public void ejbRemove() throws RemoteException {
    }

    public void ejbActivate() throws RemoteException {

    }

    public void ejbPassivate() throws RemoteException {

    }

    public String create(final String setDefinition, final SecurityContext securityContext)
        throws UniqueConstraintViolationException, InvalidXmlException, MissingMethodParameterException,
        SystemException, AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(setDefinition);
    }

    public String create(final String setDefinition, final String authHandle, final Boolean restAccess)
        throws UniqueConstraintViolationException, InvalidXmlException, MissingMethodParameterException,
        SystemException, AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(setDefinition);
    }

    public String retrieve(final String setDefinitionId, final SecurityContext securityContext)
        throws ResourceNotFoundException, MissingMethodParameterException, SystemException, AuthenticationException,
        AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(setDefinitionId);
    }

    public String retrieve(final String setDefinitionId, final String authHandle, final Boolean restAccess)
        throws ResourceNotFoundException, MissingMethodParameterException, SystemException, AuthenticationException,
        AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(setDefinitionId);
    }

    public String update(final String setDefinitionId, final String xmlData, final SecurityContext securityContext)
        throws ResourceNotFoundException, OptimisticLockingException, MissingMethodParameterException, SystemException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(setDefinitionId, xmlData);
    }

    public String update(
        final String setDefinitionId, final String xmlData, final String authHandle, final Boolean restAccess)
        throws ResourceNotFoundException, OptimisticLockingException, MissingMethodParameterException, SystemException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(setDefinitionId, xmlData);
    }

    public void delete(final String setDefinitionId, final SecurityContext securityContext)
        throws ResourceNotFoundException, MissingMethodParameterException, SystemException, AuthenticationException,
        AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(setDefinitionId);
    }

    public void delete(final String setDefinitionId, final String authHandle, final Boolean restAccess)
        throws ResourceNotFoundException, MissingMethodParameterException, SystemException, AuthenticationException,
        AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(setDefinitionId);
    }

    public String retrieveSetDefinitions(final Map filter, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        InvalidSearchQueryException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveSetDefinitions(filter);
    }

    public String retrieveSetDefinitions(final Map filter, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        InvalidSearchQueryException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveSetDefinitions(filter);
    }
}