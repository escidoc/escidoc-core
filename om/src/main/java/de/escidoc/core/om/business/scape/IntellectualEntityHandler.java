package de.escidoc.core.om.business.scape;

import info.lc.xmlns.premis_v2.PremisComplexType;
import info.lc.xmlns.premis_v2.RightsComplexType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.xerces.dom.ElementNSImpl;
import org.esidoc.core.utils.io.EscidocBinaryContent;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.purl.dc.elements._1.ElementContainer;
import org.purl.dc.elements._1.ObjectFactory;
import org.purl.dc.elements._1.SimpleLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.cmm.service.interfaces.ContentModelHandlerInterface;
import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.resources.cmm.ContentModel;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.common.util.service.UserContext;
import de.escidoc.core.om.business.interfaces.IntellectualEntityHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContextHandlerInterface;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;
import de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface;
import de.escidoc.core.resources.XLinkType;
import de.escidoc.core.resources.common.ContentStream;
import de.escidoc.core.resources.common.ContentStreams;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.common.structmap.ItemMemberRef;
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
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.escidoc.core.resources.oum.OrganizationalUnitProperties;
import de.escidoc.core.st.service.interfaces.StagingFileHandlerInterface;
import eu.scapeproject.model.BitStream;
import eu.scapeproject.model.File;
import eu.scapeproject.model.Identifier;
import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.model.IntellectualEntityCollection;
import eu.scapeproject.model.LifecycleState;
import eu.scapeproject.model.LifecycleState.State;
import eu.scapeproject.model.Representation;
import eu.scapeproject.model.IntellectualEntity.Builder;
import eu.scapeproject.util.ScapeMarshaller;

@Service("business.IntellectualEntityHandler")
public class IntellectualEntityHandler implements IntellectualEntityHandlerInterface {

    private static final Logger logger = LoggerFactory.getLogger(IntellectualEntityHandler.class);

    private static String createTaskParam(final String lastModificationDate) {
        return "<param last-modification-date=\"" + lastModificationDate + "\"/>";
    }

    private static String getVersionXml(MetadataRecord md) {
        StringBuilder versionXml = new StringBuilder("<versions>");
        NodeList nodes = md.getContent().getElementsByTagName("version");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            versionXml.append("<version date=\"" + n.getAttributes().getNamedItem("date").getNodeValue()
                + "\" number=\"" + n.getAttributes().getNamedItem("number").getNodeValue() + "\" />");
        }
        return versionXml.append("</versions>").toString();
    }

    private final Marshaller<OrganizationalUnit> ouMarshaller;

    private final Marshaller<Component> componentMarshaller;

    private final Marshaller<Container> containerMarshaller;

    private final Marshaller<Item> itemMarshaller;

    private final XPathFactory xfac = XPathFactory.newInstance();

    private static final DateTimeFormatter dateformatter = ISODateTimeFormat.dateTime();

    private final BlockingQueue<IngestItem> entitylist = new LinkedBlockingQueue<IngestItem>();

    private Context scapeContext;

    private OrganizationalUnit scapeOU;

    private String scapeContentModelId;

    private ScapeMarshaller marshaller;

    private boolean threadstarted = false;

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

    @Autowired
    @Qualifier("business.ScapePIDService")
    private ScapePIDService pidService;

    // TODO SCAPE:didn't get the semantics yet, but this has to be externalized
    private static final String SCAPE_OU_ELEMENT =
        "<mdou:organizational-unit xmlns:mdou=\"http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\" xmlns:eterms=\"http://purl.org/escidoc/metadata/terms/0.1/\"><dc:title>SCAPE Organizational Unit</dc:title></mdou:organizational-unit>";

    private static final String SCAPE_CM_ELEMENT =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><escidocContentModel:content-model xmlns:escidocContentModel=\"http://www.escidoc.de/schemas/contentmodel/0.1\" xmlns:prop=\"http://escidoc.de/core/01/properties/\"><escidocContentModel:properties><prop:name>scape-contentmodel</prop:name><prop:description>for test purpose</prop:description></escidocContentModel:properties></escidocContentModel:content-model>";

    public IntellectualEntityHandler() throws InternalClientException, JAXBException {
        ouMarshaller = MarshallerFactory.getInstance().getMarshaller(OrganizationalUnit.class);
        componentMarshaller = MarshallerFactory.getInstance().getMarshaller(Component.class);
        containerMarshaller = MarshallerFactory.getInstance().getMarshaller(Container.class);
        itemMarshaller = MarshallerFactory.getInstance().getMarshaller(Item.class);
        marshaller = ScapeMarshaller.newInstance();
    }

    @Override
    public String getIntellectualEntity(String id) throws EscidocException {
        try {
            IntellectualEntity e = getIntellectualEntityObject(id);
            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            marshaller.serialize(e, sink);
            String xml = sink.toString();
            int insertpos = xml.indexOf("?>") + 2;
            xml =
                xml.substring(0, insertpos)
                    + "<?xml-stylesheet type=\"text/xsl\" href=\"/xsl/scape_intellectual_entity.xsl\"?>"
                    + xml.substring(insertpos);
            return xml;
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }

    @Override
    public boolean isEntityQueued(String id) {
        Iterator<IngestItem> ingests = entitylist.iterator();
        while (ingests.hasNext()) {
            if (ingests.next().pid.equals(id)) {
                return true;
            }
        }
        return false;
    }

    public IntellectualEntity getIntellectualEntityObject(String id) throws Exception {
        IntellectualEntity.Builder entity = new IntellectualEntity.Builder();
        Map<String, String[]> filters = new HashMap<String, String[]>();
        filters.put("query", new String[] { "\"/properties/pid\"=" + id });
        String resultXml = containerHandler.retrieveContainers(filters);
        int pos = resultXml.indexOf("<sru-zr:numberOfRecords>") + 24;
        String tmp = new String(resultXml.substring(pos));
        tmp = tmp.substring(0, tmp.indexOf("</sru-zr:numberOfRecords>"));
        int numRecs = Integer.parseInt(tmp);
        if (numRecs == 0) {
            throw new ScapeException("Unable to find object with pid " + id);
        }
        else if (numRecs > 1) {
            throw new ScapeException("More than one hit for PID " + id + ". This is not good");
        }
        int posStart = resultXml.indexOf("<container:container");
        if (posStart > 0) {
            int posEnd = resultXml.indexOf("</container:container>") + 22;
            resultXml = resultXml.substring(posStart, posEnd);
        }
        else {
            return null;
        }
        Container c = containerMarshaller.unmarshalDocument(resultXml);
        MetadataRecord record = c.getMetadataRecords().get("DESCRIPTIVE");
        entity.descriptive(marshaller.getJaxbUnmarshaller().unmarshal(record.getContent(), ElementContainer.class));
        entity.identifier(new Identifier(c.getProperties().getPid()));
        entity.representations(fetchRepresentations(c));

        LifecycleState lfs =
            (LifecycleState) marshaller.getJaxbUnmarshaller().unmarshal(
                c.getMetadataRecords().get("LIFECYCLE-XML").getContent());
        entity.lifecycleState(lfs);
        return entity.build();
    }

    @Override
    public String getIntellectualEntitySet(List<String> ids) throws EscidocException {
        try {
            List<IntellectualEntity> entities = new ArrayList<IntellectualEntity>();
            for (String id : ids) {
                entities.add(getIntellectualEntityObject(id));
            }
            IntellectualEntityCollection coll = new IntellectualEntityCollection(entities);
            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            marshaller.serialize(coll, sink);
            return sink.toString("UTF-8");
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }

    @Override
    public String getIntellectualEntityVersionSet(String id) throws EscidocException {
        Map<String, String[]> filters = new HashMap<String, String[]>();
        filters.put("query", new String[] { "\"/properties/pid\"=" + id });
        String resultXml = containerHandler.retrieveContainers(filters);
        int posStart = resultXml.indexOf("<container:container");
        if (posStart > 0) {
            int posEnd = resultXml.indexOf("</container:container>") + 22;
            resultXml = resultXml.substring(posStart, posEnd);
        }
        else {
            return null;
        }
        try {
            Container c = containerMarshaller.unmarshalDocument(resultXml);
            return getVersionXml(c.getMetadataRecords().get("VERSION-XML"));
        }
        catch (InternalClientException e) {
            throw new ScapeException(e);
        }
    }

    @Override
    public String ingestIntellectualEntity(String xml) throws EscidocException {
        logger.debug("ingesting intellectual entity");
        return ingestIntellectualEntity(xml, null);
    }

    @Override
    public String ingestIntellectualEntityAsync(String xml) throws EscidocException {
        try {
            IngestItem p = new IngestItem(xml, "ENTITY-" + UUID.randomUUID());
            entitylist.add(p);
            // start single thread, to read the queue.
            // start the thread only once
            if (!threadstarted) {
                threadstarted = true;
                ExecutorService service = Executors.newSingleThreadExecutor();
                service.execute(new IngestWorker(UserContext.getHandle()));
            }
            return "<scape:value>" + p.pid + "</scape:value>";
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }

    @Override
    public String searchIntellectualEntities(Map<String, String[]> params) throws EscidocException {
        try {
            List<IntellectualEntity> entities = new ArrayList<IntellectualEntity>();
            String xml = containerHandler.retrieveContainers(params);
            int pos;
            while ((pos = xml.indexOf("<container:container")) != -1) {
                int endPos = xml.indexOf("</container:container>") + 22;
                String containerXml = xml.substring(pos, endPos);
                xml = xml.substring(endPos);
                IntellectualEntity.Builder entity = new IntellectualEntity.Builder();
                Container c = containerMarshaller.unmarshalDocument(containerXml);
                MetadataRecord record = c.getMetadataRecords().get("DESCRIPTIVE");
                entity.descriptive(marshaller.getJaxbUnmarshaller().unmarshal(record.getContent()));
                entity.identifier(new Identifier(c.getObjid()));
                entity.representations(fetchRepresentations(c));

                LifecycleState lfs =
                    (LifecycleState) marshaller.getJaxbUnmarshaller().unmarshal(
                        c.getMetadataRecords().get("LIFECYCLE-XML").getContent());
                entity.lifecycleState(lfs);
                entities.add(entity.build());
            }
            IntellectualEntityCollection coll = new IntellectualEntityCollection(entities);
            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            marshaller.serialize(coll, sink);
            return sink.toString();
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }

    @Override
    public String updateIntellectualEntity(String id, String xml) throws EscidocException {
        try {
            Map<String, String[]> filters = new HashMap<String, String[]>();
            filters.put("query", new String[] { "\"/properties/pid\"=" + id });
            String resultXml = containerHandler.retrieveContainers(filters);
            int pos = resultXml.indexOf("<sru-zr:numberOfRecords>") + 24;
            String tmp = new String(resultXml.substring(pos));
            tmp = tmp.substring(0, tmp.indexOf("</sru-zr:numberOfRecords>"));
            int numRecs = Integer.parseInt(tmp);
            if (numRecs == 0) {
                throw new ScapeException("Unable to find object with pid " + id);
            }
            else if (numRecs > 1) {
                throw new ScapeException("More than one hit for PID " + id + ". This is not good");
            }
            int posStart = resultXml.indexOf("<container:container");
            if (posStart > 0) {
                int posEnd = resultXml.indexOf("</container:container>") + 22;
                resultXml = resultXml.substring(posStart, posEnd);
            }
            else {
                return null;
            }
            Container orig = containerMarshaller.unmarshalDocument(resultXml);
            IntellectualEntity e =
                marshaller.deserialize(IntellectualEntity.class, new ByteArrayInputStream(xml.getBytes()));
            Container updated = createContainer(e.getIdentifier().getValue(), e);
            updated.setLastModificationDate(orig.getLastModificationDate());
            containerHandler.update(orig.getObjid(), containerMarshaller.marshalDocument(updated));
            return "<scape:value>" + updated.getProperties().getPid() + "</scape:value>";
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }

    private String ingestIntellectualEntity(String xml, String pid) throws EscidocException {
        try {
            /*
             * ensure that the context, content model and the OU are properly
             * initialized
             */
            checkScapeContext();
            checkScapeContentModel();
            if (pid == null) {
                pid = "ENTITY-" + UUID.randomUUID().toString();
            }
            IntellectualEntity e =
                marshaller.deserialize(IntellectualEntity.class, new ByteArrayInputStream(xml.getBytes()));

            /* persist the container/entity in escidoc */
            Container container = createContainer(pid, e);
            containerHandler.create(containerMarshaller.marshalDocument(container));

            /* return the pid wrapped in a scape xml answer */
            return "<scape:value>" + container.getProperties().getPid() + "</scape:value>";
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }

    /* this should be no longer needed since IDs are UUIDs now in every case */
    private void ensurePids(IntellectualEntity e) throws Exception {
        Map<String, String[]> filters = new HashMap<String, String[]>();
        filters.put("query", new String[] { "\"/properties/pid\"=" + e.getIdentifier().getValue()
            + " AND \"type\"=container" });
        String resultXml = containerHandler.retrieveContainers(filters);
        int pos = resultXml.indexOf("<sru-zr:numberOfRecords>") + 24;
        String tmp = new String(resultXml.substring(pos));
        tmp = tmp.substring(0, tmp.indexOf("</sru-zr:numberOfRecords>"));
        int numRecs = Integer.parseInt(tmp);
        if (numRecs != 0) {
            throw new ScapeException("An entity with the id " + e.getIdentifier().getValue() + " already exists");
        }
        StringBuilder query = new StringBuilder();
        for (Representation r : e.getRepresentations()) {
            query.append(" OR \"/properties/pid\"=" + r.getIdentifier().getValue());
            for (File f : r.getFiles()) {
                query.append(" OR \"/properties/pid\"=" + f.getIdentifier().getValue());
                for (BitStream bs : f.getBitStreams()) {
                    query.append(" OR \"/properties/pid\"=" + bs.getIdentifier().getValue());
                }
            }
        }
        filters.put("query", new String[] { query.toString().substring(3) });
        resultXml = itemHandler.retrieveItems(filters);
        pos = resultXml.indexOf("<sru-zr:numberOfRecords>") + 24;
        tmp = new String(resultXml.substring(pos));
        tmp = tmp.substring(0, tmp.indexOf("</sru-zr:numberOfRecords>"));
        numRecs = Integer.parseInt(tmp);
        if (numRecs != 0) {
            throw new ScapeException("An item with the id already exists");
        }

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
            throw new ScapeException(e);
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
            throw new ScapeException(e);
        }
    }

    private void checkScapeOU() throws EscidocException {
        try {
            if (scapeOU != null && scapeOU.getObjid() == null) {
                System.out.println("OU is not null and id is null");
            }
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
            throw new ScapeException(e);
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
            throw new ScapeException(e);
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
            logger.error("Error while opening ou", e);
            throw new ScapeException(e);
        }
    }

    private List<Representation> fetchRepresentations(Container c) throws Exception {
        List<Representation> reps = new ArrayList<Representation>();
        for (ItemMemberRef ref : c.getStructMap().getItems()) {
            Item i = itemMarshaller.unmarshalDocument(itemHandler.retrieve(ref.getObjid()));
            Representation rep = fetchRepresentation(i);
            reps.add(rep);
        }
        return reps;

    }

    private Representation fetchRepresentation(Item i) throws Exception {
        Representation.Builder rep = new Representation.Builder(new Identifier(i.getProperties().getPid()));

        if (i.getMetadataRecords() != null) {
            MetadataRecord record = i.getMetadataRecords().get("TECHNICAL");
            if (record != null) {
                Object md = marshaller.getJaxbUnmarshaller().unmarshal(record.getContent());
                rep.technical(md);
            }

            record = i.getMetadataRecords().get("PROVENANCE");
            if (record != null) {
                Object md = marshaller.getJaxbUnmarshaller().unmarshal(record.getContent());
                rep.provenance(md);
            }

            record = i.getMetadataRecords().get("SOURCE");
            if (record != null) {
                Object md;
                if (record.getMdType().equals("DC")) {
                    md = marshaller.getJaxbUnmarshaller().unmarshal(record.getContent(), ElementContainer.class);
                }
                else {
                    md = marshaller.getJaxbUnmarshaller().unmarshal(record.getContent());
                }
                rep.source(md);
            }

            record = i.getMetadataRecords().get("RIGHTS");
            if (record != null) {
                Object md = marshaller.getJaxbUnmarshaller().unmarshal(record.getContent());
                rep.rights(md);
            }
        }

        List<File> files = new ArrayList<File>();
        for (Relation rel : i.getRelations()) {
            if (rel.getPredicate().equals("http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasPart")) {
                files.add(fetchFile(rel.getObjid()));
            }
        }
        rep.files(files);
        return rep.build();
    }

    private File fetchFile(String objid) throws Exception {
        File.Builder file = new File.Builder();
        Item i = itemMarshaller.unmarshalDocument(itemHandler.retrieve(objid));
        file.identifier(new Identifier(i.getProperties().getPid()));
        ContentStream stream = i.getContentStreams().get(0);
        file.mimetype(stream.getMimeType());
        file.uri(URI.create(stream.getXLinkHref()));
        MetadataRecord record = i.getMetadataRecords().get("TECHNICAL");
        if (record != null) {
            Object md = marshaller.getJaxbUnmarshaller().unmarshal(record.getContent());
            file.technical(md);
        }
        List<BitStream> bitstreams = new ArrayList<BitStream>();
        for (Relation rel : i.getRelations()) {
            if (rel.getPredicate().equals("http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasPart")) {
                bitstreams.add(fetchBitStream(rel.getObjid()));
            }
        }
        file.bitStreams(bitstreams);
        return file.build();
    }

    private BitStream fetchBitStream(String objid) throws Exception {
        BitStream.Builder bs = new BitStream.Builder();
        Item i = itemMarshaller.unmarshalDocument(itemHandler.retrieve(objid));
        bs.identifier(new Identifier(i.getProperties().getPid()));
        MetadataRecord record = i.getMetadataRecords().get("TECHNICAL");
        if (record != null) {
            Object md = marshaller.getJaxbUnmarshaller().unmarshal(record.getContent());
            bs.technical(md);
        }
        return bs.build();
    }

    private Container createContainer(String pid, IntellectualEntity e) throws EscidocException {
        try {
            /* create the metadata records for the entity container */
            MetadataRecords records = new MetadataRecords();
            MetadataRecord descmd = new MetadataRecord("DESCRIPTIVE");
            DOMResult result = new DOMResult();
            if (e.getDescriptive() == null) {
                throw new ScapeException("Descriptive metadata can not be empty");
            }
            marshaller.getJaxbMarshaller().marshal(e.getDescriptive(), result);
            descmd.setContent(((Document) result.getNode()).getDocumentElement());
            records.add(descmd);
            records.add(createEscidocRecord(e.getIdentifier().getValue()));

            DocumentBuilder docbuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // version metadata record
            MetadataRecord v = new MetadataRecord("VERSION-XML");
            v.setLastModificationDate(new DateTime());
            v.setMdType("VERSION-XML");
            v.setContent(docbuilder.parse(
                new InputSource(new StringReader("<versions><version number=\"" + e.getVersionNumber() + "\" date=\""
                    + new DateTime() + "\" /></versions>"))).getDocumentElement());
            records.add(v);

            // lifecycle metadata record
            String state =
                e.getLifecycleState() != null ? e.getLifecycleState().getState().name() : LifecycleState.State.INGESTED
                    .name();
            LifecycleState lfs = new LifecycleState("", State.valueOf(state));
            MetadataRecord lc = new MetadataRecord("LIFECYCLE-XML");
            lc.setLastModificationDate(new DateTime());
            lc.setMdType("LIFECYCLE-XML");
            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            marshaller.serialize(lfs, sink);
            lc.setContent(docbuilder
                .parse(new InputSource(new StringReader(new String(sink.toByteArray())))).getDocumentElement());
            records.add(lc);

            /* create the entity container */
            Container container = new Container();
            ContainerProperties props = new ContainerProperties();
            props.setContentModel(new ContentModelRef(scapeContentModelId));
            props.setContext(new ContextRef(scapeContext.getObjid()));
            props.setPid(pid);
            container.setProperties(props);
            container.setLastModificationDate(new DateTime());
            container.setMetadataRecords(records);

            StructMap struct = new StructMap();

            /* create the items for representations */
            for (ItemMemberRef ref : createRepresentationItems(e)) {
                struct.add(ref);
            }
            container.setStructMap(struct);
            return container;
        }
        catch (Exception ex) {
            throw new ScapeException(ex);
        }
    }

    private MetadataRecord createEscidocRecord(String pid) throws JAXBException {
        MetadataRecord escidocRecord = new MetadataRecord("escidoc");
        ObjectFactory dcFac = new ObjectFactory();
        ElementContainer dcContainer = dcFac.createElementContainer();
        SimpleLiteral titleLit = new SimpleLiteral();
        titleLit.getContent().add(pid);
        dcContainer.getAny().add(dcFac.createTitle(titleLit));
        SimpleLiteral identLit = new SimpleLiteral();
        identLit.getContent().add(pid);
        dcContainer.getAny().add(dcFac.createIdentifier(identLit));
        DOMResult res = new DOMResult();

        JAXBElement<ElementContainer> jb =
            new JAXBElement(new QName("http://purl.org/dc/elements/1.1/", "dublin-core", "dc"), ElementContainer.class,
                dcContainer);
        marshaller.getJaxbMarshaller().marshal(jb, res);
        escidocRecord.setContent(((Document) res.getNode()).getDocumentElement());
        return escidocRecord;
    }

    private MetadataRecords createMdRecords(Representation r) throws JAXBException {
        MetadataRecords records = new MetadataRecords();

        /* technical metadata */
        MetadataRecord techmd = new MetadataRecord("TECHNICAL");
        Object md = r.getTechnical();
        if (md instanceof JAXBElement<?>) {
            md = ((JAXBElement) md).getValue();
        }
        if (md != null) {
            DOMResult res = new DOMResult();
            marshaller.getJaxbMarshaller().marshal(md, res);
            techmd.setMdType(md.getClass().getName());
            techmd.setContent(((Document) res.getNode()).getDocumentElement());
            records.add(techmd);
        }

        /* rights metadata */
        MetadataRecord rightsmd = new MetadataRecord("RIGHTS");
        md = r.getRights();
        if (md instanceof JAXBElement<?>) {
            md = ((JAXBElement) md).getValue();
        }
        if (md != null) {
            DOMResult res = new DOMResult();
            if (md instanceof RightsComplexType) {
                JAXBElement<ElementContainer> jb =
                    new JAXBElement(new QName("info:lc/xmlns/premis-v2", "rights", "premis"), RightsComplexType.class,
                        md);
                marshaller.getJaxbMarshaller().marshal(jb, res);
                rightsmd.setMdType("PREMIS/Rights");
            }
            else {
                marshaller.getJaxbMarshaller().marshal(md, res);
                rightsmd.setMdType(md.getClass().getName());
            }
            rightsmd.setContent(((Document) res.getNode()).getDocumentElement());
            records.add(rightsmd);
        }
        /* provenance metadata */
        MetadataRecord digiprovmd = new MetadataRecord("PROVENANCE");
        md = r.getProvenance();
        if (md instanceof JAXBElement<?>) {
            md = ((JAXBElement) md).getValue();
        }
        if (md != null) {
            DOMResult res = new DOMResult();
            if (md instanceof PremisComplexType) {
                JAXBElement<ElementContainer> jb =
                    new JAXBElement(new QName("info:lc/xmlns/premis-v2", "premis", "premis"), PremisComplexType.class,
                        md);
                marshaller.getJaxbMarshaller().marshal(jb, res);
                rightsmd.setMdType("PREMIS/Provenance");
            }
            else {
                marshaller.getJaxbMarshaller().marshal(md, res);
                rightsmd.setMdType(md.getClass().getName());
            }
            digiprovmd.setContent(((Document) res.getNode()).getDocumentElement());
            records.add(digiprovmd);
        }
        /* source metadata */
        MetadataRecord sourcemd = new MetadataRecord("SOURCE");
        md = r.getSource();
        if (md instanceof JAXBElement<?>) {
            md = ((JAXBElement) md).getValue();
        }
        if (md != null) {
            DOMResult res = new DOMResult();
            if (md instanceof ElementContainer) {
                JAXBElement<ElementContainer> jb =
                    new JAXBElement(new QName("http://purl.org/dc/elements/1.1/", "dublin-core", "dc"),
                        ElementContainer.class, md);
                marshaller.getJaxbMarshaller().marshal(jb, res);
                sourcemd.setMdType("DC");
            }
            else {
                marshaller.getJaxbMarshaller().marshal(md, res);
                sourcemd.setMdType(md.getClass().getName());
            }
            sourcemd.setContent(((Document) res.getNode()).getDocumentElement());
            records.add(sourcemd);
        }

        records.add(createEscidocRecord(r.getIdentifier().getValue()));

        return records;
    }

    private MetadataRecords createMetadataRecords(File f) throws JAXBException {
        Object md = f.getTechnical();
        MetadataRecords records = new MetadataRecords();
        records.add(createEscidocRecord(f.getIdentifier().getValue()));
        if (md == null) {
            return records;
        }
        if (md instanceof JAXBElement<?>) {
            md = ((JAXBElement) md).getValue();
        }
        if (md instanceof ElementNSImpl) {
            MetadataRecord techmd = new MetadataRecord("TECHNICAL");
            techmd.setMdType(md.getClass().getName());
            techmd.setContent((ElementNSImpl) md);
            records.add(techmd);
        }
        else {
            MetadataRecord techmd = new MetadataRecord("TECHNICAL");
            DOMResult res = new DOMResult();
            marshaller.getJaxbMarshaller().marshal(md, res);
            techmd.setMdType(md.getClass().getName());
            techmd.setContent(((Document) res.getNode()).getDocumentElement());
            records.add(techmd);
        }
        return records;
    }

    private MetadataRecords createMetadataRecords(BitStream bs) throws JAXBException {
        Object md = bs.getTechnical();
        MetadataRecords records = new MetadataRecords();
        records.add(createEscidocRecord(bs.getIdentifier().getValue()));
        if (md == null) {
            return records;
        }
        if (md instanceof JAXBElement<?>) {
            md = ((JAXBElement) md).getValue();
        }
        if (md instanceof ElementNSImpl) {
            MetadataRecord techmd = new MetadataRecord("TECHNICAL");
            techmd.setMdType(md.getClass().getName());
            techmd.setContent((ElementNSImpl) md);
            records.add(techmd);
        }
        else {
            MetadataRecord techmd = new MetadataRecord("TECHNICAL");
            DOMResult res = new DOMResult();
            marshaller.getJaxbMarshaller().marshal(md, res);
            techmd.setMdType(md.getClass().getName());
            techmd.setContent(((Document) res.getNode()).getDocumentElement());
            records.add(techmd);
        }
        return records;
    }

    private List<ItemMemberRef> createRepresentationItems(IntellectualEntity e) throws Exception {
        List<ItemMemberRef> refs = new ArrayList<ItemMemberRef>();
        for (Representation r : e.getRepresentations()) {
            Item item = new Item();
            ItemProperties props = new ItemProperties();
            props.setContentModel(new ContentModelRef(scapeContentModelId));
            props.setContext(new ContextRef(scapeContext.getObjid()));
            String repId = "REPRESENTATION-" + UUID.randomUUID().toString();
            props.setPid(repId);
            item.setProperties(props);
            item.setMetadataRecords(createMdRecords(r));
            Relations rels = new Relations();
            for (ItemMemberRef ref : createFileItems(r)) {
                Relation rel = new Relation(ref);
                rel.setPredicate("http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasPart");
                rels.add(rel);
            }
            item.setRelations(rels);
            String escidocId =
                itemMarshaller.unmarshalDocument(itemHandler.create(itemMarshaller.marshalDocument(item))).getObjid();
            refs.add(new ItemMemberRef("/ir/item/" + escidocId, repId, XLinkType.simple));
        }
        return refs;
    }

    private List<ItemMemberRef> createFileItems(Representation r) throws Exception {
        List<ItemMemberRef> refs = new ArrayList<ItemMemberRef>();
        for (File f : r.getFiles()) {
            Item item = new Item();
            ItemProperties props = new ItemProperties();
            props.setContentModel(new ContentModelRef(scapeContentModelId));
            props.setContext(new ContextRef(scapeContext.getObjid()));
            String fileId = "FILE-" + UUID.randomUUID().toString();
            props.setPid(fileId);
            item.setProperties(props);
            item.setMetadataRecords(createMetadataRecords(f));
            if (f.getUri() != null && (f.getUri().getScheme() != null)) {
                if (f.getUri().getScheme().equals("file")) {
                    java.io.File local = new java.io.File(f.getUri());
                    if (local.exists()) {
                        item.setContentStreams(createFileStreams(f));
                    }
                    else {
                        continue;
                    }
                }
                else {
                    /* fetch the content and add it to the item */
                    item.setContentStreams(createFileStreams(f));
                }
            }
            Relations rels = new Relations();
            for (ItemMemberRef ref : createBitStreamsItems(f)) {
                Relation rel = new Relation(ref);
                rel.setPredicate("http://www.escidoc.de/ontologies/mpdl-ontologies/content-relations#hasPart");
                rels.add(rel);
            }
            item.setRelations(rels);
            String escidocId =
                itemMarshaller.unmarshalDocument(itemHandler.create(itemMarshaller.marshalDocument(item))).getObjid();
            refs.add(new ItemMemberRef("/ir/item/" + escidocId, fileId, XLinkType.simple));
        }
        return refs;
    }

    private ContentStreams createFileStreams(File f) throws Exception {
        ContentStreams streams = new ContentStreams();
        ContentStream stream =
            new ContentStream((f.getFilename() != null ? f.getFilename() : "file"), StorageType.INTERNAL_MANAGED, (f
                .getMimetype() != null) ? f.getMimetype() : "application/octet-stream");
        stream.setXLinkHref(f.getUri().toASCIIString());
        streams.add(stream);
        return streams;
    }

    private List<ItemMemberRef> createBitStreamsItems(File f) throws Exception {
        List<ItemMemberRef> refs = new ArrayList<ItemMemberRef>();
        if (f.getBitStreams() != null) {
            for (BitStream bs : f.getBitStreams()) {
                Item item = new Item();
                ItemProperties props = new ItemProperties();
                props.setContentModel(new ContentModelRef(scapeContentModelId));
                props.setContext(new ContextRef(scapeContext.getObjid()));
                String bsId = "BITSTREAM-" + UUID.randomUUID().toString();
                props.setPid(bs.getIdentifier().getValue());
                item.setProperties(props);
                item.setMetadataRecords(createMetadataRecords(bs));
                String escidocId =
                    itemMarshaller
                        .unmarshalDocument(itemHandler.create(itemMarshaller.marshalDocument(item))).getObjid();
                refs.add(new ItemMemberRef("/ir/item/" + escidocId, bsId, XLinkType.simple));
            }
        }
        return refs;
    }

    private class IngestItem {
        private final String xml;

        private final String pid;

        public IngestItem(String xml, String pid) throws EscidocException {
            this.xml = xml;
            this.pid = pid;
        }
    }

    private class IngestWorker implements Runnable {

        private final String handle;

        private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(25);

        public IngestWorker(String handle) {
            this.handle = handle;
        }

        @Override
        public void run() {
            UserContext.setUserContext(handle);
            while (true) {
                try {
                    IngestItem ingestitem = IntellectualEntityHandler.this.entitylist.peek();
                    if (ingestitem == null) {
                        // Nothing to do so let's go to sleep
                        Thread.sleep(100);
                        continue;
                    }
                    ScheduledFuture<?> future =
                        executor.schedule(new IngestEntity(ingestitem, handle), 10, TimeUnit.MILLISECONDS);
                    future.get();
                    IntellectualEntityHandler.this.entitylist.remove();
                }
                catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    executor.shutdownNow();
                }
                catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    private class IngestEntity implements Runnable {

        private final String handle;

        private final IngestItem ingestitem;

        public IngestEntity(IngestItem ingestitem, String handle) {
            this.handle = handle;
            this.ingestitem = ingestitem;
        }

        @Override
        public void run() {
            UserContext.setUserContext(handle);
            try {
                long start = System.currentTimeMillis();
                IntellectualEntityHandler.this.ingestIntellectualEntity(ingestitem.xml, ingestitem.pid);
                long diff = System.currentTimeMillis() - start;
                System.out.println("Time (ms): " + diff + " thread-id: " + Thread.currentThread().getId() + " SIP-ID: "
                    + ingestitem.pid);
            }
            catch (EscidocException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }
    }

}
