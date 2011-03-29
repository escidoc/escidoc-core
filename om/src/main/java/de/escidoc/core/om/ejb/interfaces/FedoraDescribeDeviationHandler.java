package de.escidoc.core.om.ejb.interfaces;

import org.springframework.security.context.SecurityContext;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Service endpoint interface for FedoraDescribeDeviationHandler.
 */
public interface FedoraDescribeDeviationHandler extends Remote {

    String getFedoraDescription(Map parameters, SecurityContext securityContext)
            throws Exception, RemoteException;

    String getFedoraDescription(Map parameters, String authHandle, Boolean restAccess)
            throws Exception, RemoteException;

}
