package de.escidoc.core.om.service;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.ItemProperties;
import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.model.mets.SCAPEMarshaller;

@Service("service.ScapeIngestHandler")
public class ScapeIngestHandler implements de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScapeIngestHandler.class);

    private static final DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandler itemHandler;

    @Override
    public String ingestIntellectualEntity(String xml) throws EscidocException {
        try {
            IntellectualEntity entity =
                SCAPEMarshaller.getInstance().deserialize(IntellectualEntity.class,
                    new ByteArrayInputStream(xml.getBytes()));
            Document doc = domFactory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
            Item item = new Item();
            ItemProperties itemProps = new ItemProperties();
            MetadataRecords mds = new MetadataRecords();
            MetadataRecord dc = new MetadataRecord("dublin-core");
            dc.setMdType("DC");
            Element dcElement = doc.getElementById(entity.getDescriptive().getId());
            dc.setContent(dcElement);
            mds.add(dc);
            item.setProperties(itemProps);
            item.setMetadataRecords(mds);
            Marshaller<Item> itemMarshaller = MarshallerFactory.getInstance().getMarshaller(Item.class);
            itemHandler.create(itemMarshaller.marshalDocument(item));
            return entity.getIdentifier().getValue() + "\n";
        }
        catch (Exception e) {
            throw new ScapeException(e.getLocalizedMessage(), e);
        }
    }
}
