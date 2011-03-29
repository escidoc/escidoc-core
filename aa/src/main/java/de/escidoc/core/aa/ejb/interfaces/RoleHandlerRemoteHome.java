package de.escidoc.core.aa.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for RoleHandler.
 */
public interface RoleHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/RoleHandler";
    String JNDI_NAME = "ejb/RoleHandler";

    RoleHandlerRemote create()
            throws CreateException, RemoteException;

}
