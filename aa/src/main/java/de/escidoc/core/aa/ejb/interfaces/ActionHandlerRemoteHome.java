package de.escidoc.core.aa.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for ActionHandler.
 */
public interface ActionHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/ActionHandler";
    String JNDI_NAME = "ejb/ActionHandler";

    ActionHandlerRemote create()
            throws CreateException, RemoteException;

}
