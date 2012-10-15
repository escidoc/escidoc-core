package de.escidoc.core.om.service.interfaces;

import de.escidoc.core.common.exceptions.EscidocException;

public interface SRUHandlerInterface {
	String searchIntellectualEntity(String query) throws EscidocException; 
	String searchRepresentation(String query) throws EscidocException; 
	String searchFile(String query) throws EscidocException; 
}
