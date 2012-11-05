package de.escidoc.core.om.business.scape;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.cmm.service.interfaces.ContentModelHandlerInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.business.fedora.resources.cmm.ContentModel;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.common.util.IOUtils;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.business.interfaces.IntellectualEntityHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContextHandlerInterface;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;
import de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface;
import de.escidoc.core.resources.XLinkType;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.common.structmap.ItemMemberRef;
import de.escidoc.core.resources.common.structmap.MemberRef;
import de.escidoc.core.resources.common.structmap.StructMap;
import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.container.ContainerProperties;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.ContextProperties;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.ItemProperties;
import de.escidoc.core.resources.om.item.StorageType;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.ComponentContent;
import de.escidoc.core.resources.om.item.component.ComponentProperties;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.escidoc.core.resources.oum.OrganizationalUnitProperties;
import eu.scapeproject.model.File;
import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.model.Representation;
import eu.scapeproject.model.metadata.dc.DCMetadata;
import eu.scapeproject.model.mets.SCAPEMarshaller;

@Service("business.IntellectualEntityHandler")
public class IntellectualEntityHandler implements IntellectualEntityHandlerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(IntellectualEntityHandler.class);

    private static String createTaskParam(final String lastModificationDate) {
        return "<param last-modification-date=\"" + lastModificationDate + "\"/>";
    }

    private final Marshaller<OrganizationalUnit> ouMarshaller;

    private final Marshaller<Component> componentMarshaller;

    private final Marshaller<Container> containerMarshaller;

    private final Marshaller<Item> itemMarshaller;

    private final XPathFactory xfac = XPathFactory.newInstance();

    private static final DateTimeFormatter dateformatter = ISODateTimeFormat.dateTime();

    private Context scapeContext;

    private OrganizationalUnit scapeOU;

    private String scapeContentModelId;

    @Autowired
    @Qualifier("service.OrganizationalUnitHandler")
    private OrganizationalUnitHandlerInterface ouHandler;

    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandlerInterface itemHandler;

    @Autowired
    @Qualifier("service.ContextHandler")
    private ContextHandlerInterface contextHandler;;

    @Autowired
    @Qualifier("service.ContentModelHandler")
    private ContentModelHandlerInterface contentModelHandler;

    @Autowired
    @Qualifier("service.ContainerHandler")
    private ContainerHandlerInterface containerHandler;

    // TODO SCAPE:didn't get the semantics yet, but this has to be externalized
    private static final String SCAPE_OU_ELEMENT =
        "<mdou:organizational-unit xmlns:mdou=\"http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:eterms=\"http://purl.org/escidoc/metadata/terms/0.1/\"><dc:title>SCAPE Organizational Unit</dc:title></mdou:organizational-unit>";

    private static final String SCAPE_CM_ELEMENT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><escidocContentModel:content-model xmlns:escidocContentModel=\"http://www.escidoc.de/schemas/contentmodel/0.1\" xmlns:prop=\"http://escidoc.de/core/01/properties/\"><escidocContentModel:properties><prop:name>scape-contentmodel</prop:name><prop:description>for test purpose</prop:description></escidocContentModel:properties></escidocContentModel:content-model>";

    private static DateTime getLastModificationDateTime(final String xml) throws Exception {
        int start = xml.indexOf("last-modification-date=\"") + 24;
        int end = xml.indexOf("\"", start);
        return dateformatter.parseDateTime(xml.substring(start, end));
    }

    public IntellectualEntityHandler() throws InternalClientException {
        ouMarshaller = MarshallerFactory.getInstance().getMarshaller(OrganizationalUnit.class);
        componentMarshaller = MarshallerFactory.getInstance().getMarshaller(Component.class);
        containerMarshaller = MarshallerFactory.getInstance().getMarshaller(Container.class);
        itemMarshaller = MarshallerFactory.getInstance().getMarshaller(Item.class);
    }

    private void checkScapeContentModel() throws EscidocException {
        try {
            if (scapeContentModelId == null) {
                Marshaller<ContentModel> contentModelMarshaller =
                    MarshallerFactory.getInstance().getMarshaller(ContentModel.class);
                Map<String, String[]> filter = new HashMap<String, String[]>();
                filter.put("name", new String[] { "scape-contentmodel" });
                String xml = contentModelHandler.retrieveContentModels(filter);
                XPath xp = xfac.newXPath();
                XPathExpression xpe =
                    xp
                        .compile("//*[local-name()='numberOfRecords' and namespace-uri()='http://www.loc.gov/zing/srw/']");
                int numResult = Integer.parseInt(xpe.evaluate(new InputSource(new StringReader(xml))));
                if (numResult == 0) {
                    String cmXML = contentModelHandler.create(SCAPE_CM_ELEMENT);
                    int pos = cmXML.indexOf("xlink:href=\"/cmm/content-model/");
                    int end = cmXML.indexOf("\"", pos + 31);
                    scapeContentModelId = cmXML.substring(pos + 31, end);
                }
                else {
                    int posOuStart = xml.indexOf("<escidocContentModel:content-model ");
                    int posOuEnd = xml.indexOf("</escidocContentModel:content-model>");
                    String cmXML = xml.substring(posOuStart, posOuEnd + 35);
                    int pos = cmXML.indexOf("xlink:href=\"/cmm/content-model/");
                    int end = cmXML.indexOf("\"", pos + 31);
                    scapeContentModelId = cmXML.substring(pos + 31, end);
                }
            }
        }
        catch (Exception e) {
            throw new ScapeException(e.getMessage(), e);
        }
    }

    private void checkScapeContext() throws EscidocException {
        checkScapeOU();
        try {
            if (scapeContext == null) {
                Marshaller<Context> contextMarshaller = MarshallerFactory.getInstance().getMarshaller(Context.class);
                Map<String, String[]> filter = new HashMap<String, String[]>();
                filter.put("name", new String[] { "scape-context" });
                String xml = contextHandler.retrieveContexts(filter);
                XPath xp = xfac.newXPath();
                XPathExpression xpe =
                    xp
                        .compile("//*[local-name()='numberOfRecords' and namespace-uri()='http://www.loc.gov/zing/srw/']");
                int numResult = Integer.parseInt(xpe.evaluate(new InputSource(new StringReader(xml))));
                if (numResult == 0) {
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
                    String contextXml = contextHandler.create(contextMarshaller.marshalDocument(scapeContext));
                    scapeContext = contextMarshaller.unmarshalDocument(contextXml);
                    openContext();
                }
                else {
                    int posOuStart = xml.indexOf("<context:context ");
                    int posOuEnd = xml.indexOf("</context:context>");
                    String ouXML = xml.substring(posOuStart, posOuEnd + 18);
                    scapeContext = contextMarshaller.unmarshalDocument(ouXML);
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
                Map<String, String[]> filters = new HashMap<String, String[]>(1);
                filters.put("name", new String[] { "scape-default-ou" });
                String result = ouHandler.retrieveOrganizationalUnits(filters);
                XPath xp = xfac.newXPath();
                XPathExpression xpe =
                    xp
                        .compile("//*[local-name()='numberOfRecords' and namespace-uri()='http://www.loc.gov/zing/srw/']");
                int numResult = Integer.parseInt(xpe.evaluate(new InputSource(new StringReader(result))));
                if (numResult == 0) {
                    // create new SCAPE OU
                    OrganizationalUnitProperties props = new OrganizationalUnitProperties();
                    props.setCreationDate(new DateTime());
                    props.setDescription("SCAPE organizational unit");
                    props.setName("scape-default-ou");
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
                    openOU();
                }
                else {
                    int posOuStart = result.indexOf("<organizational-unit:organizational-unit ");
                    int posOuEnd = result.indexOf("</organizational-unit:organizational-unit>");
                    String ouXML = result.substring(posOuStart, posOuEnd + 42);
                    scapeOU = ouMarshaller.unmarshalDocument(ouXML);
                }
            }
        }
        catch (Exception e) {
            throw new ScapeException(e.getMessage(), e);
        }
    }

    private Container createContainer(IntellectualEntity entity, Document entityDoc) throws Exception {
        Container container = new Container();
        ContainerProperties props = new ContainerProperties();
        props.setContentModel(new ContentModelRef(scapeContentModelId));
        props.setContext(new ContextRef(scapeContext.getObjid()));
        props.setName(((DCMetadata) entity.getDescriptive()).getTitle().get(0));
        props.setDescription(((DCMetadata) entity.getDescriptive()).getDescription().get(0));
        container.setProperties(props);
        container.setMetadataRecords(createEntityMetadataRecords(entity, entityDoc));
        container.setLastModificationDate(new DateTime());
        return container;
    }

    private MetadataRecords createEntityMetadataRecords(final IntellectualEntity entity, final Document entityDoc)
        throws Exception {
        final MetadataRecords mds = new MetadataRecords();
        mds.setLastModificationDate(new DateTime());
        MetadataRecord dc = new MetadataRecord("escidoc");
        dc.setMdType("DC");
        NodeList nodes = entityDoc.getElementsByTagName("mets:dmdSec");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (entity.getDescriptive().getId().equals(n.getAttributes().getNamedItem("ID").getNodeValue())) {
                Document dcDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
                Element dcElement = dcDoc.createElement("dublin-core");
                dcElement.setAttribute("xmlns:dc", "http://purl.org/dc/elements/1.1/");
                dcElement.setAttribute("xmlns:premis", "http://www.loc.gov/standards/premis");
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
        return mds;
    }

    private String createItem(Representation r, Document doc) throws Exception {
        Item i = new Item();
        ItemProperties props = new ItemProperties();
        props.setContentModel(new ContentModelRef(scapeContentModelId));
        props.setContext(new ContextRef(scapeContext.getObjid()));
        i.setMetadataRecords(createRepresentationMetadataRecords(r));
        i.setProperties(props);
        String itemXml = itemHandler.create(itemMarshaller.marshalDocument(i));
        String itemId = getItemId(itemXml);
        DateTime lastModDate = getLastModificationDateTime(itemXml);
        // iterate over all the files and create the according components
        for (File f : r.getFiles()) {
            Component c = createComponent(f, itemId, lastModDate, doc);
            System.out.println(c.getLastModificationDate());
            itemHandler.createComponent(itemId, componentMarshaller.marshalDocument(c));
            // refetch the item so we get the current last mod date from escidoc
            itemXml = itemHandler.retrieve(itemId);
            System.out.println(itemXml);
            lastModDate = getLastModificationDateTime(itemXml);
        }
        return itemId;
    }

    private Component createComponent(File f, String itemId, DateTime itemModeDate, Document doc) throws Exception {
        Component c = new Component();
        ComponentProperties props = new ComponentProperties();
        props.setCreationDate(new DateTime());
        props.setVisibility("visible");
        props.setValidStatus("valid");
        props.setDescription("SCAPE File");
        props.setPid(f.getIdentifier().getValue());
        props.setContentCategory("Files");
        ComponentContent data = new ComponentContent();
        data.setStorageType(StorageType.INTERNAL_MANAGED);
        data.setXLinkHref(f.getUri().toASCIIString());
        c.setProperties(props);
        c.setContent(data);
        c.setLastModificationDate(itemModeDate);
        return c;
    }

    private String getComponentId(String componentXml, String itemId) {
        String hrefPrefix = "xlink:href=\"/ir/item/" + itemId + "/component/";
        final int start = componentXml.indexOf(hrefPrefix) + hrefPrefix.length();
        final int stop = componentXml.indexOf("\"", start);
        return new String(componentXml.substring(start, stop));
    }

    private MetadataRecords createRepresentationMetadataRecords(Representation r) throws Exception {
        MetadataRecords mds = new MetadataRecords();
        MetadataRecord rec = new MetadataRecord("escidoc");
        rec.setMdType("DC");
        rec.setSchema("http://purl.org/dc/elements/1.1/");
        String dc =
            "<dublin-core xmlns:dc=\"http://purl.org/dc/elements/1.1/\"><dc:title>" + r.getTitle()
                + "</dc:title></dublin-core>";
        Document doc =
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(dc.getBytes()));
        rec.setContent(doc.getDocumentElement());
        mds.add(rec);
        return mds;
    }

    private String getContainerId(String containerXml) {
        final int start = containerXml.indexOf("xlink:href=\"/ir/container/") + 26;
        final int stop = containerXml.indexOf("\"", start);
        return new String(containerXml.substring(start, stop));
    }

    @Override
    public String getIntellectuakEntitySet(List<String> ids) throws EscidocException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getIntellectualEntity(String id) throws EscidocException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getIntellectualEntityVersionSet(String id) throws EscidocException {
        // TODO Auto-generated method stub
        return null;
    }

    private String getItemId(String itemXml) {
        final int start = itemXml.indexOf("xlink:href=\"/ir/item/") + 21;
        final int end = itemXml.indexOf("\"", start);
        return new String(itemXml.substring(start, end));
    }

    @Override
    public String getLifeCyclestatus(String id) throws EscidocException {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.escidoc.core.om.service.IntellectualEntityHandlerInterface# ingestIntellectualEntity(java.lang.String)
     */
    @Override
    public String ingestIntellectualEntity(String xml) throws EscidocException {
        try {
            checkScapeContext();
            checkScapeContentModel();

            // deserialize the entity and create a org.w3c.Document for reuse by various later calls
            IntellectualEntity entity =
                SCAPEMarshaller.getInstance().deserialize(IntellectualEntity.class,
                    new ByteArrayInputStream(xml.getBytes()));
            final Document doc =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new ByteArrayInputStream(xml.getBytes()));
            StructMap map = new StructMap();

            // add the representations as single items to the container
            for (Representation r : entity.getRepresentations()) {
                String itemId = this.createItem(r, doc);
                map.add(new ItemMemberRef("/ir/item/" + itemId, r.getTitle(), XLinkType.simple));
            }

            // create the entities container and add the various representations
            Container entityContainer = this.createContainer(entity, doc);
            entityContainer.setStructMap(map);
            String containerXml = containerHandler.create(containerMarshaller.marshalDocument(entityContainer));
            String containerId = getContainerId(containerXml);
            return containerId;
        }
        catch (Exception e) {
            throw new ScapeException(e.getMessage(), e);
        }
    }

    private void openContext() throws ScapeException {
        try {
            String xml =
                contextHandler.open(scapeContext.getObjid(), createTaskParam(scapeContext
                    .getLastModificationDate().toDateTime(DateTimeZone.UTC).toString(Constants.TIMESTAMP_FORMAT)));
            XPath xp = xfac.newXPath();
            XPathExpression xpe =
                xp
                    .compile("/*[local-name()='result' and namespace-uri()='http://www.escidoc.de/schemas/result/0.1']/@last-modification-date");
            String lastModDate = xpe.evaluate(new InputSource(new StringReader(xml)));
            scapeContext.setLastModificationDate(dateformatter.parseDateTime(lastModDate));
        }
        catch (Exception e) {
            throw new ScapeException(e.getMessage(), e);
        }
    }

    private void openOU() throws ScapeException {
        try {
            String xml =
                ouHandler.open(scapeOU.getObjid(), createTaskParam(scapeOU.getLastModificationDate().toDateTime(
                    DateTimeZone.UTC).toString(Constants.TIMESTAMP_FORMAT)));
            XPath xp = xfac.newXPath();
            XPathExpression xpe =
                xp
                    .compile("/*[local-name()='result' and namespace-uri()='http://www.escidoc.de/schemas/result/0.1']/@last-modification-date");
            String lastModDate = xpe.evaluate(new InputSource(new StringReader(xml)));
            scapeOU.setLastModificationDate(dateformatter.parseDateTime(lastModDate));
        }
        catch (Exception e) {
            LOG.error("Error while opening ou", e);
            throw new ScapeException(e.getMessage(), e);
        }
    }

    @Override
    public String updateIntellectualEntity(String xml) throws EscidocException {
        // TODO Auto-generated method stub
        return null;
    }

}