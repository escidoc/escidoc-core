package de.escidoc.core.om.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.om.business.interfaces.IntellectualEntityHandlerInterface;

@Service("service.IntellectualEntityHandler")
public class IntellectualEntityHandler
    implements de.escidoc.core.om.service.interfaces.IntellectualEntityHandlerInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntellectualEntityHandler.class);

    @Autowired
    @Qualifier("business.IntellectualEntityHandler")
    private IntellectualEntityHandlerInterface handler;

    @Override
    public String getIntellectualEntity(String id) throws EscidocException {
        return handler.getIntellectualEntity(id);
    }

    @Override
    public String updateIntellectualEntity(String xml) throws EscidocException {
        return handler.updateIntellectualEntity(xml);
    }

    @Override
    public String getIntellectualEntitySet(List<String> ids) throws EscidocException {
        return handler.getIntellectuakEntitySet(ids);
    }

    @Override
    public String getIntellectualEntityVersionSet(String id) throws EscidocException {
        return handler.getIntellectualEntityVersionSet(id);
    }

    @Override
    public String ingestIntellectualEntity(String xml) throws EscidocException {
        return handler.ingestIntellectualEntity(xml);
    }

}
