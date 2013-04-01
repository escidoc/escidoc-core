package de.escidoc.core.om.service.interfaces;

import de.escidoc.core.common.exceptions.EscidocException;

public interface MetadataHandlerInterface {	
	String getMetadata(String id,String mdname, String versionId) throws EscidocException;

	String updateMetadata(String id, String mdName, String xmlData) throws EscidocException;
}
