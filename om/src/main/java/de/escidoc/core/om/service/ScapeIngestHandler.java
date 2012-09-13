package de.escidoc.core.om.service;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandler itemHandler;

    @Override
    public String ingestIntellectualEntity(String xml) throws EscidocException {
        try {
            IntellectualEntity entity =
                SCAPEMarshaller.getInstance().deserialize(IntellectualEntity.class,
                    new ByteArrayInputStream(xml.getBytes()));
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            Document doc = domFactory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
            Item item = new Item();
            ItemProperties itemProps = new ItemProperties();
            MetadataRecords mds = new MetadataRecords();
            MetadataRecord dc = new MetadataRecord("dublin-core");
            dc.setMdType("DC");
            NodeList nodes = doc.getElementsByTagName("mets:dmdSec");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (entity.getDescriptive().getId().equals(n.getAttributes().getNamedItem("ID").getNodeValue())) {
                    Element dcElement = (Element) n;
                    dc.setContent(dcElement);
                    mds.add(dc);
                }
            }
            item.setProperties(itemProps);
            item.setMetadataRecords(mds);
            item.setLastModificationDate(new DateTime());
            Marshaller<Item> itemMarshaller = MarshallerFactory.getInstance().getMarshaller(Item.class);
            return itemMarshaller.marshalDocument(item) + "\n";
            //			itemHandler.create(itemMarshaller.marshalDocument(item));
            //			return entity.getIdentifier().getValue() + "\n";
        }
        catch (Exception e) {
            throw new ScapeException(e.getLocalizedMessage(), e);
        }
    }
}
