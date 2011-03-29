package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for ContextHandler.
 */
public interface ContextHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/ContextHandlerLocal";
    String JNDI_NAME = "ejb/ContextHandlerLocal";

    ContextHandlerLocal create() throws CreateException;

}
