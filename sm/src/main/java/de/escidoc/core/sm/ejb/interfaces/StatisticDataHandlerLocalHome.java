package de.escidoc.core.sm.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for StatisticDataHandler.
 */
public interface StatisticDataHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/StatisticDataHandlerLocal";

    String JNDI_NAME = "ejb/StatisticDataHandlerLocal";

    StatisticDataHandlerLocal create() throws CreateException;

}
