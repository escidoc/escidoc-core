package de.escidoc.core.om.service;

import org.hibernate.type.MetaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.om.service.interfaces.MetadataHandlerInterface;

@Service("service.MetadataHandler")
public class MetadataHandler implements MetadataHandlerInterface {

    @Autowired
    @Qualifier("business.MetadataHandler")
    private de.escidoc.core.om.business.interfaces.MetadataHandlerInterface handler;

    @Override
    public String getMetadata(String id, String mdName, String version) throws EscidocException {
        return handler.getMetadata(id, mdName, version);
    }
    
	@Override
	public String updateMetadata(String id, String xmlData) throws EscidocException {
		return handler.updateMetadata(id, xmlData);
	}

}
