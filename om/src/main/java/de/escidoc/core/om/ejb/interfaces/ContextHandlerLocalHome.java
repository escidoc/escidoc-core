/*
 * Generated by XDoclet - Do not edit!
 */
package de.escidoc.core.om.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for ContextHandler.
 */
public interface ContextHandlerLocalHome extends EJBLocalHome {

    public static final String COMP_NAME = "java:comp/env/ejb/ContextHandlerLocal";
    public static final String JNDI_NAME = "ejb/ContextHandlerLocal";

    public ContextHandlerLocal create() throws CreateException;

}
