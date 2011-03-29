package de.escidoc.core.adm.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for AdminHandler.
 */
public interface AdminHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/AdminHandler";
    String JNDI_NAME = "ejb/AdminHandler";

    AdminHandlerRemote create()
            throws CreateException, RemoteException;

}
