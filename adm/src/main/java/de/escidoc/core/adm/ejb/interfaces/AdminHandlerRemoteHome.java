/*
 * Generated by XDoclet - Do not edit!
 */
package de.escidoc.core.adm.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for AdminHandler.
 */
public interface AdminHandlerRemoteHome extends EJBHome {

    public static final String COMP_NAME = "java:comp/env/ejb/AdminHandler";
    public static final String JNDI_NAME = "ejb/AdminHandler";

    public AdminHandlerRemote create()
            throws CreateException, RemoteException;

}
