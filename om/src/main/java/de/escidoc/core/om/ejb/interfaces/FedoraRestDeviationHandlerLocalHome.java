package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for FedoraRestDeviationHandler.
 */
public interface FedoraRestDeviationHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/FedoraRestDeviationHandlerLocal";
    String JNDI_NAME = "ejb/FedoraRestDeviationHandlerLocal";

    FedoraRestDeviationHandlerLocal create() throws CreateException;

}
