package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for ContainerHandler.
 */
public interface ContainerHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/ContainerHandlerLocal";

    String JNDI_NAME = "ejb/ContainerHandlerLocal";

    ContainerHandlerLocal create() throws CreateException;

}
