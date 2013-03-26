package de.escidoc.core.om.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.om.service.interfaces.RepresentationHandlerInterface;

@Service("service.RepresentationHandler")
public class RepresentationHandler implements RepresentationHandlerInterface {

    @Autowired
    @Qualifier("business.RepresentationHandler")
    private de.escidoc.core.om.business.interfaces.RepresentationHandlerInterface handler;

    @Override
    public String getRepresentation(String id) throws EscidocException {
        return handler.getRepresentation(id);
    }

    @Override
    public String updateRepresentation(String xml) throws EscidocException {
        return handler.updateRepresentation(xml);
    }

    @Override
    public String searchRepresentations(Map<String, String[]> params) throws EscidocException {
        return handler.searchRepresentations(params);
    }
}
