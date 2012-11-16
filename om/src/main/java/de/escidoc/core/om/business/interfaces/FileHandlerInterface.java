package de.escidoc.core.om.business.interfaces;

import java.util.Map;

import de.escidoc.core.common.exceptions.EscidocException;

public interface FileHandlerInterface {
    String getFile(String id) throws EscidocException;

    String searchFiles(Map<String, String[]> params) throws EscidocException;
}
