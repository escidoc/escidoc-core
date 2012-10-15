package de.escidoc.core.om.service.interfaces;

public interface RepresentationHandlerInterface {
	String getRepresentation(String id);
	String searchRepresentation(String query);
	String putRepresentation(String xml);
}
