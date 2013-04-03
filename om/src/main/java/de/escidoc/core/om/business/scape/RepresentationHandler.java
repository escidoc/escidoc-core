package de.escidoc.core.om.business.scape;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.purl.dc.elements._1.ElementContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.om.business.interfaces.RepresentationHandlerInterface;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.Relation;
import de.escidoc.core.resources.om.item.Item;
import eu.scapeproject.model.BitStream;
import eu.scapeproject.model.File;
import eu.scapeproject.model.Identifier;
import eu.scapeproject.model.Representation;
import eu.scapeproject.util.ScapeMarshaller;

@Service("business.RepresentationHandler")
public class RepresentationHandler implements RepresentationHandlerInterface {
    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandlerInterface itemHandler;

    private final Marshaller<Item> itemMarshaller;

    private final ScapeMarshaller marshaller;

    public RepresentationHandler() throws Exception {
        itemMarshaller = MarshallerFactory.getInstance().getMarshaller(Item.class);
        marshaller = ScapeMarshaller.newInstance();
    }

    @Override
    public String getRepresentation(String id) throws EscidocException {
        try {
            Map<String, String[]> filters = new HashMap<String, String[]>();
            filters.put("query", new String[] { "\"/properties/pid\"=" + id });
            String resultXml = itemHandler.retrieveItems(filters);
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
            int posStart = resultXml.indexOf("<escidocItem:item");
            if (posStart > 0) {
                int posEnd = resultXml.indexOf("</escidocItem:item>") + 19;
                resultXml = resultXml.substring(posStart, posEnd);
            }
            else {
                return null;
            }

            Item i = itemMarshaller.unmarshalDocument(resultXml);
            Representation r = fetchRepresentation(i);
            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            marshaller.serialize(r, sink);
            return sink.toString();
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }

    @Override
    public String updateRepresentation(String id, String xml) throws EscidocException {
        try {
            itemHandler.update(id, xml);
            return "";
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }

    @Override
    public String searchRepresentations(Map<String, String[]> params) throws EscidocException {
        return itemHandler.retrieveItems(params);
    }

    private Representation fetchRepresentation(Item i) throws Exception {
        Representation.Builder rep = new Representation.Builder(new Identifier(i.getProperties().getPid()));

        MetadataRecord record = i.getMetadataRecords().get("TECHNICAL");
        Object md = marshaller.getJaxbUnmarshaller().unmarshal(record.getContent());
        if (md instanceof JAXBElement<?>){
        	md = ((JAXBElement) md).getValue();
        }
        rep.technical(md);

        record = i.getMetadataRecords().get("PROVENANCE");
        md = marshaller.getJaxbUnmarshaller().unmarshal(record.getContent());
        if (md instanceof JAXBElement<?>){
        	md = ((JAXBElement) md).getValue();
        }
        rep.provenance(md);

        record = i.getMetadataRecords().get("SOURCE");
        md = marshaller.getJaxbUnmarshaller().unmarshal(record.getContent());
        if (md instanceof JAXBElement<?>){
        	md = ((JAXBElement) md).getValue();
        }
        rep.source(md);

        record = i.getMetadataRecords().get("RIGHTS");
        md = marshaller.getJaxbUnmarshaller().unmarshal(record.getContent());
        if (md instanceof JAXBElement<?>){
        	md = ((JAXBElement) md).getValue();
        }
        rep.rights(md);

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
        file.identifier(new Identifier(objid));
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
        bs.identifier(new Identifier(objid));
        MetadataRecord record = i.getMetadataRecords().get("TECHNICAL");
        if (record != null) {
            Object md = marshaller.getJaxbUnmarshaller().unmarshal(record.getContent());
            bs.technical(md);
        }
        return bs.build();
    }

    private Item getItem(String id, String version) throws EscidocException {
        try {
            Map<String, String[]> filters = new HashMap<String, String[]>();
            filters.put("query", new String[] { "\"/properties/pid\"=" + id });
            String resultXml = itemHandler.retrieveItems(filters);

            /* PARSE THE RESULT FROM ESCIDOC */
            int pos = resultXml.indexOf("<sru-zr:numberOfRecords>") + 24;
            String tmp = new String(resultXml.substring(pos));
            tmp = tmp.substring(0, tmp.indexOf("</sru-zr:numberOfRecords>"));
            int numRecs = Integer.parseInt(tmp);
            if (numRecs == 0) {
                return null;
            }
            else if (numRecs > 1) {
                throw new ScapeException("More than one hit for PID " + id + ". This is not good");
            }
            int posStart = resultXml.indexOf("<escidocItem:item");
            if (posStart > 0) {
                int posEnd = resultXml.indexOf("</escidocItem:item>") + 19;
                resultXml = resultXml.substring(posStart, posEnd);
            }
            else {
                return null;
            }
            Item i = itemMarshaller.unmarshalDocument(resultXml);
            return i;
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }

}
