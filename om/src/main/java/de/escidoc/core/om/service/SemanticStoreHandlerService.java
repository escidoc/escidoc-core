package de.escidoc.core.om.service;

import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreOutputFormatException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidTripleStoreQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingElementValueException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.core.context.SecurityContext;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Service endpoint interface for SemanticStoreHandler.
 */
public interface SemanticStoreHandlerService extends Remote {

    String spo(String taskParam, SecurityContext securityContext) throws SystemException,
        InvalidTripleStoreQueryException, InvalidTripleStoreOutputFormatException, InvalidXmlException,
        MissingElementValueException, AuthenticationException, AuthorizationException, RemoteException;

    String spo(String taskParam, String authHandle, Boolean restAccess) throws SystemException,
        InvalidTripleStoreQueryException, InvalidTripleStoreOutputFormatException, InvalidXmlException,
        MissingElementValueException, AuthenticationException, AuthorizationException, RemoteException;

}
