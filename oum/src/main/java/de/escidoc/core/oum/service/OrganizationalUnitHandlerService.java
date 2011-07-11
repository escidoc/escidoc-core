package de.escidoc.core.oum.service;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMdRecordException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.MdRecordNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHasChildrenException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHierarchyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.core.context.SecurityContext;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Service endpoint interface for OrganizationalUnitHandler.
 */
public interface OrganizationalUnitHandlerService extends Remote {

    String create(String xml, SecurityContext securityContext) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, MissingAttributeValueException, MissingElementValueException,
        OrganizationalUnitNotFoundException, InvalidStatusException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMdRecordException, RemoteException;

    String create(String xml, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, MissingAttributeValueException,
        MissingElementValueException, OrganizationalUnitNotFoundException, InvalidStatusException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMdRecordException, RemoteException;

    void delete(String id, SecurityContext securityContext) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, InvalidStatusException,
        OrganizationalUnitHasChildrenException, SystemException, RemoteException;

    void delete(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, OrganizationalUnitHasChildrenException, SystemException, RemoteException;

    String update(String id, String user, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        OptimisticLockingException, OrganizationalUnitHierarchyViolationException, InvalidXmlException,
        MissingElementValueException, InvalidStatusException, RemoteException;

    String update(String id, String user, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        OptimisticLockingException, OrganizationalUnitHierarchyViolationException, InvalidXmlException,
        MissingElementValueException, InvalidStatusException, RemoteException;

    String updateMdRecords(String id, String xml, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, InvalidXmlException, InvalidStatusException, MissingMethodParameterException,
        OptimisticLockingException, OrganizationalUnitNotFoundException, MissingElementValueException, SystemException,
        RemoteException;

    String updateMdRecords(String id, String xml, String authHandle, Boolean restAccess)
        throws AuthenticationException, AuthorizationException, InvalidXmlException, InvalidStatusException,
        MissingMethodParameterException, OptimisticLockingException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException, RemoteException;

    String updateParents(String id, String xml, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, InvalidXmlException, MissingMethodParameterException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException, InvalidStatusException, RemoteException;

    String updateParents(String id, String xml, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, InvalidXmlException, MissingMethodParameterException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException, InvalidStatusException, RemoteException;

    String retrieve(String id, SecurityContext securityContext) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException, RemoteException;

    String retrieve(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        RemoteException;

    String retrieveProperties(String id, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        RemoteException;

    String retrieveProperties(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        RemoteException;

    String retrieveMdRecords(String id, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        RemoteException;

    String retrieveMdRecords(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        RemoteException;

    String retrieveMdRecord(String id, String name, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MdRecordNotFoundException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException, RemoteException;

    String retrieveMdRecord(String id, String name, String authHandle, Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MdRecordNotFoundException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException, RemoteException;

    String retrieveParents(String id, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        RemoteException;

    String retrieveParents(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        RemoteException;

    String retrieveParentObjects(String ouId, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        RemoteException;

    String retrieveParentObjects(String ouId, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        RemoteException;

    String retrieveSuccessors(String id, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        RemoteException;

    String retrieveSuccessors(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        RemoteException;

    String retrieveChildObjects(String ouId, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        RemoteException;

    String retrieveChildObjects(String ouId, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        RemoteException;

    String retrievePathList(String ouId, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, OrganizationalUnitNotFoundException, SystemException, MissingMethodParameterException,
        RemoteException;

    String retrievePathList(String ouId, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, OrganizationalUnitNotFoundException, SystemException, MissingMethodParameterException,
        RemoteException;

    String retrieveOrganizationalUnits(Map filter, SecurityContext securityContext)
        throws MissingMethodParameterException, SystemException, InvalidSearchQueryException, InvalidXmlException,
        RemoteException;

    String retrieveOrganizationalUnits(Map filter, String authHandle, Boolean restAccess)
        throws MissingMethodParameterException, SystemException, InvalidSearchQueryException, InvalidXmlException,
        RemoteException;

    String close(String id, String taskParam, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException, InvalidXmlException, RemoteException;

    String close(String id, String taskParam, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException, InvalidXmlException, RemoteException;

    String open(String id, String taskParam, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException, InvalidXmlException, RemoteException;

    String open(String id, String taskParam, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException, InvalidXmlException, RemoteException;

}
