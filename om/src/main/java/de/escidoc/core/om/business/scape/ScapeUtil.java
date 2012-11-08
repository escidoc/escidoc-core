package de.escidoc.core.om.business.scape;

import java.io.ByteArrayInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.resources.common.MetadataRecord;
import eu.scapeproject.model.Agent;
import eu.scapeproject.model.Identifier;
import eu.scapeproject.model.LifecycleState;
import eu.scapeproject.model.metadata.DescriptiveMetadata;
import eu.scapeproject.model.metadata.TechnicalMetadata;
import eu.scapeproject.model.metadata.audiomd.AudioMDMetadata;
import eu.scapeproject.model.metadata.dc.DCMetadata;
import eu.scapeproject.model.metadata.fits.FitsMetadata;
import eu.scapeproject.model.metadata.mix.NisoMixMetadata;
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
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "sources");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.sources(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "subject");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.subject(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS(NS_DC, "type");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.type(nodes.item(i).getTextContent());
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
            Node node = nodes.item(j);
            if (node.getLocalName() == null) {
                return null;
            }
            if (node.getLocalName().equals("name")) {
                c.name(node.getTextContent());
            }
            else if (node.getLocalName().equals("note")) {
                c.note(node.getTextContent());
            }
        }
        return c.build();
    }

    public static int parseVersionNumber(MetadataRecord rec) {
        return Integer.parseInt(rec.getContent().getAttribute("number"));
    }

    public static TechnicalMetadata getTechMd(String xml) throws ScapeException {
        if (xml == null) {
            return null;
        }
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
            throw new ScapeException(e.getMessage(), e);
        }
    }

}
