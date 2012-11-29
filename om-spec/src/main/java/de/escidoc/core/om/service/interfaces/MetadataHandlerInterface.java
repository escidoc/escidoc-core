package de.escidoc.core.om.service.interfaces;

import de.escidoc.core.common.exceptions.EscidocException;

public interface MetadataHandlerInterface {
	String getMetadata(String id,String versionId) throws EscidocException;
}
