package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for ItemHandler.
 */
public interface ItemHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/ItemHandlerLocal";

    String JNDI_NAME = "ejb/ItemHandlerLocal";

    ItemHandlerLocal create() throws CreateException;

}
