package de.escidoc.core.oum.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for OrganizationalUnitHandler.
 */
public interface OrganizationalUnitHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/OrganizationalUnitHandlerLocal";

    String JNDI_NAME = "ejb/OrganizationalUnitHandlerLocal";

    OrganizationalUnitHandlerLocal create() throws CreateException;

}
