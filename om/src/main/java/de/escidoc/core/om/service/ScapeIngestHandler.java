package de.escidoc.core.om.service;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.hibernate.type.MetaType;
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
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.ContextProperties;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.ItemProperties;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.escidoc.core.resources.oum.OrganizationalUnitProperties;
import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.model.mets.SCAPEMarshaller;

@Service("service.ScapeIngestHandler")
public class ScapeIngestHandler implements de.escidoc.core.om.service.interfaces.ScapeIngestHandlerInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScapeIngestHandler.class);

    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandler itemHandler;

    @Autowired
    @Qualifier("service.ContextHandler")
    private ContextHandler contextHandler;

    @Autowired
    @Qualifier("service.OrganizationalUnitHandler")
    private OrganizationalUnitHandlerInterface ouHandler;

    private Context scapeContext;

    private OrganizationalUnit scapeOU;

    @Override
    public String ingestIntellectualEntity(String xml) throws EscidocException {
        try {
            checkScapeContext();

            IntellectualEntity entity =
                SCAPEMarshaller.getInstance().deserialize(IntellectualEntity.class,
                    new ByteArrayInputStream(xml.getBytes()));
            // parse the METS xml dom
            // since we need metadata as org.w3c.Element
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            Document doc = domFactory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));

            // create a new escidoc item using the ijc library
            Item item = new Item();

            // prepare the dublin core metadata
            MetadataRecords mds = new MetadataRecords();
            mds.setLastModificationDate(new DateTime());
            MetadataRecord dc = new MetadataRecord("dublin-core");
            dc.setMdType("DC");
            NodeList nodes = doc.getElementsByTagName("mets:dmdSec");
            for (int i = 0; i < nodes.getLength(); i++) {
                Node n = nodes.item(i);
                if (entity.getDescriptive().getId().equals(n.getAttributes().getNamedItem("ID").getNodeValue())) {
                    Document dcDoc = domFactory.newDocumentBuilder().newDocument();
                    Element dcElement = dcDoc.createElement("dublin-core");
                    dcElement.setAttribute("xmlns:dc", "http://purl.org/dc/elements/1.1/");
                    NodeList origDCElements =
                        (NodeList) n
                            .getChildNodes().item(1).getChildNodes().item(1).getChildNodes().item(1).getChildNodes();
                    for (int j = 1; j < origDCElements.getLength(); j++) {
                        dcElement.appendChild(dcDoc.adoptNode(origDCElements.item(j).cloneNode(true)));
                    }
                    dc.setContent(dcElement);
                    mds.add(dc);
                }
            }

            // set the properties of the item using the intellectual entities data
            ItemProperties itemProps = new ItemProperties();
            item.setProperties(itemProps);
            item.setMetadataRecords(mds);
            item.setLastModificationDate(new DateTime());

            // and use ijc's marshaller to create escidoc xml
            Marshaller<Item> itemMarshaller = MarshallerFactory.getInstance().getMarshaller(Item.class);

            // and finally use escidoc's item handler to save the generated xml data
            itemHandler.create(itemMarshaller.marshalDocument(item));
            return itemMarshaller.marshalDocument(item) + "\n";
            // return entity.getIdentifier().getValue() + "\n";
        }
        catch (Exception e) {
            throw new ScapeException(e.getLocalizedMessage(), e);
        }
    }

    private void checkScapeContext() throws EscidocException {
        checkScapeOU();
        try {
            if (scapeContext == null) {
                Marshaller<Context> contextMarshaller = MarshallerFactory.getInstance().getMarshaller(Context.class);
                try {
                    String xml = contextHandler.retrieve("scape-context");
                    scapeContext = contextMarshaller.unmarshalDocument(xml);
                }
                catch (ContextNotFoundException ce) {
                    ContextProperties props = new ContextProperties();
                    props.setCreationDate(new DateTime());
                    props.setDescription("a context for SCAPE items");
                    props.setPublicStatus(PublicStatus.RELEASED);
                    props.setName("SCAPE context");
                    props.setType("scape");
                    scapeContext = new Context();
                    scapeContext.setLastModificationDate(new DateTime());
                    scapeContext.setProperties(props);
                    contextHandler.create(contextMarshaller.marshalDocument(scapeContext));
                }
            }
        }
        catch (Exception e) {
            throw new ScapeException(e.getMessage(), e);
        }
    }

    private void checkScapeOU() throws EscidocException {
        try {
            if (scapeOU == null) {
                Marshaller<OrganizationalUnit> ouMarshaller =
                    MarshallerFactory.getInstance().getMarshaller(OrganizationalUnit.class);
                try {
                    String xml = ouHandler.retrieve("scape-ou");
                    scapeOU = ouMarshaller.unmarshalDocument(xml);
                }
                catch (OrganizationalUnitNotFoundException e) {
                    OrganizationalUnitProperties props = new OrganizationalUnitProperties();
                    props.setCreationDate(new DateTime());
                    props.setDescription("SCAPE organizational unit");
                    props.setName("scape-ou");
                    props.setPublicStatus(PublicStatus.RELEASED);
                    MetadataRecords mdRecords = new MetadataRecords();
                    MetadataRecord md_1=new MetadataRecord("scape");
                    md_1.setMdType("VOID");
                    mdRecords.add(md_1);
                    scapeOU = new OrganizationalUnit();
                    scapeOU.setProperties(props);
                    scapeOU.setLastModificationDate(new DateTime());
                    scapeOU.setMetadataRecords(mdRecords);
                    ouHandler.create(ouMarshaller.marshalDocument(scapeOU));
                }
            }
        }
        catch (Exception e) {
            throw new ScapeException(e.getMessage(), e);
        }
    }
}
