package de.escidoc.core.aa.service;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Service endpoint interface for PolicyDecisionPoint.
 */
public interface PolicyDecisionPointService extends Remote {

    String evaluate(String requestsXml, SecurityContext securityContext) throws ResourceNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    String evaluate(String requestsXml, String authHandle, Boolean restAccess) throws ResourceNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    void touch(SecurityContext securityContext) throws SystemException, RemoteException;

    void touch(String authHandle, Boolean restAccess) throws SystemException, RemoteException;

}
