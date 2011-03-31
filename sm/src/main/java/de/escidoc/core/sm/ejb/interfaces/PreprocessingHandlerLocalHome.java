package de.escidoc.core.sm.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for PreprocessingHandler.
 */
public interface PreprocessingHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/PreprocessingHandlerLocal";

    String JNDI_NAME = "ejb/PreprocessingHandlerLocal";

    PreprocessingHandlerLocal create() throws CreateException;

}
