package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for IngestHandler.
 */
public interface IngestHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/IngestHandlerLocal";

    String JNDI_NAME = "ejb/IngestHandlerLocal";

    IngestHandlerLocal create() throws CreateException;

}
