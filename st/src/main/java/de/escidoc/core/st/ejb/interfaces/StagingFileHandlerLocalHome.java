package de.escidoc.core.st.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for StagingFileHandler.
 */
public interface StagingFileHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/StagingFileHandlerLocal";

    String JNDI_NAME = "ejb/StagingFileHandlerLocal";

    StagingFileHandlerLocal create() throws CreateException;

}
