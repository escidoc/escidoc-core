package de.escidoc.core.om.business.scape;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.om.business.interfaces.MetadataHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;

@Service("business.MetadataHandler")
public class MetadataHandler implements MetadataHandlerInterface {

    @Autowired
    @Qualifier("service.ContainerHandler")
    private ContainerHandlerInterface containerHandler;

    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandlerInterface itemHandler;

    @Override
    public String getMetadata(String id, String versionId) throws EscidocException {
        return "<blah/>";

    }
}
