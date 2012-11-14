package de.escidoc.core.om.service.interfaces;

import de.escidoc.core.common.exceptions.EscidocException;

public interface RepresentationHandlerInterface {
	String getRepresentation(String id) throws EscidocException;
	String putRepresentation(String xml);
}
