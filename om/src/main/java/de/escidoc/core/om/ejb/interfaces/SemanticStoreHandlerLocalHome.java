/*
 * Generated by XDoclet - Do not edit!
 */
package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for SemanticStoreHandler.
 */
public interface SemanticStoreHandlerLocalHome extends EJBLocalHome {

    public static final String COMP_NAME = "java:comp/env/ejb/SemanticStoreHandlerLocal";
    public static final String JNDI_NAME = "ejb/SemanticStoreHandlerLocal";

    public SemanticStoreHandlerLocal create() throws CreateException;

}
