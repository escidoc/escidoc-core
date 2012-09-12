package de.escidoc.core.om.service;

import java.io.ByteArrayInputStream;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.om.business.fedora.item.FedoraItemHandler;
import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.model.mets.SCAPEMarshaller;

@Service("service.ScapeIngestHandler")
public class ScapeIngestHandler implements de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScapeIngestHandler.class);

    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandler itemHandler;

    @Override
    public String ingestIntellectualEntity(String xml) throws EscidocException {
        try {
            IntellectualEntity entity =
                SCAPEMarshaller.getInstance().deserialize(IntellectualEntity.class,
                    new ByteArrayInputStream(xml.getBytes()));
            return entity.getIdentifier().getValue() + "\n";
        }
        catch (JAXBException e) {
            throw new ScapeException(e.getLocalizedMessage(), e);
        }
    }
}
