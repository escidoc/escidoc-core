package de.escidoc.core.oum.ejb.interfaces;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.exceptions.EscidocException;
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
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHasChildrenException;
import de.escidoc.core.common.exceptions.application.violated.OrganizationalUnitHierarchyViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBLocalObject;
import java.util.Map;

/**
 * Local interface for OrganizationalUnitHandler.
 */
public interface OrganizationalUnitHandlerLocal extends EJBLocalObject {

    String ingest(String xmlData, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, MissingAttributeValueException,
        MissingElementValueException, OrganizationalUnitNotFoundException, InvalidXmlException, InvalidStatusException,
        EscidocException;

    String ingest(String xmlData, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, MissingAttributeValueException,
        MissingElementValueException, OrganizationalUnitNotFoundException, InvalidXmlException, InvalidStatusException,
        EscidocException;

    String create(String xml, SecurityContext securityContext) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, SystemException, MissingAttributeValueException, MissingElementValueException,
        OrganizationalUnitNotFoundException, InvalidStatusException, XmlCorruptedException,
        XmlSchemaValidationException, MissingMdRecordException;

    String create(String xml, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, MissingAttributeValueException,
        MissingElementValueException, OrganizationalUnitNotFoundException, InvalidStatusException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMdRecordException;

    void delete(String id, SecurityContext securityContext) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, InvalidStatusException,
        OrganizationalUnitHasChildrenException, SystemException;

    void delete(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, OrganizationalUnitHasChildrenException, SystemException;

    String update(String id, String user, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        OptimisticLockingException, OrganizationalUnitHierarchyViolationException, InvalidXmlException,
        MissingElementValueException, InvalidStatusException;

    String update(String id, String user, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException,
        OptimisticLockingException, OrganizationalUnitHierarchyViolationException, InvalidXmlException,
        MissingElementValueException, InvalidStatusException;

    String updateMdRecords(String id, String xml, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, InvalidXmlException, InvalidStatusException, MissingMethodParameterException,
        OptimisticLockingException, OrganizationalUnitNotFoundException, MissingElementValueException, SystemException;

    String updateMdRecords(String id, String xml, String authHandle, Boolean restAccess)
        throws AuthenticationException, AuthorizationException, InvalidXmlException, InvalidStatusException,
        MissingMethodParameterException, OptimisticLockingException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException;

    String updateParents(String id, String xml, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, InvalidXmlException, MissingMethodParameterException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException, InvalidStatusException;

    String updateParents(String id, String xml, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, InvalidXmlException, MissingMethodParameterException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException, InvalidStatusException;

    String retrieve(String id, SecurityContext securityContext) throws AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieve(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveProperties(String id, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveProperties(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    EscidocBinaryContent retrieveResource(String id, String resourceName, SecurityContext securityContext)
        throws OrganizationalUnitNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OperationNotFoundException, SystemException;

    EscidocBinaryContent retrieveResource(String id, String resourceName, String authHandle, Boolean restAccess)
        throws OrganizationalUnitNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OperationNotFoundException, SystemException;

    String retrieveResources(String ouId, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveResources(String ouId, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveMdRecords(String id, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveMdRecords(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveMdRecord(String id, String name, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MdRecordNotFoundException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException;

    String retrieveMdRecord(String id, String name, String authHandle, Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MdRecordNotFoundException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveParents(String id, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveParents(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveParentObjects(String ouId, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveParentObjects(String ouId, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveSuccessors(String id, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveSuccessors(String id, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveChildObjects(String ouId, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrieveChildObjects(String ouId, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException;

    String retrievePathList(String ouId, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, OrganizationalUnitNotFoundException, SystemException, MissingMethodParameterException;

    String retrievePathList(String ouId, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, OrganizationalUnitNotFoundException, SystemException, MissingMethodParameterException;

    String retrieveOrganizationalUnits(Map filter, SecurityContext securityContext)
        throws MissingMethodParameterException, SystemException, InvalidSearchQueryException, InvalidXmlException;

    String retrieveOrganizationalUnits(Map filter, String authHandle, Boolean restAccess)
        throws MissingMethodParameterException, SystemException, InvalidSearchQueryException, InvalidXmlException;

    String close(String id, String taskParam, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException, InvalidXmlException;

    String close(String id, String taskParam, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException, InvalidXmlException;

    String open(String id, String taskParam, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException, InvalidXmlException;

    String open(String id, String taskParam, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, SystemException, OptimisticLockingException, InvalidXmlException;

}
