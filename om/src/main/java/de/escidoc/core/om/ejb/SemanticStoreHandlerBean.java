/*
 * Generate EJB from POJO with Spring framework Bean Factory
 * Bernhard Kraus (Accenture)
 */

package de.escidoc.core.om.ejb;

import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreOutputFormatException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import de.escidoc.core.om.service.interfaces.SemanticStoreHandlerInterface;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.security.context.SecurityContext;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import java.rmi.RemoteException;

public class SemanticStoreHandlerBean implements SessionBean {

    SemanticStoreHandlerInterface service;
    SessionContext sessionCtx;
    private static final Logger LOGGER = LoggerFactory.getLogger(SemanticStoreHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                    beanFactoryLocator.useBeanFactory("SemanticStoreHandler.spring.ejb.context").getFactory();
            this.service = (SemanticStoreHandlerInterface) factory.getBean("service.SemanticStoreHandler");
        } catch(Exception e) {
            LOGGER.error("ejbCreate(): Exception SemanticStoreHandlerComponent: " + e);
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

    public String spo(final String taskParam,
                                final SecurityContext securityContext)
            throws SystemException,
            InvalidTripleStoreQueryException,
            InvalidTripleStoreOutputFormatException,
            InvalidXmlException,
            MissingElementValueException,
            AuthenticationException,
            AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.spo(taskParam);
    }

    public String spo(final String taskParam, final String authHandle, final Boolean restAccess)
            throws SystemException,
            InvalidTripleStoreQueryException,
            InvalidTripleStoreOutputFormatException,
            InvalidXmlException,
            MissingElementValueException,
            AuthenticationException,
            AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.spo(taskParam);
    }
}