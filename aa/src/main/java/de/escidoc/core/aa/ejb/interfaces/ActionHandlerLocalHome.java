package de.escidoc.core.aa.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for ActionHandler.
 */
public interface ActionHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/ActionHandlerLocal";

    String JNDI_NAME = "ejb/ActionHandlerLocal";

    ActionHandlerLocal create() throws CreateException;

}
