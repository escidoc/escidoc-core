package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for FedoraDescribeDeviationHandler.
 */
public interface FedoraDescribeDeviationHandlerLocalHome extends EJBLocalHome {

    String COMP_NAME = "java:comp/env/ejb/FedoraDescribeDeviationHandlerLocal";
    String JNDI_NAME = "ejb/FedoraDescribeDeviationHandlerLocal";

    FedoraDescribeDeviationHandlerLocal create()
            throws CreateException;

}
