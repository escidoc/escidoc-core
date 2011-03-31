package de.escidoc.core.sm.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for ReportHandler.
 */
public interface ReportHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/ReportHandler";

    String JNDI_NAME = "ejb/ReportHandler";

    ReportHandlerRemote create() throws CreateException, RemoteException;

}
