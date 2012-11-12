package de.escidoc.core.om.business.scape;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.om.business.interfaces.LifeCycleHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;
import de.escidoc.core.resources.om.container.Container;
import eu.scapeproject.model.mets.SCAPEMarshaller;

@Service("business.LifeCycleHandler")
public class LifeCycleHandler implements LifeCycleHandlerInterface {
    @Autowired
    @Qualifier("service.ContainerHandler")
    private ContainerHandlerInterface containerHandler;

    private final Marshaller<Container> containerMarshaller;

    public LifeCycleHandler() throws InternalClientException {
        containerMarshaller = MarshallerFactory.getInstance().getMarshaller(Container.class);
    }

    @Override
    public String getLifecycleStatus(String id) throws EscidocException {
        Container c;
        try {
            c = containerMarshaller.unmarshalDocument(containerHandler.retrieve(id));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            SCAPEMarshaller.getInstance().getJaxbMarshaller().marshal(
                ScapeUtil.parseLifeCycleState(c.getMetadataRecords().get("LIFECYCLE-XML")), bos);
            return bos.toString();
        }
        catch (Exception e) {
            throw new ScapeException(e.getMessage(), e);
        }
    }
}
