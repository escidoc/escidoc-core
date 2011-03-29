package de.escidoc.core.sm.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for ReportDefinitionHandler.
 */
public interface ReportDefinitionHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/ReportDefinitionHandler";
    String JNDI_NAME = "ejb/ReportDefinitionHandler";

    ReportDefinitionHandlerRemote create()
            throws CreateException, RemoteException;

}
