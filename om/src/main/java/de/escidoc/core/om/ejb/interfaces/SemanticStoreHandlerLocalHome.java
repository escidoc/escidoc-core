package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for SemanticStoreHandler.
 */
public interface SemanticStoreHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/SemanticStoreHandlerLocal";
    String JNDI_NAME = "ejb/SemanticStoreHandlerLocal";

    SemanticStoreHandlerLocal create() throws CreateException;

}
