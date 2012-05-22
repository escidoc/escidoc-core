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
import org.escidoc.core.domain.context.AdminDescriptorTO;
import org.escidoc.core.domain.context.AdminDescriptorsTO;
import org.escidoc.core.domain.context.ContextPropertiesTO;
import org.escidoc.core.domain.context.ContextResourcesTO;
import org.escidoc.core.domain.context.ContextTO;
import org.escidoc.core.domain.result.ResultTO;
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

/**
 * @author Marko Voß
 * 
 */
@Guarded(applyFieldConstraintsToConstructors = false, applyFieldConstraintsToSetters = false, assertParametersNotNull = false, checkInvariants = false, inspectInterfaces = true)
public class ContextRestServiceImpl implements ContextRestService {

    private final static Logger LOG = LoggerFactory.getLogger(ContextRestServiceImpl.class);

    @Autowired
    @Qualifier("service.ContextHandler")
    private ContextHandlerInterface contextHandler;

    @Autowired
    private FedoraServiceClient fedoraServiceClient;

    @Autowired
    private ServiceUtility serviceUtility;

    /**
     * 
     */
    protected ContextRestServiceImpl() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextTO create(final ContextTO contextTO)
            throws MissingMethodParameterException, ContextNameNotUniqueException, AuthenticationException,
            AuthorizationException, SystemException, ContentModelNotFoundException, ReadonlyElementViolationException,
            MissingAttributeValueException, MissingElementValueException, ReadonlyAttributeViolationException,
            InvalidContentException, OrganizationalUnitNotFoundException, InvalidStatusException, XmlCorruptedException,
            XmlSchemaValidationException {

        return serviceUtility.fromXML(ContextTO.class, this.contextHandler.create(serviceUtility.toXML(contextTO)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextTO retrieve(final String id)
            throws ContextNotFoundException, MissingMethodParameterException,
            AuthenticationException, AuthorizationException, SystemException {
        return serviceUtility.fromXML(ContextTO.class, this.contextHandler.retrieve(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextTO update(final String id, final ContextTO contextTO)
            throws ContextNotFoundException, MissingMethodParameterException, InvalidContentException,
            InvalidStatusException, AuthenticationException, AuthorizationException, ReadonlyElementViolationException,
            ReadonlyAttributeViolationException, OptimisticLockingException, ContextNameNotUniqueException,
            InvalidXmlException, MissingElementValueException, SystemException {

        return serviceUtility.fromXML(ContextTO.class, this.contextHandler.update(id, serviceUtility.toXML(contextTO)));
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
    public ContextPropertiesTO retrieveProperties(final String id)
            throws ContextNotFoundException, SystemException {
        return serviceUtility.fromXML(ContextPropertiesTO.class, this.contextHandler.retrieveProperties(id));
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
        }
        catch (IOException e) {
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
    public ContextResourcesTO retrieveResources(final String id)
            throws ContextNotFoundException, MissingMethodParameterException,
        AuthenticationException, AuthorizationException, SystemException {
        
        return serviceUtility.fromXML(ContextResourcesTO.class, this.contextHandler.retrieveResources(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBElement<? extends ResponseTypeTO> retrieveMembers(final String id,
                                                                 final SruSearchRequestParametersBean queryParam,
                                                                 final String roleId, final String userId,
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
    public AdminDescriptorTO retrieveAdminDescriptor(final String id, final String name)
        throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, AdminDescriptorNotFoundException {

        return serviceUtility.fromXML(AdminDescriptorTO.class,
                this.contextHandler.retrieveAdminDescriptor(id, name));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdminDescriptorsTO retrieveAdminDescriptors(final String id)
            throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
            AuthorizationException, SystemException {

        return serviceUtility.fromXML(AdminDescriptorsTO.class, this.contextHandler.retrieveAdminDescriptors(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AdminDescriptorTO updateAdminDescriptor(final String id,
                                                   final AdminDescriptorTO adminDescriptorTO)
        throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, OptimisticLockingException, AdminDescriptorNotFoundException,
        InvalidXmlException {

        return serviceUtility.fromXML(AdminDescriptorTO.class,
                this.contextHandler.updateAdminDescriptor(id, serviceUtility.toXML(adminDescriptorTO)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultTO open(final String id, final StatusTaskParamTO statusTaskParamTO)
            throws ContextNotFoundException, MissingMethodParameterException, InvalidStatusException,
            AuthenticationException, AuthorizationException, OptimisticLockingException, InvalidXmlException,
            SystemException, LockingException, StreamNotFoundException {

        return serviceUtility.fromXML(ResultTO.class,
                this.contextHandler.open(id, serviceUtility.toXML(statusTaskParamTO)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResultTO close(final String id, final StatusTaskParamTO statusTaskParamTO)
            throws ContextNotFoundException, MissingMethodParameterException, AuthenticationException,
            AuthorizationException, SystemException, OptimisticLockingException, InvalidXmlException,
            InvalidStatusException, LockingException, StreamNotFoundException {

        return serviceUtility.fromXML(ResultTO.class,
                this.contextHandler.close(id, serviceUtility.toXML(statusTaskParamTO)));
    }
}