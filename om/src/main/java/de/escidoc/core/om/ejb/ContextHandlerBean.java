/*
 * Generate EJB from POJO with Spring framework Bean Factory
 * Bernhard Kraus (Accenture)
 */

package de.escidoc.core.om.ejb;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.exceptions.application.invalid.ContextNotEmptyException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.AdminDescriptorNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OperationNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.StreamNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.ContextNameNotUniqueException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyAttributeViolationException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyElementViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import de.escidoc.core.om.service.interfaces.ContextHandlerInterface;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.SingletonBeanFactoryLocator;
import org.springframework.security.context.SecurityContext;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import java.rmi.RemoteException;
import java.util.Map;

public class ContextHandlerBean implements SessionBean {

    ContextHandlerInterface service = null;
    SessionContext sessionCtx;
    private static final Logger LOGGER = LoggerFactory.getLogger(ContextHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory = beanFactoryLocator.useBeanFactory("ContextHandler.spring.ejb.context").getFactory();
            service = (ContextHandlerInterface) factory.getBean("service.ContextHandler");
        } catch(Exception e) {
            LOGGER.error("ejbCreate(): Exception ContextHandlerComponent: " + e);
            throw new CreateException(e.getMessage());
        }
    }

    public void setSessionContext(final SessionContext arg0) throws RemoteException {
        sessionCtx = arg0;
    }

    public void ejbRemove() throws RemoteException {
    }

    public void ejbActivate() throws RemoteException {

    }

    public void ejbPassivate() throws RemoteException {

    }

    public String create(final String xmlData,
                                   final SecurityContext securityContext)
            throws MissingMethodParameterException,
            ContextNameNotUniqueException,
            AuthenticationException,
            AuthorizationException,
            SystemException,
            ContentModelNotFoundException,
            ReadonlyElementViolationException,
            MissingAttributeValueException,
            MissingElementValueException,
            ReadonlyAttributeViolationException,
            InvalidContentException,
            OrganizationalUnitNotFoundException,
            InvalidStatusException,
            XmlCorruptedException,
            XmlSchemaValidationException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xmlData);
    }

    public String create(final String xmlData, final String authHandle, final Boolean restAccess)
            throws MissingMethodParameterException,
            ContextNameNotUniqueException,
            AuthenticationException,
            AuthorizationException,
            SystemException,
            ContentModelNotFoundException,
            ReadonlyElementViolationException,
            MissingAttributeValueException,
            MissingElementValueException,
            ReadonlyAttributeViolationException,
            InvalidContentException,
            OrganizationalUnitNotFoundException,
            InvalidStatusException,
            XmlCorruptedException,
            XmlSchemaValidationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xmlData);
    }

    public void delete(final String id, final SecurityContext securityContext)
            throws ContextNotFoundException,
            ContextNotEmptyException,
            MissingMethodParameterException,
            InvalidStatusException,
            AuthenticationException,
            AuthorizationException,
            SystemException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public void delete(final String id, final String authHandle, final Boolean restAccess)
            throws ContextNotFoundException,
            ContextNotEmptyException,
            MissingMethodParameterException,
            InvalidStatusException,
            AuthenticationException,
            AuthorizationException,
            SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public String retrieve(final String id,
                                     final SecurityContext securityContext)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public String retrieve(final String id, final String authHandle, final Boolean restAccess)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public String retrieveProperties(final String id,
                                               final SecurityContext securityContext)
            throws ContextNotFoundException,
            SystemException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveProperties(id);
    }

    public String retrieveProperties(final String id, final String authHandle,
                                               final Boolean restAccess)
            throws ContextNotFoundException,
            SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveProperties(id);
    }

    public String update(final String id, final String xmlData,
                                   final SecurityContext securityContext)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            InvalidContentException,
            InvalidStatusException,
            AuthenticationException,
            AuthorizationException,
            ReadonlyElementViolationException,
            ReadonlyAttributeViolationException,
            OptimisticLockingException,
            ContextNameNotUniqueException,
            InvalidXmlException,
            MissingElementValueException,
            SystemException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }

    public String update(final String id, final String xmlData, final String authHandle,
                                   final Boolean restAccess)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            InvalidContentException,
            InvalidStatusException,
            AuthenticationException,
            AuthorizationException,
            ReadonlyElementViolationException,
            ReadonlyAttributeViolationException,
            OptimisticLockingException,
            ContextNameNotUniqueException,
            InvalidXmlException,
            MissingElementValueException,
            SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }

    public EscidocBinaryContent retrieveResource(final String id,
                                                                                        final String resourceName,
                                                                                        final Map parameters,
                                                                                        final SecurityContext securityContext)
            throws OperationNotFoundException,
            ContextNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResource(id, resourceName, parameters);
    }

    public EscidocBinaryContent retrieveResource(final String id,
                                                                                        final String resourceName,
                                                                                        final Map parameters,
                                                                                        final String authHandle,
                                                                                        final Boolean restAccess)
            throws OperationNotFoundException,
            ContextNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResource(id, resourceName, parameters);
    }

    public String retrieveResources(final String id,
                                              final SecurityContext securityContext)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(id);
    }

    public String retrieveResources(final String id, final String authHandle,
                                              final Boolean restAccess)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(id);
    }

    public String open(final String id, final String taskParam,
                                 final SecurityContext securityContext)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            InvalidStatusException,
            AuthenticationException,
            AuthorizationException,
            OptimisticLockingException,
            InvalidXmlException,
            SystemException,
            LockingException,
            StreamNotFoundException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.open(id, taskParam);
    }

    public String open(final String id, final String taskParam, final String authHandle,
                                 final Boolean restAccess)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            InvalidStatusException,
            AuthenticationException,
            AuthorizationException,
            OptimisticLockingException,
            InvalidXmlException,
            SystemException,
            LockingException,
            StreamNotFoundException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.open(id, taskParam);
    }

    public String close(final String id, final String taskParam,
                                  final SecurityContext securityContext)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException,
            OptimisticLockingException,
            InvalidXmlException,
            InvalidStatusException,
            LockingException,
            StreamNotFoundException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.close(id, taskParam);
    }

    public String close(final String id, final String taskParam, final String authHandle,
                                  final Boolean restAccess)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException,
            OptimisticLockingException,
            InvalidXmlException,
            InvalidStatusException,
            LockingException,
            StreamNotFoundException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.close(id, taskParam);
    }

    public String retrieveContexts(final Map filter,
                                             final SecurityContext securityContext)
            throws MissingMethodParameterException,
            SystemException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContexts(filter);
    }

    public String retrieveContexts(final Map filter, final String authHandle,
                                             final Boolean restAccess)
            throws MissingMethodParameterException,
            SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContexts(filter);
    }

    public String retrieveMembers(final String id, final Map filter,
                                            final SecurityContext securityContext)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            SystemException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMembers(id, filter);
    }

    public String retrieveMembers(final String id, final Map filter, final String authHandle,
                                            final Boolean restAccess)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMembers(id, filter);
    }

    public String retrieveAdminDescriptor(final String id, final String name,
                                                    final SecurityContext securityContext)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException,
            AdminDescriptorNotFoundException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveAdminDescriptor(id, name);
    }

    public String retrieveAdminDescriptor(final String id, final String name,
                                                    final String authHandle, final Boolean restAccess)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException,
            AdminDescriptorNotFoundException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveAdminDescriptor(id, name);
    }

    public String retrieveAdminDescriptors(final String id,
                                                     final SecurityContext securityContext)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException {
        try {
            UserContext.setUserContext(securityContext);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveAdminDescriptors(id);
    }

    public String retrieveAdminDescriptors(final String id, final String authHandle,
                                                     final Boolean restAccess)
            throws ContextNotFoundException,
            MissingMethodParameterException,
            AuthenticationException,
            AuthorizationException,
            SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        } catch(Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveAdminDescriptors(id);
    }
}