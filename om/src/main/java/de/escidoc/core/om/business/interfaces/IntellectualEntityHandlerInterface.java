package de.escidoc.core.om.business.interfaces;

import java.util.List;
import java.util.Map;

import de.escidoc.core.common.exceptions.EscidocException;

public interface IntellectualEntityHandlerInterface {
    String getIntellectualEntity(String id) throws EscidocException;

    String updateIntellectualEntity(String id, String xml) throws EscidocException;

    String getIntellectualEntitySet(List<String> ids) throws EscidocException;

    String getIntellectualEntityVersionSet(String id) throws EscidocException;

    String ingestIntellectualEntity(String xml) throws EscidocException;

    String ingestIntellectualEntityAsync(String xml) throws EscidocException;

    String updateMetadata(String id, String xmlData) throws EscidocException;

    String searchIntellectualEntities(Map<String, String[]> params) throws EscidocException;
}
