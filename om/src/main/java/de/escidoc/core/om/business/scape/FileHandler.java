package de.escidoc.core.om.business.scape;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.om.business.interfaces.FileHandlerInterface;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;
import de.escidoc.core.resources.om.item.Item;

@Service("business.FileHandler")
public class FileHandler implements FileHandlerInterface {

    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandlerInterface itemHandler;

    private final Marshaller<Item> itemMarshaller;

    public FileHandler() throws InternalClientException {
        itemMarshaller = MarshallerFactory.getInstance().getMarshaller(Item.class);
    }

    @Override
    public String getFile(String id) throws EscidocException {
        return null;
    }

}
