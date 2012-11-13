package de.escidoc.core.om.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import de.escidoc.core.common.exceptions.EscidocException;
import de.escidoc.core.common.exceptions.scape.ScapeException;
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
    public String updateIntellectualEntity(String id, String xml) throws EscidocException {
        return handler.updateIntellectualEntity(id, xml);
    }

    @Override
    public String getIntellectualEntitySet(String idData) throws EscidocException {
        BufferedReader r = new BufferedReader(new StringReader(idData));
        String uri;
        List<String> ids = new ArrayList<String>();
        try {
            while ((uri = r.readLine()) != null) {
                int posStart = uri.indexOf("/scape/entity/") + 14;
                ids.add(new String(uri.substring(posStart).trim()));
                System.out.println("ID: " + ids.get(ids.size() - 1));
            }
            if (ids.size() == 0) {
                return "<entity-list />";
            }
            return handler.getIntellectualEntitySet(ids);
        }
        catch (IOException e) {
            throw new ScapeException(e);
        }
    }

    @Override
    public String getIntellectualEntityVersionSet(String id) throws EscidocException {
        return handler.getIntellectualEntityVersionSet(id);
    }

    @Override
    public String ingestIntellectualEntity(String xml) throws EscidocException {
        return handler.ingestIntellectualEntity(xml);
    }

    @Override
    public String getMetadata(String id, String mdName) throws EscidocException {
        return handler.getMetadata(id, mdName);
    }

    @Override
    public String updateMetadata(String id, String xmlData) throws EscidocException {
        return handler.updateMetadata(id, xmlData);
    }

    @Override
    public String ingestIntellectualEntityAsync(String xml) throws EscidocException {
        return handler.ingestIntellectualEntityAsync(xml);
    }

}
