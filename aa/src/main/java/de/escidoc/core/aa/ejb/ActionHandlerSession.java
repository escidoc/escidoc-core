/*
 * Generated by XDoclet - Do not edit!
 */
package de.escidoc.core.aa.ejb;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import java.rmi.RemoteException;

/**
 * Session layer for ActionHandler.
 */
public class ActionHandlerSession extends ActionHandlerBean implements SessionBean {

    public void ejbActivate() throws RemoteException {

        super.ejbActivate();
    }

    public void ejbPassivate() throws RemoteException {
        super.ejbPassivate();
    }

    public void setSessionContext(final SessionContext ctx) throws RemoteException {
        super.setSessionContext(ctx);
    }

    public void unsetSessionContext() {
    }

    public void ejbRemove() throws RemoteException {
        super.ejbRemove();
    }

}
