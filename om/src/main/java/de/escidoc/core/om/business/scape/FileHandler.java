package de.escidoc.core.om.business.scape;

import java.util.Map;

import org.esidoc.core.utils.io.EscidocBinaryContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.om.business.interfaces.FileHandlerInterface;
import de.escidoc.core.om.service.interfaces.ItemHandlerInterface;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.st.service.interfaces.StagingFileHandlerInterface;

@Service("business.FileHandler")
public class FileHandler implements FileHandlerInterface {

    @Autowired
    @Qualifier("service.ItemHandler")
    private ItemHandlerInterface itemHandler;

    @Autowired
    @Qualifier("service.StagingFileHandler")
    private StagingFileHandlerInterface stagingFileHandler;

    private final Marshaller<Item> itemMarshaller;

    public FileHandler() throws InternalClientException {
        itemMarshaller = MarshallerFactory.getInstance().getMarshaller(Item.class);
    }

    @Override
    public EscidocBinaryContent getFile(String id) throws EscidocException {
        try {
            return stagingFileHandler.retrieve(id);
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }

    @Override
    public String searchFiles(Map<String, String[]> params) throws EscidocException {
        return itemHandler.retrieveItems(params);
    }

}
