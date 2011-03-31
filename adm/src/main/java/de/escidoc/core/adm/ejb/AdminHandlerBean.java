/*
 * Generate EJB from POJO with Spring framework Bean Factory
 * Bernhard Kraus (Accenture)
 */

package de.escidoc.core.adm.ejb;

import de.escidoc.core.adm.service.interfaces.AdminHandlerInterface;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
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

public class AdminHandlerBean implements SessionBean {

    AdminHandlerInterface service;

    SessionContext sessionCtx;

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                beanFactoryLocator.useBeanFactory("AdminHandler.spring.ejb.context").getFactory();
            this.service = (AdminHandlerInterface) factory.getBean("service.AdminHandler");
        }
        catch (Exception e) {
            LOGGER.error("ejbCreate(): Exception AdminHandlerComponent: " + e);
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

    public String deleteObjects(final String taskParam, final SecurityContext securityContext)
        throws InvalidXmlException, SystemException, AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.deleteObjects(taskParam);
    }

    public String deleteObjects(final String taskParam, final String authHandle, final Boolean restAccess)
        throws InvalidXmlException, SystemException, AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.deleteObjects(taskParam);
    }

    public String getPurgeStatus(final SecurityContext securityContext) throws SystemException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.getPurgeStatus();
    }

    public String getPurgeStatus(final String authHandle, final Boolean restAccess) throws SystemException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.getPurgeStatus();
    }

    public String getReindexStatus(final SecurityContext securityContext) throws SystemException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.getReindexStatus();
    }

    public String getReindexStatus(final String authHandle, final Boolean restAccess) throws SystemException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.getReindexStatus();
    }

    public void decreaseReindexStatus(final String objectTypeXml, final SecurityContext securityContext)
        throws InvalidXmlException, SystemException, AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.decreaseReindexStatus(objectTypeXml);
    }

    public void decreaseReindexStatus(final String objectTypeXml, final String authHandle, final Boolean restAccess)
        throws InvalidXmlException, SystemException, AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.decreaseReindexStatus(objectTypeXml);
    }

    public String reindex(final String clearIndex, final String indexNamePrefix, final SecurityContext securityContext)
        throws SystemException, InvalidSearchQueryException, AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.reindex(clearIndex, indexNamePrefix);
    }

    public String reindex(
        final String clearIndex, final String indexNamePrefix, final String authHandle, final Boolean restAccess)
        throws SystemException, InvalidSearchQueryException, AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.reindex(clearIndex, indexNamePrefix);
    }

    public String getIndexConfiguration(final SecurityContext securityContext) throws SystemException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.getIndexConfiguration();
    }

    public String getIndexConfiguration(final String authHandle, final Boolean restAccess) throws SystemException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.getIndexConfiguration();
    }

    public String getRepositoryInfo(final SecurityContext securityContext) throws SystemException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.getRepositoryInfo();
    }

    public String getRepositoryInfo(final String authHandle, final Boolean restAccess) throws SystemException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.getRepositoryInfo();
    }

    public String loadExamples(final String type, final SecurityContext securityContext)
        throws InvalidSearchQueryException, SystemException, AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.loadExamples(type);
    }

    public String loadExamples(final String type, final String authHandle, final Boolean restAccess)
        throws InvalidSearchQueryException, SystemException, AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.loadExamples(type);
    }
}