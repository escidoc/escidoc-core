/*
 * Generate EJB from POJO with Spring framework Bean Factory
 * Bernhard Kraus (Accenture)
 */

package de.escidoc.core.sm.ejb;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSqlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ScopeNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.ScopeContextViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.sm.service.interfaces.ReportDefinitionHandlerInterface;
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

public class ReportDefinitionHandlerBean implements SessionBean {

    private ReportDefinitionHandlerInterface service;

    private SessionContext sessionCtx;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportDefinitionHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                beanFactoryLocator.useBeanFactory("ReportDefinitionHandler.spring.ejb.context").getFactory();
            this.service = (ReportDefinitionHandlerInterface) factory.getBean("service.ReportDefinitionHandler");
        }
        catch (Exception e) {
            LOGGER.error("ejbCreate(): Exception ReportDefinitionHandlerComponent: " + e);
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

    public String create(final String xmlData, final SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, XmlSchemaValidationException, XmlCorruptedException, InvalidSqlException,
        MissingMethodParameterException, ScopeNotFoundException, ScopeContextViolationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xmlData);
    }

    public String create(final String xmlData, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, XmlSchemaValidationException, XmlCorruptedException,
        InvalidSqlException, MissingMethodParameterException, ScopeNotFoundException, ScopeContextViolationException,
        SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xmlData);
    }

    public void delete(final String id, final SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, ReportDefinitionNotFoundException, MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public void delete(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ReportDefinitionNotFoundException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public String retrieve(final String id, final SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, ReportDefinitionNotFoundException, MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public String retrieve(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ReportDefinitionNotFoundException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public String retrieveReportDefinitions(final Map filter, final SecurityContext securityContext)
        throws InvalidSearchQueryException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveReportDefinitions(filter);
    }

    public String retrieveReportDefinitions(final Map filter, final String authHandle, final Boolean restAccess)
        throws InvalidSearchQueryException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveReportDefinitions(filter);
    }

    public String update(final String id, final String xmlData, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ReportDefinitionNotFoundException,
        MissingMethodParameterException, ScopeNotFoundException, InvalidSqlException, ScopeContextViolationException,
        XmlSchemaValidationException, XmlCorruptedException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }

    public String update(final String id, final String xmlData, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ReportDefinitionNotFoundException,
        MissingMethodParameterException, ScopeNotFoundException, InvalidSqlException, ScopeContextViolationException,
        XmlSchemaValidationException, XmlCorruptedException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }
}