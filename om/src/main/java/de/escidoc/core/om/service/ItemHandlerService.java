package de.escidoc.core.om.service;

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
import de.escidoc.core.common.exceptions.application.notfound.ReferencedResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.RelationPredicateNotFoundException;
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
import org.springframework.security.core.context.SecurityContext;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Service endpoint interface for ItemHandler.
 */
public interface ItemHandlerService extends Remote {

    String create(String xmlData, SecurityContext securityContext) throws MissingContentException,
        ContextNotFoundException, ContentModelNotFoundException, ReadonlyElementViolationException,
        MissingAttributeValueException, MissingElementValueException, ReadonlyAttributeViolationException,
        AuthenticationException, AuthorizationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, FileNotFoundException, SystemException, InvalidContentException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, MissingMdRecordException,
        InvalidStatusException, RemoteException;

    String create(String xmlData, String authHandle, Boolean restAccess) throws MissingContentException,
        ContextNotFoundException, ContentModelNotFoundException, ReadonlyElementViolationException,
        MissingAttributeValueException, MissingElementValueException, ReadonlyAttributeViolationException,
        AuthenticationException, AuthorizationException, XmlCorruptedException, XmlSchemaValidationException,
        MissingMethodParameterException, FileNotFoundException, SystemException, InvalidContentException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, MissingMdRecordException,
        InvalidStatusException, RemoteException;

    void delete(String id, SecurityContext securityContext) throws ItemNotFoundException, AlreadyPublishedException,
        LockingException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, RemoteException;

    void delete(String id, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        AlreadyPublishedException, LockingException, AuthenticationException, AuthorizationException,
        InvalidStatusException, MissingMethodParameterException, SystemException, RemoteException;

    String retrieve(String id, SecurityContext securityContext) throws ItemNotFoundException,
        ComponentNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException, RemoteException;

    String retrieve(String id, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        ComponentNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException, RemoteException;

    String update(String id, String xmlData, SecurityContext securityContext) throws ItemNotFoundException,
        FileNotFoundException, InvalidContextException, InvalidStatusException, LockingException,
        NotPublishedException, MissingLicenceException, ComponentNotFoundException, MissingContentException,
        AuthenticationException, AuthorizationException, InvalidXmlException, MissingMethodParameterException,
        InvalidContentException, SystemException, OptimisticLockingException, AlreadyExistsException,
        ReadonlyViolationException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        ReadonlyVersionException, MissingAttributeValueException, MissingMdRecordException, RemoteException;

    String update(String id, String xmlData, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        FileNotFoundException, InvalidContextException, InvalidStatusException, LockingException,
        NotPublishedException, MissingLicenceException, ComponentNotFoundException, MissingContentException,
        AuthenticationException, AuthorizationException, InvalidXmlException, MissingMethodParameterException,
        InvalidContentException, SystemException, OptimisticLockingException, AlreadyExistsException,
        ReadonlyViolationException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        ReadonlyVersionException, MissingAttributeValueException, MissingMdRecordException, RemoteException;

    String createComponent(String id, String xmlData, SecurityContext securityContext) throws MissingContentException,
        ItemNotFoundException, ComponentNotFoundException, LockingException, MissingElementValueException,
        AuthenticationException, AuthorizationException, InvalidStatusException, MissingMethodParameterException,
        FileNotFoundException, InvalidXmlException, InvalidContentException, SystemException,
        ReadonlyViolationException, OptimisticLockingException, MissingAttributeValueException, RemoteException;

    String createComponent(String id, String xmlData, String authHandle, Boolean restAccess)
        throws MissingContentException, ItemNotFoundException, ComponentNotFoundException, LockingException,
        MissingElementValueException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, FileNotFoundException, InvalidXmlException, InvalidContentException,
        SystemException, ReadonlyViolationException, OptimisticLockingException, MissingAttributeValueException,
        RemoteException;

    String retrieveComponent(String id, String componentId, SecurityContext securityContext)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, RemoteException;

    String retrieveComponent(String id, String componentId, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, RemoteException;

    String retrieveComponentMdRecords(String id, String componentId, SecurityContext securityContext)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, RemoteException;

    String retrieveComponentMdRecords(String id, String componentId, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, RemoteException;

    String retrieveComponentMdRecord(String id, String componentId, String mdRecordId, SecurityContext securityContext)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, ComponentNotFoundException,
        MdRecordNotFoundException, MissingMethodParameterException, SystemException, RemoteException;

    String retrieveComponentMdRecord(
        String id, String componentId, String mdRecordId, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, ComponentNotFoundException,
        MdRecordNotFoundException, MissingMethodParameterException, SystemException, RemoteException;

    String updateComponent(String id, String componentId, String xmlData, SecurityContext securityContext)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, FileNotFoundException,
        MissingAttributeValueException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        ReadonlyViolationException, MissingContentException, InvalidContentException, ReadonlyVersionException,
        RemoteException;

    String updateComponent(String id, String componentId, String xmlData, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, FileNotFoundException,
        MissingAttributeValueException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        ReadonlyViolationException, MissingContentException, InvalidContentException, ReadonlyVersionException,
        RemoteException;

    String retrieveComponents(String id, SecurityContext securityContext) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, ComponentNotFoundException, MissingMethodParameterException,
        SystemException, RemoteException;

    String retrieveComponents(String id, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, ComponentNotFoundException, MissingMethodParameterException,
        SystemException, RemoteException;

    String retrieveComponentProperties(String id, String componentId, SecurityContext securityContext)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, RemoteException;

    String retrieveComponentProperties(String id, String componentId, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, RemoteException;

    String createMetadataRecord(String id, String xmlData, SecurityContext securityContext)
        throws ItemNotFoundException, ComponentNotFoundException, XmlSchemaNotFoundException, LockingException,
        MissingAttributeValueException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, InvalidXmlException, RemoteException;

    String createMetadataRecord(String id, String xmlData, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, XmlSchemaNotFoundException, LockingException,
        MissingAttributeValueException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, InvalidXmlException, RemoteException;

    String createMdRecord(String id, String xmlData, SecurityContext securityContext) throws ItemNotFoundException,
        SystemException, InvalidXmlException, LockingException, MissingAttributeValueException, InvalidStatusException,
        ComponentNotFoundException, MissingMethodParameterException, AuthorizationException, AuthenticationException,
        RemoteException;

    String createMdRecord(String id, String xmlData, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, SystemException, InvalidXmlException, LockingException,
        MissingAttributeValueException, InvalidStatusException, ComponentNotFoundException,
        MissingMethodParameterException, AuthorizationException, AuthenticationException, RemoteException;

    String retrieveMdRecord(String id, String mdRecordId, SecurityContext securityContext)
        throws ItemNotFoundException, MdRecordNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, RemoteException;

    String retrieveMdRecord(String id, String mdRecordId, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, MdRecordNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, RemoteException;

    String updateMdRecord(String id, String mdRecordId, String xmlData, SecurityContext securityContext)
        throws ItemNotFoundException, XmlSchemaNotFoundException, LockingException, InvalidContentException,
        MdRecordNotFoundException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        ReadonlyViolationException, ReadonlyVersionException, RemoteException;

    String updateMdRecord(String id, String mdRecordId, String xmlData, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, XmlSchemaNotFoundException, LockingException, InvalidContentException,
        MdRecordNotFoundException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        ReadonlyViolationException, ReadonlyVersionException, RemoteException;

    String retrieveMdRecords(String id, SecurityContext securityContext) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    String retrieveMdRecords(String id, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    String retrieveContentStreams(String id, SecurityContext securityContext) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    String retrieveContentStreams(String id, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    String retrieveContentStream(String id, String name, SecurityContext securityContext) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        ContentStreamNotFoundException, RemoteException;

    String retrieveContentStream(String id, String name, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, AuthenticationException, AuthorizationException, MissingMethodParameterException,
        SystemException, ContentStreamNotFoundException, RemoteException;

    String retrieveProperties(String id, SecurityContext securityContext) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    String retrieveProperties(String id, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    String retrieveVersionHistory(String id, SecurityContext securityContext) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    String retrieveVersionHistory(String id, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    String retrieveParents(String id, SecurityContext securityContext) throws ItemNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveParents(String id, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveRelations(String id, SecurityContext securityContext) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    String retrieveRelations(String id, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        RemoteException;

    String release(String id, String lastModified, SecurityContext securityContext) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, InvalidStatusException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        ReadonlyViolationException, ReadonlyVersionException, InvalidXmlException, RemoteException;

    String release(String id, String lastModified, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, InvalidStatusException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        ReadonlyViolationException, ReadonlyVersionException, InvalidXmlException, RemoteException;

    String submit(String id, String lastModified, SecurityContext securityContext) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, InvalidStatusException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        ReadonlyViolationException, ReadonlyVersionException, InvalidXmlException, RemoteException;

    String submit(String id, String lastModified, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, InvalidStatusException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        ReadonlyViolationException, ReadonlyVersionException, InvalidXmlException, RemoteException;

    String revise(String id, String lastModified, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, ItemNotFoundException, ComponentNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        ReadonlyViolationException, ReadonlyVersionException, InvalidContentException, XmlCorruptedException,
        RemoteException;

    String revise(String id, String lastModified, String authHandle, Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ItemNotFoundException, ComponentNotFoundException,
        LockingException, InvalidStatusException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, ReadonlyViolationException, ReadonlyVersionException, InvalidContentException,
        XmlCorruptedException, RemoteException;

    String withdraw(String id, String lastModified, SecurityContext securityContext) throws ItemNotFoundException,
        ComponentNotFoundException, NotPublishedException, LockingException, AlreadyWithdrawnException,
        AuthenticationException, AuthorizationException, InvalidStatusException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, ReadonlyViolationException, ReadonlyVersionException,
        InvalidXmlException, RemoteException;

    String withdraw(String id, String lastModified, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, NotPublishedException, LockingException,
        AlreadyWithdrawnException, AuthenticationException, AuthorizationException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, ReadonlyViolationException,
        ReadonlyVersionException, InvalidXmlException, RemoteException;

    String lock(String id, String lastModified, SecurityContext securityContext) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, InvalidContentException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidStatusException, RemoteException;

    String lock(String id, String lastModified, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, InvalidContentException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidXmlException, InvalidStatusException, RemoteException;

    String unlock(String id, String lastModified, SecurityContext securityContext) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        RemoteException;

    String unlock(String id, String lastModified, String authHandle, Boolean restAccess) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        RemoteException;

    void deleteComponent(String itemId, String componentId, SecurityContext securityContext)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, InvalidStatusException,
        RemoteException;

    void deleteComponent(String itemId, String componentId, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, InvalidStatusException,
        RemoteException;

    String moveToContext(String id, String taskParam, SecurityContext securityContext) throws ContextNotFoundException,
        InvalidContentException, ItemNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String moveToContext(String id, String taskParam, String authHandle, Boolean restAccess)
        throws ContextNotFoundException, InvalidContentException, ItemNotFoundException, LockingException,
        InvalidStatusException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, RemoteException;

    String retrieveItems(Map filter, SecurityContext securityContext) throws SystemException, RemoteException;

    String retrieveItems(Map filter, String authHandle, Boolean restAccess) throws SystemException, RemoteException;

    String assignVersionPid(String id, String taskParam, SecurityContext securityContext) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidStatusException,
        XmlCorruptedException, ReadonlyVersionException, RemoteException;

    String assignVersionPid(String id, String taskParam, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidStatusException, XmlCorruptedException, ReadonlyVersionException, RemoteException;

    String assignObjectPid(String id, String taskParam, SecurityContext securityContext) throws ItemNotFoundException,
        ComponentNotFoundException, LockingException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidStatusException,
        XmlCorruptedException, RemoteException;

    String assignObjectPid(String id, String taskParam, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, ComponentNotFoundException, LockingException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, OptimisticLockingException,
        InvalidStatusException, XmlCorruptedException, RemoteException;

    String assignContentPid(String id, String componentId, String taskParam, SecurityContext securityContext)
        throws ItemNotFoundException, LockingException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidStatusException,
        ComponentNotFoundException, XmlCorruptedException, ReadonlyVersionException, RemoteException;

    String assignContentPid(String id, String componentId, String taskParam, String authHandle, Boolean restAccess)
        throws ItemNotFoundException, LockingException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidStatusException,
        ComponentNotFoundException, XmlCorruptedException, ReadonlyVersionException, RemoteException;

    String addContentRelations(String id, String param, SecurityContext securityContext) throws SystemException,
        ItemNotFoundException, ComponentNotFoundException, OptimisticLockingException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AlreadyExistsException,
        InvalidStatusException, InvalidXmlException, MissingElementValueException, LockingException,
        ReadonlyViolationException, InvalidContentException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ReadonlyVersionException, RemoteException;

    String addContentRelations(String id, String param, String authHandle, Boolean restAccess) throws SystemException,
        ItemNotFoundException, ComponentNotFoundException, OptimisticLockingException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AlreadyExistsException,
        InvalidStatusException, InvalidXmlException, MissingElementValueException, LockingException,
        ReadonlyViolationException, InvalidContentException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ReadonlyVersionException, RemoteException;

    String removeContentRelations(String id, String param, SecurityContext securityContext) throws SystemException,
        ItemNotFoundException, ComponentNotFoundException, OptimisticLockingException, InvalidStatusException,
        MissingElementValueException, InvalidContentException, InvalidXmlException, ContentRelationNotFoundException,
        AlreadyDeletedException, LockingException, ReadonlyViolationException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, ReadonlyVersionException, RemoteException;

    String removeContentRelations(String id, String param, String authHandle, Boolean restAccess)
        throws SystemException, ItemNotFoundException, ComponentNotFoundException, OptimisticLockingException,
        InvalidStatusException, MissingElementValueException, InvalidContentException, InvalidXmlException,
        ContentRelationNotFoundException, AlreadyDeletedException, LockingException, ReadonlyViolationException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, ReadonlyVersionException,
        RemoteException;

}
