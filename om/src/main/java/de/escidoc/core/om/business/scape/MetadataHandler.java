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
    public String updateMetadata(String id, String mdName, String xmlData) throws EscidocException {
        Item i = getItem(id, null);
        MetadataRecord md;
        if (i != null) {
            md = i.getMetadataRecords().get(mdName);
            if (md == null) {
                throw new ScapeException("The metadata record " + mdName + " does not yet exist for object " + id);
            }
            itemHandler.updateMdRecord(id, mdName, xmlData);
            return "";
        }
        Container c = getContainer(id, null);
        if (c != null) {
            md = c.getMetadataRecords().get(mdName);
            if (md == null) {
                throw new ScapeException("The metadata record " + mdName + " does not yet exist for object " + id);
            }
            containerHandler.updateMetadataRecord(id, mdName, xmlData);
            return "";
        }
        throw new ItemNotFoundException("Unable to locate object with id " + id + " while updating metadata");
    }

    @Override
    public String getMetadata(String id, String mdname, String version) throws EscidocException {
        try {
            MetadataRecord md;
            Item i = getItem(id, version);
            if (i != null) {
                md = i.getMetadataRecords().get(mdname);
            }
            else {
                Container c = getContainer(id, version);
                if (c == null) {
                    throw new ItemNotFoundException("Unable to find resource with pid: " + id);
                }
                md = c.getMetadataRecords().get(mdname);
            }
            if (md == null || md.getContent() == null) {
                return "";
            }
            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            Element e = md.getContent();
            DOMImplementationLS domImplementation = (DOMImplementationLS) e.getOwnerDocument().getImplementation();
            LSSerializer lsSerializer = domImplementation.createLSSerializer();
            String xml = lsSerializer.writeToString(e);
            int insertpos = xml.indexOf("?>") + 2;
            xml =
                xml.substring(0, insertpos) + "<?xml-stylesheet type=\"text/xsl\" href=\"/xsl/scape_metadata.xsl\"?>"
                    + xml.substring(insertpos);
            return xml;

        }
        catch (Exception ie) {
            throw new ScapeException(ie);
        }
    }

    private Container getContainer(String id, String version) throws EscidocException {
        try {
            Map<String, String[]> filters = new HashMap<String, String[]>();
            filters.put("query", new String[] { "\"/properties/pid\"=" + id });
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
            return c;
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
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
