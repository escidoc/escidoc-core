package de.escidoc.core.aa.ejb.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.XmlCorruptedException;
import de.escidoc.core.common.exceptions.application.invalid.XmlSchemaValidationException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote interface for PolicyDecisionPoint.
 */
public interface PolicyDecisionPointRemote extends EJBObject {

    String evaluate(String requestsXml, SecurityContext securityContext) throws ResourceNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    String evaluate(String requestsXml, String authHandle, Boolean restAccess) throws ResourceNotFoundException,
        XmlCorruptedException, XmlSchemaValidationException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    boolean[] evaluateRequestList(List requests, SecurityContext securityContext) throws ResourceNotFoundException,
        MissingMethodParameterException, AuthenticationException, AuthorizationException, SystemException,
        RemoteException;

    boolean[] evaluateRequestList(List requests, String authHandle, Boolean restAccess)
        throws ResourceNotFoundException, MissingMethodParameterException, AuthenticationException,
        AuthorizationException, SystemException, RemoteException;

    List evaluateRetrieve(String resourceName, List ids, SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        ResourceNotFoundException, SystemException, RemoteException;

    List evaluateRetrieve(String resourceName, List ids, String authHandle, Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        ResourceNotFoundException, SystemException, RemoteException;

    List evaluateMethodForList(
        String resourceName, String methodName, List argumentList, SecurityContext securityContext)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        ResourceNotFoundException, SystemException, RemoteException;

    List evaluateMethodForList(
        String resourceName, String methodName, List argumentList, String authHandle, Boolean restAccess)
        throws AuthenticationException, AuthorizationException, MissingMethodParameterException,
        ResourceNotFoundException, SystemException, RemoteException;

    void touch(SecurityContext securityContext) throws SystemException, RemoteException;

    void touch(String authHandle, Boolean restAccess) throws SystemException, RemoteException;

}
