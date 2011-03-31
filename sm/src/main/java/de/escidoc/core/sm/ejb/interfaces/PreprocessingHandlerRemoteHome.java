package de.escidoc.core.sm.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for PreprocessingHandler.
 */
public interface PreprocessingHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/PreprocessingHandler";

    String JNDI_NAME = "ejb/PreprocessingHandler";

    PreprocessingHandlerRemote create() throws CreateException, RemoteException;

}
