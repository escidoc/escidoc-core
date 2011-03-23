/*
 * Generate EJB from POJO with Spring framework Bean Factory
 * Bernhard Kraus (Accenture)
 */

package de.escidoc.core.tme.ejb;

import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import de.escidoc.core.tme.service.interfaces.JhoveHandlerInterface;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.security.context.SecurityContext;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import java.rmi.RemoteException;

public class JhoveHandlerBean implements SessionBean {

    JhoveHandlerInterface service;
    SessionContext sessionCtx;
    private static final Logger LOGGER = LoggerFactory.getLogger(JhoveHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory = beanFactoryLocator.useBeanFactory("JhoveHandler.spring.ejb.context").getFactory();
            this.service = (JhoveHandlerInterface) factory.getBean("service.JhoveHandler");
        } catch(Exception e) {
            LOGGER.error("ejbCreate(): Exception JhoveHandlerComponent: " + e);
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

    public String extract(final String requests,
                                    final SecurityContext securityContext)
            throws AuthenticationException,
            AuthorizationException,
            XmlCorruptedException,
            XmlSchemaValidationException,
            MissingMethodParameterException,
            SystemException,
            TmeException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.extract(requests);
    }

    public String extract(final String requests, final String authHandle,
                                    final Boolean restAccess)
            throws AuthenticationException,
            AuthorizationException,
            XmlCorruptedException,
            XmlSchemaValidationException,
            MissingMethodParameterException,
            SystemException,
            TmeException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.extract(requests);
    }
}