package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for ContentRelationHandler.
 */
public interface ContentRelationHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/ContentRelationHandlerLocal";

    String JNDI_NAME = "ejb/ContentRelationHandlerLocal";

    ContentRelationHandlerLocal create() throws CreateException;

}
