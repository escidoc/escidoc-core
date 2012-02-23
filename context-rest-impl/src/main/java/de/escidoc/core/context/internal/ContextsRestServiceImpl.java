/**
 * 
 */
package de.escidoc.core.context.internal;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.escidoc.core.domain.service.ServiceUtility;
import org.escidoc.core.domain.sru.RequestType;
import org.escidoc.core.domain.sru.ResponseType;
import org.escidoc.core.domain.sru.parameters.SruRequestTypeFactory;
import org.escidoc.core.domain.sru.parameters.SruSearchRequestParametersBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.system.SystemException;
import de.escidoc.core.common.util.service.KeyValuePair;
import de.escidoc.core.context.ContextsRestService;
import de.escidoc.core.om.service.interfaces.ContextHandlerInterface;

/**
 * @author Marko VoÃŸ
 * 
 */
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
    public JAXBElement<? extends ResponseType> retrieveContexts(
        final SruSearchRequestParametersBean parameters, 
        final String roleId, 
        final String userId,
        final String omitHighlighting) throws MissingMethodParameterException, SystemException {

        final List<KeyValuePair> additionalParams = SruRequestTypeFactory.getDefaultAdditionalParams(
                roleId, userId, omitHighlighting);
        final JAXBElement<? extends RequestType> requestTO =
            SruRequestTypeFactory.createRequestTO(parameters, additionalParams);

        return (JAXBElement<? extends ResponseType>) serviceUtility.fromXML(
                this.contextHandler.retrieveContexts(serviceUtility.toMap(requestTO)));
    }

}