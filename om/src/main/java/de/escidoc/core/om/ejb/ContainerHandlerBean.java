package de.escidoc.core.om.ejb;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidItemStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContainerNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyWithdrawnException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;
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

public class ContainerHandlerBean implements SessionBean {

    private ContainerHandlerInterface service;

    private SessionContext sessionCtx;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                beanFactoryLocator.useBeanFactory("ContainerHandler.spring.ejb.context").getFactory();
            this.service = (ContainerHandlerInterface) factory.getBean("service.ContainerHandler");
        }
        catch (Exception e) {
            LOGGER.error("ejbCreate(): Exception ContainerHandlerComponent: " + e);
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
        throws ContextNotFoundException, ContentModelNotFoundException, InvalidContentException,
        MissingMethodParameterException, XmlCorruptedException, MissingAttributeValueException,
        MissingElementValueException, SystemException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMdRecordException, XmlSchemaValidationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xmlData);
    }

    public final String create(final String xmlData, final String authHandle, final Boolean restAccess)
        throws ContextNotFoundException, ContentModelNotFoundException, InvalidContentException,
        MissingMethodParameterException, XmlCorruptedException, MissingAttributeValueException,
        MissingElementValueException, SystemException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMdRecordException, XmlSchemaValidationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xmlData);
    }

    public final void delete(final String id, final SecurityContext securityContext) throws ContainerNotFoundException,
        LockingException, InvalidStatusException, SystemException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public final void delete(final String id, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, LockingException, InvalidStatusException, SystemException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public final String retrieve(final String id, final SecurityContext securityContext)
        throws MissingMethodParameterException, ContainerNotFoundException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public final String retrieve(final String id, final String authHandle, final Boolean restAccess)
        throws MissingMethodParameterException, ContainerNotFoundException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public final String update(final String id, final String xmlData, final SecurityContext securityContext)
        throws ContainerNotFoundException, LockingException, InvalidContentException, MissingMethodParameterException,
        InvalidXmlException, OptimisticLockingException, InvalidStatusException, ReadonlyVersionException,
        SystemException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        AuthenticationException, AuthorizationException, MissingAttributeValueException, MissingMdRecordException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }

    public final String update(final String id, final String xmlData, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, LockingException, InvalidContentException, MissingMethodParameterException,
        InvalidXmlException, OptimisticLockingException, InvalidStatusException, ReadonlyVersionException,
        SystemException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        AuthenticationException, AuthorizationException, MissingAttributeValueException, MissingMdRecordException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }

    public final String retrieveMembers(final String id, final Map filter, final SecurityContext securityContext)
        throws ContainerNotFoundException, InvalidSearchQueryException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMembers(id, filter);
    }

    public final String retrieveMembers(
        final String id, final Map filter, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, InvalidSearchQueryException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMembers(id, filter);
    }

    public final String retrieveTocs(final String id, final Map filter, final SecurityContext securityContext)
        throws ContainerNotFoundException, InvalidXmlException, InvalidSearchQueryException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveTocs(id, filter);
    }

    public final String retrieveTocs(
        final String id, final Map filter, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, InvalidXmlException, InvalidSearchQueryException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveTocs(id, filter);
    }

    public final String addMembers(final String id, final String taskParam, final SecurityContext securityContext)
        throws ContainerNotFoundException, LockingException, InvalidContentException, OptimisticLockingException,
        MissingMethodParameterException, SystemException, InvalidContextException, AuthenticationException,
        AuthorizationException, MissingAttributeValueException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.addMembers(id, taskParam);
    }

    public final String addMembers(
        final String id, final String taskParam, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, LockingException, InvalidContentException, OptimisticLockingException,
        MissingMethodParameterException, SystemException, InvalidContextException, AuthenticationException,
        AuthorizationException, MissingAttributeValueException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.addMembers(id, taskParam);
    }

    public final String addTocs(final String id, final String taskParam, final SecurityContext securityContext)
        throws ContainerNotFoundException, LockingException, InvalidContentException, OptimisticLockingException,
        MissingMethodParameterException, SystemException, InvalidContextException, AuthenticationException,
        AuthorizationException, MissingAttributeValueException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.addTocs(id, taskParam);
    }

    public final String addTocs(
        final String id, final String taskParam, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, LockingException, InvalidContentException, OptimisticLockingException,
        MissingMethodParameterException, SystemException, InvalidContextException, AuthenticationException,
        AuthorizationException, MissingAttributeValueException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.addTocs(id, taskParam);
    }

    public final String removeMembers(final String id, final String taskParam, final SecurityContext securityContext)
        throws ContextNotFoundException, LockingException, XmlSchemaValidationException, ItemNotFoundException,
        InvalidContextStatusException, InvalidItemStatusException, AuthenticationException, AuthorizationException,
        SystemException, ContainerNotFoundException, InvalidContentException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.removeMembers(id, taskParam);
    }

    public final String removeMembers(
        final String id, final String taskParam, final String authHandle, final Boolean restAccess)
        throws ContextNotFoundException, LockingException, XmlSchemaValidationException, ItemNotFoundException,
        InvalidContextStatusException, InvalidItemStatusException, AuthenticationException, AuthorizationException,
        SystemException, ContainerNotFoundException, InvalidContentException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.removeMembers(id, taskParam);
    }

    public final String retrieveMdRecord(final String id, final String mdRecordId, final SecurityContext securityContext)
        throws ContainerNotFoundException, MissingMethodParameterException, MdRecordNotFoundException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecord(id, mdRecordId);
    }

    public final String retrieveMdRecord(
        final String id, final String mdRecordId, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, MissingMethodParameterException, MdRecordNotFoundException,
        AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecord(id, mdRecordId);
    }

    public final String retrieveMdRecordContent(
        final String id, final String mdRecordId, final SecurityContext securityContext)
        throws ContainerNotFoundException, MdRecordNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecordContent(id, mdRecordId);
    }

    public final String retrieveMdRecordContent(
        final String id, final String mdRecordId, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, MdRecordNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecordContent(id, mdRecordId);
    }

    public final String retrieveDcRecordContent(final String id, final SecurityContext securityContext)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveDcRecordContent(id);
    }

    public final String retrieveDcRecordContent(final String id, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveDcRecordContent(id);
    }

    public final String retrieveMdRecords(final String id, final SecurityContext securityContext)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecords(id);
    }

    public final String retrieveMdRecords(final String id, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecords(id);
    }

    public final String retrieveProperties(final String id, final SecurityContext securityContext)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveProperties(id);
    }

    public final String retrieveProperties(final String id, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveProperties(id);
    }

    public final String retrieveResources(final String id, final SecurityContext securityContext)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(id);
    }

    public final String retrieveResources(final String id, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(id);
    }

    public final EscidocBinaryContent retrieveResource(
        final String id, final String resourceName, final Map parameters, final SecurityContext securityContext)
        throws SystemException, ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, OperationNotFoundException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResource(id, resourceName, parameters);
    }

    public final EscidocBinaryContent retrieveResource(
        final String id, final String resourceName, final Map parameters, final String authHandle,
        final Boolean restAccess) throws SystemException, ContainerNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, OperationNotFoundException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResource(id, resourceName, parameters);
    }

    public final String retrieveStructMap(final String id, final SecurityContext securityContext)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveStructMap(id);
    }

    public final String retrieveStructMap(final String id, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveStructMap(id);
    }

    public final String retrieveVersionHistory(final String id, final SecurityContext securityContext)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveVersionHistory(id);
    }

    public final String retrieveVersionHistory(final String id, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveVersionHistory(id);
    }

    public final String retrieveParents(final String id, final SecurityContext securityContext)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveParents(id);
    }

    public final String retrieveParents(final String id, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveParents(id);
    }

    public final String retrieveRelations(final String id, final SecurityContext securityContext)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveRelations(id);
    }

    public final String retrieveRelations(final String id, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveRelations(id);
    }

    public final String release(final String id, final String lastModified, final SecurityContext securityContext)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        ReadonlyVersionException, AuthorizationException, InvalidStatusException, SystemException,
        OptimisticLockingException, InvalidXmlException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.release(id, lastModified);
    }

    public final String release(
        final String id, final String lastModified, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        ReadonlyVersionException, AuthorizationException, InvalidStatusException, SystemException,
        OptimisticLockingException, InvalidXmlException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.release(id, lastModified);
    }

    public final String submit(final String id, final String lastModified, final SecurityContext securityContext)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, InvalidStatusException, SystemException, OptimisticLockingException,
        ReadonlyVersionException, InvalidXmlException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.submit(id, lastModified);
    }

    public final String submit(
        final String id, final String lastModified, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, InvalidStatusException, SystemException, OptimisticLockingException,
        ReadonlyVersionException, InvalidXmlException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.submit(id, lastModified);
    }

    public final String withdraw(final String id, final String lastModified, final SecurityContext securityContext)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, InvalidStatusException, SystemException, OptimisticLockingException,
        AlreadyWithdrawnException, ReadonlyVersionException, InvalidXmlException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.withdraw(id, lastModified);
    }

    public final String withdraw(
        final String id, final String lastModified, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, InvalidStatusException, SystemException, OptimisticLockingException,
        AlreadyWithdrawnException, ReadonlyVersionException, InvalidXmlException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.withdraw(id, lastModified);
    }

    public final String revise(final String id, final String lastModified, final SecurityContext securityContext)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, InvalidStatusException,
        SystemException, OptimisticLockingException, ReadonlyVersionException, XmlCorruptedException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.revise(id, lastModified);
    }

    public final String revise(
        final String id, final String lastModified, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, InvalidStatusException,
        SystemException, OptimisticLockingException, ReadonlyVersionException, XmlCorruptedException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.revise(id, lastModified);
    }

    public final String lock(final String id, final String lastModified, final SecurityContext securityContext)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.lock(id, lastModified);
    }

    public final String lock(
        final String id, final String lastModified, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.lock(id, lastModified);
    }

    public final String unlock(final String id, final String lastModified, final SecurityContext securityContext)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.unlock(id, lastModified);
    }

    public final String unlock(
        final String id, final String lastModified, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.unlock(id, lastModified);
    }

    public final String moveToContext(
        final String containerId, final String taskParam, final SecurityContext securityContext)
        throws ContainerNotFoundException, ContextNotFoundException, InvalidContentException, LockingException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.moveToContext(containerId, taskParam);
    }

    public final String moveToContext(
        final String containerId, final String taskParam, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, ContextNotFoundException, InvalidContentException, LockingException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.moveToContext(containerId, taskParam);
    }

    public final String createItem(final String containerId, final String xmlData, final SecurityContext securityContext)
        throws ContainerNotFoundException, MissingContentException, ContextNotFoundException,
        ContentModelNotFoundException, ReadonlyElementViolationException, MissingAttributeValueException,
        MissingElementValueException, ReadonlyAttributeViolationException, MissingMethodParameterException,
        InvalidXmlException, FileNotFoundException, LockingException, InvalidContentException, InvalidContextException,
        RelationPredicateNotFoundException, ReferencedResourceNotFoundException, SystemException,
        AuthenticationException, AuthorizationException, MissingMdRecordException, InvalidStatusException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createItem(containerId, xmlData);
    }

    public final String createItem(
        final String containerId, final String xmlData, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, MissingContentException, ContextNotFoundException,
        ContentModelNotFoundException, ReadonlyElementViolationException, MissingAttributeValueException,
        MissingElementValueException, ReadonlyAttributeViolationException, MissingMethodParameterException,
        InvalidXmlException, FileNotFoundException, LockingException, InvalidContentException, InvalidContextException,
        RelationPredicateNotFoundException, ReferencedResourceNotFoundException, SystemException,
        AuthenticationException, AuthorizationException, MissingMdRecordException, InvalidStatusException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createItem(containerId, xmlData);
    }

    public final String createContainer(
        final String containerId, final String xmlData, final SecurityContext securityContext)
        throws MissingMethodParameterException, ContainerNotFoundException, LockingException, ContextNotFoundException,
        ContentModelNotFoundException, InvalidContentException, InvalidXmlException, MissingAttributeValueException,
        MissingElementValueException, AuthenticationException, AuthorizationException, InvalidContextException,
        RelationPredicateNotFoundException, InvalidStatusException, ReferencedResourceNotFoundException,
        SystemException, MissingMdRecordException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createContainer(containerId, xmlData);
    }

    public final String createContainer(
        final String containerId, final String xmlData, final String authHandle, final Boolean restAccess)
        throws MissingMethodParameterException, ContainerNotFoundException, LockingException, ContextNotFoundException,
        ContentModelNotFoundException, InvalidContentException, InvalidXmlException, MissingAttributeValueException,
        MissingElementValueException, AuthenticationException, AuthorizationException, InvalidContextException,
        RelationPredicateNotFoundException, InvalidStatusException, ReferencedResourceNotFoundException,
        SystemException, MissingMdRecordException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createContainer(containerId, xmlData);
    }

    public final String retrieveContainers(final Map filter, final SecurityContext securityContext)
        throws MissingMethodParameterException, InvalidSearchQueryException, InvalidXmlException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContainers(filter);
    }

    public final String retrieveContainers(final Map filter, final String authHandle, final Boolean restAccess)
        throws MissingMethodParameterException, InvalidSearchQueryException, InvalidXmlException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContainers(filter);
    }

    public final String addContentRelations(final String id, final String param, final SecurityContext securityContext)
        throws SystemException, ContainerNotFoundException, OptimisticLockingException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AlreadyExistsException,
        InvalidStatusException, InvalidXmlException, MissingElementValueException, LockingException,
        ReadonlyVersionException, InvalidContentException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.addContentRelations(id, param);
    }

    public final String addContentRelations(
        final String id, final String param, final String authHandle, final Boolean restAccess) throws SystemException,
        ContainerNotFoundException, OptimisticLockingException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AlreadyExistsException, InvalidStatusException, InvalidXmlException,
        MissingElementValueException, LockingException, ReadonlyVersionException, InvalidContentException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.addContentRelations(id, param);
    }

    public final String removeContentRelations(
        final String id, final String param, final SecurityContext securityContext) throws SystemException,
        ContainerNotFoundException, OptimisticLockingException, InvalidStatusException, MissingElementValueException,
        InvalidXmlException, ContentRelationNotFoundException, LockingException, ReadonlyVersionException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.removeContentRelations(id, param);
    }

    public final String removeContentRelations(
        final String id, final String param, final String authHandle, final Boolean restAccess) throws SystemException,
        ContainerNotFoundException, OptimisticLockingException, InvalidStatusException, MissingElementValueException,
        InvalidXmlException, ContentRelationNotFoundException, LockingException, ReadonlyVersionException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.removeContentRelations(id, param);
    }

    public final String assignObjectPid(final String id, final String param, final SecurityContext securityContext)
        throws InvalidStatusException, ContainerNotFoundException, LockingException, MissingMethodParameterException,
        OptimisticLockingException, SystemException, InvalidXmlException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.assignObjectPid(id, param);
    }

    public final String assignObjectPid(
        final String id, final String param, final String authHandle, final Boolean restAccess)
        throws InvalidStatusException, ContainerNotFoundException, LockingException, MissingMethodParameterException,
        OptimisticLockingException, SystemException, InvalidXmlException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.assignObjectPid(id, param);
    }

    public final String assignVersionPid(final String id, final String param, final SecurityContext securityContext)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException, XmlCorruptedException, ReadonlyVersionException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.assignVersionPid(id, param);
    }

    public final String assignVersionPid(
        final String id, final String param, final String authHandle, final Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException, XmlCorruptedException, ReadonlyVersionException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.assignVersionPid(id, param);
    }
}