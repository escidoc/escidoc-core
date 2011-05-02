package de.escidoc.core.om.ejb.interfaces;

import de.escidoc.core.common.exceptions.EscidocException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBLocalObject;

/**
 * Local interface for IngestHandler.
 */
public interface IngestHandlerLocal extends EJBLocalObject {

    String ingest(String xmlData, SecurityContext securityContext) throws EscidocException;

    String ingest(String xmlData, String authHandle, Boolean restAccess) throws EscidocException;

}
