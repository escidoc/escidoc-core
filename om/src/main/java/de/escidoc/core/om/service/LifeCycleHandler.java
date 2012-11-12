package de.escidoc.core.om.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.om.business.interfaces.LifeCycleHandlerInterface;

@Service("service.LifeCycleHandler")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LifeCycleHandler implements de.escidoc.core.om.service.interfaces.LifeCycleHandlerInterface {

    private static final Logger LOG = LoggerFactory.getLogger(LifeCycleHandler.class);

    @Autowired
    @Qualifier("business.LifeCycleHandler")
    private LifeCycleHandlerInterface handler;

    @Override
    public String getLifecycleStatus(String id) throws EscidocException {
        return handler.getLifecycleStatus(id);
    }
}
