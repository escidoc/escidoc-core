package de.escidoc.core.om.service;

import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.om.service.interfaces.SRUHandlerInterface;

@Service("service.scape.SRUHandler")
public class SRUHandler implements SRUHandlerInterface {

    @Override
    public String searchIntellectualEntity(String query) throws EscidocException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String searchRepresentation(String query) throws EscidocException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String searchFile(String query) throws EscidocException {
        // TODO Auto-generated method stub
        return null;
    }

}
