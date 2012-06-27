/**
 * 
 */
package org.escidoc.core.context.internal;

import java.util.Map;

import javax.xml.bind.JAXBElement;

import net.sf.oval.guard.Guarded;
import org.escidoc.core.context.ContextsRestService;
import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.ResponseTypeTO;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.om.service.interfaces.ContextHandlerInterface;
import org.springframework.stereotype.Service;

/**
 * @author Marko Voss
 * 
 */
@Service
@Guarded(applyFieldConstraintsToConstructors = false, applyFieldConstraintsToSetters = false,
    assertParametersNotNull = false, checkInvariants = false, inspectInterfaces = true)
public class ContextsRestServiceImpl implements ContextsRestService {

    @Autowired
    @Qualifier("service.ContextHandler")
    private ContextHandlerInterface contextHandler;

    @Autowired
    private ServiceUtility serviceUtility;

    /**
     * 
     */
    protected ContextsRestServiceImpl() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JAXBElement<? extends ResponseTypeTO> retrieveContexts(
        final SruSearchRequestParametersBean parameters, 
        final String roleId, 
        final String userId,
        final String omitHighlighting) throws MissingMethodParameterException, SystemException {

        Map<String, String[]> map = serviceUtility.handleSruRequest(parameters, roleId, userId, omitHighlighting);

        return (JAXBElement<? extends ResponseTypeTO>) serviceUtility.fromXML(
                this.contextHandler.retrieveContexts(map));
    }
}