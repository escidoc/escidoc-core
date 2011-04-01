/*
 * Generate EJB from POJO with Spring framework Bean Factory
 * Bernhard Kraus (Accenture)
 */

package de.escidoc.core.sm.ejb;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSqlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ReportDefinitionNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.sm.service.interfaces.ReportHandlerInterface;
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

public class ReportHandlerBean implements SessionBean {

    private ReportHandlerInterface service;

    private SessionContext sessionCtx;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                beanFactoryLocator.useBeanFactory("ReportHandler.spring.ejb.context").getFactory();
            this.service = (ReportHandlerInterface) factory.getBean("service.ReportHandler");
        }
        catch (Exception e) {
            LOGGER.error("ejbCreate(): Exception ReportHandlerComponent: " + e);
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

    public String retrieve(final String xml, final SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, XmlCorruptedException, XmlSchemaValidationException, ReportDefinitionNotFoundException,
        MissingMethodParameterException, InvalidSqlException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(xml);
    }

    public String retrieve(final String xml, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, XmlCorruptedException, XmlSchemaValidationException,
        ReportDefinitionNotFoundException, MissingMethodParameterException, InvalidSqlException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(xml);
    }
}