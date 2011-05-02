package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for IngestHandler.
 */
public interface IngestHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/IngestHandler";

    String JNDI_NAME = "ejb/IngestHandler";

    IngestHandlerRemote create() throws CreateException, RemoteException;

}
