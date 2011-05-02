package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for ContainerHandler.
 */
public interface ContainerHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/ContainerHandler";

    String JNDI_NAME = "ejb/ContainerHandler";

    ContainerHandlerRemote create() throws CreateException, RemoteException;

}
