/**
 * 
 */
package de.escidoc.core.context.internal;

import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.ResponseTypeTO;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.context.ContextsRestService;
import de.escidoc.core.om.service.interfaces.ContextHandlerInterface;

/**
 * @author Marko Voss
 * 
 */
@Transactional
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

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.context.ContextsRestService#retrieveContexts(org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean, java.util.String, java.util.String, java.util.String)
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