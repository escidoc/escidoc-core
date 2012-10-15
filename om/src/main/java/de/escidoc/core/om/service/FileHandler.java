package de.escidoc.core.om.service;

import org.springframework.stereotype.Service;

import de.escidoc.core.om.service.interfaces.FileHandlerInterface;

@Service("service.scape.FileHandler")
public class FileHandler implements FileHandlerInterface {
    @Override
    public String getFile(String id) {
        return null;
    };
}
