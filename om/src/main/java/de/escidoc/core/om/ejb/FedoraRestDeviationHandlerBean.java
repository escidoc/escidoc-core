package de.escidoc.core.om.ejb;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.om.service.interfaces.FedoraRestDeviationHandlerInterface;
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

public class FedoraRestDeviationHandlerBean implements SessionBean {

    private FedoraRestDeviationHandlerInterface service;

    private SessionContext sessionCtx;

    private static final Logger LOGGER = LoggerFactory.getLogger(FedoraRestDeviationHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                beanFactoryLocator.useBeanFactory("FedoraRestDeviationHandler.spring.ejb.context").getFactory();
            this.service = (FedoraRestDeviationHandlerInterface) factory.getBean("service.FedoraRestDeviationHandler");
        }
        catch (Exception e) {
            LOGGER.error("ejbCreate(): Exception FedoraRestDeviationHandlerComponent: " + e);
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
    public void ejbPassivate() throws RemoteException {

    }

    public final EscidocBinaryContent getDatastreamDissemination(
        final String pid, final String dsID, final Map parameters, final SecurityContext securityContext)
        throws Exception, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.getDatastreamDissemination(pid, dsID, parameters);
    }

    public final EscidocBinaryContent getDatastreamDissemination(
        final String pid, final String dsID, final Map parameters, final String authHandle, final Boolean restAccess)
        throws Exception, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.getDatastreamDissemination(pid, dsID, parameters);
    }

    public final String export(final String pid, final Map parameters, final SecurityContext securityContext)
        throws Exception, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.export(pid, parameters);
    }

    public final String export(final String pid, final Map parameters, final String authHandle, final Boolean restAccess)
        throws Exception, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.export(pid, parameters);
    }

    public final void cache(final String pid, final String xml, final SecurityContext securityContext)
        throws Exception, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.cache(pid, xml);
    }

    public final void cache(final String pid, final String xml, final String authHandle, final Boolean restAccess)
        throws Exception, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.cache(pid, xml);
    }

    public final void removeFromCache(final String pid, final SecurityContext securityContext) throws Exception,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.removeFromCache(pid);
    }

    public final void removeFromCache(final String pid, final String authHandle, final Boolean restAccess)
        throws Exception, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.removeFromCache(pid);
    }

    public final void replaceInCache(final String pid, final String xml, final SecurityContext securityContext)
        throws Exception, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.replaceInCache(pid, xml);
    }

    public final void replaceInCache(
        final String pid, final String xml, final String authHandle, final Boolean restAccess) throws Exception,
        SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.replaceInCache(pid, xml);
    }
}