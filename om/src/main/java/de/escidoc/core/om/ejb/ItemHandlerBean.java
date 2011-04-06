package de.escidoc.core.om.ejb;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContextException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingContentException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingLicenceException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ComponentNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentRelationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.FileNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.XmlSchemaNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyDeletedException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyExistsException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyPublishedException;
import de.escidoc.core.common.exceptions.application.violated.AlreadyWithdrawnException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.NotPublishedException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.om.service.interfaces.EscidocServiceRedirectInterface;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;
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

public class ItemHandlerBean implements SessionBean {

    private ItemHandlerInterface service;

    private SessionContext sessionCtx;

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                beanFactoryLocator.useBeanFactory("ItemHandler.spring.ejb.context").getFactory();
            this.service = (ItemHandlerInterface) factory.getBean("service.ItemHandler");
        }
        catch (Exception e) {
            LOGGER.error("ejbCreate(): Exception ItemHandlerComponent: " + e);
            throw new CreateException(e.getMessage()); // Ignore FindBugs
        }
    }

    @Override
    public final void setSessionContext(final SessionContext arg0) throws RemoteException {
        this.sessionCtx = arg0;
    }

    @Override
    public void ejbRemove() throws RemoteException {
    }

    @Override
    public final void ejbActivate() throws RemoteException {

    }

    @Override
    public final void ejbPassivate() throws RemoteException {

    }

    public final String create(final String xmlData, final SecurityContext securityContext)
        throws MissingContentException, ContextNotFoundException, ContentModelNotFoundException,
        ReadonlyElementViolationException, MissingAttributeValueException, MissingElementValueException,
        ReadonlyAttributeViolationException, AuthenticationException, AuthorizationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, FileNotFoundException, SystemException,
        InvalidContentException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        MissingMdRecordException, InvalidStatusException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xmlData);
    }

    public final String create(final String xmlData, final String authHandle, final Boolean restAccess)
        throws MissingContentException, ContextNotFoundException, ContentModelNotFoundException,
        ReadonlyElementViolationException, MissingAttributeValueException, MissingElementValueException,
        ReadonlyAttributeViolationException, AuthenticationException, AuthorizationException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMethodParameterException, FileNotFoundException, SystemException,
        InvalidContentException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        MissingMdRecordException, InvalidStatusException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xmlData);
    }

    public final void delete(final String id, final SecurityContext securityContext) throws ItemNotFoundException,
        AlreadyPublishedException, LockingException, AuthenticationException, AuthorizationException,
        InvalidStatusException, MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public final void delete(final String id, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, AlreadyPublishedException, LockingException, AuthenticationException,
        AuthorizationException, InvalidStatusException, MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public final String retrieve(final String id, final SecurityContext securityContext) throws ItemNotFoundException,
        ComponentNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public final String retrieve(final String id, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
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

    public final String update(final String id, final String xmlData, final SecurityContext securityContext)
        throws ItemNotFoundException, FileNotFoundException, InvalidContextException, InvalidStatusException,
        LockingException, NotPublishedException, MissingLicenceException, ComponentNotFoundException,
        MissingContentException, AuthenticationException, AuthorizationException, InvalidXmlException,
        MissingMethodParameterException, InvalidContentException, SystemException, OptimisticLockingException,
        AlreadyExistsException, ReadonlyViolationException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, ReadonlyVersionException, MissingAttributeValueException,
        MissingMdRecordException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }

    public final String update(final String id, final String xmlData, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, FileNotFoundException, InvalidContextException, InvalidStatusException,
        LockingException, NotPublishedException, MissingLicenceException, ComponentNotFoundException,
        MissingContentException, AuthenticationException, AuthorizationException, InvalidXmlException,
        MissingMethodParameterException, InvalidContentException, SystemException, OptimisticLockingException,
        AlreadyExistsException, ReadonlyViolationException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, ReadonlyVersionException, MissingAttributeValueException,
        MissingMdRecordException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }

    public final String createComponent(final String id, final String xmlData, final SecurityContext securityContext)
        throws MissingContentException, ItemNotFoundException, ComponentNotFoundException, LockingException,
        MissingElementValueException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, FileNotFoundException, InvalidXmlException, InvalidContentException,
        SystemException, ReadonlyViolationException, OptimisticLockingException, MissingAttributeValueException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createComponent(id, xmlData);
    }

    public final String createComponent(
        final String id, final String xmlData, final String authHandle, final Boolean restAccess)
        throws MissingContentException, ItemNotFoundException, ComponentNotFoundException, LockingException,
        MissingElementValueException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, FileNotFoundException, InvalidXmlException, InvalidContentException,
        SystemException, ReadonlyViolationException, OptimisticLockingException, MissingAttributeValueException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createComponent(id, xmlData);
    }

    public final String retrieveComponent(
        final String id, final String componentId, final SecurityContext securityContext) throws ItemNotFoundException,
        ComponentNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveComponent(id, componentId);
    }

    public final String retrieveComponent(
        final String id, final String componentId, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveComponent(id, componentId);
    }

    public final String retrieveComponentMdRecords(
        final String id, final String componentId, final SecurityContext securityContext) throws ItemNotFoundException,
        ComponentNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveComponentMdRecords(id, componentId);
    }

    public final String retrieveComponentMdRecords(
        final String id, final String componentId, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveComponentMdRecords(id, componentId);
    }

    public final String retrieveComponentMdRecord(
        final String id, final String componentId, final String mdRecordId, final SecurityContext securityContext)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, ComponentNotFoundException,
        MdRecordNotFoundException, MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveComponentMdRecord(id, componentId, mdRecordId);
    }

    public final String retrieveComponentMdRecord(
        final String id, final String componentId, final String mdRecordId, final String authHandle,
        final Boolean restAccess) throws ItemNotFoundException, AuthenticationException, AuthorizationException,
        ComponentNotFoundException, MdRecordNotFoundException, MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveComponentMdRecord(id, componentId, mdRecordId);
    }

    public final String updateComponent(
        final String id, final String componentId, final String xmlData, final SecurityContext securityContext)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, FileNotFoundException,
        MissingAttributeValueException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        ReadonlyViolationException, MissingContentException, InvalidContentException, ReadonlyVersionException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updateComponent(id, componentId, xmlData);
    }

    public final String updateComponent(
        final String id, final String componentId, final String xmlData, final String authHandle,
        final Boolean restAccess) throws ItemNotFoundException, ComponentNotFoundException, LockingException,
        FileNotFoundException, MissingAttributeValueException, AuthenticationException, AuthorizationException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, ReadonlyViolationException, MissingContentException, InvalidContentException,
        ReadonlyVersionException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updateComponent(id, componentId, xmlData);
    }

    public final String retrieveComponents(final String id, final SecurityContext securityContext)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveComponents(id);
    }

    public final String retrieveComponents(final String id, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, ComponentNotFoundException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveComponents(id);
    }

    public final String retrieveComponentProperties(
        final String id, final String componentId, final SecurityContext securityContext) throws ItemNotFoundException,
        ComponentNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveComponentProperties(id, componentId);
    }

    public final String retrieveComponentProperties(
        final String id, final String componentId, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveComponentProperties(id, componentId);
    }

    public final String createMetadataRecord(
        final String id, final String xmlData, final SecurityContext securityContext) throws ItemNotFoundException,
        ComponentNotFoundException, XmlSchemaNotFoundException, LockingException, MissingAttributeValueException,
        AuthenticationException, AuthorizationException, InvalidStatusException, MissingMethodParameterException,
        SystemException, InvalidXmlException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createMetadataRecord(id, xmlData);
    }

    public final String createMetadataRecord(
        final String id, final String xmlData, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, XmlSchemaNotFoundException, LockingException,
        MissingAttributeValueException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, InvalidXmlException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createMetadataRecord(id, xmlData);
    }

    public final String createMdRecord(final String id, final String xmlData, final SecurityContext securityContext)
        throws ItemNotFoundException, SystemException, InvalidXmlException, LockingException,
        MissingAttributeValueException, InvalidStatusException, ComponentNotFoundException,
        MissingMethodParameterException, AuthorizationException, AuthenticationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createMdRecord(id, xmlData);
    }

    public final String createMdRecord(
        final String id, final String xmlData, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, SystemException, InvalidXmlException, LockingException,
        MissingAttributeValueException, InvalidStatusException, ComponentNotFoundException,
        MissingMethodParameterException, AuthorizationException, AuthenticationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.createMdRecord(id, xmlData);
    }

    public final EscidocBinaryContent retrieveContent(
        final String id, final String contentId, final SecurityContext securityContext) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        InvalidStatusException, ResourceNotFoundException, ComponentNotFoundException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContent(id, contentId);
    }

    public final EscidocBinaryContent retrieveContent(
        final String id, final String contentId, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException, InvalidStatusException, ResourceNotFoundException, ComponentNotFoundException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContent(id, contentId);
    }

    public final EscidocBinaryContent retrieveContentStreamContent(
        final String itemId, final String name, final SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, ItemNotFoundException, SystemException,
        ContentStreamNotFoundException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentStreamContent(itemId, name);
    }

    public final EscidocBinaryContent retrieveContentStreamContent(
        final String itemId, final String name, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException, ItemNotFoundException,
        SystemException, ContentStreamNotFoundException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentStreamContent(itemId, name);
    }

    public final EscidocBinaryContent retrieveContent(
        final String id, final String contentId, final String transformer, final String param,
        final SecurityContext securityContext) throws ItemNotFoundException, ComponentNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        InvalidStatusException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContent(id, contentId, transformer, param);
    }

    public final EscidocBinaryContent retrieveContent(
        final String id, final String contentId, final String transformer, final String param, final String authHandle,
        final Boolean restAccess) throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, InvalidStatusException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContent(id, contentId, transformer, param);
    }

    public final EscidocServiceRedirectInterface redirectContentService(
        final String id, final String contentId, final String transformer, final String clientService,
        final SecurityContext securityContext) throws ItemNotFoundException, ComponentNotFoundException,
        AuthorizationException, MissingMethodParameterException, SystemException, InvalidStatusException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.redirectContentService(id, contentId, transformer, clientService);
    }

    public final EscidocServiceRedirectInterface redirectContentService(
        final String id, final String contentId, final String transformer, final String clientService,
        final String authHandle, final Boolean restAccess) throws ItemNotFoundException, ComponentNotFoundException,
        AuthorizationException, MissingMethodParameterException, SystemException, InvalidStatusException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.redirectContentService(id, contentId, transformer, clientService);
    }

    public final String retrieveMdRecord(final String id, final String mdRecordId, final SecurityContext securityContext)
        throws ItemNotFoundException, MdRecordNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {
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
        throws ItemNotFoundException, MdRecordNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException {
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
        final String id, final String mdRecordId, final SecurityContext securityContext) throws ItemNotFoundException,
        MdRecordNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
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
        throws ItemNotFoundException, MdRecordNotFoundException, AuthenticationException, AuthorizationException,
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
        throws ItemNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        MdRecordNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveDcRecordContent(id);
    }

    public final String retrieveDcRecordContent(final String id, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        MdRecordNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveDcRecordContent(id);
    }

    public final String updateMdRecord(
        final String id, final String mdRecordId, final String xmlData, final SecurityContext securityContext)
        throws ItemNotFoundException, XmlSchemaNotFoundException, LockingException, InvalidContentException,
        MdRecordNotFoundException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        ReadonlyViolationException, ReadonlyVersionException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("updateMdRecord");
        }
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updateMdRecord(id, mdRecordId, xmlData);
    }

    public final String updateMdRecord(
        final String id, final String mdRecordId, final String xmlData, final String authHandle,
        final Boolean restAccess) throws ItemNotFoundException, XmlSchemaNotFoundException, LockingException,
        InvalidContentException, MdRecordNotFoundException, AuthenticationException, AuthorizationException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, ReadonlyViolationException, ReadonlyVersionException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updateMdRecord(id, mdRecordId, xmlData);
    }

    public final String retrieveMdRecords(final String id, final SecurityContext securityContext)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecords(id);
    }

    public final String retrieveMdRecords(final String id, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecords(id);
    }

    public final String retrieveContentStreams(final String id, final SecurityContext securityContext)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentStreams(id);
    }

    public final String retrieveContentStreams(final String id, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentStreams(id);
    }

    public final String retrieveContentStream(final String id, final String name, final SecurityContext securityContext)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException, ContentStreamNotFoundException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentStream(id, name);
    }

    public final String retrieveContentStream(
        final String id, final String name, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException, ContentStreamNotFoundException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentStream(id, name);
    }

    public final String retrieveProperties(final String id, final SecurityContext securityContext)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveProperties(id);
    }

    public final String retrieveProperties(final String id, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
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
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(id);
    }

    public final String retrieveResources(final String id, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
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
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException, OperationNotFoundException {
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
        final Boolean restAccess) throws ItemNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, OperationNotFoundException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResource(id, resourceName, parameters);
    }

    public final String retrieveVersionHistory(final String id, final SecurityContext securityContext)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveVersionHistory(id);
    }

    public final String retrieveVersionHistory(final String id, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
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
        throws ItemNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveParents(id);
    }

    public final String retrieveParents(final String id, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
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
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveRelations(id);
    }

    public final String retrieveRelations(final String id, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException {
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
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, InvalidStatusException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException, ReadonlyVersionException, InvalidXmlException {
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
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, InvalidStatusException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException, ReadonlyVersionException, InvalidXmlException {
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
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, InvalidStatusException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException, ReadonlyVersionException, InvalidXmlException {
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
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, InvalidStatusException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException, ReadonlyVersionException, InvalidXmlException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.submit(id, lastModified);
    }

    public final String revise(final String id, final String lastModified, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ItemNotFoundException, ComponentNotFoundException,
        LockingException, InvalidStatusException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException, ReadonlyVersionException, InvalidContentException,
        XmlCorruptedException {
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
        throws AuthenticationException, AuthorizationException, ItemNotFoundException, ComponentNotFoundException,
        LockingException, InvalidStatusException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException, ReadonlyVersionException, InvalidContentException,
        XmlCorruptedException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.revise(id, lastModified);
    }

    public final String withdraw(final String id, final String lastModified, final SecurityContext securityContext)
        throws ItemNotFoundException, ComponentNotFoundException, NotPublishedException, LockingException,
        AlreadyWithdrawnException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, ReadonlyViolationException,
        ReadonlyVersionException, InvalidXmlException {
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
        throws ItemNotFoundException, ComponentNotFoundException, NotPublishedException, LockingException,
        AlreadyWithdrawnException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, ReadonlyViolationException,
        ReadonlyVersionException, InvalidXmlException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.withdraw(id, lastModified);
    }

    public final String lock(final String id, final String lastModified, final SecurityContext securityContext)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, InvalidContentException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidXmlException, InvalidStatusException {
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
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, InvalidContentException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidXmlException, InvalidStatusException {
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
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
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
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
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

    public final void deleteComponent(
        final String itemId, final String componentId, final SecurityContext securityContext)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, InvalidStatusException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.deleteComponent(itemId, componentId);
    }

    public final void deleteComponent(
        final String itemId, final String componentId, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, InvalidStatusException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.deleteComponent(itemId, componentId);
    }

    public final String moveToContext(final String id, final String taskParam, final SecurityContext securityContext)
        throws ContextNotFoundException, InvalidContentException, ItemNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.moveToContext(id, taskParam);
    }

    public final String moveToContext(
        final String id, final String taskParam, final String authHandle, final Boolean restAccess)
        throws ContextNotFoundException, InvalidContentException, ItemNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.moveToContext(id, taskParam);
    }

    public final String retrieveItems(final Map filter, final SecurityContext securityContext) throws SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveItems(filter);
    }

    public final String retrieveItems(final Map filter, final String authHandle, final Boolean restAccess)
        throws SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveItems(filter);
    }

    public final String assignVersionPid(final String id, final String taskParam, final SecurityContext securityContext)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidStatusException, XmlCorruptedException, ReadonlyVersionException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.assignVersionPid(id, taskParam);
    }

    public final String assignVersionPid(
        final String id, final String taskParam, final String authHandle, final Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidStatusException, XmlCorruptedException, ReadonlyVersionException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.assignVersionPid(id, taskParam);
    }

    public final String assignObjectPid(final String id, final String taskParam, final SecurityContext securityContext)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidStatusException, XmlCorruptedException {
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
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidStatusException, XmlCorruptedException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.assignObjectPid(id, taskParam);
    }

    public final String assignContentPid(
        final String id, final String componentId, final String taskParam, final SecurityContext securityContext)
        throws ItemNotFoundException, LockingException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidStatusException,
        ComponentNotFoundException, XmlCorruptedException, ReadonlyVersionException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.assignContentPid(id, componentId, taskParam);
    }

    public final String assignContentPid(
        final String id, final String componentId, final String taskParam, final String authHandle,
        final Boolean restAccess) throws ItemNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidStatusException, ComponentNotFoundException, XmlCorruptedException, ReadonlyVersionException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.assignContentPid(id, componentId, taskParam);
    }

    public final String addContentRelations(final String id, final String param, final SecurityContext securityContext)
        throws SystemException, ItemNotFoundException, ComponentNotFoundException, OptimisticLockingException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AlreadyExistsException,
        InvalidStatusException, InvalidXmlException, MissingElementValueException, LockingException,
        ReadonlyViolationException, InvalidContentException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ReadonlyVersionException {
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
        ItemNotFoundException, ComponentNotFoundException, OptimisticLockingException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AlreadyExistsException,
        InvalidStatusException, InvalidXmlException, MissingElementValueException, LockingException,
        ReadonlyViolationException, InvalidContentException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ReadonlyVersionException {
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
        ItemNotFoundException, ComponentNotFoundException, OptimisticLockingException, InvalidStatusException,
        MissingElementValueException, InvalidContentException, InvalidXmlException, ContentRelationNotFoundException,
        AlreadyDeletedException, LockingException, ReadonlyViolationException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, ReadonlyVersionException {
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
        ItemNotFoundException, ComponentNotFoundException, OptimisticLockingException, InvalidStatusException,
        MissingElementValueException, InvalidContentException, InvalidXmlException, ContentRelationNotFoundException,
        AlreadyDeletedException, LockingException, ReadonlyViolationException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, ReadonlyVersionException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.removeContentRelations(id, param);
    }
}