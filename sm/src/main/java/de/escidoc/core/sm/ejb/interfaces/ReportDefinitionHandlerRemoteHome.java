/*
 * Generated by XDoclet - Do not edit!
 */
package de.escidoc.core.sm.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for ReportDefinitionHandler.
 */
public interface ReportDefinitionHandlerRemoteHome extends EJBHome {

    public static final String COMP_NAME = "java:comp/env/ejb/ReportDefinitionHandler";
    public static final String JNDI_NAME = "ejb/ReportDefinitionHandler";

    public ReportDefinitionHandlerRemote create()
            throws CreateException, RemoteException;

}
