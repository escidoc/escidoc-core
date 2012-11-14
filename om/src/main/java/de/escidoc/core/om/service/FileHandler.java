package de.escidoc.core.om.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.om.service.interfaces.FileHandlerInterface;

@Service("service.FileHandler")
public class FileHandler implements FileHandlerInterface {

    @Autowired
    @Qualifier("business.FileHandler")
    private de.escidoc.core.om.business.interfaces.FileHandlerInterface handler;

    @Override
    public String getFile(String id) throws EscidocException {
        return handler.getFile(id);
    };
}
