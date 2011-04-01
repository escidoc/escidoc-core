/*
 * Generate EJB from POJO with Spring framework Bean Factory
 * Bernhard Kraus (Accenture)
 */

package de.escidoc.core.oum.ejb;

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
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface;
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

public class OrganizationalUnitHandlerBean implements SessionBean {

    private OrganizationalUnitHandlerInterface service;

    private SessionContext sessionCtx;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationalUnitHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                beanFactoryLocator.useBeanFactory("OrganizationalUnitHandler.spring.ejb.context").getFactory();
            this.service = (OrganizationalUnitHandlerInterface) factory.getBean("service.OrganizationalUnitHandler");
        }
        catch (Exception e) {
            LOGGER.error("ejbCreate(): Exception OrganizationalUnitHandlerComponent: " + e);
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

    public String ingest(final String xmlData, final SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, MissingAttributeValueException,
        MissingElementValueException, OrganizationalUnitNotFoundException, InvalidXmlException, InvalidStatusException,
        EscidocException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.ingest(xmlData);
    }

    public String ingest(final String xmlData, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        MissingAttributeValueException, MissingElementValueException, OrganizationalUnitNotFoundException,
        InvalidXmlException, InvalidStatusException, EscidocException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.ingest(xmlData);
    }

    public String create(final String xml, final SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, SystemException, MissingAttributeValueException,
        MissingElementValueException, OrganizationalUnitNotFoundException, InvalidStatusException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMdRecordException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xml);
    }

    public String create(final String xml, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException, SystemException,
        MissingAttributeValueException, MissingElementValueException, OrganizationalUnitNotFoundException,
        InvalidStatusException, XmlCorruptedException, XmlSchemaValidationException, MissingMdRecordException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xml);
    }

    public void delete(final String id, final SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException,
        InvalidStatusException, OrganizationalUnitHasChildrenException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public void delete(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, InvalidStatusException, OrganizationalUnitHasChildrenException,
        SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public String update(final String id, final String user, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, InvalidXmlException, MissingElementValueException,
        InvalidStatusException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, user);
    }

    public String update(final String id, final String user, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException, OptimisticLockingException,
        OrganizationalUnitHierarchyViolationException, InvalidXmlException, MissingElementValueException,
        InvalidStatusException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, user);
    }

    public String updateMdRecords(final String id, final String xml, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, InvalidXmlException, InvalidStatusException,
        MissingMethodParameterException, OptimisticLockingException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updateMdRecords(id, xml);
    }

    public String updateMdRecords(final String id, final String xml, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, InvalidXmlException, InvalidStatusException,
        MissingMethodParameterException, OptimisticLockingException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updateMdRecords(id, xml);
    }

    public String updateParents(final String id, final String xml, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, InvalidXmlException, MissingMethodParameterException,
        OptimisticLockingException, OrganizationalUnitHierarchyViolationException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException, InvalidStatusException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updateParents(id, xml);
    }

    public String updateParents(final String id, final String xml, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, InvalidXmlException, MissingMethodParameterException,
        OptimisticLockingException, OrganizationalUnitHierarchyViolationException, OrganizationalUnitNotFoundException,
        MissingElementValueException, SystemException, InvalidStatusException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.updateParents(id, xml);
    }

    public String retrieve(final String id, final SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public String retrieve(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public String retrieveProperties(final String id, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveProperties(id);
    }

    public String retrieveProperties(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveProperties(id);
    }

    public EscidocBinaryContent retrieveResource(
        final String id, final String resourceName, final SecurityContext securityContext)
        throws OrganizationalUnitNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OperationNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResource(id, resourceName);
    }

    public EscidocBinaryContent retrieveResource(
        final String id, final String resourceName, final String authHandle, final Boolean restAccess)
        throws OrganizationalUnitNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, OperationNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResource(id, resourceName);
    }

    public String retrieveResources(final String ouId, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(ouId);
    }

    public String retrieveResources(final String ouId, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(ouId);
    }

    public String retrieveMdRecords(final String id, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecords(id);
    }

    public String retrieveMdRecords(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecords(id);
    }

    public String retrieveMdRecord(final String id, final String name, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MdRecordNotFoundException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecord(id, name);
    }

    public String retrieveMdRecord(final String id, final String name, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MdRecordNotFoundException,
        MissingMethodParameterException, OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecord(id, name);
    }

    public String retrieveParents(final String id, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveParents(id);
    }

    public String retrieveParents(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveParents(id);
    }

    public String retrieveParentObjects(final String ouId, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveParentObjects(ouId);
    }

    public String retrieveParentObjects(final String ouId, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveParentObjects(ouId);
    }

    public String retrieveSuccessors(final String id, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveSuccessors(id);
    }

    public String retrieveSuccessors(final String id, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveSuccessors(id);
    }

    public String retrieveChildObjects(final String ouId, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveChildObjects(ouId);
    }

    public String retrieveChildObjects(final String ouId, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveChildObjects(ouId);
    }

    public String retrievePathList(final String ouId, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, OrganizationalUnitNotFoundException, SystemException,
        MissingMethodParameterException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrievePathList(ouId);
    }

    public String retrievePathList(final String ouId, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, OrganizationalUnitNotFoundException, SystemException,
        MissingMethodParameterException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrievePathList(ouId);
    }

    public String retrieveOrganizationalUnits(final Map filter, final SecurityContext securityContext)
        throws MissingMethodParameterException, SystemException, InvalidSearchQueryException, InvalidXmlException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveOrganizationalUnits(filter);
    }

    public String retrieveOrganizationalUnits(final Map filter, final String authHandle, final Boolean restAccess)
        throws MissingMethodParameterException, SystemException, InvalidSearchQueryException, InvalidXmlException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveOrganizationalUnits(filter);
    }

    public String close(final String id, final String taskParam, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, InvalidStatusException, SystemException, OptimisticLockingException,
        InvalidXmlException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.close(id, taskParam);
    }

    public String close(final String id, final String taskParam, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, InvalidStatusException, SystemException, OptimisticLockingException,
        InvalidXmlException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.close(id, taskParam);
    }

    public String open(final String id, final String taskParam, final SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, InvalidStatusException, SystemException, OptimisticLockingException,
        InvalidXmlException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.open(id, taskParam);
    }

    public String open(final String id, final String taskParam, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        OrganizationalUnitNotFoundException, InvalidStatusException, SystemException, OptimisticLockingException,
        InvalidXmlException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.open(id, taskParam);
    }
}