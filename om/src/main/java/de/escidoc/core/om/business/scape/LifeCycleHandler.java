package de.escidoc.core.om.business.scape;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.om.business.interfaces.LifeCycleHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;
import de.escidoc.core.resources.om.container.Container;
import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.model.LifecycleState;
import eu.scapeproject.model.LifecycleState.State;
import eu.scapeproject.util.ScapeMarshaller;

@Service("business.LifeCycleHandler")
public class LifeCycleHandler implements LifeCycleHandlerInterface {
    @Autowired
    @Qualifier("service.ContainerHandler")
    private ContainerHandlerInterface containerHandler;

    private final Marshaller<Container> containerMarshaller;

    private final ScapeMarshaller marshaller;

    @Autowired
    @Qualifier("business.IntellectualEntityHandler")
    private IntellectualEntityHandler entityHandler;

    public LifeCycleHandler() throws Exception {
        containerMarshaller = MarshallerFactory.getInstance().getMarshaller(Container.class);
        marshaller = ScapeMarshaller.newInstance();
    }

    @Override
    public String getLifecycleStatus(final String id) throws EscidocException {
        try {
            if (entityHandler.isEntityQueued(id)) {
                LifecycleState state = new LifecycleState("ingest queued", State.INGESTING);
                ByteArrayOutputStream sink = new ByteArrayOutputStream();
                marshaller.serialize(state, sink);
                return sink.toString();
            }
            IntellectualEntity e = entityHandler.getIntellectualEntityObject(id);
            ByteArrayOutputStream sink = new ByteArrayOutputStream();
            marshaller.serialize(e.getLifecycleState(), sink);
            return sink.toString();

        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }
}
