package de.escidoc.core.om.business.interfaces;

import java.util.Map;

import org.esidoc.core.utils.io.EscidocBinaryContent;

import de.escidoc.core.common.exceptions.EscidocException;

public interface FileHandlerInterface {
    EscidocBinaryContent getFile(String id) throws EscidocException;

    String searchFiles(Map<String, String[]> params) throws EscidocException;
}
