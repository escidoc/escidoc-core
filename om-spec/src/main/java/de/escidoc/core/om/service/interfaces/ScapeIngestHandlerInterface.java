package de.escidoc.core.om.service.interfaces;

import de.escidoc.core.common.exceptions.EscidocException;

public interface ScapeIngestHandlerInterface {
	// string passing, really?! well if you want it you get it.
	String ingestIntellectualEntity(String xml) throws EscidocException;
}
