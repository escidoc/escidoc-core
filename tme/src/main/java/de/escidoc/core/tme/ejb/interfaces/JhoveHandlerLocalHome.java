/*
 * Generated by XDoclet - Do not edit!
 */
package de.escidoc.core.tme.ejb.interfaces;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/**
 * Local home interface for JhoveHandler.
 */
public interface JhoveHandlerLocalHome extends EJBLocalHome {

    public static final String COMP_NAME = "java:comp/env/ejb/JhoveHandlerLocal";
    public static final String JNDI_NAME = "ejb/JhoveHandlerLocal";

    public JhoveHandlerLocal create() throws CreateException;

}
