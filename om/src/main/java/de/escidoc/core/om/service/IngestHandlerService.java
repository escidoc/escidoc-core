/*
 * Generated by XDoclet - Do not edit!
 */
package de.escidoc.core.om.service;

/**
 * Service endpoint interface for IngestHandler.
 */
public interface IngestHandlerService extends java.rmi.Remote {

    public java.lang.String ingest(java.lang.String xmlData,
                                   org.springframework.security.context.SecurityContext securityContext)
            throws de.escidoc.core.common.exceptions.EscidocException, java.rmi.RemoteException;

    public java.lang.String ingest(java.lang.String xmlData, java.lang.String authHandle, java.lang.Boolean restAccess)
            throws de.escidoc.core.common.exceptions.EscidocException, java.rmi.RemoteException;

}
