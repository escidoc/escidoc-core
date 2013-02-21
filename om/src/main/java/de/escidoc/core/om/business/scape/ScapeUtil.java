package de.escidoc.core.om.business.scape;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;

import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.resources.ResourceType;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.om.item.Item;
import eu.scapeproject.model.Agent;
import eu.scapeproject.model.File;
import eu.scapeproject.model.Identifier;
import eu.scapeproject.model.LifecycleState;
import eu.scapeproject.model.Representation;
import eu.scapeproject.model.metadata.DescriptiveMetadata;
import eu.scapeproject.model.metadata.ProvenanceMetadata;
import eu.scapeproject.model.metadata.RightsMetadata;
import eu.scapeproject.model.metadata.TechnicalMetadata;
import eu.scapeproject.model.metadata.audiomd.AudioMDMetadata;
import eu.scapeproject.model.metadata.dc.DCMetadata;
import eu.scapeproject.model.metadata.fits.FitsMetadata;
import eu.scapeproject.model.metadata.marc.Marc21Metadata;
import eu.scapeproject.model.metadata.mix.NisoMixMetadata;
import eu.scapeproject.model.metadata.premis.PremisProvenanceMetadata;
import eu.scapeproject.model.metadata.premis.PremisRightsMetadata;
import eu.scapeproject.model.metadata.textmd.TextMDMetadata;
import eu.scapeproject.model.metadata.videomd.VideoMDMetadata;
import eu.scapeproject.model.mets.SCAPEMarshaller;

public abstract class ScapeUtil {

    private static final Pattern PATTERN_NS = Pattern.compile("xmlns\\:\\w+=\"[\\w\\d\\:\\./\\-]+\"");

    public static final String NS_DC = "http://purl.org/dc/elements/1.1/";

    public static final String NS_AUDIOMD = "http://www.loc.gov/AMD/";

    public static final String NS_VIDEOMD = "http://www.loc.gov/videoMD/";

    public static final String NS_TEXTMD = "info:lc/xmlns/textmd-v3";

    public static final String NS_FITS = "http://hul.harvard.edu/ois/xml/ns/fits/fits_output";

    public static final String NS_NISOMIX = "http://www.loc.gov/mix/v10";

    public static LifecycleState parseLifeCycleState(MetadataRecord rec) {
        LifecycleState.State state = LifecycleState.State.valueOf(rec.getContent().getAttribute("state"));
        String details = rec.getContent().getTextContent();
        return new LifecycleState(details, state);
    }

    public static DescriptiveMetadata parseDcMetadata(MetadataRecord record) {
        DCMetadata.Builder dc = new DCMetadata.Builder();
        dc.id(record.getName());
        NodeList nodes = record.getContent().getElementsByTagNameNS(NS_DC, "title");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.title(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "description");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.description(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "coverage");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.coverage(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "format");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.format(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "language");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.language(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "publisher");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.publisher(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "relation");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.relations(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "rights");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.rights(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "source");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.sources(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "subject");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.subject(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "type");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.types(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "contributor");
        for (int i = 0; i < nodes.getLength(); i++) {
            Agent ag = parseAgent(nodes.item(i));
            if (ag != null) {
                dc.contributor(ag);
            }
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "creator");
        for (int i = 0; i < nodes.getLength(); i++) {
            Agent ag = parseAgent(nodes.item(i));
            if (ag != null) {
                dc.creator(ag);
            }
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "date");
        for (int i = 0; i < nodes.getLength(); i++) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
            dc.date(formatter.parseDateTime(nodes.item(i).getTextContent()).toDate());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "identifier");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String type = node.getAttributes().getNamedItem("type").getNodeValue();
            String value = node.getTextContent();
            dc.identifier(new Identifier(type, value));
        }
        return dc.build();
    }

    public static Agent parseAgent(Node item) {
        NodeList nodes = item.getChildNodes();
        Agent.Builder c = new Agent.Builder();
        for (int j = 0; j < nodes.getLength(); j++) {
            Node rootnode = nodes.item(j).getParentNode();
            if (rootnode.hasAttributes()) {
                NamedNodeMap namednodemap = rootnode.getAttributes();
                if (namednodemap.getNamedItem("premis:role") != null) {
                    c.role(namednodemap.getNamedItem("premis:role").getNodeValue());
                }
                if (namednodemap.getNamedItem("premis:type") != null) {
                    c.type(namednodemap.getNamedItem("premis:type").getNodeValue());
                }
            }

            Node node = nodes.item(j).getNextSibling();
            if (node != null) {
                if (node.getLocalName() != null) {

                    if (node.getLocalName().equals("name")) {
                        c.name(node.getTextContent());
                    }
                    if (node.getLocalName().equals("note")) {
                        c.note(node.getTextContent());
                    }
                }
            }
        }
        return c.build();
    }

    public static int parseVersionNumber(MetadataRecord rec) {
        String num = rec.getContent().getAttribute("number").trim();
        if (num.length() == 0) {
            return 0;
        }
        return Integer.parseInt(num);
    }

    public static TechnicalMetadata getTechMd(String xml) throws ScapeException {
        if (xml == null) {
            return null;
        }
        // strip <?xml..?> declaration
        int pos = xml.indexOf("?>");
        if (pos > 0) {
            xml = xml.substring(pos + 2);
        }

        // decide what to do based on the namespace of the document
        Matcher m = PATTERN_NS.matcher(xml);
        if (!m.find()) {
            return null;
        }
        int posStart = xml.indexOf('"', m.start());
        String ns = xml.substring(posStart + 1, m.end() - 1);
        System.out.println("NAMESPACE for techMD " + ns);
        try {
            if (ns == null) {
                throw new ScapeException("Namespace is null, unable to fetch techmd");
            }
            if (ns.equalsIgnoreCase(NS_TEXTMD)) {
                return (TextMDMetadata) SCAPEMarshaller.getInstance().getJaxbUnmarshaller().unmarshal(
                    new ByteArrayInputStream(xml.getBytes()));
            }
            else if (ns.equalsIgnoreCase(NS_AUDIOMD)) {
                return (AudioMDMetadata) SCAPEMarshaller.getInstance().getJaxbUnmarshaller().unmarshal(
                    new ByteArrayInputStream(xml.getBytes()));
            }
            else if (ns.equalsIgnoreCase(NS_VIDEOMD)) {
                return (VideoMDMetadata) SCAPEMarshaller.getInstance().getJaxbUnmarshaller().unmarshal(
                    new ByteArrayInputStream(xml.getBytes()));
            }
            else if (ns.equalsIgnoreCase(NS_FITS)) {
                return (FitsMetadata) SCAPEMarshaller.getInstance().getJaxbUnmarshaller().unmarshal(
                    new ByteArrayInputStream(xml.getBytes()));
            }
            else if (ns.equalsIgnoreCase(NS_NISOMIX)) {
                return (NisoMixMetadata) SCAPEMarshaller.getInstance().getJaxbUnmarshaller().unmarshal(
                    new ByteArrayInputStream(xml.getBytes()));
            }
            else {
                throw new ScapeException("Unable to deserialize technical metadata with Namesapce URI " + ns);
            }
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }

    public static String getVersionXml(MetadataRecord md) {
        StringBuilder versionXml = new StringBuilder("<versions>");
        NodeList nodes = md.getContent().getElementsByTagName("version");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            versionXml.append("<version date=\"" + n.getAttributes().getNamedItem("date").getNodeValue()
                + "\" number=\"" + n.getAttributes().getNamedItem("number").getNodeValue() + "\" />");
        }
        return versionXml.append("</versions>").toString();
    }

    public static DescriptiveMetadata getSourceMd(String xml) throws JAXBException {
        return (DCMetadata) SCAPEMarshaller.getInstance().getJaxbUnmarshaller().unmarshal(new StringReader(xml));
    }

    public static RightsMetadata getRightsMd(String xml) throws JAXBException {
        return (PremisRightsMetadata) SCAPEMarshaller.getInstance().getJaxbUnmarshaller().unmarshal(
            new StringReader(xml));
    }

    public static ProvenanceMetadata getProvenanceMd(String xml) throws JAXBException {
        return (PremisProvenanceMetadata) SCAPEMarshaller.getInstance().getJaxbUnmarshaller().unmarshal(
            new StringReader(xml));
    }

    private static List<File> getFiles(Item i) {
        List<File> files = new ArrayList<File>();
        File.Builder f = new File.Builder();
        for (Relation rel : i.getRelations()) {
            // get file items from escidoc
            if (rel.getResourceType() == ResourceType.ITEM) {
                System.out.println(rel.getXLinkTitle() + " " + rel.getXLinkHref() + " " + rel.getPredicate() + " "
                    + rel.getObjid());
                int posStart = rel.getXLinkHref().indexOf("/ir/items/") + 10;
                String id = rel.getXLinkHref().substring(posStart);
                f.identifier(new Identifier(id));
                f.uri(URI.create(rel.getXLinkHref()));
                files.add(f.build());
            }
        }
        return files;
    }

    public static Representation getRepresentation(Item i) throws Exception {
        Representation.Builder rep = new Representation.Builder(new Identifier(i.getObjid()));
        rep.files(getFiles(i));

        // tech md
        Node n = i.getMetadataRecords().get("techMD").getContent();
        Document doc = n.getOwnerDocument();
        DOMImplementationLS implLs = (DOMImplementationLS) doc.getImplementation();
        String xml = implLs.createLSSerializer().writeToString(n);
        TechnicalMetadata techMd = ScapeUtil.getTechMd(xml);
        rep.technical(techMd);

        // source md
        n = i.getMetadataRecords().get("sourceMD").getContent();
        doc = n.getOwnerDocument();
        implLs = (DOMImplementationLS) doc.getImplementation();
        xml = implLs.createLSSerializer().writeToString(n);
        DescriptiveMetadata sourceMD = ScapeUtil.getSourceMd(xml);
        rep.source(sourceMD);

        // rights md
        n = i.getMetadataRecords().get("rightsMD").getContent();
        doc = n.getOwnerDocument();
        implLs = (DOMImplementationLS) doc.getImplementation();
        xml = implLs.createLSSerializer().writeToString(n);
        RightsMetadata rightsMD = ScapeUtil.getRightsMd(xml);
        rep.rights(rightsMD);

        // provenance md
        n = i.getMetadataRecords().get("digiprovMD").getContent();
        doc = n.getOwnerDocument();
        implLs = (DOMImplementationLS) doc.getImplementation();
        xml = implLs.createLSSerializer().writeToString(n);
        ProvenanceMetadata prov = ScapeUtil.getProvenanceMd(xml);
        rep.provenance(prov);

        return rep.build();
    }

}
