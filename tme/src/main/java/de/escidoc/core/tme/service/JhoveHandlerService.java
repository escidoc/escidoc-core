package de.escidoc.core.tme.service;

import de.escidoc.core.common.exceptions.application.invalid.TmeException;
import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.core.context.SecurityContext;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Service endpoint interface for JhoveHandler.
 */
public interface JhoveHandlerService extends Remote {

    String extract(String requests, SecurityContext securityContext) throws AuthenticationException,
        AuthorizationException, XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException,
        SystemException, TmeException, RemoteException;

    String extract(String requests, String authHandle, Boolean restAccess) throws AuthenticationException,
        AuthorizationException, XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException,
        SystemException, TmeException, RemoteException;

}
