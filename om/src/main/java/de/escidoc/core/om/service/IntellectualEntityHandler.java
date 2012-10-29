package de.escidoc.core.om.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.escidoc.core.common.business.Constants;
import de.escidoc.core.common.business.fedora.resources.ResourceType;
import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.application.notfound.ContextNotFoundException;
import de.escidoc.core.common.exceptions.application.notfound.OrganizationalUnitNotFoundException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
import de.escidoc.core.common.jibx.Marshaller;
import de.escidoc.core.common.jibx.MarshallerFactory;
import de.escidoc.core.common.util.IOUtils;
import de.escidoc.core.common.util.xml.XmlUtility;
import de.escidoc.core.om.business.interfaces.IntellectualEntityHandlerInterface;
import de.escidoc.core.oum.service.interfaces.OrganizationalUnitHandlerInterface;
import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.properties.PublicStatus;
import de.escidoc.core.resources.common.reference.OrganizationalUnitRef;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.om.context.ContextProperties;
import de.escidoc.core.resources.om.context.OrganizationalUnitRefs;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.ItemProperties;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.escidoc.core.resources.oum.OrganizationalUnitProperties;
import eu.scapeproject.model.IntellectualEntity;
import eu.scapeproject.model.mets.SCAPEMarshaller;

@Service("service.IntellectualEntityHandler")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
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
    public String getLifeCyclestatus(String id) throws EscidocException {
        return handler.getLifeCyclestatus(id);
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
