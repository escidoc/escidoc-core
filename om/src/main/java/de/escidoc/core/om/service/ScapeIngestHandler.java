package de.escidoc.core.om.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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
import org.xml.sax.SAXException;

import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.common.util.IOUtils;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.ContextProperties;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;
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

    // TODO SCAPE:didn't get the semantics yet, but this has to be externalized
    private static final String SCAPE_OU_ELEMENT =
        "<mdou:organizational-unit xmlns:mdou=\"http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:eterms=\"http://purl.org/escidoc/metadata/terms/0.1/\"><dc:title>SCAPE Organizational Unit</dc:title></mdou:organizational-unit>";

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
                    OrganizationalUnitRefs ouRefs = new OrganizationalUnitRefs();
                    ouRefs.add(new OrganizationalUnitRef(scapeOU.getObjid()));
                    props.setOrganizationalUnitRefs(ouRefs);
                    scapeContext = new Context();
                    scapeContext.setLastModificationDate(new DateTime());
                    scapeContext.setProperties(props);
                    String xml = contextHandler.create(contextMarshaller.marshalDocument(scapeContext));
                    scapeContext = contextMarshaller.unmarshalDocument(xml);
                    contextHandler.open(scapeContext.getObjid(), createTaskParam(getLastModificationDate(xml, ResourceType.CONTEXT)));
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
                    props.setPublicStatus(PublicStatus.OPENED);
                    MetadataRecords mdRecords = new MetadataRecords();
                    MetadataRecord md_1 = new MetadataRecord("scape");
                    md_1.setName("escidoc");
                    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
                    Document doc =
                        domFactory.newDocumentBuilder().parse(new ByteArrayInputStream(SCAPE_OU_ELEMENT.getBytes()));
                    md_1.setContent(doc.getDocumentElement());
                    mdRecords.add(md_1);
                    scapeOU = new OrganizationalUnit();
                    scapeOU.setProperties(props);
                    scapeOU.setLastModificationDate(new DateTime());
                    scapeOU.setMetadataRecords(mdRecords);
                    String xml = ouHandler.create(ouMarshaller.marshalDocument(scapeOU));
                    scapeOU = ouMarshaller.unmarshalDocument(xml);
                    ouHandler.open(scapeOU.getObjid(), createTaskParam(getLastModificationDate(xml, ResourceType.OU)));
                }
            }
        }
        catch (Exception e) {
            throw new ScapeException(e.getMessage(), e);
        }
    }

    private static String createTaskParam(final String lastModificationDate) {
        return "<param last-modification-date=\"" + lastModificationDate + "\"/>";
    }

    private static String getLastModificationDate(final String xml, final ResourceType type)
        throws XPathExpressionException, IOException, ParserConfigurationException, SAXException {
        String result = null;

        if (xml != null) {
            ByteArrayInputStream input = null;
            try {
                input = new ByteArrayInputStream(xml.getBytes(XmlUtility.CHARACTER_ENCODING));
                final DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                final Document xmlDom = db.parse(input);
                final XPath xpath = XPathFactory.newInstance().newXPath();
                result = xpath.evaluate('/' + type.getLabel() + "/@last-modification-date", xmlDom);
            }
            finally {
                IOUtils.closeStream(input);
            }
        }
        return result;
    }

}
