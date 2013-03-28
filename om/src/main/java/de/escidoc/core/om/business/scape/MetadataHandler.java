package de.escidoc.core.om.business.scape;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

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

@Service("business.MetadataHandler")
public class MetadataHandler implements MetadataHandlerInterface {

    @Autowired
    @Qualifier("service.ContainerHandler")
    private ContainerHandlerInterface containerHandler;

    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandlerInterface itemHandler;

    private final Marshaller<Container> containerMarshaller;

    public MetadataHandler() throws Exception {
        containerMarshaller = MarshallerFactory.getInstance().getMarshaller(Container.class);
    }

    @Override
    public String getMetadata(String id, String mdname, String version) throws EscidocException {
        try {
            return itemHandler.retrieveMdRecord(id, mdname);
        }
        catch (ItemNotFoundException e) {
            try {
                /*
                 * have to do flow control by exception handling, don't look at me like that, there's no
                 * ItemHandler.exists(id)
                 */

                /* might be a container */
                Map<String, String[]> filters = new HashMap<String, String[]>();
                filters.put("query", new String[] { "\"/properties/pid\"=" + id + " AND \"type\"=container" });
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
                return containerHandler.retrieveMdRecord(id, mdname);
            }
            catch (Exception ie) {
                throw new ScapeException(ie);
            }
        }
    }
}
