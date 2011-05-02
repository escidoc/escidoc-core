package de.escidoc.core.sm.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for ScopeHandler.
 */
public interface ScopeHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/ScopeHandler";

    String JNDI_NAME = "ejb/ScopeHandler";

    ScopeHandlerRemote create() throws CreateException, RemoteException;

}
