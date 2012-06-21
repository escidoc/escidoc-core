package de.escidoc.core.adm.ejb.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBObject;
import java.rmi.RemoteException;

/**
 * Remote interface for AdminHandler.
 */
public interface AdminHandlerRemote extends EJBObject {

    String deleteObjects(String taskParam, SecurityContext securityContext) throws InvalidXmlException,
        SystemException, AuthenticationException, AuthorizationException, RemoteException;

    String deleteObjects(String taskParam, String authHandle, Boolean restAccess) throws InvalidXmlException,
        SystemException, AuthenticationException, AuthorizationException, RemoteException;

    String getPurgeStatus(SecurityContext securityContext) throws SystemException, AuthenticationException,
        AuthorizationException, RemoteException;

    String getPurgeStatus(String authHandle, Boolean restAccess) throws SystemException, AuthenticationException,
        AuthorizationException, RemoteException;

    String getReindexStatus(SecurityContext securityContext) throws SystemException, AuthenticationException,
        AuthorizationException, RemoteException;

    String getReindexStatus(String authHandle, Boolean restAccess) throws SystemException, AuthenticationException,
        AuthorizationException, RemoteException;

    void decreaseReindexStatus(String objectTypeXml, SecurityContext securityContext) throws InvalidXmlException,
        SystemException, AuthenticationException, AuthorizationException, RemoteException;

    void decreaseReindexStatus(String objectTypeXml, String authHandle, Boolean restAccess) throws InvalidXmlException,
        SystemException, AuthenticationException, AuthorizationException, RemoteException;

    String reindex(String clearIndex, String indexNamePrefix, SecurityContext securityContext) throws SystemException,
        InvalidSearchQueryException, AuthenticationException, AuthorizationException, RemoteException;

    String reindex(String clearIndex, String indexNamePrefix, String authHandle, Boolean restAccess)
        throws SystemException, InvalidSearchQueryException, AuthenticationException, AuthorizationException,
        RemoteException;

    String getIndexConfiguration(SecurityContext securityContext) throws SystemException, AuthenticationException,
        AuthorizationException, RemoteException;

    String getIndexConfiguration(String authHandle, Boolean restAccess) throws SystemException,
        AuthenticationException, AuthorizationException, RemoteException;

    String getRepositoryInfo(SecurityContext securityContext) throws SystemException, AuthenticationException,
        AuthorizationException, RemoteException;

    String getRepositoryInfo(String authHandle, Boolean restAccess) throws SystemException, AuthenticationException,
        AuthorizationException, RemoteException;

    String getRepositoryInfo(final String key, SecurityContext securityContext) throws SystemException,
        AuthenticationException, AuthorizationException, RemoteException;

    String getRepositoryInfo(final String key, String authHandle, Boolean restAccess) throws SystemException,
        AuthenticationException, AuthorizationException, RemoteException;

    String loadExamples(String type, SecurityContext securityContext) throws InvalidSearchQueryException,
        SystemException, AuthenticationException, AuthorizationException, RemoteException;

    String loadExamples(String type, String authHandle, Boolean restAccess) throws InvalidSearchQueryException,
        SystemException, AuthenticationException, AuthorizationException, RemoteException;

}
