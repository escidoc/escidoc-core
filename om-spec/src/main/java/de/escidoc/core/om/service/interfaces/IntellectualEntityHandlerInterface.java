package de.escidoc.core.om.service.interfaces;

import de.escidoc.core.common.exceptions.EscidocException;

public interface IntellectualEntityHandlerInterface {
	// string passing, really?! well if you want it you get it.
	String ingestIntellectualEntity(String xml) throws EscidocException;

	String ingestIntellectualEntityAsync(String xml) throws EscidocException;

	String getIntellectualEntity(String id) throws EscidocException;

	String updateIntellectualEntity(String id, String xml) throws EscidocException;

	String getIntellectualEntitySet(String idData) throws EscidocException;
	
	String getIntellectualEntityVersionSet(String id) throws EscidocException;
	
	String getMetadata(String id, String mdNanme) throws EscidocException;
	
	String updateMetadata(String id,String xmlData) throws EscidocException;
	
	String searchIntellectualEntity(String query) throws EscidocException;
}
