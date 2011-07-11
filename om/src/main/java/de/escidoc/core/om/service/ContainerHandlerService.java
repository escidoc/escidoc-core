package de.escidoc.core.om.service;

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
import org.springframework.security.core.context.SecurityContext;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Service endpoint interface for ContainerHandler.
 */
public interface ContainerHandlerService extends Remote {

    String create(String xmlData, SecurityContext securityContext) throws ContextNotFoundException,
        ContentModelNotFoundException, InvalidContentException, MissingMethodParameterException, XmlCorruptedException,
        MissingAttributeValueException, MissingElementValueException, SystemException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AuthenticationException,
        AuthorizationException, InvalidStatusException, MissingMdRecordException, XmlSchemaValidationException,
        RemoteException;

    String create(String xmlData, String authHandle, Boolean restAccess) throws ContextNotFoundException,
        ContentModelNotFoundException, InvalidContentException, MissingMethodParameterException, XmlCorruptedException,
        MissingAttributeValueException, MissingElementValueException, SystemException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AuthenticationException,
        AuthorizationException, InvalidStatusException, MissingMdRecordException, XmlSchemaValidationException,
        RemoteException;

    void delete(String id, SecurityContext securityContext) throws ContainerNotFoundException, LockingException,
        InvalidStatusException, SystemException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, RemoteException;

    void delete(String id, String authHandle, Boolean restAccess) throws ContainerNotFoundException, LockingException,
        InvalidStatusException, SystemException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, RemoteException;

    String retrieve(String id, SecurityContext securityContext) throws MissingMethodParameterException,
        ContainerNotFoundException, AuthenticationException, AuthorizationException, SystemException, RemoteException;

    String retrieve(String id, String authHandle, Boolean restAccess) throws MissingMethodParameterException,
        ContainerNotFoundException, AuthenticationException, AuthorizationException, SystemException, RemoteException;

    String update(String id, String xmlData, SecurityContext securityContext) throws ContainerNotFoundException,
        LockingException, InvalidContentException, MissingMethodParameterException, InvalidXmlException,
        OptimisticLockingException, InvalidStatusException, ReadonlyVersionException, SystemException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AuthenticationException,
        AuthorizationException, MissingAttributeValueException, MissingMdRecordException, RemoteException;

    String update(String id, String xmlData, String authHandle, Boolean restAccess) throws ContainerNotFoundException,
        LockingException, InvalidContentException, MissingMethodParameterException, InvalidXmlException,
        OptimisticLockingException, InvalidStatusException, ReadonlyVersionException, SystemException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, AuthenticationException,
        AuthorizationException, MissingAttributeValueException, MissingMdRecordException, RemoteException;

    String retrieveMembers(String id, Map filter, SecurityContext securityContext) throws ContainerNotFoundException,
        InvalidSearchQueryException, MissingMethodParameterException, SystemException, RemoteException;

    String retrieveMembers(String id, Map filter, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, InvalidSearchQueryException, MissingMethodParameterException,
        SystemException, RemoteException;

    String retrieveTocs(String id, Map filter, SecurityContext securityContext) throws ContainerNotFoundException,
        InvalidXmlException, InvalidSearchQueryException, MissingMethodParameterException, SystemException,
        RemoteException;

    String retrieveTocs(String id, Map filter, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, InvalidXmlException, InvalidSearchQueryException,
        MissingMethodParameterException, SystemException, RemoteException;

    String addMembers(String id, String taskParam, SecurityContext securityContext) throws ContainerNotFoundException,
        LockingException, InvalidContentException, OptimisticLockingException, MissingMethodParameterException,
        SystemException, InvalidContextException, AuthenticationException, AuthorizationException,
        MissingAttributeValueException, RemoteException;

    String addMembers(String id, String taskParam, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, LockingException, InvalidContentException, OptimisticLockingException,
        MissingMethodParameterException, SystemException, InvalidContextException, AuthenticationException,
        AuthorizationException, MissingAttributeValueException, RemoteException;

    String addTocs(String id, String taskParam, SecurityContext securityContext) throws ContainerNotFoundException,
        LockingException, InvalidContentException, OptimisticLockingException, MissingMethodParameterException,
        SystemException, InvalidContextException, AuthenticationException, AuthorizationException,
        MissingAttributeValueException, RemoteException;

    String addTocs(String id, String taskParam, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, LockingException, InvalidContentException, OptimisticLockingException,
        MissingMethodParameterException, SystemException, InvalidContextException, AuthenticationException,
        AuthorizationException, MissingAttributeValueException, RemoteException;

    String removeMembers(String id, String taskParam, SecurityContext securityContext) throws ContextNotFoundException,
        LockingException, XmlSchemaValidationException, ItemNotFoundException, InvalidContextStatusException,
        InvalidItemStatusException, AuthenticationException, AuthorizationException, SystemException,
        ContainerNotFoundException, InvalidContentException, RemoteException;

    String removeMembers(String id, String taskParam, String authHandle, Boolean restAccess)
        throws ContextNotFoundException, LockingException, XmlSchemaValidationException, ItemNotFoundException,
        InvalidContextStatusException, InvalidItemStatusException, AuthenticationException, AuthorizationException,
        SystemException, ContainerNotFoundException, InvalidContentException, RemoteException;

    String retrieveMdRecord(String id, String mdRecordId, SecurityContext securityContext)
        throws ContainerNotFoundException, MissingMethodParameterException, MdRecordNotFoundException,
        AuthenticationException, AuthorizationException, SystemException, RemoteException;

    String retrieveMdRecord(String id, String mdRecordId, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, MissingMethodParameterException, MdRecordNotFoundException,
        AuthenticationException, AuthorizationException, SystemException, RemoteException;

    String retrieveMdRecords(String id, SecurityContext securityContext) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveMdRecords(String id, String authHandle, Boolean restAccess) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveProperties(String id, SecurityContext securityContext) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveProperties(String id, String authHandle, Boolean restAccess) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveStructMap(String id, SecurityContext securityContext) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveStructMap(String id, String authHandle, Boolean restAccess) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveVersionHistory(String id, SecurityContext securityContext) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveVersionHistory(String id, String authHandle, Boolean restAccess) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveParents(String id, SecurityContext securityContext) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveParents(String id, String authHandle, Boolean restAccess) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveRelations(String id, SecurityContext securityContext) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String retrieveRelations(String id, String authHandle, Boolean restAccess) throws ContainerNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String release(String id, String lastModified, SecurityContext securityContext) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, ReadonlyVersionException,
        AuthorizationException, InvalidStatusException, SystemException, OptimisticLockingException,
        InvalidXmlException, RemoteException;

    String release(String id, String lastModified, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        ReadonlyVersionException, AuthorizationException, InvalidStatusException, SystemException,
        OptimisticLockingException, InvalidXmlException, RemoteException;

    String submit(String id, String lastModified, SecurityContext securityContext) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        InvalidStatusException, SystemException, OptimisticLockingException, ReadonlyVersionException,
        InvalidXmlException, RemoteException;

    String submit(String id, String lastModified, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, InvalidStatusException, SystemException, OptimisticLockingException,
        ReadonlyVersionException, InvalidXmlException, RemoteException;

    String withdraw(String id, String lastModified, SecurityContext securityContext) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        InvalidStatusException, SystemException, OptimisticLockingException, AlreadyWithdrawnException,
        ReadonlyVersionException, InvalidXmlException, RemoteException;

    String withdraw(String id, String lastModified, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, InvalidStatusException, SystemException, OptimisticLockingException,
        AlreadyWithdrawnException, ReadonlyVersionException, InvalidXmlException, RemoteException;

    String revise(String id, String lastModified, SecurityContext securityContext) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, InvalidStatusException, SystemException,
        OptimisticLockingException, ReadonlyVersionException, XmlCorruptedException, RemoteException;

    String revise(String id, String lastModified, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, InvalidStatusException,
        SystemException, OptimisticLockingException, ReadonlyVersionException, XmlCorruptedException, RemoteException;

    String lock(String id, String lastModified, SecurityContext securityContext) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, OptimisticLockingException, InvalidStatusException, InvalidXmlException, RemoteException;

    String lock(String id, String lastModified, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException, RemoteException;

    String unlock(String id, String lastModified, SecurityContext securityContext) throws ContainerNotFoundException,
        LockingException, MissingMethodParameterException, AuthenticationException, AuthorizationException,
        SystemException, OptimisticLockingException, InvalidStatusException, InvalidXmlException, RemoteException;

    String unlock(String id, String lastModified, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, InvalidStatusException,
        InvalidXmlException, RemoteException;

    String moveToContext(String containerId, String taskParam, SecurityContext securityContext)
        throws ContainerNotFoundException, ContextNotFoundException, InvalidContentException, LockingException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String moveToContext(String containerId, String taskParam, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, ContextNotFoundException, InvalidContentException, LockingException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    String createItem(String containerId, String xmlData, SecurityContext securityContext)
        throws ContainerNotFoundException, MissingContentException, ContextNotFoundException,
        ContentModelNotFoundException, ReadonlyElementViolationException, MissingAttributeValueException,
        MissingElementValueException, ReadonlyAttributeViolationException, MissingMethodParameterException,
        InvalidXmlException, FileNotFoundException, LockingException, InvalidContentException, InvalidContextException,
        RelationPredicateNotFoundException, ReferencedResourceNotFoundException, SystemException,
        AuthenticationException, AuthorizationException, MissingMdRecordException, InvalidStatusException,
        RemoteException;

    String createItem(String containerId, String xmlData, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, MissingContentException, ContextNotFoundException,
        ContentModelNotFoundException, ReadonlyElementViolationException, MissingAttributeValueException,
        MissingElementValueException, ReadonlyAttributeViolationException, MissingMethodParameterException,
        InvalidXmlException, FileNotFoundException, LockingException, InvalidContentException, InvalidContextException,
        RelationPredicateNotFoundException, ReferencedResourceNotFoundException, SystemException,
        AuthenticationException, AuthorizationException, MissingMdRecordException, InvalidStatusException,
        RemoteException;

    String createContainer(String containerId, String xmlData, SecurityContext securityContext)
        throws MissingMethodParameterException, ContainerNotFoundException, LockingException, ContextNotFoundException,
        ContentModelNotFoundException, InvalidContentException, InvalidXmlException, MissingAttributeValueException,
        MissingElementValueException, AuthenticationException, AuthorizationException, InvalidContextException,
        RelationPredicateNotFoundException, InvalidStatusException, ReferencedResourceNotFoundException,
        SystemException, MissingMdRecordException, RemoteException;

    String createContainer(String containerId, String xmlData, String authHandle, Boolean restAccess)
        throws MissingMethodParameterException, ContainerNotFoundException, LockingException, ContextNotFoundException,
        ContentModelNotFoundException, InvalidContentException, InvalidXmlException, MissingAttributeValueException,
        MissingElementValueException, AuthenticationException, AuthorizationException, InvalidContextException,
        RelationPredicateNotFoundException, InvalidStatusException, ReferencedResourceNotFoundException,
        SystemException, MissingMdRecordException, RemoteException;

    String retrieveContainers(Map filter, SecurityContext securityContext) throws MissingMethodParameterException,
        InvalidSearchQueryException, InvalidXmlException, SystemException, RemoteException;

    String retrieveContainers(Map filter, String authHandle, Boolean restAccess)
        throws MissingMethodParameterException, InvalidSearchQueryException, InvalidXmlException, SystemException,
        RemoteException;

    String addContentRelations(String id, String param, SecurityContext securityContext) throws SystemException,
        ContainerNotFoundException, OptimisticLockingException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AlreadyExistsException, InvalidStatusException, InvalidXmlException,
        MissingElementValueException, LockingException, ReadonlyVersionException, InvalidContentException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, RemoteException;

    String addContentRelations(String id, String param, String authHandle, Boolean restAccess) throws SystemException,
        ContainerNotFoundException, OptimisticLockingException, ReferencedResourceNotFoundException,
        RelationPredicateNotFoundException, AlreadyExistsException, InvalidStatusException, InvalidXmlException,
        MissingElementValueException, LockingException, ReadonlyVersionException, InvalidContentException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, RemoteException;

    String removeContentRelations(String id, String param, SecurityContext securityContext) throws SystemException,
        ContainerNotFoundException, OptimisticLockingException, InvalidStatusException, MissingElementValueException,
        InvalidXmlException, ContentRelationNotFoundException, LockingException, ReadonlyVersionException,
        AuthenticationException, AuthorizationException, RemoteException;

    String removeContentRelations(String id, String param, String authHandle, Boolean restAccess)
        throws SystemException, ContainerNotFoundException, OptimisticLockingException, InvalidStatusException,
        MissingElementValueException, InvalidXmlException, ContentRelationNotFoundException, LockingException,
        ReadonlyVersionException, AuthenticationException, AuthorizationException, RemoteException;

    String assignObjectPid(String id, String param, SecurityContext securityContext) throws InvalidStatusException,
        ContainerNotFoundException, LockingException, MissingMethodParameterException, OptimisticLockingException,
        SystemException, InvalidXmlException, RemoteException;

    String assignObjectPid(String id, String param, String authHandle, Boolean restAccess)
        throws InvalidStatusException, ContainerNotFoundException, LockingException, MissingMethodParameterException,
        OptimisticLockingException, SystemException, InvalidXmlException, RemoteException;

    String assignVersionPid(String id, String param, SecurityContext securityContext)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException, XmlCorruptedException, ReadonlyVersionException,
        RemoteException;

    String assignVersionPid(String id, String param, String authHandle, Boolean restAccess)
        throws ContainerNotFoundException, LockingException, MissingMethodParameterException, SystemException,
        OptimisticLockingException, InvalidStatusException, XmlCorruptedException, ReadonlyVersionException,
        RemoteException;

}
