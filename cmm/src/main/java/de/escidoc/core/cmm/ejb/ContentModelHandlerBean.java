/*
 * Generate EJB from POJO with Spring framework Bean Factory
 * Bernhard Kraus (Accenture)
 */

package de.escidoc.core.cmm.ejb;

import de.escidoc.core.cmm.service.interfaces.ContentModelHandlerInterface;
import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidContentException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidStatusException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingAttributeValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ContentModelNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ContentStreamNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.LockingException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.ReadonlyVersionException;
import de.escidoc.core.common.exceptions.application.violated.ResourceInUseException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.UserContext;
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

public class ContentModelHandlerBean implements SessionBean {

    private ContentModelHandlerInterface service;

    private SessionContext sessionCtx;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentModelHandlerBean.class);

    public void ejbCreate() throws CreateException {
        try {
            final BeanFactoryLocator beanFactoryLocator = SingletonBeanFactoryLocator.getInstance();
            final BeanFactory factory =
                beanFactoryLocator.useBeanFactory("ContentModelHandler.spring.ejb.context").getFactory();
            this.service = (ContentModelHandlerInterface) factory.getBean("service.ContentModelHandler");
        }
        catch (Exception e) {
            LOGGER.error("ejbCreate(): Exception ContentModelHandlerComponent: " + e);
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
        MissingElementValueException, ContentModelNotFoundException, InvalidXmlException, InvalidStatusException,
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
        MissingAttributeValueException, MissingElementValueException, ContentModelNotFoundException,
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

    public String create(final String xmlData, final SecurityContext securityContext) throws InvalidContentException,
        MissingAttributeValueException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, XmlCorruptedException, XmlSchemaValidationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xmlData);
    }

    public String create(final String xmlData, final String authHandle, final Boolean restAccess)
        throws InvalidContentException, MissingAttributeValueException, SystemException, AuthenticationException,
        AuthorizationException, MissingMethodParameterException, XmlCorruptedException, XmlSchemaValidationException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.create(xmlData);
    }

    public void delete(final String id, final SecurityContext securityContext) throws SystemException,
        ContentModelNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, LockingException, InvalidStatusException, ResourceInUseException {

        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public void delete(final String id, final String authHandle, final Boolean restAccess) throws SystemException,
        ContentModelNotFoundException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, LockingException, InvalidStatusException, ResourceInUseException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        service.delete(id);
    }

    public String retrieve(final String id, final SecurityContext securityContext)
        throws ContentModelNotFoundException, SystemException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieve(id);
    }

    public String retrieve(final String id, final String authHandle, final Boolean restAccess)
        throws ContentModelNotFoundException, SystemException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException {
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
        throws ContentModelNotFoundException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveProperties(id);
    }

    public String retrieveProperties(final String id, final String authHandle, final Boolean restAccess)
        throws ContentModelNotFoundException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveProperties(id);
    }

    public String retrieveContentStreams(final String id, final SecurityContext securityContext)
        throws ContentModelNotFoundException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentStreams(id);
    }

    public String retrieveContentStreams(final String id, final String authHandle, final Boolean restAccess)
        throws ContentModelNotFoundException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentStreams(id);
    }

    public String retrieveContentStream(final String id, final String name, final SecurityContext securityContext)
        throws ContentModelNotFoundException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentStream(id, name);
    }

    public String retrieveContentStream(
        final String id, final String name, final String authHandle, final Boolean restAccess)
        throws ContentModelNotFoundException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentStream(id, name);
    }

    public EscidocBinaryContent retrieveContentStreamContent(
        final String id, final String name, final SecurityContext securityContext)
        throws ContentModelNotFoundException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ContentStreamNotFoundException, InvalidStatusException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentStreamContent(id, name);
    }

    public EscidocBinaryContent retrieveContentStreamContent(
        final String id, final String name, final String authHandle, final Boolean restAccess)
        throws ContentModelNotFoundException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException, ContentStreamNotFoundException, InvalidStatusException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentStreamContent(id, name);
    }

    public String retrieveResources(final String id, final SecurityContext securityContext)
        throws ContentModelNotFoundException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(id);
    }

    public String retrieveResources(final String id, final String authHandle, final Boolean restAccess)
        throws ContentModelNotFoundException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResources(id);
    }

    public String retrieveVersionHistory(final String id, final SecurityContext securityContext)
        throws ContentModelNotFoundException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveVersionHistory(id);
    }

    public String retrieveVersionHistory(final String id, final String authHandle, final Boolean restAccess)
        throws ContentModelNotFoundException, SystemException, AuthenticationException, AuthorizationException,
        MissingMethodParameterException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveVersionHistory(id);
    }

    public String retrieveContentModels(final Map parameterMap, final SecurityContext securityContext)
        throws InvalidSearchQueryException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentModels(parameterMap);
    }

    public String retrieveContentModels(final Map parameterMap, final String authHandle, final Boolean restAccess)
        throws InvalidSearchQueryException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveContentModels(parameterMap);
    }

    public String update(final String id, final String xmlData, final SecurityContext securityContext)
        throws InvalidXmlException, ContentModelNotFoundException, OptimisticLockingException, SystemException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, ReadonlyVersionException,
        MissingAttributeValueException, InvalidContentException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }

    public String update(final String id, final String xmlData, final String authHandle, final Boolean restAccess)
        throws InvalidXmlException, ContentModelNotFoundException, OptimisticLockingException, SystemException,
        AuthenticationException, AuthorizationException, MissingMethodParameterException, ReadonlyVersionException,
        MissingAttributeValueException, InvalidContentException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.update(id, xmlData);
    }

    public EscidocBinaryContent retrieveMdRecordDefinitionSchemaContent(
        final String id, final String name, final SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, ContentModelNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecordDefinitionSchemaContent(id, name);
    }

    public EscidocBinaryContent retrieveMdRecordDefinitionSchemaContent(
        final String id, final String name, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        ContentModelNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveMdRecordDefinitionSchemaContent(id, name);
    }

    public EscidocBinaryContent retrieveResourceDefinitionXsltContent(
        final String id, final String name, final SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, MissingMethodParameterException, ContentModelNotFoundException,
        ResourceNotFoundException, SystemException {
        try {
            UserContext.setUserContext(securityContext);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResourceDefinitionXsltContent(id, name);
    }

    public EscidocBinaryContent retrieveResourceDefinitionXsltContent(
        final String id, final String name, final String authHandle, final Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        ContentModelNotFoundException, ResourceNotFoundException, SystemException {
        try {
            UserContext.setUserContext(authHandle);
            UserContext.setRestAccess(restAccess);
        }
        catch (Exception e) {
            throw new SystemException("Initialization of security context failed.", e);
        }
        return service.retrieveResourceDefinitionXsltContent(id, name);
    }
}