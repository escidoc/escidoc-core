package de.escidoc.core.sm.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;

/**
 * Home interface for StatisticDataHandler.
 */
public interface StatisticDataHandlerRemoteHome extends EJBHome {

    String COMP_NAME = "java:comp/env/ejb/StatisticDataHandler";

    String JNDI_NAME = "ejb/StatisticDataHandler";

    StatisticDataHandlerRemote create() throws CreateException, RemoteException;

}
