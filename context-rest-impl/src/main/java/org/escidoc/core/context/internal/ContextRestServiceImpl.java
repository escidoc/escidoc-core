/**
 *
 */
package org.escidoc.core.context.internal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import net.sf.oval.guard.Guarded;

import org.escidoc.core.context.ContextRestService;
import org.escidoc.core.domain.ObjectFactoryProvider;
import org.escidoc.core.domain.context.*;
import org.escidoc.core.domain.result.ResultTypeTO;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.ResponseTypeTO;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.escidoc.core.domain.taskparam.status.StatusTaskParamTO;
import org.escidoc.core.services.fedora.FedoraServiceClient;
import org.escidoc.core.utils.io.EscidocBinaryContent;
import org.escidoc.core.utils.io.IOUtils;
import org.escidoc.core.utils.io.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
import de.escidoc.core.om.service.interfaces.ContextHandlerInterface;
import org.springframework.stereotype.Service;

/**
 * @author Marko Voss (marko.voss@fiz-karlsruhe.de)
 */
@Service
@Guarded(applyFieldConstraintsToConstructors = false, applyFieldConstraintsToSetters = false,
    assertParametersNotNull = false, checkInvariants = false, inspectInterfaces = true)
public class ContextRestServiceImpl implements ContextRestService {

    private final static Logger LOG = LoggerFactory.getLogger(ContextRestServiceImpl.class);

    @Autowired
    @Qualifier("service.ContextHandler")
    private ContextHandlerInterface contextHandler;

    @Autowired
    private FedoraServiceClient fedoraServiceClient;

    @Autowired
    private ServiceUtility serviceUtility;

    @Autowired
    private ObjectFactoryProvider factoryProvider;

    /**
     *
     */
    protected ContextRestServiceImpl() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBElement<ContextTypeTO> create(final ContextTypeTO contextTO)
        throws MissingMethodParameterException, ContextNameNotUniqueException, AuthenticationException,
        AuthorizationException, SystemException, ContentModelNotFoundException, ReadonlyElementViolationException,
        MissingAttributeValueException, MissingElementValueException, ReadonlyAttributeViolationException,
        InvalidContentException, OrganizationalUnitNotFoundException, InvalidStatusException, XmlCorruptedException,
        XmlSchemaValidationException {

        return factoryProvider.getContextFactory().createContext(
            serviceUtility.fromXML(ContextTypeTO.class, this.contextHandler.create(serviceUtility.toXML(contextTO))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBElement<ContextTypeTO> retrieve(final String id)
        throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        return factoryProvider.getContextFactory().createContext(
            serviceUtility.fromXML(ContextTypeTO.class, this.contextHandler.retrieve(id)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBElement<ContextTypeTO> update(final String id, final ContextTypeTO contextTO)
        throws ContextNotFoundException, MissingMethodParameterException, InvalidContentException,
        InvalidStatusException, AuthenticationException, AuthorizationException, ReadonlyElementViolationException,
        ReadonlyAttributeViolationException, OptimisticLockingException, ContextNameNotUniqueException,
        InvalidXmlException, MissingElementValueException, SystemException {

        return factoryProvider.getContextFactory().createContext(serviceUtility
            .fromXML(ContextTypeTO.class, this.contextHandler.update(id, serviceUtility.toXML(contextTO))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final String id)
        throws ContextNotFoundException, ContextNotEmptyException, MissingMethodParameterException,
        InvalidStatusException, AuthenticationException, AuthorizationException, SystemException {
        this.contextHandler.delete(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBElement<ContextPropertiesTypeTO> retrieveProperties(final String id)
        throws ContextNotFoundException, SystemException {
        return factoryProvider.getContextFactory().createProperties(
            serviceUtility.fromXML(ContextPropertiesTypeTO.class, this.contextHandler.retrieveProperties(id)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream retrieveResource(final String id, final String resourceName)
        throws OperationNotFoundException, ContextNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {

        // simulate Map for compatibility (see retrieveMembers for retrieval of members)
        EscidocBinaryContent content =
            this.contextHandler.retrieveResource(id, resourceName, new HashMap<String, String[]>());
        Stream stream = new Stream();
        try {
            IOUtils.copy(content.getContent(), stream);
        } catch (IOException e) {
            String msg = "Failed to copy stream";
            LOG.error(msg, e);
            throw new SystemException(msg, e);
        }
        return stream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBElement<ContextResourcesTypeTO> retrieveResources(final String id)
        throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        return factoryProvider.getContextFactory().createResources(
            serviceUtility.fromXML(ContextResourcesTypeTO.class, this.contextHandler.retrieveResources(id)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBElement<? extends ResponseTypeTO> retrieveMembers(final String id,
        final SruSearchRequestParametersBean queryParam, final String roleId, final String userId,
        final String omitHighlighting)
        throws ContextNotFoundException, MissingMethodParameterException, SystemException {

        Map<String, String[]> map = serviceUtility.handleSruRequest(queryParam, roleId, userId, omitHighlighting);

        return (JAXBElement<? extends ResponseTypeTO>) serviceUtility.fromXML(
            this.contextHandler.retrieveMembers(id, map));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBElement<AdminDescriptorTypeTO> retrieveAdminDescriptor(final String id, final String name)
        throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, AdminDescriptorNotFoundException {

        return factoryProvider.getContextFactory().createAdminDescriptor(
            serviceUtility.fromXML(AdminDescriptorTypeTO.class, this.contextHandler.retrieveAdminDescriptor(id, name)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBElement<AdminDescriptorsTypeTO> retrieveAdminDescriptors(final String id)
        throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException {

        return factoryProvider.getContextFactory().createAdminDescriptors(
            serviceUtility.fromXML(AdminDescriptorsTypeTO.class, this.contextHandler.retrieveAdminDescriptors(id)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBElement<AdminDescriptorTypeTO> updateAdminDescriptor(final String id,
        final AdminDescriptorTypeTO adminDescriptorTO)
        throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, AdminDescriptorNotFoundException,
        InvalidXmlException {

        return factoryProvider.getContextFactory().createAdminDescriptor(serviceUtility
            .fromXML(AdminDescriptorTypeTO.class,
                this.contextHandler.updateAdminDescriptor(id, serviceUtility.toXML(adminDescriptorTO))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBElement<ResultTypeTO> open(final String id, final StatusTaskParamTO statusTaskParamTO)
        throws ContextNotFoundException, MissingMethodParameterException, InvalidStatusException,
        AuthenticationException, AuthorizationException, OptimisticLockingException, InvalidXmlException,
        SystemException, LockingException, StreamNotFoundException {

        return factoryProvider.getResultFactory().createResult(serviceUtility
            .fromXML(ResultTypeTO.class, this.contextHandler.open(id, serviceUtility.toXML(statusTaskParamTO))));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBElement<ResultTypeTO> close(final String id, final StatusTaskParamTO statusTaskParamTO)
        throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, InvalidXmlException,
        InvalidStatusException, LockingException, StreamNotFoundException {

        return factoryProvider.getResultFactory().createResult(serviceUtility
            .fromXML(ResultTypeTO.class, this.contextHandler.close(id, serviceUtility.toXML(statusTaskParamTO))));
    }
}