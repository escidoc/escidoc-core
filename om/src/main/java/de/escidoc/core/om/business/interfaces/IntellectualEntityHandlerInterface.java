package de.escidoc.core.om.business.interfaces;

import java.util.List;

import de.escidoc.core.common.exceptions.EscidocException;

public interface IntellectualEntityHandlerInterface {
    String getIntellectualEntity(String id) throws EscidocException;

    String updateIntellectualEntity(String xml) throws EscidocException;

    String getIntellectualEntitySet(List<String> ids) throws EscidocException;

    String getIntellectualEntityVersionSet(String id) throws EscidocException;

    String ingestIntellectualEntity(String xml) throws EscidocException;

    String getMetadata(String id, String mdName) throws EscidocException;

    String updateMetadata(String id, String mdName, String xmlData) throws EscidocException;
}
