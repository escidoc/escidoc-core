package de.escidoc.core.om.ejb.interfaces;

import de.escidoc.core.common.business.fedora.EscidocBinaryContent;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * Remote interface for FedoraRestDeviationHandler.
 */
public interface FedoraRestDeviationHandlerRemote extends EJBObject {

    EscidocBinaryContent getDatastreamDissemination(
        String pid, String dsID, Map parameters, SecurityContext securityContext) throws Exception, RemoteException;

    EscidocBinaryContent getDatastreamDissemination(
        String pid, String dsID, Map parameters, String authHandle, Boolean restAccess) throws Exception,
        RemoteException;

    String export(String pid, Map parameters, SecurityContext securityContext) throws Exception, RemoteException;

    String export(String pid, Map parameters, String authHandle, Boolean restAccess) throws Exception, RemoteException;

    void cache(String pid, String xml, SecurityContext securityContext) throws Exception, RemoteException;

    void cache(String pid, String xml, String authHandle, Boolean restAccess) throws Exception, RemoteException;

    void removeFromCache(String pid, SecurityContext securityContext) throws Exception, RemoteException;

    void removeFromCache(String pid, String authHandle, Boolean restAccess) throws Exception, RemoteException;

    void replaceInCache(String pid, String xml, SecurityContext securityContext) throws Exception, RemoteException;

    void replaceInCache(String pid, String xml, String authHandle, Boolean restAccess) throws Exception,
        RemoteException;

}
