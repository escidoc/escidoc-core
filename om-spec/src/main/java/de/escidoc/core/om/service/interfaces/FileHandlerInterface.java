package de.escidoc.core.om.service.interfaces;

public interface FileHandlerInterface {
	String getFile(String id);
	String searchFile(String query);
	String getBitstream(String id);
}
