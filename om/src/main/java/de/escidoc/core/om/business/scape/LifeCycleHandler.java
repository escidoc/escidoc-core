package de.escidoc.core.om.business.scape;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

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
import eu.scapeproject.model.LifecycleState;
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
    public String getLifecycleStatus(final String id) throws EscidocException {
        Container c;
        try {
            String searchResponse = containerHandler.retrieveContainers(new HashMap<String, String[]>() {
                {
                    put("query", new String[] { "\"/properties/pid\"=" + id + " AND \"type\"=container" });
                }
            });
            String lifeCycle = null;
            int pos;
            LifecycleState.State state;
            String details = null;
            if ((pos = searchResponse.indexOf("<lifecycle state=\"")) > 0) {
                lifeCycle = searchResponse.substring(pos + 18);
                pos = lifeCycle.indexOf('\"');
                if (pos == -1) {
                    throw new ScapeException("Unable to parse lifecycle from search response");
                }
                lifeCycle = lifeCycle.substring(0, pos);
                state = LifecycleState.State.valueOf(lifeCycle);
            }
            else {
                throw new ScapeException("Unable to parse lifecycle from search reposnse");
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            SCAPEMarshaller.getInstance().getJaxbMarshaller().marshal(new LifecycleState(details, state), bos);
            return bos.toString();
        }
        catch (Exception e) {
            throw new ScapeException(e);
        }
    }
}
