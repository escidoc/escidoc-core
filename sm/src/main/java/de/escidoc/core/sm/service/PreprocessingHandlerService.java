package de.escidoc.core.sm.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.springframework.security.core.context.SecurityContext;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;

/**
 * Service endpoint interface for PreprocessingHandler.
 */
public interface PreprocessingHandlerService extends Remote {

    void preprocess(String aggregationDefinitionId, String xmlData, SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, XmlSchemaValidationException, XmlCorruptedException,
        MissingMethodParameterException, SystemException, RemoteException;

    void preprocess(String aggregationDefinitionId, String xmlData, String authHandle, Boolean restAccess)
        throws AuthenticationException, AuthorizationException, XmlSchemaValidationException, XmlCorruptedException,
        MissingMethodParameterException, SystemException, RemoteException;

}
