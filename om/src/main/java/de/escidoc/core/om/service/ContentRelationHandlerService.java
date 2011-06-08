package de.escidoc.core.om.service;

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
import org.springframework.security.core.context.SecurityContext;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Service endpoint interface for ContentRelationHandler.
 */
public interface ContentRelationHandlerService extends Remote {

    String create(String xmlData, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingAttributeValueException, MissingMethodParameterException, InvalidXmlException,
        InvalidContentException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        SystemException, RemoteException;

    String create(String xmlData, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingAttributeValueException, MissingMethodParameterException, InvalidXmlException,
        InvalidContentException, ReferencedResourceNotFoundException, RelationPredicateNotFoundException,
        SystemException, RemoteException;

    void delete(String id, SecurityContext securityContext) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, SystemException, LockingException, RemoteException;

    void delete(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, SystemException, LockingException, RemoteException;

    String lock(String id, String param, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, InvalidContentException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidStatusException, RemoteException;

    String lock(String id, String param, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, InvalidContentException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidStatusException, RemoteException;

    String unlock(String id, String param, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidXmlException, InvalidContentException,
        InvalidStatusException, RemoteException;

    String unlock(String id, String param, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, MissingMethodParameterException,
        SystemException, OptimisticLockingException, InvalidXmlException, InvalidContentException,
        InvalidStatusException, RemoteException;

    String submit(String id, String param, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidContentException, RemoteException;

    String submit(String id, String param, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidContentException, RemoteException;

    String release(String id, String param, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidContentException, RemoteException;

    String release(String id, String param, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidContentException, RemoteException;

    String revise(String id, String param, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, XmlCorruptedException,
        InvalidContentException, RemoteException;

    String revise(String id, String param, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, LockingException, InvalidStatusException,
        MissingMethodParameterException, SystemException, OptimisticLockingException, XmlCorruptedException,
        InvalidContentException, RemoteException;

    String retrieve(String id, SecurityContext securityContext) throws AuthenticationException, AuthorizationException,
        ContentRelationNotFoundException, SystemException, RemoteException;

    String retrieve(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, SystemException, RemoteException;

    String retrieveContentRelations(Map parameterMap, SecurityContext securityContext)
        throws InvalidSearchQueryException, SystemException, RemoteException;

    String retrieveContentRelations(Map parameterMap, String authHandle, Boolean restAccess)
        throws InvalidSearchQueryException, SystemException, RemoteException;

    String retrieveProperties(String id, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, SystemException, RemoteException;

    String retrieveProperties(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, SystemException, RemoteException;

    String update(String id, String xmlData, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, OptimisticLockingException, InvalidContentException,
        InvalidStatusException, LockingException, MissingAttributeValueException, SystemException, InvalidXmlException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, MissingMethodParameterException,
        RemoteException;

    String update(String id, String xmlData, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, OptimisticLockingException, InvalidContentException,
        InvalidStatusException, LockingException, MissingAttributeValueException, SystemException, InvalidXmlException,
        ReferencedResourceNotFoundException, RelationPredicateNotFoundException, MissingMethodParameterException,
        RemoteException;

    String assignObjectPid(String id, String taskParam, SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        MissingMethodParameterException, OptimisticLockingException, InvalidXmlException, SystemException,
        PidAlreadyAssignedException, RemoteException;

    String assignObjectPid(String id, String taskParam, String authHandle, Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException, LockingException,
        MissingMethodParameterException, OptimisticLockingException, InvalidXmlException, SystemException,
        PidAlreadyAssignedException, RemoteException;

    String retrieveMdRecords(String id, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, SystemException, RemoteException;

    String retrieveMdRecords(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, SystemException, RemoteException;

    String retrieveRegisteredPredicates(SecurityContext securityContext) throws InvalidContentException,
        InvalidXmlException, SystemException, RemoteException;

    String retrieveRegisteredPredicates(String authHandle, Boolean restAccess) throws InvalidContentException,
        InvalidXmlException, SystemException, RemoteException;

    String retrieveMdRecord(String id, String name, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, ContentRelationNotFoundException, MdRecordNotFoundException, SystemException,
        RemoteException;

    String retrieveMdRecord(String id, String name, String authHandle, Boolean restAccess)
        throws AuthenticationException, AuthorizationException, ContentRelationNotFoundException,
        MdRecordNotFoundException, SystemException, RemoteException;

}
