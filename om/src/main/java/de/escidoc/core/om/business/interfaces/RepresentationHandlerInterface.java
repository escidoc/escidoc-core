package de.escidoc.core.om.business.interfaces;

import de.escidoc.core.common.exceptions.EscidocException;

public interface RepresentationHandlerInterface {
	String getRepresentation(String id) throws EscidocException;

	String updateRepresentation(String xml) throws EscidocException;
}
