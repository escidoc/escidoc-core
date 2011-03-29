package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for SemanticStoreHandler.
 */
public interface SemanticStoreHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/SemanticStoreHandler";
    String JNDI_NAME = "ejb/SemanticStoreHandler";

    SemanticStoreHandlerRemote create()
            throws CreateException, RemoteException;

}
