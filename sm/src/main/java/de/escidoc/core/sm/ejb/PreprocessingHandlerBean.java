/*
 * Generate EJB from POJO with Spring framework Bean Factory
 * Bernhard Kraus (Accenture)
 */

package de.escidoc.core.sm.ejb;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.sm.service.interfaces.PreprocessingHandlerInterface;
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

public class PreprocessingHandlerBean implements SessionBean {

    private PreprocessingHandlerInterface service;

    private SessionContext sessionCtx;

    private static final Logger LOGGER = LoggerFactory.getLogger(PreprocessingHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                beanFactoryLocator.useBeanFactory("PreprocessingHandler.spring.ejb.context").getFactory();
            this.service = (PreprocessingHandlerInterface) factory.getBean("service.PreprocessingHandler");
        }
        catch (Exception e) {
            LOGGER.error("ejbCreate(): Exception PreprocessingHandlerComponent: " + e);
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

    public void preprocess(
        final String aggregationDefinitionId, final String xmlData, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, XmlSchemaValidationException, XmlCorruptedException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.preprocess(aggregationDefinitionId, xmlData);
    }

    public void preprocess(
        final String aggregationDefinitionId, final String xmlData, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, XmlSchemaValidationException, XmlCorruptedException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.preprocess(aggregationDefinitionId, xmlData);
    }
}