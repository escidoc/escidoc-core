package de.escidoc.core.aa.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for RoleHandler.
 */
public interface RoleHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/RoleHandlerLocal";

    String JNDI_NAME = "ejb/RoleHandlerLocal";

    RoleHandlerLocal create() throws CreateException;

}
