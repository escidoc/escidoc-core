package de.escidoc.core.om.ejb.interfaces;

import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Remote interface for FedoraDescribeDeviationHandler.
 */
public interface FedoraDescribeDeviationHandlerRemote extends EJBObject {

    String getFedoraDescription(Map parameters, SecurityContext securityContext) throws Exception, RemoteException;

    String getFedoraDescription(Map parameters, String authHandle, Boolean restAccess) throws Exception,
        RemoteException;

}
