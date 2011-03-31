package de.escidoc.core.st.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for StagingFileHandler.
 */
public interface StagingFileHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/StagingFileHandler";

    String JNDI_NAME = "ejb/StagingFileHandler";

    StagingFileHandlerRemote create() throws CreateException, RemoteException;

}
