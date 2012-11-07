package de.escidoc.core.om.business.scape;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.escidoc.core.resources.common.MetadataRecord;
import eu.scapeproject.model.Agent;
import eu.scapeproject.model.Identifier;
import eu.scapeproject.model.LifecycleState;
import eu.scapeproject.model.metadata.DescriptiveMetadata;
import eu.scapeproject.model.metadata.dc.DCMetadata;

public abstract class ScapeUtil {
    public static LifecycleState parseLifeCycleState(MetadataRecord rec) {
        LifecycleState.State state = LifecycleState.State.valueOf(rec.getContent().getAttribute("state"));
        String details = rec.getContent().getTextContent();
        return new LifecycleState(details, state);
    }

    public static DescriptiveMetadata parseDcMetadata(MetadataRecord record) {
        DCMetadata.Builder dc = new DCMetadata.Builder();
        NodeList nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "title");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.title(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "description");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.description(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "coverage");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.coverage(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "format");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.format(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "language");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.language(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "publisher");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.publisher(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "relation");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.relations(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "rights");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.rights(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "sources");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.sources(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "subject");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.subject(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "type");
        for (int i = 0; i < nodes.getLength(); i++) {
            dc.type(nodes.item(i).getTextContent());
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "contributor");
        for (int i = 0; i < nodes.getLength(); i++) {
            Agent ag = parseAgent(nodes.item(i));
            if (ag != null) {
                dc.contributor(ag);
            }
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "creator");
        for (int i = 0; i < nodes.getLength(); i++) {
            Agent ag = parseAgent(nodes.item(i));
            if (ag != null) {
                dc.creator(ag);
            }
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "date");
        for (int i = 0; i < nodes.getLength(); i++) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
            dc.date(formatter.parseDateTime(nodes.item(i).getTextContent()).toDate());
        }
        nodes = record.getContent().getElementsByTagNameNS("http://purl.org/dc/elements/1.1/", "identifier");
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

}
