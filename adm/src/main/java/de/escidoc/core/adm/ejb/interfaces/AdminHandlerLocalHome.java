package de.escidoc.core.adm.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for AdminHandler.
 */
public interface AdminHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/AdminHandlerLocal";

    String JNDI_NAME = "ejb/AdminHandlerLocal";

    AdminHandlerLocal create() throws CreateException;

}
