package de.escidoc.core.om.business.interfaces;

import de.escidoc.core.common.exceptions.EscidocException;

public interface MetadataHandlerInterface {

    String getMetadata(String id, String mdname, String version) throws EscidocException;

	String updateMetadata(String id, String xmlData) throws EscidocException;
}
