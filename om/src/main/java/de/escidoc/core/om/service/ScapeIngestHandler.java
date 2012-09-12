package de.escidoc.core.om.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.om.business.fedora.item.FedoraItemHandler;

@Service("service.scapeIngestHandler")
public class ScapeIngestHandler implements de.escidoc.core.om.service.interfaces.ScapeIngestHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScapeIngestHandler.class);

    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandler itemHandler;

    @Override
    public String ingestIntellectualEntity(String xml) {
        LOGGER.debug("ingesting entity:\n" + xml + "\n");
        return null;
    }
}
