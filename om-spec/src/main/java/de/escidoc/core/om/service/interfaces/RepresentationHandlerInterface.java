package de.escidoc.core.om.service.interfaces;

import java.util.Map;

import de.escidoc.core.common.exceptions.EscidocException;

public interface RepresentationHandlerInterface {
	String getRepresentation(String id) throws EscidocException;

	String updateRepresentation(String xml) throws EscidocException;

	String searchRepresentations(Map<String, String[]> params) throws EscidocException;
}
