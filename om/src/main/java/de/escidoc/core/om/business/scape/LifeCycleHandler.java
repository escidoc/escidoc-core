package de.escidoc.core.om.business.scape;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.om.business.interfaces.LifeCycleHandlerInterface;
import de.escidoc.core.om.service.interfaces.ContainerHandlerInterface;
import de.escidoc.core.resources.om.container.Container;

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
        return null;
    }
}
