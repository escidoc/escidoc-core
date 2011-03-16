/*
 * Generated by XDoclet - Do not edit!
 */
package de.escidoc.core.oai.ejb.interfaces;

import de.escidoc.core.common.exceptions.application.invalid.InvalidSearchQueryException;
import de.escidoc.core.common.exceptions.application.invalid.InvalidXmlException;
import de.escidoc.core.common.exceptions.application.missing.MissingMethodParameterException;
import de.escidoc.core.common.exceptions.application.notfound.ResourceNotFoundException;
import de.escidoc.core.common.exceptions.application.security.AuthenticationException;
import de.escidoc.core.common.exceptions.application.security.AuthorizationException;
import de.escidoc.core.common.exceptions.application.violated.OptimisticLockingException;
import de.escidoc.core.common.exceptions.application.violated.UniqueConstraintViolationException;
import de.escidoc.core.common.exceptions.system.SystemException;
import org.springframework.security.context.SecurityContext;

import javax.ejb.EJBLocalObject;
import java.util.Map;

/**
 * Local interface for SetDefinitionHandler.
 */
public interface SetDefinitionHandlerLocal extends EJBLocalObject {

    public String create(String setDefinition,
                                   SecurityContext securityContext)
            throws UniqueConstraintViolationException,
            InvalidXmlException,
            MissingMethodParameterException,
            SystemException,
            AuthenticationException,
            AuthorizationException;

    public String create(String setDefinition, String authHandle,
                                   Boolean restAccess)
            throws UniqueConstraintViolationException,
            InvalidXmlException,
            MissingMethodParameterException,
            SystemException,
            AuthenticationException,
            AuthorizationException;

    public String retrieve(String setDefinitionId,
                                     SecurityContext securityContext)
            throws ResourceNotFoundException,
            MissingMethodParameterException,
            SystemException,
            AuthenticationException,
            AuthorizationException;

    public String retrieve(String setDefinitionId, String authHandle,
                                     Boolean restAccess)
            throws ResourceNotFoundException,
            MissingMethodParameterException,
            SystemException,
            AuthenticationException,
            AuthorizationException;

    public String update(String setDefinitionId, String xmlData,
                                   SecurityContext securityContext)
            throws ResourceNotFoundException,
            OptimisticLockingException,
            MissingMethodParameterException,
            SystemException,
            AuthenticationException,
            AuthorizationException;

    public String update(String setDefinitionId, String xmlData,
                                   String authHandle, Boolean restAccess)
            throws ResourceNotFoundException,
            OptimisticLockingException,
            MissingMethodParameterException,
            SystemException,
            AuthenticationException,
            AuthorizationException;

    public void delete(String setDefinitionId,
                       SecurityContext securityContext)
            throws ResourceNotFoundException,
            MissingMethodParameterException,
            SystemException,
            AuthenticationException,
            AuthorizationException;

    public void delete(String setDefinitionId, String authHandle, Boolean restAccess)
            throws ResourceNotFoundException,
            MissingMethodParameterException,
            SystemException,
            AuthenticationException,
            AuthorizationException;

    public String retrieveSetDefinitions(Map filter,
                                                   SecurityContext securityContext)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            InvalidSearchQueryException,
            SystemException;

    public String retrieveSetDefinitions(Map filter, String authHandle,
                                                   Boolean restAccess)
            throws AuthenticationException,
            AuthorizationException,
            MissingMethodParameterException,
            InvalidSearchQueryException,
            SystemException;

}
