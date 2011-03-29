package de.escidoc.core.oum.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for OrganizationalUnitHandler.
 */
public interface OrganizationalUnitHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/OrganizationalUnitHandler";
    String JNDI_NAME = "ejb/OrganizationalUnitHandler";

    OrganizationalUnitHandlerRemote create()
            throws CreateException, RemoteException;

}
