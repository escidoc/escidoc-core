package de.escidoc.core.om.service;

import org.esidoc.core.utils.io.EscidocBinaryContent;
import org.springframework.security.core.context.SecurityContext;

import java.rmi.Remote;
import java.util.Map;

/**
 * Service endpoint interface for FedoraRestDeviationHandler.
 */
public interface FedoraRestDeviationHandlerService extends Remote {

    EscidocBinaryContent getDatastreamDissemination(
        String pid, String dsID, Map parameters, SecurityContext securityContext) throws Exception;

    EscidocBinaryContent getDatastreamDissemination(
        String pid, String dsID, Map parameters, String authHandle, Boolean restAccess) throws Exception;

    String export(String pid, Map parameters, SecurityContext securityContext) throws Exception;

    String export(String pid, Map parameters, String authHandle, Boolean restAccess) throws Exception;

    void cache(String pid, String xml, SecurityContext securityContext) throws Exception;

    void cache(String pid, String xml, String authHandle, Boolean restAccess) throws Exception;

    void removeFromCache(String pid, SecurityContext securityContext) throws Exception;

    void removeFromCache(String pid, String authHandle, Boolean restAccess) throws Exception;

    String retrieveUncached(String pid, SecurityContext securityContext) throws Exception;

    String retrieveUncached(String pid, String authHandle, Boolean restAccess) throws Exception;

}
