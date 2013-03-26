package de.escidoc.core.om.business.scape;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.om.business.interfaces.RepresentationHandlerInterface;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;
import de.escidoc.core.resources.om.item.Item;

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
        return null;
    }

    @Override
    public String updateRepresentation(String xml) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String searchRepresentations(Map<String, String[]> params) throws EscidocException {
        return itemHandler.retrieveItems(params);
    }

}
