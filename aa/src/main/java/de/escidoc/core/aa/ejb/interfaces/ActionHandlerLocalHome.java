/*
 * Generated by XDoclet - Do not edit!
 */
package de.escidoc.core.aa.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for ActionHandler.
 */
public interface ActionHandlerLocalHome extends EJBLocalHome {

    public static final String COMP_NAME = "java:comp/env/ejb/ActionHandlerLocal";
    public static final String JNDI_NAME = "ejb/ActionHandlerLocal";

    public ActionHandlerLocal create() throws CreateException;

}
