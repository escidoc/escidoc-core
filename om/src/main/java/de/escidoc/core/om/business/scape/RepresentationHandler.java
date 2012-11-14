package de.escidoc.core.om.business.scape;

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
import de.escidoc.core.resources.om.item.Item;
import eu.scapeproject.model.Representation;
import eu.scapeproject.model.mets.SCAPEMarshaller;

@Service("business.RepresentationHandler")
public class RepresentationHandler implements RepresentationHandlerInterface {
    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandlerInterface itemHandler;

    private final Marshaller<Item> itemMarshaller;

    public RepresentationHandler() throws InternalClientException {
        itemMarshaller = MarshallerFactory.getInstance().getMarshaller(Item.class);
    }

    @Override
    public String getRepresentation(String id) throws EscidocException {
        try {
            String itemXml = itemHandler.retrieve(id);
            Item i = itemMarshaller.unmarshalDocument(itemXml);
            Representation rep = ScapeUtil.getRepresentation(i);
            return SCAPEMarshaller.getInstance().serialize(rep);
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }

    @Override
    public String updateRepresentation(String xml) {
        // TODO Auto-generated method stub
        return null;
    }

}
