package de.escidoc.core.om.ejb.interfaces;

import de.escidoc.core.common.exceptions.EscidocException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;

/**
 * Remote interface for IngestHandler.
 */
public interface IngestHandlerRemote extends EJBObject {

    String ingest(String xmlData, SecurityContext securityContext) throws EscidocException, RemoteException;

    String ingest(String xmlData, String authHandle, Boolean restAccess) throws EscidocException, RemoteException;

}
