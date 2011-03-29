package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for ContextHandler.
 */
public interface ContextHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/ContextHandler";
    String JNDI_NAME = "ejb/ContextHandler";

    ContextHandlerRemote create()
            throws CreateException, RemoteException;

}
