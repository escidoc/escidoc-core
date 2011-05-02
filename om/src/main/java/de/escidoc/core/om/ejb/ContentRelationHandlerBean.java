package de.escidoc.core.om.ejb;

import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.PidAlreadyAssignedException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.om.service.interfaces.ContentRelationHandlerInterface;
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

public class ContentRelationHandlerBean implements SessionBean {

    private ContentRelationHandlerInterface service;

    private SessionContext sessionCtx;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentRelationHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                beanFactoryLocator.useBeanFactory("ContentRelationHandler.spring.ejb.context").getFactory();
            this.service = (ContentRelationHandlerInterface) factory.getBean("service.ContentRelationHandler");
        }
        catch (Exception e) {
            LOGGER.error("ejbCreate(): Exception ContentRelationHandlerComponent: " + e);
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
        throws AuthenticationException, AuthorizationException, MissingAttributeValueException,
        MissingMethodParameterException, InvalidXmlException, InvalidContentException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xmlData);
    }

    public final String create(final String xmlData, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingAttributeValueException,
        MissingMethodParameterException, InvalidXmlException, InvalidContentException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, SystemException {
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
        AuthorizationException, ContentRelationNotFoundException, SystemException, LockingException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public final void delete(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, SystemException,
        LockingException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public final String lock(final String id, final String param, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        InvalidContentException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidStatusException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.lock(id, param);
    }

    public final String lock(final String id, final String param, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        InvalidContentException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidStatusException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.lock(id, param);
    }

    public final String unlock(final String id, final String param, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidContentException, InvalidStatusException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.unlock(id, param);
    }

    public final String unlock(final String id, final String param, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidContentException, InvalidStatusException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.unlock(id, param);
    }

    public final String submit(final String id, final String param, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidContentException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.submit(id, param);
    }

    public final String submit(final String id, final String param, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidContentException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.submit(id, param);
    }

    public final String release(final String id, final String param, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidContentException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.release(id, param);
    }

    public final String release(final String id, final String param, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidContentException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.release(id, param);
    }

    public final String revise(final String id, final String param, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        XmlCorruptedException, InvalidContentException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.revise(id, param);
    }

    public final String revise(final String id, final String param, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        XmlCorruptedException, InvalidContentException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.revise(id, param);
    }

    public final String retrieve(final String id, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public final String retrieve(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public final String retrieveContentRelations(final Map parameterMap, final SecurityContext securityContext)
        throws InvalidSearchQueryException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentRelations(parameterMap);
    }

    public final String retrieveContentRelations(
        final Map parameterMap, final String authHandle, final Boolean restAccess) throws InvalidSearchQueryException,
        SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentRelations(parameterMap);
    }

    public final String retrieveProperties(final String id, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveProperties(id);
    }

    public final String retrieveProperties(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveProperties(id);
    }

    public final String update(final String id, final String xmlData, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException,
        OptimisticLockingException, InvalidContentException, InvalidStatusException, LockingException,
        MissingAttributeValueException, SystemException, InvalidXmlException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, MissingMethodParameterException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }

    public final String update(final String id, final String xmlData, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException,
        OptimisticLockingException, InvalidContentException, InvalidStatusException, LockingException,
        MissingAttributeValueException, SystemException, InvalidXmlException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, MissingMethodParameterException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }

    public final String assignObjectPid(final String id, final String taskParam, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        MissingMethodParameterException, OptimisticLockingException, InvalidXmlException, SystemException,
        PidAlreadyAssignedException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.assignObjectPid(id, taskParam);
    }

    public final String assignObjectPid(
        final String id, final String taskParam, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        MissingMethodParameterException, OptimisticLockingException, InvalidXmlException, SystemException,
        PidAlreadyAssignedException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.assignObjectPid(id, taskParam);
    }

    public final String retrieveMdRecords(final String id, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecords(id);
    }

    public final String retrieveMdRecords(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecords(id);
    }

    public final String retrieveRegisteredPredicates(final SecurityContext securityContext)
        throws InvalidContentException, InvalidXmlException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveRegisteredPredicates();
    }

    public final String retrieveRegisteredPredicates(final String authHandle, final Boolean restAccess)
        throws InvalidContentException, InvalidXmlException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveRegisteredPredicates();
    }

    public final String retrieveMdRecord(final String id, final String name, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException,
        MdRecordNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecord(id, name);
    }

    public final String retrieveMdRecord(
        final String id, final String name, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException,
        MdRecordNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecord(id, name);
    }

    public final String retrieveResources(final String id, final SecurityContext securityContext)
        throws ContentRelationNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(id);
    }

    public final String retrieveResources(final String id, final String authHandle, final Boolean restAccess)
        throws ContentRelationNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(id);
    }
}