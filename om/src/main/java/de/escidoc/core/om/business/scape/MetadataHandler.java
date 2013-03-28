package de.escidoc.core.om.business.scape;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.xerces.dom.ElementNSImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.notfound.ItemNotFoundException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.om.business.interfaces.MetadataHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.item.Item;
import eu.scapeproject.util.ScapeMarshaller;

@Service("business.MetadataHandler")
public class MetadataHandler implements MetadataHandlerInterface {

    @Autowired
    @Qualifier("service.ContainerHandler")
    private ContainerHandlerInterface containerHandler;

    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandlerInterface itemHandler;

    private final Marshaller<Container> containerMarshaller;

    private final Marshaller<Item> itemMarshaller;

    private final ScapeMarshaller marshaller;

    public MetadataHandler() throws Exception {
        containerMarshaller = MarshallerFactory.getInstance().getMarshaller(Container.class);
        itemMarshaller = MarshallerFactory.getInstance().getMarshaller(Item.class);
        marshaller = ScapeMarshaller.newInstance();
    }

    @Override
    public String getMetadata(String id, String mdname, String version) throws EscidocException {
        try {

            MetadataRecord md = getItemMetadata(id, mdname, version);
            if (md == null) {
                /* might be a container */
                md = getContainerMetadata(id, mdname, version);
                if (md == null) {
                    throw new ItemNotFoundException("Unable to find resource with pid: " + id);
                }
            }
            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            Element e = md.getContent();
            DOMImplementationLS domImplementation = (DOMImplementationLS) e.getOwnerDocument().getImplementation();
            LSSerializer lsSerializer = domImplementation.createLSSerializer();
            return lsSerializer.writeToString(e);
        }
        catch (Exception ie) {
            throw new ScapeException(ie);
        }
    }

    private MetadataRecord getContainerMetadata(String id, String mdname, String version) throws Exception {
        Map<String, String[]> filters = new HashMap<String, String[]>();
        filters.put("query", new String[] { "\"/properties/pid\"=" + id + " AND \"type\"=container" });
        String resultXml = containerHandler.retrieveContainers(filters);

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
        int posStart = resultXml.indexOf("<container:container");
        if (posStart > 0) {
            int posEnd = resultXml.indexOf("</container:container>") + 22;
            resultXml = resultXml.substring(posStart, posEnd);
        }
        else {
            return null;
        }

        Container c = containerMarshaller.unmarshalDocument(resultXml);
        return c.getMetadataRecords().get(mdname);
    }

    private MetadataRecord getItemMetadata(String id, String mdname, String version) throws Exception {
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
        return i.getMetadataRecords().get(mdname);
    }
}
