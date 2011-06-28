package de.escidoc.core.om.service;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import org.springframework.security.core.context.SecurityContext;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Service endpoint interface for FedoraRestDeviationHandler.
 */
public interface FedoraRestDeviationHandlerService extends Remote {

    EscidocBinaryContent getDatastreamDissemination(
        String pid, String dsID, Map parameters, SecurityContext securityContext) throws Exception;

    EscidocBinaryContent getDatastreamDissemination(
        String pid, String dsID, Map parameters, String authHandle, Boolean restAccess) throws Exception,
        RemoteException;

    String export(String pid, Map parameters, SecurityContext securityContext) throws Exception, RemoteException;

    String export(String pid, Map parameters, String authHandle, Boolean restAccess) throws Exception, RemoteException;

    void cache(String pid, String xml, SecurityContext securityContext) throws Exception, RemoteException;

    void cache(String pid, String xml, String authHandle, Boolean restAccess) throws Exception;

    void removeFromCache(String pid, SecurityContext securityContext) throws Exception, RemoteException;

    void removeFromCache(String pid, String authHandle, Boolean restAccess) throws Exception, RemoteException;

    String retrieveUncached(String pid, SecurityContext securityContext) throws Exception, RemoteException;

    String retrieveUncached(String pid, String authHandle, Boolean restAccess) throws Exception;

}
